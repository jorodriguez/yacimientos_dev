/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.gr.sistema.soporte;

import com.google.common.base.Strings;
import org.primefaces.PrimeFaces;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SiAdjunto;
import sia.modelo.gr.vo.GrArchivoVO;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.gr.vo.GrRutaZonasVO;
import sia.modelo.gr.vo.GrSitioVO;
import sia.modelo.gr.vo.MapaVO;
import sia.modelo.sgl.semaforo.vo.SgEstadoSemaforoVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.servicios.gr.impl.GrArchivoImpl;
import sia.servicios.gr.impl.GrMapaImpl;
import sia.servicios.gr.impl.GrPuntoImpl;
import sia.servicios.gr.impl.GrRutasZonasImpl;
import sia.servicios.gr.impl.GrSitioImpl;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.semaforo.impl.SgSemaforoImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author ihsa
 */
@Named(value = "popupGrBean")
@ViewScoped
public class PopUpGRBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "popupGrBean";
    //------------------------------------------------------

    @Inject
    private GrArchivoImpl grArchivoImpl;
    @Inject
    private GrMapaImpl grMapaImpl;
    @Inject
    private GrSitioImpl grSitioImpl;
    @Inject
    private SgSemaforoImpl sgSemaforoImpl;
    @Inject
    private SiParametroImpl parametrosSistema;
    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @Inject
    private GrRutasZonasImpl grRutasZonasImpl;
    @Inject
    private GrPuntoImpl grPuntoImpl;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoImpl;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private SoporteListas soporteListas = (SoporteListas) FacesUtilsBean.getManagedBean("soporteListas");
    private Sesion sesionBean = (Sesion) FacesUtilsBean.getManagedBean("sesion");
    private ConfiguracionBean confBean = (ConfiguracionBean) FacesUtilsBean.getManagedBean("configuracionBean");
    private List<SelectItem> lstMapas;
    private List<SelectItem> lstSemaforo;
    private String tituloPopUp = "Registrar Mapa";
    private String uploadDirectory = "";
    //private String uploadDirectoryMapa = "";    
    private String directorioArchivos = "GR/Mapas";
    private GrSitioVO sitio;
    private GrArchivoVO imagen;
    private GrArchivoVO archivo;
    private String msgCentops;
    private MapaVO zona;
    private GrPuntoVO punto;
    private RutaTerrestreVo ruta;
    private List<RutaTerrestreVo> lstRutasPorZonas;
    private List<GrRutaZonasVO> rutaZonas;
    private int rutaZonaID;
    private List<SelectItem> lstZonas;
    private GrRutaZonasVO rutaZonaVO;
    private int puntoID;
    private boolean allItems = false;
    private boolean visible = true;
    private SgEstadoSemaforoVO estadoSemaforoVO = new SgEstadoSemaforoVO();
    private List<String> listaZonasRuta = new ArrayList<>();
    private RutaTerrestreVo editHorario;
    private int horaMin;
    private int horaMax;
    private int minutoMin;
    private int minutoMax;
    private List<SelectItem> listaHorasMin = new ArrayList<>();
    private List<SelectItem> listaMinutosMin = new ArrayList<>();
    private List<SelectItem> listaHorasMax = new ArrayList<>();
    private List<SelectItem> listaMinutosMax = new ArrayList<>();
    private boolean disableText = true;

    public PopUpGRBean() {

    }

    /**
     * @return the lstMapas
     */
    public List<SelectItem> getLstMapas() {
        return grMapaImpl.getMapasItems(this.isAllItems(), this.isVisible());
    }

    public List<SelectItem> getLstMapasZonas() {
        return grMapaImpl.getMapasItems(true, this.isVisible());
    }

    /**
     * @param lstMapas the lstMapas to set
     */
    public void setLstMapas(List<SelectItem> lstMapas) {
        this.lstMapas = lstMapas;
    }

    /**
     * @return the lstSemaforo
     */
    public List<SelectItem> getLstSemaforo() {
        return sgSemaforoImpl.traerSemaforoItems();
    }

    /**
     * @param lstSemaforo the lstSemaforo to set
     */
    public void setLstSemaforo(List<SelectItem> lstSemaforo) {
        this.lstSemaforo = lstSemaforo;
    }

    public String getUploadDirectory() {
        if (this.uploadDirectory.isEmpty()) {
            this.uploadDirectory = this.parametrosSistema.find(1).getUploadDirectory();
        }
        return new StringBuilder().append(uploadDirectory).append(getDirectorioArchivos()).toString();
    }

    public void guardarSitio() {
        try {
            if (this.getSitio() != null
                    && this.getSitio().getNombre() != null && !this.getSitio().getNombre().isEmpty()
                    && this.getSitio().getDescripcion() != null && !this.getSitio().getDescripcion().isEmpty()
                    && this.getSitio().getLiga() != null && !this.getSitio().getLiga().isEmpty()) {
                grSitioImpl.crearSitio(sitio, this.sesionBean.getUsuarioVO().getId());
                confBean.cargarSitiosDt();
                FacesUtilsBean.addInfoMessage("Se registró el sitio recomendado correctamente. ");
                String jsFuncion = ";cerrarDialogoSitiosRecomendados();";
                PrimeFaces.current().executeScript(jsFuncion);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarZona(ActionEvent actionEvent) {
        try {
            if (this.getZona() != null
                    && this.getZona().getNombre() != null && !this.getZona().getNombre().isEmpty()
                    && this.getZona().getDescripcion() != null && !this.getZona().getDescripcion().isEmpty()) {
                grMapaImpl.crearZona(this.getZona(), this.sesionBean.getUsuarioVO().getId());
                confBean.cargarZonasDt();
                FacesUtilsBean.addInfoMessage("Se registró la zona correctamente. ");
                String jsFuncion = ";cerrarDialogoZonas();";
                PrimeFaces.current().executeScript(jsFuncion);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarSemaforo() {
        try {
            if (this.getEstadoSemaforoVO() != null
                    && this.getEstadoSemaforoVO().getGrMapaID() > 0 && this.getEstadoSemaforoVO().getSemaforoID() > 0) {
                if (sgEstadoSemaforoImpl.crearEstadoSemaforoZona(this.getEstadoSemaforoVO(), this.sesionBean.getUsuarioVO().getId()) != null) {
                    confBean.cargarSemaforosDt();
                    FacesUtilsBean.addInfoMessage("Se registró el semáforo correctamente. ");
                    String jsFuncion = ";cerrarDialogoSemaforos();";
                    PrimeFaces.current().executeScript(jsFuncion);
                } else {
                    FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarPunto() {
        try {
            if (this.getPunto() != null
                    && this.getPunto().getNombre() != null && !this.getPunto().getNombre().isEmpty()
                    && this.getPunto().getDescripcion() != null && !this.getPunto().getDescripcion().isEmpty()) {
                grPuntoImpl.crearPunto(this.getPunto(), this.sesionBean.getUsuarioVO().getId());
                confBean.cargarPuntosDt();
                FacesUtilsBean.addInfoMessage("Se registró el punto de seguridad correctamente. ");
                String jsFuncion = ";cerrarDialogoPuntos();";
                PrimeFaces.current().executeScript(jsFuncion);
            }
        } catch (Exception e) {
            if ("hasRefZonas".equals(e.getMessage())) {
                FacesUtilsBean.addErrorMessage("No se puede inactivar el punto seguro ya que tiene referencias activas.");
            } else {
                UtilLog4j.log.fatal(this, e);
                FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
            }

        }
    }

    public void guardarEditZonaRuta(ActionEvent actionEvent) {
        try {
            if (this.getRutaZonaVO() != null && this.getRutaZonaVO().getId() > 0) {
                grRutasZonasImpl.guardarRutaZona(this.getRutaZonaVO(), this.sesionBean.getUsuarioVO().getId());
                FacesUtilsBean.addInfoMessage("Se registró la zona correctamente. ");
                String jsFuncion = ";cerrarDialogoEditRutaZonas();";
                PrimeFaces.current().executeScript(jsFuncion);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarZonaRuta() {
        try {
            if (this.getRutaZonaID() > 0) {
                grRutasZonasImpl.crearRutaZona(getRuta().getId(), getRutaZonaID(), true, true, this.sesionBean.getUsuarioVO().getId(), this.getPuntoID(), "0");
                FacesUtilsBean.addInfoMessage("Se registró la zona correctamente. ");
                this.setRutaZonas(grRutasZonasImpl.zonasPorRuta(getRuta(), false));
                this.setRutaZonaVO(null);
                String jsFuncion = ";cerrarDialogoRutas();";
                PrimeFaces.current().executeScript(jsFuncion);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarZonaRutaDet() {
        try {
            grRutasZonasImpl.guardarRutaZona(getRutaZonaVO(), this.sesionBean.getUsuarioVO().getId());
            String jsFuncion = ";editZonaGuardar();";
            PrimeFaces.current().executeScript(jsFuncion);
            FacesUtilsBean.addInfoMessage("Se registró la zona correctamente. ");
            this.setRutaZonas(grRutasZonasImpl.zonasPorRuta(getRuta(), false));
            this.setRutaZonaVO(null);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarZonaHorarioDet() {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(getEditHorario().getHoraMinimaRuta());
            c.set(Calendar.HOUR_OF_DAY, getHoraMin());
            c.set(Calendar.MINUTE, getMinutoMin());

            Calendar c1 = Calendar.getInstance();
            c1.setTime(getEditHorario().getHoraMaximaRuta());
            c1.set(Calendar.HOUR_OF_DAY, getHoraMax());
            c1.set(Calendar.MINUTE, getMinutoMax());

            getEditHorario().setHoraMinimaRuta(c.getTime());
            getEditHorario().setHoraMaximaRuta(c1.getTime());

            sgRutaTerrestreImpl.modificarRutaTerrestre(this.sesionBean.getUsuario(), getEditHorario(), Constantes.BOOLEAN_FALSE);

            setEditHorario(null);
            confBean.cargarRutasDt();
            String jsFuncion = ";cerrarDialogoEditRutaCodigosHorarioss();";
            PrimeFaces.current().executeScript(jsFuncion);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void uploadFile(FileUploadEvent fileEvent) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {

            UploadedFile fileInfo = fileEvent.getFile();

            if (this.getArchivo().getGrTipoArchivo() > 0
                    && ((Constantes.GR_TIPO_ARCHIVO_Mapas == this.getArchivo().getGrTipoArchivo()
                    && this.getArchivo().getSgSemaforo() > 0
                    && this.getArchivo().getGrMapa() > 0)
                    || (Constantes.GR_TIPO_ARCHIVO_Mapas < this.getArchivo().getGrTipoArchivo()
                    && this.getArchivo().getSgSemaforo() == 0
                    && this.getArchivo().getGrMapa() == 0))) {

                boolean addArdchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

                String extension = FilenameUtils.getExtension(fileInfo.getFileName());

                boolean extensionValida = false;

                if (extension.equalsIgnoreCase("png")
                        || extension.equalsIgnoreCase("jpeg")
                        || extension.equalsIgnoreCase("jpg")) {
                    extensionValida = true;
                }

                if (addArdchivo && extensionValida) {

                    DocumentoAnexo documentoAnexo = almacenarAnexo(fileInfo);
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    documentoAnexo.setTipoMime(fileInfo.getContentType());
                    documentoAnexo.setRuta(getDirectorioArchivos());

                    SiAdjunto adj
                            = servicioSiAdjuntoImpl.save(
                                    documentoAnexo.getNombreBase(),
                                    new StringBuilder()
                                            .append(getDirectorioArchivos())
                                            .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                                    fileInfo.getContentType(),
                                    fileInfo.getSize(),
                                    this.sesionBean.getUsuarioVO().getId(),
                                    documentoAnexo.getUuid()
                            );

                    if (adj != null) {
                        grArchivoImpl.crear(
                                adj,
                                getArchivo().getSgSemaforo(),
                                getArchivo().getGrTipoArchivo(),
                                getArchivo().getGrMapa(),
                                sesionBean.getUsuarioVO().getId(),
                                getArchivo().getTitulo()
                        );

                        String jsFuncion = ";cerrarDialogoRegMapa();";
                        String msj = null;

                        switch (getArchivo().getGrTipoArchivo()) {
                            case Constantes.GR_TIPO_ARCHIVO_Mapas:
                                confBean.cargarMapasDt();
                                jsFuncion = ";cerrarDialogoPopUpFE();";
                                msj = "Se registró el mapa correctamente. ";
                                break;
                            case Constantes.GR_TIPO_ARCHIVO_Recomendaciones:
                                confBean.cargarRecomendacionesDt();
                                jsFuncion = ";cerrarDialogoPopUpFE();";
                                msj = "Se registró la recomendación correctamente. ";
                                break;
                            case Constantes.GR_TIPO_ARCHIVO_Sitios:
                                confBean.cargarSitiosDt();
                                jsFuncion = ";cerrarDialogoSitiosRecomendados();";
                                msj = "Se registró el sitio correctamente. ";
                                break;
                            case Constantes.GR_TIPO_ARCHIVO_Situacion:
                                confBean.cargarSituacionesDt();
                                jsFuncion = ";cerrarDialogoPopUpFE();";
                                msj = "Se registró la situación de riesgo correctamente. ";
                                break;

                            default:
                                msj = "Desconocido";
                                break;
                        }

                        FacesUtilsBean.addInfoMessage(msj);
                        PrimeFaces.current().executeScript(jsFuncion);
                    }
                } else {
                    if (!addArdchivo) {
                        FacesUtilsBean.addInfoMessage(
                                "No se permiten los siguientes caracteres especiales en el nombre del Archivo: "
                                + validadorNombreArchivo.getCaracteresNoValidos() + " "
                        );
                    }

                    if (addArdchivo && !extensionValida) {
                        FacesUtilsBean.addInfoMessage(
                                "Sólo se permiten archivos tipo imagen: png, jpeg y jpg"
                        );
                    }
                }

                fileInfo.delete();

            }
        } catch (SIAException siae) {
            UtilLog4j.log.fatal(this, siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        } catch (IOException e) {
            UtilLog4j.log.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void editFile() {
        try {
            grArchivoImpl.editarArchivo(this.getArchivo());

            String jsFuncion = ";cerrarDialogoPopUpFEedit();";
            switch (this.getArchivo().getGrTipoArchivo()) {
                case 1:
                    jsFuncion = ";cerrarDialogoPopUpFEedit();";
                    confBean.cargarMapasDt();
                    FacesUtilsBean.addInfoMessage("Se registró el mapa correctamente. ");
                    break;
                case 2:
                    jsFuncion = ";cerrarDialogoPopUpFEedit();";
                    confBean.cargarRecomendacionesDt();
                    FacesUtilsBean.addInfoMessage("Se registró la recomendación correctamente. ");
                    break;
                case 3:
                    jsFuncion = ";cerrarDialogoSitiosRecomendados();";
                    confBean.cargarSitiosDt();
                    FacesUtilsBean.addInfoMessage("Se registró el sitio correctamente. ");
                    break;
                case 4:
                    jsFuncion = ";cerrarDialogoPopUpFEedit();";
                    confBean.cargarSituacionesDt();
                    FacesUtilsBean.addInfoMessage("Se registró la situación de riesgo correctamente. ");
                    break;
            }
            PrimeFaces.current().executeScript(jsFuncion);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void uploadFileMsg(FileUploadEvent fileEvent) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            if (!Strings.isNullOrEmpty(getMsgCentops())) {

                UploadedFile fileInfo = fileEvent.getFile();

                boolean addArdchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
                if (addArdchivo) {

                    DocumentoAnexo documentoAnexo = almacenarAnexo(fileInfo);

                    SiAdjunto adj
                            = servicioSiAdjuntoImpl.save(
                                    documentoAnexo.getNombreBase(),
                                    new StringBuilder()
                                            .append(getDirectorioArchivos())
                                            .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                                    fileInfo.getContentType(),
                                    fileInfo.getSize(),
                                    this.sesionBean.getUsuarioVO().getId(),
                                    documentoAnexo.getUuid()
                            );

                    if (!Strings.isNullOrEmpty(getMsgCentops())
                            && enviarMsg(getMsgCentops())) {
                        setMsgCentops(Constantes.VACIO);
                        FacesUtilsBean.addInfoMessage("El mensaje a CENTOPS ha sido enviado correctamente.");
                    } else {
                        FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
                    }
                } else {
                    FacesUtilsBean.addInfoMessage(
                            "No se permiten los siguientes caracteres especiales en el nombre del Archivo: "
                            + validadorNombreArchivo.getCaracteresNoValidos() + " "
                    );
                }

                fileInfo.delete();

            }
        } catch (SIAException siae) {
            UtilLog4j.log.fatal(this, siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        } catch (IOException e) {
            UtilLog4j.log.fatal(this, "+ + + ERROR + + +" + e.getMessage());
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    private DocumentoAnexo almacenarAnexo(UploadedFile fileInfo)
            throws IOException, SIAException {

        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
        documentoAnexo.setRuta(getDirectorioArchivos());
        documentoAnexo.setTipoMime(fileInfo.getContentType());
        documentoAnexo.setNombreBase(fileInfo.getFileName());
        almacenDocumentos.guardarDocumento(documentoAnexo);

        return documentoAnexo;
    }

    public void enviarMsgCentops() {
        try {
            if (this.getMsgCentops() != null && !this.getMsgCentops().isEmpty()) {
                StringBuilder msg = new StringBuilder();
                msg.append("<table align=\"center\" width=\"95%\"><tbody><tr><td><table width=\"100%\"><tbody><tr>");
                msg.append("<td style=\"background-color:#A8CEF0\"><font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:left;\"><b>QUIÉN, QUÉ, DÓNDE, CUÁNDO Y CÓMO</b></font></td>");
                msg.append("</tr><tr><td style=\"text-align:left\"><font size=\"-1\" face=\"arial\"><b>");
                msg.append(this.getMsgCentops());
                msg.append("</b></font></td></tr></tbody></table>");
                if (this.enviarMsg(msg.toString(), null)) {
                    this.setMsgCentops("");
                    FacesUtilsBean.addInfoMessage("El mensaje a CENTOPS ha sido enviado correctamente.");
                } else {
                    FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
                }
            } else {
                FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    private boolean enviarMsg(String msgCentops, File archivo) {
        boolean ret = false;
        try {
            if (msgCentops != null && !msgCentops.isEmpty()) {
                byte[] fileContentEsr = null;
                byte[] fileContentGR = null;
                StringBuilder msg = new StringBuilder();
                msg.append("<table align=\"center\" width=\"95%\"><tbody><tr><td><table width=\"100%\"><tbody><tr>");
                msg.append("<td style=\"background-color:#A8CEF0\"><font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:left;\"><b>QUIÉN, QUÉ, DÓNDE, CUÁNDO Y CÓMO</b></font></td>");
                msg.append("</tr><tr><td style=\"text-align:left\"><font size=\"-1\" face=\"arial\"><b>");
                msg.append(this.getMsgCentops());
                msg.append("</b></font></td></tr></tbody></table>");
                if (grArchivoImpl.enviarCentops(msg.toString(), fileContentGR, fileContentEsr, archivo)) {
                    ret = true;
                    setMsgCentops("");
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
        return ret;
    }

    private boolean enviarMsg(String msgCentops) {
        boolean ret = false;
        try {
            if (msgCentops != null && !msgCentops.isEmpty()) {
                byte[] fileContentEsr = null;
                byte[] fileContentGR = null;
                StringBuilder msg = new StringBuilder();
                msg.append("<table align=\"center\" width=\"95%\"><tbody><tr><td><table width=\"100%\"><tbody><tr>");
                msg.append("<td style=\"background-color:#A8CEF0\"><font color=\"black\" face=\"font-family: Gill, Helvetica, sans-serif; font-size:11px;text-align:left;\"><b>QUIÉN, QUÉ, DÓNDE, CUÁNDO Y CÓMO</b></font></td>");
                msg.append("</tr><tr><td style=\"text-align:left\"><font size=\"-1\" face=\"arial\"><b>");
                msg.append(this.getMsgCentops());
                msg.append("</b></font></td></tr></tbody></table>");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
        return ret;
    }

    public void goPopupMapas(ActionEvent actionEvent) {
        try {
            GrArchivoVO newMapa = new GrArchivoVO();
            newMapa.setGrTipoArchivo(Constantes.GR_TIPO_ARCHIVO_Mapas);
            newMapa.setGrMapa(0);
            newMapa.setSgSemaforo(0);
            newMapa.setTitulo(null);

            setArchivo(newMapa);
            setDirectorioArchivos("GR/Mapas");
            setTituloPopUp("Registrar Mapa");
            PrimeFaces.current().executeScript(";abrirDialogoPopUpFE();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditMapa(int idArchivo) {
        try {
            setArchivo(grArchivoImpl.getArchivoById(idArchivo));
            getArchivo().setTitulo(null);
            setDirectorioArchivos("GR/Mapas");
            setTituloPopUp("Editar Mapa");
            PrimeFaces.current().executeScript(";abrirDialogoPopUpFEedit();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupImagen(int idArchivo) {
        try {
            //int idArchivo = Integer.parseInt(FacesUtilsBean.getRequestParameter("idArchivo"));
            if (idArchivo > 0) {
                setImagen(grArchivoImpl.getArchivoById(idArchivo));
                PrimeFaces.current().executeScript(";abrirDialogoImagen();");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupImagenMapa(ActionEvent actionEvent) {
        try {
            int tipo = Integer.parseInt(FacesUtilsBean.getRequestParameter("tipoArchivo"));
            setImagen(grArchivoImpl.getArchivo(tipo));
            PrimeFaces.current().executeScript(";abrirDialogoImagenMapa();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupRecomendaciones() {
        try {
            GrArchivoVO newRec = new GrArchivoVO();
            newRec.setGrTipoArchivo(Constantes.GR_TIPO_ARCHIVO_Recomendaciones);
            newRec.setSgSemaforo(0);
            newRec.setGrMapa(0);
            newRec.setTitulo("");

            setArchivo(newRec);
            setDirectorioArchivos("GR/Recomendacion");
            setTituloPopUp("Registrar Recomendaciones de Seguridad");
            PrimeFaces.current().executeScript(";abrirDialogoPopUpFE();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditRecomendaciones(int idArchivo) {
        try {
            setArchivo(grArchivoImpl.getArchivoById(idArchivo));
            setDirectorioArchivos("GR/Recomendacion");
            setTituloPopUp("Editar recomendación");
            PrimeFaces.current().executeScript(";abrirDialogoPopUpFEedit();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupSitiosRecomendados() {
        try {
            GrArchivoVO newArc = new GrArchivoVO();
            newArc.setGrTipoArchivo(Constantes.GR_TIPO_ARCHIVO_Sitios);
            newArc.setSgSemaforo(0);
            newArc.setGrMapa(0);
            newArc.setTitulo(null);

            setArchivo(newArc);
            setSitio(new GrSitioVO());
            setDirectorioArchivos("GR/Sitios");
            setTituloPopUp("Registrar Sitios Recomendados");
            PrimeFaces.current().executeScript(";abrirDialogoSitiosRecomendados();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditSitiosRecomendados(int idArchivo) {
        try {
            setSitio(grSitioImpl.getSitio(idArchivo));
            setArchivo(new GrArchivoVO());
            getArchivo().setGrTipoArchivo(Constantes.GR_TIPO_ARCHIVO_Sitios);
            getArchivo().setSgSemaforo(0);
            getArchivo().setGrMapa(0);
            getArchivo().setTitulo(null);
            setDirectorioArchivos("GR/Sitios");
            setTituloPopUp("Editar Sitios Recomendados");
            PrimeFaces.current().executeScript(";abrirDialogoSitiosRecomendados();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupSituacionRiesgo() {
        try {
            GrArchivoVO newArc = new GrArchivoVO();
            setArchivo(newArc);
            getArchivo().setGrTipoArchivo(Constantes.GR_TIPO_ARCHIVO_Situacion);
            getArchivo().setSgSemaforo(0);
            getArchivo().setGrMapa(0);
            getArchivo().setTitulo("");
            setDirectorioArchivos("GR/Situacion");
            setTituloPopUp("Registrar Situaciones de Riesgo");
            PrimeFaces.current().executeScript(";abrirDialogoPopUpFE();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditSituacionRiesgo(int idArchivo) {
        try {
            setArchivo(grArchivoImpl.getArchivoById(idArchivo));
            setDirectorioArchivos("GR/Situacion");
            setTituloPopUp("Editar Situaciones de Riesgo");
            PrimeFaces.current().executeScript(";abrirDialogoPopUpFEedit();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupPunto() {
        try {
            GrPuntoVO newPunto = new GrPuntoVO();
            setPunto(newPunto);
            setDirectorioArchivos("GR/Punto");
            setTituloPopUp("Registrar punto seguro");
            PrimeFaces.current().executeScript(";abrirDialogoPuntos();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupZonas() {
        try {
            MapaVO newZona = new MapaVO();
            setZona(newZona);
            setArchivo(new GrArchivoVO());
            getArchivo().setGrTipoArchivo(Constantes.GR_TIPO_ARCHIVO_Situacion);
            getArchivo().setSgSemaforo(0);
            getArchivo().setGrMapa(0);
            getArchivo().setTitulo("");
            setDirectorioArchivos("GR/Zona");
            setTituloPopUp("Registrar zona/sector");
            PrimeFaces.current().executeScript(";abrirDialogoZonas();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupSemaforo() {
        try {
            setZona(new MapaVO());
            setEstadoSemaforoVO(new SgEstadoSemaforoVO());
            setLstRutasPorZonas(new ArrayList<RutaTerrestreVo>());
            setDirectorioArchivos("GR/Semaforo");
            setTituloPopUp("Registrar semáforo");
            PrimeFaces.current().executeScript(";abrirDialogoSemaforos();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditZona(int idArchivo) {
        try {
            setZona(grMapaImpl.getMapa(idArchivo));
            setDirectorioArchivos("GR/Zonas");
            setTituloPopUp("Editar zona/sector");
            PrimeFaces.current().executeScript(";abrirDialogoZonas();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditPunto(int idArchivo) {
        try {
            setPunto(grPuntoImpl.getPunto(idArchivo));
            setDirectorioArchivos("GR/Puntos");
            setTituloPopUp("Editar punto de seguridad");
            PrimeFaces.current().executeScript(";abrirDialogoPuntos();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditZonaRuta(int idArchivo) {
        try {
            setRutaZonaVO(grRutasZonasImpl.zonaRutaPorID(idArchivo, false));
            getRutaZonaVO().setRuta(getRuta());
            setDirectorioArchivos("GR/Zonas");
            setTituloPopUp(new StringBuilder().append("Editar zona/sector de la ruta ").append(getRuta().getNombre()).toString());
            PrimeFaces.current().executeScript(";editZona();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditHorasRuta(int idRuta) {
        try {
            setEditHorario(sgRutaTerrestreImpl.traerRutaTerrestrePorID(idRuta));
            setListaHorasMin(itemsHoras());
            setListaMinutosMin(itemsMinutos());
            setListaHorasMax(itemsHoras());
            setListaMinutosMax(itemsMinutos());

            Calendar c = Calendar.getInstance();
            c.setTime(getEditHorario().getHoraMinimaRuta());
            setHoraMin(c.get(Calendar.HOUR_OF_DAY));
            setMinutoMin(c.get(Calendar.MINUTE));

            Calendar c1 = Calendar.getInstance();
            c1.setTime(getEditHorario().getHoraMaximaRuta());
            setHoraMax(c1.get(Calendar.HOUR_OF_DAY));
            setMinutoMax(c1.get(Calendar.MINUTE));

            this.setTituloPopUp("Editar horarios de la ruta");
            PrimeFaces.current().executeScript(";abrirDialogoEditRutaCodigosHorarios();"
            );
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void backEditZonaRuta(ActionEvent actionEvent) {
        try {
            this.setRutaZonaVO(null);
            PrimeFaces.current().executeScript(";editZonaGuardar();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void backEditRutaHorarios(ActionEvent actionEvent) {
        try {
            this.setEditHorario(null);
            PrimeFaces.current().executeScript(";editZonaHorasGuardar();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goEditRuta(int idArchivo) {
        try {
            setRuta(sgRutaTerrestreImpl.traerRutaTerrestrePorID(idArchivo));
            setRutaZonas(grRutasZonasImpl.zonasPorRuta(getRuta(), false));
            setRutaZonaVO(null);
            setDirectorioArchivos("GR/Rutas");

            setTituloPopUp(
                    new StringBuilder().append("Zonas/Sectores de la ruta ")
                            .append(getRuta().getNombre()).toString()
            );

            PrimeFaces.current().executeScript(";abrirDialogoRutas();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    /**
     * @return the directorioArchivos
     */
    public String getDirectorioArchivos() {
        return directorioArchivos;
    }

    /**
     * @param directorioArchivos the directorioArchivos to set
     */
    public void setDirectorioArchivos(String directorioArchivos) {
        this.directorioArchivos = directorioArchivos;
    }

    /**
     * @return the sitio
     */
    public GrSitioVO getSitio() {
        return sitio;
    }

    /**
     * @param sitio the sitio to set
     */
    public void setSitio(GrSitioVO sitio) {
        this.sitio = sitio;
    }

    /**
     * @return the imagen
     */
    public GrArchivoVO getImagen() {
        return imagen;
    }

    /**
     * @param imagen the imagen to set
     */
    public void setImagen(GrArchivoVO imagen) {
        this.imagen = imagen;
    }

    /**
     * @return the tituloPopUp
     */
    public String getTituloPopUp() {
        return tituloPopUp;
    }

    /**
     * @param tituloPopUp the tituloPopUp to set
     */
    public void setTituloPopUp(String tituloPopUp) {
        this.tituloPopUp = tituloPopUp;
    }

    /**
     * @return the msgCentops
     */
    public String getMsgCentops() {
        return msgCentops;
    }

    /**
     * @param msgCentops the msgCentops to set
     */
    public void setMsgCentops(String msgCentops) {
        this.msgCentops = msgCentops;
    }

    public String getUrlImagen() {
        StringBuilder url = new StringBuilder();
        if (getImagen() != null
                && getImagen().getSiAdjuntoVO() != null
                && getImagen().getSiAdjuntoVO().getId() > 0
                && getImagen().getSiAdjuntoVO().getUuid() != null
                && !getImagen().getSiAdjuntoVO().getUuid().isEmpty()) {

            url.append("/GR/AbrirArchivo?ZWZ2W=")
                    .append(getImagen().getSiAdjuntoVO().getId())
                    .append("&ZWZ3W=")
                    .append(getImagen().getSiAdjuntoVO().getUuid());

        } else {
            url.append(" ");
        }
        return url.toString();
    }

    /**
     * @return the archivo
     */
    public GrArchivoVO getArchivo() {
        return archivo;
    }

    /**
     * @param archivo the mapa to set
     */
    public void setArchivo(GrArchivoVO archivo) {
        this.archivo = archivo;
    }

    /**
     * @return the zona
     */
    public MapaVO getZona() {
        return zona;
    }

    /**
     * @param zona the mapa to set
     */
    public void setZona(MapaVO zona) {
        this.zona = zona;
    }

    /**
     * @return the ruta
     */
    public RutaTerrestreVo getRuta() {
        return ruta;
    }

    /**
     * @param ruta the ruta to set
     */
    public void setRuta(RutaTerrestreVo ruta) {
        this.ruta = ruta;
    }

    /**
     * @return the rutaZonaID
     */
    public int getRutaZonaID() {
        return rutaZonaID;
    }

    /**
     * @param rutaZonaID the rutaZonaID to set
     */
    public void setRutaZonaID(int rutaZonaID) {
        this.rutaZonaID = rutaZonaID;
    }

    /**
     * @return the lstZonas
     */
    public List<SelectItem> getLstZonas() {
        List<SelectItem> lst = null;
        if (getRuta() != null && getRuta().getId() > 0) {
            lst = grMapaImpl.getZonasRutaItems(getRuta().getId());
        }
        return lst;
    }

    /**
     * @return the lstZonas
     */
    public List<SelectItem> getLstPuntos() {
        return grPuntoImpl.getPuntosItems();
    }

    /*
     * @param lstZonas the lstZonas to set
     */
    public void setLstZonas(List<SelectItem> lstZonas) {
        this.lstZonas = lstZonas;
    }

    /**
     * @return the rutaZonaVO
     */
    public GrRutaZonasVO getRutaZonaVO() {
        return rutaZonaVO;
    }

    /**
     * @param rutaZonaVO the rutaZonaVO to set
     */
    public void setRutaZonaVO(GrRutaZonasVO rutaZonaVO) {
        this.rutaZonaVO = rutaZonaVO;
    }

    /**
     * @return the rutaZonas
     */
    public List<GrRutaZonasVO> getRutaZonas() {
        return rutaZonas;
    }

    /**
     * @param rutaZonas the rutaZonas to set
     */
    public void setRutaZonas(List<GrRutaZonasVO> rutaZonas) {
        this.rutaZonas = rutaZonas;
    }

    /**
     * @return the punto
     */
    public GrPuntoVO getPunto() {
        return punto;
    }

    /**
     * @param punto the punto to set
     */
    public void setPunto(GrPuntoVO punto) {
        this.punto = punto;
    }

    /**
     * @return the puntoID
     */
    public int getPuntoID() {
        return puntoID;
    }

    /**
     * @param puntoID the puntoID to set
     */
    public void setPuntoID(int puntoID) {
        this.puntoID = puntoID;
    }

    public void cambiarMapa() {
        try {
            getArchivo().setSgSemaforoVO(sgEstadoSemaforoImpl.getSemaforoZona(archivo.getGrMapa()));
            getArchivo().setSgSemaforo(getArchivo().getSgSemaforoVO().getIdSemaforo());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    /**
     * @return the allItems
     */
    public boolean isAllItems() {
        return allItems;
    }

    /**
     * @param allItems the allItems to set
     */
    public void setAllItems(boolean allItems) {
        this.allItems = allItems;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the estadoSemaforoVO
     */
    public SgEstadoSemaforoVO getEstadoSemaforoVO() {
        return estadoSemaforoVO;
    }

    /**
     * @param estadoSemaforoVO the estadoSemaforoVO to set
     */
    public void setEstadoSemaforoVO(SgEstadoSemaforoVO estadoSemaforoVO) {
        this.estadoSemaforoVO = estadoSemaforoVO;
    }

    /**
     * @return the lstRutasPorZonas
     */
    public List<RutaTerrestreVo> getLstRutasPorZonas() {
        return lstRutasPorZonas;
    }

    /**
     * @param lstRutasPorZonas the lstRutasPorZonas to set
     */
    public void setLstRutasPorZonas(List<RutaTerrestreVo> lstRutasPorZonas) {
        this.lstRutasPorZonas = lstRutasPorZonas;
    }

    /**
     * @return the listaZonasRuta
     */
    public List<String> getListaZonasRuta() {
        return listaZonasRuta;
    }

    /**
     * @param listaZonasRuta the listaZonasRuta to set
     */
    public void setListaZonasRuta(List<String> listaZonasRuta) {
        this.listaZonasRuta = listaZonasRuta;
    }

    public List<String> zonasRutaListener(String cadena) {
      return traerZonasRuta(cadena);
    }

    public void cambiarZonaRuta() {
        try {
            int aux = 2;
            String codigo = getEstadoSemaforoVO().getGrMapaIDtxt().substring(
                    (getEstadoSemaforoVO().getGrMapaIDtxt().lastIndexOf("//") + aux));
            List<MapaVO> rutaZs = soporteListas.getZonasbyCodigo(codigo);
            if (rutaZs != null && rutaZs.size() > 0) {
                getEstadoSemaforoVO().setGrMapaID(rutaZs.get(0).getId());
                setLstRutasPorZonas(
                        sgRutaTerrestreImpl.traerRutaTerrestrePorZona(
                                rutaZs.get(0).getId(),
                                getEstadoSemaforoVO().getSemaforoID()
                        )
                );
            }

         //   PrimeFaces.current().executeScript(";$(dialogoSemaforos).modal('hide');");

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    private List<String> traerZonasRuta(String cadena) {
        List<String> list;
        try {
            if (cadena != null && !cadena.isEmpty() && cadena.length() > 2) {
                list = soporteListas.regresaZonas(cadena);
            } else {
                list = new ArrayList<>();
            }
        } catch (Exception e) {
            UtilLog4j.log.warn(this, e);
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * @return the editHorario
     */
    public RutaTerrestreVo getEditHorario() {
        return editHorario;
    }

    /**
     * @param editHorario the editHorario to set
     */
    public void setEditHorario(RutaTerrestreVo editHorario) {
        this.editHorario = editHorario;
    }

    /**
     * @return the horaMin
     */
    public int getHoraMin() {
        return horaMin;
    }

    /**
     * @param horaMin the horaMin to set
     */
    public void setHoraMin(int horaMin) {
        this.horaMin = horaMin;
    }

    /**
     * @return the horaMax
     */
    public int getHoraMax() {
        return horaMax;
    }

    /**
     * @param horaMax the horaMax to set
     */
    public void setHoraMax(int horaMax) {
        this.horaMax = horaMax;
    }

    /**
     * @return the minutoMin
     */
    public int getMinutoMin() {
        return minutoMin;
    }

    /**
     * @param minutoMin the minutoMin to set
     */
    public void setMinutoMin(int minutoMin) {
        this.minutoMin = minutoMin;
    }

    /**
     * @return the minutoMax
     */
    public int getMinutoMax() {
        return minutoMax;
    }

    /**
     * @param minutoMax the minutoMax to set
     */
    public void setMinutoMax(int minutoMax) {
        this.minutoMax = minutoMax;
    }

    /**
     * @return the listaHorasMin
     */
    public List<SelectItem> getListaHorasMin() {
        return listaHorasMin;
    }

    /**
     * @param listaHorasMin the listaHorasMin to set
     */
    public void setListaHorasMin(List<SelectItem> listaHorasMin) {
        this.listaHorasMin = listaHorasMin;
    }

    /**
     * @return the listaMinutosMin
     */
    public List<SelectItem> getListaMinutosMin() {
        return listaMinutosMin;
    }

    /**
     * @param listaMinutosMin the listaMinutosMin to set
     */
    public void setListaMinutosMin(List<SelectItem> listaMinutosMin) {
        this.listaMinutosMin = listaMinutosMin;
    }

    /**
     * @return the listaHorasMax
     */
    public List<SelectItem> getListaHorasMax() {
        return listaHorasMax;
    }

    /**
     * @param listaHorasMax the listaHorasMax to set
     */
    public void setListaHorasMax(List<SelectItem> listaHorasMax) {
        this.listaHorasMax = listaHorasMax;
    }

    /**
     * @return the listaMinutosMax
     */
    public List<SelectItem> getListaMinutosMax() {
        return listaMinutosMax;
    }

    /**
     * @param listaMinutosMax the listaMinutosMax to set
     */
    public void setListaMinutosMax(List<SelectItem> listaMinutosMax) {
        this.listaMinutosMax = listaMinutosMax;
    }

    public List<SelectItem> itemsHoras() {
        List<SelectItem> listSelectItem = new ArrayList<>();

        for (int i = 0; i <= 23; i++) {
            SelectItem item = new SelectItem();
            int value = (i < 13 ? i : i - 12);
            StringBuilder hora = new StringBuilder(String.format("%02d", value));

            if (i < 12) {
                hora.append(" am");
            } else {
                hora.append(" pm");
            }

            item.setValue(i);
            item.setLabel(hora.toString());
            listSelectItem.add(item);
        }

        return listSelectItem;
    }

    public List<SelectItem> itemsMinutos() {
        List<SelectItem> listSelectItem = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            String minuto = String.format("%02d", i);
            listSelectItem.add(new SelectItem(i, minuto));
//            listSelectItem.add(new SelectItem(i, ((i < 10) ? ("0".concat(i.toString())) : i.toString())));
        }
        return listSelectItem;
    }

    public void cambiarSemaforo() {
        try {
            int idSemafro =estadoSemaforoVO.getSemaforoID();
            getEstadoSemaforoVO().setGrMapaIDtxt("");
            setListaZonasRuta(traerZonasRuta(getEstadoSemaforoVO().getGrMapaIDtxt()));
            setLstRutasPorZonas(new ArrayList<RutaTerrestreVo>());
            setDisableText(!(idSemafro > 0));
            PrimeFaces.current().executeScript(";limpiarBusquedaRutas();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    /**
     * @return the disableText
     */
    public boolean isDisableText() {
        return disableText;
    }

    /**
     * @param disableText the disableText to set
     */
    public void setDisableText(boolean disableText) {
        this.disableText = disableText;
    }
}
