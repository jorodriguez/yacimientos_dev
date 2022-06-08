/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;


import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.servicios.sistema.impl.SiCategoriaImpl;
import sia.servicios.sistema.impl.SiRelCategoriaImpl;

/**
 *
 * @author mluis
 */
@Named(value  = "registroCategoriaBean")
@ViewScoped
public class RegistroCategoriaBean implements Serializable {

    /**
     * Creates a new instance of RegistroCategoriaBean
     */
    public RegistroCategoriaBean() {
    }

    @Inject
    private Sesion sesion;
    //
    @Inject
    ArticuloRemote articuloImpl;
    @Inject
    SiRelCategoriaImpl siRelCategoriaImpl;
    @Inject
    SiCategoriaImpl siCategoriaImpl;
    //
    private Map<String, List<ArticuloVO>> mapaArticulo;
    private Map<String, List<CategoriaVo>> mapaCategria;
    private CategoriaVo categoriaVo;
    private String nombre;
    private String codigo;

    @PostConstruct
    public void iniciar() {
        setMapaCategria(new HashMap<>());
        categoriaVo = new CategoriaVo();
        mapaCategria.put("categoriaRelacion", new ArrayList<>());
        mapaCategria.put("categoriaSeleccionada", new ArrayList<>());
        mapaCategria.put("artAgregarCat", new ArrayList<>());
        mapaCategria.put("categoriaRelacion", new ArrayList<>());
        mapaCategria.put("categoriaTemporal", new ArrayList<>());
        
        mapaCategria.put("categoria", siCategoriaImpl.traerCategoriaPrincipales());
        mapaArticulo = new HashMap<>();
        mapaArticulo.put("articulo", articuloImpl.buscarArticuloSinCategoriaPorGenero(sesion.getUsuarioSesion().getId()));
    }

    public void iniciarAgregarCategoria() {
        List<ArticuloVO> latemp = new ArrayList<>();
        for (ArticuloVO articuloVO : mapaArticulo.get("articulo")) {
            if (articuloVO.isSelected()) {
                latemp.add(articuloVO);
            }
        }
        if (!latemp.isEmpty()) {
            mapaCategria.put("categoriaRelacion", new ArrayList<>());
            mapaCategria.put("categoriaSeleccionada", new ArrayList<>());
            mapaCategria.put("categoria", siCategoriaImpl.traerCategoriaPrincipales());
            iniciarCatSel();
            mapaArticulo.put("artAgregarCat", latemp);
            PrimeFaces.current().executeScript("$(dialogoAgregarCategoria).modal('show')");
        } else {
            FacesUtils.addInfoMessage("Seleccione al menos un artículo");
        }

    }

    public void agregarCategoria() {
        if (mapaCategria.get("categoriaSeleccionada").size() > 2) {
            articuloImpl.agregarCategoriaArticulo(sesion.getUsuarioSesion().getId(), mapaArticulo.get("artAgregarCat"), mapaCategria.get("categoriaSeleccionada"));
            PrimeFaces.current().executeScript("$(dialogoAgregarCategoria).modal('hide')");
            mapaArticulo.put("articulo", articuloImpl.buscarArticuloSinCategoriaPorGenero(sesion.getUsuarioSesion().getId()));
        } else {
            FacesUtils.addInfoMessage("Seleccione al menos dos subcategorías");
        }
    }

    public void iniciarRegistroCategoria() {
        nombre = "";
        codigo = "";
        PrimeFaces.current().executeScript("$(registrarCategoria).modal('show');");
    }

    public void cerrarRegistrarCategoria() {
        PrimeFaces.current().executeScript("$(registrarCategoria).modal('hide');");
        nombre = "";
        codigo = "";
    }

    public void registrarCategoria() {
        CategoriaVo cat = new CategoriaVo();
        cat.setNombre(getNombre());
        cat.setCodigo(getCodigo());
        setCodigo("");
        setNombre("");
        siCategoriaImpl.guardar(sesion.getUsuarioSesion().getId(), cat);
        PrimeFaces.current().executeScript("$(registrarCategoria).modal('hide');");
    }

    public void seleccionarCategoria(SelectEvent<CategoriaVo> event) {
        categoriaVo = (CategoriaVo) event.getObject();
        llenarCategoria(categoriaVo.getId());
        //
        mapaCategria.put("categoriaTemporal", new ArrayList<>());
        //setListaCategoría(new ArrayList<>());
        traerListaCategoria(categoriaVo.getId());
    }

    public void traerListaCategoria(int idCat) {
        mapaCategria.put("categoria", siCategoriaImpl.traerSubCategorias(idCat));
    }

