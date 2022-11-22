/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgPeriodicidad;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgPeriodicidadImpl extends AbstractFacade<SgPeriodicidad>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgPeriodicidadImpl() {
        super(SgPeriodicidad.class);
    }

    
    public List<SgPeriodicidad> findAllPeriodos() {        
         try {
           return em.createQuery("SELECT p FROM SgPeriodicidad p "
                   + " WHERE p.eliminado = :eli ORDER BY p.nombre ASC")
                   .setParameter("eli", Constantes.BOOLEAN_FALSE)                   
                   .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public void createPeriodicidad(SgPeriodicidad sgPeriodicidad, Usuario usuarioGenero) {
        UtilLog4j.log.info(this,"sgperiodicidad.createPeriodicidad()");
        try{
            sgPeriodicidad.setEliminado(Constantes.BOOLEAN_FALSE);
            sgPeriodicidad.setGenero(usuarioGenero);                        
            sgPeriodicidad.setFechaGenero(new Date());
            sgPeriodicidad.setHoraGenero(new Date());            
            super.create(sgPeriodicidad);
            
            UtilLog4j.log.info(this,"Nombre:" + SgPeriodicidad.class.getName());
            UtilLog4j.log.info(this,"Id: " + sgPeriodicidad.getId());            
            UtilLog4j.log.info(this,"usuario: " + usuarioGenero.getId());            
        }
        catch(Exception ex){
              UtilLog4j.log.fatal(this,"Excepcion en crear periodicidad " + ex.getMessage());
              ex.printStackTrace();
        }
    }
    
    public void editPeriodicidad(SgPeriodicidad sgPeriodicidad, Usuario usuarioModifico) {
        try{
            sgPeriodicidad.setEliminado(Constantes.BOOLEAN_FALSE);
            sgPeriodicidad.setModifico(usuarioModifico);                        
            sgPeriodicidad.setFechaModifico(new Date());
            sgPeriodicidad.setHoraModifico(new Date());            
            super.edit(sgPeriodicidad);            
        }catch(Exception ex){
              UtilLog4j.log.fatal(this,"Excepcion en modificar periodicidad "+ex.getMessage());
        }
    }
    
    public void deletePeriodicidad(int idPeriodicidad, Usuario usuarioGenero) {                
        SgPeriodicidad periodicidad=null;        
        try{
            periodicidad = find(idPeriodicidad);            
            periodicidad.setEliminado(Constantes.BOOLEAN_TRUE);
            super.edit(periodicidad);            
        }catch(Exception ex){
              UtilLog4j.log.fatal(this,"Excepcion en eliminar periodicidad "+ex.getMessage());
        }
    }

    
    public boolean findRepetidoEnAvisosPagos(int idPeriodicidad) {
        List<SgPeriodicidad> ret;        
        try {
            ret= em.createQuery("SELECT av FROM SgAvisoPago av "
                   + " WHERE av.sgPeriodicidad.id = :idPeriodicidad AND av.eliminado = :eliminado")
                   .setParameter("eliminado", Constantes.BOOLEAN_FALSE)                   
                    .setParameter("idPeriodicidad",idPeriodicidad)
                   .getResultList();
            UtilLog4j.log.info(this,"se encontro "+ret.size()+" veces");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en la busqueda de Repetidos en Periodicidades "+e.getMessage());
            return false;
        }
        if(ret!=null && ret.size()>0){
            return true;            
        }else return false;
        
    }

    
    public boolean findNombreRepetido(String nombre) {
        List<SgPeriodicidad> ret;        
        UtilLog4j.log.info(this,"El nombre a buscar es "+nombre.toUpperCase());
        try {
            ret= em.createQuery("SELECT p FROM SgPeriodicidad p "
                   + " WHERE UPPER(p.nombre) = :nombre AND p.eliminado = :eliminado")
                   .setParameter("eliminado", Constantes.BOOLEAN_FALSE)                   
                    .setParameter("nombre",nombre.toUpperCase())
                   .getResultList();
            UtilLog4j.log.info(this,"se encontro "+ret.size()+" veces");
        }catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion en la busqueda de Nombre Repetidos en Periodicidades "+e.getMessage());
            return false;
        }
        if(ret!=null && ret.size()>0){
            return true;            
        }else return false;
    }
}
