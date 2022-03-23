/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.servlets;

import java.io.IOException;
import javax.el.ELContext;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Setter;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcUsuarioOpcionImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.GenNrStats;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@WebServlet(name = "LSWU", urlPatterns = {"/LSWU"})
public class LSWU extends HttpServlet {

    @Inject
    private UsuarioImpl usuarioServicioImpl;
    @Inject
    private OcUsuarioOpcionImpl ocUsuarioOpcionImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private CompaniaImpl companiaImpl;
    @Inject
    private ApCampoImpl apCampoImpl;
    //
    @Inject
    UsuarioBean usuarioBean;
    @Setter
    private Usuario usuario;

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
            // ---
            UtilLog4j.log.info(this, "Z4BX2 " + request.getParameter("Z4BX2"));
            usuario = usuarioServicioImpl.find(request.getParameter("Z4BX2"));
            if (usuario != null) {
                if (usuario.getClave().equals(request.getParameter("ZWZ4W"))) {
                    int t = Integer.parseInt(request.getParameter("ZWZCA"));
                    String rutaPag = request.getParameter("ZWZPA");
                    CampoUsuarioPuestoVo campoUsuarioPuestoVo = apCampoUsuarioRhPuestoImpl.findByUsuarioCampo(t > 0 ? t : usuario.getApCampo().getId(), usuario.getId());
                    if (campoUsuarioPuestoVo != null) {
                        usuarioBean.setPuesto(campoUsuarioPuestoVo.getPuesto());
                        usuarioBean.setCompania(companiaImpl.buscarPorRFC(campoUsuarioPuestoVo.getRfcCompania()));
                        usuario.setApCampo(apCampoImpl.find(campoUsuarioPuestoVo.getIdCampo()));
                    } else {
                        usuarioBean.setCompania(usuario.getApCampo().getCompania());
                    }

                    usuarioBean.setUsuarioConectado(usuario);

                    //Verifica rol
                    SiOpcionVo siOpcionVo = ocUsuarioOpcionImpl.opcionPrincipal(usuario.getId());
                    if (siOpcionVo == null) {
                        usuarioBean.setPaginaInicial("Principal");
                    } else {
                        UtilLog4j.log.info("Ruta inicial :  : : " + siOpcionVo.getPagina());
                        usuarioBean.setPaginaInicial(siOpcionVo.getPagina());
                    }
                    if (!rutaPag.isEmpty()) {
                        usuarioBean.setPaginaInicial(rutaPag);
                    }

                    usuarioBean.setListaCampo(apCampoUsuarioRhPuestoImpl.getAllPorUsurio(usuario.getId()));
                    //
                    usuarioBean.llenarRoles();
                    //
                    response.sendRedirect(Constantes.URL_REL_SIA_WEB + usuarioBean.getPaginaInicial());
                } else {
                    response.sendRedirect(Constantes.URL_REL_SIA_PRINCIPAL);
                }
            } else {
                response.sendRedirect(Constantes.URL_REL_SIA_PRINCIPAL);
            }
        } catch (IOException e) {
            UtilLog4j.log.fatal(this, e);
            GenNrStats.saveNrData("COMPRAS-LSWU-Exception");
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

    protected FacesContext getFacesContext(HttpServletRequest request, HttpServletResponse response) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            //UtilLog4j.log.fatal(this, "No tiene sesion . . . . . . . . . . .. .  '+ + + + + + + + + + ++ + + + + +");
            FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            facesContext = contextFactory.getFacesContext(request.getSession().getServletContext(), request, response, lifecycle);

            // Set using our inner class
            InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

            // set a new viewRoot, otherwise context.getViewRoot returns null
            UIViewRoot view = facesContext.getApplication().getViewHandler().createView(facesContext, "");
            facesContext.setViewRoot(view);
        }
        return facesContext;
    }

    protected Application getApplication(FacesContext facesContext) {
        return facesContext.getApplication();
    }

    protected Object getManagedBean(Object beanName, FacesContext facesContext) {
        try {
            ELContext elContext = facesContext.getELContext();
            beanName = elContext.getELResolver().getValue(elContext, null, beanName);
        } catch (RuntimeException e) {
            throw new FacesException(e.getMessage(), e);
        }
        if (beanName == null) {
            throw new FacesException("El bean con el nombre '" + beanName + "' no se encontro. verifique el nombre.");
        }
        return beanName;

//////        Object object = null;
//////        if (facesContext != null) {
//////            ELContext ec = facesContext.getELContext();
//////            if (ec != null) {
//////                ELResolver er = ec.getELResolver();
//////                object = er.getValue(ec, null, beanName);
//////            }
//////        }
//////        return object;
        //return getApplication(facesContext).getVariableResolver().resolveVariable(facesContext, beanName);
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
