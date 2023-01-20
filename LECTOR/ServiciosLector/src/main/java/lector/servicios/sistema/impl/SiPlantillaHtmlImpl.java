/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lector.constantes.Constantes;
import lector.modelo.SiPlantillaHtml;
import lector.sistema.AbstractFacade;


/**
 *
 * @author hacosta
 */
@Stateless 
public class SiPlantillaHtmlImpl extends AbstractFacade<SiPlantillaHtml>{
    @PersistenceContext(unitName =  Constantes.PERSISTENCE_UNIT)
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiPlantillaHtmlImpl() {
        super(SiPlantillaHtml.class);
    }
    
}
