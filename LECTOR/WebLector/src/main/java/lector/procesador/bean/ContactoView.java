/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.procesador.bean;

import java.io.ByteArrayInputStream;
import lector.sistema.bean.backing.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lector.archivador.DocumentoAnexo;
import lector.constantes.Constantes;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.excepciones.LectorException;
import lector.sistema.bean.support.FacesUtils;
import lector.util.ValidadorNombreArchivo;
import lector.vision.InformacionCredencialDto;
import lector.vision.Item;
import lector.vision.api.service.LectorService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import static lector.constantes.Constantes.Etiquetas.*;
import lector.dominio.vo.CLocalidadVo;
import lector.servicios.catalogos.impl.UbicacionesImpl;
import lector.servicios.sistema.impl.ContactoImpl;
import static lector.util.UtilsProcess.castToInt;

/**
 *
 * @author jorodriguez
 */
@Named
@ViewScoped
public class ContactoView implements Serializable {

    @Inject
    private Sesion sesion;
    
    @Inject
    private UbicacionesImpl ubicacionesService;

    @Inject
    private LectorService lectorService;

    @Inject
    private ContactoImpl contactoService;

    private CroppedImage croppedImage;

    private UploadedFile originalImageFile;

    @Getter
    private UploadedFile fileInfo;

    @Getter
    @Setter
    private byte[] fileContent;

    @Getter
    @Setter
    private List<Item> listaItems;
    
    @Getter
    @Setter
    private List<CLocalidadVo> listaLocalidades;

    @Getter
    private InformacionCredencialDto informacionCredencialDto;

    @Getter
    @Setter
    private UsuarioVO usuarioDto;
    
    @Getter @Setter
    private CLocalidadVo localidadSeleccionada;

    public ContactoView() {
    }

    @PostConstruct
    public void iniciar() {
        System.out.println("@Postconstruc" + this.getClass().getCanonicalName());
        //loaders
        usuarioDto = UsuarioVO.builder()
                        .nombre("")
                        .domicilio("")
                        .fechaNacimiento(null)
                        .email("")
                        .telefono("")
                        .cCuenta(sesion.getUsuarioSesion().getCCuenta())
                        .cEstado(sesion.getUsuarioSesion().getCEstado())
                        .cMunicipio(sesion.getUsuarioSesion().getCMunicipio())
                        .cLocalidad(sesion.getUsuarioSesion().getCLocalidad())
                         .build();
       
        cargarCatalogoLocalidades();                
        
    }

    /*  public void subirAdjunto(FileUploadEvent event) {
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
                //almacenDocumentos.guardarDocumento(documentoAnexo);

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

    }*/
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
                documentoAnexo.setRuta("credenciales");
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                //almacenDocumentos.guardarDocumento(documentoAnexo);

