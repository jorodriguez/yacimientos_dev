/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.incidencia.vehiculo.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.sgl.vehiculo.vo.VehiculoIncidenciaVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.sgl.incidencia.vehiculo.model.IncidenciaVehiculoModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author ihsa
 */
@Named(value = "incidenciaVehiculoBean")
@RequestScoped
public class IncidenciaVehiculoBean implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Getter
    @Setter
    private UploadedFile fileInfo;
    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @ManagedProperty(value = "#{incidenciaVehiculoModel}")
    private IncidenciaVehiculoModel incidenciaVehiculoModel;

    public void guardar(ActionEvent event) {
        try {
            incidenciaVehiculoModel.guardar();
            PrimeFaces.current().executeScript(";dialogoInci.hide();");
            FacesUtils.addInfoMessage("Se agregó la incidencia . . .");
        } catch (Exception ex) {
            FacesUtils.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
            UtilLog4j.log.fatal(this, ex.getMessage());
        }

    }

    public void cancelar(ActionEvent event) {
        limpiar();
        //    PrimeFaces.current().executeScript(";cerrarPop('dialogoCrearInc');");
    }

    private void limpiar() {
        incidenciaVehiculoModel.setDescripcion("");
        incidenciaVehiculoModel.setTitulo("");
        incidenciaVehiculoModel.setPalabraClave("");
        incidenciaVehiculoModel.setVehiculo(-1);
        incidenciaVehiculoModel.setPriridad(-1);
        incidenciaVehiculoModel.setGerencia(-1);
    }

    public List<SelectItem> getListaPrioridad() {
        return incidenciaVehiculoModel.listaPrioridad();
    }

    public List<SelectItem> getListaGerencia() {
        return incidenciaVehiculoModel.listaGerencia();
    }

    public List<SelectItem> getListaVehiculo() {
        return incidenciaVehiculoModel.listaVehiculo();
    }

    /**
     * Creates a new instance of IncidenciaVehiculoBean
     *
     * @param event
     */
    public void administrarIncidencia(ActionEvent event) {
        incidenciaVehiculoModel.setVehiculo(Integer.parseInt(FacesUtils.getRequestParameter("vehiculo")));
        incidenciaVehiculoModel.setIdIncidencia(Integer.parseInt(FacesUtils.getRequestParameter("incidencia")));
        incidenciaVehiculoModel.administrarIncidencia();
    }

    public void subirArchivo(FileUploadEvent fileEvent) {
        boolean valid = false;
        fileInfo = fileEvent.getFile();

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

        try {

            if (addArchivo) {

                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
//		    documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setRuta(getDirectorio());
                almacenDocumentos.guardarDocumento(documentoAnexo);
                if (incidenciaVehiculoModel.isEvidencia()) {
                    valid = incidenciaVehiculoModel.guardarArchivo(
                            documentoAnexo.getNombreBase(),
                            documentoAnexo.getRuta(),
                            documentoAnexo.getTipoMime(),
                            documentoAnexo.getTamanio());
                } else {
                    valid = incidenciaVehiculoModel.guardarFactura(
                            documentoAnexo.getNombreBase(),
                            documentoAnexo.getRuta(),
                            documentoAnexo.getTipoMime(),
                            documentoAnexo.getTamanio());
                    incidenciaVehiculoModel.setFacturaVo(null);
                }

            } else {
                FacesUtils.addInfoMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

        } catch (IOException e) {
            LOGGER.fatal(e);
        } catch (SIAException e) {
            LOGGER.fatal(e);
        } catch (Exception e) {
            LOGGER.fatal(e);
        }

        if (!valid) {
            FacesUtils.addInfoMessage("Ocurrió un error al subir el archivo del Pago. Porfavor contacte al Equipo del SIA al correo soportesia@ihsa.mx");
        }
    }

    public String getDirectorio() {
        String retVal = Constantes.VACIO;
        if (incidenciaVehiculoModel.getIncidenciaVo() != null) {
            retVal = incidenciaVehiculoModel.directorio();
        }
        return retVal;
    }

    public void agregarEvidencia(ActionEvent event) {
        incidenciaVehiculoModel.setEvidencia(true);
        PrimeFaces.current().executeScript(";$('#dialogoSubirFactura').modal('show');");

    }

    public void eliminarArchivo(ActionEvent event) {
        incidenciaVehiculoModel.setIdAdjunto(Integer.parseInt(FacesUtils.getRequestParameter("adjunto")));
        int idTabla = Integer.parseInt(FacesUtils.getRequestParameter("adjunto_incidencia"));
        incidenciaVehiculoModel.eliminarArchivo(idTabla);
    }
/////Agrega factura

    public void agregarFactura(ActionEvent event) {
        incidenciaVehiculoModel.setFacturaVo(new FacturaVo());
        llenarProveedor();
        PrimeFaces.current().executeScript(";$('#gastoViaje').modal('show');");
    }

    private void llenarProveedor() {
        String proveedor = incidenciaVehiculoModel.llenarProveedor();
        PrimeFaces.current().executeScript(";llenarProveedor('frmFactura', " + proveedor + ");");
    }

    public void terminarRegistro(ActionEvent event) {
        if (incidenciaVehiculoModel.registrarFactura()) {
            incidenciaVehiculoModel.buscarFacturas();
            cerrarRegistro(event);
            FacesUtils.addInfoMessage("Se agregó la factura.");
        } else {
            FacesUtils.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx). ");
        }
    }

    public void cerrarRegistro(ActionEvent event) {
        incidenciaVehiculoModel.setFacturaVo(null);
        //    PrimeFaces.current().executeScript(";limpiarComponenteCaja();");
        cerrarDialogo("gastoViaje");
    }

    private void cerrarDialogo(String dialogo) {
        PrimeFaces.current().executeScript(";$('#" + dialogo + "').modal('hide');");
    }

    ////////////////////////////////////
    public void agregarComprobanteFactura(ActionEvent event) {
        incidenciaVehiculoModel.setFacturaVo(new FacturaVo());
        incidenciaVehiculoModel.setEvidencia(false);
        incidenciaVehiculoModel.getFacturaVo().setIdFactura(Integer.parseInt(FacesUtils.getRequestParameter("factura")));
        PrimeFaces.current().executeScript(";$('#dialogoSubirFactura').modal('show');");
    }

    public void cerrarDialogoArchivo(ActionEvent event) {
        incidenciaVehiculoModel.setFacturaVo(null);
        PrimeFaces.current().executeScript(";$('#dialogoSubirFactura').modal('hide');");
    }

    public void quitarArchivo(ActionEvent event) {
        incidenciaVehiculoModel.setFacturaVo(new FacturaVo());
        incidenciaVehiculoModel.getFacturaVo().setIdAdjunto(Integer.parseInt(FacesUtils.getRequestParameter("idAdjunto")));
        incidenciaVehiculoModel.getFacturaVo().setIdFactura(Integer.parseInt(FacesUtils.getRequestParameter("idFactura")));
        //
        incidenciaVehiculoModel.quitarArchivo();
        //
        incidenciaVehiculoModel.buscarFacturas();
        //
        incidenciaVehiculoModel.setFacturaVo(null);
    }

    public void iniciarModificarFactura(ActionEvent event) {
        incidenciaVehiculoModel.setFacturaVo(new FacturaVo());
        incidenciaVehiculoModel.getFacturaVo().setIdFactura(Integer.parseInt(FacesUtils.getRequestParameter("idFactura")));
        //
        incidenciaVehiculoModel.setFacturaVo(incidenciaVehiculoModel.llenarFactura());
        //
        incidenciaVehiculoModel.listaMoneda();
        PrimeFaces.current().executeScript(";$('#modificarFacInci').modal('show');");
    }

    public void completarModificarFactura(ActionEvent event) {
        incidenciaVehiculoModel.modificarFactura();
        //
        incidenciaVehiculoModel.buscarFacturas();
        //
        cerrarModificarFactura(event);
    }

    public void eliminarFactura(ActionEvent event) {
        incidenciaVehiculoModel.setFacturaVo(new FacturaVo());
        incidenciaVehiculoModel.getFacturaVo().setIdRelacion(Integer.parseInt(FacesUtils.getRequestParameter("idInciFac")));
        //
        incidenciaVehiculoModel.eliminarFactura();
        incidenciaVehiculoModel.buscarFacturas();
        incidenciaVehiculoModel.setFacturaVo(null);
    }

    public void cerrarModificarFactura(ActionEvent event) {
        cerrarDialogo("modificarFacInci");
        incidenciaVehiculoModel.setFacturaVo(null);
    }

    /**
     * @return the priridad
     */
    public int getPriridad() {
        return incidenciaVehiculoModel.getPriridad();
    }

    /**
     * @param priridad the priridad to set
     */
    public void setPriridad(int priridad) {
        incidenciaVehiculoModel.setPriridad(priridad);
    }

    /**
     * @return the gerencia
     */
    public int getGerencia() {
        return incidenciaVehiculoModel.getGerencia();
    }

    /**
     * @param gerencia the gerencia to set
     */
    public void setGerencia(int gerencia) {
        incidenciaVehiculoModel.setGerencia(gerencia);
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return incidenciaVehiculoModel.getTitulo();
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        incidenciaVehiculoModel.setTitulo(titulo);
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return incidenciaVehiculoModel.getDescripcion();
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        incidenciaVehiculoModel.setDescripcion(descripcion);
    }

    /**
     * @return the palabraClave
     */
    public String getPalabraClave() {
        return incidenciaVehiculoModel.getPalabraClave();
    }

    /**
     * @param palabraClave the palabraClave to set
     */
    public void setPalabraClave(String palabraClave) {
        incidenciaVehiculoModel.setPalabraClave(palabraClave);
    }

    /**
     * @return the vehiculo
     */
    public int getVehiculo() {
        return incidenciaVehiculoModel.getVehiculo();
    }

    /**
     * @param vehiculo the vehiculo to set
     */
    public void setVehiculo(int vehiculo) {
        incidenciaVehiculoModel.setVehiculo(vehiculo);
    }

    /**
     * @param incidenciaVehiculoModel the incidenciaVehiculoModel to set
     */
    public void setIncidenciaVehiculoModel(IncidenciaVehiculoModel incidenciaVehiculoModel) {
        this.incidenciaVehiculoModel = incidenciaVehiculoModel;
    }

    /**
     * @return the listaIncidencia
     */
    public List<?> getListaIncidencia() {
        return incidenciaVehiculoModel.getListaIncidencia();
    }

    /**
     * @param listaIncidencia the listaIncidencia to set
     */
    public void setListaIncidencia(List<VehiculoIncidenciaVo> listaIncidencia) {
        incidenciaVehiculoModel.setListaIncidencia(listaIncidencia);
    }

    /**
     * @return the incidenciaVo
     */
    public IncidenciaVo getIncidenciaVo() {
        return incidenciaVehiculoModel.getIncidenciaVo();
    }

    /**
     * @param incidenciaVo the incidenciaVo to set
     */
    public void setIncidenciaVo(IncidenciaVo incidenciaVo) {
        incidenciaVehiculoModel.setIncidenciaVo(incidenciaVo);
    }

    /**
     * @return the vehiculoVO
     */
    public VehiculoVO getVehiculoVO() {
        return incidenciaVehiculoModel.getVehiculoVO();
    }

    /**
     * @param vehiculoVO the vehiculoVO to set
     */
    public void setVehiculoVO(VehiculoVO vehiculoVO) {
        incidenciaVehiculoModel.setVehiculoVO(vehiculoVO);
    }

    /**
     * @return the usuarioVO
     */
    public UsuarioVO getUsuarioVO() {
        return incidenciaVehiculoModel.getUsuarioVO();
    }

    /**
     * @param usuarioVO the usuarioVO to set
     */
    public void setUsuarioVO(UsuarioVO usuarioVO) {
        incidenciaVehiculoModel.setUsuarioVO(usuarioVO);
    }

    /**
     * @return the listaAdjuntoVO
     */
    public List<AdjuntoVO> getListaAdjuntoVO() {
        return incidenciaVehiculoModel.getListaAdjuntoVO();
    }

    /**
     * @param listaAdjuntoVO the listaAdjuntoVO to set
     */
    public void setListaAdjuntoVO(List<AdjuntoVO> listaAdjuntoVO) {
        incidenciaVehiculoModel.setListaAdjuntoVO(listaAdjuntoVO);
    }

    /**
     * @return the listaFactura
     */
    public List<FacturaVo> getListaFactura() {
        return incidenciaVehiculoModel.getListaFactura();
    }

    /**
     * @param listaFactura the listaFactura to set
     */
    public void setListaFactura(List<FacturaVo> listaFactura) {
        incidenciaVehiculoModel.setListaFactura(listaFactura);
    }

    /**
     * @return the FacturaVo
     */
    public FacturaVo getFacturaVo() {
        return incidenciaVehiculoModel.getFacturaVo();
    }

    /**
     * @param facturaVo the FacturaVo to set
     */
    public void setFacturaVo(FacturaVo facturaVo) {
        incidenciaVehiculoModel.setFacturaVo(facturaVo);
    }

    /**
     * @return the listaItem
     */
    public List<SelectItem> getListaItem() {
        return incidenciaVehiculoModel.getListaItem();
    }

    /**
     * @param listaItem the listaItem to set
     */
    public void setListaItem(List<SelectItem> listaItem) {
        incidenciaVehiculoModel.setListaItem(listaItem);
    }
}
