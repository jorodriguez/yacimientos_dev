/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.direccion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.administracion.direccion.bean.model.DireccionBeanModel;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.modelo.SiCiudad;
import sia.modelo.SiEstado;
import sia.modelo.SiPais;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */

@Named(value = "direccionBean")
@javax.enterprise.context.RequestScoped
public class DireccionBean implements Serializable{

    
    @Inject
    private DireccionBeanModel direccionBeanModel;
    @Inject
    private Sesion sesion;

    public DireccionBean() {
    }

    public void goToSiPais() {
        this.direccionBeanModel.beginConversationCatalogoSiPais();
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.sesion.getControladorPopups().put("popupCreateSiPais", Boolean.FALSE);
        this.sesion.getControladorPopups().put("popupUpdateSiPais", Boolean.FALSE);
//        return "/vistas/administracion/direccion/catalogoSiPais";
    }

    public void goToSiEstado() {
        this.direccionBeanModel.beginConversationCatalogoSiEstado();
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.sesion.getControladorPopups().put("popupCreateSiEstado", Boolean.FALSE);
        this.sesion.getControladorPopups().put("popupUpdateSiEstado", Boolean.FALSE);
        this.sesion.getControladorPopups().put("popupDeleteSiEstado", Boolean.FALSE);
        this.sesion.getControladorPopups().put("popupCreateSiPais", Boolean.FALSE);

//        return "/vistas/administracion/direccion/catalogoSiEstado";
    }

