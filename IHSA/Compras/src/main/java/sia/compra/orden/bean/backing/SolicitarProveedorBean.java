/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.NamingException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.monitor.FileEntry;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.documento.vo.DocumentoVO;
import sia.modelo.proveedor.Vo.ProveedorDocumentoVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.GeneralVo;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcTerminoPagoImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvClasificacionArchivoImpl;
import sia.servicios.proveedor.impl.PvDocumentoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
@Named(value = "solicitarProveedorBean")
@ViewScoped
public class SolicitarProveedorBean implements Serializable {

    /**
     * Creates a new instance of SolicitarProveedorBean
     */
    public SolicitarProveedorBean() {
    }
    private final UsuarioBean usuarioBean = (UsuarioBean) FacesUtilsBean.getManagedBean("usuarioBean");
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    UsuarioImpl usuarioImpl;
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    PvClasificacionArchivoImpl pvClasificacionArchivoImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private PvDocumentoImpl pvDocumentoImpl;
    @Inject
    private OcTerminoPagoImpl ocTerminoPagoImpl;
    //
    private int idProveedor;
    private ProveedorVo proveedorVo;
    private ProveedorDocumentoVO proveedorDocumentoVO = new ProveedorDocumentoVO();
    private List<DocumentoVO> listaDocto;
    private List<SelectItem> listaTerminoPago;
    private int tipoDoctoSubir;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    @PostConstruct
    public void iniciar() {
        listaTerminoPago = new ArrayList<>();
        List<GeneralVo> lg = ocTerminoPagoImpl.listaTerminoPago(usuarioBean.getCompania().getRfc());
        for (GeneralVo generalVo : lg) {
            listaTerminoPago.add(new SelectItem(generalVo.getValor(), generalVo.getNombre()));
        }
        proveedorVo= new ProveedorVo();
        proveedorVo.setContactos(new ArrayList<>());
    }

    public void agregarProveedor() {
        tipoDoctoSubir = Constantes.UNO;
        PrimeFaces.current().executeScript(";$(adjuntarArchivo).modal('show');;");

    }

    public void cargarDatosProveedor() throws NamingException {
        boolean valid = true;
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        if (fileInfo.getFileName().endsWith(".xlsx")) {
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
            try {
                if (addArchivo) {
                    cargarDatos();
                    PrimeFaces.current().executeScript(";$(registroProv).modal('hide');;");
                } else {
                    FacesUtilsBean.addErrorMessage(new StringBuilder()
                            .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                            .append(validadorNombreArchivo.getCaracteresNoValidos())
                            .toString());
                }

            } catch (Exception e) {
                valid = false;
            }

            if (!valid) {
                FacesUtilsBean.addErrorMessage("Ocurrió un error al subir el archivo del Pago. Porfavor contacte al Equipo del SIA: soportesia@ihsa.mx");
            }
        } else {
            FacesUtilsBean.addErrorMessage("El archivo no es del tipo esperado (*.xlsx).");
        }
    }

    public void cargarDatos() {
        try {
            File file = new File("/tmp/" + fileInfo.getFileName());
            try (OutputStream os = new FileOutputStream(file)) {
                os.write(fileInfo.getContent());
            }

            int idP = proveedorImpl.cargarDatosProveedor(usuarioBean.getUsuarioConectado().getId(), file, usuarioBean.getCompania().getRfc());
            setIdProveedor(idP);
           // Files.deleteIfExists(file.toPath());
            //.println("idProve: " + idP);
            switch (idP) {
                case Constantes.MENOS_UNO:
                    FacesUtilsBean.addErrorMessage("El proveedor seleccionado está vetado por Grupo Cobra");
                    break;
                case Constantes.CERO:
                    FacesUtilsBean.addErrorMessage("Hay un problema en los datos del proveedor, favor de revisar las fechas (dd/MM/yyyy) y CLABE que no pase de 18 digitos.");
                    break;
                default:
                    proveedorVo = proveedorImpl.traerProveedor(idP, usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());
                    proveedorVo.setTipoProveedor(Constantes.CERO);
                    break;
            }

        } catch (IOException e) {
            UtilLog4j.log.error(e);
        }
    }

