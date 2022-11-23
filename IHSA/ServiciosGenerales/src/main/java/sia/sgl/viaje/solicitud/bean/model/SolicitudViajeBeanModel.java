/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.solicitud.bean.model;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.fechas.asueto.SiDiasAsueto;
import sia.modelo.Gerencia;
import sia.modelo.SgAerolinea;
import sia.modelo.SgDetalleItinerario;
import sia.modelo.SgEmpresa;
import sia.modelo.SgInvitado;
import sia.modelo.SgItinerario;
import sia.modelo.SgLugar;
import sia.modelo.SgMotivo;
import sia.modelo.SgOficina;
import sia.modelo.SgRutaTerrestre;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgTipoSolicitudViaje;
import sia.modelo.SgUbicacion;
import sia.modelo.SgViajero;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.fecha.asueto.impl.SiDiasAsuetoImpl;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.DetalleItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.HistorialItinerarioVO;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.RolTipoSolicitudVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.UsuarioRolGerenciaVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.MotivoVo;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.sgl.vo.SiCiudadVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgEmpresaImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgMotivoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgTipoSolicitudViajeImpl;
import sia.servicios.sgl.impl.SgViajeCiudadImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.vehiculo.impl.SgAsignarVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgAerolineaImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaCiudadImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgLugarImpl;
import sia.servicios.sgl.viaje.impl.SgRolTipoSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgUbicacionImpl;
import sia.servicios.sgl.viaje.impl.SgUsuarioRolGerenciaImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
/*
 * @Named @ConversationScoped
 */
@Named(value = "solicitudViajeBeanModel")
@ViewScoped
public class SolicitudViajeBeanModel implements Serializable {

    /**
     * @return the rutaTerrestre
     */
    public SgRutaTerrestre getRutaTerrestre() {
        return rutaTerrestre;
    }

    /**
     * @param rutaTerrestre the rutaTerrestre to set
     */
    public void setRutaTerrestre(SgRutaTerrestre rutaTerrestre) {
        this.rutaTerrestre = rutaTerrestre;
    }

    //Sistema
    @Inject
    private Sesion sesion;
    //
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgMotivoImpl sgMotivoImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeImpl;
    @Inject
    private SgTipoSolicitudViajeImpl sgTipoSolicitudViajeImpl;
    @Inject
    private SiCiudadImpl siCiudadImpl;
    @Inject
    private SgItinerarioImpl sgItinerarioImpl;
    @Inject
    private SgViajeCiudadImpl viajeCiudadImpl;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreImpl;
    @Inject
    private SgUsuarioRolGerenciaImpl sgUsuarioRolGerenciaImpl;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @Inject
    private SgRolTipoSolicitudViajeImpl sgRolTipoSolicitudViajeImpl;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoImpl;
    @Inject
    private SgViajeroImpl sgViajeroImpl;
    @Inject
    private SgDetalleRutaCiudadImpl sgDetalleRutaCiudadImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SgInvitadoImpl sgInvitadoImpl;
    @Inject
    private SgLugarImpl sgLugarImpl;
    @Inject
    private SgAsignarVehiculoImpl asignarVehiculoImpl;
    @Inject
    private SgVehiculoImpl sgVehiculoImpl;
    @Inject
    private SgEmpresaImpl sgEmpresaImpl;
    @Inject
    private SgEstatusAprobacionImpl estatusAprobacionService;
    @Inject
    private SgDetalleItinerarioImpl detalleItinerarioImpl;
    @Inject
    private SgAerolineaImpl sgAerolineaImpl;
    @Inject
    private SgUbicacionImpl ubicacionReomte;
    @Inject
    private ApCampoImpl apCampoImpl;
    @Inject
    private CompaniaImpl companiaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl campoUsuarioRhPuestoImpl;
    @Inject
    private SiDiasAsuetoImpl asuetoImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;

    //Entidades
    private Gerencia gerencia;
    private SgOficina sgOficina;
    private SolicitudViajeVO solicitudViajeVO;
    private SemaforoVo semaforoVo;
    //Clases
    private SiCiudadVO siCiudadVOOrigen;
    private SiCiudadVO siCiudadVODestino;
    private VehiculoVO vehiculoVO;
    private String optionViaje = "TERRESTRE";
    private String optionPropia = "propia";
    private String optionEstancia = "S";
    private String optionRangoSiCiudadOrigen;
    private String optionRangoSiCiudadDestino;
    private String observacion;
    private String cadena = "false"; //tambien se utiliza para tomar el comando para saber si una solicitud de viaje es sencilla o con regreso
    private String mensaje = "";
    private String nombre;
    private String operacion = Constantes.insertar;
    private String tipoSolicitud;
    private Date fechaSalida; //se cambian atipo date
    private Date fechaRegreso;
    private Date fechaSalida2; //se agregaro 2 nuevos para poder añadir el itinerario  
    private Date fechaRegreso2;
    private String nombreRol;
    private String origen = "Origen...";
    private String destino = "Destino...";
    private String ListaEmpleados;
    private String EmpleadoAdd;
    private String ListaInvitados;
    private int addInvitado;
    private String url = "";
    private String newInvitado = "";
    private String codigos = "";
    private String newTelefono = "";
    private String VehiculoActual = "";
    private String direccion = "";
    private String hotelSugerido = "";
    private ViajeroVO temporal;
    private CampoUsuarioPuestoVo campoUsuarioPuestoVo;
    private String motivoJustifica = "";
    private SgRutaTerrestre rutaTerrestre;
    private String mostrarRuta = "none";

    //Colecciones
    private List<SgOficina> sgOficinaList = Collections.EMPTY_LIST; //Oficinas de Destino
    private List<SgOficina> sgOficinaListAux = Collections.EMPTY_LIST; //Oficinas de Origen
    private List<MotivoVo> sgMotivoList = Collections.EMPTY_LIST;
    private List<SgTipoSolicitudViaje> sgTipoSolicitudViajeList = Collections.EMPTY_LIST;
    private List<ViajeroVO> listViajeroVO = new ArrayList<ViajeroVO>();
    private List<ViajeroVO> listViajeroBajarVO = new ArrayList<ViajeroVO>();
    private List<VehiculoVO> listVehiculoVO = new ArrayList<VehiculoVO>();
    private List<GerenciaVo> gerenciaList = Collections.EMPTY_LIST;
    private List<SelectItem> siCiudadOrigenSelectItem;
    private List<SelectItem> siCiudadDestinoSelectItem;
    private List<SelectItem> listaDestinoRuta;
    private List listaCasosIncumplidos;
    private List<List<Object[]>> listaOrigenes;
    private List<List<Object[]>> listaDestinos;
    private List<Object[]> listaCiudades;
    private List<Object[]> listaEmpleadosActivos;
    private List<Object[]> listInvitados;
    private List<Object[]> listEmpresas;
    private List<Object[]> listVehiculos;
    private List<SolicitudViajeVO> listSolicitudesVo = new ArrayList<>();
    private List<SolicitudViajeVO> listSolicitudesVoAereas = new ArrayList<>();
    private List<SolicitudViajeVO> listSolicitudesVoCancelar = new ArrayList<>();
    private List<Integer> jus = new ArrayList<Integer>();
    private List<SelectItem> ubicacion = new ArrayList<SelectItem>();
    private List<SelectItem> listEmpresaByUser = new ArrayList<>();
    private DataModel dataModelViajeros;
    private DataModel dataModelOficina;
    private DataModel dataModelOficinaDestino;
    private DataModel dataModelCiudad;
    private List<SiDiasAsueto> listDias = new ArrayList<>();

    private DataModel actulizarViajeros;
    //Primitivos
    private boolean viajeAereo;
    private boolean withEstancia;
    private boolean sencillo;
    private boolean asistenteDireccion;
    private boolean gerenteCapacitacion;
    private boolean panelSV = true;
    private boolean tabEmpOInv = true;
    private boolean justificaVisita;
    private boolean Justifica;
    private boolean viajaSolicitante = false;
    private boolean tieneVehiculo = false;
    private boolean conChofer = true;
    private boolean validaCheck = false;
    private boolean selectTodo = false;
    private boolean actualizar = true;
    private boolean editTel = false;
    private boolean confirVehiculo = false;
    private boolean cambiarVehiculo = false;
    private int horaSalida;
    private int horaRegreso;
    private int minutoSalida;
    private int minutoRegreso;
    private int horaSalida2;
    private int horaRegreso2;
    private int minutoSalida2;
    private int minutoRegreso2;
    private int idOficinaOrigen = -1;
    private int idOficinaDestino = -1;
    private int idSgMotivo = -1;
    private int idSgTipoSolicitudViaje = -1;
    private int idGerencia = -1;
    private int idSiCiudadOrigen = -1;
    private int idSiCiudadDestino = -1;
    private int idDetinoRuta = Constantes.RUTA_TIPO_OFICINA;
    private int idDestino = 0;
    private int idVisito = 0;
    private InvitadoVO visita;
    private int idLugar;
    private int idAerolinia = 0;
    private int idAerolinia2 = 0;
    private int idUbicacion = -1;
    private int countSVT = 0;
    private int countSVA = 0;
    private int idCampoActual = -1;
    private String rfcEmpresaSeleccionada;
    private String empleado;
    private String invitado;

    public SolicitudViajeBeanModel() {
    }

    public void goToSolicitudesPorAprobar() {
        if (isActualizar()) { //se utiliza la opcion insertar para que el metodo solo se utilice cuando entra por primera vez.
            UtilLog4j.log.info(this, "goToSolicitudesPorAprobar()");
            mostrarSolicitudesByAprobar();
        }
    }

    public String convertirFechaString(Date fecha) {
        return siManejoFechaImpl.convertirFechaStringddMMyyyy(fecha);
    }

    public void endConversation() {
        clearVariables();
    }

    public void endConversationEditSgSolicitudViaje() {
        clearVariables();
    }

    public void inicioPopUpTrue(String llave) {
        sesion.getControladorPopups().put(llave, Boolean.TRUE);
    }

    public void inicioPopUpFalse(String llave) {
        sesion.getControladorPopups().put(llave, Boolean.FALSE);
    }

    public boolean validateDateIsAfterNow(Calendar fecha) {
        Calendar cAhora = Calendar.getInstance();
        log("SolicitudViajeBeanModel.validateDateIsAfterNow(): " + (this.siManejoFechaImpl.compare(fecha, cAhora, true) == 1 ? true : false));
        return (this.siManejoFechaImpl.compare(fecha, cAhora, true) == 1 ? true : false);
    }

    public boolean validateDateIsAfterYesterday(Calendar fecha) {
        log("SolicitudViajeBeanModel.validateDateIsAfterYesterday()");
        Calendar cYesterday = Calendar.getInstance();
        cYesterday.add(Calendar.DAY_OF_YEAR, -1);
        cYesterday = this.siManejoFechaImpl.cleanCalendar(cYesterday);
        log("SolicitudViajeBeanModel.validateDateIsAfterYesterday(): " + (this.siManejoFechaImpl.compare(fecha, cYesterday, false) == 1 ? true : false));
        return (this.siManejoFechaImpl.compare(fecha, cYesterday, false) == 1 ? true : false);
    }

    /**
     * Valida que 'fechaRegreso' sea mayor que 'fechaSalida'. No valida los
     * tiempos
     *
     * @param fechaRegreso
     * @param fechaSalida
     * @return - 'true' si la 'fechaSalida' es anterior a 'fechaRegreso'
     */
    public int validateFechaRegresoIsAfterOrEqualFechaSalida(Date fechaRegreso, Date fechaSalida) {
        return this.siManejoFechaImpl.compare(fechaRegreso, fechaSalida);
    }

    /**
     * Valida que 'fechaRegreso' sea mayor que 'fechaSalida'. Valida también los
     * tiempos
     *
     * @param fechaRegreso
     * @param fechaSalida
     * @return - 'true' si la 'fechaSalida' es anterior a 'fechaRegreso'
     */
    public boolean validateFechaRegresoIsAfterFechaSalidaWithTime(Calendar fechaRegreso, Calendar fechaSalida) {
        log("SolicitudViajeBeanModel.validateFechaRegresoIsAfterFechaSalidaWithTime(): " + (this.siManejoFechaImpl.compare(fechaRegreso, fechaSalida, true) == 1 ? true : false));
        return (this.siManejoFechaImpl.compare(fechaRegreso, fechaSalida, true) == 1 ? true : false);
    }

    /**
     * Valida que 'firstDate' sea mayor que 'secondDate'.
     *
     * @param firstDate
     * @param secondDate
     * @param withTime 'true' si se desea que se valide también el tiempo aparte
     * de las fechas
     * @return - 'true' si 'firstDate' es posterior a 'secondDate'
     */
    public boolean validateFirstDateIsAfterSecondDate(Calendar firstDate, Calendar secondDate, boolean withTime) {
        boolean firstDateIsAfterSecondDate = (this.siManejoFechaImpl.compare(firstDate, secondDate, withTime) == 1 ? true : false);
        log("validateFirstDateIsAfterSecondDate(): " + firstDateIsAfterSecondDate);
        return firstDateIsAfterSecondDate;
    }

    /**
     * Valida que 'firstDate' y 'secondDate' sean el mismo día
     *
     * @param firstDate
     * @param secondDate
     * @param withTime 'true' si se desea que se valide también el tiempo aparte
     * @return 'true' si 'firstDate' es igual a 'secondDate'
     */
    public boolean validateFirstDateAndSecondDateAreTheSameDay(Calendar firstDate, Calendar secondDate, boolean withTime) {
        boolean firstDateIsAfterSecondDate = (this.siManejoFechaImpl.compare(firstDate, secondDate, withTime) == 0 ? true : false);
        log("validateFirstDateAndSecondDateAreTheSameDay(): " + firstDateIsAfterSecondDate);
        return firstDateIsAfterSecondDate;
    }

    public boolean validateDateIsToday(Calendar fecha) {
        return this.siManejoFechaImpl.dayIsToday(fecha);
    }

