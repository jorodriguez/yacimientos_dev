/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.requisicion.bean.backing;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sia.modelo.Convenio;
import sia.servicios.convenio.impl.ConvenioImpl;

/**
 *
 * @author Héctor Acosta Sierra
 */
public class VerConvenio extends HttpServlet {
    @Inject
    private ConvenioImpl convenioServicioImpl;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // utilizar un managedbean
            UsuarioBean usuarioBean = (UsuarioBean) request.getSession().getAttribute("usuarioBean");
            if (usuarioBean != null) {
             // si el managed bean usuario es diferente de null podemos verificar si ya inicio sesion    
                if (usuarioBean.getUsuarioConectado() != null) {
                    // si inicio sesion buscar el convenio q viene en el parametro del servlet
                    Convenio convenio= convenioServicioImpl.find(request.getParameter("c").toString());

//                    InputStream in = new FileInputStream(convenio.getUrl());
//                    byte[] data = new byte[in.available()];
//                    in.read(data);
                    response.setContentType("application/pdf;");
                    // la siguiente linea es para mostrar un dialogo con la opcion de abrir guardar o cancelar
                    // response.setHeader("Content-Disposition", "attachment;filename=\"convenio.pdf\";");
//                    response.setContentLength(data.length);
                    ServletOutputStream servletoutputstream = response.getOutputStream();
//                    servletoutputstream.write(data);
                    servletoutputstream.flush();
                    servletoutputstream.close();
                } // si usuarioConectado es null el usuario no a iniciado sesion
                else {
                    response.sendRedirect("/Compras/Login.jsp");
                }
            } // si el managed bean usuario es null ni si quiera a entrado a la aplicacion
            else {
                response.sendRedirect("/Compras/Login.jsp");
            }


        } catch (IOException e) {
//            Traza.exception(this, ".jsp-->IOException" + e.getMessage());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
