package com.ihsa.sia.inventario.beans.reportes;

import com.ihsa.sia.commons.AbstractBean;
import com.ihsa.sia.commons.Messages;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import sia.inventarios.service.InventarioImpl;
import sia.modelo.vo.inventarios.ReporteMayoresEntradasYSalidasVO;

import sia.inventarios.service.ReporteMayoresEntradasYSalidasImpl;
import sia.modelo.vo.inventarios.InventarioVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "entradasSalidas")
@ViewScoped
public class EntradasSalidasBean extends AbstractBean implements Serializable {

    @Inject
    private ReporteMayoresEntradasYSalidasImpl servicio;
    @Inject
    InventarioImpl inventarioImpl;
    private List<ReporteMayoresEntradasYSalidasVO> lista;
    private List<InventarioVO> existencias;
    private List<InventarioVO> existenciasMovimientos;
    private ReporteMayoresEntradasYSalidasVO filtro;

    @PostConstruct
    public void init() {
        filtro = new ReporteMayoresEntradasYSalidasVO();
        existencias= new ArrayList<InventarioVO>();
        cargarListaConFiltros();
        //
        llenarExistencias();
        llenarExistenciasMovimientos();
    }
    
    public void llenarExistencias(){
     existencias =    inventarioImpl.inventario(super.getCampoId());
    }
    
    public void llenarExistenciasMovimientos(){
        setExistenciasMovimientos(inventarioImpl.inventarioMovimientos(super.getCampoId()));
    }
    
    public void cargarListaConFiltros() {
        try {
            setLista(servicio.obtenerLista(getFiltro()));
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }
    
    public void reestablecer() {
        filtro = new ReporteMayoresEntradasYSalidasVO();
        cargarListaConFiltros();
    }

    public void fechaValidador(FacesContext context, UIComponent component, Object value) {
        if(getFiltro().getFechaInicio() == null && getFiltro().getFechaFin() == null) return;
        if(getFiltro().getFechaInicio() == null) {
            lanzarValidacionExcepcion();
        }
        if(getFiltro().getFechaFin() == null) {
            lanzarValidacionExcepcion();
        }
        //Si la fecha inicio es mayor a la fecha fin
        if(getFiltro().getFechaInicio().compareTo(getFiltro().getFechaFin()) > 0) {
            lanzarValidacionExcepcion();
        }
    }
    public void lanzarValidacionExcepcion() throws ValidatorException{
        throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                Messages.getString("sia.inventarios.reportes.rango.fechaValidacion"), null));
    }
    
    public List<ReporteMayoresEntradasYSalidasVO> getLista() {
        return lista;
    }

    public void setLista(List<ReporteMayoresEntradasYSalidasVO> lista) {
        this.lista = lista;
    }

    public ReporteMayoresEntradasYSalidasVO getFiltro() {
        return filtro;
    }

    /**
     * @return the existencias
     */
    public List<InventarioVO> getExistencias() {
        return existencias;
    }

    /**
     * @param existencias the existencias to set
     */
    public void setExistencias(List<InventarioVO> existencias) {
        this.existencias = existencias; 
    }

    /**
     * @return the existenciasMovimientos
     */
    public List<InventarioVO> getExistenciasMovimientos() {
        return existenciasMovimientos;
    }

    /**
     * @param existenciasMovimientos the existenciasMovimientos to set
     */
    public void setExistenciasMovimientos(List<InventarioVO> existenciasMovimientos) {
        this.existenciasMovimientos = existenciasMovimientos;
    }

}
