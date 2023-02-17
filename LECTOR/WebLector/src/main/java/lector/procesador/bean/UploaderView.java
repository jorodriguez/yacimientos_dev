/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.procesador.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import lector.sistema.bean.backing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lector.archivador.AlmacenDocumentos;
import lector.archivador.DocumentoAnexo;
import lector.archivador.ProveedorAlmacenDocumentos;
import lector.excepciones.LectorException;
import lector.modelo.SiAdjunto;
import lector.servicios.sistema.impl.SiParametroImpl;
import lector.sistema.bean.support.FacesUtils;
import lector.util.ValidadorNombreArchivo;
import lector.vision.Item;
import lector.vision.Item;
import lector.vision.api.service.LectorService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author jorodriguez
 */
@Named
@ViewScoped
public class UploaderView implements Serializable {

    @Inject
    private Sesion sesion;
    
    private String filename;
    
    @Inject
    private LectorService lectorService;
    
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    
    @Inject
    private SiParametroImpl parametrosSistemaServicioRemoto;
    
    
    private CroppedImage croppedImage;
    
    private UploadedFile originalImageFile;
    
    @Getter   
    private UploadedFile fileInfo;
    
    @Getter   @Setter
    private byte[] fileContent;
    
    @Getter   @Setter
    List<Item> listaItems;
    

    public UploaderView() {
    }

    @PostConstruct
    public void iniciar() {
        System.out.println("@Postconstruc"+this.getClass().getCanonicalName());
        //loaders
    }
    
    
    
    public void subirAdjunto(FileUploadEvent event) {
        System.out.println("@subirAdjunto");

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

        try {

            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

            fileInfo = event.getFile();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {

                System.out.println("--proceder a verificar");

                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta("credenciales");
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                System.out.println("nombre archivo " + fileInfo.getFileName());
                System.out.println("content type" + fileInfo.getContentType());
                System.out.println("content " + fileInfo.getContent().length);
   
                //-------- DESCOMENTAR listaItems = lectorService.getTextoData(fileInfo.getContent());
                
                this.fileContent = fileInfo.getContent();
                
                this.originalImageFile = null;
                this.croppedImage = null;

                if (fileInfo != null && fileInfo.getContent() != null && fileInfo.getContent().length > 0 && fileInfo.getFileName() != null) {
                    this.originalImageFile = fileInfo;
                    FacesMessage msg = new FacesMessage("Successful", this.originalImageFile.getFileName() + " is uploaded.");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }

                //listaTexto = lectorService.getTexto();
                System.out.println("realizado ");

                //doit updload
                /*
                
                SiAdjunto adj = adjuntoImpl.save(documentoAnexo.getNombreBase(),
                        new StringBuilder()
                                .append(documentoAnexo.getRuta())
                                .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                fileInfo.getContentType(), fileInfo.getSize(), sesion.getUsuarioConectado().getId());                              */
            } else {
                FacesUtils.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

        } catch (IOException | LectorException e) {
            System.out.println(" error al cargar " + e);
            FacesUtils.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soport@gmail.mx)");
        }

    }

    
     public void listenerAdjunto(FileUploadEvent event) {
         System.out.println("Listener ");
         this.fileInfo = event.getFile();
     }

    
    public boolean subirArchivo() {

        if (fileInfo == null) {
            FacesUtils.addErrorMessage("Seleccione un archivo.");
            return false;
        }

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

        try {

            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {

                System.out.println("--proceder a verificar");

                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta("credenciales");
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                System.out.println("nombre archivo " + fileInfo.getFileName());
                System.out.println("content type" + fileInfo.getContentType());
                System.out.println("content " + fileInfo.getContent().length);

                listaItems = lectorService.getTextoData(fileInfo.getContent());               
             
                //listaTexto = lectorService.getTexto();
                System.out.println("uploado *** ok");
                /*               
                SiAdjunto adj = adjuntoImpl.save(documentoAnexo.getNombreBase(),
                        new StringBuilder()
                                .append(documentoAnexo.getRuta())
                                .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                fileInfo.getContentType(), fileInfo.getSize(), sesion.getUsuarioConectado().getId());                              */
            } else {
                FacesUtils.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();
            fileInfo = null;
            
            return true;

        } catch (IOException | LectorException e) {
            System.out.println(" error al cargar " + e);
            FacesUtils.addInfoMessage("Ocurrió un problema al cargar el archivo");
            return false;
        }
    }

    

     public void handleFileUpload(FileUploadEvent event) {
         System.out.println("@handleFileUpload");
         
        this.originalImageFile = null;
        this.croppedImage = null;
        
        this.fileInfo = event.getFile();
        
        this.fileContent = fileInfo.getContent();
        
        if (fileInfo != null && fileInfo.getContent() != null && fileInfo.getContent().length > 0 && fileInfo.getFileName() != null) {
            this.originalImageFile = fileInfo;
            FacesMessage msg = new FacesMessage("Successful", this.originalImageFile.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void crop() {
        if (this.croppedImage == null || this.croppedImage.getBytes() == null || this.croppedImage.getBytes().length == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                    "Cropping failed."));
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                    "Cropped successfully."));
        }
    }

    public StreamedContent getImage() {
        return DefaultStreamedContent.builder()
                .contentType(originalImageFile == null ? null : originalImageFile.getContentType())
                .stream(() -> {
                    if (originalImageFile == null
                            || originalImageFile.getContent() == null
                            || originalImageFile.getContent().length == 0) {
                        return null;
                    }

                    try {
                        return new ByteArrayInputStream(originalImageFile.getContent());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .build();
    }

    public StreamedContent getCropped() {
        return DefaultStreamedContent.builder()
                .contentType(originalImageFile == null ? null : originalImageFile.getContentType())
                .stream(() -> {
                    if (croppedImage == null
                            || croppedImage.getBytes() == null
                            || croppedImage.getBytes().length == 0) {
                        return null;
                    }

                    try {
                        return new ByteArrayInputStream(this.croppedImage.getBytes());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .build();
    }
    
    public void changeFoto(ValueChangeEvent valuchangeevent){
                System.out.println("@@changeFoto");
                
    }
    
    
    
    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    public UploadedFile getOriginalImageFile() {
        return originalImageFile;
    }
    
    
    public void setFileInfo(UploadedFile uploadedFile ) {
        this.fileInfo = uploadedFile;
        if(fileInfo != null){
            this.fileContent = this.fileInfo.getContent();
        }
    }   
    
    
    public String getImageContentsAsBase64() {
            return Base64.getEncoder().encodeToString(fileContent);
    }

}
