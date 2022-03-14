/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.PvPrestacion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@LocalBean 
public class PvPrestacionImpl extends AbstractFacade<PvPrestacion>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvPrestacionImpl() {
        super(PvPrestacion.class);
    }

    
    public List<PvPrestacion> traerPrestacionActiva(boolean eliminado) {
        try {
            return em.createQuery("SELECT pres FROM PvPrestacion pres WHERE pres.eliminado = :estatus ORDER BY pres.id ASC").setParameter("estatus", eliminado).getResultList();
        } catch (Exception e) {
        }
        return null;
    }

    
    public PvPrestacion buscarPorNombre(String nombre, Usuario usuario, boolean eliminado) {
        try {
            return (PvPrestacion) em.createQuery("SELECT pres FROM PvPrestacion pres WHERE pres.nombre = :nombre AND pres.genero.id = :user AND pres.eliminado = :estatus ").setParameter("nombre", nombre).setParameter("user", usuario.getId()).setParameter("estatus", eliminado).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public void guardarPrestacion(PvPrestacion pvPrestacion, Usuario usuario, boolean eliminado) {
        if (this.buscarPorNombre(pvPrestacion.getNombre(), usuario, eliminado) != null) {
            //Se pone la prestación visible
            pvPrestacion.setGenero(usuario);
            pvPrestacion.setDescripcion(pvPrestacion.getDescripcion());
            pvPrestacion.setFechaGenero(new Date());
            pvPrestacion.setHoraGenero(new Date());
            pvPrestacion.setEliminado(eliminado);
            this.edit(pvPrestacion);
        } else {
            PvPrestacion prestacion = new PvPrestacion();
            prestacion.setGenero(usuario);
            prestacion.setNombre(pvPrestacion.getNombre());
            prestacion.setDescripcion(pvPrestacion.getDescripcion());
            prestacion.setFechaGenero(new Date());
            prestacion.setHoraGenero(new Date());
            prestacion.setEliminado(eliminado);
            this.create(prestacion);
        }
    }

    
    public void modificarPresentacion(PvPrestacion pvPrestacion, Usuario usuario) {
        pvPrestacion.setGenero(usuario);
        pvPrestacion.setNombre(pvPrestacion.getNombre());
        pvPrestacion.setFechaGenero(new Date());
        pvPrestacion.setHoraGenero(new Date());
        this.edit(pvPrestacion);
    }

    
    public void eliminarPrestacion(PvPrestacion pvPrestacion, Usuario usuario) {
        pvPrestacion.setGenero(usuario);
        pvPrestacion.setFechaGenero(new Date());
        pvPrestacion.setHoraGenero(new Date());
        pvPrestacion.setEliminado(true);
        this.edit(pvPrestacion);
    }
}
