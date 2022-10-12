/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcGerenciaTarea;
import sia.modelo.requisicion.vo.GereciaTareaVo;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author ihsa
 */
@Stateless 
public class OcGerenciaTareaImpl extends AbstractFacade<OcGerenciaTarea> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcGerenciaTareaImpl() {
        super(OcGerenciaTarea.class);
    }

    
    public List<GereciaTareaVo> traerTareaTrabajo(int idGerencia, int idCampo) {
        clearQuery();
        query.append("select gt.ID, nt.NOMBRE, tt.NOMBRE from OC_GERENCIA_TAREA gt ");
        query.append(" inner join OC_TAREA_TRABAJO tt on gt.OC_TAREA_TRABAJO = tt.ID");
        query.append(" inner join OC_NOMBRE_TAREA nt on gt.OC_NOMBRE_TAREA = nt.ID");
        query.append(" inner join AP_CAMPO ac on gt.ap_campo = ac.ID");
        query.append(" where gt.GERENCIA = ").append(idGerencia);
        query.append(" and gt.ap_campo = ").append(idCampo);
        query.append(" and gt.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
        query.append(" order by nt.nombre asc");
        
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        //
        List<GereciaTareaVo> ltt = null;
        if (lo != null) {
            ltt = new ArrayList<GereciaTareaVo>();
            for (Object[] objects : lo) {
                ltt.add(castTareaTrabajo(objects));
            }
        }
        return ltt;
    }

    private GereciaTareaVo castTareaTrabajo(Object[] objects) {
        GereciaTareaVo gt = new GereciaTareaVo();
        gt.setTarea((String) objects[1]);
        gt.setTrabajo((String) objects[2]);
        return gt;
    }
}
