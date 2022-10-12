/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ihsa.sia.api.mobile.inventario;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mluis
 */
public class AlmacenUbicacionMovilVo {
    private String almacen;
    private List<CeldaMovilVo> celdas = new ArrayList<CeldaMovilVo>();

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
     * @return the celdas
     */
    public List<CeldaMovilVo> getCeldas() {
        return celdas;
    }

    /**
     * @param celdas the celdas to set
     */
    public void setCeldas(List<CeldaMovilVo> celdas) {
        this.celdas = celdas;
    }
    
}
