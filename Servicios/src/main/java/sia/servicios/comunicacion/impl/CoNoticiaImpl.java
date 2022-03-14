/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.comunicacion.impl;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.CoComentario;
import sia.modelo.CoGrupo;
import sia.modelo.CoMeGusta;
import sia.modelo.CoMiembro;
import sia.modelo.CoNoticia;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.ComentarioVO;
import sia.modelo.comunicacion.vo.MeGustaVO;
import sia.modelo.comunicacion.vo.NoticiaAdjuntoVO;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@LocalBean 
public class CoNoticiaImpl extends AbstractFacade<CoNoticia> {
    
    private static final UtilLog4j LOGGER = UtilLog4j.log;
    
    @Inject
    private CoComentarioImpl servicioCoComentario;
    @Inject
    private CoCompartidaImpl servicioCoCompartida;
    @Inject
    private CoPrivacidadImpl servicioCoprivacidad;
    @Inject
    private CoGrupoImpl servicioCoGrupo;    
    @Inject
    private CoMeGustaImpl servicioCoMeGusta;
    @Inject
    private CoNoticiaMeGustaImpl servicioCoNoticiaMeGusta;
    @Inject
    private SiAdjuntoImpl siAdjuntoRemote;
    @Inject
    private CoNoticiaSiAdjuntoImpl servicioCoNoticiaSiAdjunto;
    @Inject
    UsuarioImpl usuarioRemote;
    
    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
	return em;
    }

    public CoNoticiaImpl() {
	super(CoNoticia.class);
    }

    
    public CoNoticia nuevaNoticia(String idUsuario, String titulo, String msj,
	    String msjAutomatico, Integer idElemento, Integer opcionSistema, List<ComparteCon> comparteCon) {

	boolean usuariosSia = false, tusGrupos = false;
	Set<Usuario> listaUsuarios = new HashSet<Usuario>();
	CoNoticia noticia = new CoNoticia();
	noticia.setGenero(new Usuario(idUsuario));
	listaUsuarios.add(noticia.getGenero());
//        noticia.setSiOpcion(this.servicioSiOpcion.find(opcionSistema));
//        noticia.setIdElemento(idElemento);
	noticia.setTitulo(titulo);
	LOGGER.info(this, "mensaje a guardar " + msj);
	noticia.setMensaje(msj);
	noticia.setMensajeAutomatico(msjAutomatico);
	noticia.setFechaGenero(new Date());
	noticia.setHoraGenero(new Date());
	noticia.setComentarios(0);
	noticia.setMegusta(0);
	noticia.setEliminado(false);

	// primero buscar si se compartio para todos los usuarios del sia
	for (ComparteCon elemento : comparteCon) {
	    if ((elemento.getTipo().equals("privacidad")) && (elemento.getId().equals("2"))) {
		noticia.setCoPrivacidad(this.servicioCoprivacidad.find(2)); // usuarios del sia
		try {
		    this.create(noticia);
		} catch (Exception e) {
		    LOGGER.fatal(this, e.getMessage());
		}
		if (this.servicioCoCompartida.addCompartido(noticia.getId(), idUsuario)
			&& this.servicioCoCompartida.compartir(noticia, new Usuario(idUsuario))) {
		    //-- enviar correo si se marco la casilla de notificar la publicación
		    usuariosSia = true;
		    break;
		}
	    }
	}

	if (!usuariosSia) {
	    //si no es para todos los usuarios del SIA buscar si se compartio con tus grupos
	    for (ComparteCon elemento : comparteCon) {
		if ((elemento.getTipo().equals("privacidad")) && (elemento.getId().equals("j"))) {
		    noticia.setCoPrivacidad(this.servicioCoprivacidad.find(4)); // tus grupos
		    //obtener los grupos de quien genero la noticia
		    tusGrupos = true;
		    for (CoGrupo grupo : this.servicioCoGrupo.getGrupos(noticia.getGenero().getId())) {
			// obtener los usuarios de los grupos de quien genero la noticia
			for (CoMiembro coMiembro : this.servicioCoGrupo.getMiembros(grupo.getId())) {
			    listaUsuarios.add(coMiembro.getMiembro());
			}
		    }
		}
		if (elemento.getTipo().equals("Usuario")) {
		    // si es un usuario ver si no esta en tus grupos para create el registro
		    boolean enGrupo = false;
		    for (CoGrupo grupo : this.servicioCoGrupo.getGrupos(noticia.getGenero().getId())) {
			// obtener los usuarios de cada grupo
			for (CoMiembro coMiembro : this.servicioCoGrupo.getMiembros(grupo.getId())) {
			    if (coMiembro.getMiembro().getId().equals(elemento.getId())) {
				enGrupo = true;
				break;
			    }
			}
		    }
		    if (!enGrupo) {
			if (elemento.getId() != null) {
			    listaUsuarios.add(new Usuario(elemento.getId()));
			}
		    }
		}
	    }

	    if (!tusGrupos) {
		noticia.setCoPrivacidad(this.servicioCoprivacidad.find(5)); // personalizado
		// si no es para usuarios SIA ni para tus grupos recorrer los grupos especificados para agregar los registros
		for (ComparteCon elemento : comparteCon) {
		    if (elemento.getTipo().equals("Grupo")) {
			// obtener los usuarios del grupo
			for (CoMiembro coMiembro : this.servicioCoGrupo.getMiembros(Integer.getInteger(elemento.getId()))) {
			    listaUsuarios.add(coMiembro.getMiembro());
			}
		    }
		}
	    }
	    try {
		//-- create Noticia
		create(noticia);
	    } catch (Exception ex) {
		LOGGER.error(ex);
	    }
	    //FIXME : falta la implementación?
	    if (this.servicioCoCompartida.compartir(noticia, listaUsuarios)) {
		//-- enviar correo si se marco la casilla de notificar la publicación

	    }
	}
	return noticia;
    }

    
    public void compartirNoticia(Integer idNoticia, String idUsuario, String idUsuarioRealizo) {
	try {
	    if (servicioCoCompartida.addCompartido(idNoticia, idUsuario)) {
		servicioCoCompartida.compartir(
                        find(idNoticia), 
                        new Usuario(idUsuario), 
                        new Usuario(idUsuarioRealizo)
                );
	    }
	    LOGGER.info("****  SE COMPARTIO LA NOCITICIA CON ++++ " + idUsuario);
	} catch (Exception e) {
	    LOGGER.fatal(this, "Excepción al compartir la noticia : : :: : " + e.getMessage(), e);
	}
    }

    
    public void editNoticia(CoNoticia noticia, String idUsuario) {
	try {
	    noticia.setModifico(new Usuario(idUsuario));
	    noticia.setFechaModifico(new Date());
	    noticia.setHoraModifico(new Date());
	    edit(noticia);
	} catch (Exception e) {
	    LOGGER.fatal(this, e.getMessage());
	}
    }

    
    public void eliminarNoticia(Integer idNoticia, String idUsuario) {
	try {
	    //enviar al log
	    CoNoticia noticiaActual = find(idNoticia);
	    noticiaActual.setModifico(new Usuario(idUsuario));
	    noticiaActual.setFechaModifico(new Date());
	    noticiaActual.setHoraModifico(new Date());
	    noticiaActual.setEliminado(Constantes.BOOLEAN_TRUE);
	    edit(noticiaActual);
	} catch (Exception e) {
	    LOGGER.fatal(this, e.getMessage());
	}
    }

    
    public void meGustaNoticia(Integer idNoticia, String idUsuario) {
	try {
	    CoMeGusta meGusta = this.servicioCoMeGusta.meGusta(idUsuario);
	    if (meGusta != null) {
		CoNoticia noti = find(idNoticia);
		noti.setMegusta(noti.getMegusta() + 1);
		this.edit(noti);
		//guardar en relacion
		this.servicioCoNoticiaMeGusta.crearMeGusta(noti, meGusta, idUsuario);
		//Enviar correo
		LOGGER.info(this, "ENVIANDO CORREO $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
	    }
	} catch (Exception e) {
	    LOGGER.fatal(this, e.getMessage());
	}
    }

    
    public void yaNoMeGustaNoticia(Integer idNoticia, Integer idMeGusta, String idUsuario) {
	CoMeGusta meGusta = servicioCoMeGusta.yaNoMeGusta(idMeGusta, idUsuario);
	if (meGusta != null) {
	    CoNoticia noti = find(idNoticia);
	    noti.setMegusta(noti.getMegusta() - 1);
	    edit(noti);
	    //eliminar en relacion
	    servicioCoNoticiaMeGusta.eliminarMeGusta(idNoticia, idMeGusta, idUsuario);
	}
    }

    
    public List<MeGustaVO> getPersonasNoticiaLike(Integer idNoticia, String idUsuario) {
	return servicioCoNoticiaMeGusta.getLikes(idNoticia, idUsuario, 0);
    }

    
    public List<ComparteCon> getCompartidosPorNoticia(Integer idNoticia) {
	return servicioCoCompartida.getListaUsuarioCompartidos(idNoticia);
    }

//    
//    public List<CoMegusta> getMeGustaNoticia(Integer idNoticia) {
//        return this.servicioCoMegusta.getPorElemento(idNoticia);
//    }
    
    public List<NoticiaVO> getNoticias(String idUsuario, int maxResult, Integer onlyIdNoticia) {
	return servicioCoCompartida.getNoticias(idUsuario, maxResult, onlyIdNoticia);
    }

    
    public CoComentario buscarComentario(Integer idComentario) {
	return servicioCoComentario.find(idComentario);
    }

    
    public void nuevoComentario(Integer idNoticia, String idUsuario, String comentario, 
            boolean notificar, boolean isRecomedacionSeguridad, int campo, int modulo) {
        
	CoNoticia noticia = this.find(idNoticia);
        //
        boolean valid = 
                servicioCoComentario.nuevoComentario(
                        noticia, 
                        usuarioRemote.find(idUsuario), 
                        comentario, 
                        notificar, 
                        isRecomedacionSeguridad, 
                        campo, 
                        modulo
                );
        
        if (valid) {
	    // edit contador de comentarios
	    noticia.setComentarios(noticia.getComentarios() + 1);
	    this.edit(noticia);
	}
    }

    
    public void editComentario(CoComentario comentario, String idUsuario) {
	try {
	    this.servicioCoComentario.modificarComentario(comentario, idUsuario);
	} catch (Exception e) {
	    LOGGER.fatal(this, e.getMessage());
	}
    }

    
    public void eliminarComentario(Integer idComentario, String idUsuario) {
	
	try {
	    if (this.servicioCoComentario.eliminarComentario(idComentario, idUsuario)) {
		CoNoticia n = servicioCoComentario.find(idComentario).getCoNoticia();
		n.setComentarios(n.getComentarios() - 1);
		this.edit(n);
	    }
	} catch (Exception e) {
	    LOGGER.fatal(this, "Excepcion en eliminar comentario " + e.getMessage());
	}
    }

//    
//    public void meGustaNoticia(CoComentario comentario, Usuario usuario) {
//        this.servicioCoComentario.meGustaNoticia(comentario, usuario);
//    }
//   
//    public List<CoMegusta> getMeGustaComentario(Integer idComentario) {
//        return this.servicioCoComentario.getMeGusta(idComentario);
//    }
//    
//    public List<CoComentario> getComentarios(Integer idNoticia, int maxResult) {
//        return this.servicioCoComentario.getComentarios(idNoticia, maxResult);
//    }
    
    public List<ComentarioVO> getComentarios(int idNoticia, int maxResult, String idUsuario) {
	return servicioCoComentario.getComentarios(idNoticia, maxResult, idUsuario);
    }

    
    public List<ComentarioVO> getComentariosFiltrados(int idNoticia, String idUsuario) {
	return servicioCoComentario.getComentariosFiltrados(idNoticia, idUsuario);
    }

    
    public void meGustaComentario(Integer idComentario, String usuario) {
	//guardar objeto me gusta
	LOGGER.info(this, "#########idComentario " + idComentario);
	try {
	    servicioCoComentario.meGusta(idComentario, usuario);
	} catch (Exception e) {
	    LOGGER.fatal(this, "Excepcion al dar me gusta" + e.getMessage());
	}
    }

    
    public void yaNoMeGustaComentario(Integer idComentario, Integer idMeGusta, String idUsuario) {
	try {
	    servicioCoComentario.yaNoMeGusta(idComentario, idMeGusta, idUsuario);
	} catch (Exception e) {
	    LOGGER.fatal(this, "Excepcion al dar me gusta" + e.getMessage());
	}
    }

    
    public List<MeGustaVO> getPersonasComentariosLike(Integer idComentario, String idUsuario) {
	return servicioCoComentario.getLikes(idComentario, idUsuario, 0);//todos los commets
    }

    
    public boolean addArchivo(String fileName, String contentType, long size, Integer idNoticia, String idUsuario) {
	return addArchivo(
		fileName,
		"Comunicacion/Noticia/" + idNoticia + File.separator + fileName,
		contentType,
		size,
		idNoticia,
		idUsuario);

    }

    
    public boolean addArchivo(String fileName, String path, String contentType, long size, Integer idNoticia, String idUsuario) {
        boolean v = false;
        try {
            
            SiAdjunto siAdjunto = 
                    siAdjuntoRemote.guardarArchivoDevolverArchivo(
                            idUsuario, 
                            1, 
                            path + File.separator + fileName, 
                            fileName, 
                            contentType, 
                            size, 
                            9, 
                            "COMUNICACION"
                    );
            
            LOGGER.info(this, "Aqui después de guardar el archivo");
            
            if (siAdjunto == null) {
                siAdjuntoRemote.eliminarArchivo(siAdjunto, idUsuario, Constantes.BOOLEAN_TRUE);
            } else {
                v = true;
                servicioCoNoticiaSiAdjunto.addArchivoAdjunto(
                        find(idNoticia), 
                        new Usuario(idUsuario), 
                        siAdjunto
                );
            }
            
        } catch (Exception e) {
            LOGGER.fatal(this, "excepcion " + e.getMessage());
        }
        
        return v;
    }

    
    public boolean deleteArchivo(SiAdjunto adjunto, Integer idRelacion, String idUsuario) {
        boolean retVal = false;
	try {
	    servicioCoNoticiaSiAdjunto.deleteArchivoAdjunto(idRelacion, new Usuario(idUsuario));
	    siAdjuntoRemote.eliminarArchivo(adjunto, idUsuario, Constantes.BOOLEAN_FALSE);
	    retVal =  true;
	} catch (Exception e) {
	    LOGGER.fatal(this, "Exception al eliminar el archivo", e);
	}
        
        return retVal;
    }

    
    public List<NoticiaAdjuntoVO> getAdjuntosNoticia(Integer idNoticia, String idUsuario) {
	return servicioCoNoticiaSiAdjunto.getAdjuntosNoticia(idNoticia, idUsuario);
    }

    
    public NoticiaVO traerNoticia(int idNoticia) {
        NoticiaVO retVal = null;
	
	try {
	    String qy = " SELECT noti.ID, "
		    + " noti.COMENTARIOS,"
		    + " noti.FECHA_GENERO,"
		    + " noti.HORA_GENERO,"
		    + " noti.MEGUSTA,"
		    + " noti.MENSAJE,"
		    + " noti.MENSAJE_AUTOMATICO, "
		    + " noti.TITULO,"
		    + " u.id as usuarioId,"
		    + " u.FOTO AS usuarioFoto,"
		    + " u.NOMBRE as usuarioNombre,"
		    + " (select count(ADJ.ID) as ADJUNTOS From CO_NOTICIA_SI_ADJUNTO ADJ where ADJ.CO_NOTICIA = NOTI.ID AND ADJ.ELIMINADO='" + Constantes.BOOLEAN_FALSE + "') "
		    + " FROM CO_NOTICIA noti"
		    + "	    inner join usuario u on noti.GENERO = u.ID "
		    + " WHERE noti.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'"
		    + " AND noti.ID =  " + idNoticia
		    + " AND noti.ELIMINADO = '" + Constantes.BOOLEAN_FALSE + "'";

	    retVal = castNoticia((Object[]) em.createNativeQuery(qy).getSingleResult());
	} catch (Exception e) {
	    LOGGER.error(e);
	}
        
        return retVal;
    }

    private NoticiaVO castNoticia(Object[] obj) {
	NoticiaVO noticiaVo = new NoticiaVO();
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
	noticiaVo.setAdjuntosCount(((Long) obj[11]).intValue());
	noticiaVo.setComentar(false);
	noticiaVo.setListaComentario(servicioCoComentario.traerComentariosPorNoticia(noticiaVo.getId()));
	return noticiaVo;
    }

}
