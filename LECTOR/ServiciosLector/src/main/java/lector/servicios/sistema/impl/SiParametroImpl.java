/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lector.constantes.Constantes;
import lector.modelo.SiParametro;
import lector.sistema.AbstractImpl;

/**
 *
 */
@Stateless 
public class SiParametroImpl extends AbstractImpl<SiParametro> {
    
    public SiParametroImpl() {
        super(SiParametro.class);
    }
    
    
    
}
