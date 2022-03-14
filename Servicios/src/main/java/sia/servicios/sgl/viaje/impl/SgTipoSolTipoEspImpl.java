/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SgTipoSolTipoEsp;
import sia.modelo.sgl.viaje.vo.TipoSolicitudTipoEspecificoVO;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgTipoSolTipoEspImpl extends AbstractFacade<SgTipoSolTipoEsp> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgTipoSolTipoEspImpl() {
        super(SgTipoSolTipoEsp.class);
    }

    
    public List<TipoSolicitudTipoEspecificoVO> buscarDiasSalida(int idSolicitud) {
        clearQuery();
        appendQuery("select se.id, ts.id, te.id, te.nombre, te.descripcion ");
        appendQuery(" from Sg_Tipo_Sol_Tipo_Esp  se, sg_tipo_solicitud_viaje ts, sg_tipo_especifico te ");
        appendQuery(" where se.sg_tipo_solicitud_viaje = ").append(idSolicitud);
        appendQuery(" and se.sg_tipo_solicitud_viaje =  ts.id and se.sg_tipo_especifico = te.id");

        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<TipoSolicitudTipoEspecificoVO> lt = new ArrayList<TipoSolicitudTipoEspecificoVO>();
        for (Object[] objects : lo) {
            lt.add(castTipoSolicitudTipoEspecifico(objects));
        }
        return lt;
    }

    private TipoSolicitudTipoEspecificoVO castTipoSolicitudTipoEspecifico(Object[] objects) {
        TipoSolicitudTipoEspecificoVO tstevo = new TipoSolicitudTipoEspecificoVO();
        tstevo.setIdTipoSolicitudTipoEspecifico((Integer) objects[0]);
        tstevo.setIdTipoSolicitud((Integer) objects[1]);
        tstevo.setIdTipoEspecifico((Integer) objects[2]);
        tstevo.setTipoEspecifico((String) objects[3]);
        tstevo.setDescripcion((String) objects[4]);
        return tstevo;
    }
}
