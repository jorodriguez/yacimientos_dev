/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import sia.contrato.bean.soporte.FacesUtils;
import sia.excepciones.SIAException;
import sia.ihsa.contratos.Sesion;
import sia.modelo.CvFormas;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.servicios.convenio.impl.CvFormasImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class FormasBean implements Serializable {

    /**
     * Creates a new instance of FormasBean
     */
    public FormasBean() {
    }

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    //
    @Inject
    private CvFormasImpl formasLocal;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    //
    @Getter
    @Setter
    private List<CvFormas> formas;
    @Getter
    @Setter
    private CvFormas forma;
    @Getter
    @Setter
    private UploadedFile fileUpload;

    @PostConstruct
    public void init() {
        formas = new ArrayList<>();
        forma = new CvFormas();
        formas = formasLocal.traerTodo();
    }

    public void eliminarArchivo() {
        int idForma = Integer.parseInt(FacesUtils.getRequestParam("idForma"));
        formasLocal.eliminarArchivo(sesion.getUsuarioSesion(), idForma);
        //
        formas = formasLocal.traerTodo();
    }

    public void agregarArchivo() {
        int idForma = Integer.parseInt(FacesUtils.getRequestParam("idForma"));
        forma = formasLocal.find(idForma);
        //
        PrimeFaces.current().executeScript("$(adjuntarArchivoForma).modal('show');");
    }

    public void subirArchivo(FileUploadEvent uploadEvent) {
        fileUpload = uploadEvent.getFile();
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        try {
            DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileUpload.getContent());
            documentoAnexo.setNombreBase(fileUpload.getFileName());
            documentoAnexo.setTipoMime(fileUpload.getContentType());
            documentoAnexo.setRuta(directorioForma());
            almacenDocumentos.guardarDocumento(documentoAnexo);
            //
            formasLocal.agregarArchivo(sesion.getUsuarioSesion(), forma.getId(), buildAdjuntoVO(documentoAnexo));
            fileUpload.delete();
            FacesUtils.addInfoMessage("Se cargó el documento");
            formas = formasLocal.traerTodo();
            PrimeFaces.current().executeScript("$(adjuntarArchivoForma).modal('hide');");
        } catch (IOException | SIAException e) {
            FacesUtils.addErrorMessage("Ocurrio un error: " + e.getMessage());
            UtilLog4j.log.fatal(this, "+ + + ERROR + + +" + e.getMessage(), e);
        }
    }

    public String directorioForma() {
        String subDir = "";
        try {
            subDir = "CV/Formas" + "/";
        } catch (Exception e) {
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

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }
}
