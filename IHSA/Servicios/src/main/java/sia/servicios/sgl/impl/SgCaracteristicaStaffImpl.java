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
import sia.modelo.SgCaracteristicaStaff;
import sia.modelo.SgStaff;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgCaracteristicaStaffImpl extends AbstractFacade<SgCaracteristicaStaff>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgCaracteristicaStaffImpl() {
	super(SgCaracteristicaStaff.class);
    }

    
    public SgCaracteristicaStaff findByCaracteristicaAndStaff(SgCaracteristica caracteristica, SgStaff staff) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaStaffImpl.findByCaracteristicaAndStaff()");

	if (caracteristica != null && staff != null) {
	    try {
		return (SgCaracteristicaStaff) em.createQuery("SELECT c FROM SgCaracteristicaStaff c WHERE c.sgCaracteristica.id = :idCaracteristica AND c.sgStaff.id = :idStaff AND c.eliminado = :eliminado").setParameter("idCaracteristica", caracteristica.getId()).setParameter("idStaff", staff.getId()).setParameter("eliminado", Constantes.NO_ELIMINADO).getSingleResult();
	    } catch (NoResultException nre) {
		return null;
	    }
	} else {
	    throw new SIAException(SgCaracteristicaStaffImpl.class.getName(), "findByCaracteristicaAndStaff()",
		    "Faltan parámetros para poder realizar la consulta",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " staff: " + (staff != null ? staff.getId() : null)));
	}
    }

    
    public SgCaracteristicaStaff create(SgCaracteristica caracteristica, SgStaff staff, Integer cantidad, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaStaffImpl.create()");

	SgCaracteristicaStaff caracteristicaStaff = null;

	if (caracteristica != null && staff != null && idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaStaff = new SgCaracteristicaStaff();
	    caracteristicaStaff.setSgCaracteristica(caracteristica);
	    caracteristicaStaff.setSgStaff(staff);
	    caracteristicaStaff.setCantidad((cantidad != null && cantidad > 0) ? cantidad : 1);
	    caracteristicaStaff.setGenero(new Usuario(idUsuario));
	    caracteristicaStaff.setFechaGenero(new Date());
	    caracteristicaStaff.setHoraGenero(new Date());
	    caracteristicaStaff.setEliminado(Constantes.NO_ELIMINADO);

	    super.create(caracteristicaStaff);
	} else {
	    throw new SIAException(SgCaracteristicaStaffImpl.class.getName(), "create()",
		    "Faltan parámetros para poder guardar la relación Característica-Staff",
		    ("Parámetros:"
		    + " caracteristica: " + (caracteristica != null ? caracteristica.getId() : null)
		    + " staff: " + (staff != null ? staff.getId() : null)
		    + " cantidad: " + cantidad
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaStaff CREATED SUCCESSFULLY");
	return caracteristicaStaff;
    }

    
    public SgCaracteristicaStaff update(SgCaracteristicaStaff caracteristicaStaff, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaStaffImpl.update()");

	if (caracteristicaStaff != null && idUsuario != null && !idUsuario.equals("")) {
	    //Si no es lo mismo, actualizar
	    if (!super.find(caracteristicaStaff.getId()).toString().equals(caracteristicaStaff.toString())) {
		super.edit(caracteristicaStaff);
	    }
	} else {
	    throw new SIAException(SgCaracteristicaStaffImpl.class.getName(), "update()",
		    "Faltan parámetros para poder eliminar la relación Característica-Staff",
		    ("Parámetros:"
		    + " caracteristicaStaff: " + (caracteristicaStaff != null ? caracteristicaStaff.getId() : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaStaff UPDATED SUCCESSFULLY");
	return caracteristicaStaff;
    }

    
    public SgCaracteristicaStaff delete(int id, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaStaffImpl.delete()");

	SgCaracteristicaStaff caracteristicaStaff = find(id);
	if (idUsuario != null && !idUsuario.equals("")) {
	    caracteristicaStaff.setEliminado(Constantes.ELIMINADO);

	    super.edit(caracteristicaStaff);
	} else {
	    throw new SIAException(SgCaracteristicaStaffImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la relación Característica-Staff",
		    ("Parámetros: "
		    + " caracteristicaStaff: " + id
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "CaracteristicaStaff DELETED SUCCESSFULLY");
	return caracteristicaStaff;
    }

    
    public SgCaracteristicaStaff getCaracteristicaStaffPrincipalByStaff(SgStaff staff) throws SIAException, NonUniqueResultException, NoResultException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaStaffImpl.getCaracteristicaStaffPrincipalByStaff()");

	SgCaracteristicaStaff caracteristicaStaffPrincipal = null;

	if (staff != null) {
	    caracteristicaStaffPrincipal = (SgCaracteristicaStaff) em.createQuery("SELECT c FROM SgCaracteristicaStaff c WHERE c.eliminado = :estado AND c.sgStaff.id = :idStaff AND c.sgCaracteristica.principal = :principal").setParameter("estado", Constantes.NO_ELIMINADO).setParameter("principal", Constantes.BOOLEAN_TRUE).setParameter("idStaff", staff.getId()).getSingleResult();
	} else {
	    throw new SIAException(SgCaracteristicaStaffImpl.class.getName(), "getCaracteristicaStaffPrincipalByStaff()",
		    "Faltan parámetros para poder buscar la relación principal Característica-Staff",
		    ("Parámetros: "
		    + "staff: " + (staff != null ? staff.getId() : null)));
	}

	return caracteristicaStaffPrincipal;
    }

    
    public List<CaracteristicaVo> getAllCaracteristicaStaffByStaffList(int staff) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaStaffImpl.getAllCaracteristicaStaffByStaffList");
	List<CaracteristicaVo> caracteristicas = new ArrayList<CaracteristicaVo>();

	String q = "select c.ID, c.NOMBRE,c.PRINCIPAL, cs.CANTIDAD, t.ID, t.NOMBRE from SG_CARACTERISTICA_STAFF cs"
		+ "	    inner join SG_CARACTERISTICA c on cs.SG_CARACTERISTICA = c.ID"
		+ "	    inner join SG_TIPO t on c.SG_TIPO = t.ID"
		+ "	    where cs.SG_STAFF = ?"
		+ "	    and cs.ELIMINADO = 'False'";
	List<Object[]> lo = em.createNativeQuery(q).setParameter(1, staff).getResultList();
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

    
    public Map<String, CaracteristicaVo> getAllCaracteristicaStaffByStaffMap(SgStaff staff) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgCaracteristicaStaffImpl.getAllCaracteristicaStaffByStaffMap()");

	List<CaracteristicaVo> caracteristicasList = getAllCaracteristicaStaffByStaffList(staff.getId());
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
    /*
     *  public void updateRelations(List<SgCaracteristicaStaff>
     * relaciones, SgStaff staff, String idUsuario) throws SIAException,
     * Exception {
     * UtilLog4j.log.info(this,"SgCaracteristicaStaffImpl.updateRelations()");
     *
     * List<SgCaracteristicaStaff> caracteristicasExistentes =
     * getAllCaracteristicaStaffByStaffList(staff);
     *
     * for (SgCaracteristicaStaff caracteristicaStaff : relaciones) {
     * UtilLog4j.log.info(this,"relación: " + caracteristicaStaff); if
     * (caracteristicasExistentes.contains(caracteristicaStaff)) { //Si la
     * relación existe, no hacer nada
     * caracteristicasExistentes.remove(caracteristicaStaff); } else { //Si no
     * existe la relación, crearla
     * create(caracteristicaStaff.getSgCaracteristica(),
     * caracteristicaStaff.getSgStaff(), caracteristicaStaff.getCantidad(),
     * idUsuario); } }
     *
     * //Si la lista de caracteristicasExistentes contiene aún relaciones,
     * eliminarlas if (!caracteristicasExistentes.isEmpty()) { for
     * (SgCaracteristicaStaff caracteristicaStaff : caracteristicasExistentes) {
     * delete(caracteristicaStaff, idUsuario); } }
     }
     */
}
