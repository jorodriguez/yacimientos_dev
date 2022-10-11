/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import java.util.Date;
import java.util.List;

/**
 *
 * @author ihsa
 */
public class ParidadVO {
    
    private int id;
    private String nombre;
    private int monedaOrigen;
    private int monedaDestino;
    private String monedaOrigenNombre;
    private String monedaDestinoNombre;
    private String monedaOrigenSiglas;
    private String monedaDestinoSiglas;
    private String compania;
    private String companiaNombre;
    private int monedaLocal;
    private String monedaNombreLocal;
    private double valor;
    private Date fechaValido; 
    private List<ParidadValorVO> mes;

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
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * @param compania the compania to set
     */
    public void setCompania(String compania) {
        this.compania = compania;
    }

    /**
     * @return the monedaLocal
     */
    public int getMonedaLocal() {
        return monedaLocal;
    }

    /**
     * @param monedaLocal the monedaLocal to set
     */
    public void setMonedaLocal(int monedaLocal) {
        this.monedaLocal = monedaLocal;
    }

    /**
     * @return the monedaNombreLocal
     */
    public String getMonedaNombreLocal() {
        return monedaNombreLocal;
    }

    /**
     * @param monedaNombreLocal the monedaNombreLocal to set
     */
    public void setMonedaNombreLocal(String monedaNombreLocal) {
        this.monedaNombreLocal = monedaNombreLocal;
    }

    /**
     * @return the companiaNombre
     */
    public String getCompaniaNombre() {
        return companiaNombre;
    }

    /**
     * @param companiaNombre the companiaNombre to set
     */
    public void setCompaniaNombre(String companiaNombre) {
        this.companiaNombre = companiaNombre;
    }

    /**
     * @return the monedaOrigen
     */
    public int getMonedaOrigen() {
        return monedaOrigen;
    }

    /**
     * @param monedaOrigen the monedaOrigen to set
     */
    public void setMonedaOrigen(int monedaOrigen) {
        this.monedaOrigen = monedaOrigen;
    }

    /**
     * @return the monedaDestino
     */
    public int getMonedaDestino() {
        return monedaDestino;
    }

    /**
     * @param monedaDestino the monedaDestino to set
     */
    public void setMonedaDestino(int monedaDestino) {
        this.monedaDestino = monedaDestino;
    }

    /**
     * @return the monedaOrigenNombre
     */
    public String getMonedaOrigenNombre() {
        return monedaOrigenNombre;
    }

    /**
     * @param monedaOrigenNombre the monedaOrigenNombre to set
     */
    public void setMonedaOrigenNombre(String monedaOrigenNombre) {
        this.monedaOrigenNombre = monedaOrigenNombre;
    }

    /**
     * @return the monedaDestinoNombre
     */
    public String getMonedaDestinoNombre() {
        return monedaDestinoNombre;
    }

    /**
     * @param monedaDestinoNombre the monedaDestinoNombre to set
     */
    public void setMonedaDestinoNombre(String monedaDestinoNombre) {
        this.monedaDestinoNombre = monedaDestinoNombre;
    }

    /**
     * @return the monedaOrigenSiglas
     */
    public String getMonedaOrigenSiglas() {
        return monedaOrigenSiglas;
    }

    /**
     * @param monedaOrigenSiglas the monedaOrigenSiglas to set
     */
    public void setMonedaOrigenSiglas(String monedaOrigenSiglas) {
        this.monedaOrigenSiglas = monedaOrigenSiglas;
    }

    /**
     * @return the monedaDestinoSiglas
     */
    public String getMonedaDestinoSiglas() {
        return monedaDestinoSiglas;
    }

    /**
     * @param monedaDestinoSiglas the monedaDestinoSiglas to set
     */
    public void setMonedaDestinoSiglas(String monedaDestinoSiglas) {
        this.monedaDestinoSiglas = monedaDestinoSiglas;
    }

    /**
     * @return the mes
     */
    public List<ParidadValorVO> getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(List<ParidadValorVO> mes) {
        this.mes = mes;
    }
    
}
