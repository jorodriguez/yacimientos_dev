/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.servicios.catalogos.impl.CompaniaImpl;

/**
 *
 * @author MLUIS
 */
@WebServlet(name = "MostraImagen", urlPatterns = {"/MostraImagen"})
public class MostraImagen extends HttpServlet implements Serializable{

    @Inject
    private CompaniaImpl companiaImpl;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String cadena = request.getParameter("ZWZ2W");
        byte[] logoc = companiaImpl.traeLogo(cadena);
        //System.out.println("Compania: " + c.getNombre());
        if (logoc != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    output.write(logoc, 0, logoc.length);
                    response.setContentType("image/jpeg");
                    response.setHeader("content-disposition", "inline;");
                    response.setContentLength(output.size());
                    //Escribir el archivo en el response
                    OutputStream out = response.getOutputStream();
                    output.writeTo(out);
                    out.flush();
                    out.close();
                } finally {
                    output.close();
                }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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
