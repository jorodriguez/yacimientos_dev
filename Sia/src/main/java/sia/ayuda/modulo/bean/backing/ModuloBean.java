/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ayuda.modulo.bean.backing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import javax.faces.model.DataModel;
import sia.ayuda.modulo.bean.model.ModuloBeanModel;
import sia.excepciones.SIAException;
import sia.modelo.SiModulo;
import sia.modelo.SiOpcion;
import sia.sistema.bean.backing.GenericPanelPopup;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@ManagedBean (name="moduloBean")
@ViewScoped
public class ModuloBean {

    //ManagedBeans
    @ManagedProperty(value = "#{moduloBeanModel}")
    private ModuloBeanModel moduloBeanModel;    
    @ManagedProperty(value = "#{genericPanelPopup}")
    private GenericPanelPopup popup;
    
    public void goToCatalogoModulos() {
//        getModuloBeanModel().beginConversacionCatalogoModulos();
        //return "/vistas/administracion/modulo/catalogoModulos";
    }         
    
    public DataModel<SiModulo> getModulosDataModel() {
        getModuloBeanModel().getAllModulos();
        return getModuloBeanModel().getModulosDataModel();
    }
    
    public DataModel<SiOpcion> getAllOpcionesFiltradasByModulo() {
        return getModuloBeanModel().getAllOpcionesFiltradasByModulo();
    }
    
    public String mostrarDetallesModulo() {
        getModuloBeanModel().setModuloSeleccionado((SiModulo)getModulosDataModel().getRowData());
        return "/vistas/administracion/modulo/detalleModulo";
    }
    
    public void mostrarPopupCrear() {
        getPopup().toogleModalCrear(actionEvent);
    }
    
    public void mostrarPopupActualizar() {
        SiModulo moduloSeleccionado = (SiModulo)getModulosDataModel().getRowData();
        getModuloBeanModel().setModuloSeleccionado(moduloSeleccionado);
        getModuloBeanModel().setNombreModulo(moduloSeleccionado.getNombre());
        getModuloBeanModel().setRutaModulo(moduloSeleccionado.getRuta());
        getPopup().toogleModalActualizar(actionEvent);
    }
    
    public void mostrarPopupEliminar() {
        getModuloBeanModel().setModuloSeleccionado((SiModulo)getModulosDataModel().getRowData());
        getPopup().toogleModalElimnar(actionEvent);
    }
    
    public void crearModulo() {
        if (getModuloBeanModel().getNombreModulo() != null && !moduloBeanModel.getNombreModulo().equals("")) {
            if (getModuloBeanModel().getRutaModulo() != null && !moduloBeanModel.getRutaModulo().equals("")) {
                try {
                    getModuloBeanModel().crearModulo(getModuloBeanModel().getNombreModulo(), getModuloBeanModel().getRutaModulo());
                    cancelarCrearModulo(actionEvent);
                } catch (Exception e) {
                    UtilLog4j.log.fatal(this, "Mensaje Exception: " + e.getMessage());
                    if(e.getMessage() == null || e.getMessage().equals("")) {
                        FacesUtils.addInfoMessage(new SIAException().getMessage());
                    }
                    else {
                        FacesUtils.addInfoMessage(e.getMessage());
                    }
                }
            } else {
                FacesUtils.addInfoMessage("La ruta del módulo no puede ser vacía");
            }
        } else {
            FacesUtils.addInfoMessage("El nombre del módulo no puede ser vacío");
        }
    }
    
    public void actualizarModulo() {
        if (getModuloBeanModel().getNombreModulo() != null && !moduloBeanModel.getNombreModulo().equals("")) {
            if (getModuloBeanModel().getRutaModulo() != null && !moduloBeanModel.getRutaModulo().equals("")) {
                try {
                    getModuloBeanModel().actualizarModulo();
                    cancelarActualizarModulo(actionEvent);
                } catch (Exception e) {
                    UtilLog4j.log.fatal(this, "Mensaje Exception: " + e.getMessage());
                    if(e.getMessage() == null || e.getMessage().equals("")) {
                        FacesUtils.addInfoMessage(new SIAException().getMessage());
                    }
                    else {
                        FacesUtils.addInfoMessage(e.getMessage());
                    }
                }
            } else {
                FacesUtils.addInfoMessage("La ruta del módulo no puede ser vacía");
            }
        } else {
            FacesUtils.addInfoMessage("El nombre del módulo no puede ser vacío");
        }
    }
    
    public void eliminarModulo() {
        try {
            getModuloBeanModel().eliminarModulo(getModuloBeanModel().getModuloSeleccionado());
            getModuloBeanModel().setModuloSeleccionado(null);
            cancelarEliminarModulo(actionEvent);
        }
        catch(Exception e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }
    
    public void cancelarCrearModulo() {
        getModuloBeanModel().setNombreModulo(null);
        getModuloBeanModel().setRutaModulo(null);
        getPopup().toogleModalCrear(actionEvent);
    }
    
    public void cancelarActualizarModulo() {
        getModuloBeanModel().setNombreModulo(null);
        getModuloBeanModel().setRutaModulo(null);
        getPopup().toogleModalActualizar(actionEvent);
    }
    
    public void cancelarEliminarModulo() {
        getModuloBeanModel().setModuloSeleccionado(null);
        getPopup().toogleModalElimnar(actionEvent);
    }
    
    /**
     * @return the nombreModulo
     */
    public String getNombreModulo() {
        return getModuloBeanModel().getNombreModulo();
    }

    /**
     * @param nombreModulo the nombreModulo to set
     */
    public void setNombreModulo(String nombreModulo) {
        getModuloBeanModel().setNombreModulo(nombreModulo);
    }
    
    /**
     * @return the rutaModulo
     */
    public String getRutaModulo() {
        return getModuloBeanModel().getRutaModulo();
    }

    /**
     * @param rutaModulo the rutaModulo to set
     */
    public void setRutaModulo(String rutaModulo) {
        getModuloBeanModel().setRutaModulo( rutaModulo);
    }
    
    /**
     * @return the moduloSeleccionado
     */
    public SiModulo getModuloSeleccionado() {
        return getModuloBeanModel().getModuloSeleccionado();
    }

    /**
     * @return the popup
     */
    public GenericPanelPopup getPopup() {
        return popup;
    }

    /**
     * @param popup the popup to set
     */
    public void setPopup(GenericPanelPopup popup) {
        this.popup = popup;
    }

    /**
     * @return the moduloBeanModel
     */
    public ModuloBeanModel getModuloBeanModel() {
        return moduloBeanModel;
    }

    /**
     * @param moduloBeanModel the moduloBeanModel to set
     */
    public void setModuloBeanModel(ModuloBeanModel moduloBeanModel) {
        this.moduloBeanModel = moduloBeanModel;
    }

}
