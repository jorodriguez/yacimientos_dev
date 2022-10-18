/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.oficina.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.persistence.NonUniqueResultException;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.modelo.Convenio;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaComedor;
import sia.modelo.SgCaracteristicaOficina;
import sia.modelo.SgCaracteristicaSanitario;
import sia.modelo.SgComedor;
import sia.modelo.SgCtrlMantenimientoSanitario;
import sia.modelo.SgDireccion;
import sia.modelo.SgHistorialConvenioOficina;
import sia.modelo.SgOficina;
import sia.modelo.SgOficinaAnalista;
import sia.modelo.SgOficinaPlano;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioOficina;
import sia.modelo.SgSalaJunta;
import sia.modelo.SgSanitario;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SiAdjunto;
import sia.modelo.SiCiudad;
import sia.modelo.SiEstado;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.oficina.vo.SgOficinaAnalistaVo;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.sgl.oficina.bean.model.OficinaBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
@Named(value = "oficinaBean")
@RequestScoped
public class OficinaBean implements Serializable {

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Getter
    @Setter
    private UploadedFile fileInfo;
    private final static UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of oficinaBean
     */
    @ManagedProperty(value = "#{oficinaBeanModel}")
    private OficinaBeanModel oficinaBeanModel;
//    @Inject
//    private SoporteProveedor soporteProveedor;

//    @Inject
//    private Sesion sesion;
    public OficinaBean() {
    }

    public String iniciaAdministraOficina() throws Exception {
        String retVal = "/vistas/sgl/oficina/administraOficina";

        if (oficinaBeanModel.buscarOficinaActualEnSesion() != null) {
            oficinaBeanModel.iniciarConversacion();
            oficinaBeanModel.controlaPopUpFalso("popupCaracteristicasOficina");
            oficinaBeanModel.controlaPopUpFalso("popupCaracteristicasComedor");
            oficinaBeanModel.controlaPopUpFalso("popupCaracteristicasSalaJuntas");
            oficinaBeanModel.controlaPopUpFalso("popupCaracteristicasSanitario");
            oficinaBeanModel.controlaPopUpFalso("popupDetalleComedor");
            oficinaBeanModel.controlaPopUpFalso("popupDetalleSalaJuntas");
            oficinaBeanModel.controlaPopUpFalso("popupDetalleSanitario");
            oficinaBeanModel.controlaPopUpFalso("popupEliminarConvenio");
            oficinaBeanModel.buscarContratoVigente();
            oficinaBeanModel.setListaRegistro(null);
            oficinaBeanModel.setListaProveedorBuscar(oficinaBeanModel.traerProveedor());
            UtilLog4j.log.info(this, "Lista proveedor: " + oficinaBeanModel.getListaProveedorBuscar().size());
        } else {
            FacesUtils.addInfoMessage(Constantes.AVISO_NO_OFICINA);
            retVal = "/principal";
        }

        return retVal;
    }

    public String iniciaBitacora() throws Exception {
        String retVal = "/vistas/sgl/oficina/bitacoraSanitario";

        if (oficinaBeanModel.buscarOficinaActualEnSesion() != null) {
            oficinaBeanModel.iniciarConversacion();
        } else {
            FacesUtils.addInfoMessage(Constantes.AVISO_NO_OFICINA);
            retVal = "/principal";
        }

        return retVal;
    }

    public String pagoServicioStaff() throws Exception {
        if (oficinaBeanModel.buscarOficinaActualEnSesion() != null) {
            oficinaBeanModel.iniciarConversacion();
            oficinaBeanModel.setIdStaff(-1);
            oficinaBeanModel.setIdTipoEspecifico(-1);
            oficinaBeanModel.traerCasaStaff();
            oficinaBeanModel.setOpcionPagar("Staff");
            oficinaBeanModel.buscarTipoGeneral();
            oficinaBeanModel.setListaProveedorBuscar(oficinaBeanModel.traerProveedor());
            UtilLog4j.log.info(this, "Lista proveedor: " + oficinaBeanModel.getListaProveedorBuscar().size());
            return "/vistas/sgl/pago/pagoStaff";
        } else {
            FacesUtils.addInfoMessage(Constantes.AVISO_NO_OFICINA);
            return "/principal";
        }
    }

