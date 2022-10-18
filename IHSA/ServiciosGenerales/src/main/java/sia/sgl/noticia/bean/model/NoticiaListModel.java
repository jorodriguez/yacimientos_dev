/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.noticia.bean.model;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import sia.constantes.Constantes;
import sia.modelo.CoComentario;
import sia.modelo.CoNoticia;
import sia.modelo.SiAdjunto;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.ComentarioVO;
import sia.modelo.comunicacion.vo.NoticiaAdjuntoVO;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sgl.noticia.bean.backing.NoticiaBean;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Named(value = "noticiaListModel")

public class NoticiaListModel implements Serializable {
    //-- Atributos ---

    @Inject
    private CoNoticiaImpl servicioNoticia;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SiAdjuntoImpl servicioSiAdjunto;
    private int idComentario = 0;
    private Integer idNoticia;
    private String directorio;
    private CoNoticia noticiaActual;
    private CoComentario comentarioActual;
    private HtmlInputHidden inputHidden; // este componente me ayuda a tomar el valor del comunicado que esta iterando
    private HtmlInputTextarea comentario; // este componente me sirve para agarrar el comentario escrito.
    private String textoNoticia;
    private String privasidad;
    private String directorioPath;
    private boolean publicar, paginar, inicio = false;
    private boolean mrPopupModificarComentario = false;
    private boolean mrPopupLikePerson = false;
    private boolean mrPopupModificarNoticia = false;
    private boolean mrSubirArchivo = false;
    private boolean mrVerAdjuntos = false;
    private boolean mrVerCompartidos = false;
    private boolean filtrar = false;
    private boolean comentar = false;
    private boolean enviarNotificacion = false;
    private boolean privilegioSubirArchivo = true; //Controlo
    private int indexPanelTap, indexPanelTap1;
    private int maxNews = 5, maxComents = 2;
    private int idComentarioActivo;
    private int idNoticiaActiva;
    private int idAdjuntoActivo;
    private DataModel comparteCon;
    private DataModel noticiasModel;
    private DataModel comentariosModel;
    private DataModel likes;
    private DataModel dataModel;
    private List<ComentarioVO> comentarioVO = null;

    /**
     * Creates a new instance of NoticiaListModel
     */
    public NoticiaListModel() {
    }

    public void beginConversationBitacoraViaje() {
//        this.conversationsManager.finalizeAllConversations();
	//       this.conversationsManager.beginConversation(this.conversation, "BitacoraViaje");
    }

