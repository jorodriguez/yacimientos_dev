/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jorodr
 */
public class ApCampoVo extends  Vo{
    private String descripcion;
    private String compania;
    private String rfcCompania;


    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param Descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
     * @return the rfcCompania
     */
    public String getRfcCompania() {
        return rfcCompania;
    }

    /**
     * @param rfcCompania the rfcCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
        this.rfcCompania = rfcCompania;
    }

    
}
