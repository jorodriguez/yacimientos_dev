/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SgCtrlMantenimientoSanitario;
import sia.modelo.SgSanitario;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class SgCtrlMantenimientoSanitarioImpl extends AbstractFacade<SgCtrlMantenimientoSanitario> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgCtrlMantenimientoSanitarioImpl() {
        super(SgCtrlMantenimientoSanitario.class);
    }

    
    public List<SgCtrlMantenimientoSanitario> traerBitacoraSanitario(int idSanitario, boolean eliminado) {
        try {
            return em.createQuery("SELECT c FROM SgCtrlMantenimientoSanitario c WHERE "
                    + "c.sgOficnaSanitario.id = :idSan AND c.eliminado = :eli "
                    + "ORDER BY c.id DESC").setParameter("idSan", idSanitario).setParameter("eli", eliminado).setMaxResults(5).getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    
    public void guardarBitacoraSanitario(Usuario usuario, SgSanitario sgSanitario, SgCtrlMantenimientoSanitario sgCtrlMantenimientoSanitario, boolean BOOLEAN_FALSE) {
        sgCtrlMantenimientoSanitario.setSgOficnaSanitario(sgSanitario);
        sgCtrlMantenimientoSanitario.setGenero(usuario);
        sgCtrlMantenimientoSanitario.setFechaGenero(new Date());
        sgCtrlMantenimientoSanitario.setHoraGenero(new Date());
        sgCtrlMantenimientoSanitario.setEliminado(BOOLEAN_FALSE);
        create(sgCtrlMantenimientoSanitario);
    }

    
    public void modificarBitacoraSanitario(Usuario usuario, SgCtrlMantenimientoSanitario sgCtrlMantenimientoSanitario, boolean eliminado) {
        sgCtrlMantenimientoSanitario.setGenero(usuario);
        sgCtrlMantenimientoSanitario.setHoraGenero(new Date());
        sgCtrlMantenimientoSanitario.setFechaGenero(new Date());
        sgCtrlMantenimientoSanitario.setEliminado(eliminado);
        edit(sgCtrlMantenimientoSanitario);
    }
 
    public boolean guardarAdjuntoBitacoraSanitario(Usuario usuario, SgCtrlMantenimientoSanitario sgCtrlMantenimientoSanitario, SiAdjunto siAdjunto) {
        boolean v;
        try {
            sgCtrlMantenimientoSanitario.setGenero(usuario);
            sgCtrlMantenimientoSanitario.setHoraGenero(new Date());
            sgCtrlMantenimientoSanitario.setFechaGenero(new Date());
            sgCtrlMantenimientoSanitario.setSiAdjunto(siAdjunto);
            edit(sgCtrlMantenimientoSanitario);
            v=true;
        } catch (Exception e) {
            v=false;
        }
        return v;
    }
    
    public boolean eliminarAdjuntoBitacoraSanitario(Usuario usuario, SgCtrlMantenimientoSanitario sgCtrlMantenimientoSanitario) {
        boolean v;
        try {
            sgCtrlMantenimientoSanitario.setGenero(usuario);
            sgCtrlMantenimientoSanitario.setHoraGenero(new Date());
            sgCtrlMantenimientoSanitario.setFechaGenero(new Date());
            sgCtrlMantenimientoSanitario.setSiAdjunto(null);
            edit(sgCtrlMantenimientoSanitario);
            v=true;
        } catch (Exception e) {
            v=false;
        }
        return v;
    }
}
