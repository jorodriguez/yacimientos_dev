/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import com.newrelic.api.agent.Trace;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.ApCampo;
import sia.modelo.Gerencia;
import sia.modelo.SgItinerario;
import sia.modelo.SgMotivoRetraso;
import sia.modelo.SgOficina;
import sia.modelo.SgRutaTerrestre;
import sia.modelo.SgSemaforo;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgTipoSolicitudViaje;
import sia.modelo.SgViajero;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.JustIncumSolVo;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.sgl.vo.SiCiudadVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgDireccionImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgJustIncumpSolImpl;
import sia.servicios.sgl.impl.SgMotivoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoSolicitudViajeImpl;
import sia.servicios.sgl.impl.SgViajeCiudadImpl;
import sia.servicios.sgl.semaforo.impl.SgSemaforoImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgSolicitudViajeImpl extends AbstractFacade<SgSolicitudViaje> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgTipoSolicitudViajeImpl sgTipoSolicitudViajeRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SgMotivoImpl sgMotivoRemote;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private EstatusImpl estatusService;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private SgInvitadoImpl sgInvitadoRemote;
    @Inject
    private SiCiudadImpl siCiudadRemote;
    @Inject
    private SgItinerarioImpl sgItinerarioRemote;
    @Inject
    private SgViajeCiudadImpl viajeCiudadRemote;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreRemote;
    @Inject
    private SgMotivoRetrasoImpl sgMotivoRetrasoRemote;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionRemote;
    @Inject
    private SgSolViajeIncumImpl sgSolViajeIncumRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SgJustIncumpSolImpl sgJustIncumpSolRemote;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaRemote;
    @Inject
    private SgSolicitudViajeSiMovimientoImpl sgSolicitudViajeSiMovimientoRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreRemote;
    @Inject
    private SgSemaforoImpl sgSemaforoRemote;
    @Inject
    private SgDireccionImpl sgDireccionRemote;
    @Inject
    private ApCampoImpl apCampoRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;

    private String beforeEvent;
    private final int idSiEventoCrear = 1;
    private final int idSiEventoModificar = 2;
    private final int idSiEventoEliminar = 3;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgSolicitudViajeImpl() {
        super(SgSolicitudViaje.class);
    }

    private Usuario getResponsableByGerencia(int idGerencia) {
        return this.gerenciaRemote.getResponsableByApCampoAndGerencia(1, idGerencia, false);
    }

    
    public int save(int idSgTipoSolicitudViaje, int idGerencia, int idOficinaOrigen, int idRutaTerrestre, int idSgMotivo,
            String observacion, Date fechaSalida, Date fechaRegreso, int idSiCiudadOrigen, int idSiCiudadDestino,
            boolean opcionPropia, boolean withEstancia, String idUsuario, int idRol, boolean redondo, int idOficinaDestino, int idEstatus, boolean conChofer
    ) {
        int valRet = 0;
        try {
            SolicitudViajeVO solicitudViajeVO = new SolicitudViajeVO();
            solicitudViajeVO.setIdSgTipoSolicitudViaje(idSgTipoSolicitudViaje);
            solicitudViajeVO.setIdGerencia(idGerencia);
            solicitudViajeVO.setIdOficinaOrigen(idOficinaOrigen);
            solicitudViajeVO.setIdRutaTerrestre(idRutaTerrestre);
            solicitudViajeVO.setIdSgMotivo(idSgMotivo);
            solicitudViajeVO.setObservacion(observacion);
            solicitudViajeVO.setFechaSalida(fechaSalida);
            solicitudViajeVO.setFechaRegreso(fechaRegreso);
            solicitudViajeVO.setIdSiCiudadOrigen(idSiCiudadOrigen);
            solicitudViajeVO.setIdSiCiudadDestino(idSiCiudadDestino);
            solicitudViajeVO.setGenero(idUsuario);
            solicitudViajeVO.setRedondo(redondo);
            solicitudViajeVO.setIdOficinaDestino(idOficinaDestino);
            solicitudViajeVO.setConChofer(conChofer);

            valRet = this.save(solicitudViajeVO, opcionPropia, withEstancia, idEstatus);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            valRet = 0;
        }
        return valRet;
    }

    
    public int save(SolicitudViajeVO solicitudViajeVO, boolean opcionPropia, boolean withEstancia, int idEstatus) {
        try {
            Usuario usuario = new Usuario(solicitudViajeVO.getGenero());
            SgTipoSolicitudViaje sgTipoSolicitudViaje = this.sgTipoSolicitudViajeRemote.find(solicitudViajeVO.getIdSgTipoSolicitudViaje());
            int idTipoEspecifico = sgTipoSolicitudViaje.getSgTipoEspecifico().getId();

            SgSolicitudViaje sgSolicitudViaje = new SgSolicitudViaje();
            if (idEstatus > 0) {
                sgSolicitudViaje.setEstatus(this.estatusService.find(idEstatus));
            } else {
                sgSolicitudViaje.setEstatus(this.estatusService.find(401));
            }
            sgSolicitudViaje.setGenero(usuario);
            sgSolicitudViaje.setFechaGenero(new Date());
            sgSolicitudViaje.setHoraGenero(new Date());

            sgSolicitudViaje.setOficinaOrigen(this.sgOficinaRemote.find(solicitudViajeVO.getIdOficinaOrigen()));
            sgSolicitudViaje.setSgTipoSolicitudViaje(sgTipoSolicitudViaje);
            sgSolicitudViaje.setGerenciaResponsable(gerenciaRemote.find(solicitudViajeVO.getIdGerencia()));
            sgSolicitudViaje.setFechaSalida(solicitudViajeVO.getFechaSalida());
            sgSolicitudViaje.setHoraSalida(solicitudViajeVO.getFechaSalida());
            sgSolicitudViaje.setObservacion(solicitudViajeVO.getObservacion());
            sgSolicitudViaje.setEliminado(Constantes.NO_ELIMINADO);
            sgSolicitudViaje.setConChofer((solicitudViajeVO.isConChofer()));

            // Con regreso
            UtilLog4j.log.info(this, "con regreso: " + String.valueOf(solicitudViajeVO.isRedondo()));
            if (solicitudViajeVO.isRedondo()) {
                sgSolicitudViaje.setFechaRegreso(solicitudViajeVO.getFechaRegreso());
                sgSolicitudViaje.setHoraRegreso(solicitudViajeVO.getFechaRegreso());
                sgSolicitudViaje.setRedondo(Constantes.BOOLEAN_TRUE);
            } else {
                sgSolicitudViaje.setRedondo(Constantes.BOOLEAN_FALSE);
                sgSolicitudViaje.setFechaRegreso(null);
                sgSolicitudViaje.setHoraRegreso(null);
            }

            //Seleccion del tab ofician - ciduda
            if (idTipoEspecifico == 2) { //Solicitud de Viaje terrestre
                //Cambio por la ruta destino
                if (solicitudViajeVO.getIdOficinaDestino() > 0) { //Viaje a oficina
                    sgSolicitudViaje.setOficinaDestino(sgOficinaRemote.find(solicitudViajeVO.getIdOficinaDestino()));//Agrega la ofician destino
                    sgSolicitudViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.RUTA_TIPO_OFICINA));
                } else { //Viaje a ciudad
                    sgSolicitudViaje.setOficinaDestino(null);
                    sgSolicitudViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.RUTA_TIPO_CIUDAD));
                }
                sgSolicitudViaje.setSgRutaTerrestre(sgRutaTerrestreRemote.find(solicitudViajeVO.getIdRutaTerrestre()));
                sgSolicitudViaje.setSgMotivo(this.sgMotivoRemote.find(solicitudViajeVO.getIdSgMotivo()));
            } else if (idTipoEspecifico == 3) {
                //buscar el destino en el detalle de la ruta
                sgSolicitudViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.SOLICITUDES_AEREA));
                sgSolicitudViaje.setOficinaOrigen(sgOficinaRemote.find(Constantes.ID_OFICINA_TORRE_MARTEL));
                sgSolicitudViaje.setOficinaDestino(null);//Agrega el semaforo
                //sgSolicitudViaje.setSgEstadoSemaforo(null);
                sgSolicitudViaje.setSgRutaTerrestre(null);
                sgSolicitudViaje.setSgMotivo(null);
            }
            //Crea la solicitud
            create(sgSolicitudViaje);

            // A ciudad
            if (solicitudViajeVO.getIdOficinaDestino() == 0) {
                //Guarda en viaje-ciudad
                viajeCiudadRemote.crear(sgSolicitudViaje.getId(), solicitudViajeVO.getIdSiCiudadOrigen(), solicitudViajeVO.getGenero());
            }

            //Crear el Itinerario de Vuelo
            if (idTipoEspecifico == 3) { //Solicitud de Viaje aérea
                //SgItinerario de Ida

                SgItinerario sgItinerario = new SgItinerario();

                sgItinerario.setSiCiudadOrigen(this.siCiudadRemote.find(solicitudViajeVO.getIdSiCiudadOrigen()));
                sgItinerario.setSiCiudadDestino(this.siCiudadRemote.find(solicitudViajeVO.getIdSiCiudadDestino()));
                sgItinerario.setSgSolicitudViaje(sgSolicitudViaje);
                sgItinerario.setIda(Constantes.BOOLEAN_TRUE);
                sgItinerarioRemote.save(sgItinerario, solicitudViajeVO.getGenero());

                //SgItinerairo de Vuelta
                SgItinerario sgItinerarioVuelta = new SgItinerario();
                sgItinerarioVuelta.setSiCiudadOrigen(this.siCiudadRemote.find(solicitudViajeVO.getIdSiCiudadDestino()));
                sgItinerarioVuelta.setSiCiudadDestino(this.siCiudadRemote.find(solicitudViajeVO.getIdSiCiudadOrigen()));
                sgItinerarioVuelta.setSgSolicitudViaje(sgSolicitudViaje);
                sgItinerarioVuelta.setIda(Constantes.BOOLEAN_FALSE);
                sgItinerarioRemote.save(sgItinerarioVuelta, solicitudViajeVO.getGenero());

            }

            //Registrar Integrante de la Solicitud automáticamente
            if (opcionPropia) {
                try {
                    SgViajero sgViajero = new SgViajero();
                    sgViajero.setSgSolicitudViaje(sgSolicitudViaje);
                    sgViajero.setUsuario(new Usuario(solicitudViajeVO.getGenero()));
                    sgViajero.setEstancia(withEstancia ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                    this.sgViajeroRemote.save(sgViajero, solicitudViajeVO.getGenero());

                } catch (Exception e) {
                    e.getStackTrace();
                    //throw new SIAException();
                }
            } else {
                List<ViajeroVO> lv = sgViajeroRemote.getAllViajerosList(sgSolicitudViaje.getId());
                for (ViajeroVO viajeroVO : lv) {
                    if (viajeroVO.getIdUsuario().equals(solicitudViajeVO.getGenero())) {
                        sgViajeroRemote.delete(viajeroVO.getId(), solicitudViajeVO.getGenero(), "");
                        break;
                    }
                }
            }
            return sgSolicitudViaje.getId();
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            UtilLog4j.log.info(this, "e al guardar o modificar la sol viaje: " + e.getMessage());// + "Causa : " + e.getCause() + " Pila: " + e.getStackTrace());
            return 0;
        }
    }

    
    public boolean modificarSolicitud(int idSgTipoSolicitudViaje, int idGerencia, int idOficinaOrigen, int idRuta, int idSgMotivo,
            String observacion, Date fechaSalida, Date fechaRegreso, int idSiCiudadOrigen, int idSiCiudadDestino,
            String opcionPropia, boolean withEstancia, String idUsuario, int idRol, String conRegreso, int idDestinoRuta, int idSolicitud,
            List<SgDetalleRutaTerrestreVo> ldrt
    ) {
        SgSolicitudViaje sgSolicitudViaje = find(idSolicitud);
        boolean v = false;
        try {

            Usuario usuario = new Usuario(idUsuario);
            SgTipoSolicitudViaje sgTipoSolicitudViaje = this.sgTipoSolicitudViajeRemote.find(idSgTipoSolicitudViaje);
            // int idTipoEspecifico = sgTipoSolicitudViaje.getSgTipoEspecifico().getId();
            sgSolicitudViaje.setGerenciaResponsable(gerenciaRemote.find(idGerencia));
            if (idOficinaOrigen < 1000 && idSiCiudadOrigen == -1) {
                idSiCiudadOrigen = sgDireccionRemote.find(sgOficinaRemote.find(idOficinaOrigen).getSgDireccion().getId()).getSiCiudad().getId();
            }

            sgSolicitudViaje.setSgTipoSolicitudViaje(sgTipoSolicitudViaje);
            sgSolicitudViaje.setFechaSalida(fechaSalida);
            sgSolicitudViaje.setHoraSalida(fechaSalida);
            sgSolicitudViaje.setObservacion(observacion);
            sgSolicitudViaje.setModifico(usuario);
            sgSolicitudViaje.setFechaModifico(new Date());
            sgSolicitudViaje.setHoraModifico(new Date());
            // Con regreso
            if (conRegreso.equals(Constantes.redondo)) {
                sgSolicitudViaje.setFechaRegreso(fechaRegreso);
                sgSolicitudViaje.setHoraRegreso(fechaRegreso);
                sgSolicitudViaje.setRedondo(Constantes.BOOLEAN_TRUE);
            } else {
                sgSolicitudViaje.setRedondo(Constantes.BOOLEAN_FALSE);
                sgSolicitudViaje.setFechaRegreso(null);
                sgSolicitudViaje.setHoraRegreso(null);
            }

            int idDestinoLocal = 0;
            //Seleccion del tab ofician - ciduda
            if (idDestinoRuta == Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
                //buscar el destino en el detalle de la ruta
                sgSolicitudViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.SOLICITUDES_AEREA));
                sgSolicitudViaje.setOficinaOrigen(sgOficinaRemote.find(Constantes.ID_OFICINA_TORRE_MARTEL));
                sgSolicitudViaje.setOficinaDestino(null);//Agrega el semaforo
                //sgSolicitudViaje.setSgEstadoSemaforo(null);
                sgSolicitudViaje.setSgRutaTerrestre(null);
            } else {
                //Cambio por la ruta destino
                SgRutaTerrestre ruta = sgRutaTerrestreRemote.find(idRuta);
                idDestinoRuta = ruta.getSgTipoEspecifico().getId();

                if (idDestinoRuta == Constantes.RUTA_TIPO_OFICINA) { //Viaje a oficina
                    sgSolicitudViaje.setOficinaDestino(sgOficinaRemote.find(sgDetalleRutaTerrestreRemote.buscarDetalleRutaTerrestreDestinoPorRuta(ruta.getId()).getIdSgOficina()));//Agrega la ofician destino
                    UtilLog4j.log.info(this, "modifica a oficina");
                    sgSolicitudViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idDestinoRuta));
                } else if (idDestinoRuta == Constantes.RUTA_TIPO_CIUDAD) { //Viaje a ciudad
                    sgSolicitudViaje.setOficinaDestino(null);
                    UtilLog4j.log.info(this, "modifica a ciudad");
                    sgSolicitudViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idDestinoRuta));

                }
                sgSolicitudViaje.setSgRutaTerrestre(ruta);
                sgSolicitudViaje.setOficinaOrigen(this.sgOficinaRemote.find(idOficinaOrigen));

            }
            sgSolicitudViaje.setSgMotivo(this.sgMotivoRemote.find(idSgMotivo));
            edit(sgSolicitudViaje);

            //Crear el Itinerario de Vuelo
            if (sgSolicitudViaje.getSgTipoEspecifico().getId() == Constantes.SOLICITUDES_AEREA) { //Solicitud de Viaje aérea
                sgSolicitudViaje.setOficinaOrigen(this.sgOficinaRemote.find(1));
                ItinerarioCompletoVo icvo = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(sgSolicitudViaje.getId(), true, false, "id");
                if (icvo == null) {
                    if (conRegreso.equals(Constantes.redondo)) {
                        SgItinerario sgItinerario = new SgItinerario();
                        sgItinerario.setSiCiudadOrigen(siCiudadRemote.find(idSiCiudadOrigen));
                        sgItinerario.setSiCiudadDestino(siCiudadRemote.find(idSiCiudadDestino));
                        sgItinerario.setSgSolicitudViaje(find(sgSolicitudViaje.getId()));
                        sgItinerario.setIda(Constantes.BOOLEAN_TRUE);
                        sgItinerarioRemote.save(sgItinerario, idUsuario);

                        //SgItinerairo de Vuelta
                        SgItinerario sgItinerarioVuelta = new SgItinerario();
                        sgItinerarioVuelta.setSiCiudadOrigen(this.siCiudadRemote.find(idSiCiudadDestino));
                        sgItinerarioVuelta.setSiCiudadDestino(this.siCiudadRemote.find(idSiCiudadOrigen));
                        sgItinerarioVuelta.setSgSolicitudViaje(find(sgSolicitudViaje.getId()));
                        sgItinerarioVuelta.setIda(Constantes.BOOLEAN_FALSE);
                        sgItinerarioRemote.save(sgItinerarioVuelta, idUsuario);
                    } else { // senciillo
                        SgItinerario sgItinerario = new SgItinerario();
                        sgItinerario.setSiCiudadOrigen(siCiudadRemote.find(idSiCiudadOrigen));
                        sgItinerario.setSiCiudadDestino(siCiudadRemote.find(idSiCiudadDestino));
                        sgItinerario.setSgSolicitudViaje(find(sgSolicitudViaje.getId()));
                        sgItinerario.setIda(Constantes.BOOLEAN_TRUE);
                        sgItinerarioRemote.save(sgItinerario, idUsuario);
                    }
                } else {
                    //SgItinerario de Ida
                    UtilLog4j.log.info(this, "id origen aereo: " + idSiCiudadOrigen);
                    if (conRegreso.equals(Constantes.redondo)) {
                        ItinerarioCompletoVo icvoVuelta = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(idSolicitud, false, false, "id");
                        UtilLog4j.log.info(this, "id origen it aereo: " + icvo.getIdCiudadOrigen());
                        if (icvo.getIdCiudadOrigen() != idSiCiudadOrigen || icvo.getIdCiudadDestino() != idSiCiudadDestino) {
                            sgItinerarioRemote.update(icvo.getId(), idSiCiudadOrigen, idSiCiudadDestino, idUsuario);
                            sgItinerarioRemote.update(icvoVuelta.getId(), idSiCiudadDestino, idSiCiudadOrigen, idUsuario);
                        }
                    } else {
                        sgItinerarioRemote.update(icvo.getId(), idSiCiudadOrigen, idSiCiudadDestino, idUsuario);
                        ItinerarioCompletoVo icvoVue = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(idSolicitud, false, false, "id");
                        sgItinerarioRemote.delete(icvoVue.getId(), idUsuario);
                    }
                }
                sgSolicitudViaje.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA));
            }

            //Registrar Integrante de la Solicitud automáticamente
            if (opcionPropia.equals(Constantes.OPCION_PROPIA)) {
                try {
                    boolean va = false;
                    if ((sgViajeroRemote.sgViajeroByUsuarioAndSgSolicitudViaje(usuario.getId(), sgSolicitudViaje.getId(), Constantes.ELIMINADO) != null)
                            || !sgViajeroRemote.existSgViajeroByUsuarioAndSgSolicitudViaje(usuario, sgSolicitudViaje)) {
                        va = true;
                    } else {
                        List<ViajeroVO> lv = sgViajeroRemote.getAllViajerosList(idSolicitud);
                        for (ViajeroVO viajeroVO : lv) {
                            if (viajeroVO.getIdUsuario().equals(idUsuario)) {
                                va = true;
                                break;   
                            }
                        }
                    }

                    if (va == false) {
                        SgViajero sgViajero = new SgViajero();
                        sgViajero.setSgSolicitudViaje(sgSolicitudViaje);
                        sgViajero.setUsuario(new Usuario(idUsuario));
                        sgViajero.setEstancia(withEstancia ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                        this.sgViajeroRemote.save(sgViajero, idUsuario);
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                    UtilLog4j.log.info(this, "Error: al modificar a solicitud: " + e.getMessage());
                    //throw new SIAException();
                }
            } else {
                List<ViajeroVO> lv = sgViajeroRemote.getAllViajerosList(sgSolicitudViaje.getId());
                for (ViajeroVO viajeroVO : lv) {
                    if (viajeroVO.getIdUsuario().equals(idUsuario)) {
                        sgViajeroRemote.delete(viajeroVO.getId(), idUsuario, "");
                        break;
                    }
                }
            }
            v = true;
        } catch (Exception e) {
            e.getStackTrace();
            UtilLog4j.log.info(this, "e al guardar o modificar la sol viaje: " + e.getMessage() + "Causa : " + e.getCause() + " Pila: " + e.getStackTrace());
            v = false;
        }
        return v;
    }

    
    public void update(SgSolicitudViaje solicitudViaje, SgOficina oficinaOrigen, int idGerencia, int idOficinaOrigen, int idOficinaDestino,
            int idTipoSolicitudViaje, int idMotivo, boolean isViajeAereo, boolean isGerente, SiCiudadVO siCiudadOrigen,
            SiCiudadVO siCiudadDestino, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.update2()");

        solicitudViaje.setOficinaOrigen(this.sgOficinaRemote.find(idOficinaOrigen));
        solicitudViaje.setGerenciaResponsable(this.gerenciaRemote.find(idGerencia));
        solicitudViaje.setSgMotivo(this.sgMotivoRemote.find(idMotivo));
        if (isViajeAereo) {
            solicitudViaje.setSgTipoSolicitudViaje(this.sgTipoSolicitudViajeRemote.find(idTipoSolicitudViaje));
            if (solicitudViaje.getOficinaDestino() != null) { //Los Viajes Aéreos no tienen Oficina Destino
                solicitudViaje.setOficinaDestino(null);
            }

            //***SgItinerario sgItinerario = this.sgItinerarioRemote.findBySolicitudViaje(solicitudViaje, true, false);
            ItinerarioCompletoVo itinerarioCompleto = this.sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(solicitudViaje.getId(), true, false, "id");
            SgItinerario sgItinerario = sgItinerarioRemote.find(itinerarioCompleto.getId());

            if (sgItinerario != null) { //Si la Solicitud ya tiene un Itinerario, entonces solo actualizarlo con el nuevo origen y destino
                /**
                 * sgItinerario.setSiCiudadOrigen(this.siCiudadRemote.find(siCiudadOrigen.getId()));
                 * sgItinerario.setSiCiudadDestino(this.siCiudadRemote.find(siCiudadDestino.getId()));
                 * this.sgItinerarioRemote.update(sgItinerario, idUsuario);
                 */
                this.sgItinerarioRemote.update(sgItinerario.getId(), siCiudadOrigen.getId(), siCiudadDestino.getId(), idUsuario);
            } else { //Si la Solicitud no tiene Itinerario crearlo
                sgItinerario = new SgItinerario();
                sgItinerario.setSiCiudadOrigen(this.siCiudadRemote.find(siCiudadOrigen.getId()));
                sgItinerario.setSiCiudadDestino(this.siCiudadRemote.find(siCiudadDestino.getId()));
                sgItinerario.setSgSolicitudViaje(solicitudViaje);
                this.sgItinerarioRemote.save(sgItinerario, idUsuario);
            }
        } else {
            solicitudViaje.setSgTipoSolicitudViaje(sgTipoSolicitudViajeRemote.find(1));
            solicitudViaje.setOficinaDestino(this.sgOficinaRemote.find(idOficinaDestino));
        }

        solicitudViaje.setModifico(new Usuario(idUsuario));
        solicitudViaje.setFechaModifico(new Date());
        solicitudViaje.setHoraModifico(new Date());

        edit(solicitudViaje);
        UtilLog4j.log.info(this, "SgSolicitudViaje UPDATED SUCCESSFULLY");
    }

    
    public void update(int idSgSolicitudViaje, int idSgTipoSolicitudViaje, int idGerencia, int idOficinaOrigen, int idOficinaDestino, int idSgMotivo, String observacion, Date fechaSalida, Date fechaRegreso, int idSiCiudadOrigen, int idSiCiudadDestino, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.update()");

        SgSolicitudViaje sgSolicitudViaje = find(idSgSolicitudViaje);

        ItinerarioCompletoVo itinerarioIda = this.sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(sgSolicitudViaje.getId(), true, false, "id");
        SgItinerario sgItinerarioIda = this.sgItinerarioRemote.find(itinerarioIda.getId());

        ItinerarioCompletoVo itinerarioVuelta = this.sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(sgSolicitudViaje.getId(), false, false, "id");
        SgItinerario sgItinerarioVuelta = this.sgItinerarioRemote.find(itinerarioVuelta.getId());

        sgSolicitudViaje.setObservacion(observacion);
        sgSolicitudViaje.setFechaSalida(fechaSalida);
        sgSolicitudViaje.setFechaRegreso(fechaRegreso);
        sgSolicitudViaje.setHoraSalida(fechaSalida);
        sgSolicitudViaje.setHoraRegreso(fechaRegreso);
        sgSolicitudViaje.setOficinaOrigen(this.sgOficinaRemote.find(idOficinaOrigen));
        if (idOficinaDestino > 0) {
            sgSolicitudViaje.setOficinaDestino(this.sgOficinaRemote.find(idOficinaDestino));
        }
        sgSolicitudViaje.setGerenciaResponsable(this.gerenciaRemote.find(idGerencia));
        sgSolicitudViaje.setSgTipoSolicitudViaje(this.sgTipoSolicitudViajeRemote.find(idSgTipoSolicitudViaje));
        sgSolicitudViaje.setSgMotivo(this.sgMotivoRemote.find(idSgMotivo));
        sgSolicitudViaje.setModifico(new Usuario(idUsuario));
        sgSolicitudViaje.setFechaModifico(new Date());
        sgSolicitudViaje.setHoraModifico(new Date());

        UtilLog4j.log.info(this, "solicitudViaje: " + sgSolicitudViaje);

        //Actualizar los Itinerarios
        if (sgSolicitudViaje.getSgTipoSolicitudViaje().getSgTipoEspecifico().getId() == 3) { //Solo si es SVA
            /*
	     * sgItinerarioIda.setSiCiudadOrigen(this.siCiudadRemote.find(idSiCiudadOrigen));
	     * sgItinerarioIda.setSiCiudadDestino(this.siCiudadRemote.find(idSiCiudadDestino));
	     * this.sgItinerarioRemote.update(sgItinerarioIda, idUsuario);
             */
            sgItinerarioRemote.update(sgItinerarioIda.getId(), idSiCiudadOrigen, idSiCiudadDestino, idUsuario);


            /*
	     * sgItinerarioVuelta.setSiCiudadOrigen(this.siCiudadRemote.find(idSiCiudadDestino));
	     * sgItinerarioVuelta.setSiCiudadDestino(this.siCiudadRemote.find(idSiCiudadOrigen));
	     * this.sgItinerarioRemote.update(sgItinerarioVuelta, idUsuario);
             */
            sgItinerarioRemote.update(sgItinerarioVuelta.getId(), idSiCiudadDestino, idSiCiudadOrigen, idUsuario);
        }

        edit(sgSolicitudViaje);
        UtilLog4j.log.info(this, "SgSolicitudViaje UPDATED SUCCESSFULLY");
    }

    
    public boolean guardarJustificacionSolicitudViaje(int idSolicitudViaje, String justificacionMotivo, int idLugar, int idInvitado, Date horaReunion, String idUsuario
    ) {
        UtilLog4j.log.info(this, "guardarJustificacionSolicitudViaje");
        UtilLog4j.log.info(this, "solicitud de viaje " + idSolicitudViaje);
        UtilLog4j.log.info(this, "justificacion " + justificacionMotivo);

        try {
            SgSolicitudViaje solicitudEncontrada = find(idSolicitudViaje);
            if (solicitudEncontrada != null) {
                UtilLog4j.log.info(this, "Solicitud encontrada");

                //guardar el motivo de retraso
                SgMotivoRetraso motivo = sgMotivoRetrasoRemote.save(justificacionMotivo, horaReunion, idInvitado, idLugar, idUsuario, 0, "");
                if (motivo != null) {
                    solicitudEncontrada.setSgMotivoRetraso(sgMotivoRetrasoRemote.find(motivo.getId()));
                    solicitudEncontrada.setModifico(new Usuario(idUsuario));
                    solicitudEncontrada.setHoraModifico(new Date());
                    solicitudEncontrada.setFechaModifico(new Date());
                    edit(solicitudEncontrada);
                    UtilLog4j.log.info(this, "SgSolicitudViaje justificada");
                } else {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al justificar la solicitud de viaje " + e.getMessage());
            return false;
        }
    }

    
    public void delete(int idSgSolicitudViaje, String idUsuario
    ) {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.delete()");

        SgSolicitudViaje sgSolicitudViaje = find(idSgSolicitudViaje);
        try {
            List<SgViajero> viajerosFromSolicitudViaje = this.sgViajeroRemote.getViajerosBySolicitudViajeList(sgSolicitudViaje, false);

            for (SgViajero v : viajerosFromSolicitudViaje) {
                this.sgViajeroRemote.delete(v.getId(), idUsuario, "");
            }

            //Si es una SgSolicitudViaje aérea, eliminar sus SgItinerarios
            if (sgSolicitudViaje.getSgTipoSolicitudViaje().getSgTipoEspecifico().getId() == 3) {
                //Eliminar Itineario de Ida
                //SgItinerario sgItinerario = this.sgItinerarioRemote.findBySolicitudViaje(sgSolicitudViaje, true, false);
                ItinerarioCompletoVo itinerarioCompleto = this.sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(sgSolicitudViaje.getId(), true, false, "id");

                if (itinerarioCompleto != null) {
                    this.sgItinerarioRemote.delete(itinerarioCompleto.getId(), idUsuario);
                }

                //Eliminar Itineario de Vuelta
                /*
		 * sgItinerario =
		 * this.sgItinerarioRemote.findBySolicitudViaje(sgSolicitudViaje,
		 * false, false);
                 */
                ItinerarioCompletoVo itinerarioCompletoVuelta = this.sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(sgSolicitudViaje.getId(), false, false, "id");
                if (itinerarioCompletoVuelta != null) {
                    this.sgItinerarioRemote.delete(itinerarioCompletoVuelta.getId(), idUsuario);
                }
            }
        } catch (SIAException siae) {
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
        }

        sgSolicitudViaje.setModifico(new Usuario(idUsuario));
        sgSolicitudViaje.setFechaModifico(new Date());
        sgSolicitudViaje.setHoraModifico(new Date());
        sgSolicitudViaje.setEliminado(Constantes.ELIMINADO);

        edit(sgSolicitudViaje);
        UtilLog4j.log.info(this, "SgSolicitudViaje DELETED SUCCESSFULLY");
    }

    
    public SgSolicitudViaje update(SgSolicitudViaje solicitudViaje, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.update()");

        solicitudViaje.setModifico(new Usuario(idUsuario));
        solicitudViaje.setFechaModifico(new Date());
        solicitudViaje.setHoraModifico(new Date());

        edit(solicitudViaje);
        UtilLog4j.log.info(this, "SgSolicitudViaje UPDATED SUCCESSFULLY");

        return solicitudViaje;
    }

    
    public void updateSolicitudCiudad(int idSgSolicitudViaje, int idCiudadDestino, int idSgMotivo, String observacion, Date fechaSalida, Date fechaRegreso, String idUsuario) throws SIAException {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.updateSolicitudCiudad()");
        try {
            SgSolicitudViaje sgSolicitudViaje = find(idSgSolicitudViaje);

            sgSolicitudViaje.setObservacion(observacion);
            sgSolicitudViaje.setFechaSalida(fechaSalida);
            sgSolicitudViaje.setFechaRegreso(fechaRegreso);
            sgSolicitudViaje.setHoraSalida(fechaSalida);
            sgSolicitudViaje.setHoraRegreso(fechaRegreso);

            sgSolicitudViaje.setSgMotivo(this.sgMotivoRemote.find(idSgMotivo));
            sgSolicitudViaje.setModifico(new Usuario(idUsuario));
            sgSolicitudViaje.setFechaModifico(new Date());
            sgSolicitudViaje.setHoraModifico(new Date());

            UtilLog4j.log.info(this, "modificar la solicitudViaje: " + sgSolicitudViaje);

            edit(sgSolicitudViaje);
            UtilLog4j.log.info(this, "SgSolicitudViajeciudad UPDATED SUCCESSFULLY");
            UtilLog4j.log.info(this, "idSol" + sgSolicitudViaje.getId());
            UtilLog4j.log.info(this, "idciudad " + idCiudadDestino);
            UtilLog4j.log.info(this, "usuario" + idUsuario);
            if (viajeCiudadRemote.modificar(sgSolicitudViaje.getId(), idCiudadDestino, idUsuario)) {
                UtilLog4j.log.info(this, "Ciudad destino modificada.. ");
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepction al modificar la silicitud de viaje " + e.getMessage());
        }
    }

    /**
     * **************************************************************
     */
    
    public int getTotalSgSolicitudViajeByEstatusAndUsuario(String idUsuario, int idEstatus
    ) {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.getTotalSgSolicitudViaje401()");
        try {
            Query q = em.createNativeQuery("SELECT COUNT(sv.id) FROM SG_SOLICITUD_VIAJE sv"
                    + " WHERE sv.estatus = " + idEstatus
                    + " AND sv.genero = '" + idUsuario + "'"
                    + " AND sv.eliminado = '" + Constantes.NO_ELIMINADO + "'"
                    + " AND not exists(select vc.SG_SOLICITUD_VIAJE from SG_VIAJE_CIUDAD vc where vc.SG_SOLICITUD_VIAJE = sv.id)");

//            UtilLog4j.log.info(this, "Query " + q.toString());
            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "SgSolicitudViajeImpl.getTotalSgSolicitudViaje401()" + e.getMessage());
            return 0;
        }
    }

    
    public int getTotalSgSolicitudViajeCiudadByEstatusAndUsuario(String idUsuario, int idEstatus
    ) {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.getTotalSgSolicitudViaje401()");
        try {
            Query q = em.createNativeQuery("SELECT COUNT(sv.id) "
                    + " FROM "
                    + " SG_SOLICITUD_VIAJE sv,"
                    + " SG_TIPO_SOLICITUD_VIAJE tsv, "
                    + " SG_TIPO_ESPECIFICO te,"
                    + " ESTATUS e, "
                    + " GERENCIA g, "
                    + " SG_MOTIVO m, "
                    + " SG_OFICINA oo,"
                    + " SG_VIAJE_CIUDAD vc,"
                    + " SI_CIUDAD ciu"
                    + " WHERE "
                    + " sv.ESTATUS = " + idEstatus
                    + " AND sv.GENERO = '" + idUsuario + "' "
                    + " AND sv.ESTATUS = e.ID "
                    + " AND sv.OFICINA_ORIGEN = oo.ID "
                    + " AND sv.GERENCIA_RESPONSABLE = g.ID "
                    + " AND sv.SG_TIPO_SOLICITUD_VIAJE = tsv.ID "
                    + " AND tsv.SG_TIPO_ESPECIFICO = te.ID "
                    + " AND sv.SG_MOTIVO = m.ID "
                    + " AND vc.SG_SOLICITUD_VIAJE = sv.id"
                    + " AND vc.SI_CIUDAD = ciu.ID"
                    + " AND sv.eliminado = 'False'");

//            UtilLog4j.log.info(this, "Query " + q.toString());
            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "SgSolicitudViajeImpl.getTotalSgSolicitudViaje401()" + e.getMessage());
            return 0;
        }
    }

    
    public SgSolicitudViaje delete(SgSolicitudViaje solicitudViaje, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.delete()");

        List<ViajeroVO> viajerosFromSolicitudViaje = this.sgViajeroRemote.getAllViajerosList(solicitudViaje.getId());

        for (ViajeroVO v : viajerosFromSolicitudViaje) {
            this.sgViajeroRemote.delete(v.getId(), idUsuario, "");
        }

        //Eliminar Itineario
        /*
	 * SgItinerario sgItinerario
	 * this.sgItinerarioRemote.findBySolicitudViaje(solicitudViaje, true,
	 * false); this.sgItinerarioRemote.findBySolicitudViaje(solicitudViaje,
	 * true, false);
         */
        ItinerarioCompletoVo itinerarioVo = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(solicitudViaje.getId(), true, false, "id");

        if (itinerarioVo != null) {
            this.sgItinerarioRemote.delete(itinerarioVo.getId(), idUsuario);
        }

        solicitudViaje.setModifico(new Usuario(idUsuario));
        solicitudViaje.setFechaModifico(new Date());
        solicitudViaje.setHoraModifico(new Date());
        solicitudViaje.setEliminado(Constantes.ELIMINADO);

        edit(solicitudViaje);
        UtilLog4j.log.info(this, "SgSolicitudViaje DELETED SUCCESSFULLY");

        return solicitudViaje;
    }

    
    public SgSolicitudViaje findByCode(String codigo
    ) {
        try {
            return (SgSolicitudViaje) em.createQuery("SELECT s FROM SgSolicitudViaje s WHERE s.codigo = :cod "
                    + " AND s.eliminado = :eli").setParameter("cod", codigo).setParameter("eli", Constantes.NO_ELIMINADO).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "En la excepción de recuperar solicitud viajes por código");
            return null;
        }
    }

    
    public List<SgSolicitudViaje> getSolicitudViajeByUsuarioAndStatus(String idUsuario, Integer estado, String orderByField, String orderByOrder, boolean eliminado
    ) {
        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.getSolicitudViajeByUsuario()");
        List<SgSolicitudViaje> solicitudesViaje;

        String queryLocal = "SELECT s FROM SgSolicitudViaje s WHERE s.genero.id = :idUsuario AND s.estatus.id= :idEstado AND s.eliminado = :eliminado";

        if (orderByField != null && !orderByField.equals("") && orderByOrder != null && !orderByOrder.equals("")) {
            queryLocal += " ORDER BY s." + orderByField + " " + orderByOrder;
        }

        Query q = em.createQuery(queryLocal);
        //Asignando parámetros
        q.setParameter("idUsuario", idUsuario);
        q.setParameter("idEstado", estado);
        q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

        solicitudesViaje = q.getResultList();

        UtilLog4j.log.info(this, "Se encontraron " + (!solicitudesViaje.isEmpty() ? solicitudesViaje.size() : 0) + " Solicitudes de Viaje para el usuario " + idUsuario);

        return solicitudesViaje;
    }

