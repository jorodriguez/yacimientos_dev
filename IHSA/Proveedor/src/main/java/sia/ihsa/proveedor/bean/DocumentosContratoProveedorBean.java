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
import javax.annotation.PostConstruct;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
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
import sia.modelo.RhDocumentos;
import sia.modelo.SiAdjunto;
import sia.modelo.contrato.vo.ContratoDocumentoVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.RhConvenioDocumentoVo;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioDocumentoImpl;
import sia.servicios.rh.impl.RhConvenioDocumentosImpl;
import sia.servicios.rh.impl.RhDocumentosImpl;
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
@Named(value = "documentosContratoProveedorBean")
@ViewScoped
public class DocumentosContratoProveedorBean implements Serializable {

    /**
     * Creates a new instance of DocumentosContratoProveedorBean
     */
    public DocumentosContratoProveedorBean() {
    }
    @Inject
    private Sesion sesion;

    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private CvConvenioDocumentoImpl convenioDocumentoImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private RhConvenioDocumentosImpl rhConvenioDocumentosImpl;
    @Inject
    private RhDocumentosImpl rhDocumentosImpl;

    @Getter
    @Setter
    private List<ContratoDocumentoVo> listaDoctos;
    @Getter
    @Setter
    private List<RhConvenioDocumentoVo> doctosRhNoPeriodicos;
    @Getter
    @Setter
    private List<RhConvenioDocumentoVo> doctosRhPeriodicos;
    @Getter
    @Setter
    private RhConvenioDocumentoVo doctoRhPeriodicoVo;

    @Getter
    @Setter
    private List<ContratoVO> contratos;
    @Getter
    @Setter
    private ContratoVO contratoVo;
    @Getter
    @Setter
    private ContratoDocumentoVo contratoDocumentoVo;
    @Getter
    @Setter
    private List<SelectItem> listDoctosPeriodicos;
    @Getter
    @Setter
    private int tipoDoctoSubir; // 0: para los doctos generales del contrato 1- Para los tipos de documentos de de RH
    @Getter
    @Setter
    private int idDoctoRh;

    @PostConstruct
    public void init() {
        tipoDoctoSubir = Constantes.CERO;
        contratos = new ArrayList<ContratoVO>();
        listaDoctos = new ArrayList<ContratoDocumentoVo>();
        doctosRhNoPeriodicos = new ArrayList<RhConvenioDocumentoVo>();
        doctosRhPeriodicos = new ArrayList<RhConvenioDocumentoVo>();
        doctoRhPeriodicoVo = new RhConvenioDocumentoVo();
        //
        listDoctosPeriodicos = new ArrayList<SelectItem>();
        List<RhDocumentos> doctos = rhDocumentosImpl.traerDocumentosPeriodicos();
        for (RhDocumentos docto : doctos) {
            listDoctosPeriodicos.add(new SelectItem(docto.getId(), docto.getNombre()));
        }
        llenarContratos();
    }

    private void llenarContratos() {
        contratos = convenioImpl.traerConvenioMaestroPorProveedorStatus(sesion.getProveedorVo().getIdProveedor(), Constantes.ESTADO_CONVENIO_ACTIVO);
    }

    private void llenarDocumentos() {
        listaDoctos = convenioDocumentoImpl.traerDocumentosPorConvenio(contratoVo.getId());
    }

    private void llenarDocumentosPeriodicos() {
        doctosRhPeriodicos = rhConvenioDocumentosImpl.traerDoctosPeriodicosPorConvenio(contratoVo.getId());
    }

    private void llenarDocumentosNoPeriodicos() {
        doctosRhNoPeriodicos = rhConvenioDocumentosImpl.traerDoctosNoPeriodicosPorConvenio(contratoVo.getId());
    }

    public void seleccionarContrato(int ind) {
        contratoVo = new ContratoVO();
        contratoVo = contratos.get(ind);
        llenarDocumentos();
        llenarDocumentosNoPeriodicos();
        llenarDocumentosPeriodicos();
        //
        PrimeFaces.current().executeScript("$(dialogoDocumentoContrato).modal('show');");
    }

    public void seleccionarDocumento(int ind) {
        contratoDocumentoVo = new ContratoDocumentoVo();
        contratoDocumentoVo = listaDoctos.get(ind);
        //
        PrimeFaces.current().executeScript("$(dialogoCargarDocumentoContrato).modal('show');");
    }

