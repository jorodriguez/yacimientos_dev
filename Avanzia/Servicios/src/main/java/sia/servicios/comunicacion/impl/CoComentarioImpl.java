/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.CoComentario;
import sia.modelo.CoMeGusta;
import sia.modelo.CoNoticia;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.vo.ComentarioVO;
import sia.modelo.comunicacion.vo.MeGustaVO;
import sia.modelo.sistema.AbstractFacade;
import sia.notificaciones.sgl.impl.NotificacionServiciosGeneralesImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class CoComentarioImpl extends AbstractFacade<CoComentario>{

    @Inject
    private CoMeGustaImpl servicioCoMegusta;
    @Inject
    private CoComentarioMeGustaImpl servicioCoComentarioMegusta;
    @Inject
    private NotificacionServiciosGeneralesImpl notificacionServiciosGeneralesRemote;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolRemote;
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CoComentarioImpl() {
	super(CoComentario.class);
    }

    
    public boolean nuevoComentario(CoNoticia noticia, Usuario usuario, String comentario, boolean notificar, boolean isRecomedarioSeguridad, int campo, int modulo) {
	CoComentario c = new CoComentario();
	c.setCoNoticia(noticia);
	c.setGenero(usuario);
	c.setMensaje(comentario);
	c.setMegusta(0);
	c.setEliminado(false);
	c.setFechaGenero(new Date());
	c.setHoraGenero(new Date());
	try {
	    this.create(c);
	    //
	    if (notificar) {
		notificacionServiciosGeneralesRemote.enviarComentarioNoticia(noticia, c, isRecomedarioSeguridad, campo, modulo);
	    }
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return false;
	}
    }

    
    public boolean modificarComentario(CoComentario comentario, String idUsuario) {
	try {
	    this.edit(comentario);
	    UtilLog4j.log.info(this, "Se modificó correctament el comentario");
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return false;
	}
    }

    
    public List<ComentarioVO> getComentarios(int idNoticia, int maxResult, String idUsuario) {
	List lista = null;
	Query q;
	try {
	    clearQuery();
	    query.append("SELECT com.id, com.MENSAJE, com.MEGUSTA, com.FECHA_GENERO, com.HORA_GENERO, u.id, u.FOTO, u.NOMBRE,");
	    query.append(" (select comm.co_Megusta From CO_COMENTARIO_ME_GUSTA comm where comm.CO_COMENTARIO = com.id AND comm.GENERO = '").append(idUsuario).append("' AND comm.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("')");
	    query.append(" FROM CO_COMENTARIO com,  USUARIO u");
	    query.append(" WHERE com.GENERO = u.id AND com.CO_NOTICIA = ").append(idNoticia);
	    query.append(" AND com.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" ORDER BY com.id DESC");

	    if (maxResult != 0) {
		lista = em.createNativeQuery(query.toString()).setMaxResults(maxResult).getResultList();
	    } else {
		//
		lista = em.createNativeQuery(query.toString()).getResultList();
	    }

	    return castVO(lista);

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer comentarios de una noticia" + e.getMessage());
	    return null;
	}
    }

    
    public List<ComentarioVO> getComentariosFiltrados(int idNoticia, String idUsuario) {
	List lista = null;
	Query q;
	try {
	    q = em.createNativeQuery("SELECT com.id,"
		    + " com.MENSAJE,"
		    + " com.MEGUSTA,"
		    + " com.FECHA_GENERO,"
		    + " com.HORA_GENERO,"
		    + " u.id,"
		    + " u.FOTO,"
		    + " u.NOMBRE,"
		    + " (select comm.co_Megusta From CO_COMENTARIO_ME_GUSTA comm where comm.CO_COMENTARIO = com.id AND comm.GENERO = '" + idUsuario + "' AND comm.ELIMINADO='" + Constantes.BOOLEAN_FALSE + "')"
		    + " FROM CO_COMENTARIO com, "
		    + " USUARIO u"
		    + " WHERE com.GENERO = u.id AND com.CO_NOTICIA = " + idNoticia
		    + " AND com.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
		    + " ORDER BY com.id DESC");
	    lista = q.getResultList();
	    return castVO(lista);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer comentarios de una noticia" + e.getMessage());
	    return null;
	}
    }

    
    public List<ComentarioVO> traerComentariosPorNoticia(int idNoticia) {
	List lista = null;
	clearQuery();
	try {
	    query.append("SELECT com.id, com.MENSAJE, com.MEGUSTA, com.FECHA_GENERO,");
	    query.append(" com.HORA_GENERO, u.id,  u.FOTO, u.NOMBRE,  0 ");
	    query.append(" FROM CO_COMENTARIO com, USUARIO u");
	    query.append(" WHERE com.GENERO = u.id AND com.CO_NOTICIA = ").append(idNoticia);
	    query.append(" AND com.ELIMINADO = '").append(Constantes.BOOLEAN_FALSE).append("'");
	    query.append(" ORDER BY com.id DESC");
	    //       System.out.println("q: comentario :  : :" + query.toString());
	    lista = em.createNativeQuery(query.toString()).getResultList();
	    return castVO(lista);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer comentarios de una noticia" + e.getMessage());
	    return null;
	}
    }

    private List<ComentarioVO> castVO(List lista) {
	ComentarioVO comentarioVo = null;
	List<ComentarioVO> lo = null;
	if (lista != null) {
	    Collections.reverse(lista);
	    lo = new ArrayList<ComentarioVO>();
	    for (Iterator it = lista.iterator(); it.hasNext();) {
		Object[] obj = (Object[]) it.next();
		comentarioVo = new ComentarioVO();
		comentarioVo.setId((Integer) obj[0]);
		comentarioVo.setMensaje(String.valueOf(obj[1]));
		comentarioVo.setMeGusta((Integer) obj[2]);
		comentarioVo.setFechaGenero((Date) obj[3]);
		comentarioVo.setHoraGenero((Date) obj[4]);
		comentarioVo.setGenero(String.valueOf(obj[5]));
		comentarioVo.setUsuarioFoto(String.valueOf(obj[6]));
		comentarioVo.setUsuarioNombre(String.valueOf(obj[7]));
		comentarioVo.setIdMegusta(obj[8] != null ? (Integer) obj[8] : 0);
//                    m = getLikes(comentarioVo.getId(), idUsuario, 3);
//                    UtilLog4j.log.info(this,"despues");
//                    if (m != null) {
//                        UtilLog4j.log.info(this,"entro");
//                        for (MeGustaVO likeUser : m) {
//                            UtilLog4j.log.info(this,"usuari " + likeUser.getNombreUsuario());
////                            if (!likeUser.getGenero().equals(idUsuario)) {
//                            con = con + likeUser.getNombreUsuario() + ",";
////                            }
//                        }
//                    }
//                    comentarioVo.setUsuariosLike("(" + con + ")");
		lo.add(comentarioVo);
	    }
	}
	return lo;
    }

    
    public void meGusta(Integer idComentario, String usuario) {
	CoMeGusta mg = null;
	try {
	    mg = this.servicioCoMegusta.meGusta(usuario);
	    if (mg != null) {
		CoComentario comentario = find(idComentario);
		//guardar relación
		if (this.servicioCoComentarioMegusta.crearMeGusta(comentario, mg, usuario)) {
		    comentario.setMegusta(comentario.getMegusta() + 1);
		    this.edit(comentario);
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    
    public void yaNoMeGusta(Integer idComentario, Integer idMeGusta, String usuario) {
	CoMeGusta mg = null;
	try {
	    mg = this.servicioCoMegusta.yaNoMeGusta(idMeGusta, usuario);
	    if (mg != null) {
		//eliminar relación
		if (this.servicioCoComentarioMegusta.eliminarMeGusta(idComentario, idMeGusta, usuario)) {
		    CoComentario comentario = find(idComentario);
		    comentario.setMegusta(comentario.getMegusta() - 1);
		    this.edit(comentario);
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    
    public boolean eliminarComentario(Integer idComentario, String idUsuario) {
	try {
	    CoComentario comentario = find(idComentario);
	    comentario.setEliminado(Constantes.BOOLEAN_TRUE);
	    //faltan los campos de modifico ETc
	    this.edit(comentario);
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return false;
	}
    }

    
    public List<MeGustaVO> getLikes(Integer idComentario, String idUsuario, int maxResult) {
	List lista = null;
	String ret = "";
	Query q;
	try {
	    q = em.createNativeQuery("SELECT lik.FECHA_GENERO,"
		    + " lik.HORA_GENERO,"
		    + " u.nombre,"
		    + " u.foto"
		    + " FROM CO_COMENTARIO_ME_GUSTA com,CO_MEGUSTA lik ,usuario u "
		    + " where com.CO_COMENTARIO = " + idComentario
		    + " and com.eliminado = '" + Constantes.BOOLEAN_FALSE + "'"
		    + " AND com.genero = u.id"
		    + " and com.CO_MEGUSTA = lik.id"
		    + " AND lik.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
		    + " ORDER BY com.id ASC");
//            if (maxResult == 0) {
	    lista = q.getResultList();
//            } else {
//                lista = q.setMaxResults(maxResult).getResultList();
//            }
	    return castVoLikes(lista);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer likes");
	    return null;
	}
    }

    private List<MeGustaVO> castVoLikes(List lista) {
	List<MeGustaVO> lo = null;
	MeGustaVO meGustaVO = null;
	if (lista != null) {
	    Collections.reverse(lista);
	    lo = new ArrayList<MeGustaVO>();
	    for (Iterator it = lista.iterator(); it.hasNext();) {
		Object[] obj = (Object[]) it.next();
		meGustaVO = new MeGustaVO();
		meGustaVO.setFechaGenero((Date) obj[0]);
		meGustaVO.setHoraGenero((Date) obj[1]);
		meGustaVO.setNombreUsuario((String) obj[2]);
		meGustaVO.setFotoUsuario((String) obj[3]);
		lo.add(meGustaVO);
	    }
	}
	return lo;
    }
}
