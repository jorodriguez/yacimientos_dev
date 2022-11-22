/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.PvTipoPersona;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class PvTipoPersonaImpl extends AbstractFacade<PvTipoPersona> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvTipoPersonaImpl() {
        super(PvTipoPersona.class);
    }
    
}
