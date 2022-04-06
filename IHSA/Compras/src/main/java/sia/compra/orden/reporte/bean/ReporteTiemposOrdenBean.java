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
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.orden.impl.OrdenTiemposVO;

/**
 *
 * @author jcarranza
 */

@Named (value = "reporteTiemposOrdenBean")
@ViewScoped
public class ReporteTiemposOrdenBean implements Serializable {

    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;

    
    @Inject
    private UsuarioBean usuarioBean;

    private List<OrdenTiemposVO> lstOrdenes = new ArrayList<>();
    private int idGerencia;
    private int idEstatus;
    private Date fecha1;
    private Date fecha2;
    private String consecutivo;
    private String proveedor;
    private List<SelectItem> gerencias = new ArrayList<>();
    private List<SelectItem> estatus = new ArrayList<>();

    @PostConstruct
    public void init() {
        setGerencias(gerenciaImpl.traerGerenciaAbreviaturaItems(usuarioBean.getUsuarioConectado().getApCampo().getId()));
        Calendar cal = Calendar.getInstance();
        fecha2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        fecha1 = cal.getTime();
        traerOrdenes();

    }

    public void traerOrdenesTiempos() {
        traerOrdenes();
    }

    private void traerOrdenes() {
        List<OrdenTiemposVO> lo = ordenImpl.ordenTiempos(
                usuarioBean.getUsuarioConectado().getApCampo().getId(), getConsecutivo(), getIdEstatus(), getIdGerencia(), getFecha1Txt(), getFecha2Txt(), getProveedor());
        setLstOrdenes(lo);

    }

    /**
     * @return the lstOrdenes
     */
    public List<OrdenTiemposVO> getLstOrdenes() {
        return lstOrdenes;
    }

    /**
     * @param lstOrdenes the lstOrdenes to set
     */
    public void setLstOrdenes(List<OrdenTiemposVO> lstOrdenes) {
        this.lstOrdenes = lstOrdenes;
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
        
        SelectItem i1 = new SelectItem(110, "En VoBo"); //Solicitada
        estatus.add(i1);
        SelectItem i2 = new SelectItem(120, "Por Revisar"); //Visto Bueno
        estatus.add(i2);
//        SelectItem i3 = new SelectItem(125, "Revisada Socio"); //Revisada Socio
//        estatus.add(i3);
        SelectItem i4 = new SelectItem(130, "Por Aprobar"); //Revisada
        estatus.add(i4);
        SelectItem i5 = new SelectItem(135, "Socio"); //Por Aprobar Socio
        estatus.add(i5);
        SelectItem i6 = new SelectItem(140, "Por Autorizar"); //Aprobada
        estatus.add(i6);
        SelectItem i9 = new SelectItem(150, "Por Enviar Proveedor"); //Autorizada
        estatus.add(i9);
        SelectItem i7 = new SelectItem(145, "En carta de Intención"); //Carta de Intención Enviada (Portal de proveedores)
        estatus.add(i7);
        SelectItem i8 = new SelectItem(148, "En Jurídico)"); //Carta de Intención Aceptada (Revisa Jurídico)
        estatus.add(i8);
        
//        SelectItem i10 = new SelectItem(151, "Autorizada Licitacion Internacional"); //Autorizada Licitacion Internacional
//        estatus.add(i10);
//        SelectItem i11 = new SelectItem(155, "Generar excel"); //Generar excel
//        estatus.add(i11);
        SelectItem i12 = new SelectItem(160, "En Proveedor SR"); //Enviada al proveedor
        estatus.add(i12);
        SelectItem i13 = new SelectItem(163, "En Proveedor RP"); //Enviada al proveedor
        estatus.add(i13);
        SelectItem i14 = new SelectItem(165, "En Proveedor CR"); //Enviada al proveedor
        estatus.add(i14);
        
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(List<SelectItem> estatus) {
        this.estatus = estatus;
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
    
    public boolean isSocio(){
        return usuarioBean.getUsuarioConectado().getApCampo().getCompania().isSocio();
    }

    /**
     * @return the proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

}
