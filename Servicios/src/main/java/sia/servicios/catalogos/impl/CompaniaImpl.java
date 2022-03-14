/*
 * CompaniaImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.catalogos.impl;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import sia.constantes.Constantes;
import sia.modelo.Compania;
import sia.modelo.sgl.vo.SgTarjetaBancariaVo;
import sia.modelo.vo.CompaniaVo;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com
 * @date 7/07/2009
 */
@LocalBean 
public class CompaniaImpl {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
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

    
    public SgTarjetaBancariaVo getTarjetaBancaria(String rfc) {
        StringBuilder bodyQuery = new StringBuilder();
        bodyQuery.append("SELECT c.nombre,tb.numero_tarjeta,tb.codigo_seguridad,tb.FECHA_VENCIMIENTO FROM SG_TARJETA_BANCARIA tb, COMPANIA c WHERE tb.compania = c.rfc AND ");
        bodyQuery.append("tb.compania = '");
        bodyQuery.append(rfc);
        bodyQuery.append("' ");
        Query query = em.createNativeQuery(bodyQuery.toString());

        Object[] result = (Object[]) query.getSingleResult();

        SgTarjetaBancariaVo tarjeta = new SgTarjetaBancariaVo();
        tarjeta.setBeneficiario((String) result[0]);
        tarjeta.setNumeroTarjeta((String) result[1]);
        tarjeta.setCodigo((String) result[2]);
        tarjeta.setFechaVencimiento((result[3] != null) ? (String) result[3] : Constantes.FECHA_DEFAULT_TARJETA_CREDITO);
        return tarjeta;
    }

    
    public List<SelectItem> traerCompaniasByUsuario(String usuarioID) {
        List<SelectItem> le = null;
        try {
            String sb = " select a.NOMBRE, a.RFC "
                    + " from COMPANIA a "
                    + " inner join AP_CAMPO c on c.COMPANIA = a.RFC and c.ELIMINADO = 'False' "
                    + " inner join AP_CAMPO_USUARIO_RH_PUESTO u on u.AP_CAMPO =  c.ID and u.ELIMINADO = 'False' "
                    + " where u.USUARIO = '" + usuarioID + "' "
                    + " AND a.ELIMINADO =  'False' "
                    + " group by a.NOMBRE, a.RFC ";

            List< Object[]> lo = em.createNativeQuery(sb).getResultList();
            le = new ArrayList<>();
            for (Object[] objects : lo) {
                le.add(new SelectItem((String) objects[1], (String) objects[0]));
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            le = null;
        }
        return le;
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
