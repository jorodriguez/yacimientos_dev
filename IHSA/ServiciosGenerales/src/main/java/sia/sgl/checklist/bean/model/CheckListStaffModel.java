/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.checklist.bean.model;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffChecklist;
import sia.servicios.sgl.impl.SgChecklistImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.sgl.sistema.bean.backing.Sesion;

/**
 *
 * @author ihsa
 */
@Named(value = "checkListStaffModel")
@ViewScoped
public class CheckListStaffModel implements Serializable {

    /**
     * Creates a new instance of CheckListStaffModel
     */
    public CheckListStaffModel() {
    }

    @Inject
    private Sesion sesion;

    @Inject
    private SgChecklistImpl checklistService;
    @Inject
    private SgStaffImpl staffService;
    //Entidades
    private SgChecklist checklist;
    private SgStaffChecklist staffChecklist;
    private SgStaff staff;

    private DataModel checklistDataModel;
    private DataModel checklistVODataModel;
    private int idStaffSelectItem;
    private boolean flag = false;
    private boolean mrPopupCrearChecklistStaff = false;
    private boolean mrPopupActualizarChecklistStaff = false;
    private boolean mrPopupDetalleChecklistStaff = false;

    @PostConstruct
    public void beginConversationChecklistOficina() {
	//Reiniciando variables necesarias
	this.checklist = null;
	this.staffChecklist = null;
	this.staff = null;
	this.idStaffSelectItem = -1;

	controlarPopFalse("popupObservacionToAdjunto");
	controlarPopFalse("popupUpdateObservacionToAdjunto");
	controlarPopFalse("popupUploadChecklistExterior");
    }

    public void getAllChecklistStaff() { //Este debería filtrar los Checklist por el Staff seleccionado
	if (this.idStaffSelectItem > 0) {
	    this.checklistDataModel = new ListDataModel(checklistService.getAllChecklistsByStaffAndStatusList(idStaffSelectItem, Constantes.NO_ELIMINADO));
	} else if (this.idStaffSelectItem <= 0) {
	    this.checklistDataModel = null;
	}
    }

