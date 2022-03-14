/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sia.servicios.sgl.viaje.impl;

import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SgViajeEstadoSemaforo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgViajeEstadoSemaforoImpl extends AbstractFacade<SgViajeEstadoSemaforo>{
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgViajeEstadoSemaforoImpl() {
        super(SgViajeEstadoSemaforo.class);
    }
    
    @Inject
    private SgViajeImpl sgViajeRemote;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;
    @Inject
    private UsuarioImpl usuarioRemote;
    
//    
//    public void guardarViajeSemaforo(int idViaje, int idEstadoSemaforo, String idUsuario){
//        try {
//            SgViajeEstadoSemaforo sgViajeEstadoSemaforo = new SgViajeEstadoSemaforo();
//            sgViajeEstadoSemaforo.setSgViaje(sgViajeRemote.find(idViaje));
//            sgViajeEstadoSemaforo.setSgEstadoSemaforo(sgEstadoSemaforoRemote.find(idEstadoSemaforo));
//            sgViajeEstadoSemaforo.setGenero(usuarioRemote.find(idUsuario));
//            sgViajeEstadoSemaforo.setFechaGenero(new Date());
//            sgViajeEstadoSemaforo.setHoraGenero(new Date());
//            sgViajeEstadoSemaforo.setEliminado(Constantes.NO_ELIMINADO);
//            create(sgViajeEstadoSemaforo);
//            
//            //Log
//   //         siLogRemote.create(SiLog.class.getName(), sgViajeEstadoSemaforo.getId(), 1, idUsuario, null, sgViajeEstadoSemaforo.toString());
//        } catch (Exception ex) {
//            Logger.getLogger(SgViajeEstadoSemaforoImpl.class.getName()).log(Level.SEVERE, null, ex);
//        } 
//    }
}
