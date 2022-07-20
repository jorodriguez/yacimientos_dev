/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.avisoPagoStaffOficina.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import sia.modelo.SgAvisoPago;
import sia.modelo.SgStaff;
import sia.sgl.avisoPagoStaffOficina.beanModel.AvisoPagoStaffBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez 2013
 * @modify mluis
 */
@Named(value = "avisoPagoStaffBean")
@RequestScoped
public class AvisoPagoStaffBean implements Serializable {

    @ManagedProperty(value = "#{avisoPagoStaffBeanModel}")
    private AvisoPagoStaffBeanModel avisoPagoStaffBeanModel;

    public AvisoPagoStaffBean() {
    }

    public void traerListaStaffItems(ActionEvent event) {
	UtilLog4j.log.info(this, "AvisoPagoBeanModel.traerListaStaffItems()");
	avisoPagoStaffBeanModel.traerStaffItems();
    }

    public void seleccionarAvisoCatalogo(ActionEvent event) {
	UtilLog4j.log.info(this, "AvisoPagoBeanModel.seleccionarAvisoCatalogo()");
	avisoPagoStaffBeanModel.setSgAvisoPago((SgAvisoPago) avisoPagoStaffBeanModel.getAvisosPagosCatalogoModel().getRowData());
	avisoPagoStaffBeanModel.setIdPeriodicidadSeleecionada((Integer) avisoPagoStaffBeanModel.getSgAvisoPago().getSgPeriodicidad().getId());
	avisoPagoStaffBeanModel.setIdPagoStaffSeleccionado((Integer) avisoPagoStaffBeanModel.getSgAvisoPago().getSgTipoEspecifico().getId());
	if (!avisoPagoStaffBeanModel.buscarPagoRepetido()) {
//        if (avisoPagoStaffBeanModel.buscarAvisoSeleccion()) {
//            UtilLog4j.log.info(this, "aviso ya existe..no se puede agregar");
//            FacesUtils.addInfoMessage("El aviso seleccionado ya se encuentra en la lista");
//        } else {
	    avisoPagoStaffBeanModel.createRelacionStaff();
	    //avisoPagoStaffBeanModel.editAvisoPago();
	    avisoPagoStaffBeanModel.traerAllAvisosToStaff();
	    avisoPagoStaffBeanModel.setMrPopupCatalogoAvisos(false);
//        }
	} else {
	    FacesUtils.addInfoMessage("El aviso seleccionado ya se encuentra en la lista");
	}
    }

    public void traerAvisosAction(ValueChangeEvent valueChangeEvent) {
	UtilLog4j.log.info(this, "AvisoPAgoStaff.traerAvisosAction");
	avisoPagoStaffBeanModel.setIdStaffSeleccionado((Integer) valueChangeEvent.getNewValue());
	avisoPagoStaffBeanModel.traerStaff();
	avisoPagoStaffBeanModel.traerAllAvisosToStaff();
    }
//    public void validadPagoAction(ValueChangeEvent valueChangeEvent){
//        avisoPagoStaffBeanModel.setIdPagoStaffSeleccionado((Integer) valueChangeEvent.getNewValue());
//        if(avisoPagoStaffBeanModel.buscarPagoRepetido())
//        {
//            FacesUtils.addInfoMessage("El tipo de pago ya esta en la lista");
//        }
//
//    }

    public void traerCatalogoAvisos() {
	UtilLog4j.log.info(this, "AvisoPAgoStaff.traerCatalogoAvisos");
	avisoPagoStaffBeanModel.traerCatalogoAvisos();
    }

    public void createAviso(ActionEvent event) {
	UtilLog4j.log.info(this, " CreateAviso ..");
	try {
	    if ((avisoPagoStaffBeanModel.getSgAvisoPago().getDiaAnticipadoPago() < 0 || avisoPagoStaffBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 30)
		    || (avisoPagoStaffBeanModel.getSgAvisoPago().getDiaEstimadoPago() < 0 || avisoPagoStaffBeanModel.getSgAvisoPago().getDiaEstimadoPago() > 30)) {
		FacesUtils.addInfoMessage("Los rangos de fechas son de 1 al 30");
	    } else {
		if (avisoPagoStaffBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 0 && avisoPagoStaffBeanModel.getSgAvisoPago().getDiaEstimadoPago() > 0 && avisoPagoStaffBeanModel.getIdPeriodicidadSeleecionada() > 0) {
		    UtilLog4j.log.info(this, "Entrando a guardar");
		    //if (avisoPagoStaffBeanModel.getSgAvisoPago().getDiaEstimadoPago() > avisoPagoStaffBeanModel.getSgAvisoPago().getDiaAnticipadoPago()) {
		    if (avisoPagoStaffBeanModel.buscarPagoRepetido()) {
			UtilLog4j.log.info(this, " El aviso ya se encuentra en la lista");
			//avisoPagoStaffBeanModel.createRelacionStaff();
			FacesUtils.addInfoMessage("El aviso ya se encuentra en la lista");
		    } else {
			//avisoPagoStaffBeanModel.createAvisoPago();
			UtilLog4j.log.info(this, " el aviso no esta en la lista..se creara todo..");
			avisoPagoStaffBeanModel.crearAviso();
			avisoPagoStaffBeanModel.createRelacionStaff();
			//avisoPagoStaffBeanModel.createRelacionStaff();;
			avisoPagoStaffBeanModel.traerAllAvisosToStaff();
			avisoPagoStaffBeanModel.setMrPopupCatalogoAvisos(false);
		    }

		} else {
		    FacesUtils.addInfoMessage("Existen datos inconsistentes..");
		}
	    }

	} catch (Exception e) {
	    UtilLog4j.log.info(this, " Excepcion " + e.getMessage());
	    FacesUtils.addInfoMessage("Se requieren todos los datos..");

	}
    }

