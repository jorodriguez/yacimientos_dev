/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.estancia.bean.model;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Gerencia;
import sia.modelo.SgHotel;
import sia.modelo.SgHotelHabitacion;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgHuespedStaff;
import sia.modelo.SgInvitado;
import sia.modelo.SgMotivo;
import sia.modelo.SgOficina;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.SgHuespedStaffVo;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.hotel.vo.SgHotelTipoEspecificoVo;
import sia.modelo.sgl.hotel.vo.SgHuespedHotelServicioVo;
import sia.modelo.sgl.vo.DetalleSolicitudVO;
import sia.modelo.sgl.vo.SgHuespedHotelVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgHotelHabitacionImpl;
import sia.servicios.sgl.impl.SgHotelImpl;
import sia.servicios.sgl.impl.SgHotelTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgHuespedHotelImpl;
import sia.servicios.sgl.impl.SgHuespedHotelServicioImpl;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sgl.impl.SgMotivoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgStaffHabitacionImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "estanciaBean")
@ViewScoped
public class EstanciaBeanModel implements Serializable {

    //Sistema //Sistema
    @Inject
    private Sesion sesion;
    //Servicios
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaImpl;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgTipoImpl sgTipoImpl;
    @Inject
    private SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    private SgHotelImpl sgHotelImpl;
    @Inject
    private SgHotelHabitacionImpl sgHotelHabitacionImpl;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelImpl;
    @Inject
    private SgHuespedStaffImpl huespedStaffService;
    @Inject
    private SgStaffHabitacionImpl habitacionStaffService;
    @Inject
    private SgStaffImpl staffService;
    @Inject
    private SgHotelImpl hotelService;
    @Inject
    private SgSolicitudEstanciaImpl solicitudEstanciaService;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private SgMotivoImpl sgMotivoImpl;
    @Inject
    private SiUsuarioTipoImpl siUsuarioCopiadoImpl;
    @Inject
    private SgHotelTipoEspecificoImpl sgHotelTipoEspecificoImpl;
    @Inject
    private SgHuespedHotelServicioImpl sgHuespedHotelServicioImpl;

    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    ApCampoGerenciaImpl apCampoGerenciaImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    //Entidades
    private DetalleEstanciaVO sgDetalleSolicitudEstancia;
    private SgTipo sgTipo;
    private SgTipoEspecifico tipoEspecifico;
    private Usuario usuario;
    private Gerencia gerencia;
    private Gerencia gerenciaCapacitacion;
    private SgHuespedHotel sgHuespedHotel;
    private SgHuespedHotel sgHuespedHotelSeleccionado;
    private SgHotel sgHotel;
    private SgStaffHabitacion habitacion;
    private SgHuespedStaff huespedStaff;
    private SgMotivo sgMotivo;
    private SgInvitado sgInvitado;
    private SgOficina sgOficina;
    //SelectItem
    private List<SelectItem> listaSelectItem;
    private List<SelectItem> listaSelectItemInvitado;
    private List<SelectItem> listaUsuariosAlta;
    private List<SelectItem> staffListSelectItem;
    @Getter
    @Setter
    private List<SelectItem> listaHoteles;
    @Getter
    @Setter
    private List<SelectItem> listaHabitacionHotel;

    //private List<SelectItem> gerenciaSelectItem;
    //private List<SelectItem> sgOficinaSelectItem;
    //private List<SelectItem> sgMotivoSelectItem;
    //private List<DetalleSolicitudVO> lu;
    //Datamodels
    private DataModel dataModel; //DataModel genérico. Úsalo si lo necesitas!  // se usas para mostrar en salida huesped hotel a los que dan VoBo a la carta de huespedes
    @Getter
    @Setter
    private List<SgSolicitudEstanciaVo> listaSolicitud;
    private List<DetalleEstanciaVO> listaDetalleSolicitud;
    private List<SgStaff> staffDataModel;
    private List<SgStaffHabitacion> habitacionesStaffDataModel;
    private List<Integer> numHabitacionesDisponiblesByStaffDataModel;
    private List<SgHuespedHotelServicioVo> lista;
    private List<SgHotelTipoEspecificoVo> serviciosHotelFacturaEmpresa;
    private DataModel listaEstancia;
    @Getter
    private DataModel listaHospedadosHotel;

    //Clases
    private DetalleSolicitudVO detalleSolicitudVO;
    private SgSolicitudEstanciaVo sgSolicitudEstanciaVo;
    //Primitivos
    private int status;
    private int idOficina;
    private int idOficinaExcluida;
    private int idStaff;
    private int idTipoEspecifico;
    private int id = 0; //Ocupado por los invitados en solicitudes de estancia b)Usuario tipo que da visto bueno a carta
    private int idGerencia;
    private int idMotivo;
    private int idHabitacion;
    private int idHotel;
    private int idIntegrante;
    private int numeroHabitacion;
    private String user;
    private String descripcion; //# Reservación Huésped Hotel,
    private String mensaje = "";
    private String opcionSeleccionada;
    private String sugerenciaFechaSalidaHuesped;
    private String invitado;
    //
    private Date fechaIngresoHuesped;
    private Date fechaSalidaHuesped;
    private Date fechaRealIngresoHuesped;
    private Date fechaRealSalidaHuesped;
    private Date fechaSalidaPropuesta; //utilizada tambien para prolongar estancias
    //Booleanos
    private boolean flag = false;
    private boolean popUp = false;
    private boolean crearPop = false;
    private boolean modificarPop = false;
    private boolean subirArchivoPop = false; //Usado: a)Validar si se abrió popup de huéspedes en hotel con mismo número de reservación
    private boolean eliminarPop = false;
    private boolean solicitaPop = false;
    private boolean generaCartaPop = false;
    private boolean mrPopupDetalleSolicitudEstancia = false;
    private boolean mrPopupRegistrarHuespedEnStaff = false;
    private boolean mrPopupRegistrarHuespedEnHotel = false;
    private boolean mrPopupConfirmacionAsignacion = false;
    private boolean disabled;
    private boolean disabledAux;
    private boolean conCorreo = true;
    private int idInVitado;
    @Getter
    @Setter
    private List<SelectItem> listaTiposHuespedes;

    /**
     * Creates a new instance of EstanciaBeanModel
     */
    public EstanciaBeanModel() {
    }

    public String convertirFechaString(Date fecha) {
        return siManejoFechaImpl.convertirFechaStringddMMyyyy(fecha);
    }

    public Date convertirStringFecha(String fecha) {
        return siManejoFechaImpl.convertirStringFechaddMMyyyy(fecha);
    }

    //Rol capacitacion
    public int regresaRol() {
        return sesion.getIdRol();
    }

    public void controlaPopUpFalso(String llave) {
        sesion.getControladorPopups().put(llave, Boolean.FALSE);
    }

    public void controlaPopUpTrue(String llave) {
        sesion.getControladorPopups().put(llave, Boolean.TRUE);
    }

    @PostConstruct
    public void iniciarSolicitudEstancia() {
        //Limpiando Variables
        this.status = 1;
        this.sgSolicitudEstanciaVo = null;
        this.detalleSolicitudVO = null;
        this.listaSolicitud = new ArrayList<>();
        this.listaDetalleSolicitud = null;
        this.popUp = false;
        this.staffListSelectItem = new ArrayList();
        serviciosHotelFacturaEmpresa = new ArrayList();
        listaTiposHuespedes = new ArrayList();
        listaHabitacionHotel = new ArrayList();
        listaHoteles = new ArrayList();
        if (siUsuarioRolImpl.buscarRolPorUsuarioModulo(sesion.getUsuario().getId(), Constantes.MODULO_SGYL, "7", Constantes.AP_CAMPO_DEFAULT)) {
            setGerenciaCapacitacion(this.gerenciaImpl.findByNameAndCompania("Capacitación", "IHI070320FI3", false));
            setIdGerencia(getGerenciaCapacitacion().getId());
        }
        setListaEstancia(new ListDataModel(findAllSgSolicitudEstanciaByUsuarioAndEstatus(Constantes.ESTATUS_SOLICITUD_ESTANCIA_PENDIENTE, false)));
        setIdGerencia(sesion.getUsuario().getGerencia().getId());
        List<UsuarioVO> luser;
        if (sesion.getUsuario().getGerencia() == null) {
            setIdGerencia(Constantes.GERENCIA_ID_SERVICIOS_GENERALES);
            FacesUtils.addErrorMessage("No tiene gerencia registrada, su solicitud se enviará a Servicios Generales");
        }
        trearSolicitudEstanciaParaRegistro();
        this.listaHospedadosHotel = traerRegistroHospedadosHotel();

        llenarTiposHuespedes();
        //
        llenarStaffWithAvailableRoomsByOficinaList();
    }

