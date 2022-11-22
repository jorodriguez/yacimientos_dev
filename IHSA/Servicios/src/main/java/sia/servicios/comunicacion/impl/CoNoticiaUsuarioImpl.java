/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CoNoticiaUsuario;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class CoNoticiaUsuarioImpl extends AbstractFacade<CoNoticiaUsuario>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    private CoNoticiaImpl coNoticiaRemote;
    @Inject
    private SgTipoImpl sgTipoRemote;
    private StringBuilder q = new StringBuilder();

    private void limpiar() {
        q.delete(0, q.length());
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CoNoticiaUsuarioImpl() {
        super(CoNoticiaUsuario.class);
    }

    
    public boolean crearRelacionNoticiaUsuario(String idUsuarioBaja, int idNoticia, int sgTipo, String idUsuario) {
        try {//23 tipo de baja
            CoNoticiaUsuario coNoticiaUsuario = new CoNoticiaUsuario();
            coNoticiaUsuario.setCoNoticia(coNoticiaRemote.find(idNoticia));
            coNoticiaUsuario.setUsuario(new Usuario(idUsuario));
            coNoticiaUsuario.setSgTipo(sgTipoRemote.find(23));
            coNoticiaUsuario.setEliminado(Constantes.BOOLEAN_FALSE);
            coNoticiaUsuario.setGenero(new Usuario(idUsuario));
            coNoticiaUsuario.setFechaGenero(new Date());
            coNoticiaUsuario.setHoraGenero(new Date());
            create(coNoticiaUsuario);
            return true;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }
    }

    
    public int buscarIdNoticiaPorUsuario(String idUsuario, int idTipo) {
        try {
            limpiar();
            q.append("select nu.co_noticia from co_noticia_usuario nu where nu.usuario = '").append(idUsuario).append("'");
            q.append(" and nu.sg_tipo = ").append(idTipo);
            q.append(" and nu.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");

            return ((Integer) em.createNativeQuery(q.toString()).getSingleResult());

        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return 0;
        }

    }

}
