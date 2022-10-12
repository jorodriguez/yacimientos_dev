/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.bean.model;

import com.newrelic.api.agent.Trace;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.modelo.CoNoticia;
import sia.modelo.Estatus;
import sia.modelo.Gerencia;
import sia.modelo.Moneda;
import sia.modelo.SgAerolinea;
import sia.modelo.SgAsignarVehiculo;
import sia.modelo.SgCambioItinerario;
import sia.modelo.SgDetalleItinerario;
import sia.modelo.SgEstatusAprobacion;
import sia.modelo.SgInvitado;
import sia.modelo.SgItinerario;
import sia.modelo.SgKilometraje;
import sia.modelo.SgLugar;
import sia.modelo.SgMotivo;
import sia.modelo.SgOficina;
import sia.modelo.SgRutaTerrestre;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgTipoSolicitudViaje;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.SgVehiculo;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeCiudad;
import sia.modelo.SgViajeVehiculo;
import sia.modelo.SgViajero;
import sia.modelo.SgViajeroSiMovimiento;
import sia.modelo.SiAdjunto;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.CambioItinerarioVO;
import sia.modelo.sgl.viaje.vo.DetalleItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.MotivoRetrasoVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.TipoSolicitudTipoEspecificoVO;
import sia.modelo.sgl.viaje.vo.UsuarioRolGerenciaVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeDestinoVo;
import sia.modelo.sgl.viaje.vo.ViajeLugarVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.EstatusAprobacionVO;
import sia.modelo.sgl.vo.SgGastoViajeVO;
import sia.modelo.sgl.vo.SiCiudadVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoCompartidaImpl;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgKilometrajeImpl;
import sia.servicios.sgl.impl.SgMotivoImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sgl.impl.SgTipoSolicitudViajeImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgViajeCiudadImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.vehiculo.impl.SgAsignarVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgLicenciaImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgAerolineaImpl;
import sia.servicios.sgl.viaje.impl.SgCadenaNegacionImpl;
import sia.servicios.sgl.viaje.impl.SgCambioItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaCiudadImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgGastoViajeImpl;
import sia.servicios.sgl.viaje.impl.SgItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgLugarImpl;
import sia.servicios.sgl.viaje.impl.SgMotivoRetrasoImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeSiMovimientoImpl;
import sia.servicios.sgl.viaje.impl.SgTipoSolTipoEspImpl;
import sia.servicios.sgl.viaje.impl.SgUsuarioRolGerenciaImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeKilometrajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeLugarImpl;
import sia.servicios.sgl.viaje.impl.SgViajeVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroSiMovimientoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.SoporteProveedor;
import sia.util.UtilLog4j;
import sia.util.comparators.ComparatorForEstatusAprobacionVo;
import sia.util.comparators.ComparatorForViajeroVo;

/**
 *
 * @author mluis
 */

/*
 * @Named @CnversationScoped
 */
@Named(value = "viajeBeanModel")

public class ViajeBeanModel implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    
    //Sistema
    @Inject
    private Sesion sesion;
    /*
     * @Inject private Sesion sesion; @Inject private Conversation conversation;
     * @Inject private ConversationsManager conversationsManager;
     *
     */
    @ManagedProperty(value = "#{soporteProveedor}")
    private SoporteProveedor soporteProveedor;
    //Beans
    /*
     * @Inject private EstanciaBeanModel estanciaBeanModel; //Servicios @Inject
     * private SoporteProveedor soporteProveedorService;
     */
    @Inject
    private SgEstatusAprobacionImpl estatusAprobacionService;
    @Inject
    private SgCadenaNegacionImpl cadenaNegacionService;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private SgOficinaImpl oficinaService;
    @Inject
    private SgMotivoImpl motivoService;
    @Inject
    private SgTipoImpl sgTipoImpl;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgTipoSolicitudViajeImpl sgTipoSolicitudViajeImpl;
    @Inject
    private SgSolicitudViajeImpl solicitudViajeService;
    @Inject
    private SgViajeroImpl sgViajeroImpl;
    @Inject
    private SgVehiculoImpl sgVehiculoImpl;
    @Inject
    private SgAsignarVehiculoImpl sgAsignarVehiculoImpl;
    @Inject
    private SgMotivoRetrasoImpl sgMotivoRetrasoImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreImpl;
    @Inject
    private SgLicenciaImpl sgLicenciaImpl;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionImpl;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SgViajeVehiculoImpl sgViajeVehiculoImpl;
    @Inject
    private SgLugarImpl sgLugarImpl;
    @Inject
    private SiCiudadImpl siCiudadImpl;
    @Inject
    private SgItinerarioImpl sgItinerarioImpl;
    @Inject
    private SgDetalleItinerarioImpl sgDetalleItinerarioImpl;
    @Inject
    private SgAerolineaImpl sgAerolineaImpl;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaImpl;
    @Inject
    private SgGastoViajeImpl sgGastoViajeImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private SgViajeroSiMovimientoImpl sgViajeroSiMovimientoImpl;
    @Inject
    private SgSolicitudViajeSiMovimientoImpl sgSolicitudViajeSiMovimientoImpl;
    @Inject
    private SgInvitadoImpl sgInvitadoImpl;
    @Inject
    private SgKilometrajeImpl sgKilometrajeImpl;
    @Inject
    private SgViajeKilometrajeImpl sgViajeKilometrajeImpl;
    @Inject
    private SgViajeCiudadImpl sgViajeCiudadImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private ApCampoImpl apCampoImpl;
    @Inject
    private SgUsuarioRolGerenciaImpl sgUsuarioRolGerenciaImpl;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoImpl;
    @Inject
    private SgTipoSolTipoEspImpl sgTipoSolTipoEspImpl;
    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private SgCambioItinerarioImpl sgCambioItinerarioImpl;
    @Inject
    private SgDetalleRutaCiudadImpl sgDetalleRutaCiudadImpl;
    @Inject
    private SgViajeLugarImpl sgViajeLugarImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaImpl;
    @Inject
    private CoCompartidaImpl coCompartidaImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;

    //Entidades
    private int opcionMenu = 1;
    private String opcionGeneral = "";
    //   private SgSolicitudViaje solicitudViaje;
    private SgViaje sgViaje;
    private SgViaje sgViajeRegreso;
    private SgAsignarVehiculo sgAsignarVehiculo;
    private SgViajero sgViajero; //usado tambien en la parte de bitacora
//    private Usuario usuario;
    private SgEstatusAprobacion estatusAprobacion;
    //private SgInvitado sgInvitado;
    private InvitadoVO invitadoVO;
    private SgSolicitudEstanciaVo solicitudEstanciaVo;
    private SgViajeVehiculo sgViajeVehiculo;
    //private SgMotivoRetraso sgMotivoRetrasoViaje;
    private MotivoRetrasoVO motivoRetrasoVO;
    private SgLugar sgLugar;
    private Gerencia gerencia;
//    private SgItinerario sgItinerario;
    //  private SgItinerario sgItinerarioVuelta;
    private ItinerarioCompletoVo itinerarioCompletoVoIda;
    private ItinerarioCompletoVo itinerarioCompletoVoVuelta;
    private SgDetalleItinerario sgDetalleItinerario;
    private SgRutaTerrestre sgRutaTerrestre;
    private Estatus estatusDevolverPorGerenteArea;
    private SiMovimiento siMovimiento;
    private SiMovimiento siMovimientoViaje;
    private SgViajeCiudad sgViajeCiudad;
    private ViajeDestinoVo viajeDestinoVo;
    //Clases
    private LicenciaVo licenciaVo;
    private UsuarioVO usuarioVO;
    private ViajeroVO viajeroVO;
    private ViajeVO viajeVO;
    private SolicitudViajeVO solicitudViajeVO;
    private ViajeLugarVO viajeLugarVO;
    private EstatusAprobacionVO estatusAprobacionVO; //Contiene la misma informacion de una solicitud anexando datos de un estatus
    private EstatusAprobacionVO estatusActualVO;
    private SiCiudadVO siCiudadVOOrigen;
    private SiCiudadVO siCiudadVODestino;
    private SgGastoViajeVO sgGastoViajeVO;
    private SemaforoVo semaforoVo;
    private GerenciaVo gerenciaVo;
    private VehiculoVO vehiculoVO;
    private HtmlInputHidden inputHidden;
    private BigDecimal bigDecimal = new BigDecimal("0.00");
    private MathContext mc = new MathContext(6, RoundingMode.HALF_UP);
    private String cadena; //Usado también para guardar el SgInvitado en el Motivo de retraso de la Solicitud de Viaje y para guardar la SiCiudad de Origen de un Viaje aéreo
    private String mensaje; //Usado también para guardar la SiCiudad de destino de un Viaje aéreo,Usado tambien en el panel de busqueda--
    //Usado tambien para la justificacion de la solicitud del semaforo
    private String invitado; //Usado para cambiar de tab en Bandeja de Solicitudes de Viaje
    private String origenAereo; //Usado: a)optionRangoSiCiudadOrigen en Crear/Modificar Escala
    private String destinoAereo; //Usado: a)optionRangoSiCiudadDestino en Crear/Modificar Escala
    private String opcionSeleccionada = ""; //Usado: a)Guardar el mail del SgInvitado en el Motivo de retraso b) Panel de busqueda
    private String motivoCancelacion = ""; //Usado: a)Observación en Gastos de Viaje
    private String fechaSalida;
    private String fechaRegreso;
    private String inicioEstancia = "01/01/1970";
    private String finEstancia = "01/01/1970";
    private String tipoViaje; // Se maneja para saber si es de ida o de regreso
    private String selectedTab;
    private String numeroVuelo;
    private String telefono;
    private String operacion;
    private boolean viajeDirecto = false;
    //Colecciones
    private List<SelectItem> listaVehiculo;
    private List listaCasosIncumplidos;
    private List<SelectItem> listaUsuariosAlta; //Usado también para las SiCiudad de Destino al crear las Escalas
    private List<SelectItem> listaInvitados; //Usado también para las SiCiudad de Origen al crear las Escala
    private List<ViajeroVO> listaViajerosVO = null;
    private List<ViajeroVO> listaQuedados = null; // PAra los quedados de los viajes
    private Map<Integer, Boolean> filaSeleccionada = new HashMap<Integer, Boolean>();
    private Map<SgSolicitudEstanciaVo, List<ViajeroVO>> mapViajeros = new HashMap<SgSolicitudEstanciaVo, List<ViajeroVO>>();
    private DataModel dataModel; //Usado: a)Almacenar los Viajeros de un Viaje de ida en los Gastos
    private DataModel dataModelDetalle; //Usado en detalle de itinerario
    private DataModel dataModelItinerarioDetalle; //Usado en detalle de itinerario
    private DataModel lista; //Usado para almacenar el detalle de los Itinerarios de Vuelta b)Almacenar los Gastos de un Viaje
    private DataModel dataModelSolicitudesPorProbar; //Usado a) Viajeros de un Viaje en quitar Viajero en Por Salir
    private DataModel dataModelOpciones; //usado tambien en noticias---
    private DataModel dataModelViajeros; //Usado: a)Almacenar los Viajeros de un Viaje de regreso en los Gastos
    private DataModel dataModelSolcitudesTemporales; //ocupado tambien en Detalle de solicitud, para mostrar los estatus aprobados
    private DataModel dataModelViajerosDetalle; //ocupado tambien en el detalle de la solicutud
    //Primitivos
    private boolean flag; //Usado también en Solicitud de Viaje para saber si se requiere Estancia o no .... Tambien usado en noticia...tambien usado en Detalle de solitud
    private boolean viajeAereo;
    private boolean pop = false; //Usado: a)Editar mail del SgInvitado en el Motivo de Retraso Terrestre
    private boolean popQuitar = false; //Usado: Para saber si se requiere un Motivo de Retraso o no
    private boolean mostrarPanel = false;
    private boolean detenerViaje = false; //Usado para verificar las fechas de salida de los viajeros
    private boolean popup = false; //Usado para saber si un Itinerario es de ida o vuelta en el Detalle del Itinerario
    private boolean estancia = false; //Usado para mostrar o no el kilometraje al agregar gasto
    private boolean mostrarAprobar = false;//controla que solo muestre solicitudes por aprobar
    private boolean mostrarAutorizar = false;//controla que solo muestre solicitudes por autorizar
    private boolean mostrarVistoBueno = false; //controla que solo muestre solicitudes por dar visto bueno
    private boolean mostrarjustificar = false; //controla que solo muestre solicitudes por justificar
    private boolean mostrarJustificarDireccion = false; //controla que solo muestre solicitudes por justificar
    private boolean solicitudFinalizada = false;
    private boolean verDetallePop = false; //Usado para mostrar o no el kilometraje al agregar gasto
    private boolean detallePop = false;
    private boolean subirArchivoPop; //Usado para controlar si algún viajero requiere estancia al solicitar una Solicitud de Viaje
    private boolean modificar = false; //Usado: a)Gastos Viaje para saber si se puede o no agregar gasto al viaje, tambien en modificar las fechas de estancia al solicitar viaje
    private boolean cancelarPop = false;
    private boolean popRegresarViaje = false;
    private boolean agregarResponsable = false; //Usado a)Agregar Viajeros desde Menú Agregar Viajeros b)Para saber si se deberán mostrar los mensajes de aprobar o no
    private boolean viajeFueraOficina;
    private boolean popAddViajero = false;
    private boolean addArchivo = false;
    private int id; //Usado para los Tipos Específicos para agregar Viajeros a la SV. También es usado para seleccionar SgAerolínea en createDetalleItinerario.xhtml
    private int idSolicitud;
    private int idOficina; //Usado: a)Moneda en Gastos de Viaje
    private int idMotivo; //Usado: a)Como SgTipo en Gastos de Viaje
    private int idGerencia; //También usado para el SgLugar en el Motivo de retraso de la Solicitud de Viaje
    private int idVehiculo; //Usado también para Oficina Origen al crear la Solicitud de Viaje
    private int idEstatusActivo; //Usado para buscar un Viaje; es el idEstatusUno
    private int idTipoSolicitudViaje; //Usado para buscar un Viaje; es el idEstatusDos
    private int idSiOperacion; //Usado: a)SgTipoEspecifico en Gastos de Viaje
    private int tamanioLista; //Usado: a)Kilometraje en Gastos de Viaje b)idSiCiudadOrigen en Crear/Modificar Escala
    private int idRuta; //Usado también para el SgLugar del Motivo de retraso
    private int idNoticia; //Usado: a)idSiCiudadDestino en Crear/Modificar Escala
    private int horaSalida;
    private int horaRegreso;
    private int minutoSalida;
    private int minutoRegreso;
    private int opcionViaje = 0; //utilizado para el control de tab's en el panel de buscar
    private Integer idUrl;
    private int tipoDestino;
    private String redondo;
    private int cambiarItinerario;
    //
    private int idCampo;
    private int idInvitado;

    /**
     * Creates a new instance of ViajeBeanModel
     */
    public ViajeBeanModel() {
    }

    public String convertirFechaString(Date fecha) {
	return siManejoFechaImpl.convertirFechaStringddMMyyyy(fecha);
    }

    public Date convertirStringFecha(String fecha) {
	return siManejoFechaImpl.convertirStringFechaddMMyyyy(fecha);
    }

    public String convertirHoraString(Date fecha) {
	return siManejoFechaImpl.convertirHoraStringHHmmss(fecha);
    }

    public String convertirHoraStringhmma(Date fecha) {
	return siManejoFechaImpl.convertirHoraStringhmma(fecha);
    }

    public Usuario buscarUsuario() {
	return usuarioImpl.find(getInvitado());
    }

    @PostConstruct
    public void iniciar() {
	limpiar();
    }

    public void limpiar() {
//        setLista(null);
	setMostrarPanel(false);
	setModificar(false);
	setPop(false);
	setPopQuitar(false);
	setVerDetallePop(false);
	setPopup(false);
	setDetenerViaje(false);
	setCancelarPop(false);
	setViajeVO(null);
	setSgViaje(null);
	setIdRuta(-1);
	setIdVehiculo(-1);
	setInvitado("");
	setLicenciaVo(null);
	setSgAsignarVehiculo(null);
	setSgViajeVehiculo(null);
	setItinerarioCompletoVoIda(null);
	setSgRutaTerrestre(null);
	setDataModelSolicitudesPorProbar(null);
	setHoraSalida(0);
	setHoraRegreso(0);
	setDataModelViajeros(null);
	setCadena("");
    }

    public void iniciarConversacion() {
	if (sesion.getIdRol() == 10 || sesion.getIdRol() == 7 || sesion.getIdRol() == 9) {
	    setOpcionViaje(7);
	    setOpcionMenu(7);
	} else {
	    setOpcionViaje(1);
	    setOpcionMenu(1);
	}
	setCadena(null);
	setViajeDestinoVo(null);
	UtilLog4j.log.info(this, "optionViaje: " + getOpcionViaje());
    }

    public void controlaPopUpFalso(String llave) {
	sesion.getControladorPopups().put(llave, Boolean.FALSE);
    }

    public void controlaPopUpTrue(String llave) {
	sesion.getControladorPopups().put(llave, Boolean.TRUE);
    }

    @Trace
    public void llenarListasolicitudes() {
	setDataModel(new ListDataModel(getAllSgSolicitudViaje()));
    }

    public boolean devolverEstadoPop(String llave) {
	return sesion.getControladorPopups().get(llave);
    }

    public void beginConversationSolicitudViaje() {
	//   this.conversationsManager.finalizeAllConversations();
	//     this.conversationsManager.beginConversation(this.conversation, Constantes.CONVERSACION_SOLICITUD_VIAJE);
	//  UtilLog4j.log.info(this, "Iniciada conversación: " + "Solicitud Viaje (" + this.getConversation().getId() + ")");
	//Limpiando variables por si acaso
	this.setCadena(null);
	this.setMensaje(null);
	this.setIdGerencia(-1);
	this.setIdOficina(-1);
	this.setIdVehiculo(-1);
	this.setIdMotivo(-1);
	this.setIdTipoSolicitudViaje(-1);
	setIdSiOperacion(-1);
	this.setViajeAereo(false);
	this.setSiCiudadVOOrigen(null);
	this.setSiCiudadVODestino(null);
	this.setDestinoAereo(null);
	this.setDataModel(null);
	setDataModelViajeros(null);
	this.setLista(null);
	this.setListaInvitados(null);
	this.setListaUsuariosAlta(null);
	setSgViaje(null);
	setSgViajeRegreso(null);
	setTamanioLista(-1);
	setMotivoRetrasoVO(null);
	setSgViajeCiudad(null);
    }

    public void beginConversationAprobaciones() {
	//     this.conversationsManager.finalizeAllConversations();
	//     this.conversationsManager.beginConversation(this.conversation, "APROBACIONES");
	//Limpiando variables
	setDataModel(null);
	setLista(null);
	setDataModelViajeros(null);
	this.setDataModelViajeros(null);
//        setGerente(false);
    }

    public void beginConversationAutorizaciones() {
	//    this.conversationsManager.finalizeAllConversations();
	//    this.conversationsManager.beginConversation(this.conversation, "AUTORIZACIONES");
	this.setDataModelViajeros(null);
    }

    public void beginConversationGastoViaje() {
	//  this.conversationsManager.finalizeAllConversations();
	//    this.conversationsManager.beginConversation(this.conversation, Constantes.CONVERSACION_GASTO_VIAJE);
	//Limpiando variables
	setDataModel(null);
	setLista(null);
	setDataModelViajeros(null);
	setCadena(null);
	setDataModel(null);
	setSgViaje(null);
	setViajeVO(null);
	setMotivoCancelacion(null);
	setViajeVO(null);
	setSgViaje(null);
	setSgViajeRegreso(null);
	setVehiculoVO(null);
	setModificar(false);
	setAgregarResponsable(false);
	setPopup(false);
	setVerDetallePop(false);
	setDetallePop(false);
	setIdMotivo(-1);
	setTamanioLista(0);
    }

    public void beginConversationSearchSolicitudViaje() {
	//  this.conversationsManager.finalizeAllConversations();
	//    this.conversationsManager.beginConversation(this.conversation, Constantes.CONVERSACION_BUSCAR_SOLICITUD_VIAJE);
	//Limpiando variables
	setDataModel(null);
	setLista(null);
	setDataModelViajeros(null);
	setDetallePop(false);
	setAgregarResponsable(false);
	setCadena(null);
	setSolicitudViajeVO(null);
	setMotivoRetrasoVO(null);
    }

    public void iniciarConversacionBitacoraViaje() {
	//    conversationsManager.finalizeAllConversations();
	//     this.conversationsManager.beginConversation(conversation, "BITACORA_VIAJE");
    }

    public Usuario findUsuarioById(String idUsuario) {
	return this.usuarioImpl.find(idUsuario);
    }

