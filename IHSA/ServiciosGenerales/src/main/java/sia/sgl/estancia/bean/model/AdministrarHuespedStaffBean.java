/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.sgl.estancia.bean.model;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgHuespedStaff;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "administrarHuespedStaffBean")
@ViewScoped
public class AdministrarHuespedStaffBean implements Serializable {

    /**
     * Creates a new instance of AdministrarHuespedStaffBean
     */
    public AdministrarHuespedStaffBean() {
    }

    @Inject
    Sesion sesion;
    @Inject
    private SgHuespedStaffImpl huespedStaffService;
    @Inject
    SiManejoFechaImpl siManejoFechaLocal;
    @Getter
    @Setter
    private List<SgHuespedStaff> huespedes;
    @Getter
    @Setter
    private SgHuespedStaff huespedStaff;
    @Getter
    @Setter
    private Date fechaSalidaPropuesta;
    @Getter
    @Setter
    private Date fechaRealIngresoHuesped;
    @Getter
    @Setter
    private Date fechaRealSalidaHuesped;
    @Getter
    @Setter
    private String opcionSeleccionada;
    @Getter
    @Setter
    private String mensaje;
    @Getter
    @Setter
    private Date fechaSalidaHuesped;
    @Getter
    @Setter
    private Date fechaIngresoHuesped;
    @Getter
    @Setter
    private boolean disabled;

    @PostConstruct
    public void iniciar() {
        huespedes = new ArrayList<>();
        huespedStaff = new SgHuespedStaff();
        try {
            huespedes = (huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
        } catch (Exception ex) {
            Logger.getLogger(AdministrarHuespedStaffBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void seleccionarFechaSalidaHuespedStaffProlongado(SgHuespedStaff event) {
        huespedStaff = (event);

        setFechaSalidaPropuesta(getHuespedStaff().getFechaSalida());
        PrimeFaces.current().executeScript("PF('dialogModificarFechaStyaff').show();");
    }

    public void mostrarPopupSalidaHuespedStaff(SgHuespedStaff actionEvent) {
        setHuespedStaff(actionEvent);
        if (getHuespedStaff().getFechaSalida() != null) {
            setFechaRealIngresoHuesped(getHuespedStaff().getFechaIngreso());
            setFechaRealSalidaHuesped(new Date());
            PrimeFaces.current().executeScript("PF('dlgSalirHab').show()");
            PrimeFaces.current().ajax().update("frmSalirHabitacionStaff");
        } else {
            PrimeFaces.current().executeScript("PF('dlgEstFechaSal').show()");
            PrimeFaces.current().ajax().update("popupEstablecerFechaSalidaHuespedStaffBase");
        }
    }

    public void mostrarPopupCambiarHuesped(SgHuespedStaff actionEvent) {
        setHuespedStaff(actionEvent);

        //Seleccionando un radio button y sus opciones por default
        setOpcionSeleccionada("Staff");

        setFechaRealIngresoHuesped(getHuespedStaff().getFechaIngreso());
        setFechaRealSalidaHuesped(new Date());
        setFechaIngresoHuesped(new Date());
        setFechaSalidaHuesped(getHuespedStaff().getFechaSalida());

        //Si es un periodo de prueba deshabilitar la modificación de la fecha de salida
        if (getHuespedStaff().getSgTipoEspecifico().getNombre().contains("rueba")) {
            setDisabled(true);
        }
        PrimeFaces.current().executeScript("PF('dlgCambiarHab').show()");

    }

    public String cancelHospedajeStaff(SgHuespedStaff data) {
        //Dándole memoria al integrante de la Solicitud que va a terminar su estancia
        setHuespedStaff(data);

        String url = "";

        try {
            huespedStaffService.cancelHospedajeStaff(huespedStaff, sesion.getUsuario().getId());
            huespedes = (huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
            mensaje = "La estancia del Huésped " + (huespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? huespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : huespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
                    + " en el Staff " + huespedStaff.getSgStaffHabitacion().getSgStaff().getNombre() + "|" + huespedStaff.getSgStaffHabitacion().getSgStaff().getNumeroStaff()
                    + " en la Habitación " + huespedStaff.getSgStaffHabitacion().getNombre() + "|" + huespedStaff.getSgStaffHabitacion().getNumeroHabitacion()
                    + " se ha cancelado";

            FacesUtils.addInfoMessage(getMensaje());

            //Quitando memorias
            setHuespedStaff(null);
            setMensaje("");
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            //Quitando memorias
            setHuespedStaff(null);
            setMensaje("");
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            //Quitando memorias
            setHuespedStaff(null);
            setMensaje("");
        }

        return url;
    }

    public void modificarFechaSalidaHuespedStaffProlongado() {
        huespedStaffService.actualizarFechaSalida(getHuespedStaff().getId(), getFechaSalidaPropuesta(), sesion.getUsuario().getId());

        PrimeFaces.current().executeScript("myPanelModificarFechaStaff.hide();");
    }

    public void exitHuespedStaff() throws SIAException, Exception {
        if (!siManejoFechaLocal.dayIsSame(fechaRealIngresoHuesped, fechaRealSalidaHuesped)) {
            if (!siManejoFechaLocal.theFirstDateIsLessThanTheSecond(fechaRealIngresoHuesped, fechaRealSalidaHuesped)) {
                throw new SIAException(EstanciaBeanModel.class.getName(),
                        "exitHuespedStaff",
                        Constantes.MENSAJE_FECHA_REAL_INGRESO_HUESPED_INVALIDA,
                        ("fechaRealIngresoHuesped: " + fechaRealIngresoHuesped + " fechaRealSalidaHuesped: " + fechaRealSalidaHuesped));
            }
        }
        huespedStaffService.exitHuespedStaff(huespedStaff, fechaRealIngresoHuesped, fechaRealSalidaHuesped, sesion.getUsuario().getId());
        huespedes = (huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
        mensaje = "La estancia del Huésped " + (huespedStaff.getSgDetalleSolicitudEstancia().getUsuario() != null ? huespedStaff.getSgDetalleSolicitudEstancia().getUsuario().getNombre() : huespedStaff.getSgDetalleSolicitudEstancia().getSgInvitado().getNombre())
                + " en el Staff " + huespedStaff.getSgStaffHabitacion().getSgStaff().getNombre() + "|" + huespedStaff.getSgStaffHabitacion().getSgStaff().getNumeroStaff()
                + " en la Habitación " + huespedStaff.getSgStaffHabitacion().getNombre() + "|" + huespedStaff.getSgStaffHabitacion().getNumeroHabitacion()
                + " ha terminado";
    }

    public void ocultarPopupSalidaHuespedStaff() {
//        log("ocultarPopUpSalidaHuespedStaff");
        //Quitándole memoria al integrante de la Solicitud que termina su estancia
        setHuespedStaff(null);
        setFechaRealIngresoHuesped(null);
        setFechaRealSalidaHuesped(null);
        setFechaSalidaPropuesta(null);
        setMensaje("");
        PrimeFaces.current().executeScript("PF('dlgSalirHab').hide();");
        PrimeFaces.current().executeScript("PF('dlgEstFechaSal').hide();");
    }

    public void establecerFechaSalidaHuespedStaffBase() throws SIAException, Exception {
        this.huespedStaff.setFechaSalida(this.fechaSalidaPropuesta);
        huespedStaffService.update(this.huespedStaff, sesion.getUsuario().getId());
        this.huespedes = (huespedStaffService.getAllHuespedesByOficinaList(sesion.getOficinaActual().getId(), true, false, sesion.getUsuario().getId()));
    }

}
