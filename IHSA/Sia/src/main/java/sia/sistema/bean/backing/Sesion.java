package sia.sistema.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;

import com.google.api.client.util.Strings;

import org.primefaces.PrimeFaces;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sia.constantes.Constantes;
import sia.ldap.ActiveDirectory;
import sia.modelo.ApCampo;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.MenuSiOpcionVo;
import sia.servicios.sistema.vo.SiModuloVo;
import sia.servicios.usuario.impl.RhTipoGerenciaImpl;
import sia.sistema.bean.support.FacesUtils;
import sia.util.Env;
import sia.util.SessionUtils;

/**
 *
 * @author Héctor
 */
@Named(value = "sesion")
@SessionScoped
@Slf4j
public class Sesion implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String LOGIN = "/principal.xhtml";
    public static final String MAIN = "index";
    public static final String USER = "user";

    @Getter
    private Properties ctx;

    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private ApCampoImpl apCampoImpl;
    @Inject
    private SiUsuarioTipoImpl siUsuarioTipoImpl;
    @Inject
    private RhTipoGerenciaImpl rhTipoGerenciaImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRol;
    @Inject
    private SiModuloImpl siModuloImpl;
    @Inject
    private SiParametroImpl parametroImpl;

    //-- Atributos
    public static final String CREATE_OPERATION = FacesUtils.getKeyResourceBundle("sistema.crear"); //genera el objeto
    public static final String UPDATE_OPERATION = FacesUtils.getKeyResourceBundle("sistema.actualizar");

