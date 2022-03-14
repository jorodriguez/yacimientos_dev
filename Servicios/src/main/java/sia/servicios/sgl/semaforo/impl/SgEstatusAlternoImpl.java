/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.semaforo.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.SgEstatusAlterno;
import sia.modelo.cadena.aprobacion.vo.EstatusAlternoVO;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgEstatusAlternoImpl extends AbstractFacade<SgEstatusAlterno> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgEstatusAlternoImpl() {
        super(SgEstatusAlterno.class);
    }

    /*
     *Verifica si el estatus de parametro con el semaforo de parametro se encuentran en la tabla estatus Alterno
     * Se utiliza en el proceso de cadenas de aprobacion
     * Joel Rodriguez
     * 14/nov/2013
     */
    
    public boolean verificarSemaforoAlternoYEstatus(int idEstatus, int idSemaforo) {
        List li=null;
        try {
            clearQuery();
            appendQuery(" Select e.id");
            appendQuery(" From sg_estatus_alterno e");
            appendQuery(" Where e.ELIMINADO = 'False' and e.SG_SEMAFORO = ");
            appendQuery(idSemaforo);
            appendQuery(" and e.ESTATUS = ");
            appendQuery(idEstatus);
            li = em.createNativeQuery(getStringQuery()).getResultList();
            if(li!=null){
                return !li.isEmpty() ? true:false;
            }else return false;
                
        } catch (NonUniqueResultException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    
    public List<EstatusAlternoVO> traerTodo() {
        try {
            clearQuery();
            List<EstatusAlternoVO> le = new ArrayList<EstatusAlternoVO>();
            appendQuery(" Select ea.id, ea.estatus, est.nombre");
            appendQuery(" From sg_estatus_alterno ea, estatus est");
            appendQuery(" Where e.ELIMINADO = 'False' ");
            appendQuery(" es.estatus = est.id order by ea.id asc");
            List<Object[]> lo = em.createNativeQuery(getStringQuery()).getResultList();
            for (Object[] objects : lo) {
                le.add(castEstatusAlterno(objects));
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public boolean solicitudPasoPorAutorizacion(int idSolicitud) {
        try {
            clearQuery();
            appendQuery("select ea.id, ea.sg_solicitud_viaje, ea.estatus from SG_ESTATUS_APROBACION ea where ea.SG_SOLICITUD_VIAJE = ").append(idSolicitud);
            appendQuery("and ea.ESTATUS in (select eal.ESTATUS from SG_ESTATUS_ALTERNO eal where eal.ELIMINADO = 'False')");

            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            if (lo.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }
    }

    private EstatusAlternoVO castEstatusAlterno(Object[] objs) {
        EstatusAlternoVO estatusAlternoVO = new EstatusAlternoVO();
        estatusAlternoVO.setId((Integer) objs[0]);
        estatusAlternoVO.setIdEstatus((Integer) objs[1]);
        estatusAlternoVO.setNombreEstatus((String) objs[2]);
        return estatusAlternoVO;
    }
}
