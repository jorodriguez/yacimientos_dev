/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

/**
 *
 * @author jcarranza
 */
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
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.modelo.SiAdjunto;
import sia.servicios.sistema.impl.SiAdjuntoImpl;

/**
 *
 * @author jcarranza
 */
@WebServlet(name = "GenUUID", urlPatterns = {"/GenUUID"})
public class GenUUID extends HttpServlet {

    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;

    static Logger log = Logger.getLogger(AbrirArchivo.class.getName());

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
	try {
	    // Verificar si hay una sesion iniciada
	    StringBuilder msg = new StringBuilder();
	    UsuarioBean usuarioBean = (UsuarioBean) request.getSession().getAttribute("usuarioBean");
	    if ((usuarioBean != null) && (usuarioBean.getUsuarioConectado() != null)) {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		msg.append("<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">");
		msg.append("Iniciar update UUID.......");
		int cuantos = 0;
		for (SiAdjunto ets : servicioSiAdjuntoImpl.findAllNotUUID()) {
		    UUID uuid = UUID.randomUUID();
		    ets.setUuid(uuid.toString());
		    servicioSiAdjuntoImpl.update(ets, usuarioBean.getUsuarioConectado().getId());
		    cuantos++;
		}

		msg.append("Se actualizaron ").append(cuantos).append(" registros.....").append("FIN update UUID");
		msg.append("</td></tr></table></body></html>");

		out.println(msg.toString());
	    }

	} catch (Exception e) {
	    log.log(Level.SEVERE, null, e);
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
