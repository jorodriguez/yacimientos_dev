/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.comunicacion;

import java.util.Date;


/**
 *
 * @author hacosta
 */
public class ComparteCon {
    private String id;
    private String nombre;
    private String tipo;
    private String descripcion;
    private String color;
    private String backgroundColor;
    
    private String foto;
    private String correoUsuario;
    private String nombreGenero;
    private Date fechaGenero;
    private Date horaGenero;
    

    public ComparteCon(String id, String nombre, String descripcion, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
         if (tipo.equals("Usuario")) {
            this.backgroundColor = "#0895d6";
            this.color="#fff";
        }else{
             if (tipo.equals("privacidad")) {
                 this.backgroundColor = "#4A9407";
                 this.color="#fff";
             }
             else{
                 if (tipo.equals("Grupo")) {
                     this.backgroundColor = "#FAF8F8";
                     this.color = "#333";
                 }
             }
         }
         
    }
    
    public ComparteCon(){}

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
     * @return the color
     */
    public String getColor() {
        return color;
    }

    
    /**
     * @return the backgroundColor
     */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the foto
     */
    public String getFoto() {
        return foto;
    }

    /**
     * @param foto the foto to set
     */
    public void setFoto(String foto) {
        this.foto = foto;
    }

    /**
     * @return the correoUsuario
     */
    public String getCorreoUsuario() {
        return correoUsuario;
    }

    /**
     * @param correoUsuario the correoUsuario to set
     */
    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    /**
     * @return the nombreGenero
     */
    public String getNombreGenero() {
        return nombreGenero;
    }

    /**
     * @param nombreGenero the nombreGenero to set
     */
    public void setNombreGenero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
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


   
}
