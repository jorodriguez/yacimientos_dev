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
import sia.modelo.SgCaracteristicaCocina;
import sia.modelo.SgCocina;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgCaracteristicaCocinaImpl extends AbstractFacade<SgCaracteristicaCocina> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaCocinaImpl() {
	super(SgCaracteristicaCocina.class);
    }

    
    public SgCaracteristicaCocina findByCaracteristicaAndCocina(SgCaracteristica caracteristica, SgCocina cocina) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaCocinaImpl.findByCaracteristicaAndCocina()");

	if (caracteristica != null && cocina != null) {
	    try {
		return (SgCaracteristicaCocina) em.createQuery("SELECT c FROM SgCaracteristicaCocina c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgCocina.id = :idCocina AND c.eliminado = :eliminado")
			.setParameter("idCaracteristica", caracteristica.getId())
			.setParameter("idCocina", cocina.getId())
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaCocinaImpl.class.getName(), "findByCaracteristicaAndCocina()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " cocina: " + (cocina != null ? cocina.getId() : null)));
	}
    }

    
    public SgCaracteristicaCocina create(SgCaracteristica caracteristica, SgCocina cocina, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaCocinaImpl.create()");

	SgCaracteristicaCocina caracteristicaCocina = null;

	if (caracteristica != null && cocina != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaCocina = new SgCaracteristicaCocina();
	    caracteristicaCocina.setSgCaracteristica(caracteristica);
	    caracteristicaCocina.setSgCocina(cocina);
	    caracteristicaCocina.setCantidad((cantidad != null && cantidad > 0) ? cantidad : 1);
	    caracteristicaCocina.setGenero(new Usuario(idUsuario));
	    caracteristicaCocina.setFechaGenero(new Date());
	    caracteristicaCocina.setHoraGenero(new Date());
	    caracteristicaCocina.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaCocina);
	} else {
	    throw new SIAException(SgCaracteristicaCocinaImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Cocina",
		    ("Parámetros: "
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " cocina: " + (cocina != null ? cocina.getId() : null)
		    + " cantidad: " + cantidad
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaCocina CREATED SUCCESSFULLY");
	return caracteristicaCocina;
    }

    
    public SgCaracteristicaCocina update(SgCaracteristicaCocina caracteristicaCocina, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaCocinaImpl.update()");

	if (caracteristicaCocina != null && idUsuario != null && !idUsuario.equals("")) {
	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaCocina.getId()).toString().equals(caracteristicaCocina.toString())) {
		super.edit(caracteristicaCocina);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaCocinaImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-Cocina",
		    ("Parámetros:"
		    + " caracteristicaCocina: " + (caracteristicaCocina != null ? caracteristicaCocina.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaCocina UPDATED SUCCESSFULLY");
	return caracteristicaCocina;
    }

    
    public SgCaracteristicaCocina delete(int idCaracteristicaCocina, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaCocinaImpl.delete()");
	SgCaracteristicaCocina caracteristicaCocina = find(idCaracteristicaCocina);
	if (caracteristicaCocina != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaCocina.setEliminado(Constantes.ELIMINADO);

	    super.edit(caracteristicaCocina);
	} else {
	    throw new SIAException(SgCaracteristicaCocinaImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-Cocina",
		    ("Parámetros: caracteristica: "
		    + " caracteristicaCocina: " + (caracteristicaCocina != null ? caracteristicaCocina.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaCocina DELETED SUCCESSFULLY");
	return caracteristicaCocina;
    }

    
    public SgCaracteristicaCocina getCaracteristicaCocinaPrincipalByCocina(SgCocina cocina) throws SIAException, NonUniqueResultException, NoResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaCocinaImpl.getCaracteristicaCocinaPrincipalByCocina()");

	if (cocina != null) {
	    try {
		return (SgCaracteristicaCocina) em.createQuery("SELECT c FROM SgCaracteristicaCocina c WHERE c.eliminado = :eliminado AND c.sgCocina.id = :idCocina AND c.sgCaracteristica.principal = :principal")
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.setParameter("principal", Constantes.BOOLEAN_TRUE)
			.setParameter("idCocina", cocina.getId())
			.getSingleResult();
	    } catch (NoResultException nre) {
		throw new SIAException(SgCaracteristicaCocinaImpl.class.getName(),
			"getCaracteristicaCocinaPrincipalByCocina", "No se encontró la Característica Principal de la Cocina: " + cocina.getNombre(),
			("Cocina: " + cocina.getId()));
	    }
	} else {
	    throw new SIAException(SgCaracteristicaCocinaImpl.class.getName(), "getCaracteristicaCocinaPrincipalByCocina()",
		    "Faltan parámetros para poder buscar la relación principal Característica-Cocina",
		    ("Parámetros: "
		    + " cocina: " + (cocina != null ? cocina.getId() : null)));
	}
    }

    
    public List<CaracteristicaVo> getAllCaracteristicaCocinaByCocinaList(int cocina) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaCocinaImpl.getAllCaracteristicaCocinaByCocinaList()");
	String q = "select c.ID, c.NOMBRE as CARACTERISTICA, c.PRINCIPAL, ch.CANTIDAD, t.ID as tipo, t.NOMBRE from SG_CARACTERISTICA_COCINA ch"
		+ "	    inner join SG_CARACTERISTICA c on ch.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    inner join SG_COCINA co on ch.SG_COCINA = co.ID"
		+ "	    where co.id = ?"
		+ "	    and ch.ELIMINADO = 'False'";
	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, cocina).getResultList();
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

    
    public Map<String, CaracteristicaVo> getAllCaracteristicaCocinaByCocinaMap(int cocina) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaCocinaImpl.getAllCaracteristicaCocinaByCocinaMap()");

	List<CaracteristicaVo> caracteristicasList = getAllCaracteristicaCocinaByCocinaList(cocina);
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
