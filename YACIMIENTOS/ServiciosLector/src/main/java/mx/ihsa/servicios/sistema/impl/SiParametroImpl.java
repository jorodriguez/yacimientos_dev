/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.servicios.sistema.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.ihsa.constantes.Constantes;
import mx.ihsa.modelo.SiParametro;
import mx.ihsa.sistema.AbstractImpl;

/**
 *
 */
@Stateless 
public class SiParametroImpl extends AbstractImpl<SiParametro> {
    
    public SiParametroImpl() {
        super(SiParametro.class);
    }
    
    
    
}
