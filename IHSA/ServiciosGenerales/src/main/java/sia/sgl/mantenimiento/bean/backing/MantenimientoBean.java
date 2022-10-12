/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.mantenimiento.bean.backing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
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
import sia.modelo.SgEstadoVehiculo;
import sia.modelo.SgVehiculoMantenimiento;
import sia.modelo.sgl.vehiculo.vo.SgKilometrajeVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.sgl.mantenimiento.bean.model.MantenimientoBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author jrodriguez
 * @modify mluis
 */
@Named(value = "mantenimientoBean")
@RequestScoped
public class MantenimientoBean implements Serializable {

    @ManagedProperty(value = "#{mantenimientoBeanModel}")
    private MantenimientoBeanModel mantenimientoBeanModel;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Getter
    @Setter
    private UploadedFile fileInfo;
    private final static UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of mantenimientoBean
     */
    public MantenimientoBean() {
    }

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
        LOGGER.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            LOGGER.info(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    public void cargarNombreProveedorSeleccionado(ValueChangeEvent event) {
        LOGGER.info(this, "buscarNombreProveedorSeleccionado");
        if (event.getNewValue() != null) {
            mantenimientoBeanModel.setIdProveedorSeleecionado(Integer.parseInt(event.getNewValue().toString()));
        }
    }

    public void nuevoMantenimiento() {
        //limpiar y traer todo
        mantenimientoBeanModel.setKilometrajeActual(0);
        mantenimientoBeanModel.traerMantenimientoTipoEspecificoItems();
        //mantenimientoBeanModel.setSeleccionRadio("Ase");
        //mantenimientoBeanModel.setOperacionProveedor("ASEGURADORA");
        mantenimientoBeanModel.setSeleccionRadio("Pro");
        mantenimientoBeanModel.setOperacionProveedor("TALLER");
//        mantenimientoBeanModel.traerListaAseguradorasItems();
        mantenimientoBeanModel.traerListaTalleresItems();
        mantenimientoBeanModel.setIdProveedorSeleecionado(-1);
        //mantenimientoBeanModel.setIdTipoMantenimientoEspecifico(-1);
        mantenimientoBeanModel.setFechaIngreso(new Date());
        mantenimientoBeanModel.traerKilometrajeActualOld();
    }

    public void registrarEntradaMantenimiento(ActionEvent event) {
        LOGGER.info(this, "registrando mantenimiento");

        //Checar SI existe un mantenimiento activo terminarlo..para que no aparescan las opciones
        if (mantenimientoBeanModel.getIdProveedorSeleecionado() != -1) {
            if (mantenimientoBeanModel.getIdTipoMantenimientoEspecifico() != -1) {
                if (mantenimientoBeanModel.getFechaIngreso() != null) {
                    if (mantenimientoBeanModel.getKilometrajeActual() != null) {
                        if (validarKilometraje()) {
                            //validar que no este activo un mmto.
                            mantenimientoBeanModel.registrarEntradaMantenimiento();

                        }
                    } else {
                        FacesUtils.addErrorMessage("Por favor escriba el kilometraje...");
                    }
                } else {
                    FacesUtils.addErrorMessage("Por favor escriba una fecha de ingreso...");
                }
            } else {
                FacesUtils.addErrorMessage("Por favor elija un tipo de mantenimiento...");
            }
        } else {
            FacesUtils.addErrorMessage("Por favor elija un proveedor...");
        }
    }

    public void modificarRegistroEntradaMantenimiento(ActionEvent event) {
        LOGGER.info(this, "Modifcarndo el registro de mantenimiento");
        if (mantenimientoBeanModel.getIdTipoMantenimientoEspecifico() != -1) {
            if (mantenimientoBeanModel.getFechaIngreso() != null) {
                if (mantenimientoBeanModel.getKilometrajeActual() != null) {
                    if (validarKilometraje()) {
                        mantenimientoBeanModel.modificarRegistroEntradaMantenimiento();
                        mantenimientoBeanModel.traerListaMantenimientos();
                        mantenimientoBeanModel.traerKilometrajeActualOld();
                        mantenimientoBeanModel.setMrPopupModificarEntrada(true);
                        mantenimientoBeanModel.setMrPopupEntrada(false);
                        LOGGER.info(this, "pop entrada" + mantenimientoBeanModel.isMrPopupEntrada());
                        LOGGER.info(this, "pop modific entrada" + mantenimientoBeanModel.isMrPopupModificarEntrada());
                    }
                } else {
                    FacesUtils.addErrorMessage("Por favor escriba el kilometraje...");
                }
            } else {
                FacesUtils.addErrorMessage("Por favor escriba una fecha de ingreso...");
            }
        } else {
            FacesUtils.addErrorMessage("Por favor elija un tipo de mantenimiento...");
        }
    }

    public void registrarSalidaMantenimiento(ActionEvent event) {
        LOGGER.info(this, "registrando salida de mantenimiento");
        try {
            LOGGER.info(this, "Mantenimiento vehiculo" + mantenimientoBeanModel.getSgMantenimiento().getSgVehiculo().getSerie());
            LOGGER.info(this, "Mantenimiento " + mantenimientoBeanModel.getSgMantenimiento().toString());
            if (mantenimientoBeanModel.isCapturaProximoMantto()) {
                if (mantenimientoBeanModel.getSgMantenimiento().getFechaSalida() != null) {
                    LOGGER.info(this, "Importe string " + mantenimientoBeanModel.getSgMantenimiento().getImporte().toString());
                    LOGGER.info(this, "Importe double" + mantenimientoBeanModel.getSgMantenimiento().getImporte().doubleValue());
                    if (mantenimientoBeanModel.getSgMantenimiento().getImporte() != null && mantenimientoBeanModel.getSgMantenimiento().getImporte().doubleValue() > 0) {
                        LOGGER.info(this, "importe != null");
                        if (mantenimientoBeanModel.getIdMoneda() != -1) {
                            if (mantenimientoBeanModel.getSgMantenimiento().getProxMantenimientoFecha() != null) {
                                if (mantenimientoBeanModel.getSgMantenimiento().getProxMantenimientoKilometraje() != null) {
                                    if (validarProximonMantenimientoKilometraje()) {
                                        mantenimientoBeanModel.registrarSalidaMantenimiento();
                                        mantenimientoBeanModel.traerListaMantenimientos();
                                        mantenimientoBeanModel.traerKilometrajeActualOld();
                                        mantenimientoBeanModel.setMrPopupSalida(false);
                                    }
                                } else {
                                    FacesUtils.addErrorMessage("Por favor escriba un kilometraje para el proximo Mtto. ..");
                                }
                            } else {
                                FacesUtils.addErrorMessage("Por favor elija una fecha para el proximo Mtto. ..");
                            }
                        } else {
                            FacesUtils.addErrorMessage("Por favor elija una moneda..");
                        }
                    } else {
                        FacesUtils.addErrorMessage("Por favor escriba un importe mayor a 0..");
                    }
                } else {
                    FacesUtils.addErrorMessage("Por favor elija una fecha de salida.");
                }

            } else {
                LOGGER.info(this, "no se captura el proximo mtto.");
                if (mantenimientoBeanModel.getSgMantenimiento().getFechaSalida() != null) {
                    LOGGER.info(this, "Importe string " + mantenimientoBeanModel.getSgMantenimiento().getImporte().toString());
                    LOGGER.info(this, "Importe double" + mantenimientoBeanModel.getSgMantenimiento().getImporte().doubleValue());
                    if (mantenimientoBeanModel.getSgMantenimiento().getImporte() != null && mantenimientoBeanModel.getSgMantenimiento().getImporte().doubleValue() > 0) {
                        LOGGER.info(this, "importe != null");
                        if (mantenimientoBeanModel.getIdMoneda() != -1) {
                            LOGGER.info(this, "moneda != -1");
                            mantenimientoBeanModel.registrarSalidaMantenimiento();
                            mantenimientoBeanModel.traerListaMantenimientos();
                            mantenimientoBeanModel.traerKilometrajeActualOld();
                            mantenimientoBeanModel.setMrPopupSalida(false);
                        } else {
                            FacesUtils.addErrorMessage("Por favor elija una moneda..");
                        }
                    } else {
                        FacesUtils.addErrorMessage("Por favor escriba un importe..");
                    }
                } else {
                    FacesUtils.addErrorMessage("Por favor elija una fecha de salida.");
                }
            }
        } catch (Exception e) {
            LOGGER.info(this, "Excepcion en bean recibir vehiculo" + e.getMessage());
        }
    }

    public void eliminarRegistroEntradaMantenimiento(ActionEvent event) {
        try {
            LOGGER.info(this, "eliminarRegistroEntradaMantenimiento");
            this.setIdMantenimientoSeleccionado(tomarParametro());
            mantenimientoBeanModel.setSgMantenimiento(mantenimientoBeanModel.findSgVehiculoMantenimiento());
            mantenimientoBeanModel.eliminarRegistroEntradaMantenimiento();
            mantenimientoBeanModel.traerListaMantenimientos();

        } catch (Exception e) {
            LOGGER.info(this, "Excepcion en la eliminacion " + e.getMessage());
        }
    }

    //Toma el parametro que envio al dar clic en la lista de mantenimientos, este paremtro define en que lista estoy
    private Integer tomarParametro() {
        LOGGER.info(this, "IdMantenimiento " + Integer.parseInt(FacesUtils.getRequestParameter("idMantenimiento")));
        return Integer.parseInt(FacesUtils.getRequestParameter("idMantenimiento"));
        //return Integer.getInteger(FacesUtils.getRequestParameter("idMantenimiento"));
    }

    public void seleccionarOperacion(ValueChangeEvent event) {
        LOGGER.info(this, "Seleccionar operacion " + event.getNewValue().toString());
        try {
            mantenimientoBeanModel.setSeleccionRadio(event.getNewValue().toString());
            LOGGER.info(this, "Seleccion" + mantenimientoBeanModel.getSeleccionRadio());
            if (mantenimientoBeanModel.getSeleccionRadio().equals("Ase"))//Consultar Aseguradoras
            {
                LOGGER.info(this, "Selecciono Aseguradoras");
                mantenimientoBeanModel.setOperacionProveedor("ASEGURADORA");
            } else if (mantenimientoBeanModel.getSeleccionRadio().equals("Pro")) {
                LOGGER.info(this, "selecciono taller");
                mantenimientoBeanModel.setOperacionProveedor("TALLER");
            }
        } catch (Exception e) {
            LOGGER.info(this, "Excepcion al seleecionar operacion " + e.getMessage());
        }
    }

    public void seleccionarTipoMantenimiento(ValueChangeEvent event) {
        LOGGER.info(this, "Seleccionar Tipo de mantenimiento");

        if (event.getNewValue() != null) {
//            mantenimientoBeanModel.setIdTipoMantenimientoEspecifico(Integer.parseInt(event.getNewValue().toString()));
        }
    }

    /**
     * ********************** VALIDACIONES *******************************
     */
    public boolean validarKilometraje() {
        boolean retVal = true;

        if (mantenimientoBeanModel.getKilometrajeActual() < mantenimientoBeanModel.getKilometrajeOld()) {
            FacesUtils.addErrorMessage("No se puede agregar un kilometraje menor a " + mantenimientoBeanModel.getKilometrajeOld() + " Km.");
            retVal = false;
        }
        return retVal;
    }

    public boolean validarProximonMantenimientoKilometraje() {
        boolean retVal = true;

        mantenimientoBeanModel.traerKilometrajeActualOld();
        if (mantenimientoBeanModel.getSgMantenimiento().getProxMantenimientoKilometraje() < mantenimientoBeanModel.getKilometrajeOld()) {
            FacesUtils.addErrorMessage("No se puede agregar un kilometraje menor a " + mantenimientoBeanModel.getKilometrajeOld() + " Km.");
            retVal = false;
        }
        return retVal;
    }

    public void validaFecha(FacesContext context, UIComponent validate, Object value) {
        Date f = (Date) value;
        Date fechaHoy = new Date();
        LOGGER.info(this, "Fecha V +" + f);

        if (f.compareTo(fechaHoy) < 0) {
            ((UIInput) validate).setValid(false);
            FacesMessage msg = new FacesMessage("Elija una fecha mayor a hoy");
            context.addMessage(validate.getClientId(context), msg);
        }
    }

    /**
     */
    public void subirComprobanteMantenimiento(FileUploadEvent fileEvent) throws Exception {
        boolean valid = false;
        fileInfo = fileEvent.getFile();

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

        if (addArchivo) {

            try {

                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta(getDirComprobanteMantenimiento());
                almacenDocumentos.guardarDocumento(documentoAnexo);
                valid
                        = mantenimientoBeanModel.guardarArchivo(
                                documentoAnexo.getNombreBase(),
                                documentoAnexo.getRuta(),
                                documentoAnexo.getTipoMime(),
                                documentoAnexo.getTamanio()
                        );
                mantenimientoBeanModel.traerListaMantenimientos();
                mantenimientoBeanModel.setArchivoMantenimineto(false);
                mantenimientoBeanModel.setMrSubirArchivo(false);
            } catch (SIAException e) {
                LOGGER.error(e);
            }

            if (!valid) {
                FacesUtils.addErrorMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
            }
        } else {
            FacesUtils.addInfoMessage(new StringBuilder()
                    .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                    .append(validadorNombreArchivo.getCaracteresNoValidos())
                    .toString());
        }

        fileInfo.delete();
    }

    public void eliminarArchivo(ActionEvent event) {
        LOGGER.info(this, "eliminarArchivo");
        //mantenimientoBeanModel.setSgMantenimiento((SgVehiculoMantenimiento) mantenimientoBeanModel.getMantenimientoDataModel().getRowData());
        this.setIdMantenimientoSeleccionado(tomarParametro());
        mantenimientoBeanModel.setSgMantenimiento(mantenimientoBeanModel.findSgVehiculoMantenimiento());
        mantenimientoBeanModel.quitarArchivo();
        mantenimientoBeanModel.traerListaMantenimientos();
    }

    public void uploadFile() {
        LOGGER.info(this, "upload");
    }

    public void traerDirectorio() {
        mantenimientoBeanModel.traerDirMantenimiento();
    }

    public String getDirComprobanteMantenimiento() {
        return mantenimientoBeanModel.getDirectorioPath();
    }

    public boolean getMrSubirArchivo() {
        return mantenimientoBeanModel.isMrSubirArchivo();
    }

    public String getNombreEstadoOld() {
        return mantenimientoBeanModel.getNombreEstadoVehiculoOld();
    }

    public SgEstadoVehiculo getSgEstadoVehiculoOld() {
        return mantenimientoBeanModel.getSgEstadoVehiculoOld();
    }

    public SgVehiculoMantenimiento getSgMantenimiento() {
        return mantenimientoBeanModel.getSgMantenimiento();
    }

    public boolean getCapturaProximoMtto() {
        return mantenimientoBeanModel.isCapturaProximoMantto();
    }

    public String getOperacionTerminarRegistro() {
        return mantenimientoBeanModel.getOperacionTerminarRegistro();
    }

    public Integer getKilometrajeActualOld() {
        return mantenimientoBeanModel.getKilometrajeOld();
    }

    public VehiculoVO getSgVehiculo() {
        return this.mantenimientoBeanModel.getSgVehiculoSeleccionado();
    }

    public List<SelectItem> getListaAseguradorasItems() {
        LOGGER.info(this, "mantenimientoBean.getListaAseguradorasItems");
        return this.mantenimientoBeanModel.getListaAseguradorasProveedoresItems();
    }

    public List<SelectItem> getListaTalleresItems() {
        LOGGER.info(this, "mantenimientoBean.getListaTalleresItems");
        return this.mantenimientoBeanModel.getListaTalleresProveedoresItems();
    }

    public List<SelectItem> getListaTipoMantenimientoItems() {
        LOGGER.info(this, "mantenimientoBeanModel.getTraerTipoMatenimiento");
        return this.mantenimientoBeanModel.getListaTiposMantenimientoItems();
    }

    public String getSeleccionRadio() {
        return mantenimientoBeanModel.getSeleccionRadio();
    }

    public Date getFechaIngreso() {
        return mantenimientoBeanModel.getFechaIngreso();
    }

    /*
     * MODIFICAR KILOMETRAJE
     */
    public SgKilometrajeVo getKilometrajeAnteriorBueno() {
        return mantenimientoBeanModel.getKilometrajeAnteriorBueno();
    }

    public SgKilometrajeVo getKilometrajeActualVo() {
        return mantenimientoBeanModel.getKilometrajeActualVo();
    }

    public void setFechaIngreso(Date fechaIngreso) {
        mantenimientoBeanModel.setFechaIngreso(fechaIngreso);
    }

    public void setSeleccionRadio(String seleccionRadio) {
        mantenimientoBeanModel.setSeleccionRadio(seleccionRadio);
    }

    public int getIdProveedorSeleccionado() {
        return mantenimientoBeanModel.getIdProveedorSeleecionado();
    }

    public void setIdProveedorSeleccionado(int idProveedorSeleccionado) {
        mantenimientoBeanModel.setIdProveedorSeleecionado(idProveedorSeleccionado);
    }

    public String getNombreProveedorSeleccionado() {
        return mantenimientoBeanModel.getNombreProveedor();
    }

    public void setNombreProveedorSeleccionado(String nombreProveedorSeleccionado) {
        mantenimientoBeanModel.setNombreProveedor(nombreProveedorSeleccionado);
    }

    public int getIdTipoMantenimientoEspecifico() {
        return mantenimientoBeanModel.getIdTipoMantenimientoEspecifico();
    }

    public void setIdTipoMantenimientoEspecifico(int idTipoMantenimientoEspecifico) {
        mantenimientoBeanModel.setIdTipoMantenimientoEspecifico(idTipoMantenimientoEspecifico);
    }

    public Integer getKilometraje() {
        return mantenimientoBeanModel.getKilometrajeActual();
    }

    public void setKilometraje(Integer kilometraje) {
        mantenimientoBeanModel.setKilometrajeActual(kilometraje);
    }

    public String getObservaciones() {
        return mantenimientoBeanModel.getObservaciones();
    }

    public void setObservaciones(String obervaciones) {
        mantenimientoBeanModel.setObservaciones(obervaciones);
    }

    public String getOperacionProveedor() {
        return mantenimientoBeanModel.getOperacionProveedor();
    }

    public List<SelectItem> getListaMondedaItems() {
        return mantenimientoBeanModel.getListaMonedaItems();
    }

    public int getIdMoneda() {
        return mantenimientoBeanModel.getIdMoneda();
    }

    public void setIdMoneda(int idMoneda) {
        mantenimientoBeanModel.setIdMoneda(idMoneda);
    }

    public DataModel getListaMantenimientoPreventivo() {
        return mantenimientoBeanModel.getMantenimientoPreventivoDataModel();
    }

    public DataModel getListaMantenimientoCorrectivo() {
        return mantenimientoBeanModel.getMantenimientoCorrectivoDataModel();
    }

    /**
     * ***************** popups *********************
     */
    public boolean getMrPopupEntrada() {
        return mantenimientoBeanModel.isMrPopupEntrada();
    }

    public boolean getMrPopupSalida() {
        return mantenimientoBeanModel.isMrPopupSalida();
    }

    public boolean getMrPopupModificarEntrada() {
        return mantenimientoBeanModel.isMrPopupModificarEntrada();
    }

    public boolean getMrPopupDetalle() {
        return mantenimientoBeanModel.isMrPopupExternoDetalle();
    }

    public void mostrarPopupDetalle(ActionEvent event) {
        //mantenimientoBeanModel.setSgMantenimiento((SgVehiculoMantenimiento) mantenimientoBeanModel.getMantenimientoDataModel().getRowData());
        this.setIdMantenimientoSeleccionado(tomarParametro());
        mantenimientoBeanModel.setSgMantenimiento(mantenimientoBeanModel.findSgVehiculoMantenimiento());
        mantenimientoBeanModel.setMrPopupExternoDetalle(true);
    }

    public void ocultarPopupDetalle(ActionEvent event) {
        mantenimientoBeanModel.setMrPopupExternoDetalle(false);
    }

    public void mostrarPopupEntrada(ActionEvent event) {
        mantenimientoBeanModel.traerVehiculoMantenimientoNoTerminado();
        if (mantenimientoBeanModel.getOperacionTerminarRegistro().equals("FALSE")) {
            LOGGER.info(this, "no existen datos");
            nuevoMantenimiento();
            mantenimientoBeanModel.setMrPopupModificarEntrada(false);
            mantenimientoBeanModel.setMrPopupEntrada(true);
        } else //LOGGER.info(this, "Importe  "+mantenimientoBeanModel.getSgMantenimiento().getImporte().intValue());
        if (mantenimientoBeanModel.getSgMantenimiento().getImporte().intValue() <= 0) {
            FacesUtils.addErrorMessage("Tiene que recibir el vehiculo para poder registrar otro mantenimiento..");
            mantenimientoBeanModel.traerListaMantenimientos();
        } else if (mantenimientoBeanModel.getSgMantenimiento().getSiAdjunto() == null) {
            FacesUtils.addErrorMessage("Para registrar otro mantenimiento es necesario adjuntar el comprobante al registro actual..");
            mantenimientoBeanModel.traerListaMantenimientos();
        } else {
            nuevoMantenimiento();
            mantenimientoBeanModel.setMrPopupModificarEntrada(false);
            mantenimientoBeanModel.setMrPopupEntrada(true);
        }
    }

    public void ocultarPopupEntradaMantenimiento(ActionEvent event) {
        mantenimientoBeanModel.setMrPopupModificarEntrada(false);
        mantenimientoBeanModel.setMrPopupEntrada(false);
    }

    public void mostrarPopupSalidaMantenimiento(ActionEvent event) {
        LOGGER.info(this, "salida");
        //mantenimientoBeanModel.setSgMantenimiento((SgVehiculoMantenimiento) mantenimientoBeanModel.getMantenimientoDataModel().getRowData());
        this.setIdMantenimientoSeleccionado(tomarParametro());
        mantenimientoBeanModel.setSgMantenimiento(mantenimientoBeanModel.findSgVehiculoMantenimiento());
        //Limpiar los componentes
        mantenimientoBeanModel.traerMoneda();
        if (mantenimientoBeanModel.getSgMantenimiento().getMoneda() != null) {
            //es modificacion
            mantenimientoBeanModel.setIdMoneda(mantenimientoBeanModel.getSgMantenimiento().getMoneda().getId());
            LOGGER.info(this, "es modificacion ");
        } else {
            LOGGER.info(this, "es Insercion ");
            mantenimientoBeanModel.setIdMoneda(-1);
            mantenimientoBeanModel.getSgMantenimiento().setFechaSalida(new Date());
        }
        mantenimientoBeanModel.setMrPopupSalida(true);
    }

    public void terminarMatenimiento(ActionEvent event) {
        //mantenimientoBeanModel.setSgMantenimiento((SgVehiculoMantenimiento) mantenimientoBeanModel.getMantenimientoDataModel().getRowData());
        this.setIdMantenimientoSeleccionado(tomarParametro());
        mantenimientoBeanModel.setSgMantenimiento(mantenimientoBeanModel.findSgVehiculoMantenimiento());
        mantenimientoBeanModel.terminarMantenimiento();
    }

    public void ocultarPopupSalida(ActionEvent event) {
        mantenimientoBeanModel.setMrPopupSalida(false);
        this.mantenimientoBeanModel.traerListaMantenimientos();

        clearComponent("frmMantenimientoPopup", "fsalida");
        clearComponent("frmMantenimientoPopup", "sImport");
        clearComponent("frmMantenimientoPopup", "sKilomentrajeProx");
        clearComponent("frmMantenimientoPopup", "fProxMntto");
    }

    public void mostrarPopupModificarEntrada(ActionEvent event) {
        if (mantenimientoBeanModel.validarEntradaMantenimiento()) {
            //mantenimientoBeanModel.setSgMantenimiento((SgVehiculoMantenimiento) mantenimientoBeanModel.getMantenimientoDataModel().getRowData());
            this.setIdMantenimientoSeleccionado(tomarParametro());
            mantenimientoBeanModel.traerListaTalleresItems();
            mantenimientoBeanModel.setSgMantenimiento(mantenimientoBeanModel.findSgVehiculoMantenimiento());
            mantenimientoBeanModel.traerMantenimientoTipoEspecificoItems();
            mantenimientoBeanModel.setObservaciones(mantenimientoBeanModel.getSgMantenimiento().getObservacion());
            mantenimientoBeanModel.setFechaIngreso(mantenimientoBeanModel.getSgMantenimiento().getFechaIngreso());
            mantenimientoBeanModel.setIdProveedorSeleecionado(mantenimientoBeanModel.getSgMantenimiento().getProveedor().getId());
            mantenimientoBeanModel.setIdTipoMantenimientoEspecifico(mantenimientoBeanModel.getSgEstadoVehiculoOld().getSgTipoEspecifico().getId());
            mantenimientoBeanModel.setKilometrajeActual(mantenimientoBeanModel.getSgMantenimiento().getSgKilometraje().getKilometraje());
            mantenimientoBeanModel.setMrPopupModificarEntrada(true);
            mantenimientoBeanModel.setMrPopupEntrada(true);
        } else {
            FacesUtils.addErrorMessage("El vehiculo ya se encuentra fuera de mantenimiento..");
            mantenimientoBeanModel.traerListaMantenimientos();
        }
    }

    public void ocultarPopupModificarEntrada(ActionEvent event) {
        mantenimientoBeanModel.setMrPopupEntrada(false);
        mantenimientoBeanModel.setMrPopupModificarEntrada(false);
    }

    public void mostrarPopupSubirArchivo(ActionEvent event) {
//        if (mantenimientoBeanModel.validarEntradaMantenimiento()) {
        //mantenimientoBeanModel.setSgMantenimiento((SgVehiculoMantenimiento) mantenimientoBeanModel.getMantenimientoDataModel().getRowData());

        this.setIdMantenimientoSeleccionado(tomarParametro());
        mantenimientoBeanModel.setSgMantenimiento(mantenimientoBeanModel.findSgVehiculoMantenimiento());
        mantenimientoBeanModel.traerDirMantenimiento();
        mantenimientoBeanModel.setArchivoMantenimineto(true);
        mantenimientoBeanModel.setMrSubirArchivo(true);
//        } else {
//            FacesUtils.addErrorMessage("El vehiculo ya se encuentra fuera de mantenimiento..");
//            mantenimientoBeanModel.traerListaMantenimientos();
//        }
    }

    public void ocultarPopupSubirArchivo(ActionEvent event) {
        mantenimientoBeanModel.setArchivoMantenimineto(false);
        mantenimientoBeanModel.setMrSubirArchivo(false);
    }

    /**
     * @return the archivoMantenimineto
     */
    public boolean isArchivoMantenimineto() {
        return mantenimientoBeanModel.isArchivoMantenimineto();
    }

    /**
     * @param archivoMantenimineto the archivoMantenimineto to set
     */
    public void setArchivoMantenimineto(boolean archivoMantenimineto) {
        mantenimientoBeanModel.setArchivoMantenimineto(archivoMantenimineto);
    }

    /**
     * @return the idMantenimientoSeleccionado
     */
    public int getIdMantenimientoSeleccionado() {
        return mantenimientoBeanModel.getIdMantenimientoSeleccionado();
    }

    /**
     * @param idMantenimientoSeleccionado the idMantenimientoSeleccionado to set
     */
    public void setIdMantenimientoSeleccionado(int idMantenimientoSeleccionado) {
        mantenimientoBeanModel.setIdMantenimientoSeleccionado(idMantenimientoSeleccionado);
    }

    /**
     * @return the tituloPopup
     */
    public String getTituloPopup() {
        return mantenimientoBeanModel.getTituloPopup();
    }

    /**
     * @param tituloPopup the tituloPopup to set
     */
    public void setTituloPopup(String tituloPopup) {
        mantenimientoBeanModel.setTituloPopup(tituloPopup);
    }

    /**
     * @param mantenimientoBeanModel the mantenimientoBeanModel to set
     */
    public void setMantenimientoBeanModel(MantenimientoBeanModel mantenimientoBeanModel) {
        this.mantenimientoBeanModel = mantenimientoBeanModel;
    }

}
