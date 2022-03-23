/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.busqueda;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.inventarios.service.ArticuloImpl;
import sia.modelo.vo.inventarios.ArticuloVO;

/**
 *
 * @author ihsa
 */
//@ViewScoped
@Named (value = "soporteArticulos")
@CustomScoped(value = "#{window}")
public class SoporteArticulos implements  Serializable{

    @Inject
    private ArticuloImpl articuloImpl;

    private List<ArticuloVO> listaArticulos = null;
    
    private List<SelectItem> listaCategorias = null;

    public List<SelectItem> obtenerArticulos(String cadenaDigitada, int campoID, int categoriaID, String codigosCategorias) {
	List<SelectItem> list = new ArrayList<>();
	String cadenaArticulo = null;
	for (ArticuloVO p : this.getArticulosActivo(null, campoID, categoriaID, codigosCategorias)) {
	    if (cadenaDigitada != null && !cadenaDigitada.isEmpty() && (p.getNombre() != null && p.getCodigo() != null && p.getNumParte() != null)) {
		cadenaArticulo = new StringBuilder().append(p.getNombre())
			.append("=>").append(p.getCodigo())
			.append("=>").append(p.getUnidadNombre())
			.append("=>").append(p.getNumParte())
			.toString().toLowerCase();
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (cadenaArticulo.contains(cadenaDigitada)) {
		    SelectItem item = new SelectItem(p, cadenaArticulo.toUpperCase());
		    list.add(item);
		}
	    } else {
		cadenaArticulo = new StringBuilder().append(p.getNombre())
			.append("=>").append(p.getCodigo())
			.append("=>").append(p.getUnidadNombre())
			.append("=>").append(p.getNumParte())
			.toString().toLowerCase();
		SelectItem item = new SelectItem(p, cadenaArticulo.toUpperCase());
		list.add(item);
	    }
	}
	return list;
    }
    
    public List<SelectItem> obtenerArticulosItems(String cadenaDigitada, int campoID, int categoriaID, String codigosCategorias) {
	return this.getArticulosActivoItems(cadenaDigitada, null, campoID, categoriaID, codigosCategorias);
    }
    
    public List<SelectItem> obtenerCategorias(String cadenaDigitada, int campoID) {
	List<SelectItem> list = new ArrayList<>();	
	for (SelectItem p : this.getCategoriasActivas(campoID)) {
	    if (cadenaDigitada != null && !cadenaDigitada.isEmpty()) {		
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (p.getLabel().toLowerCase().contains(cadenaDigitada)) {		    
                    SelectItem item = new SelectItem(p, (String) p.getValue());
		    list.add(item);
		}
	    } else {		
                SelectItem item = new SelectItem(p, (String) p.getValue());
		list.add(item);
	    }
	}
	return list;
    }

    /**
     * @return the listaArticulos
     */
    public List<ArticuloVO> getListaArticulos() {
	return listaArticulos;
    }

    /**
     * @param listaArticulos the listaArticulos to set
     */
    public void setListaArticulos(List<ArticuloVO> listaArticulos) {
	this.listaArticulos = listaArticulos;
    }

    public List<ArticuloVO> getArticulosActivo(String codigo, int campoID, int categoriaID, String codigosCategorias) {
	setListaArticulos(articuloImpl.obtenerArticulos(codigo, campoID, categoriaID, codigosCategorias));
	return getListaArticulos();
    }
    
    public List<SelectItem> getArticulosActivoItems(String texto, String codigo, int campoID, int categoriaID, String codigosCategorias) {	
	return articuloImpl.obtenerArticulosItems(texto, codigo, campoID, categoriaID, codigosCategorias);
    }
    
    public List<SelectItem> getCategoriasActivas(int campoID) {
	setListaCategorias(articuloImpl.obtenerCategorias(campoID));
	return getListaCategorias();
    }

    /**
     * @return the listaCategorias
     */
    public List<SelectItem> getListaCategorias() {
        return listaCategorias;
    }

    /**
     * @param listaCategorias the listaCategorias to set
     */
    public void setListaCategorias(List<SelectItem> listaCategorias) {
        this.listaCategorias = listaCategorias;
    }

}
