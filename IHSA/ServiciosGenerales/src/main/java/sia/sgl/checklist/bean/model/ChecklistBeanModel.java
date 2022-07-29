/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.checklist.bean.model;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgOficinaChecklist;
import sia.servicios.sgl.impl.SgChecklistImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 * 
 */
@Named(value = "checklistBeanModel")
@ViewScoped
public class ChecklistBeanModel implements Serializable {

    //Sistema
    @Inject
    private Sesion sesion;
    //ManagedBeans
    //EJBs
    @Inject
    private SgChecklistImpl checklistService;
    @Inject
    private SiParametroImpl siParametroService;
    //Entidades
    private SgChecklist checklist;
    private SgOficinaChecklist oficinaChecklist;
    //Colecciones
    private DataModel checklistDataModel;
    private DataModel checklistVODataModel;
    //Clases
    private Date filtroFechaInicio;
    private Date filtroFechaFin;
    private String selectedIndex = "0";
    private boolean flag = false;
    private String cadena;
    //Primitivos
    private boolean mrPopupCrearChecklistOficina;
    private boolean mrPopupActualizarChecklistOficina;
    private boolean mrPopupDetalleChecklistOficina;

    /**
     * Creates a new instance of ChecklistBeanModel
     */
    public ChecklistBeanModel() {
    }

