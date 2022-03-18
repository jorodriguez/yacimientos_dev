/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgCaracteristicaImpl extends AbstractFacade<SgCaracteristica> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaImpl() {
	super(SgCaracteristica.class);
    }

    
    public SgCaracteristica create(String nombreCaracteristica, boolean principal, int tipo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.create()");

	SgCaracteristica caracteristica = null;

	if (nombreCaracteristica != null && !nombreCaracteristica.equals("") && idUsuario != null && !idUsuario.equals("")) {
	    if (principal) { //Si la Característica es Principal, esta se guarda sin más, ya que siempre es 1-1 con el área a la que está asignada
		caracteristica = new SgCaracteristica();
		caracteristica.setNombre(nombreCaracteristica);
		caracteristica.setPrincipal(Constantes.BOOLEAN_TRUE);
		caracteristica.setGenero(new Usuario(idUsuario));
		caracteristica.setFechaGenero(new Date());
		caracteristica.setHoraGenero(new Date());
		caracteristica.setEliminado(Constantes.NO_ELIMINADO);

		super.create(caracteristica);
	    } else {
		//No permitir Características duplicadas
		SgCaracteristica caracteristicaExistente = findByName(nombreCaracteristica);
		if (caracteristicaExistente == null) { //No existe la Característica
		    caracteristica = new SgCaracteristica();
		    caracteristica.setNombre(nombreCaracteristica);
		    caracteristica.setPrincipal(Constantes.BOOLEAN_FALSE);
		    caracteristica.setSgTipo(new SgTipo(tipo));
		    caracteristica.setGenero(new Usuario(idUsuario));
		    caracteristica.setFechaGenero(new Date());
		    caracteristica.setHoraGenero(new Date());
		    caracteristica.setEliminado(Constantes.NO_ELIMINADO);

		    super.create(caracteristica);
		} else {
		    throw new SIAException(SgCaracteristicaImpl.class.getName(), "create()",
			    "Ya existe la Característica con el nombre: " + nombreCaracteristica);
		}
	    }
	} else {
	    throw new SIAException(SgCaracteristicaImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la Característica",
		    ("Parámetros: nombreCaracteristica: " + nombreCaracteristica
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "Característica CREATED SUCCESSFULLY");
	UtilLog4j.log.info(this, "Característica: " + caracteristica);
	return caracteristica;
    }

    
    public SgCaracteristica update(SgCaracteristica caracteristica, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.update()");

	//Si es la misma, no hacer nada
	SgCaracteristica caracteristicaOriginal = super.find(caracteristica.getId());

	if (!caracteristicaOriginal.toString().equals(caracteristica.toString())) { //Validar si no es la misma Característica
	    if (caracteristica != null && idUsuario != null && !idUsuario.equals("")) {
		//No permitir guardar Características duplicadas
		SgCaracteristica caracteristicaExistente = findByName(caracteristica.getNombre());

		if (caracteristicaExistente == null) { //No existe Característica
		    super.edit(caracteristica);
		} else {//Característica ya existente
		    throw new SIAException(SgCaracteristicaImpl.class.getName(), "update()",
			    "Ya existe la Característica con el nombre: " + caracteristica.getNombre());
		}
	    } else {
		throw new SIAException(SgCaracteristicaImpl.class.getName(), "update()",
			"Faltan parámetros para poder actualizar la Característica",
			("Parámetros: caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
			+ " idUsuario: " + idUsuario));
	    }
	}

	UtilLog4j.log.info(this, "Característica UPDATED SUCCESSFULLY");
	return caracteristica;
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SgCaracteristica delete(SgCaracteristica caracteristica, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.delete()");

	if (caracteristica != null && idUsuario != null && !idUsuario.equals("")) {

	    List<String> searchUsages = searchUsages(caracteristica);

	    if (searchUsages != null && searchUsages.size() > 0) {
		String usages = "";

		for (String u : searchUsages) {
		    usages += u + ", ";
		}

		throw new SIAException(SgCaracteristicaImpl.class.getName(), "delete()", ("La Característica ya está siendo usada en las siguientes partes del Sistema: " + usages));

	    } else {
		caracteristica.setEliminado(Constantes.ELIMINADO);
		super.edit(caracteristica);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la Característica",
		    ("Parámetros: caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "Característica DELETED SUCCESSFULLY");
	return caracteristica;
    }

    
    public SgCaracteristica findByName(String nombre) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.findByName()");

	SgCaracteristica caracteristica = null;

	if (nombre != null && !nombre.equals("")) {
	    try {
		return (SgCaracteristica) em.createQuery("SELECT c FROM SgCaracteristica c WHERE c.nombre = :nombre AND c.principal = :principal AND c.eliminado = :eliminado")
			.setParameter("nombre", nombre)
			.setParameter("principal", Constantes.BOOLEAN_FALSE)
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.getSingleResult();
	    } catch (NoResultException nre) {
		UtilLog4j.log.fatal(this, "No se encontró la Característica: " + nombre);
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaImpl.class.getName(), "findByName()",
		    "Falta el parámetro \"nombre\"",
		    ("Parámetros: nombre: " + nombre));
	}
    }

    
    public List<SgCaracteristica> findAll(Boolean principal, Boolean eliminado) throws Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.findAll()");

	List<SgCaracteristica> caracteristicas = null;

	if (principal != null && eliminado != null) {

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristica c WHERE c.principal = :principal AND c.eliminado = :eliminado ORDER BY c.nombre")
		    .setParameter("principal", (principal ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE))
		    .setParameter("eliminado", (eliminado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE))
		    .getResultList();
	} else if (principal == null && eliminado == null) {
	    return super.findAll();
	} else if (principal != null && eliminado == null) {
	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristica c WHERE c.principal = :principal ORDER BY c.nombre")
		    .setParameter("principal", (principal ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE))
		    .getResultList();
	} else {
	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristica c WHERE c.eliminado = :eliminado ORDER BY c.nombre")
		    .setParameter("eliminado", (eliminado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE))
		    .getResultList();
	}

	UtilLog4j.log.info(this, "Se encontraron " + (caracteristicas != null ? caracteristicas.size() : 0) + " caracteristicas");
	return caracteristicas;
    }

    
    public List<SgCaracteristica> getAllCaracteristicasByTipoAndPrincipalList(boolean principal, int tipo, boolean status) {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.getAllCaracteristicasByTipoAndPrincipalList()");

	List<SgCaracteristica> caracteristicas = null;

	try {
	    caracteristicas = em.createQuery("SELECT car FROM SgCaracteristica car WHERE car.eliminado = :estado AND car.principal = :principal AND car.sgTipo.id = :idTipo")
		    .setParameter("estado", status)
		    .setParameter("principal", principal)
		    .setParameter("idTipo", tipo)
		    .getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return caracteristicas;
	}
	if (caracteristicas != null) {
	    UtilLog4j.log.info(this, "Se encontraron " + caracteristicas.size() + " características de tipo: " + tipo);
	    return caracteristicas;
	} else {
	    UtilLog4j.log.info(this, "No se encontraron Características de Tipo: " + tipo);
	    return caracteristicas;
	}
    }

    
    public List<SgCaracteristica> getAllCaracteristicasByPrincipalList(String principal, String status) {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.getAllCaracteristicasByPrincipalList()");

	List<SgCaracteristica> caracteristicas = null;

	if (principal != null && !principal.equals("")) {
	    try {
		caracteristicas = em.createQuery("SELECT car FROM SgCaracteristica car WHERE car.eliminado = :estado AND car.principal = :principal")
			.setParameter("estado", status)
			.setParameter("principal", principal)
			.getResultList();
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		return caracteristicas;
	    }
	    if (caracteristicas != null) {
		UtilLog4j.log.info(this, "Se encontraron " + caracteristicas.size() + " Características de campo Principal: " + principal);
		return caracteristicas;
	    } else {
		UtilLog4j.log.info(this, "No se encontraron Características con campo Principal: " + principal);
		return caracteristicas;
	    }
	} else {
	    UtilLog4j.log.info(this, "No se pudo hacer la búsqueda de Características por Principal porque falta este parámetro");
	    return caracteristicas;
	}
    }

    
    public List<CaracteristicaVo> getAllSgCaracteristicaStaffAndOficina() throws Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.getAllSgCaracteristicaStaffAndOficina()");

	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();
	String q = " SELECT car.id, car.NOMBRE FROM Sg_Caracteristica car "
		+ "  WHERE car.PRINCIPAL = 'False'"
		+ "  and car.SG_TIPO between 2 and 3	"
		+ "  and car.ELIMINADO = 'False'"
		+ "  order by car.nombre";
	List<Object[]> lo = em.createNativeQuery(q).getResultList();

	for (Object[] obj : lo) {
	    CaracteristicaVo cvo = new CaracteristicaVo();
	    cvo.setId((Integer) obj[0]);
	    cvo.setNombre((String) obj[1]);
	    caracteristicas.add(cvo);
	}
	return caracteristicas;
    }

    
    public List<String> searchUsages(SgCaracteristica caracteristica) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaImpl.searchUsages()");

	List<String> lugares = null;

	if (caracteristica != null) {
	    lugares = new ArrayList<String>();

	    List<Object> caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaCocina c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaCocina: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaComedor c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaComedor: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaGym c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaGym: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaHabitacion c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaHabitacion: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaOficina c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaOficina: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaSalaJunta c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaSalaJunta: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaSanitario c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaSanitario: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaStaff c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaStaff: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgCaracteristicaVehiculo c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgCaracteristicaVehiculo: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	    caracteristicas = em.createQuery("SELECT c FROM SgChecklistDetalle c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.eliminado = :eliminado")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idCaracteristica", caracteristica.getId())
		    .getResultList();
	    if (caracteristicas != null && !caracteristicas.isEmpty()) {
		lugares.add("SgChecklistDetalle: " + caracteristicas.size());
		caracteristicas.clear();
	    }

	} else {
	    throw new SIAException(SgCaracteristicaImpl.class.getName(), "searchUsages()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " característica: " + (caracteristica != null ? caracteristica.getId() : null)));
	}

	if (lugares != null && !lugares.isEmpty()) {
	    UtilLog4j.log.info(this, "La Característica: " + caracteristica.getNombre() + " está siendo ya usada en " + lugares.size() + " lugares");
	} else {
	    UtilLog4j.log.info(this, "La Característica " + caracteristica.getNombre() + " no está siendo usada en ningún lugar");
	}

	return lugares;
    }

    
    public List<CaracteristicaVo> traerCaracteristicaPorTipo(int tipo) throws SIAException, Exception {
	String q = " select c.id, c.nombre from sg_caracteristica c"
		+ "	where c.sg_tipo = " + tipo
		+ "	and c.eliminado = 'False'";
	List<Object[]> lo = em.createNativeQuery(q).getResultList();
	List<CaracteristicaVo> lc = new ArrayList<CaracteristicaVo>();
	for (Object[] lo1 : lo) {
	    CaracteristicaVo cvo = new CaracteristicaVo();
	    cvo.setId((Integer) lo1[0]);
	    cvo.setNombre((String) lo1[1]);
	    lc.add(cvo);
	}
	return lc;
    }
}
