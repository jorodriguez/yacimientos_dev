/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import com.newrelic.api.agent.Trace;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.Record;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.CoNoticia;
import sia.modelo.SgEstadoSemaforo;
import sia.modelo.SgEstatusAprobacion;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgVehiculo;
import sia.modelo.SgViaje;
import sia.modelo.SgViajero;
import sia.modelo.SiMovimiento;
import sia.modelo.SiOperacion;
import sia.modelo.SiUsuarioCodigo;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.CadenaAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.JustIncumSolVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeDestinoVo;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.EstatusAprobacionVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.semaforo.impl.SgEstatusAlternoImpl;
import sia.servicios.sgl.semaforo.impl.SgRolApruebaSolicitudImpl;
import sia.servicios.sgl.vehiculo.impl.SgAsignarVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaCiudadImpl;
import sia.servicios.sgl.viaje.impl.SgItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeSiMovimientoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.servicios.sistema.impl.SiUsuarioCodigoImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;
import sia.util.notificacion.FCMSender;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgEstatusAprobacionImpl extends AbstractFacade<SgEstatusAprobacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    DSLContext dbCtx;

    @Inject
    private SgCadenaAprobacionImpl cadenaAprobacionService;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private EstatusImpl estatusService;
    @Inject
    private FolioImpl folioService;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SgSolicitudViajeSiMovimientoImpl relacionSolicitudSiMovimientoService;
    @Inject
    private SiMovimientoImpl siMovimientoService;
    @Inject
    private SiOperacionImpl siOperacionService;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private NotificacionViajeImpl notificacionViajeService;
    @Inject
    private SgViajeroImpl viajeroService;
    @Inject
    private CoNoticiaImpl coNoticiaService;
    @Inject
    private SgItinerarioImpl servicioItinerario;
    @Inject
    private SgViajeCiudadImpl sgViajeCiudadRemote;
    @Inject
    private SgEstatusAlternoImpl sgEstatusAlternoRemote;
    @Inject
    private SgRolApruebaSolicitudImpl sgRolApruebaSolicitudRemote;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private SgJustIncumpSolImpl sgJustIncumpSolRemote;
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private SgAsignarVehiculoImpl sgAsignarVehiculoRemote;

    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgDetalleRutaCiudadImpl sgDetalleRutaCiudadRemote;
    @Inject
    private SgItinerarioImpl sgItinerarioRemote;
    @Inject
    private SiUsuarioCodigoImpl siUsuarioCodigoLocal;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaRemote;
    @Inject
    private SgVehiculoImpl sgVehiculoRemote;
    @Inject
    private ApCampoImpl apCampoRemote;

    @Inject
    DSLContext dslCtx;

    //usar el metodo de marino que trae los roles de un usuario
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgEstatusAprobacionImpl() {
        super(SgEstatusAprobacion.class);
    }

    private UsuarioResponsableGerenciaVo getResponsableByGerencia(int campo, int idGerencia) {
        return gerenciaRemote.traerResponsablePorApCampoYGerencia(campo, idGerencia);
    }

    
    public boolean solicitarViaje(String correoSesion, SolicitudViajeVO solicitudEncontrada, String idUsuario, int estancia, boolean conChofer, int idVehiculo, int idApCampo) {
        CadenaAprobacionSolicitudVO primerCadenaVo = null;
        SgSolicitudViaje solicitudViaje = sgSolicitudViajeRemote.find(solicitudEncontrada.getIdSolicitud());
        solicitudViaje.setConChofer(conChofer);
        if (idVehiculo > 0) {
            solicitudViaje.setSgVehiculo(sgVehiculoRemote.find(idVehiculo));
        }
        boolean correoEnviado = true, retorno = false;
        try {
            UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(idApCampo, solicitudEncontrada.getIdGerencia());
            if (solicitudEncontrada != null) {
                //traer la primer cadena de aprobacion para agregarla a estatus de aprobacion
                UsuarioRolVo urvo = siUsuarioRolRemote.traerRolPrincipal(idUsuario, Constantes.MODULO_SGYL, idApCampo);
                if (urvo != null) {
                    if ((usuarioResponsableGerenciaVo.getIdUsuario().equals(idUsuario)
                            || urvo.getIdRol() == Constantes.ROL_ID_SGL_CAPACITACION
                            || urvo.getIdRol() == Constantes.ROL_ID_ASISTENTE_DIRECCION)) {
                        if (solicitudViaje.getSgTipoSolicitudViaje().getId() == Constantes.SOLICITUDES_TERRESTRE) {
                            primerCadenaVo = traerProximaCadenaAprobacion(solicitudViaje, Constantes.ESTATUS_APROBAR);
                        } else {
                            primerCadenaVo = traerProximaCadenaAprobacion(solicitudViaje, Constantes.ESTATUS_VISTO_BUENO);
                        }
                    } else {
                        primerCadenaVo = traerProximaCadenaAprobacion(solicitudViaje, Constantes.ESTATUS_PENDIENTE);
                    }
                } else {
                    primerCadenaVo = traerProximaCadenaAprobacion(solicitudViaje, Constantes.ESTATUS_PENDIENTE);
                }
                if (primerCadenaVo != null) {
                    //generar codigo
                    solicitudEncontrada.setCodigo(generateCode());
                    //
                    solicitudViaje.setCodigo(solicitudEncontrada.getCodigo());
                    //enviar correo de solicitud
                    correoEnviado = notificacionViajeService.enviarCorreoSolicitarViaje(solicitudEncontrada, primerCadenaVo, correoSesion, usuarioResponsableGerenciaVo.getNombreUsuario());
                    //verificar si el usuario rol se autoAprueba
                    if (correoEnviado) {
                        if (crearEstatusAprobacion(primerCadenaVo, solicitudViaje, idUsuario, conChofer, idVehiculo, idUsuario, idApCampo)) {
                            insertarEstatusAprobacionAutomatico(solicitudViaje, Constantes.ESTATUS_SOLICITADA, idUsuario);
                            UtilLog4j.log.info(this, "Se ha solicittado correctramente-.---.-.-.-.-.-.-.-.");
                            if (primerCadenaVo.isUltimaCadena()) {
                                if (conChofer) {
                                    log("Se insertara la ultima cadena de aprobacion - se lanzaran las estancias");
                                } else {
                                    if (idVehiculo > 0) {
                                        // insertarEstatusAprobacionAutomatico(solicitudViaje, Constantes.ESTATUS_APROBAR, idUsuario);
                                        solicitudViaje.setEstatus(estatusService.find(Constantes.ESTATUS_PARA_HACER_VIAJE));
                                        
                                        insertarEstatusAprobacion(solicitudViaje, primerCadenaVo, "SIA", idUsuario, Constantes.FALSE, Constantes.FALSE, Constantes.TRUE, idApCampo);
                                        //insertarEstatusAprobacionAutomatico(solicitudViaje, Constantes.ESTATUS_PARA_HACER_VIAJE, idUsuario);
                                    }

                                }
                                sgSolicitudViajeRemote.edit(solicitudViaje);
                                //esto es nuevo
                                //Lanzar las estancias por que ya aprobaron todo
                                this.sgSolicitudEstanciaRemote.solicitarSolicitudEstanciaCreadaPorSolcicitudViajePendienteDeSolicitar(solicitudViaje, idUsuario);
                            } else {
                                log("no es la ultima cadena de aprobacion");
                            }
                            retorno = true;
                        }
                    }
                }
            } else {
                UtilLog4j.log.info(this, "Solicitud no encontrada .....");
                return false;
            }
            return retorno;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exception al Request " + e);
            return false;
        }
    }

    

    
    @Trace
    public boolean aprobarSolicitud(int idEstatusAprobacion, String idUsuario) {
        UtilLog4j.log.info(this, "SgEstatusAprobacionImpl.aprobarSolicitud()");
        boolean ret = false;
        boolean esConChofer;
        CadenaAprobacionSolicitudVO cadenaProximaVo = null;
        try {
            SgEstatusAprobacion estatusAprobacion = find(idEstatusAprobacion);
            if (estatusAprobacion.getSgSolicitudViaje().isConChofer()) {
                esConChofer = estatusAprobacion.getSgSolicitudViaje().isConChofer();
            } else {
                esConChofer = Constantes.FALSE;
            }

            if (realizarAprobacion(estatusAprobacion, Constantes.ESTATUS_APROBADO, Constantes.ESTATUS_HISTORIAL, idUsuario)) {
                cadenaProximaVo = traerProximaCadenaAprobacion(estatusAprobacion.getSgSolicitudViaje(), estatusAprobacion.getEstatus().getId());
                if (cadenaProximaVo != null) {
                    SgVehiculo veh = estatusAprobacion.getSgSolicitudViaje().getSgVehiculo();
                    //sgAsignarVehiculoRemote.traerVehiculobyResponsable(estatusAprobacion.getSgSolicitudViaje().getGenero().getId());
                    int idVehiculo = (veh != null ? veh.getId() : Constantes.CERO);
                    if (crearEstatusAprobacion(cadenaProximaVo, estatusAprobacion.getSgSolicitudViaje(), idUsuario,
                            esConChofer, idVehiculo, estatusAprobacion.getSgSolicitudViaje().getGenero().getId(),
                            estatusAprobacion.getSgSolicitudViaje().getApCampo().getId())) {
                        if (cadenaProximaVo.isUltimaCadena()) {
                            if (esConChofer) {
                                log("Se insertara la ultima cadena de aprobacion - se lanzaran las estancias");
                            } else {
                                if (idVehiculo > 0) {
                                    SgSolicitudViaje solicitudViaje = estatusAprobacion.getSgSolicitudViaje();
                                    solicitudViaje.setEstatus(estatusService.find(Constantes.ESTATUS_PARA_HACER_VIAJE));
                                    sgSolicitudViajeRemote.edit(solicitudViaje);
                                    insertarEstatusAprobacionAutomatico(solicitudViaje, Constantes.ESTATUS_PARA_HACER_VIAJE, idUsuario);
                                }

                            }
                            //Lanzar las estancias por que ya aprobaron todo
                            this.sgSolicitudEstanciaRemote.solicitarSolicitudEstanciaCreadaPorSolcicitudViajePendienteDeSolicitar(estatusAprobacion.getSgSolicitudViaje(), idUsuario);

                        } else {
                            log("no es la ultima cadena de aprobacion");
                        }
                        ret = true;
                    }
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.error(this, "Excepción en traer próxima cadena de aprobación ", e);
            UtilLog4j.log.fatal(e, idUsuario);
        }

        return ret;
    }

    
    public boolean aprobarJustificandoSolicitud(int idEstatusAprobacion, int idSolicitud, String justificacionMotivo, String idUsuarioRealizo) {
        UtilLog4j.log.info(this, "SgEstatusAprobacionImpl.justificarSolicitud()");
        boolean r = false;
        try {
            //guardar  en la tabla de justificaciones
            if (sgJustIncumpSolRemote.guardarJustificacionPorAprobacionSolicitud(idSolicitud, justificacionMotivo, idUsuarioRealizo)) {
                aprobarSolicitud(idEstatusAprobacion, idUsuarioRealizo);
                r = true;
            }
            return r;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepción en traer próxima cadena de aprobación cuando se justifica por parte de gerentes " + e.getMessage());
            return false;
        }
    }

    
    public boolean aprobarListaSolicitudes(List<EstatusAprobacionVO> lista, boolean sonAutomaticas, String idUsuario) {
        boolean ret = false;
        CadenaAprobacionSolicitudVO cadenaProximaVo = null;
        try {
            for (EstatusAprobacionVO vo : lista) {
                log("Aprobar " + vo.getCodigo());
                SgEstatusAprobacion estatusAprobacion = find(vo.getId());
                if (realizarAprobacionAutomatica(estatusAprobacion, Constantes.USUARIO_SIA)) {
                    log("Traer proxima cadena de aprobacion");
                    cadenaProximaVo = traerProximaCadenaAprobacion(estatusAprobacion.getSgSolicitudViaje(), estatusAprobacion.getEstatus().getId());
                    if (cadenaProximaVo != null) {
                        VehiculoVO veh = sgAsignarVehiculoRemote.traerVehiculobyResponsable(idUsuario);
                        int idvehiculo = (veh != null ? veh.getId() : Constantes.CERO);
                        log("Cadena encontrada " + cadenaProximaVo.getNombreEstatus());
                        if (crearEstatusAprobacion(cadenaProximaVo, estatusAprobacion.getSgSolicitudViaje(), Constantes.USUARIO_SIA,
                                estatusAprobacion.getSgSolicitudViaje().isConChofer(), 
                                idvehiculo, "", estatusAprobacion.getSgSolicitudViaje().getApCampo().getId())) {
                            if (verficarPermisoInsercionEnSemaforoAlterno(estatusAprobacion.getEstatus().getId(), estatusAprobacion.getSgEstadoSemaforo().getSgSemaforo().getId())) {
                                this.sgSolicitudEstanciaRemote.solicitarSolicitudEstanciaCreadaPorSolcicitudViajePendienteDeSolicitar(estatusAprobacion.getSgSolicitudViaje(), Constantes.USUARIO_SIA);
                            }
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepción en aprobarListaSolicitudes " + e.getMessage());
            return false;
        }
    }

    /**
     * Metodo que evalua una cadena de aprobacion Creado : Joel Rodriguez ultima
     * modificacion : 18/nov/2013
     *
     * @param cadenaVo
     * @param solicitudViaje
     * @param siRol
     * @param idUsuario
     * @return
     * @throws SIAException
     * @throws Exception
     */
    // En este metodo se puede meter todo lo de crear el viaje tendria que mandar el vehiculo y llamar a sgviajeRemote
    @Trace
    private boolean crearEstatusAprobacion(CadenaAprobacionSolicitudVO cadenaVo, SgSolicitudViaje solicitudViaje, String idUsuario,
            boolean conChofer, int idVehiculo, String responsable, int idApCampo) throws SIAException, Exception {
        UtilLog4j.log.info(this, "crearEstatusAprobacion " + cadenaVo.isUltimaCadena());
        String idUsuarioQueAprobaraEstatus = "";
        boolean retorno = false;
        boolean isTerrestre = true;
        /**
         * Si es la ultima cadena de aprobacion enviar a los analistas de SGL
         *///Mejorar esto...
        if (cadenaVo.isVerificarSemaforoAlterno()) { //si es de estatus alterno, pasa al jefe
            idUsuarioQueAprobaraEstatus = evaluarUsuarioQueApruebaEstatus(cadenaVo, solicitudViaje, idApCampo);
        } else {
            if (verificarEsRolAutoApruebaSolicitud(solicitudViaje.getGenero().getId())
                    && cadenaVo.getIdEstatus() != Constantes.ESTATUS_AUTORIZAR) {
                if (solicitudViaje.getSgTipoSolicitudViaje().getSgTipoEspecifico().getId() == Constantes.SOLICITUDES_TERRESTRE) {///Verifica si la solicitud es terrestre para agregar al analista
                    if (cadenaVo.isUltimaCadena()) {
                        idUsuarioQueAprobaraEstatus = this.buscarAnalistaParaHacerViajePorOficina(solicitudViaje.getOficinaOrigen().getId());
                    } else {
                        //poner a el mimo en la cadena..
                        UtilLog4j.log.info(this, ">>>>> El usuario en sesion aprueba la solicitud en el estatus " + cadenaVo.getIdEstatus());
                        idUsuarioQueAprobaraEstatus = solicitudViaje.getGenero().getId();

                    }
                } else {
                    //poner a el mimo en la cadena..
                    UtilLog4j.log.info(this, ">>>>> El usuario en sesion aprueba la solicitud en el estatus " + cadenaVo.getIdEstatus());
                    idUsuarioQueAprobaraEstatus = solicitudViaje.getGenero().getId();
                    //Controla si el usuario en la cadena de aprobacion se autoAprueba el flujo de solicitud, verifica que este en la tabla de Roles autoaprobadores
                    //sirve tambien para controlar correos para crear viajes en el estatus 450.
                    cadenaVo.setAutoApruebaFlujo(true);
                    isTerrestre = false;
                }
            } else {
                if (cadenaVo.isUltimaCadena() && solicitudViaje.getSgTipoSolicitudViaje().getSgTipoEspecifico().getId() == Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE) {
                    UtilLog4j.log.info(this, ">>>>>Es la ultima cadena de aprobacion ");
                    if (conChofer) {
                        idUsuarioQueAprobaraEstatus = this.buscarAnalistaParaHacerViajePorOficina(solicitudViaje.getOficinaOrigen().getId());
                    } else {
                        if (idVehiculo > 0) {

                            Calendar fechaCompleta = Calendar.getInstance();
                            Calendar hs = Calendar.getInstance();
                            fechaCompleta.setTime(solicitudViaje.getFechaSalida());
                            hs.setTime(solicitudViaje.getHoraSalida());
                            fechaCompleta.set(Calendar.HOUR, hs.get(Calendar.HOUR));
                            fechaCompleta.set(Calendar.MINUTE, hs.get(Calendar.MINUTE));
                            Date fecha = fechaCompleta.getTime();

                            SgViaje viaje = sgViajeRemote.guardarViajeEmergenteVO(idUsuario, null, fecha, Constantes.CERO, Constantes.CERO, idVehiculo,
                                    responsable, solicitudViaje.getSgRutaTerrestre().getId(), solicitudViaje.getOficinaOrigen().getId(),
                                    solicitudViaje.getSgTipoEspecifico().getId(), null, (solicitudViaje.isRedondo()),
                                    Constantes.ESTATUS_VIAJE_POR_SALIR, Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_FALSE);

                            Usuario u = new Usuario(idUsuario);
                            List<SgViajero> lv = sgViajeroRemote.getViajerosBySinViaje(solicitudViaje.getId());
                            sgViajeroRemote.update(u, lv, viaje);
                            //sgViajeRemote.moverViajeAProgramado(idUsuario, viaje.getId());
                            if (solicitudViaje.isRedondo()) {
                                viaje.setFechaRegreso(solicitudViaje.getFechaRegreso());
                                viaje.setHoraRegreso(solicitudViaje.getHoraRegreso());
                                sgViajeRemote.edit(viaje);
                            }
                            retorno = true;
                        }

                    }

                } else {//cadena normal
                    idUsuarioQueAprobaraEstatus = evaluarUsuarioQueApruebaEstatus(cadenaVo, solicitudViaje, idApCampo);
                }
            }
        }
        //insertar cadena de aprobacion
        if (!idUsuarioQueAprobaraEstatus.equals("")) {
            //**Propuesta: antes de insertar Checar si el usuario a aprobar esta de vacaciones
            //**Crear una tabla donde este el usuario que se obtiene idUsuarioQueAprobaraEstatus y buscarlo en la tabla
            //**si esta : obtener al suplente y agregado checar la fecha..
            //** posteriormente este usuario se inertara
            insertarEstatusAprobacion(solicitudViaje, cadenaVo, idUsuarioQueAprobaraEstatus, idUsuario,
                    Constantes.ESTATUS_NO_APROBADO, Constantes.ESTATUS_NO_HISTORIAL, isTerrestre, idApCampo);
            //Actualizar solicitud
            updateStateSolicitudViaje(solicitudViaje.getId(), cadenaVo.getIdEstatus(), idUsuario,solicitudViaje.getHoraSalida());
            //publicar noticia
            this.createEventNews(solicitudViaje.getId(), idUsuario);
            UtilLog4j.log.info(this, ">>>>>>>>>>SE INSERTO EL NUEVO ESTATUS<<<<<<<<<<");
            UtilLog4j.log.info(this, "Estatus de aprobacion " + cadenaVo.getIdEstatus() + " lo aprueba " + idUsuarioQueAprobaraEstatus);
            //falta compartir con el proximo de la cadena
            if (cadenaVo.isUltimaCadena() && !isTerrestre) {
                Usuario usuario = usuarioRemote.find(idUsuarioQueAprobaraEstatus);
                sgViajeRemote.crearViajeAereo(solicitudViaje.getOficinaOrigen().getId(), usuario.getId(), solicitudViaje.getId(), usuario.getEmail());
            }
            retorno = true;

            try {
                enviarNotificacion(Constantes.TITULO_SOL_VIAJE_NOTIFICACION, solicitudViaje.getSgMotivo().getNombre(), idUsuarioQueAprobaraEstatus);
                //
            } catch (Exception ex) {
                Logger.getLogger(OrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retorno;
    }


    /*
     * Metodo de apoyo al metodo CrearEstatusAprobacion Se ocupa para tomar el
     * usuario que realizara la aprobacion de un estatus, dependiendo de la
     * cadena de aprobacion consultada retorna el id del usuario que aprobara la
     * cadena que entra como parametro. Joel Rodriguez.
     */
    private String evaluarUsuarioQueApruebaEstatus(CadenaAprobacionSolicitudVO cadenaVo, SgSolicitudViaje solicitudViaje, int idApCampo) {
        UtilLog4j.log.info(this, "evaluarUsuarioQueApruebaEstatus");
        try {
            if (cadenaVo.isApruebaGerenteArea()) {
                UtilLog4j.log.info(this, ">>>GERENTE - El estatus lo aprueba el gerente de AREA ..");
                return getResponsableByGerencia(idApCampo, solicitudViaje.getGerenciaResponsable().getId()).getIdUsuario();
            } else {
                if (cadenaVo.isApruebaRol()) {
                    ///aqui preguntar si es la ultima cadena, entonces es para crear viaje y saber si enviara
                    UtilLog4j.log.info(this, ">>>>>-ROL - Aprueba el Rol de la cadena de aprobacion con siRol" + cadenaVo.getIdSiRol());
                    UsuarioRolVo usuarioRolVo = siUsuarioRolRemote.traerRolUsuarioModulo(cadenaVo.getIdSiRol(), Constantes.MODULO_SGYL, Constantes.BOOLEAN_TRUE, Constantes.AP_CAMPO_NEJO);
                    if (usuarioRolVo != null) {
                        UtilLog4j.log.info(this, "-------en el rol se encontro el usuario " + usuarioRolVo.getIdUsuario());
                        return usuarioRolVo.getIdUsuario();
                    } else {
                        UtilLog4j.log.info(this, "XXX- NO EXISTEN USUARIO EN EL ROL ESPECIFICADO (X_X) XXXXXX");
                        return "";
                    }
                } else {
                    UtilLog4j.log.info(this, ">>>>> GERENCIA - Aprueba la gerencia responsable de la solicitud.. " + cadenaVo.getIdGerencia());
                    return getResponsableByGerencia(idApCampo, cadenaVo.getIdGerencia()).getIdUsuario();
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, " (X_X)Excepcion al evaluar usuario que aprueba " + e.getMessage());
            return "";
        }
    }

    /**
     * consulta y evalua la proxima cadena de aprobacion joel rodriguez
     * 18/nov/2013
     *
     * @param idSolicitud
     * @param idEstatusActual
     * @return
     */
    @Trace
    private CadenaAprobacionSolicitudVO traerProximaCadenaAprobacion(SgSolicitudViaje  solicitud, int idEstatusActual) {
        UtilLog4j.log.info(this, " estatus actual  " + idEstatusActual);
        final boolean BUSCAR_ESTATUS_IGUALES = true;//funciona como constantes, debe de estan en la clase constantes

        CadenaAprobacionSolicitudVO cadenaVO = null;
        SemaforoVo semaforoActualVo;
        try {
            
            List<CadenaAprobacionSolicitudVO> listCadenas = this.cadenaAprobacionService.traerCadenasAprobacion(solicitud.getSgTipoSolicitudViaje().getId());
            if (listCadenas != null && !listCadenas.isEmpty()) {
                //semaforoActualVo = sgEstadoSemaforoRemote.estadoActual(solicitud.getSgEstadoSemaforo().getSgRutaTerrestre().getId());
                //Si el estatus es pendiente y el solicitante es un gerente
                if (idEstatusActual == Constantes.ESTATUS_PENDIENTE) {
                    // esta solicitando apenas, tomar la primera cadena y retornar
                    cadenaVO = listCadenas.get(0);
                    return evaluarCadenaAprobacionVo(listCadenas, solicitud, cadenaVO.getIdEstatus(), BUSCAR_ESTATUS_IGUALES);
                } else {
                    return evaluarCadenaAprobacionVo(listCadenas, solicitud, idEstatusActual, false);
                }
            } else {                //crear una excepcion
                UtilLog4j.log.info(this, "(X_X) NO EXISTEN CADENAS PARA EL TIPO DE SOLICITUD DE VIAJE ");
                return null;
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "(X_X) Excepcion al buscar la siguiente cadena de aprobacion " + e);
            return null;
        }
    }

    private CadenaAprobacionSolicitudVO evaluarCadenaAprobacionVo(List<CadenaAprobacionSolicitudVO> listCadenas, SgSolicitudViaje solicitud,
            int estatusBuscar, boolean traerCadenaSecuencialmente) {
        CadenaAprobacionSolicitudVO cadenaVO = null;
        boolean activarBusquedaSecuencial = false;
        for (int x = 0; x < listCadenas.size(); x++) {
            if (activarBusquedaSecuencial) {
                estatusBuscar = listCadenas.get(x).getIdEstatus();
            }
            if ((listCadenas.get(x).getIdEstatus()) == estatusBuscar) {
                //if ((cadenaVO.getIdEstatus()) == estatusBuscar) {
                UtilLog4j.log.info(this, "SE ENCONTRO EL ESTATUS");
                if (traerCadenaSecuencialmente) {
                    cadenaVO = listCadenas.get(x);
                } else {//traer la ultima en caso de ser justificada 435
                    if (estatusBuscar == Constantes.ESTATUS_JUSTIFICAR) {
                        cadenaVO = (x <= (listCadenas.size() - 1) ? listCadenas.get(listCadenas.size() - 1) : null);
                    } else {//traer la siguiente
                        cadenaVO = (x <= (listCadenas.size() - 1) ? listCadenas.get(x + 1) : null);
                    }

                }
                //cadenaVO = listCadenas.get(x + 1);
                if (cadenaVO != null) {
                    UtilLog4j.log.info(this, "Iterando la lista de cadenas de aprobación");
                    if (Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE == solicitud.getSgTipoSolicitudViaje().getSgTipoEspecifico().getId()) {
                        if (cadenaVO.isVerificarSemaforoAlterno()) {
                            SemaforoVo semaforoActualVo = sgEstadoSemaforoRemote.estadoActual(solicitud.getSgRutaTerrestre().getId());
                            if (verficarPermisoInsercionEnSemaforoAlterno(cadenaVO.getIdEstatus(), semaforoActualVo.getIdSemaforo())
                                    || validarSemaforoDestinoSolicitud(solicitud)) {
                                //si NO esta en la tabla de Semaforo alterno NO insertar .. seguir buscando
                                UtilLog4j.log.info(this, " ### Se Insertara el estatus para autorizar el Viaje por DG ###");
                                break;
                            } else {
                                if (traerCadenaSecuencialmente) {
                                    //traerElIgual=false;
//                                    UtilLog4j.log.info(this, "Se esta comparando un estatus igual - ya no se sigue buscando pero no tiene permiso de inserccion ");
                                    activarBusquedaSecuencial = true;
                                    //break;
                                } else {
                                    estatusBuscar = cadenaVO.getIdEstatus();
                                    UtilLog4j.log.info(this, "Seguir buscando estatus por que no se encuentra en estatus Alterno ");
                                    continue;
                                }
                            }
                        } else {
                            UtilLog4j.log.info(this, "No verifica semaforo alterno - se procede a insertar la cadena");
                            break;
                        }

                    } else {
                        //  case Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA: {
                        UtilLog4j.log.info(this, "Es cadena aerea - insertar la cadena");
                        break;
                    }
                } else {
                    UtilLog4j.log.info(this, ">>No existen cadena de aprobacion o se han terminado...<<");
                }
            }
        }
        return cadenaVO;
    }

    /**
     * metodo que valida que una solicitud no viole rangos de horarios de
     * semaforos ni fechas en fin de semana joel rodriguez 20/nov/2013
     *
     * @param solicitud
     * @return
     */
    private boolean validarSemaforoDestinoSolicitud(SgSolicitudViaje solicitud) {
        return solicitudNoCumpleRangoHorarios(solicitud);
    }

    private boolean esFinDeSemanaLaFecha(Date fechaCalcular) {
        return siManejoFechaRemote.finSemana(fechaCalcular);
    }

    /*
     * La @param solicitud @return
     */
    private boolean solicitudNoCumpleRangoHorarios(SgSolicitudViaje solicitud) {
//        log("#validar horarios del semaforo para la ruta " + solicitud.getSgEstadoSemaforo().getSgRutaTerrestre().getId());

        if (validarFechaSolicitadaYFechaSalidaSolicitud(solicitud)) {
            log("##VIOLACION## :La solicitud sale mañana y se solicitó despues de las 3:00pm(o el horario de horas anticipadas)");
            return true;
        } else {
            //SemaforoVo semaforoActualVo = sgEstadoSemaforoRemote.estadoActual(solicitud.getSgRutaTerrestre().getId());
//            log(solicitud.getHoraSalida() + "  " + semaforoActualVo.getHoraMinima() + " " + solicitud.getHoraRegreso() + " " + semaforoActualVo.getHoraMaxima());
            if (solicitud.isRedondo()) { //Es redondo valida fecha salida y fecha regreso
                if (solicitud.getHoraSalida().before(solicitud.getSgRutaTerrestre().getHoraMinimaRuta())
                        || solicitud.getHoraRegreso().after(solicitud.getSgRutaTerrestre().getHoraMaximaRuta())) {

                    log("##VIOLACION## :Viola el rango de horario del semaforo actual de la ruta. (es una solicitud de redonda)");

                    return true;
                } else {
                    log("No viola rangos de horarios del semaforo para la solicitud redonda");
                    return false;
                }
            } else {//es un viaje sencillo solo validar la hora de salida
                if (solicitud.getHoraSalida().before(solicitud.getSgRutaTerrestre().getHoraMinimaRuta())) {

                    log("##VIOLACION## :Viola el rango de horario del semaforo actual de la ruta. (es una solicitud sencilla)");
                    return true;
                } else {
                    log("No viola rangos de horarios - para la solicitud sencilla");
                    return false;
                }
            }
        }
    }

    private boolean validarFechaSolicitadaYFechaSalidaSolicitud(SgSolicitudViaje solicitud) {
        log("Comenzando a validar fecha y hora solicitada..");
        //Tomar el estatus 410, es el que tiene la hora exacta en la que se solicito el viaje.
        //solicitado despues de las 3:00pm y el viaje s olicitado sea para el siguiente dia
        Date horaSolicitadaReal = null;
        boolean v = false;
        if (solicitud.getSgMotivoRetraso() != null || buscarJustificacionGerente(solicitud.getId())) {
            UtilLog4j.log.info(this, "La solicitud tiene un motivo de retraso ");
            v = true;
        }
        else {
            v = false;
        }
        return v;
    }

    private boolean buscarJustificacionGerente(int idSolicitud) {
        boolean v;
        JustIncumSolVo justIncumSolVo = sgJustIncumpSolRemote.recuperaJustificacionGerente(idSolicitud);
        if (justIncumSolVo != null) {
            v = true;
        } else {
            v = false;
        }
        return v;
    }

    private String buscarAnalistaParaHacerViajePorOficina(int idOficina) {
        String idUsuario = "";
        UtilLog4j.log.info(this, "##Es la ultima cadena de aprobacion--Buscar analistas de la oficina " + idOficina);
        //buscar en la tabla oficinaAnalista principal
        SgOficinaAnalistaVo vo = this.sgOficinaAnalistaRemote.traerAnalistaPrincipalPorOficina(idOficina);
        if (vo != null) {
            idUsuario = vo.getIdAnalista();
        } else {
            UtilLog4j.log.info(this, "## no existe un analista principal para la oficina " + idOficina);
        }
        UtilLog4j.log.info(this, "El analista " + idUsuario + " de la oficina" + idOficina + " creara el viaje");
        return idUsuario;
    }

    /**
     * Joel Rodriguez Metodo que realiza una consulta al semaforo alterno para
     * verificar si el semaforo actual y el estatus se encuentran ahi..
     *
     * @return
     */
    private boolean verficarPermisoInsercionEnSemaforoAlterno(int idEstatus, int idSemaforo) {
        UtilLog4j.log.info(this, "verficarPermisoInsercionEnSemaforoAlterno " + idEstatus + " semafor " + idSemaforo);
        try {
            return sgEstatusAlternoRemote.verificarSemaforoAlternoYEstatus(idEstatus, idSemaforo);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al vefiricarPermiso de inserccion " + e);
            return false;
        }
    }

    /**
     * Metodo que verifica si un rol aprueba su solicitud - esto es aplicado
     * para los casos de Responsable SGL, Asistente de DG los cuales son los que
     * se autoaprueban Retorna true si el rol, el usuario y quien genero la
     * solicitud de viaje son los mismos
     *
     * @param solicitud
     * @param usuarioSesion
     * @param siRol
     * @return
     */
    private boolean verificarEsRolAutoApruebaSolicitud(String idUsuarioGeneroSolicitud) {
        UtilLog4j.log.info(this, "verificarEsRolAutoApruebaSolicitud");
        try {
//            si algun rol del usuario que creo la solicitud esta en la tabla de roles que se autoAprueban, entonces si se autoaprueba
            return this.sgRolApruebaSolicitudRemote.verificarUsuarioAutoApruebaSolicitudViaje(idUsuarioGeneroSolicitud);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al verificarRolAutoAprueba " + e);
            return false;
        }
    }

    private boolean insertarEstatusAprobacion(SgSolicitudViaje sgSolicitudViaje, CadenaAprobacionSolicitudVO cadenaVo,
            String idUsuarioQueRealizaraAccion, String idUsuarioGenero, boolean isRealizada, boolean isHistorial,
            boolean isTerrestre, int idCampo) throws SIAException, Exception {
        boolean enviado = false;
        boolean insercionExitosa = false;
//        SgSolicitudViaje sgSolicitudViaje = this.sgSolicitudViajeRemote.find(idSolicitudViaje);
        Usuario usuarioRealizaraOperacion = usuarioRemote.find(idUsuarioQueRealizaraAccion);
        SgEstatusAprobacion ea = new SgEstatusAprobacion();

        ea.setHistorial(isHistorial);
        ea.setRealizado(isRealizada);
        if (sgSolicitudViaje.getEstatus().getId() == Constantes.ESTATUS_PARA_HACER_VIAJE && sgSolicitudViaje.isConChofer()) {
            ea.setAutomatico(Constantes.BOOLEAN_TRUE);
        } else {
            ea.setAutomatico(Constantes.BOOLEAN_FALSE);
        }

        if (isRealizada) {
            ea.setAutomatico(Constantes.BOOLEAN_TRUE);
            //controlar el historial de las aprobaciones
            ea.setFechaModifico(new Date());
            ea.setHoraModifico(new Date());
        }
        ea.setUsuario(usuarioRealizaraOperacion);
        ea.setEliminado(Constantes.BOOLEAN_FALSE);
        ea.setGenero(new Usuario(idUsuarioGenero));
        ea.setFechaGenero(new Date());
        ea.setHoraGenero(new Date());
        ea.setSgSolicitudViaje(sgSolicitudViaje);
        ea.setEstatus(this.estatusService.find(cadenaVo.getIdEstatus()));
        if (!ea.isAutomatico()) {
            //si no es automático enviar correo
            enviado = enviarCorreoEstatusAprobacion(ea, cadenaVo, idCampo);
        }

        if (Constantes.ESTATUS_JUSTIFICAR == ea.getEstatus().getId()) {
            ea.setUsuario(null);
        }

        if (sgSolicitudViaje.getSgRutaTerrestre() != null && sgSolicitudViaje.getSgRutaTerrestre().getId() > 0) {
            SemaforoVo vo = this.sgEstadoSemaforoRemote.estadoActual(sgSolicitudViaje.getSgRutaTerrestre().getId());
            ea.setSgEstadoSemaforo(new SgEstadoSemaforo(vo.getIdEstadoSemaforo()));
        }

        if (enviado || ea.isAutomatico()) {
            UtilLog4j.log.info(this, "Se insertará el próximo estatus de aprobación para la solicitud " + sgSolicitudViaje.getId());
            create(ea);
            insercionExitosa = true;
            if (sgSolicitudViaje.getCoNoticia() != null) {
                coNoticiaService.compartirNoticia(sgSolicitudViaje.getCoNoticia().getId(), idUsuarioQueRealizaraAccion, idUsuarioGenero);
                UtilLog4j.log.info(this, "SE COMPARTIO NOTICIA ...");
            }

        } else {
            UtilLog4j.log.info(this, "No procede nada...");
            throw new Exception("Error al enviar el correo...");
        }
        return insercionExitosa;
    }

    private boolean insertarEstatusAprobaciones(SgSolicitudViaje sgSolicitudViaje, SgSolicitudViaje sgSolicitudViaje2, CadenaAprobacionSolicitudVO cadenaVo,
            String idUsuarioQueRealizaraAccion, String idUsuarioQueRealizaraAccion2, String idUsuarioGenero, boolean isRealizada, boolean isHistorial,
            boolean isTerrestre) throws SIAException, Exception {
        boolean enviado = false;
        boolean insercionExitosa = false;
//        SgSolicitudViaje sgSolicitudViaje = this.sgSolicitudViajeRemote.find(idSolicitudViaje);
        Usuario usuarioRealizaraOperacion = new Usuario(idUsuarioQueRealizaraAccion);
        Usuario usuarioRealizaraOperacion2 = new Usuario(idUsuarioQueRealizaraAccion2);
        SgEstatusAprobacion ea = new SgEstatusAprobacion();
        SgEstatusAprobacion ea2 = new SgEstatusAprobacion();
        ea.setHistorial(isHistorial == true ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        ea.setRealizado(isRealizada == true ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        ea.setAutomatico(Constantes.BOOLEAN_FALSE);
        ea2.setHistorial(isHistorial == true ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        ea2.setRealizado(isRealizada == true ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        ea2.setAutomatico(Constantes.BOOLEAN_FALSE);

        if (isRealizada) {
            ea.setAutomatico(Constantes.BOOLEAN_TRUE);
            //controlar el historial de las aprobaciones
            ea.setFechaModifico(new Date());
            ea.setHoraModifico(new Date());

            ea2.setAutomatico(Constantes.BOOLEAN_TRUE);
            //controlar el historial de las aprobaciones
            ea2.setFechaModifico(new Date());
            ea2.setHoraModifico(new Date());
        }

        ea.setUsuario(usuarioRealizaraOperacion);
        ea.setEliminado(Constantes.BOOLEAN_FALSE);
        ea.setGenero(usuarioRealizaraOperacion);
        ea.setFechaGenero(new Date());
        ea.setHoraGenero(new Date());
        ea.setSgSolicitudViaje(sgSolicitudViaje);
        ea.setEstatus(this.estatusService.find(cadenaVo.getIdEstatus()));
        ea2.setUsuario(usuarioRealizaraOperacion2);
        ea2.setEliminado(Constantes.BOOLEAN_FALSE);
        ea2.setGenero(usuarioRealizaraOperacion2);
        ea2.setFechaGenero(new Date());
        ea2.setHoraGenero(new Date());
        ea2.setSgSolicitudViaje(sgSolicitudViaje2);
        ea2.setEstatus(this.estatusService.find(cadenaVo.getIdEstatus()));

        if (!ea.isAutomatico() && !ea2.isAutomatico()) {
            //si no es automático enviar correo
            enviado = enviarCorreoEstatusAprobaciones(ea, ea2, cadenaVo);
        }

        if (Constantes.ESTATUS_JUSTIFICAR == ea.getEstatus().getId() && Constantes.ESTATUS_JUSTIFICAR == ea2.getEstatus().getId()) {
            ea.setUsuario(null);
            ea2.setUsuario(null);
        }

        if (enviado || (ea.isAutomatico()) && ea2.isAutomatico()) {
            UtilLog4j.log.info(this, "Se insertará el próximo estatus de aprobación para la solicitud " + sgSolicitudViaje.getId());
            create(ea);
            create(ea2);
            insercionExitosa = true;
            if (sgSolicitudViaje.getCoNoticia() != null) {
                coNoticiaService.compartirNoticia(sgSolicitudViaje.getCoNoticia().getId(), idUsuarioQueRealizaraAccion, idUsuarioGenero);
                UtilLog4j.log.info(this, "SE COMPARTIO NOTICIA ...");
            }
            if (sgSolicitudViaje2.getCoNoticia() != null) {
                coNoticiaService.compartirNoticia(sgSolicitudViaje2.getCoNoticia().getId(), idUsuarioQueRealizaraAccion2, idUsuarioGenero);
                UtilLog4j.log.info(this, "SE COMPARTIO NOTICIA ...");
            }

        } else {
            UtilLog4j.log.info(this, "No procede nada...");
            throw new Exception("Error al enviar el correo...");
        }
        return insercionExitosa;
    }

    /**
     * Metodo que envia todos los correos del flujo de aprobaciones
     *
     * @param estatusAprobacion
     * @param cadenaVo
     * @return
     */
    private boolean enviarCorreoEstatusAprobacion(SgEstatusAprobacion estatusAprobacion, CadenaAprobacionSolicitudVO cadenaVo ,int idCampo) {
        boolean v = false;
        try {
            SolicitudViajeVO solicitudViaje = sgSolicitudViajeRemote.buscarPorId(estatusAprobacion.getSgSolicitudViaje().getId(), Constantes.NO_ELIMINADO, Constantes.CERO);
            UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(idCampo, solicitudViaje.getIdGerencia());
            List<ViajeroVO> lv = viajeroService.getAllViajerosList(solicitudViaje.getIdSolicitud());
            boolean tipo = (solicitudViaje.getIdSgTipoEspecifico() == 3 ? true : false);
            if (cadenaVo.isUltimaCadena()) {
                if (estatusAprobacion.getUsuario() != null && !estatusAprobacion.getUsuario().getEmail().equals(Constantes.VACIO)) {
                    v = this.notificacionViajeService.sendMailPrepareTravel(estatusAprobacion.getUsuario().getEmail(), 
                            solicitudViaje, cadenaVo.getMensajeAsuntoCorreo(estatusAprobacion.getSgSolicitudViaje().getCodigo(), tipo),
                            usuarioResponsableGerenciaVo.getNombreUsuario(), lv);

                }
            } else {
                UtilLog4j.log.info(this, ">>>>>Enviando correo para aprobar cadena <<<<<"); //aprobar flujo
                v = this.notificacionViajeService.enviarCorreoEstatusSolicitudViajePorAprobar(estatusAprobacion, 
                        cadenaVo.getMensajeAsuntoCorreo(estatusAprobacion.getSgSolicitudViaje().getCodigo(), tipo),
                        solicitudViaje, lv, idCampo);
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al enviar correo de estatusAprobacion " + e);
        }
        return v;
    }

    private boolean enviarCorreoEstatusAprobaciones(SgEstatusAprobacion estatusAprobacion, SgEstatusAprobacion estatusAprobacion2, CadenaAprobacionSolicitudVO cadenaVo) {
        boolean v = false;
        try {
            SolicitudViajeVO solicitudViaje = sgSolicitudViajeRemote.buscarPorId(estatusAprobacion.getSgSolicitudViaje().getId(), Constantes.NO_ELIMINADO, Constantes.CERO);
            SolicitudViajeVO solicitudViaje2 = sgSolicitudViajeRemote.buscarPorId(estatusAprobacion2.getSgSolicitudViaje().getId(), Constantes.NO_ELIMINADO, Constantes.CERO);
            UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, solicitudViaje.getIdGerencia());
            UsuarioResponsableGerenciaVo usuarioResponsableGerenciaVo2 = gerenciaRemote.traerResponsablePorApCampoYGerencia(Constantes.AP_CAMPO_DEFAULT, solicitudViaje2.getIdGerencia());
            List<ViajeroVO> lv = viajeroService.getAllViajerosList(solicitudViaje.getIdSolicitud());
            if (cadenaVo.isUltimaCadena()) {
                if ((estatusAprobacion.getUsuario() != null && !estatusAprobacion.getUsuario().getEmail().equals(Constantes.VACIO))
                        && ((estatusAprobacion2.getUsuario() != null && !estatusAprobacion2.getUsuario().getEmail().equals(Constantes.VACIO)))) {
                    v = this.notificacionViajeService.sendMailPrepareTravel(estatusAprobacion.getUsuario().getEmail(), solicitudViaje, cadenaVo.getMensajeAsuntoCorreo(estatusAprobacion.getSgSolicitudViaje().getCodigo(), false), usuarioResponsableGerenciaVo.getNombreUsuario(), lv);
                    v = this.notificacionViajeService.sendMailPrepareTravel(estatusAprobacion2.getUsuario().getEmail(), solicitudViaje2, cadenaVo.getMensajeAsuntoCorreo(estatusAprobacion2.getSgSolicitudViaje().getCodigo(), false), usuarioResponsableGerenciaVo2.getNombreUsuario(), lv);

                }
            } else {
                UtilLog4j.log.info(this, ">>>>>Enviando correo para aprobar cadena <<<<<"); //aprobar flujo
                v = this.notificacionViajeService.enviarCorreoEstatusSolicitudesViajePorAprobar(estatusAprobacion, estatusAprobacion2, (Constantes.MENSAJE_ESTATUS_SOLICITUDES_VIAJE_APROBAR + estatusAprobacion.getSgSolicitudViaje().getCodigo() + " y " + estatusAprobacion2.getSgSolicitudViaje().getCodigo()), solicitudViaje, solicitudViaje2);
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al enviar correo de estatusAprobacion " + e);
        }
        return v;
    }

    //Insertar el estatus 410, Estatus automatico
    @Trace
    private boolean insertarEstatusAprobacionAutomatico(SgSolicitudViaje sgSolicitudViaje, int idEstatus, String idUsuarioGenero) {
        try {
            SgEstatusAprobacion ea = new SgEstatusAprobacion();
            if (idEstatus == Constantes.ESTATUS_PARA_HACER_VIAJE) {
                ea.setHistorial(Constantes.BOOLEAN_FALSE);
                ea.setRealizado(Constantes.BOOLEAN_FALSE);
                ea.setAutomatico(Constantes.BOOLEAN_FALSE);
            } else {
                ea.setHistorial(Constantes.BOOLEAN_TRUE);
                ea.setRealizado(Constantes.BOOLEAN_TRUE);
                ea.setAutomatico(Constantes.BOOLEAN_TRUE);
            }

            //registro para controlar el historial de las aprobaciones
            ea.setFechaModifico(new Date());
            ea.setHoraModifico(new Date());
            //ea.setUsuario(sgSolicitudViaje.getGenero());
            ea.setUsuario(new Usuario(idUsuarioGenero));
            ea.setEliminado(Constantes.BOOLEAN_FALSE);
            ea.setGenero(new Usuario(idUsuarioGenero));
            ea.setFechaGenero(new Date());
            ea.setHoraGenero(new Date());
            ea.setSgSolicitudViaje(sgSolicitudViaje);
            if (ea.getSgSolicitudViaje().getSgTipoSolicitudViaje().getSgTipoEspecifico().getId() != Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
                ea.setSgEstadoSemaforo(traerEstadoSemaforoActualRuta(ea.getSgSolicitudViaje().getSgRutaTerrestre().getId()));
            }
            ea.setEstatus(estatusService.find(idEstatus));
            if (Constantes.ESTATUS_JUSTIFICAR == ea.getEstatus().getId()) {
                ea.setUsuario(null);
            }
            create(ea);
            UtilLog4j.log.info(this, "SE INSERTO UN ESTATUS AUTOMATICO ...");
            return true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al insertar el estatus automatico " + e);
            return false;
        }
    }

    /*
     * Aprobar la jsutifiacion de una solicitud de viaje
     */
    
    public boolean enviarJustificacionSolicitud(SgEstatusAprobacion estatusAprobacion, String justifiacion, int isLugar, int idInvitado, Date horaReunion, Usuario usuario) {
//        try {
//            //guardar justificacion en Sg_solicitud_viaje
//            SgSolicitudViaje sv = estatusAprobacion.getSgSolicitudViaje();
//            if (this.sgSolicitudViajeRemote.guardarJustificacionSolicitudViaje(estatusAprobacion.getSgSolicitudViaje().getId(), justifiacion, isLugar, idInvitado, horaReunion, usuario.getId())) {
//                //aprobar ..
//                this.aprobarSolicitud(estatusAprobacion, usuario);
//            }
//            return true;
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(this,"Excepcion al aprobar la justifiacion de la solicitud");
//            return false;
//        }
        return false;
    }


    /*
     * Edita el registro de estatusHtml de aprobacion..
     */
    @Trace
    private boolean realizarAprobacion(SgEstatusAprobacion ea, boolean isRealizado, boolean isHistorial, String idUsuario) {
        UtilLog4j.log.info(this, "SgEstatusAprobbacionImpl.realizarAprobacion()");
        try {
            Usuario u = usuarioRemote.find(idUsuario);
            ea.setUsuario(u);
            ea.setRealizado(isRealizado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            ea.setHistorial(isHistorial ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            ea.setModifico(u);
            ea.setFechaModifico(new Date());
            ea.setHoraModifico(new Date());
            if (ea.getSgSolicitudViaje().getSgTipoSolicitudViaje().getSgTipoEspecifico().getId() != Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
                ea.setSgEstadoSemaforo(traerEstadoSemaforoActualRuta(ea.getSgSolicitudViaje().getSgRutaTerrestre().getId()));
            }

            edit(ea);
            UtilLog4j.log.info(this, "Se aprobó el estatus " + ea.getEstatus().getNombre());

            return true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepción al realizar la aprobación " + e);
            UtilLog4j.log.info(this, "" + e);

            return false;
        }
    }

    private SgEstadoSemaforo traerEstadoSemaforoActualRuta(int idRuta) {
        UtilLog4j.log.info(this, "verificar estado actual del semaforo...idSolicitud " + idRuta);
        SemaforoVo vo = this.sgEstadoSemaforoRemote.estadoActual(idRuta);
        return vo != null ? sgEstadoSemaforoRemote.find(vo.getIdEstadoSemaforo()) : null;
    }

    /**
     * Es un metodo auxiliar a realizarAprobacion solo que este se usa para
     * aprobar de forma automatica es usado cuando se cambian semaforos y se
     * tiene que mover solicitudes de un estatus a otro(Aprobar el estatus
     * automaticamente y seguir la cadena)
     *
     * @param ea
     * @param idUsuario
     * @return
     */
    private boolean realizarAprobacionAutomatica(SgEstatusAprobacion ea, String idUsuario) {
        log("Realizar la aprobacion automatica");
        ea.setAutomatico(Constantes.BOOLEAN_TRUE);
        return realizarAprobacion(ea, true, true, idUsuario);
    }

    /**
     * MLUIS 08/11/2013 joel rodriguez Modificacion: anteriormente entraba un
     * objeto como parametro ahora entra un id y se busca
     *
     * @param idEstatusAprobacion
     * @param motivo
     * @param idUsuarioRealizo
     * @param notificar
     * @param vieneServiciosGenerales
     * @return
     * @throws java.lang.Exception
     */
    
    public boolean cancelarSolicitud(int idEstatusAprobacion, String motivo, String idUsuarioRealizo,
            boolean notificar, boolean vieneServiciosGenerales,boolean cancelaViajero) throws Exception {
        SgEstatusAprobacion estatus;
        Usuario u;
        boolean ret = false;
        int solOldEstatus = 0;
        SgEstatusAprobacion oldEstatus;
        try {
            UtilLog4j.log.info(this, "CANCELANDO SOLICITUD  .....");

            estatus = find(idEstatusAprobacion);
            oldEstatus = find(idEstatusAprobacion);
            u = new Usuario(idUsuarioRealizo);
            if (estatus != null && estatus.getId() > 0 && u != null && u.getId() != null && !u.getId().isEmpty()) {
                solOldEstatus = estatus.getSgSolicitudViaje().getEstatus().getId();
                updateStateSolicitudViaje(estatus.getSgSolicitudViaje().getId(), 400, u.getId(),estatus.getSgSolicitudViaje().getHoraSalida());
                estatus.setHistorial(Constantes.BOOLEAN_TRUE);
                estatus.setFechaModifico(new Date());
                estatus.setHoraModifico(new Date());
                estatus.setUsuario(u);
                edit(estatus);
                SolicitudViajeVO solicitudViaje = sgSolicitudViajeRemote.buscarPorId(estatus.getSgSolicitudViaje().getId(), Constantes.NO_ELIMINADO, Constantes.CERO);
                UsuarioResponsableGerenciaVo urgv = gerenciaRemote.traerResponsablePorApCampoYGerencia(1, solicitudViaje.getIdGerencia());

                if (notificacionViajeService.enviarCorreoSolicitudViajeCancelada(estatus, motivo,
                        estatus.getUsuario(), notificar, solicitudViaje, urgv.getNombreUsuario(), cancelaViajero)) {
                    //enviar al log
                    guardarSiMovimiento(estatus.getSgSolicitudViaje().getId(), motivo, siOperacionService.find(3), u.getId());
                    UtilLog4j.log.info(this, "se envio al movimiento");

                    if (vieneServiciosGenerales) {
                        //---CANCELAR LA ESTANCIA SI TIENE
                        List<ViajeroVO> listViajero = viajeroService.getViajerosWithEstanciaBySolicitudViajeList(estatus.getSgSolicitudViaje().getId(), Constantes.BOOLEAN_TRUE);
                        for (ViajeroVO vo : listViajero) {
                            UtilLog4j.log.info(this, "Viajero a termiar " + vo.getId());
                            if (vo.isEstancia() && vo.getSgSolicitudEstancia() != 0) {
                                //Verifica si la solicitud de estancia ya fue enviada
                                SgSolicitudEstanciaVo ssev = sgSolicitudEstanciaRemote.buscarEstanciaPorId(vo.getSgSolicitudEstancia());
                                if (ssev.getIdEstatus() == Constantes.ESTATUS_ASIGNADA) {
                                    //Existen estancias en la solicitud hay que cancelarlas..
                                    viajeroService.lougueOfViajeroCancel(vo.getId(), motivo, u.getId());
                                } else {
                                    sgSolicitudEstanciaRemote.cancelarSolicitudEstanciaAntesSolicitar(u.getId(), vo.getSgSolicitudEstancia(), motivo);
                                }
                            }
                        }
                    }
                    //Publicar Noticia
                    cancelRequestViajeNews(estatus.getSgSolicitudViaje(), motivo, u.getId());
                    ret = true;
                } else {
                    if (!ret && solOldEstatus > 0) {
                        this.edit(oldEstatus);
                        updateStateSolicitudViaje(oldEstatus.getSgSolicitudViaje().getId(), solOldEstatus, u.getId(),estatus.getSgSolicitudViaje().getHoraSalida());
                    }
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error al tratar de cancelar la solicitud de viaje desde panta de aprobacion", e);
            ret = false;
        }
        return ret;
    }

    
    public List<SgEstatusAprobacion> traerEstatusAprobacionPorUsuarioJPA(Usuario usuarioSession, int idEstatus) {
        UtilLog4j.log.info(this, "### traerEstatusAprobacionPorUsuario ###");
        try {
            List<SgEstatusAprobacion> listaRetorno = null;
            listaRetorno = em.createQuery("SELECT s FROM SgEstatusAprobacion s"
                    + " WHERE s.estatus.id = :idEstatus "
                    + " AND s.usuario = :usuarioSesion "
                    + " AND s.realizado = :realizado "
                    + " AND s.historial = :historial "
                    + " AND s.eliminado = :eliminado ").setParameter("idEstatus", idEstatus).setParameter("usuarioSesion", usuarioSession).setParameter("realizado", Constantes.BOOLEAN_FALSE).setParameter("historial", Constantes.BOOLEAN_FALSE).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
            UtilLog4j.log.info(this, "Se encontraon " + (listaRetorno != null ? listaRetorno.size() : 0) + " SgEstatusAprobacion para el Estatus: " + idEstatus);
            return listaRetorno;
        } catch (Exception e) {
            e.printStackTrace();
            UtilLog4j.log.info(this, "excepcion en traer estatus de aprobacion " + e);
            return null;
        }
    }

    
    public SgEstatusAprobacion getSgEstatusAprobacionByEstatusAndSgSolicitudViajeAndUsuario(int idEstatus, int idSgSolicitudViaje, boolean isTerrestre, String idUsuario) {
        UtilLog4j.log.info(this, "SgEstatusAprobacionImpl.getSgEstatusAprobacionByEstatusAndSgSolicitudViajeAndUsuario()");

        SgEstatusAprobacion sgEstatusAprobacion = null;

        UtilLog4j.log.info(this, "idSgSolicitudViaje: " + idSgSolicitudViaje);

        List<EstatusAprobacionVO> list = traerEstatusAprobacionPorUsuario(idUsuario, idEstatus, Constantes.TODAS_SOLICITUDES_VIAJE);

        if (list != null) {
            for (EstatusAprobacionVO vo : list) {
                if (vo.getIdSolicitud() == idSgSolicitudViaje) {
                    sgEstatusAprobacion = find(vo.getId());
                    break;
                }
            }
        }

        return sgEstatusAprobacion;
    }

    /**
     * Trae el estatus de aprobacion por usuario, por estatus y por tipo Joel
     * rodriguez 14/nov/2013
     *
     * @param idUsuario
     * @param idEstatus
     * @param tipoDestino Donde 'OFICINA' trae todas las soliticutes que van a
     * oficinas, 'CIUDAD' - ciudades, 'AEREO' - las solicitudes aereas
     * @return
     *
     * modificacion: se hizo solo un metodo que obtiene el query para
     * solicitudes terrestres Joel Rodriguez 25/nov/2013
     *
     */
    
    public List<EstatusAprobacionVO> traerEstatusAprobacionPorUsuario(String idUsuario, int idEstatus, int tipoDestino) {
        UtilLog4j.log.info(this, "### traerEstatusAprobacionPorUsuario ###");
        List<Object[]> lo = null;
        Query q;
        try {
            switch (tipoDestino) {
                case Constantes.TODAS_SOLICITUDES_VIAJE:
                    lo = em.createNativeQuery(getQueryEstatusTerrestre(idUsuario, idEstatus, Constantes.SOLICITUDES_TERRESTRE_OFICINA)).getResultList();
                    lo.addAll(em.createNativeQuery(getQueryEstatusTerrestre(idUsuario, idEstatus, Constantes.SOLICITUDES_TERRESTRE_CIUDAD)).getResultList());
                    //lo.addAll(em.createNativeQuery(getQueryEstatusTerrestre(idUsuario, idEstatus, Constantes.SOLICITUDES_TERRESTRE_LUGAR)).getResultList());
                    lo.addAll(em.createNativeQuery(getQueryEstatusAereo(idUsuario, idEstatus)).getResultList());
                    break;
                case Constantes.SOLICITUDES_TERRESTRE_OFICINA:
                    lo = em.createNativeQuery(getQueryEstatusTerrestre(idUsuario, idEstatus, Constantes.SOLICITUDES_TERRESTRE_OFICINA)).getResultList();
                    break;
                case Constantes.SOLICITUDES_TERRESTRE_CIUDAD:
                    lo = em.createNativeQuery(getQueryEstatusTerrestre(idUsuario, idEstatus, Constantes.SOLICITUDES_TERRESTRE_CIUDAD)).getResultList();
                    break;
                /*
	     * case Constantes.SOLICITUDES_TERRESTRE_LUGAR: lo =
	     * em.createNativeQuery(getQueryEstatusTerrestre(idUsuario,
	     * idEstatus,
	     * Constantes.SOLICITUDES_TERRESTRE_LUGAR)).getResultList();
	     * break;
                 */
                case Constantes.SOLICITUDES_AEREA:
                    lo = em.createNativeQuery(getQueryEstatusAereo(idUsuario, idEstatus)).getResultList();
                    break;
                case Constantes.SOLICITUDES_TERRESTRES:
                    lo = em.createNativeQuery(getQueryEstatusTerrestre(idUsuario, idEstatus, Constantes.SOLICITUDES_TERRESTRE_OFICINA)).getResultList();
                    lo.addAll(em.createNativeQuery(getQueryEstatusTerrestre(idUsuario, idEstatus, Constantes.SOLICITUDES_TERRESTRE_CIUDAD)).getResultList());
                    break;
            }
            //castear
            UtilLog4j.log.info(this, "<<<<<< Query Solicitudes >>>>" + getStringQuery());
            List<EstatusAprobacionVO> le = new ArrayList<EstatusAprobacionVO>();
            for (Object[] objects : lo) {
                le.add(castReturnEstatusAprobacionVO(objects));
            }
            //
            return le;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion en traer estatus de aprobacion " + e);
            return null;
        }
    }

    /**
     * Joel rodriguez Actualizacion : 27.02.2014
     *
     * la seleccion se realiza por fecha = hoy y hora despues de la
     * operacion_cambio_semaforo O fecha = a mañana esto para afectar a
     * solicitudes con hora de salida despues de la hora que se cambia el
     * semaforo
     *
     * @param idUsuario
     * @param idEstatus
     * @param idRuta
     * @return
     */
    
    public List<EstatusAprobacionVO> traerEstatusAprobacionPorUsuarioYRuta(String idUsuario, int idEstatus, int idRuta) {
        log("traerEstatusAprobacionPorUsuarioYRuta " + idEstatus + " ruta " + idRuta);
        List<EstatusAprobacionVO> le = Collections.EMPTY_LIST;
        List<Object[]> lo = null;
        try {
            //modificar query para que valla a todos los estatus  entre 415 y 450
            clearQuery();
            appendQuery(" select estatus.id,");
            appendQuery(" sol.ID,");
            appendQuery(" sol.CODIGO,");
            appendQuery(" sol.FECHA_SALIDA,");
            appendQuery(" sol.HORA_SALIDA, ");
            appendQuery(" sol.FECHA_GENERO, ");
            appendQuery(" sol.HORA_GENERO ,");
            appendQuery(" sol.FECHA_REGRESO,");
            appendQuery(" sol.HORA_REGRESO,");

            appendQuery(" (select 'True' ");
            appendQuery(" from SG_ESTATUS_APROBACION e ");
            appendQuery(" where e.ESTATUS = ").append(Constantes.ESTATUS_JUSTIFICAR);
            appendQuery(" and e.SG_SOLICITUD_VIAJE = sol.id");
            appendQuery(" and e.HISTORIAL = 'True' ");
            appendQuery(" and e.REALIZADO = 'True' ");
            appendQuery(" and e.ELIMINADO = 'False'");
            appendQuery(" and e.AUTOMATICO = 'False') as autorizado_dg");

            appendQuery(" from SG_SOLICITUD_VIAJE sol,");
            appendQuery(" SG_ESTADO_SEMAFORO es,");
            appendQuery(" SG_ESTATUS_APROBACION estatus");
            switch (idEstatus) {
                case Constantes.INDICE_CERO: //obtener todos los registros
                    appendQuery(" where estatus.ESTATUS between 410 and 450 ");
                    appendQuery(" and es.SG_RUTA_TERRESTRE = ");
                    appendQuery(idRuta);
                    break;
                default:
                    appendQuery(" where estatus.ESTATUS =  ");
                    appendQuery(idEstatus);
                    appendQuery(" and es.SG_RUTA_TERRESTRE = ");
                    appendQuery(idRuta);
                    appendQuery(" and ESTATUS.USUARIO = ");
                    appendQuery("'");
                    appendQuery(idUsuario);
                    appendQuery("'");
                    break;
            }

            appendQuery(" and estatus.SG_SOLICITUD_VIAJE = sol.id");
            appendQuery(" and sol.SG_ESTADO_SEMAFORO = es.id");
            appendQuery(" and estatus.HISTORIAL = 'False' ");
            appendQuery(" and estatus.REALIZADO = 'False' ");
            appendQuery(" and estatus.ELIMINADO = 'False'");

            appendQuery(" and ((sol.fecha_salida = cast('now' as date) ");
            appendQuery(" and sol.HORA_salida > cast('now' as time))");
            appendQuery(" or (sol.fecha_salida = (SELECT CURRENT_DATE - 1)))  ");
            appendQuery(" and EXTRACT(WEEKDAY FROM sol.FECHA_SALIDA) not in (0,6)");// no selecciona las solicitudes con fecha salida en fin de semana

            UtilLog4j.log.info(this, "Query  >>>> " + getStringQuery());
            lo = em.createNativeQuery(getStringQuery()).getResultList();

            if (lo != null && !lo.isEmpty()) {
                le = new ArrayList<EstatusAprobacionVO>();
                for (Object[] objects : lo) {
                    //---Solo obtener las solicitudes que no han sido aprobadas por el jefe
                    EstatusAprobacionVO vo = castReturnEstatusAprobacionYRutaVO(objects);
                    if (!vo.isSolicitudAutorizada()) {
                        le.add(vo);
                    }
                }
                UtilLog4j.log.info(this, "Se encontraron " + (lo.size() > 0 ? lo.size() : " NO EXISTIERON "));
            }
            return le;
        } catch (Exception e) {
            log("Excepcion al traerEstatusAprobacionPorUaurioYRuta" + e);
            return null;
        }

    }

//
//
    private EstatusAprobacionVO castReturnEstatusAprobacionYRutaVO(Object[] obj) {
        EstatusAprobacionVO estatusAprobacionVO;
        estatusAprobacionVO = new EstatusAprobacionVO();
        estatusAprobacionVO.setId((Integer) obj[0]);
        estatusAprobacionVO.setIdSolicitud((Integer) obj[1]);
        estatusAprobacionVO.setCodigo((String) obj[2]);
        estatusAprobacionVO.setFechaSalida((Date) obj[3]);
        estatusAprobacionVO.setHoraSalida((Date) obj[4]);
        estatusAprobacionVO.setFechaGenero(obj[5] != null ? (Date) obj[5] : null);
        estatusAprobacionVO.setHoraGenero(obj[6] != null ? (Date) obj[6] : null);

        estatusAprobacionVO.setFechaRegreso(obj[7] != null ? (Date) obj[7] : null);
        estatusAprobacionVO.setHoraRegreso(obj[8] != null ? (Date) obj[8] : null);
        estatusAprobacionVO.setSolicitudAutorizada(obj[9] != null ? true : false);

        return estatusAprobacionVO;
    }

    /**
     * Metodo para dar el total de estatus aprobacion por usuario
     *
     * @param idUsuario
     * @param estatus
     * @return Integer
     */
    
    public Integer getTotalEstatusAprobacionByUsuario(String idUsuario, int estatus) {
        clearQuery();
        query.append("SELECT COUNT(ea.USUARIO) FROM SG_ESTATUS_APROBACION ea ");
        query.append(" WHERE ea.REALIZADO = 'False' ");
        query.append(" AND ea.ESTATUS = '").append(estatus).append("' ");
        query.append(" AND ea.USUARIO = '").append(idUsuario).append("' ");

        Integer total = (Integer) em.createNativeQuery(query.toString()).getSingleResult();

        return total;
    }

    
    public int getTotalSolicitudesPorEstatusDestino(String idUsuario, int idEstatus, int tipoDestino) {
        UtilLog4j.log.info(this, "getTotalSolicitudesPorEstatusDestino");
        Query q;
        try {
            clearQuery();
            appendQuery(" Select  count(*)");

            switch (tipoDestino) {
                case Constantes.SOLICITUDES_TERRESTRE_OFICINA:
                    agregarComplementoQueryEstatusTerrestreOficina(idUsuario, idEstatus);
                    break;
                case Constantes.SOLICITUDES_TERRESTRE_CIUDAD:
                    agregarComplementoQueryEstatusTerrestreCiudad(idUsuario, idEstatus); // es aciudad
                    break;
                /*
	     * case Constantes.SOLICITUDES_TERRESTRE_LUGAR:
	     * agregarComplementoQueryEstatusTerrestreLugar(idUsuario,
	     * idEstatus); break;
                 */
                case Constantes.SOLICITUDES_AEREA:
                    agregarComplementoQueryEstatusAereo(idUsuario, idEstatus);
                    break;
            }
            q = em.createNativeQuery(getStringQuery());

            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepción en getTotalSolicitudesPorAprobar.." + e);
            return 0;
        }
    }

    /**
     *
     * Joel rodriguez 25/nov/2013 Metodo que arma el query para solicitudes de
     * viajes terrestres de oficina, ciudad y lugares cercanos
     *
     * @param idUsuario
     * @param idEstatus
     * @param lugarDestino
     * @return
     */
    private String getQueryEstatusTerrestre(String idUsuario, int idEstatus, int lugarDestino) {
        String seleccionComodin = "", condicionComodin = "";
        clearQuery();
        switch (lugarDestino) {
            case Constantes.SOLICITUDES_TERRESTRE_OFICINA:
                seleccionComodin = " ofiDestino.nombre as oficina_destino,";//5
                condicionComodin = agregarComplementoQueryEstatusTerrestreOficina(idUsuario, idEstatus);
                break;
            case Constantes.SOLICITUDES_TERRESTRE_CIUDAD:
                seleccionComodin = " ciu.nombre,";
                condicionComodin = agregarComplementoQueryEstatusTerrestreCiudad(idUsuario, idEstatus);
                break;

        }

        clearQuery();
        appendQuery(" SELECT ea.id AS idEstatusAprobacion,");//1
        appendQuery(" sol.codigo AS codigo_Solicitud,");//2
        appendQuery("  'TERRESTRE',");//3
        appendQuery("  ofiOrigen.nombre AS oficina_origen,");//4

        appendQuery(seleccionComodin);//5

        appendQuery("  sol.fecha_salida AS fecha_salida,");//6
        appendQuery("  sol.hora_salida AS hora_salida,");//7
        appendQuery("  sol.fecha_regreso AS fecha_regreso,");//8
        appendQuery("  sol.hora_regreso AS hora_regreso, ");//9
        appendQuery("  case when sol.sg_motivo is not null then");
        appendQuery(" (select mo.nombre from SG_MOTIVO mo where mo.ID = sol.SG_MOTIVO)");
        appendQuery(" else ''");
        appendQuery(" end,");//10
        appendQuery(" es.nombre AS nombre_estatus,");//11
        appendQuery(" g.nombre AS nombreGerencia,");//12
        appendQuery(" sol.observacion, ");//13
        appendQuery(" sol.id, ");//14
        appendQuery(" tip.id, ");//15
        appendQuery(" (Select count(id) from sg_Viajero where SG_SOLICITUD_VIAJE = sol.id AND eliminado = 'False')  as Viajeros, ");//16
        appendQuery(" CASE WHEN sol.sg_motivo_retraso is null then '' else (select smr.Justificacion_retraso from SG_MOTIVO_RETRASO smr where smr.ID=sol.sg_motivo_retraso) end,");//17
        appendQuery(" sol.redondo,");//18

//	appendQuery(" (select sem.COLOR");
//	appendQuery("    from SG_ESTADO_SEMAFORO es, SG_SEMAFORO sem");
//	appendQuery("   where es.SG_RUTA_TERRESTRE =(select est.SG_RUTA_TERRESTRE from SG_ESTADO_SEMAFORO est where est.ID = sol.SG_ESTADO_SEMAFORO)");
//	appendQuery("      and es.ACTUAL = 'True'");
//	appendQuery("      and es.ELIMINADO = 'False'");
//	appendQuery("      and es.SG_SEMAFORO = sem.id) as color_semaforo_actual,");//19
        appendQuery(" (select s.COLOR ");
        appendQuery(" from SG_SEMAFORO s ");
        appendQuery(" where s.ID = ( ");
        appendQuery(" SELECT xx ");
        appendQuery(" from ( ");
        appendQuery(" select (SELECT FIRST 1 ar.SG_SEMAFORO ");
        appendQuery(" 				FROM SG_ESTADO_SEMAFORO ar   ");
        appendQuery(" 				where ar.ELIMINADO = 'False'  	 ");
        appendQuery(" 				and ar.GR_MAPA = rz.GR_MAPA ");
        appendQuery(" 				ORDER BY ar.ID DESC) as xx ");
        appendQuery(" from GR_RUTAS_ZONAS rz   ");
        appendQuery(" where rz.SG_RUTA_TERRESTRE = sol.SG_RUTA_TERRESTRE ");
        appendQuery(" and rz.ELIMINADO = 'False' ");
        appendQuery(" order by rz.SECUENCIA LIMIT 1) as xxx ");
        appendQuery(" group by xx ");
        appendQuery(" order by xx desc)), ");//19

        appendQuery(" 0 ,");//20
        appendQuery(" sol.genero, ");//21
        appendQuery(" ea.estatus, ");//22
        appendQuery(" tip.sg_tipo_especifico,");//23

//        appendQuery(" (select es.JUSTIFICACION");
//        appendQuery("         from SG_ESTADO_SEMAFORO es");
//        appendQuery("     where es.SG_RUTA_TERRESTRE =(select est.SG_RUTA_TERRESTRE from SG_ESTADO_SEMAFORO est where est.ID = sol.SG_ESTADO_SEMAFORO)");
//        appendQuery("         and es.ACTUAL = 'True'");
//        appendQuery("         and es.ELIMINADO = 'False'");
//        appendQuery(" ) as justificacion_semaforo,");//23
        appendQuery(" (select sem.DESCRIPCION");
        appendQuery("    from SG_ESTADO_SEMAFORO es, SG_SEMAFORO sem");
        appendQuery("   where es.SG_RUTA_TERRESTRE =(select est.SG_RUTA_TERRESTRE from SG_ESTADO_SEMAFORO est where est.ID = sol.SG_ESTADO_SEMAFORO)");
        appendQuery("      and es.ACTUAL = 'True'");
        appendQuery("      and es.ELIMINADO = 'False'");
        appendQuery("      and es.SG_SEMAFORO = sem.id) as descripcion_semaforo_actual,");//24

        appendQuery(" case ");
        appendQuery("    when sol.sg_motivo_retraso is null then '' ");
        appendQuery("    else ");
        appendQuery("       (Select te.nombre from sg_motivo_retraso mr, sg_tipo_especifico te ");
        appendQuery("    where sol.sg_motivo_retraso = mr.id and mr.sg_tipo_especifico = te.id ) end,");//25

        appendQuery(" (select sem.HORA_MINIMA ");
        appendQuery("                     from SG_ESTADO_SEMAFORO es, SG_SEMAFORO sem   ");
        appendQuery("                     where es.SG_RUTA_TERRESTRE =(select est.SG_RUTA_TERRESTRE ");
        appendQuery("                                               from SG_ESTADO_SEMAFORO est ");
        appendQuery("                                               where est.ID = sol.SG_ESTADO_SEMAFORO)");
        appendQuery("       and es.ACTUAL = 'True'");
        appendQuery("       and es.ELIMINADO = 'False'");
        appendQuery("       and es.SG_SEMAFORO = sem.id) as hora_minima_actual_semaforo, ");//26

        appendQuery(" (select sem.HORA_MAXIMA ");
        appendQuery("                     from SG_ESTADO_SEMAFORO es, SG_SEMAFORO sem   ");
        appendQuery("                     where es.SG_RUTA_TERRESTRE =(select est.SG_RUTA_TERRESTRE ");
        appendQuery("                                               from SG_ESTADO_SEMAFORO est ");
        appendQuery("                                               where est.ID = sol.SG_ESTADO_SEMAFORO)");
        appendQuery("       and es.ACTUAL = 'True'");
        appendQuery("       and es.ELIMINADO = 'False'");
        appendQuery("       and es.SG_SEMAFORO = sem.id) as hora_minima_actual_semaforo ");//27
//agergaciones

//
        appendQuery(condicionComodin);
        appendQuery(" order by sol.codigo DESC");
        return getStringQuery();
    }

    private String agregarComplementoQueryEstatusTerrestreOficina(String idUsuario, int idEstatus) {
        appendQuery(" FROM sg_solicitud_viaje sol "
                + " inner join sg_Estatus_Aprobacion ea on ea.sg_solicitud_viaje = sol.id AND ea.estatus = sol.estatus and ea.ELIMINADO ='False' "
                + " inner join sg_tipo_solicitud_viaje tip on sol.sg_tipo_solicitud_viaje = tip.id and tip.ELIMINADO = 'False' "
                + " inner join estatus es on ea.estatus = es.id and es.ELIMINADO = 'False' "
                + " inner join sg_oficina ofiOrigen on sol.oficina_origen = ofiOrigen.id and ofiOrigen.ELIMINADO = 'False' "
                + " inner join gerencia g  on sol.gerencia_responsable = g.id  and g.ELIMINADO = 'False' "
                + " inner join sg_oficina ofiDestino on sol.OFICINA_DESTINO = ofiDestino.id and ofiDestino.ELIMINADO = 'False' "
                + " WHERE ea.estatus = ").append(idEstatus).append(
                " AND tip.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
        if (idEstatus != Constantes.ESTATUS_JUSTIFICAR) {
            if (idUsuario != null && !idUsuario.isEmpty()) {
                appendQuery(" AND ea.USUARIO = '").append(idUsuario).append("'");
            } else {
                appendQuery(" AND ea.USUARIO is null");
            }
        } else {
            appendQuery(" AND ea.USUARIO is null");
        }
        appendQuery(" AND ea.realizado = 'False'"
                + " AND ea.historial = 'False'"
                + " AND sol.eliminado = 'False'");
        //
        return getStringQuery();
    }

    private String agregarComplementoQueryEstatusTerrestreCiudad(String idUsuario, int idEstatus) {
        appendQuery("  FROM sg_solicitud_viaje sol "
                + "  inner join SG_RUTA_TERRESTRE r on r.id = sol.SG_RUTA_TERRESTRE and r.eliminado = 'False' "
                + "  inner join SG_DETALLE_RUTA_CIUDAD dr on dr.SG_RUTA_TERRESTRE = r.id and dr.eliminado = 'False' "
                + "  inner join si_ciudad ciu on dr.SI_CIUDAD = ciu.id and ciu.eliminado = 'False' "
                + "  inner join sg_Estatus_Aprobacion ea on ea.sg_solicitud_viaje = sol.id and ea.estatus = sol.estatus and ea.eliminado = 'False' "
                + "  inner join sg_tipo_solicitud_viaje tip on sol.sg_tipo_solicitud_viaje = tip.id and tip.eliminado = 'False' "
                + "  inner join estatus es on ea.estatus = es.id and es.eliminado = 'False' "
                + "  inner join sg_oficina ofiOrigen on sol.oficina_origen = ofiOrigen.id and ofiOrigen.eliminado = 'False' "
                + "  inner join gerencia g on sol.gerencia_responsable = g.id and g.eliminado = 'False' "
                + " WHERE ea.estatus = ").append(idEstatus).append(
                " AND tip.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
        if (idEstatus != Constantes.ESTATUS_JUSTIFICAR) {
            if (idUsuario != null && !idUsuario.isEmpty()) {
                appendQuery(" AND ea.USUARIO = '").append(idUsuario).append("'");
            } else {
                appendQuery(" AND ea.USUARIO is null");
            }
        } else {
            appendQuery(" AND ea.USUARIO is null");
        }
        appendQuery(" AND ea.realizado = 'False'"
                + " AND ea.historial = 'False'"
                + " AND sol.eliminado = 'False'");
        //
        return getStringQuery();

    }

    private String agregarComplementoQueryEstatusTerrestreLugar(String idUsuario, int idEstatus) {
        appendQuery("  FROM sg_solicitud_viaje sol, ");
        appendQuery("  SG_VIAJE_LUGAR viaje_lugar,");
        appendQuery("  SG_LUGAR lugar,");
        appendQuery("  sg_Estatus_Aprobacion ea,");
        appendQuery("  sg_tipo_solicitud_viaje tip,");
        appendQuery("  estatus es,");
        appendQuery("  sg_oficina ofiOrigen,");
        appendQuery("  gerencia g ");

        appendQuery("  WHERE ea.estatus = ").append(idEstatus);
        appendQuery("  AND tip.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_TERRESTRE);
        appendQuery("  AND ea.USUARIO = '").append(idUsuario).append("'");
        appendQuery("  AND ea.realizado = 'False'");
        appendQuery("  AND ea.historial = 'False'");
        appendQuery("  and ea.ELIMINADO ='False'");
        appendQuery("  AND sol.eliminado = 'False'");
        appendQuery("  and viaje_lugar.SG_SOLICITUD_VIAJE = sol.ID ");
        appendQuery("  and viaje_lugar.SG_LUGAR= lugar.id ");
        appendQuery("  AND ea.sg_solicitud_viaje = sol.id");
        appendQuery("  AND sol.sg_tipo_solicitud_viaje = tip.id");
        appendQuery(" AND ea.estatus = sol.estatus");

        appendQuery("  AND viaje_lugar.SG_LUGAR= lugar.id");
        appendQuery("  AND sol.OFICINA_ORIGEN = ofiOrigen.id");
        appendQuery("  AND ea.estatus = es.id");
        appendQuery("  AND sol.gerencia_responsable = g.id ");
        return getStringQuery();
    }

    private String getQueryEstatusAereo(String idUsuario, int idEstatus) {
        clearQuery();
        try {
            appendQuery(" SELECT  ea.id AS idEstatusAprobacion,"); //0
            appendQuery(" sol.codigo AS codigo_Solicitud,");//1
            appendQuery(" tip.nombre AS tipo_Solicitud,");//2
            appendQuery(" ciuOrigen.NOMBRE AS origen,");//3
            appendQuery(" ciuDestino.NOMBRE AS destino,");//4
            appendQuery(" sol.fecha_salida AS fecha_salida,");//5
            appendQuery(" sol.hora_salida AS hora_salida,");//6
            appendQuery(" sol.fecha_regreso AS fecha_regreso,");//7
            appendQuery(" sol.hora_regreso AS hora_regreso,");//8
            appendQuery(" case when sol.sg_motivo is not null then");
            appendQuery(" (select mo.nombre from SG_MOTIVO mo where mo.ID = sol.SG_MOTIVO)");
            appendQuery(" else ''");
            appendQuery(" end,");//9
            appendQuery(" es.nombre AS nombre_estatus,");//10
            appendQuery(" g.nombre AS nombreGerencia,");//11
            appendQuery(" sol.observacion, ");//12
            appendQuery(" sol.id, "); // 13
            appendQuery(" tip.id, "); // 14
            appendQuery(" (Select count(id) from sg_Viajero where SG_SOLICITUD_VIAJE = sol.id AND eliminado = 'False')  as Viajeros, "); //15
            appendQuery(" '', ");//15
            appendQuery(" sol.redondo,");//16
            appendQuery(" NULL, ");//17
            appendQuery(" (select count(det.id) ");//18
            appendQuery("  from SG_DETALLE_ITINERARIO det ");
            appendQuery(" where  det.SG_ITINERARIO = it.ID");
            appendQuery(" and det.HISTORIAL = 'False'");
            appendQuery("  and det.ELIMINADO = 'False'),");
            appendQuery("  sol.genero, ");//19
            appendQuery("  sol.estatus ,");//20
            appendQuery("  tip.sg_tipo_especifico, ");//21
            appendQuery("  NULL,");//22
            appendQuery("  NULL,");//23
            appendQuery("  NULL,");//24
            appendQuery("  NULL, ");//25
            appendQuery("  NULL");//26
            agregarComplementoQueryEstatusAereo(idUsuario, idEstatus);
            appendQuery(" order by sol.codigo DESC");

            return getStringQuery();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion al armat query aereo " + e);
            return "";
        }
    }

    //Se puso en otro metodo por que tambien es utilizado en el query qye retorna el contador
    /**
     * Modifico : NLopez 24/10/2013
     *
     * @param idUsuario
     * @param idEstatus
     */
    private void agregarComplementoQueryEstatusAereo(String idUsuario, int idEstatus) {
        appendQuery(" FROM sg_solicitud_viaje sol,");
        appendQuery(" sg_Estatus_Aprobacion ea,");
        appendQuery(" sg_tipo_solicitud_viaje tip,");
        appendQuery(" estatus es,");
        appendQuery(" sg_itinerario it,");
        appendQuery(" si_ciudad ciuOrigen,");
        appendQuery(" si_ciudad ciuDestino,");
        appendQuery(" gerencia g ");

        if (idEstatus == Constantes.QUERY_TRAER_CAMBIO_ITINERARIO) {
            appendQuery(", sg_cambio_itinerario cit");
        }

        appendQuery(" WHERE ");
        if (idEstatus == Constantes.QUERY_AEREO_CAMBIO_ITINERARIO) {
            appendQuery(" (ea.estatus between ").append(Constantes.ESTATUS_PENDIENTE).append(" AND ").append(Constantes.ESTATUS_PARA_HACER_VIAJE).append(" ) ");
            appendQuery(" AND it.id in (select dit.SG_ITINERARIO from SG_DETALLE_ITINERARIO dit where dit.sg_itinerario = it.id)  ");
            appendQuery(" AND sol.genero = '").append(idUsuario).append("'");
        }

        if (idEstatus == Constantes.QUERY_TRAER_CAMBIO_ITINERARIO) {
            appendQuery(" it.id = cit.sg_itinerario ");
            appendQuery(" AND cit.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND cit.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
        }

        if (idEstatus != Constantes.QUERY_TRAER_CAMBIO_ITINERARIO && idEstatus != Constantes.QUERY_AEREO_CAMBIO_ITINERARIO) {
            appendQuery(" ea.estatus = ").append(idEstatus);
            appendQuery(" AND it.ida = '").append(Constantes.BOOLEAN_TRUE).append("'");
            appendQuery(" AND ea.USUARIO = '").append(idUsuario).append("'");

        }
        appendQuery(" AND ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery(" AND ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery(" AND sol.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery(" and sol.fecha_salida >= current_date");
        appendQuery(" AND ea.eliminado = 'False'");
        appendQuery(" AND ea.sg_solicitud_viaje = sol.id");
        appendQuery(" AND sol.sg_tipo_solicitud_viaje = tip.id");
        appendQuery(" AND it.sg_solicitud_viaje = sol.id");
        appendQuery(" AND it.SI_CIUDAD_ORIGEN = ciuOrigen.ID ");
        appendQuery(" AND it.SI_CIUDAD_DESTINO = ciuDestino.ID  ");
        appendQuery(" AND ea.estatus = es.id ");
        appendQuery(" AND sol.gerencia_responsable = g.id");
    }

//
    ////Nuevo metodo para recuperar las solicitudes
    
    public List<EstatusAprobacionVO> traerSolicitudesSeguridad(int idEstatus, boolean isTerrestre) {
        UtilLog4j.log.info(this, "### traerEstatusAprobacionPorUsuario ###");
        UtilLog4j.log.info(this, "Estatus " + idEstatus);
        UtilLog4j.log.info(this, "terrestre " + isTerrestre);
        String qAereo = "";
        String qTerrestre = "";
        Query q;
        try {
            qTerrestre = "SELECT ea.id AS idEstatusAprobacion," // 0id solo
                    + "sol.codigo AS codigo_Solicitud," // 1
                    + "tip.nombre AS tipo_Solicitud," // 2
                    + "ofiOrigen.nombre AS oficina_origen," // 3
                    + "ofiDestino.nombre AS oficina_destino," // 4
                    + "sol.fecha_salida AS fecha_salida," // 5
                    + "sol.hora_salida AS hora_salida," // 6
                    + "sol.fecha_regreso AS fecha_regreso," // 7
                    + "sol.hora_regreso AS hora_regreso," // 8
                    + "mo.nombre AS nombre_motivo," // 9
                    + "es.nombre AS nombre_estatus,"//10
                    + "g.nombre AS nombreGerencia," // 11
                    + "sol.observacion, " // 12
                    + "sol.id, " // 13
                    + "tip.id, " // 14
                    + "(Select count(id) from sg_Viajero where SG_SOLICITUD_VIAJE = sol.id AND eliminado = 'False')  as Viajeros, "//15
                    + " ''" // 16
                    + " FROM sg_solicitud_viaje sol,"//17
                    + "sg_Estatus_Aprobacion ea,"
                    + "sg_motivo mo,"
                    + "sg_tipo_solicitud_viaje tip,"
                    + "estatus es,"
                    + "sg_oficina ofiOrigen,"
                    + "sg_oficina ofiDestino,"
                    + "gerencia g"
                    + " WHERE ea.estatus = " + idEstatus
                    + " AND ea.realizado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND ea.historial = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND sol.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND ea.sg_solicitud_viaje = sol.id"
                    + " AND sol.sg_motivo = mo.id"
                    + " AND sol.sg_tipo_solicitud_viaje = tip.id"
                    + " AND sol.oficina_origen = ofiOrigen.id"
                    + " AND sol.oficina_destino = ofiDestino.id"
                    + " AND ea.estatus = es.id"
                    + " AND sol.gerencia_responsable = g.id "
                    + " order by sol.codigo DESC";

            qAereo = "SELECT  ea.id AS idEstatusAprobacion," //0
                    + " sol.codigo AS codigo_Solicitud,"//1
                    + " tip.nombre AS tipo_Solicitud,"//2
                    + " ciuOrigen.NOMBRE AS origen,"//3
                    + " ciuDestino.NOMBRE AS destino,"//4
                    + " sol.fecha_salida AS fecha_salida,"//5
                    + " sol.hora_salida AS hora_salida,"//6
                    + " sol.fecha_regreso AS fecha_regreso,"//7
                    + " sol.hora_regreso AS hora_regreso,"//8
                    + " mo.nombre AS nombre_motivo,"//9
                    + "es.nombre AS nombre_estatus,"//10
                    + "g.nombre AS nombreGerencia,"//11
                    + "sol.observacion, "//12
                    + "sol.id, " // 13
                    + "tip.id, " // 14
                    + "(Select count(id) from sg_Viajero where SG_SOLICITUD_VIAJE = sol.id AND eliminado = 'False')  as Viajeros " //15
                    + " ''" // 16
                    + " FROM sg_solicitud_viaje sol,"
                    + " sg_Estatus_Aprobacion ea,"
                    + " sg_motivo mo,"
                    + " sg_tipo_solicitud_viaje tip,"
                    + " estatus es,"
                    + " sg_itinerario it,"
                    + " si_ciudad ciuOrigen,"
                    + " si_ciudad ciuDestino,"
                    + " gerencia g"
                    + " WHERE ea.estatus = " + idEstatus
                    + " AND ea.realizado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND ea.historial = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND sol.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND ea.sg_solicitud_viaje = sol.id"
                    + " AND sol.sg_motivo = mo.id"
                    + " AND sol.sg_tipo_solicitud_viaje = tip.id"
                    + " AND it.ida = '" + Constantes.BOOLEAN_TRUE + "'"
                    + " AND it.sg_solicitud_viaje = sol.id"
                    + " AND it.SI_CIUDAD_ORIGEN = ciuOrigen.ID "
                    + " AND it.SI_CIUDAD_DESTINO = ciuDestino.ID  "
                    + " AND ea.estatus = es.id "
                    + " AND sol.gerencia_responsable = g.id"
                    + " order by sol.codigo DESC";
            List<EstatusAprobacionVO> le = new ArrayList<EstatusAprobacionVO>();
            if (isTerrestre) {
                UtilLog4j.log.info(this, "Qterrestre " + qTerrestre);
                List<Object[]> lo = em.createNativeQuery(qTerrestre).getResultList();
                for (Object[] objects : lo) {
//                    le.add(castReturnEstatusAprobacionVO(objects));
                }
            } else {
                UtilLog4j.log.info(this, "Qterrestre " + qAereo);
                List<Object[]> lo = em.createNativeQuery(qAereo).getResultList();
                for (Object[] objects : lo) {
//                    le.add(castReturnEstatusAprobacionVO(objects));
                }
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion en traer estatus de aprobacion " + e);
            return null;
        }
    }

    /**
     * Modificado Joel Rodriguez 31/oct/2013 LE aumente el campo idEstatus a la
     * consulta pero se modificaron los metodos a donde le pega
     *
     * @param obj
     * @return
     */
    private EstatusAprobacionVO castReturnEstatusAprobacionVO(Object[] obj) {
        EstatusAprobacionVO estatusAprobacionVO;
        estatusAprobacionVO = new EstatusAprobacionVO();
        estatusAprobacionVO.setId((Integer) obj[0]);
        estatusAprobacionVO.setCodigo((String) obj[1]);
        estatusAprobacionVO.setNombreTipoSolicitud((String) obj[2]);
        estatusAprobacionVO.setNombreOrigen((String) obj[3]);
        estatusAprobacionVO.setNombreDestino((String) obj[4]);
        estatusAprobacionVO.setFechaSalida((Date) obj[5]);
        estatusAprobacionVO.setHoraSalida((Date) obj[6]);
        estatusAprobacionVO.setFechaRegreso((Date) obj[7]);
        estatusAprobacionVO.setHoraRegreso((Date) obj[8]);
        estatusAprobacionVO.setNombreMotivo((String) obj[9]);
        estatusAprobacionVO.setNombreEstatus((String) obj[10]);
        estatusAprobacionVO.setNombreGerenciaResponsable((String) obj[11]);
        estatusAprobacionVO.setObservacion((String) obj[12]);
        estatusAprobacionVO.setIdSolicitud((Integer) obj[13]);
        estatusAprobacionVO.setIdTipoSolicitud((Integer) obj[14]);
        estatusAprobacionVO.setViajerosCount((Integer) obj[15]);
        estatusAprobacionVO.setJustificacionCorta((obj[16] != null) ? (String) obj[16] : "");
        estatusAprobacionVO.setJustificacion((obj[16] != null) ? (String) obj[16] : "");
        estatusAprobacionVO.setRedondo(obj[17] != null ? (((String) obj[17]).equals(Constantes.BOOLEAN_TRUE) ? true : false) : false);
        estatusAprobacionVO.setSemaforo(obj[18] != null ? (String) obj[18] : "");
        estatusAprobacionVO.setCountEscalas((Integer) obj[19]);
        estatusAprobacionVO.setGenero((String) obj[20]);
        estatusAprobacionVO.setIdEstatus((Integer) obj[21]);
        estatusAprobacionVO.setIdTipoEspecifico((Integer) obj[22]);
        estatusAprobacionVO.setMensajePoliticaColorSemaforo(obj[23] != null ? (String) obj[23] : "-");
        estatusAprobacionVO.setMotivoAutorizarJustificacion((obj[18] != null) ? (String) obj[24] : "");
        estatusAprobacionVO.setHoraMinimaSemaforoActual((obj[25] != null) ? (Date) obj[25] : null);
        estatusAprobacionVO.setHoraMaximaSemaforoActual((obj[26] != null) ? (Date) obj[26] : null);
        estatusAprobacionVO.setSelected(false);
        estatusAprobacionVO.setJustificado((obj[16] != null && !obj[16].toString().equals("")) ? Constantes.TRUE : Constantes.FALSE);
        //Busca el motivo por el que esta en la bandeja del jefe..

        return estatusAprobacionVO;
    }

    
    public void traerHistorialEstatusAprobacionPorSolicitud(Integer idOldSolicitudViaje, Integer idNewSolicitudViaje) {
        try {
            List<EstatusAprobacionVO> estatusHist = this.traerHistorialEstatusAprobacionPorSolicitudViaje(idOldSolicitudViaje, true);
            List<EstatusAprobacionVO> estatus = this.traerHistorialEstatusAprobacionPorSolicitudViaje(idOldSolicitudViaje, false);
            SgSolicitudViaje sv = sgSolicitudViajeRemote.find(idNewSolicitudViaje);
            if (sv != null && sv.getOficinaOrigen() != null && sv.getOficinaOrigen().getId() > 0) {
                if (sv.getCodigo() == null || sv.getCodigo().isEmpty()) {
                    sv.setCodigo(this.generateCode());
                    sgSolicitudViajeRemote.edit(sv);
                }

                int contador = estatusHist.size();
                boolean traeDatos = estatus != null && estatus.size() > 0;
                for (EstatusAprobacionVO vo : estatusHist) {
                    CadenaAprobacionSolicitudVO cadenaVo = new CadenaAprobacionSolicitudVO();
                    cadenaVo.setIdEstatus(vo.getIdEstatus());
                    if (traeDatos || (!traeDatos && contador > (estatusHist.size() - 2))) {
                        this.insertarEstatusAprobacion(
                                sv,
                                cadenaVo,
                                vo.getIdUsuarioAprobo(),
                                sv.getGenero().getId(),
                                true,
                                true,
                                true, 
                                sv.getApCampo().getId());
                    } else if (!traeDatos) {
                        cadenaVo.setUltimaCadena(true);
                        this.insertarEstatusAprobacion(
                                sv,
                                cadenaVo,
                                vo.getIdUsuarioAprobo(),
                                sv.getGenero().getId(),
                                false,
                                false,
                                true,
                                sv.getApCampo().getId());
                        break;
                    }
                    contador--;
                }

                for (EstatusAprobacionVO vo : estatus) {
                    CadenaAprobacionSolicitudVO cadenaVo = new CadenaAprobacionSolicitudVO();
                    cadenaVo.setIdEstatus(vo.getIdEstatus());
                    cadenaVo.setUltimaCadena(true);
                    SgOficinaAnalistaVo analista = sgOficinaAnalistaRemote.traerAnalistaPrincipalPorOficina(sv.getOficinaOrigen().getId());
                    if (analista != null && analista.getIdAnalista() != null && !analista.getIdAnalista().isEmpty()) {
                        this.insertarEstatusAprobacion(
                                sv,
                                cadenaVo,
                                analista.getIdAnalista(),
                                sv.getGenero().getId(),
                                false,
                                false,
                                true,
                                sv.getApCampo().getId());
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SgEstatusAprobacionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public List<EstatusAprobacionVO> traerHistorialEstatusAprobacionPorSolicitudViaje(Integer idSolicitudViaje, boolean isHistorial) {
        UtilLog4j.log.info(this, "traerHistorialEstatusAprobacionPorSolicitudViaje " + idSolicitudViaje);
        try {
            clearQuery();
            appendQuery("SELECT  ea.id AS idEstatusAprobacion,");
            appendQuery(" es.nombre AS nombre_estatus,");//1
            appendQuery(" ea.AUTOMATICO,");//2
            appendQuery(" ea.GENERO,");//3
            appendQuery(" u.nombre AS NOMBRE_APROBO,");//4
            appendQuery(" ea.USUARIO AS APROBO, ");//5
            appendQuery(" ea.FECHA_GENERO,");//6
            appendQuery(" ea.HORA_GENERO,");//7
            appendQuery(" ea.FECHA_MODIFICO,");//8
            appendQuery(" ea.HORA_MODIFICO,");//9
            appendQuery(" case when ea.SG_ESTADO_SEMAFORO is not null then ");
            appendQuery("  (select s.COLOR ");
            appendQuery(" from SG_ESTADO_SEMAFORO es,");
            appendQuery(" SG_SEMAFORO s ");
            appendQuery(" where es.id = ea.sg_estado_semaforo");
            appendQuery(" and es.SG_SEMAFORO = s.ID)");
            appendQuery(" else '' end, ");//10
            appendQuery(" ea.estatus");//11
            appendQuery(" FROM sg_Estatus_Aprobacion ea,");
            appendQuery(" estatus es,Usuario u");
            appendQuery(" WHERE ea.sg_solicitud_viaje = " + idSolicitudViaje);
            appendQuery(" AND ea.realizado = '").append(isHistorial ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND ea.historial = '").append(isHistorial ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND ea.ELIMINADO = 'False'");
            appendQuery(" AND ea.usuario = u.id");
            appendQuery(" AND ea.estatus = es.id");
            appendQuery(" order by es.id ASC");
            UtilLog4j.log.info(this, "QUERY " + getStringQuery());
            List<EstatusAprobacionVO> le = new ArrayList<>();
            List<Object[]> lo = em.createNativeQuery(getStringQuery()).getResultList();
            for (Object[] objects : lo) {
                le.add(castReturnHistoryEstatusAprobacionVO(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion en traer el historial del estatus de aprobacion " + e);
            return null;
        }
    }

    
    public EstatusAprobacionVO traerEstatusPorAutorizarSolicitudViaje(int idSolicitud) {
        List<EstatusAprobacionVO> l = traerHistorialEstatusAprobacionPorSolicitudViaje(idSolicitud, false);
        EstatusAprobacionVO e = new EstatusAprobacionVO();
        List<ViajeVO> viajes;
        if (l != null && !l.isEmpty()) {
            e = l.get(l.size() - 1);
            if (e.getIdEstatus() == Constantes.ESTATUS_PARA_HACER_VIAJE) {
                viajes = sgViajeroRemote.getAllViajesBySolicitud(idSolicitud);
                if (viajes != null) {
                    e = null;
                }
            }
        }
        return e;
    }

    private EstatusAprobacionVO castReturnHistoryEstatusAprobacionVO(Object[] obj) {
        EstatusAprobacionVO estatusAprobacionVO;
        estatusAprobacionVO = new EstatusAprobacionVO();
        estatusAprobacionVO.setId((Integer) obj[0]);
        estatusAprobacionVO.setNombreEstatus((String) obj[1]);
        estatusAprobacionVO.setAutomatico((Boolean) obj[2]);
        estatusAprobacionVO.setNombreUsuarioAprobo((String) obj[4]);
        estatusAprobacionVO.setIdUsuarioAprobo((String) obj[5]);
        estatusAprobacionVO.setFechaGenero((Date) obj[6]);
        estatusAprobacionVO.setHoraGenero((Date) obj[7]);
        estatusAprobacionVO.setFechaModifico(obj[8] != null ? (Date) obj[8] : (new Date()));
        estatusAprobacionVO.setHoraModifico(obj[9] != null ? (Date) obj[9] : (new Date()));
        estatusAprobacionVO.setColorSemaforo((String) obj[10]);
        estatusAprobacionVO.setIdEstatus((Integer) obj[11]);
        return estatusAprobacionVO;
    }

    /**
     * MLUIS 03/12/2013
     */
    
    public int getCountTripRequestByOffice(String idUsuarioSesion, int idOficina, int idEstatus) {
        UtilLog4j.log.info(this, "SgEstatusAprobacionImpl.getCountTripRequestByOffice()");
        clearQuery();
        appendQuery(" SELECT count(ea.id)");
        appendQuery("  FROM sg_solicitud_viaje sol,");
        appendQuery(" sg_Estatus_Aprobacion ea,");
        appendQuery(" sg_motivo mo,");
        appendQuery(" sg_tipo_solicitud_viaje tip,");
        appendQuery(" estatus es,");
        appendQuery(" sg_tipo_especifico te,");
        appendQuery(" sg_oficina ofiOrigen,");
        appendQuery(" sg_oficina ofiDes,");
        appendQuery(" gerencia g");
        appendQuery("  WHERE ea.estatus = " + idEstatus);
        appendQuery("  AND ofiOrigen.id = " + idOficina);
        appendQuery("  and ea.usuario =  '").append(idUsuarioSesion).append("'");
        appendQuery("  AND ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery("  AND ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery("  AND sol.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery(" AND sol.estatus = ea.estatus");
        appendQuery("  AND te.id = 2");
        appendQuery("  AND ea.sg_solicitud_viaje = sol.id");
        appendQuery("  AND sol.sg_motivo = mo.id");
        appendQuery("  AND sol.oficina_destino = ofiDes.id");
        appendQuery("  AND sol.sg_tipo_solicitud_viaje = tip.id");
        appendQuery("  AND sol.oficina_origen = ofiOrigen.id");
        appendQuery("  AND ea.estatus = es.id");
        appendQuery("  AND tip.sg_tipo_especifico = te.id");
        appendQuery("  AND sol.gerencia_responsable = g.id");
        return ((Integer) em.createNativeQuery(query.toString()).getSingleResult()).intValue();
    }

    
    public int getCountAirRequestByOffice(int idOficina, int idEstatus, String idUsuarioGeneroSV) {
        clearQuery();
        appendQuery("SELECT count(ea.id)");
        appendQuery("  FROM sg_solicitud_viaje sol,");
        appendQuery(" sg_Estatus_Aprobacion ea,");
        appendQuery(" sg_itinerario it");
        appendQuery("  WHERE ea.estatus = ").append(idEstatus);
        appendQuery("  AND ea.usuario = '").append(idUsuarioGeneroSV).append("'");
        appendQuery("  AND sol.oficina_origen = ").append(idOficina);
        appendQuery("  AND ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery("  AND ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery("  AND sol.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        appendQuery("  AND sol.sg_tipo_especifico = ").append(Constantes.SOLICITUDES_AEREA);
        appendQuery("  AND ea.sg_solicitud_viaje = sol.id");
        appendQuery("  AND it.sg_solicitud_viaje = sol.id");
        appendQuery("  AND it.ida = '").append(Constantes.BOOLEAN_TRUE).append("'");
        return ((Integer) em.createNativeQuery(query.toString()).getSingleResult()).intValue();
    }

    
    public int getCountAirRequestByOfficeAnSgTipoSolicitudViaje(int idSgOficina, int idEstatus, int idSgTipoSolicitudViaje) {
        UtilLog4j.log.info(this, "SgEstatusAprobacionImpl.getCountAirRequestByOfficeAnSgTipoSolicitudViaje()");

        Query q = em.createNativeQuery("SELECT count(ea.id)"
                + " FROM sg_solicitud_viaje sol,"
                + "sg_Estatus_Aprobacion ea,"
                + "sg_motivo mo,"
                + "sg_tipo_solicitud_viaje tip,"
                + "estatus es,"
                + "sg_tipo_especifico te,"
                + "sg_oficina ofiOrigen,"
                + "gerencia g,"
                + "sg_itinerario it,"
                + "si_ciudad ci,"
                + "si_ciudad ciD"
                + " WHERE ea.estatus = " + idEstatus
                + " AND ofiOrigen.id = " + idSgOficina
                + (idSgTipoSolicitudViaje == -1 ? "" : ("AND sol.sg_tipo_solicitud_viaje = " + idSgTipoSolicitudViaje))
                + " AND ea.realizado = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND ea.historial = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND sol.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                + " AND te.id = 3"
                + " AND sol.sg_motivo = mo.id"
                + " AND sol.sg_tipo_solicitud_viaje = tip.id"
                + " AND sol.oficina_origen = ofiOrigen.id"
                + " AND sol.gerencia_responsable = g.id"
                + " AND tip.sg_tipo_especifico = te.id "
                + " AND ea.estatus = es.id"
                + " AND ea.sg_solicitud_viaje = sol.id"
                + " AND it.ida = '" + Constantes.BOOLEAN_TRUE + "'"
                + " AND it.si_ciudad_origen = ci.id"
                + " AND it.si_ciudad_destino = ciD.id"
                + " AND it.sg_solicitud_viaje = sol.id");
        return ((Integer) q.getSingleResult());
    }

    
    public List<EstatusAprobacionVO> getApprovalStatusByOffice(String idUsuarioGeneroSV, int idOficina, int idEstatus, boolean isTerrestre) {
        UtilLog4j.log.info(this, "Traer solicitudes por status y oficina origen");
        String qAereo;
        String qTerrestre;
        Query q;
        try {
            qTerrestre = "SELECT distinct ea.id AS idEstatusAprobacion," //0
                    + "sol.codigo AS codigo_Solicitud," //1
                    + "tip.nombre AS tipo_Solicitud,"//2
                    + "ofiOrigen.nombre AS oficina_origen,"//3
                    + "ofiDes.nombre AS oficina_destino,"//4
                    + "sol.fecha_salida AS fecha_salida,"//5
                    + "sol.hora_salida AS hora_salida,"//6
                    + "sol.fecha_regreso AS fecha_regreso,"//7
                    + "sol.hora_regreso AS hora_regreso,"//8
                    + "mo.nombre AS nombre_motivo,"//9
                    + "es.nombre AS nombre_estatus,"//10
                    + "g.nombre, "//11
                    + "sol.observacion , "//12
                    + "sol.id, " // 13
                    + "tip.id, " // 14
                    + "(Select count(id) from sg_Viajero where SG_SOLICITUD_VIAJE = sol.id AND eliminado = 'False')  as Viajeros, "//15
                    + " '', "//16
                    + " sol.redondo, "//17
                    + " (select se.COLOR from SG_ESTADO_SEMAFORO est,SG_SEMAFORO se where est.ID = ea.SG_ESTADO_SEMAFORO and est.SG_SEMAFORO = se.id), "//18
                    + " 0, " //19
                    + " sol.genero AS GENERO_SV, " //19
                    + " ea.estatus, " //20
                    + " tip.sg_tipo_especifico, " //21
                    + " NULL, " //23
                    + " ''," //24
                    + " NULL," //25
                    + " NULL" //26
                    + " FROM sg_solicitud_viaje sol,"
                    + "sg_Estatus_Aprobacion ea,"
                    + "sg_motivo mo,"
                    + "sg_tipo_solicitud_viaje tip,"
                    + "estatus es,"
                    + "sg_tipo_especifico te,"
                    + "sg_oficina ofiOrigen,"
                    + "sg_oficina ofiDes,"
                    + "gerencia g"
                    + " WHERE ea.estatus = " + idEstatus
                    + " AND ofiOrigen.id = " + idOficina
                    + " AND ea.usuario = '" + idUsuarioGeneroSV + "'"
                    + " AND ea.realizado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND ea.historial = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND sol.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND sol.OFICINA_DESTINO is not null"
                    + " AND sol.estatus = ea.estatus"
                    + " AND te.id = 2"
                    + " AND ea.sg_solicitud_viaje = sol.id"
                    + " AND sol.sg_motivo = mo.id"
                    + " AND sol.oficina_destino = ofiDes.id"
                    + " AND sol.sg_tipo_solicitud_viaje = tip.id"
                    + " AND sol.oficina_origen = ofiOrigen.id"
                    + " AND ea.estatus = es.id"
                    + " AND tip.sg_tipo_especifico = te.id"
                    + " AND sol.gerencia_responsable = g.id"
                    + " order by sol.codigo asc";
            clearQuery();
            appendQuery(" SELECT distinct ea.id AS idEstatusAprobacion,"); //0
            appendQuery(" sol.codigo AS codigo_Solicitud,"); //2
            appendQuery(" tip.nombre AS tipo_Solicitud,");//3
            appendQuery(" ci.nombre AS origen,");//4
            appendQuery(" ciD.nombre AS destino,");//5
            appendQuery(" sol.fecha_salida AS fecha_salida,");//6
            appendQuery(" sol.hora_salida AS hora_salida,");//7
            appendQuery(" sol.fecha_regreso AS fecha_regreso,");//7
            appendQuery(" sol.hora_regreso AS hora_regreso,");//8
            appendQuery(" '',");//9
            appendQuery(" es.nombre AS nombre_estatus,");//10
            appendQuery(" g.nombre AS nombreGerencia,");//11
            appendQuery(" sol.observacion , "); //12
            appendQuery(" sol.id, "); // 13
            appendQuery(" tip.id, "); // 14
            appendQuery(" (Select count(id) from sg_Viajero where SG_SOLICITUD_VIAJE = sol.id AND eliminado = 'False')  as Viajeros, "); //15
            appendQuery(" '', "); //16
            appendQuery(" sol.redondo,");
            appendQuery(" '', ");
            appendQuery(" (select count(det.id)  from SG_DETALLE_ITINERARIO det   where  det.SG_ITINERARIO = it.ID");
            appendQuery(" and det.HISTORIAL = 'False'and det.ELIMINADO = 'False'),"); //19
            appendQuery(" sol.genero AS GENERO_SV ,"); //20
            appendQuery(" ea.estatus, "); //21
            appendQuery(" tip.sg_tipo_especifico, "); //22
            appendQuery(" NULL, "); //23
            appendQuery(" '',"); //24
            appendQuery(" NULL,"); //24
            appendQuery(" NULL"); //24

            appendQuery(" FROM sg_solicitud_viaje sol,");
            appendQuery(" sg_Estatus_Aprobacion ea,");
            appendQuery(" sg_tipo_solicitud_viaje tip,");
            appendQuery(" estatus es,");
            appendQuery(" sg_tipo_especifico te,");
            appendQuery(" sg_oficina ofiOrigen,");
            appendQuery(" gerencia g,");
            appendQuery(" sg_itinerario it,");
            appendQuery(" si_ciudad ci,");
            appendQuery(" si_ciudad ciD");
            appendQuery(" WHERE ea.estatus = ").append(idEstatus);
            appendQuery(" AND sol.oficina_origen = ").append(idOficina);
            appendQuery(" AND ea.usuario='").append(idUsuarioGeneroSV).append("'");
            appendQuery(" AND ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND sol.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND te.id = ").append(Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA);
            appendQuery(" AND sol.sg_tipo_solicitud_viaje = tip.id");
            appendQuery(" AND sol.oficina_origen = ofiOrigen.id");
            appendQuery(" AND sol.gerencia_responsable = g.id");
            appendQuery(" AND tip.sg_tipo_especifico = te.id ");
            appendQuery(" AND ea.estatus = es.id");
            appendQuery(" AND ea.sg_solicitud_viaje = sol.id");
            appendQuery(" AND it.si_ciudad_origen = ci.id");
            appendQuery(" AND it.ida = '").append(Constantes.BOOLEAN_TRUE).append("'");
            appendQuery(" AND it.si_ciudad_destino = ciD.id");
            appendQuery(" AND it.sg_solicitud_viaje = sol.id");
            appendQuery(" order by sol.codigo asc");

            List<EstatusAprobacionVO> le = new ArrayList<EstatusAprobacionVO>();

            if (isTerrestre) {
                UtilLog4j.log.info(this, "terrestres");
//                UtilLog4j.log.info(this,"queryT: " + qTerrestre);
                List<Object[]> lo = em.createNativeQuery(qTerrestre).getResultList();
                for (Object[] objects : lo) {
                    le.add(castReturnEstatusAprobacionVO(objects));
                }
            } else {
                UtilLog4j.log.info(this, "aereas");
//                UtilLog4j.log.info(this,"queryA: " + qAereo);
                List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
//                UtilLog4j.log.info(this,"q sol aereas: " + qAereo.toString());
                for (Object[] objects : lo) {
                    le.add(castReturnEstatusAprobacionVO(objects));
                }
            }
            UtilLog4j.log.info(this, "Se encontraron: " + (le != null ? le.size() : 0) + " EstatusAprobacion para la oficina: " + idOficina + " con el estatus: " + idEstatus);
            return le;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion en traer estatus de aprobacion " + e);
            e.printStackTrace();
            return null;
        }
    }

    
    public List<EstatusAprobacionVO> getSolicitudesViajesCiudades(int idOficina, int idEstatus) {
        UtilLog4j.log.info(this, "Traer solicitudes por status y oficina origen");
        clearQuery();
        try {
            appendQuery("SELECT distinct ea.id AS idEstatusAprobacion, ");//0
            appendQuery(" sol.codigo AS codigo_Solicitud, ");//1
            appendQuery(" tip.nombre AS tipo_Solicitud,");//2
            appendQuery(" ofiOrigen.nombre AS oficina_origen, ");//3
            appendQuery(" ciud.nombre ||', '|| est.nombre||', '||pa.nombre,"); //4
            appendQuery(" sol.fecha_salida AS fecha_salida,"); //5
            appendQuery(" sol.hora_salida AS hora_salida,"); //6
            appendQuery(" sol.fecha_regreso AS fecha_regreso,"); //7
            appendQuery(" sol.hora_regreso AS hora_regreso,"); //8
            appendQuery(" mo.nombre AS nombre_motivo,"); //9
            appendQuery(" es.nombre AS nombre_estatus,"); //10
            appendQuery(" g.nombre, "); //11
            appendQuery(" sol.observacion , "); //12
            appendQuery(" sol.id,   "); //13
            appendQuery(" tip.id,   "); //14
            appendQuery(" (Select count(id) from sg_Viajero where SG_SOLICITUD_VIAJE = sol.id AND eliminado = 'False')  as Viajeros, "); //15
            appendQuery(" '', "); //16
            appendQuery(" sol.redondo,"); //17
            appendQuery(" (select se.COLOR from SG_ESTADO_SEMAFORO est,SG_SEMAFORO se where est.ID = ea.SG_ESTADO_SEMAFORO and est.SG_SEMAFORO = se.id), "); //18
            appendQuery(" 0,");
            appendQuery(" sol.genero AS GENERO_SV, "); //20
            appendQuery(" ea.estatus, "); //212
            appendQuery(" tip.sg_tipo_especifico, "); //22
            appendQuery(" (select es.JUSTIFICACION ");
            appendQuery("     from SG_ESTADO_SEMAFORO es, ");
            appendQuery("     SG_SEMAFORO se ");
            appendQuery("  where es.id = sol.SG_ESTADO_SEMAFORO");
            appendQuery("     and es.actual = 'True'");
            appendQuery("     and es.ELIMINADO = 'False'");
            appendQuery("     and es.SG_SEMAFORO = se.ID");
            appendQuery(" ) as justificacion_semaforo,");//23
            appendQuery(" '',"); //24
            appendQuery(" NULL, ");
            appendQuery(" NULL");
            appendQuery(" FROM sg_solicitud_viaje sol,");
            appendQuery(" sg_Estatus_Aprobacion ea,");
            appendQuery(" sg_motivo mo,");
            appendQuery(" sg_tipo_solicitud_viaje tip,");
            appendQuery(" estatus es,");
            appendQuery(" sg_tipo_especifico te,");
            appendQuery(" sg_oficina ofiOrigen,");
            appendQuery(" SG_VIAJE_CIUDAD vc,");
            appendQuery(" SI_CIUDAD ciud,");
            appendQuery(" SI_ESTADO est,");
            appendQuery(" SI_PAIS pa,");
            appendQuery(" gerencia g");
            appendQuery("  WHERE ea.estatus = ").append(idEstatus);
            appendQuery("  AND ofiOrigen.id = ").append(idOficina);
            appendQuery("  AND sol.OFICINA_DESTINO is null");
            appendQuery("  AND vc.sg_solicitud_viaje = sol.ID");
            appendQuery("  AND vc.SI_CIUDAD = ciud.ID");
            appendQuery("  AND ciud.SI_ESTADO = est.id");
            appendQuery("  AND est.si_pais = pa.id");
            appendQuery("  AND ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND sol.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND vc.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND te.id = ").append(Constantes.SOLICITUDES_TERRESTRE);
            appendQuery("  AND ea.sg_solicitud_viaje = sol.id");
            appendQuery("  AND sol.sg_motivo = mo.id");
            appendQuery("  AND sol.sg_tipo_solicitud_viaje = tip.id");
            appendQuery("  AND sol.oficina_origen = ofiOrigen.id");
            appendQuery("  AND ea.estatus = es.id");
            appendQuery("  AND tip.sg_tipo_especifico = te.id");
            appendQuery("  AND sol.gerencia_responsable = g.id");
            appendQuery("  order by sol.codigo asc");

            List<EstatusAprobacionVO> le = new ArrayList<EstatusAprobacionVO>();
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                le.add(castReturnEstatusAprobacionVO(objects));
            }

//            UtilLog4j.log.info(this,"Se encontraron " + (le != null ? le.size() : 0) + " EstatusAprobacion para viajes fuera de oficina");
            return le != null ? le : Collections.EMPTY_LIST;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion en traer estatus de aprobacion " + e);
            UtilLog4j.log.fatal(this, e.toString());
            return null;
        }
    }

    /**
     * *
     * Se modificó la consulta para agregar el estado de la solicitud de viaje
     *
     * @param usuarioSession
     * @param idEstatus
     * @return
     */
    
    public int getTotalSolicitudesPorEstatus(String usuarioSession, int idEstatus) {
        UtilLog4j.log.info(this, "getTotalSolicitudesPorAprobar");
        try {
            clearQuery();
            query.append("SELECT count(s.id) FROM Sg_Estatus_Aprobacion s ");
            query.append("	inner join sg_solicitud_viaje sv on s.sg_solicitud_viaje = sv.id ");
            query.append(" WHERE ");
            switch (idEstatus) {
                case Constantes.ESTATUS_JUSTIFICAR:
                    query.append(" ((s.estatus = ").append(Constantes.ESTATUS_JUSTIFICAR).append(" AND s.usuario is null) OR (s.estatus =")
                            .append(Constantes.ESTATUS_CON_CENTOPS).append(" AND s.usuario = '").append(usuarioSession).append("')) ");
                    break;
                case Constantes.ESTATUS_APROBAR:
                case Constantes.ESTATUS_VISTO_BUENO:
                    query.append(" s.ESTATUS between  ").append(Constantes.ESTATUS_VISTO_BUENO).append(" and  ").append(Constantes.ESTATUS_APROBAR);
                    query.append(" AND s.usuario = '").append(usuarioSession).append("'");
                    //   query.append(" AND sv.ESTATUS between  ").append(Constantes.ESTATUS_VISTO_BUENO).append(" and  ").append(Constantes.ESTATUS_APROBAR);
                    break;
                case Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO:
                    query.append("s.Estatus =").append(Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO);
                    break;
                default:
                    query.append("1=1");
                    break;
            }
            query.append(" AND sv.estatus = s.estatus");
            query.append(" AND s.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            query.append(" AND s.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
            query.append(" AND s.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            query.append(" AND sv.FECHA_SALIDA >= CURRENT_DATE");

            Query q = em.createNativeQuery(query.toString());
//            UtilLog4j.log.info(this,"Query " + q.toString());
//            UtilLog4j.log.info(this,"Resultado " + ((Integer) q.getSingleResult()));
Long l =    (long) q.getSingleResult();
int regresa = l.intValue();
            return (regresa );
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepción en getTotalSolicitudesPorAprobar.." + e);
            return 0;
        }
    }

    /**
     * Total para seguridad
     *
     * @param idEstatus
     * @return
     */
    
    public int getTotalSolicitudesSeguridad(int idEstatus) {
        UtilLog4j.log.info(this, "getTotalSolicitudesPorAprobar");
        try {
            Query q = em.createNativeQuery("SELECT count(s.id) FROM Sg_Estatus_Aprobacion s"
                    + " WHERE s.estatus = " + idEstatus
                    + " AND s.realizado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND s.historial = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND s.eliminado = '" + Constantes.BOOLEAN_FALSE + "'");
            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepción en getTotalSolicitudesPorAprobar.." + e);
            return 0;
        }
    }

    /**
     * Solo modifica el estatusde una solicitud
     *
     * @param idSolicitud
     * @param idEstatus
     * @param idUsuario
     * @return
     */
    public boolean updateStateSolicitudViaje(int idSolicitud, int idEstatus, String idUsuario, Date horaSalida) {
        try {
            SgSolicitudViaje solicitud = sgSolicitudViajeRemote.find(idSolicitud);
            solicitud.setEstatus(estatusService.find(idEstatus));
            solicitud.setFechaModifico(new Date());
            solicitud.setHoraModifico(new Date());
            if(idEstatus == Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO){
                solicitud.setHoraSalida(horaSalida);
            }
            sgSolicitudViajeRemote.update(solicitud, idUsuario);
            return true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al modificar la solicitud de viaje desde un estatus" + e);
            return false;
        }
    }

    private void guardarSiMovimiento(Integer idSolicitud, String motivo, SiOperacion siOperacion, String idUsuarioRealizo) throws SIAException, Exception {
        UtilLog4j.log.info(this, "guardarSIMovimiento de solicitud");
        try {
            SiMovimiento simo = siMovimientoService.guardarSiMovimiento(motivo, siOperacion, new Usuario(idUsuarioRealizo));
            UtilLog4j.log.info(this, "Guardo en simovimiento ");
            this.relacionSolicitudSiMovimientoService.guardarSiMovimiento(idSolicitud, simo.getId(), idUsuarioRealizo);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al guardar simovimiento  de solicitud " + e);
        }
    }

    private String getDigitosAño(Date fecha) {
        SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
        String Cadena = SDF.format(fecha);
        String r = Cadena.substring(8, 10);
        return r;
    }

    private String generateCode() {
        String codigo;
        codigo = "SV" + getDigitosAño(new Date()) + "-" + Integer.toString(folioService.getFolio("SOLICITUD_VIAJE_CONSECUTIVO"));
        UtilLog4j.log.info(this, "Código generado: " + codigo);
        return codigo;
    }

    /**
     * Finaliza una solicitud apartir de un parametro de tipo SgSolicitud (solo
     * cambia el campo Historial a TRUE)
     *
     * @param sgSolicitudViaje
     * @param usuarioRealizo
     * @return
     *
     * MLUIS 19/11/2013 realiza
     *
     * Joel rodriguez 04/12/2013 Se agregó un estatus nuevo para las solicitudes
     * terminadas... 460 representa terminada.
     */
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean finalizeRequest(int idSolicitudViaje, Usuario usuarioRealizo, int idRol) {
        UtilLog4j.log.info(this, "finalizeRequest");
//        SolicitudViajeVO solicitudViajeVO = sgSolicitudViajeRemote.buscarPorId(idSolicitudViaje, Constantes.NO_ELIMINADO);
        SgEstatusAprobacion ea = null;
        EstatusAprobacionSolicitudVO eaVo = null;
        try {
            SolicitudViajeVO solicitudViajeVO = sgSolicitudViajeRemote.buscarPorId(idSolicitudViaje, Constantes.NO_ELIMINADO, Constantes.CERO);
            if (idRol == Constantes.ROL_DIRECCION_GENERAL) {
                eaVo = buscarEstatusAprobacionPorIdSolicitudIdEstatus(idSolicitudViaje, solicitudViajeVO.getIdEstatus());
                ea = find(eaVo.getId());
            } else {
                ea = getSgSolicitudForFinalize(idSolicitudViaje, Constantes.ESTATUS_PARA_HACER_VIAJE);
            }
            if (ea != null) {
                if (ea.getSgSolicitudViaje().getSgRutaTerrestre() != null && ea.getSgSolicitudViaje().getSgTipoSolicitudViaje().getSgTipoEspecifico().getId() != Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
                    ea.setSgEstadoSemaforo(traerEstadoSemaforoActualRuta(ea.getSgSolicitudViaje().getSgRutaTerrestre().getId()));
                }
                ea.setHistorial(Constantes.BOOLEAN_TRUE);
                ea.setRealizado(Constantes.BOOLEAN_TRUE);
                ea.setModifico(usuarioRealizo);
                ea.setFechaModifico(new Date());
                ea.setHoraModifico(new Date());
                edit(ea);
                //publicar
                createNewsStatusAprobado(ea, usuarioRealizo.getId());
                //--crear estatus de finalizado
                insertarEstatusAprobacionAutomatico(ea.getSgSolicitudViaje(), Constantes.ESTATUS_TERMINADA, usuarioRealizo.getId());

                updateStateSolicitudViaje(idSolicitudViaje, Constantes.ESTATUS_TERMINADA, usuarioRealizo.getId(),ea.getSgSolicitudViaje().getHoraSalida());

                //Solicita las estancias
                if (idRol == Constantes.ROL_DIRECCION_GENERAL) {
                    if (solicitudViajeVO.getIdSolicitudEstancia() != 0) {
                        SgSolicitudEstanciaVo solicitudEstanciaVo = sgSolicitudEstanciaRemote.buscarEstanciaPorId(solicitudViajeVO.getIdSolicitudEstancia());
                        if (solicitudEstanciaVo.getIdEstatus() == Constantes.ESTATUS_SOLICITUD_ESTANCIA_TEMPORAL) {
                            sgSolicitudEstanciaRemote.solicitarEstancia(solicitudEstanciaVo, usuarioRealizo.getId(), sgDetalleSolicitudEstanciaRemote.traerDetallePorSolicitud(solicitudEstanciaVo.getId(), Constantes.NO_ELIMINADO));
                        }
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en finalizarSolicitud: " + e);
            return false;
        }
    }

    private SgEstatusAprobacion getSgSolicitudForFinalize(int idSolicitud, int estatus) {
        UtilLog4j.log.info(this, "getSgSolicitudForFinalize");
        try {
            return (SgEstatusAprobacion) em.createQuery("SELECT ea FROM SgEstatusAprobacion ea "
                    + " WHERE ea.estatus.id = :estatus"
                    + " AND ea.sgSolicitudViaje.id = :idSolicitudViaje"
                    + " AND ea.historial = :FALSE "
                    + " AND ea.realizado = :f ").setParameter("idSolicitudViaje", idSolicitud).setParameter("estatus", estatus).setParameter("f", Constantes.BOOLEAN_FALSE).setParameter("FALSE", Constantes.BOOLEAN_FALSE).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer la solicitud por finaliza " + e);
            return null;
        }
    }

    /**
     * ************ BITACORA - CO_NOTICIA *********************************
     */
    /**
     * Modifico: NLopez Traer correos de responsable y seguridad
     *
     * @param idSolicitudViaje
     * @param idUsuario
     */
    //crear Bitacora - Notiica
    //
    public void createEventNews(Integer idSolicitudViaje, String idUsuario) {
        UtilLog4j.log.info(this, "createEventNews " + idSolicitudViaje);
        String titulo = "";
        String mensaje = new String();
        CoNoticia noticia = null;
        List<ComparteCon> listComparteCon = null;
        try {
            SgSolicitudViaje solicitud = sgSolicitudViajeRemote.find(idSolicitudViaje);
            if (solicitud.getCoNoticia() != null) {
                // ya tiene noticia solo modificar por estatusHtml
                UtilLog4j.log.info(this, "Noticia es diferente de null..");
            } else {
                UtilLog4j.log.info(this, "crear la noticia");
                titulo = "Solicitud de Viaje : ".concat(solicitud.getCodigo());
                if (solicitud.getSgTipoSolicitudViaje().getSgTipoEspecifico().getId() == 2) {//terrestre
                    ViajeDestinoVo vdestino = sgViajeCiudadRemote.findDestinoSolicitudViaje(solicitud.getId());
                    if (vdestino != null) {
                        UtilLog4j.log.info(this, "ES UNA SOLICITUD DE OFICINA A CIUDAD..");
                        //es una solicitud de viaje terrestre de Oficina a Ciudad
                        mensaje = mostrarDetalleSolicitudViajeTerrestreOC(solicitud, vdestino);
                    } else {
                        UtilLog4j.log.info(this, "ES UNA SOLICITUD DE OFICINA A OFICINA..");
                        mensaje += mostrarDetalleSolicitudViajeTerrestreOC(solicitud, null);
                    }
                } else {
                    mensaje += mostrarDetalleSolicitudViajeAereo(solicitud);
                }
                //prueba
                listComparteCon = new ArrayList<ComparteCon>();
                //viajeros de la solicitud
                listComparteCon = this.castTravellerOfComparteCon(solicitud);
                //otros
                if (idUsuario.equals("PRUEBA")) {
                    //|| idUsuario.equals("JORODRIGUEZ") || idUsuario.equals("MLUIS") || idUsuario.equals("SLUIS") || idUsuario.equals("HACOSTA")) {
                    listComparteCon.add(new ComparteCon("PRUEBA", "", "", "Usuario"));
                    UtilLog4j.log.info(this, "asigno los usuarios a compartir");
                } else {
                    //quien creo el viaje
                    listComparteCon.add(new ComparteCon(idUsuario, "", "", "Usuario"));
                    //quien es el gerente responsable
                    listComparteCon.add(new ComparteCon(getResponsableByGerencia(solicitud.getApCampo().getId(), solicitud.getGerenciaResponsable().getId().intValue()).getIdUsuario(), "", "", "Usuario"));
                    //Responsables SGL //Seguridad

                    List<UsuarioVO> lu = usuarioRemote.getUsuariosByRol(Constantes.SGL_RESPONSABLE);

                    for (UsuarioVO usuario1 : lu) {
                        listComparteCon.add(new ComparteCon(usuario1.getId(), "", "", "Usuario"));
                    }

                }
                if (listComparteCon != null && !listComparteCon.isEmpty()) {
                    CoNoticia noti = coNoticiaService.nuevaNoticia("SIA", titulo, "", mensaje, 0, 0, listComparteCon);
                    if (noti != null) {
                        //modificar el viaje
                        try {
                            solicitud.setCoNoticia(noti);
                            sgSolicitudViajeRemote.update(solicitud, idUsuario);
                            UtilLog4j.log.info(this,
                                    "Se publico la NOTICIA de la solicitud de viaje");
                        } catch (Exception ex) {
                            throw new SIAException("Excepcion en lapublicacion de la solicitud de viaje");
                        }
                    }
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al crear la noticia o el evento - la bitacora " + e);
        }
    }

    private void createNewsStatusAprobado(SgEstatusAprobacion ea, String idUsuario) {
        UtilLog4j.log.info(this, "createNewsStatusAprobado ");
        String titulo;
        String mensaje = new String();
        String mensajeAutomatico = "Mensaje automatico generado por el SIA";
        CoNoticia noticia = null;
        List<ComparteCon> listComparteCon = null;
        //buscar viaje
        try {
            if (ea.getSgSolicitudViaje().getCoNoticia() != null) {
                UtilLog4j.log.info(this, "crear la noticia");
                noticia = ea.getSgSolicitudViaje().getCoNoticia();
                mensaje = noticia.getMensajeAutomatico();
                mensaje += mostrarEstatusAprobado(ea.getEstatus().getNombre(), ea.getUsuario().getNombre(), ea.getFechaModifico(), ea.getHoraModifico());

                try {
                    noticia.setMensajeAutomatico(mensaje);
                    coNoticiaService.editNoticia(noticia, idUsuario);
                    coNoticiaService.compartirNoticia(noticia.getId(), idUsuario, idUsuario);
                    UtilLog4j.log.info(this, "Se publico la NOTICIA de la solicitud de viaje");
                } catch (Exception ex) {
                    throw new SIAException("Excepcion en lapublicacion de la solicitud de viaje");
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al crear el evento o la bitacora por id de viaje " + e);
        }
    }

    private void cancelRequestViajeNews(SgSolicitudViaje solicitud, String motivoCancelacion, String idUsuario) {
        String titulo;
        String mensaje = "";
        CoNoticia noticia = null;
        try {
            if (solicitud.getCoNoticia() != null) {
                noticia = solicitud.getCoNoticia();
                mensaje = noticia.getMensajeAutomatico();
                mensaje += mostrarEstatusAprobado(solicitud.getEstatus().getNombre(), usuarioRemote.find(idUsuario).getNombre(), solicitud.getFechaModifico(), solicitud.getHoraModifico());
                mensaje += mostrarMotivoCancelacionSolicitudViaje(motivoCancelacion);
                noticia.setMensajeAutomatico(mensaje);
                coNoticiaService.editNoticia(noticia, idUsuario);

                UtilLog4j.log.info(this, "NOTICIA PUBLICADA CANCELADA");
            } else {
                UtilLog4j.log.info(this, "No existe noticia para esta solicitud");
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al modificar la noticia " + e);
        }
    }

    private List<ComparteCon> castTravellerOfComparteCon(SgSolicitudViaje sgSolicitudViaje) throws SIAException {
        UtilLog4j.log.info(this, "findTravellerReturnOfComparteCon " + sgSolicitudViaje.getCodigo());
        List<ComparteCon> listComparteCon = null;
        ComparteCon comparteCon = null;
        List<SgViajero> listViajero = this.viajeroService.getViajerosBySolicitudViajeList(sgSolicitudViaje, false);
        //cast to ComparteCon
        if (listViajero != null) {
            UtilLog4j.log.info(this, "tiene valor");
            listComparteCon = new ArrayList<ComparteCon>();
            for (SgViajero viajero : listViajero) {
                UtilLog4j.log.info(this, "Agregar a " + viajero.getId());
                if (viajero.getUsuario() != null) {
                    comparteCon = new ComparteCon(viajero.getUsuario().getId(), viajero.getUsuario().getNombre(), "", "Usuario");
                    listComparteCon.add(comparteCon);
                }
            }
        } else {
            UtilLog4j.log.info(this, "no tiene valor la lista..");
        }
        return listComparteCon;
    }

    //usado para las noticias
    private String mostrarEstatusAprobado(String estatusNombre, String usuarioRealizo, Date fecha, Date hora) {
        UtilLog4j.log.info(this, "mostrarEstatusAprobado");
        UtilLog4j.log.info(this, "Estatus " + estatusNombre);
        UtilLog4j.log.info(this, "Realizo " + usuarioRealizo);
        StringBuilder estatusHtml = new StringBuilder("");

        estatusHtml.append("<table width=\"100%\" cellspacing=\"0\" border=\"0\" >");
        estatusHtml.append("<tr>");
        estatusHtml.append("<td width=\"20%\" align=\"left\" style=\" font:Arial, Helvetica, sans-serif; font-size:11px; font-weight: bold;color:gray;\">".concat(estatusNombre).concat("</td>"));
        estatusHtml.append("<td width=\"40%\" align=\"left\" style=\" font:Arial, Helvetica, sans-serif; font-size:11px;color:gray;\">".concat(usuarioRealizo).concat("</td>"));
        estatusHtml.append("<td width=\"40%\" align=\"left\" style=\" font:Arial, Helvetica, sans-serif; font-size:11px;color:gray;\">".concat(Constantes.FMT_TextDate.format(fecha)).concat(" ").concat(Constantes.FMT_hmm_a.format(hora)).concat("</td>"));
        estatusHtml.append("</tr>");
        estatusHtml.append("</table>");
        return estatusHtml.toString();
    }

    private String mostrarDetalleSolicitudViajeAereo(SgSolicitudViaje solicitud) throws SIAException {
        StringBuilder detalle = new StringBuilder("");
        //SgItinerario it = servicioItinerario.findBySolicitudViaje(solicitud, true, false);
        ItinerarioCompletoVo it = servicioItinerario.buscarItinerarioCompletoVoPorIdSolicitud(solicitud.getId(), true, false, "id");
        if (it != null) {
            detalle.append("<p>Se ha realizado una solicitud del viaje áereo <strong>(").append(solicitud.isRedondo() ? "Redondo" : "Sencillo").append(")</strong> de la ciudad de <strong>".concat(it.getNombreCiudadOrigen()).concat("</strong>"));
            detalle.append(" a la ciudad de <strong>".concat(it.getNombreCiudadDestino()).concat("</strong>"));
            detalle.append(" saliendo el día <strong>".concat(Constantes.FMT_TextDate.format(solicitud.getFechaSalida())).concat("</strong>"));
            if (solicitud.isRedondo()) {
                detalle.append(", regresando el <strong>".concat(Constantes.FMT_TextDate.format(solicitud.getFechaRegreso())).concat("</strong>."));
            } else {
                detalle.append(".");
            }
            detalle.append("</p>");
            detalle.append(mostrarEstatusAprobado("SOLICITÓ", solicitud.getModifico().getNombre(), solicitud.getFechaModifico(), solicitud.getHoraModifico()));

        }
        return detalle.toString();
    }

//    private String mostrarDetalleSolicitudViajeTerrestre(SgSolicitudViaje solicitud) throws SIAException {
//        StringBuilder detalle = new StringBuilder("");
//        detalle.append("<p>Se ha realizado una solicitud del viaje Terrestre <strong>(").append(solicitud.getRedondo().equals(Constantes.BOOLEAN_TRUE) ? "Redondo" : "Sencillo").append(")</strong>  de la oficina <strong>".concat(solicitud.getOficinaOrigen().getNombre()).concat("</strong>"));
//        detalle.append(" a la oficina <strong>".concat(solicitud.getOficinaDestino().getNombre()).concat("</strong>"));
//        detalle.append(" saliendo el día <strong>".concat(Constantes.FMT_TextDate.format(solicitud.getFechaSalida())).concat("</strong>"));
//        detalle.append(" <strong>".concat(Constantes.FMT_hmm_a.format(solicitud.getHoraSalida())).concat("</strong>"));
//        if (solicitud.getRedondo().equals(Constantes.BOOLEAN_TRUE)) {
//            detalle.append(", regresando el <strong>".concat(Constantes.FMT_TextDate.format(solicitud.getFechaRegreso())).concat("</strong>"));
//            detalle.append(" <strong>".concat(Constantes.FMT_hmm_a.format(solicitud.getHoraRegreso())).concat(".</strong>"));
//        } else {
//            detalle.append(".");
//        }
//
//        detalle.append("</p>");
//        detalle.append(mostrarEstatusAprobado("SOLICITÓ", solicitud.getModifico().getNombre(), solicitud.getFechaModifico(), solicitud.getHoraModifico()));
//        return detalle.toString();
//    }
    private String mostrarDetalleSolicitudViajeTerrestreOC(SgSolicitudViaje solicitud, ViajeDestinoVo viajeDestinoVo) throws SIAException {
        StringBuilder detalle = new StringBuilder("");
        detalle.append("<p>Se ha realizado una solicitud del viaje Terrestre <strong>(").append(solicitud.isRedondo() ? "Redondo" : "Sencillo").append(")</strong>  de la oficina <strong>".concat(solicitud.getOficinaOrigen().getNombre()).concat("</strong>"));
        if (viajeDestinoVo != null && viajeDestinoVo.getCiudadDestino() != null && !viajeDestinoVo.getCiudadDestino().isEmpty()) {
            detalle.append(" a la ciudad de <strong>".concat(viajeDestinoVo.getCiudadDestino()).concat(", ").concat(viajeDestinoVo.getEstadoDestino()).concat(", ").concat(viajeDestinoVo.getPaisDestino()).concat("</strong>"));
        } else {
            detalle.append(" a la oficina <strong>".concat(solicitud.getOficinaDestino().getNombre()).concat("</strong>"));
        }
        detalle.append(" saliendo el día <strong>".concat(Constantes.FMT_TextDate.format(solicitud.getFechaSalida())).concat("</strong>"));
        detalle.append(" <strong>".concat(Constantes.FMT_hmm_a.format(solicitud.getHoraSalida())).concat("</strong>"));
        if (solicitud.isRedondo()) {
            detalle.append(", regresando el <strong>".concat(Constantes.FMT_TextDate.format(solicitud.getFechaRegreso())).concat("</strong>"));
            detalle.append(" <strong>".concat(Constantes.FMT_hmm_a.format(solicitud.getHoraRegreso())).concat(".</strong>"));
        } else {
            detalle.append(".");
        }

        detalle.append("</p>");
        detalle.append(mostrarEstatusAprobado("SOLICITÓ", solicitud.getModifico().getNombre(), solicitud.getFechaModifico(), solicitud.getHoraModifico()));
        return detalle.toString();
    }

//    private String mostrarDetalleSolicitudViajeTerrestreCiudad(SgSolicitudViaje solicitud, ViajeDestinoVo viajeDestinoVo) throws SIAException {
//        StringBuilder detalle = new StringBuilder("");
//        detalle.append("<p>Se ha realizado una solicitud del viaje Terrestre <strong>(").append(solicitud.getRedondo().equals(Constantes.BOOLEAN_TRUE) ? "Redondo" : "Sencillo").append(")</strong>  de la oficina <strong>".concat(solicitud.getOficinaOrigen().getNombre()).concat("</strong>"));
//        detalle.append(" a la ciudad de <strong>".concat(viajeDestinoVo.getCiudadDestino()).concat(", ").concat(viajeDestinoVo.getEstadoDestino()).concat(", ").concat(viajeDestinoVo.getPaisDestino()).concat("</strong>"));
//        detalle.append(" saliendo el día <strong>".concat(Constantes.FMT_TextDate.format(solicitud.getFechaSalida())).concat("</strong>"));
//        detalle.append(" <strong>".concat(Constantes.FMT_hmm_a.format(solicitud.getHoraSalida())).concat("</strong>"));
//        if (solicitud.getRedondo().equals(Constantes.BOOLEAN_TRUE)) {
//            detalle.append(", regresando el <strong>".concat(Constantes.FMT_TextDate.format(solicitud.getFechaRegreso())).concat("</strong>"));
//            detalle.append(" <strong>".concat(Constantes.FMT_hmm_a.format(solicitud.getHoraRegreso())).concat(".</strong>"));
//        } else {
//            detalle.append(".");
//        }
//        detalle.append("</p>");
//        detalle.append(mostrarEstatusAprobado("SOLICITÓ", solicitud.getModifico().getNombre(), solicitud.getFechaModifico(), solicitud.getHoraModifico()));
//        return detalle.toString();
//    }
    private String mostrarMotivoCancelacionSolicitudViaje(String motivoCancelacion) {
        UtilLog4j.log.info(this, "mostrarMotivoCancelacionSolicitudViaje");
        StringBuilder html = new StringBuilder("");

        //La fecha de Salida de esta Solicitud no cumple los requisitos de tiempo
        html.append("<table width=\"100%\" cellspacing=\"0\" border=\"0\">");
        html.append("<tr>");
        html.append("<td width=\"100%\" align=\"left\" style=\"font:Arial, Helvetica, sans-serif; font-size:11px;color:gray;font-weight: bold;\">");
        html.append(" Motivo ");
        html.append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append("</td>");
        html.append("<td width=\"100%\" align=\"left\" style=\"font:Ari1al, Helvetica, sans-serif; font-size:11px;color=gray;\">");
        html.append("<p >").append(motivoCancelacion).append("</p>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        return html.toString();
    }

    /**
     * **********Fin Bitacoras de solicitud ***********
     */
    
    public boolean findSgSolicitudConEstatusAprobado(Integer idSolicitud, Integer idEstatus) {
        boolean retVal;

        final String qS
                = "SELECT count(*) <> 0"
                + " FROM sg_estatus_aprobacion  e"
                + " WHERE e.sg_solicitud_viaje = ? AND e.estatus = ?  AND e.historial = 'True' AND e.REALIZADO = 'True'";

        try {

            retVal = dslCtx.fetchOne(qS, idSolicitud, idEstatus).into(boolean.class);

        } catch (Exception e) {
            UtilLog4j.log.error(this, "Excepcion al saber si esta autorizada la soliciitud {0} - {1}", new Object[]{idSolicitud, idEstatus}, e);
            retVal = false;
        }

        return retVal;
    }

    
    public EstatusAprobacionSolicitudVO buscarEstatusAprobacionPorIdSolicitudIdEstatus(Integer idSolicitud, Integer idEstatus) {
        String qS = "SELECT e.id, e.usuario as id_Usuario, e.historial, e.realizado, e.estatus as id_Estatus, e.sg_solicitud_viaje as id_Solicitud \n"
                + "FROM sg_estatus_aprobacion  e \n"
                + "WHERE e.sg_solicitud_viaje = ? AND e.estatus = ?";

        EstatusAprobacionSolicitudVO retVal = null;

        try {
            Record record = dslCtx.fetchOne(qS, idSolicitud, idEstatus); 
            
            if(record != null) {
                retVal = record.into(EstatusAprobacionSolicitudVO.class);
            }

        } catch (Exception e) {
            UtilLog4j.log.error(this, "Excepcion al saber si esta autorizada la solicitud {0} - {1}", new Object[]{idSolicitud, idEstatus}, e);
        }

        return retVal;
    }

    
    public String getUsuarioQueTieneSgSolicitudViaje(int idSgSolicitudViaje) {
        UtilLog4j.log.info(this, "SgEstatusAprobacionImpl.getUsuarioQueTieneSgSolicitudViaje()");

        String query
                = "SELECT u.NOMBRE \n"
                + "FROM SG_ESTATUS_APROBACION ea, USUARIO u \n"
                + " WHERE ea.SG_SOLICITUD_VIAJE=" + idSgSolicitudViaje
                + " AND ea.ELIMINADO='" + Constantes.BOOLEAN_FALSE + "' AND ea.USUARIO=u.ID ORDER BY ea.ID DESC";

        Query q = em.createNativeQuery(query);

        List<String> result = q.getResultList();
        List<EstatusAprobacionVO> list = new ArrayList<EstatusAprobacionVO>();

        EstatusAprobacionVO vo = null;

        for (String o : result) {
            vo = new EstatusAprobacionVO();
            vo.setNombreUsuarioAprobo(o);
            list.add(vo);
        }

        return ((EstatusAprobacionVO) list.get(0)).getNombreUsuarioAprobo();
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    /**
     * **********Fin Bitacoras de solicitud ***********
     */
    
    public EstatusAprobacionVO buscarEstatusAprocionPorSolicitudEstatus(int idSolicitud, int idEstatus) {
        clearQuery();
        try {
            appendQuery("Select e.id, e.usuario, u.nombre, u.email  From SG_ESTATUS_APROBACION  e, usuario u, sg_solicitud_viaje sv");
            appendQuery(" Where e.SG_SOLICITUD_VIAJE = ").append(idSolicitud).append(" AND e.ESTATUS = ").append(idEstatus);
            appendQuery(" and e.usuario = u.id and sv.estatus <> ").append(Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO);
            appendQuery(" and e.sg_solicitud_viaje = sv.id  and sv.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] object = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            EstatusAprobacionVO eavo = null;
            if (object != null) {
                eavo = new EstatusAprobacionVO();
                eavo.setId((Integer) object[0]);
                eavo.setIdUsuarioAprobo((String) object[1]);
                eavo.setNombre((String) object[2]);
                eavo.setCorreoUsuario((String) object[3]);
            }
            return eavo;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al recuperar el usuario de la solicitud " + e);
            return null;
        }
    }

    
    public boolean pasarSolicitudes(List<EstatusAprobacionVO> lo, String usuarioSolicita, String usuarioAprobara, String idSesion,
            String rfcEmpresa, String correoSesion, int idStatus) {
        boolean v;
        Usuario para = usuarioRemote.find(usuarioAprobara);
        Usuario cc = usuarioRemote.find(usuarioSolicita);
        v = notificacionViajeService.correoCambioUsuarioAprobacion(para.getEmail(), cc.getEmail(), lo, para.getNombre(), cc.getNombre(),
                correoSesion, "Aprobar");
        if (v) {
            for (EstatusAprobacionVO estatusAprobacionVO : lo) {
                SgEstatusAprobacion estatusAprobacion = find(estatusAprobacionVO.getId());
                estatusAprobacion.setUsuario(new Usuario(usuarioAprobara));
                estatusAprobacion.setModifico(new Usuario(idSesion));
                estatusAprobacion.setFechaModifico(new Date());
                estatusAprobacion.setHoraModifico(new Date());
                edit(estatusAprobacion);
            }
        }

        return v;
    }

    
    public long contarAprobacionesPendientes(int status, String usuario) {
        clearQuery();
        query.append("select count(*) from SG_ESTATUS_APROBACION ea");
        query.append("      inner join SG_SOLICITUD_VIAJE sv on ea.SG_SOLICITUD_VIAJE = sv.ID");
        query.append("  where  ea.USUARIO = '").append(usuario).append("'");
        query.append("  and ea.ESTATUS = ").append(status);
        query.append("  and ea.REALIZADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  and ea.HISTORIAL = '").append(Constantes.BOOLEAN_FALSE).append("'");
        return ((Long) em.createNativeQuery(query.toString()).getSingleResult());
    }

    
    public long contarViajesCreados() {
        clearQuery();
        query.append("SELECT COUNT(V.ID)");
        query.append(" FROM SG_VIAJE V");
        query.append(" WHERE V.ESTATUS = ").append(Constantes.ESTATUS_VIAJE_CREADO);
        query.append(" AND V.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" AND V.FECHA_PROGRAMADA >= CAST('NOW' AS DATE)");
        return ((Long) em.createNativeQuery(query.toString()).getSingleResult());

    }

    
    public long totalPendiente(String usuario) {
        clearQuery();
        query.append("select count(*) from sg_estatus_aprobacion ea");
        query.append("	    inner join SG_SOLICITUD_VIAJE sv on ea.SG_SOLICITUD_VIAJE = sv.ID and sv.fecha_salida >= current_date and sv.ESTATUS = ea.ESTATUS");
        query.append("  where ((ea.estatus between ").append(Constantes.ESTATUS_PENDIENTE).append(" and  ").append(Constantes.ESTATUS_SEGURIDAD);
        query.append("  and ea.usuario = '").append(usuario).append("'  ) ");
        if (siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_SGYL, "64", Constantes.AP_CAMPO_DEFAULT)) {
            query.append("  or (ea.ESTATUS = ").append(Constantes.ESTATUS_JUSTIFICAR).append(" and ea.usuario is null )  ");
        }
        query.append("  or (ea.ESTATUS = ").append(Constantes.ESTATUS_AUTORIZAR).append(" and ea.usuario = '").append(usuario).append("')) ");
        query.append("  and sv.ESTATUS between  ").append(Constantes.ESTATUS_PENDIENTE).append(" and  ").append(Constantes.ESTATUS_AUTORIZAR);
        query.append("  and ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  and ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
        return ((Long) (em.createNativeQuery(query.toString()).getSingleResult()));
    }

    
    public List<SolicitudViajeVO> totalSolicitudViaje(String sesion) {
        clearQuery();
        query.append("select ea.id, sv.codigo, o.nombre, sv.fecha_salida, sv.hora_salida, m.NOMBRE, sv.SG_TIPO_SOLICITUD_VIAJE,");
        query.append("       sv.OFICINA_DESTINO, sv.SG_RUTA_TERRESTRE , sv.ID, mr.justificacion_retraso, sv.fecha_regreso, sv.hora_regreso,  ");
        query.append("       u.nombre as genero  ");
        query.append(" from sg_estatus_aprobacion ea");
        query.append("          inner join SG_SOLICITUD_VIAJE sv on ea.SG_SOLICITUD_VIAJE = sv.ID and sv.fecha_salida >= current_date ");
        query.append("          inner join sg_oficina o on sv.OFICINA_ORIGEN = o.id ");
        query.append("          inner join sg_motivo m on sv.sg_motivo = m.id ");
        query.append("          inner join usuario u on sv.genero = u.id ");
        query.append("          left  join  sg_motivo_retraso mr on sv.sg_motivo_retraso = mr.id ");
        query.append("  where ((ea.estatus between ").append(Constantes.ESTATUS_PENDIENTE).append(" and  ").append(Constantes.ESTATUS_APROBAR);
        query.append("  and ea.usuario = '").append(sesion).append("'  ) ");
        if (siUsuarioRolRemote.buscarRolPorUsuarioModulo(sesion, Constantes.MODULO_SGYL, "64", Constantes.AP_CAMPO_DEFAULT)) {
            query.append("  or (ea.ESTATUS = ").append(Constantes.ESTATUS_JUSTIFICAR).append(" and ea.usuario is null )  ");
        }
        query.append("  or (ea.ESTATUS = ").append(Constantes.ESTATUS_AUTORIZAR).append(" and ea.usuario = '").append(sesion).append("')) ");
        query.append("  and sv.ESTATUS between  ").append(Constantes.ESTATUS_PENDIENTE).append(" and  ").append(Constantes.ESTATUS_APROBAR);
        query.append("  and ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  and ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");

        List<Object[]> lsol = em.createNativeQuery(query.toString()).getResultList();
        List<SolicitudViajeVO> sol = new ArrayList<>();
        for (Object[] lsol1 : lsol) {
            SolicitudViajeVO svvo = new SolicitudViajeVO();
            svvo.setIdEstatusAprobacion((Integer) lsol1[0]);
            svvo.setCodigo((String) lsol1[1]);
            svvo.setOrigen((String) lsol1[2]);
            svvo.setFechaSalida((Date) lsol1[3]);
            svvo.setHoraSalida((Date) lsol1[4]);
            svvo.setMotivo((String) lsol1[5]);
            svvo.setIdSgTipoSolicitudViaje((Integer) lsol1[6]);
            svvo.setIdOficinaDestino(lsol1[7] != null ? (Integer) lsol1[7] : 0);
            svvo.setIdRutaTerrestre(lsol1[8] != null ? (Integer) lsol1[8] : 0);
            svvo.setIdSolicitud(lsol1[9] != null ? (Integer) lsol1[9] : 0);
            svvo.setJustificacionRetraso(lsol1[10] != null ? (String) lsol1[10] : "");
            svvo.setFechaRegreso(lsol1[11] != null ? (Date) lsol1[11] : null);
            svvo.setHoraRegreso(lsol1[12] != null ? (Date) lsol1[12] : null);
            svvo.setNombreGenero(lsol1[13] != null ? (String) lsol1[13] : "");
            switch (svvo.getIdSgTipoSolicitudViaje()) {
                case Constantes.SOLICITUDES_TERRESTRE:
                    if (svvo.getIdOficinaDestino() > 0) {
                        svvo.setDestino(sgOficinaRemote.buscarPorId(svvo.getIdOficinaDestino()).getNombre());
                    } else {
                        svvo.setDestino(sgDetalleRutaCiudadRemote.buscarDetalleRutaCiudadDestinoPorRuta(svvo.getIdRutaTerrestre()).getCiudad());
                    }
                    break;
                default:
                    svvo.setDestino(sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(svvo.getIdSolicitud(), true, false, "id").getNombreCiudadDestino());
                    break;
            }
            svvo.setViajeros(sgViajeroRemote.getAllViajerosList(svvo.getIdSolicitud()));
            sol.add(svvo);
        }
        return sol;

    }

    private void enviarNotificacion(String titulo, String mensaje, String usuarioDestino) {
        try {
            //
            List<SiUsuarioCodigo> lu = siUsuarioCodigoLocal.buscarPorUsuario(usuarioDestino);
            if (lu != null && !lu.isEmpty()) {
                for (SiUsuarioCodigo lu1 : lu) {
                    FCMSender.notificaciones(titulo, mensaje, lu1.getToken(), Constantes.VIAJE_TOKEN);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OrdenImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public long contarAprobacionesPorRolPendientes(int status, String usuario, int rol) {
        clearQuery();
        query.append("select count(*) from SG_ESTATUS_APROBACION ea");
        query.append("      inner join SG_SOLICITUD_VIAJE sv on ea.SG_SOLICITUD_VIAJE = sv.ID");
        query.append("	where  '").append(usuario).append("' in (SELECT USUARIO from SI_USUARIO_ROL where SI_ROL = ").append(rol).append(")");
        query.append("  and ea.ESTATUS = ").append(status);
        query.append("  and ea.REALIZADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append("  and ea.HISTORIAL = '").append(Constantes.BOOLEAN_FALSE).append("'");
        return ((Long) em.createNativeQuery(query.toString()).getSingleResult());
    }

    
    public List<EstatusAprobacionSolicitudVO> EstatusBySolicitud(int idSV) {
        List<EstatusAprobacionSolicitudVO> le;
        String q = "SELECT ea.id, u.id as idUsuario,\n"
                + "            case when ea.estatus = 435 then 'Con Centops' else  u.nombre end  as usuario,"
                + "            ea.historial,ea.realizado,e.id as idEstatus, "
                + "            case when ea.estatus = 420 and ea.realizado = true then 'APROBADA' ELSE e.nombre END  as operacion,"
                + "            ea.fecha_modifico,ea.hora_modifico"
                + "            from sg_estatus_aprobacion ea\n"
                + "            left JOIN usuario u on u.id = ea.usuario \n"
                + "            INNER join estatus e on e.id = ea.estatus\n"
                + "            where ea.sg_solicitud_viaje = ? and ea.estatus < ?"
                + "            order by ea.estatus";
//        List <Object[]> listOb = em.createNativeQuery(q)
//                .setParameter(1, idSV)
//                .getResultList();

        le = dbCtx.fetch(q, idSV, Constantes.ESTATUS_PARA_HACER_VIAJE).into(EstatusAprobacionSolicitudVO.class);
        return le;
    }
    
    
    public boolean activarSolicitud(int idEstatusAprobacion, String motivo, String idUsuarioRealizo,
            boolean notificar,boolean cancelaViajero, int estatusRegresar){
        SgEstatusAprobacion estatus;
        Usuario u;
        boolean ret = false;
        int solOldEstatus = 0;
        SgEstatusAprobacion oldEstatus;
        try {
            UtilLog4j.log.info(this, "activar SOLICITUD  .....");

            estatus = find(idEstatusAprobacion);
            oldEstatus = estatus;
            u = new Usuario(idUsuarioRealizo);
            if (estatus != null && estatus.getId() > 0 && u != null && u.getId() != null && !u.getId().isEmpty()) {
                solOldEstatus = estatus.getSgSolicitudViaje().getEstatus().getId();
                Date d = new Date();
                
                updateStateSolicitudViaje(estatus.getSgSolicitudViaje().getId(), estatusRegresar, u.getId(),d);
                estatus.setHistorial(Constantes.FALSE);
                estatus.setFechaModifico(new Date());
                estatus.setHoraModifico(new Date());
                estatus.setUsuario(u);
                edit(estatus);
                SolicitudViajeVO solicitudViaje = sgSolicitudViajeRemote.buscarPorId(estatus.getSgSolicitudViaje().getId(), Constantes.NO_ELIMINADO, Constantes.CERO);
                UsuarioResponsableGerenciaVo urgv = 
                        gerenciaRemote.traerResponsablePorApCampoYGerencia(
                                estatus.getSgSolicitudViaje().getApCampo().getId(), solicitudViaje.getIdGerencia());

                if (notificacionViajeService.enviarCorreoSolicitudViajeReactivada(estatus, motivo,
                        estatus.getUsuario(), notificar, solicitudViaje, urgv.getNombreUsuario(), cancelaViajero)) {
                    //enviar al log
                    guardarSiMovimiento(estatus.getSgSolicitudViaje().getId(), motivo, siOperacionService.find(3), u.getId());
                    UtilLog4j.log.info(this, "se envio al movimiento");
                    //Publicar Noticia
                    //cancelRequestViajeNews(estatus.getSgSolicitudViaje(), motivo, u.getId());
                    ret = true;
                } else {
                    if (!ret && solOldEstatus > 0) {
                        this.edit(oldEstatus);
                        updateStateSolicitudViaje(oldEstatus.getSgSolicitudViaje().getId(), solOldEstatus, u.getId(),d);
                    }
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error al tratar de cancelar la solicitud de viaje desde panta de aprobacion", e);
            ret = false;
        }
        return ret;
    }
}
