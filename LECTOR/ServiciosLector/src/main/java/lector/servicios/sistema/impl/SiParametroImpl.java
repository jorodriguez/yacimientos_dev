/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lector.constantes.Constantes;
import lector.modelo.SiParametro;
import lector.sistema.AbstractFacade;

/**
 *
 */
@Stateless 
public class SiParametroImpl extends AbstractFacade<SiParametro> {
    @PersistenceContext(unitName =  Constantes.PERSISTENCE_UNIT)
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiParametroImpl() {
        super(SiParametro.class);
    }
    
}
