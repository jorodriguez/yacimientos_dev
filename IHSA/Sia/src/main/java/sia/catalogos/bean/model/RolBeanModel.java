/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;


import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.SoporteListas;

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
    private SoporteListas soporteListas;
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
	setIdCampo(sesion.getUsuario().getApCampo().getId());
    }

    public int regresaIdCampo() {
	return sesion.getUsuario().getApCampo().getId();
    }

    public String regresaRfcCompania() {
	return sesion.getUsuario().getApCampo().getCompania().getRfc();
    }

    public List<SelectItem> listaModulo() {
	return soporteListas.listaModulo();
    }

    public List<SelectItem> listaCampoPorUsuario() {
	return soporteListas.listaBloquePorUsuario(sesion.getUsuario().getId());
    }

    public List<SelectItem> listaCompaniaPorUsuario() {
	return soporteListas.listaCompaniaPorUsuario(sesion.getUsuario().getId());
    }

    public List<SelectItem> listaRol() {
	return soporteListas.listaRoles(getIdModulo());
    }

    public List<SelectItem> traerUsuario(String cadena) {
	return soporteListas.regresaUsuarioActivo(cadena, getIdCampo());

    }

    public boolean buscarUsuarioRol() {
	UsuarioRolVo uvo = siUsuarioRolImpl.findNombreUsuarioRolVO(getIdRol(), getNombreUsuario(), getIdCampo());
	return uvo == null ? true : false;
    }

    public void guardarUsuarioRol() {
	boolean v;
	v = siUsuarioRolImpl.guardarUsuarioRol(getIdRol(), getNombreUsuario(), isPrincipal(), sesion.getUsuario().getId(), getIdCampo());
	if (v) {
	    setIdModulo(-1);
	    setIdRol(-1);
	    setNombreUsuario("");
	    PrimeFaces.current().executeScript(";dialogoAgregado.show();");
	}
    }

    public List<UsuarioRolVo> listaUsuarioRol() {
	return siUsuarioRolImpl.traerRolPorNombreUsuarioModulo(getNombreUsuario(), getIdModulo(), getIdCampo());
    }

//////    public void buscarRol() {
//////        lista = usuarioImpl.traerRolPrincipalUsuarioRolModulo(getIdRol(), getIdModulo(), getRfcCompania());
//////    }
    public void buscarRol() {
	lista = siUsuarioRolImpl.traerUsuarioPorRolModulo(getIdRol(), getIdModulo(), getIdCampo());
    }

    public void eliminarUsuarioRol(int idUr) {
	siUsuarioRolImpl.eliminarUsuarioRol(idUr, sesion.getUsuario().getId());
	lista = siUsuarioRolImpl.traerUsuarioPorRolModulo(getIdRol(), getIdModulo(), getIdCampo());
    }
    //

    /**
     * @param soporteListas the soporteListas to set
     */
    public void setSoporteListas(SoporteListas soporteListas) {
	this.soporteListas = soporteListas;
    }

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
