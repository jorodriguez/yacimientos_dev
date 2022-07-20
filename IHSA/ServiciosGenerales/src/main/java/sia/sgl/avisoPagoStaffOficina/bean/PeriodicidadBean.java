/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.avisoPagoStaffOficina.bean;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import sia.modelo.SgPeriodicidad;
import sia.sgl.avisoPagoStaffOficina.beanModel.PeriodicidadBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

@Named(value = "periodicidadBean")
@RequestScoped
public class PeriodicidadBean implements Serializable {

    @ManagedProperty(value = "#{periodicidadBeanModel}")
    private PeriodicidadBeanModel periodicidadBeanModel;

    public PeriodicidadBean() {
    }

    public void createPerdiodicidad(ActionEvent event) {
	try {
	    if (!(periodicidadBeanModel.getSgPeriodicidad().getMes()).toString().equals("") && !periodicidadBeanModel.getSgPeriodicidad().getNombre().equals("")) {
		if (!periodicidadBeanModel.buscarNombreRepetido()) {
		    this.periodicidadBeanModel.createPeriodicidad();
		    periodicidadBeanModel.traerAllPeriodicidad();
		    periodicidadBeanModel.setPopupAgregarPeriodicidad(false);
		} else {
		    FacesUtils.addInfoMessage("Ya existe un nombre con esa especificación");
		    periodicidadBeanModel.setMensaje("Ya existe un nombre con esa especificación");
		}
	    } else {
		FacesUtils.addInfoMessage("Por favor especifique todos los campos");
		periodicidadBeanModel.setMensaje("Por favor especifique todos los campos");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en crear Periodicidad" + e.getMessage());
	    FacesUtils.addInfoMessage("Por favor especifique todos los campos");
	}
    }

    public void editPerdiodicidad(ActionEvent event) {
	try {
	    if (!(periodicidadBeanModel.getSgPeriodicidad().getMes()).toString().equals("") && !periodicidadBeanModel.getSgPeriodicidad().getNombre().equals("")) {
		if (!periodicidadBeanModel.buscarNombreRepetido()) {
		    this.periodicidadBeanModel.editPeriodicidad();
		    periodicidadBeanModel.setPopupModificarPeriodicidad(false);
		} else {
		    FacesUtils.addInfoMessage("Ya existe un nombre con esa especificación");
		    // periodicidadBeanModel.setMensaje("Ya existe un nombre con esa especificación");
		}
	    } else {
		FacesUtils.addInfoMessage("Por favor especifique todos los campos");
	    }
	    periodicidadBeanModel.traerAllPeriodicidad();
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en crear Periodicidad" + e.getMessage());
	    FacesUtils.addInfoMessage("Por favor especifique todos los campos");
	}
    }

    public void deletePeriodicidad(ActionEvent event) {
	//Falta controlar que si esta siendo utilizado en avisos no se pueda eliminar
	try {
	    periodicidadBeanModel.setSgPeriodicidad((SgPeriodicidad) periodicidadBeanModel.getListaPeriodicidad().getRowData());
	    if (!periodicidadBeanModel.buscarRepetidoEnAvisos()) {
		this.periodicidadBeanModel.deletePerdiodicidad();
	    } else {
		periodicidadBeanModel.setPopupAviso(true);
		//FacesUtils.addInfoMessage("No se puede eliminar el registro esta siendo utilizado por otro proceso");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en eliminar Periodicidad " + e.getMessage());
	}
    }

    /**
     * @return the listaPeriodicidad
     */
    public DataModel getListaPeriodicidad() {
	return periodicidadBeanModel.getListaPeriodicidad();
    }

    /**
     * @param listaPeriodicidad the listaPeriodicidad to set
     */
    public void setListaPeriodicidad(DataModel listaPeriodicidad) {
	periodicidadBeanModel.setListaPeriodicidad(listaPeriodicidad);
    }

    public String getMensaje() {
	return periodicidadBeanModel.getMensaje();
    }

    public SgPeriodicidad getPeriodicidad() {
	return periodicidadBeanModel.getSgPeriodicidad();
    }

    public void agregarPeriodicidad(ActionEvent event) {
	periodicidadBeanModel.setPopupAgregarPeriodicidad(true);
    }

    public void ocultarPopupAgregarPeriodicidad(ActionEvent event) {
	periodicidadBeanModel.setPopupAgregarPeriodicidad(false);
    }

    public boolean isPopupModificarPeriodicidad() {
	return periodicidadBeanModel.isPopupModificarPeriodicidad();
    }

    /**
     * @param popupModificarPeriodicidad
     */
    public void setPopupModificarPeriodicidad(boolean popupModificarPeriodicidad) {
	periodicidadBeanModel.setPopupModificarPeriodicidad(popupModificarPeriodicidad);
    }

    public void mostrarPopupModificarPeriodicidad(ActionEvent event) {
	periodicidadBeanModel.setSgPeriodicidad((SgPeriodicidad) periodicidadBeanModel.getListaPeriodicidad().getRowData());
	periodicidadBeanModel.setPopupModificarPeriodicidad(true);
    }

    public void ocultarPopupModificarPeriodicidad(ActionEvent event) {
	periodicidadBeanModel.setPopupModificarPeriodicidad(false);
    }

    /**
     * @param periodicidadBeanModel the periodicidadBeanModel to set
     */
    public void setPeriodicidadModel(PeriodicidadBeanModel periodicidadBeanModel) {
	this.setPeriodicidadBeanModel(periodicidadBeanModel);
    }

    /**
     * @return the popupAgregarPeriodicidad
     */
    public boolean isPopupAgregarPeriodicidad() {
	return periodicidadBeanModel.isPopupAgregarPeriodicidad();
    }

    /**
     * @param popupAgregarPeriodicidad the popupAgregarPeriodicidad to set
     */
    public void setPopupAgregarPeriodicidad(boolean popupAgregarPeriodicidad) {
	periodicidadBeanModel.setPopupAgregarPeriodicidad(popupAgregarPeriodicidad);
    }

    public void ocultarPopupAviso(ActionEvent event) {
	periodicidadBeanModel.setPopupAviso(false);
    }

    /**
     * @return the popupAviso
     */
    public boolean isPopupAviso() {
	return periodicidadBeanModel.isPopupAviso();
    }

    /**
     * @param popupAviso the popupAviso to set
     */
    public void setPopupAviso(boolean popupAviso) {
	periodicidadBeanModel.setPopupAviso(popupAviso);
    }

    /**
     * @param periodicidadBeanModel the periodicidadBeanModel to set
     */
    public void setPeriodicidadBeanModel(PeriodicidadBeanModel periodicidadBeanModel) {
	this.periodicidadBeanModel = periodicidadBeanModel;
    }
}
