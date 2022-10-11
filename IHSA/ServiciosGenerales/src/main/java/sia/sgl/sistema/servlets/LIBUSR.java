/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.servlets;

import java.io.IOException;
import java.io.PrintWriter;
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
import sia.excepciones.EmailNotFoundException;
import sia.modelo.RhUsuarioGerencia;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.usuario.impl.RhUsuarioGerenciaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@WebServlet(name = "LIBUSR", urlPatterns = {"/LIBUSR"})
public class LIBUSR extends HttpServlet {

    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private RhUsuarioGerenciaImpl rhUsuarioGerenciaImpl;

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UtilLog4j.log.info(this, "LIBUSR.processRequest()");

        try {
            int idGerencia = Integer.parseInt(request.getParameter("0n9gsda"));
            String idUsuarioBaja = request.getParameter("usdayfg9");
            String idUsuarioDaBaja = request.getParameter("97sdaf97");

            RhUsuarioGerencia rhUsuarioGerencia = this.rhUsuarioGerenciaImpl.findByUsuarioAndGerencia(idGerencia, idUsuarioBaja);

            this.rhUsuarioGerenciaImpl.setFreeUsuarioAndAdvicing(rhUsuarioGerencia.getId().intValue(), idUsuarioDaBaja);
        } catch (EmailNotFoundException enfe) {
            printMessage(("La liberación no pudo ser efectuada debido a que los siguientes usuarios no tienen email: " + enfe.getAllUsuariosWithoutEmail()), request, response);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    private void printMessage(String message, HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter output = response.getWriter();

            output.println("<html>");
            output.println("<head>");
            output.println("<title>Sistema Integral de Administración</title>");
            output.println("</head>");
            output.println("<body >");
            output.println("<h1 style=\"color: red; font-size: 12px;\">" + message + "</h1>");
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    protected FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {

            FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);

            // Set using our inner class
            LIBUSR.InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

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
