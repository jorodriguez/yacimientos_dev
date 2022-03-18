/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;
import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author jrodriguez
 */
public class TipoSolicitudViajeVO extends Vo{
    private Integer idTipoEspecifico;
    private String tipoEspecifico;
    private String nombreSolicitud;
    private Integer horasAnticipacion;
    private int horaMaxima;

    /**
     * @return the tipoEspecifico
     */
    public String getTipoEspecifico() {
        return tipoEspecifico;
    }

    /**
     * @param tipoEspecifico the tipoEspecifico to set
     */
    public void setTipoEspecifico(String tipoEspecifico) {
        this.tipoEspecifico = tipoEspecifico;
    }

    /**
     * @return the nombreSolicitud
     */
    public String getNombreSolicitud() {
        return nombreSolicitud;
    }

    /**
     * @param nombreSolicitud the nombreSolicitud to set
     */
    public void setNombreSolicitud(String nombreSolicitud) {
        this.nombreSolicitud = nombreSolicitud;
    }

    /**
     * @return the horasAnticipacion
     */
    public Integer getHorasAnticipacion() {
        return horasAnticipacion;
    }

    /**
     * @param horasAnticipacion the horasAnticipacion to set
     */
    public void setHorasAnticipacion(Integer horasAnticipacion) {
        this.horasAnticipacion = horasAnticipacion;
    }

    /**
     * @return the idTipoEspecifico
     */
    public Integer getIdTipoEspecifico() {
        return idTipoEspecifico;
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(Integer idTipoEspecifico) {
        this.idTipoEspecifico = idTipoEspecifico;
    }

    /**
     * @return the horaMaxima
     */
    public int getHoraMaxima() {
        return horaMaxima;
    }

    /**
     * @param horaMaxima the horaMaxima to set
     */
    public void setHoraMaxima(int horaMaxima) {
        this.horaMaxima = horaMaxima;
    }

   
    
}
