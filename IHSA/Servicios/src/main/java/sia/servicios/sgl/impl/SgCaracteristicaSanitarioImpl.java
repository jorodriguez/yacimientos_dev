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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaSanitario;
import sia.modelo.SgSanitario;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgCaracteristicaSanitarioImpl extends AbstractFacade<SgCaracteristicaSanitario>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaSanitarioImpl() {
	super(SgCaracteristicaSanitario.class);
    }

    
    public SgCaracteristicaSanitario findByCaracteristicaAndSanitario(SgCaracteristica caracteristica, int sanitario) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSanitarioImpl.findByCaracteristicaAndSanitario()");

	if (caracteristica != null) {
	    try {
		return (SgCaracteristicaSanitario) em.createQuery("SELECT c FROM SgCaracteristicaSanitario c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgSanitario.id = :idSanitario AND c.eliminado = :eliminado").setParameter("idCaracteristica", caracteristica.getId()).setParameter("idSanitario", sanitario).setParameter("eliminado", Constantes.NO_ELIMINADO).getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaSanitarioImpl.class.getName(), "findByCaracteristicaAndCocina()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " sanitario: " + sanitario));
	}
    }

    
    public SgCaracteristicaSanitario create(SgCaracteristica caracteristica, int sanitario, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSanitarioImpl.create()");

	SgCaracteristicaSanitario caracteristicaSanitario = null;

	if (caracteristica != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaSanitario = new SgCaracteristicaSanitario();
	    caracteristicaSanitario.setSgCaracteristica(caracteristica);
	    caracteristicaSanitario.setSgSanitario(new SgSanitario(sanitario));
	    caracteristicaSanitario.setCantidad((cantidad != null && cantidad > 0) ? cantidad : 1);
	    caracteristicaSanitario.setGenero(new Usuario(idUsuario));
	    caracteristicaSanitario.setFechaGenero(new Date());
	    caracteristicaSanitario.setHoraGenero(new Date());
	    caracteristicaSanitario.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaSanitario);
	} else {
	    throw new SIAException(SgCaracteristicaSanitarioImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Sanitario",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " cantidad: " + cantidad
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaSanitario CREATED SUCCESSFULLY");
	return caracteristicaSanitario;
    }

    
    public SgCaracteristicaSanitario update(SgCaracteristicaSanitario caracteristicaSanitario, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSanitarioImpl.update()");

	if (caracteristicaSanitario != null && idUsuario != null && !idUsuario.equals("")) {
	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaSanitario.getId()).toString().equals(caracteristicaSanitario.toString())) {
		super.edit(caracteristicaSanitario);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaSanitarioImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-Sanitario",
		    ("Parámetros:"
		    + " caracteristicaSanitario: " + (caracteristicaSanitario != null ? caracteristicaSanitario.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaSanitario UPDATED SUCCESSFULLY");
	return caracteristicaSanitario;
    }

    
    public SgCaracteristicaSanitario delete(CaracteristicaVo caracteristicaVo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSanitarioImpl.delete()");

	if (caracteristicaVo != null && idUsuario != null && !idUsuario.equals("")) {
	    System.out.println("ID : : :" + caracteristicaVo.getIdRelacion());
	    SgCaracteristicaSanitario caracteristicaSanitario = find(caracteristicaVo.getIdRelacion());
	    caracteristicaSanitario.setEliminado(Constantes.ELIMINADO);

	    edit(caracteristicaSanitario);
	    return caracteristicaSanitario;
	} else {
	    throw new SIAException(SgCaracteristicaSanitarioImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-Sanitario",
		    ("Parámetros: "
		    + " caracteristicaSanitario: " + (caracteristicaVo != null ? caracteristicaVo.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

    }

    
    public SgCaracteristicaSanitario getCaracteristicaSanitarioPrincipalBySanitario(SgSanitario sanitario) throws SIAException, NonUniqueResultException, NoResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSanitarioImpl.getCaracteristicaSanitarioPrincipalBySanitario()");

	if (sanitario != null) {
	    try {
		return (SgCaracteristicaSanitario) em.createQuery("SELECT c FROM SgCaracteristicaSanitario c WHERE c.eliminado = :eliminado AND c.sgSanitario.id = :idSanitario AND c.sgCaracteristica.principal = :principal").setParameter("eliminado", Constantes.NO_ELIMINADO).setParameter("principal", Constantes.BOOLEAN_TRUE).setParameter("idSanitario", sanitario.getId()).getSingleResult();
	    } catch (NoResultException nre) {
		throw new SIAException(SgCaracteristicaSanitarioImpl.class.getName(),
			"getCaracteristicaSanitarioPrincipalBySanitario", "No se encontró la Característica Principal del Sanitario: " + sanitario.getNombre(),
			("Sala Junta: " + sanitario.getId()));
	    }
	} else {
	    throw new SIAException(SgCaracteristicaSanitarioImpl.class.getName(), "getCaracteristicaSanitarioPrincipalBySanitario()",
		    "Faltan parámetros para poder buscar la relación principal Característica-Sanitario",
		    ("Parámetros: "
		    + "sanitario: " + (sanitario != null ? sanitario.getId() : null)));
	}
    }

    
    public List<CaracteristicaVo> getAllCaracteristicaSanitarioBySanitarioList(int sanitario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSanitarioImpl.getAllCaracteristicaSanitarioBySanitarioList");

	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();

	String q = "select c.ID, c.NOMBRE,c.PRINCIPAL, cs.CANTIDAD, t.ID, t.NOMBRE, cs.id from SG_CARACTERISTICA_SANITARIO cs"
		+ "	    inner join SG_CARACTERISTICA c on cs.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    inner join  sg_sanitario san on cs.SG_sanitario = san.ID"
		+ "	    where san.id = ?"
		+ "	    and cs.ELIMINADO = 'False'";
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, sanitario).getResultList();
	for (Object[] lo1 : lo) {
	    CaracteristicaVo caracteristicaVo = new CaracteristicaVo();
	    caracteristicaVo.setId((Integer) lo1[0]);
	    caracteristicaVo.setNombre((String) lo1[1]);
	    caracteristicaVo.setPrincipal((Boolean) lo1[2]);
	    caracteristicaVo.setCantidad((Integer) lo1[3]);
	    caracteristicaVo.setIdTipo((Integer) lo1[4]);
	    caracteristicaVo.setTipo((String) lo1[5]);
	    caracteristicaVo.setIdRelacion((Integer) lo1[6]);
	    caracteristicas.add(caracteristicaVo);
	}
	return caracteristicas;
    }

    
    public Map<String, CaracteristicaVo> getAllCaracteristicaSanitarioBySanitarioMap(int sanitario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSanitarioImpl.getAllCaracteristicaSanitarioBySanitarioMap()");

	List<CaracteristicaVo> caracteristicasList = getAllCaracteristicaSanitarioBySanitarioList(sanitario);
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
