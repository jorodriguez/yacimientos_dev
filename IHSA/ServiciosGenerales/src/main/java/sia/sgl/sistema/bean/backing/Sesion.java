/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiRelRolOpcionImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sgl.sistema.bean.model.PrincipalModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.utils.SessionUtils;
import sia.sgl.viaje.bean.backing.AdministrarViajeBean;
import sia.sgl.viaje.bean.model.AdministrarViajeBeanModel;
import sia.sgl.viaje.solicitud.backing.SolicitudViajeBean;
import sia.sgl.viaje.solicitud.bean.model.SolicitudViajeBeanModel;
import sia.util.Env;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 *
 * @author Héctor
 */
@Named
@SessionScoped
public class Sesion implements Serializable {

    //ManagedBeans
    //Servicios
    public static final String USER = "user";
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    SiOpcionImpl siOpcionImpl;
    @Inject
    SgOficinaImpl sgOficinaImpl;
    @Inject
    SiRelRolOpcionImpl siRelRolOpcionImpl;
    @Inject
    SgOficinaAnalistaImpl sgOficinaAnalistaImpl;
    @Inject
    private ApCampoImpl campoImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;

    //Entidades
    private Usuario usuario;
    //Clases

    private OficinaVO oficinaActual;
    private List<OficinaVO> oficinasUsuario;
    private List<CampoUsuarioPuestoVo> listCampoByUsusario;
    private TreeMap<String, Boolean> controladorPopups = new TreeMap<String, Boolean>();
    private String nombreRhPuesto;
    private String u;
    private String c;
    private int idRol;
    private String nombreRol;
    private String rol;
    private boolean justificaViajes;
    //Primitivos
    private boolean olvidoClave;
    private boolean visible = true;
    private List<UsuarioRolVo> roles;
    private String rfcEmpresa;
    //

    //
    @Getter
    @Setter
    private MenuModel menu;
    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Getter
    @Setter
    private Properties ctx;

    /**
     * Creates a new instance of sesion
     */
    public Sesion() {
    }

    public void crearMenu(String idUsuario, int idCampo) {

        construirMenu(idUsuario, idCampo);
    }

    private void construirMenu(String idUsuario, int idCampo) {
        menu = new DefaultMenuModel();
        List<SiOpcionVo> opcionesMenu = obtenerOpcionesPrincipalesMenu(idUsuario, idCampo);
        DefaultMenuItem opcPrincipal = DefaultMenuItem.builder()
                .value("Principal")
                .ajax(Boolean.FALSE)
                .command("/principal.xhtml?faces-redirect=true")
                .build();
        menu.getElements().add(opcPrincipal);
        for (SiOpcionVo opcion : opcionesMenu) {
            if (opcion.getPagina() != null) {
                //First submenu
                DefaultMenuItem firstSubmenu = DefaultMenuItem.builder()
                        .value(opcion.getNombre())
                        .ajax(Boolean.FALSE)
                        .command(opcion.getPagina() + "?faces-redirect=true")
                        .build();
                menu.getElements().add(firstSubmenu);
            } else {
                DefaultSubMenu firstSubmenu = DefaultSubMenu.builder()
                        .label(opcion.getNombre())
                        .build();

                List<SiOpcionVo> opcionesHijos = obtenerOpcionesHijoMenu(opcion.getId());
                for (SiOpcionVo opcionHijo : opcionesHijos) {
                    DefaultMenuItem item = DefaultMenuItem.builder()
                            .value(opcionHijo.getNombre())
                            .icon("pi pi-cog")
                            .ajax(false)
                            .command(opcionHijo.getPagina() + "?faces-redirect=true")
                            .update("messages")
                            .build();
                    firstSubmenu.getElements().add(item);
                }
                menu.getElements().add(firstSubmenu);
            }
        }

        DefaultMenuItem opcAyuda = DefaultMenuItem.builder()
                .value("Ayuda")
                .ajax(Boolean.FALSE)
                .command("/vistas/sgl/ayuda/consultarArbolAyudas.xhtml?faces-redirect=true")
                .build();
        menu.getElements().add(opcAyuda);
    }

    public void subirValoresContexto(HttpSession sesion) {
        Env.setContext(ctx, Env.SESSION_ID, sesion.getId());
        Env.setContext(ctx, Env.CLIENT_INFO, sesion.getServletContext().getContextPath());
        Env.setContext(ctx, Env.PUNTO_ENTRADA, "ServiciosGenerales");
        Env.setContext(ctx, Env.PROYECTO_ID, usuario.getApCampo().getId());
        Env.setContext(ctx, Env.CODIGO_COMPANIA, usuario.getApCampo().getCompania().getRfc());
    }

