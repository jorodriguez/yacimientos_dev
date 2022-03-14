/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import sia.modelo.OcUnidadCosto;
import sia.servicios.orden.impl.OcUnidadCostoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@ManagedBean
@ViewScoped
public class SubActividadBeanModel {
    
    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    @EJB
    private OcUnidadCostoImpl ocUnidadCostoImpl;
    
    private int idCampo;
    private int idSubAct;
    private List<OcUnidadCosto> subActs;
    private OcUnidadCosto subAct;
    
    public SubActividadBeanModel(){
    
    }
    
    public void inicia() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        cargarSubactividades();
    }
    
    public void cargarSubactividades(){ 
        setSubActs(ocUnidadCostoImpl.getAllActive());
    }
    
    public void nuevaSubAct() {
        setSubAct(new OcUnidadCosto());
        getSubAct().setId(0);
    }

    public void editarSubAct() {
        setSubAct(this.ocUnidadCostoImpl.find(getIdSubAct()));
    }

    public void borrarSubAct() {
        setSubAct(this.ocUnidadCostoImpl.find(getIdSubAct()));
        if (getSubAct() != null && getSubAct().getId() > 0) {
            getSubAct().setEliminado(true);
            getSubAct().setModifico(getSesion().getUsuario());
            getSubAct().setFechaModifico(new Date());
            getSubAct().setHoraModifico(new Date());

            this.ocUnidadCostoImpl.edit(getSubAct());
        }
    }

    public void guardarSubAct() {
        if (getSubAct() != null) {
            if (getSubAct().getId() > 0) {
                                
                getSubAct().setModifico(getSesion().getUsuario());
                getSubAct().setFechaModifico(new Date());
                getSubAct().setHoraModifico(new Date());

                
                this.ocUnidadCostoImpl.edit(getSubAct());
                
            } else {
                getSubAct().setEliminado(false);
                getSubAct().setGenero(getSesion().getUsuario());
                getSubAct().setFechaGenero(new Date());
                getSubAct().setHoraGenero(new Date());

                this.ocUnidadCostoImpl.create(getSubAct());
            }
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
     * @return the idCampo
     */
    public int getIdCampo() {
        return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        this.idCampo = idCampo;
    }

    /**
     * @return the idSubAct
     */
    public int getIdSubAct() {
        return idSubAct;
    }

    /**
     * @param idSubAct the idSubAct to set
     */
    public void setIdSubAct(int idSubAct) {
        this.idSubAct = idSubAct;
    }

    /**
     * @return the subActs
     */
    public List<OcUnidadCosto> getSubActs() {
        return subActs;
    }

    /**
     * @param subActs the subActs to set
     */
    public void setSubActs(List<OcUnidadCosto> subActs) {
        this.subActs = subActs;
    }

    /**
     * @return the subAct
     */
    public OcUnidadCosto getSubAct() {
        return subAct;
    }

    /**
     * @param subAct the subAct to set
     */
    public void setSubAct(OcUnidadCosto subAct) {
        this.subAct = subAct;
    }
    
}
