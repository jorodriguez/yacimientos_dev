/**
 * UsuarioBean.java
 *
 * Creada el 9/06/2009, 05:36:48 PM
 *
 * Clase desarrollada por: Héctor Acosta Sierra para la compañia: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, actualizaciones o mejoras
 * enviar un correo a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.PrimeFaces;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.modelo.Compania;
import sia.modelo.Orden;
import sia.modelo.Requisicion;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcUsuarioOpcionImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.PvProveedorSinCartaIntencionImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.Env;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 * @author Héctor Acosta Sierra @versión 1.0
 * @author-mail hacosta.0505@gmail.com @date 9/06/2009
 */
@Named(value = "usuarioBean")
@SessionScoped
@Slf4j
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    public static final String USER = "user";

    @Inject
    private UsuarioImpl usuarioServicioRemoto;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private RequisicionImpl requisicionImpl;
    @Inject
    private PvProveedorSinCartaIntencionImpl pvProveedorSinCartaIntencionImpl;
    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private OcUsuarioOpcionImpl usuarioOpcionImpl;
    //
    @Inject
    CadenasMandoBean cadenasMandoBean;
    private String usuario, clave, accionUsuario, identificacion;
    //
    @Getter
    @Setter
    private Usuario usuarioConectado;
    private Compania compania;
    //
    private boolean cambioCampo = false;
    private String origenPeticion;
    //
    @Getter
    @Setter
    private String paginaInicial;
    //
    private String puesto;
    @Getter
    @Setter
    private String codigo;
    @Getter
    @Setter
    private String seleccion = "REQUISICIÓN.";
    //
    private List<CampoUsuarioPuestoVo> listaCampo;
    private Map<String, Boolean> mapaRoles;
    @Getter
    private Properties ctx;
    @Getter
    @Setter
    private SiOpcionVo opcionVo;

    /**
     * Creates a new instance of ManagedBeanUsuario
     */
    public UsuarioBean() {
    }

    public void iniciar() {
        opcionVo = new SiOpcionVo();
        direccionar();
    }

    public void subirValoresContexto(HttpSession session) {
        ctx = new Properties();
        Env.setContext(ctx, Env.SESSION_ID, session.getId());
        //Env.setContext(ctx, Env.CLIENT_INFO, session. SessionUtils.getClientInfo(SessionUtils.getRequest()));
        Env.setContext(ctx, Env.PUNTO_ENTRADA, "Compras");
        Env.setContext(ctx, Env.PROYECTO_ID, usuarioConectado.getApCampo().getId());
        Env.setContext(ctx, Env.CODIGO_COMPANIA, usuarioConectado.getApCampo().getCompania().getRfc());
    }

    public String buscarElemento() {
        String pagina = "";
        try {
            if (codigo.equals("")) {
                pagina = "";
                FacesUtilsBean.addErrorMessage("No introdujo ningún valor.");
            } else {
                if (this.seleccion.equals("REQUISICIÓN.")) {
                    Requisicion requisicion = requisicionImpl.buscarPorConsecutivoBloque(codigo.toUpperCase(), getUsuarioConectado().getId());
                    if (requisicion != null && requisicion.getApCampo().getId().intValue() == usuarioConectado.getApCampo().getId().intValue()) {
                        pagina = "/vistas/SiaWeb/Requisiciones/DetalleHistorial.xhtml?faces-redirect=true";

                        Env.setContext(ctx, "REQ_ID", requisicion.getId());
                    } else {
                        FacesUtilsBean.addErrorMessage("Requisición no encontrada.");
                    }
                } else {

                    Orden orden = ordenImpl.buscarPorOrdenConsecutivo(codigo.toUpperCase().trim(), getUsuarioConectado().getId());
                    if (orden != null && orden.getApCampo().getId().intValue() == usuarioConectado.getApCampo().getId().intValue()) {
                        pagina = "/vistas/SiaWeb/Orden/DetalleOrden.xhtml?faces-redirect=true";
                        Env.setContext(ctx, "ORDEN_ID", orden.getId());
                    } else {
                        pagina = "";
                        FacesUtilsBean.addErrorMessage("La Orden no coincide con el Bloque.");
                    }
                }
            }
            return pagina;
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return pagina;
    }

    public void cambiarCampo(int idCampo, String nombrePuesto) {
        usuarioServicioRemoto.cambiarCampoUsuario(usuarioConectado.getId(), usuarioConectado.getId(), idCampo);
        usuarioConectado = usuarioServicioRemoto.find(usuarioConectado.getId());

        if (nombrePuesto != null && !nombrePuesto.isEmpty()) {
            setPuesto(nombrePuesto);
        }

        setCompania(usuarioConectado.getApCampo().getCompania());
        llenarRoles();
        //
        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        contarBean.taerPendiente();
        //
        redireccionar(Constantes.URL_REL_SIA_WEB);
    }

    /**
     * Realiza limpieza de información de sesion y elimina la sesión actual.
     *
     */
    private void eliminarSesionActual() {
        LOGGER.info(this, "@eliminarSesionActual");
        // eliminar bean de usuario de sesion
        this.usuarioConectado = null;

        // realizar limpieza de informacion
    }

    /**
     * Redirecciona a la URL proporcionada.
     *
     * @param url
     */
    private void redireccionar(final String url) {
        /*
	 * FacesContext fc = FacesContext.getCurrentInstance(); try {
	 * fc.getExternalContext().redirect(url);//redirecciona la página }
	 * catch (IOException ex) { logger.log(Level.SEVERE, null, ex); }
         */
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String prefix = UtilSia.getUrl(origRequest);

        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            fc.getExternalContext().redirect(prefix + url); // redirecciona la página
        } catch (IOException ex) {
            LOGGER.info(this, "Error de IO al redireccionar: {0}", new Object[]{url}, ex);
        }

    }

    public void inicioEstablecerPaginaInicio() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest servletRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        String fullURI = servletRequest.getRequestURI();
        //out.println("URI:" + fullURI);
        String[] url = servletRequest.getRequestURI().split("/");
        String lastPart = url[url.length - 1];
        //out.println("URI last part:" + lastPart);
        opcionVo = siOpcionImpl.buscarOpcionPorUltimaParteURL(lastPart);
        if (opcionVo != null) {
            PrimeFaces.current().executeScript("$(popAgregarPagina).modal('show');");
        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("No se puede establecer está página como principal."));
        }
    }

    public void marcarComoPrincipal() {
        try {
            //   
            if (opcionVo != null) {
                usuarioOpcionImpl.guardar(getUsuarioConectado().getId(), opcionVo.getId());
            } else {
                usuarioOpcionImpl.guardar(getUsuarioConectado().getId(), opcionVo.getId());
                //
            }
            PrimeFaces.current().executeScript(";cerrarDialogoModal(popAgregarPagina);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al guardar la opcion principal. " + e.getMessage());
            PrimeFaces.current().executeScript(";cerrarDialogoModal(popAgregarPagina);");
        }
    }

    public String cambiarPaginaPendiente(String pagina, int campoId) {
        //------------------------------------------------------------
        //
        usuarioServicioRemoto.cambiarCampoUsuario(getUsuarioConectado().getId(), getUsuarioConectado().getId(), campoId);
        setUsuarioConectado(usuarioServicioRemoto.find(getUsuarioConectado().getId()));
        setCompania(getUsuarioConectado().getApCampo().getCompania());
        //

        return (pagina.contains(".xthml") ? pagina : pagina + ".xhtml?faces-redirect=true");
    }

    private void log(String mensaje) {
        LOGGER.info(this, mensaje);
    }

    /**
     * Cierra la sesión actual de SiaWeb y la del sistema Sia.
     *
     * @param
     */
    public void cerrarSesion() {
        eliminarSesionActual();
        redireccionar(Constantes.URL_REL_SIA_SIGN_OUT);
    }

    /**
     * Redirige a la página principal del sistema Sia. Se remueve la sesión
     * actual del sistema SiaWeb para prevenir accesos directos por medio de
     * URL.
     *
     * @param
     */
    public void siaGo() {
        eliminarSesionActual();
        redireccionar(Constantes.URL_REL_SIA_PRINCIPAL);
    }

    public Usuario buscarPorId(String idUsuario) {
        return usuarioServicioRemoto.find(idUsuario);
    }

    public Usuario buscarPorNombre(Object nombre) {
        return usuarioServicioRemoto.buscarPorNombre(nombre);
    }

    public void mostrarPanel() {
        PopupOlvidoClave popupOlvidoClave = (PopupOlvidoClave) FacesUtilsBean.getManagedBean("popupOlvidoClave");
        if (this.getUsuarioConectado() != null) {
            popupOlvidoClave.toggleModal();
        }
    }

    // ------ Listas  --------------------
    /**
     * @param idUsuario
     * @param revisa
     * @return Lista de usuarios Que Aprueban Requisiciones
     */
    public List<SelectItem> getListaAprueban(String idUsuario, String revisa) {
        return cadenasMandoBean.getListaAprueban(idUsuario, revisa, getUsuarioConectado().getApCampo().getId());
    }

    /**
     * @param solicita
     * @return Lista de usuarios Que Revisan Requisiciones
     */
    public List<SelectItem> listaRevisa(String solicita) {
        return cadenasMandoBean.traerRevisan(solicita, getUsuarioConectado().getApCampo().getId());
    }

    /**
     * @return Lista de usuarios Que Colocan orden de compra y o servicio
     */
    public List<SelectItem> getListaAnalista() {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            //List<Usuario> tempList = usuarioServicioRemoto.getAnalistas();

            List<UsuarioVO> tempList = usuarioServicioRemoto.traerListaRolPrincipalUsuarioRolModulo(Constantes.ROL_COMPRADOR, Constantes.MODULO_COMPRA, usuarioConectado.getApCampo().getId());
            for (UsuarioVO usuarioVO : tempList) {
                SelectItem item = new SelectItem(usuarioVO.getId(), usuarioVO.getNombre());
                // esta linea es por si quiero agregar mas de un valoritem.setValue(Lista.getId());
                resultList.add(item);
            }

            return resultList;
        } catch (RuntimeException ex) {
            LOGGER.fatal(this, "Error  : :  :" + ex.getMessage());
        }
        return resultList;
    }

    public void cambiarUsuarioCampo() {
        setCambioCampo(true);
    }

    public void cerrarCambioCampo() {
        setCambioCampo(false);
    }

    /**
     * @return the Usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param Usuario the Usuario to set
     */
    public void setUsuario(String Usuario) {
        this.usuario = Usuario;
    }

    /**
     * @return the Clave
     */
    public String getClave() {
        return clave;
    }

    /**
     * @param Clave the Clave to set
     */
    public void setClave(String Clave) {
        this.clave = Clave;
    }

    /**
     * @return the AccionUsuario
     */
    public String accionUsuario() {
        return accionUsuario;
    }

    /**
     * @return the Identificacion
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * @param Identificacion the Identificacion to set
     */
    public void setIdentificacion(String Identificacion) {
        this.identificacion = Identificacion;
    }

    /**
     * @return the cambioCampo
     */
    public boolean isCambioCampo() {
        return cambioCampo;
    }

    /**
     * @param cambioCampo the cambioCampo to set
     */
    public void setCambioCampo(boolean cambioCampo) {
        this.cambioCampo = cambioCampo;
    }

    public String getOrigenPeticion() {
        return origenPeticion;
    }

    public void setOrigenPeticion(String origenPeticion) {
        this.origenPeticion = origenPeticion;
    }

    /**
     * @return the compania
     */
    public Compania getCompania() {
        return compania;
    }

    /**
     * @param compania the compania to set
     */
    public void setCompania(Compania compania) {
        this.compania = compania;
    }

    public String direccionar() {
        return getPaginaInicial();
    }

    public void llenarRoles() {

        //.println("siUsuarioRolImpl " + siUsuarioRolImpl);
        setMapaRoles(new HashMap<>());
        List<UsuarioRolVo> rolUsuario = siUsuarioRolImpl.traerRolPorUsuarioModulo(usuarioConectado.getId(), Constantes.MODULO_REQUISICION, usuarioConectado.getApCampo().getId());
        rolUsuario.addAll(siUsuarioRolImpl.traerRolPorUsuarioModulo(usuarioConectado.getId(), Constantes.MODULO_COMPRA, usuarioConectado.getApCampo().getId()));
        rolUsuario.addAll(siUsuarioRolImpl.traerRolPorUsuarioModulo(usuarioConectado.getId(), Constantes.MODULO_CONTRATO, usuarioConectado.getApCampo().getId()));
        for (UsuarioRolVo usuarioRolVo : rolUsuario) {
            getMapaRoles().put(usuarioRolVo.getNombreRol(), Boolean.TRUE);
        }
    }

    /**
     * @return the puesto
     */
    public String getPuesto() {
        return puesto;
    }

    /**
     * @param puesto the puesto to set
     */
    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    /**
     * @return the listaCampo
     */
    public List<CampoUsuarioPuestoVo> getListaCampo() {
        return listaCampo;
    }

    /**
     * @param listaCampo the listaCampo to set
     */
    public void setListaCampo(List<CampoUsuarioPuestoVo> listaCampo) {
        this.listaCampo = listaCampo;
    }

    /**
     * @return the mapaRoles
     */
    public Map<String, Boolean> getMapaRoles() {
        return mapaRoles;
    }

    /**
     * @param mapaRoles the mapaRoles to set
     */
    public void setMapaRoles(Map<String, Boolean> mapaRoles) {
        this.mapaRoles = mapaRoles;
    }
}
