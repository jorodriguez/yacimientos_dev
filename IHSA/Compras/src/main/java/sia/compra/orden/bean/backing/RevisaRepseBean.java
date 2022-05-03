/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;

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
import sia.modelo.SiAdjunto;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.orden.vo.OrdenEtsVo;
import sia.modelo.proveedor.Vo.ProveedorDocumentoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.servicios.convenio.impl.CvConvenioAdjuntoImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.PvClasificacionArchivoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
@Named(value = "revisaRepseBean")
@ViewScoped
public class RevisaRepseBean implements Serializable {

    /**
     * Creates a new instance of RevisaRepseBean
     */
    public RevisaRepseBean() {
    }
    //Sistema
    @Inject
    private UsuarioBean sesion;

    @Inject
    OrdenImpl ordenImpl;
    @Inject
    AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    OcOrdenEtsImpl ordenEtsImpl;
    @Inject
    PvClasificacionArchivoImpl clasificacionArchivoImpl;
    @Inject
    CvConvenioAdjuntoImpl convenioAdjuntoImpl;
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;

    @Getter
    @Setter
    private List<OrdenVO> comprasConRepse;
    @Getter
    @Setter
    private List<OrdenVO> comprasSinRepse;
    @Getter
    @Setter
    private List<OrdenEtsVo> doctos;
    @Getter
    @Setter
    private List<OrdenDetalleVO> items;
    @Getter
    @Setter
    private List<ProveedorDocumentoVO> documentosProveedor;
    @Getter
    @Setter
    private List<ContratoVO> archivosContrato;
    @Getter
    @Setter
    private OrdenVO ordenVo;
    @Getter
    @Setter
    private String motivo;
    @Getter
    @Setter
    private List<OrdenEtsVo> archivosRepse;
    @Getter
    @Setter
    private ProveedorDocumentoVO proveedorDocumentoVo;
    @Getter
    @Setter
    private ProveedorDocumentoVO documentoVo;
    @Getter
    @Setter
    private AdjuntoVO adjuntoVo;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    @PostConstruct
    public void init() {
        comprasConRepse = new ArrayList<>();
        comprasSinRepse = new ArrayList<>();
        items = new ArrayList<>();
        doctos = new ArrayList<>();
        archivosRepse = new ArrayList<>();
        archivosContrato = new ArrayList<>();
        documentosProveedor = new ArrayList<>();
        llenarCompras();
        ordenVo = new OrdenVO();
    }

    private void llenarCompras() {
        comprasConRepse = new ArrayList<>();
        comprasSinRepse = new ArrayList<>();
        List<OrdenVO> ordenes = autorizacionesOrdenImpl.traerOrdenStatusCampo(OrdenEstadoEnum.POR_REVISAR_REPSE.getId(),
                sesion.getUsuarioConectado().getApCampo().getId());
        for (OrdenVO ordene : ordenes) {
            if (ordene.isRepse()) {
                comprasConRepse.add(ordene);
            } else {
                comprasSinRepse.add(ordene);
            }
        }
    }

    public void aceptarRepseSinRepse() {
        List<OrdenVO> temp = new ArrayList<>();
        for (OrdenVO compra : comprasSinRepse) {
            if (compra.isSelected()) {
                temp.add(compra);
            }
        }
        if (!temp.isEmpty()) {
            for (OrdenVO ordenVO : temp) {
                ordenImpl.aceptarRepse(ordenVO.getId(), null, sesion.getUsuarioConectado().getId(), Boolean.TRUE);
            }
            llenarCompras();
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario seleccionar una compra");
        }
    }

    public void seleccionarSinCompra(int ind) {
        ordenVo = comprasSinRepse.get(ind);
        //
        items = ordenImpl.itemsPorOrdenCompra(ordenVo.getId());
        doctos = ordenEtsImpl.traerEtsPorOrdenCategoria(ordenVo.getId());
        //
        archivosContrato = convenioAdjuntoImpl.traerPorConvenioPorNumero(ordenVo.getNumeroContrato());
        //
        documentosProveedor = clasificacionArchivoImpl.traerArchivoPorProveedor(ordenVo.getIdProveedor());
        //      
        documentoVo = new ProveedorDocumentoVO();
        llenarRepse();
        //
        llenarDoctoActa();
        PrimeFaces.current().executeScript("$(dialogoDatosCompra).modal('show');");
    }

    private void llenarRepse() {
        archivosRepse = new ArrayList<OrdenEtsVo>();
        adjuntoVo = new AdjuntoVO();
        if (doctos != null) {
            for (OrdenEtsVo docto : doctos) {
                if (docto.getIdTabla() == Constantes.OCS_CATEGORIA_REPSE) {
                    archivosRepse.add(docto);
                    adjuntoVo.setId(docto.getId());
                    adjuntoVo.setUuid(docto.getUuid());
                }
            }
        }
    }

    public void aceptarCompraSinRepse(int ind) {
        ordenVo = comprasSinRepse.get(ind);
        //
        ordenImpl.aceptarRepse(ordenVo.getId(), null, sesion.getUsuarioConectado().getId(), Boolean.TRUE);
        //
        comprasSinRepse.remove(ind);
    }

    public void inicioRechazarSinRepse(int ind) {
        ordenVo = new OrdenVO();
        ordenVo = comprasSinRepse.get(ind);
        //
        PrimeFaces.current().executeScript("$(dialogoRechazarRepse).modal('show');");

    }

