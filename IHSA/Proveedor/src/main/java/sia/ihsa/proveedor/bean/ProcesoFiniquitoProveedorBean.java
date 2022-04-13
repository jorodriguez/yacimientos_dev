/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.proveedor.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.ihsa.admin.Sesion;
import sia.ihsa.utils.FacesUtilsBean;
import sia.modelo.contrato.vo.ContratoFormasNotasVo;
import sia.modelo.contrato.vo.ContratoFormasVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.vo.FacturaVo;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioFormasImpl;
import sia.servicios.convenio.impl.CvConvenioFormasNotasImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;
import org.primefaces.PrimeFaces;
import javax.inject.Named;
import javax.inject.Inject;

/**
 *
 * @author mluis
 */
@Named(value = "procesoFiniquitoProveedorBean")
@ViewScoped
public class ProcesoFiniquitoProveedorBean implements Serializable {

    @ManagedProperty("#{sesion}")
    private Sesion sesion;

    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private CvConvenioFormasImpl convenioFormasImpl;
    @Inject
    private CvConvenioFormasNotasImpl convenioFormasNotasImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiAdjuntoImpl adjuntoImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private SiFacturaImpl facturaImpl;
    //
    @Getter
    @Setter
    private List<ContratoVO> contratos;
    @Getter
    @Setter
    private List<ContratoFormasVo> contratoFormas;
    @Getter
    @Setter
    private ContratoVO contratoVo;
    @Getter
    @Setter
    private DocumentoAnexo documentoAnexo;
    @Getter
    @Setter
    private AdjuntoVO adjuntoVo;
    @Getter
    @Setter
    private ContratoFormasVo contratoFormasVo;
    @Getter
    @Setter
    private List<ContratoFormasNotasVo> contratoFormasNotas;
    @Getter
    @Setter
    private List<OrdenVO> comprasPorContrato;
    @Getter
    @Setter
    private List<FacturaVo> facturasProveedor;

    @PostConstruct
    public void iniciar() {
        contratoFormasVo = new ContratoFormasVo();
        contratoVo = new ContratoVO();
        contratos = new ArrayList<ContratoVO>();
        contratoFormas = new ArrayList<ContratoFormasVo>();
        contratoFormasNotas = new ArrayList<ContratoFormasNotasVo>();
        comprasPorContrato = new ArrayList<OrdenVO>();
        facturasProveedor = new ArrayList<FacturaVo>();
        llenarContratos();
        adjuntoVo = new AdjuntoVO();
    }

    private void llenarContratos() {
        contratos = convenioImpl.traerConvenioMaestroPorProveedorStatus(sesion.getProveedorVo().getIdProveedor(), Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO);
    }

    private void llenarContratoFormas() {
        contratoFormas = convenioFormasImpl.traerFormasPorConvenio(contratoVo.getId());
    }

