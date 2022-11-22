/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.vo.CompaniaVo;
import sia.notificaciones.proveedor.impl.NotificacionProveedorImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcCampoProveedorImpl;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvProveedorCompaniaImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.ProveedorEnum;
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

    @Inject
    private Sesion sesion;
    //
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private PvProveedorCompaniaImpl proveedorCompaniaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private OcCampoProveedorImpl ocCampoProveedorLocal;
    @Inject
    private ContactoProveedorImpl contactoProveedorImpl;
    @Inject
    private NotificacionProveedorImpl notificacionImpl;

    @Getter
    @Setter
    private String rfc;
    @Getter
    @Setter
    private String rfcCompania;
    @Getter
    @Setter
    private String correo;
    @Getter
    @Setter
    private String numeroReferencia;
//
    @Getter
    @Setter
    private int idProveedor;
    @Getter
    @Setter
    private String nombreProveedor;
    @Getter
    @Setter
    private Map<String, Boolean> filaSeleccionada = new HashMap<>();
    //
    @Getter
    @Setter
    private int idRelacion;
    @Getter
    @Setter
    private CompaniaVo companiaVo;
    @Getter
    @Setter
    private List<CompaniaVo> lista;
    @Getter
    @Setter
    private List<ContactoProveedorVO> listaContacto;
    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    private int idContacto;
    @Getter
    @Setter
    private String telefono;
    @Getter
    @Setter
    private String contacto;
    @Getter
    @Setter
    private ProveedorVo proveedorVo;
    @Getter
    @Setter
    private List<SelectItem> empresas;

    @PostConstruct
    public void iniciar() {
        listaContacto = new ArrayList<>();
        empresas = new ArrayList<>();
        lista = new ArrayList<>();
        irRelacionProveedorCompania();
        companiasPorUsuario();
    }

    //
//
    public String irAgregarProveedor() {
        setRfc("");
        setNumeroReferencia("");
        setCorreo("");
        return "/vistas/administracion/proveedor/agregarProveedor.xhtml?faces-redirect=true";
    }

    public void irRelacionProveedorCompania() {
        setLista(null);
        setRfc("");
        setNumeroReferencia("");
        setCorreo("");
        setIdProveedor(0);
        setListaContacto(null);
        setProveedorVo(null);
    }

    //Listas
    public void companiasPorUsuario() {
        try {
            List<CompaniaVo> le = usuarioImpl.traerCompaniaPorUsuario(sesion.getUsuarioVo().getId());
            le.stream().forEach(ep -> {
                empresas.add(new SelectItem(ep.getRfcCompania(), ep.getNombre()));
            });
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar la lista de empresa por usuario");
        }
    }

    public void modificar(int indice) {
        lista.get(indice).setEditar(true);
    }

    public void guardar(int indice) {
        lista.get(indice).setEditar(false);
        proveedorCompaniaImpl.modificarRel(sesion.getUsuarioVo().getId(), lista.get(indice));

    }

    public void cancelar(int indice) {
        lista.get(indice).setEditar(false);
    }

    public void eliminar(int idRel) {
        try {
            proveedorCompaniaImpl.eliminarRel(sesion.getUsuarioVo().getId(), lista.get(idRel));
            setLista((proveedorCompaniaImpl.traerCompaniaPorProveedor(getIdProveedor())));
            FacesUtils.addInfoMessage("Se eliminó la relación entre proveedor y la compania.'");
        } catch (RuntimeException e) {
            FacesUtils.addErrorMessage("No se eliminó la relación, favor de notificar al equipo a soportesia@hisa.mx");
            UtilLog4j.log.fatal(this, "No se eliminó el proveedor de compania . . . + + + +  " + e.getMessage());
        }

    }

    public void agregarRelacion() {
        proveedorCompaniaImpl.buscarRelacionProveedorCompania(getIdProveedor(), getRfcCompania());
        FacesUtils.addErrorMessage("Ya existe la relación entre el proveedor y la compania");
    }

    public void enviarArchivos() {
        if (getProveedorVo() != null && getProveedorVo().getIdProveedor() > 0) {
            notificacionImpl.notificacionArchivosPortal(getProveedorVo(), sesion.getUsuarioVo().getMail(), Constantes.VACIO);
        }
    }

    public List<String> completaProveedor(String query) {
        return proveedorImpl.traerRfcNombreLikeProveedorQueryNativo(query, sesion.getRfcCompania(), ProveedorEnum.ACTIVO.getId());
    }

    public void llenarDatosProveedor() {
        proveedorVo = new ProveedorVo();
        String[] cad = nombreProveedor.split("/");
        proveedorVo = proveedorImpl.traerProveedorPorRFC(cad[0].trim());
        if (proveedorVo != null) {
            idProveedor = proveedorVo.getIdProveedor();
            listaContacto = contactoProveedorImpl.traerTodosContactoPorProveedor(proveedorVo.getIdProveedor());
            //
            lista = proveedorCompaniaImpl.traerCompaniaPorProveedor(idProveedor);
        }
        nombreProveedor = "";
    }

    public void buscarCompaniaPorProveedor() {
        ProveedorVo proveedorVo;
        String[] cad = nombreProveedor.split("/");
        proveedorVo = proveedorImpl.traerProveedorPorRFC(cad[0].trim());
        UtilLog4j.log.info(this, "No hace nada ");

        setLista((proveedorCompaniaImpl.traerCompaniaPorProveedor(getIdProveedor())));
        setListaContacto((contactoProveedorImpl.traerContactoPorProveedor(getIdProveedor(), Constantes.CONTACTO_REP_COMPRAS)));
    }

