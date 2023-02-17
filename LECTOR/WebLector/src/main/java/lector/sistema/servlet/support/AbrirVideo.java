/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.sistema.servlet.support;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lector.constantes.Configurador;
import lector.modelo.SiAdjunto;
import lector.servicios.sistema.impl.SiAdjuntoImpl;
import lector.servicios.sistema.impl.SiParametroImpl;
import lector.sistema.bean.backing.Sesion;
import lector.util.UtilLog4j;

/**
 *
 */
public class AbrirVideo {//extends HttpServlet {

    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @Inject
    SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    Sesion sesion;

    //
    //
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
        try {
            // utilizar un managedbean
            System.out.println("Dentro del servlet Abrir video");
            sesion = (lector.sistema.bean.backing.Sesion) request.getSession().getAttribute("sesion");

            if (sesion.getUsuario() != null) {
                SiAdjunto adjunto = servicioSiAdjuntoImpl.find(Integer.parseInt(request.getParameter("a")));
                String path = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
                if (adjunto != null) {
                    try (InputStream in = new FileInputStream(path + adjunto.getUrl())) {
                        byte[] data = new byte[in.available()];
                        //
                        response.setContentType(adjunto.getTipoArchivo());
                        // la siguiente linea es para mostrar un dialogo con la opcion de abrir guardar o cancelar
                        response.setHeader("Content-Disposition", "attachment;filename=\"" + adjunto.getNombre() + "\";");
                        response.setContentLength(data.length);
                        try (ServletOutputStream servletoutputstream = response.getOutputStream()) {
                            servletoutputstream.write(data);
                            servletoutputstream.flush();
                        }
                    }
                }
            } // si usuarioConectado es null el usuario no a iniciado sesion
            else {
                response.sendRedirect(Configurador.urlSistema() + "Sia");
            }
        } catch (IOException e) {
            UtilLog4j.log.error(e);
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
    //@Override
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
   // @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
  //  @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
