/*
 * EtsServicioImpl.java
 * Creado el 7/07/2009, 08:47:52 AM
 * EJB sin estado desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este EJB sin estado (Stateless Session EJB), asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@mpg-ihsa.com.mx o a: hacosta.0505@gmail.com
 */
package sia.servicios.requisicion.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.modelo.*;
import sia.modelo.requisicion.vo.RequisicionEtsVo;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

@LocalBean 
public class ReRequisicionEtsImpl extends AbstractFacade<ReRequisicionEts> {

    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    @Inject
    private DSLContext dbCtx;

    public ReRequisicionEtsImpl() {
        super(ReRequisicionEts.class);
    }
    
            
                
    
    public void crear(Requisicion requisicion, SiAdjunto siAdjunto, Usuario usuario, boolean visible) {
        UtilLog4j.log.info(this,"crear");
        
        try {
            ReRequisicionEts reRequisicion = new ReRequisicionEts();
            reRequisicion.setDisgregado(Constantes.BOOLEAN_FALSE);
            reRequisicion.setEliminado(Constantes.BOOLEAN_FALSE);
            reRequisicion.setGenero(usuario);
            reRequisicion.setFechaGenero(new Date());
            reRequisicion.setHoraGenero(new Date());
            reRequisicion.setRequisicion(requisicion);
            reRequisicion.setSiAdjunto(siAdjunto);
            reRequisicion.setVisible(visible);
            create(reRequisicion);
        
            UtilLog4j.log.info(this,"se creó la relacion entre requisicion y siAdjunto");
                        
        } catch (Exception e) {
            UtilLog4j.log.info(this,"Excepcion al crear relación entre ReRequisiciomn " + e.getMessage());
        }
    }

    
    public void crear(Requisicion requisicion, SiAdjunto siAdjunto, Usuario usuario) {
       this.crear(requisicion, siAdjunto, usuario, Constantes.BOOLEAN_TRUE);
    }

    
    public void eliminarReRequisicion(ReRequisicionEts reRequisicion, Usuario usuario) {
        UtilLog4j.log.info(this,"eliminarReRequisicion");
        
        try {
        
            reRequisicion.setEliminado(Constantes.BOOLEAN_TRUE);
            reRequisicion.setFechaModifico(new Date());
            reRequisicion.setHoraModifico(new Date());
            reRequisicion.setModifico(usuario);
            edit(reRequisicion);
            
            UtilLog4j.log.info(this,"Se elimino el registro de ReRequisicion? " + reRequisicion.isEliminado());            
            
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
    }

    
    public List<ReRequisicionEts> traerAdjuntosPorRequisicion(boolean disgregado, int idRequisicion) {
        UtilLog4j.log.info(this,"Disgreado = " + disgregado);
        
        List<ReRequisicionEts> retVal = Collections.emptyList();
        
        try {
            retVal = em.createQuery("SELECT r FROM ReRequisicionEts r "
                    + " WHERE r.eliminado = :eliminado AND r.disgregado = :disgregado "
                    + " AND r.visible = :visible "
                    + " AND r.requisicion.id = :idRequisicion "
                    + " ORDER BY r.id ASC").setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .setParameter("disgregado", disgregado)
                    .setParameter("visible", Constantes.BOOLEAN_TRUE)
                    .setParameter("idRequisicion", idRequisicion).getResultList();
            
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        
        return retVal;
    }
    
    
    public List<RequisicionEtsVo> traerAdjuntosPorRequisicion(String consecutivo) {
        UtilLog4j.log.info(this,"Consecutivo = " + consecutivo);
        System.out.println("Consecutivo = " + consecutivo);
        
        final String query = new StringBuilder("SELECT rets.id as id_re_requisicion,")
                                                        .append(" r.id as id_requisicion,")
                                                        .append(" ad.id as id_adjunto, rets.disgregado,  rets.visible,")
                                                        .append(" r.consecutivo,")
                                                        .append(" ad.url,ad.nombre as nombre_adjunto, ad.descripcion descripcion_adjunto,")
                                                        .append(" ad.tipo_archivo,ad.peso,ad.uuid ")
                                                        .append(" FROM re_requisicion_ets rets inner join requisicion r on r.id = rets.requisicion   ")
                                                        .append(" inner join si_adjunto ad on ad.id = rets.si_adjunto")
                                                        .append(" WHERE ")
                                                        .append("  r.eliminado = false AND rets.eliminado = false AND ad.eliminado = false ")
                                                        .append(" AND r.consecutivo = ? ").toString();            
                
        List<RequisicionEtsVo> listaEts = Collections.emptyList();
        
        try {
            
            System.out.println(query);    
            
            listaEts = dbCtx.fetch(query,consecutivo).into(RequisicionEtsVo.class);
            
        } catch (DataAccessException e) {
            System.out.println("error "+e);
            UtilLog4j.log.fatal(this, e);            
        }
        
        return listaEts;
    }

    
    public List<ReRequisicionEts> traerAdjuntosPorRequisicion(int idRequisicion) {
        
        List<ReRequisicionEts> retVal = null;

        try {
            retVal = em.createQuery("SELECT r FROM ReRequisicionEts r "
                    + " WHERE r.eliminado = :eliminado "
                    + " AND r.requisicion.id = :idRequisicion "
                    + " AND r.visible = :visible "
                    + " ORDER BY r.id ASC").setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .setParameter("visible", Constantes.BOOLEAN_TRUE).setParameter("idRequisicion", idRequisicion).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        } finally {
            if(retVal == null) {
                retVal = Collections.emptyList();
            }
        }
        
        return retVal;
    }

    
    public void ponerDisgregada(ReRequisicionEts requisicion, Usuario usuario, boolean estado) {
        UtilLog4j.log.info(this,"ponerDisgregada");

        try {
            requisicion.setDisgregado(estado);
            requisicion.setModifico(usuario);
            requisicion.setFechaModifico(new Date());
            requisicion.setHoraModifico(new Date());
            edit(requisicion);

            UtilLog4j.log.info(this,"Estado disgregada");

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
    }

    
    public boolean disgregarAdjuntosPorRequisicion(int idRequisicion, Usuario usuario) {
        UtilLog4j.log.info(this,"####disgregarAdjuntosPorRequisicion {0}",  new Object[]{idRequisicion});
        
        boolean retVal = false;
        
        try {
            List<ReRequisicionEts> lista = traerAdjuntosPorRequisicion(Constantes.BOOLEAN_TRUE, idRequisicion);
            if (lista != null) {
                for (ReRequisicionEts rere : lista) {
                    ponerDisgregada(rere, usuario, Constantes.BOOLEAN_FALSE);
                    UtilLog4j.log.info(this,"relacion " + rere.getSiAdjunto().getId());
                }
            }
            
            retVal = true;
            
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        
        return retVal;
    }

    
    public ReRequisicionEts buscarPorRequisicionAdjunto(Requisicion req, SiAdjunto siAdjunto) {
        ReRequisicionEts retVal = null;
        
        try {
            UtilLog4j.log.info(this,"req : {0} - siAdjunto : {1}", new Object[]{req.getConsecutivo(), siAdjunto.getNombre()});
            
            retVal = (ReRequisicionEts) em.createQuery("SELECT r FROM ReRequisicionEts r "
                    + " WHERE  r.siAdjunto.id = :adj "
                    + " AND r.requisicion.id = :req "
                    + " AND r.eliminado = :eliminado "
                    + " ORDER BY r.id ASC")
                    .setParameter("adj", siAdjunto.getId())
                    .setParameter("req", req.getId())
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList().get(0);
                    //.setParameter("disgregado", disgregado).getResultList().get(0);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        
        return retVal;
    }

    
    public List<ReRequisicionEts> traerAdjuntosPorRequisicionVisible(int idRequisicion, boolean visible) {
        UtilLog4j.log.info(this,"Adjunto por categoria ");
        
        List<ReRequisicionEts> retVal = Collections.emptyList();
        
        try {

            Query q = em.createQuery("SELECT r FROM ReRequisicionEts r "
                    + " WHERE r.eliminado = :eliminado "
                    + " AND r.requisicion.id = :idRequisicion "
                    + " AND r.visible = :boVisible"
                    + " ORDER BY r.id ASC")
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .setParameter("idRequisicion", idRequisicion)
                    .setParameter("boVisible", visible);
            
            UtilLog4j.log.info(this,"Query de traerAdjuntosPorRequisicionCategoria" + q.toString());
            
            retVal = q.getResultList();
            
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        
        return retVal;
    }
    
    
    public List<ReRequisicionEts> traerAdjuntosPorRequisicionVisibleTipo(int idRequisicion, boolean visible, String tipo) {
        UtilLog4j.log.info(this,"Adjunto por tipo ");
        
        List<ReRequisicionEts> retVal = Collections.emptyList();
        
        try {
            
            String que = "SELECT r  "
                    + " FROM ReRequisicionEts r "                    
                    + " WHERE r.eliminado = :eliminado "
                    + " AND r.requisicion.id = :idRequisicion "
                    + " AND r.visible = :boVisible"
                    + " AND r.siAdjunto.tipoElemento = :tipoE "
                    + " ORDER BY r.id ASC";

            Query q = em.createQuery(que)                    
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .setParameter("idRequisicion", idRequisicion)
                    .setParameter("boVisible", visible)
                    .setParameter("tipoE", tipo);
            
            UtilLog4j.log.info(this,"Query de traerAdjuntosPorRequisicionCategoria" + q.toString());
            
            retVal = q.getResultList();
            
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        
        return retVal;
    }
}
