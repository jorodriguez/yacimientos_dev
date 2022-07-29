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
import sia.modelo.SgHuespedStaff;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.sgl.sistema.bean.backing.Sesion;

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
        PrimeFaces.current().executeScript("PF('myPanelModificarFechaStaff').show();");
    }
    
    
    public void mostrarPopupSalidaHuespedStaff(SgHuespedStaff actionEvent) {
//        log("mostrarPopUpSalidaHuespedStaff");

        //Dándole memoria al integrante de la Solicitud que va a terminar su estancia
        setHuespedStaff(actionEvent);

        if (getHuespedStaff().getFechaSalida() != null) {
            setFechaRealIngresoHuesped(getHuespedStaff().getFechaIngreso());
            setFechaRealSalidaHuesped(new Date());
            PrimeFaces.current().executeScript("PF('dlgSalirHab').show()");
            PrimeFaces.current().ajax().update("frmSalirHabitacionStaff");
//            sesion.getControladorPopups().put("popupSalirHuespedStaff", Boolean.TRUE);
        } else {
            PrimeFaces.current().executeScript("PF('dlgEstFechaSal').show()");
            PrimeFaces.current().ajax().update("popupEstablecerFechaSalidaHuespedStaffBase");
        }
    }
}
