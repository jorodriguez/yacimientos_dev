/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.checklist.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffChecklist;
import sia.sgl.checklist.bean.model.CheckListStaffModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "checkListStaffBean")
@RequestScoped
public class CheckListStaffBean implements Serializable {

    /**
     * Creates a new instance of CheckListStaffBean
     */
    public CheckListStaffBean() {
    }

    @ManagedProperty(value = "#{checkListStaffModel}")
    private CheckListStaffModel checkListStaffModel;

    public void cargarChecklistStaffInTable(ValueChangeEvent valueChangeEvent) {
	int value = ((Integer) valueChangeEvent.getNewValue());

	if (value < 1) { //Poner null la lista y el staff
	    this.checkListStaffModel.setIdStaffSelectItem(value);
	    this.checkListStaffModel.setStaff(null);
	    this.checkListStaffModel.setChecklistDataModel(null);
	    this.checkListStaffModel.setChecklistVODataModel(null);
	} else if (value != this.checkListStaffModel.getIdStaffSelectItem()) { //El id es diferente, entonces recargar todo
	    this.checkListStaffModel.setChecklistVODataModel(null);
	    this.checkListStaffModel.setChecklistDataModel(null);
	    this.checkListStaffModel.setIdStaffSelectItem(value);
	    checkListStaffModel.getAllChecklistStaff();
	}
    }

    public void createChecklistStaff(ActionEvent actionEvent) {
	try {
	    checkListStaffModel.createChecklistStaff();
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	} finally {
	    ocultarPopupCrearChecklistStaff(actionEvent);
	}
    }

    public void updateChecklistStaff(ActionEvent actionEvent) {
	if (!checkListStaffModel.updateChecklistStaff()) {
	    FacesUtils.addInfoMessage("Ourrió un error al actualizar el Checklist de Staff. Porfavor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
	}
	ocultarPopupActualizarChecklistStaff(actionEvent);
    }

    public void mostrarPopupCrearChecklistStaff(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "idStaffSeleccionado: " + checkListStaffModel.getIdStaffSelectItem());

	if (checkListStaffModel.getIdStaffSelectItem() > 0) {
	    if (checkListStaffModel.getThisWeekChecklistStaff() == null) {
		checkListStaffModel.setMrPopupCrearChecklistStaff(!checkListStaffModel.isMrPopupCrearChecklistStaff());
		checkListStaffModel.cargarCaracteristicas();
	    } else {
		FacesUtils.addInfoMessage("No es posible crear otro Checklist para la semana actual deibodo a que solo se permite un Checklist por semana");
	    }
	} else {
	    FacesUtils.addInfoMessage("Debes seleccionar un Staff antes");
	}
    }

    public void mostrarPopupDetalleChecklistStaff(ActionEvent actionEvent) {
	this.checkListStaffModel.setChecklistVODataModel(null);
	//Dándole memoria al StaffChecklist
	checkListStaffModel.setStaffChecklist((SgStaffChecklist) checkListStaffModel.getChecklistDataModel().getRowData());
	checkListStaffModel.getItemsVOForChecklistStaff();
	checkListStaffModel.setMrPopupDetalleChecklistStaff(!checkListStaffModel.isMrPopupDetalleChecklistStaff());
    }

    public void ocultarPopupCrearChecklistStaff(ActionEvent actionEvent) {
	checkListStaffModel.setChecklistVODataModel(null);
	checkListStaffModel.setMrPopupCrearChecklistStaff(!checkListStaffModel.isMrPopupCrearChecklistStaff());
    }

    public void ocultarPopupDetalleChecklistStaff(ActionEvent actionEvent) {
	checkListStaffModel.setChecklistVODataModel(null);
	checkListStaffModel.setStaffChecklist(null);
	checkListStaffModel.setMrPopupDetalleChecklistStaff(!checkListStaffModel.isMrPopupDetalleChecklistStaff());
    }

    public void mostrarPopupActualizarChecklistStaff(ActionEvent actionEvent) {
	this.checkListStaffModel.setChecklistVODataModel(null);
	//Dándo memoria al StaffChecklist
	checkListStaffModel.setStaffChecklist((SgStaffChecklist) checkListStaffModel.getChecklistDataModel().getRowData());
	boolean puedoModificarChecklist = true;
	try {
	    //Validar que el Checklist no ha sido modificado y que si no lo ha sido solo pueda serlo en la semana actual
	    puedoModificarChecklist = checkListStaffModel.iCanChangeTheChecklistStaff();
	    if (puedoModificarChecklist) {
		checkListStaffModel.getItemsVOForChecklistStaff();
		checkListStaffModel.setMrPopupActualizarChecklistStaff(!checkListStaffModel.isMrPopupActualizarChecklistStaff());
	    } else {
		//Quitándole la memoria a StaffChecklist
		checkListStaffModel.setStaffChecklist(null);
		FacesUtils.addInfoMessage("El Checklist ya ha sido modificado en esta semana o no corresponde a la semana actual, por lo tanto no pude ser modificado");
	    }
	} catch (Exception e) {
	    FacesUtils.addInfoMessage(e.getMessage());
	}
    }