    public void administrar(ActionEvent event) {
        String cod = FacesUtilsBean.getRequestParameter("convnum");
        contratoVo = convenioImpl.traerConveniosPorCodigo(cod);
        //
        llenarContratoFormas();
        comprasPorContrato = ordenImpl.traerOCSPorContrato(contratoVo.getId(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId(), contratoVo.getIdCampo());
        facturasProveedor = facturaImpl.traerFacturaPorProveedor(contratoVo.getProveedor(), contratoVo.getFechaInicio(), contratoVo.getFechaVencimiento(), null, 0);
        contratoFormasNotas = new ArrayList<ContratoFormasNotasVo>();
        PrimeFaces.current().executeScript("$(dialogoProceso).modal('show');");
    }

    public void mostrarNotas(ActionEvent event) {
        int indFor = Integer.parseInt(FacesUtilsBean.getRequestParameter("indice"));
        contratoFormasNotas = convenioFormasNotasImpl.traerNotasPorContratoForma(contratoFormas.get(indFor).getId());
        PrimeFaces.current().executeScript("$(dialogoNotas).modal('show');");
    }

    public void subirArchivo(ActionEvent event) {
        int indFor = Integer.parseInt(FacesUtilsBean.getRequestParameter("indice"));
        contratoFormasVo = contratoFormas.get(indFor);
        PrimeFaces.current().executeScript("$(dialogoSubirFormas).modal('show');");
    }

    public void eliminarArchivoForma(ActionEvent event) {
        int indFor = Integer.parseInt(FacesUtilsBean.getRequestParameter("indice"));
        //
        convenioFormasImpl.eliminarArchivo(sesion.getProveedorVo().getRfc(), contratoFormas.get(indFor));
        contratoFormas.get(indFor).setIdAdjunto(0);
        contratoFormas.get(indFor).setAdjunto("");
        contratoFormas.get(indFor).setUuIdAdjunto("");

    }

    public void notificarForma(ActionEvent event) {
        int indFor = Integer.parseInt(FacesUtilsBean.getRequestParameter("indice"));
        //
        contratoFormas.get(indFor).setIdCampo(contratoVo.getIdCampo());
        convenioFormasImpl.notificarForma(sesion.getProveedorVo().getRfc(), contratoFormas.get(indFor));

    }

    public void uploadFile(FileUploadEvent fileEvent) {

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            UploadedFile fileInfo = fileEvent.getFile();

            //
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
            if (addArchivo) {
                String forma_nombre;
                forma_nombre = contratoFormasVo.getFormaCodigo() + "-" + contratoFormasVo.getRfcProveedor() + "-" + fileInfo.getFileName();
                documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setNombreBase(forma_nombre);
                documentoAnexo.setRuta(directorioProve());
                AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                almacenDocumentos.guardarDocumento(documentoAnexo);
                //                
                convenioFormasImpl.agregarArchivo(sesion.getProveedorVo().getRfc(), buildAdjuntoVO(documentoAnexo), contratoFormasVo);
                llenarContratoFormas();
                FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
                PrimeFaces.current().executeScript("$(dialogoSubirFormas).modal('hide');");
            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

        } catch (IOException | SIAException e) {
            UtilLog4j.log.error(e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }

    }

    public void cancelarEnviarSolicitud(ActionEvent event) {
        contratoFormasVo = new ContratoFormasVo();
        llenarContratos();
        PrimeFaces.current().executeScript("$(dialogoProceso).modal('hide');");
    }

    public void enviarSolicitud(ActionEvent event) {
        try {
            //
            boolean enviar = true;
            for (ContratoFormasVo contratoForma : contratoFormas) {
                if (contratoForma.getIdAdjunto() == 0 && contratoForma.isValidado()) {
                    enviar = false;
                    break;
                }
            }
            if (enviar) {
                llenarContratos();
                //
                convenioImpl.actualizarStatus(sesion.getProveedorVo().getRfc(), contratoVo, Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO_FINALIZADO);
                //notificar

                PrimeFaces.current().executeScript("$(dialogoProceso).modal('hide');");
            } else {
                FacesUtilsBean.addErrorMessage("Es necesario agregar todas las formas y validarlas antes de procesar el contrato.");
            }
        } catch (Exception ex) {
            Logger.getLogger(ProcesoFiniquitoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private AdjuntoVO buildAdjuntoVO(DocumentoAnexo documentoAnexo) {
        AdjuntoVO adjunto = new AdjuntoVO();
        adjunto.setUrl(documentoAnexo.getRuta() + documentoAnexo.getNombreBase());
        adjunto.setNombre(documentoAnexo.getNombreBase());
        adjunto.setTipoArchivo(documentoAnexo.getTipoMime());
        adjunto.setTamanio(documentoAnexo.getTamanio());

        return adjunto;
    }

    public String directorioProve() {
        return "CV/Proveedor" + File.separator + sesion.getProveedorVo().getRfc()
                + File.separator + "finiquito" + File.separator + contratoVo.getNumero() + File.separator;
    }

    public void eliminarSolicitud(AjaxBehaviorEvent event) {
        documentoAnexo = null;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