                informacionCredencialDto = lectorService.getInformacionCredencial(documentoAnexo);

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

        } catch (IOException | LectorException e) {
            System.out.println(" error al cargar " + e);
            FacesUtils.addInfoMessage("Ocurrió un problema al cargar el archivo");
            return false;
        }
    }

    public void guardar(ActionEvent e) {

        try {

            validarUsuario();
            
            if (fileInfo == null) {
                informacionCredencialDto = InformacionCredencialDto
                        .builder()
                        .usuarioDto(usuarioDto)
                        .build();
            }
            
            
            //validacion de campos
            contactoService.guardarContacto(informacionCredencialDto);

        } catch (LectorException le) {

            FacesUtils.addErrorMessage(le.getMessage());

        }

    }
    
    private void validarUsuario(){
        if(usuarioDto == null){
            FacesUtils.addErrorMessage("Existió un error al intentar guardar el contacto.");           
            return;
        }
        
        if(usuarioDto.getNombre().isEmpty() ){
            FacesUtils.addErrorMessage("Nombre requerido.");           
            return;
        }
        
        if(usuarioDto.getDomicilio().isEmpty() || usuarioDto.getDomicilio().isEmpty()){
            FacesUtils.addErrorMessage("Domicilio requerido.");           
            return;
        }
        
        if(usuarioDto.getFechaNacimiento() == null){
            FacesUtils.addErrorMessage("Fecha de nacimiento requerida.");           
            return;
        }
        
        if(usuarioDto.getEmail().isEmpty() || usuarioDto.getEmail().isEmpty()){
            FacesUtils.addErrorMessage("El correo es requeido.");           
            return;
        }
        if(usuarioDto.getTelefono().isEmpty() || usuarioDto.getTelefono().isEmpty()){
            FacesUtils.addErrorMessage("El telefono es requeido.");           
            return;
        }
        
        /*if(usuarioDto.getFechaNacimiento().after(
                    Date.from(LocalDate.now().minusYears(18))
        )
            ){
            FacesUtils.addErrorMessage("Fecha de nacimiento requerida.");           
            return;
         }*/
        
    }

    private void cargarValoresUsuario() {

        System.out.println("@cargarValoresUsuario");

        if (informacionCredencialDto == null) {

            throw new NullPointerException("Es null informacionCredencialDto");
        }

        System.out.println("Valores etiquetas");

        /*for (Map.Entry<String, Item> entry : informacionCredencialDto.getEtiquetasDetectadas().entrySet()) {
            Object key = entry.getKey();
            Item val = entry.getValue();
            System.out.println("key "+key);
            System.out.println("key "+val.getValor());            
            
        }*/
 /*for(int i=0; i < informacionCredencialDto.getEtiquetasDetectadas().size();i++){
            informacionCredencialDto.getEtiquetasDetectadas().get()
        }*/
        usuarioDto = UsuarioVO.builder()
                .nombre(gettingValorEtiqueta(NOMBRE))
                .domicilio(gettingValorEtiqueta(DOMICILIO))
                .claveElector(gettingValorEtiqueta(CLAVE_DE_ELECTOR))
                .curp(gettingValorEtiqueta(CURP))
                .sexo(gettingValorEtiqueta(SEXO))
                .estado(gettingValorEtiqueta(ESTADO))
                .municipio(gettingValorEtiqueta(MUNICIPIO))
                .localidad(gettingValorEtiqueta(LOCALIDAD))
                .activo(true)
                .genero(sesion.getUsuarioSesion().getId())
                .conFoto(true)
                .build();

        String valor = gettingValorEtiqueta(VIGENCIA);

        usuarioDto.setVigencia(
                castToInt(valor)
        );

        valor = gettingValorEtiqueta(EMISION);
        usuarioDto.setAnioEmision(
                castToInt(valor)
        );

    }

    private String gettingValorEtiqueta(Constantes.Etiquetas etiqueta) {
        System.out.println("@gettingValorEtiqueta");

        System.out.println("Etiqueta " + etiqueta.name());

        Item item = informacionCredencialDto.getEtiquetasDetectadas().get(etiqueta.name());

        System.out.println("item " + item);

        return item == null ? "NO ENCONTRADO" : item.getValor();

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

    public List<Map.Entry<String, Item>> getEtiquetas() {

        Set<Map.Entry<String, Item>> setList = Collections.emptySet();

        if (informacionCredencialDto != null && informacionCredencialDto.getEtiquetasDetectadas() != null) {

            setList = informacionCredencialDto.getEtiquetasDetectadas().entrySet();

        }

        return new ArrayList<Map.Entry<String, Item>>(setList);
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
    
    private void cargarCatalogoLocalidades() {
        this.listaLocalidades = ubicacionesService.findAllLocalidades(usuarioDto.getCMunicipio());
    }

    private void cargarCatalogoSeccionesLocalidad() {
        //this.listaSecciones = ubicacionesService.findAllLocalidades(usuarioDto.getCLocalidad());
    }
    
    public List<CLocalidadVo> completeLocalidades(String query) {
        System.out.println("@complete localidades");
        String queryLowerCase = query.toLowerCase();
        System.out.println("@busqueda "+queryLowerCase);
        
        //List<CLocalidadVo> countries = countryService.getCountries();
        List<CLocalidadVo> lista = listaLocalidades
                    .stream()
                    .filter(
                        (t) -> {
                            return (t.getNombre().toLowerCase().contains(queryLowerCase));                                  
                          }
                    ).collect(Collectors.toList());
                //.map(CLocalidadVo::getNombre)
                
         //lista.forEach(e-> System.out.println("nombre "+e.getNombre()+" clave "+e.getClave()));
         
         return lista;
    }

}
