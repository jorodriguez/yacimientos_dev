/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcCodigoTarea;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class OcCodigoTareaImpl extends AbstractFacade<OcCodigoTarea> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcCodigoTareaImpl() {
        super(OcCodigoTarea.class);
    }

    
    public OcCodigoTarea buscarPorCodigo(String codigo) {
        try {
            //return em.createNamedQuery("OcCodigoTarea.buscarPorCodigo", OcCodigoTarea.class).setParameter(1, codigo).getSingleResult();
            return (OcCodigoTarea) em.createNativeQuery("SELECT * FROM oc_codigo_tarea WHERE eliminado = false and unaccent(upper(nombre)) LIKE unaccent(upper('" + codigo + "')) order by id limit 1", OcCodigoTarea.class)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }

    
    public OcCodigoTarea guardarCodigo(String codigoTarea, String sesion) {
        OcCodigoTarea ct = new OcCodigoTarea();
        ct.setNombre(codigoTarea);
        ct.setGenero(new Usuario(sesion));
        ct.setFechaGenero(new Date());
        ct.setHoraGenero(new Date());
        ct.setEliminado(Boolean.FALSE);
        //
        create(ct);
        //
        return ct;
    }

    
    public int existeTareaCodigo(String codigo) {
        int existe = 0;
        try {
            OcCodigoTarea obj = this.buscarPorCodigo(codigo);
            if(obj != null && obj.getId() > 0){
                existe = obj.getId();
            }            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            existe = 0;
        }
        return existe;
    }
    
    
    public List<OcCodigoTarea> getAllActive() {
        return em.createQuery("SELECT u FROM OcCodigoTarea u where u.eliminado = false")                
                .getResultList();
    }
}
