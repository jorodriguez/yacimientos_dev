/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.OcPresupuesto;
import sia.modelo.OcPresupuestoAdjunto;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class OcPresupuestoAdjuntoImpl extends AbstractFacade<OcPresupuestoAdjunto> {
    
    private static final UtilLog4j LOGGER = UtilLog4j.log;
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcPresupuestoAdjuntoImpl() {
        super(OcPresupuestoAdjunto.class);
    }
 
    
    
    public boolean agregarArchivoPresupuesto(int idPresupuesto, String sesion, int siAdjunto) {
        boolean v;
        try {
            OcPresupuestoAdjunto pa = new OcPresupuestoAdjunto();
            pa.setOcPresupuesto(new OcPresupuesto(idPresupuesto));
            pa.setSiAdjunto(new SiAdjunto(siAdjunto));
            pa.setGenero(new Usuario(sesion));
            pa.setFechaGenero(new Date());
            pa.setHoraGenero(new Date());
            pa.setEliminado(Constantes.NO_ELIMINADO);
            create(pa);
            
            v = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "No se guardo el archivo : : : : : " + e.getMessage());
            v = false;
        }
        return v;
    }
}
