/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CoComentario;
import sia.modelo.CoComentarioMeGusta;
import sia.modelo.CoMeGusta;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;
//import sia.servicios.comunicacion.CoMegustaRemote;

/**
 *
 * @author hacosta
 */
@LocalBean 
public class CoComentarioMeGustaImpl extends AbstractFacade<CoComentarioMeGusta> {
   
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoComentarioMeGustaImpl() {
        super(CoComentarioMeGusta.class);
    }

    
    public boolean crearMeGusta(CoComentario comentario,CoMeGusta meGusta,String idUsuario) {        
         try{
            CoComentarioMeGusta relacion = new CoComentarioMeGusta();
            relacion.setGenero(new Usuario(idUsuario));
            relacion.setCoComentario(comentario);
            relacion.setCoMeGusta(meGusta);
            relacion.setEliminado(Constantes.BOOLEAN_FALSE);
            relacion.setFechaGenero(new Date());
            relacion.setHoraGenero(new Date());  
            create(relacion);
            return true;
         }catch(Exception e){
            UtilLog4j.log.error(e);
             return false;
         }
    }

    
    public boolean eliminarMeGusta(Integer idComentario, Integer idMeGusta, String idUsuario) {
        try {
            CoComentarioMeGusta relacion = findRelacionFromIdMegusta(idComentario,idMeGusta);
            if (relacion != null) {
                relacion.setModifico(new Usuario(idUsuario));
                relacion.setFechaModifico(new Date());
                relacion.setHoraModifico(new Date());
                relacion.setEliminado(Constantes.BOOLEAN_TRUE);
                edit(relacion);                
            }
            return true;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }
    }
    
     private CoComentarioMeGusta findRelacionFromIdMegusta(Integer idComentario,Integer idMegusta) {
        return (CoComentarioMeGusta) em.createQuery("SELECT r FROM CoComentarioMeGusta r "
                + "WHERE r.coComentario.id = :idComentario AND r.coMeGusta.id = :idMeGusta AND r.eliminado = :FALSE ")
                .setParameter("idComentario", idComentario).setParameter("idMeGusta", idMegusta).setParameter("FALSE", Constantes.BOOLEAN_FALSE).getSingleResult();
    }

    
}
