/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.administracion.bean.model.ProveedorBeanModel;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.vo.CompaniaVo;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "proveedorBean")
@ViewScoped
public class ProveedorBean implements Serializable {

    /**
     * Creates a new instance of ProveedorBean
     */
    public ProveedorBean() {
    }

    @PostConstruct
    public void iniciar() {
        irRelacionProveedorCompania();
    }

    //
    @Inject
    private ProveedorBeanModel proveedorBeanModel;
//

    public String irAgregarProveedor() {
        proveedorBeanModel.irAgregarProveedor();
        proveedorBeanModel.setRfc("");
        proveedorBeanModel.setNumeroReferencia("");
        proveedorBeanModel.setCorreo("");
        return "/vistas/administracion/proveedor/agregarProveedor";
    }

    public void irRelacionProveedorCompania() {
        proveedorBeanModel.irRelacionProveedorCompania();
        llenarJson();
        proveedorBeanModel.setLista(null);
        proveedorBeanModel.setRfc("");
        proveedorBeanModel.setNumeroReferencia("");
        proveedorBeanModel.setCorreo("");
        proveedorBeanModel.setIdProveedor(0);
        proveedorBeanModel.setListaContacto(null);
        proveedorBeanModel.setProveedorVo(null);
    }

    public void llenarJson() {
        String jsonProveedores = proveedorBeanModel.traerProveedorJson();
        //UtilLog4j.log.info(this, "jsonProveedores:  " + jsonProveedores);
        PrimeFaces.current().executeScript(";setJson(" + jsonProveedores + ");");
    }

    //Listas
    public List getListaEmpresa() {
        try {
            return proveedorBeanModel.listaEmpresa();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar la lista de empresa por usuario");
            return null;
        }
    }
    //Listas

    public List<SelectItem> getListaCompaniaPorUsuario() {
        try {
            return proveedorBeanModel.listaCompaniaPorUsuario();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar la lista de empresa por usuario");
            return null;
        }
    }

    public DataModel getListaCompaniaPorPorveedor() {
        try {
            return proveedorBeanModel.getLista();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar la lista de empresa por usuario");
            return null;
        }
    }

    public void modificar() {
        proveedorBeanModel.setIdRelacion((Integer) proveedorBeanModel.getLista().getRowIndex());
        //proveedorBeanModel.setIdProveedorCompania(Integer.parseInt(FacesUtils.getRequestParameter("idProvComp")));
        List<CompaniaVo> lc = (List<CompaniaVo>) proveedorBeanModel.getLista().getWrappedData();
        lc.get(proveedorBeanModel.getIdRelacion()).setEditar(true);
        proveedorBeanModel.setLista(new ListDataModel(lc));
//        proveedorBeanModel.getCompaniaVo().setEditar(true);

    }

    public void guardar() {
        proveedorBeanModel.setIdRelacion((Integer) proveedorBeanModel.getLista().getRowIndex());
        proveedorBeanModel.completarModificarRelProvCompania();

    }

    public void cancelar() {
        proveedorBeanModel.setIdRelacion((Integer) proveedorBeanModel.getLista().getRowIndex());
        List<CompaniaVo> lc = (List<CompaniaVo>) proveedorBeanModel.getLista().getWrappedData();
        lc.get(proveedorBeanModel.getIdRelacion()).setEditar(false);
        proveedorBeanModel.setLista(new ListDataModel(lc));

    }

    public void eliminar() {
        proveedorBeanModel.setIdRelacion((Integer) proveedorBeanModel.getLista().getRowIndex());
        try {
            proveedorBeanModel.completarEliminarRelProvCompania();
            FacesUtils.addInfoMessage("Se eliminó la relación entre proveedor y la compania.'");
        } catch (RuntimeException e) {
            FacesUtils.addErrorMessage("No se eliminó la relación, favor de notificar al equipo a soportesia@hisa.mx");
            UtilLog4j.log.fatal(this, "No se eliminó el proveedor de compania . . . + + + +  " + e.getMessage());
        }

    }

    public void agregarRelacion() {
        if (proveedorBeanModel.buscarRelacionProveedorCompania()) {
            FacesUtils.addErrorMessage("Ya existe la relación entre el proveedor y la compania");
        } else {
            proveedorBeanModel.agregarRelacion();
        }
    }
    
    public void enviarArchivos() {
        proveedorBeanModel.enviarArchivosPortal();
    }

