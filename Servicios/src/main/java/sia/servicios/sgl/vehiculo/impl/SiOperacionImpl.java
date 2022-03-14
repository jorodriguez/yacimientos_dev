/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.vehiculo.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiOperacion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SiOperacionImpl extends AbstractFacade<SiOperacion>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiOperacionImpl() {
        super(SiOperacion.class);
    }
    
    
    public List<SiOperacion> traerOperacion() {
        try {
            return em.createQuery("SELECT op FROM SiOperacion op WHERE op.eliminado = :eli"
                    + " ORDER BY op.id DESC").setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public List<SiOperacion> traerOperacionPorRango(int inicio, int fin) {
        try {
            return em.createQuery("SELECT op FROM SiOperacion op "
                    + " WHERE op.id BETWEEN :ini AND :fin "
                    + " op.eliminado = :eli"
                    + " ORDER BY op.id DESC").setParameter("ini", inicio).setParameter("fin", fin).setParameter("eli", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public SiOperacion buscarPorNombre(SiOperacion siOperacion) {
        try {
            return (SiOperacion) em.createQuery("SELECT op FROM SiOperacion op WHERE op.nombre = :nombre "
                    + " AND op.eliminado = :eli"
                    + " ORDER BY op.id DESC").setParameter("nombre", siOperacion.getNombre()).setParameter("eli", Constantes.NO_ELIMINADO).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public int buscarPorNombre(String operacion) {
        try {
            clearQuery();
            query.append(" select op.id, op.nombre from si_operacion op ");
            query.append(" where op.nombre = '").append(operacion).append("'");
            query.append(" and op.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
            //
            Object[] objects = (Object[]) em.createNativeQuery(query.toString()).getSingleResult();
            int op = (Integer) objects[0];
            return op;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer la operación  : : : :  : ");
            return 0;
        }
    }

    
    public void guardarOperacion(Usuario usuario, SiOperacion siOperacion) {
        siOperacion.setGenero(usuario);
        siOperacion.setFechaGenero(new Date());
        siOperacion.setHoraGenero(new Date());
        siOperacion.setEliminado(Constantes.NO_ELIMINADO);
        create(siOperacion);
    }

    
    public void modificarOperacion(Usuario usuario, SiOperacion siOperacion) {
        siOperacion.setModifico(usuario);
        siOperacion.setFechaModifico(new Date());
        siOperacion.setHoraModifico(new Date());
        edit(siOperacion);
    }

    
    public void eliminarOperacion(Usuario usuario, SiOperacion siOperacion) {
        //buscar la operacion
        siOperacion.setModifico(usuario);
        siOperacion.setFechaModifico(new Date());
        siOperacion.setHoraModifico(new Date());
        siOperacion.setEliminado(Constantes.ELIMINADO);
        edit(siOperacion);
    }
}
