/*
 * PopupCrearContactoBean.java
 * Creado el 16/10/2009, 02:21:08 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.orden.bean.backing;

import javax.faces.bean.CustomScoped;

import javax.inject.Named;
import sia.modelo.ContactoProveedor;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 16/10/2009
 */
@Named (value = PopupCrearContactoBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PopupCrearContactoBean {

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupCrearContactoBean";
    //------------------------------------------------------

    // Esto es para mostrar los datos en el panel emergente si no no muestra nada y marca error
    private ContactoProveedor contactoProveedor = new ContactoProveedor();

    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRenderedModificar = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;

    /**
     * Creates a new instance of PopupCrearContactoProveedorBean
     */
    public PopupCrearContactoBean() {
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

    public void toggleModalModificar() {
        modalRenderedModificar = !modalRenderedModificar;
    }

    /**
     * @return the modalRenderedModificar
     */
    public boolean getModalRenderedModificar() {
        return modalRenderedModificar;
    }

    /**
     * @param modalRenderedModificar the modalRenderedModificar to set
     */
    public void setModalRenderedModificar(boolean modalRenderedModificar) {
        this.modalRenderedModificar = modalRenderedModificar;
    }

    /**
     * @return the contactoProveedor
     */
    public ContactoProveedor getContactoProveedor() {
        return contactoProveedor;
    }

    /**
     * @param contactoProveedor the contactoProveedor to set
     */
    public void setContactoProveedor(ContactoProveedor contactoProveedor) {
        this.contactoProveedor = contactoProveedor;
    }

}
