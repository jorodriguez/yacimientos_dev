/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.catalogos.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;


import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.ihsa.servicios.catalogos.impl.UsuarioImpl;
import mx.ihsa.servicios.sistema.impl.SiUsuarioRolImpl;
import org.primefaces.PrimeFaces;
import mx.ihsa.sistema.bean.backing.Sesion;

/**
 *
 * @author mluis
 */
@Named(value = "rolBeanModel")
@ViewScoped
public class RolBeanModel implements Serializable{

    /**
     * Creates a new instance of RolBeanModel
     */
    public RolBeanModel() {
    }
    
    @Inject
    private Sesion sesion;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    //
    private int idModulo;
    private int idRol;

    private String nombreUsuario;
    private String idUsuario;
    private List<SelectItem> listaUsuario;
    private List lista;
    private boolean principal = false;
    private boolean viewAll = false;
    private int idCampo;
    private String rfcCompania;
    //

    @PostConstruct
    public void iniciarIdCampo() {
    }

    public void guardarUsuarioRol() {
	boolean v;
	v =  true;//siUsuarioRolImpl.guardarUsuarioRol(getIdRol(), getNombreUsuario(), isPrincipal(), sesion.getUsuario().getId(), getIdCampo());
	if (v) {
	    setIdModulo(-1);
	    setIdRol(-1);
	    setNombreUsuario("");
	    PrimeFaces.current().executeScript(";dialogoAgregado.show();");
	}
    }


//////    public void buscarRol() {
//////        lista = usuarioImpl.traerRolPrincipalUsuarioRolModulo(getIdRol(), getIdModulo(), getRfcCompania());
//////    }
    public void buscarRol() {
	lista = siUsuarioRolImpl.traerUsuarioPorRolModulo(getIdRol(), getIdModulo(), getIdCampo());
    }

    public void eliminarUsuarioRol(int idUr) {
	//siUsuarioRolImpl.eliminarUsuarioRol(idUr, sesion.getUsuario().getId());
	lista = siUsuarioRolImpl.traerUsuarioPorRolModulo(getIdRol(), getIdModulo(), getIdCampo());
    }
    //

  
    /**
     * @return the idModulo
     */
    public int getIdModulo() {
	return idModulo;
    }

    /**
     * @param idModulo the idModulo to set
     */
    public void setIdModulo(int idModulo) {
	this.idModulo = idModulo;
    }

    /**
     * @return the idRol
     */
    public int getIdRol() {
	return idRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
	this.idRol = idRol;
    }

    /**
     * @return the nombreUsuario
     */
    public String getNombreUsuario() {
	return nombreUsuario;
    }

    /**
     * @param nombreUsuario the nombreUsuario to set
     */
    public void setNombreUsuario(String nombreUsuario) {
	this.nombreUsuario = nombreUsuario;
    }

    /**
     * @return the idUsuario
     */
    public String getIdUsuario() {
	return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(String idUsuario) {
	this.idUsuario = idUsuario;
    }

    /**
     * @return the listaUsuario
     */
    public List<SelectItem> getListaUsuario() {
	return listaUsuario;
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<SelectItem> listaUsuario) {
	this.listaUsuario = listaUsuario;
    }

    /**
     * @return the lista
     */
    public List getLista() {
	return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List lista) {
	this.lista = lista;
    }

    /**
     * @return the principal
     */
    public boolean isPrincipal() {
	return principal;
    }

    /**
     * @param principal the principal to set
     */
    public void setPrincipal(boolean principal) {
	this.principal = principal;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
	return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
	this.idCampo = idCampo;
    }

    /**
     * @return the rfcCompania
     */
    public String getRfcCompania() {
	return rfcCompania;
    }

    /**
     * @param rfcCompania the rfcCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
	this.rfcCompania = rfcCompania;
    }

    /**
     * @return the viewAll
     */
    public boolean isViewAll() {
	return viewAll;
    }

    /**
     * @param viewAll the viewAll to set
     */
    public void setViewAll(boolean viewAll) {
	this.viewAll = viewAll;
    }
}
