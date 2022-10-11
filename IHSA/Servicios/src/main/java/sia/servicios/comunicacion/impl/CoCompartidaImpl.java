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
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import sia.constantes.Constantes;
import sia.modelo.CoCompartida;
import sia.modelo.CoNoticia;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Stateless 
public class CoCompartidaImpl extends AbstractFacade<CoCompartida> {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;
    
    @Inject
    DSLContext dbCtx;
    
    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CoCompartidaImpl() {
	super(CoCompartida.class);
    }

    @Inject
    private CoComentarioImpl coComentarioRemote;
    @Inject
    private UsuarioImpl usuarioServicioImpl;

    
    public boolean compartir(CoNoticia noticia, Usuario usuarioRealizo) {
        boolean retVal = false;
        
	try {
	    CoCompartida compartida = new CoCompartida();
	    compartida.setCoNoticia(noticia);
	    compartida.setUsuario(null);
	    compartida.setEliminado(false);
	    compartida.setGenero(usuarioRealizo);
	    compartida.setFechaGenero(new Date());
	    compartida.setHoraGenero(new Date());
	    this.create(compartida);
	    retVal = true;
	} catch (Exception e) {
            LOGGER.error(e);
	}
        
        return retVal;
    }

    
    public boolean compartir(CoNoticia noticia, Usuario usuario, Usuario realizo) {
        boolean retVal = false;
        
	try {
	    CoCompartida compartida = new CoCompartida();
	    compartida.setCoNoticia(noticia);
	    compartida.setUsuario(usuario);
	    compartida.setGenero(realizo);
	    compartida.setEliminado(Constantes.BOOLEAN_FALSE);
	    compartida.setFechaGenero(new Date());
	    compartida.setHoraGenero(new Date());
	    this.create(compartida);
	    retVal = true;
	} catch (Exception e) {
	    LOGGER.error(e);
	}
        
        return retVal;
    }

    
    public boolean compartir(CoNoticia noticia, Set<Usuario> listaUsuarios) {
        boolean retVal = false;
	try {
	    for (Usuario elemento : listaUsuarios) {
		if (this.addCompartido(noticia.getId(), elemento.getId())) {
		    CoCompartida compartida = new CoCompartida();
		    compartida.setCoNoticia(noticia);
		    compartida.setUsuario(elemento);
		    compartida.setEliminado(Constantes.BOOLEAN_FALSE);
		    compartida.setFechaGenero(new Date());
		    compartida.setHoraGenero(new Date());
		    this.create(compartida);
		}
	    }
	    retVal = true;
	} catch (Exception e) {
	    LOGGER.error(e);
	}
        
        return retVal;
    }

    
    public boolean compartir(CoNoticia noticia, List<UsuarioRolVo> listaUsuarios) {
        boolean retVal = false;
        
	try {
	    for (UsuarioRolVo elemento : listaUsuarios) {
		if (this.addCompartido(noticia.getId(), elemento.getIdUsuario())) {
		    CoCompartida compartida = new CoCompartida();
		    compartida.setCoNoticia(noticia);
		    compartida.setUsuario(usuarioServicioImpl.find(elemento.getIdUsuario()));
		    compartida.setEliminado(Constantes.BOOLEAN_FALSE);
		    compartida.setFechaGenero(new Date());
		    compartida.setHoraGenero(new Date());
		    this.create(compartida);
		}
	    }
	    retVal = true;
	} catch (Exception e) {
	    LOGGER.error(e);
	}
        
        return retVal;
    }
    
    
    public List<NoticiaVO> getNoticias(String idUsuario, int maxResult, Integer onlyIdNoticia) {
	List<NoticiaVO> lista = null;
	String qy, comodin;
	
        int limit = (maxResult > 0 ? maxResult : 5);
        
        
	try {
	    comodin = "noti.ID = comp.CO_NOTICIA";
	    
            if (onlyIdNoticia != 0) {
		comodin = "noti.ID = " + onlyIdNoticia + " AND comp.CO_NOTICIA=" + onlyIdNoticia;
	    }
	    
            //TODO : revisar si es factible cambiar la consulta a esta, ver como aplicar lo del comodín:
            /*
            WITH filtradas AS (
                SELECT noti.ID
                FROM CO_NOTICIA noti
                    INNER JOIN usuario u ON noti.genero = u.id
                    INNER JOIN co_privacidad priv ON NOTI.co_privacidad = PRIV.ID
                WHERE 1 = 1
                    AND noti.co_privacidad = 2
                    AND noti.eliminado = False
                    OR noti.id in (
                        SELECT comp.co_noticia
                        FROM co_compartida comp
                        WHERE comp.usuario = 'JARAMIREZ'
                            AND comp.eliminado = False
                    )
                ORDER BY noti.FECHA_GENERO + noti.HORA_GENERO DESC
                LIMIT 5
            )
            SELECT noti.ID, noti.COMENTARIOS, noti.FECHA_GENERO, noti.HORA_GENERO, noti.MEGUSTA, noti.MENSAJE
                , noti.MENSAJE_AUTOMATICO, noti.TITULO, u.id as usuarioId, u.FOTO AS usuarioFoto
                , u.NOMBRE as usuarioNombre, priv.NOMBRE as privacidadNombre, priv.id as privacidadId
            FROM CO_NOTICIA noti
                INNER JOIN usuario u ON noti.genero = u.id
                INNER JOIN co_privacidad priv ON NOTI.co_privacidad = PRIV.ID
                INNER JOIN filtradas f ON noti.id = f.id
            WHERE 1 = 1
            */
            
            
            qy = " SELECT "
                    + " noti.ID, "
		    + " noti.COMENTARIOS,"
		    + " noti.FECHA_GENERO,"
		    + " noti.HORA_GENERO,"
		    + " noti.MEGUSTA as ME_GUSTA,"
		    + " noti.MENSAJE,"
		    + " noti.MENSAJE_AUTOMATICO, "
		    + " noti.TITULO,"
		    + " u.id as usuario_Id,"
		    + " u.FOTO AS usuario_Foto,"
		    + " u.NOMBRE as usuario_Nombre,"
		    + " priv.NOMBRE as privacidad_Nombre, "
		    + " priv.id as privacidad_Id \n"
		    + "FROM CO_NOTICIA noti, CO_COMPARTIDA comp, usuario u, co_privacidad priv \n"
		    + "WHERE (((noti.CO_privacidad = 2) OR ((comp.USUARIO = '" + idUsuario + "') AND (noti.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'))) \n"
		    + " AND (" + comodin + ") AND (noti.GENERO = u.id) \n"
		    + " AND noti.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "') \n"
		    + " AND NOTI.CO_privacidad = PRIV.ID \n"
		    + " AND noti.id = comp.CO_NOTICIA \n"
		    + "ORDER BY noti.FECHA_GENERO DESC, noti.HORA_GENERO DESC \n"
                    + "LIMIT "  + limit;

            lista = dbCtx.fetch(qy).into(NoticiaVO.class);

	} catch (DataAccessException e) {
	    LOGGER.error(e);
            lista = Collections.emptyList();
	}
        
        return lista;
    }

