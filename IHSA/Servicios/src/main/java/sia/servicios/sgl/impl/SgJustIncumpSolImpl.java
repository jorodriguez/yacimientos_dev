/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.*;
import sia.modelo.sgl.viaje.vo.JustIncumSolVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgJustIncumpSolImpl extends AbstractFacade<SgJustIncumpSol> {

    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote; 
    
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgJustIncumpSolImpl() {
        super(SgJustIncumpSol.class);
    }
    
    public boolean guardarJustificacionPorAprobacionSolicitud(int idSolicitudViaje,String justificacion,String idUsuario){
        try{
            SgJustIncumpSol sgJustIncumpSol = new SgJustIncumpSol();
                       
            sgJustIncumpSol.setSgSolicitudViaje(sgSolicitudViajeRemote.find(idSolicitudViaje));
            sgJustIncumpSol.setJustificacion(justificacion);
            sgJustIncumpSol.setGenero(new Usuario(idUsuario));
            sgJustIncumpSol.setFechaGenero(new Date());
            sgJustIncumpSol.setHoraGenero(new Date());
            sgJustIncumpSol.setEliminado(Constantes.BOOLEAN_FALSE);
            em.persist(sgJustIncumpSol);            
            return true;
        }catch(Exception e){
            UtilLog4j.log.error(e);
            return false;
        }
    }
    
    public JustIncumSolVo recuperaJustificacionGerente(int idSolicitud) {
        try {
            clearQuery();
            query.append("select jis.id, jis.justificacion,");
            query.append(" (select u.NOMBRE from USUARIO u where u.ID = jis.GENERO), jis.FECHA_GENERO, jis.HORA_GENERO");
            query.append(" from sg_just_incump_sol jis where jis.sg_solicitud_viaje = ").append(idSolicitud);
            query.append(" and jis.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            if (objects != null) {
                return castJustIncumpSol(objects);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private JustIncumSolVo castJustIncumpSol(Object[] objects) {
        JustIncumSolVo justIncumSol = new JustIncumSolVo();
        justIncumSol.setId((Integer) objects[0]);
        justIncumSol.setMotivoJustifiacion((String) objects[1]);
        justIncumSol.setJustifico((String) objects[2]);
        justIncumSol.setFecha((Date) objects[3]);
        justIncumSol.setHora((Date) objects[4]);
        return justIncumSol;
    }
    
}
