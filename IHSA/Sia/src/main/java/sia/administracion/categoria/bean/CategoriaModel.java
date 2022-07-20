/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.categoria.bean;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.inventarios.service.ArticuloRemote;
import sia.inventarios.service.InvArticuloCampoImpl;
import sia.inventarios.service.SatArticuloImpl;
import sia.modelo.InvArticulo;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.GeneralVo;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.requisicion.impl.RequisicionDetalleImpl;
import sia.servicios.sistema.impl.SiCategoriaImpl;
import sia.servicios.sistema.impl.SiRelCategoriaImpl;
import sia.servicios.sistema.impl.SiUnidadImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.sistema.bean.support.SoporteListas;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "categoriaBean")
@ViewScoped
public class CategoriaModel implements Serializable {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of CategoriaModel
     */
    public CategoriaModel() {
    }
    @Inject
    private SiCategoriaImpl siCategoriaLocal;
    @Inject
    private SiRelCategoriaImpl siRelCategoriaLocal;
    @Inject
    private SiUnidadImpl siUnidadImpl;
    @Inject
    private ArticuloRemote articuloImpl;
    @Inject
    private InvArticuloCampoImpl invArticuloCampoImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private RequisicionDetalleImpl requisicionDetalleImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private NotificacionRequisicionImpl notificacionRequisicionImpl;
    @Inject
    private SatArticuloImpl satArticuloImpl;
    @Inject
    SiRelCategoriaImpl siRelCategoriaImpl;

    private SoporteListas soporteListas = (SoporteListas) FacesUtils.getManagedBean("soporteListas");

    @Inject
    private Sesion sesion;
    private List<CategoriaVo> categoriasSeleccionadas = new ArrayList<>();
    private CategoriaVo categoriaVo;
    private List<CategoriaVo> listaCategoriaTemporal = new ArrayList<>();
    private List<CategoriaVo> listaCategoría = new ArrayList<>();
    //
    private List<SelectItem> unidad;
    private boolean adminCategoria = false;
    //
    private String articulo;
    private int idUnidad;
    private String nombreCategoria;
    private String codigoCategoria;
    //
    private List<ArticuloVO> listaArticulos;
    private List<CampoUsuarioPuestoVo> listaCampo;
    //
    private List<ArticuloVO> listaCambiarArticulos;
    private List<CategoriaVo> listaCategoriaCambiar;
    private List<CategoriaVo> listaCambiarSeleccionada;
    @Getter
    @Setter
    private CategoriaVo categoriaTempVo;
    //
    private boolean mostrarArticulos;
    //
    private ArticuloVO articuloVO;
    private String numParte;
    //
    private List<CampoVo> listaCampoPorArticulo;
    private List<CampoVo> listaNoCampoPorArticulo;
    private String usuarioNotificar;
    private List<SelectItem> listaUsuario = new ArrayList<>();
    private int newArticuloID;
    private String articuloTx;
    private List<ArticuloVO> articulosResultadoBqda = new ArrayList<>();
    private SoporteCategorias soporteCategorias = (SoporteCategorias) FacesUtils.getManagedBean("soporteCategorias");
    private List<SelectItem> satArticulosResultadoBqda = new ArrayList<>();
    private String articuloSatTx = "";
    private int idBloque;

    @PostConstruct
    public void iniciar() {
        categoriaTempVo = new CategoriaVo();
        categoriaVo = new CategoriaVo();
        listaArticulos = new ArrayList<>();
        setIdBloque(sesion.getUsuario().getApCampo().getId());
        articulosResultadoBqda = new ArrayList<>();
        listaCategoría = new ArrayList<>();
        categoriaVo.setListaCategoria(siCategoriaLocal.traerCategoriaPrincipales());
        //
        iniciarCatSel();
        //
        llenarUnidad(siUnidadImpl.traerUnidad());
        listaCampoUsuario();
        listaCategoría = invArticuloCampoImpl.traerCategoriaArticulo();
        //////////////////        
    }

    public void imprimirArticulosCategoria() {
        invArticuloCampoImpl.articulos(4);
    }

    public void llenarUnidad(List<GeneralVo> listaUnidad) {
        unidad = new ArrayList<>();
        for (GeneralVo listaUnidad1 : listaUnidad) {
            getUnidad().add(new SelectItem(listaUnidad1.getValor(), listaUnidad1.getNombre()));
        }
    }

