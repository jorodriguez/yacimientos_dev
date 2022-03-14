/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.evaluacion.impl;

import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvEvaluacionResp;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class CvEvaluacionRespImpl  extends AbstractFacade<CvEvaluacionResp> {
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    public CvEvaluacionRespImpl() {
        super(CvEvaluacionResp.class);
    }

   
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
