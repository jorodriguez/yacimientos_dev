/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.sistema;

import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author hacosta
 * @param <T>
 */
public abstract class AbstractFacade<T> {

    protected StringBuilder query = new StringBuilder();   //<<<<<<<<<<
    private final Class<T> entityClass;

    protected AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    /*
     * creado:Joel Rodriguez
     *
     * Limpia la varible query
     */
    protected void clearQuery() {
        query.delete(0, query.length());
    }

    /*
     * creado:Joel Rodriguez Agregar al query
     */
    protected StringBuilder appendQuery(Object str) {
        query.append(str != null ? str : "");
        return this.query;
    }

    /*
     * creado:Joel Rodriguez tomar lo agregado en String
     *
     */
    protected String getStringQuery() {
        return query.toString();
    }
}
