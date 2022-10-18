
package sia.servicios.oficio.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OfOficio;
import sia.modelo.OfOficioSiMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author esapien
 */
@Stateless 
public class OfOficioSiMovimientoImpl extends AbstractFacade<OfOficioSiMovimiento> {
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    
    /**
     * Constructor
     */
    public OfOficioSiMovimientoImpl() {
        super(OfOficioSiMovimiento.class);
    }
    
    /**
     * 
     * @param usuarioId
     * @param oficio
     * @param adjunto
     * @param movimiento
     * @return 
     */
    
    public OfOficioSiMovimiento agregarOficioMovimiento(OfOficio oficio, SiMovimiento movimiento, String usuarioId) {
        
        OfOficioSiMovimiento oficioMovimiento = new OfOficioSiMovimiento();
        
        oficioMovimiento.setGenero(new Usuario(usuarioId));
        oficioMovimiento.setFechaGenero(new Date());
        oficioMovimiento.setHoraGenero(new Date());
        oficioMovimiento.setEliminado(Constantes.BOOLEAN_FALSE);
        
        oficioMovimiento.setOfOficio(oficio);
        oficioMovimiento.setSiMovimiento(movimiento);
        
        this.create(oficioMovimiento);
        
        return oficioMovimiento;
    }
    
}
