/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedProperty;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.constantes.Constantes;
import sia.contrato.bean.model.RolModel;
import sia.contrato.bean.soporte.FacesUtils;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;

/**
 *
 * @author ihsa
 */
@Named(value = "depRol")
@ViewScoped
public class RolBean implements Serializable {

    static final long serialVersionUID = 1;
    /**
     * Creates a new instance of RolBean
     */
    @ManagedProperty(value = "#{rolModel}")
    private RolModel rolModel;

    public RolBean() {
    }

    /**
     * @return the listaRol
     */
    public List<SelectItem> getListaRol() {
	return rolModel.getMapaSelect().get("roles");

    }

    /**
     * @return the listaRol
     */
    public List<SelectItem> getListaGerencias() {
	return rolModel.getMapaSelect().get("gerencias");

    }

    /**
     * @return the listaRol
     */
    public List<SelectItem> getListaGerencia() {
	List<SelectItem> item = new ArrayList<SelectItem>();
	for (Object l1 : rolModel.getMapaSelect().get("gerencias")) {
	    GerenciaVo gerenciaVo = (GerenciaVo) l1;
	    item.add(new SelectItem(gerenciaVo.getId(), gerenciaVo.getNombre()));
	}
	return item;
    }

    public void cambiarGerencia(ValueChangeEvent event) {
	rolModel.setIdGerencia(Integer.parseInt(event.getNewValue().toString()));
	rolModel.llenarListaUsusarioRol();
    }

    public void cambiarRol(ValueChangeEvent event) {
	rolModel.setIdRol(Integer.parseInt(event.getNewValue().toString()));
	rolModel.llenarListaUsusarioRol();
    }

    public void iniciarAgregarGerencia() {
	rolModel.setEditar(true);
	rolModel.getUsuarioRolVo().setIdUsuarioRol(Integer.parseInt(FacesUtils.getRequestParam("idUsuarioRol")));
	rolModel.getUsuarioRolVo().setUsuario(FacesUtils.getRequestParam("nombreUsuarioRol"));
	rolModel.getUsuarioRolVo().setNombreRol(FacesUtils.getRequestParam("nombreRol"));
	//
	PrimeFaces.current().executeScript(";llenarEtiqueta('frmAgregarRolUsuario', 'cjNombreUsuario','" + rolModel.getUsuarioRolVo().getUsuario() + "');");
	//
	PrimeFaces.current().executeScript(";$(dialogoAgregarRolUsuario).modal('show');");
    }

    public void limpiarPestana() {
	rolModel.setIdGerencia(Constantes.MENOS_UNO);
	rolModel.getListaContratos().clear();
	rolModel.getListaContratosAgregar().clear();
	rolModel.setAgregarContrato(false);
    }

    public void seleccionarUsuario(SelectEvent event) {
	UsuarioRolVo obj = (UsuarioRolVo) event.getObject();
	rolModel.setUsuarioRolVo(obj);
	rolModel.buscarPermisosPorUsurio();
	//
	rolModel.setIdGerencia(Constantes.MENOS_UNO);
	rolModel.getListaContratos().clear();
	rolModel.getListaContratosAgregar().clear();
	PrimeFaces.current().executeScript(";activarTab('accesoContrato');");
    }

    public void eliminarUsuario() {
	rolModel.getUsuarioRolVo().setIdUsuarioRol(Integer.parseInt(FacesUtils.getRequestParam("idUsuarioRol")));
	//rolModel.eliminarUsuario();
	rolModel.llenarListaUsusarioRol();
    }

    public void guardarRolesUsuario() {
	rolModel.guardarRolesUsuario();
	PrimeFaces.current().executeScript(";$(dialogoAgregarRolUsuario).modal('hide');;");
    }

    public void llenarJson() {
	PrimeFaces.current().executeScript(";llenarUsuarios(" + rolModel.getUsuarioJson() + ");");
	PrimeFaces.current().executeScript(";usuario('frmAgregarRolUsuario','hidenDes', '', '');");
//
	PrimeFaces.current().executeScript(";$(dialogoAgregarRolUsuario).modal('show');");
///	System.out.println("json provee " + jsonProveedores);

    }

    public void llenarUsurioJson() {
	PrimeFaces.current().executeScript(";llenarUsuarios(" + rolModel.getUsuarioJson() + ");");
	PrimeFaces.current().executeScript(";usuario('frmAccesoContrato','hidenDes', 'hidenDesNombre', 'btnFiltro');");
    }

