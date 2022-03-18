/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.semaforo.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import sia.modelo.SgRolApruebaSolicitud;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgRolApruebaSolicitudImpl extends AbstractFacade<SgRolApruebaSolicitud> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgRolApruebaSolicitudImpl() {
	super(SgRolApruebaSolicitud.class);
    }

    /**
     * Joel rodriguez metodo que sirve para saber si el rol principal del
     * usuario se encuentra en la lista de roles que se autoaprueban solicitudes
     * de viajes..
     *
     * @param idUsuario
     * @return
     */
    
    public boolean verificarUsuarioAutoApruebaSolicitudViaje(String idUsuario) {
	UtilLog4j.log.info(this, "id del usuario a verificar " + idUsuario);
	List re = null;
	try {
	    clearQuery();
	    appendQuery(" select  ur.id");
	    appendQuery(" from si_usuario_rol ur,");
	    appendQuery(" usuario u,");
	    appendQuery(" si_rol r");
	    appendQuery(" where u.id = '");
	    appendQuery(idUsuario);
	    appendQuery("'");
	    appendQuery(" and r.si_modulo = 9");
	    appendQuery(" and ur.IS_PRINCIPAL = 'True'");
	    appendQuery(" and ur.usuario = u.id");
	    appendQuery(" and ur.si_rol = r.id");
	    appendQuery(" and ur.eliminado = 'False'");
	    appendQuery(" and r.id in (select s.SI_ROL from SG_ROL_APRUEBA_SOLICITUD s)");

	    UtilLog4j.log.info(this, "<><><>" + getStringQuery());

	    UtilLog4j.log.info(this, "--------------------------" + getStringQuery());
	    re = em.createNativeQuery(getStringQuery()).getResultList();

	    if (re != null) {
		return !re.isEmpty();
	    } else {
		return false;
	    }

	} catch (NonUniqueResultException e) {
	    UtilLog4j.log.fatal(this, "No se encontro el rol en la tabla de roles que aprueban solicitud " + e.getMessage());
	    return false;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Existio una excepcion al verificar el rol de auto aprueba " + e.getMessage());
	    return false;
	}
    }
}
