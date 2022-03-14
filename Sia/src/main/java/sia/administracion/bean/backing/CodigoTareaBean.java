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
import sia.administracion.bean.model.CodigoTareaBeanModel;
import sia.modelo.OcCodigoTarea;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author jcarranza
 */
@ManagedBean(name = "codigoTareaBean")
@ViewScoped
public class CodigoTareaBean {

    @ManagedProperty(value = "#{codigoTareaBeanModel}")
    private CodigoTareaBeanModel codigoTareaBeanModel;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;

    public CodigoTareaBean() {

    }

    @PostConstruct
    public void llenaCampo() {
        this.codigoTareaBeanModel.inicia();
    }

    public void nuevoCodigo() {
        this.codigoTareaBeanModel.nuevoCodigo();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoCodigo);");
    }

    public void borrarCodigo() {
        int idCodigo = Integer.parseInt(FacesUtils.getRequestParameter("idCodigo"));
        if (idCodigo > 0) {
            this.codigoTareaBeanModel.setIdCodigo(idCodigo);
            this.codigoTareaBeanModel.borrarCodigo();
            this.codigoTareaBeanModel.cargarCodigos();
        }
    }

    public void editarCodigo() {
        int idCodigo = Integer.parseInt(FacesUtils.getRequestParameter("idCodigo"));
        if (idCodigo > 0) {
            this.codigoTareaBeanModel.setIdCodigo(idCodigo);
            this.codigoTareaBeanModel.editarCodigo();
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoCodigo);");
        }
    }

    public void guardarCodigo() {
        this.codigoTareaBeanModel.guardarCodigo();
        this.codigoTareaBeanModel.cargarCodigos();
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCodigo);");
    }

    /**
     * @param codigoTareaBeanModel the codigoTareaBeanModel to set
     */
    public void setCodigoTareaBeanModel(CodigoTareaBeanModel codigoTareaBeanModel) {
        this.codigoTareaBeanModel = codigoTareaBeanModel;
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
     * @return the idCodigo
     */
    public int getIdCodigo() {
        return this.codigoTareaBeanModel.getIdCodigo();
    }

    /**
     * @param idCodigo the idCodigo to set
     */
    public void setIdCodigo(int idCodigo) {
        this.codigoTareaBeanModel.setIdCodigo(idCodigo);
    }

    /**
     * @return the codigos
     */
    public List<OcCodigoTarea> getCodigos() {
        return this.codigoTareaBeanModel.getCodigos();
    }

    /**
     * @param codigos the codigos to set
     */
    public void setCodigos(List<OcCodigoTarea> codigos) {
        this.codigoTareaBeanModel.setCodigos(codigos);
    }

    /**
     * @return the codigo
     */
    public OcCodigoTarea getCodigo() {
        return this.codigoTareaBeanModel.getCodigo();
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(OcCodigoTarea codigo) {
        this.codigoTareaBeanModel.setCodigo(codigo);
    }

}
