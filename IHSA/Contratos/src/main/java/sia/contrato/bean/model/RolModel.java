/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.ihsa.contratos.Sesion;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioUsuarioImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 *
 * @author ihsa
 */
@Named(value  = "rolModel")
@ViewScoped
public class RolModel implements Serializable {

    static final long serialVersionUID = 1;

    /**
     * Creates a new instance of RolModel
     */
    public RolModel() {
    }
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiRolImpl siRolImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private CvConvenioUsuarioImpl cvConvenioUsuarioImpl;
    @Inject
    private ConvenioImpl convenioImpl;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    //
    private List<UsuarioRolVo> listaUsuarioRol;
    private UsuarioRolVo usuarioRolVo;
    private int idRol;
    private int idGerencia;
    private List<GerenciaVo> gerencias;
    private List<ContratoVO> contratos;
    private List<ContratoVO> listaContratos;
    private List<ContratoVO> listaContratosAgregar;
    private boolean editar;
    private boolean agregarContrato;
    private String usuarioJson;
    private String mensaje;
    //
    private final Map<String, List<SelectItem>> mapaSelect = new HashMap<String, List<SelectItem>>();

    @PostConstruct
    public void iniciar() {
	listaContratos = new ArrayList<ContratoVO>();
	listaContratosAgregar = new ArrayList<ContratoVO>();
	usuarioRolVo = new UsuarioRolVo();
	setIdRol(sesion.getIdRol());
	llenarCombos();
	llenarListaUsusarioRol();
	setEditar(false);
	setAgregarContrato(false);
	traerJson();
    }

    public void llenarCombos() {
	List<SelectItem> item = new ArrayList<SelectItem>();
	for (RolVO rolVO : siRolImpl.traerRol(Constantes.MODULO_CONTRATO)) {
	    item.add(new SelectItem(rolVO.getId(), rolVO.getNombre()));
	}
	getMapaSelect().put("roles", item);
	//
	item = new ArrayList<SelectItem>();
	for (GerenciaVo rolVO : gerenciaImpl.traerGerenciaActivaPorCampo(sesion.getUsuarioSesion().getIdCampo())) {
	    item.add(new SelectItem(rolVO.getId(), rolVO.getNombre()));
	}
	getMapaSelect().put("gerencias", item);
    }

    public void llenarListaUsusarioRol() {
	listaUsuarioRol = siUsuarioRolImpl.traerUsuarioPorRolModulo(getIdRol(), Constantes.MODULO_CONTRATO, sesion.getUsuarioSesion().getIdCampo());
    }

    public void eliminarUsuario() {
	siUsuarioRolImpl.eliminarUsuarioRol(usuarioRolVo.getIdUsuarioRol(), sesion.getUsuarioSesion().getId());
    }

    public void guardarRolesUsuario() {
	siUsuarioRolImpl.guardar(getIdRol(), usuarioRolVo.getIdUsuario(), false, sesion.getUsuarioSesion().getIdCampo(), sesion.getUsuarioSesion().getId());

	llenarListaUsusarioRol();
    }

    public void traerJson() {
	usuarioJson = apCampoUsuarioRhPuestoImpl.traerUsuarioJsonPorCampo(sesion.getUsuarioSesion().getIdCampo());

    }

    public void buscarPermisosPorUsurio() {
	boolean isRolAdminContrato = siUsuarioRolImpl.buscarRolPorUsuarioModulo(usuarioRolVo.getIdUsuario(), Constantes.MODULO_CONTRATO, Constantes.COD_CONVENIO, sesion.getUsuarioSesion().getIdCampo());
	boolean isRolComprador = siUsuarioRolImpl.buscarRolPorUsuarioModulo(usuarioRolVo.getIdUsuario(), Constantes.MODULO_COMPRA, Constantes.COD_ROL_COMPRADOR, sesion.getUsuarioSesion().getIdCampo());
	boolean isRolConsConv = siUsuarioRolImpl.buscarRolPorUsuarioModulo(usuarioRolVo.getIdUsuario(), Constantes.MODULO_CONTRATO, Constantes.COD_ROL_CONS_ADMIN_CONV, sesion.getUsuarioSesion().getIdCampo());
	mensaje = usuarioRolVo.getUsuario();
	if (isRolConsConv || isRolComprador || isRolAdminContrato) {
	    mensaje += ", tiene acceso a todos los contratos.";
	    gerencias = new ArrayList<>();
	    contratos = new ArrayList<>();
	} else {
	    boolean isGerencte = usuarioImpl.isGerente(sesion.getUsuarioSesion().getIdCampo(), usuarioRolVo.getIdUsuario());
	    if (isGerencte) {
		gerencias = gerenciaImpl.getAllGerenciaByApCampoAndResponsable(sesion.getUsuarioSesion().getIdCampo(), usuarioRolVo.getIdUsuario(), "id", true, true, false);
		if (!mensaje.isEmpty()) {
		    mensaje = " y ";
		}
	    } else {
		gerencias = new ArrayList<>();
	    }
	    contratos = cvConvenioUsuarioImpl.traerContratoPorUsuario(usuarioRolVo.getIdUsuario());
	    if (contratos != null && !contratos.isEmpty()) {
		mensaje += ", tiene acceso a los contratos.";
	    } else {
		if (!mensaje.isEmpty()) {
		    mensaje += ",";
		}
		mensaje += " NO tiene acceso a ningún contrato.";

	    }
	}
    }