    private List<NoticiaVO> castVO(List lista) {
	NoticiaVO noticiaVo;
	List<NoticiaVO> lo = new ArrayList<>();
	for (Iterator it = lista.iterator(); it.hasNext();) {
	    Object[] obj = (Object[]) it.next();
	    noticiaVo = new NoticiaVO();
	    noticiaVo.setId((Integer) obj[0]);
	    noticiaVo.setComentarios((Integer) obj[1]);
	    noticiaVo.setFechaGenero((Date) obj[2]);
	    noticiaVo.setHoraGenero((Date) obj[3]);
	    noticiaVo.setMeGusta((Integer) obj[4]);
	    noticiaVo.setMensaje(String.valueOf(obj[5]));
	    noticiaVo.setMensajeAutomatico(String.valueOf(obj[6]));
	    noticiaVo.setTitulo(String.valueOf(obj[7]));
	    noticiaVo.setGenero(String.valueOf(obj[8]));
	    noticiaVo.setUsuarioFoto(String.valueOf(obj[9]));
	    noticiaVo.setUsuarioNombre(String.valueOf(obj[10]));
	    noticiaVo.setPrivacidadNombre(String.valueOf(obj[11]));
	    noticiaVo.setPrivacidadId((Integer) obj[12]);
	    noticiaVo.setComentar(false);
	    noticiaVo.setListaComentario(coComentarioRemote.traerComentariosPorNoticia(noticiaVo.getId()));
	    lo.add(noticiaVo);
	}
	return lo;
    }
//    public List<InvitadosComunicado> getUsuarioPorNoticia(int idNoticia, String idAutor) {
//        return em.createQuery("SELECT  i FROM InvitadosComunicado i WHERE i.comunicado.id = :idComunicado AND i.usuario.id <> :autor").setParameter("idComunicado", idComunicado).setParameter("autor", idAutor).getResultList();
//    }

    
    public boolean eliminarCompartir(CoNoticia noticia, String idUsuario) {
        boolean retVal = false;
	try {
	    CoCompartida comp = this.getCoCompartida(noticia, idUsuario);
	    if (comp != null) {
		comp.setEliminado(Constantes.BOOLEAN_TRUE);
		this.edit(comp);
//          Eliminar logicamente poner los campos especiales
	    }
	    retVal = true;
	} catch (Exception e) {
	    LOGGER.error(e);
	}
        
        return retVal;
    }

