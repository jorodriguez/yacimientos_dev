/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SgGym;
import sia.modelo.SgStaff;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgGymImpl extends AbstractFacade<SgGym>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgGymImpl() {
        super(SgGym.class);
    }

    
    public List<SgGym> getAllGimnasiosByStaff(SgStaff staff, boolean status) {
        System.out.println("SgGymImpl.getAllGimnasiosByStaff()");
        
        List<SgGym> gimnasiosList = null;
        
        if(staff != null) {
            try {
                gimnasiosList = em.createQuery("SELECT g FROM SgGym g WHERE g.eliminado = :estado AND g.sgStaff.id = :idStaff ORDER BY g.id")
                        .setParameter("estado", status)
                        .setParameter("idStaff", staff.getId())
                        .getResultList();
            }
            catch(Exception e) {
            UtilLog4j.log.error(e);
                return gimnasiosList;
            }
            
            if(gimnasiosList != null){
                return gimnasiosList;
            }
            else{
                return gimnasiosList;
            }
        }
        else {
            return gimnasiosList;
        }
    }        
}
