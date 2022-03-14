/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.requisicion.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CoNoticia;
import sia.modelo.OcRequisicionCoNoticia;
import sia.modelo.Requisicion;
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
@LocalBean 
public class OcRequisicionCoNoticiaImpl extends AbstractFacade<OcRequisicionCoNoticia>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public OcRequisicionCoNoticiaImpl() {
	super(OcRequisicionCoNoticia.class);
    }    
    @Inject
    private CoComentarioImpl coComentarioRemote;
    @Inject
    private CoNoticiaSiAdjuntoImpl coNoticiaSiAdjuntoRemote;

    
    public void guardarNoticia(String sesion, CoNoticia coNoticia, Requisicion requisicion) {
	OcRequisicionCoNoticia o = new OcRequisicionCoNoticia();
	o.setCoNoticia(coNoticia);
	o.setRequisicion(requisicion);
	o.setApCampo(requisicion.getApCampo());
	o.setActiva(Constantes.BOOLEAN_TRUE);
	//
	o.setGenero(new Usuario(sesion));
	o.setFechaGenero(new Date());
	o.setHoraGenero(new Date());
	o.setEliminado(Constantes.NO_ELIMINADO);
	create(o);

    }

    
    public List<NoticiaVO> traerNoticiaPorRequisicion(int idReq, boolean traerComentario) {
	clearQuery();
	List<NoticiaVO> ln = null;
	try {
	    query.append("select n.id, n.COMENTARIOS, n.titulo, n.mensaje, n.mensaje_Automatico, ");
	    query.append(" u.id, u.nombre, n.fecha_genero, n.hora_genero, ");
	    query.append(" (select count(ADJ.ID) as ADJUNTOS From CO_NOTICIA_SI_ADJUNTO ADJ where ADJ.CO_NOTICIA = n.ID AND ADJ.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("')");
	    query.append(" ,reqnot.id, o.ap_campo from oc_requisicion_co_noticia reqnot");
	    query.append(" inner join requisicion o on reqnot.requisicion = o.id ");
	    query.append(" inner join co_noticia n on reqnot.co_noticia = n.id ");
	    query.append(" inner join usuario u on n.genero = u.id ");
	    query.append(" where o.id = ").append(idReq);
	    query.append(" and reqnot.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and reqnot.activa = '").append(Constantes.BOOLEAN_TRUE).append("'");
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

    
    public List<NoticiaVO> traerTodasNoticiaPorRequisicion(int idReq, boolean traerComentario) {
	clearQuery();
	List<NoticiaVO> ln = null;
	try {
	    query.append("select n.id, n.COMENTARIOS, n.titulo, n.mensaje, n.mensaje_Automatico, ");
	    query.append(" u.id, u.nombre, n.fecha_genero, n.hora_genero, ");
	    query.append(" (select count(ADJ.ID) as ADJUNTOS From CO_NOTICIA_SI_ADJUNTO ADJ where ADJ.CO_NOTICIA = n.ID AND ADJ.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("')");
	    query.append(" ,reqnot.id, o.ap_campo from oc_requisicion_co_noticia reqnot");
	    query.append(" inner join requisicion o on reqnot.requisicion = o.id ");
	    query.append(" inner join co_noticia n on reqnot.co_noticia = n.id ");
	    query.append(" inner join usuario u on n.genero = u.id ");
	    query.append(" where o.id = ").append(idReq);
	    query.append(" and reqnot.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
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

    
    public List<NoticiaVO> traerNoticiaPorUsuario(String idUsuario, boolean traerComentario, int idCampo) {
	clearQuery();
	List<NoticiaVO> ln = null;
	try {
	    query.append("select n.id, n.COMENTARIOS, n.titulo, n.mensaje, n.mensaje_Automatico, ");
	    query.append(" u.id, u.nombre, n.fecha_genero, n.hora_genero, ");
	    query.append(" (select count(ADJ.ID) as ADJUNTOS From CO_NOTICIA_SI_ADJUNTO ADJ where ADJ.CO_NOTICIA = n.ID AND ADJ.ELIMINADO='").append(Constantes.BOOLEAN_FALSE).append("')");
	    query.append(" ,reqnot.id, o.ap_campo from oc_requisicion_co_noticia reqnot");
	    query.append(" inner join requisicion o on reqnot.requisicion = o.id ");
	    query.append(" inner join co_noticia n on reqnot.co_noticia = n.id ");
	    query.append(" inner join usuario u on n.genero = u.id ");
	    query.append(" inner join CO_COMPARTIDA cp on cp.co_noticia = n.id  and cp.usuario = '").append(idUsuario).append("'");
	    query.append(" inner join ap_campo campo on reqnot.ap_campo = campo.id ");
	    query.append(" where reqnot.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and reqnot.activa = '").append(Constantes.BOOLEAN_TRUE).append("'");
	    query.append(" and campo.id = ").append(idCampo);
	    query.append(" and n.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" order by n.id  desc");
	    List<Object[]> lo = em.createNativeQuery(query.toString()).getResultList();
	    if (lo == null) {
                ln = Collections.emptyList();
            } else {
		ln = new ArrayList<>();
		for (Object[] objects : lo) {
		    ln.add(castVO(objects, traerComentario));
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "ocurrio un errror al traer las noticias . . . " + e.getMessage());
	    ln = Collections.emptyList();
	}
        
        return ln;
    }

    
    public long totalNoticiaPorUsuario(String idUsuario, int idCampo) {
	clearQuery();
	long lo = 0;
	try {
	    query.append("select count(reqnot.id) from oc_requisicion_co_noticia reqnot");
	    query.append(" inner join requisicion o on reqnot.requisicion = o.id ");
	    query.append(" inner join co_noticia n on reqnot.co_noticia = n.id ");
	    query.append(" inner join usuario u on n.genero = u.id ");
	    query.append(" inner join CO_COMPARTIDA cp on cp.co_noticia = n.id  and cp.usuario = '").append(idUsuario).append("'");
	    query.append(" inner join ap_campo campo on reqnot.ap_campo = campo.id ");
	    query.append(" where reqnot.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    query.append(" and reqnot.activa = '").append(Constantes.BOOLEAN_TRUE).append("'");
	    query.append(" and campo.id = ").append(idCampo);
	    query.append(" and n.eliminado = '").append(Constantes.NO_ELIMINADO).append("'");
	    lo = ((Long) em.createNativeQuery(query.toString()).getSingleResult());
	    return lo;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "ocurrio un errror al contar las noticias . . . " + e.getMessage());
	}
	return lo;
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
	noticiaVo.setAdjuntosCount(((Long) obj[9]).intValue());
	noticiaVo.setIdRelacionNoticia((Integer) obj[10]);
	noticiaVo.setIdCampo((Integer) obj[11]);
	noticiaVo.setComentar(false);
	if (traerComentario) {
	    noticiaVo.setListaComentario(coComentarioRemote.traerComentariosPorNoticia(noticiaVo.getId()));
	}

	if (noticiaVo.getAdjuntosCount() > 0) {
	    noticiaVo.setListaAdjunto(coNoticiaSiAdjuntoRemote.getAdjuntosNoticia(noticiaVo.getId(), ""));
	}

	return noticiaVo;
    }

    
    public void finalizarNotas(String sesion, int idRequisicion) {
	List<NoticiaVO> lo = traerNoticiaPorRequisicion(idRequisicion, false);
	if (lo != null) {
	    for (NoticiaVO noticiaVO : lo) {
		OcRequisicionCoNoticia o = find(noticiaVO.getIdRelacionNoticia());
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
}
