/*
 * ServiciosAdicionalesFacade.java
 * Creada el 10/09/2009, 01:37:29 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.almacen.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.ServicioAdicional;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 10/09/2009
 */
@LocalBean 
public class ServicioAdicionalImpl {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(ServicioAdicional serviciosAdicionales) {
        em.persist(serviciosAdicionales);
    }

    
    public void edit(ServicioAdicional serviciosAdicionales) {
        em.merge(serviciosAdicionales);
    }

    /**
     *
     * @param serviciosAdicionales
     */
    
    public void remove(ServicioAdicional serviciosAdicionales) {
        em.remove(em.merge(serviciosAdicionales));
    }

    
    public ServicioAdicional find(Object id) {
        return em.find(ServicioAdicional.class, id);
    }

    public List<ServicioAdicional> findAll() {
        return em.createQuery("select object(o) from ServiciosAdicionales as o").getResultList();
    }

    public List<ServicioAdicional> getPorServicioPrincipal(Object idServicioPrincipal) {
           return em.createQuery("SELECT s FROM ServicioAdicional s WHERE s.servicioPrincipal.id = :servicioPrincipal")
                .setParameter("servicioPrincipal", idServicioPrincipal)
                .getResultList();
    }


}
