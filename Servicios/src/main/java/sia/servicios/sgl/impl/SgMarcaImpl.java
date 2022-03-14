/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgMarca;
import sia.modelo.SgTipo;
import sia.modelo.Usuario;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgMarcaImpl extends AbstractFacade<SgMarca>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgMarcaImpl() {
	super(SgMarca.class);
    }

    
    public int save(String idUsuario, String nombreMarca) {
            int idMarca = 0;
        try {
            UtilLog4j.log.info(this, "SgMarcaImpl.save()");
            SgMarca sgMarca = findByName(nombreMarca, Boolean.FALSE);
            if (sgMarca == null) { //La Marca no existe
                SgMarca marca = new SgMarca();
                marca.setNombre(nombreMarca);
                //marca.setSgTipo(new SgTipo(12));
                marca.setFechaGenero(new Date());
                marca.setHoraGenero(new Date());
                marca.setEliminado(Constantes.NO_ELIMINADO);
                marca.setGenero(new Usuario(idUsuario));
                marca.setSgTipo(new SgTipo(1));
                super.create(marca);
                idMarca = marca.getId();
            } else {
                idMarca = sgMarca.getId();
            }
            //UtilLog4j.log.info(this, "Marca GUARDADO SATISFACTORIAMENTE");
            return idMarca;
        } catch (NonUniqueResultException ex) {
            Logger.getLogger(SgMarcaImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SgMarcaImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idMarca;
    }

    
    public void update(SgMarca marca, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgMarcaImpl.update()");

	if (marca != null && idUsuario != null && !idUsuario.equals("")) {
	    //Validar que la Marca no exista
	    SgMarca marcaExistente = findByNameAndTipo(marca.getNombre(), marca.getSgTipo(), null);

	    if (marcaExistente == null) { //La Marca no existe
		marca.setFechaGenero(new Date());
		marca.setHoraGenero(new Date());
		marca.setEliminado(Constantes.NO_ELIMINADO);
		marca.setGenero(new Usuario(idUsuario));

		super.edit(marca);
	    } else {
		if (!marcaExistente.isEliminado()) { //La marca existe y no está eliminada
		    throw new SIAException(SgMarcaImpl.class.getName(),
			    "save()",
			    ("La marca " + marca.getNombre() + " ya existe"),
			    ("Parámetros: marca: " + (marca != null ? marca : null)
			    + " idUsuario: " + idUsuario));
		} else { //La Marca existe pero está eliminada
		    marcaExistente.setNombre(marca.getNombre());
		    marcaExistente.setFechaGenero(new Date());
		    marcaExistente.setHoraGenero(new Date());
		    marcaExistente.setEliminado(Constantes.NO_ELIMINADO);
		    marcaExistente.setGenero(new Usuario(idUsuario));

		    super.edit(marca);
		}
	    }
	} else {
	    throw new SIAException(SgMarcaImpl.class.getName(), "update()",
		    "Faltan parámetros para poder actualizar la Marca",
		    ("Parámetros: marca: " + (marca != null ? marca : null)
		    + " idUsuario: " + idUsuario));
	}

	UtilLog4j.log.info(this, "Marca ACTUALIZADO SATISFACTORIAMENTE");
    }

    
    public void delete(SgMarca marca, String idUsuario) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgMarcaImpl.delete()");
	if (marca != null && idUsuario != null && !idUsuario.equals("")) {
	    if (!findByIdMarcaOcuped(marca, "SgVehiculo")
		    && !findByIdMarcaOcuped(marca, "SgAccesorio")
		    && !findByIdMarcaOcuped(marca, "SgModelo")) {
		marca.setFechaGenero(new Date());
		marca.setHoraGenero(new Date());
		marca.setEliminado(Constantes.ELIMINADO);
		marca.setGenero(new Usuario(idUsuario));
		super.edit(marca);
		UtilLog4j.log.info(this, "Marca ELIMINADO SATISFACTORIAMENTE");
	    } else {
		throw new SIAException(SgMarcaImpl.class.getName(), "delete()",
			"La Marca no puede ser eliminada porque ya está siendo utilizada");
	    }
	} else {
	    throw new SIAException(SgMarcaImpl.class.getName(), "delete()",
		    "Faltan parámetros para poder eliminar la Marca",
		    ("Parámetros: marca: " + (marca != null ? marca : null)
		    + " idUsuario: " + idUsuario));
	}

    }

    
    public SgMarca findByName(String nombre, Boolean eliminado) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgMarcaImpl.findByName()");

	SgMarca marca = null;
	if (nombre != null && !nombre.equals("")) {
	    try {
		//Armando query
		String q = "SELECT m FROM SgMarca m WHERE m.nombre = :nombre";

		if (eliminado != null) {
		    q += " AND m.eliminado = :eliminado";
		}

		Query qs = em.createQuery(q);
		//Asignando parámetros
		qs.setParameter("nombre", nombre);
		if (eliminado != null) {
		    qs.setParameter("eliminado", (eliminado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE));
		}
		marca = (SgMarca) qs.getSingleResult();

		return marca;
	    } catch (NoResultException nre) {
		return marca;
	    }
	} else {
	    throw new SIAException(SgMarcaImpl.class.getName(), "findByName()",
		    "Falta el parámetro \"nombre\"",
		    ("Parámetros: nombre: " + nombre
		    + " eliminado: " + eliminado));
	}
    }

    
    public List<SgMarca> findAll(String orderByField, String orderByOrder, boolean eliminado) throws Exception {
	UtilLog4j.log.info(this, "SgMarcaImpl.findAll()");

	List<SgMarca> marcas = null;

	String consulta = "SELECT m FROM SgMarca m WHERE m.eliminado = :eliminado";

	if (orderByField != null && !orderByField.equals("") && orderByOrder != null && !orderByOrder.equals("")) {
	    consulta += " ORDER BY m." + orderByField + " " + orderByOrder;
	}

	Query q = em.createQuery(consulta);

	//Asignando parámetros
	q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

	marcas = q.getResultList();

	UtilLog4j.log.info(this, "Se encontraron " + (marcas != null ? marcas.size() : 0) + " marcas");

	return marcas;
    }

    
    public List<SgMarca> findAllByTipo(SgTipo tipo, String orderByField, String orderByOrder, boolean eliminado) throws SIAException, Exception {
	UtilLog4j.log.info(this, "SgMarcaImpl.findByTipo()");

	List<SgMarca> marcas = null;

	if (tipo != null) {
	    String query = "SELECT m FROM SgMarca m WHERE m.sgTipo.id = :idTipo AND m.eliminado = :eliminado";

	    if (orderByField != null && !orderByField.equals("") && orderByOrder != null && !orderByOrder.equals("")) {
		query += " ORDER BY m." + orderByField + " " + orderByOrder;
	    }

	    Query q = em.createQuery(query);

	    //Asignando parámetros
	    q.setParameter("idTipo", tipo.getId());
	    q.setParameter("eliminado", (eliminado ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));

	    marcas = q.getResultList();

	} else {
	    throw new SIAException(SgMarcaImpl.class.getName(), "findByTipo()",
		    "Falta el parámetro \"tipo\"",
		    ("Parámetros: tipo: " + ((tipo != null) ? tipo.getId() : null)));
	}

	UtilLog4j.log.info(this, "Se encontraron " + (marcas != null ? marcas.size() : 0) + " marcas");

	return marcas;
    }

    
    public SgMarca findByNameAndTipo(String nombre, SgTipo sgTipo, Boolean eliminado) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgMarcaImpl.findByNameAndTipo()");

	SgMarca marca = null;

	if (nombre != null && !nombre.equals("")) {
	    try {
		//Armando query
		String query = "SELECT m FROM SgMarca m WHERE m.nombre = :nombre AND m.sgTipo = :sgTipo";

		if (eliminado != null) {
		    query += " AND m.eliminado = :eliminado";
		}

		Query q = em.createQuery(query);

		//Asignando parámetros
		q.setParameter("nombre", nombre);
		q.setParameter("sgTipo", sgTipo);

		if (eliminado != null) {
		    q.setParameter("eliminado", (eliminado ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE));
		}
//                UtilLog4j.log.info(this,"Query " + query);
//                UtilLog4j.log.info(this,"tipo " + sgTipo.getNombre());
		marca = (SgMarca) q.getSingleResult();

		return marca;
	    } catch (NoResultException nre) {
		UtilLog4j.log.fatal(this, "Excepcion  " + nre.getMessage());
		return marca;
	    }
	} else {
	    throw new SIAException(SgMarcaImpl.class.getName(), "findByName()",
		    "Falta el parámetro \"nombre\"",
		    ("Parámetros: nombre: " + nombre
		    + " eliminado: " + eliminado));
	}
    }

    
    public boolean findByIdMarcaOcuped(SgMarca sgMarca, String entity) throws SIAException, NonUniqueResultException, Exception {
	UtilLog4j.log.info(this, "SgMarcaImpl.findByIdMarcaOcuped()");

	boolean ret = false;

	if (sgMarca != null && !entity.equals("")) {
	    try {
		//Armando query
		String query = "SELECT count(o) FROM ".concat(entity).concat(" o WHERE o.sgMarca = :sgMarca AND o.eliminado = :eliminado");

		Query q = em.createQuery(query);
		q.setParameter("sgMarca", sgMarca);
		q.setParameter("eliminado", (Constantes.BOOLEAN_FALSE));
		UtilLog4j.log.info(this, "Query " + query);
		if ((Long) q.getSingleResult() > 0l) {
		    UtilLog4j.log.info(this, "Esta en la tabla de " + entity);
		    ret = true;
		}

	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, "Excepcion  " + e.getMessage());
		ret = false;
	    }
	} else {
	}
	return ret;
    }

    
    public List<Vo> traerMarcaPorTipo(int tipo) {
	clearQuery();
	List<Vo> lista = null;
	try {
	    query.append("select m.id, m.nombre from SG_MARCA m where m.SG_TIPO = ").append(tipo);
	    query.append("  and m.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append("  order by m.nombre asc");
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
