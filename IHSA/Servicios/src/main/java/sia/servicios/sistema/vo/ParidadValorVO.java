/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import java.util.Date;

/**
 *
 * @author ihsa
 */
public class ParidadValorVO {
    
    private int id;
    private int idParidad;
    private double valor;
    private Date fechaValido =  new Date();   
    private int dia;

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
     * @return the idParidad
     */
    public int getIdParidad() {
        return idParidad;
    }

    /**
     * @param idParidad the idParidad to set
     */
    public void setIdParidad(int idParidad) {
        this.idParidad = idParidad;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }

    /**
     * @return the fechaValido
     */
    public Date getFechaValido() {
        return fechaValido;
    }

    /**
     * @param fechaValido the fechaValido to set
     */
    public void setFechaValido(Date fechaValido) {
        this.fechaValido = fechaValido;
    }

    /**
     * @return the dia
     */
    public int getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(int dia) {
        this.dia = dia;
    }
    
}
