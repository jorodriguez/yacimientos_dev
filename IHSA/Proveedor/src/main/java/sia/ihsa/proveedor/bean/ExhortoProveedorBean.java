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


import javax.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.excepciones.SIAException;
import sia.ihsa.admin.Sesion;
import sia.ihsa.utils.FacesUtilsBean;
import sia.modelo.CvFormas;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioExhortoImpl;
import sia.servicios.convenio.impl.CvFormasImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;
import org.primefaces.PrimeFaces;
import javax.inject.Named;
import javax.inject.Inject;

/**
 *
 * @author mluis
 */
@Named(value = "exhortoProveedorBean")
@ViewScoped
public class ExhortoProveedorBean implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    private CvConvenioExhortoImpl convenioExhortoImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private CvFormasImpl formasImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiAdjuntoImpl adjuntoImpl;
    //
    @Getter
    @Setter
    private List<ContratoVO> contratos;
    @Getter
    @Setter
    private List<CvFormas> formas;
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
    private UploadedFile fileInfo;

    @PostConstruct
    public void iniciar() {
        contratoVo = new ContratoVO();
        formas = new ArrayList<CvFormas>();
        contratos = new ArrayList<ContratoVO>();
        llenarContratos();
        adjuntoVo = new AdjuntoVO();
        formas = formasImpl.formasProveedor();
        for (CvFormas forma : formas) {
            if (forma.getNombre().contains("SOLICITUD")) {
                adjuntoVo.setId(forma.getSiAdjunto().getId());
                adjuntoVo.setNombre(forma.getSiAdjunto().getNombre());
                adjuntoVo.setUrl(forma.getSiAdjunto().getUrl());
                adjuntoVo.setUuid(forma.getSiAdjunto().getUuid());
                break;
            }
        }
    }

    private void llenarContratos() {
        contratos = convenioExhortoImpl.traerExhortosPorProveedor(sesion.getProveedorVo().getIdProveedor());
    }

    public void iniciarProceso(String cod) {
        contratoVo = convenioImpl.traerConveniosPorCodigo(cod);
        //
        PrimeFaces.current().executeScript("$(dialogoIniciar).modal('show');");
    }

    public void uploadFile(FileUploadEvent fileEvent) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            fileInfo = fileEvent.getFile();
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setRuta(directorioProve());
                AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                almacenDocumentos.guardarDocumento(documentoAnexo);

                FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
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
    
    public String getUploadDirectoryOrden() {
        return new StringBuilder().append("Proveedor/Exhorto/").toString();
    }

    public void cancelarEnviarSolicitud() {
        documentoAnexo = null;
        PrimeFaces.current().executeScript("$(dialogoIniciar).modal('hide');");
    }

    public void enviarSolicitud() {
        try {
            if (documentoAnexo != null) {
                AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
                almacenDocumentos.guardarDocumento(documentoAnexo);
                //
                convenioExhortoImpl.enviarSolicitudFiniquito(sesion.getProveedorVo(), buildAdjuntoVO(documentoAnexo), contratoVo);
                //
                llenarContratos();
                documentoAnexo = null;
                PrimeFaces.current().executeScript("$(dialogoIniciar).modal('hide');");
            } else {
                FacesUtilsBean.addErrorMessage("Es necesario agregar la solicitud de finiquito");
            }
        } catch (SIAException ex) {
            Logger.getLogger(ExhortoProveedorBean.class.getName()).log(Level.SEVERE, null, ex);
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
        return "CV/Proveedor" + File.separator + sesion.getProveedorVo().getRfc() + File.separator + "finiquito" + File.separator;
    }

    public void eliminarSolicitud() {
        documentoAnexo = null;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
