/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.incidencia.vehiculo.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Moneda;
import sia.modelo.Prioridad;
import sia.modelo.SiAdjunto;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.vehiculo.vo.VehiculoIncidenciaVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.PrioridadImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaAdjuntoImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaFacturaImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgAsignarVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "incidenciaVehiculoModel")

public class IncidenciaVehiculoModel implements Serializable {

    /**
     * Creates a new instance of IncidenciaVehiculoModel
     */
    public IncidenciaVehiculoModel() {
    }
    private int priridad;
    private int gerencia;
    private int vehiculo;
    private int idIncidencia;
    private int idAdjunto;
    private String titulo;
    private String descripcion;
    private String palabraClave;
    private List<VehiculoIncidenciaVo> listaIncidencia;
    private IncidenciaVo incidenciaVo;
    private VehiculoVO vehiculoVO;
    private UsuarioVO usuarioVO;
    private List<AdjuntoVO> listaAdjuntoVO;
    private List<FacturaVo> listaFactura;
    private List<SelectItem> listaItem;
    private FacturaVo facturaVo;
    private boolean evidencia = false;
    //Sistema
    @Inject
    private Sesion sesion;
    @Inject
    private PrioridadImpl prioridadImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private SgVehiculoImpl sgVehiculoImpl;
    @Inject
    private SiIncidenciaVehiculoImpl siIncidenciaVehiculoImpl;
    @Inject
    private SiIncidenciaAdjuntoImpl siIncidenciaAdjuntoImpl;
    @Inject
    private SiIncidenciaFacturaImpl siIncidenciaFacturaImpl;
    @Inject
    private SiIncidenciaImpl siIncidenciaImpl;
    @Inject
    private SgAsignarVehiculoImpl sgAsignarVehiculoImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SiFacturaImpl siFacturaImpl;

    @PostConstruct
    public void iniciar() {
	listaIncidencia = siIncidenciaVehiculoImpl.traerIncidenciaVehiculo(sesion.getUsuario().getId(), Constantes.ESTATUS_APROBADA);
	listaMoneda();
	facturaVo = new FacturaVo();
    }

    public List<SelectItem> listaPrioridad() {
	List<Prioridad> lp = prioridadImpl.findAll();
	List<SelectItem> li = new ArrayList<SelectItem>();
	for (Prioridad prioridad : lp) {
	    li.add(new SelectItem(prioridad.getId(), prioridad.getNombre()));
	}
	return li;
    }

    public List<SelectItem> listaGerencia() {
	List<GerenciaVo> lg = gerenciaImpl.traerGerenciaPorCompaniaCampo(Constantes.RFC_IHSA, Constantes.AP_CAMPO_DEFAULT, Constantes.NO_ELIMINADO);
	List<SelectItem> li = new ArrayList<SelectItem>();
	for (GerenciaVo ger : lg) {
	    li.add(new SelectItem(ger.getId(), ger.getNombre()));
	}
	return li;
    }

    public List<SelectItem> listaVehiculo() {
	List<VehiculoVO> lg = sgVehiculoImpl.traerVehiculoPorOficina(sesion.getOficinaActual().getId(), Constantes.NO_ELIMINADO);
	List<SelectItem> li = new ArrayList<SelectItem>();
	for (VehiculoVO veh : lg) {
	    li.add(new SelectItem(veh.getId(), veh.getMarca() + " -- " + veh.getModelo()));
	}
	return li;
    }

    public void guardar() throws Exception {
	try {
	    siIncidenciaVehiculoImpl.guardar(getVehiculo(), getTitulo(), getDescripcion(), getPriridad(), getGerencia(), Constantes.ESTATUS_APROBADA, getPalabraClave(), sesion.getUsuario().getId());
	    listaIncidencia = siIncidenciaVehiculoImpl.traerIncidenciaVehiculo(sesion.getUsuario().getId(), Constantes.ESTATUS_APROBADA);
	} catch (Exception e) {
	    throw e;
	}
    }

    public void administrarIncidencia() {
	setIncidenciaVo(siIncidenciaImpl.buscarPorId(getIdIncidencia()));
	setVehiculoVO(sgVehiculoImpl.buscarVehiculoPorId(getVehiculo()));
	setUsuarioVO(sgAsignarVehiculoImpl.traerResponsableVehiculo(getVehiculo()));
	setListaAdjuntoVO(siIncidenciaAdjuntoImpl.traerArchivoPorIncidencia(getIdIncidencia()));
	setListaFactura(siIncidenciaFacturaImpl.traerFacturaPorIncidencia(getIdIncidencia()));
    }