////    public void isUsuarioInSessionIsGerente() {
////        setGerente(this.gerenciaImpl.isUsuarioResponsableForAnyGerencia(sesion.getUsuario().getApCampo().getId(), this.sesion.getUsuario().getId(), false));
////        UtilLog4j.log.info(this, "Usuario gerente: " + isGerente());
////    }
    public int validaFechaAprobar() {
	return siManejoFechaImpl.compare(getSolicitudViajeVO().getFechaSalida(), new Date());
    }

    public boolean validaHoraSalida() {
	return siManejoFechaImpl.validaHoraMinima(getSolicitudViajeVO().getHoraSalida(), getSemaforoVo().getHoraMinimaRuta());
    }

    public boolean validaHoraRegreso() {
	return siManejoFechaImpl.validaHoraMaxima(getSolicitudViajeVO().getHoraRegreso(), getSemaforoVo().getHoraMaximaRuta());
    }

    public boolean validateFirstDateIsAfterSecondDate(Calendar firstDate, Calendar secondDate, boolean withTime) {
	boolean firstDateIsAfterSecondDate = (this.siManejoFechaImpl.compare(firstDate, secondDate, withTime) == 1 ? true : false);
	UtilLog4j.log.info(this, "validateFirstDateIsAfterSecondDate(): " + firstDateIsAfterSecondDate);
	return firstDateIsAfterSecondDate;
    }

    public SgSolicitudViaje findSgSolicitudViajeByCodigo(String codigo) {
	return this.solicitudViajeService.findByCode(codigo);
    }

    public ViajeVO searchViajeVoByCodigo(String codigo) {
	return this.sgViajeImpl.findByCodigo(codigo);
    }

    public SgViaje searchViajeByCodigo(String codigo) {
	return this.sgViajeImpl.findCodigo(codigo);
    }

    public ViajeVO buscarViajePorCodigo() {
	return this.sgViajeImpl.buscarPorCodigo(getCadena());
    }

    public SgViaje searchSgViajeVuelta(int idSgViajeIda) {
	return sgViajeImpl.findSgViajeVuelta(idSgViajeIda);
    }

    public SgSolicitudViaje searchSgSolicitudViajeByCodigo(String codigo) {
	return this.solicitudViajeService.findByCode(codigo);
    }

    public UsuarioVO convertUsuarioToUsuarioVo(Usuario u) {
	return this.usuarioImpl.convertToUsuarioVo(u);
    }

    public Usuario getResponsableByGerencia(int idGerencia) {
	return this.gerenciaImpl.getResponsableByApCampoAndGerencia(Constantes.AP_CAMPO_DEFAULT, idGerencia, false);
    }

    public SiMovimiento buscarCancelacionSolicitudViaje(Integer idSolicitudViaje) {
	this.setSiMovimiento(sgSolicitudViajeSiMovimientoImpl.findMotivoCancelacion(idSolicitudViaje));
	return getSiMovimiento();
    }

    //buscar el motivo de cancelacion de viaje
    public SiMovimiento buscarCancelacionViaje(Integer idViaje) {
	this.setSiMovimientoViaje(sgViajeImpl.findMotivoCancelacion(idViaje));
	return getSiMovimientoViaje();
    }

    public void buscarViajeCiudad(int idSgTipoSolicitudViaje) {
	setViajeDestinoVo(this.sgViajeCiudadImpl.findDestinoSolicitudViaje(idSgTipoSolicitudViaje));
    }

    public DataModel traerViajesApartirSolicitud(Integer idSolicitud) {
	setDataModelOpciones(new ListDataModel(sgViajeroImpl.getAllViajesBySolicitud(idSolicitud)));
	return getDataModelOpciones();
    }

    public DataModel traerSolicitudApartirViaje(Integer idViaje) {
	setDataModelOpciones(new ListDataModel(sgViajeroImpl.getAllSolicitudesByViaje(idViaje)));
	return getDataModelOpciones();
    }

    public SgSolicitudViaje findSgSolicitudViajeById(int id) {
	return this.solicitudViajeService.find(id);
    }

    /**
     * Valida que una fecha no sea anterior a la fecha y hora actuales.
     *
     * @param fecha - La fecha que se va a comparar contra la fecha actual
     * @return - 'true' si la fecha No es anterior a hoy y 'false' si la fecha
     * es anterior a hoy
     */
    public boolean validateDateIsAfterNow(Calendar fecha) {
	Calendar cAhora = Calendar.getInstance();
	UtilLog4j.log.info(this, "ViajeBeanModel.validateDateIsAfterNow(): " + (this.siManejoFechaImpl.compare(fecha, cAhora, true) == 1));
	return (this.siManejoFechaImpl.compare(fecha, cAhora, true) == 1);
    }

    public boolean validateDateIsAfterYesterday(Calendar fecha) {
	UtilLog4j.log.info(this, "ViajeBeanModel.validateDateIsAfterYesterday()");
	Calendar cYesterday = Calendar.getInstance();
	cYesterday.add(Calendar.DAY_OF_YEAR, -1);
	cYesterday = this.siManejoFechaImpl.cleanCalendar(cYesterday);
	UtilLog4j.log.info(this, "ViajeBeanModel.validateDateIsAfterYesterday(): " + (this.siManejoFechaImpl.compare(fecha, cYesterday, false) == 1));
	return (this.siManejoFechaImpl.compare(fecha, cYesterday, false) == 1);
    }

    /**
     * Valida que 'fechaRegreso' sea mayor que 'fechaSalida'. No valida los
     * tiempos
     *
     * @param firstDate
     * @param secondDate
     * @return - 'true' si la 'fechaSalida' es anterior a 'fechaRegreso'
     */
    public boolean validateFirstDateIsAfterOrEqualSecondDate(Calendar firstDate, Calendar secondDate) {
	UtilLog4j.log.info(this, "ViajeBeanModel.validateFirstDateIsAfterOrEqualSecondDate(): " + !(this.siManejoFechaImpl.compare(firstDate, secondDate, false) == -1));
	return !(this.siManejoFechaImpl.compare(firstDate, secondDate, false) == -1);
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
	UtilLog4j.log.info(this, "ViajeBeanModel.validateFechaRegresoIsAfterFechaSalidaWithTime(): " + (this.siManejoFechaImpl.compare(fechaRegreso, fechaSalida, true) == 1));
	return (1 == this.siManejoFechaImpl.compare(fechaRegreso, fechaSalida, true));
    }

    public boolean validateDateIsToday(Calendar fecha) {
	UtilLog4j.log.info(this, "ViajeBeanModel.validateDateIsToday(): " + (this.siManejoFechaImpl.dayIsToday(fecha)));
	return this.siManejoFechaImpl.dayIsToday(fecha);
    }

    public boolean validateDateIsToday(Date fecha) {
	return this.siManejoFechaImpl.dayIsToday(fecha);
    }

    /**
     * Si 'date' es anterior al día de hoy regresa 'true', si no, 'false'
     *
     * @return
     */
    public boolean validateDateIsBeforeToday() {
	return ((this.siManejoFechaImpl.compare(getSolicitudViajeVO().getFechaSalida(), new Date()) == -1));
    }

    public boolean validateFirstDateIsLessSecondDate(Date firstDate, Date secondDate) {
	return ((this.siManejoFechaImpl.compare(firstDate, secondDate) == -1));
    }

    public boolean validateDayIsSame(Date firstDate, Date secondDate) {
	return (this.siManejoFechaImpl.dayIsSame(firstDate, secondDate));
    }

    public boolean validateHorasAnticipaciónViajeAereo(int idTipoSolicitudViaje, Date fechaSalida) {
	UtilLog4j.log.info(this, "validateHorasAnticipaciónViajeAéreo");
	SgTipoSolicitudViaje tipoSolicitudViaje = null;
	if (idTipoSolicitudViaje != -1) {
	    tipoSolicitudViaje = this.sgTipoSolicitudViajeImpl.find(idTipoSolicitudViaje);

	    int horasAnticipacion = tipoSolicitudViaje.getHorasAnticipacion().intValue();

	    if (horasAnticipacion == 0) {
		return false;
	    } else {
		int diasDiff = this.siManejoFechaImpl.dias(fechaSalida, new Date()); //Aquí no va un new Date() sino la fecha de Salida de la Solicitud de Viaje
		int horasDiff = diasDiff * 24;
		return ((horasDiff >= horasAnticipacion) ? false : true);
	    }

	} else {
	    UtilLog4j.log.info(this, "regresará false");
	    return false;
	}
    }

    public boolean solicitudViajeHaveViajeros() throws SIAException, Exception {
	List<ViajeroVO> viajerosBySolicitud = this.sgViajeroImpl.getAllViajerosList(getSolicitudViajeVO().getIdSolicitud());
	return !viajerosBySolicitud.isEmpty();
    }

    public List<UsuarioVO> getAllResponsableGerenciaForRolAsistenteDireccion() {

	List<Usuario> responsables = new ArrayList<Usuario>();
	List<UsuarioVO> responsablesVo = new ArrayList<UsuarioVO>();
	List<UsuarioRolGerenciaVo> gerencias = this.sgUsuarioRolGerenciaImpl.traerGerenciaPorRol(this.sesion.getUsuario().getId(), this.sesion.getIdRol());
	List<CampoVo> campos = findAllApCampo();

	for (UsuarioRolGerenciaVo voGerencia : gerencias) {
	    for (CampoVo voCampo : campos) {
		Usuario u = this.gerenciaImpl.getResponsableByApCampoAndGerencia(voCampo.getId(), voGerencia.getIdGerencia(), false);
		if (u != null && !responsables.contains(u)) {
		    responsables.add(u);
		}
	    }
	}

	if (responsables != null) {
	    for (Usuario usr : responsables) {
		responsablesVo.add(convertUsuarioToUsuarioVo(usr));
	    }
	}
	return responsablesVo;
    }

    public int buscarSessionRolId() {
	return sesion.getIdRol();
    }

    public List<SgOficina> getOficinaList() {
	return this.oficinaService.traerOficina(null, Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);
    }

    public List<SelectItem> selectItemOficina() {
	List<SgOficina> oficinaList = null;
	List<SelectItem> listSelectItem = null;
	oficinaList = getOficinaList();
	if (!oficinaList.isEmpty()) {
	    listSelectItem = new ArrayList<SelectItem>();
	    for (SgOficina oficina : oficinaList) { //No incluir la Oficina Actual
		if (sesion.getOficinaActual() != null && (oficina.getId().intValue() == sesion.getOficinaActual().getId().intValue())) { //No mostrar la Oficina actual
		    continue;
		}
		listSelectItem.add(new SelectItem(oficina.getId(), oficina.getNombre()));
	    }
	}
	return listSelectItem;
    }

    public List<SgMotivo> getMotivoList() {
	return this.motivoService.getAllMotivos(Constantes.BOOLEAN_FALSE);
    }

    public List<Moneda> getAllMonedaList() {
	return this.monedaImpl.findAll();
    }

    public List<CampoVo> findAllApCampo() {
	return this.apCampoImpl.getAllField();
    }

    public Usuario getResponsableByApCampoAndGerencia(int idApCampo, int idGerencia) {
	return this.gerenciaImpl.getResponsableByApCampoAndGerencia(idApCampo, idGerencia, false);
    }

    public List<SelectItem> getAllSiCiudadSelectItem(String cadena) {
	List<SiCiudadVO> list = this.siCiudadImpl.findAllNative("nombre", true, false);
	List<SelectItem> items = new ArrayList<SelectItem>();

	for (SiCiudadVO vo : list) {
	    if (vo.getId() != null) {
		String cadenaVo = vo.getNombre().toLowerCase();
		cadena = cadena.toLowerCase();

		if (cadenaVo.startsWith(cadena)) {
		    SelectItem si = new SelectItem(vo, (vo.getNombre() + "|" + vo.getNombreSiEstado() + "|" + vo.getNombreSiPais()));
		    items.add(si);
		}
	    }
	}
	return items;
    }

    public List<SelectItem> getAllSiCiudadSelectItemByRange(String startFilter, String endFilter) {
	List<SiCiudadVO> list = this.siCiudadImpl.findAllByRangeNative(startFilter, endFilter, "nombre", true, false);
	List<SelectItem> items = new ArrayList<SelectItem>();

	for (SiCiudadVO vo : list) {
	    SelectItem si = new SelectItem(vo.getId().intValue(), (vo.getNombre() + "|" + vo.getNombreSiEstado() + "|" + vo.getNombreSiPais()));
	    items.add(si);
	}
	return items;
    }

    public Gerencia findGerencia(int idGerencia) {
	return this.gerenciaImpl.find(idGerencia);
    }

    /**
     * Devuelve una lista de solicitudes de viaje de oficina, ciudad o aéreos
     *
     * @return
     */
    @Trace
    public List<SolicitudViajeVO> getAllSgSolicitudViaje() {
	List<SolicitudViajeVO> list = null;
	UtilLog4j.log.info(this, "param: " + getOpcionViaje());
	switch (getOpcionViaje()) {
	case Constantes.SOLICITUDES_TERRESTRE_OFICINA:
	    list = this.sgSolicitudViajeImpl.getSgSolicitudViajeTerrestreToOficina(sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE, "id", true, false);
	    break;
	case Constantes.SOLICITUDES_TERRESTRE_CIUDAD:
	    list = this.sgSolicitudViajeImpl.getSgSolicitudViajeTerrestreToCiudad(sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE, "id", true, false);
	    break;
	/*
	 * case Constantes.TAB_SOLICITUDES_A_LUGAR: list =
	 * this.sgSolicitudViajeImpl.traerSolicitudViajeTerrestreALugar(sesion.getUsuario().getId(),
	 * Constantes.ESTATUS_PENDIENTE); break;
	 */
	case Constantes.SOLICITUDES_AEREA:
	    list = this.sgSolicitudViajeImpl.getSgSolicitudViajeTerrestreToAereos(sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE, "id", true, false);
	    break;
	}
	return list;
    }

    public List<UsuarioVO> findAllUsuarioActivoByGerencia(int idGerencia) {
	return apCampoUsuarioRhPuestoImpl.traerUsurioGerenciaCampo(idGerencia, Constantes.AP_CAMPO_DEFAULT);
    }

    public int totalSgSolicitudViajeToOficina() {
	return this.sgSolicitudViajeImpl.totalSgSolicitudViajeTerretreToOficina(this.sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE);
    }

    public int totalSgSolicitudViajeToCiudad() {
	return this.sgSolicitudViajeImpl.totalSgSolicitudViajeTerrestreToCiudad(this.sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE);
    }

    public int totalSgSolicitudViajeToAereos() {
	return this.sgSolicitudViajeImpl.totalSgSolicitudViajeToAereos(this.sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE);
    }

    public void getSolicitudesViajeByEmpleado() throws Exception {
	UtilLog4j.log.info(this, "ViajeBeanModel.getSolicitudesViajeByEmpleado()");
	if (getDataModel() == null) {
////////////////////////////            this.dataModel = new ListDataModel(this.solicitudViajeService.getSolicitudViajeVOByUsuarioAndStatus(Integer.valueOf(getInvitado()), this.sesion.getUsuario().getId(), 401, "id", false, false));
	}
    }

//    public void reloadSolicitudesViajeByEmpleado() throws Exception {
//////////////////////////////        setDataModel(new ListDataModel(this.solicitudViajeService.getSolicitudViajeVOByUsuarioAndStatus(Integer.valueOf(getInvitado()), this.sesion.getUsuario().getId(), 401, "id", false, false)));
//    }
    public void getViajerosBySolicitudViaje() throws SIAException, Exception {
	if (getSolicitudViajeVO() != null) {
	    this.setDataModelViajeros(new ListDataModel(this.sgViajeroImpl.getAllViajerosList(getSolicitudViajeVO().getIdSolicitud())));
	}
    }

    public List<ViajeroVO> findAllSgViajeroBySgSolicitudViaje(int idSgSolicitudViaje) {
	return this.sgViajeroImpl.getAllViajerosList(idSgSolicitudViaje);
    }

    public List<SgInvitado> traerInvitados() {
	return sgInvitadoImpl.getAllInvitado(Constantes.NO_ELIMINADO);
    }

    public List<SgLugar> getSgLugarList() {
	return this.sgLugarImpl.findAll("nombre", true, false);
    }

    public DataModel getOpcionesDeCadenasNegacion() {
	if (getInputHidden().getValue() != null) {
	    setDataModelOpciones(new ListDataModel(cadenaNegacionService.traerCadenasNegacionPorCadenaAprobacion((Integer) getInputHidden().getValue())));
	}
	return getDataModelOpciones();
    }

    public void searchSiCiudadesForSgSolicitudViaje() throws SIAException, Exception {
	if (this.getSolicitudViajeVO().getIdSgTipoSolicitudViaje() != 1) {
	    ItinerarioCompletoVo vo = sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(getSolicitudViajeVO().getIdSolicitud(), true, false, "id");
	    SgItinerario sgItinerarioTmp = sgItinerarioImpl.find(vo.getId());
//            SgItinerario sgItinerarioTmp = this.sgItinerarioImpl.findBySolicitudViaje(this.solicitudViaje, true, false);

	    SiCiudadVO siCiudadVOOrigenTmp = new SiCiudadVO();
	    siCiudadVOOrigenTmp.setId(sgItinerarioTmp.getSiCiudadOrigen().getId());
	    siCiudadVOOrigenTmp.setNombre(sgItinerarioTmp.getSiCiudadOrigen().getNombre());
	    siCiudadVOOrigenTmp.setNombreSiEstado(sgItinerarioTmp.getSiCiudadOrigen().getSiEstado().getNombre());
	    siCiudadVOOrigenTmp.setNombreSiPais(sgItinerarioTmp.getSiCiudadOrigen().getSiPais().getNombre());
	    this.setSiCiudadVOOrigen(siCiudadVOOrigenTmp);
	    this.setCadena(siCiudadVOOrigenTmp.getNombre());

	    SiCiudadVO siCiudadVODestinoTmp = new SiCiudadVO();
	    siCiudadVODestinoTmp.setId(sgItinerarioTmp.getSiCiudadDestino().getId());
	    siCiudadVODestinoTmp.setNombre(sgItinerarioTmp.getSiCiudadDestino().getNombre());
	    siCiudadVODestinoTmp.setNombreSiEstado(sgItinerarioTmp.getSiCiudadDestino().getSiEstado().getNombre());
	    siCiudadVODestinoTmp.setNombreSiPais(sgItinerarioTmp.getSiCiudadDestino().getSiPais().getNombre());
	    this.setSiCiudadVODestino(siCiudadVODestinoTmp);
	    this.setMensaje(siCiudadVODestinoTmp.getNombre());
	}
    }

    public MotivoRetrasoVO findSgMotivoRetrasoBySgSolicitudViaje() {
	if (getSolicitudViajeVO().getIdMotivoRetraso() != 0) {
	    this.setMotivoRetrasoVO(this.sgMotivoRetrasoImpl.findById(getSolicitudViajeVO().getIdMotivoRetraso(), getSolicitudViajeVO().getIdSgTipoEspecifico()));
	    return this.getMotivoRetrasoVO();
	}
	return null;

    }

    public void traerSolicitudParaCambioItinerario() {
	setDataModelSolicitudesPorProbar(new ListDataModel(sgViajeroImpl.traerSolicitudParaCambioItinerario(sesion.getUsuario().getId(), Constantes.SOLICITUDES_AEREA, Constantes.ESTATUS_APROBAR)));
    }

    public void traerSolicitudCambioItinerario() {
	setDataModelSolicitudesPorProbar(new ListDataModel(sgViajeroImpl.traerSolicitudCambioItinerario(sesion.getUsuario().getId(), Constantes.SOLICITUDES_AEREA)));
    }

    /**
     * Busca solo el SgItinerario de Ida de una Solicitud de Viaje
     *
     * @param conEscalas
     */
    public void findSgItinerarioBySgSolicitudViaje(boolean conEscalas) {
	try {
	    setItinerarioCompletoVoIda(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(getSolicitudViajeVO().getIdSolicitud(), true, conEscalas, "id"));
	    if (getItinerarioCompletoVoIda().getEscalas() != null && !itinerarioCompletoVoIda.getEscalas().isEmpty()) {
		UtilLog4j.log.info(this, "Asignar las escalas del itinerario de ida " + getItinerarioCompletoVoIda().getId() + " tiene " + getItinerarioCompletoVoIda().getEscalas());
		setDataModelDetalle(new ListDataModel(getItinerarioCompletoVoIda().getEscalas()));
	    }
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, ex.getMessage());
	}
    }

    public void findSgItinerarioVueltaBySgSolicitudViaje() {
	try {
	    setItinerarioCompletoVoVuelta(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(getSolicitudViajeVO().getIdSolicitud(), false, true, "id"));
	    //setSgItinerarioVuelta(sgItinerarioImpl.find(vo.getId()));
	    if (getItinerarioCompletoVoVuelta().getEscalas() != null && !itinerarioCompletoVoVuelta.getEscalas().isEmpty()) {
		setDataModel(new ListDataModel(getItinerarioCompletoVoVuelta().getEscalas()));
	    }
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, ex.getMessage());
	}
    }

