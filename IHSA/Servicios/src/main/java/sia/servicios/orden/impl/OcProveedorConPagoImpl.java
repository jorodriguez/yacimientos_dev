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
import sia.modelo.*;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.orden.vo.ProveedorConPagoVo;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author icristobal
 */
@Stateless 
public class OcProveedorConPagoImpl extends AbstractFacade<OcProveedorConPago>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    private CompaniaImpl companiaRemote;
    @Inject
    private ProveedorServicioImpl proveedorRemote;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcProveedorConPagoImpl() {
        super(OcProveedorConPago.class);
    }

    
    public List<ProveedorConPagoVo> findByNombrePro(String nombreProveedor) {
        List<ProveedorConPagoVo> lpc = null;
        try {
            clearQuery();
            appendQuery("SELECT r.ID,");
            appendQuery(" p.ID,");
            appendQuery(" p.NOMBRE,");
            appendQuery(" c.ID,");
            appendQuery(" c.NOMBRE,");
            appendQuery(" c.NOTIFICAR");
            appendQuery(" FROM OC_PROVEEDOR_CON_PAGO r,PROVEEDOR p,CONDICION_PAGO c");
            appendQuery(" WHERE p.NOMBRE ='").append(nombreProveedor).append("'");
            appendQuery(" and r.PROVEEDOR = p.ID");
            appendQuery(" and r.CONDICION_PAGO = c.ID");
            appendQuery(" and r.ELIMINADO = false");
            appendQuery(" ORDER BY r.ID ASC");


            UtilLog4j.log.debug(this, query.toString());

            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();


            if (lo != null && !lo.isEmpty()) {
                lpc = new ArrayList<ProveedorConPagoVo>();

                for (Object[] objects : lo) {
                    lpc.add(castProveedorConPagoVo(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
        return lpc;
    }

    
    public ProveedorConPagoVo findByNombres(String nombreProveedor, String nombreConPago, String rfcCompania ) {
        try {
            clearQuery();
            appendQuery("SELECT r.ID,");
            appendQuery(" p.ID,");
            appendQuery(" p.NOMBRE,");
            appendQuery(" c.ID,");
            appendQuery(" c.NOMBRE,");
            appendQuery(" c.NOTIFICAR");
            appendQuery(" FROM OC_PROVEEDOR_CON_PAGO r,PROVEEDOR p,CONDICION_PAGO c");
            appendQuery(" WHERE c.NOMBRE ='").append(nombreConPago).append("'");
            appendQuery(" AND p.NOMBRE ='").append(nombreProveedor).append("'");
            appendQuery(" AND r.compania ='").append(rfcCompania).append("'");
            appendQuery(" and r.PROVEEDOR = p.ID");
            appendQuery(" and r.CONDICION_PAGO = c.ID");
            appendQuery(" and r.ELIMINADO = false");

            UtilLog4j.log.debug(this, query.toString());

            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

            return castProveedorConPagoVo(obj);

        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }

    }

    
    public List<ProveedorConPagoVo> findByNombreConPago(String nombreConPago) {
        List<ProveedorConPagoVo> lpc = null;
        try {
            clearQuery();
            appendQuery("SELECT r.ID,");
            appendQuery(" p.ID,");
            appendQuery(" p.NOMBRE,");
            appendQuery(" c.ID,");
            appendQuery(" c.NOMBRE,");
            appendQuery(" c.NOTIFICAR");
            appendQuery(" FROM OC_PROVEEDOR_CON_PAGO r,PROVEEDOR p,CONDICION_PAGO c");
            appendQuery(" WHERE c.NOMBRE ='").append(nombreConPago).append("'");
            appendQuery(" and r.CONDICION_PAGO = c.ID");
            appendQuery(" and r.PROVEEDOR = p.ID");
            appendQuery(" and r.ELIMINADO = false");
            appendQuery(" ORDER BY r.ID ASC");


            UtilLog4j.log.debug(this, query.toString());

            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();

            if (lo != null && !lo.isEmpty()) {
                lpc = new ArrayList<ProveedorConPagoVo>();

                for (Object[] objects : lo) {
                    lpc.add(castProveedorConPagoVo(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
        return lpc;
    }

    private ProveedorConPagoVo castProveedorConPagoVo(Object[] objects) {
        ProveedorConPagoVo proveedorConPagoVo = new ProveedorConPagoVo();
        proveedorConPagoVo.setId((Integer) objects[0]);
        proveedorConPagoVo.setIdProveedor((Integer) objects[1]);
        proveedorConPagoVo.setNombreProveedor((String) objects[2]);
        proveedorConPagoVo.setIdConPago((Integer) objects[3]);
        proveedorConPagoVo.setNombreConPago((String) objects[4]);

        String notificar = (String) objects[5];

        if (notificar.equals(Constantes.BOOLEAN_FALSE)) {
            proveedorConPagoVo.setNotificar("No");
        } else {
            proveedorConPagoVo.setNotificar("Si");
        }

        return proveedorConPagoVo;
    }

    
    public void guardarProveedorConPago(CondicionPago condicionPago, int idProveedor, Usuario usuario, String rfcCompania) {
        try {
            OcProveedorConPago ocProveedorConPago = new OcProveedorConPago();
            ocProveedorConPago.setProveedor(proveedorRemote.find(idProveedor));
            ocProveedorConPago.setCondicionPago(condicionPago);
            ocProveedorConPago.setGenero(usuario);
            ocProveedorConPago.setCompania(companiaRemote.find(rfcCompania));
            ocProveedorConPago.setFechaGenero(new Date());
            ocProveedorConPago.setHoraGenero(new Date());
            ocProveedorConPago.setEliminado(Constantes.NO_ELIMINADO);
            create(ocProveedorConPago);
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
        }

    }

    
    public void actualizarProveedorConPago(ProveedorConPagoVo proveedorConPagoVo, CondicionPago condicionPago, Usuario usuario) {

        try {
            OcProveedorConPago original = super.find(proveedorConPagoVo.getId());//El registro seleccionado en la vista se pasa a entidad                      
            original.setCondicionPago(condicionPago);
            original.setModifico(usuario);
            original.setFechaModifico(new Date());
            original.setHoraModifico(new Date());

            super.edit(original);
            UtilLog4j.log.info(this, "OC_PROVEEDOR_CON_PAGO UPDATED SUCCESSFULLY");

        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
        }
    }

    
    public void eliminarProveedorConPago(ProveedorConPagoVo proveedorConPagoVo, Usuario usuario) {
        try {
            OcProveedorConPago original = find(proveedorConPagoVo.getId());//El registro seleccionado en la vista se pasa a entidad  
            original.setGenero(usuario);
            original.setFechaGenero(new Date());
            original.setHoraGenero(new Date());
            original.setEliminado(Constantes.ELIMINADO);
            edit(original);
            UtilLog4j.log.info(this, "OC_PROVEEDOR_CON_PAGO DELETED SUCCESSFULLY");
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
        }
    }

    /**
     * MLUIS
     *
     * @param idProveedor
     * @return
     */
    
    public List<ProveedorConPagoVo> traerCondicionPorIdProveedor(int idProveedor, String rfcCompania) {
        List<ProveedorConPagoVo> lpc = null;
        try {
            clearQuery();
            appendQuery("SELECT r.ID,");
            appendQuery(" p.ID,");
            appendQuery(" p.NOMBRE,");
            appendQuery(" c.ID,");
            appendQuery(" c.NOMBRE,");
            appendQuery(" c.NOTIFICAR");
            appendQuery(" FROM OC_PROVEEDOR_CON_PAGO r,PROVEEDOR p,CONDICION_PAGO c");
            appendQuery(" WHERE p.id = ").append(idProveedor);
            appendQuery(" and r.compania = '").append(rfcCompania).append("'");
            appendQuery(" and r.PROVEEDOR = p.ID");
            appendQuery(" and r.CONDICION_PAGO = c.ID");
            appendQuery(" and r.ELIMINADO = false");
            appendQuery(" ORDER BY r.ID ASC");
            UtilLog4j.log.debug(this, query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            UtilLog4j.log.info(this, "Size " + lo.size());
            if (lo != null && !lo.isEmpty()) {
                lpc = new ArrayList<ProveedorConPagoVo>();
                for (Object[] objects : lo) {
                    lpc.add(castProveedorConPagoVo(objects));
                }
            }
            return lpc;
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * MLUIS
     *
     * @param idProveedor
     * @return
     */
    
    public List<ProveedorConPagoVo> traerCondicionPorNombreProveedor(String nombre, String rfcCompania) {
        List<ProveedorConPagoVo> lpc = null;
        try {
            clearQuery();
            appendQuery("SELECT r.ID,");
            appendQuery(" p.ID,");
            appendQuery(" p.NOMBRE,");
            appendQuery(" c.ID,");
            appendQuery(" c.NOMBRE,");
            appendQuery(" c.NOTIFICAR");
            appendQuery(" FROM OC_PROVEEDOR_CON_PAGO r,PROVEEDOR p,CONDICION_PAGO c");
            appendQuery(" WHERE p.nombre = '").append(nombre).append("'");
            appendQuery(" and r.compania = '").append(rfcCompania).append("'");
            appendQuery(" and r.PROVEEDOR = p.ID");
            appendQuery(" and r.CONDICION_PAGO = c.ID");
            appendQuery(" and r.ELIMINADO = false");
            appendQuery(" ORDER BY r.ID ASC");
            UtilLog4j.log.debug(this, query.toString());
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (lo != null && !lo.isEmpty()) {
                lpc = new ArrayList<ProveedorConPagoVo>();
                for (Object[] objects : lo) {
                    lpc.add(castProveedorConPagoVo(objects));
                }
            }
            return lpc;
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }
    
     
    public ProveedorConPagoVo findByIds(int idProveedor, String nombreConPago, String rfcCompania ) {
        try {
            clearQuery();
            appendQuery("SELECT r.ID,");
            appendQuery(" p.ID,");
            appendQuery(" p.NOMBRE,");
            appendQuery(" c.ID,");
            appendQuery(" c.NOMBRE,");
            appendQuery(" c.NOTIFICAR");
            appendQuery(" FROM OC_PROVEEDOR_CON_PAGO r,PROVEEDOR p,CONDICION_PAGO c");
            appendQuery(" WHERE c.NOMBRE ='").append(nombreConPago).append("'");
            appendQuery(" AND p.id = ").append(idProveedor);
            appendQuery(" AND r.compania ='").append(rfcCompania).append("'");
            appendQuery(" and r.PROVEEDOR = p.ID");
            appendQuery(" and r.CONDICION_PAGO = c.ID");
            appendQuery(" and r.ELIMINADO = false");

            UtilLog4j.log.debug(this, query.toString());

            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();

            return castProveedorConPagoVo(obj);

        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }

    }
}
