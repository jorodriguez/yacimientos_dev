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
import sia.administracion.bean.model.NombreTareaBeanModel;
import sia.modelo.OcNombreTarea;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author jcarranza
 */
@ManagedBean(name = "nombreTareaBean")
@ViewScoped
public class NombreTareaBean {

    @ManagedProperty(value = "#{nombreTareaBeanModel}")
    private NombreTareaBeanModel nombreTareaBeanModel;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;

    public NombreTareaBean() {

    }

    @PostConstruct
    public void llenaCampo() {
        this.nombreTareaBeanModel.inicia();
    }

    public void nuevoNombre() {
        this.nombreTareaBeanModel.nuevoNombre();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNombretarea);");
    }

    public void borrarNombre() {
        int idNombre = Integer.parseInt(FacesUtils.getRequestParameter("idNombre"));
        if (idNombre > 0) {
            this.nombreTareaBeanModel.setIdNombre(idNombre);
            this.nombreTareaBeanModel.borrarNombre();
            this.nombreTareaBeanModel.cargarNombres();
        }
    }

    public void editarNombre() {
        int idNombre = Integer.parseInt(FacesUtils.getRequestParameter("idNombre"));
        if (idNombre > 0) {
            this.nombreTareaBeanModel.setIdNombre(idNombre);
            this.nombreTareaBeanModel.editarNombre();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoNombretarea);");
        }
    }

    public void guardarNombre() {
        this.nombreTareaBeanModel.guardarNombre();
        this.nombreTareaBeanModel.cargarNombres();
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNombretarea);");
    }

    /**
     * @param nombreTareaBeanModel the nombreTareaBeanModel to set
     */
    public void setNombreTareaBeanModel(NombreTareaBeanModel nombreTareaBeanModel) {
        this.nombreTareaBeanModel = nombreTareaBeanModel;
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
     * @return the idNombre
     */
    public int getIdNombre() {
        return this.nombreTareaBeanModel.getIdNombre();
    }

    /**
     * @param idNombre the idNombre to set
     */
    public void setIdNombre(int idNombre) {
        this.nombreTareaBeanModel.setIdNombre(idNombre);
    }

    /**
     * @return the nombres
     */
    public List<OcNombreTarea> getNombres() {
        return this.nombreTareaBeanModel.getNombres();
    }

    /**
     * @param nombres the nombres to set
     */
    public void setNombres(List<OcNombreTarea> nombres) {
        this.nombreTareaBeanModel.setNombres(nombres);
    }

    /**
     * @return the nombre
     */
    public OcNombreTarea getNombre() {
        return this.nombreTareaBeanModel.getNombre();
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(OcNombreTarea nombre) {
        this.nombreTareaBeanModel.setNombre(nombre);
    }

}
