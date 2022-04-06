/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.vehiculo.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgChecklistExtVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgChecklistExtVehiculoImpl extends AbstractFacade<SgChecklistExtVehiculo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    private static final UtilLog4j LOGGER = UtilLog4j.log;
        
    @Inject
    SiAdjuntoImpl siAdjuntoRemote;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgChecklistExtVehiculoImpl() {
        super(SgChecklistExtVehiculo.class);
    }

    
    public SgChecklistExtVehiculo buscarPorChecklist(SgChecklist sgChecklist) {
        UtilLog4j.log.info(this, "CHECKLIST.ID: " + (sgChecklist != null ? sgChecklist.getId() : null));
        
        SgChecklistExtVehiculo retVal = null;
        
        try {
            retVal = (SgChecklistExtVehiculo) em.createQuery("SELECT ce FROM SgChecklistExtVehiculo ce "
                    + " WHERE ce.sgChecklist.id = :idCheck "
                    + " AND ce.eliminado = :eli")
                    .setParameter("idCheck", sgChecklist.getId())
                    .setParameter("eli", Constantes.NO_ELIMINADO)
                    .getSingleResult();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        
        return retVal;
    }

    
    public SgChecklistExtVehiculo create(SgChecklistExtVehiculo checklistExtVehiculo, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgChecklistExtVehiculoImpl.create()");
        
        checklistExtVehiculo.setGenero(new Usuario(idUsuario));
        checklistExtVehiculo.setFechaGenero(new Date());
        checklistExtVehiculo.setHoraGenero(new Date());
        checklistExtVehiculo.setEliminado(Constantes.NO_ELIMINADO);
    
        super.create(checklistExtVehiculo);
        
        UtilLog4j.log.info(this, "SgChecklistExtVehiculo CREATED SUCCESSFULLY");
        
        return checklistExtVehiculo;
    }

    
    public SgChecklistExtVehiculo update(SgChecklistExtVehiculo checklistExtVehiculo, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgChecklistExtVehiculoImpl.update()");        
        checklistExtVehiculo.setModifico(new Usuario(idUsuario));
        checklistExtVehiculo.setFechaModifico(new Date());
        checklistExtVehiculo.setHoraModifico(new Date());        
        super.edit(checklistExtVehiculo);
        
        UtilLog4j.log.info(this, "SgChecklistExtVehiculo UPDATED SUCCESSFULLY");
        
        return checklistExtVehiculo;
    }

    
    public void delete(SgChecklistExtVehiculo checklistExtVehiculo, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgChecklistExtVehiculoImpl.delete()");
        checklistExtVehiculo.setEliminado(Constantes.ELIMINADO);
        super.edit(checklistExtVehiculo);
        UtilLog4j.log.info(this, "SgChecklistExtVehiculo DELETED SUCCESSFULLY");
        //Eliminar Adjunto
        if (checklistExtVehiculo.getSiAdjunto() != null) {
            int idsIAdjunto = checklistExtVehiculo.getSiAdjunto().getId().intValue();
            siAdjuntoRemote.delete(idsIAdjunto, idUsuario);
        }
    }
}