    public void buscarPermisosPorUsurio() {
	rolModel.buscarPermisosPorUsurio();
	//
	llenarUsurioJson();
	//
	rolModel.setIdGerencia(Constantes.MENOS_UNO);
	rolModel.getListaContratos().clear();
	rolModel.getListaContratosAgregar().clear();
    }

    public void agregarRelacionUsuarioContrato() {
	rolModel.setAgregarContrato(true);
    }

    public void eliminarRelacionConvUsuario() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idConvUser"));
	rolModel.eliminarRelacionConvUsuario(id);
	rolModel.getContratos().remove(id);
    }

    public void mostrarContratosPorGerencia(ValueChangeEvent event) {
	rolModel.setIdGerencia((Integer) event.getNewValue());
	rolModel.llenarListaContratos();
    }

    public void asignarTodosContratoGerencia() {
	if (rolModel.getIdGerencia() > 0) {
	    rolModel.asignarTodosContratoGerencia();
	    rolModel.buscarPermisosPorUsurio();
	} else {
	    FacesUtils.addErrorMessage("Es necesario seleccionar al menos una gerencia");
	}
    }

    public void quitarTodosContratoGerencia() {
	rolModel.quitarTodosContratoGerencia();
	rolModel.buscarPermisosPorUsurio();
    }

    public void seleccionarContrato(SelectEvent event) {
	ContratoVO cvo = (ContratoVO) event.getObject();
	rolModel.getListaContratosAgregar().add(cvo);
	//
	rolModel.getListaContratos().remove(cvo);
    }

    public void cancelarAgregarContrato() {
	rolModel.getListaContratosAgregar().clear();
	rolModel.setAgregarContrato(false);
    }

    public void agregarContratoUsuario() {
	if (!rolModel.getListaContratosAgregar().isEmpty()) {
	    rolModel.agregarContratoUsuario();
	    rolModel.setAgregarContrato(false);
	    rolModel.getListaContratosAgregar().clear();
	}
    }

    public void quitarContratotemporarUsuario() {
	int id = Integer.parseInt(FacesUtils.getRequestParam("idConvTemp"));
	rolModel.getListaContratosAgregar().remove(id);
    }

    /**
     * @param rolModel the rolModel to set
     */
    public void setRolModel(RolModel rolModel) {
	this.rolModel = rolModel;
    }

    /**
     * @return the usuarioRolVo
     */
    public UsuarioRolVo getUsuarioRolVo() {
	return rolModel.getUsuarioRolVo();
    }

    /**
     * @param usuarioRolVo the usuarioRolVo to set
     */
    public void setUsuarioRolVo(UsuarioRolVo usuarioRolVo) {
	rolModel.setUsuarioRolVo(usuarioRolVo);
    }

    /**
     * @return the listaUsuarioRol
     */
    public List<UsuarioRolVo> getListaUsuarioRol() {
	return rolModel.getListaUsuarioRol();
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
	rolModel.setIdRol(idRol);
    }

    /**
     *
     * @return
     */
    public int getIdRol() {
	return rolModel.getIdRol();
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
	return rolModel.getIdGerencia();
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
	rolModel.setIdGerencia(idGerencia);
    }

    /**
     * @return the editar
     */
    public boolean isEditar() {
	return rolModel.isEditar();
    }

    /**
     * @param editar the editar to set
     */
    public void setEditar(boolean editar) {
	rolModel.setEditar(editar);
    }

    /**
     * @return the listaRol
     */
    public List<GerenciaVo> getGerencias() {
	return rolModel.getGerencias();
    }

    /**
     * @return the listaRol
     */
    public List<ContratoVO> getContratos() {
	return rolModel.getContratos();
    }

    /**
     * @return the usuarioJson
     */
    public String getUsuarioJson() {
	return rolModel.getUsuarioJson();
    }

    /**
     * @return the listaContratos
     */
    public List<ContratoVO> getListaContratos() {
	return rolModel.getListaContratos();
    }

    /**
     * @return the listaContratosAgregar
     */
    public List<ContratoVO> getListaContratosAgregar() {
	return rolModel.getListaContratosAgregar();
    }

    /**
     * @return the agregarContrato
     */
    public boolean isAgregarContrato() {
	return rolModel.isAgregarContrato();
    }

    /**
     * @param agregarContrato the agregarContrato to set
     */
    public void setAgregarContrato(boolean agregarContrato) {
	rolModel.setAgregarContrato(agregarContrato);
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
	return rolModel.getMensaje();
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
	rolModel.setMensaje(mensaje);
    }
}
