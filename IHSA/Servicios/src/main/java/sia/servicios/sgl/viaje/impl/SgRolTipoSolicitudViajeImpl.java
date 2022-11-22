/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgRolTipoSolicitudViaje;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.RolTipoSolicitudVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgTipoSolicitudViajeImpl;
import sia.servicios.sistema.impl.SiRolImpl;

/**
 *
 *
 * @author mluis
 */
@Stateless 
public class SgRolTipoSolicitudViajeImpl extends AbstractFacade<SgRolTipoSolicitudViaje> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private SgTipoSolicitudViajeImpl sgTipoSolicitudViajeRemote;
    @Inject
    private SiRolImpl siRolRemote;
   
    //
    public SgRolTipoSolicitudViajeImpl() {
        super(SgRolTipoSolicitudViaje.class);
    }
    /*
     * MLUIS 12/11/2013
     *
     */

    
    public List<RolTipoSolicitudVo> traerTipoSolicitudPorRol(int idRol, int tipoViaje) {
        try {
            String s = "";
            if (tipoViaje != -1) {
                s = " and ts.sg_tipo_especifico =  " + tipoViaje;
            }
            clearQuery();

            appendQuery("select rts.id, r.id, r.nombre, ts.id, ts.nombre from sg_rol_tipo_solicitud_viaje rts, si_rol r, sg_tipo_solicitud_viaje ts ");
            appendQuery(" where rts.si_rol = ").append(idRol);
            appendQuery(s);
            appendQuery(" and rts.si_rol =  r.id");
            appendQuery(" and rts.sg_tipo_solicitud_viaje =  ts.id");
            appendQuery(" and rts.eliminado =  '").append(Constantes.NO_ELIMINADO).append("'");
            appendQuery(" order by rts.sg_tipo_solicitud_viaje asc");

            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
            List<RolTipoSolicitudVo> lobj = new ArrayList<RolTipoSolicitudVo>();
            for (Object[] objects : lo) {
                lobj.add(castRolSolicitud(objects));
            }
            return lobj;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    private RolTipoSolicitudVo castRolSolicitud(Object[] objects) {
        RolTipoSolicitudVo rolTipoSolicitudVo = new RolTipoSolicitudVo();
        rolTipoSolicitudVo.setIdRolTipoSolicitud((Integer) objects[0]);
        rolTipoSolicitudVo.setIdRol((Integer) objects[1]);
        rolTipoSolicitudVo.setNombreRol((String) objects[2]);
        rolTipoSolicitudVo.setIdTipoSolicitud((Integer) objects[3]);
        rolTipoSolicitudVo.setTipoSolicitud((String) objects[4]);
        return rolTipoSolicitudVo;
    }

    
    public void guardarRelacion(String idSesion, int idRol, int idTipo) {
        SgRolTipoSolicitudViaje sgRolTipoSolicitudViaje = new SgRolTipoSolicitudViaje();
        sgRolTipoSolicitudViaje.setSgTipoSolicitudViaje(sgTipoSolicitudViajeRemote.find(idTipo));
        sgRolTipoSolicitudViaje.setSiRol(siRolRemote.find(idRol));
        sgRolTipoSolicitudViaje.setGenero(new Usuario(idSesion));
        sgRolTipoSolicitudViaje.setFechaGenero(new Date());
        sgRolTipoSolicitudViaje.setHoraGenero(new Date());
        sgRolTipoSolicitudViaje.setEliminado(Constantes.NO_ELIMINADO);
        create(sgRolTipoSolicitudViaje);
    }

    
    public void quitarRelacion(String idSesion, int idRolTipoSol) {
        SgRolTipoSolicitudViaje sgRolTipoSolicitudViaje = find(idRolTipoSol);
        String ae = sgRolTipoSolicitudViaje.toString();
        sgRolTipoSolicitudViaje.setModifico(new Usuario(idSesion));
        sgRolTipoSolicitudViaje.setFechaModifico(new Date());
        sgRolTipoSolicitudViaje.setHoraModifico(new Date());
        sgRolTipoSolicitudViaje.setEliminado(Constantes.ELIMINADO);
        create(sgRolTipoSolicitudViaje);        
    }
}
