/*
 * PrioridadImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.catalogos.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.Prioridad;


/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 7/07/2009
 */
@LocalBean 
public class PrioridadImpl {

    @PersistenceContext
    private EntityManager em;

    public void create(Prioridad prioridad) {
        em.persist(prioridad);
    }

    public void edit(Prioridad prioridad) {
        em.merge(prioridad);
    }

    public void remove(Prioridad prioridad) {
        em.remove(em.merge(prioridad));
    }

    public Prioridad find(Object id) {
        return em.find(Prioridad.class, id);
    }

    public List<Prioridad> findAll() {
        return em.createNamedQuery("Prioridad.findAll").getResultList();
    }

}
