/*
 * ProveedorActividadFacade.java
 * Creada el 21/09/2009, 05:00:42 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.proveedor.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.ProveedorActividad;
import sia.modelo.Usuario;
import sia.servicios.convenio.impl.ActividadImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 21/09/2009
 */
@LocalBean 
public class ProveedorActividadImpl{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;
    @Inject
    private ActividadImpl actividadServicioRemoto;

    
    public void create(ProveedorActividad proveedorActividad) {
	em.persist(proveedorActividad);
    }

    
    public void edit(ProveedorActividad proveedorActividad) {
	em.merge(proveedorActividad);
    }

    
    public void remove(ProveedorActividad proveedorActividad) {
	em.remove(em.merge(proveedorActividad));
    }

    
    public ProveedorActividad find(Object id) {
	return em.find(ProveedorActividad.class, id);
    }

    
    public Integer buscarPorProveedorActividad(Object proveedor, Object actividad) {
	List<ProveedorActividad> ListaTemporal = em.createQuery("SELECT p FROM ProveedorActividad p WHERE p.proveedor.nombre = :proveedor AND p.actividad.nombre = :actividad ORDER BY p.actividad.nombre ASC").setParameter("proveedor", proveedor).setParameter("actividad", actividad).getResultList();

	if (ListaTemporal.isEmpty()) {
	    return 0;
	} else {
	    return ListaTemporal.get(0).getId();
	}
    }

    
    public List<ProveedorActividad> findAll() {
	return em.createQuery("select object(o) from ProveedorActividad as o").getResultList();
    }

    
    public List<ProveedorActividad> getPorProveedor(Object nombreProveedor) {
	return em.createQuery("SELECT p FROM ProveedorActividad p WHERE p.proveedor.nombre = :nombreProveedor ORDER BY p.actividad.nombre ASC").setParameter("nombreProveedor", nombreProveedor).getResultList();
    }

    //NUEVOS
    
    public List<ProveedorActividad> traerActividadProveedor(String proveedor) {
	return em.createQuery("SELECT p FROM ProveedorActividad p WHERE p.proveedor.nombre = :pro ORDER BY p.id ASC").setParameter("pro", proveedor).getResultList();
    }

    //NUEVOS
    
    public List<ProveedorActividad> traerActividadIdProveedor(int idProveedor) {
	//System.out.println("Proveedor: servcio:  " + proveedor);
	return em.createQuery("SELECT p FROM ProveedorActividad p WHERE p.proveedor.id = :pro ORDER BY p.id ASC").setParameter("pro", idProveedor).getResultList();
    }

    
    public List<ProveedorActividad> buscarProveedorPorActividad(int actividad) {
	return em.createQuery("SELECT p FROM ProveedorActividad p WHERE p.actividad.id = :act ORDER BY p.id ASC").setParameter("act", actividad).getResultList();
    }

    
    public List<ProveedorActividad> traerActividadPorProveedor(String proveedor) {
	return em.createQuery("SELEcT f FROM ProveedorActividad f WHERE f.proveedor.nombre = :prov ORDER BY f.id ASC").setParameter("prov", proveedor).getResultList();
    }

    
    public void agregarActividad(int idProveedor, int idActividad, String sesion) {
	ProveedorActividad proveedorActividad = new ProveedorActividad();
	proveedorActividad.setProveedor(this.proveedorServicioRemoto.find(idProveedor));
	proveedorActividad.setActividad(this.actividadServicioRemoto.find(idActividad));
	proveedorActividad.setGenero(new Usuario(sesion));
	proveedorActividad.setFechaGenero(new Date());
	proveedorActividad.setHoraGenero(new Date());
	proveedorActividad.setEliminado(Constantes.NO_ELIMINADO);
	this.create(proveedorActividad);
    }

    
    public ProveedorActividad porProveedorActividad(String proveedor, int actividad) {
	try {
	    return (ProveedorActividad) em.createQuery("SELECT p FROM ProveedorActividad p WHERE p.proveedor.nombre = :pro AND p.actividad.id = :act").setParameter("pro", proveedor).setParameter("act", actividad).getSingleResult();
	} catch (Exception e) {
	    return null;
	}

    }

    
    public ProveedorActividad buscarPorProveedorActividad(int idProveedor, int actividad) {
	try {
	    return (ProveedorActividad) em.createQuery("SELECT p FROM ProveedorActividad p WHERE p.proveedor.id = :pro AND p.actividad.id = :act").setParameter("pro", idProveedor).setParameter("act", actividad).getSingleResult();
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}

    }
}
