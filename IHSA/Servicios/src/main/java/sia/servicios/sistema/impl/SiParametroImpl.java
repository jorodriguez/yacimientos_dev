/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SiParametro;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author sluis
 */
@Stateless 
public class SiParametroImpl extends AbstractFacade<SiParametro> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiParametroImpl() {
        super(SiParametro.class);
    }
    
}
