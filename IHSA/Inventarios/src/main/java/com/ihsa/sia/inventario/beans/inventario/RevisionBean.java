package com.ihsa.sia.inventario.beans.inventario;

import com.ihsa.sia.commons.Messages;
import com.ihsa.sia.commons.SessionBean;
import com.ihsa.sia.inventario.beans.FacesUtilsBean;
import java.io.Serializable;
import java.util.List;
import java.util.MissingResourceException;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import sia.inventarios.service.AlmacenRemote;
import sia.inventarios.service.ArticuloRemote;
import sia.inventarios.service.InventarioImpl;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.util.UtilLog4j;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "revision")
@ViewScoped
public class RevisionBean implements Serializable {

    @Inject
    private AlmacenRemote servicioAlmacen;
    @Inject
    private InventarioImpl servicioInventario;
    @Inject
    private ArticuloRemote articuloImpl;
    @Inject
    SessionBean principal;

    private List<AlmacenVO> almacenes;
    private Integer almacenId;
    private ArticuloVO articulo;
    private InventarioVO inventario;
    private Double unidadesReales;
    private String notas;

    public RevisionBean() {
    }

    @PostConstruct
    public void init() {
        almacenes = servicioAlmacen.buscarPorFiltros(new AlmacenVO(), principal.getUser().getIdCampo());
        articulo = new ArticuloVO();
        inventario = new InventarioVO();
    }

    public List<ArticuloVO> completarArticulo(String cadena) {
        return articuloImpl.buscarPorPalabras(cadena, principal.getUser().getCampo());
    }

    public void buscarInventario() {
        try {
            //   event.getObject();
            inventario = servicioInventario.invetarioPorArticulo(getArticulo().getId(), getAlmacenId());
            if (inventario != null) {
                notas = Messages.getString("sia.inventarios.revision.notas");
                setUnidadesReales(null);
            } else {
                FacesUtilsBean.addInfoMessage(Messages.getString("sia.inventarios.revision.noEncontrado"));
            }
        } catch (MissingResourceException ex) {
            FacesUtilsBean.addInfoMessage(Messages.getString("sia.inventarios.revision.noEncontrado"));
            UtilLog4j.log.error(ex);
        }
    }

    public void conciliar() {
        try {
            servicioInventario.conciliar(inventario.getId(), getUnidadesReales(), getNotas(), principal.getUser().getId(), principal.getUser().getIdCampo());
            FacesUtilsBean.addInfoMessage(Messages.getString("sia.inventarios.revision.guardarMensaje"));
            inventario = null;
            articulo = new ArticuloVO();
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    public void validarUnidades(FacesContext context, UIComponent contol, Object value) {
        double unidades = 0;

        try {
            unidades = Double.valueOf(value.toString());
        } catch (NumberFormatException ex) {
            throw new ValidatorException(construirMensajeError("sia.inventarios.revision.validacionNumero"));
        } catch (NullPointerException ex) {
            throw new ValidatorException(construirMensajeError("sia.inventarios.revision.validacionNumero"));
        }

        if (unidades < 0) {
            throw new ValidatorException(construirMensajeError("sia.inventarios.revision.validacionNumero"));
        }
        if (unidades == inventario.getNumeroUnidades()) {
            throw new ValidatorException(construirMensajeError("sia.inventarios.revision.validacionCantidad"));
        }
    }

    private FacesMessage construirMensajeError(String key) {
        String mensaje = Messages.getString(key);
        return new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, mensaje);
    }

    public List<AlmacenVO> getAlmacenes() {
        return almacenes;
    }

    public Integer getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(Integer almacenId) {
        this.almacenId = almacenId;
    }

    public ArticuloVO getArticulo() {
        return articulo;
    }

    public void setArticulo(ArticuloVO articulo) {
        this.articulo = articulo;
    }

    public InventarioVO getInventario() {
        return inventario;
    }

    public void setInventario(InventarioVO inventario) {
        this.inventario = inventario;
    }

    public Double getUnidadesReales() {
        return this.unidadesReales;
    }

    public void setUnidadesReales(Double unidadesReales) {
        this.unidadesReales = unidadesReales;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

}
