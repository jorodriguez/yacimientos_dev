/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.categoria.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.excepciones.SIAException;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.SatArticuloVO;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "categoriaBean_old")
@javax.enterprise.context.RequestScoped
public class CategoriaBean implements Serializable {

    /**
     * Creates a new instance of CategoriaBean
     */
    public CategoriaBean() {
    }
    @Inject
    private CategoriaModel categoriaModel;

    public List<CategoriaVo> getListaCatPrin() {
        try {
            return categoriaModel.getCategoriaVo().getListaCategoria();
        } catch (Exception ex) {
            Logger.getLogger(CategoriaBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void imprimirArticulosCategoria() {
        categoriaModel.imprimirArticulosCategoria();
    }

    public List<SelectItem> getUnidad() {
        return categoriaModel.getUnidad();
    }

    public void agregarCategoria() {
        categoriaModel.traerListaCategoria();
        categoriaModel.setAdminCategoria(true);
        categoriaModel.setMostrarArticulos(false);
    }

    public void seleccionarCategoriaAgregar(SelectEvent event) {
        CategoriaVo con = (CategoriaVo) event.getObject();
        categoriaModel.agregarCategoriaATemporal(con);
    }
//
//    public void seleccionarCategoria(SelectEvent event) {
//        CategoriaVo con = (CategoriaVo) event.getObject();
//        //categoriaModel.getCategoriaVo().setId(con.getId());
//        categoriaModel.llenarCategoria(con.getId());
//        //
//        categoriaModel.setListaCategoriaTemporal(new ArrayList<CategoriaVo>());
//        //categoriaModel.setListaCategoría(new ArrayList<CategoriaVo>());
//        categoriaModel.traerListaCategoria();
//        if (categoriaModel.getCategoriaVo().getListaCategoria() == null || categoriaModel.getCategoriaVo().getListaCategoria().isEmpty()) {
//            categoriaModel.verArticulos();
//            categoriaModel.setMostrarArticulos(true);
//        }
//        categoriaModel.traerArticulosItemsLstCat();
//    }
//
//    public void seleccionarCategoriaCabecera() {
//        //int id = Integer.parseInt(FacesUtils.getRequestParameter("idCatSelecionada"));
//        int id = Integer.parseInt(FacesUtils.getRequestParameter("indiceCatSel"));
//        //
//        categoriaModel.traerSubcategoria(id);
//        categoriaModel.traerArticulosItemsLstCat();
//    }

   /* public void seleccionarCategoriaCabecera() {
        categoriaModel.traerSubcategoria(0);
        categoriaModel.traerArticulosItemsLstCat();
    }
    */

    public void eliminarCategoria() {
        int id = Integer.parseInt(FacesUtils.getRequestParameter("idCategoria"));
        categoriaModel.eliminarCategoria(id);
    }

    public void eliminarCategoriaTemporal() {
        int id = Integer.parseInt(FacesUtils.getRequestParameter("idCatTablaTemp"));
        categoriaModel.eliminarCategoriaTemporal(id);
    }

    public void cancelarGuardarCategoria() {
        categoriaModel.cancelarGuardarCategoria();
        categoriaModel.setAdminCategoria(false);
        categoriaModel.setMostrarArticulos(true);
    }

    public void terminarGuardarCategoria() {
        categoriaModel.terminarGuardarCategoria();
        categoriaModel.setAdminCategoria(false);
    }

    public void cerrarGuardarArticulo() {
        categoriaModel.setArticulo("");
    }

    public void guardarArticulo() {
        try {
            categoriaModel.guardarArticulo();
            categoriaModel.verArticulos();
            PrimeFaces.current().executeScript(";enviarNotificacionNuevoItem();");
        } catch (SIAException e) {
            if ("NoParte".equals(e.getMessage())) {
                FacesUtils.addErrorMessage("El No. Parte ya existe en el catálogo de articulos del Sia.");
                PrimeFaces.current().executeScript(";$(registrarArticulo).modal('show');mostrarDiv('registrarArticulo');");
            } else if ("NoCampos".equals(e.getMessage())) {
                FacesUtils.addErrorMessage("Se requiere se seleccione al menos un proyecto.");
                PrimeFaces.current().executeScript(";$(registrarArticulo).modal('show');mostrarDiv('registrarArticulo');");

            }
        } catch (Exception e) {
            Logger.getLogger(CategoriaBean.class.getName()).log(Level.SEVERE, null, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    

    //
    public void verArticulos() {
        categoriaModel.verArticulos();
    }

    public void cerrarVerArticulos() {
        categoriaModel.cerrarVerArticulos();
    }

    public void editarArticulo() {
        int i = Integer.parseInt(FacesUtils.getRequestParameter("indexTbl"));
        //String nom = FacesUtils.getRequestParameter("articuloNombre");
        try {
            categoriaModel.editarArticulo(i, "");
        } catch (SIAException ex) {
            Logger.getLogger(CategoriaBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        categoriaModel.getListaArticulos().get(i).setEditar(false);
    }

    public void inicioEditarArticulo() {
        int i = Integer.parseInt(FacesUtils.getRequestParameter("articulo"));
        if (categoriaModel.validaEditarArticulo(i)) {
            FacesUtils.addInfoMessage("El artículo no se puede modificar.");
        } else {
            categoriaModel.getListaArticulos().get(i).setEditar(true);
        }
    }

    public void eliminarArticulo() {
        int i = Integer.parseInt(FacesUtils.getRequestParameter("eliArt"));
        if (categoriaModel.validaEditarArticulo(i)) {
            FacesUtils.addInfoMessage("El artículo no se puede eliminar.");
        } else {
            try {
                categoriaModel.eliminarArticulo(i);
                categoriaModel.getListaArticulos().remove(i);
            } catch (SIAException ex) {
                Logger.getLogger(CategoriaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void registrarCategoria() {
        categoriaModel.registroCategoria();
        PrimeFaces.current().executeScript(";$(registrarCategoria).modal('hide');");
    }

    public void cerrarRegistrarCategori() {
        categoriaModel.setNombreCategoria("");
        categoriaModel.setCodigoCategoria("");
    }

    public void seleccinarArticulos() {
        categoriaModel.seleccinarArticulos();
        categoriaModel.llenarCategoriaCambiar();
    }

    public void cambiarArticulo() {
        categoriaModel.cambiarArticulo();
        PrimeFaces.current().executeScript(";$(dialogoCambiarArticulos).modal('hide');");
        //
        FacesUtils.addInfoMessage("Se cambió el artículo.");
    }

    public void seleccionarCategoriaCambio(SelectEvent event) {
        CategoriaVo c = (CategoriaVo) event.getObject();
        categoriaModel.traerSubCategoriaCambiar(c.getId());
    }

    public void seleccionarCambiarCategoriaCabecera() {
        int id = Integer.parseInt(FacesUtils.getRequestParameter("indiceCatCamSel"));
        //int idCat = Integer.parseInt(FacesUtils.getRequestParameter("idCatCamSel"));
        //categoriaModel.traerSubCategoriaCambiar(idCat);
        categoriaModel.limpiarCatCabCaecera(id);
    }

    public void seleccionarArticulo(SelectEvent event) {
        ArticuloVO con = (ArticuloVO) event.getObject();
        if (con != null) {
            seleccionarArticulo(con);
        }
    }

    public void seleccionarArticulo(ArticuloVO vvo) {
        categoriaModel.setArticuloVO(new ArticuloVO());
        categoriaModel.setArticuloVO(vvo);
        categoriaModel.llenarDatosCambiarArticulo();
        PrimeFaces.current().executeScript(";$(dialogoAgregarArticuloCampo).modal('show');;");
    }

    public void cerrarCambiarArticulos() {
        categoriaModel.setArticuloVO(null);
    }

    public void quitarArticuloCampo() {
        int indi = Integer.parseInt(FacesUtils.getRequestParameter("idRelCamArt"));
        categoriaModel.quitarArticuloCampo(indi);
    }

    public void agregarArticuloCampo() {
        categoriaModel.agregarArticuloCampo();
        PrimeFaces.current().executeScript(";$(dialogoAgregarArticuloCampo).modal('hide');;");
        categoriaModel.setArticuloVO(null);
        FacesUtils.addInfoMessage("Se agregó el artículo a los campos seleccionados");
    }

    /**
     * @return the categoriaVo
     */
    public CategoriaVo getCategoriaVo() {
        return categoriaModel.getCategoriaVo();
    }

    /**
     * @param categoriaVo the categoriaVo to set
     */
    public void setCategoriaVo(CategoriaVo categoriaVo) {
        categoriaModel.setCategoriaVo(categoriaVo);
    }

    /**
     * @return the categoriasSeleccionadas
     */
    public List<CategoriaVo> getCategoriasSeleccionadas() {
        return categoriaModel.getCategoriasSeleccionadas();
    }

    /**
     * @param categoriaModel the categoriaModel to set
     */
    public void setCategoriaModel(CategoriaModel categoriaModel) {
        this.categoriaModel = categoriaModel;
    }

    /**
     * @return the listaCategoriaTemporal
     */
    public List<CategoriaVo> getListaCategoriaTemporal() {
        return categoriaModel.getListaCategoriaTemporal();
    }

    /**
     * @param listaCategoriaTemporal the listaCategoriaTemporal to set
     */
    public void setListaCategoriaTemporal(List<CategoriaVo> listaCategoriaTemporal) {
        categoriaModel.setListaCategoriaTemporal(listaCategoriaTemporal);
    }

    /**
     * @return the listaCategoría
     */
    public List<CategoriaVo> getListaCategoría() {
        return categoriaModel.getListaCategoría();
    }

    /**
     * @param listaCategoría the listaCategoría to set
     */
    public void setListaCategoría(List<CategoriaVo> listaCategoría) {
        categoriaModel.setListaCategoría(listaCategoría);
    }

    /**
     * @return the agregarCategoria
     */
    public boolean isAdminCategoria() {
        return categoriaModel.isAdminCategoria();
    }

    /**
     * @param adminCategoria the agregarCategoria to set
     */
    public void setAdminCategoria(boolean adminCategoria) {
        categoriaModel.setAdminCategoria(adminCategoria);
    }

    /**
     * @return the articulo
     */
    public String getArticulo() {
        return categoriaModel.getArticulo();
    }

    /**
     * @param articulo the articulo to set
     */
    public void setArticulo(String articulo) {
        categoriaModel.setArticulo(articulo);
    }

    /**
     * @return the idUnidad
     */
    public int getIdUnidad() {
        return categoriaModel.getIdUnidad();
    }

    /**
     * @param idUnidad the idUnidad to set
     */
    public void setIdUnidad(int idUnidad) {
        categoriaModel.setIdUnidad(idUnidad);
    }

    /**
     * @return the listaArticulos
     */
    public List<ArticuloVO> getListaArticulos() {
        return categoriaModel.getListaArticulos();
    }

    /**
     * @return the nombreCategoria
     */
    public String getNombreCategoria() {
        return categoriaModel.getNombreCategoria();
    }

    /**
     * @param nombreCategoria the nombreCategoria to set
     */
    public void setNombreCategoria(String nombreCategoria) {
        categoriaModel.setNombreCategoria(nombreCategoria);
    }

    /**
     * @return the codigoCategoria
     */
    public String getCodigoCategoria() {
        return categoriaModel.getCodigoCategoria();
    }

    /**
     * @param codigoCategoria the codigoCategoria to set
     */
    public void setCodigoCategoria(String codigoCategoria) {
        categoriaModel.setCodigoCategoria(codigoCategoria);
    }

    /**
     * @return the listaCampo
     */
    public List<CampoUsuarioPuestoVo> getListaCampo() {
        return categoriaModel.getListaCampo();
    }

    /**
     * @return the verArticulos
     */
    public boolean isMostrarArticulos() {
        return categoriaModel.isMostrarArticulos();
    }

    /**
     * @param verArticulos the verArticulos to set
     */
    public void setMostrarArticulos(boolean verArticulos) {
        categoriaModel.setMostrarArticulos(verArticulos);
    }

    /**
     * @return
     */
    public List<ArticuloVO> getListaCambiarArticulos() {
        return categoriaModel.getListaCambiarArticulos();
    }

    /**
     * @return the listaCambiarSeleccionada
     */
    public List<CategoriaVo> getListaCambiarSeleccionada() {
        return categoriaModel.getListaCambiarSeleccionada();
    }

    /**
     * @return the listaCategoriaCambiar
     */
    public List<CategoriaVo> getListaCategoriaCambiar() {
        return categoriaModel.getListaCategoriaCambiar();
    }

    /**
     * @return the ArticuloVO
     */
    public ArticuloVO getArticuloVO() {
        return categoriaModel.getArticuloVO();
    }

    /**
     * @param ArticuloVO the ArticuloVO to set
     */
    public void setArticuloVO(ArticuloVO ArticuloVO) {
        categoriaModel.setArticuloVO(ArticuloVO);
    }

    /**
     * @return the numParte
     */
    public String getNumParte() {
        return categoriaModel.getNumParte();
    }

    /**
     * @param numParte the numParte to set
     */
    public void setNumParte(String numParte) {
        categoriaModel.setNumParte(numParte);
    }

    /**
     * @return the listaCampoPorArticulo
     */
    public List<CampoVo> getListaCampoPorArticulo() {
        return categoriaModel.getListaCampoPorArticulo();
    }

    /**
     * @return the listaNoCampoPorArticulo
     */
    public List<CampoVo> getListaNoCampoPorArticulo() {
        return categoriaModel.getListaNoCampoPorArticulo();
    }

    /**
     * @return the usuarioNotificar
     */
    public String getUsuarioNotificar() {
        return categoriaModel.getUsuarioNotificar();
    }

    /**
     * @param usuarioNotificar the usuarioNotificar to set
     */
    public void setUsuarioNotificar(String usuarioNotificar) {
        categoriaModel.setUsuarioNotificar(usuarioNotificar);
    }

    /**
     * @return the listaUsuario
     */
    public List<SelectItem> getListaUsuario() {
        return categoriaModel.getListaUsuario();
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<SelectItem> listaUsuario) {
        categoriaModel.setListaUsuario(listaUsuario);
    }

    public void usuarioListener(String texto) {
        categoriaModel.setListaUsuario(texto);
    }

    public void enviarNotificacionAltaArticulo() {
        try {
            categoriaModel.enviarNotificacionAltaArticulo();
            PrimeFaces.current().executeScript(";cerrarDialogoBootstrap(dialogoNotificarNuevoItem);");
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

//    public void traerArticulosItemsListener(String cadena) {
//        if ((cadena != null && !cadena.isEmpty() && cadena.length() > 2)
//                || (cadena == null || cadena.isEmpty())) {
//            categoriaModel.traerArticulosItemsLst(cadena);
//        }
//    }

    /**
     * @return the articuloTx
     */
    public String getArticuloTx() {
        return categoriaModel.getArticuloTx();
    }

    /**
     * @param articuloTx the articuloTx to set
     */
    public void setArticuloTx(String articuloTx) {
        categoriaModel.setArticuloTx(articuloTx);
    }

    /**
     * @return the articulosResultadoBqda
     */

    /**
     * @param articulosResultadoBqda the articulosResultadoBqda to set
     */

    public void seleccionarResultadoBA(SelectEvent event) {
        try {
            SelectItem artItem = (SelectItem) event.getObject();
            if (artItem != null && artItem.getValue() != null && ((ArticuloVO) artItem.getValue()).getId() > 0) {
                setArticuloTx(new StringBuilder().append(((ArticuloVO) artItem.getValue()).getNombre())
                        .append("=>").append(((ArticuloVO) artItem.getValue()).getNumParte())
                        .toString().toLowerCase());
                categoriaModel.cambiarArticuloBda();
                ArticuloVO vvoo = (ArticuloVO) artItem.getValue();
                seleccionarArticulo(vvoo);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    /**
     * @return the satArticulosResultadoBqda
     */
    public List<SelectItem> getSatArticulosResultadoBqda() {
        return categoriaModel.getSatArticulosResultadoBqda();
    }

    /**
     * @param satArticulosResultadoBqda the satArticulosResultadoBqda to set
     */
    public void setSatArticulosResultadoBqda(List<SelectItem> satArticulosResultadoBqda) {
        categoriaModel.setSatArticulosResultadoBqda(satArticulosResultadoBqda);
    }

    /**
     * @return the articuloSatTx
     */
    public String getArticuloSatTx() {
        return categoriaModel.getArticuloSatTx();
    }

    /**
     * @param articuloSatTx the articuloSatTx to set
     */
    public void setArticuloSatTx(String articuloSatTx) {
        categoriaModel.setArticuloSatTx(articuloSatTx);
    }

    public void seleccionarResultadoBASat(SelectEvent event) {
        try {
            SelectItem artItem = (SelectItem) event.getObject();
            if (artItem != null && artItem.getValue() != null) {
                categoriaModel.guardarArticuloSat(((SatArticuloVO) artItem.getValue()).getId());
            }
            PrimeFaces.current().executeScript(";minimizarArtSat();");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx)");
        }
    }

    public void traerArticulosItemsListenerSat(String cadena) {
        if ((cadena != null && !cadena.isEmpty() && cadena.length() > 2)
                || (cadena == null || cadena.isEmpty())) {
            categoriaModel.traerArticulosItemsLstSat(cadena);
        }
        //PrimeFaces.current().executeScript(";marcarBusqueda();");
    }

    /**
     * @return the idBloque
     */
    public int getIdBloque() {
        return categoriaModel.getIdBloque();
    }

    /**
     * @param idBloque the idBloque to set
     */
    public void setIdBloque(int idBloque) {
        categoriaModel.setIdBloque(idBloque);
    }
}
