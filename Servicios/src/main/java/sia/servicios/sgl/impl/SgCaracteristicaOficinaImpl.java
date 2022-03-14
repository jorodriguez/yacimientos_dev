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
import sia.modelo.SgCaracteristicaOficina;
import sia.modelo.SgOficina;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgCaracteristicaOficinaImpl extends AbstractFacade<SgCaracteristicaOficina> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaOficinaImpl() {
	super(SgCaracteristicaOficina.class);
    }

    
    public SgCaracteristicaOficina findByCaracteristicaAndOficina(SgCaracteristica caracteristica, int oficina) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaOficinaImpl.findByCaracteristicaAndOficina()");

	if (caracteristica != null) {
	    try {
		return (SgCaracteristicaOficina) em.createQuery("SELECT c FROM SgCaracteristicaOficina c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgOficina.id = :idOficina AND c.eliminado = :eliminado").setParameter("idCaracteristica", caracteristica.getId()).setParameter("idOficina", oficina).setParameter("eliminado", Constantes.NO_ELIMINADO).getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaOficinaImpl.class.getName(), "findByCaracteristicaAndOficina()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " oficina: " + oficina));
	}
    }

    
    public SgCaracteristicaOficina create(SgCaracteristica caracteristica, int oficina, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaOficinaImpl.create()");

	SgCaracteristicaOficina caracteristicaOficina = null;

	if (caracteristica != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaOficina = new SgCaracteristicaOficina();
	    caracteristicaOficina.setSgCaracteristica(caracteristica);
	    caracteristicaOficina.setSgOficina(new SgOficina(oficina));
	    caracteristicaOficina.setCantidad((cantidad != null && cantidad > 0) ? cantidad : 1);
	    caracteristicaOficina.setGenero(new Usuario(idUsuario));
	    caracteristicaOficina.setFechaGenero(new Date());
	    caracteristicaOficina.setHoraGenero(new Date());
	    caracteristicaOficina.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaOficina);
	} else {
	    throw new SIAException(SgCaracteristicaOficinaImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Oficina",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " oficina: " + oficina
		    + " cantidad: " + cantidad
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaOficina CREATED SUCCESSFULLY");
	return caracteristicaOficina;
    }

    
    public SgCaracteristicaOficina update(SgCaracteristicaOficina caracteristicaOficina, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaOficinaImpl.update()");

	if (caracteristicaOficina != null && idUsuario != null && !idUsuario.equals("")) {
	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaOficina.getId()).toString().equals(caracteristicaOficina.toString())) {
		super.edit(caracteristicaOficina);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaOficinaImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-Oficina",
		    ("Parámetros:"
		    + " caracteristicaOficina: " + (caracteristicaOficina != null ? caracteristicaOficina.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaOficina UPDATED SUCCESSFULLY");
	return caracteristicaOficina;
    }

    
    public SgCaracteristicaOficina delete(CaracteristicaVo caracteristicaOficina, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaOficinaImpl.delete()");
	try {
	    SgCaracteristicaOficina sco = find(caracteristicaOficina.getIdRelacion());
	    sco.setEliminado(Constantes.ELIMINADO);
	    edit(sco);
	    return sco;
	} catch (Exception e) {
	    UtilLog4j.log.error(e);
	    throw new SIAException(SgCaracteristicaOficinaImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-Oficina",
		    ("Parámetros: "
		    + "caracteristicaOficina: " + (caracteristicaOficina != null ? caracteristicaOficina.getId() : null)
		    + " idUsuario: " + idUsuario));
	}
    }

    
    public SgCaracteristicaOficina getCaracteristicaOficinaPrincipalByOficina(SgOficina oficina) throws SIAException, NonUniqueResultException, NoResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaOficinaImpl.getCaracteristicaOficinaPrincipalByOficina()");

	SgCaracteristicaOficina caracteristicaOficinaPrincipal = null;

	if (oficina != null) {
	    caracteristicaOficinaPrincipal = (SgCaracteristicaOficina) em.createQuery("SELECT c FROM SgCaracteristicaOficina c WHERE c.eliminado = :estado AND c.sgOficina.id = :idOficina AND c.sgCaracteristica.principal = :principal").setParameter("estado", Constantes.NO_ELIMINADO).setParameter("principal", Constantes.BOOLEAN_TRUE).setParameter("idOficina", oficina.getId()).getSingleResult();
	} else {
	    throw new SIAException(SgCaracteristicaOficinaImpl.class.getName(), "getCaracteristicaOficinaPrincipalByOficina()",
		    "Faltan parámetros para poder buscar la relación principal Característica-Oficina",
		    ("Parámetros: "
		    + "oficina: " + (oficina != null ? oficina.getId() : null)));
	}

	return caracteristicaOficinaPrincipal;
    }

    
    public List<CaracteristicaVo> getAllCaracteristicaOficinaByOficinaList(int oficina) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaOficinaImpl.getAllCaracteristicaOficinaByOficinaList");

	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();

	String q = "select c.ID, c.NOMBRE,c.PRINCIPAL, cs.CANTIDAD, t.ID, t.NOMBRE, cs.id from SG_CARACTERISTICA_OFICINA cs"
		+ "	    inner join SG_CARACTERISTICA c on cs.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    where cs.SG_OFICINA = ?"
		+ "	    and cs.ELIMINADO = 'False'";
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, oficina).getResultList();
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

    
    public Map<String, CaracteristicaVo> getAllCaracteristicaOficinaByOficinaMap(int oficina) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaOficinaImpl.getAllCaracteristicaOficinaByOficinaMap()");

	List<CaracteristicaVo> caracteristicasList = getAllCaracteristicaOficinaByOficinaList(oficina);
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

    
    public void asignarCaracteristicaOficina(List<SgCaracteristica> listaFilasSeleccionadas, int idOficina, Usuario usuario, boolean BOOLEAN_FALSE) {
	for (SgCaracteristica sgCaracteristica : listaFilasSeleccionadas) {
	    SgCaracteristicaOficina sgCaracteristicaOficina = new SgCaracteristicaOficina();
	    sgCaracteristicaOficina.setSgCaracteristica(sgCaracteristica);
	    sgCaracteristicaOficina.setSgOficina(new SgOficina(idOficina));
	    sgCaracteristicaOficina.setGenero(usuario);
	    sgCaracteristicaOficina.setFechaGenero(new Date());
	    sgCaracteristicaOficina.setHoraGenero(new Date());
	    sgCaracteristicaOficina.setEliminado(BOOLEAN_FALSE);
	    create(sgCaracteristicaOficina);
	}
    }
}
