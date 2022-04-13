/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import com.newrelic.api.agent.Trace;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgInvitado;
import sia.modelo.SgOficina;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgViaje;
import sia.modelo.SgViajero;
import sia.modelo.SgViajeroSiMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.viaje.vo.MotivoRetrasoVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoCompartidaImpl;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.impl.SgHuespedHotelImpl;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgViajeroImpl extends AbstractFacade<SgViajero> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;
    @Inject
    private SgViajeroSiMovimientoImpl sgViajeroSiMovimientoRemote;
    @Inject
    private NotificacionViajeImpl notificacionViajeRemote;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelService;
    @Inject
    private SgHuespedStaffImpl sgHuespedStaffService;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaService;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private SgInvitadoImpl sgInvitadoRemote;
    @Inject
    private SgMotivoRetrasoImpl sgMotivoRetrasoRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private CoCompartidaImpl coCompartidaRemote;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionRemote;

    @Inject
    DSLContext dslCtx;
    
    private UtilLog4j LOGGER = UtilLog4j.log;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgViajeroImpl() {
        super(SgViajero.class);
    }

    
    public int countSgViajeroBySgSolicitudViaje(int idSgSolicitudViaje, boolean eliminado) {
        log("SgViajeroImpl.countSgViajeroBySgSolicitudViaje()");

        Query q = em.createNativeQuery("SELECT count(id) "
                + "FROM SG_VIAJERO "
                + "WHERE SG_SOLICITUD_VIAJE = " + idSgSolicitudViaje
                + "AND ELIMINADO = '" + (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO) + "'");

        return ((Integer) q.getSingleResult()).intValue();
    }

    private Usuario getResponsableByGerencia(int campo, int idGerencia) {
        return this.gerenciaRemote.getResponsableByApCampoAndGerencia(campo, idGerencia, false);
    }

    
    public int totalViajerosTerrestresPorOficina(int idOficina, int idEstatus, String idUsuario) {
        UtilLog4j.log.info(this, "totalViajerosTerrestresPorOficina" + idOficina);
        UtilLog4j.log.info(this, " " + idEstatus);
        UtilLog4j.log.info(this, " " + idUsuario);
        int totalUsuarios = 0;
        int totalInvitados = 0;
        int totalQuedadosOfifinaOrigen = 0;
        int totalQuedadosDestino = 0;
        clearQuery();
        try {
            appendQuery("SELECT count(distinct (v.id)) "); //0
            appendQuery(" FROM SG_VIAJERO v, SG_SOLICITUD_VIAJE s, ESTATUS e, SG_DIRECCION d, SG_OFICINA o, SG_ESTATUS_APROBACION ea, ");
            appendQuery(" usuario u,  sg_tipo_solicitud_viaje ts, sg_tipo_especifico te");
            appendQuery(" WHERE s.ESTATUS  = ").append(idEstatus);
            appendQuery("  and ea.usuario =  '").append(idUsuario).append("'");
            appendQuery("  AND s.OFICINA_DESTINO is not null");
            appendQuery("  AND v.Sg_SOLICITUD_VIAJE = s.ID ");
            appendQuery("  AND s.Oficina_Origen = o.id");
            appendQuery("  AND s.ESTATUS = e.ID");
            appendQuery("  AND o.SG_DIRECCION = d.ID");
            appendQuery("  AND ea.SG_SOLICITUD_VIAJE = s.ID");
            appendQuery("  AND v.usuario = u.ID");
            appendQuery("  AND s.SG_tipo_solicitud_viaje = ts.ID");
            appendQuery("  AND ts.sg_tipo_especifico = te.ID");
            appendQuery("  AND  te.ID = ").append(Constantes.SOLICITUDES_TERRESTRE);
            appendQuery("  AND v.sg_viaje IS null");
            appendQuery("  AND v.id NOT IN (Select vm.sg_viajero FROM sg_viajero_si_movimiento vm where vm.eliminado = 'False')");
            appendQuery("  AND ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery("  AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND o.ID = ").append(idOficina);
            UtilLog4j.log.info(this, "1");
            Object t = em.createNativeQuery(query.toString()).getSingleResult();
            totalUsuarios = ((Integer) (t != null ? t : 0)).intValue();
            log("total u: " + totalUsuarios);
            clearQuery();
            appendQuery("SELECT count(distinct (v.id)) ");
            appendQuery(" FROM SG_VIAJERO v, SG_SOLICITUD_VIAJE s, ESTATUS e, SG_DIRECCION d, SG_OFICINA o, SG_ESTATUS_APROBACION ea, ");
            appendQuery(" Sg_Invitado i,  sg_tipo_solicitud_viaje ts, sg_tipo_especifico te");
            appendQuery(" WHERE s.ESTATUS  = ").append(idEstatus);
            appendQuery(" and ea.usuario =  '").append(idUsuario).append("'");
            appendQuery("  AND s.OFICINA_DESTINO is not null");
            appendQuery("  AND v.Sg_SOLICITUD_VIAJE = s.ID ");
            appendQuery("  AND s.Oficina_Origen = o.id");
            appendQuery("  AND s.ESTATUS = e.ID");
            appendQuery("  AND o.SG_DIRECCION = d.ID");
            appendQuery("  AND ea.SG_SOLICITUD_VIAJE = s.ID");
            appendQuery("  AND v.sg_invitado = i.id");
            appendQuery("  AND s.SG_tipo_solicitud_viaje = ts.ID");
            appendQuery("  AND ts.sg_tipo_especifico = te.ID");
            appendQuery("  AND  te.ID = ").append(Constantes.SOLICITUDES_TERRESTRE);
            appendQuery("  AND  v.sg_viaje IS null");
            appendQuery("  AND v.id NOT IN (Select vm.sg_viajero FROM sg_viajero_si_movimiento vm where vm.eliminado = 'False')");
            appendQuery("  AND ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery("  AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND o.ID = ").append(idOficina);
            Object i = (Integer) em.createNativeQuery(query.toString()).getSingleResult();
            totalInvitados = ((Integer) (i != null ? i : 0)).intValue();
            log("total inv: " + totalInvitados);
            clearQuery();
            appendQuery("SELECT count(distinct (v.id))");
            appendQuery(" FROM SG_VIAJERO v, SG_SOLICITUD_VIAJE s, sg_viajero_si_movimiento vjm , SG_TIPO_SOLICITUD_VIAJE tsv");
            appendQuery(" WHERE vjm.si_movimiento IN (select m.id from si_movimiento m where m.si_operacion = 5)");//quitaron al viajero de la oficina de origen
            appendQuery(" AND vjm.sg_viajero = v.id");
            appendQuery(" AND s.OFICINA_DESTINO is not null");
            appendQuery(" AND vjm.eliminado = 'False'");
            appendQuery(" AND v.sg_solicitud_viaje = s.id");
            appendQuery(" AND v.sg_viaje is null");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND s.oficina_origen = ").append(idOficina);
            appendQuery(" AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND s.sg_tipo_solicitud_viaje = tsv.ID ");
            appendQuery(" AND tsv.SG_TIPO_ESPECIFICO = ").append(Constantes.SOLICITUDES_TERRESTRE);
            Object q = em.createNativeQuery(query.toString()).getSingleResult();
            totalQuedadosOfifinaOrigen = ((Integer) (q != null ? q : 0)).intValue();
            log("total u que ofi Ori: " + totalQuedadosOfifinaOrigen);
            clearQuery();
            appendQuery("SELECT count(distinct (v.id))");
            appendQuery(" FROM SG_VIAJERO v, SG_SOLICITUD_VIAJE s, sg_viajero_si_movimiento vjm, SG_TIPO_SOLICITUD_VIAJE tsv");
            appendQuery(" WHERE vjm.si_movimiento IN (select m.id from si_movimiento m where m.si_operacion = 6)"); //quitaron al viajero de la oficina de destino
            appendQuery(" AND vjm.sg_viajero = v.id");
            appendQuery(" AND vjm.eliminado = 'False'");
            appendQuery(" AND v.sg_viaje is null");
            appendQuery(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND v.sg_solicitud_viaje = s.id");
            appendQuery(" AND s.oficina_destino = ").append(idOficina);
            appendQuery(" AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" AND s.sg_tipo_solicitud_viaje = tsv.ID ");
            appendQuery(" AND tsv.SG_TIPO_ESPECIFICO = ").append(Constantes.SOLICITUDES_TERRESTRE);
            Object qd = em.createNativeQuery(query.toString()).getSingleResult();
            totalQuedadosDestino = ((Integer) (qd != null ? qd : 0)).intValue();
            log("total u que Ofi des: " + totalQuedadosDestino);
            return totalUsuarios + totalInvitados + totalQuedadosOfifinaOrigen + totalQuedadosDestino;

        } catch (Exception e) {
            log("EXCEPCION AL TRAER VIAJEROS TERRESTREES " + e.getMessage());
            return 0;
        }
    }

    
    public List<ViajeroVO> traerViajerosTerrestre(int sgOficina, int status, String idUsuario) {
        try {

            cuerpoViajeros(status, idUsuario, sgOficina);
            query.append("  ORDER BY v.SG_SOLICITUD_VIAJE ASC");
            List<Object[]> listInv = em.createNativeQuery(query.toString()).getResultList();

            return castTraerViajeros(listInv);
        } catch (Exception e) {
            log("EXCEPCION AL TRAER VIAJEROS TERRESTREES " + e.getMessage());
            return null;
        }
    }

    
    @Deprecated
    public List<ViajeroVO> viajeroQuedadoOficinaDestino(int idOficina) {
        clearQuery();

        query.append("SELECT distinct v.ID, u.id, i.id, s.codigo, ");
        query.append(" s.fecha_salida, s.hora_salida,s.fecha_regreso, ");
        query.append(" s.hora_regreso, s.oficina_origen, ");  //--Esta cambiado para mostrar la oficina de la cual salio el viaje.
        query.append(" v.estancia, v.redondo, 6,u.TELEFONO ,u.NOMBRE, i.NOMBRE, u.EMAIL, oorigin.NOMBRE, s.id ");
        query.append(" FROM SG_VIAJERO v");
        query.append("      inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID");
        query.append("      inner join SG_VIAJERO_SI_MOVIMIENTO vjm on vjm.SG_VIAJERO = v.ID");
        query.append("      inner join SI_MOVIMIENTO m on vjm.SI_MOVIMIENTO = m.ID");
        query.append("      inner join SG_TIPO_SOLICITUD_VIAJE tsv on s.SG_TIPO_SOLICITUD_VIAJE = tsv.ID");
        query.append("      left join USUARIO u on v.USUARIO = u.ID");
        query.append("      left join SG_INVITADO i on v.SG_INVITADO = i.ID");
        //query.append("      inner join sg_oficina odes On s.oficina_destino = odes.id ");
        query.append("      inner join SG_OFICINA oorigin on oorigin.ID= s.OFICINA_ORIGEN");//se agrego esta linea para poder mostrar el destino para regresar
        query.append(" WHERE m.SI_OPERACION = ").append(Constantes.QUEDADO_OFICINA_DESTINO);
        query.append(" AND tsv.SG_TIPO_ESPECIFICO = ").append(Constantes.SOLICITUDES_TERRESTRE);
        query.append(" AND s.oficina_destino = ").append(idOficina);
        query.append(" AND vjm.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" AND v.sg_viaje is null");
        query.append(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        for (Object[] objects : lo) {
            lv.add(castViajeroVO(objects));
        }
        return lv;
    }

    
    @Deprecated
    public List<ViajeroVO> viajeroQuedadoOficinaOrigen(int idOficina, String idUsuario) {
        clearQuery();

        query.append("SELECT distinct v.ID, u.id, i.id, s.codigo, ");
        query.append(" s.fecha_salida, s.hora_salida,s.fecha_regreso, ");
        query.append(" s.hora_regreso, s.oficina_origen, ");  //--Esta cambiado para mostrar la oficina de la cual salio el viaje.
        query.append(" v.estancia, v.redondo, 5 ,u.TELEFONO ,u.NOMBRE, i.NOMBRE, u.EMAIL, odes.nombre, s.id");
        query.append(" FROM SG_VIAJERO v");
        query.append("      inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID");
        query.append("      inner join SG_VIAJERO_SI_MOVIMIENTO vjm on vjm.SG_VIAJERO = v.ID");
        query.append("      inner join SI_MOVIMIENTO m on vjm.SI_MOVIMIENTO = m.ID");
        query.append("      inner join SG_TIPO_SOLICITUD_VIAJE tsv on s.SG_TIPO_SOLICITUD_VIAJE = tsv.ID");
        query.append("      left join USUARIO u on v.USUARIO = u.ID");
        query.append("      left join SG_INVITADO i on v.SG_INVITADO = i.ID");
        query.append("      inner join sg_oficina odes On s.oficina_destino = odes.id ");
        query.append(" WHERE m.SI_OPERACION = ").append(Constantes.QUEDADO_ORIGEN);
        query.append(" AND tsv.SG_TIPO_ESPECIFICO = ").append(Constantes.SOLICITUDES_TERRESTRE);
        query.append(" AND s.oficina_origen = ").append(idOficina);
        query.append(" AND v.sg_viaje is null");
        query.append(" AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        for (Object[] objects : lo) {
            lv.add(castViajeroVO(objects));
        }
        return lv;

    }

    
    public List<ViajeroVO> viajeroQuedado(int idOficina, int operacion, boolean eliminar) {

        String fecha = "";
        String oficina = "";
        clearQuery();

        query.append("SELECT distinct v.ID, u.id, i.id, s.codigo,  s.fecha_salida, s.hora_salida,s.fecha_regreso, s.hora_regreso, s.oficina_origen,"
                + " v.estancia, v.redondo,").append(operacion).append(",u.TELEFONO ,u.NOMBRE, i.NOMBRE, u.EMAIL, odes.NOMBRE, s.id"
                + " FROM SG_VIAJERO v"
                + " inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID"
                + " inner join SG_VIAJERO_SI_MOVIMIENTO vjm on vjm.SG_VIAJERO = v.ID"
                + " inner join SI_MOVIMIENTO m on vjm.SI_MOVIMIENTO = m.ID"
                + " inner join SG_TIPO_SOLICITUD_VIAJE tsv on s.SG_TIPO_SOLICITUD_VIAJE = tsv.ID"
                + " left join USUARIO u on v.USUARIO = u.ID"
                + " left join SG_INVITADO i on v.SG_INVITADO = i.ID");
        if (operacion == 6) {
            query.append("      inner join SG_OFICINA odes on odes.ID= s.OFICINA_ORIGEN");//se agrego esta linea para poder mostrar el destino para regresar
            fecha = "AND s.FECHA_REGRESO < CAST('NOW' AS DATE)";
            oficina = " AND s.oficina_destino = " + idOficina;
        } else {
            query.append("      inner join SG_OFICINA odes on odes.ID= s.OFICINA_DESTINO");
            fecha = " AND s.FECHA_SALIDA < CAST('NOW' AS DATE)";
            oficina = " AND s.oficina_origen = " + idOficina;
        }
        query.append(" WHERE m.SI_OPERACION = ").append(operacion);
        query.append(" AND tsv.SG_TIPO_ESPECIFICO = ").append(Constantes.SOLICITUDES_TERRESTRE);
        if (idOficina > 0) {
            query.append(oficina);
        }
        query.append(" AND vjm.eliminado = '").append(Constantes.NO_ELIMINADO).append("'"
                + " AND v.sg_viaje is null"
                + " AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'"
                + " AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        if (eliminar) {
            query.append(fecha);
        }
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        for (Object[] objects : lo) {
            lv.add(castViajeroVO(objects));
        }
        return lv;
    }

    
    public List<ViajeroVO> traerViajerosAereos(SgOficina sgOficina, int status) throws SIAException {
        try {
            List<Object[]> listUser;
            Query q = em.createNativeQuery("SELECT distinct v.ID, " //0
                    + "  u.nombre, " //1
                    + "  s.codigo, " //2
                    + "  s.fecha_salida, " //3
                    + "  s.hora_salida," //4
                    + "  s.fecha_regreso, " //5
                    + "  s.hora_regreso," //6
                    + "  o.nombre," //7
                    + "  s.gerencia_responsable" //8
                    + " FROM SG_VIAJERO v, SG_SOLICITUD_VIAJE s, ESTATUS e, SG_DIRECCION d, SG_OFICINA o, SG_ESTATUS_APROBACION ea, "
                    + " usuario u,  sg_tipo_solicitud_viaje ts, sg_tipo_especifico te"
                    + " WHERE s.ESTATUS  = " + status
                    + "  AND v.Sg_SOLICITUD_VIAJE = s.ID "
                    + "  AND s. Oficina_Origen = " + sgOficina.getId()
                    + "  AND s.Oficina_Origen = o.id"
                    + "  AND s.ESTATUS = e.ID"
                    + "  AND o.SG_DIRECCION = d.ID"
                    + "  AND ea.SG_SOLICITUD_VIAJE = s.ID"
                    + "  AND v.usuario = u.ID"
                    + "  AND s.SG_tipo_solicitud_viaje = ts.ID"
                    + "  AND ts.sg_tipo_especifico = te.ID"
                    + "  AND te.ID = 3"
                    + "  AND  v.sg_viaje IS null"
                    + "  AND v.id NOT IN (Select vm.sg_viajero FROM sg_viajero_si_movimiento vm)"
                    + "  AND ea.realizado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + "  AND ea.historial = '" + Constantes.BOOLEAN_FALSE + "'"
                    + "  AND v.eliminado = '" + Constantes.NO_ELIMINADO + "'"
                    + " ORDER BY v.SG_SOLICITUD_VIAJE ASC");
            listUser = q.getResultList();
            ViajeroVO v;
            List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
            for (Object[] objects : listUser) {
                v = new ViajeroVO();
                v.setId((Integer) objects[0]);
                v.setUsuario((String) objects[1]);
                v.setCodigoSolicitudViaje((String) objects[2]);
                v.setFechaSalida((Date) objects[3]);
                v.setHoraSalida((Date) objects[4]);
                v.setFechaRegreso((Date) objects[5]);
                v.setHoraRegreso((Date) objects[6]);
                v.setDestino((String) objects[7]);
                v.setGerenciaResponsable(gerenciaRemote.find((Integer) objects[8]).getNombre());
                lv.add(v);
            }
            return lv;
        } catch (Exception e) {
            throw new SIAException("Ocurrio un error, al recuperar los viajeros aereos");
        }
    }

    private ViajeroVO castViajeroVO(Object[] objects) {
        ViajeroVO v = new ViajeroVO();
        v.setId((Integer) objects[0]);
        if (objects[1] != null) {
            v.setUsuario((String) objects[13]);
            v.setIdUsuario((String) objects[1]);
            v.setCorreo((String) objects[15] != null ? (String) objects[15] : "");
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
        v.setIdDestino((Integer) objects[8]);
        v.setEstancia((Boolean) objects[9]);
        v.setRedondo((Boolean) objects[10]);
        v.setViajeroQuedado((Integer) objects[11]);
        v.setTelefono((String) objects[12]);
        v.setDestino((String) objects[16]);
        v.setIdSolicitudViaje((Integer) objects[17] != null ? (Integer) objects[17] : 0);
        return v;
    }

    private String clonarSolicitud(int oldSolicitudID, int oldRutaID, List<ViajeroVO> lstVros) {
        String ret = null;
        try {
            if (Constantes.RUTA_MTY_SF == oldRutaID) {
                int newSolicitudID = sgSolicitudViajeRemote.clonarSolicitudViaje(oldSolicitudID, Constantes.RUTA_REY_SF, oldRutaID, lstVros);
                sgEstatusAprobacionRemote.traerHistorialEstatusAprobacionPorSolicitud(oldSolicitudID, newSolicitudID);
            } else if (Constantes.RUTA_SF_MTY == oldRutaID) {
                int newSolicitudID = sgSolicitudViajeRemote.clonarSolicitudViaje(oldSolicitudID, Constantes.RUTA_REY_MTY, oldRutaID, lstVros);
                sgEstatusAprobacionRemote.traerHistorialEstatusAprobacionPorSolicitud(oldSolicitudID, newSolicitudID);
            }
        } catch (Exception e) {
            LOGGER.fatal(e);
            ret = "ERRORLOG";
        }
        return ret;
    }

    private Date recorrerTiempoDia(Date fecha, boolean inicioDia) {
        Calendar c = Calendar.getInstance();
        if (fecha != null) {
            c.setTime(fecha);
        }
        if (inicioDia) {
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        } else {
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.MILLISECOND, 999);
        }
        return c.getTime();
    }

    
    public String agregarViaje(String usuario, int viajeID, int solViajeID, int viajeroID, boolean escala) {
        String valRet = null;
        ViajeVO viajeVO = null;
        SolicitudViajeVO solVO = null;
        ViajeroVO viajeroVO = null;
        int capacidadLibreViajeros = 0;
        Date hoy = recorrerTiempoDia(null, true);
        try {
            if (usuario != null && !usuario.isEmpty() && viajeID > 0) {
                viajeVO = sgViajeRemote.buscarPorId(viajeID, true);
                if (viajeVO != null) {
                    if (viajeVO.getVehiculoVO() != null
                            && viajeVO.getVehiculoVO().getCapacidadPasajeros() > 0
                            && viajeVO.getListaViajeros() != null
                            && viajeVO.getListaViajeros().size() > 0
                            && (viajeVO.getVehiculoVO().getCapacidadPasajeros() - viajeVO.getListaViajeros().size()) < 1) {
                        valRet = "ErrorLibres";
                    } else {
                        capacidadLibreViajeros = viajeVO.getVehiculoVO().getCapacidadPasajeros() - viajeVO.getListaViajeros().size();
                        if (solViajeID > 0) {
                            List<SolicitudViajeVO> solicitudes = sgSolicitudViajeRemote.traerSolicitudesTerrestre(viajeVO.getIdOficinaOrigen(), 0, null, null, null, null, solViajeID);
                            if (solicitudes != null && solicitudes.size() > 0) {
                                solVO = solicitudes.get(0);
                                solVO.setFechaSalida(recorrerTiempoDia(solVO.getFechaSalida(), false));
                                if (solVO.isRedondo() && solVO.getFechaRegreso() != null) {
                                    solVO.setFechaRegreso(recorrerTiempoDia(solVO.getFechaRegreso(), false));
                                }
                            }

                            if (solVO != null && solVO.getViajeros() != null && solVO.getViajeros().size() > capacidadLibreViajeros) {
                                valRet = "ErrorSolCantidad";
                            } else if (solVO != null
                                    && (solVO.getSolicitudViajeDeRetorno() < 1 || solVO.getFechaSalida() == null || solVO.getHoraSalida() == null
                                    || viajeVO.getFechaProgramada() == null || viajeVO.getHoraProgramada() == null
                                    || ((Constantes.PRIMERA_VEZ_VIAJE == solVO.getSolicitudViajeDeRetorno()
                                    || Constantes.QUEDADO_ORIGEN == solVO.getSolicitudViajeDeRetorno())
                                    && solVO.getFechaSalida().before(hoy))
                                    || (Constantes.QUEDADO_OFICINA_DESTINO == solVO.getSolicitudViajeDeRetorno()
                                    && solVO.getFechaRegreso().before(hoy)))) {
                                valRet = "ErrorFechaSalida";
                            } else if (solVO != null && viajeVO.getIdRuta() != solVO.getIdRutaTerrestre()
                                    && ((solVO.getSolicitudViajeDeRetorno() == Constantes.QUEDADO_OFICINA_DESTINO
                                    && (viajeVO.getIdOficinaOrigen() != solVO.getIdOficinaDestino() || viajeVO.getIdOficinaDestino() != solVO.getIdOficinaOrigen()))
                                    || (solVO.getSolicitudViajeDeRetorno() == Constantes.VIAJERO_ESCALA && viajeVO.getIdOficinaDestino() != solVO.getIdOficinaDestino())
                                    || (solVO.getSolicitudViajeDeRetorno() != Constantes.VIAJERO_ESCALA && solVO.getSolicitudViajeDeRetorno() != Constantes.QUEDADO_OFICINA_DESTINO))) {
                                if ((viajeVO.getIdRuta() == Constantes.RUTA_MTY_REY
                                        && (solVO.getIdRutaTerrestre() == Constantes.RUTA_MTY_SF || solVO.getIdRutaTerrestre() == Constantes.RUTA_SF_MTY))
                                        || (viajeVO.getIdRuta() == Constantes.RUTA_SF_REY
                                        && (solVO.getIdRutaTerrestre() == Constantes.RUTA_MTY_SF || solVO.getIdRutaTerrestre() == Constantes.RUTA_SF_MTY))
                                        || ((viajeVO.getIdRuta() == Constantes.RUTA_REY_SF || viajeVO.getIdRuta() == Constantes.RUTA_REY_MTY)
                                        && Constantes.VIAJERO_ESCALA == solVO.getSolicitudViajeDeRetorno() && viajeVO.getIdOficinaDestino() == solVO.getIdOficinaDestino())) {
                                    if (escala || Constantes.VIAJERO_ESCALA == solVO.getSolicitudViajeDeRetorno()) {
                                        valRet = null; //clonarSolicitud(solViajeID, solVO.getIdRutaTerrestre(), solVO.getViajeros());
                                    } else {
                                        valRet = "ErrorRutaDirecto";
                                    }
                                } else {
                                    valRet = "ErrorSolRuta";
                                }
                            } else if (solVO != null) {
                                valRet = null;
                            } else {
                                valRet = "ErrorViaje";
                            }
                        } else {
                            viajeroVO = this.buscarViajeroPorId(viajeroID);
                            if (viajeroVO != null && viajeroVO.getFechaSalida() != null) {
                                viajeroVO.setFechaSalida(recorrerTiempoDia(viajeroVO.getFechaSalida(), false));
                                if (viajeroVO.isRedondo() && viajeroVO.getFechaRegreso() != null) {
                                    viajeroVO.setFechaRegreso(recorrerTiempoDia(viajeroVO.getFechaRegreso(), false));
                                }
                            }

                            if (capacidadLibreViajeros < 1) {
                                valRet = "ErrorVroCantidad";
                            } else if (viajeroVO != null
                                    && (viajeroVO.getViajeroQuedado() < 1 || viajeroVO.getFechaSalida() == null || viajeroVO.getHoraSalida() == null
                                    || viajeVO.getFechaProgramada() == null || viajeVO.getHoraProgramada() == null
                                    || ((Constantes.PRIMERA_VEZ_VIAJE == viajeroVO.getViajeroQuedado()
                                    || Constantes.QUEDADO_ORIGEN == viajeroVO.getViajeroQuedado())
                                    && viajeroVO.getFechaSalida().before(hoy))
                                    || (Constantes.QUEDADO_OFICINA_DESTINO == viajeroVO.getViajeroQuedado()
                                    && viajeroVO.getFechaRegreso().before(hoy)))) {
                                valRet = "ErrorFechaSalida";
                            } else if (viajeroVO == null
                                    || (viajeVO.getIdRuta() != viajeroVO.getIdRutaViaje()
                                    && ((viajeroVO.getViajeroQuedado() == Constantes.QUEDADO_OFICINA_DESTINO && (viajeVO.getIdOficinaOrigen() != viajeroVO.getIdOrigen() || viajeVO.getIdOficinaDestino() != viajeroVO.getIdDestino()))
                                    || (viajeroVO.getViajeroQuedado() == Constantes.VIAJERO_ESCALA && viajeVO.getIdOficinaDestino() != viajeroVO.getIdDestino())
                                    || (viajeroVO.getViajeroQuedado() != Constantes.VIAJERO_ESCALA && viajeroVO.getViajeroQuedado() != Constantes.QUEDADO_OFICINA_DESTINO)))) {
                                if (viajeroVO != null
                                        && ((viajeVO.getIdRuta() == Constantes.RUTA_MTY_REY
                                        && (Constantes.RUTA_MTY_SF == viajeroVO.getIdRutaViaje() || Constantes.RUTA_SF_MTY == viajeroVO.getIdRutaViaje()))
                                        || (viajeVO.getIdRuta() == Constantes.RUTA_SF_REY
                                        && (Constantes.RUTA_MTY_SF == viajeroVO.getIdRutaViaje() || Constantes.RUTA_SF_MTY == viajeroVO.getIdRutaViaje()))
                                        || ((viajeVO.getIdRuta() == Constantes.RUTA_REY_SF || viajeVO.getIdRuta() == Constantes.RUTA_REY_MTY)
                                        && Constantes.VIAJERO_ESCALA == viajeroVO.getViajeroQuedado() && viajeVO.getIdOficinaDestino() == viajeroVO.getIdDestino()))) {
                                    if (escala || Constantes.VIAJERO_ESCALA == viajeroVO.getViajeroQuedado()) {
                                        valRet = null; //cclonarSolicitud(viajeroVO.getIdSolicitudViaje(), viajeroVO.getIdRutaViaje(), viajeros);
                                    } else {
                                        valRet = "ErrorRutaDirecto";
                                    }
                                } else {
                                    valRet = "ErrorVroRuta";
                                }
                            }
                        }
                    }
                    if (valRet == null) {
                        if (solViajeID > 0 && solVO != null && solVO.getViajeros() != null && solVO.getViajeros().size() > 0) {
                            for (ViajeroVO viajero : solVO.getViajeros()) {
                                if (solVO.getIdRutaTerrestre() == viajeVO.getIdRuta()
                                        || (solVO.getIdRutaTerrestre() != viajeVO.getIdRuta()
                                        && (Constantes.VIAJERO_ESCALA == viajero.getViajeroQuedado() && viajero.getIdDestino() == viajeVO.getIdOficinaDestino())
                                        || (Constantes.QUEDADO_OFICINA_DESTINO == viajero.getViajeroQuedado() && viajero.getIdOrigen() == viajeVO.getIdOficinaOrigen())
                                        || (viajeVO.getIdOficinaDestino() == Constantes.ID_OFICINA_REY_PRINCIPAL
                                        && //viajero.getIdDestino() == viajeVO.getIdOficinaDestino() &&
                                        (solVO.getIdRutaTerrestre() == Constantes.RUTA_MTY_SF || solVO.getIdRutaTerrestre() == Constantes.RUTA_SF_MTY)))) {
                                    SgViajero ror = this.find(viajero.getId());
                                    boolean agregado = this.agregarViaje(usuario, ror, sgViajeRemote.find(viajeVO.getId()), false);
                                    if (agregado
                                            && (Constantes.RUTA_MTY_SF == solVO.getIdRutaTerrestre()
                                            || Constantes.RUTA_SF_MTY == solVO.getIdRutaTerrestre())
                                            && Constantes.ID_OFICINA_REY_PRINCIPAL == viajeVO.getIdOficinaDestino()) {
                                        SgViajero ro = this.clonarViajero(usuario, viajero.getId(), false);
                                        if (ro != null && ro.getId() > 0) {
                                            ror.setSgViajero(ro);
                                            ror.setEliminado(Constantes.BOOLEAN_TRUE);
                                            this.edit(ror);
                                        }
                                    }
                                    if (agregado
                                            && ((viajeVO.getIdRuta() == Constantes.RUTA_REY_SF || viajeVO.getIdRuta() == Constantes.RUTA_REY_MTY)
                                            && Constantes.VIAJERO_ESCALA == viajero.getViajeroQuedado())) {
                                        ror.setEliminado(Constantes.BOOLEAN_FALSE);
                                        this.edit(ror);
                                    }
                                }
                            }
                        } else if (viajeroID > 0 && viajeroVO != null) {
                            SgViajero ror = this.find(viajeroVO.getId());
                            boolean agregado = this.agregarViaje(usuario, ror, sgViajeRemote.find(viajeVO.getId()), false);
                            if (agregado
                                    && (Constantes.RUTA_MTY_SF == viajeroVO.getIdRutaViaje()
                                    || Constantes.RUTA_SF_MTY == viajeroVO.getIdRutaViaje())
                                    && Constantes.ID_OFICINA_REY_PRINCIPAL == viajeVO.getIdOficinaDestino()) {
                                //AGREGAR VALIDACION DE SI EXSTE OTRO VIAJERO SIMILAR
                                SgViajero ro = this.clonarViajero(usuario, viajeroVO.getId(), false);
                                
                                if (ro != null && ro.getId() > 0) {
                                    ror.setSgViajero(ro);
                                    ror.setEliminado(Constantes.BOOLEAN_TRUE);
                                    this.edit(ror);
                                }
                            }
                            if (agregado
                                    && ((viajeVO.getIdRuta() == Constantes.RUTA_REY_SF || viajeVO.getIdRuta() == Constantes.RUTA_REY_MTY)
                                    && Constantes.VIAJERO_ESCALA == viajeroVO.getViajeroQuedado())) {
                                ror.setEliminado(Constantes.BOOLEAN_FALSE);
                                this.edit(ror);
                            }
                        }
                        valRet = "true";
                    }
                } else {
                    valRet = "ErrorViaje";
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(e);
            valRet = "ERRORLOG";
        }
        return valRet;
    }

    
    public boolean agregarViaje(String idUsuario, SgViajero sgViajero, SgViaje sgViaje, boolean notificar) {
        boolean v;
        try {
            Usuario u = usuarioRemote.find(idUsuario); //>>> corregido el error de Insert into usuario
            String correoPara;//= sgViaje.getResponsable().getEmail();
            if (sgViajero.getUsuario() != null) {
                correoPara = sgViajero.getUsuario().getEmail();
            } else {
                correoPara = gerenciaRemote.traerResponsablePorApCampoYGerencia(sgViajero.getSgSolicitudViaje().getApCampo().getId(), sgViajero.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmailUsuario();
            }
            sgViajero.setSgViaje(sgViaje);
            sgViajero.setModifico(u);
            sgViajero.setFechaModifico(new Date());
            sgViajero.setHoraModifico(new Date());
            if (notificar) {
                v = notificacionViajeRemote.sendMailAddTravellerToTrip(u.getEmail(), "", sgViaje.getFechaProgramada(), sgViaje.getHoraProgramada(), sgViaje.getFechaSalida(), sgViaje.getHoraSalida(),
                        sgViaje.getFechaRegreso(), sgViaje.getHoraRegreso(), sgViaje.isRedondo(),
                        sgViaje.getCodigo(), sgViaje.getResponsable().getNombre(), sgViaje.getSgOficina().getNombre(), sgViaje.getSgRutaTerrestre().getId(),
                        Constantes.RUTA_TIPO_OFICINA, sgViaje.getId(), sgViajero.getSgInvitado() != null ? sgViajero.getSgInvitado().getNombre() : sgViajero.getUsuario().getNombre());//)correoPara, correoCopia(usuario.getEmail(), sgViajero), sgViaje, sgViajero);
                if (v) {
                    v = notificacionViajeRemote.sendMailTravellerToTrip(correoPara, traerCorreoResponsableSGLySeguridad(), sgViaje.getId(), sgViaje.getCodigo(), sgViajero);
                }
            } else {
                v = true;
            }

            if (v) {
                edit(sgViajero);      
            }

            v = true;
        } catch (Exception e) {
            log("···························Excepcion al agregar un viajero " + e);
            v = false;
        }
        return v;
    }

    
    public SgViajero clonarViajero(String usuario, int viajeroID, boolean redondo) {
        SgViajero newViajero = null;
        SgViajero oldViajero = this.find(viajeroID);
        try {
            newViajero = this.sgViajeroByViajeroEscala(viajeroID, Constantes.BOOLEAN_TRUE);
            
            if (newViajero == null && oldViajero != null ){
                if(oldViajero.getUsuario() != null && oldViajero.getUsuario().getId() != null){
                newViajero = sgViajeroByUsuarioAndSgSolicitudViaje(
                        oldViajero.getUsuario().getId(), oldViajero.getSgSolicitudViaje().getId(), Constantes.TRUE);
                } else if(oldViajero.getSgInvitado() != null && oldViajero.getSgInvitado().getId() > 0){
                newViajero = sgViajeroBySgInvitadoAndSgSolicitudViaje(
                        oldViajero.getSgInvitado().getId(), oldViajero.getSgSolicitudViaje().getId(), Constantes.TRUE);
                }
            }
            if (newViajero != null && newViajero.getId() > 0) {
                newViajero.setSgViaje(oldViajero.getSgViaje());
                newViajero.setEliminado(Constantes.BOOLEAN_FALSE);
                this.edit(newViajero);
            } else {
                
                if (oldViajero != null && oldViajero.getId() > 0) {
                    newViajero = new SgViajero();
                    if (oldViajero.getUsuario() != null) {
                        newViajero.setUsuario(oldViajero.getUsuario());
                    }
                    if (oldViajero.getSgSolicitudViaje() != null) {
                        newViajero.setSgSolicitudViaje(oldViajero.getSgSolicitudViaje());
                    }
                    if (oldViajero.getSgViaje() != null) {
                        newViajero.setSgViaje(oldViajero.getSgViaje());
                    }
                    if (oldViajero.getObservacion() != null) {
                        newViajero.setObservacion(oldViajero.getObservacion());
                    }
                    if (oldViajero.isEliminado()) {
                        newViajero.setEliminado(oldViajero.isEliminado());
                    }
                    if (oldViajero.getSgInvitado() != null) {
                        newViajero.setSgInvitado(oldViajero.getSgInvitado());
                    }
                    if (oldViajero.isEstancia()) {
                        newViajero.setEstancia(oldViajero.isEstancia());
                    }
                    if (oldViajero.getSgSolicitudEstancia() != null) {
                        newViajero.setSgSolicitudEstancia(oldViajero.getSgSolicitudEstancia());
                    }
                    if (oldViajero.isGrAut()) {
                        newViajero.setGrAut(oldViajero.isGrAut());
                    }

                    if (oldViajero.getGrAutMotivo() != null) {
                        newViajero.setGrAutMotivo(oldViajero.getGrAutMotivo());
                    }

                    newViajero.setGenero(new Usuario(usuario));
                    newViajero.setFechaGenero(new Date());
                    newViajero.setHoraGenero(new Date());

                    if (redondo) {
                        newViajero.setRedondo(Constantes.BOOLEAN_TRUE);
                    } else {
                        newViajero.setRedondo(Constantes.BOOLEAN_FALSE);
                    }
                    this.create(newViajero);
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(e+"-------------------------> clonarViajero()");
            newViajero = null;
        }
        return newViajero;
    }

    
    public SgViajero cancelarViajero(Usuario usuario, SgViajero sgViajero) {
        SgViajero sgV = null;
        try {
            sgV = find(sgViajero.getId());
            sgViajero.setSgViaje(null);
            sgViajero.setFechaModifico(new Date());
            sgViajero.setHoraModifico(new Date());
            sgViajero.setModifico(usuario);
            edit(sgViajero);
        } catch (Exception e) {
            return null;
        }
        return sgViajero;
    }

    
    public List<SgViajero> traerViajeroPorViaje(int idViaje) {
        log("SgViajeroImpl.traerViajeroPorViaje()");
        log("idVIaje: " + idViaje);
        try {
            return em.createNamedQuery("SELECT v FROM SgViajero v WHERE v.sgViaje.id = :viaje AND v.eliminado = :eli "
                    + "ORDER BY v.id ASC").setParameter("viaje", idViaje).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            log(e.getMessage());
            return null;
        }
    }

    /**
     * Modifico : 15/10/2015 MLUIS
     *
     * @param idViaje
     * @param usrGerente
     * @return
     */
    
    public List<ViajeroVO> getTravellersByTravel(int idViaje, String usrGerente) {
        log("SgViajeroImpl.getTravellersByTravel");
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        try {
            clearQuery();
            query.append(consultaViajero(usrGerente));
            query.append("      left join SI_MOVIMIENTO m on vm.SI_MOVIMIENTO = m.ID and m.SI_OPERACION in (4,5,");
            query.append(Constantes.ID_SI_OPERACION_AGREGAR_VIAJERO).append(",").append(Constantes.ID_SI_OPERACION_INTERCAMBIO_VIAJERO);
            query.append(") ");
            query.append("	and m.si_operacion <> ").append(Constantes.ID_SI_OPERACION_VIAJERO_NO_VIAJO);
            query.append("  WHERE v.sg_Viaje =  ").append(idViaje);
            query.append("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  AND V.ID not in (select vr.ID ");
            query.append("  from SG_VIAJE a ");
            query.append("  inner join SG_VIAJERO vr on vr.SG_VIAJE = a.id and a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  inner join SG_VIAJERO_SI_MOVIMIENTO vv on vv.SG_VIAJERO =  vr.id and vv.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  inner join SI_MOVIMIENTO m on m.id = vv.SI_MOVIMIENTO and m.ELIMINADO = 'False' and m.SI_OPERACION = ").append(Constantes.ID_SI_OPERACION_CAMBIO_VIAJERO_VIAJE);
            query.append("  where a.id = ").append(idViaje).append(") ");
            query.append("  ORDER BY v.id ASC");

            List<Object[]> listObject = em.createNativeQuery(query.toString()).getResultList();

            for (Object[] objects : listObject) {
                lv.add(castViajeroPorViaje(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "e : " + e.getMessage());
        }
        return lv;
    }

    
    public List<SgViajero> getListaViajeroPorViaje(int idViaje) {
        List<SgViajero> l = null;
        try {
            l = em.createQuery("SELECT v FROM SgViajero v WHERE v.sgViaje.id = :idViaje AND v.eliminado = :eli "
                    + " AND v.id NOT IN (SELECT vm.sgViajero.id FROM SgViajeroSiMovimiento vm WHERE vm.sgViajero.id = v.id)").setParameter("idViaje", idViaje).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
            return l;
        } catch (Exception e) {
            log("Excepcion getListaViajeroPorViaje " + e.getMessage());
            return null;
        }
    }

    
    public List<ViajeroVO> getListaViajeroVOPorViaje(int idViaje) {
        List<ViajeroVO> vo = new ArrayList<ViajeroVO>();
        try {
            clearQuery();
            //                     0     1     2      3          4                5              6    
            query.append("SELECT v.ID, u.id, i.id, s.codigo, s.fecha_salida, s.hora_salida,s.fecha_regreso, ");
            //                    7              8         9         1 0           11       12      13       14      15           16            17         18                  19       
            query.append("  s.hora_regreso, v.estancia, v.redondo, u.TELEFONO ,u.NOMBRE, i.NOMBRE, u.EMAIL, vj.ID, vj.CODIGO, m.SI_OPERACION, s.id, s.SG_RUTA_TERRESTRE, v.GRAUTOMOTIVO  ");
            query.append("  FROM SG_VIAJERO v");
            query.append("      left join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID");
            query.append("      left join USUARIO u on v.USUARIO = u.ID");
            query.append("      left join SG_INVITADO i on v.SG_INVITADO = i.ID");
            query.append("      left join SG_VIAJE vj on v.SG_VIAJE = vj.ID");
            query.append("      left join SG_VIAJERO_SI_MOVIMIENTO vjm on vjm.SG_VIAJERO = v.ID");
            query.append("      inner join SI_MOVIMIENTO m on vjm.SI_MOVIMIENTO = m.ID");
            query.append("  WHERE vj.ID = ").append(idViaje);
            query.append("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

            List<Object[]> obj = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] o : obj) {
                vo.add(castViajeroPorId(o));
            }
        } catch (Exception e) {
            log("Excepcion getListaViajeroPorViaje " + e.getMessage());

        }
        return vo;
    }

    
    public void save(SgViajero viajero, String idUsuario) throws SIAException, Exception {
        log("SgViajeroImpl.save()");
        if (viajero.getSgSolicitudViaje().isRedondo()) {
            viajero.setRedondo(Constantes.BOOLEAN_TRUE);
        } else {
            viajero.setRedondo(Constantes.BOOLEAN_FALSE);
        }
        viajero.setGenero(new Usuario(idUsuario));
        viajero.setFechaGenero(new Date());
        viajero.setHoraGenero(new Date());
        viajero.setEliminado(Constantes.NO_ELIMINADO);
        viajero.setGrAut(Constantes.NO_ELIMINADO);

        create(viajero);
    }

    
    public void crearViajero(SgViajero viajero, String idUsuario) throws SIAException, Exception {
        viajero.setGenero(new Usuario(idUsuario));
        viajero.setFechaGenero(new Date());
        viajero.setHoraGenero(new Date());
        viajero.setEliminado(Constantes.NO_ELIMINADO);
        viajero.setGrAut(Constantes.NO_ELIMINADO);
        create(viajero);
    }

    
    public int guardarViajero(int idInvitado, String idUsuario, int solicitudEstancia, int idSolicitudViaje, int idViaje,
            String observacion, String idSesion, boolean estancia, boolean redondo) {
        SgViajero viajero = new SgViajero();
        try {
            log("SgViajeroImpl. guardar viajero()");
            viajero.setSgInvitado(idInvitado == 0 ? null : sgInvitadoRemote.find(idInvitado));
            viajero.setUsuario(idInvitado == 0 ? new Usuario(idUsuario) : null);
            viajero.setSgSolicitudEstancia(solicitudEstancia == 0 ? null : sgSolicitudEstanciaRemote.find(solicitudEstancia));
            viajero.setSgSolicitudViaje(sgSolicitudViajeRemote.find(idSolicitudViaje));
            viajero.setObservacion(observacion);
            viajero.setEstancia(estancia);
            viajero.setRedondo(redondo);
            viajero.setSgViaje(idViaje == 0 ? null : sgViajeRemote.find(idViaje));
            viajero.setGenero(new Usuario(idSesion));
            viajero.setFechaGenero(new Date());
            viajero.setHoraGenero(new Date());
            viajero.setEliminado(Constantes.NO_ELIMINADO);
            viajero.setGrAut(Constantes.NO_ELIMINADO);
            create(viajero);
            log("SgViajero CREATED SUCCESSFULLY");

        } catch (Exception e) {
            e.getStackTrace();
        }
        return viajero.getId();
    }

    
    public void save(Usuario usuario, SgInvitado sgInvitado, int idSgSolicitudViaje, int idSgTipoEspecifico, String observacion, String idUsuario, boolean solicitoEstancia) {
        log("SgViajeroImpl.save()");

        SgViajero sgViajero = new SgViajero();
        SgSolicitudViaje sgSolicitudViaje = sgSolicitudViajeRemote.find(idSgSolicitudViaje);
        sgViajero.setEstancia(solicitoEstancia ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        sgViajero.setSgSolicitudViaje(sgSolicitudViaje);
        sgViajero.setObservacion(observacion);

        if (sgSolicitudViaje.isRedondo()) {
            sgViajero.setRedondo(Constantes.BOOLEAN_TRUE);
        } else {
            sgViajero.setRedondo(Constantes.BOOLEAN_FALSE);
        }
        //Validar si ya existe en la Solicitud de Viaje el Viajero

        if (idSgTipoEspecifico == 19) {
            sgViajero.setUsuario(usuario);
        } else if (idSgTipoEspecifico == 20) {
            sgViajero.setSgInvitado(sgInvitado);
        }

        sgViajero.setGenero(new Usuario(idUsuario));
        sgViajero.setFechaGenero(new Date());
        sgViajero.setHoraGenero(new Date());
        sgViajero.setEliminado(Constantes.NO_ELIMINADO);
        sgViajero.setGrAut(Constantes.NO_ELIMINADO);

        super.create(sgViajero);
    }

    
    public void saveOrUpdateAllViajerosForSgSolicitudViaje(int idSgSolicitudViaje, List<ViajeroVO> viajeros, String idUsuario) {
        log("SgViajeroImpl.saveOrUpdateAllViajerosForSgSolicitudViaje()");
        try {
            if (viajeros != null) {
                for (ViajeroVO vo : viajeros) {
                    if (vo.isSavedDB()) {
                        SgViajero sgViajero = find(vo.getId().intValue());
                        if (vo.isAgregado()) {
                            log("isAgregado(): " + vo.isAgregado());

                            log("isEstanciaB: " + vo.isEstanciaB() + " - estancia: " + sgViajero.isEstancia());

                            if ((vo.isEstanciaB() && sgViajero.isEstancia()) || (!vo.isEstanciaB() && sgViajero.isEstancia())) {
                                log("entró a actualizar estancia");
                                log("vo.isEstanciaB(): " + vo.isEstanciaB());
                                update(vo.getId(), vo.getIdUsuario(), (vo.isEmpleado() ? -1 : vo.getIdInvitado().intValue()), idSgSolicitudViaje, -1, 0, vo.isEstanciaB(), idUsuario);
                            }
                        } else {
                            delete(sgViajero.getId(), idUsuario, "");
                        }
                    } else {
                        if (vo.isAgregado()) {
                            SgViajero v = new SgViajero();
                            v.setObservacion("");
                            v.setEstancia(vo.isEstanciaB() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                            v.setSgSolicitudViaje(this.sgSolicitudViajeRemote.find(idSgSolicitudViaje));
                            if (vo.isEmpleado()) {
                                v.setUsuario(new Usuario(vo.getIdUsuario()));
                            } else {
                                v.setSgInvitado(this.sgInvitadoRemote.find(vo.getIdInvitado()));
                            }
                            save(v, idUsuario);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log("error en GUADAR TODOS LOS VIAJEROS");
            log(e.getMessage());
            e.printStackTrace();
        }
    }

    
    public void update(int idSgViajero, String idEmpleado, int idSgInvitado, int idSgSolicitudViaje, int idSgViaje, int idSgSolicitudEstancia, boolean estancia, String idUsuario) {
        SgViajero sgViajero = find(idSgViajero);

        if (idEmpleado != null && !idEmpleado.isEmpty()) {
            sgViajero.setUsuario(new Usuario(idEmpleado));
        } else {
            sgViajero.setSgInvitado(this.sgInvitadoRemote.find(idSgInvitado));
        }
        sgViajero.setSgSolicitudViaje(this.sgSolicitudViajeRemote.find(idSgSolicitudViaje));
        if (idSgSolicitudEstancia > 0) {
            sgViajero.setSgSolicitudEstancia(this.sgSolicitudEstanciaRemote.find(idSgSolicitudEstancia));
        }
        if (idSgViaje > 0) {
            sgViajero.setSgViaje(this.sgViajeRemote.find(idSgViaje));
        }
        sgViajero.setEstancia(estancia ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);

        sgViajero.setModifico(new Usuario(idUsuario));
        sgViajero.setFechaModifico(new Date());
        sgViajero.setHoraModifico(new Date());

        edit(sgViajero);

        log("SgViajero UPDATED SUCCESSFULLY");
    }

    
    public void changeEstateOfEstancia(SgViajero sgViajero, Usuario usuario, boolean estate) {
        try {
            sgViajero.setEstancia(estate ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
            sgViajero.setModifico(usuario);
            sgViajero.setFechaModifico(new Date());
            sgViajero.setHoraModifico(new Date());
            log("se realizo el cambio a " + sgViajero.isEstancia());
            super.edit(sgViajero);

            //checar si tiene una solicitud de estancia solicitada..cancelarla
            if (sgViajero.getSgSolicitudViaje().getEstatus().getId() > 410) {//si ya se solicito quitar la sol de estancia
                if (sgViajero.getSgSolicitudEstancia() != null) {
                    lougueOfViajeroCancel(sgViajero.getId(), "Cancelación Automatica", usuario.getId());
                }
            }

        } catch (Exception e) {
            log("Excepcion en cambiar el estado de la Estancia" + e.getMessage());
        }
    }

    
    public SgViajero delete(int idViajero, String idUsuario, String motivo) throws SIAException, Exception {
        log("SgViajeroImpl.delete()");
        SgViajero viajero = find(idViajero);
        viajero.setModifico(new Usuario(idUsuario));
        viajero.setFechaModifico(new Date());
        viajero.setHoraModifico(new Date());
        viajero.setEliminado(Constantes.ELIMINADO);
        edit(viajero);
        
        if (!motivo.isEmpty()) {
            //Guar da el movimiento
            SiMovimiento sm = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(3), usuarioRemote.find(idUsuario));
            //
            sgViajeroSiMovimientoRemote.guardarMovimiento(idUsuario, viajero.getId(), sm);

        }

        log("SgViajero DELETED SUCCESSFULLY");
        if (viajero.getSgSolicitudViaje() != null) {
            if (viajero.getSgSolicitudViaje().getEstatus().getId() >= 450) {//si ya esta CON SERVICIOS GENERALES
                if (viajero.getSgSolicitudEstancia() != null) {
                    log("Eliminar la estancia");
                    this.lougueOfViajeroCancel(viajero.getId(), "Cancelación automática ", idUsuario);
                }
            }
        }
        return viajero;
    }

    
    public List<SgViajero> getViajerosBySolicitudViajeList(SgSolicitudViaje solicitudViaje, boolean eliminado) throws SIAException {
        log("SgViajeroImpl.getViajerosBySolicitudViajeList()");
        if (solicitudViaje != null) {
            List<SgViajero> viajerosList = null;
            viajerosList = em.createQuery("SELECT v FROM SgViajero v WHERE v.sgSolicitudViaje.id = :idSolicitudViaje "
                    + " AND v.eliminado = :eliminado").setParameter("idSolicitudViaje", solicitudViaje.getId()).setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).getResultList();

            log("Se encontraron " + (!viajerosList.isEmpty() ? viajerosList.size() : 0) + " Viajeros");
            return viajerosList;

        } else {
            throw new SIAException(this.getClass().getName(),
                    "getViajerosBySolicitudViajeList()",
                    "Faltan parámetros para realizar la consulta",
                    ("solicitudViaje:" + (solicitudViaje != null ? solicitudViaje.getId() : null)));
        }
    }

    
    public List<ViajeroVO> getViajerosWithEstanciaBySolicitudViajeList(int idSolicitud, boolean conEstancia) throws SIAException {
        log("SgViajeroImpl.getViajerosWithEstanciaBySolicitudViajeList()");
        log("Solicitud " + idSolicitud);
        String comodin = "";
        String castVar = "";
        ViajeroVO v;
        clearQuery();
        try {

            if (!conEstancia) {
                comodin = "  AND VJ.ESTANCIA = '" + conEstancia + "'";
            }
            appendQuery("SELECT VJ.ID,");//0
            appendQuery(" CASE WHEN VJ.SG_SOLICITUD_ESTANCIA is null THEN 0 ");//1
            appendQuery(" WHEN VJ.SG_SOLICITUD_ESTANCIA is not null THEN VJ.SG_SOLICITUD_ESTANCIA");
            appendQuery(" END AS SG_SOLUCITUD_ESTANCIA,");
            appendQuery(" VJ.ESTANCIA,"); //2
            appendQuery(" U.id AS USUARIO_ID,");//3
            appendQuery(" U.NOMBRE AS USUARIO_NOMBRE,");//4
            appendQuery(" VJ.OBSERVACION,"); //5
            appendQuery(" CASE WHEN INV.ID is null THEN 0 "); //6
            appendQuery(" WHEN INV.ID is not null THEN INV.ID ");
            appendQuery(" END AS ID_INVITADO,");
            appendQuery(" INV.NOMBRE AS INVITADO_NOMBRE"); //7
            appendQuery(" FROM SG_VIAJERO VJ LEFT OUTER JOIN USUARIO U ON VJ.USUARIO = U.ID,");
            appendQuery(" SG_VIAJERO VJ2 LEFT OUTER JOIN SG_INVITADO INV ON VJ2.SG_INVITADO = INV.ID");
            appendQuery(" WHERE VJ.SG_SOLICITUD_VIAJE = ").append(idSolicitud);
            appendQuery("  AND VJ.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(comodin);
            appendQuery(" AND VJ.id = VJ2.id ");
            appendQuery(" order by VJ.ID");
//            log("Query " + q.toString());
            List<ViajeroVO> lista = new ArrayList<ViajeroVO>();
            List<Object[]> list = em.createNativeQuery(query.toString()).getResultList();
            for (Object[] objects : list) {
                v = new ViajeroVO();
                v.setId((Integer) objects[0]);
                v.setSgSolicitudEstancia((Integer) objects[1]);
                v.setEstancia((Boolean)(objects[2]));
                v.setIdUsuario(String.valueOf(objects[3]));
                v.setUsuario(String.valueOf(objects[4]));
                v.setObservacion(String.valueOf(objects[5]));
                v.setIdInvitado((Integer) objects[6]);
                v.setInvitado(!(String.valueOf(objects[7]).equals("null")) ? String.valueOf(objects[7]) : "");
                v.setAgregado(false);
                lista.add(v);
            }
            return lista;
        } catch (Exception e) {
            log("Excepcion al traer viajeros nativos " + e.getMessage());
            return null;
        }
    }

    
    @Trace
    public List<ViajeroVO> getAllViajerosList(int idSgSolicitudViaje) {
        log("idSolicitud " + idSgSolicitudViaje);
        List<ViajeroVO> lista = new ArrayList<ViajeroVO>();
        try {
            lista = this.getAllViajerosList(idSgSolicitudViaje, false);
        } catch (Exception e) {
            log("Excepcion al traer los viajero " + e.getMessage());
            lista = new ArrayList<ViajeroVO>();
        }
        return lista;
    }

    /**
     * Usado en el envio de correo para solicitar viaje
     *
     * @param idSgSolicitudViaje
     * @return
     */
    
    public List<ViajeroVO> getAllViajerosList(int idSgSolicitudViaje, boolean sinViaje) {
        log("idSolicitud " + idSgSolicitudViaje);
        List<ViajeroVO> lista = null;
        try {
            clearQuery();
            appendQuery("SELECT VJ.ID,"); //0
            appendQuery(" coalesce(VJ.SG_SOLICITUD_ESTANCIA, 0)  AS SG_SOLUCITUD_ESTANCIA, ");//1);            
            appendQuery(" VJ.ESTANCIA,"); //2
            appendQuery(" U.id AS USUARIO_ID,"); //3            
            appendQuery(" coalesce(U.nombre, null)  AS nombre, "); //4
            appendQuery(" VJ.OBSERVACION,");//5
            appendQuery(" coalesce(INV.ID, 0) AS ID_INVITADO, ");//6
            appendQuery(" INV.NOMBRE AS INVITADO_NOMBRE,"); //7
            appendQuery(" coalesce(VJ.SG_VIAJE, 0) AS idViaje, "); //8
            appendQuery(" VIA.CODIGO as codigo_viaje,");//9
            appendQuery(" EST.CODIGO as codigo_estancia, "); //10            
            appendQuery(" coalesce(U.email, inv.EMAIL, null) AS correo, ");//11            
            appendQuery(" coalesce(vj.redondo, true) AS redondo, ");//12
            appendQuery(" case when vj.sg_solicitud_estancia is null then '' else (select se.codigo from sg_solicitud_estancia se where se.id = vj.sg_solicitud_estancia ) end,");//13
            appendQuery(" vj.sg_solicitud_viaje, "); //14
            appendQuery(" sv.fecha_salida, "); //15
            appendQuery(" sv.hora_salida, "); //16
            appendQuery(" sv.fecha_regreso, "); //17
            appendQuery(" sv.hora_regreso, "); //18
            appendQuery(" case when sv.oficina_destino is not null then (select ofi.nombre from sg_oficina ofi where ofi.id = sv.oficina_destino) else "); 
            appendQuery(" (select ci.nombre from sg_viaje_ciudad vc, si_ciudad ci where vc.sg_solicitud_viaje = sv.id and vc.si_ciudad = ci.id and vc.eliminado = 'False') end,");//19
            appendQuery(" sv.codigo, "); //20
            appendQuery(" coalesce(vj.sg_viaje, 0), "); //21
            appendQuery(" coalesce(u.telefono, INV.telefono, '') as telefono, "); //22
            appendQuery("case when vj.usuario is not null then (select g.nombre from GERENCIA g where g.id=u.gerencia)  else ");//23
            appendQuery(" (select emp.NOMBRE from SG_EMPRESA emp");
            appendQuery(" inner join SG_INVITADO inv2 on inv2.SG_EMPRESA=emp.ID");
            appendQuery(" inner join SG_VIAJERO viaj on viaj.SG_INVITADO=inv2.ID");
            appendQuery(" where viaj.ID = vj.id)").append(" end, ");
            appendQuery(" coalesce(u.gerencia, 0), ");//24
            appendQuery(" coalesce(u.telefono, INV.telefono, '') as telefono ");//25
            appendQuery("  FROM SG_VIAJERO VJ LEFT OUTER JOIN USUARIO U ON VJ.USUARIO = U.ID,");
            appendQuery("  SG_VIAJERO VJ2 LEFT OUTER JOIN SG_INVITADO INV ON VJ2.SG_INVITADO = INV.ID,");
            appendQuery(" SG_VIAJERO VJ3 LEFT OUTER JOIN SG_VIAJE VIA ON VJ3.SG_VIAJE = VIA.id,");
            appendQuery(" SG_VIAJERO VJ4 LEFT OUTER JOIN SG_SOLICITUD_ESTANCIA EST ON VJ4.SG_SOLICITUD_ESTANCIA = EST.id,");
            appendQuery(" sg_solicitud_viaje sv");
            appendQuery(" WHERE VJ.SG_SOLICITUD_VIAJE =").append(idSgSolicitudViaje);

            if (sinViaje) {
                appendQuery(" AND VJ.SG_VIAJE IS NULL");
            }

            appendQuery(" AND VJ.sg_solicitud_viaje = sv.id");
            appendQuery(" AND VJ.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" AND VJ.id = VJ2.id");
            appendQuery(" AND VJ.id = VJ3.id");
            appendQuery(" AND VJ.id = VJ4.id");
            appendQuery(" order by u.GERENCIA,u.NOMBRE asc,VJ.ID asc");
            log("query: " + query.toString());

            List<Object[]> list = em.createNativeQuery(query.toString()).getResultList();
            if (list != null) {
                lista = new ArrayList<ViajeroVO>();
                for (Object[] objects : list) {
                    ViajeroVO v = new ViajeroVO();
                    v.setId((Integer) objects[0]);
                    v.setSgSolicitudEstancia((Integer) objects[1]);
                    v.setEstancia((Boolean)(objects[2]));
                    v.setIdUsuario(!(String.valueOf(objects[3]).equals("null")) ? String.valueOf(objects[3]) : "");
                    v.setEsEmpleado((!(String.valueOf(objects[3]).equals("null"))));
                    v.setUsuario(!(String.valueOf(objects[4]).equals("null")) ? String.valueOf(objects[4]) : "");
                    v.setObservacion(String.valueOf(objects[5]));
                    v.setIdInvitado((Integer) objects[6]);
                    v.setInvitado(!(String.valueOf(objects[7]).equals("null")) ? String.valueOf(objects[7]) : "");
                    v.setSgViaje((Integer) objects[8]);
                    v.setCodigoViaje((String) objects[9]);
                    v.setCodigoEstancia((String) objects[10]);
                    v.setCorreo((String) objects[11]);
                    v.setRedondo((Boolean) objects[12]);
                    v.setCodigoEstancia((String) objects[13]);
                    v.setIdSolicitudViaje((Integer) objects[14]);
                    v.setFechaSalida((Date) objects[15]);
                    v.setHoraSalida((Date) objects[16]);
                    v.setFechaRegreso((Date) objects[17]);
                    v.setHoraRegreso((Date) objects[18]);
                    v.setDestino((String) objects[19]);
                    v.setCodigoSolicitudViaje((String) objects[20]);
                    v.setIdViaje((Integer) objects[21]);
                    v.setTelefono((String) objects[22]);
                    v.setGerencia((String) objects[23]);
                    v.setIdGerencia((Integer) objects[24]);
                    v.setTelefono((String) objects[25]);
                    v.setConfirTel(!v.getTelefono().isEmpty());
                    v.setEmpleado(!(v.getIdInvitado() != null && v.getIdInvitado() > 0));
                    v.setEstanciaB(v.isEstancia());
                    v.setSavedDB(true);
                    v.setAgregado(true);
                    v.setSelected(false);
                    v.setFiltered(true);
                    v.setTipoViajero(v.getIdInvitado() == 0 ? Constantes.SG_TIPO_ESPECIFICO_EMPLEADO : Constantes.SG_TIPO_ESPECIFICO_INVITADO);
                    lista.add(v);
                }
            }
            log("Se encontraron " + (lista != null && !lista.isEmpty() ? lista.size() : 0) + "SgViajero");

            //return (lista != null ? limpiarLista(lista) : Collections.EMPTY_LIST);
        } catch (Exception e) {
            log("Excepcion al traer los viajero " + e.getMessage());
            lista = null;
        }
        return lista;
    }

    private List<ViajeroVO> limpiarLista(List<ViajeroVO> lv) {
        try {
            Map<String, Integer> lvEmpleado = new TreeMap<String, Integer>();
            Map<String, Integer> lvInv = new TreeMap<String, Integer>();
            List<ViajeroVO> lvTodo = new ArrayList<ViajeroVO>();

            for (ViajeroVO viajeroVO : lv) {
                if (viajeroVO.getIdInvitado() == 0) {
                    lvEmpleado.put(viajeroVO.getGerencia() + viajeroVO.getUsuario(), viajeroVO.getId());
                } else {
                    lvInv.put(viajeroVO.getInvitado(), viajeroVO.getId());
                }
            }
            //
            int i = 0;
            if (lvEmpleado.size() > 0) {
                for (Map.Entry<String, Integer> entry : lvEmpleado.entrySet()) {
                    int idV = entry.getValue();
                    for (ViajeroVO viajeroVO : lv) {
                        if (idV == viajeroVO.getId()) {
                            lvTodo.add(viajeroVO);
                        }
                    }
                }
            }
            if (lvInv.size() > 0) {
                for (Map.Entry<String, Integer> entry : lvInv.entrySet()) {
                    int idV = entry.getValue();
                    for (ViajeroVO viajeroVO : lv) {
                        if (idV == viajeroVO.getId()) {
                            lvTodo.add(viajeroVO);
                        }
                    }
                }
            }
            //
            return lvTodo;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    /**
     * Trae los registros de viajeros con la especificacion de ciudad destino--
     *
     * Solo se ocupa para elr proceso de viajesa ciudades
     *
     * @param idSolicitud
     * @return
     */
    
    public List<ViajeroVO> getAllViajerosCityList(Integer idSolicitud) {
        log("idSolicitud " + idSolicitud);
        try {
            Query q = em.createNativeQuery("SELECT VJ.ID,"
                    + " CASE WHEN VJ.SG_SOLICITUD_ESTANCIA is null THEN 0 "
                    + " WHEN VJ.SG_SOLICITUD_ESTANCIA is not null THEN VJ.SG_SOLICITUD_ESTANCIA"
                    + " END AS SG_SOLUCITUD_ESTANCIA,"
                    + " VJ.ESTANCIA,"
                    + " U.id AS USUARIO_ID,"
                    + " CASE WHEN U.id is null THEN null"
                    + " WHEN U.id is not null THEN U.nombre"
                    + " END AS nombre,"
                    + " VJ.OBSERVACION,"
                    + " CASE WHEN INV.ID is null THEN 0 "
                    + " WHEN INV.ID is not null THEN INV.ID "
                    + " END AS ID_INVITADO,"
                    + " INV.NOMBRE AS INVITADO_NOMBRE,"
                    + " CASE WHEN VJ.SG_VIAJE is null THEN 0"
                    + " WHEN VJ.SG_VIAJE is not null THEN VJ.SG_VIAJE "
                    + " END AS idViaje,"
                    + " VIA.CODIGO as codigo_viaje,"
                    + " EST.CODIGO as codigo_estancia,"
                    + " ciu.nombre,"
                    + " sol.codigo,"
                    + " sol.fecha_salida,"
                    + " sol.hora_salida,"
                    + " sol.fecha_regreso,"
                    + " sol.hora_regreso"
                    + " FROM SG_VIAJERO VJ LEFT OUTER JOIN USUARIO U ON VJ.USUARIO = U.ID,"
                    + " SG_VIAJERO VJ2 LEFT OUTER JOIN SG_INVITADO INV ON VJ2.SG_INVITADO = INV.ID,"
                    + " SG_VIAJERO VJ3 LEFT OUTER JOIN SG_VIAJE VIA ON VJ3.SG_VIAJE = VIA.id,"
                    + " SG_VIAJERO VJ4 LEFT OUTER JOIN SG_SOLICITUD_ESTANCIA EST ON VJ4.SG_SOLICITUD_ESTANCIA = EST.id,"
                    + " SG_VIAJE_CIUDAD des,"
                    + " SI_CIUDAD ciu,"
                    + " SG_SOLICITUD_VIAJE sol"
                    + " WHERE VJ.SG_SOLICITUD_VIAJE = " + idSolicitud
                    + " and vj.SG_SOLICITUD_VIAJE = des.sg_solicitud_Viaje"
                    + " AND des.si_ciudad = ciu.id"
                    + " AND vj.SG_SOLICITUD_VIAJE = sol.id"
                    + " AND VJ.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " AND VJ.id = VJ2.id"
                    + " AND VJ.id = VJ3.id"
                    + " AND VJ.id = VJ4.id"
                    + " order by VJ.ID");

            List<ViajeroVO> lista = new ArrayList<ViajeroVO>();
            List<Object[]> list = q.getResultList();
            for (Object[] objects : list) {
                ViajeroVO v = new ViajeroVO();
                v.setId((Integer) objects[0]);
                v.setSgSolicitudEstancia((Integer) objects[1]);
                v.setEstancia((Boolean)(objects[2]));
                v.setIdUsuario(String.valueOf(objects[3]));
                v.setUsuario(String.valueOf(objects[4]));
                v.setObservacion(String.valueOf(objects[5]));

                v.setIdInvitado((Integer) objects[6]);
                v.setInvitado(!(String.valueOf(objects[7]).equals("null")) ? String.valueOf(objects[7]) : "");
                v.setSgViaje((Integer) objects[8]);

                v.setCodigoViaje((String) objects[9]);
                v.setCodigoEstancia((String) objects[10]);
                v.setDestino((String) objects[11]);
                v.setCodigoSolicitudViaje((String) objects[12]);
                v.setFechaSalida((Date) objects[13]);
                v.setHoraSalida((Date) objects[14]);
                v.setFechaRegreso((Date) objects[15]);
                v.setHoraRegreso((Date) objects[16]);

                lista.add(v);
            }
            return lista;
        } catch (Exception e) {
            log("Excepcion al traer los viajero " + e.getMessage());
            return null;
        }
    }

    /**
     * Retorna una consulta de viajes generados de una soliicitud
     *
     * @param idSolicitud
     * @return
     */
    
    public List<ViajeVO> getAllViajesBySolicitud(Integer idSolicitud) {
        log("idSolicitud " + idSolicitud);
        try {
            Query q = em.createNativeQuery("select via.id,"//0
                    + "case when via.codigo is null then ''"
                    + " when via.codigo is not null then via.codigo"
                    + " end as codigo," ///1
                    + " via.FECHA_SALIDA,"//2
                    + " via.HORA_SALIDA,"//3
                    + " via.FECHA_REGRESO,"//4
                    + " via.HORA_REGRESO,"//5
                    + " u.NOMBRE,"//6
                    + " via.RESPONSABLE,"//7
                    + " via.VEHICULO_ASIGNADO_EMPRESA,"//8
                    + " via.VEHICULO_PROPIO,"//9
                    + " via.AUTOBUS,"//10
                    + " est.nombre,"//11
                    + " case when via.SG_ITINERARIO is null then 0"
                    + "      when via.SG_ITINERARIO is not null then via.SG_ITINERARIO"
                    + "      end as itinerario,"//12
                    + " case when via.SG_RUTA_TERRESTRE is null then 0"
                    + "      when via.SG_RUTA_TERRESTRE is not null then via.SG_RUTA_TERRESTRE"
                    + "      end as ruta, "//13
                    + " tipoEsp.nombre as Tipo_Especifico,"//14
                    + " case when via.SG_VIAJE is null then 0"
                    + "      when via.SG_VIAJE is not null then via.SG_VIAJE "
                    + "      end as sg_viaje,"//15
                    + " case when via.SI_ADJUNTO is null then 0"
                    + "      when via.SI_ADJUNTO is not null then via.SI_ADJUNTO "
                    + "      end as SI_ADJUNTO,"//16
                    + " case when via.SG_OFICINA is null then ''"
                    + "      when via.SG_OFICINA is not null then (select ofc.nombre FROM sg_oficina ofc where ofc.id=via.SG_OFICINA)"
                    + "      end as nombreOficina, "//17
                    + " via.CO_NOTICIA, "//18

                    + " CASE WHEN via.SI_ADJUNTO is null THEN ''"
                    + " WHEN via.SI_ADJUNTO is not null THEN (select ad.uuid from SI_ADJUNTO ad where ad.ID = via.si_adjunto)   "
                    + " END AS uuid_adjunto"//19

                    + " From SG_VIAJE Via,Usuario u,ESTATUS est,SG_TIPO_ESPECIFICO tipoEsp"
                    + " where via.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "' AND via.id IN (select distinct(v.sg_viaje) "
                    + "          From SG_VIAJERO v "
                    + "          where v.SG_SOLICITUD_VIAJE = " + idSolicitud + " AND v.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "') "
                    + " AND via.RESPONSABLE = u.ID "
                    + " AND via.ESTATUS = est.id"
                    + " AND via.SG_TIPO_ESPECIFICO = tipoEsp.id");

            List<ViajeVO> lista = new ArrayList<>();

            List<Object[]> list = q.getResultList();
            for (Object[] objects : list) {
                ViajeVO v = new ViajeVO();
                v.setId((Integer) objects[0]);
                v.setCodigo((String) objects[1]);
                v.setFechaSalida((Date) objects[2]);
                v.setHoraSalida((Date) objects[3]);
                v.setFechaRegreso((Date) objects[4]);
                v.setHoraRegreso((Date) objects[5]);
                v.setResponsable((String) objects[6]);
                v.setIdUsuario((String) objects[7]);
                v.setVehiculoEmpresa((Boolean) objects[8]);
                v.setVehiculoPropio((Boolean) objects[9]);
                v.setAutobus((Boolean) objects[10]);
                v.setStatus((String) objects[11]);
                v.setIdItinerario(Integer.parseInt(objects[12].toString()));
                v.setIdRuta(Integer.parseInt(objects[13].toString()));
                v.setTipo((String) objects[14]); //Tipo especifico
                v.setSgViaje(Integer.parseInt(objects[15].toString()));
                v.setIdAdjunto(Integer.parseInt(objects[16].toString()));
                v.setOficina((String) objects[17]);
                v.setIdNoticia(Integer.parseInt(objects[18].toString()));
                v.setUuid((String) objects[19]);
                lista.add(v);
            }
            //
//

            return lista;
        } catch (NumberFormatException e) {
            log("Excepcion al traer los viajero " + e.getMessage());
            return null;
        }
    }

    
    public List<SolicitudViajeVO> getAllSolicitudesByViaje(Integer idSgViaje) {
        log("idViaje: " + idSgViaje);
        try {
            Query q = em.createNativeQuery("SELECT sol.id,"//0
                    + "case when sol.codigo is null then ''"
                    + "     when sol.codigo is not null then sol.codigo"
                    + "     end as codigo,"//1
                    + " sol.FECHA_SALIDA,"//2
                    + "sol.HORA_SALIDA,"//3
                    + "sol.FECHA_REGRESO,"//4
                    + "sol.HORA_REGRESO,"//5
                    + "tipo.nombre as Tipo_Solicitud,"//6
                    + "tipoEsp.nombre as tipo_especifico,"//7
                    + "est.nombre,"//8
                    + "case when sol.oficina_Origen is null then ''"
                    + "     when sol.oficina_Origen is not null then (select ofiOrigen.nombre FROM sg_oficina ofiOrigen Where ofiOrigen.id = sol.oficina_Origen)"
                    + "     end as Oficina_Origen,"//9
                    + "case when sol.oficina_destino is null then ''"
                    + "     when sol.oficina_destino is not null then (select ofiDestino.nombre FROM sg_oficina ofiDestino Where ofiDestino.id = sol.oficina_Destino)"
                    + "     end as Oficina_Destino,"//10
                    + " ger.NOMBRE,"//11
                    + " mot.NOMBRE,"//12
                    + " sol.OBSERVACION, "//13
                    + " sol.GENERO, " //14
                    + " ger.id "//15
                    + " From SG_SOLICITUD_VIAJE sol,"
                    + "    sg_tipo_solicitud_viaje tipo, "
                    + "    Estatus est,GERENCIA ger,"
                    + "    SG_MOTIVO mot,"
                    + "    SG_TIPO_ESPECIFICO tipoEsp"
                    + " where sol.ELIMINADO = 'False' AND sol.id IN (select distinct(v.SG_SOLICITUD_VIAJE)"
                    + "                    From SG_VIAJERO v"
                    + "                    where v.SG_VIAJE = " + idSgViaje + " AND v.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "') "
                    + "                AND sol.SG_TIPO_SOLICITUD_VIAJE =  tipo.id"
                    + "                AND sol.ESTATUS = est.id"
                    + "                AND sol.GERENCIA_RESPONSABLE = ger.id"
                    + "                AND sol.SG_MOTIVO = mot.id"
                    + "                AND tipo.SG_TIPO_ESPECIFICO = tipoEsp.id");

            log("query: " + q.toString());

            List<SolicitudViajeVO> lista = new ArrayList<SolicitudViajeVO>();
            List<Object[]> list = q.getResultList();
            for (Object[] objects : list) {
                SolicitudViajeVO vo = new SolicitudViajeVO();
                vo.setIdSolicitud((Integer) objects[0]);
                vo.setCodigo((String) objects[1]);
                vo.setFechaSalida((Date) objects[2]);
                vo.setHoraSalida((Date) objects[3]);
                vo.setFechaRegreso((Date) objects[4]);
                vo.setHoraRegreso((Date) objects[5]);
                vo.setTipoSolicitud((String) objects[6]);
                vo.setTipoEspecifico((String) objects[7]);
                vo.setEstatus((String) objects[8]);
                vo.setOrigen((String) objects[9]);
                vo.setDestino((String) objects[10]);
                vo.setGerencia((String) objects[11]);
                vo.setMotivo((String) objects[12]);
                vo.setObservacion((String) objects[13]);
                vo.setGenero((String) objects[14]);
                vo.setIdGerencia((Integer) objects[15]);

                MotivoRetrasoVO mr = this.sgMotivoRetrasoRemote.findById(vo.getIdSolicitud(), vo.getIdSgTipoEspecifico());
                if (mr != null) {
                    vo.setJustificacionRetraso(mr.getJustificacion());
                    if (mr.getHoraReunion() != null) {
                        vo.setHoraReunion(mr.getHoraReunion());
                    }
                    if (mr.getLugar() != null) {
                        vo.setLugarReunion(mr.getLugar());
                    }
                }

                lista.add(vo);
            }
            return lista;
        } catch (Exception e) {
            log("Excepción al traer los viajeros " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
//

    
    public void update(Usuario usuario, List<SgViajero> listViajero, SgViaje sgViaje) {
        try {
            for (SgViajero sgViajero : listViajero) {
                sgViajero.setSgViaje(sgViaje);
                sgViajero.setModifico(usuario);
                sgViajero.setFechaModifico(new Date());
                sgViajero.setHoraModifico(new Date());
                edit(sgViajero);
                log("SgViajero update viajero");
                log("dentro de modificar 1");
            }
        } catch (Exception ex) {
            log("Exceprion: " + ex.getMessage());
            Logger.getLogger(SgViajeroImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void update(Usuario usuario, int idViajero, SgViaje sgViaje) {
        try {
            SgViajero sgViajero = find(idViajero);
            sgViajero.setSgViaje(sgViaje);
            sgViajero.setModifico(usuario);
            sgViajero.setFechaModifico(new Date());
            sgViajero.setHoraModifico(new Date());
            edit(sgViajero);
            log("SgViajero update viajero");
        } catch (Exception ex) {
            log("Excepcion: " + ex.getMessage());
        }
    }

    /**
     *
     * Modificado para pruebas la linea de registro de log
     *
     * @param solicitudEstancia
     * @param listaViajerosVO
     * @param usuario
     * @return
     */
    
    @Trace
    public boolean updateViajeroWithList(SgSolicitudEstancia solicitudEstancia, List<ViajeroVO> listaViajerosVO, Usuario usuario) {
        log("updateViajeroWithList");
        try {
            for (ViajeroVO vj : listaViajerosVO) {
                SgViajero viajero = find(vj.getId());
                if (vj.isEstancia()) {
                    viajero.setSgSolicitudEstancia(solicitudEstancia);
                    edit(viajero);
                }
            }            
        } catch (Exception e) {
            log("Exception en actualizar viajeros " + e.getMessage());
            return false;
        }
        return true;
    }

    
    public void updateViajeAndSolicitud(List<SgViajero> listaViajeros, List<SgSolicitudViaje> listaSolicitud, SgViaje sgViaje, Usuario usuario) throws SIAException {
        try {
            for (SgViajero sgViajero : listaViajeros) {
                log("antes de actualiar");
                update(usuario, sgViajero.getId(), sgViaje);
                log("Despues de actualiar");
                log("viajero: " + sgViajero.getSgViaje().getId());
            }
        } catch (Exception e) {
            throw new SIAException("Ocurrio un error");
        }
    }

    
    public List<SgViajero> getViajerosBySinViaje(int idSolicitud) {
        try {
            log("ID sol: " + idSolicitud);
            return em.createQuery("select v from SgViajero v where v.sgSolicitudViaje.id = :idSol "
                    + " and v.eliminado = :eli"
                    + " and v.sgViaje is null").setParameter("idSol", idSolicitud).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            log("Exception: " + e.getMessage());
            return null;
        }
    }

    
    public void cancelTraveller(Usuario usuario, SgViajero sgViajero, String motivo, int tamanioLista) throws SIAException {
        boolean v = false;
        boolean vieneSeguridad = false;
        String correoCopia = usuario.getEmail();
        //se debe de cambiar a campo del usuario
        Usuario gerente = getResponsableByGerencia(Constantes.AP_CAMPO_DEFAULT, sgViajero.getSgSolicitudViaje().getGerenciaResponsable().getId());
        String correoPara = gerente.getEmail();
        try {

            //envio de correo
            if (usuario.getId().equals("PRUEBA")) {
                v = notificacionViajeRemote.sendCancelTraveller(correoPara, "", usuario, sgViajero, motivo, gerente);
            } else {
                v = notificacionViajeRemote.sendCancelTraveller(correoPara, correoCopia, usuario, sgViajero, motivo, gerente);
            }
            if (v) {
                //Guarda en la tabla motivo
                SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(3), usuario);

               

                //Cancela las estancias
                if (sgViajero.isEstancia()) {
                    lougueOfViajeroCancel(sgViajero.getId(), motivo, usuario.getId());
                }
                //Guarda en relacion
                sgViajeroSiMovimientoRemote.guardarMovimiento(usuario.getId(), sgViajero.getId(), siMovimiento);

                //registro en base de datos
                sgViajero.setModifico(usuario);
                sgViajero.setFechaModifico(new Date());
                sgViajero.setHoraModifico(new Date());
                sgViajero.setSgViaje(null);
                sgViajero.setEliminado(Constantes.TRUE);
                edit(sgViajero);
                log("SgViajero cancel viajero");
            }       
        } catch (Exception e) {
            log("Ocurrió un error al cancelar al viajero + " + e);
            throw new SIAException("Ocurrió un error al cancelar al viajero");
        }
    }

    
    public void takeOutTravellToTraveller(Usuario usuario, SgViajero sgViajero, String motivo, int tamanioLista, boolean interccepcion) throws SIAException {
        try {
            boolean vieneSeguridad = false;
            boolean emergente = false;
            String correoPara = sgViajero.getSgViaje().getResponsable().getEmail();

            if (sgViajero.getSgSolicitudViaje() != null) { 
                //el timer se debe de ejcutar por campo quitar la varuiable de campo default
                correoPara += "," + getResponsableByGerencia(sgViajero.getSgSolicitudViaje().getApCampo().getId(), sgViajero.getSgSolicitudViaje().getGerenciaResponsable().getId()).getEmail();
            }

            boolean v = false;
            SiMovimiento siMovimiento = null;

            //envia correo avisando que el usuario se quito del viaje y se dejo disponible para un viaje posterior
            log("correoPara: " + correoPara + " - ResponsableViaje,ResponsableGerencia");
            log("correoCopia: " + correoCopia(usuario.getEmail(), sgViajero) + " - Analista,ResponsableSGL,ResponsableSEguridad,Viajero");
            //Guarda en la tabla motivo
            SgViaje sgViaje = sgViajero.getSgViaje();
            SgViajeroSiMovimiento sm
                    = sgViajeroSiMovimientoRemote.findByTraveller(sgViajero.getId(),
                            Constantes.QUEDADO_ORIGEN, Constantes.QUEDADO_OFICINA_DESTINO, Constantes.CERO);
            if (sm == null) {
                if (sgViajero.getSgSolicitudViaje() != null) {
                    if (sgViajero.getSgSolicitudViaje().getOficinaOrigen() == sgViajero.getSgViaje().getSgOficina() && !interccepcion) { //Esta condicion es para que el destino vea al viajero que se queda
                        siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(Constantes.QUEDADO_ORIGEN), usuario);
                    } else {
                        siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(Constantes.QUEDADO_OFICINA_DESTINO), usuario);
                    }
                } else {
                    emergente = true;
                }

            } else {
                siMovimiento = sm.getSiMovimiento();
            }
            //registro en base de datos
            if (sgViajero.getSgViaje().isVehiculoAsignadoEmpresa()) {
                if (tamanioLista == 1) {
                    //SgViaje sgViaje = sgViajero.getSgViaje();
                }
            } else {
                sgViajeRemote.cancelTrip(usuario, sgViaje, motivo, true, siMovimiento, vieneSeguridad);
            }
            log("Quitar el viaje");
            //Modifica viajero
            sgViajero.setSgViaje(null);
            sgViajero.setModifico(usuario);
            sgViajero.setFechaModifico(new Date());
            sgViajero.setHoraModifico(new Date());
            if (emergente) {
                sgViajero.setEliminado(Constantes.ELIMINADO);
            }
            edit(sgViajero);
            //Guarda en si_movimiento
            UtilLog4j.log.info(this, "Despues de guardar el log de viajero");
            if (!emergente) {
                sgViajeroSiMovimientoRemote.guardarMovimiento(usuario.getId(), sgViajero.getId(), siMovimiento);
            }

            if (sgViajero.getUsuario() != null) {
                if (!coCompartidaRemote.addCompartido(sgViaje.getCoNoticia().getId(), sgViajero.getUsuario().getId())) {
                    coCompartidaRemote.eliminarCompartir(sgViaje.getCoNoticia(), sgViajero.getUsuario().getId());
                }
            }

            //}
        } catch (Exception e) {
            log("Exc :  + + + " + e.getMessage() + " causa: " + e.getCause().getMessage());
            throw new SIAException("Ocurrio un error al quitar al viajero del viaje");
        }

    }

    public SgViajero findSgViajeroIntoSgSolicitudViaje(SgSolicitudViaje sgSolicitudViaje, Usuario usuario, SgInvitado sgInvitado, int idSgTipoEspecifico) {
        log("SgViajeroImpl.findSgViajeroIntoSgSolicitudViaje()");

        try {
            if (idSgTipoEspecifico == 19) { //Buscar Usuario
                return (SgViajero) em.createQuery("SELECT v FROM SgViajero v WHERE v.usuario.id = :idUsuario AND v.sgSolicitudViaje.id = :idSgSolicitudViaje AND v.eliminado = :eliminado ORDER BY v.id ASC").setParameter("idUsuario", usuario.getId()).setParameter("idSgSolicitudViaje", sgSolicitudViaje.getId()).setParameter("eliminado", Constantes.NO_ELIMINADO).getSingleResult();
            }

            if (idSgTipoEspecifico == 20) { //Buscar SgInvitado
                return (SgViajero) em.createQuery("SELECT v FROM SgViajero v WHERE v.sgInvitado.id = :idSgInvitado AND v.sgSolicitudViaje.id = :idSgSolicitudViaje AND v.eliminado = :eliminado ORDER BY v.id ASC").setParameter("idSgInvitado", sgInvitado.getId()).setParameter("idSgSolicitudViaje", sgSolicitudViaje.getId()).setParameter("eliminado", Constantes.NO_ELIMINADO).getSingleResult();
            }
        } catch (NoResultException nre) {
            log(nre.getMessage());
            return null;
        }
        return null;
    }

    //llamado desde solicitudes
    //viajes
    //cadena de aprobaciones
    
    public boolean lougueOfViajeroCancel(Integer idViajero, String motivoCancelacion, String idUsuario) {
        try {
            //saber si el viajero tiene estancia
            //buscar viajero
            SgViajero viajero = find(idViajero);

            if (viajero.isEstancia()) {
                if (Constantes.ESTATUS_SOLICITUD_ESTANCIA_ASIGNADA == viajero.getSgSolicitudEstancia().getEstatus().getId()) {
                    log("buscar en las asignaciones");
                    if (sgHuespedHotelService.cancelLoungueHotelOfTraveler(viajero, motivoCancelacion, usuarioRemote.find(idUsuario))) {
                        log("Cancelado en el registro de Hotel ");
                    } else {
                        sgHuespedStaffService.cancelLougueStaffOfTraveler(viajero, motivoCancelacion, usuarioRemote.find(idUsuario));
                        //aqui la regresa a la solicitus
                        //Propuesta: solo hacer u metodo que solo cancele como en Hotel
                        log("Cancelado en el registro de staff ");
                    }
                } else {
                    //volver a buscar en hotel y stafff
                    log("volver a buscar en las asignaciones");
                    if (sgHuespedHotelService.cancelLoungueHotelOfTraveler(viajero, motivoCancelacion, usuarioRemote.find(idUsuario))) {
                        log("Cancelado en el registro de Hotel ");
                    } else {
                        if (sgHuespedStaffService.cancelLougueStaffOfTraveler(viajero, motivoCancelacion, usuarioRemote.find(idUsuario))) {
                            //aqui la regresa a la solicitus
                            //Propuesta: solo hacer u metodo que solo cancele como en Hotel
                            log("Cancelado en el registro de staff ");
                        } else {
                            log("esta en las solicitudes");
                            int c = 0;
                            List<DetalleEstanciaVO> lista = sgDetalleSolicitudEstanciaService.traerDetallePorSolicitud(viajero.getSgSolicitudEstancia().getId(), Constantes.NO_ELIMINADO);
                            if (lista != null && !lista.isEmpty()) {
                                for (DetalleEstanciaVO sgDetSol : lista) {
                                    if (!sgDetSol.isRegistrado()) {
                                        c++;
                                    }
                                }
                                if (c == 1) {
                                    //es el unico
                                    log("es el unico se cancelara todo");
                                    //La CANCELACIÓN DE LA Solicitud de Estancai está en este método: SgDetalleSolicitudEstanciaImpl.vefiricarCancelacionSolicitudEstancia()
                                    //el cual se manda a llamar en algún momento dentro del método siguiente:
                                    sgDetalleSolicitudEstanciaService.cancelLoungeViajeroOfRequest(viajero, motivoCancelacion, usuarioRemote.find(idUsuario));
                                    sgSolicitudEstanciaRemote.cancelarSolicitudEstancia(usuarioRemote.find(idUsuario), sgSolicitudEstanciaRemote.buscarEstanciaPorId(viajero.getSgSolicitudEstancia().getId().intValue()),
                                            motivoCancelacion, Constantes.FALSE,Constantes.TRUE);
                                } else {
                                    log("NO es el unico solo cancelarlo  a el");
                                    //cancelar soliicitud..
                                    sgDetalleSolicitudEstanciaService.cancelLoungeViajeroOfRequest(viajero, motivoCancelacion, usuarioRemote.find(idUsuario));
                                }
                            }

                        }

                    }

                }
            }
            return true;
        } catch (Exception e) {
            log("Excepcion al momento de cancelar la estancia " + e.getMessage());
            return false;
        }
    }

    
    public boolean existSgViajeroByUsuarioAndSgSolicitudViaje(Usuario usuario, SgSolicitudViaje sgSolicitudViaje) {
        log("SgViajeroImpl.existSgViajeroByUsuarioAndSgSolicitudViaje()");

        SgViajero sgViajero;

        try {
            sgViajero = (SgViajero) em.createQuery("SELECT v FROM SgViajero v WHERE v.eliminado = :eliminado AND v.usuario.id = :idUsuario AND v.sgSolicitudViaje.id = :idSgSolicitudViaje").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idUsuario", usuario.getId()).setParameter("idSgSolicitudViaje", sgSolicitudViaje.getId()).getSingleResult();
        } catch (NoResultException nre) {
            return false;
        }

        return (sgViajero != null ? true : false);
    }

    
    public boolean existSgViajeroBySgInvitadoAndSgSolicitudViaje(int idInvitado, int idSolicitudViaje) {
        log("SgViajeroImpl.existSgViajeroBySgInvitadoAndSgSolicitudViaje()");

//        SgViajero sgViajero;
        try {
            clearQuery();
            appendQuery("select * from sg_viajero inv where inv.sg_invitado = ").append(idInvitado);
            appendQuery(" and inv.sg_solicitud_viaje = ").append(idSolicitudViaje);

            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

            return (obj != null ? true : false);
        } catch (NoResultException nre) {
            return false;
        }

    }

    /**
     * Modifico: NLopez 07/11/2013 Traer correos de responsable y seguridad
     *
     * @param correoAnalista
     * @param sgViajero
     * @return
     */
    private String correoCopia(String correoAnalista, SgViajero sgViajero) {
        StringBuilder correoPara = new StringBuilder();
        correoPara.append(correoAnalista);
        List<UsuarioVO> lu = usuarioRemote.getUsuariosByRol(Constantes.SGL_RESPONSABLE);
        lu.addAll(usuarioRemote.getUsuariosByRol(Constantes.SGL_SEGURIDAD));
        for (UsuarioVO usuario1 : lu) {
            correoPara.append(",");
            correoPara.append(usuario1.getMail());
        }

        if (sgViajero.getUsuario() != null) {
            correoPara.append(",").append(sgViajero.getUsuario().getEmail());
        }
        return correoPara.toString();
    }

    /**
     * Modifico: NLopez Traer correos de responsable y seguridad
     *
     * @return
     */
    
    public String traerCorreoResponsableSGLySeguridad() {

        StringBuilder correoPara = new StringBuilder();

        List<UsuarioVO> lu = usuarioRemote.getUsuariosByRol(Constantes.SGL_RESPONSABLE);
        lu.addAll(usuarioRemote.getUsuariosByRol(Constantes.SGL_SEGURIDAD));
        int nlist = lu.size();
        int x = 1;

        for (UsuarioVO usuario1 : lu) {
            correoPara.append(usuario1.getMail());
            if (x < nlist) {
                correoPara.append(", ");
            }
            x++;
        }

        return correoPara.toString();
    }

    /**
     * Metodo que concatena las direcciones de correos de SOLO los empleados de
     * isha, es decir; solo usuarios no invitados.
     *
     * @param idSolicitud
     * @return
     */
    
    public String correosViajerosPorSolicitud(int idSolicitud) {
        String correoCopiaViajeros = "";
        try {
            for (ViajeroVO vo : getAllViajerosList(idSolicitud)) {
                if (vo.getIdInvitado() == 0) {
                    //copiar usuario
                    if (correoCopiaViajeros.equals("")) {
                        correoCopiaViajeros = find(vo.getId()).getUsuario().getEmail();
                    } else {
                        correoCopiaViajeros += "," + find(vo.getId()).getUsuario().getEmail();
                    }
                }
            }
            log("Correo de viajeros " + correoCopiaViajeros);
            return correoCopiaViajeros;
        } catch (Exception e) {
            log("Excepcion al recoger los correos de los viajeros de la solicitud " + idSolicitud);
            return "";
        }
    }

    // <editor-fold defaultstate="collapsed" desc=" dejaUsuarioOficinaDestinoViajeSencillo ">
/*
     * MLUIS
     *
     * 08/11/2013
     */
// </editor-fold>
    
    public void dejaUsuarioOficinaDestinoViajeSencillo(int idViaje, String idSesion) {
        try {
            List<ViajeroVO> lv = getTravellersByTravel(idViaje, null);
            for (ViajeroVO viajero : lv) {
                SgViajero vjro = find(viajero.getId());
                if (vjro.isRedondo()) {
                    SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento("Viajero con regreso agregado a un viaje sencillo ",
                            siOperacionRemote.find(Constantes.QUEDADO_OFICINA_DESTINO), usuarioRemote.find(idSesion));
                    SgViajero vr = new SgViajero();
                    vr.setEstancia(vjro.isEstancia());
                    vr.setObservacion(vjro.getObservacion());
                    vr.setSgInvitado(vjro.getSgInvitado() != null ? vjro.getSgInvitado() : null);
                    vr.setSgSolicitudEstancia(vjro.getSgSolicitudEstancia() != null ? vjro.getSgSolicitudEstancia() : null);
                    vr.setSgSolicitudViaje(vjro.getSgSolicitudViaje());
                    vr.setUsuario(vjro.getUsuario() != null ? vjro.getUsuario() : null);
                    vr.setSgViaje(null);
                    vr.setRedondo(Constantes.BOOLEAN_FALSE);
                    vr.setEliminado(Constantes.NO_ELIMINADO);
                    vr.setGrAut(Constantes.NO_ELIMINADO);
                    vr.setGenero(new Usuario(idSesion));
                    vr.setFechaGenero(new Date());
                    vr.setHoraGenero(new Date());
                    create(vr);
                    //Guarda en si_movimiento
                    sgViajeroSiMovimientoRemote.guardarMovimiento(idSesion, vr.getId(), siMovimiento);
                }
            }

        } catch (Exception ex) {
            log("ex: " + ex.getMessage());
        }
    }

    
    public void dejaUsuarioOficinaDestinoViajeSencillo(List<ViajeroVO> lv, String idSesion) {
        try {

            for (ViajeroVO vjro : lv) {
                SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento("Viajero con regreso agregado a un viaje sencillo ",
                        siOperacionRemote.find(Constantes.QUEDADO_OFICINA_DESTINO), usuarioRemote.find(idSesion));
                SgViajero vr = new SgViajero();
                vr.setEstancia(vjro.isEstancia());
                vr.setObservacion(vjro.getObservacion());
                vr.setSgInvitado(vjro.getUsuario().equals("null") ? sgInvitadoRemote.find(vjro.getIdInvitado()) : null);
                vr.setSgSolicitudEstancia(vjro.getSgSolicitudEstancia() != 0 ? sgSolicitudEstanciaRemote.find(vjro.getSgSolicitudEstancia()) : null);
                vr.setSgSolicitudViaje(sgSolicitudViajeRemote.find(vjro.getIdSolicitudViaje()));
                vr.setUsuario(vjro.getInvitado().equals("null") ? new Usuario(vjro.getIdUsuario()) : null);
                vr.setSgViaje(null);
                vr.setGenero(new Usuario(idSesion));
                vr.setFechaGenero(new Date());
                vr.setHoraGenero(new Date());
                vr.setRedondo(Constantes.BOOLEAN_FALSE);
                vr.setEliminado(Constantes.NO_ELIMINADO);
                vr.setGrAut(Constantes.NO_ELIMINADO);
                create(vr);
                //Guarda en si_movimiento
                sgViajeroSiMovimientoRemote.guardarMovimiento(idSesion, vr.getId(), siMovimiento);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }

    }

    /**
     * Se selecccionan los viajeros apartir de viajes que estan en estatus 510.
     * El criterio de seleccion es : se seleccionan todos los viajes en estatus
     * 510 que
     *
     * @param idRuta
     * @param dias
     * @return
     */
    
    public List<ViajeroVO> obtenerListaViajerosSolicitudSinAsignarAViajePorRuta(int idRuta, int dias) {
        List<ViajeroVO> listaViajeros = null;
        try {
            clearQuery();
            appendQuery(" Select  vi.id,");//0
            appendQuery(" case when vi.SG_SOLICITUD_ESTANCIA is null then 0");
            appendQuery(" else vi.sg_solicitud_estancia end,");//1
            appendQuery(" vi.SG_SOLICITUD_VIAJE,");//2
            appendQuery(" case when vi.SG_VIAJE is null then 0");
            appendQuery(" else vi.SG_VIAJE  end as sg_viaje,");//3
            appendQuery(" case when vi.SG_INVITADO is null then 0");
            appendQuery(" else vi.SG_INVITADO  end,");//4
            appendQuery(" case when vi.USUARIO is null then 0");
            appendQuery(" else vi.USUARIO end,");//5
            appendQuery(" vi.ESTANCIA");//6
            appendQuery(" from SG_VIAJERO vi,SG_SOLICITUD_VIAJE sol");

            appendQuery("  where vi.SG_SOLICITUD_VIAJE in (");
            appendQuery("                      select distinct(v.SG_SOLICITUD_VIAJE)");
            appendQuery("                      from sg_viajero v ");
            appendQuery("                      where v.SG_VIAJE in (select via.id");
            appendQuery("                                             from SG_VIAJE via ");
            appendQuery("                                             where via.ESTATUS = 510");
            appendQuery("                                                     and via.SG_RUTA_TERRESTRE =");
            appendQuery(idRuta);
            appendQuery("                                                     and via.ELIMINADO = 'False'");
            appendQuery("                                                     and (via.FECHA_PROGRAMADA = cast('now' as date)");
            appendQuery("                                                         and via.HORA_PROGRAMADA > cast('now' as time))");
            appendQuery("                                                     or (via.FECHA_PROGRAMADA = (SELECT CURRENT_DATE - ").append(dias).append("))");
            appendQuery("                                                 )");
            appendQuery("                      and v.ELIMINADO = 'False') ");

            /*
             * appendQuery(" where vi.SG_SOLICITUD_VIAJE in ("); appendQuery("
             * select distinct(v.SG_SOLICITUD_VIAJE)"); appendQuery(" from
             * sg_viajero v "); appendQuery(" where v.SG_VIAJE in (select
             * via.id"); appendQuery(" from SG_VIAJE via "); appendQuery(" where
             * via.ESTATUS = 510"); appendQuery(" and via.SG_RUTA_TERRESTRE =");
             * appendQuery(idRuta); appendQuery(" and via.ELIMINADO =
             * 'False')"); appendQuery(" and v.ELIMINADO = 'False')");
             *
             * if (dias > 0) { appendQuery(" and sol.fecha_salida between
             * cast('now' as date) and (SELECT CURRENT_DATE - ").append(dias).append("
             * )"); }
             */
            //appendQuery(" and vi.ESTANCIA = 'True' ");//--que requiera estancia
            appendQuery(" and vi.SG_VIAJE is null ");//--no asignado a un viaje
            appendQuery(" and sol.ESTATUS not in (460,400)");
            appendQuery(" and vi.SG_SOLICITUD_VIAJE = sol.ID");
            UtilLog4j.log.info(this, "QUERY  : : : : : : : : : " + getStringQuery());
            List<Object[]> lista = em.createNativeQuery(getStringQuery()).getResultList();
            if (lista != null && !lista.isEmpty()) {
                listaViajeros = new ArrayList<ViajeroVO>();
                for (Object[] o : lista) {
                    ViajeroVO viajero = new ViajeroVO();
                    viajero.setId((Integer) o[0]);
                    viajero.setSgSolicitudEstancia((Integer) o[1]);
                    viajero.setIdSolicitudViaje((Integer) o[2]);
                    viajero.setSgViaje((Integer) o[3]);
                    viajero.setIdInvitado((Integer) o[4]);
                    viajero.setIdUsuario((String) o[5]);
                    viajero.setEstancia((Boolean) o[6]);
                    listaViajeros.add(viajero);
                }
            }
            return listaViajeros;
        } catch (Exception e) {
            log("Excepcion al obtener lista de solicitudes sin asignar a un viaje" + e.getMessage());
            return null;
        }
    }

    private void log(String mensaje) {
        UtilLog4j.log.info(this, mensaje);
    }

    
    public List<ViajeroVO> traerSolicitudCambioItinerario(String idSesion, int idTipoSolicitud) {
        try {
            clearQuery();
            appendQuery("select vj.ID, u.EMAIL, vj.REDONDO, sv.id, case when vj.SG_VIAJE is null then 0 else vj.SG_VIAJE end");
            appendQuery(" from SG_VIAJERO vj, SG_SOLICITUD_VIAJE sv, usuario u");
            appendQuery(" where vj.sg_solicitud_viaje = sv.id and sv.ESTATUS between 415 and 450");
            appendQuery(" and vj.USUARIO = '").append(idSesion).append("'");
            appendQuery(" and vj.USUARIO = u.id");
            appendQuery(" and vj.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" and sv.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" and sv.SG_TIPO_ESPECIFICO = ").append(idTipoSolicitud);
            appendQuery(" order by sv.codigo asc ");
            UtilLog4j.log.info(this, "Q: viajeros  " + query.toString());
            List<Object[]> obj = em.createNativeQuery(query.toString()).getResultList();
            //
            List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
            ViajeroVO vj;
            for (Object[] objs : obj) {
                vj = new ViajeroVO();
                vj.setId((Integer) objs[0]);
                vj.setCorreo((String) objs[1]);
                vj.setRedondo((Boolean) objs[2]);
                vj.setIdSolicitudViaje((Integer) objs[3]);
                vj.setIdViaje((Integer) objs[4]);
                vj.setSolicitudViajeVO(sgSolicitudViajeRemote.buscarPorId(vj.getIdSolicitudViaje(), Constantes.NO_ELIMINADO, Constantes.CERO));
                if (vj.getIdViaje() > 0) {
                    vj.setViajeVO(sgViajeRemote.buscarPorId(vj.getId(), false));
                }

                lv.add(vj);
            }
            //
            return lv;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "excepcion al traer solicitudes para cambio itinerario  + + " + e.getMessage());
            return null;
        }
    }

    
    public List<ViajeroVO> traerSolicitudParaCambioItinerario(String idSesion, int idTipoSolicitud, int idEstatus) {
        try {
            clearQuery();
            appendQuery("select ea.id, u.EMAIL, sv.id, sv.redondo");
            appendQuery(" from  SG_SOLICITUD_VIAJE sv, usuario u, SG_ESTATUS_APROBACION ea ");
            appendQuery(" where ea.SG_SOLICITUD_VIAJE = sv.ID");
            appendQuery(" and ea.USUARIO = '").append(idSesion).append("'");
            appendQuery(" and ea.estatus = ").append(idEstatus);
            appendQuery(" and ea.USUARIO = u.id");
            appendQuery(" and sv.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
            appendQuery(" and sv.SG_TIPO_ESPECIFICO = ").append(idTipoSolicitud);
            appendQuery(" and sv.ID in (select it.SG_SOLICITUD_VIAJE from SG_ITINERARIO it ");
            appendQuery(" where it.ID in (select cit.SG_ITINERARIO from SG_CAMBIO_ITINERARIO cit  where cit.historial = 'False'))");
            appendQuery(" order by sv.codigo asc ");
            UtilLog4j.log.info(this, "Q: viajeros  que pidieron cambio itinerario " + query.toString());
            List<Object[]> obj = em.createNativeQuery(query.toString()).getResultList();
            //
            List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
            ViajeroVO vj;
            for (Object[] objs : obj) {
                vj = new ViajeroVO();
                vj.setId((Integer) objs[0]);
                vj.setCorreo((String) objs[1]);
                vj.setIdSolicitudViaje((Integer) objs[2]);
                vj.setRedondo((Boolean) objs[3]);
                vj.setIdViaje(0);
                vj.setSolicitudViajeVO(sgSolicitudViajeRemote.buscarPorId(vj.getIdSolicitudViaje(), Constantes.NO_ELIMINADO, Constantes.CERO));
                //Si la solicitud tiene estatus 460 -- terminada
                if (vj.getSolicitudViajeVO().getIdEstatus() < Constantes.ESTATUS_TERMINADA) {
                    lv.add(vj);
                } else if (vj.getSolicitudViajeVO().getIdEstatus() == Constantes.ESTATUS_TERMINADA) {
                    List<ViajeroVO> lvjro = getAllViajerosList(vj.getIdSolicitudViaje());
                    ViajeroVO vjroVO = lvjro.get(0);
                    ViajeVO vje = sgViajeRemote.buscarPorId(vjroVO.getIdViaje(), false);
                    if (vje.getIdEstatus() == Constantes.ESTATUS_VIAJE_POR_SALIR) {
                        vj.setViajeVO(vje);
                        lv.add(vj);
                    }
                }
            }
            //
            return lv;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "excepcion al traer solicitudes para cambio itinerario  + + " + e.getMessage());
            return null;
        }
    }

    ///////////////////////////REPORTE DE VIAJES /////////////////////
    
    public List<ViajeroVO> viajerosPorFecha(String inicio, String fin, int status, boolean traerViajerosACiudad) {
        clearQuery();
        try {

            UtilLog4j.log.info(this, "inicio : " + inicio);
            UtilLog4j.log.info(this, "fin: " + fin);
            query.append("select  viaje.codigo, u.NOMBRE, viaje.FECHA_SALIDA, viaje.hora_SALIDA, viaje.FECHA_REGRESO as Fecha_regreso, viaje.HORA_REGRESO as Hora_regreso, oo.NOMBRE as ORIGEN,");
            query.append("(select od.nombre from SG_OFICINA od where od.ID = (select drt.SG_OFICINA from SG_DETALLE_RUTA_TERRESTRE drt ");
            query.append(" where drt.SG_RUTA = viaje.SG_RUTA_TERRESTRE and drt.DESTINO = 'True'");
            query.append(" and drt.ELIMINADO = 'False')) as DESTINO");
            //
            query.append(" from SG_VIAJERO via  ");
            query.append(" join SG_VIAJE viaje on viaje.id = via.SG_VIAJE  and viaje.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            query.append(" and viaje.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and viaje.ESTATUS = ").append(status);
            query.append(" and viaje.SG_RUTA_TERRESTRE is not null");
            query.append(" join SG_SOLICITUD_VIAJE sv on via.SG_SOLICITUD_VIAJE = sv.ID and sv.OFICINA_DESTINO is not null ");
            query.append(" join USUARIO u on u.ID = via.USUARIO ");
            query.append(" join SG_OFICINA oo on viaje.sg_oficina = oo.ID");
            //query.append(" join SG_OFICINA oo on viaje.sg_oficina = oo.ID");
            query.append(" join SG_OFICINA od on sv.OFICINA_DESTINO = od.ID ");
            query.append(" order by  viaje.fecha_salida, viaje.HORA_SALIDA, u.nombre asc");
            //  UtilLog4j.log.info(this,"Repo: " + query.toString());
            List<Object[]> lo = new ArrayList<Object[]>();
            lo.addAll(em.createNativeQuery(query.toString()).getResultList());
            if (traerViajerosACiudad) {
                lo.addAll(viajerosACiudad(inicio, fin, status));
            }

            List<ViajeroVO> lv = null;
            if (lo != null) {
                lv = new ArrayList<ViajeroVO>();
                for (Object[] objects : lo) {
                    lv.add(castReporteViajero(objects));
                }
            }
            return lv;
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los viajes a oficina " + e.getMessage());
        }
        return null;
    }

    private List<Object[]> viajerosACiudad(String inicio, String fin, int status) {
        clearQuery();
        try {
            UtilLog4j.log.info(this, "inicio : " + inicio);
            UtilLog4j.log.info(this, "fin: " + fin);
            query.append("select  viaje.codigo, u.NOMBRE, viaje.FECHA_SALIDA, viaje.hora_SALIDA, viaje.FECHA_REGRESO as Fecha_regreso, viaje.HORA_REGRESO as Hora_regreso, oo.NOMBRE as ORIGEN,");
            query.append(" (select ci.nombre from si_ciudad ci where ci.ID = (select drc.SI_CIUDAD from SG_DETALLE_RUTA_CIUDAD drc ");
            query.append(" where drc.SG_RUTA_TERRESTRE = viaje.SG_RUTA_TERRESTRE and drc.DESTINO = 'True'");
            query.append(" and drc.ELIMINADO = 'False')) as DESTINO");
            //
            query.append(" from SG_VIAJERO via  ");
            query.append(" join SG_VIAJE viaje on viaje.id = via.SG_VIAJE  and viaje.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            query.append(" and viaje.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and viaje.ESTATUS = ").append(status);
            query.append(" and viaje.SG_RUTA_TERRESTRE is not null");
            query.append(" join USUARIO u on u.ID = via.USUARIO ");
            query.append(" join SG_SOLICITUD_VIAJE sv on via.SG_SOLICITUD_VIAJE = sv.ID and sv.OFICINA_DESTINO is null ");
            query.append(" join SG_OFICINA oo on viaje.sg_oficina = oo.ID");
            query.append(" order by  u.nombre asc");
            //  UtilLog4j.log.info(this,"Repo: " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();

            //List<ViajeroVO> lv = null;
            if (lo != null) {
//            lv = new ArrayList<ViajeroVO>();
//            for (Object[] objects : lo) {
//                lv.add(castReporteViajero(objects));
//            }
                return lo;
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los viajes a ciudad " + e.getMessage());
        }
        return null;
    }

    private ViajeroVO castReporteViajero(Object[] object) {
        ViajeroVO v = new ViajeroVO();
        v.setCodigoViaje((String) object[0]);
        v.setUsuario((String) object[1]);
        v.setFechaSalida((Date) object[2]);
        v.setHoraSalida((Date) object[3]);
        v.setFechaRegreso((Date) object[4]);
        v.setHoraRegreso((Date) object[5]);
        v.setOrigen((String) object[6]);
        v.setDestino((String) object[7]);

        return v;
    }

    
    public List<ViajeroVO> viajerosAreosPorFecha(String inicio, String fin, int status) {
        clearQuery();
        try {

            UtilLog4j.log.info(this, "inicio : " + inicio);
            UtilLog4j.log.info(this, "fin: " + fin);
            query.append("select  viaje.codigo, u.NOMBRE, viaje.FECHA_SALIDA, viaje.hora_SALIDA, viaje.FECHA_REGRESO as Fecha_regreso, viaje.HORA_REGRESO as Hora_regreso,");
            query.append(" (select ci.nombre from SI_CIUDAD ci where ci.ID = it.SI_CIUDAD_ORIGEN) as Origen ,");
            query.append(" (select ci.nombre from SI_CIUDAD ci where ci.ID = it.SI_CIUDAD_DESTINO )as DESTINO");
            //
            query.append(" from SG_VIAJERO via  ");
            query.append(" join SG_VIAJE viaje on viaje.id = via.SG_VIAJE  and viaje.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
            query.append(" and viaje.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' and viaje.ESTATUS = ").append(status);
            query.append(" and viaje.SG_RUTA_TERRESTRE is null");
            query.append(" join USUARIO u on u.ID = via.USUARIO ");
            query.append(" join SG_SOLICITUD_VIAJE sv on via.SG_SOLICITUD_VIAJE = sv.ID and sv.OFICINA_DESTINO is null ");
            query.append(" join SG_ITINERARIO it on viaje.SG_ITINERARIO = it.ID and it.eliminado = 'False'");
            query.append(" join SG_OFICINA oo on viaje.sg_oficina = oo.ID");
            query.append(" order by  viaje.FECHA_SALIDA, viaje.HORA_SALIDA, u.nombre asc");
            // UtilLog4j.log.info(this,"Repo: " + query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();

            List<ViajeroVO> lv = null;
            if (lo != null) {
                lv = new ArrayList<ViajeroVO>();
                for (Object[] objects : lo) {
                    lv.add(castReporteViajero(objects));
                }
                return lv;
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar los viajes aereos " + e.getMessage());
        }
        return null;
    }

    
    public boolean agregarViajeroAViaje(String sesion, int viaje, String usuario, int tipoViajero, int invitado, String mailSesion, String motivo, int operacion) {
        //Crear la solicitud
        boolean v;
        SgViajero sgViajero = new SgViajero();
        SgViaje sgViaje = sgViajeRemote.find(viaje);
        //
        if (tipoViajero == Constantes.SG_TIPO_ESPECIFICO_EMPLEADO) {
            sgViajero.setUsuario(new Usuario(usuario));
        } else {
            sgViajero.setSgInvitado(sgInvitadoRemote.find(invitado));
        }
        sgViajero.setSgViaje(sgViaje);
        sgViajero.setRedondo(Constantes.BOOLEAN_FALSE);
        //
        sgViajero.setGenero(new Usuario(sesion));
        sgViajero.setFechaGenero(new Date());
        sgViajero.setHoraGenero(new Date());
        sgViajero.setEliminado(Constantes.NO_ELIMINADO);
        sgViajero.setGrAut(Constantes.NO_ELIMINADO);
        sgViajero.setEstancia(Constantes.BOOLEAN_FALSE);
        //
        create(sgViajero);
        v = true;
        //
        sgViajeroSiMovimientoRemote.guardaMovimiento(sesion, sgViajero.getId(), motivo, operacion);
        if (tipoViajero == Constantes.SG_TIPO_ESPECIFICO_EMPLEADO
                && sgViaje != null
                && sgViaje.getCoNoticia() != null
                && sgViaje.getCoNoticia().getId() > 0
                && coCompartidaRemote.addCompartido(sgViaje.getCoNoticia().getId(), usuario)) {
            coCompartidaRemote.compartir(sgViaje.getCoNoticia(), usuarioRemote.find(usuario), usuarioRemote.find(sesion));
        }
        return v;
    }

    
    public boolean agregarViajeroAViaje(String sesion, int viaje, ViajeroVO viajero, int tipoViajero, int invitado, String mailSesion, String motivo, int operacion) {
        //Crear la solicitud
        boolean v;
        SgViajero sgViajero = new SgViajero();
        SgViaje sgViaje = sgViajeRemote.find(viaje);
        //
        if (tipoViajero == Constantes.SG_TIPO_ESPECIFICO_EMPLEADO) {
            sgViajero.setUsuario(new Usuario(viajero.getIdUsuario()));
        } else {
            sgViajero.setSgInvitado(sgInvitadoRemote.find(invitado));
        }
        sgViajero.setSgViaje(sgViaje);
        sgViajero.setRedondo(viajero.isRedondo());
        //
        sgViajero.setGenero(new Usuario(sesion));
        sgViajero.setFechaGenero(new Date());
        sgViajero.setHoraGenero(new Date());
        sgViajero.setEliminado(Constantes.NO_ELIMINADO);
        sgViajero.setGrAut(Constantes.NO_ELIMINADO);
        sgViajero.setEstancia(Constantes.BOOLEAN_FALSE);
        //
        if (viajero.getIdSolicitudViaje() > 0) {
            sgViajero.setSgSolicitudViaje(sgSolicitudViajeRemote.find(viajero.getIdSolicitudViaje()));
        }

        if (viajero.getSgSolicitudEstancia() > 0) {
            sgViajero.setSgSolicitudEstancia(sgSolicitudEstanciaRemote.find(viajero.getSgSolicitudEstancia()));
            sgViajero.setEstancia(Constantes.BOOLEAN_TRUE);
        }

        create(sgViajero);
        v = true;
        //
        sgViajeroSiMovimientoRemote.guardaMovimiento(sesion, sgViajero.getId(), motivo, operacion);
        if (tipoViajero == Constantes.SG_TIPO_ESPECIFICO_EMPLEADO
                && sgViaje != null
                && sgViaje.getCoNoticia() != null
                && sgViaje.getCoNoticia().getId() > 0
                && coCompartidaRemote.addCompartido(sgViaje.getCoNoticia().getId(), viajero.getIdUsuario())) {
            coCompartidaRemote.compartir(sgViaje.getCoNoticia(), usuarioRemote.find(viajero.getIdUsuario()), usuarioRemote.find(sesion));
        }
        return v;
    }

    
    public ViajeroVO buscarViajeroPorId(int idViajero) {
        clearQuery();
        query.append("SELECT v.ID, u.id, i.id, s.codigo, s.fecha_salida, s.hora_salida,s.fecha_regreso, "
                //             0    1     2       3           4                 5           6
                + "  s.hora_regreso, v.estancia, v.redondo, u.TELEFONO ,u.NOMBRE, i.NOMBRE, u.EMAIL, vj.ID, vj.CODIGO, case when v.SG_VIAJERO > 0 and v.ELIMINADO = 'True' then " + Constantes.VIAJERO_ESCALA + " else m.SI_OPERACION end, s.id, s.SG_RUTA_TERRESTRE, s.REDONDO  "
                //         7            8             9          10          11       12       13     14         15                                                16                                                                       17          18                19
                + " , case  "
                + " when m.SI_OPERACION > 0 then  "
                + " 		case  "
                + " 			when m.SI_OPERACION = 5 then  "
                + " 				r.SG_OFICINA  "
                + " 			else "
                + " 				case  "
                + " 					when m.SI_OPERACION = 6 then  "
                + " 						drt.SG_OFICINA "
                + " 					else  "
                + " 					r.SG_OFICINA	 "
                + " 				end "
                + " 	      end				  "
                + " 	else r.SG_OFICINA "
                + " end as origen,  "// 20
                + " case  "
                + " 	when m.SI_OPERACION > 0 then  "
                + " 		case  "
                + " 			when m.SI_OPERACION = 5 then  "
                + " 				drt.SG_OFICINA  "
                + " 			else "
                + " 				case  "
                + " 					when m.SI_OPERACION = 6 then  "
                + " 						r.SG_OFICINA "
                + " 					else  "
                + " 					drt.SG_OFICINA	 "
                + " 				end "
                + " 	      end				  "
                + " 	else drt.SG_OFICINA "
                + " end as destino, v.GRAUTOMOTIVO " //21       22
                + "  FROM SG_VIAJERO v"
                + "      left join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID and s.ELIMINADO = 'False' "
                + "      left join USUARIO u on v.USUARIO = u.ID and u.ELIMINADO = 'False' "
                + "      left join SG_INVITADO i on v.SG_INVITADO = i.ID and i.ELIMINADO = 'False' "
                + "      left join SG_VIAJE vj on v.SG_VIAJE = vj.ID and vj.ELIMINADO = 'False' "
                + "      left join SG_VIAJERO_SI_MOVIMIENTO vjm on vjm.SG_VIAJERO = v.ID and vjm.ELIMINADO = 'False' "
                + "      left join SI_MOVIMIENTO m on vjm.SI_MOVIMIENTO = m.ID and m.SI_OPERACION in (").append(Constantes.ID_SI_OPERACION_OFICINA_ORIGEN).append(", ").append(Constantes.ID_SI_OPERACION_OFICINA_DESTINO).append(")  and m.ELIMINADO = 'False' "
                + "      left join SG_RUTA_TERRESTRE r on r.id = s.SG_RUTA_TERRESTRE and r.ELIMINADO = 'False'       "
                + "      left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = r.id and drt.ELIMINADO = 'False'       "
                + "  WHERE v.ID = ").append(idViajero).append(
                "  AND (v.eliminado = '").append(Constantes.NO_ELIMINADO).append("' or (v.SG_VIAJERO is not null and v.ELIMINADO = '").append(Constantes.ELIMINADO).append("'))"
                + "  order by vjm.id desc limit 1 ");
        try {
            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

            ViajeroVO viajeroVO = castViajeroPorId(obj);
            return viajeroVO;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer el viajero, , por id : : : ::  " + e.getMessage());
            return null;
        }
    }

    private ViajeroVO castViajeroPorId(Object[] objects) {
        ViajeroVO v = new ViajeroVO();
        v.setId((Integer) objects[0]);
        if (objects[1] != null) {
            v.setUsuario((String) objects[11]);
            v.setIdUsuario((String) objects[1]);
            v.setCorreo((String) objects[13] != null ? (String) objects[13] : "");
            v.setInvitado("null");
            v.setIdInvitado(0);
            v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
            v.setEsEmpleado(Constantes.TRUE);
        } else {
            v.setIdInvitado((Integer) objects[2]);
            v.setInvitado((String) objects[12]);
            v.setUsuario("null");
            v.setIdUsuario("null");
            v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
            v.setEsEmpleado(Constantes.FALSE);
        }
        v.setCodigoSolicitudViaje((String) objects[3]);
        v.setFechaSalida((Date) objects[4]);
        v.setHoraSalida((Date) objects[5]);

        v.setEstancia((Boolean) objects[8]);
        v.setRedondo((Boolean) objects[9]);

        if (objects[19] != null && (Boolean) objects[19]) {
            v.setFechaRegreso((Date) objects[6]);
            v.setHoraRegreso((Date) objects[7]);
        }
        v.setTelefono((String) objects[10]);
        v.setIdViaje(objects[14] != null ? (Integer) objects[14] : 0);
        v.setCodigoViaje((String) objects[15]);
        v.setViajeroQuedado((Integer) objects[16] != null ? (Integer) objects[16] : Constantes.UNO); //el viajero viaja por primera vez
        v.setIdSolicitudViaje((Integer) objects[17] != null ? (Integer) objects[17] : 0);
        v.setIdRutaViaje((Integer) objects[18] != null ? (Integer) objects[18] : 0);
        v.setIdOrigen((Integer) objects[20] != null ? (Integer) objects[20] : 0);
        v.setIdDestino((Integer) objects[21] != null ? (Integer) objects[21] : 0);
        v.setGrAutorizoMotivo((String) objects[22]);
        return v;
    }

    
    public List<ViajeroVO> traerViajerosPorViajeMovimiento(int idViaje, int operacion, int oficinaDestino) {
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        try {
            clearQuery();
            query.append(consultaViajero(null));
            query.append("      left join SI_MOVIMIENTO m on vm.SI_MOVIMIENTO = m.ID ");
            query.append("  WHERE v.sg_Viaje =  ").append(idViaje);
            query.append("  and m.si_operacion = ").append(operacion);
            query.append("  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            query.append("  AND vm.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            if (Constantes.ORIGEN == oficinaDestino) {
                query.append(" and s.OFICINA_DESTINO = via.SG_OFICINA ");
            }
            query.append("  ORDER BY v.id ASC");

            List<Object[]> listObject = em.createNativeQuery(query.toString()).getResultList();

            for (Object[] objects : listObject) {
                lv.add(castViajeroPorViaje(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "e : " + e.getMessage());
        }
        return lv;
    }

    private String consultaViajero(String usrGerente) {
        StringBuilder sb = new StringBuilder(
                " SELECT distinct v.ID,  u.nombre, i.id,  s.codigo,  s.fecha_salida,  s.hora_salida,"
                //                  0       1        2       3             4            5                 
                + "  s.fecha_regreso,  s.hora_regreso, s.oficina_destino, v.estancia, via.FECHA_SALIDA, via.HORA_SALIDA, u.telefono , "
                //         6              7                8                   9                10         11             12                  
                + "  v.redondo, v.sg_solicitud_estancia , u.id, s.id, v.observacion,  via.codigo, u.telefono, i.NOMBRE, "
                //        13           14                15                16   17         18           19          20           
                + " case when u.id is null then i.EMAIL when u.id is not null then u.EMAIL end as EMAIL  "
                //                                            21        
                + ", des.nombre "
                //      22
                + "  , case when s.codigo is not null then us.NOMBRE when s.codigo is null then  uvm.NOMBRE end as creoViajero, m.si_operacion, g.nombre "
                //                                                        23                                                         24              25                 
                + " , ucg.NOMBRE, v.SG_VIAJERO, v.GRAUT "
                //      26             27          28
                + "  FROM SG_VIAJERO v"
                + "      inner join SG_VIAJE via on v.SG_VIAJE= via.ID"
                + "      left join SG_INVITADO i on v.SG_INVITADO = i.ID"
                + "      left join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID "
                + "      left join USUARIO us on us.id = s.GENERO "
                + "      left join SG_OFICINA des on s.OFICINA_DESTINO = des.ID"
                + "      left join USUARIO u on v.USUARIO = u.ID "
                + "	 left join GERENCIA g on u.GERENCIA = g.ID and g.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'")
                .append(" left join AP_CAMPO_GERENCIA cg on cg.GERENCIA = g.id and cg.AP_CAMPO = '").append(Constantes.AP_CAMPO_DEFAULT).append("'")
                .append(" and  cg.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        if (usrGerente != null && !usrGerente.isEmpty()) {
            sb.append("	 and cg.RESPONSABLE = '").append(usrGerente).append("'");
        }
        sb.append("	 left join USUARIO ucg on ucg.id = cg.RESPONSABLE and ucg.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'")
                .append("      left join SG_VIAJERO_SI_MOVIMIENTO vm on vm.SG_VIAJERO = v.ID").append("  AND vm.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        sb.append("      LEFT join USUARIO uvm on uvm.id = vm.GENERO ");
        return sb.toString();
    }

    private ViajeroVO castViajeroPorViaje(Object[] objects) {

        ViajeroVO v = new ViajeroVO();
        v.setId((Integer) objects[0]);
        if (objects[1] != null) {
            v.setUsuario((String) objects[1]);
            v.setIdUsuario((String) objects[15]);
            v.setInvitado("null");
            v.setIdInvitado(0);
            v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
            v.setEsEmpleado(Constantes.TRUE);
        } else {
            v.setIdInvitado((Integer) objects[2]);
            v.setInvitado((String) objects[20]);
            v.setUsuario("null");
            v.setIdUsuario("null");
            v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
            v.setEsEmpleado(Constantes.FALSE);
        }
        v.setCodigoSolicitudViaje(objects[3] != null ? (String) objects[3] : "");
        v.setFechaSalida((Date) objects[4]);
        v.setHoraSalida((Date) objects[5]);
        v.setFechaRegreso((Date) objects[6]);
        v.setHoraRegreso((Date) objects[7]);
        v.setIdDestino(objects[8] != null ? (Integer) objects[8] : 0);
        v.setDestino((String) objects[22]);

        v.setEstancia((boolean) objects[9]);
        v.setFechaSalidaViaje((Date) objects[10]);
        v.setHoraSalidaViaje((Date) objects[11]);
        v.setTelefono(objects[12] != null ? (String) objects[12] : "");
        v.setRedondo((boolean) objects[13]);
        v.setSgSolicitudEstancia(objects[14] != null ? (Integer) objects[14] : 0);
        v.setIdSolicitudViaje(objects[16] != null ? (Integer) objects[16] : 0);
        v.setObservacion((String) objects[17]);
        v.setCodigoViaje((String) objects[18]);
        v.setTelefono((String) objects[19]);
        v.setCorreo((String) objects[21]);
        v.setAgregado(Constantes.FALSE);
        v.setGeneroSolicitudViaje((String) objects[23]);
        v.setIdOperacion(objects[24] != null ? (Integer) objects[24] : Constantes.CERO);
        v.setGerenciaResponsable(objects[25] != null ? (String) objects[25] : "S/G");
        v.setResponsableDeGerencia(objects[26] != null ? (String) objects[26] : "S/G");
        v.setIdViajeroEscala(objects[27] != null ? (Integer) objects[27] : Constantes.CERO);
        v.setGrAutorizo((boolean) objects[28]);
        return v;
    }

    
    public List<ViajeroVO> totalViajerosAgregados(Date fechaInicio, Date fechaFin) {
        clearQuery();
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        query.append("	select o.NOMBRE, count(vj.id)  from SG_VIAJERO vj");
        query.append("		inner join SG_VIAJERO_SI_MOVIMIENTO vm on vm.SG_VIAJERO = vj.ID");
        query.append("		inner join SI_MOVIMIENTO m on vm.SI_MOVIMIENTO = m.ID");
        query.append("		inner join SI_OPERACION o on m.SI_OPERACION = o.ID");
        query.append("	where  vj.FECHA_GENERO between cast('").append(siManejoFechaLocal.convertirFechaStringyyyyMMdd(fechaInicio)).append("' as date)");
        query.append("	and cast('").append(Constantes.FMT_yyyyMMdd.format(fechaFin)).append("' as date)");
        query.append("	and vj.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("	group by o.NOMBRE order by o.nombre asc");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        //
        if (lo != null) {
            for (Object[] obj : lo) {
                ViajeroVO v = new ViajeroVO();
                v.setOperacion((String) obj[0]);
                v.setTotal((Integer) obj[1]);
                lv.add(v);
            }
        }
        return lv;
    }

    /**
     *
     * @param operacion
     * @param inicio
     * @param fin
     * @return
     */
    
    public List<ViajeroVO> traerViajerosPorOperacion(String operacion, Date inicio, Date fin) {
        clearQuery();
        query.append(consultaViajero(null));
        query.append("		inner join SI_MOVIMIENTO m on vm.SI_MOVIMIENTO = m.ID");
        query.append("		inner join SI_OPERACION o on m.SI_OPERACION = o.ID");
        query.append("	where  via.FECHA_SALIDA between cast('").append(siManejoFechaLocal.convertirFechaStringyyyyMMdd(inicio))
                .append("' as date) and ");
        query.append("	cast('").append(siManejoFechaLocal.convertirFechaStringyyyyMMdd(fin)).append("' as date)");
        query.append("	and o.NOMBRE  = '").append(operacion).append("'");
        query.append("	and v.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("	and vm.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("	order by g.NOMBRE, u.NOMBRE, i.nombre asc");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        for (Object[] lo1 : lo) {
            lv.add(castViajeroPorViaje(lo1));
        }
        return lv;
    }

    
    public void actualizarViajeros(List<ViajeroVO> lViajeros, int idSolicitudOriginal, List<SolicitudViajeVO> lSolicitudes, int gerenciaOriginal) {
        if (lViajeros != null && lSolicitudes != null) {
            if (lViajeros.size() > 1 && lSolicitudes.size() > 1) {
                SgViajero viajero = null;
                for (ViajeroVO vo : lViajeros) {
                    if (vo.isAgregado()) {
                        if (gerenciaOriginal != vo.getIdGerencia() && vo.isEmpleado()) {//s3 quita esta parte
                            SgSolicitudViaje sv = null;
                            for (SolicitudViajeVO svo : lSolicitudes) {
                                if (vo.getIdGerencia() == svo.getIdGerencia()) {
                                    vo.setIdSolicitudViaje(svo.getIdSolicitud());
                                    viajero = find(vo.getId());
                                    sv = sgSolicitudViajeRemote.find(svo.getIdSolicitud());
                                    viajero.setSgSolicitudViaje(sv);
                                    edit(viajero);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    
    public SgViajero sgViajeroByUsuarioAndSgSolicitudViaje(String usuario, int sgSolicitudViaje, boolean eliminado) {
        log("SgViajeroImpl.existSgViajeroByUsuarioAndSgSolicitudViaje()");

        SgViajero sgViajero;

        try {
            sgViajero = (SgViajero) em.createQuery("SELECT v FROM SgViajero v WHERE v.usuario.id = :idUsuario AND v.sgSolicitudViaje.id = :idSgSolicitudViaje AND v.eliminado = :eliminado")
                    .setParameter("idUsuario", usuario).setParameter("idSgSolicitudViaje", sgSolicitudViaje).setParameter("eliminado", eliminado).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

        return sgViajero;
    }

    
    public SgViajero sgViajeroBySgInvitadoAndSgSolicitudViaje(int idInvitado, int idSolicitudViaje, boolean eliminado) {
        log("SgViajeroImpl.existSgViajeroBySgInvitadoAndSgSolicitudViaje()");
        try {
            clearQuery();
            appendQuery("select id from sg_viajero inv where inv.eliminado = ").append(eliminado).append(" and inv.sg_invitado = ").append(idInvitado);
            appendQuery(" and inv.sg_solicitud_viaje = ").append(idSolicitudViaje);
            appendQuery(" and inv.sg_viaje is not null order by inv.id limit 1 ");

            Integer obj = ((Integer) em.createNativeQuery(query.toString()).getSingleResult());

            return find(obj);
        } catch (NoResultException nre) {
            return null;
        }

    }

    
    public SgViajero sgViajeroByViajeroEscala(int idSgViajero, boolean eliminado) {
        log("SgViajeroImpl.sgViajeroByViajeroEscala(idSgViajero)");
        
        SgViajero viajero = null;
        String sql = "SELECT ro.id, ro.usuario, ro.sg_solicitud_viaje, ro.sg_viaje, ro.eliminado, ro.sg_invitado, "
                + "ro.estancia, ro.sg_solicitud_estancia, ro.redondo, ro.sg_viajero "
                + " FROM sg_viajero ro WHERE  ro.sg_viajero = ? ORDER BY ro.id DESC LIMIT 1";
        
        try {
            
            Object []obj = (Object[]) em.createNativeQuery(sql)
                    .setParameter(1, idSgViajero)
                    .getSingleResult();
            
            viajero = new SgViajero();
            
            if(obj != null){
                viajero.setId((Integer) (obj[0] != null ? obj[0] : 0));
                viajero.setUsuario(obj[1] != null ? usuarioRemote.find(obj[1].toString()) : null);
                viajero.setSgSolicitudViaje(obj[2] != null ? sgSolicitudViajeRemote.find(obj[2]): null);
                viajero.setSgViaje(obj[3] != null ? sgViajeRemote.find(obj[3]) : null);
                viajero.setEliminado( obj[4] != null ? (boolean)obj[4] : Constantes.FALSE);
                viajero.setSgInvitado(obj[5] != null ? sgInvitadoRemote.find(obj[5]) : null);
                viajero.setEstancia( obj[6] != null ? (boolean)obj[6] : Constantes.FALSE);
                viajero.setSgSolicitudEstancia(obj[7] != null ? sgSolicitudEstanciaRemote.find(obj[7]) : null);
                viajero.setRedondo(obj[8] != null ? (boolean)obj[8] : Constantes.FALSE);
                viajero.setSgViajero(obj[9] != null ? find(obj[9]) : null);
                
            }
            
        } catch (Exception e) {
            LOGGER.warn(this, sql, e);
        } 
        
        return viajero;
    }

    private void cuerpoViajeros(int status, String idUsuario, int sgOficina) {
        clearQuery();
        query.append("SELECT distinct v.ID, u.id,  i.id,  s.codigo, s.fecha_salida,  s.hora_salida, s.fecha_regreso, s.hora_regreso, "
                //                               0    1     2      3             4                  5             6               7
                + "  ot.id, u.email, v.estancia, v.redondo ,u.telefono , u.NOMBRE, i.NOMBRE, CASE WHEN drt.id > 0 then ot.NOMBRE ELSE c.NOMBRE  END, s.id"
                //   8        9         10           11         12           13      14                        15                                    16
                + "  From  SG_VIAJERO v"
                + "      inner join SG_SOLICITUD_VIAJE s on v.SG_SOLICITUD_VIAJE = s.ID"
                + "      inner join ESTATUS e on s.ESTATUS = e.ID"
                + "      inner join SG_OFICINA o on s.OFICINA_ORIGEN = o.ID"
                + "      inner join SG_DIRECCION d on o.SG_DIRECCION = d.ID"
                + "      inner join SG_ESTATUS_APROBACION ea on ea.SG_SOLICITUD_VIAJE = s.ID"
                + "      left join USUARIO u on v.USUARIO = u.ID"
                + "      left join SG_INVITADO i on v.SG_INVITADO = i.ID"
                + "      inner join SG_TIPO_SOLICITUD_VIAJE ts on s.SG_TIPO_SOLICITUD_VIAJE = ts.ID"
                + "      inner join SG_TIPO_ESPECIFICO te on ts.SG_TIPO_ESPECIFICO = te.ID"
                + "      left join SG_VIAJERO_SI_MOVIMIENTO vm on vm.SG_VIAJERO  = v.ID  AND vm.eliminado = 'False'"
                + "      inner join SG_RUTA_TERRESTRE rt on rt.id = s.SG_RUTA_TERRESTRE and rt.ELIMINADO = 'False' "
                + "      left join SG_DETALLE_RUTA_TERRESTRE drt on drt.SG_RUTA = rt.ID and drt.ELIMINADO = 'False' "
                + "      left join SG_DETALLE_RUTA_CIUDAD drc on drc.SG_RUTA_TERRESTRE = rt.ID and drc.ELIMINADO = 'False' "
                + "      left join SG_OFICINA ot on ot.id = drt.SG_OFICINA  and ot.ELIMINADO = 'False' "
                + "      left join SI_CIUDAD c on c.id = drc.SI_CIUDAD  and c.ELIMINADO = 'False' "
                + "  WHERE vm.ID is null ");

        if (status > 0) {
            query.append("  AND s.ESTATUS  = ").append(status);
        }
        if (idUsuario != null && !idUsuario.isEmpty()) {
            query.append("  AND ea.usuario = '").append(idUsuario).append("'");
        }
        if (sgOficina > 0) {
            query.append("  AND s.Oficina_Origen = ").append(sgOficina);
        }
        query.append(//"  AND s.OFICINA_DESTINO is not null"
                "  AND  te.ID = ").append(Constantes.SOLICITUDES_TERRESTRE).append(
                "  AND v.sg_viaje is null  "
                + "  AND ea.realizado = '").append(Constantes.BOOLEAN_FALSE).append("'"
                + "  AND ea.historial = '").append(Constantes.BOOLEAN_FALSE).append("'"
                + "  AND v.eliminado = '").append(Constantes.NO_ELIMINADO).append("'"
                + "  AND s.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
    }

    
    public void cambiarGRAutorizacion(String idUsuario, int idViajero, String motivo) {
        try {
            if (idViajero > 0) {
                SgViajero viajero = this.find(idViajero);
                viajero.setModifico(new Usuario(idUsuario));
                viajero.setFechaModifico(new Date());
                viajero.setHoraModifico(new Date());
                viajero.setGrAutMotivo(motivo);
                if (viajero.isGrAut()) {
                    viajero.setGrAut(Constantes.BOOLEAN_FALSE);
                } else {
                    viajero.setGrAut(Constantes.BOOLEAN_TRUE);
                }
                this.edit(viajero);
            }

        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
        }
    }

    
    public void limpiarViajerosNoAtendidos(int status, int sgOficina, String idUsuario) {
        try {
            cuerpoViajeros(status, idUsuario, sgOficina);
            query.append(" AND s.FECHA_SALIDA < CAST('NOW' as DATE)");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<ViajeroVO> lv = castTraerViajeros(lo);
            if (idUsuario == null || idUsuario.isEmpty()) {
                idUsuario = Constantes.SIA;
            }
            for (ViajeroVO vo : lv) {
                SgViajero viajero = find(vo.getId());
                viajero.setEliminado(Constantes.ELIMINADO);
                viajero.setModifico(new Usuario(idUsuario));
                viajero.setFechaModifico(new Date());
                viajero.setHoraModifico(new Date());
                edit(viajero);
                sgViajeroSiMovimientoRemote.guardaMovimiento(idUsuario, vo.getId(), "Finaliza los viajeros fuera de tiempo por falta de administración del Analista", Constantes.ID_SI_OPERACION_FINALIZAR_VIAJES_FT);

            }
            lv = viajeroQuedado(sgOficina, Constantes.QUEDADO_ORIGEN, Constantes.TRUE);
            for (ViajeroVO vo : lv) {
                SgViajero viajero = find(vo.getId());
                viajero.setEliminado(Constantes.ELIMINADO);
                viajero.setModifico(new Usuario(idUsuario));
                viajero.setFechaModifico(new Date());
                viajero.setHoraModifico(new Date());
                edit(viajero);
            }
            lv = viajeroQuedado(sgOficina, Constantes.QUEDADO_OFICINA_DESTINO, Constantes.TRUE);
            for (ViajeroVO vo : lv) {
                SgViajero viajero = find(vo.getId());
                viajero.setEliminado(Constantes.ELIMINADO);
                viajero.setModifico(new Usuario(idUsuario));
                viajero.setFechaModifico(new Date());
                viajero.setHoraModifico(new Date());
                edit(viajero);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
        }
    }

    private List<ViajeroVO> castTraerViajeros(List<Object[]> listInv) {
        List<ViajeroVO> lv = new ArrayList<ViajeroVO>();
        for (Object[] objects : listInv) {
            ViajeroVO v = new ViajeroVO();
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
            v.setIdDestino((Integer) objects[8] != null ? (Integer) objects[8] : 0);
            v.setCorreo((String) objects[9]);
            v.setEstancia((Boolean) objects[10]);
            v.setRedondo((Boolean) objects[11]);
            v.setTelefono((String) objects[12]);
            v.setViajeroQuedado(Constantes.PRIMERA_VEZ_VIAJE);
            v.setDestino((String) objects[15]);
            v.setIdSolicitudViaje((Integer) objects[16] != null ? (Integer) objects[16] : 0);
            lv.add(v);
        }

        return lv;
    }
    
    
    public void bajarViajeroConEscala(int idViajero, int idViajeroEscala, String idUser){
        
        try {
            String queryViajero = "UPDATE sg_viajero set"
                    + " eliminado = true,"
                    + " modifico = ?,"
                    + " fecha_modifico = current_date,"
                    + " hora_modifico = current_time"
                    + " WHERE id = ?";
            
            String queryEscala = "UPDATE sg_viajero set"
                    + " sg_viaje = null,"
                    + " sg_viajero = null,"
                    + " eliminado = false,"
                    + " modifico = ?,"
                    + " fecha_modifico = current_date,"
                    + " hora_modifico = current_time"
                    + " WHERE id = ?";
          
            em.createNativeQuery(queryViajero)
                    .setParameter(1, idUser)
                    .setParameter(2, idViajero).executeUpdate();
            
            em.createNativeQuery(queryEscala)
                    .setParameter(1, idUser)
                    .setParameter(2, idViajeroEscala).executeUpdate();
            
        } catch ( Exception e){
            UtilLog4j.log.error(this, e);
        }
    } 
}
