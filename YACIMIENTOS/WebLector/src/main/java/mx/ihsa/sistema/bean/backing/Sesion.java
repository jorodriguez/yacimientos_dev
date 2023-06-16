package mx.ihsa.sistema.bean.backing;

import java.io.Serializable;
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

import mx.ihsa.constantes.Constantes;
import mx.ihsa.dominio.modelo.usuario.vo.UsuarioVO;
import mx.ihsa.dominio.vo.UsuarioRolVo;
import mx.ihsa.excepciones.GeneralException;
import mx.ihsa.servicios.catalogos.impl.UsuarioImpl;
import mx.ihsa.servicios.sistema.impl.SiUsuarioRolImpl;
import mx.ihsa.servicios.sistema.vo.MenuSiOpcionVo;

import org.primefaces.PrimeFaces;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mx.ihsa.sistema.bean.support.FacesUtils;
import mx.ihsa.util.Env;
import mx.ihsa.util.SessionUtils;

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
   
    @Getter
    @Setter
    private UsuarioVO usuarioSesion;

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
                usuarioSesion = usuarioImpl.login(getU(), getC());

                accion = "/main.xhtml?faces-redirect=true";

                log.info("USUARIO CONECTADO : {}", usuarioSesion.getEmail());

                SessionUtils.setAttribute(USER, usuarioSesion);

                ctx = new Properties();

                subirValoresContexto();
            
        } catch (GeneralException e) {
            log.error(Constantes.VACIO, e);
            setUsuarioSesion(null);
            FacesUtils.addInfoMessage(e.getMessage());
        } finally {
            setU(Constantes.VACIO);
            setC(Constantes.VACIO);
        }

        System.out.println("Accion = " + accion);

        return accion;
    }

    private void subirValoresContexto() {
        Env.setContext(ctx, Env.SESSION_ID, SessionUtils.getSession().getId());
        Env.setContext(ctx, Env.CLIENT_INFO, SessionUtils.getClientInfo(SessionUtils.getRequest()));
        //Env.setContext(ctx, Env.PUNTO_ENTRADA, "Sia");
    }

    /*private void llenarUsuarioVO(Usuario u) {
        //Traer puesto del usuario
        setUsuarioVo(new UsuarioVO());
        usuarioVo.setId(u.getId());
        usuarioVo.setNombre(u.getNombre());
        usuarioVo.setClave(u.getClave());
        usuarioVo.setPuesto("Puesto");
        usuarioVo.setEmail(u.getEmail());
        usuarioVo.setTelefono(u.getTelefono());
        usuarioVo.setSexo(u.getSexo());

        listaMenu = null;
    }*/

    public UsuarioRolVo traerRolPrincipal(String idUsuario, int idCamp) {
        //return siUsuarioRol.traerRolPrincipal(idUsuario, Constantes.MODULO_SGYL, idCamp);
        return null;
    }

    /*private boolean autenticarSIA() throws NoSuchAlgorithmException {
        return getUsuario().getId().equals(getU())
                && this.getUsuario().getClave().equals(encriptar(getC()));
    }
    public String encriptar(String text) throws NoSuchAlgorithmException {
        return usuarioImpl.encriptar(text);
    }*/

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
        return usuarioSesion.getId()
                + "&ZWZ4W="
                + usuarioSesion.getClave()
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
        usuarioSesion = null;
        setU(Constantes.VACIO);
        setC(Constantes.VACIO);
    }

    public void cerrarSesion() {
        log.info("CERRO SESION : {}", usuarioSesion.getId());
        usuarioSesion = null;
        setU(Constantes.VACIO);
        setC(Constantes.VACIO);
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
