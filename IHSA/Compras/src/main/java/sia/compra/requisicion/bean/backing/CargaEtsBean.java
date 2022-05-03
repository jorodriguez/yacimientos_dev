package sia.compra.requisicion.bean.backing;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.CustomScoped;
import javax.faces.component.html.HtmlInputHidden;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
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
import sia.compra.orden.bean.backing.OrdenBean;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.OcCategoriaEts;
import sia.modelo.OcOrdenEts;
import sia.modelo.ReRequisicionEts;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.OrdenEtsVo;
import sia.modelo.requisicion.vo.RequisicionEtsVo;
import sia.servicios.orden.impl.OcCategoriaEtsImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionSiMovimientoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author Héctor
 */
//
@Named(value = CargaEtsBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class CargaEtsBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "cargaEtsBean";
    //------------------------------------------------------
    //------------------------------------------------------
    @Inject
    private SiParametroImpl parametrosSistema;
    @Inject
        private SiAdjuntoImpl servicioSiAdjuntoImpl;
        @Inject
        private ReRequisicionEtsImpl servicioReRequisicion; //servicio que realiza operaciones con la relacione entee Requisicion y SiAdjunto
    @Inject
    private OcCategoriaEtsImpl servicioOcCategoriaEts;
    @Inject
    private OcOrdenEtsImpl servicioOcOrdenEts;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private RequisicionSiMovimientoImpl requisicionSiMovimientoImpl;

    //------------------------------------------------------
    @Inject
    UsuarioBean usuarioBean;
    private final PopupGeneralBean popupGeneralBean = (PopupGeneralBean) FacesUtilsBean.getManagedBean("popupGeneralBean");
    private final RequisicionBean requisicionBean = (RequisicionBean) FacesUtilsBean.getManagedBean("requisicionBean");
    private final OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");

    private DataModel<ReRequisicionEts> listaEts; //almacena la lista Especificacion tecnica de suministro
    private DataModel<OcCategoriaEts> listaOcCategoriaEts;
    private DataModel<OrdenEtsVo> listaOcOrdenEts;
    private List listaTablaComparativa;

    // file upload completed percent (Progress)
    private int fileProgress;
    private String uploadDirectory = Constantes.VACIO;
    private SiAdjunto etsActualAdjunto;
    private ReRequisicionEts etsReRequisicion;
    private OcOrdenEts etsOcOrden;
    private String clasificacion;
    //
    private HtmlInputHidden inputHidden; // este componente me ayuda a tomar el valor del comunicado que esta iterando
    private boolean seleccionarTodo;
    private int idCategoriaSelccionada = -1;
    private int idOrdenSeleccionada = -1;
    private Map<Integer, Boolean> filasSeleccionadas = new HashMap<>();
    private String paginaRegreso = Constantes.VACIO;
    private String opcionSeleccionada;
    private String preDirectorio;
    private boolean requi;

    private DataModel<ReRequisicionEts> listaEtsEspera;

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    /**
     * Creates a new instance of CargaEtsBean
     */
    public CargaEtsBean() {
    }

    public DataModel getEtsPorRequisicion() {
        if (requisicionBean.getRequisicionActual() == null) {
            listaEts = null;
        } else {
            try {
                if (requisicionBean.getRequisicionActual() != null && requisicionBean.getRequisicionActual().getId() != null) {

                    listaEts
                            = new ListDataModel(
                                    servicioReRequisicion.traerAdjuntosPorRequisicion(
                                            requisicionBean.getRequisicionActual().getId()
                                    )
                            );
                }
            } catch (Exception ex) {
                listaEts = null;
                LOGGER.fatal(this, null, ex);
            }
        }
        return getListaEts();
    }

    public DataModel getEtsPorRequisicionEspera() {
        if (requisicionBean.getRequisicionActual() == null) {
            listaEts = null;
        } else {
            try {

                if (requisicionBean.getRequisicionActual() != null && requisicionBean.getRequisicionActual().getId() != null) {

                    listaEtsEspera
                            = new ListDataModel(servicioReRequisicion.traerAdjuntosPorRequisicionVisibleTipo(
                                    requisicionBean.getRequisicionActual().getId(),
                                    false, "ESPERA"));
                }
            } catch (Exception ex) {
                listaEts = null;
                LOGGER.fatal(this, null, ex);
            }
        }
        return getListaEtsEspera();
    }

    public void eliminarEts(Object adjReq) {
        try {
            RequisicionEtsVo rq = (RequisicionEtsVo) adjReq;
            etsActualAdjunto = servicioSiAdjuntoImpl.find(rq.getIdAdjunto());

            proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(etsActualAdjunto.getUrl());
            eliminarEtsComplemento();

            FacesUtilsBean.addInfoMessage("Se eliminó correctamente el archivo...");
            getEtsPorRequisicion();
        } catch (SIAException e) {
            LOGGER.fatal(this, "Excepcion en eliminar ETS", e);
            FacesUtilsBean.addErrorMessage("Ocurrió un problema al eliminar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    public void eliminarEsperaEts(ReRequisicionEts rre) {
        try {
            //PrimeFaces.current().executeScript( ";mensajeEliminar();");
            setEtsReRequisicion(rre);
            etsActualAdjunto = getEtsReRequisicion().getSiAdjunto();

            proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(etsActualAdjunto.getUrl());
            eliminarEtsComplemento();
            getEtsPorRequisicionEspera();

            FacesUtilsBean.addInfoMessage("Se eliminó correctamente el archivo...");
        } catch (SIAException e) {
            LOGGER.fatal(this, "Excepcion en eliminar ETS", e);
            FacesUtilsBean.addErrorMessage("Ocurrió un problema al eliminar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    private void eliminarEtsComplemento() {
        etsActualAdjunto.setEliminado(Constantes.BOOLEAN_TRUE);
        servicioSiAdjuntoImpl.edit(etsActualAdjunto);
        servicioReRequisicion.eliminarReRequisicion(getEtsReRequisicion(), usuarioBean.getUsuarioConectado());
        //FacesUtilsBean.addInfoMessage("Se eliminó correctamente el archivo...");

    }

    public <T> List<T> getDataModelAsList(DataModel dm) {
        return (List<T>) dm.getWrappedData();
    }
//*

    public void actualizarEts(SiAdjunto requisicionEts) {
        etsActualAdjunto = requisicionEts;
        popupGeneralBean.toggleModal();
    }

    public void completarActualizacionEts() {
        this.servicioSiAdjuntoImpl.edit(this.etsActualAdjunto);
        PrimeFaces.current().dialog().closeDynamic("dlgNotaSubArh");
    }

    public int getFileProgress() {
        return fileProgress;
    }

    /**
     * @return the uploadDirectory
     */
    public String getUploadDirectory() {
        if (uploadDirectory.isEmpty()) {
            uploadDirectory = parametrosSistema.find(1).getUploadDirectory();
        }
        return uploadDirectory;
    }

    public String getUploadDirectoryOrden() {
        setRequi(false);
        /*setPreDirectorio(
	 new StringBuilder().append("ETS/Orden/")
	 .append(ordenBean.getOrdenActual().getId()).toString()
	 );
	 return new StringBuilder().append(getUploadDirectory()).append(getPreDirectorio()).toString();*/

        return new StringBuilder().append("ETS/Orden/")
                .append(ordenBean.getOrdenActual().getId()).toString();
    }

    public String getUploadDirectoryRequi() {
        setRequi(true);
        /*setPreDirectorio(
	 new StringBuilder().append("ETS/Requisicion/")
	 .append(requisicionBean.getRequisicionActual().getId()).toString()
	 );
	 return new StringBuilder().append(getUploadDirectory()).append(getPreDirectorio()).toString();*/

        return new StringBuilder().append("ETS/Requisicion/")
                .append(requisicionBean.getRequisicionActual().getId()).toString();
    }

    /*
     * *********************** CARGA DE ETS DESDE REQUISICION****************
     */
    /**
     *
     * @param
     */
    public void eliminarEtsOrden(int idRelacion) {
        try {
            LOGGER.info(String.format("rel : : : {0}%s", idRelacion));
            // Buscar en ReRequicision
            setEtsOcOrden(servicioOcOrdenEts.find(idRelacion));
            ReRequisicionEts reRequisicion
                    = servicioReRequisicion.buscarPorRequisicionAdjunto(
                            getEtsOcOrden().getOrden().getRequisicion(),
                            getEtsOcOrden().getSiAdjunto()
                    );

            if (reRequisicion == null) {
                if (getEtsOcOrden() != null) {
                    etsActualAdjunto = getEtsOcOrden().getSiAdjunto();
                    if (eliminarAdjunto(etsActualAdjunto, usuarioBean.getUsuarioConectado())) {
                        servicioOcOrdenEts.eliminarOcOrdenEts(getEtsOcOrden().getId(), usuarioBean.getUsuarioConectado().getId());
                        FacesUtilsBean.addInfoMessage("Se eliminó correctamente el archivo...");
                    } else {
                        FacesUtilsBean.addErrorMessage("Ocurrió un problema al eliminar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
                    }
                }
            } else {
                if (servicioOcOrdenEts.eliminarOcOrdenEts(getEtsOcOrden().getId(), usuarioBean.getUsuarioConectado().getId())) {
                    servicioReRequisicion.ponerDisgregada(
                            reRequisicion,
                            usuarioBean.getUsuarioConectado(),
                            Constantes.BOOLEAN_FALSE
                    );
                    FacesUtilsBean.addInfoMessage("El archivo ETS regreso a la requisición.");
                }
            }
            //etsPorOrdenCategoria();
            ordenEtsPorCategoria();
            etsPorOrdenRequisicion();
        } catch (NumberFormatException e) {
            LOGGER.fatal(this, "Excepcion en eliminar ETS ", e);
            FacesUtilsBean.addErrorMessage("Ocurrió un problema al eliminar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    public boolean eliminarAdjunto(SiAdjunto siAdjunto, Usuario usuario) {
        boolean retVal = false;
        try {

            proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(siAdjunto.getUrl());
            servicioSiAdjuntoImpl.delete(siAdjunto, usuarioBean.getUsuarioConectado().getId());

            retVal = true;
        } catch (SIAException ex) {
            LOGGER.fatal(this, "Excepcion en eliminar adjunto", ex);
        }

        return retVal;
    }

    /*
     * Opcion de eliminacion de ets de Requicision, se integra de nuevo a la
     * requicision NO se elimina, solo se Disgrega
     */
    public void eliminarEtsOrdenRequisicion() {
        try {
            int idRelacionn = Integer.valueOf(FacesUtilsBean.getRequestParameter("idRelacionEliminar"));

            setEtsOcOrden(servicioOcOrdenEts.find(idRelacionn));

            if (getEtsOcOrden() != null) {
                etsActualAdjunto = getEtsOcOrden().getSiAdjunto();
            }
        } catch (NumberFormatException e) {
            LOGGER.fatal(this, "Excepcion en eliminar ETS " + e.getMessage(), e);
        }
    }

    public DataModel getCategoriaEts() {
        if (ordenBean.getOrdenActual() == null) {
            setListaOcCategoriaEts(null);
        } else {
            try {
                setListaOcCategoriaEts(
                        new ListDataModel(
                                servicioOcCategoriaEts.traerOcCategoriaEts()
                        )
                );
            } catch (Exception ex) {
                setListaOcCategoriaEts(null);
                LOGGER.fatal(this, " excepcion en getOcCategoria " + ex.getMessage(), ex);
            }
        }
        return getListaOcCategoriaEts();
    }

    public void ordenEtsPorCategoria() {
        if (ordenBean.getOrdenActual() == null) {
            setListaOcOrdenEts(null);
        } else {
            try {
                setListaOcOrdenEts(
                        new ListDataModel(
                                servicioOcOrdenEts.traerEtsPorOrdenCategoria(
                                        ordenBean.getOrdenActual().getId()
                                )
                        )
                );
            } catch (Exception ex) {
                setListaOcOrdenEts(null);
                LOGGER.fatal(this, null, ex);
            }
        }
    }

    public void traerEspecificacionTecnica() {
        if (ordenBean.getOrdenActual() == null) {
            setListaOcOrdenEts(null);
        } else {
            try {
                setListaOcOrdenEts(
                        new ListDataModel(
                                servicioOcOrdenEts.traerEtsPorOrdenCategoria(
                                        ordenBean.getOrdenActual().getId(),
                                        Constantes.CERO
                                )
                        )
                );
            } catch (Exception ex) {
                setListaOcOrdenEts(null);
                LOGGER.fatal(this, null, ex);
            }
        }
    }

    public void etsPorOrdenRequisicion() {
        if (ordenBean.getOrdenActual() == null) {
            listaEts = null;
        } else {
            try {
                listaEts
                        = new ListDataModel(
                                servicioReRequisicion.traerAdjuntosPorRequisicionVisible(
                                        ordenBean.getOrdenActual().getRequisicion().getId(),
                                        Constantes.BOOLEAN_TRUE
                                )
                        );
            } catch (Exception ex) {
                listaEts = null;
                LOGGER.fatal(this, ex.getMessage(), ex);
            }
        }
    }

    public void actualizarEtsDesdeOc() {
        etsActualAdjunto = ((OcOrdenEts) getListaOcOrdenEts().getRowData()).getSiAdjunto();
        popupGeneralBean.toggleModal();
    }

    public void actualizarEtsDesdeEtsCategoria(int idRelacionn) {

        setEtsOcOrden(servicioOcOrdenEts.find(idRelacionn));
        etsActualAdjunto = getEtsOcOrden().getSiAdjunto();
        popupGeneralBean.toggleModal();
    }

    public void seleccionTodo(ValueChangeEvent textChangeEvent) {
//        DataModel<ReRequisicionEts> lista = listaEts;

        for (ReRequisicionEts reRequisicion : getListaEts()) {
            if (filasSeleccionadas.get(reRequisicion.getId())) {
                filasSeleccionadas.remove(reRequisicion.getId());
            }
        }

    }

    public List<SelectItem> getTraerOcCategoriasItems() {
        List<SelectItem> listaItem = null;

        try {

            List<OcCategoriaEts> lista = servicioOcCategoriaEts.traerOcCategoriaEts();

            if (!lista.isEmpty()) {
                listaItem = new ArrayList<>();

                for (OcCategoriaEts ce : lista) {
                    SelectItem item = new SelectItem(ce.getId(), ce.getNombre());
                    listaItem.add(item);
                }
            }

        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
        }

        return listaItem;
    }

    public void guardarSeleccionEts() {

        try {
            if (getListaEts().getRowCount() > 0) {
                for (ReRequisicionEts reRequisicion : getListaEts()) {
                    if (filasSeleccionadas.get(reRequisicion.getId())
                            && servicioOcOrdenEts.crearOcOrdenEts(
                                    ordenBean.getOrdenActual().getId(),
                                    idCategoriaSelccionada,
                                    reRequisicion.getSiAdjunto(),
                                    usuarioBean.getUsuarioConectado())) {

                        servicioReRequisicion.ponerDisgregada(
                                reRequisicion,
                                usuarioBean.getUsuarioConectado(),
                                Constantes.BOOLEAN_TRUE
                        );

                        filasSeleccionadas.remove(reRequisicion.getId());
                    }
                }
            } else {
                FacesUtilsBean.addInfoMessage("Seleccione un ETS de la requisición");
            }

            //setIdCategoriaSelccionada(-1);
            ordenEtsPorCategoria();
            etsPorOrdenRequisicion();
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion en guardar EtsSeleccionadas " + e.getMessage(), e);
        }
    }

    public void guardarTodoEts() {

        try {
            if (getListaEts().getRowCount() > 0) {
                for (ReRequisicionEts reRequisicion : getListaEts()) {
                    if (servicioOcOrdenEts.crearOcOrdenEts(
                            ordenBean.getOrdenActual().getId(),
                            idCategoriaSelccionada,
                            reRequisicion.getSiAdjunto(),
                            usuarioBean.getUsuarioConectado())) {

                        servicioReRequisicion.ponerDisgregada(
                                reRequisicion,
                                usuarioBean.getUsuarioConectado(),
                                Constantes.BOOLEAN_TRUE
                        );

                        filasSeleccionadas.remove(reRequisicion.getId());
                    }
                }
                traerEspecificacionTecnica();
                ordenEtsPorCategoria();
                etsPorOrdenRequisicion();
                listaEts = null;
            } else {
                FacesUtilsBean.addInfoMessage("Seleccione al menos un ETS de la requisición");
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion en guardar todo ", e);
        }
    }

    /*
     * ********************** CARGA DE ETS DESDE REQUISICION******************
     */
    /**
     * @param uploadFile
     */
    public void uploadFile(FileUploadEvent uploadFile) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            fileInfo = uploadFile.getFile();
            setRequi(Boolean.TRUE);
            AlmacenDocumentos almacenDocumentos
                    = proveedorAlmacenDocumentos.getAlmacenDocumentos();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                if (isRequi()) {
                    documentoAnexo.setRuta(getUploadDirectoryRequi());
                } else {
                    documentoAnexo.setRuta(getUploadDirectoryOrden());
                }

                almacenDocumentos.guardarDocumento(documentoAnexo);

                SiAdjunto adj
                        = servicioSiAdjuntoImpl.save(
                                documentoAnexo.getNombreBase(),
                                new StringBuilder()
                                        .append(documentoAnexo.getRuta())
                                        .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                                fileInfo.getContentType(),
                                fileInfo.getSize(),
                                usuarioBean.getUsuarioConectado().getId()
                        );

                if (adj != null) {
                    if (isRequi()) {
                        servicioReRequisicion.crear(
                                requisicionBean.getRequisicionActual(),
                                adj,
                                usuarioBean.getUsuarioConectado()
                        );
                    } else {
                        servicioOcOrdenEts.crearOcOrdenEts(
                                ordenBean.getOrdenActual().getId(), getIdCategoriaSelccionada(),
                                adj,
                                usuarioBean.getUsuarioConectado()
                        );
                    }
                }
                //traerEspecificacionTecnica();
                ordenEtsPorCategoria();
                FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

        } catch (IOException e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        } catch (SIAException e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    /*
     * ********************** CARGA DE ETS DESDE REQUISICION******************
     */
    /**
     * @param uploadEvent
     */
    public void uploadFileEspera(FileUploadEvent uploadEvent) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            fileInfo = uploadEvent.getFile();

            AlmacenDocumentos almacenDocumentos
                    = proveedorAlmacenDocumentos.getAlmacenDocumentos();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta(getUploadDirectoryRequi());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                SiAdjunto adj
                        = servicioSiAdjuntoImpl.save(
                                documentoAnexo.getNombreBase(),
                                new StringBuilder()
                                        .append(documentoAnexo.getRuta())
                                        .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                                fileInfo.getContentType(),
                                "ESPERA",
                                fileInfo.getSize(),
                                usuarioBean.getUsuarioConectado().getId()
                        );

                if (adj != null) {
                    servicioReRequisicion.crear(
                            requisicionBean.getRequisicionActual(),
                            adj,
                            usuarioBean.getUsuarioConectado(),
                            false
                    );
                }
                requisicionSiMovimientoImpl.saveRequestMove(this.usuarioBean.getUsuarioConectado().getId(), "ADJUNTAR ARCHIVO " + documentoAnexo.getNombreBase(), requisicionBean.getRequisicionActual().getId(), Constantes.ID_SI_OPERACION_ESPERAADJ);
                FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoAdjuntoEsperaReq);");
                requisicionBean.enEsperaDet();
            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

        } catch (IOException e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        } catch (SIAException e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    public void etsPorOrdenCategoria() {
        if (getIdCategoriaSelccionada() == -1) {
            setListaOcOrdenEts(null);
        } else {
            try {
                setListaOcOrdenEts(
                        new ListDataModel(
                                servicioOcOrdenEts.traerEtsPorOrdenCategoria(
                                        ordenBean.getOrdenActual().getId(),
                                        getIdCategoriaSelccionada()
                                )
                        )
                );
            } catch (Exception ex) {
                setListaOcOrdenEts(null);
                LOGGER.fatal(this, " excepcion en getOrdenEts", ex);
            }
        }

    }

    public void traerTablaComparativa() {
        try {
            listaTablaComparativa = servicioOcOrdenEts.traerEtsPorOrdenCategoria(ordenBean.getOrdenActual().getId(), Constantes.OCS_CATEGORIA_TABLA);
        } catch (Exception ex) {
            listaTablaComparativa = null;
            LOGGER.fatal(this, " excepcion en getOrdenEts", ex);
        }
    }

    public void eliminarTablaComparativaOrden(int idRelacion) {
        try {
            LOGGER.info("rel : : : " + idRelacion);
            // Buscar en ReRequicision
            setEtsOcOrden(servicioOcOrdenEts.find(idRelacion));
            servicioOcOrdenEts.eliminarOcOrdenEts(getEtsOcOrden().getId(), usuarioBean.getUsuarioConectado().getId());
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion en eliminar ETS " + e.getMessage());
        }
    }

    /**
     * *************************FIN CARGAR ARCHIVOS DEL SISTEMA
     * ********************
     */
    /**
     * Metodos para ir a las paginas
     */
    public String irCargarEtsRequisicion() {
        setIdCategoriaSelccionada(-1);
        return "CargarEtsRequisicion";
    }

    public String irCargarEtsOc() {

        setIdCategoriaSelccionada(-1);
        return "CargarEtsOc";
    }

    /**
     * @return the etsActualAdjunto
     */
    public SiAdjunto getEtsActualAdjunto() {
        return etsActualAdjunto;
    }

    /**
     * @param etsActual the etsActualAdjunto to set
     */
    public void setEtsActualAdjunto(SiAdjunto etsActual) {
        this.etsActualAdjunto = etsActual;
    }

    /**
     * @return the clasificacion
     */
    public String getClasificacion() {
        return clasificacion;
    }

    /**
     * @param clasificacion the clasificacion to set
     */
    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    /**
     * @return the etsReRequisicion
     */
    public ReRequisicionEts getEtsReRequisicion() {
        return etsReRequisicion;
    }

    /**
     * @param etsReRequisicion the etsReRequisicion to set
     */
    public void setEtsReRequisicion(ReRequisicionEts etsReRequisicion) {
        this.etsReRequisicion = etsReRequisicion;
    }

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

    /**
     * @return the seleccionarTodo
     */
    public boolean isSeleccionarTodo() {
        return seleccionarTodo;
    }

    /**
     * @param seleccionarTodo the seleccionarTodo to set
     */
    public void setSeleccionarTodo(boolean seleccionarTodo) {
        this.seleccionarTodo = seleccionarTodo;
    }

    /**
     * @return the idCategoriaSelccionada
     */
    public int getIdCategoriaSelccionada() {
        return idCategoriaSelccionada;
    }

    /**
     * @param idCategoriaSelccionada the idCategoriaSelccionada to set
     */
    public void setIdCategoriaSelccionada(int idCategoriaSelccionada) {
        this.idCategoriaSelccionada = idCategoriaSelccionada;
    }

    /**
     * @return the filasSeleccionadas
     */
    public Map<Integer, Boolean> getFilasSeleccionadas() {
        return filasSeleccionadas;
    }

    /**
     * @param filasSeleccionadas the filasSeleccionadas to set
     */
    public void setFilasSeleccionadas(Map<Integer, Boolean> filasSeleccionadas) {
        this.filasSeleccionadas = filasSeleccionadas;
    }

    /**
     * @return the etsOcOrden
     */
    public OcOrdenEts getEtsOcOrden() {
        return etsOcOrden;
    }

    /**
     * @param etsOcOrden the etsOcOrden to set
     */
    public void setEtsOcOrden(OcOrdenEts etsOcOrden) {
        this.etsOcOrden = etsOcOrden;
    }

    /**
     * @return the idOrdenSeleccionada
     */
    public int getIdOrdenSeleccionada() {
        return idOrdenSeleccionada;
    }

    /**
     * @param idOrdenSeleccionada the idOrdenSeleccionada to set
     */
    public void setIdOrdenSeleccionada(int idOrdenSeleccionada) {
        this.idOrdenSeleccionada = idOrdenSeleccionada;
    }

    /**
     * @return the listaOcCategoriaEts
     */
    public DataModel getListaOcCategoriaEts() {
        return listaOcCategoriaEts;
    }

    /**
     * @param listaOcCategoriaEts the listaOcCategoriaEts to set
     */
    public void setListaOcCategoriaEts(DataModel listaOcCategoriaEts) {
        this.listaOcCategoriaEts = listaOcCategoriaEts;
    }

    /**
     * @return the listaOcOrdenEts
     */
    public DataModel getListaOcOrdenEts() {
        return listaOcOrdenEts;
    }

    /**
     * @param listaOcOrdenEts the listaOcOrdenEts to set
     */
    public void setListaOcOrdenEts(DataModel listaOcOrdenEts) {
        this.listaOcOrdenEts = listaOcOrdenEts;
    }

    /**
     * @return the paginaRegreso
     */
    public String getPaginaRegreso() {
        return paginaRegreso;
    }

    /**
     * @param paginaRegreso the paginaRegreso to set
     */
    public void setPaginaRegreso(String paginaRegreso) {
        this.paginaRegreso = paginaRegreso;
    }

    /**
     * @return the opcionSeleccionada
     */
    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(String opcionSeleccionada) {
        this.opcionSeleccionada = opcionSeleccionada;
    }

    /**
     * @return the preDirectorio
     */
    public String getPreDirectorio() {
        return preDirectorio;
    }

    /**
     * @param preDirectorio the preDirectorio to set
     */
    public void setPreDirectorio(String preDirectorio) {
        this.preDirectorio = preDirectorio;
    }

    /**
     * @return the requi
     */
    public boolean isRequi() {
        return requi;
    }

    /**
     * @param requi the requi to set
     */
    public void setRequi(boolean requi) {
        this.requi = requi;
    }

    /**
     * @return the listaEts
     */
    public DataModel<ReRequisicionEts> getListaEts() {
        return listaEts;
    }

    /**
     * @return the listaTablaComparativa
     */
    public List getListaTablaComparativa() {
        return listaTablaComparativa;
    }

    /**
     * @return the listaEtsEspera
     */
    public DataModel<ReRequisicionEts> getListaEtsEspera() {
        return listaEtsEspera;
    }

    /**
     * @param listaEtsEspera the listaEtsEspera to set
     */
    public void setListaEtsEspera(DataModel<ReRequisicionEts> listaEtsEspera) {
        this.listaEtsEspera = listaEtsEspera;
    }
}
