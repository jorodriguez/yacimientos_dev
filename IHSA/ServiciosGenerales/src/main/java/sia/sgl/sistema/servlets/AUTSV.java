/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.servlets;

import com.newrelic.api.agent.Trace;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.inject.Inject;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.viaje.impl.SgItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@WebServlet(name = "AUTSV", urlPatterns = {"/AUTSV"})
public class AUTSV extends HttpServlet {

    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeImpl;
    @Inject
    private SgItinerarioImpl sgItinerarioImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UtilLog4j.log.fatal(this, "AUTSV.processRequest()");

        try {
            SolicitudViajeVO solicitudViajeVO = sgSolicitudViajeImpl.buscarPorId(Integer.parseInt(request.getParameter("mg4mvrg235m")), Constantes.NO_ELIMINADO, Constantes.CERO);
            EstatusAprobacionSolicitudVO sgEstatusAprobacion = sgEstatusAprobacionImpl.buscarEstatusAprobacionPorIdSolicitudIdEstatus(Integer.parseInt(request.getParameter("mg4mvrg235m")), Integer.parseInt(request.getParameter("e3g9a9m")));
            Usuario usuario = usuarioImpl.find(request.getParameter("v3g9m93v"));
            int campo = Integer.parseInt(request.getParameter("4ca3p0"));
            //SgSolicitudViaje solViaje = sgSolicitudViajeImpl.find(sgEstatusAprobacion.getIdSolicitud());
            String action = "";
            String motivo = "";
            switch (solicitudViajeVO.getIdEstatus()) {
                case 415: {
                    action = "Revisada";
                    motivo = "por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx.";
                }
                break;
                case 420: {
                    action = "Aprobada";
                    motivo = "por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx.";
                }
                break;
                case 435: {
                    action = "Autorizada";
                    motivo = "No se puede aprobar la solicitud debido a que ya fue previamente aprobada";
                }
                break;
                case 438: {
                    action = "Aprobada por centops";
                    motivo = "La solicitud no fue aprobada a tiempo por lo que fue transferida a CentOps.";
                }
                break;
                case 450: {
                    action = "para hacer Viaje";
                    motivo = "La solicitud ya no puede ser aprobada porque ya fue canalizada con el analista para realizar el viaje.";
                    break;
                }
            }
            boolean aps1 = false;

            if (sgEstatusAprobacion != null) {
                if (sgEstatusAprobacion.getIdUsuario().equals(usuario.getId()) && solicitudViajeVO.getIdEstatus() == sgEstatusAprobacion.getIdEstatus()) {
                    UtilLog4j.log.fatal(this, "idSgEstatusAprobacion encontrado: " + sgEstatusAprobacion.getId());
                    aps1 = vistoBuenoAprobarOrAutorizarSolicitudViaje(solicitudViajeVO.getIdSolicitud(), solicitudViajeVO.getIdEstatus(),
                            solicitudViajeVO.getFechaSalida(), solicitudViajeVO.getHoraSalida(),
                            solicitudViajeVO.getIdSgTipoSolicitudViaje(),
                            solicitudViajeVO.getCodigo(),
                            sgEstatusAprobacion.getId(), usuario.getId(), request, response, campo);
                    if (aps1) {
                        aprobar(sgEstatusAprobacion.getId(), usuario.getId(),
                                solicitudViajeVO.getCodigo(), action, request, response);
                    }

                } else {
                    printMessage(motivo, request, response, "#DBA901");
                }
            } else {
                printMessage(("No se encontró la Solicitud de Viaje con id =" + Integer.parseInt(request.getParameter("idSgSolicitudViaje"))), request, response, "red");
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
    }

    @Trace(dispatcher = true)
    private boolean vistoBuenoAprobarOrAutorizarSolicitudViaje(int idSolicitud, int idStatus, Date fechaSalida, Date horaSalida,
            int tipoSolicitud, String codigo, int idEstatusAprobacion, String idUsuario,
            HttpServletRequest request, HttpServletResponse response, int campo) {
        UtilLog4j.log.info(this, "AUTSV.vistoBuenoAprobarOrAutorizarSolicitudViaje() " + idSolicitud);
//        printMessage("AUTSV.vistoBuenoAprobarOrAutorizarSolicitudViaje()", request, response);
        boolean pass = false;
        UsuarioRolVo urv = siUsuarioRolImpl.traerRolPrincipal(idUsuario, Constantes.MODULO_SGYL, campo);
        boolean v = false;
        String action = "";
        switch (idStatus) {
            case 415: {
                action = "Revisada";
            }
            break;
            case 420: {
                action = "Aprobada";
            }
            break;
            case 435: {
                action = "Autorizada";
            }
            case 438: {
                action = "Aprobada por centops";
            }
            break;
        }

        if (siManejoFechaLocal.validaFechaSalidaViaje(fechaSalida, horaSalida)) {
            if (tipoSolicitud == Constantes.SOLICITUDES_TERRESTRE) {
                //Valida que no sea despues de las 5 con fecha de salida mañana
                if (siManejoFechaLocal.dateIsTomorrow(fechaSalida)
                        && (siManejoFechaLocal.validaHoraMaximaAprobacion(urv.getIdRol(), Constantes.HORA_MAXIMA_APROBACION))) {
                    pass = false;
                    aprobarDesdeCorreo("No es posible aprobar solicitudes de viajes después de las 5:00 pm sin justificar.", idUsuario, idEstatusAprobacion, request, response);

                } else if (siManejoFechaLocal.dayIsToday(fechaSalida)) {
                    //valida qsi se desea arobar el mismo día
                    pass = false;
                    aprobarDesdeCorreo("No es posible aprobar solicitudes de viajes el mismo día sin justificar.", idUsuario, idEstatusAprobacion, request, response);
                } else if (siManejoFechaLocal.salidaProximoLunes(new Date(), fechaSalida, idStatus)) {

                    aprobarDesdeCorreo("Debido a que la solicitud de viaje es para el próximo lunes, y en base a las políticas de viaje "
                            + "debe de ser justificada, porque la hora máxima para aprobar es el viernes a las 12:00 hrs.", idUsuario, idEstatusAprobacion, request, response);
                } else {
                    pass = true;
                    //  aprobar(idEstatusAprobacion, idUsuario, codigo, action, request, response);
                }
            } else { // Solicitudes aereas
                if (idStatus != Constantes.ESTATUS_APROBAR) {
                    pass = true;
                    //  aprobar(idEstatusAprobacion, idUsuario, codigo, action, request, response);
                } else {
                    ItinerarioCompletoVo icv = sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(idSolicitud, true, true, "id");
                    if (icv.getEscalas().size() > 0) {
                        pass = true;
                        //  aprobar(idEstatusAprobacion, idUsuario, codigo, action, request, response);
                    } else {
                        pass = false;
                        printMessage("No es posible aprobar una solicitud de viaje aérea sin itinerario", request, response, "red");
                    }
                }
            }
        } else {
            pass = false;
            printMessage("Imposible aprobar una Solicitud de Viaje para una fecha y hora de salida pasada", request, response, "red");

        }
        return pass;
    }

    @Trace(dispatcher = true)
    private void aprobar(int idEstatusAprobacion, String idUsuario, String codigo, String accion, HttpServletRequest request, HttpServletResponse response) {
        boolean v = sgEstatusAprobacionImpl.aprobarSolicitud(idEstatusAprobacion, idUsuario);
        String color = "";
        if (v) {
            color = "blue";
            String msj = "La solicitud de Viaje " + codigo + " ha sido " + accion + " satisfactoriamente";
            printMessage(msj, request, response, color);
        } else {
            color = "red";
            printMessage("Ocurrió un error. Por favor contacta al equipo del SIA para averiguar que pasó al correo soportesia@ihsa.mx", request, response, color);
            //   UtilLog4j.log.fatal(this, e);
        }
    }

    private void printMessage(String message, HttpServletRequest request, HttpServletResponse response, String color) {
        try {
            PrintWriter output = response.getWriter();
            output.println("<html>");
            output.println("<head>");
            output.print(" <meta name=\"viewport\" content=\"width=device-width\"/>");
            output.println("<title>Sistema Integral de Administración</title>");
            //
            output.print(styleCSS());
            //
            output.println("</head>");
            output.println("<center >");
            output.println("<body >");
            output.println("<section>");
            output.println("<h1 style=\"color: " + color + ";\">" + message + "</h1>");
            output.println("</section>");
            output.println("</body>");
            output.println("</center >");
            output.println("</html>");

        } catch (IOException ioe) {
            UtilLog4j.log.fatal(this, ioe.getMessage());
        }
    }

    private void aprobarDesdeCorreo(String mensaje, String idUsuario, int idEstatusAprobacion, HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter output = response.getWriter();
            output.println("<html>");
            output.println("<head>");
            output.println("<title>Sistema Integral de Administración</title>");
            output.print(" <meta name=\"viewport\" content=\"width=device-width\"/>");
            //
            output.println("<script type=\"text/javascript\">");
            output.println("function validaCaja(){");
            output.println("var e =0;var mensaje = document.getElementById(\"msj\").value;");
            output.println("if(mensaje.trim() == \"\" ){ ");
            output.println(" alert('Es necesario agregar la justificación');");
            output.println(" e++;} else {document.getElementById(\"pr\").style.display = 'block'; document.getElementById(\"btnA\").style.display = 'none';}");
            output.println("if(e==0){ return true;  }else{     return false;}");
            output.println("}");
            output.println("</script>");
            //
            output.print(styleCSS());
            //

            output.println("</head>");
            output.println("<body >");
            output.println("</br>");
            output.println("<section>");
            output.println("<h1 style=\"color: red; font-size: 13px;\">" + mensaje + "</h1>");
            output.println("</br>");
            output.println("<form action=\"" + Configurador.urlSia() + "ServiciosGenerales/AUTWJUST\" method=\"get\" >");
            output.println("<input type=\"hidden\" name=\"mg4mvrg235es\" value=\"" + idEstatusAprobacion + "\" ></input>");
            output.println("<input type=\"hidden\" name=\"v3g9u93u\" value=\"" + idUsuario + "\" ></input>");
            output.println("<table class = \"fila1\" width=\"90%;\"><tr><td>Por favor agregue la justificación: </td><tr>");
            output.println("<tr>  <td> <textarea id=\"msj\" name=\"mensaje\" style = \"width:100%;height: auto;\"  rows=\"5\"> </textarea> </td></tr>");
            output.println("<tr> <td> <div id=\"pr\" style=\"display: none;\"><label> procesando por favor espere </label></div> </td> </tr>");
            output.println("<tr>  <td >  <input type=\"submit\" style=\"margin:10px;background-color:#0895d6;border:1px solid #999;color:#fff;cursor:pointer;font-size:16px;font-weight:bold;text-decoration: none;\" "
                    + "onclick=\"return validaCaja();\" value=\"  Aprobar  \" id=\"btnA\"> </input> </td></tr>");
            output.println("</table></form>");
            output.println("</section>");
            output.println("</body></html>");
        } catch (IOException ioe) {
            UtilLog4j.log.fatal(this, ioe.getMessage());
        }
    }

