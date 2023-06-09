/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.dominio.vo;

import java.io.Serializable;
import java.util.Objects;


/**
 *
 * @author jrodriguez
 */
public class CLocalidadVo implements Serializable,Comparable<CLocalidadVo>{

    private Integer id;
    private Integer clave;
    private String nombre;
    private String estado;
    private String municipio;
    private String latitud;
    private String longitud;

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the clave
     */
    public Integer getClave() {
        return clave;
    }

    /**
     * @param clave the clave to set
     */
    public void setClave(Integer clave) {
        this.clave = clave;
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
     * @return the estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * @param estado the estado to set
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return the municipio
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * @param municipio the municipio to set
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * @return the latitud
     */
    public String getLatitud() {
        return latitud;
    }

    /**
     * @param latitud the latitud to set
     */
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    /**
     * @return the longitud
     */
    public String getLongitud() {
        return longitud;
    }

    /**
     * @param longitud the longitud to set
     */
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        CLocalidadVo localidad = (CLocalidadVo) o;
        
        return id == localidad.id
                && Objects.equals(nombre, localidad.nombre)
                && Objects.equals(clave, localidad.clave);
    }

    
    @Override
    public int compareTo(CLocalidadVo o) {        
       return nombre.compareTo(o.nombre);
    }


}
