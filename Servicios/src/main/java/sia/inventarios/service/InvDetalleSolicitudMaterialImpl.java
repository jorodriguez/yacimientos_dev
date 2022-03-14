/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.InvDetalleSolicitudMaterial;
import sia.modelo.InvSolicitudMaterial;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;

/**
 *
 * @author mluis
 */
@LocalBean 
public class InvDetalleSolicitudMaterialImpl extends AbstractFacade<InvDetalleSolicitudMaterial> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvDetalleSolicitudMaterialImpl() {
        super(InvDetalleSolicitudMaterial.class);
    }

    @Inject
    ArticuloImpl articuloRemote;

    
    public List<DetalleSolicitudMaterialAlmacenVo> traerPorSolicitudId(int idSol) {
        String c = "select  idsm.id, a.id, a.nombre, a.codigo_int, idsm.referencia,  coalesce(idsm.cantidad, 0), \n"
                + " coalesce(sum(ii.numero_unidades ), 0) as total, \n"
                + " u.id, u.nombre, idsm.cantidad_recibida, ii.id, \n"
                + " (coalesce(r.codigo, '') || coalesce(p.codigo,'') || coalesce(c.codigo,'')) as celda"
                + " from inv_detalle_solicitud_material idsm \n"
                + "	inner join inv_articulo  a on idsm.inv_articulo  = a.id\n"
                + "	inner join si_unidad  u on a.unidad = u.id\n"
                + "	inner join inv_inventario ii on ii.articulo  = a.id and ii.eliminado = false "
                + "	inner join inv_solicitud_material sm on sm.id = idsm.inv_solicitud_material "
                + "	inner join inv_almacen al on al.id = sm.inv_almacen and ii.almacen = al.id "
                + "	left join inv_inventario_celda ic on ic.inv_inventario = ii.id and ic.eliminado = false "
                + "	left join inv_celda c on ic.inv_celda = c.id and c.eliminado = false "
                + "	left join inv_rack r on c.inv_rack = r.id and r.eliminado = false "
                + "	left join inv_piso p on c.inv_piso = p.id and p.eliminado = false "
                + " where idsm.inv_solicitud_material  = " + idSol
                + " and idsm.eliminado  = false "
                + " group by idsm.id, a.id, a.nombre, a.codigo_int, idsm.referencia, idsm.cantidad, u.id, u.nombre, idsm.cantidad_recibida, ii.id, celda";
        List<Object[]> objects = em.createNativeQuery(c).getResultList();
        List<DetalleSolicitudMaterialAlmacenVo> lista = new ArrayList<>();
        for (Object[] object : objects) {
            lista.add(cast(object));
        }
        return lista;
    }

    private DetalleSolicitudMaterialAlmacenVo cast(Object[] obj) {
        DetalleSolicitudMaterialAlmacenVo ddVo = new DetalleSolicitudMaterialAlmacenVo();
        ddVo.setId((Integer) obj[0]);
        ddVo.setIdArticulo((Integer) obj[1]);
        ddVo.setArticulo((String) obj[2]);
        ddVo.setCodigoArt((String) obj[3]);
        ddVo.setReferencia((String) obj[4]);
        ddVo.setCantidad(((BigDecimal) obj[5]).doubleValue());
        ddVo.setDisponibles(((BigDecimal) obj[6]).doubleValue());
        ddVo.setIdUnidad(((Integer) obj[7]));
        ddVo.setUnidad(((String) obj[8]));
        ddVo.setCantidadRecibida(obj[9] != null ? ((BigDecimal) obj[9]).doubleValue() : 0.0);
        ddVo.setIdInventario(((Integer) obj[10]));
        ddVo.setUbicacion(((String) obj[11]));

        return ddVo;
    }

    
    public void guardar(int idSolicitud, DetalleSolicitudMaterialAlmacenVo dsmav, String sesion) {
        InvDetalleSolicitudMaterial detalleSolicitudMaterial = new InvDetalleSolicitudMaterial();
        detalleSolicitudMaterial.setInvSolicitudMaterial(new InvSolicitudMaterial(idSolicitud));
        detalleSolicitudMaterial.setInvArticulo(articuloRemote.buscarPorCodigo(dsmav.getCodigoArt(), dsmav.getIdUnidad()));
        detalleSolicitudMaterial.setReferencia(dsmav.getReferencia());
        detalleSolicitudMaterial.setCantidad(dsmav.getCantidad());
        detalleSolicitudMaterial.setGenero(new Usuario(sesion));
        detalleSolicitudMaterial.setFechaGenero(new Date());
        detalleSolicitudMaterial.setHoraGenero(new Date());
        detalleSolicitudMaterial.setEliminado(Constantes.BOOLEAN_FALSE);
        //
        create(detalleSolicitudMaterial);
    }

    
    public void modificar(int idSolicitud, DetalleSolicitudMaterialAlmacenVo dsmav, String sesion) {
        InvDetalleSolicitudMaterial detalleSolicitudMaterial = find(dsmav.getId());
        detalleSolicitudMaterial.setInvArticulo(articuloRemote.buscarPorCodigo(dsmav.getCodigoArt(), dsmav.getIdUnidad()));
        detalleSolicitudMaterial.setReferencia(dsmav.getCodigoArt());
        detalleSolicitudMaterial.setCantidad(dsmav.getCantidad());
        detalleSolicitudMaterial.setModifico(new Usuario(sesion));
        detalleSolicitudMaterial.setFechaModifico(new Date());
        detalleSolicitudMaterial.setHoraModifico(new Date());
        //
        edit(detalleSolicitudMaterial);
    }

    
    public void eliminar(int idDetSolicitud, String sesion) {
        InvDetalleSolicitudMaterial detalleSolicitudMaterial = find(idDetSolicitud);
        detalleSolicitudMaterial.setModifico(new Usuario(sesion));
        detalleSolicitudMaterial.setFechaModifico(new Date());
        detalleSolicitudMaterial.setHoraModifico(new Date());
        detalleSolicitudMaterial.setEliminado(Constantes.BOOLEAN_TRUE);
        //
        edit(detalleSolicitudMaterial);
    }

    
    public void actualizaCantidadRecibida(List<DetalleSolicitudMaterialAlmacenVo> dsmav, String sesion) {
        for (DetalleSolicitudMaterialAlmacenVo dsVo : dsmav) {
            InvDetalleSolicitudMaterial detalleSolicitudMaterial = find(dsVo.getId());
            detalleSolicitudMaterial.setCantidadRecibida(dsVo.getCantidadRecibida());

            detalleSolicitudMaterial.setModifico(new Usuario(sesion));
            detalleSolicitudMaterial.setFechaModifico(new Date());
            detalleSolicitudMaterial.setHoraModifico(new Date());
            //
            edit(detalleSolicitudMaterial);
        }
    }
}
