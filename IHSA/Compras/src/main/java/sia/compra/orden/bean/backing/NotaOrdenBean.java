/*
 * NotaOrdenBean.java
 * Creado el 1/12/2009, 05:52:51 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.orden.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.CoComentario;
import sia.modelo.CoNoticia;
import sia.modelo.NotaOrden;
import sia.modelo.Orden;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.orden.vo.ContactoOrdenVo;
import sia.modelo.requisicion.vo.NotaVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.orden.impl.NotificacionOrdenImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.orden.impl.NotaOrdenImpl;
import sia.servicios.orden.impl.OcOrdenCoNoticiaImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.com @date 1/12/2009
 */
@Named
@ViewScoped
public class NotaOrdenBean implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    //------------------------------------------------------
    @Inject
    private NotaOrdenImpl notaOrdenServicioImpl;
    @Inject
    private OrdenImpl ordenServicioImpl;
    @Inject
    private NotificacionOrdenImpl notificacionOrdenImpl;
    @Inject
    private CoNoticiaImpl coNoticiaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private OcOrdenCoNoticiaImpl ocOrdenCoNoticiaImpl;
    //
    //-- Managed Beans ----
    @Inject
    UsuarioBean usuarioBean;
    // - - - - - - - - - -
    private DataModel listaNotas = null; //almacena la lista de Ordenes de compra
    //----------------------
    private NotaOrden notaActual;
    private String operacion;
    //

    //
    // Atributos
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
    private int idComentarioActivo = 0;
    private int idNoticiaActiva = 0;
    private int idAdjuntoActivo;
    private DataModel dataModel;
    private CoNoticia noticiaActual;
    private CoComentario comentarioActual;
//
    private List<NoticiaVO> listaNoticia;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    /**
     * Creates a new instance of NotaOrdenBean
     */
    public NotaOrdenBean() {
//         this.respuestaActual = new NotaOrden();
//         this.respuestaActual.setTitulo("Respuesta: ");
//         this.respuestaActual.setMensaje("Mensaje de la respuesta...");
//         this.notaActual = new NotaOrden();
//         this.notaActual.setTitulo("Respuesta: ");
//         this.notaActual.setMensaje("Mensaje de la respuesta...");
    }

    @PostConstruct
    public void iniciar() {
        traerNoticiaPorUsuario();
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
        return operacion;
    }

    public List<NoticiaVO> traerNoticiaPorOrden(int idOrden) {
        return ocOrdenCoNoticiaImpl.traerNoticiaPorOrden(idOrden, true);
    }

    public void traerNoticiaPorUsuario() {
        if (usuarioBean.getUsuarioConectado() != null) {
            listaNoticia = ocOrdenCoNoticiaImpl.traerNoticiaPorUsuario(usuarioBean.getUsuarioConectado().getId(), true, usuarioBean.getUsuarioConectado().getApCampo().getId());
        }

    }

    public void nuevoComentario(int idN, int idCampo, String respuesta) {
        //String comen = (String) FacesUtilsBean.getRequestParameter("respuesta");
        textoNoticia = respuesta;
        if (textoNoticia.trim().isEmpty() || textoNoticia.equals("null")) {
            FacesUtilsBean.addInfoMessage("Agregue un comentario a la noticia .  .  .  . ");
        } else {
            setIdNoticiaActiva(idN);
            agregarComentario(getIdNoticiaActiva(), idCampo, textoNoticia);
            traerNoticiaPorUsuario();
            textoNoticia = "";
        }
    }

    public DataModel comentarNoticia(Integer idNoticia, String idUsuario) {
        try {
            List<NoticiaVO> l = coNoticiaImpl.getNoticias(idUsuario, 0, 0);
            for (int it = 0; it < l.size(); it++) {
                if (l.get(it).getId().equals(idNoticia)) {
                    l.get(it).setComentar(true);
                }
            }
            return new ListDataModel(l);
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Noticia comentar : :  : " + ex.getMessage());
            return null;
        }
    }

