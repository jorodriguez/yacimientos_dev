/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SgCocina;
import sia.modelo.SgStaff;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgCocinaImpl extends AbstractFacade<SgCocina> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgCocinaImpl() {
        super(SgCocina.class);
    }

    
    public List<SgCocina> getAllCocinasByStaff(SgStaff staff, boolean status) {
//        System.out.println("SgCocinaImpl.getAllCocinasByStaff()");

        List<SgCocina> cocinasList = null;

        if (staff != null) {
            try {
                cocinasList = em.createQuery("SELECT g FROM SgCocina g WHERE g.eliminado = :estado AND g.sgStaff.id = :idStaff ORDER BY g.id")
                        .setParameter("estado", status)
                        .setParameter("idStaff", staff.getId())
                        .getResultList();
            } catch (Exception e) {
                UtilLog4j.log.error(e);
                return cocinasList;
            }
        } else {
            return cocinasList;
        }
        return cocinasList;
    }
}
