/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;




import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.administracion.bean.model.ModelDevolucionRequisicion;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Orden;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author mluis
 */
@Named(value = "devolucionRequisicion")
@ViewScoped
public class DevolucionRequisicion implements Serializable {

    @Inject
    private ModelDevolucionRequisicion modelDevolucionRequisicion;
    private String req;
    private boolean mostrar = false;
    private Orden orden;
    private AutorizacionesOrden autorizacionesOrden;
    DataModel<Orden> listaOrden;
    String dev = "False";
    DataModel<AutorizacionesOrden> listaAuto;
    TreeSet treeSet = new TreeSet();
    List idOrden = new ArrayList();

    /**
     * Creates a new instance of DevolucionRequisicion
     */
    public DevolucionRequisicion() {
    }

    public List<SelectItem> getListaCampo() {
	return modelDevolucionRequisicion.listaCampo();
    }

    public void cambiarValorCampo(ValueChangeEvent valueChangeEvent) {
	modelDevolucionRequisicion.setIdBloque((Integer) valueChangeEvent.getNewValue());
	modelDevolucionRequisicion.setIdGerencia(-1);

    }

    public void limpiar() {
	this.req = "";
	modelDevolucionRequisicion.setRequisicionVO(null);
    }

    public void irReasignarRequision() {
	setIdBloque(modelDevolucionRequisicion.regresaBloqueSesion());
	this.req = "";
	modelDevolucionRequisicion.setRequisicionVO(null);
    }

    public void buscarRequisicion() {
	modelDevolucionRequisicion.setRequisicionVO(modelDevolucionRequisicion.buscarRequisicion(getReq().trim()));
	if (modelDevolucionRequisicion.getRequisicionVO() == null) {
	    this.mostrar = true;
	    this.listaAuto = null;
	    this.listaOrden = null;
	} else {
	    this.mostrar = false;
	    this.autorizacionesOrden = null;
	    this.orden = null;
	    this.listaOrden = null;
	    this.listaAuto = null;
	    this.idOrden = new ArrayList();
	    this.treeSet = new TreeSet();
	    this.listaOrden = new ListDataModel(this.modelDevolucionRequisicion.buscarOrdenConReqJPA(getRequisicionVO().getId()));
	    if (this.listaOrden != null) {
		for (Orden o : this.listaOrden) {
		    this.idOrden.add(o.getId());
		}
		this.listaAuto = new ListDataModel(this.modelDevolucionRequisicion.buscarAutoOrden(this.idOrden));
	    }
	    if (this.listaAuto.getRowCount() > 0) {
		for (AutorizacionesOrden auto : this.listaAuto) {
		    this.treeSet.add(auto.getEstatus().getId());
		}
	    }
	    if (this.treeSet.contains(101)
		    || this.treeSet.contains(110)
		    || this.treeSet.contains(120)
		    || this.treeSet.contains(130)
		    || this.treeSet.contains(140)
		    || this.treeSet.contains(150)) {
		this.dev = "False";
	    } else {
		this.dev = "True";
	    }
	}
    }

