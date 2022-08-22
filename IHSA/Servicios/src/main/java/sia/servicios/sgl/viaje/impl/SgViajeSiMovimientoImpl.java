/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeSiMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgViajeSiMovimientoImpl extends AbstractFacade<SgViajeSiMovimiento>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }    
    @Inject
    private SiMovimientoImpl siMovimientoRemote;

    public SgViajeSiMovimientoImpl() {
	super(SgViajeSiMovimiento.class);
    }

    
    public boolean guardarViajeCancelado(Usuario usuario, SgViaje sgViaje, SiMovimiento siMovimiento) {
	boolean v = true;
	SgViajeSiMovimiento sgViajeSiMovimiento = new SgViajeSiMovimiento();
	sgViajeSiMovimiento.setSgViaje(sgViaje);
	sgViajeSiMovimiento.setSiMovimiento(siMovimiento);
	sgViajeSiMovimiento.setGenero(usuario);
	sgViajeSiMovimiento.setFechaGenero(new Date());
	sgViajeSiMovimiento.setHoraGenero(new Date());
	sgViajeSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);
	create(sgViajeSiMovimiento);	
	return v;
    }

    
    public SgViajeSiMovimiento finByTravelStatus(int idViaje, int status) {
	try {
	    return (SgViajeSiMovimiento) em.createQuery("select v from SgViajeSiMovimiento v "
		    + " where v.sgViaje.id = :id "
		    + " AND v.sgViaje.estatus.id = :esta"
		    + " AND v.eliminado = :eli")
		    .setParameter("id", idViaje)
		    .setParameter("eli", Constantes.BOOLEAN_FALSE)
		    .setParameter("esta", status).getSingleResult();
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public SgViajeSiMovimiento findByTravelAndOperation(int idViaje, int status, int operacion) {
	try {
	    SgViajeSiMovimiento svm = (SgViajeSiMovimiento) em.createQuery("select v from SgViajeSiMovimiento v "
		    + " where v.sgViaje.id = :id "
		    + " AND v.sgViaje.estatus.id = :esta"
		    + " AND v.siMovimiento.siOperacion.id = :idOp"
		    + " AND v.eliminado = :eli")
		    .setParameter("id", idViaje)
		    .setParameter("eli", Constantes.BOOLEAN_FALSE)
		    .setParameter("idOp", operacion)
		    .setParameter("esta", status).getSingleResult();
            
            return svm;
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	    return null;
	}
    }

    
    public int guardarViajeMovimiento(String usuario, int idViaje, int idOperacion, String motivo) {
	SiMovimiento siMovimiento = siMovimientoRemote.save(motivo, idOperacion, usuario);
	SgViajeSiMovimiento sgViajeSiMovimiento = new SgViajeSiMovimiento();
	sgViajeSiMovimiento.setSgViaje(new SgViaje(idViaje));
	sgViajeSiMovimiento.setSiMovimiento(siMovimiento);
	sgViajeSiMovimiento.setGenero(new Usuario(usuario));
	sgViajeSiMovimiento.setFechaGenero(new Date());
	sgViajeSiMovimiento.setHoraGenero(new Date());
	sgViajeSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);
	create(sgViajeSiMovimiento);	
	return sgViajeSiMovimiento.getId();
    }
}
