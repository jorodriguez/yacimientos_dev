/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.hotel.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author b75ckd35th
 */
public class SgHotelTipoEspecificoVo extends Vo {

    private int idSgHotel;
    private String nombreSgHotel;
    private int idSgTipoEspecifico;
    private String nombreSgTipoEspecifico;
    private boolean facturadoEmpresa = false;

    public SgHotelTipoEspecificoVo() {
    }

    /**
     * @return the idSgHotel
     */
    public int getIdSgHotel() {
        return idSgHotel;
    }

    /**
     * @param idSgHotel the idSgHotel to set
     */
    public void setIdSgHotel(int idSgHotel) {
        this.idSgHotel = idSgHotel;
    }

    /**
     * @return the nombreSgHotel
     */
    public String getNombreSgHotel() {
        return nombreSgHotel;
    }

    /**
     * @param nombreSgHotel the nombreSgHotel to set
     */
    public void setNombreSgHotel(String nombreSgHotel) {
        this.nombreSgHotel = nombreSgHotel;
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
     * @return the facturadoEmpresa
     */
    public boolean isFacturadoEmpresa() {
        return facturadoEmpresa;
    }

    /**
     * @param facturadoEmpresa the facturadoEmpresa to set
     */
    public void setFacturadoEmpresa(boolean facturadoEmpresa) {
        this.facturadoEmpresa = facturadoEmpresa;
    }

    
    public String toString() {
        return "SgHotelTipoEspecificoVo{" + "idSgHotel=" + idSgHotel + ", nombreSgHotel=" + nombreSgHotel + ", idSgTipoEspecifico=" + idSgTipoEspecifico + ", nombreSgTipoEspecifico=" + nombreSgTipoEspecifico + ", facturadoEmpresa=" + facturadoEmpresa + '}';
    }
}
