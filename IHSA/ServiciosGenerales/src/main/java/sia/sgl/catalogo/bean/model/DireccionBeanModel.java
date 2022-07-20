/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.catalogo.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.modelo.SiCiudad;
import sia.modelo.SiEstado;
import sia.modelo.SiPais;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiEstadoImpl;
import sia.servicios.sistema.impl.SiPaisImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Named(value = "direccionBeanModel")

public class DireccionBeanModel implements Serializable {

    //Sistema
    @Inject
    private Sesion sesion;
    //Servicios
    @Inject
    private SiPaisImpl siPaisImpl;
    @Inject
    private SiEstadoImpl siEstadoImpl;
    @Inject
    private SiCiudadImpl siCiudadImpl;
    //Entidades
    private SiPais siPais;
    private SiEstado siEstado;
    private SiCiudad siCiudad;
    //Clases
    //Colecciones
    private DataModel dataModel;
    private List<SelectItem> selectItemList;
    //Primitivos
    private int id;
    private int idSiEstado;
    //Booleanos

    public void beginConversationCatalogoSiPais() {
	//Reiniciando variables
	this.dataModel = null;
	this.siPais = null;
    }

    public void beginConversationCatalogoSiEstado() {
	this.setSesion((Sesion) FacesUtils.getManagedBean("sesion"));
	//Reiniciando variables
	this.id = -1;
	this.dataModel = null;
	this.siPais = null;
	this.siEstado = null;
    }

    public void controlaPopUp(String cadena, boolean estado) {
	sesion.getControladorPopups().put(cadena, estado);
    }

    public void beginConversationCatalogoSiCiudad() {
	UtilLog4j.log.info(this, "beginConversationCatalogoSiCiudad");
	this.setSesion((Sesion) FacesUtils.getManagedBean("sesion"));
	//Reiniciando variables
	this.dataModel = null;
	this.id = -1;
	this.idSiEstado = -1;
	this.siEstado = null;
	this.siCiudad = null;
    }

    //>>>>>>>>>>>>>>>>>> SiPais - START <<<<<<<<<<<<<<<<<
    public SiPais getSiPaisById(int idSiPais) {
	return this.siPaisImpl.find(idSiPais);
    }

    public void getAllSiPais() {
	if (this.dataModel == null) {
	    this.dataModel = new ListDataModel(this.siPaisImpl.findAll("nombre", true, false));
	}
    }

    public void reloadAllSiPais() {
	this.dataModel = new ListDataModel(this.siPaisImpl.findAll("nombre", true, false));
    }

    public List<SiPais> getAllSiPaisList() {
	return this.siPaisImpl.findAll("nombre", true, false);
    }

    public void saveSiPais() throws ExistingItemException {
	this.siPaisImpl.save(getSiPais(), this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siPaisImpl.findAll("nombre", true, false));
    }

    public void saveSiPaisOnly() throws ExistingItemException {
	this.siPaisImpl.save(getSiPais(), this.sesion.getUsuario().getId());
    }

    public void updateSiPais() throws ExistingItemException {
	this.siPaisImpl.update(getSiPais(), this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siPaisImpl.findAll("nombre", true, false));
    }

    public void deleteSiPais() throws ItemUsedBySystemException {
	this.siPaisImpl.delete(getSiPais(), this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siPaisImpl.findAll("nombre", true, false));
    }
    //>>>>>>>>>>>>>>>>>> SiPais - END <<<<<<<<<<<<<<<<<

    //>>>>>>>>>>>>>>>>>> SiEstado - START <<<<<<<<<<<<<<<<<
    public SiEstado getSiEstadoById(int idSiEstado) {
	return this.siEstadoImpl.find(idSiEstado);
    }

    public void getAllSiEstadoBySiPais() {
	if (this.dataModel == null) {
	    this.dataModel = new ListDataModel(this.siEstadoImpl.findAll(getId(), "nombre", true, false));
	}
    }

    public void reloadAllSiEstado() {
	this.dataModel = new ListDataModel(this.siEstadoImpl.findAll(getId(), "nombre", true, false));
    }

    public List<SiEstado> getAllSiEstadoList(int idSiPais) {
	return this.siEstadoImpl.findAll(idSiPais, "nombre", true, false);
    }

