/*
 * NotaRequisicionBean.java
 * Creado el 21/01/2010, 01:37:54 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputTextarea;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.CoComentario;
import sia.modelo.CoNoticia;
import sia.modelo.Requisicion;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.requisicion.impl.OcRequisicionCoNoticiaImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 21/01/2010
 */
@Named (value = NotaRequisicionBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class NotaRequisicionBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "notaRequisicionBean";
    //------------------------------------------------------
    @Inject
    private NotificacionRequisicionImpl notificacionRequisicionImpl;
    @Inject
    private OcRequisicionCoNoticiaImpl ocRequisicionCoNoticiaImpl;
    @Inject
    private CoNoticiaImpl coNoticiaImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    //-- Managed Beans ----
    
    @Inject
    private UsuarioBean usuarioBean;
    
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    
    // - - - - - - - - - -
    //----------------------
    protected static final String UPDATE_OPERATION = "Actualizar";
    protected static final String CREATE_OPERATION = "Crear";
    
    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    // Atributos
    private HtmlInputHidden inputHidden = new HtmlInputHidden(); // este componente me ayuda a tomar el valor del comunicado que esta iterando
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
    private boolean enviarNotificacion = true;
    private int indexPanelTap, indexPanelTap1;
    private int maxComents;
    private int idComentarioActivo = 0;
    private int idNoticiaActiva = 0;
    private int idAdjuntoActivo;
    private DataModel comparteCon;
    private DataModel likes;
    private DataModel dataModel;
    private CoNoticia noticiaActual;
    private CoComentario comentarioActual;

    @Getter
    @Setter
    private UploadedFile fileInfo;
    /**
     * Creates a new instance of NotaRequisicionBean
     */
    public NotaRequisicionBean() {        
    }
    
    @PostConstruct
    public void iniciar(){
     filtrar = true;   
    }

    /**
     * @return the operacion
     */
    public DataModel traerNoticiaPorRequisicion(int idReq) {
	return new ListDataModel(ocRequisicionCoNoticiaImpl.traerNoticiaPorRequisicion(idReq, true));
    }

    public DataModel traerNoticiaPorUsuario(String idUsuario) {
	return new ListDataModel(ocRequisicionCoNoticiaImpl.traerNoticiaPorUsuario(usuarioBean.getUsuarioConectado().getId(), true, usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public DataModel comentarNoticia(Integer idNoticia, String idUsuario) {
	try {
	    List<NoticiaVO> lista = coNoticiaImpl.getNoticias(idUsuario, 0, 0);
	    for (int it = 0; it < lista.size(); it++) {
		if (lista.get(it).getId().equals(idNoticia)) {
		    lista.get(it).setComentar(true);
		}
	    }
	    return new ListDataModel(lista);
	} catch (RuntimeException ex) {
	    UtilLog4j.log.fatal(this, "Noticia comentar : :  : " + ex.getMessage(), ex);
	    return null;
	}
    }

    public DataModel traerComentario(int idNoticia) {
	return new ListDataModel(coNoticiaImpl.getComentariosFiltrados(idNoticia, usuarioBean.getUsuarioConectado().getId()));
    }

    /**
     * ***************
     */
    public void nuevaNoticia() {
	//<p>Escribe aqu&iacute; lo que deseas compartir...</p>
	if (!this.textoNoticia.equals("")) {
	    coNoticiaImpl.nuevaNoticia(usuarioBean.getUsuarioConectado().getId(), "",
		    this.textoNoticia, "", null, 1, (List<ComparteCon>) this.getComparteCon().getWrappedData());
	} else {
	    FacesUtilsBean.addInfoMessage("Por favor escribe algo..");
	}

	this.comparteCon = null;
	this.setTextoNoticia("");
    }

    public void modificarNoticia() {
	if (!this.getNoticiaActual().getMensajeAutomatico().equals("")) {
	    setIdNoticiaActiva(noticiaActual.getId());
	    coNoticiaImpl.editNoticia(noticiaActual, usuarioBean.getUsuarioConectado().getId());
	    setMrPopupModificarNoticia(false);
	} else {
	    FacesUtilsBean.addErrorMessage("Por favor escribe la noticia..");
	}
    }

    public void eliminarNoticia(int idN) {
	try {
	    this.setIdNoticiaActiva(idN);
	    coNoticiaImpl.eliminarNoticia(idNoticiaActiva, usuarioBean.getUsuarioConectado().getId());
	    UtilLog4j.log.fatal(this, "Noticia eliminado");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Exception en elimnar cmentario " + e.getMessage());
	}
    }

    public void comentarNoticia(int idN) {
	UtilLog4j.log.fatal(this, "comentar noticia");
	try {
	    this.setIdNoticiaActiva(idN);
	    setComentar(true);
	    //this.noticias = null; // refrescar la lista

	    UtilLog4j.log.info(this, "Comentar");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Exception en comentar noticia " + e.getMessage(), e);
	}
    }

    public void agregarComentario(int idNoticia, int campo) {
	if (!this.getComentario().getValue().toString().equals("")) {
	    //tomar id de la noticia actual para buscarla..
	    coNoticiaImpl.nuevoComentario(
                    idNoticia, usuarioBean.getUsuarioConectado().getId(), 
                    this.getComentario().getValue().toString(), 
                    enviarNotificacion, 
                    false, 
                    campo, 
                    Constantes.MODULO_REQUISICION
            );
	    //--------
	    this.comentario.setValue("");
	    if (isFiltrar()) {
		maxComents = maxComents + 1;
	    }
	    this.setComentar(false);
	}
    }

    //focus
//    public void refresh() {
//        FacesContext context = FacesContext.getCurrentInstance();
//        Application application = context.getApplication();
//        ViewHandler viewHandler = application.getViewHandler();
//        UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());
//        context.setViewRoot(viewRoot);
//        context.renderResponse();
//    }
    public void modificarComentario() {
	if (!this.getComentarioActual().getMensaje().equals("")) {
	    coNoticiaImpl.editComentario(getComentarioActual(), usuarioBean.getUsuarioConectado().getId());
	    setMrPopupModificarComentario(false);
	} else {
	    FacesUtilsBean.addErrorMessage("Por favor escribe el comentario..");
	}
    }

    public void eliminarComentario(int idCom) {
	try {
	    this.idComentarioActivo = idCom;
	    UtilLog4j.log.info(this, "idComentario " + idComentarioActivo);
	    coNoticiaImpl.eliminarComentario(idComentarioActivo, usuarioBean.getUsuarioConectado().getId());
	    UtilLog4j.log.info(this, "Comentario eliminado");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Exception en elimnar cmentario " + e.getMessage(), e);
	}
    }

    public void meGustaComentario(int idCom) {
	this.idComentarioActivo = idCom;
	UtilLog4j.log.info(this, "idComentario " + idComentarioActivo);
	coNoticiaImpl.meGustaComentario(idComentarioActivo, usuarioBean.getUsuarioConectado().getId());
    }

    public void yaNoMeGustaComentario(int idCom, int idMG) {
	UtilLog4j.log.info(this, "ya no me gusta el comentario...");
	try {
	    this.idComentarioActivo = idCom;
	    int idMeGusta = idMG;
	    UtilLog4j.log.info(this, "idcomentario" + idComentarioActivo);
	    UtilLog4j.log.info(this, "idMeGusta " + idMeGusta);
	    coNoticiaImpl.yaNoMeGustaComentario(idComentarioActivo, idMeGusta, usuarioBean.getUsuarioConectado().getId());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al dar en ya no me gusta" + e.getMessage(), e);
	}
    }

    /**
     * **************************FILE *******************
     */
    public void subirArchivo(FileUploadEvent event) throws NamingException {
        UtilLog4j.log.info(this, "subirArchivo");
        boolean v = false;

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        try {
            fileInfo = event.getFile();

                boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

                if (addArchivo) {
                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    documentoAnexo.setTipoMime(fileInfo.getContentType());
                    documentoAnexo.setRuta(getDirectorioPath());
                    almacenDocumentos.guardarDocumento(documentoAnexo);

                    v = coNoticiaImpl.addArchivo(
                            documentoAnexo.getNombreBase(),
                            documentoAnexo.getTipoMime(),
                            documentoAnexo.getTamanio(),
                            idNoticiaActiva,
                            usuarioBean.getUsuarioConectado().getId()
                    );
                    setMrSubirArchivo(false);
                } else {
                    FacesUtilsBean.addInfoMessage(new StringBuilder()
                            .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                            .append(validadorNombreArchivo.getCaracteresNoValidos())
                            .toString());
                }

                fileInfo.delete();
//		if (fileInfo.getStatus().isSuccess() || fileInfo.isSaved()) {
//		    UtilLog4j.log.info(this, "Si guardo el archivo..");
//		    v = coNoticiaImpl.addArchivo(fileInfo.getFileName(),
//			    fileInfo.getContentType(),
//			    fileInfo.getSize(), idNoticiaActiva, usuarioBean.getUsuarioConectado().getId());
//		    setMrSubirArchivo(false);
//		} else {
//		    UtilLog4j.log.info(this, "No paso ");
//		}
            if (v == false) {
                FacesUtilsBean.addErrorMessage("Ocurrio una excepción, favor de comunicar a soportesia@ihsa.mx");
            }

        } catch (IOException e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addErrorMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        } catch (SIAException e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addErrorMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    public void eliminarArchivo(int idAr, int idNotAdj, int idN) {
	idAdjuntoActivo = idAr;
	int idRelacion = idNotAdj;
	idNoticiaActiva = idN;
	//

	if (!quitarArchivo(this.idNoticiaActiva, this.idAdjuntoActivo, idRelacion, usuarioBean.getUsuarioConectado().getId())) {
	    FacesUtilsBean.addErrorMessage("Existió un error al eliminar el arvhivo..");
	} else {
	    this.dataModel = new ListDataModel(coNoticiaImpl.getAdjuntosNoticia(idNoticiaActiva, usuarioBean.getUsuarioConectado().getId()));
	    FacesUtilsBean.addErrorMessage("Se eliminó correctamente el archivo...");
	}
    }

    public boolean quitarArchivo(Integer idNoticia, Integer idArchivo, Integer idRelacion, String idUsuario) {
	String path = this.siParametroImpl.find(1).getUploadDirectory();
	SiAdjunto adjunto = siAdjuntoImpl.find(idArchivo);
	try {
	    File file = new File(path + adjunto.getUrl());
	    UtilLog4j.log.info(this, "path :" + path);
	    if (file.delete()) {
		UtilLog4j.log.info(this, "Entro a eliminar");
		coNoticiaImpl.deleteArchivo(adjunto, idRelacion, idUsuario);
		UtilLog4j.log.info(this, "Elimino el adjunto de la noticia");
	    }
	    UtilLog4j.log.info(this, "entrando a eliminar el archivo fisico");
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
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en quitar archivo :" + e.getMessage());
	    return false;
	}
    }

    public void uploadFile() {
	UtilLog4j.log.info(this, "upload");
    }

    public void traerAdjuntosNoticia(int idN) {
	//recargar lista de adjuntos
	setIdNoticiaActiva(idN);
	this.dataModel = new ListDataModel(coNoticiaImpl.getAdjuntosNoticia(idNoticiaActiva, usuarioBean.getUsuarioConectado().getId()));
    }

    /**
     * ***************************************************
     */
    
    public void mostrarPopupModificarComentario(int idCom) {
	idComentarioActivo = idCom;
	this.setComentarioActual(coNoticiaImpl.buscarComentario(idComentarioActivo));
	this.setMrPopupModificarComentario(true);
    }

    public void ocultarPopupModificarComentario() {
	this.setComentarioActual(null);
	this.setMrPopupModificarComentario(false);
    }

    public void mostrarPopupModificarNoticia(int idN) {
	this.setIdNoticiaActiva(idN);
	setNoticiaActual(coNoticiaImpl.find(idNoticiaActiva));
	this.setMrPopupModificarNoticia(true);
    }

    public void ocultarPopupModificarNoticia() {
	this.setNoticiaActual(null);
	this.setMrPopupModificarNoticia(false);
    }

    public void ocultarPopupUsuariosCompartidos() {
	this.setMrVerCompartidos(false);
	setLikes(null);
    }

    public void mostrarPopupSubirArchivo(int idN) {
	this.setIdNoticiaActiva(idN);
	setDirectorioPath(traerDirectorio(idNoticiaActiva));
	UtilLog4j.log.info(this, "idNOticia" + idNoticiaActiva);
	this.setMrSubirArchivo(true);
    }


    private String traerDirectorio(int idNoticia) {
	return "Comunicacion/Noticia/" + idNoticia;
    }

    public String mostrarPaginaSubirArchivo(int idN) {
	UtilLog4j.log.info(this, "mostrarpag");
	this.setIdNoticiaActiva(idN);
	UtilLog4j.log.info(this, "idNOticia" + idNoticiaActiva);
	return "vistas/comunicacion/adjuntarArchivo";
    }

    public void ocultarPopupSubirArchivo() {
	this.setMrSubirArchivo(false);
    }

    public void ocultarPopupVerAdjuntos() {
	this.setMrVerAdjuntos(false);
    }

    public void mostrarPopupVerAdjuntos(int idN) {
	traerAdjuntosNoticia(idN);
	this.setMrVerAdjuntos(true);
    }

    public void activarDesactivarCheckBox() {
	this.enviarNotificacion = !this.enviarNotificacion;
    }

    public Integer getIdNoticiaActiva() {
	return this.idNoticiaActiva;
    }

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
	    UtilLog4j.log.info(this, "Error en comparte con: " + e.getMessage());
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
//            directorioPath = noticiaListModel.getDirectorio();
//            UtilLog4j.log.info(this, "directori path"+directorioPath);
//            return directorioPath;
//        } else {
//            UtilLog4j.log.info(this, "directori path"+directorioPath);
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

//    /**
//     * @return the comentarioVO
//     */
//    public List<ComentarioVO> getComentarioVO() {
//        return comentarioVO;
//    }
//
//    /**
//     * @param comentarioVO the comentarioVO to set
//     */
//    public void setComentarioVO(List<ComentarioVO> comentarioVO) {
//        this.comentarioVO = comentarioVO;
//    }
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

    public long getTotalNotasPorInvitado() {
	try {
	    return ocRequisicionCoNoticiaImpl.totalNoticiaPorUsuario(this.usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getApCampo().getId());
	} catch (RuntimeException ex) {
	    UtilLog4j.log.fatal(this, ex.getMessage());
	}
	return 0;
    }

    public void completarCreacionNota(String autor, Requisicion requisicion) {
	try {
	    // -- enviar la notificación - - -
	    StringBuilder asunto = new StringBuilder();
	    asunto.append("Nota de la Requisición: ").append(requisicion.getConsecutivo()).append(' ');

	    if (notificacionRequisicionImpl.envioNotaRequisicion(
		    castUsuarioInvitados(requisicion).toString(),
		    "", "",
		    new StringBuilder().append("Nota de la Requisición: ").append(requisicion.getConsecutivo()).toString(), requisicion,
		    autor, textoNoticia,
		    "solicito")) {
		//
		//Noticias nueavas
		CoNoticia coNoticia = coNoticiaImpl.nuevaNoticia(usuarioBean.getUsuarioConectado().getId(), asunto.toString(), "", textoNoticia, 0, 0, castUsuarioComparteCon(requisicion));
		//Guarda la nota
		ocRequisicionCoNoticiaImpl.guardarNoticia(usuarioBean.getUsuarioConectado().getId(), coNoticia, requisicion);
	    }
	    FacesUtilsBean.addInfoMessage("Se creó correctamente La Nota");
	    PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoNotaReq);");

	} catch (RuntimeException ex) {
	    UtilLog4j.log.fatal(this, ex.getMessage());
	}
    }

    private String castUsuarioInvitados(Requisicion requisicion) {
	try {
	    //        hacer una lista de invitados enviar la notificacion y si se envio correctamente guardarlos a todos
	    StringBuilder invitados = new StringBuilder();
	    switch (requisicion.getEstatus().getId()) {
	    case Constantes.REQUISICION_SOLICITADA:
		invitados.append(requisicion.getSolicita().getEmail());
		invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
		break;
	    case Constantes.REQUISICION_REVISADA:
		//solicita
		invitados.append(requisicion.getSolicita().getEmail());
		invitados.append(",").append(requisicion.getRevisa().getEmail());
		invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
		break;
	    default:
		invitados.append(requisicion.getSolicita().getEmail());
		invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
		break;
	    }
	    return invitados.toString();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al castear los usuario  " + e.getMessage());
	    return null;
	}
    }

    private List<ComparteCon> castUsuarioComparteCon(Requisicion requisicion) {
	try {
	    List<ComparteCon> listaCompartidos = new ArrayList<ComparteCon>();
	    //        hacer una lista de invitados enviar la notificacion y si se envio correctamente guardarlos a todos
	    switch (requisicion.getEstatus().getId()) {
	    case Constantes.REQUISICION_SOLICITADA:
		//solicita
		listaCompartidos.add(castComparteCon(requisicion.getSolicita()));
		listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
		break;
	    case Constantes.REQUISICION_REVISADA:
		//solicita
		listaCompartidos.add(castComparteCon(requisicion.getSolicita()));
		listaCompartidos.add(castComparteCon(requisicion.getRevisa()));
		listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
		break;
	    default:
		listaCompartidos.add(castComparteCon(requisicion.getSolicita()));
		listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
	    }
	    return listaCompartidos;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al castear los usuario  " + e.getMessage());
	    return null;
	}
    }

    private ComparteCon castComparteCon(Usuario usuario) {
	ComparteCon compateCon = new ComparteCon();
	compateCon.setId(usuario.getId());
	compateCon.setNombre(usuario.getNombre());
	compateCon.setCorreoUsuario(usuario.getEmail());
	compateCon.setTipo("Usuario");
	return compateCon;
    }

    /**
     * @param idNoticiaActiva the idNoticiaActiva to set
     */
    public void setIdNoticiaActiva(int idNoticiaActiva) {
	this.idNoticiaActiva = idNoticiaActiva;
    }
}
