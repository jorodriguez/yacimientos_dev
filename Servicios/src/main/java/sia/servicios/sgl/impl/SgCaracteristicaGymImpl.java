/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaGym;
import sia.modelo.SgGym;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgCaracteristicaGymImpl extends AbstractFacade<SgCaracteristicaGym> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaGymImpl() {
	super(SgCaracteristicaGym.class);
    }

    
    public SgCaracteristicaGym findByCaracteristicaAndGimnasio(SgCaracteristica caracteristica, SgGym gimnasio) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaGymImpl.findByCaracteristicaAndGimnasio()");

	if (caracteristica != null && gimnasio != null) {
	    try {
		return (SgCaracteristicaGym) em.createQuery("SELECT c FROM SgCaracteristicaGym c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgGym.id = :idGimnasio AND c.eliminado = :eliminado")
			.setParameter("idCaracteristica", caracteristica.getId())
			.setParameter("idGimnasio", gimnasio.getId())
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaGymImpl.class.getName(), "findByCaracteristicaAndGimnasio()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " habitacion: " + (gimnasio != null ? gimnasio.getId() : null)));
	}
    }

    
    public SgCaracteristicaGym create(SgCaracteristica caracteristica, SgGym gimnasio, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaGymImpl.create()");

	SgCaracteristicaGym caracteristicaGimnasio = null;

	if (caracteristica != null && gimnasio != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaGimnasio = new SgCaracteristicaGym();
	    caracteristicaGimnasio.setSgCaracteristica(caracteristica);
	    caracteristicaGimnasio.setSgGym(gimnasio);
	    caracteristicaGimnasio.setCantidad((cantidad != null && cantidad > 0) ? cantidad : 1);
	    caracteristicaGimnasio.setGenero(new Usuario(idUsuario));
	    caracteristicaGimnasio.setFechaGenero(new Date());
	    caracteristicaGimnasio.setHoraGenero(new Date());
	    caracteristicaGimnasio.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaGimnasio);
	} else {
	    throw new SIAException(SgCaracteristicaGymImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Gym",
		    ("Parámetros: "
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " gimnasio: " + (gimnasio != null ? gimnasio.getId() : null)
		    + " cantidad: " + cantidad
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaGym CREATED SUCCESSFULLY");
	return caracteristicaGimnasio;
    }

    
    public SgCaracteristicaGym update(SgCaracteristicaGym caracteristicaGimnasio, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaGymImpl.update()");

	if (caracteristicaGimnasio != null && idUsuario != null && !idUsuario.equals("")) {
	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaGimnasio.getId()).toString().equals(caracteristicaGimnasio.toString())) {
		super.edit(caracteristicaGimnasio);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaGymImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-Gym",
		    ("Parámetros:"
		    + " caracteristicaGimnasio: " + (caracteristicaGimnasio != null ? caracteristicaGimnasio.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaGym UPDATED SUCCESSFULLY");
	return caracteristicaGimnasio;
    }

    
    public SgCaracteristicaGym delete(int idCaracteristicaGimnasio, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaGymImpl.delete()");
	SgCaracteristicaGym caracteristicaGimnasio = find(idCaracteristicaGimnasio);
	if (caracteristicaGimnasio != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaGimnasio.setEliminado(Constantes.ELIMINADO);

	    super.edit(caracteristicaGimnasio);
	} else {
	    throw new SIAException(SgCaracteristicaGymImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-Gimnasio",
		    ("Parámetros: "
		    + " caracteristicaGimnasio: " + (caracteristicaGimnasio != null ? caracteristicaGimnasio.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaGym DELETED SUCCESSFULLY");
	return caracteristicaGimnasio;
    }

    
    public SgCaracteristicaGym getCaracteristicaGymPrincipalByGimnasio(SgGym gimnasio) throws SIAException, NonUniqueResultException, NoResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaGymImpl.getCaracteristicaGimnasioPrincipalByGimnasio()");

	if (gimnasio != null) {
	    try {
		return (SgCaracteristicaGym) em.createQuery("SELECT c FROM SgCaracteristicaGym c WHERE c.eliminado = :estado AND c.sgGym.id = :idGimnasio AND c.sgCaracteristica.principal = :principal")
			.setParameter("estado", Constantes.NO_ELIMINADO)
			.setParameter("principal", Constantes.BOOLEAN_TRUE)
			.setParameter("idGimnasio", gimnasio.getId())
			.getSingleResult();
	    } catch (NoResultException nre) {
		throw new SIAException(SgCaracteristicaGymImpl.class.getName(),
			"getCaracteristicaGymPrincipalByGimnasio", "No se encontró la Característica Principal del Gimnasio: " + gimnasio.getNombre(),
			("Gimnasio: " + gimnasio.getId()));
	    }
	} else {
	    throw new SIAException(SgCaracteristicaGymImpl.class.getName(), "getCaracteristicaGimnasioPrincipalByGimnasio()",
		    "Faltan parámetros para poder buscar la relación principal Característica-Gym",
		    ("Parámetros: "
		    + "gimnasio: " + (gimnasio != null ? gimnasio.getId() : null)));
	}
    }

    
    public List<CaracteristicaVo> getAllCaracteristicaGymByGimnasioList(int gimnasio) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaGymImpl.getAllCaracteristicaGymByGymList");
	String q = "select c.ID, c.NOMBRE as CARACTERISTICA,c.PRINCIPAL, ch.CANTIDAD, t.ID as tipo, t.NOMBRE from SG_CARACTERISTICA_GYM ch"
		+ "	    inner join SG_CARACTERISTICA c on ch.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    inner join SG_GYM g on ch.SG_GYM = g.ID"
		+ "	    where g.id = ?"
		+ "	    and ch.ELIMINADO = 'False'";
	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, gimnasio).getResultList();
	for (Object[] lo1 : lo) {
	    CaracteristicaVo caracteristicaVo = new CaracteristicaVo();
	    caracteristicaVo.setId((Integer) lo1[0]);
	    caracteristicaVo.setNombre((String) lo1[1]);
	    caracteristicaVo.setPrincipal((Boolean) lo1[2]);
	    caracteristicaVo.setCantidad((Integer) lo1[3]);
	    caracteristicaVo.setIdTipo((Integer) lo1[4]);
	    caracteristicaVo.setTipo((String) lo1[5]);
	    caracteristicas.add(caracteristicaVo);
	}
	return caracteristicas;

    }

    
    public Map<String, CaracteristicaVo> getAllCaracteristicaGymByGimnasioMap(int gimnasio) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaGymImpl.getAllCaracteristicaGymByGymMap()");

	List<CaracteristicaVo> caracteristicasList = getAllCaracteristicaGymByGimnasioList(gimnasio);
	Map<String, CaracteristicaVo> caracteristicasMap = null;

	if (caracteristicasList != null) {
	    caracteristicasMap = new TreeMap<String, CaracteristicaVo>();
	    for (CaracteristicaVo caracteristica : caracteristicasList) {
		caracteristicasMap.put(caracteristica.getNombre(), caracteristica);
	    }
	    return caracteristicasMap;
	}
	return caracteristicasMap;
    }
}
