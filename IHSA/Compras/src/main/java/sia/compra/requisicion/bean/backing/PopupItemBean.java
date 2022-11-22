/*
 * PopupItemBean.java
 * Creado el 16/07/2009, 11:37:28 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import javax.faces.bean.CustomScoped;
import javax.inject.Named;


/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 16/07/2009@Named (value="popupItemBean")
 * @CustomScoped(value = "#{window}")
 */
@Named (value = PopupItemBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PopupItemBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupItemBean";
    //------------------------------------------------------

    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;

    /**
     * Creates a new instance of PopupItemBean
     */
    public PopupItemBean() {
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
     * @return the modalRendered
     */
    public boolean getModalRendered() {
        return modalRendered;
    }

    /**
     * @param modalRendered the modalRendered to set
     */
    public void setModalRendered(boolean modalRendered) {
        this.modalRendered = modalRendered;
    }

}
