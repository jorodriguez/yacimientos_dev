/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.checklist.bean;

import java.io.Serializable;
import java.util.Date;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgOficinaChecklist;
import sia.sgl.checklist.bean.model.ChecklistBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Named(value = "checklistBean")
@RequestScoped
public class ChecklistBean implements Serializable {

    //Sistema
    @ManagedProperty(value = "#{checklistBeanModel}")
    private ChecklistBeanModel checklistBeanModel;

    /**
     * Creates a new instance of ChecklistBean
     */
    public ChecklistBean() {
    }

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
	UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
	try {
	    FacesContext context = FacesContext.getCurrentInstance();
	    UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
	    UIComponent parentComponent = component.getParent();
	    parentComponent.getChildren().clear();
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
	}
    }

    public void clearVariables(ActionEvent actionEvent) {
	//Borrar variables
	checklistBeanModel.setFlag(false);
	checklistBeanModel.setChecklist(null);
	checklistBeanModel.setChecklistVODataModel(null);
	checklistBeanModel.setSelectedIndex("0");
	checklistBeanModel.setCadena("");
    }

    public DataModel getChecklistOficinaDataModel() {
	return checklistBeanModel.getChecklistDataModel();
    }

    /**
     * @return the checklistVODataModel
     */
    public DataModel getChecklistVODataModel() {
	return checklistBeanModel.getChecklistVODataModel();
    }

    /**
     * @param checklistVODataModel the checklistVODataModel to set
     */
    public void setChecklistVODataModel(DataModel checklistVODataModel) {
	checklistBeanModel.setChecklistDataModel(checklistVODataModel);
    }

    public DataModel getChecklistOficinaVODataModelForDetalle() {
	return checklistBeanModel.getChecklistVODataModel();
    }

    public DataModel getChecklistOficinaVODataModelForUpdate() {
	return checklistBeanModel.getChecklistVODataModel();
    }

    public void createChecklistOficina(ActionEvent actionEvent) {
	try {
	    checklistBeanModel.createChecklistOficina();
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	} finally {
	    ocultarPopupCrearChecklistOficina(actionEvent);
	}
    }

    public void updateChecklistOficina(ActionEvent actionEvent) {
//        UtilLog4j.log.info(this, "ChecklistBean.updateChecklistOficina()");
	if (!checklistBeanModel.updateChecklistOficina()) {
	    FacesUtils.addInfoMessage("Ourrió un error al actualizar el Checklist de Oficina. Porfavor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
	}
	ocultarPopupActualizarChecklistOficina(actionEvent);
    }

    /**
     * @return the checklist
     */
    public SgChecklist getChecklist() {
	return checklistBeanModel.getChecklist();
    }

    /**
     * @param checklist the checklist to set
     */
    public void setChecklist(SgChecklist checklist) {
	checklistBeanModel.setChecklist(checklist);
    }

    /**
     * @return the filtroFechaInicio
     */
    public Date getFiltroFechaInicio() {
	return checklistBeanModel.getFiltroFechaInicio();
    }

    /**
     * @param filtroFechaInicio the filtroFechaInicio to set
     */
    public void setFiltroFechaInicio(Date filtroFechaInicio) {
	checklistBeanModel.setFiltroFechaInicio(filtroFechaInicio);
    }

    /**
     * @return the filtroFechaFin
     */
    public Date getFiltroFechaFin() {
	return checklistBeanModel.getFiltroFechaFin();
    }

    /**
     * @param filtroFechaFin the filtroFechaFin to set
     */
    public void setFiltroFechaFin(Date filtroFechaFin) {
	checklistBeanModel.setFiltroFechaFin(filtroFechaFin);
    }

    /**
     * @return the checklistDataModel
     */
    public DataModel getChecklistDataModel() {
	return checklistBeanModel.getChecklistDataModel();
    }

    /**
     * @param checklistDataModel the checklistDataModel to set
     */
    public void setChecklistDataModel(DataModel checklistDataModel) {
	checklistBeanModel.setChecklistDataModel(checklistDataModel);
    }

//    ===================================== Popups =====================================
    public void mostrarPopupCrearChecklistOficina(ActionEvent actionEvent) {
	if (checklistBeanModel.getThisWeekChecklistOficina() == null) {
	    checklistBeanModel.setMrPopupCrearChecklistOficina(!checklistBeanModel.isMrPopupCrearChecklistOficina());
	    checklistBeanModel.iniciarCheck();
	} else {
	    FacesUtils.addInfoMessage("No es posible crear otro Checklist para la semana actual deibodo a que solo se permite un Checklist por semana");
	}
    }

    public void ocultarPopupCrearChecklistOficina(ActionEvent actionEvent) {
	checklistBeanModel.setChecklistDataModel(null);
	checklistBeanModel.setChecklistVODataModel(null);
	checklistBeanModel.setMrPopupCrearChecklistOficina(!checklistBeanModel.isMrPopupCrearChecklistOficina());
    }

    public void mostrarPopupDetalleChecklistOficina(ActionEvent actionEvent) {
	this.checklistBeanModel.setChecklistVODataModel(null);
	//Dándole memoria al OficinaChecklist
	checklistBeanModel.setOficinaChecklist((SgOficinaChecklist) checklistBeanModel.getChecklistDataModel().getRowData());
	checklistBeanModel.getItemsVOForChecklistOficina();
	checklistBeanModel.setMrPopupDetalleChecklistOficina(!checklistBeanModel.isMrPopupDetalleChecklistOficina());
    }

    public void ocultarPopupDetalleChecklistOficina(ActionEvent actionEvent) {
	this.checklistBeanModel.setChecklistVODataModel(null);
	this.checklistBeanModel.setOficinaChecklist(null);
	checklistBeanModel.setMrPopupDetalleChecklistOficina(!checklistBeanModel.isMrPopupDetalleChecklistOficina());
    }

    public void mostrarPopupActualizarChecklistOficina(ActionEvent actionEvent) {
	this.checklistBeanModel.setChecklistVODataModel(null);
	//Dándole memoria al OficinaChecklist
	checklistBeanModel.setOficinaChecklist((SgOficinaChecklist) checklistBeanModel.getChecklistDataModel().getRowData());
	boolean puedoModificarChecklist = true;
	try {
	    //Validar que el Checklist no ha sido modificado y que si no lo ha sido solo pueda serlo en la semana actual
	    puedoModificarChecklist = checklistBeanModel.iCanChangeTheChecklistOficina();
	    if (puedoModificarChecklist) {
		checklistBeanModel.getItemsVOForChecklistOficina();
		checklistBeanModel.setMrPopupActualizarChecklistOficina(!checklistBeanModel.isMrPopupActualizarChecklistOficina());
	    } else {
		//Quitándole la memoria a OficinaChecklist
		checklistBeanModel.setOficinaChecklist(null);
		FacesUtils.addInfoMessage("El Checklist ya ha sido modificado en esta semana o no corresponde a la semana actual, por lo tanto no pude ser modificado");
	    }
	} catch (Exception e) {
	    FacesUtils.addInfoMessage(e.getMessage());
	}
    }

    public void ocultarPopupActualizarChecklistOficina(ActionEvent actionEvent) {
	//Quitándo memorias
	checklistBeanModel.setOficinaChecklist(null);
	checklistBeanModel.setChecklistVODataModel(null);
	checklistBeanModel.setMrPopupActualizarChecklistOficina(!checklistBeanModel.isMrPopupActualizarChecklistOficina());
    }

    /**
     * @return the mrPopupCrearChecklistOficina
     */
    public boolean isMrPopupCrearChecklistOficina() {
	return checklistBeanModel.isMrPopupCrearChecklistOficina();
    }

    /**
     * @return the mrPopupActualizarChecklistOficina
     */
    public boolean isMrPopupActualizarChecklistOficina() {
	return checklistBeanModel.isMrPopupActualizarChecklistOficina();
    }

    /**
     * @return the mrPopupDetalleChecklistOficina
     */
    public boolean isMrPopupDetalleChecklistOficina() {
	return checklistBeanModel.isMrPopupDetalleChecklistOficina();
    }

    /**
     * @return the selectedIndex
     */
    public String getSelectedIndex() {
	try {
	    return checklistBeanModel.getSelectedIndex();
	} catch (RuntimeException e) {
	    return checklistBeanModel.getSelectedIndex();
	}
    }

    /**
     * @param selectedIndex the selectedIndex to set
     */
    public void setSelectedIndex(String selectedIndex) {
	checklistBeanModel.setSelectedIndex(selectedIndex);
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
	return checklistBeanModel.getCadena();
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
	checklistBeanModel.setCadena(cadena);
    }

//    /**
//     * @return the adjunto
//     */
//    public SiAdjunto getAdjunto() {
//        return checklistBeanModel.getAdjunto();
//    }
//
//    /**
//     * @param adjunto the adjunto to set
//     */
//    public void setAdjunto(SiAdjunto adjunto) {
//        checklistBeanModel.setAdjunto(adjunto);
//    }
    /**
     * @return the checklistLlantas
     */
    /**
     * @return the flag
     */
    public boolean isFlag() {
	return checklistBeanModel.isFlag();
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
	checklistBeanModel.setFlag(flag);
    }

    /**
     * @param checklistBeanModel the checklistBeanModel to set
     */
    public void setChecklistBeanModel(ChecklistBeanModel checklistBeanModel) {
	this.checklistBeanModel = checklistBeanModel;
    }

}
