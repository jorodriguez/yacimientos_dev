/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgEstadoVehiculo;
import sia.modelo.SgVehiculo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Stateless 
public class SgEstadoVehiculoImpl extends AbstractFacade<SgEstadoVehiculo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    @Inject
    SgTipoImpl tipoService;
    @Inject
    SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    SgVehiculoImpl vehiculoRemote;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgEstadoVehiculoImpl() {
        super(SgEstadoVehiculo.class);
    }

    
    public SgEstadoVehiculo createEstadoVehiculoActual(int idVehiculo, int idTipoEspecifico, String Observaciones, Usuario usuarioGenero) {
        UtilLog4j.log.info(this, "createEstadoVehiculoActual");        
        try {
            SgEstadoVehiculo sgEstadoVehiculoNuevo = new SgEstadoVehiculo();
            SgEstadoVehiculo sgEstadoVehiculoPasado = findEstadoVehiculoActual(idVehiculo);
            sgEstadoVehiculoNuevo.setSgTipo(this.tipoService.find(9));
            sgEstadoVehiculoNuevo.setSgTipoEspecifico(this.tipoEspecificoService.find(idTipoEspecifico));
            sgEstadoVehiculoNuevo.setSgVehiculo(this.vehiculoRemote.find(idVehiculo));
            sgEstadoVehiculoNuevo.setActivo(Constantes.BOOLEAN_TRUE);
            sgEstadoVehiculoNuevo.setEliminado(Constantes.BOOLEAN_FALSE);
            sgEstadoVehiculoNuevo.setGenero(usuarioGenero);
            sgEstadoVehiculoNuevo.setFechaGenero(new Date());
            sgEstadoVehiculoNuevo.setHoraGenero(new Date());      //Falta que agrege el idTipo para kilomentraje  
            sgEstadoVehiculoNuevo.setObservacion(Observaciones);
            super.create(sgEstadoVehiculoNuevo);

            if (sgEstadoVehiculoPasado != null) {
                sgEstadoVehiculoPasado.setActivo(Constantes.BOOLEAN_FALSE);
                edit(sgEstadoVehiculoPasado);
            }

            return sgEstadoVehiculoNuevo;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en la creacion de Estado de Vehiculo " + e.getMessage());
            return null;
        }
    }

    
    public void eliminarEstado(SgEstadoVehiculo estado, Usuario usuarioElimino) {
        try {            
            estado.setEliminado(Constantes.BOOLEAN_TRUE);
            super.edit(estado);            
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en eliminar estado " + e.getMessage());
        }
    }

    
    public SgEstadoVehiculo traerUltimoEstado(int idVehiculo) {
        UtilLog4j.log.info(this, "traerUltimoEstado");
        List<SgEstadoVehiculo> lret = null;
        try {
            lret = em.createQuery("SELECT e FROM SgEstadoVehiculo e "
                    + " WHERE e.sgVehiculo.id = :idVehiculo AND e.eliminado = :eliminado ORDER BY e.id DESC ").setParameter("idVehiculo", idVehiculo).setParameter("eliminado", Constantes.BOOLEAN_FALSE).setMaxResults(1).getResultList();
            if (!lret.isEmpty()) {
                return lret.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {

            UtilLog4j.log.fatal(this, "Excpcion en traer le ultimo estado " + e.getMessage());
            return null;
        }
    }

    
    public void activarDesactivarEstado(SgEstadoVehiculo estadoVehiculo, Usuario usuarioModifico, boolean estado) {
        try {        
            estadoVehiculo.setActivo(estado);
            edit(estadoVehiculo);            
        } catch (Exception e) {

            UtilLog4j.log.fatal(this, "Excepcion en activacion de estado " + e.getMessage());
        }
    }

    
    public SgEstadoVehiculo findEstadoVehiculoActual(int idVehiculo) {
        UtilLog4j.log.info(this, "SgEstadoVehiculoImpl.findEstadoVehiculoActual");
        List<SgEstadoVehiculo> ret = null;
        try {
            ret = em.createQuery("SELECT e FROM SgEstadoVehiculo e "
                    + " WHERE e.sgVehiculo.id = :idVehiculo AND e.activo = :actual AND e.eliminado = :eliminado ").setParameter("idVehiculo", idVehiculo).setParameter("actual", Constantes.BOOLEAN_TRUE).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
            //UtilLog4j.log.info(this,"size estad"+ret.size());
            if (ret != null && ret.size() > 0) {
                UtilLog4j.log.info(this, "Estado actual " + ret.get(0).getSgTipoEspecifico().getNombre());
                return ret.get(0);
            } else {
                UtilLog4j.log.info(this, "el estado es operacion normal");
                return null;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el estado de vehiculo actual " + e.getMessage());
            return null;
        }
    }

    
    public boolean regresarEstadoAnterior(SgVehiculo sgVehiculo, SgEstadoVehiculo sgEstadoActual, Usuario usuario) {
        UtilLog4j.log.info(this, "regresarEstadoAnterior");
        SgEstadoVehiculo estadoActualAnterior = null;
        SgEstadoVehiculo estadoActual = null;        
        try {
            estadoActualAnterior = findLastEstadoVehiculo(sgVehiculo, 2).get(1);
            if (estadoActualAnterior != null) {
                UtilLog4j.log.info(this, "si hay un estado anterior");                
                estadoActualAnterior.setActivo(Constantes.BOOLEAN_TRUE);
                estadoActualAnterior.setModifico(usuario);
                estadoActualAnterior.setFechaModifico(new Date());
                estadoActualAnterior.setHoraModifico(new Date());

                //estadoActual = findEstadoVehiculoActual(sgVehiculo.getId());
                UtilLog4j.log.info(this, "el estado actual se elimino");                
                sgEstadoActual.setEliminado(Constantes.BOOLEAN_TRUE);
                sgEstadoActual.setModifico(usuario);
                sgEstadoActual.setFechaModifico(new Date());
                sgEstadoActual.setHoraModifico(new Date());

                edit(sgEstadoActual);
                edit(estadoActualAnterior);
                UtilLog4j.log.info(this, "el proceso de regreso de estado fue exitoso");
                return true;
            } else {
                sgEstadoActual = findEstadoVehiculoActual(sgVehiculo.getId());
                sgEstadoActual.setEliminado(Constantes.BOOLEAN_TRUE);
                sgEstadoActual.setModifico(usuario);
                sgEstadoActual.setFechaModifico(new Date());
                sgEstadoActual.setHoraModifico(new Date());
                edit(sgEstadoActual);
                UtilLog4j.log.info(this, "solo existia un estado, el regreso de estado fue exitoso");
                return true;
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en regresar a estado anterior de vehiculo" + e.getMessage());
            return false;
        }
    }

    
    public List<SgEstadoVehiculo> findLastEstadoVehiculo(SgVehiculo sgVehiculo, int maximoResultados) {
        List<SgEstadoVehiculo> lret = null;
        try {
            lret = em.createQuery("SELECT e FROM SgEstadoVehiculo e "
                    + " WHERE e.sgVehiculo.id = :idVehiculo AND e.eliminado = :eliminado ORDER BY e.id DESC ").setParameter("idVehiculo", sgVehiculo.getId()).setParameter("eliminado", Constantes.BOOLEAN_FALSE).getResultList();
            if (!lret.isEmpty()) {
                if (maximoResultados >= lret.size()) {
                    return lret;
                } else {
                    return lret.subList(0, maximoResultados);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer el estados de vehiculo  " + e.getMessage());
            return null;
        }
    }

    /*
     *
     * Busca el ultimo estado del vehiculo para realizar la modificacion @param
     * sgVehiculo @param idTipoEspecifico @param Observaciones @param
     * usuarioModifico @return
     */
    
    public SgEstadoVehiculo editEstadoVehiculo(SgVehiculo sgVehiculo, int idTipoEspecifico, String Observaciones, Usuario usuarioModifico) {
        UtilLog4j.log.info(this, "editEstadoVehiculo");
        try {
            SgEstadoVehiculo sgEstadoVehiculo = findEstadoVehiculoActual(sgVehiculo.getId());
            sgEstadoVehiculo.setSgTipo(this.tipoService.find(9));
            sgEstadoVehiculo.setSgTipoEspecifico(this.tipoEspecificoService.find(idTipoEspecifico));
            sgEstadoVehiculo.setModifico(usuarioModifico);
            sgEstadoVehiculo.setFechaModifico(new Date());
            sgEstadoVehiculo.setHoraModifico(new Date());
            sgEstadoVehiculo.setObservacion(Observaciones);
            edit(sgEstadoVehiculo);
        
            return sgEstadoVehiculo;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en la creacion de Estado de Vehiculo " + e.getMessage());
            return null;
        }
    }
}
