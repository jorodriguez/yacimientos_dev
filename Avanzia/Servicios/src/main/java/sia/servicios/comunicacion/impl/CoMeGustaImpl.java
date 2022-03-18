/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CoMeGusta;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class CoMeGustaImpl extends AbstractFacade<CoMeGusta> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoMeGustaImpl() {
        super(CoMeGusta.class);
    }

    
    public CoMeGusta meGusta(String idUsuario) {
        try {
            CoMeGusta meGusta = new CoMeGusta();
            meGusta.setEliminado(Constantes.BOOLEAN_FALSE);
            meGusta.setFechaGenero(new Date());
            meGusta.setHoraGenero(new Date());
            meGusta.setGenero(new Usuario(idUsuario));
            create(meGusta);
            return meGusta;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }

    }

    
    public CoMeGusta yaNoMeGusta(int idMeGusta, String idUsuario) {
        try {
            CoMeGusta meGusta = find(idMeGusta);
            if (meGusta != null) {
                meGusta.setModifico(new Usuario(idUsuario));
                meGusta.setEliminado(Constantes.BOOLEAN_FALSE);
                meGusta.setFechaModifico(new Date());
                meGusta.setHoraModifico(new Date());
                create(meGusta);
            }
            return meGusta;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }
}