    public void procesarProveedor() {
        try {
            if (proveedorVo.getContactos().size() > 0) {
                if (proveedorVo.getLstDocsProveedor().size() > 0) {
                    boolean continuar = true;
                    for (ProveedorDocumentoVO pvO : proveedorVo.getLstDocsProveedor()) {
                        if (pvO.getAdjuntoVO().getId() == 0) {
                            continuar = false;
                            break;
                        }
                    }
                    if (continuar) {
                        if (proveedorVo.getIdPago() > 0) {
                            UsuarioVO sesion = usuarioImpl.findById(usuarioBean.getUsuarioConectado().getId());
                            proveedorImpl.procesarProveedor(sesion, proveedorVo);
                            proveedorVo.setStatus(ProveedorEnum.EN_PROCESO.getId());
                        } else {
                            FacesUtilsBean.addErrorMessage("Seleccione el termino de pago del proveedor . . .  ");
                        }
                    } else {
                        FacesUtilsBean.addErrorMessage("Hace falta documentación física . . . ");
                    }
                } else {
                    FacesUtilsBean.addErrorMessage("Agregue la documentación. . . ");
                }
            } else {
                FacesUtilsBean.addErrorMessage("No hay contactos registrados, por favor verifique la sección de contactos en el archivo de Datos generales del proveedor. ");
            }
        } catch (Exception e) {
            FacesUtilsBean.addErrorMessage("Excepcion al solicitar el proveedor . . . " + e);
            UtilLog4j.log.error(e);
        }

    }

    public void nuevoDocumento(int idP) {
        try {
            setListaDocto(pvDocumentoImpl.traerDocFaltanteProveedor(idP, Constantes.DOCUMENTO_TIPO_PROVEEDOR));
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoAgregarDoctoProv);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void agregarDocumentos() {
        List<DocumentoVO> ltemp = new ArrayList<>();
        for (DocumentoVO voSelected : getListaDocto()) {
            if (voSelected.isSelected()) {
                ltemp.add(voSelected);
            }
        }
        pvClasificacionArchivoImpl.guardar(usuarioBean.getUsuarioConectado().getId(), ltemp, proveedorVo.getIdProveedor());
        //
        proveedorVo.setLstDocsProveedor(new ArrayList<>());
        proveedorVo.getLstDocsProveedor().addAll(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(idProveedor, Constantes.CERO));
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoAgregarDoctoProv);");
    }

