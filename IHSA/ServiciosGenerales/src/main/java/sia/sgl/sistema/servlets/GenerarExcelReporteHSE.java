/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.servicios.sgl.combustible.impl.SgTarjetaOperacionImpl;

/**
 *
 * @author ihsa
 */
@WebServlet(name = "generarExcelReporteHSE", urlPatterns = {"/generarExcelReporteHSE"})
public class GenerarExcelReporteHSE extends HttpServlet {

    @Inject
    private SgTarjetaOperacionImpl operacionLocal;

    static Logger log = Logger.getLogger(sia.sgl.sistema.servlets.GenerarExcelReporteHSE.class.getName());

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
            throws ServletException, IOException, ParseException {
        
        File fileTempExcel = null;
        String fechaI = request.getParameter("DFIWZ");
        int mes = Integer.parseInt(fechaI)-1;

        fileTempExcel = operacionLocal.crearArchivo(fileTempExcel,mes);

        if (fileTempExcel != null && fileTempExcel.exists()) {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.addHeader("Content-Disposition", "attachment;filename=ReporteMantenimiento.xlsx");
            try (BufferedOutputStream bfout = new BufferedOutputStream(response.getOutputStream());
                    BufferedInputStream infile = new BufferedInputStream(new FileInputStream(fileTempExcel.getPath()))) {
                byte[] tmp = new byte[8192];
                int c;
                while ((c = infile.read(tmp)) > 0) {
                    bfout.write(tmp, 0, c);
                }
                bfout.flush();
            } catch (Exception e) {
                log.log(Level.SEVERE, null, e);
            }

        } else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(
                    new StringBuilder().append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">").append("La solicitud que est&#225;s realizando es incorrecta. ") //                                .append("Los identificadores del archivo de la petici&#243;n no coinciden con los identificadores en la base de datos.")
                            .append("</td></tr></table></body></html>").toString());
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
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(GenerarExcelReporteHSE.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(GenerarExcelReporteHSE.class.getName()).log(Level.SEVERE, null, ex);
        }
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
