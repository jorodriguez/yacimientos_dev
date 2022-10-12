/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import javax.faces.bean.CustomScoped;

import javax.inject.Named;

/**
 *
 * @author ihsa
 */

@Named (value= PopupCompletarOrdenBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PopupCompletarOrdenBean  implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupCompletarOrdenBean";
    //------------------------------------------------------

    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;
    private String navCode = "";


    /** Creates a new instance of PopupCancelarOrden */
    public PopupCompletarOrdenBean() {
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
        modalRendered =!modalRendered;
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

    /**
     * @return the navCode
     */
    public String getNavCode() {
        return navCode;
    }

    /**
     * @param navCode the navCode to set
     */
    public void setNavCode(String navCode) {
        this.navCode = navCode;
    }


}
