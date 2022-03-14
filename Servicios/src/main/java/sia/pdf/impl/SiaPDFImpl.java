/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.pdf.impl;

import com.google.common.base.Strings;
import com.lowagie.text.pdf.PdfWriter;
import com.newrelic.api.agent.Trace;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Compania;
import sia.modelo.CvEvaluacion;
import sia.modelo.OcRequisicionCheckcode;
import sia.modelo.Orden;
import sia.modelo.Requisicion;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.OcRequisicionCheckcodeVO;
import sia.modelo.sgl.vo.Vo;
import sia.servicios.convenio.impl.CvConvenioDocumentoImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.orden.impl.OcRequisicionCheckcodeImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class SiaPDFImpl {

    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoRemote;
    @Inject
    private OrdenImpl ordenRemote;
    @Inject
    private OcOrdenEtsImpl servicioOcOrdenEts;
    @Inject
    private ReRequisicionEtsImpl servicioReRequisicion;
    @Inject
    private RequisicionImpl requisicionRemote;
    @Inject
    private OcRequisicionCheckcodeImpl ocRequisicionCheckcodeRemote;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private CvConvenioDocumentoImpl  cvConvenioDocumentoLocal;

    private static final String REP_NAME_EVALUACION = "evaluacion";
    private static final String REP_NAME_ORDEN = "reporteOC";
    private static final String REP_NAME_ORDEN_MULTI = "reporteOCMulti";
    private static final String EXT_PDF = ".pdf";
    private static final String JASPER_EXTENSION = ".jasper";
    private static final String JASPER_FILE_PATH_ORDEN = "Formatos/fuentesOrden";
    private static final String PDF_FILE_PATH_ORDEN = "Formatos/formatosOrden";
    private static final String REP_NAME_REQUI = "reporteMargen";
    private static final String JASPER_FILE_PATH_REQUISICION = "Formatos/fuentesRequisicion";
    private static final String PDF_FILE_PATH_REQUISICION = "Formatos/formatosRequisicion";
    private static final String JASPER_FILE_PATH_EVALUACION = "Formatos/fuentesEvaluacion";
    private static final String PDF_FILE_PATH_EVALUACION = "Formatos/formatosEvaluacion";
    private static final String PDFFILEPATH = "CondicionesGenerales/";
    private static final int ID_CATEGO_PDF_ETS = 4;

    private static final String SECRET = "ColchónPikolin";
    private static final int STR_MAX_LEN = 1;
    private static final String MIME_APP_PDF = "application/pdf";

    private static final UtilLog4j<SiaPDFImpl> LOGGER = UtilLog4j.log;

//    private final static Logger LOGGER = Logger.getLogger(OrdenImpl.class.getName());
    
    public void validarUUID(Orden orden) throws Exception {
        if (Strings.isNullOrEmpty(orden.getUuid())) {
            setUuidOrden(orden);
        }
    }

    private File crearPDFOrder(Orden source, Usuario usr) throws Exception {

        String nombreBase;

        if (Strings.isNullOrEmpty(source.getNavCode())) {
            nombreBase = source.getConsecutivo();
        } else {
            nombreBase = source.getNavCode();
        }

        return generarOrdenPDF(source, source.getId(), nombreBase, usr);
    }

    private File crearPDFEvaluacion(CvEvaluacion source, Usuario usr) throws Exception {
        String nombreBase = source.getConvenio().getCodigo() + "_" + source.getId();
        return generarEvaluacionPDF(source, source.getId(), nombreBase, usr);
    }

    
    @Trace
    public File getPDF(Object source, Usuario usr, boolean borrarArchivo) throws Exception {
        DocumentoAnexo pdfFile;
        File retVal = null;
        try {
            if (source != null) {
                if (source instanceof Orden) {
                    if (Strings.isNullOrEmpty(((Orden) source).getNavCode())) {
                        pdfFile = buscarOrdenPDF(((Orden) source).getConsecutivo());
                    } else {
                        pdfFile = buscarOrdenPDF(((Orden) source).getNavCode());
                    }

                    if (pdfFile == null) {
                        retVal = crearPDFOrder((Orden) source, usr);
                    } else if (borrarArchivo) {
                        final AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                        almacenDocumentos.borrarDocumento(pdfFile);
                        retVal = crearPDFOrder((Orden) source, usr);
                    }
                } else if (source instanceof Requisicion) {
                    pdfFile = buscarRequisicionPDF((Requisicion) source);
                    if (pdfFile == null) {
                        retVal = generarRequisicionPDF((Requisicion) source, usr);
                    } else if (borrarArchivo) {
                        final AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                        almacenDocumentos.borrarDocumento(pdfFile);
                        retVal = generarRequisicionPDF((Requisicion) source, usr);
                    }
                } else if (source instanceof CvEvaluacion) {
                    pdfFile = buscarEvaluacionPDF((CvEvaluacion) source);
                    if (pdfFile == null) {
                        retVal = crearPDFEvaluacion((CvEvaluacion) source, usr);
                    } else if (borrarArchivo) {
                        final AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                        almacenDocumentos.borrarDocumento(pdfFile);
                        retVal = crearPDFEvaluacion((CvEvaluacion) source, usr);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.warn(this, null, ex);
            throw new Exception(ex.getMessage());
        }

        return retVal;
    }

    private Orden setUuidOrden(Orden orden) {
        orden.setUuid(getUUID());
        ordenRemote.editarOrden(orden);
        return orden;
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    private File generarRequisicionPDF(Requisicion requisicion, Usuario usr) throws Exception {
        File pdfFile = null;

        DocumentoAnexo retVal;

        try (Connection conn = getConexion();) {
            final Map<String, Object> params = new HashMap();
            final String REPOSITORYPATH = getRepositoryPath();
            final String subRepotPath = new StringBuilder().append(REPOSITORYPATH).append(JASPER_FILE_PATH_REQUISICION).append(File.separator).toString();

            // Create arguments
            params.put("REPORT_LOCALE", new Locale("es", "MX"));
            params.put("idRequisicion", requisicion.getId());
            params.put("SUBREPORT_DIR", subRepotPath);

            JasperData jasperData = new JasperData();
            jasperData.repositoryPath = REPOSITORYPATH;
            jasperData.params = params;
            jasperData.conn = conn;
            jasperData.jasperFilePath = JASPER_FILE_PATH_REQUISICION + File.separator;
            jasperData.pdfFilePath = PDF_FILE_PATH_REQUISICION + File.separator;
            jasperData.repName = REP_NAME_REQUI;
            jasperData.baseFileName = requisicion.getConsecutivo();

            pdfFile = generateAndExportJasper(jasperData);

            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            retVal = new DocumentoAnexo(pdfFile);
            retVal.setRuta(PDF_FILE_PATH_REQUISICION + File.separator);
            retVal.setNombreBase(requisicion.getConsecutivo() + EXT_PDF);
            almacenDocumentos.guardarDocumento(retVal);

            guardarRequisicionETS(requisicion,
                    new StringBuilder().append(requisicion.getConsecutivo()).append(EXT_PDF).toString(),
                    new StringBuilder().append(PDF_FILE_PATH_REQUISICION + File.separator)
                            .append(requisicion.getConsecutivo())
                            .append(EXT_PDF).toString(),
                    MIME_APP_PDF,
                    pdfFile.getTotalSpace(),
                    usr
            );
        } catch (JRException ex) {
            LOGGER.warn(this, null, ex);
            throw new Exception(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.warn(this, null, ex);
            throw new Exception(ex.getMessage());
        }

        return pdfFile;
    }

    private File generateAndExportJasper(JasperData jasperData) throws JRException {
        // Generate jasper print
        final JasperPrint jprint
                = (JasperPrint) JasperFillManager.fillReport(
                        new StringBuilder()
                                .append(jasperData.repositoryPath)
                                .append(jasperData.jasperFilePath)
                                .append(jasperData.repName)
                                .append(JASPER_EXTENSION).toString(),
                        jasperData.params,
                        jasperData.conn
                );

        // Export pdf file
        final JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED, Boolean.TRUE);
        exporter.setParameter(JRPdfExporterParameter.IS_128_BIT_KEY, Boolean.TRUE);
        exporter.setParameter(JRPdfExporterParameter.PERMISSIONS, PdfWriter.ALLOW_PRINTING);
        exporter.setParameter(
                JRExporterParameter.OUTPUT_FILE_NAME,
                new StringBuilder().append(jasperData.repositoryPath)
                        .append(jasperData.pdfFilePath)
                        .append(jasperData.baseFileName)
                        .append(EXT_PDF).toString()
        );
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jprint);
        exporter.exportReport();

        return new File(
                new StringBuilder().append(jasperData.repositoryPath)
                        .append(jasperData.pdfFilePath)
                        .append(jasperData.baseFileName)
                        .append(EXT_PDF).toString()
        );
    }

    private void guardarOrdenETS(Orden orden, String fileName, String path,
            String content, long size, Usuario usr)
            throws Exception {
        try {
            SiAdjunto adj
                    = this.servicioSiAdjuntoRemote.save(
                            fileName,
                            path,
                            content,
                            size,
                            usr.getId()
                    );

            if (adj != null) {
                this.servicioOcOrdenEts.crearOcOrdenEts(
                        orden.getId(), ID_CATEGO_PDF_ETS, adj,
                        usr
                );

                StringBuilder url = new StringBuilder(100);
                url.append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W=")
                        .append(adj.getId())
                        .append("&ZWZ3W=")
                        .append(adj.getUuid());
                orden.setUrl(url.toString());
                orden.setCheckcode(encriptar(orden.getProveedor().getRfc(), url.toString()));
                this.ordenRemote.editarOrden(orden);

                //            orden.getRequisicion().setCheckcode(this.encriptar(orden.getProveedor().getRfc(),orden.getRequisicion().getUrl(), "ColchónPikolin"));
//            this.requisicionRemote.edit(orden.getRequisicion());
                OcRequisicionCheckcodeVO check
                        = ocRequisicionCheckcodeRemote.getRequiCheckCode(
                                orden.getId(),
                                orden.getRequisicion().getId(),
                                orden.getProveedor().getRfc()
                        );

                if (check == null) {
                    OcRequisicionCheckcode checkCode = new OcRequisicionCheckcode();
                    checkCode.setOrden(orden);
                    checkCode.setRequisicion(orden.getRequisicion());
                    checkCode.setRfc(orden.getProveedor().getRfc());
                    checkCode.setCheckcode(encriptar(orden.getProveedor().getRfc(), orden.getRequisicion().getUrl()));
                    checkCode.setEliminado(Constantes.BOOLEAN_FALSE);
                    ocRequisicionCheckcodeRemote.create(checkCode);
                } else if (check.isEliminado()) {
                    OcRequisicionCheckcode checkCode = ocRequisicionCheckcodeRemote.find(check.getId());
                    checkCode.setEliminado(Constantes.BOOLEAN_FALSE);
                    ocRequisicionCheckcodeRemote.edit(checkCode);
                }
            }

        } catch (SIAException e) {
            LOGGER.warn(this, null, e);
            throw new Exception("Error al generar el ETS: " + e.getMensajeParaProgramador());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn(this, null, e);
            throw new Exception("Error al generar el ETS: " + e.getMessage());
        }
    }

    private void guardarEvaluacionETS(CvEvaluacion eva, String fileName, String path,
            String content, long size, Usuario usr)
            throws Exception {
        try {
            SiAdjunto adj
                    = this.servicioSiAdjuntoRemote.save(
                            fileName,
                            path,
                            content,
                            size,
                            usr.getId()
                    );

            if (adj != null) {
                Vo tipoDoc = new Vo();
                tipoDoc.setId(Constantes.DOCUMENTO_TIPO_EVALUACION);
                this.cvConvenioDocumentoLocal.guardar(usr.getId(),
                        tipoDoc,
                        eva.getConvenio().getId(),
                        adj.getId());
            }

        } catch (SIAException e) {
            LOGGER.warn(this, null, e);
            throw new Exception("Error al generar el ETS: " + e.getMensajeParaProgramador());
        }
    }

    private void guardarRequisicionETS(Requisicion requisicion, String fileName,
            String path, String content, long size, Usuario usr) throws Exception {
        try {
            SiAdjunto adj
                    = this.servicioSiAdjuntoRemote.save(
                            fileName,
                            path,
                            content,
                            size,
                            usr.getId()
                    );

            if (adj != null) {
                this.servicioReRequisicion.crear(requisicion, adj, usr, Constantes.BOOLEAN_FALSE);
                StringBuilder url = new StringBuilder();
                url.append(Configurador.urlSia()).append("Compras/OFWSS?Z4BX2=SIA&ZWZ4W=")
                        .append(adj.getId())
                        .append("&ZWZ3W=")
                        .append(adj.getUuid());
                requisicion.setUrl(url.toString());
            }

            this.requisicionRemote.edit(requisicion);

        } catch (Exception e) {
            LOGGER.warn(this, null, e);
            throw new Exception("Error al generar el ETS :" + e.getMessage());
        }
    }

    private Connection getConexion() {
        Connection result = null;

        try {
            final Context initialContext = new InitialContext();

            final DataSource datasource = (DataSource) initialContext.lookup(Constantes.DATASOURCE_CONTEXT);
            if (datasource == null) {
                LOGGER.warn(this, "Failed to lookup datasource.");
            } else {
                result = datasource.getConnection();
            }
        } catch (NamingException | SQLException ex) {
            LOGGER.warn(this, null, ex);
        }

        return result;
    }

    private File generarOrdenPDF(Orden orden, Integer ordenID, String nombre, Usuario usr)
            throws Exception {
        File pdfFile = null;

        try (Connection conn = getConexion();) {
            Map params = new HashMap();
            final String REPOSITORYPATH = getRepositoryPath();

            // Create arguments
            params.put("REPORT_LOCALE", new Locale("es", "MX"));
            params.put("ORDENID", ordenID);
            String subRepotPath = new StringBuilder().append(REPOSITORYPATH).append(JASPER_FILE_PATH_ORDEN).append(File.separator).toString();
            params.put("SUBREPORT_DIR", subRepotPath);

            // Generate jasper print
            JasperData jasperData = new JasperData();
            jasperData.repositoryPath = REPOSITORYPATH;
            jasperData.params = params;
            jasperData.conn = conn;
            jasperData.jasperFilePath = JASPER_FILE_PATH_ORDEN + File.separator;
            jasperData.pdfFilePath = PDF_FILE_PATH_ORDEN + File.separator;

            if (orden.isMultiproyecto()) {
                jasperData.repName = REP_NAME_ORDEN_MULTI;
            } else {
                jasperData.repName = REP_NAME_ORDEN;
            }

            jasperData.baseFileName = nombre;
            pdfFile = generateAndExportJasper(jasperData);

            String nomArchivo = new StringBuilder().append(nombre).append(EXT_PDF).toString();

            guardarOrdenETS(orden,
                    new StringBuilder().append(nombre).append(EXT_PDF).toString(),
                    new StringBuilder().append(PDF_FILE_PATH_ORDEN).append(File.separator).append(nomArchivo).toString(),
                    MIME_APP_PDF,
                    pdfFile.getTotalSpace(),
                    usr
            );

            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            DocumentoAnexo documentoAnexo = new DocumentoAnexo(pdfFile);
            documentoAnexo.setRuta(PDF_FILE_PATH_ORDEN);
            documentoAnexo.setNombreBase(nomArchivo);
            almacenDocumentos.guardarDocumento(documentoAnexo);

        } catch (JRException ex) {
            LOGGER.warn(this, null, ex);
            throw new Exception(ex);
        } catch (Exception ex) {
            LOGGER.warn(this, null, ex);
            throw ex;
        }

        return pdfFile;
    }

    private File generarEvaluacionPDF(CvEvaluacion eva, Integer evaID, String nombre, Usuario usr)
            throws Exception {
        File pdfFile = null;

        try (Connection conn = getConexion();) {
            Map params = new HashMap();
            final String REPOSITORYPATH = getRepositoryPath();

            // Create arguments
            params.put("REPORT_LOCALE", new Locale("es", "MX"));
            params.put("idEva", evaID);
            String subRepotPath = new StringBuilder().append(REPOSITORYPATH).append(JASPER_FILE_PATH_EVALUACION).append(File.separator).toString();
            params.put("SUBREPORT_DIR", subRepotPath);

            // Generate jasper print
            JasperData jasperData = new JasperData();
            jasperData.repositoryPath = REPOSITORYPATH;
            jasperData.params = params;
            jasperData.conn = conn;
            jasperData.jasperFilePath = JASPER_FILE_PATH_EVALUACION + File.separator;
            jasperData.pdfFilePath = PDF_FILE_PATH_EVALUACION + File.separator;

            jasperData.repName = REP_NAME_EVALUACION;

            jasperData.baseFileName = nombre;
            pdfFile = generateAndExportJasper(jasperData);

            String nomArchivo = new StringBuilder().append(nombre).append(EXT_PDF).toString();

            guardarEvaluacionETS(eva,
                    new StringBuilder().append(nombre).append(EXT_PDF).toString(),
                    new StringBuilder().append(PDF_FILE_PATH_ORDEN).append(File.separator).append(nomArchivo).toString(),
                    MIME_APP_PDF,
                    pdfFile.getTotalSpace(),
                    usr
            );

            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            DocumentoAnexo documentoAnexo = new DocumentoAnexo(pdfFile);
            documentoAnexo.setRuta(PDF_FILE_PATH_EVALUACION);
            documentoAnexo.setNombreBase(nomArchivo);
            almacenDocumentos.guardarDocumento(documentoAnexo);

        } catch (JRException ex) {
            LOGGER.warn(this, null, ex);
            throw new Exception(ex);
        } catch (Exception ex) {
            LOGGER.warn(this, null, ex);
            throw ex;
        }

        return pdfFile;
    }

    private String getRepositoryPath() {
        return this.parametrosSistemaServicioRemoto.find(STR_MAX_LEN).getUploadDirectory();
    }

    
    public DocumentoAnexo buscarOrdenPDF(String nombre) {
        DocumentoAnexo pdfFile = null;
        try {

            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            pdfFile = almacenDocumentos.cargarDocumento(PDF_FILE_PATH_ORDEN + File.separator + nombre + EXT_PDF);
            if (pdfFile != null) {
                pdfFile.setNombreBase(nombre + EXT_PDF);
                pdfFile.setRuta(PDF_FILE_PATH_ORDEN);
            }
        } catch (Exception ex) {
            LOGGER.warn(this, null, ex);
        }

        return pdfFile;
    }

    
    public File buscarPdfCG(Compania compania) throws Exception {
        File pdfFile = null;
        try {
            pdfFile
                    = new File(
                            new StringBuilder().append(getRepositoryPath())
                                    .append(PDFFILEPATH).append(File.separator)
                                    .append(compania.getRfc())
                                    .append(EXT_PDF).toString()
                    );
            if (!pdfFile.exists()) {
                pdfFile = null;
            }
        } catch (Exception ex) {
            LOGGER.warn(this, null, ex);
        }
        return pdfFile;
    }

    private DocumentoAnexo buscarRequisicionPDF(Requisicion requisicion) {
        DocumentoAnexo pdfFile = null;
        try {

            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            pdfFile = almacenDocumentos.cargarDocumento(PDF_FILE_PATH_REQUISICION + File.separator + requisicion.getConsecutivo() + EXT_PDF);

            if (pdfFile != null) {
                pdfFile.setNombreBase(requisicion.getConsecutivo() + EXT_PDF);
                pdfFile.setRuta(PDF_FILE_PATH_REQUISICION);
            }

        } catch (Exception ex) {
            LOGGER.warn(this, null, ex);
        }
        return pdfFile;
    }

    private DocumentoAnexo buscarEvaluacionPDF(CvEvaluacion cvEvaluacion) {
        DocumentoAnexo pdfFile = null;
        try {
            String nombreArc = cvEvaluacion.getConvenio().getCodigo() + "_" + cvEvaluacion.getId();
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            pdfFile = almacenDocumentos.cargarDocumento(PDF_FILE_PATH_EVALUACION + File.separator + nombreArc + EXT_PDF);

            if (pdfFile != null) {
                pdfFile.setNombreBase(nombreArc + EXT_PDF);
                pdfFile.setRuta(PDF_FILE_PATH_EVALUACION);
            }

        } catch (Exception ex) {
            LOGGER.warn(this, null, ex);
        }
        return pdfFile;
    }

    private String encriptar(String rfc, String url)
            throws NoSuchAlgorithmException {
        String retVal;
        StringBuilder text = new StringBuilder();
        try {
            text.append(rfc).append(url).append(SECRET);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(text.toString().getBytes("ISO-8859-1"));
            StringBuilder h = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                String cad = Integer.toHexString(0xFF & b[i]);
                if (cad.length() == STR_MAX_LEN) {
                    h.append('0');
                }
                h.append(cad);
            }
            retVal = h.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.warn(this, "Excepcion al encriptar", e);
            retVal = text.toString();
        }

        return retVal;
    }

    private class JasperData {

        protected transient String repositoryPath;
        protected transient Map params;
        protected transient Connection conn;
        protected transient String jasperFilePath;
        protected transient String pdfFilePath;
        protected transient String repName;
        protected transient String baseFileName;
    }
}
