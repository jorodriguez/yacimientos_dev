/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.categoria.bean;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import sia.inventarios.service.ArticuloImpl;
import sia.inventarios.service.SatArticuloImpl;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.SatArticuloVO;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */

//@ViewScoped
@ManagedBean(name = "soporteCategorias")
@CustomScoped(value = "#{window}")
public class SoporteCategorias {

    @EJB
    private ArticuloImpl articuloImpl;
    
    @EJB
    private SatArticuloImpl satArticuloImpl;

    private List<ArticuloVO> listaArticulos = null;
    
    private List<SatArticuloVO> satListaArticulos = null;
    
    private List<SelectItem> listaCategorias = null;

    public List<SelectItem> obtenerArticulos(String cadenaDigitada, int campoID, int categoriaID, String codigosCategorias) {
	List<SelectItem> list = new ArrayList<SelectItem>();
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
    
    public List<SelectItem> obtenerArticulos(String cadenaDigitada, int categoriaID, String codigosCategorias, String usuarioID) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	String cadenaArticulo = null;
	for (ArticuloVO p : this.getArticulosActivo(null, categoriaID, codigosCategorias, usuarioID)) {
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
    
    public List<SelectItem> obtenerArticulosSat(String cadenaDigitada) {
	List<SelectItem> list = new ArrayList<SelectItem>();	
	for (SatArticuloVO p : this.getArticulosActivoSat(cadenaDigitada)) {
	    if (cadenaDigitada != null && !cadenaDigitada.isEmpty() && (p.getCodigo() != null && p.getDescripcion() != null)) {		
		SelectItem item = new SelectItem(p, p.getCodigo());
		list.add(item);
		
	    } else {		
		SelectItem item = new SelectItem(p, p.getCodigo());
		list.add(item);
	    }
	}
	return list;
    }
    
    public List<SelectItem> obtenerArticulosItems(String cadenaDigitada, int campoID, int categoriaID, String codigosCategorias) {
	return this.getArticulosActivoItems(cadenaDigitada, null, campoID, categoriaID, codigosCategorias);
    }
    
    public List<SelectItem> obtenerArticulosItems(String cadenaDigitada, int categoriaID, String codigosCategorias, String usuarioID) {
	return this.getArticulosActivoItems(cadenaDigitada, null, categoriaID, codigosCategorias, usuarioID);
    }
    
    public List<SelectItem> obtenerCategorias(String cadenaDigitada, int campoID) {
	List<SelectItem> list = new ArrayList<SelectItem>();	
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
	UtilLog4j.log.info(this, "Articulos: " + getListaArticulos().size() + " completa");
	return getListaArticulos();
    }
    
    public List<ArticuloVO> getArticulosActivo(String codigo, int categoriaID, String codigosCategorias, String usuarioID) {
	setListaArticulos(articuloImpl.obtenerArticulosUsuario(codigo, categoriaID, codigosCategorias, usuarioID));
	UtilLog4j.log.info(this, "Articulos: " + getListaArticulos().size() + " completa");
	return getListaArticulos();
    }
    
    public List<SatArticuloVO> getArticulosActivoSat(String codigo) {
	setSatListaArticulos(satArticuloImpl.getSatArtsVO(codigo));
        //obtenerArticulos(codigo, campoID, categoriaID, codigosCategorias));
	UtilLog4j.log.info(this, "Articulos: " + getSatListaArticulos().size() + " completa");
	return getSatListaArticulos();
    }
    
    public List<SelectItem> getArticulosActivoItems(String texto, String codigo, int campoID, int categoriaID, String codigosCategorias) {	
	return articuloImpl.obtenerArticulosItems(texto, codigo, campoID, categoriaID, codigosCategorias);
    }
    
    public List<SelectItem> getArticulosActivoItems(String texto, String codigo, int categoriaID, String codigosCategorias, String usuarioID) {	
	return articuloImpl.obtenerArticulosItemsUsuario(texto, codigo, categoriaID, codigosCategorias, usuarioID);
    }
    
    public List<SelectItem> getCategoriasActivas(int campoID) {
	setListaCategorias(articuloImpl.obtenerCategorias(campoID));
	UtilLog4j.log.info(this, "Categorias: " + getListaCategorias().size() + " completa");
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

    /**
     * @return the satListaArticulos
     */
    public List<SatArticuloVO> getSatListaArticulos() {
        return satListaArticulos;
    }

    /**
     * @param satListaArticulos the satListaArticulos to set
     */
    public void setSatListaArticulos(List<SatArticuloVO> satListaArticulos) {
        this.satListaArticulos = satListaArticulos;
    }

}

