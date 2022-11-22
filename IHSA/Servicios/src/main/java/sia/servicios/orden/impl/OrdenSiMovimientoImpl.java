/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Orden;
import sia.modelo.OrdenSiMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class OrdenSiMovimientoImpl extends AbstractFacade<OrdenSiMovimiento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    public OrdenSiMovimientoImpl() {
        super(OrdenSiMovimiento.class);
    }
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;

    
    public void saverOrderMoove(String idSesion, int idOrden, String motivo, String solicita) {
        try {
            OrdenSiMovimiento ordenSiMovimiento = new OrdenSiMovimiento();

            //Guardar en movimiento
            SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(4), usuarioRemote.find(idSesion));
            //Giardar en relacion con requisicion

            ordenSiMovimiento.setOrden(new Orden(idOrden));
            ordenSiMovimiento.setSiMovimiento(siMovimiento);
            ordenSiMovimiento.setGenero(new Usuario(idSesion));
            ordenSiMovimiento.setSolicitaDevolucion(usuarioRemote.buscarPorNombre(solicita));
            ordenSiMovimiento.setFechaGenero(new Date());
            ordenSiMovimiento.setHoraGenero(new Date());
            ordenSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);
            create(ordenSiMovimiento);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Exc: motivo orden: " + ex.getMessage());
        }
    }

    
    public OrdenSiMovimiento saverOrderMoove(Usuario usrGenero, int idOrden, String motivo, Usuario usrSolicita, int operacion) {
        OrdenSiMovimiento ordenSiMovimiento = null;
        try {
            ordenSiMovimiento = new OrdenSiMovimiento();
            //Guardar en movimiento
            SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(operacion), usrGenero);
            //Giardar en relacion con requisicion
            ordenSiMovimiento.setOrden(new Orden(idOrden));
            ordenSiMovimiento.setSiMovimiento(siMovimiento);
            ordenSiMovimiento.setGenero(usrGenero);
            ordenSiMovimiento.setSolicitaDevolucion(usrSolicita);
            ordenSiMovimiento.setFechaGenero(new Date());
            ordenSiMovimiento.setHoraGenero(new Date());
            ordenSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);
            create(ordenSiMovimiento);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Exc: motivo orden: " + ex.getMessage());
        }
        return ordenSiMovimiento;
    }

    
    public void guardarMovimiento(String idSesion, int idOrden, String motivo, String solicita, int operacion) {
        try {
            OrdenSiMovimiento ordenSiMovimiento = new OrdenSiMovimiento();

            //Guardar en movimiento
            SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(operacion),
                    usuarioRemote.findRH(idSesion));
            //Giardar en relacion con requisicion

            ordenSiMovimiento.setOrden(new Orden(idOrden));
            ordenSiMovimiento.setSiMovimiento(siMovimiento);
            ordenSiMovimiento.setGenero(new Usuario(idSesion));
            ordenSiMovimiento.setSolicitaDevolucion(new Usuario(solicita));
            ordenSiMovimiento.setFechaGenero(new Date());
            ordenSiMovimiento.setHoraGenero(new Date());
            ordenSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);
            create(ordenSiMovimiento);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Exc: motivo orden: " + ex.getMessage());
        }
    }

    
    public List<MovimientoVO> getMovimientsobyOrden(int idOrden) {
        List<MovimientoVO> movimientos = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select m.SOLICITA_DEVOLUCION, ");
            sb.append(" (case when (m.SOLICITA_DEVOLUCION is null) then uu.NOMBRE else u.NOMBRE END) as SOLICITO, m.GENERO, uu.NOMBRE as GENERO, ");
            sb.append(" m.FECHA_GENERO, m.HORA_GENERO, s.MOTIVO, so.NOMBRE ");
            sb.append(" from ORDEN_SI_MOVIMIENTO m  ");
            sb.append(" left join SI_MOVIMIENTO s on s.ID = m.SI_MOVIMIENTO ");
            sb.append(" left join SI_OPERACION so on so.ID = s.SI_OPERACION ");
            sb.append(" LEFT JOIN USUARIO u on u.ID = m.SOLICITA_DEVOLUCION ");
            sb.append(" LEFT JOIN USUARIO uu on uu.ID = m.GENERO ");
            sb.append(" WHERE m.ORDEN = ").append(idOrden);
            sb.append(" order by so.NOMBRE ");

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                movimientos = new ArrayList<>();
                for (Object[] objects : lo) {
                    movimientos.add(castMovimiento(objects));
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Exc: motivo orden: " + ex.getMessage());
        }
        return movimientos;
    }

    private MovimientoVO castMovimiento(Object[] object) {
        MovimientoVO movimiento = new MovimientoVO();
        movimiento.setNombreSolicito((String) object[1]);
        movimiento.setGenero((String) object[3]);
        movimiento.setFechaGenero((Date) object[4]);
        movimiento.setHoraGenero((Date) object[5]);
        movimiento.setMotivo((String) object[6]);
        movimiento.setOperacion((String) object[7]);

        return movimiento;
    }

    
    public List<OrdenVO> ordenesRechadas(String inicio, String fin, int operacion, int idCampo) {
        clearQuery();
        query.append("select o.CONSECUTIVO, e.nombre, o.REFERENCIA,  o.FECHA as Solicitada, an.NOMBRE as Comprador,u.NOMBRE as Devolvio, ");
        query.append(" sm.MOTIVO, om.FECHA_GENERO as Fecha_Devuelta, r.consecutivo  from ORDEN_SI_MOVIMIENTO om");
        query.append(" inner join orden o on om.ORDEN = o.ID");
        query.append(" left join REQUISICION r on o.REQUISICION = r.ID ");
        query.append(" inner join AUTORIZACIONES_ORDEN  ao on ao.ORDEN  = o.ID ");
        query.append(" inner join ESTATUS e on ao.ESTATUS = e.ID");
        query.append(" inner join SI_MOVIMIENTO sm on om.SI_MOVIMIENTO = sm.ID");
        query.append(" inner join USUARIO u on om.GENERO = u.ID");
        query.append(" inner join USUARIO an on o.ANALISTA = an.ID");
        query.append(" where sm.SI_OPERACION = ").append(operacion);
        query.append(" and o.ap_campo = ").append(idCampo);
        query.append(" and ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<OrdenVO>();
            for (Object[] objects : lo) {
                lor.add(castOrdenMovimiento(objects));
            }
        }
        return lor;

    }

    
    public List<OrdenVO> ordenesPorUsuario(int operacion, int idCampo, String idUsuario, String inicio, String fin) {
        clearQuery();
        query.append("select o.CONSECUTIVO, e.nombre, o.REFERENCIA,  o.FECHA as Solicitada, an.NOMBRE as Comprador,u.NOMBRE as Devolvio, ");
        query.append(" sm.MOTIVO, om.FECHA_GENERO as Fecha_Devuelta, r.consecutivo  from ORDEN_SI_MOVIMIENTO om");
        query.append(" inner join orden o on om.ORDEN = o.ID");
        query.append(" left join REQUISICION r on o.REQUISICION = r.ID ");
        query.append(" inner join AUTORIZACIONES_ORDEN  ao on ao.ORDEN  = o.ID ");
        query.append(" inner join ESTATUS e on ao.ESTATUS = e.ID");
        query.append(" inner join SI_MOVIMIENTO sm on om.SI_MOVIMIENTO = sm.ID");
        query.append(" inner join USUARIO u on om.GENERO = u.ID");
        query.append(" inner join USUARIO an on o.ANALISTA = an.ID");
        query.append(" where sm.SI_OPERACION = ").append(operacion);
        if (operacion == Constantes.ID_SI_OPERACION_CANCELAR) {
            query.append(" and ao.estatus = ").append(Constantes.ORDENES_CANCELADAS);
        } else {
            query.append(" and ao.estatus <> ").append(Constantes.ORDENES_CANCELADAS);
        }
        query.append(" and ao.FECHA_SOLICITO between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
        query.append(" and o.ap_campo = ").append(idCampo);
        query.append(" and ao.solicito = '").append(idUsuario).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<OrdenVO> lor = null;
        if (lo != null) {
            lor = new ArrayList<OrdenVO>();
            for (Object[] objects : lo) {
                lor.add(castOrdenMovimiento(objects));
            }
        }
        return lor;

    }

    private OrdenVO castOrdenMovimiento(Object[] obj) {
        OrdenVO o = new OrdenVO();
        o.setConsecutivo((String) obj[0]);
        o.setEstatus((String) obj[1]);
        o.setReferencia((String) obj[2]);
        o.setFechaSolicita((Date) obj[3]);
        o.setAnalista((String) obj[4]);
        o.setUsuario((String) obj[5]);
        o.setMotivo((String) obj[6]);
        o.setFechaGenero((Date) obj[7]);
        o.setRequisicion((String) obj[8]);
        return o;
    }

    
    public List<MovimientoVO> traerMovimientsoOrdenOperacion(int idOrden, int operacionId) {
        List<MovimientoVO> movimientos = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select m.SOLICITA_DEVOLUCION, ")
                    .append(" (case when (m.SOLICITA_DEVOLUCION is null) then uu.NOMBRE else u.NOMBRE END) as SOLICITO, m.GENERO, uu.NOMBRE as GENERO, ")
                    .append(" m.FECHA_GENERO, m.HORA_GENERO, s.MOTIVO, so.NOMBRE ")
                    .append(" from ORDEN_SI_MOVIMIENTO m  ")
                    .append(" left join SI_MOVIMIENTO s on s.ID = m.SI_MOVIMIENTO ")
                    .append(" left join SI_OPERACION so on so.ID = s.SI_OPERACION ")
                    .append(" LEFT JOIN USUARIO u on u.ID = m.SOLICITA_DEVOLUCION ")
                    .append(" LEFT JOIN USUARIO uu on uu.ID = m.GENERO ")
                    .append(" WHERE m.ORDEN = ").append(idOrden)
                    .append(" and s.si_operacion  = ").append(operacionId)
                    .append(" and m.eliminado = false")
                    .append(" order by so.NOMBRE ");

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                movimientos = new ArrayList<MovimientoVO>();
                for (Object[] objects : lo) {
                    movimientos.add(castMovimiento(objects));
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Exc: motivo orden: " + ex.getMessage());
        }
        return movimientos;
    }

    
    public long totalCartaRechazadaPorProveedor(int proveedorId) {
        String c = "select count(osm.id) \n"
                + " from orden_si_movimiento osm \n"
                + " 	inner join si_movimiento sm on osm.si_movimiento  = sm.id \n"
                + " 	inner join orden o on osm.orden  = o.id \n"
                + " where o.proveedor  = " + proveedorId
                + " and sm.si_operacion  = " + Constantes.ID_OPERACION_REC_CARTA_INTENCION
                + " and sm.eliminado  = false \n"
                + " and osm.eliminado  = false ";
        return (long) em.createNativeQuery(c).getSingleResult();

    }

    
    public List<MovimientoVO> traerCartasRechazadasProveedor(int proveedorId) {
        String c = "select o.consecutivo, o.referencia, osm.fecha_genero , sm.motivo \n"
                + " from orden_si_movimiento osm \n"
                + " 	inner join si_movimiento sm on osm.si_movimiento  = sm.id \n"
                + " 	inner join orden o on osm.orden  = o.id \n"
                + " where o.proveedor  = \n" + proveedorId
                + " and sm.si_operacion  = " + Constantes.ID_OPERACION_REC_CARTA_INTENCION
                + " and sm.eliminado  = false \n"
                + " and osm.eliminado  = false";
        List<Object[]> lista = em.createNativeQuery(c).getResultList();
        List<MovimientoVO> movs = new ArrayList<MovimientoVO>();
        for (Object[] objects : lista) {
            MovimientoVO mVo = new MovimientoVO();
            mVo.setCodigo((String) objects[0]);
            mVo.setDescripcion((String) objects[1]);
            mVo.setFechaGenero((Date) objects[2]);
            mVo.setMotivo((String) objects[3]);
            movs.add(mVo);
        }
        return movs;
    }
}
