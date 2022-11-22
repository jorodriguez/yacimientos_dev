/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgCadenaAprobacion;
import sia.modelo.SgCadenaNegacion;
import sia.modelo.SiOperacion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgCadenaNegacionImpl extends AbstractFacade<SgCadenaNegacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgCadenaNegacionImpl() {
        super(SgCadenaNegacion.class);
    }
    @Inject
    private EstatusImpl estatusService;
    
    
    public void crearCadenaNegacion(SgCadenaAprobacion cadenaAprobacion, SiOperacion siOperacion, int idEstatus, Usuario usuario) {
        try {
            SgCadenaNegacion cn = new SgCadenaNegacion();
            cn.setEliminado(Constantes.BOOLEAN_FALSE);
            cn.setEstatus(estatusService.find(idEstatus));
            cn.setSgCadenaAprobacion(cadenaAprobacion);
            cn.setSiOperacion(siOperacion);
            cn.setGenero(usuario);
            cn.setFechaGenero(new Date());
            cn.setHoraGenero(new Date());
            super.create(cn);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);            
        }
    }

    
    public List<SgCadenaNegacion> traerCadenasNegacionPorCadenaAprobacion(int idCadenaAprobacion) {
        try {
            return em.createQuery("SELECT n FROM SgCadenaNegacion n "
                    + " WHERE n.sgCadenaAprobacion.id = :idCadenaAprobacion "
                    + " AND n.eliminado = :eli ORDER BY n.siOperacion.id ASC").setParameter("idCadenaAprobacion", idCadenaAprobacion).setParameter("eli", Constantes.BOOLEAN_FALSE).getResultList();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);            
            return null;
        }
    }
    
    //Buscar cadenas de negacion por estatus de aprobacion y tipo de solicitud
    
    public SgCadenaNegacion traerCadenasNegacionPorTipoSolicitudEstatus(int idSiOperacion,int idEstatusAprobacion,int idTipoSolicitud) {
        try {
             return (SgCadenaNegacion) em.createQuery("SELECT n FROM SgCadenaNegacion n "
                    + " WHERE n.siOperacion.id = :idSiOperacion "
                    + " AND n.eliminado = :eli "
                    + " AND n.sgCadenaAprobacion.sgTipoSolicitudViaje.id = :idTipoSolicitud "
                    + " AND n.sgCadenaAprobacion.estatus.id = :idEstatusAprobacion ")
                    .setParameter("idTipoSolicitud", idTipoSolicitud)
                    .setParameter("idSiOperacion", idSiOperacion)                   
                    .setParameter("idEstatusAprobacion", idEstatusAprobacion)   
                    .setParameter("eli", Constantes.BOOLEAN_FALSE)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);            
            return null;
        }
    }
    

    
    public void modificarCadenaNegacion(SgCadenaNegacion cn, int idEstatusNuevo, Usuario usuarioGenero) {
        try {
            cn.setEliminado(Constantes.BOOLEAN_FALSE);
            cn.setEstatus(estatusService.find(idEstatusNuevo));
            cn.setFechaModifico(new Date());
            cn.setHoraModifico(new Date());
            super.edit(cn);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);            
        }
    }
}
