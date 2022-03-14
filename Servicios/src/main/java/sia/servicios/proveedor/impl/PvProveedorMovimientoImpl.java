/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.PvProveedorMovimiento;
import sia.modelo.SiOperacion;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class PvProveedorMovimientoImpl extends AbstractFacade<PvProveedorMovimiento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PvProveedorMovimientoImpl() {
        super(PvProveedorMovimiento.class);
    }
    @Inject
    SiMovimientoImpl siMovimientoRemote;

    /**
     *
     * @param sesion
     * @param proveedor
     * @param motivo
     */
    public void guardar(String sesion, int proveedor, String motivo) {
        try {
            PvProveedorMovimiento ppm = new PvProveedorMovimiento();
            ppm.setProveedor(new Proveedor(proveedor));
            ppm.setSiMovimiento(siMovimientoRemote.guardarSiMovimiento(motivo, new SiOperacion(Constantes.ID_SI_OPERACION_DEVOLVER), new Usuario(sesion)));
            ppm.setGenero(new Usuario(sesion));
            ppm.setFechaGenero(new Date());
            ppm.setHoraGenero(new Date());
            ppm.setEliminado(Constantes.NO_ELIMINADO);
            create(ppm);
        } catch (Exception ex) {
            Logger.getLogger(PvProveedorMovimientoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
