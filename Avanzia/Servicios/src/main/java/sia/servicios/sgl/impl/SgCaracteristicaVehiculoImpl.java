/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaVehiculo;
import sia.modelo.SgVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgCaracteristicaVehiculoImpl extends AbstractFacade<SgCaracteristicaVehiculo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaVehiculoImpl() {
	super(SgCaracteristicaVehiculo.class);
    }

    
    public SgCaracteristicaVehiculo findByCaracteristicaAndVehiculo(SgCaracteristica caracteristica, int vehiculo) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaVehiculoImpl.findByCaracteristicaAndVehiculo()");

	if (caracteristica != null && vehiculo > 0) {
	    try {
		return (SgCaracteristicaVehiculo) em.createQuery("SELECT c FROM SgCaracteristicaVehiculo c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgVehiculo.id = :idVehiculo AND c.eliminado = :eliminado")
			.setParameter("idCaracteristica", caracteristica.getId())
			.setParameter("idVehiculo", vehiculo)
			.setParameter("eliminado", Constantes.NO_ELIMINADO)
			.getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaVehiculoImpl.class.getName(), "findByCaracteristicaAndVehiculo()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " vehiculo: " + vehiculo));
	}
    }

    
    public SgCaracteristicaVehiculo create(int caracteristica, int vehiculo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaVehiculoImpl.create()");

	SgCaracteristicaVehiculo caracteristicaVehiculo = null;

	if (idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaVehiculo = new SgCaracteristicaVehiculo();
	    caracteristicaVehiculo.setSgCaracteristica(new SgCaracteristica(caracteristica));
	    caracteristicaVehiculo.setSgVehiculo(new SgVehiculo(vehiculo));
	    caracteristicaVehiculo.setGenero(new Usuario(idUsuario));
	    caracteristicaVehiculo.setFechaGenero(new Date());
	    caracteristicaVehiculo.setHoraGenero(new Date());
	    caracteristicaVehiculo.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaVehiculo);

	} else {
	    throw new SIAException(SgCaracteristicaVehiculoImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Vehículo",
		    ("Parámetros:"
		    + " caracteristica: " + caracteristica
		    + " vehiculo: " + vehiculo
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaVehiculo CREATED SUCCESSFULLY");
	return caracteristicaVehiculo;
    }

    
    public SgCaracteristicaVehiculo update(SgCaracteristicaVehiculo caracteristicaVehiculo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaVehiculoImpl.update()");

	if (caracteristicaVehiculo != null && idUsuario != null && !idUsuario.equals("")) {

	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaVehiculo.getId()).toString().equals(caracteristicaVehiculo.toString())) {
		caracteristicaVehiculo.setModifico(new Usuario(idUsuario));
		caracteristicaVehiculo.setFechaModifico(new Date());
		caracteristicaVehiculo.setHoraModifico(new Date());
		super.edit(caracteristicaVehiculo);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaVehiculoImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-Vehículo",
		    ("Parámetros:"
		    + " caracteristicaVehiculo: " + (caracteristicaVehiculo != null ? caracteristicaVehiculo.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaVehiculo UPDATED SUCCESSFULLY");
	return caracteristicaVehiculo;
    }

    
    public SgCaracteristicaVehiculo delete(int idCaracteristicaVehiculo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaVehiculoImpl.delete()");
	SgCaracteristicaVehiculo caracteristicaVehiculo = find(idCaracteristicaVehiculo);
	if (idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaVehiculo.setEliminado(Constantes.ELIMINADO);
	    super.edit(caracteristicaVehiculo);

	} else {
	    throw new SIAException(SgCaracteristicaVehiculoImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-Vehículo",
		    ("Parámetros: "
		    + " caracteristicaVehiculo: " + (caracteristicaVehiculo != null ? caracteristicaVehiculo.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaVehiculo DELETED SUCCESSFULLY");
	return caracteristicaVehiculo;
    }

    
    public List<CaracteristicaVo> getAllCaracteristicaVehiculoByVehiculoList(int vehiculo) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaVehiculoImpl.getAllCaracteristicaVehiculoByVehiculoList");
	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();
	String q = "select c.ID, c.NOMBRE,c.PRINCIPAL,  t.ID, t.NOMBRE, cs.id from SG_CARACTERISTICA_VEHICULO cs"
		+ "	    inner join SG_CARACTERISTICA c on cs.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    inner join  SG_VEHICULO v on cs.SG_VEHICULO = v.ID"
		+ "	    where v.ID = ?"
		+ "	    and cs.ELIMINADO = 'False'";
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, vehiculo).getResultList();
	for (Object[] lo1 : lo) {
	    CaracteristicaVo caracteristicaVo = new CaracteristicaVo();
	    caracteristicaVo.setId((Integer) lo1[0]);
	    caracteristicaVo.setNombre((String) lo1[1]);
	    caracteristicaVo.setPrincipal((Boolean) lo1[2]);
	    caracteristicaVo.setIdTipo((Integer) lo1[3]);
	    caracteristicaVo.setTipo((String) lo1[4]);
	    caracteristicaVo.setIdRelacion((Integer) lo1[5]);
	    caracteristicas.add(caracteristicaVo);
	}
	return caracteristicas;
    }
}
