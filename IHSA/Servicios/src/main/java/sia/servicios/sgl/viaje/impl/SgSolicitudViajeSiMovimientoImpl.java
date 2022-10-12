/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgSolViajeSiMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sgl.viaje.vo.SolicitudViajeMovimientoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgSolicitudViajeSiMovimientoImpl extends AbstractFacade<SgSolViajeSiMovimiento>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgSolicitudViajeSiMovimientoImpl() {
        super(SgSolViajeSiMovimiento.class);
    }
    
    @Inject
    private SiMovimientoImpl siMovimientoService;    
    @Inject
    private SgSolicitudViajeImpl solicitudService;

    
    public void guardarSiMovimiento(Integer idSolicitudViaje, Integer idMovimiento, String idUsuarioGenero) throws Exception {
        try {
            SgSolViajeSiMovimiento relacion = new SgSolViajeSiMovimiento();
            relacion.setEliminado(Constantes.BOOLEAN_FALSE);
            relacion.setGenero(new Usuario(idUsuarioGenero));
            relacion.setFechaGenero(new Date());
            relacion.setHoraGenero(new Date());
            relacion.setSgSolicitudViaje(solicitudService.find(idSolicitudViaje));
            relacion.setSiMovimiento(siMovimientoService.find(idMovimiento));
            super.create(relacion);            
        } catch (Exception e) {
            UtilLog4j.log.error(e);

        }
    }

     //usado en el panel de busqueda..Modificado (Tenia las primeras letras del atributo en mayus...)
    public SiMovimiento findMotivoCancelacion(Integer idSolicitudViaje) {
        try {
            return (SiMovimiento) em.createQuery("SELECT m.siMovimiento FROM SgSolViajeSiMovimiento m WHERE m.sgSolicitudViaje.id = :idSolicitud"
                    + "  AND m.eliminado = :eli ").setParameter("idSolicitud", idSolicitudViaje).setParameter("eli", Constantes.BOOLEAN_FALSE).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public SolicitudViajeMovimientoVo buscarMotivoCancelacion(int idSolicitud, int idOperacion) {
        try {
            clearQuery();
            query.append("select u.nombre, (select op.NOMBRE from SI_OPERACION op where op.ID =  ").append(idOperacion).append("),");
            query.append(" m.MOTIVO, svm.FECHA_GENERO, svm.HORA_GENERO  from SG_SOL_VIAJE_SI_MOVIMIENTO svm,  USUARIO u , SI_MOVIMIENTO m");
            query.append(" where svm.SG_SOLICITUD_VIAJE = ").append(idSolicitud);
            query.append(" and svm.SI_MOVIMIENTO = m.id and svm.GENERO = u.ID");
            query.append(" and svm.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            Object[] obj = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            if (obj != null) {
                return castSolicitudViajeMovimiento(obj);
            } else {
                return null;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    private SolicitudViajeMovimientoVo castSolicitudViajeMovimiento(Object[] obj) {
        SolicitudViajeMovimientoVo svmv = new SolicitudViajeMovimientoVo();
        svmv.setCancelo((String) obj[0]);
        svmv.setOperacion((String) obj[1]);
        svmv.setMotivo((String) obj[2]);
        svmv.setFecha((Date) obj[3]);
        svmv.setHora((Date) obj[4]);
        return svmv;
    }
}