//    
//    public List<SolicitudViajeVO> getSolicitudViajeVOByUsuarioAndStatus(String idUsuario, int idEstatus, String orderByField, boolean sortAscending, boolean eliminado) {
//        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.getSolicitudViajeVOByUsuarioAndStatus()");
//
//        String q = "SELECT "
//                + "sv.id, " //0
//                + "sv.codigo, " //1
//                + "sv.observacion, " //2
//                + "sv.fecha_salida, " //3
//                + "sv.hora_salida, " //4
//                + "sv.fecha_regreso, " //5
//                + "sv.hora_regreso, " //6
//                + "sv.genero, " //7
//                + "tsv.id as id_sg_tipo_solicitud_viaje, " //8
//                + "tsv.nombre AS nombre_sg_tipo_solicitud_viaje, " //9
//                + "e.id AS id_estatus, " //10
//                + "e.nombre AS nombre_estatus, " //11
//                + "oo.id AS id_oficina_origen, " //12
//                + "oo.nombre AS nombre_oficina_origen, " //13
//                + "CASE WHEN sv.OFICINA_DESTINO IS NULL THEN 0 " //14
//                + "WHEN sv.OFICINA_DESTINO IS NOT NULL THEN od.id " //14
//                + "END AS id_oficina_destino, "
//                + "CASE WHEN sv.OFICINA_DESTINO IS NULL THEN 'NULL' " //15
//                + "WHEN sv.OFICINA_DESTINO IS NOT NULL THEN od.nombre " //15
//                + "END AS nombre_oficina_destino, "
//                + "g.id AS id_gerencia, " //16
//                + "g.nombre AS nombre_gerencia, " //17
//                + "m.id AS id_sg_motivo, " //18
//                + "m.nombre AS nombre_sg_motivo, " //19
//                + "te.id AS id_sg_tipo_especifico, " //20
//                + "(SELECT COUNT(id) FROM SG_VIAJERO WHERE SG_SOLICITUD_VIAJE = sv.id AND eliminado = '" + Constantes.NO_ELIMINADO + "') AS count_viajeros "//21
//                + "FROM "
//                + "SG_SOLICITUD_VIAJE sv LEFT OUTER JOIN SG_OFICINA od ON sv.OFICINA_DESTINO = od.ID, "
//                + "SG_TIPO_SOLICITUD_VIAJE tsv, SG_TIPO_ESPECIFICO te,"
//                + "ESTATUS e, "
//                + "GERENCIA g, "
//                + "SG_MOTIVO m, "
//                + "SG_OFICINA oo "
//                + "WHERE "
//                + "sv.ESTATUS = " + idEstatus + " "
//                + "AND sv.GENERO = '" + idUsuario + "' "
//                + "AND tsv.SG_TIPO_ESPECIFICO = 2"
//                + "AND sv.ESTATUS = e.ID "
//                + "AND sv.OFICINA_ORIGEN = oo.ID "
//                + "AND sv.GERENCIA_RESPONSABLE = g.ID "
//                + "AND sv.SG_TIPO_SOLICITUD_VIAJE = tsv.ID "
//                + "AND tsv.SG_TIPO_ESPECIFICO = te.ID "
//                + "AND sv.SG_MOTIVO = m.ID "
//                + "AND sv.eliminado = '" + Constantes.NO_ELIMINADO + "' "
//                + " AND not exists(select vc.SG_SOLICITUD_VIAJE from SG_VIAJE_CIUDAD vc where vc.SG_SOLICITUD_VIAJE = sv.id) ";
//
//        if (orderByField != null && !orderByField.isEmpty()) {
//            q += " ORDER BY sv." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
//        }
//
//        Query query = em.createNativeQuery(q);
//
//        UtilLog4j.log.info(this, "query: " + query.toString());
//
//        List<Object[]> result = query.getResultList();
//        List<SolicitudViajeVO> list = new ArrayList<SolicitudViajeVO>();
//
//        SolicitudViajeVO vo = null;
//
//        for (Object[] objects : result) {
//            vo = new SolicitudViajeVO();
//            vo.setIdSolicitud((Integer) objects[0]);
//            vo.setCodigo((String) objects[1]);
//            vo.setObservacion((String) objects[2]);
//            vo.setFechaSalida((Date) objects[3]);
//            vo.setHoraSalida((Date) objects[4]);
//            vo.setFechaRegreso((Date) objects[5]);
//            vo.setHoraRegreso((Date) objects[6]);
//            vo.setGenero((String) objects[7]);
//            vo.setIdSgTipoSolicitudViaje((Integer) objects[8]);
//
//            if (vo.getIdSgTipoSolicitudViaje() == 1) {
//                vo.setTipoSolicitud("TERRESTRE");
//            } else if (vo.getIdSgTipoSolicitudViaje() == 2) {
//                vo.setTipoSolicitud("TERRESTRE");
//            } else {
//                vo.setTipoSolicitud((String) objects[9]);
//            }
//
//            vo.setIdEstatus((Integer) objects[10]);
//            vo.setEstatus((String) objects[11]);
//            vo.setIdOficinaOrigen((Integer) objects[12]);
//            vo.setOrigen((String) objects[13]);
//            vo.setIdOficinaDestino((Integer) objects[14]);
//            vo.setDestino((String) objects[15]);
//            vo.setIdGerencia((Integer) objects[16]);
//            vo.setGerencia((String) objects[17]);
//            vo.setIdSgMotivo((Integer) objects[18]);
//            vo.setMotivo((String) objects[19]);
//            vo.setIdSgTipoEspecifico((Integer) objects[20]);
//            vo.setCountSgViajero((Integer) objects[21]);
//            list.add(vo);
//        }
//
//        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SgSolicitudViaje");
//
//        return (list != null ? list : Collections.EMPTY_LIST);
//    }
//    
//    public List<SolicitudViajeVO> getSolicitudViajeCiudadVO(String idUsuario, int idEstatus) {
//        UtilLog4j.log.info(this, "SgSolicitudViajeImpl.getSolicitudViajeCiudadVO()");
//        String q = "SELECT "
//                + " sv.id, "//0
//                + " sv.codigo, "//1
//                + " sv.observacion, "//2
//                + " sv.fecha_salida,"//3
//                + " sv.hora_salida, "//4
//                + " sv.fecha_regreso,"//5
//                + " sv.hora_regreso,"//6
//                + " sv.genero,"//7
//                + " tsv.id as id_sg_tipo_solicitud_viaje,"//8
//                + " tsv.nombre AS nombre_sg_tipo_solicitud_viaje, "//9
//                + " e.id AS id_estatus, "//10
//                + "  e.nombre AS nombre_estatus, "//11
//                + "  oo.id AS id_oficina_origen,  "//12
//                + "  oo.NOMBRE as nombre_oficina,"//13
//                + " g.id AS id_gerencia, "//14
//                + "  g.nombre AS nombre_gerencia, "//15
//                + "  m.id AS id_sg_motivo, "//16
//                + "  m.nombre AS nombre_sg_motivo, "//17
//                + "  te.id AS id_sg_tipo_especifico, "//18
//                + "  ciu.NOMBRE,"//19
//                + " (SELECT COUNT(id) FROM SG_VIAJERO WHERE SG_SOLICITUD_VIAJE = sv.id AND eliminado = 'False') AS count_viajeros "//20
//                + " FROM "
//                + "   SG_SOLICITUD_VIAJE sv,"
//                + "   SG_TIPO_SOLICITUD_VIAJE tsv, "
//                + " SG_TIPO_ESPECIFICO te,"
//                + "  ESTATUS e, "
//                + " GERENCIA g, "
//                + " SG_MOTIVO m, "
//                + " SG_OFICINA oo,"
//                + " SG_VIAJE_CIUDAD vc,"
//                + " SI_CIUDAD ciu"
//                + " WHERE "
//                + " sv.ESTATUS = " + idEstatus
//                + " AND sv.GENERO = '" + idUsuario + "' "
//                + " AND sv.ESTATUS = e.ID "
//                + " AND sv.OFICINA_ORIGEN = oo.ID "
//                + " AND sv.GERENCIA_RESPONSABLE = g.ID "
//                + " AND sv.SG_TIPO_SOLICITUD_VIAJE = tsv.ID "
//                + " AND tsv.SG_TIPO_ESPECIFICO = te.ID "
//                + " AND sv.SG_MOTIVO = m.ID "
//                + " AND vc.SG_SOLICITUD_VIAJE = sv.id"
//                + " AND vc.SI_CIUDAD = ciu.ID"
//                + " AND sv.eliminado = '" + Constantes.BOOLEAN_FALSE + "'";
//
//        Query query = em.createNativeQuery(q);
//
//        UtilLog4j.log.info(this, "query: " + query.toString());
//
//        List<Object[]> result = query.getResultList();
//        List<SolicitudViajeVO> list = new ArrayList<SolicitudViajeVO>();
//
//        SolicitudViajeVO vo = null;
//
//        for (Object[] objects : result) {
//            vo = new SolicitudViajeVO();
//            vo.setIdSolicitud((Integer) objects[0]);
//            vo.setCodigo((String) objects[1]);
//            vo.setObservacion((String) objects[2]);
//            vo.setFechaSalida((Date) objects[3]);
//            vo.setHoraSalida((Date) objects[4]);
//            vo.setFechaRegreso((Date) objects[5]);
//            vo.setHoraRegreso((Date) objects[6]);
//            vo.setGenero((String) objects[7]);
//            vo.setIdSgTipoSolicitudViaje((Integer) objects[8]);
//            vo.setTipoSolicitud((String) objects[9]);
//            vo.setIdEstatus((Integer) objects[10]);
//            vo.setEstatus((String) objects[11]);
//            vo.setIdOficinaOrigen((Integer) objects[12]);
//            vo.setOrigen((String) objects[13]);
//            vo.setIdGerencia((Integer) objects[14]);
//            vo.setGerencia((String) objects[15]);
//            vo.setIdSgMotivo((Integer) objects[16]);
//            vo.setMotivo((String) objects[17]);
//            vo.setIdSgTipoEspecifico((Integer) objects[18]);
//            vo.setDestino((String) objects[19]);
//            vo.setCountSgViajero((Integer) objects[20]);
//            list.add(vo);
//        }
//        UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SgSolicitudViaje");
//        return (list != null ? list : Collections.EMPTY_LIST);
//    }
    
    public List<SolicitudViajeVO> getSgSolicitudViajeTerrestreToOficina(String idUsuario, int idEstatus, String orderByField, boolean sortAscending, boolean eliminado
    ) {
        clearQuery();
        appendQuery("SELECT sv.ID,  "); //0
        appendQuery("sv.CODIGO, "); //1
        appendQuery("sv.OBSERVACION, "); //2
        appendQuery("sv.FECHA_SALIDA, "); //3
        appendQuery("sv.HORA_SALIDA, "); //4
        appendQuery("sv.FECHA_REGRESO, "); //5
        appendQuery("sv.HORA_REGRESO, "); //6
        appendQuery("sv.GENERO, "); //7
        appendQuery("sv.OFICINA_ORIGEN AS ID_OFICINA_ORIGEN, "); //8
        appendQuery("sv.OFICINA_DESTINO AS ID_OFICINA_DESTINO, "); //9
        appendQuery("(SELECT oo.NOMBRE FROM SG_OFICINA oo WHERE oo.ID=sv.OFICINA_ORIGEN) AS NOMBRE_OFICINA_ORIGEN, "); //10
        appendQuery("(SELECT od.NOMBRE FROM SG_OFICINA od WHERE od.ID=sv.OFICINA_DESTINO) AS NOMBRE_OFICINA_DESTINO, "); //11
        appendQuery("tsv.ID as ID_SG_TIPO_SOLICITUD_VIAJE, "); //12
        appendQuery("tsv.NOMBRE AS NOMBRE_SG_TIPO_SOLICITUD_VIAJE, "); //13
        appendQuery("e.ID AS ID_ESTATUS, "); //14
        appendQuery("e.NOMBRE AS NOMBRE_ESTATUS, "); //15
        appendQuery("g.ID AS ID_GERENCIA, "); //16
        appendQuery("g.NOMBRE AS NOMBRE_GERENCIA, "); //17
        appendQuery("m.ID AS ID_SG_MOTIVO, "); //18
        appendQuery("m.NOMBRE AS NOMBRE_SG_MOTIVO, "); //19
        appendQuery("te.ID AS ID_SG_TIPO_ESPECIFICO, "); //20
        appendQuery("te.NOMBRE AS NOMBRE_SG_TIPO_ESPECIFICO,  "); //21
        appendQuery("(SELECT COUNT(v.id) FROM SG_VIAJERO v WHERE SG_SOLICITUD_VIAJE = sv.ID AND v.eliminado = 'False') AS COUNT_VIAJEROS, "); //22

//        appendQuery("case when sv.sg_estado_semaforo is NULL then '' when sv.sg_estado_semaforo is not null then (select sem.COLOR from SG_ESTADO_SEMAFORO es, SG_SEMAFORO sem  "); //23
//        appendQuery("   where es.SG_RUTA_TERRESTRE =(select est.SG_RUTA_TERRESTRE from SG_ESTADO_SEMAFORO est where est.ID = sv.SG_ESTADO_SEMAFORO)");
//        appendQuery("      and es.ACTUAL = 'True'");
//        appendQuery("      and es.ELIMINADO = 'False'");
//        appendQuery("      and es.SG_SEMAFORO = sem.id) end,");//23
        appendQuery(" (select s.COLOR ");
        appendQuery(" from SG_SEMAFORO s ");
        appendQuery(" where s.ID = ( ");
        appendQuery(" SELECT FIRST 1 xx ");
        appendQuery(" from ( ");
        appendQuery(" select (SELECT FIRST 1 ar.SG_SEMAFORO ");
        appendQuery(" 				FROM SG_ESTADO_SEMAFORO ar   ");
        appendQuery(" 				where ar.ELIMINADO = 'False'  	 ");
        appendQuery(" 				and ar.GR_MAPA = rz.GR_MAPA ");
        appendQuery(" 				ORDER BY ar.ID DESC) as xx ");
        appendQuery(" from GR_RUTAS_ZONAS rz   ");
        appendQuery(" where rz.SG_RUTA_TERRESTRE = sv.SG_RUTA_TERRESTRE ");
        appendQuery(" and rz.ELIMINADO = 'False' ");
        appendQuery(" order by rz.SECUENCIA) ");
        appendQuery(" group by xx ");
        appendQuery(" order by xx desc)), ");//23

        appendQuery("sv.REDONDO, "); //24

        appendQuery(" -1 AS id_sg_estado_semaforo, "); //25
        appendQuery(" case when sv.sg_motivo_retraso is null then 0 when sv.sg_motivo_retraso is not null then sv.sg_motivo_retraso end "); //26
        appendQuery(" ,sv.SG_RUTA_TERRESTRE "); //27
        appendQuery("FROM SG_SOLICITUD_VIAJE sv, ");
        appendQuery(" SG_TIPO_SOLICITUD_VIAJE tsv, ");
        appendQuery("SG_TIPO_ESPECIFICO te, ");
        appendQuery("ESTATUS e, ");
        appendQuery("GERENCIA g, ");
        appendQuery("SG_MOTIVO m, ");
        appendQuery("SG_OFICINA o ");
        appendQuery("WHERE sv.ESTATUS=").append(idEstatus).append(" ");
        appendQuery("AND sv.GENERO='").append(idUsuario).append("' ");
        appendQuery("AND sv.OFICINA_DESTINO IS NOT NULL ");
        appendQuery("AND tsv.SG_TIPO_ESPECIFICO = 2 ");
        appendQuery("AND sv.ESTATUS=e.ID  ");
        appendQuery("AND sv.OFICINA_ORIGEN=o.ID  ");
        appendQuery("AND sv.GERENCIA_RESPONSABLE=g.ID ");
        appendQuery("AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
        appendQuery("AND tsv.SG_TIPO_ESPECIFICO=te.ID ");
        appendQuery("AND sv.SG_MOTIVO=m.ID ");
        appendQuery("AND sv.eliminado='").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");
//                + "CASE WHEN sv.OFICINA_DESTINO IS NULL THEN 0 " //14
//                + "WHEN sv.OFICINA_DESTINO IS NOT NULL THEN od.id " //14
//                + "END AS id_oficina_destino, "
        if (orderByField != null && !orderByField.isEmpty()) {
            appendQuery("ORDER BY sv.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        Query queryLocal = em.createNativeQuery(this.query.toString());

//        UtilLog4j.log.info(this, query.toString());
        List<Object[]> result = queryLocal.getResultList();
        List<SolicitudViajeVO> list = new ArrayList<SolicitudViajeVO>();
        SolicitudViajeVO vo = null;

        for (Object[] objects : result) {
            vo = new SolicitudViajeVO();
            vo.setIdSolicitud((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setObservacion((String) objects[2]);
            vo.setFechaSalida((Date) objects[3]);
            vo.setHoraSalida((Date) objects[4]);
            vo.setFechaRegreso((Date) objects[5]);
            vo.setHoraRegreso((Date) objects[6]);
            vo.setGenero((String) objects[7]);
            vo.setIdOficinaOrigen((Integer) objects[8]);
            vo.setIdOficinaDestino((Integer) objects[9]);
            vo.setOrigen((String) objects[10]);
            vo.setDestino((String) objects[11]);
            vo.setIdSgTipoSolicitudViaje((Integer) objects[12]);
            vo.setTipoSolicitud(Constantes.SV_TERRESTRE); //(String) objects[13]
            vo.setIdEstatus((Integer) objects[14]);
            vo.setEstatus((String) objects[15]);
            vo.setIdGerencia((Integer) objects[16]);
            vo.setGerencia((String) objects[17]);
            vo.setIdSgMotivo((Integer) objects[18]);
            vo.setMotivo((String) objects[19]);
            vo.setIdSgTipoEspecifico((Integer) objects[20]);
            vo.setTipoEspecifico((String) objects[21]);
            vo.setCountSgViajero((Integer) objects[22]);
            vo.setColorSgSemaforo(((String) objects[23]) != null ? (String) objects[23] : "");
            vo.setRedondo((Boolean) objects[24]);
            vo.setSencillo((Boolean) objects[24]);
            //vo.setIdEstadoSemaforo((Integer) objects[25]);
            vo.setIdMotivoRetraso((Integer) objects[26]);
            vo.setIdRutaTerrestre((Integer) objects[27] != null ? (Integer) objects[27] : 0);

            if (!vo.getColorSgSemaforo().equals("")) {
                /// consulsultar el semaforo actual
                vo.setSemaforoVo(crearSemaforoVO(vo.getIdSemaforo(), vo.getIdRutaTerrestre()));
            }
            list.add(vo);
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgSolicitudViaje a oficina para el usuario " + idUsuario + " estatus " + idEstatus);

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    private SemaforoVo crearSemaforoVO(int idSemaforo, int idRuta) {
        SemaforoVo vo = null;
        try {
            vo = new SemaforoVo();
            vo.setIdRuta(idRuta);
            vo.setIdSemaforo(idSemaforo);
            SgRutaTerrestre ruta = sgRutaTerrestreRemote.find(idRuta);
            SgSemaforo semaforo = sgSemaforoRemote.find(idSemaforo);
            vo.setNombreRuta(ruta.getNombre());
            vo.setColor(semaforo.getColor());
            vo.setOrigen(ruta.getSgOficina().getNombre());
//            vo.setFechaInicio((Date) objects[6]);
//            vo.setHoraInicio((Date) objects[7]);
//            vo.setFechaFin((Date) objects[8]);
//            vo.setHoraFin((Date) objects[9]);
            vo.setHoraMinimaRuta(ruta.getHoraMinimaRuta());
            vo.setHoraMaximaRuta(ruta.getHoraMaximaRuta());
            vo.setRutaTipoEspecifico(ruta.getSgTipoEspecifico().getId());
            vo.setDescripcion(semaforo.getDescripcion());
//            vo.setJustificacion(varJus);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            vo = null;
        }
        return vo;
    }

    
    public SolicitudViajeVO buscarPorId(int idSolicitud, boolean eliminado, int se) {
        try {
            clearQuery();
            appendQuery("SELECT sv.ID,  "); //0
            appendQuery(" sv.CODIGO, "); //1
            appendQuery(" sv.OBSERVACION, "); //2
            appendQuery(" sv.FECHA_SALIDA, "); //3
            appendQuery(" sv.HORA_SALIDA, "); //4
            appendQuery(" sv.FECHA_REGRESO, "); //5
            appendQuery(" sv.HORA_REGRESO, "); //6
            appendQuery(" sv.GENERO, "); //7
            appendQuery(" sv.OFICINA_ORIGEN AS ID_OFICINA_ORIGEN, "); //8
            appendQuery(" case when sv.oficina_destino is null then 0 when sv.oficina_destino is not null then sv.oficina_destino end, "); //9
            appendQuery(" (SELECT oo.NOMBRE FROM SG_OFICINA oo WHERE oo.ID=sv.OFICINA_ORIGEN) AS NOMBRE_OFICINA_ORIGEN, "); //10
            appendQuery(" (SELECT od.NOMBRE FROM SG_OFICINA od WHERE od.ID=sv.OFICINA_DESTINO) AS NOMBRE_OFICINA_DESTINO, "); //11
            appendQuery(" tsv.ID as ID_SG_TIPO_SOLICITUD_VIAJE, "); //12
            appendQuery(" tsv.NOMBRE AS NOMBRE_SG_TIPO_SOLICITUD_VIAJE, "); //13
            appendQuery(" e.ID AS ID_ESTATUS, "); //14
            appendQuery(" e.NOMBRE AS NOMBRE_ESTATUS, "); //15
            appendQuery(" g.ID AS ID_GERENCIA, "); //16
            appendQuery(" g.NOMBRE AS NOMBRE_GERENCIA, "); //17a
            appendQuery(" case when sv.sg_motivo is NULL then 0 when sv.sg_motivo is not null then sv.sg_motivo end, ");
            appendQuery(" case when sv.sg_motivo is NULL then '' when sv.sg_motivo is not null then  (select m.nombre from SG_MOTIVO m where sv.sg_motivo = m.ID) end,");

            appendQuery(" te.ID AS ID_SG_TIPO_ESPECIFICO, "); //20
            appendQuery(" te.NOMBRE AS NOMBRE_SG_TIPO_ESPECIFICO,  "); //21
            appendQuery(" (SELECT COUNT(v.id) FROM SG_VIAJERO v WHERE SG_SOLICITUD_VIAJE = sv.ID AND v.eliminado = 'False') AS COUNT_VIAJEROS, "); //22

            //appendQuery(" case when sv.sg_estado_semaforo is NULL then 0 when sv.sg_estado_semaforo is not null then (SELECT s.COLOR FROM SG_SEMAFORO s WHERE s.ID=(SELECT es.SG_SEMAFORO FROM SG_ESTADO_SEMAFORO es WHERE es.ID=sv.SG_ESTADO_SEMAFORO)) end AS COLOR_SG_SEMAFORO, "); //23
            appendQuery(" (select s.COLOR ");
            appendQuery(" from SG_SEMAFORO s ");
            appendQuery(" where s.ID = ( ");
            appendQuery(" SELECT xx ");
            appendQuery(" from ( ");
            appendQuery(" select (SELECT ar.SG_SEMAFORO ");
            appendQuery(" 				FROM SG_ESTADO_SEMAFORO ar   ");
            appendQuery(" 				where ar.ELIMINADO = 'False'  	 ");
            appendQuery(" 				and ar.GR_MAPA = rz.GR_MAPA ");
            appendQuery(" 				ORDER BY ar.ID DESC limit 1 ) as xx ");
            appendQuery(" from GR_RUTAS_ZONAS rz   ");
            appendQuery(" where rz.SG_RUTA_TERRESTRE = sv.SG_RUTA_TERRESTRE ");
            appendQuery(" and rz.ELIMINADO = 'False' ");
            appendQuery(" order by rz.SECUENCIA limit 1 ) as xxx ");
            appendQuery(" group by xx ");
            appendQuery(" order by xx desc)),  "); //23

            appendQuery(" sv.REDONDO, "); //24
            appendQuery(" case when sv.sg_estado_semaforo is null then 0 when sv.sg_estado_semaforo is not null then sv.SG_ESTADO_SEMAFORO end AS id_sg_estado_semaforo, "); //25
            appendQuery(" case when sv.sg_motivo_retraso is null then 0 when sv.sg_motivo_retraso is not null then sv.sg_motivo_retraso end, "); //26
            appendQuery(" case when sv.co_noticia is null then 0 when sv.co_noticia is not null then sv.co_noticia end ,"); //27
            appendQuery(" u.NOMBRE,"); //28
            appendQuery(" sv.FECHA_GENERO,"); //29
            appendQuery(" sv.HORA_GENERO,"); //30
            appendQuery("( select distinct(v.SG_SOLICITUD_ESTANCIA)");
            appendQuery("         from SG_VIAJERO v");
            appendQuery(" where v.ELIMINADO = 'False'");
            //TODO : revisar la consulta
            if (se == Constantes.CERO) {
                appendQuery(" and v.SG_SOLICITUD_VIAJE =sv.id and v.estancia = 'True') as solicitudEstancia,");//31
            } else {
                appendQuery(" and v.SG_SOLICITUD_ESTANCIA =" + se + " and v.estancia = 'True') as solicitudEstancia,");//31
            }
            appendQuery(" case when u.email is null then '' else u.email end, "); //32
            appendQuery(" sv.SG_RUTA_TERRESTRE "); //33
            appendQuery(" FROM SG_SOLICITUD_VIAJE sv, ");
            appendQuery(" SG_TIPO_SOLICITUD_VIAJE tsv, ");
            appendQuery(" SG_TIPO_ESPECIFICO te, ");
            appendQuery(" ESTATUS e, ");
            appendQuery(" GERENCIA g, ");
            appendQuery(" SG_OFICINA o, ");
            appendQuery(" usuario u");
            appendQuery(" WHERE sv.id = ").append(idSolicitud);
            appendQuery(" AND sv.ESTATUS=e.ID  ");
            appendQuery(" AND sv.OFICINA_ORIGEN=o.ID  ");
            appendQuery(" AND sv.GERENCIA_RESPONSABLE=g.ID ");
            appendQuery(" AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
            appendQuery(" AND sv.SG_TIPO_ESPECIFICO=te.ID ");
            appendQuery(" and sv.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" and sv.GENERO = u.id");
            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            return castSolicitudViajeVO(obj);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al recuperar por id; " + e.getMessage());
            return null;
        }
    }

    private SolicitudViajeVO castSolicitudViajeVO(Object[] objects) {
        SolicitudViajeVO vo = new SolicitudViajeVO();
        vo.setIdSolicitud((Integer) objects[0]);
        vo.setCodigo((String) objects[1]);
        vo.setObservacion((String) objects[2]);
        vo.setFechaSalida((Date) objects[3]);
        vo.setHoraSalida((Date) objects[4]);
        vo.setFechaRegreso((Date) objects[5]);
        vo.setHoraRegreso((Date) objects[6]);
        vo.setGenero((String) objects[7]);
        vo.setIdOficinaOrigen((Integer) objects[8]);
        vo.setIdOficinaDestino((Integer) objects[9]);
        vo.setOrigen((String) objects[10]);
        vo.setDestino((String) objects[11]);
        vo.setIdSgTipoSolicitudViaje((Integer) objects[12]);
        vo.setTipoSolicitud(vo.getIdSgTipoSolicitudViaje() > 2 ? (String) objects[13] : Constantes.SV_TERRESTRE); //
        vo.setIdEstatus((Integer) objects[14]);
        vo.setEstatus((String) objects[15]);
        vo.setIdGerencia((Integer) objects[16]);
        vo.setGerencia((String) objects[17]);
        vo.setIdSgMotivo((Integer) objects[18]);
        vo.setMotivo((String) objects[19]);
        vo.setIdSgTipoEspecifico((Integer) objects[20]);
        vo.setTipoEspecifico((String) objects[21]);
        vo.setCountSgViajero(((Long) objects[22]).intValue());
        vo.setColorSgSemaforo(((String) objects[23]));
        vo.setRedondo((Boolean) objects[24]);
        vo.setSencillo(((Boolean) objects[24]));
//        vo.setIdEstadoSemaforo((Integer) objects[25]);
        vo.setIdMotivoRetraso((Integer) objects[26]);
        vo.setIdNoticia((Integer) objects[27]);
        //--Se muestran en el historial de aprobaciones
        vo.setNombreGenero((String) objects[28]);
        vo.setFechaGenero((Date) objects[29]);
        vo.setHoraGenero((Date) objects[30]);
        vo.setIdSolicitudEstancia(objects[31] != null ? (Integer) objects[31] : 0);
        vo.setCorreoGenero((String) objects[32]);
        vo.setIdRutaTerrestre(objects[33] != null ? (Integer) objects[33] : 0);
        if (vo.getIdMotivoRetraso() != 0) {
            //traer el motivo de retraso para la solicitud
            vo.setMotivoRetrasoVo(sgMotivoRetrasoRemote.findById(vo.getIdMotivoRetraso(), vo.getIdSgTipoSolicitudViaje()));
        }
        if (vo.getIdSgTipoEspecifico() == Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {
            ItinerarioCompletoVo itinerarioCompletoVoIda = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(vo.getIdSolicitud(), true, true, "id");
            ItinerarioCompletoVo itinerarioCompletoVoVuelta = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(vo.getIdSolicitud(), false, true, "id");
            vo.setItinerarioCompletoVoIda(itinerarioCompletoVoIda);
            vo.setItinerarioCompletoVoVuelta(itinerarioCompletoVoVuelta);
        }
        JustIncumSolVo justIncumSolVo = sgJustIncumpSolRemote.recuperaJustificacionGerente(vo.getIdSolicitud());
        if (justIncumSolVo != null) {
            vo.setJustIncumSol(justIncumSolVo);
        } else {
            vo.setJustIncumSol(null);
        }
        if (vo.getIdEstatus() == Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO) {
            vo.setSolicitudViajeMovimientoVo(sgSolicitudViajeSiMovimientoRemote.buscarMotivoCancelacion(vo.getIdSolicitud(), Constantes.ID_SI_OPERACION_CANCELAR));
        } else {
            vo.setSolicitudViajeMovimientoVo(null);
        }

        return vo;
    }

    
    public List<SolicitudViajeVO> traerSolicitudViajeTerrestreALugar(String idUsuario, int idEstatus) {
        clearQuery();
        appendQuery("SELECT sv.ID,  "); //0
        appendQuery("sv.CODIGO, "); //1
        appendQuery("sv.OBSERVACION, "); //2
        appendQuery("sv.FECHA_SALIDA, "); //3
        appendQuery("sv.HORA_SALIDA, "); //4
        appendQuery("sv.FECHA_REGRESO, "); //5
        appendQuery("sv.HORA_REGRESO, "); //6
        appendQuery("sv.GENERO, "); //7
        appendQuery("sv.OFICINA_ORIGEN AS ID_OFICINA_ORIGEN, "); //8
        appendQuery("(SELECT oo.NOMBRE FROM SG_OFICINA oo WHERE oo.ID=sv.OFICINA_ORIGEN) AS NOMBRE_OFICINA_ORIGEN, "); //9
        appendQuery("(SELECT l.NOMBRE FROM Sg_lugar l WHERE l.ID=(SELECT vl.Sg_lugar FROM SG_VIAJE_lugar vl WHERE vl.SG_SOLICITUD_VIAJE=sv.ID and vl.eliminado = 'False')), "); //10
        appendQuery("tsv.ID as ID_SG_TIPO_SOLICITUD_VIAJE, "); //11
        appendQuery("tsv.NOMBRE AS NOMBRE_SG_TIPO_SOLICITUD_VIAJE, "); //12
        appendQuery("e.ID AS ID_ESTATUS, "); //13
        appendQuery("e.NOMBRE AS NOMBRE_ESTATUS, "); //14
        appendQuery("g.ID AS ID_GERENCIA, "); //15
        appendQuery("g.NOMBRE AS NOMBRE_GERENCIA, "); //16
        appendQuery("m.ID AS ID_SG_MOTIVO, "); //17
        appendQuery("m.NOMBRE AS NOMBRE_SG_MOTIVO, "); //18
        appendQuery("te.ID AS ID_SG_TIPO_ESPECIFICO, "); //19
        appendQuery("te.NOMBRE AS NOMBRE_SG_TIPO_ESPECIFICO,  "); //20
        appendQuery("(SELECT COUNT(v.id) FROM SG_VIAJERO v WHERE SG_SOLICITUD_VIAJE = sv.ID AND v.eliminado = 'False') AS COUNT_VIAJEROS, "); //21
        appendQuery("case when sv.sg_estado_semaforo is NULL then '' when sv.sg_estado_semaforo is not null then (SELECT s.COLOR FROM SG_SEMAFORO s WHERE s.ID=(SELECT es.SG_SEMAFORO FROM SG_ESTADO_SEMAFORO es WHERE es.ID=sv.SG_ESTADO_SEMAFORO)) end AS COLOR_SG_SEMAFORO, "); //22
        appendQuery("sv.REDONDO, "); //23
        appendQuery("case when sv.sg_estado_semaforo is null then -1 when sv.sg_estado_semaforo is not null then sv.SG_ESTADO_SEMAFORO end AS id_sg_estado_semaforo, "); //24
        appendQuery("(SELECT l.id FROM Sg_lugar l WHERE l.ID=(SELECT vl.sg_lugar FROM SG_VIAJE_lugar vl WHERE vl.SG_SOLICITUD_VIAJE=sv.ID and vl.eliminado = 'False')) "); //25
        appendQuery("FROM SG_SOLICITUD_VIAJE sv, ");
        appendQuery(" SG_TIPO_SOLICITUD_VIAJE tsv, ");
        appendQuery("SG_TIPO_ESPECIFICO te, ");
        appendQuery("ESTATUS e, ");
        appendQuery("GERENCIA g, ");
        appendQuery("SG_MOTIVO m, ");
        appendQuery("SG_OFICINA o ");
        appendQuery("WHERE sv.ESTATUS=").append(idEstatus).append(" ");
        appendQuery("AND sv.GENERO='").append(idUsuario).append("' ");
        appendQuery("AND sv.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("AND sv.OFICINA_DESTINO IS NULL ");
        appendQuery("AND tsv.SG_TIPO_ESPECIFICO = ").append(Constantes.SOLICITUDES_TERRESTRE);
        appendQuery("AND sv.id in (select vl.sg_solicitud_viaje from sg_viaje_lugar vl where vl.sg_solicitud_viaje=sv.id) ");
        appendQuery("AND sv.ESTATUS=e.ID  ");
        appendQuery("AND sv.OFICINA_ORIGEN=o.ID  ");
        appendQuery("AND sv.GERENCIA_RESPONSABLE=g.ID ");
        appendQuery("AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
        appendQuery("AND tsv.SG_TIPO_ESPECIFICO=te.ID ");
        appendQuery("AND sv.SG_MOTIVO=m.ID ");
        appendQuery("AND sv.eliminado='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("ORDER BY sv.codigo asc ");

//        UtilLog4j.log.info(this, query.toString());
        List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();
        List<SolicitudViajeVO> list = new ArrayList<SolicitudViajeVO>();
        SolicitudViajeVO vo = null;
        for (Object[] objects : result) {
            vo = new SolicitudViajeVO();
            vo.setIdSolicitud((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setObservacion((String) objects[2]);
            vo.setFechaSalida((Date) objects[3]);
            vo.setHoraSalida((Date) objects[4]);
            vo.setFechaRegreso((Date) objects[5]);
            vo.setHoraRegreso((Date) objects[6]);
            vo.setGenero((String) objects[7]);
            vo.setIdOficinaOrigen((Integer) objects[8]);
            vo.setOrigen((String) objects[9]);
            vo.setDestino((String) objects[10]);
            vo.setIdSgTipoSolicitudViaje((Integer) objects[11]);
            vo.setTipoSolicitud(Constantes.SV_TERRESTRE); //(String) objects[12]
            vo.setIdEstatus((Integer) objects[13]);
            vo.setEstatus((String) objects[14]);
            vo.setIdGerencia((Integer) objects[15]);
            vo.setGerencia((String) objects[16]);
            vo.setIdSgMotivo((Integer) objects[17]);
            vo.setMotivo((String) objects[18]);
            vo.setIdSgTipoEspecifico((Integer) objects[19]);
            vo.setTipoEspecifico((String) objects[20]);
            vo.setCountSgViajero((Integer) objects[21]);
            vo.setColorSgSemaforo(((String) objects[23]) != null ? (((String) objects[22]).toLowerCase()) : "");
            vo.setRedondo((Boolean) objects[23]);
//            vo.setIdEstadoSemaforo((Integer) objects[24]);
            vo.setIdSiCiudadDestino((Integer) objects[25]);
            list.add(vo);
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgSolicitudViaje");

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public List<SolicitudViajeVO> getSgSolicitudViajeTerrestreToCiudad(String idUsuario, int idEstatus, String orderByField, boolean sortAscending, boolean eliminado) {
        clearQuery();
        appendQuery("SELECT sv.ID,  "); //0
        appendQuery("sv.CODIGO, "); //1
        appendQuery("sv.OBSERVACION, "); //2
        appendQuery("sv.FECHA_SALIDA, "); //3
        appendQuery("sv.HORA_SALIDA, "); //4
        appendQuery("sv.FECHA_REGRESO, "); //5
        appendQuery("sv.HORA_REGRESO, "); //6
        appendQuery("sv.GENERO, "); //7
        appendQuery("sv.OFICINA_ORIGEN AS ID_OFICINA_ORIGEN, "); //8
        appendQuery("(SELECT oo.NOMBRE FROM SG_OFICINA oo WHERE oo.ID=sv.OFICINA_ORIGEN) AS NOMBRE_OFICINA_ORIGEN, "); //9
        appendQuery("(SELECT c.NOMBRE FROM SI_CIUDAD c WHERE c.ID=(SELECT vc.SI_CIUDAD FROM SG_VIAJE_CIUDAD vc WHERE vc.SG_SOLICITUD_VIAJE=sv.ID and vc.eliminado = 'False')) AS NOMBRE_SI_CIUDAD, "); //10
        appendQuery("tsv.ID as ID_SG_TIPO_SOLICITUD_VIAJE, "); //11
        appendQuery("tsv.NOMBRE AS NOMBRE_SG_TIPO_SOLICITUD_VIAJE, "); //12
        appendQuery("e.ID AS ID_ESTATUS, "); //13
        appendQuery("e.NOMBRE AS NOMBRE_ESTATUS, "); //14
        appendQuery("g.ID AS ID_GERENCIA, "); //15
        appendQuery("g.NOMBRE AS NOMBRE_GERENCIA, "); //16
        appendQuery("m.ID AS ID_SG_MOTIVO, "); //17
        appendQuery("m.NOMBRE AS NOMBRE_SG_MOTIVO, "); //18
        appendQuery("te.ID AS ID_SG_TIPO_ESPECIFICO, "); //19
        appendQuery("te.NOMBRE AS NOMBRE_SG_TIPO_ESPECIFICO,  "); //20
        appendQuery("(SELECT COUNT(v.id) FROM SG_VIAJERO v WHERE SG_SOLICITUD_VIAJE = sv.ID AND v.eliminado = 'False') AS COUNT_VIAJEROS, "); //21
        //
//        appendQuery("case when sv.sg_estado_semaforo is NULL then '' when sv.sg_estado_semaforo is not null then (select sem.COLOR from SG_ESTADO_SEMAFORO es, SG_SEMAFORO sem  "); //23
//        appendQuery("   where es.SG_RUTA_TERRESTRE =(select est.SG_RUTA_TERRESTRE from SG_ESTADO_SEMAFORO est where est.ID = sv.SG_ESTADO_SEMAFORO)");
//        appendQuery("      and es.ACTUAL = 'True'");
//        appendQuery("      and es.ELIMINADO = 'False'");
//        appendQuery("      and es.SG_SEMAFORO = sem.id) end,");//23
        appendQuery(" (select s.COLOR ");
        appendQuery(" from SG_SEMAFORO s ");
        appendQuery(" where s.ID = ( ");
        appendQuery(" SELECT FIRST 1 xx ");
        appendQuery(" from ( ");
        appendQuery(" select (SELECT FIRST 1 ar.SG_SEMAFORO ");
        appendQuery(" 				FROM SG_ESTADO_SEMAFORO ar   ");
        appendQuery(" 				where ar.ELIMINADO = 'False'  	 ");
        appendQuery(" 				and ar.GR_MAPA = rz.GR_MAPA ");
        appendQuery(" 				ORDER BY ar.ID DESC) as xx ");
        appendQuery(" from GR_RUTAS_ZONAS rz   ");
        appendQuery(" where rz.SG_RUTA_TERRESTRE = sv.SG_RUTA_TERRESTRE ");
        appendQuery(" and rz.ELIMINADO = 'False' ");
        appendQuery(" order by rz.SECUENCIA) ");
        appendQuery(" group by xx ");
        appendQuery(" order by xx desc)), ");//23

        //   appendQuery("case when sv.sg_estado_semaforo is NULL then '' when sv.sg_estado_semaforo is not null then (SELECT s.COLOR FROM SG_SEMAFORO s WHERE s.ID=(SELECT es.SG_SEMAFORO FROM SG_ESTADO_SEMAFORO es WHERE es.ID=sv.SG_ESTADO_SEMAFORO)) end AS COLOR_SG_SEMAFORO, "); //22
        appendQuery("sv.REDONDO, "); //23
        appendQuery(" -1 AS id_sg_estado_semaforo, "); //24
        appendQuery("(SELECT c.ID FROM SI_CIUDAD c WHERE c.ID=(SELECT vc.SI_CIUDAD FROM SG_VIAJE_CIUDAD vc WHERE vc.SG_SOLICITUD_VIAJE=sv.ID and vc.eliminado = 'False')) AS ID_SI_CIUDAD "); //25
        appendQuery(" ,sv.SG_RUTA_TERRESTRE "); //26
        appendQuery("FROM SG_SOLICITUD_VIAJE sv, ");
        appendQuery(" SG_TIPO_SOLICITUD_VIAJE tsv, ");
        appendQuery("SG_TIPO_ESPECIFICO te, ");
        appendQuery("ESTATUS e, ");
        appendQuery("GERENCIA g, ");
        appendQuery("SG_MOTIVO m, ");
        appendQuery("SG_OFICINA o ");
        appendQuery("WHERE sv.ESTATUS=").append(idEstatus).append(" ");
        appendQuery("AND sv.GENERO='").append(idUsuario).append("' ");
        appendQuery("AND sv.ELIMINADO='").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("AND sv.OFICINA_DESTINO IS NULL ");
        appendQuery("AND tsv.SG_TIPO_ESPECIFICO = 2 ");
        appendQuery("AND sv.id in (select vc.sg_solicitud_viaje from sg_viaje_ciudad vc where vc.sg_solicitud_viaje=sv.id) ");
        appendQuery("AND sv.ESTATUS=e.ID  ");
        appendQuery("AND sv.OFICINA_ORIGEN=o.ID  ");
        appendQuery("AND sv.GERENCIA_RESPONSABLE=g.ID ");
        appendQuery("AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
        appendQuery("AND tsv.SG_TIPO_ESPECIFICO=te.ID ");
        appendQuery("AND sv.SG_MOTIVO=m.ID ");
        appendQuery("AND sv.eliminado='").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");

        if (orderByField != null && !orderByField.isEmpty()) {
            appendQuery("ORDER BY sv.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        List<Object[]> result = em.createNativeQuery(query.toString()).getResultList();
        List<SolicitudViajeVO> list = new ArrayList<SolicitudViajeVO>();
        SolicitudViajeVO vo = null;

        for (Object[] objects : result) {
            vo = new SolicitudViajeVO();
            vo.setIdSolicitud((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setObservacion((String) objects[2]);
            vo.setFechaSalida((Date) objects[3]);
            vo.setHoraSalida((Date) objects[4]);
            vo.setFechaRegreso((Date) objects[5]);
            vo.setHoraRegreso((Date) objects[6]);
            vo.setGenero((String) objects[7]);
            vo.setIdOficinaOrigen((Integer) objects[8]);
            vo.setOrigen((String) objects[9]);
            vo.setDestino((String) objects[10]);
            vo.setIdSgTipoSolicitudViaje((Integer) objects[11]);
            vo.setTipoSolicitud(Constantes.SV_TERRESTRE); //(String) objects[12]
            vo.setIdEstatus((Integer) objects[13]);
            vo.setEstatus((String) objects[14]);
            vo.setIdGerencia((Integer) objects[15]);
            vo.setGerencia((String) objects[16]);
            vo.setIdSgMotivo((Integer) objects[17]);
            vo.setMotivo((String) objects[18]);
            vo.setIdSgTipoEspecifico((Integer) objects[19]);
            vo.setTipoEspecifico((String) objects[20]);
            vo.setCountSgViajero((Integer) objects[21]);
            vo.setColorSgSemaforo(((String) objects[23]) != null ? (String) objects[22] : "");
            vo.setRedondo((Boolean) (objects[23]));
//            vo.setIdEstadoSemaforo((Integer) objects[24]);
            vo.setIdSiCiudadDestino((Integer) objects[25]);
            vo.setIdRutaTerrestre((Integer) objects[26] != null ? (Integer) objects[26] : 0);

            if (vo.getColorSgSemaforo() != null && !vo.getColorSgSemaforo().equals("")) {
                /// consulsultar el semaforo actual
                vo.setSemaforoVo(crearSemaforoVO(vo.getIdSemaforo(), vo.getIdRutaTerrestre()));
            }
            list.add(vo);
        }
        UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgSolicitudViaje a ciudad usuario " + idUsuario + " estatus " + idEstatus);

        return (list.isEmpty()
                ? Collections.EMPTY_LIST : list);
    }

    
    public List<SolicitudViajeVO> getSgSolicitudViajeTerrestreToAereos(String idUsuario, int idEstatus, String orderByField, boolean sortAscending, boolean eliminado) {
        clearQuery();
        appendQuery("SELECT sv.ID,  "); //0
        appendQuery("sv.CODIGO, "); //1
        appendQuery("sv.OBSERVACION, "); //2
        appendQuery("sv.FECHA_SALIDA, "); //3
        appendQuery("sv.FECHA_REGRESO, "); //4
        appendQuery("sv.GENERO, "); //5
        appendQuery("sv.OFICINA_ORIGEN AS ID_OFICINA_ORIGEN, "); //6
        appendQuery("(SELECT oo.NOMBRE FROM SG_OFICINA oo WHERE oo.ID=sv.OFICINA_ORIGEN) AS NOMBRE_OFICINA_ORIGEN, "); //7
        appendQuery("(SELECT c.ID FROM SI_CIUDAD c WHERE c.ID=(SELECT i.SI_CIUDAD_ORIGEN FROM SG_ITINERARIO i WHERE i.SG_SOLICITUD_VIAJE=sv.ID AND i.IDA='True')) AS ID_SI_CIUDAD_ORIGEN, "); //8
        appendQuery("(SELECT c.NOMBRE FROM SI_CIUDAD c WHERE c.ID=(SELECT i.SI_CIUDAD_ORIGEN FROM SG_ITINERARIO i WHERE i.SG_SOLICITUD_VIAJE=sv.ID AND i.IDA='True')) AS NOMBRE_SI_CIUDAD_ORIGEN, "); //9
        appendQuery("(SELECT c.ID FROM SI_CIUDAD c WHERE c.ID=(SELECT i.SI_CIUDAD_DESTINO FROM SG_ITINERARIO i WHERE i.SG_SOLICITUD_VIAJE=sv.ID AND i.IDA='True')) AS ID_SI_CIUDAD_DESTINO, "); //10
        appendQuery("(SELECT c.NOMBRE FROM SI_CIUDAD c WHERE c.ID=(SELECT i.SI_CIUDAD_DESTINO FROM SG_ITINERARIO i WHERE i.SG_SOLICITUD_VIAJE=sv.ID AND i.IDA='True')) AS NOMBRE_SI_CIUDAD_DESTINO, "); //11
        appendQuery("tsv.ID as ID_SG_TIPO_SOLICITUD_VIAJE, "); //12
        appendQuery("tsv.NOMBRE AS NOMBRE_SG_TIPO_SOLICITUD_VIAJE, "); //13
        appendQuery("e.ID AS ID_ESTATUS, "); //14
        appendQuery("e.NOMBRE AS NOMBRE_ESTATUS, "); //15
        appendQuery("g.ID AS ID_GERENCIA, "); //16
        appendQuery("g.NOMBRE AS NOMBRE_GERENCIA, "); //17
        appendQuery("te.ID AS ID_SG_TIPO_ESPECIFICO, "); //18
        appendQuery("te.NOMBRE AS NOMBRE_SG_TIPO_ESPECIFICO,  "); //19
        appendQuery("(SELECT COUNT(v.id) FROM SG_VIAJERO v WHERE SG_SOLICITUD_VIAJE = sv.ID AND v.eliminado = 'False') AS COUNT_VIAJEROS, "); //20
        appendQuery("sv.REDONDO, "); //21
        appendQuery("sv.hora_salida, "); //22
        appendQuery("sv.hora_regreso "); //23
        appendQuery("FROM SG_SOLICITUD_VIAJE sv, ");
        appendQuery(" SG_TIPO_SOLICITUD_VIAJE tsv, ");
        appendQuery("SG_TIPO_ESPECIFICO te, ");
        appendQuery("ESTATUS e, ");
        appendQuery("GERENCIA g, ");
        appendQuery("SG_OFICINA o ");
        appendQuery("WHERE sv.ESTATUS=").append(idEstatus).append(" ");
        appendQuery("AND sv.GENERO='").append(idUsuario).append("' ");
        appendQuery("AND tsv.SG_TIPO_ESPECIFICO = 3 ");
        appendQuery("AND sv.OFICINA_DESTINO IS NULL ");
        appendQuery("AND sv.ESTATUS=e.ID  ");
        appendQuery("AND sv.OFICINA_ORIGEN=o.ID  ");
        appendQuery("AND sv.GERENCIA_RESPONSABLE=g.ID ");
        appendQuery("AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
        appendQuery("AND tsv.SG_TIPO_ESPECIFICO=te.ID ");
        appendQuery("AND sv.eliminado='").append(eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO).append("' ");

        if (orderByField != null && !orderByField.isEmpty()) {
            appendQuery("ORDER BY sv.").append(orderByField).append(" ").append(sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
        }

        Query queryLocal = em.createNativeQuery(this.query.toString());

//        UtilLog4j.log.info(this, query.toString());
        List<Object[]> result = queryLocal.getResultList();
        List<SolicitudViajeVO> list = new ArrayList<SolicitudViajeVO>();
        SolicitudViajeVO vo = null;

        for (Object[] objects : result) {
            vo = new SolicitudViajeVO();
            vo.setIdSolicitud((Integer) objects[0]);
            vo.setCodigo((String) objects[1]);
            vo.setObservacion((String) objects[2]);
            vo.setFechaSalida((Date) objects[3]);
            vo.setFechaRegreso((Date) objects[4]);
            vo.setGenero((String) objects[5]);
            vo.setIdOficinaOrigen((Integer) objects[6]);
            vo.setOrigen((String) objects[7]);
            vo.setIdSiCiudadOrigen((Integer) objects[8]);
            vo.setNombreSiCiudadOrigen((String) objects[9]);
            vo.setIdSiCiudadDestino((Integer) objects[10]);
            vo.setDestino((String) objects[11]);
            vo.setIdSgTipoSolicitudViaje((Integer) objects[12]);
            vo.setTipoSolicitud((String) objects[13]);
            vo.setIdEstatus((Integer) objects[14]);
            vo.setEstatus((String) objects[15]);
            vo.setIdGerencia((Integer) objects[16]);
            vo.setGerencia((String) objects[17]);
            vo.setIdSgTipoEspecifico((Integer) objects[18]);
            vo.setTipoEspecifico((String) objects[19]);
            vo.setCountSgViajero((Integer) objects[20]);
            vo.setRedondo((Boolean) (objects[21]));
            vo.setHoraSalida((Date) objects[22]);
            vo.setHoraRegreso((Date) objects[23]);
            list.add(vo);
        }

        UtilLog4j.log.info(this, "Se encontraron " + (list.isEmpty() ? "0" : list.size()) + " SgSolicitudViaje aereos para " + idUsuario + " estatus " + idEstatus);

        return (list.isEmpty() ? Collections.EMPTY_LIST : list);
    }

    
    public int totalSgSolicitudViajeTerretreToOficina(String idUsuario, int idEstatus) {

        try {
            clearQuery();
            appendQuery("SELECT COUNT(sv.ID)  "); //0
            appendQuery("FROM SG_SOLICITUD_VIAJE sv, ");
            appendQuery(" SG_TIPO_SOLICITUD_VIAJE tsv, ");
            appendQuery("SG_TIPO_ESPECIFICO te, ");
            appendQuery("ESTATUS e, ");
            appendQuery("GERENCIA g, ");
            appendQuery("SG_MOTIVO m, ");
            appendQuery("SG_OFICINA o ");
            appendQuery("WHERE sv.ESTATUS=").append(idEstatus).append(" ");
            appendQuery("AND sv.GENERO='").append(idUsuario).append("' ");
            appendQuery("AND tsv.SG_TIPO_ESPECIFICO = 2 ");
            appendQuery("AND sv.OFICINA_DESTINO IS NOT NULL ");
            appendQuery("AND sv.ESTATUS=e.ID  ");
            appendQuery("AND sv.OFICINA_ORIGEN=o.ID  ");
            appendQuery("AND sv.GERENCIA_RESPONSABLE=g.ID ");
            appendQuery("AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
            appendQuery("AND tsv.SG_TIPO_ESPECIFICO=te.ID ");
            appendQuery("AND sv.SG_MOTIVO=m.ID ");
            appendQuery("AND sv.eliminado='").append(Constantes.NO_ELIMINADO).append("' ");

            Query q = em.createNativeQuery(this.query.toString());

//            UtilLog4j.log.info(this, "Query " + q.toString());
            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "SgSolicitudViajeImpl.totalSgSolicitudViajeTerretreToOficina()" + e.getMessage());
            return 0;
        }
    }

    
    public int totalSgSolicitudViajeTerrestreToCiudad(String idUsuario, int idEstatus) {

        try {
            clearQuery();
            appendQuery("SELECT COUNT(sv.ID) "); //0
            appendQuery("FROM SG_SOLICITUD_VIAJE sv, ");
            appendQuery(" SG_TIPO_SOLICITUD_VIAJE tsv, ");
            appendQuery("SG_TIPO_ESPECIFICO te, ");
            appendQuery("ESTATUS e, ");
            appendQuery("GERENCIA g, ");
            appendQuery("SG_MOTIVO m, ");
            appendQuery("SG_OFICINA o ");
            appendQuery("WHERE sv.ESTATUS=").append(idEstatus).append(" ");
            appendQuery("AND sv.GENERO='").append(idUsuario).append("' ");
            appendQuery("AND sv.OFICINA_DESTINO IS NULL ");
            appendQuery("AND tsv.SG_TIPO_ESPECIFICO = 2 ");
            appendQuery("AND sv.id in (select vc.sg_solicitud_viaje from sg_viaje_ciudad vc where vc.sg_solicitud_viaje=sv.id) ");
            appendQuery("AND sv.ESTATUS=e.ID  ");
            appendQuery("AND sv.OFICINA_ORIGEN=o.ID  ");
            appendQuery("AND sv.GERENCIA_RESPONSABLE=g.ID ");
            appendQuery("AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
            appendQuery("AND tsv.SG_TIPO_ESPECIFICO=te.ID ");
            appendQuery("AND sv.SG_MOTIVO=m.ID ");
            appendQuery("AND sv.eliminado='").append(Constantes.NO_ELIMINADO).append("' ");

            Query q = em.createNativeQuery(this.query.toString());

//            UtilLog4j.log.info(this, "Query " + q.toString());
            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "SgSolicitudViajeImpl.totalSgSolicitudViajeTerrestreToCiudad()" + e.getMessage());
            return 0;
        }
    }

    
    public int totalSgSolicitudViajeTerrestreALugar(String idUsuario, int idEstatus) {

        try {
            clearQuery();
            appendQuery("SELECT COUNT(sv.ID) "); //0
            appendQuery("FROM SG_SOLICITUD_VIAJE sv, ");
            appendQuery(" SG_TIPO_SOLICITUD_VIAJE tsv, ");
            appendQuery("SG_TIPO_ESPECIFICO te, ");
            appendQuery("ESTATUS e, ");
            appendQuery("GERENCIA g, ");
            appendQuery("SG_MOTIVO m, ");
            appendQuery("SG_OFICINA o ");
            appendQuery("WHERE sv.ESTATUS=").append(idEstatus).append(" ");
            appendQuery("AND sv.GENERO='").append(idUsuario).append("' ");
            appendQuery("AND sv.OFICINA_DESTINO IS NULL ");
            appendQuery("AND tsv.SG_TIPO_ESPECIFICO = 2 ");
            appendQuery("AND sv.id in (select vl.sg_solicitud_viaje from sg_viaje_lugar vl where vl.sg_solicitud_viaje = sv.id) ");
            appendQuery("AND sv.ESTATUS=e.ID  ");
            appendQuery("AND sv.OFICINA_ORIGEN=o.ID  ");
            appendQuery("AND sv.GERENCIA_RESPONSABLE=g.ID ");
            appendQuery("AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
            appendQuery("AND tsv.SG_TIPO_ESPECIFICO=te.ID ");
            appendQuery("AND sv.SG_MOTIVO=m.ID ");
            appendQuery("AND sv.eliminado='").append(Constantes.NO_ELIMINADO).append("' ");

            Query q = em.createNativeQuery(this.query.toString());

//            UtilLog4j.log.info(this, "Query " + q.toString());
            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "SgSolicitudViajeImpl.totalSgSolicitudViajeTerrestreToCiudad()" + e.getMessage());
            return 0;
        }
    }

    
    public int totalSgSolicitudViajeToAereos(String idUsuario, int idEstatus) {

        try {
            clearQuery();
            appendQuery("SELECT COUNT(sv.ID)  "); //0
            appendQuery("FROM SG_SOLICITUD_VIAJE sv, ");
            appendQuery("SG_TIPO_SOLICITUD_VIAJE tsv, ");
            appendQuery("SG_TIPO_ESPECIFICO te, ");
            appendQuery("ESTATUS e, ");
            appendQuery("GERENCIA g, ");
            appendQuery("SG_OFICINA o ");
            appendQuery("WHERE sv.ESTATUS=").append(idEstatus).append(" ");
            appendQuery("AND sv.GENERO='").append(idUsuario).append("' ");
            appendQuery("AND tsv.SG_TIPO_ESPECIFICO = 3 ");
            appendQuery("AND sv.OFICINA_DESTINO IS NULL ");
            appendQuery("AND sv.ESTATUS=e.ID  ");
            appendQuery("AND sv.OFICINA_ORIGEN=o.ID  ");
            appendQuery("AND sv.GERENCIA_RESPONSABLE=g.ID ");
            appendQuery("AND sv.SG_TIPO_SOLICITUD_VIAJE=tsv.ID ");
            appendQuery("AND tsv.SG_TIPO_ESPECIFICO=te.ID ");
            appendQuery("AND sv.eliminado='").append(Constantes.NO_ELIMINADO).append("' ");
            Query q = em.createNativeQuery(this.query.toString());

//            UtilLog4j.log.info(this, "Query " + q.toString());
            return ((Integer) q.getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "SgSolicitudViajeImpl.totalSgSolicitudViajeToAereos()" + e.getMessage());
            return 0;
        }
    }

    
    public List<SolicitudViajeVO> buscarPorCodigo(String codigo, boolean eliminado) {
        try {
            List<SolicitudViajeVO> lv = new ArrayList<SolicitudViajeVO>();
            //                                         0                   1                  2                  3            4                  5          6               7
            StringBuilder q = new StringBuilder();
            q.append(" SELECT distinct v.id as viajeroID, u.id as usuarioID,  i.id as invitadoID,  s.codigo, s.fecha_salida,  s.hora_salida, s.fecha_regreso, s.hora_regreso, "
                    //          8                                    9         10           11         12              13                   14	
                    + "COALESCE(s.oficina_destino,0) as destinoOf, u.email, v.estancia, v.redondo ,u.telefono , u.NOMBRE as uNombre, i.NOMBRE as iNombre"
                    //                                  15                                            16              17                                           18             19             20                   21                     
                    + ", case when odes.id > 0 then odes.nombre else c.nombre end as desNombre, s.id as solID, COALESCE(r.id,0) as rutaID, COALESCE(r.SG_OFICINA,0) AS oficina, s.redondo as r2, c.id as destinoCD, o.nombre as orgNombre,"
                    //22    23              24                        25                      26               27         28               29                30          31      32        
                    + "e.id, te.id, s.gerencia_responsable, v.SG_SOLICITUD_ESTANCIA, g.NOMBRE as gerenciaName, e.nombre, s.CONCHOFER, s.fecha_modifico, s.hora_modifico, svi.id, ug.nombre,"
                    //      33         34          35      36
                    + " mot.nombre, mr.id,s.observacion,  te.nombre,"
                    //          37                        38                                   39                              
                    + " iti.id as idItinerario,coi.id as idciudadItinerarioOrigen, coi.nombre as ciudadOrigenIti,"
                    //             40                              41
                    + " cdi.id as idCiudadDestinioIti, cdi.nombre as ciudadDestinoIti"
                    + "  From  SG_VIAJERO v"
                    + "      inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID AND s.eliminado = ?"
                    + "      left join SG_RUTA_TERRESTRE r on r.id = s.SG_RUTA_TERRESTRE and r.ELIMINADO = ?"
                    + "      left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = r.id and drt.ELIMINADO = ?"
                    + "      left join SG_OFICINA odes on odes.ID = drt.SG_OFICINA and odes.ELIMINADO = ?"
                    + "      left join SG_DETALLE_RUTA_CIUDAD drc on drc.SG_RUTA_TERRESTRE = r.id and drc.ELIMINADO = ?"
                    + "      left join SI_CIUDAD c on c.id = drc.SI_CIUDAD and c.ELIMINADO = ?"
                    + "      inner join ESTATUS e on s.ESTATUS = e.ID"
                    + "      left join SG_OFICINA o on s.OFICINA_ORIGEN = o.ID"
                    + "      inner join SG_DIRECCION d on o.SG_DIRECCION = d.ID"
                    + "      left join USUARIO u on v.USUARIO = u.ID"
                    + "      left join SG_INVITADO i on v.SG_INVITADO = i.ID"
                    + "      inner join SG_TIPO_SOLICITUD_VIAJE ts on s.SG_TIPO_SOLICITUD_VIAJE = ts.ID"
                    + "      inner join SG_TIPO_ESPECIFICO te on ts.SG_TIPO_ESPECIFICO = te.ID"
                    + "      left join SG_VIAJERO_SI_MOVIMIENTO vm on vm.SG_VIAJERO  = v.ID  AND vm.eliminado = ?"
                    + "      inner join GERENCIA g on g.ID = s.GERENCIA_RESPONSABLE and g.ELIMINADO = ?"
                    + "      left join SG_SOL_VIAJE_INCUM svi on svi.SG_SOLICITUD_VIAJE = s.ID "
                    + "      INNER join USUARIO ug on ug.ID = s.GENERO "
                    + "      INNER JOIN SG_MOTIVO mot on mot.ID = s.SG_MOTIVO and mot.ELIMINADO= ?"
                    + "      LEFT JOIN SG_MOTIVO_RETRASO MR ON MR.ID = S.SG_MOTIVO_RETRASO AND MR.ELIMINADO= ?"
                    + "      left join sg_itinerario iti on iti.sg_solicitud_viaje = s.id"
                    + "      left join si_ciudad coi on coi.id = iti.si_ciudad_origen"
                    + "      left join si_ciudad cdi on cdi.id = iti.si_ciudad_destino"
                    + "      WHERE s.eliminado = false AND v.eliminado = false and s.codigo = ? and v.genero = s.genero"
                    + "      order by s.codigo, s.fecha_salida");

            List<Object[]> obj = em.createNativeQuery(q.toString())
                    .setParameter(1, Constantes.FALSE)
                    .setParameter(2, Constantes.FALSE)
                    .setParameter(3, Constantes.FALSE)
                    .setParameter(4, Constantes.FALSE)
                    .setParameter(5, Constantes.FALSE)
                    .setParameter(6, Constantes.FALSE)
                    .setParameter(7, Constantes.FALSE)
                    .setParameter(8, Constantes.FALSE)
                    .setParameter(9, Constantes.FALSE)
                    .setParameter(10, Constantes.FALSE)
                    .setParameter(11, codigo)
                    .getResultList();
            return castListSV(obj, lv);
        } catch (Exception e) {
            e.getStackTrace();
            UtilLog4j.log.info(this, "Exc: " + e.getMessage());
            return null;
        }

    }

    // jevazquez
    
    @Trace(dispatcher = true)
    public boolean solicitarViaje(String idusuario, SolicitudViajeVO solicitud,
            InvitadoVO visitoA, String mensaje, int idRol, List<Integer> listaIncumpl,
            int idLugar, boolean conChofer, int idVehiculo, String descripcion, int idUbicacion, int idApCampo) {
        /*Calendar cHoraSalida = Calendar.getInstance();
	 cHoraSalida.set(Calendar.HOUR_OF_DAY, horaSalida);
	 Date hs = cHoraSalida.getTime();*/
        Usuario usuario = usuarioRemote.find(idusuario);

        boolean v = false;
        int idSE = Constantes.CERO;
        int idMotivo = Constantes.CERO;

        List<ViajeroVO> listaViajerosVO = sgViajeroRemote.getAllViajerosList(solicitud.getIdSolicitud());

        try {
            ApCampo campo = apCampoRemote.find(idApCampo);
            SgSolicitudViaje sgSolicitudViaje = find(solicitud.getIdSolicitud());
            sgSolicitudViaje.setApCampo(campo);
            if (sgSolicitudViaje.getSgTipoEspecifico().getId() != Constantes.RUTA_TIPO_OFICINA) {
                sgSolicitudViaje.setOficinaDestino(sgSolicitudViaje.getOficinaOrigen());
            }
            idMotivo = sgSolicitudViaje.getSgMotivo().getId();
            if (sgSolicitudViaje.getSgTipoEspecifico().getId() == Constantes.SOLICITUDES_AEREA) {
                if (idMotivo != 580 && idMotivo != 18) {
                    idMotivo = 270;
                }
            }

            Date fechaFinEsytancia = new Date();
            if (sgSolicitudViaje.getFechaRegreso() != null) {
                fechaFinEsytancia = sgSolicitudViaje.getFechaRegreso();
            } else {
                fechaFinEsytancia = siManejoFechaLocal.fechaSumarDias(sgSolicitudViaje.getFechaSalida(), 1);
            }

            if (listaViajerosVO != null) {
                if (listaViajerosVO.size() == 1) {
                    ViajeroVO vo = listaViajerosVO.get(0);
                    if (vo.isEstancia()) {
                        SgSolicitudEstancia sse = sgSolicitudEstanciaRemote.guardarSolicitud(usuario, sgSolicitudViaje.getFechaSalida(), fechaFinEsytancia,
                                Constantes.ESTATUS_SOLICITUD_ESTANCIA_TEMPORAL, sgSolicitudViaje.getOficinaDestino().getId(), Constantes.BOOLEAN_FALSE, idMotivo,
                                (sgSolicitudViaje.getGerenciaResponsable() != null ? sgSolicitudViaje.getGerenciaResponsable().getId() : usuario.getGerencia().getId()),
                                idUbicacion, sgSolicitudViaje.getApCampo());
                        sgDetalleSolicitudEstanciaRemote.guardarHuespededSolicitudEstancia(usuario.getId(), sse.getId(), vo.getIdInvitado() == 0 ? true : false,
                                vo.getIdUsuario(), vo.getIdInvitado(), descripcion);
                        sgViajeroRemote.updateViajeroWithList(sse, listaViajerosVO, usuario);
                        idSE = sse.getId();
                    }

                } else if (listaViajerosVO.size() > 1) {
                    SgSolicitudEstancia sse = null;
                    for (ViajeroVO vo : listaViajerosVO) {
                        if (vo.isEstancia() && vo.isAgregado()) {
                            if (sse == null) {

                                sse = sgSolicitudEstanciaRemote.guardarSolicitud(usuario, sgSolicitudViaje.getFechaSalida(), fechaFinEsytancia,
                                        Constantes.ESTATUS_SOLICITUD_ESTANCIA_TEMPORAL, sgSolicitudViaje.getOficinaDestino().getId(), Constantes.BOOLEAN_FALSE, idMotivo,
                                        (sgSolicitudViaje.getGerenciaResponsable() != null ? sgSolicitudViaje.getGerenciaResponsable().getId() : usuario.getGerencia().getId()),
                                        idUbicacion, campo);
                                //  break;
                            }
                            sgDetalleSolicitudEstanciaRemote.guardarHuespededSolicitudEstancia(usuario.getId(), sse.getId(), vo.getIdInvitado() == 0 ? true : false,
                                    vo.getIdUsuario(), vo.getIdInvitado(), descripcion);
                            idSE = sse.getId();
                        }

                    }
                    sgViajeroRemote.updateViajeroWithList(sse, listaViajerosVO, usuario);

                }
                if (sgSolicitudViaje.getSgTipoEspecifico().getId() != Constantes.RUTA_TIPO_OFICINA) {
                    sgSolicitudViaje.setOficinaDestino(null);
                }
            }

            //apartir de aqui se revisan los imcumplimientos y se justifica el viaje finalizando con la solicitud del mismo
            if (visitoA != null) { // Motivo retraso
                int idTipoEspecifico = 0;
                if (listaIncumpl.size() == 1) {
                    idTipoEspecifico = listaIncumpl.get(0);
                } else {
                    if (listaIncumpl.contains(Constantes.SEMAFORO_NEGRO_TIPO_ESPECIFICO)) {
                        idTipoEspecifico = Constantes.SEMAFORO_NEGRO_TIPO_ESPECIFICO;
                    } else if (listaIncumpl.contains(Constantes.SEMAFORO_HORARIO_TIPO_ESPECIFICO)) {
                        idTipoEspecifico = Constantes.SEMAFORO_HORARIO_TIPO_ESPECIFICO;
                    } else if (listaIncumpl.contains(Constantes.FIN_SEMANA_TIPO_ESPECIFICO)) {
                        idTipoEspecifico = Constantes.FIN_SEMANA_TIPO_ESPECIFICO;
                    } else if (listaIncumpl.contains(Constantes.DIA_ANTERIOR_TIPO_ESPECIFICO)) {
                        idTipoEspecifico = Constantes.DIA_ANTERIOR_TIPO_ESPECIFICO;
                    }
                }
                //verificar si se utiliza la gernecia de ser asi sera un motivo por sv
                if (visitoA.getEmpresa() != null && !visitoA.getEmpresa().equals("") && sgSolicitudViaje.getGerenciaResponsable().getId() > 0) {
                    SgMotivoRetraso mr = sgMotivoRetrasoRemote.save(mensaje, sgSolicitudViaje.getHoraSalida(), visitoA.getIdInvitado(),
                            idLugar, usuario.getId(), idTipoEspecifico, visitoA.getUsuario());
                    sgSolicitudViaje.setSgMotivoRetraso(mr);
                    sgSolicitudViaje.setModifico(usuario);
                    sgSolicitudViaje.setFechaModifico(new Date());
                    sgSolicitudViaje.setHoraModifico(new Date());
                    edit(sgSolicitudViaje);
                    if (listaIncumpl != null) {
                        for (Integer integer : listaIncumpl) {
                            sgSolViajeIncumRemote.guardar(usuario.getId(), sgSolicitudViaje.getId(), integer);
                        }

                    }
                    v = sgEstatusAprobacionRemote.solicitarViaje(usuario.getEmail(), solicitud, usuario.getId(), idSE, conChofer, idVehiculo, idApCampo); //este se cambiara por si son varias
                }

            } else {
                if (mensaje == null || mensaje.equals("")) {
                    if (listaIncumpl != null) {
                        for (Integer integer : listaIncumpl) {

                            sgSolViajeIncumRemote.guardar(usuario.getId(), sgSolicitudViaje.getId(), integer);
                        }
                    }
                    v = sgEstatusAprobacionRemote.solicitarViaje(usuario.getEmail(), solicitud, usuario.getId(), idSE, conChofer, idVehiculo, idApCampo); //este se cambiara por si son varias
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al solicitar" + e);
            v = false;
        }
        return v;
    }

    /**
     * MLUIS 30/10/2013 REcupera todas las solicitudes entres solicitadas y por
     * hacer viaje Joel Rodriguez actualizacion : 27.02.2014 Ahora se
     * seleccionan las solicitudes de hoy y mañana con hora de salida mayor a la
     * de cambio de semaforo
     *
     */
    
    public List<SolicitudViajeVO> traerSolicitudesEnProcesoAprobacion(int idRuta, int dias) {
        try {
            clearQuery();
            appendQuery("SELECT  sol.id AS idEstatusAprobacion,");
            appendQuery(" es.nombre AS nombre_estatus,");//1
            appendQuery(" es.id ,");//2
            appendQuery(" sol.GENERO,");//3
            appendQuery(" sol.FECHA_GENERO,");//6
            appendQuery(" sol.HORA_GENERO,");//7
            appendQuery(" sol.sg_tipo_solicitud_viaje,");//7
            appendQuery(" esSem.sg_semaforo,");//7
            appendQuery(" case when sol.SG_ESTADO_SEMAFORO is not null then ");
            appendQuery("  (select s.COLOR ").append(" from SG_ESTADO_SEMAFORO es,");
            appendQuery(" SG_SEMAFORO s ").append(" where es.id = sol.sg_estado_semaforo").append(" and es.SG_SEMAFORO = s.ID)");
            appendQuery(" end ");//10
            appendQuery(" FROM sg_solicitud_viaje sol, estatus es, sg_estado_semaforo esSem, sg_tipo_solicitud_viaje tsv ");
            appendQuery(" WHERE sol.estatus between  ").append(Constantes.ESTATUS_VISTO_BUENO).append(" and ").append(Constantes.ESTATUS_PARA_HACER_VIAJE);
            appendQuery(" and esSem.sg_ruta_terrestre = ").append(idRuta);
            appendQuery(" and tsv.sg_tipo_especifico = ").append(Constantes.TIPO_ESPECIFICO_SOLICITUD_TERRESTRE);
            appendQuery(" and sol.sg_tipo_solicitud_viaje = tsv.id");
            appendQuery(" and sol.sg_estado_semaforo = esSem.id");
            appendQuery(" AND sol.estatus = es.id");
            appendQuery(" AND sol.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            if (dias > 0) {
                // appendQuery(" and sol.FECHA_SALIDA between cast('now' as date) and  (SELECT CURRENT_DATE - ").append(dias).append(")");
                appendQuery(" and ((sol.fecha_salida = cast('now' as date)");
                appendQuery(" and sol.HORA_salida > cast('now' as time))");
                appendQuery(" or (sol.fecha_salida = (SELECT CURRENT_DATE - ").append(dias).append(")))");
            }
            appendQuery(" order by sol.codigo ASC");

            UtilLog4j.log.info(this, "Q: " + query.toString());
            List<SolicitudViajeVO> le = new ArrayList<SolicitudViajeVO>();
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {

                SolicitudViajeVO vo = new SolicitudViajeVO();
                vo.setIdSolicitud((Integer) objects[0]);
                vo.setEstatus((String) objects[1]);
                vo.setIdEstatus((Integer) objects[2]);
                vo.setGenero((String) objects[3]);
                vo.setFechaGenero((Date) objects[4]);
                vo.setHoraGenero((Date) objects[5]);
                vo.setIdSemaforo((Integer) objects[6]);
                vo.setIdSgTipoSolicitudViaje((Integer) objects[7]);
                vo.setColorSgSemaforo((String) objects[8]);
                le.add(vo);
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "excepcion en traer el historial del estatus de aprobacion " + e.getMessage());
            return null;
        }

    }

    /*
     * Joel rodriguez modificado el 24/enero/2014 Este proceso realiza lo
     * siguiente 1. Buscar los viajeros que no se han subido a un viaje y este
     * viaje no esta esta en proceso. Caso: Se genero una solicitud de viaje con
     * 5 viajeros, pero solo 4 son agregados a un viaje, y este viaje se pone en
     * estatus En Proceso(510), solo 1 se queda en la bandeja del anañista. Este
     * proceso busca a ese viajero que no se ha subido a un viaje y cancela su
     * estancia (si es que ya ha sido hospedado ya sea en staff u hotel) y
     * finaliza la Solicitud solo con los 4 viajeros atendidos. MLUIS 03/02/2014
     * Se agrego 0 al rol que lo esta haciendo...
     */
    
    public boolean terminarSolicitudesPorProcesoSemaforoPorRuta(int idRuta, String motivoCancelacion, int dias) {
        TreeSet<Integer> listaIdSolicitudesPorTerminar = null;
        try {
            List<ViajeroVO> listViajeros = sgViajeroRemote.obtenerListaViajerosSolicitudSinAsignarAViajePorRuta(idRuta, dias);
            if (listViajeros != null && !listViajeros.isEmpty()) {
                UtilLog4j.log.info(this, "Comenzar a cancelar estancias  - ");
                //UtilLog4j.log.info(this, "Comenzar a cancelar estancias  - ");
                listaIdSolicitudesPorTerminar = new TreeSet<Integer>();
                for (ViajeroVO vo : listViajeros) {
                    //cancelar su estancia
                    if (vo.getSgSolicitudEstancia() != 0) {
                        sgViajeroRemote.lougueOfViajeroCancel(vo.getId(), motivoCancelacion, Constantes.USUARIO_SIA);
                    }

                    UtilLog4j.log.info(this, "agregar la solicitud de viaje  " + vo.getIdSolicitudViaje());
                    listaIdSolicitudesPorTerminar.add(vo.getIdSolicitudViaje());
                }
                UtilLog4j.log.info(this, "Comenzar a terminar solicitudes de estancias");
                //terminar lista de solicitudes de viajes obtenidas de viajeros
                for (Integer i : listaIdSolicitudesPorTerminar) {
                    sgEstatusAprobacionRemote.finalizeRequest(i, new Usuario(Constantes.USUARIO_SIA), 0);
                }
            }
            return true;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al terminar solicitudes de viajes en proceso de semaforo " + e.getMessage());
            return false;
        }
    }

    //clona la SV para crear otro igual con distinta gerencia y mueve a los viajeros que pertenecen a la nueva gerencia
    
    public SgSolicitudViaje clonarSolicitudViaje2(int id, int gerencia, int destinoCiudad) {

        SgSolicitudViaje sgSolicitudViaje = null;
        try {
            SolicitudViajeVO solicitudViajeVO = this.buscarPorId(id, Constantes.BOOLEAN_FALSE, Constantes.CERO);
            int idsgRuta = 0;
            if (solicitudViajeVO.getIdOficinaDestino() == 0) {
                if (solicitudViajeVO.getIdSgTipoEspecifico() != Constantes.TIPO_ESPECIFICO_SOLICITUD_AEREA) {

                    solicitudViajeVO.setIdRutaTerrestre(destinoCiudad);
                    idsgRuta = destinoCiudad;
                }

            } else {
                idsgRuta = sgRutaTerrestreRemote.findSgRutaTerrestreBySgOficinaOrigenAndSgOficinaDestino(solicitudViajeVO.getIdOficinaOrigen(),
                        solicitudViajeVO.getIdOficinaDestino(), Constantes.RUTA_TIPO_OFICINA).getId();
            }
            //Nuevas solicitudes
            Calendar fechaSalidaCompleta = Calendar.getInstance();
            Calendar fechaRegresoCompleta = Calendar.getInstance();
            Date fr = null;
            fechaSalidaCompleta.setTime(solicitudViajeVO.getFechaSalida());
            String[] arrHora = Constantes.FMT_HHmmss.format(solicitudViajeVO.getHoraSalida()).split(":");
            fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHora[0]));
            fechaSalidaCompleta.set(Calendar.MINUTE, Integer.parseInt(arrHora[1]));
            Date fs = fechaSalidaCompleta.getTime();

            if (solicitudViajeVO.isRedondo()) {
                fechaRegresoCompleta.setTime(solicitudViajeVO.getFechaRegreso());
                String[] arrHoraReg = Constantes.FMT_HHmmss.format(solicitudViajeVO.getHoraRegreso()).split(":");
                fechaRegresoCompleta.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHoraReg[0]));
                fechaRegresoCompleta.set(Calendar.MINUTE, Integer.parseInt(arrHoraReg[1]));
                fr = fechaRegresoCompleta.getTime();
            }

            int idSol = this.save(solicitudViajeVO.getIdSgTipoSolicitudViaje(),
                    gerencia, solicitudViajeVO.getIdOficinaOrigen(),
                    idsgRuta, solicitudViajeVO.getIdSgMotivo(),
                    solicitudViajeVO.getObservacion(), fs, fr, solicitudViajeVO.getIdSiCiudadOrigen(),
                    solicitudViajeVO.getIdSiCiudadDestino(), Constantes.FALSE, Constantes.FALSE, solicitudViajeVO.getGenero(),
                    Constantes.CERO, solicitudViajeVO.isRedondo(), solicitudViajeVO.getIdOficinaDestino(), Constantes.CERO, Constantes.BOOLEAN_TRUE);

            UtilLog4j.log.info(this, "Id sol viaje original: " + solicitudViajeVO.getIdSolicitud());
            UtilLog4j.log.info(this, "Id sol viaje nueva: " + idSol);
            UtilLog4j.log.info(this, "id gerencia" + gerencia);
            sgSolicitudViaje = this.find(idSol);

            return sgSolicitudViaje;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al clonar una solicitud de viaje " + e.getMessage());
            return null;
        }
    }

    
    public int clonarSolicitudViaje(int id, int newRutaID, int oldRutaID, List<ViajeroVO> lstVros
    ) {
        int idSol = 0;
        SgSolicitudViaje sgSolicitudViaje = null;
        try {
            SolicitudViajeVO solicitudViajeVO = this.buscarPorId(id, Constantes.BOOLEAN_FALSE, Constantes.CERO);
            //SemaforoVo semaforoActualVo = sgEstadoSemaforoRemote.buscarEstadoSemaforoPorId(solicitudViajeVO.getIdEstadoSemaforo());

            //Nuevas solicitudes
            Calendar fechaSalidaCompleta = Calendar.getInstance();
            Calendar fechaRegresoCompleta = Calendar.getInstance();
            Date fr = null;
            fechaSalidaCompleta.setTime(solicitudViajeVO.getFechaSalida());
            String[] arrHora = Constantes.FMT_HHmmss.format(solicitudViajeVO.getHoraSalida()).split(":");
            fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHora[0]));
            Date fs = fechaSalidaCompleta.getTime();

            if (solicitudViajeVO.isRedondo()) {
                fechaRegresoCompleta.setTime(solicitudViajeVO.getFechaRegreso());
                String[] arrHoraReg = Constantes.FMT_HHmmss.format(solicitudViajeVO.getHoraRegreso()).split(":");
                fechaRegresoCompleta.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHoraReg[0]));
                fr = fechaRegresoCompleta.getTime();
            }
            int idEstatus = 0;
            boolean propia = Constantes.TRUE;
            if (newRutaID > 0) {
                SgRutaTerrestre newRuta = sgRutaTerrestreRemote.find(newRutaID);
                SgRutaTerrestre oldRuta = sgRutaTerrestreRemote.find(oldRutaID);

                solicitudViajeVO.setIdRutaTerrestre(newRuta.getId());
                solicitudViajeVO.setIdOficinaOrigen(newRuta.getSgOficina().getId());
                solicitudViajeVO.setIdSiCiudadOrigen(newRuta.getSgOficina().getSgDireccion().getSiCiudad().getId());
                idEstatus = 450;
                propia = Constantes.FALSE;

                if (oldRuta != null && oldRuta.getTiempoViaje() != null && !oldRuta.getTiempoViaje().isEmpty()) {
                    if ("I".equals(isNumber(oldRuta.getTiempoViaje()))) {
                        fechaSalidaCompleta.add(Calendar.HOUR_OF_DAY, Integer.parseInt(oldRuta.getTiempoViaje()));
                        fs = fechaSalidaCompleta.getTime();
                    } else if ("D".equals(isNumber(oldRuta.getTiempoViaje()))) {
                        String entero = oldRuta.getTiempoViaje().substring(0, oldRuta.getTiempoViaje().indexOf("."));
                        String decimal = oldRuta.getTiempoViaje().substring(oldRuta.getTiempoViaje().indexOf(".") + 1,
                                (oldRuta.getTiempoViaje().length() - (oldRuta.getTiempoViaje().indexOf(".") + 1)) >= 1 ? oldRuta.getTiempoViaje().indexOf(".") + 2 : oldRuta.getTiempoViaje().length());
                        if (entero != null && "I".equals(isNumber(entero))) {
                            int horas = Integer.parseInt(entero);
                            fechaSalidaCompleta.add(Calendar.HOUR_OF_DAY, horas);
                        }
                        if (decimal != null && "I".equals(isNumber(decimal))) {
                            int minutos = BigDecimal.valueOf((Integer.parseInt(decimal) * 6)).intValue();
                            fechaSalidaCompleta.add(Calendar.MINUTE, minutos > 60 ? 59 : minutos);
                        }
                        fs = fechaSalidaCompleta.getTime();

                    }
                }
            }

            solicitudViajeVO.setFechaSalida(fs);
            solicitudViajeVO.setFechaRegreso(fr);

            idSol = this.save(solicitudViajeVO, propia, false, idEstatus);

            UtilLog4j.log.info(this, "Id sol viaje cancelada: " + solicitudViajeVO.getIdSolicitud());
            UtilLog4j.log.info(this, "Id sol viaje nueva: " + idSol);
            UtilLog4j.log.info(this, "paso acá 1");
            List<ViajeroVO> lvro = new ArrayList<ViajeroVO>();
            if (lstVros == null) {
                lvro.addAll(sgViajeroRemote.getAllViajerosList(solicitudViajeVO.getIdSolicitud(), true));
            } else {
                lvro.addAll(lstVros);
            }

            sgSolicitudViaje = this.find(idSol);
            for (ViajeroVO viajeroVO : lvro) {
                SgViajero sgViajero = new SgViajero();
                if (viajeroVO.getIdInvitado() > 0) {
                    sgViajero.setSgInvitado(sgInvitadoRemote.find(viajeroVO.getIdInvitado()));
                } else {
                    sgViajero.setUsuario(new Usuario(viajeroVO.getIdUsuario()));
                }
                sgViajero.setSgSolicitudViaje(sgSolicitudViaje);
                sgViajero.setEstancia(Constantes.BOOLEAN_FALSE);
                sgViajeroRemote.save(sgViajero, sgSolicitudViaje.getGenero().getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al clonar una solicitud de viaje " + e.getMessage());
            idSol = 0;
        }
        return idSol;
    }

    private String isNumber(String num) {
        String ret = null;
        try {
            if (num != null && !num.isEmpty()) {
                if (num.contains(".")) {
                    double d = Double.parseDouble(num);
                    ret = "D";
                } else {
                    int i = Integer.parseInt(num);
                    ret = "I";
                }
            }
        } catch (Exception e) {
            ret = null;
        }
        return ret;
    }

    
    public List<SolicitudViajeVO> buscarTotalSolicitudViaje(int idOficina, int tipoSolicitud, String inicio, String fin) {
        List<SolicitudViajeVO> lsol = new ArrayList<SolicitudViajeVO>();
        try {
            clearQuery();
            query.append("select g.NOMBRE, count(*) from SG_SOLICITUD_VIAJE sv");
            query.append("      inner join GERENCIA g on sv.GERENCIA_RESPONSABLE = g.ID");
            query.append("  where sv.OFICINA_ORIGEN = ").append(idOficina);
            query.append("  and sv.SG_TIPO_ESPECIFICO = ").append(tipoSolicitud);
            query.append("  and sv.ESTATUS <> ").append(Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO);
            query.append("  and sv.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            query.append("  and sv.ELIMINADO ='").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  group by g.NOMBRE");
            //
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                lsol.add(castTotalSol(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los totales de sol viaje : : : : : : : :: " + e.getMessage());
        }
        return lsol;
    }

    private SolicitudViajeVO castTotalSol(Object[] objects) {
        SolicitudViajeVO sol = new SolicitudViajeVO();
        sol.setGerencia((String) objects[0]);
        sol.setTotal((Integer) objects[1]);
        return sol;
    }

    
    public List<SolicitudViajeVO> traerSolicitudPorGerencia(int oficina, int tipoSolicitud, String gerencia, String inicio, String fin) {
        List<SolicitudViajeVO> lsol = new ArrayList<SolicitudViajeVO>();
        try {
            clearQuery();
            query.append("select sv.CODIGO, g.NOMBRE, u.NOMBRE, inv.NOMBRE,  sv.FECHA_SALIDA, sv.HORA_SALIDA, sv.FECHA_REGRESO, mot.nombre,c.NOMBRE, cid.NOMBRE, odes.NOMBRE from SG_VIAJERO vj");
            query.append("      left join SG_INVITADO inv on vj.SG_INVITADO = inv.ID");
            query.append("      left join USUARIO u on vj.USUARIO = u.ID");
            query.append("      inner join SG_SOLICITUD_VIAJE sv on vj.SG_SOLICITUD_VIAJE = sv.ID");
            query.append("      left join SG_MOTIVO mot on sv.SG_MOTIVO = mot.ID");
            query.append("      inner join GERENCIA g on sv.GERENCIA_RESPONSABLE = g.ID");
            query.append("      inner join SG_OFICINA ori on sv.OFICINA_ORIGEN = ori.ID");
            query.append("      left join SG_VIAJE_CIUDAD vc on vc.SG_SOLICITUD_VIAJE = sv.ID");
            query.append("      left join SI_CIUDAD c on vc.SI_CIUDAD = c.ID");
            query.append("      left join SG_ITINERARIO it on it.SG_SOLICITUD_VIAJE = sv.ID and it.IDA = 'True'");
            query.append("      left join SI_CIUDAD cid on it.SI_CIUDAD_DESTINO = cid.ID");
            query.append("      left join SG_OFICINA odes on  sv.OFICINA_DESTINO = odes.ID");
            query.append("      ");
            query.append("  where sv.OFICINA_ORIGEN = ").append(oficina);
            query.append("  and g.nombre = '").append(gerencia).append("'");
            query.append("  and sv.sg_tipo_especifico = ").append(tipoSolicitud);
            query.append("  and sv.ESTATUS <> ").append(Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO);
            query.append("  and sv.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            query.append("  and sv.ELIMINADO ='").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  group by sv.CODIGO, g.NOMBRE, u.NOMBRE, inv.NOMBRE, sv.FECHA_GENERO,  sv.FECHA_SALIDA, sv.HORA_SALIDA, sv.FECHA_REGRESO, mot.nombre,c.NOMBRE, cid.NOMBRE, odes.NOMBRE  ");
            query.append("  order by sv.codigo asc");
//
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : lo) {
                lsol.add(castReporteSol(objects, tipoSolicitud));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer los datos de sol viaje : : : : : : : :: " + e.getMessage());
        }
        return lsol;
    }

    private SolicitudViajeVO castReporteSol(Object[] objects, int tipoSol) {
        SolicitudViajeVO sol = new SolicitudViajeVO();
        String viajero = ((String) objects[2]) != null ? (String) objects[2] : (String) objects[3];
        sol.setCodigo((String) objects[0]);
        sol.setGerencia((String) objects[1]);
        sol.setViajero(viajero);
        sol.setFechaSalida((Date) objects[4]);
        sol.setHoraSalida((Date) objects[5]);
        sol.setFechaRegreso((Date) objects[6]);// != null ? (Date) objects[6] : null);
        sol.setMotivo((String) objects[7]);
        switch (tipoSol) {
            case Constantes.RUTA_TIPO_CIUDAD:
                sol.setDestino((String) objects[8]);
                break;
            case Constantes.SOLICITUDES_AEREA:
                sol.setDestino((String) objects[9]);
                break;
            default:
                sol.setDestino((String) objects[10]);
                break;
        }
        return sol;
    }

    
    public SgSolicitudViaje traerUltimaSolicitud(String usuario) {
        int ultimoViaje = 0;
        SgSolicitudViaje sv = null;
        clearQuery();
        appendQuery("SELECT MAX (ID) ");
        appendQuery("FROM SG_SOLICITUD_VIAJE ");
        appendQuery("WHERE GENERO = '").append(usuario).append("' ");
        appendQuery("AND ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ");
        appendQuery("AND ESTATUS = ").append(Constantes.ESTATUS_PENDIENTE).append(" ");
        appendQuery("AND SG_TIPO_SOLICITUD_VIAJE = ").append(Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE);
        try {
            ultimoViaje = ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "traerUltimaSolicitud " + e);
            ultimoViaje = 0;

        }

        if (ultimoViaje > 0) {
            sv = find(ultimoViaje);
        }

        return sv;

    }

    
    public List<SolicitudViajeVO> clonarSolicitudes(List<ViajeroVO> vo, int idSolicitud, CampoUsuarioPuestoVo campoUsuarioPuestoVo, int idDestinoCiudad, SolicitudViajeVO svo) {
        List<Integer> gerencias = new ArrayList<>();
        List<SolicitudViajeVO> listSolicitudes = new ArrayList<>();
        if (vo != null) {
            if (vo.size() > 1) {
                for (ViajeroVO viajero : vo) {
                    if (viajero.getIdUsuario().equals(campoUsuarioPuestoVo.getUsuario())) {
                        viajero.setIdGerencia(campoUsuarioPuestoVo.getIdGerencia());
                    }
                    if (viajero.isEmpleado() && viajero.isAgregado()) {
                        if (!gerencias.contains(viajero.getIdGerencia())) {
                            gerencias.add(viajero.getIdGerencia());
                        }
                    }
                }
                SgSolicitudViaje sViaje = new SgSolicitudViaje();
                if (gerencias.contains(campoUsuarioPuestoVo.getIdGerencia())) {
                    listSolicitudes.add(svo);
                    gerencias.remove(gerencias.indexOf(campoUsuarioPuestoVo.getIdGerencia()));
                } else {
                    if (gerencias.isEmpty()) {
                        gerencias.add(campoUsuarioPuestoVo.getIdGerencia());
                    }
                    sViaje = find(idSolicitud);
                    Gerencia g = gerenciaRemote.find(gerencias.get(Constantes.CERO));
                    sViaje.setGerenciaResponsable(g);
                    edit(sViaje);
                    svo.setGerencia(g.getNombre());
                    svo.setIdGerencia(g.getId());
                    svo.setIdSolicitud(sViaje.getId());
                    gerencias.remove(Constantes.CERO);
                    listSolicitudes.add(svo);
                }

                for (int gerencia : gerencias) {
                    SolicitudViajeVO newSVO = null;
                    try {
                        newSVO = svo.clone();
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(SgSolicitudViajeImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    sViaje = clonarSolicitudViaje2(idSolicitud, gerencia, idDestinoCiudad);
                    // newSVO = svo;
                    newSVO.setGerencia(sViaje.getGerenciaResponsable().getNombre());
                    newSVO.setIdGerencia(sViaje.getGerenciaResponsable().getId());
                    newSVO.setIdSolicitud(sViaje.getId());
                    listSolicitudes.add(newSVO);
                }
            } else if (vo.size() == 1) {
                SgSolicitudViaje sv = new SgSolicitudViaje();
                if (!vo.get(0).getIdUsuario().equals(campoUsuarioPuestoVo.getUsuario())) {
                    if (vo.get(0).getIdGerencia() != campoUsuarioPuestoVo.getIdGerencia() && vo.get(0).isEmpleado()) {
                        sv = find(idSolicitud);
                        sv.setGerenciaResponsable(gerenciaRemote.find(vo.get(0).getIdGerencia()));
                        edit(sv);
                        svo.setGerencia(sv.getGerenciaResponsable().getNombre());
                        svo.setIdGerencia(sv.getGerenciaResponsable().getId());
                        listSolicitudes.add(svo);
                    }
                }

            }
        }
        return listSolicitudes;
    }

    //Metodo que divide la solicitud de viajes de Mty-SF o SF-Mty en 2 solicitudes distintas haciendo escala en reynosa
    
    public List<SgSolicitudViaje> dividirSolicitud(List<ViajeroVO> vo, int idSolicitud, String u) {

        List<SgSolicitudViaje> list = new ArrayList<SgSolicitudViaje>();
        try {
            SgSolicitudViaje sViaje = find(idSolicitud);
            SgSolicitudViaje sViaje2 = clonarSolicitudViaje2(idSolicitud, sViaje.getGerenciaResponsable().getId(), Constantes.CERO);
            int hrIda = 0, hrRegreso = 0;
            double minIda = 0, minRegreso = 0;
            if (sViaje.getOficinaOrigen().getId() == Constantes.ID_OFICINA_TORRE_MARTEL) {
                hrIda = 4;
                hrRegreso = 2;
            } else {
                hrIda = 2;
                hrRegreso = 4;
            }
            if (sViaje.getSgRutaTerrestre() != null) {
                if (sViaje.getSgRutaTerrestre().getId() == Constantes.RUTA_MTY_SF) {
                    sViaje.setSgRutaTerrestre(sgRutaTerrestreRemote.find(Constantes.RUTA_MTY_REY));
                    sViaje2.setSgRutaTerrestre(sgRutaTerrestreRemote.find(Constantes.RUTA_REY_SF));
                } else {
                    sViaje.setSgRutaTerrestre(sgRutaTerrestreRemote.find(Constantes.RUTA_SF_REY));
                    sViaje2.setSgRutaTerrestre(sgRutaTerrestreRemote.find(Constantes.RUTA_REY_MTY));
                }
            } else {
                if (sViaje.getOficinaOrigen().getId() == Constantes.ID_OFICINA_TORRE_MARTEL) {
                    sViaje.setSgRutaTerrestre(sgRutaTerrestreRemote.find(Constantes.RUTA_MTY_REY));
                    sViaje2.setSgRutaTerrestre(sgRutaTerrestreRemote.find(Constantes.RUTA_REY_SF));
                } else {
                    sViaje.setSgRutaTerrestre(sgRutaTerrestreRemote.find(Constantes.RUTA_SF_REY));
                    sViaje2.setSgRutaTerrestre(sgRutaTerrestreRemote.find(Constantes.RUTA_REY_MTY));
                }
            }
            double hS = Double.parseDouble(sViaje.getSgRutaTerrestre().getTiempoViaje());
            hrIda = (int) hS;
            minIda = (hS - hrIda) * 60;
            double hR = Double.parseDouble(sViaje2.getSgRutaTerrestre().getTiempoViaje());
            hrRegreso = (int) hR;
            minRegreso = (hR - hrIda) * 60;
            sViaje.setOficinaDestino(sgOficinaRemote.find(Constantes.ID_OFICINA_REY_PRINCIPAL));
            if (sViaje.isRedondo()) {
                sViaje.setHoraRegreso(siManejoFechaLocal.sumarTiempo(sViaje.getHoraRegreso(), hrRegreso, ((int) minRegreso)));
            }
            edit(sViaje);
            list.add(sViaje);
            sViaje2.setHoraSalida(siManejoFechaLocal.sumarTiempo(sViaje2.getHoraSalida(), hrIda, ((int) minIda)));
            sViaje2.setOficinaOrigen(sgOficinaRemote.find(Constantes.ID_OFICINA_REY_PRINCIPAL));

            edit(sViaje2);
            list.add(sViaje2);
            if (vo.size() > 0) {
                for (ViajeroVO viajero : vo) {
                    sgViajeroRemote.guardarViajero(viajero.getIdInvitado(), viajero.getIdUsuario(), Constantes.CERO, sViaje2.getId(),
                            Constantes.CERO, viajero.getObservacion(), u, viajero.isEstancia(), viajero.isRedondo());
                    viajero.setEstancia(false);
                    viajero.setEstanciaB(false);
                    SgViajero editViajero = sgViajeroRemote.find(viajero.getId());
                    editViajero.setEstancia(false);
                    sgViajeroRemote.edit(editViajero);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(e);
        }

        return list;
    }

    
    public List<SolicitudViajeVO> traerSolicitudesTerrestre(int sgOficina, int status, String idUsuario, Date fecha1, Date fecha2, String textBusqueda, int idSolicitud) {
        List<SolicitudViajeVO> lv = new ArrayList<SolicitudViajeVO>();
        try {
            clearQuery();
            String oficinaOrigen = "";
            if (//idSolicitud == 0 &&
                    sgOficina > 0) {
                oficinaOrigen = " AND s.Oficina_Origen = " + sgOficina;
            }

            String oficinaDestino = "";
            if (//idSolicitud == 0 &&
                    sgOficina > 0) {
                oficinaDestino = " AND s.OFICINA_DESTINO = " + sgOficina;
            }

            String esOficinaReynosa = "";
            if (//idSolicitud == 0 &&
                    sgOficina > 0) {
                esOficinaReynosa = " AND " + Constantes.ID_OFICINA_REY_PRINCIPAL + " = " + sgOficina;
            }

            String estatusDet = "";
            if (//idSolicitud == 0 &&
                    status > 0) {
                estatusDet = " AND s.ESTATUS  = " + status;
            }

            String fechasDetSalida = "";
            String fechasDetRegreso = "";
            if (//idSolicitud == 0 &&
                    fecha1 != null && fecha2 != null) {
                fechasDetSalida = " and s.FECHA_SALIDA >=  '" + Constantes.FMT_yyyy_MM_dd.format(fecha1)
                        + "'  and s.FECHA_SALIDA <= '" + Constantes.FMT_yyyy_MM_dd.format(fecha2) + "' ";
                fechasDetRegreso = " and s.FECHA_REGRESO >=  '" + Constantes.FMT_yyyy_MM_dd.format(fecha1)
                        + "'  and s.FECHA_REGRESO <= '" + Constantes.FMT_yyyy_MM_dd.format(fecha2) + "' ";
            }

            String busquedaDet = "";
            if (//idSolicitud == 0 &&
                    textBusqueda != null && !textBusqueda.isEmpty()) {
                busquedaDet = " and (upper(s.CODIGO) like upper('%" + textBusqueda
                        + "%') or upper (u.NOMBRE) like upper ('%" + textBusqueda + "%')) ";
            }

            String joins = "      left join SG_VIAJERO_SI_MOVIMIENTO vm on vm.SG_VIAJERO  = v.ID  AND vm.eliminado = 'False'"
                    + "  WHERE s.eliminado = '" + Constantes.NO_ELIMINADO + "'"
                    + "  AND vm.id is null  ";

            String solicitudIDval = "";
            if (idSolicitud > 0) {
                solicitudIDval = " and s.id =  " + idSolicitud;
//                joins = " WHERE s.eliminado = '" + Constantes.NO_ELIMINADO + "' " +
//                        " and v.id not in (select a.SG_VIAJERO "+
//                        " from SG_VIAJERO_SI_MOVIMIENTO a "+
//                        " inner join SI_MOVIMIENTO m on m.ID = a.SI_MOVIMIENTO and m.SI_OPERACION = " + Constantes.QUEDADO_OFICINA_DESTINO +
//                        " where a.SG_VIAJERO =  v.id)";
            }
            //                                            0                   1                  2                  3            4                  5          6               7
            String selectColums = " SELECT distinct v.ID as viajeroID, u.id as usuarioID,  i.id as invitadoID,  s.codigo, s.fecha_salida,  s.hora_salida, s.fecha_regreso, s.hora_regreso, "
                    + "s.oficina_destino as destinoOf, u.email, v.estancia, v.redondo ,u.telefono , u.NOMBRE as uNombre, i.NOMBRE as iNombre";
            //          8                         9         10           11         12              13                   14
            //                                            0                   1                  2                  3            4                  5          6               7
            String selectColumsEscala = " SELECT distinct v.ID as viajeroID, u.id as usuarioID,  i.id as invitadoID,  s.codigo, s.fecha_salida,  s.hora_salida, s.fecha_regreso, s.hora_regreso, "
                    + "case when m.id > 0 then o.id else odes.id end as destinoOf, u.email, v.estancia, v.redondo ,u.telefono , u.NOMBRE as uNombre, i.NOMBRE as iNombre";
            //          8                         9         10           11         12              13                   14

            query.append(" select * from (" + selectColums + ", case when odes.id > 0 then odes.nombre else c.nombre end as desNombre, s.id as solID, r.id as rutaID, " + Constantes.PRIMERA_VEZ_VIAJE + " as tipo, r.SG_OFICINA, s.redondo as r2, c.id as destinoCD, o.nombre as orgNombre "
                    //                                                                       15                                            16              17                           18                           19             20            21                      22
                    + "  From  SG_VIAJERO v"
                    + "      inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID AND s.eliminado = 'False'"
                    + "      inner join SG_RUTA_TERRESTRE r on r.id = s.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False'"
                    + "      left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = r.id and drt.ELIMINADO = 'False'"
                    + "      left join SG_OFICINA odes on odes.ID = drt.SG_OFICINA and odes.ELIMINADO = 'False'"
                    + "      left join SG_DETALLE_RUTA_CIUDAD drc on drc.SG_RUTA_TERRESTRE = r.id and drc.ELIMINADO = 'False'"
                    + "      left join SI_CIUDAD c on c.id = drc.SI_CIUDAD and c.ELIMINADO = 'False'"
                    + "      inner join ESTATUS e on s.ESTATUS = e.ID"
                    + "      left join SG_OFICINA o on s.OFICINA_ORIGEN = o.ID"
                    + "      inner join SG_DIRECCION d on o.SG_DIRECCION = d.ID"
                    + "      inner join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID AND ea.realizado = 'False' AND ea.historial = 'False' "
                    + "      left join USUARIO u on v.USUARIO = u.ID"
                    + "      left join SG_INVITADO i on v.SG_INVITADO = i.ID"
                    + "      inner join SG_TIPO_SOLICITUD_VIAJE ts on s.SG_TIPO_SOLICITUD_VIAJE = ts.ID"
                    + "      inner join SG_TIPO_ESPECIFICO te on ts.SG_TIPO_ESPECIFICO = te.ID"
                    + joins
                    + "  AND te.ID = " + Constantes.SOLICITUDES_TERRESTRE
                    + "  AND v.sg_viaje is null  "
                    + "  AND v.eliminado = '" + Constantes.NO_ELIMINADO + "'");

//            if (idSolicitud > 0) {
//                query.append(solicitudIDval);
//            } else {
            query.append(
                    oficinaOrigen
                    + estatusDet
                    + fechasDetSalida
                    + busquedaDet
                    + solicitudIDval
                    + " union "
                    + selectColums + ", case when odes.id > 0 then odes.nombre else c.nombre end as desNombre, s.id as solID, r.id as rutaID, " + Constantes.QUEDADO_OFICINA_DESTINO + " as tipo, r.SG_OFICINA, s.redondo as r2, c.id as destinoCD, o.nombre as orgNombre"
                    //                                                                       15                    16              17                           18                                  19             20               21                     22
                    + " From  SG_VIAJERO v       "
                    + " inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID AND s.eliminado = 'False'       "
                    + " inner join SG_RUTA_TERRESTRE r on r.id = s.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False'       "
                    + " left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = r.id and drt.ELIMINADO = 'False'       "
                    + " left join SG_OFICINA odes on odes.ID = r.SG_OFICINA and odes.ELIMINADO = 'False'       "
                    + " left join SG_DETALLE_RUTA_CIUDAD drc on drc.SG_RUTA_TERRESTRE = r.id and drc.ELIMINADO = 'False'       "
                    + " left join SI_CIUDAD c on c.id = drc.SI_CIUDAD and c.ELIMINADO = 'False'       "
                    + " inner join ESTATUS e on s.ESTATUS = e.ID       "
                    + " left join SG_OFICINA o on s.OFICINA_ORIGEN = o.ID       "
                    + " inner join SG_DIRECCION d on o.SG_DIRECCION = d.ID       "
                    + " inner join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID AND ea.realizado = 'False' AND ea.historial = 'False'        "
                    + " left join USUARIO u on v.USUARIO = u.ID       "
                    + " left join SG_INVITADO i on v.SG_INVITADO = i.ID       "
                    + " inner join SG_TIPO_SOLICITUD_VIAJE ts on s.SG_TIPO_SOLICITUD_VIAJE = ts.ID       "
                    + " inner join SG_TIPO_ESPECIFICO te on ts.SG_TIPO_ESPECIFICO = te.ID       "
                    + " inner join SG_VIAJERO_SI_MOVIMIENTO vjm on vjm.SG_VIAJERO = v.ID AND vjm.ELIMINADO = 'False' "
                    + " inner join SI_MOVIMIENTO m on vjm.SI_MOVIMIENTO = m.ID and m.SI_OPERACION = " + Constantes.QUEDADO_OFICINA_DESTINO
                    + " WHERE s.eliminado = 'False'   "
                    + "  AND te.ID = " + Constantes.SOLICITUDES_TERRESTRE
                    + "  AND v.sg_viaje is null  "
                    + "  AND v.eliminado = '" + Constantes.NO_ELIMINADO + "'"
                    + oficinaDestino
                    + estatusDet
                    + fechasDetRegreso
                    + busquedaDet
                    + solicitudIDval
                    + " union "
                    + selectColums + ", case when odes.id > 0 then odes.nombre else c.nombre end as desNombre, s.id as solID, r.id as rutaID, " + Constantes.QUEDADO_ORIGEN + " as tipo, r.SG_OFICINA, s.redondo as r2, c.id as destinoCD, o.nombre as orgNombre "
                    //                                                                       15                     16              17                           18                           19             20                21             22
                    + " From  SG_VIAJERO v       "
                    + " inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID AND s.eliminado = 'False'       "
                    + " inner join SG_RUTA_TERRESTRE r on r.id = s.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False'       "
                    + " left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = r.id and drt.ELIMINADO = 'False'       "
                    + " left join SG_OFICINA odes on odes.ID = drt.SG_OFICINA and odes.ELIMINADO = 'False'       "
                    + " left join SG_DETALLE_RUTA_CIUDAD drc on drc.SG_RUTA_TERRESTRE = r.id and drc.ELIMINADO = 'False'       "
                    + " left join SI_CIUDAD c on c.id = drc.SI_CIUDAD and c.ELIMINADO = 'False'       "
                    + " inner join ESTATUS e on s.ESTATUS = e.ID       "
                    + " left join SG_OFICINA o on s.OFICINA_ORIGEN = o.ID       "
                    + " inner join SG_DIRECCION d on o.SG_DIRECCION = d.ID       "
                    + " inner join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID AND ea.realizado = 'False' AND ea.historial = 'False'        "
                    + " left join USUARIO u on v.USUARIO = u.ID       "
                    + " left join SG_INVITADO i on v.SG_INVITADO = i.ID       "
                    + " inner join SG_TIPO_SOLICITUD_VIAJE ts on s.SG_TIPO_SOLICITUD_VIAJE = ts.ID       "
                    + " inner join SG_TIPO_ESPECIFICO te on ts.SG_TIPO_ESPECIFICO = te.ID       "
                    + " inner join SG_VIAJERO_SI_MOVIMIENTO vjm on vjm.SG_VIAJERO = v.ID AND vjm.ELIMINADO = 'False' "
                    + " inner join SI_MOVIMIENTO m on vjm.SI_MOVIMIENTO = m.ID and m.SI_OPERACION = " + Constantes.QUEDADO_ORIGEN
                    + " WHERE s.eliminado = 'False'   "
                    + "  AND te.ID = " + Constantes.SOLICITUDES_TERRESTRE
                    + "  AND v.sg_viaje is null  "
                    + "  AND v.eliminado = '" + Constantes.NO_ELIMINADO + "'"
                    + oficinaOrigen
                    + estatusDet
                    + fechasDetSalida
                    + busquedaDet
                    + solicitudIDval
                    + " union "
                    + selectColumsEscala + ", case when m.id > 0 then o.nombre else odes.nombre end as desNombre, s.id as solID, r.id as rutaID, " + Constantes.VIAJERO_ESCALA + " as tipo, " + Constantes.ID_OFICINA_REY_PRINCIPAL + " as SG_OFICINA, s.redondo as r2, case when m.id > 0 then o.id else odes.id end as destinoID, o.nombre as orgNombre "
                    //                                                                       15                  16              17                           18                           19                                                      20                                       21                                           22
                    + " From  SG_VIAJERO v       "
                    + " inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID AND s.eliminado = 'False'       "
                    + " inner join SG_RUTA_TERRESTRE r on r.id = s.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False'       "
                    + " left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = r.id and drt.ELIMINADO = 'False'       "
                    + " left join SG_OFICINA odes on odes.ID = drt.SG_OFICINA and odes.ELIMINADO = 'False'       "
                    + " left join SG_DETALLE_RUTA_CIUDAD drc on drc.SG_RUTA_TERRESTRE = r.id and drc.ELIMINADO = 'False'       "
                    + " left join SI_CIUDAD c on c.id = drc.SI_CIUDAD and c.ELIMINADO = 'False'       "
                    + " inner join ESTATUS e on s.ESTATUS = e.ID       "
                    + " left join SG_OFICINA o on s.OFICINA_ORIGEN = o.ID       "
                    + " inner join SG_DIRECCION d on o.SG_DIRECCION = d.ID       "
                    + " inner join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID AND ea.realizado = 'False' AND ea.historial = 'False'        "
                    + " left join USUARIO u on v.USUARIO = u.ID       "
                    + " left join SG_INVITADO i on v.SG_INVITADO = i.ID       "
                    + " inner join SG_TIPO_SOLICITUD_VIAJE ts on s.SG_TIPO_SOLICITUD_VIAJE = ts.ID       "
                    + " inner join SG_TIPO_ESPECIFICO te on ts.SG_TIPO_ESPECIFICO = te.ID       "
                    + " left join SG_VIAJERO_SI_MOVIMIENTO vjm on vjm.SG_VIAJERO = v.ID AND vjm.ELIMINADO = 'False' "
                    + " left join SI_MOVIMIENTO m on vjm.SI_MOVIMIENTO = m.ID and m.SI_OPERACION = 6       "
                    + " WHERE s.eliminado = 'False'   "
                    + "  AND te.ID = " + Constantes.SOLICITUDES_TERRESTRE
                    + "  AND v.sg_viaje is not null  "
                    + "  AND v.eliminado = '" + Constantes.ELIMINADO + "'"
                    + "  and v.SG_VIAJERO is not null "
                    + "  AND s.OFICINA_ORIGEN in (" + Constantes.ID_OFICINA_TORRE_MARTEL + ", " + Constantes.ID_OFICINA_SAN_FERNANDO + ") "
                    + "  AND s.OFICINA_DESTINO in (" + Constantes.ID_OFICINA_TORRE_MARTEL + ", " + Constantes.ID_OFICINA_SAN_FERNANDO + ") "
                    + "  AND s.SG_RUTA_TERRESTRE in (" + Constantes.RUTA_MTY_SF + ", " + Constantes.RUTA_SF_MTY + ") "
                    + esOficinaReynosa
                    + estatusDet
                    + fechasDetSalida
                    + busquedaDet
                    + solicitudIDval
            );
//            }
            query.append(")  as solTerrestres order by codigo, fecha_salida, hora_salida ");

            List<Object[]> listInv = em.createNativeQuery(query.toString()).getResultList();
            ViajeroVO v;
            SolicitudViajeVO sol = new SolicitudViajeVO();
            int solID = 0;
            if (listInv != null && !listInv.isEmpty()) {
                for (Object[] objects : listInv) {
                    v = new ViajeroVO();
                    v.setId((Integer) objects[0]);
                    if (objects[1] != null) {
                        v.setIdUsuario((String) objects[1]);
                        v.setUsuario((String) objects[13]);
                        v.setInvitado("null");
                        v.setIdInvitado(0);
                        v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
                    } else {
                        v.setIdInvitado((Integer) objects[2]);
                        v.setInvitado((String) objects[14]);
                        v.setUsuario("null");
                        v.setIdUsuario("null");
                        v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
                    }
                    v.setCodigoSolicitudViaje((String) objects[3]);
                    v.setFechaSalida((Date) objects[4]);
                    v.setHoraSalida((Date) objects[5]);
                    v.setFechaRegreso((Date) objects[6]);
                    v.setHoraRegreso((Date) objects[7]);
                    v.setCorreo((String) objects[9]);
                    v.setEstancia((Boolean) objects[10]);
                    v.setRedondo((Boolean) objects[11]);
                    v.setTelefono((String) objects[12]);
                    v.setViajeroQuedado((Integer) objects[18] != null ? (Integer) objects[18] : Constantes.PRIMERA_VEZ_VIAJE);
                    v.setDestino((String) objects[15]);
                    v.setIdSolicitudViaje((Integer) objects[16] != null ? (Integer) objects[16] : 0);

                    if (Constantes.QUEDADO_OFICINA_DESTINO == v.getViajeroQuedado()) {
                        v.setIdOrigen((Integer) objects[8] != null ? (Integer) objects[8] : 0);
                        v.setIdDestino((Integer) objects[19]);
                    } else if (Constantes.VIAJERO_ESCALA == v.getViajeroQuedado()) {
                        v.setIdOrigen((Integer) objects[19]);
                        v.setIdDestino((Integer) objects[21]);
                    } else {
                        v.setIdOrigen((Integer) objects[19]);
                        v.setIdDestino((Integer) objects[8] != null ? (Integer) objects[8] : 0);
                    }

                    if (solID == 0) {
                        solID = (Integer) objects[16];
                        sol = new SolicitudViajeVO();
                        sol.setIdSolicitud(solID);
                        sol.setCodigo((String) objects[3]);
                        sol.setDestino((String) objects[15]);
                        sol.setIdRutaTerrestre((Integer) objects[17] != null ? (Integer) objects[17] : 0);
                        sol.setFechaSalida((Date) objects[4]);
                        sol.setHoraSalida((Date) objects[5]);
                        sol.setFechaRegreso((Date) objects[6]);
                        sol.setHoraRegreso((Date) objects[7]);
                        sol.setIdOficinaOrigen((Integer) objects[19]);
                        sol.setIdOficinaDestino((Integer) objects[8] != null ? (Integer) objects[8] : 0);
                        sol.setIdSiCiudadDestino((Integer) objects[21] != null ? (Integer) objects[21] : 0);
                        sol.setRedondo(((Boolean) objects[20]));
                        sol.setSolicitudViajeDeRetorno(v.getViajeroQuedado());
                        sol.setViajeros(new ArrayList<ViajeroVO>());
                        sol.getViajeros().add(v);
                    } else if (solID == (Integer) objects[16]) {
                        sol.getViajeros().add(v);
                    } else if (solID != (Integer) objects[16]) {
                        lv.add(sol);
                        solID = (Integer) objects[16];
                        sol = new SolicitudViajeVO();
                        sol.setIdSolicitud(solID);
                        sol.setCodigo((String) objects[3]);
                        sol.setDestino((String) objects[15]);
                        sol.setIdRutaTerrestre((Integer) objects[17] != null ? (Integer) objects[17] : 0);
                        sol.setFechaSalida((Date) objects[4]);
                        sol.setHoraSalida((Date) objects[5]);
                        sol.setFechaRegreso((Date) objects[6]);
                        sol.setHoraRegreso((Date) objects[7]);
                        sol.setIdOficinaOrigen((Integer) objects[19]);
                        sol.setIdOficinaDestino((Integer) objects[8] != null ? (Integer) objects[8] : 0);
                        sol.setIdSiCiudadDestino((Integer) objects[21] != null ? (Integer) objects[21] : 0);
                        sol.setRedondo(((Boolean) objects[20]));
                        sol.setSolicitudViajeDeRetorno(v.getViajeroQuedado());
                        sol.setViajeros(new ArrayList<ViajeroVO>());
                        sol.getViajeros().add(v);
                    }
                }
            }
            if (listInv != null && !listInv.isEmpty()) {
                lv.add(sol);
            }
        } catch (Exception e) {
            lv = new ArrayList<SolicitudViajeVO>();
            UtilLog4j.log.fatal(e);
        }
        return lv;
    }

    
    public List<ViajeroVO> traerSolicitudesAereas() {
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select v.id, "
                    + "v.USUARIO, u.NOMBRE, v.SG_INVITADO, i.NOMBRE, "//Datos Viajero
                    + "u.EMAIL, i.EMAIL, v.REDONDO, "
                    + "s.id, s.CODIGO, " //datos de la Solicitud
                    + "s.FECHA_SALIDA, s.HORA_SALIDA, s.FECHA_REGRESO, s.HORA_REGRESO, "
                    + "s.ESTATUS, s.SG_TIPO_SOLICITUD_VIAJE, s.SG_TIPO_ESPECIFICO, "
                    + "s.OFICINA_ORIGEN, s.REDONDO, s.OBSERVACION, "
                    + "it.ID,it.SI_CIUDAD_ORIGEN,it.SI_CIUDAD_DESTINO, " //itinerario
                    + "it.IDA, it.NOTIFICADO, "
                    + "v.SG_VIAJE, v.ESTANCIA, v.SG_SOLICITUD_ESTANCIA, "
                    + "co.NOMBRE, cd.NOMBRE "
                    + "from SG_VIAJERO v "
                    + "inner join SG_SOLICITUD_VIAJE s on s.id=v.SG_SOLICITUD_VIAJE "
                    + "left join USUARIO u on u.ID=v.USUARIO "
                    + "left join SG_INVITADO i on i.ID=v.SG_INVITADO "
                    + "inner join SG_ITINERARIO it on it.SG_SOLICITUD_VIAJE = s.ID "
                    + "inner join SI_CIUDAD co on co.ID=it.SI_CIUDAD_ORIGEN "
                    + "inner join SI_CIUDAD cd on cd.ID=it.SI_CIUDAD_DESTINO "
                    + "where s.ESTATUS = ? "
                    + "and s.SG_TIPO_ESPECIFICO = ? "
                    + "and s.ELIMINADO = ? "
                    + "and it.IDA = ? "
                    + " order by s.FECHA_SALIDA, s.HORA_SALIDA, s.codigo");

            List<Object[]> listObj = em.createNativeQuery(sb.toString())
                    .setParameter(1, Constantes.ESTATUS_PARA_HACER_VIAJE)
                    .setParameter(2, Constantes.SOLICITUDES_AEREA)
                    .setParameter(3, Constantes.NO_ELIMINADO)
                    .setParameter(4, Constantes.BOOLEAN_TRUE)
                    .getResultList();

            if (listObj != null && !listObj.isEmpty()) {
                int solID = 0;
                ViajeroVO v;
                SolicitudViajeVO sol = null;
                for (Object[] objects : listObj) {
                    v = new ViajeroVO();
                    v.setId((Integer) objects[0]);
                    if (objects[1] != null) {
                        v.setIdUsuario((String) objects[1]);
                        v.setUsuario((String) objects[2]);
                        v.setCorreo((String) objects[5]);
                        v.setInvitado("null");
                        v.setIdInvitado(0);
                        v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
                        v.setEmpleado(Constantes.TRUE);
                    } else {
                        v.setIdInvitado((Integer) objects[3]);
                        v.setInvitado((String) objects[4]);
                        v.setUsuario("null");
                        v.setIdUsuario("null");
                        v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
                        v.setEmpleado(Constantes.FALSE);
                    }
                    v.setCodigoSolicitudViaje((String) objects[9]);
                    v.setFechaSalida((Date) objects[10]);
                    v.setHoraSalida((Date) objects[11]);
                    v.setFechaRegreso((Date) objects[12]);
                    v.setHoraRegreso((Date) objects[13]);
                    v.setEstancia((Boolean) objects[26]);
                    v.setRedondo((Boolean) objects[18]);
                    v.setIdOrigen((Integer) objects[21]);
                    v.setOrigen((String) objects[28]);
                    v.setIdDestino((Integer) objects[22]);
                    v.setDestino((String) objects[29]);
                    v.setIdSolicitudViaje((Integer) objects[8] != null ? (Integer) objects[0] : 0);

//                    if (solID == 0) {
//                        solID = (Integer) objects[8];
//                        sol = new SolicitudViajeVO();
//                        sol.setIdSolicitud(solID);
//                        sol.setCodigo((String) objects[9]);
//                        sol.setDestino((String) objects[29]);
//                        sol.setFechaSalida((Date) objects[10]);
//                        sol.setHoraSalida((Date) objects[11]);
//                        sol.setFechaRegreso((Date) objects[12]);
//                        sol.setHoraRegreso((Date) objects[13]);
//                        sol.setIdOficinaOrigen(Constantes.ID_OFICINA_TORRE_MARTEL);
//                        sol.setIdSiCiudadDestino((Integer) objects[22] != null ? (Integer) objects[22] : 0);
//                        sol.setRedondo(((Boolean) objects[18]));
//                        sol.setViajeros(new ArrayList<ViajeroVO>());
//                        sol.getViajeros().add(v);
//                    } else if (solID == (Integer) objects[8]) {
//                        sol.getViajeros().add(v);
//                    } else if (solID != (Integer) objects[8]) {
//                        lv.add(sol);
//                        solID = (Integer) objects[8];
//                        sol = new SolicitudViajeVO();
//                        sol.setIdSolicitud(solID);
//                        sol.setCodigo((String) objects[9]);
//                        sol.setDestino((String) objects[29]);
//                        sol.setFechaSalida((Date) objects[10]);
//                        sol.setHoraSalida((Date) objects[11]);
//                        sol.setFechaRegreso((Date) objects[12]);
//                        sol.setHoraRegreso((Date) objects[13]);
//                        sol.setIdOficinaOrigen(Constantes.ID_OFICINA_TORRE_MARTEL);
//                        sol.setIdSiCiudadDestino((Integer) objects[22] != null ? (Integer) objects[22] : 0);
//                        sol.setRedondo(((Boolean) objects[18]));
//                        sol.setViajeros(new ArrayList<ViajeroVO>());
//                        sol.getViajeros().add(v);
//                    }
                    lv.add(v);
                }

            }

        } catch (Exception e) {
            UtilLog4j.log.error(this, e);
        }

        return lv;
    }

    
    public List<SolicitudViajeVO> traerSolicitudesTerrestreByEstatus(int status, int status2, String idUsuario, String valFecha) {
        List<SolicitudViajeVO> lv = new ArrayList<SolicitudViajeVO>();
        try {
            String usuario = "";
            String est = "";
            if (idUsuario != null && !idUsuario.isEmpty()) {
                usuario = " AND ea.USUARIO = '" + idUsuario + "'";
            }
            //                                         0                   1                  2                  3            4                  5          6               7
            StringBuilder q = new StringBuilder();
            q.append(" SELECT distinct v.ID as viajeroID, u.id as usuarioID,  i.id as invitadoID,  s.codigo, s.fecha_salida,  s.hora_salida, s.fecha_regreso, s.hora_regreso, "
                    //          8                         9         10           11         12              13                   14	
                    + "s.oficina_destino as destinoOf, u.email, v.estancia, v.redondo ,u.telefono , u.NOMBRE as uNombre, i.NOMBRE as iNombre"
                    //                                  15                                            16              17             18             19             20                   21                     
                    + ", case when odes.id > 0 then odes.nombre else c.nombre end as desNombre, s.id as solID, r.id as rutaID, r.SG_OFICINA, s.redondo as r2, c.id as destinoCD, o.nombre as orgNombre,"
                    //22    23              24                        25                      26               27         28               29                30          31      32        
                    + "e.id, te.id, s.gerencia_responsable, v.SG_SOLICITUD_ESTANCIA, g.NOMBRE as gerenciaName, e.nombre, s.CONCHOFER, s.fecha_modifico, s.hora_modifico, svi.id, ug.nombre,"
                    //      33         34          35      36
                    + " mot.nombre, mr.id,s.observacion,  te.nombre"
                    + "  From  SG_VIAJERO v"
                    + "      inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID AND s.eliminado = 'False'"
                    + "      inner join SG_RUTA_TERRESTRE r on r.id = s.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False'"
                    + "      left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = r.id and drt.ELIMINADO = 'False'"
                    + "      left join SG_OFICINA odes on odes.ID = drt.SG_OFICINA and odes.ELIMINADO = 'False'"
                    + "      left join SG_DETALLE_RUTA_CIUDAD drc on drc.SG_RUTA_TERRESTRE = r.id and drc.ELIMINADO = 'False'"
                    + "      left join SI_CIUDAD c on c.id = drc.SI_CIUDAD and c.ELIMINADO = 'False'"
                    + "      inner join ESTATUS e on s.ESTATUS = e.ID"
                    + "      left join SG_OFICINA o on s.OFICINA_ORIGEN = o.ID"
                    + "      inner join SG_DIRECCION d on o.SG_DIRECCION = d.ID"
                    + "      left join USUARIO u on v.USUARIO = u.ID"
                    + "      left join SG_INVITADO i on v.SG_INVITADO = i.ID"
                    + "      inner join SG_TIPO_SOLICITUD_VIAJE ts on s.SG_TIPO_SOLICITUD_VIAJE = ts.ID"
                    + "      inner join SG_TIPO_ESPECIFICO te on ts.SG_TIPO_ESPECIFICO = te.ID"
                    + "      left join SG_VIAJERO_SI_MOVIMIENTO vm on vm.SG_VIAJERO  = v.ID  AND vm.eliminado = 'False'"
                    + "      inner join GERENCIA g on g.ID = s.GERENCIA_RESPONSABLE and g.ELIMINADO='False'"
                    + "      left join SG_SOL_VIAJE_INCUM svi on svi.SG_SOLICITUD_VIAJE = s.ID "
                    + "      INNER join USUARIO ug on ug.ID = s.GENERO and ug.ELIMINADO ='False'"
                    + "      INNER JOIN SG_MOTIVO mot on mot.ID = s.SG_MOTIVO and mot.ELIMINADO='False'"
                    + "      LEFT JOIN SG_MOTIVO_RETRASO MR ON MR.ID = S.SG_MOTIVO_RETRASO AND MR.ELIMINADO='False'");
            if (status > 0 && status2 > 0) {
                q.append("      left join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID AND ea.realizado = 'False' AND ea.historial = 'False' ");
                est = "AND s.estatus > " + Constantes.ESTATUS_PENDIENTE + " and s.estatus < " + Constantes.ESTATUS_PARA_HACER_VIAJE;
            } else if (status > 0 && status2 <= 0) {
                if (status != Constantes.ESTATUS_SOLICITUD_VIAJE_CANCELADO){
                    q.append("      inner join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID AND ea.realizado = 'False' AND ea.historial = 'False' ");
                }                
                est = " AND s.estatus = " + status;
            } else if (status <= 0 && status2 <= 0) {
                q.append("      left join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID AND ea.realizado = 'False' AND ea.historial = 'False' ");
                est = "AND s.estatus > " + Constantes.ESTATUS_PENDIENTE;
            }
            q.append("  WHERE s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'"
                    + "  AND te.ID = ").append(Constantes.SOLICITUDES_TERRESTRE)
                    .append("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'").append(est);

            q.append(usuario)
                    .append(valFecha)
                    .append("order by s.codigo, s.fecha_salida");

            List<Object[]> listSV = em.createNativeQuery(q.toString()).getResultList();
            castListSV(listSV, lv);
        } catch (Exception e) {
            UtilLog4j.log.error(this, e);
        }
        return lv;
    }

    
    public List<SolicitudViajeVO> traerSolicitudesAereasByEstatus(int estatus, String usuario, int siRol) {
        List<SolicitudViajeVO> lv = new ArrayList<SolicitudViajeVO>();
        try {
            String validaUsuario = "";
            if (siRol != Constantes.ROL_ADMIN_VIAJES_AEREOS) {
                validaUsuario = " and  ea.USUARIO = '" + usuario + "'";
            }
            //                               0                   1                      2                3                       4
            String q = "SELECT distinct v.ID as viajeroID, u.id as usuarioID, u.NOMBRE as uNombre,  i.id as invitadoID, i.NOMBRE as iNombre,"
                    //     5         6
                    + " u.email, u.telefono,"
                    //     7            8                 9             10              11                 12             13
                    + " s.codigo, s.fecha_salida,  s.hora_salida, s.fecha_regreso, s.hora_regreso,s.redondo as redsol,s.id as solID,"
                    //      14                15
                    + " v.estancia, v.redondo as redVia,"
                    //           16                 17
                    + " c.nombre as oriNombre, c.id as origenCD,"
                    //             18                  19   
                    + " cd.NOMBRE as destinoCD,cd.ID  as destinoCD,"
                    //         20                      21
                    + " ii.id as itinerarioIda, ir.id as itinerarioVuelta,"
                    //           22           23    24     25             26                        27
                    + " o.nombre as oficina, o.ID, e.id, te.id, s.gerencia_responsable, v.SG_SOLICITUD_ESTANCIA,"
                    //    28      29           30           31               32            33        34          35
                    + " g.NOMBRE, tsv.NOMBRE, tsv.ID, s.fecha_modifico, s.hora_modifico, ug.nombre, mot.nombre, s.observacion "
                    + " From  SG_VIAJERO v"
                    + " inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID AND s.eliminado = ?"//1P
                    + " INNER JOIN SG_ITINERARIO ii on ii.SG_SOLICITUD_VIAJE=s.ID and ii.IDA='True' and ii.ELIMINADO = ?"//2p
                    + " LEFT JOIN SG_ITINERARIO ir on ir.SG_SOLICITUD_VIAJE=s.ID and ir.IDA='False' and ir.ELIMINADO = ?"//3p
                    + " left join SI_CIUDAD c on c.id = ii.SI_CIUDAD_ORIGEN and c.ELIMINADO = ?"//4p
                    + " left join SI_CIUDAD cd on cd.id = iI.SI_CIUDAD_DESTINO and cd.ELIMINADO = ?"//5p
                    + " inner join ESTATUS e on s.ESTATUS = e.ID"
                    + " left join SG_OFICINA o on s.OFICINA_ORIGEN = o.ID"
                    + " inner join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID AND ea.realizado = ? AND ea.historial = ?"//6p y 7p
                    + " left join USUARIO u on v.USUARIO = u.ID"
                    + " left join SG_INVITADO i on v.SG_INVITADO = i.ID"
                    + " inner join SG_TIPO_ESPECIFICO te on S.SG_TIPO_ESPECIFICO = te.ID"
                    + " left join SG_VIAJERO_SI_MOVIMIENTO vm on vm.SG_VIAJERO  = v.ID  AND vm.eliminado = ?"//8p
                    + " INNER JOIN GERENCIA g on g.id = s.GERENCIA_RESPONSABLE and g.ELIMINADO = ?"//9p
                    + " inner join SG_TIPO_SOLICITUD_VIAJE tsv on tsv.ID = s.SG_TIPO_SOLICITUD_VIAJE and tsv.ELIMINADO = ?"//10p
                    + " INNER join USUARIO ug on ug.ID = s.GENERO and ug.ELIMINADO = ?"//11
                    + " INNER JOIN SG_MOTIVO mot on mot.ID = s.SG_MOTIVO and mot.ELIMINADO= ?"//12
                    + " WHERE s.eliminado = ?"//13
                    + " AND te.ID = ?"//14p
                    + " AND v.eliminado = ?"//15p
                    + " AND s.ESTATUS  = ?"//16p
                    + validaUsuario
                    + " AND s.fecha_salida >= CAST('NOW' AS DATE)"
                    + " order by s.id, s.fecha_salida";

            List<Object[]> listSV = em.createNativeQuery(q)
                    .setParameter(1, Constantes.BOOLEAN_FALSE)
                    .setParameter(2, Constantes.BOOLEAN_FALSE)
                    .setParameter(3, Constantes.BOOLEAN_FALSE)
                    .setParameter(4, Constantes.BOOLEAN_FALSE)
                    .setParameter(5, Constantes.BOOLEAN_FALSE)
                    .setParameter(6, Constantes.BOOLEAN_FALSE)
                    .setParameter(7, Constantes.BOOLEAN_FALSE)
                    .setParameter(8, Constantes.BOOLEAN_FALSE)
                    .setParameter(9, Constantes.BOOLEAN_FALSE)
                    .setParameter(10, Constantes.BOOLEAN_FALSE)
                    .setParameter(11, Constantes.BOOLEAN_FALSE)
                    .setParameter(12, Constantes.BOOLEAN_FALSE)
                    .setParameter(13, Constantes.BOOLEAN_FALSE)
                    .setParameter(14, Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA)
                    .setParameter(15, Constantes.BOOLEAN_FALSE)
                    .setParameter(16, estatus)
                    .getResultList();
            ViajeroVO v;
            SolicitudViajeVO sol = new SolicitudViajeVO();
            int solID = 0;
            if (listSV != null && !listSV.isEmpty()) {
                for (Object[] objects : listSV) {
                    v = new ViajeroVO();
                    v.setId((Integer) objects[0]);
                    if (objects[1] != null) {
                        v.setIdUsuario((String) objects[1]);
                        v.setUsuario((String) objects[2]);
                        v.setInvitado("null");
                        v.setIdInvitado(0);
                        v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
                        v.setEsEmpleado(Constantes.TRUE);
                    } else {
                        v.setIdInvitado((Integer) objects[3]);
                        v.setInvitado((String) objects[4]);
                        v.setUsuario("null");
                        v.setIdUsuario("null");
                        v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
                        v.setEsEmpleado(Constantes.FALSE);
                    }
                    v.setCodigoSolicitudViaje((String) objects[7]);
                    v.setFechaSalida((Date) objects[8]);
                    v.setHoraSalida((Date) objects[9]);
                    v.setFechaRegreso((Date) objects[10]);
                    v.setHoraRegreso((Date) objects[11]);
                    v.setCorreo((String) objects[5]);
                    v.setEstancia((Boolean) objects[14]);
                    v.setEstanciaB(v.isEstancia());
                    v.setRedondo((Boolean) objects[15]);
                    v.setTelefono((String) objects[6]);
                    v.setDestino((String) objects[18]);
                    v.setIdSolicitudViaje((Integer) objects[13] != null ? (Integer) objects[13] : 0);
                    v.setIdOrigen((Integer) objects[17] != null ? (Integer) objects[17] : 0);
                    v.setIdDestino((Integer) objects[19] != null ? (Integer) objects[19] : 0);
                    v.setSgSolicitudEstancia((Integer) objects[27] != null ? (Integer) objects[27] : 0);

                    if (solID == 0) {
                        solID = (Integer) objects[13];
                        sol = new SolicitudViajeVO();
                        sol.setIdSolicitud(solID);
                        sol.setCodigo((String) objects[7]);
                        sol.setDestino((String) objects[18]);
                        sol.setIdSiCiudadDestino((Integer) objects[19] != null ? (Integer) objects[19] : 0);
                        sol.setIdSiCiudadOrigen((Integer) objects[17] != null ? (Integer) objects[17] : 0);
                        sol.setOrigen((String) objects[16]);
                        sol.setFechaSalida((Date) objects[8]);
                        sol.setHoraSalida((Date) objects[9]);
                        sol.setFechaRegreso((Date) objects[10]);
                        sol.setHoraRegreso((Date) objects[11]);
                        sol.setIdOficinaOrigen((Integer) objects[17]);
                        sol.setRedondo(((Boolean) objects[12]));
                        sol.setViajeros(new ArrayList<ViajeroVO>());
                        sol.getViajeros().add(v);
                        sol.setSelect(Constantes.FALSE);
                        sol.setIdEstatus((Integer) objects[24] != null ? (Integer) objects[24] : 0);
                        sol.setIdSgTipoEspecifico((Integer) objects[25] != null ? (Integer) objects[25] : 0);
                        sol.setIdItinerarioIda((Integer) objects[20] != null ? (Integer) objects[20] : 0);
                        sol.setIdItinerarioVuelta((Integer) objects[21] != null ? (Integer) objects[21] : 0);
                        sol.setIdGerencia((Integer) objects[26] != null ? (Integer) objects[26] : 0);
                        sol.setGerencia((String) objects[28]);
                        sol.setTipoSolicitud((String) objects[29]);
                        sol.setIdSgTipoSolicitudViaje((Integer) objects[30] != null ? (Integer) objects[30] : 0);
                        sol.setFechaModifico((Date) objects[31]);
                        sol.setHoraModifico((Date) objects[32]);
                        sol.setGenero(objects[33] != null ? (String) objects[33] : "");
                        sol.setMotivo(objects[34] != null ? (String) objects[34] : "");
                        sol.setMotivoRetrasoVo(null);
                        sol.setObservacion(objects[35] != null ? (String) objects[35] : "Sin Observación");
                    } else if (solID == (Integer) objects[13]) {
                        sol.getViajeros().add(v);
                    } else if (solID != (Integer) objects[13]) {
                        lv.add(sol);
                        solID = (Integer) objects[13];
                        sol = new SolicitudViajeVO();
                        sol.setIdSolicitud(solID);
                        sol.setCodigo((String) objects[7]);
                        sol.setDestino((String) objects[18]);
                        sol.setIdSiCiudadDestino((Integer) objects[19] != null ? (Integer) objects[19] : 0);
                        sol.setIdSiCiudadOrigen((Integer) objects[17] != null ? (Integer) objects[17] : 0);
                        sol.setOrigen((String) objects[16]);
                        sol.setFechaSalida((Date) objects[8]);
                        sol.setHoraSalida((Date) objects[9]);
                        sol.setFechaRegreso((Date) objects[10]);
                        sol.setHoraRegreso((Date) objects[11]);
                        sol.setIdOficinaOrigen((Integer) objects[17]);
                        sol.setRedondo(((Boolean) objects[12]));
                        sol.setViajeros(new ArrayList<ViajeroVO>());
                        sol.getViajeros().add(v);
                        sol.setSelect(Constantes.FALSE);
                        sol.setIdEstatus((Integer) objects[24] != null ? (Integer) objects[24] : 0);
                        sol.setIdSgTipoEspecifico((Integer) objects[25] != null ? (Integer) objects[25] : 0);
                        sol.setIdItinerarioIda((Integer) objects[20] != null ? (Integer) objects[20] : 0);
                        sol.setIdItinerarioVuelta((Integer) objects[21] != null ? (Integer) objects[21] : 0);
                        sol.setIdGerencia((Integer) objects[26] != null ? (Integer) objects[26] : 0);
                        sol.setGerencia((String) objects[28]);
                        sol.setTipoSolicitud((String) objects[29]);
                        sol.setIdSgTipoSolicitudViaje((Integer) objects[30] != null ? (Integer) objects[30] : 0);
                        sol.setFechaModifico((Date) objects[31]);
                        sol.setHoraModifico((Date) objects[32]);
                        sol.setGenero(objects[33] != null ? (String) objects[33] : "");
                        sol.setMotivo(objects[34] != null ? (String) objects[34] : "");
                        sol.setMotivoRetrasoVo(null);
                        sol.setObservacion(objects[35] != null ? (String) objects[35] : "sin Observación");
                    }
                }
            }
            if (listSV != null && !listSV.isEmpty()) {
                lv.add(sol);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e);
        }
        return lv;
    }

    public List<SolicitudViajeVO> castListSV(List<Object[]> listSV, List<SolicitudViajeVO> lv) {
        ViajeroVO v;
        SolicitudViajeVO sol = new SolicitudViajeVO();
        int solID = 0;
        if (listSV != null && !listSV.isEmpty()) {
            for (Object[] objects : listSV) {
                v = new ViajeroVO();
                v.setId((Integer) objects[0]);
                if (objects[1] != null) {
                    v.setIdUsuario((String) objects[1]);
                    v.setUsuario((String) objects[13]);
                    v.setInvitado("null");
                    v.setIdInvitado(0);
                    v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
                    v.setEmpleado(Constantes.TRUE);
                } else {
                    v.setIdInvitado((Integer) objects[2]);
                    v.setInvitado((String) objects[14]);
                    v.setUsuario("null");
                    v.setIdUsuario("null");
                    v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
                    v.setEmpleado(Constantes.FALSE);
                }
                v.setCodigoSolicitudViaje((String) objects[3]);
                v.setFechaSalida((Date) objects[4]);
                v.setHoraSalida((Date) objects[5]);
                v.setFechaRegreso((Date) objects[6]);
                v.setHoraRegreso((Date) objects[7]);
                v.setCorreo((String) objects[9]);
                v.setEstancia((Boolean) objects[10]);
                v.setEstanciaB(v.isEstancia());
                v.setRedondo((Boolean) objects[11]);
                v.setTelefono((String) objects[12]);
                v.setDestino((String) objects[15]);
                v.setIdSolicitudViaje((Integer) objects[16] != null ? (Integer) objects[16] : 0);
                v.setIdOrigen((Integer) objects[18]);
                v.setIdDestino((Integer) objects[8] != null ? (Integer) objects[8] : 0);
                v.setSgSolicitudEstancia((Integer) objects[25] != null ? (Integer) objects[25] : 0);

                if (solID == 0) {
                    solID = (Integer) objects[16];
                    sol = new SolicitudViajeVO();
                    sol.setIdSolicitud(solID);
                    sol.setCodigo((String) objects[3]);
                    sol.setDestino((String) objects[15]);
                    sol.setOrigen((String) objects[21]);
                    sol.setIdRutaTerrestre((Integer) objects[17] != null ? (Integer) objects[17] : 0);
                    sol.setFechaSalida((Date) objects[4]);
                    sol.setHoraSalida((Date) objects[5]);
                    sol.setFechaRegreso((Date) objects[6]);
                    sol.setHoraRegreso((Date) objects[7]);
                    sol.setIdOficinaOrigen((Integer) objects[18]);
                    sol.setIdOficinaDestino((Integer) objects[8] != null ? (Integer) objects[8] : 0);
                    sol.setIdSiCiudadDestino((Integer) objects[20] != null ? (Integer) objects[20] : 0);
                    sol.setRedondo(((Boolean) objects[19]));
                    sol.setSolicitudViajeDeRetorno(v.getViajeroQuedado());
                    sol.setViajeros(new ArrayList<ViajeroVO>());
                    sol.getViajeros().add(v);
                    sol.setSelect(Constantes.FALSE);
                    sol.setIdEstatus((Integer) objects[22] != null ? (Integer) objects[22] : 0);
                    sol.setIdSgTipoEspecifico((Integer) objects[23] != null ? (Integer) objects[23] : 0);
                    sol.setIdGerencia((Integer) objects[24] != null ? (Integer) objects[24] : 0);
                    sol.setGerencia((String) objects[26]);
                    sol.setEstatus((String) objects[27]);
                    sol.setConChofer((Boolean) objects[28]);
                    sol.setFechaModifico((Date) objects[29]);
                    sol.setHoraModifico((Date) objects[30]);
                    sol.setIdSolImcumplimiento(objects[31] != null ? (Integer) objects[31] : 0);
                    sol.setGenero(objects[32] != null ? (String) objects[32] : "");
                    sol.setMotivo(objects[33] != null ? (String) objects[33] : "");
                    sol.setObservacion(objects[35] != null ? (String) objects[35] : "Sin Observación");
                    sol.setTipoEspecifico((String) objects[36]);
                    if (sol.getIdSgTipoEspecifico() == Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA) {
                        sol.setOrigen((String) objects[39]);
                        sol.setDestino((String) objects[41]);
                    }
                    if (objects[34] != null) {
                        int i = (Integer) objects[34];
                        sol.setMotivoRetrasoVo(sgMotivoRetrasoRemote.findById(i, Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE));
                    } else {
                        sol.setMotivoRetrasoVo(null);
                    }
                } else if (solID == (Integer) objects[16]) {
                    sol.getViajeros().add(v);
                } else if (solID != (Integer) objects[16]) {
                    lv.add(sol);
                    solID = (Integer) objects[16];
                    sol = new SolicitudViajeVO();
                    sol.setIdSolicitud(solID);
                    sol.setCodigo((String) objects[3]);
                    sol.setDestino((String) objects[15]);
                    sol.setOrigen((String) objects[21]);
                    sol.setIdRutaTerrestre((Integer) objects[17] != null ? (Integer) objects[17] : 0);
                    sol.setFechaSalida((Date) objects[4]);
                    sol.setHoraSalida((Date) objects[5]);
                    sol.setFechaRegreso((Date) objects[6]);
                    sol.setHoraRegreso((Date) objects[7]);
                    sol.setIdOficinaOrigen((Integer) objects[18]);
                    sol.setIdOficinaDestino((Integer) objects[8] != null ? (Integer) objects[8] : 0);
                    sol.setIdSiCiudadDestino((Integer) objects[20] != null ? (Integer) objects[20] : 0);
                    sol.setRedondo(((Boolean) objects[19]));
                    sol.setSolicitudViajeDeRetorno(v.getViajeroQuedado());
                    sol.setViajeros(new ArrayList<ViajeroVO>());
                    sol.getViajeros().add(v);
                    sol.setSelect(Constantes.FALSE);
                    sol.setIdEstatus((Integer) objects[22] != null ? (Integer) objects[22] : 0);
                    sol.setIdSgTipoEspecifico((Integer) objects[23] != null ? (Integer) objects[23] : 0);
                    sol.setIdGerencia((Integer) objects[24] != null ? (Integer) objects[24] : 0);
                    sol.setGerencia((String) objects[26]);
                    sol.setEstatus((String) objects[27]);
                    sol.setConChofer((Boolean) objects[28]);
                    sol.setFechaModifico((Date) objects[29]);
                    sol.setHoraModifico((Date) objects[30]);
                    sol.setIdSolImcumplimiento(objects[31] != null ? (Integer) objects[31] : 0);
                    sol.setGenero(objects[32] != null ? (String) objects[32] : "");
                    sol.setMotivo(objects[33] != null ? (String) objects[33] : "");
                    sol.setObservacion(objects[35] != null ? (String) objects[35] : "sin Observación");
                    sol.setTipoEspecifico(objects[36] != null ? (String) objects[36] : "");
                    if (objects[34] != null) {
                        int i = (Integer) objects[34];
                        sol.setMotivoRetrasoVo(sgMotivoRetrasoRemote.findById(i, Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_TERRESTRE));
                    } else {
                        sol.setMotivoRetrasoVo(null);
                    }

                }
            }
        }
        if (listSV != null && !listSV.isEmpty()) {
            lv.add(sol);
        }
        return lv;
    }

    
    public List<SolicitudViajeVO> traerListMisSolicitudes(String idUser, Date d1, Date d2, String filtro) {

        String sentencia = "";

        switch (filtro) {
            case "fs":
                sentencia = "    and solicitud.fecha_salida BETWEEN '" + Constantes.FMT_yyyy_MM_dd.format(d1)
                        + "' and '" + Constantes.FMT_yyyy_MM_dd.format(d2) + "'"
                        + "      and solicitud.estatus >  401 \n";
                break;
            case "fr":
                sentencia = "    and solicitud.fecha_regreso BETWEEN '" + Constantes.FMT_yyyy_MM_dd.format(d1)
                        + "' and '" + Constantes.FMT_yyyy_MM_dd.format(d2) + "'"
                        + "      and solicitud.estatus >  401 \n";
                break;
            case "act":
                //todo lo que su fecha de Salida sea mayor a hoy 
                sentencia = "    and solicitud.fecha_salida >= '" + Constantes.FMT_yyyy_MM_dd.format(new Date()) + "'\n"
                        + "      and solicitud.estatus >  401 \n";
                break;
            default:
                sentencia = "       and solicitud.estatus != 401   \n";
                break;
        }

        String sql = "SELECT solicitud.id as idSolicitud, solicitud.codigo as codSolicitud,\n" //0-1
                + " solicitud.genero,solicitud.fecha_genero,solicitud.hora_genero,\n" //2-4
                + " solicitud.fecha_salida as fsSolicitud, solicitud.hora_salida as hsSolicitud,\n" //5-6
                + " solicitud.fecha_regreso as frSolicitud, solicitud.hora_regreso as hrSolicitud,\n" //7-8
                + " es.id as idEstatusSolicitud, es.nombre as estatus_Solicitud,\n"//9-10
                + " rt.id, rt.nombre as rutaSolicitud,\n"//11-12
                + " solicitud.modifico as  modSolicitud, solicitud.fecha_modifico as fmSolicitud,\n"//13-14
                + " solicitud.hora_modifico as hmSolicitud, \n" //15
                + " viajero.id as idViajero, viajero.usuario as idusuario, u.nombre as usuario,\n" //16-18
                + " COALESCE(viaje.id,0) as idViaje, viaje.codigo as codViaje, viaje.fecha_salida as fsViaje, viaje.hora_salida as hsViaje,\n" //19-22
                + " viaje.fecha_regreso as frViaje, viaje.hora_regreso as hrViaje,\n" //23-24
                + " viaje.fecha_programada as fpViaje, viaje.hora_programada as hpViaje,\n" //26
                + " ev.id as idEstatusViaje, ev.nombre as estatusViaje, viajero.sg_viajero, viajero.eliminado \n" // 27-30
                + " ,viajero.estancia, se.codigo,se.id, solicitud.sg_tipo_especifico, solicitud.redondo \n " //31-33
                + " ,CASE WHEN solicitud.fecha_salida >= CURRENT_DATE and solicitud.estatus >401 THEN true ELSE false END \n"
                + "  FROM sg_solicitud_viaje solicitud\n"
                + "  INNER JOIN sg_viajero viajero on viajero.sg_solicitud_viaje = solicitud.id and solicitud.eliminado = ?\n"//1 false
                + "  INNER JOIN estatus es on es.id = solicitud.estatus\n"
                + "  INNER JOIN sg_ruta_terrestre rt on rt.id = solicitud.sg_ruta_terrestre \n"
                + "  INNER JOIN usuario u on u.id = viajero.usuario and u.eliminado = ? \n"//2 false
                + "  LEFT JOIN sg_viaje viaje on viaje.id = viajero.sg_viaje AND viaje.eliminado = ? \n"//3 false
                + "  LEFT JOIN estatus ev on ev.id = viaje.estatus\n"
                + "   LEFT JOIN sg_solicitud_estancia se on se.id = viajero.sg_solicitud_estancia"
                + "    where 1=1\n"
                + "    and  viajero.usuario = ? \n" //4 iduser
                + "    and viajero.eliminado = false \n"
                + sentencia // se debe de cambiar la sentencias segun sea el caso
                + " UNION \n"
                + "  SELECT solicitud.id as idSolicitud, solicitud.codigo as codSolicitud,\n" //0-1
                + "   solicitud.genero,solicitud.fecha_genero,solicitud.hora_genero,\n" //2-4
                + "   solicitud.fecha_salida as fsSolicitud, solicitud.hora_salida as hsSolicitud,\n" //5-6
                + "   solicitud.fecha_regreso as frSolicitud, solicitud.hora_regreso as hrSolicitud,\n" //7-8
                + "   es.id as idEstatusSolicitud, es.nombre as estatus_Solicitud,0, co.nombre ||' a '|| cd.nombre,\n" //9-12
                + "   solicitud.modifico as  modSolicitud, solicitud.fecha_modifico as fmSolicitud,\n" //13-14
                + "   solicitud.hora_modifico as hmSolicitud, \n" //15
                + "   viajero.id as idViajero, viajero.usuario as idusuario, u.nombre as usuario,\n" //16-18
                + "   COALESCE(viaje.id,0) as idViaje, viaje.codigo as codViaje, viaje.fecha_salida as fsViaje, viaje.hora_salida as hsViaje,\n" //19-22
                + "   viaje.fecha_regreso as frViaje, viaje.hora_regreso as hrViaje,\n" //23-24
                + "   viaje.fecha_programada as fpViaje, viaje.hora_programada as hpViaje,\n" //25-26
                + "   ev.id as idEstatusViaje, ev.nombre as estatusViaje, viajero.sg_viajero, viajero.eliminado \n" //27-30
                + " ,viajero.estancia, se.codigo, se.id, solicitud.sg_tipo_especifico, solicitud.redondo \n" //31-35
                + " ,CASE WHEN solicitud.fecha_salida >= CURRENT_DATE and solicitud.estatus >401 THEN true ELSE false END \n"
                + "     FROM sg_solicitud_viaje solicitud\n"
                + "     INNER JOIN sg_viajero viajero on viajero.sg_solicitud_viaje = solicitud.id and solicitud.eliminado = ? \n"//5 false
                + "     INNER JOIN estatus es on es.id = solicitud.estatus\n"
                + "     INNER JOIN usuario u on u.id = viajero.usuario and u.eliminado = ?\n" //6 false
                + "     INNER JOIN sg_itinerario ii on ii.sg_solicitud_viaje = solicitud.id and ii.ida = ? \n" //7 true
                + "INNER JOIN si_ciudad co on co.id = ii.si_ciudad_origen\n"
                + "INNER JOIN si_ciudad cd on cd.id = ii.si_ciudad_destino \n"
                + "     LEFT JOIN sg_viaje viaje on viaje.id = viajero.sg_viaje AND viaje.eliminado = ?\n"//8 false
                + "     LEFT JOIN estatus ev on ev.id = viaje.estatus\n"
                + "      LEFT JOIN sg_solicitud_estancia se on se.id = viajero.sg_solicitud_estancia"
                + "       where  1=1 \n"
                + "       and  viajero.usuario = ? \n" //9 iduser 
                + "       and viajero.eliminado = false \n"
                + sentencia //puede variar
                + "       and solicitud.sg_tipo_especifico = ? \n" //10 3
                + "       order by fsSolicitud";

        List<Object[]> listSV = em.createNativeQuery(sql)
                .setParameter(1, Constantes.BOOLEAN_FALSE)
                .setParameter(2, Constantes.BOOLEAN_FALSE)
                .setParameter(3, Constantes.BOOLEAN_FALSE)
                .setParameter(4, idUser)
                .setParameter(5, Constantes.BOOLEAN_FALSE)
                .setParameter(6, Constantes.BOOLEAN_FALSE)
                .setParameter(7, Constantes.TRUE)
                .setParameter(8, Constantes.BOOLEAN_FALSE)
                .setParameter(9, idUser)
                .setParameter(10, Constantes.SG_TIPO_ESPECIFICO_SOLICITUD_VIAJE_AEREA)
                .getResultList();

        ViajeroVO viajero;
        SolicitudViajeVO solicitud = null;
        ViajeVO viajeVO;
        List<SolicitudViajeVO> listSolicitudes = new ArrayList<>();
        String codVia = "";

        if (listSV != null && !listSV.isEmpty()) {
            for (Object[] ob : listSV) {
                viajero = new ViajeroVO();
                if (solicitud != null) {
                    int ids = (int) ob[0];
                    if (ids != solicitud.getIdSolicitud()) {

                        solicitud = new SolicitudViajeVO();
                        solicitud.setIdSolicitud((Integer) ob[0]);
                        solicitud.setCodigo((String) ob[1]);
                        solicitud.setGenero((String) ob[2]);
                        solicitud.setFechaGenero((Date) ob[3]);
                        solicitud.setHoraGenero((Date) ob[4]);
                        solicitud.setFechaSalida((Date) ob[5]);
                        solicitud.setHoraSalida((Date) ob[6]);
                        solicitud.setFechaRegreso((Date) ob[7]);//este puede se null
                        solicitud.setHoraRegreso((Date) ob[8]);
                        solicitud.setIdEstatus((int) ob[9]);
                        solicitud.setEstatus((String) ob[10]);
                        solicitud.setIdRutaTerrestre((int) ob[11]);
                        solicitud.setNombreRuta((String) ob[12]);
                        solicitud.setNombreModifico((String) ob[13]);
                        solicitud.setFechaModifico((Date) ob[14]);
                        solicitud.setHoraModifico((Date) ob[15]);
                        solicitud.setIdSgTipoEspecifico((int) ob[34]);
                        solicitud.setRedondo((boolean) ob[35]);
                        solicitud.setViajeros(new ArrayList<ViajeroVO>());
                        solicitud.setNombreRuta((String) ob[12]);
                        solicitud.setSelect((boolean) ob[36]);

                        if (solicitud.getIdRutaTerrestre() > 0) {
                            RutaTerrestreVo rt = sgRutaTerrestreRemote.traerRutaTerrestrePorID(solicitud.getIdRutaTerrestre());
                            solicitud.setRuta(rt);
                        }

                        viajero.setId((Integer) ob[16]);
                        viajero.setIdUsuario((String) ob[17]);
                        viajero.setUsuario((String) ob[18]);
                        viajero.setEstancia((boolean) ob[31]);
                        if (viajero.isEstancia()) {
                            viajero.setCodigoEstancia((String) ob[32]);
                            viajero.setSgSolicitudEstancia((Integer) ob[33]);
                            solicitud.setCodEstancia((String) ob[32]);
                            solicitud.setIdEstancia((Integer) ob[33]);
                        }
                        if (ob[19] != null) {
                            viajeVO = new ViajeVO();
                            viajeVO.setId((Integer) ob[19]);
                            if (viajeVO.getId() > 0) {
                                viajeVO.setCodigo((String) ob[20]);
                                viajeVO.setFechaSalida((Date) ob[21]);
                                viajeVO.setHoraSalida((Date) ob[22]);
                                viajeVO.setFechaRegreso((Date) ob[23]);
                                viajeVO.setHoraRegreso((Date) ob[24]);
                                viajeVO.setFechaProgramada((Date) ob[25]);
                                viajeVO.setHoraProgramada((Date) ob[26]);
                                viajeVO.setIdEstatus((int) ob[27]);
                                viajeVO.setEstatus((String) ob[28]);

                                codVia = (String) ob[20];
                                solicitud.setListViajes(codVia);
                            }
                            viajero.setViajeVO(viajeVO);
                        }
                        solicitud.getViajeros().add(viajero);
                        listSolicitudes.add(solicitud);
                    } else {

                        viajero.setId((Integer) ob[16]);
                        viajero.setIdUsuario((String) ob[17]);
                        viajero.setUsuario((String) ob[18]);
                        viajero.setEstancia((boolean) ob[31]);
                        if (viajero.isEstancia()) {
                            viajero.setCodigoEstancia((String) ob[32]);
                            viajero.setSgSolicitudEstancia((Integer) ob[33]);
                            solicitud.setCodEstancia((String) ob[32]);
                            solicitud.setIdEstancia((Integer) ob[33]);
                        }
                        if (ob[19] != null) {
                            viajeVO = new ViajeVO();
                            viajeVO.setId((Integer) ob[19]);
                            if (viajeVO.getId() > 0) {
                                viajeVO.setCodigo((String) ob[20]);
                                viajeVO.setFechaSalida((Date) ob[21]);
                                viajeVO.setHoraSalida((Date) ob[22]);
                                viajeVO.setFechaRegreso((Date) ob[23]);
                                viajeVO.setHoraRegreso((Date) ob[24]);
                                viajeVO.setFechaProgramada((Date) ob[25]);
                                viajeVO.setHoraProgramada((Date) ob[26]);
                                viajeVO.setIdEstatus((int) ob[27]);
                                viajeVO.setEstatus((String) ob[28]);

                                if (codVia.isEmpty()) {
                                    codVia = (String) ob[20];
                                    solicitud.setListViajes(codVia);
                                } else {
                                    codVia += ", " + (String) ob[20];
                                    solicitud.setListViajes(codVia);
                                }
                            }
                            viajero.setViajeVO(viajeVO);
                        }
                        solicitud.getViajeros().add(viajero);
                    }
                } else {
                    solicitud = new SolicitudViajeVO();

                    solicitud.setIdSolicitud((Integer) ob[0]);
                    solicitud.setCodigo((String) ob[1]);
                    solicitud.setGenero((String) ob[2]);
                    solicitud.setFechaGenero((Date) ob[3]);
                    solicitud.setHoraGenero((Date) ob[4]);
                    solicitud.setFechaSalida((Date) ob[5]);
                    solicitud.setHoraSalida((Date) ob[6]);
                    solicitud.setFechaRegreso((Date) ob[7]);//este puede se null
                    solicitud.setHoraRegreso((Date) ob[8]);
                    solicitud.setIdEstatus((int) ob[9]);
                    solicitud.setEstatus((String) ob[10]);
                    solicitud.setIdRutaTerrestre((int) ob[11]);
                    solicitud.setNombreModifico((String) ob[13]);
                    solicitud.setFechaModifico((Date) ob[14]);
                    solicitud.setHoraModifico((Date) ob[15]);
                    solicitud.setIdSgTipoEspecifico((int) ob[34]);
                    solicitud.setRedondo((boolean) ob[35]);
                    solicitud.setViajeros(new ArrayList<ViajeroVO>());
                    solicitud.setNombreRuta((String) ob[12]);
                    solicitud.setSelect((boolean) ob[36]);

                    if (solicitud.getIdRutaTerrestre() > 0) {
                        RutaTerrestreVo rt = sgRutaTerrestreRemote.traerRutaTerrestrePorID(solicitud.getIdRutaTerrestre());
                        solicitud.setRuta(rt);
                    }
                    viajero.setId((Integer) ob[16]);
                    viajero.setIdUsuario((String) ob[17]);
                    viajero.setUsuario((String) ob[18]);
                    viajero.setEstancia((boolean) ob[31]);
                    if (viajero.isEstancia()) {
                        viajero.setCodigoEstancia((String) ob[32]);
                        viajero.setSgSolicitudEstancia((Integer) ob[33]);
                        solicitud.setCodEstancia((String) ob[32]);
                        solicitud.setIdEstancia((Integer) ob[33]);
                    }
                    if (ob[19] != null) {
                        viajeVO = new ViajeVO();
                        viajeVO.setId((Integer) ob[19]);
                        if (viajeVO.getId() > 0) {
                            viajeVO.setCodigo((String) ob[20]);
                            viajeVO.setFechaSalida((Date) ob[21]);
                            viajeVO.setHoraSalida((Date) ob[22]);
                            viajeVO.setFechaRegreso((Date) ob[23]);
                            viajeVO.setHoraRegreso((Date) ob[24]);
                            viajeVO.setFechaProgramada((Date) ob[25]);
                            viajeVO.setHoraProgramada((Date) ob[26]);
                            viajeVO.setIdEstatus((int) ob[27]);
                            viajeVO.setEstatus((String) ob[28]);

                            if (codVia.isEmpty()) {
                                codVia = (String) ob[20];
                                solicitud.setListViajes(codVia);
                            } else {
                                codVia += ", " + (String) ob[20];
                                solicitud.setListViajes(codVia);
                            }
                        }
                        viajero.setViajeVO(viajeVO);
                    }
                    solicitud.getViajeros().add(viajero);
                    listSolicitudes.add(solicitud);
                }

            }

        }

        return listSolicitudes;
    }
    
    
    
    public String procesarSolicitudViaje(int idSolicitud, String idUsuario, int idEstatus, int idCampo) {
        UtilLog4j.log.fatal(this, "AUTSV.processRequest()");
        String motivo = "";
        try {
            SolicitudViajeVO solicitudViajeVO = buscarPorId(idSolicitud, Constantes.NO_ELIMINADO, Constantes.CERO);
            EstatusAprobacionSolicitudVO sgEstatusAprobacion = sgEstatusAprobacionRemote.buscarEstatusAprobacionPorIdSolicitudIdEstatus(idSolicitud, idEstatus);
            Usuario usuario = usuarioRemote.find(idUsuario);            
            //SgSolicitudViaje solViaje = sgSolicitudViajeRemote.find(sgEstatusAprobacion.getIdSolicitud());
            String action = "";
            
            switch (solicitudViajeVO.getIdEstatus()) {
                case 415: {
                    action = "Revisada";
                    motivo = "por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx.";
                }
                break;
                case 420: {
                    action = "Aprobada";
                    motivo = "por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx.";
                }
                break;
                case 435: {
                    action = "Autorizada";
                    motivo = "No se puede aprobar la solicitud debido a que ya fue previamente aprobada";
                }
                break;
                case 438: {
                    action = "Aprobada por centops";
                    motivo = "La solicitud no fue aprobada a tiempo por lo que fue transferida a CentOps.";
                }
                break;
                case 450: {
                    action = "para hacer Viaje";
                    motivo = "La solicitud ya no puede ser aprobada porque ya fue canalizada con el analista para realizar el viaje.";
                    break;
                }
            }
            String aps1 = "";

            if (sgEstatusAprobacion != null) {
                if (sgEstatusAprobacion.getIdUsuario().equals(usuario.getId()) && solicitudViajeVO.getIdEstatus() == sgEstatusAprobacion.getIdEstatus()) {
                    UtilLog4j.log.fatal(this, "idSgEstatusAprobacion encontrado: " + sgEstatusAprobacion.getId());
                    aps1 = vistoBuenoAprobarOrAutorizarSolicitudViaje(solicitudViajeVO.getIdSolicitud(), solicitudViajeVO.getIdEstatus(),
                            solicitudViajeVO.getFechaSalida(), solicitudViajeVO.getHoraSalida(),
                            solicitudViajeVO.getIdSgTipoSolicitudViaje(),
                            solicitudViajeVO.getCodigo(),
                            sgEstatusAprobacion.getId(), usuario.getId(), idCampo);
                    if (aps1 == null || aps1.isEmpty()) {
                        motivo = aprobar(sgEstatusAprobacion.getId(), usuario.getId(),
                                solicitudViajeVO.getCodigo(), action);                        
                    }

                } else {
                    //printMessage(motivo, request, response, "#DBA901");
                }
            } else {
                //printMessage(("No se encontró la Solicitud de Viaje con id =" + Integer.parseInt(request.getParameter("idSgSolicitudViaje"))), request, response, "red");
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return motivo;
    }
    
    private String vistoBuenoAprobarOrAutorizarSolicitudViaje(int idSolicitud, int idStatus, Date fechaSalida, Date horaSalida,
            int tipoSolicitud, String codigo, int idEstatusAprobacion, String idUsuario, int campo) {
        UtilLog4j.log.info(this, "AUTSV.vistoBuenoAprobarOrAutorizarSolicitudViaje() " + idSolicitud);

        String pass = "";
        UsuarioRolVo urv = siUsuarioRolRemote.traerRolPrincipal(idUsuario, Constantes.MODULO_SGYL, campo);
        boolean v = false;

        if (siManejoFechaLocal.validaFechaSalidaViaje(fechaSalida, horaSalida)) {
            if (tipoSolicitud == Constantes.SOLICITUDES_TERRESTRE) {
                //Valida que no sea despues de las 5 con fecha de salida mañana
                if (siManejoFechaLocal.dateIsTomorrow(fechaSalida)
                        && (siManejoFechaLocal.validaHoraMaximaAprobacion(urv.getIdRol(), Constantes.HORA_MAXIMA_APROBACION))) {
                    pass = "No es posible aprobar solicitudes de viajes después de las 5:00 pm sin justificar.";

                } else if (siManejoFechaLocal.dayIsToday(fechaSalida)) {
                    //valida qsi se desea arobar el mismo día
                    pass = "No es posible aprobar solicitudes de viajes el mismo día sin justificar.";
                } else if (siManejoFechaLocal.salidaProximoLunes(new Date(), fechaSalida, idStatus)) {
                    pass = "Debido a que la solicitud de viaje es para el próximo lunes, y en base a las políticas de viaje "
                            + "debe de ser justificada, porque la hora máxima para aprobar es el viernes a las 12:00 hrs.";
                } else {
                    pass = null;
                    //  aprobar(idEstatusAprobacion, idUsuario, codigo, action, request, response);
                }
            } else { // Solicitudes aereas
                if (idStatus != Constantes.ESTATUS_APROBAR) {
                    pass = null;
                    //  aprobar(idEstatusAprobacion, idUsuario, codigo, action, request, response);
                } else {
                    ItinerarioCompletoVo icv = sgItinerarioRemote.buscarItinerarioCompletoVoPorIdSolicitud(idSolicitud, true, true, "id");
                    if (icv.getEscalas().size() > 0) {
                        pass = null;
                        //  aprobar(idEstatusAprobacion, idUsuario, codigo, action, request, response);
                    } else {
                        pass = "No es posible aprobar una solicitud de viaje aérea sin itinerario";
                    }
                }
            }
        } else {
            pass = "Imposible aprobar una Solicitud de Viaje para una fecha y hora de salida pasada";

        }
        return pass;
    }
    
    private String aprobar(int idEstatusAprobacion, String idUsuario, String codigo, String accion) {
        String ret = "";
        boolean v = sgEstatusAprobacionRemote.aprobarSolicitud(idEstatusAprobacion, idUsuario);
        String color = "";
        if (v) {
            color = "blue";
            ret = "La solicitud de Viaje " + codigo + " ha sido " + accion + " satisfactoriamente";
            
        } else {
            color = "red";
            ret = "Ocurrió un error. Por favor contacta al equipo del SIA para averiguar que pasó al correo soportesia@ihsa.mx";
            //   UtilLog4j.log.fatal(this, e);
        }
        return ret;
    }
    

}
