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
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import sia.modelo.SgAvisoPago;
import sia.modelo.SgOficina;
import sia.sgl.avisoPagoStaffOficina.beanModel.AvisoPagoOficinaBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "avisoPagoOficinaBean")
@javax.faces.bean.RequestScoped
public class AvisoPagoOficinaBean implements Serializable {

    @ManagedProperty(value = "#{avisoPagoOficinaBeanModel}")
    private AvisoPagoOficinaBeanModel avisoPagoOficinaBeanModel;

    /**
     * Creates a new instance of AvisoPagoOficinaBean
     */
    public AvisoPagoOficinaBean() {
    }

    public void traerListaOficinaItems(ActionEvent event) {
	UtilLog4j.log.info(this, "AvisoPagoBeanModel.traerListaOficinaItems()");
	avisoPagoOficinaBeanModel.traerOficinaItems();
    }

    public void seleccionarAvisoCatalogo(ActionEvent event) {
	UtilLog4j.log.info(this, "AvisoPagoBeanModel.seleccionarAvisoCatalogo()");
	avisoPagoOficinaBeanModel.setSgAvisoPago((SgAvisoPago) avisoPagoOficinaBeanModel.getAvisosPagosCatalogoModel().getRowData());
	avisoPagoOficinaBeanModel.setIdPeriodicidadSeleccionada(avisoPagoOficinaBeanModel.getSgAvisoPago().getSgPeriodicidad().getId());
	avisoPagoOficinaBeanModel.setIdPagoOficinaSeleccionado(avisoPagoOficinaBeanModel.getSgAvisoPago().getSgTipoEspecifico().getId());

	if (!avisoPagoOficinaBeanModel.buscarPagoRepetido()) {
	    //buscar que no este el aviso repetido
	    // if (!avisoPagoOficinaBeanModel.createAvisoBuscandoEnEliminados()) {// buscar en los eliminados y reestablecer si lo encuentra
	    avisoPagoOficinaBeanModel.createRelacionOficina();
	    avisoPagoOficinaBeanModel.traerAllAvisosToOficina();
	    avisoPagoOficinaBeanModel.setMrPopupCatalogoAvisos(false);
	    // }
	} else {
	    FacesUtils.addInfoMessage("El aviso seleccionado ya se encuentra en la lista");
	}

    }

    public void traerAvisosAction(ValueChangeEvent valueChangeEvent) {
	UtilLog4j.log.info(this, "AvisoPAgoOficina.traerAvisosAction");
	//avisoPagoOficinaBeanModel.setIdOficinaSeleccionada((Integer) valueChangeEvent.getNewValue());
	avisoPagoOficinaBeanModel.traerOficinas();
	avisoPagoOficinaBeanModel.traerAllAvisosToOficina();
    }

    public void traerCatalogoAvisos() {
	UtilLog4j.log.info(this, "AvisoPagoOficina.traerCatalogoAvisos");
	avisoPagoOficinaBeanModel.traerCatalogoAvisos();
    }

