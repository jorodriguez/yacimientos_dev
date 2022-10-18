/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.ihsa.gr.sistema.soporte;

import org.primefaces.PrimeFaces;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.primefaces.event.TabChangeEvent;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgViaje;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.gr.vo.GrArchivoVO;
import sia.modelo.gr.vo.GrIntercepcionVO;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoComentarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.gr.impl.GrArchivoImpl;
import sia.servicios.gr.impl.GrInterseccionImpl;
import sia.servicios.gr.impl.GrPuntoImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeSiMovimientoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroSiMovimientoImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.Env;
import sia.util.UtilLog4j;
import sia.util.UtilSia;

/**
 *
 * @author jcarranza
 */
@Named(value = "sesion")
@SessionScoped
public class Sesion implements Serializable {

    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private SiModuloImpl siModuloImpl;
    @Inject
    private GrArchivoImpl grArchivoImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private CoNoticiaImpl coNoticiaImpl;
    @Inject
    private SgViajeroSiMovimientoImpl sgViajeroSiMovimientoImpl;
    @Inject
    private SgViajeroImpl sgViajeroImpl;
    @Inject
    private SiMovimientoImpl siMovimientoImpl;
    @Inject
    private SiOperacionImpl siOperacionImpl;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaImpl;
    @Inject
    private GrInterseccionImpl grInterseccionImpl;
    @Inject
    private SgViajeSiMovimientoImpl sgViajeSiMovimientoImpl;
    @Inject
    private GrPuntoImpl grPuntoImpl;
    @Inject
    private SgViajeVehiculoImpl sgViajeVehiculoImpl;
    @Inject
    private CoComentarioImpl coComentarioImpl;

    //private GrViaje grViaje = (GrViaje) FacesUtilsBean.getManagedBean("grViaje");
    //Clases
    private TreeMap<String, Boolean> controladorPopups = new TreeMap<String, Boolean>();
    private final String userTxt = "Usuario";
    private final String passTxt = "Contraseña";
    private String user;
    private String pass;
    private UsuarioVO usuarioVO;
    private boolean admin;
    private boolean grVia;
    private Usuario usuario;
    private boolean desdeSia;
    private List<GrArchivoVO> alertas;
    private List<RutaTerrestreVo> rutasPausa;
    private List<RutaTerrestreVo> rutasPausaGerente;
    private List<RutaTerrestreVo> rutasProceso;
    private List<RutaTerrestreVo> rutasPendienes;
    private int numDias = 0;
    private List<ViajeVO> lstViajesInt;
    private List<GrIntercepcionVO> intercepcionesProgramadas;
    private String txtComentario;
    private int idNoticia;
    private int idViajeIntA;
    private int idViajeIntB;
    private int idViajeLlegadaPS;
    private int idPSLlegada;
    private GrIntercepcionVO intercepcion;
    private boolean responsable;
    private List<SelectItem> puntosItems;
    private int idViajeroGRAut;
    private String grAutTxtMotivo;
    private ViajeVO viajeD;
    private ViajeVO viajeB;
    private Properties ctx;
    private GrArchivoVO mapa;
    /**
     * Creates a new instance of Sesion
     */
    public Sesion() {
        this.user = "Usuario";
        this.pass = "Contraseña";
    }

    public String accederSistema() throws NoSuchAlgorithmException {
        String p = "";
        if (user != null && pass != null && !user.isEmpty() && !this.userTxt.equals(user)) {
            setUsuario(usuarioImpl.find(getUser()));
            if (getUsuario() == null) {
                FacesUtilsBean.addErrorMessage(FacesUtilsBean.getKeyResourceBundle("sia.sistema.principal.acceder.usuario.no.encontrado"));
            } else if (!getUsuario().isEliminado() && getUsuario().getId().equals(getUser())) {
                boolean entrar = false;
                if ((getPass().length() >= 35 && getUsuario().getClave().equals(getPass()))) {
                    entrar = true;
                    this.setDesdeSia(true);
                } else if (getPass().length() < 35 && getUsuario().getClave().equals(this.encriptar(getPass()))) {
                    entrar = true;
                    this.setDesdeSia(false);
                }
                if (entrar) {
                    setAdmin(siUsuarioRolImpl.buscarRolPorUsuarioModulo(getUsuario().getId(), Constantes.MODULO_GR, "GRADM", 0));
                    setGrVia(siUsuarioRolImpl.buscarRolPorUsuarioModulo(getUsuario().getId(), Constantes.MODULO_GR, "GRVIA", 0));
                    List<ApCampoGerenciaVo> lst = apCampoGerenciaImpl.findByCampoGerenciaResponsable(0, 0, getUsuario().getId(), false);
                    setResponsable(lst != null && !lst.isEmpty());
                    if (isAdmin() || isGrVia() || siUsuarioRolImpl.buscarRolPorUsuarioModulo(getUsuario().getId(), Constantes.MODULO_GR, "ROLGR1", 0)) {
                        setUser(null);
                        setPass(null);
                        llenarUsuarioVO(getUsuario());
                        p = "/GR/vistas/gr/principal.xhtml?faces-redirect=true";
                        if (isAdmin() || isGrVia()) {
                            this.setRutas();
                        } else {
                            this.setAlertas(this.grArchivoImpl.getAlertas());
                            if (isResponsable()) {
                                this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
                            }
                        }
                        UtilLog4j.log.info(this, "INICIO SESION GR : " + getUsuario().getId());
                    } else {
                        FacesUtilsBean.addErrorMessage(FacesUtilsBean.getKeyResourceBundle("sia.sistema.principal.acceder.sin.permiso"));
                    }
                } else {
                    setUsuarioVO(null);
                    FacesUtilsBean.addErrorMessage(FacesUtilsBean.getKeyResourceBundle("sia.sistema.principal.acceder.incorrecto"));
                }
            } else {
                setUsuarioVO(null);
                FacesUtilsBean.addErrorMessage(FacesUtilsBean.getKeyResourceBundle("sia.sistema.principal.acceder.incorrecto"));
            }
        } else {
            setUsuarioVO(null);
            FacesUtilsBean.addErrorMessage(FacesUtilsBean.getKeyResourceBundle("sia.sistema.principal.acceder.incorrecto"));
        }
        return p;
    }