    public void cargarCaracteristicas() {
	try {
	    staff = staffService.find(idStaffSelectItem);
	    this.checklistVODataModel = new ListDataModel(checklistService.getAllItemsChecklistVO(staff, Constantes.NO_ELIMINADO));
	} catch (Exception ex) {
	    Logger.getLogger(CheckListStaffModel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public List<SgStaff> allStaffByOficina() {
	if (sesion.getOficinaActual() != null) {
	    return staffService.getAllStaffByStatusAndOficina(Constantes.NO_ELIMINADO, sesion.getOficinaActual().getId());
	}
	return null;

    }

    public void createChecklistStaff() throws SIAException {
	boolean saveChecklistSuccessfull = true;
	if (checklistVODataModel == null || this.checklistVODataModel.getRowCount() <= 0) { //No se deben crear Checklist vacíos
	    throw new SIAException("No es posible crear un Checklist sin elementos");
	} else {
	    saveChecklistSuccessfull = checklistService.saveChecklist(this.staff, this.checklistVODataModel, sesion.getUsuario().getId());

	    if (!saveChecklistSuccessfull) {
		throw new SIAException("Ocurrió un error al crear el Checklist. Porfavor contacta al Equipo del SIA para solucionar esto al correo soportesia@ihsa.mx");
	    } else {
		//Recargar los Checklist's
		if (this.idStaffSelectItem > 0) {
		    this.checklistDataModel = new ListDataModel(checklistService.getAllChecklistsByStaffAndStatusList(idStaffSelectItem, Constantes.NO_ELIMINADO));
		}
	    }
	}
    }

    public boolean updateChecklistStaff() {
	boolean updateChecklistSuccessfull = checklistService.updateChecklist(this.staffChecklist.getSgChecklist(), checklistVODataModel, sesion.getUsuario().getId());
	//Recargar los Checklist's
	if (this.idStaffSelectItem > 0) {
	    this.checklistDataModel = new ListDataModel(checklistService.getAllChecklistsByStaffAndStatusList(idStaffSelectItem, Constantes.NO_ELIMINADO));
	}
	return updateChecklistSuccessfull;
    }

    public SgStaffChecklist getThisWeekChecklistStaff() {
	SgStaff staffHouse = this.staffService.find(this.idStaffSelectItem);
	return checklistService.getThisWeekChecklistStaff(staffHouse);
    }

    public boolean iCanChangeTheChecklistStaff() throws Exception {
//        UtilLog4j.log.info(this, "ChecklistBeanModel.iCanChangeTheChecklistStaff()");
	return checklistService.iCanChangeTheChecklist(this.staffChecklist);
    }

    public void getItemsVOForChecklistStaff() {
	if (this.staffChecklist != null && this.checklistVODataModel == null) {
	    this.checklistVODataModel = new ListDataModel(checklistService.getChecklistVOItemsByChecklist(this.staffChecklist.getSgChecklist()));
	}
    }

    public void controlarPopFalse(String popup) {
	sesion.getControladorPopups().put(popup, Boolean.FALSE);
    }

    public void controlarPopTrue(String popup) {
	sesion.getControladorPopups().put(popup, Boolean.TRUE);
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the checklist
     */
    public SgChecklist getChecklist() {
	return checklist;
    }

    /**
     * @param checklist the checklist to set
     */
    public void setChecklist(SgChecklist checklist) {
	this.checklist = checklist;
    }

    /**
     * @return the staffChecklist
     */
    public SgStaffChecklist getStaffChecklist() {
	return staffChecklist;
    }

    /**
     * @param staffChecklist the staffChecklist to set
     */
    public void setStaffChecklist(SgStaffChecklist staffChecklist) {
	this.staffChecklist = staffChecklist;
    }

    /**
     * @return the staff
     */
    public SgStaff getStaff() {
	return staff;
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(SgStaff staff) {
	this.staff = staff;
    }

    /**
     * @return the checklistDataModel
     */
    public DataModel getChecklistDataModel() {
	return checklistDataModel;
    }

    /**
     * @param checklistDataModel the checklistDataModel to set
     */
    public void setChecklistDataModel(DataModel checklistDataModel) {
	this.checklistDataModel = checklistDataModel;
    }

    /**
     * @return the checklistVODataModel
     */
    public DataModel getChecklistVODataModel() {
	return checklistVODataModel;
    }

    /**
     * @param checklistVODataModel the checklistVODataModel to set
     */
    public void setChecklistVODataModel(DataModel checklistVODataModel) {
	this.checklistVODataModel = checklistVODataModel;
    }

    /**
     * @return the idStaffSelectItem
     */
    public int getIdStaffSelectItem() {
	return idStaffSelectItem;
    }

    /**
     * @param idStaffSelectItem the idStaffSelectItem to set
     */
    public void setIdStaffSelectItem(int idStaffSelectItem) {
	this.idStaffSelectItem = idStaffSelectItem;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
	return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
	this.flag = flag;
    }

    /**
     * @return the mrPopupCrearChecklistStaff
     */
    public boolean isMrPopupCrearChecklistStaff() {
	return mrPopupCrearChecklistStaff;
    }

    /**
     * @param mrPopupCrearChecklistStaff the mrPopupCrearChecklistStaff to set
     */
    public void setMrPopupCrearChecklistStaff(boolean mrPopupCrearChecklistStaff) {
	this.mrPopupCrearChecklistStaff = mrPopupCrearChecklistStaff;
    }

    /**
     * @return the mrPopupActualizarChecklistStaff
     */
    public boolean isMrPopupActualizarChecklistStaff() {
	return mrPopupActualizarChecklistStaff;
    }

    /**
     * @param mrPopupActualizarChecklistStaff the
     * mrPopupActualizarChecklistStaff to set
     */
    public void setMrPopupActualizarChecklistStaff(boolean mrPopupActualizarChecklistStaff) {
	this.mrPopupActualizarChecklistStaff = mrPopupActualizarChecklistStaff;
    }

    /**
     * @return the mrPopupDetalleChecklistStaff
     */
    public boolean isMrPopupDetalleChecklistStaff() {
	return mrPopupDetalleChecklistStaff;
    }

    /**
     * @param mrPopupDetalleChecklistStaff the mrPopupDetalleChecklistStaff to
     * set
     */
    public void setMrPopupDetalleChecklistStaff(boolean mrPopupDetalleChecklistStaff) {
	this.mrPopupDetalleChecklistStaff = mrPopupDetalleChecklistStaff;
    }
}
