/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo.sistema.vo;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ihsa
 */

public class CategoriaVo {

    private int id;
    private String nombre;
    private String descripcion;
    private String codigo;
    private boolean selected;
    private int idPadre;
    private List<CategoriaVo> listaCategoria = new ArrayList<CategoriaVo>();
    private boolean tieneProductos;

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

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the idPadre
     */
    public int getIdPadre() {
        return idPadre;
    }

    /**
     * @param idPadre the idPadre to set
     */
    public void setIdPadre(int idPadre) {
        this.idPadre = idPadre;
    }

    /**
     * @return the listaCategoria
     */
    public List<CategoriaVo> getListaCategoria() {
        return listaCategoria;
    }

    /**
     * @param listaCategoria the listaCategoria to set
     */
    public void setListaCategoria(List<CategoriaVo> listaCategoria) {
        this.listaCategoria = listaCategoria;
    }

    /**
     * @return the tieneProductos
     */
    public boolean isTieneProductos() {
        return tieneProductos;
    }

    /**
     * @param tieneProductos the tieneProductos to set
     */
    public void setTieneProductos(boolean tieneProductos) {
        this.tieneProductos = tieneProductos;
    }

}
