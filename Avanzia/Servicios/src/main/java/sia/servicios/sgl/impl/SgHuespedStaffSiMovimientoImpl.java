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
import sia.modelo.SgHpStaffSiMovimiento;
import sia.modelo.SgHuespedStaff;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgHuespedStaffSiMovimientoImpl extends AbstractFacade<SgHpStaffSiMovimiento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgHuespedStaffImpl sgHuespedStaffRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;    
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgHuespedStaffSiMovimientoImpl() {
        super(SgHpStaffSiMovimiento.class);
    }

    
    public void save(int idSgHuespedStaff, int idSiMovimiento, String idUsuario) {

        SgHpStaffSiMovimiento sgHpStaffSiMovimiento;

        sgHpStaffSiMovimiento = new SgHpStaffSiMovimiento();
        sgHpStaffSiMovimiento.setSiMovimiento(this.siMovimientoRemote.find(idSiMovimiento));
        sgHpStaffSiMovimiento.setSgHuespedStaff(this.sgHuespedStaffRemote.find(idSgHuespedStaff));
        sgHpStaffSiMovimiento.setGenero(new Usuario(idUsuario));
        sgHpStaffSiMovimiento.setFechaGenero(new Date());
        sgHpStaffSiMovimiento.setHoraGenero(new Date());
        sgHpStaffSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);

        create(sgHpStaffSiMovimiento);
    }

    
    public void guardarHuespedStaffSiMovimiento(SgHuespedStaff sgHuespedStaff, SiMovimiento siMovimiento, Usuario usuarioGenero) throws Exception {
        SgHpStaffSiMovimiento relacion = new SgHpStaffSiMovimiento();
        relacion.setEliminado(Constantes.BOOLEAN_FALSE);
        relacion.setGenero(usuarioGenero);
        relacion.setFechaGenero(new Date());
        relacion.setHoraGenero(new Date());
        relacion.setSgHuespedStaff(sgHuespedStaff);
        relacion.setSiMovimiento(siMovimiento);
        super.create(relacion);
    }
}