    private CoCompartida getCoCompartida(CoNoticia noticia, String idUsuario) {
        CoCompartida retVal = null;
	
        try {
	    
	    String sql = 
                    " SELECT *  FROM CO_COMPARTIDA co"
                    + " where co.co_noticia = ? "
		    + "   AND usuario = ? "
		    + "   and co.eliminado = ? ";
	    
            retVal = (CoCompartida) em
                    .createNativeQuery(sql, CoCompartida.class)
                    .setParameter(1, noticia.getId())
                    .setParameter(2, idUsuario)
                    .setParameter(3, Constantes.BOOLEAN_FALSE)
                    .getSingleResult();
	    
	} catch (NonUniqueResultException nu) {
            LOGGER.error(nu);
	}
        
        return retVal;
    }

    
    public List<ComparteCon> getListaUsuarioCompartidos(Integer idNoticia) {
	List<ComparteCon> retVal = null;
        
	try {
	    String sql = "SELECT comp.id,"//0
		    + " u.foto,"//1
		    + " u.nombre,"//2
		    + " comp.FECHA_GENERO,"//3
		    + " comp.hora_GENERO,"//4
		    + " u.email"//5
		    + " FROM co_Compartida comp,Usuario u"
		    + " WHERE co_noticia= ? AND u.id = comp.usuario AND comp.ELIMINADO = 'False' AND u.id <> 'SIA'";
            
	    retVal = castCompartidoVO(
                    em.createNativeQuery(sql)
                    .setParameter(1, idNoticia)
                    .getResultList()
            );
	} catch (Exception e) {
	    LOGGER.error(e);
	    
	}
        
        return retVal;
    }

    private List<ComparteCon> castCompartidoVO(List lista) {
	ComparteCon comparteCon;
	List<ComparteCon> lo = new ArrayList<ComparteCon>();
	for (Iterator it = lista.iterator(); it.hasNext();) {
	    Object[] obj = (Object[]) it.next();
	    comparteCon = new ComparteCon();
	    comparteCon.setFoto((String) obj[1]);
	    comparteCon.setNombre((String) obj[2]);
	    comparteCon.setFechaGenero((Date) obj[3]);
	    comparteCon.setHoraGenero((Date) obj[4]);
	    comparteCon.setCorreoUsuario((String) obj[5]);

	    lo.add(comparteCon);

	}
	return lo;
    }

    
    public boolean addCompartido(int noticiaID, String usuarioID) {
	boolean ret = false;
	try {
	    StringBuilder sb = new StringBuilder();
	    sb.append(" select case when COUNT(ID) > 0 then 1 when COUNT(ID) < 1 then 0 END   ");
	    sb.append(" from CO_COMPARTIDA  ");
	    sb.append(" where CO_NOTICIA = ").append(noticiaID);
	    sb.append(" and USUARIO = '").append(usuarioID).append("' ");
	    sb.append(" AND ELIMINADO = 'False' ");

	    Integer obj = ((Integer) em.createNativeQuery(sb.toString()).getSingleResult());

	    if (obj == 0) {
		ret = true;
	    }

	} catch (Exception e) {
	    LOGGER.fatal(this, e);
	}

	return ret;
    }
}
