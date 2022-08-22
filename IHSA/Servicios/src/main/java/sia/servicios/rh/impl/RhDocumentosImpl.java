/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.rh.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.RhDocumentos;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class RhDocumentosImpl extends AbstractFacade<RhDocumentos> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RhDocumentosImpl() {
        super(RhDocumentos.class);
    }

    
    public List<RhDocumentos> traerTodo() {
        return em.createNamedQuery("RhDocumentos.findAll").getResultList();
    }

    
    public List<RhDocumentos> traerDocumentosPeriodicos() {
        return em.createNamedQuery("RhDocumentos.documentosPeriodicos").getResultList();
    }
    
    
    public List<RhDocumentos> traerDocumentosNoPeriodicos() {
        return em.createNamedQuery("RhDocumentos.documentosNoPeriodicos").getResultList();
    }

}
