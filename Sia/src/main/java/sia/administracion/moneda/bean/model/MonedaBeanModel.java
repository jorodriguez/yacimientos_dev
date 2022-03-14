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
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.modelo.Moneda;
import sia.servicios.catalogos.impl.CompaniaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */

@ManagedBean
@CustomScoped(value = "#{window}")
public class MonedaBeanModel implements Serializable {
    
    private Sesion sesion;
    private List<MonedaVO> lstMoneda;
    private MonedaVO newMoneda;
    private List<SelectItem> companias;
    private String companiaSeleccionada;
    
    @EJB private MonedaImpl monedaImpl;
    @EJB private CompaniaImpl companiaImpl;
    @EJB private UsuarioImpl usuarioImpl;
    
    @PostConstruct
    public void init() {
        this.setSesion((Sesion) FacesUtils.getManagedBean("sesion"));                        
        this.cargarCompanias();
        this.setNewMoneda(null);
        if(this.getCompaniaSeleccionada() == null || this.getCompaniaSeleccionada().isEmpty()){
            this.setCompaniaSeleccionada(this.getSesion().getRfcCompania());
            refrescarTabla();
        }        
    }

    public void refrescarTabla() {        
        this.setLstMoneda(monedaImpl.traerMonedasPorCompania(this.getCompaniaSeleccionada(), 0));
    }
    
    public void cargarCompanias() {        
        this.setCompanias(companiaImpl.traerCompaniasByUsuario(this.getSesion().getUsuario().getId()));
    }
    
    public void cargarMoneda(int idMoneda){
        List<MonedaVO> monedas = monedaImpl.traerMonedasPorCompania(this.getSesion().getRfcCompania(), idMoneda);
        if(monedas.size() > 0){
            setNewMoneda(monedas.get(0));
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
     * @return the lstMoneda
     */
    public List<MonedaVO> getLstMoneda() {
        return lstMoneda;
    }

    /**
     * @param lstMoneda the lstMoneda to set
     */
    public void setLstMoneda(List<MonedaVO> lstMoneda) {
        this.lstMoneda = lstMoneda;
    }

    /**
     * @return the newMoneda
     */
    public MonedaVO getNewMoneda() {
        return newMoneda;
    }

    /**
     * @param newMoneda the newMoneda to set
     */
    public void setNewMoneda(MonedaVO newMoneda) {
        this.newMoneda = newMoneda;
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
    
    public void guardarMoneda() {        
        try {
            if (getNewMoneda() != null) {
                if (getNewMoneda().getId() > 0) {
                    Moneda moneda = monedaImpl.find(getNewMoneda().getId());
                    boolean guardar = false;
                    if(moneda != null && !moneda.getNombre().equals(getNewMoneda().getNombre())){
                        moneda.setNombre(getNewMoneda().getNombre());
                        guardar = true;
                    }
                    if(moneda != null && !moneda.getSiglas().equals(getNewMoneda().getSiglas())){
                        moneda.setSiglas(getNewMoneda().getSiglas());
                        guardar = true;
                    }
                    if(moneda != null && 
                            (getNewMoneda().isActivo() && moneda.isEliminado())                            
                            ){
                        moneda.setEliminado(Constantes.BOOLEAN_FALSE);
                        guardar = true;
                    }
                    if(moneda != null && 
                            (!getNewMoneda().isActivo() && moneda.isEliminado())                            
                            ){
                        moneda.setEliminado(Constantes.BOOLEAN_TRUE);
                        guardar = true;
                    }
                    if(guardar){
                        monedaImpl.edit(moneda);
                    }                    
                } else {
                    Moneda moneda = new Moneda();
                    moneda.setNombre(getNewMoneda().getNombre());
                    moneda.setSiglas(getNewMoneda().getSiglas());
                    moneda.setCompania(companiaImpl.find(getNewMoneda().getCompania()));
                    moneda.setEliminado(Constantes.BOOLEAN_FALSE);
                    moneda.setGenero(usuarioImpl.find(getSesion().getUsuario().getId()));
                    moneda.setFechaGenero(new Date());
                    moneda.setHoraGenero(new Date());
                    monedaImpl.create(moneda);
                }
            }
            
        } catch (Exception e) {            
            UtilLog4j.log.fatal(e);
        }
    }
    
    public void desactivarMoneda() {        
        try {
            if (getNewMoneda() != null) {
                if (getNewMoneda().getId() > 0) {
                    Moneda moneda = monedaImpl.find(getNewMoneda().getId());                                      
                    if(moneda != null){
                        moneda.setEliminado(Constantes.BOOLEAN_TRUE);
                        moneda.setFechaModifico(new Date());
                        moneda.setHoraModifico(new Date());
                        moneda.setModifico(getSesion().getUsuario());
                        monedaImpl.edit(moneda);
                    }
                } 
            }            
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
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
}
