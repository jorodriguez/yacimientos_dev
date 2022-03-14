/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.modelo.SgHistorialConvenioStaff;
import sia.modelo.SgStaff;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgHistorialConvenioStaffImpl extends AbstractFacade<SgHistorialConvenioStaff>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgHistorialConvenioStaffImpl() {
        super(SgHistorialConvenioStaff.class);
    }

    
    public List<SgHistorialConvenioStaff> getHistorialConvenioStaffVigente(String vigente, SgStaff staff, boolean eliminado) {
        UtilLog4j.log.info(this, "SgHistorialConvenioStaffImpl.getHistorialConvenioStaffVigente()");
        List<SgHistorialConvenioStaff> lhc;
        try {
            UtilLog4j.log.info(this, "idStaff " + staff.getId() + " vigente " + vigente + " eliminado " + eliminado);
            lhc = em.createQuery("SELECT hc FROM SgHistorialConvenioStaff  hc"
                    + " WHERE hc.vigente = :vigente AND hc.sgStaff.id = :idStaff AND hc.eliminado = :elim").setParameter("idStaff", staff.getId()).setParameter("vigente", vigente).setParameter("elim", eliminado).getResultList();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existió un error al consultar el historial de convenios " + e.getMessage());
            return null;
        }
        return lhc;
    }

    
    public List<SgHistorialConvenioStaff> getAllHistorialConvenioStaff(SgStaff staff, boolean eliminado) {
        UtilLog4j.log.info(this, "SgHistorialConvenioStaffImpl.getAllHistorialConvevioStaff()");
        List<SgHistorialConvenioStaff> listaHistorial;
        try {
            listaHistorial = em.createQuery("SELECT hc FROM SgHistorialConvenioStaff  hc"
                    + " WHERE hc.sgStaff.id = :idStaff AND hc.eliminado = :elim ORDER BY hc.id DESC").setParameter("idStaff", staff.getId()).setParameter("elim", eliminado).getResultList();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existió un error al consultar el historial de convenios " + e.getMessage());
            return null;
        }
        return listaHistorial;
    }

    
    public void eliminarRelacion(SgHistorialConvenioStaff hist, Usuario usuario) {
        try {
            hist.setGenero(usuario);
            hist.setHoraGenero(new Date());
            hist.setFechaGenero(new Date());
            hist.setVigente(Constantes.BOOLEAN_FALSE);
            hist.setEliminado(Constantes.BOOLEAN_TRUE);
            super.edit(hist);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Existió un error al eliminar la relación  " + e.getMessage());
        }
    }

    
    public List<SgHistorialConvenioStaff> getContratoByVigenteList(boolean vigente) {
        UtilLog4j.log.info(this, "SgHistorialConvenioStaffImpl.getContratoByVigenteList()");
        try {
            return em.createQuery("SELECT h FROM SgHistorialConvenioStaff h WHERE h.vigente = :vigente AND h.eliminado = :eliminado").setParameter("vigente", vigente).setParameter("eliminado", Constantes.NO_ELIMINADO).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public SgHistorialConvenioStaff traerContratoVigente(SgStaff staff) {
        try {
            return (SgHistorialConvenioStaff) em.createQuery("SELECT h FROM SgHistorialConvenioStaff h WHERE "
                    + " h.vigente = :vigente "
                    + " AND h.sgStaff.id = :staff"
                    + " AND h.eliminado = :eliminado")
                    .setParameter("vigente", Constantes.BOOLEAN_TRUE)
                    .setParameter("staff", staff.getId())
                    .setParameter("eliminado", Constantes.NO_ELIMINADO).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    
    public List<SgHistorialConvenioStaff> buscarRelacionConvenio(SgStaff staff, Convenio convenioSeleccionado) {
        try {
            return em.createQuery("SELECT h FROM SgHistorialConvenioStaff h WHERE "
                    + " h.sgStaff.id = :idStaff" 
                    + " AND h.vigente = :v "
                    + " AND h.convenio.id = :idCon"
                    + " AND h.vigente = :v "
                    + " AND h.eliminado = :eliminado")
                    .setParameter("v", Constantes.BOOLEAN_TRUE)
                    .setParameter("idStaff", staff.getId())
                    .setParameter("idCon", convenioSeleccionado.getId())
                    .setParameter("v", Constantes.BOOLEAN_TRUE)
                    .setParameter("eliminado", Constantes.NO_ELIMINADO)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public SgHistorialConvenioStaff guardarConvenio(Usuario usuario, Convenio convenioSeleccionado, SgStaff staff) {
        try {
            SgHistorialConvenioStaff sgHistorialConvenioStaff = new SgHistorialConvenioStaff();
            sgHistorialConvenioStaff.setConvenio(convenioSeleccionado);
            sgHistorialConvenioStaff.setSgStaff(staff);
            sgHistorialConvenioStaff.setFechaGenero(new Date());
            sgHistorialConvenioStaff.setHoraGenero(new Date());
            sgHistorialConvenioStaff.setGenero(usuario);
            sgHistorialConvenioStaff.setVigente(Constantes.BOOLEAN_TRUE);
            sgHistorialConvenioStaff.setEliminado(Constantes.NO_ELIMINADO);
            create(sgHistorialConvenioStaff);
            return sgHistorialConvenioStaff;
        } catch (Exception e) {
            return null;
        }
    }

    
    public void quitarContratoVigente(Usuario usuario, SgHistorialConvenioStaff ultimoConvenioStaff, SgStaff staff) {
        ultimoConvenioStaff.setSgStaff(staff);
        ultimoConvenioStaff.setVigente(Constantes.BOOLEAN_FALSE);
        ultimoConvenioStaff.setFechaGenero(new Date());
        ultimoConvenioStaff.setHoraGenero(new Date());
        ultimoConvenioStaff.setGenero(usuario);
        edit(ultimoConvenioStaff);
    }
}
