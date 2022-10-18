/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.factura.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SiAdjunto;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.vo.FacturaAdjuntoVo;
import sia.modelo.sistema.vo.FacturaContenidoNacionalVo;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.vo.inventarios.ArticuloVO;
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
 * @author mluis
 */
@Named(value = "facturaBean")
@ViewScoped
public class FacturaBean implements Serializable {

    /**
     * Creates a new instance of FacturaBean
     */
    public FacturaBean() {
    }

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
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;
    //
    private List<FacturaVo> listaFactura;
    private List<FacturaVo> listaNotaCredito;
    private List<FacturaContenidoNacionalVo> contenidoNacional;
    private String motivo;
    private List<FacturaAdjuntoVo> listaArchivosFactura;
    private FacturaVo facturaVo;
    private String tituloPopup;
    private int tipoCargaArchivo;
    private List<FacturaVo> listaFacturaCNH;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    @PostConstruct
    public void iniciar() {
        facturaVo = new FacturaVo();

        listaFactura = siFacturaImpl.traerFacturaPorStatus(FacturaEstadoEnum.ENVIADA_CLIENTE.getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO, usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());
        setListaFacturaCNH(new ArrayList<FacturaVo>());//siFacturaImpl.traerFacturaPorStatus(FacturaEstadoEnum.PROCESO_DE_PAGO.getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO, usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc()));
    }

    public void seleccionarFactura(SelectEvent<FacturaVo> event) {
        facturaVo = (FacturaVo) event.getObject();
        listaArchivosFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
        //
        facturaVo.setDetalleFactura(siFacturaDetalleImpl.detalleFactura(facturaVo.getId()));
        //
        PrimeFaces.current().executeScript("$(dialogoDatosFactura).modal('show');");
        //
        siFacturaImpl.marcarLeida(usuarioBean.getUsuarioConectado().getId(), facturaVo);
        facturaVo.setLeida(Constantes.BOOLEAN_TRUE);
        //
        listaNotaCredito = siFacturaImpl.traerNotaCredito(facturaVo.getId());
        //
        contenidoNacional = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(facturaVo.getId());
        //
        FacturaAdjuntoVo facAdVo = new FacturaAdjuntoVo();
        facAdVo.setId(0);
        facAdVo.setTipo("Orden compra");
        facAdVo.setAdjuntoVo(new AdjuntoVO());
        facAdVo.getAdjuntoVo().setId(facturaVo.getIdAdjunto());
        facAdVo.getAdjuntoVo().setNombre(facturaVo.getCodigoOrden());
        //
        listaArchivosFactura.add(facAdVo);
    }

    public void seleccionarFacturaPago(SelectEvent event) {
        facturaVo = (FacturaVo) event.getObject();
        listaArchivosFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
        //
        facturaVo.setDetalleFactura(siFacturaDetalleImpl.detalleFactura(facturaVo.getId()));
        //
        siFacturaImpl.marcarLeida(usuarioBean.getUsuarioConectado().getId(), facturaVo);
        facturaVo.setLeida(Constantes.BOOLEAN_TRUE);
        //
        listaNotaCredito = siFacturaImpl.traerNotaCredito(facturaVo.getId());
        //
        contenidoNacional = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(facturaVo.getId());
        PrimeFaces.current().executeScript(";activarTab('tabOCSProcComprasLi', 0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');");
    }

    public void aceptarFactura() {
        List<FacturaVo> lTemp = new ArrayList<>();
        for (FacturaVo fvo : listaFactura) {
            if (fvo.isSelected()) {
                lTemp.add(fvo);
            }
        }
        if (!lTemp.isEmpty()) {
            aceptarCCN(lTemp);
        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos una factura.");
        }

    }

