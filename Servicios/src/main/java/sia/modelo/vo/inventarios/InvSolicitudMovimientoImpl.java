/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo.inventarios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.InvSolicitudMaterial;
import sia.modelo.InvSolicitudMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class InvSolicitudMovimientoImpl extends AbstractFacade<InvSolicitudMovimiento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InvSolicitudMovimientoImpl() {
        super(InvSolicitudMovimiento.class);
    }
    @Inject
    SiMovimientoImpl  siMovimientoRemote;

    public InvSolicitudMovimiento guardar(String sesion, int idSolicitud, String motivo, int operacion) {
        InvSolicitudMovimiento solMov = null;
        try {
            solMov = new InvSolicitudMovimiento();
            //Guardar en movimiento
            SiMovimiento siMovimiento = siMovimientoRemote.save(motivo, operacion, sesion);
            //Giardar en relacion con requisicion
            solMov.setInvSolicitudMaterial(new InvSolicitudMaterial(idSolicitud));
            solMov.setSiMovimiento(siMovimiento);
            solMov.setGenero(new Usuario(sesion));
            solMov.setFechaGenero(new Date());
            solMov.setHoraGenero(new Date());
            solMov.setEliminado(Constantes.NO_ELIMINADO);
            create(solMov);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Excepcion al guardar motivo" + ex.getMessage());
        }
        return solMov;
    }

    public List<MovimientoVO> traerPorSolicitud(int idSolicitud) {
        String c = "select ism.id, m.id, m.motivo, u.nombre,ism.fecha_genero, ism.hora_genero from inv_solicitud_movimiento ism \n"
                + "	inner join si_movimiento m on ism.si_movimiento  = m.id \n"
                + "	inner join usuario  u on ism.genero  = u.id \n"
                + "where ism.inv_solicitud_material  = " + idSolicitud
                + "and ism.eliminado  = false \n"
                + "order by ism.id desc";
        List<Object[]> lo = em.createNativeQuery(c).getResultList();
        List<MovimientoVO> lista = new ArrayList<>();
        for (Object[] objects : lo) {
            MovimientoVO m = new MovimientoVO();
            m.setId((Integer) objects[0]);
            m.setIdRelacion((Integer) objects[1]);
            m.setMotivo((String) objects[2]);
            m.setGenero((String) objects[3]);
            m.setFechaGenero((Date) objects[4]);
            m.setHoraGenero((Date) objects[5]);
            //
            lista.add(m);
        }
        return lista;
    }

}
