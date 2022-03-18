/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.semaforo.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.excepciones.EmailNotFoundException;
import sia.excepciones.SIAException;
import sia.modelo.CoNoticia;
import sia.modelo.SgEstadoSemaforo;
import sia.modelo.SgHotel;
import sia.modelo.SgSemaforo;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgStaff;
import sia.modelo.SgViaje;
import sia.modelo.SgViajero;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.sgl.estancia.vo.HuespedVo;
import sia.modelo.sgl.estancia.vo.SgHuespedStaffVo;
import sia.modelo.sgl.semaforo.vo.EstadoSemaforoCambioVO;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.semaforo.vo.SgEstadoSemaforoVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.EstatusAprobacionVO;
import sia.modelo.sgl.vo.SgHuespedHotelVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.util.UtilLog4j;
import sia.constantes.Configurador;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.gr.impl.GrMapaImpl;
import sia.servicios.gr.impl.GrRutasZonasImpl;
import sia.servicios.sgl.huesped.impl.SiUsuarioTipoImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgHotelImpl;
import sia.servicios.sgl.impl.SgHuespedHotelImpl;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgEstadoSemaforoImpl extends AbstractFacade<SgEstadoSemaforo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgEstadoSemaforoImpl() {
	super(SgEstadoSemaforo.class);
    }
    
    @Inject
    DSLContext dslCtx;
    //
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;
    @Inject
    private SgSemaforoImpl sgSemaforoRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SiUsuarioTipoImpl siUsuarioTipoRemote;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreRemote;
    @Inject
    private CoNoticiaImpl coNoticiaRemote;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private SgHuespedStaffImpl sgHuespedStaffRemote;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;    
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private SgInvitadoImpl sgInvitadoRemote;
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;
    //
    @Inject
    private SgHotelImpl sgHotelRemote;
    @Inject
    private SgStaffImpl sgStaffRemote;
    //
    @Inject
    private GrMapaImpl grMapaRemote;
    @Inject
    private GrRutasZonasImpl grRutasZonasRemote;

    
    public List<SemaforoVo> traerEstadoRuta() {
	try {
	    List<Object[]> list;
	    clearQuery();
	    appendQuery(" select es.ID, rt.ID, se.ID, rt.NOMBRE, se.COLOR, o.NOMBRE as origen, es.FECHA_INICIO, ");
	    appendQuery(" es.HORA_INICIO,es.FECHA_FIN, es.HORA_FIN, es.HORA_MINIMA, es.HORA_MAXIMA, es.JUSTIFICACION, rt.sg_tipo_especifico, se.descripcion , o.id ");
	    appendQuery("  from SG_ESTADO_SEMAFORO es, SG_SEMAFORO se, SG_RUTA_TERRESTRE rt, SG_OFICINA o ");
	    appendQuery(" where es.SG_SEMAFORO = se.ID and es.SG_RUTA_TERRESTRE = rt.ID ");
	    appendQuery(" and es.ELIMINADO = 'False' and es.ACTUAL = 'True' and rt.SG_OFICINA = o.id");
	    appendQuery(" ORDER BY O.NOMBRE, RT.SG_TIPO_ESPECIFICO  ASC");
	    UtilLog4j.log.info(this, "Q: " + query.toString());
	    list = em.createNativeQuery(query.toString()).getResultList();
	    List<SemaforoVo> lv = new ArrayList<SemaforoVo>();
	    for (Object[] objects : list) {
		lv.add(castEstadoVO(objects));
	    }
	    UtilLog4j.log.info(this, "Lista ciudad: " + lv.size());
	    return lv;
	} catch (Exception e) {
	    e.getStackTrace();
	    UtilLog4j.log.fatal(this, "Exc: al recuperar los estados de semaforo: " + e.getMessage());
	    return null;
	}
    }

    
    public List<SemaforoVo> traerEstadoPorRuta(int idRuta) {
	try {
	    List<Object[]> list;
	    clearQuery();
	    appendQuery(" select es.ID, rt.ID, se.ID, rt.NOMBRE, se.COLOR, o.NOMBRE as origen, es.FECHA_INICIO, ");
	    appendQuery(" es.HORA_INICIO,es.FECHA_FIN, es.HORA_FIN, rt.HORA_MINIMARUTA, rt.HORA_MAXIMARUTA, es.JUSTIFICACION ,rt.sg_tipo_especifico , se.descripcion , o.id");
	    appendQuery("  from SG_ESTADO_SEMAFORO es, SG_SEMAFORO se, SG_RUTA_TERRESTRE rt, SG_OFICINA o");
	    appendQuery(" where es.SG_SEMAFORO = se.ID and es.SG_RUTA_TERRESTRE = rt.ID ");
	    appendQuery(" and es.SG_RUTA_TERRESTRE =  ").append(idRuta);
	    appendQuery(" and es.SG_SEMAFORO = se.ID and es.SG_RUTA_TERRESTRE  = rt.ID ");
	    appendQuery(" and es.ELIMINADO = 'False' and rt.SG_OFICINA = o.id ");
	    appendQuery(" ORDER BY O.NOMBRE, RT.SG_TIPO_ESPECIFICO  ASC");
	    list = em.createNativeQuery(query.toString()).getResultList();
	    List<SemaforoVo> lv = new ArrayList<SemaforoVo>();
	    for (Object[] objects : list) {
		lv.add(castEstadoVO(objects));
	    }
	    return lv;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "e:  " + e.getMessage());
	    return null;
	}
    }

    private SemaforoVo castEstadoVO(Object[] objects) {
	SemaforoVo semaforoVo = new SemaforoVo();
	try {
	    int id = (Integer) objects[2];
	    String estilo = "";
	    String col = (String) objects[4];
	    String varJus = (String) objects[12];
            semaforoVo.setIdEstadoSemaforo((Integer) objects[0]);
	    semaforoVo.setIdRuta((Integer) objects[1]);
	    semaforoVo.setIdSemaforo((Integer) objects[2]);
	    semaforoVo.setNombreRuta((String) objects[3]);
	    semaforoVo.setColor(col);
	    semaforoVo.setOrigen((String) objects[5]);
//            semaforoVo.setFechaInicio((Date) objects[6]);
//            semaforoVo.setHoraInicio((Date) objects[7]);
//            semaforoVo.setFechaFin((Date) objects[8]);
//            semaforoVo.setHoraFin((Date) objects[9]);
	    semaforoVo.setHoraMinimaRuta((Date) objects[10]);
	    semaforoVo.setHoraMaximaRuta((Date) objects[11]);
	    semaforoVo.setRutaTipoEspecifico((Integer) objects[13]);
	    semaforoVo.setDescripcion((String) objects[14]);
	    semaforoVo.setJustificacion(varJus);
	    if (varJus != null && varJus.length() > 8) {
		semaforoVo.setJustificacionCorto(varJus.substring(0, 8));
	    } else {
		semaforoVo.setJustificacionCorto(varJus);
	    }

	    if (id == 1) {
		estilo = "verde";
	    } else if (id == 2) {
		estilo = "amarillo";
	    } else if (id == 3) {
		estilo = "rojo";
	    } else if (id == 4) {
		estilo = "negro";
	    }
	    semaforoVo.setEstilo(estilo);
	    semaforoVo.setIdOficinaOrigen((Integer) objects[15]);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Error en cast: " + e.getMessage());
	}
	return semaforoVo;
    }

    /*
     * // <editor-fold defaultstate="collapsed" desc=" Cambia el color del
     * semaforo "> MLUIS 08/11/2013
     */
