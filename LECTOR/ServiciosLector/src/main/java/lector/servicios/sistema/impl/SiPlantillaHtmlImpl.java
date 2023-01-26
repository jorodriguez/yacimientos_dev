/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.servicios.sistema.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lector.constantes.Constantes;
import lector.modelo.SiPlantillaHtml;
import lector.sistema.AbstractImpl;


/**
 *
 * @author hacosta
 */
@Stateless 
public class SiPlantillaHtmlImpl extends AbstractImpl<SiPlantillaHtml>{
    
    public SiPlantillaHtmlImpl() {
        super(SiPlantillaHtml.class);
    }
    
}
