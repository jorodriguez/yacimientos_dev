/*
 * ServicioImpl.java
 * Creada el 26/08/2009, 10:51:42 AM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.catalogos.impl;


import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.Servicio;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 26/08/2009
 */
@LocalBean 
public class ServicioImpl{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    public void create(Servicio servicio) {
        em.persist(servicio);
    }

    public void edit(Servicio servicio) {
        em.merge(servicio);
    }

    public void remove(Servicio servicio) {
        em.remove(em.merge(servicio));
    }

    public Servicio find(Object id) {
        return em.find(Servicio.class, id);
    }

    public List<Servicio> findAll(int posicionInicio, int tamañoFragmento) {
        return em.createQuery("select object(o) from Servicio as o")
                .setFirstResult(posicionInicio)
                .setMaxResults(tamañoFragmento)
                .getResultList();
    }

    
    public List<Servicio> traerTodos() {
       return em.createQuery("select object(o) from Servicio as o ORDER BY o.nombre ASC").getResultList();
    }

 
}
