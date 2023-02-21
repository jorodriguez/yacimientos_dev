package lector.sistema.bean.backing;

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

import com.google.api.client.util.Strings;
import lector.constantes.Constantes;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.dominio.vo.UsuarioRolVo;
import lector.modelo.Usuario;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.servicios.sistema.impl.SiUsuarioRolImpl;
import lector.servicios.sistema.vo.MenuSiOpcionVo;

import org.primefaces.PrimeFaces;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lector.sistema.bean.support.FacesUtils;
import lector.util.Env;
import lector.util.SessionUtils;

/**
 *
 */
@Named(value = "sesion")
@SessionScoped
@Slf4j
public class Sesion implements Serializable {

    public static final long serialVersionUID = 1L;

    //public static final String LOGIN = "/main.xhtml";
    public static final String MAIN = "index";
    public static final String USER = "user";

    @Getter
    private Properties ctx;

    @Inject
    private UsuarioImpl usuarioImpl;        
        
    @Inject
    private SiUsuarioRolImpl siUsuarioRol;
    

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
    //private List<SiModuloVo> listaModulo;
    
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
                    
                    System.out.println("Encontrado");
                    
                    if (autenticarSIA()) {                    
                        olvidoClave = false;
                        
                        llenarUsuarioVO(usuario);

                        accion = "/main.xhtml?faces-redirect=true";
                        setU(Constantes.VACIO);
                        setC(Constantes.VACIO);
                        log.info("USUARIO CONECTADO : {}", usuario.getId());

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
            FacesUtils.addInfoMessage("Ocurrió una excepción, favor de contactar con el equipo de Soporte Técnico.");
        }
        
        System.out.println("Accion = "+accion);

        return accion;
    }

    
    private void subirValoresContexto() {
        Env.setContext(ctx, Env.SESSION_ID, SessionUtils.getSession().getId());
        Env.setContext(ctx, Env.CLIENT_INFO, SessionUtils.getClientInfo(SessionUtils.getRequest()));
        //Env.setContext(ctx, Env.PUNTO_ENTRADA, "Sia");
    }
    
    
    private void llenarUsuarioVO(Usuario u) {
        //Traer puesto del usuario
        setUsuarioVo(new UsuarioVO());
        usuarioVo.setId(u.getId());
        usuarioVo.setNombre(u.getNombre());
        usuarioVo.setClave(u.getClave());
        usuarioVo.setPuesto("Puesto");
        usuarioVo.setMail(u.getEmail());                
        usuarioVo.setTelefono(u.getTelefono());
        usuarioVo.setSexo(u.getSexo());
        
        usuarioVo.setAdministraTI(false);
        
        listaMenu = null;
    }
   

    public UsuarioRolVo traerRolPrincipal(String idUsuario, int idCamp) {
        //return siUsuarioRol.traerRolPrincipal(idUsuario, Constantes.MODULO_SGYL, idCamp);
        return null;
    }

    
    private boolean autenticarSIA() throws NoSuchAlgorithmException {
        return getUsuario().getId().equals(getU())
                && this.getUsuario().getClave().equals(encriptar(getC()));
    }

    public String encriptar(String text) throws NoSuchAlgorithmException {
        return usuarioImpl.encriptar(text);
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
        ctx = null;
        
        PrimeFaces.current().executeScript(";refrescar();");
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

}
