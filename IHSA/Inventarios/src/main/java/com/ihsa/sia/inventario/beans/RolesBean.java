package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.SessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import javax.faces.context.FacesContext;

import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import sia.modelo.Usuario;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;

@Named(value = "roles")
@SessionScoped
public class RolesBean implements Serializable {

    public static final int ADMINISTRADOR_DE_INVENTARIOS = 1101;
    public static final int RESPONSABLE_DE_ALMACEN = 1102;
    public static final int EMPLEADO_DE_ALMACEN = 1103;

    @Inject
    private UsuarioImpl usuarioImpl;

    private Integer campoId;
    private boolean embeddedCampo;

    public UsuarioVO getUsuario() {
	return getSessionBean().getUser();
    }

    public boolean estaUsuarioEnRol(int rolId) {
	return mapearRolId(getUsuario().getRoles()).contains(rolId);
    }

    public boolean esAdministradorDeInventarios() {
	return estaUsuarioEnRol(ADMINISTRADOR_DE_INVENTARIOS);
    }

    public boolean esResponsableDeAlmacen() {
	return estaUsuarioEnRol(RESPONSABLE_DE_ALMACEN);
    }

    public boolean esEmpleadoDeAlmacen() {
	return estaUsuarioEnRol(EMPLEADO_DE_ALMACEN);
    }

    private List<Integer> mapearRolId(List<UsuarioRolVo> usuarioRoles) {
	List<Integer> listaIds = new ArrayList<Integer>();

	for (UsuarioRolVo usuarioRol : usuarioRoles) {
	    listaIds.add(usuarioRol.getIdRol());
	}

	return listaIds;
    }

    private SessionBean getSessionBean() {
	SessionBean principal = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");
	if (principal == null) {
	    return null;
	}
	return principal;
    }

    public void guardarElemento() {
	try {

	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    /**
     * @return the campoId
     */
    public Integer getCampoId() {
	return campoId;
    }

    /**
     * @param campoId the campoId to set
     */
    public void setCampoId(Integer campoId) {
	this.campoId = campoId;
    }

    public void abrirPopCampo() {
	setEmbeddedCampo(true);
	PrimeFaces.current().executeScript("PF('crearDialogoCampo').show()");
    }

    public void cerrarPopCampo() {
	setEmbeddedCampo(false);
    }

    public void saveCambioCampo() {
	try {
	    if (getCampoId() != getUsuario().getIdCampo()) {
		cambiarCampoUsr(getUsuario().getId(), getUsuario().getId(), getCampoId());
	    }
	    setEmbeddedCampo(false);
	    PrimeFaces.current().executeScript( ";mostrarDialogoCampoRefrescar();");
	} catch (Exception ex) {
	    Logger.getLogger(RolesBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * @return the embeddedCampo
     */
    public boolean isEmbeddedCampo() {
	return embeddedCampo;
    }

    /**
     * @param embeddedCampo the embeddedCampo to set
     */
    public void setEmbeddedCampo(boolean embeddedCampo) {
	this.embeddedCampo = embeddedCampo;
    }

    private void cambiarCampoUsr(String idUsr, String idUsrModifico, int newCampo) {
	//System.out.println("user : " + idUsr + "Mod " + idUsrModifico);
	usuarioImpl.cambiarCampoUsuario(idUsr, idUsrModifico, newCampo);
	Usuario usuario = usuarioImpl.find(idUsr);
	getUsuario().setIdCampo(usuario.getApCampo().getId());
	getUsuario().setCampo(usuario.getApCampo().getNombre());
    }
}
