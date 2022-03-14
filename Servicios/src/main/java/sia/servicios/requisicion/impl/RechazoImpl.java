/*
 * RechazoImpl .java
 * Creada el 19/08/2009, 11:59:39 AM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.requisicion.impl;


import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.Rechazo;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 19/08/2009
 */
@LocalBean 
public class RechazoImpl {
    @PersistenceContext
    private EntityManager em;

    
    public void create(Rechazo rechazo) {
        em.persist(rechazo);
    }

    
    public void edit(Rechazo rechazo) {
        em.merge(rechazo);
    }

    
    public void remove(Rechazo rechazo) {
        em.remove(em.merge(rechazo));
    }

    
    public Rechazo find(Object id) {
        return em.find(Rechazo.class, id);
    }

    
    public List<Rechazo> findAll() {
        return em.createQuery("select object(o) from Rechazo as o").getResultList();
    }

    
    public List<Rechazo> getRechazosPorRequisicion(int idRequisicion) {
        return em.createNativeQuery("SELECT * FROM Rechazo r WHERE r.requisicion = ?", Rechazo.class)
                .setParameter(1, idRequisicion)
                .getResultList();
    }

    
    public List<Rechazo> getRechazosIncumplidos(int idRequisicion) {
        return em.createNativeQuery("SELECT * FROM Rechazo r WHERE r.requisicion = ? AND r.cumplido = ? ", Rechazo.class)
                .setParameter(1, idRequisicion)
                .setParameter(2, false)
                .getResultList();
    }

}
