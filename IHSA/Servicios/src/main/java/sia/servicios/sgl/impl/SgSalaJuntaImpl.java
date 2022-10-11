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
import sia.modelo.SgSalaJunta;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgSalaJuntaImpl extends AbstractFacade<SgSalaJunta>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }
    @Inject
    private SgCaracteristicaImpl sgCaracteristicaRemote;
    @Inject
    private SgCaracteristicaSalaJuntaImpl sgCaracteristicaSalaJuntaRemote;

    public SgSalaJuntaImpl() {
	super(SgSalaJunta.class);
    }

    
    public void guardarOficinaSalaJunta(SgTipo sgTipo, int sgOficina, SgSalaJunta sgSalaJunta, Usuario usuario, boolean eliminadoFalse) {
	sgSalaJunta.setSgOficina(new SgOficina(sgOficina));
	sgSalaJunta.setGenero(usuario);
	sgSalaJunta.setFechaGenero(new Date());
	sgSalaJunta.setHoraGenero(new Date());
	sgSalaJunta.setEliminado(eliminadoFalse);
	create(sgSalaJunta);

	SgCaracteristica caracteristica = null;
	try {
	    caracteristica = sgCaracteristicaRemote.create(("Sala de Juntas " + sgSalaJunta.getNombre()), true, Constantes.CERO, usuario.getId());
	    if (caracteristica != null) {

		sgCaracteristicaSalaJuntaRemote.create(caracteristica, sgSalaJunta.getId(), null, usuario.getId());
	    }
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	}
    }

    
    public List<SgSalaJunta> traerSalaJuntaOficina(int sgOficina, boolean BOOLEAN_FALSE) {
	return em.createQuery("SELECT s FROM SgSalaJunta s WHERE s.sgOficina.id = :ofi AND  s.eliminado = :eli ORDER BY s.nombre ASC ")
		.setParameter("ofi", sgOficina)
		.setParameter("eli", BOOLEAN_FALSE)
		.getResultList();
    }

    
    public void modificarSalaJuntaOficina(SgSalaJunta sgSalaJunta, Usuario usuario) {
	sgSalaJunta.setGenero(usuario);
	sgSalaJunta.setFechaGenero(new Date());
	sgSalaJunta.setHoraGenero(new Date());
	edit(sgSalaJunta);
    }

    
    public void eliminarSalaJuntaOficina(SgSalaJunta sgSalaJunta, Usuario usuario, boolean eliminado) {
	sgSalaJunta.setGenero(usuario);
	sgSalaJunta.setFechaGenero(new Date());
	sgSalaJunta.setHoraGenero(new Date());
	sgSalaJunta.setEliminado(eliminado);
	edit(sgSalaJunta);
    }

    
    public List<SgSalaJunta> getAllSalaJuntaByOficinaList(int oficina, boolean estado) {
	List<SgSalaJunta> salasJuntasList = null;

	if (oficina > 0) {
	    try {
		salasJuntasList = em.createQuery("SELECT sj FROM SgSalaJunta sj WHERE sj.eliminado = :estado AND sj.sgOficina.id = :idOficina")
			.setParameter("estado", estado)
			.setParameter("idOficina", oficina)
			.getResultList();
	    } catch (Exception e) {
            UtilLog4j.log.error(e);
		return salasJuntasList;
	    }

	    if (salasJuntasList != null) {
		return salasJuntasList;
	    } else {
		return salasJuntasList;
	    }
	} else {
	    return salasJuntasList;
	}
    }
}