//--------------------------------------------VIAJES ----------------------------------
    /**
     * Devuelve el total de Solicitudes de Viaje fuera de oficina y entre
     * oficinas
     *
     * @return
     */
    public int totalSolicitudesParaViajes() {
	UtilLog4j.log.info(this, "totalSolicitudesParaViajes--");
	if (sesion.getOficinaActual() != null) {
	    return sgViajeroImpl.totalViajerosTerrestresPorOficina(sesion.getOficinaActual().getId(), Constantes.ESTATUS_PARA_HACER_VIAJE, sesion.getUsuario().getId());
	}
	return Constantes.CERO;

    }

    //---------------------- Cambio de Itinerario -----------------------------
    public void guardarJustificacionItinerario() {
	sgCambioItinerarioImpl.guardarJustificacionItinerario(getIdSolicitud(), getTipoViaje(), getMensaje(), sesion.getUsuario().getId(), sesion.getUsuario().getEmail(), sesion.getUsuario().getNombre());
    }

    public int totalSolicitudesViajeOficina() {
	return sgEstatusAprobacionImpl.getCountTripRequestByOffice(sesion.getUsuario().getId(), sesion.getOficinaActual().getId(),
		Constantes.ESTATUS_PARA_HACER_VIAJE);
    }

    public int totalSolicitudesViajeFueraOficina() {
	List<EstatusAprobacionVO> ls = new ArrayList<EstatusAprobacionVO>();
	if (sesion.getOficinaActual() != null) {
	    ls = sgEstatusAprobacionImpl.getSolicitudesViajesCiudades(sesion.getOficinaActual().getId(), Constantes.ESTATUS_PARA_HACER_VIAJE);

	}
	return ls.size();
    }

    public long totalSolicitudesCreadas() {
	return sgEstatusAprobacionImpl.contarViajesCreados();
    }

    public int compararFechas(Date f, Date fecha2) {
	return siManejoFechaImpl.compare(f, fecha2);
    }

    public boolean fechaEsAntesHoraEspecifica(Date fecha, int hora, int minuto) {
	return siManejoFechaImpl.dateIsBeforeSpecificTime(fecha, hora, minuto);
    }

    public DataModel traerViajeros() {
	UtilLog4j.log.info(this, "ViajeBeanModel.traerViajeros()");
	try {
	    List<ViajeroVO> l = sgViajeroImpl.traerViajerosTerrestre(sesion.getOficinaActual().getId(), Constantes.ESTATUS_PARA_HACER_VIAJE, sesion.getUsuario().getId());
	    l.addAll(sgViajeroImpl.viajeroQuedado(sesion.getOficinaActual().getId(), Constantes.QUEDADO_ORIGEN, Constantes.FALSE));
	    l.addAll(sgViajeroImpl.viajeroQuedado(sesion.getOficinaActual().getId(), Constantes.QUEDADO_OFICINA_DESTINO, Constantes.FALSE));

	    Collections.sort(l, new ComparatorForViajeroVo());
	    setLista(new ListDataModel(l));
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
	return getLista();
    }

    public List<ViajeroVO> traerViajerosList() {
	UtilLog4j.log.info(this, "ViajeBeanModel.traerViajeros()");
	List<ViajeroVO> l = new ArrayList<ViajeroVO>();
	try {
	    l.addAll(sgViajeroImpl.traerViajerosTerrestre(sesion.getOficinaActual().getId(), Constantes.ESTATUS_PARA_HACER_VIAJE, sesion.getUsuario().getId()));
//
	    l.addAll(sgViajeroImpl.viajeroQuedadoOficinaOrigen(sesion.getOficinaActual().getId(), sesion.getUsuario().getId()));
	    l.addAll(sgViajeroImpl.viajeroQuedadoOficinaDestino(sesion.getOficinaActual().getId()));

	    Collections.sort(l, new ComparatorForViajeroVo());
	    setLista(new ListDataModel(l));
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
	return l;
    }

    public List<SelectItem> listaVehiculos() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	try {
	    List<VehiculoVO> lv = sgVehiculoImpl.traerVehiculoPorOficina(getIdOficina(), Constantes.NO_ELIMINADO);
	    for (VehiculoVO sgV : lv) {
		l.add(new SelectItem(sgV.getId(), sgV.getMarca() + " - " + sgV.getModelo() + " - " + sgV.getNumeroPlaca() + " - " + sgV.getColor()));
	    }
	} catch (Exception ex) {
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}

	return l;
    }

    public DataModel traerDetalleRuta() throws SIAException {
	if (getIdRuta() > 0) {
	    return new ListDataModel(sgDetalleRutaTerrestreImpl.getAllSgDetalleRutaTerrestreBySgRutaTerrestre(getIdRuta(), "id", true, false));//getDetailByRuote(getIdRuta(), Constantes.NO_ELIMINADO));
	}
	return null;
    }

    public DataModel traerRutaViaje() throws SIAException {
	if (getIdRuta() > 0) {
	    return new ListDataModel(sgDetalleRutaTerrestreImpl.getAllSgDetalleRutaTerrestreBySgRutaTerrestre(getViajeVO().getId(), "id", true, false));//getDetailByRuote(getIdRuta(), Constantes.NO_ELIMINADO));
	}
	return null;
    }

    public SgVehiculo buscarVehiculo() {
	try {
	    return sgVehiculoImpl.find(getIdVehiculo());
	} catch (Exception e) {
	    return null;
	}
    }

    public VehiculoVO buscarVehiculoVo() {
	try {
	    return sgVehiculoImpl.buscarVehiculoPorId(getIdVehiculo());
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "e: " + e.getMessage());
	    return null;
	}
    }

    public SgAsignarVehiculo buscarVehiculoAsignado() {
	try {
	    UtilLog4j.log.info(this, "buscarVehiculoAsignado con el idVehiculo: " + getVehiculoVO().getId());
	    return sgAsignarVehiculoImpl.buscarUsuarioPorVehiculo(getVehiculoVO().getId());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return null;
	}
    }

    public LicenciaVo buscarLiciencia() {
	if (getUsuarioVO() != null) {
	    setLicenciaVo(sgLicenciaImpl.buscarLicenciaVigentePorUsuario(getUsuarioVO().getId()));
	    return getLicenciaVo();
	}
	return null;
    }

    public List<SelectItem> traerUsuarioActivo(String cadenaDigitada) {
	return soporteProveedor.regresaUsuarioActivoVO(cadenaDigitada);
    }

    public UsuarioVO buscarUsuarioPorNombre() {
	setUsuarioVO(usuarioImpl.findByName(getInvitado()));
	return getUsuarioVO();
    }

    public int buscarOficinaEnSesion() {
	return sesion.getOficinaActual().getId();
    }

    public UsuarioVO buscarUsuarioPorId() {
	setUsuarioVO(usuarioImpl.findById(getUsuarioVO().getId()));
	return getUsuarioVO();
    }

    public boolean crearViajeEmergencia() {
	boolean v;
	v = sgViajeImpl.guardarViajeEmergente(sesion.getUsuario().getId(), getOpcionSeleccionada(), getSgViaje().getFechaProgramada(), getHoraSalida(), getMinutoSalida(),
		getIdVehiculo(), getCadena(), getIdRuta(), sesion.getOficinaActual().getId(), getIdSiOperacion(), Constantes.FALSE);
	return v;
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
     * @return the opcionMenu
     */
    public int getOpcionMenu() {
	return opcionMenu;
    }

    /**
     * @param opcionMenu the opcionMenu to set
     */
    public void setOpcionMenu(int opcionMenu) {
	this.opcionMenu = opcionMenu;
    }

    /**
     * @return the sgViaje
     */
    public SgViaje getSgViaje() {
	return sgViaje;
    }

    /**
     * @param sgViaje the sgViaje to set
     */
    public void setSgViaje(SgViaje sgViaje) {
	this.sgViaje = sgViaje;
    }

    /**
     * @return the sgViajeRegreso
     */
    public SgViaje getSgViajeRegreso() {
	return sgViajeRegreso;
    }

    /**
     * @param sgViajeRegreso the sgViajeRegreso to set
     */
    public void setSgViajeRegreso(SgViaje sgViajeRegreso) {
	this.sgViajeRegreso = sgViajeRegreso;
    }

    /**
     * @return the sgAsignarVehiculo
     */
    public SgAsignarVehiculo getSgAsignarVehiculo() {
	return sgAsignarVehiculo;
    }

    /**
     * @param sgAsignarVehiculo the sgAsignarVehiculo to set
     */
    public void setSgAsignarVehiculo(SgAsignarVehiculo sgAsignarVehiculo) {
	this.sgAsignarVehiculo = sgAsignarVehiculo;
    }

    /**
     * @return the sgViajero
     */
    public SgViajero getSgViajero() {
	return sgViajero;
    }

    /**
     * @param sgViajero the sgViajero to set
     */
    public void setSgViajero(SgViajero sgViajero) {
	this.sgViajero = sgViajero;
    }

    /**
     * @return the estatusAprobacion
     */
    public SgEstatusAprobacion getEstatusAprobacion() {
	return estatusAprobacion;
    }

    /**
     * @param estatusAprobacion the estatusAprobacion to set
     */
    public void setEstatusAprobacion(SgEstatusAprobacion estatusAprobacion) {
	this.estatusAprobacion = estatusAprobacion;
    }

    /**
     * @return the invitadoVO
     */
    public InvitadoVO getInvitadoVO() {
	return invitadoVO;
    }

    /**
     * @param invitadoVO the invitadoVO to set
     */
    public void setInvitadoVO(InvitadoVO invitadoVO) {
	this.invitadoVO = invitadoVO;
    }

    /**
     * @return the solicitudEstanciaVo
     */
    public SgSolicitudEstanciaVo getSolicitudEstanciaVo() {
	return solicitudEstanciaVo;
    }

    /**
     * @param solicitudEstanciaVo the solicitudEstanciaVo to set
     */
    public void setSolicitudEstanciaVo(SgSolicitudEstanciaVo solicitudEstanciaVo) {
	this.solicitudEstanciaVo = solicitudEstanciaVo;
    }

    /**
     * @return the sgViajeVehiculo
     */
    public SgViajeVehiculo getSgViajeVehiculo() {
	return sgViajeVehiculo;
    }

    /**
     * @param sgViajeVehiculo the sgViajeVehiculo to set
     */
    public void setSgViajeVehiculo(SgViajeVehiculo sgViajeVehiculo) {
	this.sgViajeVehiculo = sgViajeVehiculo;
    }

    /**
     * @return the motivoRetrasoVO
     */
    public MotivoRetrasoVO getMotivoRetrasoVO() {
	return motivoRetrasoVO;
    }

    /**
     * @param motivoRetrasoVO the motivoRetrasoVO to set
     */
    public void setMotivoRetrasoVO(MotivoRetrasoVO motivoRetrasoVO) {
	this.motivoRetrasoVO = motivoRetrasoVO;
    }

    /**
     * @return the sgLugar
     */
    public SgLugar getSgLugar() {
	return sgLugar;
    }

    /**
     * @param sgLugar the sgLugar to set
     */
    public void setSgLugar(SgLugar sgLugar) {
	this.sgLugar = sgLugar;
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
     * @return the itinerarioCompletoVoIda
     */
    public ItinerarioCompletoVo getItinerarioCompletoVoIda() {
	return itinerarioCompletoVoIda;
    }

    /**
     * @param itinerarioCompletoVoIda the itinerarioCompletoVoIda to set
     */
    public void setItinerarioCompletoVoIda(ItinerarioCompletoVo itinerarioCompletoVoIda) {
	this.itinerarioCompletoVoIda = itinerarioCompletoVoIda;
    }

    /**
     * @return the itinerarioCompletoVoVuelta
     */
    public ItinerarioCompletoVo getItinerarioCompletoVoVuelta() {
	return itinerarioCompletoVoVuelta;
    }

    /**
     * @param itinerarioCompletoVoVuelta the itinerarioCompletoVoVuelta to set
     */
    public void setItinerarioCompletoVoVuelta(ItinerarioCompletoVo itinerarioCompletoVoVuelta) {
	this.itinerarioCompletoVoVuelta = itinerarioCompletoVoVuelta;
    }

    /**
     * @return the sgDetalleItinerario
     */
    public SgDetalleItinerario getSgDetalleItinerario() {
	return sgDetalleItinerario;
    }

    /**
     * @param sgDetalleItinerario the sgDetalleItinerario to set
     */
    public void setSgDetalleItinerario(SgDetalleItinerario sgDetalleItinerario) {
	this.sgDetalleItinerario = sgDetalleItinerario;
    }

    /**
     * @return the sgRutaTerrestre
     */
    public SgRutaTerrestre getSgRutaTerrestre() {
	return sgRutaTerrestre;
    }

    /**
     * @param sgRutaTerrestre the sgRutaTerrestre to set
     */
    public void setSgRutaTerrestre(SgRutaTerrestre sgRutaTerrestre) {
	this.sgRutaTerrestre = sgRutaTerrestre;
    }

    /**
     * @return the estatusDevolverPorGerenteArea
     */
    public Estatus getEstatusDevolverPorGerenteArea() {
	return estatusDevolverPorGerenteArea;
    }

    /**
     * @param estatusDevolverPorGerenteArea the estatusDevolverPorGerenteArea to
     * set
     */
    public void setEstatusDevolverPorGerenteArea(Estatus estatusDevolverPorGerenteArea) {
	this.estatusDevolverPorGerenteArea = estatusDevolverPorGerenteArea;
    }

    /**
     * @return the siMovimiento
     */
    public SiMovimiento getSiMovimiento() {
	return siMovimiento;
    }

    /**
     * @param siMovimiento the siMovimiento to set
     */
    public void setSiMovimiento(SiMovimiento siMovimiento) {
	this.siMovimiento = siMovimiento;
    }

    /**
     * @return the siMovimientoViaje
     */
    public SiMovimiento getSiMovimientoViaje() {
	return siMovimientoViaje;
    }

    /**
     * @param siMovimientoViaje the siMovimientoViaje to set
     */
    public void setSiMovimientoViaje(SiMovimiento siMovimientoViaje) {
	this.siMovimientoViaje = siMovimientoViaje;
    }

    /**
     * @return the sgViajeCiudad
     */
    public SgViajeCiudad getSgViajeCiudad() {
	return sgViajeCiudad;
    }

    /**
     * @param sgViajeCiudad the sgViajeCiudad to set
     */
    public void setSgViajeCiudad(SgViajeCiudad sgViajeCiudad) {
	this.sgViajeCiudad = sgViajeCiudad;
    }

    /**
     * @return the viajeDestinoVo
     */
    public ViajeDestinoVo getViajeDestinoVo() {
	return viajeDestinoVo;
    }

    /**
     * @param viajeDestinoVo the viajeDestinoVo to set
     */
    public void setViajeDestinoVo(ViajeDestinoVo viajeDestinoVo) {
	this.viajeDestinoVo = viajeDestinoVo;
    }

    /**
     * @return the licenciaVo
     */
    public LicenciaVo getLicenciaVo() {
	return licenciaVo;
    }

    /**
     * @param licenciaVo the licenciaVo to set
     */
    public void setLicenciaVo(LicenciaVo licenciaVo) {
	this.licenciaVo = licenciaVo;
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
     * @return the viajeroVO
     */
    public ViajeroVO getViajeroVO() {
	return viajeroVO;
    }

    /**
     * @param viajeroVO the viajeroVO to set
     */
    public void setViajeroVO(ViajeroVO viajeroVO) {
	this.viajeroVO = viajeroVO;
    }

    /**
     * @return the viajeVO
     */
    public ViajeVO getViajeVO() {
	return viajeVO;
    }

    /**
     * @param viajeVO the viajeVO to set
     */
    public void setViajeVO(ViajeVO viajeVO) {
	this.viajeVO = viajeVO;
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
     * @return the estatusAprobacionVO
     */
    public EstatusAprobacionVO getEstatusAprobacionVO() {
	return estatusAprobacionVO;
    }

    /**
     * @param estatusAprobacionVO the estatusAprobacionVO to set
     */
    public void setEstatusAprobacionVO(EstatusAprobacionVO estatusAprobacionVO) {
	this.estatusAprobacionVO = estatusAprobacionVO;
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
     * @return the sgGastoViajeVO
     */
    public SgGastoViajeVO getSgGastoViajeVO() {
	return sgGastoViajeVO;
    }

    /**
     * @param sgGastoViajeVO the sgGastoViajeVO to set
     */
    public void setSgGastoViajeVO(SgGastoViajeVO sgGastoViajeVO) {
	this.sgGastoViajeVO = sgGastoViajeVO;
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
     * @return the gerenciaVo
     */
    public GerenciaVo getGerenciaVo() {
	return gerenciaVo;
    }

    /**
     * @param gerenciaVo the gerenciaVo to set
     */
    public void setGerenciaVo(GerenciaVo gerenciaVo) {
	this.gerenciaVo = gerenciaVo;
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
     * @return the inputHidden
     */
    public HtmlInputHidden getInputHidden() {
	return inputHidden;
    }

    /**
     * @param inputHidden the inputHidden to set
     */
    public void setInputHidden(HtmlInputHidden inputHidden) {
	this.inputHidden = inputHidden;
    }

    /**
     * @return the bigDecimal
     */
    public BigDecimal getBigDecimal() {
	return bigDecimal;
    }

    /**
     * @param bigDecimal the bigDecimal to set
     */
    public void setBigDecimal(BigDecimal bigDecimal) {
	this.bigDecimal = bigDecimal;
    }

    /**
     * @param mc the mc to set
     */
    public void setMc(MathContext mc) {
	this.mc = mc;
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
     * @return the origenAereo
     */
    public String getOrigenAereo() {
	return origenAereo;
    }

    /**
     * @param origenAereo the origenAereo to set
     */
    public void setOrigenAereo(String origenAereo) {
	this.origenAereo = origenAereo;
    }

    /**
     * @return the destinoAereo
     */
    public String getDestinoAereo() {
	return destinoAereo;
    }

    /**
     * @param destinoAereo the destinoAereo to set
     */
    public void setDestinoAereo(String destinoAereo) {
	this.destinoAereo = destinoAereo;
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
     * @return the motivoCancelacion
     */
    public String getMotivoCancelacion() {
	return motivoCancelacion;
    }

    /**
     * @param motivoCancelacion the motivoCancelacion to set
     */
    public void setMotivoCancelacion(String motivoCancelacion) {
	this.motivoCancelacion = motivoCancelacion;
    }

    /**
     * @return the fechaSalida
     */
    public String getFechaSalida() {
	return fechaSalida;
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(String fechaSalida) {
	this.fechaSalida = fechaSalida;
    }

    /**
     * @return the fechaRegreso
     */
    public String getFechaRegreso() {
	return fechaRegreso;
    }

    /**
     * @param fechaRegreso the fechaRegreso to set
     */
    public void setFechaRegreso(String fechaRegreso) {
	this.fechaRegreso = fechaRegreso;
    }

    /**
     * @return the tipoViaje
     */
    public String getTipoViaje() {
	return tipoViaje;
    }

    /**
     * @param tipoViaje the tipoViaje to set
     */
    public void setTipoViaje(String tipoViaje) {
	this.tipoViaje = tipoViaje;
    }

    /**
     * @return the selectedTab
     */
    public String getSelectedTab() {
	return selectedTab;
    }

    /**
     * @param selectedTab the selectedTab to set
     */
    public void setSelectedTab(String selectedTab) {
	this.selectedTab = selectedTab;
    }

    /**
     * @return the listaVehiculo
     */
    public List<SelectItem> getListaVehiculo() {
	return listaVehiculo;
    }

    /**
     * @param listaVehiculo the listaVehiculo to set
     */
    public void setListaVehiculo(List<SelectItem> listaVehiculo) {
	this.listaVehiculo = listaVehiculo;
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
     * @return the listaInvitados
     */
    public List<SelectItem> getListaInvitados() {
	return listaInvitados;
    }

    /**
     * @param listaInvitados the listaInvitados to set
     */
    public void setListaInvitados(List<SelectItem> listaInvitados) {
	this.listaInvitados = listaInvitados;
    }

    /**
     * @return the listaViajerosVO
     */
    public List<ViajeroVO> getListaViajerosVO() {
	return listaViajerosVO;
    }

    /**
     * @param listaViajerosVO the listaViajerosVO to set
     */
    public void setListaViajerosVO(List<ViajeroVO> listaViajerosVO) {
	this.listaViajerosVO = listaViajerosVO;
    }

    /**
     * @return the listaQuedados
     */
    public List<ViajeroVO> getListaQuedados() {
	return listaQuedados;
    }

    /**
     * @param listaQuedados the listaQuedados to set
     */
    public void setListaQuedados(List<ViajeroVO> listaQuedados) {
	this.listaQuedados = listaQuedados;
    }

    /**
     * @return the filaSeleccionada
     */
    public Map<Integer, Boolean> getFilaSeleccionada() {
	return filaSeleccionada;
    }

    /**
     * @param filaSeleccionada the filaSeleccionada to set
     */
    public void setFilaSeleccionada(Map<Integer, Boolean> filaSeleccionada) {
	this.filaSeleccionada = filaSeleccionada;
    }

    /**
     * @return the mapViajeros
     */
    public Map<SgSolicitudEstanciaVo, List<ViajeroVO>> getMapViajeros() {
	return mapViajeros;
    }

    /**
     * @param mapViajeros the mapViajeros to set
     */
    public void setMapViajeros(Map<SgSolicitudEstanciaVo, List<ViajeroVO>> mapViajeros) {
	this.mapViajeros = mapViajeros;
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
     * @return the dataModelDetalle
     */
    public DataModel getDataModelDetalle() {
	return dataModelDetalle;
    }

    /**
     * @param dataModelDetalle the dataModelDetalle to set
     */
    public void setDataModelDetalle(DataModel dataModelDetalle) {
	this.dataModelDetalle = dataModelDetalle;
    }

    /**
     * @return the dataModelItinerarioDetalle
     */
    public DataModel getDataModelItinerarioDetalle() {
	return dataModelItinerarioDetalle;
    }

    /**
     * @param dataModelItinerarioDetalle the dataModelItinerarioDetalle to set
     */
    public void setDataModelItinerarioDetalle(DataModel dataModelItinerarioDetalle) {
	this.dataModelItinerarioDetalle = dataModelItinerarioDetalle;
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
	return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
	this.lista = lista;
    }

    /**
     * @return the dataModelSolicitudesPorProbar
     */
    public DataModel getDataModelSolicitudesPorProbar() {
	return dataModelSolicitudesPorProbar;
    }

    /**
     * @param dataModelSolicitudesPorProbar the dataModelSolicitudesPorProbar to
     * set
     */
    public void setDataModelSolicitudesPorProbar(DataModel dataModelSolicitudesPorProbar) {
	this.dataModelSolicitudesPorProbar = dataModelSolicitudesPorProbar;
    }

    /**
     * @return the dataModelOpciones
     */
    public DataModel getDataModelOpciones() {
	return dataModelOpciones;
    }

    /**
     * @param dataModelOpciones the dataModelOpciones to set
     */
    public void setDataModelOpciones(DataModel dataModelOpciones) {
	this.dataModelOpciones = dataModelOpciones;
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

    /**
     * @return the dataModelSolcitudesTemporales
     */
    public DataModel getDataModelSolcitudesTemporales() {
	return dataModelSolcitudesTemporales;
    }

    /**
     * @param dataModelSolcitudesTemporales the dataModelSolcitudesTemporales to
     * set
     */
    public void setDataModelSolcitudesTemporales(DataModel dataModelSolcitudesTemporales) {
	this.dataModelSolcitudesTemporales = dataModelSolcitudesTemporales;
    }

    /**
     * @return the dataModelViajerosDetalle
     */
    public DataModel getDataModelViajerosDetalle() {
	return dataModelViajerosDetalle;
    }

    /**
     * @param dataModelViajerosDetalle the dataModelViajerosDetalle to set
     */
    public void setDataModelViajerosDetalle(DataModel dataModelViajerosDetalle) {
	this.dataModelViajerosDetalle = dataModelViajerosDetalle;
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
     * @return the pop
     */
    public boolean isPop() {
	return pop;
    }

    /**
     * @param pop the pop to set
     */
    public void setPop(boolean pop) {
	this.pop = pop;
    }

    /**
     * @return the popQuitar
     */
    public boolean isPopQuitar() {
	return popQuitar;
    }

    /**
     * @param popQuitar the popQuitar to set
     */
    public void setPopQuitar(boolean popQuitar) {
	this.popQuitar = popQuitar;
    }

    /**
     * @return the mostrarPanel
     */
    public boolean isMostrarPanel() {
	return mostrarPanel;
    }

    /**
     * @param mostrarPanel the mostrarPanel to set
     */
    public void setMostrarPanel(boolean mostrarPanel) {
	this.mostrarPanel = mostrarPanel;
    }

    /**
     * @return the detenerViaje
     */
    public boolean isDetenerViaje() {
	return detenerViaje;
    }

    /**
     * @param detenerViaje the detenerViaje to set
     */
    public void setDetenerViaje(boolean detenerViaje) {
	this.detenerViaje = detenerViaje;
    }

    /**
     * @return the popup
     */
    public boolean isPopup() {
	return popup;
    }

    /**
     * @param popup the popup to set
     */
    public void setPopup(boolean popup) {
	this.popup = popup;
    }

    /**
     * @return the estancia
     */
    public boolean isEstancia() {
	return estancia;
    }

    /**
     * @param estancia the estancia to set
     */
    public void setEstancia(boolean estancia) {
	this.estancia = estancia;
    }

    /**
     * @return the mostrarAprobar
     */
    public boolean isMostrarAprobar() {
	return mostrarAprobar;
    }

    /**
     * @param mostrarAprobar the mostrarAprobar to set
     */
    public void setMostrarAprobar(boolean mostrarAprobar) {
	this.mostrarAprobar = mostrarAprobar;
    }

    /**
     * @return the mostrarAutorizar
     */
    public boolean isMostrarAutorizar() {
	return mostrarAutorizar;
    }

    /**
     * @param mostrarAutorizar the mostrarAutorizar to set
     */
    public void setMostrarAutorizar(boolean mostrarAutorizar) {
	this.mostrarAutorizar = mostrarAutorizar;
    }

    /**
     * @return the mostrarVistoBueno
     */
    public boolean isMostrarVistoBueno() {
	return mostrarVistoBueno;
    }

    /**
     * @param mostrarVistoBueno the mostrarVistoBueno to set
     */
    public void setMostrarVistoBueno(boolean mostrarVistoBueno) {
	this.mostrarVistoBueno = mostrarVistoBueno;
    }

    /**
     * @return the mostrarjustificar
     */
    public boolean isMostrarjustificar() {
	return mostrarjustificar;
    }

    /**
     * @param mostrarjustificar the mostrarjustificar to set
     */
    public void setMostrarjustificar(boolean mostrarjustificar) {
	this.mostrarjustificar = mostrarjustificar;
    }

    /**
     * @return the mostrarJustificarDireccion
     */
    public boolean isMostrarJustificarDireccion() {
	return mostrarJustificarDireccion;
    }

    /**
     * @param mostrarJustificarDireccion the mostrarJustificarDireccion to set
     */
    public void setMostrarJustificarDireccion(boolean mostrarJustificarDireccion) {
	this.mostrarJustificarDireccion = mostrarJustificarDireccion;
    }

    /**
     * @return the solicitudFinalizada
     */
    public boolean isSolicitudFinalizada() {
	return solicitudFinalizada;
    }

    /**
     * @param solicitudFinalizada the solicitudFinalizada to set
     */
    public void setSolicitudFinalizada(boolean solicitudFinalizada) {
	this.solicitudFinalizada = solicitudFinalizada;
    }

    /**
     * @return the verDetallePop
     */
    public boolean isVerDetallePop() {
	return verDetallePop;
    }

    /**
     * @param verDetallePop the verDetallePop to set
     */
    public void setVerDetallePop(boolean verDetallePop) {
	this.verDetallePop = verDetallePop;
    }

    /**
     * @return the detallePop
     */
    public boolean isDetallePop() {
	return detallePop;
    }

    /**
     * @param detallePop the detallePop to set
     */
    public void setDetallePop(boolean detallePop) {
	this.detallePop = detallePop;
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
     * @return the modificar
     */
    public boolean isModificar() {
	return modificar;
    }

    /**
     * @param modificar the modificar to set
     */
    public void setModificar(boolean modificar) {
	this.modificar = modificar;
    }

    public void eliminarEstancia(final int idViajero) {
	try {
	    for (ViajeroVO vj : listaViajerosVO) {
		final SgViajero viajero = sgViajeroImpl.find(vj.getId());
		if (viajero != null && viajero.isEstancia() && (idViajero == 0 || idViajero == viajero.getId())) {
		    viajero.setEstancia(false);
		    viajero.setSgSolicitudEstancia(null);
		    sgViajeroImpl.edit(viajero);
		}
	    }
	} catch (Exception ex) {
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * @return the cancelarPop
     */
    public boolean isCancelarPop() {
	return cancelarPop;
    }

    /**
     * @param cancelarPop the cancelarPop to set
     */
    public void setCancelarPop(boolean cancelarPop) {
	this.cancelarPop = cancelarPop;
    }

    /**
     * @return the popRegresarViaje
     */
    public boolean isPopRegresarViaje() {
	return popRegresarViaje;
    }

    /**
     * @param popRegresarViaje the popRegresarViaje to set
     */
    public void setPopRegresarViaje(boolean popRegresarViaje) {
	this.popRegresarViaje = popRegresarViaje;
    }

    /**
     * @return the agregarResponsable
     */
    public boolean isAgregarResponsable() {
	return agregarResponsable;
    }

    /**
     * @param agregarResponsable the agregarResponsable to set
     */
    public void setAgregarResponsable(boolean agregarResponsable) {
	this.agregarResponsable = agregarResponsable;
    }

    /**
     * @return the viajeFueraOficina
     */
    public boolean isViajeFueraOficina() {
	return viajeFueraOficina;
    }

    /**
     * @param viajeFueraOficina the viajeFueraOficina to set
     */
    public void setViajeFueraOficina(boolean viajeFueraOficina) {
	this.viajeFueraOficina = viajeFueraOficina;
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
     * @return the idSolicitud
     */
    public int getIdSolicitud() {
	return idSolicitud;
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
     * @return the idVehiculo
     */
    public int getIdVehiculo() {
	return idVehiculo;
    }

    /**
     * @param idVehiculo the idVehiculo to set
     */
    public void setIdVehiculo(int idVehiculo) {
	this.idVehiculo = idVehiculo;
    }

    /**
     * @return the idEstatusActivo
     */
    public int getIdEstatusActivo() {
	return idEstatusActivo;
    }

    /**
     * @param idEstatusActivo the idEstatusActivo to set
     */
    public void setIdEstatusActivo(int idEstatusActivo) {
	this.idEstatusActivo = idEstatusActivo;
    }

    /**
     * @return the idTipoSolicitudViaje
     */
    public int getIdTipoSolicitudViaje() {
	return idTipoSolicitudViaje;
    }

    /**
     * @param idTipoSolicitudViaje the idTipoSolicitudViaje to set
     */
    public void setIdTipoSolicitudViaje(int idTipoSolicitudViaje) {
	this.idTipoSolicitudViaje = idTipoSolicitudViaje;
    }

    /**
     * @return the idSiOperacion
     */
    public int getIdSiOperacion() {
	return idSiOperacion;
    }

    /**
     * @param idSiOperacion the idSiOperacion to set
     */
    public void setIdSiOperacion(int idSiOperacion) {
	this.idSiOperacion = idSiOperacion;
    }

    /**
     * @return the tamanioLista
     */
    public int getTamanioLista() {
	return tamanioLista;
    }

    /**
     * @param tamanioLista the tamanioLista to set
     */
    public void setTamanioLista(int tamanioLista) {
	this.tamanioLista = tamanioLista;
    }

    /**
     * @return the idRuta
     */
    public int getIdRuta() {
	return idRuta;
    }

    /**
     * @param idRuta the idRuta to set
     */
    public void setIdRuta(int idRuta) {
	this.idRuta = idRuta;
    }

    /**
     * @return the idNoticia
     */
    public int getIdNoticia() {
	return idNoticia;
    }

    /**
     * @param idNoticia the idNoticia to set
     */
    public void setIdNoticia(int idNoticia) {
	this.idNoticia = idNoticia;
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
     * @return the opcionViaje
     */
    public int getOpcionViaje() {
	return opcionViaje;
    }

    /**
     * @param opcionViaje the opcionViaje to set
     */
    public void setOpcionViaje(int opcionViaje) {
	this.opcionViaje = opcionViaje;
    }

    /**
     * @return the idUrl
     */
    public Integer getIdUrl() {
	return idUrl;
    }

    /**
     * @param idUrl the idUrl to set
     */
    public void setIdUrl(Integer idUrl) {
	this.idUrl = idUrl;
    }

    /**
     * @return the tipoDestino
     */
    public int getTipoDestino() {
	return tipoDestino;
    }

    /**
     * @param tipoDestino the tipoDestino to set
     */
    public void setTipoDestino(int tipoDestino) {
	this.tipoDestino = tipoDestino;
    }

    /**
     * @return the redondo
     */
    public String getRedondo() {
	return redondo;
    }

    /**
     * @param redondo the redondo to set
     */
    public void setRedondo(String redondo) {
	this.redondo = redondo;
    }

    /**
     * @return the cambiarItinerario
     */
    public int getCambiarItinerario() {
	return cambiarItinerario;
    }

    /**
     * @param cambiarItinerario the cambiarItinerario to set
     */
    public void setCambiarItinerario(int cambiarItinerario) {
	this.cambiarItinerario = cambiarItinerario;
    }

    public boolean validateLicenciaActiva() {

	return (this.siManejoFechaImpl.compare(getLicenciaVo().getVencimiento(), new Date()) >= 0 ? true : false);
    }

    public boolean viajoAyer() {
	boolean v = false;
	try {
	    //busca el el viajero en viajes anteriores
	    List<SgViaje> lv = sgViajeImpl.getTravelByResponsible(getUsuarioVO().getId());

	    Date d = siManejoFechaImpl.fechaRestarDias(siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaSalida()), 1);
	    if (!lv.isEmpty()) {
		for (SgViaje sgV : lv) {
		    if (sgV.getFechaSalida() != null) {
			if (sgV.getFechaSalida().compareTo(d) == 0 || sgV.getFechaSalida().compareTo(siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaSalida())) == 0) {
			    v = true;
			    break;
			}
		    } else {
			if (sgV.getFechaProgramada().compareTo(d) == 0 || sgV.getFechaProgramada().compareTo(siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaSalida())) == 0) {
			    v = true;
			    break;
			}
		    }

		}
	    }
	} catch (Exception ex) {
	    v = false;
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}
	return v;
    }

    public void iniciarCampo() {
	setIdCampo(sesion.getUsuario().getApCampo().getId());
    }

    public DataModel<ViajeroVO> filtarFilasSeleccionadas() {
	UtilLog4j.log.info(this, "Lista viajeros sin seleccionar : + + +" + getLista().getRowCount());
	DataModel<ViajeroVO> lt = getLista();
	List<ViajeroVO> l = new ArrayList<ViajeroVO>();
	UtilLog4j.log.info(this, "Filas seleccionadas: " + filaSeleccionada.size());
	for (ViajeroVO sgV : lt) {
	    if (filaSeleccionada.get(sgV.getId())) {
		l.add(sgV);
		filaSeleccionada.remove(sgV.getId());
	    }
	}
	setLista(new ListDataModel(l));
	return getLista();
    }

    public boolean validaFechaIguales() {
	boolean v = false;
	//Verifica las fechas de salida
	///Verifica que la fecha se salida sea igual en todos los viajeros
	Date d;
	List<ViajeroVO> l = dataModelAList(getLista());
	if (l.get(0).getViajeroQuedado() == Constantes.QUEDADO_OFICINA_DESTINO) {
	    d = l.get(0).getFechaRegreso();
	} else {
	    d = l.get(0).getFechaSalida();
	}
	for (ViajeroVO viajero : l) {
	    if (viajero.getViajeroQuedado() == Constantes.QUEDADO_OFICINA_DESTINO) {
		if (siManejoFechaImpl.compare(d, viajero.getFechaRegreso()) == 0) {
		    //   setDetenerViaje(true);
		    v = true;
		    //Poner fecha salida
		} else {
		    //    setDetenerViaje(false);
		    break;
		}
	    } else {
		if (siManejoFechaImpl.compare(d, viajero.getFechaSalida()) == 0) {
		    //    setDetenerViaje(true);
		    v = true;
		    //Poner fecha salida
		} else {
		    //    setDetenerViaje(false);
		    break;
		}
	    }
	}
	setFechaSalida(siManejoFechaImpl.convertirFechaStringddMMyyyy(d));

	return v;
    }

    public void iniciaRedondoSencilloCiudad() {
	//Trae semaforo por id
	try {
	    if (sesion.getOficinaActual() != null) {
		setIdOficina(sesion.getOficinaActual().getId());
	    } else {
		setIdOficina(Constantes.ID_OFICINA_TORRE_MARTEL);
	    }
	    setViajeFueraOficina(true);
	    setFechaSalida(siManejoFechaImpl.convertirFechaStringddMMyyyy(getSolicitudViajeVO().getFechaSalida()));
	    setHoraSalida(6);
	    int idR = getSolicitudViajeVO().getIdRutaTerrestre();
	    setSemaforoVo(sgEstadoSemaforoImpl.estadoActual(idR));
	    setIdRuta(getSemaforoVo().getIdRuta());
	    traerDetalleRuta();
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Exc: inicio viaje ciudad: " + e.getMessage());
	}
    }

    public void iniciaRedondoSencillo() {
	try {
	    List<ViajeroVO> lv = dataModelAList(getLista());
	    boolean v = false;
	    Date fr = null;
	    if (sesion.getOficinaActual() != null) {
		setIdOficina(sesion.getOficinaActual().getId());
	    }
	    if (lv.size() > 0) {
		ViajeroVO vjrVo = lv.get(0);
		//SolicitudViajeVO solV = sgSolicitudViajeImpl.buscarPorCodigo(vjrVo.getCodigoSolicitudViaje(), Constantes.BOOLEAN_FALSE);
		//Trae semaforo por id
		if (vjrVo.isRedondo()) { // inicia con sencillo
		    setRedondo(Constantes.sencillo);
//		    setFechaSalida(siManejoFechaImpl.convertirFechaStringddMMyyyy(solV.getFechaSalida()));
		    String[] hs = siManejoFechaImpl.convertirHoraStringHHmmss(vjrVo.getHoraSalida()).split(":");
		    setHoraSalida(Integer.parseInt(hs[0]));
		} else {
		    setRedondo(Constantes.redondo); // inicia con redondo
		    String[] hs = siManejoFechaImpl.convertirHoraStringHHmmss(vjrVo.getHoraSalida()).split(":");
		    String[] hr = siManejoFechaImpl.convertirHoraStringHHmmss(vjrVo.getHoraRegreso()).split(":");
		    setHoraSalida(Integer.parseInt(hs[0]));
		    setHoraRegreso(Integer.parseInt(hr[0]));
		//    setFechaRegreso(siManejoFechaImpl.convertirFechaStringddMMyyyy(solV.getFechaRegreso()));
		}
		//
		//int idR = solV.getIdRutaTerrestre();
		//setSemaforoVo(sgEstadoSemaforoImpl.estadoActual(idR));
		setIdRuta(getSemaforoVo().getIdRuta());
		traerDetalleRuta();
	    }

	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Exc: iniciar viaje " + e.getMessage());
	}
    }

    public boolean validaFechaSalidaViajePasada() {
	List<ViajeroVO> l = dataModelAList(getLista());
	int e = 0;
	for (ViajeroVO vjro : l) {
	    if (vjro.getViajeroQuedado() == Constantes.QUEDADO_OFICINA_DESTINO) {
		if (siManejoFechaImpl.compare(vjro.getFechaRegreso(), new Date()) == -1) {
		    e++;
		}
	    } else {
		if (siManejoFechaImpl.compare(vjro.getFechaSalida(), new Date()) == -1) {
		    e++;
		}
	    }
	}

	return e == 0;
    }

    public Date sumarDias(Date date, int dias) {
	return siManejoFechaImpl.fechaSumarDias(date, dias);
    }

    public <T> List<T> dataModelAList(DataModel m) {
	return (List<T>) m.getWrappedData();
    }

    public void quitarUsuarioLista() {
	try {
	    List<ViajeroVO> l = dataModelAList(getLista());
	    for (ViajeroVO vVO : l) {
		if (vVO.getId() == getViajeroVO().getId()) {
		    l.remove(vVO);
		    break;
		}
	    }
	    if (l.isEmpty()) {
		setLista(null);
		setOpcionViaje(1);
		if (getSgViaje() != null) {
		    setSgViaje(null);
		}
	    } else {
		setTamanioLista(l.size());
		setLista(new ListDataModel(l));
	    }
	} catch (RuntimeException e) {
	    UtilLog4j.log.info(this, "Error : + + + " + e.getMessage() + "causa: " + e.getCause().toString());
//            return null;
	}
//        return getLista();
    }

    public List<SelectItem> listaOficina() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	try {
	    List<SgOficina> lv = oficinaService.findAll(false);
	    for (SgOficina sgO : lv) {
		l.add(new SelectItem(sgO.getId(), sgO.getSgDireccion().getSiCiudad().getNombre()));
	    }
	} catch (Exception ex) {
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}

	return l;
    }

    public List<SelectItem> listaOficinaVO() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	try {
	    List<OficinaVO> lv = oficinaService.traerListaOficina();
	    for (OficinaVO sgO : lv) {
		l.add(new SelectItem(sgO.getId(), sgO.getNombre()));
	    }
	} catch (Exception ex) {
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}

	return l;
    }

    public boolean verificaRuta() {
	UtilLog4j.log.info(this, "ViajeBeanModel.verificaRuta()");
	boolean v = false;
	try {
	    List<ViajeroVO> listaViajero = new ArrayList<ViajeroVO>();
	    if (getViajeVO() != null) {
		setSgViaje(sgViajeImpl.find(getViajeVO().getId()));
	    }
	    if (getSgViaje() != null) {
		if (getSgViaje().getCodigo() != null) {
		    listaViajero = sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
		} else {
		    listaViajero = dataModelAList(getLista());
		}
	    }
	    if (validaRutaViajero(listaViajero) > 0) { //No encontro nada
		v = true;
	    }
	} catch (Exception ex) {
	    UtilLog4j.log.info(this, "ex: traer ruta " + ex.getMessage());
	}
	return v;
    }

    private int validaRutaViajero(List<ViajeroVO> listaViajero) {
	//Solicitudes atendidas
	SolicitudViajeVO solVO;
	int t = 0;
	String ciudadOrigen = "";
	String ciudadDestino = "";
	String ciudadDestinoRuta = sgRutaTerrestreImpl.traerCiudadDestinoRuta(getIdRuta());
	for (ViajeroVO vj : listaViajero) {
	   // solVO = sgSolicitudViajeImpl.buscarPorCodigo(vj.getCodigoSolicitudViaje(), Constantes.NO_ELIMINADO); //Agregamos la sol a la lista temporal de solicitudes
	    if (vj.getViajeroQuedado() == Constantes.PRIMERA_VEZ_VIAJE) {  // viajero viaja por primera vez
//		ciudadDestino = sgOficinaImpl.traerCiudadPorIdOficina(solVO.getIdOficinaDestino());
		if (ciudadDestino.equals(ciudadDestinoRuta)) {
		    t++; // los destinos son iguales
		} else {
		    t = 0; // los destinos son diferentes
		    break;
		}
	    } else if (vj.getViajeroQuedado() == Constantes.QUEDADO_ORIGEN) {  // viajero quedado en origen
	//	ciudadDestino = sgOficinaImpl.traerCiudadPorIdOficina(solVO.getIdOficinaDestino());
		if (ciudadDestino.equals(ciudadDestinoRuta)) {
		    t++; // los destinos son iguales
		} else {
		    t = 0; // los destinos son diferentes
		    break;
		}
	    } else if (vj.getViajeroQuedado() == Constantes.QUEDADO_OFICINA_DESTINO) {  // viajero quedado en destino
	//	ciudadOrigen = sgOficinaImpl.traerCiudadPorIdOficina(solVO.getIdOficinaOrigen());
		if (ciudadOrigen.equals(ciudadDestinoRuta)) {
		    t++; // los destinos son iguales
		} else {
		    t = 0; // los destinos son diferentes
		    break;
		}
	    }
	}
	return t;
    }

    public boolean verificaRutaRegreso() {
	UtilLog4j.log.info(this, "ViajeBeanModel.verificaRuta()");
	boolean v = false;
	try {
	    List<ViajeroVO> listaViajero = dataModelAList(getLista());
	    for (ViajeroVO vjrVO : listaViajero) {
		vjrVO.setTipoViajero(Constantes.QUEDADO_OFICINA_DESTINO);
	    }
	    if (getOpcionSeleccionada().equals("todos") || getOpcionSeleccionada().equals("seleccion")) {
		if (validaRutaViajero(listaViajero) > 0) { //encontro misma ruta
		    v = true;
		}
	    } else if (getOpcionSeleccionada().equals("ninguno")) {
		v = true;
	    }
	} catch (Exception ex) {
	    UtilLog4j.log.info(this, "ex: " + ex.getMessage());
	}
	return v;
    }

    public boolean verificaRutaViajero() {
	boolean v = false;
	int t = 0;
	String ciudadOrigenSol = "";
	String ciudadDestinoSol = "";
	String ciudadDestinoViaje = sgRutaTerrestreImpl.traerCiudadDestinoRuta(getViajeVO().getIdRuta());

	//SolicitudViajeVO solVO = sgSolicitudViajeImpl.buscarPorCodigo(getViajeroVO().getCodigoSolicitudViaje(), Constantes.NO_ELIMINADO); //Agregamos la sol a la lista temporal de solicitudes

	if (getViajeroVO().getViajeroQuedado() == Constantes.PRIMERA_VEZ_VIAJE) {  // viajero viaja por primera vez
	//    ciudadDestinoSol = sgOficinaImpl.traerCiudadPorIdOficina(solVO.getIdOficinaDestino());
	    if (ciudadDestinoSol.equals(ciudadDestinoViaje)) {
		t++; // los destinos son iguales
	    } else {
		t = 0; // los destinos son diferentes
	    }
	} else if (getViajeroVO().getViajeroQuedado() == Constantes.QUEDADO_ORIGEN) {  // viajero quedado en origen
	 //   ciudadDestinoSol = sgOficinaImpl.traerCiudadPorIdOficina(solVO.getIdOficinaDestino());
	    if (ciudadDestinoSol.equals(ciudadDestinoViaje)) {
		t++; // los destinos son iguales
	    } else {
		t = 0; // los destinos son diferentes
	    }
	} else if (getViajeroVO().getViajeroQuedado() == Constantes.QUEDADO_OFICINA_DESTINO) {  // viajero quedado en destino
	   // ciudadOrigenSol = sgOficinaImpl.traerCiudadPorIdOficina(solVO.getIdOficinaOrigen());
	    if (ciudadOrigenSol.equals(ciudadDestinoViaje)) {
		t++; // los destinos son iguales
	    } else {
		t = 0; // los destinos son diferentes
	    }
	}
	if (t == 0) {
	    v = false;
	} else {
	    v = true;
	}
	return v;
    }

    public void crearViaje() {
	List<ViajeroVO> listaViajero = dataModelAList(getLista());
	List<SolicitudViajeVO> listSolViajeTemp = new ArrayList<SolicitudViajeVO>();
	TreeSet<String> treeSetSolicitud = new TreeSet<String>();

	//Solicitudes atendidas
	for (ViajeroVO v : listaViajero) {
	    treeSetSolicitud.add(v.getCodigoSolicitudViaje());
	}
	UtilLog4j.log.info(this, "Lista viajero temporal: " + listaViajero.size());

	for (String codigo : treeSetSolicitud) {
	 //   solicitudViajeVO = sgSolicitudViajeImpl.buscarPorCodigo(codigo, Constantes.NO_ELIMINADO); //Agregamos la sol a la lista temporal de solicitudes
	    listSolViajeTemp.add(solicitudViajeVO);
	}
// apartir de aqui se cambiara para el envio de correos
	StringBuilder correoPara = new StringBuilder();

	List<UsuarioVO> lu = usuarioImpl.getUsuariosByRol(Constantes.SGL_RESPONSABLE);
	//lu.addAll(usuarioImpl.getUsuariosByRol(Constantes.SGL_SEGURIDAD));
	for (UsuarioVO uvo : usuarioImpl.getUsuariosByRol(Constantes.SGL_ANALISTA)) {
	    if (uvo.getId().equals(sgOficinaAnalistaImpl.traerAnalistaPrincipalPorOficina(Constantes.ID_OFICINA_TORRE_MARTEL).getIdAnalista())) {
		lu.add(uvo);
	    }
	}
	int nlist = lu.size();
	int x = 1;
	correoPara.append(sesion.getUsuario().getEmail()).append(",");
	for (UsuarioVO usuario1 : lu) {
	    correoPara.append(usuario1.getMail());
	    if (x < nlist) {
		correoPara.append(", ");
	    }
	    x++;
	}
	if (getOpcionSeleccionada().equals(Constantes.VEHICULO_EMPRESA)) {
	    sgViaje = sgViajeImpl.saveCompanyCar(sesion.getUsuario(), getSgViaje().getResponsable(), getVehiculoVO(), listSolViajeTemp,
		    listaViajero, sesion.getOficinaActual().getId(), getOpcionSeleccionada(), getIdRuta(), getSgViaje().getFechaProgramada(),
		    getSgViaje().getFechaRegreso(), getSgViaje().getHoraProgramada(), getSgViaje().getHoraRegreso(), correoPara.toString(),
		    getRedondo(), getTelefono());
	} else {
	    for (ViajeroVO vj : listaViajero) { //Dentro del método 'noCompanyCar' se envían los e-mails correspondientes pendiente
		if (vj.getIdInvitado() == 0) {
		    usuarioImpl.agregaTelefonoUsuario(vj.getIdUsuario(), vj.getTelefono(), sesion.getUsuario().getId());
		}
		UtilLog4j.log.info(this, "Viajero " + (vj.getIdInvitado() == 0 ? vj.getUsuario() : vj.getInvitado()));
		UtilLog4j.log.info(this, " Teléfono viajero: " + vj.getTelefono());
		sgViaje = sgViajeImpl.noCompanyCar(sesion.getOficinaActual().getId(), vj, getOpcionSeleccionada(), sesion.getUsuario(),
			getIdRuta(), getSgViaje().getFechaProgramada(), getSgViaje().getFechaRegreso(), getSgViaje().getHoraProgramada(), getSgViaje().getHoraRegreso(),
			correoPara.toString(), getRedondo(), vj.getTelefono());

		//publicar noticia (Es temporal, no debe de ir aqui.)
		CoNoticia noticia = sgViajeImpl.createEventNews(sgViaje.getId(), sesion.getUsuario().getId());
		coCompartidaImpl.compartir(noticia, siUsuarioRolImpl.traerRolPorCodigo("GRADM", Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_SGYL));
		//
		sgViajeroImpl.agregarViaje(sesion.getUsuario().getId(), sgViajeroImpl.find(vj.getId()), getSgViaje(), false);
	    }
	    sgViajeImpl.validarViajesTerrestres(sesion.getUsuario(), sgViaje, listaViajero, null);
	}
    }

    public boolean actualizarSolicitud() {
	boolean va = true;
	List<ViajeroVO> lViajero = dataModelAList(getLista());
	SgSolicitudViaje sgSolicitudViaje;
	List<SgViajero> lvi;
	TreeSet<String> codigo = new TreeSet<String>();
	for (ViajeroVO v : lViajero) {
	    codigo.add(v.getCodigoSolicitudViaje());
	}
	for (String string : codigo) {
	    try {
		sgSolicitudViaje = sgSolicitudViajeImpl.findByCode(string);
		lvi = sgViajeroImpl.getViajerosBySolicitudViajeList(sgSolicitudViaje, false);
		for (SgViajero vj : lvi) {
		    if (vj.getSgViaje() == null) {
			va = false;
			break;
		    }
		}
		if (va) {
		    sgEstatusAprobacionImpl.finalizeRequest(sgSolicitudViaje.getId(), sesion.getUsuario(), sesion.getIdRol());
		}
	    } catch (SIAException ex) {
		Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	return va;
    }

    public List<SelectItem> listaCampo() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<ApCampoVo> lc;
	try {
	    lc = apCampoImpl.traerApCampo();
	    for (ApCampoVo ca : lc) {
		SelectItem item = new SelectItem(ca.getId(), ca.getNombre());
		l.add(item);
	    }
	    return l;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error al traer los campos  . .. . " + e.getMessage());
	    return null;
	}
    }

    //viaje por salir
    public int totalViajesPorSalir() throws SIAException {
	return sgViajeImpl.getCountTrip(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR, sesion.getUsuario().getId());
//        List<ViajeVO> viajes = this.sgViajeImpl.getSgViajeBySgOficinaAndEstatus(this.sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR);
//        return (!viajes.isEmpty() ? viajes.size() : 0);
    }

    @Deprecated
    public List<ViajeVO> viajeTerrestrePorSalir() throws SIAException, Exception {
	try {
	    return this.sgViajeImpl.getRoadTripByExit(this.sesion.getOficinaActual().getId(),
		    Constantes.ESTATUS_VIAJE_POR_SALIR, Constantes.ESTATUS_VIAJE_CREADO, false, null, null, false, 0, true, null);

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    UtilLog4j.log.info(this, "Causa: " + e.getCause().toString() + "Error: " + e.getMessage());
	    throw new SIAException(getMensaje());
	}
    }

    /**
     * Creo: NLopez
     *
     * @return
     */
    public DataModel traerViajeEsperaAutorizacion() {
	List<ViajeVO> lv = this.sgViajeImpl.getSgViajeBySgOficinaAndEstatus(Constantes.NINGUNA_OFICINA, Constantes.VIAJE_ESPERA_AUTORIZACION);
	setDataModelOpciones(new ListDataModel(lv));
	return getDataModelOpciones();

    }

    public boolean validaFechaSalidaViaje() {
	return siManejoFechaImpl.validaFechaSalidaViaje(getSgViaje().getFechaProgramada(), getSgViaje().getHoraProgramada());
    }

    public void salidaViaje() throws SIAException {
	sgViajeImpl.exitTrip(sesion.getUsuario(), getSgViaje(), Constantes.ESTATUS_VIAJE_PROCESO, null, true);
    }

    public boolean guardarArchivoViaje(String fileName, String path, String contentType, long size) {
	boolean v = false;
	try {
	    SiAdjunto siAdjunto = 
                    siAdjuntoImpl.save(
                            fileName, 
                            path + File.separator + fileName, 
                            contentType, 
                            size, 
                            sesion.getUsuario().getId()
                    );
	    //        UtilLog4j.log.info(this, "Aqui después de guardar el archivo");
	    if (siAdjunto != null) {
		getViajeVO().setAdjunto(siAdjunto.getNombre());
		getViajeVO().setIdAdjunto(siAdjunto.getId());
		getViajeVO().setUuid(siAdjunto.getUuid());
		v = sgViajeImpl.addFile(getSgViaje(), sesion.getUsuario(), siAdjunto);
	    }
//            else {
//                siAdjuntoImpl.eliminarArchivo(siAdjunto, sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//            }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
	return v;
    }

    public String dir() {
	//return siParametroImpl.find(1).getUploadDirectory() + "SGyL/Viajes/" + getSgViaje().getId() + "/";
        return "SGyL/Viajes/" + getSgViaje().getId();
    }

    public void eliminarArchivoViaje() throws SIAException {
        
        SiAdjunto adjunto = getSgViaje().getSiAdjunto();
        
        if(adjunto == null) {
            LOGGER.error(this, "No existe el adjunto para el viaje " + getSgViaje().getCodigo());
        } else {
            try {
                proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(adjunto.getUrl());
                
                sgViajeImpl.update(getSgViaje(), sesion.getUsuario());
		siAdjuntoImpl.eliminarArchivo(
                        getSgViaje().getSiAdjunto(), 
                        sesion.getUsuario().getId(), 
                        Constantes.BOOLEAN_TRUE
                );
		getViajeVO().setIdAdjunto(0);
		getViajeVO().setAdjunto("");
		getViajeVO().setUuid("");
            } catch (Exception e) {
                LOGGER.error("Eliminando adjunto : " + adjunto.getUrl(), e);
                throw new SIAException(e.getMessage());
            }
        }
        
        
        
	//Se eliminan fisicamente los archivos
//	String path = this.siParametroImpl.find(1).getUploadDirectory();
//	try {
//	    File file = new File(path + getSgViaje().getSiAdjunto().getUrl());
//	    if (file.delete()) {
//		sgViajeImpl.update(getSgViaje(), sesion.getUsuario());
//		siAdjuntoImpl.eliminarArchivo(getSgViaje().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//		getViajeVO().setIdAdjunto(0);
//		getViajeVO().setAdjunto("");
//		getViajeVO().setUuid("");
//	    }
//	    //Elimina la carpeta
//	    String dir = "SGyL/Viajes/" + getSgViaje().getId();
////            UtilLog4j.log.info(this, "Ruta carpeta: " + dir);
//	    File sessionfileUploadDirectory = new File(path + dir);
//	    if (sessionfileUploadDirectory.isDirectory()) {
//		try {
//		    sessionfileUploadDirectory.delete();
//		} catch (SecurityException e) {
//		    UtilLog4j.log.fatal(this, e.getMessage());
//		}
//	    }
//	    //        }
//	} catch (Exception e) {
//	    UtilLog4j.log.fatal(this, e.getMessage());
//	}
    }

    public void traerViajerosPorViaje() {
	List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
	try {
	    List<ViajeroVO> lviajero = sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
	    for (ViajeroVO viajero : lviajero) {
		if (viajero.isRedondo()) {
		    lv.add(viajero);
		}
	    }
	    setTamanioLista(lv.size());
	    UtilLog4j.log.info(this, "L: " + lv.size());
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Exc viajeros por viaje  + + + + +   " + e.getMessage().toString());
	}
	setLista(new ListDataModel(lv));
    }

    public List<ViajeroVO> getSgViajeroBySgViaje() {
	return sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
    }

    public List<ViajeroVO> getAllViajeroVoBySgViaje(int idSgViaje) {
	return sgViajeroImpl.getTravellersByTravel(idSgViaje, null);
    }

    public void pasarFechaViajeAVariable() {
	if (getRedondo().equals(Constantes.redondo)) {
	    setFechaSalida(siManejoFechaImpl.convertirFechaStringddMMyyyy(getSgViaje().getFechaProgramada()));
	    String[] hs = siManejoFechaImpl.convertirHoraStringhmma(getSgViaje().getHoraProgramada()).split(":");
	    setHoraSalida(Integer.parseInt(hs[0]));
	    setMinutoSalida(Integer.parseInt(hs[1].substring(0, 2)));
	    //
	    setFechaRegreso(siManejoFechaImpl.convertirFechaStringddMMyyyy(getSgViaje().getFechaRegreso()));
	    String[] hr = siManejoFechaImpl.convertirHoraStringhmma(getSgViaje().getHoraRegreso()).split(":");
	    setHoraRegreso(Integer.parseInt(hr[0]));
	    setMinutoRegreso(Integer.parseInt(hr[1].substring(0, 2)));
	} else {
	    setFechaSalida(siManejoFechaImpl.convertirFechaStringddMMyyyy(getSgViaje().getFechaProgramada()));
	    String[] hs = siManejoFechaImpl.convertirHoraStringhmma(getSgViaje().getHoraProgramada()).split(":");
	    setHoraSalida(Integer.parseInt(hs[0]));
	    setMinutoSalida(Integer.parseInt(hs[1].substring(0, 2)));
	}
    }

    public void pasarFechaVariableAViaje() {
	try {
	    Date fs = siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaSalida());
	    Calendar cHoraSalida = Calendar.getInstance();
	    cHoraSalida.set(Calendar.HOUR_OF_DAY, getHoraSalida());
	    cHoraSalida.set(Calendar.MINUTE, getMinutoSalida());
	    //
	    getSgViaje().setFechaProgramada(fs);
	    getSgViaje().setHoraProgramada(cHoraSalida.getTime());
	    UtilLog4j.log.info(this, "Hora Salida: " + cHoraSalida.getTime());
	    //
	    Calendar cHoraRegreso = Calendar.getInstance();
	    if (this.getSgViaje().getSgViaje() == null) { //si es viaje de ida
		if (getRedondo().equals(Constantes.redondo)) {
		    Date fr = siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaRegreso());
		    cHoraRegreso.set(Calendar.HOUR_OF_DAY, getHoraRegreso());
		    cHoraRegreso.set(Calendar.MINUTE, getMinutoRegreso());
		    getSgViaje().setFechaRegreso(fr);
		    getSgViaje().setHoraRegreso(cHoraRegreso.getTime());
		    UtilLog4j.log.info(this, "Hora Regreso: " + cHoraRegreso.getTime());
		} else {
		    getSgViaje().setFechaRegreso(null);
		    getSgViaje().setHoraRegreso(null);
		}
	    }

	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error al pasar las fechas " + ex.getMessage());
	}
    }

    public Date ponerFechaSalida() {
	List<ViajeroVO> l = dataModelAList(getLista());
	return l.get(0).getFechaSalida();
    }

    public boolean validarFechaSalidaViajeAnteriorHoy(Date fechaSalida, Date horaSalida) {
	return siManejoFechaImpl.validaFechaSalidaViaje(fechaSalida, horaSalida);
    }

    public Date ponerFechaRegreso() {
	List<ViajeroVO> l = dataModelAList(getLista());
	return l.get(0).getFechaRegreso();
    }

    public LicenciaVo buscarLicienciaModificar() {
	setLicenciaVo(sgLicenciaImpl.buscarLicenciaVigentePorUsuario(getSgViaje().getResponsable().getId()));
	return getLicenciaVo();
    }

    public SgViajeVehiculo buscarVehiculoPorViaje() {
	return sgViajeVehiculoImpl.getVehicleByTravel(getSgViaje().getId());
    }

    public SgViajeVehiculo buscarVehiculoPorViajeVo() {
	return sgViajeVehiculoImpl.getVehicleByTravel(getViajeVO().getId());
    }

    public SgViaje buscarViajePorId(int idViaje) {
	return sgViajeImpl.find(idViaje);
    }

    //ocupado tambien en panel de busqueda
    public SgRutaTerrestre buscarRutaTerrestre() {
	if (getSgViaje() != null && getSgViaje().getSgViajeCiudad() == null) {
	    return sgRutaTerrestreImpl.find(getSgViaje().getSgRutaTerrestre().getId());//Se cambio buscar ruta de nombre por id
	}
	return null;
    }

    public void buscarDestinoTerrestre() {
	if (getViajeVO() != null) {
	    getViajeVO().setDestinoCiudad(sgRutaTerrestreImpl.traerCiudadDestinoRuta(getViajeVO().getIdRuta()));//Se cambio buscar ruta de nombre por id
	}
    }

    public DataModel traerDetalleRutaViaje() throws SIAException {
	return new ListDataModel(sgDetalleRutaTerrestreImpl.getDetailByRuote(getSgViaje().getSgRutaTerrestre().getId(), Constantes.NO_ELIMINADO));
    }

    public String traerDestinoRutaEnViaje() throws SIAException {
	if (getViajeVO().getTipoRuta() == Constantes.RUTA_TIPO_OFICINA) {
	    return sgDetalleRutaTerrestreImpl.buscarDetalleRutaTerrestreDestinoPorRuta(getViajeVO().getIdRuta()).getNombreSgOficina();
	} else {
	    return sgDetalleRutaCiudadImpl.buscarDetalleRutaCiudadDestinoPorRuta(getViajeVO().getIdRuta()).getCiudad();
	}
    }

    public boolean completarModificarViaje() {
	if ((getIdRuta() == 1 || getIdRuta() == 7) && !this.isViajeDirecto()) {
	    List<ViajeroVO> listaViajero = dataModelAList(getLista());
	    if (getIdRuta() == 1) {
		this.setIdRuta(6);// Cambiamos la ruta de MTY-SF a MTY-REY
		int nuevaSolViaje = solicitudViajeService.clonarSolicitudViaje(this.getSolicitudViajeVO().getIdSolicitud(), 4, this.getIdRuta(), listaViajero);
		sgEstatusAprobacionImpl.traerHistorialEstatusAprobacionPorSolicitud(this.getSolicitudViajeVO().getIdSolicitud(), nuevaSolViaje);

	    } else if (getIdRuta() == 7) {
		this.setIdRuta(5);// Cambiamos la ruta de SF-MTY a SF-REY
		int nuevaSolViaje = solicitudViajeService.clonarSolicitudViaje(this.getSolicitudViajeVO().getIdSolicitud(), 3, this.getIdRuta(), listaViajero);
		sgEstatusAprobacionImpl.traerHistorialEstatusAprobacionPorSolicitud(this.getSolicitudViajeVO().getIdSolicitud(), nuevaSolViaje);
	    }
	}
	return sgViajeImpl.updateTrips(sesion.getUsuario(), getSgViaje(), getSgViajeVehiculo(), getVehiculoVO(), getOpcionSeleccionada(),
		getIdRuta(), getRedondo(),Constantes.FALSE);
    }

    public SgViajero buscarViajeroPorId() {
	return sgViajeroImpl.find(getViajeroVO().getId());
    }

    public void eliminarViajeroDeSolicitud() throws SIAException {
	try {
	    sgViajeroImpl.delete(getSgViajero().getId(), sesion.getUsuario().getId(), getMotivoCancelacion());
	    //Agregar la logica para finalizar o cancelar la solicitud por no tener viajeros.
	} catch (Exception ex) {
	    UtilLog4j.log.info(this, "excep: al borrar viajero" + ex.getMessage());
	}
    }

    public void cancelarViajero() throws SIAException {
	sgViajeroImpl.cancelTraveller(sesion.getUsuario(), sgViajeroImpl.find(getViajeroVO().getId()), getMotivoCancelacion(), getTamanioLista());

	//Obtine tamanio de la lista
	if (getSgViaje() != null) {
	    List<ViajeroVO> lv = sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
	    if (!lv.isEmpty()) {
		setTamanioLista(lv.size());
		UtilLog4j.log.info(this, "L cancelar viajero: " + lv.size());
		setLista(new ListDataModel(lv));
	    } else {
		setTamanioLista(0);
		setLista(null);
	    }
	}

	//Verifica si ya no hay viajeros en el viaje
	int idS = sgViajeroImpl.find(getViajeroVO().getId()).getSgSolicitudViaje().getId();
	int tamanioListaSolicitud = sgViajeroImpl.getViajerosBySinViaje(idS).size();
	UtilLog4j.log.info(this, "Tamanio lista: model antes de finalizar req" + tamanioListaSolicitud);
	for (SgViajero vj : sgViajeroImpl.getViajerosBySinViaje(idS)) {
	    UtilLog4j.log.info(this, "Viajero: " + vj.getSgSolicitudViaje().getId());
	}
	UtilLog4j.log.info(this, "ID SOL: " + idS);
	if (tamanioListaSolicitud == 0) {
	    UtilLog4j.log.info(this, "Era el el ultimo viajero");
	    sgEstatusAprobacionImpl.finalizeRequest(idS, sesion.getUsuario(), sesion.getIdRol());
	}

	//busca si tiene estancia
	if (getViajeroVO().isEstancia()) {
	    //Hacer lo que se tenia que hacer con estancia
	    sgDetalleSolicitudEstanciaImpl.cancelLoungeViajeroOfRequest(sgViajeroImpl.find(getViajeroVO().getId()), "", sesion.getUsuario());
	}

	//cancelar estancia dentro de cancelar viajero
    }

    public ViajeroVO buscarViajeroPorId(int idViajero) {
	return sgViajeroImpl.buscarViajeroPorId(idViajero);
    }

    public void quitarViajero() throws SIAException {
	sgViajeroImpl.takeOutTravellToTraveller(sesion.getUsuario(), sgViajeroImpl.find(viajeroVO.getId()), getMotivoCancelacion(), getTamanioLista(),Constantes.FALSE);

	List<ViajeroVO> lv = sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
	setLista(new ListDataModel(lv));
	//Verifica si ya no hay viajeros en el viaje
    }

    public void quitarViajeroFromPorSalir(int countViajeros) throws SIAException {
	sgViajeroImpl.takeOutTravellToTraveller(sesion.getUsuario(), sgViajeroImpl.find(viajeroVO.getId()), getMotivoCancelacion(), countViajeros, Constantes.FALSE);
    }

    public void eliminarViaje() throws SIAException {
	sgViajeImpl.cancelTrip(sesion.getUsuario(), getSgViaje(), getMotivoCancelacion(), false, null, false);
    }

    public void finalizarSolicitudViaje() {
	sgEstatusAprobacionImpl.finalizeRequest(getSolicitudViajeVO().getIdSolicitud(), sesion.getUsuario(), sesion.getIdRol());
    }

    public void cancelarViaje() throws SIAException {
	UtilLog4j.log.info(this, "ViajeBeanModel.cancelarViaje()");
	sgViajeImpl.cancelTrip(sesion.getUsuario(), getSgViaje(), getMotivoCancelacion(), false, null, false);
    }
    /*
     * Detner viaje
     */

    public void detenerViaje() {
	sgViajeImpl.detenerViaje(sesion.getUsuario(), getSgViaje(), getMotivoCancelacion());
    }
    /*
     * Fin de detener viaje
     */

    public SgViajeVehiculo buscarViajeAsignadoParaviaje() {
	setSgViajeVehiculo(sgViajeVehiculoImpl.getVehicleByTravel(getSgViaje().getId()));
	return getSgViajeVehiculo();
    }

    public int buscarIdVehiculoAsignadoAViaje() {
	return sgViajeVehiculoImpl.getVehicleByTravel(getViajeVO().getId()).getSgVehiculo().getId();
    }

    public DataModel traerViajerosPorViajeDetalle() {
	List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
	try {
	    if (getSgViaje() != null) {
		lv = sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Exc viajeros por viaje  + + + + +   " + e.getMessage().toString());
	}
	return new ListDataModel(lv);
    }

    public void llenaDatosItinerario() {
	try {
	    setDataModel(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getSgViaje().getSgItinerario().getId(), true, "id").getEscalas()));
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, ex.getMessage());
	}
    }

    //usado para llenar itinerarios desde panel de busqueda
    public void llenaDatosItinerarioSolicitud() {
	try {
	    itinerarioCompletoVoIda = sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(getSolicitudViajeVO().getIdSolicitud(), true, true, "id");

	    setDataModel(new ListDataModel(itinerarioCompletoVoIda.getEscalas()));
	} catch (Exception ex) {
	    UtilLog4j.log.info(this, "Error al llenar el Itienerario " + ex.getMessage());
	}
    }

    public void llenarItinerarioViaje() {
	//setDataModel(new ListDataModel(sgDetalleItinerarioImpl.findBySgItinerario(getSgItinerario(), "id", true, false)));
	setDataModel(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getViajeVO().getIdItinerario(), true, "id").getEscalas()));
    }

    /**
     * MLUIS 19/11/2013
     */
    //Viaje aereo de regreso
    public void llenaDatosItinerarioRegreso() {
	try {
	    List<ViajeroVO> l = sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
	    if (!l.isEmpty()) {
		UtilLog4j.log.info(this, "viajero: " + l.get(0).getId());
	//	setSolicitudViajeVO(sgSolicitudViajeImpl.buscarPorCodigo(l.get(0).getCodigoSolicitudViaje(), Constantes.NO_ELIMINADO));

		itinerarioCompletoVoVuelta = sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(getSolicitudViajeVO().getIdSolicitud(), false, true, "id");
		if (getItinerarioCompletoVoVuelta() != null) {
		    setDataModel(new ListDataModel(itinerarioCompletoVoVuelta.getEscalas()));
		    setTamanioLista(getDataModel().getRowCount());
		}
	    }
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, ex.getMessage());
	}
    }

    public SgDetalleItinerario findSgDetalleItinerario(int idSgDetalleItinerario) {
	return this.sgDetalleItinerarioImpl.find(idSgDetalleItinerario);
    }

    /**
     * *****************************************
     */
    public SolicitudViajeVO buscarSolicitudViajePorCodigo(String codigo) {
	//setSolicitudViajeVO(solicitudViajeService.buscarPorCodigo(codigo, Constantes.NO_ELIMINADO));
	return getSolicitudViajeVO();
    }

    public SgSolicitudViaje buscarSolicitudViajePorId(int idSolicitudViaje) {
	return solicitudViajeService.find(idSolicitudViaje);
    }

    public SolicitudViajeVO buscarPorIdSolicitudViaje(int idSolicitudViaje) {
	return solicitudViajeService.buscarPorId(idSolicitudViaje, Constantes.NO_ELIMINADO, Constantes.CERO);
    }

    public int totalViajeroPorViaje() {
	List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
	try {
	    lv = sgViajeroImpl.getTravellersByTravel(getViajeVO().getId(), null);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Exc viajeros por viaje  + + + + +   " + e.getMessage());
	}
	return lv.size();
    }

    public DataModel traerViajeroAgregarViajeroAViaje() {
	if (getViajeVO() != null) {
	    return new ListDataModel(sgViajeroImpl.getTravellersByTravel(getViajeVO().getId(), null));
	}
	return null;
    }

    public String getUsuarioSession() {
	String usr = "";
	if (sesion != null && sesion.getUsuario() != null && sesion.getUsuario().getId() != null) {
	    usr = sesion.getUsuario().getId();
	}
	return usr;
    }

    public void agregarViajeroAViaje(int viajeroID, int viajeID) {
	sgViaje = sgViajeImpl.find(viajeID);
	setViajeroVO(buscarViajeroPorId(viajeroID));
	agregarViajeroAViaje();

    }

    public void agregarViajeroAViaje() {
	sgViaje = sgViajeImpl.find(getViajeVO().getId());
	sgViajeroImpl.agregarViaje(sesion.getUsuario().getId(), sgViajeroImpl.find(getViajeroVO().getId()), getSgViaje(), true);
	SgViajeroSiMovimiento sgViajeroSiMovimiento = sgViajeroSiMovimientoImpl.findByTraveller(getViajeroVO().getId(),
                Constantes.QUEDADO_ORIGEN,Constantes.CERO, Constantes.CERO);
	if (sgViajeroSiMovimiento != null) {
	    sgViajeroSiMovimientoImpl.deleteRelation(sesion.getUsuario(), sgViajeroSiMovimiento);
	}
	//Verifica si es el ultimo viajero de la solicitud
	List<SgViajero> li = sgViajeroImpl.getViajerosBySinViaje(getViajeroVO().getIdSolicitudViaje()); //viaje == null
	if (li.isEmpty()) {
	    sgEstatusAprobacionImpl.finalizeRequest(getViajeroVO().getIdSolicitudViaje(), sesion.getUsuario(), sesion.getIdRol());
	}
    }

    public boolean agregarEmpleadoAViaje() {//
	String motivo = "Empleado agregado al Viaje -" + sgViaje.getCodigo() + "- desdes la administración de los viajes.";
	return sgViajeroImpl.agregarViajeroAViaje(sesion.getUsuario().getId(), getSgViaje().getId(), getCadena(), Constantes.SG_TIPO_ESPECIFICO_EMPLEADO, 0, sesion.getUsuario().getEmail(), motivo, Constantes.ID_SI_OPERACION_AGREGAR_VIAJERO); //empleado
    }

    public boolean agregarInvitadoAViaje() {//
	String motivo = "Invitado agregado al Viaje -" + sgViaje.getCodigo() + "- desdes la administración de los viajes.";
	return sgViajeroImpl.agregarViajeroAViaje(sesion.getUsuario().getId(), getSgViaje().getId(), getCadena(), Constantes.SG_TIPO_ESPECIFICO_INVITADO, getIdInvitado(), sesion.getUsuario().getEmail(), motivo, Constantes.ID_SI_OPERACION_AGREGAR_VIAJERO); //empleado
    }

    /////////////////////////////////////////
    public String usuariosJson() {
	return usuarioImpl.traerUsuarioActivoJson();
    }

    public String llenarInvitadoJson() {
	return sgInvitadoImpl.traerInvitadoJsonPorCampo();
    }
    ///////////////////////////////////////////
    //Viajes en proceso

    public int totalViajesEnProceso() throws SIAException {
	if (sesion.getOficinaActual() != null) {
	    return sgViajeImpl.tolalViajeEnProcesso(Constantes.ESTATUS_VIAJE_PROCESO, sesion.getUsuario().getId(), sesion.getOficinaActual().getId());
	}
	return 0;
    }

    public DataModel viajeTerrestreEnProceso() {
	if (sesion.getOficinaActual() != null) {
	    List<ViajeVO> lv = sgViajeImpl.getRoadTripInProcess(Constantes.ESTATUS_VIAJE_PROCESO, sesion.getUsuario().getId(), sesion.getOficinaActual().getId());
	    setLista(new ListDataModel(lv));
	}
	return getLista();

    }

//Viajes oficina destino
    public int totalViajeOficina() throws SIAException {
	return sgViajeImpl.getCountTripOffice(sesion.getOficinaActual().getId(),
		Constantes.ESTATUS_VIAJE_EN_DESTINO,
		sesion.getUsuario().getId());
    }

    public DataModel traerViajesOficinaDestino(boolean traerRetorno) throws SIAException {
	List<ViajeVO> lv = new ArrayList<ViajeVO>();
	lv.addAll(sgViajeImpl.getRoadTripDesOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_EN_DESTINO, sesion.getUsuario().getId(), traerRetorno));
	lv.addAll(sgViajeImpl.getRoadTripDesOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_PROCESO, sesion.getUsuario().getId(), traerRetorno));
	setLista(new ListDataModel(lv));
	return getLista();
    }

    //viajes de regreso por FINALIZAR
    public int totalViajesPorFinalizar() throws SIAException {
	return sgViajeImpl.getCountTravelsByFinalize(Constantes.ESTATUS_VIAJE_PROCESO, sesion.getUsuario().getId(),
		sesion.getOficinaActual().getId());
    }

    public DataModel traerViajesRegresoPorFinalizar() throws SIAException {
	List<ViajeVO> list = sgViajeImpl.getTravellersByFinalize(Constantes.ESTATUS_VIAJE_PROCESO, sesion.getUsuario().getId(),
		sesion.getOficinaActual().getId());
	setLista(new ListDataModel(list));
	return getLista();
    }

    public int filtarFilasSeleccionadasViajeRegreso() {
	UtilLog4j.log.info(this, "Lista viajeros viaje regreso : + + + " + getLista().getRowCount());
	DataModel<ViajeroVO> lt = getLista();
	setListaQuedados(new ArrayList<ViajeroVO>());
	List<ViajeroVO> l = new ArrayList<ViajeroVO>();
	for (ViajeroVO sgV : lt) {
	    if (filaSeleccionada.get(sgV.getId())) {
		sgV.setTipoViajero(Constantes.QUEDADO_OFICINA_DESTINO);
		l.add(sgV);
		filaSeleccionada.remove(sgV.getId());
	    } else {
		getListaQuedados().add(sgV);
	    }
	}
	UtilLog4j.log.info(this, "Filas seleccionadas: " + l.size());
	int t = l.size();
	if (t == 0) {
	    setLista(getLista());
	} else {
	    setLista(new ListDataModel(l));
	}

	return t;
    }

    public void crearViajeRegreso() {
	try {

	    List<ViajeroVO> list = new ArrayList<ViajeroVO>(); //lista de viajeros
	    List<ViajeroVO> listTemp = dataModelAList(getLista()); //lista de viajeros
	    listaQuedados = new ArrayList<ViajeroVO>();
	    for (ViajeroVO viajero : listTemp) {
		if (filaSeleccionada.get(viajero.getId())) {
		    list.add(viajero);
		} else {
		    listaQuedados.add(viajero);
		}
	    }
	    Date fs = siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaSalida());
	    Calendar cHoraSalida = Calendar.getInstance();
	    cHoraSalida.set(Calendar.HOUR_OF_DAY, getHoraSalida());
	    cHoraSalida.set(Calendar.MINUTE, getMinutoSalida());

	    if (getSgViaje() != null && getSgViaje().getSgViajeCiudad() != null) {
		//Rellenar los viajeros
		getSgViaje().setFechaProgramada(fs);
		getSgViaje().setHoraProgramada(cHoraSalida.getTime());

		list = this.sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
	    }
	    sgViajeImpl.saveReturnTrip(sesion.getUsuario(), sesion.getOficinaActual().getId(),
		    getSgViaje(), getIdRuta(), list, false, getListaQuedados(), fs, cHoraSalida.getTime(), getIdVehiculo());
	} catch (Exception ex) {
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public DataModel traerSolicitudesAereas() throws SIAException {
	if (sesion.getOficinaActual() != null) {
	    List<EstatusAprobacionVO> lv = sgEstatusAprobacionImpl.getApprovalStatusByOffice(sesion.getUsuario().getId(),
		    sesion.getOficinaActual().getId(),
		    Constantes.ESTATUS_PARA_HACER_VIAJE, false);

	    if (siUsuarioRolImpl.buscarRolPorUsuarioModulo(sesion.getUsuario().getId(), Constantes.MODULO_SGYL, "7", Constantes.AP_CAMPO_DEFAULT)) {
		//
		List<EstatusAprobacionVO> tmp = new ArrayList<EstatusAprobacionVO>();

		for (EstatusAprobacionVO vo : lv) {
		    if (vo.getIdTipoSolicitud() == 6) {
			tmp.add(vo);
		    }
		}
		Collections.sort(tmp, new ComparatorForEstatusAprobacionVo());
		setLista(new ListDataModel(tmp));
	    } else {
		Collections.sort(lv, new ComparatorForEstatusAprobacionVo());
		setLista(new ListDataModel(lv));
	    }

	    UtilLog4j.log.info(this, "lv.size() " + lv.size());
	}

	return getLista();
    }
    //Viajeros aereos

    public int totalSolicitudesParaViajesAereos() {
	return sgEstatusAprobacionImpl.getCountAirRequestByOffice(sesion.getOficinaActual().getId(),
		Constantes.ESTATUS_PARA_HACER_VIAJE, sesion.getUsuario().getId());
    }

    public void guardarViajeAereo() throws SIAException {
	List<ViajeroVO> lv = dataModelAList(getLista());
	setSgViaje(sgViajeImpl.guardarViajeAereo(sesion.getOficinaActual().getId(), sesion.getUsuario(), getSgViaje(),
		getSolicitudViajeVO().getIdSolicitud(), lv, getItinerarioCompletoVoIda(), getRedondo()));
    }

    public void guardarViajeAereoRegreso() {
	try {
	    List<ViajeroVO> vj = sgViajeroImpl.getTravellersByTravel(getSgViaje().getId(), null);
	    UtilLog4j.log.info(this, "Viajeros: " + vj.size());
	    sgViajeImpl.guardarViajeAereoRegreso(sesion.getOficinaActual().getId(), sesion.getUsuario().getId(), getViajeVO(),
		    getSolicitudViajeVO().getIdSolicitud(), vj, getItinerarioCompletoVoVuelta());
	} catch (Exception ex) {
	    UtilLog4j.log.info(this, "Exc: + + +  " + ex.getMessage());
	}
    }

    public DataModel traerViajeroPorSolicitud() {
	try {
	    if (getSolicitudViajeVO() != null) {
		setLista(new ListDataModel(sgViajeroImpl.getAllViajerosList(getSolicitudViajeVO().getIdSolicitud())));
		return getLista();
	    }
	} catch (Exception ex) {
	    return null;
	}
	return null;
    }

    /**
     * Traer todos los viajeros por solicitud
     *
     * @return
     */
    public DataModel traerTodosViajerosPorSolicitud() {
	try {
	    setLista(new ListDataModel(sgViajeroImpl.getAllViajerosList(getSolicitudViajeVO().getIdSolicitud())));
	    return getLista();
	} catch (Exception ex) {
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    /**
     * Traer todos los viajeros por viaje
     *
     * @return DataModel (DataModelViajeros)
     */
    public DataModel traerTodosViajerosPorViaje() {
	try {
	    //DataModelViajeros
	    setDataModelViajeros(new ListDataModel(sgViajeroImpl.getTravellersByTravel(getViajeVO().getId(), null)));
	    return getDataModelViajeros();
	} catch (Exception ex) {
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    //Traer viajes aereos por salir
    public int totalViajesAereosPorSalir() {
	return sgViajeImpl.getCountAirTravel(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR, this.sesion.getUsuario().getId());
    }

    @Deprecated
    public DataModel traerViajesAereosPorSalir() throws SIAException {
	List<ViajeVO> lv = null;
	lv = sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR, this.sesion.getUsuario().getId());
	setLista(new ListDataModel(lv));
	return getLista();
    }

    //Traer viajes aereos en proceso
    public int totalViajesAereosEnProceso() {
	return sgViajeImpl.getCountAirTravel(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_PROCESO, this.sesion.getUsuario().getId());
    }

    public DataModel traerViajesAereosEnProceso() throws SIAException {
	List<ViajeVO> lv = null;
	lv = sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_PROCESO, this.sesion.getUsuario().getId());

	setLista(new ListDataModel(lv));
	return getLista();
    }

    /**
     * MLUIS 06/11/2013
     */
    //Oficina destino
    public void finalizarViaje() {
	sgViajeImpl.finalizarViaje(getViajeVO().getId(), sesion.getUsuario().getId());
	sgViaje = sgViajeImpl.find(getViajeVO().getId());

//////////        if (getSgViaje().getRedondo().equals(Constantes.BOOLEAN_FALSE)
//////////                && getSgViaje().getSgViajeCiudad() == null && getSgViaje().getSgViajeLugar() == null) { // viaje sencillo
//////////            sgViajeroImpl.dejaUsuarioOficinaDestinoViajeSencillo(getViajeVO().getId(), sesion.getUsuario().getId());
//////////        }
    }
    //----------------------------------------- SOLICITUD DE VIAJE PANEL ----------------------

    public DataModel traerSolicitudViajes() {
	List<EstatusAprobacionVO> ls = sgEstatusAprobacionImpl.getApprovalStatusByOffice(sesion.getUsuario().getId(), sesion.getOficinaActual().getId(), Constantes.ESTATUS_PARA_HACER_VIAJE, true);
	setLista(new ListDataModel(ls));
	UtilLog4j.log.info(this, "Lsita solicitudes: " + getLista().getRowCount());
	return getLista();
    }

    public boolean verificaEstadoViajero() {
	boolean viaje = false;
	SgSolicitudViaje sol = sgSolicitudViajeImpl.find(getEstatusAprobacionVO().getIdSolicitud());
	try {
	    List<SgViajero> sg = sgViajeroImpl.getViajerosBySolicitudViajeList(sol, false);
	    for (SgViajero sgV : sg) {
		if (sgV.getSgViaje() != null) {
		    viaje = true;
		}
	    }
	} catch (Exception ex) {
	    UtilLog4j.log.info(this, "Ocurrio un error +" + ex.getMessage());
	}
	return viaje;
    }

    public boolean cancelarSolicitudPanel() throws Exception {
	setEstatusAprobacion(sgEstatusAprobacionImpl.find(getEstatusAprobacionVO().getId()));
	return sgEstatusAprobacionImpl.cancelarSolicitud(getEstatusAprobacion().getId(), getMotivoCancelacion(), sesion.getUsuario().getId(), false, Constantes.VIENE_SERVICIOS_GENERALES, Constantes.FALSE);

    }
//--------------------------------------------FIN VIAJES *-----------------------------

    public List<SgTipoTipoEspecifico> getSgTipoTipoEspecificoBySgTipoList(int idSgTipo) {
	return sgTipoTipoEspecificoImpl.traerPorTipo(sgTipoImpl.find(idSgTipo), Constantes.NO_ELIMINADO);
    }

    public List<SelectItem> traerInvitado(String cadenaDigitada) {
	UtilLog4j.log.info(this, "Cadena model: " + cadenaDigitada);
	return soporteProveedor.regresaInvitado(cadenaDigitada);
    }

    public InvitadoVO buscarInvitadoPorNombre() {
	return sgInvitadoImpl.buscarInvitado(getCadena());
    }

    public void deleteSolicitudViaje() throws SIAException, Exception {
	this.solicitudViajeService.delete(getSolicitudViajeVO().getIdSolicitud(), this.sesion.getUsuario().getId());
//////////////////////////////////////////////////        this.dataModel = new ListDataModel(this.solicitudViajeService.getSolicitudViajeVOByUsuarioAndStatus(Integer.valueOf(getInvitado()), this.sesion.getUsuario().getId(), 401, "id", true, false));
    }

    //Verifica la hora maxima para hacer solicitudes
    public int traeHoraMaximaParaSolicitud() {
	return sgTipoSolicitudViajeImpl.buscarPorId(getSolicitudViajeVO().getIdSgTipoSolicitudViaje()).getHoraMaxima();
    }
    //Verifica si la fecha de salida es mañana

    public boolean validaFechaSalidaEsManana() {
	return siManejoFechaImpl.validaSolicitaHoyParaMananaDespuesHora(getSolicitudViajeVO().getFechaSalida(), getSolicitudViajeVO().getIdSgTipoSolicitudViaje());
    }
    //Verifica si es sabado o domino

    public boolean buscaFinSemana() {
	boolean validado = false;
	Calendar cal = Calendar.getInstance();
	UtilLog4j.log.info(this, "Fecha salida " + getSolicitudViajeVO().getFechaSalida());
	cal.setTime(getSolicitudViajeVO().getFechaSalida());
	UtilLog4j.log.info(this, "CAl. " + cal.getTime() + " - - - - - - - - " + cal.get(cal.DAY_OF_WEEK));
	int diaFecha = cal.get(cal.DAY_OF_WEEK);
	List<TipoSolicitudTipoEspecificoVO> lt = sgTipoSolTipoEspImpl.buscarDiasSalida(getSolicitudViajeVO().getIdSgTipoSolicitudViaje());
	//  List<TipoSolicitudTipoEspecificoVO> lt2 = sgTipoSolTipoEspImpl.buscarDiasSalida(getSolicitudViajeVO().getIdSgTipoSolicitudViaje());
	for (TipoSolicitudTipoEspecificoVO tipoSolicitudTipoEspecificoVO : lt) {
	    if (diaFecha == Integer.parseInt(tipoSolicitudTipoEspecificoVO.getTipoEspecifico().trim())) {
		validado = true;
	    }
	}
	if (!validado && getSolicitudViajeVO().isRedondo()) {
	    cal.setTime(getSolicitudViajeVO().getFechaRegreso());
	    diaFecha = cal.get(cal.DAY_OF_WEEK);
	    for (TipoSolicitudTipoEspecificoVO tipoSolicitudTipoEspecificoVO : lt) {
		if (diaFecha == Integer.parseInt(tipoSolicitudTipoEspecificoVO.getTipoEspecifico().trim())) {
		    validado = true;
		}
	    }

	}
	return validado;
    }


    /*
     * Joel Rodriguez Inicia la solicitud de viaje.

     public boolean solicitarSolicitud() {
     try {
     UtilLog4j.log.info(this, "nueva fecha: " + getFinEstancia());

     return sgSolicitudViajeImpl.solicitarViaje(sesion.getUsuario(), getSolicitudViajeVO(),
     getListaViajerosVO(), getInvitadoVO(), getMensaje(),
     sesion.getIdRol(), getListaCasosIncumplidos());
     } catch (Exception e) {
     UtilLog4j.log.info(this, "No se pudo convertir la fecha de inicio y fin de estancia + " + e.getMessage());
     return false;
     }

     } */

    /*
     * public MotivoRetrasoVO findSgMotivoRetrasoBySgSolicitudViaje(int
     * idMotivoRetraso) { return
     * this.sgMotivoRetrasoImpl.findById(idMotivoRetraso); }
     */
    public boolean existSgInvitadoIntoSgSolicitudViaje() {
	return this.sgViajeroImpl.existSgViajeroBySgInvitadoAndSgSolicitudViaje(getInvitadoVO().getIdInvitado(), solicitudViajeVO.getIdSolicitud());
    }

    public void saveViajero() throws SIAException, Exception {
	// UtilLog4j.log.info(this, "IdSolicitudViaje: " + (this.solicitudViaje != null ? this.solicitudViaje.getId() : null));
	UtilLog4j.log.info(this, "Con estancia ? " + isEstancia());

//        this.sgViajeroImpl.save(this.usuario, this.sgInvitado, this.solicitudViaje.getId(), this.id, this.sgViajero.getObservacion(), this.sesion.getUsuario().getId(), isEstancia());
	if (getId() == 19) {
	    setMensaje(getUsuarioVO().getNombre());
	} else if (getId() == 20) {
	    setMensaje(getInvitadoVO().getNombre());
	}

	this.invitado = "";
	//      this.usuario = null;
	this.invitadoVO = null;

	//Recargar la lista de Viajeros
	//this.dataModelViajeros = new ListDataModel(this.sgViajeroImpl.getViajerosBySolicitudViajeList(this.solicitudViaje, false));
	this.dataModelViajeros = new ListDataModel(this.sgViajeroImpl.getAllViajesBySolicitud(getSolicitudViajeVO().getIdSolicitud()));
    }

    public void saveAllViajeros(List<ViajeroVO> viajeros) {
	UtilLog4j.log.info(this, "ViajeBeanModel.saveAllViajeros()");
	this.sgViajeroImpl.saveOrUpdateAllViajerosForSgSolicitudViaje(getSolicitudViajeVO().getIdSolicitud(), viajeros, this.sesion.getUsuario().getId());
    }

//    public List<SgViajero> getSGviajeroBySgSolicitudViajeList(SgSolicitudViaje sgSolicitudViaje, boolean eliminado) throws SIAException, Exception {
//        return this.sgViajeroImpl.getViajerosBySolicitudViajeList(this.solicitudViaje, false);
//    }
    public List<ViajeroVO> getSGviajeroBySgSolicitudViajeList() throws SIAException, Exception {
	return this.sgViajeroImpl.getAllViajerosList(getSolicitudViajeVO().getIdSolicitud());
    }

    public void changeEstateOfEstancia() {
	try {
	    if (getSgViajero() != null) {
		if (getSgViajero().isEstancia()) {
		    this.sgViajeroImpl.changeEstateOfEstancia(getSgViajero(), sesion.getUsuario(), false);
		} else {
		    this.sgViajeroImpl.changeEstateOfEstancia(getSgViajero(), sesion.getUsuario(), true);
		}
		this.dataModelViajeros = new ListDataModel(this.sgViajeroImpl.getAllViajerosList(solicitudViajeVO.getIdSolicitud()));
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepción al cambiar el estado de la estancia: " + e.getMessage());
	}
    }

    public void deleteViajero() throws SIAException, Exception {
	//this.sgViajero.setSgSolicitudViaje(this.solicitudViaje);
	this.sgViajeroImpl.delete(sgViajero.getId(), sesion.getUsuario().getId(), "");
//        this.sgViajero = new SgViajero();
	//Recargar la lista de Viajeros
	this.dataModelViajeros = new ListDataModel(this.sgViajeroImpl.getAllViajerosList(solicitudViajeVO.getIdSolicitud()));
    }

    public List<SelectItem> getListaUsuario(String cadenaDigitada) {
//        return soporteProveedorService.regresaUsuario(cadenaDigitada);
	return soporteProveedor.regresaUsuarioActivoVO(cadenaDigitada);//, -1, "nombre", true, true, false);
    }

    /**
     * Valida que el último Estatus de Aprobación de una Solicitud de Viaje sea
     * 440, true, false ó 430, true, false ó 420. Si se cumple alguna de estas
     * condiciones, es posible agregarle Viajeros a la Solicitud de Estancia
     * desde la opción de buscar Solicitudes de Viaje
     *
     * @return
     */
    public boolean validateEstatusAprobacionForAddTravellersToSgSolicitudViaje() {
	return true;
//        EstatusAprobacionVO = this.estatusAprobacionService.
    }

    /**
     * **
     ******************* APROBACIONES *****************************
     */
    //Comentado por que se convirtió a la forma nativa..
//    public void traerSolicitudesPorAprobar() {
//        UtilLog4j.log.info(this, "traerSolicitudesPorAprobar");
//        try {
//            this.dataModelSolicitudesPorProbar = new ListDataModel(this.estatusAprobacionService.traerEstatusAprobacionPorUsuario(sesion.getUsuario().getId(), getIdEstatusActivo()));
//
//            UtilLog4j.log.info(this, " datamodel de aprobaciones " + dataModelSolicitudesPorProbar.getRowCount());
//        } catch (Exception e) {
//            UtilLog4j.log.info(this, "excepcion al traer solicitudes " + e.getMessage());
//        }
//    }
    public SgEstatusAprobacion findEstatusAprobacion(int idEstatusAprobacion) {
	UtilLog4j.log.info(this, "buscar estatus de aprobacion");
	try {
	    return this.estatusAprobacionService.find(idEstatusAprobacion);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion al traer el estatus de aprobacion " + e.getMessage());
	    return null;
	}
    }

    public void traerSolicitudesPorAprobar() {
	UtilLog4j.log.info(this, "Estatus a consultar" + getIdEstatusActivo());
	try {
	    List<EstatusAprobacionVO> solicitudes = this.estatusAprobacionService.traerEstatusAprobacionPorUsuario(sesion.getUsuario().getId(), getIdEstatusActivo(), getOpcionViaje());
	    this.setDataModelSolicitudesPorProbar(new ListDataModel(solicitudes));
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepción al traer solicitudes por aprobar " + e.getMessage());
	    this.setDataModelSolicitudesPorProbar(null);
	}
    }

    // traer contador de solicitudes
    public int getTotalSolicitudesEstatus(int tipoDestino) {
	try {
	    return estatusAprobacionService.getTotalSolicitudesPorEstatusDestino(sesion.getUsuario().getId(), getIdEstatusActivo(), tipoDestino);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepción en traer total de solicitues por darle seguridad .." + e.getMessage());
	    return 0;
	}
    }

    public boolean usuarioResponsableCapacitacion() {
	return siUsuarioRolImpl.buscarRolPorUsuarioModulo(sesion.getUsuario().getId(), Constantes.MODULO_SGYL, "7", Constantes.AP_CAMPO_DEFAULT);
    }

    public void traerTodasSolicitudesPorJustificar() {
	List<EstatusAprobacionVO> solicitudes;
	solicitudes = this.estatusAprobacionService.traerEstatusAprobacionPorUsuario(sesion.getUsuario().getId(), getIdEstatusActivo(), Constantes.TODAS_SOLICITUDES_VIAJE);
	this.setDataModelSolicitudesPorProbar(new ListDataModel(solicitudes));
    }

    public void traerTodasSolicitudes() {
	List<EstatusAprobacionVO> solicitudes;
	solicitudes = this.estatusAprobacionService.traerEstatusAprobacionPorUsuario(sesion.getUsuario().getId(), getIdEstatusActivo(), getTipoDestino());
	this.setDataModelSolicitudesPorProbar(new ListDataModel(solicitudes));
    }

    public int getTotalSolicitudesPorJustificar() {
	try {
	    return estatusAprobacionService.getTotalSolicitudesPorEstatus(sesion.getUsuario().getId(), getIdEstatusActivo());
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepción en traer total de solicitues por darle seguridad .." + e.getMessage());
	    return 0;
	}
    }

    public int totalSolicitudViajeALugar() {
	return sgSolicitudViajeImpl.totalSgSolicitudViajeTerrestreALugar(sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE);
    }

    public boolean justificarSolicitudPorPorViolacionDeAprobacion() {
	try {
	    return sgEstatusAprobacionImpl.aprobarJustificandoSolicitud(getEstatusAprobacion().getId(), getEstatusAprobacion().getSgSolicitudViaje().getId(), getMotivoCancelacion(), sesion.getUsuario().getId());
	} catch (Exception ex) {
	    UtilLog4j.log.info(this, "Excepcion al cancelar una soliciud desde aprobar por violacion " + ex.getMessage());
	    return false;
	}
    }

    public boolean aprobarSolicitud() {
	UtilLog4j.log.info(this, "idEstatus a aprobar " + getEstatusAprobacion().getId());
	try {
	    if (estatusAprobacionService.aprobarSolicitud(getEstatusAprobacion().getId(), sesion.getUsuario().getId())) {
		traerSolicitudesPorAprobar();
		setFlag(true); //Indica si se aprobó la solicitud
		setAgregarResponsable(true); //Para indicar que se deben mostrar los mensajes
		return true;
	    } else {
		setFlag(false);
		return false;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    UtilLog4j.log.info(this, "excepcion en aprobar " + e.getMessage());
	    setAgregarResponsable(false);
	    setFlag(false);
	    return false;
	}
    }

    /**
     * Creo: NLopez
     *
     * @return
     */
    public boolean aprobarViajeDireccion() {

	try {
	    sgViajeImpl.updateViajeDireccion(getViajeVO().getId(), sesion.getUsuario());

	    setFlag(true); //Indica si se aprobó la solicitud
	    setAgregarResponsable(true); //Para indicar que se deben mostrar los mensajes

	    return true;

	} catch (Exception e) {
	    e.printStackTrace();
	    UtilLog4j.log.error(this, "excepcion en aprobar ", e);
	    setAgregarResponsable(false);
	    setFlag(false);
	    return false;
	}
    }

    /**
     * guarda la justificacion de salida en tabla sgMotivoRetraso mensaje
     * :representa la justificacion getIdGerencia() : es el id de sgLugar
     * seleccioando
     *
     * @return
     */
    public boolean guardarJustificacionSolicitudViaje() {
	try {
	    Calendar horaReunion = Calendar.getInstance();
	    horaReunion.set(Calendar.HOUR_OF_DAY, this.horaSalida);
	    horaReunion.set(Calendar.MINUTE, this.minutoSalida);

	    if (this.sgSolicitudViajeImpl.guardarJustificacionSolicitudViaje(this.solicitudViajeVO.getIdSolicitud(), mensaje, getIdGerencia(), invitadoVO.getIdInvitado(), horaReunion.getTime(), sesion.getUsuario().getId())) {
		UtilLog4j.log.info(this, "La justificacion fue agregada a la solicitud..");
		traerTodasSolicitudesPorJustificar();
	    }
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    UtilLog4j.log.info(this, "excepcion en aprobar " + e.getMessage());
	    return false;
	}
    }

    public boolean enviarJustificacionSolicitudViaje() {
	try {
//            if (estatusAprobacionService.enviarJustificacionSolicitud(getEstatusAprobacion(), motivoCancelacion, sesion.getUsuario())) {
//                traerTodasSolicitudesPorJustificar();
//            }
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    UtilLog4j.log.info(this, "excepcion en aprobar " + e.getMessage());
	    return false;
	}
    }

    public boolean cancelarSolicitud() {
	UtilLog4j.log.info(this, "Cancelar Solicitud");
	try {
	    if (estatusAprobacionService.cancelarSolicitud(getEstatusAprobacion().getId(), getMotivoCancelacion(), sesion.getUsuario().getId(), false, Constantes.VIENE_SERVICIOS_GENERALES,Constantes.FALSE)) {
		traerSolicitudesPorAprobar();
	    }
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.error(this, "", e);
	    return false;
	}
    }

//    public boolean devolverSolicitud() {
//        try {                                                                                                  //idSiOperacion (4 es DEVOLVER)
//            if (estatusAprobacionService.devolverSolicitud(getEstatusAprobacion(), getMotivoCancelacion(), 4, sesion.getUsuario(), getEstatusDevolverPorGerenteArea())) {
//                traerSolicitudesPorAprobar();
//            }
//            return true;
//        } catch (Exception e) {
//            UtilLog4j.log.info(this, "excepcion al Devolver la solicitud " + e.getMessage());
//            return false;
//        }
//    }

    /*
     * Trae el total de las solicitudes con el estatus 401 : PENDIENTE
     */
    public int getTotalSgSolicitudViaje401() {
	UtilLog4j.log.info(this, "getTotalSgSolicitudViaje401");

	int totalSVTerrestreOficina = this.solicitudViajeService.totalSgSolicitudViajeTerretreToOficina(sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE);
	int totalSVTerrestreCiudad = this.solicitudViajeService.totalSgSolicitudViajeTerrestreToCiudad(sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE);
	int totalSVAereo = this.solicitudViajeService.totalSgSolicitudViajeToAereos(sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE);

	return totalSVTerrestreOficina + totalSVTerrestreCiudad + totalSVAereo;
    }

    public int getTotalSgSolicitudViaje401Ciudad() {
	return this.solicitudViajeService.getTotalSgSolicitudViajeCiudadByEstatusAndUsuario(sesion.getUsuario().getId(), Constantes.ESTATUS_PENDIENTE);
    }

    /*
     * Trae el total de las solicitudes con el estatus 420 : SOLICITADA
     */
    public int getTotalSolicitudesPorAprobar() {
	UtilLog4j.log.info(this, "getTotalSolicitudesPorAprobar");
	try {
	    return estatusAprobacionService.getTotalSolicitudesPorEstatus(sesion.getUsuario().getId(), Constantes.ESTATUS_APROBAR);
//            }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepción en traer total de solicitues" + e.getMessage());
	    e.printStackTrace();
	    return 0;
	}
    }

    /*
     * Trae el total de las solicitudes con el estatus 430 : SEGURIDAD$
     */
    public int getTotalSolicitudesPorSeguridad() {
	UtilLog4j.log.info(this, "getTotalSolicitudesPorSeguridad");
	try {
	    return estatusAprobacionService.getTotalSolicitudesPorEstatus(sesion.getUsuario().getId(), Constantes.ESTATUS_SEGURIDAD);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepción en traer total de solicitues por darle seguridad .." + e.getMessage());
	    return 0;
	}
    }

    /*
     * Trae el total de las solicitudes con el estatus 440 : FINALIZAR
     */
    public int getTotalSolicitudesPorFinalizar() {
	UtilLog4j.log.info(this, "getTotalSolicitudesPorFinalizar");
	try {
	    return estatusAprobacionService.getTotalSolicitudesPorEstatus(sesion.getUsuario().getId(), Constantes.ESTATUS_AUTORIZAR);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepción en traer total de solicitues por darle finalizar .." + e.getMessage());
	    return 0;
	}
    }

    public int getTotalCambiosItinerario() {

	try {
	    return sgCambioItinerarioImpl.getTotalCambiosItinerario();
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepción en traer total de solicitues por darle finalizar .." + e.getMessage());
	    return 0;
	}

    }

    /*
     * Trae el total de las solicitudes por estatus
     */
    public int getTotalSolicitudesPorEstatus(Integer estatus) {
	UtilLog4j.log.info(this, "getTotalSolicitudesPorFinalizar");
	try {
	    return estatusAprobacionService.getTotalSolicitudesPorEstatus(sesion.getUsuario().getId(), estatus);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepción en traer total de solicitues por darle finalizar .." + e.getMessage());
	    return 0;
	}
    }

    public int getTotalSolicitudesPorVistoBueno() {
	UtilLog4j.log.info(this, "getTotalSolicitudesPorVistoBueno");
	try {
	    return estatusAprobacionService.getTotalSolicitudesPorEstatus(sesion.getUsuario().getId(), Constantes.ESTATUS_VISTO_BUENO);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en traer total de solicitues por Visto bueno .." + e.getMessage());
	    return 0;
	}
    }

    public void traerHistorialAprobaciones() {
	UtilLog4j.log.info(this, "traerHistorialAprobaciones");
	if (solicitudViajeVO != null) {
	    List<EstatusAprobacionVO> li = sgEstatusAprobacionImpl.traerHistorialEstatusAprobacionPorSolicitudViaje(this.solicitudViajeVO.getIdSolicitud(), true);
	    this.estatusActualVO = sgEstatusAprobacionImpl.traerEstatusPorAutorizarSolicitudViaje(solicitudViajeVO.getIdSolicitud());
//            UtilLog4j.log.info(this, ""+li.size());
	    setDataModelSolcitudesTemporales(new ListDataModel(li));
	}
    }

    /**
     * Metodo que carga los detalles de una solicitud de viaje ya sea tipo aerea
     * o terrestre
     *
     * @param idSolicitudSeleccionada
     */
    public void cargarDetalleSolicitudViaje(int idSolicitudSeleccionada) {
	UtilLog4j.log.info(this, "Id solicitud seleccionada " + idSolicitudSeleccionada);
	this.setSolicitudViajeVO(this.buscarPorIdSolicitudViaje(idSolicitudSeleccionada));
	UtilLog4j.log.info(this, "SOLICITUD ENCONTRADA " + getSolicitudViajeVO().getCodigo());
	//cargar viajeros ala lista
	this.cargarViajerosPorSolicitudVo();
	this.traerHistorialAprobaciones();
	switch (getOpcionViaje()) {
	case Constantes.SOLICITUDES_TERRESTRE_OFICINA: //Viajes terrestres a oficina
	    UtilLog4j.log.info(this, "DETALLE DE OFICINA A OFICINA");
	    this.controlaPopUpTrue("popupDetalleSolicitudViaje");
	    break;
	case Constantes.SOLICITUDES_TERRESTRE_CIUDAD: //Viajes terrestres a Ciudades
	    this.findSolicitudViajeCiudad(); //<< buscar ciudad destino
	    UtilLog4j.log.info(this, "DETALLE DE OFICINA A CIUDAD");
	    this.controlaPopUpTrue("popupDetalleSolicitudViaje");
	    break;
	case Constantes.SOLICITUDES_AEREA: //Viajes Aéreos
	    UtilLog4j.log.info(this, "DETALLE AEREO");
	    this.findSgItinerarioBySgSolicitudViaje(Constantes.CARGAR_ESCALAS);
	    this.findSgItinerarioVueltaBySgSolicitudViaje();
	    this.controlaPopUpTrue("popupDetalleSolicitudViajeAereo");
	    break;
	case Constantes.SOLICITUDES_TERRESTRES: //Viajes terrestres a oficina
	    UtilLog4j.log.info(this, "DETALLE DE OFICINA A OFICINA");
	    this.controlaPopUpTrue("popupDetalleSolicitudViaje");
	    break;
	}
    }

    /*
     * *
     *
     *
     * Metodo que se utiliza al momento de mostrar el detalle de la solicitud de
     * viaje Para llamar este metodo es necesario tener memoria en el objeto
     * SolicutudVo
     */
    public void cargarViajerosPorSolicitudVo() {
	UtilLog4j.log.info(this, "viajeros de la solicitud " + this.solicitudViajeVO.getIdSolicitud());
	//pasar todos los viajeros a la lista para servir el viaje
	setDataModelViajerosDetalle(new ListDataModel(this.sgViajeroImpl.getAllViajerosList(this.solicitudViajeVO.getIdSolicitud())));
    }

    public boolean buscarEstatusDeSolicitud(Integer idSolicitud) {
	this.solicitudFinalizada = sgEstatusAprobacionImpl.findSgSolicitudConEstatusAprobado(idSolicitud, 440);
	return this.isSolicitudFinalizada();
    }

    /**
     * ************ SOLICITUDES DE ESTANCIA ********************
     */
    public void nuevaSolicitudEstancia() {
	UtilLog4j.log.info(this, "nuevaSolicitudEstancia");
	//Limpiando variables
	solicitudEstanciaVo = new SgSolicitudEstanciaVo();

	if (getSolicitudViajeVO().isRedondo()) {
	    //getSolicitudEstanciaVo().setFinEstancia(getSolicitudViajeVO().getFechaRegreso());
	    setFinEstancia(siManejoFechaImpl.convertirFechaStringddMMyyyy(getSolicitudViajeVO().getFechaRegreso()));
	} else {
	    //getSolicitudEstanciaVo().setFinEstancia(getSolicitudViajeVO().getFechaSalida());
	    setFinEstancia(siManejoFechaImpl.convertirFechaStringddMMyyyy(getSolicitudViajeVO().getFechaSalida()));
	}
	if ((getSolicitudViajeVO().getIdSgTipoSolicitudViaje() == Constantes.SOLICITUDES_TERRESTRE
		&& getSolicitudViajeVO().getIdOficinaDestino() == 0)
		|| (getSolicitudViajeVO().getIdSgTipoEspecifico() == Constantes.SOLICITUDES_AEREA)) {
	    getSolicitudEstanciaVo().setIdSgOfina(getSolicitudViajeVO().getIdOficinaOrigen());
	} else {
	    if (getSolicitudViajeVO().getIdOficinaDestino() == Constantes.ID_OFICINA_REY_INFRA) {
		getSolicitudEstanciaVo().setIdSgOfina(Constantes.ID_OFICINA_REY_PRINCIPAL);
	    } else {
		getSolicitudEstanciaVo().setIdSgOfina(getSolicitudViajeVO().getIdOficinaDestino());
	    }
	}
	if (getSolicitudViajeVO().getIdSgTipoEspecifico() == Constantes.SOLICITUDES_TERRESTRE) {
	    getSolicitudEstanciaVo().setIdSgMotivo(getSolicitudViajeVO().getIdSgMotivo());
	} else {
	    getSolicitudEstanciaVo().setIdSgMotivo(Constantes.MOTIVO_AEREO);
	}

	//getSolicitudEstanciaVo().setInicioEstancia(getSolicitudViajeVO().getFechaSalida());
	setInicioEstancia(siManejoFechaImpl.convertirFechaStringddMMyyyy(getSolicitudViajeVO().getFechaSalida()));
	setLista(null);
	setDataModel(null);
    }

    public SemaforoVo buscarSemaforoActual() {
	int idR = getSolicitudViajeVO().getIdRutaTerrestre();
	return sgEstadoSemaforoImpl.estadoActual(idR);
    }

    public SemaforoVo buscarEstadoActualSemaforo() {
	setSemaforoVo(sgEstadoSemaforoImpl.estadoActual(getIdRuta()));
	return getSemaforoVo();
    }

    public SgViajero findViajero(int idViajero) {
	try {
	    return sgViajeroImpl.find(idViajero);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al buscar un viajero " + e.getMessage());
	    return null;
	}
    }

    public SgOficina findSgOficinaById(int idSgOficina) {
	return this.sgOficinaImpl.find(idSgOficina);
    }

    private List<ViajeroVO> getViajerosVOWithLougueList() throws SIAException {
	UtilLog4j.log.info(this, "getViajerosVOWithLougueList");
	if (getSolicitudViajeVO() != null) {
	    setListaViajerosVO(sgViajeroImpl.getViajerosWithEstanciaBySolicitudViajeList(getSolicitudViajeVO().getIdSolicitud(), Constantes.BOOLEAN_TRUE));
	    return getListaViajerosVO();
	}
	return null;

    }

    public int getViajerosConEstancia() throws SIAException {
	UtilLog4j.log.info(this, "getViajerosConEstancia");
	getViajerosVOWithLougueList();
	setDataModelViajeros(new ListDataModel(getListaViajerosVO()));
	if (getListaViajerosVO() != null) {
	    return getListaViajerosVO().size();
	} else {
	    return 0;
	}
    }

    public void changeEstateOfEstanciaExterno() {
	try {
	    if (getViajeroVO() != null) {
		if (getViajeroVO().isEstancia()) {
		    this.sgViajeroImpl.changeEstateOfEstancia((findViajero(getViajeroVO().getId())), sesion.getUsuario(), false);
		    //quitar de la lista temporal
		    mapViajeros.remove(getViajeroVO());
		    //getListaViajerosVO().remov;
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al cambiar el estabdi de la estancia " + e.getMessage());
	}
    }

    // public boolean crearSolicitudTemporal() throws SIAException {
    public void crearSolicitudTemporal() throws SIAException {
	mapViajeros.put(getSolicitudEstanciaVo(), getListaViajerosVO());
	filaSeleccionada.clear();
	UtilLog4j.log.info(this, "eliminar la lista agregada de la lista principal");
	setDataModelViajeros(new ListDataModel(getListaViajerosVO()));
	getListaSolicitudesTemporales();
    }

    public DataModel getListaSolicitudesTemporales() {
	UtilLog4j.log.info(this, "##getListaSolicitudesTemporales##");
	List<SgSolicitudEstancia> listaKeys = null;
	listaKeys = new ArrayList<SgSolicitudEstancia>();

	Iterator it = mapViajeros.entrySet().iterator();
	while (it.hasNext()) {
	    Map.Entry ent = (Map.Entry) it.next();
	    listaKeys.add((SgSolicitudEstancia) ent.getKey());
	}
	setDataModelSolcitudesTemporales(new ListDataModel(listaKeys));
	return getDataModelSolcitudesTemporales();
    }

    public DataModel getListaViajerosVoTemporalesPorSolicitud() {

	List<ViajeroVO> lTemp = mapViajeros.get(solicitudEstanciaVo);
	this.setDataModel(new ListDataModel(lTemp));
	return getDataModel();
    }

    public boolean validarFilasSeleccionadas() {
	boolean ret = false;
	for (ViajeroVO v : getListaViajerosVO()) {
	    if (filaSeleccionada.get(v.getId()).booleanValue()) {
		ret = true;
		UtilLog4j.log.info(this, "Existen filas seleccionadas");
		break;
	    }
	}
	return ret;
    }

    //     *************** ITINERARIO - INICIO    **********************
//    public boolean haveEscalasSgItinerario() {
//
//        //List<SgDetalleItinerario> tmp = this.sgDetalleItinerarioImpl.findBySgItinerario(this.sgItinerario, "id", true, false);
//        //return ((tmp != null && !tmp.isEmpty()) ? true : false);
//        ItinerarioCompletoVo tmp = sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(this.sgItinerario.getId(), true, "id");
//        return ((tmp.getEscalas() != null && !tmp.getEscalas().isEmpty()) ? true : false);
//    }
    public List<SgAerolinea> getAllSgAerolineaList() {
	return this.sgAerolineaImpl.findAll("nombre", true, false);
    }

//    public void getAllSgDetalleItinerarioBySgItinerario() {
//        //if (getDataModel() == null) {
//        if (getSgItinerario() != null) {
//            //setDataModel(new ListDataModel(this.sgDetalleItinerarioImpl.findBySgItinerario(this.sgItinerario, "id", true, false)));
//            setDataModelDetalle(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(this.sgItinerario.getId(), true, "id").getEscalas()));
//        }
//        //}
//    }
//    public void getAllSgDetalleItinerarioBySgItinerarioVuelta() {
//        if (getLista() == null) {
//            if (getSgItinerarioVuelta() != null) {
//                //setLista(new ListDataModel(this.sgDetalleItinerarioImpl.findBySgItinerario(this.sgItinerarioVuelta, "id", true, false)));
//                setLista(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(this.sgItinerarioVuelta.getId(), true, "id").getEscalas()));
//            }
//        }
//    }
    public Calendar clearCalendar(Calendar calendar) {
	return this.siManejoFechaImpl.cleanCalendar(calendar);
    }

    private void pasarVariablesAItinerario() {
	try {
	    Calendar cFechaSalida = Calendar.getInstance();
	    cFechaSalida.setTime(siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaSalida()));
	    clearCalendar(cFechaSalida);
	    cFechaSalida.set(Calendar.HOUR_OF_DAY, getHoraSalida());
	    cFechaSalida.set(Calendar.MINUTE, getMinutoSalida());
	    UtilLog4j.log.info(this, "formPopupCreateSgDetalleItinerario.cFechaSalida: " + cFechaSalida.getTime());

	    Calendar cFechaRegreso = Calendar.getInstance();
	    cFechaRegreso.setTime(siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaRegreso()));
	    clearCalendar(cFechaRegreso);
	    cFechaRegreso.set(Calendar.HOUR_OF_DAY, getHoraRegreso());
	    cFechaRegreso.set(Calendar.MINUTE, getMinutoRegreso());
	    UtilLog4j.log.info(this, "formPopupCreateSgDetalleItinerario.cFechaRegreso: " + cFechaRegreso.getTime());
	    sgDetalleItinerario.setFechaSalida(siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaSalida()));

	    Calendar cHoraSalida = Calendar.getInstance();
	    clearCalendar(cHoraSalida);
	    cHoraSalida.setTime(getSgDetalleItinerario().getFechaSalida());
	    cHoraSalida.set(Calendar.HOUR_OF_DAY, this.horaSalida);
	    cHoraSalida.set(Calendar.MINUTE, this.minutoSalida);

	    sgDetalleItinerario.setFechaLlegada(siManejoFechaImpl.convertirStringFechaddMMyyyy(getFechaRegreso()));
	    Calendar cHoraRegreso = Calendar.getInstance();
	    clearCalendar(cHoraRegreso);
	    cHoraRegreso.setTime(getSgDetalleItinerario().getFechaLlegada());
	    cHoraRegreso.set(Calendar.HOUR_OF_DAY, this.horaRegreso);
	    cHoraRegreso.set(Calendar.MINUTE, this.minutoRegreso);
	    UtilLog4j.log.info(this, "Hora Salida: " + cHoraSalida.getTime());
	    UtilLog4j.log.info(this, "Hora Regreso: " + cHoraRegreso.getTime());

	    this.sgDetalleItinerario.setHoraSalida(cHoraSalida.getTime());
	    this.sgDetalleItinerario.setHoraLlegada(cHoraRegreso.getTime());

	    //Asignar tiempo de vuelo
	    Long tiempoVuelo = this.siManejoFechaImpl.getDiffInMinutes(cHoraSalida, cHoraRegreso);
	    Double tv = (double) (tiempoVuelo.doubleValue() / 60);
	    UtilLog4j.log.info(this, "Tiempo Vuelo in Minutes: " + tiempoVuelo);
	    UtilLog4j.log.info(this, "Tiempo Vuelo in Horas: " + tv);

	    getSgDetalleItinerario().setTiempoVuelo(tv);
	    getSgDetalleItinerario().setNumeroVuelo(getNumeroVuelo());

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Error:  en la conversion de horas: " + e.getMessage());
	}
    }

    public void saveSgDetalleItinerario() {
	//Crear Times para hora y minuto de salida y regreso
	pasarVariablesAItinerario();
	if (isPopup()) {
	    this.sgDetalleItinerarioImpl.save(getSgDetalleItinerario(), getItinerarioCompletoVoIda().getId().intValue(), getId(), getTamanioLista(), getIdNoticia(), this.sesion.getUsuario().getId());
	    //setDataModel(new ListDataModel(this.sgDetalleItinerarioImpl.findBySgItinerario(getSgItinerario(), "id", true, false)));
	    setDataModelDetalle(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getItinerarioCompletoVoIda().getId(), true, "id").getEscalas()));
	} else {
	    this.sgDetalleItinerarioImpl.save(getSgDetalleItinerario(), getItinerarioCompletoVoVuelta().getId().intValue(), getId(), getTamanioLista(), getIdNoticia(), this.sesion.getUsuario().getId());
	    //setLista(new ListDataModel(this.sgDetalleItinerarioImpl.findBySgItinerario(getSgItinerarioVuelta(), "id", true, false)));
	    setLista(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getItinerarioCompletoVoVuelta().getId(), true, "id").getEscalas()));
	}
    }

    public void notificaCambioItinerario() {
	sgItinerarioImpl.notificaCambioItinerario(getSolicitudViajeVO().getIdSolicitud(),true);
    }

    public void notificaCambioItinerarioVuelta() {
	sgItinerarioImpl.notificaCambioItinerario(getSolicitudViajeVO().getIdSolicitud(),false);
    }

    public void updateSgDetalleItinerario() {
	pasarVariablesAItinerario();
	if (isPopup()) { // Ida
	    this.sgDetalleItinerarioImpl.update(getSgDetalleItinerario().getId().intValue(), getItinerarioCompletoVoIda().getId(), getId(), getTamanioLista(), getIdNoticia(),
		    getSgDetalleItinerario().getNumeroVuelo(), getSgDetalleItinerario().getFechaSalida(), getSgDetalleItinerario().getHoraSalida(), getSgDetalleItinerario().getFechaLlegada(), getSgDetalleItinerario().getHoraLlegada(), getSgDetalleItinerario().getTiempoVuelo(), this.sesion.getUsuario().getId());
	    setDataModelDetalle(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getItinerarioCompletoVoIda().getId(), true, "id").getEscalas()));
	} else { // vuelta
	    this.sgDetalleItinerarioImpl.update(getSgDetalleItinerario().getId().intValue(), getItinerarioCompletoVoVuelta().getId(), getId(), getTamanioLista(), getIdNoticia(),
		    getSgDetalleItinerario().getNumeroVuelo(), getSgDetalleItinerario().getFechaSalida(), getSgDetalleItinerario().getHoraSalida(), getSgDetalleItinerario().getFechaLlegada(), getSgDetalleItinerario().getHoraLlegada(), getSgDetalleItinerario().getTiempoVuelo(), this.sesion.getUsuario().getId());
	    //setLista(new ListDataModel(this.sgDetalleItinerarioImpl.findBySgItinerario(getSgItinerarioVuelta(), "id", true, false)));
	    setDataModel(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getItinerarioCompletoVoVuelta().getId(), true, "id").getEscalas()));
	}
    }

    public void deleteSgDetalleItinerario() throws ItemUsedBySystemException {
	if (isPopup()) {
	    this.sgDetalleItinerarioImpl.delete(this.sgDetalleItinerario, this.sesion.getUsuario().getId());
	    //setDataModel(new ListDataModel(this.sgDetalleItinerarioImpl.findBySgItinerario(getSgItinerario(), "id", true, false)));
	    setDataModelDetalle(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getItinerarioCompletoVoIda().getId(), true, "id").getEscalas()));
	    setPopup(false);
	} else {
	    this.sgDetalleItinerarioImpl.delete(this.sgDetalleItinerario, this.sesion.getUsuario().getId());
	    //setLista(new ListDataModel(this.sgDetalleItinerarioImpl.findBySgItinerario(getSgItinerarioVuelta(), "id", true, false)));
	    setDataModel(new ListDataModel(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getItinerarioCompletoVoVuelta().getId(), true, "id").getEscalas()));
	}

    }

    public boolean cancelarSolicitudPorPorViolacionDeAprobacion(boolean clonarSolicitud) {
	try {
	    //setMotivoCancelacion((clonarSolicitud ? "Finalizada " : "Cancelada") + Constantes.mensajeCancelacionAutomaticaSolicitudViajesinMotivoRetraso);
	    if (estatusAprobacionService.cancelarSolicitud(getEstatusAprobacion().getId(), getMotivoCancelacion(), sesion.getUsuario().getId(), false, Constantes.VIENE_SERVICIOS_GENERALES,Constantes.FALSE)) {
		if (clonarSolicitud) {
		    sgSolicitudViajeImpl.clonarSolicitudViaje(getSolicitudViajeVO().getIdSolicitud(), 0, 0, null);
		}
	    }
	    return true;
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, "Excepcion al cancelar una soliciud desde aprobar por violacion " + ex.getMessage());
	    return false;
	}
    }

    public boolean validarAprobacionOCancelacionSolicitud() {  //te enconte;
	boolean re = false;
	//si sale hoy o mañana y la aprobacion es despues de las 5:00pm y no tiene motivo de retraso CANCELAR
	if (solicitudViajeVO.getIdEstatus() == Constantes.ESTATUS_JUSTIFICAR) {
	    return re;
	} else {
	    if (siManejoFechaImpl.dateIsTomorrow(solicitudViajeVO.getFechaSalida())
		    && (siManejoFechaImpl.validaHoraMaximaAprobacion(sesion.getIdRol(), Constantes.HORA_MAXIMA_APROBACION))) {
		//validar la hora de aprobacion
		//proceder a cancelar
		re = true;
		UtilLog4j.log.info(this, "La solicitud no contiene un motivo de retraso y su fecha de salida es hoy o mañana ");
	    } else if (siManejoFechaImpl.dayIsToday(solicitudViajeVO.getFechaSalida())
		    && (siManejoFechaImpl.validaHoraMaximaAprobacion(sesion.getIdRol(), Constantes.HORA_MAXIMA_APROBACION))) {
		re = true;
	    } else {
		re = false;
	    }
	}
	return re;
    }

    /**
     * Creo: NLopez
     */
    public void updateSGDetalleItinerarioHistorial() {
	//getAllSgDetalleItinerarioBySgItinerario();

	List<DetalleItinerarioCompletoVo> escalas = sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(this.itinerarioCompletoVoIda.getId(), true, "id").getEscalas();
	UtilLog4j.log.debug(this, escalas.toString());
	for (DetalleItinerarioCompletoVo escala : escalas) {
	    SgDetalleItinerario dit;
	    UtilLog4j.log.debug(this, "ID Detalle Itinerario: " + escala.getId());

	    dit = sgDetalleItinerarioImpl.find(escala.getId());
	    dit.setHistorial(Constantes.BOOLEAN_TRUE);

	    this.sgDetalleItinerarioImpl.update(dit, dit.getSgItinerario().getId(), dit.getSgAerolinea().getId(), this.sesion.getUsuario().getId());
	}

	List<CambioItinerarioVO> cambios = sgCambioItinerarioImpl.getCambioItinerarioVOPorItinerario(this.itinerarioCompletoVoIda.getId());

	for (CambioItinerarioVO cambioVo : cambios) {
	    SgCambioItinerario cambio;
	    cambio = sgCambioItinerarioImpl.find(cambioVo.getId());
	    cambio.setHistorial(Constantes.BOOLEAN_TRUE);

	    this.sgCambioItinerarioImpl.update(cambio, this.itinerarioCompletoVoIda.getId(), this.sesion.getUsuario().getId());
	}
    }

    /**
     * Creo: NLopez
     */
    public void getHistorialItinerarioPorSolicitud(Integer idSolicitud) {
	setLista(new ListDataModel(sgItinerarioImpl.getHistorialItinerarioPorSolicitud(idSolicitud)));
    }

    //******************************VIAJES A OTRAS CIUDADES **************
    //Cargar viajeros de la solicitud de viaje terrestre a ciudad
    public boolean validaFechaSalidaViajeCiudad() {
	if (siManejoFechaImpl.compare(getSolicitudViajeVO().getFechaSalida(), new Date()) == -1) {
	    return false;
	} else {
	    return true;
	}
    }

    public DataModel cargarViajerosTerrestreDeSolicitud() {
	UtilLog4j.log.info(this, "cargarViajerosTerrestresSolicitud");
	List<ViajeroVO> listViajerosVo = this.sgViajeroImpl.getAllViajerosList(this.solicitudViajeVO.getIdSolicitud()); //pasar todos los viajeros a la lista para servir el viaje
	UtilLog4j.log.info(this, "lista de viajeros " + listViajerosVo.size());
	setLista(new ListDataModel(listViajerosVo));
	return getLista();
    }

    public DataModel traerSolicitudViajesCiudades() {
	List<EstatusAprobacionVO> ls = sgEstatusAprobacionImpl.getSolicitudesViajesCiudades(sesion.getOficinaActual().getId(), Constantes.ESTATUS_PARA_HACER_VIAJE);
	setLista(new ListDataModel(ls));
	UtilLog4j.log.info(this, "Lsita solicitudes: " + getLista().getRowCount());
	return getLista();
    }

    public void crearViajeCiudad() {
	UtilLog4j.log.info(this, "crear viaje ciudad");
	List<ViajeroVO> lv = dataModelAList(getLista());
	sgViaje = sgViajeImpl.saveCompanyCarCiudad(sesion.getUsuario(),
		getSgViaje().getResponsable(),
		getVehiculoVO(),
		getSolicitudViajeVO().getIdSolicitud(),
		sesion.getOficinaActual().getId(),
		getOpcionSeleccionada(),
		getIdRuta(),
		getSgViaje().getFechaProgramada(),
		getSgViaje().getFechaRegreso() != null ? sgViaje.getFechaRegreso() : null,
		getSgViaje().getHoraProgramada(),
		getSgViaje().getHoraRegreso() != null ? sgViaje.getHoraRegreso() : null,
		getRedondo(), lv, getTelefono());
	//}
    }

    /**
     * *********************************************************************
     */
    //     *************** GASTO_VIAJE - INICIO    ********************** */
    public List<SgTipoTipoEspecifico> getSgTipoTipoEspecificoBySgTipoAndPagoList(int idSgTipo, boolean isPago) {
	return sgTipoTipoEspecificoImpl.traerPorTipoPago(sgTipoImpl.find(idSgTipo), Constantes.NO_ELIMINADO, (isPago ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE));
    }

    public List<ViajeroVO> getAllSgViajeroBySgViajeFromViajeVo() {
	return (this.viajeVO != null ? this.sgViajeroImpl.getTravellersByTravel(this.viajeVO.getId(), null) : null);
    }

    public void getAllSgGastoViajeBySgViaje() {
	if (this.sgViaje != null && this.lista == null) {
	    setLista(new ListDataModel(this.sgGastoViajeImpl.findAllSgGastoViajeBySgViajeNative(getSgViaje().getId().intValue(), "id", true, false)));
	} else if (this.sgViaje == null) {
	    setLista(null);
	}
    }

    public void saveSgGastoViaje() {
	//Guardar Kilometraje
	if (getIdSiOperacion() == 8 && getVehiculoVO() != null) {
	    SgKilometraje sgKilometraje = sgKilometrajeImpl.createKilometrajeActual(getVehiculoVO().getId(), 8, getTamanioLista(), this.sesion.getUsuario());
	    this.sgViajeKilometrajeImpl.save(getSgViaje().getId().intValue(), sgKilometraje.getId().intValue(), this.sesion.getUsuario().getId());
	}

	this.sgGastoViajeImpl.save(getBigDecimal(), getMotivoCancelacion(), getIdOficina(), getSgViaje().getId(), getIdMotivo(), getIdSiOperacion(), this.sesion.getUsuario().getId());
	setLista(new ListDataModel(this.sgGastoViajeImpl.findAllSgGastoViajeBySgViajeNative(getSgViaje().getId().intValue(), "id", true, false)));
    }

    public void updateSgGastoViaje() {
	this.sgGastoViajeImpl.update(getSgGastoViajeVO().getId(), getSgGastoViajeVO().getImporte(), getSgGastoViajeVO().getObservacion(), getSgGastoViajeVO().getIdSgTipoEspecifico(), getSgGastoViajeVO().getIdMoneda(), this.sesion.getUsuario().getId());
	setLista(new ListDataModel(this.sgGastoViajeImpl.findAllSgGastoViajeBySgViajeNative(getSgViaje().getId().intValue(), "id", true, false)));
    }

    public void deleteSgGastoViaje() throws SIAException {
	this.sgGastoViajeImpl.delete(getSgGastoViajeVO().getId(), this.sesion.getUsuario().getId());
	if (getSgGastoViajeVO().getIdSiAdjunto() > 0) {
	    //Eliminar el archivo físico
	    SiAdjunto siAdjunto = this.siAdjuntoImpl.find(this.sgGastoViajeVO.getIdSiAdjunto());
	    if (physicallyDeleteFile(this.siParametroImpl.find(1).getUploadDirectory() + siAdjunto.getUrl())) {
		//Eliminar carpeta
		if (physicallyDeleteFile(this.siParametroImpl.find(1).getUploadDirectory() + Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/GastoViaje/" + this.sgGastoViajeVO.getId())) {
		    this.sgGastoViajeImpl.deleteSiAdjuntoFromSgGastoViaje(this.sgGastoViajeVO.getId(), siAdjunto.getId(), this.sesion.getUsuario().getId());
		} else {
		    throw new SIAException("Error al eliminar la carpeta físicamente");
		}
	    } else {
		throw new SIAException("Error al eliminar el archivo físicamente");
	    }
	}

	setLista(new ListDataModel(this.sgGastoViajeImpl.findAllSgGastoViajeBySgViajeNative(this.viajeVO.getId(), "id", true, false)));
    }

    public String getDirectoryForComprobanteSgGastoViaje() {
	UtilLog4j.log.info(this, "Directorio: " + this.siParametroImpl.find(1).getUploadDirectory() + Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/GastoViaje/" + this.sgGastoViajeVO.getId() + "/");
	return this.siParametroImpl.find(1).getUploadDirectory() + Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/GastoViaje/" + this.sgGastoViajeVO.getId() + "/";
    }

    public boolean physicallyDeleteFile(String url) {
	UtilLog4j.log.info(this, "Url: " + url);
	boolean deleted = false;

	try {
	    Files.delete(Paths.get(url));
	    deleted = true;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}

	UtilLog4j.log.info(this, "isDelete: " + deleted);

	return deleted;
    }

    public void addSiAdjuntoToSgGastoViaje(String fileName, String contentType, long size) {
	UtilLog4j.log.info(this, "Absolute path complete " + getDirectoryForComprobanteSgGastoViaje() + fileName);
	int idSiAdjunto = siAdjuntoImpl.saveSiAdjunto(fileName, contentType, (Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/GastoViaje/" + this.sgGastoViajeVO.getId() + "/" + fileName), size, this.sesion.getUsuario().getId());
	sgGastoViajeImpl.addSiAdjuntoToSgGastoViaje(this.getSgGastoViajeVO().getId(), idSiAdjunto, this.sesion.getUsuario().getId());
	setLista(new ListDataModel(this.sgGastoViajeImpl.findAllSgGastoViajeBySgViajeNative(this.viajeVO.getId(), "id", true, false)));
    }

    public void deleteComprobanteFromSgGastoViaje() throws SIAException {
	//Se eliminan fisicamente los archivos
	SiAdjunto siAdjunto = this.siAdjuntoImpl.find(this.sgGastoViajeVO.getIdSiAdjunto());

	if (physicallyDeleteFile(this.siParametroImpl.find(1).getUploadDirectory() + siAdjunto.getUrl())) {
	    this.sgGastoViajeImpl.deleteSiAdjuntoFromSgGastoViaje(this.sgGastoViajeVO.getId(), siAdjunto.getId(), this.sesion.getUsuario().getId());
	    setLista(new ListDataModel(this.sgGastoViajeImpl.findAllSgGastoViajeBySgViajeNative(this.viajeVO.getId(), "id", true, false)));
	} else {
	    throw new SIAException("Error al eliminar el archivo físicamente");
	}
    }

    public void saveSgKilometraje(int idSgVehiculo, int idSgTipoEspecifico, int kilometraje) {
	sgKilometrajeImpl.createKilometrajeActual(idSgVehiculo, idSgTipoEspecifico, kilometraje, this.sesion.getUsuario());
    }

    public SgKilometraje getKilometrajeActual(int idSgVehiculo) {
	return sgKilometrajeImpl.findKilometrajeActualVehiculo(idSgVehiculo);
    }
    //     *************** GASTO_VIAJE - FIN    **********************

    //     *************** BITACORA_VIAJE - INICIO    **********************
    //buscar viajero en la lista de viaje
    //lo ocupo para activar la opcion de subir archivo en bitacora..
    public boolean findTravelerOnListOfTravel() {
	List<SgViajero> listv = sgViajeroImpl.getListaViajeroPorViaje(viajeVO.getId());
	if (listv != null) {
	    for (SgViajero v : listv) {
		if (v.getUsuario() != null) {
		    if (sesion.getUsuario().getId().equals(v.getUsuario().getId())) {
			flag = true;
		    }
		}
	    }
	}
	return flag;
    }

    public List<SelectItem> listaOficinaCampo() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	try {
	    List<OficinaVO> lv = oficinaService.traerListaOficina();
	    for (OficinaVO sgO : lv) {
		l.add(new SelectItem(sgO.getId(), sgO.getNombre()));
	    }
	} catch (Exception ex) {
	    Logger.getLogger(ViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
	}

	return l;
    }
    //     *************** BITACORA_VIAJE - FIN    **********************

    /**
     * ***************** Viajes a ciudades *
     */
    public void getSolicitudesViajeCiudades() throws Exception {
	UtilLog4j.log.info(this, "ViajeBeanModel.getSolicitudesViajeCiudades()");
	if (dataModel == null) {
//////////////////////////////////            this.dataModel = new ListDataModel(this.solicitudViajeService.getSolicitudViajeCiudadVO(this.sesion.getUsuario().getId(), 401));
	}
    }

    /**
     * Busca el destino de una Solicitud de Viaje fuera de oficina
     *
     * @return
     */
    public SgViajeCiudad findSolicitudViajeCiudad() {
	try {
	    UtilLog4j.log.info(this, "findSolicitudViajeCiudad");
	    if (this.solicitudViajeVO.getIdSgTipoSolicitudViaje() == 2) {
		ViajeDestinoVo vd = sgViajeCiudadImpl.findDestinoSolicitudViaje(getSolicitudViajeVO().getIdSolicitud());
		if (vd != null) {
		    this.setSgViajeCiudad(this.sgViajeCiudadImpl.find(vd.getId()));
		}
	    }
	    return getSgViajeCiudad() != null ? getSgViajeCiudad() : null;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al buscar el destino de la soliicut " + e.getMessage());
	    return null;
	}
    }

    /**
     * Busca el destino de una Solicitud de Viaje de oficina a lugar
     *
     * @return
     */
    public ViajeLugarVO buscarSolicitudViajeLugar() {
	try {
	    if (solicitudViajeVO.getIdSgTipoSolicitudViaje() == 2) {
		viajeLugarVO = sgViajeLugarImpl.buscarLugarDestinoSolicitudViaje(getSolicitudViajeVO().getIdSolicitud());
	    }
	    return getViajeLugarVO();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al buscar el destino de la soliicut " + e.getMessage());
	    e.printStackTrace();
	    return null;
	}
    }

    //***********************************************************
    public String getUsuarioAprobaraForConsultaSV(int idSgSolicitudViaje) {
	if (idSgSolicitudViaje > 0) {
	    return this.estatusAprobacionService.getUsuarioQueTieneSgSolicitudViaje(idSgSolicitudViaje);
	} else {
	    return null;
	}
    }

    /**
     * @param idSolicitud the idSolicitud to set
     */
    public void setIdSolicitud(int idSolicitud) {
	this.idSolicitud = idSolicitud;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @param soporteProveedor the soporteProveedor to set
     */
    public void setSoporteProveedor(SoporteProveedor soporteProveedor) {
	this.soporteProveedor = soporteProveedor;
    }

    /**
     * @return the viajeLugarVO
     */
    public ViajeLugarVO getViajeLugarVO() {
	return viajeLugarVO;
    }

    /**
     * @param viajeLugarVO the viajeLugarVO to set
     */
    public void setViajeLugarVO(ViajeLugarVO viajeLugarVO) {
	this.viajeLugarVO = viajeLugarVO;
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

    /**
     * *
     */
    public List<SelectItem> regresaUsuarioActivoVO(String cadenaDigitada) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	for (UsuarioVO p : usuarioImpl.usuarioActio(-1)) {
	    if (p.getNombre() != null) {
		String cadenaPersona = p.getNombre().toLowerCase();
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (cadenaPersona.contains(cadenaDigitada)) {
		    SelectItem item = new SelectItem(p, p.getNombre());
		    list.add(item);
		}
	    }
	}
	return list;
    }

    /**
     * @return the numeroVuelo
     */
    public String getNumeroVuelo() {
	return numeroVuelo;
    }

    /**
     * @param numeroVuelo the numeroVuelo to set
     */
    public void setNumeroVuelo(String numeroVuelo) {
	this.numeroVuelo = numeroVuelo;
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
     * @return the telefono
     */
    public String getTelefono() {
	return telefono;
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
	this.telefono = telefono;
    }

    public EstatusAprobacionVO getEstatusActualVO() {
	return this.estatusActualVO;
    }

    /**
     * @return the opcionGeneral
     */
    public String getOpcionGeneral() {
	return opcionGeneral;
    }

    /**
     * @param opcionGeneral the opcionGeneral to set
     */
    public void setOpcionGeneral(String opcionGeneral) {
	this.opcionGeneral = opcionGeneral;
    }

    /**
     * @return the inicioEstancia
     */
    public String getInicioEstancia() {
	return inicioEstancia;
    }

    /**
     * @param inicioEstancia the inicioEstancia to set
     */
    public void setInicioEstancia(String inicioEstancia) {
	this.inicioEstancia = inicioEstancia;
    }

    /**
     * @return the finEstancia
     */
    public String getFinEstancia() {
	return finEstancia;
    }

    /**
     * @param finEstancia the finEstancia to set
     */
    public void setFinEstancia(String finEstancia) {
	this.finEstancia = finEstancia;
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
	return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
	this.idCampo = idCampo;
    }

    /**
     * @return the idInvitado
     */
    public int getIdInvitado() {
	return idInvitado;
    }

    /**
     * @param idInvitado the idInvitado to set
     */
    public void setIdInvitado(int idInvitado) {
	this.idInvitado = idInvitado;
    }

    public List traerViajesXSalirMty() throws SIAException {
	List<ViajeVO> lv = new ArrayList<ViajeVO>();
	try {
	    lv = sgViajeImpl.sgviajesXSalir(Constantes.ID_OFICINA_TORRE_MARTEL);

	} catch (Exception e) {
	    lv = null;
	    System.out.println("error: " + e);
	}
	return lv;
    }

    public List traerViajesXSalirRey() throws SIAException {

	List<ViajeVO> lv = new ArrayList<ViajeVO>();
	try {
	    lv = sgViajeImpl.sgviajesXSalir(Constantes.ID_OFICINA_REY_PRINCIPAL);

	} catch (Exception e) {
	    lv = null;
	    System.out.println("error: " + e);
	}
	return lv;
    }

    public List traerViajesXSalirMon() throws SIAException {

	List<ViajeVO> lv = new ArrayList<ViajeVO>();
	try {
	    lv = sgViajeImpl.sgviajesXSalir(Constantes.ID_OFICINA_MONCLOVA);
	    for (ViajeVO vo : lv) {
		setSgRutaTerrestre(sgRutaTerrestreImpl.find(vo.getIdRuta()));
		if (vo.isVehiculoEmpresa()) {
		    setSgViajeVehiculo(sgViajeVehiculoImpl.getVehicleByTravel(vo.getId()));

		}
	    }

	    //busca el vehiculo asignado en viaje_vehiculo
	} catch (Exception e) {
	    lv = null;
	    System.out.println("error: " + e);
	}
	return lv;
    }

    public List traerViajesXSalirSF() throws SIAException {

	List<ViajeVO> lv = new ArrayList<ViajeVO>();
	try {
	    lv = sgViajeImpl.sgviajesXSalir(Constantes.ID_OFICINA_SAN_FERNANDO);
	} catch (Exception e) {
	    lv = null;
	    System.out.println("error: " + e);
	}
	return lv;
    }

    public List traerViajesXSalirAereo() throws SIAException {

	List<ViajeVO> lv = new ArrayList<ViajeVO>();
	try {
	    lv = sgViajeImpl.viajesBySalirAereos();
	} catch (Exception e) {
	    lv = null;
	    System.out.println("error: " + e);
	}
	return lv;
    }

    public void autoriza(String s, Sesion sesion) throws SIAException {
	System.out.println("------------- " + sesion.getUsuario().getId() + ", " + sesion.getOficinaActual().getId());
	this.sesion = sesion;
	setSgViaje(sgViajeImpl.findCodigo(s));
	salidaViaje();
	//sgViajeImpl.ValidarViajesTerrestres(sesion.getUsuario(), s, sesion.getOficinaActual().getId());
    }

    /**
     * @return the popAddViajero
     */
    public boolean isPopAddViajero() {
	return popAddViajero;
    }

    /**
     * @param popAddViajero the popAddViajero to set
     */
    public void setPopAddViajero(boolean popAddViajero) {
	this.popAddViajero = popAddViajero;
    }

    /**
     * @return the addArchivo
     */
    public boolean isAddArchivo() {
	return addArchivo;
    }

    /**
     * @param addArchivo the addArchivo to set
     */
    public void setAddArchivo(boolean addArchivo) {
	this.addArchivo = addArchivo;
    }

    public void subirDocumentoAutomatico(int viaje) {
	SgViaje sgViajeDoc = sgViajeImpl.find(viaje);
	try {
	    SiAdjunto siAdjunto = sgViajeImpl.generarDocumentoAutomatico(viaje);
	    if (siAdjunto != null) {
		ViajeVO vo = sgViajeImpl.findByCodigo(sgViajeDoc.getCodigo());
		vo.setAdjunto(siAdjunto.getNombre());
		vo.setIdAdjunto(siAdjunto.getId());
		vo.setUuid(siAdjunto.getUuid());
		sgViajeImpl.addFile(sgViajeDoc, usuarioImpl.find("SIA"), siAdjunto);
	    }
	    if (sgViajeDoc.getEstatus().getId() == Constantes.ESTATUS_VIAJE_POR_SALIR) {
		viajeVO.setIdAdjunto(sgViajeDoc.getSiAdjunto().getId());
		viajeVO.setUuid(sgViajeDoc.getSiAdjunto().getUuid());
		viajeVO.setAdjunto(sgViajeDoc.getSiAdjunto().getNombre());
		setSgViaje(sgViajeDoc);
		setOpcionViaje(2000);
		System.out.println("id adjunto " + viajeVO.getUuid() + "=" + sgViajeDoc.getSiAdjunto().getUuid());
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}

    }

    public void limpiarViajes() {
	try {
	    sgViajeImpl.limpiarViajes(sesion.getUsuario());
	    sgViajeroImpl.limpiarViajerosNoAtendidos(Constantes.ESTATUS_PARA_HACER_VIAJE, sesion.getOficinaActual().getId(), sesion.getUsuario().getId());
	} catch (Exception e) {
	    System.out.println("Problema al limpiar viajes " + e);
	}

    }

    public boolean isJustificaViajes() {
        return this.sesion.isJustificaViajes();
    }

    /**
     * @return the viajeDirecto
     */
    public boolean isViajeDirecto() {
	return viajeDirecto;
    }

    /**
     * @param viajeDirecto the viajeDirecto to set
     */
    public void setViajeDirecto(boolean viajeDirecto) {
	this.viajeDirecto = viajeDirecto;
    }

    public boolean getTieneRegreso(int idViaje) {
	return sgViajeImpl.tieneRegreso(idViaje);
    }
}
