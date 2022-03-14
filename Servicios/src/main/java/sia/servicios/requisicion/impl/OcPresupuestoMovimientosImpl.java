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
import sia.modelo.OcPresupuestoMovimientos;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@LocalBean 
public class OcPresupuestoMovimientosImpl extends AbstractFacade<OcPresupuestoMovimientos>{
    
    private static final UtilLog4j LOGGER = UtilLog4j.log;
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcPresupuestoMovimientosImpl() {
        super(OcPresupuestoMovimientos.class);
    }
    
    
    public void importarTemp(int idPresupuesto, String sesion){
        OcPresupuestoMovimientos movimiento = new OcPresupuestoMovimientos();
        movimiento.setOcPresupuesto(new OcPresupuesto(idPresupuesto));        
        movimiento.setAccion("IMPORTARTEMP");
        movimiento.setGenero(new Usuario(sesion));
        movimiento.setFechaGenero(new  Date());
        movimiento.setHoraGenero(new Date());
        movimiento.setEliminado(Constantes.BOOLEAN_FALSE);        
        create(movimiento);
    }
    
    
    public void importarGuardar(int idPresupuesto, String sesion){
        OcPresupuestoMovimientos movimiento = new OcPresupuestoMovimientos();
        movimiento.setOcPresupuesto(new OcPresupuesto(idPresupuesto));        
        movimiento.setAccion("IMPORTARGUARDAR");
        movimiento.setGenero(new Usuario(sesion));
        movimiento.setFechaGenero(new  Date());
        movimiento.setHoraGenero(new Date());
        movimiento.setEliminado(Constantes.BOOLEAN_FALSE);        
        create(movimiento);
    }
    
    
    public void exportar(int idPresupuesto, String sesion){
        OcPresupuestoMovimientos movimiento = new OcPresupuestoMovimientos();
        movimiento.setOcPresupuesto(new OcPresupuesto(idPresupuesto));        
        movimiento.setAccion("EXPORTAR");
        movimiento.setGenero(new Usuario(sesion));
        movimiento.setFechaGenero(new  Date());
        movimiento.setHoraGenero(new Date());
        movimiento.setEliminado(Constantes.BOOLEAN_FALSE);        
        create(movimiento);
    }
    
    
    public void inactivar(int idPresupuesto, String sesion){
        OcPresupuestoMovimientos movimiento = new OcPresupuestoMovimientos();
        movimiento.setOcPresupuesto(new OcPresupuesto(idPresupuesto));        
        movimiento.setAccion("INACTIVAR");
        movimiento.setGenero(new Usuario(sesion));
        movimiento.setFechaGenero(new  Date());
        movimiento.setHoraGenero(new Date());
        movimiento.setEliminado(Constantes.BOOLEAN_FALSE);        
        create(movimiento);
    }
    
    
    public void modificarMonto(int idPresupuesto, int idPresDet, String sesion){
        OcPresupuestoMovimientos movimiento = new OcPresupuestoMovimientos();
        movimiento.setOcPresupuesto(new OcPresupuesto(idPresupuesto));        
        movimiento.setAccion("MM"+idPresDet);
        movimiento.setGenero(new Usuario(sesion));
        movimiento.setFechaGenero(new  Date());
        movimiento.setHoraGenero(new Date());
        movimiento.setEliminado(Constantes.BOOLEAN_FALSE);        
        create(movimiento);
    }
    
}
