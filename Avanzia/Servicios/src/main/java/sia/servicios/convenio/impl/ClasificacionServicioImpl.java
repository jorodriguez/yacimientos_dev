/*
 * TituloFacade.java
 * Creada el 22/09/2009, 09:00:27 AM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.convenio.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.ClasificacionServicio;
import sia.modelo.ProveedorActividad;
import sia.servicios.proveedor.impl.ProveedorActividadImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com
 * @date 22/09/2009
 */
@Stateless 
public class ClasificacionServicioImpl {

    @PersistenceContext
    private EntityManager em;
    //--- Utilizando otro EJB
    @Inject
    private ProveedorActividadImpl proveedorActividadServicioRemoto;

    
    public void create(ClasificacionServicio clasificacionServicio) {
        em.persist(clasificacionServicio);
    }

    
    public void edit(ClasificacionServicio clasificacionServicio) {
        em.merge(clasificacionServicio);
    }

    
    public void remove(ClasificacionServicio clasificacionServicio) {
        em.remove(em.merge(clasificacionServicio));
    }

    
    public ClasificacionServicio find(Object id) {
        return em.find(ClasificacionServicio.class, id);
    }

    
    public List<ClasificacionServicio> findAll() {
        return em.createQuery("select object(o) from Titulo as o").getResultList();
    }
//
//    public ClasificacionServicio buscarPorNombre(Object nombre){
//        return
//    }

    
    public List<ClasificacionServicio> getPorProveedorActividad(Object proveedor, Object actividad) {
        return em.createQuery("SELECT c FROM ClasificacionServicio c WHERE c.proveedorActividad.id = :proveedorActividad ORDER BY c.nombre ASC")
                .setParameter("proveedorActividad", this.proveedorActividadServicioRemoto.buscarPorProveedorActividad(proveedor, actividad))
                .getResultList();
    }

    
    public List<ClasificacionServicio> traerClasificacionPorProveedorActividad(int actividad) {
        return em.createQuery("SELECT c FROM ClasificacionServicio c WHERE c.proveedorActividad.id = :a").setParameter("a", actividad).getResultList();

    }

    
    public List<ClasificacionServicio> traerClasificacionPorActividad(String proActividad, int actividad) {
        return em.createQuery("SELECT DISTINCT c FROM ClasificacionServicio c WHERE c.proveedorActividad.proveedor.nombre = :ac AND c.proveedorActividad.actividad.id = :a").setParameter("a", actividad).setParameter("ac", proActividad).getResultList();
    }

    
    public boolean agregarServicioProveedor(int proveedor, int proveedorActividad, String servicio) {
        boolean v = false;
        ClasificacionServicio clasificacionServicio = new ClasificacionServicio();
        try {
            ProveedorActividad pa = proveedorActividadServicioRemoto.find(proveedorActividad);
            if (pa != null) {
                clasificacionServicio.setProveedorActividad(pa);
                clasificacionServicio.setNombre(servicio);
                this.create(clasificacionServicio);
                v = true;
            } else {
                return v;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return v;

    }
}