    public String salir() {
        usuarioVO = null;
        this.setUser(this.getUserTxt());
        this.setPass(this.getPassTxt());
        String ret = "";
        if (this.isDesdeSia()) {
            redireccionar(Constantes.URL_REL_SIA_SIGN_OUT);
        } else {
            redireccionar(Constantes.URL_REL_SIA_SIGN_OUT);
        }
        return ret;
    }

    public void goSia() {
        usuarioVO = null;
        this.setUser(this.getUserTxt());
        this.setPass(this.getPassTxt());
        redireccionar(Constantes.URL_REL_SIA_PRINCIPAL);
    }

    private void redireccionar(final String url) {
        HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String prefix = UtilSia.getUrl(origRequest);
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            fc.getExternalContext().redirect(prefix + url); // redirecciona la página
        } catch (IOException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public String principal() {
        if (isAdmin()) {
            this.setRutas();
        } else {
            this.setAlertas(this.grArchivoImpl.getAlertas());
        }
        if (isResponsable()) {
            this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
        }
        return "/vistas/gr/principal.xhtml?faces-redirect=true";
    }

    public void onTabChange(TabChangeEvent tabEvent) {
        String tabTitle = tabEvent.getTab().getTitle();
        switch (tabTitle) {
            case "Viajes Pendientes":
                setRutasViajesPenMtd();
                //PrimeFaces.current().ajax().update("frmViajesPen:tbViajes:pnlRutas");
                PrimeFaces.current().ajax().update("frmViajesPen");
                break;
            case "Viajes en Proceso":
                setRutasViajesProMtd();
                //PrimeFaces.current().ajax().update("frmViajesPen:tbViajes:pnlViaProc");
                PrimeFaces.current().ajax().update("frmViajesPro");
                break;
            case "Viajes por autorizar":
                setRutasViajesPauMtd();
                //PrimeFaces.current().ajax().update("frmViajesPen:tbViajes:pnlViaPausa");
                PrimeFaces.current().ajax().update("frmViajesAut");
                break;
            default:
                setRutasViajesIntMtd();
                //PrimeFaces.current().ajax().update("frmViajesPen:tbViajes:pnlViajesInter");
                PrimeFaces.current().ajax().update("frmViajesInt");
        }
    }

    public void llenarUsuarioVO(Usuario u) {
        //Traer puesto del usuario
        setUsuarioVO(new UsuarioVO());
        getUsuarioVO().setId(u.getId());
        getUsuarioVO().setNombre(u.getNombre());
        getUsuarioVO().setClave(u.getClave());
        getUsuarioVO().setPuesto(traerPuestoUsuario(u.getId(), u.getApCampo().getId().intValue()));
        getUsuarioVO().setMail(u.getEmail());
        getUsuarioVO().setDestinatarios(u.getDestinatarios());
        getUsuarioVO().setRfc(u.getRfc());
        getUsuarioVO().setTelefono(u.getTelefono());
        getUsuarioVO().setExtension(u.getExtension());
        getUsuarioVO().setCelular(u.getCelular());
        getUsuarioVO().setSexo(u.getSexo());

        getUsuarioVO().setIdCampo(u.getApCampo().getId());
        getUsuarioVO().setCampo(u.getApCampo().getNombre());
        getUsuarioVO().setIdGerencia(u.getGerencia().getId());
        getUsuarioVO().setActivo(u.isActivo());
    }

    public String traerPuestoUsuario(String idUsuario, int idCampo) {
        return apCampoUsuarioRhPuestoImpl.getPuestoPorUsurioCampo(idUsuario, idCampo);
    }

    public String encriptar(String text) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] b = md.digest(text.getBytes());
            int size = b.length;
            StringBuilder h = new StringBuilder(size);

