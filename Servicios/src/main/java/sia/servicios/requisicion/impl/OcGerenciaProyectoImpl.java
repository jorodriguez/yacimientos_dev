/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcGerenciaProyecto;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcGerenciaProyectoImpl extends AbstractFacade<OcGerenciaProyecto> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcGerenciaProyectoImpl() {
        super(OcGerenciaProyecto.class);
    }
    @Inject
    private GerenciaImpl gerenciaRemote;

    
    public List<GerenciaVo> traerGerencia(int idCampo) {
        clearQuery();
        try {
            query.append("select distinct(g.ID), g.NOMBRE, g.abrev from OC_GERENCIA_PROYECTO gp ");
            query.append(" inner join GERENCIA g on gp.GERENCIA = g.ID");
            query.append(" inner join ap_CAMPO ac on gp.AP_CAMPO = ac.ID");
            query.append(" where gp.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
            query.append(" and gp.ap_campo = ").append(idCampo);
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<GerenciaVo> lger = null;
            if (lo != null && lo.size() > 0) {
                lger = new ArrayList<GerenciaVo>();
                for (Object[] objects : lo) {
                    lger.add(castGerencia(objects));
                }
//                lger = gerenciaAbrev(lg);
            }
            return lger;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepción al traer las gerencias secundarias por abrev + + + + " + e.getMessage());
        }
        return null;
    }

    private List<GerenciaVo> gerenciaAbrev(List<GerenciaVo> lg) {
        List<GerenciaVo> lger = new ArrayList<GerenciaVo>();
        try {
            for (GerenciaVo gerenciaVo : lg) {
                lger.addAll(gerenciaRemote.traerGerenciaVoSecundariaAbreviatura(gerenciaVo.getAbrev()));
            }

        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lger;
    }

    
    public List<ProyectoOtVo> traerProyectoOt(int idGerencia, int idCampo) {
        clearQuery();
        query.append("select pot.ID, pot.NOMBRE from OC_GERENCIA_PROYECTO gp");
        query.append(" inner join PROYECTO_OT pot on gp.PROYECTO_OT = pot.ID");
        query.append(" inner join ap_CAMPO ac on gp.AP_CAMPO = ac.ID");
        query.append(" where gp.GERENCIA = ").append(idGerencia);
        query.append(" and gp.ap_campo = ").append(idCampo);
        query.append(" and gp.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<ProyectoOtVo> lg = null;
        if (lo != null && lo.size() > 0) {
            lg = new ArrayList<ProyectoOtVo>();
            for (Object[] objects : lo) {
                lg.add(castProyecto(objects));
            }
        }
        return lg;
    }

    
    public List<SelectItem> traerProyectoOtItems(int idGerencia, int idCampo) {
        clearQuery();
        query.append("select pot.ID, pot.NOMBRE from OC_GERENCIA_PROYECTO gp");
        query.append(" inner join PROYECTO_OT pot on gp.PROYECTO_OT = pot.ID");
        query.append(" inner join ap_CAMPO ac on gp.AP_CAMPO = ac.ID");
        query.append(" where gp.GERENCIA = ").append(idGerencia);
        query.append(" and gp.ap_campo = ").append(idCampo);
        query.append(" and gp.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<SelectItem> lg = null;
        if (lo != null && lo.size() > 0) {
            lg = new ArrayList<SelectItem>();
            SelectItem item = null;
            for (Object[] objects : lo) {
                item = new SelectItem((Integer) objects[0], (String) objects[1]);
                lg.add(item);
            }
        }
        return lg;
    }

    private GerenciaVo castGerencia(Object[] obj) {
        GerenciaVo g = new GerenciaVo();
        g.setId((Integer) obj[0]);
        g.setNombre((String) obj[1]);
        g.setAbrev((String) obj[2]);
        return g;
    }

    private ProyectoOtVo castProyecto(Object[] obj) {
        ProyectoOtVo g = new ProyectoOtVo();
        g.setId((Integer) obj[0]);
        g.setNombre((String) obj[1]);
        return g;
    }

    
    public List<OcGerenciaProyecto> traerTodoPorCampo(int idCampo) {
        try {
            return em.createNamedQuery("OcGerenciaProyecto.trearPorCampo").setParameter(1, idCampo).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }

    }
}
