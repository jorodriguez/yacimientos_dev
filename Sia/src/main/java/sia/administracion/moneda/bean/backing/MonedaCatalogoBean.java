/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.moneda.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.administracion.moneda.bean.model.MonedaBeanModel;
import sia.servicios.sistema.vo.MonedaVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@ManagedBean(name = "monedaCatalogoBean")
@RequestScoped
public class MonedaCatalogoBean implements Serializable {

    @ManagedProperty(value = "#{monedaBeanModel}")
    private MonedaBeanModel monedaBeanModel;

    /**
     * @return the lstMoneda
     */
    public List<MonedaVO> getLstMoneda() {
        return this.getMonedaBeanModel().getLstMoneda();
    }

    /**
     * @param lstMoneda the lstMoneda to set
     */
    public void setLstMoneda(List<MonedaVO> lstMoneda) {
        this.getMonedaBeanModel().setLstMoneda(lstMoneda);
    }

    /**
     * @return the monedaBeanModel
     */
    public MonedaBeanModel getMonedaBeanModel() {
        return monedaBeanModel;
    }

    /**
     * @param monedaBeanModel the monedaBeanModel to set
     */
    public void setMonedaBeanModel(MonedaBeanModel monedaBeanModel) {
        this.monedaBeanModel = monedaBeanModel;
    }

    /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return monedaBeanModel.getSesion();
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.monedaBeanModel.setSesion(sesion);
    }

    /**
     * @return the newMoneda
     */
    public MonedaVO getNewMoneda() {
        return this.monedaBeanModel.getNewMoneda();
    }

    /**
     * @param newMoneda the newMoneda to set
     */
    public void setNewMoneda(MonedaVO newMoneda) {
        this.monedaBeanModel.setNewMoneda(newMoneda);
    }

    public void cargarCompanias(){
        this.monedaBeanModel.cargarCompanias();
    }
    
    public void cargarMoneda(int idMoneda){
        this.monedaBeanModel.cargarMoneda(idMoneda);
    }
    
    public void desactivarMoneda(){
        this.monedaBeanModel.desactivarMoneda();
    }
    
    public void init(){
        this.monedaBeanModel.init();
    }
    
    public void crearMoneda() {
        try {
            this.setNewMoneda(new MonedaVO());
            cargarCompanias();
            String metodo = ";abrirDialogoCrearMoneda();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void editarMoneda() {
        try {
            int idMoneda = Integer.parseInt(FacesUtils.getRequestParameter("idMoneda"));
            if (idMoneda > 0) {
                cargarCompanias();
                cargarMoneda(idMoneda);
                String metodo = ";abrirDialogoCrearMoneda();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void deleteMoneda() {
        try {
            int idMoneda = Integer.parseInt(FacesUtils.getRequestParameter("idMoneda"));
            if (idMoneda > 0) {
                cargarMoneda(idMoneda);
                desactivarMoneda();
                this.refrescarTabla();
                this.setNewMoneda(null);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    /**
     * @return the companias
     */
    public List<SelectItem> getCompanias() {
        return this.monedaBeanModel.getCompanias();
    }

    /**
     * @param companias the companias to set
     */
    public void setCompanias(List<SelectItem> companias) {
        this.monedaBeanModel.setCompanias(companias);
    }

    public void guardarMoneda() {
        try {
            this.monedaBeanModel.guardarMoneda();
            this.refrescarTabla();
            this.setNewMoneda(null);
            String metodo = ";cerrarDialogoCrearMoneda();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
    public void cambiarValorCompania(ValueChangeEvent event) {	
        try {            
            String idCompania = (String) event.getNewValue();
            if(idCompania != null && !idCompania.isEmpty()){
                this.setCompaniaSeleccionada(idCompania);
                this.refrescarTabla();               
            }            
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
    /**
     * @return the companiaSeleccionada
     */
    public String getCompaniaSeleccionada() {
        return this.monedaBeanModel.getCompaniaSeleccionada();
    }

    /**
     * @param companiaSeleccionada the companiaSeleccionada to set
     */
    public void setCompaniaSeleccionada(String companiaSeleccionada) {
        this.monedaBeanModel.setCompaniaSeleccionada(companiaSeleccionada);
    }
    
    public void refrescarTabla() {        
        this.monedaBeanModel.refrescarTabla();
    }
}
