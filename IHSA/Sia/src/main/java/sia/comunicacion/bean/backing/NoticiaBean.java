/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.comunicacion.bean.backing;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;



import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputTextarea;

import javax.faces.model.DataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.file.UploadedFile;
import sia.comunicacion.bean.model.NoticiaListModel;
import sia.modelo.CoComentario;
import sia.modelo.CoNoticia;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.NoticiaAdjuntoVO;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
@Named(value = "noticiaBean")
@ViewScoped
public class NoticiaBean implements Serializable {

    //-- Managed Beans ----
    @Inject
    private Sesion sesion;
    @Inject
    private NoticiaListModel noticiaListModel;
    //-- Constantes ---
    private static final String COMPARTIR = "Escribe aquí lo que deseas compartir...";
    private static final String COMENTAR_NOTIFICACION = "Has un comentario sobre esta Notificación...";
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
    private boolean enviarNotificacion = false;
    private int indexPanelTap, indexPanelTap1;
    private int maxNews, maxComents;
    private int idComentarioActivo = 0;
    private int idNoticiaActiva = 0;
    private int idAdjuntoActivo;
    private DataModel comparteCon;

    private DataModel dataModel;
    private CoNoticia noticiaActual;
    private CoComentario comentarioActual;

    @PostConstruct
    public void llenarNoticia() {
        indexPanelTap1 = 0;
        // paginacion
        maxNews = 5;
        maxComents = 2;
        if (sesion.getUsuario() != null) {
            if (!comentar && !isFiltrar()) {
                //traer todosfiltrarNoticia
                noticiaListModel.noticias(this.sesion.getUsuario().getId(), maxNews, 0);
            } else {
                if (comentar) {
                    UtilLog4j.log.info(this, "comentar noticia");
                    noticiaListModel.comentarNoticia(idNoticiaActiva, sesion.getUsuario().getId());
                    UtilLog4j.log.info(this, "Comentar");
                }
                if (isFiltrar()) {
                    UtilLog4j.log.info(this, "filtrar noticia");
                    noticiaListModel.noticias(this.sesion.getUsuario().getId(), 0, idNoticiaActiva);
                }
            }
        }
    }

//    private List<ComentarioVO> comentarioVO=null;
    /**
     * constructor of NoticiaBean
     */
    public NoticiaBean() {
    }

    public void processTabChange(TabChangeEvent tabChangeEvent) {
        if (tabChangeEvent.getTab().getFacetCount() == 0) {
            this.setTextoNoticia(COMPARTIR);
        } else {
            this.setTextoNoticia(COMENTAR_NOTIFICACION);
        }
    }

