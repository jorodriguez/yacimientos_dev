/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.CustomScoped;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sia.modelo.OcActivoFijo;
import sia.modelo.sgl.vo.OrdenDetalleVO;

/**
 *
 * @author ihsa
 */

@Named (value= "popupCompletarActivoFijoBean")
@ViewScoped
public class PopupCompletarActivoFijoBean implements Serializable{

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupCompletarActivoFijoBean";
    //------------------------------------------------------

    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;
    private OrdenDetalleVO linea;
    private List<OcActivoFijo> navCodes;
    private int codesSize;
    private int popUpocProductoID;
    private List<SelectItem> ocProductos;
    private boolean applyAll = false;
    private String afValue;
    


    /** Creates a new instance of PopupCancelarOrden */
    public PopupCompletarActivoFijoBean() {
    }
    
    /** Creates a new instance of PopupCancelarOrden */
    public PopupCompletarActivoFijoBean(List<OcActivoFijo> codes) {
        this.navCodes = codes;
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
     * @return the navCodes
     */
    public List<OcActivoFijo> getNavCodes() {
        return navCodes;
    }

    /**
     * @param navCodes the navCodes to set
     */
    public void setNavCodes(List<OcActivoFijo> navCodes) {
        this.navCodes = navCodes;
    }

    /**
     * @return the codesSize
     */
    public int getCodesSize() {
        return codesSize;
    }

    /**
     * @return the linea
     */
    public OrdenDetalleVO getLinea() {
        return linea;
    }

    /**
     * @param linea the linea to set
     */
    public void setLinea(OrdenDetalleVO linea) {
        this.linea = linea;
    }

    /**
     * @return the PopUpocProductoID
     */
    public int getPopUpocProductoID() {
        return popUpocProductoID;
    }

    /**
     * @param PopUpocProductoID the PopUpocProductoID to set
     */
    public void setPopUpocProductoID(int popUpocProductoID) {
        this.popUpocProductoID = popUpocProductoID;
    }

    /**
     * @return the ocProductos
     */
    public List<SelectItem> getOcProductos() {
        return ocProductos;
    }

    /**
     * @param ocProductos the ocProductos to set
     */
    public void setOcProductos(List<SelectItem> ocProductos) {
        this.ocProductos = ocProductos;
    }

    /**
     * @return the applyAll
     */
    public boolean isApplyAll() {
        return applyAll;
    }

    /**
     * @param applyAll the applyAll to set
     */
    public void setApplyAll(boolean applyAll) {
        this.applyAll = applyAll;
    }

    /**
     * @return the afValue
     */
    public String getAfValue() {
        return afValue;
    }

    /**
     * @param afValue the afValue to set
     */
    public void setAfValue(String afValue) {
        this.afValue = afValue;
    }
}
