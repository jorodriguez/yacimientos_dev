/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.impl;

import java.util.Date;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SgHpHotelSiMovimiento;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;

/**
 *
 * @author jrodriguez
 */
@LocalBean 
public class SgHuespedHotelSiMovimientoImpl extends AbstractFacade<SgHpHotelSiMovimiento> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    //
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgHuespedHotelSiMovimientoImpl() {
        super(SgHpHotelSiMovimiento.class);
    }

    
    public void save(int idSgHuespedHotel, int idSiMovimiento, String idUsuario) {

        SgHpHotelSiMovimiento sgHpHotelSiMovimiento;

        sgHpHotelSiMovimiento = new SgHpHotelSiMovimiento();
        sgHpHotelSiMovimiento.setSiMovimiento(this.siMovimientoRemote.find(idSiMovimiento));
        sgHpHotelSiMovimiento.setSgHuespedHotel(this.sgHuespedHotelRemote.find(idSgHuespedHotel));
        sgHpHotelSiMovimiento.setGenero(new Usuario(idUsuario));
        sgHpHotelSiMovimiento.setFechaGenero(new Date());
        sgHpHotelSiMovimiento.setHoraGenero(new Date());
        sgHpHotelSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);

        create(sgHpHotelSiMovimiento);
        
    }

    
    public void guardarHuespedHotelSiMovimiento(SgHuespedHotel sgHuespedHotel, SiMovimiento siMovimiento, Usuario usuarioGenero) throws Exception {
        SgHpHotelSiMovimiento relacion = new SgHpHotelSiMovimiento();
        relacion.setEliminado(Constantes.BOOLEAN_FALSE);
        relacion.setGenero(usuarioGenero);
        relacion.setFechaGenero(new Date());
        relacion.setHoraGenero(new Date());
        relacion.setSgHuespedHotel(sgHuespedHotel);
        relacion.setSiMovimiento(siMovimiento);
        super.create(relacion);
    }
}