    public void refrescarTodo() {
        try {
            maxComents = 2;
            comentar = false;
            filtrar = false;
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//
    public void verComentariosDeNoticia() {
        try {
            this.maxComents = tomarMaxComments();
            setComentar(false);
            this.idNoticiaActiva = tomarIdNoticia();
            inputHidden.setValue(this.idNoticiaActiva);
            setFiltrar(true);

            //this.comentarios = null;
//            idNoticiaActiva = tomarIdNoticia();           c
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DataModel getComentarios() {
        DataModel comentarios = null;

        try {
            if (this.inputHidden != null
                    && this.getInputHidden().getValue() != null
                    && idNoticiaActiva != 0) {

                if ((Integer) this.getInputHidden().getValue() == this.idNoticiaActiva && isFiltrar()) {
                    UtilLog4j.log.info(this, "Filtrar " + isFiltrar() + " con " + maxComents);
                    comentarios = this.noticiaListModel.getComentarios((Integer) this.getInputHidden().getValue(), this.maxComents, sesion.getUsuario().getId());
                    //getComentariosFiltrados((Integer) this.getInputHidden().getValue(), sesion.getUsuario().getId());
                } else {
                    UtilLog4j.log.info(this, "no filtro " + (Integer) this.getInputHidden().getValue() + " con " + maxComents);
                    comentarios = this.noticiaListModel.getComentarios((Integer) this.getInputHidden().getValue(), this.maxComents, sesion.getUsuario().getId());
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al traer comentarios " + e.getMessage());
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, e);
        }

        return comentarios;
    }

    public void meGustaNoticia() {
        this.idNoticiaActiva = tomarIdNoticia();
        UtilLog4j.log.info(this, "idNoticia " + idNoticiaActiva);
        this.noticiaListModel.meGustaNoticia(idNoticiaActiva, sesion.getUsuario().getId());
    }

    public void yaNoMeGustaNoticia() {
        this.idNoticiaActiva = tomarIdNoticia();
        int idMeGusta = tomarIdMeGusta();
        UtilLog4j.log.info(this, "idNoticia " + idNoticiaActiva);
        UtilLog4j.log.info(this, "idMeGusta " + idMeGusta);
        this.noticiaListModel.yaNoMeGustaNoticia(idNoticiaActiva, idMeGusta, sesion.getUsuario().getId());
    }

    public void nuevaNoticia() {
        //<p>Escribe aqu&iacute; lo que deseas compartir...</p>
        if (!this.textoNoticia.equals("")) {
            this.noticiaListModel.nuevaNoticia(sesion.getUsuario().getId(), "",
                    this.textoNoticia, "", null, 1, (List<ComparteCon>) this.getComparteCon().getWrappedData());
        } else {
            FacesUtils.addInfoMessage("Por favor escribe algo..");
        }

        this.comparteCon = null;
        this.setTextoNoticia("");
    }

    public void modificarNoticia() {
        if (!this.getNoticiaActual().getMensaje().equals("")) {
            idNoticiaActiva = noticiaActual.getId();
            this.noticiaListModel.modificarNoticia(noticiaActual, sesion.getUsuario().getId());
            setMrPopupModificarNoticia(false);
        } else {
            FacesUtils.addErrorMessage("Por favor escribe la noticia..");
        }
    }

    public void eliminarNoticia() {
        try {
            this.idNoticiaActiva = tomarIdNoticia();
            this.noticiaListModel.eliminarNoticia(idNoticiaActiva, sesion.getUsuario().getId());
            UtilLog4j.log.info(this, "Comentario eliminado");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exception en elimnar cmentario " + e.getMessage());
        }
    }

    public void comentarNoticia() {
        UtilLog4j.log.info(this, "comentar noticia");
        try {
            this.idNoticiaActiva = tomarIdNoticia();
            if (!filtrar) {
                setComentar(true);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exception en comentar noticia " + e.getMessage());
        }
    }

    public void modificarComentario() {
        if (!this.getComentarioActual().getMensaje().equals("")) {
            this.noticiaListModel.modificarComentario(getComentarioActual(), sesion.getUsuario().getId());
            setMrPopupModificarComentario(false);
        } else {
            FacesUtils.addErrorMessage("Por favor escribe el comentario..");
        }
    }

    public void eliminarComentario() {
        try {
            this.idComentarioActivo = tomarIdComentario();
            UtilLog4j.log.info(this, "idComentario " + idComentarioActivo);
            this.noticiaListModel.eliminarComentario(idComentarioActivo, sesion.getUsuario().getId());
            UtilLog4j.log.info(this, "Comentario eliminado");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exception en elimnar cmentario " + e.getMessage());
        }
    }

    public void meGustaComentario() {
        this.idComentarioActivo = tomarIdComentario();
        UtilLog4j.log.info(this, "idComentario " + idComentarioActivo);
        this.noticiaListModel.meGustaComentario(idComentarioActivo, sesion.getUsuario().getId());
    }

    public void yaNoMeGustaComentario() {
        UtilLog4j.log.info(this, "ya no me gusta el comentario...");
        try {
            this.idComentarioActivo = Integer.parseInt(FacesUtils.getRequestParameter("idComentario"));// tomarIdComentario();
            int idMeGusta = Integer.parseInt(FacesUtils.getRequestParameter("idMeGusta"));//tomarIdMeGusta();
            UtilLog4j.log.info(this, "idcomentario" + idComentarioActivo);
            UtilLog4j.log.info(this, "idMeGusta " + idMeGusta);
            this.noticiaListModel.yaNoMeGustaComentario(idComentarioActivo, idMeGusta, sesion.getUsuario().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al dar en ya no me gusta" + e.getMessage());
        }
    }

    /**
     * **************************FILE *******************
     */
    public void subirArchivo(FileUploadEvent fileEntryEvent) throws NamingException {
        UtilLog4j.log.info(this, "subirArchivo");
        UploadedFile fileInfo = fileEntryEvent.getFile();
        boolean v = false;
        try {
            UtilLog4j.log.info(this, "Si guardo el archivo..");
            v = noticiaListModel.guardarArchivo(
                    fileInfo.getFileName(),
                    fileInfo.getContentType(),
                    fileInfo.getSize(), sesion.getUsuario().getId(), this.idNoticiaActiva);
            setMrSubirArchivo(false);
            if (v == false) {
                FacesUtils.addErrorMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al subir archivo" + e.getMessage());
        }
    }

    public void eliminarArchivo() {
        UtilLog4j.log.info(this, "eliminarArchivo");
        NoticiaAdjuntoVO no = (NoticiaAdjuntoVO) dataModel.getRowData();
        Integer idRelacion = no.getIdNoticiaAdjunto();
        idAdjuntoActivo = no.getId();

        if (!noticiaListModel.quitarArchivo(this.idNoticiaActiva, this.idAdjuntoActivo, idRelacion, sesion.getUsuario().getId())) {
            FacesUtils.addErrorMessage("Existió un error al eliminar el arvhivo..");
        } else {
            this.dataModel = noticiaListModel.getAdjuntoNoticia(idNoticiaActiva, sesion.getUsuario().getId());
            FacesUtils.addErrorMessage("Se eliminó correctamente el archivo...");
        }
    }

    public void uploadFile() {
        UtilLog4j.log.info(this, "upload");
    }

    public void traerAdjuntosNoticia() {
        //recargar lista de adjuntos
        idNoticiaActiva = tomarIdNoticia();
        this.dataModel = noticiaListModel.getAdjuntoNoticia(idNoticiaActiva, sesion.getUsuario().getId());
    }

    /**
     * ***************************************************
     */
    private Integer tomarIdComentario() {
        return Integer.parseInt(FacesUtils.getRequestParameter("idComentario"));
    }

    private Integer tomarIdNoticia() {
        return Integer.parseInt(FacesUtils.getRequestParameter("idNoticia"));
    }

    private Integer tomarIdMeGusta() {
        return Integer.parseInt(FacesUtils.getRequestParameter("idMeGusta"));
    }

    private Integer tomarIdArchivo() {
        return Integer.parseInt(FacesUtils.getRequestParameter("idArchivo"));
    }

    private Integer tomarIdCoNoticiaSiAdjunto() {
        return Integer.parseInt(FacesUtils.getRequestParameter("idCoNoticiaSiAdjunto"));
    }

    private Integer tomarMaxComments() {
        return Integer.parseInt(FacesUtils.getRequestParameter("maxComments"));
    }

    public void mostrarPopupModificarComentario() {
        idComentarioActivo = Integer.parseInt(FacesUtils.getRequestParameter("idComentario"));
        this.setComentarioActual(noticiaListModel.buscarComentario(idComentarioActivo));
        this.setMrPopupModificarComentario(true);
    }

    public void ocultarPopupModificarComentario() {
        this.setComentarioActual(null);
        this.setMrPopupModificarComentario(false);
    }

    public void mostrarPopupModificarNoticia() {
        this.idNoticiaActiva = Integer.parseInt(FacesUtils.getRequestParameter("idNoticia"));
        setNoticiaActual(noticiaListModel.buscarNoticia(idNoticiaActiva));
        this.setMrPopupModificarNoticia(true);
    }

    public void ocultarPopupModificarNoticia() {
        this.setNoticiaActual(null);
        this.setMrPopupModificarNoticia(false);
    }

    public void mostrarPopupSubirArchivo() {
        this.idNoticiaActiva = tomarIdNoticia();
        noticiaListModel.setIdNoticia(idNoticiaActiva);
        setDirectorioPath(noticiaListModel.traerDirectorio(idNoticiaActiva));
        UtilLog4j.log.info(this, "idNOticia" + idNoticiaActiva);
        this.setMrSubirArchivo(true);
    }

    public String mostrarPaginaSubirArchivo() {
        UtilLog4j.log.info(this, "mostrarpag");
        this.idNoticiaActiva = tomarIdNoticia();
        noticiaListModel.setIdNoticia(idNoticiaActiva);
        UtilLog4j.log.info(this, "idNOticia" + idNoticiaActiva);
        return "vistas/comunicacion/adjuntarArchivo";
    }

    public void ocultarPopupSubirArchivo() {
        this.setMrSubirArchivo(false);
    }

    public void ocultarPopupVerAdjuntos() {
        this.setMrVerAdjuntos(false);
    }

    public void mostrarPopupVerAdjuntos() {
        traerAdjuntosNoticia();
        this.setMrVerAdjuntos(true);
    }

    public void activarDesactivarCheckBox() {
        this.enviarNotificacion = !this.enviarNotificacion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public Integer getIdNoticiaActiva() {
        return this.idNoticiaActiva;
    }

    /**
     * @param noticiaListModel the noticiaListModel to set
     */
    public void setNoticiaListModel(NoticiaListModel noticiaListModel) {
        this.noticiaListModel = noticiaListModel;
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

    public int getIdComentario() {
        return this.noticiaListModel.getIdComentario();
    }

    public void setIdComentario(int idComentario) {
        this.noticiaListModel.setIdComentario(idComentario);
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

    /**
     * @return the noticias
     */
    public List<NoticiaVO> getListaNoticias() {
        return noticiaListModel.getListaNoticias();
    }

    /**
     * @param noticias the noticias to set
     */
    public void setListaNoticias(List<NoticiaVO> noticias) {
        noticiaListModel.setListaNoticias(noticias);
    }
}