    private List<SiOpcionVo> obtenerOpcionesPrincipalesMenu(String idUsuario, int idCampo) {
        return siOpcionImpl.getAllSiOpcionBySiModulo(Constantes.MODULO_SGYL, idUsuario, idCampo);
    }

    private List<SiOpcionVo> obtenerOpcionesHijoMenu(Integer id) {
        return siOpcionImpl.getChildSiOpcion(id, getUsuario().getId(), Constantes.MODULO_SGYL);
    }

    public String goToReturn(String page) {
        if (!page.endsWith(".xhtml")) {
            page += ".xhtml";
        }
        return page + "?faces-redirect=true";
    }

    public boolean isUsuarioInSessionGerente() {
        return this.gerenciaImpl.isUsuarioResponsableForAnyGerencia(-1, this.usuario.getId(), false);
    }

    /**
     * Elimina la información relacionada con la sesión actual.
     *
     */
    private void eliminarSesionActual() {

        log("@eliminarSesionActual");

        this.usuario = null;
        this.nombreRhPuesto = null;
        this.oficinaActual = null;
        this.oficinasUsuario = null;
        this.rol = null;
        roles = null;
        menu = null;
    }

    /**
     * Redirecciona a la URL proporcionada.
     *
     * @param url
     */
    private void redireccionar(final String url) {

        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String prefix = UtilSia.getUrl(origRequest);

        FacesContext fc = FacesContext.getCurrentInstance();

        try {
            fc.getExternalContext().redirect(prefix + url); // redirecciona la página
        } catch (IOException ex) {
            log("Error de IO al redireccionar: " + ex.getMessage());
        }
    }

    /**
     * Modifoc NLopez 14/11/2013
     *
     * @param actionEvent
     */
    public void cerrarSesion(ActionEvent actionEvent) {

        UtilLog4j.log.info(this, "CERRO SESION : " + usuario.getId());

        eliminarSesionActual();

        redireccionar(Constantes.URL_REL_SIA_SIGN_OUT);
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (this.usuario == null) {
            this.visible = true;
        } else {
            this.visible = false;
        }
    }

    /**
     * Redirecciona a la página principal del sistema Sia. Elimina la
     * información de la sesión actual para evitar acceso directo por medio de
     * URL.
     *
     * @param actionEvent
     */
    public void siaGo(ActionEvent actionEvent) {

        eliminarSesionActual();

        redireccionar(Constantes.URL_REL_SIA_PRINCIPAL);

    }

    public void completarCambioOficina(OficinaVO ofVo) {
        setOficinaActual(ofVo);
        AdministrarViajeBeanModel administrarViajeBean = (AdministrarViajeBeanModel) FacesUtils.getManagedBean("administrarViajeBean");
        administrarViajeBean.cargarSolicitudesYViajes();
        PrimeFaces.current().executeScript(";$(dialogoCambiarOficina).modal('hide');");
        redireccionar("/ServiciosGenerales/");
    }

    public void completarCambioCampo(int idCampo) {
        ApCampo a = campoImpl.find(idCampo);
        this.getUsuario().setApCampo(a);
        
        List<UsuarioRolVo> ur = siUsuarioRolImpl.traerRolPorUsuarioModulo(this.getUsuario().getId(), Constantes.MODULO_SGYL, idCampo);

        if (ur != null) {
            this.setRoles(ur);
            for (UsuarioRolVo usuarioRolVo : ur) {
                if (usuarioRolVo.isPrincipal()) {
                    this.setNombreRol(usuarioRolVo.getNombreRol());
                    this.setIdRol(usuarioRolVo.getIdRol());
                    break;
                }
            }
            AdministrarViajeBeanModel administrarViajeBean = (AdministrarViajeBeanModel) FacesUtils.getManagedBean("administrarViajeBean");
            SolicitudViajeBeanModel solicitudViajeBean = (SolicitudViajeBeanModel) FacesUtils.getManagedBean("solicitudViajeBeanModel");
            PrincipalModel principalBean = (PrincipalModel) FacesUtils.getManagedBean(("principalBean"));

            crearMenu(usuario.getId(), idCampo);
            principalBean.opcionesPriciles();

            switch (this.getIdRol()) {
                case Constantes.SGL_RESPONSABLE:
                    this.setRol(Constantes.ROL_SGL_RESPONSABLE);
                    this.setOficinasUsuario((sgOficinaImpl.traerListaOficina()));
                    administrarViajeBean.cargarSolicitudesYViajes();
                    solicitudViajeBean.goToSolicitudesPorAprobar();
                    break;
                case Constantes.SGL_ANALISTA:
                    setOficinasUsuario((sgOficinaImpl.traerListaOficina()));
                    administrarViajeBean.cargarSolicitudesYViajes();
                    break;
                case Constantes.SGL_ADMINISTRA:
                    setOficinasUsuario((sgOficinaImpl.traerListaOficina()));
                    administrarViajeBean.cargarSolicitudesYViajes();
                    break;
                case Constantes.ROL_GERENTE:
                    solicitudViajeBean.goToSolicitudesPorAprobar();
                    break;
                case Constantes.ROL_JUSTIFICA_VIAJES:
                    solicitudViajeBean.goToSolicitudesPorAprobar();
                    break;
            }

        }
        System.out.println("setting to usuario "+getUsuario().getApCampo().getNombre());
        PrimeFaces.current().executeScript(";$(dialogoCambiarCampo).modal('hide');recargar(msg);");
        redireccionar("/ServiciosGenerales/");
    }

