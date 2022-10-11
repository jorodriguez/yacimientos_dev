/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.modelo.Orden;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiParametroImpl;

/**
 *
 * @author mluis
 */
@WebServlet(name = "generaExcelOCSAI", urlPatterns = {"/ocsai2014"})
public class GeneraExcelOCSAI extends HttpServlet {

    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private OrdenImpl ordenServicioRemoto;
    static Logger log = Logger.getLogger(sia.compra.servlets.GenerarExcel.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        File fileTempExcel = null;
        try {
            String ord = request.getParameter("CONSECUTIVO");
            Orden orden = ordenServicioRemoto.buscarPorConsecutivo(ord);
            if (orden != null) {
                String REPOSITORYPATH = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
                String PLANTILLAPATH = "Plantillas/ExcelNAV/";
                fileTempExcel = File.createTempFile("excelTemporal", ".xlsx", new File(new StringBuilder().append(REPOSITORYPATH).append(PLANTILLAPATH).toString()));

                fileTempExcel = ordenServicioRemoto.generarExcelOCSAI(orden, fileTempExcel);

                if (fileTempExcel != null && fileTempExcel.exists()) {
                    try (InputStream in = new FileInputStream(fileTempExcel)) {
                        //in = new FileInputStream(fileTempExcel);
                        byte[] data = new byte[in.available()];
                        in.read(data);
                        response.setContentType("application/vnd.ms-excel");
                        response.setContentLength(data.length);
                        try (ServletOutputStream servletoutputstream = response.getOutputStream()) {
                            servletoutputstream.write(data);
                            servletoutputstream.flush();
                        }
                        //
                    }
                } else {
                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println(
                            new StringBuilder().append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">").append("La solicitud que est&#225;s realizando es incorrecta. ") //                                .append("Los identificadores del archivo de la petici&#243;n no coinciden con los identificadores en la base de datos.")
                                    .append("</td></tr></table></body></html>").toString());
                }
            } else {
                response.setContentType("text/html");
                StringBuilder sb = new StringBuilder();
                PrintWriter out = response.getWriter();
                sb.append("<html><body><table><tr><td style=\"width:95%; ");
                sb.append(" text-align:center; padding:3px; background-color:#A8CEF0; ");
                sb.append(" color:#004181; font-size:15px\">");
                sb.append("No se encontró la OC/S. Agregue por favor códigos válidos. No . . . . ");
                sb.append("</td></tr></table></body></html>");
                out.println(sb.toString());
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
            if (fileTempExcel != null && fileTempExcel.exists()) {
                fileTempExcel.delete();
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
