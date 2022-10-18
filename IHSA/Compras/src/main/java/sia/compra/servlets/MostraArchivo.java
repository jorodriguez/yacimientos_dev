/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.excepciones.SIAException;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.sistema.impl.SiAdjuntoImpl;

/**
 *
 * @author MLUIS
 */
@WebServlet(name = "MostraArchivo", urlPatterns = {"/MostraArchivo"})
public class MostraArchivo extends HttpServlet implements Serializable {

    @Inject
    private SiAdjuntoImpl adjuntoImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

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
        response.setContentType("text/html;charset=UTF-8");
        //out.println("Mostrar archivo: ");
        String SAId = request.getParameter("ZWZ2W");
        String SAUUID = request.getParameter("ZWZ3W");
        if (!SAId.isEmpty()) {
            AdjuntoVO ets = adjuntoImpl.buscarArchivo(Integer.parseInt(SAId), SAUUID);
            //System.out.println("Compania: " + c.getNombre());
            if (ets != null) {
                try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                    DocumentoAnexo documento;
                    try {
                        documento = almacenDocumentos.cargarDocumento(ets.getUrl());
                        if (documento != null) {
                            output.write(documento.getContenido(), 0, documento.getContenido().length);
                            response.setContentType("application/pdf");
                            response.setHeader("content-disposition", "inline;");
                            response.setContentLength(output.size());
                            try (OutputStream out = response.getOutputStream()) {
                                output.writeTo(out);
                                out.flush();
                            }
                        }
                    } catch (SIAException ex) {
                        Logger.getLogger(MostraArchivo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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
