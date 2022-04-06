/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.gr.vo;

import java.util.Date;

/**
 *
 * @author ihsa
 */
public class GrSitioVO {

    private int id;
    private String nombre;
    private String descripcion;
    private String liga;
    private Date fech_Genero;
    private Date hora_Genero;
    private boolean activo;

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

    private String ponerBrinco(String fuente) {
        return new StringBuilder().append(fuente.substring(0, fuente.lastIndexOf(" ")))
                .append(" <br/> ").append(fuente.substring(fuente.lastIndexOf(" ") + 1, fuente.length())).toString();
    }
    
    public String getNombreIcon() {
        StringBuilder nombreIcon = new StringBuilder();
        int bloqueSize = 20;
        if (this.nombre.length() > bloqueSize) {            
            for(int i = 0; i < this.getNombre().length(); i=i+bloqueSize){
                if(this.getNombre().length() > (i+bloqueSize)){
                    nombreIcon.append(this.ponerBrinco(this.getNombre().substring(i, i+bloqueSize)));
                }else{
                    nombreIcon.append(this.ponerBrinco(getNombre().substring(i, this.getNombre().length())));
                }
            }
        } else {
            nombreIcon.append(this.getNombre());
        }

        return nombreIcon.toString();
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
     * @return the liga
     */
    public String getLiga() {
        return liga;
    }
    
    /**
     * @return the liga
     */
    public String getLigaWeb() {
        String ligaWeb = "";
        if(getLiga() != null && !getLiga().isEmpty()){
            ligaWeb = getLiga();            
            if(!getLiga().contains("http")){
                ligaWeb = "http://"+ligaWeb;
            }
        }
        return ligaWeb;
    }

    /**
     * @param liga the liga to set
     */
    public void setLiga(String liga) {
        this.liga = liga;
    }

    /**
     * @return the fech_Genero
     */
    public Date getFech_Genero() {
        return fech_Genero;
    }

    /**
     * @param fech_Genero the fech_Genero to set
     */
    public void setFech_Genero(Date fech_Genero) {
        this.fech_Genero = fech_Genero;
    }

    /**
     * @return the hora_Genero
     */
    public Date getHora_Genero() {
        return hora_Genero;
    }

    /**
     * @param hora_Genero the hora_Genero to set
     */
    public void setHora_Genero(Date hora_Genero) {
        this.hora_Genero = hora_Genero;
    }

    /**
     * @return the activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * @param activo the activo to set
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}
