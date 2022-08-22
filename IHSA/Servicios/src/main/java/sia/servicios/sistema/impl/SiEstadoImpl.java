/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.modelo.SiEstado;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SiEstadoImpl extends AbstractFacade<SiEstado> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    
    @Inject
    private SiPaisImpl siPaisRemote;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SiEstadoImpl() {
	super(SiEstado.class);
    }

    
    public void save(SiEstado siEstado, int idSiPais, String idUsuario) throws ExistingItemException {
	UtilLog4j.log.info(this, "SiEstadoImpl.save()");

	SiPais siPais = this.siPaisRemote.find(idSiPais);
	SiEstado existente = findByNameAndSiPais(siEstado.getNombre(), siPais, false);

	if (existente == null) {
	    siEstado.setSiPais(siPais);
	    siEstado.setGenero(new Usuario(idUsuario));
	    siEstado.setFechaGenero(new Date());
	    siEstado.setHoraGenero(new Date());
	    siEstado.setEliminado(Constantes.NO_ELIMINADO);

	    create(siEstado);
	    UtilLog4j.log.info(this, "SiEstado CREATED SUCCESSFULLY");
	} else {
	    throw new ExistingItemException(siEstado.getNombre(), siEstado);
	}
    }

    
    public void update(SiEstado siEstado, int idSiPais, String idUsuario) throws ExistingItemException {
	UtilLog4j.log.info(this, "SiEstadoImpl.update()");

	SiPais siPais = this.siPaisRemote.find(idSiPais);
	SiEstado original = super.find(siEstado.getId());
	SiEstado existente = findByNameAndSiPais(siEstado.getNombre(), siPais, false);
	if (existente == null) {
	    siEstado.setModifico(new Usuario(idUsuario));
	    siEstado.setFechaModifico(new Date());
	    siEstado.setHoraModifico(new Date());

	    edit(siEstado);
	    UtilLog4j.log.info(this, "SiEstado UPDATED SUCCESSFULLY");
	} else {
	    if (original.getId().intValue() == existente.getId().intValue()) { //Validar si estoy intentando guardar el mismo
		siEstado.setModifico(new Usuario(idUsuario));
		siEstado.setFechaModifico(new Date());
		siEstado.setHoraModifico(new Date());

		edit(siEstado);
		UtilLog4j.log.info(this, "SiEstado UPDATED SUCCESSFULLY");
	    } else {
		throw new ExistingItemException(existente.getNombre(), existente);
	    }
	}
    }

    
    public void delete(SiEstado siEstado, String idUsuario) throws ItemUsedBySystemException {
	UtilLog4j.log.info(this, "SiEstadoImpl.delete()");

	if (!isUsed(siEstado)) {
	    siEstado.setModifico(new Usuario(idUsuario));
	    siEstado.setFechaModifico(new Date());
	    siEstado.setHoraModifico(new Date());
	    siEstado.setEliminado(Constantes.ELIMINADO);
	    edit(siEstado);
	    UtilLog4j.log.info(this, "SiEstado DELETED SUCCESSFULLY");
	} else {
	    throw new ItemUsedBySystemException(siEstado.getNombre(), siEstado);
	}
    }

    
    public SiEstado findByNameAndSiPais(String nombre, SiPais siPais, boolean eliminado) {
	UtilLog4j.log.info(this, "SiEstadoImpl.findByNameAndSiPais()");

	SiEstado siEstado = null;

	try {
	    siEstado = (SiEstado) em.createQuery("SELECT e FROM SiEstado e WHERE e.eliminado = :eliminado AND e.nombre = :nombre AND e.siPais.id = :idSiPais").setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO)).setParameter("nombre", nombre).setParameter("idSiPais", siPais.getId()).getSingleResult();
	} catch (NonUniqueResultException nure) {
	    UtilLog4j.log.fatal(this, nure.getMessage());
	    UtilLog4j.log.fatal(this, "Se encontró más de un resultado para el SiEstado con nombre: " + nombre);
	    return siEstado;
	} catch (NoResultException nre) {
	    UtilLog4j.log.fatal(this, nre.getMessage());
	    UtilLog4j.log.fatal(this, "No se encontró ningún SiEstado con nombre:" + nombre);
	    return siEstado;
	}

	return siEstado;
    }

    
    public List<SiEstado> findAll(SiPais siPais, String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SiEstadoImpl.findAll()");

	List<SiEstado> list;

	String query = "SELECT e FROM SiEstado e WHERE e.eliminado = :eliminado AND e.siPais.id = :idSiPais";

	if (orderByField != null && !orderByField.isEmpty()) {
	    query += " ORDER BY e." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
	}

	Query q = em.createQuery(query);

	//Asignando parámetros
	q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
	q.setParameter("idSiPais", siPais.getId());

	list = q.getResultList();

	UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SiEstado");

	return (list != null ? list : Collections.EMPTY_LIST);
    }

    
    public List<SiEstado> findAll(int idSiPais, String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SiEstadoImpl.findAll()");
	if (idSiPais > 0) {
	    SiPais siPais = this.siPaisRemote.find(idSiPais);
	    return findAll(siPais, orderByField, sortAscending, eliminado);
	} else {
	    return Collections.EMPTY_LIST;
	}
    }

    public boolean isUsed(SiEstado siEstado) {
	UtilLog4j.log.info(this, "SiEstadoImpl.isUsed()");

	int cont = 0;

	List<Object> list = null;

	list = em.createQuery("SELECT c FROM SiCiudad c WHERE c.siEstado.id = :idSiEstado AND c.eliminado = :eliminado").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("idSiEstado", siEstado.getId().intValue()).getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SiEstado " + siEstado.getId() + " usado en SiCiudad");
	    cont++;
	    list.clear();
	}

	return (cont != 0);
    }

    
    public SiEstado save(String estado, int pais, String sesion) throws ExistingItemException {
	UtilLog4j.log.info(this, "SiEstadoImpl.save()");

	SiPais siPais = this.siPaisRemote.find(pais);
	SiEstado siEstado = findByNameAndSiPais(estado, siPais, false);

	if (siEstado == null) {
	    siEstado = new SiEstado();
	    siEstado.setSiPais(siPais);
	    siEstado.setGenero(new Usuario(sesion));
	    siEstado.setFechaGenero(new Date());
	    siEstado.setHoraGenero(new Date());
	    siEstado.setEliminado(Constantes.NO_ELIMINADO);
	    //
	    create(siEstado);
	    UtilLog4j.log.info(this, "SiEstado CREATED SUCCESSFULLY");

	} else {
	    throw new ExistingItemException(estado, siEstado);
	}
	return siEstado;
    }
}
