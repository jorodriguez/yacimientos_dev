/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
public class SiAyudaAdjuntoVo extends Vo {

    private String url;
    private String tipoArchivo;
    private int idSiAjunto;
    private String uuidSiAjunto;
    private int idSiAyuda;

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the tipoArchivo
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * @param tipoArchivo the tipoArchivo to set
     */
    public void setTipoArchivo(String tipoArchivo) {
        this.tipoArchivo = tipoArchivo;
    }

    /**
     * @return the idSiAjunto
     */
    public int getIdSiAjunto() {
        return idSiAjunto;
    }

    /**
     * @param idSiAjunto the idSiAjunto to set
     */
    public void setIdSiAjunto(int idSiAjunto) {
        this.idSiAjunto = idSiAjunto;
    }

    /**
     * @return the idSiAyuda
     */
    public int getIdSiAyuda() {
        return idSiAyuda;
    }

    /**
     * @param idSiAyuda the idSiAyuda to set
     */
    public void setIdSiAyuda(int idSiAyuda) {
        this.idSiAyuda = idSiAyuda;
    }

    /**
     * @return the uuidSiAjunto
     */
    public String getUuidSiAjunto() {
        return uuidSiAjunto;
    }

    /**
     * @param uuidSiAjunto the uuidSiAjunto to set
     */
    public void setUuidSiAjunto(String uuidSiAjunto) {
        this.uuidSiAjunto = uuidSiAjunto;
    }
}
