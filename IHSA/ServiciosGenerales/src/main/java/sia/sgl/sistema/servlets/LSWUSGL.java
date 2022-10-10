/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import sia.constantes.Constantes;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 *
 * @author hacosta
 */
@WebServlet(name = "LSWUSGL", urlPatterns = {"/LSWUSGL"})
public class LSWUSGL extends HttpServlet {

    @Inject
    private Sesion sesion;
    //
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    //

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * * Modifoc NLopez 14/11/2013
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String url = UtilSia.getUrl(request);
            sesion.setUsuario(usuarioImpl.find(request.getParameter("Z4BX2")));
            if (sesion.getUsuario() != null && sesion.getUsuario().getClave().equals(request.getParameter("ZWZ4W"))) {

                sesion.setRfcEmpresa(sesion.getUsuario().getApCampo().getCompania().getRfc());
                int idCampoActual = sesion.getUsuario().getApCampo().getId();
                int idoficinaActual = sesion.getUsuario().getSgOficina().getId();
                List<UsuarioRolVo> ur = siUsuarioRolImpl.traerRolPorUsuarioModulo(sesion.getUsuario().getId(), Constantes.MODULO_SGYL, idCampoActual);
                sesion.setNombreRhPuesto(apCampoUsuarioRhPuestoImpl.getPuestoPorUsurioCampo(sesion.getUsuario().getId(), idCampoActual));
                if (ur != null) {
                    for (UsuarioRolVo usuarioRolVo : ur) {
                        if (usuarioRolVo.isPrincipal()) {
                            sesion.setNombreRol(usuarioRolVo.getNombreRol());
                            sesion.setIdRol(usuarioRolVo.getIdRol());
                            break;
                        }
                    }
                    //System.out.println("Rol: " + sesion.getIdRol());
                    sesion.setRoles(ur);
                    //crea el menu
                    sesion.crearMenu(sesion.getUsuario().getId(), idCampoActual);
                    sesion.setListCampoByUsusario(apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuario().getId()));
                    //
                    //
                    //agrga la oficina a RSGyL, Asisitente, y capacitacion

                    if (sesion.getUsuario().getSgOficina() != null) {
                        if (sesion.getUsuario().getSgOficina().getId() > 0) {
                            idoficinaActual = sesion.getUsuario().getSgOficina().getId();
                        }
                    }
                    // se debe de cargar previo la oficina actual y ya de ser necesaria se actualiza.
                    sesion.setOficinaActual(sgOficinaImpl.buscarPorId(idoficinaActual));
                    switch (sesion.getIdRol()) {
                        case Constantes.ROL_EMPLEADO_GENERAL:
                            for (UsuarioRolVo se : sesion.getRoles()) {
                                if (se.getIdRol() == Constantes.ROL_ID_ADMINISTRA_SGL) {
                                    sesion.setOficinaActual(sgOficinaImpl.buscarPorId(idoficinaActual));
                                    sesion.setOficinasUsuario((sgOficinaImpl.traerListaOficina()));
                                    break;
                                }
                            }
                            break;
                        case Constantes.SGL_RESPONSABLE:
                            sesion.setOficinaActual(sgOficinaImpl.buscarPorId(idoficinaActual));
                            sesion.setRol(Constantes.ROL_SGL_RESPONSABLE);
                            sesion.setOficinasUsuario((sgOficinaImpl.traerListaOficina()));
                            UtilLog4j.log.info(this, "Oficina asignada default al Usuario Responsable SGL: Torres martel");
                            break;
                        case Constantes.ROL_ID_ADMINISTRA_SGL:
                            sesion.setRol(Constantes.ROL_SGL_ADMINISTRA);
                            sesion.setOficinasUsuario((sgOficinaImpl.traerListaOficina()));
                            sesion.setOficinaActual(sgOficinaImpl.buscarPorId(idoficinaActual));
                            UtilLog4j.log.info(this, "Oficina asignada default al Usuario Administrador de SGL: " + sesion.getOficinaActual().getNombre());
                            break;
                        case Constantes.ROL_ID_ANALISTA_SGL:
                            sesion.setRol(Constantes.ROL_SGL_ANALISTA);
                            List<OficinaVO> oficinasAnalistas = sgOficinaAnalistaImpl.traerOficina(sesion.getUsuario().getId());

                            if (oficinasAnalistas != null) { //Poniendo la lista de Oficinas del Usuario en Sesion
                                List<OficinaVO> oficinas = new ArrayList<>();
                                for (OficinaVO oficinaAnalista : oficinasAnalistas) {
                                    oficinas.add(oficinaAnalista);
                                    if (oficinaAnalista.getSgOficinaAnalistaVo().isPrincipal()) {
                                        sesion.setOficinaActual(sgOficinaImpl.buscarPorId(oficinaAnalista.getId()));
                                    }
                                }
                                sesion.setOficinasUsuario((oficinas));
                            } else {
                                sesion.setOficinaActual(sgOficinaImpl.buscarPorId(idoficinaActual));
                                sesion.setOficinasUsuario(null);
                            }
                            break;
                        case Constantes.ROL_ID_ASISTENTE_DIRECCION:
                            sesion.setOficinaActual(sgOficinaImpl.buscarPorId(idoficinaActual));
                            break;
                        case Constantes.ROL_ID_SGL_CAPACITACION:
                            sesion.setOficinaActual(sgOficinaImpl.buscarPorId(idoficinaActual));
                            break;
                        default:
                            if (usuarioImpl.isGerente(Constantes.AP_CAMPO_DEFAULT, sesion.getUsuario().getId())) {
                                UtilLog4j.log.info(this, Constantes.ROL_SISTEMA_GERENTE);
                                sesion.setRol(Constantes.ROL_SISTEMA_GERENTE);
                            } else {
                                UtilLog4j.log.info(this, Constantes.ROL_SISTEMA_EMPLEADO);
                                sesion.setRol(Constantes.ROL_SISTEMA_EMPLEADO);
                            }
                            break;
                    }

                    sesion.setCtx(new Properties());

                    HttpSession session = request.getSession();
                    sesion.subirValoresContexto(session);
                    response.sendRedirect(url + Constantes.URL_REL_SERVICIOS_GENERALES);
                } else { // si no tiene roles lo regresa al sia
                    response.sendRedirect(url + Constantes.URL_REL_SIA_PRINCIPAL);
                }

                //Esto es para los roles que existen.
            } else {
                response.sendRedirect(url + Constantes.URL_REL_SIA_PRINCIPAL);
            }
        } catch (IOException e) {
            UtilLog4j.log.fatal(this, e);
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