    public void createRelacion(ActionEvent event) {
	if (avisoPagoStaffBeanModel.getSgAvisoPago() != null) {
	    avisoPagoStaffBeanModel.createRelacionStaff();
	}
    }

    public void editAviso(ActionEvent event) {
	UtilLog4j.log.info(this, " editAviso ..");
	try {
	    if ((avisoPagoStaffBeanModel.getSgAvisoPago().getDiaAnticipadoPago() < 0 || avisoPagoStaffBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 30)
		    || (avisoPagoStaffBeanModel.getSgAvisoPago().getDiaEstimadoPago() < 0 || avisoPagoStaffBeanModel.getSgAvisoPago().getDiaEstimadoPago() > 30)) {
		FacesUtils.addInfoMessage("Los rangos de fechas son de 1 al 30");
	    } else {
		if (avisoPagoStaffBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 0 && avisoPagoStaffBeanModel.getSgAvisoPago().getDiaEstimadoPago() > 0 && avisoPagoStaffBeanModel.getIdPeriodicidadSeleecionada() > 0) {
		    //if (avisoPagoStaffBeanModel){
		    //   FacesUtils.addInfoMessage("No se puede modificar, esta repetido el aviso");
		    // }else{
		    avisoPagoStaffBeanModel.editAvisoPago();
		    avisoPagoStaffBeanModel.setMrPopupModificarAviso(false);
		    //}

		} else {
		    FacesUtils.addInfoMessage("Se requieren todos los datos..");
		}
	    }

	} catch (Exception e) {
	    UtilLog4j.log.info(this, " Excepción al modificar el Aviso..");
	}
    }

    public void deleteRelacionAvisoPago(ActionEvent event) {
	UtilLog4j.log.info(this, " deleteAviso..");
	try {
	    avisoPagoStaffBeanModel.setSgAvisoPago((SgAvisoPago) avisoPagoStaffBeanModel.getAvisosPagosStaffModel().getRowData());
	    avisoPagoStaffBeanModel.deteleRelacionAvisoPago();
	} catch (Exception e) {
	    UtilLog4j.log.info(this, " Excepcion al eliminar la relacion el Aviso..");
	}
    }

    public DataModel getAvisosPagosStaffModel() {
	return avisoPagoStaffBeanModel.getAvisosPagosStaffModel();
    }

    public DataModel getCatalogoAvisosModel() {
	return avisoPagoStaffBeanModel.getAvisosPagosCatalogoModel();
    }

    public List<SelectItem> getListaStaffItems() {
	return avisoPagoStaffBeanModel.getListaStaffItems();
    }

    public List<SelectItem> getListaPagosStaffItems() {
	return avisoPagoStaffBeanModel.getListaPagosStaffItems();
    }

    public List<SelectItem> getListaPeriodosPagoItems() {
	return avisoPagoStaffBeanModel.getListaPeriodosPagoItems();
    }

    public SgStaff getStaffSeleccionado() {
	return avisoPagoStaffBeanModel.getSgStaffSeleccionado();
    }

    public int getPeriodicidadSeleccionada() {
	return avisoPagoStaffBeanModel.getIdPeriodicidadSeleecionada();
    }

    public SgAvisoPago getAvisoPago() {
	return avisoPagoStaffBeanModel.getSgAvisoPago();
    }

    public Date fechaActual() {
	return avisoPagoStaffBeanModel.getFechaActual();
    }

    public Date fechaAviso() {
	return avisoPagoStaffBeanModel.getFechaAviso();
    }

    public Date fechaProximoPago() {
	return avisoPagoStaffBeanModel.getFechaPago();
    }

    public String mensajeError() {
	return avisoPagoStaffBeanModel.getMensajeError();
    }

    /*
     * Metodos de popups
     */
    public boolean getMrPopupAgregarAviso() {
	return avisoPagoStaffBeanModel.isMrMostrarPanelAgregarNuevoAviso();
    }