    public void saveSiEstado() throws ExistingItemException {
	this.siEstadoImpl.save(getSiEstado(), this.getId(), this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siEstadoImpl.findAll(getId(), "nombre", true, false));
    }

    public void saveSiEstadoOnly() throws ExistingItemException {
	this.siEstadoImpl.save(getSiEstado(), this.getId(), this.sesion.getUsuario().getId());
    }

    public void updateSiEstado() throws ExistingItemException {
	this.siEstadoImpl.update(getSiEstado(), this.getId(), this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siEstadoImpl.findAll(getId(), "nombre", true, false));
    }

    public void deleteSiEstado() throws ItemUsedBySystemException {
	this.siEstadoImpl.delete(this.siEstado, this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siEstadoImpl.findAll(getId(), "nombre", true, false));
    }
    //>>>>>>>>>>>>>>>>>> SiEstado - END <<<<<<<<<<<<<<<<<

    //>>>>>>>>>>>>>>>>>> SiCiudad - START <<<<<<<<<<<<<<<<<
    public void getAllSiCiudad() {
	if (this.dataModel == null) {
	    this.dataModel = new ListDataModel(this.siCiudadImpl.findAll(getIdSiEstado(), "nombre", true, false));
	}
    }

    public void reloadAllSiCiudad() {
	this.dataModel = new ListDataModel(this.siCiudadImpl.findAll(getIdSiEstado(), "nombre", true, false));
    }

    public void saveSiCiudad() throws ExistingItemException {
	this.siCiudadImpl.save(getSiCiudad(), getId(), getIdSiEstado(), this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siCiudadImpl.findAll(getIdSiEstado(), "nombre", true, false));
    }

    public void updateSiCiudad() throws ExistingItemException {
	this.siCiudadImpl.update(getSiCiudad(), getIdSiEstado(), this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siCiudadImpl.findAll(getIdSiEstado(), "nombre", true, false));
    }

    public void deleteSiCiudad() throws ItemUsedBySystemException {
	UtilLog4j.log.info(this, "DireccionBeanModel.deleteSiCiudad()");

	this.siCiudadImpl.delete(this.siCiudad, this.sesion.getUsuario().getId());
	this.dataModel = new ListDataModel(this.siCiudadImpl.findAll(getIdSiEstado(), "nombre", true, false));
    }
    //>>>>>>>>>>>>>>>>>> Propiedades - END <<<<<<<<<<<<<<<<<

    //>>>>>>>>>>>>>>>>>> Propiedades - START <<<<<<<<<<<<<<<<<
    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
	return dataModel;
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
	this.dataModel = dataModel;
    }

    /**
     * @return the siPais
     */
    public SiPais getSiPais() {
	return siPais;
    }

    /**
     * @param siPais the siPais to set
     */
    public void setSiPais(SiPais siPais) {
	this.siPais = siPais;
    }

    /**
     * @return the siEstado
     */
    public SiEstado getSiEstado() {
	return siEstado;
    }

    /**
     * @param siEstado the siEstado to set
     */
    public void setSiEstado(SiEstado siEstado) {
	this.siEstado = siEstado;
    }

    /**
     * @return the siCiudad
     */
    public SiCiudad getSiCiudad() {
	return siCiudad;
    }

    /**
     * @param siCiudad the siCiudad to set
     */
    public void setSiCiudad(SiCiudad siCiudad) {
	this.siCiudad = siCiudad;
    }

    /**
     * @return the id
     */
    public int getId() {
	return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
	this.id = id;
    }

    /**
     * @return the selectItemList
     */
    public List<SelectItem> getSelectItemList() {
	return selectItemList;
    }

    /**
     * @param selectItemList the selectItemList to set
     */
    public void setSelectItemList(List<SelectItem> selectItemList) {
	this.selectItemList = selectItemList;
    }

    /**
     * @return the idSiEstado
     */
    public int getIdSiEstado() {
	return idSiEstado;
    }

    /**
     * @param idSiEstado the idSiEstado to set
     */
    public void setIdSiEstado(int idSiEstado) {
	this.idSiEstado = idSiEstado;
    }

//    public SiCiudad buscarCiudad(int ciudadID){
//        SiCiudad ciudad = null;
//        try{
//            ciudad = siCiudadImpl.find(ciudadID);
//        }catch(Exception e){
//            UtilLog4j.log.fatal(this, e);
//            ciudad = null;
//        }
//        return ciudad;
//    }
    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }
}
