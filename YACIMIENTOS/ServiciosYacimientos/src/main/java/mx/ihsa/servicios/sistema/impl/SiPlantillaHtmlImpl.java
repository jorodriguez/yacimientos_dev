/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.servicios.sistema.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.ihsa.constantes.Constantes;
import mx.ihsa.modelo.SiPlantillaHtml;
import mx.ihsa.sistema.AbstractImpl;


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
