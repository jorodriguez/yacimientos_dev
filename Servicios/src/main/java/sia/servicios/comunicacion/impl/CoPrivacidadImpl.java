/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CoPrivacidad;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author hacosta
 */
@LocalBean 
public class CoPrivacidadImpl extends AbstractFacade<CoPrivacidad> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoPrivacidadImpl() {
        super(CoPrivacidad.class);
    }
    
    
    public CoPrivacidad buscarPorNombre(String nombre) {
        CoPrivacidad privacidad = null;
        try {
            privacidad = (CoPrivacidad) em.createQuery(
                "SELECT p FROM CoPrivacidad p WHERE p.nombre = :nombre").setParameter("nombre", nombre).getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(CoPrivacidadImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return privacidad;
    }
    
    
    public List<CoPrivacidad> getListaPrivacidad(){
        return em.createQuery("SELECT p FROM CoPrivacidad p WHERE p.eliminado = :eliminado AND p.visible = :visible")
                .setParameter("visible", true)
                .setParameter("eliminado", false).getResultList();
    }
}
