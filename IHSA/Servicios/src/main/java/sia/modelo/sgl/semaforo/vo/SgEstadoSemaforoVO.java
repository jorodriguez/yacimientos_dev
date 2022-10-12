/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.semaforo.vo;

import java.util.Date;
import java.util.List;
import sia.modelo.SgEstadoSemaforo;
import sia.modelo.SgSemaforo;
import sia.modelo.gr.vo.MapaVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;

/**
 *
 * @author ihsa
 */

public class SgEstadoSemaforoVO {
    private int id;
    private int semaforoID;
    private SemaforoVo semaforoVO;
    private int grMapaID;
    private String grMapaIDtxt;
    private MapaVO mapa;
    private Date fechaInicio;
    private Date horaInicio;
    private Date fechaFin;
    private Date horaFin;
    private Date fechaGenero;
    private Date horaGenero;
    private Date fechaModifico;
    private Date horaModifico;
    private String justificacion;
//    private Date horaMinima;
//    private Date horaMaxima;
    private SgEstadoSemaforo ultimoSemaforo;
    private SgSemaforo nuevoSemaforo;
    private List<RutaTerrestreVo> lstRutaByZona;
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
     * @return the semaforoID
     */
    public int getSemaforoID() {
        return semaforoID;
    }

    /**
     * @param semaforoID the semaforoID to set
     */
    public void setSemaforoID(int semaforoID) {
        this.semaforoID = semaforoID;
    }

    /**
     * @return the semaforoVO
     */
    public SemaforoVo getSemaforoVO() {
        return semaforoVO;
    }

    /**
     * @param semaforoVO the semaforoVO to set
     */
    public void setSemaforoVO(SemaforoVo semaforoVO) {
        this.semaforoVO = semaforoVO;
    }

    /**
     * @return the grMapaID
     */
    public int getGrMapaID() {
        return grMapaID;
    }

    /**
     * @param grMapaID the grMapaID to set
     */
    public void setGrMapaID(int grMapaID) {
        this.grMapaID = grMapaID;
    }

    /**
     * @return the mapa
     */
    public MapaVO getMapa() {
        return mapa;
    }

    /**
     * @param mapa the mapa to set
     */
    public void setMapa(MapaVO mapa) {
        this.mapa = mapa;
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {        
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the horaInicio
     */
    public Date getHoraInicio() {
        return horaInicio;
    }

    /**
     * @param horaInicio the horaInicio to set
     */
    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * @return the horaFin
     */
    public Date getHoraFin() {
        return horaFin;
    }

    /**
     * @param horaFin the horaFin to set
     */
    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin;
    }

    /**
     * @return the fechaGenero
     */
    public Date getFechaGenero() {
        return fechaGenero;
    }

    /**
     * @param fechaGenero the fechaGenero to set
     */
    public void setFechaGenero(Date fechaGenero) {
        this.fechaGenero = fechaGenero;
    }

    /**
     * @return the horaGenero
     */
    public Date getHoraGenero() {
        return horaGenero;
    }

    /**
     * @param horaGenero the horaGenero to set
     */
    public void setHoraGenero(Date horaGenero) {
        this.horaGenero = horaGenero;
    }

    /**
     * @return the fechaModifico
     */
    public Date getFechaModifico() {
        return fechaModifico;
    }

    /**
     * @param fechaModifico the fechaModifico to set
     */
    public void setFechaModifico(Date fechaModifico) {
        this.fechaModifico = fechaModifico;
    }

    /**
     * @return the horaModifico
     */
    public Date getHoraModifico() {
        return horaModifico;
    }

    /**
     * @param horaModifico the horaModifico to set
     */
    public void setHoraModifico(Date horaModifico) {
        this.horaModifico = horaModifico;
    }

    /**
     * @return the justificacion
     */
    public String getJustificacion() {
        return justificacion;
    }

    /**
     * @param justificacion the justificacion to set
     */
    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

//    /**
//     * @return the horaMinima
//     */
//    public Date getHoraMinima() {
//        return horaMinima;
//    }
//
//    /**
//     * @param horaMinima the horaMinima to set
//     */
//    public void setHoraMinima(Date horaMinima) {
//        this.horaMinima = horaMinima;
//    }
//
//    /**
//     * @return the horaMaxima
//     */
//    public Date getHoraMaxima() {
//        return horaMaxima;
//    }
//
//    /**
//     * @param horaMaxima the horaMaxima to set
//     */
//    public void setHoraMaxima(Date horaMaxima) {
//        this.horaMaxima = horaMaxima;
//    }

    /**
     * @return the ultimoSemaforo
     */
    public SgEstadoSemaforo getUltimoSemaforo() {
        return ultimoSemaforo;
    }

    /**
     * @param ultimoSemaforo the ultimoSemaforo to set
     */
    public void setUltimoSemaforo(SgEstadoSemaforo ultimoSemaforo) {
        this.ultimoSemaforo = ultimoSemaforo;
    }

    /**
     * @return the nuevoSemaforo
     */
    public SgSemaforo getNuevoSemaforo() {
        return nuevoSemaforo;
    }

    /**
     * @param nuevoSemaforo the nuevoSemaforo to set
     */
    public void setNuevoSemaforo(SgSemaforo nuevoSemaforo) {
        this.nuevoSemaforo = nuevoSemaforo;
    }

    /**
     * @return the lstRutaByZona
     */
    public List<RutaTerrestreVo> getLstRutaByZona() {
        return lstRutaByZona;
    }

    /**
     * @param lstRutaByZona the lstRutaByZona to set
     */
    public void setLstRutaByZona(List<RutaTerrestreVo> lstRutaByZona) {
        this.lstRutaByZona = lstRutaByZona;
    }

    /**
     * @return the grMapaIDtxt
     */
    public String getGrMapaIDtxt() {
        return grMapaIDtxt;
    }

    /**
     * @param grMapaIDtxt the grMapaIDtxt to set
     */
    public void setGrMapaIDtxt(String grMapaIDtxt) {
        this.grMapaIDtxt = grMapaIDtxt;
    }
          
    
    
}
