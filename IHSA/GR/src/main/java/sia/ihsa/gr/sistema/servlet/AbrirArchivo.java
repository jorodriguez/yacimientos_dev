/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.gr.sistema.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.inject.Inject;
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
import sia.ihsa.gr.sistema.soporte.Sesion;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@WebServlet(name = "AbrirArchivo", urlPatterns = {"/AbrirArchivo"})
public class AbrirArchivo extends HttpServlet {

    @EJB
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @EJB
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private Sesion sesion;
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
	boolean error = false;
	ServletOutputStream servletoutputstream = null;
	String fullFilePath = null;
	AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

	try {

	    if (sesion == null) {
		response.sendRedirect(Configurador.urlSia() + "Sia");
	    } else {
		// si el managed bean usuario es diferente de null podemos verificar si ya inicio sesion
		if (sesion.getUsuarioVO() == null) {
		    response.sendRedirect(Configurador.urlSia() + "Sia");
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
				"<html><body><table><tr><td style=\"width:95%; text-align:center; padding:3px; background-color:#A8CEF0; color:#004181; font-size:15px\">"
				+ "La solicitud que est&#225;s realizando es incorrecta. "
				+ "</td></tr></table></body></html>"
			);
		    } else {
			fullFilePath = path + ets.getUrl();

			DocumentoAnexo documento = almacenDocumentos.cargarDocumento(ets.getUrl());

			response.setContentType(documento.getTipoMime());
//                        response.setHeader("Content-Disposition", "attachment;filename=\"" + ets.getNombre() + "\"");
			response.setContentLength(documento.getContenido().length);

			servletoutputstream = response.getOutputStream();
			servletoutputstream.write(documento.getContenido());
			servletoutputstream.flush();
		    }
		} // si usuarioConectado es null el usuario no a iniciado sesion
	    } // si el managed bean usuario es null ni si quiera a entrado a la aplicacion
	} catch (IOException e) {
	    LOGGER.error(this, "File : " + fullFilePath, e);
	    error = true;
	} catch (NumberFormatException e) {
	    LOGGER.error(this, "File : " + fullFilePath, e);
	    error = true;
	} catch (SIAException e) {
	    LOGGER.error(this, "File : " + fullFilePath, e);
	    error = true;
	} catch (Exception e) {
	    LOGGER.error(this, "File : " + fullFilePath, e);
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