    public boolean getMrPopupModificarAviso() {
	return avisoPagoStaffBeanModel.isMrPopupModificarAviso();
    }

    public boolean getMrMostrarPanelCatalogo() {
	return avisoPagoStaffBeanModel.isMrMostrarPanelCatalogo();
    }

    public boolean getMrPopupCatalogoAviso() {
	return avisoPagoStaffBeanModel.isMrPopupCatalogoAvisos();
    }

    public void mostrarPanelAgregarAviso(ActionEvent event) {
	avisoPagoStaffBeanModel.traerPagosPorTipoEspecificoToStaffItems();
	avisoPagoStaffBeanModel.traerPeriodosItems();
	avisoPagoStaffBeanModel.setSgAvisoPago(new SgAvisoPago());
	avisoPagoStaffBeanModel.setMrMostrarPanelAgregarNuevoAviso(true);
	avisoPagoStaffBeanModel.setMrMostrarPanelCatalogo(false);
    }

    public void mostrarPopupAgregarAvisoDesdeCatalogo(ActionEvent event) {
	avisoPagoStaffBeanModel.setMrPopupCatalogoAvisos(true);
	avisoPagoStaffBeanModel.setMrMostrarPanelAgregarNuevoAviso(false);
	avisoPagoStaffBeanModel.setMrMostrarPanelCatalogo(true);
    }

    public void ocultarPopupAgregarAviso(ActionEvent event) {
	avisoPagoStaffBeanModel.setMrMostrarPanelAgregarNuevoAviso(false);
    }

    public void mostrarPopupCatalogoAviso(ActionEvent event) {
	if (avisoPagoStaffBeanModel.getIdStaffSeleccionado() != -1) {
	    avisoPagoStaffBeanModel.setMensajeError("");
	    avisoPagoStaffBeanModel.traerCatalogoAvisos();
	    avisoPagoStaffBeanModel.setSgAvisoPago(new SgAvisoPago());
	    avisoPagoStaffBeanModel.setMrPopupCatalogoAvisos(true);
	    avisoPagoStaffBeanModel.setMrMostrarPanelCatalogo(true);
	    avisoPagoStaffBeanModel.setMrMostrarPanelAgregarNuevoAviso(false);
	} else {
	    // FacesUtils.addInfoMessage("Debe seleccionar un Staff House");
	    avisoPagoStaffBeanModel.setMensajeError("· Debe seleccionar un Staff House");
	}

    }

    public void ocultarPopupCatalogoAviso(ActionEvent event) {
	avisoPagoStaffBeanModel.setMrPopupCatalogoAvisos(false);
	avisoPagoStaffBeanModel.setMrMostrarPanelAgregarNuevoAviso(false);
    }

    public void mostrarPopupModificarAviso(ActionEvent event) {
	avisoPagoStaffBeanModel.traerCatalogoAvisos();
	avisoPagoStaffBeanModel.traerPagosPorTipoEspecificoToStaffItems();
	avisoPagoStaffBeanModel.traerPeriodosItems();
	//isoPagoStaffBeanModel.setSgAvisoPago(new SgAvisoPago());
	UtilLog4j.log.info(this, "MostrarPopupModificarAviso");
	UtilLog4j.log.info(this, "tomar el registro seleccionado");
	avisoPagoStaffBeanModel.setSgAvisoPago((SgAvisoPago) avisoPagoStaffBeanModel.getAvisosPagosStaffModel().getRowData());
	UtilLog4j.log.info(this, "el idpagoStaffSeleccionado");
	avisoPagoStaffBeanModel.setIdPagoStaffSeleccionado(avisoPagoStaffBeanModel.getSgAvisoPago().getSgTipoEspecifico().getId());
	UtilLog4j.log.info(this, "el idPeriodicidad");
	avisoPagoStaffBeanModel.setIdPeriodicidadSeleecionada(avisoPagoStaffBeanModel.getSgAvisoPago().getSgPeriodicidad().getId());
	UtilLog4j.log.info(this, "el mostrar panel");
//        avisoPagoStaffBeanModel.setid
	//avisoPagoStaffBeanModel.setMrPopupCatalogoAvisos(true);
	//avisoPagoStaffBeanModel.setMrMostrarPanelCatalogo(true);
	avisoPagoStaffBeanModel.setMrPopupModificarAviso(true);
    }

    public void ocultarPopupModificarAviso(ActionEvent event) {
	avisoPagoStaffBeanModel.setMrPopupModificarAviso(false);
    }

    /**
     * @param avisoPagoStaffBeanModel the avisoPagoStaffBeanModel to set
     */
    public void setAvisoPagoStaffBeanModel(AvisoPagoStaffBeanModel avisoPagoStaffBeanModel) {
	this.avisoPagoStaffBeanModel = avisoPagoStaffBeanModel;
    }
}