//se fija el objeto a modificar
    private TreeMap<String, Boolean> controladorPopups = new TreeMap<>();
    private final Calendar calendario = Calendar.getInstance();
    
    @Getter
    private final Date fecha = calendario.getTime();
    
    private Usuario usuario;

    @Getter
    @Setter
    private UsuarioVO usuarioVo;

    @Getter
    @Setter
    private UsuarioVO usuarioVoAlta;

    @Getter
    @Setter
    private int idGerencia;

    @Getter
    @Setter
    private int idCampo;

    @Getter
    @Setter
    private String u;
    
    @Getter
    @Setter
    private String c;
    
    private boolean olvidoClave;
    private boolean visible = true;

    private String rfcCompania;
    private List<SiModuloVo> listaModulo;

    @Getter
    @Setter
    private List<CampoUsuarioPuestoVo> camposPorUsuario;

    @Getter
    @Setter
    private List<MenuSiOpcionVo> listaMenu;

    @Getter
    @Setter
    private DataModel lista;

    public String login() {
        String accion = Constantes.VACIO;

        try {
            // Checamos si existe el usuario
            if (Strings.isNullOrEmpty(getC())) {
                FacesUtils.addInfoMessage("Es necesario introducir la contraseña.");
            } else {
                usuario = usuarioImpl.find(getU());

                if (usuario == null) {
                    FacesUtils.addInfoMessage("Usuario no encontrado.");
                    setUsuarioVo(null);
                    setUsuarioVoAlta(null);
                    setUsuario(null);
                    accion = Constantes.VACIO;
                    setU(Constantes.VACIO);
                    setC(Constantes.VACIO);
                } else {
                    boolean autenticado;

                    if (usuario.getUsuarioDirectorio() == null || usuario.getUsuarioDirectorio().isEmpty()) {
                        autenticado = autenticarSIA();
                    } else {
                        autenticado = autenticarAD();

                        if (!autenticado) {
                            //si no se pudo autenticar contra AD, intentamos contra SIA
                            autenticado = autenticarSIA();
                        }
                    }

                    if (autenticado) {
                        olvidoClave = false;
                        
                        //TODO : establecer como variables en contexto y obtenerlos de ahí
                        llenarUsuarioVO(usuario);
                        setUsuarioVoAlta(null);
                        setIdCampo(usuarioVo.getIdCampo());
                        setRfcCompania(usuarioVo.getRfcEmpresa());
                        //
                        taerPendiente();

                        accion = "/principal.xhtml?faces-redirect=true";
                        setU(Constantes.VACIO);
                        setC(Constantes.VACIO);
                        log.info("USUARIO CONECTADO : {}", usuario.getId());
                        traerCampo();

                        SessionUtils.setAttribute(USER, usuario);

                        ctx = new Properties();
                        
                        subirValoresContexto();

                    } else {
                        FacesUtils.addInfoMessage("Usuario o contraseña es incorrecta.");
                        setUsuarioVoAlta(null);
                        setUsuarioVo(null);
                        setUsuario(null);
                        this.olvidoClave = true;
                        accion = Constantes.VACIO;
                    }

                }
            }
        } catch (NoSuchAlgorithmException e) {
            log.error(Constantes.VACIO, e);

            setUsuario(null);
            setUsuarioVo(null);
            setUsuarioVoAlta(null);
            FacesUtils.addInfoMessage("Ocurrió una excepción, favor de contactar con el equipo del SIA al correo soportesia@ihsa.mx");
        }

        return accion;
    }

    
    private void subirValoresContexto() {
        Env.setContext(ctx, Env.SESSION_ID, SessionUtils.getSession().getId());
        Env.setContext(ctx, Env.CLIENT_INFO, SessionUtils.getClientInfo(SessionUtils.getRequest()));
        Env.setContext(ctx, Env.PUNTO_ENTRADA, "Sia");
        Env.setContext(ctx, Env.PROYECTO_ID, usuarioVo.getIdCampo());
        Env.setContext(ctx, Env.CODIGO_COMPANIA, usuarioVo.getRfcEmpresa());
    }
    
    
    private void llenarUsuarioVO(Usuario u) {
        //Traer puesto del usuario
        setUsuarioVo(new UsuarioVO());
        usuarioVo.setId(u.getId());
        usuarioVo.setNombre(u.getNombre());
        usuarioVo.setClave(u.getClave());
        usuarioVo.setPuesto(traerPuestoUsusaio(u.getId(), u.getApCampo().getId()));
        usuarioVo.setMail(u.getEmail());
        usuarioVo.setDestinatarios(u.getDestinatarios());
        usuarioVo.setRfc(u.getRfc());
        usuarioVo.setTelefono(u.getTelefono());
        usuarioVo.setExtension(u.getExtension());
        usuarioVo.setCelular(u.getCelular());
        usuarioVo.setSexo(u.getSexo());
        //
        usuarioVo.setIdCampo(u.getApCampo().getId());
        usuarioVo.setCampo(u.getApCampo().getNombre());
        usuarioVo.setFotoCampo(u.getApCampo().getFoto());
        //Se agrega a la sesion de SIA el RFC de la empresa a la que pertenece el empleado
        usuarioVo.setRfcEmpresa(u.getApCampo().getCompania().getRfc());
        //
        usuarioVo.setIdGerencia(u.getGerencia().getId());
        usuarioVo.setActivo(u.isActivo());
        usuarioVo.setPregunta(u.getPreguntaSecreta());
        usuarioVo.setRespuesta(u.getRespuestaPreguntaSecreta());
        //Otros 5
        usuarioVo.setFechaIngreso(u.getFechaIngreso());

        if (u.getSgOficina() != null) {
            usuarioVo.setOficina(u.getSgOficina().getNombre());
            usuarioVo.setIdOficina(u.getSgOficina().getId());
        }

        if (u.getGerencia() == null) {
            setIdGerencia(-1);
        } else {
            usuarioVo.setIdGerencia(u.getGerencia().getId());
            usuarioVo.setGerencia(u.getGerencia().getNombre());
        }

        if (u.getSgEmpresa() != null) {
            usuarioVo.setIdNomina(u.getSgEmpresa().getId());
        }
        usuarioVo.setAdministraTI(false);
        //buscar si es un usuario que administra TI
        List<UsuarioTipoVo> usuarioList = getUsuariosAdministranTI();
        if (usuarioList != null && !usuarioList.isEmpty()) {
            for (UsuarioTipoVo vo : usuarioList) {
                if (vo.getIdUser().equals(usuarioVo.getId())) {
                    log.info("ES USUARIO QUE ADMINISTRA TI");
                    usuarioVo.setAdministraTI(true);
                    break;
                }
            }
        }
        usuarioVo.setUsuarioInSessionGerente(usuarioResponsableForAnyGerencia());
        usuarioVo.setLiberaUsuarios(isUsuarioInSessionLiberador(this.usuarioVo.getId()));

        UsuarioRolVo uvo = traerRolPrincipal(u.getId(), u.getApCampo().getId());

        if (uvo != null) {
            usuarioVo.setRolPrincipal(uvo.getNombreRol());
            usuarioVo.setRolId(uvo.getIdRol());
        }

        listaMenu = null;
    }

    public boolean isUsuarioInSessionLiberador(String idUsuario) {
        return this.rhTipoGerenciaImpl.isLiberador(idUsuario);
    }

    public UsuarioRolVo traerRolPrincipal(String idUsuario, int idCamp) {
        return siUsuarioRol.traerRolPrincipal(idUsuario, Constantes.MODULO_SGYL, idCamp);
    }

    private List<UsuarioTipoVo> getUsuariosAdministranTI() {
        return this.siUsuarioTipoImpl.getListUser(19, 1);
    }

    public boolean usuarioResponsableForAnyGerencia() {
        return gerenciaImpl.isUsuarioResponsableForAnyGerencia(-1, getUsuarioVo().getId(), false);
    }

    private String traerPuestoUsusaio(String userId, int campoId) {
        return apCampoUsuarioRhPuestoImpl.getPuestoPorUsurioCampo(userId, campoId);
    }

    private boolean autenticarSIA() throws NoSuchAlgorithmException {
        return getUsuario().getId().equals(getU())
                && this.getUsuario().getClave().equals(encriptar(getC()));
    }

    public String encriptar(String text) throws NoSuchAlgorithmException {
        return usuarioImpl.encriptar(text);
    }

    public void taerPendiente() {
        if (usuarioVo != null) {
            listaModulo = traerModulo(usuarioVo, 0);
        }
    }

    public List<SiModuloVo> traerModulo(UsuarioVO usuario, int moduloID) {
        return siModuloImpl.getModulosUsuario(usuario.getId(), moduloID);
    }

    public List<SiModuloVo> getListaModulos() {
        return listaModulo;
    }

    public String getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String retVal = Constantes.VACIO;

        try {
            retVal = sdf.format(getFecha());
        } catch (Exception e) {
            log.warn(Constantes.VACIO, e);
        }

        return retVal;

    }

    public String getArrancarModulo(int campo, String pagina) {
        return usuarioVo.getId()
                + "&ZWZ4W="
                + usuarioVo.getClave()
                + "&ZWZCA=" + campo
                + "&ZWZPA=" + pagina;
    }

    public String sustituirArrancarModuloPorCampo(String url, int campo, String pagina) {   
        String resultado;
        if (url == null) {
            resultado = Constantes.VACIO;
        } else {
            resultado = url.replace("@@AM@@", getArrancarModulo(campo, pagina));
        }
        return resultado;
    }
    
    

    public String sustituirArrancarModulo(String url) {
        String resultado;
        if (url == null) {
            resultado = Constantes.VACIO;
        } else {
            resultado = url.replace("@@AM@@", getArrancarModulo(0, ""));
        }
        return /*Configurador.urlSia() +*/ resultado + "?faces-redirect=true";
    }

    public void cerrarSesionExterno() {
        usuario = null;
        setU(Constantes.VACIO);
        setC(Constantes.VACIO);

        setUsuarioVoAlta(null);
        setUsuarioVo(null);

    }

    public void cerrarSesion() {
        log.info("CERRO SESION : {}", usuario.getId());
        usuario = null;
        setU(Constantes.VACIO);
        setC(Constantes.VACIO);
        setUsuarioVo(null);
        setUsuarioVoAlta(null);
        setCamposPorUsuario(null);
        ctx = null;
        
        PrimeFaces.current().executeScript(";refrescar();");
    }

    public void traerCampo() {
        try {
            listaBloquePorUsuario();
        } catch (Exception e) {
            log.warn(Constantes.VACIO, e);
        }
    }

    public void cambioCampoUsuario(int campoId) {
        try {
            cambiarUsuarioPuesto(usuario.getId(), usuario.getId(), campoId);
            //
            ApCampo campo = apCampoImpl.find(campoId);
            usuario.setApCampo(campo);
            //
            CampoUsuarioPuestoVo con = apCampoUsuarioRhPuestoImpl.findByUsuarioCampo(campoId, usuarioVo.getId());
            //
            usuarioVo.setIdCampo(campo.getId());
            usuarioVo.setCampo(campo.getNombre());
            usuarioVo.setFotoCampo(campo.getFoto());
            setRfcCompania(usuarioVo.getRfcEmpresa());
            usuarioVo.setGerencia(con.getGerencia());
            usuarioVo.setPuesto(con.getPuesto());
            setUsuarioVoAlta(null);
            
            subirValoresContexto();
            
            
            //
            PrimeFaces.current().executeScript(";$(dialogoUsuarioCampo).modal('hide');;");
            FacesContext.getCurrentInstance().getExternalContext().redirect(Constantes.URL_REL_SIA_PRINCIPAL);
        } catch (IOException ex) {
            log.error(Constantes.VACIO, ex);
        }
    }

    public void cambiarUsuarioPuesto(String idUsuario, String idUserModifico, int campo) {
        usuarioImpl.cambiarCampoUsuario(idUsuario, idUserModifico, campo);
        //
    }

    public void listaBloquePorUsuario() {
        try {
            if (getUsuarioVo() != null) {
                camposPorUsuario
                        = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(getUsuarioVo().getId());
            }
        } catch (RuntimeException e) {
            log.error("Ocurrio un error : : : : : {}", e.getMessage(), e);
        }
    }

    public String mostrarDatosUsuario() {
        return "/vistas/administracion/usuario/datosUsuario";
    }

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
        log.debug("Limpiando el componente: {} : {}", nombreFormulario, nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            log.error("Hubo algún error al limpiar el componente: {} : {}", nombreFormulario, nombreComponente);
        }
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

        visible = this.usuario == null;

    }

    public boolean isAnalistaCosto() {
        return isAnalistaCosto(usuario.getId(), usuario.getApCampo().getId());
    }

    public boolean isAnalistaCosto(String sesion, int idCamp) {
        UsuarioRolVo usrRol = null;
        try {
            usrRol = siUsuarioRol.findUsuarioRolVO(Constantes.ROL_VISTO_BUENO_COSTO, sesion, idCamp);
        } catch (Exception ex) {
            log.warn(Constantes.VACIO, ex);
        }
        return usrRol != null && usrRol.getIdUsuarioRol() > 0;
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
     * @return the controladorPopups
     */
    public Map<String, Boolean> getControladorPopups() {
        return controladorPopups;
    }

    /**
     * @param controladorPopups the controladorPopups to set
     */
    public void setControladorPopups(TreeMap<String, Boolean> controladorPopups) {
        this.controladorPopups = controladorPopups;
    }

    /**
     * @return the rfCompania
     */
    public String getRfcCompania() {
        return rfcCompania;
    }

    /**
     * @param rfcCompania the rfCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
        this.rfcCompania = rfcCompania;
    }

    /**
     * Autenticar al usuario contra Active Directory
     *
     * @return true si el usuario fue encontraro y la contraseña fue válida, false en caso
     * contrario.
     */
    private boolean autenticarAD() {
        boolean retVal = false;
        log.info("Autenticando contra Directorio ....");

        String directorio = parametroImpl.find(1).getDirectorioUsuarios();

        if (Strings.isNullOrEmpty(directorio)) {
            throw new IllegalStateException("No está configurado el directorio para autenticación de usuarios.");
        } else {
            try {
                //Creating instance of ActiveDirectory, if it doesn't blows up, then the user/pass are the right ones
                ActiveDirectory activeDirectory = new ActiveDirectory(getU(), getC(), directorio);
                activeDirectory.closeLdapConnection();

                retVal = true;
            } catch (NamingException e) {
                log.warn("*** Al validar contra AD ...", e);
            }
        }

        return retVal;
    }
}
