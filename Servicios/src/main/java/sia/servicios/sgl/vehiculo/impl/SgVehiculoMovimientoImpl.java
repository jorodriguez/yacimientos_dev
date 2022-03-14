/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.vehiculo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgVehiculo;
import sia.modelo.SgVehiculoSiMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sgl.vehiculo.vo.SgVehiculoSiMovimientoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author nlopez
 */
@LocalBean 
public class SgVehiculoMovimientoImpl extends AbstractFacade<SgVehiculoSiMovimiento>{

    private StringBuilder bodyQuery = new StringBuilder();
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    SgOficinaImpl sgOficinaRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    @Inject
    private SgVehiculoImpl sgVehiculoRemote;

    public SgVehiculoMovimientoImpl() {
        super(SgVehiculoSiMovimiento.class);
    }

    private void clearCuerpoQuery() {
        this.bodyQuery.delete(0, bodyQuery.length());
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    
    public boolean save(String motivo, int idVehiculo, int idOficinaOrigen, int idOficinaDestino, String idUsuario) {
        try {
            SgVehiculo vehiculo = sgVehiculoRemote.find(idVehiculo);
            if (vehiculo != null) {
                SiMovimiento movimiento = siMovimientoRemote.save(motivo, Constantes.ID_SI_OPERACION_ASIGNAR_VEHICULO, idUsuario);
                if (movimiento != null) {
                    SgVehiculoSiMovimiento sgVehiculoMovimiento = new SgVehiculoSiMovimiento();
                    sgVehiculoMovimiento.setSgVehiculo(vehiculo);
                    sgVehiculoMovimiento.setSiMovimiento(movimiento);
                    sgVehiculoMovimiento.setGenero(new Usuario(idUsuario));
                    sgVehiculoMovimiento.setOficinaOrigen(sgOficinaRemote.find(idOficinaOrigen));
                    sgVehiculoMovimiento.setOficinaDestino(sgOficinaRemote.find(idOficinaDestino));
                    sgVehiculoMovimiento.setFechaGenero(new Date());
                    sgVehiculoMovimiento.setHoraGenero(new Date());
                    sgVehiculoMovimiento.setEliminado(Constantes.NO_ELIMINADO);

                    create(sgVehiculoMovimiento);                    
                }
            }
            return true;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }

    }

    
    public List<SgVehiculoSiMovimientoVO> traerMovimientosOficinaVehiculo(Integer idVehiculo) {
        clearCuerpoQuery();
        List<SgVehiculoSiMovimientoVO> le = null;
        try {
            bodyQuery.append("select vm.id, "); // 0
            bodyQuery.append(" oa.nombre, "); //1
            bodyQuery.append(" oc.nombre, "); //2
            bodyQuery.append(" m.motivo "); //3
            bodyQuery.append(" from sg_vehiculo_si_movimiento vm, ");
            bodyQuery.append(" sg_oficina oa, ");
            bodyQuery.append(" sg_oficina oc, ");
            bodyQuery.append(" si_movimiento m");
            bodyQuery.append(" where vm.sg_vehiculo =").append(idVehiculo);
            bodyQuery.append(" and vm.SG_OFICINA_ACTUAL = oa.id ");
            bodyQuery.append(" and vm.sg_oficina_cambio = oc.id ");
            bodyQuery.append(" and vm.si_movimiento = m.id ");
            bodyQuery.append(" order by vm.id desc ");



            List<Object[]> lo = em.createNativeQuery(bodyQuery.toString()).getResultList();
            if (lo != null) {

                le = new ArrayList<SgVehiculoSiMovimientoVO>();
                for (Object[] objects : lo) {
                    le.add(castReturnVehiculoMotiviVO(objects));
                }
            }
            return le;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    public SgVehiculoSiMovimientoVO castReturnVehiculoMotiviVO(Object[] obj) {
        SgVehiculoSiMovimientoVO vm = new SgVehiculoSiMovimientoVO();

        vm.setId((Integer) obj[0]);
        vm.setOficinaOrigen((obj[1] == null) ? "" : (String) obj[1]);
        vm.setOficinaDestino((obj[2] == null) ? "" : (String) obj[2]);
        vm.setMovimiento((String) obj[3]);

        return vm;
    }

   
}
