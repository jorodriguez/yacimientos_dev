/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.incidencia.vehiculo.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiTag;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SiTagImpl extends AbstractFacade<SiTag>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiTagImpl() {
        super(SiTag.class);
    }

    
    public int guardar(String sesion, String tag) {
            SiTag siTag = new SiTag();
            siTag.setPalabraClave(tag);
            siTag.setGenero(new Usuario(sesion));
            siTag.setFechaGenero(new Date());
            siTag.setHoraGenero(new Date());
            siTag.setEliminado(Constantes.NO_ELIMINADO);
            create(siTag);
            //
        return siTag.getId();
    }

}
