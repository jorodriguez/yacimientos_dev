package sia.servicios.orden.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcCategoriaEts;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

@Stateless 
public class OcCategoriaEtsImpl extends AbstractFacade<OcCategoriaEts> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public OcCategoriaEtsImpl() {
	super(OcCategoriaEts.class);
    }

    
    public void crearOcCategproaEts(OcCategoriaEts ocCategoriaEts, Usuario usuario) {
	UtilLog4j.log.info(this, "crearOrdenCategoriaEts");
	try {
	    ocCategoriaEts.setEliminado(Constantes.BOOLEAN_FALSE);
	    ocCategoriaEts.setGenero(usuario);
	    ocCategoriaEts.setFechaGenero(new Date());
	    ocCategoriaEts.setHoraGenero(new Date());
	    create(ocCategoriaEts);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al crear ocCategoria " + e.getMessage());
	}
    }

    
    public void eliminarOcCategoriaEts(OcCategoriaEts ocCategoriaEts, Usuario usuario) {
	UtilLog4j.log.info(this, "eliminarReRequisicion");
	try {
	    ocCategoriaEts.setEliminado(Constantes.BOOLEAN_TRUE);
	    ocCategoriaEts.setFechaModifico(new Date());
	    ocCategoriaEts.setHoraModifico(new Date());
	    ocCategoriaEts.setModifico(usuario);
	    edit(ocCategoriaEts);
	    UtilLog4j.log.info(this, "se elimino el registro de ocCategoriaEts");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al eliminar ocCategoriaEts " + e.getMessage());
	}
    }

    
    public List<OcCategoriaEts> traerOcCategoriaEts() {
	try {
	    UtilLog4j.log.info(this, " traerCAtegorias");
	    return em.createQuery("SELECT o FROM OcCategoriaEts o "
		    + " WHERE o.eliminado = :eliminado "
		    + " ORDER BY o.id ASC")
		    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
		    .getResultList();

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en traer OcCategoriaEts" + e.getMessage());
	    return null;
	}
    }
}
