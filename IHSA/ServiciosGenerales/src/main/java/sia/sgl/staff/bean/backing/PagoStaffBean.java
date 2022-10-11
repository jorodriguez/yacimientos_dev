/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.staff.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.excepciones.SIAException;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioStaff;
import sia.modelo.SgStaff;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.staff.bean.model.PagoStaffModel;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author ihsa
 */
@Named(value = "pagoStaffBean")
@RequestScoped
public class PagoStaffBean implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    @Getter
    @Setter
    private UploadedFile fileInfo;
    private final static UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of pagoStaffBean
     */
    public PagoStaffBean() {
    }
    @ManagedProperty(value = "#{pagoStaffModel}")
    private PagoStaffModel pagoStaffModel;

    public void traerPagoStaff(ValueChangeEvent valueChangeEvent) {
        pagoStaffModel.setIdTipoEspecifico((Integer) valueChangeEvent.getNewValue());
        pagoStaffModel.traerPagoPorStaff();
    }

    public void buscarStaff(ValueChangeEvent valueChangeEvent) {
        pagoStaffModel.setIdStaff((Integer) valueChangeEvent.getNewValue());
        pagoStaffModel.setLista(null);
        pagoStaffModel.buscarObjetoStaff();
//        pagoStaffModel.setIdTipoEspecifico(-1);
        pagoStaffModel.traerTipoEspecificoPorTipoStaff();
        if (this.pagoStaffModel.getIdTipoEspecifico() > 0) {
            this.pagoStaffModel.traerPagoPorStaff();
        } else {
            this.pagoStaffModel.setLista(null);
        }
    }

    public List<SelectItem> getTraerTipoEspStaff() {
        pagoStaffModel.traerTipoEspecificoPorTipoStaff();
        return pagoStaffModel.getListaTipoEspecifico();
    }

    public List<SelectItem> getTraerCasaStaff() {
        return pagoStaffModel.getListaPagos();
    }

    public DataModel getTraerPorTipoEspecificoStaff() {
        try {
            return pagoStaffModel.getLista();
        } catch (Exception e) {
            return null;
        }
    }

    public void seleccionarPagoStaff(ActionEvent event) {
        pagoStaffModel.setSgPagoServicioStaff((SgPagoServicioStaff) pagoStaffModel.getLista().getRowData());
        pagoStaffModel.setSgPagoServicio(pagoStaffModel.getSgPagoServicioStaff().getSgPagoServicio());
        pagoStaffModel.setIdMoneda(pagoStaffModel.getSgPagoServicio().getMoneda().getId());
        pagoStaffModel.setModificarPopUp(true);
    }

    public void eliminarPagoServicioStaff(ActionEvent event) {
        pagoStaffModel.setSgPagoServicioStaff((SgPagoServicioStaff) pagoStaffModel.getLista().getRowData());
        pagoStaffModel.setSgPagoServicio(pagoStaffModel.getSgPagoServicioStaff().getSgPagoServicio());
        pagoStaffModel.setEliminarPop(true);
    }

    public void completarEliminarPagoStaff(ActionEvent event) {
        pagoStaffModel.eliminarPagoServicioStaff();
        pagoStaffModel.setSgPagoServicio(null);
        pagoStaffModel.setEliminarPop(false);
        pagoStaffModel.traerPagoPorStaff();
    }

    public void subirComprobanteStaff(ActionEvent event) {
        pagoStaffModel.setSgPagoServicioStaff((SgPagoServicioStaff) pagoStaffModel.getLista().getRowData());
        pagoStaffModel.setSgPagoServicio(pagoStaffModel.getSgPagoServicioStaff().getSgPagoServicio());
        pagoStaffModel.setSubirArchivo(true);
    }

    public void eliminarComprobanteStaff(ActionEvent event) {
        pagoStaffModel.setSgPagoServicioStaff((SgPagoServicioStaff) pagoStaffModel.getLista().getRowData());
        pagoStaffModel.setSgPagoServicio(pagoStaffModel.getSgPagoServicioStaff().getSgPagoServicio());
        pagoStaffModel.eliminarComprobante();
        pagoStaffModel.setSgPagoServicio(null);
    }

    public void subirArchivoPago(FileUploadEvent fileEvent) {
        boolean valid = false;
        fileInfo = fileEvent.getFile();

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

        try {
            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta(getDir());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                valid
                        = pagoStaffModel.guardarArchivo(
                                documentoAnexo.getNombreBase(),
                                documentoAnexo.getTipoMime(),
                                documentoAnexo.getTamanio()
                        );

                pagoStaffModel.setSubirArchivo(false);
                pagoStaffModel.setSgPagoServicio(null);

            } else {
                FacesUtils.addInfoMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();
        } catch (IOException e) {
            LOGGER.fatal(this, "Excepcion al subir archivo", e);
        } catch (SIAException e) {
            LOGGER.fatal(this, "Excepcion al subir archivo", e);
        }

        if (!valid) {
            FacesUtils.addInfoMessage("Ocurrió un error al subir el archivo del Pago. Porfavor contacte al Equipo del SIA al correo soportesia@ihsa.mx");
        }
    }

    public void cerrarPopSubirArchivo(ActionEvent event) {

        pagoStaffModel.setSgPagoServicio(null);
        pagoStaffModel.setSubirArchivo(false);
    }

    public List<SelectItem> getTraerMondeda() {
        return pagoStaffModel.traerMoneda();
    }

    public void proveedorListener(String textChangeEvent) {
        pagoStaffModel.setListaProveedor(regresaProveedorActivo(textChangeEvent));
        pagoStaffModel.setPro(textChangeEvent);
    }

    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (Iterator it = pagoStaffModel.getListaProveedorBuscar().iterator(); it.hasNext();) {
            String string = (String) it.next();
            if (string != null) {
                if (string.toLowerCase().startsWith(cadenaDigitada.toLowerCase())) {
                    SelectItem item = new SelectItem(string);
                    list.add(item);
                }
            }
        }
        return list;
    }

    public void setListaProveedor(List<SelectItem> listaProveedor) {
        pagoStaffModel.setListaProveedor(listaProveedor);
    }

    public void guardarPagoServicioStaff(ActionEvent event) {

        if (pagoStaffModel.buscarProveedorPorNombre() == null) {
            FacesUtils.addErrorMessage("Proveedor es requerido");
        } else if (pagoStaffModel.getSgPagoServicio().getFechaInicio() == null) {
            FacesUtils.addErrorMessage("Fecha de Inicio es requerido");
        } else if (pagoStaffModel.getSgPagoServicio().getFechaFin() == null) {
            FacesUtils.addErrorMessage("Fecha Fin es requerido");
        } else if (pagoStaffModel.getSgPagoServicio().getFechaVencimiento() == null) {
            FacesUtils.addErrorMessage("Fecha Vencimiento es requerido");
        } else if (pagoStaffModel.getIdMoneda() < 1) {
            FacesUtils.addErrorMessage("Moneda es requerido");
        } else if (pagoStaffModel.getSgPagoServicio().getImporte() == null) {
            FacesUtils.addErrorMessage("Importe es requerido");
        } else {
            if (pagoStaffModel.guardarPagoServicioStaff()) {
                pagoStaffModel.setPopUp(false);
                pagoStaffModel.setCrearPopUp(false);
                pagoStaffModel.setModificarPopUp(false);
                pagoStaffModel.setSgPagoServicio(null);
                pagoStaffModel.getLista();
                pagoStaffModel.setPro(null);
                pagoStaffModel.setIdMoneda(0);
            } else {
                FacesUtils.addErrorMessage("Ocurrió un error al guardar el Pago de la Oficina. Por favor contacte al Equipo del SIA al correo soportesia@ihsa.mx");
            }
        }
    }

    public void cerraPopPagoServicio(ActionEvent event) {
        pagoStaffModel.setSgPagoServicio(null);
        pagoStaffModel.setPopUp(false);
        pagoStaffModel.setModificarPopUp(false);
        pagoStaffModel.getLista();
        pagoStaffModel.getListaComedor();
        pagoStaffModel.setPro("");
    }

    public void modificarPagoServicio(ActionEvent event) {
        if (pagoStaffModel.getSgPagoServicio().getFechaInicio() == null) {
            FacesUtils.addErrorMessage("Fecha de Inicio es requerido");
        } else if (pagoStaffModel.getSgPagoServicio().getFechaFin() == null) {
            FacesUtils.addErrorMessage("Fecha Fin es requerido");
        } else if (pagoStaffModel.getSgPagoServicio().getFechaVencimiento() == null) {
            FacesUtils.addErrorMessage("Fecha de Vencimiento es requerido");
        } else if (pagoStaffModel.getSgPagoServicio().getImporte() != null) {
            BigDecimal importe = new BigDecimal(pagoStaffModel.getSgPagoServicio().getImporte().longValue());
            if (pagoStaffModel.getIdMoneda() < 1) {
                FacesUtils.addErrorMessage("Moneda es requerido");
            } else if (importe.longValue() < 1) {
                FacesUtils.addErrorMessage("Importes es requerido");
            } else {
                pagoStaffModel.modificarPagoServicio();
                pagoStaffModel.setModificarPopUp(false);
                pagoStaffModel.setSgPagoServicio(null);
            }
        } else {
            FacesUtils.addErrorMessage("Importes es requerido");
        }
    }

    public void cerraPopModificarPagoServicio(ActionEvent event) {
        pagoStaffModel.traerPagoPorOficina();
        pagoStaffModel.setModificarPopUp(false);
        pagoStaffModel.setSgPagoServicio(null);
    }

    public void cerrarPopEliminar(ActionEvent event) {
        pagoStaffModel.setEliminarPop(false);
    }

    public String getDir() {
        String retVal = "";

        if (pagoStaffModel.getSgPagoServicio() != null) {
            retVal = pagoStaffModel.getDirectorio();
        }

        return retVal;
    }

    public void agregarPagoServicioOficina(ActionEvent event) {
        pagoStaffModel.setPro("");
        pagoStaffModel.setPopUp(true);
        pagoStaffModel.setSgPagoServicio(null);
        pagoStaffModel.setSgPagoServicio(new SgPagoServicio());
        pagoStaffModel.setCrearPopUp(true);
    }

    /**
     * @return the popUp
     */
    public boolean isPopUp() {
        return pagoStaffModel.isPopUp();
    }

    /**
     * @param popUp the popUp to set
     */
    public void setPopUp(boolean popUp) {
        pagoStaffModel.setPopUp(popUp);
    }

    /**
     * @return the modificarPopUp
     */
    public boolean isModificarPopUp() {
        return pagoStaffModel.isModificarPopUp();
    }

    /**
     * @param modificarPopUp the modificarPopUp to set
     */
    public void setModificarPopUp(boolean modificarPopUp) {
        pagoStaffModel.setModificarPopUp(modificarPopUp);
    }

    /**
     * @return the sgTipoEspecifico
     */
    public SgTipoEspecifico getSgTipoEspecifico() {
        return pagoStaffModel.getSgTipoEspecifico();
    }

    /**
     * @param sgTipoEspecifico the sgTipoEspecifico to set
     */
    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
        pagoStaffModel.setSgTipoEspecifico(sgTipoEspecifico);
    }

    /**
     * @return the idStaff
     */
    public int getIdStaff() {
        return pagoStaffModel.getIdStaff();
    }

    /**
     * @param idStaff the idStaff to set
     */
    public void setIdStaff(int idStaff) {
        pagoStaffModel.setIdStaff(idStaff);
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
        return pagoStaffModel.getIdTipoEspecifico();
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
        pagoStaffModel.setIdTipoEspecifico(idTipoEspecifico);
    }

    /**
     * @return the opcionPagar
     */
    public String getOpcionPagar() {
        return pagoStaffModel.getOpcionPagar();
    }

    /**
     * @param opcionPagar the opcionPagar to set
     */
    public void setOpcionPagar(String opcionPagar) {
        pagoStaffModel.setOpcionPagar(opcionPagar);
    }

    /**
     * @return the sgTipo
     */
    public SgTipo getSgTipo() {
        return pagoStaffModel.getSgTipo();
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(SgTipo sgTipo) {
        pagoStaffModel.setSgTipo(sgTipo);
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return pagoStaffModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        pagoStaffModel.setLista(lista);
    }

    /**
     * @return the listaProveedorBuscar
     */
    public List<String> getListaProveedorBuscar() {
        return pagoStaffModel.getListaProveedorBuscar();
    }

    /**
     * @param listaProveedorBuscar the listaProveedorBuscar to set
     */
    public void setListaProveedorBuscar(List<String> listaProveedorBuscar) {
        pagoStaffModel.setListaProveedorBuscar(listaProveedorBuscar);
    }

    /**
     * @return the listaPagos
     */
    public List<SelectItem> getListaPagos() {
        return pagoStaffModel.getListaPagos();
    }

    /**
     * @param listaPagos the listaPagos to set
     */
    public void setListaPagos(List<SelectItem> listaPagos) {
        pagoStaffModel.setListaPagos(listaPagos);
    }

    /**
     * @return the eliminarPop
     */
    public boolean isEliminarPop() {
        return pagoStaffModel.isEliminarPop();
    }

    /**
     * @param eliminarPop the eliminarPop to set
     */
    public void setEliminarPop(boolean eliminarPop) {
        pagoStaffModel.setEliminarPop(eliminarPop);
    }

    /**
     * @return the subirArchivo
     */
    public boolean isSubirArchivo() {
        return pagoStaffModel.isSubirArchivo();
    }

    /**
     * @param subirArchivo the subirArchivo to set
     */
    public void setSubirArchivo(boolean subirArchivo) {
        pagoStaffModel.setSubirArchivo(subirArchivo);
    }

    /**
     * @return the sgStaff
     */
    public SgStaff getSgStaff() {
        return pagoStaffModel.getSgStaff();
    }

    /**
     * @return the sgPagoServicio
     */
    public SgPagoServicio getSgPagoServicio() {
        return pagoStaffModel.getSgPagoServicio();
    }

    /**
     * @param sgPagoServicio the sgPagoServicio to set
     */
    public void setSgPagoServicio(SgPagoServicio sgPagoServicio) {
        pagoStaffModel.setSgPagoServicio(sgPagoServicio);
    }

    /**
     * @return the pro
     */
    public String getPro() {
        return pagoStaffModel.getPro();
    }

    /**
     * @param pro the pro to set
     */
    public void setPro(String pro) {
        pagoStaffModel.setPro(pro);
    }

    /**
     * @return the sgPagoServicioStaff
     */
    public SgPagoServicioStaff getSgPagoServicioStaff() {
        return pagoStaffModel.getSgPagoServicioStaff();
    }

    /**
     * @param sgPagoServicioStaff the sgPagoServicioStaff to set
     */
    public void setSgPagoServicioStaff(SgPagoServicioStaff sgPagoServicioStaff) {
        pagoStaffModel.setSgPagoServicioStaff(sgPagoServicioStaff);
    }

    /**
     * @return the idMoneda
     */
    public int getIdMoneda() {
        return pagoStaffModel.getIdMoneda();
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdMoneda(int idMoneda) {
        pagoStaffModel.setIdMoneda(idMoneda);
    }

    /**
     * @return the crearPopUp
     */
    public boolean isCrearPopUp() {
        return pagoStaffModel.isCrearPopUp();
    }

    /**
     * @param crearPopUp the crearPopUp to set
     */
    public void setCrearPopUp(boolean crearPopUp) {
        pagoStaffModel.setCrearPopUp(crearPopUp);
    }

    /**
     * @return the listaProveedor
     */
    public List<SelectItem> getListaProveedor() {
        return pagoStaffModel.getListaProveedor();
    }

    /**
     * @param pagoStaffModel the pagoStaffModel to set
     */
    public void setPagoStaffModel(PagoStaffModel pagoStaffModel) {
        this.pagoStaffModel = pagoStaffModel;
    }

}
