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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaHabitacion;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgCaracteristicaHabitacionImpl extends AbstractFacade<SgCaracteristicaHabitacion>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaHabitacionImpl() {
	super(SgCaracteristicaHabitacion.class);
    }

    
    public SgCaracteristicaHabitacion findByCaracteristicaAndHabitacion(SgCaracteristica caracteristica, SgStaffHabitacion habitacion) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaHabitacionImpl.findByCaracteristicaAndHabitacion()");

	if (caracteristica != null && habitacion != null) {
	    try {
		return (SgCaracteristicaHabitacion) em.createQuery("SELECT c FROM SgCaracteristicaHabitacion c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgStaffHabitacion.id = :idHabitacion AND c.eliminado = :eliminado")
			.setParameter("idCaracteristica", caracteristica.getId())
			.setParameter("idHabitacion", habitacion.getId())
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaHabitacionImpl.class.getName(), "findByCaracteristicaAndHabitacion()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " habitacion: " + (habitacion != null ? habitacion.getId() : null)));
	}
    }

    
    public SgCaracteristicaHabitacion create(SgCaracteristica caracteristica, SgStaffHabitacion habitacion, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaHabitacionImpl.create()");

	SgCaracteristicaHabitacion caracteristicaHabitacion = null;

	if (caracteristica != null && habitacion != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaHabitacion = new SgCaracteristicaHabitacion();
	    caracteristicaHabitacion.setSgCaracteristica(caracteristica);
	    caracteristicaHabitacion.setSgStaffHabitacion(habitacion);
	    caracteristicaHabitacion.setCantidad((cantidad != null && cantidad > 0) ? cantidad : 1);
	    caracteristicaHabitacion.setGenero(new Usuario(idUsuario));
	    caracteristicaHabitacion.setFechaGenero(new Date());
	    caracteristicaHabitacion.setHoraGenero(new Date());
	    caracteristicaHabitacion.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaHabitacion);
	} else {
	    throw new SIAException(SgCaracteristicaHabitacionImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Habitación",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " habitacion: " + (habitacion != null ? habitacion.getId() : null)
		    + " cantidad: " + cantidad
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaHabitacion CREATED SUCCESSFULLY");
	return caracteristicaHabitacion;
    }

    
    public SgCaracteristicaHabitacion update(SgCaracteristicaHabitacion caracteristicaHabitacion, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaHabitacionImpl.update()");

	if (caracteristicaHabitacion != null && idUsuario != null && !idUsuario.equals("")) {
	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaHabitacion.getId()).toString().equals(caracteristicaHabitacion.toString())) {
		super.edit(caracteristicaHabitacion);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaHabitacionImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-Habitación",
		    ("Parámetros:"
		    + " caracteristicaHabitacion: " + (caracteristicaHabitacion != null ? caracteristicaHabitacion.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaHabitacion UPDATED SUCCESSFULLY");
	return caracteristicaHabitacion;
    }

    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SgCaracteristicaHabitacion delete(int idCaracteristicaHabitacion, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaHabitacionImpl.delete()");
	SgCaracteristicaHabitacion sgCaracteristicaHabitacion = find(idCaracteristicaHabitacion);
	if (sgCaracteristicaHabitacion != null && idUsuario != null && !idUsuario.equals("")) {
	    sgCaracteristicaHabitacion.setEliminado(Constantes.ELIMINADO);

	    super.edit(sgCaracteristicaHabitacion);
	} else {
	    throw new SIAException(SgCaracteristicaHabitacionImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-Habitación",
		    ("Parámetros:"
		    + " caracteristicaHabitacion: " + (sgCaracteristicaHabitacion != null ? sgCaracteristicaHabitacion.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaHabitacion DELETED SUCCESSFULLY");
	return sgCaracteristicaHabitacion;
    }

    
    public SgCaracteristicaHabitacion getCaracteristicaHabitacionPrincipalByHabitacion(SgStaffHabitacion habitacion) throws SIAException, NonUniqueResultException, NoResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaHabitacionImpl.findCaracteristicaHabitacionPrincipalByHabitacion()");

	if (habitacion != null) {
	    try {
		return (SgCaracteristicaHabitacion) em.createQuery("SELECT c FROM SgCaracteristicaHabitacion c WHERE c.eliminado = :eliminado AND c.sgStaffHabitacion.id = :idHabitacion AND c.sgCaracteristica.principal = :principal AND c.sgCaracteristica.eliminado = :eliminado")
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.setParameter("principal", Constantes.BOOLEAN_TRUE)
			.setParameter("idHabitacion", habitacion.getId())
			.getSingleResult();
	    } catch (NoResultException nre) {
		throw new SIAException(SgCaracteristicaHabitacionImpl.class.getName(), "getCaracteristicaHabitacionPrincipalByHabitacion", "No se encontró la Característica Principal de la Habitación: " + habitacion.getNombre(), ("Habitación: " + habitacion.getId()));
	    }
	} else {
	    throw new SIAException(SgCaracteristicaHabitacionImpl.class.getName(), "getCaracteristicaHabitacionPrincipalByHabitacion()",
		    "Faltan parámetros para poder buscar la relación principal Característica-Habitación",
		    ("Parámetros:"
		    + " habitacion: " + (habitacion != null ? habitacion.getId() : null)));
	}
    }

    
    public List<CaracteristicaVo> getAllCaracteristicaHabitacionByHabitacionList(int habitacion) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaHabitacionImpl.getAllCaracteristicaHabitacionByHabitacion()");
        //                   0     1             2                    3            4              5          6     
	String q = "select c.ID, s.ID, c.NOMBRE as CARACTERISTICA,c.PRINCIPAL, ch.CANTIDAD, t.ID as tipo, t.NOMBRE from SG_CARACTERISTICA_HABITACION ch"
		+ "	    inner join SG_CARACTERISTICA c on ch.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    inner join SG_STAFF_HABITACION s on ch.SG_STAFF_HABITACION = s.ID"
		+ "	    where s.id = ?"
		+ "	    and ch.ELIMINADO = 'False'";
	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, habitacion).getResultList();
	for (Object[] lo1 : lo) {
	    CaracteristicaVo caracteristicaVo = new CaracteristicaVo();
	    caracteristicaVo.setId((Integer) lo1[0]);
	    caracteristicaVo.setNombre((String) lo1[2]);
	    caracteristicaVo.setPrincipal((Boolean) lo1[3]);
	    caracteristicaVo.setCantidad((Integer) lo1[4]);
	    caracteristicaVo.setIdTipo((Integer) lo1[5]);
	    caracteristicaVo.setTipo((String) lo1[6]);
	    caracteristicas.add(caracteristicaVo);
	}
	return caracteristicas;

    }

    
    public Map<String, CaracteristicaVo> getAllCaracteristicaHabitacionByHabitacionMap(SgStaffHabitacion habitacion) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaHabitacionImpl.getAllCaracteristicaHabitacionByHabitacionMap()");

	List<CaracteristicaVo> caracteristicasList = getAllCaracteristicaHabitacionByHabitacionList(habitacion.getId());
	Map<String, CaracteristicaVo> caracteristicasMap = null;

	if (caracteristicasList != null) {
	    caracteristicasMap = new TreeMap<String, CaracteristicaVo>();
	    for (CaracteristicaVo caracteristica : caracteristicasList) {
		caracteristicasMap.put(caracteristica.getNombre(), caracteristica);
	    }
	}
	return caracteristicasMap;
    }
}
