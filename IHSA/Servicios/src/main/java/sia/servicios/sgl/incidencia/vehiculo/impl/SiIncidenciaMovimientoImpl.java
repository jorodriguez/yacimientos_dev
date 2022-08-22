/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.incidencia.vehiculo.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Estatus;
import sia.modelo.SiIncidencia;
import sia.modelo.SiIncidenciaMovimientos;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author mluis
 */
@Stateless 
public class SiIncidenciaMovimientoImpl extends AbstractFacade<SiIncidenciaMovimientos>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiIncidenciaMovimientoImpl() {
        super(SiIncidenciaMovimientos.class);
    }
    
    
    public void gurdar(int idIncidencia, int status, String sesion, String accion){
        SiIncidenciaMovimientos incidenciaMovimiento = new SiIncidenciaMovimientos();
        incidenciaMovimiento.setSiIncidencia(new SiIncidencia(idIncidencia));
        incidenciaMovimiento.setEstatus(new Estatus(status));
        incidenciaMovimiento.setAccion(accion);
        incidenciaMovimiento.setGenero(new Usuario(sesion));
        incidenciaMovimiento.setFechaGenero(new  Date());
        incidenciaMovimiento.setHoraGenero(new Date());
        incidenciaMovimiento.setEliminado(Constantes.BOOLEAN_FALSE);
        //
        create(incidenciaMovimiento);
    }
    
}
