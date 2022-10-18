/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SgGastoInsumo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgGastoInsumoImpl extends AbstractFacade<SgGastoInsumo>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgGastoInsumoImpl() {
        super(SgGastoInsumo.class);
    }
    
}
