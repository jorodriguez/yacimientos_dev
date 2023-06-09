/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.sistema.bean.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;



/**
 *
 */
@Named
@ViewScoped
public class GenericPanelPopup implements Serializable {
    
       
    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    private boolean modalRenderedCrear = false;
    private boolean modalRenderedActualizar = false;
    private boolean modalRenderedEliminar = false;
    private boolean modalRenderedAgregar = false;
    
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;
    /** Creates a new instance of GenericPanelPopup */
    public GenericPanelPopup() {
        
    }
    
    
    /**
     * Toogle to Modal the flag 'modalRendered'
     * @param actionEvent 
     */        
    public void toggleModal() {
        setModalRendered(!isModalRendered());
    }
    
    /**
     * Toogle to Modal the flag 'modalRenderedCrear'
     * @param actionEvent 
     */    
    public void toogleModalCrear() {        
        setModalRenderedCrear(!isModalRenderedCrear());
    }
    
    /**
     * Toogle to Modal the flag 'modalRenderedActualizar'
     * @param actionEvent 
     */
    public void toogleModalActualizar(){
        setModalRenderedActualizar(!isModalRenderedActualizar());
    }    
    
    /**
     * Toogle to Modal the flag 'mrEliminarModulo'
     * @param actionEvent 
     */
    public void toogleModalElimnar(){
        setModalRenderedEliminar(!isModalRenderedEliminar());
    } 
    
    /**
     * @author: icristobal
     * @param actionEvent 
     */
    public void toogleModalAgregar(){
        setModalRenderedAgregar(!isModalRenderedAgregar());
    } 

    /**
     * @param modalRendered the modalRendered to set
     */
    public void setModalRendered(boolean modalRendered) {
        this.modalRendered = modalRendered;
    }    
    
    /**
     * @param modalRenderedCrear the modalRenderedCrear to set
     */
    public void setModalRenderedCrear(boolean modalRenderedCrear) {
        this.modalRenderedCrear = modalRenderedCrear;
    }
    
   /**
     * @param modalRenderedActualizar the modalRenderedActualizar to set
     */
    public void setModalRenderedActualizar(boolean modalRenderedActualizar) {
        this.modalRenderedActualizar = modalRenderedActualizar;
    }    
    
    /**
     * @param modalRenderedEliminar the modalRenderedEliminar to set
     */
    public void setModalRenderedEliminar(boolean modalRenderedEliminar) {
        this.modalRenderedEliminar = modalRenderedEliminar;
    }
    
    /**
     * @author: icristobal
     * @param modalRenderedAgregar the modalRenderedAgregar to set
     */
    public void setModalRenderedAgregar(boolean modalRenderedAgregar) {
        this.modalRenderedAgregar = modalRenderedAgregar;
    }
    
    /**
     * Return the boolean value flag 'modalRendered'
     * @return
     */
    public boolean isModalRendered() {
        return modalRendered;
    }
    
    /**
     * Return the boolean value flag 'modalRenderedCrear'
     * @return
     */
    public boolean isModalRenderedCrear() {
        return modalRenderedCrear;
    }
    
    /**
     * Return the boolean value flag 'modalRenderedActualizar'
     * @return 
     */
    public boolean isModalRenderedActualizar(){
        return modalRenderedActualizar;
    }
    
    /**
     * Returnthe boolean value flag 'modalRenderedEliminar'
     * @return
     */
    public boolean isModalRenderedEliminar() {
        return modalRenderedEliminar;
    }
    
    /**
     * @author: icristobal
     * Returnthe boolean value flag 'modalRenderedAgregar'
     * @return
     */
    public boolean isModalRenderedAgregar() {
        return modalRenderedAgregar;
    }

    /**
     * @return the autoCentre
     */
    public boolean isAutoCentre() {
        return autoCentre;
    }

    /**
     * @param autoCentre the autoCentre to set
     */
    public void setAutoCentre(boolean autoCentre) {
        this.autoCentre = autoCentre;
    }

    /**
     * @param draggableRendered the draggableRendered to set
     */
    public void setDraggableRendered(boolean draggableRendered) {
        this.draggableRendered = draggableRendered;
    }        
    
    /**
     * @return the draggableRendered
     */
    public boolean isDraggableRendered() {
        return draggableRendered;
    }

}
