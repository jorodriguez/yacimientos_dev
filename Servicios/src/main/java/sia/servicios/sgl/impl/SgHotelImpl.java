/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.modelo.Proveedor;
import sia.modelo.SgHotel;
import sia.modelo.SgOficina;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgHotelImpl extends AbstractFacade<SgHotel>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    private SgHotelHabitacionImpl hotelHabitacionRemote;
    @Inject
    private SgHotelTipoEspecificoImpl sgHotelTipoEspecificoRemote;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoRemote;
    
    private StringBuilder bodyQuery = new StringBuilder();
    

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgHotelImpl() {
	super(SgHotel.class);
    }

    
    public List<SgHotel> getAllHotel(int sgOficina) {
	UtilLog4j.log.info(this, "Entrando a buscar hoteles");
	List<SgHotel> listReturn = null;
	try {
	    listReturn = em.createQuery("SELECT h FROM SgHotel h "
		    + "WHERE h.sgOficina.id = :sgOficina AND h.eliminado = :eliminado ORDER BY h.proveedor.nombre ASC ").setParameter("eliminado", Constantes.BOOLEAN_FALSE).setParameter("sgOficina", sgOficina).getResultList();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer los hoteles " + e.getMessage());
	}
	return listReturn != null ? listReturn : Collections.EMPTY_LIST;
    }

    
    public void createHotel(Proveedor proveedor, SgHotel sgHotel, int sgOficina, Usuario usuarioGenero) {
	UtilLog4j.log.info(this, "SgHotelImpl.createHotel()");

	sgHotel.setProveedor(proveedor);
	sgHotel.setSgOficina(new SgOficina(sgOficina));
	sgHotel.setEliminado(Constantes.BOOLEAN_FALSE);
	sgHotel.setFechaGenero(new Date());
	sgHotel.setHoraGenero(new Date());
	sgHotel.setGenero(usuarioGenero);
	create(sgHotel);

	//Guardar los Servicios básicos
	List<SgTipoTipoEspecifico> list = this.sgTipoTipoEspecificoRemote.getSgTipoTipoEspecificoBySgTipo(8, true, false, false);

	for (SgTipoTipoEspecifico tte : list) {
	    try {
		if ("Hospedaje".equals(tte.getSgTipoEspecifico().getNombre())) {
		    this.sgHotelTipoEspecificoRemote.save(sgHotel.getId(), tte.getSgTipoEspecifico().getId(), usuarioGenero.getId());
		}
	    } catch (ExistingItemException eie) {
		continue;
	    }
	}
    }

    
    public void deleteHotel(SgHotel sgHotel, Usuario usuarioGenero) {
	try {
	    UtilLog4j.log.info(this, "SgHotelImpl.deleteHotel()");
	    if (sgHotel != null) {
		sgHotel.setEliminado(Constantes.BOOLEAN_TRUE);
		sgHotel.setFechaGenero(new Date());
		sgHotel.setHoraGenero(new Date());
		sgHotel.setGenero(usuarioGenero);
		super.edit(sgHotel);
		UtilLog4j.log.info(this, "se eliminó el hotel");
		//Eliminar habitaciones de ese hotel...
		hotelHabitacionRemote.deleteAllHabitacionesToHotel(sgHotel, usuarioGenero);

	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al eliminar hotel " + e.getMessage());
	}
    }

    
    public void editHotel(SgHotel sgHotel, int sgOficina, Usuario usuarioGenero) {
	try {
	    UtilLog4j.log.info(this, "SgHotelImpl.editHotel()");
	    sgHotel.setSgOficina(new SgOficina(sgOficina));
	    sgHotel.setFechaGenero(new Date());
	    sgHotel.setHoraGenero(new Date());
	    sgHotel.setGenero(usuarioGenero);
	    super.edit(sgHotel);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    
    public boolean buscarHotelRepetido(Proveedor proveedor, int oficina) {
	UtilLog4j.log.info(this, "SgHotelImpl.buscarRepetido()");
	SgHotel h = null;
	try {
	    h = (SgHotel) (em.createQuery("SELECT h FROM SgHotel h "
		    + "WHERE h.sgOficina.id = :sgOficina AND h.proveedor = :proveedor AND h.eliminado = :eliminado").setParameter("eliminado", Constantes.BOOLEAN_FALSE).setParameter("sgOficina", oficina).setParameter("proveedor", proveedor).getSingleResult());

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion .." + e.getMessage());
	    UtilLog4j.log.fatal(this, "retorno");
	    return false;
	}
	UtilLog4j.log.info(this, "Registro encontrado " + h.getEstrellas() + " nombre  " + h.getProveedor().getNombre());
	return true;
    }

}