    public void ocultarPopupActualizarChecklistStaff(ActionEvent actionEvent) {
	//Quitándo la memoria a StaffChecklist
	checkListStaffModel.setStaffChecklist(null);
	checkListStaffModel.setChecklistVODataModel(null);
	checkListStaffModel.setMrPopupActualizarChecklistStaff(!checkListStaffModel.isMrPopupActualizarChecklistStaff());
    }

    public List<SelectItem> getStaffByOficinaListItem() {
	List<SgStaff> staffList = checkListStaffModel.allStaffByOficina();
	List<SelectItem> staffListItem = new ArrayList<SelectItem>();

	for (SgStaff staff : staffList) {
	    SelectItem item = new SelectItem(staff.getId(), staff.getNombre());
	    staffListItem.add(item);
	}
	return staffListItem;
    }

    public DataModel getChecklistStaffDataModel() {
	return checkListStaffModel.getChecklistDataModel();
    }

    public DataModel getChecklistStaffVODataModelForDetalle() {
	return checkListStaffModel.getChecklistVODataModel();
    }

    public DataModel getChecklistStaffVODataModelForUpdate() {
	return checkListStaffModel.getChecklistVODataModel();
    }

    /**
     * @return the checklist
     */
    public SgChecklist getChecklist() {
	return checkListStaffModel.getChecklist();
    }

    /**
     * @param checklist the checklist to set
     */
    public void setChecklist(SgChecklist checklist) {
	checkListStaffModel.setChecklist(checklist);
    }

    /**
     * @return the staffChecklist
     */
    public SgStaffChecklist getStaffChecklist() {
	return checkListStaffModel.getStaffChecklist();
    }

    /**
     * @param staffChecklist the staffChecklist to set
     */
    public void setStaffChecklist(SgStaffChecklist staffChecklist) {
	checkListStaffModel.setStaffChecklist(staffChecklist);
    }

    /**
     * @return the staff
     */
    public SgStaff getStaff() {
	return checkListStaffModel.getStaff();
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(SgStaff staff) {
	checkListStaffModel.setStaff(staff);
    }

    /**
     * @return the checklistDataModel
     */
    public DataModel getChecklistDataModel() {
	return checkListStaffModel.getChecklistDataModel();
    }

    /**
     * @param checklistDataModel the checklistDataModel to set
     */
    public void setChecklistDataModel(DataModel checklistDataModel) {
	checkListStaffModel.setChecklistDataModel(checklistDataModel);
    }

    /**
     * @return the checklistVODataModel
     */
    public DataModel getChecklistVODataModel() {
	return checkListStaffModel.getChecklistVODataModel();
    }

    /**
     * @param checklistVODataModel the checklistVODataModel to set
     */
    public void setChecklistVODataModel(DataModel checklistVODataModel) {
	checkListStaffModel.setChecklistVODataModel(checklistVODataModel);
    }

    /**
     * @return the idStaffSelectItem
     */
    public int getIdStaffSelectItem() {
	return checkListStaffModel.getIdStaffSelectItem();
    }

    /**
     * @param idStaffSelectItem the idStaffSelectItem to set
     */
    public void setIdStaffSelectItem(int idStaffSelectItem) {
	checkListStaffModel.setIdStaffSelectItem(idStaffSelectItem);
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
	return checkListStaffModel.isFlag();
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
	checkListStaffModel.setFlag(flag);
    }

    /**
     * @return the mrPopupCrearChecklistStaff
     */
    public boolean isMrPopupCrearChecklistStaff() {
	return checkListStaffModel.isMrPopupCrearChecklistStaff();
    }

    /**
     * @param mrPopupCrearChecklistStaff the mrPopupCrearChecklistStaff to set
     */
    public void setMrPopupCrearChecklistStaff(boolean mrPopupCrearChecklistStaff) {
	checkListStaffModel.setMrPopupCrearChecklistStaff(mrPopupCrearChecklistStaff);
    }

    /**
     * @return the mrPopupActualizarChecklistStaff
     */
    public boolean isMrPopupActualizarChecklistStaff() {
	return checkListStaffModel.isMrPopupActualizarChecklistStaff();
    }

    /**
     * @param mrPopupActualizarChecklistStaff the
     * mrPopupActualizarChecklistStaff to set
     */
    public void setMrPopupActualizarChecklistStaff(boolean mrPopupActualizarChecklistStaff) {
	checkListStaffModel.setMrPopupActualizarChecklistStaff(mrPopupActualizarChecklistStaff);
    }

    /**
     * @return the mrPopupDetalleChecklistStaff
     */
    public boolean isMrPopupDetalleChecklistStaff() {
	return checkListStaffModel.isMrPopupDetalleChecklistStaff();
    }

    /**
     * @param mrPopupDetalleChecklistStaff the mrPopupDetalleChecklistStaff to
     * set
     */
    public void setMrPopupDetalleChecklistStaff(boolean mrPopupDetalleChecklistStaff) {
	checkListStaffModel.setMrPopupDetalleChecklistStaff(mrPopupDetalleChecklistStaff);
    }

    /**
     * @param checkListStaffModel the checkListStaffModel to set
     */
    public void setCheckListStaffModel(CheckListStaffModel checkListStaffModel) {
	this.checkListStaffModel = checkListStaffModel;
    }
}
