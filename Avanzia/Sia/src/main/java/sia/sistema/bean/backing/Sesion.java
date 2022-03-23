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
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.model.DataModel;
import javax.inject.Named;
import javax.naming.NamingException;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.ldap.ActiveDirectory;
import sia.modelo.ApCampo;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.campo.nuevo.impl.RhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.RhEmpleadoMaterialImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.requisicion.impl.CadenasMandoImpl;
import sia.servicios.rh.impl.RhCampoGerenciaImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sgl.impl.SgEmpresaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiRelRolOpcionImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.MenuSiOpcionVo;
import sia.servicios.sistema.vo.SiModuloVo;
import sia.servicios.usuario.impl.RhTipoGerenciaImpl;
import sia.servicios.usuario.impl.RhUsuarioGerenciaImpl;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor
 */
@Named(value = "sesion")
@SessionScoped
public class Sesion implements Serializable {

    public static final long serialVersionUID = 1L;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private CadenasMandoImpl cadenasMandoImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private RhPuestoImpl rhPuestoImpl;
    @Inject
    private ApCampoImpl apCampoImpl;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgEmpresaImpl sgEmpresaImpl;
    @Inject
    private RhEmpleadoMaterialImpl rhEmpleadoMaterialImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    private RhUsuarioGerenciaImpl rhUsuarioGerenciaImpl;
    @Inject
    private SiUsuarioTipoImpl siUsuarioTipoImpl;
    @Inject
    private RhTipoGerenciaImpl rhTipoGerenciaImpl;
    @Inject
    private RhUsuarioGerenciaImpl usuarioGerenciaImpl;
    @Inject
    private RhCampoGerenciaImpl rhCampoGerenciaImpl;
    @Inject
    private CoNoticiaImpl coNoticiaImpl;
    @Inject
    private SiRolImpl siRolImpl;
    @Inject
    private SiRelRolOpcionImpl siRolOpcion;
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
    private String u, c;
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

    public Sesion() {
    }

    public String login() {
        String accion = Constantes.VACIO;

        try {
            // Checamos si existe el usuario
            if (getC().trim().isEmpty()) {
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
                        llenarUsuarioVO(usuario);
                        setUsuarioVoAlta(null);
                        setIdCampo(usuarioVo.getIdCampo());
                        setRfcCompania(usuarioVo.getRfcEmpresa());
                        //
                        taerPendiente();

                        accion = "/principal";
                        setU(Constantes.VACIO);
                        setC(Constantes.VACIO);
                        LOGGER.info(this, "USUARIO CONECTADO : " + usuario.getId());
                        traerCampo();
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
        } catch (NoSuchAlgorithmException | NamingException e) {
            LOGGER.fatal(this, Constantes.VACIO, e);

            setUsuario(null);
            setUsuarioVo(null);
            setUsuarioVoAlta(null);
            FacesUtils.addInfoMessage("Ocurrió una excepción, favor de contactar con el equipo del SIA al correo soportesia@ihsa.mx");
        }

        return accion;
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
                    LOGGER.info(this, "ES USUARIO QUE ADMINISTRA TI");
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
        //LOGGER.info("Autenticando con SIA ....");

        return getUsuario().getId().equals(getU())
                && this.getUsuario().getClave().equals(encriptar(getC()));
    }

    public String encriptar(String text) throws NoSuchAlgorithmException {
        //LOGGER.info(this, "Text: ");
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

        setUsuarioVoAlta(null);
        setUsuarioVo(null);

    }

    public void cerrarSesion() {
        LOGGER.info(this, "CERRO SESION : " + usuario.getId());
        usuario = null;
        setU(Constantes.VACIO);
        setC(Constantes.VACIO);
        setUsuarioVo(null);
        setUsuarioVoAlta(null);
        setCamposPorUsuario(null);
        PrimeFaces.current().executeScript(";refrescar();");
    }

    public void traerCampo() {
        try {
            listaBloquePorUsuario();
        } catch (Exception e) {
            LOGGER.error("", e);
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
            setRfcCompania(usuarioVo.getRfcEmpresa());
            usuarioVo.setGerencia(con.getGerencia());
            usuarioVo.setPuesto(con.getPuesto());
            setUsuarioVoAlta(null);
            //
            PrimeFaces.current().executeScript(";$(dialogoUsuarioCampo).modal('hide');;"
            );
            FacesContext.getCurrentInstance().getExternalContext().redirect(Constantes.URL_REL_SIA_PRINCIPAL);
        } catch (IOException ex) {
            Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
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
            LOGGER.fatal(this, "Ocurrio un error : : : : : " + e.getMessage(), e);
        }
    }

    public String mostrarDatosUsuario() {
        return "/vistas/administracion/usuario/datosUsuario";
    }

    /*
    public String mostrarPanel() {
        String retVal = Constantes.VACIO;

        if (setUsuarioVoAlta() == null) {
            FacesUtils.addInfoMessage("Lo sentimos, usuario no encontrado, favor de verificar el usuario");
        } else {
            setAgregarNuevoPass(0);
            retVal = "/vistas/administracion/usuario/olvidoPassword";
        }

        return retVal;
    }

    public Effect getHighlightAreaError() {
        return null;
    }
     */
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
        return isAnalistaCosto(usuario.getId(), usuario.getApCampo().getId());
    }

    public boolean isAnalistaCosto(String sesion, int idCamp) {
        UsuarioRolVo usrRol = null;
        try {
            usrRol = siUsuarioRol.findUsuarioRolVO(Constantes.ROL_VISTO_BUENO_COSTO, sesion, idCamp);
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
        return usrRol != null && usrRol.getIdUsuarioRol() > 0;
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

    private boolean autenticarAD() throws NamingException {
        boolean retVal = false;
        LOGGER.info("Autenticando contra Directorio ....");

        String directorio = parametroImpl.find(1).getDirectorioUsuarios();

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
