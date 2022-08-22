/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.requisicion.bean.backing;

import javax.faces.bean.CustomScoped;
import javax.inject.Named;


/**
 *
 * @author hacosta
 */
@Named (value = PopupOlvidoClave.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PopupOlvidoClave {

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupOlvidoClave";
    //------------------------------------------------------

    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;

    /**
     * Creates a new instance of PopupOlvidoClave
     */
    public PopupOlvidoClave() {
    }

    public boolean getDraggableRendered() {
        return draggableRendered;
    }

    public void setDraggableRendered(boolean draggableRendered) {
        this.draggableRendered = draggableRendered;
    }

    public boolean getAutoCentre() {
        return autoCentre;
    }

    public void setAutoCentre(boolean autoCentre) {
        this.autoCentre = autoCentre;
    }

    public void toggleModal() {
        modalRendered = !modalRendered;
    }

    /**
     * @return the modalRenderedModificar
     */
    public boolean getModalRendered() {
        return modalRendered;
    }

    /**
     * @param modalRendered     
     */
    public void setModalRendered(boolean modalRendered) {
        this.modalRendered = modalRendered;
    }

}
