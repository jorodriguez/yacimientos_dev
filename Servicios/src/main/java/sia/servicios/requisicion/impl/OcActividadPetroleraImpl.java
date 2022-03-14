/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcActividadPetrolera;
import sia.modelo.requisicion.vo.OcActividadVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class OcActividadPetroleraImpl extends AbstractFacade<OcActividadPetrolera> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcActividadPetroleraImpl() {
        super(OcActividadPetrolera.class);
    }

    
    public List<OcActividadVO> getActividadesVOs() {
        UtilLog4j.log.info(this, "#getActividadesVOs ");
        ArrayList<OcActividadVO> lst = new ArrayList<OcActividadVO>();
        try {
            String query = "select ID, CODIGO, NOMBRE from OC_ACTIVIDADPETROLERA where ELIMINADO = false ";

            UtilLog4j.log.info(this, "query" + query);
            
            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            OcActividadVO vo = null;
            if (lo != null) {                
                for (Object[] objects : lo) {
                    vo = new OcActividadVO();
                    vo.setId((Integer) objects[0]);
                    vo.setCodigo((String) objects[1]);
                    vo.setNombre((String) objects[2]);
                    lst.add(vo);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<OcActividadVO>();
        }
        return lst;
    }

    
    public List<SelectItem> getActividadesItems() {
    UtilLog4j.log.info(this, "#getActividadesVOs ");
        ArrayList<SelectItem> lst = new ArrayList<SelectItem>();
        try {
            String query = "select ID, CODIGO, NOMBRE from OC_ACTIVIDADPETROLERA where ELIMINADO = false ";

            UtilLog4j.log.info(this, "query" + query);
            
            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            SelectItem item = null;
            if (lo != null) {                
                for (Object[] objects : lo) {                    
                    item = new SelectItem((Integer) objects[0], (String) objects[2]);                    
                    lst.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al obtener los proyectos OT'S " + e.getMessage(), e);
            lst = new ArrayList<SelectItem>();
        }
        return lst;
    }
    
    
    public int validarActividadExiste(String codigo, String nombre) {
        UtilLog4j.log.info(this, "#validarActividadExiste ");
        int idAct = 0;
        try {
            String query = "select ID from OC_ACTIVIDADPETROLERA where ELIMINADO = false "
                    +" and codigo = '"+codigo+"' "
                    +" and upper(nombre) = upper('"+nombre+"') " ;

            UtilLog4j.log.info(this, "query" + query);
            
            List<Object[]> lo = em.createNativeQuery(query).getResultList();
            
            if (lo != null && lo.size() > 0) {                
                idAct = (Integer) ((Object)lo.get(0));
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al validar la existencia de una actividad petrolera " + e.getMessage(), e);
            idAct = 0;
            
        }
        return idAct;
    }

}
