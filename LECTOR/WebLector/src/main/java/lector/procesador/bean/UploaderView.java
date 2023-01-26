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
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lector.archivador.AlmacenDocumentos;
import lector.archivador.DocumentoAnexo;
import lector.archivador.ProveedorAlmacenDocumentos;
import lector.excepciones.SIAException;
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
    
    @Getter   @Setter
    private UploadedFile fileInfo;
    
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
                
                System.out.println("nombre archivo "+fileInfo.getFileName());
                System.out.println("content type"+fileInfo.getContentType());
                System.out.println("content "+fileInfo.getContent().length);
                
                //String path = parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
                
                //String url = almacenDocumentos.getRaizAlmacen() + documentoAnexo.getRuta()+"/"+documentoAnexo.getNombreBase();
                
                //System.out.println("PATH "+path.get);
                //System.out.println("URL "+url);
                
                //listaTexto = lectorService.getTexto(fileInfo.getContent());
                //listaTexto = lectorService.getTexto(new URL("https://res.cloudinary.com/dwttlkcmu/image/upload/v1674658258/samples/mingo_yhlwbs.jpg"));
                //listaTexto = lectorService.getTexto("/home/jorodriguez/Descargas/mingo.jpeg");
                
//                 listaItems = lectorService.getTextoAlt("https://res.cloudinary.com/dwttlkcmu/image/upload/v1674658258/samples/mingo_yhlwbs.jpg");

                listaItems = lectorService.getTextoData(fileInfo.getContent());
                 
                 //listaTexto = lectorService.getTexto();
                
                System.out.println("realizado ");
                
                /*for(Item item : listaTexto){
                    System.out.println(item.getEtiqueta()+ " "+item.getValor());
                }*/
                
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

        } catch (IOException | SIAException e) {
            System.out.println(" error al cargar "+e);
            FacesUtils.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soport@gmail.mx)");
        }
    }
    
     public String uploadDirectoryTicket() {
        return new StringBuilder().append("credenciales/").append(sesion.getUsuario().getId()).toString();
    }
    

     public void handleFileUpload(FileUploadEvent event) {
         System.out.println("@handleFileUpload");
         
        this.originalImageFile = null;
        this.croppedImage = null;
        
        UploadedFile file = event.getFile();
        
        if (file != null && file.getContent() != null && file.getContent().length > 0 && file.getFileName() != null) {
            this.originalImageFile = file;
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
    
    
    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    public UploadedFile getOriginalImageFile() {
        return originalImageFile;
    }
     

}