    public void llenarCategoria(int idCategoria) {
        mapaCategria.get("categoriaSeleccionada");
        CategoriaVo lcs = siRelCategoriaImpl.traerCategoriaPorCategoria(idCategoria, null, sesion.getUsuarioSesion().getIdCampo());
        mapaCategria.get("categoriaSeleccionada").add(lcs);
    }

    public void seleccionarCategoriaCabecera(int id) {
        //
        traerSubcategoria(id);
    }

    private void traerSubcategoria(int indice) {
        categoriaVo = mapaCategria.get("categoriaSeleccionada").get(indice);
        if (indice == 0) {
            mapaCategria.put("categoriaSeleccionada", new ArrayList<>());
            iniciarCatSel();
            mapaCategria.put("categoria", siCategoriaImpl.traerCategoriaPrincipales());
            mapaCategria.put("categoriaTemporal", new ArrayList<>());
        } else {
            mapaCategria.put("categoria", siCategoriaImpl.traerSubCategorias(categoriaVo.getId()));
            if ((indice + 1) < mapaCategria.get("categoriaSeleccionada").size()) {
                for (int i = (mapaCategria.get("categoriaSeleccionada").size() - 1); i > indice; i--) {
                    mapaCategria.get("categoriaSeleccionada").remove(i);
                }
            }
        }
    }

    private void iniciarCatSel() {
        CategoriaVo c = new CategoriaVo();
        c.setNombre("Pricipales");
        c.setId(Constantes.CERO);
        mapaCategria.get("categoriaSeleccionada").add(c);
    }

    public void iniciarEnlaceCategoria() {
        mapaCategria.put("categoriaTemporal", new ArrayList<>());
        mapaCategria.put("categoriaRelacion", siCategoriaImpl.traerCategoriaMenosPrincipales());
    }

    public void eliminarCategoria(int id) {
        siRelCategoriaImpl.eliminarRelacion(sesion.getUsuarioSesion().getId(), mapaCategria.get("categoria").get(id).getIdPadre());
        mapaCategria.get("categoria").remove(id);
    }

    public void cancelarGuardarCategoria() {
        mapaCategria.put("categoriaTemporal", new ArrayList<>());
        mapaCategria.put("categoriaRelacion", new ArrayList<>());

    }

    public void terminarGuardarCategoria() {
        if (!mapaCategria.get("categoriaTemporal").isEmpty()) {
            siRelCategoriaImpl.guardar(sesion.getUsuarioSesion().getId(), mapaCategria.get("categoriaTemporal"), categoriaVo.getId());
            for (CategoriaVo listaCategoriaTemporal1 : mapaCategria.get("categoriaTemporal")) {
                categoriaVo.getListaCategoria().add(categoriaVo.getListaCategoria().size(), listaCategoriaTemporal1);
            }
            mapaCategria.put("categoriaTemporal", new ArrayList<>());
            mapaCategria.put("categoriaRelacion", new ArrayList<>());
        } else {
            FacesUtils.addInfoMessage("Seleccione al menos una categoría");
        }

    }

    public void seleccionarCategoriaAgregar(SelectEvent<CategoriaVo> event) {
        mapaCategria.get("categoriaTemporal").add(event.getObject());
        mapaCategria.get("categoriaRelacion").remove(event.getObject());
    }

    public void eliminarCategoriaTemporal(int id) {
        mapaCategria.get("categoriaRelacion").add(siCategoriaImpl.buscarCategoriaPorId(mapaCategria.get("categoriaTemporal").get(id).getId()));
        //
        mapaCategria.get("categoriaTemporal").remove(id);
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the categoriaVo
     */
    public CategoriaVo getCategoriaVo() {
        return categoriaVo;
    }

    /**
     * @param categoriaVo the categoriaVo to set
     */
    public void setCategoriaVo(CategoriaVo categoriaVo) {
        this.categoriaVo = categoriaVo;
    }

    /**
     * @return the mapaCategria
     */
    public Map<String, List<CategoriaVo>> getMapaCategria() {
        return mapaCategria;
    }

    /**
     * @param mapaCategria the mapaCategria to set
     */
    public void setMapaCategria(Map<String, List<CategoriaVo>> mapaCategria) {
        this.mapaCategria = mapaCategria;
    }

    /**
     * @return the mapaArticulo
     */
    public Map<String, List<ArticuloVO>> getMapaArticulo() {
        return mapaArticulo;
    }

    /**
     * @param mapaArticulo the mapaArticulo to set
     */
    public void setMapaArticulo(Map<String, List<ArticuloVO>> mapaArticulo) {
        this.mapaArticulo = mapaArticulo;
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
}
