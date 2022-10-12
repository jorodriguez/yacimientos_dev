/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ihsa.sia.api.mobile.inventario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;

/**
 *
 * @author mluis
 */
public class SolicitudMaterialMovilVo {
    
    private int id;
    private String almacen;
    private String folio;
    private String solicita;
    private String recogeMaterial;
    private Date fechaRequiere;
    private List<SolicitudMaterialMovilVo> solicitudes = new ArrayList<SolicitudMaterialMovilVo>();
    private List<DetalleSolicitudMaterialMovilVo> detalleSolicitud = new ArrayList<DetalleSolicitudMaterialMovilVo>();

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the almacen
     */
    public String getAlmacen() {
        return almacen;
    }

    /**
     * @param almacen the almacen to set
     */
    public void setAlmacen(String almacen) {
        this.almacen = almacen;
    }

    /**
     * @return the folio
     */
    public String getFolio() {
        return folio;
    }

    /**
     * @param folio the folio to set
     */
    public void setFolio(String folio) {
        this.folio = folio;
    }

    /**
     * @return the solicita
     */
    public String getSolicita() {
        return solicita;
    }

    /**
     * @param solicita the solicita to set
     */
    public void setSolicita(String solicita) {
        this.solicita = solicita;
    }

    /**
     * @return the fechaRequiere
     */
    public Date getFechaRequiere() {
        return fechaRequiere;
    }

    /**
     * @param fechaRequiere the fechaRequiere to set
     */
    public void setFechaRequiere(Date fechaRequiere) {
        this.fechaRequiere = fechaRequiere;
    }

    /**
     * @return the solicitudes
     */
    public List<SolicitudMaterialMovilVo> getSolicitudes() {
        return solicitudes;
    }

    /**
     * @param solicitudes the solicitudes to set
     */
    public void setSolicitudes(List<SolicitudMaterialMovilVo> solicitudes) {
        this.solicitudes = solicitudes;
    }

    /**
     * @return the detalleSolicitud
     */
    public List<DetalleSolicitudMaterialMovilVo> getDetalleSolicitud() {
        return detalleSolicitud;
    }

    /**
     * @param detalleSolicitud the detalleSolicitud to set
     */
    public void setDetalleSolicitud(List<DetalleSolicitudMaterialMovilVo> detalleSolicitud) {
        this.detalleSolicitud = detalleSolicitud;
    }

    /**
     * @return the recogeMaterial
     */
    public String getRecogeMaterial() {
        return recogeMaterial;
    }

    /**
     * @param recogeMaterial the recogeMaterial to set
     */
    public void setRecogeMaterial(String recogeMaterial) {
        this.recogeMaterial = recogeMaterial;
    }

}