    public void agregarArchivoDocto(ProveedorDocumentoVO pvDocto) {
        try {
            tipoDoctoSubir = 2;
            proveedorDocumentoVO = pvDocto;
            String metodo = ";abrirDialogoModal(adjuntarArchivo);";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void eliminarProveedorDocumento(ProveedorDocumentoVO pvDocto) {
        try {
            pvClasificacionArchivoImpl.eliminar(usuarioBean.getUsuarioConectado().getId(), pvDocto.getId());
            proveedorVo.getLstDocsProveedor().remove(pvDocto);
        } catch (NumberFormatException e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public void quitarSoloArchivoDocumento(ProveedorDocumentoVO pvDocto) {
        try {
            siAdjuntoImpl.eliminarArchivo(pvDocto.getAdjuntoVO().getId(), usuarioBean.getUsuarioConectado().getId());
            pvClasificacionArchivoImpl.quitarArchivoDocumento(usuarioBean.getUsuarioConectado().getId(), pvDocto.getId());
            //
            proveedorVo.setLstDocsProveedor(new ArrayList<>());
            proveedorVo.getLstDocsProveedor().addAll(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(proveedorVo.getIdProveedor(), Constantes.CERO));
        } catch (NumberFormatException e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void subirArchivo(FileUploadEvent event) {
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        try {
            fileInfo = event.getFile();
            if (tipoDoctoSubir == Constantes.UNO) {
                cargarDatosProveedor();
                FacesUtilsBean.addInfoMessage("Se agregaron los datos del proveedor. ");
            } else {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                String extFile = FilenameUtils.getExtension(fileInfo.getFileName());
                documentoAnexo.setNombreBase(proveedorDocumentoVO.getDocumento() + '.' + extFile);
                documentoAnexo.setRuta(getDirectorioDocumentoProveedor());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                almacenDocumentos.guardarDocumento(documentoAnexo);
                //
                AdjuntoVO adjunto = new AdjuntoVO();
                adjunto.setUrl(documentoAnexo.getRuta() + documentoAnexo.getNombreBase());
                adjunto.setNombre(documentoAnexo.getNombreBase());
                adjunto.setTipoArchivo(documentoAnexo.getTipoMime());
                adjunto.setTamanio(documentoAnexo.getTamanio());
                //
                proveedorDocumentoVO.setAdjuntoVO(adjunto);
                agregarAdjuntoProveedor();
            }
            PrimeFaces.current().executeScript("$(adjuntarArchivo).modal('hide');");
            FacesUtilsBean.addInfoMessage("Se agrego la documentación. ");
        } catch (Exception e) {
            FacesUtilsBean.addErrorMessage("Ocurrio un error: " + e.getMessage());
            UtilLog4j.log.fatal(this, "+ + + ERROR + + +" + e.getMessage(), e);
        }
    }

    public String getDirectorioDocumentoProveedor() {
        String dir = "";
        try {
            dir = "CV/Proveedor/" + proveedorVo.getRfc() + "/";
        } catch (RuntimeException e) {
            UtilLog4j.log.fatal(e);
        }
        return dir;
    }

    public void agregarAdjuntoProveedor() throws Exception {
        int adj = siAdjuntoImpl.saveSiAdjunto(
                proveedorDocumentoVO.getAdjuntoVO().getNombre(),
                proveedorDocumentoVO.getAdjuntoVO().getTipoArchivo(),
                proveedorDocumentoVO.getAdjuntoVO().getUrl(),
                proveedorDocumentoVO.getAdjuntoVO().getTamanio(),
                usuarioBean.getUsuarioConectado().getId()
        );
        pvClasificacionArchivoImpl.guardar(usuarioBean.getUsuarioConectado().getId(), proveedorDocumentoVO.getId(), adj);
        proveedorVo.setLstDocsProveedor(new ArrayList<ProveedorDocumentoVO>());
        proveedorVo.getLstDocsProveedor().addAll(pvClasificacionArchivoImpl.traerArchivoPorProveedorOid(proveedorVo.getIdProveedor(), Constantes.CERO));

    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * @return the proveedorVo
     */
    public ProveedorVo getProveedorVo() {
        return proveedorVo;
    }

    /**
     * @param proveedorVo the proveedorVo to set
     */
    public void setProveedorVo(ProveedorVo proveedorVo) {
        this.proveedorVo = proveedorVo;
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

    /**
     * @return the listaDocto
     */
    public List<DocumentoVO> getListaDocto() {
        return listaDocto;
    }

    /**
     * @param listaDocto the listaDocto to set
     */
    public void setListaDocto(List<DocumentoVO> listaDocto) {
        this.listaDocto = listaDocto;
    }

    /**
     * @return the tipoDoctoSubir
     */
    public int getTipoDoctoSubir() {
        return tipoDoctoSubir;
    }

    /**
     * @param tipoDoctoSubir the tipoDoctoSubir to set
     */
    public void setTipoDoctoSubir(int tipoDoctoSubir) {
        this.tipoDoctoSubir = tipoDoctoSubir;
    }

    /**
     * @return the listaTerminoPago
     */
    public List<SelectItem> getListaTerminoPago() {
        return listaTerminoPago;
    }

    /**
     * @param listaTerminoPago the listaTerminoPago to set
     */
    public void setListaTerminoPago(List<SelectItem> listaTerminoPago) {
        this.listaTerminoPago = listaTerminoPago;
    }

}
