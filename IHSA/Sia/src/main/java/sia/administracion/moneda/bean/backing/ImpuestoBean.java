/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.moneda.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.administracion.moneda.bean.model.ImpuestoBeanModel;
import sia.servicios.sistema.vo.ImpuestoVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "impuestoBean")
@RequestScoped
public class ImpuestoBean implements Serializable {

    @Inject
    private ImpuestoBeanModel impuestoBeanModel;

    /**
     * @return the impuestoBeanModel
     */
    public ImpuestoBeanModel getImpuestoBeanModel() {
        return impuestoBeanModel;
    }

    /**
     * @param impuestoBeanModel the impuestoBeanModel to set
     */
    public void setImpuestoBeanModel(ImpuestoBeanModel impuestoBeanModel) {
        this.impuestoBeanModel = impuestoBeanModel;
    }

    /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return getImpuestoBeanModel().getSesion();
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        getImpuestoBeanModel().setSesion(sesion);
    }

    /**
     * @return the lstImpuesto
     */
    public List<ImpuestoVO> getLstImpuesto() {
        return getImpuestoBeanModel().getLstImpuesto();
    }

    /**
     * @param lstImpuesto the lstImpuesto to set
     */
    public void setLstImpuesto(List<ImpuestoVO> lstImpuesto) {
        getImpuestoBeanModel().setLstImpuesto(lstImpuesto);
    }

    /**
     * @return the newImpuesto
     */
    public ImpuestoVO getNewImpuesto() {
        return getImpuestoBeanModel().getNewImpuesto();
    }

    /**
     * @param newImpuesto the newImpuesto to set
     */
    public void setNewImpuesto(ImpuestoVO newImpuesto) {
        getImpuestoBeanModel().setNewImpuesto(newImpuesto);
    }

    /**
     * @return the companias
     */
    public List<SelectItem> getCompanias() {
        return getImpuestoBeanModel().getCompanias();
    }

    /**
     * @param companias the companias to set
     */
    public void setCompanias(List<SelectItem> companias) {
        getImpuestoBeanModel().setCompanias(companias);
    }

    /**
     * @return the impuestoSeleccionada
     */
    public int getImpuestoSeleccionada() {
        return getImpuestoBeanModel().getImpuestoSeleccionada();
    }

    /**
     * @param impuestoSeleccionada the impuestoSeleccionada to set
     */
    public void setImpuestoSeleccionada(int impuestoSeleccionada) {
        getImpuestoBeanModel().setImpuestoSeleccionada(impuestoSeleccionada);
    }

    /**
     * @return the companiaSeleccionada
     */
    public String getCompaniaSeleccionada() {
        return getImpuestoBeanModel().getCompaniaSeleccionada();
    }

    /**
     * @param companiaSeleccionada the companiaSeleccionada to set
     */
    public void setCompaniaSeleccionada(String companiaSeleccionada) {
        getImpuestoBeanModel().setCompaniaSeleccionada(companiaSeleccionada);
    }

    public void crearImpuesto() {
        try {
            this.setNewImpuesto(new ImpuestoVO());
                PrimeFaces.current().executeScript("$('dialogoPopUpImpuesto').modal(show);");
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void editarImpuesto(int idImpuesto) {
        try {
            if (idImpuesto > 0) {
                cargarImpuesto(idImpuesto);
                PrimeFaces.current().executeScript("$('dialogoPopUpImpuesto').modal(show);");
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void guardarImpuesto() {
        try {
            getImpuestoBeanModel().guardarImpuesto();
            this.refrescarTabla();
            this.setNewImpuesto(null);
            String metodo = ";cerrarDialogoCrearImpuesto();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void cargarImpuesto(int idImpuesto) {
        getImpuestoBeanModel().cargarImpuesto(idImpuesto);
    }

    public void refrescarTabla() {
        getImpuestoBeanModel().refrescarTabla();
    }

    public void cambiarValorCompania() {
        try {
            this.refrescarTabla();
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
}
