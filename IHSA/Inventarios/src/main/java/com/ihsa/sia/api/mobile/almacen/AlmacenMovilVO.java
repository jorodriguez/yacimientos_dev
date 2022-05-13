package com.ihsa.sia.api.mobile.almacen;

/**
 * Clase que representa el almacen que es utilizado por el API de aplicaciones moviles
 *
 * @author Aplimovil SA de CV
 */
public class AlmacenMovilVO {

    private int id;
    private String nombre;

    public AlmacenMovilVO(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
