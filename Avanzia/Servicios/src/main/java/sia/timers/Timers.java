package sia.timers;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;
import sia.constantes.Constantes;
import sia.excepciones.EmailNotFoundException;
import sia.excepciones.SIAException;
import sia.modelo.Compania;
import sia.modelo.SgAvisoPago;
import sia.modelo.SgDetalleRutaTerrestre;
import sia.modelo.SgEstatusAprobacion;
import sia.modelo.SgHistorialConvenioOficina;
import sia.modelo.SgHistorialConvenioStaff;
import sia.modelo.SgHuespedStaff;
import sia.modelo.SgKilometraje;
import sia.modelo.SgOficina;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.SgPagoServicioVehiculo;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgStaff;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgVehiculoMantenimiento;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.cursoManejo.vo.CursoManejoVo;
import sia.modelo.fecha.asueto.impl.SiDiasAsuetoImpl;
import sia.modelo.gr.vo.GrArchivoVO;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.modelo.orden.vo.OrdenCorreoVo;
import sia.modelo.requisicion.vo.RequisicionReporteVO;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.AvisoPagoVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.sgl.vo.SgHuespedVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoVo;
import sia.notificaciones.orden.impl.NotificacionOrdenImpl;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.gr.impl.GrArchivoImpl;
import sia.servicios.oficio.impl.OfOficioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sgl.impl.SgAvisoPagoImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgHistorialConvenioOficinaImpl;
import sia.servicios.sgl.impl.SgHistorialConvenioStaffImpl;
import sia.servicios.sgl.impl.SgHuespedHotelImpl;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgPagoServicioVehiculoImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgVehiculoMantenimientoImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.vehiculo.impl.SgCursoManejoImpl;
import sia.servicios.sgl.vehiculo.impl.SgLicenciaImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 * TimerService API -
 * http://docs.oracle.com/javaee/6/api/javax/ejb/TimerService.html
 * https://blogs.oracle.com/arungupta/entry/totd_146_understanding_the_ejb
 * http://docs.oracle.com/javaee/1.4/tutorial/doc/Session5.html
 *
 * @author b75ckd35th
 */
public class Timers implements TimedObject {

    //Servicios
    @Resource
    private TimerService timerService;
    //
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgHistorialConvenioOficinaImpl sgHistorialConvenioOficinaRemote;
    @Inject
    private SgHistorialConvenioStaffImpl sgHistorialConvenioStaffRemote;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;
    @Inject
    private NotificacionViajeImpl notificacionViajeRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaRemote;
    @Inject
    private SgLicenciaImpl sgLicenciaRemote;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgStaffImpl sgStaffRemote;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelRemote;
    @Inject
    private SgAvisoPagoImpl sgAvisoPagoRemote;
    @Inject
    private SgHuespedStaffImpl sgHuespedStaffRemote;
    @Inject
    private SgPagoServicioVehiculoImpl sgPagoServicioVehiculoRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SgVehiculoMantenimientoImpl sgVehiculoMantenimientoRemote;
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreRemote;
    @Inject
    private SiUsuarioTipoImpl siUsuarioTipoRemote;
    @Inject
    private OrdenImpl ordenRemote;
    @Inject
    private NotificacionOrdenImpl notificacionOrdenRemote;
    @Inject
    private ApCampoImpl apCampoRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private CompaniaImpl companiaRemote;
    // Servicios remotos
    @Inject
    private OfOficioImpl oficioServicioRemoto;
    @Inject
    private SiDiasAsuetoImpl diasAsuetoRemote;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenRemote;
    @Inject
    private GrArchivoImpl grArchivoRemote;
    @Inject
    private ConvenioImpl convenioRemote;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionRemote;
    @Inject
    private RequisicionImpl requisicionRemote;
    @Inject
    private NotificacionRequisicionImpl notificacionRequisicionRemote;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private EstatusImpl estatusRemote;
    @Inject
    private SgCursoManejoImpl cursoManejoRemote;

    //Variables
    private final TreeSet<AvisoPagoVO> listaAvisosEnviados = new TreeSet<>();
    //S
    private String telefonoSeguridad = Constantes.VACIO;

    private final static String SIA_EMAIL = "siaihsa@ihsa.mx";
    private final static String SIA_USER = "SIA";

