/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.checklist.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.excepciones.SIAException;
import sia.modelo.SgChecklist;
import sia.modelo.SgChecklistExtVehiculo;
import sia.modelo.SgChecklistLlantas;
import sia.modelo.SgKilometraje;
import sia.modelo.SgVehiculoChecklist;
import sia.sgl.checklist.bean.model.CheckListVehiculoModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author ihsa
 */
@Named(value = "checkListVehiculoBean")
@RequestScoped
public class CheckListVehiculoBean implements Serializable {

    /**
     * Creates a new instance of CheckListVehiculoBean
     */
    public CheckListVehiculoBean() {
    }

    @ManagedProperty(value = "#{checkListVehiculoModel}")
    private CheckListVehiculoModel checkListVehiculoModel;
    @Getter
    @Setter
    private UploadedFile fileInfo;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    public void clearVariables(ActionEvent actionEvent) {
        //Borrar variables
        checkListVehiculoModel.setDisabledChecklistInterior(false);
        checkListVehiculoModel.setDisabledChecklistExterior(false);
        checkListVehiculoModel.setChecklist(null);
        checkListVehiculoModel.setChecklistExtVehiculo(null);
        checkListVehiculoModel.setChecklistLlantas(null);
        checkListVehiculoModel.setChecklistVODataModel(null);
        checkListVehiculoModel.setKilometraje(null);
        checkListVehiculoModel.setSelectedIndex("0");
        checkListVehiculoModel.setCadena("");
    }

    public String goToChecklistVehiculo() {
        if (!checkListVehiculoModel.getMantenimientoNoTerminado()) {
            FacesUtils.addErrorMessage("No se puede crear un Checklist mientras el Vehículo esté en Mantenimiento");
            return "";
        } else {
            checkListVehiculoModel.beginConversationChecklistVehiculo();
            //Metiendo popups a Map de Popups
            checkListVehiculoModel.controlarPopFalse("popupObservacionToAdjunto");
            checkListVehiculoModel.controlarPopFalse("popupUpdateObservacionToAdjunto");
            checkListVehiculoModel.controlarPopFalse("popupUploadChecklistExterior");
            checkListVehiculoModel.setDisabledChecklistInterior(false);
            checkListVehiculoModel.setDisabledChecklistExterior(true);
            checkListVehiculoModel.setDisabledChecklistLlantas(true);
            return "/vistas/sgl/vehiculo/checklist/createChecklistVehiculo";
        }
    }

    public DataModel getChecklistVehiculoVODataModel() {
        try {
            return checkListVehiculoModel.getChecklistVODataModel();
        } catch (Exception siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            return null;
        }
    }

