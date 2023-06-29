/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.procesador.bean;

import mx.ihsa.sistema.bean.backing.Sesion;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.ihsa.sistema.bean.support.FacesUtils;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import mx.ihsa.dominio.vo.CLocalidadVo;
import mx.ihsa.dominio.vo.CSeccionVo;
import mx.ihsa.util.UtilLog4j;
import mx.ihsa.archivador.DocumentoAnexo;
import mx.ihsa.dominio.modelo.usuario.vo.UsuarioVO;
import mx.ihsa.excepciones.GeneralException;
import mx.ihsa.util.ValidadorNombreArchivo;

/**
 *
 * @author jorodriguez
 */
@Named
@ViewScoped
public class ContactoView implements Serializable {

    @Inject
    private Sesion sesion;


    private CroppedImage croppedImage;

    private UploadedFile originalImageFile;

    @Getter
    private UploadedFile fileInfo;

    @Getter
    @Setter
    private byte[] fileContent;


    private final Function<CSeccionVo, SelectItem> seccionToSelectItem = m -> new SelectItem(m.getId(), m.getNombre());
    
    private static final UtilLog4j log = UtilLog4j.log;

    public ContactoView() {
    }

    @PostConstruct
    public void iniciar() {
        System.out.println("@Postconstruc" + this.getClass().getCanonicalName());
        //loaders
        limpiarForma();
        
    }
    
    
    private void limpiarForma(){
           fileContent = null;
           
                      
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

        final ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

        try {

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {

                final DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta("dummyPath");
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                //almacenDocumentos.guardarDocumento(documentoAnexo);
                
                cargarValoresUsuario();

                System.out.println("upload *** ok");

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

        } catch (IOException | GeneralException e) {
            System.out.println(" error al cargar " + e);
            FacesUtils.addInfoMessage("Ocurrió un problema al cargar el archivo");
            return false;
        }
    }

    public void guardar() {
        
        log.info("@Guardar");
        System.out.println("@guardar");

      //  try {

            if(!validarUsuario()){
                System.out.println("Validacion error");
                return;
            }
            
            if(validarTelefono()){
                FacesUtils.addErrorMessage("msg_telefono", "El número de télefono ya fue registrado.");
                FacesUtils.addErrorMessage("El número de télefono ya fue registrado.");
                System.out.println("Validacion error");
                return;
            }
            
            
            if (fileInfo == null) {
                
            }

            limpiarForma();
            
            FacesUtils.addInfoMessage("Contacto agregado.");
            System.out.println("Contacto agregado");
            log.info("contacto agregado....");

    }

    private boolean validarUsuario() {
        
        log.info("@validarUsuario");
        
        System.out.println("@validarUsuario");
                        
                       
        return true;

    }

    private boolean validarTelefono(){
        
        return true;
        
    }
    
    private void cargarValoresUsuario() {

        log.info("@cargarValoresUsuario");

        System.out.println("Valores etiquetas");

        
        limpiarForma();
    
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
        } else {
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
                    } catch (Exception e) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .build();
    }


    public void changeFoto(ValueChangeEvent valuchangeevent) {
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

    public void setFileInfo(UploadedFile uploadedFile) {
        this.fileInfo = uploadedFile;
        if (fileInfo != null) {
            this.fileContent = this.fileInfo.getContent();
        }
    }

    public String getImageContentsAsBase64() {

        return fileContent != null ? Base64.getEncoder().encodeToString(fileContent) : null;
    }


    
    public void handleChangeLocalidad(ValueChangeEvent event){                        
        System.out.println("handleChangeLocalidad" );
        
                
    }

}
