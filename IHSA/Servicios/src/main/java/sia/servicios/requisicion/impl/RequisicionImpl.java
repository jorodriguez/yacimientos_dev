/*
 * RequisicionImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.modelo.Prioridad;
import sia.modelo.Rechazo;
import sia.modelo.Requisicion;
import sia.modelo.RequisicionDetalle;
import sia.modelo.Usuario;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.requisicion.vo.NotaVO;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.requisicion.vo.RequisicionView;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.RespuestaVo;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.notificaciones.usuario.movil.impl.NotificacionMovilUsuarioImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.PrioridadImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.convenio.impl.CvConvenioArticuloImpl;
import sia.servicios.orden.impl.OcUnidadCostoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.RequisicionEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 * @version 1.0
 */
@Stateless
public class RequisicionImpl extends AbstractFacade<Requisicion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    DSLContext dbCtx;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RequisicionImpl() {
        super(Requisicion.class);
    }
    //--- Utilizando otros EJB
    @Inject
    private RequisicionDetalleImpl requisicionDetalleServicioRemoto;
    @Inject
    private RechazoImpl rechazoServicioRemoto;
    @Inject
    private PrioridadImpl prioridadServicioRemoto;
    @Inject
    private EstatusImpl estatusRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private ProyectoOtImpl proyectoOtRemote;
    @Inject
    private OcTareaImpl ocTareaRemote;
    @Inject
    private RequisicionSiMovimientoImpl requisicionSiMovimientoRemote;
    @Inject
    private NotificacionRequisicionImpl notificacionRequisicionRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private NotaRequisicionImpl notaRequisicionRemote;
    @Inject
    private OcUnidadCostoImpl ocUnidadCostoRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @Inject
    private OcUsoCFDIImpl ocUsoCFDILocal;
    @Inject
    private CvConvenioArticuloImpl cvConvenioArticuloLocal;
    @Inject
    private NotificacionMovilUsuarioImpl notificacionMovilRemote;
    @Inject
    private OcRequisicionCoNoticiaImpl ocRequisicionCoNoticiaRemote;

    public Prioridad calcularPrioridad(Date fecha1, Date fecha2) {

        int dias = siManejoFechaLocal.dias(fecha1, fecha2);

        Prioridad prioridad = new Prioridad();

        if (dias <= 2) {
            prioridad = prioridadServicioRemoto.find(1);
        } else if (dias > 2 && dias <= 10) {
            prioridad = prioridadServicioRemoto.find(2);
        } else {
            prioridad = prioridadServicioRemoto.find(3);
        }

        return prioridad;
    }

    /**
     *
     * @param id
     * @return
     */
    public Requisicion buscarLazyPorId(int id) {
        try {
            return (Requisicion) em.createQuery("SELECT r FROM Requisicion r WHERE r.id = :id", Requisicion.class).setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    public Requisicion buscarPorConsecutivo(Object consecutivo) {
        try {
            return (Requisicion) em.createQuery(
                    "SELECT r FROM Requisicion r WHERE r.eliminado = false and r.consecutivo = :consecutivo").setParameter("consecutivo", consecutivo).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    public RequisicionVO buscarPorConsecutivoConDetalle(final String consecutivo) {
        try {
            final boolean conDetalle = true;
            final boolean seleccionado = false;

            final StringBuilder sb = new StringBuilder("")
                    .append(consultaRequisicion())
                    .append(" WHERE r.CONSECUTIVO = '").append(consecutivo).append("'");

            final Object[] requisicion = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();

            RequisicionVO requisicionVo = null;

            if (requisicion != null) {
                requisicionVo = castRequisicionConDetalle(requisicion, conDetalle, seleccionado);
            }

            return requisicionVo;

        } catch (Exception e) {
            System.out.println("Error " + e);
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    public Requisicion buscarPorConsecutivoEmpresa(String consecutivo, String rfcEmpresa) {

        Requisicion r = null;

        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select * from requisicion r where r.consecutivo = '").append(consecutivo).append("'");
            sb.append(" and r.compania = (select c.RFC from compania c where c.RFC = '").append(rfcEmpresa).append("'");
            sb.append(" )");
            UtilLog4j.log.info(this, "Consulta: " + sb.toString());
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();

            if (obj != null) {
                r = find((Integer) obj[0]);
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al buscar la requision por consecutivo: " + e.getMessage(), e);
        }

        return r;
    }

    public Requisicion buscarPorConsecutivoBloque(String consecutivo, int idCampo, String usuario) {
        Requisicion r = null;
        try {
            boolean v = siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_REQUISICION, Constantes.CODIGO_ROL_CONS_REQ, idCampo);

            StringBuilder sb = new StringBuilder();
            sb.append("select * from requisicion r where r.consecutivo = '").append(consecutivo).append("'");
            sb.append(" and r.ap_campo = ").append(idCampo);
            if (!v) {
                sb.append(" and '").append(usuario).append("' in (r.solicita, r.revisa, r.aprueba, r.asigna, r.compra, r.visto_bueno)");
            }
            UtilLog4j.log.info(this, "Consulta por id usuario: " + sb.toString());
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();

            if (obj != null) {
                r = find((Integer) obj[0]);
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al buscar la requision por consecutivo: " + e.getMessage(), e);
        }

        return r;
    }

    public RequisicionView buscarConsecutivo(String consecutivo, String usuario) {
        try {

            final StringBuilder query = new StringBuilder();
            
            query.append("SELECT r.ID,")
                    .append("                        	       r.CONSECUTIVO,")
                    .append("                        	       r.REFERENCIA,")
                    .append("                        	       r.observaciones,")
                    .append("                        	       to_char(r.FECHA_REQUERIDA,'YYYY-mm-dd') as fecha_requerida,")
                    .append("                        	       r.lugar_entrega,")
                    .append("                        	       p.nombre as prioridad,")
                    .append("                        	       c.siglas as siglas_compania, ")
                    .append("                        	       g.nombre as gerencia,")
                    .append("                                  r.MONTO_MN,")
                    .append("                        	       r.MONTO_USD,")
                    .append("                        	       r.MONTOTOTAL_USD,")
                    .append("                        	       r.url,")
                    .append("                        	       e.id as id_estatus,")
                    .append("                        	       e.nombre as estatus,")
                    .append("                        	       u_solicita.nombre as solicita,")
                    .append("                        	       to_char(r.fecha_solicito + r.hora_solicito,'YYYY-mm-dd HH24:MI') as fecha_solicito,")
                    .append("                        	       u_revisa.nombre as revisa,")
                    .append("                        	       to_char(r.fecha_reviso + r.hora_reviso,'YYYY-mm-dd HH24:MI') as fecha_revisa,")
                    .append("                        	       u_aprueba.nombre as aprueba,")
                    .append("                        	       to_char(r.fecha_aprobo + r.hora_aprobo,'YYYY-mm-dd HH24:MI') as fecha_aprueba,")
                    .append("                        	       u_visto_bueno.nombre as visto_bueno,")
                    .append("                        	       to_char(r.fecha_visto_bueno + r.hora_visto_bueno,'YYYY-mm-dd HH24:MI') as fecha_visto_bueno,")
                    .append("                        	       u_cancelo.nombre as cancelo,")
                    .append("                        	       to_char(r.fecha_cancelo + r.hora_cancelo,'YYYY-mm-dd HH24:MI') as fecha_cancelo,")
                    .append("                        	       u_asigna.nombre as asigno,")
                    .append("                        	       to_char(r.fecha_asigno + r.hora_asigno,'YYYY-mm-dd HH24:MI') as fecha_asigno,")
                    .append("                        	       u_finalizo.nombre as finalizo,")
                    .append("                        	       to_char(r.fecha_finalizo + r.hora_finalizo,'YYYY-mm-dd HH24:MI') as fecha_finalizo,")
                    .append("                        	       u_comprador.nombre as comprador, ")
                    .append("                                  r.tipo,               ")
                    .append("                        	       r.proveedor,             ")
                    .append("                        	       r.motivo_cancelo,")
                    .append("                        	       r.motivo_finalizo,")
                    .append("                        	       (select EXISTS( ")
                    .append("                        				select ")
                    .append("                         			from si_usuario_rol urol inner join si_rol rol on rol.id = urol.si_rol")
                    .append("                         					inner join usuario u on u.id = urol.usuario")
                    .append("                         			where u.id = ? ")                    // 1- para usuario
                    .append("                         				and urol.ap_campo = r.ap_campo")
                    .append("                         				and rol.si_modulo = ? ")    //  2- PARAM Modulo para buscar el rol
                    .append("                         				and rol.codigo = ? ")        // 3- PARAM codigo de la requi
                    .append("                         				and u.eliminado = false")
                    .append("                         				and urol.eliminado = false                         	")
                    .append("                        		 )) as tiene_rol_consulta")
                    .append("                         FROM Requisicion r inner join compania c on c.rfc = r.compania ")
                    .append("                         				   inner join prioridad p on p.id = r.prioridad")
                    .append("                         				   inner join estatus e on e.id = r.estatus")
                    .append("                         				   inner join gerencia g on g.id = r.gerencia")
                    .append("                         				   inner join usuario u_solicita on u_solicita.id = r.solicita")
                    .append("                         				   inner join usuario u_revisa on u_revisa.id = r.revisa")
                    .append("                         				   inner join usuario u_aprueba on u_aprueba.id = r.aprueba")
                    .append("                         				   left join usuario u_visto_bueno on u_visto_bueno.id = r.visto_bueno")
                    .append("                         				   left join usuario u_asigna on u_asigna.id = r.asigna")
                    .append("                         				   left join usuario u_finalizo on u_finalizo.id = r.finalizo")
                    .append("                         				   left join usuario u_comprador on u_comprador.id = r.compra")
                    .append("                         				   left join usuario u_cancelo on u_cancelo.id = r.cancelo")
                    .append("                            WHERE r.consecutivo = TRIM(?) ") // 4- consecutivo de requisición
                    .append("                         		AND r.ESTATUS NOT IN  (1)                          		")
                    .append("                         		AND r.eliminado = false ");
            
            
             return dbCtx
                .fetchOne(
                        query.toString(),
                        usuario,
                        Constantes.MODULO_REQUISICION,
                        Constantes.CODIGO_ROL_CONS_REQ,
                        consecutivo
                ).into(RequisicionView.class);
            

        } catch (Exception e) {
            UtilLog4j.log.info(this, "buscar la requision por consecutivo : " + e.getMessage(), e);
            return null;
        }        
    }

    public RequisicionVO buscarPorConsecutivoBloque(String consecutivo, int idBloque, boolean conDetalle, boolean seleccionado) {
        RequisicionVO r = null;

        try {

            StringBuilder sb = new StringBuilder();
            sb.append(consultaRequisicion());
            sb.append(" where r.CONSECUTIVO = '").append(consecutivo).append("'");
            sb.append(" and  r.AP_CAMPO = ").append(idBloque);
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                r = castRequisicionConDetalle(obj, conDetalle, seleccionado);
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Error al buscar la requision por consecutivo: " + e.getMessage());
        }

        return r;
    }

    /**
     * @param idUsuario
     * @param apCampo
     * @return La lista de requisiciones creadas pero que no se han solicitado
     */
    public List getRequisicionesSinSolicitar(Object idUsuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "SELECT r.ID," //0
                + " r.RECHAZADA, " //1
                + " c.siglas, " //2
                + " r.oc_unidad_costo," //5
                + " r.tipo," //5
                + " r.referencia" //5
                + " FROM Requisicion r, Compania c"
                + " WHERE r.ESTATUS = "
        ).append(Constantes.REQUISICION_PENDIENTE)
                .append(
                        " AND c.rfc = r.COMPANIA"
                        + " AND r.SOLICITA = '").append(idUsuario)
                .append("' AND r.AP_CAMPO = ").append(apCampo)
                .append(" AND r.eliminado = '").append(Constantes.NO_ELIMINADO)
                .append("' ORDER BY r.ID ASC");
        //UtilLog4j.log.info(this,"Q Solicita requisiciones status 1: " + q.toString());
        return em.createNativeQuery(sb.toString()).getResultList();
    }

    /**
     * @return La lista de requisiciones sin revisar
     */
    public List getRequisicionesSinRevisar(Object idUsuario, int apCampo) {
        clearQuery();
        query.append("SELECT r.ID,"); //0
        query.append(" r.CONSECUTIVO, "); //1
        query.append(" r.REFERENCIA, "); //2
        query.append(" r.FECHA_SOLICITO, "); //3
        query.append(" r.FECHA_REQUERIDA, "); //4
        query.append(" p.nombre, "); //5
        query.append(" c.siglas, "); //6
        query.append(" r.MONTO_MN, "); //7
        query.append(" r.MONTO_USD,"); //8
        query.append(" r.MONTOTOTAL_USD "); //9
        query.append(" FROM Requisicion r, Compania c, Prioridad p");
        query.append(" WHERE r.ESTATUS = ").append(Constantes.REQUISICION_SOLICITADA); //10
        query.append(" AND r.prioridad = p.id");
        query.append(" AND r.COMPANIA =c.rfc ");
        query.append(" AND r.REVISA = '").append(idUsuario).append("'");
        query.append(" AND r.AP_CAMPO = ").append(apCampo);
        query.append(" AND r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" ORDER BY r.CONSECUTIVO ASC");
        //UtilLog4j.log.info(this,"Q Revisa requisiciones estatus 10: " + q.toString());
        return em.createNativeQuery(query.toString()).getResultList();
    }

    public List getRequisicionesSinAprobar(Object idUsuario, int apCampo) {
        clearQuery();
        query.append("SELECT r.ID,"); //0
        query.append(" r.CONSECUTIVO, "); //1
        query.append(" r.REFERENCIA, "); //2
        query.append(" r.FECHA_SOLICITO, "); //3
        query.append(" r.FECHA_REQUERIDA, "); //4
        query.append(" p.nombre, "); //5
        query.append(" c.siglas, "); //6
        query.append(" r.MONTO_MN, "); //7
        query.append(" r.MONTO_USD,"); //8
        query.append(" r.MONTOTOTAL_USD"); //9
        query.append(" FROM Requisicion r, Compania c, Prioridad p");
        query.append(" WHERE r.ESTATUS = ").append(Constantes.REQUISICION_REVISADA); //15
        query.append(" AND c.rfc = r.COMPANIA");
        query.append(" AND p.id = r.PRIORIDAD");
        query.append(" AND r.APRUEBA = '").append(idUsuario).append("'");
        query.append(" AND r.AP_CAMPO = ").append(apCampo);
        query.append(" AND r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" ORDER BY r.CONSECUTIVO ASC");
        //UtilLog4j.log.info(this,"Q Aprobar requisiciones estatus 15: " + q.toString());
        return em.createNativeQuery(query.toString()).getResultList();
    }

    //--Para obtener una lista descendente 
    public List<RequisicionView> getUltimasRequisicionesModificadas(Object idUsuario, int apCampo) {
        clearQuery();
        query.append("               SELECT r.ID,")
                .append("        		r.CONSECUTIVO,")
                .append("        		r.REFERENCIA,")
                .append("        	        r.observaciones,\n")
                .append("        		to_char(r.FECHA_REQUERIDA,'YYYY-mm-dd') as fecha_requerida,")
                .append("        	        r.lugar_entrega,\n")
                .append("        		p.nombre as prioridad,")
                .append("        		c.siglas as siglas_compania, ")
                .append("        		g.nombre as gerencia,")
                .append("                      r.MONTO_MN,")
                .append("        		r.MONTO_USD,")
                .append("        	       r.MONTOTOTAL_USD,")
                .append("        	       r.url,")
                .append("        	       e.id as id_estatus,")
                .append("        	       e.nombre as estatus,")
                .append("        	       u_solicita.nombre as solicita,")
                .append("        	       to_char(r.fecha_solicito + r.hora_solicito,'YYYY-mm-dd HH24:MI') as fecha_solicito,")
                .append("        	       u_revisa.nombre as revisa,")
                .append("        	       to_char(r.fecha_reviso + r.hora_reviso,'YYYY-mm-dd HH24:MI') as fecha_revisa,")
                .append("        	       u_aprueba.nombre as aprueba,")
                .append("        	       to_char(r.fecha_aprobo + r.hora_aprobo,'YYYY-mm-dd HH24:MI') as fecha_aprueba,")
                .append("        	       u_visto_bueno.nombre as visto_bueno,")
                .append("        	       to_char(r.fecha_visto_bueno + r.hora_visto_bueno,'YYYY-mm-dd HH24:MI') as fecha_visto_bueno,")
                .append("        	       u_cancelo.nombre as cancelo,")
                .append("        	       to_char(r.fecha_cancelo + r.hora_cancelo,'YYYY-mm-dd HH24:MI') as fecha_cancelo,")
                .append("        	       u_asigna.nombre as asigno,")
                .append("        	       to_char(r.fecha_asigno + r.hora_asigno,'YYYY-mm-dd HH24:MI') as fecha_asigno,")
                .append("        	       u_finalizo.nombre as finalizo,")
                .append("        	       to_char(r.fecha_finalizo + r.hora_finalizo,'YYYY-mm-dd HH24:MI') as fecha_finalizo,")
                .append("        	       u_comprador.nombre as comprador, ")
                .append("                      r.tipo,\n")
                .append("        	       r.proveedor,\n")
                .append("        	       r.motivo_cancelo,\n")
                .append("        	       r.motivo_finalizo  ")
                .append("         FROM Requisicion r inner join compania c on c.rfc = r.compania ")
                .append("         				   inner join prioridad p on p.id = r.prioridad")
                .append("         				   inner join estatus e on e.id = r.estatus")
                .append("         				   inner join gerencia g on g.id = r.gerencia")
                .append("         				   inner join usuario u_solicita on u_solicita.id = r.solicita")
                .append("         				   inner join usuario u_revisa on u_revisa.id = r.revisa")
                .append("         				   inner join usuario u_aprueba on u_aprueba.id = r.aprueba")
                .append("         				   left join usuario u_visto_bueno on u_visto_bueno.id = r.visto_bueno")
                .append("         				   left join usuario u_asigna on u_asigna.id = r.asigna")
                .append("         				   left join usuario u_finalizo on u_finalizo.id = r.finalizo")
                .append("         				   left join usuario u_comprador on u_comprador.id = r.compra")
                .append("         				   left join usuario u_cancelo on u_cancelo.id = r.cancelo")
                .append("            WHERE ( ")
                .append("         			r.solicita = ? ")
                .append("         			OR r.revisa = ? ")
                .append("         			OR r.aprueba = ? ")
                .append("         			OR r.aprueba = ? ")
                .append("         			OR r.visto_bueno = ? ")
                .append("         			OR r.cancelo = ? ")
                .append("         			OR r.finalizo = ? ")
                .append("         			OR r.asigna = ? ")
                .append("         		)         		 ")
                .append("         		AND r.ESTATUS NOT IN  (1) ")
                .append("         		AND r.AP_CAMPO = ? ")
                .append("         		AND r.eliminado = false ")
                .append("         		AND r.fecha_modifico is not null ")
                .append("         		and r.hora_modifico is not null ")
                .append("         ORDER BY (r.fecha_modifico+r.hora_modifico)::timestamp desc ")
                .append("         LIMIT 3");
        return dbCtx
                .fetch(
                        query.toString(),
                        idUsuario,
                        idUsuario,
                        idUsuario,
                        idUsuario,
                        idUsuario,
                        idUsuario,
                        idUsuario,
                        idUsuario,
                        apCampo
                ).into(RequisicionView.class);

    }

    public List<Requisicion> getRequisicionesSinAutorizar(Object idUsuario) {
        return em.createQuery(
                "SELECT r FROM Requisicion r WHERE r.estatus.id = 20 AND r.autoriza.id = :autoriza").setParameter("autoriza", idUsuario).getResultList();
    }

    public List<Requisicion> getRequisicionesSinVistoBueno(Object idUsuario) {
        return em.createQuery(
                "SELECT r FROM Requisicion r WHERE r.estatus.id = 30 AND r.vistoBueno.id = :vistoBueno").setParameter("vistoBueno", idUsuario).getResultList();
    }

    public List<RequisicionVO> getRequisicionesSinAsignar(String idUsuario, int apCampo, int idRol, int status) {
        clearQuery();
        query.append("SELECT r.ID, r.CONSECUTIVO, r.REFERENCIA,  r.FECHA_SOLICITO,  r.FECHA_REQUERIDA,  p.nombre,  r.MONTO_MN,  ")
                .append(" r.MONTOTOTAL_USD, u.nombre, g.nombre, (SELECT count(rd.id) from requisicion_detalle rd \n")
                .append(" 									inner join inv_inventario  inv on rd.inv_articulo = inv.articulo \n")
                .append("                                                                       inner join inv_almacen al on inv.almacen = al.id")
                .append(" 								where rd.requisicion = r.id ")
                .append("                                                               and al.ap_campo = ").append(apCampo).append(")")
                .append(" from REQUISICION r")
                .append("   inner join PRIORIDAD p on r.PRIORIDAD = p.ID")
                .append("   inner join usuario u  on r.solicita = u.ID")
                .append("   inner join gerencia g on r.gerencia = g.ID")
                .append(" where r.ESTATUS = ").append(status)
                .append(" and r.ap_campo = ").append(apCampo);
        if (idUsuario.equals("PRUEBA")) {
            query.append(" AND r.solicita = '").append(idUsuario).append("'");
        } else {
            query.append(" AND r.solicita <> '").append("PRUEBA").append("'");
            query.append(" and '").append(idUsuario).append("' in (select ur.USUARIO from SI_USUARIO_ROL ur, AP_CAMPO_USUARIO_RH_PUESTO cap where ur.SI_ROL = ").append(idRol).append(" and ur.ELIMINADO = false");
            query.append(" and cap.AP_CAMPO = r.AP_CAMPO and ur.USUARIO = cap.usuario").append(")");
        }

        query.append(" order by g.nombre asc, r.consecutivo asc ");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<RequisicionVO> lr = null;
        if (lo != null) {
            lr = new ArrayList<>();
            for (Object[] objects : lo) {
                lr.add(castReqSinAsignar(objects));
            }
        }
        return lr;
    }

    public List<RequisicionVO> getRequisicionesEnEspera(String idUsuario, int apCampo, int idRol, int status, int gerenciaID, boolean isAdmin) {
        clearQuery();
        query.append("SELECT r.ID, r.CONSECUTIVO, r.REFERENCIA,  r.FECHA_SOLICITO,  r.FECHA_REQUERIDA,  p.nombre,  r.MONTO_MN,  ")
                .append(" r.MONTOTOTAL_USD, u.nombre, g.nombre, (SELECT count(rd.id) from requisicion_detalle rd \n")
                .append(" 									inner join inv_inventario  inv on rd.inv_articulo = inv.articulo \n")
                .append("                                                                       inner join inv_almacen al on inv.almacen = al.id")
                .append(" 								where rd.requisicion = r.id ")
                .append("                                                               and al.ap_campo = ").append(apCampo).append(")")
                .append(" from REQUISICION r")
                .append("   inner join PRIORIDAD p on r.PRIORIDAD = p.ID ")
                .append("   inner join usuario u  on r.solicita = u.ID ")
                .append("   inner join gerencia g on r.gerencia = g.ID ")
                .append("   inner join requisicion_si_movimiento rm on rm.requisicion = r.id and rm.eliminado = false ")
                .append("   inner join si_movimiento m on m.id = rm.si_movimiento and m.eliminado = false and m.si_operacion = 94  ");
        if (!isAdmin) {
            query.append(" and m.genero = '").append(idUsuario).append("' ");
        }

        query.append(" where r.ESTATUS = ").append(status)
                .append(" and r.ap_campo = ").append(apCampo);

        if (gerenciaID > 0) {
            query.append(" and g.id = ").append(gerenciaID);
        }

        query.append(" and '").append(idUsuario).append("' in (select ur.USUARIO from SI_USUARIO_ROL ur, AP_CAMPO_USUARIO_RH_PUESTO cap where ur.SI_ROL = ").append(idRol).append(" and ur.ELIMINADO = false");
        query.append(" and cap.AP_CAMPO = r.AP_CAMPO and ur.USUARIO = cap.usuario").append(")");

        query.append(" order by g.nombre asc, r.consecutivo asc ");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<RequisicionVO> lr = null;
        if (lo != null) {
            lr = new ArrayList<>();
            for (Object[] objects : lo) {
                lr.add(castReqSinAsignar(objects));
            }
        }
        return lr;
    }

    private RequisicionVO castReqSinAsignar(Object[] objects) {
        RequisicionVO o = new RequisicionVO();
        o.setId((Integer) objects[0]);
        o.setConsecutivo(String.valueOf(objects[1]));
        o.setReferencia(String.valueOf(objects[2]));
        o.setFechaSolicitada((Date) objects[3]);
        o.setFechaRequerida((Date) objects[4]);
        o.setPrioridad((String) objects[5]);
        o.setMontoPesos((Double) objects[6]);
        o.setMontoDolares((Double) objects[7]);
        o.setSolicita((String) objects[8]);
        o.setGerencia((String) objects[9]);
        o.setTotal((Long) objects[10]);
        o.setSelected(false);

        return o;

    }

    /**
     * @param idUsuario
     * @param apCampo
     * @return La lista de requisiciones sin disgregar por un analista
     */
    //Modificacion ṕara recibir Requisiciones
    public List<RequisicionVO> getRequisicionesSinDisgregar(String idUsuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(consultaRequisicion())
                .append(" WHERE r.ESTATUS = ").append(Constantes.ESTATUS_ASIGNADA)
                .append(" AND r.COMPRA = '").append(idUsuario).append("'")
                .append(" AND r.AP_CAMPO = ").append(apCampo)
                .append(" AND r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'")
                .append(" ORDER BY r.CONSECUTIVO ASC");
        List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
        List<RequisicionVO> lr = null;
        if (lo != null) {
            lr = new ArrayList<RequisicionVO>();
            for (Object[] objects : lo) {
                lr.add(castRequisicionConDetalle(objects, Constantes.FALSE, Constantes.FALSE));
            }
        }
        return lr;
    }

    /**
     * @param idRequisicion
     * @param agrupadorID
     * @return La lista de items por JPA
     */
    public List<RequisicionDetalle> getItems(Object idRequisicion, int agrupadorID) {
        return requisicionDetalleServicioRemoto.getItemsPorRequisicion(idRequisicion, agrupadorID);
    }

    /**
     * @param idRequisicion
     * @param autorizdo
     * @param seleccionado
     * @return La lista de items consulta nativa
     */
    public List<RequisicionDetalleVO> getItemsPorRequisicion(int idRequisicion, boolean autorizdo, boolean seleccionado) {
        return requisicionDetalleServicioRemoto.getItemsPorRequisicionConsultaNativa(idRequisicion, autorizdo, seleccionado);
    }

    public List<RequisicionDetalleVO> getItemsPorRequisicionMulti(Object idRequisicion, boolean autorizdo, boolean seleccionado) {
        return requisicionDetalleServicioRemoto.getItemsPorRequisicionConsultaNativaMulti(idRequisicion, autorizdo, seleccionado);
    }

    /**
     * @return La lista de items para modulo analista
     */
    public List<RequisicionDetalle> getItemsAnalista(Object idRequisicion) {
        return requisicionDetalleServicioRemoto.getItemsAnalista(idRequisicion);
    }

    /**
     * @param idRequisicion
     * @param seleccionado
     * @return La lista de items con consulta nativa
     */
    public List<RequisicionDetalleVO> getItemsAnalistaNativa(int idRequisicion, boolean seleccionado) {
        return requisicionDetalleServicioRemoto.getItemsAnalistaNativa(idRequisicion, seleccionado);
    }

    /**
     * @return La lista de items con consulta nativa
     */
    public List<RequisicionDetalleVO> getItemsAnalistaNativaMulti(Object idRequisicion, boolean seleccionado) {
        return requisicionDetalleServicioRemoto.getItemsAnalistaNativaMulti(idRequisicion, seleccionado);
    }

    public RequisicionDetalle buscarItemPorId(Object id) {
        return em.find(RequisicionDetalle.class, id);
    }

    public void actualizarItem(RequisicionDetalle item) {
//	item.setImporte(item.getCantidadAutorizada() * item.getPrecioUnitario());
        requisicionDetalleServicioRemoto.edit(item);
//	editMontos(item.getRequisicion().getId());
    }

    public void actualizarItemCrearRequisicion(RequisicionDetalle item, int idTarea) {
//	item.setSiUnidad(ocUnidadRemote.find(idUnidad));
        if (idTarea > 0 && TipoRequisicion.PS.equals(item.getRequisicion().getTipo())) {
            item.setOcTarea(ocTareaRemote.find(idTarea));
        }
        item.setCantidadAutorizada(item.getCantidadSolicitada());
//	item.setImporte(item.getCantidadSolicitada() * item.getPrecioUnitario());
//	item.setUnidad(null);
        requisicionDetalleServicioRemoto.edit(item);
//	editMontos(item.getRequisicion().getId());
    }

    public void eliminarItem(RequisicionDetalle item) {
        requisicionDetalleServicioRemoto.remove(item);
    }

    public void eliminarItems(Requisicion requisicion, int agrupadorID) {
        for (RequisicionDetalle item : this.getItems(requisicion.getId(), agrupadorID)) {
            requisicionDetalleServicioRemoto.remove(item);
        }
    }

    public void crearItem(RequisicionDetalle item, int idNombreTarea, String sesion) {
        //	item.setSiUnidad(ocUnidadRemote.find(idUnidad));
        if (idNombreTarea > 0 && TipoRequisicion.PS.equals(item.getRequisicion().getTipo())) {
            item.setOcTarea(ocTareaRemote.find(idNombreTarea));
        }
        item.setAutorizado(Boolean.TRUE);
        item.setCantidadAutorizada(item.getCantidadSolicitada());
        //
        item.setGenero(new Usuario(sesion));
        item.setFechaGenero(new Date());
        item.setHoraGenero(new Date());
        item.setEliminado(Constantes.FALSE);
        requisicionDetalleServicioRemoto.create(item);

    }

    // implementación de Rechazos ---
    public List<Rechazo> getRechazosPorRequisicion(int idRequisicion) {
        return rechazoServicioRemoto.getRechazosPorRequisicion(idRequisicion);
    }

    public List<Rechazo> getRechazosIncumplidos(int idRequisicion) {
        return rechazoServicioRemoto.getRechazosIncumplidos(idRequisicion);
    }

    public void crearRechazo(Rechazo rechazo) {
        rechazoServicioRemoto.create(rechazo);
    }

    public void actualizarRechazo(Rechazo rechazo) {
        rechazoServicioRemoto.edit(rechazo);
    }

    public long getTotalRequisicionesSinSolicitar(Object idUsuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*) from Requisicion r ");
        sb.append(" where r.estatus = ").append(Constantes.REQUISICION_PENDIENTE);
        sb.append(" and r.solicita = '").append(idUsuario).append("'");
        sb.append(" and r.ap_campo = ").append(apCampo);
        sb.append(" and r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        //
        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());

    }

    public long getTotalRequisicionesSinRevisar(Object idUsuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*) from Requisicion r ");
        sb.append(" where r.estatus = ").append(Constantes.REQUISICION_SOLICITADA);
        sb.append(" and r.revisa = '").append(idUsuario).append("'");
        sb.append(" and r.ap_campo = ").append(apCampo);
        sb.append(" and r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        //
        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    public long getTotalRequisicionesSinAprobar(Object idUsuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*) from Requisicion r ");
        sb.append(" where r.estatus = ").append(Constantes.REQUISICION_REVISADA);
        sb.append(" and r.ap_campo = ").append(apCampo);
        sb.append(" and r.aprueba = '").append(idUsuario).append("'");
        sb.append(" and r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        //
        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
    }

    public long getTotalRequisicionesSinVistoBueno(String idSesion, int idCampo, String tipoRequisicion, int idRol) {
        clearQuery();
        query.append("SELECT count(r.id) ");
        query.append(" FROM Requisicion r");
        query.append(" inner join GERENCIA g on r.GERENCIA = g.id");
        query.append(" inner join PROYECTO_OT pot on r.PROYECTO_OT  = pot.id");
        query.append(" left join OC_UNIDAD_COSTO uc on r.OC_UNIDAD_COSTO = uc.id");
        query.append(" WHERE r.ESTATUS = ").append(Constantes.REQUISICION_VISTO_BUENO_C);
        query.append(" and '").append(idSesion).append("'");
        query.append(" in ( select u.id from USUARIO u ");
        query.append(" inner join AP_CAMPO_USUARIO_RH_PUESTO cap on cap.USUARIO = u.id ");
        query.append(" inner join SI_USUARIO_ROL ur on ur.USUARIO = u.id and ur.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append(" inner join SI_ROL rol on ur.SI_ROL = rol.ID and rol.ID = ").append(idRol);
        query.append(" and rol.SI_MODULO = ").append(Constantes.MODULO_REQUISICION);
        query.append(" where u.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        query.append(" )");
        query.append(" and r.AP_CAMPO = ").append(idCampo);
        query.append(" and r.TIPO = '").append(tipoRequisicion).append("'");

        return ((Long) em.createNativeQuery(query.toString()).getSingleResult());

    }

    public long getTotalRequisicionesSinAsignar(String idUsuario, int apCampo, int estatus, int idRol) {
        long retVal = 0;

        StringBuilder sb = new StringBuilder();
        StringBuilder sbP = new StringBuilder();

        if (idUsuario.equals("PRUEBA")) {
            sbP.append(" AND r.SOLICITA = '").append(idUsuario).append("'");
        } else {
            sbP.append(" AND r.SOLICITA <> '").append("PRUEBA").append("'")
                    .append(" and '").append(idUsuario)
                    .append("' in (select ur.usuario from si_usuario_rol  ur where ur.SI_ROL = ").append(idRol)
                    .append(" and ur.AP_CAMPO = ")
                    .append(apCampo).append(" and ur.eliminado = '").append(Constantes.NO_ELIMINADO).append("')");
        }

        sb.append("select count(*) from Requisicion r WHERE r.estatus = ").append(estatus)
                .append(" AND r.ap_Campo = ").append(apCampo).append(" AND r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'")
                .append(sbP.toString());

        UtilLog4j.log.info(this, "con sin asignar : " + sb.toString());

        try {
            retVal = ((Long) em.createNativeQuery(sb.toString()).getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.error("****", e);
        }

        return retVal;
    }

    public long getTotalRequisicionesEnEspera(String idUsuario, int apCampo, int estatus, int idRol) {
        long retVal = 0;

        String sb = " SELECT count(r.id) "
                + " from requisicion r "
                + " inner join requisicion_si_movimiento rm on rm.requisicion = r.id and rm.eliminado = false "
                + " inner join si_movimiento m on m.id = rm.si_movimiento and m.eliminado = false and m.si_operacion = " + Constantes.ID_SI_OPERACION_ESPERA
                + " and m.genero = '" + idUsuario + "' "
                + " where r.eliminado = false "
                + " and r.estatus = " + estatus
                + " and '" + idUsuario + "' IN ( "
                + " 	select ur.usuario  "
                + " 	from si_usuario_rol  ur  "
                + " 	where ur.SI_ROL = " + idRol
                + " 	and ur.ap_campo =  " + apCampo
                + " 	and ur.eliminado = false "
                + " ) ";

        UtilLog4j.log.info(this, "con sin asignar : " + sb);

        try {
            retVal = ((Long) em.createNativeQuery(sb).getSingleResult());
        } catch (Exception e) {
            UtilLog4j.log.error("****", e);
        }

        return retVal;
    }

    public long getTotalRequisicionesSinDisgregar(Object idUsuario, int apCampo) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*) from Requisicion r ");
        sb.append(" where r.estatus = ").append(Constantes.REQUISICION_ASIGNADA);
        sb.append(" and r.compra = '").append(idUsuario).append("'");
        sb.append(" and r.ap_campo = ").append(apCampo);
        sb.append(" and r.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        //
        return ((Long) em.createNativeQuery(sb.toString()).getSingleResult());

//        return ((Long) em.createQuery("select count(r) from Requisicion r WHERE r.estatus.id = " + Constantes.REQUISICION_ASIGNADA + " AND r.compra.id = :idUsuario"
//                + " AND r.apCampo.id = :campo "
//                + " AND r.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("idUsuario", idUsuario).setParameter("campo", apCampo).getSingleResult()).intValue();
    }

    // implementacion de ETS (Especificación tecnica de suministro)
    //////Implementacion para requisiciones
    /**
     * ************* NUEVO HISTORIAL ****************
     */
    public List listaRequisicionesSolicitadas(Usuario usuario) {
        try {
            Query q = em.createNativeQuery("SELECT r.id, " //0
                    + "r.consecutivo, " //1
                    + "r.FECHA_SOLICITO, " //2
                    + "r.FECHA_REQUERIDA, " //3
                    + "c.siglas, " //4
                    + "r.proveedor, " //5
                    + "e.nombre ," //6
                    + "r.referencia" //7
                    + " FROM Requisicion r, Compania c, Estatus e"
                    + " WHERE r.solicita = '" + usuario.getId() + "'"
                    + " AND r.AP_CAMPO = " + usuario.getApCampo().getId()
                    + " AND c.RFC = r.COMPANIA "
                    + "AND (e.id = r.estatus AND r.estatus >= 10) "
                    + " ORDER BY r.FECHA_SOLICITO DESC");
            return q.getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    public List listaRequisicionesRevisadas(Usuario usuario) {
        try {
            Query q = em.createNativeQuery("SELECT r.id, "
                    + " r.consecutivo, "
                    + " r.FECHA_REVISO, "
                    + " r.FECHA_REQUERIDA, "
                    + " p.nombre,"
                    + " c.siglas,"
                    + " e.nombre, "
                    + " r.REFERENCIA"
                    + " FROM Requisicion r, Compania c, Estatus e, Prioridad p"
                    + " WHERE r.revisa = '" + usuario.getId() + "'"
                    + " AND r.AP_CAMPO = " + usuario.getApCampo().getId()
                    + " AND c.RFC = r.COMPANIA "
                    + " AND p.id = r.PRIORIDAD"
                    + " AND (e.id = r.estatus AND r.estatus >= 15)  "
                    + " ORDER BY r.FECHA_REVISO DESC");

            return q.getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    public List listaRequisicionesAprobadas(Usuario usuario) {
        try {
            Query q = em.createNativeQuery("SELECT r.id, "
                    + " r.consecutivo, "
                    + " r.FECHA_APROBO, "
                    + " r.FECHA_REQUERIDA, "
                    + " p.nombre, "
                    + " c.siglas, "
                    + " e.nombre,"
                    + " r.referencia"
                    + " FROM Requisicion r, Compania c, Estatus e, Prioridad p"
                    + " WHERE r.aprueba = '" + usuario.getId() + "'"
                    + " AND r.AP_CAMPO = " + usuario.getApCampo().getId()
                    + " AND c.RFC = r.COMPANIA "
                    + " AND p.id = r.PRIORIDAD"
                    + " AND (e.id = r.estatus AND r.estatus >= 20)  "
                    + " ORDER BY r.FECHA_APROBO DESC");
            return q.getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    public List listaRequisicionesAsignadas(Usuario usuario) {
        try {
            Query q = em.createNativeQuery("SELECT r.id, "
                    + "r.consecutivo, "
                    + "r.FECHA_ASIGNO, "
                    + "r.FECHA_REQUERIDA, "
                    + "p.nombre, "
                    + "c.siglas , "
                    + "e.nombre,"
                    + "r.referencia"
                    + " FROM Requisicion r, Compania c, Estatus e, Prioridad p"
                    + " WHERE r.asigna  = '" + usuario.getId() + "'"
                    + " AND r.AP_CAMPO = " + usuario.getApCampo().getId()
                    + " AND c.RFC = r.COMPANIA "
                    + " AND p.id = r.PRIORIDAD"
                    + " AND (e.id = r.estatus)"
                    //////                    + " AND (e.id = r.estatus AND r.estatus = 40)"
                    + " ORDER BY r.FECHA_ASIGNO DESC");
            return q.getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }
//Devolver req

    public boolean devolverSIARequisicion(String idSesion, int idRequisicion, String motivo, String idAnalista) throws Exception {
        //NoOtificar devolucion
        Requisicion requisicion = find(idRequisicion);

        requisicion.setModifico(usuarioRemote.find(idSesion));
        requisicion.setFechaModifico(new Date());
        requisicion.setHoraModifico(new Date());
        Usuario usuarioSesion = usuarioRemote.find(idSesion);
        //
        Usuario analista = usuarioRemote.find(idAnalista);
        boolean v;

        v = notificacionRequisicionRemote.envioReasignarRequisicion(usuarioSesion.getEmail(), requisicion.getCompra().getEmail(),
                "", new StringBuilder().append("Reasignó la requisición - ").append(requisicion.getConsecutivo()).toString(),
                requisicion);
        if (v) {
            v = notificacionRequisicionRemote.envioNotificacionRequisicionPDF(analista.getDestinatarios(), usuarioSesion.getEmail(),
                    usuarioRemote.find("SIA").getEmail(),
                    new StringBuilder().append("REQUISICIÓN: ").append(requisicion.getConsecutivo()).append(" POR FAVOR COLOCAR LA ORDEN DE COMPRA.").toString(),
                    requisicion, usuarioSesion, "solicito", "revisar", "aprobar");

            requisicion.setCompra(analista);
            if (v) {
                v = notificacionRequisicionRemote.envioAutorizadaRequisicion(requisicion.getSolicita().getDestinatarios(),
                        "", "",
                        new StringBuilder().append("REQUISICIÓN: ").append(requisicion.getConsecutivo()).append(" AUTORIZADA.").toString(),
                        requisicion,
                        "solicito", "revisar", "aprobar");
                if (v) {
                    requisicionSiMovimientoRemote.saveRequestMove(idSesion, motivo, idRequisicion, Constantes.ID_SI_OPERACION_DEVOLVER);

                    edit(requisicion);

                } else {
                    return false;
                }

            } else {
                return false;
            }
        } else {
            return false;
        }
        return v;
    }

    public int obtieneTotalRequisiciones(Object idUsuario) {
        return ((int) em.createQuery("select count(r) from Requisicion r WHERE r.estatus.id IN(40,1,10,15,20,30,35)  AND r.asigna.id = :asigna AND r.eliminado = :eli").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("asigna", idUsuario).getSingleResult());
    }

    public void edit(Requisicion requisicion) {
        try {
            super.edit(requisicion);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(RequisicionImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransactionRequiredException ex) {
            Logger.getLogger(RequisicionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * *
     *
     * -- and r.id not in (select o.REQUISICION from ORDEN o where o.ELIMINADO =
     * false)
     *
     * @param idComprador
     * @param status
     * @param dias
     * @param idBloque
     * @return
     */
    public List<RequisicionVO> listaRequisicionAsignadas(String idComprador, int status, int dias, int idBloque) {
        List<RequisicionVO> lr = null;
        try {
            clearQuery();
            query.append("select r.ID, r.PROVEEDOR, r.REFERENCIA, g.nombre as gerencia, pot.nombre as proyectoOT, u.nombre as comprador, r.FECHA_ASIGNO,");
            query.append(" (select count(*) from REQUISICION_DETALLE rd where rd.DISGREGADO = true and rd.REQUISICION = r.ID) as EN_OCS,");
            query.append(" (select count(*) from REQUISICION_DETALLE rd where rd.autorizado = true and rd.REQUISICION = r.ID) as TotalItems,");
            query.append(" r.consecutivo,");
            query.append(" (select count(*) from REQUISICION_DETALLE rd where rd.autorizado = true and rd.DISGREGADO = 'No' and rd.REQUISICION = r.ID) as ItemsPendientes");
            query.append(" from REQUISICION r, gerencia g, PROYECTO_OT pot, USUARIO u");
            query.append(" where r.FECHA_ASIGNO <= (SELECT CURRENT_DATE - ").append(dias).append(")");
            query.append(" and r.ELIMINADO = false");
            query.append(" and r.compra = '").append(idComprador).append("'");//MPG050310434'--IHI070320FI3'    ");
            query.append(" and r.ESTATUS = ").append(status);
            query.append(" and r.ap_campo = ").append(idBloque);
            query.append(" and r.COMPRA = u.ID");
            query.append(" and r.GERENCIA = g.id");
            query.append(" and r.PROYECTO_OT = pot.id");
            query.append(" order by r.fecha_asigno desc");
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (lo != null) {
                lr = new ArrayList<RequisicionVO>();
                for (Object[] objects : lo) {
                    lr.add(castRequisicionVO(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lr = null;
        }
        return lr;
    }

    private RequisicionVO castRequisicionVO(Object[] objects) {
        RequisicionVO requisicionVO = new RequisicionVO();
        requisicionVO.setId((Integer) objects[0]);
        requisicionVO.setProveedor((String) objects[1]);
        requisicionVO.setReferencia((String) objects[2]);
        requisicionVO.setGerencia((String) objects[3]);
        requisicionVO.setProyectoOT((String) objects[4]);
        requisicionVO.setComprador((String) objects[5]);
        requisicionVO.setFechaAsignada((Date) objects[6]);
        requisicionVO.setTotalItemEnOrden((Long) objects[7]);
        requisicionVO.setTotalItems((Long) objects[8]);
        requisicionVO.setConsecutivo((String) objects[9]);
        requisicionVO.setTotalItemSinOrden((Long) objects[10]);
        return requisicionVO;
    }

    public boolean crearRequisicionDesdeOtra(String idSesion, RequisicionVO requisicionVO) {
        boolean v = false;
        try {
            Requisicion requisicion = find(requisicionVO.getId());
            Requisicion r = new Requisicion();
            r.setCompania(requisicion.getCompania());
            r.setGerencia(gerenciaRemote.find(requisicionVO.getIdGerencia()));
            r.setProyectoOt(proyectoOtRemote.find(requisicionVO.getIdProyectoOT()));
            if (requisicionVO.getTipo().equals(TipoRequisicion.PS.name())) {
                if (requisicionVO.getIdUnidadCosto() > 0) {
                    r.setOcUnidadCosto(ocUnidadCostoRemote.find(requisicionVO.getIdUnidadCosto()));
                }
                r.setTipo(TipoRequisicion.PS);
            } else if (requisicionVO.getTipo().equals(TipoRequisicion.AI.name())) {
                r.setTipoObra(null);
                r.setTipo(TipoRequisicion.PS);
            } else {
                r.setTipoObra(null);
                r.setTipo(TipoRequisicion.AF);
            }
            //
            r.setEstatus(estatusRemote.find(Constantes.REQUISICION_PENDIENTE));
            r.setSolicita(usuarioRemote.find(idSesion));
            r.setProveedor(requisicion.getProveedor());
            r.setReferencia(requisicion.getReferencia());
            r.setContrato(requisicion.isContrato());
            r.setFechaElaboracion(new Date());
            r.setFechaRequerida(new Date());
            r.setLugarEntrega(requisicion.getLugarEntrega());
            r.setObservaciones(requisicion.getObservaciones());
            r.setPrioridad(requisicion.getPrioridad());
            r.setGenero(usuarioRemote.find(idSesion));
            r.setFechaGenero(new Date());
            r.setHoraGenero(new Date());
            r.setEliminado(Constantes.NO_ELIMINADO);
            r.setApCampo(requisicion.getApCampo());
            r.setRechazada(Constantes.BOOLEAN_FALSE);
            r.setMultiproyecto(requisicion.isMultiproyecto());
            r.setOcMetodoPago(requisicion.getOcMetodoPago());
            create(r);

            //Genera los Items
            List<RequisicionDetalleVO> listaReqDetSel = new ArrayList<>();
            for (RequisicionDetalleVO requisicionDetalleVO : requisicionVO.getListaDetalleRequision()) {
                if (requisicionDetalleVO.isSelected()) {
                    listaReqDetSel.add(requisicionDetalleVO);
                }
            }
            //List<RequisicionDetalleVO> lr = requisicionDetalleServicioRemoto.getItemsPorRequisicionConsultaNativa(requisicion.getId());
            for (RequisicionDetalleVO requisicionDetalleVO : listaReqDetSel) {
                requisicionDetalleServicioRemoto.crearDetalleRequisicion(idSesion, requisicionDetalleVO, r.getId());

            }
            // Edita los montos
//	    editMontos(r.getId());
            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return v;
    }

    public List<RequisicionVO> buscarRequisicionPorFiltro(String idSesion, int idBloque, int idStatus, String fechaInicio, String fechaFin) {
        StringBuilder sb = new StringBuilder();
        List<Object[]> lo;
        List<RequisicionVO> lr = null;
        sb.append("SELECT r.id,  r.consecutivo,  r.FECHA_SOLICITO,  r.FECHA_REQUERIDA, c.siglas, r.proveedor,  e.nombre , r.referencia, u.nombre, "); //7
        sb.append(" pot.nombre, pot.cuenta_contable, g.nombre, r.monto_mn, r.montototal_usd");
        sb.append(" FROM Requisicion r");
        sb.append(" inner join proyecto_ot pot on r.proyecto_ot = pot.id");
        sb.append(" inner join gerencia g on r.gerencia = g.id");
        sb.append(" inner join compania c on r.COMPANIA = c.RFC");
        sb.append(" inner join ESTATUS e on r.ESTATUS = e.id");
        sb.append(" inner join USUARIO u on r.SOLICITA = u.id");
        sb.append(" where r.ap_campo = ").append(idBloque);
        sb.append(" and r.estatus >= ").append(idStatus);
        sb.append(" and r.ESTATUS <> ").append(Constantes.REQUISICION_CANCELADA);
        sb.append(" and r.fecha_solicito between cast('").append(fechaInicio).append("' as date)").append(" and cast('").append(fechaFin).append("' as date)");
        sb.append(" and  '").append(idSesion).append("' in (r.solicita, r.revisa, r.aprueba, r.asigna, r.visto_bueno) ");

        sb.append(" order by r.consecutivo asc");
        lo = em.createNativeQuery(sb.toString()).getResultList();
        if (lo != null) {
            lr = new ArrayList<RequisicionVO>();
            for (Object[] objects : lo) {
                lr.add(castRequisicion(objects));
            }
        }
        return lr;
    }

    private RequisicionVO castRequisicion(Object[] objects) {
        RequisicionVO requisicionVO = new RequisicionVO();
        requisicionVO.setId((Integer) objects[0]);
        requisicionVO.setConsecutivo((String) objects[1]);
        requisicionVO.setFechaSolicitada((Date) objects[2]);
        requisicionVO.setFechaRequerida((Date) objects[3]);
        requisicionVO.setCompania((String) objects[4]);
        requisicionVO.setProveedor((String) objects[5]);
        requisicionVO.setEstatus((String) objects[6]);
        requisicionVO.setReferencia((String) objects[7]);
        requisicionVO.setSolicita((String) objects[8]);
        requisicionVO.setProyectoOT((String) objects[9]);
        requisicionVO.setCuentaOt((String) objects[10]);
        requisicionVO.setGerencia((String) objects[11]);
        requisicionVO.setMontoPesos((Double) objects[12]);
        requisicionVO.setMontoTotalDolares((Double) objects[13]);
        return requisicionVO;
    }
    //Consultas para historial de requisiciones

    public List<RequisicionVO> buscarRequisicionFiltroPorPalabra(String idSesion, int idBloque, String referencia) {
        List<Object[]> lo;
        List<RequisicionVO> lr = null;
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT r.id,  r.consecutivo,  r.FECHA_SOLICITO,  r.FECHA_REQUERIDA, c.siglas, r.proveedor,  e.nombre , r.referencia, u.nombre, "); //7
        sb.append(" pot.nombre, pot.cuenta_contable, g.nombre, r.monto_mn, r.montototal_usd");
        sb.append(" FROM Requisicion r");
        sb.append(" inner join proyecto_ot pot on r.proyecto_ot = pot.id");
        sb.append(" inner join gerencia g on r.gerencia = g.id");
        sb.append("     inner join compania c on r.COMPANIA = c.RFC");
        sb.append("     inner join ESTATUS e on r.ESTATUS = e.id");
        sb.append("     inner join USUARIO u on r.SOLICITA = u.id");
        sb.append("     inner join REQUISICION_DETALLE rd on r.id =  rd.REQUISICION and upper(rd.DESCRIPCION_SOLICITANTE) like upper('%").append(referencia).append("%')");
        sb.append(" where rd.autorizado = '").append(Constantes.BOOLEAN_TRUE).append("'");
        sb.append(" and  '").append(idSesion).append("' in (r.solicita, r.revisa, r.aprueba,  r.asigna, r.visto_bueno)");
        sb.append(" and r.estatus >= ").append(Constantes.REQUISICION_SOLICITADA);
        sb.append(" and r.ESTATUS <> ").append(Constantes.REQUISICION_CANCELADA);

        //sb.append(" and r.solicita = '").append(idSesion).append("'");
        sb.append(" and r.ap_campo = ").append(idBloque);
        sb.append(" and r.compania = c.rfc");
        sb.append(" and r.estatus = e.id ");
        sb.append(" order by r.consecutivo asc");
        lo = em.createNativeQuery(sb.toString()).getResultList();
        if (lo != null) {
            lr = new ArrayList<RequisicionVO>();
            for (Object[] objects : lo) {
                lr.add(castRequisicion(objects));
            }
        }
        return lr;
    }

    private RequisicionVO castRequisicionConDetalle(Object[] obj, boolean conDetalle, boolean seleccionado) {
        try {
            RequisicionVO requisicionVO = new RequisicionVO();
            requisicionVO.setId((Integer) obj[0]);
            requisicionVO.setConsecutivo((String) obj[1]);
            requisicionVO.setRfcCompania((String) obj[2]);
            requisicionVO.setCompania((String) obj[3]);
            requisicionVO.setIdStatus((Integer) obj[4]);
            requisicionVO.setEstatus((String) obj[5]);
            requisicionVO.setFechaRequerida((Date) obj[6]);
            requisicionVO.setLugarEntrega((String) obj[7]);
            requisicionVO.setIdGerencia((Integer) obj[8]);
            requisicionVO.setGerencia((String) obj[9]);
            requisicionVO.setIdProyectoOT((Integer) obj[10]);
            requisicionVO.setProyectoOT((String) obj[11]);

            requisicionVO.setIdTipoObra((Integer) obj[12] != null ? (Integer) obj[12] : 0);
            requisicionVO.setTipoObra((String) obj[13] != null ? (String) obj[13] : "");
            requisicionVO.setProveedor((String) obj[14]);
            requisicionVO.setObservaciones((String) obj[15]);
            requisicionVO.setReferencia((String) obj[16]);
            // revisa
            requisicionVO.setIdSolicita((String) obj[17]);
            requisicionVO.setSolicita((String) obj[18]);
            //
            requisicionVO.setIdRevisa((String) obj[19]);
            requisicionVO.setRevisa((String) obj[20]);
            //
            requisicionVO.setIdAprueba((String) obj[21]);
            requisicionVO.setAprueba((String) obj[22]);
            //
            requisicionVO.setIdAsigna((String) obj[23]);
            requisicionVO.setAsigna((String) obj[24]);
            //
            requisicionVO.setIdComprador((String) obj[25]);
            requisicionVO.setComprador((String) obj[26]);
            //
            //echasjava.lang.String
            requisicionVO.setFechaSolicitada((Date) obj[27]);
            requisicionVO.setHoraSolicitada((Date) obj[28]);
            requisicionVO.setFechaRevisa((Date) obj[29]);
            requisicionVO.setHoraRevisa((Date) obj[30]);
            requisicionVO.setFechaAprueba((Date) obj[31]);
            requisicionVO.setHoraAprueba((Date) obj[32]);
            requisicionVO.setFechaAsignada((Date) obj[33]);
            requisicionVO.setHoraAsignada((Date) obj[34]);
            //
            requisicionVO.setIdBloque((Integer) obj[35]);
            requisicionVO.setBloque((String) obj[36]);
            //
            requisicionVO.setMontoPesos((Double) obj[37]);
            requisicionVO.setMontoDolares((Double) obj[38]);
            requisicionVO.setMontoTotalDolares((Double) obj[39]);

            requisicionVO.setIdPrioridad((Integer) obj[40]);
            requisicionVO.setPrioridad((String) obj[41]);
            //
//
            requisicionVO.setTipo((String) obj[42] != null ? (String) obj[42] : "");
            requisicionVO.setCheckCode((String) obj[43] != null ? (String) obj[43] : "");
            requisicionVO.setUrl((String) obj[44] != null ? (String) obj[44] : "");
            requisicionVO.setIdUnidadCosto((Integer) obj[45] != null ? (Integer) obj[45] : 0);
            requisicionVO.setUnidadCosto((String) obj[46] != null ? (String) obj[46] : "");

            //cfdi
            requisicionVO.setIdCfdi((Integer) obj[47] != null ? (Integer) obj[47] : 0);
            requisicionVO.setCodigoCfdi((String) obj[48] != null ? (String) obj[48] : "");
            requisicionVO.setNombreCfdi((String) obj[49] != null ? (String) obj[49] : "");

            //visto bueno  costos
            requisicionVO.setIdVistoBueno((String) obj[50] != null ? (String) obj[50] : "");
            requisicionVO.setVistoBueno((String) obj[51] != null ? (String) obj[51] : "");
            requisicionVO.setFechaVistoBueno((Date) obj[52]);
            requisicionVO.setHoraVistoBueno((Date) obj[53]);

            //nueva
            requisicionVO.setNueva(obj[54] != null ? (Boolean) obj[54] : false);

            List<RequisicionDetalleVO> lrd = null;
            List<AdjuntoVO> lra = null;
            List<NotaVO> lrn = null;
            if (conDetalle) {
                lrd = requisicionDetalleServicioRemoto.getItemsPorRequisicionConsultaNativa(requisicionVO.getId(), false, seleccionado);
                //lra = reRequisicionEtsRemote.traerAdjuntosPorRequisicion(idRequisicion);
                lrn = notaRequisicionRemote.getNotasPorRequisicion(requisicionVO.getId());
            }
            requisicionVO.setListaDetalleRequision(lrd);
            return requisicionVO;

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    public long totalRequisionesPendienteDesdeAniosAnterior(String idUsuario, int idBloque, int anio, int DIAS_ANTICIPADOS, int ESTATUS_ASIGNADA) {
        StringBuilder sb = new StringBuilder();

        sb.append("select count(*) "); //7
        sb.append(" FROM Requisicion r");
        sb.append(" where r.FECHA_ASIGNO <= (SELECT CURRENT_DATE - ").append(DIAS_ANTICIPADOS).append(")");
        sb.append(" and extract(year from r.FECHA_ASIGNO) <  ").append(anio);
        sb.append(" and r.ap_campo = ").append(idBloque);
        sb.append(" and r.COMPRA = '").append(idUsuario).append("'");//(select u.id from USUARIO u where u.NOMBRE = '").append(nombre).append("' and u.ELIMINADO = false)");
        sb.append(" and r.ESTATUS = ").append(ESTATUS_ASIGNADA);
        return (Long) em.createNativeQuery(sb.toString()).getSingleResult();
    }

    public long totalRequisionesPorMes(String idUsuario, int idBloque, int numMes, int DIAS_ANTICIPADOS, int ESTATUS_ASIGNADA, int anio) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "select count(*) FROM Requisicion r"
                + " where r.FECHA_ASIGNO <= (SELECT CURRENT_DATE - ")
                .append(DIAS_ANTICIPADOS).append(")")
                .append(" and extract(month from r.FECHA_ASIGNO) = ").append(numMes)
                .append(" and extract(year from r.FECHA_ASIGNO) =  ").append(anio)
                .append(" and r.ap_campo = ").append(idBloque)
                .append(" and r.COMPRA = '").append(idUsuario)
                .append("' and r.ESTATUS = ").append(ESTATUS_ASIGNADA);
        //
        return (Long) em.createNativeQuery(sb.toString()).getSingleResult();
    }

    public boolean pasarRequisiciones(List<RequisicionVO> lo, int idStatus, String usuarioTiene, String usuarioAprobara, String idSesion,
            String rfcEmpresa, String mailSesion) {

        Usuario para = usuarioRemote.find(usuarioAprobara);
        Usuario cc = usuarioRemote.find(usuarioTiene);
        String status = traduccionStatus(idStatus);

        boolean v
                = notificacionRequisicionRemote.enviarCorreoCambioRequisiciones(
                        para.getEmail(),
                        cc.getEmail(),
                        mailSesion,
                        lo,
                        cc.getNombre(),
                        para.getNombre(),
                        rfcEmpresa,
                        status
                );

        if (v) {
            for (RequisicionVO requisicionVO : lo) {
                Requisicion requisicion = find(requisicionVO.getId());
                switch (idStatus) {
                    case Constantes.REQUISICION_PENDIENTE:
                        requisicion.setSolicita(usuarioRemote.find(usuarioAprobara));
                        break;
                    case Constantes.REQUISICION_SOLICITADA:
                        requisicion.setRevisa(usuarioRemote.find(usuarioAprobara));
                        break;
                    case Constantes.REQUISICION_REVISADA:
                        requisicion.setAprueba(usuarioRemote.find(usuarioAprobara));
                        break;
                    default:
                        requisicion.setCompra(usuarioRemote.find(usuarioAprobara));
                        break;
                }
                requisicion.setModifico(usuarioRemote.find(idSesion));
                requisicion.setFechaModifico(new Date());
                requisicion.setHoraModifico(new Date());
                edit(requisicion);
            }
        }
        return v;
    }

    private String traduccionStatus(int idStatus) {
        String retVal;

        switch (idStatus) {
            case Constantes.REQUISICION_SOLICITADA: //10
                retVal = " Revisar";
                break;
            case Constantes.REQUISICION_REVISADA: //110
                retVal = " Aprobar";
                break;
            default:
                retVal = " Procesar ";
                break;
        }
        return retVal;
    }

    public List<RequisicionVO> requisicionesSinVistoBueno(String idSesion, int idCampo, String tipo, int rolUsuario) {
        List<RequisicionVO> lr = null;
        try {
            clearQuery();
            query.append(
                    "SELECT r.ID,r.consecutivo, r.referencia, r.FECHA_SOLICITO, g.NOMBRE as gerencia, pot.nombre as proyectoOT,"
                    + "uc.NOMBRE as costo, r.RECHAZADA, (select FECHA from RECHAZO where REQUISICION = r.id order by fecha desc  limit 1) "
                    + ", r.compania, uso.id, uso.nombre, uso.codigo"
                    + " FROM Requisicion r"
                    + "     inner join GERENCIA g on r.GERENCIA = g.id"
                    + "     inner join PROYECTO_OT pot on r.PROYECTO_OT  = pot.id"
                    + "     left join OC_USO_CFDI uso on r.oc_uso_cfdi  = uso.id"
                    + "     left join OC_UNIDAD_COSTO uc on r.OC_UNIDAD_COSTO = uc.id"
                    + " WHERE r.ESTATUS = "
            ).append(Constantes.REQUISICION_VISTO_BUENO_C)
                    .append(" and '").append(idSesion)
                    .append(
                            "' in ( select u.id from USUARIO u "
                            + " inner join AP_CAMPO_USUARIO_RH_PUESTO cap on cap.USUARIO = u.id "
                            + " inner join SI_USUARIO_ROL ur on ur.USUARIO = u.id and ur.ELIMINADO = '"
                    ).append(Constantes.BOOLEAN_FALSE)
                    .append("' "
                            + " inner join SI_ROL rol on ur.SI_ROL = rol.ID and rol.ID = "
                    ).append(rolUsuario)
                    .append(" and rol.SI_MODULO = ").append(Constantes.MODULO_REQUISICION)
                    .append(" where u.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("')")
                    .append(" and r.AP_CAMPO = ").append(idCampo)
                    .append(" and r.TIPO = '").append(tipo).append("'")
                    .append(" ORDER BY g.nombre ASC, r.FECHA_SOLICITO DESC ");

            RequisicionVO o;
            //
            List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
            if (!l.isEmpty()) {
                lr = new ArrayList<>();
                for (Object[] objects : l) {
                    o = new RequisicionVO();
                    o.setId((Integer) objects[0]);
                    o.setConsecutivo(String.valueOf(objects[1]));
                    o.setReferencia(String.valueOf(objects[2]));
                    o.setFechaSolicitada((Date) objects[3]);
                    o.setGerencia((String) objects[4]);
                    o.setProyectoOT((String) objects[5]);
                    o.setUnidadCosto((String) objects[6]);
                    o.setRechazada((Boolean) objects[7]);
                    if (objects[8] != null) {
                        o.setUltimoRechazo((Date) objects[8]);
                    }
                    o.setCompania((String) objects[9]);
                    o.setIdCfdi(objects[10] != null ? (Integer) objects[10] : Constantes.CERO);
                    o.setNombreCfdi((String) objects[11]);
                    o.setCodigoCfdi((String) objects[12]);
                    lr.add(o);
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lr = null;
        }

        return lr;
    }

    public List<RequisicionVO> requisicionesPorEstatus(String idSesion, int idCampo, String tipo, int status) {
        List<RequisicionVO> lr = null;

        try {
            clearQuery();
            query.append(
                    "SELECT r.ID,r.consecutivo, r.referencia, r.FECHA_SOLICITO, g.NOMBRE as gerencia, pot.nombre as proyectoOT,uc.NOMBRE as costo, u.nombre"
                    + " FROM Requisicion r"
                    + " inner join GERENCIA g on r.GERENCIA = g.id"
                    + " inner join PROYECTO_OT pot on r.PROYECTO_OT  = pot.id"
                    + " left join OC_UNIDAD_COSTO uc on r.OC_UNIDAD_COSTO = uc.id"
                    + " inner join usuario u on r.solicita = u.id"
                    + " WHERE r.ESTATUS = "
            ).append(status)
                    .append(" and r.AP_CAMPO = ").append(idCampo)
                    .append(" and r.TIPO = '").append(tipo)
                    .append("' and r.solicita <> '").append(Constantes.USUARIO_PRUEBA)
                    .append("' ORDER BY r.ID ASC");

            RequisicionVO o;

            List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
            if (!l.isEmpty()) {
                lr = new ArrayList<RequisicionVO>();
                for (Object[] objects : l) {
                    o = new RequisicionVO();
                    o.setId((Integer) objects[0]);
                    o.setConsecutivo(String.valueOf(objects[1]));
                    o.setReferencia(String.valueOf(objects[2]));
                    o.setFechaSolicitada((Date) objects[3]);
                    o.setGerencia((String) objects[4]);
                    o.setProyectoOT((String) objects[5]);
                    o.setUnidadCosto((String) objects[6]);
                    o.setSolicita((String) objects[7]);
                    lr.add(o);
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lr = null;
        }

        return lr;
    }

    public List<RequisicionVO> traerRequisicionSinSolicitar(int idCampo, int diasAnticipados) {
        clearQuery();

        Date fecha = siManejoFechaLocal.fechaRestarDias(new Date(), diasAnticipados);
        String fCad = Constantes.FMT_yyyyMMdd.format(fecha);
        List<RequisicionVO> lr = null;

        try {
            query.append(
                    "SELECT u.NOMBRE, count(r.ID)"
                    + " FROM Requisicion r"
                    + " left join usuario u on r.compra = u.id"
                    + " WHERE r.ESTATUS = "
            ).append(Constantes.REQUISICION_ASIGNADA)
                    .append(" and r.AP_CAMPO = ").append(idCampo)
                    .append(" and r.compra <> '").append(Constantes.USUARIO_PRUEBA)
                    .append("' and r.fecha_asigno <= cast('").append(fCad)
                    .append("' as date) and u.eliminado = '").append(Constantes.NO_ELIMINADO)
                    .append(
                            "' group by u.NOMBRE "
                            + " having count(r.id) > 0"
                            + " order by u.NOMBRE asc"
                    );

            RequisicionVO o;

            List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();

            if (!l.isEmpty()) {
                lr = new ArrayList<RequisicionVO>();
                for (Object[] objects : l) {
                    o = new RequisicionVO();
                    o.setComprador((String) objects[0]);
                    o.setTotal((Integer) objects[1]);
                    lr.add(o);
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lr = null;
        }
        return lr;
    }

    public List<RequisicionVO> traerRequisicionSinSolicitarPorUsuaario(int idCampo, int diasAnticipados, String comprador) {
        clearQuery();
        Date fecha = siManejoFechaLocal.fechaRestarDias(new Date(), diasAnticipados);
        String fCad = Constantes.FMT_yyyyMMdd.format(fecha);
        List<RequisicionVO> lr = null;
        try {
            query.append(consultaRequisicion())
                    .append(" WHERE r.eliminado = false")
                    .append(" and r.ESTATUS = ").append(Constantes.REQUISICION_ASIGNADA)
                    .append(" and r.AP_CAMPO = ").append(idCampo)
                    .append(" and r.compra = '").append(comprador)
                    .append("' and r.fecha_asigno <= cast('").append(fCad)
                    .append("' as date) order by r.FECHA_SOLICITO desc");
            //     

            List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
            if (l != null) {
                lr = new ArrayList<RequisicionVO>();
                for (Object[] objects : l) {
                    lr.add(castRequisicionConDetalle(objects, Constantes.FALSE, Constantes.FALSE));
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lr = null;
        }
        return lr;
    }

    public List<RequisicionVO> traerPorRangoEstado(int idCampo, int solicitada, int vistoBueno) {
        clearQuery();
        query.append("select r.CONSECUTIVO, g.nombre, r.REFERENCIA, r.LUGAR_ENTREGA, u.nombre as Solicita, e.NOMBRE, r.FECHA_SOLICITO, r.monto_mn, r.monto_usd");
        query.append(" from REQUISICION r ");
        query.append(" inner join USUARIO u on r.SOLICITA = u.id");
        query.append(" inner join ESTATUS e on r.ESTATUS = e.ID");
        query.append(" inner join GERENCIA g on r.GERENCIA = g.ID");
        query.append(" where r.eliminado = false and r.ESTATUS between ").append(solicitada).append(" and ").append(vistoBueno).append(" and r.AP_CAMPO = ").append(idCampo);
        query.append(" order by r.consecutivo asc");
        //
        List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
        List<RequisicionVO> lr = null;
        if (l != null) {
            lr = new ArrayList<RequisicionVO>();
            for (Object[] objects : l) {
                lr.add(castRequisicionProceso(objects));
            }

        }
        return lr;
    }

    private RequisicionVO castRequisicionProceso(Object[] objects) {
        RequisicionVO requisicionVO = new RequisicionVO();
        requisicionVO.setConsecutivo((String) objects[0]);
        requisicionVO.setGerencia((String) objects[1]);
        requisicionVO.setReferencia((String) objects[2]);
        requisicionVO.setLugarEntrega((String) objects[3]);
        requisicionVO.setSolicita((String) objects[4]);
        requisicionVO.setEstatus((String) objects[5]);
        requisicionVO.setFechaSolicitada((Date) objects[6]);
        requisicionVO.setMontoPesos((Double) objects[7]);
        requisicionVO.setMontoDolares((Double) objects[8]);
        return requisicionVO;
    }

    public RequisicionVO buscarRequisicionPorUsuario(String consecutivo, int idCampo, String usuario, boolean detalle, boolean seleccionado) {
        RequisicionVO r = null;
        try {
            boolean v = siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_REQUISICION, Constantes.CODIGO_ROL_CONS_REQ, idCampo);
            StringBuilder sb = new StringBuilder(225);
            sb.append(consultaRequisicion())
                    .append(" where r.CONSECUTIVO = '")
                    .append(consecutivo).append("' and  r.AP_CAMPO = ").append(idCampo)
                    .append(" and (select count(id) from REQUISICION_DETALLE where REQUISICION = r.ID and INV_ARTICULO is not null and multiproyecto_id is null) > 0 ");
            if (!v) {
                sb.append(" and '").append(usuario).append("' in (r.solicita, r.revisa, r.aprueba, r.asigna, r.compra, r.visto_bueno)");
            }
            UtilLog4j.log.info(this, "Consulta: " + sb.toString());
            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();
            if (obj != null) {
                r = castRequisicionConDetalle(obj, detalle, seleccionado);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return r;
    }

    private String consultaRequisicion() {
//	StringBuilder sb = new StringBuilder(1200);
//	sb.append(
        return " select r.id, r.CONSECUTIVO,c.RFC, c.nombre, e.id, e.NOMBRE, r.FECHA_REQUERIDA, "
                + " r.LUGAR_ENTREGA, g.ID, g.NOMBRE, pot.id, pot.NOMBRE, o.id, o.NOMBRE, r.PROVEEDOR, r.OBSERVACIONES, r.referencia "
                + " ,u.id, u.nombre, ur.id, ur.nombre, ua.id, ua.nombre, uasi.id,uasi.nombre, ucomp.id, ucomp.nombre, "
                + " r.fecha_solicito, r.hora_solicito, r.fecha_reviso, r.hora_reviso,r.fecha_aprobo,"
                + " r.hora_aprobo,r.fecha_asigno, r.hora_asigno, cp.id, cp.nombre"
                + " , r.monto_mn, r.monto_usd, r.montoTotal_usd, pri.id, pri.nombre, r.tipo, r.checkcode, r.url, r.oc_unidad_costo, uc.nombre "
                + " ,cfdi.id as id_cdfi, cfdi.codigo, cfdi.nombre as cfdi,"
                + " vobo.id, vobo.nombre as visto_bueno,r.fecha_visto_bueno,r.hora_visto_bueno,"
                + " r.nueva"
                + " from REQUISICION r "
                + " LEFT JOIN COMPANIA c ON r.COMPANIA = c.rfc "
                + " LEFT JOIN ESTATUS e ON r.ESTATUS = e.id "
                + " LEFT JOIN GERENCIA g ON r.GERENCIA = g.id"
                + " LEFT JOIN PROYECTO_OT pot ON r.PROYECTO_OT = pot.id"
                + " LEFT JOIN TIPO_OBRA o ON r.TIPO_OBRA = o.id"
                + " LEFT JOIN USUARIO u ON r.SOLICITA = u.id"
                + " LEFT JOIN USUARIO ur ON r.REVISA = ur.id"
                + " LEFT JOIN USUARIO ua ON r.APRUEBA = ua.id"
                + " LEFT JOIN USUARIO uasi ON r.ASIGNA = uasi.id"
                + " LEFT JOIN USUARIO ucomp ON r.COMPRA = ucomp.id"
                + " LEFT JOIN USUARIO vobo ON r.visto_bueno = vobo.id"
                + " LEFT JOIN AP_CAMPO cp ON r.ap_campo = cp.id"
                + " LEFT JOIN PRIORIDAD pri ON r.PRIORIDAD = pri.id"
                + " LEFT JOIN oc_unidad_costo uc ON r.oc_unidad_costo = uc.id"
                + "  LEFT JOIN oc_uso_cfdi cfdi ON r.oc_uso_cfdi = cfdi.id ";
//        );
//	return sb.toString();
    }

    public List<RequisicionVO> traerRequisicionPorGerencia(int status, int campo, int gerencia, String inicio, String fin) {
        StringBuilder sb = new StringBuilder(700);
        List<Object[]> lo;
        List<RequisicionVO> lr = null;
        sb.append(
                "SELECT r.id,  r.consecutivo,  r.FECHA_SOLICITO,  r.FECHA_REQUERIDA, c.siglas, r.proveedor,  e.nombre , r.referencia, u.nombre, " //7
                + " pot.nombre, pot.cuenta_contable, g.nombre, r.monto_mn, r.montototal_usd"
                + " FROM Requisicion r"
                + " inner join proyecto_ot pot on r.proyecto_ot = pot.id"
                + " inner join gerencia g on r.gerencia = g.id"
                + "     inner join compania c on r.COMPANIA = c.RFC"
                + "     inner join ESTATUS e on r.ESTATUS = e.id"
                + "     inner join USUARIO u on r.SOLICITA = u.id"
                + " where r.ap_campo = "
        ).append(campo)
                .append(" and r.estatus >= ").append(status)
                .append(" and r.gerencia = ").append(gerencia)
                .append(" and r.ESTATUS <> ").append(Constantes.REQUISICION_CANCELADA)
                .append(" and r.fecha_solicito between cast('").append(inicio)
                .append("' as date) and cast('").append(fin).append("' as date)"
                + " order by r.consecutivo asc");
        //   UtilLog4j.log.info(this,"Q: " + sb.toString());
        lo = em.createNativeQuery(sb.toString()).getResultList();
        if (lo != null) {
            lr = new ArrayList<RequisicionVO>();
            for (Object[] objects : lo) {
                lr.add(castRequisicion(objects));
            }
        }
        return lr;
    }

    public void cambiarAnalistaRequisicion(String sesion, int idRequisicion, String compra) {
        Requisicion r = find(idRequisicion);
        r.setCompra(usuarioRemote.find(compra));
        r.setModifico(usuarioRemote.find(sesion));
        r.setFechaModifico(new Date());
        r.setHoraModifico(new Date());
        edit(r);
    }

    public long totalRequisicionesPendientes(String idUsuarioSesion, int status) {
        int total = 0;
        // requisiciones sin solicitar
        String sql
                = "SELECT count(*) AS cuantos\n"
                + "FROM requisicion r\n"
                + "WHERE r.estatus = ?\n"
                + "   AND r.solicita = ?\n"
                + "   AND r.eliminado = false";

        total += dbCtx
                .fetchOne(sql, Constantes.REQUISICION_PENDIENTE, idUsuarioSesion)
                .into(Integer.class);

        // sin visto bueno de costos
        sql
                = "SELECT count(req.id) AS cuantos\n"
                + "FROM Requisicion req\n"
                + "   INNER JOIN si_usuario_rol ur ON req.ap_campo = ur.ap_campo \n"
                + "       AND ur.usuario = ?\n"
                + "       AND ur.si_rol = ?\n"
                + "       AND ur.eliminado = false\n"
                + "   INNER JOIN si_rol r ON ur.si_rol = r.id AND r.si_modulo = ?\n"
                + "   INNER JOIN usuario u ON ur.usuario = u.id AND u.eliminado = false\n"
                + "WHERE req.ESTATUS = ?\n"
                + "   and req.tipo = ?";

        total += dbCtx
                .fetchOne(sql,
                        idUsuarioSesion,
                        Constantes.ROL_VISTO_BUENO_COSTO,
                        Constantes.MODULO_REQUISICION,
                        Constantes.REQUISICION_VISTO_BUENO_C,
                        TipoRequisicion.PS.name()
                )
                .into(Integer.class);

        // sin visto bueno de contabilidad
        total += dbCtx
                .fetchOne(sql,
                        idUsuarioSesion,
                        Constantes.ROL_VISTO_BUENO_CONTABILIDAD,
                        Constantes.MODULO_REQUISICION,
                        Constantes.REQUISICION_VISTO_BUENO_C,
                        TipoRequisicion.AF.name()
                )
                .into(Integer.class);

        // sin revisar
        sql
                = "SELECT count(*) AS cuantos\n"
                + "FROM requisicion r\n"
                + "WHERE r.estatus = ?\n"
                + "   AND r.revisa = ?\n"
                + "   AND r.eliminado = false";

        total += dbCtx
                .fetchOne(sql, Constantes.REQUISICION_SOLICITADA, idUsuarioSesion)
                .into(Integer.class);

        // sin aprobar
        sql
                = "SELECT count(*) AS cuantos\n"
                + "FROM requisicion r\n"
                + "WHERE r.estatus = ?\n"
                + "   AND r.aprueba = ?\n"
                + "   AND r.eliminado = false";

        total += dbCtx
                .fetchOne(sql, Constantes.REQUISICION_REVISADA, idUsuarioSesion)
                .into(Integer.class);

        // FIXME : requisiciones sin asignar
        sql
                = "SELECT count(*) AS cuantos\n"
                + "FROM requisicion r\n"
                + "   INNER JOIN si_usuario_rol ur ON r.ap_campo = ur.ap_campo \n"
                + "       AND ur.usuario = ?\n"
                + "	AND ur.si_rol = ?\n"
                + "	AND ur.eliminado = false\n"
                + "WHERE 1 = 1\n"
                + "   AND r.estatus = ?\n"
                + "   AND r.eliminado = false\n"
                + "   AND r.solicita <> 'PRUEBA'\n";

        total += dbCtx
                .fetchOne(sql,
                        idUsuarioSesion,
                        Constantes.ROL_ASIGNA_REQUISICION,
                        //TODO : originalmente tenía REQUISICION_VISTO_BUENO, pero no mostraba datos en el widget, revisar
                        Constantes.REQUISICION_APROBADA
                )
                .into(Integer.class);

        // sin disgregar
        sql
                = "SELECT count(*) AS cuantos\n"
                + "FROM requisicion r \n"
                + "WHERE r.estatus =  ?\n"
                + "   AND r.compra = ?\n"
                + "   AND r.eliminado = false";

        total += dbCtx
                .fetchOne(sql, Constantes.REQUISICION_ASIGNADA, idUsuarioSesion)
                .into(Integer.class);

        return total;
    }

    public long totalRequisicionesPorCampo(String sesion, int campo) {

        int total = 0;
        try {
            total += getTotalRequisicionesSinRevisar(sesion, campo);
            total += getTotalRequisicionesSinAprobar(sesion, campo);
            total += getTotalRequisicionesSinAsignar(sesion, campo, Constantes.REQUISICION_VISTO_BUENO, Constantes.ROL_ASIGNA_REQUISICION);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return total;
    }

    public List<RequisicionVO> totalReqOcsSinProcesar(int idCampo, int diasAnticipados) {
        Date fecha = siManejoFechaLocal.fechaRestarDias(new Date(), diasAnticipados);
        String fCad = Constantes.FMT_yyyyMMdd.format(fecha);
        List<RequisicionVO> lr = null;
        try {
            String c = "WITH Compradores  as(\n"
                    + "SELECT u.NOMBRE, (select count(r.id) from REQUISICION r where r.eliminado = false and r.ESTATUS = ?1 and r.COMPRA  = u.id\n"
                    + "				and r.AP_CAMPO = ?2 \n"
                    + "	    			 and r.fecha_asigno <= ?3 \n"
                    + "	    			 and r.COMPRA <> 'PRUEBA') as totalReq, \n"
                    + "			  (SELECT count(ao.id) from AUTORIZACIONES_ORDEN ao \n"
                    + "			  	inner join orden o on ao.ORDEN = o.id and o.ELIMINADO = false and o.AP_CAMPO = ?4 \n"
                    + "			  where ao.ESTATUS = ?5 and o.ANALISTA = u.id and ao.ELIMINADO =false\n"
                    + "			  ) as totalOcs \n"
                    + "from USUARIO  u\n"
                    + "	 inner join SI_USUARIO_ROL ur on ur.USUARIO = u.id\n"
                    + "where ur.AP_CAMPO = ?6 \n"
                    + " and u.ELIMINADO =false\n"
                    + "and ur.ELIMINADO = false\n"
                    + "and ur.SI_ROL = ?7 \n"
                    + "group by u.id, u.NOMBRE\n"
                    + "order by u.NOMBRE \n"
                    + ")\n"
                    + "select * from Compradores c \n"
                    + "	where c.totalReq > 0 or c.totalOcs > 0";

            Query q
                    = em.createNativeQuery(c)
                            .setParameter(1, Constantes.REQUISICION_ASIGNADA)
                            .setParameter(2, idCampo)
                            .setParameter(3, fecha)
                            .setParameter(4, idCampo)
                            .setParameter(5, Constantes.ORDENES_SIN_SOLICITAR)
                            .setParameter(6, idCampo)
                            .setParameter(7, Constantes.ROL_COMPRADOR);

            RequisicionVO o;
            List<Object[]> l = q.getResultList();
            if (l != null) {
                lr = new ArrayList<>();
                for (Object[] objects : l) {
                    o = new RequisicionVO();
                    o.setComprador((String) objects[0]);
                    o.setTotal((Long) objects[1]);
                    o.setTotalItems((Long) objects[2]);
                    lr.add(o);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lr;
    }

    public SiOpcionVo totalRevPagina(String usuario, int campo, int status) {
        String s = " SELECT o.nombre, o.pagina, count(r.id) AS total "
                + "FROM si_opcion o \n"
                + "\tinner join REQUISICION r on r.ESTATUS = o.ESTATUS_CONTAR \n"
                + " where     o.ELIMINADO =false \n"
                + "\tand o.SI_MODULO =  " + Constantes.MODULO_REQUISICION + " \n"
                + condicionStatus(status, usuario, campo)
                + "\tand r.ap_campo =  " + campo + " \n"
                + "GROUP by o.nombre, o.pagina "
                + "HAVING count(r.id) > 0";

        SiOpcionVo opcionVo = new SiOpcionVo();

        try {

            Record result = dbCtx.fetchOne(s);

            if (result != null) {
                opcionVo = result.into(SiOpcionVo.class);
            }

        } catch (Exception e) {
            UtilLog4j.log.warn(e);

            opcionVo = null;
        }

        return opcionVo;
    }

    private String condicionStatus(int status, String sesion, int campo) {
        String con = " and r.estatus = " + status;
        switch (status) {
            case Constantes.REQUISICION_SOLICITADA:
                con += " and r.revisa = '" + sesion + "'";
                break;
            case Constantes.REQUISICION_REVISADA:
                con += " and r.aprueba = '" + sesion + "'";
                break;
            case Constantes.REQUISICION_ASIGNADA:
                con += " and r.compra = '" + sesion + "'";
                break;
            case Constantes.REQUISICION_VISTO_BUENO:
                con += " and '" + sesion + "' in ( select ur.usuario from  SI_USUARIO_ROL ur where ur.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
                        + " and ur.si_rol in (" + Constantes.ROL_VISTO_BUENO_CONTABILIDAD + ") and ur.ap_campo = " + campo + ")";
                break;
            case Constantes.REQUISICION_VISTO_BUENO_C:
                con += " and '" + sesion + "' in ( select ur.usuario from  SI_USUARIO_ROL ur where ur.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
                        + " and ur.si_rol in (" + Constantes.ROL_VISTO_BUENO_COSTO + ") and ur.ap_campo = " + campo + ")";
                break;
            case Constantes.REQUISICION_APROBADA:
                con += " and r.solicita <> 'PRUEBA'  and '" + sesion + "' in ( select ur.usuario from  SI_USUARIO_ROL ur where ur.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
                        + " and ur.si_rol = " + Constantes.ROL_ASIGNA_REQUISICION + " and ur.ap_campo = " + campo + ")";
                break;
            default:
                con += " and r.solicita = '" + sesion + "'";
                break;
        }
        return con;
    }

    public List<SiOpcionVo> totalRevPagina(String id, int idCampo, List<RequisicionEstadoEnum> estadosReq) {
        List<SiOpcionVo> lo = new ArrayList<>();
        for (RequisicionEstadoEnum requisicionEstadoEnum : estadosReq) {
            SiOpcionVo so = totalRevPagina(id, idCampo, requisicionEstadoEnum.getId());
            if (so != null && so.getTotal() > 0) {
                so.setIdCampo(idCampo);
                lo.add(so);
            }
        }
        return lo;
    }

    public Requisicion buscarPorConsecutivoBloque(String consecutivo, String usuario) {
        Requisicion r = null;
        try {

            StringBuilder sb = new StringBuilder();
            sb.append("select * from requisicion r where r.eliminado = false and r.consecutivo = '").append(consecutivo).append("'");
            UtilLog4j.log.info(this, "Consulta por id usuario: " + sb.toString());
            r = buscarPorConsecutivo(consecutivo);
            boolean v = siUsuarioRolRemote.buscarRolPorUsuarioModulo(usuario, Constantes.MODULO_REQUISICION, Constantes.CODIGO_ROL_CONS_REQ, r.getApCampo().getId());
            if (!v) {
                if (!(r.getSolicita().getId().equals(usuario)
                        || r.getRevisa().getId().equals(usuario)
                        || r.getAprueba().getId().equals(usuario)
                        || r.getAsigna().getId().equals(usuario))) {
                    r = null;
                }
            }
        } catch (Exception e) {
            r = null;
            UtilLog4j.log.info(this, "Requision no encontrada por consecutivo: " + e.getMessage(), e);
        }

        return r;
    }

    public List<RequisicionVO> traerRequisicionesArticuloContrato(String sesion, int estatus, int campo) {
        List<RequisicionVO> lr = null;
        String sb = consultaRequisicion()
                + "     WHERE r.ESTATUS = " + estatus
                + "     AND r.contrato = 'True'"
                + "     AND r.COMPRA = '" + sesion + "'"
                + "     AND r.AP_CAMPO = " + campo
                + "     AND r.eliminado = false \n"
                + "     ORDER BY r.CONSECUTIVO ASC ";

        List<Object[]> l = em.createNativeQuery(sb).getResultList();
        if (l != null) {
            lr = new ArrayList<RequisicionVO>();
            for (Object[] objects : l) {
                lr.add(castRequisicionConDetalle(objects, Constantes.FALSE, Constantes.FALSE));
            }
        }
        return lr;
    }

    public List<RequisicionVO> traerRequisicionesSinContrato(String sesion, int estatus, int campo) {
        List<RequisicionVO> lr = null;
        String sb = consultaRequisicion()
                + "     WHERE r.ESTATUS = " + estatus
                + "     AND r.contrato <> 'True'"
                + "     AND r.COMPRA = '" + sesion + "'"
                + "     AND r.AP_CAMPO = " + campo
                + "     AND r.eliminado = false \n"
                + "     ORDER BY r.CONSECUTIVO ASC ";

        List<Object[]> l = em.createNativeQuery(sb).getResultList();
        if (l != null) {
            lr = new ArrayList<RequisicionVO>();
            for (Object[] objects : l) {
                lr.add(castRequisicionConDetalle(objects, Constantes.FALSE, Constantes.FALSE));
            }
        }
        return lr;
    }

    public void verificaArticuloItemTieneContrato(Requisicion requisicionActual, String sesion, int campo) {
        List<RequisicionDetalleVO> listaItem = requisicionDetalleServicioRemoto.getItemsAnalistaNativa(requisicionActual.getId(), false);
        String arts = "";
        for (RequisicionDetalleVO requisicionDetalleVO : listaItem) {
            if (arts.isEmpty()) {
                arts = String.valueOf(requisicionDetalleVO.getArtID());
            } else {
                arts += ", " + String.valueOf(requisicionDetalleVO.getArtID());
            }
        }
        //
        List<ConvenioArticuloVo> lc = cvConvenioArticuloLocal.buscarArticulosEnConvenio(arts, campo);
        if (lc != null && !lc.isEmpty()) {
            requisicionActual.setContrato(Constantes.BOOLEAN_TRUE);
        } else {
            requisicionActual.setContrato(Constantes.BOOLEAN_FALSE);
        }
        requisicionActual.setModifico(usuarioRemote.find(sesion));
        requisicionActual.setFechaModifico(new Date());
        requisicionActual.setHoraModifico(new Date());
        edit(requisicionActual);
    }

    public void finalizaRequisicion(String sesion, int req) {
        Requisicion requisicion = find(req);
        requisicion.setEstatus(estatusRemote.find(Constantes.REQUISICION_FINALIZADA)); // 60 = Finalizada
        requisicion.setModifico(usuarioRemote.find(sesion));
        requisicion.setFechaModifico(new Date());
        requisicion.setHoraModifico(new Date());
        edit(requisicion);
    }

    public RespuestaVo rechazar(Usuario sesion, RequisicionVO requisicionActual, String motivo) {
        // se toma la requisición y motivo de rechazo del panel emergente rechazar requisición
        final Requisicion requisicion = find(requisicionActual.getId());
        final Rechazo rechazo = new Rechazo();

        //Asigno requisicion fecha hora y usuario de rechazo
        rechazo.setMotivo(motivo);
        rechazo.setRequisicion(requisicion);
        rechazo.setFecha(new Date());
        rechazo.setHora(new Date());
        rechazo.setCumplido(Constantes.BOOLEAN_FALSE);
        rechazo.setRechazo(usuarioRemote.find(sesion.getId()));

        UsuarioVO uvo
                = usuarioRemote.traerResponsableGerencia(
                        requisicion.getApCampo().getId(),
                        Constantes.GERENCIA_ID_COMPRAS,
                        requisicion.getCompania().getRfc());

        System.out.println("mail: " + uvo.getMail());
        final boolean correoEnviado = notificacionRequisicionRemote.envioRechazoRequisicion(
                getDestinatarios(requisicion, sesion),
                "", uvo.getMail(),
                new StringBuilder().append("REQUISICIÓN: ").append(requisicion.getConsecutivo()).append(" DEVUELTA").toString(),
                requisicion,
                rechazo);

        if (correoEnviado) {
            //-- Marcar la requisicion como rechazada y regresarla al solicitante
            requisicion.setRechazada(Constantes.BOOLEAN_TRUE);
            requisicion.setEstatus(estatusRemote.find(Constantes.REQUISICION_PENDIENTE)); // 1 = Pendiente
            //-- Quitar las fechas y hora de operaciones realizadas
            requisicion.setFechaReviso(null);
            requisicion.setHoraReviso(null);
            requisicion.setFechaAprobo(null);
            requisicion.setHoraAprobo(null);
            requisicion.setFechaAutorizo(null);
            requisicion.setHoraAutorizo(null);
            requisicion.setFechaVistoBueno(null);
            requisicion.setHoraVistoBueno(null);
            //actualiza la requisición
            edit(requisicion);
            //actualizar rechazo
            crearRechazo(rechazo);
        }

        return RespuestaVo.builder().realizado(correoEnviado).build();

    }

    private String getDestinatarios(Requisicion requisicion, Usuario sesion) {
        StringBuilder destinatariosSB = new StringBuilder();
        //Verifica si la req es de costos o contabilidad

        String correoFinanza
                = traerCorreoAsigna(
                        requisicion.getTipo().equals(TipoRequisicion.AF)
                        ? Constantes.ROL_VISTO_BUENO_CONTABILIDAD
                        : Constantes.ROL_VISTO_BUENO_COSTO,
                        Constantes.MODULO_REQUISICION,
                        requisicion.getApCampo().getId()
                );

        switch (requisicion.getEstatus().getId()) {
            case Constantes.REQUISICION_SOLICITADA:
                destinatariosSB.append(requisicion.getSolicita().getDestinatarios());
                break;
            case Constantes.REQUISICION_REVISADA:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(requisicion.getRevisa().getEmail());
                destinatariosSB.append(',').append(correoFinanza);
                break;
            case Constantes.REQUISICION_APROBADA:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(requisicion.getRevisa().getEmail());
                destinatariosSB.append(',').append(requisicion.getAprueba().getEmail());
                destinatariosSB.append(',').append(correoFinanza);
                break;
            case Constantes.REQUISICION_VISTO_BUENO_C:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(sesion.getEmail());
                break;
            case Constantes.REQUISICION_VISTO_BUENO:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(requisicion.getRevisa().getEmail());
                destinatariosSB.append(',').append(requisicion.getAprueba().getEmail());
                destinatariosSB.append(',').append(correoFinanza);
                destinatariosSB.append(',').append(sesion.getEmail());
                break;
            case Constantes.REQUISICION_ASIGNADA:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(requisicion.getRevisa().getEmail());
                destinatariosSB.append(',').append(requisicion.getAprueba().getEmail());
                destinatariosSB.append(',').append(correoFinanza);
                destinatariosSB.append(',').append(sesion.getEmail());
                break;
            default:
                destinatariosSB.append(sesion.getEmail());
                break;
        }
        return destinatariosSB.toString();
    }

    private String traerCorreoAsigna(int rol, int modulo, int campo) {
        StringBuilder sb = new StringBuilder();
        try {
            List<UsuarioVO> lu = usuarioRemote.traerListaRolPrincipalUsuarioRolModulo(rol, modulo, campo);
            for (UsuarioVO usuarioVO : lu) {
                if (sb.length() == 0) {
                    sb.append(usuarioVO.getMail());
                } else {
                    sb.append(',').append(usuarioVO.getMail());
                }
                UtilLog4j.log.info(this, "Nombre asignar: " + usuarioVO.getNombre());
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al traer los correos de asiganción de orden " + e.getMessage(), e);
            sb = new StringBuilder(Constantes.VACIO);
        }
        return sb.toString();
    }

    public RespuestaVo cancelar(Usuario sesion, RequisicionVO requisicionVO, String motivo) {

        RespuestaVo.RespuestaVoBuilder respuesta = RespuestaVo.builder();

        try {
            final Requisicion requisicionActual = find(requisicionVO.getId());
            // se toma la requisición del panel emergente Cancelar requisición
            //Asigno fecha en que se cancela la requisiciòn
            requisicionActual.setFechaCancelo(new Date());
            requisicionActual.setHoraCancelo(new Date());
            requisicionActual.setCancelo(usuarioRemote.find(sesion.getId()));
            // Esto Sirve para probar la aplicaciòn
            final boolean correoEnviado = notificacionRequisicionRemote.envioCancelacionRequisicion(
                    getDestinatarios(requisicionActual, sesion),
                    "",
                    "",
                    new StringBuilder().append("REQUISICIÓN: ").append(requisicionActual.getConsecutivo()).append(" CANCELADA").toString(),
                    requisicionActual,
                    "cancelo");
            if (correoEnviado) {
                //Si mando el correo se actualiza la requisición
                requisicionActual.setEstatus(estatusRemote.find(Constantes.REQUISICION_CANCELADA)); // 50 = Cancelada
                edit(requisicionActual);
                //FIXME: Enviar notificacion movil

            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
            respuesta.realizado(false);
        }

        return respuesta.build();
    }

    public void vistoBuenoRequisicion(Usuario sesion, RequisicionVO requisicionVO) {
        Requisicion requiActual = find(requisicionVO.getId());
        //
        try {

            requiActual.setOcUsoCFDI(ocUsoCFDILocal.find(requisicionVO.getIdCfdi()));
            requiActual.setVistoBueno(usuarioRemote.find(sesion.getId()));
            requiActual.setHoraVistoBueno(new Date());
            requiActual.setFechaVistoBueno(new Date());
            requiActual.setEstatus(estatusRemote.find(Constantes.REQUISICION_SOLICITADA)); // 10 = solicitada
            final String asunto = new StringBuilder("REVISAR LA REQUISICIÓN: ").append(requiActual.getConsecutivo()).toString();

            //   LOGGER.info(this, "Correo de asigna: " + correoAsigna);
            notificacionRequisicionRemote.envioNotificacionRequisicion(
                    requiActual.getRevisa().getEmail(), "", "", asunto,
                    requiActual, "solicito", "", "");

            //Enviar mensaje a quien revisa          
            notificacionMovilRemote
                    .enviarNotificacionRequisicion(
                            requiActual,
                            requiActual.getRevisa(),
                            asunto
                    );

            edit(requiActual);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    public Map<Integer, List<SiOpcionVo>> totalPendienteCampos(String sesion) {
        String sql = "SELECT r.ap_campo AS id_campo, o.nombre, o.pagina, count(r.id) AS total \n"
                + "                FROM si_opcion o\n"
                + "                   INNER JOIN requisicion r ON r.estatus = o.estatus_contar\n"
                + "                WHERE o.ELIMINADO = false   \n"
                + "                   AND \n"
                + "                       (r.estatus, ?) IN (\n"
                + "                 		(1, r.solicita),\n"
                + "			        (10, r.revisa),\n"
                + "        			(15, r.aprueba),\n"
                + "        			(25, (SELECT usuario FROM si_usuario_rol WHERE si_rol = 34 AND eliminado = false AND ap_campo = r.ap_campo and '" + sesion + "' = usuario)), -- costos\n"
                + "        			(35, (SELECT usuario FROM si_usuario_rol WHERE si_rol = 35 AND eliminado = false AND ap_campo = r.ap_campo  and '" + sesion + "' = usuario)), -- conta\n"
                + "			        (20, (SELECT usuario FROM si_usuario_rol WHERE si_rol = 21 AND eliminado = false AND ap_campo = r.ap_campo  and '" + sesion + "' = usuario)), -- rol as\n"
                + "			        (40, r.compra))\n"
                + "                   AND o.si_modulo =  " + Constantes.MODULO_REQUISICION
                + "                   AND r.eliminado = false \n"
                + "                GROUP by r.ap_campo, o.nombre, o.pagina \n"
                + "                   HAVING count(r.id) > 0 \n"
                + "                ORDER BY r.ap_campo";
        Map<Integer, List<SiOpcionVo>> retVal = null;
        try {

            List<SiOpcionVo> lo
                    = dbCtx.fetch(sql, sesion, Constantes.MODULO_REQUISICION)
                            .into(SiOpcionVo.class);

            if (!lo.isEmpty()) {
                retVal = new HashMap<>();

                for (SiOpcionVo opcion : lo) {
                    if (retVal.get(opcion.getIdCampo()) == null) {
                        retVal.put(opcion.getIdCampo(), new ArrayList<>());
                    }

                    retVal.get(opcion.getIdCampo()).add(opcion);
                }
            }

        } catch (DataAccessException e) {
            UtilLog4j.log.fatal(this, "Error: ", e);
            retVal = Collections.emptyMap();
        }

        return retVal;
    }

    public RespuestaVo revisarRequisicion(Requisicion requisicion, Usuario usuario) {

        requisicion.setFechaReviso(new Date());
        requisicion.setHoraReviso(new Date());
        requisicion.setRevisa(usuario);
        //requisicion.setEstatus(new Estatus(Constantes.REQUISICION_REVISADA)); // 15 = Revisada              
        requisicion.setEstatus(estatusRemote.find(Constantes.REQUISICION_REVISADA)); // 15 = Revisada              

        boolean sinItemsAutorizados = (getItemsPorRequisicion(requisicion.getId(), Constantes.AUTORIZADO, Constantes.NO_SELECCION) == null);

        RespuestaVo.RespuestaVoBuilder respuesta = RespuestaVo.builder().realizado(true);

        if (sinItemsAutorizados) {

            respuesta.realizado(false);

            respuesta.mensaje("requisiciones.revision.sin.items.autorizados");

        } else {

            final String asunto = new StringBuilder().append("APROBAR LA REQUISICIÓN: ").append(requisicion.getConsecutivo()).toString();

            if (notificacionRequisicionRemote.envioNotificacionRequisicion(
                    requisicion.getAprueba().getEmail(),
                    "",
                    "",
                    asunto,
                    requisicion, "solicito", "revisar")) {

                edit(requisicion);

                ocRequisicionCoNoticiaRemote.finalizarNotas(usuario.getId(), requisicion.getId());

                notificacionMovilRemote
                        .enviarNotificacionRequisicion(
                                requisicion,
                                requisicion.getAprueba(),
                                asunto
                        );

            } else {
                //---- Mostrar mensaje  ----                
                respuesta.realizado(false);
                respuesta.mensaje("requisiciones.correo.REVnoenviado");
            }
        }

        return respuesta.build();
    }

    public RespuestaVo aprobarRequisicion(Requisicion requisicion, Usuario usuario) {
        requisicion.setFechaAprobo(new Date());
        requisicion.setHoraAprobo(new Date());
        requisicion.setAprueba(usuario);

        boolean sinItemsAutorizados = (getItemsPorRequisicion(requisicion.getId(), Constantes.AUTORIZADO, Constantes.NO_SELECCION) == null);

        RespuestaVo respuesta;

        if (sinItemsAutorizados) {
            respuesta = RespuestaVo.builder()
                    .realizado(false)
                    .mensaje("requisiciones.revision.sin.items.autorizados")
                    .build();
        } else {
            if (requisicion.isNueva()) { // La requisicion es nueva y pasa  a compras
                respuesta = aprobarNuevaRequision(requisicion, usuario);
            } else {
                respuesta = enviarRequisicionFinanzas(requisicion, usuario);
            }
        }
        return respuesta;
    }

    private RespuestaVo aprobarNuevaRequision(Requisicion requisicion, Usuario usuario) {

        final String asunto = "ASIGNAR LA REQUISICIÓN: " + requisicion.getConsecutivo();

        final RespuestaVo.RespuestaVoBuilder respuesta = RespuestaVo.builder().realizado(true);

        final String correoAsigna = traerCorreoAsigna(
                Constantes.ROL_ASIGNA_REQUISICION,
                Constantes.MODULO_REQUISICION,
                requisicion.getApCampo().getId()
        );

        requisicion.setEstatus(estatusRemote.find(Constantes.REQUISICION_APROBADA));

        if (!correoAsigna.isEmpty() && notificacionRequisicionRemote.envioNotificacionRequisicion(
                correoAsigna, "", "", asunto,
                requisicion, "solicito", "revisar", "aprobar")) {

            edit(requisicion);

            ocRequisicionCoNoticiaRemote.finalizarNotas(usuario.getId(), requisicion.getId());

            /*notificacionMovilRemote
                            .enviarNotificacionRequisicion(
                                        requisicion,
                                        requisicion.getAprueba(),
                                        asunto
                   );*/
        } else {
            respuesta.realizado(false);
            respuesta.mensaje("requisiciones.correos.APRnoenviado");
        }

        return respuesta.build();
    }

    private RespuestaVo enviarRequisicionFinanzas(Requisicion requisicion, Usuario usuario) {

        final String asunto = "REVISAR LA REQUISICIÓN: " + requisicion.getConsecutivo();

        final RespuestaVo.RespuestaVoBuilder respuesta = RespuestaVo.builder().realizado(true);

        //requisicion.setEstatus(new Estatus(Constantes.REQUISICION_VISTO_BUENO_C));
        requisicion.setEstatus(estatusRemote.find(Constantes.REQUISICION_VISTO_BUENO_C));

        String correoRevisa = traerCorreoAsigna(
                Constantes.ROL_VISTO_BUENO_COSTO,
                Constantes.MODULO_REQUISICION,
                requisicion.getApCampo().getId()
        );

        if (notificacionRequisicionRemote.envioNotificacionRequisicion(
                correoRevisa, "", "", asunto,
                requisicion, "solicito", "revisar", "aprobar")) {

            edit(requisicion);

            ocRequisicionCoNoticiaRemote.finalizarNotas(usuario.getId(), requisicion.getId());
        } else {
            respuesta.realizado(false);
            respuesta.mensaje("requisiciones.correos.APRnoenviado");
        }

        return respuesta.build();
    }

    public List<RequisicionTiemposVO> requisicionTiempos(int idCampo, String consecutivo, int status, int gerencia, String fecha1, String fecha2) {
        List<RequisicionTiemposVO> lr = null;

        try {
            clearQuery();
            query.append(
                    " select  "
                    + " r.id, r.consecutivo, r.referencia,g.nombre, r.tipo, "
                    + " us.nombre,r.fecha_solicito,  "
                    + " uv.nombre,r.fecha_visto_bueno,(coalesce(r.fecha_visto_bueno,current_date) - coalesce(r.fecha_solicito, current_date)), "
                    + " ur.nombre,r.fecha_reviso,(coalesce(r.fecha_reviso,current_date) - coalesce(r.fecha_visto_bueno, current_date)), "
                    + " ua.nombre,r.fecha_aprobo,(coalesce(r.fecha_aprobo,current_date) - coalesce(r.fecha_reviso, current_date)), "
                    + " ug.nombre,r.fecha_asigno,(coalesce(r.fecha_asigno,current_date) - coalesce(r.fecha_aprobo, current_date)) "
                    + " from requisicion r "
                    + " inner join gerencia g on g.id = r.gerencia "
                    + " left join usuario us on us.id = r.solicita "
                    + " left join usuario uv on uv.id = r.visto_bueno "
                    + " left join usuario ur on ur.id = r.revisa "
                    + " left join usuario ua on ua.id = r.aprueba "
                    + " left join usuario ug on ug.id = r.asigna "
                    + " where r.eliminado = false "
                    + " and r.estatus > 1 "
            );

            if (idCampo > 0) {
                query.append(" and r.AP_CAMPO = ").append(idCampo);
            }

            if (consecutivo != null && !consecutivo.isEmpty()) {
                query.append(" and r.consecutivo = '").append(consecutivo).append("' ");
            }

            if (status > 0) {
                query.append(" and r.estatus = ").append(status);
            }

            if (gerencia > 0) {
                query.append(" and r.gerencia = ").append(gerencia);
            }

            if (fecha1 != null && fecha2 != null && !fecha1.isEmpty() && !fecha2.isEmpty()) {
                query.append(" AND r.fecha_solicito >= '").append(fecha1).append("' AND r.fecha_solicito <= '").append(fecha2).append("' ");
            }

            query.append(" ORDER BY r.ID DESC");

            RequisicionTiemposVO o;

            List<Object[]> l = em.createNativeQuery(query.toString()).getResultList();
            if (!l.isEmpty()) {
                lr = new ArrayList<>();
                for (Object[] objects : l) {
                    o = new RequisicionTiemposVO();
                    o.setIdRequisicion((Integer) objects[0]);
                    o.setConsecutivo(String.valueOf(objects[1]));
                    o.setReferencia(String.valueOf(objects[2]));
                    o.setGerencia(String.valueOf(objects[3]));
                    o.setTipo(String.valueOf(objects[4]));

                    o.setNombreSolicita(String.valueOf(objects[5]));
                    o.setFechaSolicita((Date) objects[6]);

                    o.setNombreVistoBueno(objects[7] != null ? String.valueOf(objects[7]) : ("AF".equals(o.getTipo()) ? "Contabilidad" : "Costos"));
                    o.setFechaVistoBueno((Date) objects[8]);
                    o.setDiasVistoBueno(objects[9] != null ? ((Integer) objects[9]) : 0);

                    o.setNombreRevisa(objects[10] != null ? String.valueOf(objects[10]) : "");
                    o.setFechaRevisa((Date) objects[11]);
                    o.setDiasRevisa(objects[12] != null ? ((Integer) objects[12]) : 0);

                    o.setNombreAprueba(objects[13] != null ? String.valueOf(objects[13]) : "");
                    o.setFechaAprueba((Date) objects[14]);
                    o.setDiasAprueba(objects[15] != null ? ((Integer) objects[15]) : 0);

                    o.setNombreAsigna(objects[16] != null ? String.valueOf(objects[16]) : "");
                    o.setFechaAsigna((Date) objects[17]);
                    o.setDiasAsigna(objects[18] != null ? ((Integer) objects[18]) : 0);

                    lr.add(o);
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lr = null;
        }

        return lr;
    }

}
