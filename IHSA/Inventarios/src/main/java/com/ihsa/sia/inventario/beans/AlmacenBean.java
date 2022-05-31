package com.ihsa.sia.inventario.beans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import sia.inventarios.service.AlmacenRemote;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "almacen")
@ViewScoped
public class AlmacenBean extends LocalAbstractBean<AlmacenVO, Integer> implements Serializable {

    @Inject
    protected AlmacenRemote  servicio;

    @Inject
    protected UsuarioImpl usuarioServicio;

    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;

    private List<UsuarioVO> usuarios;
    private boolean embedded;
    private List<CampoUsuarioPuestoVo> campos;
    private RolesBean roles = new RolesBean();

    public AlmacenBean() {
	super(AlmacenVO.class);
    }

    @PostConstruct
    protected void init() {
	super.init();
	super.getFiltro().setIdCampo(roles.getUsuario().getIdCampo());
	usuarios = usuarioServicio.obtenerListaUsuarios(RolesBean.RESPONSABLE_DE_ALMACEN);
	cargarCampos();
    }

    public void cancelar() {
	setEmbedded(false);
    }

    public List<UsuarioVO> getUsuarios() {
	return usuarios;
    }

    @Override
    protected AlmacenRemote getServicio() {
	return servicio;
    }


    @Override
    protected String mensajeCrearKey() {
	return "sia.inventarios.almacenes.crearMensaje";
    }

    @Override
    protected String mensajeEditarKey() {
	return "sia.inventarios.almacenes.editarMensaje";
    }

    @Override
    protected String mensajeEliminarKey() {
	return "sia.inventarios.almacenes.eliminarMensaje";
    }

    public boolean isEmbedded() {
	return embedded;
    }

    public void setEmbedded(boolean embedded) {
	this.embedded = embedded;
    }

    /**
     * @return the campos
     */
    public List<CampoUsuarioPuestoVo> getCampos() {
	return campos;
    }

    private void cargarCampos() {
	try {
	    if (campos == null) {
		campos = apCampoUsuarioRhPuestoRemote.getCampoPorUsurio(roles.getUsuario().getId(), roles.getUsuario().getIdCampo());
	    }
	} catch (Exception ex) {
	    ManejarExcepcion(ex);
	}
    }

}
