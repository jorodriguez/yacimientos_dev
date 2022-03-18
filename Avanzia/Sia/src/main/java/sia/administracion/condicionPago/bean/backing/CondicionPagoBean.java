/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.condicionPago.bean.backing;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.administracion.condicionPago.bean.model.CondicionPagoModel;
import sia.modelo.Proveedor;
import sia.modelo.orden.vo.ProveedorConPagoVo;
import sia.sistema.bean.backing.GenericPanelPopup;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author icristobal
 */
@Named(value = "condicionPagoB")
@ViewScoped
public class CondicionPagoBean implements Serializable{

    @Inject
    private CondicionPagoModel condicionPagoModel;
    @Inject
    private GenericPanelPopup popup;

    /**
     * Creates a new instance of CondicionPagoBean
     */
    public CondicionPagoBean() {
    }

    public void entrar() {
        condicionPagoModel.setPro(0);
        condicionPagoModel.setNomConPago(null);
        condicionPagoModel.setConPagoEdit(null);
        condicionPagoModel.setListaProveedor(null);
        condicionPagoModel.setListaConPago(null);
        condicionPagoModel.setListData(null);
        condicionPagoModel.setListaConPagoEdit(null);
        condicionPagoModel.setProveedorConPagoVo(null);
        condicionPagoModel.setVisibleConPago(false);
        condicionPagoModel.setVolver(false);
        popup.setModalRenderedCrear(false);
        popup.setModalRenderedActualizar(false);
        popup.setModalRenderedEliminar(false);
        popup.setModalRenderedAgregar(false);

    }

    public void entrarDesdeCatalogo() {
        if (condicionPagoModel.isVolver() == true) {//Si regresa del catálogo NO inicializa. Solo refresca las condiciones de pago                     
            condicionPagoModel.setVisibleConPago(true);
            condicionPagoModel.setVolver(false);
            condicionPagoModel.setRfcCompania(condicionPagoModel.regresaRfcCompaniaSesion());
            condicionPagoModel.getAllConPagoProveedor();
        } else {
            entrar();
        }
    }

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
        //     UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente + e.getMessage());
        }
    }

    //********************************************************************************************************
//  AUTO-COMPLETAR proveedor-Cuando el texto cambia, se actualiza la lista con los proveedores que cumplen el criterio de búsqueda.
    public void proveedorListener() {
        condicionPagoModel.setVisibleConPago(true);
        condicionPagoModel.setProveedor(null);
        condicionPagoModel.setListaProveedor(null);
        condicionPagoModel.setNomConPago(null);
        condicionPagoModel.getAllConPagoProveedor();
    }
    //********************************************************************************************************        
    //  AUTO-COMPLETAR Condiciones de pago
