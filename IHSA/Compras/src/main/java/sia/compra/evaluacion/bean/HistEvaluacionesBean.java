/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.evaluacion.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;


import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.contrato.vo.EvaluacionRespuestaVo;
import sia.servicios.evaluacion.impl.CvEvaluacionImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionRespImpl;
import sia.servicios.evaluacion.impl.CvHistorialExternoImpl;

/**
 *
 * @author jcarranza
 */
@Named (value = "histEvaluacionesBean")
@ViewScoped
public class HistEvaluacionesBean implements Serializable{

    public HistEvaluacionesBean() {

    }

    @Inject
    private UsuarioBean usuarioBean;

    private List<EvaluacionRespuestaVo> lstEvaluacionesSer = new ArrayList<>();
    private List<EvaluacionRespuestaVo> lstEvaluacionesSum = new ArrayList<>();
    private List<EvaluacionRespuestaVo> lstEvaluacionesObr = new ArrayList<>();
    
    private int idGerencia;
    private int idProveedor;
    private Date fechaI;
    private Date fechaF;
    
    private List<SelectItem> gerencias;
    private List<SelectItem> proveedores;

    @Inject
    CvEvaluacionImpl cvEvaluacionImpl;
    @Inject
    CvEvaluacionRespImpl cvEvaluacionRespImpl;
    @Inject
    CvHistorialExternoImpl cvHistorialExternoImpl;

    @PostConstruct
    public void iniciar() {
        Calendar cal = Calendar.getInstance();
        this.setFechaF(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -60);
        this.setFechaI(cal.getTime());
        
        this.setLstEvaluacionesSer(cvEvaluacionImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_SERVICIO));
        this.setLstEvaluacionesSum(cvEvaluacionImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_SUMINISTRO));
        this.setLstEvaluacionesObr(cvEvaluacionImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_OBRA));
        
        this.getLstEvaluacionesSer().addAll(cvHistorialExternoImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_SERVICIO));
        this.getLstEvaluacionesSum().addAll(cvHistorialExternoImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_SUMINISTRO));
        this.getLstEvaluacionesObr().addAll(cvHistorialExternoImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_OBRA));

        setProveedores(cvEvaluacionImpl.getProveedores(usuarioBean.getUsuarioConectado().getApCampo().getId()));
        setGerencias(cvEvaluacionImpl.getGerencias(usuarioBean.getUsuarioConectado().getApCampo().getId()));
        //setConvenios(cvEvaluacionImpl.getConvenios(usuarioBean.getUsuarioConectado().getApCampo().getId()));        
    }

    public void iniciarEvaluaciones() {
        this.iniciar();
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    public void filtrarEvaluaciones() {        
        this.setLstEvaluacionesSer(cvEvaluacionImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_SERVICIO));
        this.getLstEvaluacionesSer().addAll(cvHistorialExternoImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_SERVICIO));
        this.setLstEvaluacionesSum(cvEvaluacionImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_SUMINISTRO));
        this.getLstEvaluacionesSum().addAll(cvHistorialExternoImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_SUMINISTRO));
        this.setLstEvaluacionesObr(cvEvaluacionImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_OBRA));
        this.getLstEvaluacionesObr().addAll(cvHistorialExternoImpl.traerEvaluacionRespuestas(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia(), getIdProveedor(), getFechaI(), getFechaF(), Constantes.TIPO_CLASIFICACION_CONVENIO_OBRA));
    }

    /**
     * @return the lstEvaluacionesSer
     */
    public List<EvaluacionRespuestaVo> getLstEvaluacionesSer() {
        return lstEvaluacionesSer;
    }

    /**
     * @param lstEvaluacionesSer the lstEvaluacionesSer to set
     */
    public void setLstEvaluacionesSer(List<EvaluacionRespuestaVo> lstEvaluacionesSer) {
        this.lstEvaluacionesSer = lstEvaluacionesSer;
    }

    /**
     * @return the lstEvaluacionesSum
     */
    public List<EvaluacionRespuestaVo> getLstEvaluacionesSum() {
        return lstEvaluacionesSum;
    }

    /**
     * @param lstEvaluacionesSum the lstEvaluacionesSum to set
     */
    public void setLstEvaluacionesSum(List<EvaluacionRespuestaVo> lstEvaluacionesSum) {
        this.lstEvaluacionesSum = lstEvaluacionesSum;
    }

    /**
     * @return the lstEvaluacionesObr
     */
    public List<EvaluacionRespuestaVo> getLstEvaluacionesObr() {
        return lstEvaluacionesObr;
    }

    /**
     * @param lstEvaluacionesObr the lstEvaluacionesObr to set
     */
    public void setLstEvaluacionesObr(List<EvaluacionRespuestaVo> lstEvaluacionesObr) {
        this.lstEvaluacionesObr = lstEvaluacionesObr;
    }
   
    /**
     * @return the gerencias
     */
    public List<SelectItem> getGerencias() {
        return gerencias;
    }

    /**
     * @param gerencias the gerencias to set
     */
    public void setGerencias(List<SelectItem> gerencias) {
        this.gerencias = gerencias;
    }

    /**
     * @return the proveedores
     */
    public List<SelectItem> getProveedores() {
        return proveedores;
    }

    /**
     * @param proveedores the proveedores to set
     */
    public void setProveedores(List<SelectItem> proveedores) {
        this.proveedores = proveedores;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * @return the fechaI
     */
    public Date getFechaI() {
        return fechaI;
    }

    /**
     * @param fechaI the fechaI to set
     */
    public void setFechaI(Date fechaI) {
        this.fechaI = fechaI;
    }

    /**
     * @return the fechaF
     */
    public Date getFechaF() {
        return fechaF;
    }

    /**
     * @param fechaF the fechaF to set
     */
    public void setFechaF(Date fechaF) {
        this.fechaF = fechaF;
    }

}
