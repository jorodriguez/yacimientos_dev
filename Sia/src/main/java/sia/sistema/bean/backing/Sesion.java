/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.backing;

import com.google.api.client.util.Strings;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.model.DataModel;
import javax.naming.NamingException;
import org.primefaces.PrimeFaces;
import org.primefaces.component.effect.Effect;
import org.primefaces.event.SelectEvent;
import sia.catalogos.bean.model.UsuarioListModel;
import sia.constantes.Constantes;
import sia.ldap.ActiveDirectory;
import sia.modelo.ApCampo;
import sia.modelo.Compania;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.SiModuloVo;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor
 */
@ManagedBean(name = "sesion")
@SessionScoped
public class Sesion implements Serializable {

    public static final long serialVersionUID = 1L;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @ManagedProperty(value = "#{usuarioListModel}")
    private UsuarioListModel usuarioListModel;

    @EJB
    private SiParametroImpl parametrosSistema;

    //-- Atributos
    public static final String CREATE_OPERATION = FacesUtils.getKeyResourceBundle("sistema.crear"); //genera el objeto
    public static final String UPDATE_OPERATION = FacesUtils.getKeyResourceBundle("sistema.actualizar");

//se fija el objeto a modificar
    private TreeMap<String, Boolean> controladorPopups = new TreeMap<>();
    private final Calendar calendario = Calendar.getInstance();
    private final Date fecha = calendario.getTime();
    private Usuario usuario;
    private String u, c;
    private boolean olvidoClave;
    private boolean visible = true;

    private String rfcCompania;
    private List<SiModuloVo> listaModulo;
    private DataModel camposPorUsuario;

    public Sesion() {
    }

    public String login() {
        String accion = Constantes.VACIO;

        try {
            // Checamos si existe el usuario
            if (getC().trim().isEmpty()) {
                FacesUtils.addInfoMessage("Es necesario introducir la contraseña.");
            } else {
                usuario = usuarioListModel.buscarPorId(getU());

                if (usuario == null) {
                    FacesUtils.addInfoMessage("Usuario no encontrado.");
                    usuarioListModel.setUsuarioVO(null);
                    usuarioListModel.setUsuarioVOAlta(null);
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
                        usuarioListModel.llenarUsuarioVO(usuario);
                        usuarioListModel.setUsuarioVOAlta(null);
                        usuarioListModel.setIdCampo(usuarioListModel.getUsuarioVO().getIdCampo());
                        setRfcCompania(usuarioListModel.getUsuarioVO().getRfcEmpresa());

                        accion = "/principal";
                        setU(Constantes.VACIO);
                        setC(Constantes.VACIO);
                        LOGGER.info(this, "USUARIO CONECTADO : " + usuario.getId());
                        traerCampo();
                    } else {
                        FacesUtils.addInfoMessage("Usuario o contraseña es incorrecta.");
                        usuarioListModel.setUsuarioVOAlta(null);
                        usuarioListModel.setUsuarioVO(null);
                        setUsuario(null);
                        this.olvidoClave = true;
                        accion = Constantes.VACIO;
                    }

                }
            }
        } catch (NoSuchAlgorithmException | NamingException e) {
            LOGGER.fatal(this, Constantes.VACIO, e);

            setUsuario(null);
            usuarioListModel.setUsuarioVO(null);
            usuarioListModel.setUsuarioVOAlta(null);
            FacesUtils.addInfoMessage("Ocurrió una excepción, favor de contactar con el equipo del SIA al correo soportesia@ihsa.mx");
        }

