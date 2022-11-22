/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.avisoPagoStaffOficina.beanModel;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import sia.modelo.SgPeriodicidad;
import sia.servicios.sgl.impl.SgPeriodicidadImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 */
@Named(value = "periodicidadBeanModel")

public class PeriodicidadBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    //
    @Inject
    private SgPeriodicidadImpl sgPeriodicidadService;
    //
    private boolean popupAgregarPeriodicidad = false;
    private boolean popupModificarPeriodicidad = false;
    private boolean popupAviso = false;
    private String mensaje = "";
    private SgPeriodicidad sgPeriodicidad;
    private DataModel listaPeriodicidad;

    //private String nombrePeriodicidad;
    public PeriodicidadBeanModel() {
    }

    @PostConstruct
    public void iniciar() {
	sgPeriodicidad = new SgPeriodicidad();
	listaPeriodicidad = new ArrayDataModel();
	traerAllPeriodicidad();
    }

    public void traerAllPeriodicidad() {
	UtilLog4j.log.info(this, "AvisoPagosStaffBeanModel");
	try {
	    setListaPeriodicidad(new ListDataModel(sgPeriodicidadService.findAllPeriodos()));
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public boolean buscarNombreRepetido() {
	UtilLog4j.log.info(this, "buscar nombre repetido " + getSgPeriodicidad().getNombre());
	boolean ret = false;
	try {
	    ret = sgPeriodicidadService.findNombreRepetido(getSgPeriodicidad().getNombre());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "excepcion en la busqueda de nombre repetido " + e.getMessage());
	}
	return ret;
    }

    /*
     * Buscar en avisos pagos para saber si el id de la periodicidad esta
     * relacionada con un aviso (para controlar la eliminacion de la
     * periodicidad)
     */
    public boolean buscarRepetidoEnAvisos() {
	UtilLog4j.log.info(this, "buscarRepetidosEnAviso");
	boolean ret = false;
	try {
	    ret = sgPeriodicidadService.findRepetidoEnAvisosPagos(getSgPeriodicidad().getId());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "excepcion en la busqueda de repetidos " + e.getMessage());
	}
	return ret;
    }

    public void createPeriodicidad() {
	UtilLog4j.log.info(this, "periodicidadBeanModel.createPeriodicidad()");
	try {
	    UtilLog4j.log.info(this, "Listo para agregar");
	    if (getSgPeriodicidad() != null) {
		sgPeriodicidadService.createPeriodicidad(getSgPeriodicidad(), sesion.getUsuario());
		//traerAllPeriodicidad();
		UtilLog4j.log.info(this, "periodicidad creada");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public void editPeriodicidad() {
	UtilLog4j.log.info(this, "periodicidadBeanModel.editPeriodicidad()");
	try {
	    UtilLog4j.log.info(this, "Listo para modificar");
	    if (getSgPeriodicidad() != null) {
		sgPeriodicidadService.editPeriodicidad(getSgPeriodicidad(), sesion.getUsuario());
		UtilLog4j.log.info(this, "periodicidad modificada");
		//  traerAllPeriodicidad();
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public void deletePerdiodicidad() {
	UtilLog4j.log.info(this, "periodicidad.deletePeriodicidad()");
	try {
	    this.sgPeriodicidadService.deletePeriodicidad(getSgPeriodicidad().getId(), sesion.getUsuario());
	    traerAllPeriodicidad();
	    UtilLog4j.log.info(this, "Periodicidad Eliminada");
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    /**
     * @return the sgPeriodicidad
     */
    public SgPeriodicidad getSgPeriodicidad() {
	return sgPeriodicidad;
    }

    /**
     * @param sgPeriodicidad the sgPeriodicidad to set
     */
    public void setSgPeriodicidad(SgPeriodicidad sgPeriodicidad) {
	this.sgPeriodicidad = sgPeriodicidad;
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

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the popupAgregarPeriodicidad
     */
    public boolean isPopupAgregarPeriodicidad() {
	return popupAgregarPeriodicidad;
    }

    /**
     * @param popupAgregarPeriodicidad the popupAgregarPeriodicidad to set
     */
    public void setPopupAgregarPeriodicidad(boolean popupAgregarPeriodicidad) {
	this.popupAgregarPeriodicidad = popupAgregarPeriodicidad;
    }

    /**
     * @return the popupModificarPeriodicidad
     */
    public boolean isPopupModificarPeriodicidad() {
	return popupModificarPeriodicidad;
    }

    /**
     * @param popupModificarPeriodicidad the popupModificarPeriodicidad to set
     */
    public void setPopupModificarPeriodicidad(boolean popupModificarPeriodicidad) {
	this.popupModificarPeriodicidad = popupModificarPeriodicidad;
    }

    /**
     * @return the popupAviso
     */
    public boolean isPopupAviso() {
	return popupAviso;
    }

    /**
     * @param popupAviso the popupAviso to set
     */
    public void setPopupAviso(boolean popupAviso) {
	this.popupAviso = popupAviso;
    }

    /**
     * @return the listaPeriodicidad
     */
    public DataModel getListaPeriodicidad() {
	return listaPeriodicidad;
    }

    /**
     * @param listaPeriodicidad the listaPeriodicidad to set
     */
    public void setListaPeriodicidad(DataModel listaPeriodicidad) {
	this.listaPeriodicidad = listaPeriodicidad;
    }
}
