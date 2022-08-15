/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.combustible.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import sia.modelo.combustible.vo.Consumo;
import sia.modelo.sgl.vo.TarjetaOperacionVO;
import sia.servicios.sgl.combustible.impl.SgTarjetaOperacionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jevazquez
 */
@Named(value = "reporteQHSEBeanModel")
@ViewScoped
public class ReporteQHSEBeanModel implements Serializable {

    

    @Inject
    private Sesion sesion;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private SgTarjetaOperacionImpl sgTarjetaOperacionImpl;
    @Inject 
    private SiManejoFechaImpl siManejoFechaImpl;

    private List<Consumo> listConsumon;
    private List<SelectItem> meses;
    private List<TarjetaOperacionVO> kmByUsuario;
    private List<TarjetaOperacionVO> maxByOficina;
    private int mesSeleccion = -1;
    private double  ltsCombustible = 0;
    private double importe = 0;
    private double precio = 0;
    private long kmMensual = 0;
    private double rendimiento = 0;

    @PostConstruct
    public void iniciar() {
        cargarMeses(); 
    }

    /**
     * @return the listConsumon
     */
    public List<Consumo> getListConsumon() {
        return listConsumon;
    }

    /**
     * @param listConsumon the listConsumon to set
     */
    public void setListConsumon(List<Consumo> listConsumon) {
        this.listConsumon = listConsumon;
    }

   

    public void traerTotales(int mes) {
        try{
            setMesSeleccion(mes+1);

        TarjetaOperacionVO o = sgTarjetaOperacionImpl.regresarMaximosMensuales(mes);
        
        setLtsCombustible(o.getCantidad());
        setImporte(o.getCargo());
        setKmMensual(o.getKmMensual());
        setPrecio(o.getPrecioUnitario());
        setRendimiento(o.getRendimiento());
        
            setKmByUsuario(sgTarjetaOperacionImpl.kmMensualByUser(mes));
            setMaxByOficina(sgTarjetaOperacionImpl.regresarMaximosMensualesByOficina(mes));
        } catch(Exception e){
            UtilLog4j.log.fatal(e);
        }
        
        
        
        
    }

    /**
     * @return the mesSeleccion
     */
    public int getMesSeleccion() {
        return mesSeleccion;
    }

    /**
     * @param mesSeleccion the mesSeleccion to set
     */
    public void setMesSeleccion(int mesSeleccion) {
        this.mesSeleccion = mesSeleccion;
    }
    
    /**
     * @return the ltsCombustible
     */
    public double getLtsCombustible() {
        return ltsCombustible;
    }

    /**
     * @param ltsCombustible the ltsCombustible to set
     */
    public void setLtsCombustible(double ltsCombustible) {
        this.ltsCombustible = ltsCombustible;
    }

    /**
     * @return the importe
     */
    public double getImporte() {
        return importe;
    }

    /**
     * @param importe the importe to set
     */
    public void setImporte(double importe) {
        this.importe = importe;
    }

    /**
     * @return the precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * @return the kmMensual
     */
    public long getKmMensual() {
        return kmMensual;
    }

    /**
     * @param kmMensual the kmMensual to set
     */
    public void setKmMensual(long kmMensual) {
        this.kmMensual = kmMensual;
    }

    /**
     * @return the rendimiento
     */
    public double getRendimiento() {
        return rendimiento;
    }

    /**
     * @param rendimiento the rendimiento to set
     */
    public void setRendimiento(double rendimiento) {
        this.rendimiento = rendimiento;
    }

    /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return sesion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public void cargarMeses(){
        setMeses(siManejoFechaImpl.meses());
    }

    /**
     * @return the meses
     */
    public List<SelectItem> getMeses() {
        return meses;
    }

    /**
     * @param Meses the meses to set
     */
    public void setMeses(List<SelectItem> Meses) {
        this.meses = Meses;
    }

    /**
     * @return the kmByUsuario
     */
    public List<TarjetaOperacionVO> getKmByUsuario() {
        return kmByUsuario;
    }

    /**
     * @param kmByUsuario the kmByUsuario to set
     */
    public void setKmByUsuario(List<TarjetaOperacionVO> kmByUsuario) {
        this.kmByUsuario = kmByUsuario;
    }
    
    public void descargarExcel(){
        
    }

    /**
     * @return the maxByOficina
     */
    public List<TarjetaOperacionVO> getMaxByOficina() {
        return maxByOficina;
    }

    /**
     * @param maxByOficina the maxByOficina to set
     */
    public void setMaxByOficina(List<TarjetaOperacionVO> maxByOficina) {
        this.maxByOficina = maxByOficina;
    }
}
