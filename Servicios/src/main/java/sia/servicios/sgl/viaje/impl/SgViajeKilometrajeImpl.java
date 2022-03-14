/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgViajeKilometraje;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgKilometrajeImpl;

/**
 *
 * @author b75ckd35th
 */
@LocalBean 
public class SgViajeKilometrajeImpl extends AbstractFacade<SgViajeKilometraje>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private SgKilometrajeImpl sgKilometrajeRemote;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgViajeKilometrajeImpl() {
        super(SgViajeKilometraje.class);
    }

    
    public void save(int idSgViaje, int idSgKilometraje, String idUsuario) {
        SgViajeKilometraje sgViajeKilometraje = new SgViajeKilometraje();
        sgViajeKilometraje.setSgViaje(this.sgViajeRemote.find(idSgViaje));
        sgViajeKilometraje.setSgKilometraje(this.sgKilometrajeRemote.find(idSgKilometraje));
        sgViajeKilometraje.setGenero(new Usuario(idUsuario));
        sgViajeKilometraje.setFechaGenero(new Date());
        sgViajeKilometraje.setHoraGenero(new Date());
        sgViajeKilometraje.setEliminado(Constantes.NO_ELIMINADO);

        super.create(sgViajeKilometraje);

    }
}
