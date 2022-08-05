/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.busqueda;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.inject.Inject;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.sgl.viaje.busqueda.model.MisSolicitudesBeanModel;

/**
 *
 * @author jevazquez
 */
@Named(value = "misSolicitudesBean")
@RequestScoped
public class MisSolicitudesBean implements Serializable {

    @Inject
    private MisSolicitudesBeanModel misSolicitudesBeanModel;

    public MisSolicitudesBean() {

    }

    /**
     * @return the buscarSVOrVIBeanModel
     */
    public MisSolicitudesBeanModel getMisSolicitudesBeanModel() {
        return misSolicitudesBeanModel;
    }

    /**
     * @param buscarSVOrVIBeanModel the buscarSVOrVIBeanModel to set
     */
    public void setMisSolicitudesBeanModel(MisSolicitudesBeanModel misSolicitudesBeanModel) {
        this.misSolicitudesBeanModel = misSolicitudesBeanModel;
    }

    /**
     * @return the listaSolicitudes
     */
    public List<SolicitudViajeVO> getListaSolicitudes() {
        return misSolicitudesBeanModel.getListaSolicitudes();
    }

    /**
     * @param listaSolicitudes the listaSolicitudes to set
     */
    public void setListaSolicitudes(List<SolicitudViajeVO> listaSolicitudes) {
        this.misSolicitudesBeanModel.setListaSolicitudes(listaSolicitudes);
    }

    public void buscarByFiltros() {
        misSolicitudesBeanModel.buscarByFiltros();
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return misSolicitudesBeanModel.getFechaInicio();
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        misSolicitudesBeanModel.setFechaInicio(fechaInicio);
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return misSolicitudesBeanModel.getFechaFin();
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        misSolicitudesBeanModel.setFechaFin(fechaFin);
    }

    public void cancelarSV() throws Exception {
        misSolicitudesBeanModel.cancelarSV();
    }

    public void popCancelarSolicitud(int idSol) {
        misSolicitudesBeanModel.popCancelarSolicitud(idSol);
    }

    /**
     * @return the svActual
     */
    public SolicitudViajeVO getSvActual() {
        return misSolicitudesBeanModel.getSvActual();
    }

    /**
     * @param svActual the svActual to set
     */
    public void setSvActual(SolicitudViajeVO svActual) {
        misSolicitudesBeanModel.setSvActual(svActual);
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return misSolicitudesBeanModel.getMotivo();
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        misSolicitudesBeanModel.setMotivo(motivo);
    }

    /**
     * @return the filtro
     */
    public String getFiltro() {
        return misSolicitudesBeanModel.getFiltro();
    }

    /**
     * @param filtro the filtro to set
     */
    public void setFiltro(String filtro) {
        misSolicitudesBeanModel.setFiltro(filtro);
    }
}
