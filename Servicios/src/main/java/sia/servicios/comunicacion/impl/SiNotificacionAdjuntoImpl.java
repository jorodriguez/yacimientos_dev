/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiAdjunto;
import sia.modelo.SiNotificacion;
import sia.modelo.SiNotificacionAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jorodriguez
 */
@LocalBean 
public class SiNotificacionAdjuntoImpl extends AbstractFacade<SiNotificacionAdjunto>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiNotificacionAdjuntoImpl() {
        super(SiNotificacionAdjunto.class);
    }

    
    public void addArchivoNotificacionAdjunto(SiNotificacion siNotificacion, SiAdjunto siAdjutno, Usuario usuario) {
        try {
            SiNotificacionAdjunto siNotificacionAdjunto = new SiNotificacionAdjunto();
            siNotificacionAdjunto.setSiAdjunto(siAdjutno);
            siNotificacionAdjunto.setSiNotificacion(siNotificacion);
            siNotificacionAdjunto.setGenero(usuario);
            siNotificacionAdjunto.setFechaGenero(new Date());
            siNotificacionAdjunto.setHoraGenero(new Date());
            siNotificacionAdjunto.setEliminado(Constantes.BOOLEAN_FALSE);
            create(siNotificacionAdjunto);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    
    public List<SiNotificacionAdjunto> findAllNotificacionAdjuntoToNotificacion(SiNotificacion siNotificacion) {
        try {
            return em.createQuery("SELECT n FROM SiNotificacionAdjunto n "
                    + " WHERE n.siNotificacion = :siNotificacion AND n.eliminado = :eli ORDER BY n.id ASC")
                    .setParameter("siNotificacion", siNotificacion)
                    .setParameter("eli", Constantes.BOOLEAN_FALSE)
                    .getResultList();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

    
    public void deleteArchivoNotificacionAdjunto(SiNotificacionAdjunto siNotifiacionAdjunto, Usuario usuarioModifico) {
        try {
            siNotifiacionAdjunto.setEliminado(Constantes.BOOLEAN_TRUE);
            edit(siNotifiacionAdjunto);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

}
