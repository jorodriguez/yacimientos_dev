/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcRequisicionCheckcode;
import sia.modelo.orden.vo.OcRequisicionCheckcodeVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;
/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcRequisicionCheckcodeImpl  extends AbstractFacade<OcRequisicionCheckcode> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public OcRequisicionCheckcodeImpl() {
        super(OcRequisicionCheckcode.class);
    }

    
    
    public OcRequisicionCheckcodeVO getRequiCheckCode(int idOrden, int idRequisicion, String rfc) {
        OcRequisicionCheckcodeVO obj = null;
        StringBuilder querySB = new StringBuilder();
        try {            
            querySB.append(" select a.ID, a.REQUISICION, a.ORDEN, a.RFC, a.CHECKCODE,a.ELIMINADO, r.URL ");
            querySB.append(" from OC_REQUISICION_CHECKCODE a ");
            querySB.append(" inner join REQUISICION r on r.ID = a.REQUISICION ");
            querySB.append(" where a.ORDEN = ").append(idOrden);
            querySB.append(" and a.REQUISICION = ").append(idRequisicion);
            querySB.append(" and a.RFC = '").append(rfc).append("' ");
            querySB.append(" order by a.FECHA_GENERO desc ");

            List<Object[]> lo = em.createNativeQuery(querySB.toString()).getResultList();
            
            if(lo != null && lo.size() > 0){
                obj = castOcRequisicionCheckcode(lo.get(0));
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            
        }
        return obj;
    }
    
     private OcRequisicionCheckcodeVO castOcRequisicionCheckcode(Object[] objects) {
        OcRequisicionCheckcodeVO vo = new OcRequisicionCheckcodeVO();
        vo.setId((Integer) objects[0]);
        vo.setIdRequisicion((Integer) objects[1]);
        vo.setIdOrden((Integer) objects[2]);
        vo.setRfc((String) objects[3]);
        vo.setCheckcode((String) objects[4]);
        vo.setEliminado((Boolean) objects[5]);
        vo.setUrl((String) objects[6]);
        return vo;
    }
    
}
