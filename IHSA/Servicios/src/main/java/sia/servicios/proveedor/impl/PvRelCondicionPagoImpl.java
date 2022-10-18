/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.PvRelCondicionPago;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class PvRelCondicionPagoImpl extends AbstractFacade<PvRelCondicionPago>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvRelCondicionPagoImpl() {
        super(PvRelCondicionPago.class);
    }
    
}
