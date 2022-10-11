/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.Actividad;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class ActividadImpl extends AbstractFacade<Actividad> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ActividadImpl() {
        super(Actividad.class);
    }

    
    public void agregarActividadNueva(String actividadNueva) {
        try {
            Actividad actividad = new Actividad();
            actividad.setNombre(actividadNueva);
            this.create(actividad);
        } catch (Exception e) {
            e.getMessage();
        }

    }

    
    public Actividad buscarPorNombre(String nombreActividad) {
        try {
            return (Actividad) em.createQuery("SELECT a FROM Actividad a WHERE a.nombre = :nombre").setParameter("nombre", nombreActividad).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
