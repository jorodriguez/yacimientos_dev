/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.factura.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.monitor.FileEntry;
import org.primefaces.PrimeFaces;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SiAdjunto;
import sia.modelo.sistema.vo.FacturaAdjuntoVo;
import sia.modelo.sistema.vo.FacturaContenidoNacionalVo;
import sia.modelo.sistema.vo.FacturaVo;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaContenidoNacionalImpl;
import sia.servicios.sistema.impl.SiFacturaDetalleImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiFacturaStatusImpl;
import sia.util.FacturaEstadoEnum;
import sia.util.TipoArchivoFacturaEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author jcarranza
 */
@Named(value = "comprobantePagoBean")
@ViewScoped
public class ComprobantePagoBean implements Serializable {

    private static final UtilLog4j<ComprobantePagoBean> LOGGER = UtilLog4j.log;

    @Inject
    private UsuarioBean usuarioBean;
//
    @Inject
    SiFacturaStatusImpl siFacturaStatusImpl;
    @Inject
    SiFacturaImpl siFacturaImpl;
    @Inject
    SiFacturaAdjuntoImpl siFacturaAdjuntoImpl;
    @Inject
    SiFacturaDetalleImpl siFacturaDetalleImpl;
    @Inject
    SiFacturaContenidoNacionalImpl facturaContenidoNacionalImpl;
    @Inject
    NotificacionRequisicionImpl notificacionImpl;
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;
    //
    private List<FacturaVo> listaFactura = new ArrayList<>();
    private FacturaVo facturaVo;
    private List<FacturaVo> listaNotaCredito = new ArrayList<>();
    private List<FacturaContenidoNacionalVo> contenidoNacional = new ArrayList<>();
    private List<FacturaAdjuntoVo> listaArchivosFactura = new ArrayList<>();
    private List<FacturaAdjuntoVo> listaArchivosNotaCredito = new ArrayList<>();
    private Date inicio;
    private Date fin;
    private List<SelectItem> proveedores = new ArrayList<>();
    private String provSelected;
    private String motivo;
    private boolean rechazarFactura;
    private String ligaZip = "";
    private boolean mostrarLiga;

    private static final String CP_MODAL_HIDE = ";$(dialogoComprobante).modal('hide');";
    private static final String ARCHIVO_FACT_FILE_ENTRY = "frmArchivoFact:file-entry";
    private static final String CP_MODAL_SHOW = ";$(dialogoComprobante).modal('show');";
    //
    @Getter
    @Setter
    private UploadedFile fileInfo;

    /**
     * Creates a new instance of HistorialFacturaBean
     */
    public ComprobantePagoBean() {
    }

    @PostConstruct
    public void iniciar() {
        Calendar cal = Calendar.getInstance();
        fin = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        inicio = cal.getTime();
        setFacturaVo(new FacturaVo());
        consultarListaFacturas();
        this.setMostrarLiga(false);
    }

    public void buscarFactura() {
        this.setMostrarLiga(false);
        consultarListaFacturas();
    }

    public void ejecutarProcesoComprobantePago() {
        try {
            //System.out.println("Ejecutando el proceso de comprobantes de pago ");
            //System.out.println("Home: " + System.getProperty("user.home"));
            /*
            String path = System.getProperty("user.home") + "/Timers/run.sh --mode p --action PROCESAR_COMPROBANTE_PAGO --email exchange";
            String[] command = {"sh", path};
            Process process = Runtime.getRuntime().exec(command);
            process.destroy();
             */

            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cd", System.getProperty("user.home") + "/Timers/", "./run.sh --mode p --action PROCESAR_COMPROBANTE_PAGO --email exchange");
            //           
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                //System.out.println("Success!");
                //System.out.println(output);
                System.exit(0);
                //System.out.println("Finaliza el proceso de comprobantes de pago ");
            }
            process.destroy();

            consultarListaFacturas();
            UtilLog4j.log.info("Terminando el proceso de comprobantes de pago ");
            //Runtime.getRuntime().exec("java -jar Timers.jar --mode p --action PROCESAR_COMPROBANTE_PAGO --email exchange"); //"--mode p --action PROCESAR_COMPROBANTE_PAGO --email gmail");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ComprobantePagoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void regresarFactura() {
        this.setMostrarLiga(false);
        consultarListaFacturas();
        PrimeFaces.current().executeScript("ocultarDiv('divHistFacturasDesc');mostrarDiv('divHistFacturas');limpiarTodos();");
    }

