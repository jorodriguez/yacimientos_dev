/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.noticia.bean.backing;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.modelo.CoComentario;
import sia.modelo.CoNoticia;
import sia.sgl.noticia.bean.model.NoticiaListModel;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.NoticiaAdjuntoVO;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
/*
 * @Named(value = "noticiaBean")
@RequestScoped
 */
@Named(value = "noticiaBean")
@RequestScoped
public class NoticiaBean implements Serializable {

    @Inject
    private Sesion sesion;
    @ManagedProperty(value = "#{noticiaListModel}")
    private NoticiaListModel noticiaListModel;
    @Getter
    @Setter
    private UploadedFile fileInfo;
    /*
     * @Inject private NoticiaListModel noticiaListModel;
     */
    //-- Constantes ---
    private static final String COMPARTIR = "Escribe aquí lo que deseas compartir...";
    private static final String COMENTAR_NOTIFICACION = "Has un comentario sobre esta Notificación...";
    // Atributos
    private HtmlInputHidden inputHidden; // este componente me ayuda a tomar el valor del comunicado que esta iterando
    //private HtmlInputTextarea comentario; // este componente me sirve para agarrar el comentario escrito.
    private String textoNoticia;
    private String privasidad;

    /**
     * constructor of NoticiaBean
     */
    public NoticiaBean() {
    }

    public String goToBitacora() {
        //this.noticiaListModel.beginConversationBitacoraViaje();
        return "/vistas/sgl/viaje/bitacoraViaje";
    }

    public String goToPrincial() {
//        this.noticiaListModel.beginConversationBitacoraViaje();
        return "/principal";
    }

//    public void processTabChange(TabChangeEvent tabChangeEvent) {
//        if (tabChangeEvent.getNewTabIndex() == 0) {
//            this.setTextoNoticia(COMPARTIR);
//        } else {
//            this.setTextoNoticia(COMENTAR_NOTIFICACION);
//        }
//    }

    /**
     * @return the noticias
     */
    public DataModel getNoticias() {
        try {
            if (noticiaListModel.getNoticiasModel() == null && this.sesion.getUsuario() != null) {
                if (!noticiaListModel.isComentar() && !noticiaListModel.isFiltrar()) {
                    //traer todos
//                    UtilLog4j.log.info(this, "no filtrar noticia.");
                    noticiaListModel.setNoticiasModel(this.noticiaListModel.getNoticias(this.sesion.getUsuario().getId(), noticiaListModel.getMaxNews(), 0));
                } else {
                    if (noticiaListModel.isComentar()) {
//                        UtilLog4j.log.info(this, "comentar noticia");
                        noticiaListModel.setNoticiasModel(this.noticiaListModel.comentarNoticia(noticiaListModel.getIdNoticiaActiva(), sesion.getUsuario().getId()));
//                        UtilLog4j.log.info(this, "Comentar");
                    }
                    if (noticiaListModel.isFiltrar()) {
//                        UtilLog4j.log.info(this, "filtrar noticia");
                        noticiaListModel.setNoticiasModel(this.noticiaListModel.getNoticias(this.sesion.getUsuario().getId(), 0, noticiaListModel.getIdNoticiaActiva()));
//                        UtilLog4j.log.info(this, "noticia count" + noticiaListModel.getNoticiasModel().getRowCount());
                    }
                }
                return noticiaListModel.getNoticiasModel();
            } else {
                return noticiaListModel.getNoticiasModel();
            }
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
            return noticiaListModel.getNoticiasModel();
        }
    }

    public void setNoticias(DataModel noticias) {
        noticiaListModel.setNoticiasModel(noticias);
    }

