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
import sia.modelo.*;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgColorImpl extends AbstractFacade<SgColor> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgColorImpl() {
        super(SgColor.class);
    }

    
    public List<SgColor> getAllColors(boolean eliminado) {
        UtilLog4j.log.info(this, "SgColorImpl.getAlColor()");
        List<SgColor> colorList = null;
        try {
            colorList = em.createQuery("SELECT c FROM SgColor c WHERE c.eliminado = :eliminado ORDER BY c.nombre ASC").setParameter("eliminado", eliminado).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existió un error al consultar los colores" + e.getMessage());
            return null;
        }

        return colorList;
    }

    
    public void guardarColor(Usuario usuario, SgColor sgColor) {
        UtilLog4j.log.info(this, "guardarCOlor"+sgColor.getNombre());
            sgColor.setGenero(usuario);
            sgColor.setFechaGenero(new Date());
            sgColor.setHoraGenero(new Date());
            sgColor.setEliminado(Constantes.BOOLEAN_FALSE);
            super.create(sgColor);        
    }
    
    
    public void modificarColor(Usuario usuario, SgColor sgColor) {
        try{
            UtilLog4j.log.info(this, "modificarCOlor"+sgColor.getNombre());
            UtilLog4j.log.info(this, "existe se modifico");                        
            sgColor.setModifico(usuario);
            sgColor.setFechaModifico(new Date());
            sgColor.setHoraModifico(new Date());            
            super.edit(sgColor);
        }catch(Exception e){
            UtilLog4j.log.fatal(this, "excepcion en modificar color "+e.getMessage());
        }
    }
    

    
    public void eliminarColor(Usuario usuario, SgColor sgColor, boolean eliminado) {
        sgColor.setGenero(usuario);
        sgColor.setFechaGenero(new Date());
        sgColor.setHoraGenero(new Date());
        sgColor.setEliminado(eliminado);
        edit(sgColor);        
    }

    
    public SgColor buscarPorNombre(String nombre) {
            UtilLog4j.log.info(this, "buscarporNombre"+nombre);
        try {
            return (SgColor) em.createQuery("SELECT c FROM SgColor c WHERE c.nombre = :nombre AND c.eliminado = :eli")
                    .setParameter("eli", Constantes.BOOLEAN_FALSE)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion al buscar color por nombre "+e.getMessage());
            return null;
        }
    }

    private SgColor buscarPorNombreEliminado(String nombre) {
            UtilLog4j.log.info(this, "buscarPor nombreEliminado");
        try {
            return (SgColor) em.createQuery("SELECT m FROM SgColor m WHERE m.nombre = :nombre AND m.eliminado = :eli").setParameter("eli", Constantes.ELIMINADO).setParameter("nombre", nombre).getSingleResult();
        }catch (Exception e) {
            UtilLog4j.log.fatal(this, "excepcion al buscar nombre eliminado "+e.getMessage());
            return null;
        }
    }

    
    public boolean buscarColorOcupado(int idColor) {
        UtilLog4j.log.info(this, "buscrCOlorOcupado");
        SgColor color=null;
        List<SgVehiculo> listVe=null;
        try{
             listVe = em.createQuery("SELECT v FROM SgVehiculo v WHERE v.sgColor.id = :idColor AND v.eliminado = :eli")
             .setParameter("eli", Constantes.BOOLEAN_FALSE)
             .setParameter("idColor", idColor).getResultList();
             
             if(listVe.size()>0){
                 UtilLog4j.log.info(this, "Se encontro en la relacion ");
                return true;
             }else{UtilLog4j.log.info(this, "NO se encontro en la relacion "); return false;}
    
        }catch(Exception e){
            UtilLog4j.log.fatal(this, "Excepcion en "+e.getMessage());
            return false;
        }
    }
}
