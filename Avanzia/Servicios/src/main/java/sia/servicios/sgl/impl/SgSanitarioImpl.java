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
import sia.modelo.SgOficina;
import sia.modelo.SgSanitario;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgSanitarioImpl extends AbstractFacade<SgSanitario>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    private SgCaracteristicaImpl sgCaracteristicaRemote;
    @Inject
    private SgCaracteristicaSanitarioImpl sgCaracteristicaSanitarioRemote;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgSanitarioImpl() {
	super(SgSanitario.class);
    }

    
    public List<SgSanitario> traerSanitario(int sgOficina, boolean eliminado) {
	return em.createQuery("SELECT s FROM SgSanitario s WHERE s.sgOficina.id = :ofi AND s.eliminado = :eli ORDER BY s.nombre ASC")
		.setParameter("ofi", sgOficina)
		.setParameter("eli", eliminado)
		.getResultList();
    }

    
    public void guardarOficinaSanitario(SgTipo sgTipo, int sgOficina, SgSanitario sgSanitario, Usuario usuario, boolean eliminado) {
	sgSanitario.setSgOficina(new SgOficina(sgOficina));
	sgSanitario.setGenero(usuario);
	sgSanitario.setFechaGenero(new Date());
	sgSanitario.setHoraGenero(new Date());
	sgSanitario.setEliminado(eliminado);
	create(sgSanitario);

	SgCaracteristica caracteristica = null;
	try {
	    caracteristica = sgCaracteristicaRemote.create(("Sanitario " + sgSanitario.getNombre()), true, Constantes.TIPO_PAGO_OFICINA, usuario.getId());
	    if (caracteristica != null) {

		sgCaracteristicaSanitarioRemote.create(caracteristica, sgSanitario.getId(), null, usuario.getId());
	    }
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	}
    }

    
    public void modificarOficinaSanitario(SgSanitario sgSanitario, Usuario usuario) {
	sgSanitario.setGenero(usuario);
	sgSanitario.setFechaGenero(new Date());
	sgSanitario.setHoraGenero(new Date());
	edit(sgSanitario);
    }

    
    public void eliminarOficinaSanitario(SgSanitario sgSanitario, Usuario usuario, boolean eliminado) {
	sgSanitario.setGenero(usuario);
	sgSanitario.setFechaGenero(new Date());
	sgSanitario.setHoraGenero(new Date());
	sgSanitario.setEliminado(eliminado);
	edit(sgSanitario);
    }

    
    public List<SgSanitario> getAllSanitarioByOficinaList(int oficina, boolean status) {
	List<SgSanitario> sanitariosList = null;
	try {
	    sanitariosList = em.createQuery("SELECT san FROM SgSanitario san WHERE san.eliminado = :estado AND san.sgOficina.id = :idOficina")
		    .setParameter("estado", status)
		    .setParameter("idOficina", oficina)
		    .getResultList();
	    return sanitariosList;
	} catch (Exception e) {
	    UtilLog4j.log.error(e);
	    return sanitariosList;
	}
    }
}
