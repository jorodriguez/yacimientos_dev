/*
 * EstatusImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Estatus;
import sia.modelo.vo.StatusVO;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 7/07/2009
 */
@Stateless 
public class EstatusImpl{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    public void create(Estatus estatus) {
	em.persist(estatus);
    }

    
    public void edit(Estatus estatus) {
	em.merge(estatus);
    }

    
    public void remove(Estatus estatus) {
	em.remove(em.merge(estatus));
    }

    
    public Estatus find(Object id) {
	return em.find(Estatus.class, id);
    }

    
    public List<Estatus> findAll() {
	return em.createQuery("select object(o) from Estatus as o").getResultList();
    }

    
    public List<Estatus> traerPorRango(int inicio, int fin) {
	return em.createQuery("SELECT e FROM Estatus e WHERE e.id BETWEEN :ini AND :fin").setParameter("ini", inicio).setParameter("fin", fin).getResultList();
    }

    
    public List<StatusVO> traerPorTipo(String comprobante) {
	StringBuilder sb = new StringBuilder();
	sb.append("Select e.id, e.nombre from estatus e where e.tipo = '").append(comprobante).append("'");
	sb.append(" and e.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	sb.append(" order by e.id asc");
	List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
	List<StatusVO> le = null;
	if (lo != null) {
	    le = new ArrayList<>();
	    for (Object[] objects : lo) {
		le.add(castStatus(objects));
	    }
	}
	return le;
    }

    private StatusVO castStatus(Object[] objects) {
	StatusVO e = new StatusVO();
	e.setIdStatus((Integer) objects[0]);
	e.setNombre((String) objects[1]);
	return e;
    }
}
