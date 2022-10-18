/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gr.vo;

import java.util.Date;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;

/**
 *
 * @author ihsa
 */
public class MapaVO {
    
    private int id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Date fechaGenero;
    private Date horaGenero;
    private Date fechaModifico;
    private Date horaModifico;
    private boolean activa; 
    private boolean visible; 
    private SemaforoVo semaforoActual;
    private String colorSemaforo = "#FFF";

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
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
     * @return the activa
     */
    public boolean isActiva() {
        return activa;
    }

    /**
     * @param activa the activa to set
     */
    public void setActiva(boolean activa) {
        this.activa = activa;
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
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the semaforoActual
     */
    public SemaforoVo getSemaforoActual() {
        return semaforoActual;
    }

    /**
     * @param semaforoActual the semaforoActual to set
     */
    public void setSemaforoActual(SemaforoVo semaforoActual) {
        this.semaforoActual = semaforoActual;
        if(this.semaforoActual != null && this.semaforoActual.getEstilo() != null && !this.semaforoActual.getEstilo().isEmpty()){
            if("verde".equals(this.semaforoActual.getEstilo())){
                setColorSemaforo("green");
            }else if("amarillo".equals(this.semaforoActual.getEstilo())){
                setColorSemaforo("yellow");
            }else if("rojo".equals(this.semaforoActual.getEstilo())){
                setColorSemaforo("red");
            }else if("negro".equals(this.semaforoActual.getEstilo())){
                setColorSemaforo("black");
            }
        }
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
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
