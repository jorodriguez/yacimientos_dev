/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.taller.bean.backing;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import sia.modelo.SgTallerMantenimiento;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.taller.beanModel.TallerMantenimientoBeanModel;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "tallerBean")
@RequestScoped
public class TallerMantenimientoBean implements Serializable {

    @ManagedProperty(value = "#{tallerBeanModel}")
    private TallerMantenimientoBeanModel tallerBeanModel;

    /**
     * Creates a new instance of aseguradoraBean
     */
    public TallerMantenimientoBean() {
    }

    public void createTaller(ActionEvent event) {
        try {
            if (tallerBeanModel.getNombreProveedor() != null && !tallerBeanModel.getNombreProveedor().equals("")) {
                if (!tallerBeanModel.buscarProveedorRepetido()) {
                    if (tallerBeanModel.createTaller()) {
                        FacesUtils.addInfoMessage("Se agregó el taller la lista de talleres de mantenimiento..");
                        tallerBeanModel.setNombreProveedor("");
                    } else {
                        FacesUtils.addErrorMessage("Existió un conflicto, por favor contacta al equipo del SIA para solucionar este conflicto al correo soportesia@ihsa.mx");
                    }
                    if (tallerBeanModel.isMostrarConfirmacion()) {
                        tallerBeanModel.setMostrarConfirmacion(false);
                    }
                } else {
                    FacesUtils.addErrorMessage("El proveedor ya se encuentra dado de alta en la lista");
                }
            } else {
                FacesUtils.addErrorMessage("Por favor especifique un proveedor");
            }
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Existió un conflicto, por favor contacta al equipo del SIA para solucionar este conflicto al correo soportesia@ihsa.mx");
            log("Excepcion al intentar guardar un taller " + e.getMessage());
        }
    }

    public void cancelarConfirmacion(ActionEvent event) {
        tallerBeanModel.setNombreProveedor("");
        tallerBeanModel.setMostrarConfirmacion(false);
    }

    public void encontrarProveedorEnAseguradoras() {
        tallerBeanModel.setMostrarConfirmacion(true);
    }

    public void deleteTaller(ActionEvent event) {
        tallerBeanModel.setTallerSeleccionado((SgTallerMantenimiento) tallerBeanModel.getTalleresModel().getRowData());
        if (tallerBeanModel.getTallerSeleccionado() != null) {
            if (!tallerBeanModel.buscarProveedorEnMantenimiento()) {
                tallerBeanModel.deleteTalleres();
                FacesUtils.addInfoMessage("Se eliminó correctamente el registro de taller de mantenimiento..");
            } else {
                FacesUtils.addErrorMessage("No se puede eliminar el registro, esta siendo utilizado por otro proceso");
            }
        }
    }

    public void buscarObjetoProveedor(ActionEvent event) {
        if (tallerBeanModel.traerProveedorPorNombre(tallerBeanModel.getNombreProveedor()).getNombre() == null) {
            UtilLog4j.log.info(this, "El proveedor es null");
        }
    }

    //Auto completar proveedor
    public List<SelectItem> getListaProveedor() {
        return tallerBeanModel.getListaProveedor();
    }

    public void proveedorListener(String textChangeEvent) {
        tallerBeanModel.setListaProveedor(regresaProveedorActivo(textChangeEvent));
        tallerBeanModel.setNombreProveedor(textChangeEvent);
        log("[Nombre del proveedor " + tallerBeanModel.getNombreProveedor());
        //validar guardar
        if (!tallerBeanModel.buscarProveedorRepetido()) {
            UtilLog4j.log.info(this, "[no esta repetido el proveedor");
        } else {
            FacesUtils.addErrorMessage("El proveedor ya se encuentra dado de alta en la lista");
        }
    }

    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (Iterator it = tallerBeanModel.getListaProveedorBuscar().iterator(); it.hasNext();) {
            String string = (String) it.next();
            if (string != null) {
                if (string.toLowerCase().startsWith(cadenaDigitada.toLowerCase())) {
                    SelectItem item = new SelectItem(string);
                    list.add(item);
                }
            }
        }
        return list;
    }

    public void setListaProveedor(List<SelectItem> listaProveedor) {
        tallerBeanModel.setListaProveedor(listaProveedor);
    }

    public DataModel getTallerModel() {
        return tallerBeanModel.getTalleresModel();
    }

    public boolean getMostrarConfirmacion() {
        return tallerBeanModel.isMostrarConfirmacion();
    }

    public String getNombreProveedor() {
        return tallerBeanModel.getNombreProveedor();
    }

    /**
     * @param nombreProveedor the nombreProveedor to set
     */
    public void setNombreProveedor(String nombreProveedor) {
        tallerBeanModel.setNombreProveedor(nombreProveedor);
    }

    public void limpiarComponente(String nombreFormulario, String nombreComponente) {
        log("Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
            log("limpio el compnente");
        } catch (Exception e) {
            log("Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
        //log(mensaje);
    }

    /**
     * @param tallerBeanModel the tallerBeanModel to set
     */
    public void setTallerBeanModel(TallerMantenimientoBeanModel tallerBeanModel) {
        this.tallerBeanModel = tallerBeanModel;
    }
}
