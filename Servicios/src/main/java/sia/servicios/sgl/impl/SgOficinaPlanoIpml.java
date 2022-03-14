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
import sia.modelo.SgOficina;
import sia.modelo.SgOficinaPlano;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgOficinaPlanoIpml extends AbstractFacade<SgOficinaPlano>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgOficinaPlanoIpml() {
	super(SgOficinaPlano.class);
    }

    
    public void guardarOficinaPlano(Usuario usuario, int sgOficina, SiAdjunto siAdjunto) {

	SgOficinaPlano sgOficinaPlano = new SgOficinaPlano();
	sgOficinaPlano.setSgOficina(new SgOficina(sgOficina));
	sgOficinaPlano.setSiAdjunto(siAdjunto);
	sgOficinaPlano.setGenero(usuario);
	sgOficinaPlano.setFechaGenero(new Date());
	sgOficinaPlano.setHoraGenero(new Date());
	sgOficinaPlano.setVigente(Constantes.BOOLEAN_TRUE);
	sgOficinaPlano.setEliminado(Constantes.NO_ELIMINADO);
	create(sgOficinaPlano);

    }

    
    public void quitarVigenteOficinaPlano(Usuario usuario, SgOficinaPlano sgOficinaPlano) {
	sgOficinaPlano.setGenero(usuario);
	sgOficinaPlano.setFechaGenero(new Date());
	sgOficinaPlano.setHoraGenero(new Date());
	sgOficinaPlano.setVigente(Constantes.BOOLEAN_FALSE);
	edit(sgOficinaPlano);
    }

    
    public boolean eliminarOficinaPlano(Usuario usuario, SgOficinaPlano sgOficinaPlano) {
	try {
	    sgOficinaPlano.setGenero(usuario);
	    sgOficinaPlano.setFechaGenero(new Date());
	    sgOficinaPlano.setHoraGenero(new Date());
	    sgOficinaPlano.setVigente(Constantes.BOOLEAN_FALSE);
	    sgOficinaPlano.setEliminado(Constantes.ELIMINADO);
	    edit(sgOficinaPlano);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    
    public List<SgOficinaPlano> traerPlanoOficina(int sgOficina) {
	try {
	    return em.createQuery("SELECT p FROM SgOficinaPlano p WHERE p.sgOficina.id = :id AND "
		    + " p.eliminado = :eli ORDER BY p.id DESC").setParameter("id", sgOficina).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public List<SgOficinaPlano> buscarPlanoVigente(int sgOficina) {
	try {
	    return em.createQuery("SELECT p FROM SgOficinaPlano p WHERE p.sgOficina.id = :id AND p.eliminado = :eli "
		    + " AND p.vigente = :v ORDER BY p.id ASC").setParameter("v", Constantes.BOOLEAN_TRUE).setParameter("id", sgOficina).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
	} catch (Exception e) {
	    return null;
	}
    }
}
