/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ayuda.modulo.bean.model;

import java.io.Serializable;
import javax.inject.Inject;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.SiModulo;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author sluis
 */
//@Named
//@ConversationScoped
@Named
@ViewScoped
public class ModuloBeanModel implements Serializable {

//    @Inject
//    private Conversation conversation;
//    @Inject
//    private ConversationsManager conversationsManager;
    @Inject
    private SiModuloImpl moduloService;
    @Inject
    private SiOpcionImpl opcionService;
    private Sesion sesion;
    private DataModel<SiModulo> modulosDataModel = null;
    private String nombreModulo;
    private String rutaModulo;
    private SiModulo moduloSeleccionado;
    
    public ModuloBeanModel () {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        //Limpiando variables por si acaso
        this.modulosDataModel = null;
        this.nombreModulo = null;
        this.rutaModulo = null;
        this.moduloSeleccionado = null;
    
    }
            
//    public void beginConversacionCatalogoModulos() ć
//        this.conversationsManager.finalizeAllConversations();
//        this.conversationsManager.beginConversation(this.conversation, Constantes.CONVERSACION_CATALOGO_MODULOS);        
//    }
    
    public void reloadAllModulos() {
        this.modulosDataModel = (DataModel) new ListDataModel(moduloService.getAllModulosByEstado(Constantes.NO_ELIMINADO));
    }

    public void getAllModulos() {
        if (this.modulosDataModel == null) {
            this.modulosDataModel = (DataModel) new ListDataModel(moduloService.getAllModulosByEstado(Constantes.NO_ELIMINADO));
        }
    }

    public void crearModulo(String nombreModulo, String ruta) throws Exception {
        this.moduloService.crearModulo(nombreModulo, ruta, sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
        this.reloadAllModulos();
    }

    public void actualizarModulo() throws Exception {
        this.moduloService.actualizarModulo(this.getModuloSeleccionado(), this.getNombreModulo(), this.getRutaModulo(), sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
    }

    public void eliminarModulo(SiModulo modulo) throws Exception {
        this.moduloService.eliminarModulo(modulo, sesion.getUsuario().getId(), Constantes.ELIMINADO);
        reloadAllModulos();
    }

    /**
     * @return the modulosDataModel
     */
    public DataModel<SiModulo> getModulosDataModel() {
        return this.modulosDataModel;
    }

    public DataModel getAllOpcionesFiltradasByModulo() {
        ListDataModel listOpciones = new ListDataModel(opcionService.getAllOpcionesByModulo(this.moduloSeleccionado.getNombre(), Constantes.NO_ELIMINADO));
        return (DataModel) listOpciones;
    }

    /**
     * @return the nombreModulo
     */
    public String getNombreModulo() {
        return nombreModulo;
    }

    /**
     * @param nombreModulo the nombreModulo to set
     */
    public void setNombreModulo(String nombreModulo) {
        this.nombreModulo = nombreModulo;
    }

    /**
     * @return the rutaModulo
     */
    public String getRutaModulo() {
        return rutaModulo;
    }

    /**
     * @param rutaModulo the rutaModulo to set
     */
    public void setRutaModulo(String rutaModulo) {
        this.rutaModulo = rutaModulo;
    }

    /**
     * @return the moduloSeleccionado
     */
    public SiModulo getModuloSeleccionado() {
        return moduloSeleccionado;
    }

    /**
     * @param moduloSeleccionado the moduloSeleccionado to set
     */
    public void setModuloSeleccionado(SiModulo moduloSeleccionado) {
        this.moduloSeleccionado = moduloSeleccionado;
    }

    /**
     * @return the conversation
     */
//    public Conversation getConversation() {
//        return conversation;
//    }
//
//    /**
//     * @param conversation the conversation to set
//     */
//    public void setConversation(Conversation conversation) {
//        this.conversation = conversation;
//    }

}
