/*
 * PopupBean.java
 * Creado el 29/06/2009, 10:27:13 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;



import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.Rechazo;
import sia.modelo.Requisicion;
import sia.modelo.RequisicionDetalle;


/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 29/06/2009
 */
@Named (value=PopupBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PopupBean implements Serializable {
    
    //------------------------------------------------------
    public static final String BEAN_NAME = "popupBean";
    //------------------------------------------------------

    
    
    @Inject
    private UsuarioBean usuarioBean;
    // Esto es para mostrar los datos en el panel emergente si no no muestra nada y marca error
    private Requisicion requisicion             = new Requisicion();
    private RequisicionDetalle item             = new RequisicionDetalle();
    private Rechazo rechazo                     = new Rechazo();
    private List<SelectItem> listaCategorias;
    private boolean cambiarDpto = false;
    // render flags for both dialogs
    private boolean draggableRendered           = false;
    private boolean modalRenderedModificar      = false;
    private boolean modalRenderedCancelar       = false;
    private boolean modalRenderedDevolver       = false;
    private boolean modalRenderedCategoria      = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre                  = false;
    private String  analista;

        public PopupBean(){
    }

    public void validarMotivoRechazo(FacesContext context, UIComponent validate, Object value){
        String motivoRechazo = (String) value;

        if (motivoRechazo.trim().equals("")){
               ((UIInput)validate).setValid(false);
               FacesMessage msg = new FacesMessage("El valor es necesario...");
               context.addMessage(validate.getClientId(context), msg);
        }
    }
    
    //--- Lista de Analistas
    

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

    public void toggleModalCancelar() {
        modalRenderedCancelar = !modalRenderedCancelar;
    }

    public void toggleModalDevolver() {
//        this.requisicionBean.setError(false);
        setModalRenderedDevolver(!isModalRenderedDevolver());
    }
    
    public void toggleModalCategoria() {
//        this.requisicionBean.setError(false);
        setModalRenderedCategoria(!isModalRenderedCategoria());
    }

    public void toggleModalModificar() {
        modalRenderedModificar =!modalRenderedModificar;
    }

    /**
     * @return the requisicion
     */
    public Requisicion getRequisicion() {
        return requisicion;
    }

    /**
     * @param requisicion the requisicion to set
     */
    public void setRequisicion(Requisicion requisicion) {
        this.requisicion = requisicion;
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
     * @return the modalRenderedCancelar
     */
    public boolean getModalRenderedCancelar() {
        return modalRenderedCancelar;
    }

    /**
     * @param modalRenderedCancelar the modalRenderedCancelar to set
     */
    public void setModalRenderedCancelar(boolean modalRenderedCancelar) {
        this.modalRenderedCancelar = modalRenderedCancelar;
    }

    /**
     * @return the item
     */
    public RequisicionDetalle getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(RequisicionDetalle item) {
        this.item = item;
    }

    /**
     * @return the analista
     */
    public String getAnalista() {
        return analista;
    }

    /**
     * @param analista the analista to set
     */
    public void setAnalista(String analista) {
        this.analista = analista;
    }

    /**
     * @return the rechazo
     */
    public Rechazo getRechazo() {
        return rechazo;
    }

    /**
     * @param rechazo the rechazo to set
     */
    public void setRechazo(Rechazo rechazo) {
        this.rechazo = rechazo;
    }

    /**
     * @return the modalRenderedDevolver
     */
    public boolean isModalRenderedDevolver() {
        return modalRenderedDevolver;
    }

    /**
     * @param modalRenderedDevolver the modalRenderedDevolver to set
     */
    public void setModalRenderedDevolver(boolean modalRenderedDevolver) {
        this.modalRenderedDevolver = modalRenderedDevolver;
    }

    /**
     * @return the modalRenderedCategoria
     */
    public boolean isModalRenderedCategoria() {
        return modalRenderedCategoria;
    }

    /**
     * @param modalRenderedCategoria the modalRenderedCategoria to set
     */
    public void setModalRenderedCategoria(boolean modalRenderedCategoria) {
        this.modalRenderedCategoria = modalRenderedCategoria;
    }

    /**
     * @return the cambiarDpto
     */
    public boolean isCambiarDpto() {
        return cambiarDpto;
    }

    /**
     * @param cambiarDpto the cambiarDpto to set
     */
    public void setCambiarDpto(boolean cambiarDpto) {
        this.cambiarDpto = cambiarDpto;
    }
    
    public String getDescItem(){
        String ret = "";
        if(this.item.getInvArticulo() != null){
            ret += this.item.getInvArticulo().getDescripcion();
            ret += " ";
            ret += this.item.getTextNav();
        } else {
            ret += this.item.getDescripcionSolicitante();
        } 
        return ret.toUpperCase();
    }

}