/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Constantes;
import sia.modelo.SgEstatusAprobacion;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@WebServlet(name = "AUTOWJUSTI", urlPatterns = {"/AUTWJUST"})
public class AUTOWJUSTI extends HttpServlet {

    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionImpl;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeImpl;

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

        try {
            String justidicacion = request.getParameter("mensaje");
            SgEstatusAprobacion sgEstatusAprobacion = sgEstatusAprobacionImpl.find(Integer.parseInt(request.getParameter("mg4mvrg235es")));
            EstatusAprobacionSolicitudVO ea = sgEstatusAprobacionImpl.buscarEstatusAprobacionPorIdSolicitudIdEstatus(sgEstatusAprobacion.getSgSolicitudViaje().getId(), Constantes.ESTATUS_JUSTIFICAR);
            if (sgEstatusAprobacion != null) {
                SolicitudViajeVO solVo = sgSolicitudViajeImpl.buscarPorId(sgEstatusAprobacion.getSgSolicitudViaje().getId(), Constantes.NO_ELIMINADO,Constantes.CERO);
                Usuario usuario = usuarioImpl.find(request.getParameter("v3g9u93u"));
                if (sgEstatusAprobacion.getUsuario().getId().equals(usuario.getId()) && solVo.getIdEstatus() == sgEstatusAprobacion.getEstatus().getId()) {
                     boolean v = false;
                    if(ea == null){
                         v = sgEstatusAprobacionImpl.aprobarJustificandoSolicitud(sgEstatusAprobacion.getId(), solVo.getIdSolicitud(), justidicacion, usuario.getId());
                    }
                    
                    if (v) {
                        printMessage(("Solicitud de Viaje " + solVo.getCodigo() 
                                + " ha sido Aprobada satisfactoriamente y debera ser Autorizada por el departamento de Gestion de Riesgos"),
                                request, response, "blue");
                    } else {
                        printMessage("Ocurrió un error. Por favor contacta al equipo del SIA para averiguar que pasó al correo soportesia@ihsa.mx", request, response, "red");
                    }
                } else {
                    printMessage("La solicitud ya fue aprobada ", request, response,"black");
                }
            } else {
                printMessage(("No se encontró la Solicitud de Viaje con id =" + Integer.parseInt(request.getParameter("idSgSolicitudViaje"))),
                        request, response, "#DBA901");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    private void printMessage(String message, HttpServletRequest request, HttpServletResponse response, String color) {
        try {
            PrintWriter output = response.getWriter();
            output.println("<html>");
            output.println("<head>");
            output.println("<title>Sistema Integral de Administración</title>");
            output.println("</head>");
            output.println("<body >");
            output.println("<h1 style=\"color: "+color+"; \">" + message + "</h1>");
            output.println("</body>");
            output.println("</html>");
        } catch (IOException ioe) {
            UtilLog4j.log.fatal(this, ioe.getMessage());
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
