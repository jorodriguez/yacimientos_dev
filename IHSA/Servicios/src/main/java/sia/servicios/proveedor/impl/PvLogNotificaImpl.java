/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.PvLogNotifica;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class PvLogNotificaImpl extends AbstractFacade<PvLogNotifica> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvLogNotificaImpl() {
        super(PvLogNotifica.class);
    }
    
    
    public List<PvLogNotifica> traerEntregadaPorProveedor(String rfc) {
        return em.createQuery("SELECT f FROM PvLogNotifica f WHERE f.contactoProveedor.proveedor.rfc = :rfc ORDER BY f.id ASC").setParameter("rfc", rfc).getResultList();
    }

    
    public List<PvLogNotifica> traerNotificacionNoEntregada(String rfc) {
    return em.createQuery("SELECT f FROM PvLogNotifica f WHERE f.contactoProveedor.proveedor.rfc = :rfc AND f.entregada = :entregada").setParameter("entregada", false).setParameter("rfc", rfc).getResultList();
    }
}
