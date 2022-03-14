/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiFactura;
import sia.modelo.SiFacturaMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.sistema.vo.FacturaMovimientoVo;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SiFacturaMovimientoImpl extends AbstractFacade<SiFacturaMovimiento>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Inject
    SiMovimientoImpl siMovimientoRemote;

    public SiFacturaMovimientoImpl() {
        super(SiFacturaMovimiento.class);
    }

    
    public void guardar(int idFactura, String motivo, String sesion) {
        SiMovimiento movimiento = siMovimientoRemote.save(motivo, Constantes.ID_SI_OPERACION_DEVOLVER, sesion);
        SiFacturaMovimiento siFacturaMovimiento = new SiFacturaMovimiento();
        siFacturaMovimiento.setSiFactura(new SiFactura(idFactura));
        siFacturaMovimiento.setSiMovimiento(movimiento);
        siFacturaMovimiento.setGenero(new Usuario(sesion));
        siFacturaMovimiento.setFechaGenero(new Date());
        siFacturaMovimiento.setHoraGenero(new Date());
        siFacturaMovimiento.setEliminado(Constantes.NO_ELIMINADO);
        //
        create(siFacturaMovimiento);

    }

    
    public List<FacturaMovimientoVo> movimientos(int idFactura) {
        String c = "select fm.id, f.id, m.id, u.nombre, m.motivo, fm.fecha_genero from si_factura_movimiento fm"
                + "     inner join usuario u on fm.genero = u.id "
                + "     inner join si_factura f on fm.si_factura = f.id "
                + "     inner join si_movimiento m on fm.si_movimiento = m.id"
                + " where f.id = ?"
                + " and fm.eliminado = false";
        List<Object[]> lista = em.createNativeQuery(c).setParameter(1, idFactura).getResultList();
        List<FacturaMovimientoVo> movimientos = new ArrayList<>();
        for (Object[] objects : lista) {
            FacturaMovimientoVo fmv = new FacturaMovimientoVo();
            fmv.setId((Integer) objects[0]);
            fmv.setIdFactura((Integer) objects[1]);
            fmv.setMovimientoVO(new MovimientoVO());
            fmv.getMovimientoVO().setId((Integer) objects[2]);
            fmv.setGenero((String) objects[3]);
            fmv.getMovimientoVO().setMotivo((String) objects[4]);
            fmv.setFecha((Date) objects[5]);
            movimientos.add(fmv);
        }
        return movimientos;

    }
}
