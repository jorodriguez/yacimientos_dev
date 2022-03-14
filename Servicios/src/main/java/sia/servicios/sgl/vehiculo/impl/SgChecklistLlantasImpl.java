/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.vehiculo.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgChecklistLlantas;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgChecklistLlantasImpl extends AbstractFacade<SgChecklistLlantas>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
  
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgChecklistLlantasImpl() {
        super(SgChecklistLlantas.class);
    }

    
    public SgChecklistLlantas create(SgChecklistLlantas checklistLlantas, String idUsuario) throws SIAException, Exception {
        checklistLlantas.setGenero(new Usuario(idUsuario));
        checklistLlantas.setFechaGenero(new Date());
        checklistLlantas.setHoraGenero(new Date());
        checklistLlantas.setEliminado(Constantes.NO_ELIMINADO);

        super.create(checklistLlantas);

        return checklistLlantas;
    }

    
    public SgChecklistLlantas update(SgChecklistLlantas checklistLlantas, String idUsuario) throws SIAException, Exception {
        checklistLlantas.setModifico(new Usuario(idUsuario));
        checklistLlantas.setFechaModifico(new Date());
        checklistLlantas.setHoraModifico(new Date());

        super.edit(checklistLlantas);

        return checklistLlantas;
    }

    
    public SgChecklistLlantas buscarPorChecklist(SgChecklist sgChecklist) {
        try {
            return (SgChecklistLlantas) em.createQuery("SELECT cl FROM SgChecklistLlantas cl WHERE cl.sgChecklist.id = :idCheck AND cl.eliminado = :eli").setParameter("idCheck", sgChecklist.getId()).setParameter("eli", Constantes.NO_ELIMINADO).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return null;
        }
    }
}