    @PostConstruct
    public void initTimers() {
        log("Timers.initTimers()");

        if (timerService.getTimers() != null) {
            for (Timer timer : timerService.getTimers()) {
                log("Terminado Timer: " + timer.getInfo());
                timer.cancel();
            }
        }

        for (Schedules schedule : Schedules.values()) {
            timerService.createCalendarTimer(
                    schedule.getScheduleExpression(),
                    schedule.getTimerConfig()
            );

            log("Creado el  Timer: " + schedule.getTimerConfig().getInfo());
        }
    }

    
    public void ejbTimeout(Timer timer) {
        log("Timers.ejbTimeout()");
        log("Ejecutando Timer: " + new Date());

        if (timer.getInfo().equals(Constantes.TIMER_AVISO_VENCIMIENTO_CONVENIOS)) {
            envioEmailsVencimientoConvenios();
        }
       
        //## OK ##
        /*if (timer.getInfo().equals(Constantes.TIMER_AVISO_VENCIMIENTO_LICENCIAS)) {
            log("Comenzando con licenciencias");
            envioCorreoVencimientoLicencia();
        }
        if (timer.getInfo().equals(Constantes.TIMER_AVISO_VENCIMIENTO_LICENCIAS_SEMANAL)) {
            log("Comenzando con licenciencias");
          envioCorreoVencimientoLicenciaSemanal();
        }
        if (timer.getInfo().equals(Constantes.TIMER_AVISO_VENCIMIENTO_CURSO_MANEJO)) {
            log("TIMER_AVISO_VENCIMIENTO_CURSO_MANEJO");
            enviarCorreoVencimientoCursoManejoSemanal();
        }
        
        if(timer.getInfo().equals(Constantes.TIMER_QUITA_VIGENCIA_CURSO_MANEJO)){
            log("TIMER_QUITA_VIGENCIA_CURSO_MANEJO");
            quitarVigenciaCursoManejo();
        }
        if(timer.getInfo().equals(Constantes.TIMER_QUITA_VIGENCIA_LICENCIA)){
            log("TIMER_QUITA_VIGENCIA_LICENCIA");
            quitarVigenciaCursoManejo();
        }*/
        
        //## OK ##
        if (timer.getInfo().equals(Constantes.TIMER_AVISO_PAGOS)) {
            log("Comenzando con avisos de pagos");
            envioNotificacionPagosStaffOficina();
        }

        if (timer.getInfo().equals(Constantes.TIMER_AVISO_SALIDA_ESTANCIA_STAFF)) {
            log("Comenzando con avisos de salida de staff");
            envioEmailsVencimientoHuespedStaff();
        }
        //## OK ##
        if (timer.getInfo().equals(Constantes.TIMER_VENCIMIENTO_PAGO_SERVICIO)) {
            log("Comenzando con pagos de servicio de vehiculo");
            envioCorreoAvisoVencimientoPagoVehiculo();
        }
        //## OK ##
        if (timer.getInfo().equals(Constantes.TIMER_AVISO_MANTENIMIENTO)) {
            log("Comenzando con Aviso para mantenimientos");
            envioCorreoAvisoMantenimientoPorOficina();
        }
        // ## OK ##
        if (timer.getInfo().equals(Constantes.TIMER_SALIDA_AUTOMATICA)) {
            log("salida automatica");
            try {
                prolongacionAutomaticaEstanciaEnStaffYHotel();
                salidaAutomaticaHuespedStaffYHotel();
            } catch (EmailNotFoundException ex) {
                Logger.getLogger(Timers.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Timers.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // ## OK ##
        if (timer.getInfo().equals(Constantes.TIMER_VIAJES_SEMAFORO)) {
            log("validar semaforo de viajes");
            validarSemaforoViajes();
        }

        if (timer.getInfo().equals(Constantes.TIMER_REPORTE_DIARIO_VIAJE)) {
            log("Reporte viajes");
            publicaReporteViajesDiario();
        }

        if (timer.getInfo().equals(Constantes.TIMER_NOTIFICA_VIAJE_AEREO)) {
            log("Notifica viaje aereo");
            notificaSalidaViajeAereo();
        }
        /**
         * Notifica de monto acumulado en las OC/S
         */
        if (timer.getInfo().equals(Constantes.TIMER_REPORTE_MONTO_ACUMULADO)) {
            log("Notifica OC/S monto acumulado  - - - - ");
            notificaOrdenSuperaMonto();
        }

//	if (timer.getInfo().equals(Constantes.TIMER_ESTADO_SEMAFORO)) {
//	    log("Aviso de Estado semaforo");
//	    verificaEstadoSemaforo();
//	}
        /**
         * Notifica OC/S por autorizar para MPG
         */
        if (timer.getInfo().equals(Constantes.TIMER_REPORTE_AUTORIZA_ORDENES)) {
            log("Notifica OC/S Por autorizar - - - - MPG-IHSA");
            notificaOrdenPorAutorizar();
        }

        // Enviar correo de notificación de nuevos oficios registrados en las últimas 24 horas
        if (Constantes.TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_ALTA_OFICIOS.equals(timer.getInfo())) {

            try {
                notificarAltaOficios();
            } catch (Exception ex) {
                log("Se presentó un error en proceso automático de envío de correo de Informe de Avance.", ex);
            }
        }
        // Enviar correo de notificación de  oficios no promovidos  en la última semana
        //jevazquez 23/feb/2015

        if (Constantes.TIMER_CONFIG_CONTROL_OFICIOS_NOTIFICACION_NO_PROMOVIDOS_OFICIOS.equals(timer.getInfo())) {

            try {
                //jevazquez 15/abr/2015 aprobado
                notificarNoPromovidoOficios();
            } catch (Exception ex) {
                log("Se presentó un error en proceso automático de envío de correo de Informe de Avance.", ex);
            }
        }
        /**
         * Reporte de compradores
         */
        if (timer.getInfo().equals(Constantes.TIMER_CONFIG_REPORTE_COMPRADORES)) {
            reporteCompradores();
        }

        /**
         * Vencimiento de contrato
         */
        if (Constantes.CONVENIOS_POR_VENCER.equals(timer.getInfo())) {

            try {
                notificarConvenioPorVencer();
            } catch (Exception ex) {
                log("Se presentó un error en proceso automático de envío de correo de Informe de Avance." + ex);
            }
        }
        /**
         * Vencimiento de contrato
         */
        if (Constantes.CONVENIOS_VENCIDOS.equals(timer.getInfo())) {

            try {
                notificarConvenioConveniosVencidos();
            } catch (Exception ex) {
                log("Se presentó un error en proceso automático de envío de correo de Informe de Avance." + ex);
            }
        }

        if (timer.getInfo().equals(Constantes.TIMER_LIMPIAR_VIAJESYSOLICITUDES)) {
            log("limpiar solicitudes y viajes no realizados en tiempo");
            limpiarSolicitudYViajes();
        }

        //TODO : revisar si realmente se tiene que eliminar o modificar el momento y a quien se envía
//        if(timer.getInfo().equals(Constantes.TIMER_CONFIG_REPORTE_SV_POR_APROBAR)){
//            log("manda correo una hora antes de que las solicitudes tengan que pasar por el departamento de GR");
//            try{
//                cambioDeAprobacion(Constantes.FALSE);
//            } catch(Exception e){
//                UtilLog4j.log.fatal(this, e);
//            }
//             
//            
//        } 
        if (timer.getInfo().equals(Constantes.TIMER_CAMBIO_DE_APROBACION)) {
            log("crea un registro con un estatus de que el gerente no aprobo la solicitud y despues pasa la solicitud con GR");
            try {
                cambioDeAprobacion(Constantes.TRUE);
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e);
            }
        }

        /*if(timer.getInfo().equals(Constantes.TIMER_REPORTE_GERENTE_SG)){
            log("manda correo una con los viajes programados para el dia siguiente y los no aprobados.");
            try{
             reporteGerenteSG();
            } catch(Exception e){
                UtilLog4j.log.fatal(this, e);
            }
        }*/
    }

    /*
     * Notifica
     */
    public void notificarMantenimientoVehicularPorPeriodo() {
        final List<SgOficina> listaOficina
                = sgOficinaRemote.traerOficina(null, Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);

        if (!listaOficina.isEmpty()) {
            for (SgOficina of : listaOficina) {
                List<VehiculoVO> lVehiculoVo
                        = sgVehiculoMantenimientoRemote.getVehiculosConProximoMtto(of.getId());

                if (!lVehiculoVo.isEmpty()) {
                    //enviar correo
                    notificacionServiciosGeneralesRemote.enviarAvisoNotificacionProxMantenimientoPorPeriodicidad(of.getId(), lVehiculoVo);
                }
            }
        }
    }

    private void envioCorreoAvisoMantenimientoPorOficina() {
        List<SgVehiculoMantenimiento> listaMmtoPorFecha = null;
        List<SgKilometraje> listaKilometrajesActuales;

        try {
            Date hoy = new Date();
            final List<OficinaVO> lOficina = sgOficinaRemote.traerListaOficina();

            if (!lOficina.isEmpty()) {
                for (final OficinaVO of : lOficina) {
                    List<VehiculoVO> listaMmto
                            = sgVehiculoMantenimientoRemote.traerMantenimientosProximoKilometrajePorRealizar(of.getId(), 500);  //500 km

                    if (listaMmto == null || listaMmto.isEmpty()) {
                        log("No existieron mantenimientos por kilometraje a vencer");
                    } else {
                        notificacionServiciosGeneralesRemote.enviarAvisoNotificacionProxMantenimientoPorKm(of.getId(), listaMmto, of.getNombre());
                    }

//                    listaMmto = null;
//                    listaKilometrajesActuales = null;
                    log("....Traer mantenimientos por fecha....");

                    listaMmto = sgVehiculoMantenimientoRemote.traerMantenimientosProximaFechaPorRealizar(of.getId(), siManejoFechaLocal.fechaSumarDias(hoy, Constantes.QUINCE_DIAS_ANTICIPADOS));

                    if (listaMmto == null || listaMmto.isEmpty()) {
                        log("No existieron mantenimientos por fecha a vencer");
                    } else {
                        log("Nueva lista de mantenimiento por fecha a realizar ...");
//                        listaKilometrajesActuales = sgKilometrajeService.traerKilometrajeProximaFechaPorRealizar(of, manejoFechaService.fechaSumarDias(hoy, Constantes.QUINCE_DIAS_ANTICIPADOS));
                        notificacionServiciosGeneralesRemote.enviarAvisoNotificacionProxMantenimientoPorFecha(
                                of.getId(),
                                listaMmto,
                                siManejoFechaLocal.fechaSumarDias(
                                        hoy,
                                        Constantes.QUINCE_DIAS_ANTICIPADOS
                                ),
                                of.getNombre()
                        );
                    }
                }
            }
        } catch (Exception e) {
            log("Excepcion : " + e.getMessage());
        }
    }

//    public void salidaAutomaticaViajes() {
//	log("Metodo para sacar los viajes");
//	final StringBuilder texto = new StringBuilder();
//
//	try {
//	    final List<ViajeVO> list = sgViajeRemote.getAllRoadTripByExit(501);
//
//	    if (list != null && !list.isEmpty()) {
//		for (final ViajeVO viajeVO : list) {
//		    SgViaje sgViaje = sgViajeRemote.find(viajeVO.getId());
//		    if (sgViaje.getSiAdjunto() != null) {
//			UtilLog4j.log.info(this, "Sacando viajes");
//			texto.append("Sacando el viaje ").append(sgViaje.getCodigo());
//			sgViajeRemote.exitTrip(usuarioRemote.find("SIA"), sgViaje, 510, null, true);
//
//		    }
//		}
//	    }
//	} catch (SIAException ex) {
//	    notificacionViajeRemote.sendMailExceptionError(SIA_EMAIL, "EXCEPCION " + texto, "Excepcion  : " + ex.getMessage());
//	    Logger.getLogger(Timers.class.getName()).log(Level.SEVERE, null, ex);
//	}
//
//    }
    public void validarSemaforoViajes() {
        log("Metodo para avisar a CENTOPS que actualice el semaforo");
        final StringBuilder texto = new StringBuilder();
        try {
            final List<GrArchivoVO> list = grArchivoRemote.getAlertas();
            if (list != null && !list.isEmpty()) {
                StringBuilder msg = new StringBuilder();
                msg.append("Actualmente se encuentra un semáforo en <b>ROJO</b> o <b>NEGRO</b>. Favor, de actualizar la información de este semáforo antes de las <b>5:30 am</b>.");
                grArchivoRemote.enviarCentops(msg.toString(), null, null, null);
            }
        } catch (Exception ex) {
            notificacionViajeRemote.sendMailExceptionError(SIA_EMAIL, "EXCEPCION " + texto, "Excepcion  : " + ex.getMessage());
            Logger.getLogger(Timers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limpiarSolicitudYViajes() {
        log("Metodo para limpiar las solicitudes de viaje que no se atendieron en tiempo");
        final StringBuilder texto = new StringBuilder();
        try {
            sgViajeRemote.limpiarViajes(null);
            sgViajeroRemote.limpiarViajerosNoAtendidos(Constantes.ESTATUS_PARA_HACER_VIAJE, 0, null);
        } catch (Exception ex) {
            notificacionViajeRemote.sendMailExceptionError(SIA_EMAIL, "EXCEPCION " + texto, "Excepcion  : " + ex.getMessage());
            Logger.getLogger(Timers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void envioEmailsVencimientoConvenios() {
        log("Timers.envioEmailsVencimientoConvenios()");
        List<SgHistorialConvenioOficina> historialConveniosOficinaVigentes = sgHistorialConvenioOficinaRemote.getContratoByVigenteList();
        List<SgHistorialConvenioStaff> historialConveniosStaffVigentes = sgHistorialConvenioStaffRemote.getContratoByVigenteList(Constantes.BOOLEAN_TRUE);
        log("Se encontraron: " + historialConveniosOficinaVigentes.size() + " historialConveniosOficinaVigentes");
        log("Se encontraron: " + historialConveniosStaffVigentes.size() + " historialConveniosStaffVigentes");

        for (SgHistorialConvenioOficina hco : historialConveniosOficinaVigentes) {
            int[] diasParaVencimientoConvenioOficina
                    = siManejoFechaLocal.convenioExpiresInLessThan30Days(hco.getConvenio());

            if (diasParaVencimientoConvenioOficina[0] == 1) {
                log("El Convenio de la Oficina: " + hco.getSgOficina().getId() + " expira en 30 días");
                //Trayendo todos los Analistas que están asignados a esta oficina
                List<SgOficinaAnalista> oficinasAnalistas
                        = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(hco.getSgOficina().getId(), Constantes.NO_ELIMINADO);

                for (SgOficinaAnalista oa : oficinasAnalistas) {
                    notificacionServiciosGeneralesRemote.enviarCorreoAvisoVencimientoContratoOficina(
                            hco.getSgOficina(),
                            hco.getConvenio(),
                            oa.getAnalista(),
                            diasParaVencimientoConvenioOficina[1]
                    );
                }
            }
        }

        for (SgHistorialConvenioStaff hcs : historialConveniosStaffVigentes) {
            int[] diasParaVencimientoConvenioStaff = siManejoFechaLocal.convenioExpiresInLessThan30Days(hcs.getConvenio());
            if (diasParaVencimientoConvenioStaff[0] == 1) {
                log("El Convenio del Staff: " + hcs.getSgStaff().getId() + " expira en 30 días");
                //Trayendo todos los Analistas que están asignados a la Oficina del Staff
                List<SgOficinaAnalista> oficinasAnalistas = sgOficinaAnalistaRemote.getAnalistasByOficinaAndStatus(hcs.getSgStaff().getSgOficina().getId(), Constantes.NO_ELIMINADO);
                for (SgOficinaAnalista oa : oficinasAnalistas) {
                    notificacionServiciosGeneralesRemote.enviarCorreoAvisoVencimientoContratoStaff(hcs.getSgStaff(), hcs.getConvenio(), oa.getAnalista(), diasParaVencimientoConvenioStaff[1]);
                }
            }
        }
    }

    /*
     * Envia mails de aviso de vencimiento de licencia comparando 15 dias antes
     * de la fecha establecida en el registro de la misma.
     *
     * La notificación se realiza por oficina.
     *
     */
    private void envioCorreoVencimientoLicencia() {

        final List<OficinaVO> listaOficina = sgOficinaRemote.traerListaOficina();
        final Date hoy = new Date();

        if (!listaOficina.isEmpty()) {
            for (final OficinaVO of : listaOficina) {
                log("Recorriendo la oficina" + of.getNombre());

                List<LicenciaVo> li
                        = sgLicenciaRemote.traerLicenciasPorOficina(
                                of.getId(),
                                siManejoFechaLocal.fechaSumarDias(
                                        hoy,
                                        Constantes.QUINCE_DIAS_ANTICIPADOS
                                )
                        );

                if (li == null || li.isEmpty()) {
                    log("No existieron licencias por vencer");
                } else {
                    log("lista de avisos de licencias con " + li.size() + " registros ");
                    notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoLicenciasPorOficina(
                            of.getId(),
                            li,
                            siManejoFechaLocal.fechaSumarDias(hoy, Constantes.QUINCE_DIAS_ANTICIPADOS),
                            of.getNombre()
                    );

                }
            }
        }
    }

    public void envioNotificacionPagosStaffOficina() {
        log("******Timers.envioNotificacionPagoStaffOficina()");

        //traer oficinas..
        final List<SgOficina> listaOficina
                = sgOficinaRemote.traerOficina(null, Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);

        if (!listaOficina.isEmpty()) {
            for (final SgOficina of : listaOficina) {
                log("Recorriendo la oficina" + of.getNombre());

                //para oficina
                List<SgAvisoPago> listaAvisosPagos
                        = sgAvisoPagoRemote.traerAvisosPagosPorOficinaConFechaHoy(of);

                if (!listaAvisosPagos.isEmpty()) {
                    log("lista de avisos ");
                    notificacionServiciosGeneralesRemote.enviarCorreoAvisoNotificacionPagoOficina(of, listaAvisosPagos);

                    log("Se envio el correo para oficinas");
                    for (final SgAvisoPago pago : listaAvisosPagos) {
                        //pasarlos al treeset
                        log("Agregar aviso al treeset" + pago.getSgTipoEspecifico().getNombre());
                        listaAvisosEnviados.add(new AvisoPagoVO(pago));
                    }
                }
                //oficina
                //staff

                boolean envioSt = false;

                List<SgStaff> listaStaff
                        = sgStaffRemote.getAllStaffByStatusAndOficina(Constantes.BOOLEAN_FALSE, of.getId());

                if (!listaStaff.isEmpty()) {
                    for (final SgStaff st : listaStaff) {

                        listaAvisosPagos = sgAvisoPagoRemote.traerAvisosPagosPorStaffConFechaHoy(st);
                        log("Recorriendo Staff***** " + st.getNombre());

                        if (!listaAvisosPagos.isEmpty()) {
                            envioSt = true;
                            for (final SgAvisoPago pago : listaAvisosPagos) {
                                log("Agregar aviso al treset en staff" + pago.getSgTipoEspecifico().getNombre());
                                listaAvisosEnviados.add(new AvisoPagoVO(pago));
                            }
                        }
                    }

                    if (envioSt) {
                        notificacionServiciosGeneralesRemote.enviarCorreoAvisoNotificacionPagoStaff(of);
                    }
                }

            }
        }

        for (AvisoPagoVO pagoVO : listaAvisosEnviados) {

            SgAvisoPago pago = pagoVO.getPago();
            Date fechaAvisoNueva = siManejoFechaLocal.fechaSumarMes(pago.getFechaProximoAviso(), pago.getSgPeriodicidad().getMes());

            log("la fecha nueva de proximo aviso es " + pago.getFechaProximoAviso());
            log("Componer fecha de pago");

            Date fechaPago = siManejoFechaLocal.componerFechaApartirDeDia(fechaAvisoNueva, pago.getDiaEstimadoPago());

            log("Fecha pago" + fechaPago);

            while (fechaAvisoNueva.compareTo(fechaPago) > 0) {
                fechaAvisoNueva = siManejoFechaLocal.fechaSumarMes(pago.getFechaProximoAviso(), pago.getSgPeriodicidad().getMes());
            }

            log("Poner nueva fecha de aviso");

            sgAvisoPagoRemote.editAvisoPagoNuevaFechaAviso(pago, fechaAvisoNueva);

            log("Se editó correcatamente la fecha");
        }

    }

    public void envioEmailsVencimientoHuespedStaff() {
        log("Timers.envioEmailsVencimientoHuespedStaff()");
        Date fechaMasQuinceDias
                = siManejoFechaLocal.fechaSumarDias(new Date(), Constantes.QUINCE_DIAS_ANTICIPADOS);

        List<SgOficina> listaOficina
                = sgOficinaRemote.traerOficina(null, Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);

        List<SgHuespedStaff> listaHuespedesConVencimientoEn15Dias;
        boolean envioSt;

        if (!listaOficina.isEmpty()) {
            for (SgOficina of : listaOficina) {
                envioSt = false;

                List<SgStaff> listaStaff
                        = sgStaffRemote.getAllStaffByStatusAndOficina(Constantes.BOOLEAN_FALSE, of.getId());

                if (!listaStaff.isEmpty()) {
                    for (SgStaff st : listaStaff) {
                        listaHuespedesConVencimientoEn15Dias
                                = sgHuespedStaffRemote.findAllVencimientoEstanciaPorStaff(
                                        fechaMasQuinceDias,
                                        st,
                                        Constantes.QUINCE_DIAS_ANTICIPADOS
                                );

                        if (listaHuespedesConVencimientoEn15Dias != null
                                && !listaHuespedesConVencimientoEn15Dias.isEmpty()) {
                            log("Existen huéspedes con vencimiento");
                            envioSt = true;
                            break;
                        }
                    }
                }
                if (envioSt) {
                    notificacionServiciosGeneralesRemote.enviaCorreoAvisoSalidaHuespedStaffPorOficina(of, fechaMasQuinceDias);
                }
            }
        }
    }

    /**
     * Tenencias (15 dias antes de la fecha de periodo) Seguros (60 dias antes
     * de la fecha de fin de periodo)
     */
    private void envioCorreoAvisoVencimientoPagoVehiculo() {

        final List<SgOficina> lOficina
                = sgOficinaRemote.traerOficina(null, Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);

        Date hoy = new Date();

        if (lOficina.isEmpty()) {
            log("NO existen oficina con visto bueno..");
        } else {
            SgTipoEspecifico tipoTenencia = sgTipoEspecificoRemote.find(12);
            SgTipoEspecifico tipoSeguro = sgTipoEspecificoRemote.find(14);

            for (SgOficina oficina : lOficina) {
                log("Recorriendo la oficina" + oficina.getNombre());
                //<--El id del tipo de pago (12 es para tenencia)

                //12 es el tipo especifico de Tenencia
                List<SgPagoServicioVehiculo> listaPagos
                        = sgPagoServicioVehiculoRemote.traerPagoVehiculoPorFechaVencimientoYOficina(
                                oficina,
                                siManejoFechaLocal.fechaSumarDias(hoy, Constantes.QUINCE_DIAS_ANTICIPADOS),
                                tipoTenencia
                        );

                if (listaPagos == null || listaPagos.isEmpty()) {
                    log("No existieron pagos de tenencia por vencer");
                } else {
                    log("lista de avisos de pagos para vehiculo ");
                    notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoPagosVehiculoPorOficina(
                            oficina,
                            listaPagos,
                            siManejoFechaLocal.fechaSumarDias(hoy, Constantes.QUINCE_DIAS_ANTICIPADOS),
                            tipoTenencia
                    );
                }

                //14 es el tipo especifico de Seguro vehicular
                listaPagos
                        = sgPagoServicioVehiculoRemote.traerPagoVehiculoPorFechaVencimientoYOficina(
                                oficina,
                                siManejoFechaLocal.fechaSumarDias(hoy, Constantes.SESENTA_DIAS_ANTICIPADOS),
                                tipoSeguro
                        );

                if (listaPagos == null || listaPagos.isEmpty()) {
                    log("No existieron pagos de seguro por vencer");
                } else {
                    log("lista de avisos de pagos de seguros para vehiculo ");
                    notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoPagosVehiculoPorOficina(
                            oficina,
                            listaPagos,
                            siManejoFechaLocal.fechaSumarDias(
                                    hoy,
                                    Constantes.SESENTA_DIAS_ANTICIPADOS
                            ),
                            tipoSeguro
                    );

                }
            }
        }
    }

    public void salidaAutomaticaHuespedStaffYHotel() throws Exception {
        log("Timers.salidaAutomaticaHuespedStaff()");
        for (final SgHuespedVO hs : sgHuespedStaffRemote.findAllVencimientoEstanciaPorStaff(Constantes.FMT_yyyyMMdd.format(new Date()))) {
            try {
                sgHuespedStaffRemote.exitHuespedStaff(hs, hs.getFecha_ingreso(), new Date(), Constantes.USUARIO_SIA);
            } catch (Exception ex) {
                throw ex;
            }
        }

        log("Salida normal de huespedes en Hotel de la oficina ");
        for (final int sgHuespedHotel : sgHuespedHotelRemote.traerHuespedPorHotelPorFechaSalidaHoy()) {
            sgHuespedHotelRemote.salidaHuespedHotelForTimer(sgHuespedHotel, Constantes.USUARIO_SIA);
        }
    }

    public void prolongacionAutomaticaEstanciaEnStaffYHotel() throws EmailNotFoundException {
        log("Comenzando a prolongar estancias por timer");
        List<SgOficina> sgOficinaList = null;
        final List<SemaforoVo> listaRutasEnNegro
                = sgEstadoSemaforoRemote.traerEstadoSemaforoPorColor(Constantes.ID_COLOR_SEMAFORO_NEGRO, Constantes.BOOLEAN_TRUE);

        if (listaRutasEnNegro != null && !listaRutasEnNegro.isEmpty()) {
            for (final SemaforoVo semaforoVo : listaRutasEnNegro) {
                log(" {{{recorriendo el semaforo  }}}}} " + semaforoVo.getColor());

                if (semaforoVo.getRutaTipoEspecifico() == 21) { //oficina
                    log("Es una ruta a oficina -- se prolongaran las estancias de la oficina destino");
                    //si el semaforo es de ruta terrestre a oficina enviar la oficina del destino
                    SgDetalleRutaTerrestre destinoSgRutaTerrestre = sgDetalleRutaTerrestreRemote.findSgDetalleRutaTerrestreDestinoBySgRutaTerrestre(semaforoVo.getIdRuta());
                    log("destinoSgRutaTerrestre " + destinoSgRutaTerrestre.getSgOficina().getId());
                    sgEstadoSemaforoRemote.prolongarEstanciasPorOficinayRuta(destinoSgRutaTerrestre.getSgOficina().getId(), semaforoVo.getIdRuta(), Constantes.USUARIO_SIA);
                } else {//ciudad
                    log("Es una ruta a ciudad-- se prolongaran las estancias de la oficina origen");
                    //si el semaforo es de ruta a ciudad enviar la oficina origen
                    sgEstadoSemaforoRemote.prolongarEstanciasPorOficinayRuta(semaforoVo.getIdOficinaOrigen(), semaforoVo.getIdRuta(), Constantes.USUARIO_SIA);
                }
            }
        }

    }

    private void log(String mensaje, Throwable e) {
        UtilLog4j.log.info(this, mensaje, e);
    }

    private void log(String mensaje) {
        log(mensaje, null);
    }

    public void publicaReporteViajesDiario() {
        final List<ViajeVO> lv
                = sgViajeRemote.traerViajesTerrestrePorEstatus(Constantes.ESTATUS_VIAJE_POR_SALIR, true);

        if (!lv.isEmpty()) {
            final ViajeVO viajeVO = lv.get(0);
            String cc
                    = publicaViaje(Constantes.NOTIFICA_DIRECCION_GENERAL, Constantes.ID_OFICINA_TORRE_MARTEL);

            notificacionViajeRemote.sendMailNotificarDireccionGral(
                    lv,
                    cc,
                    Constantes.FMT_ddMMyyy.format(viajeVO.getFechaProgramada())
            );
        }
    }

    //Tipo  5 : Copiados seguridad
    // Tipo 7:  usuario copiados de direccion general
    private String publicaViaje(final int idTipo, final int idOficina) {
        log("publicaViaje");
        final StringBuilder correo = new StringBuilder();

        List<UsuarioTipoVo> luc = siUsuarioTipoRemote.getListUser(idTipo, idOficina);

        for (UsuarioTipoVo usuarioCopiadoVo : luc) {
//            log("Id usuario copiado "+(usuarioCopiadoVo.getUsuario() == null ? "Es null el usuario":usuarioCopiadoVo.getNombre()));
            log("correo usuario copiado " + (usuarioCopiadoVo.getCorreo() == null ? "Es null " : usuarioCopiadoVo.getCorreo()));
            if (correo.length() == 0) {
                correo.append(usuarioCopiadoVo.getCorreo());
                telefonoSeguridad = usuarioCopiadoVo.getTelefono();

            } else {
                correo.append(',').append(usuarioCopiadoVo.getCorreo());
                telefonoSeguridad += ", " + usuarioCopiadoVo.getTelefono();
            }
        }
        log("Correo viaje publicado. " + correo);
//        log("Correo viaje publicado. " + nombre);
        return correo.toString();
    }

    //Tipo  5 : Copiados seguridad
    // Tipo 7:  usuario copiados de direccion general
    private String correoPara(final List<ViajeroVO> lv) {
        log("publicaViaje");
        StringBuilder correo = new StringBuilder();

        for (final ViajeroVO viajeroVO : lv) {
            if (viajeroVO.getIdInvitado() == 0 && !viajeroVO.getCorreo().isEmpty()) {

                if (correo.length() > 0) {
                    correo.append(',');
                }

                correo.append(viajeroVO.getCorreo());
            }

        }
        log("Correo viaje publicado. " + correo);
//        log("Correo viaje publicado. " + nombre);
        return correo.toString();
    }

    /*
     * MLUIS 17/12/2013
     */
 /*
     *
     */

    public void notificaSalidaViajeAereo() {

        List<Integer> li = new ArrayList<Integer>();
        li.add(Constantes.SGL_SEGURIDAD);
        li.add(Constantes.ROL_CENTRO_OPERACION);
        List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_NEJO);
        String cc = traerCorreo(lu);

        List<ViajeVO> lv = sgViajeRemote.traerViajesAereoPorEstatus(Constantes.ESTATUS_VIAJE_POR_SALIR, true);
        if (!lv.isEmpty()) {
            //String cc = publicaViaje(Constantes.NOTIFICA_GESTION_RIESGOS, Constantes.ID_OFICINA_TORRE_MARTEL);
            for (ViajeVO viajeVO : lv) {
                String correoPara = correoPara(viajeVO.getListaViajeros());
                notificacionViajeRemote.sendMailNotificaViajero(correoPara, viajeVO, Constantes.FMT_ddMMyyy.format(viajeVO.getFechaProgramada()), cc, telefonoSeguridad);
            }
            ViajeVO viajeVO = lv.get(0);
            notificacionViajeRemote.sendMailNotificaGestionRiesgo(cc, lv, Constantes.FMT_ddMMyyy.format(viajeVO.getFechaProgramada()));
        }
    }

    /**
     *
     */
    private List<OrdenVO> castOC(List<Object[]> lo) {

        final List<OrdenVO> lor = new ArrayList<OrdenVO>();
        for (final Object[] objects : lo) {
            OrdenVO orden = new OrdenVO();
            orden.setId((Integer) objects[0]);
            orden.setConsecutivo(String.valueOf(objects[1]));
            orden.setReferencia(String.valueOf(objects[2]));
            orden.setRequisicion((String) objects[3]);
            orden.setFecha((Date) objects[4]);
            orden.setTotal((Double) objects[8]);
            orden.setMoneda((String) objects[9]);
            orden.setProveedor((String) objects[6]);
            orden.setEstatus((String) objects[5]);
            orden.getContratoVO().setNumero(String.valueOf(objects[7]));
            orden.setSuperaMonto((Boolean) objects[10]);
            lor.add(orden);
        }

        return lor;
    }

    private List<OrdenCorreoVo> ordenesPorAutorizar(final String idUsuario, final int idCampo, final String campo) {
        final TreeSet<String> lnomp = new TreeSet<String>();
        final List<OrdenCorreoVo> loAutoOr = new ArrayList<OrdenCorreoVo>();
        List<OrdenVO> loTem;
        int i = 0;

        //   for (String usuario : idU) {
        // for (CampoUsuarioPuestoVo campoUsuarioPuestoVo : apCampoUsuarioRhPuestoRemote.getAllPorUsurio(idUsuario)) {
        List<OrdenVO> loAuto = ordenRemote.getOrdenesAutorizaCompras(idUsuario, idCampo);
        if (loAuto != null) {
            //loAuto = new ArrayList<OrdenVO>();
            for (final OrdenVO ordenVO : loAuto) {
                lnomp.add(ordenVO.getProveedor());
            }
            //
            for (final String np : lnomp) {
                OrdenCorreoVo ocv = new OrdenCorreoVo();
                loTem = new ArrayList<OrdenVO>();
                for (OrdenVO ordenVO : loAuto) {
                    if (np.equals(ordenVO.getProveedor())) {
                        i++;
                        final OrdenVO orden = new OrdenVO();
                        orden.setConsecutivo(ordenVO.getConsecutivo());
                        orden.setReferencia(ordenVO.getReferencia());
                        orden.setFecha(ordenVO.getFecha());
                        orden.setTotal(ordenVO.getTotal());
                        orden.setMoneda(ordenVO.getMoneda());
                        loTem.add(orden);
                    }
                }// fin del dor interno
                if (i > 0) {
                    ocv.setCampo(campo);
                    ocv.setProveedor(np);
                    ocv.setLorden(loTem);
                    loAutoOr.add(ocv);
                }
                i = 0;

            }// fin del for de proveedores
        }
        //  }
        //  }
        return loAutoOr;
    }

    public void notificaOrdenSuperaMonto() {
        List<OrdenVO> loTem;
        String campo = Constantes.VACIO;
        String provedor = Constantes.VACIO;
        double total = 0;
//	int i = 0;

        final List<OrdenCorreoVo> loAutoOr = new ArrayList<OrdenCorreoVo>();

        final UsuarioResponsableGerenciaVo urgv
                = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT,
                        Constantes.ID_GERENCIA_IHSA);

        log("Trees  : : : : : " + urgv.getIdUsuario());
        for (final CampoUsuarioPuestoVo campoUsuarioPuestoVo : apCampoUsuarioRhPuestoRemote.getAllPorUsurio(urgv.getIdUsuario())) {
            loAutoOr.addAll(ordenesPorAutorizar(urgv.getIdUsuario(), campoUsuarioPuestoVo.getIdCampo(), campoUsuarioPuestoVo.getCampo()));
        }

        //Monto acululado
        final List<OrdenVO> lo = ordenRemote.traerOrdenSuperaMonto();
        final List<OrdenCorreoVo> listaOrdenCorreo = new ArrayList<OrdenCorreoVo>();

        if (lo != null) {
            final TreeSet<Integer> lger = new TreeSet<Integer>();
            final TreeSet<Integer> lprovee = new TreeSet<Integer>();

            for (final OrdenVO ordenVO : lo) {
                lger.add(ordenVO.getIdGerencia());
                lprovee.add(ordenVO.getIdProveedor());
            }

            String gerencia = Constantes.VACIO;

            for (final int idGer : lger) {
                for (final int idProv : lprovee) {
                    int i = 0;

                    final OrdenCorreoVo ocv = new OrdenCorreoVo();
                    loTem = new ArrayList<OrdenVO>();

                    for (final OrdenVO ordenVO : lo) {
                        if (idGer == ordenVO.getIdGerencia() && idProv == ordenVO.getIdProveedor()) {
                            i++;
                            final OrdenVO orden = new OrdenVO();
                            gerencia = ordenVO.getGerencia();
                            provedor = ordenVO.getProveedor();
                            orden.setConsecutivo(ordenVO.getConsecutivo());
                            orden.setFechaSolicita(ordenVO.getFechaSolicita());
                            orden.setReferencia(ordenVO.getReferencia());
                            orden.setNombreProyectoOT(ordenVO.getNombreProyectoOT());
                            orden.setTotalUsd(ordenVO.getTotalUsd());
                            orden.setEstatus(ordenVO.getEstatus());
                            loTem.add(orden);
                            total += ordenVO.getTotalUsd();
                        }
                    }

                    if (i > 0) {
                        ocv.setGerencia(gerencia);
                        ocv.setProveedor(provedor);
                        ocv.setLorden(loTem);
                        ocv.setTotal(total);
                        listaOrdenCorreo.add(ocv);
                    }

                    provedor = Constantes.VACIO;
                    gerencia = Constantes.VACIO;
                    total = 0;
//		    i = 0;
                }
            }

            if (!listaOrdenCorreo.isEmpty()) {
                notificacionOrdenRemote.sendMailNotificaOrdenSuperaMonto(
                        correoNotificaOrden(urgv.getIdUsuario()),
                        usuarioRemote.find(SIA_USER).getEmail(),
                        listaOrdenCorreo,
                        false
                );
            }
        }

        if (!loAutoOr.isEmpty() || !listaOrdenCorreo.isEmpty()) {

            notificacionOrdenRemote.sendMailNotificaOrdenPorAutorizar(
                    urgv.getEmailUsuario(),
                    usuarioRemote.find(SIA_USER).getEmail(),
                    loAutoOr,
                    listaOrdenCorreo,
                    true
            );
        }

    }

    private String correoNotificaOrden(final String idUsuario) {
        final StringBuilder correo = new StringBuilder();

        final List<UsuarioTipoVo> lu = siUsuarioTipoRemote.getListUser(18, Constantes.AP_CAMPO_DEFAULT);

        for (final UsuarioTipoVo usuarioTipoVo : lu) {
            if (!usuarioTipoVo.getIdUser().equals(idUsuario)) {
                if (correo.length() > 0) {
                    correo.append(',');
                }

                correo.append(usuarioTipoVo.getCorreo());
            }

        }

        return correo.toString();
    }

    private String traerCorreo(final List<UsuarioRolVo> lrol) {
        final StringBuilder cc = new StringBuilder();

        for (final UsuarioRolVo usuarioRol : lrol) {
            if (cc.length() > 0) {
                cc.append(',');
            }

            cc.append(usuarioRol.getCorreo());
        }
        return cc.toString();
    }

    public void notificaOrdenPorAutorizar() {
//	final TreeSet<UsuarioResponsableGerenciaVo> idU = new TreeSet<UsuarioResponsableGerenciaVo>();
        final List<OrdenCorreoVo> loAutoOr = new ArrayList<OrdenCorreoVo>();

        for (final Compania compania : companiaRemote.findAll()) {

            UsuarioResponsableGerenciaVo idUser = null;

            if (!compania.getRfc().equals(Constantes.RFC_IHSA)) {
                for (final ApCampoVo apCampoVo : apCampoRemote.traerApCampoPorEmpresa(compania.getRfc())) {
                    idUser
                            = gerenciaRemote.traerResponsablePorApCampoYGerencia(apCampoVo.getId(),
                                    Constantes.ID_GERENCIA_IHSA);

                    loAutoOr.addAll(
                            ordenesPorAutorizar(
                                    idUser.getIdUsuario(),
                                    apCampoVo.getId(),
                                    apCampoVo.getNombre()
                            )
                    );
                }
                //
                if (null != idUser && !loAutoOr.isEmpty()) {
                    notificacionOrdenRemote.sendMailNotificaOrdenPorAutorizar(
                            idUser.getEmailUsuario(),
                            usuarioRemote.find(SIA_USER).getEmail(),
                            loAutoOr,
                            null,
                            true
                    );
                }
            } //MPG no IHSA

            loAutoOr.clear();
        }

//	log("Trees  : : : : : " + idU.size());
    }

    /**
     * Notifica por correo de nuevos oficios dados de alta en las últimas 24
     * horas.
     *
     */
    private void notificarAltaOficios() throws Exception {

        //jevazquez 15/abr/2015 aprobado
        if (diasAsuetoRemote.buscarByFechaAlDia()) {
            log("No se envio el correo por ser día de asueto");
        } else {
            oficioServicioRemoto.enviarNotificacionAltaOficios();
        }

    }

    //jevazquez 15/abr/2015 aprobado
    private void notificarNoPromovidoOficios() throws Exception {
        //if (!diasAsuetoRemote.buscarByFechaAlDia()){
        oficioServicioRemoto.enviarNotificacionNoPromovidoOficios();
    }

    private void notificarConvenioPorVencer() throws Exception {
        convenioRemote.notificarConvenioPorVencer();
    }

    private void notificarConvenioConveniosVencidos() {
        for (ApCampoVo traerApCampo : apCampoRemote.traerApCampo()) {
            convenioRemote.notificarVencimiento(traerApCampo.getId());
        }

    }

    private void ordenesSinAutorizar() {
        try {
            final List<ApCampoVo> listaCampo = apCampoRemote.traerApCampo();

            for (final ApCampoVo apCampoVo : listaCampo) {
                final List<OrdenVO> listaOrden
                        = autorizacionesOrdenRemote.traerOrdenSinAutorizar(
                                Constantes.ESTATUS_POR_APROBAR_SOCIO,
                                Constantes.DIA_ANTERIOR_TIPO_ESPECIFICO,
                                apCampoVo.getId()
                        );

                if (listaOrden != null && !listaOrden.isEmpty()) {
                    notificacionOrdenRemote.enviarNotificacionOrdenSinAutorizar(
                            correoGerencia(apCampoVo.getId()),
                            listaOrden,
                            apCampoVo.getNombre()
                    );
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al notificar ocs sin autorizar : : : : : : " + e.getMessage());
        }
    }

    private String correoGerencia(final int campo) {
        final StringBuilder sb = new StringBuilder();
        String retVal = SIA_EMAIL;

        //
        final UsuarioResponsableGerenciaVo urg = buscarGerencia(campo, Constantes.ID_GERENCIA_IHSA);

        if (urg != null) {
            log("Nombre + ihsa" + urg.getNombreUsuario());
            sb.append(urg.getEmailUsuario());

            final UsuarioResponsableGerenciaVo urgDir
                    = buscarGerencia(campo, Constantes.GERENCIA_ID_ADMINISTRACION);

            if (urgDir != null) {
                log("Nombre +  finanzas" + urgDir.getNombreUsuario());
                if (sb.length() == 0) {
                    sb.append(urg.getEmailUsuario());
                } else {
                    sb.append(',').append(urgDir.getEmailUsuario());
                }
            }

            final UsuarioResponsableGerenciaVo urgCompra
                    = buscarGerencia(campo, Constantes.GERENCIA_ID_COMPRAS);

            if (urgCompra != null) {
                log("Nombre + compras" + urgCompra.getNombreUsuario());
                if (sb.length() == 0) {
                    sb.append(urg.getEmailUsuario());
                } else {
                    sb.append(',').append(urgCompra.getEmailUsuario());
                }
            }
        }

        if (sb.length() > 0) {
            retVal = sb.toString();
        }

        //
        return retVal;
    }

    private UsuarioResponsableGerenciaVo buscarGerencia(final int campo, final int gerencia) {
        UsuarioResponsableGerenciaVo urg;
        urg = gerenciaRemote.traerResponsablePorApCampoYGerencia(campo, gerencia);
        return urg;
    }

    private void reporteCompradores() {
        try {
            Calendar c = Calendar.getInstance();
            int mes = c.get(c.MONTH);
            int anio = c.get(c.YEAR);

            List<ApCampoVo> lcp = apCampoRemote.traerApCampo();
            for (ApCampoVo apCampoVo : lcp) {
                String sb1 = "Reporte de analistas de compras ( " + apCampoVo.getNombre() + " )";
                List<UsuarioVO> lURol = usuarioRemote.traerListaRolPrincipalUsuarioRolModulo(Constantes.ROL_COMPRADOR, Constantes.MODULO_COMPRA, apCampoVo.getId());
                List<RequisicionReporteVO> listaNumeroComprador = new ArrayList<RequisicionReporteVO>();
                List<RequisicionReporteVO> lReporte = new ArrayList<RequisicionReporteVO>();
                for (UsuarioVO usuarioVO : lURol) {
                    //Reporte detalle de req
                    RequisicionReporteVO r = new RequisicionReporteVO();
                    List<RequisicionVO> lr = requisicionRemote.listaRequisicionAsignadas(usuarioVO.getId(), Constantes.ESTATUS_ASIGNADA, Constantes.DIAS_REPORTE_COMPRADORES, apCampoVo.getId());
                    //Reporte totales
                    if (lr.size() > 0) {
                        r.setComprador(usuarioVO.getNombre());
                        r.setLRequisicion(lr);
                        lReporte.add(r);
                        //
                        long total = 0;
                        List<RequisicionVO> lrTemp = new ArrayList<RequisicionVO>();
                        RequisicionReporteVO rrvo = new RequisicionReporteVO();
                        rrvo.setComprador(usuarioVO.getNombre());
                        for (int numMes = 0; numMes <= mes; numMes++) {
                            RequisicionVO req = new RequisicionVO();
                            //
                            long totalMes = requisicionRemote.totalRequisionesPorMes(usuarioVO.getId(), apCampoVo.getId(), (numMes + 1), Constantes.DIAS_REPORTE_COMPRADORES, Constantes.ESTATUS_ASIGNADA, anio);

                            //
                            String m = Constantes.MESES[numMes];
                            if (totalMes > 0) {
                                req.setCadena(m);
                                req.setTotalItems(totalMes);
                                lrTemp.add(req);
                                total += totalMes;
                            }
                        }
                        rrvo.setLRequisicion(lrTemp);
                        //Anios anteriores al actual
                        String mens = "Anterior a " + anio;
                        rrvo.setCadena(mens);
                        rrvo.setTotalAnioAnteriores(requisicionRemote.totalRequisionesPendienteDesdeAniosAnterior(usuarioVO.getId(), apCampoVo.getId(),
                                anio, Constantes.DIAS_REPORTE_COMPRADORES, Constantes.ESTATUS_ASIGNADA));
                        total += rrvo.getTotalAnioAnteriores();
                        rrvo.setTotalRequisiciones(total);
                        listaNumeroComprador.add(rrvo);
                    }
                } // fin del comprador
                //Se agrega a la lista del total de requisiones por mes y anio
                if (lReporte.size() > 0) {
                    UsuarioResponsableGerenciaVo urgv = gerenciaRemote.traerResponsablePorApCampoYGerencia(apCampoVo.getId(), Constantes.GERENCIA_ID_COMPRAS);
                    notificacionRequisicionRemote.envioReporteDiarioCompradores(urgv.getEmailUsuario(), "", usuarioRemote.find("SIA").getEmail(),
                            lReporte, sb1, Constantes.DIAS_REPORTE_COMPRADORES, listaNumeroComprador);
                }
                //
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    private void cambioDeAprobacion(boolean cambiar) throws Exception {
        try {
            List<Integer> li = new ArrayList<Integer>();
            li.add(Constantes.SGL_SEGURIDAD);
            li.add(Constantes.ROL_CENTRO_OPERACION);
            List<UsuarioRolVo> lu = siUsuarioRolRemote.traerUsuarioByRol(li, Constantes.AP_CAMPO_NEJO);
            Joiner join = Joiner.on(",").skipNulls();
            List<String> correos = new ArrayList<String>();
            for (UsuarioRolVo vo : lu) {
                correos.add(vo.getCorreo());
            }
            String cc = join.join(correos);
            List<SolicitudViajeVO> list = sgSolicitudViajeRemote.traerSolicitudesTerrestreByEstatus(
                    Constantes.ESTATUS_APROBAR, Constantes.CERO, null, " AND s.fecha_salida = CAST('tomorrow' AS DATE)");
            String title = "Reporte de solicitudes de viaje sin aprobación del gerente responsable al dia ";
            if (cambiar) {
                title = "Reporte de solicitudes de viaje sin aprobación pasadas al departamento de Gestion de Riesgos ";
                SgEstatusAprobacion sgEstatusAprobacion;
                SgSolicitudViaje sv;
                Usuario u = usuarioRemote.find("SIA");
                for (SolicitudViajeVO vo : list) {
                    sv = sgSolicitudViajeRemote.find(vo.getIdSolicitud());
                    sgEstatusAprobacion = new SgEstatusAprobacion();
                    sgEstatusAprobacion.setSgSolicitudViaje(sv);
                    sgEstatusAprobacion.setAutomatico(Constantes.BOOLEAN_TRUE);
                    sgEstatusAprobacion.setEliminado(Constantes.BOOLEAN_FALSE);
                    sgEstatusAprobacion.setEstatus(estatusRemote.find(Constantes.ESTATUS_GERENTE_NO_APROBO));
                    sgEstatusAprobacion.setFechaGenero(new Date());
                    sgEstatusAprobacion.setHoraGenero(new Date());
                    sgEstatusAprobacion.setGenero(u);
                    sgEstatusAprobacion.setRealizado(Constantes.BOOLEAN_TRUE);
                    sgEstatusAprobacion.setHistorial(Constantes.BOOLEAN_TRUE);
                    sgEstatusAprobacion.setUsuario(u);
                    sgEstatusAprobacionRemote.create(sgEstatusAprobacion);

                    sgEstatusAprobacion = new SgEstatusAprobacion();
                    sgEstatusAprobacion.setSgSolicitudViaje(sv);
                    sgEstatusAprobacion.setAutomatico(Constantes.BOOLEAN_FALSE);
                    sgEstatusAprobacion.setEliminado(Constantes.BOOLEAN_FALSE);
                    sgEstatusAprobacion.setEstatus(estatusRemote.find(Constantes.ESTATUS_CON_CENTOPS));
                    sgEstatusAprobacion.setFechaGenero(new Date());
                    sgEstatusAprobacion.setHoraGenero(new Date());
                    sgEstatusAprobacion.setGenero(u);
                    sgEstatusAprobacion.setRealizado(Constantes.BOOLEAN_FALSE);
                    sgEstatusAprobacion.setHistorial(Constantes.BOOLEAN_FALSE);
                    sgEstatusAprobacion.setUsuario(usuarioRemote.find("CENTOPS"));
                    sgEstatusAprobacionRemote.create(sgEstatusAprobacion);
                    sv.setEstatus(estatusRemote.find(Constantes.ESTATUS_CON_CENTOPS));
                    sgSolicitudViajeRemote.update(sv, u.getId());

                }
            }

            if (list.size() > 0) {
                notificacionViajeRemote.sendMailNotificarGR(list, cc, Constantes.FMT_ddMMyyy.format(new Date()), title);
                if (title.equals("Reporte de solicitudes de viaje sin aprobación del gerente responsable al dia ")) {
                    for (SolicitudViajeVO vo : list) {
                        EstatusAprobacionSolicitudVO ea= sgEstatusAprobacionRemote.buscarEstatusAprobacionPorIdSolicitudIdEstatus(vo.getIdSolicitud(), vo.getIdEstatus());
                        SgEstatusAprobacion e = sgEstatusAprobacionRemote.find(ea.getId());
                        notificacionViajeRemote.enviarCorreoEstatusSolicitudViajePorAprobar(e,
                                Constantes.MENSAJE_ESTATUS_SOLICITUD_VIAJE_APROBAR + " " + vo.getCodigo(), vo, null, Constantes.AP_CAMPO_DEFAULT);
                    }
                }

            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }

    }

    public void reporteGerenteSG() throws SIAException {
        UsuarioVO u = usuarioRemote.traerResponsableGerencia(Constantes.AP_CAMPO_DEFAULT, Constantes.GERENCIA_ID_SERVICIOS_GENERALES, SIA_EMAIL);
        String title = "Reporte de viaje Programados ";
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();

        List<SolicitudViajeVO> listSol
                = sgSolicitudViajeRemote.traerSolicitudesTerrestreByEstatus(
                        Constantes.CERO,
                        Constantes.CERO,
                        null,
                        " AND s.fecha_salida = CAST('tomorrow' AS DATE)"
                );
        List<SolicitudViajeVO> listSolSA
                = sgSolicitudViajeRemote.traerSolicitudesTerrestreByEstatus(
                        Constantes.ESTATUS_PENDIENTE,
                        Constantes.ESTATUS_PARA_HACER_VIAJE,
                        null,
                        " AND s.fecha_salida = CAST('tomorrow' AS DATE)"
                );

        List<OficinaVO> oficinas = sgOficinaRemote.traerListaOficina();

        List<List<ViajeVO>> viajesByOficinas = new ArrayList<List<ViajeVO>>();

        for (OficinaVO of : oficinas) {
            List<ViajeVO> viajes
                    = sgViajeRemote.getRoadTripByExit(
                            of.getId(),
                            Constantes.ESTATUS_VIAJE_POR_SALIR,
                            Constantes.CERO,
                            Constantes.TRUE,
                            dt,
                            dt,
                            Constantes.FALSE,
                            Constantes.CERO,
                            Constantes.TRUE,
                            null
                    );
            viajesByOficinas.add(viajes);
        }

        notificacionViajeRemote.sendMailNotificarGerenteServicios(
                viajesByOficinas,
                u.getMail(),
                Constantes.FMT_ddMMyyy.format(new Date()),
                title,
                listSol,
                listSolSA
        );

        // List<ViajeVO> viajesRey = sgViajeRemote.getRoadTripByExit(2, Constantes.ESTATUS_VIAJE_POR_SALIR, Constantes.CERO, true, fecha1, fecha2, true, 0, true, Boolean.TRUE);
        // List<ViajeVO> viajesSF = sgViajeRemote.getRoadTripByExit(3, Constantes.ESTATUS_VIAJE_POR_SALIR, Constantes.CERO, true, fecha1, fecha2, true, 0, true, Boolean.TRUE);
    }

    private void envioCorreoVencimientoLicenciaSemanal() {

        List<LicenciaVo> li;
        List<ApCampoVo> listCampo = apCampoRemote.traerApCampo();
        List <LicenciaVo> tem = new ArrayList<>();
        int gerencia = 0;

        for (ApCampoVo a : listCampo) {
            li = sgLicenciaRemote.traerLicienciaVigente(Constantes.TRUE, a.getId(), " order by o.id, l.FECHA_VENCIMIENTO");
            if (li != null && !li.isEmpty()) {
                boolean notifico = notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoLicenciasPorSemana(a.getId(), li, Constantes.CERO);
                
                if(notifico){
                    for (LicenciaVo licenciaActual : li){
                        notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoLicenciaByUser(licenciaActual, Constantes.TRUE);
                    }
                    li = sgLicenciaRemote.traerLicienciaVigente(Constantes.TRUE, a.getId(), " order by u.gerencia,o.id, l.FECHA_VENCIMIENTO");
                    
                    for (LicenciaVo vo :li){
                        if(tem.isEmpty()){
                            tem.add(vo);
                            gerencia = vo.getGerencia();
                        } else {
                            
                            if(gerencia == vo.getGerencia()){
                                tem.add(vo);
                                
                            } else {
                               // curByGerencia.add(tem);
                               notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoLicenciasPorSemana(a.getId(), tem, gerencia);
                                tem = new ArrayList<>();
                                tem.add(vo);
                                gerencia = vo.getGerencia();
                            }
                        }
                    }
                    notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoLicenciasPorSemana(a.getId(), tem, gerencia);
                }
            } else {
                log("No existen licencias por vencer proximamente en el bloque" + a.getNombre());
            }

        }

    }

    public void enviarCorreoVencimientoCursoManejoSemanal() {

        List<ApCampoVo> listCampo = apCampoRemote.traerApCampo();
        Date nextMes = siManejoFechaLocal.fechaSumarMes(new Date(), Constantes.MESES_PREVIOS);
        List <CursoManejoVo> tem = new ArrayList<>();
        int gerencia = 0;
        for (ApCampoVo campo : listCampo) {
            List<CursoManejoVo> listCurso = cursoManejoRemote.usuariosCursosActivosByVencimeintoAndCampo(
                    new Date(), nextMes, campo.getId(),Constantes.TRUE,Constantes.FALSE, "   order by ofi.id,c.FECHA_VENCIMIENTO");
            if (!listCurso.isEmpty()) {
                boolean notifico = notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoCursoManejo(campo.getId(), listCurso, Constantes.CERO);
                
                
                if (notifico) {
                    for (CursoManejoVo curso : listCurso) {
                        notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoCursoManejoByUser(curso, Constantes.TRUE);
                        
                    }
                    listCurso = cursoManejoRemote.usuariosCursosActivosByVencimeintoAndCampo(
                    new Date(), nextMes, campo.getId(),Constantes.TRUE,Constantes.FALSE, "   order by u.gerencia, ofi.id, c.FECHA_VENCIMIENTO");
                    
                    for (CursoManejoVo vo :listCurso){
                        if(tem.isEmpty()){
                            tem.add(vo);
                            gerencia = vo.getGerencia();
                        } else {
                            
                            if(gerencia == vo.getGerencia()){
                                tem.add(vo);
                                
                            } else {
                               // curByGerencia.add(tem);
                               notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoCursoManejo(campo.getId(), tem, gerencia);
                                tem = new ArrayList<>();
                                tem.add(vo);
                                gerencia = vo.getGerencia();
                            }
                        }
                    }
                    notificacionServiciosGeneralesRemote.enviarAvisoNotificacionVencimientoCursoManejo(campo.getId(), tem, gerencia);
                }
            } else {
                log("No existen cursos de manejo por vencer proximamente en el bloque " + campo.getNombre());
            }
        }

    }
    
    public void quitarVigenciaCursoManejo(){
        List<CursoManejoVo> listCurso = cursoManejoRemote.relacionUsuarioCursos(Constantes.FALSE, Constantes.FALSE, Constantes.FALSE);
        for(CursoManejoVo c : listCurso){
            cursoManejoRemote.quitarVigenciaCursoVencido(c.getIdCursoManejo(), Constantes.SIA);
        }
        
    }
    
    public void quitarVigenciaLicencias(){
        List<LicenciaVo> listLicencias = sgLicenciaRemote.traerLicienciaVigente(Constantes.FALSE, 
                Constantes.AP_CAMPO_DEFAULT, "   order by ofi.id,c.FECHA_VENCIMIENTO");
        
        for (LicenciaVo vo : listLicencias){
            sgLicenciaRemote.quitarLicenciaVigente(usuarioRemote.find(Constantes.SIA), vo.getId());
        }
    }
}