    public DataModel getOrdenesCompra() {
	try {
	    return this.listaOrden;
	} catch (Exception e) {
	    return null;
	}
    }

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeListener) {
	modelDevolucionRequisicion.setIdBloque((Integer) valueChangeListener.getNewValue());
	modelDevolucionRequisicion.setRequisicionVO(null);
	listaOrden = null;
	listaAuto = null;

    }

    public List<SelectItem> getListaCampoPorUsuario() {
	return modelDevolucionRequisicion.listaCampoPorUsuario();
    }

    /**
     * @return Lista de usuarios Que Colocan orden de compra y o servicio
     */
    public List<SelectItem> getListaAnalista() {
	return modelDevolucionRequisicion.listaAnalista();
    }

    public void devolverSIARequisicion() {
	if (modelDevolucionRequisicion.getRequisicionVO().getIdStatus() == 40 && this.dev.equals("True")) {
	    modelDevolucionRequisicion.setDevPop(true);
	} else {
	    this.orden = null;
	    this.autorizacionesOrden = null;
	    FacesUtils.addInfoMessage("No tiene autorización para esta operación");
	}
    }

    public void completarDevolverSIARequisicion() {
	try {
	    if (!modelDevolucionRequisicion.getMotivo().trim().isEmpty()) {
		this.modelDevolucionRequisicion.devolverSIARequisicion();
		modelDevolucionRequisicion.setRequisicionVO(null);
		this.listaAuto = null;
		this.listaOrden = null;
		this.idOrden = new ArrayList();
		this.treeSet = new TreeSet();
		modelDevolucionRequisicion.setDevPop(false);
		modelDevolucionRequisicion.setMotivo("");
		FacesUtils.addInfoMessage("msgDev", "Se devolvió la requisión " + getReq());
		setReq("");
		PrimeFaces.current().executeScript(";cerrarDialogoBootstrap(dialogoReasignarReq);");
	    } else {
		FacesUtils.addErrorMessage("msgDevReq", "Es necesario especificar el motivo de devolución");
	    }
	} catch (Exception ex) {
	    FacesUtils.addErrorMessage("msgDevReq", "No se pudo reasignar la requisición, por favor notifique el problema a: sia@ihsa.mx");
	    Logger.getLogger(DevolucionRequisicion.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void cancelarDevolverSIARequisicion() {
	modelDevolucionRequisicion.setDevPop(false);
	modelDevolucionRequisicion.setMotivo("");
    }

    public DataModel getListaAuto() {
	return listaAuto;
    }

    public void verificarOC() {
	this.orden = (Orden) this.listaOrden.getRowData();
	this.autorizacionesOrden = this.modelDevolucionRequisicion.buscarAutoOrden(this.orden.getId());
    }

    public void setModelDevolucionRequisicion(ModelDevolucionRequisicion modelDevolucionRequisicion) {
	this.modelDevolucionRequisicion = modelDevolucionRequisicion;
    }

    public String getReq() {
	return req;
    }

    public void setReq(String req) {
	this.req = req;
    }

    public boolean isMostrar() {
	return mostrar;
    }

    public void setMostrar(boolean mostrar) {
	this.mostrar = mostrar;
    }

    public Orden getOrden() {
	return orden;
    }

    public void setOrden(Orden orden) {
	this.orden = orden;
    }

    public AutorizacionesOrden getAutorizacionesOrden() {
	return autorizacionesOrden;
    }

    public void setAutorizacionesOrden(AutorizacionesOrden autorizacionesOrden) {
	this.autorizacionesOrden = autorizacionesOrden;
    }

    public DataModel getListaOrden() {
	try {
	    return listaOrden;
	} catch (Exception e) {
	    return null;
	}

    }

    public String getDev() {
	return dev;
    }

    public void setDev(String dev) {
	this.dev = dev;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
	return modelDevolucionRequisicion.getMotivo();
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
	modelDevolucionRequisicion.setMotivo(motivo);
    }

    /**
     * @return the devPop
     */
    public boolean isDevPop() {
	return modelDevolucionRequisicion.isDevPop();
    }

    /**
     * @param devPop the devPop to set
     */
    public void setDevPop(boolean devPop) {
	modelDevolucionRequisicion.setDevPop(devPop);
    }

    /**
     * @return the idAnalista
     */
    public String getIdAnalista() {
	return modelDevolucionRequisicion.getIdAnalista();
    }

    /**
     * @param idAnalista the idAnalista to set
     */
    public void setIdAnalista(String idAnalista) {
	modelDevolucionRequisicion.setIdAnalista(idAnalista);
    }

    /**
     * @return the idBloque
     */
    public int getIdBloque() {
	return modelDevolucionRequisicion.getIdBloque();
    }

    /**
     * @param idBloque the idBloque to set
     */
    public void setIdBloque(int idBloque) {
	modelDevolucionRequisicion.setIdBloque(idBloque);
    }

    /**
     * @return the requisicionVO
     */
    public RequisicionVO getRequisicionVO() {
	return modelDevolucionRequisicion.getRequisicionVO();
    }

    /**
     * @param requisicionVO the requisicionVO to set
     */
    public void setRequisicionVO(RequisicionVO requisicionVO) {
	modelDevolucionRequisicion.setRequisicionVO(requisicionVO);
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
	return modelDevolucionRequisicion.getIdGerencia();
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
	modelDevolucionRequisicion.setIdGerencia(idGerencia);
    }

}
