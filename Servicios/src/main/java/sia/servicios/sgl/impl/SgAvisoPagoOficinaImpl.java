/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgAvisoPago;
import sia.modelo.SgAvisoPagoOficina;
import sia.modelo.SgAvisoPagoStaff;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgAvisoPagoOficinaImpl extends AbstractFacade<SgAvisoPagoOficina> {

    @Inject
    SgOficinaImpl oficinaService;
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgAvisoPagoOficinaImpl() {
        super(SgAvisoPagoOficina.class);
    }

    
    public void createRelacionAvisoPagoOficina(SgAvisoPago sgAvisoPago, int idOficina, Usuario usuarioGenero) {
        UtilLog4j.log.info(this," sgAvisoPAgoOficinaImpl.createRelacionAvisoPagoOficina");
        try {
            SgAvisoPagoOficina relacion = new SgAvisoPagoOficina();
            relacion.setSgAvisoPago(sgAvisoPago);
            relacion.setSgOficina(oficinaService.find(idOficina));
            relacion.setGenero(usuarioGenero);
            relacion.setFechaGenero(new Date());
            relacion.setHoraGenero(new Date());
            relacion.setEliminado(Constantes.BOOLEAN_FALSE);
            create(relacion);
            UtilLog4j.log.info(this,"se creo la relacion satisfactoriamente  ");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion aqui en rel aviso pago oficina" + e.getMessage());
        }
    }

    
    public void deleteRelacionAvisoOficina(int idAvisoPago, int idOficina, Usuario usuarioGenero) {
        UtilLog4j.log.info(this,"sgAvisoPagoOficinaImpl.deleteAvisoPagoOficina ");
        SgAvisoPagoOficina r = findSgAvisoPagoRepetidoRelacionParaOficina(idOficina, idAvisoPago, Constantes.BOOLEAN_FALSE);
        try {
            r.setEliminado(Constantes.BOOLEAN_TRUE);
            r.setGenero(usuarioGenero);
            r.setFechaGenero(new Date());
            r.setHoraGenero(new Date());
            super.edit(r);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion :" + e.getMessage());
        }
        UtilLog4j.log.info(this,"se elimino correctamente la relacion ");
    }

    
    public SgAvisoPagoOficina findSgAvisoPagoRepetidoRelacionParaOficina(int idOficina, int idAvisoPago, boolean eliminado) {
        UtilLog4j.log.info(this,"Buscar el objeto Aviso en la relacion");
        List<SgAvisoPagoOficina> relacion;
        try {
            relacion =  em.createQuery("SELECT r FROM SgAvisoPagoOficina r "
                    + " WHERE r.sgOficina.id = :idOficina AND "
                    + " r.sgAvisoPago.id = :idAvisoPago AND "
                    + " r.eliminado = :eliminado ").setParameter("idOficina", idOficina).setParameter("idAvisoPago", idAvisoPago).setParameter("eliminado", eliminado).getResultList();
            if (relacion.size() > 0){
                return relacion.get(0);
            } else {
                UtilLog4j.log.info(this,"El objeto buscado es null");
                return null;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this," Excepcion en la consulta " + e.getMessage());
            return null;
        }
    }

    
    public int findCoutSgAvisoPagoRepetidoRelacion(int idAvisoPago) {
        UtilLog4j.log.info(this,"Buscar el objeto Aviso en la relacion");
        List<SgAvisoPagoOficina> lrelacion;
        try {
            lrelacion = em.createQuery("SELECT r FROM SgAvisoPagoOficina r "
                    + " WHERE r.sgAvisoPago.id = :idAvisoPago AND r.sgAvisoPago.eliminado =:eliminado AND "
                    + " r.eliminado = :eliminado ")
                    .setParameter("idAvisoPago", idAvisoPago)
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .getResultList();
            
            if (!lrelacion.isEmpty()) {
                return lrelacion.size();
            } else {
                return 0;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this," Excepcion en la consulta " + e.getMessage());
            return 0;
        }
    }

    
    public List<SgAvisoPago> findAllAvisoPagoToOficina(int idOficina) {
        UtilLog4j.log.info(this,"Entrando a avisos de pago de Oficina");
        List<SgAvisoPago> listReturn = null;
        try {
            listReturn = em.createQuery("SELECT r.sgAvisoPago FROM SgAvisoPagoOficina r "
                    + " WHERE r.sgOficina.id = :idOficina AND r.sgOficina.eliminado = :eliminado AND r.eliminado = :eliminado  ").setParameter("eliminado", Constantes.BOOLEAN_FALSE).setParameter("idOficina", idOficina).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepción ***" + e.getMessage());
        }
        return listReturn;
    }

    
    public List<SgAvisoPago> findAllCatalogoAvisos(int idOficina) {
        UtilLog4j.log.info(this,"sgAvisoPagoOficina.findAllCatalofoAvisos");
        List<SgAvisoPago> listReturn = null;
        try {
            listReturn = em.createQuery("SELECT av FROM SgAvisoPago av "
                    + " WHERE av.eliminado = :eliminado "
                    + " AND av.id NOT IN "
                    + " (SELECT avo.sgAvisoPago.id FROM SgAvisoPagoOficina avo WHERE avo.sgOficina.id = :idOficina AND avo.eliminado = :eliminado) ORDER BY av.sgTipoEspecifico.nombre ASC ").setParameter("idOficina", idOficina).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this,"Excepcion ***" + e.getMessage());
        }
        return listReturn;
    }

    
    public SgAvisoPagoOficina findAvisoPagoRepetido(int idOficina, int idTipoEspecifico) {
        UtilLog4j.log.info(this,"Buscar oficina y tipo especifico");
        SgAvisoPagoStaff relacion;
        List<SgAvisoPagoOficina> ret = null;
        try {
            ret = em.createQuery("SELECT r FROM SgAvisoPagoOficina r "
                    + " WHERE r.sgOficina.id = :idOficina AND "
                    + " r.sgAvisoPago.sgTipoEspecifico.id = :idTipoEspecifico AND "
                    + " r.eliminado = :eliminado ").setParameter("idOficina", idOficina).setParameter("idTipoEspecifico", idTipoEspecifico).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this," Excepcion en la consulta " + e.getMessage());
            return null;
        }
        if (ret.size() > 0) {
            return ret.get(0);
        } else {
            UtilLog4j.log.info(this,"El objeto buscado es null");
            return null;
        }
    }

    
    public SgAvisoPagoOficina buscarPorIdOficinaAndIdTipoPago(int idOficina, int idTipoPago) {
        UtilLog4j.log.info(this,"Buscar el objeto Aviso en la relacion");
        SgAvisoPagoOficina relacion;
        try {
            relacion = (SgAvisoPagoOficina) em.createQuery("SELECT r FROM SgAvisoPagoOficina r "
                    + " WHERE r.sgOficina.id = :idOficina AND "
                    + " r.sgAvisoPago.sgTipoEspecifico.id = :idTipoPago AND "
                    + " r.eliminado = :eliminado ").setParameter("idStaff", idOficina).setParameter("idTipoPago", idTipoPago).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getSingleResult();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this," Excepcion en la consulta " + e.getMessage());
            return null;
        }
        if (relacion != null) {
//            UtilLog4j.log.info(this,"Objeto a retornar " + re.getSgTipoEspecifico().getNombre());
        } else {
            UtilLog4j.log.info(this,"El objeto buscado es null");
        }
        return relacion;
    }

    
    public void activarRelacion(SgAvisoPagoOficina sgAvisoPagoOficina, Usuario usuarioGenero) {
        try {
            sgAvisoPagoOficina.setEliminado(Constantes.BOOLEAN_FALSE);
            sgAvisoPagoOficina.setFechaGenero(new Date());
            sgAvisoPagoOficina.setHoraGenero(new Date());
            super.edit(sgAvisoPagoOficina);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }

    }
}
