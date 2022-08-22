/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.estancia.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import sia.modelo.Gerencia;
import sia.modelo.SgDetalleSolicitudEstancia;
import sia.modelo.SgHotel;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgHuespedStaff;
import sia.modelo.SgInvitado;
import sia.modelo.SgMotivo;
import sia.modelo.SgOficina;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.estancia.vo.SgHuespedStaffVo;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.modelo.sgl.vo.DetalleSolicitudVO;
import sia.modelo.sgl.vo.SgHuespedHotelVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.sgl.estancia.bean.model.EstanciaBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
@Named(value = "_old_estanciaBean")
@RequestScoped
public class EstanciaBean implements Serializable {

    @ManagedProperty(value = "#{estanciaBeanModel}")
    private EstanciaBeanModel estanciaBeanModel;
    @Getter
    @Setter
    private UploadedFile fileInfo;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    public EstanciaBean() {
    }
//
//    /**
//     * Opción solo permitada para: Gerentes SGL_RESPONSABLE SGL_ADMINISTRA
//     * RESPONSABLE_CAPACITACION
//     *
//     * @return
//     */
//    public String goToSolicitudEstancia() {
//        return "/vistas/sgl/estancia/solicitudEstancia";
//    }
//
//    public String goToRegistroHuesped() {
//
//        estanciaBeanModel.beginConversationRegistroHuesped();
//        return "/vistas/sgl/estancia/registroHuesped";
//    }
//
//    public String goToAsignarHabitacion() {
//        LOGGER.info("EstanciaBean.goToAsignarHabitacion()");
//        //Limpiando variables
//        estanciaBeanModel.setListaDetalleSolicitud(null);
//        //Dándole memoria a Solicitud de Estancia
//        estanciaBeanModel.setSgSolicitudEstanciaVo((SgSolicitudEstanciaVo) estanciaBeanModel.getListaSolicitud().getRowData());
//        return "/vistas/sgl/estancia/asignarHabitacion";
//    }
//
//    public String goToCambioHabitacionStaff() {
//        LOGGER.info("EstanciaBean");
//        estanciaBeanModel.beginConversacionCambioHabitacionStaffHuesped();
//        //Estableciendo nombres de Popups
//        estanciaBeanModel.controlaPopUpFalso("popupCambiarHuesped");
//        estanciaBeanModel.controlaPopUpFalso("popupSalirHuespedStaff");
//        estanciaBeanModel.controlaPopUpFalso("popupCancelarHospedajeStaff");
//        estanciaBeanModel.controlaPopUpFalso("popupEstablecerFechaSalidaHuespedStaffBase");
//        return "/vistas/sgl/estancia/cambioHabitacionStaff";
//    }
//
//    public void chargeServiciosHotelFacturaEmpresaValueChangeListener(ValueChangeEvent valueChangeEvent) {
//        this.estanciaBeanModel.setIdHotel(((Integer) valueChangeEvent.getNewValue()));
//        setServiciosHotelFacturaEmpresa(new ListDataModel(this.estanciaBeanModel.getallServiciosHotelFacturaEmpresa()));
//    }
//
//    /**
//     * Este método limpia el valor de un Componente HTML
//     *
//     * @param nombreFormulario
//     * @param nombreComponente
//     */
//    public void clearComponent(String nombreFormulario, String nombreComponente) {
//        log("Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
//        try {
//            FacesContext context = FacesContext.getCurrentInstance();
//            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
//            UIComponent parentComponent = component.getParent();
//            parentComponent.getChildren().clear();
//        } catch (Exception e) {
//            log("Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
//        }
//    }
//
//    /**
//     * Devuelve el estado de un popup 'true' si el popup está visible y 'false'
//     * si no lo está utilizando para ello el id del popup o 'llave'. Si el popup
//     * o 'llave' no existe, entonces devuelve 'false'
//     *
//     * Modificadores: SKLM (25/Oct/2013)
//     *
//     * @author Seth Karim Luis Martínez (25/Octubre/2013)
//     * @param llave
//     * @return
//     */
//    public boolean obtenerEstadoPopup(String llave) {
//        return this.estanciaBeanModel.obtenerEstadoPopup(llave);
//    }
//
//    public int getCalculoDiasEstancia() {
//        return estanciaBeanModel.calculoDiasEstancia();
//    }
//
//    public int getTotalSgSolicitudEstanciaByUsuario() {
//        return this.estanciaBeanModel.totalSgSolicitudEstancia(-1, 1, false);
//    }
//
//    public void valorStatusRegistro(ActionEvent event) {
//        estanciaBeanModel.setStatus(10);
//        estanciaBeanModel.setListaSolicitud(null);
//    }
//
//    public List<SelectItem> getGerenciaByApCampoAndResponsableSelectItem() {
//        List<GerenciaVo> list = estanciaBeanModel.getGerenciaByApCampoAndResponsableList();
//        List<SelectItem> gList = new ArrayList<SelectItem>();
//
//        for (GerenciaVo g : list) {
//            SelectItem item = new SelectItem(g.getId(), g.getNombre());
//            gList.add(item);
//        }
//        return gList;
//    }
//
//    public void cerrarPopSolicitud(ActionEvent event) {
//        estanciaBeanModel.setSgSolicitudEstanciaVo(null);
//        estanciaBeanModel.setSgMotivo(null);
//        estanciaBeanModel.setPopUp(false);
//        estanciaBeanModel.setCrearPop(false);
//        estanciaBeanModel.setModificarPop(false);
//        estanciaBeanModel.setListaSolicitud(null);
//        estanciaBeanModel.setIdGerencia(-1);
//        estanciaBeanModel.setIdOficina(-1);
//        estanciaBeanModel.setIdMotivo(-1);
//        clearComponent("popupCrearEditarSolicitudEstancia", "fechaInicio");
//        clearComponent("popupCrearEditarSolicitudEstancia", "fechaFin");
//    }
//
//    //DETALLE DE LA SOLICITUD
//    public void solicitarEstancia(ActionEvent event) {
//        SgSolicitudEstanciaVo vo = (SgSolicitudEstanciaVo) getListaEstancia().getRowData();
//        estanciaBeanModel.setSgSolicitudEstanciaVo(this.estanciaBeanModel.findSgSolicitudEstanciaById(vo.getId()));
//        if (estanciaBeanModel.getSgSolicitudEstanciaVo().getDetalle().size() < 1) {
//            FacesUtils.addErrorMessage("Imposible solicitar una Solicitud de Estancia sin integrantes.");
//        } else if (estanciaBeanModel.comparaFecha() == -1) {
//            FacesUtils.addErrorMessage("No es posible solicitar una estancia con fecha anterior a hoy");
//        } else {
//            estanciaBeanModel.setSolicitaPop(true);
//        }
//    }
//
//    public void cerrarEnvioSolicitud(ActionEvent event) {
//        estanciaBeanModel.setSolicitaPop(false);
//        estanciaBeanModel.setSgSolicitudEstanciaVo(null);
//    }
//
//    public void modificarSolicitud(ActionEvent event) {
//        SgSolicitudEstanciaVo vo = (SgSolicitudEstanciaVo) estanciaBeanModel.getListaEstancia().getRowData();
//        estanciaBeanModel.setSgSolicitudEstanciaVo(this.estanciaBeanModel.findSgSolicitudEstanciaById(vo.getId()));
//        estanciaBeanModel.setIdMotivo(estanciaBeanModel.getSgSolicitudEstanciaVo().getIdSgMotivo());
//        estanciaBeanModel.setPopUp(true);
//        estanciaBeanModel.setModificarPop(true);
//    }
//
//    public void verDetalleSolicitud(ActionEvent event) {
//        try {
//            SgSolicitudEstanciaVo vo = (SgSolicitudEstanciaVo) estanciaBeanModel.getListaEstancia().getRowData();
//            estanciaBeanModel.setSgSolicitudEstanciaVo(this.estanciaBeanModel.findSgSolicitudEstanciaById(vo.getId()));
//            estanciaBeanModel.traerDetalleSolicitud();
//        } catch (Exception ex) {
//            Logger.getLogger(EstanciaBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public DataModel getTrearSolicitudEstancia() {
//        try {
//            return estanciaBeanModel.trearSolicitudEstancia();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public void agregarMotivo(ActionEvent event) {
//        estanciaBeanModel.setSgMotivo(new SgMotivo());
//    }
//
//    public void completarMotivo(ActionEvent event) {
//        if (estanciaBeanModel.getSgMotivo().getNombre().isEmpty()) {
//            FacesUtils.addErrorMessage("Es necesario agregar el motivo");
//        } else if (estanciaBeanModel.buscarMotivoPorNombre() != null) {
//            FacesUtils.addErrorMessage("El motivo ya existe, por favor intente con otro nombre");
//        } else {
//            estanciaBeanModel.completarMotivo();
//            estanciaBeanModel.setSgMotivo(null);
//        }
//    }
//
//    public void cerrarMotivo(ActionEvent event) {
//        estanciaBeanModel.setSgMotivo(null);
//    }
//
//    public void validaFechaFin(FacesContext context, UIComponent validate, Object value) {
//        Date f = (Date) value;
//        if (f != null) {
//            Calendar cFechaFin = this.estanciaBeanModel.converterDateToCalendar(f, false);
//            if (estanciaBeanModel.getSgSolicitudEstanciaVo().getInicioEstancia() != null) {
//                Calendar cFechaInicio = this.estanciaBeanModel.converterDateToCalendar(this.estanciaBeanModel.getSgSolicitudEstanciaVo().getInicioEstancia(), false);
//                if (!this.estanciaBeanModel.validateSecondDateIsAfterOrEqualFirstDate(cFechaFin, cFechaInicio)) {
//                    log("Error fecha salida posterior");
//                    ((UIInput) validate).setValid(false);
//                    FacesUtils.addErrorMessage("popupCrearEditarSolicitudEstancia:msgCrearSolicitudEstancia", "Elija una fecha posterior a la de inicio del periodo");
//                }
//            }
//        }
//    }
//
//    public void validaFechaInicio(FacesContext context, UIComponent validate, Object value) {
//        Date f = (Date) value;
//        if (f != null) {
//            Date d;
//            Calendar calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, -1);
//            d = calendar.getTime();
//            log("Fecha" + d);
//            log("Fecha" + f);
//            if (f.compareTo(d) < 0) {
//                ((UIInput) validate).setValid(false);
//                FacesUtils.addErrorMessage("popupCrearEditarSolicitudEstancia:msgCrearSolicitudEstancia", FacesUtils.getKeyResourceBundle("sistema.mensaje.error.fechaAnteriorHoy"));
//            }
//        }
//    }
//
//    public List<SelectItem> getTrearListaOficinaEstanciaViaje() {
//        return estanciaBeanModel.traerListaOficinaEstaciaViaje();
//    }
//
//    public SgTipoEspecifico getTipoEspecificoById() {
//        return estanciaBeanModel.getTipoEspecificoById(-1);
//    }
//
//    public void cambiarTipoEstancia(ValueChangeEvent valueChangeEvent) {
//        estanciaBeanModel.setIdTipoEspecifico((Integer) valueChangeEvent.getNewValue());
//        estanciaBeanModel.setUser("");
//        if (estanciaBeanModel.getSgDetalleSolicitudEstancia() != null) {
//            estanciaBeanModel.getSgDetalleSolicitudEstancia().setInvitado(null);
//        }
//        estanciaBeanModel.setInvitado("");
//        estanciaBeanModel.setMensaje("");
//        estanciaBeanModel.setUsuario(null);
//        estanciaBeanModel.setSgInvitado(null);
//    }
//
//    public DataModel getIntegrantesBySolicitudEstancia() {
//        try {
//            estanciaBeanModel.getDetalleSolicitudEstanciaBySolicitudEstancia();
//            return estanciaBeanModel.getListaDetalleSolicitud();
//        } catch (SIAException siae) {
//            FacesUtils.addErrorMessage(siae.getMessage());
//            log(siae.getMensajeParaProgramador());
//            return null;
//        } catch (Exception e) {
//            log(e.getMessage());
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//            return null;
//        }
//    }
//
//    public void modificarUsuarioDetalle(ActionEvent event) {
//        estanciaBeanModel.setSgDetalleSolicitudEstancia((DetalleEstanciaVO) estanciaBeanModel.getListaDetalleSolicitud().getRowData());
//        estanciaBeanModel.setPopUp(true);
//        estanciaBeanModel.setModificarPop(true);
//        if (estanciaBeanModel.getSgDetalleSolicitudEstancia().getIdInvitado() == 0) {
//            estanciaBeanModel.setUser(estanciaBeanModel.getSgDetalleSolicitudEstancia().getUsuario());
//        } else if (estanciaBeanModel.getSgDetalleSolicitudEstancia().getIdInvitado() != 0) {
//            estanciaBeanModel.setInvitado(estanciaBeanModel.getSgDetalleSolicitudEstancia().getInvitado());
//        }
//    }
//
//    public void eliminarUsuarioDetalle(ActionEvent event) {
//        estanciaBeanModel.setSgDetalleSolicitudEstancia((DetalleEstanciaVO) estanciaBeanModel.getListaDetalleSolicitud().getRowData());
//        estanciaBeanModel.eliminarUsuarioDetalle();
//        estanciaBeanModel.setListaDetalleSolicitud(null);
//        try {
//            estanciaBeanModel.traerDetalleSolicitud();
//        } catch (SIAException siae) {
//            FacesUtils.addErrorMessage(siae.getMessage());
//            log(siae.getMensajeParaProgramador());
//        } catch (Exception e) {
//            log(e.getMessage());
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//        }
//    }
//
////    public void agregarIntegrantesDetalle(ActionEvent event) {
////        estanciaBeanModel.setListaUsuario(null);
////        estanciaBeanModel.setIdTipoEspecifico(-1);
////        estanciaBeanModel.setCrearPop(true);
////        estanciaBeanModel.setLu(new ArrayList<DetalleSolicitudVO>());
////        estanciaBeanModel.setSgDetalleSolicitudEstancia(new DetalleEstanciaVO());
////    }
////
////    public void regresarSolicitud(ActionEvent event) {
////        estanciaBeanModel.setListaSolicitud(null);
////        estanciaBeanModel.setListaDetalleSolicitud(null);
////        estanciaBeanModel.setListaUsuario(null);
////        estanciaBeanModel.setDetalleSolicitudVO(null);
////        estanciaBeanModel.setSgSolicitudEstanciaVo(null);
////        setDataModel(new ListDataModel(this.estanciaBeanModel.findAllSgSolicitudEstanciaByUsuarioAndEstatus(Constantes.ESTATUS_SOLICITUD_ESTANCIA_PENDIENTE, false)));
////    }
//    /**
//     * Registro de huespedes en las casas staff u hotel
//     */
//    public DataModel getTrearSolicitudEstanciaParaRegistro() {
//        try {
//            return estanciaBeanModel.trearSolicitudEstanciaParaRegistro();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public void verSolicitudCompleta(ActionEvent event) {
//        estanciaBeanModel.setSgSolicitudEstanciaVo((SgSolicitudEstanciaVo) estanciaBeanModel.getListaSolicitud().getRowData());
//        estanciaBeanModel.setPopUp(true);
//    }
//
//    public DataModel getTraerDetalleSolicitudRegistro() {
//        return estanciaBeanModel.traerDetalleSolicitudRegistro();
//    }
//
//    public String goToRegistroRegistroHotel() {
//        estanciaBeanModel.controlaPopUpFalso("popupConfirmarHuespedesMismaReservacionHotel");
//        this.estanciaBeanModel.setServiciosHotelFacturaEmpresa(null);
//        estanciaBeanModel.setSgDetalleSolicitudEstancia((DetalleEstanciaVO) estanciaBeanModel.getListaDetalleSolicitud().getRowData());
//        estanciaBeanModel.setFlag(false);
//        estanciaBeanModel.setDisabled(true);
//        estanciaBeanModel.setDisabledAux(true);
//        estanciaBeanModel.setPopUp(true);
//        setSubirArchivoPop(false);
//        estanciaBeanModel.setSgHuespedHotel(new SgHuespedHotel());
//        clearComponent("popupRegistroHuespedHotel", "fechaIngresoHuespedHotel");
//        clearComponent("popupRegistroHuespedHotel", "fechaSalidaHuespedHotel");
//        clearComponent("popupRegistroHuespedHotel", "fechaSalidaHuespedHotelPropuesta");
//        return "/vistas/sgl/estancia/registroHotel";
//    }
//
//    public String goToRegistroStaff() {
//        estanciaBeanModel.setFlag(false);
//        estanciaBeanModel.setDisabled(true);
//        estanciaBeanModel.setDisabledAux(true);
//        estanciaBeanModel.setSgDetalleSolicitudEstancia((DetalleEstanciaVO) estanciaBeanModel.getListaDetalleSolicitud().getRowData());
//        estanciaBeanModel.setMrPopupRegistrarHuespedEnStaff(!estanciaBeanModel.isMrPopupRegistrarHuespedEnStaff());
//////        //
//////        estanciaBeanModel.setFechaIngresoHuesped(estanciaBeanModel.convertirFechaString(estanciaBeanModel.getSgSolicitudEstanciaVo().getInicioEstancia()));
//////        //
//////        estanciaBeanModel.setFechaSalidaHuesped(estanciaBeanModel.convertirFechaString(estanciaBeanModel.getSgSolicitudEstanciaVo().getFinEstancia()));
//////        //
//
//        clearComponent("popupRegistroHuespedStaff", "fechaIngresoHuespedStaff");
//        clearComponent("popupRegistroHuespedStaff", "fechaSalidaHuespedStaff");
//        clearComponent("popupRegistroHuespedStaff", "fechaSalidaHuespedStaffPropuesta");
//        estanciaBeanModel.controlaPopUpFalso("popupSeleccionarHabitacionStaff");
//        return "/vistas/sgl/estancia/registroStaff";
//    }
//
//    public String cancelarSolicitudRegistroHuesped() {
//        estanciaBeanModel.setSgDetalleSolicitudEstancia((DetalleEstanciaVO) estanciaBeanModel.getListaDetalleSolicitud().getRowData());
//        estanciaBeanModel.cancelarSolicitudRegistroHuesped();
//        estanciaBeanModel.traerDetalleSolicitudRegistro();
//        FacesUtils.addInfoMessage(estanciaBeanModel.getMensaje());
//
//        String url = "";
//
//        //Actualizar la Solicitud de Estancia
//        estanciaBeanModel.reloadSolicitudEstancia();
//        if (estanciaBeanModel.getSgSolicitudEstanciaVo().getNombreEstatus().equals("Asignada")) {
//            url = goToRegistroHuesped();
//            FacesUtils.addInfoMessage("Se terminaron de registrar o cancelar todos los integrantes de la Solicitud de Estancia: "
//                    + estanciaBeanModel.getSgSolicitudEstanciaVo().getCodigo());
//        }
//        estanciaBeanModel.setMensaje("");
//        return url;
//    }
//
//    public List<SelectItem> getListaHotel() {
//        return estanciaBeanModel.listaHotel();
//    }
//
//    public void traerHabitaciones(ValueChangeEvent valueChangeEvent) {
//        estanciaBeanModel.setIdHotel((Integer) valueChangeEvent.getNewValue());
//        estanciaBeanModel.buscarHotel();
//        estanciaBeanModel.listaHabitacion();
//    }
//
//    public void traerHabitacionesCambio(ValueChangeEvent valueChangeEvent) {
//        estanciaBeanModel.setId((Integer) valueChangeEvent.getNewValue());
//        estanciaBeanModel.listaHabitacionCambio();
//    }
//
//    public List<SelectItem> getListaHabitacion() {
//        estanciaBeanModel.buscarHotel();
//        return estanciaBeanModel.listaHabitacion();
//    }
//
//    public List<SelectItem> getListaHabitacionCambio() {
//        return estanciaBeanModel.listaHabitacionCambio();
//    }
//
//    public void buscarTipo(ValueChangeEvent valueChangeEvent) {
//        estanciaBeanModel.setIdHabitacion((Integer) valueChangeEvent.getNewValue());
//        estanciaBeanModel.buscarTipo();
//        estanciaBeanModel.listaTipoHuesped();
//    }
//
//    public List<SelectItem> getListaTipoHuesped() {
//        return estanciaBeanModel.listaTipoHuesped();
//    }
//
//    public List<SelectItem> getTiposHuespedes() {
//        return estanciaBeanModel.getTiposHuespedes();
//    }
//
//    public List<SelectItem> getTiposHuespedesForHotel() {
//        return estanciaBeanModel.getTiposHuespedesForHotel();
//    }
//
//    public DataModel getHuespedesHotelByNumeroReservacion() {
//        List<SgHuespedHotelVo> sgHuespedHotelList = this.estanciaBeanModel.findSgHuespedHotelByNumeroReservacion(getSgHuespedHotel().getNumeroHabitacion());
//
//        if (sgHuespedHotelList != null && !sgHuespedHotelList.isEmpty()) {
//            return new ListDataModel(sgHuespedHotelList);
//        } else {
//            return null;
//        }
//    }
//
//    public void openPopupConfirmarHuespedesMismaReservacionHotel(ActionEvent actionEvent) {
//        estanciaBeanModel.controlaPopUpTrue("popupConfirmarHuespedesMismaReservacionHotel");
////        this.sesion.getControladorPopups().put("", Boolean.TRUE);
//    }
//
//    public void closePopupConfirmarHuespedesMismaReservacionHotelForConfirmar(ActionEvent actionEvent) {
//        estanciaBeanModel.controlaPopUpFalso("popupConfirmarHuespedesMismaReservacionHotel");
//        //this.sesion.getControladorPopups().put("", Boolean.FALSE);
//
//        List<SgHuespedHotelVo> sgHuespedHotelList = this.estanciaBeanModel.findSgHuespedHotelByNumeroReservacion(getSgHuespedHotel().getNumeroHabitacion());
//
//        SgHuespedHotelVo hh = sgHuespedHotelList.get(0);
//        estanciaBeanModel.setIdHabitacion(hh.getIdSgHotelHabitacion());
//        estanciaBeanModel.setIdHotel(hh.getIdSgHotel());
//        estanciaBeanModel.setSubirArchivoPop(false); //indicar que se cerró el popup de huéspedes con mismo número de reservación
//    }
//
//    public void closePopupConfirmarHuespedesMismaReservacionHotel(ActionEvent actionEvent) {
//        estanciaBeanModel.controlaPopUpFalso("popupConfirmarHuespedesMismaReservacionHotel");
//        //this.sesion.getControladorPopups().put("popupConfirmarHuespedesMismaReservacionHotel", Boolean.FALSE);
//    }
//
//    public void validateNumeroReservacionHuespedHotel(ActionEvent actionEvent) {
//        List<SgHuespedHotelVo> sgHuespedHotelList = this.estanciaBeanModel.findSgHuespedHotelByNumeroReservacion(getSgHuespedHotel().getNumeroHabitacion());
//        log("validandoNumeroReservacionHotel");
//        if (sgHuespedHotelList != null && !sgHuespedHotelList.isEmpty()) {
//            estanciaBeanModel.setSubirArchivoPop(true);
//        }
//        openPopupConfirmarHuespedesMismaReservacionHotel(actionEvent);
//    }
//
//    public String registrarHuespedHotel() {
//        log("EstanciaBean.registrarHuespedHotel");
//
//        if (estanciaBeanModel.registrarHuespedHotel()) {
//            FacesUtils.addInfoMessage("El Huésped "
//                    + ((estanciaBeanModel.getSgDetalleSolicitudEstancia().getIdInvitado() == 0) ? estanciaBeanModel.getSgDetalleSolicitudEstancia().getUsuario() : estanciaBeanModel.getSgDetalleSolicitudEstancia().getInvitado())
//                    + " ha sido registrado en el Hotel " + this.estanciaBeanModel.getSgHuespedHotel().getSgHotelHabitacion().getSgHotel().getProveedor().getNombre());
//
//            estanciaBeanModel.traerDetalleSolicitudRegistro();
//            return validateSolicitudEstanciaTerminada();
//        } else {
//            FacesUtils.addErrorMessage("msgRegistroHuespedHotel", "Hubo un error al asignar el Huésped en la Habitación del hotel. Porfavor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
//            estanciaBeanModel.controlaPopUpFalso("popupConfirmarHuespedesMismaReservacionHotel");
//        }
//        return "";
//    }
//
//    public String goToEmpty() {
//        return "";
//    }
//
//    public String buscaIntegrantesSinRegistro() {
//        String cad = "";
//        int c = 0;
//        DataModel<SgDetalleSolicitudEstancia> lista = null;
////	try {
////	//    lista = estanciaBeanModel.traerDetalleSolicitud();
////	} catch (SIAException siae) {
////	    FacesUtils.addErrorMessage(siae.getMessage());
////	    log(siae.getMensajeParaProgramador());
////	} catch (Exception e) {
////	    log(e.getMessage());
////	    FacesUtils.addErrorMessage(new SIAException().getMessage());
////	}
//
//        for (SgDetalleSolicitudEstancia sgDS : lista) {
//            if (!sgDS.isRegistrado() || !sgDS.isCancelado()) {
//                c++;
//            }
//        }
//        if (c == 0) {
//            cad = "/vistas/sgl/estancia/registroHuesped";
//        }
//        return cad;
//    }
//
//    // REGISTRAR HUÉSPED - INICIO
////    public void validateFechaIngresoHuespedRegistro(FacesContext context, UIComponent validate, Object value) {
////        log("EstanciaBean.validateFechaIngresoHuespedRegistro()");
////        Date fechaIngreso = (Date) value;
////        Date fechaIngresoHSolicitudEstancia = estanciaBeanModel.getSgSolicitudEstancia().getInicioEstancia();
////
////        if (fechaIngreso != null && fechaIngresoHSolicitudEstancia != null) {
////            if (siManejoFechaLocal.compare(fechaIngreso, fechaIngresoHSolicitudEstancia) == -1) {
////                ((UIInput) validate).setValid(false);
////                FacesUtils.addErrorMessage("msgRegistroHuespedStaff", "La fecha de Ingreso debe ser mayor o igual a la fecha de Ingreso especificada en la Solicitud");
////            }
////        }
////    }
////
////    public void validateFechaSalidaHuespedRegistro(FacesContext context, UIComponent validate, Object value) {
////        log("EstanciaBean.validateFechaSalidaHuespedRegistro()");
////        Date fechaSalida = (Date) value;
////        Date fechaIngreso = estanciaBeanModel.getFechaIngresoHuesped();
////
////        if (fechaIngreso != null && fechaSalida != null) {
////            if (siManejoFechaLocal.compare(fechaSalida, fechaIngreso) == -1) {
////                ((UIInput) validate).setValid(false);
////                FacesUtils.addErrorMessage("msgRegistroHuespedStaff", "La fecha de Salida debe ser mayor o igual a la fecha de Ingreso");
////            }
////        }
////    }
////    public void validateFechaSalidaPropuestaHuespedRegistro(FacesContext context, UIComponent validate, Object value) {
////        log("EstanciaBean.validateFechaSalidaPropuestaHuespedRegistro()");
////        Date fechaSalidaPropuesta = (Date) value;
////        Date fechaIngreso = estanciaBeanModel.getFechaIngresoHuesped();
////
////        if (fechaSalidaPropuesta != null && fechaIngreso != null) {
////            if (siManejoFechaLocal.compare(fechaSalidaPropuesta, fechaIngreso) == -1) {
////                ((UIInput) validate).setValid(false);
////                FacesUtils.addErrorMessage("msgRegistroHuespedStaff", "La fecha de Salida Propuesta debe ser mayor o igual a la fecha de Ingreso");
////            }
////        }
////    }
//    // REGISTRAR HUÉSPED - FIN
//    public void validaFechaRegistro(FacesContext context, UIComponent validate, Object value) {
//        Date f = (Date) value;
//        if (estanciaBeanModel.getSgHuespedHotel().getFechaIngreso() != null) {
//            if (f.compareTo(estanciaBeanModel.getSgHuespedHotel().getFechaIngreso()) < 0) {
//                ((UIInput) validate).setValid(false);
//                FacesMessage msg = new FacesMessage("Elija una fecha posterior a la de inicio del periodo");
//                context.addMessage(validate.getClientId(context), msg);
//            }
//        }
//    }
//
////    public void validateFechaRealSalida(FacesContext context, UIComponent validate, Object value) {
////        Date fechaRealSalida = (Date) value;
////        Date fechaRealIngreso = this.estanciaBeanModel.getFechaRealIngresoHuesped();
////
////        log("FechaRealIngreso: " + fechaRealIngreso);
////        log("FechaRealSalida: " + fechaRealSalida);
////
////        if (fechaRealIngreso != null && fechaRealSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaRealSalida, fechaRealIngreso) == -1) {
////                ((UIInput) validate).setValid(false);
////                FacesUtils.addErrorMessage("msgCambioHuespedStaff", "La fecha Real de Salida debe ser igual o mayor a la fecha Real de Ingreso");
////            }
////        }
////    }
////    public void validateFechaSalidaHuespedStaffBase(FacesContext context, UIComponent validate, Object value) {
////        Date fechaSalidaHuespedStaffBase = (Date) value;
////
////        Date fechaMinimaSalida = new Date(); //La fecha de salida de un Huésped de Base se establecerá con un mínimo de 15 días de anticipación
////        fechaMinimaSalida = siManejoFechaLocal.fechaSumarDias(fechaMinimaSalida, 15);
////
////        if (siManejoFechaLocal.compare(fechaSalidaHuespedStaffBase, fechaMinimaSalida) == -1) {
////            ((UIInput) validate).setValid(false);
////            FacesUtils.addErrorMessage("msgEstablecerFechaSalidaHuespedStaffBase", "La fecha de Salida debe ser mayor o igual a 15 días a partir de hoy");
////        }
////    }
////
////    public void validateFechaIngresoCambio(FacesContext context, UIComponent validate, Object value) {
//////        log("validateFechaIngresoCambio");
////        Date fechaIngreso = (Date) value;
////        Date fechaRealSalidaHuesped = this.estanciaBeanModel.getFechaRealSalidaHuesped();
////
////        if (fechaIngreso != null && fechaRealSalidaHuesped != null) {
////            if (this.siManejoFechaLocal.compare(fechaIngreso, fechaRealSalidaHuesped) == -1) {
////                ((UIInput) validate).setValid(false);
////                FacesUtils.addErrorMessage("msgCambioHuespedStaff", "La fecha de Ingreso debe ser posterior a la fecha Real de Salida");
////            }
////        }
////    }
////
////    public void validateFechaSalidaCambio(FacesContext context, UIComponent validate, Object value) {
////        Date fechaSalida = (Date) value;
////        Date fechaIngreso = this.estanciaBeanModel.getFechaIngresoHuesped();
////
////        if (fechaIngreso != null && fechaSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaSalida, fechaIngreso) == -1) {
////                ((UIInput) validate).setValid(false);
////                FacesUtils.addErrorMessage("msgCambioHuespedStaff", "La fecha de Salida debe ser posterior a la fecha de Ingreso");
////            }
////        }
////    }
////    public void validaFechaSalida(FacesContext context, UIComponent validate, Object value) {
////        Date fechaRealSalida = (Date) value;
////        Date fechaRealIngreso = estanciaBeanModel.getSgHuespedHotel().getFechaRealIngreso();
////
////        if (estanciaBeanModel.getSgHuespedHotel().getFechaIngreso() != null) {
////            if (siManejoFechaLocal.compare(fechaRealSalida, fechaRealIngreso) == -1) {
////                ((UIInput) validate).setValid(false);
////                FacesUtils.addErrorMessage("msgSalidaHuespedHotel", "La fecha  Real de Salida debe ser posterior a la fecha Real de Ingreso");
////            }
////        }
////    }
////
////    public void validaNuevaIngreso(FacesContext context, UIComponent validate, Object value) {
////        Date fechaRealSalida = (Date) value;
////        Date fechaPropuesta = estanciaBeanModel.getFechaSalidaPropuesta();
////        if (estanciaBeanModel.getSgHuespedHotel().getFechaIngreso() != null) {
////            if (siManejoFechaLocal.compare(estanciaBeanModel.getSgHuespedHotel().getFechaIngreso(), fechaPropuesta) == -1) {
////                ((UIInput) validate).setValid(false);
////                FacesUtils.addErrorMessage("La fecha seleccionada debe ser mayor a la fecha de ingreso..");
////            }
////        }
////    }
//    public void validaFechaCambioHotel(FacesContext context, UIComponent validate, Object value) {
//        Date f = (Date) value;
//        if (estanciaBeanModel.getSgHuespedHotelSeleccionado().getFechaIngreso() != null) {
//            if (f.compareTo(estanciaBeanModel.getSgHuespedHotelSeleccionado().getFechaIngreso()) < 0) {
//                ((UIInput) validate).setValid(false);
//                FacesMessage msg = new FacesMessage("Elija una fecha posterior a la de inicio del periodo");
//                context.addMessage(validate.getClientId(context), msg);
//            }
//        }
//    }
//
////    public void validateFechaIngresoCambioHotelToHotel(FacesContext context, UIComponent validate, Object value) {
////        Date fechaIngreso = (Date) value;
////        Date fechaRealSalidaHuesped = estanciaBeanModel.getSgHuespedHotel().getFechaRealSalida();
////
////        if (siManejoFechaLocal.compare(fechaIngreso, fechaRealSalidaHuesped) == -1) {
////            ((UIInput) validate).setValid(false);
////            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Ingreso debe ser posterior a la fecha Real de Salida");
////        }
////    }
////
////    public void validateFechaSalidaCambioHotelToHotel(FacesContext context, UIComponent validate, Object value) {
////        Date fechaSalida = (Date) value;
////        Date fechaIngreso = estanciaBeanModel.getSgHuespedHotelSeleccionado().getFechaIngreso();
////
////        if (siManejoFechaLocal.compare(fechaSalida, fechaIngreso) == -1) {
////            ((UIInput) validate).setValid(false);
////            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Salida debe ser posterior a la fecha de Ingreso");
////        }
////    }
////
////    public void validateFechaSalidaCambioHotelToStaff(FacesContext context, UIComponent validate, Object value) {
////        Date fechaSalida = (Date) value;
////        Date fechaIngreso = estanciaBeanModel.getHuespedStaff().getFechaIngreso();
////
////        if (siManejoFechaLocal.compare(fechaSalida, fechaIngreso) == -1) {
////            ((UIInput) validate).setValid(false);
////            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Salida debe ser posterior a la fecha de Ingreso");
////        }
////    }
//    public String backToAsignarHabitacionFromRegistroHotel() {
//        estanciaBeanModel.setPopUp(false);
//        estanciaBeanModel.setDisabled(false);
//        estanciaBeanModel.setFlag(false);
//        estanciaBeanModel.setMensaje("");
//        estanciaBeanModel.setIdStaff(-1);
//        estanciaBeanModel.setIdHotel(-1);
//        estanciaBeanModel.setIdHabitacion(-1);
//        estanciaBeanModel.setIdTipoEspecifico(-1);
//        estanciaBeanModel.setHabitacion(null);
//        estanciaBeanModel.setTipoEspecifico(null);
//        estanciaBeanModel.setSgHuespedHotel(null);
//        estanciaBeanModel.setFechaIngresoHuesped(null);
//        estanciaBeanModel.setFechaSalidaHuesped(null);
//        estanciaBeanModel.setFechaSalidaPropuesta(null);
//        estanciaBeanModel.setHabitacionesStaffDataModel(null);
//        estanciaBeanModel.setSgDetalleSolicitudEstancia(null);
//        this.estanciaBeanModel.setServiciosHotelFacturaEmpresa(null);
//
//        return "/vistas/sgl/estancia/asignarHabitacion";
//    }
//
//    public String backToAsignarHabitacionFromRegistroStaff() {
//        estanciaBeanModel.setDisabled(false);
//        estanciaBeanModel.setFlag(false);
//        estanciaBeanModel.setMensaje("");
//        estanciaBeanModel.setIdStaff(-1);
//        estanciaBeanModel.setIdTipoEspecifico(-1);
//        estanciaBeanModel.setHabitacion(null);
//        estanciaBeanModel.setTipoEspecifico(null);
//        estanciaBeanModel.setFechaIngresoHuesped(null);
//        estanciaBeanModel.setFechaSalidaHuesped(null);
//        estanciaBeanModel.setFechaSalidaPropuesta(null);
//        estanciaBeanModel.setHabitacionesStaffDataModel(null);
//        estanciaBeanModel.setSgDetalleSolicitudEstancia(null);
//        estanciaBeanModel.setStaffListSelectItem(null);
//        estanciaBeanModel.setMrPopupRegistrarHuespedEnStaff(!estanciaBeanModel.isMrPopupRegistrarHuespedEnStaff());
//
//        return "/vistas/sgl/estancia/asignarHabitacion";
//    }
//
//    public void openPopupUpdateServiciosHuesped(ActionEvent actionEvent) {
//        estanciaBeanModel.setServiciosHotelFacturaEmpresa(null);
//        estanciaBeanModel.controlaPopUpTrue("popupModificarRegistroHuespedHotel");
////        sesion.getControladorPopups().put("", Boolean.TRUE);
//        this.estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) this.estanciaBeanModel.getLista().getRowData());
//        estanciaBeanModel.setServiciosHotelFacturaEmpresa(new ListDataModel(this.estanciaBeanModel.getAllServiciosFacturaEmpresa()));
//    }
//
//    public void closePopupUpdateServiciosHuesped(ActionEvent actionEvent) {
//        estanciaBeanModel.setServiciosHotelFacturaEmpresa(null);
//        this.estanciaBeanModel.setSgHuespedHotel(null);
//        estanciaBeanModel.controlaPopUpFalso("popupModificarRegistroHuespedHotel");
////        sesion.getControladorPopups().put("", Boolean.FALSE);
//    }
//
//    public void updateServiciosHuespedHotel(ActionEvent actionEvent) {
//        this.estanciaBeanModel.updateServicios();
//        closePopupUpdateServiciosHuesped(actionEvent);
//    }
//
//    /**
//     * **
//     * Salida de huespedes de hotel y staff-house
//     */
//    public String salidaStaff() {
//        estanciaBeanModel.iniciarConvesacionSalidaHuesped();
//        return "/vistas/sgl/estancia/salidaHuespedStaff";
//    }
//
//    public String salidaHotel() {
//        estanciaBeanModel.iniciarConvesacionSalidaHuesped();
//        estanciaBeanModel.controlaPopUpFalso("popupServicioCartaHuesped");
//        estanciaBeanModel.controlaPopUpFalso("popupModificarRegistroHuespedHotel");
//        //sesion.getControladorPopups().put("popupServicioCartaHuesped", Boolean.FALSE);
//        //sesion.getControladorPopups().put("popupModificarRegistroHuespedHotel", Boolean.FALSE);
//        if (estanciaBeanModel.getSgHotel() != null) {
//            estanciaBeanModel.setSgHotel(null);
//        }
//        return "/vistas/sgl/estancia/salidaHuespedHotel";
//    }
//
//    public void buscarHospedadosPorHotel(ValueChangeEvent valueChangeEvent) {
//        estanciaBeanModel.setIdHotel((Integer) valueChangeEvent.getNewValue());
//        estanciaBeanModel.setLista(null);
//        estanciaBeanModel.traerHospedadosHotel();
//        estanciaBeanModel.buscarHotel();
//    }
//
//    public DataModel getTraerHospedadosHotel() {
//        try {
//            return estanciaBeanModel.traerHospedadosHotel();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    /*
//     * MODIFICAR FECHA DE SALIDA DE HUIESPED EN HOTEL
//     */
//    public void modificarFechaSalidaHuespedHotelProlongado(ActionEvent event) {
//
////        if (validarCambioFechaStaffHotel(estanciaBeanModel.getSgHuespedHotel().getFechaIngreso())) {
//        estanciaBeanModel.actualizarFechaSalidaHuespedHotel();
//        PrimeFaces.current().executeScript("myPanelModificarFechaHotel.hide();");
////        }
//    }
//
//    public void modificarFechaSalidaHuespedStaffProlongado(ActionEvent event) {
////        if (validarCambioFechaStaffHotel(estanciaBeanModel.getHuespedStaff().getFechaIngreso())) {
//        estanciaBeanModel.actualizarFechaSalidaHuespedStaff();
//        PrimeFaces.current().executeScript("myPanelModificarFechaStaff.hide();");
////        }
//    }
////    private boolean validarCambioFechaStaffHotel(Date Fecha) {
////        boolean retorno = true;
////        if (estanciaBeanModel.getFechaSalidaPropuesta() == null) {
////            FacesUtils.addErrorMessage("Por favor seleccione una fecha del calendario");
////            retorno = false;
////        } else {
////            if (estanciaBeanModel.validarFechaCompare(getFechaSalidaPropuesta(), (new Date())) == -1) {
////                FacesUtils.addErrorMessage("La fecha seleccionada debe ser mayor a hoy..");
////                retorno = false;
////            } else {
////                if (siManejoFechaLocal.compare(getFechaSalidaPropuesta(), Fecha) == -1) {
////                    FacesUtils.addErrorMessage("La fecha seleccionada debe ser mayor a la fecha de ingreso..");
////                    retorno = false;
////                }
////            }
////        }
////        return retorno;
////    }
//
//    public void seleccionarFechaSalidaHuespedHotelProlongado(ActionEvent event) {
//        estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) estanciaBeanModel.getLista().getRowData());
//        estanciaBeanModel.setFechaSalidaPropuesta(estanciaBeanModel.getSgHuespedHotel().getFechaSalida());
//        PrimeFaces.current().executeScript("myPanelModificarFechaHotel.show();");
//    }
//
//    public void seleccionarFechaSalidaHuespedStaffProlongado(ActionEvent event) {
//        estanciaBeanModel.setHuespedStaff((SgHuespedStaff) estanciaBeanModel.getDataModel().getRowData());
//
//        estanciaBeanModel.setFechaSalidaPropuesta(estanciaBeanModel.getHuespedStaff().getFechaSalida());
//        PrimeFaces.current().executeScript("myPanelModificarFechaStaff.show();");
//    }
//
//    /**
//     *
//     */
//    public void marcarSalidaHuesped(ActionEvent event) {
//        estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) estanciaBeanModel.getLista().getRowData());
//        //saber si la solicitud del huesped que se intenta sacar proviene de una solicitud de viaje..
//        //SI - traer el estado actual de la ruta de la solicitud de viaje
//        //Si es semaforo negro ? No sacar : Sacar sin problemas el huesped
//        //NO - Sacar sin problemas el huesped
//
//        if (estanciaBeanModel.getSgHuespedHotel().getSiAdjunto() == null) {//saida de huésped modificacion ayer noche
//            FacesUtils.addErrorMessage("No es posible registrar la salida del huésped; se necesita la Carta de reservación de hospedaje");
//            estanciaBeanModel.setSgHuespedHotel(null);
//        } else {
//            estanciaBeanModel.getSgHuespedHotel().setFechaRealIngreso(estanciaBeanModel.getSgHuespedHotel().getFechaIngreso());
//            estanciaBeanModel.getSgHuespedHotel().setFechaRealSalida(new Date());
////            estanciaBeanModel.getSgHuespedHotel().setFechaRealSalida(estanciaBeanModel.getSgHuespedHotel().getFechaSalida());
//            estanciaBeanModel.setPopUp(true);
//        }
//    }
//
//    public void completarMarcaSalidaHuesped(ActionEvent event) {
//        estanciaBeanModel.marcarSalidaHuesped();
//        estanciaBeanModel.setLista(null);
//        estanciaBeanModel.setPopUp(false);
//        estanciaBeanModel.setSgHuespedHotel(null);
//    }
//
//    public void cerrarPopSalidaHuesped(ActionEvent event) {
//        estanciaBeanModel.setPopUp(false);
//        estanciaBeanModel.setSgHuespedHotel(null);
//        clearComponent("popupSalidaHuespedHotel", "fechaRealIngreso");
//        clearComponent("popupSalidaHuespedHotel", "fechaRealSalida");
//    }
//
//    public void cancelarRegistroHuesped(ActionEvent event) {
//        estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) estanciaBeanModel.getLista().getRowData());
//        estanciaBeanModel.cancelarRegistroHuesped();
//        FacesUtils.addInfoMessage("El Huésped "
//                + (estanciaBeanModel.getSgHuespedHotel().getSgDetalleSolicitudEstancia().getUsuario() != null
//                ? estanciaBeanModel.getSgHuespedHotel().getSgDetalleSolicitudEstancia().getUsuario().getNombre()
//                : estanciaBeanModel.getSgHuespedHotel().getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
//                + " fué Cancelado");
//        estanciaBeanModel.setLista(null);
//    }
//
//    public void cambiarRegistroHuesped(ActionEvent event) {
//        estanciaBeanModel.setStaffListSelectItem(null);
//        estanciaBeanModel.setId(-1);
//        estanciaBeanModel.setIdHabitacion(-1);
//        estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) estanciaBeanModel.getLista().getRowData());
//        //Cambiar de lugar al hueped
//        estanciaBeanModel.setOpcionSeleccionada("h");
//        //Asignar fechas reales de entrada y salida de la Habitación de Hotel que se deja
//        estanciaBeanModel.getSgHuespedHotel().setFechaRealIngreso(estanciaBeanModel.getSgHuespedHotel().getFechaIngreso());
//        estanciaBeanModel.getSgHuespedHotel().setFechaRealSalida(new Date());
//        //Asignando fechas de entrada y salida de la nueva habitación de hotel
//        estanciaBeanModel.setSgHuespedHotelSeleccionado(new SgHuespedHotel());
//        estanciaBeanModel.getSgHuespedHotelSeleccionado().setFechaIngreso(new Date());
//        //Si es un periodo de prueba deshabilitar la modificación de la fecha de salida
//        if (estanciaBeanModel.getSgHuespedHotel().getSgTipoEspecifico().getNombre().contains("rueba")) {
//            estanciaBeanModel.setDisabled(true);
//        }
//        estanciaBeanModel.getSgHuespedHotelSeleccionado().setFechaSalida(estanciaBeanModel.getSgHuespedHotel().getFechaSalida());
//        estanciaBeanModel.setCrearPop(true);
//    }
//
//    public void abrirPopupAsignarNumeroHabitacion(ActionEvent actionEvent) {
//        this.estanciaBeanModel.abrirPopup("popupAsignarNumeroReservacionHuespedHotel");
//        setSgHuespedHotel((SgHuespedHotel) estanciaBeanModel.getLista().getRowData());
//    }
//
//    public void cerrarPopupAsignarNumeroHabitacion(String llavePopup) {
//        this.estanciaBeanModel.cerrarPopup("popupAsignarNumeroReservacionHuespedHotel");
//        setSgHuespedHotel(null);
//        setDescripcion("");
//        clearComponent("formPopupAsignarNumeroReservacionHuespedHotel", "descripcion");
//    }
//
//    public void asignarNumeroReservacionYFechasHuespedHotel(String llavePopup) {
//        try {
//            if (this.estanciaBeanModel.validateSecondDateIsAfterOrEqualFirstDate(this.estanciaBeanModel.converterDateToCalendar(getSgHuespedHotel().getFechaSalida(), false), this.estanciaBeanModel.converterDateToCalendar(getSgHuespedHotel().getFechaIngreso(), false))) {
//                this.estanciaBeanModel.actualizarNumeroReservacionYFechasHuespedHotel();
//                cerrarPopupAsignarNumeroHabitacion(llavePopup);
//                FacesUtils.addInfoMessage(
//                        new StringBuilder().append(FacesUtils.getKeyResourceBundle("sgl.solicitudEstancia.huesped")).append(" ").append(FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria")).toString());
//            } else {
//                FacesUtils.addErrorMessage("formPopupAsignarNumeroReservacionHuespedHotel:msgsPopupAsignarNumeroReservacionHuespedHotel", FacesUtils.getKeyResourceBundle("sistema.mensaje.error.fechaRegresoAntesFechaSalida"));
//            }
//        } catch (Exception e) {
//            cerrarPopupAsignarNumeroHabitacion(llavePopup);
//            UtilLog4j.log.error(this, e.getMessage(), e);
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//        }
//    }
//
//    public DataModel getAllServiciosHotelIncluidosTarifa() {
//        return new ListDataModel(this.estanciaBeanModel.getAllServiciosHotelIncluidosTarifa());
//    }
//
//    public void openPopupGenerateCarta(javax.faces.event.ActionEvent actionEvent) {
//        this.estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) this.estanciaBeanModel.getLista().getRowData());
//        //Traer todos los servicios que facturará la Empresa (IHSA) que no están en la relación directa con Hotel
//        setServiciosHotelFacturaEmpresa(new ListDataModel(this.estanciaBeanModel.getAllServiciosFacturaEmpresa()));
//        setId(-1);
//        estanciaBeanModel.controlaPopUpTrue("popupServicioCartaHuesped");
//        //sesion.getControladorPopups().put("popupServicioCartaHuesped", Boolean.TRUE);
//    }
//
//    public void closePopupGenerateCarta(javax.faces.event.ActionEvent actionEvent) {
//        log("closePopupGenerateCarta");
//        this.estanciaBeanModel.setServiciosHotelFacturaEmpresa(null);
//        this.estanciaBeanModel.setSgHuespedHotel(null);
//        estanciaBeanModel.setId(-1);
//        estanciaBeanModel.controlaPopUpFalso("popupServicioCartaHuesped");
//        //sesion.getControladorPopups().put("popupServicioCartaHuesped", Boolean.FALSE);
//        //log("popup (close): " + this.sesion.getControladorPopups().get("popupServicioCartaHuesped"));
//    }
//
//    public void closePopupGenerateCartaFromServlet() {
//        log("closePopupGenerateCarta");
//        this.estanciaBeanModel.setServiciosHotelFacturaEmpresa(null);
//        this.estanciaBeanModel.setSgHuespedHotel(null);
//        estanciaBeanModel.setId(-1);
//        estanciaBeanModel.controlaPopUpFalso("popupServicioCartaHuesped");
//        //sesion.getControladorPopups().put("popupServicioCartaHuesped", Boolean.FALSE);
//        //log("popup (close): " + this.sesion.getControladorPopups().get("popupServicioCartaHuesped"));
//    }
//
//    public void generateCarta(ActionEvent actionEvent) {
//        int errors = 0;
//
//        if (getId() < 0) {
//            FacesUtils.addErrorMessage("popupGeneraCarta:msgsPpopupGeneraCarta", "Por favor elige quién dará Visto Bueno a la carta");
//            errors++;
//        }
//
//        if (errors == 0) {
//            this.estanciaBeanModel.updateServicios();
//            //Mandar a abrir la carta
//
////            FacesContext fc = FacesContext.getCurrentInstance();
////            HttpServletRequest req = (HttpServletRequest) fc.getExternalContext().getRequest();
////            HttpServletResponse resp = (HttpServletResponse) fc.getExternalContext().getResponse();
////
////            try {
////                GeneraCarta gc = new GeneraCarta();
////                gc.generaPDF(req, resp); //this is a servlet defined as below
////
////            } catch (Exception e) {
////                log(e.getMessage());
////                e.printStackTrace();
////            }
////            FacesContext context = FacesContext.getCurrentInstance();
////            String baseURL = context.getExternalContext().getRequestContextPath();
////            String url = baseURL + "/GeneraCarta?b=" + getSgHuespedHotel().getId() + ",Héctor Acosta Sierra";
////            try {
////                String encodeURL = context.getExternalContext().encodeResourceURL(url);
////                context.getExternalContext().redirect(encodeURL);
////                context.
////            } catch (Exception e) {
////            } finally {
////                context.responseComplete();
////            }
////            FacesContext faces = FacesContext.getCurrentInstance();
////            ExternalContext contex = faces.getExternalContext();
////            try {
////                contex.redirect("localhost:8080/ServiciosGenerales/GeneraCarta?b=" + getSgHuespedHotel().getId() + ",Héctor Acosta Sierra");
////            }
////            catch (IOException ioe) {
////                log(ioe.getMessage());
////                ioe.printStackTrace();
////            }
////            log("se supone que se abrió el servlet");
////            closePopupGenerateCarta(actionEvent);
//        }
//    }
//
//    public int generateCarta() {
//        int error = 0;
//
//        if (getId() < 0) {
//            FacesUtils.addErrorMessage("popupGeneraCarta:msgsPpopupGeneraCarta", "Por favor elige quién dará Visto Bueno a la carta");
//            return 1;
//        }
//
//        if (error == 0) {
//            this.estanciaBeanModel.updateServicios();
//            return 0;
//        }
//        return 0;
//    }
//
//    public void abrirPopSubirCartaAsignacionHotel(ActionEvent event) {
//        estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) estanciaBeanModel.getLista().getRowData());
//        estanciaBeanModel.setSubirArchivoPop(true);
//    }
//
//    public void quitarCartaAsignacion(ActionEvent event) {
//        estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) estanciaBeanModel.getLista().getRowData());
//        estanciaBeanModel.quitarCartaAsignacion();
//    }
//
//    public String getDirCarta() {
//        return estanciaBeanModel.dirCarta();
//    }
//
//    public void subirArchivoCarta(FileUploadEvent fileEvent) throws Exception {
//        boolean isValid = false;
//        fileInfo = fileEvent.getFile();
//
//        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
//        AlmacenDocumentos almacenDocumentos
//                = proveedorAlmacenDocumentos.getAlmacenDocumentos();
//
//        boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());
//
//        if (addArchivo) {
//            try {
//                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
//                documentoAnexo.setNombreBase(fileInfo.getFileName());
//                documentoAnexo.setRuta(getDirCarta());
//                almacenDocumentos.guardarDocumento(documentoAnexo);
//
//                isValid = estanciaBeanModel.guardarArchivoCarta(
//                        documentoAnexo.getNombreBase(),
//                        documentoAnexo.getRuta(),
//                        documentoAnexo.getTipoMime(),
//                        documentoAnexo.getTamanio()
//                );
//                estanciaBeanModel.traerHospedadosHotel();
//                estanciaBeanModel.setSgHuespedHotel(null);
//                estanciaBeanModel.setSubirArchivoPop(false);
//
//            } catch (SIAException e) {
//                LOGGER.error(e);
//            }
//
//        } else {
//            FacesUtils.addErrorMessage(new StringBuilder()
//                    .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
//                    .append(validadorNombreArchivo.getCaracteresNoValidos())
//                    .toString());
//        }
//
//        if (!isValid) {
//            FacesUtils.addErrorMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
//        }
//
//        fileInfo.delete();
//    }
//
//    public void uploadFile() {
//        log("upload Successfull");
//    }
//
//    public void cerrarPopSubirArchivo(ActionEvent event) {
//        estanciaBeanModel.setSgHuespedHotel(null);
//        estanciaBeanModel.setSubirArchivoPop(false);
//
//    }
//
//    public void seleccionarOpcionCanbioHuesped(ValueChangeEvent valueChangeEvent) {
//        estanciaBeanModel.setOpcionSeleccionada((String) valueChangeEvent.getNewValue());
//        if (estanciaBeanModel.getOpcionSeleccionada().equals("h")) {
//            //Asignar fechas de entrada y salida del nuevo registro de Hotel
//            estanciaBeanModel.setSgHuespedHotelSeleccionado(new SgHuespedHotel());
//            estanciaBeanModel.getSgHuespedHotelSeleccionado().setFechaIngreso(new Date());
//            estanciaBeanModel.getSgHuespedHotelSeleccionado().setFechaSalida(estanciaBeanModel.getSgHuespedHotel().getFechaSalida());
//        } else if (estanciaBeanModel.getOpcionSeleccionada().equals("s")) {
//            //Asignar fechas de entrada y salida del nuevo registro de Staff
//            estanciaBeanModel.setHuespedStaff(new SgHuespedStaff());
//            estanciaBeanModel.getHuespedStaff().setFechaIngreso(new Date());
//            estanciaBeanModel.getHuespedStaff().setFechaSalida(estanciaBeanModel.getSgHuespedHotel().getFechaSalida());
//        }
//    }
//
//    public void eliminarRegistroHuesped(ActionEvent event) {
//        estanciaBeanModel.setSgHuespedHotel((SgHuespedHotel) estanciaBeanModel.getLista().getRowData());
//        estanciaBeanModel.eliminarRegistroHuesped();
//    }
//
//    public void completarCambioHuespedHotel(ActionEvent event) {
//
//        Date fechaRealIngreso = this.estanciaBeanModel.getSgHuespedHotel().getFechaRealIngreso();
//        Date fechaRealSalida = this.estanciaBeanModel.getSgHuespedHotel().getFechaRealSalida();
//        Date fechaIngreso = this.estanciaBeanModel.getSgHuespedHotelSeleccionado().getFechaIngreso();
//        Date fechaSalida = this.estanciaBeanModel.getSgHuespedHotelSeleccionado().getFechaSalida();
//        SgTipoEspecifico tipoEspecifico = this.estanciaBeanModel.getSgHuespedHotel().getSgTipoEspecifico();
//        int errors = 0;
//
//        if (fechaRealIngreso == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Fecha Real de Ingreso es requerida");
//        }
//
//        if (fechaRealSalida == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Fecha Real de Salida es requerida");
//        }
//
//        if (fechaIngreso == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Fecha de Ingreso es requerida");
//        }
//
//        if (tipoEspecifico.getId() == 16 && fechaSalida == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Fecha de Salida es requerida");
//        }
//
////        if (fechaRealIngreso != null && fechaRealSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaRealSalida, fechaRealIngreso) == -1) {
////                errors++;
////                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha Real de Salida debe ser igual o mayor a la fecha Real de Ingreso");
////            }
////        }
////
////        if (fechaIngreso != null && fechaRealSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaIngreso, fechaRealSalida) == -1) {
////                errors++;
////                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Ingreso debe ser menor o igual a la fecha Real de Salida");
////            }
////        }
////
////        //Huésped Período de Prueba, validar que la Fecha de Ingreso < Fecha de Salida
////        if (tipoEspecifico.getId().intValue() == 15 && fechaIngreso != null && fechaSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaSalida, fechaIngreso) == -1) {
////                errors++;
////                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Ingreso debe ser menor a la fecha de Salida");
////            }
////        }
////
////        //Huésped Itinerante, validar que la Fecha de Salida > Fecha de Ingreso
////        if (tipoEspecifico.getId().intValue() == 16 && fechaIngreso != null && fechaSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaSalida, fechaIngreso) == -1) {
////                errors++;
////                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Ingreso debe ser menor o igual a la fecha de Salida");
////            }
////        }
//        if (this.estanciaBeanModel.getId() < 1) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Hotel es requerido");
//        }
//
//        if (this.estanciaBeanModel.getIdHabitacion() < 1) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Habitación es requerido");
//        }
//
//        if (this.estanciaBeanModel.getSgHuespedHotelSeleccionado().getNumeroHabitacion().isEmpty()) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "# Reservación es requerido");
//        }
//
//        if (errors == 0) {
//            if (estanciaBeanModel.registrarCambioHuespedHotel()) {
//                SgHuespedHotel huespedHotel = this.estanciaBeanModel.getSgHuespedHotelSeleccionado();
////            FacesUtils.addInfoMessage("El cambio del Huésped de Hotel a Hotel se realizó satisfactoriamente");
//                FacesUtils.addInfoMessage("El Huésped "
//                        + (huespedHotel.getSgDetalleSolicitudEstancia().getUsuario() != null ? huespedHotel.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : huespedHotel.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
//                        + " ha sido cambiado al Hotel: "
//                        + huespedHotel.getSgHotelHabitacion().getSgHotel().getProveedor().getNombre()
//                        + " con el número de reservación: "
//                        + huespedHotel.getNumeroHabitacion()
//                        + " satisfactoriamente");
//                cancelarCambioHuespedHotel(event);
//            } else {
//                FacesUtils.addErrorMessage("Ocurrió un error, contacte a sia@ihsa.mx, para contener la situación");
//            }
//        }
//    }
//
//    public void seleccionHabitacionHuesped(ActionEvent event) {
//        log("seleccionHabitacionHuesped");
//        Date fechaRealIngreso = this.estanciaBeanModel.getSgHuespedHotel().getFechaRealIngreso();
//        log("1" + fechaRealIngreso);
//        Date fechaRealSalida = this.estanciaBeanModel.getSgHuespedHotel().getFechaRealSalida();
//        log("1" + fechaRealSalida);
//        Date fechaIngreso = this.estanciaBeanModel.getHuespedStaff().getFechaIngreso();
//        log("fechaIngreso" + fechaIngreso);
//        Date fechaSalida = this.estanciaBeanModel.getHuespedStaff().getFechaSalida();
//        log("fechaSalida" + fechaSalida);
//        SgTipoEspecifico tipoEspecifico = this.estanciaBeanModel.getSgHuespedHotel().getSgTipoEspecifico();
//        log("tipoEspecifico" + tipoEspecifico);
//        int errors = 0;
//
//        if (fechaRealIngreso == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Fecha Real de Ingreso es requerida");
//        }
//
//        if (fechaRealSalida == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Fecha Real de Salida es requerida");
//        }
//
//        if (fechaIngreso == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Fecha de Ingreso es requerida");
//        }
//
//        if (tipoEspecifico.getId() == 16 && fechaSalida == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Fecha de Salida es requerida");
//        }
//
////        if (fechaRealIngreso != null && fechaRealSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaRealSalida, fechaRealIngreso) == -1) {
////                errors++;
////                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha Real de Salida debe ser igual o mayor a la fecha Real de Ingreso");
////            }
////        }
////
////        if (fechaIngreso != null && fechaRealSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaIngreso, fechaRealSalida) == -1) {
////                errors++;
////                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Ingreso debe ser menor o igual a la fecha Real de Salida");
////            }
////        }
////
////        Huésped Período de Prueba, validar que la Fecha de Ingreso < Fecha de Salida
////        if (tipoEspecifico.getId().intValue() == 15 && fechaIngreso != null && fechaSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaSalida, fechaIngreso) == -1) {
////                errors++;
////                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Ingreso debe ser menor a la fecha de Salida");
////            }
////        }
////
////        Huésped Itinerante, validar que la Fecha de Salida > Fecha de Ingreso
////        if (tipoEspecifico.getId().intValue() == 16 && fechaIngreso != null && fechaSalida != null) {
////            if (this.siManejoFechaLocal.compare(fechaSalida, fechaIngreso) == -1) {
////                errors++;
////                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "La fecha de Ingreso debe ser menor o igual a la fecha de Salida");
////            }
////        }
//        if (this.estanciaBeanModel.getIdStaff() < 1) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Staff es requerido");
//        }
//
//        if (this.estanciaBeanModel.getHabitacion() == null) {
//            errors++;
//            FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Habitación es requerida");
//        }
//
//        if (errors == 0) {
//            log("Sin eerrores");
//            if (estanciaBeanModel.guardarCambioHuespedStaff()) {
//                SgHuespedStaff huespedStaff = this.estanciaBeanModel.getHuespedStaff();
//                FacesUtils.addInfoMessage("El Huésped "
//                        + (huespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? huespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : huespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
//                        + " ha sido cambiado al Staff "
//                        + huespedStaff.getSgStaffHabitacion().getSgStaff().getNombre() + "|" + huespedStaff.getSgStaffHabitacion().getSgStaff().getNumeroStaff()
//                        + "en la Habitación "
//                        + huespedStaff.getSgStaffHabitacion().getNombre() + "|" + huespedStaff.getSgStaffHabitacion().getNumeroHabitacion()
//                        + " satisfactoriamente");
//                cancelarCambioHuespedHotel(event);
//            } else {
//                FacesUtils.addErrorMessage("msgCambioHuespedHotel", "Ocurrió un error. Favor de contactar al equipo de desarrollo del SIA");
//            }
//        }
//    }
//
//    public void cancelarCambioHuespedHotel(ActionEvent event) {
//        estanciaBeanModel.setHabitacion(null);
//        estanciaBeanModel.setHuespedStaff(null);
//        estanciaBeanModel.setSgHuespedHotel(null);
//        estanciaBeanModel.setSgHuespedHotelSeleccionado(null);
//        estanciaBeanModel.setHabitacionesStaffDataModel(null);
//
//        estanciaBeanModel.setId(-1);
//        estanciaBeanModel.setIdStaff(-1);
//        estanciaBeanModel.setIdHabitacion(-1);
//        estanciaBeanModel.setDisabled(false);
//        estanciaBeanModel.setCrearPop(false);
//
//        clearComponent("popupCambiarHuespedHotel", "pnStk");
//    }
//
//    /**
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * Registro de Huésped en Staff - INICIO * * * * * * * * * * * * * * * * * *
//     * * * * * * * * * * * * * * * * * * *
//     */
//    public DataModel getSolicitudEstanciaEnviadasDataModel() {
//        estanciaBeanModel.solicitudEstanciaByStatusEnviado();
//        return estanciaBeanModel.getListaSolicitud();
//    }
//
//    public DataModel getStaffByOficinaDataModel() {
//        estanciaBeanModel.getStaffByOficina();
//        return estanciaBeanModel.getStaffDataModel();
//    }
//
//    public List<SelectItem> getStaffSelectItem() {
//        try {
//            estanciaBeanModel.staffByOficinaList();
//            return estanciaBeanModel.getStaffListSelectItem();
//        } catch (Exception e) {
//            log(e.getMessage());
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//            return null;
//        }
//    }
//
//    public Date getFechaSalidaPeriodoPrueba() {
//        try {
//            Date fechaEntradaHuespedNormal = estanciaBeanModel.getFechaIngresoHuesped();
//            log("Fecha Ingreso Normal: " + fechaEntradaHuespedNormal);
//            Calendar fechaSalidaPropuesta = Calendar.getInstance();
//            fechaSalidaPropuesta.setTime(fechaEntradaHuespedNormal);
//            fechaSalidaPropuesta.add(Calendar.MONTH, 3);
//            estanciaBeanModel.setFechaSalidaPropuesta(fechaSalidaPropuesta.getTime());
//            return fechaSalidaPropuesta.getTime();
//
//        } catch (Exception e) {
//            e.getStackTrace();
//            return null;
//        }
//    }
//
//    public void validateTipoHuesped(ValueChangeEvent valueChangeEvent) {
//        log("EstanciaBeanModel.validateTipoHuesped()");
//        clearComponent("popupRegistroHuespedStaff", "fechaIngresoHuespedStaff");
//        clearComponent("popupRegistroHuespedStaff", "fechaSalidaHuespedStaff");
//        clearComponent("popupRegistroHuespedStaff", "fechaSalidaHuespedStaffPropuesta");
//        clearComponent("popupRegistroHuespedHotel", "fechaIngresoHuespedHotel");
//        clearComponent("popupRegistroHuespedHotel", "fechaSalidaHuespedHotel");
//        clearComponent("popupRegistroHuespedHotel", "fechaSalidaHuespedHotelPropuesta");
//
//        log("newValue: " + (Integer) valueChangeEvent.getNewValue());
//        SgTipoEspecifico tipoHuesped = estanciaBeanModel.getTipoEspecificoById(Integer.valueOf(valueChangeEvent.getNewValue().toString()));
////
//        if (tipoHuesped != null) {
//            estanciaBeanModel.setTipoEspecifico(tipoHuesped);
//            estanciaBeanModel.setIdTipoEspecifico(tipoHuesped.getId());
////
//            estanciaBeanModel.setDisabledAux(false);
//            estanciaBeanModel.setFechaIngresoHuesped(estanciaBeanModel.getSgSolicitudEstanciaVo().getInicioEstancia());
//            log("FechaIngresoHuesped: " + estanciaBeanModel.getFechaIngresoHuesped());
//
//            if (estanciaBeanModel.getIdTipoEspecifico() == 15) { //Tipo de Huésped Periodo de Prueba
////            log("Huésped - Periodo de Prueba");
//                Date dp = estanciaBeanModel.sumaFecha();
//                estanciaBeanModel.setFechaSalidaPropuesta(dp);
//                estanciaBeanModel.setDisabled(true); //Deshabilita la fecha de Salida en la vista
//                estanciaBeanModel.setFlag(true); //Renderiza la fecha de Salida Propuesta
//
//            } else if (estanciaBeanModel.getIdTipoEspecifico() == 16) { //Tipo de Huésped Itinerante
////            log("Huésped - Itinerante");
//                estanciaBeanModel.setFechaSalidaHuesped(estanciaBeanModel.getSgSolicitudEstanciaVo().getFinEstancia());
//                log("FechaSalidaHuesped: " + estanciaBeanModel.getFechaSalidaHuesped());
//                estanciaBeanModel.setFechaSalidaPropuesta(null);
//                estanciaBeanModel.setDisabled(false); //Habilita la fecha de Salida en la vista
//                estanciaBeanModel.setFlag(false); //Evita que se renderize la fecha de Salida Propuesta
//            } else if (estanciaBeanModel.getIdTipoEspecifico() == 17) {  //Tipo de Huésped Base
////            log("Huésped - Base");
//                estanciaBeanModel.setFechaSalidaHuesped(null);
//                log("FechaSalidaHuesped: " + estanciaBeanModel.getFechaSalidaHuesped());
//                estanciaBeanModel.setFechaSalidaPropuesta(null);
//                log("FechaSalidaPropuesta: " + estanciaBeanModel.getFechaSalidaPropuesta());
//                estanciaBeanModel.setDisabled(true); //Deshabilita la fecha de Salida en la vista
//                estanciaBeanModel.setFlag(false); //Evita que se renderize la fecha de Salida Propuesta
//            }
//        } else {
//            estanciaBeanModel.setFlag(false);
//            estanciaBeanModel.setDisabled(true);
//            estanciaBeanModel.setDisabledAux(true);
//            estanciaBeanModel.setFechaIngresoHuesped(null);
//            estanciaBeanModel.setFechaSalidaHuesped(null);
//            estanciaBeanModel.setFechaSalidaPropuesta(null);
//            clearComponent("popupRegistroHuespedStaff", "fechaIngresoHuespedStaff");
//            clearComponent("popupRegistroHuespedStaff", "fechaSalidaHuespedStaff");
//            clearComponent("popupRegistroHuespedStaff", "fechaSalidaHuespedStaffPropuesta");
//            clearComponent("popupRegistroHuespedHotel", "fechaIngresoHuespedHotel");
//            clearComponent("popupRegistroHuespedHotel", "fechaSalidaHuespedHotel");
//            clearComponent("popupRegistroHuespedHotel", "fechaSalidaHuespedHotelPropuesta");
//        }
//    }
//
//    public DataModel getNumHabitacionesDisponiblesByStaffDataModel() {
//        estanciaBeanModel.getNumHabitacionesDisponiblesByStaff();
//        return estanciaBeanModel.getNumHabitacionesDisponiblesByStaffDataModel();
//    }
//
//    public void cargarHabitacionesInTableByStaff(ValueChangeEvent valueChangeEvent) {
//        if (valueChangeEvent.getNewValue() != null) {
//            if (!valueChangeEvent.getNewValue().toString().equals("")) {
//                estanciaBeanModel.setIdStaff(Integer.valueOf(valueChangeEvent.getNewValue().toString()));
//                log("idStaff" + Integer.valueOf(valueChangeEvent.getNewValue().toString()));
//                if (((Integer) valueChangeEvent.getNewValue()).intValue() > 0) {
//                    estanciaBeanModel.getHabitacionesByStaff(estanciaBeanModel.getStaffById());
//                } else {
//                    estanciaBeanModel.setHabitacionesStaffDataModel(null);
//                }
//            } else {
//                FacesUtils.addErrorMessage("Por favor realize una seleccion");
//            }
//        } else {
//            FacesUtils.addErrorMessage("Por favor realize una seleccion");
//        }
//    }
//
//    public DetalleEstanciaVO getIntegranteSeleccionado() {
//        return estanciaBeanModel.getSgDetalleSolicitudEstancia();
//    }
//
//    public String registerHuespedInStaffHouse() {
//        if (!estanciaBeanModel.registrarHuespedStaff()) {
//            log("Hubo un error al registrar al Huésped");
//            FacesUtils.addErrorMessage("Hubo un error al asignar el Huésped en la Habitación del Staff. Porfavor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
//            return "";
//        } else {
//            FacesUtils.addInfoMessage("El Huésped "
//                    + ((estanciaBeanModel.getSgDetalleSolicitudEstancia().getIdInvitado() == 0) ? estanciaBeanModel.getSgDetalleSolicitudEstancia().getUsuario() : estanciaBeanModel.getSgDetalleSolicitudEstancia().getInvitado())
//                    + " ha sido asignado al Staff " + estanciaBeanModel.getHabitacion().getSgStaff().getNombre() + "-" + estanciaBeanModel.getHabitacion().getSgStaff().getNumeroStaff()
//                    + " en la Habitación " + estanciaBeanModel.getHabitacion().getNombre() + "-" + estanciaBeanModel.getHabitacion().getNumeroHabitacion());
//            estanciaBeanModel.traerDetalleSolicitudRegistro();
//            return validateSolicitudEstanciaTerminada();
//        }
//    }
//
//    public String validateSolicitudEstanciaTerminada() {
////        log("EstanciaBean.validateSolicitudEstanciaTerminada()");
//        String url = "/vistas/sgl/estancia/asignarHabitacion";
//        if (estanciaBeanModel.getSgSolicitudEstanciaVo().getIdEstatus() == Constantes.ESTATUS_ASIGNADA) {
//            url = goToRegistroHuesped();
//            FacesUtils.addInfoMessage("Se terminaron de registrar o cancelar todos los integrantes de la Solicitud de Estancia: "
//                    + estanciaBeanModel.getSgSolicitudEstanciaVo().getCodigo());
//            estanciaBeanModel.setSgSolicitudEstanciaVo(null);
//
//            estanciaBeanModel.setIdTipoEspecifico(-1);
//        }
//        log("url a regresar: " + url);
//
//        //limpiando variables
//        estanciaBeanModel.setPopUp(false);
//        estanciaBeanModel.setDisabled(false);
//        estanciaBeanModel.setFlag(false);
//        estanciaBeanModel.setMensaje("");
//        estanciaBeanModel.setIdStaff(-1);
//        estanciaBeanModel.setIdHotel(-1);
//        estanciaBeanModel.setIdHabitacion(-1);
//        estanciaBeanModel.setIdTipoEspecifico(-1);
//        estanciaBeanModel.setHabitacion(null);
//        estanciaBeanModel.setTipoEspecifico(null);
//        estanciaBeanModel.setSgHuespedHotel(null);
//        estanciaBeanModel.setFechaIngresoHuesped(null);
//        estanciaBeanModel.setFechaSalidaHuesped(null);
//        estanciaBeanModel.setFechaSalidaPropuesta(null);
//        estanciaBeanModel.setHabitacionesStaffDataModel(null);
//        estanciaBeanModel.setSgDetalleSolicitudEstancia(null);
//        this.estanciaBeanModel.setServiciosHotelFacturaEmpresa(null);
//
//        return url;
//    }
//
//    //Popups Registro de Huéspedes
//    public void mostrarPopupDetalleSolicitudEstancia(ActionEvent actionEvent) {
//        //Dándo memoria a Solicitud de Estancia
//        estanciaBeanModel.setSgSolicitudEstanciaVo((SgSolicitudEstanciaVo) estanciaBeanModel.getListaSolicitud().getRowData());
//        //Trayendo los integrantes (Detalle) de la Solicitud de Estancia
//        try {
//            estanciaBeanModel.traerDetalleSolicitud();
//            estanciaBeanModel.setMrPopupDetalleSolicitudEstancia(!estanciaBeanModel.isMrPopupDetalleSolicitudEstancia());
//        } catch (Exception e) {
//            log(e.getMessage());
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//        }
//
//    }
//
//    public void ocultarPopupDetalleSolicitudEstancia(ActionEvent actionEvent) {
//        //Limpiando variables
//        estanciaBeanModel.setSgSolicitudEstanciaVo(null);
//        estanciaBeanModel.setListaDetalleSolicitud(null);
//        estanciaBeanModel.setMrPopupDetalleSolicitudEstancia(!estanciaBeanModel.isMrPopupDetalleSolicitudEstancia());
//    }
//
//    public void mostrarPopupConfirmacionAsignacionHabitacion(ActionEvent actionEvent) {
//        log("Habitación seleccionada: " + (((SgStaffHabitacion) estanciaBeanModel.getHabitacionesStaffDataModel().getRowData()).getId()));
//        //Dándole memoria a la Habitación
//        estanciaBeanModel.setHabitacion((SgStaffHabitacion) estanciaBeanModel.getHabitacionesStaffDataModel().getRowData());
//        estanciaBeanModel.setMrPopupConfirmacionAsignacion(!estanciaBeanModel.isMrPopupConfirmacionAsignacion());
//    }
//
//    public void ocultarPopupConfirmacionAsignacionHabitacion(ActionEvent actionEvent) {
//        log("EstanciaBean.ocultarPopupConfirmacionAsignacionHabitacion()");
//        //Quitándole memoria a la Habitación
//        estanciaBeanModel.setHabitacion(null);
//        estanciaBeanModel.setMrPopupConfirmacionAsignacion(!estanciaBeanModel.isMrPopupConfirmacionAsignacion());
//    }
//
//    public void cancelarSolicitudEstancia(ActionEvent event) {
//        estanciaBeanModel.setSgSolicitudEstanciaVo((SgSolicitudEstanciaVo) estanciaBeanModel.getListaSolicitud().getRowData());
//        //Valida si no se han agregado usuarioa staff y hotel
//        DataModel<SgHuespedHotel> lh = estanciaBeanModel.traerRegistroHospedadosHotel();
//        DataModel<SgHuespedStaff> ls = estanciaBeanModel.traerHospedadosStaff();
//        estanciaBeanModel.setListaDetalleSolicitud(estanciaBeanModel.traerDetalleSolicitudRegistro());
//        if (ls.getRowCount() < 1 && lh.getRowCount() < 1) {
//            estanciaBeanModel.setEliminarPop(true);
//        } else {
//            FacesUtils.addErrorMessage("No es posible marcar como cancelada  la solicitud, debido a que ya se han registrado integrantes");
//        }
//    }
//
//    public void eliminarSE() {
//        setConCorreo(Constantes.FALSE);
//        ActionEvent event = null;
//        cancelarSolicitudEstancia(event);
//
//    }
//
//    public void completarCancelarSolicitudEstancia(ActionEvent event) {
//        try {
//            if (!estanciaBeanModel.getMensaje().isEmpty()) {
//                estanciaBeanModel.cancelarSolicitudEstancia();
//                estanciaBeanModel.setMensaje("");
//                FacesUtils.addInfoMessage("La Solicitud de Estancia " + getSgSolicitudEstanciaVo().getCodigo() + " fue cancelada satisfactoriamente");
//                estanciaBeanModel.setSgSolicitudEstanciaVo(null);
//                estanciaBeanModel.setListaDetalleSolicitud(null);
//                estanciaBeanModel.setEliminarPop(false);
//                setConCorreo(Constantes.TRUE);
//            } else {
//                FacesUtils.addErrorMessage("Es necesario agregar un motivo de cancelación");
//            }
//        } catch (Exception e) {
//            FacesUtils.addErrorMessage("Ocurrio un error al cancelar la solcitud. . .");
//            e.getStackTrace();
//        }
//
//    }
//
//    public void cerrarPorCancelaSolicitud(ActionEvent event) {
//        estanciaBeanModel.setMensaje("");
//        estanciaBeanModel.setSgSolicitudEstanciaVo(null);
//        estanciaBeanModel.setListaDetalleSolicitud(null);
//        estanciaBeanModel.setEliminarPop(false);
//    }
//
//    /**
//     * @return the mrPopupDetalleSolicitudEstancia
//     */
//    public boolean isMrPopupDetalleSolicitudEstancia() {
//        return estanciaBeanModel.isMrPopupDetalleSolicitudEstancia();
//    }
//
//    /**
//     * @return the mrPopupRegistrarHuespedEnStaff
//     */
//    public boolean isMrPopupRegistrarHuespedEnStaff() {
//        return estanciaBeanModel.isMrPopupRegistrarHuespedEnStaff();
//    }
//
//    /**
//     * @return the mrPopupRegistrarHuespedEnHotel
//     */
//    public boolean isMrPopupRegistrarHuespedEnHotel() {
//        return estanciaBeanModel.isMrPopupRegistrarHuespedEnHotel();
//    }
//
//    /**
//     * @return the mrPopupConfirmacionAsignacion
//     */
//    public boolean isMrPopupConfirmacionAsignacion() {
//        return estanciaBeanModel.isMrPopupConfirmacionAsignacion();
//    }
//
//    /**
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * Registro de Huésped en Staff - FIN * * * * * * * * * * * * * * * * * * *
//     * * * * * * * * * * * * * * * * * *
//     */
//    /**
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * Cambiar Huésped en Staff - INICIO
//     *
//     * * * * * * * * * * * * * * * * * * * *
//     * * * * * * * * * * * * * * * * * @return
//     * @return
//     */
//    public DataModel getHuespedesRegistradosEnStaffDataModel() {
//        try {
//            estanciaBeanModel.huespedesRegistradosEnStaff();
//        } catch (SIAException siae) {
//            FacesUtils.addErrorMessage(siae.getMessage());
//            log(siae.getMensajeParaProgramador());
//        } catch (Exception e) {
//            log(e.getMessage());
//            UtilLog4j.log.fatal(this, e.getMessage());
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//        }
//        return estanciaBeanModel.getDataModel();
//    }
//
//    public void changeHuesped(ActionEvent actionEvent) {
//        try {
//            Date fechaRealIngreso = this.estanciaBeanModel.getFechaRealIngresoHuesped();
//            Date fechaRealSalida = this.estanciaBeanModel.getFechaRealSalidaHuesped();
//            Date fechaIngreso = this.estanciaBeanModel.getFechaIngresoHuesped();
//            int errors = 0;
//
//            if (fechaRealIngreso == null) {
//                errors++;
//                FacesUtils.addErrorMessage("msgCambioHuespedStaff", "Fecha Real de Ingreso es requerida");
//            }
//
//            if (fechaRealSalida == null) {
//                errors++;
//                FacesUtils.addErrorMessage("msgCambioHuespedStaff", "Fecha Real de Salida es requerida");
//            }
//
//            if (fechaIngreso == null) {
//                errors++;
//                FacesUtils.addErrorMessage("msgCambioHuespedStaff", "Fecha de Ingreso es requerida");
//            }
//
//            if (estanciaBeanModel.getOpcionSeleccionada().equals("Staff")) {
//                try {
//                    if (this.estanciaBeanModel.getIdStaff() < 1) {
//                        errors++;
//                        FacesUtils.addErrorMessage("msgCambioHuespedStaff", "Staff es requerido");
//                    }
//
//                    if (this.estanciaBeanModel.getHabitacion() == null) {
//                        errors++;
//                        FacesUtils.addErrorMessage("msgCambioHuespedStaff", "Habitación es requerida");
//                    }
//
//                    if (errors == 0) {
//                        estanciaBeanModel.changeHuespedToHabitacionStaff();
//                        FacesUtils.addInfoMessage(estanciaBeanModel.getMensaje());
//                        ocultarPopupCambiarHuesped(actionEvent);
//                    }
//                } catch (SIAException siae) {
//                    FacesUtils.addErrorMessage("msgCambioHuespedStaff", siae.getMessage());
//                    log(siae.getMensajeParaProgramador());
//                } catch (Exception e) {
//                    log(e.getMessage());
//                    FacesUtils.addErrorMessage(new SIAException().getMessage());
//                    ocultarPopupCambiarHuesped(actionEvent);
//                }
//            } else if (estanciaBeanModel.getOpcionSeleccionada().equals("Hotel")) {
//                try {
//                    if (this.estanciaBeanModel.getIdHotel() < 1) {
//                        errors++;
//                        FacesUtils.addErrorMessage("msgCambioHuespedStaff", "Hotel es requerido");
//                    }
//
//                    if (this.estanciaBeanModel.getIdHabitacion() < 1) {
//                        errors++;
//                        FacesUtils.addErrorMessage("msgCambioHuespedStaff", "Habitación es requerido");
//                    }
//
//                    if (this.estanciaBeanModel.getNumeroHabitacion() < 1) {
//                        errors++;
//                        FacesUtils.addErrorMessage("msgCambioHuespedStaff", "# Reservación es requerido");
//                    }
//
//                    if (errors == 0) {
//                        estanciaBeanModel.changeHuespedToHabitacionHotel();
//                        FacesUtils.addInfoMessage(estanciaBeanModel.getMensaje());
//                        ocultarPopupCambiarHuesped(actionEvent);
//                    }
//                } catch (SIAException siae) {
//                    FacesUtils.addErrorMessage("msgCambioHuespedStaff", siae.getMessage());
//                    log(siae.getMensajeParaProgramador());
//                } catch (Exception e) {
//                    log(e.getMessage());
//                    ocultarPopupCambiarHuesped(actionEvent);
//                }
//            }
//
//        } catch (Exception e) {
//            e.getStackTrace();
//        }
//    }
//
//    public void seleccionarHabitacion(ActionEvent actionEvent) {
//        estanciaBeanModel.setHabitacion((SgStaffHabitacion) estanciaBeanModel.getHabitacionesStaffDataModel().getRowData());
//
//        if (getHabitacion().isOcupada()) {
//            log("se abrirá el popup");
//            openPopupSeleccionarHabitacion(actionEvent);
//        }
//    }
//
//    public void openPopupSeleccionarHabitacion(ActionEvent actionEvent) {
//        estanciaBeanModel.controlaPopUpTrue("popupSeleccionarHabitacionStaff");
//        //sesion.getControladorPopups().put("popupSeleccionarHabitacionStaff", Boolean.TRUE);
//    }
//
//    public void closePopupSeleccionarHabitacion(ActionEvent actionEvent) {
//        setHabitacion(null);
//        estanciaBeanModel.controlaPopUpFalso("popupSeleccionarHabitacionStaff");
////        sesion.getControladorPopups().put("popupSeleccionarHabitacionStaff", Boolean.FALSE);
//    }
//
//    public void confirmarSeleccionarHabitacion(ActionEvent actionEvent) {
//        estanciaBeanModel.controlaPopUpFalso("popupSeleccionarHabitacionStaff");
////        sesion.getControladorPopups().put("popupSeleccionarHabitacionStaff", Boolean.FALSE);
//    }
//
//    public DataModel getHuespedesByHabitacionStaff() {
//        List<SgHuespedStaffVo> huespedes = this.estanciaBeanModel.getHuespedesByHabitacionStaff();
//
//        if (huespedes != null && !huespedes.isEmpty()) {
//            return new ListDataModel(huespedes);
//        } else {
//            return null;
//        }
//    }
//
//    public void mostrarPopupCambiarHuesped(ActionEvent actionEvent) {
////        clearComponent("popupCambiarHuespedStaff", "pnStk");
//        estanciaBeanModel.setIdHotel(-1);
//        estanciaBeanModel.setIdHabitacion(-1);
//        //Dándole memoria al integrante de la Solicitud
//        estanciaBeanModel.setHuespedStaff((SgHuespedStaff) estanciaBeanModel.getDataModel().getRowData());
//
//        //Seleccionando un radio button y sus opciones por default
//        estanciaBeanModel.setOpcionSeleccionada("Staff");
//
//        estanciaBeanModel.setFechaRealIngresoHuesped(estanciaBeanModel.getHuespedStaff().getFechaIngreso());
//        estanciaBeanModel.setFechaRealSalidaHuesped(new Date());
//        estanciaBeanModel.setFechaIngresoHuesped(new Date());
//        estanciaBeanModel.setFechaSalidaHuesped(estanciaBeanModel.getHuespedStaff().getFechaSalida());
//
//        //Si es un periodo de prueba deshabilitar la modificación de la fecha de salida
//        if (estanciaBeanModel.getHuespedStaff().getSgTipoEspecifico().getNombre().contains("rueba")) {
//            estanciaBeanModel.setDisabled(true);
//        }
//
//        estanciaBeanModel.controlaPopUpTrue("popupCambiarHuesped");
////        sesion.getControladorPopups().put("popupCambiarHuesped", Boolean.TRUE);
//    }
//
//    public void ocultarPopupCambiarHuesped(ActionEvent actionEvent) {
//        //Quitando memorias
//        estanciaBeanModel.setHabitacion(null);
//        estanciaBeanModel.setHuespedStaff(null);
//        estanciaBeanModel.setFechaRealIngresoHuesped(null);
//        estanciaBeanModel.setFechaRealSalidaHuesped(null);
//        estanciaBeanModel.setFechaIngresoHuesped(null);
//        estanciaBeanModel.setFechaSalidaHuesped(null);
//        estanciaBeanModel.setHabitacionesStaffDataModel(null);
//        estanciaBeanModel.setNumeroHabitacion(0);
//        estanciaBeanModel.setMensaje("");
//        estanciaBeanModel.setIdStaff(-1);
//        estanciaBeanModel.setIdHotel(-1);
//        estanciaBeanModel.setIdHabitacion(-1);
//        estanciaBeanModel.setIdTipoEspecifico(-1);
//        estanciaBeanModel.setDisabled(false);
//        clearComponent("popupCambiarHuespedStaff", "pnStk");
//
//        estanciaBeanModel.controlaPopUpFalso("popupCambiarHuesped");
////        sesion.getControladorPopups().put("popupCambiarHuesped", Boolean.FALSE);
//    }
//
//    /**
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * Cambiar Huésped en Staff - FIN * * * * * * * * * * * * * * * * * * * * *
//     * * * * * * * * * * * * * * * *
//     */
//    /**
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * Salida de Habitación Staff - INICIO * * * * * * * * * * * * * * * * * * *
//     * * * * * * * * * * * * * * * * * *
//     */
//    public void exitHuespedStaff(ActionEvent actionEvent) {
//        try {
//            estanciaBeanModel.exitHuespedStaff();
//            FacesUtils.addInfoMessage(estanciaBeanModel.getMensaje());
//            ocultarPopupSalidaHuespedStaff(actionEvent);
//        } catch (SIAException siae) {
//            if (siae.getMessage().equals(Constantes.MENSAJE_FECHA_REAL_INGRESO_HUESPED_INVALIDA)) {
//                FacesUtils.addErrorMessage("msgSalirHabitacionStaffPopup", siae.getMessage());
//                log(siae.getMensajeParaProgramador());
//            } else {
//                FacesUtils.addErrorMessage(siae.getMessage());
//                log(siae.getMensajeParaProgramador());
//                ocultarPopupSalidaHuespedStaff(actionEvent);
//            }
//        } catch (Exception e) {
//            log(e.getMessage());
//            UtilLog4j.log.fatal(this, e.getMessage());
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//            ocultarPopupSalidaHuespedStaff(actionEvent);
//        }
//    }
//
//    public void mostrarPopupSalidaHuespedStaff(ActionEvent actionEvent) {
////        log("mostrarPopUpSalidaHuespedStaff");
//
//        //Dándole memoria al integrante de la Solicitud que va a terminar su estancia
//        estanciaBeanModel.setHuespedStaff((SgHuespedStaff) estanciaBeanModel.getDataModel().getRowData());
//
//        if (estanciaBeanModel.getHuespedStaff().getFechaSalida() != null) {
//            estanciaBeanModel.setFechaRealIngresoHuesped(estanciaBeanModel.getHuespedStaff().getFechaIngreso());
//            estanciaBeanModel.setFechaRealSalidaHuesped(new Date());
//            estanciaBeanModel.controlaPopUpTrue("popupSalirHuespedStaff");
////            sesion.getControladorPopups().put("popupSalirHuespedStaff", Boolean.TRUE);
//        } else {
//            //Proponer fecha de Salida en 15 días
////            Date hoy = new Date();
////            hoy = siManejoFechaLocal.fechaSumarDias(hoy, 15);
////            estanciaBeanModel.setFechaSalidaPropuesta(hoy);
//            estanciaBeanModel.controlaPopUpTrue("popupEstablecerFechaSalidaHuespedStaffBase");
////            sesion.getControladorPopups().put("popupEstablecerFechaSalidaHuespedStaffBase", Boolean.TRUE);
//        }
//    }
//
//    public void ocultarPopupSalidaHuespedStaff(ActionEvent actionEvent) {
////        log("ocultarPopUpSalidaHuespedStaff");
//        //Quitándole memoria al integrante de la Solicitud que termina su estancia
//        estanciaBeanModel.setHuespedStaff(null);
//        estanciaBeanModel.setFechaRealIngresoHuesped(null);
//        estanciaBeanModel.setFechaRealSalidaHuesped(null);
//        estanciaBeanModel.setFechaSalidaPropuesta(null);
//        estanciaBeanModel.setMensaje("");
//        estanciaBeanModel.controlaPopUpFalso("popupSalirHuespedStaff");
//        estanciaBeanModel.controlaPopUpFalso("popupEstablecerFechaSalidaHuespedStaffBase");
////        sesion.getControladorPopups().put("popupSalirHuespedStaff", Boolean.FALSE);
////        sesion.getControladorPopups().put("popupEstablecerFechaSalidaHuespedStaffBase", Boolean.FALSE);
//        clearComponent("popupEstablecerFechaSalidaHuespedStaffBase", "fechaSalidaHuespedStaffBasePropuesta");
//    }
//
//    /**
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * Salida de Habitación Staff - FIN * * * * * * * * * * * * * * * * * * * *
//     * * * * * * * * * * * * * * * * *
//     */
//    /**
//     *
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * Cancelar estancia del Huésped de Habitación Staff - INICIO
//     *
//     * * * * * * *
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * @return
//     */
//    public String cancelHospedajeStaff() {
//        //Dándole memoria al integrante de la Solicitud que va a terminar su estancia
//        estanciaBeanModel.setHuespedStaff((SgHuespedStaff) estanciaBeanModel.getDataModel().getRowData());
//
//        String url = "";
//
//        try {
//            estanciaBeanModel.cancelHospedajeStaff();
//            FacesUtils.addInfoMessage(estanciaBeanModel.getMensaje());
//
//            //Quitando memorias
//            estanciaBeanModel.setHuespedStaff(null);
//            estanciaBeanModel.setMensaje("");
//        } catch (SIAException siae) {
//            FacesUtils.addErrorMessage(siae.getMessage());
//            log(siae.getMensajeParaProgramador());
//            //Quitando memorias
//            estanciaBeanModel.setHuespedStaff(null);
//            estanciaBeanModel.setMensaje("");
//        } catch (Exception e) {
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//            UtilLog4j.log.fatal(this, e.getMessage());
//            //Quitando memorias
//            estanciaBeanModel.setHuespedStaff(null);
//            estanciaBeanModel.setMensaje("");
//        }
//
//        return url;
//    }
//
//    /**
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * Cancelar estancia del Huésped de Habitación Staff - FIN * * * * * * * * *
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     */
//    public void establecerFechaSalidaHuespedStaffBase(ActionEvent actionEvent) {
//        try {
//            estanciaBeanModel.establecerFechaSalidaHuespedStaffBase();
//            FacesUtils.addInfoMessage("La fecha de Salida del Huésped ha sido establecida");
//        } catch (SIAException siae) {
//            FacesUtils.addErrorMessage(siae.getMessage());
//            log(siae.getMensajeParaProgramador());
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(this, e.getMessage());
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//        } finally {
//            ocultarPopupSalidaHuespedStaff(actionEvent);
//        }
//    }
//// *******************************************     GENERA CARTA HOTEL ********************************
//
//    public DataModel getTraerAprobacionCarta() {
//        return estanciaBeanModel.traerAprobacionCarta();
//    }
//
//    public List<SelectItem> getSelectItemVistoBuenoCarta() {
//        List<UsuarioTipoVo> usuarios = this.estanciaBeanModel.getAllUserAprobarCartHuesped();
//        List<SelectItem> list = new ArrayList<SelectItem>();
//
//        for (UsuarioTipoVo u : usuarios) {
//            SelectItem item = new SelectItem(u.getId(), u.getUsuario());
//            list.add(item);
//        }
//        return list;
//    }
//
//    /**
//     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//     * PROPIEDADES (Getters y Setters) - INICIO * * * * * * * * * * * * * * * *
//     * * * * * * * * * * * * * * * * * * * * *
//     */
//    public Gerencia getGerencia() {
//        return estanciaBeanModel.getGerencia();
//    }
//
//    public DetalleSolicitudVO getDetalleSolicitudVO() {
//        return estanciaBeanModel.getDetalleSolicitudVO();
//    }
//
//    public void setDetalleSolicitudVO(DetalleSolicitudVO detalleSolicitudVO) {
//        estanciaBeanModel.setDetalleSolicitudVO(detalleSolicitudVO);
//    }
//
//    public String getDescripcion() {
//        return estanciaBeanModel.getDescripcion();
//    }
//
//    public void setDescripcion(String descripcion) {
//        estanciaBeanModel.setDescripcion(descripcion);
//    }
//
//    public String getUser() {
//        return estanciaBeanModel.getUser();
//    }
//
//    public void setUser(String user) {
//        estanciaBeanModel.setUser(user);
//    }
////autocompletar
//
//    public List<SelectItem> getListaUsuariosAlta() {
//        return estanciaBeanModel.getListaUsuariosAlta();
//    }
//
//    public List<SelectItem> getListaSelectItemInvitado() {
//        return estanciaBeanModel.getListaSelectItemInvitado();
//    }
//
//    /**
//     * @param listaSelectItemInvitado the listaSelectItemInvitado to set
//     */
//    public void setListaSelectItemInvitado(List<SelectItem> listaSelectItemInvitado) {
//        estanciaBeanModel.setListaSelectItemInvitado(listaSelectItemInvitado);
//    }
////
//
//    public void setListaUsuariosAlta(List<SelectItem> lista) {
//        estanciaBeanModel.setListaUsuariosAlta(lista);
//    }
//
//    public int getIdTipoEspecifico() {
//        return estanciaBeanModel.getIdTipoEspecifico();
//    }
//
//    public void setIdTipoEspecifico(int idTipoEspecifico) {
//        estanciaBeanModel.setIdTipoEspecifico(idTipoEspecifico);
//    }
//
//    public int getIdOficina() {
//        return estanciaBeanModel.getIdOficina();
//    }
//
//    public void setIdOficina(int idOficina) {
//        estanciaBeanModel.setIdOficina(idOficina);
//    }
//
//    public int getStatus() {
//        return estanciaBeanModel.getStatus();
//    }
//
//    public List<SelectItem> getListaSelectItem() {
//        return estanciaBeanModel.getListaSelectItem();
//    }
//
//    public void setListaSelectItem(List<SelectItem> listaSelectItem) {
//        estanciaBeanModel.setListaSelectItem(listaSelectItem);
//    }
//
//    public boolean isPopUp() {
//        return estanciaBeanModel.isPopUp();
//    }
//
//    public boolean isCrearPop() {
//        return estanciaBeanModel.isCrearPop();
//    }
//
//    public boolean isModificarPop() {
//        return estanciaBeanModel.isModificarPop();
//    }
//
//    public SgSolicitudEstanciaVo getSgSolicitudEstanciaVo() {
//        return estanciaBeanModel.getSgSolicitudEstanciaVo();
//    }
//
//    public DetalleEstanciaVO getSgDetalleSolicitudEstancia() {
//        return estanciaBeanModel.getSgDetalleSolicitudEstancia();
//    }
//
//    public Usuario getUsuario() {
//        return estanciaBeanModel.getUsuario();
//    }
//
//    public String getMensaje() {
//        return estanciaBeanModel.getMensaje();
//    }
//
//    /**
//     * @return the listaDetalleSolicitud
//     */
//    public DataModel getListaDetalleSolicitud() {
//        return estanciaBeanModel.getListaDetalleSolicitud();
//    }
//
//    /**
//     * @param listaDetalleSolicitud the listaDetalleSolicitud to set
//     */
//    public void setListaDetalleSolicitud(DataModel listaDetalleSolicitud) {
//        estanciaBeanModel.setListaDetalleSolicitud(listaDetalleSolicitud);
//    }
//
//    /**
//     * @return the habitacionesStaffDataModel
//     */
//    public DataModel getHabitacionesStaffDataModel() {
//        return estanciaBeanModel.getHabitacionesStaffDataModel();
//    }
//
//    /**
//     * @param habitacionesStaffDataModel the habitacionesStaffDataModel to set
//     */
//    public void setHabitacionesStaffDataModel(DataModel habitacionesStaffDataModel) {
//        estanciaBeanModel.setHabitacionesStaffDataModel(habitacionesStaffDataModel);
//    }
//
//    public int getIdIntegrante() {
//        return estanciaBeanModel.getIdIntegrante();
//    }
//
//    public int getIdHotel() {
//        return estanciaBeanModel.getIdHotel();
//    }
//
//    public void setIdHotel(int idHotel) {
//        estanciaBeanModel.setIdHotel(idHotel);
//    }
//
//    public void setIdIntegrante(int idIntegrante) {
//        estanciaBeanModel.setIdIntegrante(idIntegrante);
//    }
//
//    public void setIdHabitacion(int idHabitacion) {
//        estanciaBeanModel.setIdHabitacion(idHabitacion);
//    }
//
//    public int getIdHabitacion() {
//        return estanciaBeanModel.getIdHabitacion();
//    }
//
//    public int getNumeroHabitacion() {
//        return estanciaBeanModel.getNumeroHabitacion();
//    }
//
//    public void setNumeroHabitacion(int numeroHabitacion) {
//        estanciaBeanModel.setNumeroHabitacion(numeroHabitacion);
//    }
//
//    public SgHuespedHotel getSgHuespedHotel() {
//        return estanciaBeanModel.getSgHuespedHotel();
//    }
//
//    /**
//     * @return the idStaff
//     */
//    public int getIdStaff() {
//        return estanciaBeanModel.getIdStaff();
//    }
//
//    /**
//     * @param idStaff the idStaff to set
//     */
//    public void setIdStaff(int idStaff) {
//        estanciaBeanModel.setIdStaff(idStaff);
//    }
//
//    public SgHotel getSgHotel() {
//        return estanciaBeanModel.getSgHotel();
//    }
//
//    /**
//     * @return the habitacion
//     */
//    public SgStaffHabitacion getHabitacion() {
//        return estanciaBeanModel.getHabitacion();
//    }
//
//    /**
//     * @param habitacion the habitacion to set
//     */
//    public void setHabitacion(SgStaffHabitacion habitacion) {
//        estanciaBeanModel.setHabitacion(habitacion);
//    }
//
//    /**
//     * @return the fechaIngresoHuesped
//     */
//    public Date getFechaIngresoHuesped() {
//        return estanciaBeanModel.getFechaIngresoHuesped();
//    }
//
//    /**
//     * @param fechaIngresoHuesped the fechaIngresoHuesped to set
//     */
//    public void setFechaIngresoHuesped(Date fechaIngresoHuesped) {
//        estanciaBeanModel.setFechaIngresoHuesped(fechaIngresoHuesped);
//    }
//
//    /**
//     * @return the fechaSalidaHuesped
//     */
//    public Date getFechaSalidaHuesped() {
//        return estanciaBeanModel.getFechaSalidaHuesped();
//    }
//
//    /**
//     * @param fechaSalidaHuesped the fechaSalidaHuesped to set
//     */
//    public void setFechaSalidaHuesped(Date fechaSalidaHuesped) {
//        estanciaBeanModel.setFechaSalidaHuesped(fechaSalidaHuesped);
//    }
//
//    /**
//     * @return the dataModel
//     */
//    public DataModel getDataModel() {
//        return estanciaBeanModel.getDataModel();
//    }
//
//    /**
//     * @param dataModel the dataModel to set
//     */
//    public void setDataModel(DataModel dataModel) {
//        estanciaBeanModel.setDataModel(dataModel);
//    }
//
//    public SgHuespedHotel getSgHuespedHotelSeleccionado() {
//        return estanciaBeanModel.getSgHuespedHotelSeleccionado();
//    }
//
//    public void setSgHuespedHotelSeleccionado(SgHuespedHotel sgHuespedHotelSeleccionado) {
//        estanciaBeanModel.setSgHuespedHotelSeleccionado(sgHuespedHotelSeleccionado);
//    }
//
//    public String getOpcionSeleccionada() {
//        return estanciaBeanModel.getOpcionSeleccionada();
//    }
//
//    /**
//     * @param opcionSeleccionada the opcionSeleccionada to set
//     */
//    public void setOpcionSeleccionada(String opcionSeleccionada) {
//        estanciaBeanModel.setOpcionSeleccionada(opcionSeleccionada);
//    }
//
//    /**
//     * @return the id
//     */
//    public int getId() {
//        return estanciaBeanModel.getId();
//    }
//
//    /**
//     * @param id the id to set
//     */
//    public void setId(int id) {
//        log("*******************************************************************************************************************");
//        log("guardando id: " + id);
//        estanciaBeanModel.setId(id);
//    }
//
//    /**
//     * @return the huespedStaff
//     */
//    public SgHuespedStaff getHuespedStaff() {
//        return estanciaBeanModel.getHuespedStaff();
//    }
//
//    /**
//     * @param huespedStaff the huespedStaff to set
//     */
//    public void setHuespedStaff(SgHuespedStaff huespedStaff) {
//        estanciaBeanModel.setHuespedStaff(huespedStaff);
//    }
//
//    /**
//     * @return the fechaRealIngresoHuesped
//     */
//    public Date getFechaRealIngresoHuesped() {
//        return estanciaBeanModel.getFechaRealIngresoHuesped();
//    }
//
//    /**
//     * @param fechaRealIngresoHuesped the fechaRealIngresoHuesped to set
//     */
//    public void setFechaRealIngresoHuesped(Date fechaRealIngresoHuesped) {
//        estanciaBeanModel.setFechaRealIngresoHuesped(fechaRealIngresoHuesped);
//    }
//
//    /**
//     * @return the fechaRealSalidaHuesped
//     */
//    public Date getFechaRealSalidaHuesped() {
//        return estanciaBeanModel.getFechaRealSalidaHuesped();
//    }
//
//    /**
//     * @param fechaRealSalidaHuesped the fechaRealSalidaHuesped to set
//     */
//    public void setFechaRealSalidaHuesped(Date fechaRealSalidaHuesped) {
//        estanciaBeanModel.setFechaRealSalidaHuesped(fechaRealSalidaHuesped);
//    }
//
//    /**
//     * @return the eliminarPop
//     */
//    public boolean isEliminarPop() {
//        return estanciaBeanModel.isEliminarPop();
//    }
//
//    /**
//     * @param eliminarPop the eliminarPop to set
//     */
//    public void setEliminarPop(boolean eliminarPop) {
//        estanciaBeanModel.setEliminarPop(eliminarPop);
//    }
//
//    public void setMensaje(String mensaje) {
//        estanciaBeanModel.setMensaje(mensaje);
//    }
//
//    /**
//     * @return the subirArchivoPop
//     */
//    public boolean isSubirArchivoPop() {
//        return estanciaBeanModel.isSubirArchivoPop();
//    }
//
//    /**
//     * @param subirArchivoPop the subirArchivoPop to set
//     */
//    public void setSubirArchivoPop(boolean subirArchivoPop) {
//        estanciaBeanModel.setSubirArchivoPop(subirArchivoPop);
//    }
//
//    /**
//     * @return the idMotivo
//     */
//    public int getIdMotivo() {
//        return estanciaBeanModel.getIdMotivo();
//    }
//
//    /**
//     * @param idMotivo the idMotivo to set
//     */
//    public void setIdMotivo(int idMotivo) {
//        estanciaBeanModel.setIdMotivo(idMotivo);
//    }
//
//    /**
//     * @return the sgMotivo
//     */
//    public SgMotivo getSgMotivo() {
//        return estanciaBeanModel.getSgMotivo();
//    }
//
//    /**
//     * @param sgMotivo the sgMotivo to set
//     */
//    public void setSgMotivo(SgMotivo sgMotivo) {
//        estanciaBeanModel.setSgMotivo(sgMotivo);
//    }
//
//    /**
//     * @return the solicitaPop
//     */
//    public boolean isSolicitaPop() {
//        return estanciaBeanModel.isSolicitaPop();
//    }
//
//    /**
//     * @param solicitaPop the solicitaPop to set
//     */
//    public void setSolicitaPop(boolean solicitaPop) {
//        estanciaBeanModel.setSolicitaPop(solicitaPop);
//    }
//
//    /**
//     * @return the fechaSalidaPropuesta
//     */
//    public Date getFechaSalidaPropuesta() {
//        return estanciaBeanModel.getFechaSalidaPropuesta();
//    }
//
//    /**
//     * @param fechaSalidaPropuesta the fechaSalidaPropuesta to set
//     */
//    public void setFechaSalidaPropuesta(Date fechaSalidaPropuesta) {
//        estanciaBeanModel.setFechaSalidaPropuesta(fechaSalidaPropuesta);
//    }
//
//    /**
//     * @return the sugerenciaFechaSalidaHuesped
//     */
//    public String getSugerenciaFechaSalidaHuesped() {
//        return estanciaBeanModel.getSugerenciaFechaSalidaHuesped();
//    }
//
//    /**
//     * @param sugerenciaFechaSalidaHuesped the sugerenciaFechaSalidaHuesped to
//     * set
//     */
//    public void setSugerenciaFechaSalidaHuesped(String sugerenciaFechaSalidaHuesped) {
//        estanciaBeanModel.setSugerenciaFechaSalidaHuesped(sugerenciaFechaSalidaHuesped);
//    }
//
//    /**
//     * @return the tipoEspecifico
//     */
//    public SgTipoEspecifico getTipoEspecifico() {
//        return estanciaBeanModel.getTipoEspecifico();
//    }
//
//    /**
//     * @param tipoEspecifico the tipoEspecifico to set
//     */
//    public void setTipoEspecifico(SgTipoEspecifico tipoEspecifico) {
//        estanciaBeanModel.setTipoEspecifico(tipoEspecifico);
//    }
//
//    /**
//     * @return the disabled
//     */
//    public boolean isDisabled() {
//        return estanciaBeanModel.isDisabled();
//    }
//
//    /**
//     * @param disabled the disabled to set
//     */
//    public void setDisabled(boolean disabled) {
//        estanciaBeanModel.setDisabled(disabled);
//    }
//
//    /**
//     * @return the flag
//     */
//    public boolean isFlag() {
//        return estanciaBeanModel.isFlag();
//    }
//
//    /**
//     * @param flag the flag to set
//     */
//    public void setFlag(boolean flag) {
//        estanciaBeanModel.setFlag(flag);
//    }
//
//    /**
//     * @return the disabledAux
//     */
//    public boolean isDisabledAux() {
//        return estanciaBeanModel.isDisabledAux();
//    }
//
//    /**
//     * @param disabledAux the disabledAux to set
//     */
//    public void setDisabledAux(boolean disabledAux) {
//        estanciaBeanModel.setDisabledAux(disabledAux);
//    }
//
//    /**
//     * @return the staffListSelectItem
//     */
//    public List<SelectItem> getStaffListSelectItem() {
//        return estanciaBeanModel.getStaffListSelectItem();
//    }
//
//    /**
//     * @param staffListSelectItem the staffListSelectItem to set
//     */
//    public void setStaffListSelectItem(List<SelectItem> staffListSelectItem) {
//        estanciaBeanModel.setStaffListSelectItem(staffListSelectItem);
//    }
//
//    /**
//     * @return the invitado
//     */
//    public String getInvitado() {
//        return estanciaBeanModel.getInvitado();
//    }
//
//    /**
//     * @param invitado the invitado to set
//     */
//    public void setInvitado(String invitado) {
//        estanciaBeanModel.setInvitado(invitado);
//    }
//
//    /**
//     * @return the sgInvitado
//     */
//    public SgInvitado getSgInvitado() {
//        return estanciaBeanModel.getSgInvitado();
//    }
//
//    /**
//     * @param sgInvitado the sgInvitado to set
//     */
//    public void setSgInvitado(SgInvitado sgInvitado) {
//        estanciaBeanModel.setSgInvitado(sgInvitado);
//    }
//
//    /**
//     * @return the idGerencia
//     */
//    public int getIdGerencia() {
//        return estanciaBeanModel.getIdGerencia();
//    }
//
//    /**
//     * @param idGerencia the idGerencia to set
//     */
//    public void setIdGerencia(int idGerencia) {
//        estanciaBeanModel.setIdGerencia(idGerencia);
//    }
//
//    /**
//     * @return the generaCartaPop
//     */
//    public boolean isGeneraCartaPop() {
//        return estanciaBeanModel.isGeneraCartaPop();
//    }
//
//    /**
//     * @param generaCartaPop the generaCartaPop to set
//     */
//    public void setGeneraCartaPop(boolean generaCartaPop) {
//        estanciaBeanModel.setGeneraCartaPop(generaCartaPop);
//    }
//
//    /**
//     * @return the serviciosHotelFacturaEmpresa
//     */
//    public DataModel getServiciosHotelFacturaEmpresa() {
//        return this.estanciaBeanModel.getServiciosHotelFacturaEmpresa();
//    }
//
//    /**
//     * @param serviciosHotelFacturaEmpresa the serviciosHotelFacturaEmpresa to
//     * set
//     */
//    public void setServiciosHotelFacturaEmpresa(DataModel serviciosHotelFacturaEmpresa) {
//        this.estanciaBeanModel.setServiciosHotelFacturaEmpresa(serviciosHotelFacturaEmpresa);
//    }
//
//    /**
//     * @return the gerenciaCapacitacion
//     */
//    public Gerencia getGerenciaCapacitacion() {
//        return this.estanciaBeanModel.getGerenciaCapacitacion();
//    }
//
//    /**
//     * @param gerenciaCapacitacion the gerenciaCapacitacion to set
//     */
//    public void setGerenciaCapacitacion(Gerencia gerenciaCapacitacion) {
//        this.estanciaBeanModel.setGerenciaCapacitacion(gerenciaCapacitacion);
//    }
//
//    /**
//     * @return the sgOficina
//     */
//    public SgOficina getSgOficina() {
//        return this.estanciaBeanModel.getSgOficina();
//    }
//
//    /**
//     * @param sgOficina the sgOficina to set
//     */
//    public void setSgOficina(SgOficina sgOficina) {
//        this.estanciaBeanModel.setSgOficina(sgOficina);
//    }
//
//    /**
//     * @param sgSolicitudEstancia the sgSolicitudEstancia to set
//     */
//    public void setSgSolicitudEstanciaVo(SgSolicitudEstanciaVo sgSolicitudEstancia) {
//        this.estanciaBeanModel.setSgSolicitudEstanciaVo(sgSolicitudEstancia);
//    }
//
//    /**
//     * @param sgHuespedHotel the sgHuespedHotel to set
//     */
//    public void setSgHuespedHotel(SgHuespedHotel sgHuespedHotel) {
//        this.estanciaBeanModel.setSgHuespedHotel(sgHuespedHotel);
//    }
//
//    /**
//     * @param estanciaBeanModel the estanciaBeanModel to set
//     */
//    public void setEstanciaBeanModel(EstanciaBeanModel estanciaBeanModel) {
//        this.estanciaBeanModel = estanciaBeanModel;
//    }
//
//    private void log(String mensaje) {
//        UtilLog4j.log.info(this, mensaje);
//    }
//
//    /**
//     * @return the listaEstancia
//     */
//    public DataModel getListaEstancia() {
//        return estanciaBeanModel.getListaEstancia();
//    }
//
//    /**
//     * @param listaEstancia the listaEstancia to set
//     */
//    public void setListaEstancia(DataModel listaEstancia) {
//        estanciaBeanModel.setListaEstancia(listaEstancia);
//    }
//
//    /**
//     * @return the idInVitado
//     */
//    public int getIdInVitado() {
//        return estanciaBeanModel.getIdInVitado();
//    }
//
//    /**
//     * @param idInVitado the idInVitado to set
//     */
//    public void setIdInVitado(int idInVitado) {
//        estanciaBeanModel.setIdInVitado(idInVitado);
//    }
//
//    /**
//     * @return the listaDetalleEstancia
//     */
//    public List<DetalleSolicitudVO> getListaDetalleEstancia() {
//        return estanciaBeanModel.getListaDetalleEstancia();
//    }
//
//    /**
//     * @param listaDetalleEstancia the listaDetalleEstancia to set
//     */
//    public void setListaDetalleEstancia(List<DetalleSolicitudVO> listaDetalleEstancia) {
//        estanciaBeanModel.setListaDetalleEstancia(listaDetalleEstancia);
//    }
//
//    /**
//     * @return the conCorreo
//     */
//    public boolean isConCorreo() {
//        return estanciaBeanModel.isConCorreo();
//    }
//
//    /**
//     * @param conCorreo the conCorreo to set
//     */
//    public void setConCorreo(boolean conCorreo) {
//        estanciaBeanModel.setConCorreo(conCorreo);
//    }
}
