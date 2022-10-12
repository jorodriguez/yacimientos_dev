/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import javax.faces.bean.CustomScoped;
import javax.inject.Named;


/**
 *
 * @author Héctor
 */
@Named (value = PopupGeneralBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PopupGeneralBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupGeneralBean";
    //------------------------------------------------------

    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    private boolean modalRenderedOlvidoClave = false;
    private boolean modalRenderedMensaje = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;

    /**
     * Creates a new instance of PopupGeneralBean
     */
    public PopupGeneralBean() {
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

    public void toggleModalMensaje() {
        modalRenderedMensaje = !modalRenderedMensaje;
    }

    public void toggleModalOlvidoClave() {
        modalRenderedOlvidoClave = !modalRenderedOlvidoClave;
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

    /**
     * @return the modalRenderedMensaje
     */
    public boolean isModalRenderedMensaje() {
        return modalRenderedMensaje;
    }

    /**
     * @param modalRenderedMensaje the modalRenderedMensaje to set
     */
    public void setModalRenderedMensaje(boolean modalRenderedMensaje) {
        this.modalRenderedMensaje = modalRenderedMensaje;
    }

    /**
     * @return the modalRenderedOlvidoClave
     */
    public boolean isModalRenderedOlvidoClave() {
        return modalRenderedOlvidoClave;
    }

    /**
     * @param modalRenderedOlvidoClave the modalRenderedOlvidoClave to set
     */
    public void setModalRenderedOlvidoClave(boolean modalRenderedOlvidoClave) {
        this.modalRenderedOlvidoClave = modalRenderedOlvidoClave;
    }

}