    public boolean validateFechaSalidaViajeAereo(Calendar cFechaSalida, int idSgTipoSolicitudViaje) {
        log("SolicitudViajeBeanModel.validateFechaSalidaViajeAereo()");
        SgTipoSolicitudViaje sgTipoSolicitudViaje = this.sgTipoSolicitudViajeImpl.find(idSgTipoSolicitudViaje);

        int diasAnticipacion = sgTipoSolicitudViaje.getHorasAnticipacion() / 24;

        Calendar cHoy = Calendar.getInstance();
        cHoy.add(Calendar.DATE, diasAnticipacion);
        clearCalendar(cHoy);

        log("cHoy: " + cHoy.getTime());
        log("cFechaSalida: " + cFechaSalida.getTime());
        log("cHoy < cFechaSalida: " + (cFechaSalida.compareTo(cHoy) == -1 ? false : true));

        return (cHoy.compareTo(cFechaSalida) == -1 ? true : false);
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

    //----------------------------------------- INICIO SOLICITUD DE VIAJE BEAN ----------------------*/
    public List<SelectItem> traerOficina() {
        try {
            List<SelectItem> l = null;
            List lrg;
            SelectItem item;
            if (getOptionViaje().equals(Constantes.solicitudTerrestre)) {
                l = new ArrayList<SelectItem>();
                lrg = sgOficinaImpl.findByVistoBuenoList(true, false);
                for (Object object : lrg) {
                    SgOficina ovo = (SgOficina) object;
                    item = new SelectItem(ovo.getId(), ovo.getNombre());
                    l.add(item);
                }
            } else if (getOptionViaje().equals(Constantes.solicitudAereo)) {
                l = new ArrayList<SelectItem>();
                setIdOficinaOrigen(1);
                SgOficina ofi = findSgOficinaById(1);
                item = new SelectItem(ofi.getId(), ofi.getNombre());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    public void traerOficinaModal() {
        try {

            if (getOptionViaje().equals(Constantes.solicitudTerrestre)) {
                // setDataModelOficina(new ListDataModel(sgOficinaImpl.findByVistoBuenoList(true, false)));
                if (this.getRfcEmpresaSeleccionada() != null && !this.getRfcEmpresaSeleccionada().isEmpty()) {
                    setListaOrigenes(sgRutaTerrestreImpl.traerOigenJson(this.getRfcEmpresaSeleccionada()));
                }
                Gson gson = new Gson();
                JsonArray a = new JsonArray();
                List<Object[]> listaOficinas = getListaOrigenes().get(0);
                List<Object[]> listaCiudadesO = getListaOrigenes().get(1);

                for (Object[] o : listaOficinas) {
                    if (listaOficinas != null) {
                        JsonObject ob = new JsonObject();
                        ob.addProperty("value", o[0] != null ? (Integer) o[0] : 0);
                        ob.addProperty("label", o[1] != null ? (String) o[1] + "-Oficina" : "-");
                        ob.addProperty("type", "1");
                        a.add(ob);
                    }
                }
                for (Object[] o : listaCiudadesO) {
                    if (listaCiudadesO != null) {
                        JsonObject ob = new JsonObject();
                        ob.addProperty("value", o[0] != null ? (Integer) o[0] + 1000 : 0);
                        ob.addProperty("label", o[1] != null ? (String) o[1] + "-" + o[2] + "-" + o[3] : "-");
                        ob.addProperty("type", "2");
                        a.add(ob);
                    }
                }
                String origenes = gson.toJson(a);
                //  
                PrimeFaces.current().executeScript(";cargarDatos(" + origenes + ",'oficinasYCiudades','groupO','groupC');");
            }

        } catch (Exception e) {
            e.getStackTrace();

        }
    }

    public void inicializaValores() {
        if (sesion.getOficinaActual() != null) {
            setIdOficinaOrigen(sesion.getOficinaActual().getId());
        }
        setOptionViaje(Constantes.solicitudTerrestre);
        setNombreRol(sesion.getNombreRol());
        setIdOficinaDestino(-1);
        setSemaforoVo(new SemaforoVo());
        setOptionPropia(Constantes.OPCION_OTRO);
        setFechaRegreso(new Date());
        setFechaSalida(new Date());

        listaDestino();

    }

    public List<SelectItem> listaTipoSolicitud() {
        List<SelectItem> l = new ArrayList<>();
        List<RolTipoSolicitudVo> lrsol = null;
        if (getOptionViaje().equals(Constantes.solicitudTerrestre)) {
            log("Es terrestre  +  +  +  + + + + +  + + + + + + + + + ");
            lrsol = sgRolTipoSolicitudViajeImpl.traerTipoSolicitudPorRol(sesion.getIdRol(), Constantes.SOLICITUDES_TERRESTRE);
        } else if (getOptionViaje().equals(Constantes.solicitudAereo)) {
            log("Es aerea   - -  -- - -  - - - - - -- - - - - - ");
            lrsol = sgRolTipoSolicitudViajeImpl.traerTipoSolicitudPorRol(sesion.getIdRol(), Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA);
            log("lrsol: aerea  " + lrsol.size());
        }
        if (lrsol != null && !lrsol.isEmpty()) {
            if (lrsol.size() == 1) {
                int idTS = lrsol.get(0).getIdTipoSolicitud();
                setIdSgTipoSolicitudViaje(idTS);
                setTipoSolicitud(lrsol.get(0).getTipoSolicitud());
            } else {
                for (RolTipoSolicitudVo rolTipoSolicitudVo : lrsol) {
                    SelectItem item = new SelectItem(rolTipoSolicitudVo.getIdTipoSolicitud(), rolTipoSolicitudVo.getTipoSolicitud());
                    l.add(item);
                }
            }
        }
        return l;
    }

    public List<SelectItem> traerListaGerencia() {
        List<SelectItem> l = null;
        List lrg = null;
        if (sesion.getIdRol() == Constantes.ROL_GERENTE) { //Es gerente
            l = new ArrayList<SelectItem>();
            lrg = gerenciaImpl.getAllGerenciaByApCampoAndResponsable(1, sesion.getUsuario().getId(), "nombre", true, Boolean.TRUE, false);
            if (!lrg.isEmpty()) {
                if (lrg.size() == 1) {
                    GerenciaVo gvo = (GerenciaVo) lrg.get(0);
                    setIdGerencia(gvo.getId());
                    setNombre(gvo.getNombre());
                } else {
                    for (Object object : lrg) {
                        GerenciaVo gv = (GerenciaVo) object;
                        SelectItem item = new SelectItem(gv.getId(), gv.getNombre());
                        l.add(item);
                    }
                }
            }
        } else {
            lrg = sgUsuarioRolGerenciaImpl.traerGerenciaPorRol(sesion.getUsuario().getId(), sesion.getIdRol());
            l = new ArrayList<SelectItem>();
            if (!lrg.isEmpty()) {
                if (lrg.size() == 1) {
                    UsuarioRolGerenciaVo gvo = (UsuarioRolGerenciaVo) lrg.get(0);
                    setIdGerencia(gvo.getIdGerencia());
                    setNombre(gvo.getGerencia());
                } else {
                    for (Object object : lrg) {
                        UsuarioRolGerenciaVo usuarioRolGerenciaVo = (UsuarioRolGerenciaVo) object;
                        SelectItem item = new SelectItem(usuarioRolGerenciaVo.getIdGerencia(), usuarioRolGerenciaVo.getGerencia());
                        l.add(item);
                    }
                }
            } // verifica si la gerecia esta vacia
        }// No es gerente
        return l;
    }

    public void listaDestino() {
        try {
            String destinos = "";
            Gson gson = new Gson();
            JsonArray a = new JsonArray();
            List<Object[]> listaTerrestre = new ArrayList<>();
            List<Object[]> listaAerea = new ArrayList<>();
            if (getIdOficinaOrigen() < 1000) {
                if (this.getRfcEmpresaSeleccionada() != null && !this.getRfcEmpresaSeleccionada().isEmpty()) {
                    setListaDestinos(sgRutaTerrestreImpl.traerDestinosJson(getIdOficinaOrigen(), this.getRfcEmpresaSeleccionada()));
                }
                listaTerrestre = getListaDestinos().get(0);
                listaAerea = getListaDestinos().get(1);
                for (Object[] o : listaTerrestre) {
                    if (listaTerrestre != null) {
                        JsonObject ob = new JsonObject();
                        Date dmin = (Date) o[3];
                        Date dMax = (Date) o[4];
                        String hr = Constantes.FMT_hmm_a.format(dmin) + "hrs a " + Constantes.FMT_hmm_a.format(dMax) + "hrs";
                        ob.addProperty("value", o[0] != null ? (Integer) o[0] : 0);
                        ob.addProperty("type", "1");
                        ob.addProperty("horario", hr);
                        if (o[2].equals("a Oficina")) {
                            ob.addProperty("label", o[1] != null ? (String) o[1] + "-Oficina" : "-");
                        } else {
                            ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
                        }
                        a.add(ob);
                    }
                }
                //
                for (Object[] o : listaAerea) {
                    if (listaAerea != null) {
                        JsonObject ob = new JsonObject();
                        ob.addProperty("value", o[0] != null ? (Integer) o[0] + 1000 : 0);
                        ob.addProperty("label", o[1] != null ? (String) o[1] + "-" + o[2] + "-" + o[3] : "-");
                        ob.addProperty("type", "2");

                        a.add(ob);
                    }
                }
            } else {
                setIdSiCiudadOrigen(getIdOficinaOrigen() - 1000);
                setIdOficinaOrigen(Constantes.ID_OFICINA_TORRE_MARTEL);
                setIdDetinoRuta(Constantes.SOLICITUDES_AEREA);
                listaAerea = siCiudadImpl.traerCiudadJson(getIdSiCiudadOrigen());
                getListaDestinos().set(1, listaAerea);
                //
                for (Object[] o : listaAerea) {
                    if (listaAerea != null) {
                        JsonObject ob = new JsonObject();
                        ob.addProperty("value", o[0] != null ? (Integer) o[0] + 1000 : 0);
                        ob.addProperty("label", o[1] != null ? (String) o[1] + "-" + o[2] + "-" + o[3] : "-");
                        ob.addProperty("type", "2");
                        a.add(ob);
                    }
                }
            }
            destinos = gson.toJson(a);
            PrimeFaces.current().executeScript(";limpiarDataList();");
            PrimeFaces.current().executeScript(";cargarDatos(" + destinos + ",'destinos','groupDO','groupDC');");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public SemaforoVo buscarSemaforoActual() {
        if (getIdOficinaDestino() > -1) {
            setSemaforoVo(sgEstadoSemaforoImpl.estadoActual(getIdOficinaDestino()));

            return getSemaforoVo();
        }
        return null;

    }

    public void tiemposPropuestosViaje() {
        if (getOptionViaje().equals(Constantes.solicitudTerrestre)) {
            setHoraSalida(6);
            setMinutoSalida(0);
        } else {//(getOptionViaje().equals(Constantes.solicitudAereo))
            setHoraSalida(8);
            setMinutoSalida(30);
        }
        if (getCadena().equals(Constantes.redondo)) {
            setHoraRegreso(16);
            setMinutoRegreso(0);
        }
    }

    /*
     *
     * MLUIS -- -20/11/2013 @ option propia : propia <---> otro * master
     */
    public void saveSgSolicitudViajeTerrestre() throws SIAException {

        try {
            setIdGerencia(sesion.getUsuario().getGerencia().getId());
            Calendar fechaSalidaCompleta = Calendar.getInstance();
            fechaSalidaCompleta.setTime(getFechaSalida());
            fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, getHoraSalida());
            fechaSalidaCompleta.set(Calendar.MINUTE, getMinutoSalida());
            Date fs = fechaSalidaCompleta.getTime();
            setFechaSalida(fs);

            Date fr = null;

            if (getCadena().equals(Constantes.redondo)) {
                Date cFechaRegreso = getFechaRegreso();
                Calendar fechaRegresoCompleta = Calendar.getInstance();
                fechaRegresoCompleta.setTime(cFechaRegreso);
                fechaRegresoCompleta.set(Calendar.HOUR_OF_DAY, getHoraRegreso());
                fechaRegresoCompleta.set(Calendar.MINUTE, getMinutoRegreso());
                fr = fechaRegresoCompleta.getTime();
                setFechaRegreso(fr);
            }

            //setIdDetinoRuta(sgRutaTerrestreImpl.find(getIdDestino()).getSgTipoEspecifico().getId());
            int idSolicitudViaje = this.sgSolicitudViajeImpl.save(getIdSgTipoSolicitudViaje(), getIdGerencia(), getIdOficinaOrigen(), getIdDestino(),
                    getIdSgMotivo(), getObservacion(), fs, fr,
                    getIdSiCiudadOrigen(), getIdSiCiudadDestino(), Constantes.OPCION_PROPIA.equals(getOptionPropia()), Constantes.FALSE,
                    this.sesion.getUsuario().getId(), sesion.getIdRol(), Constantes.redondo.equals(getCadena()), getIdDetinoRuta(), 0,
                    isconChofer() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            if (idSolicitudViaje > 0) {
                setPanelSV(Constantes.FALSE);
                setSolicitudViajeVO(sgSolicitudViajeImpl.buscarPorId(idSolicitudViaje, Constantes.NO_ELIMINADO, Constantes.CERO));
                setListViajeroVO(sgViajeroImpl.getAllViajerosList(idSolicitudViaje));
                //setDataModelViajeros(new ListDataModel());
                usuariosActivos(Constantes.CERO);
                // inicializarlistInvitados(); se inicializa des de el principio lo cual hace que no sea nesesario aqui.
                setJustifica(justificarSolicitud());
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

    public boolean modificarSolicitudViaje() {
        boolean v = false;
        try {
            // PrimeFaces.current().executeScript(";limpiarDataListEmpJust();");
            //PrimeFaces.current().executeScript(";limpiarDataListEmp();");
            //PrimeFaces.current().executeScript(";limpiarDataListVehiculo();");
            setListaEmpleadosActivos(null);
            usuariosActivos(Constantes.CERO);
            inicializarlistInvitados(true);
            //vehiculosJson();
            if (isViajaSolicitante()) {
                setVehiculoVO(asignarVehiculoImpl.traerVehiculobyResponsable(sesion.getUsuario().getId()));
                if (getVehiculoVO() != null) {
                    setVehiculoActual(getVehiculoVO().getMarca() + " " + getVehiculoVO().getModelo() + " " + getVehiculoVO().getNumeroPlaca());
                }
                if (getVehiculoVO() != null) {
                    setTieneVehiculo(Constantes.TRUE);
                    // setconChofer(Constantes.FALSE);
                    setListVehiculoVO(sgVehiculoImpl.traerVehiculoPorUsuarioAsignado(sesion.getUsuario().getId()));
                } else {
                    setTieneVehiculo(Constantes.FALSE);
                }
            }

            Date fr = null;
            Calendar fechaSalidaCompleta = Calendar.getInstance();
            fechaSalidaCompleta.setTime(getFechaSalida());
            fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, getHoraSalida());
            fechaSalidaCompleta.set(Calendar.MINUTE, getMinutoSalida());
            Date fs = fechaSalidaCompleta.getTime();
            setFechaSalida(fs);

            if (getCadena().equals(Constantes.redondo)) {
                Calendar fechaRegresoCompleta = Calendar.getInstance();
                fechaRegresoCompleta.setTime(getFechaRegreso());
                fechaRegresoCompleta.set(Calendar.HOUR_OF_DAY, getHoraRegreso());
                fechaRegresoCompleta.set(Calendar.MINUTE, getMinutoRegreso());
                fr = fechaRegresoCompleta.getTime();
                setFechaRegreso(fr);
            }
            log("Id solicitud: " + getSolicitudViajeVO().getIdSolicitud());
            List<SgDetalleRutaTerrestreVo> ldrt = null;
            if (getIdDestino() > 1000) {
                setIdSiCiudadDestino(getIdDestino() - 1000);
                setIdDetinoRuta(Constantes.SOLICITUDES_AEREA);
                if (getIdSgMotivo() == 18) {
                    setIdSgTipoSolicitudViaje(3);
                } else {
                    setIdSgTipoSolicitudViaje(7);
                }
            } else { //lo carga por default con el valor de empleado
                setIdSgTipoSolicitudViaje(Constantes.SOLICITUDES_TERRESTRE);
                setIdDetinoRuta(Constantes.SOLICITUDES_TERRESTRE);
            }

            int campoSelec = apCampoImpl.campoByUserAndCompani(getRfcEmpresaSeleccionada(), sesion.getUsuario().getId());

            if (campoSelec <= 0) {
                setIdCampoActual(Constantes.AP_CAMPO_DEFAULT);
            } else {
                setIdCampoActual(campoSelec);
            }
            setCampoUsuarioPuestoVo(campoUsuarioRhPuestoImpl.findByUsuarioCampo(getIdCampoActual(), sesion.getUsuario().getId()));
            campoUsuarioPuestoVo.setIdUsuario(sesion.getUsuario().getId());
            campoUsuarioPuestoVo.setUsuario(sesion.getUsuario().getNombre());
            setIdGerencia(getCampoUsuarioPuestoVo().getIdGerencia());// RECUPERAR SDE APCAMPOUSUARIRHPUESTO POR 
            v = sgSolicitudViajeImpl.modificarSolicitud(getIdSgTipoSolicitudViaje(), getIdGerencia(), getIdOficinaOrigen(), getIdDestino(),
                    getIdSgMotivo(), getObservacion(), fs, fr,
                    getIdSiCiudadOrigen(), getIdSiCiudadDestino(), getOptionPropia(), isWithEstancia(),
                    this.sesion.getUsuario().getId(), sesion.getIdRol(), getCadena(), getIdDetinoRuta(),
                    getSolicitudViajeVO().getIdSolicitud(), ldrt);
            List<ViajeroVO> lv = sgViajeroImpl.getAllViajerosList(getSolicitudViajeVO().getIdSolicitud());
            setListViajeroVO(lv);
            //setDataModelViajeros(new ListDataModel(lv));
            setSolicitudViajeVO(sgSolicitudViajeImpl.buscarPorId(getSolicitudViajeVO().getIdSolicitud(), Constantes.NO_ELIMINADO, Constantes.CERO));
            getSolicitudViajeVO().setIdGerencia(getIdGerencia());
            getSolicitudViajeVO().setGerencia(getCampoUsuarioPuestoVo().getGerencia());
            if (getSolicitudViajeVO().getIdSgTipoEspecifico() == Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
                ItinerarioCompletoVo itVO = sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(getSolicitudViajeVO().getIdSolicitud(), Constantes.TRUE, Constantes.FALSE, "it.ID");
                setOrigen(itVO.getNombreCiudadOrigen());
                setDestino(itVO.getNombreCiudadDestino());
            } else if (getSolicitudViajeVO().getIdOficinaDestino() > 0) {
                setOrigen(getSolicitudViajeVO().getOrigen());
                setDestino(getSolicitudViajeVO().getDestino());
            } else {
                setOrigen(getSolicitudViajeVO().getOrigen());
                setDestino(siCiudadImpl.find(sgDetalleRutaCiudadImpl.buscarDetalleRutaCiudadDestinoPorRuta(getIdDestino()).getIdCiudad()).getNombre());
            }

            setJustifica(justificarSolicitud());//ubicarlo bien antes de terminar
            ///
            if (getSolicitudViajeVO().getIdGerencia() != getIdGerencia()) {
                ///Remover todos los viajeros cargados a la solicitud
                for (ViajeroVO viajeroVO : lv) {
                    try {
                        sgViajeroImpl.delete(viajeroVO.getId(), sesion.getUsuario().getId(), "");
                    } catch (SIAException ex) {
                        Logger.getLogger(SolicitudViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(SolicitudViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        } catch (Exception ex) {
            v = false;
            log("ex: modificar fecha: " + ex.getMessage());
        }
        return v;
    }

    public void inicializaValorerModificar() {
        setOperacion(Constantes.modificar);
        listaDestino();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        setIdGerencia(getSolicitudViajeVO().getIdGerencia());
        setIdSgTipoSolicitudViaje(getSolicitudViajeVO().getIdSgTipoSolicitudViaje());
        setIdOficinaOrigen(getSolicitudViajeVO().getIdOficinaOrigen());
        setNombreRol(sesion.getNombreRol());
        //   setIdOficinaDestino(getSolicitudViajeVO().getIdOficinaDestino()); // buscar la oficina destino
        setFechaSalida(getSolicitudViajeVO().getFechaSalida());
        setOptionViaje(getSolicitudViajeVO().getIdSgTipoEspecifico() == 2 ? Constantes.solicitudTerrestre : Constantes.solicitudAereo);
        if (getSolicitudViajeVO().getIdSgTipoEspecifico() == Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE) {
            String hs = siManejoFechaImpl.convertirHoraStringHHmmss(getSolicitudViajeVO().getHoraSalida());
            String nhs[] = hs.split(":");
            setHoraSalida(Integer.parseInt(nhs[0]));
            setMinutoSalida(Integer.parseInt(nhs[1]));

            setSemaforoVo(buscarSemaforoActual());
            setOptionViaje(Constantes.solicitudTerrestre);
            setIdSgMotivo(getSolicitudViajeVO().getIdSgMotivo());
            //Busca en viaje-ciudad
            //ViajeDestinoVo viajeDestinoVo = viajeCiudadImpl.findDestinoSolicitudViaje(getSolicitudViajeVO().getIdSolicitud());
            if (getSemaforoVo() != null) {
                if (getSemaforoVo().getRutaTipoEspecifico() == Constantes.RUTA_TIPO_CIUDAD) {
                    setIdDetinoRuta(Constantes.RUTA_TIPO_CIUDAD);
                } else if (getSemaforoVo().getRutaTipoEspecifico() == Constantes.RUTA_TIPO_OFICINA) {
                    setIdDetinoRuta(Constantes.RUTA_TIPO_OFICINA);
                }
                setIdOficinaDestino(getSemaforoVo().getIdRuta());
            } else {
                setIdOficinaDestino(-1);
                setIdDetinoRuta(Constantes.RUTA_TIPO_OFICINA);
            }

        } else if (getSolicitudViajeVO().getIdSgTipoEspecifico() == Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA) {
            setIdDetinoRuta(-1);
            setIdSiCiudadOrigen(getSolicitudViajeVO().getIdSiCiudadOrigen());
            setIdSiCiudadDestino(getSolicitudViajeVO().getIdSiCiudadDestino());
            setOptionViaje(Constantes.solicitudAereo);
            int letraOrigen = (getSolicitudViajeVO().getNombreSiCiudadOrigen().substring(0, 1)).charAt(0);
            int letraDestino = getSolicitudViajeVO().getDestino().substring(0, 1).charAt(0);
            if (letraOrigen <= 70) { //70 = F
                setOptionRangoSiCiudadOrigen("AF");
                setSiCiudadOrigenSelectItem(getAllSiCiudadSelectItemByRange("A", "F"));
            } else if (letraOrigen <= 77) { //77 = M
                setOptionRangoSiCiudadOrigen("GM");
                setSiCiudadOrigenSelectItem(getAllSiCiudadSelectItemByRange("G", "M"));
            } else if (letraOrigen <= 82) { //82 = R
                setOptionRangoSiCiudadOrigen("NR");
                setSiCiudadOrigenSelectItem(getAllSiCiudadSelectItemByRange("N", "R"));
            } else if (letraOrigen <= 90) { //90 = Z
                setOptionRangoSiCiudadOrigen("SZ");
                setSiCiudadOrigenSelectItem(getAllSiCiudadSelectItemByRange("S", "Z"));
            }

            if (letraDestino <= 70) { //70 = F
                setOptionRangoSiCiudadDestino("AF");
                setSiCiudadDestinoSelectItem(getAllSiCiudadSelectItemByRange("A", "F"));
            } else if (letraDestino <= 77) { //77 = M
                setOptionRangoSiCiudadDestino("GM");
                setSiCiudadDestinoSelectItem(getAllSiCiudadSelectItemByRange("G", "M"));
            } else if (letraDestino <= 82) { //82 = R
                setOptionRangoSiCiudadDestino("NR");
                setSiCiudadDestinoSelectItem(getAllSiCiudadSelectItemByRange("N", "R"));
            } else if (letraDestino <= 90) { //90 = Z
                setOptionRangoSiCiudadDestino("SZ");
                setSiCiudadDestinoSelectItem(getAllSiCiudadSelectItemByRange("S", "Z"));
            }
        }
        setCadena(getSolicitudViajeVO().isRedondo() ? Constantes.redondo : Constantes.sencillo);
        if (getCadena().equals(Constantes.redondo)) {
            String hr = siManejoFechaImpl.convertirHoraStringHHmmss(getSolicitudViajeVO().getHoraRegreso());
            String nhr[] = hr.split(":");
            setFechaRegreso(getSolicitudViajeVO().getFechaRegreso());
            setHoraRegreso(Integer.parseInt(nhr[0]));
            setMinutoRegreso(Integer.parseInt(nhr[1]));
        }
        setObservacion(getSolicitudViajeVO().getObservacion());
        setOptionPropia("otro");
        setOptionEstancia("N");
        setObservacion(getSolicitudViajeVO().getObservacion());
        //Busca el semaofor y la url
    }

    public void clearVariables() {
        setSgOficinaList(Collections.EMPTY_LIST);
        setSgMotivoList(Collections.EMPTY_LIST);
        setSgTipoSolicitudViajeList(Collections.EMPTY_LIST);
        setGerenciaList(Collections.EMPTY_LIST);
        setSiCiudadOrigenSelectItem(null);
        setSiCiudadDestinoSelectItem(null);
        setGerencia(null);
        setSgOficina(null);
        setSiCiudadVOOrigen(null);
        setSiCiudadVODestino(null);
        setObservacion(null);
        setOperacion(null);

        setAsistenteDireccion(false);
        setWithEstancia(false);

        setIdOficinaOrigen(-1);
        setIdOficinaDestino(-1);
        setIdSgMotivo(-1);
        setIdSgTipoSolicitudViaje(-1);
        setIdGerencia(-1);
        setIdSiCiudadOrigen(-1);
        setIdSiCiudadDestino(-1);
        setSemaforoVo(null);
    }

    public void setOptionViajeDefault() {
        setOptionViaje("T");
        log("INIT..................................");
        setTiemposPropuestosViajeTerrestre();
    }

    public void setOptionEstanciaDefault() {
        setOptionEstancia("N");
        setWithEstancia(false);
    }

    public void setOptionRangoSiCiudadOrigenAndDestino() {
        setOptionRangoSiCiudadOrigen("AF");
        setOptionRangoSiCiudadDestino("AF");
    }

    public void setOficinaOrigenDefaultForViajeAereo() {
        setSgOficina(findSgOficinaById(1));
    }

    public void setTiemposPropuestosViajeTerrestre() {
        log("asiganando tiempos propuestos para SVT");
//        setFechaSalida(c);
        setHoraSalida(6);
        setMinutoSalida(0);
//        setFechaRegreso(c);
        setHoraRegreso(16);
        setMinutoRegreso(0);
    }

    public void setTiemposPropuestosViajeAereo(int idSgTipoSolicitudViaje) {
        if (idSgTipoSolicitudViaje == -1) {
            log("asignando tiempos propuestos para SVA default");

//            setFechaSalida(cFechaSalida);
            setHoraSalida(6);
            setMinutoSalida(0);
//            setFechaRegreso(cFechaRegreso);
            setHoraRegreso(16);
            setMinutoRegreso(0);
        } else {
            log("asignando tiempos propuestos para SVA de SgTipoSolicitud: " + idSgTipoSolicitudViaje);
            SgTipoSolicitudViaje sgTipoSolicitudViaje = this.sgTipoSolicitudViajeImpl.find(idSgTipoSolicitudViaje);

            int horasAnticipacion = sgTipoSolicitudViaje.getHorasAnticipacion();
            int diasAnticipacion = horasAnticipacion / 24;
            log("dias anticipación: " + diasAnticipacion);

        }

        log("fechaSalida: " + getFechaSalida());
        log("fechaRegreso: " + getFechaRegreso());
    }

    public Gerencia findGerenciaById(int id) {
        return this.gerenciaImpl.find(id);
    }

    public SgSolicitudViaje findSgSolicitudViajeById(int id) {
        return this.sgSolicitudViajeImpl.find(id);
    }

    public SgTipoSolicitudViaje findSgTipoSolicitudViajeById(int id) {
        return this.sgTipoSolicitudViajeImpl.find(id);
    }

    public SgOficina findSgOficinaById(int id) {
        return this.sgOficinaImpl.find(id);
    }

    public void searchSiCiudadesForSgSolicitudViaje() throws SIAException {
        ItinerarioCompletoVo vo = sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(getSolicitudViajeVO().getIdSolicitud(), true, false, "id");
        SgItinerario sgItinerarioTmp = sgItinerarioImpl.find(vo.getId());

        SiCiudadVO siCiudadVOOrigenTmp = new SiCiudadVO();
        siCiudadVOOrigenTmp.setId(sgItinerarioTmp.getSiCiudadOrigen().getId());
        siCiudadVOOrigenTmp.setNombre(sgItinerarioTmp.getSiCiudadOrigen().getNombre());
        siCiudadVOOrigenTmp.setNombreSiEstado(sgItinerarioTmp.getSiCiudadOrigen().getSiEstado().getNombre());
        siCiudadVOOrigenTmp.setNombreSiPais(sgItinerarioTmp.getSiCiudadOrigen().getSiPais().getNombre());
        this.siCiudadVOOrigen = siCiudadVOOrigenTmp;
        this.cadena = siCiudadVOOrigenTmp.getNombre();

        SiCiudadVO siCiudadVODestinoTmp = new SiCiudadVO();
        siCiudadVODestinoTmp.setId(sgItinerarioTmp.getSiCiudadDestino().getId());
        siCiudadVODestinoTmp.setNombre(sgItinerarioTmp.getSiCiudadDestino().getNombre());
        siCiudadVODestinoTmp.setNombreSiEstado(sgItinerarioTmp.getSiCiudadDestino().getSiEstado().getNombre());
        siCiudadVODestinoTmp.setNombreSiPais(sgItinerarioTmp.getSiCiudadDestino().getSiPais().getNombre());
        this.siCiudadVODestino = siCiudadVODestinoTmp;
        this.mensaje = siCiudadVODestinoTmp.getNombre();
    }

    public Gerencia getGerenciaByParam(String param) {
        log("getGerenciaByParam.param: " + param);
        Gerencia g = null;

        if ("G".equals(param) || "GR".equals(param)) {
            g = this.gerenciaImpl.find(11); //Dirección General
        } else if ("T".equals(param) || "TR".equals(param)) {
            g = this.gerenciaImpl.find(62); //Dirección Técnica
        } else if ("S".equals(param) || "SR".equals(param)) {
            g = this.gerenciaImpl.find(63); //Subdirección General
        } else if ("CA".equals(param) || "CR".equals(param)) {
            g = this.gerenciaImpl.find(29); //Perforación
        } else if ("PR".equals(param)) {
            g = this.gerenciaImpl.find(33); //Servicios Generales
        } else if ("OPRC".equals(param)) {
            g = this.gerenciaImpl.find(55); //Capacitación
        }
        return g;
    }

    public List<GerenciaVo> getAllGerenciaListByParam(String param) {
        log("getAllGerenciaListByParam.param: " + param);
        List<GerenciaVo> list = null;

        if ("PG".equals(param)) {
            list = this.gerenciaImpl.getAllGerenciaByApCampoAndResponsable(1, this.sesion.getUsuario().getId(), "nombre", true, null, false);
        } else if ("PA".equals(param) && this.sesion.isUsuarioInSessionGerente()) {
            list = this.gerenciaImpl.getAllGerenciaByApCampoAndResponsable(1, this.sesion.getUsuario().getId(), "nombre", true, null, false);
        } else if ("OPR".equals(param) || "OPA".equals(param)) {
            //      list = this.gerenciaImpl.getAllGerenciaByApCompaniaAndApCampo("IHI070320FI3", 1, "nombre", true, true, false);
            list = this.gerenciaImpl.getAllGerenciaByApCompaniaAndApCampo("IHI070320FI3", 1, "nombre", true, null, false);
        }
        return list;
    }

    public List<SgOficina> getAllSgOficinaList() {
        return this.sgOficinaImpl.findAll(false);
    }

    public List<SgOficina> getOficinasReynosa() {
        List<SgOficina> list = new ArrayList<SgOficina>();
        list.add(this.sgOficinaImpl.find(2));
        list.add(this.sgOficinaImpl.find(4));
        return list;
    }

    public List<MotivoVo> getAllSgMotivoList() {
        return sgMotivoImpl.traerTodosMotivo();
    }

    /**
     * @return
     */
    public List<SgTipoSolicitudViaje> getAllSgTipoSolicitudViajeList() {
        List<SgTipoSolicitudViaje> list = new ArrayList();

        if (Constantes.ROL_SISTEMA_EMPLEADO.equals(this.sesion.getRol())) {
            if (!isAsistenteDireccion() && !isGerenteCapacitacion()) {
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(5)); //Empresa (rifa)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
            } else if (isAsistenteDireccion()) {
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
            } else if (isGerenteCapacitacion()) {
                if ("OPRC".equals(getOptionPropia())) {
                    list.add(this.sgTipoSolicitudViajeImpl.find(6)); //Capacitación
                } else {
                    list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                    list.add(this.sgTipoSolicitudViajeImpl.find(5)); //Empresa (rifa)
                    list.add(this.sgTipoSolicitudViajeImpl.find(6)); //Capacitación
                    list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
                }
            }
        } else if (Constantes.ROL_SGL_ADMINISTRA.equals(this.sesion.getRol())) {
            list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
            list.add(this.sgTipoSolicitudViajeImpl.find(5)); //Empresa (rifa)
            list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
        } else if (Constantes.ROL_SISTEMA_GERENTE.equals(this.sesion.getRol())) {
            if ("PG".equals(getOptionPropia())) { //Propia Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(5)); //Empresa (rifa)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales (nuevo ingreso)
            } else if ("OG".equals(getOptionPropia())) { //Otra persona de su Gerencia Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales (nuevo ingreso)
            } else if ("OPG".equals(getOptionPropia())) { //Otra persona externa Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales (nuevo ingreso)
            }
        } else if (Constantes.ROL_SGL_ANALISTA.equals(this.sesion.getRol())) {
            if ("PA".equals(getOptionPropia()) || "OPA".equals(getOptionPropia())) {
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(5)); //Empresa (rifa)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
            } else if ("CA".equals(getOptionPropia())) {
                list.add(this.sgTipoSolicitudViajeImpl.find(4)); //Cambio de Guardia (Perforación/Company Man)
            }
        } else if (Constantes.ROL_SGL_RESPONSABLE.equals(this.sesion.getRol())) {
            if ("PR".equals(getOptionPropia())) { //Propia Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(5)); //Empresa (rifa)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
            } else if ("GR".equals(getOptionPropia())) { //Dirección General Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
            } else if ("TR".equals(getOptionPropia())) { //Dirección Técnica Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
            } else if ("SR".equals(getOptionPropia())) { //Subdirección Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales
            } else if ("CR".equals(getOptionPropia())) { //Companyman Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(4)); //Motivos Laborales (perforación/Company Man)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales (nuevo ingreso)
            } else if ("OR".equals(getOptionPropia())) { //Otra persona de su Gerencia Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales (nuevo ingreso)
            } else if ("OPR".equals(getOptionPropia())) { //Otra persona externa Responsable
                list.add(this.sgTipoSolicitudViajeImpl.find(3)); //Vacaciones (expatriados)
                list.add(this.sgTipoSolicitudViajeImpl.find(7)); //Motivos Laborales (nuevo ingreso)
            }
        }

        return list;
    }

    public List<SelectItem> getAllSiCiudadSelectItem(String cadena) {
        if (getOptionViaje().equals("AEREO")) {
            List<SiCiudadVO> list = this.siCiudadImpl.findAllNative("nombre", true, false);
            List<SelectItem> items = new ArrayList<SelectItem>();

            for (SiCiudadVO vo : list) {
                if (vo.getId() != null) {
                    String cadenaVo = vo.getNombre().toLowerCase();
                    cadena = cadena.toLowerCase();

                    if (cadenaVo.startsWith(cadena)) {
                        log(vo.toString());
                        SelectItem si = new SelectItem(vo, (vo.getNombre() + "|" + vo.getNombreSiEstado() + "|" + vo.getNombreSiPais()));
                        items.add(si);
                    }
                }
            }
            return items;
        }
        return null;
    }

    public List<SelectItem> getAllSiCiudadSelectItemByRange(String startFilter, String endFilter) {
        if (getOptionViaje().equals("AEREO")) {
            List<SiCiudadVO> list = this.siCiudadImpl.findAllByRangeNative(startFilter, endFilter, "nombre", true, false);
            List<SelectItem> items = new ArrayList<SelectItem>();

            for (SiCiudadVO vo : list) {
                SelectItem si = new SelectItem(vo.getId(), (vo.getNombre() + "|" + vo.getNombreSiEstado() + "|" + vo.getNombreSiPais()));
                items.add(si);
            }
            return items;
        }
        return null;
    }

    public List<SgDetalleRutaTerrestreVo> getAllSgDetalleRutaTerrestreBySgOficinaOrigen(int idSgOficinaOrigen) {
        return this.sgDetalleRutaTerrestreImpl.getAllSgDetalleRutaTerrestreBySgOficinaOrigen(idSgOficinaOrigen);
    }

    public void saveSgMotivo() throws ExistingItemException {
        SgMotivo sgMotivo = this.sgMotivoImpl.saveRO(getNombre(), this.sesion.getUsuario().getId());
        setSgMotivoList(getAllSgMotivoList());
        setIdSgMotivo(sgMotivo.getId());
    }

    /**
     * @return the viajeAereo
     */
    public boolean isViajeAereo() {
        return viajeAereo;
    }

    /**
     * @param viajeAereo the viajeAereo to set
     */
    public void setViajeAereo(boolean viajeAereo) {
        this.viajeAereo = viajeAereo;
    }

    /**
     * @return the optionViaje
     */
    public String getOptionViaje() {
        return optionViaje;
    }

    /**
     * @param optionViaje the optionViaje to set
     */
    public void setOptionViaje(String optionViaje) {
        this.optionViaje = optionViaje;
    }

    /**
     * @return the asistenteDireccion
     */
    public boolean isAsistenteDireccion() {
        return asistenteDireccion;
    }

    /**
     * @param asistenteDireccion the asistenteDireccion to set
     */
    public void setAsistenteDireccion(boolean asistenteDireccion) {
        this.asistenteDireccion = asistenteDireccion;
    }

    /**
     * @return the horaSalida
     */
    public int getHoraSalida() {
        return horaSalida;
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(int horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * @return the horaRegreso
     */
    public int getHoraRegreso() {
        return horaRegreso;
    }

    /**
     * @param horaRegreso the horaRegreso to set
     */
    public void setHoraRegreso(int horaRegreso) {
        this.horaRegreso = horaRegreso;
    }

    /**
     * @return the minutoSalida
     */
    public int getMinutoSalida() {
        return minutoSalida;
    }

    /**
     * @param minutoSalida the minutoSalida to set
     */
    public void setMinutoSalida(int minutoSalida) {
        this.minutoSalida = minutoSalida;
    }

    /**
     * @return the minutoRegreso
     */
    public int getMinutoRegreso() {
        return minutoRegreso;
    }

    /**
     * @param minutoRegreso the minutoRegreso to set
     */
    public void setMinutoRegreso(int minutoRegreso) {
        this.minutoRegreso = minutoRegreso;
    }

    /**
     * @return the idOficinaOrigen
     */
    public int getIdOficinaOrigen() {
        return idOficinaOrigen;
    }

    /**
     * @param idOficinaOrigen the idOficinaOrigen to set
     */
    public void setIdOficinaOrigen(int idOficinaOrigen) {
        this.idOficinaOrigen = idOficinaOrigen;
    }

    /**
     * @return the idOficinaDestino
     */
    public int getIdOficinaDestino() {
        return idOficinaDestino;
    }

    /**
     * @param idOficinaDestino the idOficinaDestino to set
     */
    public void setIdOficinaDestino(int idOficinaDestino) {
        this.idOficinaDestino = idOficinaDestino;
    }

    /**
     * @return the idSgMotivo
     */
    public int getIdSgMotivo() {
        return idSgMotivo;
    }

    /**
     * @param idSgMotivo the idSgMotivo to set
     */
    public void setIdSgMotivo(int idSgMotivo) {
        this.idSgMotivo = idSgMotivo;
    }

    /**
     * @return the sgOficinaList
     */
    public List<SgOficina> getSgOficinaList() {
        return sgOficinaList;
    }

    /**
     * @param sgOficinaList the sgOficinaList to set
     */
    public void setSgOficinaList(List<SgOficina> sgOficinaList) {
        this.sgOficinaList = sgOficinaList;
    }

    /**
     * @return the sgMotivoList
     */
    public List<MotivoVo> getSgMotivoList() {
        return sgMotivoList;
    }

    /**
     * @param sgMotivoList the sgMotivoList to set
     */
    public void setSgMotivoList(List<MotivoVo> sgMotivoList) {
        this.sgMotivoList = sgMotivoList;
    }

    /**
     * @return the fechaSalida
     */
    public Date getFechaSalida() {
        return fechaSalida;
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    /**
     * @return the fechaRegreso
     */
    public Date getFechaRegreso() {
        return fechaRegreso;
    }

    /**
     * @param fechaRegreso the fechaRegreso to set
     */
    public void setFechaRegreso(Date fechaRegreso) {
        this.fechaRegreso = fechaRegreso;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /**
     * @return the idSgTipoSolicitudViaje
     */
    public int getIdSgTipoSolicitudViaje() {
        return idSgTipoSolicitudViaje;
    }

    /**
     * @param idSgTipoSolicitudViaje the idSgTipoSolicitudViaje to set
     */
    public void setIdSgTipoSolicitudViaje(int idSgTipoSolicitudViaje) {
        this.idSgTipoSolicitudViaje = idSgTipoSolicitudViaje;
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
     * @return the sgTipoSolicitudViajeList
     */
    public List<SgTipoSolicitudViaje> getSgTipoSolicitudViajeList() {
        return sgTipoSolicitudViajeList;
    }

    /**
     * @param sgTipoSolicitudViajeList the sgTipoSolicitudViajeList to set
     */
    public void setSgTipoSolicitudViajeList(List<SgTipoSolicitudViaje> sgTipoSolicitudViajeList) {
        this.sgTipoSolicitudViajeList = sgTipoSolicitudViajeList;
    }

    /**
     * @return the siCiudadVOOrigen
     */
    public SiCiudadVO getSiCiudadVOOrigen() {
        return siCiudadVOOrigen;
    }

    /**
     * @param siCiudadVOOrigen the siCiudadVOOrigen to set
     */
    public void setSiCiudadVOOrigen(SiCiudadVO siCiudadVOOrigen) {
        this.siCiudadVOOrigen = siCiudadVOOrigen;
    }

    /**
     * @return the siCiudadVODestino
     */
    public SiCiudadVO getSiCiudadVODestino() {
        return siCiudadVODestino;
    }

    /**
     * @param siCiudadVODestino the siCiudadVODestino to set
     */
    public void setSiCiudadVODestino(SiCiudadVO siCiudadVODestino) {
        this.siCiudadVODestino = siCiudadVODestino;
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
        return cadena;
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
        this.cadena = cadena;
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
     * @return the siCiudadOrigenSelectItem
     */
    public List<SelectItem> getSiCiudadOrigenSelectItem() {
        return siCiudadOrigenSelectItem;
    }

    /**
     * @param siCiudadOrigenSelectItem the siCiudadOrigenSelectItem to set
     */
    public void setSiCiudadOrigenSelectItem(List<SelectItem> siCiudadOrigenSelectItem) {
        this.siCiudadOrigenSelectItem = siCiudadOrigenSelectItem;
    }

    /**
     * @return the siCiudadDestinoSelectItem
     */
    public List<SelectItem> getSiCiudadDestinoSelectItem() {
        return siCiudadDestinoSelectItem;
    }

    /**
     * @param siCiudadDestinoSelectItem the siCiudadDestinoSelectItem to set
     */
    public void setSiCiudadDestinoSelectItem(List<SelectItem> siCiudadDestinoSelectItem) {
        this.siCiudadDestinoSelectItem = siCiudadDestinoSelectItem;
    }

    /**
     * @return the optionPropia
     */
    public String getOptionPropia() {
        return optionPropia;
    }

    /**
     * @param optionPropia the optionPropia to set
     */
    public void setOptionPropia(String optionPropia) {
        this.optionPropia = optionPropia;
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
     * @return the gerenciaList
     */
    public List<GerenciaVo> getGerenciaList() {
        return gerenciaList;
    }

    /**
     * @param gerenciaList the gerenciaList to set
     */
    public void setGerenciaList(List<GerenciaVo> gerenciaList) {
        this.gerenciaList = gerenciaList;
    }

    /**
     * @return the withEstancia
     */
    public boolean isWithEstancia() {
        return withEstancia;
    }

    /**
     * @param withEstancia the withEstancia to set
     */
    public void setWithEstancia(boolean withEstancia) {
        this.withEstancia = withEstancia;
    }

    /**
     * @return the gerenteCapacitacion
     */
    public boolean isGerenteCapacitacion() {
        return gerenteCapacitacion;
    }

    /**
     * @param gerenteCapacitacion the gerenteCapacitacion to set
     */
    public void setGerenteCapacitacion(boolean gerenteCapacitacion) {
        this.gerenteCapacitacion = gerenteCapacitacion;
    }

    /**
     * @return the optionEstancia
     */
    public String getOptionEstancia() {
        return optionEstancia;
    }

    /**
     * @param optionEstancia the optionEstancia to set
     */
    public void setOptionEstancia(String optionEstancia) {
        this.optionEstancia = optionEstancia;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    /**
     * @return the optionRangoSiCiudadOrigen
     */
    public String getOptionRangoSiCiudadOrigen() {
        return optionRangoSiCiudadOrigen;
    }

    /**
     * @param optionRangoSiCiudadOrigen the optionRangoSiCiudadOrigen to set
     */
    public void setOptionRangoSiCiudadOrigen(String optionRangoSiCiudadOrigen) {
        this.optionRangoSiCiudadOrigen = optionRangoSiCiudadOrigen;
    }

    /**
     * @return the optionRangoSiCiudadDestino
     */
    public String getOptionRangoSiCiudadDestino() {
        return optionRangoSiCiudadDestino;
    }

    /**
     * @param optionRangoSiCiudadDestino the optionRangoSiCiudadDestino to set
     */
    public void setOptionRangoSiCiudadDestino(String optionRangoSiCiudadDestino) {
        this.optionRangoSiCiudadDestino = optionRangoSiCiudadDestino;
    }

    /**
     * @return the idSiCiudadOrigen
     */
    public int getIdSiCiudadOrigen() {
        return idSiCiudadOrigen;
    }

    /**
     * @param idSiCiudadOrigen the idSiCiudadOrigen to set
     */
    public void setIdSiCiudadOrigen(int idSiCiudadOrigen) {
        this.idSiCiudadOrigen = idSiCiudadOrigen;
    }

    /**
     * @return the idSiCiudadDestino
     */
    public int getIdSiCiudadDestino() {
        return idSiCiudadDestino;
    }

    /**
     * @param idSiCiudadDestino the idSiCiudadDestino to set
     */
    public void setIdSiCiudadDestino(int idSiCiudadDestino) {
        this.idSiCiudadDestino = idSiCiudadDestino;
    }

    /**
     * @return the sgOficinaListAux
     */
    public List<SgOficina> getSgOficinaListAux() {
        return sgOficinaListAux;
    }

    /**
     * @param sgOficinaListAux the sgOficinaListAux to set
     */
    public void setSgOficinaListAux(List<SgOficina> sgOficinaListAux) {
        this.sgOficinaListAux = sgOficinaListAux;
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * @return the sencillo
     */
    public boolean isSencillo() {
        return sencillo;
    }

    /**
     * @param sencillo the sencillo to set
     */
    public void setSencillo(boolean sencillo) {
        this.sencillo = sencillo;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the semaforoVo
     */
    public SemaforoVo getSemaforoVo() {
        return semaforoVo;
    }

    /**
     * @param semaforoVo the semaforoVo to set
     */
    public void setSemaforoVo(SemaforoVo semaforoVo) {
        this.semaforoVo = semaforoVo;
    }

    /**
     * @return the tipoSolicitud
     */
    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * @param tipoSolicitud the tipoSolicitud to set
     */
    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * @return the solicitudViajeVO
     */
    public SolicitudViajeVO getSolicitudViajeVO() {
        return solicitudViajeVO;
    }

    /**
     * @param solicitudViajeVO the solicitudViajeVO to set
     */
    public void setSolicitudViajeVO(SolicitudViajeVO solicitudViajeVO) {
        this.solicitudViajeVO = solicitudViajeVO;
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
     * @return the idDetinoRuta
     */
    public int getIdDetinoRuta() {
        return idDetinoRuta;
    }

    /**
     * @param idDetinoRuta the idDetinoRuta to set
     */
    public void setIdDetinoRuta(int idDetinoRuta) {
        this.idDetinoRuta = idDetinoRuta;
    }

    /**
     * @return the listaDestinoRuta
     */
    public List<SelectItem> getListaDestinoRuta() {
        return listaDestinoRuta;
    }

    /**
     * @param listaDestinoRuta the listaDestinoRuta to set
     */
    public void setListaDestinoRuta(List<SelectItem> listaDestinoRuta) {
        this.listaDestinoRuta = listaDestinoRuta;
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    /**
     * @return the panelSV
     */
    public boolean isPanelSV() {
        return panelSV;
    }

    /**
     * @param panelSV the panelSV to set
     */
    public void setPanelSV(boolean panelSV) {
        this.panelSV = panelSV;
    }

    /**
     * @return the dataModelViajeros
     */
    public DataModel getDataModelViajeros() {
        return dataModelViajeros;
    }

    /**
     * @param dataModelViajeros the dataModelViajeros to set
     */
    public void setDataModelViajeros(DataModel dataModelViajeros) {
        this.dataModelViajeros = dataModelViajeros;
    }

    public <T> List<T> dataModelAList(DataModel m) {
        return (List<T>) m.getWrappedData();
    }

    /**
     * @return the origen
     */
    public String getOrigen() {
        return origen;
    }

    /**
     * @param origen the origen to set
     */
    public void setOrigen(String origen) {
        this.origen = origen;
    }

    /**
     * @return the destino
     */
    public String getDestino() {
        return destino;
    }

    /**
     * @param destino the destino to set
     */
    public void setDestino(String destino) {
        this.destino = destino;
    }

    /**
     * @return the dataModelOficina
     */
    public DataModel getDataModelOficina() {
        return dataModelOficina;
    }

    /**
     * @param dataModelOficina the dataModelOficina to set
     */
    public void setDataModelOficina(DataModel dataModelOficina) {
        this.dataModelOficina = dataModelOficina;
    }

    /**
     * @return the dataModelOficinaDestino
     */
    public DataModel getDataModelOficinaDestino() {
        return dataModelOficinaDestino;
    }

    /**
     * @param dataModelOficinaDestino the dataModelOficinaDestino to set
     */
    public void setDataModelOficinaDestino(DataModel dataModelOficinaDestino) {
        this.dataModelOficinaDestino = dataModelOficinaDestino;
    }

    /**
     * @return the dataModelCiudad
     */
    public DataModel getDataModelCiudad() {
        return dataModelCiudad;
    }

    /**
     * @param dataModelCiudad the dataModelCiudad to set
     */
    public void setDataModelCiudad(DataModel dataModelCiudad) {
        this.dataModelCiudad = dataModelCiudad;
    }

    /**
     * @return the idDestino
     */
    public int getIdDestino() {
        return idDestino;
    }

    /**
     * @param idDestino the idDestino to set
     */
    public void setIdDestino(int idDestino) {
        this.idDestino = idDestino;
    }

    public void usuariosActivos(int idGerencia) {
        String usuarios = "";
        JsonArray a = new JsonArray();
        Gson gson = new Gson();
        if (getListaEmpleadosActivos() == null || getListaEmpleadosActivos().isEmpty()) {
            setListaEmpleadosActivos(usuariosJson(idGerencia));
            for (Object[] o : getListaEmpleadosActivos()) {

                JsonObject ob = new JsonObject();
                ob.addProperty("value", o[0] != null ? (String) o[0] : "-");
                ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
                ob.addProperty("type", o[2] != null ? (String) o[2] : "-");
                a.add(ob);
            }
            usuarios = gson.toJson(a);
            PrimeFaces.current().executeScript(";cargarDatosInvOrEmp(" + usuarios + ",'empleadosList');");
            if (idGerencia == 0) {
                PrimeFaces.current().executeScript(";cargarDatosInvOrEmp(" + usuarios + ",'empleadosListJust');");
            }

            setListaEmpleados(usuarios);
        }

    }

    /**
     * @return the tabEmpOInv
     */
    public boolean isTabEmpOInv() {
        return tabEmpOInv;
    }

    /**
     * @param tabEmpOInv the tabEmpOInv to set
     */
    public void setTabEmpOInv(boolean tabEmpOInv) {
        this.tabEmpOInv = tabEmpOInv;
    }

    /**
     * @return the ListaEmpleados
     */
    public String getListaEmpleados() {
        return ListaEmpleados;
    }

    /**
     * @param ListaEmpleados the dataListaEmpleados to set
     */
    public void setListaEmpleados(String ListaEmpleados) {
        this.ListaEmpleados = ListaEmpleados;
    }

    /**
     * @return the dataListaInvitados
     */
    public String getListaInvitados() {
        return ListaInvitados;
    }

    /**
     * @param ListaInvitados the dataListaInvitados to set
     */
    public void setListaInvitados(String ListaInvitados) {
        this.ListaInvitados = ListaInvitados;
    }

    /*  public void addNewViajero(String idUsuario, boolean agregagdo, int idInvitado) {
     int idSolicitudViaje = getSolicitudViajeVO().getIdSolicitud();
     SgViajero sgViajero = new SgViajero();
     if(!idUsuario.equals("")){
     sgViajero.setUsuario(this.usuarioImpl.find(idUsuario));
     } else {
     idUsuario = sesion.getUsuario().getId();
     }
     sgViajero.setSgSolicitudViaje(sgSolicitudViajeImpl.find(idSolicitudViaje));
     sgViajero.setEstancia(Constantes.BOOLEAN_TRUE);

     if(idInvitado>0){
     sgViajero.setSgInvitado(sgInvitadoImpl.find(idInvitado));
     }

     try {
     this.sgViajeroImpl.save(sgViajero, idUsuario);
     siLogImpl.create(sgViajero.getClass().getName(), sgViajero.getId(), 1, idUsuario, null, sgViajero.toString());
     List <ViajeroVO> viajerosActuales =dataModelAList(getDataModelViajeros());
     List <ViajeroVO> viajerosNew = sgViajeroImpl.getAllViajerosList(idSolicitudViaje);
     if(viajerosActuales.size() != viajerosNew.size()){
     for(ViajeroVO von : viajerosNew){
     for(ViajeroVO voa :viajerosActuales){
     if(von.getId().equals(voa.getId())){
     von.setAgregado(voa.isAgregado());
     von.setEstancia(voa.getEstancia());
     von.setEstanciaB(voa.isEstanciaB());
     von.setSelected(voa.isSelected());
     }
     }
     }
     setDataModelViajeros(new ListDataModel(viajerosNew));
     }
     if(idInvitado == 0){
     setDataListaEmpleados(null);
     } else {
     setDataListaInvitados(null);
     }


     } catch (Exception ex) {
     Logger.getLogger(SolicitudViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
     }

     }*/
    public String actualizarListaViajeros() throws Exception {
        SgViajero viajeroActual = new SgViajero();
        Usuario u = null;
        SgInvitado i = null;
        boolean est = false;
        String regresa = "";
        if (getListViajeroVO() != null) {
            for (ViajeroVO vo : getListViajeroVO()) {
                if (vo.getId() != null && vo.getId() > 0) {
                    viajeroActual = sgViajeroImpl.find(vo.getId());
                    viajeroActual.setRedondo(getCadena().equals(Constantes.redondo) ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);

                    if (vo.isEstanciaB() && vo.isSinEstancia()) {
                        regresa = "Solo debe de seleccionar una casilla";
                        break;
                    } else if (!vo.isEstanciaB() && !vo.isSinEstancia()) {
                        regresa = "Debe de selleccionar si desea estancia";
                        break;
                    } else {
                        viajeroActual.setEstancia(vo.isEstanciaB());

                    }
                    viajeroActual.setEliminado(Constantes.BOOLEAN_FALSE);
                    sgViajeroImpl.edit(viajeroActual);
                    if (vo.getIdUsuario().equals(sesion.getUsuario().getId())) {
                        setViajaSolicitante(Constantes.TRUE);
                    }
                    if (vo.isEstanciaB()) {
                        est = vo.isEstanciaB();
                    }
                } else {
                    viajeroActual = new SgViajero();
                    viajeroActual.setEstancia(vo.isEstanciaB() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                    viajeroActual.setRedondo(getCadena().equals(Constantes.redondo) ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                    viajeroActual.setSgInvitado((vo.getIdInvitado() != null && vo.getIdInvitado() > 0) ? sgInvitadoImpl.find(vo.getIdInvitado()) : null);
                    viajeroActual.setSgSolicitudViaje(sgSolicitudViajeImpl.find(getSolicitudViajeVO().getIdSolicitud()));
                    viajeroActual.setUsuario((vo.getIdUsuario() != null && !vo.getIdUsuario().isEmpty()) ? usuarioImpl.find(vo.getIdUsuario()) : null);
                    sgViajeroImpl.crearViajero(viajeroActual, sesion.getUsuario().getId());
                    if (vo.getIdUsuario() != null && !vo.getIdUsuario().isEmpty() && vo.getIdUsuario().equals(sesion.getUsuario().getId())) {
                        setViajaSolicitante(Constantes.TRUE);
                    }
                    if (vo.isEstanciaB()) {
                        est = vo.isEstanciaB();
                    }
                }
                setWithEstancia(est);
                if (vo.getIdUsuario() != null && !vo.getIdUsuario().isEmpty()) {
                    u = usuarioImpl.find(vo.getIdUsuario());
                    if (u != null) {
                        u.setTelefono(vo.getTelefono());
                        usuarioImpl.edit(u);
                    }
                } else {
                    i = sgInvitadoImpl.find(vo.getIdInvitado());
                    i.setTelefono(vo.getTelefono());
                    sgInvitadoImpl.edit(i);
                }
            }

        }
        if (getListViajeroBajarVO() != null) {
            for (ViajeroVO vo : getListViajeroBajarVO()) {
                if (vo.getId() != null && vo.getId() > 0) {
                    viajeroActual = sgViajeroImpl.find(vo.getId());
                    viajeroActual.setEliminado(Constantes.BOOLEAN_TRUE);
                    sgViajeroImpl.edit(viajeroActual);
                    if (vo.getIdUsuario().equals(sesion.getUsuario().getId())) {
                        setViajaSolicitante(Constantes.FALSE);
                    }
                }
            }
        }
        return regresa;
    }

    /**
     * @return the actulizarViajeros
     */
    public DataModel getActulizarViajeros() {
        return actulizarViajeros;
    }

    /**
     * @param actulizarViajeros the actulizarViajeros to set
     */
    public void setActulizarViajeros(DataModel actulizarViajeros) {
        this.actulizarViajeros = actulizarViajeros;
    }

    public void inicializarlistInvitados(boolean todo) {
        String invitados = "";
        JsonArray a = new JsonArray();
        Gson gson = new Gson();
        if (getListaInvitados() == null || getListInvitados().isEmpty()) {
            setListInvitados(llenarInvitadoJson());
            for (Object[] o : getListInvitados()) {

                JsonObject ob = new JsonObject();
                ob.addProperty("value", o[0] != null ? (Integer) o[0] : 0);
                ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
                ob.addProperty("type", o[2] != null ? (String) o[2] : "-");
                a.add(ob);
            }
            invitados = gson.toJson(a);
            PrimeFaces.current().executeScript(";cargarDatosInvOrEmp(" + invitados + ",'invitadosList');");
            if (todo) {
                PrimeFaces.current().executeScript(";cargarDatosInvOrEmp(" + invitados + ",'invitadosListJust');");
            }

            setListaInvitados(invitados);
        }
    }

    public boolean justificarSolicitud() {
        boolean devuelve = false;
        if (getSolicitudViajeVO() != null) {
            if (getSolicitudViajeVO().getIdSgTipoSolicitudViaje() == Constantes.SOLICITUDES_TERRESTRE) {
                int totalViajeros = getListViajeroVO().size();
                String literal = "";

                List<Integer> casosRetraso = new ArrayList<>();
                if (totalViajeros > 0) {
                    if (siManejoFechaImpl.compare(getSolicitudViajeVO().getFechaSalida(), new Date()) >= 0) { //valida fecha salida mayor o igual a hoy
                        if (siManejoFechaImpl.dayIsToday(getSolicitudViajeVO().getFechaSalida())) { //mismo dia
                            literal = "sgl.mensaje.solicitud.viaje.mismo.dia";
                            //request = true;//Pasa por DG
                            casosRetraso.add(Constantes.DIA_ANTERIOR_TIPO_ESPECIFICO);  // id del tipo especifico para mismo dia
                        }
                        if (siManejoFechaImpl.validaSolicitaHoyParaMananaDespuesHora(getSolicitudViajeVO().getFechaSalida(), getSolicitudViajeVO().getIdSgTipoSolicitudViaje())) { // Salida mañana y es despues de la hora maxima
                            literal = "sgl.mensaje.solicitud.viaje.manana.despues.hora";
                            casosRetraso.add(Constantes.DIA_ANTERIOR_TIPO_ESPECIFICO);  // id del tipo especifico para mismo dia
                            //request = true;
                        }
                        SgRutaTerrestre rt = sgRutaTerrestreImpl.find(getSolicitudViajeVO().getIdRutaTerrestre());
                        if (getSolicitudViajeVO().isRedondo()) {
                            if (siManejoFechaImpl.finSemana(getSolicitudViajeVO().getFechaSalida())
                                    || siManejoFechaImpl.finSemana(getSolicitudViajeVO().getFechaRegreso())) { // sabado y domingo
                                literal = "sgl.mensaje.solicitud.viaje.fin.semana";
                                //request = true;
                                casosRetraso.add(Constantes.FIN_SEMANA_TIPO_ESPECIFICO);  // id del tipo especifico para fin semana
                            }
                            if (asuetoImpl.esDiaFestivo(getSolicitudViajeVO().getFechaSalida()) || asuetoImpl.esDiaFestivo(getSolicitudViajeVO().getFechaRegreso())) { //valida día festivo
                                casosRetraso.add(Constantes.DIA_FESTIVO);
                            }

                            if (siManejoFechaImpl.salidaProximoLunes(new Date(), getSolicitudViajeVO().getFechaSalida(), getSolicitudViajeVO().getIdEstatus())) {//validar si hoy es viernes y la solicitud sale el proximo LUNES
                                casosRetraso.add(Constantes.PROXIMO_LUNES);
                            }
                            if (rt != null) {
                                if (siManejoFechaImpl.validaHoraMinima(getSolicitudViajeVO().getHoraSalida(), rt.getHoraMinimaRuta())
                                        || siManejoFechaImpl.validaHoraMaxima(getSolicitudViajeVO().getHoraSalida(), rt.getHoraMaximaRuta())) {
                                    //no cumple el horario
                                    literal = "sgl.mensaje.solicitud.viaje.estado.semaforo";
                                    casosRetraso.add(Constantes.SEMAFORO_HORARIO_TIPO_ESPECIFICO);
                                }
                                if (siManejoFechaImpl.validaHoraMaxima(getSolicitudViajeVO().getHoraRegreso(), rt.getHoraMaximaRuta())
                                        || siManejoFechaImpl.validaHoraMinima(getSolicitudViajeVO().getHoraRegreso(), rt.getHoraMinimaRuta())) {
                                    //no cumple el horario
                                    literal = "sgl.mensaje.solicitud.viaje.estado.semaforo";
                                    casosRetraso.add(Constantes.SEMAFORO_HORARIO_TIPO_ESPECIFICO);
                                }
                            }

                        } else {
                            if (siManejoFechaImpl.finSemana(getSolicitudViajeVO().getFechaSalida())) { // sabado y domingo
                                literal = "sgl.mensaje.solicitud.viaje.fin.semana";
                                //request = true;
                                casosRetraso.add(Constantes.FIN_SEMANA_TIPO_ESPECIFICO);  // id del tipo especifico para fin semana
                            }
                            if (asuetoImpl.esDiaFestivo(getSolicitudViajeVO().getFechaSalida())) {
                                casosRetraso.add(Constantes.FIN_SEMANA_TIPO_ESPECIFICO);
                            }

                            if (rt != null) {
                                if (siManejoFechaImpl.validaHoraMinima(getSolicitudViajeVO().getHoraSalida(), rt.getHoraMinimaRuta())
                                        || siManejoFechaImpl.validaHoraMaxima(getSolicitudViajeVO().getHoraSalida(), rt.getHoraMaximaRuta())) {
                                    //no cumple el horario
                                    literal = "sgl.mensaje.solicitud.viaje.estado.semaforo";
                                    casosRetraso.add(Constantes.SEMAFORO_HORARIO_TIPO_ESPECIFICO);
                                }
                            }
                        }

                        if (casosRetraso.size() > 0) {
                            devuelve = true;
                        }
                        setListaCasosIncumplidos(casosRetraso);
                    } else {// va antes
                        FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.error.solicitar.fechaPasada"));

                    }

                } else {
                    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.error.solicitudSinViajeros"));

                }
            }
        }
        return devuelve;
    }

    public boolean validarFormulario() {
        boolean valida = true;
        if (getOrigen() == null || getOrigen().equals("Origen...")) {
            if (getIdOficinaOrigen() <= Constantes.CERO) {
                FacesUtils.addErrorMessage("Seleccione una Oficina de Origen");
                valida = false;
            }
        } else {
            if (getIdOficinaOrigen() <= Constantes.CERO) { //valida el origen
                FacesUtils.addErrorMessage("Seleccione una Oficina de Origen");
                valida = false;
            } else {
                if (getDestino() == null || getDestino().isEmpty() || getDestino().equals("Destino...")) {
                    if (getIdDestino() <= Constantes.CERO) { //valida el destino
                        FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.viaje.ruta.eligir.destino2"));
                        valida = false;
                    }
                } else {
                    if (getOrigen().equals(getDestino())) {
                        FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.viaje.ruta.eligir.OrigenDestino"));
                        valida = false;
                    } else {
                        if (getIdSgMotivo() <= Constantes.CERO) { //valida el motivo
                            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sia.sgl.agregar.motivo"));
                            valida = false;
                        } else {
                            if (!validarTelefonosByViajero()) {
                                FacesUtils.addErrorMessage("telefono(s) no validado(s)");
                                valida = false;
                            } else {

                                if (getRfcEmpresaSeleccionada() == null || getRfcEmpresaSeleccionada().isEmpty() || getRfcEmpresaSeleccionada().equals("-1")) {
                                    FacesUtils.addErrorMessage("Debe de seleccionar una empresa para poder realizar el viaje");
                                    valida = false;
                                } else {
                                    //se valida la fecha y hora segun sea el caso
                                    Calendar fechaSalidaCompleta = Calendar.getInstance();
                                    fechaSalidaCompleta.setTime(getFechaSalida());
                                    fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, getHoraSalida());
                                    fechaSalidaCompleta.set(Calendar.MINUTE, getMinutoSalida());
                                    Date fechaSal = fechaSalidaCompleta.getTime();
                                    setFechaSalida(fechaSal);

                                    if (getCadena().equals(Constantes.redondo)) {

                                        Calendar fechaRegresoCompleta = Calendar.getInstance();
                                        fechaRegresoCompleta.setTime(getFechaRegreso());
                                        fechaRegresoCompleta.set(Calendar.HOUR_OF_DAY, getHoraRegreso());
                                        fechaRegresoCompleta.set(Calendar.MINUTE, getMinutoRegreso());
                                        Date fechaReg = fechaRegresoCompleta.getTime();
                                        setFechaRegreso(fechaReg);

                                        if (getFechaSalida().after(getFechaRegreso()) || getFechaSalida().equals(getFechaRegreso())) {
                                            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.fechaRegresoAntesFechaSalida"));
                                            valida = false;
                                        } else if (siManejoFechaImpl.dayIsToday(getFechaSalida())) {
                                            if (fechaSal.getTime() < new Date().getTime()) {
                                                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.info.solicitar.horaPasada"));
                                                valida = false;
                                            }
                                        }
                                    } else {
                                        if (siManejoFechaImpl.dayIsToday(getFechaSalida())) {
                                            if (fechaSal.getTime() < new Date().getTime()) {
                                                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.solicitudViaje.mensaje.info.solicitar.horaPasada"));
                                                valida = false;
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return valida;
    }

    public void visito(int invitado) {
        setVisita(new InvitadoVO());
        UsuarioVO u = null;
        if (invitado == Constantes.EMPLEADO) {
            u = usuarioImpl.findByName(getEmpleadoAdd());
        }

        if (u != null) {
            getVisita().setNombre(u.getNombre());
            getVisita().setEmpresa("IHSA");
            getVisita().setEmail(u.getMail());
            getVisita().setUsuario(u.getId());
        } else {
            String[] cad = getInvitado().split("//");
            visita = sgInvitadoImpl.buscarInvitado(cad[0]);

        }
        setJustificaVisita(true);
    }

    /**
     * @return the visita
     */
    public InvitadoVO getVisita() {
        return visita;
    }

    /**
     * @param visita the visita to set
     */
    public void setVisita(InvitadoVO visita) {
        this.visita = visita;
    }

    /**
     * @return the justificaVisita
     */
    public boolean isJustificaVisita() {
        return justificaVisita;
    }

    /**
     * @param justificaVisita the justificaVisita to set
     */
    public void setJustificaVisita(boolean justificaVisita) {
        this.justificaVisita = justificaVisita;
    }

    public List<Object[]> usuariosJson(int idGerencia) {
        return usuarioImpl.traerUsuarioActivosJson(idGerencia);
    }

    public List<Object[]> llenarInvitadoJson() {
        return sgInvitadoImpl.traerInvitadosJsonPorCampo();
    }

    public void vehiculosJson() {
        setListVehiculos(sgVehiculoImpl.traerVehiculosActivoJson());
        JsonArray a = new JsonArray();
        Gson gson = new Gson();
        for (Object[] o : getListVehiculos()) {
            if (getListVehiculos() != null) {
                JsonObject ob = new JsonObject();
                ob.addProperty("value", (Number) (o[0] != null ? (Integer) o[0] : "-"));
                ob.addProperty("label", o[1] != null ? o[1] + "-" + o[2] + "-" + o[3] + "-" + o[4] : "-");
                ob.addProperty("type", o[5] != null ? (String) o[5] : "-");
                a.add(ob);
            }
        }
        String vehiculos = gson.toJson(a);
        PrimeFaces.current().executeScript(";cargarDatosVehiculo(" + vehiculos + ",'vehiculosList');");

    }

    /**
     * @return the idVisito
     */
    public int getIdVisito() {
        return idVisito;
    }

    /**
     * @param idVisito the idVisito to set
     */
    public void setIdVisito(int idVisito) {
        this.idVisito = idVisito;
    }

    public List<SgLugar> getSgLugarList() {
        return this.sgLugarImpl.findAll("nombre", true, false);
    }

    /**
     * @return the Justifica
     */
    public boolean isJustifica() {
        return Justifica;
    }

    /**
     * @param Justifica the Justifica to set
     */
    public void setJustifica(boolean Justifica) {
        this.Justifica = Justifica;
    }

    /**
     * @return the listaCasosIncumplidos
     */
    public List getListaCasosIncumplidos() {
        return listaCasosIncumplidos;
    }

    /**
     * @param listaCasosIncumplidos the listaCasosIncumplidos to set
     */
    public void setListaCasosIncumplidos(List listaCasosIncumplidos) {
        this.listaCasosIncumplidos = listaCasosIncumplidos;
    }

    @PostConstruct
    public void inicializarComponetes() {
        boolean b = false;
        Usuario usuarioSesion = sesion.getUsuario();
        String u = usuarioSesion.getId();
        fechasFestivas();
        setCadena(Constantes.sencillo);
        SgSolicitudViaje sv = sgSolicitudViajeImpl.traerUltimaSolicitud(u);
        if (sv != null) {
//            if (sv.isRedondo()) {
//                setCadena(Constantes.redondo);
                if (siManejoFechaImpl.compare(sv.getFechaSalida(), new Date()) >= 0) {
                    setFechaSalida(sv.getFechaSalida());
                    setFechaRegreso(sv.getFechaRegreso());
                } else {
                    setFechaSalida(new Date());
                    setFechaRegreso(new Date());
                }
//            } 
//            else {
//                setCadena(Constantes.sencillo);
//                if (siManejoFechaImpl.compare(sv.getFechaSalida(), new Date()) >= 0) {
//                    setFechaSalida(sv.getFechaSalida());
//                    setFechaRegreso(getFechaSalida());
//                } else {
//                    setFechaSalida(new Date());
//                    setFechaRegreso(new Date());
//                }
//            }
            setOrigen(sv.getOficinaOrigen().getNombre());
            setIdOficinaOrigen(sv.getOficinaOrigen().getId());

            if (sv.getOficinaDestino() != null) {
                setDestino(sv.getOficinaDestino().getNombre());
                setIdOficinaDestino(sv.getOficinaDestino().getId());
                setIdDestino(sv.getOficinaDestino().getId());
            }
            if (sv.getSgMotivo() != null) {
                setIdSgMotivo(sv.getSgMotivo().getId());
            }
            //setObservacion(sv.getObservacion());
            setObservacion("");

            setOperacion(Constantes.modificar);
            setSolicitudViajeVO(sgSolicitudViajeImpl.buscarPorId(sv.getId(), Constantes.NO_ELIMINADO, Constantes.CERO));
            //listaDestino();
            b = true;
            // setDataModelViajeros(dataModelViajeros);
            //  setSemaforoVo(buscarSemaforoActual());
            setListViajeroVO(sgViajeroImpl.getAllViajerosList(sv.getId()));
            // setDataModelViajeros(new ListDataModel());
        } else {
            setFechaSalida(new Date());
            setFechaRegreso(new Date());
            setIdDetinoRuta(Constantes.RUTA_TIPO_OFICINA);
            int origenOf = usuarioSesion.getSgOficina().getId();
            setIdOficinaOrigen(origenOf);
            setIdSgMotivo(2);
            setIdSgTipoSolicitudViaje(Constantes.SOLICITUDES_TERRESTRE);
            switch (origenOf) {
                case Constantes.ID_OFICINA_TORRE_MARTEL:
                    setIdDestino(Constantes.RUTA_MTY_REY);
                    break;
                case Constantes.ID_OFICINA_SAN_FERNANDO:
                    setIdDestino(Constantes.RUTA_SF_MTY);
                    break;
                default:
                    setIdDestino(Constantes.RUTA_REY_MTY);
                    break;
            }
            try {
                saveSgSolicitudViajeTerrestre();
            } catch (SIAException ex) {
                Logger.getLogger(SolicitudViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        traerOficinaModal();
        //usuariosActivos(Constantes.CERO);
        // vehiculosJson();
        //inicializarlistInvitados(true);
        setUbicacion(cargarListUbicacion());// SE DEBE DE CARGAR DESPUES DE DAR SOLICITAR DE SER NECESARIO
        setListEmpresaByUser(cargarListaEmpresas());
    }

    public void addOrRemoveViajeros(String idUsuario, boolean addOrRemove, int idInvitado) {
        boolean enLista = false;
        if (getListViajeroVO() != null) {
            if (addOrRemove) { //agrega viajero nuevo
                ViajeroVO newVO = getTemporal();
                if (idUsuario != null && !idUsuario.isEmpty()) { //es empleado
                    for (ViajeroVO vo : getListViajeroVO()) {
                        if (vo.isEsEmpleado() && vo.getIdUsuario().equals(idUsuario)) {
                            enLista = true;
                            FacesUtils.addInfoMessage("El viajero ya existe");
                            break;
                        }
                    }

                    if (!enLista) {
                        for (ViajeroVO vo : getListViajeroBajarVO()) {
                            if (vo.isEsEmpleado() && vo.getIdUsuario().equals(idUsuario)) {
                                getListViajeroVO().add(vo);
                                getListViajeroBajarVO().remove(vo);
                                enLista = true;
                                break;
                            }
                        }
                        if (!enLista) {
                            Usuario u = usuarioImpl.find(idUsuario);
                            //  newVO = new ViajeroVO(); 
                            newVO.setIdUsuario(idUsuario);
                            newVO.setUsuario(u.getNombre());
                            newVO.setEsEmpleado(Constantes.TRUE);
                            newVO.setGerencia(u.getGerencia().getNombre());
                            newVO.setEstanciaB(Constantes.TRUE);
                            newVO.setTelefono(u.getTelefono());
                            getListViajeroVO().add(newVO);
                        }

                    }
                } else { //es invitado
                    if (idInvitado > 0) {
                        for (ViajeroVO vo : getListViajeroVO()) {
                            if (!vo.isEsEmpleado() && vo.getIdInvitado() == idInvitado) {
                                enLista = true;
                                FacesUtils.addInfoMessage("El viajero ya existe");
                                break;
                            }
                        }
                        if (!enLista) {
                            int count = 0;
                            for (ViajeroVO vo : getListViajeroBajarVO()) {
                                if (!vo.isEsEmpleado() && vo.getIdInvitado() == idInvitado) {
                                    getListViajeroVO().add(vo);
                                    getListViajeroBajarVO().remove(count);
                                    enLista = true;
                                    break;
                                }
                                count++;
                            }
                            if (!enLista) {
                                SgInvitado nuevoInvitado = sgInvitadoImpl.find(idInvitado);

                                newVO = new ViajeroVO();
                                newVO.setIdInvitado(idInvitado);
                                newVO.setInvitado(nuevoInvitado.getNombre());
                                newVO.setEsEmpleado(Constantes.FALSE);
                                newVO.setEmpresa(nuevoInvitado.getSgEmpresa().getNombre());
                                newVO.setEstanciaB(Constantes.TRUE);
                                newVO.setTelefono(nuevoInvitado.getTelefono());

                                getListViajeroVO().add(newVO);
                            }
                        }
                        setAddInvitado(Constantes.CERO);
                    }

                }
            } else { //quita vijero
                if (idUsuario != null && !idUsuario.isEmpty()) {
                    for (ViajeroVO vo : getListViajeroVO()) {
                        if (vo.isEsEmpleado() && vo.getIdUsuario().equals(idUsuario)) {
                            getListViajeroBajarVO().add(vo);
                            getListViajeroVO().remove(vo);
                            break;
                        }
                    }
                } else {
                    int count = 0;
                    for (ViajeroVO vo : getListViajeroVO()) {
                        if (!vo.isEsEmpleado() && vo.getIdInvitado() == idInvitado) {
                            getListViajeroBajarVO().add(vo);
                            getListViajeroVO().remove(count);
                            break;
                        }
                        count++;
                    }
                    setAddInvitado(Constantes.CERO);
                }
            }
        }
    }

    /**
     * @return the idLugar
     */
    public int getIdLugar() {
        return idLugar;
    }

    /**
     * @param idLugar the idLugar to set
     */
    public void setIdLugar(int idLugar) {
        this.idLugar = idLugar;
    }

    public boolean solicitarViaje() throws SIAException {
        boolean regresa = false;
        String msj = "";
        List<ViajeroVO> viajeros = getListViajeroVO(); //aqui tarda
        String u = this.sesion.getUsuario().getId();
        int r = this.sesion.getIdRol();

        switch (getSolicitudViajeVO().getIdOficinaDestino()) {
            case 1:
                setIdLugar(3);
                break;
            case 2:
                setIdLugar(1);
                break;
            case 3:
                setIdLugar(2);
                break;
            default:
                setIdLugar(4);
                break;
        }
        List<SolicitudViajeVO> listSolicitudes = null;
        String descrip = null;

        if (getSolicitudViajeVO().getIdSgTipoEspecifico() != Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA) {
            listSolicitudes = sgSolicitudViajeImpl.clonarSolicitudes(viajeros, getSolicitudViajeVO().getIdSolicitud(), campoUsuarioPuestoVo, getIdDestino(), getSolicitudViajeVO());
        } else {
            String dir = FacesUtils.getRequestParameter("direccionV");
            String hot = FacesUtils.getRequestParameter("hotelSug");
            if (dir.isEmpty()) {
                dir = "sin Sugerencia";
            }
            if (hot.isEmpty()) {
                hot = "sin Sugerencia";
            }
            descrip = dir + "-" + hot;
        }

        if (listSolicitudes != null) {
            if (listSolicitudes.size() > 1) {
                sgViajeroImpl.actualizarViajeros(viajeros, getSolicitudViajeVO().getIdSolicitud(), listSolicitudes, getSolicitudViajeVO().getIdGerencia());
                for (SolicitudViajeVO sv : listSolicitudes) {
                    viajeros = sgViajeroImpl.getAllViajerosList(sv.getIdSolicitud());
                    if (listSolicitudes.indexOf(sv) > 0) {
                        setconChofer(Constantes.TRUE);
                    }
                    regresa = sgSolicitudViajeImpl.solicitarViaje(u, sv,
                            getVisita(), getMensaje(), r, getListaCasosIncumplidos(), getIdLugar(),
                            (isconChofer() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE),
                            (getVehiculoVO() != null ? getVehiculoVO().getId() : Constantes.CERO), descrip, getIdUbicacion(), getIdCampoActual());

                }
                if (regresa) {
                    //regresar codigo para mandar mensaje con el codigo
                    FacesUtils.addInfoMessage("Las solicitudes de viaje fueron creadas con exito");
                    msj = "Las solicitudes de viaje fueron creadas con exito";
                    PrimeFaces.current().executeScript(";recargar('" + msj + "');");

                } else {
                    FacesUtils.addErrorMessage("ocurrio un error inesperado favor de comunicarse con el equipo del SIA al correo soportesia@ihsa.mx");
                }
                PrimeFaces.current().executeScript(";$(modalFinalizar).modal('hide');");
                inicializarComponetes();
                setPanelSV(Constantes.TRUE);
            } else {
                regresa = sgSolicitudViajeImpl.solicitarViaje(u, getSolicitudViajeVO(),
                        getVisita(), getMensaje(), r, getListaCasosIncumplidos(), getIdLugar(),
                        (isconChofer() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE),
                        (getVehiculoVO() != null ? getVehiculoVO().getId() : Constantes.CERO), descrip, getIdUbicacion(), getIdCampoActual());
                if (regresa) {
                    //regresar codigo para mandar mensaje con el codigo
                    FacesUtils.addInfoMessage("La solicitud de viaje fue creada con exito");
                    msj = "La solicitud de viaje fue creada con exito";
                    PrimeFaces.current().executeScript(";recargar('" + msj + "');");

                } else {
                    FacesUtils.addErrorMessage("ocurrio un error inesperado favor de comunicarse con el equipo del SIA al correo soportesia@ihsa.mx");
                }
                PrimeFaces.current().executeScript(";$('#modalFinalizar').modal('hide');");
                inicializarComponetes();
                setPanelSV(Constantes.TRUE);

            }

        } else if (getSolicitudViajeVO() != null) {
            regresa = sgSolicitudViajeImpl.solicitarViaje(u, getSolicitudViajeVO(),
                    getVisita(), getMensaje(), r, getListaCasosIncumplidos(), getIdLugar(),
                    (isconChofer() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE),
                    (getVehiculoVO() != null ? getVehiculoVO().getId() : Constantes.CERO), descrip, getIdUbicacion(), getIdCampoActual());
            if (regresa) {
                //regresar codigo para mandar mensaje con el codigo
                FacesUtils.addInfoMessage("La solicitud de viaje fue creada con exito");
                msj = "La solicitud de viaje fue creada con exito";
                PrimeFaces.current().executeScript(";recargar('" + msj + "');");

            } else {
                FacesUtils.addErrorMessage("ocurrio un error inesperado favor de comunicarse con el equipo del SIA al correo soportesia@ihsa.mx");
            }
            PrimeFaces.current().executeScript(";$('#modalFinalizar').modal('hide');");
            inicializarComponetes();
            setPanelSV(Constantes.TRUE);

        }
        // PrimeFaces.current().executeScript(";recargar('"+msj+"');");
        return regresa;
    }

    public List<UsuarioVO> traerUsuarios(String cadena) {
        return apCampoUsuarioRhPuestoImpl.traerUsurioPorParteNombre(cadena, sesion.getUsuario().getApCampo().getId());
    }

    public List<InvitadoVO> traerInvitados(String cadena) {
        return sgInvitadoImpl.buscarInvitadoParteNombre(cadena);
    }

    /**
     * @return the EmpleadoAdd
     */
    public String getEmpleadoAdd() {
        return EmpleadoAdd;
    }

    /**
     * @param EmpleadoAdd the EmpleadoAdd to set
     */
    public void setEmpleadoAdd(String EmpleadoAdd) {
        this.EmpleadoAdd = EmpleadoAdd;
    }

    /**
     * @return the addInvitado
     */
    public int getAddInvitado() {
        return addInvitado;
    }

    /**
     * @param addInvitado the addInvitado to set
     */
    public void setAddInvitado(int addInvitado) {
        this.addInvitado = addInvitado;
    }

    public void actualizarSolicitud() {
        if (getIdDestino() > 1000) {
            setIdSiCiudadOrigen(getIdDestino() - 1000);
        }
    }

    /**
     * @return the listaOrigenes
     */
    public List<List<Object[]>> getListaOrigenes() {
        return listaOrigenes;
    }

    /**
     * @param listaOrigenes the listaOrigenes to set
     */
    public void setListaOrigenes(List<List<Object[]>> listaOrigenes) {
        this.listaOrigenes = listaOrigenes;
    }

    /**
     * @return the listaDestinos
     */
    public List<List<Object[]>> getListaDestinos() {
        return listaDestinos;
    }

    /**
     * @param listaDestinos the listaDestinos to set
     */
    public void setListaDestinos(List<List<Object[]>> listaDestinos) {
        this.listaDestinos = listaDestinos;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param ruta the url to set
     */
    public void setUrl(String ruta) {
        this.url = ruta;
    }

    /**
     * @return the listViajeroVO
     */
    public List<ViajeroVO> getListViajeroVO() {
        return listViajeroVO;
    }

    /**
     * @param listViajeroVO the listViajeroVO to set
     */
    public void setListViajeroVO(List<ViajeroVO> listViajeroVO) {
        this.listViajeroVO = listViajeroVO;
    }

    /**
     * @return the listViajeroBajarVO
     */
    public List<ViajeroVO> getListViajeroBajarVO() {
        return listViajeroBajarVO;
    }

    /**
     * @param listViajeroBajarVO the listViajeroBajarVO to set
     */
    public void setListViajeroBajarVO(List<ViajeroVO> listViajeroBajarVO) {
        this.listViajeroBajarVO = listViajeroBajarVO;
    }

    /**
     * @return the viajaSolicitante
     */
    public boolean isViajaSolicitante() {
        return viajaSolicitante;
    }

    /**
     * @param viajaSolicitante the viajaSolicitante to set
     */
    public void setViajaSolicitante(boolean viajaSolicitante) {
        this.viajaSolicitante = viajaSolicitante;
    }

    /**
     * @return the tieneVehiculo
     */
    public boolean isTieneVehiculo() {
        return tieneVehiculo;
    }

    /**
     * @param tieneVehiculo the tieneVehiculo to set
     */
    public void setTieneVehiculo(boolean tieneVehiculo) {
        this.tieneVehiculo = tieneVehiculo;
    }

    /**
     * @return the vehiculoVO
     */
    public VehiculoVO getVehiculoVO() {
        return vehiculoVO;
    }

    /**
     * @param vehiculoVO the vehiculoVO to set
     */
    public void setVehiculoVO(VehiculoVO vehiculoVO) {
        this.vehiculoVO = vehiculoVO;
    }

    /**
     * @return the conChofer
     */
    public boolean isconChofer() {
        return conChofer;
    }

    /**
     * @param asignado the conChofer to set
     */
    public void setconChofer(boolean asignado) {
        this.conChofer = asignado;
    }

    /**
     * @return the listVehiculoVO
     */
    public List<VehiculoVO> getListVehiculoVO() {
        return listVehiculoVO;
    }

    /**
     * @param listVehiculoVO the listVehiculoVO to set
     */
    public void setListVehiculoVO(List<VehiculoVO> listVehiculoVO) {
        this.listVehiculoVO = listVehiculoVO;
    }

    /**
     * @return the listaEmpleadosActivos
     */
    public List<Object[]> getListaEmpleadosActivos() {
        return listaEmpleadosActivos;
    }

    /**
     * @param listaEmpleadosActivos the listaEmpleadosActivos to set
     */
    public void setListaEmpleadosActivos(List<Object[]> listaEmpleadosActivos) {
        this.listaEmpleadosActivos = listaEmpleadosActivos;
    }

    /**
     * @param listaInvitados the listaInvitados to set
     */
    public void setListInvitados(List<Object[]> listaInvitados) {
        this.listInvitados = listaInvitados;
    }

    /**
     * @return the listInvitados
     */
    public List<Object[]> getListInvitados() {
        return listInvitados;
    }

    /**
     * @return the newInvitado
     */
    public String getNewInvitado() {
        return newInvitado;
    }

    /**
     * @param newInvitado the newInvitado to set
     */
    public void setNewInvitado(String newInvitado) {
        this.newInvitado = newInvitado;
    }

    public void crearInvitado(String empresa) {
        ViajeroVO newVO = new ViajeroVO();
        if (getNewInvitado() != null && !getNewInvitado().isEmpty()) {
            if (empresa != null && !empresa.isEmpty()) {
                int idEmpresa = 0;
                for (Object[] ob : getListEmpresas()) {
                    if (empresa.equals(ob[1])) {
                        idEmpresa = Integer.parseInt(ob[0].toString());
                        break;
                    }
                }
                SgInvitado i = null;
                InvitadoVO vo = new InvitadoVO();
                vo.setNombre(getNewInvitado());
                vo.setTelefono(getNewTelefono());
                // vo.setEmail(sesion.getUsuario().getEmail());
                vo.setEmail("sia@ihsa.mx");
                if (idEmpresa == Constantes.CERO) {
                    SgEmpresa e = sgEmpresaImpl.buscarPorNombre(empresa);
                    if (e != null) {
                        idEmpresa = e.getId();
                    } else {
                        e = new SgEmpresa();
                        e.setNombre(empresa);
                        sgEmpresaImpl.guardarEmpresa(sesion.getUsuario(), e);
                        idEmpresa = sgEmpresaImpl.buscarPorNombre(empresa).getId();
                    }

                }
                i = sgInvitadoImpl.guardarInvitado(sesion.getUsuario(), vo, idEmpresa);
                newVO.setIdInvitado(i.getId());
                newVO.setInvitado(i.getNombre());
                newVO.setEsEmpleado(Constantes.FALSE);
                newVO.setEmpresa(i.getSgEmpresa().getNombre());
                newVO.setEstanciaB(Constantes.FALSE);
                newVO.setTelefono(i.getTelefono());
                setNombre(i.getNombre());
                setNewTelefono(i.getTelefono());
                newVO.setConfirTel(Constantes.TRUE);
                setTemporal(newVO);
                addOrRemoveViajeros("", Constantes.TRUE, i.getId());
                PrimeFaces.current().executeScript(";$('#modalCrearInvitado').modal('hide');");
                inicializarlistInvitados(true);
            } else {
                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("Es necesaria la empresa para poder crear el invitado"));
            }
        } else {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("Es necesario el nombre para poder crear el invitado"));
        }
    }

    public void empresas() {
        String emp = "";
        JsonArray a = new JsonArray();
        Gson gson = new Gson();
        if (getListEmpresas() == null || getListEmpresas().isEmpty()) {
            setListEmpresas(sgEmpresaImpl.traerEmpresasJson());
            for (Object[] o : getListEmpresas()) {

                JsonObject ob = new JsonObject();
                ob.addProperty("value", o[0] != null ? (Integer) o[0] : 0);
                ob.addProperty("label", o[1] != null ? (String) o[1] : "-");
                a.add(ob);
            }
            emp = gson.toJson(a);
            PrimeFaces.current().executeScript(";limpiarDataListComp();");
            PrimeFaces.current().executeScript(";cargarDatosEmpresa(" + emp + ",'listEmpresas');");
        }

    }

    /**
     * @return the listEmpresas
     */
    public List<Object[]> getListEmpresas() {
        return listEmpresas;
    }

    /**
     * @param listEmpresas the listEmpresas to set
     */
    public void setListEmpresas(List<Object[]> listEmpresas) {
        this.listEmpresas = listEmpresas;
    }

    public void mostrarSolicitudesByAprobar() {
        try {
            int e1 = 0;
            int e2 = 0;
            switch (sesion.getIdRol()) {
                case Constantes.ROL_GERENTE:
                    e1 = Constantes.ESTATUS_APROBAR;
                    e2 = Constantes.ESTATUS_VISTO_BUENO;
                    break;
                case Constantes.ROL_DIRECCION_GENERAL:
                    e1 = Constantes.CERO;
                    e2 = Constantes.ESTATUS_AUTORIZAR;
                    break;
                case Constantes.ROL_JUSTIFICA_VIAJES:
                    e1 = Constantes.ESTATUS_JUSTIFICAR;
                    e2 = Constantes.CERO;
                    break;
                case Constantes.SGL_RESPONSABLE:
                    e1 = Constantes.CERO;
                    e2 = Constantes.ESTATUS_APROBAR;
                    break;
                case Constantes.ROL_ADMIN_VIAJES_AEREOS:
                    e1 = Constantes.CERO;
                    e2 = Constantes.ESTATUS_APROBAR;
                    break;
                default:
                    e1 = Constantes.CERO;
                    e2 = Constantes.CERO;
                    break;
            }
            if (e1 != 0 && e2 != 0) {
                setListSolicitudesVo(
                        sgSolicitudViajeImpl.traerSolicitudesTerrestreByEstatus(
                                e1, Constantes.CERO, sesion.getUsuario().getId(), " AND s.fecha_salida >= CAST('NOW' AS DATE)"));
                getListSolicitudesVo().addAll(
                        sgSolicitudViajeImpl.traerSolicitudesAereasByEstatus(e2, sesion.getUsuario().getId(), sesion.getIdRol()));
                setCountSVT(getListSolicitudesVo().size());
            } else if (e2 == 0 && e1 == Constantes.ESTATUS_JUSTIFICAR) {
                setListSolicitudesVo(
                        sgSolicitudViajeImpl.traerSolicitudesTerrestreByEstatus(
                                e1, Constantes.CERO, null, " AND s.fecha_salida >= CAST('NOW' AS DATE)"));
                getListSolicitudesVo().addAll(
                        sgSolicitudViajeImpl.traerSolicitudesTerrestreByEstatus(
                                Constantes.ESTATUS_CON_CENTOPS, Constantes.CERO, null, " AND s.fecha_salida >= CAST('NOW' AS DATE)"));
                setCountSVT(getListSolicitudesVo().size());
            } else if (e2 != 0 && e1 == 0) {
                setListSolicitudesVoAereas(
                        sgSolicitudViajeImpl.traerSolicitudesAereasByEstatus(e2, sesion.getUsuario().getId(), sesion.getIdRol()));
                setCountSVA(getListSolicitudesVoAereas().size());
            }
            setActualizar(Constantes.FALSE);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    /**
     * @return the listSolicitudesVo
     */
    public List<SolicitudViajeVO> getListSolicitudesVo() {
        return listSolicitudesVo;
    }

    /**
     * @param listSolicitudesVo the listSolicitudesVo to set
     */
    public void setListSolicitudesVo(List<SolicitudViajeVO> listSolicitudesVo) {
        this.listSolicitudesVo = listSolicitudesVo;
    }

    public void aprobarSV(boolean tipo) {

        ArrayList<String> cod = new ArrayList<String>();
        boolean aprobada = false;
        setCodigos("");
        List<SolicitudViajeVO> lista = new ArrayList<SolicitudViajeVO>();
        ArrayList<String> jusCod = new ArrayList<>();
        if (tipo) {
            lista = getListSolicitudesVo();
        } else {
            lista = getListSolicitudesVoAereas();
        }

        for (SolicitudViajeVO vo : lista) {
            if (vo.isSelect()) {
//                Calendar cal = new GregorianCalendar();
//                cal.setTimeInMillis(vo.getFechaSalida().getTime());
//                cal.set(Calendar.HOUR_OF_DAY, 23);
//                cal.set(Calendar.MINUTE, 59);
//                Date maxHora = new Date(cal.getTimeInMillis()); mover a nueva ventana
                // vo.setHoraSalida(maxHora);
                if (siManejoFechaImpl.validaFechaSalidaViaje(vo.getFechaSalida(), vo.getHoraSalida())) {
                    if (vo.getViajeros().size() > 0) {
                        if (vo.getIdSgTipoEspecifico() == Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE) {
                            if (vo.getIdEstatus() == Constantes.ESTATUS_APROBAR) {
                                if (siManejoFechaImpl.dateIsTomorrow(vo.getFechaSalida())
                                        && (siManejoFechaImpl.validaHoraMaximaAprobacion(sesion.getIdRol(), Constantes.HORA_MAXIMA_APROBACION))) {
                                    getJus().add(vo.getIdSolicitud());
                                    jusCod.add(vo.getCodigo());
                                } else if (siManejoFechaImpl.salidaProximoLunes(new Date(), vo.getFechaSalida(), vo.getIdEstatus())) {
                                    getJus().add(vo.getIdSolicitud());
                                    jusCod.add(vo.getCodigo());
                                } else {
                                    aprobada = estatusAprobacionService.aprobarSolicitud(
                                            estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(
                                                    vo.getIdSolicitud(), vo.getIdEstatus()).getId(), sesion.getUsuario().getId());
                                }
                            } else {
                                aprobada = estatusAprobacionService.aprobarSolicitud(
                                        estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(
                                                vo.getIdSolicitud(), vo.getIdEstatus()).getId(), sesion.getUsuario().getId());
                            }
                        } else {
                            if (vo.getIdEstatus() == Constantes.ESTATUS_APROBAR) {
                                List<HistorialItinerarioVO> h = sgItinerarioImpl.getHistorialItinerarioPorSolicitud(vo.getIdSolicitud());
                                if (h != null && h.size() > 0) {
                                    aprobada = estatusAprobacionService.aprobarSolicitud(
                                            estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(
                                                    vo.getIdSolicitud(), vo.getIdEstatus()).getId(), sesion.getUsuario().getId());
                                } else {
                                    FacesUtils.addErrorMessage("No es posible aprobar la solicitud " + vo.getCodigo() + " debido a que no cuenta con ningun Itinerario");
                                    break;
                                }
                                sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(idGerencia, sencillo, cadena);

                            } else {
                                aprobada = estatusAprobacionService.aprobarSolicitud(
                                        estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(
                                                vo.getIdSolicitud(), vo.getIdEstatus()).getId(), sesion.getUsuario().getId());
                            }

                        }
                    } else {
                        FacesUtils.addErrorMessage("No es posible aprobar la solicitud" + vo.getCodigo() + " debido a que no cuenta con ningun viajero");
                    }

                } else {
                    FacesUtils.addInfoMessage("No se puede aprobar la solicitud: " + vo.getCodigo() + " debido a que su fecha de salida ya ha pasado.");
                }

                if (aprobada) {
                    cod.add(vo.getCodigo());
                }

            }
        }
        setCodigos(Joiner.on(",").join(cod));
        if (aprobada) {
            FacesUtils.addInfoMessage("Se Aprobo la(s) Solicitud(es) siguiente(s): " + getCodigos());
            mostrarSolicitudesByAprobar();
        } else {
            FacesUtils.addErrorMessage("ocurrio un error inesperado favor de comunicarse con el equipo del SIA al correo soportesia@ihsa.mx");
        }
        if (getJus().size() > 0) {
            setCodigos(Joiner.on(",").join(jusCod));
            setMensaje("");
            PrimeFaces.current().executeScript(";$('#modalJustificar').modal('show');");
        }
    }

    public void activarSv() {
        ArrayList<String> cod = new ArrayList<String>();
        boolean aprobada = false;
        setCodigos("");
        List<SolicitudViajeVO> lista = getListSolicitudesVo();
        ArrayList<String> jusCod = new ArrayList<>();

        for (SolicitudViajeVO vo : lista) {
            if (vo.isSelect()) {
                if (vo.getViajeros().size() > 0) {
                    if (vo.getIdEstatus() == Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO) {

                        EstatusAprobacionSolicitudVO est
                                = estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(vo.getIdSolicitud(), Constantes.ESTATUS_JUSTIFICAR);
                        int idEst = 0;
                        if (est != null) {
                            idEst = est.getId();
                            System.out.println(idEst);
                        } else {
                            est = estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(vo.getIdSolicitud(), Constantes.ESTATUS_CON_CENTOPS);
                            idEst = est.getId();
                            System.out.println(idEst + " zzzz");
                        }

                        if (idEst > 0) {
                            aprobada = estatusAprobacionService.activarSolicitud(idEst, "Activada por Centops",
                                    sesion.getUsuario().getId(), Constantes.FALSE, Constantes.FALSE, est.getIdEstatus());
                            if (aprobada) {
                                cod.add(vo.getCodigo());
                            }
                        }
                    }
                }
            }
        }
        mostrarSolicitudesCanceladas();
    }

    public boolean aprobarWhitJustificacion() {
        boolean regresa = false;
        EstatusAprobacionSolicitudVO e = new EstatusAprobacionSolicitudVO();
        if (getJus() != null && !getJus().isEmpty()) {
            if (getMensaje() != null && !getMensaje().isEmpty()) {
                for (int i : getJus()) {
                    e = estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(i, Constantes.ESTATUS_APROBAR);
                    estatusAprobacionService.aprobarJustificandoSolicitud(e.getId(), i, getMensaje(), sesion.getUsuario().getId());
                    regresa = Constantes.TRUE;
                }
            }

        }
        PrimeFaces.current().executeScript(";$('#modalJustificar').modal('hide');");
        return regresa;
    }

    public void validaSeleccion(boolean cancel, boolean gerenteAp) {
        boolean val = false;
        setCodigos("");
        ArrayList<String> cod = new ArrayList<String>();
        setListSolicitudesVoCancelar(new ArrayList<SolicitudViajeVO>());
        if (gerenteAp) {
            for (SolicitudViajeVO vo : getListSolicitudesVo()) {
                if (vo.isSelect()) {
                    cod.add(vo.getCodigo());
                    val = true;
                    if (cancel) {
                        getListSolicitudesVoCancelar().add(vo);
                    }
                }
            }
        } else {
            for (SolicitudViajeVO vo : getListSolicitudesVoAereas()) {
                if (vo.isSelect()) {
                    cod.add(vo.getCodigo());
                    val = true;
                    if (cancel) {
                        getListSolicitudesVoCancelar().add(vo);
                    }
                }
            }
        }

        setCodigos(Joiner.on(",").join(cod));
        setValidaCheck(val);

    }

    /**
     * @return the validaCheck
     */
    public boolean isValidaCheck() {
        return validaCheck;
    }

    /**
     * @param validaCheck the validaCheck to set
     */
    public void setValidaCheck(boolean validaCheck) {
        this.validaCheck = validaCheck;
    }

    /**
     * @return the codigos
     */
    public String getCodigos() {
        return codigos;
    }

    /**
     * @param codigos the codigos to set
     */
    public void setCodigos(String codigos) {
        this.codigos = codigos;
    }

    public void cancelarSolictud() {
        setCodigos("");
        boolean cancelar = false;
        ArrayList<String> cod = new ArrayList<String>();
        for (SolicitudViajeVO vo : getListSolicitudesVoCancelar()) {
            if (vo.isSelect()) {
                try {
                    cancelar = estatusAprobacionService.cancelarSolicitud(
                            estatusAprobacionService.buscarEstatusAprobacionPorIdSolicitudIdEstatus(vo.getIdSolicitud(), vo.getIdEstatus()).getId(),
                            getMensaje(), sesion.getUsuario().getId(), true, false, Constantes.FALSE);
                    if (cancelar) {
                        cod.add(vo.getCodigo());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SolicitudViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        setListSolicitudesVoCancelar(new ArrayList<SolicitudViajeVO>());
        setMensaje("");
        setCodigos(Joiner.on(",").join(cod));
        if (cancelar) {
            FacesUtils.addInfoMessage("Se cancela(n) la(s) Solicitud(es) siguiente(s): " + getCodigos());
            mostrarSolicitudesByAprobar();
        } else {
            FacesUtils.addErrorMessage("ocurrio un error inesperado favor de comunicarse con el equipo del SIA al correo soportesia@ihsa.mx");
        }
    }

    /**
     * @return the listSolicitudesVoCancelar
     */
    public List<SolicitudViajeVO> getListSolicitudesVoCancelar() {
        return listSolicitudesVoCancelar;
    }

    /**
     * @param listSolicitudesVoCancelar the listSolicitudesVoCancelar to set
     */
    public void setListSolicitudesVoCancelar(List<SolicitudViajeVO> listSolicitudesVoCancelar) {
        this.listSolicitudesVoCancelar = listSolicitudesVoCancelar;
    }

    /**
     * @return the listSolicitudesVoAereas
     */
    public List<SolicitudViajeVO> getListSolicitudesVoAereas() {
        return listSolicitudesVoAereas;
    }

    /**
     * @param listSolicitudesVoAereas the listSolicitudesVoAereas to set
     */
    public void setListSolicitudesVoAereas(List<SolicitudViajeVO> listSolicitudesVoAereas) {
        this.listSolicitudesVoAereas = listSolicitudesVoAereas;
    }

    /**
     * @return the jus
     */
    public List<Integer> getJus() {
        return jus;
    }

    /**
     * @param jus the jus to set
     */
    public void setJus(List<Integer> jus) {
        this.jus = jus;
    }

    public void cargarListaViajeros(boolean terrestre) {
        List<SolicitudViajeVO> lista = new ArrayList();
        setSolicitudViajeVO(new SolicitudViajeVO());
        setListViajeroBajarVO(new ArrayList<ViajeroVO>());
        setCodigos("");
        String cod = "";
        int count = 0;
        if (terrestre) {
            lista = getListSolicitudesVo();
        } else {
            lista = getListSolicitudesVoAereas();
        }
        for (SolicitudViajeVO vo : lista) {
            if (vo.isSelect()) {
                cod = vo.getCodigo();
                setSolicitudViajeVO(vo);
                count++;
            }
        }
        if (count == 1) {
            setListaEmpleadosActivos(null);
            usuariosActivos(getSolicitudViajeVO().getIdGerencia());
            setListInvitados(new ArrayList<Object[]>());
            inicializarlistInvitados(false);
            setCodigos(cod);
            PrimeFaces.current().executeScript(";$('#modalAddViajeros').modal('show');");
        } else if (count > 1) {
            setSolicitudViajeVO(new SolicitudViajeVO());
            FacesUtils.addErrorMessage("Solo se debe de seleccionar una Solicitud para poder agregar viajeros");
        } else {
            setSolicitudViajeVO(new SolicitudViajeVO());
            FacesUtils.addErrorMessage("Debe de seleccionar una Solicitud para poder agregar viajeros");
        }
    }

    public void removeViajeros() {
        int idViajero = Integer.parseInt(FacesUtils.getRequestParameter("idViajero"));
        String idUsuario = FacesUtils.getRequestParameter("idUsuario");
        int IdInvitado = Integer.parseInt(FacesUtils.getRequestParameter("idInvitado"));
        if (idViajero > 0) {
            for (ViajeroVO vo : getSolicitudViajeVO().getViajeros()) {
                if (idViajero == vo.getId()) {
                    getListViajeroBajarVO().add(vo);
                    getSolicitudViajeVO().getViajeros().remove(vo);
                    break;
                }
            }
        } else {
            for (ViajeroVO vo : getSolicitudViajeVO().getViajeros()) {
                if (idViajero == 0) {
                    if (vo.getIdInvitado() > 0 && IdInvitado > 0) {
                        if (vo.getIdInvitado() == IdInvitado) {
                            getSolicitudViajeVO().getViajeros().remove(vo);
                            break;
                        }
                    } else {
                        if (idUsuario.equals(vo.getIdUsuario())) {
                            getSolicitudViajeVO().getViajeros().remove(vo);
                            break;
                        }
                    }
                }
            }

        }

    }

    public void addViajeros() {
        String empleado = FacesUtils.getRequestParameter("completarEmpleado");
        String invitado = FacesUtils.getRequestParameter("completarInvitado");
        UsuarioVO user = null;
        InvitadoVO inv = null;
        boolean agregar = true;
        ViajeroVO v = null;
        if (empleado != null && !empleado.isEmpty()) {
            user = usuarioImpl.findByName(empleado);
        } else if (invitado != null && !invitado.isEmpty()) {
            inv = sgInvitadoImpl.buscarInvitado(invitado);
        } else {
            FacesUtils.addErrorMessage("Ocurrio un error inesperado");
        }
        //valida que el viajero no se encuentre en la lista
        for (ViajeroVO vo : getSolicitudViajeVO().getViajeros()) {
            if (user != null) {
                if (vo.getUsuario().equals(user.getId())) {
                    FacesUtils.addErrorMessage("No se puede agregar al mismo viajero mas de una vez");
                    agregar = false;
                    break;
                }
            } else if (inv != null) {
                if (inv.getIdInvitado() == vo.getIdInvitado()) {
                    FacesUtils.addErrorMessage("No se puede agregar al mismo viajero mas de una vez");
                    agregar = false;
                    break;
                }
            }
        }

        //Valida que no se encuentre en la lista de bajas
        if (getListViajeroBajarVO() != null && !getListViajeroBajarVO().isEmpty()) {
            for (ViajeroVO vo : getListViajeroBajarVO()) {
                if (user != null) {
                    if (user.getId().equals(vo.getUsuario())) {
                        v = vo;
                        getListViajeroBajarVO().remove(vo);
                        break;
                    }
                } else {
                    if (inv.getIdInvitado() == vo.getIdInvitado()) {
                        v = vo;
                        getListViajeroBajarVO().remove(vo);
                        break;
                    }
                }
            }
        }

        if (agregar) {
            if (v != null) {
                getSolicitudViajeVO().getViajeros().add(v);
            } else {
                v = new ViajeroVO();
                if (user != null) {
                    v.setIdUsuario(user.getId());
                    v.setUsuario(user.getNombre());
                    v.setInvitado("null");
                    v.setIdInvitado(0);
                    v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
                    v.setEsEmpleado(Constantes.TRUE);
                    v.setCorreo(user.getMail());
                    v.setTelefono(user.getTelefono());
                } else if (inv != null) {
                    v.setIdInvitado(inv.getIdInvitado());
                    v.setInvitado(inv.getNombre());
                    v.setUsuario("null");
                    v.setIdUsuario("null");
                    v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
                    v.setEsEmpleado(Constantes.FALSE);
                    v.setCorreo("");
                    v.setTelefono("");
                }
                v.setId(0);
                v.setCodigoSolicitudViaje(getSolicitudViajeVO().getCodigo());
                v.setFechaSalida(getSolicitudViajeVO().getFechaSalida());
                v.setHoraSalida(getSolicitudViajeVO().getHoraSalida());
                v.setFechaRegreso(getSolicitudViajeVO().getFechaRegreso());
                v.setHoraRegreso(getSolicitudViajeVO().getHoraRegreso());
                v.setEstancia(Constantes.BOOLEAN_FALSE);
                v.setEstanciaB(Constantes.FALSE);
                v.setRedondo(getSolicitudViajeVO().isRedondo() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                v.setDestino(getSolicitudViajeVO().getDestino());
                v.setIdSolicitudViaje(getSolicitudViajeVO().getIdSolicitud());
                v.setSgSolicitudEstancia(0);
                v.setIdViaje(0);
                if (getSolicitudViajeVO().getIdSgTipoEspecifico() == Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA) {
                    v.setIdOrigen(getSolicitudViajeVO().getIdSiCiudadOrigen());
                    v.setIdDestino(getSolicitudViajeVO().getIdSiCiudadDestino());
                } else {
                    v.setIdOrigen(getSolicitudViajeVO().getIdOficinaOrigen());
                    v.setIdDestino(getSolicitudViajeVO().getIdOficinaDestino() != Constantes.CERO ? getSolicitudViajeVO().getIdOficinaDestino() : getSolicitudViajeVO().getIdSiCiudadDestino());
                }

                getSolicitudViajeVO().getViajeros().add(v);
            }

        }
    }

    public void addEditOrRemoveViajeros() throws Exception {
        List<ViajeroVO> lista = getSolicitudViajeVO().getViajeros();
        String motivo = "Eliminado por " + sesion.getUsuario().getNombre();

        if (lista.size() > 0) {
            boolean estanciaActual = false;
            int idestancia = 0;
            for (ViajeroVO vo : lista) {
                estanciaActual = (vo.isEstancia());
                if (vo.getSgSolicitudEstancia() > 0) {
                    idestancia = vo.getSgSolicitudEstancia();
                }
                if (vo.getId() > 0) {
                    if (estanciaActual != vo.isEstanciaB()) {
                        sgViajeroImpl.update(vo.getId(), vo.getIdUsuario(), vo.getIdInvitado(), vo.getIdSolicitudViaje(), Constantes.CERO,
                                vo.getSgSolicitudEstancia(), vo.isEstanciaB(), sesion.getUsuario().getId());

                    }
                } else {
                    vo.setSgSolicitudEstancia(idestancia);
                    if (estanciaActual != vo.isEstanciaB()) {
                        vo.setEstancia(estanciaActual ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                    }
                    sgViajeroImpl.guardarViajero(vo.getIdInvitado(), vo.getIdUsuario(), vo.getSgSolicitudEstancia(),
                            vo.getIdSolicitudViaje(), Constantes.CERO, "", sesion.getUsuario().getId(), vo.isEstancia(), vo.isRedondo());
                }
            }
            for (ViajeroVO vob : getListViajeroBajarVO()) {
                if (vob.getId() > 0) {
                    sgViajeroImpl.delete(vob.getId(), sesion.getUsuario().getId(), motivo);
                }
            }
        }
        PrimeFaces.current().executeScript(";$('#modalAddViajeros').modal('hide');");
        mostrarSolicitudesByAprobar();
    }

    public void cargarDatosItinerario() {
        setSolicitudViajeVO(new SolicitudViajeVO());
        List<SolicitudViajeVO> list = getListSolicitudesVoAereas();
        boolean select = false;
        int i = 0;

        for (SolicitudViajeVO vo : list) {
            if (vo.isSelect()) {
                i++;
                if (i == 1) {
                    vo.setItinerarioCompletoVoIda(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(vo.getIdSolicitud(), Constantes.TRUE, Constantes.FALSE, "it.id"));
                    vo.setItinerarioCompletoVoVuelta(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(vo.getIdSolicitud(), Constantes.FALSE, Constantes.FALSE, "it.id"));
                    if (vo.getIdItinerarioIda() > 0) {
                        vo.getItinerarioCompletoVoIda().setEscalas(detalleItinerarioImpl.traerDetalleItinerario(vo.getIdItinerarioIda()));
                    }
                    if (vo.getIdItinerarioVuelta() > 0) {
                        vo.getItinerarioCompletoVoVuelta().setEscalas(detalleItinerarioImpl.traerDetalleItinerario(vo.getIdItinerarioVuelta()));
                    }
                    setSolicitudViajeVO(vo);
                    select = true;
                }

            }
        }
        if (select) {
            if (i == 1) {
                cargarDatosEscalaItinerario();
                PrimeFaces.current().executeScript(";$('#modalItinerario').modal('show');");
            } else {
                FacesUtils.addErrorMessage("Solo debe de seleccionar una Solicitud para poder ver el Itinerario");
            }

        } else {
            FacesUtils.addErrorMessage("Debe de seleccionar una Solicitud para poder ver el Itinerario");
        }

    }

    public boolean cargarDatosEscalaItinerario() {
        Gson gson = new Gson();
        JsonArray a = new JsonArray();
        setListaCiudades(sgRutaTerrestreImpl.traerOigenJson().get(1));
        for (Object[] o : getListaCiudades()) {
            if (getListaCiudades() != null) {
                JsonObject ob = new JsonObject();
                ob.addProperty("value", o[0] != null ? (Integer) o[0] : 0);
                ob.addProperty("label", o[1] != null ? (String) o[1] + "-" + o[2] + "-" + o[3] : "-");
                ob.addProperty("type", "2");
                a.add(ob);
            }
        }
        String origenes = gson.toJson(a);
        PrimeFaces.current().executeScript(";cargarDatos(" + origenes + ",'ciudadesOrigenIda','groupOIda','groupCIda');");
        PrimeFaces.current().executeScript(";cargarDatos(" + origenes + ",'destinosIda','groupDOIda','groupDCIda');");
        PrimeFaces.current().executeScript(";cargarDatos(" + origenes + ",'ciudadesOrigenVuelta','groupOV','groupCV');");
        PrimeFaces.current().executeScript(";cargarDatos(" + origenes + ",'destinosV','groupDOV','groupDCV');");

        if (origenes != null && !origenes.isEmpty()) {
            return Constantes.TRUE;
        } else {
            return Constantes.FALSE;
        }
    }

    public List<SgAerolinea> getAllSgAerolineaList() {
        return sgAerolineaImpl.findAll("nombre", true, false);
    }

    public void addEscalas(boolean ida) {
        List<DetalleItinerarioCompletoVo> listDetalle = new ArrayList<DetalleItinerarioCompletoVo>();
        DetalleItinerarioCompletoVo newDetalle = new DetalleItinerarioCompletoVo();
        String OrigenEsc = "";
        String destinoEsc = "";
        String numVuelo = "";
        int idCdOrigen = 0;
        int idCdDestino = 0;
        Calendar fechaSalidaCompleta = Calendar.getInstance();
        Date fs;
        Calendar fechaRegresoCompleta = Calendar.getInstance();
        Date fr;
        double llegada;
        double salida;
        double tiempoV;
        int aerolinea;
        String nameOrigen = "";
        String nameDestino = "";
        String nameAerolinea = "";
        Long tiempoVuelo;

        if (ida) {
            if (getSolicitudViajeVO().getItinerarioCompletoVoIda().getEscalas() != null) {
                listDetalle = getSolicitudViajeVO().getItinerarioCompletoVoIda().getEscalas();
            }
            OrigenEsc = FacesUtils.getRequestParameter("oriIda");
            destinoEsc = FacesUtils.getRequestParameter("desIda");
            numVuelo = FacesUtils.getRequestParameter("numVI");
            fechaSalidaCompleta.setTime(getFechaSalida());
            fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, getHoraSalida());
            fechaSalidaCompleta.set(Calendar.MINUTE, getMinutoSalida());
            fechaRegresoCompleta.setTime(getFechaRegreso());
            fechaRegresoCompleta.set(Calendar.HOUR_OF_DAY, getHoraRegreso());
            fechaRegresoCompleta.set(Calendar.MINUTE, getMinutoRegreso());
            fs = fechaSalidaCompleta.getTime();
            fr = fechaRegresoCompleta.getTime();
            tiempoVuelo = this.siManejoFechaImpl.getDiffInMinutes(fechaSalidaCompleta, fechaRegresoCompleta);
            tiempoV = (double) (tiempoVuelo.doubleValue() / 60);
            aerolinea = getIdAerolinia();
            nameAerolinea = sgAerolineaImpl.find(aerolinea).getNombre();

            setFechaRegreso(fr);
            setFechaSalida(fs);
        } else {
            if (getSolicitudViajeVO().getItinerarioCompletoVoVuelta().getEscalas() != null) {
                listDetalle = getSolicitudViajeVO().getItinerarioCompletoVoVuelta().getEscalas();
            }
            OrigenEsc = FacesUtils.getRequestParameter("oriVuelta");
            destinoEsc = FacesUtils.getRequestParameter("desV");
            numVuelo = FacesUtils.getRequestParameter("numVV");
            fechaSalidaCompleta.setTime(getFechaSalida2());
            fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, getHoraSalida2());
            fechaSalidaCompleta.set(Calendar.MINUTE, getMinutoSalida2());
            fechaRegresoCompleta.setTime(getFechaRegreso2());
            fechaRegresoCompleta.set(Calendar.HOUR_OF_DAY, getHoraRegreso2());
            fechaRegresoCompleta.set(Calendar.MINUTE, getMinutoRegreso2());
            fs = fechaSalidaCompleta.getTime();
            fr = fechaRegresoCompleta.getTime();
            tiempoVuelo = this.siManejoFechaImpl.getDiffInMinutes(fechaSalidaCompleta, fechaRegresoCompleta);
            tiempoV = (double) (tiempoVuelo.doubleValue() / 60);
            setFechaRegreso2(fr);
            setFechaSalida2(fs);
            aerolinea = getIdAerolinia2();
            nameAerolinea = sgAerolineaImpl.find(aerolinea).getNombre();
        }
        if (!OrigenEsc.equals(destinoEsc)) {
            for (Object[] ob : getListaCiudades()) {
                if (OrigenEsc.equals(ob[1].toString() + "-" + ob[2].toString() + "-" + ob[3].toString())) {
                    idCdOrigen = (Integer) ob[0];
                    nameOrigen = ob[1].toString();
                }
                if (destinoEsc.equals(ob[1].toString() + "-" + ob[2].toString() + "-" + ob[3].toString())) {
                    idCdDestino = (Integer) ob[0];
                    nameDestino = ob[1].toString();
                }
                if (idCdDestino > 0 && idCdOrigen > 0) {
                    break;
                }
            }
            newDetalle.setIdCiudadOrigen(idCdOrigen);
            newDetalle.setIdCiudadDestino(idCdDestino);
            newDetalle.setIdAerolinea(aerolinea);
            newDetalle.setNumeroVuelo(numVuelo);
            newDetalle.setFechaSalida(fs);
            newDetalle.setHoraSalida(fs);
            newDetalle.setFechaLlegada(fr);
            newDetalle.setHoraLlegada(fr);
            newDetalle.setTiempoVuelo(tiempoV);
            newDetalle.setNombreCiudadOrigen(nameOrigen);
            newDetalle.setNombreCiudadDestino(nameDestino);
            newDetalle.setNombreAerolinea(nameAerolinea);
            listDetalle.add(newDetalle);
            if (ida) {
                getSolicitudViajeVO().getItinerarioCompletoVoIda().setEscalas(listDetalle);
                PrimeFaces.current().executeScript(";$('#addItIda').slideToggle();");
            } else {
                getSolicitudViajeVO().getItinerarioCompletoVoVuelta().setEscalas(listDetalle);
                PrimeFaces.current().executeScript(";$('#addItVuelta').slideToggle();");
            }

        } else {
            FacesUtils.addErrorMessage("El origen y el destino no puedenser iguales");
        }

    }

    /**
     * @return the idAerolinia
     */
    public int getIdAerolinia() {
        return idAerolinia;
    }

    /**
     * @param idAerolinia the idAerolinia to set
     */
    public void setIdAerolinia(int idAerolinia) {
        this.idAerolinia = idAerolinia;
    }

    /**
     * @return the listaCiudades
     */
    public List<Object[]> getListaCiudades() {
        return listaCiudades;
    }

    /**
     * @param listaCiudades the listaCiudades to set
     */
    public void setListaCiudades(List<Object[]> listaCiudades) {
        this.listaCiudades = listaCiudades;
    }

    /**
     * @return the fechaSalida2
     */
    public Date getFechaSalida2() {
        return fechaSalida2;
    }

    /**
     * @param fechaSalida2 the fechaSalida2 to set
     */
    public void setFechaSalida2(Date fechaSalida2) {
        this.fechaSalida2 = fechaSalida2;
    }

    /**
     * @return the fechaRegreso2
     */
    public Date getFechaRegreso2() {
        return fechaRegreso2;
    }

    /**
     * @param fechaRegreso2 the fechaRegreso2 to set
     */
    public void setFechaRegreso2(Date fechaRegreso2) {
        this.fechaRegreso2 = fechaRegreso2;
    }

    /**
     * @return the horaSalida2
     */
    public int getHoraSalida2() {
        return horaSalida2;
    }

    /**
     * @param horaSalida2 the horaSalida2 to set
     */
    public void setHoraSalida2(int horaSalida2) {
        this.horaSalida2 = horaSalida2;
    }

    /**
     * @return the horaRegreso2
     */
    public int getHoraRegreso2() {
        return horaRegreso2;
    }

    /**
     * @param horaRegreso2 the horaRegreso2 to set
     */
    public void setHoraRegreso2(int horaRegreso2) {
        this.horaRegreso2 = horaRegreso2;
    }

    /**
     * @return the minutoSalida2
     */
    public int getMinutoSalida2() {
        return minutoSalida2;
    }

    /**
     * @param minutoSalida2 the minutoSalida2 to set
     */
    public void setMinutoSalida2(int minutoSalida2) {
        this.minutoSalida2 = minutoSalida2;
    }

    /**
     * @return the minutoRegreso2
     */
    public int getMinutoRegreso2() {
        return minutoRegreso2;
    }

    /**
     * @param minutoRegreso2 the minutoRegreso2 to set
     */
    public void setMinutoRegreso2(int minutoRegreso2) {
        this.minutoRegreso2 = minutoRegreso2;
    }

    /**
     * @return the idAerolinia2
     */
    public int getIdAerolinia2() {
        return idAerolinia2;
    }

    /**
     * @param idAerolinia2 the idAerolinia2 to set
     */
    public void setIdAerolinia2(int idAerolinia2) {
        this.idAerolinia2 = idAerolinia2;
    }

    public void removerEscala(boolean ida) {
        String delete;
        setJus(new ArrayList<Integer>());
        if (ida) {
            delete = FacesUtils.getRequestParameter("numDeleteI");
            for (DetalleItinerarioCompletoVo d : solicitudViajeVO.getItinerarioCompletoVoIda().getEscalas()) {
                if (delete.equals(d.getNumeroVuelo())) {
                    solicitudViajeVO.getItinerarioCompletoVoIda().getEscalas().remove(d);
                    if (d.getId() != null && d.getId() > 0) {
                        getJus().add(d.getId());
                    }
                    break;
                }
            }
        } else {
            delete = FacesUtils.getRequestParameter("numDeleteV");
            for (DetalleItinerarioCompletoVo d : solicitudViajeVO.getItinerarioCompletoVoVuelta().getEscalas()) {
                if (delete.equals(d.getNumeroVuelo())) {
                    solicitudViajeVO.getItinerarioCompletoVoVuelta().getEscalas().remove(d);
                    if (d.getId() != null && d.getId() > 0) {
                        getJus().add(d.getId());
                    }
                    break;
                }
            }
        }
    }

    public void guardarCambiosEscala() throws ItemUsedBySystemException {
        if (getJus().size() > 0) {
            SgDetalleItinerario det = null;
            for (int i : getJus()) {
                det = detalleItinerarioImpl.find(i);
                detalleItinerarioImpl.delete(det, sesion.getUsuario().getId());
            }
        }
        if (getSolicitudViajeVO().getItinerarioCompletoVoIda() != null && getSolicitudViajeVO().getItinerarioCompletoVoIda().getEscalas().size() > 0) {
            for (DetalleItinerarioCompletoVo vo : getSolicitudViajeVO().getItinerarioCompletoVoIda().getEscalas()) {
                if (vo.getId() == null || vo.getId() == 0) {
                    Calendar cals = Calendar.getInstance();
                    cals.setTime(vo.getHoraSalida());
                    Calendar call = Calendar.getInstance();
                    call.setTime(vo.getHoraLlegada());
                    detalleItinerarioImpl.save(solicitudViajeVO.getItinerarioCompletoVoIda().getId(), vo.getIdAerolinea(),
                            vo.getIdCiudadOrigen(), vo.getIdCiudadDestino(), sesion.getUsuario().getId(), vo.getNumeroVuelo(),
                            vo.getFechaSalida(), cals, vo.getFechaLlegada(), call);
                }
            }
        }
        if (getSolicitudViajeVO().getItinerarioCompletoVoVuelta() != null && getSolicitudViajeVO().getItinerarioCompletoVoVuelta().getEscalas().size() > 0) {
            for (DetalleItinerarioCompletoVo vo : getSolicitudViajeVO().getItinerarioCompletoVoVuelta().getEscalas()) {
                if (vo.getId() == null || vo.getId() == 0) {
                    Calendar cals = Calendar.getInstance();
                    cals.setTime(vo.getHoraSalida());
                    Calendar call = Calendar.getInstance();
                    call.setTime(vo.getHoraLlegada());
                    detalleItinerarioImpl.save(solicitudViajeVO.getItinerarioCompletoVoVuelta().getId(), vo.getIdAerolinea(),
                            vo.getIdCiudadOrigen(), vo.getIdCiudadDestino(), sesion.getUsuario().getId(), vo.getNumeroVuelo(),
                            vo.getFechaSalida(), cals, vo.getFechaLlegada(), call);
                }
            }
        }
        PrimeFaces.current().executeScript(";cerrar();");
        mostrarSolicitudesByAprobar();
    }

    public void notificarItinerario(boolean ida) {
        boolean reg = false;
        for (SolicitudViajeVO vo : getListSolicitudesVoAereas()) {
            if (vo.isSelect()) {
                reg = sgItinerarioImpl.notificaCambioItinerario(vo.getIdSolicitud(), ida);
            }
        }
        if (reg) {
            FacesUtils.addInfoMessage("Se ha enviado el itinerario.");
        }
    }

    /**
     * @return the selectTodo
     */
    public boolean isSelectTodo() {
        return selectTodo;
    }

    /**
     * @param selectTodo the selectTodo to set
     */
    public void setSelectTodo(boolean selectTodo) {
        this.selectTodo = selectTodo;
    }

    public void seleccionoTodo(boolean terrestre) {
        if (terrestre) {
            for (int i = 0; i < getListSolicitudesVo().size(); i++) {
                getListSolicitudesVo().get(i).setSelect(Constantes.TRUE);
            }
        } else {
            for (int i = 0; i < getListSolicitudesVoAereas().size(); i++) {
                getListSolicitudesVoAereas().get(i).setSelect(Constantes.TRUE);
            }
        }
        setActualizar(Constantes.FALSE);
    }

    public void desSelecinarTodo(boolean terrestre) {
        if (terrestre) {
            for (int i = 0; i < getListSolicitudesVo().size(); i++) {
                getListSolicitudesVo().get(i).setSelect(Constantes.FALSE);
            }
        } else {
            for (int i = 0; i < getListSolicitudesVoAereas().size(); i++) {
                getListSolicitudesVoAereas().get(i).setSelect(Constantes.FALSE);
            }
        }
    }

    /**
     * @return the actualizar
     */
    public boolean isActualizar() {
        return actualizar;
    }

    /**
     * @param actualizar the actualizar to set
     */
    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }

    public boolean validarTelefonosByViajero() {
        boolean valida = true;
        for (ViajeroVO vo : getListViajeroVO()) {
            if (!vo.isConfirTel()) {
                valida = Constantes.FALSE;
                break;
            }
        }
        return valida;
    }

    /**
     * @return the editTel
     */
    public boolean isEditTel() {
        return editTel;
    }

    /**
     * @param editTel the editTel to set
     */
    public void setEditTel(boolean editTel) {
        this.editTel = editTel;
    }

    /**
     * @return the newTelefono
     */
    public String getNewTelefono() {
        return newTelefono;
    }

    /**
     * @param newTelefono the newTelefono to set
     */
    public void setNewTelefono(String newTelefono) {
        this.newTelefono = newTelefono;
    }

    /**
     * @return the temporal
     */
    public ViajeroVO getTemporal() {
        return temporal;
    }

    /**
     * @param temporal the temporal to set
     */
    public void setTemporal(ViajeroVO temporal) {
        this.temporal = temporal;
    }

    public void addNewTelefono() {
        String tel = FacesUtils.getRequestParameter("telfonoNew");
        boolean esEmpleado = false;
        if (getTemporal().getIdUsuario() != null && !getTemporal().getIdUsuario().isEmpty()) {
            esEmpleado = true;
        }
        for (ViajeroVO via : getListViajeroVO()) {
            if (esEmpleado) {
                if (via.getIdUsuario() != null && !via.getIdUsuario().isEmpty()) {
                    if (via.getIdUsuario().equals(getTemporal().getIdUsuario())) {
                        via.setTelefono(tel);
                        getTemporal().setTelefono(tel);
                        via.setConfirTel(Constantes.TRUE);
                        break;
                    }
                }
            } else {
                if (via.getIdInvitado() > 0) {
                    if (via.getIdInvitado() == getTemporal().getIdInvitado()) {
                        via.setTelefono(tel);
                        via.setConfirTel(Constantes.TRUE);
                        getTemporal().setTelefono(tel);
                        break;
                    }
                }
            }
        }
        //PrimeFaces.current().executeScript(";reloadTable();");
        PrimeFaces.current().executeScript(";$('#modalAddTel').modal('hide');");
        // setListViajeroVO(getListViajeroBajarVO());
    }

    public void editarTel() {
        //  setEditTel(Constantes.TRUE);
        String user = FacesUtils.getRequestParameter("usuarioTel");
        String inv = FacesUtils.getRequestParameter("invitadoTel");
        //  String tel = FacesUtils.getRequestParameter("telphon");
        for (ViajeroVO vo : getListViajeroVO()) {
            if (user != null && !user.isEmpty() && !user.equals("null")) {
                if (vo.getIdUsuario().equals(user)) {
                    vo.setEditarTel(Constantes.TRUE);
                    setNombre(vo.getUsuario());
                    setNewTelefono(vo.getTelefono() != null ? vo.getTelefono() : 0 + "");
                    setTemporal(vo);
                    break;
                }
            } else {
                if (inv != null) {
                    int idInv = Integer.parseInt(inv);
                    if (vo != null && vo.getIdInvitado() != null && vo.getIdInvitado() == idInv) {
                        vo.setEditarTel(Constantes.TRUE);
                        setNombre(vo.getInvitado());
                        setNewTelefono(vo.getTelefono() != null ? vo.getTelefono() : 0 + "");
                        setTemporal(vo);
                        break;
                    }
                }
            }
        }
        PrimeFaces.current().executeScript(";$(modalAddTel).modal('show');");
    }

    public void confirmarTelefon() {
        String user = FacesUtils.getRequestParameter("usuarioTel");
        //int inv = Integer.parseInt(FacesUtils.getRequestParameter("invitadoTel"));
        String tel = FacesUtils.getRequestParameter("telActual");
        if (tel == null || tel.isEmpty()) {
            tel = FacesUtils.getRequestParameter("telfonoNew");
        }

        if (user == null || user.isEmpty()) {
            user = getTemporal().getIdUsuario();
        }

        if (tel != null && !tel.isEmpty()) {
            for (ViajeroVO vo : getListViajeroVO()) {
                if (user != null && !user.isEmpty()) {
                    if (vo.getIdUsuario().equals(user)) {
                        vo.setTelefono(tel);
                        vo.setConfirTel(Constantes.TRUE);
                        break;
                    }
                } else {
                    if (getTemporal() != null && getTemporal().getIdInvitado() != null && vo != null && vo.getIdInvitado() != null) {
                        if (getTemporal().getIdInvitado().intValue() == vo.getIdInvitado().intValue()) {
                            if (vo.getTelefono() != null && !vo.getTelefono().isEmpty()) {
                                vo.setConfirTel(Constantes.TRUE);
                                vo.setTelefono(tel);
                                break;
                            }
                        }
                    }
                }
            }
            PrimeFaces.current().executeScript(";$(modalAddTel).modal('hide');");
        } else {
            FacesUtils.addErrorMessage("Favor de agregar el telefono");
        }

    }

    public void trarViajerosActuales() {
        setListViajeroVO(getListViajeroBajarVO());
        setListViajeroBajarVO(null);
    }

    public void confirmarUsuarioYTelefono(int idInvitado, String IdUsuario) {
        ViajeroVO newVO = new ViajeroVO();
        SgInvitado i = null;
        Usuario u = null;
        if (idInvitado > 0) {
            i = sgInvitadoImpl.find(idInvitado);
            newVO.setIdInvitado(i.getId());
            newVO.setInvitado(i.getNombre());
            newVO.setEsEmpleado(Constantes.FALSE);
            newVO.setEmpresa(i.getSgEmpresa().getNombre());
            newVO.setEstanciaB(Constantes.FALSE);
            newVO.setTelefono(i.getTelefono());
            setNombre(i.getNombre());
            setNewTelefono(i.getTelefono());
        } else {
            u = usuarioImpl.find(IdUsuario);
            newVO.setIdUsuario(u.getId());
            newVO.setUsuario(u.getNombre());
            newVO.setEsEmpleado(Constantes.TRUE);
            newVO.setGerencia(u.getGerencia().getNombre());
            newVO.setEstanciaB(Constantes.FALSE);
            newVO.setTelefono(u.getTelefono());
            setNombre(u.getNombre());
            setNewTelefono(u.getTelefono());
        }
        setTemporal(newVO);

        PrimeFaces.current().executeScript(";$(modalAddTel).modal('show');");
    }

    public void cargarVehiculo() {
        String idV = FacesUtils.getRequestParameter("completarVehiculo");
        int idVehiculo = 0;
        if (!confirVehiculo) {
            if (idV != null && !idV.isEmpty()) {
                for (Object[] ob : getListVehiculos()) {
                    if (idV.equals(ob[1] + "-" + ob[2] + "-" + ob[3] + "-" + ob[4])) {
                        idVehiculo = Integer.parseInt(ob[0].toString());
                        break;
                    }
                }
                setVehiculoVO(sgVehiculoImpl.buscarVehiculoPorId(idVehiculo));
                setConfirVehiculo(Constantes.TRUE);
            } else {
                FacesUtils.addErrorMessage("Favor de confirmar o selecionar un vehiculo");
            }
        }

    }

    public UsuarioVO buscarUsuario() {
        return usuarioImpl.findByName(empleado);
    }

    public InvitadoVO buscarInvitado(String criteria) {
        return sgInvitadoImpl.buscarInvitado(criteria);
    }

    /**
     * @return the listVehiculos
     */
    public List<Object[]> getListVehiculos() {
        return listVehiculos;
    }

    /**
     * @param listVehiculos the listVehiculos to set
     */
    public void setListVehiculos(List<Object[]> listVehiculos) {
        this.listVehiculos = listVehiculos;
    }

    /**
     * @return the VehiculoActual
     */
    public String getVehiculoActual() {
        return VehiculoActual;
    }

    /**
     * @param VehiculoActual the VehiculoActual to set
     */
    public void setVehiculoActual(String VehiculoActual) {
        this.VehiculoActual = VehiculoActual;
    }

    /**
     * @return the confirVehiculo
     */
    public boolean isConfirVehiculo() {
        return confirVehiculo;
    }

    /**
     * @param confirVehiculo the confirVehiculo to set
     */
    public void setConfirVehiculo(boolean confirVehiculo) {
        this.confirVehiculo = confirVehiculo;
    }

    /**
     * @return the cambiarVehiculo
     */
    public boolean isCambiarVehiculo() {
        return cambiarVehiculo;
    }

    /**
     * @param cambiarVehiculo the cambiarVehiculo to set
     */
    public void setCambiarVehiculo(boolean cambiarVehiculo) {
        this.cambiarVehiculo = cambiarVehiculo;
    }

    /**
     * @return the ubicacion
     */
    public List<SelectItem> getUbicacion() {
        return ubicacion;
    }

    /**
     * @param ubicacion the ubicacion to set
     */
    public void setUbicacion(List<SelectItem> ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<SelectItem> cargarListUbicacion() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            List<SgUbicacion> lu = ubicacionReomte.findAll();
            for (SgUbicacion u : lu) {
                l.add(new SelectItem(u.getId(), u.getNombre()));
            }
        } catch (Exception ex) {
            Logger.getLogger(SolicitudViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the hotelSugerido
     */
    public String getHotelSugerido() {
        return hotelSugerido;
    }

    /**
     * @param hotelSugerido the hotelSugerido to set
     */
    public void setHotelSugerido(String hotelSugerido) {
        this.hotelSugerido = hotelSugerido;
    }

    /**
     * @return the idUbicacion
     */
    public int getIdUbicacion() {
        return idUbicacion;
    }

    /**
     * @param idUbicacion the idUbicacion to set
     */
    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    /**
     * @return the countSVT
     */
    public int getCountSVT() {
        return countSVT;
    }

    /**
     * @param countSVT the countSVT to set
     */
    public void setCountSVT(int countSVT) {
        this.countSVT = countSVT;
    }

    /**
     * @return the countSVA
     */
    public int getCountSVA() {
        return countSVA;
    }

    /**
     * @param countSVA the countSVA to set
     */
    public void setCountSVA(int countSVA) {
        this.countSVA = countSVA;
    }

    /**
     * @return the listEmpresaByUser
     */
    public List<SelectItem> getListEmpresaByUser() {
        return listEmpresaByUser;
    }

    /**
     * @param listEmpresaByUser the listEmpresaByUser to set
     */
    public void setListEmpresaByUser(List<SelectItem> listEmpresaByUser) {
        this.listEmpresaByUser = listEmpresaByUser;
    }

    public List<SelectItem> cargarListaEmpresas() {

        return companiaImpl.traerCompaniasByUsuario(sesion.getUsuario().getId());
    }

    /**
     * @return the idCampoActual
     */
    public int getIdCampoActual() {
        return idCampoActual;
    }

    /**
     * @param idCampoActual the idCampoActual to set
     */
    public void setIdCampoActual(int idCampoActual) {
        this.idCampoActual = idCampoActual;
    }

    /**
     * @return the rfcEmpresaSeleccionada
     */
    public String getRfcEmpresaSeleccionada() {
        return rfcEmpresaSeleccionada;
    }

    /**
     * @param rfcEmpresaSeleccionada the rfcEmpresaSeleccionada to set
     */
    public void setRfcEmpresaSeleccionada(String rfcEmpresaSeleccionada) {
        this.rfcEmpresaSeleccionada = rfcEmpresaSeleccionada;
    }

    /**
     * @return the campoUsuarioPuestoVo
     */
    public CampoUsuarioPuestoVo getCampoUsuarioPuestoVo() {
        return campoUsuarioPuestoVo;
    }

    /**
     * @param campoUsuarioPuestoVo the campoUsuarioPuestoVo to set
     */
    public void setCampoUsuarioPuestoVo(CampoUsuarioPuestoVo campoUsuarioPuestoVo) {
        this.campoUsuarioPuestoVo = campoUsuarioPuestoVo;
    }

    public void fechasFestivas() {
        int year = siManejoFechaImpl.traerAnioActual();
        List<SiDiasAsueto> lda = asuetoImpl.diasAsuetoByYear(year);
        String fechasAsueto = "";
        Gson gson = new Gson();
        JsonArray a = new JsonArray();
        String fecha = "";
        if (lda != null) {
            for (SiDiasAsueto da : lda) {
                JsonObject ob = new JsonObject();
                fecha = Constantes.FMT_yyyy_MM_dd.format(da.getFecha());
                ob.addProperty("value", fecha);
                a.add(ob);
            }
            fechasAsueto = gson.toJson(a);
            PrimeFaces.current().executeScript(";cargarFechas(" + fechasAsueto + ");");
        }
    }

    /**
     * @return the listDias
     */
    public List<SiDiasAsueto> getListDias() {
        return listDias;
    }

    /**
     * @param listDias the listDias to set
     */
    public void setListDias(List<SiDiasAsueto> listDias) {
        this.listDias = listDias;
    }

    public void mostrarSolicitudesCanceladas() {
        try {

            setListSolicitudesVo(
                    sgSolicitudViajeImpl.traerSolicitudesTerrestreByEstatus(
                            Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO, Constantes.CERO, null, " AND s.fecha_salida >= CAST('NOW' AS DATE)"));

            setCountSVT(getListSolicitudesVo().size());
            setActualizar(Constantes.FALSE);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    /**
     * @return the empleado
     */
    public String getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
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
}
