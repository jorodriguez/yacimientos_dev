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
import sia.excepciones.SIAException;
import sia.modelo.Requisicion;
import sia.modelo.RequisicionSiMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.requisicion.vo.RequisicionEsperaVO;
import sia.modelo.requisicion.vo.RequisicionMovimientoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class RequisicionSiMovimientoImpl extends AbstractFacade<RequisicionSiMovimiento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    @Inject
    private SiOperacionImpl siOperacionRemote;

    public RequisicionSiMovimientoImpl() {
        super(RequisicionSiMovimiento.class);
    }

    
    public void saveRequestMove(String idSesion, String motivo, int idRequisicion, int idSiOperacion) {
        try {
            RequisicionSiMovimiento requisicionSiMovimiento = new RequisicionSiMovimiento();

            //Guardar en movimiento
            SiMovimiento siMovimiento = siMovimientoRemote.guardarSiMovimiento(motivo, siOperacionRemote.find(idSiOperacion), usuarioRemote.find(idSesion));
            //Giardar en relacion con requisicion

            requisicionSiMovimiento.setRequisicion(new Requisicion(idRequisicion));
            requisicionSiMovimiento.setSiMovimiento(siMovimiento);
            requisicionSiMovimiento.setGenero(new Usuario(idSesion));
            requisicionSiMovimiento.setFechaGenero(new Date());
            requisicionSiMovimiento.setHoraGenero(new Date());
            requisicionSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);
            create(requisicionSiMovimiento);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(ex);
        }
    }

    
    public RequisicionEsperaVO requisicionMovDet(int idRequi) {
        List<RequisicionMovimientoVO> movs = this.requisicionMovimientos(idRequi, Constantes.ID_SI_OPERACION_ESPERA);
        RequisicionEsperaVO lr = null;
        if (movs != null && movs.size() > 0) {
            lr = new RequisicionEsperaVO();
            lr.setRegistro(movs.get(movs.size()-1));
            
        }
        
        if(lr != null && lr.getRegistro() != null){
            lr.setMsgs(this.requisicionMovimientos(idRequi, Constantes.ID_SI_OPERACION_ESPERAMSG));
        }
        
        return lr;
    }

    
    public List<RequisicionMovimientoVO> requisicionMovimientos(int idRequi, int operacionID) {
        List<RequisicionMovimientoVO> lr = null;
        String sb = "   select rm.id, m.si_operacion, m.motivo, u.nombre, m.fecha_genero, m.hora_genero "
                + "     from requisicion_si_movimiento rm "
                + "     inner join si_movimiento m on m.id = rm.si_movimiento and m.eliminado = false "
                + "     inner join usuario u on u.id = m.genero "
                + "     where rm.eliminado = false "
                + "     and m.si_operacion = " + operacionID
                + "     and rm.requisicion = " + idRequi
                + "     order by rm.id ";

        List<Object[]> l = em.createNativeQuery(sb).getResultList();
        if (l != null) {
            lr = new ArrayList<>();
            RequisicionMovimientoVO o = null;
            for (Object[] objects : l) {
                o = new RequisicionMovimientoVO();
                o.setId((Integer) objects[0]);
                o.setIdOperacion((Integer) objects[1]);                
                o.setMotivo(String.valueOf(objects[2]));
                o.setUsuario((String) objects[3]);
                o.setFecha((Date) objects[4]);
                o.setHora((Date) objects[5]);
                lr.add(o);
            }
        }
        return lr;
    }
}
