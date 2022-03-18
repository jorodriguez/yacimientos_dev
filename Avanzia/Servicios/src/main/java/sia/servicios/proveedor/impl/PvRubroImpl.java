/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.PvRubro;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class PvRubroImpl extends AbstractFacade<PvRubro> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvRubroImpl() {
        super(PvRubro.class);
    }

    
    public List<PvRubro> traerRubro(boolean eliminado){
        try {
            return em.createQuery("SELECT r FROM PvRubro r WHERE r.eliminado = :eli ORDER BY r.nombre ASC").setParameter("eli",eliminado).getResultList();
        } catch (Exception e) {
        return  null;
        }
    }
    
    public PvRubro buscarPorNombre(String nombre, Usuario usuario, boolean eliminado) {
        try {
            return (PvRubro) em.createQuery("SELECT r FROM PvRubro r WHERE r.nombre = :nombre "
                    + " AND r.genero.id = :user AND r.eliminado = :eli").setParameter("nombre", nombre).setParameter("user", usuario.getId()).setParameter("eli", eliminado).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public void guardarRubro(PvRubro pvRubro, Usuario usuario, boolean eliminado) {
        if (this.buscarPorNombre(pvRubro.getNombre(), usuario, eliminado) != null) {
            pvRubro.setGenero(usuario);
            pvRubro.setFechaGenero(new Date());
            pvRubro.setHoraGenero(new Date());
            pvRubro.setEliminado(eliminado);
            this.edit(pvRubro);
        } else {
            pvRubro.setGenero(usuario);
            pvRubro.setFechaGenero(new Date());
            pvRubro.setHoraGenero(new Date());
            pvRubro.setEliminado(eliminado);
            this.create(pvRubro);
        }
    }

    
    public void modificarRubro(PvRubro pvRubro, Usuario usuario) {
        pvRubro.setGenero(usuario);
        pvRubro.setFechaGenero(new Date());
        pvRubro.setHoraGenero(new Date());
        this.edit(pvRubro);
    }

    
    public void eliminarRubro(PvRubro pvRubro, Usuario usuario, boolean eliminado) {
        pvRubro.setGenero(usuario);
        pvRubro.setFechaGenero(new Date());
        pvRubro.setHoraGenero(new Date());
        pvRubro.setEliminado(eliminado);
        this.edit(pvRubro);
    }
}
