

package sia.controloficios.sistema.soporte;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
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
import javax.servlet.http.HttpSession;
import sia.constantes.Constantes;
import sia.controloficios.sistema.bean.backing.Sesion;
import sia.excepciones.InsufficientPermissionsException;
import sia.excepciones.InvalidPermissionsException;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CompaniaBloqueGerenciaVo;
import sia.modelo.oficio.vo.PermisosVo;
import sia.modelo.rol.vo.RolVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiPermisoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author esapien
 */
@WebServlet(name = "LSWU", urlPatterns = {"/LSWU"})
public class LSWU extends HttpServlet {
    
    @Inject
    private UsuarioImpl usuarioServicioImpl;
    @Inject
    private SiPermisoImpl siPermisoServicio;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    
    @Inject
    private Sesion sesion;
    
    private Usuario usuario;

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
        
        getLogger().info(this, "@processRequest");
        
        try {
            // ---
            usuario = usuarioServicioImpl.find(request.getParameter("Z4BX2"));
            
            if (sesion == null) {
                
                getLogger().info(this, "sesion == null");
                
                response.sendRedirect(Constantes.URL_REL_SIA_PRINCIPAL);
                
                return;
            }

            getLogger().info(this, "sesion != null");

            if (usuario.getClave().equals(request.getParameter("ZWZ4W"))) {

                getLogger().info(this, "Dentro del servlet");

                sesion.setUsuario(usuario);
                sesion.setPuesto(apCampoUsuarioRhPuestoRemote.getPuestoPorUsurioCampo(usuario.getId(), usuario.getApCampo().getId()));

                // obtener roles del usuario en este modulo

                List<RolVO> roles = siPermisoServicio.fetchPermisosPorUsuarioModulo(usuario.getId(), Constantes.OFICIOS_MODULO_ID, usuario.getApCampo().getId());
                 PermisosVo permisos = null;
                if (!roles.isEmpty()) {
                    permisos = new PermisosVo(roles);
                }

                StringBuilder sb = new StringBuilder();
                sb.append("rfc = ");
                sb.append(usuario.getApCampo().getCompania().getRfc());
                sb.append(", campo = ");
                sb.append(usuario.getApCampo().getId());
                sb.append(", gerencia = ");
                sb.append(usuario.getGerencia().getId());

                getLogger().info(this, sb.toString());

                sesion.setPermisos(permisos);

                // configurar opciones de bloque del usuario

                // obtener las opciones de bloques para este usuario
                List<CompaniaBloqueGerenciaVo> bloquesUsuario = apCampoUsuarioRhPuestoRemote.traerCompaniasBloquesGerencias(usuario.getId());

                sesion.setBloquesUsuario(bloquesUsuario);

                // establecer el primer bloque de la lista como activo
                CompaniaBloqueGerenciaVo bloqueActivo = bloquesUsuario.get(0);
                for(CompaniaBloqueGerenciaVo ba :bloquesUsuario){
                    if(ba.getBloqueId() == usuario.getApCampo().getId()){
                        bloqueActivo = ba;
                        break;
                    }
                }

                sesion.setBloqueActivo(bloqueActivo);
                
                sesion.setCtx(new Properties());
                
                HttpSession session = request.getSession();

                sesion.subirValoresContexto(session);
                
                response.sendRedirect(Constantes.URL_REL_CONTROL_OFICIOS);

            } else {
                response.sendRedirect(Constantes.URL_REL_SIA_PRINCIPAL);
            }
            
        } catch (InvalidPermissionsException ex) {
            
            getLogger().error(this, "Ocurrió un error de configuración de permisos: {0}", ex);
            
        }
    }
    
    
    /**
     * 
     * @return 
     */
    private UtilLog4j getLogger() {
        return UtilLog4j.log;
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
