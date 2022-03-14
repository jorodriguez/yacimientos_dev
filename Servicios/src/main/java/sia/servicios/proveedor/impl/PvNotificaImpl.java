/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.PvNotifica;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@LocalBean 
public class PvNotificaImpl extends AbstractFacade<PvNotifica> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvNotificaImpl() {
        super(PvNotifica.class);
    }

    
    public List<PvNotifica> traerEntregadaPorProveedor(String rfc) {
        return em.createQuery("SELECT f FROM PvNotifica f WHERE f.contacto.proveedor.rfc = :rfc AND f.entregada = :entregada").setParameter("entregada", true).setParameter("rfc", rfc).getResultList();
    }

    
    public List<PvNotifica> traerNotificacionNoEntregada(String rfc) {
    return em.createQuery("SELECT f FROM PvNotifica f WHERE f.contacto.proveedor.rfc = :rfc AND f.entregada = :entregada").setParameter("entregada", false).setParameter("rfc", rfc).getResultList();
    }
    
}
