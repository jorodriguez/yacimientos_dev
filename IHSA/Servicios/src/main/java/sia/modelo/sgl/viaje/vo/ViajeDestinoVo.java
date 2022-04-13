/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import sia.modelo.sgl.vo.Vo;

/**
 *
 * @author mluis
 */
public class ViajeDestinoVo extends Vo {

    private Integer idCiudadDestino;
    private String ciudadDestino;
    private String estadoDestino;
    private String paisDestino;

    public ViajeDestinoVo() {
    }

    /**
     * @return the ciudadDestino
     */
    public String getCiudadDestino() {
        return ciudadDestino;
    }

    /**
     * @param ciudadDestino the ciudadDestino to set
     */
    public void setCiudadDestino(String ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    /**
     * @return the idCiudadDestino
     */
    public Integer getIdCiudadDestino() {
        return idCiudadDestino;
    }

    /**
     * @param idCiudadDestino the idCiudadDestino to set
     */
    public void setIdCiudadDestino(Integer idCiudadDestino) {
        this.idCiudadDestino = idCiudadDestino;
    }

    /**
     * @return the estadoDestino
     */
    public String getEstadoDestino() {
        return estadoDestino;
    }

    /**
     * @param estadoDestino the estadoDestino to set
     */
    public void setEstadoDestino(String estadoDestino) {
        this.estadoDestino = estadoDestino;
    }

    /**
     * @return the paisDestino
     */
    public String getPaisDestino() {
        return paisDestino;
    }

    /**
     * @param paisDestino the paisDestino to set
     */
    public void setPaisDestino(String paisDestino) {
        this.paisDestino = paisDestino;
    }
}
