/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.orden.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CoNoticia;
import sia.modelo.OcOrdenCoNoticia;
import sia.modelo.Orden;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.comunicacion.impl.CoComentarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaSiAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class OcOrdenCoNoticiaImpl extends AbstractFacade<OcOrdenCoNoticia> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public OcOrdenCoNoticiaImpl() {
	super(OcOrdenCoNoticia.class);
    }    
    @Inject
    private CoComentarioImpl coComentarioRemote;
    @Inject
    private CoNoticiaSiAdjuntoImpl coNoticiaSiAdjuntoRemote;

    
    public void guardarNoticia(String sesion, CoNoticia coNoticia, Orden orden) {
	OcOrdenCoNoticia o = new OcOrdenCoNoticia();
	o.setCoNoticia(coNoticia);
	o.setOrden(orden);
	o.setApCampo(orden.getApCampo());
	o.setActiva(Constantes.BOOLEAN_TRUE);
	//
	o.setGenero(new Usuario(sesion));
	o.setFechaGenero(new Date());
	o.setHoraGenero(new Date());
	o.setEliminado(Constantes.NO_ELIMINADO);
	create(o);

    }

    
    public void finalizarNoticia(String sesion, int idOrden) {
	List<NoticiaVO> lo = traerNoticiaPorOrden(idOrden, false);
	if (lo != null) {
	    for (NoticiaVO noticiaVO : lo) {
		OcOrdenCoNoticia o = find(noticiaVO.getIdRelacionNoticia());
		o.setActiva(Constantes.BOOLEAN_FALSE);
		//
		o.setGenero(new Usuario(sesion));
		o.setFechaGenero(new Date());
		o.setHoraGenero(new Date());
		o.setEliminado(Constantes.NO_ELIMINADO);
		edit(o);
	    }
	}
    }

    
    public List<NoticiaVO> traerNoticiaPorOrden(int idOrden, boolean traerComentario) {
	clearQuery();
	List<NoticiaVO> ln = null;
	try {
	    query.append("select n.id, n.COMENTARIOS, n.titulo, n.mensaje, n.mensaje_Automatico, ");
	    query.append(" u.id, u.nombre, n.fecha_genero, n.hora_genero, ");
	    query.append(" (select count(ADJ.ID) as ADJUNTOS From CO_NOTICIA_SI_ADJUNTO ADJ where ADJ.CO_NOTICIA = n.ID AND ADJ.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("')");
	    query.append(" ,ornot.id, o.ap_campo from oc_orden_co_noticia ornot");
	    query.append(" inner join orden o on ornot.orden = o.id ");
	    query.append(" inner join co_noticia n on ornot.co_noticia = n.id ");
	    query.append(" inner join usuario u on n.genero = u.id ");
	    query.append(" where o.id = ").append(idOrden);
	    query.append(" and ornot.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and n.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and ornot.activa = '").append(Constantes.BOOLEAN_TRUE).append("'");
	    query.append(" order by n.id  desc");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		ln = new ArrayList<NoticiaVO>();
		for (Object[] objects : lo) {
		    ln.add(castVO(objects, traerComentario));
		}
	    }
	    return ln;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "ocurrio un errror al traer las noticias . . . " + e.getMessage());
	    return null;
	}
    }

    
    public List<NoticiaVO> traerNoticiaPorUsuario(String idUsuario, boolean traerComentario, int idCampo) {
	clearQuery();
	List<NoticiaVO> ln = null;
	try {
	    query.append("select n.id, n.COMENTARIOS, n.titulo, n.mensaje, n.mensaje_Automatico, ");
	    query.append(" u.id, u.nombre, n.fecha_genero, n.hora_genero, ");
	    query.append(" (select count(ADJ.ID) as ADJUNTOS From CO_NOTICIA_SI_ADJUNTO ADJ where ADJ.CO_NOTICIA = n.ID AND ADJ.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("')");
	    query.append(" ,ornot.id, o.ap_campo from oc_orden_co_noticia ornot");
	    query.append(" inner join orden o on ornot.orden = o.id ");
	    query.append(" inner join co_noticia n on ornot.co_noticia = n.id ");
	    query.append(" inner join usuario u on n.genero = u.id ");
	    query.append(" inner join CO_COMPARTIDA cp on cp.co_noticia = n.id  and cp.usuario = '").append(idUsuario).append("'");
	    query.append(" inner join ap_campo campo on ornot.ap_campo = campo.id ");
	    query.append(" where ornot.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and campo.id = ").append(idCampo);
	    query.append(" and ornot.activa = '").append(Constantes.BOOLEAN_TRUE).append("'");
	    query.append(" and n.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" order by n.id  desc");
	    UtilLog4j.log.info(this, "Q: : : :  noticia por usuario : " + query.toString());
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		ln = new ArrayList<NoticiaVO>();
		for (Object[] objects : lo) {
		    ln.add(castVO(objects, traerComentario));
		}
	    }
	    return ln;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "ocurrio un errror al traer las noticias . . . " + e.getMessage());
	    return null;
	}
    }

    
    public long totalNoticiaPorUsuario(String idUsuario, int idCampo) {
	clearQuery();
	long ln = 0;
	try {
	    query.append("select count(ornot.id) from oc_orden_co_noticia ornot");
	    query.append(" inner join orden o on ornot.orden = o.id ");
	    query.append(" inner join co_noticia n on ornot.co_noticia = n.id ");
	    query.append(" inner join usuario u on n.genero = u.id ");
	    query.append(" inner join CO_COMPARTIDA cp on cp.co_noticia = n.id  and cp.usuario = '").append(idUsuario).append("'");
	    query.append(" inner join ap_campo campo on ornot.ap_campo = campo.id ");
	    query.append(" where ornot.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and campo.id = ").append(idCampo);
	    query.append(" and ornot.activa = '").append(Constantes.BOOLEAN_TRUE).append("'");
	    query.append(" and n.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    ln = ((Long) em.createNativeQuery(query.toString()).getSingleResult());
	    return ln;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "ocurrio un errror al contar las noticias . . . " + e.getMessage());
	}
	return ln;
    }

    private NoticiaVO castVO(Object[] obj, boolean traerComentario) {
	NoticiaVO noticiaVo = new NoticiaVO();
	noticiaVo.setId((Integer) obj[0]);
	noticiaVo.setComentarios((Integer) obj[1]);
	noticiaVo.setTitulo(String.valueOf(obj[2]));
	noticiaVo.setMensaje(String.valueOf(obj[3]));
	noticiaVo.setMensajeAutomatico(String.valueOf(obj[4]));
	noticiaVo.setGenero(String.valueOf(obj[5]));
	noticiaVo.setUsuarioNombre(String.valueOf(obj[6]));
	noticiaVo.setFechaGenero((Date) obj[7]);
	noticiaVo.setHoraGenero((Date) obj[8]);
	noticiaVo.setAdjuntosCount(obj[9] != null ? (Long) obj[9] : Constantes.CERO);
	noticiaVo.setIdRelacionNoticia(obj[10] != null ? (Integer) obj[10] : 0);
	noticiaVo.setIdCampo((Integer) obj[11]);
	noticiaVo.setMostrarComentarios(false);
	noticiaVo.setComentar(false);
	if (traerComentario) {
	    noticiaVo.setListaComentario(coComentarioRemote.traerComentariosPorNoticia(noticiaVo.getId()));
	    UtilLog4j.log.info(this, "Lista comentarios: : " + noticiaVo.getListaComentario().size());
	}
	if (noticiaVo.getAdjuntosCount() > 0) {
	    noticiaVo.setListaAdjunto(coNoticiaSiAdjuntoRemote.getAdjuntosNoticia(noticiaVo.getId(), ""));
	}

	return noticiaVo;
    }

    
    public List<NoticiaVO> traerTodasNoticiasPorOrden(int idOrden, boolean traerComentario) {
	clearQuery();
	List<NoticiaVO> ln = null;
	try {
	    query.append("select n.id, n.COMENTARIOS, n.titulo, n.mensaje, n.mensaje_Automatico, ");
	    query.append(" u.id, u.nombre, n.fecha_genero, n.hora_genero, ");
	    query.append(" (select count(ADJ.ID) as ADJUNTOS From CO_NOTICIA_SI_ADJUNTO ADJ where ADJ.CO_NOTICIA = n.ID AND ADJ.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("')");
	    query.append(" ,ornot.id, o.ap_campo from oc_orden_co_noticia ornot");
	    query.append(" inner join orden o on ornot.orden = o.id ");
	    query.append(" inner join co_noticia n on ornot.co_noticia = n.id ");
	    query.append(" inner join usuario u on n.genero = u.id ");
	    query.append(" where o.id = ").append(idOrden);
	    query.append(" and ornot.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and n.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" order by n.id  desc");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo != null) {
		ln = new ArrayList<NoticiaVO>();
		for (Object[] objects : lo) {
		    ln.add(castVO(objects, traerComentario));
		}
	    }
	    return ln;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "ocurrio un errror al traer las noticias . . . " + e.getMessage());
	    return null;
	}
    }
}
