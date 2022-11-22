/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.combustible.bean;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import sia.modelo.combustible.vo.Consumo;
import sia.modelo.sgl.vo.TarjetaOperacionVO;
import sia.sgl.combustible.bean.model.ReporteQHSEBeanModel;
import sia.util.UtilLog4j;

/**
 *
 * @author jevazquez
 */
@Named(value = "reporteQHSEBean")
@RequestScoped
public class ReporteQHSEBean implements Serializable {

    @ManagedProperty(value = "#{reporteQHSEBeanModel}")
    private ReporteQHSEBeanModel reporteQHSEBeanModel;

    public ReporteQHSEBean() {

    }

    public void iniciar() {

    }

    /**
     * @return the listConsumon
     */
    public List<Consumo> getListConsumon() {
        return getReporteQHSEBeanModel().getListConsumon();
    }

    /**
     * @param listConsumon the listConsumon to set
     */
    public void setListConsumon(List<Consumo> listConsumon) {
        getReporteQHSEBeanModel().setListConsumon(listConsumon);
    }

    /**
     * @return the mesSeleccion
     */
    public int getMesSeleccion() {
        return getReporteQHSEBeanModel().getMesSeleccion();
    }

    /**
     * @param mesSeleccion the mesSeleccion to set
     */
    public void setMesSeleccion(int mesSeleccion) {
        getReporteQHSEBeanModel().setMesSeleccion(mesSeleccion);
    }

    public void selectMes(ValueChangeEvent v) {

        try {
            setMesSeleccion((int) v.getNewValue());
            getReporteQHSEBeanModel().traerTotales(getMesSeleccion() - 1);

        } catch (Exception e){
            UtilLog4j.log.fatal(e);
        } 
        }
        /**
         * @return the ltsCombustible
         */
    public double getLtsCombustible() {
        return getReporteQHSEBeanModel().getLtsCombustible();
    }

    /**
     * @param ltsCombustible the ltsCombustible to set
     */
    public void setLtsCombustible(double ltsCombustible) {
        getReporteQHSEBeanModel().setLtsCombustible(ltsCombustible);
    }

    /**
     * @return the importe
     */
    public double getImporte() {
        return getReporteQHSEBeanModel().getImporte();
    }

    /**
     * @param importe the importe to set
     */
    public void setImporte(double importe) {
        getReporteQHSEBeanModel().setImporte(importe);
    }

    /**
     * @return the precio
     */
    public double getPrecio() {
        return getReporteQHSEBeanModel().getPrecio();
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(double precio) {
        getReporteQHSEBeanModel().setPrecio(precio);
    }

    /**
     * @return the kmMensual
     */
    public long getKmMensual() {
        return getReporteQHSEBeanModel().getKmMensual();
    }

    /**
     * @param kmMensual the kmMensual to set
     */
    public void setKmMensual(long kmMensual) {
        getReporteQHSEBeanModel().setKmMensual(kmMensual);
    }

    /**
     * @return the rendimiento
     */
    public double getRendimiento() {
        return getReporteQHSEBeanModel().getRendimiento();
    }

    /**
     * @param rendimiento the rendimiento to set
     */
    public void setRendimiento(double rendimiento) {
        getReporteQHSEBeanModel().setRendimiento(rendimiento);
    }

    /**
     * @return the reporteQHSEBeanModel
     */
    public ReporteQHSEBeanModel getReporteQHSEBeanModel() {
        return reporteQHSEBeanModel;
    }

    /**
     * @param reporteQHSEBeanModel the reporteQHSEBeanModel to set
     */
    public void setReporteQHSEBeanModel(ReporteQHSEBeanModel reporteQHSEBeanModel) {
        this.reporteQHSEBeanModel = reporteQHSEBeanModel;
    }

    /**
     * @return the meses
     */
    public List<SelectItem> getMeses() {
        return reporteQHSEBeanModel.getMeses();
    }

    /**
     * @param Meses the meses to set
     */
    public void setMeses(List<SelectItem> Meses) {
        reporteQHSEBeanModel.setMeses(Meses);
    }
    
    /**
     * @return the kmByUsuario
     */
    public List<TarjetaOperacionVO> getKmByUsuario() {
        return reporteQHSEBeanModel.getKmByUsuario();
    }

    /**
     * @param kmByUsuario the kmByUsuario to set
     */
    public void setKmByUsuario(List<TarjetaOperacionVO> kmByUsuario) {
        reporteQHSEBeanModel.setKmByUsuario(kmByUsuario);
    }
    
    /**
     * @return the maxByOficina
     */
    public List<TarjetaOperacionVO> getMaxByOficina() {
        return reporteQHSEBeanModel.getMaxByOficina();
    }

    /**
     * @param maxByOficina the maxByOficina to set
     */
    public void setMaxByOficina(List<TarjetaOperacionVO> maxByOficina) {
        reporteQHSEBeanModel.setMaxByOficina(maxByOficina);
    }
}