    public void mostrarPopupDetalleSolicitudEstancia(int idSolEst) {
        sgSolicitudEstanciaVo = solicitudEstanciaService.buscarEstanciaPorId(idSolEst);
        try {
            traerDetalleSolicitud();
            PrimeFaces.current().executeScript("PF('dlgSolEst').show();");
        } catch (Exception e) {
            log(e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void cancelarSolicitudEstancia(SgSolicitudEstanciaVo event) {
        setSgSolicitudEstanciaVo(event);
        //Valida si no se han agregado usuarioa staff y hotel
        DataModel<SgHuespedHotel> lh = traerRegistroHospedadosHotel();
        DataModel<SgHuespedStaff> ls = traerHospedadosStaff();
        traerDetalleSolicitud();
        if (ls.getRowCount() < 1 && lh.getRowCount() < 1) {
            setEliminarPop(true);
            PrimeFaces.current().executeScript("PF('dlgCanSol').show();");
        } else {
            FacesUtils.addErrorMessage("No es posible marcar como cancelada  la solicitud, debido a que ya se han registrado integrantes");
        }
    }

    public void completarCancelarSolicitudEstancia() {
        try {
            if (!getMensaje().isEmpty()) {
                cancelarSolicitudEstancia();
                setMensaje("");
                FacesUtils.addInfoMessage("La Solicitud de Estancia " + getSgSolicitudEstanciaVo().getCodigo() + " fue cancelada satisfactoriamente");
                setSgSolicitudEstanciaVo(null);
                setListaDetalleSolicitud(null);
                setEliminarPop(false);
                setConCorreo(Constantes.TRUE);
                PrimeFaces.current().executeScript("PF('dlgCanSol').hide();");
            } else {
                FacesUtils.addErrorMessage("Es necesario agregar un motivo de cancelación");
            }
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrio un error al cancelar la solcitud. . .");
            e.getStackTrace();
        }

    }

    public String goToAsignarHabitacion(SgSolicitudEstanciaVo estVo) {
        String returnVal = "/vistas/sgl/estancia/asignarHabitacion.xhtml";
        try {
            //Limpiando variables
            setSgSolicitudEstanciaVo(estVo);
            setListaDetalleSolicitud(null);
            getDetalleSolicitudEstanciaBySolicitudEstancia();
        } catch (Exception ex) {
            Logger.getLogger(EstanciaBeanModel.class.getName()).log(Level.SEVERE, null, ex);
            returnVal = "";
        }
        return returnVal;
    }

    /**
     * Convierte una fecha a un Calendar
     *
     * @param date
     * @param withTime - Indica si se conservan o no los valores del tiempo
     * @return
     */
    public Calendar converterDateToCalendar(Date date, boolean withTime) {
        return this.siManejoFechaImpl.converterDateToCalendar(date, withTime);
    }

    public Calendar clearCalendar(Calendar calendar) {
        return this.siManejoFechaImpl.cleanCalendar(calendar);
    }

    /**
     * Valida que 'secondDate' sea mayor que 'firstDate'. No valida los tiempos
     *
     * @param firstDate
     * @param secondDate
     * @return - 'true' si 'firstDate' es anterior a 'secondDate'
     */
    public boolean validateSecondDateIsAfterOrEqualFirstDate(Calendar secondDate, Calendar firstDate) {
        log("EstanciaBeanModel.validateSecondDateIsAfterOrEqualFirstDate(): " + (this.siManejoFechaImpl.compare(secondDate, firstDate, false) == -1 ? false : true));
        return (this.siManejoFechaImpl.compare(secondDate, firstDate, false) == -1 ? false : true);
    }

    public boolean isUsuarioGerente() {
        return this.gerenciaImpl.isUsuarioResponsableForAnyGerencia(-1, sesion.getUsuario().getId(), false);
    }

    /**
     * Abre un popup. Si no existe lo pone
     *
     * Modificadores: SKLM (25/Oct/2013)
     *
     * @author Seth Karim Luis Martínez (25/Octubre/2013)
     * @param llave
     */
    public void abrirPopup(String llave) {
        this.sesion.getControladorPopups().put(llave, Boolean.TRUE);
    }

    /**
     * Cierra un popup
     *
     * Modificadores: SKLM (25/Oct/2013)
     *
     * @author Seth Karim Luis Martínez (25/Octubre/2013)
     * @param llave
     */
    public void cerrarPopup(String llave) {
        this.sesion.getControladorPopups().put(llave, Boolean.FALSE);
    }

    /**
     * Devuelve el estado de un popup 'true' si el popup está visible y 'false'
     * si no lo está utilizando para ello el id del popup o 'llave'. Si el popup
     * o 'llave' no existe, entonces devuelve 'false'
     *
     * Modificadores: SKLM (25/Oct/2013)
     *
     * @author Seth Karim Luis Martínez (25/Octubre/2013)
     * @param llave
     * @return
     */
    public boolean obtenerEstadoPopup(String llave) {
        if (this.sesion.getControladorPopups().containsKey(llave)) {
            return this.sesion.getControladorPopups().get(llave);
        } else {
            return false;
        }
    }

    public int calculoDiasEstancia() {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            if (getIdTipoEspecifico() > 0) {
                c1.setTime(getFechaIngresoHuesped());
                c1 = this.siManejoFechaImpl.cleanCalendar(c1);

                if (isFlag()) { //Es un huésped de Periodo de Prueba y se está mostrando una fecha propuesta
                    c2.setTime(getFechaSalidaPropuesta());
                    c2 = this.siManejoFechaImpl.cleanCalendar(c2);

                    return this.siManejoFechaImpl.diferenciaDias(c1, c2);
                } else {
                    c2.setTime(getFechaSalidaHuesped());
                    c2 = this.siManejoFechaImpl.cleanCalendar(c2);

                    return this.siManejoFechaImpl.diferenciaDias(c1, c2);
                }
            } else {
                if (this.getSgSolicitudEstanciaVo() != null) {
                    c1.setTime(getSgSolicitudEstanciaVo().getInicioEstancia());
                    c1 = this.siManejoFechaImpl.cleanCalendar(c1);

                    c2.setTime(getSgSolicitudEstanciaVo().getFinEstancia());
                    c2 = this.siManejoFechaImpl.cleanCalendar(c2);

                    return this.siManejoFechaImpl.diferenciaDias(c1, c2);
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
            return 0;
        }

    }

    public SgSolicitudEstanciaVo findSgSolicitudEstanciaById(int idSgSolicitudEstancia) {
        return this.sgSolicitudEstanciaImpl.buscarEstanciaPorId(idSgSolicitudEstancia);
    }

    public Gerencia findGerenciaByNameAndCompania(String nombre, String compania) {
        return this.gerenciaImpl.findByNameAndCompania(nombre, compania, false);
    }

    //Ocupada para traer las oficinas sin la que se envia por parametro
    public List<SelectItem> traerListaOficinaEstaciaViaje() {
        List<SelectItem> ls = new ArrayList<SelectItem>();
        try {
            List<SgOficina> lo = sgOficinaImpl.getOfficeWhitoutCurrent(this.getIdOficinaExcluida());
            for (SgOficina o : lo) {
                SelectItem item = new SelectItem(o.getId(), o.getNombre() + "||" + o.getSgDireccion().getSiCiudad().getNombre());
                ls.add(item);
            }
            setListaSelectItem(ls);
            return getListaSelectItem();
        } catch (Exception e) {
            return null;
        }
    }

    public SgMotivo buscarMotivoPorNombre() {
        return sgMotivoImpl.buscarPorNombre(getSgMotivo().getNombre());
    }

    public void completarMotivo() {
        sgMotivoImpl.guardarMotivo(sesion.getUsuario(), getSgMotivo());
    }

    public List<GerenciaVo> getGerenciaByApCampoAndResponsableList() {
        //return this.gerenciaImpl.getAllGerenciaByApCampoAndResponsable(1, this.sesion.getUsuario().getId(), "nombre", true, true, false);
        return this.gerenciaImpl.getAllGerenciaByApCampoAndResponsable(1, this.sesion.getUsuario().getId(), "nombre", true, null, false);
    }

    public int totalSgSolicitudEstancia(int idSgOficina, int idEstatus, Boolean fromTravel) {
        return this.sgSolicitudEstanciaImpl.totalSgSolicitudEstancia(sesion.getUsuario().getId(), idSgOficina, idEstatus, fromTravel);
    }

    public void trearSolicitudEstancia() {
        try {
            listaSolicitud = sgSolicitudEstanciaImpl.trearSolicitudEstanciaPorOficina(sesion.getOficinaActual().getId(), getStatus(), sesion.getUsuario().getId(), Constantes.BOOLEAN_FALSE);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer las estancias por solicitud :  :  :  " + e.getMessage());
        }
    }

    public List<SgSolicitudEstanciaVo> findAllSgSolicitudEstanciaByUsuarioAndEstatus(int idEstatus, Boolean fromTravel) {
        return this.sgSolicitudEstanciaImpl.findAll(sesion.getUsuario().getId(), -1, idEstatus, fromTravel, "id", false, false);
    }

    public SgOficina findSgOficinaById(int idSgOficina) {
        return this.sgOficinaImpl.find(idSgOficina);
    }

    public List<UsuarioRolVo> listaUsuarioRol() {
        return siUsuarioRolImpl.traerRolPorNombreUsuarioModulo(getUser(), Constantes.MODULO_SGYL, Constantes.AP_CAMPO_DEFAULT);
    }

    public int buscarDetalleSolicitud() {
        int v;
        List<DetalleEstanciaVO> l = sgDetalleSolicitudEstanciaImpl.traerDetallePorSolicitud(getSgSolicitudEstanciaVo().getId(), Constantes.NO_ELIMINADO);
        return l.size();
    }

    public int comparaFecha() {
        return siManejoFechaImpl.compare(getSgSolicitudEstanciaVo().getInicioEstancia(), new Date());
    }

    public void getDetalleSolicitudEstanciaBySolicitudEstancia() throws SIAException, Exception {
        if (this.sgSolicitudEstanciaVo != null && this.listaDetalleSolicitud == null) {
            setListaDetalleSolicitud(sgDetalleSolicitudEstanciaImpl.getAllIntegrantesBySolicitud(this.sgSolicitudEstanciaVo.getId(), null, null, false));
        }
    }

    public List<SgHuespedStaffVo> getHuespedesByHabitacionStaff() {
        return this.huespedStaffService.getAllSgHuespedStaffBySgStaffHabitacion(getHabitacion().getId());
    }

    public void eliminarUsuarioDetalle() {
        sgDetalleSolicitudEstanciaImpl.eliminarDetalleSolicitud(sesion.getUsuario(), getSgDetalleSolicitudEstancia(), Constantes.BOOLEAN_TRUE);
    }

    public void cancelarSolicitudRegistroHuesped(int idDetSol) {
        sgDetalleSolicitudEstanciaImpl.cancelarSolicitudRegistroHuesped(sesion.getUsuario(), idDetSol, true);
        List<DetalleEstanciaVO> ld = sgDetalleSolicitudEstanciaImpl.traerDetallePorSolicitud(getSgSolicitudEstanciaVo().getId(), Constantes.NO_ELIMINADO);
        if (ld.size() == 1) {
            sgSolicitudEstanciaImpl.cancelarSolicitudEstancia(sesion.getUsuario(), getSgSolicitudEstanciaVo(), "Se cancela por cancelación del huésped", Constantes.FALSE, Constantes.TRUE);
        } else {
            int can = 0;
            int finaliza = 0;
            for (DetalleEstanciaVO detalleEstanciaVO : ld) {
                if (detalleEstanciaVO.isCancelado()) {
                    can++;
                }
                if (!detalleEstanciaVO.isRegistrado()
                        && !detalleEstanciaVO.isCancelado()) {

                    finaliza++;
                }
            }
            if (can == ld.size()) {
                sgSolicitudEstanciaImpl.cancelarSolicitudEstancia(sesion.getUsuario(), getSgSolicitudEstanciaVo(), "Se cancela por cancelacion de huespedes", Constantes.FALSE, Constantes.TRUE);
            }
            if (finaliza == 0) {
                sgSolicitudEstanciaImpl.finalizaSolicitud(getSgSolicitudEstanciaVo().getId(), sesion.getUsuario().getId());
            }
        }
        setListaEstancia(new ListDataModel(findAllSgSolicitudEstanciaByUsuarioAndEstatus(Constantes.ESTATUS_SOLICITUD_ESTANCIA_PENDIENTE, false)));
        PrimeFaces.current().executeScript("PF('dlgSolEst').hide();");
        this.mensaje = "Se ha cancelado la asignación de habitación del Huésped ";
    }

    public void goToRegistroStaff(DetalleEstanciaVO detVo) {
        setFlag(false);
        setDisabled(true);
        setDisabledAux(true);
        setSgDetalleSolicitudEstancia(detVo);
        setMrPopupRegistrarHuespedEnStaff(!isMrPopupRegistrarHuespedEnStaff());

        PrimeFaces.current().executeScript("$(dlgRegHuesped).modal('show')");
        PrimeFaces.current().executeScript("PF('dlgSolEst').hide()");
    }

    public void seleccionarHabitacion(SgStaffHabitacion hab) {
        setHabitacion(hab);
        hab.setOcupada(Boolean.TRUE);
        if (getHabitacion().isOcupada()) {
            log("se abrirá el popup");
        }
    }

    public void cargarHabitacionesInTableByStaff() {
        llenarHabitacionesByStaff();
    }

    /**
     *
     * Registro de huespedes en el staff u hotel
     */
    public void trearSolicitudEstanciaParaRegistro() {
        try {
            if (getListaSolicitud() == null || getListaSolicitud().isEmpty()) {
                log("Oficina : " + sesion.getOficinaActual().getId());
                setListaSolicitud(sgSolicitudEstanciaImpl.trearSolicitudEstanciaPorOficina(sesion.getOficinaActual().getId(), Constantes.ESTATUS_SOLICITUD_ESTANCIA_SOLICITADA, sesion.getUsuario().getId(), Constantes.BOOLEAN_FALSE));
            }
        } catch (Exception e) {
            System.out.println("Error al traer sol huespedes");
        }
    }

    public void traerDetalleSolicitud() {
        setListaDetalleSolicitud(sgDetalleSolicitudEstanciaImpl.getAllIntegrantesBySolicitud(this.sgSolicitudEstanciaVo.getId(), Boolean.FALSE, Boolean.FALSE, false));
    }

    public List<SgHuespedHotelServicioVo> getAllServiciosFacturaEmpresa() {
        return getSgHuespedHotel() != null ? this.sgHuespedHotelServicioImpl.getAllSgTipoEspecificoFacturaEmpesaBySgHotel(getSgHuespedHotel().getId()) : null;
    }

    public List<SgHotelTipoEspecificoVo> getallServiciosHotelFacturaEmpresa() {
        return this.sgHotelTipoEspecificoImpl.getAllSgHotelTipoEspecificoBySgHotelAndProvided(getIdHotel(), false, "nombre", true, false);
    }

    public int generateCarta() {
        int errors = 0;

        if (getId() < 0) {
            FacesUtils.addErrorMessage("popupGeneraCarta:msgsPpopupGeneraCarta", "Por favor elige quién dará Visto Bueno a la carta");
            return 1;
        }

        if (errors == 0) {
            updateServicios();
            return 0;
        }
        return 0;
    }

    public List getAllServiciosHotelIncluidosTarifa() {
        log("EstanciaBeanModel.getAllServiciosHotelIncluidosTarifa()");
        log("SgHuespedHotel: " + (getSgHuespedHotel() != null ? getSgHuespedHotel().getSgHotelHabitacion().getSgHotel().getId() : null));
        return getSgHuespedHotel() != null ? this.sgHotelTipoEspecificoImpl.getAllSgHotelTipoEspecificoBySgHotelAndProvided(getSgHuespedHotel().getSgHotelHabitacion().getSgHotel().getId(), true, "nombre", true, false) : null;
    }

    public void updateServicios() {
        List<SgHotelTipoEspecificoVo> list = getServiciosHotelFacturaEmpresa();
        log("====================Servicios facturados por la empresa para actualizar (Inicio) =====================");
//        for (SgHuespedHotelServicioVo vo : list) {
//            log(vo.toString());
//        }
        log("====================Servicios facturados por la empresa para actualizar (Fin) =====================");
        //this.sgHuespedHotelServicioImpl.updateServicios(list, getSgHuespedHotel().getId(), this.sesion.getUsuario().getId());
//        setServiciosHotelFacturaEmpresa((getAllServiciosFacturaEmpresa()));
    }

    //Registro de Huéspedes
    public void solicitudEstanciaByStatusEnviado() {
//        log("EstanciaBeanModel.getSolicitudEstanciaByStatusEnviado()");
        listaSolicitud = sgSolicitudEstanciaImpl.trearSolicitudEstanciaPorOficina(sesion.getOficinaActual().getId(), Constantes.ESTATUS_SOLICITUD_ESTANCIA_SOLICITADA, sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);

    }

    public void staffByOficinaList() {
        List<SgStaff> staffList = staffService.getAllStaffByStatusAndOficina(Constantes.NO_ELIMINADO, sesion.getOficinaActual().getId());
        setStaffListSelectItem(new ArrayList<>());
        for (SgStaff staff : staffList) {
            SelectItem item = new SelectItem(staff.getId(), staff.getNombre() + " | " + staff.getNumeroStaff());
            getStaffListSelectItem().add(item);
        }
    }

    public void validateTipoHuesped() {
        log("EstanciaBeanModel.validateTipoHuesped()");

        SgTipoEspecifico tipoHuesped = getTipoEspecificoById(idTipoEspecifico);
//
        if (tipoHuesped != null) {
            setTipoEspecifico(tipoHuesped);
            setIdTipoEspecifico(tipoHuesped.getId());
//
            setDisabledAux(false);
            setFechaIngresoHuesped(getSgSolicitudEstanciaVo().getInicioEstancia());
            log("FechaIngresoHuesped: " + getFechaIngresoHuesped());

            if (getIdTipoEspecifico() == 15) { //Tipo de Huésped Periodo de Prueba
//            log("Huésped - Periodo de Prueba");
                Date dp = sumaFecha();
                setFechaSalidaPropuesta(dp);
                setDisabled(true); //Deshabilita la fecha de Salida en la vista
                setFlag(true); //Renderiza la fecha de Salida Propuesta

            } else if (getIdTipoEspecifico() == 16) { //Tipo de Huésped Itinerante
//            log("Huésped - Itinerante");
                setFechaSalidaHuesped(getSgSolicitudEstanciaVo().getFinEstancia());
                log("FechaSalidaHuesped: " + getFechaSalidaHuesped());
                setFechaSalidaPropuesta(null);
                setDisabled(false); //Habilita la fecha de Salida en la vista
                setFlag(false); //Evita que se renderize la fecha de Salida Propuesta
            } else if (getIdTipoEspecifico() == 17) {  //Tipo de Huésped Base
//            log("Huésped - Base");
                setFechaSalidaHuesped(null);
                log("FechaSalidaHuesped: " + getFechaSalidaHuesped());
                setFechaSalidaPropuesta(null);
                log("FechaSalidaPropuesta: " + getFechaSalidaPropuesta());
                setDisabled(true); //Deshabilita la fecha de Salida en la vista
                setFlag(false); //Evita que se renderize la fecha de Salida Propuesta
            }
        } else {
            setFlag(false);
            setDisabled(true);
            setDisabledAux(true);
            setFechaIngresoHuesped(null);
            setFechaSalidaHuesped(null);
            setFechaSalidaPropuesta(null);
        }
    }

    public void llenarStaffWithAvailableRoomsByOficinaList() {
        try {
            List<SgStaff> staffList = this.staffService.getAllStaffWithAvailableRoomsByOficinaList(this.sesion.getOficinaActual().getId());
            staffListSelectItem = new ArrayList<>();
            for (SgStaff staff : staffList) {
                SelectItem item = new SelectItem(staff.getId(), staff.getNombre() + " | " + staff.getNumeroStaff());
                staffListSelectItem.add(item);
            }
        } catch (Exception ex) {
            Logger.getLogger(EstanciaBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getStaffByOficina() {
//        log("EstanciaBeanModel.getStaffByOficina()");
        try {
            setStaffDataModel(this.staffService.getAllStaffWithAvailableRoomsByOficinaList(this.sesion.getOficinaActual().getId()));
        } catch (Exception ex) {
            Logger.getLogger(EstanciaBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public SgSolicitudEstancia getSolicitudEstancia(int idSolicitudEstancia) {
//        log("EstanciaBeanModel.getSolicitudEstancia()");
        return solicitudEstanciaService.find(idSolicitudEstancia);
    }

    /**
     * Crea un DataModel con puros números. Los números son la cantidad de
     * habitaciones disponibles por Staff
     */
    public void getNumHabitacionesDisponiblesByStaff() {
//        log("EstanciaBeanModel.getHabitacionesDisponiblesByStaff()");
        getStaffByOficina();
        List<Integer> habitacionesDisponibles = new ArrayList<Integer>();

        for (SgStaff staffAux : staffDataModel) {
            //Traigo todas las habitaciones de cada Staff y averiguo cuántas están desocupadas usando su atributo  'ocupada'
            List<SgStaffHabitacion> habitaciones = habitacionStaffService.getAllHabitacionesByStaff(staffAux, Constantes.NO_ELIMINADO);
            int cont = 0;
            for (SgStaffHabitacion hab : habitaciones) {
                if (!hab.isOcupada()) {
                    cont++;
                }
            }
            log("El Staff " + staffAux.getNombre() + " tiene " + cont + " habitaciones disponibles");
            habitacionesDisponibles.add(cont);
        }
        setNumHabitacionesDisponiblesByStaffDataModel(habitacionesDisponibles);
    }

    /**
     * Devuelve un DataModel con todas las habitaciones de un Staff
     *
     * @param staff
     * @return
     */
    public void llenarHabitacionesByStaff() {
        log("EstanciaBeanModel.getHabitacionesByStaff()");
        List<SgStaffHabitacion> habitaciones = habitacionStaffService.getAllHabitacionesByStaffAndOcupadoList(idStaff, null, false);
        setHabitacionesStaffDataModel(habitaciones);
    }

    public void reloadSolicitudEstancia() {
//        log("EstanciaBeanModel.reloadSolicitudEstancia()");
        this.sgSolicitudEstanciaVo = (this.sgSolicitudEstanciaVo != null ? sgSolicitudEstanciaImpl.buscarEstanciaPorId(this.sgSolicitudEstanciaVo.getId()) : null);
    }

    public void listaHabitacion() {
        List<SgHotelHabitacion> lh = sgHotelHabitacionImpl.findAllHabitacionesPorIdHotel(idHotel);
        for (SgHotelHabitacion sgHabitacion : lh) {
            BigDecimal precio = sgHabitacion.getPrecio();
            precio = precio.setScale(2, RoundingMode.HALF_UP);
            SelectItem item = new SelectItem(sgHabitacion.getId(), sgHabitacion.getSgTipoEspecifico().getNombre() + " || " + precio);
            listaHabitacionHotel.add(item);
        }
    }

    public SgTipoEspecifico getTipoEspecificoById(int id) {
        log("EstanciBeanModel.getTipoEspecificoById()");
        if (id != 0 && id != -1) {
            log("Tipo Específico encontrado por id: " + (id) + " - " + tipoEspecificoService.find(id));
            return tipoEspecificoService.find(id);
        } else {
//            if (this.idTipoEspecifico != -1 && this.idTipoEspecifico != 0) {
//                log("Tipo especifico encontrado: " + this.idTipoEspecifico + " - " + tipoEspecificoService.find(this.idTipoEspecifico));
//                return tipoEspecificoService.find(this.idTipoEspecifico);
//            } else {
            return null;
//            }
        }
    }

//    public List<SelectItem> listaHabitacionCambio() {
//        log("EstanciaBeanModel.listaHabitacionCambio()");
//        if (getId() > 0) {
//            List<SelectItem> l = new ArrayList<>();
//            try {
//                setSgHotel(sgHotelImpl.find(getId()));
//                List<SgHotelHabitacion> lh = sgHotelHabitacionImpl.findAllHabitacionesToHotel(getSgHotel());
//                for (SgHotelHabitacion sgHabitacion : lh) {
//                    BigDecimal precio = sgHabitacion.getPrecio();
//                    precio = precio.setScale(2, RoundingMode.HALF_UP);
//                    SelectItem item = new SelectItem(sgHabitacion.getId(), sgHabitacion.getSgTipoEspecifico().getNombre() + " || " + precio);
//                    l.add(item);
//                }
//            } catch (Exception e) {
//            }
//            return l;
//        } else {
//            return null;
//        }
//    }
    public void llenarTiposHuespedes() {
        this.sgTipo = buscarTipo();
        List<SgTipoTipoEspecifico> lh = sgTipoTipoEspecificoImpl.traerPorTipo(getSgTipo(), Constantes.NO_ELIMINADO);
        for (SgTipoTipoEspecifico sgTipoTipoEspecifico : lh) {
            SelectItem item = new SelectItem(sgTipoTipoEspecifico.getSgTipoEspecifico().getId(), sgTipoTipoEspecifico.getSgTipoEspecifico().getNombre());
            listaTiposHuespedes.add(item);
        }
    }

    public void goToRegistroRegistroHotel(DetalleEstanciaVO detEstVo) {
        setServiciosHotelFacturaEmpresa(null);
        setSgDetalleSolicitudEstancia(detEstVo);
        setFlag(false);
        setDisabled(true);
        setDisabledAux(true);
        setPopUp(true);
        setSubirArchivoPop(false);
        setSgHuespedHotel(new SgHuespedHotel());
        //
        llenarListaHoteles();
        PrimeFaces.current().executeScript("$(dlgRegHuespedHotel).modal('show');");
        PrimeFaces.current().executeScript("PF('dlgSolEst').hide()");
    }

    private void llenarListaHoteles() {
        listaHoteles = new ArrayList<>();
        List<SgHotel> hoteles = sgHotelImpl.getAllHotel(sesion.getOficinaActual().getId());
        hoteles.stream().forEach(h -> {
            listaHoteles.add(new SelectItem(h.getId(), h.getProveedor().getNombre()));
        });
    }

    public void registrarHuespedHotel() {
        try {
            if (this.fechaSalidaPropuesta != null) {
                this.sgHuespedHotel.setFechaIngreso(this.getFechaIngresoHuesped());
                this.sgHuespedHotel.setFechaSalida(fechaSalidaPropuesta);
                this.sgHuespedHotel.setSgTipoEspecifico(this.tipoEspecifico);
                sgHuespedHotelImpl.guardarHuespedHotel(sesion.getUsuario().getId(), getSgHuespedHotel(), sgDetalleSolicitudEstancia.getIdDetalleEstancia(),
                        sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getIdInvitado() : 0,
                        sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getInvitado() : "",
                        sgDetalleSolicitudEstancia.getIdInvitado() == 0 ? sgDetalleSolicitudEstancia.getUsuario() : "",
                        sgDetalleSolicitudEstancia.getTipoDetalle(), sgDetalleSolicitudEstancia.getCorreoUsuario(),
                        getIdHotel(), getIdHabitacion(), getSgSolicitudEstanciaVo().getId(), getSgTipo(), getIdTipoEspecifico(), "");

                tipoEspecificoService.ponerUsadoTipoEspecifico(getIdTipoEspecifico(), sesion.getUsuario());

            } else {
                this.sgHuespedHotel.setFechaIngreso(this.getFechaIngresoHuesped());
                this.sgHuespedHotel.setFechaSalida(getFechaSalidaHuesped());
                this.sgHuespedHotel.setSgTipoEspecifico(this.tipoEspecifico);
                sgHuespedHotelImpl.guardarHuespedHotel(sesion.getUsuario().getId(), getSgHuespedHotel(), sgDetalleSolicitudEstancia.getIdDetalleEstancia(),
                        sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getIdInvitado() : 0,
                        sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getInvitado() : "",
                        sgDetalleSolicitudEstancia.getIdInvitado() == 0 ? sgDetalleSolicitudEstancia.getUsuario() : "",
                        sgDetalleSolicitudEstancia.getTipoDetalle(), sgDetalleSolicitudEstancia.getCorreoUsuario(),
                        getIdHotel(), getIdHabitacion(), getSgSolicitudEstanciaVo().getId(), getSgTipo(), getIdTipoEspecifico(), "");

                tipoEspecificoService.ponerUsadoTipoEspecifico(getIdTipoEspecifico(), sesion.getUsuario());

            }

            //Guardar los Servicios facturados por la empresa
            List<SgHotelTipoEspecificoVo> list = (getServiciosHotelFacturaEmpresa());
            for (SgHotelTipoEspecificoVo vo : list) {
                if (vo.isFacturadoEmpresa()) {
                    this.sgHuespedHotelServicioImpl.save(getSgHuespedHotel().getId(), vo.getIdSgTipoEspecifico(), true, this.sesion.getUsuario().getId());
                }
            }
            setListaEstancia(new ListDataModel(findAllSgSolicitudEstanciaByUsuarioAndEstatus(Constantes.ESTATUS_SOLICITUD_ESTANCIA_PENDIENTE, false)));

            //Guardar los servicios proveídos por el hotel
            List<SgHotelTipoEspecificoVo> list2 = this.sgHotelTipoEspecificoImpl.getAllSgHotelTipoEspecificoBySgHotelAndProvided(idHotel, true, "id", true, false);
            for (SgHotelTipoEspecificoVo vo : list2) {
                this.sgHuespedHotelServicioImpl.save(getSgHuespedHotel().getId(), vo.getIdSgTipoEspecifico(), false, this.sesion.getUsuario().getId());
            }
            if (listaDetalleSolicitud != null && listaDetalleSolicitud.size() == 1) {
                sgSolicitudEstanciaImpl.finalizaSolicitud(sgSolicitudEstanciaVo.getId(), sesion.getUsuario().getId());
                PrimeFaces.current().executeScript("PF('dlgSolEst').hide()");
            } else {
                traerDetalleSolicitud();
                PrimeFaces.current().executeScript("PF('dlgSolEst').show()");
            }
            PrimeFaces.current().executeScript("$(dlgRegHuespedHotel).modal('hide');");

        } catch (Exception e) {
            log(e.getMessage());
        }
    }

    public void backToAsignarHabitacionFromRegistroHotel() {
        setPopUp(false);
        setDisabled(false);
        setFlag(false);
        setMensaje("");
        setIdStaff(-1);
        setIdHotel(-1);
        setIdHabitacion(-1);
        setIdTipoEspecifico(-1);
        setHabitacion(null);
        setTipoEspecifico(null);
        setSgHuespedHotel(null);
        setFechaIngresoHuesped(null);
        setFechaSalidaHuesped(null);
        setFechaSalidaPropuesta(null);
        setHabitacionesStaffDataModel(null);
        setSgDetalleSolicitudEstancia(null);
        this.setServiciosHotelFacturaEmpresa(null);
        //
        PrimeFaces.current().executeScript("$(dlgRegHuesped).modal('hide');");
    }

    public void chargeServiciosHotelFacturaEmpresaValueChangeListener() {
        setServiciosHotelFacturaEmpresa((getallServiciosHotelFacturaEmpresa()));
        listaHabitacion();
    }

    public void registrarHuespedStaff() {
//        log("EstanciaBeanModel.registrarHuespedStaff()");
        try {
            if (tipoEspecifico != null) {
                if (this.tipoEspecifico.getId() == 15) { //Tipo de Huésped Periodo de Prueba
                    //int idDetalleEstancia, int idInvitado, String invitado, String empleado, String tipoEspecifico, String correoEmpleado
                    huespedStaffService.saveHuespedStaff(this.sgSolicitudEstanciaVo, this.sgDetalleSolicitudEstancia.getIdDetalleEstancia(),
                            sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getIdInvitado() : 0,
                            sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getInvitado() : "",
                            sgDetalleSolicitudEstancia.getIdInvitado() == 0 ? sgDetalleSolicitudEstancia.getUsuario() : "",
                            sgDetalleSolicitudEstancia.getTipoDetalle(), sgDetalleSolicitudEstancia.getCorreoUsuario(), this.sgTipo, this.tipoEspecifico, this.habitacion, this.getFechaIngresoHuesped(), this.fechaSalidaPropuesta, sesion.getUsuario().getId());
                } else if (this.tipoEspecifico.getId() == 16) { //Tipo de Huésped Itinerante
                    huespedStaffService.saveHuespedStaff(this.sgSolicitudEstanciaVo, this.sgDetalleSolicitudEstancia.getIdDetalleEstancia(),
                            sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getIdInvitado() : 0,
                            sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getInvitado() : "",
                            sgDetalleSolicitudEstancia.getIdInvitado() == 0 ? sgDetalleSolicitudEstancia.getUsuario() : "",
                            sgDetalleSolicitudEstancia.getTipoDetalle(), sgDetalleSolicitudEstancia.getCorreoUsuario(), this.sgTipo, this.tipoEspecifico, this.habitacion, this.getFechaIngresoHuesped(), this.getFechaSalidaHuesped(), sesion.getUsuario().getId());
                } else if (this.tipoEspecifico.getId() == 17) {  //Tipo de Huésped Base
                    huespedStaffService.saveHuespedStaff(this.sgSolicitudEstanciaVo, this.sgDetalleSolicitudEstancia.getIdDetalleEstancia(),
                            sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getIdInvitado() : 0,
                            sgDetalleSolicitudEstancia.getIdInvitado() != 0 ? sgDetalleSolicitudEstancia.getInvitado() : "",
                            sgDetalleSolicitudEstancia.getIdInvitado() == 0 ? sgDetalleSolicitudEstancia.getUsuario() : "",
                            sgDetalleSolicitudEstancia.getTipoDetalle(), sgDetalleSolicitudEstancia.getCorreoUsuario(), this.sgTipo, this.tipoEspecifico, this.habitacion, this.getFechaIngresoHuesped(), null, sesion.getUsuario().getId());
                }
                //
                setListaEstancia(new ListDataModel(findAllSgSolicitudEstanciaByUsuarioAndEstatus(Constantes.ESTATUS_SOLICITUD_ESTANCIA_PENDIENTE, false)));
                //
                if (listaDetalleSolicitud.size() == 1) {
                    sgSolicitudEstanciaImpl.finalizaSolicitud(sgSolicitudEstanciaVo.getId(), sesion.getUsuario().getId());
                    PrimeFaces.current().executeScript("PF('dlgSolEst').hide()");
                } else {
                    traerDetalleSolicitud();
                    PrimeFaces.current().executeScript("PF('dlgSolEst').show()");
                }
                PrimeFaces.current().executeScript("$(dlgRegHuesped).modal('hide')");
            }
        } catch (SIAException siae) {
            log(siae.getMensajeParaProgramador());
        } catch (Exception e) {
            log(e.getMessage());
        }
    }

    public SgTipo buscarTipo() {
        setSgTipo(sgTipoImpl.find(4));
        return getSgTipo();
    }

    public boolean cancelarSolicitudEstancia() {
        boolean v = sgSolicitudEstanciaImpl.cancelarSolicitudEstancia(sesion.getUsuario(), getSgSolicitudEstanciaVo(), getMensaje(),
                Constantes.TRUE, isConCorreo());

        //Recargar lista de Solicitudes
        log("DEspues de cancelar la solicitud:  " + getSgSolicitudEstanciaVo().getId());
        this.listaSolicitud = (sgSolicitudEstanciaImpl.trearSolicitudEstanciaPorOficina(sesion.getOficinaActual().getId(), Constantes.ESTATUS_SOLICITUD_ESTANCIA_SOLICITADA, sesion.getUsuario().getId(), Constantes.NO_ELIMINADO));
        return v;
    }

    public boolean eliminarSolicitudEstancia(SgSolicitudEstanciaVo est) {
        mensaje = "Eliminado por el analista de Servicios Generales";
        sgSolicitudEstanciaVo = est;
        boolean v = sgSolicitudEstanciaImpl.cancelarSolicitudEstancia(sesion.getUsuario(),
                getSgSolicitudEstanciaVo(), getMensaje(),
                Constantes.FALSE, Constantes.FALSE);

        //Recargar lista de Solicitudes
        log("DEspues de cancelar la solicitud:  " + getSgSolicitudEstanciaVo().getId());
        this.listaSolicitud = (sgSolicitudEstanciaImpl.trearSolicitudEstanciaPorOficina(sesion.getOficinaActual().getId(), Constantes.ESTATUS_SOLICITUD_ESTANCIA_SOLICITADA, sesion.getUsuario().getId(), Constantes.NO_ELIMINADO));
        return v;
    }

    public DataModel traerHospedadosStaff() {
        try {
            return new ListDataModel(huespedStaffService.getAllHuespedesBySolicitud(this.sgSolicitudEstanciaVo.getId()));
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel traerRegistroHospedadosHotel() {
        try {
            return new ListDataModel(sgHuespedHotelImpl.traerHospedadosHotel(this.sgSolicitudEstanciaVo.getId()));
        } catch (Exception e) {
            return null;
        }
    }

    public List<SgHuespedHotelVo> findSgHuespedHotelByNumeroReservacion(String numeroHabitacion) {
        return this.sgHuespedHotelImpl.findAllSgHuespedHotelByNumeroReservacion(numeroHabitacion);
    }

    /**
     * Salida de huespedes en hotel y staff-house
     *
     */
    public void marcarSalidaHuesped() {
        log("Fecha real salida: " + this.sgHuespedHotel.getFechaRealSalida());
        sgHuespedHotelImpl.marcarSalidaHuesped(sesion.getUsuario(), getSgHuespedHotel());
        sgHuespedHotelImpl.traerHuespedPorHotel(getIdHotel(), sesion.getUsuario().getId());
    }

    public void cancelarRegistroHuesped() {
        sgHuespedHotelImpl.cancelarRegistroHuesped(sesion.getUsuario(), getSgHuespedHotel());
        sgHuespedHotelImpl.traerHuespedPorHotel(getIdHotel(), sesion.getUsuario().getId());
    }

    public void eliminarRegistroHuesped() {
        sgHuespedHotelImpl.eliminarRegistroHuesped(sesion.getUsuario(), getSgHuespedHotel());
    }

    public void actualizarNumeroReservacionYFechasHuespedHotel() {
        SgHuespedHotel hh = getSgHuespedHotel();
        this.sgHuespedHotelImpl.actualizar(hh.getId(), hh.getSgTipoEspecifico().getId(), hh.getSgHotelHabitacion().getId(), getDescripcion(), hh.getFechaIngreso(), hh.getFechaSalida(), true, false, this.sesion.getUsuario().getId());
    }

    /**
     * * actualizar la fecha de salida proplongada por un semaforo
     */
    public void actualizarFechaSalidaHuespedHotel() {
        try {
            this.sgHuespedHotelImpl.actualizarFechaSalida(getSgHuespedHotel().getId(), getFechaSalidaPropuesta(), sesion.getUsuario().getId());
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void actualizarFechaSalidaHuespedStaff() {
        try {
            this.huespedStaffService.actualizarFechaSalida(getHuespedStaff().getId(), getFechaSalidaPropuesta(), sesion.getUsuario().getId());
            this.setDataModel(null);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * ***************************************************************
     */
    public String dirCarta() {
        if (getSgHuespedHotel() != null) {
            return "SGyL/Huesped/" + getSgHuespedHotel().getId() + "/";
        }
        return "";
    }

    public boolean guardarArchivoCarta(String fileName, String ruta, String contentType, long size) {
        boolean v = false;
        SiAdjunto siAdjunto
                = siAdjuntoImpl.guardarArchivoDevolverArchivo(
                        sesion.getUsuario().getId(),
                        1,
                        ruta + fileName,
                        fileName,
                        contentType,
                        size,
                        9,
                        "sgl"
                );
        if (siAdjunto != null) {
            sgHuespedHotelImpl.guardarCartaAsignacion(
                    sesion.getUsuario(),
                    getSgHuespedHotel(),
                    siAdjunto
            );
            v = true;
        }
        return v;
    }

    public void quitarCartaAsignacion() {
        boolean v;
        //Se eliminan fisicamente los archivos
        String path = this.siParametroImpl.find(1).getUploadDirectory();
        try {
            File file = new File(path + getSgHuespedHotel().getSiAdjunto().getUrl());
            log("Archivo: " + path + getSgHuespedHotel().getSiAdjunto().getUrl());
            if (file.delete()) {
                v = sgHuespedHotelImpl.quitarCartaAsignacion(sesion.getUsuario(), getSgHuespedHotel());
                if (v) {
                    siAdjuntoImpl.eliminarArchivo(getSgHuespedHotel().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
                }
            }
            //elimina la carpeta
            String dir = "SGyL/Huesped/" + getSgHuespedHotel().getId();
            File sessionfileUploadDirectory = new File(path + dir);
            if (sessionfileUploadDirectory.isDirectory()) {
                try {
                    sessionfileUploadDirectory.delete();
                } catch (SecurityException e) {
                    log(e.getMessage());
                }
            }
        } catch (Exception e) {
            log(e.getMessage());
        }
    }

    /**
     * Cambia a Huésped de un Hotel a otro
     *
     * @return
     */
//    public DataModel getHabitacionesByStaffCambioHuesped() {
//        try {
//            setLista((habitacionStaffService.getAllHabitacionesByStaffAndOcupadoList(getHabitacion().getSgStaff(), false, false)));
//            return getLista();
//        } catch (Exception e) {
//            return null;
//        }
//    }
    /**
     * Cambia a un Huésped de un Hotel a un Staff
     *
     * @return
     */
//    public boolean guardarCambioHuespedStaff() {
//        log("guardarCambioHuespedStaff");
//        boolean v;
//        getHuespedStaff().setSgDetalleSolicitudEstancia(getSgHuespedHotel().getSgDetalleSolicitudEstancia());
//        v = huespedStaffService.guardarCambioHuespedStaff(sesion.getUsuario(), getHuespedStaff(),
//                getHabitacion(),
//                getIdStaff(),
//                getSgTipo(),
//                getSgHuespedHotel());
//        setLista(sgHuespedHotelImpl.traerHuespedPorHotel(getIdHotel(), sesion.getUsuario().getId()));
//        return v;
//    }

    /*
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Cambio de Habitación Staff - INICIO * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * * *
     */
    public void huespedesRegistradosEnStaff() throws SIAException, Exception {
//        log("EstanciaBeanModel.getHuespedesRegistradosEnStaff()");
        this.dataModel = new ListDataModel(huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));

    }

    public void changeHuespedToHabitacionStaff() throws SIAException, Exception {
        try {
            huespedStaffService.changeHuespedStaff(this.huespedStaff, this.habitacion, this.getFechaIngresoHuesped(),
                    this.getFechaSalidaHuesped(), fechaRealIngresoHuesped, fechaRealSalidaHuesped, sesion.getUsuario().getId());
            this.dataModel = new ListDataModel(huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
            this.mensaje = "El Huésped " + (this.huespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? this.huespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : this.huespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
                    + " ha sido cambiado a la habitación " + this.habitacion.getNombre() + "|" + this.getHabitacion().getNumeroHabitacion() + " satisfactoriamente";

        } catch (Exception e) {
            log("Excepcion al cambiar un huesped " + e.getMessage());
        }
    }

    public void changeHuespedToHabitacionHotel() throws SIAException, Exception {
//        log("EstanciaBeanModel.changeHuespedToHabitacionHotel()");
        huespedStaffService.changeHuespedStaffToHotel(this.huespedStaff, this.idHotel, this.idHabitacion, this.numeroHabitacion, this.fechaRealIngresoHuesped, this.fechaRealSalidaHuesped, this.getFechaIngresoHuesped(), this.getFechaSalidaHuesped(), sesion.getUsuario().getId());
        SgHotel hotel = hotelService.find(this.idHotel);
        this.dataModel = new ListDataModel(huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
        this.mensaje = "El Huésped " + (this.huespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? this.huespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : this.huespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
                + " ha sido cambiado al Hotel " + hotel.getProveedor().getNombre() + " con el número de reservación: " + this.numeroHabitacion + " satisfactoriamente";
    }

    public void exitHuespedStaff() throws SIAException, Exception {
//        log("EstanciaBeanModel.exitHuespedStaff()");
        //Validar fechas
        if (!siManejoFechaImpl.dayIsSame(this.fechaRealIngresoHuesped, this.fechaRealSalidaHuesped)) {
            if (!siManejoFechaImpl.theFirstDateIsLessThanTheSecond(this.fechaRealIngresoHuesped, this.fechaRealSalidaHuesped)) {
                throw new SIAException(EstanciaBeanModel.class.getName(),
                        "exitHuespedStaff",
                        Constantes.MENSAJE_FECHA_REAL_INGRESO_HUESPED_INVALIDA,
                        ("fechaRealIngresoHuesped: " + this.fechaRealIngresoHuesped + " fechaRealSalidaHuesped: " + this.fechaRealSalidaHuesped));
            }
        }
        huespedStaffService.exitHuespedStaff(this.huespedStaff, this.fechaRealIngresoHuesped, this.fechaRealSalidaHuesped, sesion.getUsuario().getId());
        this.dataModel = new ListDataModel(huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
        this.mensaje = "La estancia del Huésped " + (this.huespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? this.huespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : this.huespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
                + " en el Staff " + this.huespedStaff.getSgStaffHabitacion().getSgStaff().getNombre() + "|" + this.huespedStaff.getSgStaffHabitacion().getSgStaff().getNumeroStaff()
                + " en la Habitación " + this.huespedStaff.getSgStaffHabitacion().getNombre() + "|" + this.huespedStaff.getSgStaffHabitacion().getNumeroHabitacion()
                + " ha terminado";
    }

    public void establecerFechaSalidaHuespedStaffBase() throws SIAException, Exception {
        this.huespedStaff.setFechaSalida(this.fechaSalidaPropuesta);
        huespedStaffService.update(this.huespedStaff, sesion.getUsuario().getId());
        this.dataModel = new ListDataModel(huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
    }

    public void cancelHospedajeStaff() throws SIAException, Exception {
//        log("EstanciaBeanModel.cancelHospedajeStaff()");
        huespedStaffService.cancelHospedajeStaff(this.huespedStaff, sesion.getUsuario().getId());
        this.dataModel = new ListDataModel(huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
        this.mensaje = "La estancia del Huésped " + (this.huespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? this.huespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : this.huespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
                + " en el Staff " + this.huespedStaff.getSgStaffHabitacion().getSgStaff().getNombre() + "|" + this.huespedStaff.getSgStaffHabitacion().getSgStaff().getNumeroStaff()
                + " en la Habitación " + this.huespedStaff.getSgStaffHabitacion().getNombre() + "|" + this.huespedStaff.getSgStaffHabitacion().getNumeroHabitacion()
                + " se ha cancelado";
    }

    //* *****************************************GENERA CARTA *************************************************
    public List<UsuarioTipoVo> getAllUserAprobarCartHuesped() {
        return siUsuarioCopiadoImpl.getListUser(15, sesion.getOficinaActual().getId());
    }

    public DataModel getListaAprobacionCarta() {
        try {
            List<UsuarioTipoVo> l = siUsuarioCopiadoImpl.getListUser(15, sesion.getOficinaActual().getId());
            setDataModel(new ListDataModel(l));
            return getDataModel();
        } catch (Exception e) {
            return null;
        }
    }

    public Date sumaFecha() {
        try {
            return siManejoFechaImpl.fechaSumarDias(getFechaIngresoHuesped(), 90);
        } catch (Exception e) {
            return new Date();
        }
    }

    /**
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Cambio de Habitación Staff - FIN * * * * * * * * * * * * * * * * * * * *
     * * * * * * * * * * * * * * * * *
     */
    /**
     * @return the sgDetalleSolicitudEstancia
     */
    public DetalleEstanciaVO getSgDetalleSolicitudEstancia() {
        return sgDetalleSolicitudEstancia;
    }

    /**
     * @param sgDetalleSolicitudEstancia the sgDetalleSolicitudEstancia to set
     */
    public void setSgDetalleSolicitudEstancia(DetalleEstanciaVO sgDetalleSolicitudEstancia) {
        this.sgDetalleSolicitudEstancia = sgDetalleSolicitudEstancia;
    }

    /**
     * @return the popUp
     */
    public boolean isPopUp() {
        return popUp;
    }

    /**
     * @param popUp the popUp to set
     */
    public void setPopUp(boolean popUp) {
        this.popUp = popUp;
    }

    /**
     * @return the crearPop
     */
    public boolean isCrearPop() {
        return crearPop;
    }

    /**
     * @param crearPop the crearPop to set
     */
    public void setCrearPop(boolean crearPop) {
        this.crearPop = crearPop;
    }

    /**
     * @return the modificarPop
     */
    public boolean isModificarPop() {
        return modificarPop;
    }

    /**
     * @param modificarPop the modificarPop to set
     */
    public void setModificarPop(boolean modificarPop) {
        this.modificarPop = modificarPop;
    }

    /**
     * @return the listaSelectItem
     */
    public List<SelectItem> getListaSelectItem() {
        return listaSelectItem;
    }

    /**
     * @param listaSelectItem the listaSelectItem to set
     */
    public void setListaSelectItem(List<SelectItem> listaSelectItem) {
        this.listaSelectItem = listaSelectItem;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the idOficina
     */
    public int getIdOficina() {
        return idOficina;
    }

    /**
     * @param idOficina the idOficina to set
     */
    public void setIdOficina(int idOficina) {
        this.idOficina = idOficina;
    }

    /**
     * @return the sgTipo
     */
    public SgTipo getSgTipo() {
        return sgTipo;
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(SgTipo sgTipo) {
        this.sgTipo = sgTipo;
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
        return idTipoEspecifico;
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
        this.idTipoEspecifico = idTipoEspecifico;
    }

    /**
     * @return the listaUsuariosAlta
     */
    public List<SelectItem> getListaUsuariosAlta() {
        return listaUsuariosAlta;
    }

    /**
     * @param listaUsuariosAlta the listaUsuariosAlta to set
     */
    public void setListaUsuariosAlta(List<SelectItem> listaUsuariosAlta) {
        this.listaUsuariosAlta = listaUsuariosAlta;
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
     * @return the detalleSolicitudVO
     */
    public DetalleSolicitudVO getDetalleSolicitudVO() {
        return detalleSolicitudVO;
    }

    /**
     * @param detalleSolicitudVO the detalleSolicitudVO to set
     */
    public void setDetalleSolicitudVO(DetalleSolicitudVO detalleSolicitudVO) {
        this.detalleSolicitudVO = detalleSolicitudVO;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the gerencia
     */
    public Gerencia getGerencia() {
        return gerencia;
    }

    /**
     * @param gerencia the gerencia to set
     */
    public void setGerencia(Gerencia gerencia) {
        this.gerencia = gerencia;
    }

    /**
     * @return the listaDetalleSolicitud
     */
    public List<DetalleEstanciaVO> getListaDetalleSolicitud() {
        return this.listaDetalleSolicitud;
    }

    /**
     * @param listaDetalleSolicitud the listaDetalleSolicitud to set
     */
    public void setListaDetalleSolicitud(List<DetalleEstanciaVO> listaDetalleSolicitud) {
        this.listaDetalleSolicitud = listaDetalleSolicitud;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the mrPopupDetalleSolicitudEstancia
     */
    public boolean isMrPopupDetalleSolicitudEstancia() {
        return mrPopupDetalleSolicitudEstancia;
    }

    /**
     * @param mrPopupDetalleSolicitudEstancia the
     * mrPopupDetalleSolicitudEstancia to set
     */
    public void setMrPopupDetalleSolicitudEstancia(boolean mrPopupDetalleSolicitudEstancia) {
        this.mrPopupDetalleSolicitudEstancia = mrPopupDetalleSolicitudEstancia;
    }

    /**
     * @return the mrPopupRegistrarHuespedEnStaff
     */
    public boolean isMrPopupRegistrarHuespedEnStaff() {
        return mrPopupRegistrarHuespedEnStaff;
    }

    /**
     * @param mrPopupRegistrarHuespedEnStaff the mrPopupRegistrarHuespedEnStaff
     * to set
     */
    public void setMrPopupRegistrarHuespedEnStaff(boolean mrPopupRegistrarHuespedEnStaff) {
        this.mrPopupRegistrarHuespedEnStaff = mrPopupRegistrarHuespedEnStaff;
    }

    /**
     * @return the mrPopupRegistrarHuespedEnHotel
     */
    public boolean isMrPopupRegistrarHuespedEnHotel() {
        return mrPopupRegistrarHuespedEnHotel;
    }

    /**
     * @param mrPopupRegistrarHuespedEnHotel the mrPopupRegistrarHuespedEnHotel
     * to set
     */
    public void setMrPopupRegistrarHuespedEnHotel(boolean mrPopupRegistrarHuespedEnHotel) {
        this.mrPopupRegistrarHuespedEnHotel = mrPopupRegistrarHuespedEnHotel;
    }

    /**
     * @return the idHotel
     */
    public int getIdHotel() {
        return idHotel;
    }

    /**
     * @param idHotel the idHotel to set
     */
    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    /**
     * @return the idIntegrante
     */
    public int getIdIntegrante() {
        return idIntegrante;
    }

    /**
     * @param idIntegrante the idIntegrante to set
     */
    public void setIdIntegrante(int idIntegrante) {
        this.idIntegrante = idIntegrante;
    }

    /**
     * @return the idHabitacion
     */
    public int getIdHabitacion() {
        return idHabitacion;
    }

    /**
     * @param idHabitacion the idHabitacion to set
     */
    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    /**
     * @return the numeroHabitacion
     */
    public int getNumeroHabitacion() {
        return numeroHabitacion;
    }

    /**
     * @param numeroHabitacion the numeroHabitacion to set
     */
    public void setNumeroHabitacion(int numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    /**
     * @return the sgHuespedHotel
     */
    public SgHuespedHotel getSgHuespedHotel() {
        return sgHuespedHotel;
    }

    /**
     * @param sgHuespedHotel the sgHuespedHotel to set
     */
    public void setSgHuespedHotel(SgHuespedHotel sgHuespedHotel) {
        this.sgHuespedHotel = sgHuespedHotel;
    }

    /**
     * @return the staffDataModel
     */
    public List<SgStaff> getStaffDataModel() {
        return staffDataModel;
    }

    /**
     * @param staffDataModel the staffDataModel to set
     */
    public void setStaffDataModel(List<SgStaff> staffDataModel) {
        this.staffDataModel = staffDataModel;
    }

    /**
     * @return the habitacionesDisponiblesByStaffDataModel
     */
    public List<Integer> getNumHabitacionesDisponiblesByStaffDataModel() {
        return numHabitacionesDisponiblesByStaffDataModel;
    }

    /**
     * @param habitacionesDisponiblesByStaffDataModel the
     * habitacionesDisponiblesByStaffDataModel to set
     */
    public void setNumHabitacionesDisponiblesByStaffDataModel(List<Integer> numHabitacionesDisponiblesByStaffDataModel) {
        this.numHabitacionesDisponiblesByStaffDataModel = numHabitacionesDisponiblesByStaffDataModel;
    }

    /**
     * @return the idStaff
     */
    public int getIdStaff() {
        return idStaff;
    }

    /**
     * @param idStaff the idStaff to set
     */
    public void setIdStaff(int idStaff) {
        this.idStaff = idStaff;
    }

    /**
     * @return the habitacionesStaffDataModel
     */
    public List<SgStaffHabitacion> getHabitacionesStaffDataModel() {
        return habitacionesStaffDataModel;
    }

    /**
     * @param habitacionesStaffDataModel the habitacionesStaffDataModel to set
     */
    public void setHabitacionesStaffDataModel(List<SgStaffHabitacion> habitacionesStaffDataModel) {
        this.habitacionesStaffDataModel = habitacionesStaffDataModel;
    }

    /**
     * @return the mrPopupConfirmacionAsignacion
     */
    public boolean isMrPopupConfirmacionAsignacion() {
        return mrPopupConfirmacionAsignacion;
    }

    /**
     * @param mrPopupConfirmacionAsignacion the mrPopupConfirmacionAsignacion to
     * set
     */
    public void setMrPopupConfirmacionAsignacion(boolean mrPopupConfirmacionAsignacion) {
        this.mrPopupConfirmacionAsignacion = mrPopupConfirmacionAsignacion;

    }

    /*
     * @return the sgHotel
     */
    public SgHotel getSgHotel() {
        if (this.sgHotel == null && this.idHotel > 0) {
            this.sgHotel = sgHotelImpl.find(this.idHotel);
        }
        return sgHotel;
    }

    /**
     * @param sgHotel the sgHotel to set
     */
    public void setSgHotel(SgHotel sgHotel) {
        this.sgHotel = sgHotel;
    }

    /**
     * @return the habitacion
     */
    public SgStaffHabitacion getHabitacion() {
        return habitacion;
    }

    /**
     * @param habitacion the habitacion to set
     */
    public void setHabitacion(SgStaffHabitacion habitacion) {
        this.habitacion = habitacion;
    }

    /**
     * @return the sgHuespedHotelSeleccionado
     */
    public SgHuespedHotel getSgHuespedHotelSeleccionado() {
        return sgHuespedHotelSeleccionado;
    }

    /**
     * @param sgHuespedHotelSeleccionado the sgHuespedHotelSeleccionado to set
     */
    public void setSgHuespedHotelSeleccionado(SgHuespedHotel sgHuespedHotelSeleccionado) {
        this.sgHuespedHotelSeleccionado = sgHuespedHotelSeleccionado;
    }

    /**
     * @return the opcionSeleccionada
     */
    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(String opcionSeleccionada) {
        this.opcionSeleccionada = opcionSeleccionada;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
        return dataModel;
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * @return the huespedStaff
     */
    public SgHuespedStaff getHuespedStaff() {
        return huespedStaff;
    }

    /**
     * @param huespedStaff the huespedStaff to set
     */
    public void setHuespedStaff(SgHuespedStaff huespedStaff) {
        this.huespedStaff = huespedStaff;
    }

    /**
     * @return the fechaRealIngresoHuesped
     */
    public Date getFechaRealIngresoHuesped() {
        return fechaRealIngresoHuesped;
    }

    /**
     * @param fechaRealIngresoHuesped the fechaRealIngresoHuesped to set
     */
    public void setFechaRealIngresoHuesped(Date fechaRealIngresoHuesped) {
        this.fechaRealIngresoHuesped = fechaRealIngresoHuesped;
    }

    /**
     * @return the fechaRealSalidaHuesped
     */
    public Date getFechaRealSalidaHuesped() {
        return fechaRealSalidaHuesped;
    }

    /**
     * @param fechaRealSalidaHuesped the fechaRealSalidaHuesped to set
     */
    public void setFechaRealSalidaHuesped(Date fechaRealSalidaHuesped) {
        this.fechaRealSalidaHuesped = fechaRealSalidaHuesped;
    }

    /**
     * @return the eliminarPop
     */
    public boolean isEliminarPop() {
        return eliminarPop;
    }

    /**
     * @param eliminarPop the eliminarPop to set
     */
    public void setEliminarPop(boolean eliminarPop) {
        this.eliminarPop = eliminarPop;
    }

    /**
     * @return the subirArchivoPop
     */
    public boolean isSubirArchivoPop() {
        return subirArchivoPop;
    }

    /**
     * @param subirArchivoPop the subirArchivoPop to set
     */
    public void setSubirArchivoPop(boolean subirArchivoPop) {
        this.subirArchivoPop = subirArchivoPop;
    }

    /**
     * @return the idMotivo
     */
    public int getIdMotivo() {
        return idMotivo;
    }

    /**
     * @param idMotivo the idMotivo to set
     */
    public void setIdMotivo(int idMotivo) {
        this.idMotivo = idMotivo;
    }

    /**
     * @return the sgMotivo
     */
    public SgMotivo getSgMotivo() {
        return sgMotivo;
    }

    /**
     * @param sgMotivo the sgMotivo to set
     */
    public void setSgMotivo(SgMotivo sgMotivo) {
        this.sgMotivo = sgMotivo;
    }

    /**
     * @return the solicitaPop
     */
    public boolean isSolicitaPop() {
        return solicitaPop;
    }

    /**
     * @param solicitaPop the solicitaPop to set
     */
    public void setSolicitaPop(boolean solicitaPop) {
        this.solicitaPop = solicitaPop;
    }

    /**
     * @return the fechaSalidaPropuesta
     */
    public Date getFechaSalidaPropuesta() {
        return fechaSalidaPropuesta;
    }

    /**
     * @param fechaSalidaPropuesta the fechaSalidaPropuesta to set
     */
    public void setFechaSalidaPropuesta(Date fechaSalidaPropuesta) {
        this.fechaSalidaPropuesta = fechaSalidaPropuesta;
    }

    /**
     * @return the sugerenciaFechaSalidaHuesped
     */
    public String getSugerenciaFechaSalidaHuesped() {
        return sugerenciaFechaSalidaHuesped;
    }

    /**
     * @param sugerenciaFechaSalidaHuesped the sugerenciaFechaSalidaHuesped to
     * set
     */
    public void setSugerenciaFechaSalidaHuesped(String sugerenciaFechaSalidaHuesped) {
        this.sugerenciaFechaSalidaHuesped = sugerenciaFechaSalidaHuesped;
    }

    /**
     * @return the tipoEspecifico
     */
    public SgTipoEspecifico getTipoEspecifico() {
        return tipoEspecifico;
    }

    /**
     * @param tipoEspecifico the tipoEspecifico to set
     */
    public void setTipoEspecifico(SgTipoEspecifico tipoEspecifico) {
        this.tipoEspecifico = tipoEspecifico;
    }

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /**
     * @return the disabledAux
     */
    public boolean isDisabledAux() {
        return disabledAux;
    }

    /**
     * @param disabledAux the disabledAux to set
     */
    public void setDisabledAux(boolean disabledAux) {
        this.disabledAux = disabledAux;
    }

    /**
     * @return the staffListSelectItem
     */
    public List<SelectItem> getStaffListSelectItem() {
        return staffListSelectItem;
    }

    /**
     * @param staffListSelectItem the staffListSelectItem to set
     */
    public void setStaffListSelectItem(List<SelectItem> staffListSelectItem) {
        this.staffListSelectItem = staffListSelectItem;
    }

    /**
     * @return the invitado
     */
    public String getInvitado() {
        return invitado;
    }

    /**
     * @param invitado the invitado to set
     */
    public void setInvitado(String invitado) {
        this.invitado = invitado;
    }

    /**
     * @return the sgInvitado
     */
    public SgInvitado getSgInvitado() {
        return sgInvitado;
    }

    /**
     * @param sgInvitado the sgInvitado to set
     */
    public void setSgInvitado(SgInvitado sgInvitado) {
        this.sgInvitado = sgInvitado;
    }

    /**
     * @return the listaSelectItemInvitado
     */
    public List<SelectItem> getListaSelectItemInvitado() {
        return listaSelectItemInvitado;
    }

    /**
     * @param listaSelectItemInvitado the listaSelectItemInvitado to set
     */
    public void setListaSelectItemInvitado(List<SelectItem> listaSelectItemInvitado) {
        this.listaSelectItemInvitado = listaSelectItemInvitado;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

    /**
     * @return the idOficinaExcluida
     */
    public int getIdOficinaExcluida() {
        return idOficinaExcluida;
    }

    /**
     * @param idOficinaExcluida the idOficinaExcluida to set
     */
    public void setIdOficinaExcluida(int idOficinaExcluida) {
        this.idOficinaExcluida = idOficinaExcluida;
    }

    /**
     * @return the generaCartaPop
     */
    public boolean isGeneraCartaPop() {
        return generaCartaPop;
    }

    /**
     * @param generaCartaPop the generaCartaPop to set
     */
    public void setGeneraCartaPop(boolean generaCartaPop) {
        this.generaCartaPop = generaCartaPop;
    }

    /**
     * @return the serviciosHotelFacturaEmpresa
     */
    public List<SgHotelTipoEspecificoVo> getServiciosHotelFacturaEmpresa() {
        return serviciosHotelFacturaEmpresa;
    }

    /**
     * @param serviciosHotelFacturaEmpresa the serviciosHotelFacturaEmpresa to
     * set
     */
    public void setServiciosHotelFacturaEmpresa(List<SgHotelTipoEspecificoVo> serviciosHotelFacturaEmpresa) {
        this.serviciosHotelFacturaEmpresa = serviciosHotelFacturaEmpresa;
    }

    /**
     * @return the gerenciaCapacitacion
     */
    public Gerencia getGerenciaCapacitacion() {
        return gerenciaCapacitacion;
    }

    /**
     * @param gerenciaCapacitacion the gerenciaCapacitacion to set
     */
    public void setGerenciaCapacitacion(Gerencia gerenciaCapacitacion) {
        this.gerenciaCapacitacion = gerenciaCapacitacion;
    }

    /**
     * @return the sgOficina
     */
    public SgOficina getSgOficina() {
        return sgOficina;
    }

    /**
     * @param sgOficina the sgOficina to set
     */
    public void setSgOficina(SgOficina sgOficina) {
        this.sgOficina = sgOficina;
    }

//    /**
//     * @return the sgMotivoSelectItem
//     */
//    public List<SelectItem> getSgMotivoSelectItem() {
//        return sgMotivoSelectItem;
//    }
//
//    /**
//     * @param sgMotivoSelectItem the sgMotivoSelectItem to set
//     */
//    public void setSgMotivoSelectItem(List<SelectItem> sgMotivoSelectItem) {
//        this.sgMotivoSelectItem = sgMotivoSelectItem;
//    }
//
//    /**
//     * @return the gerenciaSelectItem
//     */
//    public List<SelectItem> getGerenciaSelectItem() {
//        return gerenciaSelectItem;
//    }
//
//    /**
//     * @param gerenciaSelectItem the gerenciaSelectItem to set
//     */
//    public void setGerenciaSelectItem(List<SelectItem> gerenciaSelectItem) {
//        this.gerenciaSelectItem = gerenciaSelectItem;
//    }
//
//    /**
//     * @return the sgOficinaSelectItem
//     */
//    public List<SelectItem> getSgOficinaSelectItem() {
//        return sgOficinaSelectItem;
//    }
//
//    /**
//     * @param sgOficinaSelectItem the sgOficinaSelectItem to set
//     */
//    public void setSgOficinaSelectItem(List<SelectItem> sgOficinaSelectItem) {
//        this.sgOficinaSelectItem = sgOficinaSelectItem;
//    }
    /**
     * @return the sgSolicitudEstanciaVo
     */
    public SgSolicitudEstanciaVo getSgSolicitudEstanciaVo() {
        return sgSolicitudEstanciaVo;
    }

    /**
     * @param sgSolicitudEstanciaVo the sgSolicitudEstanciaVo to set
     */
    public void setSgSolicitudEstanciaVo(SgSolicitudEstanciaVo sgSolicitudEstanciaVo) {
        this.sgSolicitudEstanciaVo = sgSolicitudEstanciaVo;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    /**
     * @return the listaEstancia
     */
    public DataModel getListaEstancia() {
        return listaEstancia;
    }

    /**
     * @param listaEstancia the listaEstancia to set
     */
    public void setListaEstancia(DataModel listaEstancia) {
        this.listaEstancia = listaEstancia;
    }

    /**
     * @return the fechaIngresoHuesped
     */
    public Date getFechaIngresoHuesped() {
        return fechaIngresoHuesped;
    }

    /**
     * @param fechaIngresoHuesped the fechaIngresoHuesped to set
     */
    public void setFechaIngresoHuesped(Date fechaIngresoHuesped) {
        this.fechaIngresoHuesped = fechaIngresoHuesped;
    }

    /**
     * @return the fechaSalidaHuesped
     */
    public Date getFechaSalidaHuesped() {
        return fechaSalidaHuesped;
    }

    /**
     * @param fechaSalidaHuesped the fechaSalidaHuesped to set
     */
    public void setFechaSalidaHuesped(Date fechaSalidaHuesped) {
        this.fechaSalidaHuesped = fechaSalidaHuesped;
    }

    /**
     * @return the idInVitado
     */
    public int getIdInVitado() {
        return idInVitado;
    }

    /**
     * @param idInVitado the idInVitado to set
     */
    public void setIdInVitado(int idInVitado) {
        this.idInVitado = idInVitado;
    }

    /**
     * @return the conCorreo
     */
    public boolean isConCorreo() {
        return conCorreo;
    }

    /**
     * @param conCorreo the conCorreo to set
     */
    public void setConCorreo(boolean conCorreo) {
        this.conCorreo = conCorreo;
    }

}