    private String styleCSS() {
        StringBuilder css = new StringBuilder();
        css.append("<script type=\"text/css\">");
        css.append("*{margin:4%;} article{ float:left;width:50%;}");
        css.append("body{   background:#C3E5F9;    color:white;    font-size:16px;    font-family:Arial;    text-shadow:1px 1px 0 black; }");
        css.append(" section{   background:#12A89D;     margin:1%;    overflow:hidden;    padding:1%;    text-align:center;");
        css.append(" width:1000px;} .fila1{background:#FFFF; } .fila2{    background:#FFFF000; } ");
        css.append("@media screen and (max-width:1000px){    section{       width:90%;    } }");
        css.append(" @media screen and (max-width:1000px){    article{       width:90%;    } }");
        css.append("</script>");

        return css.toString();
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    protected FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {

            FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);

            // Set using our inner class
            AUTSV.InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

            // set a new viewRoot, otherwise context.getViewRoot returns null
            UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
            facesContext.setViewRoot(view);
        }
        return facesContext;
    }

    protected Application getApplication(FacesContext facesContext) {
        return facesContext.getApplication();
    }

    protected Object getManagedBean(String beanName, FacesContext facesContext) {
        return getApplication(facesContext).getVariableResolver().resolveVariable(facesContext, beanName);
    }

// You need an inner class to be able to call FacesContext.setCurrentInstance
    // since it's a protected method
    private abstract static class InnerFacesContext extends FacesContext {

        protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
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
