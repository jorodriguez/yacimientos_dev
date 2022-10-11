/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.combustible.impl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.Compania;
import sia.modelo.SgTarjetaBancaria;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoImpl;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SgTarjetaBancariaImpl extends AbstractFacade<SgTarjetaBancaria> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public SgTarjetaBancariaImpl() {
	super(SgTarjetaBancaria.class);
    }

    @Inject
    private SgTipoImpl sgTipoRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;

    /**
     *
     * @param sesion
     * @param tarjeta
     * @return
     */
    
    public SgTarjetaBancaria guardar(String sesion, String tarjeta) {
	try {
	    SgTarjetaBancaria sgTarjeta = new SgTarjetaBancaria();
	    sgTarjeta.setCompania(new Compania(Constantes.RFC_IHSA));
	    sgTarjeta.setNumeroTarjeta(tarjeta);
	    sgTarjeta.setSgTipo(sgTipoRemote.getTipoByNombre("Tarjetas", Constantes.NO_ELIMINADO));
	    sgTarjeta.setSgTipoEspecifico(sgTipoEspecificoRemote.buscarPorNombre("Combustible", Constantes.NO_ELIMINADO));
	    sgTarjeta.setGenero(new Usuario(sesion));
	    sgTarjeta.setFechaGenero(new Date());
	    sgTarjeta.setHoraGenero(new Date());
	    sgTarjeta.setEliminado(Constantes.NO_ELIMINADO);
	    //
	    create(sgTarjeta);
	    return sgTarjeta;
	} catch (Exception ex) {
	    // 
	    Logger.getLogger(SgTarjetaBancariaImpl.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    
    public SgTarjetaBancaria buscarPorNumero(String tarjeta) {
	try {
	    String sb = " select t.* from sg_tarjeta_bancaria t where t.numero_tarjeta = ? and t.eliminado = 'False'";
	    return (SgTarjetaBancaria) em.createNativeQuery(sb, SgTarjetaBancaria.class).setParameter(1, tarjeta).getSingleResult();
	} catch (NoResultException ex) {
	    return null;
	}
    }
}
