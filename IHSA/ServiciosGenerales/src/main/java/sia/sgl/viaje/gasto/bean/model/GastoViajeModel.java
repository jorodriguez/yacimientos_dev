/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.gasto.bean.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.modelo.Moneda;
import sia.modelo.sgl.viaje.vo.ViajeFacturaVo;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.vo.FacturaVo;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.viaje.impl.SgViajeFacturaImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.ProveedorEnum;

/**
 *
 * @author mluis
 */
@Named(value = "gastoViajeModel")
@ViewScoped
public class GastoViajeModel implements Serializable {

    //Sistema
    @Inject
    private Sesion sesion;
    //
    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private SgViajeroImpl sgViajeroImpl;
    @Inject
    private SgViajeFacturaImpl sgViajeFacturaImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private SiFacturaImpl siFacturaImpl;
    //
    private String inicio;
    private String fin;
    private List lista;
    private List listaFactura;
    private List<SelectItem> listaItem;
    private FacturaVo facturaVo;
    private ViajeVO viajeVO;
    private int id;
    private int idviajeFactura;
    private AdjuntoVO adjuntoVO;
    private String dir = "";

    @PostConstruct
    public void iniciar() {
	setInicio(siManejoFechaImpl.traerStringInicioMesddMMyyyy());
	setFin(siManejoFechaImpl.convertirFechaStringddMMyyyy(new Date()));
	lista = new ArrayList();
	listaMoneda();
	buscarViajes();
    }

    private void listaMoneda() {
	List<Moneda> lm = monedaImpl.findAll();
	setListaItem(new ArrayList<SelectItem>());
	for (Moneda moneda : lm) {
	    getListaItem().add(new SelectItem(moneda.getId(), moneda.getNombre()));
	}
    }

    //
    public void buscarViajes() {
	lista = new ArrayList<ViajeVO>();
	lista = sgViajeImpl.traerViajesAereosPorFechas(getInicio(), getFin());
    }

    public void llenarViajePorId() {
	viajeVO = sgViajeImpl.buscarPorId(getId(), false);
    }

    public void buscarFacturas() {
	listaFactura = new ArrayList<ViajeFacturaVo>();
	listaFactura = sgViajeFacturaImpl.traerFacturaPorViaje(getViajeVO().getId());
    }

    public void buscarViajeros() {
	List<ViajeFacturaVo> lFactura = sgViajeFacturaImpl.traerFacturaPorViaje(getViajeVO().getId());
	if (lFactura.isEmpty()) {
	    List<ViajeroVO> listaViajero = sgViajeroImpl.getTravellersByTravel(getViajeVO().getId(), null);
	    for (ViajeroVO viajeroVO : listaViajero) {
		sgViajeFacturaImpl.guardarViajeViajero(getViajeVO().getId(), viajeroVO.getId(), sesion.getUsuario().getId());
	    }
	}
    }

    public String llenarProveedor() {
	return proveedorImpl.getProveedorJson(sesion.getUsuario().getApCampo().getCompania().getRfc(), ProveedorEnum.ACTIVO.getId());
    }

    public boolean registrarFactura() {
	return sgViajeFacturaImpl.guardarFactura(getIdviajeFactura(), getFacturaVo(), sesion.getUsuario().getId());
    }

    public void agregarArchivoFactura() {
	int siAdjunto = siAdjuntoImpl.saveSiAdjunto(getAdjuntoVO().getNombre(), getAdjuntoVO().getTipoArchivo(), getAdjuntoVO().getUrl(), getAdjuntoVO().getTamanio(), sesion.getUsuario().getId());
	//
	siFacturaImpl.agregarArchivo(getId(), siAdjunto, sesion.getUsuario().getId());
	buscarFacturas();
    }

    public void quitarArchivo() {
	//Se eliminan fisicamente los archivos
	String path = Constantes.RUTA_LOCAL_FILES + getAdjuntoVO().getUrl();
	File file = new File(path);
	if (file.delete()) {
	    siAdjuntoImpl.delete(getAdjuntoVO().getId(), sesion.getUsuario().getId());
	}
	siFacturaImpl.quitarArchivo(getId(), sesion.getUsuario().getId());
    }

    public FacturaVo busarFacturaPorId() {
	return siFacturaImpl.buscarFactura(getId());
    }

    public void modificarFactura() {
	siFacturaImpl.modificarFactura(getFacturaVo(), sesion.getUsuario().getId());
    }

    public void eliminarFactura() {
	siFacturaImpl.eliminarFactura(getId(), Constantes.CERO, sesion.getUsuario().getId());
    }

    public void eliminarViajeFactura() {
	sgViajeFacturaImpl.eliminarViajeFactura(getIdviajeFactura(), sesion.getUsuario().getId());
    }

    /**
     * Creates a new instance of GastoViajeModel
     */
    public GastoViajeModel() {
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the inicio
     */
    public String getInicio() {
	return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(String inicio) {
	this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public String getFin() {
	return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(String fin) {
	this.fin = fin;
    }

    /**
     * @return the lista
     */
    public List getLista() {
	return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List lista) {
	this.lista = lista;
    }

    /**
     * @return the facturaVo
     */
    public FacturaVo getFacturaVo() {
	return facturaVo;
    }

    /**
     * @param facturaVo the facturaVo to set
     */
    public void setFacturaVo(FacturaVo facturaVo) {
	this.facturaVo = facturaVo;
    }

    /**
     * @return the id
     */
    public int getId() {
	return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
	this.id = id;
    }

    /**
     * @return the viajeVO
     */
    public ViajeVO getViajeVO() {
	return viajeVO;
    }

    /**
     * @param viajeVO the viajeVO to set
     */
    public void setViajeVO(ViajeVO viajeVO) {
	this.viajeVO = viajeVO;
    }

    /**
     * @return the listaItem
     */
    public List<SelectItem> getListaItem() {
	return listaItem;
    }

    /**
     * @param listaItem the listaItem to set
     */
    public void setListaItem(List<SelectItem> listaItem) {
	this.listaItem = listaItem;
    }

    /**
     * @return the adjuntoVO
     */
    public AdjuntoVO getAdjuntoVO() {
	return adjuntoVO;
    }

    /**
     * @param adjuntoVO the adjuntoVO to set
     */
    public void setAdjuntoVO(AdjuntoVO adjuntoVO) {
	this.adjuntoVO = adjuntoVO;
    }

    /**
     * @return the listaFactura
     */
    public List getListaFactura() {
	return listaFactura;
    }

    /**
     * @param listaFactura the listaFactura to set
     */
    public void setListaFactura(List listaFactura) {
	this.listaFactura = listaFactura;
    }

    /**
     * @return the dir
     */
    public String getDir() {
	return dir;
    }

    /**
     * @param dir the dir to set
     */
    public void setDir(String dir) {
	this.dir = dir;
    }

    /**
     * @return the idviajeFactura
     */
    public int getIdviajeFactura() {
	return idviajeFactura;
    }

    /**
     * @param idviajeFactura the idviajeFactura to set
     */
    public void setIdviajeFactura(int idviajeFactura) {
	this.idviajeFactura = idviajeFactura;
    }
}
