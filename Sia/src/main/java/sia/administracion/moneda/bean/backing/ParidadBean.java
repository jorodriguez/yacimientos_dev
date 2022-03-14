/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.moneda.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.administracion.moneda.bean.model.ParidadBeanModel;
import sia.modelo.Compania;
import sia.servicios.sistema.vo.ParidadAnual;
import sia.servicios.sistema.vo.ParidadVO;
import sia.servicios.sistema.vo.ParidadValorVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */

@ManagedBean(name = "paridadBean")
@RequestScoped
public class ParidadBean implements Serializable{
    
    @ManagedProperty(value = "#{paridadBeanModel}")
    private ParidadBeanModel paridadBeanModel;
        
   /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return this.getParidadBeanModel().getSesion();
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.getParidadBeanModel().setSesion(sesion);
    }

    /**
     * @return the lstParidad
     */
    public List<ParidadVO> getLstParidad() {
        return this.getParidadBeanModel().getLstParidad();
    }

    /**
     * @param lstParidad the lstParidad to set
     */
    public void setLstParidad(List<ParidadVO> lstParidad) {
        this.getParidadBeanModel().setLstParidad(lstParidad);
    }

    /**
     * @return the newParidad
     */
    public ParidadVO getNewParidad() {
        return this.getParidadBeanModel().getNewParidad();
    }

    /**
     * @param newParidad the newParidad to set
     */
    public void setNewParidad(ParidadVO newParidad) {
        this.getParidadBeanModel().setNewParidad(newParidad);
    }

    /**
     * @return the companias
     */
    public List<SelectItem> getCompanias() {
        return this.getParidadBeanModel().getCompanias();
    }

    /**
     * @param companias the companias to set
     */
    public void setCompanias(List<SelectItem> companias) {
        this.getParidadBeanModel().setCompanias(companias);
    }

    /**
     * @return the paridadSeleccionada
     */
    public int getParidadSeleccionada() {
        return this.getParidadBeanModel().getParidadSeleccionada();
    }

    /**
     * @param paridadSeleccionada the paridadSeleccionada to set
     */
    public void setParidadSeleccionada(int paridadSeleccionada) {
        this.getParidadBeanModel().setParidadSeleccionada(paridadSeleccionada);
    }

    /**
     * @return the companiaSeleccionada
     */
    public String getCompaniaSeleccionada() {
        return this.getParidadBeanModel().getCompaniaSeleccionada();
    }

    /**
     * @param companiaSeleccionada the companiaSeleccionada to set
     */
    public void setCompaniaSeleccionada(String companiaSeleccionada) {
        this.getParidadBeanModel().setCompaniaSeleccionada(companiaSeleccionada);
    }

    /**
     * @return the paridadBeanModel
     */
    public ParidadBeanModel getParidadBeanModel() {
        return paridadBeanModel;
    }

    /**
     * @param paridadBeanModel the paridadBeanModel to set
     */
    public void setParidadBeanModel(ParidadBeanModel paridadBeanModel) {
        this.paridadBeanModel = paridadBeanModel;
    }
    
    public void crearParidad() {
        try {
            this.setNewParidad(new ParidadVO());                                    
            this.setCompaniaSelec();
            cargarMonedasOri();
            String metodo = ";abrirDialogoCrearParidad();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
    public void crearParidadValor() {
        try {
            int idParidad = Integer.parseInt(FacesUtils.getRequestParameter("idParidad"));
            if (idParidad > 0) {                
            this.setNewParidadValorVO(new ParidadValorVO());  
            this.getNewParidadValorVO().setIdParidad(idParidad);
            this.setCompaniaSelec();            
            String metodo = ";abrirDialogoCrearParidadValor();";
            PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void editarParidad() {
        try {
            int idParidad = Integer.parseInt(FacesUtils.getRequestParameter("idParidad"));
            if (idParidad > 0) {                
                this.setCompaniaSelec();                
                cargarMonedasOri();
                cargarParidad(idParidad);
                String metodo = ";abrirDialogoCrearParidad();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
    public void editarParidadValor() {
        try {
            int idParidadValor = Integer.parseInt(FacesUtils.getRequestParameter("idParidadValor"));
            if (idParidadValor > 0) {                
                this.setCompaniaSelec();                                
                cargarParidadValor(idParidadValor);
                String metodo = ";abrirDialogoCrearParidadValor();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void deleteParidad() {
        try {
            int idParidad = Integer.parseInt(FacesUtils.getRequestParameter("idParidad"));
            if (idParidad > 0) {
                cargarParidad(idParidad);
                desactivarParidad();
                this.refrescarTabla();
                this.setNewParidad(null);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
    public void guardarParidad() {
        try {
            this.paridadBeanModel.guardarParidad();
            this.refrescarTabla();
            this.setNewParidad(null);
            String metodo = ";cerrarDialogoCrearParidad();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
    public void guardarParidadValor() {
        try {
            this.paridadBeanModel.guardarParidadValor();
            cargarParidadAnual();
            this.setNewParidadValorVO(null);            
            String metodo = ";cerrarDialogoCrearParidadValor();";
            PrimeFaces.current().executeScript(metodo);
        } catch (EJBException e) {
            FacesUtils.addErrorMessage("El valor de la paridad esta mal capturado o ya existe una paridad para esta fecha.");
        } catch (Exception ex) {            
            FacesUtils.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
        }
    }
    
    public void cargarCompanias(){
        this.paridadBeanModel.cargarCompanias();
    }
    
    public void cargarMonedasOri(){
        this.paridadBeanModel.cargarMonedasOri();
    }
    
    public void cargarMonedasDes(int mOID){
        this.paridadBeanModel.cargarMonedasDes(mOID);
    }
    
    public void cargarParidad(int idParidad){
        this.paridadBeanModel.cargarParidad(idParidad);
    }
    
    public void cargarParidadValor(int idParidadValor){
        this.paridadBeanModel.cargarParidadValor(idParidadValor);
    }
    
    public void refrescarTabla() {        
        this.paridadBeanModel.refrescarTabla();
    }
    
    public void desactivarParidad() {        
        this.paridadBeanModel.desactivarParidad();
    }
    
    public void cambiarValorCompania(ValueChangeEvent event) {	
        try {            
            String idCompania = (String) event.getNewValue();
            if(idCompania != null && !idCompania.isEmpty()){
                this.setCompaniaSeleccionada(idCompania);
                this.refrescarTabla();  
                this.cargarParidadAnual();
            }            
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
    public void cambiarValorMonedaOrigen(ValueChangeEvent event) {	
        try {            
            int monedaOrID = (Integer) event.getNewValue();
            if(monedaOrID > 0){                
                cargarMonedasDes(monedaOrID);
            }            
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
        
    /**
     * @return the companiaSelec
     */
    public Compania getCompaniaSelec() {
        return getParidadBeanModel().getCompaniaSelec();
    }

    /**
     * @param companiaSelec the companiaSelec to set
     */
    public void setCompaniaSelec(Compania companiaSelec) {
        getParidadBeanModel().setCompaniaSelec(companiaSelec);
    }
    
    public void setCompaniaSelec() {
        getParidadBeanModel().setCompaniaSelec();
    }
    
    /**
     * @return the monedasOrigen
     */
    public List<SelectItem> getMonedasOrigen() {
        return getParidadBeanModel().getMonedasOrigen();
    }

    /**
     * @param monedasOrigen the monedasOrigen to set
     */
    public void setMonedasOrigen(List<SelectItem> monedasOrigen) {
        getParidadBeanModel().setMonedasOrigen(monedasOrigen);
    }

    /**
     * @return the monedasDestino
     */
    public List<SelectItem> getMonedasDestino() {
        return getParidadBeanModel().getMonedasDestino();
    }

    /**
     * @param monedasDestino the monedasDestino to set
     */
    public void setMonedasDestino(List<SelectItem> monedasDestino) {
        getParidadBeanModel().setMonedasDestino(monedasDestino);
    }
    
    /**
     * @return the activeTab1
     */
    public String getActiveTab1() {
        return getParidadBeanModel().getActiveTab1();
    }

    /**
     * @param activeTab1 the activeTab1 to set
     */
    public void setActiveTab1(String activeTab1) {
        getParidadBeanModel().setActiveTab1(activeTab1);
    }

    /**
     * @return the activeTab2
     */
    public String getActiveTab2() {
        return getParidadBeanModel().getActiveTab2();
    }

    /**
     * @param activeTab2 the activeTab2 to set
     */
    public void setActiveTab2(String activeTab2) {
        getParidadBeanModel().setActiveTab2(activeTab2);
    }
    
    /**
     * @return the indexTab
     */
    public int getIndexTab() {
        return getParidadBeanModel().getIndexTab();
    }

    /**
     * @param indexTab the indexTab to set
     */
    public void setIndexTab(int indexTab) {
        getParidadBeanModel().setIndexTab(indexTab);
    }
    
    /**
     * @return the paridadAnual
     */
    public ParidadAnual getParidadAnual() {
        return getParidadBeanModel().getParidadAnual();
    }

    /**
     * @param paridadAnual the paridadAnual to set
     */
    public void setParidadAnual(ParidadAnual paridadAnual) {
        getParidadBeanModel().setParidadAnual(paridadAnual);
    }
    
    public void cargarParidadAnual() {
        getParidadBeanModel().cargarParidadAnual();
    }
    
    /**
     * @return the newParidadValorVO
     */
    public ParidadValorVO getNewParidadValorVO() {
        return getParidadBeanModel().getNewParidadValorVO();
    }

    /**
     * @param newParidadValorVO the newParidadValorVO to set
     */
    public void setNewParidadValorVO(ParidadValorVO newParidadValorVO) {
        getParidadBeanModel().setNewParidadValorVO(newParidadValorVO);
    }
}
