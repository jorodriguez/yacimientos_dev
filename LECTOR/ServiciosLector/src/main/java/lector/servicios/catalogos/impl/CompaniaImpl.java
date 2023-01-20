package lector.servicios.catalogos.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lector.constantes.Constantes;
import lector.dominio.vo.CompaniaVo;
import org.jooq.DSLContext;
import lector.modelo.Compania;
import lector.util.UtilLog4j;

/**
 */
@Stateless 
public class CompaniaImpl {

    @PersistenceContext(unitName = Constantes.PERSISTENCE_UNIT )
    private EntityManager em;

    @Inject
    DSLContext dbCtx;

    
    public void create(Compania compania) {
        em.persist(compania);
    }

    
    public void edit(Compania compania) {
        em.merge(compania);
    }

    
    public void remove(Compania compania) {
        em.remove(em.merge(compania));
    }

    
    public Compania find(Object id) {
        return em.find(Compania.class, id);
    }

    
    public List<Compania> findAll() {
        return em.createQuery("select object(o) from Compania as o").getResultList();
    }

    
    public List<Compania> getAll() {
        return dbCtx.fetch("SELECT * FROM Compania where eliminado = false").into(Compania.class);
    }

    
    public Compania buscarPorNombre(Object nombreCompañia) {
        return (Compania) em.createQuery("SELECT c FROM Compania c WHERE c.nombre = :nombre")
                .setParameter("nombre", nombreCompañia)
                .getSingleResult();

    }

    public byte[] traeLogo(String rfcCompania) {
        try {
            String sb = " select a.rfc, a.logo "
                    + " from COMPANIA a "
                    + " where a.rfc = '" + rfcCompania + "' "
                    + " AND a.ELIMINADO =  'False' ";

            Object[] c = (Object[]) em.createNativeQuery(sb).getSingleResult();
            return (byte[]) c[1];
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            return null;
        }
    }

    
    public Compania buscarPorRFC(String rfcCompañia) {
        try {
            return (Compania) em.createQuery("SELECT c FROM Compania c WHERE c.rfc = :nombre", Compania.class)
                    .setParameter("nombre", rfcCompañia)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public CompaniaVo traerPorRFC(String rfcCompañia) {
        try {
            String c = "SELECT c.rfc, c.nombre, c.requisito_factura FROM Compania c WHERE c.rfc = ? ";
            Object[] obj = (Object[]) em.createNativeQuery(c).setParameter(1, rfcCompañia).getSingleResult();
            CompaniaVo com = new CompaniaVo();
            com.setRfcCompania((String) obj[0]);
            com.setNombre((String) obj[1]);
            com.setRequisitoFactura((String) obj[2]);
            return com;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

}
