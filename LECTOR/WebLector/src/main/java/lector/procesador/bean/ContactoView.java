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
import lector.dominio.vo.CSeccionVo;
import lector.servicios.catalogos.impl.UbicacionesImpl;
import lector.servicios.sistema.impl.ContactoImpl;
import lector.util.UtilLog4j;
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

    @Getter
    @Setter
    private CLocalidadVo localidadSeleccionada;

    @Getter
    private List<SelectItem> seccionesItems;
        
    private final Function<CSeccionVo, SelectItem> seccionToSelectItem = m -> new SelectItem(m.getId(), m.getNombre());
    
    @Getter    
    private Map<Integer,CLocalidadVo> localidadesMap;
    
    private static final UtilLog4j log = UtilLog4j.log;

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
                .anioEmision(0)
                .vigencia(0)
                .sexo("")                
                .cCuenta(sesion.getUsuarioSesion().getCCuenta())
                .cEstado(sesion.getUsuarioSesion().getCEstado())
                .estado(sesion.getUsuarioSesion().getEstado())
                .estadoClave(sesion.getUsuarioSesion().getEstadoClave())
                .cMunicipio(sesion.getUsuarioSesion().getCMunicipio())                
                .municipio(sesion.getUsuarioSesion().getMunicipio())
                .municipioClave(sesion.getUsuarioSesion().getMunicipioClave())
                .cLocalidad(sesion.getUsuarioSesion().getCLocalidad())
                .localidad(sesion.getUsuarioSesion().getLocalidad())
                .localidadClave(sesion.getUsuarioSesion().getLocalidadClave())                
                .genero(sesion.getUsuarioSesion().getId())
                .registro(sesion.getUsuarioSesion().getId())                
                .cTipoContacto(2)
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

    public void guardar() {
        
        log.info("@Guardar");
        System.out.println("@guardar");

        try {

            if(!validarUsuario()){
                System.out.println("Validacion error");
                return;
            }
            
            
            if (fileInfo == null) {
                informacionCredencialDto = InformacionCredencialDto
                        .builder()
                        .usuarioDto(usuarioDto)
                        .build();
            }

            //validacion de campos
            contactoService.guardarContacto(informacionCredencialDto);

        } catch (LectorException le) {
             System.out.println("guardar ex"+le.getMessage());
            FacesUtils.addErrorMessage(le.getMessage());

        }

    }

    private boolean validarUsuario() {
        
        log.info("@validarUsuario");
        
        System.out.println("@validarUsuario");
                        
        if (usuarioDto == null) {
            FacesUtils.addErrorMessage("Existió un error al intentar guardar el contacto.");
            return false;
        }

        if (usuarioDto.getNombre().isEmpty() || usuarioDto.getNombre().isBlank()) {
            FacesUtils.addErrorMessage("Nombre requerido.");
            return false;
        }

        if (usuarioDto.getDomicilio().isEmpty() || usuarioDto.getDomicilio().isBlank()) {
            FacesUtils.addErrorMessage("Domicilio requerido.");
            return false;
        }

        if (usuarioDto.getFechaNacimiento() == null) {
            FacesUtils.addErrorMessage("Fecha de nacimiento requerida.");
            return false;
        }

        if (usuarioDto.getEmail().isEmpty() || usuarioDto.getEmail().isBlank()) {
            FacesUtils.addErrorMessage("El correo es requeido.");
            return false;
        }
        if (usuarioDto.getTelefono().isEmpty() || usuarioDto.getTelefono().isBlank()) {
            FacesUtils.addErrorMessage("El telefono es requeido.");
            return false;          
            
        }
        
        return true;

        /*if(usuarioDto.getFechaNacimiento().after(
                    Date.from(LocalDate.now().minusYears(18))
        )
            ){
            FacesUtils.addErrorMessage("Fecha de nacimiento requerida.");           
            return;
         }*/
    }

    private void cargarValoresUsuario() {

        log.info("@cargarValoresUsuario");

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
        
        log.info("@cargarCatalogoLocalidades");
       
        final List<CLocalidadVo> localidades = ubicacionesService.findAllLocalidades(usuarioDto.getCMunicipio());
        
        this.localidadesMap = localidades.stream().collect(Collectors.toMap(CLocalidadVo::getId, Function.identity()));
        
        
    }
    
    public void handleChangeLocalidad(ValueChangeEvent event){                        
        System.out.println("handleChangeLocalidad" );
        
        System.out.println("handleChangeLocalidad "+event.getNewValue() );
        
        final int idLocalidadSelect = (int) event.getNewValue();
        
        this.localidadSeleccionada = this.localidadesMap.get(idLocalidadSelect);
        
        System.out.println("New selection localidad : "+this.localidadSeleccionada.getId());
        System.out.println("New selection municipio : "+this.localidadSeleccionada.getMunicipio());

        //Tofix
        usuarioDto.setLocalidad(
                    String.valueOf(this.localidadSeleccionada.getClave())
        );
                
        // cargar las secciones de la localidad seleccionada
        
        List<CSeccionVo> secciones = ubicacionesService.findAllSeccionesLocalidad(this.localidadSeleccionada.getId());
        
        this.seccionesItems = secciones.stream().map(seccionToSelectItem).collect(Collectors.toList());        
        
        
                
    }

}
