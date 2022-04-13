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
public class SgHuespedHotelServicioVo extends Vo {

    private int idSgHotel;
    private int idSgHuespedHotel;
    private int idSgTipoEspecifico;
    private boolean facturadoEmpresa;
    private String nombreSgHotel;
    private String nombreSgTipoEspecifico;

    public SgHuespedHotelServicioVo() {
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
     * @return the idSgHuespedHotel
     */
    public int getIdSgHuespedHotel() {
        return idSgHuespedHotel;
    }

    /**
     * @param idSgHuespedHotel the idSgHuespedHotel to set
     */
    public void setIdSgHuespedHotel(int idSgHuespedHotel) {
        this.idSgHuespedHotel = idSgHuespedHotel;
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

    
    public String toString() {
        return "SgHuespedHotelServicioVo{" + "id=" + getId() + ", idSgHotel=" + idSgHotel + ", idSgHuespedHotel=" + idSgHuespedHotel + ", idSgTipoEspecifico=" + idSgTipoEspecifico + ", nombreSgHotel=" + nombreSgHotel + ", facturadoEmpresa=" + facturadoEmpresa + ", nombreSgTipoEspecifico=" + nombreSgTipoEspecifico + '}';
    }
}
