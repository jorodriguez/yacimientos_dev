/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.backing;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.model.DataModel;
import sia.administracion.condicionPago.bean.model.RelacionConPagoProModel;
import sia.catalogos.bean.model.CatalogoConPagoModel;
import sia.modelo.orden.vo.CondicionPagoVO;
import sia.sistema.bean.backing.GenericPanelPopup;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author icristobal
 */
@ManagedBean(name = "catalogoConPagoB")
@ViewScoped
public class CatalogoConPago {

    @ManagedProperty(value = "#{catalogoConPagoModel}")
    private CatalogoConPagoModel catalogoConPagoModel;
    @ManagedProperty(value = "#{relacionConPagoProModel}")
    private RelacionConPagoProModel relacionConPagoProModel;
    @ManagedProperty(value = "#{genericPanelPopup}")
    private GenericPanelPopup popup;

    public CatalogoConPago() {
    }

    public void entrar() {
        catalogoConPagoModel.setNomConPago(null);
        catalogoConPagoModel.setRfcCompania(catalogoConPagoModel.regresaRfcCompaniaSesion());
        catalogoConPagoModel.getAllConPago();
        popup.setModalRenderedCrear(false);
        popup.setModalRenderedActualizar(false);
        popup.setModalRenderedEliminar(false);
        popup.setModalRenderedAgregar(false);
    }
    

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
        UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }


    public String goToRelacionConPagoPro() {
        catalogoConPagoModel.setCondicionPagoVO((CondicionPagoVO) getListData().getRowData());
        relacionConPagoProModel.setNomConPago(catalogoConPagoModel.getCondicionPagoVO().getNombre());
        relacionConPagoProModel.getAllProveedorConPago();

        return "/vistas/administracion/relacionConPagoPro";
    }

    public void mostrarPopupCrear() {
        catalogoConPagoModel.setNomConPago(null);
        getPopup().toogleModalCrear(actionEvent);
    }

    public void mostrarPopupActualizar() {
        catalogoConPagoModel.setNomConPago(null);
        catalogoConPagoModel.setCondicionPagoVO((CondicionPagoVO) getListData().getRowData());
        //valida que no se haya usado        
        if (catalogoConPagoModel.listaOrdenesPorCondicionPago()) {
            getPopup().toogleModalActualizar(actionEvent);
        } else {
            FacesUtils.addErrorMessage("No se puede modificar una condición de pago que se ha utilizado en alguna OC/S");
        }


    }

    public void mostrarPopupEliminar() {
        catalogoConPagoModel.setNomConPago(null);
        catalogoConPagoModel.setCondicionPagoVO((CondicionPagoVO) getListData().getRowData());
        if (catalogoConPagoModel.listaOrdenesPorCondicionPago()) {
            getPopup().toogleModalElimnar(actionEvent);
        } else {
            FacesUtils.addErrorMessage("No se puede eliminar una condición de pago que se ha utilizado en alguna OC/S");
        }

    }

    public void crearConPago() {
        if (catalogoConPagoModel.getNomConPago().isEmpty()) {
            FacesUtils.addErrorMessage("formPopupCrearConPago:inpTxtnombreConPago", FacesUtils.getKeyResourceBundle("orden.conPago.requerido"));
        } else if (catalogoConPagoModel.buscarConPagoPorNombre() != null) {
            FacesUtils.addErrorMessage("formPopupCrearConPago:inpTxtnombreConPago", FacesUtils.getKeyResourceBundle("orden.conPago.existe.catalogo"));

        } else {
            try {
                catalogoConPagoModel.guardarAltaCondicion();
                FacesUtils.addInfoMessage("Condicion de Pago " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
                cancelarCrearConPago(actionEvent);

            } catch (Exception ex) {
                Logger.getLogger(CatalogoConPago.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void actualizarConPago() {
        catalogoConPagoModel.setNomConPago(catalogoConPagoModel.getCondicionPagoVO().getNombre());

        if (catalogoConPagoModel.getNomConPago().isEmpty()) {
            FacesUtils.addErrorMessage("formPopupActualizarConPago:inpTxtnombreConPagoEdit", FacesUtils.getKeyResourceBundle("orden.conPago.requerido"));

        } else if (catalogoConPagoModel.buscarConPagoPorNombre() != null) {
            FacesUtils.addErrorMessage("formPopupActualizarConPago:inpTxtnombreConPagoEdit", FacesUtils.getKeyResourceBundle("orden.conPago.existe.catalogo"));

        } else {
            try {
                catalogoConPagoModel.actualizarConPago();
                FacesUtils.addInfoMessage("Condicion de Pago " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
                cancelarActualizarConPago(actionEvent);

            } catch (Exception ex) {
                UtilLog4j.log.fatal(this, "ERRRRR O OO O O O RRRRR + " + ex.getMessage());
                Logger.getLogger(CatalogoConPago.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void eliminarConPago() {
        String proveedores = catalogoConPagoModel.buscarProConPagoPorNombre();//Se buscan los proveedores ligados con la condicion de 
        if (proveedores != null) {
            FacesUtils.addErrorMessage("formPopupEliminarConPago:msgsPopupEliminarConPago", FacesUtils.getKeyResourceBundle("orden.conPago.error.delete.catalogo") + proveedores);
        } else {
            try {
                this.catalogoConPagoModel.eliminarConPago();
                FacesUtils.addInfoMessage("Condicion de Pago " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
                cancelarEliminarConPago(actionEvent);
            } catch (Exception ex) {
                Logger.getLogger(CatalogoConPago.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void cancelarCrearConPago() {
        catalogoConPagoModel.setNomConPago(null);
        clearComponent("formPopupCrearConPago", "inpTxtnombreConPago");
        getPopup().toogleModalCrear(actionEvent);
    }

    public void cancelarActualizarConPago() {
        catalogoConPagoModel.setNomConPago(null);
        catalogoConPagoModel.setCondicionPagoVO(null);
        clearComponent("formPopupActualizarConPago", "inpTxtnombreConPagoEdit");
        getPopup().toogleModalActualizar(actionEvent);
    }

    public void cancelarEliminarConPago() {
        catalogoConPagoModel.setNomConPago(null);
        catalogoConPagoModel.setCondicionPagoVO(null);
        getPopup().toogleModalElimnar(actionEvent);
    }

    public CatalogoConPagoModel getCatalogoConPagoModel() {
        return catalogoConPagoModel;
    }

    public void setCatalogoConPagoModel(CatalogoConPagoModel catalogoConPagoModel) {
        this.catalogoConPagoModel = catalogoConPagoModel;
    }

    public RelacionConPagoProModel getRelacionConPagoProModel() {
        return relacionConPagoProModel;
    }

    public void setRelacionConPagoProModel(RelacionConPagoProModel relacionConPagoProModel) {
        this.relacionConPagoProModel = relacionConPagoProModel;
    }

    public DataModel getListData() {
        return catalogoConPagoModel.getListData();
    }

    public void setListData(DataModel listData) {
        this.catalogoConPagoModel.setListData(listData);
    }

    public GenericPanelPopup getPopup() {
        return popup;
    }

    public void setPopup(GenericPanelPopup popup) {
        this.popup = popup;
    }
    
}
