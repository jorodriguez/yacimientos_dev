/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.huesped.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.SiUsuarioTipo;
import sia.modelo.Usuario;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Stateless 
public class SiUsuarioTipoImpl extends AbstractFacade<SiUsuarioTipo> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }
    //
    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgTipoImpl sgTipoRemote;    
    @Inject
    private SgOficinaImpl sgOficinaRemote;

    public SiUsuarioTipoImpl() {
	super(SiUsuarioTipo.class);
    }

    
    public List<UsuarioTipoVo> getListUser(int idTipo, int idOficina) {
	List<UsuarioTipoVo> lv = new ArrayList<>();
	try {
	    clearQuery();
	    query.append("select uc.id,");
	    query.append(" t.id, ");
	    query.append(" t.nombre,");
	    query.append(" u.id,");
	    query.append(" u.nombre, ");
	    query.append(" u.email, ");
	    query.append(" u.telefono, ");
	    query.append(" case when uc.descripcion is not null then uc.descripcion");
	    query.append("     when uc.descripcion is null then '' ");
	    query.append(" end");
	    query.append(" from si_usuario_tipo uc, usuario u, sg_tipo t  ");
	    query.append(" where uc.sg_tipo =  ").append(idTipo);
	    query.append(" and uc.usuario = u.id  ");
	    query.append(" and uc.sg_oficina = ").append(idOficina);
	    query.append(" and uc.sg_tipo = t.id  ");
	    query.append(" and uc.eliminado = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" order by u.nombre asc ");

	    UtilLog4j.log.debug(this, query.toString());

	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();

	    for (Object[] objects : lo) {
		lv.add(castUsuarioTipoVo(objects));
	    }
	    return lv;
	} catch (Exception e) {
	    UtilLog4j.log.error(this, e.getMessage(), e);
	    return lv;
	}
    }

    private UsuarioTipoVo castUsuarioTipoVo(Object[] objects) {
	UsuarioTipoVo usuarioTipoVo = new UsuarioTipoVo();
	usuarioTipoVo.setId((Integer) objects[0]);
	usuarioTipoVo.setIdTipo((Integer) objects[1]);
	usuarioTipoVo.setTipo((String) objects[2]);
	usuarioTipoVo.setIdUser((String) objects[3]);
	usuarioTipoVo.setUsuario((String) objects[4]);
	usuarioTipoVo.setCorreo((String) objects[5]);
	usuarioTipoVo.setTelefono((String) objects[6]);
	usuarioTipoVo.setDescripcion((String) objects[7]);
	return usuarioTipoVo;
    }

    
    public void guardarUsurioCopiado(String idUser, int idTipo, String cadena, String desc, int idOficina) {
	SiUsuarioTipo siUsuarioTipo = new SiUsuarioTipo();
	siUsuarioTipo.setUsuario(usuarioRemote.buscarPorNombre(cadena));
	siUsuarioTipo.setSgTipo(sgTipoRemote.find(idTipo));
	siUsuarioTipo.setSgOficina(sgOficinaRemote.find(idOficina));
	siUsuarioTipo.setDescripcion(desc);
	siUsuarioTipo.setGenero(new Usuario(idUser));
	siUsuarioTipo.setFechaGenero(new Date());
	siUsuarioTipo.setHoraGenero(new Date());
	siUsuarioTipo.setEliminado(Constantes.NO_ELIMINADO);
	create(siUsuarioTipo);	
    }

    
    public void quitarUsuario(String usuario, int idUserCopy) {
	SiUsuarioTipo siUsuarioTipo = find(idUserCopy);
	siUsuarioTipo.setEliminado(Constantes.ELIMINADO);
	siUsuarioTipo.setModifico(new Usuario(usuario));
	siUsuarioTipo.setFechaModifico(new Date());
	siUsuarioTipo.setHoraModifico(new Date());
	edit(siUsuarioTipo);
    }
}