    public String directorio() {
        return Constantes.RUTA_INCIDENCIA_VEHICULO + getIncidenciaVo().getIdIncidencia() + "/";
    }

    public boolean guardarArchivo(String fileName, String ruta, String contentType, long size) {
        boolean v = true;
        try {
            SiAdjunto siAdjunto = 
                    siAdjuntoImpl.save(
                            fileName, 
                            ruta + fileName, 
                            contentType, 
                            size, 
                            sesion.getUsuario().getId()
                    );
            if (siAdjunto != null) {
                v = siIncidenciaAdjuntoImpl.agregarArchivoIncidencia(getIdIncidencia(), sesion.getUsuario().getId(), siAdjunto.getId());
                listaAdjuntoVO = siIncidenciaAdjuntoImpl.traerArchivoPorIncidencia(getIdIncidencia());
            }
        } catch (SIAException ex) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al guardar el archivo . . . . . " + ex.getMessage());
            v = false;
        }
        return v;
    }

    public void eliminarArchivo(int idInciAdj) {
	//Se eliminan fisicamente los archivos
	String path = this.siParametroImpl.find(1).getUploadDirectory();
	try {
	    SiAdjunto siAdjunto = siAdjuntoImpl.find(getIdAdjunto());
	    File file = new File(path + siAdjunto.getUrl());
	    if (file.delete()) {
		//
		siAdjuntoImpl.eliminarArchivo(getIdAdjunto(), sesion.getUsuario().getId());
		//Elmina la relacion entre la incidencia y los adjuntos
		siIncidenciaAdjuntoImpl.eliminarRelacion(idInciAdj, sesion.getUsuario().getId());
		//llena la tabla
		listaAdjuntoVO = siIncidenciaAdjuntoImpl.traerArchivoPorIncidencia(getIdIncidencia());

	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "No se eliminoó el archivo  + + + + + " + e.getMessage());
	}
    }

    public void listaMoneda() {
	List<Moneda> lm = monedaImpl.findAll();
	setListaItem(new ArrayList<SelectItem>());
	for (Moneda moneda : lm) {
	    getListaItem().add(new SelectItem(moneda.getId(), moneda.getNombre()));
	}
    }

    public String llenarProveedor() {
	return proveedorImpl.getProveedorJson(sesion.getUsuario().getApCampo().getCompania().getRfc(), ProveedorEnum.ACTIVO.getId());
    }

    public boolean registrarFactura() {
	return siIncidenciaFacturaImpl.guardar(getIdIncidencia(), getFacturaVo(), sesion.getUsuario().getId());
    }

    public void buscarFacturas() {
	listaFactura = new ArrayList<FacturaVo>();
	listaFactura = siIncidenciaFacturaImpl.traerFacturaPorIncidencia(getIdIncidencia());
    }

    public boolean guardarFactura(String fileName, String ruta, String contentType, long size) {
        boolean v = true;
        try {
            SiAdjunto siAdjunto = 
                    siAdjuntoImpl.save(
                            fileName, 
                            ruta + fileName, 
                            contentType, 
                            size, 
                            sesion.getUsuario().getId()
                    );
            
            if (siAdjunto != null) {
                siFacturaImpl.agregarArchivo(getFacturaVo().getIdFactura(), siAdjunto.getId(), sesion.getUsuario().getId());
                listaFactura = siIncidenciaFacturaImpl.traerFacturaPorIncidencia(getIdIncidencia());
            }
        } catch (SIAException ex) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al guardar el archivo . . . . . " + ex.getMessage());
            v = false;
        }
        return v;
    }

    public String directorioFactura() {
            return Constantes.RUTA_INCIDENCIA_FACTURA + getIdIncidencia()+ "/";
    }

    public void quitarArchivo() {
	//Se eliminan fisicamente los archivos
	AdjuntoVO adjuntoVO = siAdjuntoImpl.buscarArchivo(getFacturaVo().getIdAdjunto());
	if (adjuntoVO != null) {
	    String path = Constantes.RUTA_LOCAL_FILES + adjuntoVO.getUrl();
	    File file = new File(path);
	    file.delete();
	    siAdjuntoImpl.delete(adjuntoVO.getId(), sesion.getUsuario().getId());
	}
	siFacturaImpl.quitarArchivo(getFacturaVo().getIdFactura(), sesion.getUsuario().getId());
    }

    public FacturaVo llenarFactura() {
	return siFacturaImpl.buscarFactura(getFacturaVo().getIdFactura());
    }

    public void modificarFactura() {
	siFacturaImpl.modificarFactura(getFacturaVo(), sesion.getUsuario().getId());
    }

    public void eliminarFactura() {
	siIncidenciaFacturaImpl.eliminarFactura(getFacturaVo().getIdRelacion(), sesion.getUsuario().getId());
    }

    /**
     * @return the priridad
     */
    public int getPriridad() {
	return priridad;
    }

    /**
     * @param priridad the priridad to set
     */
    public void setPriridad(int priridad) {
	this.priridad = priridad;
    }

    /**
     * @return the gerencia
     */
    public int getGerencia() {
	return gerencia;
    }

    /**
     * @param gerencia the gerencia to set
     */
    public void setGerencia(int gerencia) {
	this.gerencia = gerencia;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
	return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
	this.titulo = titulo;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
	return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
    }

    /**
     * @return the palabraClave
     */
    public String getPalabraClave() {
	return palabraClave;
    }

    /**
     * @param palabraClave the palabraClave to set
     */
    public void setPalabraClave(String palabraClave) {
	this.palabraClave = palabraClave;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the vehiculo
     */
    public int getVehiculo() {
	return vehiculo;
    }

    /**
     * @param vehiculo the vehiculo to set
     */
    public void setVehiculo(int vehiculo) {
	this.vehiculo = vehiculo;
    }

    /**
     * @return the listaIncidencia
     */
    public List<VehiculoIncidenciaVo> getListaIncidencia() {
	return listaIncidencia;
    }

    /**
     * @param listaIncidencia the listaIncidencia to set
     */
    public void setListaIncidencia(List<VehiculoIncidenciaVo> listaIncidencia) {
	this.listaIncidencia = listaIncidencia;
    }

    /**
     * @return the incidenciaVo
     */
    public IncidenciaVo getIncidenciaVo() {
	return incidenciaVo;
    }

    /**
     * @param incidenciaVo the incidenciaVo to set
     */
    public void setIncidenciaVo(IncidenciaVo incidenciaVo) {
	this.incidenciaVo = incidenciaVo;
    }

    /**
     * @return the vehiculoVO
     */
    public VehiculoVO getVehiculoVO() {
	return vehiculoVO;
    }

    /**
     * @param vehiculoVO the vehiculoVO to set
     */
    public void setVehiculoVO(VehiculoVO vehiculoVO) {
	this.vehiculoVO = vehiculoVO;
    }

    /**
     * @return the usuarioVO
     */
    public UsuarioVO getUsuarioVO() {
	return usuarioVO;
    }

    /**
     * @param usuarioVO the usuarioVO to set
     */
    public void setUsuarioVO(UsuarioVO usuarioVO) {
	this.usuarioVO = usuarioVO;
    }

    /**
     * @return the listaAdjuntoVO
     */
    public List<AdjuntoVO> getListaAdjuntoVO() {
	return listaAdjuntoVO;
    }

    /**
     * @param listaAdjuntoVO the listaAdjuntoVO to set
     */
    public void setListaAdjuntoVO(List<AdjuntoVO> listaAdjuntoVO) {
	this.listaAdjuntoVO = listaAdjuntoVO;
    }

    /**
     * @return the listaFactura
     */
    public List<FacturaVo> getListaFactura() {
	return listaFactura;
    }

    /**
     * @param listaFactura the listaFactura to set
     */
    public void setListaFactura(List<FacturaVo> listaFactura) {
	this.listaFactura = listaFactura;
    }

    /**
     * @return the idIncidencia
     */
    public int getIdIncidencia() {
	return idIncidencia;
    }

    /**
     * @param idIncidencia the idIncidencia to set
     */
    public void setIdIncidencia(int idIncidencia) {
	this.idIncidencia = idIncidencia;
    }

    /**
     * @return the idAdjunto
     */
    public int getIdAdjunto() {
	return idAdjunto;
    }

    /**
     * @param idAdjunto the idAdjunto to set
     */
    public void setIdAdjunto(int idAdjunto) {
	this.idAdjunto = idAdjunto;
    }

    /**
     * @return the FacturaVo
     */
    public FacturaVo getFacturaVo() {
	return facturaVo;
    }

    /**
     * @param facturaVo the FacturaVo to set
     */
    public void setFacturaVo(FacturaVo facturaVo) {
	this.facturaVo = facturaVo;
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
     * @return the evidencia
     */
    public boolean isEvidencia() {
	return evidencia;
    }

    /**
     * @param evidencia the evidencia to set
     */
    public void setEvidencia(boolean evidencia) {
	this.evidencia = evidencia;
    }
}
