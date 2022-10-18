/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

import java.math.BigDecimal;

/**
 *
 * @author b75ckd35th
 */
public class SgGastoViajeVO {

    private int id;
    private String codigoSgViaje;
    private String nombreSgTipoEspecifico;
    private int idSgTipoEspecifico;
    private BigDecimal importe;
    private String nombreMoneda;
    private int idMoneda;
    private String observacion;
    private int idSiAdjunto;
    private String uuid;

    public SgGastoViajeVO() {
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
     * @return the codigoSgViaje
     */
    public String getCodigoSgViaje() {
        return codigoSgViaje;
    }

    /**
     * @param codigoSgViaje the codigoSgViaje to set
     */
    public void setCodigoSgViaje(String codigoSgViaje) {
        this.codigoSgViaje = codigoSgViaje;
    }

    /**
     * @return the nombreSgTipoEspecifico
     */
    public String getNombreSgTipoEspecifico() {
        return nombreSgTipoEspecifico;
    }

    /**
     * @param nombreSgTipoEspecifico the nombreSgTipoEspecifico to set
     */
    public void setNombreSgTipoEspecifico(String nombreSgTipoEspecifico) {
        this.nombreSgTipoEspecifico = nombreSgTipoEspecifico;
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
     * @return the importe
     */
    public BigDecimal getImporte() {
        return importe;
    }

    /**
     * @param importe the importe to set
     */
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    /**
     * @return the nombreMoneda
     */
    public String getNombreMoneda() {
        return nombreMoneda;
    }

    /**
     * @param nombreMoneda the nombreMoneda to set
     */
    public void setNombreMoneda(String nombreMoneda) {
        this.nombreMoneda = nombreMoneda;
    }

    /**
     * @return the idMoneda
     */
    public int getIdMoneda() {
        return idMoneda;
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
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
     * @return the idSiAdjunto
     */
    public int getIdSiAdjunto() {
        return idSiAdjunto;
    }

    /**
     * @param idSiAdjunto the idSiAdjunto to set
     */
    public void setIdSiAdjunto(int idSiAdjunto) {
        this.idSiAdjunto = idSiAdjunto;
    }

    
    public String toString() {
        return "SgGastoViajeVO{" + "id=" + id + ", codigoSgViaje=" + codigoSgViaje + ", nombreSgTipoEspecifico=" + nombreSgTipoEspecifico + ", idSgTipoEspecifico=" + idSgTipoEspecifico + ", importe=" + importe + ", nombreMoneda=" + nombreMoneda + ", idMoneda=" + idMoneda + ", observacion=" + observacion + ", idSiAdjunto=" + idSiAdjunto + '}';
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
