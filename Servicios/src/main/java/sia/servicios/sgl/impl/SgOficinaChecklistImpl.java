/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgChecklist;
import sia.modelo.SgOficina;
import sia.modelo.SgOficinaChecklist;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgOficinaChecklistImpl extends AbstractFacade<SgOficinaChecklist>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgOficinaChecklistImpl() {
	super(SgOficinaChecklist.class);
    }

    
    public List<SgOficinaChecklist> getAllOficinaChecklistByOficinaAndStatusList(int oficina, boolean status) {
        UtilLog4j.log.info(this, "SgOficinaChecklistImpl.getAllOficinaChecklistByOficinaAndStatusList()");
        List<SgOficinaChecklist> checklistsOficina = null;
        try {
            if (oficina > 0) {
                checklistsOficina = em.createQuery("SELECT chk FROM SgOficinaChecklist chk WHERE chk.eliminado = :estado AND chk.sgOficina.id = :idOficina ORDER BY chk.id DESC ")
                        .setParameter("estado", status)
                        .setParameter("idOficina", oficina)
                        .getResultList();
            } else {
                UtilLog4j.log.info(this, "Faltan parámetros para poder realizar la búsqueda de Checklist por Oficina");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            checklistsOficina = null;
        }
        return checklistsOficina;
    }

    
    public SgOficinaChecklist getThisWeekOficinaChecklist(int oficina, Calendar inicioSemana, Calendar finSemana) {
	UtilLog4j.log.info(this, "SgOficinaChecklistImpl.getThisWeekOficinaChecklist()");

	SgOficinaChecklist oficinaChecklist = null;

	if (inicioSemana != null && finSemana != null) {
	    try {
		oficinaChecklist = (SgOficinaChecklist) em.createQuery("SELECT chk FROM SgOficinaChecklist chk WHERE chk.eliminado = :eliminado AND chk.sgOficina.id = :idOficina AND chk.sgChecklist.fechaGenero between :inicioSemana AND :finSemana")
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.setParameter("idOficina", oficina)
			.setParameter("inicioSemana", inicioSemana.getTime())
			.setParameter("finSemana", finSemana.getTime())
			.getSingleResult();
	    } catch (NonUniqueResultException nure) {
		UtilLog4j.log.fatal(this, nure.getMessage());
//                nure.printStackTrace();
		return oficinaChecklist;
	    } catch (NoResultException nre) {
		UtilLog4j.log.fatal(this, nre.getMessage());
//                nre.printStackTrace();
		return null;
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Hubo un error al obtener el Checklist de esta semana de la Oficina: " + oficina);
		UtilLog4j.log.fatal(this, e.getMessage());
//                UtilLog4j.log.fatal(this,e.getStackTrace());
		return oficinaChecklist;
	    }
	    return oficinaChecklist;
	} else {
	    UtilLog4j.log.info(this, "No es posible obtener el Checklist de esta semana de la Oficina: " + oficina + "porque faltan parámetros");
	    return oficinaChecklist;
	}
    }

    
    public SgOficinaChecklist createOficinaChecklist(int oficina, SgChecklist checklist, String idUsuario) {
	UtilLog4j.log.info(this, "SgOficinaChecklistImpl.createOficinaChecklist()");

	if (oficina > 0 && checklist != null) {
	    SgOficinaChecklist oficinaChecklist = new SgOficinaChecklist();
	    oficinaChecklist.setSgOficina(new SgOficina(oficina));
	    oficinaChecklist.setSgChecklist(checklist);
	    oficinaChecklist.setFechaGenero(new Date());
	    oficinaChecklist.setHoraGenero(new Date());
	    oficinaChecklist.setGenero(new Usuario(idUsuario));
	    oficinaChecklist.setEliminado(Constantes.NO_ELIMINADO);

	    try {
		super.create(oficinaChecklist);
		return oficinaChecklist;
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Hubo un error al crear la relación entre la Oficina: " + oficina + " y el Checklist: " + checklist.getId());
		UtilLog4j.log.fatal(this, e.getMessage());
		return null;
	    }
	} else {
	    UtilLog4j.log.info(this, "Faltan parámetros para poder crear la relación entre la Oficina: " + oficina + " y el Checklist: " + checklist.getId());
	    return null;
	}

    }

}