// </editor-fold>
    
    public boolean cambiarEstadoRuta(String correoSesion, String idGenero, List<SemaforoVo> listaFilasSeleccionada,
	    int idSemaforoSeleccionado, SemaforoVo semaforoVo) throws EmailNotFoundException {

	boolean v;
	String cp;
	String ccp;
	int idRol = siUsuarioRolRemote.traerRolPorUsuarioModulo(idGenero, Constantes.MODULO_SGYL, Constantes.AP_CAMPO_NEJO).get(0).getIdRol();
	String cco = usuarioRemote.find(Constantes.USUARIO_SIA).getEmail();
	SgSemaforo semaforoColorSeleccionado = sgSemaforoRemote.find(idSemaforoSeleccionado);
	List<EstadoSemaforoCambioVO> les = new ArrayList<EstadoSemaforoCambioVO>();
	String ruta = ""; // para publicar la noticia
	//Notificar
	for (SemaforoVo sem : listaFilasSeleccionada) {
	    EstadoSemaforoCambioVO escvo = new EstadoSemaforoCambioVO();
	    escvo.setRuta(sem.getNombreRuta());
	    escvo.setColorAnterior(sem.getColor());
	    escvo.setColorNuevo(semaforoColorSeleccionado.getColor());
	    escvo.setDescripcion(semaforoColorSeleccionado.getDescripcion());
	    les.add(escvo);
	}

	cp = gerenciaRemote.getResponsableByApCampoAndGerencia(1, 11, false).getEmail();
	ccp = correoCopia(); //dos correos, uno para  todo IHSA y otro para Direccion General
	v = notificacionServiciosGeneralesRemote.enviarCorreoCambioEstadoSemaforoDireccion(cp, ccp, cco, les, idSemaforoSeleccionado, semaforoVo.getJustificacion());

	if (v) {
	    for (SemaforoVo semVo : listaFilasSeleccionada) {

                if (semVo.getIdSemaforo() != idSemaforoSeleccionado) {
		    switch (idSemaforoSeleccionado) {

		    case Constantes.ID_COLOR_SEMAFORO_VERDE: //se cambia a Verde
//                            log("+++++++++++++++++Cambio de " + sgEstadoSemaforoAnterior.getSgSemaforo().getColor() + " a  Verde");
			evaluarCambioSemaforo(semVo, idSemaforoSeleccionado, semVo.getIdRuta(), idGenero);
			v = true;
			break;
		    case Constantes.ID_COLOR_SEMAFORO_AMARILLO: //se cambia a Amarillo
//                            log("+++++++++++++++++Cambio de " + sgEstadoSemaforoAnterior.getSgSemaforo().getColor() + " a  Amarillo");
			//Verifica que las solicitudes que tiene asignada el Jefe
			evaluarCambioSemaforo(semVo, idSemaforoSeleccionado, semVo.getIdRuta(), idGenero);
			v = true;
			break;
		    case Constantes.ID_COLOR_SEMAFORO_ROJO: //se cambia a Rojo
//                            log("+++++++++++++++++Cambio de " + sgEstadoSemaforoAnterior.getSgSemaforo().getColor() + " a  Rojo");
			evaluarCambioSemaforo(semVo, idSemaforoSeleccionado, semVo.getIdRuta(), idGenero);
			v = true;
			break;
		    case Constantes.ID_COLOR_SEMAFORO_NEGRO: //se cambia a Negro
//                            log("+++++++++++++++++Cambio de " + sgEstadoSemaforoAnterior.getSgSemaforo().getColor() + " a  negro");
			cancelarSolicitudesSemaforoNegro(semVo, "Cancelado por semaforo ", Constantes.UNO, idGenero, idRol);
			v = true;
			break;
		    }//fin del swic
		    if (v) {
			UtilLog4j.log.debug(this, new StringBuilder().append("Ruta: ").append(semVo.getIdRuta()).toString());
			UtilLog4j.log.debug(this, new StringBuilder().append("Semaforo >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : ").append(idSemaforoSeleccionado).toString());

			SgEstadoSemaforo sgEstadoSemaforo = new SgEstadoSemaforo();
			sgEstadoSemaforo.setActual(Constantes.BOOLEAN_TRUE);
			sgEstadoSemaforo.setSgSemaforo(semaforoColorSeleccionado);

//TODO GRMAPA                      sgEstadoSemaforo.setSgRutaTerrestre(sgRutaTerrestreRemote.find(semVo.getIdRuta()));
			sgEstadoSemaforo.setFechaInicio(new Date());
			sgEstadoSemaforo.setHoraInicio(new Date());
			if (idSemaforoSeleccionado == Constantes.ID_COLOR_SEMAFORO_NEGRO) {
//                            sgEstadoSemaforo.setHoraMaxima(null);
//                            sgEstadoSemaforo.setHoraMinima(null);
			} else {
			    if (semVo.getIdSemaforo() == Constantes.ID_COLOR_SEMAFORO_NEGRO) {
				SemaforoVo semTemp = traerUltimoColorRuta(semVo.getIdSemaforo(), semVo.getIdRuta());
//                                sgEstadoSemaforo.setHoraMaxima(semTemp.getHoraMaxima());
//                                sgEstadoSemaforo.setHoraMinima(semTemp.getHoraMinima());
			    } else {
//                                sgEstadoSemaforo.setHoraMaxima(semVo.getHoraMaxima());
//                                sgEstadoSemaforo.setHoraMinima(semVo.getHoraMinima());
			    }
			}
			sgEstadoSemaforo.setJustificacion(semaforoVo.getJustificacion());
			sgEstadoSemaforo.setGenero(new Usuario(idGenero));
			sgEstadoSemaforo.setFechaGenero(new Date());
			sgEstadoSemaforo.setHoraGenero(new Date());
			sgEstadoSemaforo.setEliminado(Constantes.NO_ELIMINADO);
			create(sgEstadoSemaforo);
			//Se obtienen las rutas selecionadas
			/**
			 * ************************* ESTANCIAS
			 * ***************************************************
			 *
			 * NOTA:El proceso de prolongacion de estancias solo se
			 * realiza desde el Timer a las 8:00 am.
			 */
//                        if (isRutaOficina) {
//                            log("es ruta a oficina");
//                            prolongarEstanciasPorOficinayRuta(destinoSgRutaTerrestre.getSgOficina().getId(), sgRutaTerrestre.getId(), idGenero, false);
//                        } else {
//                            prolongarEstanciasPorOficinayRuta(sgRutaTerrestre.getSgOficina().getId(), sgRutaTerrestre.getId(), idGenero, false);
//                        }
			/**
			 * ****************************************************************************
			 */
		    }
		}// Termina si el semaforo no es igual que el seleccionado
	    }//Fin del for de filas seleccionadas
	}//fin del primer if despues de enviar a todo a direccion general

        //Noticia
//        crearNoticia(idGenero, listaFilasSeleccionada, semaforoColorSeleccionado.getColor());
	//Se envia correo a todo ihsa
	List<SemaforoVo> ltem = new ArrayList<SemaforoVo>();
//        for (SemaforoVo semVoActual : listaFilasSeleccionada) {
//            SgEstadoSemaforo sgEstadoSemaforoAnt = find(semVoActual.getIdEstadoSemaforo());
//            if (sgEstadoSemaforoAnt.getSgSemaforo().getId() != idSemaforoSeleccionado) {
//                ltem.add(semVoActual);
//            }
//        }
	if (!ltem.isEmpty()) {
	    cp = Configurador.notificacionSemaforo();
	    UtilLog4j.log.info(this, "**** * * * * * * ** *Enviando correo a todo IHSA... cp="+cp);
	    //cp = "mluis@ihsa.mx";
	    notificacionServiciosGeneralesRemote.enviarCorreoCambioEstadoSemaforoTodoIhsa(cp, "", "", les, idSemaforoSeleccionado);
	}

	return v;
    }

    private SemaforoVo traerUltimoColorRuta(int idSemaforo, int idRuta) {
	try {
	    clearQuery();
	    query.append("select first 1 es.ID, es.HORA_MINIMA, es.HORA_MAXIMA, es.SG_SEMAFORO ");
	    query.append(" from SG_ESTADO_SEMAFORO es ");
	    query.append(" where es.SG_SEMAFORO <> ").append(idSemaforo);
	    query.append(" and es.SG_RUTA_TERRESTRE =  ").append(idRuta);
	    query.append(" and es.ACTUAL = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" and es.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" order by es.ID desc");
	    Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    if (objects != null) {
		SemaforoVo semaforoVo = new SemaforoVo();
//                semaforoVo.setIdEstadoSemaforo((Integer) objects[0]);
//                semaforoVo.setHoraMinima((Date) objects[1]);
//                semaforoVo.setHoraMaxima((Date) objects[2]);
		semaforoVo.setIdSemaforo((Integer) objects[3]);
		return semaforoVo;
	    } else {
		return null;
	    }
	} catch (Exception e) {
	    return null;
	}
    }

    /**
     * Joel Rodriguez Fecha : 12/nov/2013 Se ejecuta al cambio de semanforo en
     * procesos inversos a l semaforo negro.. Amarillo a Verde Rojo a Verde :Las
     * solicitudes que estaban para aprobacion de justificar pasan a ser
     * autorizadas automaticamente, y se quedan solo las solicitudes con fecha
     * de salida en fin de semana
     *
     * Negro a Verde :Las solicitudes que estaban para aprobacion de justificar
     * pasan a ser autorizadas automaticamente, y se queda con las solicitudes
     * con fecha de salida en fin de semana
     *
     * Negro a Rojo Negro a Amarillo :Las solicitudes que estaban para
     * aprobacion de justificar pasan a ser autorizadas automaticamente, y se
     * queda con las solicitudes con fecha de salida en fin de semana con la
     * hora minima y hora maxima respectiva al semaforo rojo..
     *
     * @param semaforoVoActual
     * @param idColorSemaforo
     */
    private void evaluarCambioSemaforo(SemaforoVo semVo, int idSemaforoACambiar, int idRuta, String idUsuarioGenera) {
	UsuarioRolVo urv = siUsuarioRolRemote.traerRolUsuarioModulo(Constantes.ROL_DIRECCION_GENERAL, Constantes.MODULO_SGYL, Constantes.BOOLEAN_TRUE, Constantes.AP_CAMPO_NEJO);
	//Verifica que las solicitudes que tiene asignada el Jefe
	try {
//            SgSemaforo semaforoNuevo = sgSemaforoRemote.find(idSemaforoNuevo);
	    if (urv != null && semVo != null) {
		//Autorizar las solicitudes de la opcion de SG
		autoAprobarSolicitudesEnEstatusAutorizar(semVo, urv.getIdUsuario(), Constantes.ESTATUS_JUSTIFICAR, idRuta);

		//saber si el cambio es hacia arriba o hacia abajo
		if (semVo.getIdSemaforo() < idSemaforoACambiar) {
		    UtilLog4j.log.info(this, "El cambio de semaforo es hacia arriba, se procede a cancelar las solicitudes que no cumplan con horarios");
		    //--evaluar semaforo para que cancele por horario se semaforo
		    List<EstatusAprobacionVO> lsol = sgEstatusAprobacionRemote.traerEstatusAprobacionPorUsuarioYRuta(Constantes.NULL, Constantes.INDICE_CERO, idRuta);
		    //traerEstatusAprobacionPorUsuario(urv.getIdUsuario(), Constantes.ESTATUS_JUSTIFICAR, Constantes.SOLICITUDES_TERRESTRES);
		    if (lsol != null && !lsol.isEmpty()) {
			log("Comenzando a cancelar las solicitudes que no cumplen con rangos de horarios en todos los estatus");
			//exclir las que no cumplen los semaforos o salen en fin de semana y dejar solo las que estan bien para pasar a aprobar
			List<EstatusAprobacionVO> lVioladas = obtenerSolicitudesVioladas(semVo, lsol, true);
			if (lVioladas != null && !lVioladas.isEmpty()) {
			    UtilLog4j.log.info(this, "Lista preparada para aprobaciones : " + lsol.size());
                            //sgEstatusAprobacionRemote.aprobarListaSolicitudes(lsol, true, idUsuarioGenera);
			    //cancelar las solicitudes que no cumplen rangos de horarios.
			    for (EstatusAprobacionVO vo : lVioladas) {
				log("Cancelando la solicitud " + vo.getCodigo());
				sgEstatusAprobacionRemote.cancelarSolicitud(vo.getId(), "Cancelado por cambio de semaforo  " + semVo.getColor(), idUsuarioGenera, true, true,Constantes.FALSE);
			    }
			}
		    } else {
			log("No existieron solicitudes*****");
		    }
		} else {
		    UtilLog4j.log.info(this, "El cambio de semaforo es hacia abajo, solo se autorizaron las solicitudes del jefe..");
		}
	    }
	} catch (Exception ex) {
	    Logger.getLogger(SgEstadoSemaforoImpl.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    //*Buscar las solicitudes en el estatus pasado por parametro y autoriza las solicitudes que no violan ningun horario
    private boolean autoAprobarSolicitudesEnEstatusAutorizar(SemaforoVo semaforoNuevo, String idUsuario, int estatusAutorizar, int idRuta) {
//        UtilLog4j.log.info(this,"Comenzando a autoaprobar "+semaforoNuevo.getColor()+" "+estatusAutorizar+" "+idRuta);
	List<EstatusAprobacionVO> lsol = sgEstatusAprobacionRemote.traerEstatusAprobacionPorUsuarioYRuta(idUsuario, estatusAutorizar, idRuta);
	if (lsol != null && !lsol.isEmpty()) {
	    UtilLog4j.log.info(this, "comenzando a autorizar las solicitudes en estatus Autorizar");
	    //obtener las solicitudes que violan politica
	    List<EstatusAprobacionVO> lVioladas = obtenerSolicitudesVioladas(semaforoNuevo, lsol, true);
	    //a la lista actual de todas las solicitudes por ruta quitar las violadas para dejar las buenas
	    lsol.removeAll(lVioladas);
	    if (lsol != null && !lsol.isEmpty()) {
		UtilLog4j.log.info(this, "Lista preparada para aprobaciones : " + lsol.size());
		sgEstatusAprobacionRemote.aprobarListaSolicitudes(lsol, true, idUsuario);
	    } else {
		log("NO existieron solicitudes en el estatus " + estatusAutorizar + " para pasar a la siguiente cadena");
	    }
	}
	return true;
    }

    /**
     * Este metodo es auxiliar al metodo "evaluarCambioSemaforo" y sirve para
     * excluir solicitudes de viajes al cambiar semaforos
     *
     * @param semaforoActual
     * @param listaIterar
     * @param excluirFechaFinSemana
     * @param excluirHoraMinimaMaxima
     */
    private List<EstatusAprobacionVO> obtenerSolicitudesVioladas(SemaforoVo semaforoActual, List<EstatusAprobacionVO> listaIterar,
	    boolean excluirHoraMinimaMaxima) {
//        log(" idSemaforo  " + semaforoActual.getIdEstadoSemaforo());
	boolean remover = false;
	List<EstatusAprobacionVO> listaSolicitudesVioladas = new ArrayList<EstatusAprobacionVO>();
	for (EstatusAprobacionVO vo : listaIterar) {
	    remover = false;
//            if (excluirFechaFinSemana) {
//                //excluir las que su fecha se salida no es un fin de semana
//                if (this.esFinDeSemanaLaFecha(vo.getFechaSalida())) {
//                    //listaIterar.remove(vo);
//                    remover = true;
//                    log("Viola la salida en fin de semana");
//                }
//            }
	    if (excluirHoraMinimaMaxima) {
		//excluir las que no cumplan con el rango de horario de salida
		if (vo.getHoraRegreso() != null) {
		    log("la hora de regreso es diferente a null es una solicitud de viaje con regreso");
//                    if (siManejoFechaLocal.validaHoraMinima(vo.getHoraSalida(), semaforoActual.getHoraMinima())
//                            || siManejoFechaLocal.validaHoraMaxima(vo.getHoraRegreso(), semaforoActual.getHoraMaxima())) {
//                        log("No cumple el rango- horaminima " + Constantes.FMT_hmm_a.format(vo.getHoraSalida()) + " - Horario minimo de semaforo " + Constantes.FMT_hmm_a.format(semaforoActual.getHoraMinima()));
//                        log("No cumple el rango- hora maxima " + Constantes.FMT_hmm_a.format(vo.getHoraRegreso()) + " - Horario maximo de semaforo " + Constantes.FMT_hmm_a.format(semaforoActual.getHoraMaxima()));
//                        remover = true;
//                    }
		} else {

//                    if (siManejoFechaLocal.validaHoraMinima(vo.getHoraSalida(), semaforoActual.getHoraMinima())) {
//                        log("No cumple el horario " + Constantes.FMT_hmm_a.format(vo.getHoraSalida()) + "- solo se valido la hora de salida - Horario minimo de semaforo " + Constantes.FMT_hmm_a.format(semaforoActual.getHoraMinima()));
//                        remover = true;
//                    }
		}
	    }
	    if (remover) {
		listaSolicitudesVioladas.add(vo);
		log("Agregada a lalista de remover");
	    } else {
		log("no violo nada");
	    }
	}
	log(" lista original  " + listaIterar.size());

	return listaSolicitudesVioladas;
    }

    private boolean esFinDeSemanaLaFecha(Date fechaCalcular) {
	return siManejoFechaLocal.finSemana(fechaCalcular);
    }

    private Date fechaSalida(SolicitudViajeVO solicitudViajeVO) {
	Date fs = solicitudViajeVO.getFechaSalida();
	Calendar cHoraSalida = Calendar.getInstance();
	String[] hcs = Constantes.FMT_hmm_a.format(solicitudViajeVO.getHoraSalida()).split(":");
	int hs = Integer.parseInt(hcs[0]);
	int ms = Integer.parseInt(hcs[1].substring(0, 2));
	cHoraSalida.set(Calendar.HOUR_OF_DAY, hs);
	cHoraSalida.set(Calendar.MINUTE, ms);
	Date fr2 = cHoraSalida.getTime();
	return fr2;
    }

    private Date fechaRegreso(SolicitudViajeVO solicitudViajeVO) {
	if (solicitudViajeVO.getFechaRegreso() != null) {
	    Date fr = solicitudViajeVO.getFechaRegreso();
	    Calendar cHoraSalida = Calendar.getInstance();
	    String[] hcs = Constantes.FMT_hmm_a.format(solicitudViajeVO.getHoraRegreso()).split(":");
	    int hs = Integer.parseInt(hcs[0]);
	    int ms = Integer.parseInt(hcs[1].substring(0, 2));
	    cHoraSalida.set(Calendar.HOUR_OF_DAY, hs);
	    cHoraSalida.set(Calendar.MINUTE, ms);
	    Date fr2 = cHoraSalida.getTime();
	    return fr2;
	}
	return null;
    }

    private void log(String mensaje) {
	UtilLog4j.log.info(this, mensaje);
    }

    /**
     * metodo utilizado para prolongar estancias por oficina y ruta
     *
     * @param idOficina
     * @param idRuta
     * @param idUsuarioCambio
     * @param esTimer
     * @throws EmailNotFoundException
     */
    
    public void prolongarEstanciasPorOficinayRuta(int idOficina, int idRuta, String idUsuarioCambio) throws EmailNotFoundException {
	log("prolongarEstancias");
	Date fechaProximaSalidaEstancia = siManejoFechaLocal.traerFechaProlongadaDiaLaboralesApartirHoy();
	List<SgHuespedHotelVo> listaHuespedesHotelConSalidaHoy;
	List<SgHuespedStaffVo> listaHuespedesStaffConSalidaHoy;
	List<SgHotel> sgHotelList;
	SemaforoVo semaforoVo = null;
	StringBuilder sb;
	boolean todoEnviado = false;
//            SgRutaTerrestre ruta = sgRutaTerrestreRemote.find(idRuta);
	log("*++++comenzanco a buscar estancias");
	log("Comenzando con Hoteles");
	//List<SgSolicitudEstanciaVo> listaSolicitudesGeneradasDeSolViaje = sgSolicitudEstanciaRemote.traerSolicitudesEstanciaGeneradasDeSolicutdViajePorOficinaYRuta(idOficina, idRuta);
	sgHotelList = this.sgHotelRemote.getAllHotel(idOficina);
	if (sgHotelList != null && !sgHotelList.isEmpty()) {
	    for (SgHotel sgHotel : sgHotelList) {
		log(" Analizando el hotel .....  " + sgHotel.getProveedor().getNombre());
		listaHuespedesHotelConSalidaHoy = this.sgHuespedHotelRemote.traerHuespedPorHotelPorFechaSalidaHoy(sgHotel.getId());
		if (listaHuespedesHotelConSalidaHoy != null && !listaHuespedesHotelConSalidaHoy.isEmpty()) {
		    UtilLog4j.log.info(this, "Existen huéspedes con vencimiento en hotel");
		    for (SgHuespedHotelVo sgHuespedHotel : listaHuespedesHotelConSalidaHoy) {
//                                if (this.siManejoFechaLocal.compare(sgHuespedHotel.getFechaSalida(), new Date()) == 0) {
//                        UtilLog4j.log.info(this,"Comenzando a buscar semaforo de la solicitud  " + sgHuespedHotel.getIdSgDetalleSolicitudEstancia());
//                        semaforoVo = this.sgSolicitudEstanciaRemote.traerSemaforoActualSolicitudViajeApartirDeSolicitudEstancia(sgHuespedHotel.getIdSgSolicitudEstancia());
//                        if (semaforoVo != null) {
//                            if (semaforoVo.getIdSemaforo() == Constantes.ID_COLOR_SEMAFORO_NEGRO) {
			UtilLog4j.log.info(this, "El color del semaforo es negro");
			sgHuespedHotelRemote.crearNuevoRegistroEstanciaProlongada(sgHuespedHotel.getId(), fechaProximaSalidaEstancia, Constantes.USUARIO_SIA);
//                            }
//                        }
		    }
		}
	    }
	}

	log("Comenzando con staff");
	List<SgStaff> listaStaff = sgStaffRemote.getAllStaffByStatusAndOficina(Constantes.BOOLEAN_FALSE, idOficina);
	if (listaStaff != null && !listaStaff.isEmpty()) {
	    for (SgStaff st : listaStaff) {
		listaHuespedesStaffConSalidaHoy = sgHuespedStaffRemote.traerHuespedStaffPorFechaSalidaHoy(st.getId(), Constantes.EXCLUIR_PERIODO_PRUEBA);
		//findAllVencimientoEstanciaPorStaff(new Date(), st, 0);
		if (listaHuespedesStaffConSalidaHoy != null && !listaHuespedesStaffConSalidaHoy.isEmpty()) {
		    UtilLog4j.log.info(this, "Existen huespedes con vencimiento");
		    for (SgHuespedStaffVo hs : listaHuespedesStaffConSalidaHoy) {
			try {
			    UtilLog4j.log.info(this, " Comenzando a buscar semaforo de la solicitud de estancia " + hs.getIdSgSolicitudEstancia());
//                            semaforoVo = this.sgSolicitudEstanciaRemote.traerSemaforoActualSolicitudViajeApartirDeSolicitudEstancia(hs.getIdSgSolicitudEstancia());
//                            if (semaforoVo != null) {
//                                //es un semaforo negro prolongar un dia mas al huesped
//                                //sghuespedStaffRemote.prolongarEstancia
//                                if (semaforoVo.getIdSemaforo() == Constantes.ID_COLOR_SEMAFORO_NEGRO) {
//                                    UtilLog4j.log.info(this,"El semaforo es negro alargar las estancias en staff " + hs.getId());
			    sgHuespedStaffRemote.actualizarFechaSalida(hs.getId(), fechaProximaSalidaEstancia, Constantes.USUARIO_SIA);
//                                }
//                            }
			} catch (Exception ex) {
			    UtilLog4j.log.fatal(this, "Excepción salidaAutomaticaHuespedStaff " + ex.getMessage());
			    ex.printStackTrace();
			}
		    }
		}
	    }
	}
    }

    private boolean actualizarFechaHuespedStaffYHotel(List<HuespedVo> listaHuespedHotel, List<HuespedVo> listaHuespedStaff, Date fechaNuevaSalida, String idUsuario, boolean esTimer) {
	try {
	    if (listaHuespedHotel != null && !listaHuespedHotel.isEmpty()) {
		for (HuespedVo vo : listaHuespedHotel) {
		    sgHuespedHotelRemote.crearNuevoRegistroEstanciaProlongada(vo.getId(), fechaNuevaSalida, idUsuario);
		}
	    }
	    if (listaHuespedStaff != null && !listaHuespedStaff.isEmpty()) {
		for (HuespedVo vo : listaHuespedStaff) {
		    sgHuespedStaffRemote.prolongarEstancia(vo.getId(), fechaNuevaSalida, idUsuario);
		}
	    }
	    return true;
	} catch (Exception e) {
	    log("Excepcion al prolongar los huespedes en staff y hote " + e.getMessage());
	    return false;
	}

    }

    private String correoCopia() {
	String correo = "";
	List<UsuarioTipoVo> luc = siUsuarioTipoRemote.getListUser(Constantes.ID_TIPO_CAMBIO_ESTADO_SEMAFORO, Constantes.ID_OFICINA_TORRE_MARTEL);
	for (UsuarioTipoVo usuarioTipoVo : luc) {
	    if (correo.isEmpty()) {
		correo = usuarioTipoVo.getCorreo();
	    } else {
		correo += "," + usuarioTipoVo.getCorreo();
	    }
	}
	return correo;
    }

    private void crearNoticia(List<RutaTerrestreVo> lSem, String color) {
	String titulo = "";
	StringBuilder mensaje = new StringBuilder();

	List<ComparteCon> listComparteCon = new ArrayList<ComparteCon>();
	try {
	    titulo = "Estado del semaforo -- ".concat(Constantes.FMT_TextDateLarge.format(new Date()));
	    mensaje.delete(0, mensaje.length());
	    mensaje.append("<br/> El departamento de G"
		    + "estión de Riesgos ha cambiado el estado del semaforo a color <b>").append(color);
	    mensaje.append("</b> debido a recientes acontecimientos en la(s) ruta(s) :<br/>");
	    //realizar la lista de rutas
	    mensaje.append("<ul>");
	    for (RutaTerrestreVo sem : lSem) {
		mensaje.append("<li><strong>");
		mensaje.append(sem.getNombre());
		mensaje.append("</strong></li>");
	    }
	    mensaje.append("</ul>");

	    //quien creo el viaje
	    listComparteCon.add(new ComparteCon("2", "", "", "privacidad"));
	    if (listComparteCon != null && !listComparteCon.isEmpty()) {
		UtilLog4j.log.info(this, "crear noticia");
		CoNoticia noti = coNoticiaRemote.nuevaNoticia("SIA", titulo, "", mensaje.toString(), 0, 0, listComparteCon);
		if (noti != null) {
		    try {
//                        for (SemaforoVo semaforoVo : lSem) {
//                            SgEstadoSemaforo sgEstadoSemaforo = sgEstadoSemaforoRemote.find(semaforoVo.getIdEstadoSemaforo());

//                            sgEstadoSemaforo.setCoNoticia(coNoticiaRemote.find(noti.getId()));
//                            sgEstadoSemaforo.setModifico(usuarioRemote.find(idSesion));
//                            sgEstadoSemaforo.setFechaModifico(new Date());
//                            sgEstadoSemaforo.setHoraModifico(new Date());
//                            edit(sgEstadoSemaforo);
//                        }
			//
		    } catch (Exception ex) {
			UtilLog4j.log.fatal(this, "Excepcion al crear la       noticia " + ex.getMessage());
		    }
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al crear la noticia " + e.getMessage());
	}
    }

    
    public SemaforoVo estadoActual(int idRuta) {
	try {
	    clearQuery();
//            appendQuery(" select es.ID, rt.ID, se.ID, rt.NOMBRE, se.COLOR, o.NOMBRE as origen, es.FECHA_INICIO, ");
//            appendQuery(" es.HORA_INICIO,es.FECHA_FIN, es.HORA_FIN, rt.HORA_MINIMARUTA, rt.HORA_MAXIMARUTA, es.JUSTIFICACION, rt.sg_tipo_especifico, se.descripcion, o.id");
//            appendQuery("  from SG_ESTADO_SEMAFORO es, SG_SEMAFORO se, SG_RUTA_TERRESTRE rt, SG_OFICINA o");
//            appendQuery(" where es.SG_SEMAFORO = se.ID and es.SG_RUTA_TERRESTRE = rt.ID ");
//            appendQuery(" and es.SG_RUTA_TERRESTRE =  ").append(idRuta);
//            appendQuery(" and es.ELIMINADO = 'False' and rt.SG_OFICINA = o.id ");
//            appendQuery(" and es.actual = '").append(Constantes.BOOLEAN_TRUE).append("'");

	    appendQuery(" select ");
	    appendQuery(" (select es.id from SG_ESTADO_SEMAFORO es where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1), ");
	    appendQuery(" s.SG_RUTA_TERRESTRE, ");
	    appendQuery(" (select es.SG_SEMAFORO from SG_ESTADO_SEMAFORO es where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1) as xx, ");
	    appendQuery(" r.NOMBRE, ");
	    appendQuery(" (select ss.color from SG_ESTADO_SEMAFORO es inner join SG_SEMAFORO ss on ss.id = es.SG_SEMAFORO where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1), ");
	    appendQuery(" o.NOMBRE, ");
	    appendQuery(" (select es.FECHA_INICIO from SG_ESTADO_SEMAFORO es where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1) as xxx, ");
	    appendQuery(" (select es.HORA_INICIO from SG_ESTADO_SEMAFORO es where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1), ");
	    appendQuery(" (select es.FECHA_FIN from SG_ESTADO_SEMAFORO es where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1),  ");
	    appendQuery(" (select es.HORA_FIN from SG_ESTADO_SEMAFORO es where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1), ");
	    appendQuery(" r.HORA_MINIMARUTA, r.HORA_MAXIMARUTA,  ");
	    appendQuery(" (select es.JUSTIFICACION from SG_ESTADO_SEMAFORO es where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1), ");
	    appendQuery(" r.sg_tipo_especifico,  ");
	    appendQuery(" (select ss.descripcion from SG_ESTADO_SEMAFORO es inner join SG_SEMAFORO ss on ss.id = es.SG_SEMAFORO where es.GR_MAPA = s.GR_MAPA order by es.id desc limit 1), ");
	    appendQuery(" r.SG_OFICINA  ");
	    appendQuery(" from GR_RUTAS_ZONAS s  ");
	    appendQuery(" inner join SG_RUTA_TERRESTRE r on r.id = s.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False'	 ");
	    appendQuery(" inner join SG_OFICINA o on o.id = r.SG_OFICINA and o.ELIMINADO = 'False'	 ");
	    appendQuery(" where s.SG_RUTA_TERRESTRE = ").append(idRuta);
	    appendQuery(" and s.ELIMINADO = 'False' order by xx desc, xxx desc limit 1 ");

	    Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    return castEstadoVO(obj);
	} catch (Exception e) {
	    e.getStackTrace();
	    UtilLog4j.log.fatal(this, "e actual semaforo: " + e.getMessage());
	    return null;
	}
    }

    
    public SemaforoVo buscarEstadoSemaforoPorId(int idEstadoSemaforo) {
	try {
	    clearQuery();
	    appendQuery(" select es.ID, rt.ID, se.ID, rt.NOMBRE, se.COLOR, o.NOMBRE as origen, es.FECHA_INICIO, ");
	    appendQuery(" es.HORA_INICIO,es.FECHA_FIN, es.HORA_FIN, es.HORA_MINIMA, es.HORA_MAXIMA, es.JUSTIFICACION, rt.sg_tipo_especifico, se.descripcion , o.id");
	    appendQuery("  from SG_ESTADO_SEMAFORO es, SG_SEMAFORO se, SG_RUTA_TERRESTRE rt, SG_OFICINA o");
	    appendQuery(" where  es.id = ").append(idEstadoSemaforo);
	    appendQuery(" and es.SG_SEMAFORO = se.ID and es.SG_RUTA_TERRESTRE = rt.ID");
	    appendQuery(" and es.ELIMINADO = 'False' and rt.SG_OFICINA = o.id ");
	    Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
	    return castEstadoVO(obj);
	} catch (Exception e) {
	    e.getStackTrace();
	    UtilLog4j.log.fatal(this, "e actual semaforo: " + e.getMessage());
	    return null;
	}
    }

    
    public List<SemaforoVo> traerEstadoSemaforoPorColor(int idSemaforo, boolean estado) {
	try {
	    List<Object[]> list;
	    clearQuery();
	    appendQuery(" select es.ID, rt.ID, se.ID, rt.NOMBRE, se.COLOR, o.NOMBRE as origen, es.FECHA_INICIO, ");
	    appendQuery(" es.HORA_INICIO,es.FECHA_FIN, es.HORA_FIN, es.HORA_MINIMA, es.HORA_MAXIMA, es.JUSTIFICACION ,rt.sg_tipo_especifico , se.descripcion,o.id ");
	    appendQuery("  from SG_ESTADO_SEMAFORO es, SG_SEMAFORO se, SG_RUTA_TERRESTRE rt, SG_OFICINA o");
	    appendQuery(" where es.SG_SEMAFORO = se.ID and es.SG_RUTA_TERRESTRE = rt.ID ");
	    appendQuery(" and es.sg_semaforo =  ").append(idSemaforo);
	    appendQuery(" and es.SG_SEMAFORO = se.ID and es.SG_RUTA_TERRESTRE  = rt.ID ");
	    appendQuery(" and es.ELIMINADO = 'False' and rt.SG_OFICINA = o.id and es.actual = '").append(estado).append("'");
	    appendQuery(" ORDER BY O.NOMBRE, RT.SG_TIPO_ESPECIFICO  ASC");
	    list = em.createNativeQuery(query.toString()).getResultList();
	    List<SemaforoVo> lv = new ArrayList<SemaforoVo>();
	    for (Object[] objects : list) {
		lv.add(castEstadoVO(objects));
	    }
	    return lv;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "e:  " + e.getMessage());
	    return null;
	}
    }

    
    public void actualizar(int idEstadoSemaforo, String idSesion, boolean eliminado) {
	//
	SgEstadoSemaforo sgEstadoSemaforo = find(idEstadoSemaforo);
	String ae = sgEstadoSemaforo.toString();
//        sgEstadoSemaforo.setHoraMinima(horaMinima);
//        sgEstadoSemaforo.setHoraMaxima(horaMaxima);
	sgEstadoSemaforo.setModifico(new Usuario(idSesion));
	sgEstadoSemaforo.setFechaModifico(new Date());
	sgEstadoSemaforo.setHoraModifico(new Date());
	sgEstadoSemaforo.setEliminado(eliminado);
	edit(sgEstadoSemaforo);
	UsuarioRolVo urv = siUsuarioRolRemote.traerRolUsuarioModulo(Constantes.ROL_DIRECCION_GENERAL, Constantes.MODULO_SGYL, Constantes.BOOLEAN_TRUE, Constantes.AP_CAMPO_NEJO);
        //
//TODO GRMAPA        autoAprobarSolicitudesEnEstatusAutorizar(buscarEstadoSemaforoPorId(idEstadoSemaforo), urv.getIdUsuario(), Constantes.ESTATUS_JUSTIFICAR, sgEstadoSemaforo.getSgRutaTerrestre().getId());

	//TODO GRMAPA       List<EstatusAprobacionVO> lsol = sgEstatusAprobacionRemote.traerEstatusAprobacionPorUsuarioYRuta(Constantes.NULL, Constantes.INDICE_CERO, sgEstadoSemaforo.getSgRutaTerrestre().getId());
	List<EstatusAprobacionVO> lsol = null;
	if (lsol != null) {
	    UsuarioRolVo usuarioRolVo = siUsuarioRolRemote.traerRolUsuarioModulo(Constantes.ROL_CENTRO_OPERACION, Constantes.MODULO_SGYL, Constantes.BOOLEAN_TRUE, Constantes.AP_CAMPO_NEJO);
	    List<EstatusAprobacionVO> lea = obtenerSolicitudesVioladas(buscarEstadoSemaforoPorId(idEstadoSemaforo), lsol, true);
	    if (lea != null && !lea.isEmpty()) {
		UtilLog4j.log.info(this, "Lista preparada para aprobaciones : " + lsol.size());
                //sgEstatusAprobacionRemote.aprobarListaSolicitudes(lsol, true, idUsuarioGenera);
		//cancelar las solicitudes que no cumplen rangos de horarios.
		for (EstatusAprobacionVO vo : lea) {
		    try {
			log("Cancelando la solicitud " + vo.getCodigo());
//TODO GRMAPA                        sgEstatusAprobacionRemote.cancelarSolicitud(vo.getId(), "Cancelada por cambio en el horario para la ruta  " + sgEstadoSemaforo.getSgRutaTerrestre().getNombre(),
//TODO GRMAPA                               usuarioRolVo.getIdUsuario(), true, false);
		    } catch (Exception ex) {
			Logger.getLogger(SgEstadoSemaforoImpl.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
	    }

	}	
    }

    
    public void actualizarJustificacion(int idEstadoSemaforo, String idSesion, String justificacion) {
	SgEstadoSemaforo sgEstadoSemaforo = find(idEstadoSemaforo);
	String ae = sgEstadoSemaforo.toString();

	sgEstadoSemaforo.setJustificacion(justificacion);
	sgEstadoSemaforo.setModifico(new Usuario(idSesion));
	sgEstadoSemaforo.setFechaModifico(new Date());
	sgEstadoSemaforo.setHoraModifico(new Date());
	edit(sgEstadoSemaforo);	
    }

    
    public void crearEstadoSemaforo(String idUsuario, int idRuta) {
	SgEstadoSemaforo sgEstadoSemaforo = new SgEstadoSemaforo();
	SgSemaforo sgSemaforo = sgSemaforoRemote.find(Constantes.ID_COLOR_SEMAFORO_AMARILLO);
//TODO GRMAPA         sgEstadoSemaforo.setSgRutaTerrestre(sgRutaTerrestreRemote.find(idRuta));
	sgEstadoSemaforo.setSgSemaforo(sgSemaforo);
	sgEstadoSemaforo.setJustificacion(sgSemaforo.getDescripcion());
	sgEstadoSemaforo.setFechaInicio(new Date());
//        sgEstadoSemaforo.setHoraMinima(horaMinima);
//        sgEstadoSemaforo.setHoraMaxima(horaMaxima);
	sgEstadoSemaforo.setGenero(new Usuario(idUsuario));
	sgEstadoSemaforo.setFechaGenero(new Date());
	sgEstadoSemaforo.setHoraGenero(new Date());
	sgEstadoSemaforo.setEliminado(Constantes.NO_ELIMINADO);
	sgEstadoSemaforo.setActual(Constantes.BOOLEAN_TRUE);
	create(sgEstadoSemaforo);	
    }

    
    public void eliminarEstadoSemaforo(String idSesion, int idSgRutaTerrestre) {
	SemaforoVo estadoSemaforo = this.estadoActual(idSgRutaTerrestre);
	//

	if (estadoSemaforo != null) {
//            SgEstadoSemaforo sgEstadoSemaforo = find(estadoSemaforo.getIdEstadoSemaforo());
//            String ae = sgEstadoSemaforo.toString();
//            sgEstadoSemaforo.setModifico(usuarioRemote.find(idSesion));
//            sgEstadoSemaforo.setFechaModifico(new Date());
//            sgEstadoSemaforo.setHoraModifico(new Date());
//            sgEstadoSemaforo.setEliminado(Constantes.ELIMINADO);
//            sgEstadoSemaforo.setActual(Constantes.BOOLEAN_FALSE);
//            edit(sgEstadoSemaforo);
	}

    }

    
    public void cancelarSolicitudesSemaforoNegro(SemaforoVo semVo, String cancelado_por_semaforo, int dias, String idGenero, int idRol) {
	String motivoCancelacion;
	boolean v = false;
	//Viajes en proceso

	log("comenzando a terminar solicitudes de viajes que se han asignado a viajer a medias..");
	if (sgSolicitudViajeRemote.terminarSolicitudesPorProcesoSemaforoPorRuta(semVo.getIdRuta(), cancelado_por_semaforo, dias)) {
	    log("Terminada las solicitudes en proceso por ruta");
	}

	//Viajes por salir
	List<ViajeVO> lvje = sgViajeRemote.traerViajesPorRuta(Constantes.ESTATUS_VIAJE_POR_SALIR, semVo.getIdRuta(), dias, false, null, 0);
	//

	List<SolicitudViajeVO> lTempo = new ArrayList<SolicitudViajeVO>();
	if (lvje != null) {
	    for (ViajeVO viajeVO : lvje) {
		try {
		    motivoCancelacion = "El viaje <b>".concat(viajeVO.getCodigo()).concat("</b> ha sido <b> CANCELADO</b>, debido a que el estado del semáforo se cambio a color Negro en la ruta seleccionada para el viaje.");
		    SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivoCancelacion, siOperacionRemote.find(Constantes.ID_SI_OPERACION_CANCELAR), usuarioRemote.find(idGenero));
		    sgViajeRemote.cancelTrip(usuarioRemote.find(idGenero), sgViajeRemote.find(viajeVO.getId()),
			    motivoCancelacion, false, siMovimiento, true);
		    v = true;
		} catch (SIAException ex) {
		    Logger.getLogger(SgEstadoSemaforoImpl.class.getName()).log(Level.SEVERE, null, ex);
		} catch (Exception ex) {
		    Logger.getLogger(SgEstadoSemaforoImpl.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	}
	//Cancela todas las solicitudes de viajes
	List<SolicitudViajeVO> lea = sgSolicitudViajeRemote.traerSolicitudesEnProcesoAprobacion(semVo.getIdRuta(), dias);

	for (SolicitudViajeVO sol : lea) {
	    if (sol.getIdEstatus() == Constantes.ESTATUS_PARA_HACER_VIAJE) {
		if (sol.getIdSemaforo() != Constantes.ID_COLOR_SEMAFORO_NEGRO) {
		    lTempo.add(sol);
		}
	    } else { // Cancela el resto de solicitudes
		lTempo.add(sol);
	    }
	}

	//Cancela todos los viajes por salir
	if (lTempo.size() > 0) {
	    int tab;
	    SgSolicitudViaje sgSolicitudViaje;
	    for (SolicitudViajeVO solVo : lTempo) {
		SolicitudViajeVO solicitudViajeVO = sgSolicitudViajeRemote.buscarPorId(solVo.getIdSolicitud(), Constantes.NO_ELIMINADO, Constantes.CERO);

		//Envia el correo la solicitud
		try {
		    EstatusAprobacionSolicitudVO estatusAprobacion = sgEstatusAprobacionRemote.buscarEstatusAprobacionPorIdSolicitudIdEstatus(solicitudViajeVO.getIdSolicitud(), solicitudViajeVO.getIdEstatus());
		    motivoCancelacion = "La solcicitud de viaje <b>".concat(solicitudViajeVO.getCodigo() != null ? solicitudViajeVO.getCodigo() : "--").concat("</b> ha sido <b> CANCELADA</b>, debido a que el estado del semáforo se cambio a color Negro en la ruta de viaje solicitada.");
		    v = sgEstatusAprobacionRemote.cancelarSolicitud(estatusAprobacion.getId(), motivoCancelacion, idGenero, false, Constantes.VIENE_SERVICIOS_GENERALES,Constantes.FALSE);
		    if (v) {
			//Genera las nuevas
			if (solicitudViajeVO.getIdOficinaDestino() == 0) {
			    tab = Constantes.RUTA_TIPO_CIUDAD;
			} else {
			    tab = Constantes.RUTA_TIPO_OFICINA;
			}
			//Nuevas solicitudes
			Calendar fechaSalidaCompleta = Calendar.getInstance();
			Calendar fechaRegresoCompleta = Calendar.getInstance();
			Date fr = null;
			fechaSalidaCompleta.setTime(solicitudViajeVO.getFechaSalida());
			String[] arrHora = siManejoFechaLocal.convertirHoraStringHHmmss(solicitudViajeVO.getHoraSalida()).split(":");
			fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHora[0]));
			Date fs = fechaSalidaCompleta.getTime();

			if (solicitudViajeVO.isRedondo()) {
			    fechaRegresoCompleta.setTime(solicitudViajeVO.getFechaRegreso());
			    String[] arrHoraReg = Constantes.FMT_HHmmss.format(solicitudViajeVO.getHoraRegreso()).split(":");
			    fechaSalidaCompleta.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrHoraReg[0]));
			    fr = fechaRegresoCompleta.getTime();
			}

			int idSol = sgSolicitudViajeRemote.save(solicitudViajeVO.getIdSgTipoSolicitudViaje(),
				solicitudViajeVO.getIdGerencia(), solicitudViajeVO.getIdOficinaOrigen(),
				semVo.getIdRuta(), solicitudViajeVO.getIdSgMotivo(),
				solicitudViajeVO.getObservacion(), fs, fr, solicitudViajeVO.getIdSiCiudadOrigen(),
				solicitudViajeVO.getIdSiCiudadDestino(), Constantes.TRUE, Constantes.FALSE, solicitudViajeVO.getGenero(),
				idRol, solicitudViajeVO.isRedondo(), solicitudViajeVO.getIdOficinaDestino(), 0, Constantes.BOOLEAN_TRUE);
			UtilLog4j.log.info(this, "Id sol viaje cancelada: " + solicitudViajeVO.getIdSolicitud());
			UtilLog4j.log.info(this, "Id sol viaje nueva: " + idSol);
			List<ViajeroVO> lvro = sgViajeroRemote.getAllViajerosList(solicitudViajeVO.getIdSolicitud());
			sgSolicitudViaje = sgSolicitudViajeRemote.find(idSol);
			for (ViajeroVO viajeroVO : lvro) {
			    SgViajero sgViajero = new SgViajero();
			    if (viajeroVO.getIdInvitado() > 0) {
				sgViajero.setSgInvitado(sgInvitadoRemote.find(viajeroVO.getIdInvitado()));
			    } else {
				sgViajero.setUsuario(new Usuario(viajeroVO.getIdUsuario()));
			    }
			    sgViajero.setSgSolicitudViaje(sgSolicitudViaje);
			    sgViajero.setGenero(new Usuario(solicitudViajeVO.getGenero()));
			    sgViajero.setFechaGenero(new Date());
			    sgViajero.setHoraGenero(new Date());
			    sgViajero.setEstancia(viajeroVO.isEstancia());
			    sgViajeroRemote.save(sgViajero, idGenero);
			}
		    }

		} catch (Exception ex) {
		    UtilLog4j.log.fatal(this, "ex: " + ex.getMessage());
		}
	    }
	} else {
	    v = true;
	}
    }

    
    public SemaforoVo getSemaforoZona(int idZona) {
	SemaforoVo obj = null;
	try {
	    clearQuery();
	    appendQuery(" select SG_SEMAFORO ");
	    appendQuery(" from SG_ESTADO_SEMAFORO ");
	    appendQuery(" where GR_MAPA = ").append(idZona);
	    appendQuery(" and ELIMINADO = 'False' ");
	    appendQuery(" order by ID desc ");
            appendQuery(" limit 1  ");

	    int objID = (Integer) em.createNativeQuery(query.toString()).getSingleResult();
	    if (objID > 0) {
		obj = sgSemaforoRemote.traerSemaforo(objID);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    obj = null;
	}
	return obj;
    }

    
    public List<SgEstadoSemaforoVO> getEstadoSemaforos(int idZona) {
	List<SgEstadoSemaforoVO> semaforos = null;
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(" SELECT ID, SG_SEMAFORO, HORA_INICIO, HORA_FIN, FECHA_INICIO, FECHA_FIN, HORA_MINIMA, HORA_MAXIMA, JUSTIFICACION, FECHA_GENERO, HORA_GENERO, FECHA_MODIFICO, HORA_MODIFICO, GR_MAPA ");
	    sb.append(" FROM SG_ESTADO_SEMAFORO  ");
	    sb.append(" where ELIMINADO = 'False' ");
	    sb.append(" and GR_MAPA is not null ");
	    if (idZona > 0) {
		sb.append(" and GR_MAPA = ").append(idZona);
	    }
	    sb.append(" order by ID desc  limit 50 ");
	    List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
	    if (lo != null) {
		semaforos = new ArrayList<SgEstadoSemaforoVO>();
		for (Object[] objects : lo) {
		    semaforos.add(castEstadoSemaforo(objects));
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    semaforos = null;
	}
	return semaforos;
    }

    private SgEstadoSemaforoVO castEstadoSemaforo(Object[] obj) {
	SgEstadoSemaforoVO vo = new SgEstadoSemaforoVO();
	vo.setId((Integer) obj[0]);
	vo.setSemaforoID((Integer) obj[1]);
	vo.setHoraInicio((Date) obj[2]);
	vo.setHoraFin((Date) obj[3]);
	vo.setFechaInicio((Date) obj[4]);
	vo.setFechaFin((Date) obj[5]);
//        vo.setHoraMinima((Date) obj[6]);
//        vo.setHoraMaxima((Date) obj[7]);
	vo.setJustificacion((String) obj[8]);
	vo.setFechaGenero((Date) obj[9]);
	vo.setHoraGenero((Date) obj[10]);
	vo.setGrMapaID((Integer) obj[13] != null ? (Integer) obj[13] : 0);
	vo.setMapa(grMapaRemote.getMapa(vo.getGrMapaID()));
	vo.setSemaforoVO(sgSemaforoRemote.traerSemaforo(vo.getSemaforoID()));

	return vo;
    }

    
    public SgEstadoSemaforo crearEstadoSemaforoZona(SgEstadoSemaforoVO semaforo, String usrID) {
	SgEstadoSemaforo nuevo = null;
	SgSemaforo semaf = null;
	try {
	    SgEstadoSemaforo ultimoSemaforo = this.finalizarEstadoSemaforoZona(semaforo.getSemaforoID(), semaforo.getGrMapaID(), usrID, false);
	    semaforo.setUltimoSemaforo(ultimoSemaforo); 
	    semaf = sgSemaforoRemote.find(semaforo.getSemaforoID());
	    semaforo.setNuevoSemaforo(semaf);
	    semaforo.setLstRutaByZona(sgRutaTerrestreRemote.traerRutaTerrestrePorZona(semaforo.getGrMapaID(), semaforo.getSemaforoID()));
	    if (ultimoSemaforo != null && ultimoSemaforo.getFechaFin() != null
		    && this.notificarCambioSemaforo(semaforo, usrID)) {
		//sgEstadoSemaforoRemote.edit(ultimoSemaforo);
                
		nuevo = new SgEstadoSemaforo();
		nuevo.setSgSemaforo(semaf);
		nuevo.setGrMapa(grMapaRemote.find(semaforo.getGrMapaID()));
		nuevo.setJustificacion(semaforo.getJustificacion());
		nuevo.setGenero(usuarioRemote.find(usrID));
		nuevo.setFechaGenero(new Date());
		nuevo.setHoraGenero(new Date());
		nuevo.setFechaInicio(new Date());
		nuevo.setHoraInicio(new Date());
		nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
		this.create(nuevo);
	    } else {
		throw new Exception("No se pudo finalizar el semaforo actual");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    nuevo = null;
	}
	return nuevo;
    }

    private boolean notificarCambioSemaforo(SgEstadoSemaforoVO semaforo, String usrID) {
        boolean v = false;
        try {
            String cp= "";
            String ccp;
            String cco="";
            Usuario u = usuarioRemote.find(Constantes.USUARIO_SIA);
            Usuario g = gerenciaRemote.getResponsableByApCampoAndGerencia(1, 11, false);
            if (u != null){
                cco = u.getEmail();
            }
            if (g != null){
                cp = g.getEmail();
            }
            
            ccp = correoCopia(); //dos correos, uno para  todo IHSA y otro para Direccion General
            v = notificacionServiciosGeneralesRemote.enviarCorreoCambioEstadoSemaforoDireccion(cp, ccp, cco, semaforo);
            crearNoticia(semaforo.getLstRutaByZona(), semaforo.getNuevoSemaforo().getColor());
            if (("Rojo".equalsIgnoreCase(semaforo.getNuevoSemaforo().getColor())) || ("Negro".equalsIgnoreCase(semaforo.getNuevoSemaforo().getColor()))) {
                notificarCambioSemaforoViajeros(semaforo, usrID);
            }
            if (!semaforo.getLstRutaByZona().isEmpty()) {
                cp = Configurador.notificacionSemaforo();
                UtilLog4j.log.info(this, "**** * * * * * * ** *Enviando correo a todo IHSA...cp="+cp);                
                notificacionServiciosGeneralesRemote.enviarCorreoCambioEstadoSemaforoTodoIhsa(cp, "", "", semaforo);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            v = false;
        }

        return v;
    }

    private boolean notificarCambioSemaforoViajeros(SgEstadoSemaforoVO semaforo, String usrID) {
	try {
	    for (RutaTerrestreVo rutaVO : semaforo.getLstRutaByZona()) {
		rutaVO.setZonas(grRutasZonasRemote.zonasPorRuta(rutaVO, true));
		notificarCambioSemaforoViajerosEP(rutaVO, semaforo);
		if (Constantes.ID_COLOR_SEMAFORO_NEGRO == semaforo.getNuevoSemaforo().getId()) {
		    cancelarViajesPS(rutaVO, semaforo, usrID);
		    pausaViajesPS(rutaVO, semaforo, usrID);
		} else if (Constantes.ID_COLOR_SEMAFORO_ROJO == semaforo.getNuevoSemaforo().getId()) {
		    pausaViajesPS(rutaVO, semaforo, usrID);
		}
		notificarCambioSemaforoViajerosPS(rutaVO, semaforo);
	    }

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
	return false;
    }

    private boolean notificarCambioSemaforoViajerosEP(RutaTerrestreVo rutaVO, SgEstadoSemaforoVO semaforo) {
        boolean ret = false;
        try {
            for (ViajeVO viajeVO : sgViajeRemote.traerViajesPorRuta(Constantes.ESTATUS_VIAJE_PROCESO, rutaVO.getId(), 0, false, null, 0)) {
                viajeVO.setListaViajeros(sgViajeroRemote.getTravellersByTravel(viajeVO.getId(), null));
                if(viajeVO.getListaViajeros() != null && viajeVO.getListaViajeros().size() > 0){
                    ret = notificacionServiciosGeneralesRemote.enviarCorreoCambioEstadoSemaforoViajeros(rutaVO, viajeVO, semaforo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            ret = false;
        }
        return ret;
    }

    private boolean notificarCambioSemaforoViajerosPS(RutaTerrestreVo rutaVO, SgEstadoSemaforoVO semaforo) {
        boolean ret = false;
        try {
            for (ViajeVO viajeVO : sgViajeRemote.traerViajesPorRuta(Constantes.ESTATUS_VIAJE_POR_SALIR, rutaVO.getId(), 0, false, null, 0)) {
                viajeVO.setListaViajeros(sgViajeroRemote.getTravellersByTravel(viajeVO.getId(), null));
                if(viajeVO.getListaViajeros() != null && viajeVO.getListaViajeros().size() > 0){
                    ret = notificacionServiciosGeneralesRemote.enviarCorreoCambioEstadoSemaforoViajeros(rutaVO, viajeVO, semaforo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            ret = false;
        }
        return ret;
    }

    private boolean cancelarViajesPS(RutaTerrestreVo rutaVO, SgEstadoSemaforoVO semaforo, String usrID) {
	boolean ret = false;
	try {
	    for (SgViaje viaje : sgViajeRemote.traerViajesPorRutaCancelar(Constantes.ESTATUS_VIAJE_POR_SALIR, rutaVO.getId(), semaforo.getGrMapaID(), Constantes.ID_COLOR_SEMAFORO_NEGRO, true)) {
		try {
		    StringBuilder motivoCancelacion = new StringBuilder();
		    motivoCancelacion.append("El viaje <b>");
		    motivoCancelacion.append(viaje.getCodigo());
		    motivoCancelacion.append("</b> ha sido <b> CANCELADO</b>, debido a que el estado del semáforo se cambio a color ");
		    motivoCancelacion.append(semaforo.getNuevoSemaforo().getColor());
		    motivoCancelacion.append(" en la ruta seleccionada para el viaje.");
		    SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivoCancelacion.toString(), siOperacionRemote.find(Constantes.ID_SI_OPERACION_CANCELAR), usuarioRemote.find(usrID));
		    sgViajeRemote.cancelTrip(usuarioRemote.find(usrID), viaje,
			    motivoCancelacion.toString(), false, siMovimiento, true);
		} catch (SIAException ex) {
		    UtilLog4j.log.fatal(this, ex);
		} catch (Exception ex) {
		    UtilLog4j.log.fatal(this, ex);
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    ret = false;
	}
	return ret;
    }

    private boolean pausaViajesPS(RutaTerrestreVo rutaVO, SgEstadoSemaforoVO semaforo, String usrID) {
	boolean ret = false;
	try {
	    for (SgViaje viaje : sgViajeRemote.traerViajesPorRutaCancelar(Constantes.ESTATUS_VIAJE_POR_SALIR, rutaVO.getId(), semaforo.getGrMapaID(), Constantes.ID_COLOR_SEMAFORO_ROJO, true)) {
		try {
		    StringBuilder motivoCancelacion = new StringBuilder();
		    motivoCancelacion.append("El viaje <b>");
		    motivoCancelacion.append(viaje.getCodigo());
		    motivoCancelacion.append("</b> ha sido <b> DETENIDO</b>, debido a que el estado del semáforo se cambio a color ");
		    motivoCancelacion.append(semaforo.getNuevoSemaforo().getColor());
		    motivoCancelacion.append(" en la ruta seleccionada para el viaje.");
		    Usuario usrSesion = usuarioRemote.find(usrID);
		    sgViajeRemote.pausaViaje(usrSesion, viaje, motivoCancelacion.toString(), null, true);
		    sgViajeRemote.compartirNoticiaGerentes(usrSesion, viaje);
		} catch (SIAException ex) {
		    UtilLog4j.log.fatal(this, ex);
		} catch (Exception ex) {
		    UtilLog4j.log.fatal(this, ex);
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    ret = false;
	}
	return ret;
    }

    
    public SgEstadoSemaforo finalizarEstadoSemaforoZona(int idSemaforo, int idMapa, String usrID, boolean finaliza) {
	SgEstadoSemaforo obj = null;
	try {
	    String sql = " SELECT ID, Sg_Semaforo as  idsemaforo "
	    +" FROM SG_ESTADO_SEMAFORO  "
	    +" where ELIMINADO = ? "
	    +" and FECHA_FIN is null "
	    +" and GR_MAPA = ?"
	    +" order by id desc limit 1 ";

            Record r = dslCtx.fetchOne(sql,Constantes.FALSE,idMapa);
            
            if(r != null) {
                obj = r.into(SgEstadoSemaforo.class);
                int idSgSemaforo = (int) r.getValue("idsemaforo");

                if (idSgSemaforo > 0) {
                    obj.setSgSemaforo(sgSemaforoRemote.find(idSgSemaforo));
                }

                obj.setFechaFin(new Date());
                obj.setHoraFin(new Date());
                obj.setFechaModifico(new Date());
                obj.setHoraModifico(new Date());

                if (obj != null && obj.getId() != null) {
                    String update = "update sg_estado_semaforo set "
                            + " modifico = ?, "
                            + "fecha_modifico = current_date,"
                            + " hora_modifico = current_time, "
                            + " fecha_fin = current_date,"
                            + " hora_fin = current_time"
                            + " where id = ?";
                    em.createNativeQuery(update)
                            .setParameter(1, usrID)
                            .setParameter(2, obj.getId());
                }
            }
            
	} catch (DataAccessException e) {
	    UtilLog4j.log.fatal(this, e);
	    obj = null;
            
	}
	return obj;
    }

    
    public String getColorSemaforoRuta(int idRutaTerestre) {
	String ret = null;
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(" select s.COLOR   ");
	    sb.append(" from SG_SEMAFORO s   ");
	    sb.append(" where s.ID = (   ");
	    sb.append(" SELECT xx   ");
	    sb.append(" from (   ");
	    sb.append(" select ( ");
	    sb.append(" 					SELECT ar.SG_SEMAFORO  ");
	    sb.append(" 					FROM SG_ESTADO_SEMAFORO ar  ");
	    sb.append(" 					where ar.ELIMINADO = 'False' ");
	    sb.append(" 					and ar.GR_MAPA = rz.GR_MAPA  	 ");
	    sb.append(" 					ORDER BY ar.ID DESC LIMIT 1 ) as xx   ");
	    sb.append(" 			from GR_RUTAS_ZONAS rz     ");
	    sb.append(" 			where rz.SG_RUTA_TERRESTRE = ").append(idRutaTerestre);
	    sb.append(" 			and rz.ELIMINADO = 'False'   ");
	    sb.append(" 			order by rz.SECUENCIA) AS xxx  ");
	    sb.append(" group by xx  order by xx desc LIMIT 1) ");

	    ret = String.valueOf((Object) em.createNativeQuery(sb.toString()).getSingleResult());

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	    ret = null;
	}
	return ret;
    }
}
