/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.convenio.impl;

import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.CvRelacionActividad;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@LocalBean 
public class CvRelacionActividadImpl extends AbstractFacade<CvRelacionActividad> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CvRelacionActividadImpl() {
        super(CvRelacionActividad.class);
    }

    

    
    public List<CvRelacionActividad> traerConvenios(int actividad, String proveedor, int proActividad) {
        if (actividad > 0 && proActividad < 0 && proveedor.equals("-1")) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.actividad.id = :act").setParameter("act", actividad).getResultList();
        } else if (actividad > 0 && !proveedor.equals("-1") && proActividad < 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.actividad.id = :act AND r.clasificacion.proveedorActividad.proveedor.nombre = :pro").setParameter("act", actividad).setParameter("pro", proveedor).getResultList();
        } else if (actividad > 0 && !proveedor.equals("-1") && proActividad > 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.id = :proAct").setParameter("proAct", proActividad).getResultList();
        }
        return null;
    }

    
    public List<CvRelacionActividad> traerConveniosVigentePorProveedor(int actividad, String proveedor, int subActividad) {
        if (actividad > 0 && subActividad < 0 && proveedor.equals("-1")) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.actividad.id = :act AND r.convenio.estatus.id = :est AND r.convenio.cvTipo.id = :uno").setParameter("uno", 1).setParameter("est", 301).setParameter("act", actividad).getResultList();
        } else if (actividad > 0 && !proveedor.equals("-1") && subActividad < 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.actividad.id = :act AND r.clasificacion.proveedorActividad.proveedor.nombre = :pro "
                    + "AND r.convenio.estatus.id = :est AND r.convenio.cvTipo.id = :uno").setParameter("uno", 1).setParameter("est", 301).setParameter("act", actividad).setParameter("pro", proveedor).getResultList();
        } else if (actividad > 0 && !proveedor.equals("-1") && subActividad > 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.id = :proAct AND r.convenio.estatus.id = :est AND r.convenio.cvTipo.id = :uno").setParameter("uno", 1).setParameter("est", 301).setParameter("proAct", subActividad).getResultList();
        }
        return null;
    }

    
    public List<CvRelacionActividad> traerAcuerdosVigentePorProveedor(int actividad, String proveedor, int subActividad) {
        if (actividad > 0 && subActividad < 0 && proveedor.equals("-1")) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.actividad.id = :act AND r.convenio.estatus.id = :est AND "
                    + " r.convenio.cvTipo.id = :dos").setParameter("dos", 2).setParameter("est", 301).setParameter("act", actividad).getResultList();
        } else if (actividad > 0 && !proveedor.equals("-1") && subActividad < 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.actividad.id = :act AND r.clasificacion.proveedorActividad.proveedor.nombre = :pro "
                    + "AND r.convenio.estatus.id = :est AND r.convenio.cvTipo.id = :dos").setParameter("dos", 2).setParameter("est", 301).setParameter("act", actividad).setParameter("pro", proveedor).getResultList();
        } else if (actividad > 0 && !proveedor.equals("-1") && subActividad > 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.id = :proAct AND r.convenio.estatus.id = :est AND r.convenio.cvTipo.id = :dos").setParameter("dos", 2).setParameter("est", 301).setParameter("proAct", subActividad).getResultList();
        }
        return null;
    }

    
    public List<CvRelacionActividad> traerServiciosVigentePorProveedor(int actividad, String proveedor, int subActividad) {
        if (actividad > 0 && subActividad < 0 && proveedor.equals("-1")) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.actividad.id = :act AND r.convenio.estatus.id = :est AND "
                    + " r.convenio.cvTipo.id = :tres").setParameter("tres", 3).setParameter("est", 301).setParameter("act", actividad).getResultList();
        } else if (actividad > 0 && !proveedor.equals("-1") && subActividad < 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.actividad.id = :act AND r.clasificacion.proveedorActividad.proveedor.nombre = :pro "
                    + "AND r.convenio.estatus.id = :est AND r.convenio.cvTipo.id = :tres").setParameter("tres", 3).setParameter("est", 301).setParameter("act", actividad).setParameter("pro", proveedor).getResultList();
        } else if (actividad > 0 && !proveedor.equals("-1") && subActividad > 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.id = :proAct AND r.convenio.estatus.id = :est AND r.convenio.cvTipo.id = :tres").setParameter("tres", 3).setParameter("est", 301).setParameter("proAct", subActividad).getResultList();
        }
        return null;
    }

    
    public List<CvRelacionActividad> getConveniosVigentePorProveedor(String proveedor, int actividad) {
        if (proveedor != null && actividad < 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.proveedor.nombre = :pro AND "
                    + " r.convenio.cvTipo.id = :uno AND r.convenio.estatus.id = :est").setParameter("est", 301).setParameter("uno", 1).setParameter("pro", proveedor).getResultList();
        } else if (proveedor != null && actividad > 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.proveedor.nombre = :pro AND "
                    + " r.clasificacion.proveedorActividad.actividad.id = :act AND r.convenio.cvTipo.id = :uno AND r.convenio.estatus.id = :est").setParameter("est", 301).setParameter("uno", 1).setParameter("pro", proveedor).setParameter("act", actividad).getResultList();
        }
        return null;
    }

    
    public List<CvRelacionActividad> traerAcuerdosporProveedor(String proveedor, int actividad) {
        if (proveedor != null && actividad < 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.proveedor.nombre = :pro AND "
                    + "r.convenio.cvTipo.id = :dos AND r.convenio.estatus.id = :est").setParameter("est", 301).setParameter("dos", 2).setParameter("pro", proveedor).getResultList();
        } else if (proveedor != null && actividad > 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.proveedor.nombre = :pro AND "
                    + " r.clasificacion.proveedorActividad.actividad.id = :act AND r.convenio.cvTipo.id = :dos AND r.convenio.estatus.id = :est").setParameter("est", 301).setParameter("dos", 2).setParameter("pro", proveedor).setParameter("act", actividad).getResultList();
        }
        return null;
    }

    
    public List<CvRelacionActividad> traerServiciosPorProveedor(String proveedor, int actividad) {
        if (proveedor != null && actividad < 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.proveedor.nombre = :pro AND "
                    + "r.convenio.cvTipo.id = :tres AND r.convenio.estatus.id = :est").setParameter("est", 301).setParameter("tres", 3).setParameter("pro", proveedor).getResultList();
        } else if (proveedor != null && actividad > 0) {
            return em.createQuery("SELECT r FROM CvRelacionActividad r WHERE r.clasificacion.proveedorActividad.proveedor.nombre = :pro AND "
                    + " r.clasificacion.proveedorActividad.actividad.id = :act AND "
                    + " r.convenio.cvTipo.id = :tres AND r.convenio.estatus.id = :est").setParameter("est", 301).setParameter("tres", 3).setParameter("pro", proveedor).setParameter("act", actividad).getResultList();
        }
        return null;
    }
}
