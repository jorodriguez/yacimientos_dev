/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.ArrayList;
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
import sia.modelo.SiCiudad;
import sia.modelo.SiEstado;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.SiCiudadVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SiCiudadImpl extends AbstractFacade<SiCiudad>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;        
    @Inject
    private SiPaisImpl siPaisRemote;
    @Inject
    private SiEstadoImpl siEstadoRemote;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SiCiudadImpl() {
	super(SiCiudad.class);
    }

    
    public void save(SiCiudad siCiudad, int idSiPais, int idSiEstado, String idUsuario) throws ExistingItemException {
	UtilLog4j.log.info(this, "SiCiudadImpl.save()");

	SiPais siPais = this.siPaisRemote.find(idSiPais);
	SiEstado siEstado = this.siEstadoRemote.find(idSiEstado);
	SiCiudad existente = findByNameAndSiEstado(siCiudad.getNombre(), siEstado, false);

	if (existente == null) {
	    siCiudad.setSiPais(siPais);
	    siCiudad.setSiEstado(siEstado);
	    siCiudad.setGenero(new Usuario(idUsuario));
	    siCiudad.setFechaGenero(new Date());
	    siCiudad.setHoraGenero(new Date());
	    siCiudad.setEliminado(Constantes.NO_ELIMINADO);

	    create(siCiudad);
	    UtilLog4j.log.info(this, "SiCiudad CREATED SUCCESSFULLY");

	} else {
	    throw new ExistingItemException(siCiudad.getNombre(), siCiudad);
	}
    }

    
    public void update(SiCiudad siCiudad, int idSiEstado, String idUsuario) throws ExistingItemException {
	UtilLog4j.log.info(this, "SiCiudadImpl.update()");

	SiEstado siEstado = this.siEstadoRemote.find(idSiEstado);
	SiCiudad original = find(siCiudad.getId());
	SiCiudad existente = findByNameAndSiEstado(siCiudad.getNombre(), siEstado, false);

	if (existente == null) {
	    siCiudad.setModifico(new Usuario(idUsuario));
	    siCiudad.setFechaModifico(new Date());
	    siCiudad.setHoraModifico(new Date());

	    super.edit(siCiudad);
	    UtilLog4j.log.info(this, "SiCiudad UPDATED SUCCESSFULLY");
	} else {
	    if (original.getId().intValue() == existente.getId().intValue()) { //Validar si estoy intentando guardar el mismo
		siCiudad.setModifico(new Usuario(idUsuario));
		siCiudad.setFechaModifico(new Date());
		siCiudad.setHoraModifico(new Date());

		super.edit(siCiudad);
		UtilLog4j.log.info(this, "SiCiudad UPDATED SUCCESSFULLY");
	    } else {
		throw new ExistingItemException(existente.getNombre(), existente);
	    }
	}
    }

    
    public void delete(SiCiudad siCiudad, String idUsuario) throws ItemUsedBySystemException {
	UtilLog4j.log.info(this, "SiCiudadImpl.delete()");

	UtilLog4j.log.info(this, "SiCiudadImpl - justo antes de ir al metodo isUsed()");
	if (!isUsed(siCiudad)) {
	    siCiudad.setModifico(new Usuario(idUsuario));
	    siCiudad.setFechaModifico(new Date());
	    siCiudad.setHoraModifico(new Date());
	    siCiudad.setEliminado(Constantes.ELIMINADO);

	    super.edit(siCiudad);
	    UtilLog4j.log.info(this, "SiCiudad DELETED SUCCESSFULLY");
	} else {
	    throw new ItemUsedBySystemException(siCiudad.getNombre(), siCiudad);
	}
    }

    
    public SiCiudad findByNameAndSiEstado(String nombre, SiEstado siEstado, boolean eliminado) {
	UtilLog4j.log.info(this, "SiCiudadImpl.findByNameAndSiEstado()");

	SiCiudad SiCiudad = null;

	try {
	    SiCiudad = (SiCiudad) em.createQuery("SELECT c FROM SiCiudad c WHERE c.eliminado = :eliminado AND c.nombre = :nombre AND c.siEstado.id = :idSiEstado")
		    .setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO))
		    .setParameter("nombre", nombre)
		    .setParameter("idSiEstado", siEstado.getId())
		    .getSingleResult();
	} catch (NonUniqueResultException nure) {
	    UtilLog4j.log.fatal(this, nure.getMessage());
	    UtilLog4j.log.fatal(this, "Se encontró más de un resultado para el SiCiudad con nombre: " + nombre);
	    return SiCiudad;
	} catch (NoResultException nre) {
	    UtilLog4j.log.fatal(this, nre.getMessage());
	    UtilLog4j.log.fatal(this, "No se encontró ningún SiCiudad con nombre:" + nombre);
	    return SiCiudad;
	}

	return SiCiudad;
    }

    
    public List<SiCiudadVO> findAllNative(String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SiCiudadImpl.findAllNative()");

	List<Object[]> list;
	Query q = em.createNativeQuery("SELECT c.id, " //0
		+ "  c.nombre, " //1
		+ "  p.nombre AS nombre_si_pais, " //2
		+ "  e.nombre AS nombre_si_estado" //3
		+ " FROM SI_CIUDAD c, SI_PAIS p, SI_ESTADO e"
		+ " WHERE c.SI_PAIS =  p.id"
		+ "  AND c.SI_ESTADO = e.id "
		+ "  AND c.eliminado = '" + Constantes.NO_ELIMINADO + "'"
		+ " ORDER BY c. " + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
	list = q.getResultList();

	UtilLog4j.log.info(this, "query: " + q.toString());

	List<SiCiudadVO> voList = new ArrayList<SiCiudadVO>();
	SiCiudadVO vo;
	for (Object[] objects : list) {
	    vo = new SiCiudadVO();
	    vo.setId((Integer) objects[0]);
	    vo.setNombre(String.valueOf(objects[1]));
	    vo.setNombreSiPais((String) objects[2]);
	    vo.setNombreSiEstado((String) objects[3]);
	    voList.add(vo);
	}

	UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SiCiudad");

	return (voList != null ? voList : Collections.EMPTY_LIST);
    }

    
    public List<SiCiudadVO> findAllByRangeNative(String startFilter, String endFilter, String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SiCiudadImpl.findAllByRangeNative()");

	String startFilterMayus = startFilter.toUpperCase();
	String startFilterMinus = startFilter.toLowerCase();
	String endFilterMayus = String.valueOf((char) ((endFilter.toUpperCase().charAt(0)) + 1));
	String endFilterMinus = String.valueOf((char) ((endFilter.toLowerCase().charAt(0)) + 1));

	UtilLog4j.log.info(this, "startFilter: " + startFilterMayus + " " + endFilterMayus);
	UtilLog4j.log.info(this, "endFilter: " + startFilterMinus + " " + endFilterMinus);

	List<Object[]> list;
	Query q = em.createNativeQuery("SELECT c.id, " //0
		+ "  c.nombre, " //1
		+ "  p.nombre AS nombre_si_pais, " //2
		+ "  e.nombre AS nombre_si_estado" //3
		+ " FROM SI_CIUDAD c, SI_PAIS p, SI_ESTADO e"
		+ " WHERE c.SI_PAIS =  p.id"
		+ " AND e.SI_PAIS=p.ID"
		+ " AND c.SI_ESTADO = e.id "
		+ " AND c.eliminado = '" + Constantes.NO_ELIMINADO + "'"
		+ " AND c.eliminado = '" + Constantes.NO_ELIMINADO + "'"
		+ " AND e.eliminado = '" + Constantes.NO_ELIMINADO + "'"
		+ " AND (c.nombre BETWEEN '" + startFilterMayus + "'"
		+ " AND '" + endFilterMayus + "'"
		+ " OR c.nombre BETWEEN '" + startFilterMinus + "'"
		+ " AND '" + endFilterMinus + "')"
		+ " ORDER BY c. " + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC));
	list = q.getResultList();

	UtilLog4j.log.info(this, "query: " + q.toString());

	List<SiCiudadVO> voList = new ArrayList<SiCiudadVO>();
	SiCiudadVO vo;
	for (Object[] objects : list) {
	    vo = new SiCiudadVO();
	    vo.setId((Integer) objects[0]);
	    vo.setNombre(String.valueOf(objects[1]));
	    vo.setNombreSiPais((String) objects[2]);
	    vo.setNombreSiEstado((String) objects[3]);
	    voList.add(vo);
	}

	UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SiCiudad entre los valores: " + startFilter + "-" + endFilter);

	return (voList != null ? voList : Collections.EMPTY_LIST);
    }

    
    public List<SiCiudad> findAll(SiEstado siEstado, String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SiCiudadImpl.findAll()");

	List<SiCiudad> list;

	String query = "SELECT e FROM SiCiudad e WHERE e.eliminado = :eliminado AND e.siEstado.id = :idSiEstado AND e.siEstado.eliminado = :eliminado";

	if (orderByField != null && !orderByField.isEmpty()) {
	    query += " ORDER BY e." + orderByField + " " + (sortAscending ? Constantes.ORDER_BY_ASC : Constantes.ORDER_BY_DESC);
	}

	Query q = em.createQuery(query);

	//Asignando parámetros
	q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
	q.setParameter("idSiEstado", siEstado.getId());

	list = q.getResultList();

	UtilLog4j.log.info(this, "Se encontraron " + (list != null ? list.size() : 0) + " SiCiudad");

	return (list != null ? list : Collections.EMPTY_LIST);
    }

    
    public List<SiCiudad> findAll(int idSiEstado, String orderByField, boolean sortAscending, boolean eliminado) {
	UtilLog4j.log.info(this, "SiCiudadImpl.findAll()");
	if (idSiEstado > 0) {
	    SiEstado siEstado = this.siEstadoRemote.find(idSiEstado);
	    return findAll(siEstado, orderByField, sortAscending, eliminado);
	} else {
	    return Collections.EMPTY_LIST;
	}
    }

    public boolean isUsed(SiCiudad siCiudad) {
	UtilLog4j.log.info(this, "SiCiudadImpl.isUsed()");

	UtilLog4j.log.info(this, "En el metodo isUsed() de SiCiudadImpl");

	int cont = 0;

	List<Object> list = Collections.EMPTY_LIST;

	list = em.createQuery("SELECT a FROM SgItinerario a WHERE a.siCiudadOrigen.id = :idSiCiudadOrigen OR a.siCiudadDestino.id = :idSiCiudadDestino AND a.eliminado = :eliminado")
		.setParameter("eliminado", Constantes.NO_ELIMINADO)
		.setParameter("idSiCiudadOrigen", siCiudad.getId().intValue())
		.setParameter("idSiCiudadDestino", siCiudad.getId().intValue())
		.getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SiCiudad " + siCiudad.getId().intValue() + " usado en SgItinerario");
	    cont++;
	    list.clear();
	}

	list = em.createQuery("SELECT a FROM SgDetalleItinerario a WHERE a.siCiudadOrigen.id = :idSiCiudadOrigen OR a.siCiudadDestino.id = :idSiCiudadDestino AND a.eliminado = :eliminado")
		.setParameter("eliminado", Constantes.NO_ELIMINADO)
		.setParameter("idSiCiudadOrigen", siCiudad.getId().intValue())
		.setParameter("idSiCiudadDestino", siCiudad.getId().intValue())
		.getResultList();
	if (list != null && !list.isEmpty()) {
	    UtilLog4j.log.info(this, "SiCiudad " + siCiudad.getId().intValue() + " usado en SgDetalleItinerario");
	    cont++;
	    list.clear();
	}

	return (cont == 0 ? false : true);
    }

    public List<Object[]> traerCiudadJson(int id) {
	List<Object[]> listaCiudades;
	clearQuery();
	clearQuery();
	query.append(" select c.ID,c.NOMBRE,e.NOMBRE,p.NOMBRE from SI_CIUDAD c");
	query.append(" inner join SI_ESTADO e on e.ID=c.SI_ESTADO");
	query.append(" inner join SI_PAIS p on p.ID=e.SI_PAIS");
	query.append(" where p.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" and e.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" and c.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
	query.append(" and c.id <> ").append(id);
	listaCiudades = em.createNativeQuery(query.toString()).getResultList();
	return listaCiudades;
    }

    
    public SiCiudad buscarPorNombre(String ciudad, int estado) {
	String sb = "select c.* from si_ciudad c where upper(c.NOMBRE) similar  to upper(?)  and c.SI_ESTADO = ? and c.eliminado = False";
	try {
	    return (SiCiudad) em.createNativeQuery(sb, SiCiudad.class).setParameter(1, ciudad).setParameter(2, estado).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.warn(e);
	    return null;
	}

    }
}
