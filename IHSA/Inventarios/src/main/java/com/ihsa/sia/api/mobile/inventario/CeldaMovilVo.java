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
public class CeldaMovilVo {
    private int id;
    private String celda;

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
     * @return the celda
     */
    public String getCelda() {
        return celda;
    }

    /**
     * @param celda the celda to set
     */
    public void setCelda(String celda) {
        this.celda = celda;
    }

}
