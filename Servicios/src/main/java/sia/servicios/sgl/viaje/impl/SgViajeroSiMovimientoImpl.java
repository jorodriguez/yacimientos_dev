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
import sia.modelo.SgViajero;
import sia.modelo.SgViajeroSiMovimiento;
import sia.modelo.SiMovimiento;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class SgViajeroSiMovimientoImpl extends AbstractFacade<SgViajeroSiMovimiento>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }    
    @Inject
    private SgViajeroImpl sgViajeroRemote;
    @Inject
    private SiMovimientoImpl siMovimientoRemote;

    private static final UtilLog4j LOGGER = UtilLog4j.log;
    
    public SgViajeroSiMovimientoImpl() {
	super(SgViajeroSiMovimiento.class);
    }

    
    public boolean guardarMovimiento(String usuarioSesion, int idViajero, SiMovimiento movimiento) {
	boolean v;
	SgViajeroSiMovimiento sgViajeroSiMovimiento = new SgViajeroSiMovimiento();
        SgViajero viajero = sgViajeroRemote.find(idViajero);
	sgViajeroSiMovimiento.setSgViajero(viajero);
	sgViajeroSiMovimiento.setSiMovimiento(movimiento);
	sgViajeroSiMovimiento.setGenero(new Usuario(usuarioSesion));
	sgViajeroSiMovimiento.setFechaGenero(new Date());
	sgViajeroSiMovimiento.setHoraGenero(new Date());
	sgViajeroSiMovimiento.setEliminado(Constantes.NO_ELIMINADO);
	create(sgViajeroSiMovimiento);
        if(Constantes.ID_SI_OPERACION_VIAJERO_NO_VIAJO == movimiento.getSiOperacion().getId()){
            viajero.setEliminado(Constantes.BOOLEAN_TRUE);
            sgViajeroRemote.edit(viajero);
        }
	v = true;
	
	return v;
    }

    
    public SgViajeroSiMovimiento findByTraveller(int idViajero,int operacion1, int operacion2, int operacion3) {
        
	try {
            SgViajeroSiMovimiento sgViajeroSiMovimiento = new SgViajeroSiMovimiento();
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT svm.id, svm.SI_MOVIMIENTO, svm.SG_VIAJERO "
                    + " FROM SG_VIAJERO_SI_MOVIMIENTO svm "
                    + " INNER JOIN SI_MOVIMIENTO m ON m.ID = svm.SI_MOVIMIENTO "
                    + " WHERE svm.ELIMINADO = ? AND  svm.SG_VIAJERO = ? "
                    + " AND m.SI_OPERACION IN (?,?,?) AND m.ELIMINADO = ? "
                    + " ORDER BY svm.id desc limit 1");
            
            Object [] ob = (Object[]) 
                    em.createNativeQuery(sb.toString())
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, idViajero)
                    .setParameter(3, operacion1)
                    .setParameter(4, operacion2)
                    .setParameter(5, operacion3)
                    .setParameter(6, Constantes.NO_ELIMINADO)
                    .getSingleResult();
            
            sgViajeroSiMovimiento.setId((Integer) ob[0] );
            sgViajeroSiMovimiento.setSiMovimiento(siMovimientoRemote.find((Integer)ob[1]));
            sgViajeroSiMovimiento.setSgViajero(sgViajeroRemote.find(idViajero));
            return sgViajeroSiMovimiento;
	} catch (Exception e) {
            LOGGER.fatal(e);
            LOGGER.fatal(e, e.getMessage());
	    return null;
	}
    }

    
    public void deleteRelation(Usuario usuario, SgViajeroSiMovimiento sgViajeroSiMovimiento) {
	try {
	    sgViajeroSiMovimiento.setModifico(usuario);
	    sgViajeroSiMovimiento.setFechaModifico(new Date());
	    sgViajeroSiMovimiento.setHoraModifico(new Date());
	    sgViajeroSiMovimiento.setEliminado(Constantes.ELIMINADO);
	    edit(sgViajeroSiMovimiento);	    
	} catch (Exception e) {
	    LOGGER.fatal(e);
            LOGGER.fatal(e, e.getMessage());
	}
    }

    
    public void guardaMovimiento(String sesion, int idViajero, String motivo, int idOperacion) {
	//Guar da el movimiento
	SiMovimiento sm = siMovimientoRemote.save(motivo, idOperacion, sesion);
	//
	guardarMovimiento(sesion, idViajero, sm);
    }
}