    public void listaCampoUsuario() {
        listaCampo = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuario().getId());
    }

    public void registroCategoria() {
        CategoriaVo ca = new CategoriaVo();
        ca.setNombre(getNombreCategoria());
        ca.setCodigo(getCodigoCategoria().toUpperCase());
        siCategoriaLocal.guardar(sesion.getUsuario().getId(), ca);
        setNombreCategoria("");
        setCodigoCategoria("");
        categoriaVo.setListaCategoria(siCategoriaLocal.traerCategoriaPrincipales());
    }

    public void llenarCategoria(int idCategoria) {
        categoriaVo = siRelCategoriaLocal.traerCategoriaPorCategoria(idCategoria, null, this.getIdBloque());
        categoriasSeleccionadas.add(categoriaVo);
    }

    public void traerSubcategoria(int indice) {
        CategoriaVo c = categoriasSeleccionadas.get(indice);
        if (indice == 0) {
            listaCategoría = invArticuloCampoImpl.traerCategoriaArticulo();
            categoriaVo = new CategoriaVo();
            // categoriaVo.setListaCategoria(siCategoriaLocal.traerCategoriaPrincipales());
            categoriasSeleccionadas = new ArrayList<>();
            iniciarCatSel();
            setAdminCategoria(false);
        } else {
            setCategoriaVo(siRelCategoriaImpl.traerCategoriaPorCategoria(c.getId(), getSoloCodigos(getCategoriasSeleccionadas().subList(0, indice)), sesion.getUsuarioVo().getIdCampo()));
            if (c.getId() != categoriaVo.getId()) {
                categoriasSeleccionadas.add(categoriaVo);// limpiar lista seleccionadas
            }
            if ((indice + 1) < categoriasSeleccionadas.size()) {
                for (int i = (categoriasSeleccionadas.size() - 1); i > indice; i--) {
                    categoriasSeleccionadas.remove(i);
                }
            }
        }

        if (categoriasSeleccionadas.size() < 3) {
            setMostrarArticulos(false);
            listaArticulos.clear();
            articulosResultadoBqda = articuloImpl.obtenerArticulos(null, sesion.getUsuarioVo().getIdCampo(), 0,
                    getCodigos(
                            getCategoriasSeleccionadas().size() > 1
                            ? getCategoriasSeleccionadas().subList(1, getCategoriasSeleccionadas().size())
                            : new ArrayList<>()));
        } else {
            verArticulos();
        }

    }

    public void abrirPopUpArticulo() {
        try {
            this.setNumParte("");
            this.setArticulo("");
            PrimeFaces.current().executeScript(";$(registrarArticulo).modal('show');mostrarDiv('registrarArticulo');");
        } catch (Exception e) {
            Logger.getLogger(CategoriaBean.class.getName()).log(Level.SEVERE, null, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void agregarCategoria() {
        traerListaCategoria();
        setAdminCategoria(true);
        setMostrarArticulos(false);
    }

    public void traerListaCategoria() {
        listaCategoría = siCategoriaLocal.traerCategoriMenosPrincipalMenosSubcategorias(categoriaVo.getId());
    }

    private void iniciarCatSel() {
        CategoriaVo c = new CategoriaVo();
        c.setNombre("Pricipales");
        c.setId(Constantes.CERO);
        categoriasSeleccionadas.add(c);
    }

    public void agregarCategoriaATemporal(CategoriaVo cat) {
        listaCategoriaTemporal.add(cat);
        listaCategoría.remove(cat);
    }

    public void eliminarCategoriaTemporal(int indice) {
        listaCategoría.add(listaCategoría.size(), siCategoriaLocal.buscarCategoriaPorId(listaCategoriaTemporal.get(indice).getId()));
        //
        listaCategoriaTemporal.remove(indice);
    }

    public void cancelarGuardarCategoria() {
        //for (CategoriaVo categoriaTemp : listaCategoriaTemporal) {
        //    listaCategoría.add(listaCategoría.size(), categoriaTemp);
        //}
        listaCategoría = new ArrayList<>();
        listaCategoriaTemporal = new ArrayList<>();
    }

    public void terminarGuardarCategoria() {
        siRelCategoriaLocal.guardar(sesion.getUsuario().getId(), listaCategoriaTemporal, categoriaVo.getId());
        for (CategoriaVo listaCategoriaTemporal1 : listaCategoriaTemporal) {
            categoriaVo.getListaCategoria().add(categoriaVo.getListaCategoria().size(), listaCategoriaTemporal1);
        }
        listaCategoriaTemporal = new ArrayList<>();
        listaCategoría = new ArrayList<>();
    }

    public void eliminarCategoria(int indiceCategoria) {
        //invArticuloCampoImpl.traerCategoriaArticulo();
        siRelCategoriaLocal.eliminarRelacion(sesion.getUsuario().getId(), categoriaVo.getListaCategoria().get(indiceCategoria).getIdPadre());
        categoriaVo.getListaCategoria().remove(indiceCategoria);
    }

    public void seleccionarCategoria(SelectEvent<CategoriaVo> event) {
        try {
            CategoriaVo con = (CategoriaVo) event.getObject();
            //out.println("Categoría selec:" + con.getNombre());
            //setCategoriaVo(con);
            //
            categoriaVo = siRelCategoriaImpl.traerCategoriaPorCategoria(con.getId(), getSoloCodigos(categoriasSeleccionadas), sesion.getUsuarioVo().getIdCampo());
            //.out.println("Categoría rec: " + categoriaVo.getNombre() + " cats: " + categoriaVo.getListaCategoria());
            listaCategoría = categoriaVo.getListaCategoria();
            //
            //.out.println("Seleccionadas : " + categoriasSeleccionadas.size());
            //llenarCategoria(getSoloCodigos(getCategoriasSeleccionadas()));
            categoriasSeleccionadas.add(categoriaVo);
            if (getCategoriasSeleccionadas() != null && getCategoriasSeleccionadas().size() > 1) {
                setArticuloTx("");
                articulosResultadoBqda = articuloImpl.obtenerArticulos(
                        null, sesion.getUsuarioVo().getIdCampo(),
                        0, getCodigos(getCategoriasSeleccionadas().size() > 1
                                ? getCategoriasSeleccionadas().subList(1, getCategoriasSeleccionadas().size())
                                : new ArrayList<>()));
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    public void cerrarCambiarArticulos() {
        setArticuloVO(null);
    }
    public void seleccionarResultadoBA(SelectEvent event) {
        try {
            articuloVO = (ArticuloVO) event.getObject();
            llenarDatosCambiarArticulo();
            PrimeFaces.current().executeScript(";$(dialogoAgregarArticuloCampo).modal('show');;");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void seleccionarArticulo(ArticuloVO vvo) {
    }

    public void guardarArticulo() throws SIAException {
        // guardar articulo
        List<CampoVo> ltemp = new ArrayList<CampoVo>();
        for (CampoUsuarioPuestoVo listaCampoUsuario : getListaCampo()) {
            if (listaCampoUsuario.isSelected()) {
                ltemp.add(new CampoVo(listaCampoUsuario.getIdCampo(), listaCampoUsuario.getCampo()));
                listaCampoUsuario.setSelected(false);
            }
        }
        if (ltemp.size() > 0) {
            articuloVO = new ArticuloVO();
            articuloVO.setNombre(articulo);
            //articuloVO.setCampoID(listaCampoUsuario.getIdCampo());
            articuloVO.setUnidadId(idUnidad);
            articuloVO.setDescripcion(articulo);
            articuloVO.setCodigo(articuloImpl.construirCodigo(categoriasSeleccionadas));
            if (categoriasSeleccionadas != null && categoriasSeleccionadas.size() > 0) {
                articuloVO.setIdRel(categoriasSeleccionadas.get(categoriasSeleccionadas.size() - 1).getId());
            }
            try {
                setNewArticuloID(articuloImpl.guardarArticulo(articuloVO, sesion.getUsuario().getId(), ltemp, categoriasSeleccionadas, getNumParte().trim()));
                //articulo = "";
                FacesUtils.addInfoMessage("El artículo fue registrado en el catálogo correctamente.");
            } catch (EJBException e) {
                LOGGER.info(this, "", e);
                throw new SIAException("NoParte");
            } catch (SIAException e) {
                LOGGER.info(this, "", e);
            }
        } else {
            throw new SIAException("NoCampos");
        }
        articulo = "";
    }

    public void verArticulos() {
        listaArticulos = articuloImpl.obtenerArticulos(null, this.getIdBloque(), this.getCategoriaVo().getId(),
                catCodigo(this.getCategoriasSeleccionadas().size() > 2
                        ? this.getCategoriasSeleccionadas().subList(2, this.getCategoriasSeleccionadas().size())
                        : new ArrayList<>()
                ));
        //
    }

    public void cerrarVerArticulos() {
//	listaArticulos = new ArrayList<ArticuloVO>();
    }

    private String catCodigo(List<CategoriaVo> categorias) {
        StringBuilder codigosTxt = new StringBuilder();
        for (CategoriaVo cat : categorias) {
            codigosTxt.append(" and upper(a.CODIGO) like upper('%").append(cat.getCodigo()).append("%') ");
        }
        return codigosTxt.toString();
    }

    private String getSoloCodigos(List<CategoriaVo> categorias) {
        String codigosTxt = "";
        for (CategoriaVo cat : categorias) {
            if (codigosTxt.isEmpty() && cat.getId() > 0) {
                codigosTxt = cat.getId() + "";
            } else if (cat.getId() > 0) {
                codigosTxt = codigosTxt + "," + cat.getId();
            }
        }
        return codigosTxt;
    }

    public void editarArticulo(int idAvo, String art) throws SIAException {
        ArticuloVO avo = articuloImpl.buscar(getListaArticulos().get(idAvo).getId(), this.getIdBloque());
        /*
	 articulo.setCodigo(articuloVO.getCodigo());
	 articulo.setNombre(articuloVO.getNombre());
	 articulo.setDescripcion(articuloVO.getDescripcion());
	 articulo.setUnidad(new SiUnidad(articuloVO.getUnidadId()));
         */
        if (avo != null) {
            avo.setNombre(getListaArticulos().get(idAvo).getNombre());
            avo.setDescripcion(getListaArticulos().get(idAvo).getNombre());
            articuloImpl.actualizar(avo, sesion.getUsuario().getId(), this.getIdBloque());
        }
    }

    public boolean validaEditarArticulo(int idArt) {
        return requisicionDetalleImpl.articuloEnRequisicion(getListaArticulos().get(idArt).getId());
    }

    public void eliminarArticulo(int id) throws SIAException {
        articuloImpl.eliminar(getListaArticulos().get(id).getIdRel(), sesion.getUsuario().getId(), this.getIdBloque());
    }

    public void seleccinarArticulos() {
        listaCambiarArticulos = new ArrayList<ArticuloVO>();
        for (ArticuloVO listaArticulo : listaArticulos) {
            if (listaArticulo.isSelected()) {
                listaCambiarArticulos.add(listaArticulo);
            }
        }
    }

    public void llenarCategoriaCambiar() {
        listaCategoriaCambiar = siCategoriaLocal.traerCategoriaPrincipales();
        listaCambiarSeleccionada = new ArrayList<>();
        CategoriaVo c = new CategoriaVo();
        c.setNombre("Pricipales");
        c.setId(Constantes.CERO);
        listaCambiarSeleccionada.add(c);
    }

    public void traerSubCategoriaCambiar(int cv) {
        CategoriaVo c = siRelCategoriaLocal.traerCategoriaPorCategoria(cv, null, this.getIdBloque());
        listaCambiarSeleccionada.add(c);
        listaCategoriaCambiar = c.getListaCategoria();
    }

    public void limpiarCatCabCaecera(int indice) {
        if (indice == 0) {
            llenarCategoriaCambiar();
        } else {
            CategoriaVo cv = siRelCategoriaLocal.traerCategoriaPorCategoria(listaCambiarSeleccionada.get(indice).getId(), null, this.getIdBloque());
            listaCategoriaCambiar = cv.getListaCategoria();
            if ((indice + 1) < listaCambiarSeleccionada.size()) {
                for (int i = (listaCambiarSeleccionada.size() - 1); i > indice; i--) {
                    listaCambiarSeleccionada.remove(i);
                }
            }
        }
    }

    public void cambiarArticulo() {
        articuloImpl.cambiarArticulo(sesion.getUsuario().getId(), listaCambiarArticulos, listaCambiarSeleccionada.subList(1, listaCambiarSeleccionada.size()));
        listaCategoriaCambiar = new ArrayList<>();
        listaCambiarSeleccionada = new ArrayList<>();
        verArticulos();
    }

    public void llenarDatosCambiarArticulo() {
        listaNoCampoPorArticulo = invArticuloCampoImpl.traerNoCampoArticulo(articuloVO.getId());
        listaCampoPorArticulo = invArticuloCampoImpl.traerCampoPorArticulo(articuloVO.getId());
    }

    public void agregarArticuloCampo() {
        List<CampoVo> ltemp = new ArrayList<CampoVo>();
        for (CampoVo campovo : listaNoCampoPorArticulo) {
            if (campovo.isSelected()) {
                ltemp.add(campovo);
            }
        }
        invArticuloCampoImpl.guardarArticuloCampo(sesion.getUsuario().getId(), articuloVO.getId(), ltemp);
        llenarDatosCambiarArticulo();
        //
        PrimeFaces.current().executeScript("$(dialogoAgregarArticuloCampo).modal('hide');");
    }

    public void quitarArticuloCampo(int idRel) {
        invArticuloCampoImpl.eliminar(sesion.getUsuario().getId(), listaCampoPorArticulo.get(idRel).getIdRelacion());
        listaNoCampoPorArticulo = invArticuloCampoImpl.traerNoCampoArticulo(listaCampoPorArticulo.get(idRel).getId());
        listaCampoPorArticulo.remove(idRel);
    }

    /**
     * @return the categoriasSeleccionadas
     */
    public List<CategoriaVo> getCategoriasSeleccionadas() {
        return categoriasSeleccionadas;
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
     * @return the listaCategoriaTemporal
     */
    public List<CategoriaVo> getListaCategoriaTemporal() {
        return listaCategoriaTemporal;
    }

    /**
     * @param listaCategoriaTemporal the listaCategoriaTemporal to set
     */
    public void setListaCategoriaTemporal(List<CategoriaVo> listaCategoriaTemporal) {
        this.listaCategoriaTemporal = listaCategoriaTemporal;
    }

    /**
     * @return the listaCategoría
     */
    public List<CategoriaVo> getListaCategoría() {
        return listaCategoría;
    }

    /**
     * @param listaCategoría the listaCategoría to set
     */
    public void setListaCategoría(List<CategoriaVo> listaCategoría) {
        this.listaCategoría = listaCategoría;
    }

    /**
     * @return the adminCategoria
     */
    public boolean isAdminCategoria() {
        return adminCategoria;
    }

    /**
     * @param adminCategoria the adminCategoria to set
     */
    public void setAdminCategoria(boolean adminCategoria) {
        this.adminCategoria = adminCategoria;
    }

    /**
     * @return the articulo
     */
    public String getArticulo() {
        return articulo;
    }

    /**
     * @param articulo the articulo to set
     */
    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    /**
     * @return the idUnidad
     */
    public int getIdUnidad() {
        return idUnidad;
    }

    /**
     * @param idUnidad the idUnidad to set
     */
    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    /**
     * @return the unidad
     */
    public List<SelectItem> getUnidad() {
        return unidad;
    }

    /**
     * @return the listaArticulos
     */
    public List<ArticuloVO> getListaArticulos() {
        return listaArticulos;
    }

    /**
     * @return the nombreCategoria
     */
    public String getNombreCategoria() {
        return nombreCategoria;
    }

    /**
     * @param nombreCategoria the nombreCategoria to set
     */
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    /**
     * @return the codigoCategoria
     */
    public String getCodigoCategoria() {
        return codigoCategoria;
    }

    /**
     * @param codigoCategoria the codigoCategoria to set
     */
    public void setCodigoCategoria(String codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    /**
     * @return the listaCampo
     */
    public List<CampoUsuarioPuestoVo> getListaCampo() {
        return listaCampo;
    }

    /**
     * @return the verArticulos
     */
    public boolean isMostrarArticulos() {
        return mostrarArticulos;
    }

    /**
     * @param mostrarArticulos the mostrarArticulos to set
     */
    public void setMostrarArticulos(boolean mostrarArticulos) {
        this.mostrarArticulos = mostrarArticulos;
    }

    /**
     */
    public List<ArticuloVO> getListaCambiarArticulos() {
        return listaCambiarArticulos;
    }

    /**
     * @return the listaCambiarSeleccionada
     */
    public List<CategoriaVo> getListaCambiarSeleccionada() {
        return listaCambiarSeleccionada;
    }

    /**
     * @return the listaCategoriaCambiar
     */
    public List<CategoriaVo> getListaCategoriaCambiar() {
        return listaCategoriaCambiar;
    }

    /**
     * @return the ArticuloVO
     */
    public ArticuloVO getArticuloVO() {
        return articuloVO;
    }

    /**
     * @param articuloVO the articuloVO to set
     */
    public void setArticuloVO(ArticuloVO articuloVO) {
        this.articuloVO = articuloVO;
    }

    /**
     * @return the numParte
     */
    public String getNumParte() {
        return numParte;
    }

    /**
     * @param numParte the numParte to set
     */
    public void setNumParte(String numParte) {
        this.numParte = numParte;
    }

    /**
     * @return the listaCampoPorArticulo
     */
    public List<CampoVo> getListaCampoPorArticulo() {
        return listaCampoPorArticulo;
    }

    /**
     * @return the listaNoCampoPorArticulo
     */
    public List<CampoVo> getListaNoCampoPorArticulo() {
        return listaNoCampoPorArticulo;
    }

    /**
     * @return the usuarioNotificar
     */
    public String getUsuarioNotificar() {
        return usuarioNotificar;
    }

    /**
     * @param usuarioNotificar the usuarioNotificar to set
     */
    public void setUsuarioNotificar(String usuarioNotificar) {
        this.usuarioNotificar = usuarioNotificar;
    }

    /**
     * @return the listaUsuario
     */
    public List<SelectItem> getListaUsuario() {
        return listaUsuario;
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<SelectItem> listaUsuario) {
        this.listaUsuario = listaUsuario;
    }

    public void setListaUsuario(String text) {
        setListaUsuario(traerUsuario(text, 0));
    }

    public List<SelectItem> traerUsuario(String cadena, int apCampo) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        try {
            if (!Strings.isNullOrEmpty(cadena) && cadena.length() > 2) {
                list = soporteListas.regresaUsuarioActivo(cadena, apCampo);
            }
        } catch (Exception e) {
            LOGGER.info(this, "", e);
            list = Collections.emptyList();
        }
        return list;
    }

    /**
     * @return the newArticuloID
     */
    public int getNewArticuloID() {
        return newArticuloID;
    }

    /**
     * @param newArticuloID the newArticuloID to set
     */
    public void setNewArticuloID(int newArticuloID) {
        this.newArticuloID = newArticuloID;
    }

    public void enviarNotificacionAltaArticulo() {
        try {
            if (this.usuarioNotificar != null && !this.usuarioNotificar.isEmpty()) {
                Usuario usr = usuarioImpl.buscarPorNombre(this.usuarioNotificar);
                if (usr != null && this.newArticuloID > 0) {
                    InvArticulo articulo = articuloImpl.find(this.newArticuloID);
                    notificacionRequisicionImpl.envioNotificacionAltaArticulo(
                            usr.getEmail(), "", "", " Notificación de registro de artículo ",
                            articulo, sesion.getUsuario().getApCampo().getCompania().getNombre(), this.getCategoriasSeleccionadas());
                }
            }
        } catch (RuntimeException e) {
            LOGGER.warn(this, "", e);
        }
    }

    /**
     * @return the articuloTx
     */
    public String getArticuloTx() {
        return articuloTx;
    }

    /**
     * @param articuloTx the articuloTx to set
     */
    public void setArticuloTx(String articuloTx) {
        this.articuloTx = articuloTx;
    }

    /**
     * @return the articulosResultadoBqda
     */
    public List<ArticuloVO> getArticulosResultadoBqda() {
        return articulosResultadoBqda;
    }

    /**
     * @param articulosResultadoBqda the articulosResultadoBqda to set
     */
    public void setArticulosResultadoBqda(List<ArticuloVO> articulosResultadoBqda) {
        this.articulosResultadoBqda = articulosResultadoBqda;
    }

    private List<SelectItem> traerArticulosItems(String cadena) {
        List<SelectItem> list;
        try {
            cadena = filtrosCadena(cadena.replace(" ", "%"));
            list = soporteCategorias.obtenerArticulosItems(cadena, this.getIdBloque(),
                    Constantes.CERO,
                    getCodigos(this.getCategoriasSeleccionadas().size() > 1
                            ? this.getCategoriasSeleccionadas().subList(1, this.getCategoriasSeleccionadas().size())
                            : new ArrayList<>()));
        } catch (Exception e) {
            list = new ArrayList<SelectItem>();
        }
        return list;
    }

    private String filtrosCadena(String cadena) {
        String[] output = cadena.split("\\%");
        StringBuilder cadenaNombre = new StringBuilder("and ((");
//        StringBuilder cadenaCodigo = new StringBuilder(") or (");
        String and = "";
        for (String s : output) {
            cadenaNombre.append(and).append("upper(a.NOMBRE||a.CODIGO_INT) like upper('%").append(s).append("%') ");
//            cadenaCodigo.append(and).append("upper(a.CODIGO_INT) like upper('%").append(s).append("%') ");                        
            and = " and ";
        }
//        return cadenaNombre.toString()+cadenaCodigo.toString()+"))";
        return cadenaNombre.toString() + "))";
    }

    private String getCodigos(List<CategoriaVo> categorias) {
        String codigosTxt = "";
        for (CategoriaVo cat : categorias) {
            codigosTxt = codigosTxt + " and upper(a.CODIGO) like upper('%" + cat.getCodigo() + "%') ";
        }
        return codigosTxt;
    }

    public void cambiarArticuloBda() {
        try {
            if (getArticuloTx() != null && !getArticuloTx().isEmpty()) {
                int aux = 2;
                String codigoInt = getArticuloTx().substring(
                        (getArticuloTx().lastIndexOf("=>") + aux));
                List<ArticuloVO> articulos = soporteCategorias.getArticulosActivo(codigoInt, this.getIdBloque(), 0, "");
                if (articulos != null && articulos.size() > 0) {
                    if (articulos.get(0).getCategorias() != null && !articulos.get(0).getCategorias().isEmpty()) {
                        String[] output = articulos.get(0).getCategorias().split(",");
                        if (output != null && output.length > 0) {
                            categoriasSeleccionadas = new ArrayList<>();
                            iniciarCatSel();
                            for (String s : output) {
                                llenarCategoria(Integer.parseInt(s));
                            }
                            setListaCategoriaTemporal(new ArrayList<>());
                            traerListaCategoria();
                            if (getCategoriaVo().getListaCategoria() == null || getCategoriaVo().getListaCategoria().size() == 0) {
                                verArticulos();
                                setMostrarArticulos(true);
                            }
                            //   traerArticulosItemsLstCat();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a soportesia@ihsa.mx");
        }
    }

    /**
     * @return the satArticulosResultadoBqda
     */
    public List<SelectItem> getSatArticulosResultadoBqda() {
        return satArticulosResultadoBqda;
    }

    /**
     * @param satArticulosResultadoBqda the satArticulosResultadoBqda to set
     */
    public void setSatArticulosResultadoBqda(List<SelectItem> satArticulosResultadoBqda) {
        this.satArticulosResultadoBqda = satArticulosResultadoBqda;
    }

    /**
     * @return the articuloSatTx
     */
    public String getArticuloSatTx() {
        return articuloSatTx;
    }

    /**
     * @param articuloSatTx the articuloSatTx to set
     */
    public void setArticuloSatTx(String articuloSatTx) {
        this.articuloSatTx = articuloSatTx;
    }

    public void traerArticulosItemsLstSat(String cadena) {
        setSatArticulosResultadoBqda(soporteCategorias.obtenerArticulosSat(cadena));
    }

    public boolean guardarArticuloSat(int artSatID) {
        boolean ret = true;
        try {
            if (this.getListaCampoPorArticulo() != null && !this.getListaCampoPorArticulo().isEmpty()) {
                for (CampoVo vo : this.getListaCampoPorArticulo()) {
                    if (Constantes.PAIS_MEXICO == vo.getIdCompaniaPais()) {
                        invArticuloCampoImpl.gardarArticuloSat(vo.getIdRelacion(), artSatID, sesion.getUsuario().getId());
                    }
                }
                setSatArticulosResultadoBqda(soporteCategorias.obtenerArticulosSat(null));
                setArticuloSatTx("");
                llenarDatosCambiarArticulo();
            }
        } catch (Exception e) {
            LOGGER.error(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
            ret = false;
        }

        return ret;
    }

    /**
     * @return the idBloque
     */
    public int getIdBloque() {
        return idBloque;
    }

    /**
     * @param idBloque the idBloque to set
     */
    public void setIdBloque(int idBloque) {
        this.idBloque = idBloque;
    }
}
