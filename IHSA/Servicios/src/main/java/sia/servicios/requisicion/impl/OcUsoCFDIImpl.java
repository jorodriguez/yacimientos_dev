/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcUsoCFDI;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class OcUsoCFDIImpl extends AbstractFacade<OcUsoCFDI> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcUsoCFDIImpl() {
        super(OcUsoCFDI.class);
    }

    
    public List<OcUsoCFDI> traerCFDIPorTipo(String tipo) {
        try {
            return em.createNamedQuery("OcUsoCFDI.traerPorTipo", OcUsoCFDI.class).setParameter(1, tipo).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public OcUsoCFDI buscarPorCodigo(String usoCfdi, String tipo) {
           try {
            return (OcUsoCFDI) em.createNamedQuery("OcUsoCFDI.traerPorCodigo", OcUsoCFDI.class)
                    .setParameter(1, usoCfdi)
                    .setParameter(2, tipo)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
}
