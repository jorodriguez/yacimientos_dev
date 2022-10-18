/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiCondicion;
import sia.modelo.Usuario;

/**
 *
 * @author mluis
 */
import sia.modelo.sistema.AbstractFacade;

@Stateless 
public class SiCondicionImpl extends AbstractFacade<SiCondicion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiCondicionImpl() {
        super(SiCondicion.class);
    }

    
    public List<SiCondicion> traerCondicion(boolean NO_ELIMINADO) {
        try {
            return em.createQuery("SELECT c FROM SiCondicion c WHERE c.eliminado = :eli ORDER BY c.nombre ASC").setParameter("eli", NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public SiCondicion buscarPorNombre(SiCondicion siCondicion) {
        try {
            return (SiCondicion) em.createQuery("SELECT c FROM SiCondicion c WHERE c.eliminado = :eli AND c.nombre = :nombre").setParameter("eli", Constantes.NO_ELIMINADO).setParameter("nombre", siCondicion.getNombre()).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public void guardarCondicion(Usuario usuario, SiCondicion siCondicion) {
        try {
            siCondicion.setGenero(usuario);
            siCondicion.setFechaGenero(new Date());
            siCondicion.setHoraGenero(new Date());
            siCondicion.setEliminado(Constantes.NO_ELIMINADO);
            create(siCondicion);
        } catch (Exception e) {
        }
    }

    
    public void modificarCondicion(Usuario usuario, SiCondicion siCondicion) {
        try {
            siCondicion.setGenero(usuario);
            siCondicion.setFechaGenero(new Date());
            siCondicion.setHoraGenero(new Date());
            edit(siCondicion);
        } catch (Exception e) {
        }
    }

    
    public void eliminarCondicion(Usuario usuario, SiCondicion siCondicion) {
        try {
            siCondicion.setGenero(usuario);
            siCondicion.setFechaGenero(new Date());
            siCondicion.setHoraGenero(new Date());
            siCondicion.setEliminado(Constantes.ELIMINADO);
            edit(siCondicion);
        } catch (Exception e) {
        }
    }
}
