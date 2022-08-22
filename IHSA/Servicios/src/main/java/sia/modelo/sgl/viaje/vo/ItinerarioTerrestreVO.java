/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.util.List;
import sia.modelo.gr.vo.GrPuntoVO;

/**
 *
 * @author ihsa
 */
public class ItinerarioTerrestreVO {
    
    private int id;
    private String nombrePS;
    private int idPS;
    private ViajeVO viajeMTY;
    private ViajeVO viajeREY;
    private ViajeVO viajeSF;
    private List<GrPuntoVO> lstRutaDetPredefinida;

    /**
     * @return the viajeMTY
     */
    public ViajeVO getViajeMTY() {
        return viajeMTY;
    }

    /**
     * @param viajeMTY the viajeMTY to set
     */
    public void setViajeMTY(ViajeVO viajeMTY) {
        this.viajeMTY = viajeMTY;
    }

    /**
     * @return the viajeREY
     */
    public ViajeVO getViajeREY() {
        return viajeREY;
    }

    /**
     * @param viajeREY the viajeREY to set
     */
    public void setViajeREY(ViajeVO viajeREY) {
        this.viajeREY = viajeREY;
    }

    /**
     * @return the viajeSF
     */
    public ViajeVO getViajeSF() {
        return viajeSF;
    }

    /**
     * @param viajeSF the viajeSF to set
     */
    public void setViajeSF(ViajeVO viajeSF) {
        this.viajeSF = viajeSF;
    }
    
    public int getCantidadNodos(){
        int ret = 0;
        if(this.getLstRutaDetPredefinida() != null && this.getLstRutaDetPredefinida().size() > 0){
            ret = this.getLstRutaDetPredefinida().size();
        }                
        return ret;
    }
    
    public int getFactor(){
        int ret = 0;
        int aux =  this.getCantidadNodos();
        if(aux > 0){
            ret = 100/aux;
        }    
        return ret;
    }

    /**
     * @return the lstRutaDetPredefinida
     */
    public List<GrPuntoVO> getLstRutaDetPredefinida() {
        return lstRutaDetPredefinida;
    }

    /**
     * @param lstRutaDetPredefinida the lstRutaDetPredefinida to set
     */
    public void setLstRutaDetPredefinida(List<GrPuntoVO> lstRutaDetPredefinida) {
        this.lstRutaDetPredefinida = lstRutaDetPredefinida;
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
     * @return the nombrePS
     */
    public String getNombrePS() {
        return nombrePS;
    }

    /**
     * @param nombrePS the nombrePS to set
     */
    public void setNombrePS(String nombrePS) {
        this.nombrePS = nombrePS;
    }

    /**
     * @return the idPS
     */
    public int getIdPS() {
        return idPS;
    }

    /**
     * @param idPS the idPS to set
     */
    public void setIdPS(int idPS) {
        this.idPS = idPS;
    }
    
}