    public void createAviso(ActionEvent event) {
	UtilLog4j.log.info(this, " CreateAviso ..");
	SgAvisoPago aviso = null;
	try {
	    if ((avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaAnticipadoPago() < 0 || avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 30)
		    || (avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaEstimadoPago() < 0 || avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaEstimadoPago() > 30)) {
		FacesUtils.addInfoMessage("Los rangos de fechas son de 1 al 30");
	    } else {

		if (avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 0 && avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaEstimadoPago() > 0 && avisoPagoOficinaBeanModel.getIdPeriodicidadSeleccionada() > 0) {
		    // if (!avisoPagoOficinaBeanModel.createAvisoBuscandoEnEliminados()) {
		    if (avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 30) {
			FacesUtils.addInfoMessage("El dia de pago debe de estar entre 1 y 30 dias");
		    } else {
			if (avisoPagoOficinaBeanModel.buscarPagoRepetido()) {
			    UtilLog4j.log.info(this, " El aviso ya se encuentra en la lista");
			    FacesUtils.addInfoMessage("El aviso ya se encuentra en la lista");
			} else {
			    //avisoPagoStaffBeanModel.createAvisoPago();
			    //   if (!avisoPagoOficinaBeanModel.buscarPagoRepetidoAtributos()) {
			    UtilLog4j.log.info(this, " el aviso no esta en la lista..se creara todo..");
			    //avisoPagoOficinaBeanModel.createOficinaAndRelacion();
			    avisoPagoOficinaBeanModel.crearAviso();
			    avisoPagoOficinaBeanModel.createRelacionOficina();
			    avisoPagoOficinaBeanModel.traerAllAvisosToOficina();
			    avisoPagoOficinaBeanModel.setMrPopupCatalogoAvisos(false);
			    // } else {
			    //   FacesUtils.addInfoMessage("El aviso ya se encuentra en la lista");
			    //}
			}
		    }

		} else {
		    FacesUtils.addInfoMessage("Se requieren todos los datos..");
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, " Excepcion " + e.getMessage());
	    FacesUtils.addInfoMessage("Se requieren todos los datos..");

	}
    }

    public void createRelacion(ActionEvent event) {
	if (avisoPagoOficinaBeanModel.getSgAvisoPago() != null) {
	    avisoPagoOficinaBeanModel.createRelacionOficina();
	}
    }

    public void editAviso(ActionEvent event) {
	UtilLog4j.log.info(this, " editAviso ..");
	try {
	    if ((avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaAnticipadoPago() < 0 || avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 30)
		    || (avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaEstimadoPago() < 0 || avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaEstimadoPago() > 30)) {
		FacesUtils.addInfoMessage("Los rangos de fechas son de 1 al 30");
	    } else {
		if (avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaAnticipadoPago() > 0 && avisoPagoOficinaBeanModel.getSgAvisoPago().getDiaEstimadoPago() > 0 && avisoPagoOficinaBeanModel.getIdPeriodicidadSeleccionada() > 0) {
		    avisoPagoOficinaBeanModel.editAvisoPago();
		    avisoPagoOficinaBeanModel.setMrPopupModificarAviso(false);
		} else {
		    FacesUtils.addInfoMessage("Se requieren todos los datos..");
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, " Excepcion al modificar el Aviso..");
	}
    }

    public void deleteRelacionAvisoPago(ActionEvent event) {
	UtilLog4j.log.info(this, " deleteAviso..");
	try {
	    avisoPagoOficinaBeanModel.setSgAvisoPago((SgAvisoPago) avisoPagoOficinaBeanModel.getAvisosPagosOficinaModel().getRowData());
	    avisoPagoOficinaBeanModel.deteleRelacionAvisoPago();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excep" + e.getMessage());
	    UtilLog4j.log.fatal(this, " Excepcion al eliminar la relacion el Aviso..");
	}
    }

    public DataModel getAvisosPagosOficinaModel() {
	return avisoPagoOficinaBeanModel.getAvisosPagosOficinaModel();
    }

    public DataModel getCatalogoAvisosModel() {
	return avisoPagoOficinaBeanModel.getAvisosPagosCatalogoModel();
    }

    public List<SelectItem> listaOficinaItems() {
	return avisoPagoOficinaBeanModel.getListaOficinaItems();
    }

    public List<SelectItem> listaPagosOficinaItems() {
	return avisoPagoOficinaBeanModel.getListaPagosOficinaItems();
    }

    public List<SelectItem> listaPeriodosPagoItems() {
	return avisoPagoOficinaBeanModel.getListaPeriodosPagoItems();
    }

    public SgOficina getOficinaSeleccionado() {
	return avisoPagoOficinaBeanModel.getSgOficinaSeleccionada();
    }

    public int getPeriodicidadSeleccionada() {
	return avisoPagoOficinaBeanModel.getIdPeriodicidadSeleccionada();
    }

    public SgAvisoPago getAvisoPago() {
	return avisoPagoOficinaBeanModel.getSgAvisoPago();
    }

    public Date fechaActual() {
	return avisoPagoOficinaBeanModel.getFechaActual();
    }

    public Date fechaAviso() {
	return avisoPagoOficinaBeanModel.getFechaAviso();
    }

    public Date fechaProximoPago() {
	return avisoPagoOficinaBeanModel.getFechaPago();
    }

    /*
     * Metodos de popups
     */
    public boolean getMrPopupAgregarAviso() {
	return avisoPagoOficinaBeanModel.isMrMostrarPanelAgregarNuevoAviso();
    }

    public boolean getMrPopupModificarAviso() {
	return avisoPagoOficinaBeanModel.isMrPopupModificarAviso();
    }

    public boolean getMrMostrarPanelCatalogo() {
	return avisoPagoOficinaBeanModel.isMrMostrarPanelCatalogo();
    }

    public boolean getMrPopupCatalogoAviso() {
	return avisoPagoOficinaBeanModel.isMrPopupCatalogoAvisos();
    }

    public void mostrarPanelAgregarAviso(ActionEvent event) {
	avisoPagoOficinaBeanModel.traerPagosPorTipoEspecificoToOficinaItems();
	avisoPagoOficinaBeanModel.traerPeriodosItems();
	avisoPagoOficinaBeanModel.setSgAvisoPago(new SgAvisoPago());
	avisoPagoOficinaBeanModel.setMrMostrarPanelAgregarNuevoAviso(true);
	avisoPagoOficinaBeanModel.setMrMostrarPanelCatalogo(false);
    }

    public void mostrarPopupAgregarAvisoDesdeCatalogo(ActionEvent event) {
	avisoPagoOficinaBeanModel.setMrPopupCatalogoAvisos(true);
	avisoPagoOficinaBeanModel.setMrMostrarPanelAgregarNuevoAviso(false);
	avisoPagoOficinaBeanModel.setMrMostrarPanelCatalogo(true);
    }

    public void ocultarPopupAgregarAviso(ActionEvent event) {
	avisoPagoOficinaBeanModel.setMrMostrarPanelAgregarNuevoAviso(false);
    }

    public void mostrarPopupCatalogoAviso(ActionEvent event) {
	avisoPagoOficinaBeanModel.traerCatalogoAvisos();
	avisoPagoOficinaBeanModel.setSgAvisoPago(new SgAvisoPago());
	avisoPagoOficinaBeanModel.setMrPopupCatalogoAvisos(true);
	avisoPagoOficinaBeanModel.setMrMostrarPanelCatalogo(true);
	avisoPagoOficinaBeanModel.setMrMostrarPanelAgregarNuevoAviso(false);
    }

    public void ocultarPopupCatalogoAviso(ActionEvent event) {
	avisoPagoOficinaBeanModel.setMrPopupCatalogoAvisos(false);
	avisoPagoOficinaBeanModel.setMrMostrarPanelAgregarNuevoAviso(false);
    }

    public void mostrarPopupModificarAviso(ActionEvent event) {
	avisoPagoOficinaBeanModel.traerCatalogoAvisos();
	avisoPagoOficinaBeanModel.traerPagosPorTipoEspecificoToOficinaItems();
	avisoPagoOficinaBeanModel.traerPeriodosItems();
	avisoPagoOficinaBeanModel.setSgAvisoPago((SgAvisoPago) avisoPagoOficinaBeanModel.getAvisosPagosOficinaModel().getRowData());
	avisoPagoOficinaBeanModel.setIdPagoOficinaSeleccionado(avisoPagoOficinaBeanModel.getSgAvisoPago().getSgTipoEspecifico().getId());
	avisoPagoOficinaBeanModel.setIdPeriodicidadSeleccionada(avisoPagoOficinaBeanModel.getSgAvisoPago().getSgPeriodicidad().getId());
	UtilLog4j.log.info(this, "el mostrar panel");
	avisoPagoOficinaBeanModel.setMrPopupModificarAviso(true);
    }

    public void ocultarPopupModificarAviso(ActionEvent event) {
	avisoPagoOficinaBeanModel.setMrPopupModificarAviso(false);
    }

    /**
     * @param avisoPagoOficinaBeanModel the avisoPagoOficinaBeanModel to set
     */
    public void setAvisoPagoOficinaBeanModel(AvisoPagoOficinaBeanModel avisoPagoOficinaBeanModel) {
	this.avisoPagoOficinaBeanModel = avisoPagoOficinaBeanModel;
    }
}