/*
    public void conPagoListener(ValueChangeEvent textChangeEvent) {
        if (textChangeEvent.getComponent() instanceof SelectInputText) {
            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
            String cadenaDigitada = (String) textChangeEvent.getNewValue();

            condicionPagoModel.regresaConPagoActiva(cadenaDigitada);

            if (autoComplete.getSelectedItem() != null) {
                String conPago = (String) autoComplete.getSelectedItem().getValue();
                condicionPagoModel.setNomConPago(conPago);
                condicionPagoModel.setListaConPago(null);
            }
        }
    }
    */
    //*********************************************************************************************************           

    public void validaAutoCompleteConPago() {
        if (condicionPagoModel.getNomConPago().isEmpty()) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("orden.conPago.requerido"));

        } else if (condicionPagoModel.buscarConPagoPorNombre() == null) {
            mostrarPopupCrear();

        } else {
            mostrarPopupAgregar();
        }
    }

    public String goToCatalogoConPago() {
        condicionPagoModel.setVolver(true);
////        llenarProveedor();
        return "/vistas/administracion/catalogoConPago";
    }

    public void llenarProveedor() {
        String jsonProveedores = condicionPagoModel.llenarProveedor();
        UtilLog4j.log.info(this, "jsonProveedores:  " + jsonProveedores);
        PrimeFaces.current().executeScript(";setJson(" + jsonProveedores + ");");

    }

    public void mostrarPopupCrear() {
        getPopup().toogleModalCrear();
    }

    public void mostrarPopupAgregar() {
        getPopup().toogleModalAgregar();
    }

    public void mostrarPopupActualizar() {
        condicionPagoModel.setNomConPago(null);
        ProveedorConPagoVo conPagoSeleccionado = (ProveedorConPagoVo) getListData().getRowData();
        condicionPagoModel.setProveedorConPagoVo(conPagoSeleccionado);
        condicionPagoModel.regresaListaConPagoEdit();
        getPopup().toogleModalActualizar();
    }

    public void mostrarPopupEliminar() {
        condicionPagoModel.setNomConPago(null);
        ProveedorConPagoVo conPagoSeleccionado = (ProveedorConPagoVo) getListData().getRowData();
        condicionPagoModel.setProveedorConPagoVo(conPagoSeleccionado);
        getPopup().toogleModalElimnar();
    }

    public void crearProveedorConPago() {
        if (condicionPagoModel.buscarConPagoPorNombre() != null) {
            FacesUtils.addErrorMessage("formPopupCrearConPago:inpTxtnombreConPago", FacesUtils.getKeyResourceBundle("orden.conPago.existe.catalogo"));

        } else {
            try {
                condicionPagoModel.guardarAltaCondicionReturn();
                condicionPagoModel.guardarProveedorConPago();
                FacesUtils.addInfoMessage("Condicion de Pago " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
                cancelarCrearProveedorConPago();

            } catch (Exception ex) {
                Logger.getLogger(CondicionPagoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void cambiarLstaCondicionesPorCompania(ValueChangeEvent event) {
        condicionPagoModel.setRfcCompania((String) event.getNewValue());
        condicionPagoModel.setPro(0);
        condicionPagoModel.setListData(null);
        llenarProveedor();
    }

    public List<SelectItem> getListaCompania() {
        try {
            return condicionPagoModel.listaCompania();
        } catch (Exception e) {
            return null;
        }
    }

    public void agregarProveedorConPago() {
        if (condicionPagoModel.buscarConPagoProPorNombre() != null) {
            FacesUtils.addErrorMessage("formPopupAgregarConPago:msgsPopupAgregarConPago", FacesUtils.getKeyResourceBundle("orden.conPago.existe.proveedor"));

        } else {
            try {
                condicionPagoModel.guardarProveedorConPago();
                FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("orden.conPago.agregada.satisfactoria"));
                cancelarAgregarProveedorConPago();

            } catch (Exception ex) {
                Logger.getLogger(CondicionPagoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void actualizarProveedorConPago() {
        if (condicionPagoModel.getConPagoEdit().equals("-1")) {
            FacesUtils.addErrorMessage("formPopupActualizarConPago:somActualizarConPago", FacesUtils.getKeyResourceBundle("orden.debe.seleccionar.conPago"));

        } else if (condicionPagoModel.buscarConPagoProPorNombre() != null) {
            FacesUtils.addErrorMessage("formPopupActualizarConPago:msgsPopupActualizarConPago", FacesUtils.getKeyResourceBundle("orden.conPago.existe.proveedor"));

        } else {
            try {
                condicionPagoModel.buscarConPagoPorNombre();
                condicionPagoModel.actualizarProveedorConPago();
                FacesUtils.addInfoMessage("Condicion de Pago " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
                cancelarActualizarProveedorConPago();

            } catch (Exception ex) {
                Logger.getLogger(CondicionPagoBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void eliminarProveedorConPago() {
        try {
            this.condicionPagoModel.eliminarProveedorConPago();
            FacesUtils.addInfoMessage("Condicion de Pago " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
            cancelarEliminarProveedorConPago();

        } catch (Exception ex) {
            Logger.getLogger(CondicionPagoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cancelarCrearProveedorConPago() {
        condicionPagoModel.setNomConPago(null);
        clearComponent("formPopupCrearConPago", "inpTxtnombreConPago");
        getPopup().toogleModalCrear();
    }

    public void cancelarAgregarProveedorConPago() {
        condicionPagoModel.setNomConPago(null);
        getPopup().toogleModalAgregar();
    }

    public void cancelarActualizarProveedorConPago() {
        condicionPagoModel.setConPagoEdit(null);
        condicionPagoModel.setProveedorConPagoVo(null);
        getPopup().toogleModalActualizar();
    }

    public void cancelarEliminarProveedorConPago() {
        condicionPagoModel.setNomConPago(null);
        condicionPagoModel.setProveedorConPagoVo(null);
        getPopup().toogleModalElimnar();
    }

    public CondicionPagoModel getCondicionPagoModel() {
        return condicionPagoModel;
    }

    public void setCondicionPagoModel(CondicionPagoModel condicionPagoModel) {
        this.condicionPagoModel = condicionPagoModel;
    }

    public Proveedor getProveedor() {
        return condicionPagoModel.getProveedor();
    }

    public void setProveedor(Proveedor proveedor) {
        this.condicionPagoModel.setProveedor(proveedor);
    }

    public int getPro() {
        return condicionPagoModel.getPro();
    }

    public void setPro(int pro) {
        this.condicionPagoModel.setPro(pro);
    }

    public DataModel getListData() {
        return condicionPagoModel.getListData();
    }

    public void setListData(DataModel listData) {
        this.condicionPagoModel.setListData(listData);
    }

    public GenericPanelPopup getPopup() {
        return popup;
    }

    public void setPopup(GenericPanelPopup popup) {
        this.popup = popup;
    }

    /**
     * @return the rfcCompania
     */
    public String getRfcCompania() {
        return condicionPagoModel.getRfcCompania();
    }

    /**
     * @param rfcCompania the rfcCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
        condicionPagoModel.setRfcCompania(rfcCompania);
    }
}
