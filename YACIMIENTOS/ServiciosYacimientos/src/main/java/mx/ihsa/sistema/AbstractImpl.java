/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.sistema;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.ihsa.constantes.Constantes;
import mx.ihsa.util.UtilSia;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

/**
 *
 * @param <T>
 */
public abstract class AbstractImpl<T> {

    public static final String FIELD_MODIFICO = "modifica";
    public static final String FIELD_GENERO = "genero";
    public static final String FIELD_ID = "id";
    public static final String FIELD_ELIMINADO = "eliminado";

    @PersistenceContext(unitName = Constantes.PERSISTENCE_UNIT)
    protected EntityManager em;

    @Inject
    protected DSLContext dbCtx;

    protected StringBuilder query = new StringBuilder();

    private final Class<T> entityClass;

    private String TABLE_NAME = "";

    //protected abstract EntityManager getEntityManager();
    protected AbstractImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.TABLE_NAME = UtilSia.camelToSnake(entityClass.getName());
    }

    public void create(T entity) {
        em.persist(entity);
    }

    public void edit(T entity) {
        em.merge(entity);
    }

    public void remove(T entity) {
        em.remove(em.merge(entity));
    }

    public T find(Object id) {
        return em.find(entityClass, id);
    }

    public <DTO> List<DTO> findAll(Class<DTO> dto, String... app) {

        //return dbCtx.fetch("SELECT * FROM "+camelToSnake(entityClass.getName())+" where eliminado = false").into(dto);
        return dbCtx.fetch("SELECT * FROM " + TABLE_NAME + " where eliminado = false").into(dto);

    }

    public <D> D findById(final int id, Class dto) {
        D retVal = null;
        Class<D> typed = null;

        try {
            retVal = dbCtx.select()
                    .from(TABLE_NAME)
                    .where(DSL.field(FIELD_ID).eq(id))
                    .fetchOneInto(typed);
        } catch (DataAccessException e) {
            System.out.println("Error findId " + e);
            //log.warn("*** error {} with id {}", table, id, e);
        }

        return retVal;
    }

    public <D> D findAll() {
        D retVal = null;
        Class<D> typed = null;

        try {
            retVal = dbCtx.select()
                    .from(TABLE_NAME)
                    .where(DSL.field(FIELD_ELIMINADO).eq(false))
                    .fetchOneInto(typed);

        } catch (DataAccessException e) {
            System.out.println("Error findAll " + e);
            //log.warn("*** error {} with id {}", table, id, e);
        }

        return retVal;
    }
    
    public <D> List<D> getByCondition(final Condition conditions) {
		List<D> retVal = null;
                
                final Class<D> typed = null;
                
		try {
			                 SelectJoinStep<Record> select = dbCtx.select().from(TABLE_NAME);

			if(conditions != null) {
				select.getQuery().addConditions(conditions);
			}

			/*if(orderBy != null) {
				select.orderBy(orderBy);
			}*/

			retVal = select.fetchInto(typed);
                        
		} catch (DataAccessException e) {
			System.err.println("Error getByCondition "+e);			
			retVal = Collections.emptyList();
		}

		return retVal;
	}

    protected void clearQuery() {
        query.delete(0, query.length());
    }

    protected StringBuilder appendQuery(Object str) {
        query.append(str != null ? str : "");
        return this.query;
    }

    protected String getStringQuery() {
        return query.toString();
    }
}