    public void enviarFacturaCNH() {
        if (facturaVo != null && facturaVo.getId() > 0) {
            if (siFacturaImpl.isEnviarCNH(facturaVo.getId())) {
                List<FacturaVo> lTemp = new ArrayList<>();
                lTemp.add(facturaVo);
                enviarFacturaCNH(lTemp);
                facturaVo = null;
                PrimeFaces.current().executeScript("resetTabs();regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
            } else {
                FacesUtilsBean.addErrorMessage("Se debe capturar el No. póliza de registro. Y cargar un archivo de pago y otro de complemento de pago.");
            }
        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos una factura.");
        }

    }

    public void inicioDevolverFactura() {
        List<FacturaVo> lTemp = new ArrayList<>();
        for (FacturaVo fvo : listaFactura) {
            if (fvo.isSelected()) {
                lTemp.add(fvo);
            }
        }
        if (!lTemp.isEmpty()) {
            motivo = "";
            PrimeFaces.current().executeScript("$(dialogoDevFac).modal('show');");
        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos una factura.");
        }

    }

    public void aceptarFacturaSeleccionada() {
        List<FacturaVo> lf = new ArrayList<>();
        lf.add(facturaVo);
        //
        aceptarCCN(lf);
        //
        PrimeFaces.current().executeScript("$(dialogoDatosFactura).modal('hide');");
    }

    private void aceptarCCN(List<FacturaVo> lf) {
        siFacturaImpl.aceptarCCN(usuarioBean.getUsuarioConectado().getId(), lf, usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.ENVIADA_CLIENTE.getId(), FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId());
        //
        listaFactura = siFacturaImpl.traerFacturaPorStatus(FacturaEstadoEnum.ENVIADA_CLIENTE.getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO, usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());
        //
        ContarBean cb = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        cb.llenarFacturaSinProcesar("totalFacSinProc", FacturaEstadoEnum.ENVIADA_CLIENTE.getId());
    }

    private void enviarFacturaCNH(List<FacturaVo> lf) {
        siFacturaImpl.pagarFactura(usuarioBean.getUsuarioConectado().getId(), lf, usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.PROCESO_DE_PAGO.getId(), FacturaEstadoEnum.PAGADA.getId());
        listaFacturaCNH = new ArrayList<>();//siFacturaImpl.traerFacturaPorStatus(FacturaEstadoEnum.PROCESO_DE_PAGO.getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO, usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());        
        ContarBean cb = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        cb.llenarFacturaSinProcesar("totalFacSinEnviar", FacturaEstadoEnum.PROCESO_DE_PAGO.getId());
    }

    public void devolverFactura() {
        if (!motivo.isEmpty()) {
            siFacturaStatusImpl.rechazarCCN(usuarioBean.getUsuarioConectado().getId(), facturaVo, motivo, usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.ENVIADA_CLIENTE.getId());
            motivo = "";
            PrimeFaces.current().executeScript("$(dialogoDatosFactura).modal('hide');");
            //
            listaFactura = siFacturaImpl.traerFacturaPorStatus(FacturaEstadoEnum.ENVIADA_CLIENTE.getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO, usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario agregar el motivo.");
        }

    }

    public void cerrarDevolverFactura() {
        motivo = "";
        PrimeFaces.current().executeScript("$(dialogoDevFac).modal('hide');");
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

    public void agregarPoliza() {
        if (facturaVo != null && facturaVo.getId() > 0) {
            siFacturaImpl.guardarPoliza(usuarioBean.getUsuarioConectado().getId(), facturaVo);
            listaFactura = siFacturaImpl.traerFacturaPorStatus(FacturaEstadoEnum.ENVIADA_CLIENTE.getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO, usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());
            String mtd = "ocultarDiv('dvPolizaMod" + facturaVo.getId() + "'); mostrarDiv('dvPoliza" + facturaVo.getId() + "');";
            PrimeFaces.current().executeScript(mtd);
        }
    }

    public void agregarPolizaPago() {
        if (facturaVo != null && facturaVo.getId() > 0) {
            siFacturaImpl.guardarPolizaPago(usuarioBean.getUsuarioConectado().getId(), facturaVo);
            listaFactura = siFacturaImpl.traerFacturaPorStatus(FacturaEstadoEnum.ENVIADA_CLIENTE.getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO, usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());
            String mtd = "ocultarDiv('dvPolizaModPago" + facturaVo.getId() + "'); mostrarDiv('dvPolizaPago" + facturaVo.getId() + "');";
            PrimeFaces.current().executeScript(mtd);
        }
    }

    public void uploadFile(FileUploadEvent event) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            fileInfo = event.getFile();
            if (facturaVo != null && facturaVo.getId() > 0) {
                SiAdjunto adj;
                boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
                if (addArchivo) {
                    String tipo = "COMPLEMENTO";
                    String ruta = "";
                    switch (this.getTipoCargaArchivo()) {
                        case 1:
                            tipo = TipoArchivoFacturaEnum.TIPO_PAGO.toString();
                            ruta = "Proveedor/" + facturaVo.getProveedorRfc() + "/Complemento";
                            break;
                        case 2:
                            tipo = TipoArchivoFacturaEnum.TIPO_COMPLEMENTO.toString();
                            ruta = "Proveedor/" + facturaVo.getProveedorRfc() + "/Pago";
                            break;
                        default:
                            tipo = TipoArchivoFacturaEnum.TIPO_COMPLEMENTO.toString();
                            ruta = "Proveedor/" + facturaVo.getProveedorRfc() + "/Complemento";
                            break;
                    }
                    adj = guardarAdjunto(fileInfo, ruta);

                    siFacturaAdjuntoImpl.guardar(adj, facturaVo.getId(), tipo, usuarioBean.getUsuarioConectado().getId());
                    listaArchivosFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
                    FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");

                    PrimeFaces.current().executeScript(";$(dialogoArchivoComplemento).modal('hide');");

                    fileInfo.delete();
                } else {
                    FacesUtilsBean.addErrorMessage(new StringBuilder()
                            .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                            .append(validadorNombreArchivo.getCaracteresNoValidos())
                            .toString());
                }
            } else {
                FacesUtilsBean.addErrorMessage("Es necesario seleccionar un archivo ");
            }

        } catch (Exception e) {
            UtilLog4j.log.error(e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    private SiAdjunto guardarAdjunto(UploadedFile fileUpload, String ruta) {
        try {
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileUpload.getContent());
            documentoAnexo.setRuta(ruta);
            documentoAnexo.setTipoMime(fileUpload.getContentType());
            documentoAnexo.setNombreBase(fileUpload.getFileName());
            almacenDocumentos.guardarDocumento(documentoAnexo);
            return siAdjuntoImpl.save(
                    documentoAnexo.getNombreBase(),
                    new StringBuilder()
                            .append(documentoAnexo.getRuta())
                            .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                    fileUpload.getContentType(),
                    fileUpload.getSize(),
                    usuarioBean.getUsuarioConectado().getId());
        } catch (SIAException ex) {
            Logger.getLogger(FacturaBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void cerrarCargaComplemento() {
        PrimeFaces.current().executeScript(";$(dialogoArchivoComplemento).modal('hide');");
    }

    public void inicioCargaComplemento() {
        FacesUtilsBean.addErrorMessage("frmArchivoFact:file-entry", "");
        this.setTituloPopup("Cargar el archivo del Complemento de pago");
        this.setTipoCargaArchivo(2);
        PrimeFaces.current().executeScript(";$(dialogoArchivoComplemento).modal('show');");
    }

    public void inicioCargaPago() {
        FacesUtilsBean.addErrorMessage("frmArchivoFact:file-entry", "");
        this.setTituloPopup("Cargar el archivo del pago");
        this.setTipoCargaArchivo(1);
        PrimeFaces.current().executeScript(";$(dialogoArchivoComplemento).modal('show');");
    }

    public void eliminarComplementoPago() {
        int idFacSop = Integer.parseInt(FacesUtilsBean.getRequestParameter("idFacturaSoporte"));
        if (idFacSop > 0) {
            siFacturaAdjuntoImpl.eliminar(idFacSop, usuarioBean.getUsuarioConectado().getId());
            listaArchivosFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), Constantes.BOOLEAN_TRUE);
        }

    }

    /**
     * @return the tituloPopup
     */
    public String getTituloPopup() {
        return tituloPopup;
    }

    /**
     * @param tituloPopup the tituloPopup to set
     */
    public void setTituloPopup(String tituloPopup) {
        this.tituloPopup = tituloPopup;
    }

    /**
     * @return the tipoCargaArchivo
     */
    public int getTipoCargaArchivo() {
        return tipoCargaArchivo;
    }

    /**
     * @param tipoCargaArchivo the tipoCargaArchivo to set
     */
    public void setTipoCargaArchivo(int tipoCargaArchivo) {
        this.tipoCargaArchivo = tipoCargaArchivo;
    }

    /**
     * @return the listaFacturaCNH
     */
    public List<FacturaVo> getListaFacturaCNH() {
        return listaFacturaCNH;
    }

    /**
     * @param listaFacturaCNH the listaFacturaCNH to set
     */
    public void setListaFacturaCNH(List<FacturaVo> listaFacturaCNH) {
        this.listaFacturaCNH = listaFacturaCNH;
    }
}
