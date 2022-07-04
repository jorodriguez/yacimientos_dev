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
import javax.faces.view.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.excepciones.SIAException;
import sia.ihsa.admin.Sesion;
import sia.ihsa.utils.FacesUtilsBean;
import sia.modelo.SiAdjunto;
import sia.modelo.proveedor.Vo.ProveedorDocumentoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.proveedor.impl.PvClasificacionArchivoImpl;
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
@Named(value = "documentacionBean")
@ViewScoped
public class DocumentacionBean implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    private PvClasificacionArchivoImpl pvClasificacionArchivoImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    //
    private List<ProveedorDocumentoVO> listaDoctos;
    private ProveedorDocumentoVO proveedorDocumentoVO;

    @PostConstruct
    public void iniciar() {
        listaDoctos = new ArrayList<>();
        listaDoctos = pvClasificacionArchivoImpl.traerArchivoPorProveedor(sesion.getProveedorVo().getIdProveedor());
    }

    public void eliminarArchivo(int ind) {
        
        pvClasificacionArchivoImpl.eliminarArchivo(listaDoctos.get(ind).getId(), listaDoctos.get(ind).getAdjuntoVO().getId(), sesion.getProveedorVo().getRfc());
        listaDoctos.get(ind).setAdjuntoVO(new AdjuntoVO());
//
        listaDoctos = pvClasificacionArchivoImpl.traerArchivoPorProveedor(sesion.getProveedorVo().getIdProveedor());
    }

    public void agregarArchivo(int ind) {
        proveedorDocumentoVO = new ProveedorDocumentoVO();
        
        proveedorDocumentoVO = listaDoctos.get(ind);
        //
        FacesUtilsBean.addErrorMessage("frmDocArch:fileEntryDoc", "");
        //
        PrimeFaces.current().executeScript("$(dialogoDocumentacion).modal('show');");

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
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                almacenDocumentos.guardarDocumento(documentoAnexo);
                //
                SiAdjunto adj = siAdjuntoImpl.save(documentoAnexo.getNombreBase(),
                        new StringBuilder()
                                .append(documentoAnexo.getRuta())
                                .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                        fileInfo.getContentType(), fileInfo.getSize(), sesion.getProveedorVo().getRfc());

                if (adj != null) {
                    pvClasificacionArchivoImpl.agregarArchivo(sesion.getProveedorVo().getRfc(), proveedorDocumentoVO, adj.getId());
                }
                listaDoctos = pvClasificacionArchivoImpl.traerArchivoPorProveedor(sesion.getProveedorVo().getIdProveedor());
                //
                PrimeFaces.current().executeScript("$(dialogoDocumentacion).modal('hide');");
                FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

            //
            listaDoctos = pvClasificacionArchivoImpl.traerArchivoPorProveedor(sesion.getProveedorVo().getIdProveedor());

        } catch (IOException | SIAException e) {
            UtilLog4j.log.error(e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }

    }

    public String directorioProve() {
        return "CV/Proveedor/" + File.separator + sesion.getProveedorVo().getRfc();
    }

    /**
     * @return the listaDoctos
     */
    public List<ProveedorDocumentoVO> getListaDoctos() {
        return listaDoctos;
    }

    /**
     * @param listaDoctos the listaDoctos to set
     */
    public void setListaDoctos(List<ProveedorDocumentoVO> listaDoctos) {
        this.listaDoctos = listaDoctos;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the proveedorDocumentoVO
     */
    public ProveedorDocumentoVO getProveedorDocumentoVO() {
        return proveedorDocumentoVO;
    }

    /**
     * @param proveedorDocumentoVO the proveedorDocumentoVO to set
     */
    public void setProveedorDocumentoVO(ProveedorDocumentoVO proveedorDocumentoVO) {
        this.proveedorDocumentoVO = proveedorDocumentoVO;
    }

}
