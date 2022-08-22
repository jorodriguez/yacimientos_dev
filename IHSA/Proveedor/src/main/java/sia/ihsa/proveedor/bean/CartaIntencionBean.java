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
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.orden.vo.OrdenEtsVo;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.orden.impl.OrdenSiMovimientoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;
import org.primefaces.PrimeFaces;
import javax.inject.Named;
import javax.inject.Inject;

/**
 *
 * @author mluis
 */
@Named(value = "cartaIntencionBean")
@ViewScoped
public class CartaIntencionBean implements Serializable {

    /**
     * Creates a new instance of CartaIntencionBean
     */
    public CartaIntencionBean() {
    }

    @Inject
    private Sesion sesion;

    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private OcOrdenEtsImpl ordenEtsImpl;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private OrdenSiMovimientoImpl ordenSiMovimientoImpl;

    @Getter
    @Setter
    private OrdenVO ordeneVo;
    @Getter
    @Setter
    private List<OrdenVO> ordenes;
    @Getter
    @Setter
    private List<OrdenDetalleVO> items;
    @Getter
    @Setter
    private List<OrdenEtsVo> doctosOrden;
    @Getter
    @Setter
    private List<MovimientoVO> rechazos;
    @Getter
    @Setter
    private String motivo;

    @PostConstruct
    public void init() {
        ordenes = new ArrayList<>();
        items = new ArrayList<>();
        llenarCartas();
        rechazos = new ArrayList<>();
    }

    private void llenarCartas() {
        ordenes = autorizacionesOrdenImpl.traerOrdenPorProveedorStatus(sesion.getProveedorVo().getIdProveedor(), OrdenEstadoEnum.POR_ACEPTAR_CARTA_INTENCION.getId());
    }

    private void llenarDoctos() {
        doctosOrden = ordenEtsImpl.traerEtsPorOrdenCategoria(ordeneVo.getId(), Constantes.OCS_CATEGORIA_REPSE);
    }

    public void agregarRepse(int indice) {
        ordeneVo = new OrdenVO();
        //
        ordeneVo = ordenes.get(indice);
        //
        PrimeFaces.current().executeScript("$(dialogoSubirRepse).modal('show');");
    }

    public void seleccionarCompra(int indice) {
        ordeneVo = new OrdenVO();
        //
        ordeneVo = ordenes.get(indice);
        //
        items = ordenImpl.itemsPorOrdenCompra(ordeneVo.getId());
        doctosOrden = new ArrayList<>();
        llenarDoctos();
        rechazos = ordenSiMovimientoImpl.traerMovimientsoOrdenOperacion(ordeneVo.getId(), Constantes.ID_SI_RECHAZAR_REPSE);
        //
        PrimeFaces.current().executeScript("$(dialogoDatosCompra).modal('show');");
    }

    public void aceptarCompra(int indice) {
        boolean continuar = false;
        ordeneVo = new OrdenVO();
        //
        ordeneVo = ordenes.get(indice);
        if (ordeneVo.isRepse()) {
            if (validarRepse()) {
                continuar = true;
            } else {
                FacesUtilsBean.addErrorMessage("Es necesario agregar el archivo REPSE");
            }
        } else {
            continuar = true;
        }

        if (continuar) {
            UsuarioVO userSesion = new UsuarioVO();
            userSesion.setId(sesion.getProveedorVo().getRfc());
            userSesion.setNombre(sesion.getProveedorVo().getNombre());
            items = ordenImpl.itemsPorOrdenCompra(ordeneVo.getId());
            ordenImpl.aceptarCartaIntencion(ordeneVo.getId(), items, userSesion);
            llenarCartas();
            items = new ArrayList<OrdenDetalleVO>();
            PrimeFaces.current().executeScript("$(dialogoDatosCompra).modal('hide');");
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario agregar el archivo REPSE");
        }
    }

    private boolean validarRepse() {
        boolean continuar = false;
        doctosOrden = ordenEtsImpl.traerEtsPorOrdenCategoria(ordeneVo.getId(), Constantes.OCS_CATEGORIA_REPSE);
        if (doctosOrden != null) {
            for (OrdenEtsVo ordenEtsVo : doctosOrden) {
                if (ordenEtsVo.getGenero().equals(sesion.getProveedorVo().getRfc())) {
                    continuar = true;
                    break;
                }
            }
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario agregar el archivo REPSE");
        }
        return continuar;
    }

    public void inicioRechazarCompra(int indice) {
        ordeneVo = new OrdenVO();
        //
        motivo = "";
        ordeneVo = ordenes.get(indice);
        PrimeFaces.current().executeScript("$(dialogoRechazarCompra).modal('show');");

    }

    public void rechazarCompra() {
        if (motivo.length() > 49) {
            UsuarioVO userSesion = new UsuarioVO();
            userSesion.setId(sesion.getProveedorVo().getRfc());
            userSesion.setNombre(sesion.getProveedorVo().getNombre());
            ordenImpl.rechazarCartaIntencion(ordeneVo.getId(), items, motivo, userSesion);
            motivo = "";
            ordeneVo = new OrdenVO();
            llenarCartas();
            PrimeFaces.current().executeScript("$(dialogoRechazarCompra).modal('hide');");
        } else {
            FacesUtilsBean.addErrorMessage("Escriba al menos 50 caracteres");
        }
    }

    public void uploadFile(FileUploadEvent fileEvent) {

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {

            UploadedFile fileInfo = fileEvent.getFile();

            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                if (fileInfo.getFileName().endsWith(".pdf")) {
                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                    documentoAnexo.setRuta(directorioProve());
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    almacenDocumentos.guardarDocumento(documentoAnexo);
                    //
                    SiAdjunto adj = siAdjuntoImpl.save(documentoAnexo.getNombreBase(),
                            new StringBuilder()
                                    .append(documentoAnexo.getRuta())
                                    .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                            fileInfo.getContentType(), fileInfo.getSize(), sesion.getProveedorVo().getRfc());
                    Usuario us = new Usuario();
                    us.setId(sesion.getProveedorVo().getRfc());
                    us.setNombre(sesion.getProveedorVo().getNombre());
                    //
                    ordenEtsImpl.crearOcOrdenEts(ordeneVo.getId(), Constantes.OCS_CATEGORIA_REPSE, adj, us);
                    //
                    llenarDoctos();
                    PrimeFaces.current().executeScript("$(dialogoSubirRepse).modal('hide');");
                    FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
                } else {
                    FacesUtilsBean.addErrorMessage(new StringBuilder()
                            .append("Solo se pueden adjuntar archivos con extensión pdf. ")
                            .toString());
                }
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

    public void eliminarRepse(int indice) {
        //
        ordenEtsImpl.eliminarOcOrdenEts(doctosOrden.get(indice).getIdEtsOrden(), sesion.getProveedorVo().getRfc());
        //
        llenarDoctos();

    }

    public String directorioProve() {
        return new StringBuilder().append("ETS/Orden")
                .append(ordeneVo.getId()).toString();
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
