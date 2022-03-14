/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SiNivel;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SiNivelImpl extends AbstractFacade<SiNivel>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiNivelImpl() {
        super(SiNivel.class);
    }

    
    public List<SiNivel> traerTodo() {
        try {
            return em.createNamedQuery("SiNivel.findAll").getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public SiNivel bucarPorCodigo(String codigo) {
        try {
            return (SiNivel) em.createNamedQuery("SiNivel.findCodigo", SiNivel.class).setParameter(1, codigo).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

}
