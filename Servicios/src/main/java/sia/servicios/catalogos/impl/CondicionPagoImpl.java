/*
 * CondicionPagoFacade.java
 * Creada el 26/08/2009, 12:25:54 PM
 * Clase Java desarrollada por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de esta clase, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: new_nick_name@hotmail.com
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CondicionPago;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.CondicionPagoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 26/08/2009
 */
@LocalBean 
public class CondicionPagoImpl extends AbstractFacade<CondicionPago> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CondicionPagoImpl() {
        super(CondicionPago.class);
    }

    
    public void create(CondicionPago condicionPago) {
        em.persist(condicionPago);
    }

    
    public void edit(CondicionPago condicionPago) {
        em.merge(condicionPago);
    }

    
    public void remove(CondicionPago condicionPago) {
        em.remove(em.merge(condicionPago));
    }

    
    public CondicionPago find(Object id) {
        return em.find(CondicionPago.class, id);
    }

    
    public List<CondicionPago> findAll() {
        return em.createQuery("select object(o) from CondicionPago as o").getResultList();
    }

    
    public List<CondicionPago> findAllByEstado(String estado) {
        return em.createQuery("SELECT c FROM CondicionPago c WHERE c.eliminado = :estado ORDER BY c.nombre ASC").setParameter("estado", estado).getResultList();
        // return  em.createQuery("SELECT c FROM CondicionPago c WHERE c.eliminado = :estado ORDER BY c.id ASC").setParameter("estado", estado).getResultList();

    }

    
    public CondicionPago buscarPorNombre(Object nombreCondicionPago, boolean estado) {
        try {
            return (CondicionPago) em.createQuery("SELECT c FROM CondicionPago c WHERE c.nombre = :nombre AND c.eliminado = :estado").setParameter("nombre", nombreCondicionPago).setParameter("estado", estado).getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    
    public void guardarAltaCondicion(String nombre, boolean tr, Usuario usuario, String compania) {
        try {
            CondicionPago existente = buscarPorNombre(nombre, Constantes.BOOLEAN_TRUE);//Se verifica si ya existe una Condicion de Pago 'eliminada' con el mismo nombre, solo se le cambiara el status
            if (existente != null) {
                existente.setNotificar(tr);
                existente.setGenero(usuario);
                existente.setFechaGenero(new Date());
                existente.setHoraGenero(new Date());
                existente.setEliminado(Constantes.BOOLEAN_FALSE);
                edit(existente);
            } else {
                CondicionPago condicionPago = new CondicionPago();
                condicionPago.setNombre(nombre);
                condicionPago.setNotificar(tr);
                condicionPago.setGenero(usuario);
                condicionPago.setFechaGenero(new Date());
                condicionPago.setHoraGenero(new Date());
                condicionPago.setEliminado(Constantes.BOOLEAN_FALSE);
                create(condicionPago);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
        }
    }

    
    public CondicionPago guardarAltaCondicionReturn(String nombre, boolean tr, Usuario usuario) {
        try {
            CondicionPago existente = buscarPorNombre(nombre, Constantes.BOOLEAN_TRUE);//Se verifica si ya existe una Condicion de Pago 'eliminada' con el mismo nombre, solo se le cambiara el status

            if (existente != null) {
                existente.setNotificar(tr);
                existente.setGenero(usuario);
                existente.setFechaGenero(new Date());
                existente.setHoraGenero(new Date());
                existente.setEliminado(Constantes.BOOLEAN_FALSE);
                this.edit(existente);
                return existente;
            } else {
                CondicionPago condicionPago = new CondicionPago();
                condicionPago.setNombre(nombre);
                condicionPago.setNotificar(tr);
                condicionPago.setGenero(usuario);
                condicionPago.setFechaGenero(new Date());
                condicionPago.setHoraGenero(new Date());
                condicionPago.setEliminado(Constantes.BOOLEAN_FALSE);
                this.create(condicionPago);
                return condicionPago;
            }

        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
            return null;
        }
    }

    
    public void actualizarConPago(int idConPago, String nombre, boolean notifica, Usuario usuario, String compania) {

        try {            
            CondicionPago condicionPago = find(idConPago);            
            condicionPago.setNombre(nombre);
            condicionPago.setNotificar(notifica);
            condicionPago.setModifico(usuario);
            condicionPago.setFechaModifico(new Date());
            condicionPago.setHoraModifico(new Date());
            edit(condicionPago);
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
        }
    }

    
    public void eliminarConPago(int idConPago, Usuario usuario) {
        try {
            CondicionPago condicionPago = find(idConPago);
            condicionPago.setGenero(usuario);
            condicionPago.setFechaGenero(new Date());
            condicionPago.setHoraGenero(new Date());
            condicionPago.setEliminado(Constantes.ELIMINADO);
            super.edit(condicionPago);
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage(), e);
        }
    }

    
    public List<String> traerNombreConPagoQueryNativo() {
        clearQuery();
        query.append("SELECT  c.id, c.nombre FROM Condicion_Pago c WHERE c.eliminado = 'False'");
        
        query.append(" ORDER BY c.id ASC");
        List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
        List<String> ls = null;
        if (lo != null) {
            ls = new ArrayList<String>();
            for (Object[] objects : lo) {
                ls.add((String) objects[1]);
            }
        }
        return ls;
    }

    
    public List<String> traerNombreLikeConPagoQueryNativo(String cadena) {
        return em.createNativeQuery("SELECT c.nombre FROM Condicion_Pago c WHERE c.eliminado = 'False' AND  upper(c.nombre) LIKE '" + cadena.toUpperCase() + "%'"
                + " ORDER BY c.nombre ASC").getResultList();
    }

    
    public List<CondicionPagoVO> trearCondicionPago(boolean estado) {
        List<CondicionPagoVO> lcp = null;
        try {
            clearQuery();
            query.append("SELECT c.id, c.nombre, c.notificar FROM Condicion_Pago c ");
            query.append(" where c.eliminado = '").append(estado).append("'");
            query.append(" order by c.id asc");
            //
            List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();

            if (lo != null) {
                lcp = new ArrayList<CondicionPagoVO>();
                for (Object[] objects : lo) {
                    lcp.add(castCondicionPago(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return lcp;
    }

    private CondicionPagoVO castCondicionPago(Object[] objects) {
        try {
            CondicionPagoVO cpvo = new CondicionPagoVO();
            cpvo.setId((Integer) objects[0]);
            cpvo.setNombre((String) objects[1]);
            cpvo.setAnticipo(((String) objects[2]).equals(Constantes.BOOLEAN_TRUE) ? true : false);
            return cpvo;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }

    }
}
