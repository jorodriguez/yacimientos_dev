/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.combustible.impl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.modelo.SgEstacion;
import sia.modelo.SiCiudad;
import sia.modelo.SiEstado;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiEstadoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SgEstacionImpl extends AbstractFacade<SgEstacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgEstacionImpl() {
	super(SgEstacion.class);
    }

    @Inject
    private SiEstadoImpl siEstadoRemote;
    @Inject
    private SiCiudadImpl siCiudadRemote;

    
    public SgEstacion guardarEstacion(String sesion, String estacion, String rfcEstacion, String estado, String ciudad, String colonia, String domicilio) {
	SgEstacion e = new SgEstacion();
	e.setNumeroEstacion(estacion);
	e.setRfc(rfcEstacion);
	e.setSiPais(new SiPais(Constantes.PAIS_MEXICO));

	String es = "";
	if (estado.equals("NVL")) {
	    es = "Nuevo León";
	} else if (estado.equals("TAMPS")) {
	    es = "Tamaulipas";
	}

	SiEstado est = siEstadoRemote.findByNameAndSiPais(es, e.getSiPais(), false);
	if (est == null) {
	    try {
		est = siEstadoRemote.save(estado, Constantes.PAIS_MEXICO, sesion);
	    } catch (ExistingItemException ex) {
		Logger.getLogger(SgEstacionImpl.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	e.setSiEstado(est);
	if (est != null) {
	    SiCiudad c = siCiudadRemote.buscarPorNombre(ciudad, est.getId());

	    e.setSiCiudad(c);
	}
	e.setCiudadSinRegistro(ciudad);
	e.setColonia(colonia);
	e.setCalle(domicilio);
	//
	e.setGenero(new Usuario(sesion));
	e.setFechaGenero(new Date());
	e.setHoraGenero(new Date());
	e.setEliminado(Constantes.NO_ELIMINADO);
	//
	create(e);
	return e;
    }

    
    public SgEstacion buscarPorNumero(String numero) {
	try {
	    return (SgEstacion) em.createNamedQuery("SgEstacion.buscarPorNumero").setParameter(1, numero).getSingleResult();
	} catch (Exception e) {
	    UtilLog4j.log.warn(e);
	    return null;
	}
    }

}
