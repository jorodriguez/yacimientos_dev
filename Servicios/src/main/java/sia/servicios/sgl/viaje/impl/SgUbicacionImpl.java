/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.viaje.impl;

import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.modelo.SgUbicacion;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;

/**
 *
 * @author jevazquez
 */
@LocalBean 
public class SgUbicacionImpl extends AbstractFacade<SgUbicacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;    
    @Inject
    private UsuarioImpl usuarioService;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SgUbicacionImpl() {
        super(SgUbicacion.class);
    }
    

}
