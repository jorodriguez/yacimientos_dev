/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcNombreTarea;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcNombreTareaImpl extends AbstractFacade<OcNombreTarea> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcNombreTareaImpl() {
        super(OcNombreTarea.class);
    }

    
    public OcNombreTarea buscarPorNombre(String nombreTarea) {
        try {
            //return (OcNombreTarea) em.createNamedQuery("OcNombreTarea.buscarPorNombre").setParameter(1, nombreTarea).getSingleResult();
            return (OcNombreTarea) em.createNativeQuery("SELECT * FROM oc_nombre_tarea WHERE eliminado = false and unaccent(upper(nombre)) LIKE unaccent(upper('" + nombreTarea + "')) order by id limit 1", OcNombreTarea.class)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }

    
    public OcNombreTarea guardarNombreTarea(String nombreTarea, String sesion) {
        OcNombreTarea nt = new OcNombreTarea();
        try {
            nt.setNombre(nombreTarea);
            nt.setGenero(new Usuario(sesion));
            nt.setFechaGenero(new Date());
            nt.setHoraGenero(new Date());
            nt.setEliminado(Boolean.FALSE);
            //
            create(nt);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return nt;
    }
    
    
    public int existeTareaNombre(String nombre) {
        int existe = 0;
        try {
            OcNombreTarea obj = this.buscarPorNombre(nombre);
            if(obj != null && obj.getId() > 0){
                existe = obj.getId();
            }            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            existe = 0;
        }
        return existe;
    }
    
    
    public List<OcNombreTarea> getAllActive() {
        return em.createQuery("SELECT u FROM OcNombreTarea u where u.eliminado = false")                
                .getResultList();
    }

}
