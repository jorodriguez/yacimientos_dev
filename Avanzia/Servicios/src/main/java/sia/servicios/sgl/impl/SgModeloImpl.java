/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgMarca;
import sia.modelo.SgModelo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgModeloImpl extends AbstractFacade<SgModelo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    SgTipoEspecificoImpl tipoEspecificoService;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgModeloImpl() {
	super(SgModelo.class);
    }

    
    public int save(String idUsuario, String nombreModelo, int marca, int tipoEspecifico) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgModeloImpl.save()");
	SgModelo sgModelo = findByName(nombreModelo, Boolean.FALSE);
	if (sgModelo == null) {
	    SgModelo modelo = new SgModelo();
	    modelo.setNombre(nombreModelo);
	    modelo.setSgMarca(new SgMarca(marca));
	    modelo.setSgTipoEspecifico(new SgTipoEspecifico(tipoEspecifico));
	    modelo.setFechaGenero(new Date());
	    modelo.setHoraGenero(new Date());
	    modelo.setEliminado(Constantes.NO_ELIMINADO);
	    modelo.setGenero(new Usuario(idUsuario));
	    create(modelo);
	    //Poner usado el Tipo Específico
	    tipoEspecificoService.ponerUsadoTipoEspecifico(modelo.getSgTipoEspecifico().getId(), new Usuario(idUsuario));
	    return modelo.getId();
	} else {
	    return sgModelo.getId();
	}

    }

    
    public void update(SgModelo modelo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgModeloImpl.update()");

	if (modelo != null && idUsuario != null && !idUsuario.equals("")) {

	    boolean existeModelo = false;

	    List<SgModelo> modelosByMarcaAndTipoEspecifico = findAll(modelo.getSgTipoEspecifico(), modelo.getSgMarca(), null, null, false);

	    if (modelosByMarcaAndTipoEspecifico != null) {
		for (SgModelo m : modelosByMarcaAndTipoEspecifico) {
		    if (m.getNombre().equals(modelo.getNombre()) && (m.getId().intValue() != modelo.getId().intValue())) { //Si existe otro Modelo con el mismo nombre
			existeModelo = true;
			UtilLog4j.log.info(this, "El Modelo ya existe");
			break;
		    }
		}
	    }

	    if (!existeModelo) {
		modelo.setModifico(new Usuario(idUsuario));
		modelo.setFechaModifico(new Date());
		modelo.setHoraModifico(new Date());

		super.edit(modelo);
	    } else {
		throw new SIAException(SgModeloImpl.class.getName(), "save()", "Ya existe el Modelo: " + modelo.getNombre());
	    }
	} else {
	    throw new SIAException(SgModeloImpl.class.getName(), "update()",
		    "Faltan parámetros para poder actualizar el Modelo",
		    ("Parámetros: modelo: " + (modelo != null ? modelo : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "Modelo UPDATED SUCCESSFULLY");
    }

    
    public void delete(SgModelo modelo, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgModeloImpl.delete()");

	if (modelo != null && idUsuario != null && !idUsuario.equals("")) {

	    Long cont = 0l;

	    cont += (Long) em.createQuery("SELECT COUNT(v) FROM SgVehiculo v WHERE v.eliminado = :eliminado AND v.sgModelo.id = :idModelo")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idModelo", modelo.getId())
		    .getSingleResult();

	    cont += (Long) em.createQuery("SELECT COUNT(v) FROM SgAccesorio v WHERE v.eliminado = :eliminado AND v.sgModelo.id = :idModelo")
		    .setParameter("eliminado", Constantes.NO_ELIMINADO)
		    .setParameter("idModelo", modelo.getId())
		    .getSingleResult();

	    if (cont == 0) {		
		modelo.setEliminado(Constantes.ELIMINADO);
		super.edit(modelo);
	    } else {
		UtilLog4j.log.info(this, "El Modelo ya está siendo usado en: " + cont + " lugares");
		throw new SIAException(SgModeloImpl.class.getName(), " delete()", " Este Modelo no se puede eliminar debido a que ya está siendo utilizado.");
	    }
	} else {
	    throw new SIAException(SgModeloImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar el Modelo",
		    ("Parámetros: modelo: " + (modelo != null ? modelo : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "Modelo DELETED SUCCESSFULLY");
    }

    
    public SgModelo findByName(String nombre, Boolean eliminado) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgModeloImpl.findByName()");

	SgModelo modelo = null;

	if (nombre != null && !nombre.equals("")) {
	    try {
		//Armando query
		String query = "SELECT m FROM SgModelo m WHERE m.nombre = :nombre";

		if (eliminado != null) {
		    query += " AND m.eliminado = :eliminado";
		}

		Query q = em.createQuery(query);

		//Asignando parámetros
		q.setParameter("nombre", nombre);
		if (eliminado != null) {
		    q.setParameter("eliminado", (eliminado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE));
		}

		modelo = (SgModelo) q.getSingleResult();

		return modelo;
	    } catch (NoResultException nre) {
		UtilLog4j.log.info(this, "No se encontró el Modelo con nombre: " + nombre);
		return modelo;
	    }
	} else {
	    throw new SIAException(SgModeloImpl.class.getName(), "findByName()",
		    "Falta el parámetro \"nombre\"",
		    ("Parámetros: nombre: " + nombre
		    + " eliminado: " + eliminado));
	}
    }

    
    public List<SgModelo> findAll(SgTipoEspecifico tipoEspecifico, SgMarca marca, String orderByField, String orderByOrder, boolean eliminado) throws Exception {
	UtilLog4j.log.info(this, "SgModeloImpl.findAll()");

	List<SgModelo> modelos = null;

	//Armando query
	String query = "SELECT m FROM SgModelo m WHERE m.eliminado = :eliminado";

	if (tipoEspecifico != null) {
	    query += " AND m.sgTipoEspecifico.id = :idTipoEspecifico";
	}
	if (marca != null) {
	    query += " AND m.sgMarca.id = :idMarca";
	}

	if (orderByField != null && !orderByField.equals("") && orderByOrder != null && !orderByOrder.equals("")) {
	    query += " ORDER BY m." + orderByField + " " + orderByOrder;
	}

	Query q = em.createQuery(query);

	//Asignando parámetros
	q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
	if (tipoEspecifico != null) {
	    q.setParameter("idTipoEspecifico", tipoEspecifico.getId());
	}
	if (marca != null) {
	    q.setParameter("idMarca", marca.getId());
	}

	modelos = q.getResultList();

	UtilLog4j.log.info(this, "Se encontraron " + (modelos != null ? modelos.size() : 0) + " modelos");

	return modelos;
    }

    
    public List<Vo> traerModeloPorTipo(int tipoEspecifico, int marca) {
	clearQuery();
	List<Vo> lista = null;
	try {
	    query.append("select mod.id, mod.nombre from SG_MODELO mod where mod.SG_TIPO_ESPECIFICO = ").append(tipoEspecifico);
	    query.append("  and mod.sg_marca = ").append(marca);
	    query.append("  and mod.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append("  order by mod.nombre asc");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		lista = new ArrayList<Vo>();
		for (Object[] obj : lo) {
		    Vo vo = new Vo();
		    vo.setId((Integer) obj[0]);
		    vo.setNombre((String) obj[1]);
		    lista.add(vo);
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
	return lista;

    }
}
