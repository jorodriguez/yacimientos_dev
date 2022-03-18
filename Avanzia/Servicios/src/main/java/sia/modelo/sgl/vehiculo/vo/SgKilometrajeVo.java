/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vehiculo.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
public class SgKilometrajeVo extends Vo {

    private int idSgVehiculo;
    private int idSgOficina;
    private int idSgMarcaVehiculo;
    private int idSgModeloVehiculo;
    private int idSgTipo;
    private int idSgTipoEspecifico;
    private boolean validado;
    private String placaVehiculo;
    private String serieVehiculo;
    private String nombreMarcaVehiculo;
    private String nombreModeloVehiculo;
    private String observacion;
    private Integer kilometrajeActual;
    private Integer kilometrajeNuevo;
    private String nombreTipoEspecifico;
    private boolean actual;
    private int sgTipoEspecifico;
    private int sgTipo;
    

    public String getCss() {
//        if (isValidado()) {
            if (this.kilometrajeNuevo == null || (this.kilometrajeNuevo.intValue() < this.kilometrajeActual.intValue())) {
                return "inputTextShadowRed";
            } else {
                return "inputTextShadow";
            }
//        } else {
//            return "inputTextShadow";
//        }
    }
    
    public String getMensaje() {
//        if (isValidado()) {
            if (this.kilometrajeNuevo == null) {
                return "sistema.mensaje.error.esRequerido";
            } else {
                if (this.kilometrajeNuevo.intValue() < this.kilometrajeActual.intValue()) {
                    return "sgl.kilometraje.mensaje.error.actualMenor.corto";
                } else {
                    return "sistema.vacio";
                }
            }
//        } else {
//            return "sistema.vacio";
//        }
    }

    /**
     * @return the idSgVehiculo
     */
    public int getIdSgVehiculo() {
        return idSgVehiculo;
    }

    /**
     * @param idSgVehiculo the idSgVehiculo to set
     */
    public void setIdSgVehiculo(int idSgVehiculo) {
        this.idSgVehiculo = idSgVehiculo;
    }

    /**
     * @return the idSgOficina
     */
    public int getIdSgOficina() {
        return idSgOficina;
    }

    /**
     * @param idSgOficina the idSgOficina to set
     */
    public void setIdSgOficina(int idSgOficina) {
        this.idSgOficina = idSgOficina;
    }

    /**
     * @return the idSgMarcaVehiculo
     */
    public int getIdSgMarcaVehiculo() {
        return idSgMarcaVehiculo;
    }

    /**
     * @param idSgMarcaVehiculo the idSgMarcaVehiculo to set
     */
    public void setIdSgMarcaVehiculo(int idSgMarcaVehiculo) {
        this.idSgMarcaVehiculo = idSgMarcaVehiculo;
    }

    /**
     * @return the idSgModeloVehiculo
     */
    public int getIdSgModeloVehiculo() {
        return idSgModeloVehiculo;
    }

    /**
     * @param idSgModeloVehiculo the idSgModeloVehiculo to set
     */
    public void setIdSgModeloVehiculo(int idSgModeloVehiculo) {
        this.idSgModeloVehiculo = idSgModeloVehiculo;
    }

    /**
     * @return the idSgTipo
     */
    public int getIdSgTipo() {
        return idSgTipo;
    }

    /**
     * @param idSgTipo the idSgTipo to set
     */
    public void setIdSgTipo(int idSgTipo) {
        this.idSgTipo = idSgTipo;
    }

    /**
     * @return the idSgTipoEspecifico
     */
    public int getIdSgTipoEspecifico() {
        return idSgTipoEspecifico;
    }

    /**
     * @param idSgTipoEspecifico the idSgTipoEspecifico to set
     */
    public void setIdSgTipoEspecifico(int idSgTipoEspecifico) {
        this.idSgTipoEspecifico = idSgTipoEspecifico;
    }

    /**
     * @return the validado
     */
    public boolean isValidado() {
        return validado;
    }

