/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SiPlantillaHtml;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author hacosta
 */
@Stateless 
public class SiPlantillaHtmlImpl extends AbstractFacade<SiPlantillaHtml>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiPlantillaHtmlImpl() {
        super(SiPlantillaHtml.class);
    }
    
}
