/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import com.newrelic.api.agent.Trace;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcActivoFijo;
import sia.modelo.Orden;
import sia.modelo.OrdenDetalle;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.OcActivoFijoVO;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcActivoFijoImpl{

    @Inject
    private OrdenDetalleImpl ordenDetalleRemote;
    @PersistenceContext
    private EntityManager em;

    
    public void create(OcActivoFijo actFijo) {
        em.persist(actFijo);
    }

    
    public void edit(OcActivoFijo actFijo) {
        em.merge(actFijo);
    }

    
    public void remove(OcActivoFijo actFijo) {
        em.remove(em.merge(actFijo));
    }

    
    public OcActivoFijo find(Object id) {
        return em.find(OcActivoFijo.class, id);
    }

    
    public List<OcActivoFijo> findAll() {
        return em.createQuery("select object(o) from OcActivoFijo as o").getResultList();
    }

    
    @Trace
    public List<OcActivoFijoVO> getDetActivoFijo(int ordenID, int ordenDetID) {
        List<OcActivoFijoVO> items = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT a.ID, a.CODIGO ");
        sql.append(" FROM OC_ACTIVO_FIJO a ");
        sql.append(" where a.ELIMINADO = false ");
        sql.append(" and a.ORDEN = ").append(ordenID);
        if (ordenDetID > 0) {
            sql.append(" and a.ORDEN_DETALLE = ").append(ordenDetID);
        }
        List<Object[]> lo = em.createNativeQuery(sql.toString()).getResultList();

        for (Object[] objects : lo) {
            items.add(castActivoFijo(objects));
        }

        return items;
    }

    private OcActivoFijoVO castActivoFijo(Object[] objects) {
        OcActivoFijoVO item = new OcActivoFijoVO();
        item.setId((Integer) objects[0]);
        item.setCodigo((String) objects[1]);
        item.setOldCodigo((String) objects[1]);
        return item;
    }

    
    public int afCompletos(int ordenID) {        
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append(" cast((cast(sum(cast(TRUNC(o.CANTIDAD) as integer)) as integer) - (select COUNT(ID) from OC_ACTIVO_FIJO af where af.ORDEN = ").append(ordenID).append(" and af.ELIMINADO = false)) as integer) as resultado ");
        sql.append(" FROM ORDEN_DETALLE o ");
        sql.append(" where (o.ELIMINADO is null or o.ELIMINADO = false) ");
        sql.append(" and o.ORDEN = ").append(ordenID);
        System.out.println("asasdaSD: " + sql.toString());
        return ((int) em.createNativeQuery(sql.toString()).getSingleResult());
    }

    
    public void completarActivosFijo(Orden orden, OrdenDetalle linea, Usuario usrConectado, List<OcActivoFijoVO> navCodes) throws Exception {
        try {
            boolean esPrimero = true;
            String principal = "";
            for (OcActivoFijoVO itemVO : navCodes) {
                if (itemVO != null) {
                    if(esPrimero){
                        principal = itemVO.getCodigo();
                        esPrimero = false;
                    }
                    if (itemVO.getId() > 0 && itemVO.getCodigo() != null 
                            && (!itemVO.getCodigo().isEmpty() || (principal != null && !principal.isEmpty()))
                            && (itemVO.getOldCodigo() == null || !itemVO.getOldCodigo().equals(itemVO.getCodigo()))) {
                        OcActivoFijo item = find(itemVO.getId());                        
                        item.setCodigo(itemVO.getCodigo().isEmpty() ? principal : itemVO.getCodigo());
                        item.setModifico(usrConectado);
                        item.setFechaModifico(new Date());
                        item.setHoraModifico(new Date());
                        edit(item);
                    } else if (itemVO.getId() == 0 && itemVO.getCodigo() != null && !itemVO.getCodigo().isEmpty()) {
                        OcActivoFijo item = new OcActivoFijo();
                        item.setCodigo(itemVO.getCodigo());
                        item.setGenero(usrConectado);
                        item.setFechaGenero(new Date());
                        item.setHoraGenero(new Date());
                        item.setEliminado(Constantes.BOOLEAN_FALSE);
                        item.setOrden(orden);
                        item.setOrden_detalle(linea);
                        create(item);
                    } else if (itemVO.getId() == 0 && principal != null && !principal.isEmpty() && (itemVO.getCodigo() == null || itemVO.getCodigo().isEmpty())) {
                        OcActivoFijo item = new OcActivoFijo();
                        item.setCodigo(principal);
                        item.setGenero(usrConectado);
                        item.setFechaGenero(new Date());
                        item.setHoraGenero(new Date());
                        item.setEliminado(Constantes.BOOLEAN_FALSE);
                        item.setOrden(orden);
                        item.setOrden_detalle(linea);
                        create(item);
                    }
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    
    public void allNewAF(Orden orden, Usuario usrConectado, List<OrdenDetalle> lstDet, OcActivoFijoVO navCode) throws Exception {
        try {
            for (OrdenDetalle dtVO : lstDet) {
                int cantidad = dtVO.getCantidad().intValue();
                if (cantidad < 1) {
                    cantidad = 1;
                }
                for (int i = 0; i < cantidad; i++) {
                    OcActivoFijo item = new OcActivoFijo();
                    item.setCodigo(navCode.getCodigo());
                    item.setGenero(usrConectado);
                    item.setFechaGenero(new Date());
                    item.setHoraGenero(new Date());
                    item.setEliminado(Constantes.BOOLEAN_FALSE);
                    item.setOrden(orden);
                    item.setOrden_detalle(dtVO);
                    create(item);
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    
    public void allUpdateAF(Orden orden, Usuario usrConectado, List<OcActivoFijoVO> navCodes, OcActivoFijoVO navCode) throws Exception {
        try {
            for (OcActivoFijoVO itemVO : navCodes) {
                OcActivoFijo item = find(itemVO.getId());
                item.setCodigo(navCode.getCodigo());
                item.setModifico(usrConectado);
                item.setFechaModifico(new Date());
                item.setHoraModifico(new Date());
                edit(item);
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    
    public void newUpdateAF(Orden orden, Usuario usrConectado, List<OrdenDetalle> lstDet, List<OcActivoFijoVO> navCodes, OcActivoFijoVO navCode) throws Exception {
        try {
            this.allUpdateAF(orden, usrConectado, navCodes, navCode);
            for (OrdenDetalle detVO : lstDet) {
                int cantidad = detVO.getCantidad().intValue();
                List<OcActivoFijoVO> lstAF = this.getDetActivoFijo(orden.getId(), detVO.getId());
                if (cantidad < 1) {
                    cantidad = 1;
                }                
                if (lstAF.isEmpty() || lstAF.size() < cantidad) {
                    for (int i = 0; i < (cantidad-lstAF.size()); i++) {
                        OcActivoFijo item = new OcActivoFijo();
                        item.setCodigo(navCode.getCodigo());
                        item.setGenero(usrConectado);
                        item.setFechaGenero(new Date());
                        item.setHoraGenero(new Date());
                        item.setEliminado(Constantes.BOOLEAN_FALSE);
                        item.setOrden(orden);
                        item.setOrden_detalle(detVO);
                        create(item);
                    }
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    
    public boolean unicActivosFijo(Orden orden, List<OcActivoFijoVO> navCodes) throws Exception {
        boolean ret = false;
        try {
            StringBuilder codigos = new StringBuilder();
            OcActivoFijoVO itemVO = null;
            for (int i = 0; i < navCodes.size(); i++) {
                itemVO = navCodes.get(i);
                if (i == (navCodes.size() - 1)) {
                    codigos.append("'").append(itemVO.getCodigo()).append("'");
                } else {
                    codigos.append("'").append(itemVO.getCodigo()).append("', ");
                }

            }

            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT COUNT(a.CODIGO) ");
            sql.append(" FROM OC_ACTIVO_FIJO a ");

            if (orden != null && orden.getId() > 0) {
                sql.append(" inner join AUTORIZACIONES_ORDEN ao on ao.ORDEN = a.ORDEN  ");
                sql.append(" WHERE a.ELIMINADO = false ");
                sql.append(" and a.ORDEN <>  ").append(orden.getId());
                sql.append(" and ao.ESTATUS <>  ").append(Constantes.ORDENES_CANCELADAS);

            } else {
                sql.append(" WHERE a.ELIMINADO = false ");
            }
            sql.append(" AND a.CODIGO IN (");
            sql.append(codigos);
            sql.append(" ) ");
            long afs = ((long) em.createNativeQuery(sql.toString()).getSingleResult());

            ret = afs == 0;

        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
        return ret;
    }
}
