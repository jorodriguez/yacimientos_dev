/*
 * PopupRespuestaNotaOrden.java
 * Creado el 9/12/2009, 06:33:58 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import javax.faces.bean.CustomScoped;

import javax.inject.Named;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 9/12/2009
 */
@Named (value=PopupRespuestaNotaBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PopupRespuestaNotaBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupRespuestaNotaBean";
    //------------------------------------------------------

    
    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;

    /** Creates a new instance of PopupRespuestaNotaOrden */
    public PopupRespuestaNotaBean() {
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
     * @param modalRenderedModificar the modalRenderedModificar to set
     */
    public void setModalRendered(boolean modalRendered) {
        this.modalRendered = modalRendered;
    }
}