//
    public void verComentariosDeNoticia(int idN) {
        try {
            setComentar(false);
            this.setIdNoticiaActiva(idN);
            setFiltrar(true);

            //this.comentarios = null;
//            idNoticiaActiva = tomarIdNoticia();           c
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Error : : : : :" + ex.getMessage());
        }
    }

    public DataModel traerComentario(int idNoticia) {
        return new ListDataModel(coNoticiaImpl.getComentariosFiltrados(idNoticia, usuarioBean.getUsuarioConectado().getId()));
    }

    public void modificarNoticia() {
        if (!this.getNoticiaActual().getMensajeAutomatico().equals("")) {
            setIdNoticiaActiva((int) noticiaActual.getId());
            coNoticiaImpl.editNoticia(noticiaActual, usuarioBean.getUsuarioConectado().getId());
            traerNoticiaPorUsuario();
            setMrPopupModificarNoticia(false);
        } else {
            FacesUtilsBean.addErrorMessage("Por favor escribe la noticia..");
        }
    }

    public void eliminarNoticia(int idN) {
        try {
            this.setIdNoticiaActiva(idN);
            coNoticiaImpl.eliminarNoticia(idNoticiaActiva, usuarioBean.getUsuarioConectado().getId());
            UtilLog4j.log.fatal(this, "Comentario eliminado");
            traerNoticiaPorUsuario();
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
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exception en comentar noticia " + e.getMessage());
        }
    }

    public void agregarComentario(int idNoticia, int campo, String comentario) {
        if (!comentario.equals("")) {
            //tomar id de la noticia actual para buscarla..
            coNoticiaImpl.nuevoComentario(idNoticia, usuarioBean.getUsuarioConectado().getId(), comentario, enviarNotificacion, false, campo, Constantes.MODULO_COMPRA);

            this.setComentar(false);
        }
    }

    public void modificarComentario() {
        if (!this.getComentarioActual().getMensaje().equals("")) {
            coNoticiaImpl.editComentario(getComentarioActual(), usuarioBean.getUsuarioConectado().getId());
            setMrPopupModificarComentario(false);
            //
            traerNoticiaPorUsuario();
        } else {
            FacesUtilsBean.addErrorMessage("Por favor escribe el comentario..");
        }
    }

    public void eliminarComentario(int idC) {
        try {
            this.idComentarioActivo = idC;
            UtilLog4j.log.fatal(this, "idComentario " + idComentarioActivo);
            coNoticiaImpl.eliminarComentario(idComentarioActivo, usuarioBean.getUsuarioConectado().getId());
            //
            traerNoticiaPorUsuario();
            UtilLog4j.log.fatal(this, "Comentario eliminado");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Exception en elimnar cmentario " + e.getMessage());
        }
    }

    public void subirArchivo(FileUploadEvent fileEvent) {
        UtilLog4j.log.fatal(this, "subirArchivo");
        boolean v = false;
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();
        fileInfo = fileEvent.getFile();
        try {

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setRuta(getDirectorioPath());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                v
                        = coNoticiaImpl.addArchivo(
                                documentoAnexo.getNombreBase(),
                                documentoAnexo.getRuta(),
                                documentoAnexo.getTipoMime(),
                                documentoAnexo.getTamanio(),
                                idNoticiaActiva,
                                usuarioBean.getUsuarioConectado().getId()
                        );
                traerNoticiaPorUsuario();
                setMrSubirArchivo(false);
                PrimeFaces.current().executeScript("PF('dlgNotaSubArh').hide()");
            } else {
                FacesUtilsBean.addInfoMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

//                if (fileInfo.getStatus().isSuccess() || fileInfo.isSaved()) {
//                    UtilLog4j.log.fatal(this, "Si guardo el archivo..");
//                    v = coNoticiaImpl.addArchivo(fileInfo.getFileName(),
//                            fileInfo.getContentType(),
//                            fileInfo.getSize(), idNoticiaActiva, usuarioBean.getUsuarioConectado().getId());
//                    setMrSubirArchivo(false);
//                    this.noticias = null;
//                } else {
//                    UtilLog4j.log.fatal(this, "No paso ");
//                }
            if (v == false) {
                FacesUtilsBean.addErrorMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
            }

            fileInfo.delete();
        } catch (IOException | SIAException e) {
            LOGGER.fatal(this, "Excepcion al subir archivo", e);
        }
    }

    public void eliminarArchivo(int idAr, int idRelacion, int idN) {
        idAdjuntoActivo = idAr;
        idNoticiaActiva = idN;
        //
        if (quitarArchivo(this.idNoticiaActiva, this.idAdjuntoActivo, idRelacion, usuarioBean.getUsuarioConectado().getId())) {
            this.dataModel
                    = new ListDataModel(
                            coNoticiaImpl.getAdjuntosNoticia(
                                    idNoticiaActiva,
                                    usuarioBean.getUsuarioConectado().getId()
                            )
                    );
            FacesUtilsBean.addInfoMessage("Se eliminó correctamente el archivo...");
            traerNoticiaPorUsuario();
        } else {
            FacesUtilsBean.addErrorMessage("Existió un error al eliminar el arvhivo..");
        }
    }

    public boolean quitarArchivo(Integer idNoticia, Integer idArchivo, Integer idRelacion, String idUsuario) {
        boolean retVal = false;
        //String path = this.siParametroImpl.find(1).getUploadDirectory();
        SiAdjunto adjunto = siAdjuntoImpl.find(idArchivo);
        if (adjunto == null) {
            FacesUtilsBean.addErrorMessage("No se localizó el adjunto.");
        } else {
            try {
                proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(adjunto.getUrl());
                coNoticiaImpl.deleteArchivo(adjunto, idRelacion, idUsuario);

                retVal = true;
            } catch (SIAException ex) {
                LOGGER.error("Eliminando adjunto " + adjunto.getUrl(), ex);
            }
        }
        return retVal;

//        try {
//            File file = new File(path + adjunto.getUrl());
//            UtilLog4j.log.fatal(this, "path :" + path);
//            UtilLog4j.log.fatal(this, "path absoluto :" + adjunto.getUrl());
//            if (file.delete()) {
//                UtilLog4j.log.fatal(this, "Entro a eliminar");
//                coNoticiaImpl.deleteArchivo(adjunto, idRelacion, idUsuario);
//                UtilLog4j.log.fatal(this, "Elimino el adjunto de la noticia");
//            }
//            UtilLog4j.log.fatal(this, "entrando a eliminar el archivo fisico");
//            String dir = "Comunicacion/Noticia" + "/" + idNoticia + "/";
//            UtilLog4j.log.fatal(this, "Ruta carpeta: " + dir);
//            File sessionfileUploadDirectory = new File(path + dir);
//            if (sessionfileUploadDirectory.isDirectory()) {
//                try {
//                    sessionfileUploadDirectory.delete();
//                } catch (SecurityException e) {
//                    UtilLog4j.log.fatal(this, e.getMessage());
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(this, "Excepcion en quitar archivo :" + e.getMessage());
//            return false;
//        }
    }

    public void uploadFile() {
        UtilLog4j.log.fatal(this, "upload");
    }

    public void traerAdjuntosNoticia(int idNoticia) {
        //recargar lista de adjuntos
        setIdNoticiaActiva(idNoticia);
        this.dataModel = new ListDataModel(coNoticiaImpl.getAdjuntosNoticia(idNoticiaActiva, usuarioBean.getUsuarioConectado().getId()));
    }

    public void mostrarPopupModificarComentario(int idC) {
        idComentarioActivo = idC;
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

    public void mostrarPopupSubirArchivo(int idN) {
        this.setIdNoticiaActiva(idN);
        setDirectorioPath(traerDirectorio(idNoticiaActiva));
        UtilLog4j.log.fatal(this, "idNOticia" + idNoticiaActiva);
        this.setMrSubirArchivo(true);
    }
//public String TraerDirectorio(Integer idNoticia) {

    private String traerDirectorio(int idNoticia) {
//            this.setDirectorioPath(siParametroImpl.find(1).getUploadDirectory() + "SGyL/Vehiculo/ComprobanteMantenimiento" + "/" + getSgMantenimiento().getId() + "/");
        //return siParametroImpl.find(1).getUploadDirectory() + "Comunicacion/Noticia/" + idNoticia + "/";
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
     * ******************
     */
    /**
     * @return the NotaActual
     */
    public NotaOrden getNotaActual() {
        return notaActual;
    }

    /**
     * @param NotaActual the NotaActual to set
     */
    public void setNotaActual(NotaOrden notaActual) {
        this.notaActual = notaActual;
    }

    /**
     * @return Lista de Notas por usuario
     */
    public DataModel getNotasPorInvitado() {
        try {
            this.setListaNotas(new ListDataModel(this.notaOrdenServicioImpl.getNotasPorInvitado(this.usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getApCampo().getId())));
        } catch (RuntimeException ex) {
            this.setListaNotas(null);
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return this.getListaNotas();
    }

    /**
     * @return Lista de Notas por ORden de compra
     */
    public DataModel getNotasPorOrden(Object idOrden) {
        try {
            this.setListaNotas(new ListDataModel(this.notaOrdenServicioImpl.getNotasPorOrden(idOrden)));
        } catch (RuntimeException ex) {
            this.setListaNotas(null);
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return this.getListaNotas();
    }

    public DataModel getNotasParaDetalleOrden(Object idOrden) {
        try {
            this.setListaNotas(new ListDataModel(this.notaOrdenServicioImpl.getNotasParaDetalleOrden(idOrden)));
        } catch (RuntimeException ex) {
            this.setListaNotas(null);
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return this.getListaNotas();
    }

    public long getTotalNotasPorInvitado() {
        try {
            return ocOrdenCoNoticiaImpl.totalNoticiaPorUsuario(usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getApCampo().getId());
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return 0;

    }

    public void seleccionarNota() {
        NotaVO notaVo = ((NotaVO) this.getListaNotas().getRowData());
        this.notaActual = this.notaOrdenServicioImpl.find(notaVo.getId());
    }

    public void completarCreacionNota() {
        try {
            if (this.getOperacion().equals(Constantes.UPDATE_OPERATION)) {
                this.notaOrdenServicioImpl.edit(this.notaActual);
                FacesUtilsBean.addInfoMessage("Se actualizó correctamente la nota");
            } else {
                List<ContactoOrdenVo> listaContactosOrden = ordenServicioImpl.getContactosVo(notaActual.getOrden().getId());
                this.notaActual.setFecha(new Date());
                this.notaActual.setHora(new Date());
                this.notaActual.setRespuestas(0);
                this.notaActual.setIdentificador(0);
                this.notaActual.setFinalizada(Constantes.BOOLEAN_FALSE);
                //    UtilLog4j.log.fatal(this, "Corroes invi orden " + invitados.toString());
//
                UsuarioVO uvo = usuarioImpl.traerResponsableGerencia(this.notaActual.getOrden().getApCampo().getId(), Constantes.GERENCIA_ID_COMPRAS,
                        this.notaActual.getOrden().getCompania().getRfc());
                StringBuilder asunto = new StringBuilder();
                asunto.append("Nota de la orden: ").append(notaActual.getOrden().getConsecutivo()).append(' ');
                if (notificacionOrdenImpl.enviarNotificacionNotaOrden(
                        castUsuarioInvitados(notaActual.getOrden()),
                        "",
                        uvo.getMail(),
                        this.notaActual.getOrden(),
                        asunto.toString(), usuarioBean.getUsuarioConectado().getNombre(),
                        notaActual.getMensaje(),
                        listaContactosOrden)) {
                    this.notaActual = this.notaOrdenServicioImpl.save(this.notaActual);
                    //
                    //Noticias nueavas
                    CoNoticia coNoticia = coNoticiaImpl.nuevaNoticia(usuarioBean.getUsuarioConectado().getId(), asunto.toString(), "",
                            notaActual.getMensaje(), 0, 0, castUsuarioComparteCon(notaActual.getOrden()));
                    //Guarda la nota
                    ocOrdenCoNoticiaImpl.guardarNoticia(usuarioBean.getUsuarioConectado().getId(), coNoticia, notaActual.getOrden());

                }
                FacesUtilsBean.addInfoMessage("Se creó correctamente la nota");
            }
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNotaOrden);");
            //this.popupCrearNotaBean.toggleModal(actionEvent);
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    private String castUsuarioInvitados(Orden orden) {
        try {
            //        hacer una lista de invitados enviar la notificacion y si se envio correctamente guardarlos a todos
            StringBuilder invitados = new StringBuilder();
            switch (orden.getAutorizacionesOrden().getEstatus().getId()) {
                case Constantes.ESTATUS_SOLICITADA:
                    invitados.append(this.notaActual.getOrden().getAutorizacionesOrden().getSolicito().getEmail());
                    break;
                case Constantes.ORDENES_SIN_APROBAR:
                    //solicita
                    invitados.append(this.notaActual.getOrden().getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_MPG:
                    //solicita
                    invitados.append(this.notaActual.getOrden().getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaGerencia().getEmail());
                    invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_IHSA:
                    invitados.append(this.notaActual.getOrden().getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaGerencia().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaMpg().getEmail());
                    invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ESTATUS_POR_APROBAR_SOCIO:
                    invitados.append(this.notaActual.getOrden().getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaGerencia().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaMpg().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaIhsa().getEmail());
                    invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_COMPRAS:
                    invitados.append(this.notaActual.getOrden().getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaGerencia().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaMpg().getEmail());
                    invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaIhsa().getEmail());
                    invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
                    if (orden.getCompania().isSocio()) {
                        invitados.append(",").append(this.notaActual.getOrden().getAutorizacionesOrden().getAutorizaFinanzas().getEmail());
                    }
                    break;
                default:
                    invitados.append(this.notaActual.getOrden().getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
            }
            return invitados.toString();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al castear los usuario  " + e.getMessage());
            return null;
        }
    }

    private List<ComparteCon> castUsuarioComparteCon(Orden orden) {
        try {
            List<ComparteCon> listaCompartidos = new ArrayList<ComparteCon>();
            //        hacer una lista de invitados enviar la notificacion y si se envio correctamente guardarlos a todos
            switch (orden.getAutorizacionesOrden().getEstatus().getId()) {
                case Constantes.ORDENES_SIN_APROBAR:
                    //solicita
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAnalista()));
                    //invitados.append(this.notaActual.getOrden().getAutorizacionesOrden().getSolicito().getEmail());
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    //invitados.append(",").append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_MPG:
                    //solicita
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAnalista()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_IHSA:
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAnalista()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaMpg()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    break;
                case Constantes.ESTATUS_POR_APROBAR_SOCIO:
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAnalista()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaMpg()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaIhsa()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_COMPRAS:
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAnalista()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaMpg()));
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaIhsa()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    if (orden.getCompania().isSocio()) {
                        listaCompartidos.add(castComparteCon(notaActual.getOrden().getAutorizacionesOrden().getAutorizaFinanzas()));
                    }
                    break;
                default:
                    listaCompartidos.add(castComparteCon(notaActual.getOrden().getAnalista()));
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

    public void cambiarNotaOrden(int idNota) {
        this.setNotaActual(this.notaOrdenServicioImpl.find(idNota));
    }

    /**
     * @return the listaNotas
     */
    public DataModel getListaNotas() {
        return listaNotas;
    }

    /**
     * @param listaNotas the listaNotas to set
     */
    public void setListaNotas(DataModel listaNotas) {
        this.listaNotas = listaNotas;
    }

    /**
     * @param idNoticiaActiva the idNoticiaActiva to set
     */
    public void setIdNoticiaActiva(int idNoticiaActiva) {
        this.idNoticiaActiva = idNoticiaActiva;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * @return the listaNoticia
     */
    public List<NoticiaVO> getListaNoticia() {
        return listaNoticia;
    }

    /**
     * @param listaNoticia the listaNoticia to set
     */
    public void setListaNoticia(List<NoticiaVO> listaNoticia) {
        this.listaNoticia = listaNoticia;
    }
}
