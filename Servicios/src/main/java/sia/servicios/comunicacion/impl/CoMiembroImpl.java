/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CoMiembro;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@LocalBean 
public class CoMiembroImpl extends AbstractFacade<CoMiembro>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoMiembroImpl() {
        super(CoMiembro.class);
    }

    
    public List<CoMiembro> getMiembros(Integer idGrupo) {
        return this.em.createQuery("SELECT m FROM CoMiembro m WHERE m.eliminado = :eliminado AND m.coGrupo.id = :idGrupo ORDER BY m.miembro.nombre")
                .setParameter("eliminado", false)
                .setParameter("idGrupo", idGrupo)
                .getResultList();
    }

    
    public int getTotal(Integer idGrupo) {
        return ((Long) em.createQuery("select count(m) FROM CoMiembro m WHERE m.eliminado = :eliminado AND m.coGrupo.id = :idGrupo")
                .setParameter("eliminado", false)
                .setParameter("idGrupo", idGrupo)
                .getSingleResult()).intValue();
    }

    
    public CoMiembro getMiembroPorNombre(String nombreMiembro, Integer idGrupo) {
        try {
         return (CoMiembro) em.createQuery("SELECT m FROM CoMiembro m WHERE m.miembro.nombre =:nombre AND m.coGrupo.id = :idGrupo")
                .setParameter("nombre", nombreMiembro)
                .setParameter("idGrupo", idGrupo)
                .getResultList()
                .get(0);           
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
    
    
}
