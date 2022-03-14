/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.SgTallerMantenimiento;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgTallerMantenimientoImpl extends AbstractFacade<SgTallerMantenimiento>{

    private static final UtilLog4j<SgTallerMantenimientoImpl> LOGGER = UtilLog4j.log;
    
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgOficinaImpl sgOficinaRemote;
    @Inject
    private ProveedorServicioImpl proveedorRemote;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgTallerMantenimientoImpl() {
	super(SgTallerMantenimiento.class);
    }

    
    public void createTaller(int idProveedor, int idOficina, String usuarioGenero) {
	
	try {
	    SgTallerMantenimiento taller = new SgTallerMantenimiento();
	    taller.setSgOficina(this.sgOficinaRemote.find(idOficina));
	    taller.setProveedor(this.proveedorRemote.find(idProveedor));
	    taller.setGenero(new Usuario(usuarioGenero));
	    taller.setEliminado(Constantes.BOOLEAN_FALSE);
	    taller.setFechaGenero(new Date());
	    taller.setHoraGenero(new Date());
	    create(taller);
	} catch (Exception e) {
	    LOGGER.warn(this, e);
	}
    }

    
    public void deleteTaller(SgTallerMantenimiento sgTaller, Usuario usuarioGenero) {
	try {
	    sgTaller.setEliminado(Constantes.BOOLEAN_TRUE);
	    super.edit(sgTaller);

	} catch (Exception e) {
	    LOGGER.warn(this, e);
	}
    }

    
    public List<SgTallerMantenimiento> findAllTalleres(int oficina) {
	LOGGER.fatal(this, "findAllTalleres");
	List<SgTallerMantenimiento> ret = null;
	try {
	    ret = em.createQuery("SELECT a FROM SgTallerMantenimiento a "
		    + " WHERE a.eliminado = :eliminado AND a.sgOficina.id = :idOficina ORDER BY a.proveedor.nombre ASC ")
		    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
		    .setParameter("idOficina", oficina)
		    .getResultList();
	    LOGGER.fatal(this, "Retorno talleres " + ret.size());
	} catch (Exception e) {
	    LOGGER.fatal(this, "Excepcion al consultar talleres(Proveedor) " + e.getMessage());
	}
        
        return ret;
    }

    
    public List<Proveedor> findAllTalleresProveedor(int oficina) {
	LOGGER.info(this, "findAllTalleresProveedor");
	List<Proveedor> ret = null;
	try {
	    ret = em.createQuery("SELECT a.proveedor FROM SgTallerMantenimiento a "
		    + " WHERE a.eliminado = :eliminado AND a.sgOficina.id = :idOficina ORDER BY a.proveedor.nombre ASC ")
		    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
		    .setParameter("idOficina", oficina)
		    .getResultList();
	    LOGGER.fatal(this, "Retorno talleres " + ret.size());
	    
	} catch (Exception e) {
	    LOGGER.fatal(this, "Excepcion al consultar talleres(Proveedor) " + e.getMessage(), e);
	}
        
        return ret;
    }

    
    public boolean findTaller(String nombreProv, int oficina) {
	boolean retVal = false;
        
	try {
	    final SgTallerMantenimiento ret = (SgTallerMantenimiento) em.createQuery("SELECT t FROM SgTallerMantenimiento t "
		    + " WHERE t.proveedor.nombre = :nombreProveedor AND t.sgOficina.id = :idOficina AND t.eliminado = :eliminado ")
		    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
		    .setParameter("nombreProveedor", nombreProv)
		    .setParameter("idOficina", oficina)
		    .getSingleResult();
	    
            retVal = null != ret;
            
	} catch (Exception e) {
	    LOGGER.fatal(this, "Excepcion en buscar el proveedor " + e.getMessage());
	}
        
        return retVal;
    }

}
