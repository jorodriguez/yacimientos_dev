/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.staff.bean.backing;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.persistence.NonUniqueResultException;
import sia.excepciones.SIAException;
import sia.modelo.Convenio;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaCocina;
import sia.modelo.SgCaracteristicaGym;
import sia.modelo.SgCaracteristicaHabitacion;
import sia.modelo.SgCaracteristicaStaff;
import sia.modelo.SgCocina;
import sia.modelo.SgDireccion;
import sia.modelo.SgGym;
import sia.modelo.SgHistorialConvenioStaff;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffHabitacion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.staff.bean.model.StaffBeanModel;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Named(value = "staffBean")
@RequestScoped
public class StaffBean implements Serializable {

    @ManagedProperty(value = "#{staffBeanModel}")
    private StaffBeanModel staffBeanModel;
    //
    private String mensajeError = "";

    /**
     * Creates a new instance of StaffBean
     */
    public StaffBean() {
    }

    public String goToAdministrarStaff() {
        staffBeanModel.setStaff((SgStaff) staffBeanModel.getDataModel().getRowData());

        staffBeanModel.setUltimoConvenioStaff(staffBeanModel.buscarContratoVigente());
        staffBeanModel.setListaProveedorBuscar(staffBeanModel.traerProveedor());
        staffBeanModel.controlaPopUp("popupCaracteristicasStaff", Boolean.FALSE);
        staffBeanModel.controlaPopUp("popupCaracteristicasHabitacion", Boolean.FALSE);
        staffBeanModel.controlaPopUp("popupCaracteristicasGimnasio", Boolean.FALSE);
        staffBeanModel.controlaPopUp("popupCaracteristicasCocina", Boolean.FALSE);
        return "/vistas/sgl/staff/administrarStaff";
    }

    public String goToCatalogoStaff() {
        staffBeanModel.beginConversationStaffCatalog();
        return "/vistas/sgl/staff/catalogoStaff";
    }

    public String goToMenuPrincipalSGL() {
        UtilLog4j.log.info(this, "StaffBean.goToMenuPrincipalSGL");
        return "/principal";
    }

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
        UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    public DataModel getStaffDataModel() {
        staffBeanModel.getAllStaffByOficina();
        return staffBeanModel.getDataModel();
    }

    public DataModel getHabitacionesDataModel() {
        staffBeanModel.getAllHabitacionesByStaff();
        return staffBeanModel.getHabitacionesDataModel();
    }

    public DataModel getGimnasiosDataModel() {
        staffBeanModel.getAllGimnasiosByStaff();
        return staffBeanModel.getGimnasiosDataModel();
    }

    public DataModel getCocinasDataModel() {
        staffBeanModel.getAllCocinasByStaff();
        return staffBeanModel.getCocinasDataModel();
    }

//    public void valueChangedCaracteristica(ValueChangeEvent valueChangeEvent) {
//       UtilLog4j.log.info(this, "valueChanged");
//        if (valueChangeEvent.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete = (SelectInputText) valueChangeEvent.getComponent();
//
//            String text = (String) valueChangeEvent.getNewValue();
//           UtilLog4j.log.info(this, "Text:" + text);
//
//            if (text == null || text.trim().isEmpty()) {
//               UtilLog4j.log.info(this, "text == null o length==0");
//                staffBeanModel.getMatchesList().clear();
//            } else {
//                updateList(valueChangeEvent);
//            }
//
//            if (autoComplete.getSelectedItem() != null) { //Si se seleccionó una Característica desde el SelectItem
//               UtilLog4j.log.info(this, "Actual SelectItem: " + (String) autoComplete.getSelectedItem().getValue());
//                try { //Agregar la Característica
//                    if (staffBeanModel.getCantidadCaracteristica() != null && staffBeanModel.getCantidadCaracteristica() > 0) {
//                        staffBeanModel.setPrefijo((String) autoComplete.getSelectedItem().getValue());
//                        staffBeanModel.addCaracteristica();
//                        FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + (String) autoComplete.getSelectedItem().getValue());
//                    } else {
//                        FacesUtils.addErrorMessage("msgCaracteristicas", "Cantidad es requerido");
//                    }
//                } catch (SIAException siae) {
//                    FacesUtils.addErrorMessage(siae.getMessage());
//                   UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
//                } catch (Exception e) {
//                   UtilLog4j.log.info(this, e.getMessage());
//
//                    FacesUtils.addErrorMessage(new SIAException().getMessage());
//                } finally {
//                    staffBeanModel.setCantidadCaracteristica(1);
//                }
//            }
//        }
//    }
//    public void updateList(ValueChangeEvent valueChangeEvent) {
//       UtilLog4j.log.info(this, "Actualizando la lista desde el updateList(valueChangeEvent)");
//        SelectInputText autoComplete = (SelectInputText) valueChangeEvent.getComponent();
//        String text = (String)valueChangeEvent.getNewValue();
//        updateList(text);
//    }
    public void textChangeListener(String cadena) {
        UtilLog4j.log.info(this, "textChangeEvent");
        this.staffBeanModel.setPrefijo(cadena);
        UtilLog4j.log.info(this, "prefijo: " + cadena);

        if (cadena == null || cadena.trim().isEmpty()) {
            UtilLog4j.log.info(this, "text == null o length==0");
            staffBeanModel.getMatchesList().clear();
        } else {
            updateList(cadena);
        }

//            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
        staffBeanModel.setPrefijo(cadena);

    }

    public void updateList(String text) {
        staffBeanModel.getMatchesList().clear();
        for (SelectItem si : staffBeanModel.getCaracteristicas()) {
            if ((((String) si.getValue()).toLowerCase()).startsWith(text.toLowerCase())) {
                staffBeanModel.getMatchesList().add(si);
            }
        }
        UtilLog4j.log.info(this, "Matcheslist: " + staffBeanModel.getMatchesList().size());
    }

    public DataModel getCaracteristicasStaffDataModel() {
        try {
            staffBeanModel.getAllCaracteristicasStaff();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            return staffBeanModel.getCaracteristicasStaffDataModel();
        }
    }

    public DataModel getCaracteristicasHabitacionDataModel() {
        try {
            staffBeanModel.getAllCaracteristicasHabitacion();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            return staffBeanModel.getCaracteristicasHabitacionDataModel();
        }
    }

    public DataModel getCaracteristicasGimnasioDataModel() {
        try {
            staffBeanModel.getAllCaracteristicasGimnasio();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            return staffBeanModel.getCaracteristicasGimnasioDataModel();
        }
    }

    public DataModel getCaracteristicasCocinaDataModel() {
        try {
            staffBeanModel.getAllCaracteristicasCocina();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            return staffBeanModel.getCaracteristicasCocinaDataModel();
        }
    }

