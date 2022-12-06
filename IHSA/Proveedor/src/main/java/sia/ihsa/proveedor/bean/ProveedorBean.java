/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.proveedor.bean;

import com.google.common.base.Strings;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.ihsa.admin.Sesion;
import sia.ihsa.utils.FacesUtilsBean;
import sia.modelo.Orden;
import sia.modelo.SiAdjunto;
import sia.modelo.documento.vo.DocumentoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.vo.FacturaAdjuntoVo;
import sia.modelo.sistema.vo.FacturaContenidoNacionalVo;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.vo.ApCampoVo;
import sia.notificaciones.proveedor.impl.NotificacionProveedorImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvClasificacionArchivoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaContenidoNacionalImpl;
import sia.servicios.sistema.impl.SiFacturaDetalleImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.util.FacturaEstadoEnum;
import sia.util.OrdenEstadoEnum;
import sia.util.TipoArchivoFacturaEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;
import sia.constantes.Configurador;
import org.primefaces.PrimeFaces;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.faces.view.ViewScoped;
import org.apache.commons.io.FilenameUtils;
import javax.inject.Named;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import sia.util.Env;

/**
 *
 * @author mluis
 */
@Named(value = "proveedorBean")
@ViewScoped
public class ProveedorBean implements Serializable {

    @Inject
    private Sesion sesion;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    private SiFacturaImpl siFacturaImpl;
    @Inject
    private ApCampoImpl apCampoImpl;
    @Inject
    private CompaniaImpl companiaImpl;
    @Inject
    private SiFacturaAdjuntoImpl facturaAdjuntoImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private SiFacturaDetalleImpl facturaDetalleImpl;
    @Inject
    private SiFacturaContenidoNacionalImpl facturaContenidoNacionalImpl;
    @Inject
    private PvClasificacionArchivoImpl pvClasificacionArchivoImpl;
    @Inject
    private ProveedorServicioImpl proveedorServicioImpl;
    @Inject
    private NotificacionProveedorImpl notificacionImpl;
    @Inject
    private SiFacturaAdjuntoImpl siFacturaAdjuntoImpl;
    //
    private Map<String, List<?>> mapaInicial;
    private Map<String, Long> mapaTotales;
    private List<ApCampoVo> listaEmpresa;
    private int campo;
    private int campoEnv;
    private int campoRec;
    private int campoRecCXP;
    private String codigoOrden;
    private Map<String, List<SelectItem>> selectCampo;
    private List<FacturaVo> facturas;
    private List<FacturaAdjuntoVo> soportes;
    private List<FacturaAdjuntoVo> comprobantes;
    private int idFactura;
    private Date inicio;
    private Date fin;
    private FacturaVo facturaVo;
    private List<FacturaContenidoNacionalVo> contenidoNacionalFactura;

    private DocumentoVO docVO;

    private boolean isTipoCXPXML = true;
    private String folioHist;
    private static final String CXP_MODAL_HIDE = ";$(dialogoDatosFacturaCXP).modal('hide');";
    private static final String ARCHIVO_FACT_FILE_ENTRY = "frmArchivoFact:file-entry";
    private static final String CXP_MODAL_SHOW = ";$(dialogoDatosFacturaCXP).modal('show');";

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Getter
    @Setter
    private boolean mensajeLeido;

    @PostConstruct
    public void iniciar() {
        idFactura = 0;
        mensajeLeido = false;
        if (sesion.getCompaniaVoSesion() == null) {
            mapaInicial = new HashMap<>();
            mapaTotales = new HashMap<>();
            listaEmpresa = new ArrayList<>();
            selectCampo = new HashMap<>();
            contenidoNacionalFactura = new ArrayList<>();
        } else {
            contenidoNacionalFactura = new ArrayList<>();
            //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            //Fecha actual
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            //
            setInicio(calendar.getTime());
            setFin(new Date());
            //
            iniciarCarga();
        }
        facturas = new ArrayList<>();

    }

