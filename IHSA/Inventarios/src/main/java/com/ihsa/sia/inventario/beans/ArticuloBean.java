package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.SaveObservable;
import com.ihsa.sia.commons.SaveObserver;
import com.ihsa.sia.inventario.beans.catalogo.unidad.UnidadBean;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import sia.excepciones.SIAException;
import sia.inventarios.service.ArticuloRemote;
import sia.inventarios.service.UnidadImpl;
import sia.inventarios.service.UnidadRemote;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.UnidadVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.sistema.impl.SiCategoriaImpl;
import sia.servicios.sistema.vo.MonedaVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "articulo")
@ViewScoped
public class ArticuloBean extends LocalAbstractBean<ArticuloVO, Integer> implements Serializable, SaveObserver {

    @Inject
    private ArticuloRemote servicio;
    @Inject
    private UnidadRemote unidadServicio;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoRemote;
    @Inject
    private SiCategoriaImpl categoriaServicio;
    @Inject
    MonedaImpl monedaRemote;

    @Inject
    private UnidadBean unidadBean;

    private boolean embedded;
    private List<UnidadVO> unidades;
    private List<MonedaVO> monedas;
    private List<CampoUsuarioPuestoVo> campos;
    private RolesBean roles = new RolesBean();
    private List<CategoriaVo> categoriasPrincipales;
    private String codigosPrincipales;

    public ArticuloBean() {
        super(ArticuloVO.class);
    }

    @Override
    protected void init() {
        super.init();
        super.getFiltro().setCampoId(roles.getUsuario().getIdCampo());
        unidadBean.addObserver(this);
        cargarCampos();
        cargarUnidades();
        cargarMonedas();
        cargarCategoriasPrincipales();
    }

    private void cargarCategoriasPrincipales() {
        try {
            categoriasPrincipales = categoriaServicio.traerCategoriaPrincipales();
            StringBuilder sb = new StringBuilder();
            for (CategoriaVo categoria : categoriasPrincipales) {
                sb.append(categoria.getCodigo()).append("-");
            }
            codigosPrincipales = sb.toString();
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    public void nuevaUnidad() {
        unidadBean.agregarNuevo();
        getUnidadBean().setEmbedded(true);
    }

    public void cancelar() {
        setEmbedded(false);
    }

    public void validarCodigoUnico(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (getEsNuevoElemento()) {
            String codigo = (String) value;
            if (servicio.existeArticuloConCodigo(codigo, getCampoId())) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        obtenerCadenaDeRecurso("sia.inventarios.articulos.codigoUnicoMensaje"), null));
            }
        }
    }

    private void cargarUnidades() {
        try {
            unidades = unidadServicio.buscarPorFiltros(new UnidadVO(), getCampoId());
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    private void cargarMonedas() {
        try {
            monedas = monedaRemote.traerMonedaActiva(getCampoId());
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    private void cargarCampos() {
        try {
            if (campos == null) {
                campos = apCampoUsuarioRhPuestoRemote.getCampoPorUsurio(roles.getUsuario().getId(), roles.getUsuario().getIdCampo());
            }
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    @Override
    protected ArticuloRemote getServicio() {
        return servicio;
    }

    @Override
    public void cargarElementParaEditar(Integer id) {
        getUnidadBean().setEmbedded(false);
        try {
            setElemento(servicio.buscar(id, getCampoId()));
        } catch (SIAException ex) {
            Logger.getLogger(ArticuloBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void agregarNuevo() {
        getUnidadBean().setEmbedded(false);
        super.agregarNuevo();
    }

    @Override
    protected String mensajeCrearKey() {
        return "sia.inventarios.articulos.crearMensaje";
    }

    @Override
    protected String mensajeEditarKey() {
        return "sia.inventarios.articulos.editarMensaje";
    }

    @Override
    protected String mensajeEliminarKey() {
        return "sia.inventarios.articulos.eliminarMensaje";
    }

    public List<UnidadVO> getUnidades() {
        return unidades;
    }

    public UnidadBean getUnidadBean() {
        return unidadBean;
    }

    public void setUnidadBean(UnidadBean unidadBean) {
        this.unidadBean = unidadBean;
    }

    @Override
    public void update(SaveObservable observable, String event) {
        getUnidadBean().setEmbedded(false);
        cargarCampos();
        cargarUnidades();
        getElemento().setUnidadId(getUnidadBean().getElemento().getId());
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    /**
     * @return the campos
     */
    public List<CampoUsuarioPuestoVo> getCampos() {
        return campos;
    }

    public List<CategoriaVo> getCategoriasPrincipales() {
        return categoriasPrincipales;
    }

    public String getCodigosPrincipales() {
        return codigosPrincipales;
    }

    /**
     * @return the monedas
     */
    public List<MonedaVO> getMonedas() {
        return monedas;
    }

    /**
     * @param monedas the monedas to set
     */
    public void setMonedas(List<MonedaVO> monedas) {
        this.monedas = monedas;
    }
}
