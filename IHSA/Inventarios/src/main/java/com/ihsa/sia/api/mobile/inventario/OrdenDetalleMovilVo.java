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
public class OrdenDetalleMovilVo {
    private int id;
    private String nombreArticulo;
    private int idArticulo;
    private String unidad;
    private double cantidad;
    private double precio;
    private double totalRecibido;
    private double totalPendiente;

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
     * @return the nombreArticulo
     */
    public String getNombreArticulo() {
        return nombreArticulo;
    }

    /**
     * @param nombreArticulo the nombreArticulo to set
     */
    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    /**
     * @return the idArticulo
     */
    public int getIdArticulo() {
        return idArticulo;
    }

    /**
     * @param idArticulo the idArticulo to set
     */
    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
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

    /**
     * @return the cantidad
     */
    public double getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * @return the totalRecibido
     */
    public double getTotalRecibido() {
        return totalRecibido;
    }

    /**
     * @param totalRecibido the totalRecibido to set
     */
    public void setTotalRecibido(double totalRecibido) {
        this.totalRecibido = totalRecibido;
    }

    /**
     * @return the totalPendiente
     */
    public double getTotalPendiente() {
        return totalPendiente;
    }

    /**
     * @param totalPendiente the totalPendiente to set
     */
    public void setTotalPendiente(double totalPendiente) {
        this.totalPendiente = totalPendiente;
    }
}