    public DataModel getTraerOficinaPorAnalista() {
        try {
            return new ListDataModel(sgOficinaAnalistaImpl.getOficinasByAnalistaAndStatus(usuario, false));
        } catch (Exception e) {
            return null;
        }
    }

    public void reloadUser(ActionEvent actionEvent) {
        this.usuario = this.usuarioImpl.find(this.usuario.getId());
    }

    /**
     * @return the u
     */
    public String getU() {
        return u;
    }

    /**
     * @param u the u to set
     */
    public void setU(String u) {
        this.u = u;
    }

    /**
     * @return the c
     */
    public String getC() {
        return c;
    }

    /**
     * @param c the c to set
     */
    public void setC(String c) {
        this.c = c;
    }

    /**
     * @return the olvidoClave
     */
    public boolean isOlvidoClave() {
        return olvidoClave;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Regresa el Mapa que controla los Popups
     *
     * @return the controladorPopups
     */
    public TreeMap<String, Boolean> getControladorPopups() {
        return controladorPopups;
    }

    /**
     * @param controladorPopups the controladorPopups to set
     */
    public void setControladorPopups(TreeMap<String, Boolean> controladorPopups) {
        this.controladorPopups = controladorPopups;
    }

    /**
     * Regresa un DataModel que contiene todas las oficinas a las que tiene
     * acceso el Usuario en Sesion
     *
     * @return the oficinasUsuario
     */
    public List<OficinaVO> getOficinasUsuario() {
        return oficinasUsuario;
    }

    /**
     * @param oficinasUsuario the oficinasUsuario to set
     */
    public void setOficinasUsuario(List<OficinaVO> oficinasUsuario) {
        this.oficinasUsuario = oficinasUsuario;
    }

    /**
     * @return the rol
     */
    public String getRol() {

        return rol;
    }

    /**
     * @param rol the rol to set
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * @return the nombreRhPuesto
     */
    public String getNombreRhPuesto() {
        return nombreRhPuesto;
    }

    /**
     * @param nombreRhPuesto the nombreRhPuesto to set
     */
    public void setNombreRhPuesto(String nombreRhPuesto) {
        this.nombreRhPuesto = nombreRhPuesto;
    }

    /**
     * @return the idRol
     */
    public int getIdRol() {
        return idRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /**
     * @return the nombreRol
     */
    public String getNombreRol() {
        return nombreRol;
    }

    /**
     * @param nombreRol the nombreRol to set
     */
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    /**
     * @return the roles
     */
    public List<UsuarioRolVo> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(List<UsuarioRolVo> roles) {
        this.roles = roles;
    }

    /*
     * @return the oficinaVO
     */
    public OficinaVO getOficinaActual() {
        return oficinaActual;
    }

    /**
     * @param oficinaVO the oficinaVO to set
     */
    public void setOficinaActual(OficinaVO oficinaVO) {
        this.oficinaActual = oficinaVO;
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    /**
     * @return the rfcEmpresa
     */
    public String getRfcEmpresa() {
        return rfcEmpresa;
    }

    /**
     * @param rfcEmpresa the rfcEmpresa to set
     */
    public void setRfcEmpresa(String rfcEmpresa) {
        this.rfcEmpresa = rfcEmpresa;
    }

    /**
     * @return the justificaViajes
     */
    public boolean isJustificaViajes() {
        return Constantes.ROL_JUSTIFICA_VIAJES == this.idRol;
    }

    /**
     * @param justificaViajes the justificaViajes to set
     */
    public void setJustificaViajes(boolean justificaViajes) {
        this.justificaViajes = justificaViajes;
    }

    /**
     * @return the listCampoByUsusario
     */
    public List<CampoUsuarioPuestoVo> getListCampoByUsusario() {
        return listCampoByUsusario;
    }

    /**
     * @param listCampoByUsusario the listCampoByUsusario to set
     */
    public void setListCampoByUsusario(List<CampoUsuarioPuestoVo> listCampoByUsusario) {
        this.listCampoByUsusario = listCampoByUsusario;
    }

}