        return accion;
    }

    private boolean autenticarSIA() throws NoSuchAlgorithmException {
        //LOGGER.info("Autenticando con SIA ....");

        return getUsuario().getId().equals(getU())
                && this.getUsuario().getClave().equals(usuarioListModel.encriptar(getC()));
    }

    public void taerPendiente() {
        if (usuarioListModel.getUsuarioVO() != null) {
            listaModulo = usuarioListModel.traerModulo(usuarioListModel.getUsuarioVO(), 0);
        }
    }

    public List<SiModuloVo> getListaModulos() {
        return listaModulo;
    }

    public String getAño() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String retVal = Constantes.VACIO;

        try {
            retVal = sdf.format(getFecha());
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return retVal;

    }

    public String getArrancarModulo(int campo, String pagina) {
        return usuarioListModel.getUsuarioVO().getId()
                + "&ZWZ4W="
                + usuarioListModel.getUsuarioVO().getClave()
                + "&ZWZCA=" + campo
                + "&ZWZPA=" + pagina;
    }

    public String sustituirArrancarModuloPorCampo(String url, int campo, String pagina) {
        String resultado;
        if (url == null) {
            resultado = Constantes.VACIO;
        } else {
            resultado = url.replaceAll("@@AM@@", getArrancarModulo(campo, pagina));
        }
        return resultado;
    }

    public String sustituirArrancarModulo(String url) {
        String resultado;
        if (url == null) {
            resultado = Constantes.VACIO;
        } else {
            resultado = url.replaceAll("@@AM@@", getArrancarModulo(0, ""));
        }
        return resultado;
    }

    public void cerrarSesionExterno() {
        usuario = null;
        setU(Constantes.VACIO);
        setC(Constantes.VACIO);

        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setUsuarioVO(null);

        usuarioListModel = null;
        //conversationsManager.finalizeAllConversations();
    }

    public void cerrarSesion() {
        LOGGER.info(this, "CERRO SESION : " + usuario.getId());
        usuario = null;
        setU(Constantes.VACIO);
        setC(Constantes.VACIO);
        usuarioListModel.setUsuarioVO(null);
        usuarioListModel.setUsuarioVOAlta(null);
        setCamposPorUsuario(null);
        PrimeFaces.current().executeScript(";refrescar();");
        //conversationsManager.finalizeAllConversations();
    }

    public void traerCampo() {
        try {
            setCamposPorUsuario(usuarioListModel.listaBloquePorUsuario());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void cambioCampoUsuario(SelectEvent event) {
        try {
            CampoUsuarioPuestoVo con = (CampoUsuarioPuestoVo) event.getObject();
            usuarioListModel.cambiarUsuarioPuesto(usuario.getId(), usuario.getId(), con.getIdCampo());
            //
            ApCampo campo = new ApCampo();
            campo.setId(con.getIdCampo());
            campo.setNombre(con.getCampo());
            campo.setCompania(new Compania(con.getRfcCompania()));
            usuario.setApCampo(campo);
            //
            //
            usuarioListModel.getUsuarioVO().setIdCampo(con.getIdCampo());
            usuarioListModel.getUsuarioVO().setCampo(con.getCampo());
            setRfcCompania(usuarioListModel.getUsuarioVO().getRfcEmpresa());
            usuarioListModel.getUsuarioVO().setGerencia(con.getGerencia());
            usuarioListModel.getUsuarioVO().setPuesto(con.getPuesto());
            usuarioListModel.setUsuarioVOAlta(null);
            //
            PrimeFaces.current().executeScript(";$(dialogoUsuarioCampo).modal('hide');;"
            );
            FacesContext.getCurrentInstance().getExternalContext().redirect(Constantes.URL_REL_SIA_PRINCIPAL);
        } catch (IOException ex) {
            Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String mostrarDatosUsuario() {
        this.usuarioListModel.setCambiarPass("no");
        return "/vistas/administracion/usuario/datosUsuario";
    }
    

    public String mostrarPanel() {
        String retVal = Constantes.VACIO;

        if (usuarioListModel.getUsuarioVOAlta() == null) {
            FacesUtils.addInfoMessage("Lo sentimos, usuario no encontrado, favor de verificar el usuario");
        } else {
            usuarioListModel.setAgregarNuevoPass(0);
            retVal = "/vistas/administracion/usuario/olvidoPassword";
        }

        return retVal;
    }

    public Effect getHighlightAreaError() {
        return null;
    }

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
        LOGGER.debug(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            LOGGER.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
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
        return usuarioListModel.isAnalistaCosto(usuario.getId(), usuario.getApCampo().getId());
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
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
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
//    public void showComentar(){
//        this.comentar = !this.comentar;
//    }

    /**
     * @param usuarioListModel the usuarioListModel to set
     */
    public void setUsuarioListModel(UsuarioListModel usuarioListModel) {
        this.usuarioListModel = usuarioListModel;
    }

    /**
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
     * @return the camposPorUsuario
     */
    public DataModel getCamposPorUsuario() {
        return camposPorUsuario;
    }

    /**
     * @param camposPorUsuario the camposPorUsuario to set
     */
    public void setCamposPorUsuario(DataModel camposPorUsuario) {
        this.camposPorUsuario = camposPorUsuario;
    }

    private boolean autenticarAD() throws NamingException {
        boolean retVal = false;
        LOGGER.info("Autenticando contra Directorio ....");

        String directorio = parametrosSistema.find(1).getDirectorioUsuarios();

        if (Strings.isNullOrEmpty(directorio)) {
            throw new IllegalStateException("No está configurado el directorio para autenticación de usuarios.");
        } else {
            //Creating instance of ActiveDirectory, if it doesn't blows up, then the user/pass are the right ones
            ActiveDirectory activeDirectory = new ActiveDirectory(getU(), getC(), directorio);
            activeDirectory.closeLdapConnection();

            retVal = true;
        }

        return retVal;
    }
}
