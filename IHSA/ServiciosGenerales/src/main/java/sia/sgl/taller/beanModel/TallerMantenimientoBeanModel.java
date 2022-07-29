/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.taller.beanModel;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import sia.modelo.Proveedor;
import sia.modelo.SgTallerMantenimiento;
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
@Named(value = "tallerBeanModel")
@ViewScoped
public class TallerMantenimientoBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    //
    @Inject
    private SgAseguradoraImpl aseguradoraService;
//    Servicios
    @Inject
    ProveedorServicioImpl proveedorService;
    @Inject
    SgTallerMantenimientoImpl tallerService;
    @Inject
    SgVehiculoMantenimientoImpl sgMantenimientoService;
    // Entidades
    private SgTallerMantenimiento tallerSeleccionado;
    private Proveedor proveedorSeleccionado;
    private boolean mostrarConfirmacion;
    // variables
    private int idProveedorSeleccioando;
    private String nombreProveedor;
    //listas SelectItems
    private List<SelectItem> listaProveedor;
    private List<String> listaProveedorBuscar;
    private DataModel talleresModel;

    public TallerMantenimientoBeanModel() {
    }

    @PostConstruct
    public void iniciar() {
	setListaProveedorBuscar(traerProveedor());
	traerTalles();
    }

    public void traerTalles() {
	log("traerAllAvisosToOficina");
	try {
	    this.setTalleresModel(new ListDataModel(tallerService.findAllTalleres(sesion.getOficinaActual().getId())));
	} catch (Exception e) {
	    log(e.getMessage());
	}
    }

    public Proveedor traerProveedorPorNombre(String nombre) {
	UtilLog4j.log.info(this, "Traer proveedor por nombre");
	setProveedorSeleccionado(proveedorService.getPorNombre(nombreProveedor, sesion.getRfcEmpresa()));
	UtilLog4j.log.info(this, "el proveedor es " + getProveedorSeleccionado().getNombre());
	return getProveedorSeleccionado();

    }

    public boolean createTaller() {
	log("createTaller");
	boolean ret = false;
	try {
	    if (getNombreProveedor() != null && !getNombreProveedor().equals("")) {
		traerProveedorPorNombre(getNombreProveedor());

		tallerService.createTaller(getProveedorSeleccionado().getId(), sesion.getOficinaActual().getId(), sesion.getUsuario().getId());
		setNombreProveedor("");
		traerTalles();
		ret = true;
	    }
	    return ret;
	} catch (Exception e) {
	    log("Exception al crear un taller de mantenimiento " + e.getMessage());
	    return false;

	}
    }

    public void deleteTalleres() {
	if (getTallerSeleccionado() != null) {
	    tallerService.deleteTaller(getTallerSeleccionado(), sesion.getUsuario());
	    traerTalles();
	}
    }

    public boolean buscarProveedorEnMantenimiento() {
	UtilLog4j.log.info(this, "buscarProveedorEnMantenimiento()");
	if (getTallerSeleccionado() != null) {
	    return sgMantenimientoService.findProveedorEnMantenimiento(getTallerSeleccionado().getProveedor());
	} else {
	    UtilLog4j.log.info(this, "no se encontro");
	    return false;
	}
    }

    public boolean buscarProveedorRepetido() {
	try {
	    UtilLog4j.log.info(this, "buscarProveedorRepetido()");
	    if (getNombreProveedor() != null && !getNombreProveedor().equals("")) {
		UtilLog4j.log.info(this, "preparado para buscar talleres repetidos");
		return tallerService.findTaller(getNombreProveedor(), sesion.getOficinaActual().getId());
	    } else {
		UtilLog4j.log.info(this, "no se encontro");
		return false;
	    }
	} catch (Exception e) {
	    log("Excepcion al buscar un proveedor repetido " + e.getMessage());
	    return false;
	}
    }

    public boolean encontrarProveedorEnAseguradora() {
	UtilLog4j.log.info(this, "encontrarProveedorEnAseguradora");
	return aseguradoraService.findAseguradora(getNombreProveedor());
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
     * @return the talleresModel
     */
    public DataModel getTalleresModel() {
	return talleresModel;
    }

    /**
     * @param talleresModel the talleresModel to set
     */
    public void setTalleresModel(DataModel talleresModel) {
	this.talleresModel = talleresModel;
    }

    /**
     * @return the tallerSeleccionado
     */
    public SgTallerMantenimiento getTallerSeleccionado() {
	return tallerSeleccionado;
    }

    /**
     * @param tallerSeleccionado the tallerSeleccionado to set
     */
    public void setTallerSeleccionado(SgTallerMantenimiento tallerSeleccionado) {
	this.tallerSeleccionado = tallerSeleccionado;
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
	return proveedorService.traerNombreProveedorQueryNativo(sesion.getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());
    }

    private void log(String mensaje) {
	UtilLog4j.log.info(this, mensaje);
	//log(mensaje);
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }
}
