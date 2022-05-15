/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.inventarios.service.ArticuloImpl;
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.InvArticulo;
import sia.modelo.OrdenDetalle;
import sia.modelo.SiFactura;
import sia.modelo.SiFacturaDetalle;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.FacturaDetalleVo;
import sia.servicios.orden.impl.OrdenDetalleImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class SiFacturaDetalleImpl extends AbstractFacade<SiFacturaDetalle>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiFacturaDetalleImpl() {
        super(SiFacturaDetalle.class);
    }

    @Inject
    SiFacturaImpl siFacturaRemote;
    @Inject
    ArticuloRemote articuloRemote;
    @Inject
    OrdenDetalleImpl ordenDetalleRemote;

    
    public void guardar(int idFactura, List<OrdenDetalleVO> partidas, String sesion) {
        for (OrdenDetalleVO partida : partidas) {
            SiFacturaDetalle siFacturaDetalle = new SiFacturaDetalle();
            siFacturaDetalle.setSiFactura(new SiFactura(idFactura));
            if (partida.getId() > 0) {
                siFacturaDetalle.setOrdenDetalle(new OrdenDetalle(partida.getId()));
            }
            //
            siFacturaDetalle.setMultiproyectoId(partida.getIdAgrupador());
            if (partida.getArtID() > 0) {
                siFacturaDetalle.setInvArticulo(new InvArticulo(partida.getArtID()));
            } else {
                InvArticulo art = articuloRemote.buscarPorNombre(partida.getArtNombre(), partida.getArtIdUnidad());
                if (art != null) {
                    siFacturaDetalle.setInvArticulo(art);
                }
            }
            siFacturaDetalle.setCantidad(partida.getCantidadPorFacturar());
            siFacturaDetalle.setDescripcion(partida.getArtNombre());
            siFacturaDetalle.setPrecioUnitario(partida.getPrecioUnitario());
            siFacturaDetalle.setImporte(new BigDecimal(partida.getImporte()));
            siFacturaDetalle.setGenero(new Usuario(sesion));
            siFacturaDetalle.setFechaGenero(new Date());
            siFacturaDetalle.setHoraGenero(new Date());
            siFacturaDetalle.setEliminado(Constantes.NO_ELIMINADO);
            //
            create(siFacturaDetalle);
            //
        }
        //
//        SiFactura factura = siFacturaRemote.find(idFactura);
//        factura.setMonto(totalPorFactura(idFactura));
//        factura.setModifico(new Usuario(sesion));
//        factura.setFechaModifico(new Date());
//        factura.setHoraModifico(new Date());
//        siFacturaRemote.edit(factura);
    }

    
    public List<FacturaDetalleVo> traerDetalle(int idFactura) {
        String c = " select fd.id, fd.si_factura, fd.orden_detalle, fd.descripcion, fd.cantidad, fd.precio_unitario, fd.importe, od.cantidad "
                + " from si_factura_detalle fd "
                + "         left join orden_detalle od on fd.orden_detalle = od.id "
                + " where fd.si_factura =  ?1 "
                + " and fd.eliminado = false ";
        System.out.println("asdasdasd : " + c);
        List<Object[]> lo = em.createNativeQuery(c).setParameter(1, idFactura).getResultList();
        List<FacturaDetalleVo> lista = new ArrayList<>();
        for (Object[] objects : lo) {
            lista.add(castDetalle(objects));
        }
        return lista;
    }

    private FacturaDetalleVo castDetalle(Object[] obj) {
        FacturaDetalleVo fDVo = new FacturaDetalleVo();
        fDVo.setId((Integer) obj[0]);
        fDVo.setIdFactura((Integer) obj[1]);
        fDVo.setIdOrdenDetalle(obj[2] != null ? (Integer) obj[2] : Constantes.CERO);
        fDVo.setDescripcion((String) obj[3]);
        fDVo.setCantidad((BigDecimal) obj[4]);
        fDVo.setPrecio((BigDecimal) obj[5]);
        fDVo.setImporte((BigDecimal) obj[6]);
        fDVo.setCantidadOrdenDetalle(obj[7] != null ? (Double) obj[7] : Constantes.CERO);
        return fDVo;
    }

    
    public void eliminar(int idDetalleFactura, String sesion) {
        SiFacturaDetalle siFacturaDetalle = em.createNamedQuery("SiFacturaDetalle.findId", SiFacturaDetalle.class).setParameter(1, idDetalleFactura).getSingleResult();
        siFacturaDetalle.setEliminado(Constantes.ELIMINADO);
        siFacturaDetalle.setModifico(new Usuario(sesion));
        siFacturaDetalle.setFechaModifico(new Date());
        siFacturaDetalle.setHoraModifico(new Date());
        //
        edit(siFacturaDetalle);
    }

    
    public BigDecimal totalFacturadoPorOrden(int idOrden) {
        String c = "SELECT COALESCE(sum(fd.importe), 0) from si_factura_detalle  fd \n"
                + "	inner join orden_detalle od on fd.orden_detalle = od.id \n"
                + " where od.orden = ?1 \n"
                + " and fd.eliminado = false ";
        return (BigDecimal) em.createNativeQuery(c).setParameter(1, idOrden).getSingleResult();
    }

    
    public BigDecimal totalPorFactura(int idFactura) {
        String c = "SELECT COALESCE(sum(fd.importe), 0) from si_factura_detalle fd where fd.si_factura = ?1 and fd.eliminado = false";
        return (BigDecimal) em.createNativeQuery(c).setParameter(1, idFactura).getSingleResult();
    }

    
    public List<FacturaDetalleVo> detalleFactura(int idFactura) {
        String c = "SELECT * from (\n"
                + "	select fd.descripcion, fd.precio_unitario, fd.cantidad,\n"
                + "		sum(fd.importe) as importe\n"
                + "	from si_factura_detalle fd\n"
                + "	where fd.si_factura =  \n" + idFactura
                + "	and fd.eliminado = false \n"
                + "	GROUP by rollup (fd.descripcion, fd.precio_unitario, fd.cantidad)\n"
                + "	order by  fd.descripcion\n"
                + ") as partidas\n"
                + "where (precio_unitario is not null and cantidad is not null) or descripcion is null";
        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        List<FacturaDetalleVo> lista = new ArrayList<>();
        for (Object[] objects : lo) {
            FacturaDetalleVo fdVo = new FacturaDetalleVo();
            if (objects[0] != null) {
                fdVo.setDescripcion(objects[0] != null ? (String) objects[0] : Constantes.VACIO);
                fdVo.setPrecio((BigDecimal) objects[1]);
                fdVo.setCantidad((BigDecimal) objects[2]);
                fdVo.setImporte((BigDecimal) objects[3]);
                lista.add(fdVo);
            }
        }
        return lista;
    }

    
    public void actualizarOrdenDetalleFactura(List<FacturaDetalleVo> listaFacturaDetalle, String rfc, boolean multiproyecto, int idOrden) {
        for (FacturaDetalleVo facturaDetalleVo : listaFacturaDetalle) {
            SiFacturaDetalle detalle = em.createNamedQuery("SiFacturaDetalle.findId", SiFacturaDetalle.class).setParameter(1, facturaDetalleVo.getId()).getSingleResult();
            if (multiproyecto) {
                OrdenDetalle od = ordenDetalleRemote.itemsPorOrdenMultiID(idOrden, facturaDetalleVo.getIdOrdenDetalle());
                if (od != null) {
                    detalle.setOrdenDetalle(od);
                    detalle.setInvArticulo(od.getInvArticulo());
                    detalle.setMultiproyectoId(facturaDetalleVo.getIdOrdenDetalle());
                }
            } else {
                OrdenDetalle od = ordenDetalleRemote.find(facturaDetalleVo.getIdOrdenDetalle());
                if (od != null) {
                    detalle.setOrdenDetalle(od);
                    detalle.setInvArticulo(od.getInvArticulo());
                }
            }

            detalle.setModifico(new Usuario(rfc));
            detalle.setFechaModifico(new Date());
            detalle.setHoraModifico(new Date());
            //
            edit(detalle);
        }
    }
}
