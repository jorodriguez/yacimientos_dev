/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.reporte.bean;

import javax.enterprise.context.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.SgSolicitudEstancia;
import sia.sgl.reporte.bean.model.ReporteBeanModel;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.ConversationsManager;
import sia.sgl.sistema.bean.support.FacesUtils;

/**
 *
 * @author marino
 */
@Named
@RequestScoped
public class ReporteBean {


    @Inject
    ReporteBeanModel reporteBeanModel;
    @Inject
    ConversationsManager conversationsManager;
    @Inject
    Sesion sesion;

    /**
     * Creates a new instance of reporteBean
     */    
    public ReporteBean() {
    }

    public String consultaSolicitudEstancia() {
        if (sesion.getOficinaActual() == null) {
            FacesUtils.addInfoMessage(Constantes.AVISO_NO_OFICINA);
            return "/principal";
        } else {
            reporteBeanModel.iniciarConvesacionConsulta();
            return "/vistas/sgl/estancia/consultaEstancia";
        }
    }

    public void buscarEstanciaPorcodigo(ActionEvent event) {
        if (reporteBeanModel.buscarSolicitudEstancia() == null) {
            FacesUtils.addInfoMessage("No se encontro solicitud de estancia para para el código -" + reporteBeanModel.getCodigo());
        }
    }

    public DataModel getTraerDetalleSolicitud() {
        try {
            return reporteBeanModel.traerDetalleSolicitud();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerHospedadosHotel() {
        try {
            return reporteBeanModel.traerHospedadosHotel();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerCanceladosHotel() {
        try {
            return reporteBeanModel.traerCanceladosHotel();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerNoHospedadosNoCanceladosHotel() {
        try {
            return reporteBeanModel.traerNoHospedadosNoCanceladosHotel();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerHospedadosStaff() {
        try {
            return reporteBeanModel.traerHospedadosStaff();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerCanceldosHuespedStaff() {
        try {
            return reporteBeanModel.traerCanceldosHuespedStaff();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerEstanciaTerminadaStaff() {
        try {
            return reporteBeanModel.traerEstanciaTerminadaStaff();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return reporteBeanModel.getCodigo();
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        reporteBeanModel.setCodigo(codigo);
    }

    /**
     * @return the sgSolicitudEstancia
     */
    public SgSolicitudEstancia getSgSolicitudEstancia() {
        return reporteBeanModel.getSgSolicitudEstancia();
    }

    /**
     * @param sgSolicitudEstancia the sgSolicitudEstancia to set
     */
    public void setSgSolicitudEstancia(SgSolicitudEstancia sgSolicitudEstancia) {
        reporteBeanModel.setSgSolicitudEstancia(sgSolicitudEstancia);
    }
    /**
     * @return the lista
     */
    public DataModel getLista() {
        return reporteBeanModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        reporteBeanModel.setLista(lista);
    }
}
