/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import com.newrelic.api.agent.Trace;
import java.io.IOException;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Configurador;
import sia.excepciones.SIAException;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor
 */
@WebServlet(name = "AbrirArchivo", urlPatterns = {"/AbrirArchivo"})
public class AbrirArchivo extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
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

        ServletOutputStream servletoutputstream = null;
        String fullFilePath = null;
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

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
                    String SAId = request.getParameter("ZWZ2W");
                    String SAUUID = request.getParameter("ZWZ3W");
                    AdjuntoVO ets = servicioSiAdjuntoImpl.buscarArchivo(Integer.parseInt(SAId), SAUUID);

                    fullFilePath = SAId + " - " + SAUUID;

                    if (ets == null) {
                        writeMessage(
                                response,
                                "<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">"
                                + "La solicitud que est&#225;s realizando es incorrecta. "
                                + "</td></tr></table></body></html>"
                        );
                    } else {

                        fullFilePath = ets.getNombreUUID();

                        DocumentoAnexo documento = almacenDocumentos.cargarDocumento(ets.getUrl());

                        response.setContentType(documento.getTipoMime());
                        response.setHeader("Content-Disposition", "attachment;filename=\"" + ets.getNombreUUID() + "\"");
                        response.setContentLength(documento.getContenido().length);

                        servletoutputstream = response.getOutputStream();
                        servletoutputstream.write(documento.getContenido());
                        servletoutputstream.flush();
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("File : " + fullFilePath, e);
            error = true;
        } catch (NumberFormatException e) {
            LOGGER.error("File : " + fullFilePath, e);
            error = true;
        } catch (SIAException e) {
            LOGGER.error("File : " + fullFilePath, e);
            error = true;
        } catch (Exception e) {
            LOGGER.error("File : " + fullFilePath, e);
            error = true;
        } finally {
            if (servletoutputstream != null) {
                servletoutputstream.close();
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