    public void eliminarArchivo(int ind) {
        int idCF = ind;
        convenioDocumentoImpl.quitarArchivoDocumento(sesion.getProveedorVo().getRfc(), listaDoctos.get(idCF).getId());
        //
        llenarDocumentos();
        //
    }

    public void cambiarTipoDoctoPeriodico(AjaxBehaviorEvent event) {
        if (idDoctoRh == 0) {
            llenarDocumentosPeriodicos();
        } else {
            doctosRhPeriodicos = rhConvenioDocumentosImpl.traerDoctosPeriodicosPorConvenioPorDocumento(contratoVo.getId(), idDoctoRh);
        }
    }

    public void nuevoDoctoPeriodico() {
        tipoDoctoSubir = Constantes.UNO;
        doctoRhPeriodicoVo = new RhConvenioDocumentoVo();
        doctoRhPeriodicoVo.setIdConvenio(contratoVo.getId());
        doctoRhPeriodicoVo.setIdPeriodicidad(1);
        PrimeFaces.current().executeScript("$(dialogoCargarDocumentoContrato).modal('show');");
    }

    public void agregarDoctoPeriodico(int ind) {
        doctoRhPeriodicoVo = new RhConvenioDocumentoVo();
        int idCF = ind;
        //
        tipoDoctoSubir = Constantes.UNO;
        doctoRhPeriodicoVo = doctosRhPeriodicos.get(idCF);
        PrimeFaces.current().executeScript("$(dialogoCargarDocumentoContrato).modal('show');");
    }

    public void eliminarDoctoPeriodico(int ind) {
        int idCF = ind;
        rhConvenioDocumentosImpl.quitarArchivo(sesion.getProveedorVo().getRfc(), doctosRhPeriodicos.get(idCF).getId());
        //
        llenarDocumentosPeriodicos();
    }

    public void agregarDoctoNoPeriodico(int ind) {
        doctoRhPeriodicoVo = new RhConvenioDocumentoVo();
        int idCF = ind;
        //
        tipoDoctoSubir = Constantes.UNO;
        doctoRhPeriodicoVo = doctosRhNoPeriodicos.get(idCF);
        PrimeFaces.current().executeScript("$(dialogoCargarDocumentoContrato).modal('show');");
    }

    public void eliminarDoctoNoPeriodico(int ind) {
        int idCF = ind;
        rhConvenioDocumentosImpl.quitarArchivo(sesion.getProveedorVo().getRfc(), doctosRhNoPeriodicos.get(idCF).getId());
        //
        llenarDocumentosNoPeriodicos();
    }

    public void uploadFile(FileUploadEvent fileEvent) {
        
            ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
            try {

                UploadedFile fileInfo = fileEvent.getFile();

                AlmacenDocumentos almacenDocumentos
                        = proveedorAlmacenDocumentos.getAlmacenDocumentos();

                boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

                if (addArchivo) {
                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                    documentoAnexo.setRuta(directorioProve());
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    documentoAnexo.setTipoMime(fileInfo.getContentType());
                    almacenDocumentos.guardarDocumento(documentoAnexo);
                    //
                    SiAdjunto adj = siAdjuntoImpl.save(documentoAnexo.getNombreBase(),
                            new StringBuilder()
                                    .append(documentoAnexo.getRuta())
                                    .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                            fileInfo.getContentType(), fileInfo.getSize(), sesion.getProveedorVo().getRfc());

                    if (adj != null) {
                        if (tipoDoctoSubir == 0) {
                            convenioDocumentoImpl.agregarArchivo(sesion.getProveedorVo().getRfc(), contratoDocumentoVo, adj.getId());
                            llenarDocumentos();
                        } else {
                            if (doctoRhPeriodicoVo.getId() > 0) {
                                rhConvenioDocumentosImpl.agregarArchivo(sesion.getProveedorVo().getRfc(), doctoRhPeriodicoVo, adj.getId());
                            } else {
                                doctoRhPeriodicoVo.setIdDocumento(idDoctoRh);
                                doctoRhPeriodicoVo.setIdAdjunto(adj.getId());
                                rhConvenioDocumentosImpl.guardar(sesion.getProveedorVo().getRfc(), doctoRhPeriodicoVo);
                            }
                            llenarDocumentosPeriodicos();
                            llenarDocumentosNoPeriodicos();
                        }
                    }
                    //
                    PrimeFaces.current().executeScript("$(dialogoCargarDocumentoContrato).modal('hide');");
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

    public String directorioProve() {
        return "CV/Proveedor/" + sesion.getProveedorVo().getRfc();
    }
}
