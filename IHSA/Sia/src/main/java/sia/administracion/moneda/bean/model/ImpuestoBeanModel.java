/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.moneda.bean.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.Impuesto;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.ImpuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.vo.ImpuestoVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named
@ViewScoped
public class ImpuestoBeanModel implements Serializable {

    @Inject
    private Sesion sesion;
    private List<ImpuestoVO> lstImpuesto;
    private ImpuestoVO newImpuesto;
    private List<SelectItem> companias;
    private int impuestoSeleccionada;
    private String companiaSeleccionada;   
    
    @Inject
    private CompaniaImpl companiaImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private ImpuestoImpl impuestoImpl;

    @PostConstruct
    public void init() {
        this.setSesion((Sesion) FacesUtils.getManagedBean("sesion"));
        this.cargarCompanias();
        this.setNewImpuesto(null);
        if (this.getCompaniaSeleccionada() == null || this.getCompaniaSeleccionada().isEmpty()) {
            this.setCompaniaSeleccionada(this.getSesion().getRfcCompania());
            refrescarTabla();
        }
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
     * @return the lstImpuesto
     */
    public List<ImpuestoVO> getLstImpuesto() {
        return lstImpuesto;
    }

    /**
     * @param lstImpuesto the lstImpuesto to set
     */
    public void setLstImpuesto(List<ImpuestoVO> lstImpuesto) {
        this.lstImpuesto = lstImpuesto;
    }

    /**
     * @return the newImpuesto
     */
    public ImpuestoVO getNewImpuesto() {
        return newImpuesto;
    }

    /**
     * @param newImpuesto the newImpuesto to set
     */
    public void setNewImpuesto(ImpuestoVO newImpuesto) {
        this.newImpuesto = newImpuesto;
    }

    /**
     * @return the companias
     */
    public List<SelectItem> getCompanias() {
        return companias;
    }

    /**
     * @param companias the companias to set
     */
    public void setCompanias(List<SelectItem> companias) {
        this.companias = companias;
    }

    /**
     * @return the impuestoSeleccionada
     */
    public int getImpuestoSeleccionada() {
        return impuestoSeleccionada;
    }

    /**
     * @param impuestoSeleccionada the impuestoSeleccionada to set
     */
    public void setImpuestoSeleccionada(int impuestoSeleccionada) {
        this.impuestoSeleccionada = impuestoSeleccionada;
    }

    /**
     * @return the companiaSeleccionada
     */
    public String getCompaniaSeleccionada() {
        return companiaSeleccionada;
    }

    /**
     * @param companiaSeleccionada the companiaSeleccionada to set
     */
    public void setCompaniaSeleccionada(String companiaSeleccionada) {
        this.companiaSeleccionada = companiaSeleccionada;
    }
    
    public void cargarCompanias() {
        this.setCompanias(companiaImpl.traerCompaniasByUsuario(this.getSesion().getUsuario().getId()));
    }
    
    public void refrescarTabla() {
        this.setLstImpuesto(impuestoImpl.traerImpuesto(this.getCompaniaSeleccionada(), 0));
    }
    
    public void cargarImpuesto(int idImpuesto){
        List<ImpuestoVO> impuestos = impuestoImpl.traerImpuesto(this.getCompaniaSeleccionada(), idImpuesto);
        if(impuestos.size() > 0){
            setNewImpuesto(impuestos.get(0));
        }
    }
    
    public void guardarImpuesto() {        
        try {
            if (getNewImpuesto()!= null) {
                if (getNewImpuesto().getId() > 0) {
                    Impuesto impuesto = impuestoImpl.find(getNewImpuesto().getId());
                    boolean guardar = false;
                    if(impuesto != null && !impuesto.getNombre().equals(getNewImpuesto().getNombre())){
                        impuesto.setNombre(getNewImpuesto().getNombre());
                        guardar = true;
                    }
                    if(impuesto != null && impuesto.getValor() != getNewImpuesto().getValor()){
                        impuesto.setValor(getNewImpuesto().getValor());
                        guardar = true;
                    }                    
                    if(guardar){
                        impuestoImpl.edit(impuesto);
                    }                    
                } else {
                    Impuesto impuesto = new Impuesto();
                    impuesto.setNombre(getNewImpuesto().getNombre());
                    impuesto.setValor(getNewImpuesto().getValor());
                    impuesto.setCompania(companiaImpl.find(getCompaniaSeleccionada()));
                    impuesto.setEliminado(Constantes.BOOLEAN_FALSE);
                    impuesto.setGenero(usuarioImpl.find(getSesion().getUsuario().getId()));
                    impuesto.setFechaGenero(new Date());
                    impuesto.setHoraGenero(new Date());
                    impuestoImpl.create(impuesto);
                }
            }
            
        } catch (Exception e) {            
            UtilLog4j.log.fatal(e);
        }
    }

}