    public void addCaracteristicaStaff(ActionEvent actionEvent) {
        try {
            if (!this.staffBeanModel.getPrefijo().isEmpty()) {
                if (staffBeanModel.getCantidadCaracteristica() != null && staffBeanModel.getCantidadCaracteristica() > 0) {
                    staffBeanModel.addCaracteristica();
                    updateList(staffBeanModel.getPrefijo());
                    FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + staffBeanModel.getPrefijo());
                } else {
                    FacesUtils.addErrorMessage("msgCaracteristicas", "Cantidad es requerido");
                }
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicas", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage("msgCaracteristicas", siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica al Staff");
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            staffBeanModel.setCantidadCaracteristica(1);
        }
    }

    public void addCaracteristicaHabitacion(ActionEvent actionEvent) {
        try {
            if (!this.staffBeanModel.getPrefijo().isEmpty()) {
                if (staffBeanModel.getCantidadCaracteristica() != null && staffBeanModel.getCantidadCaracteristica() > 0) {
                    staffBeanModel.addCaracteristica();
                    updateList(staffBeanModel.getPrefijo());
                    FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + staffBeanModel.getPrefijo());
                } else {
                    FacesUtils.addErrorMessage("msgCaracteristicas", "Cantidad es requerido");
                }
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicas", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage("msgCaracteristicas", siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica a la Habitación");
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            staffBeanModel.setCantidadCaracteristica(1);
        }
    }

    public void addCaracteristicaGimnasio(ActionEvent actionEvent) {
        try {
            if (!this.staffBeanModel.getPrefijo().isEmpty()) {
                if (staffBeanModel.getCantidadCaracteristica() != null && staffBeanModel.getCantidadCaracteristica() > 0) {
                    staffBeanModel.addCaracteristica();
                    updateList(staffBeanModel.getPrefijo());
                    FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + staffBeanModel.getPrefijo());
                } else {
                    FacesUtils.addErrorMessage("msgCaracteristicas", "Cantidad es requerido");
                }
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicas", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage("msgCaracteristicas", siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica al Gimnasio");
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            staffBeanModel.setCantidadCaracteristica(1);
        }
    }

    public void addCaracteristicaCocina(ActionEvent actionEvent) {
        try {
            if (!this.staffBeanModel.getPrefijo().isEmpty()) {
                if (staffBeanModel.getCantidadCaracteristica() != null && staffBeanModel.getCantidadCaracteristica() > 0) {
                    staffBeanModel.addCaracteristica();
                    updateList(staffBeanModel.getPrefijo());
                    FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + staffBeanModel.getPrefijo());
                } else {
                    FacesUtils.addErrorMessage("msgCaracteristicas", "Cantidad es requerido");
                }
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicas", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage("msgCaracteristicas", siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica a la Cocina");
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            staffBeanModel.setCantidadCaracteristica(1);
        }
    }

    public void removeCaracteristicaStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.setCaracteristicaStaff((SgCaracteristicaStaff) staffBeanModel.getCaracteristicasStaffDataModel().getRowData());
            staffBeanModel.removeCaracteristica(new SgCaracteristicaStaff());
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void removeCaracteristicaHabitacion(ActionEvent actionEvent) {
        try {
            staffBeanModel.setCaracteristicaHabitacion((SgCaracteristicaHabitacion) staffBeanModel.getCaracteristicasHabitacionDataModel().getRowData());
            staffBeanModel.removeCaracteristica(new SgCaracteristicaHabitacion());
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void removeCaracteristicaGimnasio(ActionEvent actionEvent) {
        try {
            staffBeanModel.setCaracteristicaGimnasio((SgCaracteristicaGym) staffBeanModel.getCaracteristicasGimnasioDataModel().getRowData());
            staffBeanModel.removeCaracteristica(new SgCaracteristicaGym());
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void removeCaracteristicaCocina(ActionEvent actionEvent) {
        try {
            staffBeanModel.setCaracteristicaCocina((SgCaracteristicaCocina) staffBeanModel.getCaracteristicasCocinaDataModel().getRowData());
            staffBeanModel.removeCaracteristica(new SgCaracteristicaCocina());
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void mostrarPopupAgregarCaracteristicaStaff(ActionEvent actionEvent) {
        staffBeanModel.setObject(new SgCaracteristicaStaff());
        //Cargar todas las Características
        staffBeanModel.getAllCaracteristicas();
        staffBeanModel.controlaPopUp("popupCaracteristicasStaff", Boolean.TRUE);
//        staffBeanModel.setMrPopupAgregarCaracteristicaStaff(!staffBeanModel.isMrPopupAgregarCaracteristicaStaff());
    }

    public void ocultarPopupAgregarCaracteristicaStaff(ActionEvent actionEvent) {
        //Quitándole memorias
        staffBeanModel.setPrefijo("");
        staffBeanModel.setCantidadCaracteristica(1);
        staffBeanModel.setObject(null);
        staffBeanModel.setCaracteristica(null);
        staffBeanModel.setCaracteristicaStaff(null);
        staffBeanModel.setCaracteristicas(null);
        staffBeanModel.setMatchesList(null);
        staffBeanModel.setCaracteristicasStaffDataModel(null);
        staffBeanModel.controlaPopUp("popupCaracteristicasStaff", Boolean.FALSE);
//        staffBeanModel.setMrPopupAgregarCaracteristicaStaff(!staffBeanModel.isMrPopupAgregarCaracteristicaStaff());
    }

    public void mostrarPopupCaracteristicasHabitacionStaff(ActionEvent actionEvent) {
        //Dándole memoria a la Habitación seleccionada
        SgStaffHabitacion habitacionSeleccionada = (SgStaffHabitacion) staffBeanModel.getHabitacionesDataModel().getRowData();
        UtilLog4j.log.info(this, "Habitación seleccionada: " + habitacionSeleccionada.getNombre());
        staffBeanModel.setHabitacion(habitacionSeleccionada);
        staffBeanModel.setObject(new SgCaracteristicaHabitacion());
        //Cargar todas las Características
        staffBeanModel.getAllCaracteristicas();
        staffBeanModel.controlaPopUp("popupCaracteristicasHabitacion", Boolean.TRUE);
//        staffBeanModel.setMrPopupCaracteristicasHabitacionStaff(!staffBeanModel.isMrPopupCaracteristicasHabitacionStaff());
    }

    public void ocultarPopupCaracteristicasHabitacionStaff(ActionEvent actionEvent) {
        //Quitándo memorias
        staffBeanModel.setPrefijo("");
        staffBeanModel.setCantidadCaracteristica(1);
        staffBeanModel.setHabitacion(null);
        staffBeanModel.setCaracteristica(null);
        staffBeanModel.setCaracteristicaHabitacion(null);
        staffBeanModel.setCaracteristicas(null);
        staffBeanModel.setMatchesList(null);
        staffBeanModel.setCaracteristicasHabitacionDataModel(null);
        staffBeanModel.controlaPopUp("popupCaracteristicasHabitacion", Boolean.FALSE);
//        staffBeanModel.setMrPopupCaracteristicasHabitacionStaff(!staffBeanModel.isMrPopupCaracteristicasHabitacionStaff());
    }

    public void mostrarPopupCaracteristicasGimnasioStaff(ActionEvent actionEvent) {
        //Dándole memoria al Gimnasio seleccionado
        SgGym gimnasioSeleccionado = (SgGym) staffBeanModel.getGimnasiosDataModel().getRowData();
        UtilLog4j.log.info(this, "Gimnasio seleccionado: " + gimnasioSeleccionado.getNombre());
        staffBeanModel.setGimnasio(gimnasioSeleccionado);
        staffBeanModel.setObject(new SgCaracteristicaGym());
        //Cargar todas las Características
        staffBeanModel.getAllCaracteristicas();
        staffBeanModel.controlaPopUp("popupCaracteristicasGimnasio", Boolean.TRUE);
//        staffBeanModel.setMrPopupCaracteristicasGimnasioStaff(!staffBeanModel.isMrPopupCaracteristicasGimnasioStaff());
    }

    public void ocultarPopupCaracteristicasGimnasioStaff(ActionEvent actionEvent) {
        //Quitándo memorias
        staffBeanModel.setPrefijo("");
        staffBeanModel.setCantidadCaracteristica(1);
        staffBeanModel.setGimnasio(null);
        staffBeanModel.setCaracteristica(null);
        staffBeanModel.setCaracteristicaGimnasio(null);
        staffBeanModel.setCaracteristicas(null);
        staffBeanModel.setMatchesList(null);
        staffBeanModel.setCaracteristicasGimnasioDataModel(null);
        staffBeanModel.controlaPopUp("popupCaracteristicasGimnasio", Boolean.FALSE);
//        staffBeanModel.setMrPopupCaracteristicasGimnasioStaff(!staffBeanModel.isMrPopupCaracteristicasGimnasioStaff());
    }

    public void mostrarPopupCaracteristicasCocinaStaff(ActionEvent actionEvent) {
        //Dándole memoria a la Cocina seleccionada
        SgCocina cocinaSeleccionada = (SgCocina) staffBeanModel.getCocinasDataModel().getRowData();
        UtilLog4j.log.info(this, "Cocina seleccionada: " + cocinaSeleccionada.getNombre());
        staffBeanModel.setCocina(cocinaSeleccionada);
        staffBeanModel.setObject(new SgCaracteristicaCocina());
        //Cargar todas las Características
        staffBeanModel.getAllCaracteristicas();
        staffBeanModel.controlaPopUp("popupCaracteristicasCocina", Boolean.TRUE);
//        staffBeanModel.setMrPopupCaracteristicasCocinaStaff(!staffBeanModel.isMrPopupCaracteristicasCocinaStaff());
    }

    public void ocultarPopupCaracteristicasCocinaStaff(ActionEvent actionEvent) {
        //Quitándole memoria a CaracteristicaStaff
        staffBeanModel.setPrefijo("");
        staffBeanModel.setCantidadCaracteristica(1);
        staffBeanModel.setCocina(null);
        staffBeanModel.setCaracteristica(null);
        staffBeanModel.setCaracteristicaCocina(null);
        staffBeanModel.setCaracteristicas(null);
        staffBeanModel.setMatchesList(null);
        staffBeanModel.setCaracteristicasCocinaDataModel(null);
        staffBeanModel.controlaPopUp("popupCaracteristicasCocina", Boolean.FALSE);
//        staffBeanModel.setMrPopupCaracteristicasCocinaStaff(!staffBeanModel.isMrPopupCaracteristicasCocinaStaff());
    }

    public DataModel getConveniosProveedor() {
        return staffBeanModel.getConveniosVigentePorProveedor();
    }

    public DataModel getTraerContratoStaff() {
//        staffBeanModel.buscarContratoVigente();
        if (staffBeanModel.getUltimoConvenioStaff() != null) {
            UtilLog4j.log.info(this, "Sg historial: " + getUltimoHistorialConvenioStaff().getConvenio().getCodigo());
        }
        return staffBeanModel.traerContratoStaff();
    }

    public void mostrarPopupVerHistorialConvenios(ActionEvent actionEvent) {
        staffBeanModel.getTodoHistorialConvenioStaff();
        staffBeanModel.setMrPopupVerHistorialConvenios(!staffBeanModel.isMrPopupVerHistorialConvenios());
    }

    public DataModel getTodoHistorialModelConvenios() {
//       UtilLog4j.log.info(this, "StaffBean.getTodoHistorialModelConvenios()");
        staffBeanModel.getTodoHistorialConvenioStaff();
        return staffBeanModel.getTodoHistorialConvenioModel();
    }

    public void abrirAdjuntoConvenioHistorial(ActionEvent event) {
        staffBeanModel.setSgHistorialConvenioSelecionado((SgHistorialConvenioStaff) staffBeanModel.getTodoHistorialConvenioModel().getRowData());
        staffBeanModel.setConvenioSeleccionado(staffBeanModel.getSgHistorialConvenioSelecionado().getConvenio());
    }

    public DataModel getTraerAdjuntoContrato() {
        try {
            return staffBeanModel.traerAdjuntoContrato();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerArchivoConvenioStafffHistorial() {
        return staffBeanModel.traerArchivoConvenioStaffHistorial();
    }

    public void createStaff(ActionEvent actionEvent) {
        try {
            if (staffBeanModel.getIdPais() > 0) {
                staffBeanModel.createStaff();
                FacesUtils.addInfoMessage("El Staff fué creado satisfactoriamente");
                ocultarPopupCrearStaff(actionEvent);
            } else {
                FacesUtils.addErrorMessage("formCrearStaff:pais", "País es requerido");
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al crear el Staff. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
    }

    public void addHabitacionToStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.addHabitacionToStaff();
        } catch (Exception e) {
            FacesUtils.addInfoMessage("Hubo un error al agregar la habitación al Staff. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            UtilLog4j.log.fatal(this, e.getMessage());

        }
        ocultarPopupAgregarHabitacionStaff(actionEvent);
    }

    public void addGimnasioToStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.addGimnasioToStaff();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al agregar el gimnasio al Staff. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
        ocultarPopupAgregarGimnasioStaff(actionEvent);
    }

    public void addCocinaToStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.addCocinaToStaff();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al agregar la cocina al Staff. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
        ocultarPopupAgregarCocinaStaff(actionEvent);
    }

    public void updateStaff(ActionEvent actionEvent) {
        try {
            if (staffBeanModel.getIdPais() > 0) {
                staffBeanModel.updateStaff();
                FacesUtils.addInfoMessage("El Staff fué actualizado satisfactoriamente");
                ocultarPopupModificarGeneralesStaff(actionEvent);
            } else {
                FacesUtils.addErrorMessage("formActualizarDatosGeneralesStaff:paisM", "País es requerido");
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al actualizar el Staff. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
    }

    public void updateHabitacionStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.updateHabitacionStaff();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al actualizar la Habitación. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
        ocultarPopupModificarHabitacionStaff(actionEvent);
    }

    public void updateGimnasioStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.updateGimnasioStaff();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al actualizar el Gimnasio. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
        ocultarPopupModificarGimnasioStaff(actionEvent);
    }

    public void updateCocinaStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.updateCocinaStaff();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al actualizar la Cocina. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
        ocultarPopupModificarCocinaStaff(actionEvent);
    }

    public void deleteStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.deleteStaff();
            FacesUtils.addInfoMessage("El Staff fué eliminado exitosamente");
            ocultarPopupEliminarStaff(actionEvent);
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            ocultarPopupEliminarStaff(actionEvent);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
            ocultarPopupEliminarStaff(actionEvent);
        }
    }

    public void deleteHabitacionStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.deleteHabitacionStaff();
        } catch (SIAException siae) {
            if (siae.getLiteral() != null && !siae.getLiteral().isEmpty()) {
                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(siae.getLiteral()));
            } else {
                FacesUtils.addErrorMessage(siae.getMessage());
            }
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            ocultarPopupEliminarHabitacionStaff(actionEvent);
        }
    }

    public void deleteGimnasioStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.deleteGimnasioStaff();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al eliminar el Gimnasio del Staff. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
        ocultarPopupEliminarGimnasioStaff(actionEvent);
    }

    public void deleteCocinaStaff(ActionEvent actionEvent) {
        try {
            staffBeanModel.deleteCocinaStaff();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al eliminar la Cocina del Staff. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }
        ocultarPopupEliminarCocinaStaff(actionEvent);
    }

    public void addConvenioToProveedorToStaff(ActionEvent actionEvent) throws Exception {
        staffBeanModel.setConvenioSeleccionado((Convenio) staffBeanModel.getConvenioStaff().getRowData());
        if (staffBeanModel.buscarRelacionConvenio().isEmpty()) {
            staffBeanModel.quitarContratoVigente();
            //Asigna el nuevo contrato
            staffBeanModel.setUltimoConvenioStaff(staffBeanModel.addContratoProveedor());
            //REcupera el contrato asignado
            staffBeanModel.traerContratoStaff();
            staffBeanModel.setConvenioSeleccionado(null);
            staffBeanModel.buscarContratoVigente();
            staffBeanModel.setMrPopupAgregarContrato(false);
        } else {
            FacesUtils.addErrorMessage("Ya existe el contrato asignado " + staffBeanModel.getConvenioSeleccionado().getCodigo() + ", favor de seleccionar otro");
        }
    }

    public void deleteRelacionHistorialConvenio(ActionEvent actionEvent) {
//       UtilLog4j.log.info(this, "StaffBean.deleteRelacionHistorialConvenio()");
        try {
            staffBeanModel.deleteRelacionConvenio();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            if (e.getMessage().equals("")) {
                FacesUtils.addInfoMessage("Hubo un error al eliminar el contrato. Por favor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            } else {
                FacesUtils.addInfoMessage(e.getMessage());
            }
        }

        //ocultar el popup
        ocultarPopupEliminarConvenio(actionEvent);
    }

//////////    public List<SelectItem> getListaProveedor() {
//////////        return staffBeanModel.getListaProveedor();
//////////    }
//////////
//////////    public void proveedorListener(ValueChangeEvent textChangeEvent) {
//////////        if (textChangeEvent.getComponent() instanceof SelectInputText) {
//////////            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
//////////            String cadenaDigitada = (String) textChangeEvent.getNewValue();
//////////            if (staffBeanModel.getListaProveedorBuscar() == null) {
//////////                staffBeanModel.setListaProveedorBuscar(staffBeanModel.traerProveedor());
//////////                staffBeanModel.setListaProveedor(regresaProveedorActivo(cadenaDigitada));
//////////            }
//////////            staffBeanModel.setListaProveedor(regresaProveedorActivo(cadenaDigitada));
//////////            setMensajeError("");
//////////            if (autoComplete.getSelectedItem() != null) {
//////////                Proveedor proveedorSel = (Proveedor) autoComplete.getSelectedItem().getValue();
//////////                staffBeanModel.setNombreProveedorSeleccionado(proveedorSel.getNombre());
//////////                staffBeanModel.getConveniosVigentePorProveedor();
//////////                if (staffBeanModel.getConvenioStaff().getRowCount() < 1) {
//////////                    setMensajeError("· No hay convenios para este proveedor..");
//////////                }
//////////            } else {
//////////                staffBeanModel.setConvenioStaff(null);
//////////            }
//////////        }
//////////    }
    public List<SelectItem> getListaProveedor() {
        return staffBeanModel.getListaProveedor();
    }

    public void proveedorListener(String textChangeEvent) {
        staffBeanModel.setListaProveedor(regresaProveedorActivo(textChangeEvent));
        staffBeanModel.setNombreProveedorSeleccionado(textChangeEvent);
    }

    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (Iterator it = staffBeanModel.getListaProveedorBuscar().iterator(); it.hasNext();) {
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
        staffBeanModel.setListaProveedor(listaProveedor);
    }
//Fiin de autocompletar

    public void abrirArchivoConvenio(javax.faces.event.ActionEvent event) throws IOException {
        staffBeanModel.setSgHistorialConvenioSelecionado((SgHistorialConvenioStaff) staffBeanModel.getHistorialConvenioModel().getRowData());
        UtilLog4j.log.fatal(this, "-----historial : " + staffBeanModel.getSgHistorialConvenioSelecionado().getConvenio().getNombre());
        staffBeanModel.buscarAdjuntoConvenio();
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().redirect(
                new StringBuilder().append("/ServiciosGenerales/AbrirArchivo?ZWZ2W=").append(getIdAdjunto()).append("&amp;ZWZ3W=").append(getUuid()).toString());
    }

    public SgHistorialConvenioStaff getUltimoHistorialConvenioStaff() {
        return staffBeanModel.getUltimoConvenioStaff();
    }

    public void abrirArchivoConvenioHistorial(javax.faces.event.ActionEvent event) throws IOException {
        staffBeanModel.setSgHistorialConvenioSelecionado((SgHistorialConvenioStaff) staffBeanModel.getTodoHistorialConvenioModel().getRowData());
        UtilLog4j.log.fatal(this, "-----historial : " + staffBeanModel.getSgHistorialConvenioSelecionado().getConvenio().getNombre());
        staffBeanModel.buscarAdjuntoConvenio();
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().redirect(
                new StringBuilder().append("/ServiciosGenerales/AbrirArchivo?ZWZ2W=").append(getIdAdjunto()).append("&amp;ZWZ3W=").append(getUuid()).toString());

    }

    public void mostrarPopupAbrirArchivo(ActionEvent actionEvent) {
//       UtilLog4j.log.info(this, "StaffBean.mostrarPopupAbrirArchivo");
        staffBeanModel.setSgHistorialConvenioSelecionado((SgHistorialConvenioStaff) staffBeanModel.getHistorialConvenioModel().getRowData());
        staffBeanModel.setConvenioSeleccionado(staffBeanModel.getSgHistorialConvenioSelecionado().getConvenio());
        staffBeanModel.setMrPopupAbrirArchivo(true);
    }

    public void ocultarPopupAbrirArchivo(ActionEvent actionEvent) {
//       UtilLog4j.log.info(this, "StaffBean.ocultarPopupAbrirArchivo");
        staffBeanModel.setMrPopupAbrirArchivo(false);
    }

    public void mostrarPopupAbrirArchivoTodoHistorial(ActionEvent actionEvent) {
//       UtilLog4j.log.info(this, "StaffBean.mostrarPopupAbrirArchivoHistorial");
        staffBeanModel.setSgHistorialConvenioSelecionado((SgHistorialConvenioStaff) staffBeanModel.getTodoHistorialConvenioModel().getRowData());
        staffBeanModel.setConvenioSeleccionado(staffBeanModel.getSgHistorialConvenioSelecionado().getConvenio());
        staffBeanModel.setMrPopupAbrirArchivoTodoHistorial(true);
    }

    public void ocultarPopupAbrirArchivoTodoHistorial(ActionEvent actionEvent) {
//       UtilLog4j.log.info(this, "StaffBean.ocultarPopupAbrirArchivoHistorial");
        staffBeanModel.setMrPopupAbrirArchivoTodoHistorial(false);
    }

    public List<SelectItem> getListaPais() {
        if (staffBeanModel.getStaff() != null) {
            return staffBeanModel.listaPais();
        }
        return null;
    }

    public void mostrarPopupCrearStaff(ActionEvent actionEvent) {
        //Dándole memoria a oficina y dirección para guardar los datos
        staffBeanModel.setStaff(new SgStaff());
        staffBeanModel.setDireccion(new SgDireccion());
        staffBeanModel.setMrPopupCrearStaff(!staffBeanModel.isMrPopupCrearStaff());
    }

    public void ocultarPopupCrearStaff(ActionEvent actionEvent) {
        //Quitándo memoria a Staff y Dirección
        staffBeanModel.setStaff(null);
        staffBeanModel.setDireccion(null);
        //Limpiando componentes
        clearComponent("formCrearStaff", "nombreStaff");
        clearComponent("formCrearStaff", "numeroStaff");
        clearComponent("formCrearStaff", "telefonoStaff");
        clearComponent("formCrearStaff", "pais");
        clearComponent("formCrearStaff", "estado");
        clearComponent("formCrearStaff", "municipio");
        clearComponent("formCrearStaff", "ciudad");
        clearComponent("formCrearStaff", "colonia");
        clearComponent("formCrearStaff", "calle");
        clearComponent("formCrearStaff", "numeroExterior");
        clearComponent("formCrearStaff", "numeroInterior");
        clearComponent("formCrearStaff", "piso");
        clearComponent("formCrearStaff", "codigoPostal");
        staffBeanModel.setMrPopupCrearStaff(!staffBeanModel.isMrPopupCrearStaff());
    }

    public void mostrarPopupModificarGeneralesStaff(ActionEvent actionEvent) {
        staffBeanModel.setMrPopupModificarGeneralesStaff(!staffBeanModel.isMrPopupModificarGeneralesStaff());
        staffBeanModel.setIdPais(staffBeanModel.getStaff().getSgDireccion().getSiPais().getId());
    }

    public void ocultarPopupModificarGeneralesStaff(ActionEvent actionEvent) {
        //Actualizar el Staff por si le había puesto algún otro valor a sus campos
        staffBeanModel.reloadStaff();
        //Limpiar componentes
        clearComponent("formActualizarDatosGeneralesStaff", "nombreStaff");
        clearComponent("formActualizarDatosGeneralesStaff", "numeroStaff");
        clearComponent("formActualizarDatosGeneralesStaff", "telefonoStaff");
        clearComponent("formActualizarDatosGeneralesStaff", "pais");
        clearComponent("formActualizarDatosGeneralesStaff", "estado");
        clearComponent("formActualizarDatosGeneralesStaff", "municipio");
        clearComponent("formActualizarDatosGeneralesStaff", "ciudad");
        clearComponent("formActualizarDatosGeneralesStaff", "colonia");
        clearComponent("formActualizarDatosGeneralesStaff", "calle");
        clearComponent("formActualizarDatosGeneralesStaff", "numeroExterior");
        clearComponent("formActualizarDatosGeneralesStaff", "numeroInterior");
        clearComponent("formActualizarDatosGeneralesStaff", "piso");
        clearComponent("formActualizarDatosGeneralesStaff", "codigoPostal");
        staffBeanModel.setMrPopupModificarGeneralesStaff(!staffBeanModel.isMrPopupModificarGeneralesStaff());
    }

    public void mostrarPopupEliminarStaff(ActionEvent actionEvent) {
        SgStaff staffSeleccionado = (SgStaff) staffBeanModel.getDataModel().getRowData();
        staffBeanModel.setStaff(staffSeleccionado);
        staffBeanModel.setMrPopupEliminarStaff(!staffBeanModel.isMrPopupEliminarStaff());
    }

    public void ocultarPopupEliminarStaff(ActionEvent actionEvent) {
        //Quitándo memoria a Staff
        staffBeanModel.setStaff(null);
        staffBeanModel.setMrPopupEliminarStaff(!staffBeanModel.isMrPopupEliminarStaff());
    }

    public void mostrarPopupAgregarHabitacionStaff(ActionEvent actionEvent) {
        //Dándo memoria a habitación
        staffBeanModel.setHabitacion(new SgStaffHabitacion());
        staffBeanModel.setMrPopupAgregarHabitacion(!staffBeanModel.isMrPopupAgregarHabitacion());
    }

    public void ocultarPopupAgregarHabitacionStaff(ActionEvent actionEvent) {
        //Quitándo memoria a Habitación
        staffBeanModel.setHabitacion(null);
        //Limpiando componentes
        clearComponent("popupAgregarHabitacion", "nombreHabitacion");
        clearComponent("popupAgregarHabitacion", "numeroHabitacion");
        staffBeanModel.setMrPopupAgregarHabitacion(!staffBeanModel.isMrPopupAgregarHabitacion());
    }

    public void mostrarPopupModificarHabitacionStaff(ActionEvent actionEvent) {
        SgStaffHabitacion habitacionSeleccionada = (SgStaffHabitacion) staffBeanModel.getHabitacionesDataModel().getRowData();
        staffBeanModel.setHabitacion(habitacionSeleccionada);
        staffBeanModel.setMrPopupModificarHabitacion(!staffBeanModel.isMrPopupModificarHabitacion());
    }

    public void ocultarPopupModificarHabitacionStaff(ActionEvent actionEvent) {
        //Quitándo memoria a Habitación
        staffBeanModel.setHabitacion(null);
        //Limpiando componente
        clearComponent("formActualizarHabitacionStaff", "nombreHabitacion");
        clearComponent("formActualizarHabitacionStaff", "numeroHabitacion");
        staffBeanModel.setMrPopupModificarHabitacion(!staffBeanModel.isMrPopupModificarHabitacion());
    }

    public void mostrarPopupEliminarHabitacionStaff(ActionEvent actionEvent) {
        SgStaffHabitacion habitacionSeleccionada = (SgStaffHabitacion) staffBeanModel.getHabitacionesDataModel().getRowData();
        staffBeanModel.setHabitacion(habitacionSeleccionada);
        staffBeanModel.setMrPopupEliminarHabitacion(!staffBeanModel.isMrPopupEliminarHabitacion());
    }

    public void ocultarPopupEliminarHabitacionStaff(ActionEvent actionEvent) {
        //Quitándo memoria a Habitación
        staffBeanModel.setHabitacion(null);
        staffBeanModel.setMrPopupEliminarHabitacion(!staffBeanModel.isMrPopupEliminarHabitacion());
    }

    public void mostrarPopupAgregarGimnasioStaff(ActionEvent actionEvent) {
        //Dándo memoria a Gimnasio
        staffBeanModel.setGimnasio(new SgGym());
        staffBeanModel.setMrPopupAgregarGimnasio(!staffBeanModel.isMrPopupAgregarGimnasio());
    }

    public void ocultarPopupAgregarGimnasioStaff(ActionEvent actionEvent) {
        //Quitándo memoria a Gimnasio
        staffBeanModel.setGimnasio(null);
        //Limpiando componente
        clearComponent("formCrearGimnasio", "nombreGimnasio");
        staffBeanModel.setMrPopupAgregarGimnasio(!staffBeanModel.isMrPopupAgregarGimnasio());
    }

    public void mostrarPopupModificarGimnasioStaff(ActionEvent actionEvent) {
        SgGym gimnasioSeleccionado = (SgGym) staffBeanModel.getGimnasiosDataModel().getRowData();
        staffBeanModel.setGimnasio(gimnasioSeleccionado);
        staffBeanModel.setMrPopupModificarGimnasio(!staffBeanModel.isMrPopupModificarGimnasio());
    }

    public void ocultarPopupModificarGimnasioStaff(ActionEvent actionEvent) {
        //Quitándo memoria a Gimnasio
        staffBeanModel.setGimnasio(null);
        //Limpiando componente
        clearComponent("formActualizarGimnasio", "nombreGimnasio");
        staffBeanModel.setMrPopupModificarGimnasio(!staffBeanModel.isMrPopupModificarGimnasio());
    }

    public void mostrarPopupEliminarGimnasioStaff(ActionEvent actionEvent) {
        SgGym gimnasioSeleccionado = (SgGym) staffBeanModel.getGimnasiosDataModel().getRowData();
        staffBeanModel.setGimnasio(gimnasioSeleccionado);
        staffBeanModel.setMrPopupEliminarGimnasio(!staffBeanModel.isMrPopupEliminarGimnasio());
    }

    public void ocultarPopupEliminarGimnasioStaff(ActionEvent actionEvent) {
        //Quitándo memoria Gimnasio
        staffBeanModel.setGimnasio(null);
        staffBeanModel.setMrPopupEliminarGimnasio(!staffBeanModel.isMrPopupEliminarGimnasio());
    }

    public void mostrarPopupAgregarCocinaStaff(ActionEvent actionEvent) {
        //Dándole memoria a Cocina
        staffBeanModel.setCocina(new SgCocina());
        staffBeanModel.setMrPopupAgregarCocina(!staffBeanModel.isMrPopupAgregarCocina());
    }

    public void ocultarPopupAgregarCocinaStaff(ActionEvent actionEvent) {
        //Quitándo memoria a Cocina
        staffBeanModel.setCocina(null);
        //Limpiando componente
        clearComponent("formPopupCrearCocina", "nombreCocina");
        staffBeanModel.setMrPopupAgregarCocina(!staffBeanModel.isMrPopupAgregarCocina());
    }

    public void mostrarPopupModificarCocinaStaff(ActionEvent actionEvent) {
        SgCocina cocinaSeleccionada = (SgCocina) staffBeanModel.getCocinasDataModel().getRowData();
        staffBeanModel.setCocina(cocinaSeleccionada);
        staffBeanModel.setMrPopupModificarCocina(!staffBeanModel.isMrPopupModificarCocina());
    }

    public void ocultarPopupModificarCocinaStaff(ActionEvent actionEvent) {
        staffBeanModel.setMrPopupModificarCocina(!staffBeanModel.isMrPopupModificarCocina());
        //Quitándo memoria a Cocina
        clearComponent("formPopupActualizarCocina", "nombreCocina");
        staffBeanModel.setCocina(null);
    }

    public void mostrarPopupEliminarCocinaStaff(ActionEvent actionEvent) {
        SgCocina cocinaSeleccionada = (SgCocina) staffBeanModel.getCocinasDataModel().getRowData();
        staffBeanModel.setCocina(cocinaSeleccionada);
        staffBeanModel.setMrPopupEliminarCocina(!staffBeanModel.isMrPopupEliminarCocina());
    }

    public void ocultarPopupEliminarCocinaStaff(ActionEvent actionEvent) {
        //Quitándo memoria Cocina
        staffBeanModel.setCocina(null);
        staffBeanModel.setMrPopupEliminarCocina(!staffBeanModel.isMrPopupEliminarCocina());
    }

    public void mostrarPopupDetalleHabitacionStaff(ActionEvent actionEvent) {
        //Dándole memoria a la Habitación seleccionada
        SgStaffHabitacion habitacionSeleccionada = (SgStaffHabitacion) staffBeanModel.getHabitacionesDataModel().getRowData();
        UtilLog4j.log.info(this, "Habitación seleccionada:" + habitacionSeleccionada);
        staffBeanModel.setHabitacion(habitacionSeleccionada);
        staffBeanModel.setMrPopupDetalleHabitacionStaff(!staffBeanModel.isMrPopupDetalleHabitacionStaff());
    }

    public void ocultarPopupDetalleHabitacionStaff(ActionEvent actionEvent) {
        //Quitándole memoria a la Habitación seleccionada
        staffBeanModel.setHabitacion(null);
        staffBeanModel.setCaracteristicasHabitacionDataModel(null);
        staffBeanModel.setMrPopupDetalleHabitacionStaff(!staffBeanModel.isMrPopupDetalleHabitacionStaff());
    }

    public void mostrarPopupDetalleGimnasioStaff(ActionEvent actionEvent) {
        //Dándole memoria al Gimnasio seleccionado
        SgGym gimnasioSeleccionado = (SgGym) staffBeanModel.getGimnasiosDataModel().getRowData();
        UtilLog4j.log.info(this, "Gimnasio seleccionado: " + gimnasioSeleccionado);
        staffBeanModel.setGimnasio(gimnasioSeleccionado);
        staffBeanModel.setMrPopupDetalleGimnasioStaff(!staffBeanModel.isMrPopupDetalleGimnasioStaff());
    }

    public void ocultarPopupDetalleGimnasioStaff(ActionEvent actionEvent) {
        //Quitándole memoria al Gimnasio seleccionado
        staffBeanModel.setGimnasio(null);
        staffBeanModel.setCaracteristicasGimnasioDataModel(null);
        staffBeanModel.setMrPopupDetalleGimnasioStaff(!staffBeanModel.isMrPopupDetalleGimnasioStaff());
    }

    public void mostrarPopupDetalleCocinaStaff(ActionEvent actionEvent) {
        //Dándole memoria a la Cocina seleccionada
        SgCocina cocinaSeleccionada = (SgCocina) staffBeanModel.getCocinasDataModel().getRowData();
        UtilLog4j.log.info(this, "Cocina seleccionado: " + cocinaSeleccionada);
        staffBeanModel.setCocina(cocinaSeleccionada);
        staffBeanModel.setCaracteristicasCocinaDataModel(null);
        staffBeanModel.setMrPopupDetalleCocinaStaff(!staffBeanModel.isMrPopupDetalleCocinaStaff());
    }

    public void ocultarPopupDetalleCocinaStaff(ActionEvent actionEvent) {
        //Quitándole memoria a la Cocina seleccionada
        staffBeanModel.setCocina(null);
        staffBeanModel.setCaracteristicasCocinaDataModel(null);
        staffBeanModel.setMrPopupDetalleCocinaStaff(!staffBeanModel.isMrPopupDetalleCocinaStaff());
    }

    public void mostrarPopupAgregarConvenioProveedor(ActionEvent actionEvent) {
        // staffBeanModel.(new SgHistorialConvenioStaff());
        staffBeanModel.setNombreProveedorSeleccionado("");
        staffBeanModel.setConvenioStaff(null);
        staffBeanModel.setMrPopupAgregarContrato(true);
    }

    public void ocultarPopupAgregarConvenioProveedor(ActionEvent actionEvent) {
        staffBeanModel.setMrPopupAgregarContrato(false);
    }

    public void mostrarPopupEliminarConvenio(ActionEvent actionEvent) {
        // staffBeanModel.setUltimoConvenioStaff((SgHistorialConvenioStaff) staffBeanModel.getConvenioStaffModel().getRowData());
        staffBeanModel.setMrPopupEliminarContrato(!staffBeanModel.isMrPopupEliminarContrato());
    }

    public void ocultarPopupEliminarConvenio(ActionEvent actionEvent) {
        staffBeanModel.setMrPopupEliminarContrato(!staffBeanModel.isMrPopupEliminarContrato());
    }

    public void ocultarPopupVerHistorialConvenios(ActionEvent actionEvent) {
        staffBeanModel.setMrPopupVerHistorialConvenios(false);
    }

    public int getIdAdjunto() {
        return staffBeanModel.getIdAdjunto();
    }

    public String getUuid() {
        return staffBeanModel.getUuid();
    }

    /**
     * @return the staff
     */
    public SgStaff getStaff() {
        return staffBeanModel.getStaff();
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(SgStaff staff) {
        staffBeanModel.setStaff(staff);
    }

    /**
     * @return the direccion
     */
    public SgDireccion getDireccion() {
        return staffBeanModel.getDireccion();
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(SgDireccion direccion) {
        staffBeanModel.setDireccion(direccion);
    }

    /**
     * @return the habitacion
     */
    public SgStaffHabitacion getHabitacion() {
        return staffBeanModel.getHabitacion();
    }

    /**
     * @param habitacion the habitacion to set
     */
    public void setHabitacion(SgStaffHabitacion habitacion) {
        staffBeanModel.setHabitacion(habitacion);
    }

    /**
     * @return the gimnasio
     */
    public SgGym getGimnasio() {
        return staffBeanModel.getGimnasio();
    }

    /**
     * @param gimnasio the gimnasio to set
     */
    public void setGimnasio(SgGym gimnasio) {
        staffBeanModel.setGimnasio(gimnasio);
    }

    /**
     * @return the caracteristicaAgregadaMensaje
     */
    public String getCaracteristicaAgregadaMensaje() {
        return staffBeanModel.getCaracteristicaAgregadaMensaje();
    }

    /**
     * @param caracteristicaAgregadaMensaje the caracteristicaAgregadaMensaje to
     * set
     */
    public void setCaracteristicaAgregadaMensaje(String caracteristicaAgregadaMensaje) {
        staffBeanModel.setCaracteristicaAgregadaMensaje(caracteristicaAgregadaMensaje);
    }

    /**
     * @return the caracteristica
     */
    public SgCaracteristica getCaracteristica() {
        return staffBeanModel.getCaracteristica();
    }

    /**
     * @param caracteristica the caracteristica to set
     */
    public void setCaracteristica(SgCaracteristica caracteristica) {
        staffBeanModel.setCaracteristica(caracteristica);
    }

    /**
     * @return the cocina
     */
    public SgCocina getCocina() {
        return staffBeanModel.getCocina();
    }

    /**
     * @param cocina the cocina to set
     */
    public void setCocina(SgCocina cocina) {
        staffBeanModel.setCocina(cocina);
    }

    public String getNombreProveedorSeleccionado() {
        return staffBeanModel.getNombreProveedorSeleccionado();
    }

    public void setNombreProveedorSeleccionado(String nombreProveedorSeleccionado) {
        staffBeanModel.setNombreProveedorSeleccionado(nombreProveedorSeleccionado);
    }

    public boolean isMrPopupAbrirArchivoTodoHistorial() {
        return staffBeanModel.isMrPopupAbrirArchivoTodoHistorial();
    }

    public boolean isMrPopupAbrirArchivo() {
        return staffBeanModel.isMrPopupAbrirArchivo();
    }

    public boolean isMrPopupVerHistorialConvenios() {
        return staffBeanModel.isMrPopupVerHistorialConvenios();
    }

    public boolean isMrPopupCrearStaff() {
        return staffBeanModel.isMrPopupCrearStaff();
    }

    public boolean isMrPopupModificarGeneralesStaff() {
        return staffBeanModel.isMrPopupModificarGeneralesStaff();
    }

    /**
     * @return the mrPopupEliminarStaff
     */
    public boolean isMrPopupEliminarStaff() {
        return staffBeanModel.isMrPopupEliminarStaff();
    }

    public boolean isMrPopupAgregarHabitacion() {
        return staffBeanModel.isMrPopupAgregarHabitacion();
    }

    /**
     * @return the mrPopupCreateContrato
     */
    public boolean isMrPopupAgregarContrato() {
        return staffBeanModel.isMrPopupAgregarContrato();
    }

    /**
     * @return the mrPopupModificarHabitacion
     */
    public boolean isMrPopupModificarHabitacion() {
        return staffBeanModel.isMrPopupModificarHabitacion();
    }

    /**
     * @return the mrPopupEliminarHabitacion
     */
    public boolean isMrPopupEliminarHabitacion() {
        return staffBeanModel.isMrPopupEliminarHabitacion();
    }

    /**
     * @return the mrPopUpAgregarGimnasio
     */
    public boolean isMrPopupAgregarGimnasio() {
        return staffBeanModel.isMrPopupAgregarGimnasio();
    }

    /**
     * @return the mrPopUpModificarGimnasio
     */
    public boolean isMrPopupModificarGimnasio() {
        return staffBeanModel.isMrPopupModificarGimnasio();
    }

    /**
     * @return the mrPopupEliminarGimnaio
     */
    public boolean isMrPopupEliminarGimnasio() {
        return staffBeanModel.isMrPopupEliminarGimnasio();
    }

    /**
     * @return the mrPopupAgregarCaracteristicaStaff
     */
    public boolean isMrPopupAgregarCaracteristicaStaff() {
        return staffBeanModel.isMrPopupAgregarCaracteristicaStaff();
    }

    /**
     * @return the mrPopupAgregarCocina
     */
    public boolean isMrPopupAgregarCocina() {
        return staffBeanModel.isMrPopupAgregarCocina();
    }

    /**
     * @return the mrPopupModificarCocina
     */
    public boolean isMrPopupModificarCocina() {
        return staffBeanModel.isMrPopupModificarCocina();
    }

    /**
     * @return the mrPopupEliminarCocina
     */
    public boolean isMrPopupEliminarCocina() {
        return staffBeanModel.isMrPopupEliminarCocina();
    }

    /**
     * @return the mrPopupDetalleHabitacionStaff
     */
    public boolean isMrPopupDetalleHabitacionStaff() {
        return staffBeanModel.isMrPopupDetalleHabitacionStaff();
    }

    /**
     * @return the mrPopupDetalleGimnasioStaff
     */
    public boolean isMrPopupDetalleGimnasioStaff() {
        return staffBeanModel.isMrPopupDetalleGimnasioStaff();
    }

    /**
     * @return the mrPopupDetalleCocinaStaff
     */
    public boolean isMrPopupDetalleCocinaStaff() {
        return staffBeanModel.isMrPopupDetalleCocinaStaff();
    }

    /**
     * @return the mrPopupCaracteristicasHabitacionStaff
     */
    public boolean isMrPopupCaracteristicasHabitacionStaff() {
        return staffBeanModel.isMrPopupCaracteristicasHabitacionStaff();
    }

    /**
     * @return the mrPopupCaracteristicasGimnasioStaff
     */
    public boolean isMrPopupCaracteristicasGimnasioStaff() {
        return staffBeanModel.isMrPopupCaracteristicasGimnasioStaff();
    }

    /**
     * @return the mrPopupCaracteristicasCocinaStaff
     */
    public boolean isMrPopupCaracteristicasCocinaStaff() {
        return staffBeanModel.isMrPopupCaracteristicasCocinaStaff();
    }

    public boolean isMrPopupEliminarConvenio() {
        return staffBeanModel.isMrPopupEliminarContrato();
    }

    /**
     * @return the mensajeError
     */
    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * @param mensajeError the mensajeError to set
     */
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    /**
     * @return the cantidadCaracteristica
     */
    public Integer getCantidadCaracteristica() {
        return staffBeanModel.getCantidadCaracteristica();
    }

    /**
     * @param cantidadCaracteristica the cantidadCaracteristica to set
     */
    public void setCantidadCaracteristica(Integer cantidadCaracteristica) {
        staffBeanModel.setCantidadCaracteristica(cantidadCaracteristica);
    }

    /**
     * @return the prefijo
     */
    public String getPrefijo() {
        return staffBeanModel.getPrefijo();
    }

    /**
     * @param prefijo the prefijo to set
     */
    public void setPrefijo(String prefijo) {
        staffBeanModel.setPrefijo(prefijo);
    }

    /**
     * @return the caracteristicaStaff
     */
    public SgCaracteristicaStaff getCaracteristicaStaff() {
        return staffBeanModel.getCaracteristicaStaff();
    }

    /**
     * @return the convenioSeleccionado
     */
    public Convenio getConvenioSeleccionado() {
        return staffBeanModel.getConvenioSeleccionado();
    }

    /**
     * @param convenioSeleccionado the convenioSeleccionado to set
     */
    public void setConvenioSeleccionado(Convenio convenioSeleccionado) {
        staffBeanModel.setConvenioSeleccionado(convenioSeleccionado);
    }

    /**
     * @param caracteristicaStaff the caracteristicaStaff to set =======
     */
    public void setCaracteristicaStaff(SgCaracteristicaStaff caracteristicaStaff) {
        staffBeanModel.setCaracteristicaStaff(caracteristicaStaff);
    }

    /**
     * @return the caracteristicas
     */
    public List<SelectItem> getCaracteristicas() {
        return staffBeanModel.getCaracteristicas();
    }

    /**
     * @param caracteristicas the caracteristicas to set
     */
    public void setCaracteristicas(List<SelectItem> caracteristicas) {
        staffBeanModel.setCaracteristicas(caracteristicas);
    }

    /**
     * @return the matchesList
     */
    public List<SelectItem> getMatchesList() {
        return staffBeanModel.getMatchesList();
    }

    /**
     * @param matchesList the matchesList to set
     */
    public void setMatchesList(List<SelectItem> matchesList) {
        staffBeanModel.setMatchesList(matchesList);
    }

    /**
     * @return the idPais
     */
    public int getIdPais() {
        return staffBeanModel.getIdPais();
    }

    /**
     * @param idPais the idPais to set
     */
    public void setIdPais(int idPais) {
        staffBeanModel.setIdPais(idPais);
    }

    /**
     * @param staffBeanModel the staffBeanModel to set
     */
    public void setStaffBeanModel(StaffBeanModel staffBeanModel) {
        this.staffBeanModel = staffBeanModel;
    }
}
