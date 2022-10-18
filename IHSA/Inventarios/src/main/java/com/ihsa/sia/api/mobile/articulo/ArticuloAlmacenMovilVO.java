package com.ihsa.sia.api.mobile.articulo;

import com.ihsa.sia.api.mobile.almacen.AlmacenMovilVO;

/**
 * Clase que representa el almacen de cada artículo que es utilizado por el API de aplicaciones moviles
 *
 * @author Aplimovil SA de CV
 */
public class ArticuloAlmacenMovilVO  {

    private AlmacenMovilVO almacen;
    private double unidades;
    private String ubicacion;

    public ArticuloAlmacenMovilVO(int id, String nombre, double unidades, String ubicacion) {
        almacen = new AlmacenMovilVO(id,  nombre);
        this.unidades = unidades;
        this.ubicacion = ubicacion;
    }

    public AlmacenMovilVO getAlmacen() {
        return almacen;
    }

    public void setAlmacen(AlmacenMovilVO almacen) {
        this.almacen = almacen;
    }

    public double getUnidades() {
        return unidades;
    }

    public void setUnidades(double unidades) {
        this.unidades = unidades;
    }

    /**
     * @return the ubicacion
     */
    public String getUbicacion() {
        return ubicacion;
    }

    /**
     * @param ubicacion the ubicacion to set
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
