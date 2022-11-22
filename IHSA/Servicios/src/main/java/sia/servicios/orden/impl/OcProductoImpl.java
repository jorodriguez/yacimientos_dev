/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcProducto;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;


/**
 *
 * @author ihsa
 */
@Stateless 
public class OcProductoImpl extends AbstractFacade<OcProducto> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcProductoImpl() {
        super(OcProducto.class);
    }
    
    
    public List<SelectItem> traerProducto(String rfcCompania) {
        StringBuilder sb = new StringBuilder();
        List<SelectItem> lrd = null;
        try {
            sb.append(" SELECT a.ID, p.NOMBRE, p.CODIGO ");
            sb.append(" FROM OC_PRODUCTO_compania a "
                    + "     inner join oc_producto p on a.oc_producto = p.id ");
            sb.append(" where  a.compania = '").append(rfcCompania).append("' and a.ELIMINADO = 'False' ");

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null && lo.size() > 0) {
                lrd = new ArrayList<SelectItem>();
                for (Object[] objects : lo) {
                    SelectItem item = new SelectItem((Integer) objects[0], String.valueOf(objects[1]));
                    lrd.add(item);                    
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al recuperar el catalogo de OcProductoVO : : " + e.getMessage());
        }
        return lrd;
    }
}
