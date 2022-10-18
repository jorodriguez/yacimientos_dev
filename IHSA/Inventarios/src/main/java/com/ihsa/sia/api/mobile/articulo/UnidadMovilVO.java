package com.ihsa.sia.api.mobile.articulo;

/**
 * Clase que representa una unidad de un artículo que es utilizado por el API de aplicaciones moviles
 *
 * @author Aplimovil SA de CV
 */
public class UnidadMovilVO {

    private Integer id;
    private String nombre;

    public UnidadMovilVO() {
    }

    public UnidadMovilVO(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
