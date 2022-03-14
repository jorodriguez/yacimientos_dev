package sia.inventarios.service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import sia.excepciones.SIAException;
import sia.modelo.Usuario;

/**
 *
 * @author Aplimovil SA de CV
 */
public class Utilitarios {

    public static boolean esNuloOVacio(String cadena) {
	return (cadena == null || cadena.isEmpty());
    }
    
    public static boolean esNuloOVacio(Double dooble) {
	return (dooble == null || dooble == 0);
    }

    public static boolean esNuloOVacio(Integer entero) {
	return (entero == null || entero == 0);
    }

    public static boolean esNuloOVacio(Long entero) {
	return (entero == null || entero == 0);
    }

    public static int obtenerNumero(Integer entero) {
	return entero == null ? 0 : entero.intValue();
    }
    public static double obtenerNumero(Double entero) {
	return entero == null ? 0 : entero;
    }

    public static String crearExpresionLike(String cadena) {
	return String.format("%%%s%%", cadena);
    }

    /**
     * Obtiene una lista de entidades en base a una entidad ejemplo
     *
     * @param entity T
     * @return
     */
    public static <T> List<T> findByExample(EntityManager em, T entity) throws SIAException {
	String alias = entity.getClass().getSimpleName().substring(0, 1).toLowerCase();
	String from = new StringBuilder().append("select ").append(alias).append(" from ").append(entity.getClass().getSimpleName()).append(" ").append(alias).append(" where 1 = 1").toString();
	StringBuilder sql = new StringBuilder().append(from);

	for (Field f : entity.getClass().getDeclaredFields()) {
	    Column col = (Column) f.getAnnotation(Column.class);

	    f.setAccessible(true);
	    try {
		Object value = f.get(entity);
		if ((col != null) && (value != null)) {
		    StringBuilder criterio = new StringBuilder().append(" and ").append(alias).append(".").append(f.getName());
		    if (value instanceof String) {
			String stringValue = (String) value;
			if (!stringValue.isEmpty()) {
			    criterio.append(" like '%").append(stringValue).append("%' ");
			    sql.append(criterio);
			}
		    } else if (value instanceof Date) {
			Date dateValue = (Date) value;
			criterio.append(" = ").append(dateValue);
			sql.append(criterio);
		    } else if (value instanceof Long) {
			Long dateValue = (Long) value;
			criterio.append(" = ").append(dateValue);
			sql.append(criterio);
		    } else if (value instanceof Short) {
			Short dateValue = (Short) value;
			criterio.append(" = ").append(dateValue);
			sql.append(criterio);
		    }
		}
	    } catch (Exception ex) {
		throw new SIAException("AbstractFacade", "findByExample", ex.getMessage());
	    }
	}

	Query qry = em.createQuery(sql.toString());
	return qry.getResultList();
    }

    /**
     * Busca entidades utilizando un native query
     *
     * @param sql
     * @param type
     * @param parameters
     * @return List
     * @throws SIAException
     */
    public static <T> List<T> findByNativeQuery(EntityManager em, String sql, Class<T> type, Map<String, Object> parameters) throws SIAException {
	try {
	    Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
	    Query query = em.createNativeQuery(sql, type);

	    for (Map.Entry<String, Object> entry : rawParameters) {
		query.setParameter(entry.getKey(), entry.getValue());
	    }

	    return query.getResultList();
	} catch (Exception ex) {
	    throw new SIAException("AbstractFacade", "findByNativeQuery", ex.getMessage());
	}
    }

    /**
     * Devuelve una lista de entidades utilizando un named query
     *
     * @param sql
     * @param type
     * @return List
     * @throws SIAException
     */
    public static <T> List<T> findByNamedQuery(EntityManager em, String sql, Class<T> type) throws SIAException {
	try {
	    Query query = em.createNamedQuery(sql, type);
	    return query.getResultList();
	} catch (Exception ex) {
	    throw new SIAException("AbstractFacade", "findByNamedQuery", ex.getMessage());
	}
    }
}
