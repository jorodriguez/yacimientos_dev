/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.utils;

import java.io.IOException;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Configurador;
import sia.excepciones.SIAException;
import sia.ihsa.admin.Sesion;
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

    @EJB
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @EJB
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletOutputStream servletoutputstream = null;
        //
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean error = false;

        try {
            // utilizar un managedbean
            final Sesion sesion = (Sesion) request.getSession().getAttribute("sesion");

            if (sesion == null) {
                response.sendRedirect(Configurador.urlSia() + "Proveedor");
            } else {
                // si el managed bean usuario es diferente de null podemos verificar si ya inicio sesion
                if (sesion.getProveedorVo() == null) {
                    response.sendRedirect(Configurador.urlSia() + "Proveedor");
                } else {
                    // si inicio sesion buscar el convenio q viene en el parametro del servlet
                    String saId = request.getParameter("ZWZ2W");
                    String saUuid = request.getParameter("ZWZ3W");
                    AdjuntoVO ets = servicioSiAdjuntoImpl.buscarArchivo(Integer.parseInt(saId), saUuid);

                    //

                    if (ets == null) {
                        writeMessage(
                                response,
                                "<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">"
                                + "La solicitud que est&#225;s realizando es incorrecta. "
                                + "</td></tr></table></body></html>"
                        );
                    } else {

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

        } catch (IOException | NumberFormatException | SIAException e) {
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
