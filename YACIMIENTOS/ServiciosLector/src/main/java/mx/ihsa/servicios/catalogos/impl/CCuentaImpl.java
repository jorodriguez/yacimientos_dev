package mx.ihsa.servicios.catalogos.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mx.ihsa.constantes.Constantes;
import org.jooq.DSLContext;
import mx.ihsa.modelo.CCuenta;
import mx.ihsa.util.UtilLog4j;

@Stateless 
public class CCuentaImpl {

    @PersistenceContext(unitName = Constantes.PERSISTENCE_UNIT )
    private EntityManager em;

    @Inject
    DSLContext dbCtx;

    
    public void create(CCuenta ccuenta) {
        em.persist(ccuenta);
    }

    
    public void edit(CCuenta ccuenta) {
        em.merge(ccuenta);
    }

    
    public void remove(CCuenta ccuenta) {
        em.remove(em.merge(ccuenta));
    }

    
    public CCuenta find(Object id) {
        return em.find(CCuenta.class, id);
    }
    
    
    public List<CCuenta> findAll() {
        return em.createQuery("select object(o) from CCuenta as o").getResultList();
    }

    
    public List<CCuenta> getAll() {
        return dbCtx.fetch("SELECT * FROM CCuenta where eliminado = false").into(CCuenta.class);
    }

    
    public CCuenta buscarPorNombre(Object nombreCompañia) {
        return (CCuenta) em.createQuery("SELECT c FROM CCuenta c WHERE c.nombre = :nombre")
                .setParameter("nombre", nombreCompañia)
                .getSingleResult();

    }

    public byte[] traeLogo(String rfcCCuenta) {
        try {
            String sb = " select a.rfc, a.logo "
                    + " from COMPANIA a "
                    + " where a.rfc = '" + rfcCCuenta + "' "
                    + " AND a.ELIMINADO =  'False' ";

            Object[] c = (Object[]) em.createNativeQuery(sb).getSingleResult();
            return (byte[]) c[1];
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return null;
        }
    }

    
    public CCuenta buscarPorRFC(String rfcCompañia) {
        try {
            return (CCuenta) em.createQuery("SELECT c FROM CCuenta c WHERE c.rfc = :nombre", CCuenta.class)
                    .setParameter("nombre", rfcCompañia)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    /*public CCuentaVo traerPorRFC(String rfcCompañia) {
        try {
            String c = "SELECT c.rfc, c.nombre, c.requisito_factura FROM CCuenta c WHERE c.rfc = ? ";
            Object[] obj = (Object[]) em.createNativeQuery(c).setParameter(1, rfcCompañia).getSingleResult();
            CCuentaVo com = new CCuentaVo();
            com.setRfcCCuenta((String) obj[0]);
            com.setNombre((String) obj[1]);
            com.setRequisitoFactura((String) obj[2]);
            return com;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
*/
}
