/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.reporte.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.modelo.SgHuespedHotel;
import sia.modelo.SgHuespedStaff;
import sia.modelo.SgSolicitudEstancia;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.servicios.sgl.impl.SgDetalleSolicitudEstanciaImpl;
import sia.servicios.sgl.impl.SgHuespedHotelImpl;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author marino
 */
@Named(value = "reporteBean")
@ViewScoped
public class ReporteBeanModel implements Serializable {

    @Inject
    Sesion sesion;

    //Servicios
    @Inject
    private SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    private SgDetalleSolicitudEstanciaImpl sgDetalleSolicitudEstanciaImpl;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelImpl;
    @Inject
    private SgHuespedStaffImpl sgHuespedStaffImpl;
    //Entidades
    private SgSolicitudEstancia sgSolicitudEstancia;
    //Variables
    private String codigo;
    @Getter
    @Setter
    private List<DetalleEstanciaVO> listaDetEst;
    @Getter
    @Setter
    private List<SgHuespedHotel> listaHuesHotel;
    @Getter
    @Setter
    private List<SgHuespedHotel> listaCancHotel;
    @Getter
    @Setter
    private List<SgHuespedHotel> listaHospNoCancHotel;
    @Getter
    @Setter
    private List<SgHuespedStaff> listaHospStaff;
    @Getter
    @Setter
    private List<SgHuespedStaff> listaCancStaff;
    @Getter
    @Setter
    private List<SgHuespedStaff> listaEstTerStaff;

    /**
     * Creates a new instance of reporteBeanModel
     */
    public ReporteBeanModel() {
    }

    @PostConstruct
    public void iniciar() {
        sgSolicitudEstancia = new SgSolicitudEstancia();
        listaDetEst = new ArrayList<>();
        listaHuesHotel = new ArrayList<>();
        listaCancHotel = new ArrayList<>();
        listaHospNoCancHotel = new ArrayList<>();
        listaHospStaff = new ArrayList<>();
        listaCancStaff = new ArrayList<>();
        listaEstTerStaff = new ArrayList<>();
    }

    public void buscarEstanciaPorcodigo() {
        setSgSolicitudEstancia(sgSolicitudEstanciaImpl.buscarEstanciaPorcodigo(getCodigo().toUpperCase()));
        if (sgSolicitudEstancia == null) {
            FacesUtils.addInfoMessage("No se encontro solicitud de estancia para para el código -" + getCodigo());
        } else {
            traerDetalleSolicitud();
            traerHospedadosHotel();
            traerCanceladosHotel();
            traerNoHospedadosNoCanceladosHotel();
            traerHospedadosStaff();
            traerCanceldosHuespedStaff();
            traerEstanciaTerminadaStaff();
        }
    }

    public void traerDetalleSolicitud() {
        listaDetEst = sgDetalleSolicitudEstanciaImpl.traerDetallePorSolicitud(getSgSolicitudEstancia().getId(), Constantes.NO_ELIMINADO);
    }

    public void traerHospedadosHotel() {
        listaHuesHotel = sgHuespedHotelImpl.traerHospedadosHotel(getSgSolicitudEstancia().getId());
    }

    public void traerCanceladosHotel() {
        listaCancHotel = sgHuespedHotelImpl.traerCanceladosHotel(getSgSolicitudEstancia());
    }

    public void traerNoHospedadosNoCanceladosHotel() {
        listaHospNoCancHotel = sgHuespedHotelImpl.traerNoHospedadosNoCanceladosHotel(getSgSolicitudEstancia());
    }

    public void traerHospedadosStaff() {
        UtilLog4j.log.info(this, "hospedados staff");
        listaHospStaff = sgHuespedStaffImpl.getAllHuespedesBySolicitudHospedado(getSgSolicitudEstancia());
    }

    public void traerCanceldosHuespedStaff() {
        UtilLog4j.log.info(this, "cancelados staff");
        listaCancStaff = sgHuespedStaffImpl.getAllHuespedesBySolicitudCancelado(getSgSolicitudEstancia());
    }

    public void traerEstanciaTerminadaStaff() {
        UtilLog4j.log.info(this, "terminados staff");
        listaEstTerStaff = sgHuespedStaffImpl.getAllHuespedesBySolicitudEstanciaTerminada(getSgSolicitudEstancia());
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the sgSolicitudEstancia
     */
    public SgSolicitudEstancia getSgSolicitudEstancia() {
        return sgSolicitudEstancia;
    }

    /**
     * @param sgSolicitudEstancia the sgSolicitudEstancia to set
     */
    public void setSgSolicitudEstancia(SgSolicitudEstancia sgSolicitudEstancia) {
        this.sgSolicitudEstancia = sgSolicitudEstancia;
    }

}
