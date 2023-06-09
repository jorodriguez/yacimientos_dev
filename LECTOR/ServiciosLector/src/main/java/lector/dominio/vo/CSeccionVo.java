/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.dominio.vo;

import java.io.Serializable;


/**
 *
 * @author jrodriguez
 */
public class CSeccionVo implements Serializable{

    private Integer id;
    private Integer clave;
    private String nombre;
    private String estado;
    private String municipio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClave() {
        return clave;
    }

    public void setClave(Integer clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

  


}
