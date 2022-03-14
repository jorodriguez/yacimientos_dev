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
import sia.administracion.bean.model.SubActividadBeanModel;
import sia.modelo.OcUnidadCosto;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author jcarranza
 */
@ManagedBean(name = "subActividadBean")
@ViewScoped
public class SubActividadBean {

    @ManagedProperty(value = "#{subActividadBeanModel}")
    private SubActividadBeanModel subActividadBeanModel;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;

    public SubActividadBean() {

    }

    @PostConstruct
    public void llenaCampo() {
        this.subActividadBeanModel.inicia();
    }

    public void nuevaSubAct() {
        this.subActividadBeanModel.nuevaSubAct();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoSubact);");

    }

    public void borrarSubAct() {
        int idSubact = Integer.parseInt(FacesUtils.getRequestParameter("idSubact"));
        if (idSubact > 0) {
            this.subActividadBeanModel.setIdSubAct(idSubact);
            this.subActividadBeanModel.borrarSubAct();
            this.subActividadBeanModel.cargarSubactividades();
        }
    }

    public void editarSubAct() {
        int idSubact = Integer.parseInt(FacesUtils.getRequestParameter("idSubact"));
        if (idSubact > 0) {
            this.subActividadBeanModel.setIdSubAct(idSubact);
            this.subActividadBeanModel.editarSubAct();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoSubact);");
        }
    }

    public void guardarSubAct() {
        this.subActividadBeanModel.guardarSubAct();
        this.subActividadBeanModel.cargarSubactividades();
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoSubact);");

    }

    /**
     * @param subActividadBeanModel the subActividadBeanModel to set
     */
    public void setSubActividadBeanModel(SubActividadBeanModel subActividadBeanModel) {
        this.subActividadBeanModel = subActividadBeanModel;
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
     * @return the idSubAct
     */
    public int getIdSubAct() {
        return this.subActividadBeanModel.getIdSubAct();
    }

    /**
     * @param idSubAct the idSubAct to set
     */
    public void setIdSubAct(int idSubAct) {
        this.subActividadBeanModel.setIdSubAct(idSubAct);
    }

    /**
     * @return the subActs
     */
    public List<OcUnidadCosto> getSubActs() {
        return this.subActividadBeanModel.getSubActs();
    }

    /**
     * @param subActs the subActs to set
     */
    public void setSubActs(List<OcUnidadCosto> subActs) {
        this.subActividadBeanModel.setSubActs(subActs);
    }

    /**
     * @return the subAct
     */
    public OcUnidadCosto getSubAct() {
        return this.subActividadBeanModel.getSubAct();
    }

    /**
     * @param subAct the subAct to set
     */
    public void setSubAct(OcUnidadCosto subAct) {
        this.subActividadBeanModel.setSubAct(subAct);
    }
}
