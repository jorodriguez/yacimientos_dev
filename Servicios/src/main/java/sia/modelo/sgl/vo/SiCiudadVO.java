/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sgl.vo;

/**
 *
 * @author b75ckd35th
 */
public class SiCiudadVO {

    private Integer id;
    private String nombre;
    private String nombreSiPais;
    private String nombreSiEstado;
    private String latitud;
    private String longitud;

    public SiCiudadVO() {
    }

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
     * @return the nombreSiPais
     */
    public String getNombreSiPais() {
        return nombreSiPais;
    }

    /**
     * @param nombreSiPais the nombreSiPais to set
     */
    public void setNombreSiPais(String nombreSiPais) {
        this.nombreSiPais = nombreSiPais;
    }

    /**
     * @return the nombreSiEstado
     */
    public String getNombreSiEstado() {
        return nombreSiEstado;
    }

    /**
     * @param nombreSiEstado the nombreSiEstado to set
     */
    public void setNombreSiEstado(String nombreSiEstado) {
        this.nombreSiEstado = nombreSiEstado;
    }

    
    public String toString() {
        return "SiCiudadVO{"
                + "id=" + id
                + ", nombre=" + nombre
                + ", nombreSiPais=" + nombreSiPais
                + ", nombreSiEstado=" + nombreSiEstado
                + '}';
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
}