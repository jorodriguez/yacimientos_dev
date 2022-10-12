package com.ihsa.sia.inventario.beans.reportes;

import com.ihsa.sia.commons.Messages;
import com.ihsa.sia.inventario.beans.inventario.MovimientosBean;
import java.text.MessageFormat;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.TransaccionVO;
import sia.servicios.catalogos.impl.UsuarioImpl;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "historialMovimientos")
@ViewScoped
public class HistorialMovimientosBean extends MovimientosBean {

    @Inject
    private UsuarioImpl servicioUsuario;

    private List<UsuarioVO> usuarios;
    private List<TransaccionVO> listaImprimir;
    private int tamanioPagina;
    private String mensaje;

    @Override
    @PostConstruct
    public void init() {
	try {
	    usuarios = servicioUsuario.obtenerListaUsuarios();
	    tamanioPagina = 50;
	} catch (Exception ex) {
	    ManejarExcepcion(ex);
	}
	super.init();
    }

    @Override
    protected void cargarListaConFiltros() {
	try {
	    listaImprimir = getServicio().buscarPorFiltros(getFiltro(), 0, tamanioPagina, null, true, principal.getUser().getIdCampo());
	    contarFilas(principal.getUser().getIdCampo());
	    if (getFilasTotales() > listaImprimir.size()) {
		mensaje = MessageFormat.format(obtenerCadenaDeRecurso("sia.inventarios.movimientos.mensajePaginacion"), tamanioPagina);
	    }
	} catch (Exception ex) {
	    ManejarExcepcion(ex);
	}
    }

    public void fechaValidador(FacesContext context, UIComponent component, Object value) {
	if (getFiltro().getFechaInicio() == null && getFiltro().getFechaFin() == null) {
	    return;
	}
	if (getFiltro().getFechaInicio() == null) {
	    lanzarValidacionExcepcion();
	}
	if (getFiltro().getFechaFin() == null) {
	    lanzarValidacionExcepcion();
	}
	//Si la fecha inicio es mayor a la fecha fin
	if (getFiltro().getFechaInicio().compareTo(getFiltro().getFechaFin()) > 0) {
	    lanzarValidacionExcepcion();
	}
    }

    public void lanzarValidacionExcepcion() throws ValidatorException {
	throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		Messages.getString("sia.inventarios.reportes.rango.fechaValidacion"), null));
    }

    public List<UsuarioVO> getUsuarios() {
	return usuarios;
    }

    public List<TransaccionVO> getListaImprimir() {
	return listaImprimir;
    }

    public String getMensaje() {
	return mensaje;
    }
}
