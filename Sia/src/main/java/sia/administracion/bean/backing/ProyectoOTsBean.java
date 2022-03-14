/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import sia.administracion.bean.model.ProyectoOTsBeanModel;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcSubCampoVO;
import sia.modelo.requisicion.vo.OcYacimientoVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author jcarranza
 */
@ManagedBean(name = "proyectoOTsBean")
@ViewScoped
public class ProyectoOTsBean {
    @ManagedProperty(value = "#{proyectoOTsBeanModel}")
    private ProyectoOTsBeanModel proyectoOTsBeanModel;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    
    public ProyectoOTsBean(){
    
    }
    
     @PostConstruct
    public void llenaCampo() {
        this.proyectoOTsBeanModel.inicia();
    }


    /**
     * @param proyectoOTsBeanModel the proyectoOTsBeanModel to set
     */
    public void setProyectoOTsBeanModel(ProyectoOTsBeanModel proyectoOTsBeanModel) {
        this.proyectoOTsBeanModel = proyectoOTsBeanModel;
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
    
    /**
     * @return the proVO
     */
    public ProyectoOtVo getProVO() {
        return this.proyectoOTsBeanModel.getProVO();
    }

    /**
     * @param proVO the proVO to set
     */
    public void setProVO(ProyectoOtVo proVO) {
        this.proyectoOTsBeanModel.setProVO(proVO);
    }
    
    /**
     * @return the lstProyectos
     */
    public List<ProyectoOtVo> getLstProyectos() {
        return this.proyectoOTsBeanModel.getLstProyectos();
    }

    /**
     * @param lstProyectos the lstProyectos to set
     */
    public void setLstProyectos(List<ProyectoOtVo> lstProyectos) {
        this.proyectoOTsBeanModel.setLstProyectos(lstProyectos);
    }
    
    public void nuevoProy(){
            this.proyectoOTsBeanModel.nuevoProy();
            this.proyectoOTsBeanModel.cargarSubcampo();
            this.proyectoOTsBeanModel.cargarYacimientos();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoProyOT);");    
    }
    
    public void abrirProy(){
    int idProy = Integer.parseInt(FacesUtils.getRequestParameter("idProy"));
        if (idProy > 0) {
            this.proyectoOTsBeanModel.setIdProy(idProy);
            this.proyectoOTsBeanModel.abrirProy();
            this.proyectoOTsBeanModel.cargarOts();
        }
    }
    
    public void cerrarProy(){
    int idProy = Integer.parseInt(FacesUtils.getRequestParameter("idProy"));
        if (idProy > 0) {
            this.proyectoOTsBeanModel.setIdProy(idProy);
            this.proyectoOTsBeanModel.cerrarProy();
            this.proyectoOTsBeanModel.cargarOts();
        }
    }
    
    public void editarProy(){
    int idProy = Integer.parseInt(FacesUtils.getRequestParameter("idProy"));
        if (idProy > 0) {
            this.proyectoOTsBeanModel.setIdProy(idProy);
            this.proyectoOTsBeanModel.editarProy();
            this.proyectoOTsBeanModel.cargarYacimientos();
            this.proyectoOTsBeanModel.cargarSubcampo();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoProyOT);");
        }
    }
    
    public void borrarProy(){
    int idProy = Integer.parseInt(FacesUtils.getRequestParameter("idProy"));
        if (idProy > 0) {
            this.proyectoOTsBeanModel.setIdProy(idProy);
            this.proyectoOTsBeanModel.borrarProy();         
            this.proyectoOTsBeanModel.cargarOts();
        }
    }
    
    public void guardarProy(){    
            this.proyectoOTsBeanModel.guardarProy();
            this.proyectoOTsBeanModel.cargarOts();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoProyOT);");        
    }
    
       /**
     * @return the lstSubcampos
     */
    public List<OcSubCampoVO> getLstSubcampos() {
        return this.proyectoOTsBeanModel.getLstSubcampos();
    }

    /**
     * @param lstSubcampos the lstSubcampos to set
     */
    public void setLstSubcampos(List<OcSubCampoVO> lstSubcampos) {
        this.proyectoOTsBeanModel.setLstSubcampos(lstSubcampos);
    }

    /**
     * @return the lstYacimientos
     */
    public List<OcYacimientoVO> getLstYacimientos() {
        return this.proyectoOTsBeanModel.getLstYacimientos();
    }

    /**
     * @param lstYacimientos the lstYacimientos to set
     */
    public void setLstYacimientos(List<OcYacimientoVO> lstYacimientos) {
        this.proyectoOTsBeanModel.setLstYacimientos(lstYacimientos);
    }
}