    public void createChecklisInteriorVehiculo(ActionEvent actionEvent) {
        UtilLog4j.log.info(this, "Kilometraje NUEVO es null? - " + (checkListVehiculoModel.getKilometraje() == null ? true : false));
        try {
            if (checkListVehiculoModel.getKilometraje() != null && checkListVehiculoModel.getKilometraje().getKilometraje() != null && checkListVehiculoModel.getKilometraje().getKilometraje() > 0) {
                if (checkListVehiculoModel.getKilometraje().getKilometraje() >= 0) {
                    if (checkListVehiculoModel.kilometrajeGreat()) {
                        System.out.println("1");
                        checkListVehiculoModel.createChecklistInteriorVehiculo();
                        System.out.println("2");
                        //checkListVehiculoModel.traerKilometrajeActualOld();
                        checkListVehiculoModel.setDisabledChecklistInterior(true);
                        System.out.println("3");
                        checkListVehiculoModel.setDisabledChecklistExterior(false);
                        checkListVehiculoModel.setKilometrajeActual(null);
                        checkListVehiculoModel.setChecklistVODataModel(null);
                        checkListVehiculoModel.setSelectedIndex("1");
                        FacesUtils.addInfoMessage("El Checklist de Interior fué creado satisfactoriamente");
                    } else {
                        FacesUtils.addErrorMessage("El Kilometraje actual debe ser mayor que el último registrado. Si no es así porfavor pide al Administrador de SGL que reinicie el Kilometraje");
                    }
                } else {
                    FacesUtils.addErrorMessage("Kilometraje debe ser un entero positivo");
                }
            } else {
                FacesUtils.addErrorMessage("Kilometraje es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public String createChecklistLlantas() {
        try {
            if (!checkListVehiculoModel.getChecklistLlantas().getDelanteraIzquierda().isEmpty()
                    && !checkListVehiculoModel.getChecklistLlantas().getDelanteraDerecha().isEmpty()
                    && !checkListVehiculoModel.getChecklistLlantas().getTraseraIzquierda().isEmpty()
                    && !checkListVehiculoModel.getChecklistLlantas().getTraseraDerecha().isEmpty()) {
                checkListVehiculoModel.createChecklistLlantasVehiculo();
                //Borrar variables
                checkListVehiculoModel.setDisabledChecklistInterior(true);
                checkListVehiculoModel.setDisabledChecklistExterior(true);
                checkListVehiculoModel.setDisabledChecklistLlantas(true);
                checkListVehiculoModel.setFlag(false);
                checkListVehiculoModel.setChecklist(null);
                checkListVehiculoModel.setChecklistExtVehiculo(null);
                checkListVehiculoModel.setChecklistLlantas(null);
                checkListVehiculoModel.setKilometraje(null);
                checkListVehiculoModel.setCadena("");
                checkListVehiculoModel.setSelectedIndex("0");
                FacesUtils.addInfoMessage("El Checklist de Llantas fué creado satisfactoriamente");
                return "/vistas/sgl/vehiculo/administrarVehiculo";
            } else {
                FacesUtils.addErrorMessage("Todos los porcentajes de uso de las llantas son requeridos, excepto el de refacción");
                return "";
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
            return "";
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            return "";
        }
    }

    public void uploadFileChecklistExteriorVehiculo(FileUploadEvent fileEvent) {
        try {
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
                    documentoAnexo.setRuta(getDirectoryChecklistExteriorVehiculo());
                    almacenDocumentos.guardarDocumento(documentoAnexo);

                    valid
                            = checkListVehiculoModel.guardarArchivoChecklistExteriorVehiculo(
                                    documentoAnexo.getNombreBase(),
                                    documentoAnexo.getRuta(),
                                    documentoAnexo.getTipoMime(),
                                    documentoAnexo.getTamanio()
                            );

                    if (valid) {
                        checkListVehiculoModel.setFlag(true);
                        closePopupUploadChecklistExterior(null);
                        FacesUtils.addInfoMessage("El Checklist Externo fué creado satisfactoriamente");
                    }
                } else {
                    FacesUtils.addInfoMessage(new StringBuilder()
                            .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                            .append(validadorNombreArchivo.getCaracteresNoValidos())
                            .toString());
                }

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

            if (!valid) {
                FacesUtils.addInfoMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
            }

            fileInfo.delete();

//		if (Files.exists(savedFile)) {
//		    FacesUtils.addInfoMessage("Upload Successfull...");
//		    try {
//			if (checkListVehiculoModel.guardarArchivoChecklistExteriorVehiculo(fileInfo.getFileName(), fileInfo.getContentType(), fileInfo.getSize())) {
//			    checkListVehiculoModel.setFlag(true);
//			    closePopupUploadChecklistExterior(null);
//			    FacesUtils.addInfoMessage("El Checklist Externo fué creado satisfactoriamente");
//			} else {
//			    removePhysicalFile(fileInfo);
//			}
//		    } catch (SIAException siae) {
//			FacesUtils.addErrorMessage(siae.getMessage());
//			UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
//			removePhysicalFile(fileInfo);
//		    } catch (Exception e) {
//			UtilLog4j.log.info(this, e.getMessage());
//			e.printStackTrace();
//			FacesUtils.addErrorMessage(new SIAException().getMessage());
//			removePhysicalFile(fileInfo);
//		    }
//		} else {
//		    UtilLog4j.log.info(this, "File no saved");
//		    FacesUtils.addErrorMessage(new SIAException().getMessage());
//		}
//	    }
        } catch (IOException ex) {
            Logger.getLogger(CheckListVehiculoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateAdjunto(ActionEvent actionEvent) {
        try {
            checkListVehiculoModel.updateAdjunto();
            FacesUtils.addInfoMessage("La Obeservación fue agregada satisfactoriamente");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            closePopupAddObservacionToChecklistExterior(actionEvent);
            closePopupUpdateObservacionToChecklistExterior(actionEvent);
        }
    }

    public void clearComponent(String nombreFormulario, String nombreComponente) {
        UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            LOGGER.error(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente, e);
        }
    }

    public void closePopupUploadChecklistExterior(ActionEvent actionEvent) {
        UtilLog4j.log.info(this, "closePopupUploadChecklistExterior");
        checkListVehiculoModel.controlarPopFalse("popupUploadChecklistExterior");
    }

    public void closePopupAddObservacionToChecklistExterior(ActionEvent actionEvent) {
        clearComponent("popupAgregarObservacion", "observacion");
        checkListVehiculoModel.controlarPopFalse("popupObservacionToAdjunto");
    }

    public void openPopupUpdateObservacionToChecklistExterior(ActionEvent actionEvent) {
        checkListVehiculoModel.controlarPopTrue("popupUpdateObservacionToAdjunto");
    }

    public void closePopupUpdateObservacionToChecklistExterior(ActionEvent actionEvent) {
        clearComponent("popupModificarObservacion", "observacion");
        checkListVehiculoModel.controlarPopFalse("popupUpdateObservacionToAdjunto");
    }

    public String getDirectoryChecklistExteriorVehiculo() {
        return checkListVehiculoModel.getDirectoryChecklistExteriorVehiculo();
    }

    public void deleteAdjuntoChecklistExterior(ActionEvent actionEvent) {
        try {
            checkListVehiculoModel.deleteAdjuntoChecklistExterior();
            checkListVehiculoModel.setFlag(false);
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void continueToSteap3(ActionEvent actionEvent) {
        if (checkListVehiculoModel.getChecklistExtVehiculo() != null && checkListVehiculoModel.getChecklistExtVehiculo().getSiAdjunto() != null) {
            checkListVehiculoModel.setChecklistLlantas(new SgChecklistLlantas());
            checkListVehiculoModel.getChecklistLlantas().setDelanteraDerecha("100");
            checkListVehiculoModel.getChecklistLlantas().setDelanteraIzquierda("100");
            checkListVehiculoModel.getChecklistLlantas().setTraseraDerecha("100");
            checkListVehiculoModel.getChecklistLlantas().setTraseraIzquierda("100");
            checkListVehiculoModel.getChecklistLlantas().setRefaccion("0");
            checkListVehiculoModel.setDisabledChecklistExterior(true);
            checkListVehiculoModel.setDisabledChecklistLlantas(false);
            checkListVehiculoModel.setSelectedIndex("2");
        } else {
            FacesUtils.addErrorMessage("Debes adjuntar un archivo en el Checklist de Exterior");
        }
    }

    /**
     * @param checkListVehiculoModel the checkListVehiculoModel to set
     */
    public void setCheckListVehiculoModel(CheckListVehiculoModel checkListVehiculoModel) {
        this.checkListVehiculoModel = checkListVehiculoModel;
    }

    /**
     * @return the checklist
     */
    public SgChecklist getChecklist() {
        return checkListVehiculoModel.getChecklist();
    }

    /**
     * @param checklist the checklist to set
     */
    public void setChecklist(SgChecklist checklist) {
        checkListVehiculoModel.setChecklist(checklist);
    }

    /**
     * @return the selectedIndex
     */
    public String getSelectedIndex() {
        return checkListVehiculoModel.getSelectedIndex();
    }

    /**
     * @param selectedIndex the selectedIndex to set
     */
    public void setSelectedIndex(String selectedIndex) {
        checkListVehiculoModel.setSelectedIndex(selectedIndex);
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
        return checkListVehiculoModel.getCadena();
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
        checkListVehiculoModel.setCadena(cadena);
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return checkListVehiculoModel.isFlag();
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        checkListVehiculoModel.setFlag(flag);
    }

    /**
     * @return the checklistVODataModel
     */
    public DataModel getChecklistVODataModel() {
        return checkListVehiculoModel.getChecklistVODataModel();
    }

    /**
     * @param checklistVODataModel the checklistVODataModel to set
     */
    public void setChecklistVODataModel(DataModel checklistVODataModel) {
        checkListVehiculoModel.setChecklistVODataModel(checklistVODataModel);
    }

    /**
     * @return the kilometraje
     */
    public SgKilometraje getKilometraje() {
        return checkListVehiculoModel.getKilometraje();
    }

    /**
     * @param kilometraje the kilometraje to set
     */
    public void setKilometraje(SgKilometraje kilometraje) {
        checkListVehiculoModel.setKilometraje(kilometraje);
    }

    /**
     * @return the kilometrajeActual
     */
    public SgKilometraje getKilometrajeActual() {
        return checkListVehiculoModel.getKilometrajeActual();
    }

    /**
     * @param kilometrajeActual the kilometrajeActual to set
     */
    public void setKilometrajeActual(SgKilometraje kilometrajeActual) {
        checkListVehiculoModel.setKilometrajeActual(kilometrajeActual);
    }

    /**
     * @return the disabledChecklistLlantas
     */
    public boolean isDisabledChecklistLlantas() {
        return checkListVehiculoModel.isDisabledChecklistLlantas();
    }

    /**
     * @param disabledChecklistLlantas the disabledChecklistLlantas to set
     */
    public void setDisabledChecklistLlantas(boolean disabledChecklistLlantas) {
        checkListVehiculoModel.setDisabledChecklistLlantas(disabledChecklistLlantas);
    }

    /**
     * @return the checklistLlantas
     */
    public SgChecklistLlantas getChecklistLlantas() {
        return checkListVehiculoModel.getChecklistLlantas();
    }

    /**
     * @param checklistLlantas the checklistLlantas to set
     */
    public void setChecklistLlantas(SgChecklistLlantas checklistLlantas) {
        checkListVehiculoModel.setChecklistLlantas(checklistLlantas);
    }

    /**
     * @return the disabledChecklistExterior
     */
    public boolean isDisabledChecklistExterior() {
        return checkListVehiculoModel.isDisabledChecklistExterior();
    }

    /**
     * @param disabledChecklistExterior the disabledChecklistExterior to set
     */
    public void setDisabledChecklistExterior(boolean disabledChecklistExterior) {
        checkListVehiculoModel.setDisabledChecklistExterior(disabledChecklistExterior);
    }

    public SgChecklistExtVehiculo getChecklistExtVehiculo() {
        return checkListVehiculoModel.getChecklistExtVehiculo();
    }

    /**
     * @param checklistExtVehiculo the checklistExtVehiculo to set
     */
    public void setChecklistExtVehiculo(SgChecklistExtVehiculo checklistExtVehiculo) {
        checkListVehiculoModel.setChecklistExtVehiculo(checklistExtVehiculo);
    }

    /**
     * @return the vehiculoChecklist
     */
    public SgVehiculoChecklist getVehiculoChecklist() {
        return checkListVehiculoModel.getVehiculoChecklist();
    }

    /**
     * @param vehiculoChecklist the vehiculoChecklist to set
     */
    public void setVehiculoChecklist(SgVehiculoChecklist vehiculoChecklist) {
        checkListVehiculoModel.setVehiculoChecklist(vehiculoChecklist);
    }

    public void openPopupAddObservacionToChecklistExterior(ActionEvent actionEvent) {
        checkListVehiculoModel.controlarPopTrue("popupObservacionToAdjunto");
    }

    /**
     * @return the disabledChecklistInterior
     */
    public boolean isDisabledChecklistInterior() {
        return checkListVehiculoModel.isDisabledChecklistInterior();
    }

    /**
     * @param disabledChecklistInterior the disabledChecklistInterior to set
     */
    public void setDisabledChecklistInterior(boolean disabledChecklistInterior) {
        checkListVehiculoModel.setDisabledChecklistInterior(disabledChecklistInterior);
    }

    public void openPopupUploadChecklistExterior(ActionEvent actionEvent) {
        checkListVehiculoModel.controlarPopTrue("popupUploadChecklistExterior");
    }

}
