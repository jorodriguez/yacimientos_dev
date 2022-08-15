/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.gasto.bean.backing;

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
import sia.excepciones.SIAException;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.vo.FacturaVo;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.gasto.bean.model.GastoViajeModel;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
@Named(value = "gastoViajeBean")
@RequestScoped
public class GastoViajeBean implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    @ManagedProperty(value = "#{gastoViajeModel}")
    private GastoViajeModel gastoViajeModel;

    @Getter
    @Setter
    private UploadedFile fileInfo;
    public GastoViajeBean() {
    }

    public void bsucarVaiajes(ActionEvent event) {
	gastoViajeModel.buscarViajes();
    }

    public void agregarFacturaViaje(ActionEvent event) {
	gastoViajeModel.setIdviajeFactura(Integer.parseInt(FacesUtils.getRequestParameter("viajeFactura")));
	gastoViajeModel.setFacturaVo(new FacturaVo());
	llenarProveedor();
	PrimeFaces.current().executeScript(";$(gastoViaje).modal('show');");
    }

    public void regresarPaginaViajes(ActionEvent event) {
	gastoViajeModel.setFacturaVo(null);
	gastoViajeModel.setId(0);
	gastoViajeModel.buscarViajes();
    }

    public void iniciarRegistroViaje(ActionEvent event) {
	gastoViajeModel.setId(Integer.parseInt(FacesUtils.getRequestParameter("viaje")));
	gastoViajeModel.llenarViajePorId();
	gastoViajeModel.buscarViajeros();
	gastoViajeModel.buscarFacturas();
    }

    public void agregarArchivo(ActionEvent event) {
	gastoViajeModel.setId(Integer.parseInt(FacesUtils.getRequestParameter("factura")));
	PrimeFaces.current().executeScript(";$(archivoFactura).modal('show');");
    }

    public void quitarArchivo(ActionEvent event) {
	gastoViajeModel.setId(Integer.parseInt(FacesUtils.getRequestParameter("factura")));
	gastoViajeModel.setAdjuntoVO(new AdjuntoVO());
	gastoViajeModel.getAdjuntoVO().setId(Integer.parseInt(FacesUtils.getRequestParameter("adjunto")));
	gastoViajeModel.getAdjuntoVO().setUrl(FacesUtils.getRequestParameter("rutaArchivo"));
	//
	gastoViajeModel.quitarArchivo();
	//
	gastoViajeModel.setAdjuntoVO(null);
	gastoViajeModel.setId(0);
	gastoViajeModel.buscarFacturas();
    }

    private void llenarProveedor() {
	String proveedor = gastoViajeModel.llenarProveedor();
	PrimeFaces.current().executeScript(";llenarProveedor('frmFactura', " + proveedor + ");");
    }

    public void terminarRegistro(ActionEvent event) {
	if (gastoViajeModel.registrarFactura()) {
	    gastoViajeModel.buscarFacturas();
	    cerrarRegistro(event);
	} else {
	    FacesUtils.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx). ");
	}
    }

    public void cerrarRegistro(ActionEvent event) {
	gastoViajeModel.setFacturaVo(null);
	gastoViajeModel.setId(0);
	gastoViajeModel.setIdviajeFactura(0);
	//  PrimeFaces.current().executeScript(";limpiarComponenteCaja();");
	cerrarDialogo("gastoViaje");
    }

    public String getRutaArchivo() {
	if (gastoViajeModel.getViajeVO() != null) {
	    gastoViajeModel.setDir(
                    "SGyL/Viajes/Factura/"
                            .concat(gastoViajeModel.getViajeVO().getCodigo())
                            .concat("/")
            );
	}
	return gastoViajeModel.getDir();

    }

    public void guardarArchivo(FileUploadEvent fileEvent) {
        fileInfo = fileEvent.getFile();

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
            try {
                if (addArchivo) {

                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    documentoAnexo.setRuta(getRutaArchivo());
                    almacenDocumentos.guardarDocumento(documentoAnexo);

                    gastoViajeModel.setAdjuntoVO(new AdjuntoVO());
                    gastoViajeModel.getAdjuntoVO().setUrl(documentoAnexo.getRuta() + documentoAnexo.getNombreBase());
                    gastoViajeModel.getAdjuntoVO().setNombre(documentoAnexo.getNombreBase());
                    gastoViajeModel.getAdjuntoVO().setTipoArchivo(documentoAnexo.getTipoMime());
                    gastoViajeModel.getAdjuntoVO().setTamanio(documentoAnexo.getTamanio());
                    gastoViajeModel.agregarArchivoFactura();
                    gastoViajeModel.setAdjuntoVO(null);
                    gastoViajeModel.setId(0);
                    cerrarDialogo("archivoFactura");
                } else {
                    cerrarDialogo("archivoFactura");
                    FacesUtils.addInfoMessage(new StringBuilder()
                            .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                            .append(validadorNombreArchivo.getCaracteresNoValidos())
                            .toString());
                }
            } catch (SIAException e) {
                LOGGER.fatal(e);
            }
    }

    private void cerrarDialogo(String dialogo) {
	PrimeFaces.current().executeScript(";$(" + dialogo + ").modal('hide');");
    }

    public void iniciarModificarFactura(ActionEvent event) {
	gastoViajeModel.setId(Integer.parseInt(FacesUtils.getRequestParameter("factura")));
	gastoViajeModel.setFacturaVo(gastoViajeModel.busarFacturaPorId());
	PrimeFaces.current().executeScript(";$(modificarGastoViaje).modal('show');");
    }

    public void modificarFactura(ActionEvent event) {
	gastoViajeModel.modificarFactura();
	gastoViajeModel.setFacturaVo(null);
	cerrarDialogo("modificarGastoViaje");
	gastoViajeModel.buscarFacturas();
	//   PrimeFaces.current().executeScript(";limpiarComponenteCaja();");
    }

    public void cerrarModificacionRegistro(ActionEvent event) {
	gastoViajeModel.setFacturaVo(null);
	gastoViajeModel.setId(0);
	//   PrimeFaces.current().executeScript(";limpiarComponenteCaja();");
	cerrarDialogo("modificarGastoViaje");
    }

    public void eliminarFactura(ActionEvent event) {
	gastoViajeModel.setId(Integer.parseInt(FacesUtils.getRequestParameter("factura")));
	gastoViajeModel.setIdviajeFactura(Integer.parseInt(FacesUtils.getRequestParameter("viajeFactura")));
	gastoViajeModel.eliminarFactura();
	gastoViajeModel.eliminarViajeFactura();
	gastoViajeModel.buscarFacturas();
    }

    /**
     * @param gastoViajeModel the gastoViajeModel to set
     */
    public void setGastoViajeModel(GastoViajeModel gastoViajeModel) {
	this.gastoViajeModel = gastoViajeModel;
    }

    /**
     * @return the inicio
     */
    public String getInicio() {
	return gastoViajeModel.getInicio();
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(String inicio) {
	gastoViajeModel.setInicio(inicio);
    }

    /**
     * @return the fin
     */
    public String getFin() {
	return gastoViajeModel.getFin();
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(String fin) {
	gastoViajeModel.setFin(fin);
    }

    /**
     * @return the lista
     */
    public List getLista() {
	return gastoViajeModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List lista) {
	gastoViajeModel.setLista(lista);
    }

    /**
     * @return the facturaVo
     */
    public FacturaVo getFacturaVo() {
	return gastoViajeModel.getFacturaVo();
    }

    /**
     * @param facturaVo the facturaVo to set
     */
    public void setFacturaVo(FacturaVo facturaVo) {
	gastoViajeModel.setFacturaVo(facturaVo);
    }

    /**
     * @return the id
     */
    public int getId() {
	return gastoViajeModel.getId();
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
	gastoViajeModel.setId(id);
    }

    /**
     * @return the viajeVO
     */
    public ViajeVO getViajeVO() {
	return gastoViajeModel.getViajeVO();
    }

    /**
     * @param viajeVO the viajeVO to set
     */
    public void setViajeVO(ViajeVO viajeVO) {
	gastoViajeModel.setViajeVO(viajeVO);
    }

    /**
     * @return the listaItem
     */
    public List<SelectItem> getListaItem() {
	return gastoViajeModel.getListaItem();
    }

    /**
     * @param listaItem the listaItem to set
     */
    public void setListaItem(List<SelectItem> listaItem) {
	gastoViajeModel.setListaItem(listaItem);
    }

    /**
     * @return the adjuntoVO
     */
    public AdjuntoVO getAdjuntoVO() {
	return gastoViajeModel.getAdjuntoVO();
    }

    /**
     * @param adjuntoVO the adjuntoVO to set
     */
    public void setAdjuntoVO(AdjuntoVO adjuntoVO) {
	gastoViajeModel.setAdjuntoVO(adjuntoVO);
    }

    /**
     * @return the listaFactura
     */
    public List getListaFactura() {
	return gastoViajeModel.getListaFactura();
    }

    /**
     * @param listaFactura the listaFactura to set
     */
    public void setListaFactura(List listaFactura) {
	gastoViajeModel.setListaFactura(listaFactura);
    }

    /**
     * @param dir the dir to set
     */
    public void setDir(String dir) {
	gastoViajeModel.setDir(dir);
    }
}
