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
public class OrdenMovilVo {
    
    private int id;
    private String codigo;
    private String referencia;
    private List<OrdenDetalleMovilVo> articulos = new ArrayList<OrdenDetalleMovilVo>();

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
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * @return the articulos
     */
    public List<OrdenDetalleMovilVo> getArticulos() {
        return articulos;
    }

    /**
     * @param articulos the articulos to set
     */
    public void setArticulos(List<OrdenDetalleMovilVo> articulos) {
        this.articulos = articulos;
    }
    
}
