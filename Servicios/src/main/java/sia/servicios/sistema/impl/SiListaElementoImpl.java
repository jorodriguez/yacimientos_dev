/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SiListaElemento;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */

@LocalBean 
public class SiListaElementoImpl  extends AbstractFacade<SiListaElemento> {
 @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiListaElementoImpl() {
        super(SiListaElemento.class);
    }
    
    
    
    public List<SelectItem> getListaElementos(String nombre, int id) {
        List<SelectItem> lst = null;
        try {
            StringBuilder sb = new StringBuilder();

            sb.append(" select le.ID, le.NOMBRE ");
            sb.append(" from SI_LISTA_ELEMENTO le ");
            if(nombre != null && !nombre.isEmpty()){
                sb.append(" inner join SI_LISTA l on l.ID = le.SI_LISTA and l.NOMBRE = '").append(nombre).append("' ");
            }            
            sb.append(" where le.ELIMINADO = 'False' ");
            if(nombre == null && id > 0){
                sb.append(" and le.SI_LISTA = ").append(id);
            }                        
            sb.append(" order by le.ID ");

            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());
            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                lst = new ArrayList<SelectItem>();
                for (Object[] objects : lo) {
                    lst.add(new SelectItem((Integer) objects[0], (String) objects[1]));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            lst = null;
        }
        return lst;
    }
}
