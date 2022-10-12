/*
 * MonedaImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Moneda;
import sia.servicios.sistema.vo.MonedaVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 7/07/2009
 */
@Stateless 
public class MonedaImpl {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(Moneda moneda) {
        em.persist(moneda);
    }

    
    public void edit(Moneda moneda) {
        em.merge(moneda);
    }

    
    public void remove(Moneda moneda) {
        em.remove(em.merge(moneda));
    }

    
    public Moneda find(Object id) {
        return em.find(Moneda.class, id);
    }

    
    public List<Moneda> findAll() {
        return em.createQuery("select object(o) from Moneda as o").getResultList();
    }

    
    public Moneda buscarPorNombre(String nombreMoneda, String compania) {
        return (Moneda) em.createQuery("SELECT m FROM Moneda m WHERE m.eliminado = :eli AND m.nombre = :nombre AND m.compania.rfc = :compania").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("nombre", nombreMoneda).setParameter("compania", compania).getSingleResult();
    }

    
    public List<MonedaVO> traerMonedasPorCompania(String companiaID, int monedaID) {
        List<MonedaVO> le = null;
        try {
            String sb = " select a.ID, a.NOMBRE, a.SIGLAS, a.ELIMINADO, a.COMPANIA "
                    + " from MONEDA a "
                    + " where COMPANIA = '" + companiaID + "' ";
            if (monedaID > 0) {
                sb += " and ID = " + monedaID;
            }

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<MonedaVO>();
            for (Object[] objects : lo) {
                MonedaVO or = new MonedaVO();
                or.setId((Integer) objects[0]);
                or.setNombre((String) objects[1]);
                or.setSiglas((String) objects[2]);
                or.setActivo((Boolean) objects[3]);
                or.setCompania((String) objects[4]);
                le.add(or);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
    }

    
    public List<SelectItem> traerMonedasPorCompaniaItems(String companiaID, String siglas) {
        List<SelectItem> le = null;
        try {
            String sb = " select a.ID, a.NOMBRE, a.SIGLAS, a.ELIMINADO, a.COMPANIA "
                    + " from MONEDA a "
                    + " where COMPANIA = '" + companiaID + "' ";
            if (siglas != null && !siglas.isEmpty()) {
                sb += " and a.SIGLAS <> '" + siglas + "' ";
            }

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<SelectItem>();
            for (Object[] objects : lo) {
                le.add(new SelectItem((Integer) objects[0], (String) objects[1]));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
    }

    
    public List<MonedaVO> traerMonedaActiva(int apCampoID) {
        List<MonedaVO> le = null;
        try {
            String sb = " select m.ID, m.NOMBRE, m.SIGLAS, m.ELIMINADO, m.COMPANIA "
                    + " from moneda m "
                    + " inner join AP_CAMPO c on c.COMPANIA = m.COMPANIA and c.ELIMINADO = 'False' "
                    + " where m.ELIMINADO = 'False' "
                    + " and  c.ID = " + apCampoID;

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<MonedaVO>();
            for (Object[] objects : lo) {
                MonedaVO or = new MonedaVO();
                or.setId((Integer) objects[0]);
                or.setNombre((String) objects[1]);
                or.setSiglas((String) objects[2]);
                or.setActivo((Boolean) objects[3]);
                or.setCompania((String) objects[4]);
                le.add(or);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
    }

    
    public List<MonedaVO> traerPorCompania(String companiaID) {
        List<MonedaVO> le = null;
        try {
            String sb = " select a.ID, a.NOMBRE, a.SIGLAS "
                    + " from MONEDA a "
                    + " where a.COMPANIA = '" + companiaID + "' "
                    + " and a.eliminado = false ";

            List<Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<>();
            for (Object[] objects : lo) {
                MonedaVO or = new MonedaVO();
                or.setId((Integer) objects[0]);
                or.setNombre((String) objects[1]);
                or.setSiglas((String) objects[2]);
                le.add(or);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
    }
}
