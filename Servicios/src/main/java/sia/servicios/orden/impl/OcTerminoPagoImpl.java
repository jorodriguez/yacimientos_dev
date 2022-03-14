/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcTerminoPago;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.vo.GeneralVo;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcTerminoPagoImpl extends AbstractFacade<OcTerminoPago>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcTerminoPagoImpl() {
        super(OcTerminoPago.class);
    }
    
    
    
    public List<GeneralVo> listaTerminoPago(String compania){
        try {
            clearQuery();
            query.append("select tp.id, tp.nombre from oc_termino_pago tp ");
            query.append(" where tp.eliminado = 'False'");
            if(compania != null && !compania.isEmpty()){
                query.append(" and tp.COMPANIA = '").append(compania).append("' ");
            }            
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<GeneralVo> lg = null;
            if (lo != null) {
                 lg = new ArrayList<GeneralVo>();
                 for (Object[] objects : lo) {
                    lg.add(castTerminoPago(objects));
                }
            }
            return lg;
        } catch (Exception e) {
            return null;
        }
    }

    private GeneralVo castTerminoPago(Object[] objects) {
        GeneralVo g = new GeneralVo();
        g.setValor((Integer) objects[0]);
        g.setNombre((String) objects[1]);
        return g;
        
    }
    
    
}
