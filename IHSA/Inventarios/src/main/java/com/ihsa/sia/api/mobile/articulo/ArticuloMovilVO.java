package com.ihsa.sia.api.mobile.articulo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa el artículo que es utilizado por el API de aplicaciones
 * moviles
 *
 * @author Aplimovil SA de CV
 */
public class ArticuloMovilVO {

    private Integer id;
    private String sku;
    private String nombre;
    private String descripcion;
    private UnidadMovilVO unidad;
    private List<ArticuloAlmacenMovilVO> existencias = new ArrayList<>();

    public ArticuloMovilVO() {
    }

    public ArticuloMovilVO(Integer id, String sku, String nombre, String descripcion, UnidadMovilVO unidad) {
        this.id = id;
        this.sku = sku;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidad = unidad;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UnidadMovilVO getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadMovilVO unidad) {
        this.unidad = unidad;
    }

    public List<ArticuloAlmacenMovilVO> getExistencias() {
        return existencias;
    }

    public void setExistencias(List<ArticuloAlmacenMovilVO> existencias) {
        this.existencias = existencias;
    }
}
