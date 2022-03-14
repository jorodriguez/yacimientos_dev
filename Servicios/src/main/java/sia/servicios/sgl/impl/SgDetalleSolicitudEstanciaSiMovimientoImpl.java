/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.*;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgDetalleSolicitudEstanciaSiMovimientoImpl extends AbstractFacade<SgDetSolEstSiMovi> {

    @Inject
    private SiMovimientoImpl siMovimientoService;
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgDetalleSolicitudEstanciaSiMovimientoImpl() {
        super(SgDetSolEstSiMovi.class);
    }

    
    public void guardarDetalleSolicitudEstanciaSiMovimiento(SgDetalleSolicitudEstancia sgDetalleSolicitudEstancia, Integer idSiMovimiento, Usuario usuarioGenero) throws Exception {
        try {
            SgDetSolEstSiMovi relacion = new SgDetSolEstSiMovi();
            relacion.setSgDetalleSolicitudEstancia(sgDetalleSolicitudEstancia);
            relacion.setSiMovimiento(siMovimientoService.find(idSiMovimiento));
            relacion.setGenero(usuarioGenero);
            
            relacion.setEliminado(Constantes.BOOLEAN_FALSE);                      
            relacion.setFechaGenero(new Date());
            relacion.setHoraGenero(new Date());
            create(relacion);            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }
}
