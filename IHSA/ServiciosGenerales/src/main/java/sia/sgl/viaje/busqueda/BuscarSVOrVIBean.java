/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.busqueda;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.sgl.viaje.busqueda.model.BuscarSVOrVIBeanModel;

/**
 *
 * @author jevazquez
 */
@Named(value = "buscarSVOrVIBean")
@RequestScoped
public class BuscarSVOrVIBean implements Serializable{

    
    @ManagedProperty(value = "#{buscarSVOrVIBeanModel}")
    private BuscarSVOrVIBeanModel buscarSVOrVIBeanModel;
    
    public BuscarSVOrVIBean(){
        
    }
    /**
     * @return the buscarSVOrVIBeanModel
     */
    public BuscarSVOrVIBeanModel getBuscarSVOrVIBeanModel() {
        return buscarSVOrVIBeanModel;
    }

    /**
     * @param buscarSVOrVIBeanModel the buscarSVOrVIBeanModel to set
     */
    public void setBuscarSVOrVIBeanModel(BuscarSVOrVIBeanModel buscarSVOrVIBeanModel) {
        this.buscarSVOrVIBeanModel = buscarSVOrVIBeanModel;
    }
    
    
    /**
     * @return the codigo
     */
    public String getCodigoSV() {
        return buscarSVOrVIBeanModel.getCodigoSV();
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigoSV(String codigo) {
        buscarSVOrVIBeanModel.setCodigoSV(codigo);
    }
    
    /**
     * @return the tipo
     */
    public boolean isTipoSoV() {
        return buscarSVOrVIBeanModel.isTipoSoV();
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipoSoV(boolean tipo) {
        buscarSVOrVIBeanModel.setTipoSoV(tipo);
    }
    
    /**
     * @return the sv
     */
    public SolicitudViajeVO getSv() {
        return buscarSVOrVIBeanModel.getSv();
    }

    /**
     * @param sv the sv to set
     */
    public void setSv(SolicitudViajeVO sv) {
        buscarSVOrVIBeanModel.setSv(sv);
    }

    /**
     * @return the vi
     */
    public List<ViajeVO> getVi() {
        return buscarSVOrVIBeanModel.getVi();
    }

    /**
     * @param vi the vi to set
     */
    public void setVi(List<ViajeVO> vi) {
        buscarSVOrVIBeanModel.setVi(vi);
    }
    
    public void buscarSV(){
        buscarSVOrVIBeanModel.buscarSV();
    }
    
    public void buscarVI(){
        buscarSVOrVIBeanModel.buscarVI();
    }
    
    /**
     * @return the fechaSalida
     */
    public String getFechaSalida() {
        return buscarSVOrVIBeanModel.getFechaSalida();
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(String fechaSalida) {
        buscarSVOrVIBeanModel.setFechaSalida(fechaSalida);
    }

    /**
     * @return the fechaRegreso
     */
    public String getFechaRegreso() {
        return buscarSVOrVIBeanModel.getFechaRegreso();
    }

    /**
     * @param fechaRegreso the fechaRegreso to set
     */
    public void setFechaRegreso(String fechaRegreso) {
        buscarSVOrVIBeanModel.setFechaRegreso(fechaRegreso);
    }
    
    /**
     * @return the lea
     */
    public List<EstatusAprobacionSolicitudVO> getLea() {
        return buscarSVOrVIBeanModel.getLea();
    }

    /**
     * @param lea the lea to set
     */
    public void setLea(List<EstatusAprobacionSolicitudVO> lea) {
        buscarSVOrVIBeanModel.setLea(lea);
    }
    
    /**
     * @return the codigoVI
     */
    public String getCodigoVI() {
        return buscarSVOrVIBeanModel.getCodigoVI();
    }

    /**
     * @param codigoVI the codigoVI to set
     */
    public void setCodigoVI(String codigoVI) {
        buscarSVOrVIBeanModel.setCodigoVI(codigoVI);
    }
    
    /**
     * @return the estatusActual
     */
    public String getEstatusActual() {
        return buscarSVOrVIBeanModel.getEstatusActual();
    }

    /**
     * @param estatusActual the estatusActual to set
     */
    public void setEstatusActual(String estatusActual) {
        buscarSVOrVIBeanModel.setEstatusActual(estatusActual);
    }
    
    /**
     * @return the viajeBuscar
     */
    public ViajeVO getViajeBuscar() {
        return buscarSVOrVIBeanModel.getViajeBuscar();
    }

    /**
     * @param viajeBuscar the viajeBuscar to set
     */
    public void setViajeBuscar(ViajeVO viajeBuscar) {
        buscarSVOrVIBeanModel.setViajeBuscar(viajeBuscar);
    }
}
