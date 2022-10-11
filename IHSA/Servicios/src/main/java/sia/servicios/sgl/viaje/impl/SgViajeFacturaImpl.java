/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeFactura;
import sia.modelo.SgViajero;
import sia.modelo.SiFactura;
import sia.modelo.Usuario;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.sgl.viaje.vo.ViajeFacturaVo;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SgViajeFacturaImpl extends AbstractFacade<SgViajeFactura>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private SiFacturaImpl siFacturaRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    public SgViajeFacturaImpl() {
        super(SgViajeFactura.class);
    }

    
    public void guardarViajeViajero(int viaje, int viajero, String sesion) {
        SgViajeFactura sgViajeFactura = new SgViajeFactura();
        sgViajeFactura.setSgViaje(new SgViaje(viaje));
        sgViajeFactura.setSgViajero(new SgViajero(viajero));
        sgViajeFactura.setGenero(new Usuario(sesion));
        sgViajeFactura.setFechaGenero(new Date());
        sgViajeFactura.setHoraGenero(new Date());
        sgViajeFactura.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(sgViajeFactura);
    }

    
    public boolean guardarFactura(int idViajeFactura, FacturaVo facturaVo, String sesion) {
        boolean v = true;
        try {
            SiFactura siFactura = siFacturaRemote.guardarFactura(facturaVo, sesion, new ArrayList<OrdenDetalleVO>(),Constantes.VACIO);
            //
            SgViajeFactura sgViajeFactura = find(idViajeFactura);
            //
            sgViajeFactura.setSiFactura(new SiFactura(siFactura.getId()));
            sgViajeFactura.setModifico(new Usuario(sesion));
            sgViajeFactura.setFechaModifico(new Date());
            sgViajeFactura.setHoraModifico(new Date());
            //
            edit(sgViajeFactura);
            //
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error al guardar la factura   + + + + + + + +" + e.getMessage());
            v = false;
        }
        return v;
    }

    
    public List<ViajeFacturaVo> traerFacturaPorViaje(int viaje) {
        clearQuery();
        query.append("select vf.id, v.codigo, f.concepto, f.folio, f.monto, f.observacion, p.id, p.nombre, m.id, m.nombre, f.fecha_emision, f.si_adjunto, adj.uuid, f.id, adj.url, vj.id, u.nombre, inv.nombre from sg_viaje_factura vf ");
        query.append("      inner join sg_viaje v on vf.sg_viaje = v.id ");
        query.append("      inner join sg_viajero vj on vf.sg_viajero = vj.id ");
        //
        query.append("      left join usuario u on vj.usuario = u.id ");
        query.append("      left join sg_invitado inv on vj.sg_invitado = inv.id ");
        //
        query.append("      left join si_factura f on vf.si_factura = f.id");
        query.append("      left join proveedor p on f.proveedor = p.id");
        query.append("      left join moneda m on f.moneda = m.id");
        query.append("      left join SI_ADJUNTO adj on f.SI_ADJUNTO = adj.ID");
        query.append("  where vf.sg_viaje = ").append(viaje);
        query.append("  and vf.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        //
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ViajeFacturaVo> lf = new ArrayList<ViajeFacturaVo>();
        try {
            for (Object[] objects : lo) {
                lf.add(castFactura(objects));
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lf;
    }

    private ViajeFacturaVo castFactura(Object[] objects) {
        ViajeFacturaVo facturaVo = new ViajeFacturaVo();
        facturaVo.setIdViajeFactura((Integer) objects[0]);
        facturaVo.setViaje((String) objects[1]);
        facturaVo.setConcepto((String) objects[2]);
        facturaVo.setFolio((String) objects[3]);
        facturaVo.setMonto((BigDecimal) (objects[4] != null ? (BigDecimal) objects[4] : 0.0));
        facturaVo.setObservacion((String) objects[5]);
        facturaVo.setIdProveedor(objects[6] != null ? (Integer) objects[6] : 0);
        facturaVo.setProveedor((String) objects[7]);
        facturaVo.setIdMoneda(objects[8] != null ? (Integer) objects[8] : 0);
        facturaVo.setMoneda((String) objects[9]);
        facturaVo.setFechaEmision((Date) objects[10]);
        facturaVo.setIdAdjunto(objects[11] != null ? (Integer) objects[11] : 0);
        facturaVo.setUuId((String) objects[12]);
        facturaVo.setId(objects[13] != null ? (Integer) objects[13] : 0);
        facturaVo.setUrlArchivo((String) objects[14]);
        facturaVo.setIdViajero(objects[15] != null ? (Integer) objects[15] : 0);
        facturaVo.setViajero(objects[16] != null ? (String) objects[16] : (String) objects[17]);
        facturaVo.setTipo(objects[16] != null ? "Empleado" : "Invitado");
        return facturaVo;
    }

    
    public void eliminarViajeFactura(int idviajeFactura, String sesion) {
        SgViajeFactura sgViajeFactura = find(idviajeFactura);
        sgViajeFactura.setSiFactura(null);
        sgViajeFactura.setModifico(new Usuario(sesion));
        sgViajeFactura.setFechaModifico(new Date());
        sgViajeFactura.setHoraModifico(new Date());
        //
        edit(sgViajeFactura);
    }

    
    public List<ViajeFacturaVo> buscarViajesPorGerencia(String gerencia, int moneda) {
        clearQuery();
        query.append("select vf.id, v.codigo, f.concepto, f.folio, f.monto, f.observacion, p.id, p.nombre, m.id, m.nombre, f.fecha_emision, f.si_adjunto, adj.uuid, f.id, adj.url, vj.id, u.nombre, inv.nombre from sg_viaje_factura vf ");
        query.append("      inner join sg_viaje v on vf.sg_viaje = v.id ");
        query.append("      inner join sg_viajero vj on vf.sg_viajero = vj.id ");
        query.append("      left join usuario u on vj.usuario = u.id ");
        query.append("      left join sg_invitado inv on vj.sg_invitado = inv.id ");
        query.append("      inner join si_factura f on vf.si_factura = f.id");
        query.append("      left  join proveedor p on f.proveedor = p.id");
        query.append("      left join moneda m on f.moneda = m.id");
        query.append("      left join SI_ADJUNTO adj on f.SI_ADJUNTO = adj.ID");
        query.append("      inner join SG_ITINERARIO i on v.SG_ITINERARIO = i.ID");
        query.append("      inner join SG_SOLICITUD_VIAJE sv on i.SG_SOLICITUD_VIAJE = sv.ID");
        query.append("      inner join GERENCIA g on sv.GERENCIA_RESPONSABLE = g.ID");
        query.append("  where  g.nombre = '").append(gerencia).append("'");
        query.append("  and m.id = ").append(moneda);
        query.append("  and vf.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ViajeFacturaVo> lf = new ArrayList<ViajeFacturaVo>();
        try {
            for (Object[] object : lo) {
                lf.add(castFactura(object));
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lf;
    }

    private ViajeFacturaVo castTotalViajeFactura(Object[] object) {
        ViajeFacturaVo viajeFacturaVo = new ViajeFacturaVo();
        viajeFacturaVo.setGerencia((String) object[0]);
        viajeFacturaVo.setTotal((Double) object[1]);
        return viajeFacturaVo;
    }

    
    public List<ViajeFacturaVo> buscarGastoViajes(String inicio, String fin, int moneda) {
        clearQuery();
        query.append("select g.NOMBRE, sum(f.MONTO) from sg_viaje_factura vf ");
        query.append("      inner join sg_viaje v on vf.sg_viaje = v.id");
        query.append("      inner join SG_ITINERARIO i on v.SG_ITINERARIO = i.id");
        query.append("      inner join SG_SOLICITUD_VIAJE sv on i.SG_SOLICITUD_VIAJE = sv.ID");
        query.append("      inner join GERENCIA g on sv.GERENCIA_RESPONSABLE = g.ID");
        query.append("      inner join si_factura f on vf.si_factura = f.id");
        query.append("  where  v.FECHA_SALIDA between cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(inicio)).append("' as date) and cast('").append(siManejoFechaLocal.cambiarddmmyyyyAyyyymmaa(fin)).append("' as date)");
        query.append("  and f.MONEDA = ").append(moneda);
        query.append("  and vf.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("  group by g.NOMBRE ");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ViajeFacturaVo> lf = new ArrayList<ViajeFacturaVo>();
        try {
            for (Object[] object : lo) {
                lf.add(castTotalViajeFactura(object));
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lf;
    }

    
    public List<ViajeFacturaVo> buscarGastoViajePorEmpleado(String idUsuario) {
        clearQuery();
        query.append("  select v.CODIGO, sum(f.MONTO) from SG_VIAJE_FACTURA vf ");
        query.append("      inner join SI_FACTURA f on vf.SI_FACTURA = f.ID");
        query.append("      inner join SG_VIAJERO vj on vf.SG_VIAJERO = vj.ID");
        query.append("      inner join SG_VIAJE v on vf.SG_VIAJE = v.ID");
        query.append("  where vj.USUARIO = '").append(idUsuario).append("'");
        query.append("  and vf.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append("  and v.ESTATUS <> ").append(Constantes.ESTATUS_VIAJE_CANCELADO);
        query.append("  group by v.CODIGO ");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ViajeFacturaVo> lvf = new ArrayList<ViajeFacturaVo>();
        for (Object[] objects : lo) {
            lvf.add(castTotalViajeFactura(objects));
        }
        return lvf;
    }
}
