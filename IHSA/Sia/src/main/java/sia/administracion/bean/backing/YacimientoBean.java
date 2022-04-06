/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;




import org.primefaces.PrimeFaces;
import sia.administracion.bean.model.YacimientoBeanModel;
import sia.modelo.OcYacimiento;
import sia.modelo.requisicion.vo.OcYacimientoVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author jcarranza
 */
@Named(value = "yacimientoBean")
@ViewScoped
public class YacimientoBean implements  Serializable{

    @Inject
    private YacimientoBeanModel yacimientoBeanModel;

    @Inject
    private Sesion sesion;

    public YacimientoBean() {

    }

    @PostConstruct
    public void llenaCampo() {
        this.yacimientoBeanModel.inicia();
    }

    public void nuevoYacimiento() {
        this.yacimientoBeanModel.nuevoYacimiento();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoYacimiento);");

    }

    public void borrarYacimiento() {
        int idYac = Integer.parseInt(FacesUtils.getRequestParameter("idYac"));
        if (idYac > 0) {
            this.yacimientoBeanModel.setIdYacimiento(idYac);
            this.yacimientoBeanModel.borrarYacimiento();
            this.yacimientoBeanModel.cargarYacimientos();
        }
    }

    public void editarYacimiento() {
        int idYac = Integer.parseInt(FacesUtils.getRequestParameter("idYac"));
        if (idYac > 0) {
            this.yacimientoBeanModel.setIdYacimiento(idYac);
            this.yacimientoBeanModel.editarYacimiento();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoYacimiento);");
        }
    }

    public void guardarYacimiento() {
        this.yacimientoBeanModel.guardarYacimiento();
        this.yacimientoBeanModel.cargarYacimientos();
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoYacimiento);");

    }

    /**
     * @param yacimientoBeanModel the yacimientoBeanModel to set
     */
    public void setYacimientoBeanModel(YacimientoBeanModel yacimientoBeanModel) {
        this.yacimientoBeanModel = yacimientoBeanModel;
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
     * @return the idYacimiento
     */
    public int getIdYacimiento() {
        return this.yacimientoBeanModel.getIdYacimiento();
    }

    /**
     * @param idYacimiento the idYacimiento to set
     */
    public void setIdYacimiento(int idYacimiento) {
        this.yacimientoBeanModel.setIdYacimiento(idYacimiento);
    }

    /**
     * @return the lstYacimientos
     */
    public List<OcYacimientoVO> getLstYacimientos() {
        return this.yacimientoBeanModel.getLstYacimientos();
    }

    /**
     * @param lstYacimientos the lstYacimientos to set
     */
    public void setLstYacimientos(List<OcYacimientoVO> lstYacimientos) {
        this.yacimientoBeanModel.setLstYacimientos(lstYacimientos);
    }

    /**
     * @return the yacObj
     */
    public OcYacimiento getYacObj() {
        return this.yacimientoBeanModel.getYacObj();
    }

    /**
     * @param yacObj the yacObj to set
     */
    public void setYacObj(OcYacimiento yacObj) {
        this.yacimientoBeanModel.setYacObj(yacObj);
    }
    
    /**
     * @return the yacVO
     */
    public OcYacimientoVO getYacVO() {
        return  this.yacimientoBeanModel.getYacVO();
    }

    /**
     * @param yacVO the yacVO to set
     */
    public void setYacVO(OcYacimientoVO yacVO) {
         this.yacimientoBeanModel.setYacVO(yacVO);
    }
}