    public void buscarCompaniaPorProveedor() {
        UtilLog4j.log.info(this, "No hace nada ");
        proveedorBeanModel.listaCompania();
    }

/////////////////////////////////////////////////////////////////// campor proveedor //////////////////////
    public void irRelacionProveedorCampo() {
        proveedorBeanModel.irRelacionProveedorCompania();
        llenarJson();
        proveedorBeanModel.setLista(null);
        proveedorBeanModel.setRfc("");
        proveedorBeanModel.setNumeroReferencia("");
        proveedorBeanModel.setCorreo("");
        proveedorBeanModel.traerProveedorCampo();
        proveedorBeanModel.setIdProveedor(0);
        proveedorBeanModel.setListaContacto(null);
    }

    public List<SelectItem> getListaCampoPorUsuario() {
        return proveedorBeanModel.listaCampo();
    }

    public void cambirCampoSeleccionado(ValueChangeEvent event) {
        proveedorBeanModel.setIdCampo((Integer) event.getNewValue());
        proveedorBeanModel.traerProveedorCampo();

    }

    public void agregarProveedor() {
        if (proveedorBeanModel.estaProveedorCampo()) {
            FacesUtils.addInfoMessage("El proveedor ya esta registrado.  ");
        } else {
            proveedorBeanModel.agregarProveedor();
            proveedorBeanModel.traerProveedorCampo();
            FacesUtils.addInfoMessage("Se registró el proveedor.  ");
        }
    }

    public DataModel getListaPorveedorPorCampo() {
        try {
            return proveedorBeanModel.getLista();
        } catch (Exception e) {
            System.out.println("Ocurrio un error al recuperar la lista de empresa por usuario" + e.getMessage());
            return null;
        }
    }

    public void eliminarProveedorCampo() {
        proveedorBeanModel.setIdRelacion(Integer.parseInt(FacesUtils.getRequestParameter("idProveedorCampo")));
        proveedorBeanModel.eliminarProveedorCampo();
        proveedorBeanModel.traerProveedorCampo();
    }
/////////////////////////////////////// PROVEEDOR CONTACTO ////////////////////

    public void agregarContacto() {
        proveedorBeanModel.agregarContacto();
        proveedorBeanModel.listaContactoPorPorveedor();
        proveedorBeanModel.setContacto("");
        proveedorBeanModel.setTelefono("");
        proveedorBeanModel.setCorreo("");
    }

    public void cancelarAgregarContacto() {
        proveedorBeanModel.setContacto("");
        proveedorBeanModel.setTelefono("");
        proveedorBeanModel.setCorreo("");
    }

    public DataModel getListaContactoPorPorveedor() {
        try {
            return proveedorBeanModel.getListaContacto();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer contactos  . . . . " + e.getMessage());
            return null;
        }
    }

    public void modificarContacto() {
        proveedorBeanModel.setIdContacto((Integer) proveedorBeanModel.getListaContacto().getRowIndex());
        List<ContactoProveedorVO> lc = (List<ContactoProveedorVO>) proveedorBeanModel.getListaContacto().getWrappedData();
        lc.get(proveedorBeanModel.getIdContacto()).setEditar(true);
        proveedorBeanModel.setListaContacto(new ListDataModel(lc));
    }

    public void completarModificarContacto() {
        proveedorBeanModel.setIdContacto((Integer) proveedorBeanModel.getListaContacto().getRowIndex());
        proveedorBeanModel.completarModificarContacto();
    }

    public void cancelarModificarContacto() {
        proveedorBeanModel.setIdContacto((Integer) proveedorBeanModel.getListaContacto().getRowIndex());
        List<ContactoProveedorVO> lc = (List<ContactoProveedorVO>) proveedorBeanModel.getListaContacto().getWrappedData();
        lc.get(proveedorBeanModel.getIdContacto()).setEditar(false);
        proveedorBeanModel.setListaContacto(new ListDataModel(lc));
    }

    public void eliminarContacto() {
        proveedorBeanModel.setIdContacto(Integer.parseInt(FacesUtils.getRequestParameter("contacto")));
        proveedorBeanModel.eliminarContacto();
        proveedorBeanModel.listaContactoPorPorveedor();
    }

    public void modificarDatosProveedor() {
        proveedorBeanModel.modificarDatosProveedor();
    }

