/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgSolViajeIncum;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgSolViajeIncumImpl extends AbstractFacade<SgSolViajeIncum>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgSolViajeIncumImpl() {
        super(SgSolViajeIncum.class);
    }
     
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    //
    
    
    /**
     * MLUIS
     * 26/11/2013
     */
    
    public void guardar(String idUsuario, int idSolicitud, int idTipoEsp) {
        SgSolViajeIncum sgSolViajeIncum = new SgSolViajeIncum();
        sgSolViajeIncum.setSgSolicitudViaje(sgSolicitudViajeRemote.find(idSolicitud));
        sgSolViajeIncum.setSgTipoEspecifico(sgTipoEspecificoRemote.find(idTipoEsp));
        sgSolViajeIncum.setHistorial(Constantes.BOOLEAN_TRUE);
        sgSolViajeIncum.setGenero(new Usuario(idUsuario));
        sgSolViajeIncum.setFechaGenero(new Date());
        sgSolViajeIncum.setHoraGenero(new Date());
        sgSolViajeIncum.setEliminado(Constantes.NO_ELIMINADO);
        create(sgSolViajeIncum);        
    }
    
}