    private void iniciarCarga() {
        campo = 0;
        mapaInicial = new HashMap<>();
        mapaTotales = new HashMap<>();

        if (sesion.getProveedorVo() != null) {
            mapaInicial.put(
                    "ordenCompra",
                    autorizacionesOrdenImpl.traerOrdenPorRangoStatusCompania(
                            OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(),
                            OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId(),
                            sesion.getProveedorVo().getIdProveedor(),
                            sesion.getCompaniaVoSesion().getRfcCompania()
                    )
            );

            mapaInicial.put(
                    "facturaCliente",
                    siFacturaImpl.traerFacturaPorStatus(
                            (sesion.getProveedorVo().isNacional() ? FacturaEstadoEnum.ENVIADA_CLIENTE.getId() : FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId()),
                            Constantes.CERO,
                            sesion.getProveedorVo().getIdProveedor(),
                            sesion.getCompaniaVoSesion().getRfcCompania()
                    )
            );

            mapaInicial.put(
                    "facturaRechazada",
                    siFacturaImpl.traerFacturaDevuelta(
                            FacturaEstadoEnum.CREADA.getId(),
                            Constantes.CERO, sesion.getProveedorVo().getIdProveedor(),
                            sesion.getCompaniaVoSesion().getRfcCompania()
                    )
            );

            cargarCXP();

        }

        traerContadores();

        List<SelectItem> lista = new ArrayList<>();
        listaEmpresa = new ArrayList<>();
        selectCampo = new HashMap<>();
        lista.add(new SelectItem(Constantes.CERO, "Todas . . . "));
        listaEmpresa = apCampoImpl.traerApCampoPorEmpresa(sesion.getRfcCompania());

        if (!mapaInicial.get("ordenCompra").isEmpty()) {
            for (ApCampoVo campoVo : listaEmpresa) {
                lista.add(new SelectItem(campoVo.getId(), campoVo.getNombre()));
            }
        }

        selectCampo.put("asignada", lista);

        lista = new ArrayList<>();
        lista.add(new SelectItem(Constantes.CERO, "Todas . . . "));

        if (!mapaInicial.get("facturaCliente").isEmpty()) {
            for (ApCampoVo campoVo : listaEmpresa) {
                lista.add(new SelectItem(campoVo.getId(), campoVo.getNombre()));
            }
        }

        selectCampo.put("enviadas", lista);

        lista = new ArrayList<>();
        lista.add(new SelectItem(Constantes.CERO, "Todas . . . "));

        if (!mapaInicial.get("facturaRechazada").isEmpty()) {
            for (ApCampoVo campoVo : listaEmpresa) {
                lista.add(new SelectItem(campoVo.getId(), campoVo.getNombre()));
            }
        }

        selectCampo.put("rechazada", lista);
        
        
        selectCampo.put("facturaCXPCombo",siFacturaImpl.traerFacturaCXPCompania(sesion.getProveedorVo().getIdProveedor(), null, 0));
        //
        traerFacturaPorProveedor();
    }

    private void cargarCXP() {
        mapaInicial.put(
                "facturaCXP",
                siFacturaImpl.traerFacturaCXP(
                        sesion.getProveedorVo().getIdProveedor(),
                        sesion.getCompaniaVoSesion().getRfcCompania(),
                        0
                )
        );
    }

    private void cargarTotalCXP() {
        mapaTotales.put(
                "totalCXP",
                new Long(mapaInicial.get("facturaCXP") != null ? mapaInicial.get("facturaCXP").size() : 0)
        );
    }

    private void traerFacturaPorProveedor() {
        mapaInicial.put("facturaPorProveedor",
                siFacturaImpl.traerFacturaPorProveedor(sesion.getProveedorVo().getIdProveedor(), inicio, fin, folioHist, 750));
    }

    private void traerContadores() {
        long tf = autorizacionesOrdenImpl.totalOrdenPorEstatusProveedorEmpresa(
                OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId(),
                sesion.getCompaniaVoSesion().getRfcCompania(),
                sesion.getProveedorVo().getIdProveedor()
        );
        tf += autorizacionesOrdenImpl.totalOrdenPorEstatusProveedorEmpresa(
                OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(),
                sesion.getCompaniaVoSesion().getRfcCompania(),
                sesion.getProveedorVo().getIdProveedor()
        );
        mapaTotales.put(
                "totalOCS",
                tf
        );

        mapaTotales.put(
                "totalFacint",
                siFacturaImpl.totalFacturaPorStatusCompania(
                        (sesion.getProveedorVo().isNacional() ? FacturaEstadoEnum.ENVIADA_CLIENTE.getId() : FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId()),
                        sesion.getCompaniaVoSesion().getRfcCompania(),
                        sesion.getProveedorVo().getIdProveedor()
                )
        );

        mapaTotales.put(
                "totalFacturaRechazada",
                siFacturaImpl.totalFacturaDevueltasPorCompania(
                        FacturaEstadoEnum.CREADA.getId(),
                        sesion.getCompaniaVoSesion().getRfcCompania(),
                        sesion.getProveedorVo().getIdProveedor()
                )
        );

        cargarTotalCXP();
    }