    private void consultarListaFacturas() {
        setListaFactura(siFacturaImpl.traerFacturaPorStatusFecha(
                FacturaEstadoEnum.PROCESO_DE_PAGO.getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                getProvSelected(),
                inicio,
                fin
        ));
    }

    public void exportarFactura() {
        String facIDs = "";
        File fileTemp = null;

        for (FacturaVo vo : getListaFactura()) {
            if (vo.isSelected()) {
                if (facIDs != null && facIDs.isEmpty()) {
                    facIDs += vo.getId();
                } else {
                    facIDs += ", " + vo.getId();
                }
            }
        }

        if (facIDs != null && !facIDs.isEmpty()) {
            this.setMostrarLiga(true);
            this.setLigaZip("/Compras/GenerarZip?ZWZ2W=0&ZWZ3W=" + facIDs);
            consultarListaFacturas();
        }

        PrimeFaces.current().executeScript("ocultarDiv('divHistFacturas');mostrarDiv('divHistFacturasDesc');");
    }

    public void seleccionar() {
        int id = Integer.parseInt(FacesUtilsBean.getRequestParameter("idFac"));
        facturaVo = siFacturaImpl.buscarFactura(id);
        PrimeFaces.current().executeScript(CP_MODAL_SHOW);
    }

    public void seleccionarNotaCredito() {
        int id = Integer.parseInt(FacesUtilsBean.getRequestParameter("idNotaCredito"));
        //
        setListaArchivosNotaCredito(siFacturaAdjuntoImpl.traerSoporteFactura(id, Constantes.BOOLEAN_FALSE));
        PrimeFaces.current().executeScript("$(dialogoArchivosNotaCredito).modal('show');");
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    /**
     * @return the listaFactura
     */
    public List<FacturaVo> getListaFactura() {
        return listaFactura;
    }

    /**
     * @param listaFactura the listaFactura to set
     */
    public void setListaFactura(List<FacturaVo> listaFactura) {
        this.listaFactura = listaFactura;
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
     * @return the listaNotaCredito
     */
    public List<FacturaVo> getListaNotaCredito() {
        return listaNotaCredito;
    }

    /**
     * @param listaNotaCredito the listaNotaCredito to set
     */
    public void setListaNotaCredito(List<FacturaVo> listaNotaCredito) {
        this.listaNotaCredito = listaNotaCredito;
    }

    /**
     * @return the contenidoNacional
     */
    public List<FacturaContenidoNacionalVo> getContenidoNacional() {
        return contenidoNacional;
    }

    /**
     * @param contenidoNacional the contenidoNacional to set
     */
    public void setContenidoNacional(List<FacturaContenidoNacionalVo> contenidoNacional) {
        this.contenidoNacional = contenidoNacional;
    }

    /**
     * @return the listaArchivosFactura
     */
    public List<FacturaAdjuntoVo> getListaArchivosFactura() {
        return listaArchivosFactura;
    }

    /**
     * @param listaArchivosFactura the listaArchivosFactura to set
     */
    public void setListaArchivosFactura(List<FacturaAdjuntoVo> listaArchivosFactura) {
        this.listaArchivosFactura = listaArchivosFactura;
    }

    /**
     * @return the listaArchivosNotaCredito
     */
    public List<FacturaAdjuntoVo> getListaArchivosNotaCredito() {
        return listaArchivosNotaCredito;
    }

    /**
     * @param listaArchivosNotaCredito the listaArchivosNotaCredito to set
     */
    public void setListaArchivosNotaCredito(List<FacturaAdjuntoVo> listaArchivosNotaCredito) {
        this.listaArchivosNotaCredito = listaArchivosNotaCredito;
    }

    /**
     * @return the proveedores
     */
    public List<SelectItem> getProveedores() {
        return proveedores;
    }

    /**
     * @param proveedores the proveedores to set
     */
    public void setProveedores(List<SelectItem> proveedores) {
        this.proveedores = proveedores;
    }

    public void proveedorListener(String event) {
        setProveedores(traerProveedores(event));
    }

    private List<SelectItem> traerProveedores(String cadena) {
        List<SelectItem> list = new ArrayList<>();
        try {
            if (cadena != null && !cadena.isEmpty() && cadena.length() > 2) {
                list = siFacturaImpl.traerProveedorPorStatusFacturas(cadena, FacturaEstadoEnum.PROCESO_DE_PAGO.getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId());
            }
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

    /**
     * @return the provSelected
     */
    public String getProvSelected() {
        return provSelected;
    }

    /**
     * @param provSelected the provSelected to set
     */
    public void setProvSelected(String provSelected) {
        this.provSelected = provSelected;
    }

    public void devolverFactura() {
        if (!motivo.isEmpty()) {
            siFacturaStatusImpl.rechazarFactura(usuarioBean.getUsuarioConectado().getId(), facturaVo, getMotivo(), usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.PROCESO_DE_PAGO.getId());
            setMotivo("");
            PrimeFaces.current().executeScript("$(dialogoDatosFacturaHistorial).modal('hide');");
            //
            setListaFactura(siFacturaImpl.traerFacturaPorStatusFecha(
                    FacturaEstadoEnum.PROCESO_DE_PAGO.getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    getProvSelected(),
                    inicio,
                    fin
            ));
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario agregar el motivo.");
        }

    }

    public void aceptarFactura() {
        List<FacturaVo> lf = new ArrayList<>();
        lf.add(facturaVo);
        siFacturaImpl.aceptarFactura(usuarioBean.getUsuarioConectado().getId(), lf, usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.PROCESO_DE_PAGO.getId(), FacturaEstadoEnum.PROCESO_DE_PAGO.getId());
        PrimeFaces.current().executeScript("$(dialogoDatosFacturaHistorial).modal('hide');");

        setListaFactura(siFacturaImpl.traerFacturaPorStatusFecha(
                FacturaEstadoEnum.PROCESO_DE_PAGO.getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                getProvSelected(),
                inicio,
                fin
        ));

    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the rechazarFactura
     */
    public boolean isRechazarFactura() {
        return rechazarFactura;
    }

    /**
     * @param rechazarFactura the rechazarFactura to set
     */
    public void setRechazarFactura(boolean rechazarFactura) {
        this.rechazarFactura = rechazarFactura;
    }

    /**
     * @return the ligaZip
     */
    public String getLigaZip() {
        return ligaZip;
    }

    /**
     * @param ligaZip the ligaZip to set
     */
    public void setLigaZip(String ligaZip) {
        this.ligaZip = ligaZip;
    }

    /**
     * @return the mostrarLiga
     */
    public boolean isMostrarLiga() {
        return mostrarLiga;
    }

    /**
     * @param mostrarLiga the mostrarLiga to set
     */
    public void setMostrarLiga(boolean mostrarLiga) {
        this.mostrarLiga = mostrarLiga;
    }

    public void uploadFile(FileUploadEvent event) {
        fileInfo = event.getFile();
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
            if (addArchivo) {
                procesarArchivo(fileInfo);
                siFacturaStatusImpl.cambiarEstatus(facturaVo.getId(), FacturaEstadoEnum.PROCESO_DE_PAGO.getId(), FacturaEstadoEnum.PAGADA.getId(), usuarioBean.getUsuarioConectado().getId());
                fileInfo.delete();
                consultarListaFacturas();
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

    public void cerrarCargaCP() {
        PrimeFaces.current().executeScript(CP_MODAL_HIDE);
    }

    private void procesarArchivo(UploadedFile file) {
        SiAdjunto adj = guardarAdjunto(file);
        //
        siFacturaAdjuntoImpl.guardar(adj, facturaVo.getId(), TipoArchivoFacturaEnum.TIPO_PAGO.toString(), usuarioBean.getUsuarioConectado().getId());
        siFacturaImpl.guardarComprobante(usuarioBean.getUsuarioConectado().getId(), facturaVo, adj);
        FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");

        PrimeFaces.current().executeScript(CP_MODAL_HIDE);
    }

    private SiAdjunto guardarAdjunto(UploadedFile file) {
        try {
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            DocumentoAnexo documentoAnexo = new DocumentoAnexo(file.getContent());
            documentoAnexo.setRuta(uploadDirectory());
            documentoAnexo.setTipoMime(file.getContentType());
            documentoAnexo.setNombreBase(file.getFileName());
            almacenDocumentos.guardarDocumento(documentoAnexo);            
            return siAdjuntoImpl.save(
                    documentoAnexo.getNombreBase(),
                    new StringBuilder()
                            .append(documentoAnexo.getRuta())
                            .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                    file.getContentType(),
                    file.getSize(),
                    usuarioBean.getUsuarioConectado().getId()
            );

        } catch (SIAException ex) {
            UtilLog4j.log.error(ex);
        }
        return null;
    }

    public String uploadDirectory() {
        return "Proveedor/" + facturaVo.getProveedorRfc() + "/Soportes";

    }
}