    public void seleccionarCompra(int ind) {
        ordenVo = comprasConRepse.get(ind);
        //
        items = ordenImpl.itemsPorOrdenCompra(ordenVo.getId());
        doctos = ordenEtsImpl.traerEtsPorOrdenCategoria(ordenVo.getId());
        //
        archivosContrato = convenioAdjuntoImpl.traerPorConvenioPorNumero(ordenVo.getNumeroContrato());
        //
        documentosProveedor = clasificacionArchivoImpl.traerArchivoPorProveedor(ordenVo.getIdProveedor());
        //
        documentoVo = new ProveedorDocumentoVO();
        //
        llenarRepse();
        //
        llenarDoctoActa();
        PrimeFaces.current().executeScript("$(dialogoDatosCompra).modal('show');");
    }

    private void llenarDoctoActa() {
        //
        proveedorDocumentoVo = new ProveedorDocumentoVO();
        if (documentosProveedor != null) {
            for (ProveedorDocumentoVO pDocVo : documentosProveedor) {
                if (pDocVo.getIdDocumento() == 13) {
                    proveedorDocumentoVo.setDocumento(pDocVo.getDocumento());
                    proveedorDocumentoVo.setAdjuntoVO(new AdjuntoVO());
                    proveedorDocumentoVo.getAdjuntoVO().setId(pDocVo.getAdjuntoVO().getId());
                    proveedorDocumentoVo.getAdjuntoVO().setUuid(pDocVo.getAdjuntoVO().getUuid());
                    break;
                }
            }
        }
    }

    public void mostrarDoctoProveedor(int adjId, String adjUuid, String docto) {
        documentoVo = new ProveedorDocumentoVO();
        //
        if (docto.endsWith(".pdf")) {
            documentoVo.setAdjuntoVO(new AdjuntoVO());
            documentoVo.getAdjuntoVO().setId(adjId);
            documentoVo.getAdjuntoVO().setUuid(adjUuid);
            documentoVo.setDocumento(docto);
        } else {
            FacesUtilsBean.addErrorMessage("Solo se pueden mostrar arhivos con extensión pdf");
        }
    }

    public void aceptarRepse() {
        List<OrdenVO> temp = new ArrayList<>();
        for (OrdenVO compra : comprasConRepse) {
            if (compra.isSelected()) {
                temp.add(compra);
            }
        }
        if (!temp.isEmpty()) {
            for (OrdenVO ordenVO : temp) {
                ordenImpl.aceptarRepse(ordenVO.getId(), null, sesion.getUsuarioConectado().getId(), Boolean.TRUE);
            }
            llenarCompras();
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario seleccionar una compra");
        }
    }

    public void aceptarCompraConRepse(int ind) {
        ordenVo = comprasConRepse.get(ind);
        //
        ordenImpl.aceptarRepse(ordenVo.getId(), null, sesion.getUsuarioConectado().getId(), Boolean.TRUE);
        //
        comprasConRepse.remove(ind);
    }

    public void inicioRechazarRepse(int ind) {
        ordenVo = new OrdenVO();
        ordenVo = comprasConRepse.get(ind);
        //
        PrimeFaces.current().executeScript("$(dialogoRechazarRepse).modal('show');");

    }

    public void rechazarRepse() {
        ordenImpl.rechazarRepse(ordenVo.getId(), null, sesion.getUsuarioConectado().getEmail(), motivo, sesion.getUsuarioConectado().getId());
        llenarCompras();
        //
        PrimeFaces.current().executeScript("$(dialogoRechazarRepse).modal('hide');");
    }

    public void agregarArchivoRepse(int ind) {
        ordenVo = new OrdenVO();
        ordenVo = comprasConRepse.get(ind);
        //
        PrimeFaces.current().executeScript("$(dialogoSubirRepse).modal('show');");
    }

    public void uploadFile(FileUploadEvent fileUploadEvent) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            fileInfo = fileUploadEvent.getFile();
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setRuta(directorioProve());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                almacenDocumentos.guardarDocumento(documentoAnexo);
                //
                SiAdjunto adj = siAdjuntoImpl.save(documentoAnexo.getNombreBase(),
                        new StringBuilder()
                                .append(documentoAnexo.getRuta())
                                .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                        fileInfo.getContentType(), fileInfo.getSize(), sesion.getUsuarioConectado().getId());
                //
                ordenEtsImpl.crearOcOrdenEts(ordenVo.getId(), Constantes.OCS_CATEGORIA_REPSE, adj, sesion.getUsuarioConectado());
                //
                PrimeFaces.current().executeScript("$(dialogoSubirRepse).modal('hide');");
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

    public void abriraArchivo(int ind) {
        try {
            adjuntoVo = new AdjuntoVO();
            if (ind > 0) {
                //
                OrdenEtsVo etV = archivosRepse.get(ind);
                if (etV.getNombre().endsWith(".pdf")) {
                    adjuntoVo.setId(etV.getId());
                    adjuntoVo.setUuid(etV.getUuid());
                    PrimeFaces.current().executeScript("$(dialogoAbrirArchivo).modal('show');");
                } else {
                    FacesUtilsBean.addErrorMessage("Solo se pueden abrir archivos pdf.");
                    //PrimeFaces.current().executeScript(;alertaGeneral('" + "Solo se pueden abrir archivos pdf." + "');");
                }
            }
        } catch (NumberFormatException ex) {
            Logger.getLogger(RevisaRepseBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cerrarAbriraArchivo() {
        try {
            adjuntoVo = new AdjuntoVO();
            PrimeFaces.current().executeScript("$(dialogoAbrirArchivo).modal('hide');");
        } catch (Exception ex) {
            Logger.getLogger(RevisaRepseBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String directorioProve() {
        return new StringBuilder().append("ETS/Orden")
                .append(ordenVo.getId()).toString();
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
    }

}