    public void seleccionarEmpresa() {
        sesion.setCompaniaVoSesion(companiaImpl.traerPorRFC(sesion.getRfcCompania()));
        //
        PrimeFaces.current().executeScript("entendido('checkAceptoPrin','divEntendidoPrin');");
        //
        PrimeFaces.current().executeScript("$(dialogoMensajePrincipal).modal('show');");
    }

    public String refrescarPrincipalFactura() {
        //
        iniciarCarga();
        //
        PrimeFaces.current().executeScript("entendido('checkAceptoPrin','divEntendidoPrin');");
        //

//        PrimeFaces.current().executeScript("introJs().start();");
        PrimeFaces.current().executeScript("$(dialogoMensajePrincipal).modal('hide');");

        //
        return "/principal";
    }

    public void seleccionarCliente() {
        mapaInicial.put(
                "ordenCompra",
                autorizacionesOrdenImpl.traerOrdenPorRangoStatusCompania(
                        OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(),
                        OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId(),
                        sesion.getProveedorVo().getIdProveedor(),
                        sesion.getCompaniaVoSesion().getRfcCompania(),
                        campo)
        );
    }

    public void seleccionarCampoFacEnviada() {
        mapaInicial.put(
                "facturaCliente",
                siFacturaImpl.traerFacturaPorStatus(
                        FacturaEstadoEnum.ENVIADA_CLIENTE.getId(),
                        campoEnv,
                        sesion.getProveedorVo().getIdProveedor(),
                        sesion.getCompaniaVoSesion().getRfcCompania()
                )
        );
    }

    public void seleccionarCampoFacRechazada() {
        mapaInicial.put(
                "facturaRechazada",
                siFacturaImpl.traerFacturaDevuelta(
                        FacturaEstadoEnum.CREADA.getId(),
                        campoRec,
                        sesion.getProveedorVo().getIdProveedor(),
                        sesion.getCompaniaVoSesion().getRfcCompania()
                )
        );
    }
    
    public void seleccionarCampoCXP() {
        mapaInicial.put(
                "facturaCXP",
                siFacturaImpl.traerFacturaCXP(
                        sesion.getProveedorVo().getIdProveedor(),
                        sesion.getCompaniaVoSesion().getRfcCompania(),
                       campoRecCXP 
                )
        );
    }

    public void seleccionarCompra() {
        int compraId = Integer.parseInt(FacesUtilsBean.getRequestParameter("compraId"));
        facturas = siFacturaImpl.facturasPorOrden(compraId);
        //

        //
        PrimeFaces.current().executeScript("$(dialogoFacturaOrden).modal('show');");
    }

    public void agregarSoporte(int idFac) {
        soportes = new ArrayList<>();
        idFactura = idFac;
        llenarSoportes(idFactura);
    }

    public void agregarArchivoPortal(int idDDoc, String tipoDDoc) {
        if (idDDoc > 0 && tipoDDoc != null && !tipoDDoc.isEmpty()) {
            DocumentoVO vvo = new DocumentoVO();
            vvo.setTipoDoc(idDDoc);
            vvo.setTipoDocTxt(tipoDDoc);
            vvo.setObligatoria("ESV".equals(vvo.getTipoDocTxt()) || "AP".equals(vvo.getTipoDocTxt()));
            this.setDocVO(vvo);
            PrimeFaces.current().executeScript("$(adjuntarArchivoPortal).modal('show');");
        }
    }

    public void borrarArchivoPortal(int idDDoc, String uuid) {
        if (idDDoc > 0 && uuid != null && !uuid.isEmpty()) {
            if (proveedorServicioImpl.eliminarArchivosPortal(sesion.getProveedorVo().getIdProveedor(),
                    Constantes.LISTA_TIPO_PORTAL, sesion.getCompaniaVoSesion().getRfcCompania(),
                    uuid)) {
                sesion.setProveedorVo(proveedorServicioImpl.traerProveedorPorRfc(null, null, sesion.getProveedorVo().getIdProveedor(), Constantes.VACIO));
            }
        }
    }