            for (int i = 0; i < size; i++) {
                int u = b[i] & 255;
                if (u < 16) {
                    h.append(Integer.toHexString(u));
                } else {
                    h.append(Integer.toHexString(u));
                }
            }
            //clave encriptada
            return h.toString();
        } catch (Exception e) {
            return text;
        }
    }

    public void cerrarSecion() {
        UtilLog4j.log.info(this, "CERRO SESION SECURITY");
        setUsuarioVO(null);
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * @return the usuarioVO
     */
    public UsuarioVO getUsuarioVO() {
        return usuarioVO;
    }

    /**
     * @param usuarioVO the usuarioVO to set
     */
    public void setUsuarioVO(UsuarioVO usuarioVO) {
        this.usuarioVO = usuarioVO;
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
     * @return the userTxt
     */
    public String getUserTxt() {
        return userTxt;
    }

    /**
     * @return the passTxt
     */
    public String getPassTxt() {
        return passTxt;
    }

    public List<GrArchivoVO> getAlertas() {
        return alertas;
    }

    public List<GrArchivoVO> setAlertas(List<GrArchivoVO> alerts) {
        return alertas = alerts;
    }

    /**
     * @return the admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
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
    }

    /**
     * @return the desdeSia
     */
    public boolean isDesdeSia() {
        return desdeSia;
    }

    /**
     * @param desdeSia the desdeSia to set
     */
    public void setDesdeSia(boolean desdeSia) {
        this.desdeSia = desdeSia;
    }

    /**
     * @return the rutas
     */
    public List<RutaTerrestreVo> getRutasProceso() {
        return rutasProceso;
    }

    /**
     * @param rutas the rutas to set
     */
    public void setRutasProceso(List<RutaTerrestreVo> rutas) {
        this.rutasProceso = rutas;
    }

    public void setRutas() {
        setRutasProceso(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.ESTATUS_VIAJE_PROCESO, null));
        setRutasPendienes(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.ESTATUS_VIAJE_POR_SALIR, null, getNumDias()));
//        setRutasPausa(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, null));
//        setIntercepcionesProgramadas(grInterseccionImpl.traerViajesPorInterceptar(0));
        if (isResponsable()) {
            this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
        }
    }

    public void setRutasViajesPen(ActionEvent actionEvent) {
        setRutasViajesPenMtd();
    }

    private void setRutasViajesPenMtd() {
        setRutasPendienes(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.ESTATUS_VIAJE_POR_SALIR, null, getNumDias()));
        if (isResponsable()) {
            this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
        }
    }

    public void setRutasViajesPro(ActionEvent actionEvent) {
        setRutasViajesProMtd();
    }

    private void setRutasViajesProMtd() {
        setRutasProceso(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.ESTATUS_VIAJE_PROCESO, null));
        if (isResponsable()) {
            this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
        }
    }

    public void setRutasViajesPau(ActionEvent actionEvent) {
        setRutasViajesPauMtd();
    }

    private void setRutasViajesPauMtd() {
        setRutasPausa(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, null));
        if (isResponsable()) {
            this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
        }
    }

    public void setRutasViajesInt(ActionEvent actionEvent) {
        setRutasViajesIntMtd();
    }

    private void setRutasViajesIntMtd() {
        setIntercepcionesProgramadas(grInterseccionImpl.traerViajesPorInterceptarSimp(0));
        if (isResponsable()) {
            this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
        }
    }

    public void actRutasPendientesCambio() {
        try {
            setRutasPendienes(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.ESTATUS_VIAJE_POR_SALIR, null, numDias));
            if (isResponsable()) {
                this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    /**
     * @return the rutas
     */
    public List<RutaTerrestreVo> getRutasPendienes() {
        return rutasPendienes;
    }

    /**
     * @param rutas the rutas to set
     */
    public void setRutasPendienes(List<RutaTerrestreVo> rutas) {
        this.rutasPendienes = rutas;
    }

    public void finalizarViaje(int idViaje, int idInter) {
        try {
            if (idViaje > 0) {
                SgViaje vi = sgViajeImpl.find(idViaje);
                if (vi != null) {
                    boolean tieneRegreso = sgViajeImpl.tieneRegreso(idViaje);
                    if (vi.isRedondo() && !tieneRegreso) {
                        sgViajeImpl.cambiarEstado(idViaje, getUsuario().getId(), Constantes.ESTATUS_VIAJE_EN_DESTINO);
                        sgViajeImpl.mensajeLlegada(idViaje, getUsuario().getId(), Constantes.DESTINO, "");

                    } else if (vi.isRedondo() && tieneRegreso) {
                        sgViajeImpl.finalizarViaje(idViaje, getUsuario().getId());
                        boolean enOrigen = grInterseccionImpl.viajeEnInterceptar(idViaje);
                        sgViajeImpl.mensajeLlegada(idViaje, getUsuario().getId(), enOrigen ? Constantes.ORIGEN : Constantes.DESTINO, "");

                    } else if (!vi.isRedondo()) {
                        sgViajeImpl.finalizarViaje(idViaje, getUsuario().getId());
                        boolean enOrigen = grInterseccionImpl.viajeEnInterceptar(idViaje);
                        sgViajeImpl.mensajeLlegada(idViaje, getUsuario().getId(), enOrigen ? Constantes.ORIGEN : Constantes.DESTINO, "");
                        sgViajeroImpl.dejaUsuarioOficinaDestinoViajeSencillo(idViaje, getUsuarioVO().getId());

                    }
                }

                if (idInter > 0) {
                    setRutasViajesIntMtd();
                } else {
                    setRutasViajesProMtd();
                }
                String metodo = ";cerrarDialogoGRViajeDet();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void finalizarViajeSencillo(ActionEvent actionEvent) {
        try {
            int idViaje = Integer.parseInt(FacesUtilsBean.getRequestParameter("idViaje"));
            if (idViaje > 0) {
                SgViaje vi = sgViajeImpl.find(idViaje);
                if (vi != null) {
                    sgViajeImpl.finalizarViaje(idViaje, getUsuario().getId());
                    sgViajeImpl.mensajeLlegada(idViaje, getUsuario().getId(), Constantes.ORIGEN, "");
                    //  SgViaje viRegreso =sgViajeImpl.findSgViajeVuelta(idViaje);
//                    if(vi.getConIntercepcion() == null || vi.getConIntercepcion().equals(Constantes.BOOLEAN_FALSE)){//se puede quitar el problema se daria al interceptar un viaje que no se haya marcado, al interceptar el viaje se realizara el cambio a sencillo y se marcara en caso de no estar marcado 
//                        sgViajeroImpl.dejaUsuarioOficinaDestinoViajeSencillo(idViaje, getUsuarioVO().getId()); //se agrega el viajero en caso de que ninguno de los viajes venga marcados, y si se hayan interceptado 
//                        //se debe de cambiar el viaje a sencillo y marcar con intercepcion
//                   }

                    setRutas();
                }
            }
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void iniciarViaje(int idViaje, int idInter) {
        try {
            List<ViajeroVO> viajeros = new ArrayList<>();
            List<ViajeroVO> viajerosRegreso = new ArrayList<>();
            ViajeroVO newViajero;
            if (idViaje > 0) {
                SgViaje viaje = sgViajeImpl.find(idViaje);
                if (viaje.isConIntercepcion()) {
                    viaje.setRedondo(Constantes.BOOLEAN_FALSE);
                    viajeros = sgViajeroImpl.getTravellersByTravel(idViaje, null);
                    int idViajero = 0;
                    for (ViajeroVO vo : viajeros) {
                        if (vo.isRedondo()) {
                            newViajero = new ViajeroVO();
                            newViajero.setIdInvitado(vo.getIdInvitado());
                            newViajero.setInvitado(vo.getInvitado());
                            newViajero.setUsuario(vo.getUsuario());
                            newViajero.setIdUsuario(vo.getIdUsuario());
                            newViajero.setSgSolicitudEstancia(vo.getSgSolicitudEstancia());
                            newViajero.setIdSolicitudViaje(vo.getIdSolicitudViaje());
                            newViajero.setEstancia(vo.isEstancia());
                            newViajero.setRedondo(Constantes.BOOLEAN_FALSE);
                            viajerosRegreso.add(newViajero);
                        }
                    }
                    sgViajeroImpl.dejaUsuarioOficinaDestinoViajeSencillo(viajerosRegreso, getUsuario().getId());

                }

                System.out.println("u: " + getUsuario().getId());
                sgViajeImpl.exitTrip(getUsuario(), viaje, Constantes.ESTATUS_VIAJE_PROCESO, null, true);
//                cargarViajeD(this.getViajeD().getId());
                if (idInter > 0) {
                    setRutasViajesIntMtd();
                } else {
                    setRutasViajesPenMtd();
                }
                String metodo = ";cerrarDialogoGRViajeDet();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (NumberFormatException | SIAException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void cancelarViaje(int idViaje) {
        try {
            SgViaje viaje = sgViajeImpl.find(idViaje);
            StringBuilder motivoCancelacion = new StringBuilder();
            motivoCancelacion.append("El viaje <b>");
            motivoCancelacion.append(viaje.getCodigo());
            motivoCancelacion.append("</b> ha sido <b> CANCELADO</b>, debido a que cambio el estado del semáforo y no se autorizo.");
            SiMovimiento siMovimiento = siMovimientoImpl.guardarSiMovimiento(motivoCancelacion.toString(), siOperacionImpl.find(Constantes.ID_SI_OPERACION_CANCELAR), getUsuario());
            sgViajeImpl.cancelTrip(getUsuario(), viaje, motivoCancelacion.toString(), false, siMovimiento, true);
            setRutasViajesPenMtd();
            String metodo = ";cerrarDialogoGRViajeDet();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void cancelarViajePendiente(int idViaje) {
        try {
            SgViaje viaje = sgViajeImpl.find(idViaje);
            StringBuilder motivoCancelacion = new StringBuilder();
            motivoCancelacion.append("El viaje <b>");
            motivoCancelacion.append(viaje.getCodigo());
            motivoCancelacion.append("</b> ha sido <b> CANCELADO</b>, debido a que no se realizó en tiempo.");
            SiMovimiento siMovimiento = siMovimientoImpl.guardarSiMovimiento(motivoCancelacion.toString(), siOperacionImpl.find(Constantes.ID_SI_OPERACION_CANCELAR), getUsuario());
            sgViajeImpl.cancelTrip(getUsuario(), viaje, motivoCancelacion.toString(), false, siMovimiento, true);
            setRutasViajesPenMtd();
            String metodo = ";cerrarDialogoGRViajeDet();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void bajarViajero(int idViajero) {
        try {
            if (idViajero > 0) {
                ViajeroVO vo = sgViajeroImpl.buscarViajeroPorId(idViajero);
                sgViajeroSiMovimientoImpl.guardaMovimiento(usuario.getId(), idViajero,
                        new StringBuilder().append("El viajero, ")
                                .append(vo.getTipoViajero() == Constantes.SG_TIPO_ESPECIFICO_EMPLEADO ? vo.getUsuario() : vo.getInvitado())
                                .append(", no viajó en el viaje ").append(vo.getCodigoViaje()).toString(),
                        Constantes.ID_SI_OPERACION_VIAJERO_NO_VIAJO);
                setRutasViajesPauMtd();
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void autorizarViaje(int idViaje) {
        try {
            sgViajeImpl.updateViajeDireccion(idViaje, getUsuario());
            //sgViajeImpl.cambiarEstado(idViaje, getUsuario().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR);
            //sgViajeImpl.mensajeLlegada(idViaje, getUsuario().getId(), Constantes.DESTINO, "");
            setRutasViajesPauMtd();
            setRutasViajesPenMtd();
            String metodo = ";cerrarDialogoGRViajeDet();";
            PrimeFaces.current().executeScript(metodo);
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void agregarComentario() {
        try {
            if (getViajeD() != null && getViajeD().getIdNoticia() > 0) {
                coNoticiaImpl.nuevoComentario(getViajeD().getIdNoticia(), getUsuario().getId(), getTxtComentario(), true, true, Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_GR);
                setTxtComentario("");
                refrescarNoticia();
            }
            String metodo = ";cerrarDialogoAddComentario();abrirDialogoGRViajeDet();";
            PrimeFaces.current().executeScript(metodo);
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    private void refrescarNoticia() {
        if (getViajeD() != null && getViajeD().getIdNoticia() > 0) {
            NoticiaVO noticia = new NoticiaVO();
            noticia.setListaComentario(coComentarioImpl.traerComentariosPorNoticia(getViajeD().getIdNoticia()));
            getViajeD().setNoticia(noticia);
        }
    }

    public void guardarGRAutoriza() {
        try {
            if (getIdViajeroGRAut() > 0) {
                sgViajeroImpl.cambiarGRAutorizacion(getUsuario().getId(), getIdViajeroGRAut(), getGrAutTxtMotivo());
                cargarViajeD(this.getViajeD().getId());
            }
            String metodo = ";cerrarDialogoGRAutoriza();abrirDialogoGRViajeDet();";
            PrimeFaces.current().executeScript(metodo);
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void interceptarViaje() {
        try {
            if (this.getIntercepcion() == null) {
                if (getIdViajeIntA() > 0 && getIdViajeIntB() > 0) {
                    grInterseccionImpl.crearIntercepcionViajes(getIdViajeIntA(), getIdViajeIntB(), 0, getUsuario().getId());
                    setRutasViajesIntMtd();
//                    setRutas();
                }
            } else if (this.getIntercepcion() != null && this.getIdViajeIntA() > 0 && this.getIdViajeIntB() > 0) {
                grInterseccionImpl.crearIntercepcionViajes(getIdViajeIntA(), getIdViajeIntB(), getUsuario().getId(), this.getIntercepcion());
                setRutasViajesIntMtd();
//                setRutas();
            }

            if (getViajeD().getIdEstatus() == 501) {
                setRutasViajesPenMtd();
            } else if (getViajeD().getIdEstatus() == 510) {
                setRutasViajesProMtd();
            }

            String metodo = ";cerrarDialogoInterceptarViaje();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    /**
     * @return the txtComentario
     */
    public String getTxtComentario() {
        return txtComentario;
    }

    /**
     * @param txtComentario the txtComentario to set
     */
    public void setTxtComentario(String txtComentario) {
        this.txtComentario = txtComentario;
    }

    /**
     * @return the rutasPausa
     */
    public List<RutaTerrestreVo> getRutasPausa() {
        return rutasPausa;
    }

    /**
     * @param rutasPausa the rutasPausa to set
     */
    public void setRutasPausa(List<RutaTerrestreVo> rutasPausa) {
        this.rutasPausa = rutasPausa;
    }

    public void goAddComentario(int idNot) {
        try {
//            int idNoticiaAux = Integer.parseInt(FacesUtilsBean.getRequestParameter("idNoticia"));
            // setIdViajeIntA(idNot);
            String metodo = ";abrirDialogoAddComentario();cerrarDialogoGRViajeDet();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goGRViajeDet(int idViaje, int idInter) {
        try {

            if (idViaje > 0) {
                cargarViajeD(idViaje);
                if (idInter > 0 && getViajeD() != null) {
                    getViajeD().setIdIntercambio(idInter);
                    int idViajeB = grInterseccionImpl.viajeEnInterceptarRest(idInter, idViaje);
                    setViajeB(sgViajeImpl.buscarPorId(idViajeB, false, false, false));
                }
                String metodo = ";abrirDialogoGRViajeDet();";
                PrimeFaces.current().executeScript(metodo);

            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    private void cargarViajeD(int idViajeD) {
        try {
            setViajeD(sgViajeImpl.buscarPorId(idViajeD, true, true, false));
//            if (getViajeD() != null) {                
//                getViajeD().setListaViajeros(sgViajeroImpl.getTravellersByTravel(idViajeD, null));
//            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goGRAutoriza(int idViajero) {
        try {
            setIdViajeroGRAut(idViajero);
            ViajeroVO vo = sgViajeroImpl.buscarViajeroPorId(idViajero);
            setGrAutTxtMotivo(vo.getGrAutorizoMotivo());
            String metodo = ";cerrarDialogoGRViajeDet();abrirDialogoGRAutoriza();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goInterceptarViaje(int idViaje, int idViajeInter) {
        try {
            setIntercepcion(null);
            setIdViajeIntA(idViaje);
            setLstViajesInt(new ArrayList<ViajeVO>());
            getLstViajesInt().addAll(sgViajeImpl.traerViajesInterceptantes(Constantes.ESTATUS_VIAJE_POR_SALIR, getIdViajeIntA(), null, null));
            getLstViajesInt().addAll(sgViajeImpl.traerViajesInterceptantes(Constantes.ESTATUS_VIAJE_PROCESO, getIdViajeIntA(), null, null));
            String metodo = ";cerrarDialogoGRViajeDet();abrirDialogoInterceptarViaje();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goInterceptarViajeInter(ActionEvent actionEvent) {
        try {
            int idViaje = Integer.parseInt(FacesUtilsBean.getRequestParameter("idViaje"));
            int idInter = Integer.parseInt(FacesUtilsBean.getRequestParameter("idInter"));
            if (idViaje > 0 && idInter > 0) {
                List<GrIntercepcionVO> inters = grInterseccionImpl.traerViajesPorInterceptar(idInter);
                if (inters != null && inters.size() > 0) {
                    setIdViajeIntA(idViaje);
                    setIntercepcion(inters.get(0));
                    setLstViajesInt(new ArrayList<ViajeVO>());
                    getLstViajesInt().addAll(sgViajeImpl.traerViajesInterceptantes(Constantes.ESTATUS_VIAJE_POR_SALIR, getIdViajeIntA(), null, null));
                    getLstViajesInt().addAll(sgViajeImpl.traerViajesInterceptantes(Constantes.ESTATUS_VIAJE_PROCESO, getIdViajeIntA(), null, null));
                    String metodo = ";abrirDialogoInterceptarViaje();";
                    PrimeFaces.current().executeScript(metodo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void abrirInterceptarViaje(int idViaje, int idInter) {
        try {
            if (idViaje > 0 && idInter > 0) {
                List<GrIntercepcionVO> inters = grInterseccionImpl.traerViajesPorInterceptar(idInter);
                if (inters != null && inters.size() > 0) {
                    setIntercepcion(inters.get(0));
                    getIntercepcion().setIntercambiarVehiculo(false);
                    getIntercepcion().setIntercambiarViajeros(true);
                    String metodo = ";cerrarDialogoGRViajeDet();abrirDialogoDetalleInt();";
                    PrimeFaces.current().executeScript(metodo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void llegoPSeguridad(int idViaje, int idInter) {
        try {
            if (idViaje > 0 && idInter > 0) {
                List<GrIntercepcionVO> inters = grInterseccionImpl.traerViajesPorInterceptar(idInter);
                if (inters != null && inters.size() > 0) {
                    setIntercepcion(inters.get(0));
                    setIdViajeLlegadaPS(idViaje);
                    setPuntosItems(grPuntoImpl.getPuntosItems(getIdViajeLlegadaPS()));
                    String metodo = ";cerrarDialogoGRViajeDet();abrirDialogoLlegadaPS();";
                    PrimeFaces.current().executeScript(metodo);
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarLlegadaPS() {
        try {
            boolean error = false;
            if (getIdPSLlegada() > 0 && getIdViajeLlegadaPS() > 0) {
                if ((this.intercepcion.getPuntoSeguridadID() == 0)
                        || (this.intercepcion.getPuntoSeguridadID() > 0 && this.intercepcion.getPuntoSeguridadID() == this.getIdPSLlegada())) {
                    GrPuntoVO pvo = grPuntoImpl.getPunto(getIdPSLlegada());
                    sgViajeSiMovimientoImpl.guardarViajeMovimiento(getUsuarioVO().getId(), getIdViajeLlegadaPS(), Constantes.ID_SI_OP_LLEGO_VIAJE, "Viaje en punto de seguridad: " + pvo.getNombre());
                    sgViajeImpl.mensajeLlegada(getIdViajeLlegadaPS(), getUsuarioVO().getId(), Constantes.PUNTO_SEGURIDAD, pvo.getNombre());
                    grInterseccionImpl.guardarPS(this.intercepcion.getId(), pvo.getId(), getUsuarioVO().getId());
                    setRutasViajesIntMtd();
                    setIntercepcion(null);
                    setIdViajeLlegadaPS(0);
                } else {
                    FacesUtilsBean.addErrorMessage("No coincide el punto de seguridad donde los viajes se encontraran. ");
                    error = true;
                }
            }
            if (!error) {
                String metodo = ";cerrarDialogoLlegadaPS();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void guardarDetalleInt() {
        boolean actualiza = false;
        try {
            if (getIntercepcion() != null && getIntercepcion().getId() > 0) {
                actualiza = sgViajeImpl.pasarViajeros(this.getIntercepcion(), getUsuario().getId());

                if (getIntercepcion().isIntercambiarVehiculo()) {
                    int vehiculoA = this.getIntercepcion().getViajeA().getVehiculoVO().getId();
                    int vehiculoB = this.getIntercepcion().getViajeB().getVehiculoVO().getId();
                    sgViajeVehiculoImpl.actualizarVehiculo(getUsuario().getId(),
                            this.getIntercepcion().getViajeA().getIdViajeVehiculo(),
                            vehiculoB);

                    sgViajeVehiculoImpl.actualizarVehiculo(getUsuario().getId(),
                            this.getIntercepcion().getViajeB().getIdViajeVehiculo(),
                            vehiculoA);
                    actualiza = true;
                }
            }
            if (actualiza) {
                setRutasViajesIntMtd();
            }
            setIntercepcion(null);
            String metodo = ";cerrarDialogoDetalleInt();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void retomarViaje(int idViaje, int idInter) {
        try {
            if (idViaje > 0 && idInter > 0) {
                List<GrIntercepcionVO> inters = grInterseccionImpl.traerViajesPorInterceptar(idInter);
                if (inters != null && inters.size() > 0) {
                    setIntercepcion(inters.get(0));
                    sgViajeImpl.guardarMovimiento(getUsuarioVO().getId(), idViaje, Constantes.ID_SI_OPERACION_RETOMAR_VIAJE, "Se retomo el viaje desde punto de seguridad");
                    sgViajeImpl.mensajeSalidaPunto(idViaje, getUsuarioVO().getId(), Constantes.UNO, this.getIntercepcion().getPuntoSeguridadNombre());
                    setRutasViajesIntMtd();
                    String metodo = ";cerrarDialogoGRViajeDet();";
                    PrimeFaces.current().executeScript(metodo);
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    /**
     * @return the grVia
     */
    public boolean isGrVia() {
        return grVia;
    }

    /**
     * @param grVia the grVia to set
     */
    public void setGrVia(boolean grVia) {
        this.grVia = grVia;
    }

    /**
     * @return the responsable
     */
    public boolean isResponsable() {
        return responsable;
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(boolean responsable) {
        this.responsable = responsable;
    }

    /**
     * @return the rutasPausaGerente
     */
    public List<RutaTerrestreVo> getRutasPausaGerente() {
        return rutasPausaGerente;
    }

    /**
     * @param rutasPausaGerente the rutasPausaGerente to set
     */
    public void setRutasPausaGerente(List<RutaTerrestreVo> rutasPausaGerente) {
        this.rutasPausaGerente = rutasPausaGerente;
    }

    public void actualizarRutasPausaGerente() {
        if (isResponsable()) {
            this.setRutasPausaGerente(sgRutaTerrestreImpl.traerRutaTerrestreViaje(Constantes.VIAJE_ESPERA_AUTORIZACION, getUsuarioVO().getId()));
        }
    }

    /**
     * @return the numDias
     */
    public int getNumDias() {
        return numDias;
    }

    /**
     * @param numDias the numDias to set
     */
    public void setNumDias(int numDias) {
        this.numDias = numDias;
    }

    /**
     * @return the idViajeIntA
     */
    public int getIdViajeIntA() {
        return idViajeIntA;
    }

    /**
     * @param idViajeInt the idViajeInt to set
     */
    public void setIdViajeIntA(int idViajeInt) {
        this.idViajeIntA = idViajeInt;
    }

    /**
     * @return the idViajeInt
     */
    public int getIdViajeIntB() {
        return idViajeIntB;
    }

    /**
     * @param idViajeInt the idViajeInt to set
     */
    public void setIdViajeIntB(int idViajeInt) {
        this.idViajeIntB = idViajeInt;
    }

    /**
     * @return the lstViajesInt
     */
    public List<ViajeVO> getLstViajesInt() {
        return lstViajesInt;
    }

    /**
     * @param lstViajesInt the lstViajesInt to set
     */
    public void setLstViajesInt(List<ViajeVO> lstViajesInt) {
        this.lstViajesInt = lstViajesInt;
    }

    /**
     * @return the intercepcionesProgramadas
     */
    public List<GrIntercepcionVO> getIntercepcionesProgramadas() {
        return intercepcionesProgramadas;
    }

    /**
     * @param intercepcionesProgramadas the intercepcionesProgramadas to set
     */
    public void setIntercepcionesProgramadas(List<GrIntercepcionVO> intercepcionesProgramadas) {
        this.intercepcionesProgramadas = intercepcionesProgramadas;
    }

    /**
     * @return the idViajeLlegadaPS
     */
    public int getIdViajeLlegadaPS() {
        return idViajeLlegadaPS;
    }

    /**
     * @param idViajeLlegadaPS the idViajeLlegadaPS to set
     */
    public void setIdViajeLlegadaPS(int idViajeLlegadaPS) {
        this.idViajeLlegadaPS = idViajeLlegadaPS;
    }

    /**
     * @return the idPSLlegada
     */
    public int getIdPSLlegada() {
        return idPSLlegada;
    }

    /**
     * @param idPSLlegada the idPSLlegada to set
     */
    public void setIdPSLlegada(int idPSLlegada) {
        this.idPSLlegada = idPSLlegada;
    }

    /**
     * @return the intercepcion
     */
    public GrIntercepcionVO getIntercepcion() {
        return intercepcion;
    }

    /**
     * @param intercepcion the intercepcion to set
     */
    public void setIntercepcion(GrIntercepcionVO intercepcion) {
        this.intercepcion = intercepcion;
    }

    /**
     * @return the puntosItems
     */
    public List<SelectItem> getPuntosItems() {
        return puntosItems;
    }

    /**
     * @param puntosItems the puntosItems to set
     */
    public void setPuntosItems(List<SelectItem> puntosItems) {
        this.puntosItems = puntosItems;
    }

    /**
     * @return the idViajeroGRAut
     */
    public int getIdViajeroGRAut() {
        return idViajeroGRAut;
    }

    /**
     * @param idViajeroGRAut the idViajeroGRAut to set
     */
    public void setIdViajeroGRAut(int idViajeroGRAut) {
        this.idViajeroGRAut = idViajeroGRAut;
    }

    /**
     * @return the grAutTxtMotivo
     */
    public String getGrAutTxtMotivo() {
        return grAutTxtMotivo;
    }

    /**
     * @param grAutTxtMotivo the grAutTxtMotivo to set
     */
    public void setGrAutTxtMotivo(String grAutTxtMotivo) {
        this.grAutTxtMotivo = grAutTxtMotivo;
    }

    /**
     * @return the viajeD
     */
    public ViajeVO getViajeD() {
        return viajeD;
    }

    /**
     * @param viajeD the viajeD to set
     */
    public void setViajeD(ViajeVO viajeD) {
        this.viajeD = viajeD;
    }

    /**
     * @return the viajeB
     */
    public ViajeVO getViajeB() {
        return viajeB;
    }

    /**
     * @param viajeB the viajeB to set
     */
    public void setViajeB(ViajeVO viajeB) {
        this.viajeB = viajeB;
    }
    
    public void subirValoresContexto(HttpSession session) {
        setCtx(new Properties());
        Env.setContext(getCtx(), Env.SESSION_ID, session.getId());
        //Env.setContext(ctx, Env.CLIENT_INFO, session. SessionUtils.getClientInfo(SessionUtils.getRequest()));
        Env.setContext(getCtx(), Env.PUNTO_ENTRADA, "GR");
        Env.setContext(getCtx(), Env.PROYECTO_ID, getUsuario().getApCampo().getId());
        Env.setContext(getCtx(), Env.CODIGO_COMPANIA, getUsuario().getApCampo().getCompania().getRfc());
    }

    /**
     * @return the ctx
     */
    public Properties getCtx() {
        return ctx;
    }

    /**
     * @param ctx the ctx to set
     */
    public void setCtx(Properties ctx) {
        this.ctx = ctx;
    }

    /**
     * @return the mapa
     */
    public GrArchivoVO getMapa() {
        return mapa;
    }

    /**
     * @param mapa the mapa to set
     */
    public void setMapa(GrArchivoVO mapa) {
        this.mapa = mapa;
    }

}
