/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgVehiculo;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgViajeVehiculoImpl extends AbstractFacade<SgViajeVehiculo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgViajeVehiculoImpl() {
	super(SgViajeVehiculo.class);
    }


    
    public void save(Usuario usuario, SgVehiculo sgVehiculo, SgViaje sgViaje) throws SIAException {
	try {
	    SgViajeVehiculo sgViajeVehiculo = new SgViajeVehiculo();
	    sgViajeVehiculo.setSgVehiculo(sgVehiculo);
	    sgViajeVehiculo.setSgViaje(sgViaje);
	    sgViajeVehiculo.setGenero(usuario);
	    sgViajeVehiculo.setFechaGenero(new Date());
	    sgViajeVehiculo.setHoraGenero(new Date());
	    sgViajeVehiculo.setEliminado(Constantes.NO_ELIMINADO);
	    create(sgViajeVehiculo);
        } catch (Exception ex) {
	    Logger.getLogger(SgViajeVehiculoImpl.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    
    public void guardar(String sesion, int idVehiculo, int idViaje) throws SIAException {
	try {
	    SgViajeVehiculo sgViajeVehiculo = new SgViajeVehiculo();
	    sgViajeVehiculo.setSgVehiculo(new SgVehiculo(idVehiculo));
	    sgViajeVehiculo.setSgViaje(new SgViaje(idViaje));
	    sgViajeVehiculo.setGenero(new Usuario(sesion));
	    sgViajeVehiculo.setFechaGenero(new Date());
	    sgViajeVehiculo.setHoraGenero(new Date());
	    sgViajeVehiculo.setEliminado(Constantes.NO_ELIMINADO);
	    create(sgViajeVehiculo);	    
	} catch (Exception ex) {
	    Logger.getLogger(SgViajeVehiculoImpl.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    
    public SgViajeVehiculo getVehicleByTravel(int idViaje) {
	try {
	    return (SgViajeVehiculo) em.createQuery("SELECT v FROM SgViajeVehiculo v WHERE v.sgViaje.id = :idViaje AND v.eliminado = :eli").setParameter("idViaje", idViaje).setParameter("eli", Constantes.NO_ELIMINADO).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.toString());
	    return null;
	}
    }

    
    public void update(Usuario usuario, SgViajeVehiculo sgViajeVehiculo, SgVehiculo sgVehiculo) {
	try {
	    sgViajeVehiculo.setSgVehiculo(sgVehiculo);
	    sgViajeVehiculo.setModifico(usuario);
	    sgViajeVehiculo.setFechaModifico(new Date());
	    sgViajeVehiculo.setHoraModifico(new Date());
	    edit(sgViajeVehiculo);
	} catch (Exception e) {
            UtilLog4j.log.error(e);
	}
    }

    
    public int contarVehiculoViajes(int idVehiculo) {
	clearQuery();
	query.append("select count(*) from SG_VIAJE_VEHICULO viav ");
	query.append("  where viav.SG_VEHICULO = ").append(idVehiculo);
	query.append("  and viav.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	return ((Integer) em.createNativeQuery(query.toString()).getSingleResult());
    }

    
    public void guardarViajeVehiculo(String usuario, int idVehiculo, int sgViaje) {
	try {
	    SgViajeVehiculo sgViajeVehiculo = new SgViajeVehiculo();
	    sgViajeVehiculo.setSgVehiculo(new SgVehiculo(idVehiculo));
	    sgViajeVehiculo.setSgViaje(new SgViaje(sgViaje));
	    sgViajeVehiculo.setGenero(new Usuario(usuario));
	    sgViajeVehiculo.setFechaGenero(new Date());
	    sgViajeVehiculo.setHoraGenero(new Date());
	    sgViajeVehiculo.setEliminado(Constantes.NO_ELIMINADO);
	    create(sgViajeVehiculo);	    
	} catch (Exception ex) {
	    Logger.getLogger(SgViajeVehiculoImpl.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    
    public void actualizarVehiculo(String sesion, int idViajeVehiculo, int vehiculo) {
	SgViajeVehiculo sgViajeVehiculo = find(idViajeVehiculo);
	sgViajeVehiculo.setSgVehiculo(new SgVehiculo(vehiculo));
	sgViajeVehiculo.setModifico(new Usuario(sesion));
	sgViajeVehiculo.setFechaModifico(new Date());
	sgViajeVehiculo.setHoraModifico(new Date());
	edit(sgViajeVehiculo);
    }
}
