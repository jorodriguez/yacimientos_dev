/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.servlets;

import com.newrelic.api.agent.Trace;
import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Constantes;
import sia.modelo.Usuario;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@WebServlet(name = "AUTSE", urlPatterns = {"/AUTSE"})
public class AUTSE extends HttpServlet {

    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurshh
     */
    @Trace(dispatcher = true)
    public void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UtilLog4j.log.fatal(this, "AUTEV.processRequest()");
        try {
             Usuario usuario = usuarioImpl.find(request.getParameter("e3g9m93e"));
            int idSolicitud =Integer.parseInt(request.getParameter("mg4merg235m"));
            
            
            SgSolicitudEstanciaVo se = sgSolicitudEstanciaImpl.buscarEstanciaPorId(idSolicitud);
             
            if (se.getIdEstatus() == Constantes.REQUISICION_VISTO_BUENO){
                if(sgSolicitudEstanciaImpl.aprobarEstancia(usuario.getId(), idSolicitud)){
                    printMessage(("Se a aprobado la solicitud de estancia "+se.getCodigo()), request, response, "blue");
                } else {
                     printMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx). ", request, response, "red");
                }
                
            } else if(se.getIdEstatus() == 10){
                printMessage("No se puede aprobar la solicitud debido a que ya se encuentra aprobada ", request, response, "#DBA901");
            } else {
                printMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx). ", request, response, "red");
            }
        } catch (Exception e ) {
            UtilLog4j.log.error(e,e);
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
