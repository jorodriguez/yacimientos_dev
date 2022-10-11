/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import com.newrelic.api.agent.Trace;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Configurador;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@WebServlet(name = "GenerarZip", urlPatterns = {"/GenerarZip"})
public class GenerarZip extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private SiFacturaAdjuntoImpl siFacturaAdjuntoImpl;
    @Inject
    UsuarioBean sesion;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Trace(dispatcher = true)
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullFilePath = null;
        File fileZip = null;

        boolean error = false;

        try {
            // utilizar un managedbean

            if (sesion == null) {
                response.sendRedirect(Configurador.urlSia() + "Sia");
            } else {
                // si el managed bean usuario es diferente de null podemos verificar si ya inicio sesion
                if (sesion.getUsuarioConectado() == null) {
                    response.sendRedirect(Configurador.urlSia() + "Sia");
                } else {
                    // si inicio sesion buscar el convenio q viene en el parametro del servlet
                    int facId = Integer.parseInt(request.getParameter("ZWZ2W"));
                    String facIds = String.valueOf(request.getParameter("ZWZ3W"));

                    if (facId > 0) {
                        fileZip = siFacturaAdjuntoImpl.crearZipFile(facId, siFacturaAdjuntoImpl.traerArchivosFactura(facId, UUID.randomUUID().toString()));
                    } else if (facIds != null && !facIds.isEmpty()) {
                        fileZip = siFacturaAdjuntoImpl.crearZipFile(0, siFacturaAdjuntoImpl.traerArchivosFacturaByIds(facIds, UUID.randomUUID().toString()));
                    }

                    if (fileZip != null && fileZip.exists()) {

                        try ( InputStream in = new FileInputStream(fileZip);  ServletOutputStream servletoutputstream = response.getOutputStream();) {
                            byte[] data = new byte[in.available()];
                            in.read(data);
                            response.setContentType("application/zip");
                            response.setContentLength(data.length);
                            servletoutputstream.write(data);
                            servletoutputstream.flush();
                        } catch (IOException e) {
                            UtilLog4j.log.error(e);
                        }
                    } else {
                        response.setContentType("text/html");
                        PrintWriter out = response.getWriter();
                        out.println(
                                new StringBuilder().append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">").append("La solicitud que est&#225;s realizando es incorrecta. ") //                                .append("Los identificadores del archivo de la petici&#243;n no coinciden con los identificadores en la base de datos.")
                                        .append("</td></tr></table></body></html>").toString());
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("File : " + fullFilePath, e);
            error = true;
        } catch (NumberFormatException e) {
            LOGGER.error("File : " + fullFilePath, e);
            error = true;
        } catch (Exception e) {
            LOGGER.error("File : " + fullFilePath, e);
            error = true;
        } finally {
            if (fileZip != null && fileZip.exists()) {
                fileZip.delete();
            }
        }

        if (error) {
            writeMessage(response, "No fue posible recuperar el archivo solicitado.");
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

    public void writeMessage(HttpServletResponse response, String message) {
        try {
            response.setContentType("text/html");
            response.getWriter().println(message);
        } catch (IOException ex) {
            LOGGER.fatal(Level.SEVERE, null, ex);
        }
    }
}
