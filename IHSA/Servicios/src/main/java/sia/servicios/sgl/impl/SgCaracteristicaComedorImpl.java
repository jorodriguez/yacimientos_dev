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
import sia.modelo.SgCaracteristicaComedor;
import sia.modelo.SgComedor;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgCaracteristicaComedorImpl extends AbstractFacade<SgCaracteristicaComedor>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaComedorImpl() {
	super(SgCaracteristicaComedor.class);
    }

    
    public SgCaracteristicaComedor findByCaracteristicaAndComedor(SgCaracteristica caracteristica, int comedor) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaComedorImpl.findByCaracteristicaAndComedor()");

	if (caracteristica != null) {
	    try {
		return (SgCaracteristicaComedor) em.createQuery("SELECT c FROM SgCaracteristicaComedor c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgComedor.id = :idComedor AND c.eliminado = :eliminado")
			.setParameter("idCaracteristica", caracteristica.getId())
			.setParameter("idComedor", comedor)
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaComedorImpl.class.getName(), "findByCaracteristicaAndComedor()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " comedor: " + comedor));
	}
    }

    
    public SgCaracteristicaComedor create(SgCaracteristica caracteristica, int comedor, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaComedorImpl.create()");

	SgCaracteristicaComedor caracteristicaComedor = null;

	if (caracteristica != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaComedor = new SgCaracteristicaComedor();
	    caracteristicaComedor.setSgCaracteristica(caracteristica);
	    caracteristicaComedor.setSgComedor(new SgComedor(comedor));
	    caracteristicaComedor.setCantidad((cantidad != null && cantidad > 0) ? cantidad : 1);
	    caracteristicaComedor.setGenero(new Usuario(idUsuario));
	    caracteristicaComedor.setFechaGenero(new Date());
	    caracteristicaComedor.setHoraGenero(new Date());
	    caracteristicaComedor.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaComedor);
	} else {
	    throw new SIAException(SgCaracteristicaComedorImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Comedor",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " cantidad: " + cantidad
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaComedor CREATED SUCCESSFULLY");
	return caracteristicaComedor;
    }

    
    public SgCaracteristicaComedor update(SgCaracteristicaComedor caracteristicaComedor, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaComedorImpl.update()");

	if (caracteristicaComedor != null && idUsuario != null && !idUsuario.equals("")) {
	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaComedor.getId()).toString().equals(caracteristicaComedor.toString())) {
		super.edit(caracteristicaComedor);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaComedorImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-Comedor",
		    ("Parámetros:"
		    + " caracteristicaComedor: " + (caracteristicaComedor != null ? caracteristicaComedor.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaComedor UPDATED SUCCESSFULLY");
	return caracteristicaComedor;
    }

    
    public SgCaracteristicaComedor delete(CaracteristicaVo caracteristicaComedor, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaComedorImpl.delete()");

	if (caracteristicaComedor != null && idUsuario != null && !idUsuario.equals("")) {
	    SgCaracteristicaComedor caracteristicaComedor1 = find(caracteristicaComedor.getIdRelacion());
	    caracteristicaComedor1.setEliminado(Constantes.ELIMINADO);

	    super.edit(caracteristicaComedor1);
	    return caracteristicaComedor1;
	} else {
	    throw new SIAException(SgCaracteristicaComedorImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-Comedor",
		    ("Parámetros: "
		    + " caracteristicaComedor: " + (caracteristicaComedor != null ? caracteristicaComedor.getId() : null)
		    + " idUsuario: " + idUsuario));
	}
    }

    
    public SgCaracteristicaComedor getCaracteristicaComedorPrincipalByComedor(int comedor) throws SIAException, NonUniqueResultException, NoResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaComedorImpl.getCaracteristicaComedorPrincipalByComedor()");

	try {
	    return (SgCaracteristicaComedor) em.createQuery("SELECT c FROM SgCaracteristicaComedor c WHERE c.eliminado = :eliminado AND c.sgComedor.id = :idComedor AND c.sgCaracteristica.principal = :principal")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("principal", Constantes.BOOLEAN_TRUE)
		    .setParameter("idComedor", comedor)
		    .getSingleResult();
	} catch (NoResultException nre) {
	    throw new SIAException(SgCaracteristicaComedorImpl.class.getName(),
		    "getCaracteristicaComedorPrincipalByComedor", "No se encontró la Característica Principal del Comedor: " + comedor);
	}

    }

    
    public List<CaracteristicaVo> getAllCaracteristicaComedorByComedorList(int comedor) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaComedorImpl.getAllCaracteristicaComedorByComedorList");

	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();

	String q = "select c.ID, c.NOMBRE,c.PRINCIPAL, cs.CANTIDAD, t.ID, t.NOMBRE, cs.id from SG_CARACTERISTICA_COMEDOR cs"
		+ "	    inner join SG_CARACTERISTICA c on cs.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    inner join  SG_COMEDOR com on cs.SG_comedor = com.ID"
		+ "	    where com.id = ?"
		+ "	    and cs.ELIMINADO = 'False'";
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, comedor).getResultList();
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

    
    public Map<String, CaracteristicaVo> getAllCaracteristicaComedorByComedorMap(int comedor) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaComedorImpl.getAllCaracteristicaComedorByComedorMap()");

	List<CaracteristicaVo> caracteristicasList = getAllCaracteristicaComedorByComedorList(comedor);
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
