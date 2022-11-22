/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Configurador;
import sia.modelo.Orden;
import sia.modelo.SiAdjunto;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@WebServlet(name = "AbrirPDF", urlPatterns = {"/AbrirPDF"})
public class AbrirPDF extends HttpServlet {

    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private OrdenImpl ordenServicioRemoto;
    static Logger log = Logger.getLogger(sia.compra.servlets.AbrirPDF.class.getName());

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
        StringBuilder respuesta = null;
        try {
            // utilizar un managedbean
            UsuarioBean sesion = (UsuarioBean) request.getSession().getAttribute("usuarioBean");

            if (sesion != null) {
                // si el managed bean usuario es diferente de null podemos verificar si ya inicio sesion
                if (sesion.getUsuarioConectado() != null) {
                    // si inicio sesion buscar el convenio q viene en el parametro del servlet
                    String ordenID = request.getParameter("ZWZ1W");
                    Orden orden = ordenServicioRemoto.find(Integer.parseInt(ordenID));
                    if (orden != null && orden.getId() > 0 && orden.getUuid() != null && !orden.getUuid().isEmpty()) {
                        SiAdjunto ets = servicioSiAdjuntoImpl.find(orden.getId(), orden.getUuid(), true);
                        String path = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
                        if (ets != null) {
                            
                            try(InputStream in = new FileInputStream(path + ets.getUrl());
                                    ServletOutputStream servletoutputstream = response.getOutputStream();) {
                                
                                byte[] data = new byte[in.available()];
                                in.read(data);
                                response.setContentType(ets.getTipoArchivo());
                                
                                // la siguiente linea es para mostrar un dialogo con la opcion de abrir guardar o cancelar
                                // response.setHeader("Content-Disposition", "attachment;filename=\"convenio.pdf\";");
                                response.setContentLength(data.length);
                                
                                servletoutputstream.write(data);
                                servletoutputstream.flush();
                                
                                //
                                in.close();
                            } catch (IOException e) {
                                UtilLog4j.log.error(e);
                            }
                        } else {
                            respuesta = new StringBuilder().append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">").append("La solicitud que est&#225;s realizando es incorrecta. ")
                                    //.append("Los identificadores del archivo de la petici&#243;n no coinciden con los identificadores en la base de datos.")
                                    .append("</td></tr></table></body></html>");
                        }
                    } else {
                        respuesta = new StringBuilder().append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">")
                                .append("La solicitud que est&#225;s realizando es incorrecta. ")
                                .append("El identificador de la orden de compra o servicio de la petici&#243;n es incorrecto.").append("</td></tr></table></body></html>");
                    }
                } // si usuarioConectado es null el usuario no a iniciado sesion
                else {
                    response.sendRedirect(Configurador.urlSia() + "Sia");
                }
            } // si el managed bean usuario es null ni si quiera a entrado a la aplicacion
            else {
                response.sendRedirect(Configurador.urlSia() + "Sia");
            }
        } catch (Exception e) {
            respuesta = new StringBuilder().append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">")
                    .append("Error : La solicitud que est&#225;s realizando es incorrecta. ")
                    .append("El parametro enviado como identificador de la orden no se pudo interpretar.").append("</td></tr></table></body></html>");
            log.log(Level.SEVERE, null, e);
        }

        if (respuesta != null) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(respuesta.toString());
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
