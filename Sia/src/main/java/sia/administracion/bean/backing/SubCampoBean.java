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
import sia.administracion.bean.model.SubCampoBeanModel;
import sia.modelo.OcSubcampo;
import sia.modelo.requisicion.vo.OcSubCampoVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author jcarranza
 */
@ManagedBean(name = "subCampoBean")
@ViewScoped
public class SubCampoBean {

    @ManagedProperty(value = "#{subCampoBeanModel}")
    private SubCampoBeanModel subCampoBeanModel;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;

    public SubCampoBean() {

    }

    @PostConstruct
    public void llenaCampo() {
        this.subCampoBeanModel.inicia();
    }

    public void nuevaSubcampo() {
        this.subCampoBeanModel.nuevoSubcampo();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoSubcampo);");

    }

    public void borrarSubcampo() {
        int idSubCampo = Integer.parseInt(FacesUtils.getRequestParameter("idSubCampo"));
        if (idSubCampo > 0) {
            this.subCampoBeanModel.setIdSubcampo(idSubCampo);
            this.subCampoBeanModel.borrarSubcampo();
            this.subCampoBeanModel.cargarSubcampos();
        }
    }

    public void editarSubcampo() {
        int idSubCampo = Integer.parseInt(FacesUtils.getRequestParameter("idSubCampo"));
        if (idSubCampo > 0) {
            this.subCampoBeanModel.setIdSubcampo(idSubCampo);
            this.subCampoBeanModel.editarSubcampo();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoSubcampo);");
        }
    }

    public void guardarSubcampo() {
        this.subCampoBeanModel.guardarSubcampo();
        this.subCampoBeanModel.cargarSubcampos();
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoSubcampo);");

    }

    /**
     * @param subCampoBeanModel the subCampoBeanModel to set
     */
    public void setSubCampoBeanModel(SubCampoBeanModel subCampoBeanModel) {
        this.subCampoBeanModel = subCampoBeanModel;
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
     * @return the idSubcampo
     */
    public int getIdSubcampo() {
        return this.subCampoBeanModel.getIdSubcampo();
    }

    /**
     * @param idSubcampo the idSubcampo to set
     */
    public void setIdSubAct(int idSubcampo) {
        this.subCampoBeanModel.setIdSubcampo(idSubcampo);
    }

    /**
     * @return the lstSubcampos
     */
    public List<OcSubCampoVO> getLstSubcampos() {
        return this.subCampoBeanModel.getLstSubcampos();
    }

    /**
     * @param lstSubcampos the lstSubcampos to set
     */
    public void setLstSubcampos(List<OcSubCampoVO> lstSubcampos) {
        this.subCampoBeanModel.setLstSubcampos(lstSubcampos);
    }

    /**
     * @return the subObj
     */
    public OcSubcampo getSubObj() {
        return this.subCampoBeanModel.getSubObj();
    }

    /**
     * @param subObj the subObj to set
     */
    public void setSubAct(OcSubcampo subObj) {
        this.subCampoBeanModel.setSubObj(subObj);
    }

    /**
     * @param subVO the subVO to set
     */
    public void setSubVO(OcSubCampoVO subVO) {
        this.subCampoBeanModel.setSubVO(subVO);
    }

    /**
     * @return the subVO
     */
    public OcSubCampoVO getSubVO() {
        return this.subCampoBeanModel.getSubVO();
    }

}
