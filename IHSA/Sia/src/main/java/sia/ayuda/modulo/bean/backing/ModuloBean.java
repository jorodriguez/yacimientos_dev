/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ayuda.modulo.bean.backing;





import java.io.Serializable;
import javax.faces.model.DataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
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
@Named
@ViewScoped
public class ModuloBean implements Serializable{

    //ManagedBeans
    @Inject
    private ModuloBeanModel moduloBeanModel;    
    @Inject
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
        getPopup().toogleModalCrear();
    }
    
    public void mostrarPopupActualizar() {
        SiModulo moduloSeleccionado = (SiModulo)getModulosDataModel().getRowData();
        getModuloBeanModel().setModuloSeleccionado(moduloSeleccionado);
        getModuloBeanModel().setNombreModulo(moduloSeleccionado.getNombre());
        getModuloBeanModel().setRutaModulo(moduloSeleccionado.getRuta());
        getPopup().toogleModalActualizar();
    }
    
    public void mostrarPopupEliminar() {
        getModuloBeanModel().setModuloSeleccionado((SiModulo)getModulosDataModel().getRowData());
        getPopup().toogleModalElimnar();
    }
    
    public void crearModulo() {
        if (getModuloBeanModel().getNombreModulo() != null && !moduloBeanModel.getNombreModulo().equals("")) {
            if (getModuloBeanModel().getRutaModulo() != null && !moduloBeanModel.getRutaModulo().equals("")) {
                try {
                    getModuloBeanModel().crearModulo(getModuloBeanModel().getNombreModulo(), getModuloBeanModel().getRutaModulo());
                    cancelarCrearModulo();
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
                    cancelarActualizarModulo();
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
            cancelarEliminarModulo();
        }
        catch(Exception e) {
            FacesUtils.addInfoMessage(e.getMessage());
        }
    }
    
    public void cancelarCrearModulo() {
        getModuloBeanModel().setNombreModulo(null);
        getModuloBeanModel().setRutaModulo(null);
        getPopup().toogleModalCrear();
    }
    
    public void cancelarActualizarModulo() {
        getModuloBeanModel().setNombreModulo(null);
        getModuloBeanModel().setRutaModulo(null);
        getPopup().toogleModalActualizar();
    }
    
    public void cancelarEliminarModulo() {
        getModuloBeanModel().setModuloSeleccionado(null);
        getPopup().toogleModalElimnar();
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
