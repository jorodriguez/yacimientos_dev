/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.inventarios.service;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.InvRack;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class InvRackImpl extends AbstractFacade<InvRack>  {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvRackImpl() {
        super(InvRack.class);
    }

    
    public List<InvRack> traerTodo() {
        return em.createNamedQuery("InvRack.findAll").getResultList();
    }

}
