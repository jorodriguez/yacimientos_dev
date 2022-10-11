/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sia.sistema.bean.backing;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;


/**
 *
 * @author hacosta
 */
@Named(value="panelPopup")
@ViewScoped
public class PanelPopup implements Serializable{
    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    private boolean mrGrupo = false;
    private boolean mrEliminarGrupo = false;

    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;
    /** Creates a new instance of PanelPopup */
    public PanelPopup() {
    }
    
    public void toggleModal() {
        setModalRendered(!isModalRendered());
    }
    public void tmGrupo() {
        setMrGrupo(!isMrGrupo());
    }
    public void tmElimnarGrupo(){
        setMrEliminarGrupo(!isMrEliminarGrupo());
    } 
    /**
     * @return the draggableRendered
     */
    public boolean isDraggableRendered() {
        return draggableRendered;
    }

    /**
     * @param draggableRendered the draggableRendered to set
     */
    public void setDraggableRendered(boolean draggableRendered) {
        this.draggableRendered = draggableRendered;
    }

    /**
     * @return the modalRendered
     */
    public boolean isModalRendered() {
        return modalRendered;
    }

    /**
     * @param modalRendered the modalRendered to set
     */
    public void setModalRendered(boolean modalRendered) {
        this.modalRendered = modalRendered;
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
     * @return the mrGrupo
     */
    public boolean isMrGrupo() {
        return mrGrupo;
    }

    /**
     * @param mrGrupo the mrGrupo to set
     */
    public void setMrGrupo(boolean mrGrupo) {
        this.mrGrupo = mrGrupo;
    }

    /**
     * @return the mrEliminarGrupo
     */
    public boolean isMrEliminarGrupo() {
        return mrEliminarGrupo;
    }

    /**
     * @param mrEliminarGrupo the mrEliminarGrupo to set
     */
    public void setMrEliminarGrupo(boolean mrEliminarGrupo) {
        this.mrEliminarGrupo = mrEliminarGrupo;
    }

}