/////////////////////////////////////////////////////////////////// campor proveedor //////////////////////
//    public void irRelacionProveedorCampo() {
//        irRelacionProveedorCompania();
//        setLista(null);
//        setRfc("");
//        setNumeroReferencia("");
//        setCorreo("");
//        traerProveedorCampo();
//        setIdProveedor(0);
//        setListaContacto(null);
//    }
//
//    public void cambirCampoSeleccionado(ValueChangeEvent event) {
//        setIdCampo((Integer) event.getNewValue());
//        traerProveedorCampo();
//
//    }
//
//    public void agregarProveedor() {
//        if (estaProveedorCampo()) {
//            FacesUtils.addInfoMessage("El proveedor ya esta registrado.  ");
//        } else {
//            agregarProveedor();
//            traerProveedorCampo();
//            FacesUtils.addInfoMessage("Se registró el proveedor.  ");
//        }
//    }
//
//    public boolean estaProveedorCampo() {
//        return ocCampoProveedorLocal.buscarProveedorCampo(getIdCampo(), getIdProveedor());
//    }
//
/////////////////////////////////////// PROVEEDOR CONTACTO ////////////////////
    public void agregarContacto() {
        addContacto();
        listaContactoPorPorveedor();
        setContacto("");
        setTelefono("");
        setCorreo("");
    }

    public void addContacto() {
        if (getCorreo() != null && !getCorreo().isEmpty()) {
            String newCorreo = getCorreo().trim();
            int ascii = newCorreo.codePointAt(newCorreo.length() - 1);
            if (ascii < 65 || (ascii > 90 && ascii < 97) || ascii > 122) {
                setCorreo(newCorreo.substring(Constantes.CERO, newCorreo.length() - 1));
            }
        }
        contactoProveedorImpl.guardarContacto(getIdProveedor(), getContacto(), getTelefono(), getCorreo(), Constantes.CONTACTO_REP_COMPRAS, sesion.getUsuarioVo().getId());
    }

    public void listaContactoPorPorveedor() {
        setListaContacto((contactoProveedorImpl.traerContactoPorProveedor(getIdProveedor(), Constantes.CONTACTO_REP_COMPRAS)));
    }

    public void cancelarAgregarContacto() {
        setContacto("");
        setTelefono("");
        setCorreo("");
    }

    public void modificarContacto(int idCont) {
        listaContacto.get(idCont).setEditar(true);
    }

    public void completarModificarContacto(int idContacto) {
        contactoProveedorImpl.actualizarContacto(listaContacto.get(idContacto).getIdContactoProveedor(), listaContacto.get(idContacto).getNombre(), listaContacto.get(idContacto).getCorreo(), listaContacto.get(idCampo).getTelefono(), Boolean.FALSE, sesion.getUsuarioVo().getId());
        setListaContacto((contactoProveedorImpl.traerContactoPorProveedor(getIdProveedor(), Constantes.CONTACTO_REP_COMPRAS)));

    }

    public void cancelarModificarContacto(int idCont) {
        listaContacto.get(getIdContacto()).setEditar(false);
    }

    public void eliminarContacto(int idCont) {
        contactoProveedorImpl.eliminarContacto(idCont, sesion.getUsuarioVo().getId());
        listaContactoPorPorveedor();
    }

    public void modificarDatosProveedor() {
        proveedorImpl.modificarDatos(getProveedorVo(), sesion.getUsuarioVo().getId());
        PrimeFaces.current().executeScript("$(dialogoProveedor).modal('hide');");
    }

    ///
    public void enviarContrasenia() {
        if (proveedorImpl.notificaCambioPassword(proveedorVo.getIdProveedor())) {
            PrimeFaces.current().executeScript("$(dialogoCambiarPassProveedor).modal('hide')");
            FacesUtils.addErrorMessage("Se envío la contraseña al proveedor.");
        } else {
            FacesUtils.addErrorMessage("Ocurrio un error, favor de notificar al equipo de desarrollo del SIA (siaihsa@ihsa.mx)");
        }

    }

}
