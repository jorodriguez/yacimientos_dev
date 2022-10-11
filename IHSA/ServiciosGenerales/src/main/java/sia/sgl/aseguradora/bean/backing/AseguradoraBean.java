/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.aseguradora.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import sia.modelo.SgAseguradora;
import sia.sgl.aseguradora.bean.model.AseguradoraBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "aseguradoraBean")
@RequestScoped
public class AseguradoraBean implements Serializable {

    @ManagedProperty(value = "#{aseguradoraBeanModel}")
    private AseguradoraBeanModel aseguradoraBeanModel;

    /**
     * Creates a new instance of aseguradoraBean
     */
    public AseguradoraBean() {
    }

    public void createAseguradora(ActionEvent event) {
        try {
            if (aseguradoraBeanModel.getNombreProveedor() != null && !aseguradoraBeanModel.getNombreProveedor().equals("")) {
                if (!aseguradoraBeanModel.buscarProveedorRepetido()) {
                    aseguradoraBeanModel.createAseguradora();
//                AseguradoraBeanModel.traerAseguradoras();
                    aseguradoraBeanModel.setNombreProveedor("");
                    if (aseguradoraBeanModel.isMostrarConfirmacion()) {
                        aseguradoraBeanModel.setMostrarConfirmacion(false);
                    }
                } else {
                    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.aseguradora.proveedor.msg.repetido"));
                }
            } else {
                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.aseguradora.proveedor.msg.especifique"));
            }
        } catch (Exception e) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.aseguradora.proveedor.msg.especifique"));
        }
    }

    public void cancelarConfirmacion(ActionEvent event) {
        aseguradoraBeanModel.setNombreProveedor("");
        aseguradoraBeanModel.setMostrarConfirmacion(false);
    }

    public void encontrarProveedorEnTalleres() {
        aseguradoraBeanModel.setMostrarConfirmacion(true);
    }

    public void deleteAseguradora(ActionEvent event) {
        aseguradoraBeanModel.setAseguradoraSeleccionada((SgAseguradora) aseguradoraBeanModel.getAseguradorasModel().getRowData());
        if (aseguradoraBeanModel.getAseguradoraSeleccionada() != null) {
            if (!aseguradoraBeanModel.buscarProveedorEnMantenimiento()) {
                aseguradoraBeanModel.deleteAseguradora();
            } else {
                FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.aseguradora.proveedor.msg.no.puede.eliminar"));
            }
        }
    }

    public void buscarObjetoProveedor(ActionEvent event) {
        if (aseguradoraBeanModel.traerProveedorPorNombre(aseguradoraBeanModel.getNombreProveedor()).getNombre() == null) {
            UtilLog4j.log.info(this, "El proveedor es null");
        }
    }
//Auto completar proveedor

    public List<SelectItem> getListaProveedor() {
        return aseguradoraBeanModel.getListaProveedor();
    }

    public void proveedorListener(String textChangeEvent) {
        aseguradoraBeanModel.setListaProveedor(regresaProveedorActivo(textChangeEvent));
//	    if (autoComplete.getSelectedItem() != null) {
//		String proveedorSel = (String) autoComplete.getSelectedItem().getValue();
//		aseguradoraBeanModel.setNombreProveedor(proveedorSel);
//		//validar guardar
//		if (!aseguradoraBeanModel.buscarProveedorRepetido()) {
//		    aseguradoraBeanModel.setNombreProveedor("");
//		} else {
//		    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.aseguradora.proveedor.msg.ya.existe"));
//		}
//	    }
    }

    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (Iterator it = aseguradoraBeanModel.getListaProveedorBuscar().iterator(); it.hasNext();) {
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
        aseguradoraBeanModel.setListaProveedor(listaProveedor);
    }

////////    public List<SelectItem> getListaProveedor() {
////////        return AseguradoraBeanModel.getListaProveedor();
////////    }
////////
////////    public void proveedorListener(ValueChangeEvent textChangeEvent) {
////////        System.out.print("Entro a valueChange");
////////        if (textChangeEvent.getComponent() instanceof SelectInputText) {
////////            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
////////            String cadenaDigitada = (String) textChangeEvent.getNewValue();
////////            System.out.print("antes de traer lista");
////////            AseguradoraBeanModel.setListaProveedor(this.soporteProveedor.regresaProveedorActivo(cadenaDigitada));
////////            System.out.print("despues de traer lista");
////////            if (autoComplete.getSelectedItem() != null) {
////////                Proveedor proveedorSel = (Proveedor) autoComplete.getSelectedItem().getValue();
////////                AseguradoraBeanModel.setNombreProveedor(proveedorSel.getNombre());
////////                //validar guardar
////////                if (!AseguradoraBeanModel.buscarProveedorRepetido()) {
////////                    System.out.print("no esta repetido");
////////                    if (!AseguradoraBeanModel.encontrarProveedorEnTalleres()) {
////////                        System.out.print("no esta en talleres");
////////                        AseguradoraBeanModel.createAseguradora();
////////                    } else {
////////                        AseguradoraBeanModel.setMostrarConfirmacion(true);
////////                    }
////////                } else {
////////                    FacesUtils.addErrorMessage("El proveedor ya se encuentra en la lista");
////////                }
////////                System.out.print("Selecciono proveedor");
////////            }
////////            System.out.print("nueva lista armanda");
////////        }
////////
////////    }
    public DataModel getAseguradorasModel() {
        return aseguradoraBeanModel.getAseguradorasModel();
    }

    public boolean getMostrarConfirmacion() {
        return aseguradoraBeanModel.isMostrarConfirmacion();
    }

    public String getNombreProveedor() {
        return aseguradoraBeanModel.getNombreProveedor();
    }

    public void setNombreProveedor(String nombreProveedor) {
        aseguradoraBeanModel.setNombreProveedor(nombreProveedor);
    }

    public void limpiarComponente(String nombreFormulario, String nombreComponente) {
        UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
            UtilLog4j.log.info(this, "limpio el compnente");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    /**
     * @param aseguradoraBeanModel the aseguradoraBeanModel to set
     */
    public void setAseguradoraBeanModel(AseguradoraBeanModel aseguradoraBeanModel) {
        this.aseguradoraBeanModel = aseguradoraBeanModel;
    }
}
