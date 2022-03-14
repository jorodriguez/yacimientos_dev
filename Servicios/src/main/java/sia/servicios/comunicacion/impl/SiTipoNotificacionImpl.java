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
import sia.modelo.SiCodificacion;
import sia.modelo.SiTipoNotificacion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@LocalBean 
public class SiTipoNotificacionImpl extends AbstractFacade<SiTipoNotificacion>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiTipoNotificacionImpl() {
        super(SiTipoNotificacion.class);
    }

    
    public void createTipoNotificacion(SiTipoNotificacion siTipoNotificacion,SiCodificacion siCodificacion,Usuario usuario) {
       try{           
            siTipoNotificacion.setSiCodificacion(siCodificacion);
            siTipoNotificacion.setGenero(usuario);
            siTipoNotificacion.setFechaGenero(new Date());
            siTipoNotificacion.setHoraGenero(new Date());
            siTipoNotificacion.setEliminado(Constantes.BOOLEAN_FALSE);
            create(siTipoNotificacion);
            UtilLog4j.log.info(this, "Todo bien al guardar siTipoNotificacion");
        }catch(Exception e){
            UtilLog4j.log.fatal(this, "Excepcion en crear siTipoNotificacion"+e.getMessage());
        }
    }

    
    public void updateTipoNotificacion(SiTipoNotificacion siTipoNotificacion, Usuario usuarioModifico) {
         try{            
            siTipoNotificacion.setModifico(usuarioModifico);
            siTipoNotificacion.setFechaModifico(new Date());
            siTipoNotificacion.setHoraModifico(new Date());
            siTipoNotificacion.setEliminado(Constantes.BOOLEAN_FALSE);
            edit(siTipoNotificacion);
            UtilLog4j.log.info(this, "Todo bien al modificar siTipoNotificacion");
        }catch(Exception e){
            UtilLog4j.log.fatal(this, "Excepcion en modificar siTipoNotificacion"+e.getMessage());
        }
    }

    
    public void deleteTipoNotificacion(SiTipoNotificacion siTipoNotificacion, Usuario usuario) {
         try{            
            siTipoNotificacion.setEliminado(Constantes.BOOLEAN_TRUE);
            edit(siTipoNotificacion);
            UtilLog4j.log.info(this, "Todo bien al eliminar siTipoNotificacion");
        }catch(Exception e){
            UtilLog4j.log.fatal(this, "Excepcion en eliminar siTipoNotificacion"+e.getMessage());
        }
    }

    
    public List<SiTipoNotificacion> findAllTiposNotificacion(Usuario usuarioSesion) {
        UtilLog4j.log.info(this, "Usuario en session "+usuarioSesion.getNombre());
      try {
           return em.createQuery("SELECT t FROM SiTipoNotificacion t "
                   + " WHERE t.genero = :usuarioSesion AND t.eliminado = :eli ORDER BY t.id ASC")
                   .setParameter("usuarioSesion", usuarioSesion)                   
                   .setParameter("eli", Constantes.BOOLEAN_FALSE)                   
                   .getResultList();
        } catch (Exception e) {
             UtilLog4j.log.fatal(this, "Excepcion en la consulta de tipo notificacion "+e.getMessage());
            return null;
        }
    }

}
