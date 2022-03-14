/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.Impuesto;
import sia.servicios.sistema.vo.ImpuestoVO;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class ImpuestoImpl {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(Impuesto impuesto) {
        em.persist(impuesto);
    }

    
    public void edit(Impuesto impuesto) {
        em.merge(impuesto);
    }

    
    public void remove(Impuesto impuesto) {
        em.remove(em.merge(impuesto));
    }

    
    public Impuesto find(Object id) {
        return em.find(Impuesto.class, id);
    }

    
    public List<Impuesto> findAll() {
        return em.createQuery("select object(o) from Impuesto as o").getResultList();
    }

    
    public List<ImpuestoVO> traerImpuesto(String companiaID, int impuestoID) {
        List<ImpuestoVO> le = null;
        try {
            String sb = " select a.id, a.NOMBRE, a.VALOR, a.COMPANIA, m.NOMBRE "
                    + " from IMPUESTO a "
                    + " inner join COMPANIA m on m.rfc = a.COMPANIA "
                    + " where a.ELIMINADO = 'False' ";
            if (companiaID != null && !companiaID.isEmpty()) {
                sb += " and a.COMPANIA = '" + companiaID + "' ";
            }

            if (impuestoID > 0) {
                sb += " and a.ID = " + impuestoID;
            }

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<ImpuestoVO>();
            for (Object[] objects : lo) {
                ImpuestoVO or = new ImpuestoVO();
                or.setId((Integer) objects[0]);
                or.setNombre((String) objects[1]);                
                or.setValor((Double) objects[2]);                
                or.setRfc((String) objects[3]);
                or.setCompania((String) objects[4]);
                le.add(or);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
    }
    
    
    public List<SelectItem> traerImpuestoItems(String companiaID, int impuestoID) {
        List<SelectItem> le = null;
        try {
            String sb = " select a.id, a.NOMBRE, a.VALOR, a.COMPANIA, m.NOMBRE "
                    + " from IMPUESTO a "
                    + " inner join COMPANIA m on m.rfc = a.COMPANIA "
                    + " where a.ELIMINADO = 'False' ";
            if (companiaID != null && !companiaID.isEmpty()) {
                sb += " and a.COMPANIA = '" + companiaID + "' ";
            }

            if (impuestoID > 0) {
                sb += " and a.ID = " + impuestoID;
            }

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<SelectItem>();
            SelectItem item0 = new SelectItem(0, "Sin IVA");
            le.add(item0);
            for (Object[] objects : lo) {
                le.add(new SelectItem((Integer) objects[0], (String) objects[1]+" "+(Double) objects[2]+" %"));		                
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
    }
    
}