    /**
     * *********
     *
     * Buscar viaje y traer noticia
     *
     */
    public void buscarNoticiaViaje(ActionEvent event) {
        try {
            noticiaListModel.setMaxComents(tomarMaxComments());
            noticiaListModel.setComentar(false);
            noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
            getInputHidden().setValue(noticiaListModel.getIdNoticiaActiva());
            noticiaListModel.setFiltrar(true);
            noticiaListModel.setNoticiasModel(null);
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * ******************************************
     */
//   
    public void verComentariosDeNoticia(ActionEvent event) {
        try {
            noticiaListModel.setMaxComents(tomarMaxComments());
            noticiaListModel.setComentar(false);
            noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
            getInputHidden().setValue(noticiaListModel.getIdNoticiaActiva());
            noticiaListModel.setFiltrar(true);
            noticiaListModel.setNoticiasModel(null);
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //forma externa 
    public void filtrarNoticia(Integer idNoticia, boolean mostrarOpcionInicio, boolean privilegioSubirArchivo) {
////        UtilLog4j.log.info(this, "filtrarNoticia " + idNoticia);
        try {
            CoNoticia noti = noticiaListModel.buscarNoticia(idNoticia);
            noticiaListModel.setMaxComents(noti.getComentarios());
            noticiaListModel.setComentar(false);
            noticiaListModel.setIdNoticiaActiva(noti.getId());
            setInputHidden(new HtmlInputHidden());
            getInputHidden().setValue(noti.getId());
            noticiaListModel.setFiltrar(true);
            noticiaListModel.setNoticiasModel(null);
            noticiaListModel.setInicio(mostrarOpcionInicio);
            noticiaListModel.setPaginar(true);//<<<<<<Controla mostrar la bitacora o no
            noticiaListModel.setPrivilegioSubirArchivo(privilegioSubirArchivo);
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Ver todos las noticias
     */
    public void refrescarTodo(ActionEvent event) {
        try {
            noticiaListModel.setMaxComents(2);
            noticiaListModel.setComentar(false);
            noticiaListModel.setFiltrar(false);
            noticiaListModel.setNoticiasModel(null);
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DataModel getComentarios() {
        try {
            if (this.getInputHidden() != null) {
                if (this.getInputHidden().getValue() != null
                        && (Integer) this.getInputHidden().getValue() == noticiaListModel.getIdNoticiaActiva()
                        && noticiaListModel.isFiltrar()) {
                    noticiaListModel.setComentariosModel(
                            this.noticiaListModel.getComentarios((Integer) this.getInputHidden().getValue(),
                                    noticiaListModel.getMaxComents(),
                                    sesion.getUsuario().getId()));
                } else {
                    noticiaListModel.setComentariosModel(
                            this.noticiaListModel.getComentarios((Integer) this.getInputHidden().getValue(),
                                    noticiaListModel.getMaxComents(),
                                    sesion.getUsuario().getId()));
                }
            }
            //return noticiaListModel.getComentariosModel();
        } catch (Exception e) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, e);
        }

        return noticiaListModel.getComentariosModel();
    }

    public void meGustaNoticia(ActionEvent actionEvent) {
        noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
//        UtilLog4j.log.info(this, "idNoticia " + noticiaListModel.getIdNoticiaActiva());
        this.noticiaListModel.meGustaNoticia(noticiaListModel.getIdNoticiaActiva(), sesion.getUsuario().getId());
        noticiaListModel.setNoticiasModel(null);
    }

    public void yaNoMeGustaNoticia(ActionEvent actionEvent) {
        noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
        int idMeGusta = tomarIdMeGusta();
//        UtilLog4j.log.info(this, "idNoticia " + noticiaListModel.getIdNoticiaActiva());
//        UtilLog4j.log.info(this, "idMeGusta " + idMeGusta);
        this.noticiaListModel.yaNoMeGustaNoticia(noticiaListModel.getIdNoticiaActiva(), idMeGusta, sesion.getUsuario().getId());
        noticiaListModel.setNoticiasModel(null);
    }

    public void nuevaNoticia(ActionEvent actionEvent) {
        //<p>Escribe aqu&iacute; lo que deseas compartir...</p>
        if (!this.textoNoticia.equals("")) {
            this.noticiaListModel.nuevaNoticia(sesion.getUsuario().getId(), "",
                    this.textoNoticia, "", null, 1, (List<ComparteCon>) noticiaListModel.getComparteCon().getWrappedData());
        } else {
            FacesUtils.addInfoMessage("Por favor escribe algo..");
        }

        noticiaListModel.setComparteCon(null);
        this.setTextoNoticia("");
        noticiaListModel.setNoticiasModel(null);
    }

    public void modificarNoticia(ActionEvent actionEvent) {
//        UtilLog4j.log.info(this, "modificarNoticia");
        if (!noticiaListModel.getNoticiaActual().getMensaje().equals("")) {
//            UtilLog4j.log.info(this, "noticiaListModel.getNoticiaActual().getMensaje()" + noticiaListModel.getNoticiaActual().getMensaje());
            noticiaListModel.setIdNoticiaActiva((int) noticiaListModel.getNoticiaActual().getId());
            this.noticiaListModel.modificarNoticia(noticiaListModel.getNoticiaActual(), sesion.getUsuario().getId());
            noticiaListModel.setMrPopupModificarNoticia(false);
            noticiaListModel.setNoticiasModel(null);
        } else {
            FacesUtils.addErrorMessage("Por favor escribe la noticia..");
        }
    }

    public void eliminarNoticia(ActionEvent actionEvent) {
        try {
            noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
            this.noticiaListModel.eliminarNoticia(noticiaListModel.getIdNoticiaActiva(), sesion.getUsuario().getId());
            noticiaListModel.setNoticiasModel(null);
//            UtilLog4j.log.info(this, "Comentario eliminado");
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exception en elimnar cmentario " + e.getMessage());
        }
    }

    public void comentarNoticia(ActionEvent actionEvent) {
//        UtilLog4j.log.info(this, "comentar noticia");
        try {
            noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
            if (!noticiaListModel.isFiltrar()) {
                noticiaListModel.setComentar(true);
                noticiaListModel.setNoticiasModel(null);
            }
//            UtilLog4j.log.info(this, "Comentar");
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exception en comentar noticia " + e.getMessage());
        }
    }

    public void getPeronasLikeNoticia() {
        try {
            noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
            noticiaListModel.setLikes(noticiaListModel.getLikesForNoticia(noticiaListModel.getIdNoticiaActiva(), sesion.getUsuario().getId()));
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //metodo nuevo
    public void getCompartidos() {
        try {
            noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
            noticiaListModel.setLikes(noticiaListModel.getCompartidosPorNoticia(noticiaListModel.getIdNoticiaActiva()));
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exception en comentar noticia " + e.getMessage());
        }
    }

    /**
     * **** COMENTARIOS ********
     */
    public void getPeronasLikeComentario() {
        try {
            noticiaListModel.setLikes(noticiaListModel.getLikesForComentario(tomarIdComentario(), sesion.getUsuario().getId()));
        } catch (Exception ex) {
            Logger.getLogger(NoticiaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nuevoComentario(ActionEvent actionEvent) {
//        UtilLog4j.log.info(this, "nuevo comentario");
        if (!this.getComentario().getValue().toString().equals("")) {
            UtilLog4j.log.info(this, "Cometario" + this.getComentario().getValue().toString());
            //tomar id de la noticia actual para buscarla..
            noticiaListModel.setNoticiaActual(noticiaListModel.buscarNoticia(((NoticiaVO) noticiaListModel.getNoticiasModel().getRowData()).getId()));
            this.noticiaListModel.nuevoComentario(noticiaListModel.getNoticiaActual().getId(), sesion.getUsuario().getId(), this.getComentario().getValue().toString());
            //--------
            noticiaListModel.getComentario().setValue("");
            noticiaListModel.setMaxComents(noticiaListModel.getMaxComents() + 1);
            noticiaListModel.setNoticiasModel(null);
            noticiaListModel.setComentar(false);
        }
    }

    public void modificarComentario(ActionEvent actionEvent) {
//        UtilLog4j.log.info(this, "modificarComentario");
        if (!noticiaListModel.getComentarioActual().getMensaje().equals("")) {
            UtilLog4j.log.info(this, "noticiaListModel.getComentarioActual() " + noticiaListModel.getComentarioActual().getMensaje());
            this.noticiaListModel.modificarComentario(noticiaListModel.getComentarioActual(), sesion.getUsuario().getId());
            noticiaListModel.setMrPopupModificarComentario(false);
        } else {
            FacesUtils.addErrorMessage("Por favor escribe el comentario..");
        }
    }

    public void eliminarComentario(ActionEvent actionEvent) {
        try {
            noticiaListModel.setIdComentarioActivo(tomarIdComentario());
            this.noticiaListModel.eliminarComentario(noticiaListModel.getIdComentarioActivo(), sesion.getUsuario().getId());
//            UtilLog4j.log.info(this, "Comentario eliminado");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exception en elimnar cmentario " + e.getMessage());
        }
    }

    public void meGustaComentario(ActionEvent actionEvent) {
        noticiaListModel.setIdComentarioActivo(tomarIdComentario());
////        UtilLog4j.log.info(this, "idComentario " + noticiaListModel.getIdComentarioActivo());
        this.noticiaListModel.meGustaComentario(noticiaListModel.getIdComentarioActivo(), sesion.getUsuario().getId());
    }

    public void yaNoMeGustaComentario(ActionEvent actionEvent) {
//        UtilLog4j.log.info(this, "ya no me gusta el comentario...");
        try {
            noticiaListModel.setIdComentarioActivo(Integer.parseInt(FacesUtils.getRequestParameter("idComentario")));// tomarIdComentario();
            int idMeGusta = Integer.parseInt(FacesUtils.getRequestParameter("idMeGusta"));//tomarIdMeGusta();
//            UtilLog4j.log.info(this, "idcomentario" + noticiaListModel.getIdComentarioActivo());
//            UtilLog4j.log.info(this, "idMeGusta " + idMeGusta);
            this.noticiaListModel.yaNoMeGustaComentario(noticiaListModel.getIdComentarioActivo(), idMeGusta, sesion.getUsuario().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al dar en ya no me gusta" + e.getMessage());
        }
    }

    public void subirArchivo(FileUploadEvent fileEvent) throws Exception {
        boolean valid = false;
        fileInfo = fileEvent.getFile();
        try {

            valid = noticiaListModel.guardarArchivo(
                    fileInfo.getFileName(),
                    fileInfo.getContentType(),
                    fileInfo.getSize(), sesion.getUsuario().getId(),
                    noticiaListModel.getIdNoticiaActiva()
            );
            noticiaListModel.setMrSubirArchivo(false);
            noticiaListModel.setNoticiasModel(null);
            if (valid == false) {
                FacesUtils.addErrorMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al subir archivo" + e.getMessage());
        }
    }

    public void eliminarArchivo(ActionEvent event) {
        UtilLog4j.log.info(this, "eliminarArchivo");
        NoticiaAdjuntoVO no = (NoticiaAdjuntoVO) noticiaListModel.getDataModel().getRowData();
        Integer idRelacion = no.getIdNoticiaAdjunto();
        noticiaListModel.setIdAdjuntoActivo(no.getId());

        if (noticiaListModel.quitarArchivo(noticiaListModel.getIdNoticiaActiva(), noticiaListModel.getIdAdjuntoActivo(), idRelacion, sesion.getUsuario().getId())) {
            noticiaListModel.setNoticiasModel(null);
            noticiaListModel.setDataModel(noticiaListModel.getAdjuntoNoticia(noticiaListModel.getIdNoticiaActiva(), sesion.getUsuario().getId()));
            FacesUtils.addErrorMessage("Se eliminó correctamente el archivo...");
        } else {
            FacesUtils.addErrorMessage("Existió un error al eliminar el arvhivo..");
        }
    }

    public void uploadFile() {
        UtilLog4j.log.info(this, "upload");
    }

    public void traerAdjuntosNoticia() {
        //recargar lista de adjuntos
        noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
        noticiaListModel.setDataModel(noticiaListModel.getAdjuntoNoticia(noticiaListModel.getIdNoticiaActiva(), sesion.getUsuario().getId()));
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

    public void mostrarPopupModificarComentario(ActionEvent actionEvent) {
        noticiaListModel.setIdComentarioActivo(Integer.parseInt(FacesUtils.getRequestParameter("idComentario")));
        noticiaListModel.setComentarioActual(noticiaListModel.buscarComentario(noticiaListModel.getIdComentarioActivo()));
        noticiaListModel.setMrPopupModificarComentario(true);
    }

    public void ocultarPopupModificarComentario(ActionEvent actionEvent) {
        noticiaListModel.setComentarioActual(null);
        noticiaListModel.setMrPopupModificarComentario(false);
    }

    public void mostrarPopupModificarNoticia(ActionEvent actionEvent) {
        noticiaListModel.setIdNoticiaActiva(Integer.parseInt(FacesUtils.getRequestParameter("idNoticia")));
        noticiaListModel.setNoticiaActual(noticiaListModel.buscarNoticia(noticiaListModel.getIdNoticiaActiva()));
        noticiaListModel.setMrPopupModificarNoticia(true);
    }

    public void ocultarPopupModificarNoticia(ActionEvent actionEvent) {
        noticiaListModel.setNoticiaActual(null);
        noticiaListModel.setMrPopupModificarNoticia(false);
    }

    public void mostrarPopupComentariosLikes(ActionEvent actionEvent) {
        //cargar las personas por comentario 
        getPeronasLikeComentario();
        noticiaListModel.setMrPopupLikePerson(true);
    }

    public void mostrarPopupNoticiaLikes(ActionEvent actionEvent) {
        //cargar las personas por Noticia
        getPeronasLikeNoticia();
        noticiaListModel.setMrPopupLikePerson(true);
    }

    public void ocultarPopupLikes(ActionEvent actionEvent) {
        noticiaListModel.setMrPopupLikePerson(false);
    }

    public void mostrarPopupUsuariosCompartidos(ActionEvent actionEvent) {
        //cargar las personas por Noticia
        this.getCompartidos();
        this.setMrVerCompartidos(true);
    }

    public void ocultarPopupUsuariosCompartidos(ActionEvent actionEvent) {
        this.setMrVerCompartidos(false);
        setLikes(null);
    }

    public void mostrarPopupSubirArchivo(ActionEvent actionEvent) {
        noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
        noticiaListModel.setIdNoticia(noticiaListModel.getIdNoticiaActiva());
        noticiaListModel.setDirectorioPath(noticiaListModel.traerDirectorio(noticiaListModel.getIdNoticiaActiva()));
//        UtilLog4j.log.info(this, "idNOticia" + noticiaListModel.getIdNoticiaActiva());
        noticiaListModel.setMrSubirArchivo(true);
    }

    public String mostrarPaginaSubirArchivo() {
//        UtilLog4j.log.info(this, "mostrarpag");
        noticiaListModel.setIdNoticiaActiva((int) tomarIdNoticia());
        noticiaListModel.setIdNoticia(noticiaListModel.getIdNoticiaActiva());
//        UtilLog4j.log.info(this, "idNOticia" + noticiaListModel.getIdNoticiaActiva());
        return "vistas/comunicacion/adjuntarArchivo";
    }

    public void ocultarPopupSubirArchivo(ActionEvent actionEvent) {
        noticiaListModel.setMrSubirArchivo(false);
    }

    public void ocultarPopupVerAdjuntos(ActionEvent actionEvent) {
        noticiaListModel.setMrVerAdjuntos(false);
    }

    public void mostrarPopupVerAdjuntos(ActionEvent actionEvent) {
        traerAdjuntosNoticia();
        noticiaListModel.setMrVerAdjuntos(true);
    }

    public void activarDesactivarCheckBox(ActionEvent actionEvent) {
        this.noticiaListModel.setEnviarNotificacion(!noticiaListModel.isEnviarNotificacion());
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @param noticiaListModel the noticiaListModel to set
     */
    public void setNoticiaListModel(NoticiaListModel noticiaListModel) {
        this.noticiaListModel = noticiaListModel;
    }

    /**
     * @return the comentario
     */
    public HtmlInputTextarea getComentario() {
        return noticiaListModel.getComentario();
//        if(comentario == null){
//             UtilLog4j.log.info(this, "comentario = null");
//             return comentario = new HtmlInputTextarea();                    
//        }else{
//            UtilLog4j.log.info(this, "comentario = No null");
//            return comentario;
//        }
        //return comentario;
    }

    /**
     * @param comentario the comentario to set
     */
    public void setComentario(HtmlInputTextarea comentario) {
        noticiaListModel.setComentario(comentario);
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
//

    public int getIdComentario() {
        return this.noticiaListModel.getIdComentario();
    }

    public void setIdComentario(int idComentario) {
        this.noticiaListModel.setIdComentario(idComentario);
    }

    public boolean isMrPopupModificarComentario() {
        return noticiaListModel.isMrPopupModificarComentario();
    }

    /**
     * @param mrPopupModificar the mrPopupModificar to set
     */
    public void setMrPopupModificarComentario(boolean mrPopupModificar) {
        noticiaListModel.setMrPopupModificarComentario(mrPopupModificar);
    }
//

    /**
     * @return the comentarioActual
     */
    public CoComentario getComentarioActual() {
        return noticiaListModel.getComentarioActual();
    }

    /**
     * @param comentarioActual the comentarioActual to set
     */
    public void setComentarioActual(CoComentario comentarioActual) {
        noticiaListModel.setComentarioActual(comentarioActual);
    }
//

    /**
     * @return the mrPopupModificarNoticia
     */
    public boolean isMrPopupModificarNoticia() {
        return noticiaListModel.isMrPopupModificarNoticia();
    }

    /**
     * @param mrPopupModificarNoticia the mrPopupModificarNoticia to set
     */
    public void setMrPopupModificarNoticia(boolean mrPopupModificarNoticia) {
        noticiaListModel.setMrPopupModificarNoticia(mrPopupModificarNoticia);
    }
//

    /**
     * @return the noticiaActual
     */
    public CoNoticia getNoticiaActual() {
        return noticiaListModel.getNoticiaActual();
    }

    /**
     * @param noticiaActual the noticiaActual to set
     */
    public void setNoticiaActual(CoNoticia noticiaActual) {
        noticiaListModel.setNoticiaActual(noticiaActual);
    }
//
//    /**
//     * @return the mrPopupLikePerson
//     */

    public boolean isMrPopupLikePerson() {
        return noticiaListModel.isMrPopupLikePerson();
    }

    /**
     * @param mrPopupLikePerson the mrPopupLikePerson to set
     */
    public void setMrPopupLikePerson(boolean mrPopupLikePerson) {
        noticiaListModel.setMrPopupLikePerson(mrPopupLikePerson);
    }
//
//    /**
//     * @return the likes
//     */

    public DataModel getLikes() {
        return noticiaListModel.getLikes();
    }

    /**
     * @param likes the likes to set
     */
    public void setLikes(DataModel likes) {
        noticiaListModel.setLikes(likes);
    }
//
//    /*
//     * @return the directorioPath
//     */

    public String getDirectorioPath() {
        if (noticiaListModel.getDirectorioPath() == null) {
            noticiaListModel.setDirectorioPath(noticiaListModel.traerDirectorio(noticiaListModel.getIdNoticiaActiva()));
//            UtilLog4j.log.info(this, "directori path" + noticiaListModel.getDirectorioPath());
            return noticiaListModel.getDirectorioPath();
        } else {
//            UtilLog4j.log.info(this, "directori path" + noticiaListModel.getDirectorioPath());
            return noticiaListModel.getDirectorioPath();
        }
    }

    /**
     * @return the mrSubirArchivo
     */
    public boolean isMrSubirArchivo() {
        return noticiaListModel.isMrSubirArchivo();
    }

    /**
     * @param mrSubirArchivo the mrSubirArchivo to set
     */
    public void setMrSubirArchivo(boolean mrSubirArchivo) {
        noticiaListModel.setMrSubirArchivo(mrSubirArchivo);
    }

//    /**
//     * @return the dataModel
//     */
    public DataModel getDataModel() {
        return noticiaListModel.getDataModel();
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
        noticiaListModel.setDataModel(dataModel);
    }
//

    /**
     * @return the mrVerAdjuntos
     */
    public boolean isMrVerAdjuntos() {
        return noticiaListModel.isMrVerAdjuntos();
    }

    /**
     * @param mrVerAdjuntos the mrVerAdjuntos to set
     */
    public void setMrVerAdjuntos(boolean mrVerAdjuntos) {
        noticiaListModel.setMrVerAdjuntos(mrVerAdjuntos);
    }
//

    /**
     * @return the comentar
     */
    public boolean isComentar() {
        return noticiaListModel.isComentar();
    }

    /**
     * @param comentar the comentar to set
     */
    public void setComentar(boolean comentar) {
        noticiaListModel.setComentar(comentar);
    }
//

    /**
     * @return the filtrar
     */
    public boolean isFiltrar() {
        return noticiaListModel.isFiltrar();
    }

    /**
     * @param filtrar the filtrar to set
     */
    public void setFiltrar(boolean filtrar) {
        noticiaListModel.setFiltrar(filtrar);
    }

    /**
     * @return the filtrar
     */
    public boolean isInicio() {
        return noticiaListModel.isInicio();
    }

    /**
     * @param filtrar the filtrar to set
     */
    public void setInicio(boolean inicio) {
        noticiaListModel.setInicio(inicio);
    }

    /**
     * @return the filtrar
     */
    public boolean isPaginar() {
        return noticiaListModel.isPaginar();
    }

    /**
     * @param filtrar the filtrar to set
     */
    public void setPaginar(boolean paginar) {
        noticiaListModel.setPaginar(paginar);
    }

    /**
     * @return the filtrar
     */
    public boolean isPrivilegioSubirArchivo() {
        return noticiaListModel.isPrivilegioSubirArchivo();
    }

    /**
     * @param filtrar the filtrar to set
     */
    public void setPrivilegioSubirArchivo(boolean privilegioSubirArchivo) {
        noticiaListModel.setPrivilegioSubirArchivo(privilegioSubirArchivo);
    }
//
//
//

    /**
     * @return the idNoticiaActiva
     */
    public int getIdNoticiaActiva() {
        return noticiaListModel.getIdNoticia();
    }

    /**
     * @param idNoticiaActiva the idNoticiaActiva to set
     */
    public void setIdNoticiaActiva(int idNoticiaActiva) {
        noticiaListModel.setIdNoticiaActiva(idNoticiaActiva);
    }
//

    /**
     * @return the inputHidden
     */
    public HtmlInputHidden getInputHidden() {
        return inputHidden;
    }

    /**
     * @param inputHidden the inputHidden to set
     */
    public void setInputHidden(HtmlInputHidden inputHidden) {
        this.inputHidden = inputHidden;
    }

    public boolean isEnviarNotificacion() {
        return noticiaListModel.isEnviarNotificacion();
    }

    /**
     * @param enviarNotificacion the enviarNotificacion to set
     */
    public void setEnviarNotificacion(boolean enviarNotificacion) {
        noticiaListModel.setEnviarNotificacion(enviarNotificacion);
    }

    public boolean isMrVerCompartidos() {
        return noticiaListModel.isMrVerCompartidos();
    }

    /**
     * @param mrVerCompartidos the mrVerCompartidos to set
     */
    public void setMrVerCompartidos(boolean mrVerCompartidos) {
        noticiaListModel.setMrVerCompartidos(mrVerCompartidos);
    }
}
