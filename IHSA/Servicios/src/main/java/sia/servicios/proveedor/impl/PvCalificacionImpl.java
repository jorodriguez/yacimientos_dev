/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.PvCalificacion;
import sia.modelo.PvPrestacion;
import sia.modelo.PvRubro;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.GerenciaImpl;

/**
 *
 * @author mluis
 */
@Stateless 
public class PvCalificacionImpl extends AbstractFacade<PvCalificacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    @Inject
    private ProveedorServicioImpl proveedorRemote;
    @Inject
    private GerenciaImpl gerenciaRemote;
    @Inject
    private PvPrestacionImpl pvPrestacionRemote;

    public PvCalificacionImpl() {
        super(PvCalificacion.class);
    }

    
    public void guardarCalificaciones(String pro, int gerenciaId, int pvPrestacionId, List<PvCalificacion> listaCalificacion, Usuario usuario,
    String rfcEmpresa) {
        List<PvCalificacion> lc = traerCalificacionPorProveedor(pro, gerenciaId, pvPrestacionId, Constantes.NO_ELIMINADO);
        if (!lc.isEmpty()) {
            //quitar las calificaciones vigentes
            quitarCalificacionesVigentes(lc, usuario);
        } else {
            for (PvCalificacion pvCalificacion : listaCalificacion) {
                pvCalificacion.setProveedor(this.proveedorRemote.getPorNombre(pro,rfcEmpresa));
                pvCalificacion.setGerencia(this.gerenciaRemote.find(gerenciaId));
                pvCalificacion.setPvPrestacion(this.pvPrestacionRemote.find(pvPrestacionId));
                pvCalificacion.setGenero(usuario);
                pvCalificacion.setFechaGenero(new Date());
                pvCalificacion.setHoraGenero(new Date());
                pvCalificacion.setVigente(Constantes.BOOLEAN_TRUE);
                pvCalificacion.setEliminado(false);
                this.create(pvCalificacion);
            }
        }
    }

    private void quitarCalificacionesVigentes(List<PvCalificacion> lc, Usuario usuario) {
        for (PvCalificacion pvCalificacion : lc) {
            pvCalificacion.setGenero(usuario);
            pvCalificacion.setFechaGenero(new Date());
            pvCalificacion.setHoraGenero(new Date());
            pvCalificacion.setVigente(Constantes.BOOLEAN_FALSE);
            this.edit(pvCalificacion);
        }
    }

    
    public List<PvCalificacion> traerCalificacionPorProveedor(String pro, int gerenciaId, int pvPrestacionId, boolean eliminado) {
        try {
            return em.createQuery("SELECT  c FROM PvCalificacion c WHERE c.proveedor.nombre = :pro "
                    + " AND c.gerencia.id = :ger "
                    + " AND c.pvPrestacion.id = :pres"
                    + " AND c.vigente = :vi "
                    + " AND c.eliminado = :eli").setParameter("vi", Constantes.BOOLEAN_TRUE).setParameter("pro", pro).setParameter("ger", gerenciaId).setParameter("pres", pvPrestacionId).setParameter("eli", eliminado).getResultList();

        } catch (Exception e) {
            return null;
        }
    }

    
    public List<PvCalificacion> traerCalificacionesPorGerencia(int idGerencia, boolean eliminado) {
        try {
            return em.createQuery("SELECT c FROM PvCalificacion c WHERE c.gerencia.id = :ger "
                    + " AND c.eliminado = :eli"
                    + " AND c.vigente = :vi ").setParameter("vi", Constantes.BOOLEAN_TRUE).setParameter("ger", idGerencia).setParameter("eli", eliminado).getResultList();

        } catch (Exception e) {
            return null;
        }
    }

    
    public List<PvCalificacion> traerCalificacionesPorPrestacion(int idPrestacion, boolean eliminado) {
        try {
            return em.createQuery("SELECT c FROM PvCalificacion c WHERE  c.pvPrestacion.id = :pres"
                    + " AND c.eliminado = :eli"
                    + " AND c.vigente = :vi ").setParameter("vi", Constantes.BOOLEAN_TRUE).setParameter("pres", idPrestacion).setParameter("eli", eliminado).getResultList();

        } catch (Exception e) {
            return null;
        }
    }

    
    public void actualizarCalificacion(List<PvCalificacion> listaCalificacion, Usuario usuario) {
        for (PvCalificacion pvCalificacion : listaCalificacion) {
            pvCalificacion.setGenero(usuario);
            pvCalificacion.setFechaGenero(new Date());
            pvCalificacion.setHoraGenero(new Date());
            this.edit(pvCalificacion);
        }
    }

    
    public List<PvCalificacion> traerCalificacionPorProveedor(String pro, boolean eliminado) {
        try {
            return em.createQuery("SELECT c FROM PvCalificacion c WHERE  c.proveedor.nombre = :pro"
                    + " AND c.eliminado = :eli"
                    + " AND c.vigente = :vi ").setParameter("vi", Constantes.BOOLEAN_TRUE).setParameter("pro", pro).setParameter("eli", eliminado).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public boolean buscarPrestacionEnCalificacion(PvPrestacion pvPrestacion) {
        boolean v;
        try {
            List<PvCalificacion> list = em.createQuery("SELECT f FROM PvCalificacion f WHERE f.pvPrestacion.id = :idPrs "
                    + " AND f.eliminado = :eli"
                    + " AND f.vigente = :vi ").setParameter("vi", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("idPrs", pvPrestacion.getId()).getResultList();
            if (list.size() > 0) {
                v = true;
            } else {
                v = false;
            }
        } catch (Exception e) {
            v = false;
        }
        return v;
    }

    
    public boolean buscarRubroEnCalificacion(PvRubro pvRubro) {
        boolean v;
        try {
            List<PvCalificacion> list;
            list = em.createQuery("SELECT f FROM PvCalificacion f WHERE f.pvRubro.id = :id AND f.eliminado = :eli"
                    + " AND f.vigente = :vi ").setParameter("vi", Constantes.BOOLEAN_TRUE).setParameter("eli", Constantes.NO_ELIMINADO).setParameter("id", pvRubro.getId()).getResultList();
            if (list.size() > 0) {
                v = true;
            } else {
                v = false;
            }
        } catch (Exception e) {
            v = false;
        }

        return v;
    }

    
    public void eliminarCalificacion(List<PvCalificacion> listaCalificacion, boolean eliminado) {
        for (PvCalificacion pvCalificacion : listaCalificacion) {
            pvCalificacion.setVigente(Constantes.BOOLEAN_FALSE);
            pvCalificacion.setEliminado(eliminado);
            edit(pvCalificacion);
        }
    }

    
    public List<PvCalificacion> traerCalificacionPorProveedorModificar(String proveedor, String gerencia, String prestacion, boolean eliminado) {
        try {
            return em.createQuery("SELECT  c FROM PvCalificacion c WHERE c.proveedor.nombre = :pro "
                    + " AND c.gerencia.nombre = :ger "
                    + " AND c.pvPrestacion.nombre = :pres"
                    + " AND c.eliminado = :eli"
                    + " AND c.vigente = :vi ")
                    .setParameter("vi", Constantes.BOOLEAN_TRUE).setParameter("pro", proveedor).setParameter("ger", gerencia).setParameter("pres", prestacion).setParameter("eli", eliminado).getResultList();

        } catch (Exception e) {
            return null;
        }
    }
}
