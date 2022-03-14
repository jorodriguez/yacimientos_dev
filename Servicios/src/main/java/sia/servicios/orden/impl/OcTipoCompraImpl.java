/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcTipoCompra;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@LocalBean 
public class OcTipoCompraImpl extends AbstractFacade<OcTipoCompra>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcTipoCompraImpl() {
        super(OcTipoCompra.class);
    }
    
    
    public List<OcTipoCompra> traerTipoCompra() {
        return em.createNamedQuery("OcTipoCompra.findAll").getResultList();
    }
}
