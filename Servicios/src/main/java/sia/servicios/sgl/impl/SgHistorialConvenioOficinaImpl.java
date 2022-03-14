/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.modelo.SgHistorialConvenioOficina;
import sia.modelo.SgOficina;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgHistorialConvenioOficinaImpl extends AbstractFacade<SgHistorialConvenioOficina>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgHistorialConvenioOficinaImpl() {
	super(SgHistorialConvenioOficina.class);
    }

    
    public SgHistorialConvenioOficina traerContratoVigente(int sgOficina) {
	try {
	    return (SgHistorialConvenioOficina) em.createQuery("SELECT h FROM SgHistorialConvenioOficina h "
		    + " WHERE h.sgOficina.id = :id "
		    + " AND h.vigente = :v "
		    + " AND h.eliminado = :eli").setParameter("id", sgOficina).setParameter("v", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.BOOLEAN_FALSE).getSingleResult();
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public SgHistorialConvenioOficina asignarContratoOficina(int sgOficina, Convenio convenio, Usuario usuario) {
	try {
	    return crearHistorial(sgOficina, convenio, usuario);
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    private SgHistorialConvenioOficina crearHistorial(int sgOficina, Convenio convenio, Usuario usuario) {
	try {
	    SgHistorialConvenioOficina sgHistorialConvenioOficina = new SgHistorialConvenioOficina();
	    sgHistorialConvenioOficina.setConvenio(convenio);
	    sgHistorialConvenioOficina.setSgOficina(new SgOficina(sgOficina));
	    sgHistorialConvenioOficina.setGenero(usuario);
	    sgHistorialConvenioOficina.setFechaGenero(new Date());
	    sgHistorialConvenioOficina.setHoraGenero(new Date());
	    sgHistorialConvenioOficina.setEliminado(Constantes.BOOLEAN_FALSE);
	    sgHistorialConvenioOficina.setVigente(Constantes.BOOLEAN_TRUE);
	    create(sgHistorialConvenioOficina);
	    return sgHistorialConvenioOficina;
	} catch (Exception e) {
	    return null;
	}
    }

    
    public List<SgHistorialConvenioOficina> buscarRelacionConvenio(int sgOficina, Convenio convenio) {
	try {
	    return em.createQuery("SELECT h FROM SgHistorialConvenioOficina h WHERE h.sgOficina.id = :id "
		    + " AND h.convenio.id = :con"
		    + " AND h.eliminado = :eli"
		    + " AND h.vigente = :v").setParameter("con", convenio).setParameter("id", sgOficina).setParameter("eli", Constantes.BOOLEAN_FALSE).setParameter("v", Constantes.BOOLEAN_TRUE).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public void quitarContratoVigente(SgHistorialConvenioOficina sgHistorialConvenioOficina, Usuario usuario, int sgOficina) {
	try {
	    sgHistorialConvenioOficina.setSgOficina(new SgOficina(sgOficina));
	    sgHistorialConvenioOficina.setGenero(usuario);
	    sgHistorialConvenioOficina.setFechaGenero(new Date());
	    sgHistorialConvenioOficina.setHoraGenero(new Date());
	    sgHistorialConvenioOficina.setVigente(Constantes.BOOLEAN_FALSE);
	    edit(sgHistorialConvenioOficina);
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	}
    }

    
    public void eliminarConvenioVigente(SgHistorialConvenioOficina sgHistorialConvenioOficina, Usuario usuario) {
	sgHistorialConvenioOficina.setGenero(usuario);
	sgHistorialConvenioOficina.setFechaGenero(new Date());
	sgHistorialConvenioOficina.setHoraGenero(new Date());
	sgHistorialConvenioOficina.setEliminado(Constantes.ELIMINADO);
	sgHistorialConvenioOficina.setVigente(Constantes.BOOLEAN_FALSE);
	edit(sgHistorialConvenioOficina);
    }

    
    public List<SgHistorialConvenioOficina> traerHistorialConvenio(int sgOficina) {
	try {
	    return em.createQuery("SELECT h FROM SgHistorialConvenioOficina h WHERE h.sgOficina.id = :id "
		    + " AND h.eliminado = :eli ORDER BY h.id DESC").setParameter("id", sgOficina).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public List<SgHistorialConvenioOficina> getContratoByVigenteList() {
	try {
	    return em.createQuery("SELECT h FROM SgHistorialConvenioOficina h WHERE h.vigente = :v AND h.eliminado = :eli").setParameter("v", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }
}