    @PostConstruct
    public void beginConversationChecklistOficina() {
	try {
	    //Reiniciando variables necesarias
	    this.checklist = null;
	    this.checklistDataModel = null;
	    this.oficinaChecklist = null;

	    controlarPopFalse("popupObservacionToAdjunto");
	    controlarPopFalse("popupUpdateObservacionToAdjunto");
	    controlarPopFalse("popupUploadChecklistExterior");
	    checklistDataModel = new ListDataModel(checklistService.getAllChecklistsByOficinaAndStatusList(sesion.getOficinaActual().getId(), Constantes.NO_ELIMINADO));
	} catch (Exception ex) {
	    Logger.getLogger(ChecklistBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void controlarPopFalse(String popup) {
	sesion.getControladorPopups().put(popup, Boolean.FALSE);
    }

    public void controlarPopTrue(String popup) {
	sesion.getControladorPopups().put(popup, Boolean.TRUE);
    }

    public void iniciarCheck() {
	try {
	    this.checklistVODataModel = new ListDataModel(checklistService.getAllItemsChecklistVO(sesion.getOficinaActual(), Constantes.NO_ELIMINADO));
	} catch (Exception ex) {
	    Logger.getLogger(ChecklistBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void createChecklistOficina() throws SIAException {
//        UtilLog4j.log.info(this, "ChecklistBeanModel.createChecklistOficina()");
	boolean saveChecklistSuccessfull = true;
//
	if (checklistVODataModel == null || this.checklistVODataModel.getRowCount() <= 0) { //No se deben crear Checklist vacíos
	    throw new SIAException("No es posible crear un Checklist sin elementos");
	} else {
	    saveChecklistSuccessfull = checklistService.saveChecklist(sesion.getOficinaActual(), this.checklistVODataModel, sesion.getUsuario().getId());

	    if (!saveChecklistSuccessfull) {
		throw new SIAException("Ocurrió un error al crear el Checklist. Porfavor contacta al Equipo del SIA para solucionar esto al correo soportesia@ihsa.mx");
	    }
	}
	//Recargar los Checklist's
	this.checklistDataModel = new ListDataModel(checklistService.getAllChecklistsByOficinaAndStatusList(sesion.getOficinaActual().getId(), Constantes.NO_ELIMINADO));
    }

    public boolean updateChecklistOficina() {
//        UtilLog4j.log.info(this, "ChecklistBeanModel.updateChecklistOficina()");
	boolean updateChecklistSuccessfull = checklistService.updateChecklist(this.oficinaChecklist.getSgChecklist(), checklistVODataModel, sesion.getUsuario().getId());
	//Recargar Checklist's
	this.checklistDataModel = new ListDataModel(checklistService.getAllChecklistsByOficinaAndStatusList(sesion.getOficinaActual().getId(), Constantes.NO_ELIMINADO));
	return updateChecklistSuccessfull;
    }

    /**
     * Elimina físicamente un archivo
     *
     * @param url
     * @return
     */
    public boolean eliminarArchivoFisicamente(String url) throws SIAException, Exception {
	boolean retVal = false;

	try {
	    UtilLog4j.log.info(this, "Url a eliminar: " + url);
	    Files.delete(Paths.get(siParametroService.find(1).getUploadDirectory() + url));

	    retVal = true;
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, "Excepcion en eliminar adjunto" + ex.getMessage());
	}

	return retVal;
    }

    public SgOficinaChecklist getThisWeekChecklistOficina() {
//        UtilLog4j.log.info(this, "ChecklistBeanModel.getThisWeekChecklistOficina()");
	return checklistService.getThisWeekChecklistOficina(sesion.getOficinaActual().getId());
    }

    public boolean iCanChangeTheChecklistOficina() throws Exception {
//        UtilLog4j.log.info(this, "ChecklistBeanModel.iCanChangeTheChecklistOficina()");
	return checklistService.iCanChangeTheChecklist(this.oficinaChecklist);
    }

    public void getItemsVOForChecklistOficina() {
	if (this.checklistVODataModel == null) {
	    this.checklistVODataModel = new ListDataModel(checklistService.getChecklistVOItemsByChecklist(this.oficinaChecklist.getSgChecklist()));
	}
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
     * @return the filtroFechaInicio
     */
    public Date getFiltroFechaInicio() {
	return (Date) filtroFechaInicio.clone();
    }

    /**
     * @param filtroFechaInicio the filtroFechaInicio to set
     */
    public void setFiltroFechaInicio(Date filtroFechaInicio) {
	this.filtroFechaInicio = (Date) filtroFechaInicio.clone();
    }

    /**
     * @return the filtroFechaFin
     */
    public Date getFiltroFechaFin() {
	return (Date) filtroFechaFin.clone();
    }

    /**
     * @param filtroFechaFin the filtroFechaFin to set
     */
    public void setFiltroFechaFin(Date filtroFechaFin) {
	this.filtroFechaFin = (Date) filtroFechaFin.clone();
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
     * @return the oficinaChecklist
     */
    public SgOficinaChecklist getOficinaChecklist() {
	return oficinaChecklist;
    }

    /**
     * @param oficinaChecklist the oficinaChecklist to set
     */
    public void setOficinaChecklist(SgOficinaChecklist oficinaChecklist) {
	this.oficinaChecklist = oficinaChecklist;
    }

    /**
     * @return the mrPopupCrearChecklistOficina
     */
    public boolean isMrPopupCrearChecklistOficina() {
	return mrPopupCrearChecklistOficina;
    }

    /**
     * @param mrPopupCrearChecklistOficina the mrPopupCrearChecklistOficina to
     * set
     */
    public void setMrPopupCrearChecklistOficina(boolean mrPopupCrearChecklistOficina) {
	this.mrPopupCrearChecklistOficina = mrPopupCrearChecklistOficina;
    }

    /**
     * @return the mrPopupActualizarChecklistOficina
     */
    public boolean isMrPopupActualizarChecklistOficina() {
	return mrPopupActualizarChecklistOficina;
    }

    /**
     * @param mrPopupActualizarChecklistOficina the
     * mrPopupActualizarChecklistOficina to set
     */
    public void setMrPopupActualizarChecklistOficina(boolean mrPopupActualizarChecklistOficina) {
	this.mrPopupActualizarChecklistOficina = mrPopupActualizarChecklistOficina;
    }

    /**
     * @return the mrPopupDetalleChecklistOficina
     */
    public boolean isMrPopupDetalleChecklistOficina() {
	return mrPopupDetalleChecklistOficina;
    }

    /**
     * @param mrPopupDetalleChecklistOficina the mrPopupDetalleChecklistOficina
     * to set
     */
    public void setMrPopupDetalleChecklistOficina(boolean mrPopupDetalleChecklistOficina) {
	this.mrPopupDetalleChecklistOficina = mrPopupDetalleChecklistOficina;
    }

    /**
     * @return the selectedIndex
     */
    public String getSelectedIndex() {
	try {
	    return selectedIndex;
	} catch (RuntimeException e) {
	    return selectedIndex;
	}
    }

    /**
     * @param selectedIndex the selectedIndex to set
     */
    public void setSelectedIndex(String selectedIndex) {
	this.selectedIndex = selectedIndex;
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
	return cadena;
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
	this.cadena = cadena;
    }

//    /**
//     * @return the adjunto
//     */
//    public SiAdjunto getAdjunto() {
//        return adjunto;
//    }
//
//    /**
//     * @param adjunto the adjunto to set
//     */
//    public void setAdjunto(SiAdjunto adjunto) {
//        this.adjunto = adjunto;
//    }
    /**
     * @return the checklistExtVehiculo
     */
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
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

}
