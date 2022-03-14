/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.comunicacion.bean.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
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
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
//@SessionScoped
@ManagedBean(name = "noticiaListModel")
@CustomScoped(value = "#{window}")
public class NoticiaListModel implements Serializable {
    //-- Atributos ---
    //-- Managed Beans ----

    @EJB
    private CoNoticiaImpl servicioNoticia;
    @EJB
    private SiParametroImpl siParametroImpl;
    @EJB
    private SiAdjuntoImpl servicioSiAdjunto;
    //
    private List<NoticiaVO> listaNoticia;
    private int idComentario = 0;
    private Integer idNoticia;
    private String directorio;

    /**
     * Creates a new instance of NoticiaListModel
     */
    public NoticiaListModel() {
    }

    /**
     * ********** NOTICIAS ****************
     * @param idUsuario
     * @param maxResult
     * @param onlyIdNoticia
     */
    public void noticias(String idUsuario, int maxResult, Integer onlyIdNoticia) {
//        NoticiaVO noticiaVo = null;
//        List<NoticiaVO> lo = new ArrayList<NoticiaVO>();
	try {
	    listaNoticia = new ArrayList<>();
	    listaNoticia = servicioNoticia.getNoticias(idUsuario, maxResult, onlyIdNoticia);
	} catch (Exception ex) {
	    Logger.getLogger(NoticiaListModel.class.getName()).log(Level.SEVERE, null, ex);
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
	this.servicioNoticia.nuevaNoticia(idUsuario, titulo, msj, msjAutomatico, idElemento, opcionSistema, comparteCon);
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
	return servicioNoticia.addArchivo(fileName, contentType, size, idNoticia, idUsuario);
    }

    //public String TraerDirectorio(Integer idNoticia) {
    public String traerDirectorio(Integer idNoticia) {
//            this.setDirectorioPath(siParametroImpl.find(1).getUploadDirectory() + "SGyL/Vehiculo/ComprobanteMantenimiento" + "/" + getSgMantenimiento().getId() + "/");
	return siParametroImpl.find(1).getUploadDirectory() + "Comunicacion/Noticia/" + idNoticia + "/";
    }

    public boolean quitarArchivo(Integer idNoticia, Integer idArchivo, Integer idRelacion, String idUsuario) {
	String path = this.siParametroImpl.find(1).getUploadDirectory();
	SiAdjunto adjunto = servicioSiAdjunto.find(idArchivo);
	try {
	    File file = new File(path + adjunto.getUrl());
	    if (file.delete()) {
		servicioNoticia.deleteArchivo(adjunto, idRelacion, idUsuario);
	    }
	    String dir = "Comunicacion/Noticia" + "/" + idNoticia + "/";
	    File sessionfileUploadDirectory = new File(path + dir);

	    if (sessionfileUploadDirectory.isDirectory()) {
		try {
		    sessionfileUploadDirectory.delete();
		} catch (SecurityException e) {
		    UtilLog4j.log.info(this, e.getMessage());
		}
	    }
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en quitar archivo :" + e.getMessage());
	    return false;
	}
    }

    public DataModel getAdjuntoNoticia(Integer idNoticia, String idUsuario) {
	try {
	    List<NoticiaAdjuntoVO> lis = servicioNoticia.getAdjuntosNoticia(idNoticia, idUsuario);
	    UtilLog4j.log.info(this, "Lista de ADJUNTOS " + lis.size());
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

    public void modificarComentario(CoComentario comentario, String idUsuario) {
	this.servicioNoticia.editComentario(comentario, idUsuario);
    }

    public CoComentario buscarComentario(Integer idComentario) {
	return this.servicioNoticia.buscarComentario(idComentario);
    }

    public void eliminarComentario(Integer idComentario, String idUsuario) {
	this.servicioNoticia.eliminarComentario(idComentario, idUsuario);
    }

    //public DataModel getComentarios(Integer idNoticia, int maxResult, String idUsuario) {
    public DataModel getComentarios(Integer idNoticia, int maxResult, String idUsuario) {
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
	List<ComentarioVO> lista;
	try {
	    return this.servicioNoticia.getComentarios(idNoticia, maxResult, idUsuario);

	} catch (RuntimeException ex) {
	    Logger.getLogger(NoticiaListModel.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }

    public DataModel getComentariosFiltrados(Integer idNoticia, String idUsuario) {
	UtilLog4j.log.info(this, "geComentariosFiltrados para " + idNoticia);
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
     * @return the noticias
     */
    public List<NoticiaVO> getListaNoticias() {
	return listaNoticia;
    }

    /**
     * @param noticias the noticias to set
     */
    public void setListaNoticias(List<NoticiaVO> noticias) {
	this.listaNoticia = noticias;
    }
    /**
     * ***************************************************
     */
}
