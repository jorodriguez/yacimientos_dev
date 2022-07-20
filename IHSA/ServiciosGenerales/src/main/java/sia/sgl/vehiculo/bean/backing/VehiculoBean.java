/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.vehiculo.bean.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.persistence.NonUniqueResultException;
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
import sia.modelo.SgAsignarVehiculo;
import sia.modelo.SgChecklistExtVehiculo;
import sia.modelo.SgChecklistLlantas;
import sia.modelo.SgColor;
import sia.modelo.SgKilometraje;
import sia.modelo.SgMarca;
import sia.modelo.SgModelo;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioVehiculo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgVehiculoChecklist;
import sia.modelo.Usuario;
import sia.modelo.sgl.vehiculo.vo.SgKilometrajeVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sgl.vo.Vo;
import sia.sgl.mantenimiento.bean.model.MantenimientoBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.vehiculo.bean.model.VehiculoBeanModel;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author b75ckd35th
 *
 */
@Named(value = "vehiculoBean_old")
@RequestScoped
public class VehiculoBean implements Serializable {

    public static final long serialVersionUID = 1L;

    @ManagedProperty(value = "#{vehiculoBeanModel}")
    private VehiculoBeanModel vehiculoBeanModel;
    @ManagedProperty(value = "#{mantenimientoBeanModel}")
    private MantenimientoBeanModel mantenimientoBeanModel;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Getter
    @Setter
    private UploadedFile fileInfo;
    private final static UtilLog4j LOGGER = UtilLog4j.log;

    public VehiculoBean() {
    }

