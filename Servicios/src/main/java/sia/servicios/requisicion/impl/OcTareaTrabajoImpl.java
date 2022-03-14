/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcTareaTrabajo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcTareaTrabajoImpl extends AbstractFacade<OcTareaTrabajo> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcTareaTrabajoImpl() {
        super(OcTareaTrabajo.class);
    }
    
}
