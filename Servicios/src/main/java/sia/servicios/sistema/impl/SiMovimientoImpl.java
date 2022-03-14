/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiMovimiento;
import sia.modelo.SiOperacion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.vehiculo.impl.SiOperacionImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SiMovimientoImpl extends AbstractFacade<SiMovimiento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //
    @Inject
    private SiOperacionImpl siOperacionRemote;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiMovimientoImpl() {
        super(SiMovimiento.class);
    }

    
    public SiMovimiento save(String motivo, int idSiOperacion, String idUsuario) {

        SiMovimiento siMovimiento;

        siMovimiento = new SiMovimiento();
        siMovimiento.setMotivo(motivo);
        siMovimiento.setSiOperacion(this.siOperacionRemote.find(idSiOperacion));
        siMovimiento.setGenero(new Usuario(idUsuario));
        siMovimiento.setFechaGenero(new Date());
        siMovimiento.setHoraGenero(new Date());
        siMovimiento.setEliminado(Constantes.NO_ELIMINADO);

        create(siMovimiento);

        return siMovimiento;
    }

    
    public SiMovimiento guardarSiMovimiento(String motivo, SiOperacion siOperacion, Usuario usuarioGenero) {
        SiMovimiento siMovimiento = new SiMovimiento();
        try {
            siMovimiento.setGenero(usuarioGenero);
            siMovimiento.setMotivo(motivo);
            siMovimiento.setFechaGenero(new Date());
            siMovimiento.setHoraGenero(new Date());
            siMovimiento.setEliminado(Constantes.NO_ELIMINADO);
            siMovimiento.setSiOperacion(siOperacion);
            create(siMovimiento);            
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
        return siMovimiento;
    }
}
