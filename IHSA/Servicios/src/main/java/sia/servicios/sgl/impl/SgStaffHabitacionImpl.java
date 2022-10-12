/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.sistema.AbstractFacade;
import sia.excepciones.SIAException;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Stateless 
public class SgStaffHabitacionImpl extends AbstractFacade<SgStaffHabitacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgStaffHabitacionImpl() {
        super(SgStaffHabitacion.class);
    }
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)    
    public SgStaffHabitacion update(SgStaffHabitacion habitacionStaff, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgStaffHabitacionImpl.update()");

//        habitacionStaff.setModifico(usuarioService.find(idUsuario));
//        habitacionStaff.setFechaModifico(new Date());
//        habitacionStaff.setHoraModifico(new Date());
        super.edit(habitacionStaff);

        return habitacionStaff;
    }
    
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)    
    public SgStaffHabitacion delete(SgStaffHabitacion habitacionStaff, String idUsuario) throws SIAException, Exception {
        UtilLog4j.log.info(this, "SgStaffHabitacionImpl.delete()");

        habitacionStaff.setEliminado(Constantes.ELIMINADO);
//        habitacionStaff.setModifico(usuarioService.find(idUsuario));
//        habitacionStaff.setFechaModifico(new Date());
//        habitacionStaff.setHoraModifico(new Date());
        super.edit(habitacionStaff);

        return habitacionStaff;
    }    

    
    public List<SgStaffHabitacion> getAllHabitacionesByStaff(SgStaff staff, boolean status) {
        List<SgStaffHabitacion> habitacionesList = null;
        try {
            if (staff != null) {
                habitacionesList = em.createQuery("SELECT h FROM SgStaffHabitacion h WHERE h.eliminado = :estado AND h.sgStaff.id = :idStaff ORDER BY h.id")
                        .setParameter("estado", status)
                        .setParameter("idStaff", staff.getId())
                        .getResultList();
                if (habitacionesList != null) {
                    UtilLog4j.log.info(this, "Se encontraron " + habitacionesList.size() + " habitaciones para el Staff " + staff.getNombre());                    
                } 
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            habitacionesList = null;
        }
        return habitacionesList;
    }

    
    public List<SgStaffHabitacion> getAllHabitacionesByStaffAndOcupadoList(SgStaff staff, Boolean ocupada, boolean eliminada) {
        UtilLog4j.log.info(this, "SgStaffHabitacionImpl.getAllHabitacionesByStaffAndOcupadoList()");

        List<SgStaffHabitacion> habitacionesList = null;

        if (staff != null) {
            try {
                String q = "SELECT h FROM SgStaffHabitacion h WHERE h.eliminado = :estado AND h.sgStaff.id = :idStaff " + (ocupada != null ? " AND h.ocupada = :ocupada" : " ") + " ORDER BY h.id ASC";

                Query query = em.createQuery(q);
                if (ocupada != null) {
                    query.setParameter("ocupada", ((ocupada) ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
                }
                query.setParameter("estado", ((eliminada) ? Constantes.ELIMINADO : Constantes.NO_ELIMINADO));
                query.setParameter("idStaff", staff.getId());

                habitacionesList = query.getResultList();
            } catch (Exception e) {
                UtilLog4j.log.info(this, e.getMessage());
                return habitacionesList;
            }

            if (habitacionesList != null) {
                UtilLog4j.log.info(this, "Se encontraron " + habitacionesList.size() + " habitaciones ocupadas [" + ocupada + "] para el Staff " + staff.getNombre());
                return habitacionesList;
            } else {
                return habitacionesList;
            }
        } else {
            return habitacionesList;
        }
    }
}