    ///
    public void enviarContraseña() {
        if (proveedorBeanModel.enviarContraseña()) {
            PrimeFaces.current().executeScript("$(dialogoCambiarPassProveedor).modal('hide')");
            FacesUtils.addErrorMessage("Se envío la contraseña al proveedor.");
        } else {
            FacesUtils.addErrorMessage("Ocurrio un error, favor de notificar al equipo de desarrollo del SIA (siaihsa@ihsa.mx)");
        }

    }

    /**
     * @return the rfc
     */
    public String getRfc() {
        return proveedorBeanModel.getRfc();
    }

    /**
     * @param rfc the rfc to set
     */
    public void setRfc(String rfc) {
        proveedorBeanModel.setRfc(rfc);
    }

    /**
     * @return the correo
     */
    public String getCorreo() {
        return proveedorBeanModel.getCorreo();
    }

    /**
     * @param correo the correo to set
     */
    public void setCorreo(String correo) {
        proveedorBeanModel.setCorreo(correo);
    }

    /**
     * @return the numeroReferencia
     */
    public String getNumeroReferencia() {
        return proveedorBeanModel.getNumeroReferencia();
    }

    /**
     * @param numeroReferencia the numeroReferencia to set
     */
    public void setNumeroReferencia(String numeroReferencia) {
        proveedorBeanModel.setNumeroReferencia(numeroReferencia);
    }

    /**
     * @param proveedorBeanModel the proveedorBeanModel to set
     */
    public void setProveedorBeanModel(ProveedorBeanModel proveedorBeanModel) {
        this.proveedorBeanModel = proveedorBeanModel;
    }

    /**
     * @return the rfcCompania
     */
    public String getRfcCompania() {
        return proveedorBeanModel.getRfcCompania();
    }

    /**
     * @param rfcCompania the rfcCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
        proveedorBeanModel.setRfcCompania(rfcCompania);
    }

    /**
     * @return the filaSeleccionada
     */
    public Map<String, Boolean> getFilaSeleccionada() {
        return proveedorBeanModel.getFilaSeleccionada();
    }

    /**
     * @param filaSeleccionada the filaSeleccionada to set
     */
    public void setFilaSeleccionada(Map<String, Boolean> filaSeleccionada) {
        proveedorBeanModel.setFilaSeleccionada(filaSeleccionada);
    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return proveedorBeanModel.getIdProveedor();
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        proveedorBeanModel.setIdProveedor(idProveedor);
    }

    /**
     * @return the nombreProveedor
     */
    public String getNombreProveedor() {
        return proveedorBeanModel.getNombreProveedor();
    }

    /**
     * @param nombreProveedor the nombreProveedor to set
     */
    public void setNombreProveedor(String nombreProveedor) {
        proveedorBeanModel.setNombreProveedor(nombreProveedor);
    }

    /**
     * @return the idProveedorCompania
     */
    public int getIdProveedorCompania() {
        return proveedorBeanModel.getIdRelacion();
    }

    /**
     * @param idRelacion the idProveedorCompania to set
     */
    public void setIdProveedorCompania(int idRelacion) {
        proveedorBeanModel.setIdRelacion(idRelacion);
    }

    /**
     * @return the companiaVo
     */
    public CompaniaVo getCompaniaVo() {
        return proveedorBeanModel.getCompaniaVo();
    }

    /**
     * @param companiaVo the companiaVo to set
     */
    public void setCompaniaVo(CompaniaVo companiaVo) {
        proveedorBeanModel.setCompaniaVo(companiaVo);
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return proveedorBeanModel.getIdCampo();
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        proveedorBeanModel.setIdCampo(idCampo);
    }

    /**
     * @return the idContacto
     */
    public int getIdContacto() {
        return proveedorBeanModel.getIdContacto();
    }

    /**
     * @param idContacto the idContacto to set
     */
    public void setIdContacto(int idContacto) {
        proveedorBeanModel.setIdContacto(idContacto);
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return proveedorBeanModel.getTelefono();
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
        proveedorBeanModel.setTelefono(telefono);
    }

    /**
     * @return the contacto
     */
    public String getContacto() {
        return proveedorBeanModel.getContacto();
    }

    /**
     * @param contacto the contacto to set
     */
    public void setContacto(String contacto) {
        proveedorBeanModel.setContacto(contacto);
    }

    /**
     * @return the proveedorVo
     */
    public ProveedorVo getProveedorVo() {
        return proveedorBeanModel.getProveedorVo();
    }

    /**
     * @param proveedorVo the proveedorVo to set
     */
    public void setProveedorVo(ProveedorVo proveedorVo) {
        proveedorBeanModel.setProveedorVo(proveedorVo);
    }
}
