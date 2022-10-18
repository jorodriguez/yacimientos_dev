/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.RelProyectoTipoObra;
import sia.modelo.TipoObra;
import sia.modelo.Usuario;
import sia.modelo.vo.RelProyectoTipoObraVO;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.catalogos.impl.TipoObraImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor
 */
@Stateless 
public class RelProyectoTipoObraImpl{

    @Inject
    private TipoObraImpl tipoObraServicioRemoto;
    @Inject
    private ProyectoOtImpl proyectoOtServicioRemoto;
    
    @PersistenceContext
    private EntityManager em;

    
    public void create(RelProyectoTipoObra relProyectoTipoObra) {
        em.persist(relProyectoTipoObra);
    }

    
    public void edit(RelProyectoTipoObra relProyectoTipoObra) {
        em.merge(relProyectoTipoObra);
    }

    
    public void remove(RelProyectoTipoObra relProyectoTipoObra) {
        em.remove(em.merge(relProyectoTipoObra));
    }

    
    public RelProyectoTipoObra find(Object id) {
        return em.find(RelProyectoTipoObra.class, id);
    }

    
    public List<RelProyectoTipoObra> findAll() {
        return em.createQuery("select object(o) from RelProyectoTipoObra as o").getResultList();
    }

    
    public List<TipoObra> getPorProyectoOt(Object nombreProyectoOt) {
        return em.createQuery("SELECT r.tipoObra FROM RelProyectoTipoObra r "
                + " WHERE r.proyectoOt.nombre = :nombreProyecto "
                + " AND r.tipoObra.visible =:visible "
                + " AND r.eliminado = 'False' "
                + " AND r.tipoObra.eliminado = 'False' "
                + " ORDER BY r.tipoObra.nombre ASC")
                .setParameter("nombreProyecto", nombreProyectoOt)
                .setParameter("visible", true).getResultList();
    }

    
    public List<RelProyectoTipoObraVO> traerPorProyectoId(int proyectoOtId) {
        //
        try {
            String q = "SELECT rel.ID, "
                    + " pot.NOMBRE,"
                    + " tob.NOMBRE"
                    + " FROM REL_PROYECTO_TIPO_OBRA rel,TIPO_OBRA tob,PROYECTO_OT pot"
                    + " WHERE rel.PROYECTO_OT = " + proyectoOtId + " AND tob.ID = rel.TIPO_OBRA AND pot.ID = rel.PROYECTO_OT AND rel.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
                    + " ORDER BY rel.ID ASC";

            List<RelProyectoTipoObraVO> le = new ArrayList<RelProyectoTipoObraVO>();
            List<Object[]> lo = em.createNativeQuery(q).getResultList();

            for (Object[] objects : lo) {
                RelProyectoTipoObraVO tVo = new RelProyectoTipoObraVO();
                tVo.setId((Integer) objects[0]);
                tVo.setNombreProyectoOT((String) objects[1]);
                tVo.setNombreTipoObra((String) objects[2]);
                le.add(tVo);
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }

        //return em.createQuery("SELECT f FROM RelProyectoTipoObra f WHERE f.proyectoOt.id = :p ORDER BY f.id ASC").setParameter("p", proyectoOtId).getResultList();
    }

    
    public void guardarRelProyectoTipoObra(Integer idProyectoOt, int tipoOId, String idUsuario) {
        RelProyectoTipoObra relProyectoTipoObra = new RelProyectoTipoObra();
        relProyectoTipoObra.setProyectoOt(this.proyectoOtServicioRemoto.find(idProyectoOt));
        relProyectoTipoObra.setTipoObra(this.tipoObraServicioRemoto.find(tipoOId));
        relProyectoTipoObra.setEliminado(Constantes.BOOLEAN_FALSE);
        relProyectoTipoObra.setGenero(new Usuario(idUsuario));
        relProyectoTipoObra.setFechaGenero(new Date());
        relProyectoTipoObra.setHoraGenero(new Date());
        this.create(relProyectoTipoObra);
    }

    
    public boolean eliminarRelProyectoTipoObra(Integer idRelacion, String idUsuario) {
        try {
            RelProyectoTipoObra rto = find(idRelacion);
            rto.setEliminado(Constantes.BOOLEAN_TRUE);
            rto.setModifico(new Usuario(idUsuario));
            rto.setFechaModifico(new Date());
            rto.setHoraModifico(new Date());
            edit(rto);
            return true;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }
    }

    
    public boolean buscarProyectoOtTipoObreRepetidos(int idProyectoOt, int idTipoObra) {
        try {
            String q = " select *"
                    + " from REL_PROYECTO_TIPO_OBRA p"
                    + " where p.TIPO_OBRA = " + idTipoObra
                    + " and p.PROYECTO_OT =  " + idProyectoOt
                    + " and p.ELIMINADO = 'False'";
            List<Object[]> lo = em.createNativeQuery(q).getResultList();
            return lo.isEmpty();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }

    }
}