    public String getSubDirectorioDocumento() {
        String subDir = "";
        try {
            subDir = "CV/Proveedor/" + sesion.getProveedorVo().getRfc() + "/ArchivosPortal/";
        } catch (RuntimeException e) {
            UtilLog4j.log.fatal(e);
        }
        return subDir;
    }

    private AdjuntoVO buildAdjuntoVO(DocumentoAnexo documentoAnexo) {
        AdjuntoVO adjunto = new AdjuntoVO();
        adjunto.setUrl(documentoAnexo.getRuta() + documentoAnexo.getNombreBase());
        adjunto.setNombre(documentoAnexo.getNombreBase());
        adjunto.setTipoArchivo(documentoAnexo.getTipoMime());
        adjunto.setTamanio(documentoAnexo.getTamanio());

        return adjunto;
    }

    public void subirArchivoPortal(FileUploadEvent fileEvent) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        try {
            UploadedFile fileInfo = fileEvent.getFile();
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                String extFile = FilenameUtils.getExtension(fileInfo.getFileName());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setNombreBase(sesion.getProveedorVo().getRfc() + "-" + this.getDocVO().getTipoDocTxt() + '.' + extFile);
                documentoAnexo.setRuta(getSubDirectorioDocumento());

                almacenDocumentos.guardarDocumento(documentoAnexo);

                int idADDjunto = siAdjuntoImpl.saveSiAdjunto(
                        buildAdjuntoVO(documentoAnexo),
                        sesion.getProveedorVo().getRfc()
                );

                pvClasificacionArchivoImpl.guardar(sesion.getProveedorVo().getRfc(), this.getDocVO(), sesion.getProveedorVo().getIdProveedor(), idADDjunto);

                FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
                fileInfo.delete();

                PrimeFaces.current().executeScript("$(adjuntarArchivoPortal).modal('hide');");
            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "+ + + ERROR + + +" + e.getMessage(), e);
        } finally {
            sesion.setProveedorVo(proveedorServicioImpl.traerProveedorPorRfc(null, null, sesion.getProveedorVo().getIdProveedor(), Constantes.VACIO));
            if (sesion.getProveedorVo().getPortalActPrep() != null && sesion.getProveedorVo().getPortalEstSocVig() != null) {
                notificacionImpl.notificacionArchivosPortal(sesion.getProveedorVo(), Constantes.VACIO, Constantes.VACIO);
                PrimeFaces.current().executeScript("$(dialogoMensajeArchivo).modal('hide');");
                PrimeFaces.current().executeScript("$(dialogoMensaje).modal('show');");
            }
        }
    }

    private void llenarSoportes(int facturaId) {
        List<FacturaAdjuntoVo> archivos = facturaAdjuntoImpl.traerSoporteFactura(facturaId, Constantes.BOOLEAN_TRUE);
        for (FacturaAdjuntoVo archivo : archivos) {
            if (archivo.getTipo().equals(TipoArchivoFacturaEnum.TIPO_SOPORTE.toString())) {
                soportes.add(archivo);
            }
        }
    }

    public String seleccionar(int idOrd) {
        Env.setContext(sesion.getCtx(), "idCompra", idOrd);
        return "/vistas/administraCompra.xhtml?faces-redirect=true";
    }

    public String seleccionarFactura(int fac) {
        Env.setContext(sesion.getCtx(), "idFactura", fac);
        return "/vistas/administraCompra.xhtml?faces-redirect=true";
    }

    public void buscarHistorial() {
        traerFacturaPorProveedor();
    }

    public void seleccionarFacturaHistorial(int idFac) {
        soportes = new ArrayList<>();
        facturaVo = new FacturaVo();
        facturaVo.setDetalleFactura(new ArrayList<>());
        idFactura = idFac;
        facturaVo = siFacturaImpl.buscarFactura(idFactura);
        soportes = facturaAdjuntoImpl.traerSoporteFactura(idFactura, false);
        setComprobantes(facturaAdjuntoImpl.traerSoporteFactura(idFactura, false, "'" + TipoArchivoFacturaEnum.TIPO_PAGO.toString() + "'"));
        if (facturaVo.getIdAdjunto() > 0) {
            FacturaAdjuntoVo facturaAdjuntoVo = new FacturaAdjuntoVo();
            facturaAdjuntoVo.setAdjuntoVo(new AdjuntoVO());
            facturaAdjuntoVo.setTipo("Carta ");
            facturaAdjuntoVo.getAdjuntoVo().setId(facturaVo.getIdAdjunto());
            facturaAdjuntoVo.getAdjuntoVo().setNombre(facturaVo.getAdjunto());
            facturaAdjuntoVo.getAdjuntoVo().setUuid(facturaVo.getUuId());
            soportes.add(facturaAdjuntoVo);
        }

        //
        facturaVo.setDetalleFactura(facturaDetalleImpl.traerDetalle(idFactura));
        //
        contenidoNacionalFactura = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(idFactura);
        //
        //System.out.println("fac: " + facturaVo.getFolio());
        PrimeFaces.current().executeScript("$(dialogoDatosFactura).modal('show');");
    }

    public void seleccionarFacturaCXPPdf(int idFac) {
        facturaVo = new FacturaVo();
        idFactura = idFac;
        facturaVo = siFacturaImpl.buscarFactura(idFactura);
        this.setIsTipoCXPXML(false);

        PrimeFaces.current().executeScript(CXP_MODAL_SHOW);
    }

    public void seleccionarFacturaCXPXml(int idFac) {
        facturaVo = new FacturaVo();
        idFactura = idFac;
        facturaVo = siFacturaImpl.buscarFactura(idFactura);
        this.setIsTipoCXPXML(true);

        PrimeFaces.current().executeScript(CXP_MODAL_SHOW);
    }

    public void borrarFacturaCXPXml(int idFac) {
        facturaVo = new FacturaVo();
        idFactura = idFac;
        facturaVo = siFacturaImpl.buscarFactura(idFactura);

        siFacturaImpl.borrarCXPXml(sesion.getProveedorVo().getRfc(), facturaVo);
        siAdjuntoImpl.eliminarArchivo(facturaVo.getComplementoPago(), sesion.getProveedorVo().getRfc());
        cargarCXP();
    }

    public void cerrarCargaSoporte() {
        PrimeFaces.current().executeScript(CXP_MODAL_HIDE);
    }

    public void uploadFile(FileUploadEvent fileEvent) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            UploadedFile fileInfo = fileEvent.getFile();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                //
                if (isIsTipoCXPXML()) {
                    procesarCXPXML(fileInfo);
                } else {
                    procesarCXPPdf(fileInfo);
                }

                fileInfo.delete();

            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

        } catch (Exception e) {
            LOGGER.error(this, e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    private void procesarCXPPdf(UploadedFile fileInfo) {
        SiAdjunto adj = guardarAdjunto(fileInfo);
        //
        siFacturaAdjuntoImpl.guardar(adj, facturaVo.getId(), TipoArchivoFacturaEnum.TIPO_CXP_PDF.toString(), sesion.getProveedorVo().getRfc());
        siFacturaImpl.guardarCXPPdf(sesion.getProveedorVo().getRfc(), facturaVo, adj);
        FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
        cargarCXP();
        cargarTotalCXP();
        PrimeFaces.current().executeScript(CXP_MODAL_HIDE);
    }

    private void procesarCXPXML(UploadedFile fileInfo) {
        try {
            siFacturaImpl.validarCXP(getFileFromUploadedFile(fileInfo), facturaVo);

            SiAdjunto adj = guardarAdjunto(fileInfo);
            if (adj == null) {
                FacesUtilsBean.addErrorMessage(
                        ARCHIVO_FACT_FILE_ENTRY,
                        "No se cargó el archivo de la factura, por favor envie un correo soportesia@ihsa.mx."
                );
            } else {
                siFacturaAdjuntoImpl.guardar(
                        adj,
                        facturaVo.getId(),
                        TipoArchivoFacturaEnum.TIPO_CXP_XML.toString(),
                        sesion.getProveedorVo().getRfc()
                );
                siFacturaImpl.guardarCXPXml(sesion.getProveedorVo().getRfc(), facturaVo, adj);
                cargarCXP();
                cargarTotalCXP();
                PrimeFaces.current().executeScript(CXP_MODAL_HIDE);
            }
        } catch (IllegalStateException | EJBException e) {
            PrimeFaces.current().executeScript("$(msjValidando).css('visibility', 'hidden')");
            FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, e.getMessage());
        }
    }

    public String buscarOrden() {
        String pag = "";
        //
        if (Strings.isNullOrEmpty(codigoOrden)) {
            FacesUtilsBean.addErrorMessage("Ingrese un código interno de orden de compra. ");
        } else {
            Orden ord
                    = ordenImpl.ordenPorEmpresaEstatus(
                            codigoOrden.trim(),
                            sesion.getCompaniaVoSesion().getRfcCompania(),
                            OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(),
                            OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId()
                    );

            if (ord == null) {
                FacesUtilsBean.addErrorMessage("La orden " + codigoOrden + " aún no esta disponible para el proceso de facturación, por favor, "
                        + " póngase en contacto con el comprador.");
            } else {
                ExternalContext exc = FacesContext.getCurrentInstance().getExternalContext();
                exc.getSessionMap();
                Map<String, Object> sessionMap = exc.getSessionMap();
                sessionMap.put("idCompra", ord.getId());
                //
                pag = "/vistas/administraCompra.xhtml";
            }

        }

        return pag;
    }

    private File getFileFromUploadedFile(UploadedFile uploadedFileX) {
        try {
            Path tmpFile = Files.createTempFile(FilenameUtils.getBaseName(uploadedFileX.getFileName()), "." + FilenameUtils.getExtension(uploadedFileX.getFileName()));
            Files.copy(uploadedFileX.getInputStream(), tmpFile, StandardCopyOption.REPLACE_EXISTING);
            return tmpFile.toFile();
        } catch (IOException e) {
            FacesUtilsBean.addErrorMessage(ARCHIVO_FACT_FILE_ENTRY, e.getMessage());
            return null;
        }
    }

    public void cargarSoporte(UploadedFile fileInfo) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        //
        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
        if (addArchivo) {
            SiAdjunto adj = guardarAdjunto(fileInfo);
            facturaAdjuntoImpl.guardar(adj, idFactura, TipoArchivoFacturaEnum.TIPO_XML.toString(), sesion.getProveedorVo().getRfc());

            llenarSoportes(idFactura);
            //
            PrimeFaces.current().executeScript("$(dialogoCargaSoporte).modal('hide');");
            FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
        } else {
            FacesUtilsBean.addErrorMessage(new StringBuilder()
                    .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                    .append(validadorNombreArchivo.getCaracteresNoValidos())
                    .toString());
        }

    }

    private SiAdjunto guardarAdjunto(UploadedFile fileInfo) {
        try {
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
            documentoAnexo.setRuta(uploadDirectory());
            documentoAnexo.setNombreBase(fileInfo.getFileName());
            documentoAnexo.setTipoMime(fileInfo.getContentType());
            almacenDocumentos.guardarDocumento(documentoAnexo);
            return siAdjuntoImpl.save(
                    documentoAnexo.getNombreBase(),
                    new StringBuilder()
                            .append(documentoAnexo.getRuta())
                            .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                    fileInfo.getContentType(),
                    fileInfo.getSize(),
                    sesion.getProveedorVo().getRfc()
            );

        } catch (SIAException ex) {
            UtilLog4j.log.error(ex);
        }
        return null;
    }

    public String uploadDirectory() {
        return "Proveedor/" + sesion.getProveedorVo().getRfc() + "/Soportes";

    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the campo
     */
    public int getCampo() {
        return campo;
    }

    /**
     * @param campo the campo to set
     */
    public void setCampo(int campo) {
        this.campo = campo;
    }

    /**
     * @return the mapaInicial
     */
    public Map<String, List<?>> getMapaInicial() {
        return mapaInicial;
    }

    /**
     * @param mapaInicial the mapaInicial to set
     */
    public void setMapaInicial(Map<String, List<?>> mapaInicial) {
        this.mapaInicial = mapaInicial;
    }

    /**
     * @return the mapaTotales
     */
    public Map<String, Long> getMapaTotales() {
        return mapaTotales;
    }

    /**
     * @param mapaTotales the mapaTotales to set
     */
    public void setMapaTotales(Map<String, Long> mapaTotales) {
        this.mapaTotales = mapaTotales;
    }

    /**
     * @return the selectCampo
     */
    public Map<String, List<SelectItem>> getSelectCampo() {
        return selectCampo;
    }

    /**
     * @param selectCampo the selectCampo to set
     */
    public void setSelectCampo(Map<String, List<SelectItem>> selectCampo) {
        this.selectCampo = selectCampo;
    }

    /**
     * @return the codigoOrden
     */
    public String getCodigoOrden() {
        return codigoOrden;
    }

    /**
     * @param codigoOrden the codigoOrden to set
     */
    public void setCodigoOrden(String codigoOrden) {
        this.codigoOrden = codigoOrden;
    }

    /**
     * @return the campoEnv
     */
    public int getCampoEnv() {
        return campoEnv;
    }

    /**
     * @param campoEnv the campoEnv to set
     */
    public void setCampoEnv(int campoEnv) {
        this.campoEnv = campoEnv;
    }

    /**
     * @return the campoRec
     */
    public int getCampoRec() {
        return campoRec;
    }

    /**
     * @param campoRec the campoRec to set
     */
    public void setCampoRec(int campoRec) {
        this.campoRec = campoRec;
    }

    /**
     * @return the facturas
     */
    public List<FacturaVo> getFacturas() {
        return facturas;
    }

    /**
     * @param facturas the facturas to set
     */
    public void setFacturas(List<FacturaVo> facturas) {
        this.facturas = facturas;
    }

    /**
     * @return the soportes
     */
    public List<FacturaAdjuntoVo> getSoportes() {
        return soportes;
    }

    /**
     * @param soportes the soportes to set
     */
    public void setSoportes(List<FacturaAdjuntoVo> soportes) {
        this.soportes = soportes;
    }

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        this.fin = fin;
    }

    /**
     * @return the facturaVo
     */
    public FacturaVo getFacturaVo() {
        return facturaVo;
    }

    /**
     * @param facturaVo the facturaVo to set
     */
    public void setFacturaVo(FacturaVo facturaVo) {
        this.facturaVo = facturaVo;
    }

    /**
     * @return the contenidoNacionalFactura
     */
    public List<FacturaContenidoNacionalVo> getContenidoNacionalFactura() {
        return contenidoNacionalFactura;
    }

    /**
     * @param contenidoNacionalFactura the contenidoNacionalFactura to set
     */
    public void setContenidoNacionalFactura(List<FacturaContenidoNacionalVo> contenidoNacionalFactura) {
        this.contenidoNacionalFactura = contenidoNacionalFactura;
    }

    /**
     * @return the docVO
     */
    public DocumentoVO getDocVO() {
        return docVO;
    }

    /**
     * @param docVO the docVO to set
     */
    public void setDocVO(DocumentoVO docVO) {
        this.docVO = docVO;
    }

    /**
     * @return the isTipoCXPXML
     */
    public boolean isIsTipoCXPXML() {
        return isTipoCXPXML;
    }

    /**
     * @param isTipoCXPXML the isTipoCXPXML to set
     */
    public void setIsTipoCXPXML(boolean isTipoCXPXML) {
        this.isTipoCXPXML = isTipoCXPXML;
    }

    /**
     * @return the folioHist
     */
    public String getFolioHist() {
        return folioHist;
    }

    /**
     * @param folioHist the folioHist to set
     */
    public void setFolioHist(String folioHist) {
        this.folioHist = folioHist;
    }

    /**
     * @return the comprobantes
     */
    public List<FacturaAdjuntoVo> getComprobantes() {
        return comprobantes;
    }

    /**
     * @param comprobantes the comprobantes to set
     */
    public void setComprobantes(List<FacturaAdjuntoVo> comprobantes) {
        this.comprobantes = comprobantes;
    }

    public boolean isBloquearFacturas() {
        return Configurador.bloquearFacturas();
    }

    public String getBloquearFacturasMsg() {
        return Configurador.bloquearFacturasMsg();
    }

    /**
     * @return the campoRecCXP
     */
    public int getCampoRecCXP() {
        return campoRecCXP;
    }

    /**
     * @param campoRecCXP the campoRecCXP to set
     */
    public void setCampoRecCXP(int campoRecCXP) {
        this.campoRecCXP = campoRecCXP;
    }
}
