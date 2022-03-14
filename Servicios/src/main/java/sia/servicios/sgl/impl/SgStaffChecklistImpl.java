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
import sia.modelo.SgStaff;
import sia.modelo.SgStaffChecklist;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgStaffChecklistImpl extends AbstractFacade<SgStaffChecklist> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgStaffChecklistImpl() {
	super(SgStaffChecklist.class);
    }

    
    public SgStaffChecklist createStaffChecklist(SgStaff staff, SgChecklist checklist, String idUsuario) {
	UtilLog4j.log.info(this, "SgStaffChecklistImpl.createStaffChecklist()");

	SgStaffChecklist staffChecklist = new SgStaffChecklist();
	staffChecklist.setSgStaff(staff);
	staffChecklist.setSgChecklist(checklist);
	staffChecklist.setFechaGenero(new Date());
	staffChecklist.setHoraGenero(new Date());
	staffChecklist.setGenero(new Usuario(idUsuario));
	staffChecklist.setEliminado(Constantes.NO_ELIMINADO);

	try {
	    super.create(staffChecklist);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Hubo un error al crear la relación entre el Staff: " + staff.getId() + " y el Checklist: " + checklist.getId());
	    UtilLog4j.log.info(this, e.getMessage());
	    return null;
	}
	return staffChecklist;
    }

    
    public List<SgStaffChecklist> getAllStaffChecklistByFechaInicioPeriodoAndFechaFinPeriodoAndStatusList(Date fechaInicioPeriodo, Date fechaFinPeriodo, boolean status) {
	UtilLog4j.log.info(this, "SgStaffChecklistImpl.getAllStaffChecklistByFechaInicioPeriodoAndFechaFinPeriodoAndStatusList()");

	List<SgStaffChecklist> checklistsStaff = null;

	if (fechaInicioPeriodo != null && fechaFinPeriodo != null) { //Consulta entre rango de fechas
	    try {
		checklistsStaff = em.createQuery("SELECT chk FROM SgStaffChecklist chk WHERE chk.eliminado = :estado AND chk.sgChecklist.fechaInicioSemana between :fechaInicio AND :fechaFin ")
			.setParameter("estado", status)
			.setParameter("fechaInicio", fechaInicioPeriodo)
			.setParameter("fechaFin", fechaFinPeriodo)
			.getResultList();
	    } catch (Exception e) {
		UtilLog4j.log.info(this, e.getMessage());
		UtilLog4j.log.info(this, "Hubo un error al obtener las Características del Staff");
		return null;
	    }
	} else if (fechaInicioPeriodo != null && fechaFinPeriodo == null) { //Consulta de desde una fecha específica hasta hoy
	    try {
		fechaFinPeriodo = new Date();
		checklistsStaff = em.createQuery("SELECT chk FROM SgStaffChecklist chk WHERE chk.eliminado = :estado AND chk.sgChecklist.fechaInicioSemana between :fechaInicio AND :fechaFin ")
			.setParameter("estado", status)
			.setParameter("fechaInicio", fechaInicioPeriodo)
			.setParameter("fechaFin", fechaFinPeriodo)
			.getResultList();
	    } catch (Exception e) {
		UtilLog4j.log.info(this, e.getMessage());
		UtilLog4j.log.info(this, "Hubo un error al obtener las Características del Staff");
		return null;
	    }
	} else if (fechaInicioPeriodo == null && fechaFinPeriodo != null) { //Consulta hasta una fecha específica
	    try {
		checklistsStaff = em.createQuery("SELECT chk FROM SgStaffChecklist chk WHERE chk.eliminado = :estado AND chk.sgChecklist.fechaInicioSemana < :fechaFin ")
			.setParameter("estado", status)
			.setParameter("fechaInicio", fechaInicioPeriodo)
			.setParameter("fechaFin", fechaFinPeriodo)
			.getResultList();
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		UtilLog4j.log.fatal(this, "Hubo un error al obtener las Características del Staff");
		return null;
	    }
	} else { //Consulta sin filtro de fechas
	    try {
		checklistsStaff = em.createQuery("SELECT chk FROM SgStaffChecklist chk WHERE chk.eliminado = :estado")
			.setParameter("estado", status)
			.getResultList();
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		UtilLog4j.log.fatal(this, "Hubo un error al obtener las Características del Staff");
		return null;
	    }
	}

	if (checklistsStaff != null) {
	    UtilLog4j.log.info(this, "Se encontraron " + checklistsStaff.size() + " checklist de Staff");
	    return checklistsStaff;
	} else {
	    return checklistsStaff;
	}
    }

    
    public List<SgStaffChecklist> getAllStaffChecklistByStaffAndStatusList(int staff, boolean status) {
        UtilLog4j.log.info(this, "SgStaffChecklistImpl.getAllStaffChecklistByStaffAndStatusList()");
        List<SgStaffChecklist> checklistsStaff = null;
        try {
            if (staff > 0) {
                checklistsStaff = em.createQuery("SELECT chk FROM SgStaffChecklist chk WHERE chk.eliminado = :estado AND chk.sgStaff.id = :idStaff ORDER BY chk.id DESC ")
                        .setParameter("estado", status)
                        .setParameter("idStaff", staff)
                        .getResultList();                
            } else {
                UtilLog4j.log.info(this, "Faltan parámetros para poder realizar la búsqueda de Checklist por Staff");              
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Hubo un error al obtener las relaciones de checklist con el Staff: " + staff);
            UtilLog4j.log.fatal(this, e.getMessage());
            checklistsStaff = null;
        }
        return checklistsStaff;
    }

    
    public SgStaffChecklist getThisWeekStaffChecklist(SgStaff staff, Calendar inicioSemana, Calendar finSemana) {
	UtilLog4j.log.info(this, "SgStaffChecklistImpl.getThisWeekStaffChecklist()");

	UtilLog4j.log.info(this, "Inicio semana (Date): " + inicioSemana.getTime());
	UtilLog4j.log.info(this, "Fin semana (Date): " + finSemana.getTime());

	SgStaffChecklist staffChecklist = null;

	if (inicioSemana != null && finSemana != null) {
	    try {
		staffChecklist = (SgStaffChecklist) em.createQuery("SELECT chk FROM SgStaffChecklist chk WHERE chk.eliminado = :eliminado AND chk.sgStaff.id = :idStaff AND chk.sgChecklist.fechaGenero between :inicioSemana AND :finSemana")
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.setParameter("idStaff", staff.getId())
			.setParameter("inicioSemana", inicioSemana.getTime())
			.setParameter("finSemana", finSemana.getTime())
			.getSingleResult();
	    } catch (NonUniqueResultException nure) {
		UtilLog4j.log.info(this, nure.getMessage());
//                nure.printStackTrace();
		return staffChecklist;
	    } catch (NoResultException nre) {
		UtilLog4j.log.info(this, nre.getMessage());
//                nre.printStackTrace();
		return null;
	    } catch (Exception e) {
		UtilLog4j.log.info(this, "Hubo un error al obtener el Checklist de esta semana del Staff: " + staff.getId());
		UtilLog4j.log.info(this, e.getMessage());
//                e.printStackTrace();
		return staffChecklist;
	    }
	    return staffChecklist;
	} else {
	    UtilLog4j.log.info(this, "No es posible obtener el Checklist de esta semana del Staff: " + staff.getId() + "porque faltan parámetros");
	    return staffChecklist;
	}
    }

    
    public boolean deleteStaffChecklist(SgStaffChecklist staffChecklist, String idUsuario) {
	UtilLog4j.log.info(this, "SgStaffChecklistImpl.deleteStaffChecklist()");
	boolean deleteSuccessfull = true;

	staffChecklist.setGenero(new Usuario(idUsuario));
	staffChecklist.setFechaGenero(new Date());
	staffChecklist.setHoraGenero(new Date());
	staffChecklist.setEliminado(Constantes.ELIMINADO);

	try {
	    super.edit(staffChecklist);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrió un err al eliminar el staffChecklist: " + staffChecklist.getId());
	    UtilLog4j.log.info(this, e.getMessage());
	    return !deleteSuccessfull;
	}
	return deleteSuccessfull;
    }

}
