package com.ihsa.sia.inventario.beans.inventario;

import com.ihsa.sia.commons.AbstractBean;
import com.ihsa.sia.commons.Messages;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import sia.inventarios.service.AlmacenImpl;
import sia.inventarios.service.InventarioImpl;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.InventarioVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "revision")
@ViewScoped
public class RevisionBean extends AbstractBean implements Serializable {

    @Inject
    private AlmacenImpl servicioAlmacen;
    @Inject
    private InventarioImpl servicioInventario;

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
	almacenes = servicioAlmacen.buscarPorFiltros(new AlmacenVO(), getCampoId());
	articulo = new ArticuloVO();
    }

    public void buscarInventario() {
	try {
	    inventario = servicioAlmacen.buscarInventario(getAlmacenId(), getArticulo().getId(), getCampoId());
	    if (inventario != null) {
		notas = Messages.getString("sia.inventarios.revision.notas");
		setUnidadesReales(null);
	    } else {
		addInfoMessage(Messages.getString("sia.inventarios.revision.noEncontrado"));
	    }
	} catch (Exception ex) {
	    ManejarExcepcion(ex);
	}
    }

    public void conciliar() {
	try {
	    servicioInventario.conciliar(inventario.getId(), getUnidadesReales(), getNotas(), getUserName(), getCampoId());
	    addInfoMessage(Messages.getString("sia.inventarios.revision.guardarMensaje"));
	    inventario = null;
	    articulo = new ArticuloVO();
	} catch (Exception ex) {
	    ManejarExcepcion(ex);
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
