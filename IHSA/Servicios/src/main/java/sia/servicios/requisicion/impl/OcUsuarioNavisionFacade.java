/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.servicios.requisicion.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import sia.modelo.OcUsuarioNavision;

/**
 *
 * @author efectiva
 */
@Stateless
public class OcUsuarioNavisionFacade extends AbstractFacade<OcUsuarioNavision> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcUsuarioNavisionFacade() {
        super(OcUsuarioNavision.class);
    }
    
    public List<String> traerUsuarios(String nombre){
        return em.createNativeQuery("select NOMBRE from C_USUARIO_NAVISION"
        + "where upper(NOMBRE) like '%"+ nombre.toUpperCase() + "%'"
        + "and ELIMINADO = false").getResultList();
    }
     
}
