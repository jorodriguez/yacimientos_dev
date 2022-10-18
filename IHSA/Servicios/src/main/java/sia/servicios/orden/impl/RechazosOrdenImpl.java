/*
 * RechazosOrdenFacade.java
 * Creada el 13/10/2009, 06:06:32 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.orden.impl;


import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.RechazosOrden;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 13/10/2009
 */
@Stateless 
public class RechazosOrdenImpl{
    @PersistenceContext
    private EntityManager em;

    public void create(RechazosOrden rechazosOrden) {
        em.persist(rechazosOrden);
    }

    public void edit(RechazosOrden rechazosOrden) {
        em.merge(rechazosOrden);
    }

    public void remove(RechazosOrden rechazosOrden) {
        em.remove(em.merge(rechazosOrden));
    }

    public RechazosOrden find(Object id) {
        return em.find(RechazosOrden.class, id);
    }

    public List<RechazosOrden> findAll() {
        return em.createQuery("select object(o) from RechazosOrden as o").getResultList();
    }

    public List<RechazosOrden> getRechazosPorOrden(Object idOrden) {
        return em.createQuery("SELECT r FROM RechazosOrden r WHERE r.orden.id = :idOrden AND r.cumplido = :cumplido")
                .setParameter("idOrden", idOrden)
                .setParameter("cumplido", "No")
                .getResultList();
    }
}
