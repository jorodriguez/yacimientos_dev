/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sia.servicios.comunicacion.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.*;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jorodriguez
 */
@LocalBean 
public class SiNotificacionImpl extends AbstractFacade<SiNotificacion>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject 
    private SiCodificacionImpl siCondificacionService;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiNotificacionImpl() {
        super(SiNotificacion.class);
    }

    
    public boolean createNotificacion(SiNotificacion siNotificacion, SiTipoNotificacion siTipoNotificacion,Usuario usuario){
        UtilLog4j.log.info(this,"createNotificacion");
     try{   
            //crear codigo 
            siNotificacion.setSiTipoNotificacion(siTipoNotificacion);
            siNotificacion.setCodigo(siCondificacionService.getCodigo(siTipoNotificacion.getSiCodificacion(), usuario));
            siNotificacion.setEnviada(Constantes.BOOLEAN_FALSE);
            siNotificacion.setGenero(usuario);
            siNotificacion.setFechaGenero(new Date());
            siNotificacion.setHoraGenero(new Date());
            siNotificacion.setEliminado(Constantes.BOOLEAN_FALSE);
            create(siNotificacion);
            siCondificacionService.ponerUsada(siTipoNotificacion.getSiCodificacion(), usuario);
            UtilLog4j.log.info(this,"Todo bien al guardar siNotificacion");
            return true;
        }catch(Exception e){
            UtilLog4j.log.fatal(this,"Excepcion en crear siNotificacion "+e.getMessage());
            return true;
        }    
    }

     
    public boolean updateNotificacion(SiNotificacion siNotificacion, SiTipoNotificacion siTipoNotificacion, Usuario usuario) {
        try{   
            UtilLog4j.log.info(this,"find ok");
            siNotificacion.setCodigo(siCondificacionService.getCodigo(siTipoNotificacion.getSiCodificacion(), usuario));
            UtilLog4j.log.info(this,"codigo OK");
            UtilLog4j.log.info(this,"sitipo "+siTipoNotificacion.getNombre());
            siNotificacion.setSiTipoNotificacion(siTipoNotificacion);
            siNotificacion.setModifico(usuario);
            siNotificacion.setFechaModifico(new Date());
            siNotificacion.setHoraModifico(new Date());            
            edit(siNotificacion);
            UtilLog4j.log.info(this,"Todo bien al modificar siNotificacion");
            return true;
        }catch(Exception e){
            UtilLog4j.log.fatal(this,"Excepcion en modificar siNotificacion "+e.getMessage());
            return true;
        }    
     }
     
    
    public void eliminarNotificacion(SiNotificacion siNotificacion, Usuario usuario) {
         try{   
            siNotificacion.setEliminado(Constantes.BOOLEAN_TRUE);
            edit(siNotificacion);
            UtilLog4j.log.info(this,"Todo bien al eliminar notificacion");
        }catch(Exception e){
            UtilLog4j.log.fatal(this,"Excepcion en eliminar notificacion"+e.getMessage());
        }    
    }

    
    public List<SiNotificacion> findAllNotificacion(Usuario usuario,String estado) {
         try {
           return em.createQuery("SELECT n FROM SiNotificacion n "
                   + " WHERE n.genero =:usuarioSesion AND n.enviada =:estado AND  n.eliminado = :eli ORDER BY n.id ASC")
                   .setParameter("usuarioSesion", usuario)                   
                   .setParameter("estado",estado)                   
                   .setParameter("eli", Constantes.BOOLEAN_FALSE)                   
                   .getResultList();
        } catch (Exception e) {
             UtilLog4j.log.fatal(this,"Excepcion en la consulta de codificaciones "+e.getMessage());
            return null;
        }
    }

    
    public void notificacionEnviada(SiNotificacion siNotificacion, Usuario usuario) {
        try{   
            siNotificacion.setEnviada(Constantes.BOOLEAN_TRUE);
            siNotificacion.setModifico(usuario);
            siNotificacion.setFecha(new Date());
            siNotificacion.setHoraModifico(new Date());
            edit(siNotificacion);
            UtilLog4j.log.info(this,"Todo bien al poner como enviada la notificacion");
        }catch(Exception e){
            UtilLog4j.log.fatal(this,"Excepcion en poner como enviada la notificacion"+e.getMessage());
        }   
    }

}