    public void goToSiCiudad() {
        this.direccionBeanModel.beginConversationCatalogoSiCiudad();
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.sesion.getControladorPopups().put("popupCreateSiCiudad", Boolean.FALSE);
        this.sesion.getControladorPopups().put("popupUpdateSiCiudad", Boolean.FALSE);
        this.sesion.getControladorPopups().put("popupDeleteSiCiudad", Boolean.FALSE);
        this.sesion.getControladorPopups().put("popupCreateSiPais", Boolean.FALSE);
        this.sesion.getControladorPopups().put("popupCreateSiEstado", Boolean.FALSE);

//        return "/vistas/administracion/direccion/catalogoSiCiudad";
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
            UtilLog4j.log.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    public void loadOnTableSiEstadoBySiPaisListener(ValueChangeEvent valueChangeEvent) {
        int idAnterior = this.direccionBeanModel.getId();
        this.direccionBeanModel.setId(((Integer) valueChangeEvent.getNewValue()).intValue());

        if (this.direccionBeanModel.getId() > 0) {
            if (idAnterior != ((Integer) valueChangeEvent.getNewValue()).intValue()) {
                this.direccionBeanModel.reloadAllSiEstado();
            }

        } else {
            this.direccionBeanModel.setDataModel(null); //paises
        }
    }

    public void loadSiEstadoBySiPaisOnComboListener(ValueChangeEvent valueChangeEvent) {
        int idAnterior = this.direccionBeanModel.getId();
        this.direccionBeanModel.setId(((Integer) valueChangeEvent.getNewValue()).intValue());

        if (this.direccionBeanModel.getId() > 0) {
            if (idAnterior != ((Integer) valueChangeEvent.getNewValue()).intValue()) {
                //Rellenar combo de Estados
                List<SiEstado> siEstadoList = this.direccionBeanModel.getAllSiEstadoList(this.direccionBeanModel.getId());
                List<SelectItem> list = new ArrayList<SelectItem>();

                for (SiEstado sp : siEstadoList) {
                    SelectItem item = new SelectItem(sp.getId(), sp.getNombre());
                    list.add(item);
                }
                this.direccionBeanModel.setSelectItemList(list); //estados
                this.direccionBeanModel.setDataModel(null); //ciudades
            }
        } else {
            this.direccionBeanModel.setSelectItemList(null); //estados
            this.direccionBeanModel.setDataModel(null); //ciudades
        }
    }
    
    public void loadSiCiudadBySiEstadoOnTableListener(ValueChangeEvent valueChangeEvent) {
        int idAnterior = this.direccionBeanModel.getIdSiEstado();
        this.direccionBeanModel.setIdSiEstado(((Integer) valueChangeEvent.getNewValue()).intValue());

        if (this.direccionBeanModel.getIdSiEstado() > 0) {
            if (idAnterior != ((Integer) valueChangeEvent.getNewValue()).intValue()) {
                this.direccionBeanModel.reloadAllSiCiudad();
            }
        } else {
            this.direccionBeanModel.setDataModel(null); //ciudades
        }
    }  
    
    //>>>>>>>>>>>>>>>>>> Catálogo SiPais - START <<<<<<<<<<<<<<<<<   
    public DataModel getAllSiPaisDataModel() {
        try {
            this.direccionBeanModel.getAllSiPais();
            return this.direccionBeanModel.getDataModel();
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public List<SelectItem> getAllSiPaisSelectItem() {
        List<SiPais> siPaisList = this.direccionBeanModel.getAllSiPaisList();
        List<SelectItem> list = new ArrayList<SelectItem>();

        for (SiPais sp : siPaisList) {
            SelectItem item = new SelectItem(sp.getId(), sp.getNombre());
            list.add(item);
        }

        return list;
    }    
    
    public void saveSiPais() {
        try {
            this.direccionBeanModel.saveSiPais();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siPais") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
            closePopupCreateSiPais();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupCreateSiPais:msgsPopupCreateSiPais", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SiPais)eie.getElemento()).getNombre());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupCreateSiPais:msgsPopupCreateSiPais", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void saveSiPaisOnly() {
        try {
            this.direccionBeanModel.saveSiPaisOnly();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siPais") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
            closePopupCreateSiPais();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupCreateSiPais:msgsPopupCreateSiPais", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SiPais)eie.getElemento()).getNombre());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupCreateSiPais:msgsPopupCreateSiPais", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }    

    public void updateSiPais() {
        try {
            this.direccionBeanModel.updateSiPais();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siPais") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
            closePopupUpdateSiPais();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupUpdateSiPais:msgsPopupUpdateSiPais", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SiPais)eie.getElemento()).getNombre());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
            this.direccionBeanModel.reloadAllSiPais();
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupUpdateSiPais:msgsPopupUpdateSiPais", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteSiPais() {
        try {
            this.direccionBeanModel.setSiPais((SiPais) this.direccionBeanModel.getDataModel().getRowData());
            this.direccionBeanModel.deleteSiPais();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siPais") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
        } catch (ItemUsedBySystemException iue) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(iue.getLiteral()));
            UtilLog4j.log.fatal(this, iue.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }    
    
    public void openPopupCreateSiPais() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(new SiPais());
        this.sesion.getControladorPopups().put("popupCreateSiPais", Boolean.TRUE);
    }

    public void closePopupCreateSiPais() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(null);
        clearComponent("formPopupCreateSiPais", "inpTxtNombre");
        this.sesion.getControladorPopups().put("popupCreateSiPais", Boolean.FALSE);
    }

    public void openPopupUpdateSiPais() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais((SiPais) this.direccionBeanModel.getDataModel().getRowData());
        this.sesion.getControladorPopups().put( "popupUpdateSiPais", Boolean.TRUE);
    }

    public void closePopupUpdateSiPais() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(null);
        clearComponent("formPopupUpdateSiPais", "inpTxtNombre");
        this.sesion.getControladorPopups().put("popupUpdateSiPais", Boolean.FALSE);
    }
    //>>>>>>>>>>>>>>>>>> Catálogo SiPais - END <<<<<<<<<<<<<<<<<   
    
    //>>>>>>>>>>>>>>>>>> Catálogo SiEstado - START <<<<<<<<<<<<<<<<< 
    public DataModel getAllSiEstadoDataModel() {
        return this.direccionBeanModel.getDataModel();
    }
    
    public void saveSiEstado() {
        try {
            this.direccionBeanModel.saveSiEstado();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siEstado") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
            closePopupCreateSiEstado();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupCreateSiEstado:msgsPopupCreateSiEstado", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SiEstado)eie.getElemento()).getNombre());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupCreateSiEstado:msgsPopupCreateSiEstado", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void saveSiEstadoFromSiCiudad() {
        try {
            this.direccionBeanModel.saveSiEstadoOnly();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siEstado") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));

            //Rellenando de nuevo el combo de SiEstado
            List<SiEstado> siEstadoList = this.direccionBeanModel.getAllSiEstadoList(this.direccionBeanModel.getId());
            List<SelectItem> list = new ArrayList<SelectItem>();

            for (SiEstado sp : siEstadoList) {
                SelectItem item = new SelectItem(sp.getId(), sp.getNombre());
                list.add(item);
            }
            this.direccionBeanModel.setSelectItemList(list); //estados

            closePopupCreateSiEstado();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupCreateSiEstado:msgsPopupCreateSiEstado", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SiEstado) eie.getElemento()).getNombre());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupCreateSiEstado:msgsPopupCreateSiEstado", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateSiEstado() {
        try {
            this.direccionBeanModel.updateSiEstado();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siEstado") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
            closePopupUpdateSiEstado();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupUpdateSiEstado:msgsPopupUpdateSiEstado", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SiEstado)eie.getElemento()).getNombre());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
            this.direccionBeanModel.reloadAllSiEstado();
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupUpdateSiEstado:msgsPopupUpdateSiEstado", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteSiEstado() {
        try {
            this.direccionBeanModel.setSiEstado((SiEstado) this.direccionBeanModel.getDataModel().getRowData());
            this.direccionBeanModel.deleteSiEstado();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siEstado") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
        } catch (ItemUsedBySystemException iue) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(iue.getLiteral()));
            UtilLog4j.log.fatal(this, iue.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }   
    
    public void openPopupCreateSiEstado() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        if (this.direccionBeanModel.getId() > 0) {
            this.direccionBeanModel.setSiPais(this.direccionBeanModel.getSiPaisById(this.direccionBeanModel.getId()));
            this.direccionBeanModel.setSiEstado(new SiEstado());
            this.sesion.getControladorPopups().put("popupCreateSiEstado", Boolean.TRUE);
        } else {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("siEstado.mensaje.error.seleccionarSiPais"));
        }
    }
    
    public void openPopupCreateSiEstadoFromCatalogoSiCiudad() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        if (this.direccionBeanModel.getId() > 0) {
            this.direccionBeanModel.setSiPais(this.direccionBeanModel.getSiPaisById(this.direccionBeanModel.getId()));
            this.direccionBeanModel.setSiEstado(new SiEstado());
            this.sesion.getControladorPopups().put("popupCreateSiEstado", Boolean.TRUE);
        } else {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("siEstado.mensaje.error.seleccionarSiPais"));
        }
    }    

    public void closePopupCreateSiEstado() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(null);
        this.direccionBeanModel.setSiEstado(null);
        clearComponent("formPopupCreateSiEstado", "inpTxtNombre");
        this.sesion.getControladorPopups().put("popupCreateSiEstado", Boolean.FALSE);
    }
    
    public void openPopupUpdateSiEstado() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(this.direccionBeanModel.getSiPaisById(this.direccionBeanModel.getId()));
        this.direccionBeanModel.setSiEstado((SiEstado) this.direccionBeanModel.getDataModel().getRowData());
        this.sesion.getControladorPopups().put("popupUpdateSiEstado", Boolean.TRUE);
    }

    public void closePopupUpdateSiEstado() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(null);
        this.direccionBeanModel.setSiEstado(null);
        clearComponent("formPopupUpdateSiEstado", "inpTxtNombre");
        this.sesion.getControladorPopups().put("popupUpdateSiEstado", Boolean.FALSE);
    }    
    //>>>>>>>>>>>>>>>>>> Catálogo SiEstado - END <<<<<<<<<<<<<<<<<   
    
    //>>>>>>>>>>>>>>>>>> Catálogo SiCiudad - START <<<<<<<<<<<<<<<<< 
    public DataModel getAllSiCiudadDataModel() {
        return this.direccionBeanModel.getDataModel();
    }
    
    public void saveSiCiudad() {
        try {
            this.direccionBeanModel.saveSiCiudad();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siCiudad") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
            closePopupCreateSiCiudad();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupCreateSiCiudad:msgsPopupCreateSiCiudad", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SiCiudad)eie.getElemento()).getNombre());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupCreateSiCiudad:msgsPopupCreateSiCiudad", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateSiCiudad() {
        try {
            this.direccionBeanModel.updateSiCiudad();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siCiudad") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
            closePopupUpdateSiEstado();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("formPopupUpdateSiCiudad:msgsPopupUpdateSiCiudad", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SiCiudad)eie.getElemento()).getNombre());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
            this.direccionBeanModel.reloadAllSiCiudad();
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupUpdateSiCiudad:msgsPopupUpdateSiCiudad", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteSiCiudad() {
        try {
            this.direccionBeanModel.setSiCiudad((SiCiudad) this.direccionBeanModel.getDataModel().getRowData());
            this.direccionBeanModel.deleteSiCiudad();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("siCiudad") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
        } catch (ItemUsedBySystemException iue) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(iue.getLiteral()));
            UtilLog4j.log.fatal(this, iue.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }   
    
    public void openPopupCreateSiCiudad() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        if (this.direccionBeanModel.getId() <= 0) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("siEstado.mensaje.error.seleccionarSiPais"));
        } else if(this.direccionBeanModel.getIdSiEstado() <= 0) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("siEstado.mensaje.error.seleccionarSiEstado"));
        }
        else {
            this.direccionBeanModel.setSiPais(this.direccionBeanModel.getSiPaisById(this.direccionBeanModel.getId()));
            this.direccionBeanModel.setSiEstado(this.direccionBeanModel.getSiEstadoById(this.direccionBeanModel.getIdSiEstado()));
            this.direccionBeanModel.setSiCiudad(new SiCiudad());
            this.sesion.getControladorPopups().put("popupCreateSiCiudad", Boolean.TRUE);
        }
    }

    public void closePopupCreateSiCiudad() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(null);
        this.direccionBeanModel.setSiEstado(null);
        this.direccionBeanModel.setSiCiudad(null);
        clearComponent("formPopupCreateSiCiudad", "inpTxtNombre");
        this.sesion.getControladorPopups().put("popupCreateSiCiudad", Boolean.FALSE);
    }

    public void openPopupUpdateSiCiudad() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(this.direccionBeanModel.getSiPaisById(this.direccionBeanModel.getId()));
        this.direccionBeanModel.setSiEstado(this.direccionBeanModel.getSiEstadoById(this.direccionBeanModel.getIdSiEstado()));
        this.direccionBeanModel.setSiCiudad((SiCiudad) this.direccionBeanModel.getDataModel().getRowData());
        this.sesion.getControladorPopups().put("popupUpdateSiCiudad", Boolean.TRUE);
    }

    public void closePopupUpdateSiCiudad() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        this.direccionBeanModel.setSiPais(null);
        this.direccionBeanModel.setSiEstado(null);
        this.direccionBeanModel.setSiCiudad(null);
        clearComponent("formPopupUpdateSiCiudad", "inpTxtNombre");
        this.sesion.getControladorPopups().put("popupUpdateSiCiudad", Boolean.FALSE);
    }    
    //>>>>>>>>>>>>>>>>>> Catálogo SiCiudad - END <<<<<<<<<<<<<<<<<       

    /**
     * @return the siPais
     */
    public SiPais getSiPais() {
        return this.direccionBeanModel.getSiPais();
    }

    /**
     * @param siPais the siPais to set
     */
    public void setSiPais(SiPais siPais) {
        this.direccionBeanModel.setSiPais(siPais);
    }

    /**
     * @return the siEstado
     */
    public SiEstado getSiEstado() {
        return this.direccionBeanModel.getSiEstado();
    }

    /**
     * @param siEstado the siEstado to set
     */
    public void setSiEstado(SiEstado siEstado) {
        this.direccionBeanModel.setSiEstado(siEstado);
    }

    /**
     * @return the siCiudad
     */
    public SiCiudad getSiCiudad() {
        return this.direccionBeanModel.getSiCiudad();
    }

    /**
     * @param siCiudad the siCiudad to set
     */
    public void setSiCiudad(SiCiudad siCiudad) {
        this.direccionBeanModel.setSiCiudad(siCiudad);
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return this.direccionBeanModel.getId();
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.direccionBeanModel.setId(id);
    }    
    
    /**
     * @return the selectItemList
     */
    public List<SelectItem> getSelectItemList() {
        return this.direccionBeanModel.getSelectItemList();
    }

    /**
     * @param selectItemList the selectItemList to set
     */
    public void setSelectItemList(List<SelectItem> selectItemList) {
        this.direccionBeanModel.setSelectItemList(selectItemList);
    }    
    
    /**
     * @return the idSiEstado
     */
    public int getIdSiEstado() {
        return this.direccionBeanModel.getIdSiEstado();
    }

    /**
     * @param idSiEstado the idSiEstado to set
     */
    public void setIdSiEstado(int idSiEstado) {
        this.direccionBeanModel.setIdSiEstado(idSiEstado);
    }

    /**
     * @param direccionBeanModel the direccionBeanModel to set
     */
    public void setDireccionBeanModel(DireccionBeanModel direccionBeanModel) {
        this.direccionBeanModel = direccionBeanModel;
    }
}
