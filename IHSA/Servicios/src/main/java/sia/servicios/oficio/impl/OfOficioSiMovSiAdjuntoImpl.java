/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.oficio.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OfOficioSiMovSiAdjunto;
import sia.modelo.OfOficioSiMovimiento;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author esapien
 */
@Stateless 
public class OfOficioSiMovSiAdjuntoImpl extends AbstractFacade<OfOficioSiMovSiAdjunto> {
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    
    /**
     * Constructor
     */
    public OfOficioSiMovSiAdjuntoImpl() {
        super(OfOficioSiMovSiAdjunto.class);
    }
    
    /**
     * Agrega una nueva relación en entre un movimiento de oficio y un archivo
     * adjunto (tabla OF_OFICIO_SI_MOV_SI_ADJUNTO).
     * 
     * @param oficioMovimiento
     * @param adjunto
     * @param usuarioId
     * @return 
     */
    
    public OfOficioSiMovSiAdjunto agregarOficioMovAdjunto(OfOficioSiMovimiento oficioMovimiento, SiAdjunto adjunto, String usuarioId) {
        
        OfOficioSiMovSiAdjunto oficioMovAdjunto = new OfOficioSiMovSiAdjunto();
        
        oficioMovAdjunto.setOfOficioSiMovimiento(oficioMovimiento);
        oficioMovAdjunto.setSiAdjunto(adjunto);
        
        oficioMovAdjunto.setGenero(new Usuario(usuarioId));
        oficioMovAdjunto.setFechaGenero(new Date());
        oficioMovAdjunto.setHoraGenero(new Date());
        oficioMovAdjunto.setEliminado(Constantes.BOOLEAN_FALSE);
        
        this.create(oficioMovAdjunto);
        
        return oficioMovAdjunto;
    }
    
}
