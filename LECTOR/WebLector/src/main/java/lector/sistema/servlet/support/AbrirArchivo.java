/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.sistema.servlet.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lector.archivador.AlmacenDocumentos;
import lector.archivador.DocumentoAnexo;
import lector.archivador.ProveedorAlmacenDocumentos;
import lector.constantes.Configurador;
import lector.dominio.vo.AdjuntoVO;
import lector.excepciones.LectorException;
import lector.servicios.sistema.impl.SiAdjuntoImpl;
import lector.servicios.sistema.impl.SiParametroImpl;
import lector.sistema.bean.backing.Sesion;
import lector.util.UtilLog4j;

/**
 *
 * @author Héctor
 */
@WebServlet(name = "AbrirArchivo", urlPatterns = {"/AbrirArchivo"})
public class AbrirArchivo extends HttpServlet {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    Sesion sesion;

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

        InputStream inFile = null;
        ServletOutputStream servletoutputstream = null;

        String fullFilePath = null;

        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
        boolean error = false;

        try {
            //  Sesion sesion = (Sesion) request.getSession().getAttribute("sesion");
            if (sesion == null) {
                response.sendRedirect(Configurador.urlSistema() + "WebLector");
            } else {
                // si el managed bean usuario es diferente de null podemos verificar si ya inicio sesion
                if (sesion.getUsuario() == null) {
                    response.sendRedirect(Configurador.urlSistema() + "WebLector");
                } else {
                    // si inicio sesion buscar el convenio q viene en el parametro del servlet
                    String SAId = request.getParameter("ZWZ2W");
                    String SAUUID = request.getParameter("ZWZ3W");
                    AdjuntoVO ets = servicioSiAdjuntoImpl.buscarArchivo(Integer.parseInt(SAId), SAUUID);
                    String path = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
                    if (ets == null) {
                        response.setContentType("text/html");
                        PrintWriter out = response.getWriter();
                        out.println(
                                new StringBuilder()
                                        .append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">")
                                        .append("La solicitud que est&#225;s realizando es incorrecta. ") //.append("Los identificadores del archivo de la petici&#243;n no coinciden con los identificadores en la base de datos.")
                                        .append("</td></tr></table></body></html>").toString());
                    } else {
                        /*fullFilePath = path + ets.getUrl();

			 inFile = Files.newInputStream(Paths.get(fullFilePath));

			 byte[] data = new byte[inFile.available()];
			 inFile.read(data);

			 if(ets.getNombre().endsWith(".msg")) {
			 response.setContentType("application/vnd.ms-outlook"+" ;charset=utf-8");
			 } else {
			 response.setContentType(ets.getTipoArchivo());
			 }

			 response.setHeader("Content-Disposition","attachment;filename=\"" + ets.getNombre()+"\"");

			 response.setContentLength(data.length);*/

                        LOGGER.info(" URL : " + almacenDocumentos.getRaizAlmacen());

                        DocumentoAnexo documento = almacenDocumentos.cargarDocumento(ets.getUrl());

                        response.setContentType(documento.getTipoMime());
                        response.setHeader("Content-Disposition", "attachment;filename=\"" + ets.getNombre() + "\"");
                        response.setContentLength(documento.getContenido().length);

                        servletoutputstream = response.getOutputStream();
                        servletoutputstream.write(documento.getContenido());
                        servletoutputstream.flush();
                    }
                } // si usuarioConectado es null el usuario no a iniciado sesion
            } // si el managed bean usuario es null ni si quiera a entrado a la aplicacion
        } catch (LectorException e) {
            LOGGER.error("File : " + fullFilePath, e);
            error = true;
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
            if (inFile != null) {
                inFile.close();
            }
            if (servletoutputstream != null) {
                servletoutputstream.close();
            }
        }

        if (error) {
            writeMessage(response, "No fue posible recuperar el archivo solicitado.");
        }
    }

    public void writeMessage(HttpServletResponse response, String message) {
        try {
            response.setContentType("text/html");
            response.getWriter().println(message);
        } catch (IOException ex) {
            LOGGER.fatal(Level.SEVERE, null, ex);
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