    /**
     * ********** NOTICIAS ****************
     */
    public DataModel getNoticias(String idUsuario, int maxResult, Integer onlyIdNoticia) {
//        UtilLog4j.log.fatal(this, "getNOticias "+idUsuario+" resul "+maxResult+" idnoticia"+onlyIdNoticia);
	NoticiaVO noticiaVo = null;
	try {
	    return new ListDataModel(servicioNoticia.getNoticias(idUsuario, maxResult, onlyIdNoticia));

	} catch (RuntimeException ex) {
	    Logger.getLogger(NoticiaListModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    public DataModel comentarNoticia(Integer idNoticia, String idUsuario) {
	try {
	    List<NoticiaVO> lista = servicioNoticia.getNoticias(idUsuario, 0, 0);
	    for (int it = 0; it < lista.size(); it++) {
		if (lista.get(it).getId().equals(idNoticia)) {
		    lista.get(it).setComentar(true);
		}
	    }
	    return new ListDataModel(lista);
	} catch (RuntimeException ex) {
	    Logger.getLogger(NoticiaListModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    public void meGustaNoticia(int idNoticia, String idUsuario) {
	try {
	    this.servicioNoticia.meGustaNoticia(idNoticia, idUsuario);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Exepcion " + e.getMessage());
	}
    }

    public void yaNoMeGustaNoticia(Integer idNoticia, Integer idMegusta, String idUsuario) {
	try {
	    this.servicioNoticia.yaNoMeGustaNoticia(idNoticia, idMegusta, idUsuario);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Exepcion " + e.getMessage());
	}
    }

    public void nuevaNoticia(String idUsuario, String titulo, String msj,
	    String msjAutomatico, Integer idElemento, Integer opcionSistema, List<ComparteCon> comparteCon) {
	servicioNoticia.nuevaNoticia(idUsuario, titulo, msj, msjAutomatico, idElemento, opcionSistema, comparteCon);
    }

    public void modificarNoticia(CoNoticia noticia, String idUsuario) {
	this.servicioNoticia.editNoticia(noticia, idUsuario);
    }

    public void eliminarNoticia(Integer idNoticia, String idUsuario) {
	this.servicioNoticia.eliminarNoticia(idNoticia, idUsuario);
    }

    public CoNoticia buscarNoticia(Integer idNoticia) {
	return this.servicioNoticia.find(idNoticia);
    }

    public DataModel getLikesForNoticia(Integer idNoticia, String idUsuario) {
	return new ListDataModel(servicioNoticia.getPersonasNoticiaLike(idNoticia, idUsuario));
    }

    public DataModel getCompartidosPorNoticia(Integer idNoticia) {
	return new ListDataModel(servicioNoticia.getCompartidosPorNoticia(idNoticia));
    }

    /**
     * **** ARCHIVO - NOTICIA **********>
     *
     */
    public boolean guardarArchivo(String fileName, String contentType, long size, String idUsuario, Integer idNoticia) {
////        UtilLog4j.log.fatal(this, "guardararchivo Model");
	return servicioNoticia.addArchivo(fileName, contentType, size, idNoticia, idUsuario);
    }

    //public String TraerDirectorio(Integer idNoticia) {
    public String traerDirectorio(Integer idNoticia) {
	return "Comunicacion/Noticia/" + idNoticia + "/";
    }

    public boolean quitarArchivo(Integer idNoticia, Integer idArchivo, Integer idRelacion, String idUsuario) {
	String path = this.siParametroImpl.find(1).getUploadDirectory();
	SiAdjunto adjunto = servicioSiAdjunto.find(idArchivo);
	boolean retVal = false;

	try {
	    Files.delete(Paths.get(path + adjunto.getUrl()));

	    servicioNoticia.deleteArchivo(adjunto, idRelacion, idUsuario);

	    String dir = "Comunicacion/Noticia" + "/" + idNoticia + "/";
	    UtilLog4j.log.info(this, "Ruta carpeta: " + dir);
	    File sessionfileUploadDirectory = new File(path + dir);
	    if (sessionfileUploadDirectory.isDirectory()) {
		try {
		    sessionfileUploadDirectory.delete();
		} catch (SecurityException e) {
		    UtilLog4j.log.fatal(this, e.getMessage());
		}
	    }
	    retVal = true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en quitar archivo :" + e.getMessage());
	}

	return retVal;
    }

    public DataModel getAdjuntoNoticia(Integer idNoticia, String idUsuario) {
	try {
	    List<NoticiaAdjuntoVO> lis = servicioNoticia.getAdjuntosNoticia(idNoticia, idUsuario);
//            UtilLog4j.log.fatal(this, "Lista de ADJUNTOS " + lis.size());
	    return new ListDataModel(lis);
	} catch (RuntimeException ex) {
	    Logger.getLogger(NoticiaListModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    /**
     * ********** COMENTARIOS ****************
     */
    public void meGustaComentario(int idComentario, String idUsuario) {
	try {
	    this.servicioNoticia.meGustaComentario(idComentario, idUsuario);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Exepcion " + e.getMessage());
	}
    }

    public void yaNoMeGustaComentario(Integer idComentario, Integer idMegusta, String idUsuario) {
	try {
	    this.servicioNoticia.yaNoMeGustaComentario(idComentario, idMegusta, idUsuario);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Exepcion " + e.getMessage());
	}
    }

    public void nuevoComentario(int idNoticia, String idUsuario, String comentario) {
	this.servicioNoticia.nuevoComentario(idNoticia, idUsuario, comentario, isEnviarNotificacion(), false, Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_SGYL);//solo para seguridad es true
    }

    public void modificarComentario(CoComentario comentario, String idUsuario) {
	this.servicioNoticia.editComentario(comentario, idUsuario);
    }

    public CoComentario buscarComentario(Integer idComentario) {
//        UtilLog4j.log.fatal(this, "buscar comentario "+idComentario);
	return this.servicioNoticia.buscarComentario(idComentario);
    }

    public void eliminarComentario(Integer idComentario, String idUsuario) {
//        UtilLog4j.log.info(this, "Eliminar el cometar " + idComentario);
//        UtilLog4j.log.info(this, "Usuario " + idUsuario);
	this.servicioNoticia.eliminarComentario(idComentario, idUsuario);

    }

    //public DataModel getComentarios(Integer idNoticia, int maxResult, String idUsuario) {
    public DataModel getComentarios(Integer idNoticia, int maxResult, String idUsuario) {
//        UtilLog4j.log.info(this, "geComentarios "+idNoticia);
	List<ComentarioVO> lista;
	try {
	    lista = this.servicioNoticia.getComentarios(idNoticia, maxResult, idUsuario);
	    return new ListDataModel(lista);
	} catch (RuntimeException ex) {
	    Logger.getLogger(NoticiaListModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    public List<ComentarioVO> getComentario(Integer idNoticia, int maxResult, String idUsuario) {
//        UtilLog4j.log.fatal(this, "geComentarios");
	List<ComentarioVO> lista;
	try {
	    return this.servicioNoticia.getComentarios(idNoticia, maxResult, idUsuario);

	} catch (RuntimeException ex) {
	    Logger.getLogger(NoticiaListModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    public DataModel getComentariosFiltrados(Integer idNoticia, String idUsuario) {
//        UtilLog4j.log.fatal(this, "geComentariosFiltrados para " + idNoticia);
	try {
	    List<ComentarioVO> lista = this.servicioNoticia.getComentariosFiltrados(idNoticia, idUsuario);
	    return new ListDataModel(lista);
	} catch (RuntimeException ex) {
	    Logger.getLogger(NoticiaListModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    public DataModel getLikesForComentario(Integer idComentario, String idUsuario) {
	return new ListDataModel(servicioNoticia.getPersonasComentariosLike(idComentario, idUsuario));
    }

    /**
     * @return the idComentario
     */
    public int getIdComentario() {
	return idComentario;
    }

    /**
     * @param idComentario the idComentario to set
     */
    public void setIdComentario(int idComentario) {
	this.idComentario = idComentario;
    }

    /**
     * @return the idNoticia
     */
    public Integer getIdNoticia() {
	return idNoticia;
    }

    /**
     * @param idNoticia the idNoticia to set
     */
    public void setIdNoticia(Integer idNoticia) {
	this.idNoticia = idNoticia;
    }

    /**
     * @return the noticiaActual
     */
    public CoNoticia getNoticiaActual() {
	return noticiaActual;
    }

    /**
     * @param noticiaActual the noticiaActual to set
     */
    public void setNoticiaActual(CoNoticia noticiaActual) {
	this.noticiaActual = noticiaActual;
    }

    /**
     * @return the comentarioActual
     */
    public CoComentario getComentarioActual() {
	return comentarioActual;
    }

    /**
     * @param comentarioActual the comentarioActual to set
     */
    public void setComentarioActual(CoComentario comentarioActual) {
	this.comentarioActual = comentarioActual;
    }

    /**
     * ***************************************************
     */
    /**
     * @param inputHidden the inputHidden to set
     */
    public void setInputHidden(HtmlInputHidden inputHidden) {
	this.inputHidden = inputHidden;
    }

    /**
     * @return the comentario
     */
    public HtmlInputTextarea getComentario() {
	return comentario;
    }

    /**
     * @param comentario the comentario to set
     */
    public void setComentario(HtmlInputTextarea comentario) {
	this.comentario = comentario;
    }

    /**
     * @return the textoNoticia
     */
    public String getTextoNoticia() {
	return textoNoticia;
    }

    /**
     * @param textoNoticia the textoNoticia to set
     */
    public void setTextoNoticia(String textoNoticia) {
	this.textoNoticia = textoNoticia;
    }

    /**
     * @return the privasidad
     */
    public String getPrivasidad() {
	return privasidad;
    }

    /**
     * @param privasidad the privasidad to set
     */
    public void setPrivasidad(String privasidad) {
	this.privasidad = privasidad;
    }

    /**
     * @return the publicar
     */
    public boolean isPublicar() {
	return publicar;
    }

    /**
     * @param publicar the publicar to set
     */
    public void setPublicar(boolean publicar) {
	this.publicar = publicar;
    }

    /**
     * @return the paginar
     */
    public boolean isPaginar() {
	return paginar;
    }

    /**
     * @param paginar the paginar to set
     */
    public void setPaginar(boolean paginar) {
	this.paginar = paginar;
    }

    /**
     * @return the indexPanelTap
     */
    public int getIndexPanelTap() {
	return indexPanelTap;
    }

    /**
     * @param indexPanelTap the indexPanelTap to set
     */
    public void setIndexPanelTap(int indexPanelTap) {
	this.indexPanelTap = indexPanelTap;
    }

    /**
     * @return the indexPanelTap1
     */
    public int getIndexPanelTap1() {
	return indexPanelTap1;
    }

    /**
     * @param indexPanelTap1 the indexPanelTap1 to set
     */
    public void setIndexPanelTap1(int indexPanelTap1) {
	this.indexPanelTap1 = indexPanelTap1;
    }

    /**
     * @return the inputHidden
     */
    public HtmlInputHidden getInputHidden() {
	return inputHidden;
    }

    /**
     * @return the comparteCon
     */
    public DataModel getComparteCon() {
	try {
	    return comparteCon;
	} catch (Exception e) {
	    Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, e);
	    return comparteCon;
	}
    }

    /**
     * @param comparteCon the comparteCon to set
     */
    public void setComparteCon(DataModel comparteCon) {
	this.comparteCon = comparteCon;
    }

    /**
     * @return the mrPopupModificar
     */
    public boolean isMrPopupModificarComentario() {
	return mrPopupModificarComentario;
    }

    /**
     * @param mrPopupModificar the mrPopupModificar to set
     */
    public void setMrPopupModificarComentario(boolean mrPopupModificar) {
	this.mrPopupModificarComentario = mrPopupModificar;
    }

    /**
     * @return the mrPopupModificarNoticia
     */
    public boolean isMrPopupModificarNoticia() {
	return mrPopupModificarNoticia;
    }

    /**
     * @param mrPopupModificarNoticia the mrPopupModificarNoticia to set
     */
    public void setMrPopupModificarNoticia(boolean mrPopupModificarNoticia) {
	this.mrPopupModificarNoticia = mrPopupModificarNoticia;
    }

    /**
     * @return the mrPopupLikePerson
     */
    public boolean isMrPopupLikePerson() {
	return mrPopupLikePerson;
    }

    /**
     * @param mrPopupLikePerson the mrPopupLikePerson to set
     */
    public void setMrPopupLikePerson(boolean mrPopupLikePerson) {
	this.mrPopupLikePerson = mrPopupLikePerson;
    }

    /**
     * @return the likes
     */
    public DataModel getLikes() {
	return likes;
    }

    /**
     * @param likes the likes to set
     */
    public void setLikes(DataModel likes) {
	this.likes = likes;
    }

    public String getDirectorioPath() {
	return directorioPath;

    }
    /*
     * @return the directorioPath
     */
//    public String getDirectorioPath() {
//        if (directorioPath == null) {
//            directorioPath = getDirectorio();
//            UtilLog4j.log.fatal(this, "directori path"+directorioPath);
//            return directorioPath;
//        } else {
//            UtilLog4j.log.fatal(this, "directori path"+directorioPath);
//            return directorioPath;
//        }
//    }

    /**
     * @param directorioPath the directorioPath to set
     */
    public void setDirectorioPath(String directorioPath) {
	this.directorioPath = directorioPath;
    }

    /**
     * @return the mrSubirArchivo
     */
    public boolean isMrSubirArchivo() {
	return mrSubirArchivo;
    }

    /**
     * @param mrSubirArchivo the mrSubirArchivo to set
     */
    public void setMrSubirArchivo(boolean mrSubirArchivo) {
	this.mrSubirArchivo = mrSubirArchivo;
    }

    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
	return dataModel;
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
	this.dataModel = dataModel;
    }

    /**
     * @return the mrVerAdjuntos
     */
    public boolean isMrVerAdjuntos() {
	return mrVerAdjuntos;
    }

    /**
     * @param mrVerAdjuntos the mrVerAdjuntos to set
     */
    public void setMrVerAdjuntos(boolean mrVerAdjuntos) {
	this.mrVerAdjuntos = mrVerAdjuntos;
    }

    /**
     * @return the comentar
     */
    public boolean isComentar() {
	return comentar;
    }

    /**
     * @param comentar the comentar to set
     */
    public void setComentar(boolean comentar) {
	this.comentar = comentar;
    }

    /**
     * @return the filtrar
     */
    public boolean isFiltrar() {
	return filtrar;
    }

    /**
     * @param filtrar the filtrar to set
     */
    public void setFiltrar(boolean filtrar) {
	this.filtrar = filtrar;
    }

    /**
     * @return the comentarioVO
     */
    public List<ComentarioVO> getComentarioVO() {
	return comentarioVO;
    }

    /**
     * @param comentarioVO the comentarioVO to set
     */
    public void setComentarioVO(List<ComentarioVO> comentarioVO) {
	this.comentarioVO = comentarioVO;
    }

    /**
     * @return the idNoticiaActiva
     */
    public int getIdNoticiaActiva() {
	return idNoticiaActiva;
    }

    /**
     * @param idNoticiaActiva the idNoticiaActiva to set
     */
    public void setIdNoticiaActiva(int idNoticiaActiva) {
	this.idNoticiaActiva = idNoticiaActiva;
    }

    /**
     * @return the noticiasModel
     */
    public DataModel getNoticiasModel() {
	return noticiasModel;
    }

    /**
     * @param noticiasModel the noticiasModel to set
     */
    public void setNoticiasModel(DataModel noticiasModel) {
	this.noticiasModel = noticiasModel;
    }

    /**
     * @return the comentariosModel
     */
    public DataModel getComentariosModel() {
	return comentariosModel;
    }

    /**
     * @param comentariosModel the comentariosModel to set
     */
    public void setComentariosModel(DataModel comentariosModel) {
	this.comentariosModel = comentariosModel;
    }

    /**
     * @return the idComentarioActivo
     */
    public int getIdComentarioActivo() {
	return idComentarioActivo;
    }

    /**
     * @param idComentarioActivo the idComentarioActivo to set
     */
    public void setIdComentarioActivo(int idComentarioActivo) {
	this.idComentarioActivo = idComentarioActivo;
    }

    /**
     * @return the idAdjuntoActivo
     */
    public int getIdAdjuntoActivo() {
	return idAdjuntoActivo;
    }

    /**
     * @param idAdjuntoActivo the idAdjuntoActivo to set
     */
    public void setIdAdjuntoActivo(int idAdjuntoActivo) {
	this.idAdjuntoActivo = idAdjuntoActivo;
    }

    /**
     * @return the maxComents
     */
    public int getMaxComents() {
	return maxComents;
    }

    /**
     * @param maxComents the maxComents to set
     */
    public void setMaxComents(int maxComents) {
	this.maxComents = maxComents;
    }

    /**
     * @return the maxNews
     */
    public int getMaxNews() {
	return maxNews;
    }

    /**
     * @param maxNews the maxNews to set
     */
    public void setMaxNews(int maxNews) {
	this.maxNews = maxNews;
    }

    /**
     * @return the inicio
     */
    public boolean isInicio() {
	return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(boolean inicio) {
	this.inicio = inicio;
    }

    /**
     * @return the privilegioSubirArchivo
     */
    public boolean isPrivilegioSubirArchivo() {
	return privilegioSubirArchivo;
    }

    /**
     * @param privilegioSubirArchivo the privilegioSubirArchivo to set
     */
    public void setPrivilegioSubirArchivo(boolean privilegioSubirArchivo) {
	this.privilegioSubirArchivo = privilegioSubirArchivo;
    }

    /**
     * @return the enviarNotificacion
     */
    public boolean isEnviarNotificacion() {
	return enviarNotificacion;
    }

    /**
     * @param enviarNotificacion the enviarNotificacion to set
     */
    public void setEnviarNotificacion(boolean enviarNotificacion) {
	this.enviarNotificacion = enviarNotificacion;
    }

    /**
     * @return the mrVerCompartidos
     */
    public boolean isMrVerCompartidos() {
	return mrVerCompartidos;
    }

    /**
     * @param mrVerCompartidos the mrVerCompartidos to set
     */
    public void setMrVerCompartidos(boolean mrVerCompartidos) {
	this.mrVerCompartidos = mrVerCompartidos;
    }
}
