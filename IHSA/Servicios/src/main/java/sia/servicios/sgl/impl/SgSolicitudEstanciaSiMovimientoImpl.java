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
import sia.modelo.SgSolEstSiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgSolicitudEstanciaSiMovimientoImpl extends AbstractFacade<SgSolEstSiMovimiento>{
//

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }    
    @Inject
    private SiMovimientoImpl siMovimientoService;
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaRemote;
    
    public SgSolicitudEstanciaSiMovimientoImpl() {
        super(SgSolEstSiMovimiento.class);
    }

    
    public void guardarSiMovimiento(int sgSolicitudEstancia, Integer idSiMovimiento, String idUsuarioGenero) {
        //
        try {
            //
            SgSolEstSiMovimiento relacion = new SgSolEstSiMovimiento();
            relacion.setEliminado(Constantes.BOOLEAN_FALSE);
            relacion.setGenero(new Usuario(idUsuarioGenero));
            relacion.setFechaGenero(new Date());
            relacion.setHoraGenero(new Date());
            relacion.setSgSolicitudEstancia(sgSolicitudEstanciaRemote.find(sgSolicitudEstancia));
            //relacion.setSgSolicitudEstancia(sgSolicitudEstancia);
            relacion.setSiMovimiento(this.siMovimientoService.find(idSiMovimiento));
            create(relacion);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }
}