    public String pagoServicioVehiculo() throws Exception {
        if (oficinaBeanModel.buscarOficinaActualEnSesion() == null) {
            FacesUtils.addInfoMessage(Constantes.AVISO_NO_OFICINA);
            return "/principal";
        } else {
            oficinaBeanModel.iniciarConversacion();
            oficinaBeanModel.setIdTipoEspecifico(-1);
            oficinaBeanModel.setOpcionPagar("Vehículo");
            oficinaBeanModel.buscarTipoGeneral();
            oficinaBeanModel.setListaContrato(null);
            return "/vistas/sgl/pago/pagoVehiculo";
        }
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
            UtilLog4j.log.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    public void opcionSeleccionadaListener(ValueChangeEvent valueChangeEvent) {
        oficinaBeanModel.setOpcionSeleccionada((Integer) valueChangeEvent.getNewValue());
        oficinaBeanModel.traerOficinaRegistro();

    }

    public void siPaisListener(ValueChangeEvent valueChangeEvent) {
        int value = (Integer) valueChangeEvent.getNewValue();
        setIdPais(value);
        if (value < 0) {
            setListaEstado(Collections.EMPTY_LIST);
            setListaCiudad(Collections.EMPTY_LIST);
            setIdEstado(-1);
            setIdCiudad(-1);
        } else {
            setListaEstado(getAllSiEstadoSelectItem());
        }
    }

    public void siEstadoListener(ValueChangeEvent valueChangeEvent) {
        int value = (Integer) valueChangeEvent.getNewValue();
        setIdEstado(value);
        if (value < 0) {
            setListaCiudad(Collections.EMPTY_LIST);
            setIdEstado(-1);
        } else {
            setListaCiudad(getAllSiCiudadSelectItem());
        }
    }

    public void openPopupCreateSgOficina(ActionEvent actionEvent) {
        oficinaBeanModel.controlaPopUpTrue("popupCreateSgOficina");
//        sesion.getControladorPopups().put("", Boolean.TRUE);
        setListaPais(getAllSiPaisSelectItem());
    }

    public void closePopupCreateSgOficina(ActionEvent actionEvent) {
        this.oficinaBeanModel.clearVariables();
        setOpcionSeleccionada(1);
        clearComponent("formPopupCreateSgOficina", "nombre");
        clearComponent("formPopupCreateSgOficina", "telefono");
        clearComponent("formPopupCreateSgOficina", "analista");
        clearComponent("formPopupCreateSgOficina", "municipio");
        clearComponent("formPopupCreateSgOficina", "colonia");
        clearComponent("formPopupCreateSgOficina", "calle");
        clearComponent("formPopupCreateSgOficina", "numExterior");
        clearComponent("formPopupCreateSgOficina", "numInterior");
        clearComponent("formPopupCreateSgOficina", "numPiso");
        clearComponent("formPopupCreateSgOficina", "codigoPostal");
        clearComponent("formPopupCreateSgOficina", "longitudID");
        clearComponent("formPopupCreateSgOficina", "latitudID");
        oficinaBeanModel.controlaPopUpFalso("popupCreateSgOficina");
//        sesion.getControladorPopups().put("popupCreateSgOficina", Boolean.FALSE);
    }

    public void openPopupUpdateSgOficina(ActionEvent actionEvent) {
        oficinaBeanModel.setOficinaVO((OficinaVO) oficinaBeanModel.getMapaLista().get("oficina").getRowData());

        oficinaBeanModel.setNombre(getOficinaVO().getNombre());
        oficinaBeanModel.setTelefono(getOficinaVO().getNumeroTelefono());
        oficinaBeanModel.setMunicipio(getOficinaVO().getMunicipio());
        oficinaBeanModel.setColonia(getOficinaVO().getColonia());
        oficinaBeanModel.setCalle(getOficinaVO().getCalle());
        oficinaBeanModel.setNumExterior(getOficinaVO().getNumeroExterior());
        oficinaBeanModel.setNumInterior(getOficinaVO().getNumeroInterior());
        oficinaBeanModel.setNumPiso(getOficinaVO().getNumeroPiso());
        oficinaBeanModel.setCodigoPostal(getOficinaVO().getCodigoPostal());
        oficinaBeanModel.setLatitud(getOficinaVO().getLatitud());
        oficinaBeanModel.setLongitud(getOficinaVO().getLongitud());
        oficinaBeanModel.setIdPais(getOficinaVO().getIdSiPais());
        oficinaBeanModel.setIdEstado(getOficinaVO().getIdSiEstado());
        oficinaBeanModel.setIdCiudad(getOficinaVO().getIdSiCiudad());
        oficinaBeanModel.setListaPais(getAllSiPaisSelectItem());
        oficinaBeanModel.setListaEstado(getAllSiEstadoSelectItem());
        oficinaBeanModel.setListaCiudad(getAllSiCiudadSelectItem());
        oficinaBeanModel.controlaPopUpTrue("popupUpdateSgOficina");
    }

    public void closePopupUpdateSgOficina(ActionEvent actionEvent) {
        this.oficinaBeanModel.clearVariables();
        oficinaBeanModel.setOpcionSeleccionada(1);
        clearComponent("formPopupUpdateSgOficina", "nombre");
        clearComponent("formPopupUpdateSgOficina", "telefono");
        clearComponent("formPopupUpdateSgOficina", "municipio");
        clearComponent("formPopupUpdateSgOficina", "colonia");
        clearComponent("formPopupUpdateSgOficina", "calle");
        clearComponent("formPopupUpdateSgOficina", "numExterior");
        clearComponent("formPopupUpdateSgOficina", "numInterior");
        clearComponent("formPopupUpdateSgOficina", "numPiso");
        clearComponent("formPopupUpdateSgOficina", "codigoPostal");
        clearComponent("formPopupUpdateSgOficina", "longitudID");
        clearComponent("formPopupUpdateSgOficina", "latitudID");
        oficinaBeanModel.controlaPopUpFalso("popupUpdateSgOficina");
        //     sesion.getControladorPopups().put("popupUpdateSgOficina", Boolean.FALSE);
    }

    public void openPoupAsignarAnalistaOficina(ActionEvent actionEvent) {
        oficinaBeanModel.setOficinaVO((OficinaVO) getDataModelAnlistas().getRowData());
        oficinaBeanModel.setDataModelAux(new ListDataModel(this.oficinaBeanModel.getAllSgOficinaAnalistaBySgOficina(getOficinaVO().getId())));
        oficinaBeanModel.controlaPopUpTrue("popupAsignarAnalistaOficina");
        //sesion.getControladorPopups().put("popupAsignarAnalistaOficina", Boolean.TRUE);
    }

    public void closePopupAsignarAnalistaOficina(ActionEvent actionEvent) {
        oficinaBeanModel.setDataModelAux(null);
        oficinaBeanModel.setOficinaVO(null);
        oficinaBeanModel.controlaPopUpFalso("popupAsignarAnalistaOficina");
        //sesion.getControladorPopups().put("popupAsignarAnalistaOficina", Boolean.FALSE);
    }

    public void vistoBueno(ActionEvent event) {
        OficinaVO vo = (OficinaVO) oficinaBeanModel.getMapaLista().get("vistoBueno").getRowData();
//        oficinaBeanModel.setSgOficina((SgOficina) oficinaBeanModel.getListaVistoBueno().getRowData());
        try {
            oficinaBeanModel.vistoBueno(vo.getId());
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.sgOficina.mensaje.info.vistoBueno"));
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

//    public List<SelectItem> getListaPais() {
//        if (oficinaBeanModel.getSgOficinaAnalista() != null || oficinaBeanModel.getSgOficina() != null) {
//            return oficinaBeanModel.listaPais();
//        }
//        return null;
//    }
    public List<SelectItem> getAllSiPaisSelectItem() {
        List<SelectItem> listSelectItem = null;
        List<SiPais> list = this.oficinaBeanModel.getAllSiPais();
        if (!list.isEmpty()) {
            listSelectItem = new ArrayList<SelectItem>();
            for (SiPais siPais : list) {
                listSelectItem.add(new SelectItem(siPais.getId(), siPais.getNombre()));
            }
        }
        return listSelectItem;
    }

    public List<SelectItem> getAllSiEstadoSelectItem() {
        List<SelectItem> listSelectItem = null;
        List<SiEstado> list = this.oficinaBeanModel.getAllSiEstado(getIdPais());
        if (!list.isEmpty()) {
            listSelectItem = new ArrayList<SelectItem>();
            for (SiEstado siEstado : list) {
                listSelectItem.add(new SelectItem(siEstado.getId(), siEstado.getNombre()));
            }
        }
        return listSelectItem;
    }

    public List<SelectItem> getAllSiCiudadSelectItem() {
        List<SelectItem> listSelectItem = null;
        List<SiCiudad> list = this.oficinaBeanModel.getAllSiCiudad(getIdEstado());
        if (!list.isEmpty()) {
            listSelectItem = new ArrayList<SelectItem>();
            for (SiCiudad siCiudad : list) {
                listSelectItem.add(new SelectItem(siCiudad.getId(), siCiudad.getNombre()));
            }
        }
        return listSelectItem;
    }

    public void guardarOficina(ActionEvent event) {
        int errors = 0;
        if (!oficinaBeanModel.getUser().isEmpty()) {
            if (oficinaBeanModel.existeAnalista()) {
                if (oficinaBeanModel.getIdPais() == -1) {
                    errors++;
                    FacesUtils.addErrorMessage("formAgregarOficina:pais", "El país es requerido");
                }
            } else {
                errors++;
                FacesUtils.addErrorMessage("formAgregarOficina:userSelect", "Analista inexistente");
            }
        } else {
            errors++;
            FacesUtils.addErrorMessage("formAgregarOficina:userSelect", "Analista es requerido");
        }

        if (errors == 0) {
            try {
                oficinaBeanModel.guardarOficina();
            } catch (SIAException siae) {
                FacesUtils.addErrorMessage(siae.getMessage());
                UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            } catch (Exception e) {
                UtilLog4j.log.fatal(e);
                FacesUtils.addErrorMessage(new SIAException().getMessage());
            } finally {
                oficinaBeanModel.setUsuario(null);
                oficinaBeanModel.setUser(null);
                oficinaBeanModel.setIdPais(-1);
                cerrarPop(event);
            }
        }
    }

    public void saveSgOficina(ActionEvent actionEvent) {
        int errors = 0;

        if (getNombre().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:nombre",
                    FacesUtils.getKeyResourceBundle("sgl.generales.nombre")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getUser().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:analista",
                    FacesUtils.getKeyResourceBundle("sgl.analista")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        } else if (!oficinaBeanModel.existeAnalista()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:analista",
                    FacesUtils.getKeyResourceBundle("sgl.analista")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.noExiste"));
        }
        if (getIdPais() < 0) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:pais",
                    FacesUtils.getKeyResourceBundle("sia.siPais")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getIdEstado() < 0) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:estado",
                    FacesUtils.getKeyResourceBundle("sia.siEstado")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getIdCiudad() < 0) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:ciudad",
                    FacesUtils.getKeyResourceBundle("sia.siCiudad")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getMunicipio().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:municipio",
                    FacesUtils.getKeyResourceBundle("sgl.direccion.municipio")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getCalle().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:calle",
                    FacesUtils.getKeyResourceBundle("sgl.direccion.calle")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getNumExterior().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:numExterior",
                    FacesUtils.getKeyResourceBundle("sgl.direccion.numeroExterior")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getCodigoPostal().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:codigoPostal",
                    FacesUtils.getKeyResourceBundle("sgl.direccion.codigoPostal")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }

        if (getLongitud().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:longitudID",
                    new StringBuilder().append("Longitud").append(" ")
                            .append(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido")).toString());
        }

        if (getLatitud().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:latitudID",
                    new StringBuilder().append("Latitud").append(" ")
                            .append(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido")).toString());
        }

        if (errors == 0) {
            try {
                OficinaVO vo = new OficinaVO();
                vo.setNombre(getNombre());
                vo.setNumeroTelefono(getTelefono());
                vo.setMunicipio(getMunicipio());
                vo.setColonia(getColonia());
                vo.setCalle(getCalle());
                vo.setNumeroExterior(getNumExterior());
                vo.setNumeroInterior(getNumInterior());
                vo.setNumeroPiso(getNumPiso());
                vo.setCodigoPostal(getCodigoPostal());
                vo.setIdSiPais(getIdPais());
                vo.setIdSiEstado(getIdEstado());
                vo.setIdSiCiudad(getIdCiudad());
                vo.setLatitud(getLatitud());
                vo.setLongitud(getLongitud());
                this.oficinaBeanModel.saveSgOficina(vo, getUser());
                oficinaBeanModel.setOpcionSeleccionada(Constantes.DOS);
                oficinaBeanModel.traerOficinaRegistro();
                FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sistema.oficina")
                        + " "
                        + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
                closePopupCreateSgOficina(actionEvent);
            } catch (ExistingItemException eie) {
                FacesUtils.addErrorMessage("formPopupCreateSgOficina:msgsPopupCreateSgOficina",
                        FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + eie.getNombreElemento());
            } catch (Exception e) {
                closePopupCreateSgOficina(actionEvent);
                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.guardar"));
                UtilLog4j.log.fatal(this, e);
            }
        }
    }

    public void updateSgOficina(ActionEvent actionEvent) {
        int errors = 0;

        if (getNombre().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupUpdateSgOficina:nombre",
                    FacesUtils.getKeyResourceBundle("sgl.generales.nombre")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getIdPais() < 0) {
            errors++;
            FacesUtils.addErrorMessage("formPopupUpdateSgOficina:pais",
                    FacesUtils.getKeyResourceBundle("sia.siPais")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getIdEstado() < 0) {
            errors++;
            FacesUtils.addErrorMessage("formPopupUpdateSgOficina:estado",
                    FacesUtils.getKeyResourceBundle("sia.siEstado")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getIdCiudad() < 0) {
            errors++;
            FacesUtils.addErrorMessage("formPopupUpdateSgOficina:ciudad",
                    FacesUtils.getKeyResourceBundle("sia.siCiudad")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getMunicipio().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupUpdateSgOficina:municipio",
                    FacesUtils.getKeyResourceBundle("sgl.direccion.municipio")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getCalle().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupUpdateSgOficina:calle",
                    FacesUtils.getKeyResourceBundle("sgl.direccion.calle")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getNumExterior().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupUpdateSgOficina:numExterior",
                    FacesUtils.getKeyResourceBundle("sgl.direccion.numeroExterior")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getCodigoPostal().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupUpdateSgOficina:codigoPostal",
                    FacesUtils.getKeyResourceBundle("sgl.direccion.codigoPostal")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        }
        if (getLongitud().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:longitudID",
                    new StringBuilder().append("Longitud").append(" ")
                            .append(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido")).toString());
        }
        if (getLatitud().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupCreateSgOficina:latitudID",
                    new StringBuilder().append("Latitud").append(" ")
                            .append(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido")).toString());
        }

        if (errors == 0) {
            try {
                OficinaVO vo = new OficinaVO();
                vo.setNombre(getNombre());
                vo.setNumeroTelefono(getTelefono());
                vo.setMunicipio(getMunicipio());
                vo.setColonia(getColonia());
                vo.setCalle(getCalle());
                vo.setNumeroExterior(getNumExterior());
                vo.setNumeroInterior(getNumInterior());
                vo.setNumeroPiso(getNumPiso());
                vo.setCodigoPostal(getCodigoPostal());
                vo.setIdSiPais(getIdPais());
                vo.setIdSiEstado(getIdEstado());
                vo.setIdSiCiudad(getIdCiudad());
                vo.setLatitud(getLatitud());
                vo.setLongitud(getLongitud());
                this.oficinaBeanModel.updateSgOficina(getOficinaVO().getId(), vo);
                oficinaBeanModel.traerOficinaRegistro();
                FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sistema.oficina")
                        + " "
                        + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
                closePopupUpdateSgOficina(actionEvent);
            } catch (ExistingItemException eie) {
                FacesUtils.addErrorMessage("formPopupUpdateSgOficina:msgsPopupUpdateSgOficina",
                        FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + eie.getNombreElemento());
                UtilLog4j.log.fatal(this, eie);
            } catch (Exception e) {
                closePopupUpdateSgOficina(actionEvent);
                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.actualizar"));
                UtilLog4j.log.fatal(this, e);
            }
        }
    }

    public void deleteSgOficina(ActionEvent actionEvent) {
        OficinaVO oficinaVO = (OficinaVO) oficinaBeanModel.getMapaLista().get("oficina").getRowData();
        try {
            this.oficinaBeanModel.deleteSgOficina(oficinaVO.getId());
            oficinaBeanModel.traerOficinaRegistro();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sistema.oficina")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
        } catch (ItemUsedBySystemException iuse) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(iuse.getLiteral()));
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void eliminarOficina(ActionEvent event) {
        oficinaBeanModel.setSgOficinaAnalista((SgOficinaAnalista) oficinaBeanModel.getListaRegistro().getRowData());
        try {
            oficinaBeanModel.eliminarOficina();
            oficinaBeanModel.traerOficinaRegistro();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void saveSgOficinaAnalista(ActionEvent actionEvent) {
        int errors = 0;
        if (getUser().isEmpty()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupAsignarAnalistaOficina:msgsPopupAsignarAnalistaOficina",
                    FacesUtils.getKeyResourceBundle("sgl.analista")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        } else if (!oficinaBeanModel.existeAnalista()) {
            errors++;
            FacesUtils.addErrorMessage("formPopupAsignarAnalistaOficina:msgsPopupAsignarAnalistaOficina",
                    FacesUtils.getKeyResourceBundle("sgl.analista")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.noExiste"));
        }

        try {
            if (errors == 0) {
                this.oficinaBeanModel.saveSgOficinaAnalista(getOficinaVO().getId(), getUser());
                FacesUtils.addInfoMessage("formPopupAsignarAnalistaOficina:msgsPopupAsignarAnalistaOficina",
                        (FacesUtils.getKeyResourceBundle("sgl.analista")
                        + " "
                        + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.agregacionSatisfactoria")));
                setDataModelAux(new ListDataModel(this.oficinaBeanModel.getAllSgOficinaAnalistaBySgOficina(getOficinaVO().getId())));
                clearComponent("formPopupAsignarAnalistaOficina", "analista");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("formPopupAsignarAnalistaOficina:msgsPopupAsignarAnalistaOficina", new SIAException().getMessage());
        }
    }

    public void marcarPrincipal(ActionEvent event) {
        SgOficinaAnalistaVo sgOficinaAnalistaVo = (SgOficinaAnalistaVo) getDataModelAux().getRowData();
        oficinaBeanModel.marcarPrincipal(sgOficinaAnalistaVo.getId(), sgOficinaAnalistaVo.getIdSgOficina());

    }

    public void deleteSgOficinaAnalista(ActionEvent actionEvent) {
        SgOficinaAnalistaVo sgOficinaAnalistaVo = (SgOficinaAnalistaVo) getDataModelAux().getRowData();

        try {
            this.oficinaBeanModel.deleteSgOficinaAnalista(sgOficinaAnalistaVo.getId());
            FacesUtils.addInfoMessage("formPopupAsignarAnalistaOficina:msgsPopupAsignarAnalistaOficina",
                    (FacesUtils.getKeyResourceBundle("sgl.analista")
                    + " "
                    + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria")));
            setDataModelAux(new ListDataModel(this.oficinaBeanModel.getAllSgOficinaAnalistaBySgOficina(getOficinaVO().getId())));
            clearComponent("formPopupAsignarAnalistaOficina", "analista");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("formPopupAsignarAnalistaOficina:msgsPopupAsignarAnalistaOficina", new SIAException().getMessage());
        }
    }

    public void modificarOficina(ActionEvent event) {
        oficinaBeanModel.setSgOficinaAnalista((SgOficinaAnalista) oficinaBeanModel.getListaRegistro().getRowData());
        oficinaBeanModel.setUser(oficinaBeanModel.getSgOficinaAnalista().getAnalista().getNombre());
        oficinaBeanModel.setUsuario(oficinaBeanModel.getSgOficinaAnalista().getAnalista());
        oficinaBeanModel.setIdPais(oficinaBeanModel.getSgOficinaAnalista().getSgOficina().getSgDireccion().getSiPais().getId());
        oficinaBeanModel.setModificarPopUp(true);
    }

    public void completarModificacionOficina(ActionEvent event) {
        UtilLog4j.log.info(this, "País: " + oficinaBeanModel.getSgOficinaAnalista().getSgOficina().getSgDireccion().getSiPais().getNombre());

        int errors = 0;
        if (!oficinaBeanModel.getUser().isEmpty()) {
            if (oficinaBeanModel.existeAnalista()) {
                if (oficinaBeanModel.getIdPais() < 1) {
                    errors++;
                    FacesUtils.addErrorMessage("formModificarOficina:paisM", "País es requerido");
                }
            } else {
                errors++;
                FacesUtils.addErrorMessage("formModificarOficina:userSelect", "Analista inexistente");
            }
        } else {
            errors++;
            FacesUtils.addErrorMessage("formModificarOficina:userSelect", "Analista es requerido");
        }

        if (errors == 0) {
            try {
                oficinaBeanModel.completarModificacionOficina();
            } catch (SIAException siae) {
                FacesUtils.addErrorMessage(siae.getMessage());
                UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e);
                FacesUtils.addErrorMessage(new SIAException().getMessage());
            } finally {
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgOficinaAnalista(null);
                oficinaBeanModel.setUser(null);
                oficinaBeanModel.setUsuario(null);
                oficinaBeanModel.setIdPais(-1);
                oficinaBeanModel.traerOficinaRegistro();
            }
        }
    }

    public void cerrarPop(ActionEvent event) {
//        UtilLog4j.log.fatal(this, "Pop verdetalle " + isVerDetallePop());
        oficinaBeanModel.setPopUp(false);
        oficinaBeanModel.setCrearPopUp(false);
        oficinaBeanModel.setModificarPopUp(false);
        oficinaBeanModel.setMostrarPanel(false);
        oficinaBeanModel.setVerDetallePop(false);
        oficinaBeanModel.setEliminarPop(false);
        oficinaBeanModel.setSubirArchivo(false);
        oficinaBeanModel.setCambiarPopUp(false);
        oficinaBeanModel.setAbrirArchivHistorial(false);
        oficinaBeanModel.setSgDireccion(null);
        oficinaBeanModel.setSgComedor(null);
        oficinaBeanModel.setSgDireccion(null);
        oficinaBeanModel.setSgSalaJunta(null);
        oficinaBeanModel.setSgSanitario(null);
        oficinaBeanModel.setSgCaracteristica(null);
        oficinaBeanModel.setUsuario(null);
    }

    public void cerrarPopAgregarContrato(ActionEvent event) {
        oficinaBeanModel.setAgregarPopContrato(false);
    }

    public void cerrarCrearOficinaPop(ActionEvent event) {
        oficinaBeanModel.setCrearPopUp(false);
        oficinaBeanModel.setSgDireccion(null);
        oficinaBeanModel.setUsuario(null);
        oficinaBeanModel.setUser(null);
        oficinaBeanModel.setSgOficinaAnalista(null);
        oficinaBeanModel.setIdPais(-1);
    }

    public void cerrarModificarOficinaPop(ActionEvent event) {
        oficinaBeanModel.setModificarPopUp(false);
        oficinaBeanModel.setSgOficina(null);
        oficinaBeanModel.setSgDireccion(null);
        oficinaBeanModel.setSgOficinaAnalista(null);
        oficinaBeanModel.setIdPais(-1);
    }

    public void usuarioListener(String textChangeEvent) {
        oficinaBeanModel.setUser(textChangeEvent);

        setListaUsuario(oficinaBeanModel.getListaUsuario(textChangeEvent));

//                oficinaBeanModel.setUsuario((Usuario) autoComplete.getSelectedItem().getValue());
        oficinaBeanModel.setUser(textChangeEvent);
    }

    public void agregarComedor(ActionEvent event) {
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setCrearPopUp(true);
        oficinaBeanModel.setSgComedor(new SgComedor());
    }

    public void guardarOficinaComedor(ActionEvent event) {
        if (oficinaBeanModel.getSgComedor().getNombre().isEmpty()) {
            FacesUtils.addErrorMessage("formAgregarComedor:textN", "Nombre es requerido");
        } else {
            oficinaBeanModel.guardarOficinaComedor();
            oficinaBeanModel.setPopUp(false);
            oficinaBeanModel.setCrearPopUp(false);
            oficinaBeanModel.setSgComedor(null);
        }
    }

    public void modificarComedorOficina(ActionEvent event) {
        oficinaBeanModel.setSgComedor((SgComedor) oficinaBeanModel.getMapaLista().get("comedorOficina").getRowData());
        oficinaBeanModel.setPrincipal(oficinaBeanModel.getSgComedor().getNombre());
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setModificarPopUp(true);
    }

    public void eliminarComedorOficina(ActionEvent event) {
        oficinaBeanModel.setSgComedor((SgComedor) oficinaBeanModel.getMapaLista().get("comedorOficina").getRowData());
        oficinaBeanModel.setEliminarPop(true);
    }

    public void completarEliminarComedorOficina(ActionEvent event) {
        try {
            oficinaBeanModel.eliminarComedorOficina();
            cerrarPopEliminar(event);
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            cerrarPopEliminar(event);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
            cerrarPopEliminar(event);
        }
    }

    public void cerrarPopEliminar(ActionEvent event) {
        oficinaBeanModel.setEliminarPop(false);
        oficinaBeanModel.setSgComedor(null);
        oficinaBeanModel.setSgCaracteristicaOficina(null);
        oficinaBeanModel.setSgCaracteristica(null);
        oficinaBeanModel.setSgSalaJunta(null);
        oficinaBeanModel.setSgSanitario(null);
        oficinaBeanModel.setSgOficinaPlano(null);
    }

    public void verDetalle(ActionEvent event) {
        oficinaBeanModel.setSgComedor((SgComedor) oficinaBeanModel.getMapaLista().get("comedorOficina").getRowData());
        oficinaBeanModel.setVerDetallePop(true);
    }

    public void completarModificacioOficinaComedor(ActionEvent event) {
        if (oficinaBeanModel.getSgComedor().getNombre().isEmpty()) {
            FacesUtils.addErrorMessage("Nombre es requerido");
        } else {
            try {
                oficinaBeanModel.completarModificacioOficinaComedor();
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgComedor(null);
            } catch (SIAException siae) {
                FacesUtils.addErrorMessage(siae.getMessage());
                UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgComedor(null);
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e.getMessage());

                FacesUtils.addErrorMessage(new SIAException().getMessage());
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgComedor(null);
            }
        }
    }

    //SALA JUNTA
    public void agregarSalaJunta(ActionEvent event) {
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setCrearPopUp(true);
        oficinaBeanModel.setSgSalaJunta(new SgSalaJunta());
    }

    public void guardarOficinaSalaJunta(ActionEvent event) {
        if (oficinaBeanModel.getSgSalaJunta().getNombre().isEmpty()) {
            FacesUtils.addErrorMessage("Nombre es requerido");
        } else {
            oficinaBeanModel.guardarOficinaSalaJunta();
            oficinaBeanModel.setPopUp(false);
            oficinaBeanModel.setModificarPopUp(false);
            oficinaBeanModel.setCrearPopUp(false);
            oficinaBeanModel.setSgSalaJunta(null);
        }
    }

    public void modificarOficinaSalaJunta(ActionEvent event) {
        oficinaBeanModel.setSgSalaJunta((SgSalaJunta) oficinaBeanModel.getMapaLista().get("salaOficina").getRowData());
        oficinaBeanModel.setPrincipal(oficinaBeanModel.getSgSalaJunta().getNombre());
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setModificarPopUp(true);
    }

    public void completarModificacioOficinaSalaJunta(ActionEvent event) {
        if (oficinaBeanModel.getSgSalaJunta().getNombre().isEmpty()) {
            FacesUtils.addErrorMessage("Nombre es requerido");
        } else {
            try {
                oficinaBeanModel.modificarSalaJuntaOficina();
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgSalaJunta(null);
            } catch (SIAException siae) {
                FacesUtils.addErrorMessage(siae.getMessage());
                UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgSalaJunta(null);
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e.getMessage());

                FacesUtils.addErrorMessage(new SIAException().getMessage());
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgSalaJunta(null);
            }

        }
    }

    public void eliminarOficinaSalaJunta(ActionEvent event) {
        oficinaBeanModel.setSgSalaJunta((SgSalaJunta) oficinaBeanModel.getMapaLista().get("salaOficina").getRowData());
        oficinaBeanModel.setEliminarPop(true);
    }

    public void completarEliminarSalaJuntaOficina(ActionEvent event) {
        try {
            oficinaBeanModel.eliminarSalaJuntaOficina();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            cerrarPopEliminar(event);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
            cerrarPopEliminar(event);
        }
    }

    public void verDetalleSalaJuntaOficina(ActionEvent event) {
        oficinaBeanModel.setSgSalaJunta((SgSalaJunta) oficinaBeanModel.getMapaLista().get("salaOficina").getRowData());
        oficinaBeanModel.setVerDetallePop(true);
    }

    //SANITARIO
    public void agregarOficinaSanitario(ActionEvent event) {
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setCrearPopUp(true);
        oficinaBeanModel.setSgSanitario(new SgSanitario());
        oficinaBeanModel.getSgSanitario().setSexo('M');
    }

    public void guardarOficinaSanitario(ActionEvent event) {
        if (oficinaBeanModel.getSgSanitario().getNombre().isEmpty()) {
            FacesUtils.addErrorMessage("Nombre es requerido");
        } else {
            oficinaBeanModel.guardarOficinaSanitario();
            oficinaBeanModel.setPopUp(false);
            oficinaBeanModel.setModificarPopUp(false);
            oficinaBeanModel.setCrearPopUp(false);
            oficinaBeanModel.setSgSanitario(null);
        }
    }

    public void modificarSanitarioOficina(ActionEvent event) {
        oficinaBeanModel.setSgSanitario((SgSanitario) oficinaBeanModel.getMapaLista().get("sanitarioOficina").getRowData());
        oficinaBeanModel.setPrincipal(oficinaBeanModel.getSgSanitario().getNombre());
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setModificarPopUp(true);
    }

    public void eliminarSanitarioOficina(ActionEvent event) {
        oficinaBeanModel.setSgSanitario((SgSanitario) oficinaBeanModel.getMapaLista().get("sanitarioOficina").getRowData());
        oficinaBeanModel.setPrincipal(oficinaBeanModel.getSgSanitario().getNombre());
        oficinaBeanModel.setEliminarPop(true);
    }

    public void completarEliminarSanitarioOficina(ActionEvent event) {
        try {
            oficinaBeanModel.eliminarSanitarioOficina();
            cerrarPopEliminar(event);
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            cerrarPopEliminar(event);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
            cerrarPopEliminar(event);
        }
    }

    public void completarModificacioOficinaSanitario(ActionEvent event) {
        if (oficinaBeanModel.getSgSanitario().getNombre().isEmpty()) {
            FacesUtils.addErrorMessage("Nombre es requerido");
        } else {
            try {
                oficinaBeanModel.completarModificacionSanitarioOficina();
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgSanitario(null);
            } catch (SIAException siae) {
                FacesUtils.addErrorMessage(siae.getMessage());
                UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgSanitario(null);
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e.getMessage());

                FacesUtils.addErrorMessage(new SIAException().getMessage());
                oficinaBeanModel.setPopUp(false);
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgSanitario(null);
            }
        }
    }

    //CONTRATO
    public void agregarOficinaContrato(ActionEvent event) {
        oficinaBeanModel.setAgregarPopContrato(true);
        oficinaBeanModel.setPro("");
        oficinaBeanModel.setSgHistorialConvenioOficina(new SgHistorialConvenioOficina());
    }

    public DataModel getTraerContratoOficina() {
        oficinaBeanModel.buscarContratoVigente();
        if (oficinaBeanModel.getSgHistorialConvenioOficina() != null) {
            UtilLog4j.log.info(this, "Sg historial: " + getSgHistorialConvenioOficina().getConvenio().getCodigo());
        }
        return oficinaBeanModel.traerContratoOficina();
    }

    public DataModel getTraerContratoOficinaHistorial() {
        try {
            return oficinaBeanModel.traerContratoOficinaHistorial();
        } catch (Exception e) {
            return null;
        }
    }

    public void abrirArchivoConvenio(ActionEvent event) throws IOException {
        oficinaBeanModel.setSgHistorialConvenioOficina((SgHistorialConvenioOficina) oficinaBeanModel.getListaContrato().getRowData());
        oficinaBeanModel.buscarAdjuntoConvenio();
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().redirect(
                new StringBuilder().append("/ServiciosGenerales/AbrirArchivo?ZWZ2W=").append(getIdAdjunto())
                        .append("&amp;ZWZ3W=").append(getUuid()).toString());
    }

    public void openPopupEliminarRelacionContrato(ActionEvent event) {
        oficinaBeanModel.iniciaControlPopUp("popupEliminarConvenio", !oficinaBeanModel.devolverEstadoPop("popupEliminarConvenio"));
        //sesion.getControladorPopups().put("popupEliminarConvenio", !sesion.getControladorPopups().get("popupEliminarConvenio"));
    }

    public void closePopupEliminarRelacionContrato(ActionEvent actionEvent) {
        oficinaBeanModel.iniciaControlPopUp("popupEliminarConvenio", !oficinaBeanModel.devolverEstadoPop("popupEliminarConvenio"));
        //sesion.getControladorPopups().put("popupEliminarConvenio", !sesion.getControladorPopups().get("popupEliminarConvenio"));
    }

    public void verHistorialContrato(ActionEvent event) {
        oficinaBeanModel.traerHistorialConvenio();
        oficinaBeanModel.setVerDetallePop(true);
    }

    public void buscarConvenioPorProveedor(ActionEvent event) {
        if (oficinaBeanModel.getPro().isEmpty()) {
            FacesUtils.addErrorMessage("Escriba el nombre de un proveedor");
        } else {
            oficinaBeanModel.setListaContratoProveedor(oficinaBeanModel.traerConvenioPorProveedor());
            if (oficinaBeanModel.getListaContratoProveedor().getRowCount() < 1) {
                FacesUtils.addInfoMessage("No hay contrato(s) con el proveedor: " + oficinaBeanModel.getPro());
            }
        }
    }

    public DataModel getTraerConvenioPorProveedor() {
        return oficinaBeanModel.traerConvenioPorProveedor();
    }

    public void asignarContratoOficina(ActionEvent event) {
        oficinaBeanModel.setConvenio((Convenio) oficinaBeanModel.getListaContratoProveedor().getRowData());
        if (oficinaBeanModel.buscarRelacionConvenio().isEmpty()) {
            oficinaBeanModel.quitarContratoVigente();
            //Asigna el nuevo contrato
            oficinaBeanModel.setSgHistorialConvenioOficina(oficinaBeanModel.asignarContratoOficina());
            //Recupera el contrato asignado
            oficinaBeanModel.traerContratoOficina();
            oficinaBeanModel.setConvenio(null);
            oficinaBeanModel.setAgregarPopContrato(false);
            oficinaBeanModel.setPopUp(false);
        } else {
            FacesUtils.addErrorMessage("Ya existe el contrato asignado, favor de seleccionar otro");
        }
    }

    public void completarEliminarConvenioOficina(ActionEvent event) {
        oficinaBeanModel.eliminarRelacionConvenioOficina();
        oficinaBeanModel.setTamanioLista(oficinaBeanModel.traerContratoOficina().getRowCount());
        oficinaBeanModel.setSgHistorialConvenioOficina(null);
        oficinaBeanModel.setListaContrato(null);
        oficinaBeanModel.iniciaControlPopUp("popupEliminarConvenio", !oficinaBeanModel.devolverEstadoPop("popupEliminarConvenio"));
//        sesion.getControladorPopups().put("popupEliminarConvenio", !sesion.getControladorPopups().get("popupEliminarConvenio"));
    }

    public void cerrarPopHistorial(ActionEvent event) {
        oficinaBeanModel.setListaVistoBueno(null);
        oficinaBeanModel.setListaRegistro(null);
        oficinaBeanModel.setConvenio(null);
        oficinaBeanModel.setVerDetallePop(false);
    }

    public DataModel getTraerHistorialConvenio() {
        return oficinaBeanModel.traerHistorialConvenio();
    }

    public void abrirAdjuntoConvenio(ActionEvent event) {
        oficinaBeanModel.setSgHistorialConvenioOficina((SgHistorialConvenioOficina) oficinaBeanModel.getListaContrato().getRowData());
        if (oficinaBeanModel.buscarAdjuntoConvenioNuevo().size() > 0) {
            for (SiAdjunto siAdjunto : oficinaBeanModel.buscarAdjuntoConvenioNuevo()) {
                oficinaBeanModel.setUrl(Constantes.URL.concat(siAdjunto.getId().toString()));
                break;
            }
            oficinaBeanModel.getUrl();
        } else {
            FacesUtils.addInfoMessage("No se encontraron archivo(s) para abrir del contrato, " + oficinaBeanModel.getSgHistorialConvenioOficina().getConvenio().getCodigo());
        }
    }

    public void abrirAdjuntoConvenioHistorial(ActionEvent event) {
        SgHistorialConvenioOficina hco = (SgHistorialConvenioOficina) oficinaBeanModel.getListaRegistro().getRowData();
        oficinaBeanModel.setConvenio(hco.getConvenio());
    }

    public DataModel getTraerAdjuntoContrato() {
        DataModel retVal = null;
        try {
            retVal = oficinaBeanModel.traerAdjuntoContrato();
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return retVal;
    }

//////    public DataModel getTraerArchivoConvenioOficia() {
//////        try {
//////            return oficinaBeanModel.traerArchivoConvenioOficia();
//////        } catch (Exception e) {
//////            return null;
//////        }
//////    }
////    public DataModel getTraerArchivoConvenioOficiaHistorial() {
////        try {
////            return oficinaBeanModel.traerArchivoConvenioOficiaHistorial();
////        } catch (Exception e) {
////            return null;
////        }
////    }
    public void cerrarPopAbrirArchivoConvenioHistorial(ActionEvent event) {
        oficinaBeanModel.setListaContrato(null);
        oficinaBeanModel.setAbrirArchivHistorial(false);
    }

    public String getUrl() {
        return oficinaBeanModel.getUrl();
    }

    //PLANO DE OFICINA
    public void agregarPlano(ActionEvent event) {
        oficinaBeanModel.setSubirArchivo(true);
        oficinaBeanModel.setSgOficinaPlano(new SgOficinaPlano());
    }

    public DataModel getTraerPlano() {
        DataModel retVal = null;

        try {
            retVal = oficinaBeanModel.traerPlanoOficina();
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return retVal;
    }

    public void eliminarPlano(ActionEvent event) {
        oficinaBeanModel.setSgOficinaPlano((SgOficinaPlano) oficinaBeanModel.getListaPlano().getRowData());
        oficinaBeanModel.setEliminarPop(true);
    }

    public void completarEliminarPlano(ActionEvent event) {
        oficinaBeanModel.eliminarPlano();
        oficinaBeanModel.setSgOficinaPlano(null);
        oficinaBeanModel.setEliminarPop(false);
    }

    public void subirPlanoOficina(FileUploadEvent fileEvent) {
        try {
            boolean valid = false;
            fileInfo = fileEvent.getFile();
            ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

            AlmacenDocumentos almacenDocumentos
                    = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                boolean error = true;
                try {

                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                    documentoAnexo.setRuta(getDirPlano());
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    documentoAnexo.setTipoMime(fileInfo.getContentType());
                    almacenDocumentos.guardarDocumento(documentoAnexo);

                    valid
                            = oficinaBeanModel.guardarPlanoOficina(
                                    documentoAnexo.getNombreBase(),
                                    documentoAnexo.getRuta(),
                                    documentoAnexo.getTipoMime(),
                                    documentoAnexo.getTamanio()
                            );
                    oficinaBeanModel.traerPlanoOficina();
                    oficinaBeanModel.setSgOficinaPlano(null);
                    oficinaBeanModel.setSubirArchivo(false);

                    error = false;

                } catch (SIAException e) {
                    LOGGER.fatal(e);
                }

                if (!valid || error) {
                    FacesUtils.addErrorMessage("No se pudo guardar el archivo. Porfavor pónganse en contacto con el Equipo del SIA al correo soportesia@ihsa.mx");
                }
            } else {
                FacesUtils.addInfoMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();
        } catch (IOException ex) {
            Logger.getLogger(OficinaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getDirPlano() {
        return oficinaBeanModel.dirPlano();
    }

    //BITÁCORA DE SANITARIO
    public void traerBitacoraSanitarioListener(ValueChangeEvent valueChangeEvent) {
        oficinaBeanModel.setIdSanitario((Integer) valueChangeEvent.getNewValue());
        oficinaBeanModel.buscarSanitarioPorId();
        oficinaBeanModel.traerBitacoraSanitario();
    }

    public List<SelectItem> getTraerListaSanitario() {
        try {
            return oficinaBeanModel.traerListaSanitario();
        } catch (Exception e) {
            return null;
        }
    }

    public void agregarBitacora(ActionEvent event) {
        oficinaBeanModel.setSgCtrlMantenimientoSanitario(new SgCtrlMantenimientoSanitario());
        oficinaBeanModel.buscarSanitarioPorId();
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setCrearPopUp(true);
    }

    public void seleccionarBitacoraSanitario(ActionEvent event) {
        oficinaBeanModel.setSgCtrlMantenimientoSanitario((SgCtrlMantenimientoSanitario) oficinaBeanModel.getMapaLista().get("bitacoraSanitario").getRowData());
        oficinaBeanModel.setSgSanitario(oficinaBeanModel.getSgCtrlMantenimientoSanitario().getSgOficnaSanitario());
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setModificarPopUp(true);
    }

    public void eliminarBitacoraSanitario(ActionEvent event) {
        oficinaBeanModel.setSgCtrlMantenimientoSanitario((SgCtrlMantenimientoSanitario) oficinaBeanModel.getMapaLista().get("bitacoraSanitario").getRowData());
        oficinaBeanModel.eliminarBitacoraSanitario();
        oficinaBeanModel.setSgCtrlMantenimientoSanitario(null);
    }

    public void subirBitacoraSanitario(ActionEvent event) {
        oficinaBeanModel.setSgCtrlMantenimientoSanitario((SgCtrlMantenimientoSanitario) oficinaBeanModel.getMapaLista().get("bitacoraSanitario").getRowData());
        oficinaBeanModel.setSgSanitario(oficinaBeanModel.getSgCtrlMantenimientoSanitario().getSgOficnaSanitario());
        oficinaBeanModel.setSubirArchivo(true);
    }

    public void eliminarAdjuntoBitacoraSanitario(ActionEvent event) {
        oficinaBeanModel.setSgCtrlMantenimientoSanitario((SgCtrlMantenimientoSanitario) oficinaBeanModel.getMapaLista().get("bitacoraSanitario").getRowData());
        if (!oficinaBeanModel.eliminarAdjuntoBitacoraSanitario()) {
            FacesUtils.addErrorMessage("Ocurrió un error al eliminar el adjunto de la Bitácora. Porfavor contacte al Equipo del SIA al correo soportesia@ihsa.mx");
        }
        oficinaBeanModel.setSgCtrlMantenimientoSanitario(null);
    }

    public void guardarBitacoraSanitario(ActionEvent event) {
        if (!oficinaBeanModel.getSgCtrlMantenimientoSanitario().getNumero().isEmpty()) {
            if (oficinaBeanModel.getSgCtrlMantenimientoSanitario().getInicioRegistro() != null) {
                if (oficinaBeanModel.getSgCtrlMantenimientoSanitario().getFinRegistro() != null) {
                    oficinaBeanModel.guardarBitacoraSanitario();
                    oficinaBeanModel.setSgSanitario(null);
                    oficinaBeanModel.setSgCtrlMantenimientoSanitario(null);
                    oficinaBeanModel.setPopUp(false);
                    oficinaBeanModel.setCrearPopUp(false);
                    oficinaBeanModel.traerBitacoraSanitario();
                } else {
                    FacesUtils.addErrorMessage("Es necesario seleccionar la fecha final");
                }
            } else {
                FacesUtils.addErrorMessage("Es necesario seleccionar la fecha de inicio");
            }
        } else {
            FacesUtils.addErrorMessage("Es necesario agregar el número");
        }
    }

    public void modificarBitacoraSanitario(ActionEvent event) {
        if (oficinaBeanModel.getSgCtrlMantenimientoSanitario().getNumero() != null) {
            if (oficinaBeanModel.getSgCtrlMantenimientoSanitario().getInicioRegistro() != null) {
                if (oficinaBeanModel.getSgCtrlMantenimientoSanitario().getFinRegistro() != null) {
                    oficinaBeanModel.modificarBitacoraSanitario();
                    oficinaBeanModel.setSgSanitario(null);
                    oficinaBeanModel.setSgCtrlMantenimientoSanitario(null);
                    oficinaBeanModel.setModificarPopUp(false);
                    oficinaBeanModel.traerBitacoraSanitario();
                } else {
                    FacesUtils.addErrorMessage("Es necesario seleccionar la fecha final");
                }
            } else {
                FacesUtils.addErrorMessage("Es necesario seleccionar la fecha de inicio");
            }
        } else {
            FacesUtils.addErrorMessage("Es necesario agregar el número");
        }
    }

    public void validaFechaBitacora(FacesContext context, UIComponent validate, Object value) {
        Date f = (Date) value;
        UtilLog4j.log.info(this, "Fecha V +" + f);
        if (f.compareTo(oficinaBeanModel.getSgCtrlMantenimientoSanitario().getInicioRegistro()) < 0) {
            ((UIInput) validate).setValid(false);
            FacesMessage msg = new FacesMessage("Elija una fecha posterior a la de inicio del periodo");
            context.addMessage(validate.getClientId(context), msg);
        }
    }

    public void cerrapPopBitacora(ActionEvent event) {
        oficinaBeanModel.setSgCtrlMantenimientoSanitario(null);
        oficinaBeanModel.setSgSanitario(null);
        oficinaBeanModel.setPopUp(false);
        oficinaBeanModel.setCrearPopUp(false);
        oficinaBeanModel.setModificarPopUp(false);
        oficinaBeanModel.traerBitacoraSanitario();
    }

    public String getDirBitacora() {
        String retVal = Constantes.VACIO;

        if (oficinaBeanModel.getSgCtrlMantenimientoSanitario() != null) {
            retVal = oficinaBeanModel.getDirBitacora();
        }

        return retVal;
    }

    public void subirBitacora(FileUploadEvent fileEvent) {
        try {
            boolean valid = false;
            fileInfo = fileEvent.getFile();

            final ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();

            final AlmacenDocumentos almacenDocumentos
                    = proveedorAlmacenDocumentos.getAlmacenDocumentos();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                try {
                    DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                    documentoAnexo.setNombreBase(fileInfo.getFileName());
                    documentoAnexo.setTipoMime(fileInfo.getContentType());
                    documentoAnexo.setRuta(getDirBitacora());
                    almacenDocumentos.guardarDocumento(documentoAnexo);

                    valid
                            = oficinaBeanModel.guardarAdjuntoBitacoraSanitario(
                                    documentoAnexo.getNombreBase(),
                                    documentoAnexo.getTipoMime(),
                                    documentoAnexo.getTamanio()
                            );
                    oficinaBeanModel.traerBitacoraSanitario();
                    oficinaBeanModel.setSgCtrlMantenimientoSanitario(null);
                    oficinaBeanModel.setSubirArchivo(false);

                } catch (SIAException e) {
                    LOGGER.error(e);
                }

            } else {
                FacesUtils.addInfoMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

            if (!valid) {
                FacesUtils.addInfoMessage(
                        "Ocurrió un error al subir la Bitácora.  Porfavor contacte al Equipo del SIA al correo soportesia@ihsa.mx"
                );
            }
        } catch (IOException ex) {
            Logger.getLogger(OficinaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadFile() {
        UtilLog4j.log.info(this, "upload");
    }

    public void cerrapPopBitacoraSubirArchivo(ActionEvent event) {
        oficinaBeanModel.setSubirArchivo(false);
        oficinaBeanModel.setSgCtrlMantenimientoSanitario(null);
    }

    //CARACTERISTICAS
//    public void valueChangedCaracteristica(ValueChangeEvent valueChangeEvent) {
//        UtilLog4j.log.info(this, "valueChanged");
//        if (valueChangeEvent.getComponent() instanceof SelectInputText) {
//            SelectInputText autoComplete = (SelectInputText) valueChangeEvent.getComponent();
//            this.oficinaBeanModel.setPrefijo((String) autoComplete.getSelectedItem().getValue());
//
//            String text = (String) valueChangeEvent.getNewValue();
//            UtilLog4j.log.info(this, "Text:" + text);
//
//            if (text == null || text.trim().isEmpty()) {
//                UtilLog4j.log.info(this, "text == null o length==0");
//                oficinaBeanModel.getMatchesList().clear();
//            } else {
//                updateList(valueChangeEvent);
//            }
//
//            if (autoComplete.getSelectedItem() != null) { //Si se seleccionó una Característica desde el SelectItem
//                UtilLog4j.log.info(this, "Actual SelectItem: " + (String) autoComplete.getSelectedItem().getValue());
//                this.oficinaBeanModel.setPrefijo((String) autoComplete.getSelectedItem().getValue());
////                try { //Agregar la Característica
////                    if (oficinaBeanModel.getCantidadCaracteristica() != null && oficinaBeanModel.getCantidadCaracteristica() > 0) {
////                        oficinaBeanModel.setPrefijo((String) autoComplete.getSelectedItem().getValue());
////                        oficinaBeanModel.addCaracteristica();
////                        oficinaBeanModel.setCantidadCaracteristica(1);
////                        FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + (String) autoComplete.getSelectedItem().getValue());
////                    } else {
////                        FacesUtils.addErrorMessage("msgCaracteristicas", "Cantidad es requerido");
////                    }
////                } catch (SIAException siae) {
////                    FacesUtils.addErrorMessage(siae.getMessage());
////                    UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
////                } catch (Exception e) {
////                    UtilLog4j.log.info(this, e.getMessage());
////                    e.printStackTrace();
////                    FacesUtils.addErrorMessage(new SIAException().getMessage());
////                } finally {
////                    oficinaBeanModel.setCantidadCaracteristica(1);
////                }
//            }
//        }
//    }
//    public void updateList(ValueChangeEvent valueChangeEvent) {
//        UtilLog4j.log.info(this, "Actualizando la lista desde el updateList(valueChangeEvent)");
//        SelectInputText autoComplete = (SelectInputText) valueChangeEvent.getComponent();
//        String text = (String) valueChangeEvent.getNewValue();
//        updateList(text);
//    }
    public void textChangeListener(String textChangeEvent) {
        UtilLog4j.log.info(this, "textChangeEvent");
        this.oficinaBeanModel.getSgCaracteristicaOficina().setNombre(textChangeEvent);

        UtilLog4j.log.info(this, "text == null o length==0");
        oficinaBeanModel.getMatchesList().clear();

        //SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
        this.oficinaBeanModel.getSgCaracteristicaOficina().setNombre((String) textChangeEvent);

    }

    public void updateList(String text) {
        UtilLog4j.log.info(this, "Actualizando la lista desde el updateList(textChangeEvent)");
        oficinaBeanModel.getMatchesList().clear();
        for (SelectItem si : oficinaBeanModel.getCaracteristicas()) {
            if ((((String) si.getValue()).toLowerCase()).startsWith(text.toLowerCase())) {
                oficinaBeanModel.getMatchesList().add(si);
            }
        }
        UtilLog4j.log.info(this, "Matcheslist: " + oficinaBeanModel.getMatchesList().size());
    }

    public void addCaracteristicaOficina(ActionEvent actionEvent) {
        try {
            if (!this.oficinaBeanModel.getSgCaracteristicaOficina().getNombre().isEmpty()) {
                if (oficinaBeanModel.getCantidadCaracteristica() != null && oficinaBeanModel.getCantidadCaracteristica() > 0) {
                    oficinaBeanModel.addCaracteristica();
                    updateList(oficinaBeanModel.getSgCaracteristicaOficina().getNombre());
                    FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + oficinaBeanModel.getPrefijo());
                } else {
                    FacesUtils.addErrorMessage("msgCaracteristicasGeneralesOficina", "Cantidad es requerido");
                }
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicasGeneralesOficina", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage("msgCaracteristicasGeneralesOficina", siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica a la Oficina");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            oficinaBeanModel.setCantidadCaracteristica(1);
        }
    }

    public void addCaracteristicaComedor(ActionEvent actionEvent) {
        try {
            if (!this.oficinaBeanModel.getSgCaracteristicaOficina().getNombre().isEmpty()) {
                if (oficinaBeanModel.getCantidadCaracteristica() != null
                        && oficinaBeanModel.getCantidadCaracteristica() > 0) {
                    oficinaBeanModel.addCaracteristicaComedor();
                    updateList(oficinaBeanModel.getSgCaracteristicaOficina().getNombre());
                    FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + oficinaBeanModel.getPrefijo());
                } else {
                    FacesUtils.addErrorMessage("msgCaracteristicasComedor", "Cantidad es requerido");
                }
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicasComedor", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica al Comedor");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            oficinaBeanModel.setCantidadCaracteristica(1);
//            this.oficinaBeanModel.getMatchesList().clear();
//            this.oficinaBeanModel.setPrefijo("");
//            clearComponent("formCaracteristicasComedor", "sinTxtCaracteristica");
        }
    }

    public void addCaracteristicaSalaJunta(ActionEvent actionEvent) {
        try {
            if (!this.oficinaBeanModel.getSgCaracteristicaOficina().getNombre().isEmpty()) {
                if (oficinaBeanModel.getCantidadCaracteristica() != null && oficinaBeanModel.getCantidadCaracteristica() > 0) {
                    oficinaBeanModel.addCaracteristicaSala();
                    updateList(oficinaBeanModel.getSgCaracteristicaOficina().getNombre());
                    FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + oficinaBeanModel.getPrefijo());
                } else {
                    FacesUtils.addErrorMessage("msgCaracteristicasSalaJunta", "Cantidad es requerido");
                }
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicasSalaJunta", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica a la Sala de Juntas");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            oficinaBeanModel.setCantidadCaracteristica(1);
//            this.oficinaBeanModel.getMatchesList().clear();
//            this.oficinaBeanModel.setPrefijo("");
//            clearComponent("formCaracteristicasSalaJunta", "sinTxtCaracteristica");
        }
    }

    public void addCaracteristicaSanitario(ActionEvent actionEvent) {
        try {
            if (!this.oficinaBeanModel.getSgCaracteristicaOficina().getNombre().isEmpty()) {
                if (oficinaBeanModel.getCantidadCaracteristica() != null
                        && oficinaBeanModel.getCantidadCaracteristica() > 0) {
                    oficinaBeanModel.addCaracteristicaSanitario();
                    updateList(oficinaBeanModel.getSgCaracteristicaOficina().getNombre());
                    FacesUtils.addInfoMessage("msgCaracteristicas", "Agregada la Característica: " + oficinaBeanModel.getPrefijo());
                } else {
                    FacesUtils.addErrorMessage("msgCaracteristicasSanitario", "Cantidad es requerido");
                }
            } else {
                FacesUtils.addErrorMessage("msgCaracteristicasSanitario", "Nombre es requerido");
            }
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (NonUniqueResultException nure) {
            FacesUtils.addErrorMessage("Se encontró asociada mas de una vez la misma Característica al Sanitario");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        } finally {
            oficinaBeanModel.setCantidadCaracteristica(1);
//            this.oficinaBeanModel.getMatchesList().clear();
//            this.oficinaBeanModel.setPrefijo("");
//            clearComponent("formCaracteristicasSanitario", "sinTxtCaracteristica");
        }
    }

    public void removeCaracteristicaOficina(ActionEvent actionEvent) {
        try {
            oficinaBeanModel.setSgCaracteristicaOficina((CaracteristicaVo) oficinaBeanModel.getMapaLista().get("caracteristica_oficina").getRowData());
            oficinaBeanModel.removeCaracteristica();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void removeCaracteristicaComedor(ActionEvent actionEvent) {
        try {
            oficinaBeanModel.setSgCaracteristicaOficina((CaracteristicaVo) oficinaBeanModel.getMapaLista().get("comedor").getRowData());
            oficinaBeanModel.removeCaracteristicaComedor();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void removeCaracteristicaSalaJunta(ActionEvent actionEvent) {
        try {
            oficinaBeanModel.setSgCaracteristicaOficina((CaracteristicaVo) oficinaBeanModel.getMapaLista().get("sala").getRowData());
            oficinaBeanModel.removeCaracteristicaSala();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void removeCaracteristicaSanitario(ActionEvent actionEvent) {
        try {
            oficinaBeanModel.setSgCaracteristicaOficina((CaracteristicaVo) oficinaBeanModel.getMapaLista().get("sanitario").getRowData());
            oficinaBeanModel.removeCaracteristicaSanitario();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);

            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void agregarCaracteristicaOficina(ActionEvent event) {
        oficinaBeanModel.setObject(new SgCaracteristicaOficina());
        //Cargar todas las Características
        oficinaBeanModel.getAllCaracteristicas();
        oficinaBeanModel.controlaPopUpTrue("popupCaracteristicasOficina");
        //sesion.getControladorPopups().put("popupCaracteristicasOficina", Boolean.TRUE);
    }

    public void agregarCaracteristicaComedorOficina(ActionEvent event) {
        try {
            //Dándole memoria al Comedor seleccionado
            oficinaBeanModel.setSgComedor((SgComedor) oficinaBeanModel.getMapaLista().get("comedorOficina").getRowData());
            oficinaBeanModel.setObject(new SgCaracteristicaComedor());
            //Cargar todas las Características
            oficinaBeanModel.getAllCaracteristicas();
            oficinaBeanModel.caracteristicasComedor();
            oficinaBeanModel.controlaPopUpTrue("popupCaracteristicasComedor");
            //sesion.getControladorPopups().put("popupCaracteristicasComedor", Boolean.TRUE);
        } catch (Exception ex) {
            Logger.getLogger(OficinaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarCaracteristicaSalaJuntaOficina(ActionEvent event) {
        try {
            //Dándole memoria a la Sala de Juntas seleccionada
            oficinaBeanModel.setSgSalaJunta((SgSalaJunta) oficinaBeanModel.getMapaLista().get("salaOficina").getRowData());
            //Cargar todas las Características
            oficinaBeanModel.getAllCaracteristicas();
            oficinaBeanModel.caracteristicasSalaJunta();
            oficinaBeanModel.controlaPopUpTrue("popupCaracteristicasSalaJuntas");
            //sesion.getControladorPopups().put("popupCaracteristicasSalaJuntas", Boolean.TRUE);
        } catch (Exception ex) {
            Logger.getLogger(OficinaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarCaracteristicaSanitario(ActionEvent event) {
        try {
            //Dándole memoria al Sanitario seleccionado
            oficinaBeanModel.setSgSanitario((SgSanitario) oficinaBeanModel.getMapaLista().get("sanitarioOficina").getRowData());
            oficinaBeanModel.setObject(new SgCaracteristicaSanitario());
            //Cargar todas las Características
            oficinaBeanModel.getAllCaracteristicas();
            oficinaBeanModel.caracteristicasSanitario();
            oficinaBeanModel.controlaPopUpTrue("popupCaracteristicasSanitario");
            //sesion.getControladorPopups().put("popupCaracteristicasSanitario", Boolean.TRUE);
        } catch (Exception ex) {
            Logger.getLogger(OficinaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closePopupCaracteristicasOficina(ActionEvent actionEvent) {
        //Quitándo memorias
        oficinaBeanModel.setPrefijo("");
        oficinaBeanModel.setCantidadCaracteristica(1);
        oficinaBeanModel.setSgCaracteristica(null);
        oficinaBeanModel.setSgCaracteristicaOficina(null);
        oficinaBeanModel.setCaracteristicas(null);
        oficinaBeanModel.setMatchesList(null);
        cerrarPop(actionEvent);
        oficinaBeanModel.controlaPopUpFalso("popupCaracteristicasOficina");
        //sesion.getControladorPopups().put("popupCaracteristicasOficina", Boolean.FALSE);
    }

    public void closePopupCaracteristicasComedor(ActionEvent actionEvent) {
        oficinaBeanModel.setPrefijo("");
        oficinaBeanModel.setCantidadCaracteristica(1);
        oficinaBeanModel.setSgComedor(null);
        oficinaBeanModel.setSgCaracteristica(null);
        oficinaBeanModel.setCaracteristicas(null);
        oficinaBeanModel.setMatchesList(null);
        cerrarPop(actionEvent);
        oficinaBeanModel.controlaPopUpFalso("popupCaracteristicasComedor");
        //sesion.getControladorPopups().put("popupCaracteristicasComedor", Boolean.FALSE);
    }

    public void closePopupCaracteristicasSalaJuntas(ActionEvent actionEvent) {
        oficinaBeanModel.setPrefijo("");
        oficinaBeanModel.setCantidadCaracteristica(1);
        oficinaBeanModel.setSgSalaJunta(null);
        oficinaBeanModel.setSgCaracteristica(null);
        oficinaBeanModel.setCaracteristicas(null);
        oficinaBeanModel.setMatchesList(null);
        cerrarPop(actionEvent);
        oficinaBeanModel.controlaPopUpFalso("popupCaracteristicasSalaJuntas");
//        sesion.getControladorPopups().put("popupCaracteristicasSalaJuntas", Boolean.FALSE);
    }

    public void closePopupCaracteristicasSanitario(ActionEvent actionEvent) {
        oficinaBeanModel.setPrefijo("");
        oficinaBeanModel.setCantidadCaracteristica(1);
        oficinaBeanModel.setSgSanitario(null);
        oficinaBeanModel.setSgCaracteristica(null);
        oficinaBeanModel.setCaracteristicas(null);
        oficinaBeanModel.setMatchesList(null);
        cerrarPop(actionEvent);
        oficinaBeanModel.controlaPopUpFalso("popupCaracteristicasSanitario");
        //sesion.getControladorPopups().put("popupCaracteristicasSanitario", Boolean.FALSE);
    }

    public void openPopupDetalleComedor(ActionEvent actionEvent) {
        oficinaBeanModel.setSgComedor((SgComedor) oficinaBeanModel.getMapaLista().get("comedorOficina").getRowData());
        oficinaBeanModel.controlaPopUpTrue("popupDetalleComedor");
        //sesion.getControladorPopups().put("popupDetalleComedor", !sesion.getControladorPopups().get("popupDetalleComedor"));
    }

    public void closePopUpDetalleComedor(ActionEvent actionEvent) {
        oficinaBeanModel.setSgComedor(null);
        oficinaBeanModel.iniciaControlPopUp("popupDetalleComedor", oficinaBeanModel.devolverEstadoPop("popupDetalleComedor"));
        //sesion.getControladorPopups().put("popupDetalleComedor", !sesion.getControladorPopups().get("popupDetalleComedor"));
    }

    public void openPopupDetalleSalaJuntas(ActionEvent actionEvent) {
        oficinaBeanModel.setSgSalaJunta((SgSalaJunta) oficinaBeanModel.getMapaLista().get("salaOficina").getRowData());
        oficinaBeanModel.iniciaControlPopUp("popupDetalleSalaJuntas", oficinaBeanModel.devolverEstadoPop("popupDetalleSalaJuntas"));
        //sesion.getControladorPopups().put("popupDetalleSalaJuntas", !sesion.getControladorPopups().get("popupDetalleSalaJuntas"));
    }

    public void closePopUpDetalleSalaJuntas(ActionEvent actionEvent) {
        oficinaBeanModel.setSgSalaJunta(null);
        oficinaBeanModel.iniciaControlPopUp("popupDetalleSalaJuntas", oficinaBeanModel.devolverEstadoPop("popupDetalleSalaJuntas"));
        //sesion.getControladorPopups().put("popupDetalleSalaJuntas", !sesion.getControladorPopups().get("popupDetalleSalaJuntas"));
    }

    public void openPopupDetalleSanitario(ActionEvent actionEvent) {
        oficinaBeanModel.setSgSanitario((SgSanitario) oficinaBeanModel.getMapaLista().get("sanitarioOficina").getRowData());
        oficinaBeanModel.iniciaControlPopUp("popupDetalleSanitario", oficinaBeanModel.devolverEstadoPop("popupDetalleSanitario"));
        //sesion.getControladorPopups().put("popupDetalleSanitario", !sesion.getControladorPopups().get("popupDetalleSanitario"));
    }

    public void closePopUpDetalleSanitario(ActionEvent actionEvent) {
        oficinaBeanModel.setSgSanitario(null);
        oficinaBeanModel.iniciaControlPopUp("popupDetalleSanitario", oficinaBeanModel.devolverEstadoPop("popupDetalleSanitario"));
//        sesion.getControladorPopups().put("popupDetalleSanitario", !sesion.getControladorPopups().get("popupDetalleSanitario"));
    }

    public void cerrarPanel(ActionEvent event) {
        oficinaBeanModel.setMostrarPanel(false);
        oficinaBeanModel.setSgCaracteristica(null);
    }

    //PAGO DE SERVICIOS
    //Auto completar Proveedor
    public List<SelectItem> getListaProveedor() {
        return oficinaBeanModel.getListaProveedor();
    }

    public void proveedorListener(String cadenaDigitada) {
        oficinaBeanModel.setListaProveedor(regresaProveedorActivo(cadenaDigitada));
        oficinaBeanModel.setPro(cadenaDigitada);
    }

    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (String string : oficinaBeanModel.getListaProveedorBuscar()) {
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
        oficinaBeanModel.setListaProveedor(listaProveedor);
    }
    //Fin de autocompletar Proveedor

    public List<SelectItem> getTraerVehiculo() {
        return oficinaBeanModel.traerVehiculo();
    }

    public void buscarVehiculo(ValueChangeEvent valueChangeEvent) {
        oficinaBeanModel.setIdVehiculo((Integer) valueChangeEvent.getNewValue());
        oficinaBeanModel.setListaTipoEspecifico(null);

    }

    public List<SelectItem> getTraerTipoEspOficina() {
        oficinaBeanModel.traerTipoEspecificoPorTipoOficina();
        return oficinaBeanModel.getListaTipoEspecifico();
    }

    //FIN STAFF
    //OFICINA
    public void traerPagoOficina(ValueChangeEvent valueChangeEvent) {
        oficinaBeanModel.setIdTipoEspecifico((Integer) valueChangeEvent.getNewValue());
        oficinaBeanModel.traerPagoPorOficina();
    }

    public void seleccionarPagoOficina(ActionEvent event) {
        oficinaBeanModel.setSgPagoServicioOficina((SgPagoServicioOficina) oficinaBeanModel.getMapaLista().get("pago").getRowData());
        oficinaBeanModel.setSgPagoServicio(oficinaBeanModel.getSgPagoServicioOficina().getSgPagoServicio());
        oficinaBeanModel.setIdMoneda(oficinaBeanModel.getSgPagoServicio().getMoneda().getId());
        oficinaBeanModel.setModificarPopUp(true);
    }

    public void eliminarPagoServicioOficina(ActionEvent event) {
        oficinaBeanModel.setSgPagoServicioOficina((SgPagoServicioOficina) oficinaBeanModel.getMapaLista().get("pago").getRowData());
        oficinaBeanModel.setSgPagoServicio(oficinaBeanModel.getSgPagoServicioOficina().getSgPagoServicio());
        UtilLog4j.log.info(this, "Pago" + oficinaBeanModel.getSgPagoServicio().getFechaFin());
        oficinaBeanModel.setEliminarPop(true);

    }

    public void completarEliminarPagoOficina(ActionEvent event) {
        oficinaBeanModel.eliminarPagoServicioOficina();
        oficinaBeanModel.setSgPagoServicio(null);
        oficinaBeanModel.setEliminarPop(false);
        oficinaBeanModel.traerPagoPorOficina();
    }

    public void subirComprobanteOficina(ActionEvent event) {
        oficinaBeanModel.setSgPagoServicioOficina((SgPagoServicioOficina) oficinaBeanModel.getMapaLista().get("pago").getRowData());
        oficinaBeanModel.setSgPagoServicio(oficinaBeanModel.getSgPagoServicioOficina().getSgPagoServicio());
        oficinaBeanModel.setSubirArchivo(true);
    }

    public void eliminarComprobanteOficina(ActionEvent event) {
        oficinaBeanModel.setSgPagoServicioOficina((SgPagoServicioOficina) oficinaBeanModel.getMapaLista().get("pago").getRowData());
        oficinaBeanModel.setSgPagoServicio(oficinaBeanModel.getSgPagoServicioOficina().getSgPagoServicio());
        oficinaBeanModel.eliminarComprobante();
        oficinaBeanModel.setSgPagoServicio(null);
    }
    //FIN OFICINA

    public void agregarPagoServicioOficina(ActionEvent event) {
        oficinaBeanModel.setPro("");
        oficinaBeanModel.setPopUp(true);
        oficinaBeanModel.setSgPagoServicio(null);
        oficinaBeanModel.setSgPagoServicio(new SgPagoServicio());
        oficinaBeanModel.setCrearPopUp(true);
    }

    public void guardarPagoServicioOficina(ActionEvent event) {
        LOGGER.info("Registrando pago de servicio ...");

        if (oficinaBeanModel.buscarProveedorPorNombre() == null) {
            FacesUtils.addErrorMessage("Proveedor es requerido");
        } else if (oficinaBeanModel.getSgPagoServicio().getFechaInicio() == null) {
            FacesUtils.addErrorMessage("Fecha de Inicio es requerido");
        } else if (oficinaBeanModel.getSgPagoServicio().getFechaFin() == null) {
            FacesUtils.addErrorMessage("Fecha Fin es requerido");
        } else if (oficinaBeanModel.getSgPagoServicio().getFechaVencimiento() == null) {
            FacesUtils.addErrorMessage("Fecha Vencimiento es requerido");
        } else if (oficinaBeanModel.getIdMoneda() < 1) {
            FacesUtils.addErrorMessage("Moneda es requerido");
        } else if (oficinaBeanModel.getSgPagoServicio().getImporte() == null) {
            FacesUtils.addErrorMessage("Importe es requerido");
        } else if (oficinaBeanModel.guardarPagoServicioOficina()) {
            oficinaBeanModel.setPopUp(false);
            oficinaBeanModel.setCrearPopUp(false);
            oficinaBeanModel.setModificarPopUp(false);
            oficinaBeanModel.setSgPagoServicio(null);
            oficinaBeanModel.setPro(null);
            oficinaBeanModel.setIdMoneda(0);
        } else {
            FacesUtils.addErrorMessage("Ocurrió un error al guardar el Pago de la Oficina. Por favor contacte al Equipo del SIA al correo soportesia@ihsa.mx");
        }
    }

    public String getDir() {
        String retVal = Constantes.VACIO;

        if (oficinaBeanModel.getSgPagoServicio() != null) {
            retVal = oficinaBeanModel.getDirectorio();
        }

        return retVal;
    }

    public void subirArchivoPago(FileUploadEvent fileEvent) {
        boolean valid = false;
        fileInfo = fileEvent.getFile();
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        AlmacenDocumentos almacenDocumentos
                = proveedorAlmacenDocumentos.getAlmacenDocumentos();
        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
        if (addArchivo) {
            boolean error = true;
            try {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta(getDir());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                valid = oficinaBeanModel.guardarArchivo(
                        documentoAnexo.getNombreBase(),
                        fileInfo.getContentType(),
                        fileInfo.getSize());

                oficinaBeanModel.setSubirArchivo(false);
                oficinaBeanModel.setSgPagoServicio(null);

                error = false;

                fileInfo.delete();
            } catch (IOException | SIAException e) {
                LOGGER.fatal(e);
            }

            if (!valid || error) {
                FacesUtils.addInfoMessage("Ocurrió un error al subir el archivo del Pago. Porfavor contacte al Equipo del SIA al correo soportesia@ihsa.mx");
            }
        } else {
            FacesUtils.addInfoMessage(new StringBuilder()
                    .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                    .append(validadorNombreArchivo.getCaracteresNoValidos())
                    .toString());
        }

    }

    public void refresh() {
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        ViewHandler viewHandler = application.getViewHandler();
        UIViewRoot viewRoot = viewHandler.createView(context, context.getViewRoot().getViewId());
        context.setViewRoot(viewRoot);
//        context.renderResponse(); //Optional
    }

    public void cerrarPopSubirArchivo(ActionEvent event) {

        oficinaBeanModel.setSgPagoServicio(null);
        oficinaBeanModel.setSubirArchivo(false);
    }

    public void modificarPagoServicio(ActionEvent event) {
        if (oficinaBeanModel.getSgPagoServicio().getFechaInicio() == null) {
            FacesUtils.addErrorMessage("Fecha de Inicio es requerido");
        } else if (oficinaBeanModel.getSgPagoServicio().getFechaFin() == null) {
            FacesUtils.addErrorMessage("Fecha Fin es requerido");
        } else if (oficinaBeanModel.getSgPagoServicio().getFechaVencimiento() == null) {
            FacesUtils.addErrorMessage("Fecha de Vencimiento es requerido");
        } else if (oficinaBeanModel.getSgPagoServicio().getImporte() != null) {
            BigDecimal importe = new BigDecimal(oficinaBeanModel.getSgPagoServicio().getImporte().longValue());
            if (oficinaBeanModel.getIdMoneda() < 1) {
                FacesUtils.addErrorMessage("Moneda es requerido");
            } else if (importe.longValue() < 1) {
                FacesUtils.addErrorMessage("Importes es requerido");
            } else {
                oficinaBeanModel.modificarPagoServicio();
                oficinaBeanModel.setModificarPopUp(false);
                oficinaBeanModel.setSgPagoServicio(null);
            }
        } else {
            FacesUtils.addErrorMessage("Importes es requerido");
        }
    }

    public void cerraPopModificarPagoServicio(ActionEvent event) {
        oficinaBeanModel.traerPagoPorOficina();
        oficinaBeanModel.setModificarPopUp(false);
        oficinaBeanModel.setSgPagoServicio(null);
    }

    public void cerrarPopSubirArchivoPlano(ActionEvent event) {
        oficinaBeanModel.setSgOficinaPlano(null);
        oficinaBeanModel.setSubirArchivo(false);
    }

    public SgPagoServicioOficina getServicioOficina() {
        return oficinaBeanModel.getSgPagoServicioOficina();
    }

    public void validaFecha(FacesContext context, UIComponent validate, Object value) {
        Date f = (Date) value;
        if (f != null && oficinaBeanModel.getSgPagoServicio().getFechaInicio() != null) {
            UtilLog4j.log.info(this, "F:" + f);
            UtilLog4j.log.info(this, "Fecha V +" + f);
            if (f.compareTo(oficinaBeanModel.getSgPagoServicio().getFechaInicio()) < 0) {
                ((UIInput) validate).setValid(false);
                FacesMessage msg = new FacesMessage("Elija una fecha posterior a la de inicio del periodo");
                context.addMessage(validate.getClientId(context), msg);
            }
        }
    }

    public CaracteristicaVo getSgCaracteristicaOficina() {
        return oficinaBeanModel.getSgCaracteristicaOficina();
    }

    public List<SelectItem> getListaPagos() {
        return oficinaBeanModel.getListaPagos();
    }

    public String getPrincipal() {
        return oficinaBeanModel.getPrincipal();
    }

    public void setPrincipal(String principal) {
        oficinaBeanModel.setPrincipal(principal);
    }

    public int getIdVehiculo() {
        return oficinaBeanModel.getIdVehiculo();
    }

    public void setIdVehiculo(int idVehiculo) {
        oficinaBeanModel.setIdVehiculo(idVehiculo);
    }

    public int getIdStaff() {
        return oficinaBeanModel.getIdStaff();
    }

    public void setIdStaff(int idStaff) {
        oficinaBeanModel.setIdStaff(idStaff);
    }

    public int getIdEstado() {
        return oficinaBeanModel.getIdEstado();
    }

    public void setIdEstado(int idEstado) {
        oficinaBeanModel.setIdEstado(idEstado);
    }

    public int getIdCiudad() {
        return oficinaBeanModel.getIdCiudad();
    }

    public void setIdCiudad(int idCiudad) {
        oficinaBeanModel.setIdCiudad(idCiudad);
    }

    public void getIdVehiculo(int idVehiculo) {
        oficinaBeanModel.setIdVehiculo(idVehiculo);
    }

    public List<SelectItem> getTraerMondeda() {
        return oficinaBeanModel.traerMoneda();
    }

    public SgTipoEspecifico getSgTipoEspecifico() {
        return oficinaBeanModel.getSgTipoEspecifico();
    }

    public String getOpcionPagar() {
        return oficinaBeanModel.getOpcionPagar();
    }

    public void setOpcionPagar(String opcionPagar) {
        oficinaBeanModel.setOpcionPagar(opcionPagar);
    }

    public String getPro() {
        return oficinaBeanModel.getPro();
    }

    public void setPro(String pro) {
        oficinaBeanModel.setPro(pro);
    }

    public int getIdSanitario() {
        return oficinaBeanModel.getIdSanitario();
    }

    public void setIdSanitario(int idSanitario) {
        oficinaBeanModel.setIdSanitario(idSanitario);
    }

    public SgCtrlMantenimientoSanitario getSgCtrlMantenimientoSanitario() {
        return oficinaBeanModel.getSgCtrlMantenimientoSanitario();
    }

    /**
     * @param sgPagoServicio the sgPagoServicio to set
     */
    public void setSgPagoServicio(SgPagoServicio sgPagoServicio) {
        oficinaBeanModel.setSgPagoServicio(sgPagoServicio);
    }

    public SgPagoServicio getSgPagoServicio() {
        return oficinaBeanModel.getSgPagoServicio();
    }

    public int getIdMoneda() {
        return oficinaBeanModel.getIdMoneda();
    }

    public void setIdMoneda(int idMoneda) {
        oficinaBeanModel.setIdMoneda(idMoneda);
    }

    public void setIdTipo(int idTipo) {
        oficinaBeanModel.setIdTipo(idTipo);
    }

    public void setIdTipoEspecifico(int idIdTipoEspecifico) {
        oficinaBeanModel.setIdTipoEspecifico(idIdTipoEspecifico);
    }

    public void cerraPopPagoServicio(ActionEvent event) {
        oficinaBeanModel.setSgPagoServicio(null);
        oficinaBeanModel.setPopUp(false);
        oficinaBeanModel.setModificarPopUp(false);
        oficinaBeanModel.setPro("");
        oficinaBeanModel.getListaContrato();
    }

    public int getIdTipoEspecifico() {
        return oficinaBeanModel.getIdTipoEspecifico();
    }

    public int getIdTipo() {
        return oficinaBeanModel.getIdTipo();
    }

    public DataModel getListaContrato() {
        return oficinaBeanModel.getListaContrato();
    }

    public List getListaFilasSeleccionadas() {
        return oficinaBeanModel.getListaFilasSeleccionadas();
    }

    public Map<Integer, Boolean> getFilasSeleccionadas() {
        return oficinaBeanModel.getFilasSeleccionadas();
    }

    public boolean isSubirArchivo() {
        return oficinaBeanModel.isSubirArchivo();
    }

    public boolean isVerDetallePop() {
        return oficinaBeanModel.isVerDetallePop();
    }

    public boolean isMostrarPanel() {
        return oficinaBeanModel.isMostrarPanel();
    }

    public SgCaracteristica getSgCaracteristica() {
        return oficinaBeanModel.getSgCaracteristica();
    }

    public SgSanitario getSgSanitario() {
        return oficinaBeanModel.getSgSanitario();
    }

    public void setSgSanitario(SgSanitario sgSanitario) {
        oficinaBeanModel.setSgSanitario(sgSanitario);
    }

    public SgSalaJunta getSgSalaJunta() {
        return oficinaBeanModel.getSgSalaJunta();
    }

    public SgComedor getSgComedor() {
        return oficinaBeanModel.getSgComedor();
    }

    public int getOpcionSeleccionada() {
        return oficinaBeanModel.getOpcionSeleccionada();
    }

    public void setOpcionSeleccionada(int opcionSeleccinada) {
        oficinaBeanModel.setOpcionSeleccionada(opcionSeleccinada);
    }

    public void traerOficinaPorParametro(ValueChangeEvent valueChangeEvent) {
        oficinaBeanModel.setOpcionSeleccionada((Integer) valueChangeEvent.getNewValue());
        oficinaBeanModel.traerOficinaRegistro();
    }

    public List<SelectItem> getListaUsuario() {
        return oficinaBeanModel.getListaUsuariosAlta();
    }

    public void setListaUsuario(List<SelectItem> lista) {
        oficinaBeanModel.setListaUsuariosAlta(lista);
    }

    public Usuario getUsuario() {
        return oficinaBeanModel.getUsuario();
    }

    public String getUser() {
        return oficinaBeanModel.getUser();
    }

    public void setUser(String user) {
        oficinaBeanModel.setUser(user);
    }

    public SgHistorialConvenioOficina getSgHistorialConvenioOficina() {
        return oficinaBeanModel.getSgHistorialConvenioOficina();
    }

    /**
     * @param sgHistorialConvenioOficina the sgHistorialConvenioOficina to set
     */
    public void setSgHistorialConvenioOficina(SgHistorialConvenioOficina sgHistorialConvenioOficina) {
        oficinaBeanModel.setSgHistorialConvenioOficina(sgHistorialConvenioOficina);
    }

    public SgOficina getSgOficina() {
        return oficinaBeanModel.getSgOficina();
    }

    public SgOficinaAnalista getSgOficinaAnalista() {
        return oficinaBeanModel.getSgOficinaAnalista();
    }

    public void setSgOficinaAnalista(SgOficinaAnalista sgOficinaAnalista) {
        oficinaBeanModel.setSgOficinaAnalista(sgOficinaAnalista);
    }

    public SgDireccion getSgDireccion() {
        return oficinaBeanModel.getSgDireccion();
    }

    public void agregarOficina(ActionEvent event) {
        oficinaBeanModel.setSgDireccion(new SgDireccion());
        oficinaBeanModel.setSgOficina(new SgOficina());
        oficinaBeanModel.setCrearPopUp(true);
    }

    public boolean isPopUp() {
        return oficinaBeanModel.isPopUp();
    }

    public void setPopUp(boolean popUp) {
        oficinaBeanModel.setPopUp(popUp);
    }

    public boolean isAgregarCaracteristicaOficia() {
        return oficinaBeanModel.isAgregarCaracteristicaOficia();
    }

    public void setAgregarCaracteristicaOficia(boolean agregarCaracteristicaOficia) {
        oficinaBeanModel.setAgregarCaracteristicaOficia(agregarCaracteristicaOficia);
    }

    public boolean isCrearPop() {
        return oficinaBeanModel.isCrearPopUp();
    }

    public void setCrearPop(boolean crearPop) {
        oficinaBeanModel.setCrearPopUp(crearPop);
    }

    public boolean isModificarPop() {
        return oficinaBeanModel.isModificarPopUp();
    }

    public void setModificarPop(boolean modificarPop) {
        oficinaBeanModel.setModificarPopUp(modificarPop);
    }

    public boolean isCambiarPop() {
        return oficinaBeanModel.isCambiarPopUp();
    }

    public void setCambiarPop(boolean cambiarPop) {
        oficinaBeanModel.setCambiarPopUp(cambiarPop);
    }

    public boolean isEliminarPop() {
        return oficinaBeanModel.isEliminarPop();
    }

    public int getIdAdjunto() {
        return oficinaBeanModel.getIdAdjunto();
    }

    public String getUuid() {
        return oficinaBeanModel.getUuid();
    }

    public int getTamanioLista() {
        return oficinaBeanModel.getTamanioLista();
    }

    public Convenio getConvenio() {
        return oficinaBeanModel.getConvenio();
    }

    public boolean isAbrirArchivoHistorial() {
        return oficinaBeanModel.isAbrirArchivHistorial();
    }

    public SgOficinaPlano getSgOficinaPlano() {
        return oficinaBeanModel.getSgOficinaPlano();
    }

    /**
     * @return the numeroFactura
     */
    public String getNumeroFactura() {
        return oficinaBeanModel.getNumeroFactura();
    }

    /**
     * @param numeroFactura the numeroFactura to set
     */
    public void setNumeroFactura(String numeroFactura) {
        oficinaBeanModel.setNumeroFactura(numeroFactura);
    }

    /**
     * @return the cantidadCaracteristica
     */
    public Integer getCantidadCaracteristica() {
        return oficinaBeanModel.getCantidadCaracteristica();
    }

    /**
     * @param cantidadCaracteristica the cantidadCaracteristica to set
     */
    public void setCantidadCaracteristica(Integer cantidadCaracteristica) {
        oficinaBeanModel.setCantidadCaracteristica(cantidadCaracteristica);
    }

    /**
     * @return the prefijo
     */
    public String getPrefijo() {
        return oficinaBeanModel.getPrefijo();
    }

    /**
     * @param prefijo the prefijo to set
     */
    public void setPrefijo(String prefijo) {
        oficinaBeanModel.setPrefijo(prefijo);
    }

    /**
     * @return the listaPlano
     */
    public DataModel getListaPlano() {
        return oficinaBeanModel.getListaPlano();
    }

    /**
     * @param listaPlano the listaPlano to set
     */
    public void setListaPlano(DataModel listaPlano) {
        oficinaBeanModel.setListaPlano(listaPlano);
    }

    /**
     * @return the agregarPopContrato
     */
    public boolean isAgregarPopContrato() {
        return oficinaBeanModel.isAgregarPopContrato();
    }

    /**
     * @param agregarPopContrato the agregarPopContrato to set
     */
    public void setAgregarPopContrato(boolean agregarPopContrato) {
        oficinaBeanModel.setAgregarPopContrato(agregarPopContrato);
    }

    /**
     * @return the caracteristicas
     */
    public List<SelectItem> getCaracteristicas() {
        return oficinaBeanModel.getCaracteristicas();
    }

    /**
     * @param caracteristicas the caracteristicas to set
     */
    public void setCaracteristicas(List<SelectItem> caracteristicas) {
        oficinaBeanModel.setCaracteristicas(caracteristicas);
    }

    /**
     * @return the matchesList
     */
    public List<SelectItem> getMatchesList() {
        return oficinaBeanModel.getMatchesList();
    }

    /**
     * @param matchesList the matchesList to set
     */
    public void setMatchesList(List<SelectItem> matchesList) {
        oficinaBeanModel.setMatchesList(matchesList);
    }

    /**
     * @return the idPais
     */
    public int getIdPais() {
        return oficinaBeanModel.getIdPais();
    }

    /**
     * @param idPais the idPais to set
     */
    public void setIdPais(int idPais) {
        oficinaBeanModel.setIdPais(idPais);
    }

    /**
     * @return the oficinaVO
     */
    public OficinaVO getOficinaVO() {
        return this.oficinaBeanModel.getOficinaVO();
    }

    /**
     * @param oficinaVO the oficinaVO to set
     */
    public void setOficinaVO(OficinaVO oficinaVO) {
        this.oficinaBeanModel.setOficinaVO(oficinaVO);
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return this.oficinaBeanModel.getNombre();
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.oficinaBeanModel.setNombre(nombre);
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return this.oficinaBeanModel.getTelefono();
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
        this.oficinaBeanModel.setTelefono(telefono);
    }

    /**
     * @return the municipio
     */
    public String getMunicipio() {
        return this.oficinaBeanModel.getMunicipio();
    }

    /**
     * @param municipio the municipio to set
     */
    public void setMunicipio(String municipio) {
        this.oficinaBeanModel.setMunicipio(municipio);
    }

    /**
     * @return the colonia
     */
    public String getColonia() {
        return this.oficinaBeanModel.getColonia();
    }

    /**
     * @param colonia the colonia to set
     */
    public void setColonia(String colonia) {
        this.oficinaBeanModel.setColonia(colonia);
    }

    /**
     * @return the calle
     */
    public String getCalle() {
        return this.oficinaBeanModel.getCalle();
    }

    /**
     * @param calle the calle to set
     */
    public void setCalle(String calle) {
        this.oficinaBeanModel.setCalle(calle);
    }

    /**
     * @return the numExterior
     */
    public String getNumExterior() {
        return this.oficinaBeanModel.getNumExterior();
    }

    /**
     * @param numExterior the numExterior to set
     */
    public void setNumExterior(String numExterior) {
        this.oficinaBeanModel.setNumExterior(numExterior);
    }

    /**
     * @return the numInterior
     */
    public String getNumInterior() {
        return this.oficinaBeanModel.getNumInterior();
    }

    /**
     * @param numInterior the numInterior to set
     */
    public void setNumInterior(String numInterior) {
        this.oficinaBeanModel.setNumInterior(numInterior);
    }

    /**
     * @return the numPiso
     */
    public String getNumPiso() {
        return this.oficinaBeanModel.getNumPiso();
    }

    /**
     * @param numPiso the numPiso to set
     */
    public void setNumPiso(String numPiso) {
        this.oficinaBeanModel.setNumPiso(numPiso);
    }

    /**
     * @return the codigoPostal
     */
    public String getCodigoPostal() {
        return this.oficinaBeanModel.getCodigoPostal();
    }

    /**
     * @param codigoPostal the codigoPostal to set
     */
    public void setCodigoPostal(String codigoPostal) {
        this.oficinaBeanModel.setCodigoPostal(codigoPostal);
    }

    /**
     * @param listaPagos the listaPagos to set
     */
    public void setListaPagos(List<SelectItem> listaPagos) {
        this.oficinaBeanModel.setListaPagos(listaPagos);
    }

    /**
     * @return the listaProveedorBuscar
     */
    public List<String> getListaProveedorBuscar() {
        return this.oficinaBeanModel.getListaProveedorBuscar();
    }

    /**
     * @param listaProveedorBuscar the listaProveedorBuscar to set
     */
    public void setListaProveedorBuscar(List<String> listaProveedorBuscar) {
        this.oficinaBeanModel.setListaProveedorBuscar(listaProveedorBuscar);
    }

    /**
     * @return the dataModelAux
     */
    public DataModel getDataModelAux() {
        return this.oficinaBeanModel.getDataModelAux();
    }

    /**
     * @param dataModelAux the dataModelAux to set
     */
    public void setDataModelAux(DataModel dataModelAux) {
        this.oficinaBeanModel.setDataModelAux(dataModelAux);
    }

    /**
     * @param oficinaBeanModel the oficinaBeanModel to set
     */
    public void setOficinaBeanModel(OficinaBeanModel oficinaBeanModel) {
        this.oficinaBeanModel = oficinaBeanModel;
    }

    /**
     * @return the latitud
     */
    public String getLatitud() {
        return this.oficinaBeanModel.getLatitud();
    }

    /**
     * @param latitud the latitud to set
     */
    public void setLatitud(String latitud) {
        this.oficinaBeanModel.setLatitud(latitud);
    }

    /**
     * @return the longitud
     */
    public String getLongitud() {
        return this.oficinaBeanModel.getLongitud();
    }

    /**
     * @param longitud the longitud to set
     */
    public void setLongitud(String longitud) {
        this.oficinaBeanModel.setLongitud(longitud);
    }

    /**
     * @return the listaPais
     */
    public List<SelectItem> getListaPais() {
        return this.oficinaBeanModel.getListaPais();
    }

    /**
     * @param listaPais the listaPais to set
     */
    public void setListaPais(List<SelectItem> listaPais) {
        this.oficinaBeanModel.setListaPais(listaPais);
    }

    /**
     * @return the listaEstado
     */
    public List<SelectItem> getListaEstado() {
        return this.oficinaBeanModel.getListaEstado();
    }

    /**
     * @param listaEstado the listaEstado to set
     */
    public void setListaEstado(List<SelectItem> listaEstado) {
        this.oficinaBeanModel.setListaEstado(listaEstado);
    }

    /**
     * @return the listaCiudad
     */
    public List<SelectItem> getListaCiudad() {
        return this.oficinaBeanModel.getListaCiudad();
    }

    /**
     * @param listaCiudad the listaCiudad to set
     */
    public void setListaCiudad(List<SelectItem> listaCiudad) {
        this.oficinaBeanModel.setListaCiudad(listaCiudad);
    }

    /**
     * @return the dataModelAnlistas
     */
    public DataModel getDataModelAnlistas() {
        return oficinaBeanModel.getDataModelAnlistas();
    }

    /**
     * @param dataModelAnlistas the dataModelAnlistas to set
     */
    public void setDataModelAnlistas(DataModel dataModelAnlistas) {
        oficinaBeanModel.setDataModelAnlistas(dataModelAnlistas);
    }

    /**
     * @return the mapaLista
     */
    public Map<String, DataModel> getMapaLista() {
        return oficinaBeanModel.getMapaLista();
    }

    /**
     * @param mapaLista the mapaLista to set
     */
    public void setMapaLista(Map<String, DataModel> mapaLista) {
        oficinaBeanModel.setMapaLista(mapaLista);
    }

    public void traerBitacoraSanitario() {
        oficinaBeanModel.traerBitacoraSanitario();
    }

}
