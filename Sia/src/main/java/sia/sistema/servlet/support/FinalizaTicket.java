/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.servlet.support;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.constantes.Constantes;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaImpl;
import sia.util.TicketEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@WebServlet(name = "FinalizaTicket", urlPatterns = {"/PROCTICK"})
public class FinalizaTicket extends HttpServlet {

    @EJB
    SiIncidenciaImpl incidenciaLocal;

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
        response.setContentType("text/html;charset=UTF-8");
        int idInc = Integer.parseInt(request.getParameter("mg4mvrg205m"));
        String userId = request.getParameter("v3g9m9v");
        String opc = request.getParameter("b20Iw9v3");
        try {
            IncidenciaVo incidenciaVo = incidenciaLocal.buscarPorId(idInc);
            if (incidenciaVo.getIdEstado() == TicketEstadoEnum.SOLUCIONADO.getId()) {
                if (opc.equals(String.valueOf(Constantes.BOOLEAN_TRUE))) {
                    incidenciaLocal.confirmarSolucionIncidencia(incidenciaVo, userId);
                    printMessage("El proceso del ticket # " + incidenciaVo.getCodigo() + " ha sido terminado.", response, "blue");
                } else {
                    incidenciaLocal.cambiarEstadoIncidencia(incidenciaVo, userId);
                    printMessage("El ticket # " + incidenciaVo.getCodigo() + " fue devuelto a Soporte Técnico, para su atención.", response, "red");
                }
            } else if (incidenciaVo.getIdEstado() == TicketEstadoEnum.CERRADO.getId()) {
                printMessage("El proceso del ticket # " + incidenciaVo.getCodigo() + " ha finalizado, no es necesario presionar el botón.", response, "green");
            }
        } catch (Exception e) {

        }
    }

    private void printMessage(String message, HttpServletResponse response, String color) {
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
