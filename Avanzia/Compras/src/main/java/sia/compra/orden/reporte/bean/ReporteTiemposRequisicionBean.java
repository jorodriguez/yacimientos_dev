/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.reporte.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;


import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.requisicion.impl.RequisicionTiemposVO;

/**
 *
 * @author jcarranza
 */
@Named (value = "reporteReqTiemposBean")
@ViewScoped
public class ReporteTiemposRequisicionBean implements Serializable {

    @Inject
    private RequisicionImpl requisicionImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;

    
    @Inject
    private UsuarioBean usuarioBean;

    private List<RequisicionTiemposVO> lstRequisiciones = new ArrayList<>();
    private int idGerencia;
    private int idEstatus;
    private Date fecha1;
    private Date fecha2;
    private String consecutivo;
    private List<SelectItem> gerencias = new ArrayList<>();
    private List<SelectItem> estatus = new ArrayList<>();

    @PostConstruct
    public void init() {
        setGerencias(gerenciaImpl.traerGerenciaAbreviaturaItems(usuarioBean.getUsuarioConectado().getApCampo().getId()));
        Calendar cal = Calendar.getInstance();
        fecha2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        fecha1 = cal.getTime();
        traerRequisiciones();

    }

    public void traerRequisicionTiempos() {
        traerRequisiciones();
    }

    private void traerRequisiciones() {
        List<RequisicionTiemposVO> lo = requisicionImpl.requisicionTiempos(
                usuarioBean.getUsuarioConectado().getApCampo().getId(), getConsecutivo(), getIdEstatus(), getIdGerencia(), getFecha1Txt(), getFecha2Txt());
        setLstRequisiciones(lo);

    }

    /**
     * @return the lstRequisiciones
     */
    public List<RequisicionTiemposVO> getLstRequisiciones() {
        return lstRequisiciones;
    }

    /**
     * @param lstRequisiciones the lstRequisiciones to set
     */
    public void setLstRequisiciones(List<RequisicionTiemposVO> lstRequisiciones) {
        this.lstRequisiciones = lstRequisiciones;
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
     * @return the idEstatus
     */
    public int getIdEstatus() {
        return idEstatus;
    }

    /**
     * @param idEstatus the idEstatus to set
     */
    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    /**
     * @return the fecha1
     */
    public Date getFecha1() {
        return fecha1;
    }

    /**
     * @param fecha1 the fecha1 to set
     */
    public void setFecha1(Date fecha1) {
        this.fecha1 = fecha1;
    }

    /**
     * @return the fecha2
     */
    public Date getFecha2() {
        return fecha2;
    }

    /**
     * @param fecha2 the fecha2 to set
     */
    public void setFecha2(Date fecha2) {
        this.fecha2 = fecha2;
    }

    /**
     * @return the consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * @return the fecha1Txt
     */
    public String getFecha1Txt() {
        String ret = "";
        if (getFecha1() != null) {
            ret = Constantes.FMT_yyyy_MM_dd.format(fecha1);
        }
        return ret;
    }

    /**
     * @return the fecha2Txt
     */
    public String getFecha2Txt() {
        String ret = "";
        if (getFecha2() != null) {
            ret = Constantes.FMT_yyyy_MM_dd.format(fecha2);
        }
        return ret;
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
     * @return the estatus
     */
    public List<SelectItem> getEstatus() {
        estatus = new ArrayList<>();        

        SelectItem i1  = new SelectItem(10, "Por Revisar");
        estatus.add(i1);
        SelectItem i10 = new SelectItem(15, "Por Aprobar");
        estatus.add(i10);
        SelectItem i2  = new SelectItem(20, "Por Asignar");
        estatus.add(i2);
        SelectItem i3  = new SelectItem(25, "En Costos");
        estatus.add(i3);
        SelectItem i5  = new SelectItem(35, "En Contabilidad");
        estatus.add(i5);
        SelectItem i6  = new SelectItem(40, "Asignada");
        estatus.add(i6);
        SelectItem i9  = new SelectItem(60, "Finalizada");
        estatus.add(i9);

        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(List<SelectItem> estatus) {
        this.estatus = estatus;
    }

}
