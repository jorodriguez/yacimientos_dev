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
import sia.modelo.SgCaracteristicaSalaJunta;
import sia.modelo.SgSalaJunta;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgCaracteristicaSalaJuntaImpl extends AbstractFacade<SgCaracteristicaSalaJunta> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaSalaJuntaImpl() {
	super(SgCaracteristicaSalaJunta.class);
    }

    
    public SgCaracteristicaSalaJunta findByCaracteristicaAndSalaJunta(SgCaracteristica caracteristica, int salaJunta) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSalaJuntaImpl.findByCaracteristicaAndSalaJunta()");

	if (caracteristica != null) {
	    try {
		return (SgCaracteristicaSalaJunta) em.createQuery("SELECT c FROM SgCaracteristicaSalaJunta c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgSalaJunta.id = :idSalaJunta AND c.eliminado = :eliminado")
			.setParameter("idCaracteristica", caracteristica.getId())
			.setParameter("idSalaJunta", salaJunta)
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaSalaJuntaImpl.class.getName(), "findByCaracteristicaAndSalaJunta()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " salaJunta: " + salaJunta));
	}
    }

    
    public SgCaracteristicaSalaJunta create(SgCaracteristica caracteristica, int salaJunta, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSalaJuntaImpl.create()");

	SgCaracteristicaSalaJunta caracteristicaSalaJunta = null;

	if (caracteristica != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaSalaJunta = new SgCaracteristicaSalaJunta();
	    caracteristicaSalaJunta.setSgCaracteristica(caracteristica);
	    caracteristicaSalaJunta.setSgSalaJunta(new SgSalaJunta(salaJunta));
	    caracteristicaSalaJunta.setCantidad((cantidad != null && cantidad > 0) ? cantidad : 1);
	    caracteristicaSalaJunta.setGenero(new Usuario(idUsuario));
	    caracteristicaSalaJunta.setFechaGenero(new Date());
	    caracteristicaSalaJunta.setHoraGenero(new Date());
	    caracteristicaSalaJunta.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaSalaJunta);
	} else {
	    throw new SIAException(SgCaracteristicaSalaJuntaImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Sala Junta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " salaJunta: " + salaJunta)
		    + " cantidad: " + cantidad
		    + " idUsuario: " + idUsuario);
	}

	UtilLog4j.log.info(this, "CaracteristicaSalaJunta CREATED SUCCESSFULLY");
	return caracteristicaSalaJunta;
    }

    
    public SgCaracteristicaSalaJunta update(SgCaracteristicaSalaJunta caracteristicaSalaJunta, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSalaJuntaImpl.update()");

	if (caracteristicaSalaJunta != null && idUsuario != null && !idUsuario.equals("")) {
	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaSalaJunta.getId()).toString().equals(caracteristicaSalaJunta.toString())) {
		super.edit(caracteristicaSalaJunta);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaSalaJuntaImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-SalaJunta",
		    ("Parámetros:"
		    + " caracteristicaSalaJunta: " + (caracteristicaSalaJunta != null ? caracteristicaSalaJunta.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaSalaJunta UPDATED SUCCESSFULLY");
	return caracteristicaSalaJunta;
    }

    
    public SgCaracteristicaSalaJunta delete(CaracteristicaVo caracteristicaVo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSalaJuntaImpl.delete()");

	if (caracteristicaVo != null && idUsuario != null && !idUsuario.equals("")) {
	    SgCaracteristicaSalaJunta caracteristicaSalaJunta = find(caracteristicaVo.getIdRelacion());
	    caracteristicaSalaJunta.setEliminado(Constantes.ELIMINADO);
	    super.edit(caracteristicaSalaJunta);
	    return caracteristicaSalaJunta;
	} else {
	    throw new SIAException(SgCaracteristicaSalaJuntaImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-SalaJunta",
		    ("Parámetros: "
		    + " caracteristicaSalaJunta: " + (caracteristicaVo != null ? caracteristicaVo.getId() : null)
		    + " idUsuario: " + idUsuario));
	}
    }

    
    public SgCaracteristicaSalaJunta getCaracteristicaSalaJuntaPrincipalBySalaJunta(SgSalaJunta salaJunta) throws SIAException, NonUniqueResultException, NoResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSalaJuntaImpl.getCaracteristicaSalaJuntaPrincipalBySalaJunta()");

	if (salaJunta != null) {
	    try {
		return (SgCaracteristicaSalaJunta) em.createQuery("SELECT c FROM SgCaracteristicaSalaJunta c WHERE c.eliminado = :eliminado AND c.sgSalaJunta.id = :idSalaJunta AND c.sgCaracteristica.principal = :principal ORDER BY c.sgCaracteristica.nombre ASC")
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.setParameter("principal", Constantes.BOOLEAN_TRUE)
			.setParameter("idSalaJunta", salaJunta.getId())
			.getSingleResult();
	    } catch (NoResultException nre) {
		throw new SIAException(SgCaracteristicaSalaJuntaImpl.class.getName(),
			"getCaracteristicaSalaJuntaPrincipalBySalaJunta", "No se encontró la Característica Principal de la Sala de Juntas: " + salaJunta.getNombre(),
			("Sala Junta: " + salaJunta.getId()));
	    }
	} else {
	    throw new SIAException(SgCaracteristicaSalaJuntaImpl.class.getName(), "getCaracteristicaSalaJuntaPrincipalBySalaJunta()",
		    "Faltan parámetros para poder buscar la relación principal Característica-SalaJunta",
		    ("Parámetros: "
		    + "salaJunta: " + (salaJunta != null ? salaJunta.getId() : null)));
	}
    }

    
    public List<CaracteristicaVo> getAllCaracteristicaSalaJuntaBySalaJuntaList(int salaJunta) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSalaJuntaImpl.getAllCaracteristicaSalaJuntaBySalaJuntaList");
	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();

	String q = "select c.ID, c.NOMBRE, c.PRINCIPAL, cs.CANTIDAD, t.ID, t.NOMBRE, cs.id from SG_CARACTERISTICA_SALA_JUNTA cs"
		+ "	    inner join SG_CARACTERISTICA c on cs.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    inner join  SG_SALA_JUNTA s on cs.SG_SALA_JUNTA = s.ID"
		+ "	    where s.id = ?"
		+ "	    and cs.ELIMINADO = 'False'";
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, salaJunta).getResultList();
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

    
    public Map<String, CaracteristicaVo> getAllCaracteristicaSalaJuntaBySalaJuntaMap(int salaJunta) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaSalaJuntaImpl.getAllCaracteristicaSalaJuntaBySalaJuntaMap()");

	List<CaracteristicaVo> caracteristicasList = getAllCaracteristicaSalaJuntaBySalaJuntaList(salaJunta);
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
