/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;


import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named
@ViewScoped
public class BuscarBean implements Serializable {

    /**
     * Creates a new instance of BuscarBean
     */
    public BuscarBean() {
    }
    //
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private MonedaImpl monedaImpl;
    //
    @Inject
    private UsuarioBean usuarioBean;
    //
    private int idGerencia;
    private int idProveedor;
    private int idMoneda;
    private int rango;
    private String tipo;
    private List<SelectItem> listaMonto;
    private List<SelectItem> listaGerencia;
    private List<SelectItem> listaTipo;
    private List listaOrden;
    //
    private int minimo;
    private double maximo;
    //
    private String agregarFecha = "Si";
    private LocalDate fechaInicio;
    private LocalDate fechaFin ;

    public void limpiarValor() {
	setIdGerencia(-1);
	setIdProveedor(0);
    }
    
    public List<SelectItem> getListaMoneda() {
        List<SelectItem> item = new ArrayList<>();
        for (MonedaVO mo : monedaImpl.traerMonedaActiva(usuarioBean.getUsuarioConectado().getApCampo().getId())) {
            SelectItem i = new SelectItem(mo.getId(), mo.getNombre());
            item.add(i);
        }
        return item;
    }

    @PostConstruct
    public void llenarDatos() {
	listaMonto = new ArrayList<>();
	listaMonto.add(new SelectItem(1, "$0.00 a $5,000"));
	listaMonto.add(new SelectItem(2, "$5,001 a $10,000"));
	listaMonto.add(new SelectItem(3, "$10,001 a $20,000"));
	listaMonto.add(new SelectItem(4, "Mayor a $20,000"));
	listaMonto.add(new SelectItem(5, "Sin Filtro"));
	//
	setRango(5);
	//
	setIdMoneda(usuarioBean.getUsuarioConectado().getApCampo().getCompania().getMoneda().getId());
	//Lista de gerecias
	List<GerenciaVo> lg = gerenciaImpl.traerGerenciaActiva(usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc(), usuarioBean.getUsuarioConectado().getApCampo().getId());
	listaGerencia = new ArrayList<>();
	for (GerenciaVo gerenciaVo : lg) {
	    listaGerencia.add(new SelectItem(gerenciaVo.getId(), gerenciaVo.getNombre()));
	}
	//Proveedores
	String proveedores = proveedorImpl.getProveedorJson(usuarioBean.getCompania().getRfc(), ProveedorEnum.ACTIVO.getId());
	PrimeFaces.current().executeScript(";setJson(" + proveedores + ");");
	//Tipo
	try {
	    listaTipo = new ArrayList<>();
	    getListaTipo().add(new SelectItem(TipoRequisicion.PS.name(), "Productos/Servicios"));
	    getListaTipo().add(new SelectItem(TipoRequisicion.AF.name(), "Activo Fijo"));
	    getListaTipo().add(new SelectItem(TipoRequisicion.AI.name(), "Ninguna de las anteriores"));
	    getListaTipo().add(new SelectItem(Constantes.BOOLEAN_FALSE, "Sin Clasificar"));
	    setTipo(TipoRequisicion.PS.name());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	}
        fechaFin = LocalDate.now();
        fechaInicio = fechaFin.minusDays(30);
    }

    public void buscarOCS() {
	castRango();
	listaOrden = ordenImpl.buscarOCS(
                getIdGerencia(), 
                Constantes.CERO, 
                getIdMoneda(), 
                minimo, 
                maximo, 
                getTipo(), 
                usuarioBean.getUsuarioConectado().getApCampo().getId(), 
                getRango(), 
                Constantes.PALABRA_SI.equals(getAgregarFecha()), 
                getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }

    private void castRango() {
	switch (getRango()) {
	case 1:
	    minimo = 0;
	    maximo = 5000;
	    break;
	case 2:
	    minimo = 5001;
	    maximo = 10000;
	    break;
	case 3:
	    minimo = 10001;
	    maximo = 20000;
	    break;
	case 4:
	    minimo = 20001;
	    maximo = ordenImpl.traerTotalMaximoOCS(getIdMoneda());
	    break;
	default:
	    break;
	}
    }

    public List<SelectItem> getListaMonto() {
	return listaMonto;
    }

    public List<SelectItem> getListaGerencia() {
	return listaGerencia;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
	return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
	this.idGerencia = idGerencia;
    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
	return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
	this.idProveedor = idProveedor;
    }

    /**
     * @return the idMoneda
     */
    public int getIdMoneda() {
	return idMoneda;
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdMoneda(int idMoneda) {
	this.idMoneda = idMoneda;
    }

    /**
     * @return the rango
     */
    public int getRango() {
	return rango;
    }

    /**
     * @param rango the rango to set
     */
    public void setRango(int rango) {
	this.rango = rango;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
	return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
	this.tipo = tipo;
    }

    /**
     * @return the listaOrden
     */
    public List getListaOrden() {
	return listaOrden;
    }

    /**
     * @return the listaTipo
     */
    public List<SelectItem> getListaTipo() {
	return listaTipo;
    }

    /**
     * @return the agregarFecha
     */
    public String getAgregarFecha() {
	return agregarFecha;
    }

    /**
     * @param agregarFecha the agregarFecha to set
     */
    public void setAgregarFecha(String agregarFecha) {
	this.agregarFecha = agregarFecha;
    }

    /**
     * @return the fechaInicio
     */
    public LocalDate getFechaInicio() {
	return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(LocalDate fechaInicio) {
	this.fechaInicio = fechaInicio;
    }

    /**
     * @return the fechaFin
     */
    public LocalDate getFechaFin() {
	return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(LocalDate fechaFin) {
	this.fechaFin = fechaFin;
    }
}
