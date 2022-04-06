/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.viaje.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import sia.constantes.Constantes;
import sia.modelo.gr.vo.GrRutaZonasVO;

/**
 *
 * @author mluis
 */

public class RutaTerrestreVo implements Serializable{
    private int id;
    private String nombre;
    private String tiempoViaje;
    private List<GrRutaZonasVO> zonas; 
    private List<ViajeVO> viajes;
    private String colorSemaforo;
    private String cssColorSemaforo;
    private String cssColorSemaforoTxt;
    private String tipo;
    private Date horaMinimaRuta;
    private Date horaMaximaRuta;
    private String etiquetaHorario;
    
    public RutaTerrestreVo(){
    
    }
    
    public RutaTerrestreVo(int id){
        this.setId(id);
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
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the tiempoViaje
     */
    public String getTiempoViaje() {
        return tiempoViaje;
    }

    /**
     * @param tiempoViaje the tiempoViaje to set
     */
    public void setTiempoViaje(String tiempoViaje) {
        this.tiempoViaje = tiempoViaje;
    }

    /**
     * @return the zonas
     */
    public List<GrRutaZonasVO> getZonas() {
        return zonas;
    }
    
    public String getZonasTxt(){
        String zonasTxt = "";
        if(getZonas() != null && getZonas().size() > 0){
            zonasTxt = getZonas().toString().substring(1,getZonas().toString().length()-1);
        }
        return zonasTxt;
    }

    /**
     * @param zonas the zonas to set
     */
    public void setZonas(List<GrRutaZonasVO> zonas) {
        this.zonas = zonas;
    }

    /**
     * @return the viajes
     */
    public List<ViajeVO> getViajes() {
        return viajes;
    }

    /**
     * @param viajes the viajeros to set
     */
    public void setViajes(List<ViajeVO> viajes) {
        this.viajes = viajes;
    }

    /**
     * @return the colorSemaforo
     */
    public String getColorSemaforo() {
        return colorSemaforo;
    }

    /**
     * @param colorSemaforo the colorSemaforo to set
     */
    public void setColorSemaforo(String colorSemaforo) {
        this.colorSemaforo = colorSemaforo;
    }

    /**
     * @return the cssColorSemaforo
     */
    public String getCssColorSemaforo() {
         if(this.colorSemaforo != null && !this.colorSemaforo.isEmpty()){
            if("Verde".equalsIgnoreCase(this.colorSemaforo)){
                setCssColorSemaforo("green");
            }else if("Amarillo".equalsIgnoreCase(this.colorSemaforo)){
                setCssColorSemaforo("yellow");
            }else if("Rojo".equalsIgnoreCase(this.colorSemaforo)){
                setCssColorSemaforo("red");
            }else if("Negro".equalsIgnoreCase(this.colorSemaforo)){
                setCssColorSemaforo("black");
            }
        }
        return cssColorSemaforo;
    }

    /**
     * @param cssColorSemaforo the cssColorSemaforo to set
     */
    public void setCssColorSemaforo(String cssColorSemaforo) {
        this.cssColorSemaforo = cssColorSemaforo;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the cssColorSemaforoTxt
     */
    public String getCssColorSemaforoTxt() {
        if(this.colorSemaforo != null && !this.colorSemaforo.isEmpty()){
            if("Verde".equalsIgnoreCase(this.colorSemaforo)){
                setCssColorSemaforoTxt("black");
            }else if("Amarillo".equalsIgnoreCase(this.colorSemaforo)){
                setCssColorSemaforoTxt("black");
            }else if("Rojo".equalsIgnoreCase(this.colorSemaforo)){
                setCssColorSemaforoTxt("black");
            }else if("Negro".equalsIgnoreCase(this.colorSemaforo)){
                setCssColorSemaforoTxt("white");
            }
        }
        return cssColorSemaforoTxt;
    }

    /**
     * @param cssColorSemaforoTxt the cssColorSemaforoTxt to set
     */
    public void setCssColorSemaforoTxt(String cssColorSemaforoTxt) {
        this.cssColorSemaforoTxt = cssColorSemaforoTxt;
    }

    /**
     * @return the horaMinimaRuta
     */
    public Date getHoraMinimaRuta() {
        return horaMinimaRuta;
    }

    /**
     * @param horaMinimaRuta the horaMinimaRuta to set
     */
    public void setHoraMinimaRuta(Date horaMinimaRuta) {
        this.horaMinimaRuta = horaMinimaRuta;
    }

    /**
     * @return the horaMaximaRuta
     */
    public Date getHoraMaximaRuta() {
        return horaMaximaRuta;
    }

    /**
     * @param horaMaximaRuta the horaMaximaRuta to set
     */
    public void setHoraMaximaRuta(Date horaMaximaRuta) {
        this.horaMaximaRuta = horaMaximaRuta;
    }

    /**
     * @return the etiquetaHorario
     */
    public String getEtiquetaHorario() {
        StringBuilder etiqueta = new StringBuilder();
        etiqueta.append("Hora Minima:");
        etiqueta.append(Constantes.FMT_hmm_a.format(getHoraMinimaRuta()));
        etiqueta.append(" - ");
        etiqueta.append("Hora Maxima:");
        etiqueta.append(Constantes.FMT_hmm_a.format(getHoraMaximaRuta()));
        setEtiquetaHorario(etiqueta.toString());
        return etiquetaHorario;
    }

    /**
     * @param etiquetaHorario the etiquetaHorario to set
     */
    public void setEtiquetaHorario(String etiquetaHorario) {
        this.etiquetaHorario = etiquetaHorario;
    }
}
