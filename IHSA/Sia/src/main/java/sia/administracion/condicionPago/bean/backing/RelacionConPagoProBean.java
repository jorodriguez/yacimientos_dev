/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.condicionPago.bean.backing;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.model.DataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.administracion.condicionPago.bean.model.RelacionConPagoProModel;
import sia.modelo.orden.vo.ProveedorConPagoVo;
import sia.sistema.bean.backing.GenericPanelPopup;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author icristobal
 */
@Named
@ViewScoped
public class RelacionConPagoProBean implements Serializable {

    @Inject
    private RelacionConPagoProModel relacionConPagoProModel;
    @Inject
    private GenericPanelPopup popup;

    public RelacionConPagoProBean() {

    }

    public void entrar() {
        popup.setModalRenderedEliminar(false);
    }

    public void mostrarPopupEliminar() {
        ProveedorConPagoVo proveedorSeleccionado = (ProveedorConPagoVo) getListData().getRowData();
        relacionConPagoProModel.setProveedorConPagoVo(proveedorSeleccionado);
        getPopup().toogleModalElimnar();
    }

    public void eliminarRelacion() {
        try {
            this.relacionConPagoProModel.eliminarRelacion();
            FacesUtils.addInfoMessage("La relación con el proveedor " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
            cancelarEliminarRelacion();

        } catch (Exception ex) {
            Logger.getLogger(CondicionPagoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cancelarEliminarRelacion() {
        relacionConPagoProModel.setProveedorConPagoVo(null);
        getPopup().toogleModalElimnar();
    }

    public RelacionConPagoProModel getRelacionConPagoProModel() {
        return relacionConPagoProModel;
    }

    public void setRelacionConPagoProModel(RelacionConPagoProModel relacionConPagoProModel) {
        this.relacionConPagoProModel = relacionConPagoProModel;
    }

    public DataModel getListData() {
        return relacionConPagoProModel.getListData();
    }

    public void setListData(DataModel listData) {
        this.relacionConPagoProModel.setListData(listData);
    }

    public GenericPanelPopup getPopup() {
        return popup;
    }

    public void setPopup(GenericPanelPopup popup) {
        this.popup = popup;
    }

}