    public void llenarListaContratos() {
	listaContratos = new ArrayList<ContratoVO>();
	listaContratos = convenioImpl.traerConveniosPorGerencia(idGerencia, sesion.getUsuarioSesion().getIdCampo());
    }

    public void asignarTodosContratoGerencia() {
	List<ContratoVO> lcg = convenioImpl.traerConveniosPorGerencia(getIdGerencia(), sesion.getUsuarioSesion().getIdCampo());
	cvConvenioUsuarioImpl.guardar(usuarioRolVo.getIdUsuario(), lcg, sesion.getUsuarioSesion().getId());
    }

    public void quitarTodosContratoGerencia() {

	for (ContratoVO lcg1 : contratos) {
	    if (lcg1.isSelected()) {
		cvConvenioUsuarioImpl.eliminar(lcg1.getIdRelacion(), sesion.getUsuarioSesion().getId());
	    }
	}

    }

    public void agregarContratoUsuario() {
	cvConvenioUsuarioImpl.guardar(usuarioRolVo.getIdUsuario(), listaContratosAgregar, sesion.getUsuarioSesion().getId());
	//
	contratos = cvConvenioUsuarioImpl.traerContratoPorUsuario(usuarioRolVo.getIdUsuario());
	mensaje = ", tiene acceso a los contratos.";
    }

    public void eliminarRelacionConvUsuario(int idConUser) {
	cvConvenioUsuarioImpl.eliminar(contratos.get(idConUser).getIdRelacion(), sesion.getUsuarioSesion().getId());
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the usuarioRolVo
     */
    public UsuarioRolVo getUsuarioRolVo() {
	return usuarioRolVo;
    }

    /**
     * @param usuarioRolVo the usuarioRolVo to set
     */
    public void setUsuarioRolVo(UsuarioRolVo usuarioRolVo) {
	this.usuarioRolVo = usuarioRolVo;
    }

    /**
     * @return the listaUsuarioRol
     */
    public List<UsuarioRolVo> getListaUsuarioRol() {
	return listaUsuarioRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
	this.idRol = idRol;
    }

    /**
     *
     * @return
     */
    public int getIdRol() {
	return idRol;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
	return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
	this.idGerencia = idGerencia;
    }

    /**
     * @return the mapaSelect
     */
    public Map<String, List<SelectItem>> getMapaSelect() {
	return mapaSelect;
    }

    /**
     * @return the gerencias
     */
    public List<GerenciaVo> getGerencias() {
	return gerencias;
    }

    /**
     * @return the editar
     */
    public boolean isEditar() {
	return editar;
    }

    /**
     * @param editar the editar to set
     */
    public void setEditar(boolean editar) {
	this.editar = editar;
    }

    /**
     * @return the contratos
     */
    public List<ContratoVO> getContratos() {
	return contratos;
    }

    /**
     * @return the usuarioJson
     */
    public String getUsuarioJson() {
	return usuarioJson;
    }

    /**
     * @return the listaContratos
     */
    public List<ContratoVO> getListaContratos() {
	return listaContratos;
    }

    /**
     * @return the listaContratosAgregar
     */
    public List<ContratoVO> getListaContratosAgregar() {
	return listaContratosAgregar;
    }

    /**
     * @return the agregarContrato
     */
    public boolean isAgregarContrato() {
	return agregarContrato;
    }

    /**
     * @param agregarContrato the agregarContrato to set
     */
    public void setAgregarContrato(boolean agregarContrato) {
	this.agregarContrato = agregarContrato;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
	return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
	this.mensaje = mensaje;
    }
}
