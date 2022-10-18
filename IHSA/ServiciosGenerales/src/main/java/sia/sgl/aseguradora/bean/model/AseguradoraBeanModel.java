/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.aseguradora.bean.model;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import sia.modelo.Proveedor;
import sia.modelo.SgAseguradora;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgAseguradoraImpl;
import sia.servicios.sgl.impl.SgTallerMantenimientoImpl;
import sia.servicios.sgl.impl.SgVehiculoMantenimientoImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "aseguradoraBeanModel")
@ViewScoped
public class AseguradoraBeanModel implements Serializable {

    @Inject
    private Sesion sesion;

//    Servicios
    @Inject
    SgAseguradoraImpl aseguradoraService;
    @Inject
    SgTallerMantenimientoImpl tallerService;
    @Inject
    SgVehiculoMantenimientoImpl sgMantenimientoService;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    // Entidades
    private SgAseguradora aseguradoraSeleccionada;
    private Proveedor proveedorSeleccionado;
    // variables
    private int idProveedorSeleccioando;
    private String nombreProveedor;
    private boolean mostrarConfirmacion;
    //listas SelectItems
    private List<SelectItem> listaProveedor;
    private List<String> listaProveedorBuscar;
    private DataModel aseguradorasModel;

    public AseguradoraBeanModel() {
    }

    @PostConstruct
    public void iniciar() {
	setListaProveedorBuscar(traerProveedor());
	traerAseguradoras();
    }

    public void traerAseguradoras() {
	UtilLog4j.log.info(this, "traerAllAvisosToOficina");
	try {
	    ListDataModel<SgAseguradora> avisosModel = new ListDataModel(aseguradoraService.findAllAseguradoras());
	    this.setAseguradorasModel(avisosModel);
	    UtilLog4j.log.info(this, "Datamodel de aseguradoras asignado " + avisosModel.getRowCount());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    public Proveedor traerProveedorPorNombre(String nombre) {
	UtilLog4j.log.info(this, "Traer proveedor por nombre");
	setProveedorSeleccionado(proveedorImpl.getPorNombre(nombreProveedor, sesion.getRfcEmpresa()));
	UtilLog4j.log.info(this, "el proveedor es " + getProveedorSeleccionado().getNombre());
	return getProveedorSeleccionado();
    }

    public void createAseguradora() {
	if (getNombreProveedor() != null && !getNombreProveedor().equals("")) {
	    traerProveedorPorNombre(getNombreProveedor());
	    aseguradoraService.createAseguradora(getProveedorSeleccionado(), sesion.getUsuario());
	    setNombreProveedor("");
	    traerAseguradoras();
	}
    }

    public void deleteAseguradora() {
	if (getAseguradoraSeleccionada() != null) {
	    aseguradoraService.deleteAseguradora(aseguradoraSeleccionada, sesion.getUsuario());
	    traerAseguradoras();
	}
    }

    public boolean buscarProveedorRepetido() {
	UtilLog4j.log.info(this, "buscarProveedorRepetido()");
        
        boolean retVal = false;
        
	if (Strings.isNullOrEmpty(getNombreProveedor())) {
	    UtilLog4j.log.info(this, "no se encontro " + getNombreProveedor());
	} else {
            retVal = aseguradoraService.findAseguradora(getNombreProveedor());
	}
        
        return retVal;
    }

    public boolean buscarProveedorEnMantenimiento() {
	UtilLog4j.log.info(this, "buscarProveedorEnMantenimiento()");
        
        boolean retVal = false;
        
	if (getAseguradoraSeleccionada() == null) {
	    UtilLog4j.log.info(this, "no se encontro " + getNombreProveedor());
	} else {
            retVal = sgMantenimientoService.findProveedorEnMantenimiento(getAseguradoraSeleccionada().getProveedor());
	}
        
        return retVal;
    }

    public boolean encontrarProveedorEnTalleres() {
	UtilLog4j.log.info(this, "encontrarProveedorEnTaller");
	return tallerService.findTaller(getNombreProveedor(), sesion.getOficinaActual().getId());
    }

    /**
     * @return the idProveedorSeleccioando
     */
    public int getIdProveedorSeleccioando() {
	return idProveedorSeleccioando;
    }

    /**
     * @param idProveedorSeleccioando the idProveedorSeleccioando to set
     */
    public void setIdProveedorSeleccioando(int idProveedorSeleccioando) {
	this.idProveedorSeleccioando = idProveedorSeleccioando;
    }

    /**
     * @return the proveedorSeleccionado
     */
    public Proveedor getProveedorSeleccionado() {
	return proveedorSeleccionado;
    }

    /**
     * @param proveedorSeleccionado the proveedorSeleccionado to set
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
	this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * @return the nombreProveedor
     */
    public String getNombreProveedor() {
	return nombreProveedor;
    }

    /**
     * @param nombreProveedor the nombreProveedor to set
     */
    public void setNombreProveedor(String nombreProveedor) {
	this.nombreProveedor = nombreProveedor;
    }

    /**
     * @return the listaProveedor
     */
    public List<SelectItem> getListaProveedor() {
	return listaProveedor;
    }

    /**
     * @param listaProveedor the listaProveedor to set
     */
    public void setListaProveedor(List<SelectItem> listaProveedor) {
	this.listaProveedor = listaProveedor;
    }

    /**
     * @return the aseguradorasModel
     */
    public DataModel getAseguradorasModel() {
	return aseguradorasModel;
    }

    /**
     * @param aseguradorasModel the aseguradorasModel to set
     */
    public void setAseguradorasModel(DataModel aseguradorasModel) {
	this.aseguradorasModel = aseguradorasModel;
    }

    /**
     * @return the aseguradoraSeleccionada
     */
    public SgAseguradora getAseguradoraSeleccionada() {
	return aseguradoraSeleccionada;
    }

    /**
     * @param aseguradoraSeleccionada the aseguradoraSeleccionada to set
     */
    public void setAseguradoraSeleccionada(SgAseguradora aseguradoraSeleccionada) {
	this.aseguradoraSeleccionada = aseguradoraSeleccionada;
    }

    /**
     * @return the mostrarConfirmacion
     */
    public boolean isMostrarConfirmacion() {
	return mostrarConfirmacion;
    }

    /**
     * @param mostrarConfirmacion the mostrarConfirmacion to set
     */
    public void setMostrarConfirmacion(boolean mostrarConfirmacion) {
	this.mostrarConfirmacion = mostrarConfirmacion;
    }

    /**
     * @return the listaProveedorBuscar
     */
    public List<String> getListaProveedorBuscar() {
	return listaProveedorBuscar;
    }

    /**
     * @param listaProveedorBuscar the listaProveedorBuscar to set
     */
    public void setListaProveedorBuscar(List<String> listaProveedorBuscar) {
	this.listaProveedorBuscar = listaProveedorBuscar;
    }

    public List<String> traerProveedor() {
	return proveedorImpl.traerNombreProveedorQueryNativo(sesion.getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

}
