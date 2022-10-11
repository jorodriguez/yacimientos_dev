/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.contratos.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.el.ELContext;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
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
import sia.constantes.Constantes;
import sia.ihsa.contratos.Sesion;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.MenuSiOpcionVo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {

    static final long serialVersionUID = 1;
    //
    @Inject
    private UsuarioImpl usuarioServicioImpl;
    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private ApCampoImpl apCampoImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    //
    @Inject
    Sesion sesion;
    //

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {

	processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	try {
	    sesion.setUsuarioSesion(usuarioServicioImpl.findById(request.getParameter("Z4BX2")));
	    if (sesion.getUsuarioSesion() != null) {
		if (sesion.getUsuarioSesion().getClave().equals(request.getParameter("ZWZ4W"))) {
                    sesion.setCampoUsuarioPuestoVo(new CampoUsuarioPuestoVo());
		    sesion.setLista(apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioSesion().getId()));
		    //Verifica rol
		    List<UsuarioRolVo> lu = siUsuarioRolImpl.traerRolPorUsuarioModulo(sesion.getUsuarioSesion().getId(), Constantes.MODULO_CONTRATO, sesion.getUsuarioSesion().getIdCampo());
		    if (lu != null && !lu.isEmpty()) {
			sesion.setRfcEmpresa(apCampoImpl.find(sesion.getUsuarioSesion().getIdCampo()).getCompania().getRfc());
			//
			List<SiOpcionVo> listaItems = siOpcionImpl.getAllSiOpcionBySiModulo(Constantes.MODULO_CONTRATO, sesion.getUsuarioSesion().getId(), sesion.getUsuarioSesion().getIdCampo());
			List<MenuSiOpcionVo> itemsReturn = new ArrayList<>();
			for (SiOpcionVo oldVO : listaItems) {
			    MenuSiOpcionVo menuSiOpcionVo = new MenuSiOpcionVo();
			    menuSiOpcionVo.setPadre(oldVO);
			    List<SiOpcionVo> listaOpciones = siOpcionImpl.getChildSiOpcion(oldVO.getId(), sesion.getUsuarioSesion().getId(), Constantes.MODULO_CONTRATO);
			    for (SiOpcionVo hijo : listaOpciones) {
				menuSiOpcionVo.getHijos().add(hijo);
			    }
			    itemsReturn.add(menuSiOpcionVo);
			}
                        OUTER:
                        for (UsuarioRolVo usuarioRolVo : lu) {
                            switch (usuarioRolVo.getIdRol()) {
                                case Constantes.ROL_ADMINISTRA_CONTRATO:
                                    sesion.setPaginaInicio("/vistas/contrato/admin/inicio.xhtml");
                                    sesion.setIdRol(Constantes.ROL_ADMINISTRA_CONTRATO);
                                    break OUTER;
                                case Constantes.ROL_REVISA_CONTRATO:
                                    sesion.setPaginaInicio("/vistas/contrato/consulta/revisaContrato.xhtml");
                                    sesion.setIdRol(Constantes.ROL_REVISA_CONTRATO);
                                    break OUTER;
                                case Constantes.ROL_CONSULTA_CONTRATO:
                                    sesion.setPaginaInicio("/vistas/contrato/consulta/revisaContrato.xhtml");
                                    sesion.setIdRol(Constantes.ROL_CONSULTA_CONTRATO);
                                    break OUTER;
                                case Constantes.ROL_REGISTRA_PROVEEDOR:
                                    sesion.setPaginaInicio("/vistas/contrato/proveedores/administraProveedores.xhtml");
                                    sesion.setIdRol(Constantes.ROL_REGISTRA_PROVEEDOR);
                                    break OUTER;
                                default:
                                    break;
                            }
                        }
			sesion.setMenu(itemsReturn);
			//sout
			response.sendRedirect(Constantes.URL_REL_CONTRATO + sesion.getPaginaInicio());
		    } else {
			response.sendRedirect(Constantes.URL_REL_SIA_PRINCIPAL);
		    }
		} else {
		    response.sendRedirect(Constantes.URL_REL_SIA_PRINCIPAL);
		}
	    }
	} catch (IOException e) {
	    UtilLog4j.log.fatal(e);
	}
    }

    protected Object getManagedBean(Object beanName, FacesContext facesContext) {
	try {
	    ELContext elContext = facesContext.getELContext();
	    beanName = elContext.getELResolver().getValue(elContext, null, beanName);
	} catch (Exception e) {
	    throw new FacesException(e.getMessage(), e);
	}
	if (beanName == null) {
	    throw new FacesException("El bean no se encontro. verifique el nombre.");
	}
	return beanName;
    }

    private abstract static class InnerFacesContext extends FacesContext {

	protected static void setFacesContextAsCurrentInstance(FacesContext facesContext) {
	    FacesContext.setCurrentInstance(facesContext);
	}
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
}
