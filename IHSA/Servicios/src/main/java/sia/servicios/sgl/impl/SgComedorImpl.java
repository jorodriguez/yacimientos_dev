/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgComedor;
import sia.modelo.SgOficina;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgComedorImpl extends AbstractFacade<SgComedor>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgComedorImpl() {
	super(SgComedor.class);
    }
    @Inject
    private SgCaracteristicaImpl sgCaracteristicaRemote;
    @Inject
    SgCaracteristicaComedorImpl sgCaracteristicaComedorRemote;

    
    public List<SgComedor> traerComedorPorOficina(int sgOficina, boolean BOOLEAN_FALSE) {
	try {
	    return em.createQuery("SELECT o FROM SgComedor o WHERE o.sgOficina.id = :oficina AND o.eliminado = :eli ORDER BY o.nombre ASC")
		    .setParameter("oficina", sgOficina)
		    .setParameter("eli", BOOLEAN_FALSE)
		    .getResultList();
	} catch (Exception e) {
	    return null;
	}
    }

    
    public void guardarOficinaComedor(SgTipo sgTipo, int sgOficina, SgComedor sgComedor, Usuario usuario, boolean BOOLEAN_FALSE) {
	try {
	    sgComedor.setSgOficina(new SgOficina(sgOficina));
	    sgComedor.setGenero(usuario);
	    sgComedor.setFechaGenero(new Date());
	    sgComedor.setHoraGenero(new Date());
	    sgComedor.setEliminado(BOOLEAN_FALSE);
	    create(sgComedor);

	    SgCaracteristica sgCaracteristica = null;
	    sgCaracteristica = sgCaracteristicaRemote.create(("Comedor " + sgComedor.getNombre()), true, Constantes.CERO, usuario.getId());
	    //Crear la caracteristica principal
	    if (sgCaracteristica != null) {
		sgCaracteristicaComedorRemote.create(sgCaracteristica, sgComedor.getId(), null, usuario.getId());
	    }
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	}
    }

    
    public void modificacioOficinaComedor(SgComedor sgComedor, Usuario usuario) {
	sgComedor.setGenero(usuario);
	sgComedor.setFechaGenero(new Date());
	sgComedor.setHoraGenero(new Date());
	edit(sgComedor);

    }

    
    public void eliminarComedorOficina(SgComedor sgComedor, Usuario usuario, boolean BOOLEAN_TRUE) {
	sgComedor.setGenero(usuario);
	sgComedor.setFechaGenero(new Date());
	sgComedor.setHoraGenero(new Date());
	sgComedor.setEliminado(BOOLEAN_TRUE);
	edit(sgComedor);
    }
}