    public String goToCatalogoVehiculo() {
        try {
            vehiculoBeanModel.traerVehiculoPorOficina();
            vehiculoBeanModel.setVehiculo(new VehiculoVO());
        } catch (Exception ex) {
            Logger.getLogger(VehiculoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "/vistas/sgl/vehiculo/list";
    }

    public List<SelectItem> getSgTipoEspecificoBySgTipoSelectItem() {
        try {
            return vehiculoBeanModel.getSgTipoEspecificoBySgTipoSelectItem();
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepción dentro de traer tipo especifico");
            return null;
        }
    }

    public void regresarCatalogoVehiculo(ActionEvent event) throws SIAException {
        try {
            vehiculoBeanModel.traerVehiculoPorOficina();
            LOGGER.info(this, " Dentro de regresar al catalogo ");
        } catch (Exception ex) {
            Logger.getLogger(VehiculoBean.class.getName()).log(Level.SEVERE, null, ex);
            LOGGER.fatal(this, "Ocurrio un error al regresar");
        }
    }

    public String regresarAdministrarVehiculo() {
        vehiculoBeanModel.traerAsignacionVehiculo();
        return "administrarVehiculo";
    }

    public String administrarVehiculo(VehiculoVO vVo) {
        vehiculoBeanModel.setChecklist(null);
        vehiculoBeanModel.setChecklistExtVehiculo(null);
        vehiculoBeanModel.setChecklistLlantas(null);
        vehiculoBeanModel.setSgKilometraje(null);
        vehiculoBeanModel.setSgVehiculoChecklist(null);
        vehiculoBeanModel.setVehiculoChecklist(null);
        vehiculoBeanModel.setSgChecklistExtVehiculo(null);
        vehiculoBeanModel.setSgChecklistLlantas(null);
        vehiculoBeanModel.setFlag(false);
        vehiculoBeanModel.setChecklistVODataModel(null);
        setIdMoneda(0);
        if (vehiculoBeanModel.getVehiculo() != null) {
            vehiculoBeanModel.traerTaller();
            mantenimientoBeanModel.iniciarMantenimiento(vehiculoBeanModel.getVehiculo());
            vehiculoBeanModel.setUsuario(null);
            mantenimientoBeanModel.setSgVehiculoSeleccionado(vehiculoBeanModel.getVehiculo());
            mantenimientoBeanModel.traerKilometrajeActualOld();
            mantenimientoBeanModel.traerEstadoVehiculoActual();
            mantenimientoBeanModel.controlarPop("popupCaracteristicasVehiculo", false);
            mantenimientoBeanModel.controlarPop("popupDetalleChecklistVehiculo", false);
            mantenimientoBeanModel.traerVehiculoMantenimientoNoTerminado(); //<-- Saber si el vehicululo esta en mantenimiento

            return "administrarVehiculo.xhtml?faces-redirect=true";
        } else {
            FacesUtils.addInfoMessage("Paso algo, favor de contactar al equipo de desarrollo del SIA");
            return "";
        }
    }

    public String goToMantenimientoVehiculo() {
//        vehiculoBeanModel.iniciarConvesarion();
        LOGGER.info(this, "vehiculo " + vehiculoBeanModel.getVehiculo().getSerie());
        mantenimientoBeanModel.iniciarMantenimiento(vehiculoBeanModel.getVehiculo());
        return "/vistas/sgl/mantenimiento/mantenimientoVehiculo";
    }

    public String goToChecklistVehiculo() {
        vehiculoBeanModel.goToChecklistVehiculo();
        return "/vistas/sgl/vehiculo/checklist/createChecklistVehiculo";
    }

    public String goToCreate() {
        vehiculoBeanModel.traerCaracteristicas();
        if (vehiculoBeanModel.getCaracteristicas().isEmpty() || vehiculoBeanModel.getCaracteristicas().size() <= 10) {
            FacesUtils.addErrorMessage("Deben existir al menos 10 características de Vehículos antes de que puedas dar de alta un Vehículo");
            return "";
        } else {
            setVehiculo(new VehiculoVO());
            setIdTipoEspecifico(-1);
            setIdMarca(-1);
            setIdModelo(-1);
            setIdColor(-1);
            setPeriodicidadAvisoMantenimiento(7000);
            setKilometrajeInicial(0);
            this.vehiculoBeanModel.setCaracteristicas(null);
            return "/vistas/sgl/vehiculo/create";
        }
    }

    public void clearVariables(ActionEvent actionEvent) {
        vehiculoBeanModel.setChecklist(null);
        vehiculoBeanModel.setChecklistExtVehiculo(null);
        vehiculoBeanModel.setChecklistLlantas(null);
        vehiculoBeanModel.setSgKilometraje(null);
        vehiculoBeanModel.setSgVehiculoChecklist(null);
        vehiculoBeanModel.setVehiculoChecklist(null);
        vehiculoBeanModel.setSgChecklistExtVehiculo(null);
        vehiculoBeanModel.setSgChecklistLlantas(null);
        vehiculoBeanModel.setFlag(false);
        try {
            vehiculoBeanModel.traerChecklistPorVehiculo();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void clearVariablesCreateVehiculo(ActionEvent actionEvent) {
        setVehiculo(null);
        setIdTipoEspecifico(-1);
        setIdMarca(-1);
        setIdModelo(-1);
        setIdColor(-1);
        setKilometrajeInicial(0);
        setPeriodicidadAvisoMantenimiento(500);
        this.vehiculoBeanModel.setCaracteristicas(null);
    }

    public void seleccionarTaller(ValueChangeEvent e) {
        if (e.getNewValue() != null) {
            vehiculoBeanModel.getVehiculo().setIdProveedor((Integer) e.getNewValue());
            vehiculoBeanModel.seleccionarTaller();
            PrimeFaces.current().executeScript(";mostrarDiv('divAsignado');ocultarDiv('divTaller');");
        }

    }

    public void seleccionarEstado(ValueChangeEvent e) {
        if (e.getNewValue() != null) {
            vehiculoBeanModel.getVehiculo().setIdEstado((Integer) e.getNewValue());
            vehiculoBeanModel.seleccionarEstado();
            PrimeFaces.current().executeScript(";mostrarDiv('divEstadoActual');ocultarDiv('divEstadoNuevo');");
        }
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
            LOGGER.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    public void textChangeListener(String textChangeEvent) {
        LOGGER.info(this, "textChangeEvent");
        this.vehiculoBeanModel.setPrefijo(textChangeEvent);
        LOGGER.info(this, "prefijo: " + textChangeEvent);

        if (textChangeEvent == null || textChangeEvent.trim().isEmpty()) {
            LOGGER.info(this, "text == null o length==0");
            vehiculoBeanModel.getMatchesList().clear();
        } else {
            updateList(textChangeEvent);
        }

//            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
        this.vehiculoBeanModel.setPrefijo(textChangeEvent);

    }

    public void updateList(String textChangeEvent) {
        vehiculoBeanModel.getMatchesList().clear();
        for (SelectItem si : vehiculoBeanModel.getCaracteristicas()) {
            if ((((String) si.getValue()).toLowerCase()).startsWith(textChangeEvent.toLowerCase())) {
                vehiculoBeanModel.getMatchesList().add(si);
            }
        }
        LOGGER.info(this, "Matcheslist: " + vehiculoBeanModel.getMatchesList().size());
    }

    public void getCaracteristicasVehiculoDataModel() {
        try {
            vehiculoBeanModel.allCaracteristicasVehiculo();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.info(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void addCaracteristicaVehiculo(ActionEvent actionEvent) {
        try {
            if (!this.vehiculoBeanModel.getPrefijo().isEmpty()) {
                vehiculoBeanModel.addCaracteristica();
                updateList(vehiculoBeanModel.getPrefijo());
                FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + vehiculoBeanModel.getPrefijo());
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicas", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage("msgCaracteristicas", siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica al Vehículo");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void removeCaracteristicaVehiculo(ActionEvent actionEvent) {
        try {
            vehiculoBeanModel.setCaracteristicaVo((CaracteristicaVo) vehiculoBeanModel.getMapaDatos().get("caracteristica").getRowData());
            vehiculoBeanModel.removeCaracteristica();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void openPopupCaracteristicasVehiculo(ActionEvent actionEvent) {
        try {
            //Cargar todas las Características
            vehiculoBeanModel.traerCaracteristicas();
            //
            vehiculoBeanModel.allCaracteristicasVehiculo();
            vehiculoBeanModel.controlarPop("popupCaracteristicasVehiculo", Boolean.TRUE);
        } catch (Exception ex) {
            Logger.getLogger(VehiculoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closePopupCaracteristicasVehiculo(ActionEvent actionEvent) {
        //Quitándole memorias
        vehiculoBeanModel.setPrefijo("");
        vehiculoBeanModel.setCaracteristicas(null);
        vehiculoBeanModel.setMatchesList(null);
        vehiculoBeanModel.controlarPop("popupCaracteristicasVehiculo", Boolean.FALSE);
    }

    public void traerColorSelectItem() {
        vehiculoBeanModel.traerColorsItems();
    }

    public List<SelectItem> getColorSelectItem() {
        return vehiculoBeanModel.getColorListItem();
    }

    public List<SelectItem> getMarcasByTipoSelectItem() {
        List<Vo> marcasList = null;
        List<SelectItem> marcasListItem = null;

        try {
            marcasList = vehiculoBeanModel.getMarcasByTipo();
            marcasListItem = new ArrayList<SelectItem>();
            for (Vo marca : marcasList) {
                SelectItem item = new SelectItem(marca.getId(), marca.getNombre());
                marcasListItem.add(item);
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            return marcasListItem;
        }
    }

    public List<SelectItem> getModelosByTipoSelectItem() {
        List<SgModelo> modelosList = null;
        List<SelectItem> modelosListItem = null;

        try {
            modelosList = vehiculoBeanModel.getModelosByTipo();
            modelosListItem = new ArrayList<SelectItem>();
            for (SgModelo modelo : modelosList) {
                SelectItem item = new SelectItem(modelo.getId(), modelo.getNombre());
                modelosListItem.add(item);
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            return modelosListItem;
        }
    }

    /**
     * ****************************************************
     */
    public void limpiarComponente(String nombreFormulario, String nombreComponente) {
        LOGGER.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
            LOGGER.info(this, "limpio el componente");
        } catch (Exception e) {
            LOGGER.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    public List<SelectItem> getListaTipoEspecifico() {
        try {
            return vehiculoBeanModel.listaTipoEspecifico();
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepción dentro de traer tipo especifico");
            return null;
        }
    }

    /**
     * ****************************************************
     * @param valueChangeEvent
     */
    public void loadModelosInComboModelos(ValueChangeEvent valueChangeEvent) {
        try {
            vehiculoBeanModel.getVehiculo().setIdTipoEspecifico((Integer) valueChangeEvent.getNewValue());
            vehiculoBeanModel.getModelosByTipoEspecifico();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            LOGGER.fatal(this, e.getMessage());
        }
    }

    public void loadModelosInComboModelosFromComboMarcas(ValueChangeEvent valueChangeEvent) {
        try {
            vehiculoBeanModel.getVehiculo().setIdMarca((Integer) valueChangeEvent.getNewValue());
            vehiculoBeanModel.getModelosByTipoEspecifico();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            LOGGER.fatal(this, e.getMessage());
        }
    }

    public List<SelectItem> getModelosSelectItem() {
        List<Vo> modelosList = vehiculoBeanModel.getModelos();
        List<SelectItem> modelosListItem = null;

        if (modelosList != null) {
            modelosListItem = new ArrayList<SelectItem>();
            for (Vo modelo : modelosList) {
                SelectItem item = new SelectItem(modelo.getId(), modelo.getNombre());
                modelosListItem.add(item);
            }
        }
        return modelosListItem;
    }

    public void reactivateVehiculo(ActionEvent actionEvent) {
        LOGGER.info(this, "VehiculoBean.reactivateVehiculo()");
        try {
            vehiculoBeanModel.reactivateVehiculo();
            closePopupReactivateVehiculo(actionEvent);
            FacesUtils.addInfoMessage("El Vehículo fue reactivado exitosamente");
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
            siae.printStackTrace();
            closePopupReactivateVehiculo(actionEvent);
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            closePopupReactivateVehiculo(actionEvent);
        }
    }

    public String saveVehiculo() {
        int errors = 0;

        //Validaciones
        if (vehiculoBeanModel.getVehiculo().getIdTipoEspecifico() < 1) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:tipoVehiculo", FacesUtils.getKeyResourceBundle("sgl.vehiculo.tipo") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (vehiculoBeanModel.getVehiculo().getIdMarca() < 1) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:marca", FacesUtils.getKeyResourceBundle("sgl.vehiculo.marca") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (vehiculoBeanModel.getVehiculo().getIdModelo() < 1) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:modelo", FacesUtils.getKeyResourceBundle("sgl.vehiculo.modelo") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (vehiculoBeanModel.getVehiculo().getNumeroPlaca() == null || vehiculoBeanModel.getVehiculo().getNumeroPlaca().trim().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:numeroPlaca", FacesUtils.getKeyResourceBundle("sgl.vehiculo.numeroPlaca") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (vehiculoBeanModel.getVehiculo().getSerie() == null || vehiculoBeanModel.getVehiculo().getSerie().trim().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:numeroSerie", FacesUtils.getKeyResourceBundle("sgl.vehiculo.numeroSerie") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (vehiculoBeanModel.getVehiculo().getIdColor() < 1) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:sgColor", FacesUtils.getKeyResourceBundle("sgl.vehiculo.color") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (vehiculoBeanModel.getVehiculo().getCapacidadPasajeros() == null || vehiculoBeanModel.getVehiculo().getCapacidadPasajeros() < 1) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:capacidadPasajeros", FacesUtils.getKeyResourceBundle("sgl.vehiculo.capacidadPasajeros") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getKilometrajeInicial() < 0) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:kilometrajeInicial", FacesUtils.getKeyResourceBundle("sgl.vehiculo.kilometrajeInicial") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getPeriodicidadAvisoMantenimiento() < 1) {
            errors++;
            FacesUtils.addErrorMessage("formCreateVehiculo:periodicidadMntto", FacesUtils.getKeyResourceBundle("sgl.vehiculo.periodicidadMantenimiento") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }

        if (errors == 0) {
            try {
                if (vehiculoBeanModel.existNumeroSerie()) {
                    FacesUtils.addErrorMessage("Ya existe otro Vehículo con el número de Serie: " + vehiculoBeanModel.getVehiculo().getSerie());
                    return "";
                }
                if (vehiculoBeanModel.existNumeroSerieBaja()) { //Si existe un Vehículo con el mismo número de serie (existente o baja
                    FacesUtils.addErrorMessage("Ya existe un Vehículo dado de Baja con el número de Serie: " + vehiculoBeanModel.getVehiculo().getSerie());
                    FacesUtils.addErrorMessage("Puedes reactivarlo en el Menú de Vehículos, en la opción Vehículos de Baja");
//                    openPopupReactivateVehiculo();
                    return "";
                } else {
                    vehiculoBeanModel.saveVehiculo();
                    vehiculoBeanModel.setVehiculo(new VehiculoVO());
                    FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
                    return "list";
                }
            } catch (SIAException siae) {
                FacesUtils.addErrorMessage(siae.getMessage());
                LOGGER.fatal(this, siae.getMensajeParaProgramador());
                return "";
            } catch (Exception e) {
                LOGGER.info(this, e.getMessage());
                return "";
            }
        } else {
            return "";
        }
    }

    public void updateVehiculo(ActionEvent actionEvent) {
        try {
            int errors = 0;

            //Validaciones
            if (vehiculoBeanModel.getVehiculo().getIdTipoEspecifico() < 1) {
                errors++;
                FacesUtils.addErrorMessage("Tipo de Vehículo es requerido");
            }
            if (vehiculoBeanModel.getVehiculo().getIdMarca() < 1) {
                errors++;
                FacesUtils.addErrorMessage("Marca es requerido");
            }
            if (vehiculoBeanModel.getVehiculo().getIdModelo() < 1) {
                errors++;
                FacesUtils.addErrorMessage("Modelo es requerido");
            }
            if (vehiculoBeanModel.getVehiculo().getIdColor() < 1) {
                errors++;
                FacesUtils.addErrorMessage("Color es requerido");
            }

            if (vehiculoBeanModel.getVehiculo().getSerie().isEmpty()) {
                FacesUtils.addErrorMessage("Es necesario agregar el número de Serie: ");
            }

            if (vehiculoBeanModel.getVehiculo().getPeriodoKmMantenimiento() > 0) {
                errors++;
                FacesUtils.addErrorMessage("updateDatosVehiculo:periodicidadMntto", FacesUtils.getKeyResourceBundle("sgl.vehiculo.periodicidadMantenimiento") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
            }

            if (errors == 0) {
                vehiculoBeanModel.updateVehiculo();
                FacesUtils.addInfoMessage("El Vehículo fue actualizado exitosamente");
                PrimeFaces.current().executeScript(";$(dialogoModificarVehiculo).modal('hide');;");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
            closePopupUpdateVehiculo(actionEvent);
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrió un error al actualizar el Vehículo");
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            closePopupUpdateVehiculo(actionEvent);
        }
    }

    public void deleteVehiculo(ActionEvent actionEvent) {
        try {
            vehiculoBeanModel.deleteVehiculo();
            FacesUtils.addInfoMessage("El Vehículo fue eliminado exitosamente");
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrió un error al eliminar el Vehículo");
            LOGGER.info(this, e.getMessage());
            e.printStackTrace();
        } finally {
            closePopupDeleteVehiculo(actionEvent);
        }
    }

    public void bajaVehiculo(ActionEvent actionEvent) {
        try {
            vehiculoBeanModel.bajaVehiculo();
            FacesUtils.addInfoMessage("El Vehículo fue dado de baja exitosamente");
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrió un error al dar de baja el Vehículo");
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
        } finally {
            closePopupBajaVehiculo(actionEvent);
        }
    }

    public void saveMarca(ActionEvent actionEvent) {
        try {
            if (vehiculoBeanModel.getMarca().getNombre() != null && !vehiculoBeanModel.getMarca().getNombre().trim().isEmpty()) {
                vehiculoBeanModel.saveMarca();
                FacesUtils.addInfoMessage("msgCreateVehiculo", "La Marca fue creada exitosamente");
                closePopupCrearMarca(actionEvent);
            } else {
                FacesUtils.addErrorMessage("msgMarca", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
            closePopupCrearMarca(actionEvent);
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrió un error al crear la Marca");
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            closePopupCrearMarca(actionEvent);
        }
    }

    public void saveModelo(ActionEvent actionEvent) {
        int errors = 0;

        if (vehiculoBeanModel.getIdMarca() < 0) {
            errors++;
            FacesUtils.addErrorMessage("msgModelo", "Marca es requerido");
        }
        if (vehiculoBeanModel.getIdTipoEspecifico() < 0) {
            errors++;
            FacesUtils.addErrorMessage("msgModelo", "Tipo es requerido");
        }
        if (vehiculoBeanModel.getModelo().getNombre() == null && vehiculoBeanModel.getModelo().getNombre().trim().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("msgModelo", "Nombre es requerido");
        }

        if (errors == 0) {
            try {
                vehiculoBeanModel.saveModelo();
                FacesUtils.addInfoMessage("msgCreateVehiculo", "El Modelo fue creada exitosamente");
                closePopupSaveModelo(actionEvent);
            } catch (SIAException siae) {
                FacesUtils.addErrorMessage(siae.getMessage());
                LOGGER.fatal(this, siae.getMensajeParaProgramador());
                closePopupSaveModelo(actionEvent);
            } catch (Exception e) {
                FacesUtils.addErrorMessage("Ocurrió un error al crear el Modelo");
                LOGGER.fatal(this, e.getMessage());
                e.printStackTrace();
                closePopupSaveModelo(actionEvent);
            }
        }
    }

    public void saveTipoEspecificoVehiculo(ActionEvent actionEvent) {
        try {
            if (vehiculoBeanModel.getSgTipoEspecifico().getNombre() != null && !vehiculoBeanModel.getSgTipoEspecifico().getNombre().trim().isEmpty()) {
                vehiculoBeanModel.saveTipoEspecificoVehiculo();
                FacesUtils.addInfoMessage("msgCreateVehiculo", "El Tipo Específico fue creado exitosamente");
                closePopupSaveTipoEspecificoVehiculo(actionEvent);
            } else {
                FacesUtils.addErrorMessage("msgTipoEspecifico", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrió un error al crear el Tipo Específico");
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
        } finally {
            closePopupSaveTipoEspecificoVehiculo(actionEvent);
        }
    }

    public SgMarca getMarcaById() {
        return vehiculoBeanModel.getMarcaById();
    }

    public SgTipoEspecifico getTipoEspecificoById() {
        return vehiculoBeanModel.getTipoEspecificoById();
    }

//Fin de licencias
    public void openPopupUpdateVehiculo(ActionEvent actionEvent) {
        try {
            vehiculoBeanModel.getModelosByTipoEspecifico();
            PrimeFaces.current().executeScript(";$(dialogoModificarVehiculo).modal('show');;");
            //vehiculoBeanModel.controlarPop("popupUpdateVehiculo", !vehiculoBeanModel.estadoControlarPop("popupUpdateVehiculo"));
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
        }
    }

    public void closePopupUpdateVehiculo(ActionEvent actionEvent) {
        vehiculoBeanModel.setIdColor(-1);
        vehiculoBeanModel.setIdTipoEspecifico(-1);
        vehiculoBeanModel.setIdMarca(-1);
        vehiculoBeanModel.setIdModelo(-1);
        vehiculoBeanModel.setModelos(null);
        vehiculoBeanModel.reloadVehiculo();
        clearComponent("updateDatosVehiculo", "numeroPlaca");
        clearComponent("updateDatosVehiculo", "serie");
        clearComponent("updateDatosVehiculo", "color");
        clearComponent("updateDatosVehiculo", "cajonEstacionamiento");
        clearComponent("updateDatosVehiculo", "capacidadPasajeros");
        clearComponent("updateDatosVehiculo", "observacion");
        vehiculoBeanModel.controlarPop("popupUpdateVehiculo", !vehiculoBeanModel.estadoControlarPop("popupUpdateVehiculo"));
//        //Volver a cargar los datos de los vehículos
//        vehiculoBeanModel.reloadVehiculo();
    }

    public void openPopupDeleteVehiculo(ActionEvent actionEvent) {
        vehiculoBeanModel.setVehiculo((VehiculoVO) vehiculoBeanModel.getLista().getRowData());
        vehiculoBeanModel.controlarPop("popupDeleteVehiculo", !vehiculoBeanModel.estadoControlarPop("popupDeleteVehiculo"));
    }

    public void closePopupDeleteVehiculo(ActionEvent actionEvent) {
        vehiculoBeanModel.setVehiculo(null);
        vehiculoBeanModel.controlarPop("popupDeleteVehiculo", !vehiculoBeanModel.estadoControlarPop("popupDeleteVehiculo"));
    }

    public void openPopupBajaVehiculo(ActionEvent actionEvent) {
        vehiculoBeanModel.setVehiculo((VehiculoVO) vehiculoBeanModel.getLista().getRowData());
        vehiculoBeanModel.controlarPop("popupBajaVehiculo", !vehiculoBeanModel.estadoControlarPop("popupBajaVehiculo"));
    }

    public void closePopupBajaVehiculo(ActionEvent actionEvent) {
        vehiculoBeanModel.setVehiculo(null);
        vehiculoBeanModel.controlarPop("popupBajaVehiculo", !vehiculoBeanModel.estadoControlarPop("popupBajaVehiculo"));
    }

    public void openPopupCrearMarca(ActionEvent actionEvent) {
        vehiculoBeanModel.setMarca(new SgMarca());
        vehiculoBeanModel.controlarPop("popupCreateMarca", !vehiculoBeanModel.estadoControlarPop("popupCreateMarca"));
    }

    public void closePopupCrearMarca(ActionEvent actionEvent) {
        vehiculoBeanModel.setMarca(null);
        clearComponent("popupCreateMarca", "nombreMarca");
        vehiculoBeanModel.controlarPop("popupCreateMarca", !vehiculoBeanModel.estadoControlarPop("popupCreateMarca"));
    }

    public void openPopupSaveTipoEspecificoVehiculo(ActionEvent actionEvent) {
        vehiculoBeanModel.setSgTipoEspecifico(new SgTipoEspecifico());
        vehiculoBeanModel.controlarPop("popupCreateTipoEspecificoVehiculo", !vehiculoBeanModel.estadoControlarPop("popupCreateTipoEspecificoVehiculo"));
    }

    public void closePopupSaveTipoEspecificoVehiculo(ActionEvent actionEvent) {
        vehiculoBeanModel.setSgTipoEspecifico(null);
        clearComponent("popupCreateTipoEspecificoVehiculo", "nombreTipoEspecifico");
        clearComponent("popupCreateTipoEspecificoVehiculo", "descripcionTipoEspecifico");
        vehiculoBeanModel.controlarPop("popupCreateTipoEspecificoVehiculo", Boolean.FALSE);
    }

    public void openPopupSaveModelo(ActionEvent actionEvent) {
        int errors = 0;

        if (vehiculoBeanModel.getIdTipoEspecifico() < 1) {
            errors++;
            FacesUtils.addErrorMessage("Debe seleccionar un Tipo de Vehículo antes de intentar crear un Modelo");
        }
        if (vehiculoBeanModel.getIdMarca() < 1) {
            errors++;
            FacesUtils.addErrorMessage("Debe seleccionar una Marca antes de intentar crear un Modelo");
        }

        if (errors == 0) {
            vehiculoBeanModel.setModelo(new SgModelo());
            vehiculoBeanModel.controlarPop("popupCreateModelo", !vehiculoBeanModel.estadoControlarPop("popupCreateModelo"));
        }
    }

    public void closePopupSaveModelo(ActionEvent actionEvent) {
        vehiculoBeanModel.setModelo(null);
        clearComponent("popupCreateModelo", "nombreModelo");
        vehiculoBeanModel.controlarPop("popupCreateModelo", !vehiculoBeanModel.estadoControlarPop("popupCreateModelo"));
    }

    public void openPopupReactivateVehiculo(ActionEvent actionEvent) {
        //Dando memoria al Vehículo
        this.vehiculoBeanModel.setVehiculo((VehiculoVO) this.vehiculoBeanModel.getLista().getRowData());
        vehiculoBeanModel.controlarPop("popupActivarVehiculo", Boolean.TRUE);
    }

    public void closePopupReactivateVehiculo(ActionEvent actionEvent) {
        //Quitando memoria
        this.vehiculoBeanModel.setVehiculo(null);
        vehiculoBeanModel.controlarPop("popupActivarVehiculo", Boolean.FALSE);
    }

    //Asignar vehiculo
    public void paginaAsignarVehiculo(ActionEvent event) {
        usuarioJson(event);
        vehiculoBeanModel.setSgVehiculoChecklist(vehiculoBeanModel.buscarUltimoCheckList());
        vehiculoBeanModel.setSgAsignarVehiculo(new SgAsignarVehiculo());
        vehiculoBeanModel.getSgAsignarVehiculo().setFechaOperacion(new Date());
        vehiculoBeanModel.setPopUp(true);
    }

    public void usuarioJson(ActionEvent event) {
        String usuario = vehiculoBeanModel.usuariosJson();
        PrimeFaces.current().executeScript(";llenarJsonUsuario(" + usuario + ");");
    }    /////////

    public void recibirVehiculo(ActionEvent event) {
        vehiculoBeanModel.setSgAsignarVehiculo((SgAsignarVehiculo) vehiculoBeanModel.getMapaDatos().get("asignar").getRowData());
        //Verifica si el ultimo checklist
        vehiculoBeanModel.setSgVehiculoChecklist(vehiculoBeanModel.buscarUltimoCheckList());
        vehiculoBeanModel.getSgAsignarVehiculo().setFechaOperacion(new Date());
        vehiculoBeanModel.getSgAsignarVehiculo().setObservacion("");
        vehiculoBeanModel.setRecibirVehiculoPop(true);
    }

    //Pagos
    public String administrarPagos() {
        vehiculoBeanModel.setSgPagoServicioVehiculo((SgPagoServicioVehiculo) vehiculoBeanModel.getMapaDatos().get("pagos").getRowData());
        vehiculoBeanModel.setIdTipoEspecifico(vehiculoBeanModel.getSgPagoServicioVehiculo().getSgPagoServicio().getSgTipoEspecifico().getId());
        vehiculoBeanModel.traerPagoPorVehiculo();
        return "/vistas/sgl/pago/pagoVehiculo";
    }

    public void verDetallePago(ActionEvent event) {
        vehiculoBeanModel.setSgPagoServicioVehiculo((SgPagoServicioVehiculo) vehiculoBeanModel.getMapaDatos().get("pagos").getRowData());
        vehiculoBeanModel.setPagoPop(true);
    }

    public void cerrarPopPago(ActionEvent event) {
        vehiculoBeanModel.setSgPagoServicioVehiculo(null);
        vehiculoBeanModel.setPagoPop(false);
    }

    public void traerPagoVehiculo() {
        try {
            vehiculoBeanModel.traerPagoPorServicioVehiculo();
        } catch (Exception e) {
            LOGGER.fatal(this, "Exc Dentro de traer pago");
        }
    }

    public DataModel getTraerAsignacionVehiculo() {
        try {
            return vehiculoBeanModel.traerAsignacionVehiculo();
        } catch (Exception e) {
            LOGGER.fatal(this, "Exc Dentro asigna");
            return null;
        }
    }

    public String getDir() {
        if (vehiculoBeanModel.getSgPagoServicio() != null) {
            return vehiculoBeanModel.getDirectorio();
        } else {
            return "";
        }
    }

    public void guardarPagoServicioVehiculo(ActionEvent event) {
        LOGGER.info(this, "guardarPagoServicioVehiculo");
        if (vehiculoBeanModel.buscarProveedorPorNombre() == null) {
            FacesUtils.addInfoMessage("Es necesario un proveedor");
        } else if (vehiculoBeanModel.getSgPagoServicio().getFechaInicio() == null) {
            FacesUtils.addInfoMessage("Elija una fecha de inicio");
        } else if (vehiculoBeanModel.getSgPagoServicio().getFechaFin() == null) {
            FacesUtils.addInfoMessage("Elija una fecha fin");
        } else if (vehiculoBeanModel.getSgPagoServicio().getFechaVencimiento() == null) {
            FacesUtils.addInfoMessage("Elija una fecha de vencimiento");
        } else if (vehiculoBeanModel.getIdMoneda() < 1) {
            FacesUtils.addInfoMessage("Elija una moneda");
        } else if (vehiculoBeanModel.getSgPagoServicio().getImporte() == null) {
            FacesUtils.addInfoMessage("Es necesario agregar un importe");
        } else {
            if (vehiculoBeanModel.guardarPagoServicioVehiculo()) {
                vehiculoBeanModel.setPopUp(false);
                vehiculoBeanModel.setCrearPop(false);
                vehiculoBeanModel.setModificarPop(false);
                vehiculoBeanModel.setSgPagoServicio(null);
                vehiculoBeanModel.getLista();
                vehiculoBeanModel.setPro(null);
                vehiculoBeanModel.setIdMoneda(0);
            } else {
                FacesUtils.addInfoMessage("Ocurrio una excepción favor de notificar al equipo del SIA al correo soportesia@ihsa.mx");
            }
        }
    }

    public List<SelectItem> getTraerMondeda() {
        return vehiculoBeanModel.traerMoneda();
    }

    public void cerraPopPagoServicio(ActionEvent event) {
        vehiculoBeanModel.setSgPagoServicio(null);
        vehiculoBeanModel.setPopUp(false);
        vehiculoBeanModel.setModificarPop(false);
        vehiculoBeanModel.getLista();
    }

    public void modificarPagoServicio(ActionEvent event) {
        if (vehiculoBeanModel.getSgPagoServicio().getFechaInicio() == null) {
            FacesUtils.addInfoMessage("Elija una fecha de inicio");
        } else if (vehiculoBeanModel.getSgPagoServicio().getFechaFin() == null) {
            FacesUtils.addInfoMessage("Elija una fecha fin");
        } else if (vehiculoBeanModel.getSgPagoServicio().getFechaVencimiento() == null) {
            FacesUtils.addInfoMessage("Elija una fecha de vencimiento");
        } else if (vehiculoBeanModel.getIdMoneda() < 1) {
            FacesUtils.addInfoMessage("Elija una moneda");
        } else if (vehiculoBeanModel.getSgPagoServicio().getImporte() == null) {
            FacesUtils.addInfoMessage("Es necesario agregar un importe");
        } else {
            vehiculoBeanModel.modificarPagoServicio();
            vehiculoBeanModel.setModificarPop(false);
            vehiculoBeanModel.setSgPagoServicioVehiculo(null);
            vehiculoBeanModel.setSgPagoServicio(null);
        }
    }

    public void cerraPopModificarPagoServicio(ActionEvent event) {
        vehiculoBeanModel.setModificarPop(false);
        vehiculoBeanModel.setSgPagoServicioVehiculo(null);
        vehiculoBeanModel.setSgPagoServicio(null);
        clearComponent("formModificarPago", "fInicio");
        clearComponent("formModificarPago", "fFin");
        clearComponent("formModificarPago", "fVencimiento");

        vehiculoBeanModel.traerPagoPorVehiculo();
    }

    //FIN DE ASIGNAR VEHICULO
    public void limpiarAsigna(ActionEvent event) {
        vehiculoBeanModel.setUsuario(null);
        vehiculoBeanModel.setUser("");
//        vehiculoBeanModel.setIdChecklist(-1);
        vehiculoBeanModel.setSgAsignarVehiculo(null);
        vehiculoBeanModel.setLista(null);
        try {
            vehiculoBeanModel.traerVehiculoPorOficina();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrió un error al consultar los Vehículos");
            LOGGER.fatal(this, e.getMessage());
        }
    }

    public void buscarLicencia(ActionEvent event) {
        if (vehiculoBeanModel.buscarEmpledoPorNombre() != null) {
            if (vehiculoBeanModel.buscarLicenciaVigente()) {
                //Evita que se asignen dos vehiculos al usuario
                vehiculoBeanModel.setSgVehiculoChecklist(vehiculoBeanModel.buscarUltimoCheckList());
                vehiculoBeanModel.setSgAsignarVehiculo(new SgAsignarVehiculo());
                vehiculoBeanModel.getSgAsignarVehiculo().setFechaOperacion(new Date());
                vehiculoBeanModel.setPopUp(true);
            } else {
                FacesUtils.addErrorMessage("No es posible asignar el vehículo al usuario debido a que no tiene licencia vigente");
            }
//            vehiculoBeanModel.traerVehiculoAsignado();
        } else {
            LOGGER.info(this, "#No existe usuario para buscar#");
            vehiculoBeanModel.setUsuario(null);
            vehiculoBeanModel.setAsignacionSinTerminar(false);
        }
    }

    public DataModel getTraerVehiculoAsignado() {
        try {
            LOGGER.info(this, "Dentro de traer vehiculo asignado");
            return vehiculoBeanModel.traerAsignacionVehiculo();
        } catch (Exception e) {
            LOGGER.fatal(this, "Exc Dentro de traer vehiculo asignado");
            return null;
        }
    }

    public void seleccionarVehiculo(ActionEvent event) {
        vehiculoBeanModel.setVehiculo((VehiculoVO) vehiculoBeanModel.getMapaDatos().get("asignar").getRowData());
    }

    public void completarAsignacionVehiculo(ActionEvent event) throws Exception {
        if (vehiculoBeanModel.buscarLicenciaVigente()) {
            if (vehiculoBeanModel.getSgAsignarVehiculo().getFechaOperacion() != null) {
                vehiculoBeanModel.completarAsignacionVehiculo(); //Vehiculo no disponible
                vehiculoBeanModel.setUser("");
                vehiculoBeanModel.setUsuario(null);
                vehiculoBeanModel.setAsignacionSinTerminar(false);
                vehiculoBeanModel.setPopUp(false);
                vehiculoBeanModel.setSgAsignarVehiculo(null);
                vehiculoBeanModel.traerAsignacionVehiculo();
            } else {
                FacesUtils.addInfoMessage("Es necesario selecionar una fecha");
            }
        } else {
            FacesUtils.addErrorMessage("No es posible asignar el vehículo al usuario debido a que no tiene licencia vigente");
        }
    }

    public void cancelarAsignacion(ActionEvent event) {
        vehiculoBeanModel.setUser("");
        vehiculoBeanModel.setUsuario(null);
        vehiculoBeanModel.setAsignacionSinTerminar(false);
        vehiculoBeanModel.setPopUp(false);
        vehiculoBeanModel.setSgAsignarVehiculo(null);
    }

    public void seleccionarAsignacion(ActionEvent event) {
        vehiculoBeanModel.setSgAsignarVehiculo((SgAsignarVehiculo) vehiculoBeanModel.getMapaDatos().get("asignar").getRowData());
        vehiculoBeanModel.setModificarPop(true);
    }

    public void completarModificacionVehiculo(ActionEvent event) {
        if (vehiculoBeanModel.completarModificacionVehiculo()) {
//////            vehiculoBeanModel.traerVehiculoAsignado();
            vehiculoBeanModel.setSgAsignarVehiculo(null);
            vehiculoBeanModel.setModificarPop(false);
        } else {
            FacesUtils.addInfoMessage("No fue posible realizar la modificación");
        }
    }

    public void eliminarAsignacion(ActionEvent event) {
        vehiculoBeanModel.setSgAsignarVehiculo((SgAsignarVehiculo) vehiculoBeanModel.getMapaDatos().get("asignar").getRowData());
        if (vehiculoBeanModel.getSgAsignarVehiculo().getSiOperacion().getId() == 1) {
            vehiculoBeanModel.eliminarAsignarVehiculo();
        } else if (vehiculoBeanModel.getSgAsignarVehiculo().getSiOperacion().getId() == 2) {
            vehiculoBeanModel.eliminarRecepcionVehiculo();
        }
        vehiculoBeanModel.getMapaDatos().put("asignar", vehiculoBeanModel.traerAsignacionVehiculo());
        vehiculoBeanModel.setSgAsignarVehiculo(null);
    }

    public void subirCarta(ActionEvent event) {
        vehiculoBeanModel.setSgAsignarVehiculo((SgAsignarVehiculo) vehiculoBeanModel.getMapaDatos().get("asignar").getRowData());
        vehiculoBeanModel.setSubirArchivoPop(true);
        mantenimientoBeanModel.setMrSubirArchivo(true);
    }

    public void quitarCartaAsigna(ActionEvent event) {
        vehiculoBeanModel.setSgAsignarVehiculo((SgAsignarVehiculo) vehiculoBeanModel.getMapaDatos().get("asignar").getRowData());
        vehiculoBeanModel.quitarCartaAsigna();
        vehiculoBeanModel.setSgAsignarVehiculo(null);
    }

    public void subirCartaAsignacion(FileUploadEvent fileEvent) {
        fileInfo = fileEvent.getFile();
        boolean valid = false;
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
        try {
            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setRuta(getDirCartaAsigna());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                valid
                        = vehiculoBeanModel.guardarCartaAsignacion(
                                documentoAnexo.getNombreBase(),
                                documentoAnexo.getRuta(),
                                documentoAnexo.getTipoMime(),
                                documentoAnexo.getTamanio()
                        );
                vehiculoBeanModel.setSubirArchivoPop(false);
                mantenimientoBeanModel.setMrSubirArchivo(false);
                vehiculoBeanModel.setSgAsignarVehiculo(null);
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

//
//	    final Path file = Paths.get(fileInfo.getFile().getAbsolutePath());
//	    final Path savedFile = Files.write(file, Files.readAllBytes(file));
//
//	    if (fileInfo.getStatus().isSuccess() || Files.exists(savedFile)) {
//		valid = vehiculoBeanModel.guardarCartaAsignacion(
//			fileInfo.getFileName(),
//			fileInfo.getContentType(),
//			fileInfo.getSize());
//		vehiculoBeanModel.setSubirArchivoPop(false);
//		mantenimientoBeanModel.setMrSubirArchivo(false);
//		vehiculoBeanModel.setSgAsignarVehiculo(null);
//	    } else {
//		LOGGER.info(this, "No paso el is Saved");
//	    }
        if (!valid) {
            FacesUtils.addInfoMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
        }

    }

    public String getDirCartaAsigna() {
        LOGGER.info(this, "DEntro de dir");
        if (vehiculoBeanModel.getSgAsignarVehiculo() != null) {
            return vehiculoBeanModel.dirCartaAsigna();
        } else {
            return "";
        }
    }

    public void cerrarPopSubirArchivo(ActionEvent event) {
        mantenimientoBeanModel.setMrSubirArchivo(false);
        mantenimientoBeanModel.setArchivoMantenimineto(false);
        vehiculoBeanModel.setSgAsignarVehiculo(null);
        vehiculoBeanModel.setSubirArchivoPop(false);
        vehiculoBeanModel.setEliminarPop(false);
        vehiculoBeanModel.setModificarPop(false);
        vehiculoBeanModel.setUser("");
        vehiculoBeanModel.setIdTipoEspecifico(-1);
        vehiculoBeanModel.setCrearPop(false);
    }

    public void cerrarModificaAsigna(ActionEvent event) {
        vehiculoBeanModel.setSgAsignarVehiculo(null);
        vehiculoBeanModel.setModificarPop(false);

    }

    public void verDetalleAsignacion(ActionEvent event) {
        vehiculoBeanModel.setSgAsignarVehiculo((SgAsignarVehiculo) vehiculoBeanModel.getMapaDatos().get("asignar").getRowData());
        vehiculoBeanModel.setLista(null);
        //buscar los checklist
        vehiculoBeanModel.traerChecklistVehiculo();
        vehiculoBeanModel.setSgChecklistExtVehiculo(vehiculoBeanModel.buscarChecklistExterior());
        vehiculoBeanModel.setSgChecklistLlantas(vehiculoBeanModel.buscarChecklistLlantas());
        //Buscar asignacion
        if (vehiculoBeanModel.getSgAsignarVehiculo().getPertenece() > 0) {
            vehiculoBeanModel.setSgAsignarVehiculoRecibido(vehiculoBeanModel.getSgAsignarVehiculo());
        }
        vehiculoBeanModel.setVerDetallePop(true);
//        return "/vistas/sgl/vehiculo/detalleAsignaVehiculo";
    }

    public void cerrarVerDetallePop(ActionEvent event) {
        vehiculoBeanModel.setVerDetallePop(false);
        vehiculoBeanModel.setSgAsignarVehiculo(null);
        vehiculoBeanModel.setSgAsignarVehiculoRecibido(null);
    }

    public void completarRecibirVehiculo(ActionEvent event) {
        if (vehiculoBeanModel.getSgAsignarVehiculo().getFechaOperacion() == null) {
            FacesUtils.addInfoMessage("Es necesario seleccionar una fecha");
        } else if (vehiculoBeanModel.mofificarDespuesAsignar()) {
            vehiculoBeanModel.completarRecibirVehiculo();
            //Poner al vehiuculo disponible
            //vehiculoBeanModel.ponerVehiculoDisponible();
            vehiculoBeanModel.setRecibirVehiculoPop(false);
            vehiculoBeanModel.setSgAsignarVehiculo(null);
        } else {
            FacesUtils.addInfoMessage("Ocurrio un error al modificar el asignado");
        }
    }

    public void cancelarRecibirgVehiculo(ActionEvent event) {
        vehiculoBeanModel.setRecibirVehiculoPop(false);
        vehiculoBeanModel.setSgAsignarVehiculo(null);
    }

    //Auto completar proveedor
    public void proveedorListener(String textChangeEvent) {
//            vehiculoBeanModel.setListaProveedor(regresaProveedorActivo(cadenaDigitada));
        vehiculoBeanModel.setListaProveedor(vehiculoBeanModel.regresaProveedorActivo(textChangeEvent));
        LOGGER.info(this, "Lista: " + vehiculoBeanModel.getListaProveedor().size());
        vehiculoBeanModel.setPro(textChangeEvent);
        vehiculoBeanModel.setListaProveedor(null);
    }

    public List<SelectItem> getListaProveedor() {
        return vehiculoBeanModel.getListaProveedor();
    }

    public void setListaProveedor(List<SelectItem> listaProveedor) {
        vehiculoBeanModel.setListaProveedor(listaProveedor);
    }

    /**
     * *
     * Pago de vehiculo
     *
     */
    public String pagoServicio() {
        vehiculoBeanModel.setIdTipoEspecifico(-1);
        vehiculoBeanModel.setLista(null);
        return "/vistas/sgl/pago/pagoVehiculo";
    }

    public List<SelectItem> getTraerTipoEspVehiculo() {
        return vehiculoBeanModel.traerTipoEspecificoPorTipoVehiculo();
    }

    public void traerPagoVehiculo(ValueChangeEvent valueChangeEvent) {
        vehiculoBeanModel.setIdTipoEspecifico((Integer) valueChangeEvent.getNewValue());
        vehiculoBeanModel.traerPagoPorVehiculo();
    }

    public void agregarPagoServicioOficina(ActionEvent event) {
        vehiculoBeanModel.setPopUp(true);
        vehiculoBeanModel.setSgPagoServicio(new SgPagoServicio());
        vehiculoBeanModel.setCrearPop(true);
    }

    public void traerPorTipoEspecificoVehiculo() {
        try {
            vehiculoBeanModel.traerPagoPorServicioVehiculo();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    public void seleccionarPagoVehiculo(ActionEvent event) {
        LOGGER.info(this, "seleccionar Pago de vehiculo ");
        vehiculoBeanModel.setSgPagoServicioVehiculo((SgPagoServicioVehiculo) vehiculoBeanModel.getMapaDatos().get("pagoVehiculo").getRowData());
        vehiculoBeanModel.setSgPagoServicio(((SgPagoServicioVehiculo) vehiculoBeanModel.getMapaDatos().get("pagoVehiculo").getRowData()).getSgPagoServicio());
        //vehiculoBeanModel.setSgPagoServicio(vehiculoBeanModel.getSgPagoServicioVehiculo().getSgPagoServicio());
        vehiculoBeanModel.setIdMoneda(vehiculoBeanModel.getSgPagoServicio().getMoneda().getId());
        vehiculoBeanModel.setModificarPop(true);
    }

    public void completarEliminarPagoVehiculo(ActionEvent event) {
        vehiculoBeanModel.eliminarPagoServicioVehiculo();
        vehiculoBeanModel.setEliminarPop(false);
        vehiculoBeanModel.traerPagoPorVehiculo();
    }

    public void eliminarPagoServicioVehiculo(ActionEvent event) {
        vehiculoBeanModel.setSgPagoServicioVehiculo((SgPagoServicioVehiculo) vehiculoBeanModel.getMapaDatos().get("pagoVehiculo").getRowData());
        vehiculoBeanModel.setSgPagoServicio(vehiculoBeanModel.getSgPagoServicioVehiculo().getSgPagoServicio());
        vehiculoBeanModel.setEliminarPop(true);
    }

    public void subirComprobanteVehiculo(ActionEvent event) {
        vehiculoBeanModel.setSgPagoServicioVehiculo((SgPagoServicioVehiculo) vehiculoBeanModel.getMapaDatos().get("pagoVehiculo").getRowData());
        vehiculoBeanModel.setSgPagoServicio(vehiculoBeanModel.getSgPagoServicioVehiculo().getSgPagoServicio());
        vehiculoBeanModel.setSubirArchivoPop(true);

    }

    public void eliminarComprobanteVehiculo(ActionEvent event) {
        vehiculoBeanModel.setSgPagoServicioVehiculo((SgPagoServicioVehiculo) vehiculoBeanModel.getMapaDatos().get("pagoVehiculo").getRowData());
        vehiculoBeanModel.setSgPagoServicio(vehiculoBeanModel.getSgPagoServicioVehiculo().getSgPagoServicio());
        vehiculoBeanModel.eliminarComprobante();
        vehiculoBeanModel.setSgPagoServicio(null);
    }

    public void subirArchivoPago(FileUploadEvent fileEvent) {
        fileInfo = fileEvent.getFile();
        boolean valid = false;

        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

        if (addArchivo) {
            try {

                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta(vehiculoBeanModel.getDirectorio());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                try {
                    valid
                            = vehiculoBeanModel.guardarArchivoPago(
                                    documentoAnexo.getNombreBase(),
                                    documentoAnexo.getRuta(),
                                    documentoAnexo.getTipoMime(),
                                    documentoAnexo.getTamanio()
                            );
                } catch (Exception ex) {
                    Logger.getLogger(VehiculoBean.class.getName()).log(Level.SEVERE, null, ex);
                }

                vehiculoBeanModel.setSubirArchivoPop(false);
                vehiculoBeanModel.setSgPagoServicio(null);
                fileInfo.delete();
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (SIAException e) {
                LOGGER.error(e);
            }
            if (!valid) {
                FacesUtils.addInfoMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
            }
        } else {
            FacesUtils.addInfoMessage(new StringBuilder()
                    .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                    .append(validadorNombreArchivo.getCaracteresNoValidos())
                    .toString());
        }

    }

    public void cerrarPopEliminar(ActionEvent event) {
        vehiculoBeanModel.setEliminarPop(false);
    }

    /**
     * CATALOGO VEHICULO
     *
     * @return
     */
    public DataModel getTraerVehiculoPorOficina() {
        try {
            return vehiculoBeanModel.traerVehiculoPorOficina();
        } catch (Exception e) {
            return null;
        }
    }

    public void validaFechaPago(FacesContext context, UIComponent validate, Object value) {
        Date f = (Date) value;
        LOGGER.info(this, "Fecha V +" + f);
        if (f.compareTo(vehiculoBeanModel.getSgPagoServicio().getFechaInicio()) <= 0) {
            try {
                ((UIInput) validate).setValid(false);
                FacesMessage msg = new FacesMessage("Elija una fecha posterior a la de inicio del periodo");
                context.addMessage(validate.getClientId(context), msg);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    public DataModel getChecklistVehiculoVODataModelForDetalle() {
        return vehiculoBeanModel.getChecklistVODataModel();
    }

    public String openDetalleChecklistVehiculo() {
        vehiculoBeanModel.setVehiculoChecklist((SgVehiculoChecklist) vehiculoBeanModel.getMapaDatos().get("checklist").getRowData());
        vehiculoBeanModel.getItemsVOForVehiculoChecklist();
        vehiculoBeanModel.getChecklistExteriorAndLlantas();
        if (vehiculoBeanModel.getChecklistExtVehiculo() == null || vehiculoBeanModel.getChecklistLlantas() == null || vehiculoBeanModel.getChecklistLlantas().getId() == null) {
            FacesUtils.addErrorMessage("msgDetalleChecklistVehiculo", "Checklist Incompleto. Porfavor modíficalo para completarlo");
        }
        return "/vistas/sgl/vehiculo/checklist/detalleChecklistVehiculo";
//        sesion.getControladorPopups().put("popupDetalleChecklistVehiculo", Boolean.TRUE);
    }

    public String closeDetalleChecklistVehiculo() {
        vehiculoBeanModel.setChecklist(null);
        vehiculoBeanModel.setChecklistExtVehiculo(null);
        vehiculoBeanModel.setChecklistLlantas(null);
        vehiculoBeanModel.setSgKilometraje(null);
        vehiculoBeanModel.setSgVehiculoChecklist(null);
        vehiculoBeanModel.setVehiculoChecklist(null);
        vehiculoBeanModel.setSgChecklistExtVehiculo(null);
        vehiculoBeanModel.setSgChecklistLlantas(null);
        vehiculoBeanModel.setFlag(false);
        try {
            vehiculoBeanModel.traerChecklistPorVehiculo();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
        vehiculoBeanModel.setChecklistVODataModel(null);
//        sesion.getControladorPopups().put("popupDetalleChecklistVehiculo", Boolean.FALSE);
        return "/vistas/sgl/vehiculo/administrarVehiculo";
    }

    public void loadChecklistsVehiculo(ActionEvent actionEvent) {
        vehiculoBeanModel.setVehiculoChecklist((SgVehiculoChecklist) vehiculoBeanModel.getMapaDatos().get("checklist").getRowData());
        vehiculoBeanModel.setChecklist(vehiculoBeanModel.getVehiculoChecklist().getSgChecklist());
        vehiculoBeanModel.getChecklistExteriorAndLlantas();
        vehiculoBeanModel.controlarPop("popupObservacionToAdjunto", Boolean.FALSE);
        vehiculoBeanModel.controlarPop("popupUpdateObservacionToAdjunto", Boolean.FALSE);
    }

    public DataModel getChecklistVehiculoVODataModelForUpdate() {
        vehiculoBeanModel.getItemsVOForChecklistVehiculo();
        return vehiculoBeanModel.getChecklistVODataModel();
    }

    public void updateChecklistVehiculo(ActionEvent actionEvent) {
        LOGGER.info(this, "VehiculoBean.updateChecklistVehiculo");
        SgKilometraje kilometraje = this.vehiculoBeanModel.getVehiculoChecklist().getSgKilometraje();
        LOGGER.info(this, "Kilometraje: " + kilometraje.getKilometraje());
        if (kilometraje.getKilometraje() != null) {
            if (kilometraje.getKilometraje() >= 0) {
                if (vehiculoBeanModel.kilometrajeGreat()) {
                    if (!vehiculoBeanModel.updateChecklistVehiculo()) {
                        FacesUtils.addErrorMessage("Ourrió un error al actualizar el Checklist de Vehículo. Porfavor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
                    } else {
                        FacesUtils.addInfoMessage("Checklist de Vehículo Interior actualizado satisfactoriamente");
                    }
                } else {
                    FacesUtils.addErrorMessage("El Kilometraje actual debe ser mayor que el último registrado. Si no es así porfavor pide al Administrador de SGL que reinicie el Kilometraje");
                }
            } else {
                FacesUtils.addErrorMessage("Kilometraje debe ser un entero positivo");
            }
        } else {
            FacesUtils.addErrorMessage("Kilometraje es requerido");
        }
    }

    public String getDirectoryChecklistExteriorVehiculo() {
        return vehiculoBeanModel.getDirectoryChecklistExteriorVehiculo();
    }

    public void uploadFileChecklistExteriorVehiculo(FileUploadEvent fileEvent) {
        fileInfo = fileEvent.getFile();
        try {
            FacesUtils.addInfoMessage("Upload Successfull...");
            try {
                if (!vehiculoBeanModel.guardarArchivoChecklistExteriorVehiculo(fileInfo.getFileName(), fileInfo.getContentType(), fileInfo.getSize())) {
                    vehiculoBeanModel.eliminarArchivoFisicamente(Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/" + "Checklist/" + vehiculoBeanModel.getChecklist().getId() + "/" + fileInfo.getFileName());
                } else {
                    FacesUtils.addInfoMessage("El Checklist Externo fue actualizado satisfactoriamente");
                }
            } catch (SIAException siae) {
                FacesUtils.addErrorMessage(siae.getMessage());
                LOGGER.fatal(this, siae.getMensajeParaProgramador());

            } catch (Exception e) {
                LOGGER.fatal(this, e.getMessage());
                FacesUtils.addErrorMessage(new SIAException().getMessage());
                vehiculoBeanModel.eliminarArchivoFisicamente(Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/" + "Checklist/" + vehiculoBeanModel.getChecklist().getId() + "/" + fileInfo.getFileName());
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void uploadFileChecklistExteriorVehiculoNuevo(FileUploadEvent fileEvent) {
        fileInfo = fileEvent.getFile();
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();

        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

        try {

            if (addArchivo) {

                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setRuta(getDirectoryChecklistExteriorVehiculo());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                boolean valid
                        = vehiculoBeanModel.guardarArchivoChecklistExteriorVehiculoNuevo(
                                documentoAnexo.getNombreBase(),
                                documentoAnexo.getTipoMime(),
                                documentoAnexo.getTamanio()
                        );

                if (valid) {
                    FacesUtils.addInfoMessage("El Checklist de Exterior fue creado satisfactoriamente");
                } else {
                    FacesUtils.addErrorMessage("No fue posible agregar el archivo " + documentoAnexo.getNombreBase());
                }
            } else {
                FacesUtils.addInfoMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

        } catch (IOException e) {
            FacesUtils.addErrorMessage(e.getMessage());
            LOGGER.fatal(e);
        } catch (SIAException e) {
            FacesUtils.addErrorMessage(e.getMessage());
            LOGGER.fatal(e);
        } catch (Exception e) {
            FacesUtils.addErrorMessage(e.getMessage());
            LOGGER.fatal(e);
        }
    }

    public void deleteAdjuntoChecklistExterior(java.awt.event.ActionEvent actionEvent) {
        try {
            vehiculoBeanModel.deleteAdjuntoChecklistExterior();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.error(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.error(this, e);

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void deleteAdjuntoChecklistExteriorNuevo(java.awt.event.ActionEvent actionEvent) {
        try {
            vehiculoBeanModel.deleteAdjuntoChecklistExterior();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void updateChecklistLlantas(ActionEvent actionEvent) {
        try {
            if (!vehiculoBeanModel.getChecklistLlantas().getDelanteraIzquierda().isEmpty()
                    && !vehiculoBeanModel.getChecklistLlantas().getDelanteraDerecha().isEmpty()
                    && !vehiculoBeanModel.getChecklistLlantas().getTraseraIzquierda().isEmpty()
                    && !vehiculoBeanModel.getChecklistLlantas().getTraseraDerecha().isEmpty()) {
                vehiculoBeanModel.updateChecklistLlantasVehiculo();
                FacesUtils.addInfoMessage("El Checklist de Llantas fue actualizado satisfactoriamente");
            } else {
                FacesUtils.addErrorMessage("Todos los porcentajes de uso de las llantas son requeridos, excepto el de refacción");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void createChecklistLlantas(ActionEvent actionEvent) {
        try {
            if (!vehiculoBeanModel.getChecklistLlantas().getDelanteraIzquierda().isEmpty()
                    && !vehiculoBeanModel.getChecklistLlantas().getDelanteraDerecha().isEmpty()
                    && !vehiculoBeanModel.getChecklistLlantas().getTraseraIzquierda().isEmpty()
                    && !vehiculoBeanModel.getChecklistLlantas().getTraseraDerecha().isEmpty()) {
                vehiculoBeanModel.createChecklistLlantasVehiculo();
                //Borrar variables
//                vehiculoBeanModel.setFlag(false);
                FacesUtils.addInfoMessage("El Checklist de Llantas fue creado satisfactoriamente");
            } else {
                FacesUtils.addErrorMessage("Todos los porcentajes de uso de las llantas son requeridos, excepto el de refacción");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void updateAdjunto(ActionEvent actionEvent) {
        try {
            vehiculoBeanModel.updateAdjunto();
            FacesUtils.addInfoMessage("La Observación fue agregada satisfactoriamente");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            closePopupAddObservacionToChecklistExterior(actionEvent);
            closePopupUpdateObservacionToChecklistExterior(actionEvent);
        }
    }

    public void restartKilometrajeVehiculo(ActionEvent actionEvent) {
        try {
            vehiculoBeanModel.restartKilometrajeVehiculo();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            mantenimientoBeanModel.traerKilometrajeActualOld();
            mantenimientoBeanModel.traerListaMantenimientos();
            closePopupRestartKilometraje(actionEvent);
        }
    }

    public void modifyKilometrajeVehiculo(ActionEvent actionEvent) {
        try {
            if (getIdMoneda() > 0) {
                vehiculoBeanModel.modifyKilometrajeVehiculo();
            } else {
                FacesUtils.addErrorMessage("popupModifyKilometraje:msgsModifyKilometraje", FacesUtils.getKeyResourceBundle("sgl.kilometraje.mensaje.error.modificarNoEsReinicar"));
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            LOGGER.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            e.printStackTrace();
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            mantenimientoBeanModel.traerKilometrajeActualOld();
            if (getIdMoneda() > 0) {
                closePopupModifyKilometraje(actionEvent);
            }
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Color - FIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void abrirPopupAgregarColor(ActionEvent event) {
        vehiculoBeanModel.setSgColor(new SgColor());
        vehiculoBeanModel.setPopUp(true);
    }

    public void cerrarPopupAgregarColor(ActionEvent event) {
        vehiculoBeanModel.setSgColor(null);
        vehiculoBeanModel.setPopUp(false);
        clearComponent("popupCrearColor", "color");
    }

    public DataModel getTraerColor() {
        try {
            return vehiculoBeanModel.traerColores();
        } catch (Exception e) {
            return null;
        }
    }

    public void seleccionarColor(ActionEvent event) {
        vehiculoBeanModel.setSgColor((SgColor) vehiculoBeanModel.getLista().getRowData());
        vehiculoBeanModel.setModificarPopUp(true);
    }

    public void eliminarColor(ActionEvent event) {
        vehiculoBeanModel.setSgColor((SgColor) vehiculoBeanModel.getLista().getRowData());

        if (vehiculoBeanModel.buscarColorOcupado()) {
            //sgl.color.mensaje.no.eliminar
//            FacesUtils.addInfoMessage("No es posible eliminar el registro, esta siendo utilizado en otro proceso");
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.color.mensaje.no.eliminar"));
        } else {
            LOGGER.info(this, "eliminar ");
            vehiculoBeanModel.eliminarColor();
            vehiculoBeanModel.setSgColor(null);
        }
    }

    public void guardarColor(ActionEvent event) {
        if (vehiculoBeanModel.getSgColor().getNombre().isEmpty()) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.color.mensaje.falta"));
            vehiculoBeanModel.setSgColor(null);
            vehiculoBeanModel.setPopUp(false);
            clearComponent("popupCrearColor", "color");
        } else if (vehiculoBeanModel.buscarColorPorNombre() != null) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.color.mensaje.ya.existe") + ": " + vehiculoBeanModel.getSgColor().getNombre());
            vehiculoBeanModel.setSgColor(null);
            vehiculoBeanModel.setSgColor(null);
            vehiculoBeanModel.setPopUp(false);
            clearComponent("popupCrearColor", "color");
        } else {
            vehiculoBeanModel.guardarColor();
            vehiculoBeanModel.traerColorsItems();
            vehiculoBeanModel.setSgColor(null);
            vehiculoBeanModel.setSgColor(null);
            vehiculoBeanModel.setPopUp(false);
            clearComponent("popupCrearColor", "color");
        }
    }

    public void completarModificacionColor(ActionEvent event) {
        if (vehiculoBeanModel.getSgColor().getNombre().isEmpty()) {
            //FacesUtils.addInfoMessage("Por favor especifique el nombre del color");
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.color.mensaje.falta"));
        } else if (vehiculoBeanModel.buscarColorPorNombre() != null) {
            //
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.color.mensaje.ya.existe"));
            //FacesUtils.addInfoMessage("Ya existe un nombre con esa especificación, por favor intente con otro nombre");
        } else {
            vehiculoBeanModel.modificarColor();
            vehiculoBeanModel.setSgColor(null);
            vehiculoBeanModel.setModificarPopUp(false);
        }
    }

    public void cerrarPopModificar(ActionEvent event) {
        vehiculoBeanModel.setSgColor(null);
        vehiculoBeanModel.setModificarPopUp(false);
    }

    /**
     * *************************************************************************
     */
    //-----------------INICIO - ACTUALIZAR KILOMENTRAJES ----------------------------
    //-----------------FIN - ACTUALIZAR KILOMENTRAJES ----------------------------
    public void openPopupAddObservacionToChecklistExterior(ActionEvent actionEvent) {
        vehiculoBeanModel.controlarPop("popupObservacionToAdjunto", Boolean.TRUE);
    }

    public void closePopupAddObservacionToChecklistExterior(ActionEvent actionEvent) {
        clearComponent("popupAgregarObservacion", "observacion");
        vehiculoBeanModel.controlarPop("popupObservacionToAdjunto", Boolean.FALSE);
    }

    public void openPopupUpdateObservacionToChecklistExterior(ActionEvent actionEvent) {
        vehiculoBeanModel.controlarPop("popupUpdateObservacionToAdjunto", Boolean.TRUE);
    }

    public void closePopupUpdateObservacionToChecklistExterior(ActionEvent actionEvent) {
        clearComponent("popupModificarObservacion", "observacion");
        vehiculoBeanModel.controlarPop("popupUpdateObservacionToAdjunto", Boolean.FALSE);
    }

    public void openPopupRestartKilometraje(ActionEvent actionEvent) {
        vehiculoBeanModel.setMensaje("");
        vehiculoBeanModel.controlarPop("popupRestartKilometraje", Boolean.TRUE);
    }

    public void closePopupRestartKilometraje(ActionEvent actionEvent) {
        vehiculoBeanModel.setMensaje(null);
        clearComponent("popupRestartKilometraje", "kilometraje");
        vehiculoBeanModel.controlarPop("popupRestartKilometraje", Boolean.FALSE);
    }

    public void openPopupModifyKilometraje(ActionEvent actionEvent) {
        mantenimientoBeanModel.traerVehiculoMantenimientoNoTerminado();
        SgKilometrajeVo kmActual, kmAnteriorBueno = null;
        //traer kilometrajes anteriores y actual
        List<SgKilometrajeVo> listKm = mantenimientoBeanModel.traerKilometrajeAnteriorBuenoYActual();
        if (listKm != null) {
            //validar que el km actual no sea de un reinicio o modificacion
            mantenimientoBeanModel.setKilometrajeActualVo(listKm.get(0));
            mantenimientoBeanModel.setKilometrajeAnteriorBueno(listKm.size() > 1 ? listKm.get(1) : listKm.get(0));
            if (mantenimientoBeanModel.getKilometrajeActualVo().getIdSgTipoEspecifico() == Constantes.ID_TIPO_ESPECIFICO_REINICIO_KILOMETRAJE) {
                FacesUtils.addErrorMessage("No se puede modificar el kilometraje debido a que la ultima operacion de este vehiculo fue un Reinicio de Kilometraje...");
            } else {
                if (mantenimientoBeanModel.getOperacionTerminarRegistro().equals("TRUE")) {
                    //no se ha terminado un mantenimiento..
                    FacesUtils.addErrorMessage("Existe un mantenimiento activo registrado para este vehiculo, tiene que terminarlo para poder modificar el kilometraje");
                } else {
                    vehiculoBeanModel.setMensaje("");
                    //traer el ultimo kilometraje bueno del vehiculo...
                    setIdMoneda(mantenimientoBeanModel.getKilometrajeActualVo().getKilometrajeActual());
                    vehiculoBeanModel.controlarPop("popupModifyKilometraje", Boolean.TRUE);
                }
            }
        }
    }

    public SgKilometrajeVo getKilometrajeActualVo() {
        return mantenimientoBeanModel.getKilometrajeActualVo();
    }

    public void closePopupModifyKilometraje(ActionEvent actionEvent) {
        vehiculoBeanModel.setMensaje(null);
        clearComponent("popupModifyKilometraje", "motivoModificacionKilometraje");
        vehiculoBeanModel.controlarPop("popupModifyKilometraje", Boolean.FALSE);
    }

    //FIN CATALOGO VEHICULO
    //*************Cambio vehiculo de Oficina
    public List<SelectItem> getListaOficina() {
        return vehiculoBeanModel.listaOficina();
    }

    public List<SelectItem> getListaOficina2() {
        return vehiculoBeanModel.listaOficina2();
    }

    public int getIdOficina() {
        return vehiculoBeanModel.getIdOficina();

    }

    public void setIdOficina(int idOficina) {
        vehiculoBeanModel.setIdOficina(idOficina);
    }

    public int getIdOficina2() {
        return vehiculoBeanModel.getIdOficina2();
    }

    public void setIdOficina2(int idOficina) {
        vehiculoBeanModel.setIdOficina2(idOficina);
    }

    public DataModel traerAllVehiculoOficina() {
        try {
            this.vehiculoBeanModel.getAllVehiculoOficina();

            return this.vehiculoBeanModel.getDataModel();
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            LOGGER.fatal(this, e.getMessage());
            return null;
        }
    }

    public DataModel getTraerAllVehiculoPorOficina() {
        try {
            return vehiculoBeanModel.getLista();
//            return vehiculoBeanModel.traerVehiculoPorOficina();
        } catch (Exception e) {
            return null;
        }
    }

    public void idOficinaSel(ValueChangeEvent valueChangeEvent) throws SIAException, Exception {
        if (valueChangeEvent != null) {
            vehiculoBeanModel.setIdOficina((Integer) valueChangeEvent.getNewValue());
            vehiculoBeanModel.traerVehiculoEstado();
        }
    }

    public void cambiarEstado(ValueChangeEvent valueChangeEvent) throws SIAException, Exception {
        if (valueChangeEvent != null) {
            vehiculoBeanModel.setIdEstado((Integer) valueChangeEvent.getNewValue());
            vehiculoBeanModel.traerVehiculoEstado();
        }

    }

    public void openPopupCambioOficina(ActionEvent actionEvent) {
        //LOGGER.debug(this," openPopupCambioOficina " + vehiculoBeanModel.getVehiculo().toString());
        int idVehiculo = Integer.parseInt(FacesUtils.getRequestParameter("idVehiculo"));
        vehiculoBeanModel.setVehiculo(vehiculoBeanModel.getVehiculoById(idVehiculo));

        if (vehiculoBeanModel.verificaViaje()) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo.viaje"));
        } else {
            if (vehiculoBeanModel.verificaMantenimiento()) {
                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo.mantenimiento"));
            } else {
                setIdOficina2(-1);
                vehiculoBeanModel.controlarPop("popupCambiarOficina", Boolean.TRUE);
                setMensaje("");
                vehiculoBeanModel.controlarPop("popupPreGuardarCambioOficina", Boolean.TRUE);
            }
        }
    }

    public void closePopupCambioOficina(ActionEvent actionEvent) {

        this.vehiculoBeanModel.setVehiculo(null);
        this.vehiculoBeanModel.controlarPop("popupCambiarOficina", Boolean.FALSE);
    }

    public void buscarVehiculoPorUsuario(ActionEvent event) {
        vehiculoBeanModel.traerVehiculoPorUsuario();
    }

    /**
     * Creo: NLopez
     *
     * @param actionEvent
     */
    public void closePopupPreGuardarCambioOficina(ActionEvent actionEvent) {
        this.vehiculoBeanModel.controlarPop("popupPreGuardarCambioOficina", Boolean.FALSE);
    }

    public void saveCambioOficina(ActionEvent actionEvent) {
        //if (getIdOficina2() == -1) {
        //  FacesUtils.addErrorMessage("popupCambiarOficina:msgpopupcambio", FacesUtils.getKeyResourceBundle("slg.vehiculo.oficina.elije"));
        //} else {
        try {
            this.vehiculoBeanModel.saveCambioOficina();
            closePopupPreGuardarCambioOficina(actionEvent);
            closePopupCambioOficina(actionEvent);
            //this.vehiculoBeanModel.reloadAllVehiculoOficina();
            vehiculoBeanModel.traerVehiculoPorOficina();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo.oficina.cambio.correcto"));

        } catch (SIAException ex) {
            Logger.getLogger(VehiculoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(VehiculoBean.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
//        }
    }
    //**********Fin Cambio vehiculo de oficina *************

    /**
     * ******** Actualizar Kilometrajes de Vehículo *********
     * ******************************************************
     */
    public DataModel getSgKilometrajeForUpdateDataModel() {
        LOGGER.info(this, "VehiculoBean.getSgKilometrajeForUpdateDataModel()");
        if (getDataModel() == null) {
            setDataModel(new ListDataModel(this.vehiculoBeanModel.getSgKilometrajeForUpdateList()));
        }
        return getDataModel();
    }

    public void updateAllKilometraje(ActionEvent actionEvent) {
        boolean kilometraje_blank = false;
        boolean kilometraje_less = false;

        //Validar que todos los campos lleven valor
        DataModel<SgKilometrajeVo> dataModel = getDataModel();

        for (SgKilometrajeVo vo : dataModel) {
            vo.setValidado(true);

            if (vo.getKilometrajeNuevo() == null) {
                kilometraje_blank = true;
            } else {
                if (vo.getKilometrajeNuevo().intValue() < vo.getKilometrajeActual().intValue()) {
                    kilometraje_less = true;
                }
            }
        }

        LOGGER.info(this, "kilometraje_blank: " + kilometraje_blank);
        LOGGER.info(this, "kilometraje_less: " + kilometraje_less);

        if (kilometraje_blank) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo.kilometraje.mensaje.error.kilometrajesRequeridos"));
        }
        if (kilometraje_less) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo.kilometraje.mensaje.error.kilometrajeNuevoMenorActual"));
        }

        if (!kilometraje_blank && !kilometraje_less) {
            try {
                this.vehiculoBeanModel.updateAllKilometraje();
                FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo.kilometraje.mensaje.info.kilometrajesActualizadosSatisfactoriamente"));
            } catch (Exception e) {
                LOGGER.fatal(this, e.getMessage());
                e.printStackTrace();
                FacesUtils.addErrorMessage(new SIAException().getMessage());
            }
        }
    }

    /**
     * @return the popUp >>>>>>> servlet
     */
    public boolean isPopUp() {
        return vehiculoBeanModel.isPopUp();
    }

    /**
     * @param popUp the popUp to set
     */
    public void setPopUp(boolean popUp) {
        vehiculoBeanModel.setPopUp(popUp);
    }

    /**
     * @return the crearPop
     */
    public boolean isCrearPop() {
        return vehiculoBeanModel.isCrearPop();
    }

    /**
     * @param crearPop the crearPop to set
     */
    public void setCrearPop(boolean crearPop) {
        vehiculoBeanModel.setCrearPop(crearPop);
    }

    /**
     * @return the eliminarPop
     */
    public boolean isEliminarPop() {
        return vehiculoBeanModel.isEliminarPop();
    }

    /**
     * @param eliminarPop the eliminarPop to set
     */
    public void setEliminarPop(boolean eliminarPop) {
        vehiculoBeanModel.setEliminarPop(eliminarPop);
    }

    /**
     * @return the modificarPop
     */
    public boolean isModificarPop() {
        return vehiculoBeanModel.isModificarPop();
    }

    /**
     * @param modificarPop the modificarPop to set
     */
    public void setModificarPop(boolean modificarPop) {
        vehiculoBeanModel.setModificarPop(modificarPop);
    }

    /**
     * @return the subirArchivoPop
     */
    public boolean isSubirArchivoPop() {
        return vehiculoBeanModel.isSubirArchivoPop();
    }

    /**
     * @param subirArchivoPop the subirArchivoPop to set
     */
    public void setSubirArchivoPop(boolean subirArchivoPop) {
        vehiculoBeanModel.setSubirArchivoPop(subirArchivoPop);
    }

    /**
     * Propiedades
     */
    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return vehiculoBeanModel.getMensaje();
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        vehiculoBeanModel.setMensaje(mensaje);
    }

    /**
     * @return the user
     */
    public String getUser() {
        return vehiculoBeanModel.getUser();
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        vehiculoBeanModel.setUser(user);
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return vehiculoBeanModel.getUsuario();
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        vehiculoBeanModel.setUsuario(usuario);
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
        return vehiculoBeanModel.getIdTipoEspecifico();
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
        vehiculoBeanModel.setIdTipoEspecifico(idTipoEspecifico);
    }

    /**
     * @return the alerta
     */
    public String getAlerta() {
        return vehiculoBeanModel.getAlerta();
    }

    /**
     * @param alerta the alerta to set
     */
    public void setAlerta(String alerta) {
        vehiculoBeanModel.setAlerta(alerta);
    }

    /**
     * @return the numerDias
     */
    public int getNumerDias() {
        return vehiculoBeanModel.getNumerDias();
    }

    /**
     * @param numerDias the numerDias to set
     */
    public void setNumerDias(int numerDias) {
        vehiculoBeanModel.setNumerDias(numerDias);
    }

    /**
     * @return the sgKilometraje
     */
    public SgKilometraje getSgKilometraje() {
        return vehiculoBeanModel.getSgKilometraje();
    }

    /**
     * @param sgKilometraje the sgKilometraje to set
     */
    public void setSgKilometraje(SgKilometraje sgKilometraje) {
        vehiculoBeanModel.setSgKilometraje(sgKilometraje);
    }

    /**
     * @return the idCheclist
     */
    public int getIdChecklist() {
        return vehiculoBeanModel.getIdChecklist();
    }

    /**
     * @param idCheclist the idCheclist to set
     */
    public void setIdChecklist(int idChecklist) {
        vehiculoBeanModel.setIdChecklist(idChecklist);
    }

    /**
     * @return the sgAsignarVehiculo
     */
    public SgAsignarVehiculo getSgAsignarVehiculo() {
        return vehiculoBeanModel.getSgAsignarVehiculo();
    }

    /**
     * @param sgAsignarVehiculo the sgAsignarVehiculo to set
     */
    public void setSgAsignarVehiculo(SgAsignarVehiculo sgAsignarVehiculo) {
        vehiculoBeanModel.setSgAsignarVehiculo(sgAsignarVehiculo);
    }

    /**
     * @return the idModulo
     */
    public int getIdModelo() {
        return vehiculoBeanModel.getIdModelo();
    }

    /**
     * @param idModulo the idModulo to set
     */
    public void setIdModelo(int idModelo) {
        vehiculoBeanModel.setIdModelo(idModelo);
    }

    /**
     * @return the idMarca
     */
    public int getIdMarca() {
        return vehiculoBeanModel.getIdMarca();
    }

    /**
     * @param idMarca the idMarca to set
     */
    public void setIdMarca(int idMarca) {
        vehiculoBeanModel.setIdMarca(idMarca);
    }

    /**
     * @return the opcionSeleccionada
     */
    public String getOpcionSeleccionada() {
        return vehiculoBeanModel.getOpcionSeleccionada();
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(String opcionSeleccionada) {
        vehiculoBeanModel.setOpcionSeleccionada(opcionSeleccionada);
    }

    /**
     * @return the cadenaBuscar
     */
    public String getCadenaBuscar() {
        return vehiculoBeanModel.getCadenaBuscar();
    }

    /**
     * @param cadenaBuscar the cadenaBuscar to set
     */
    public void setCadenaBuscar(String cadenaBuscar) {
        vehiculoBeanModel.setCadenaBuscar(cadenaBuscar);
    }

    /**
     * @return the sgChecklistLlantas
     */
    public SgChecklistLlantas getSgChecklistLlantas() {
        return vehiculoBeanModel.getSgChecklistLlantas();
    }

    /**
     * @param sgChecklistLlantas the sgChecklistLlantas to set
     */
    public void setSgChecklistLlantas(SgChecklistLlantas sgChecklistLlantas) {
        vehiculoBeanModel.setSgChecklistLlantas(sgChecklistLlantas);
    }

    /**
     * @return the sgChecklistExtVehiculo
     */
    public SgChecklistExtVehiculo getSgChecklistExtVehiculo() {
        return vehiculoBeanModel.getSgChecklistExtVehiculo();
    }

    /**
     * @param sgChecklistExtVehiculo the sgChecklistExtVehiculo to set
     */
    public void setSgChecklistExtVehiculo(SgChecklistExtVehiculo sgChecklistExtVehiculo) {
        vehiculoBeanModel.setSgChecklistExtVehiculo(sgChecklistExtVehiculo);
    }

    /**
     * @return the recibirVehiculoPop
     */
    public boolean isRecibirVehiculoPop() {
        return vehiculoBeanModel.isRecibirVehiculoPop();
    }

    /**
     * @param recibirVehiculoPop the recibirVehiculoPop to set
     */
    public void setRecibirVehiculoPop(boolean recibirVehiculoPop) {
        vehiculoBeanModel.setRecibirVehiculoPop(recibirVehiculoPop);
    }

    /**
     * @return the idAsignaVehiculo
     */
    public int getIdAsignaVehiculo() {
        return vehiculoBeanModel.getIdAsignaVehiculo();
    }

    /**
     * @param idAsignaVehiculo the idAsignaVehiculo to set
     */
    public void setIdAsignaVehiculo(int idAsignaVehiculo) {
        vehiculoBeanModel.setIdAsignaVehiculo(idAsignaVehiculo);
    }

    /**
     * @return the vehiculo
     */
    public VehiculoVO getVehiculo() {
        return vehiculoBeanModel.getVehiculo();
    }

    /**
     * @param vehiculo the vehiculo to set
     */
    public void setVehiculo(VehiculoVO vehiculo) {
        vehiculoBeanModel.setVehiculo(vehiculo);
    }

    public SgPagoServicioVehiculo getServicioVehiculo() {
        return vehiculoBeanModel.getSgPagoServicioVehiculo();
    }

    public String getPro() {
        return vehiculoBeanModel.getPro();
    }

    public void setPro(String pro) {
        vehiculoBeanModel.setPro(pro);
    }

    /**
     * @param sgPagoServicio the sgPagoServicio to set
     */
    public void setSgPagoServicio(SgPagoServicio sgPagoServicio) {
        vehiculoBeanModel.setSgPagoServicio(sgPagoServicio);
    }

    public SgPagoServicio getSgPagoServicio() {
        return vehiculoBeanModel.getSgPagoServicio();
    }

    public SgTipoEspecifico getSgTipoEspecifico() {
        return vehiculoBeanModel.getSgTipoEspecifico();
    }

    public int getIdMoneda() {
        return vehiculoBeanModel.getIdMoneda();
    }

    public void setIdMoneda(int idMoneda) {
        vehiculoBeanModel.setIdMoneda(idMoneda);
    }

    public int getIdColor() {
        return vehiculoBeanModel.getIdColor();
    }

    public void setIdColor(int idColor) {
        vehiculoBeanModel.setIdColor(idColor);
    }

    /**
     * @return the sgVehiculoChecklist
     */
    public SgVehiculoChecklist getSgVehiculoChecklist() {
        return vehiculoBeanModel.getSgVehiculoChecklist();
    }

    /**
     * @param sgVehiculoChecklist the sgVehiculoChecklist to set
     */
    public void setSgVehiculoChecklist(SgVehiculoChecklist sgVehiculoChecklist) {
        vehiculoBeanModel.setSgVehiculoChecklist(sgVehiculoChecklist);
    }

    /**
     * @return the marca
     */
    public SgMarca getMarca() {
        return vehiculoBeanModel.getMarca();
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(SgMarca marca) {
        vehiculoBeanModel.setMarca(marca);
    }

    /**
     * @return the modelo
     */
    public SgModelo getModelo() {
        return vehiculoBeanModel.getModelo();
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(SgModelo modelo) {
        vehiculoBeanModel.setModelo(modelo);
    }

    /**
     * @return the sgAsignarVehiculoRecibido
     */
    public SgAsignarVehiculo getSgAsignarVehiculoRecibido() {
        return vehiculoBeanModel.getSgAsignarVehiculoRecibido();
    }

    /**
     * @param sgAsignarVehiculoRecibido the sgAsignarVehiculoRecibido to set
     */
    public void setSgAsignarVehiculoRecibido(SgAsignarVehiculo sgAsignarVehiculoRecibido) {
        vehiculoBeanModel.setSgAsignarVehiculoRecibido(sgAsignarVehiculoRecibido);
    }

    /**
     * @return the matchesList
     */
    public List<SelectItem> getMatchesList() {
        return vehiculoBeanModel.getMatchesList();
    }

    /**
     * @param matchesList the matchesList to set
     */
    public void setMatchesList(List<SelectItem> matchesList) {
        vehiculoBeanModel.setMatchesList(matchesList);
    }

    /**
     * @return the prefijo
     */
    public String getPrefijo() {
        return vehiculoBeanModel.getPrefijo();
    }

    /**
     * @param prefijo the prefijo to set
     */
    public void setPrefijo(String prefijo) {
        vehiculoBeanModel.setPrefijo(prefijo);
    }

    /**
     * @return the asignacionSinTerminar
     */
    public boolean isAsignacionSinTerminar() {
        return vehiculoBeanModel.isAsignacionSinTerminar();
    }

    /**
     * @param asignacionSinTerminar the asignacionSinTerminar to set
     */
    public void setAsignacionSinTerminar(boolean asignacionSinTerminar) {
        vehiculoBeanModel.setAsignacionSinTerminar(asignacionSinTerminar);
    }

    /**
     * @return the checklistExtVehiculo
     */
    public SgChecklistExtVehiculo getChecklistExtVehiculo() {
        return vehiculoBeanModel.getChecklistExtVehiculo();
    }

    /**
     * @param checklistExtVehiculo the checklistExtVehiculo to set
     */
    public void setChecklistExtVehiculo(SgChecklistExtVehiculo checklistExtVehiculo) {
        vehiculoBeanModel.setChecklistExtVehiculo(checklistExtVehiculo);
    }

    /**
     * @return the checklistLlantas
     */
    public SgChecklistLlantas getChecklistLlantas() {
        return vehiculoBeanModel.getChecklistLlantas();
    }

    /**
     * @param checklistLlantas the checklistLlantas to set
     */
    public void setChecklistLlantas(SgChecklistLlantas checklistLlantas) {
        vehiculoBeanModel.setChecklistLlantas(checklistLlantas);
    }

    /**
     * @return the vehiculoChecklist
     */
    public SgVehiculoChecklist getVehiculoChecklist() {
        return vehiculoBeanModel.getVehiculoChecklist();
    }

    /**
     * @param vehiculoChecklist the vehiculoChecklist to set
     */
    public void setVehiculoChecklist(SgVehiculoChecklist vehiculoChecklist) {
        vehiculoBeanModel.setVehiculoChecklist(vehiculoChecklist);
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return vehiculoBeanModel.isFlag();
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        vehiculoBeanModel.setFlag(flag);
    }

    /**
     * @return the verDetallePop
     */
    public boolean isVerDetallePop() {
        return vehiculoBeanModel.isVerDetallePop();
    }

    /**
     * @param verDetallePop the verDetallePop to set
     */
    public void setVerDetallePop(boolean verDetallePop) {
        vehiculoBeanModel.setVerDetallePop(verDetallePop);
    }

    /**
     * @return the pagoPop
     */
    public boolean isPagoPop() {
        return vehiculoBeanModel.isPagoPop();
    }

    /**
     * @param pagoPop the pagoPop to set
     */
    public void setPagoPop(boolean pagoPop) {
        vehiculoBeanModel.setPagoPop(pagoPop);
    }

    /**
     * @return the sgPagoServicioVehiculo
     */
    public SgPagoServicioVehiculo getSgPagoServicioVehiculo() {
        return vehiculoBeanModel.getSgPagoServicioVehiculo();
    }

    /**
     * @param sgPagoServicioVehiculo the sgPagoServicioVehiculo to set
     */
    public void setSgPagoServicioVehiculo(SgPagoServicioVehiculo sgPagoServicioVehiculo) {
        vehiculoBeanModel.setSgPagoServicioVehiculo(sgPagoServicioVehiculo);
    }

    /**
     * @return the idPais
     */
    public int getIdPais() {
        return vehiculoBeanModel.getIdPais();
    }

    /**
     * @param idPais the idPais to set
     */
    public void setIdPais(int idPais) {
        vehiculoBeanModel.setIdPais(idPais);
    }

    public boolean isCrearPopUp() {
        return vehiculoBeanModel.isCrearPopUp();
    }

    public boolean isModificarPopUp() {
        return vehiculoBeanModel.isModificarPopUp();
    }

    public SgColor getSgColor() {
        return vehiculoBeanModel.getSgColor();
    }

    public void setSgMotivo(SgColor sgColor) {
        vehiculoBeanModel.setSgColor(sgColor);
    }

    /**
     * @return the kilometrajeActual
     */
    public SgKilometraje getKilometrajeActual() {
        return vehiculoBeanModel.getKilometrajeActual();
    }

    /**
     * @param kilometrajeActual the kilometrajeActual to set
     */
    public void setKilometrajeActual(SgKilometraje kilometrajeActual) {
        vehiculoBeanModel.setKilometrajeActual(kilometrajeActual);
    }

    /**
     * @return the licenciaVo =======
     * @return the dataModel
     */
    public DataModel getDataModel() {
        return this.vehiculoBeanModel.getDataModel();
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
        this.vehiculoBeanModel.setDataModel(dataModel);
    }

    /**
     * @return the numerDias
     */
    public int getKilometrajeInicial() {
        return this.vehiculoBeanModel.getNumerDias();
    }

    /**
     * @param numerDias the numerDias to set
     */
    public void setKilometrajeInicial(int numerDias) {
        this.vehiculoBeanModel.setNumerDias(numerDias);
    }

    /**
     * @return the idAsignaVehiculo
     */
    public int getPeriodicidadAvisoMantenimiento() {
        return this.vehiculoBeanModel.getIdAsignaVehiculo();
    }

    /**
     * @param idAsignaVehiculo the idAsignaVehiculo to set
     */
    public void setPeriodicidadAvisoMantenimiento(int idAsignaVehiculo) {
        this.vehiculoBeanModel.setIdAsignaVehiculo(idAsignaVehiculo);
    }

    /**
     * @param vehiculoBeanModel the vehiculoBeanModel to set
     */
    public void setVehiculoBeanModel(VehiculoBeanModel vehiculoBeanModel) {
        this.vehiculoBeanModel = vehiculoBeanModel;
    }

    /**
     * @param mantenimientoBeanModel the mantenimientoBeanModel to set
     */
    public void setMantenimientoBeanModel(MantenimientoBeanModel mantenimientoBeanModel) {
        this.mantenimientoBeanModel = mantenimientoBeanModel;
    }

    /**
     * @return the sgVehiculoSeleccionado
     */
    public VehiculoVO getSgVehiculoSeleccionado() {
        return mantenimientoBeanModel.getSgVehiculoSeleccionado();
    }

    /**
     * @param sgVehiculoSeleccionado the sgVehiculoSeleccionado to set
     */
    public void setSgVehiculoSeleccionado(VehiculoVO sgVehiculoSeleccionado) {
        mantenimientoBeanModel.setSgVehiculoSeleccionado(sgVehiculoSeleccionado);
    }

    /**
     * @return the idEstado
     */
    public int getIdEstado() {
        return vehiculoBeanModel.getIdEstado();
    }

    /**
     * @param idEstado the idEstado to set
     */
    public void setIdEstado(int idEstado) {
        vehiculoBeanModel.setIdEstado(idEstado);
    }

    /**
     * @return the listaTaller
     */
    public List<SelectItem> getListaTaller() {
        return vehiculoBeanModel.getListaTaller();
    }

    /**
     * @param listaTaller the listaTaller to set
     */
    public void setListaTaller(List<SelectItem> listaTaller) {
        vehiculoBeanModel.setListaTaller(listaTaller);
    }

    /**
     * @return the listaEstado
     */
    public List<SelectItem> getListaEstado() {
        return vehiculoBeanModel.getListaEstado();
    }

    /**
     * @param listaEstado the listaEstado to set
     */
    public void setListaEstado(List<SelectItem> listaEstado) {
        vehiculoBeanModel.setListaEstado(listaEstado);
    }

    /**
     * @return the mapaDatos
     */
    public Map<String, DataModel> getMapaDatos() {
        return vehiculoBeanModel.getMapaDatos();
    }

    /**
     * @param mapaDatos the mapaDatos to set
     */
    public void setMapaDatos(Map<String, DataModel> mapaDatos) {
        vehiculoBeanModel.setMapaDatos(mapaDatos);
    }

}
