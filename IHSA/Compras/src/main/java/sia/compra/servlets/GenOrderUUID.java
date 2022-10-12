/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.modelo.Orden;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.servicios.orden.impl.OrdenImpl;

/**
 *
 * @author ihsa
 */

@WebServlet(name = "GenOrderUUID", urlPatterns = {"/GenOrderUUID"})
public class GenOrderUUID extends HttpServlet {

    @Inject
    private OrdenImpl ordenRmt;
    
    static Logger log = Logger.getLogger(GenOrderUUID.class.getName());

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
        try {
            // Verificar si hay una sesion iniciada
             StringBuilder msg = new StringBuilder();
            UsuarioBean usuarioBean = (UsuarioBean) request.getSession().getAttribute("usuarioBean");
            if ((usuarioBean != null) && (usuarioBean.getUsuarioConectado() != null)) {
                String anio = request.getParameter("Anio");
                String estatus = request.getParameter("Estatus");
                response.setContentType("text/html");
                        PrintWriter out = response.getWriter();
                        msg.append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">");
                        msg.append("Iniciar update UUID.......");
                        int cuantos = 0;
                        int anioInt = 0;
                        int estatusInt = 0;
                try{
                    anioInt = Integer.parseInt(anio);
                }catch(Exception e){
                    anioInt = 0;
                }
                
                try{
                    estatusInt = Integer.parseInt(estatus);
                }catch(Exception e){
                    estatusInt = 0;
                }
                
                for(Integer ordenID : ordenRmt.ordenesSinUUID(anioInt, estatusInt)){
                    Orden orden = ordenRmt.find(ordenID);
                    UUID uuid = UUID.randomUUID();            
                    orden.setUuid(uuid.toString());
                    ordenRmt.editarOrden(orden);
                    cuantos++;
                }
                
                msg.append("Se actualizaron ").append(cuantos).append(" registros.....").append("FIN update UUID");
                                msg.append("</td></tr></table></body></html>");
                        
                        out.println(msg.toString() );
            } 

        } catch(Exception e){
            log.log(Level.SEVERE, null, e);
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

