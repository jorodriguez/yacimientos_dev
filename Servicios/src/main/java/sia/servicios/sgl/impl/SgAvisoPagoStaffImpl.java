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
import sia.modelo.SgAvisoPagoStaff;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgAvisoPagoStaffImpl extends AbstractFacade<SgAvisoPagoStaff> {

    @Inject
    SgStaffImpl staffService;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgAvisoPagoStaffImpl() {
        super(SgAvisoPagoStaff.class);
    }

    /*
     * @Descripcion: Metodo que consulta la realcion entre SgAvisoPago y
     * SgAvisoPagoStaff y me retorna una coleccion de Registros de SgAvisosPagos
     *
     */
    
    public List<SgAvisoPago> findAllAvisoPagoToStaff(int idStaff) {
        UtilLog4j.log.info(this, "Entrando a avisos de pago de staff");
        List<SgAvisoPago> listReturn = null;
        try {
            //AND .eliminado = :eliminado 
            listReturn = em.createQuery("SELECT r.sgAvisoPago FROM SgAvisoPagoStaff r "
                    + " WHERE r.sgStaff.id = :idStaff AND r.sgStaff.eliminado = :eliminado AND r.eliminado = :eliminado ").setParameter("eliminado", Constantes.BOOLEAN_FALSE).setParameter("idStaff", idStaff).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion ***" + e.getMessage());
        }
        return listReturn;
    }

    /*
     * Busco en la relacion y compar el id del staff y que la relacion no este
     * eliminada. Si se encuentra comparo
     * idPeriodicidad,idTipoEspecifico,diaEstimadoPago, diaAnticipado Si
     * encuentra un registro, retorno el mismo--
     *
     */
    
    public SgAvisoPagoStaff findSgAvisoPagoRepetidoRelacion(int idStaff, int idAvisoPago) {
        UtilLog4j.log.info(this, "Buscar el objeto Aviso en la relacion");
        SgAvisoPagoStaff relacion;
        try {
            relacion = (SgAvisoPagoStaff) em.createQuery("SELECT r FROM SgAvisoPagoStaff r "
                    + " WHERE r.sgStaff.id = :idStaff AND "
                    + " r.sgAvisoPago.id = :idAvisoPago AND "
                    + " r.eliminado = :eliminado ").setParameter("idStaff", idStaff).setParameter("idAvisoPago", idAvisoPago).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList().get(0);

            UtilLog4j.log.info(this, "Todo bien en la busqueda de relacion");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, " Excepcion en la consulta " + e.getMessage());
            return null;
        }
        if (relacion != null) {
          //  UtilLog4j.log.info(this, "Objeto a retornar " + re.getSgTipoEspecifico().getNombre());
        } else {
            UtilLog4j.log.info(this, "El objeto buscado es null");
        }
        return relacion;
    }

    
    public SgAvisoPagoStaff buscarPorIdStaffAndIdTipoPago(int idStaff, int idTipoPago) {
        UtilLog4j.log.info(this, "Buscar el objeto Aviso en la relacion");
        SgAvisoPagoStaff relacion;
        try {
            relacion = (SgAvisoPagoStaff) em.createQuery("SELECT r FROM SgAvisoPagoStaff r "
                    + " WHERE r.sgStaff.id = :idStaff AND "
                    + " r.sgAvisoPago.sgTipoEspecifico.id = :idTipoPago AND "
                    + " r.eliminado = :eliminado ").setParameter("idStaff", idStaff).setParameter("idTipoPago", idTipoPago).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getSingleResult();

            UtilLog4j.log.info(this, "Todo bien en la busqueda de relacion");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, " Excepcion en la consulta " + e.getMessage());
            return null;
        }
        if (relacion != null) {
//            UtilLog4j.log.info(this, "Objeto a retornar " + re.getSgTipoEspecifico().getNombre());
        } else {
            UtilLog4j.log.info(this, "El objeto buscado es null");
        }
        return relacion;
    }
    
    
    
    public int findCoutSgAvisoPagoRepetidoRelacion(int idAvisoPago) {
        UtilLog4j.log.info(this, "Buscar el objeto Aviso en la relacion");
        List<SgAvisoPagoStaff> lrelacion;
        try {
            lrelacion = em.createQuery("SELECT r FROM SgAvisoPagoStaff r "
                    + " WHERE r.sgAvisoPago.id = :idAvisoPago AND r.sgAvisoPago.eliminado = :eliminado AND "
                    + " r.eliminado = :eliminado ")
                    .setParameter("idAvisoPago", idAvisoPago)
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .getResultList();

            UtilLog4j.log.info(this, "Todo bien en la busqueda de aviso repetido");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, " Excepcion en la consulta " + e.getMessage());
            return 0;
        }        
        UtilLog4j.log.info(this, "Retornar "+lrelacion.size());
        return lrelacion.size();
    }

    
    public void createRelacionAvisoPagoStaff(SgAvisoPago sgAvisoPago, int idStaff, Usuario usuarioGenero) {
        UtilLog4j.log.info(this, " sgAvisoPAgoStaffImpl.createRelacionAvisoPagoStaff");
        try {
            SgAvisoPagoStaff relacion = new SgAvisoPagoStaff();
            relacion.setSgAvisoPago(sgAvisoPago);
            relacion.setSgStaff(staffService.find(idStaff));
            relacion.setGenero(usuarioGenero);
            relacion.setFechaGenero(new Date());
            relacion.setHoraGenero(new Date());
            relacion.setEliminado(Constantes.BOOLEAN_FALSE);            
            create(relacion);
             UtilLog4j.log.info(this, "se creo la relacion satisfactoriamente  ");
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion aqui " + e.getMessage());
        }
    }

    //Actualización 10/01/2013 
        /*
         * Consulta nativa
         * select av.* From SG_AVISO_PAGO av
                       Where av.eliminado = 'False' AND av.id NOT IN (SELECT avs.SG_AVISO_PAGO FROM SG_AVISO_PAGO_STAFF avs
                                                WHERE avs.sg_staff = 1 AND avs.ELIMINADO = 'False')
         */
    
    public List<SgAvisoPago> findAllCatalogoAvisos(int idStaff) {
        UtilLog4j.log.info(this, "sgAvisoPagoStaff.findAllCatalofoAvisos");
        List<SgAvisoPago> listReturn = null;
        try {
            listReturn = em.createQuery("SELECT av FROM SgAvisoPago av "
                    + " WHERE av.eliminado = :eliminado "
                    + " AND av.id NOT IN "
                    + " (SELECT avs.sgAvisoPago.id FROM SgAvisoPagoStaff avs WHERE avs.sgStaff.id = :idStaff AND avs.eliminado = :eliminado) ")
                    .setParameter("idStaff", idStaff)
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .getResultList();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion ***" + e.getMessage());
        }
        return listReturn;
    }

    
    public void deleteAvisoPagoStaff(SgAvisoPagoStaff relacionAvisoPago, Usuario usuarioGenero) {
        try {
            relacionAvisoPago.setGenero(usuarioGenero);
            relacionAvisoPago.setFechaGenero(new Date());
            relacionAvisoPago.setHoraGenero(new Date());
            relacionAvisoPago.setEliminado(Constantes.BOOLEAN_TRUE);
            super.edit(relacionAvisoPago);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion :" + e.getMessage());
        }
    }

    
    public void deleteRelacionAvisoStaff(int idAvisoPago, int idStaff, Usuario usuarioGenero) {
        UtilLog4j.log.info(this, "sgAvisoPagoStaffImpl.deleteRelacionAvisoStaff ");
        SgAvisoPagoStaff r = findSgAvisoPagoRepetidoRelacion(idStaff, idAvisoPago);                
        try {
            r.setEliminado(Constantes.BOOLEAN_TRUE);
            r.setGenero(usuarioGenero);
            r.setFechaGenero(new Date());
            r.setHoraGenero(new Date());
            super.edit(r);
            
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion :" + e.getMessage());
        }
        UtilLog4j.log.info(this, "se elimino correctamente la relacion ");
    }

    
    public SgAvisoPagoStaff findAvisoPagoRepetido(int idStaff, int idTipoEspecifico) {
        UtilLog4j.log.info(this, "Buscar staff y tipo especifico");
        SgAvisoPagoStaff relacion;
        List<SgAvisoPagoStaff> ret=null;
        try {
            ret =  em.createQuery("SELECT r FROM SgAvisoPagoStaff r "
                    + " WHERE r.sgStaff.id = :idStaff AND "
                    + " r.sgAvisoPago.sgTipoEspecifico.id = :idTipoEspecifico AND "
                    + " r.eliminado = :eliminado ")
                    .setParameter("idStaff", idStaff)
                    .setParameter("idTipoEspecifico", idTipoEspecifico)
                    .setParameter("eliminado", Constantes.BOOLEAN_FALSE)
                    .getResultList();

            UtilLog4j.log.info(this, "Todo bien en la busqueda de relacion");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, " Excepcion en la consulta " + e.getMessage());
            return null;
        }
        if (ret.size() > 0) {
//            UtilLog4j.log.info(this, "Objeto a retornar " + re.getSgTipoEspecifico().getNombre());
            return ret.get(0);
        } else {
            UtilLog4j.log.info(this, "El objeto buscado es null");
            return null;
        }
        //return relacion;
    }
}