    /**
     * @param validado the validado to set
     */
    public void setValidado(boolean validado) {
        this.validado = validado;
    }

    /**
     * @return the placaVehiculo
     */
    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    /**
     * @param placaVehiculo the placaVehiculo to set
     */
    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    /**
     * @return the serieVehiculo
     */
    public String getSerieVehiculo() {
        return serieVehiculo;
    }

    /**
     * @param serieVehiculo the serieVehiculo to set
     */
    public void setSerieVehiculo(String serieVehiculo) {
        this.serieVehiculo = serieVehiculo;
    }

    /**
     * @return the nombreMarcaVehiculo
     */
    public String getNombreMarcaVehiculo() {
        return nombreMarcaVehiculo;
    }

    /**
     * @param nombreMarcaVehiculo the nombreMarcaVehiculo to set
     */
    public void setNombreMarcaVehiculo(String nombreMarcaVehiculo) {
        this.nombreMarcaVehiculo = nombreMarcaVehiculo;
    }

    /**
     * @return the nombreModeloVehiculo
     */
    public String getNombreModeloVehiculo() {
        return nombreModeloVehiculo;
    }

    /**
     * @param nombreModeloVehiculo the nombreModeloVehiculo to set
     */
    public void setNombreModeloVehiculo(String nombreModeloVehiculo) {
        this.nombreModeloVehiculo = nombreModeloVehiculo;
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
     * @return the kilometrajeActual
     */
    public Integer getKilometrajeActual() {
        return kilometrajeActual;
    }

    /**
     * @param kilometrajeActual the kilometrajeActual to set
     */
    public void setKilometrajeActual(Integer kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    /**
     * @return the kilometrajeNuevo
     */
    public Integer getKilometrajeNuevo() {
        return kilometrajeNuevo;
    }

    /**
     * @param kilometrajeNuevo the kilometrajeNuevo to set
     */
    public void setKilometrajeNuevo(Integer kilometrajeNuevo) {
        this.kilometrajeNuevo = kilometrajeNuevo;
//        this.validado = true;
    }

    
    public String toString() {
        return "SgKilometrajeVo{" + "idSgVehiculo=" + idSgVehiculo + ", idSgMarcaVehiculo=" + idSgMarcaVehiculo + ", idSgModeloVehiculo=" + idSgModeloVehiculo + ", placaVehiculo=" + placaVehiculo + ", serieVehiculo=" + serieVehiculo + ", nombreMarcaVehiculo=" + nombreMarcaVehiculo + ", nombreModeloVehiculo=" + nombreModeloVehiculo + ", kilometrajeActual=" + getKilometrajeActual() + ", kilometrajeNuevo=" + getKilometrajeNuevo() + '}';
    }

    /**
     * @return the actual
     */
    public boolean isActual() {
        return actual;
    }

    /**
     * @param actual the actual to set
     */
    public void setActual(boolean actual) {
        this.actual = actual;
    }

    /**
     * @return the sgTipoEspecifico
     */
    public int getSgTipoEspecifico() {
        return sgTipoEspecifico;
    }

    /**
     * @param sgTipoEspecifico the sgTipoEspecifico to set
     */
    public void setSgTipoEspecifico(int sgTipoEspecifico) {
        this.sgTipoEspecifico = sgTipoEspecifico;
    }

    /**
     * @return the sgTipo
     */
    public int getSgTipo() {
        return sgTipo;
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(int sgTipo) {
        this.sgTipo = sgTipo;
    }

    /**
     * @return the nombreTipoEspecifico
     */
    public String getNombreTipoEspecifico() {
        return nombreTipoEspecifico;
    }

    /**
     * @param nombreTipoEspecifico the nombreTipoEspecifico to set
     */
    public void setNombreTipoEspecifico(String nombreTipoEspecifico) {
        this.nombreTipoEspecifico = nombreTipoEspecifico;
    }
}
