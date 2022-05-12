/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ihsa.sia.api.mobile.inventario;

/**
 *
 * @author mluis
 */
public class DetalleSolicitudMaterialMovilVo {

    private int id;
    private int solicitudId;
    private String articulo;
    private String unidad;
    private int articuloId;
    private double disponible;
    private double cantidadSolicitada;
    private double cantidadRecibida;
    private String usuarioRecoge;
    private String observacion;

    /**
     * @return the solicitudId
     */
    public int getSolicitudId() {
        return solicitudId;
    }

    /**
     * @param solicitudId the solicitudId to set
     */
    public void setSolicitudId(int solicitudId) {
        this.solicitudId = solicitudId;
    }
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
     * @return the articulo
     */
    public String getArticulo() {
        return articulo;
    }

    /**
     * @param articulo the articulo to set
     */
    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    /**
     * @return the articuloId
     */
    public int getArticuloId() {
        return articuloId;
    }

    /**
     * @param articuloId the articuloId to set
     */
    public void setArticuloId(int articuloId) {
        this.articuloId = articuloId;
    }

    /**
     * @return the disponible
     */
    public double getDisponible() {
        return disponible;
    }

    /**
     * @param disponible the disponible to set
     */
    public void setDisponible(double disponible) {
        this.disponible = disponible;
    }

    /**
     * @return the cantidadSolicitada
     */
    public double getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    /**
     * @param cantidadSolicitada the cantidadSolicitada to set
     */
    public void setCantidadSolicitada(double cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    /**
     * @return the cantidadRecibida
     */
    public double getCantidadRecibida() {
        return cantidadRecibida;
    }

    /**
     * @param cantidadRecibida the cantidadRecibida to set
     */
    public void setCantidadRecibida(double cantidadRecibida) {
        this.cantidadRecibida = cantidadRecibida;
    }

    /**
     * @return the usuarioRecoge
     */
    public String getUsuarioRecoge() {
        return usuarioRecoge;
    }

    /**
     * @param usuarioRecoge the usuarioRecoge to set
     */
    public void setUsuarioRecoge(String usuarioRecoge) {
        this.usuarioRecoge = usuarioRecoge;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    /**
     * @return the unidad
     */
    public String getUnidad() {
        return unidad;
    }

    /**
     * @param unidad the unidad to set
     */
    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
            
}
