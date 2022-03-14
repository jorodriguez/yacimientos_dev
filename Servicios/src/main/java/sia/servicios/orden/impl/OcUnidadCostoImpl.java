/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.OcUnidadCosto;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class OcUnidadCostoImpl extends AbstractFacade<OcUnidadCosto> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcUnidadCostoImpl() {
        super(OcUnidadCosto.class);
    }

    
    public OcUnidadCosto buscarPorNombre(String nombre) {
        try {
            return (OcUnidadCosto) em.createNativeQuery("SELECT * FROM oc_unidad_costo WHERE eliminado = false and unaccent(upper(nombre)) LIKE unaccent(upper('" + nombre + "'))", OcUnidadCosto.class)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }
    
    
    public OcUnidadCosto buscarPorNombreYCodigo(String nombre, String codigo) {
        try {
            return (OcUnidadCosto) em.createNativeQuery("SELECT * FROM oc_unidad_costo WHERE eliminado = false and codigo = '"+codigo+"' and unaccent(upper(nombre)) LIKE unaccent(upper('" + nombre + "'))", OcUnidadCosto.class)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }

    
    public OcUnidadCosto buscarPorCodigo(String codigo) {
        try {
            return (OcUnidadCosto) em.createNativeQuery("SELECT * from oc_unidad_costo where eliminado = false and codigo =  substring('" + codigo + "', 5, 4)").setParameter(1, codigo).getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }
    
    
    public OcUnidadCosto guardarUnidadCosto(String nombre, String codigo, String sesion) {
        OcUnidadCosto nt = new OcUnidadCosto();
        try {
            nt.setNombre(nombre);
            nt.setCodigo(codigo);
            nt.setGenero(new Usuario(sesion));
            nt.setFechaGenero(new Date());
            nt.setHoraGenero(new Date());
            nt.setEliminado(Boolean.FALSE);
            //
            create(nt);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return nt;
    }
    
    
    public int validarUnidadCostoExiste(String codigo, String nombre) {
        UtilLog4j.log.info(this, "#validarUnidadCostoExiste ");
        int existe = 0;
        try {
            String query = "select ID, CODIGO, NOMBRE from oc_unidad_costo where ELIMINADO = false "
                    +" and upper(codigo) = upper('"+codigo+"') "
                    +" and upper(nombre) = upper('"+nombre+"') limit 1" ;

            UtilLog4j.log.info(this, "query" + query);
            
            Object[] lo = (Object[]) em.createNativeQuery(query).getSingleResult();
            
            if (lo != null) {                
                existe = (Integer)lo[0];
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, "Error: al validar la existencia de una unidad de costo " + e.getMessage(), e);
            existe = 0;
        }
        return existe;
    }
    
    
    public List<OcUnidadCosto> getAllActive() {
        return em.createQuery("SELECT u FROM OcUnidadCosto u where u.eliminado = false")                
                .getResultList();
    }

}
