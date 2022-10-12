/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.mantenimiento.bean.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Proveedor;
import sia.modelo.SgEstadoVehiculo;
import sia.modelo.SgKilometraje;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.SgVehiculoMantenimiento;
import sia.modelo.SiAdjunto;
import sia.modelo.sgl.vehiculo.vo.SgKilometrajeVo;
import sia.modelo.sgl.vehiculo.vo.SgMantenimientoVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgEstadoVehiculoImpl;
import sia.servicios.sgl.impl.SgKilometrajeImpl;
import sia.servicios.sgl.impl.SgTallerMantenimientoImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgVehiculoMantenimientoImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author
 */
@Named(value = "mantenimientoBeanModel")

public class MantenimientoBeanModel implements Serializable {

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private Sesion sesion;

    @Inject
    private SgVehiculoMantenimientoImpl sgMantenimientoService;
    @Inject
    private SgKilometrajeImpl sgKilometrajeService;
    @Inject
    private SgEstadoVehiculoImpl sgEstadoVehiculoService;
    @Inject
    private SgTallerMantenimientoImpl sgTallerService;
    @Inject
    SgTipoTipoEspecificoImpl sgtipoTipoEspecificoService;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    SgVehiculoImpl vehiculoImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    ProveedorServicioImpl  proveedorService;
    private String directorioPath;
    private String operacionTerminarRegistro = Constantes.VACIO;
    private Proveedor proveedorSeleccionado;
    private String nombreProveedor;
    private Date fechaIngreso;
    private Date fechaSalida;
    private int idProveedorSeleecionado;
    private int idTipoMantenimientoEspecifico;
    private int idMantenimientoSeleccionado;
    private int idAdjunto;
    private int idMoneda;
    private String operacionProveedor = Constantes.VACIO;
    private String seleccionRadio = Constantes.VACIO;
    private String observaciones = Constantes.VACIO;
    private String tituloPopup = Constantes.VACIO;
    private Integer kilometrajeActual = 0;
    private Integer kilometrajeOld;
    private SgVehiculoMantenimiento sgMantenimiento;
    private VehiculoVO sgVehiculoSeleccionado;
    private SgEstadoVehiculo sgEstadoVehiculoOld;
    private SgKilometraje sgKilometraje;
    private String nombreEstadoVehiculoOld;
    //listasItems
    private List<SelectItem> listaAseguradorasProveedoresItems;
    private List<SelectItem> listaTalleresProveedoresItems;
    private List<SelectItem> listaTiposMantenimientoItems;
    private List<SelectItem> listaMonedaItems;
    private DataModel mantenimientoCorrectivoDataModel;
    private DataModel mantenimientoPreventivoDataModel;
    //booleanos
    private boolean mrPopupEntrada = false;
    private boolean mrPopupModificarEntrada = false;
    private boolean mrPopupSalida = false;
    private boolean mrPopupExternoDetalle = false;
    private boolean mrSubirArchivo = false;
    private boolean capturaProximoMantto = false;
    private boolean archivoMantenimineto = false;
    private SgKilometrajeVo kilometrajeAnteriorBueno;
    private SgKilometrajeVo kilometrajeActualVo;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    /**
     * Creates a new instance of mantenimientoBeanModel
     */
    public MantenimientoBeanModel() {
    }

    @PostConstruct
    public void iniciar() {
	setNombreEstadoVehiculoOld("Operación normal");
	UtilLog4j.log.info(this, "");
	UtilLog4j.log.info(this, "consulta de kilometraje old ok"); //para mostrarlo y validar
	UtilLog4j.log.info(this, "consulta de lista de mantenimiento ok");
//        UtilLog4j.log.info(this, "vehiculo " + sgVehiculoSeleccionado.getNombre());
    }

    public void iniciarMantenimiento(VehiculoVO sgVehiculo) {
	this.setSgVehiculoSeleccionado(sgVehiculo);
	traerListaMantenimientos();
	traerKilometrajeActualOld();
	traerVehiculoMantenimientoNoTerminado();
	traerEstadoVehiculoActual();
	traerKilometrajeActualOld();
	this.traerVehiculoMantenimientoNoTerminado(); //
	traerListaMantenimientos();

    }

    public SgVehiculoMantenimiento findSgVehiculoMantenimiento() {
	return sgMantenimientoService.find(this.idMantenimientoSeleccionado);
    }

    public void traerListaMantenimientos() {
	UtilLog4j.log.info(this, "+*traerListaMantenimientos*+");
	try {
	    //mantenimiento tipo preventivo
	    ListDataModel<SgMantenimientoVo> mantenimientoModel = new ListDataModel(sgMantenimientoService.findMantenimientosMaxResults(getSgVehiculoSeleccionado().getId(), 4, 3));
	    this.setMantenimientoPreventivoDataModel(mantenimientoModel);
	    UtilLog4j.log.info(this, "&&&&&&&Datamodel de mantenimientos preventivos asigando " + mantenimientoPreventivoDataModel.getRowCount());

	    ListDataModel<SgMantenimientoVo> mantenimientoTempModel = new ListDataModel(sgMantenimientoService.findMantenimientosMaxResults(getSgVehiculoSeleccionado().getId(), 9, 3));
	    this.setMantenimientoCorrectivoDataModel(mantenimientoTempModel);
	    UtilLog4j.log.info(this, "&&&&&&Datamodel de mantenimientos Correctivos asigando " + mantenimientoCorrectivoDataModel.getRowCount());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    /*
     * Traer el ultimo kilometraje anterior bueno, validar que si es de un
     * reinicio no pueda hacer la operacion
     */
    public List<SgKilometrajeVo> traerKilometrajeAnteriorBuenoYActual() {
	UtilLog4j.log.info(this, "traerKilometrajeAnteriorBueno" + getSgVehiculoSeleccionado().getId());
	SgKilometrajeVo kmActual = null;
	try {
	    return this.sgKilometrajeService.traerKilometrajeActualYAnterior(getSgVehiculoSeleccionado().getId());
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrio un error en la consulta de traerKilometrajeAnteriorBueno " + e.getMessage());
	    return null;
	}
    }

    public List<SelectItem> traerListaTalleresItems() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<Proveedor> la = null;
	try {
	    la = sgTallerService.findAllTalleresProveedor(sesion.getOficinaActual().getId());
	    UtilLog4j.log.info(this, "covertir a selectitem");
	    for (Proveedor pro : la) {
		UtilLog4j.log.info(this, " pro " + pro.getNombre());
		SelectItem item = new SelectItem(pro.getId(), pro.getNombre());
		l.add(item);
	    }
	    this.setListaTalleresProveedoresItems(l);
	    UtilLog4j.log.info(this, "antes de retornar");
	    return getListaTalleresProveedoresItems();
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrio un error en la consulta de proveedores de talleres " + e.getMessage());
	    return null;
	}
    }

    public List<SelectItem> traerMantenimientoTipoEspecificoItems() {
	UtilLog4j.log.info(this, "traerMantenimientoTipoEspecificoItems");
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<SgTipoTipoEspecifico> lc;
	try {
	    //Tipo Especifico
	    setIdTipoMantenimientoEspecifico(-1);
	    lc = sgtipoTipoEspecificoService.traerPorIdTipo(9, Constantes.BOOLEAN_FALSE);
	    for (SgTipoTipoEspecifico tipoEsp : lc) {
		if (tipoEsp.getSgTipoEspecifico().getId().intValue() != 7) {
		    //poner al primer elemento seleccionado como proveedor seleccionado
		    if (getIdTipoMantenimientoEspecifico() == -1) {
			setIdTipoMantenimientoEspecifico(tipoEsp.getSgTipoEspecifico().getId());
		    }
		    SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		    l.add(item);
		}
	    }

	    setListaTiposMantenimientoItems(l);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrio un error en la consulta de tipos especificos (Mantenimientos)");
	}
	return getListaTiposMantenimientoItems();
    }

    public void traerMoneda() {
	List<MonedaVO> lc;
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    lc = monedaImpl.traerMonedaActiva(Constantes.AP_CAMPO_DEFAULT);
	    UtilLog4j.log.info(this, "LMon: " + lc.size());
	    for (MonedaVO mon : lc) {
		SelectItem item = new SelectItem(mon.getId(), mon.getSiglas());
		l.add(item);
	    }
	    setListaMonedaItems(l);
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en traer lista de monedas");
	}
    }

    public void registrarEntradaMantenimiento() {
	try {
	    setProveedorSeleccionado(this.proveedorService.find(getIdProveedorSeleecionado()));
	    if (sgMantenimiento != null) {
		terminarMantenimiento();
		UtilLog4j.log.info(this, "se termino el mantenimiento ");
	    }

	    this.sgMantenimientoService.registroEntradaMantenimiento(getIdTipoMantenimientoEspecifico(),
		    getSgVehiculoSeleccionado(),
		    getProveedorSeleccionado(),
		    getKilometrajeActual(),
		    sesion.getUsuario(),
		    getObservaciones(),
		    getFechaIngreso());
	    setMrPopupEntrada(false);
	    setMrPopupModificarEntrada(false);
	    this.observaciones = Constantes.VACIO;
	    traerListaMantenimientos();
	    traerKilometrajeActualOld();
	    traerVehiculoMantenimientoNoTerminado();

	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al registrar la entrada al mantenimiento " + e.getMessage());
	}
    }

    public boolean terminarMantenimiento() {
	UtilLog4j.log.info(this, "terminarMantenimiento");
	try {
	    if (getSgMantenimiento() != null) {
		this.sgMantenimientoService.terminarMantenimiento(getSgMantenimiento(), sesion.getUsuario());
//            traerVehiculoMantenimientoNoTerminado();
		return true;
	    } else {
		UtilLog4j.log.info(this, "Sg mantenimiento es null ");
		return false;
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al terminar el mmto-. " + e.getMessage());
	    return false;
	}

    }

    public void modificarRegistroEntradaMantenimiento() {
	UtilLog4j.log.info(this, "modificarRegistroEntradaMantenimiento");
	try {
	    setProveedorSeleccionado(this.proveedorService.find(getIdProveedorSeleecionado()));
	    this.sgMantenimientoService.modificarRegistroEntradaMantenimiento(getSgMantenimiento(),
		    getIdTipoMantenimientoEspecifico(),
		    getProveedorSeleccionado(),
		    getKilometrajeActual(),
		    sesion.getUsuario(),
		    getObservaciones(),
		    getFechaIngreso());
	    traerKilometrajeActualOld();
	    traerVehiculoMantenimientoNoTerminado();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al modificar la entrada al mantenimiento " + e.getMessage());
	}
    }

    public void eliminarRegistroEntradaMantenimiento() {
	UtilLog4j.log.info(this, "mantenimientoBeanModel.eliminarRegistroEntradaMantenimiento");
	UtilLog4j.log.info(this, "vehiculo " + sgMantenimiento.getSgVehiculo().getSerie());
	SgKilometraje kmAnterior;
	SgEstadoVehiculo estadoAnterior;
	try {
	    sgKilometrajeService.eliminarKilometraje(sgMantenimiento.getSgKilometraje(), sesion.getUsuario());
	    kmAnterior = sgKilometrajeService.traerUltimoKm(sgMantenimiento.getSgVehiculo().getId());
	    UtilLog4j.log.info(this, "elimino el km");
	    if (kmAnterior != null) {
		sgKilometrajeService.activarKilometraje(kmAnterior, sesion.getUsuario());
		UtilLog4j.log.info(this, "activo el ultimo km");
	    } else {
		UtilLog4j.log.info(this, "No hay kilometrajes anteriores");
	    }
	    sgEstadoVehiculoService.eliminarEstado(sgEstadoVehiculoOld, sesion.getUsuario());
	    UtilLog4j.log.info(this, "Elimino el estado");
	    UtilLog4j.log.info(this, "activo el ultimo estado");

	    sgMantenimientoService.eliminarRegistroMantenimiento(getSgMantenimiento(), sesion.getUsuario());
	    UtilLog4j.log.info(this, "Todo bien al eliminar el mantenimiento");
	    //traer el estado nuevo
	    traerKilometrajeActualOld();
	    setOperacionTerminarRegistro("FALSE");
	    setNombreEstadoVehiculoOld("Operación normal");
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al eliminar el registro de mantenimiento " + e.getMessage());
	}
    }

    public void registrarSalidaMantenimiento() {
	UtilLog4j.log.info(this, "registrarSalidaMantenimiento ");
	try {
	    this.terminarMantenimiento();
	    this.sgMantenimientoService.registroSalidaMantenimiento(getSgMantenimiento(), getIdMoneda(), sesion.getUsuario());
	    traerVehiculoMantenimientoNoTerminado();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en registro de salida de mantenimiento " + e.getMessage());
	}
    }

    public boolean validarEntradaMantenimiento() {
	try {
	    if (sgMantenimientoService.findRegistroEntradaNOTerminado(getSgVehiculoSeleccionado().getId()) != null) {
		return true;
	    } else {
		return false;
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "excepcion al validar la entrada a mantenimiento " + e.getMessage());
	    return false;
	}
    }

    public void controlarPop(String pop, boolean estado) {
	sesion.getControladorPopups().put(pop, estado);
    }

    public void traerVehiculoMantenimientoNoTerminado() {
	UtilLog4j.log.info(this, "Traer el registro de mantenimiento para terminar");
	try {
	    this.setSgMantenimiento(sgMantenimientoService.findRegistroEntradaNOTerminado(getSgVehiculoSeleccionado().getId()));
	    if (getSgMantenimiento() != null) {
		this.setOperacionTerminarRegistro("TRUE");
		UtilLog4j.log.info(this, "Existe un registro por finalizar");
		//si existe un mantenimiento por terminar, traer el estado ..
		traerEstadoVehiculoActual();
	    } else {
		this.setOperacionTerminarRegistro("FALSE");
		UtilLog4j.log.info(this, "NO Existen registros por finalizar");
		traerEstadoVehiculoActual();
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion al traer el registro no terminado de mantenimiento");
	}
    }

    public void traerEstadoVehiculoActual() {
	UtilLog4j.log.info(this, "traerEstadoVehiculoActual");
	try {
	    this.setSgEstadoVehiculoOld(sgEstadoVehiculoService.findEstadoVehiculoActual(getSgVehiculoSeleccionado().getId()));
	    if (getSgEstadoVehiculoOld() != null) {
		setNombreEstadoVehiculoOld(getSgEstadoVehiculoOld().getSgTipoEspecifico().getNombre());
		/**
		 * ** CONTROL DE CAPTURA DE FECHA Y KILOMETRAJE DE PROXIMO
		 * MANTENIMIENTO AL RECIBIR UN VEHICULO ***
		 */
		if (getSgEstadoVehiculoOld().getSgTipoEspecifico().getId() == 4) {
		    UtilLog4j.log.info(this, " se captura el proximo mtto");
		    setCapturaProximoMantto(true);
		} else {
		    setCapturaProximoMantto(false);
		}
	    } else {
		setNombreEstadoVehiculoOld("Operación normal");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en la consulta de estado actual del vehiculo" + e.getMessage());
	}
    }

    /**
     * ****************subir archivo *******************
     */
    public boolean guardarArchivo(String fileName, String ruta, String contentType, long size) {
	boolean v = false;
	UtilLog4j.log.info(this, "Absolute path {0}", new Object[]{ruta + File.separator + fileName});

	SiAdjunto siAdjunto
		= siAdjuntoImpl.guardarArchivoDevolverArchivo(
			sesion.getUsuario().getId(),
			1,
			ruta + File.separator + fileName,
			fileName,
			contentType,
			size,
			9,
			"SGyL"
		);
	if (siAdjunto != null) {
	    v = true;
	    sgMantenimientoService.addArchivoAdjunto(
		    getSgMantenimiento(),
		    sesion.getUsuario(),
		    siAdjunto
	    );
	}
//        else {
//            siAdjuntoImpl.eliminarArchivo(siAdjunto, sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//        }
	return v;
    }

    public void traerDirMantenimiento() {
	if (sgMantenimiento != null) {
	    //this.setDirectorioPath(siParametroImpl.find(1).getUploadDirectory() + "SGyL/Vehiculo/ComprobanteMantenimiento" + "/" + getSgMantenimiento().getId() + "/");
	    setDirectorioPath("SGyL/Vehiculo/ComprobanteMantenimiento/" + getSgMantenimiento().getId());

	    LOGGER.info(this, "directorio " + getDirectorioPath());
	}
    }

    public void quitarArchivo() {

	try {
	    proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(
		    getSgMantenimiento().getSiAdjunto().getUrl()
	    );

	    siAdjuntoImpl.eliminarArchivo(
		    getSgMantenimiento().getSiAdjunto(),
		    sesion.getUsuario().getId(),
		    Constantes.BOOLEAN_TRUE
	    );
	    sgMantenimientoService.deleteArchivoAdjunto(sgMantenimiento, sesion.getUsuario());
	} catch (SIAException e) {
	    LOGGER.error(
		    "No se eliminó el adjunto: " + getSgMantenimiento().getSiAdjunto().getUrl(),
		    e
	    );
	}
//        String path = this.siParametroImpl.find(1).getUploadDirectory();
//        try {
//            File file = new File(path + getSgMantenimiento().getSiAdjunto().getUrl());
//            UtilLog4j.log.info(this, "path :" + path);
//            UtilLog4j.log.info(this, "path absoluto :" + getSgMantenimiento().getSiAdjunto().getUrl());
//            if (file.delete()) {
//                UtilLog4j.log.info(this, "Entro a eliminar");
//                siAdjuntoImpl.eliminarArchivo(getSgMantenimiento().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//                UtilLog4j.log.info(this, "Elimino el archivo de siAdjunto");
//                sgMantenimientoService.deleteArchivoAdjunto(sgMantenimiento, sesion.getUsuario());
//                UtilLog4j.log.info(this, "Elimino el adjunto de mantenimiento");
//            }
//            UtilLog4j.log.info(this, "entrando a eliminar el archivo fisico");
//            String dir = "SGyL/Vehiculo/ComprobanteMantenimiento" + "/" + getSgMantenimiento().getId() + "/";
//            UtilLog4j.log.info(this, "Ruta carpeta: " + dir);
//            File sessionfileUploadDirectory = new File(path + dir);
//            if (sessionfileUploadDirectory.isDirectory()) {
//                try {
//                    sessionfileUploadDirectory.delete();
//                } catch (SecurityException e) {
//                    UtilLog4j.log.fatal(this, e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            UtilLog4j.log.fatal(this, "Excepcion en quitar archivo :" + e.getMessage());
//        }
    }

    /**
     * *************************************************
     */
    public void traerKilometrajeActualOld() {
	LOGGER.info(this, "traerKilometrajeOld");

	this.sgKilometraje
		= sgKilometrajeService.findKilometrajeActualVehiculo(getSgVehiculoSeleccionado().getId());

	if (sgKilometraje == null) {
	    this.setKilometrajeOld(0);
	} else {
	    this.setKilometrajeOld(this.sgKilometraje.getKilometraje());
	}
	LOGGER.info(this, "Kilometraje " + getKilometrajeOld());
    }

    /**
     * @return the proveedorSeleccionado
     */
    public Proveedor getProveedorSeleccionado() {
	return proveedorSeleccionado;
    }

    /**
     * @param proveedorSeleccionado the proveedorSeleccionado to set
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
	this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * @return the nombreProveedor
     */
    public String getNombreProveedor() {
	return nombreProveedor;
    }

    /**
     * @param nombreProveedor the nombreProveedor to set
     */
    public void setNombreProveedor(String nombreProveedor) {
	this.nombreProveedor = nombreProveedor;
    }

    /**
     * @return the operacionProveedor
     */
    public String getOperacionProveedor() {
	return operacionProveedor;
    }

    /**
     * @param operacionProveedor the operacionProveedor to set
     */
    public void setOperacionProveedor(String operacionProveedor) {
	this.operacionProveedor = operacionProveedor;
    }

    /**
     * @return the kilometrajeActual
     */
    public Integer getKilometrajeActual() {
	return kilometrajeActual;
    }

    /**
     * @param kilometrajeActual the kilometrajeActual to set
     */
    public void setKilometrajeActual(Integer kilometrajeActual) {
	this.kilometrajeActual = kilometrajeActual;
    }

    /**
     * @return the listaTiposMantenimientoItems
     */
    public List<SelectItem> getListaTiposMantenimientoItems() {
	return listaTiposMantenimientoItems;
    }

    /**
     * @param listaTiposMantenimientoItems the listaTiposMantenimientoItems to
     * set
     */
    public void setListaTiposMantenimientoItems(List<SelectItem> listaTiposMantenimientoItems) {
	this.listaTiposMantenimientoItems = listaTiposMantenimientoItems;
    }

    /**
     * @return the seleccionRadio
     */
    public String getSeleccionRadio() {
	return seleccionRadio;
    }

    /**
     * @param seleccionRadio the seleccionRadio to set
     */
    public void setSeleccionRadio(String seleccionRadio) {
	this.seleccionRadio = seleccionRadio;
    }

    /**
     * @return the sgMantenimiento
     */
    public SgVehiculoMantenimiento getSgMantenimiento() {
	return sgMantenimiento;
    }

    /**
     * @param sgMantenimiento the sgMantenimiento to set
     */
    public void setSgMantenimiento(SgVehiculoMantenimiento sgMantenimiento) {
	this.sgMantenimiento = sgMantenimiento;
    }

    /**
     * @return the idTipoMantenimientoEspecifico
     */
    public int getIdTipoMantenimientoEspecifico() {
	return idTipoMantenimientoEspecifico;
    }

    /**
     * @param idTipoMantenimientoEspecifico the idTipoMantenimientoEspecifico to
     * set
     */
    public void setIdTipoMantenimientoEspecifico(int idTipoMantenimientoEspecifico) {
	this.idTipoMantenimientoEspecifico = idTipoMantenimientoEspecifico;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
	return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
	this.observaciones = observaciones;
    }

    /**
     * @return the listaTalleresProveedoresItems
     */
    public List<SelectItem> getListaTalleresProveedoresItems() {
	return listaTalleresProveedoresItems;
    }

    /**
     * @param listaTalleresProveedoresItems the listaTalleresProveedoresItems to
     * set
     */
    public void setListaTalleresProveedoresItems(List<SelectItem> listaTalleresProveedoresItems) {
	this.listaTalleresProveedoresItems = listaTalleresProveedoresItems;
    }

    /**
     * @return the listaAseguradorasProveedoresItems
     */
    public List<SelectItem> getListaAseguradorasProveedoresItems() {
	return listaAseguradorasProveedoresItems;
    }

    /**
     * @param listaAseguradorasProveedoresItems the
     * listaAseguradorasProveedoresItems to set
     */
    public void setListaAseguradorasProveedoresItems(List<SelectItem> listaAseguradorasProveedoresItems) {
	this.listaAseguradorasProveedoresItems = listaAseguradorasProveedoresItems;
    }

    /**
     * @return the idProveedorSeleecionado
     */
    public int getIdProveedorSeleecionado() {
	return idProveedorSeleecionado;
    }

    /**
     * @param idProveedorSeleecionado the idProveedorSeleecionado to set
     */
    public void setIdProveedorSeleecionado(int idProveedorSeleecionado) {
	this.idProveedorSeleecionado = idProveedorSeleecionado;
    }

    /**
     * @return the kilometrajeOld
     */
    public Integer getKilometrajeOld() {
	return kilometrajeOld;
    }

    /**
     * @param kilometrajeOld the kilometrajeOld to set
     */
    public void setKilometrajeOld(Integer kilometrajeOld) {
	this.kilometrajeOld = kilometrajeOld;
    }

    /**
     * @return the operacionTerminarRegistro
     */
    public String getOperacionTerminarRegistro() {
	return operacionTerminarRegistro;
    }

    /**
     * @param operacionTerminarRegistro the operacionTerminarRegistro to set
     */
    public void setOperacionTerminarRegistro(String operacion) {
	this.operacionTerminarRegistro = operacion;
    }

    /**
     * @return the listaMonedaItems
     */
    public List<SelectItem> getListaMonedaItems() {
	return listaMonedaItems;
    }

    /**
     * @param listaMonedaItems the listaMonedaItems to set
     */
    public void setListaMonedaItems(List<SelectItem> listaMonedaItems) {
	this.listaMonedaItems = listaMonedaItems;
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
     * @return the mrPopupEntrada
     */
    public boolean isMrPopupEntrada() {
	return mrPopupEntrada;
    }

    /**
     * @param mrPopupEntrada the mrPopupEntrada to set
     */
    public void setMrPopupEntrada(boolean mrPopupEntrada) {
	this.mrPopupEntrada = mrPopupEntrada;
    }

    /**
     * @return the mrPopupSalida
     */
    public boolean isMrPopupSalida() {
	return mrPopupSalida;
    }

    /**
     * @param mrPopupSalida the mrPopupSalida to set
     */
    public void setMrPopupSalida(boolean mrPopupSalida) {
	this.mrPopupSalida = mrPopupSalida;
    }

    /**
     * @return the mantenimientoDataModel
     */
    public DataModel getMantenimientoCorrectivoDataModel() {
	return mantenimientoCorrectivoDataModel;
    }

    /**
     * @param mantenimientoDataModel the mantenimientoDataModel to set
     */
    public void setMantenimientoCorrectivoDataModel(DataModel mantenimientoCorrectivoDataModel) {
	this.mantenimientoCorrectivoDataModel = mantenimientoCorrectivoDataModel;
    }

    /**
     * @return the mrPopupModificarEntrada
     */
    public boolean isMrPopupModificarEntrada() {
	return mrPopupModificarEntrada;
    }

    /**
     * @param mrPopupModificarEntrada the mrPopupModificarEntrada to set
     */
    public void setMrPopupModificarEntrada(boolean mrPopupModificarEntrada) {
	this.mrPopupModificarEntrada = mrPopupModificarEntrada;
    }

    /**
     * @return the sgEstadoVehiculoOld
     */
    public SgEstadoVehiculo getSgEstadoVehiculoOld() {
	return sgEstadoVehiculoOld;
    }

    /**
     * @param sgEstadoVehiculoOld the sgEstadoVehiculoOld to set
     */
    public void setSgEstadoVehiculoOld(SgEstadoVehiculo sgEstadoVehiculoOld) {
	this.sgEstadoVehiculoOld = sgEstadoVehiculoOld;
    }

    /**
     * @return the mrSubirArchivo
     */
    public boolean isMrSubirArchivo() {
	return mrSubirArchivo;
    }

    /**
     * @param mrSubirArchivo the mrSubirArchivo to set
     */
    public void setMrSubirArchivo(boolean mrSubirArchivo) {
	this.mrSubirArchivo = mrSubirArchivo;
    }

    /**
     * @return the directorioPath
     */
    public String getDirectorioPath() {
	return directorioPath;
    }

    /**
     * @param directorioPath the directorioPath to set
     */
    public void setDirectorioPath(String directorioPath) {
	this.directorioPath = directorioPath;
    }

    /**
     * @return the fechaIngreso
     */
    public Date getFechaIngreso() {
	return (Date) fechaIngreso.clone();
    }

    /**
     * @param fechaIngreso the fechaIngreso to set
     */
    public void setFechaIngreso(Date fechaIngreso) {
	this.fechaIngreso = (Date) fechaIngreso.clone();
    }

    /**
     * @return the fechaSalida
     */
    public Date getFechaSalida() {
	return (Date) fechaSalida.clone();
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(Date fechaSalida) {
	this.fechaSalida = (Date) fechaSalida.clone();
    }

    /**
     * @return the nombreEstadoVehiculoOld
     */
    public String getNombreEstadoVehiculoOld() {
	return nombreEstadoVehiculoOld;
    }

    /**
     * @param nombreEstadoVehiculoOld the nombreEstadoVehiculoOld to set
     */
    public void setNombreEstadoVehiculoOld(String nombreEstadoVehiculoOld) {
	this.nombreEstadoVehiculoOld = nombreEstadoVehiculoOld;
    }

    /**
     * @return the sgKilometraje
     */
    public SgKilometraje getSgKilometraje() {
	return sgKilometraje;
    }

    /**
     * @param sgKilometraje the sgKilometraje to set
     */
    public void setSgKilometraje(SgKilometraje sgKilometraje) {
	this.sgKilometraje = sgKilometraje;
    }

    /**
     * @return the mrPopupExternoDetalle
     */
    public boolean isMrPopupExternoDetalle() {
	return mrPopupExternoDetalle;
    }

    /**
     * @param mrPopupExternoDetalle the mrPopupExternoDetalle to set
     */
    public void setMrPopupExternoDetalle(boolean mrPopupExternoDetalle) {
	this.mrPopupExternoDetalle = mrPopupExternoDetalle;
    }

    /**
     * @return the capturaProximoMantto
     */
    public boolean isCapturaProximoMantto() {
	return capturaProximoMantto;
    }

    /**
     * @param capturaProximoMantto the capturaProximoMantto to set
     */
    public void setCapturaProximoMantto(boolean capturaProximoMantto) {
	this.capturaProximoMantto = capturaProximoMantto;
    }

    /**
     * @return the archivoMantenimineto
     */
    public boolean isArchivoMantenimineto() {
	return archivoMantenimineto;
    }

    /**
     * @param archivoMantenimineto the archivoMantenimineto to set
     */
    public void setArchivoMantenimineto(boolean archivoMantenimineto) {
	this.archivoMantenimineto = archivoMantenimineto;
    }

    /**
     * @return the mantenimientoPreventivoDataModel
     */
    public DataModel getMantenimientoPreventivoDataModel() {
	return mantenimientoPreventivoDataModel;
    }

    /**
     * @param mantenimientoPreventivoDataModel the
     * mantenimientoPreventivoDataModel to set
     */
    public void setMantenimientoPreventivoDataModel(DataModel mantenimientoPreventivoDataModel) {
	this.mantenimientoPreventivoDataModel = mantenimientoPreventivoDataModel;
    }

    /**
     * @return the idMantenimientoSeleccionado
     */
    public int getIdMantenimientoSeleccionado() {
	return idMantenimientoSeleccionado;
    }

    /**
     * @param idMantenimientoSeleccionado the idMantenimientoSeleccionado to set
     */
    public void setIdMantenimientoSeleccionado(int idMantenimientoSeleccionado) {
	this.idMantenimientoSeleccionado = idMantenimientoSeleccionado;
    }

    /**
     * @return the tituloPopup
     */
    public String getTituloPopup() {
	return tituloPopup;
    }

    /**
     * @param tituloPopup the tituloPopup to set
     */
    public void setTituloPopup(String tituloPopup) {
	this.tituloPopup = tituloPopup;
    }

    /**
     * @return the kilometrajeAnteriorBueno
     */
    public SgKilometrajeVo getKilometrajeAnteriorBueno() {
	return kilometrajeAnteriorBueno;
    }

    /**
     * @param kilometrajeAnteriorBueno the kilometrajeAnteriorBueno to set
     */
    public void setKilometrajeAnteriorBueno(SgKilometrajeVo kilometrajeAnteriorBueno) {
	this.kilometrajeAnteriorBueno = kilometrajeAnteriorBueno;
    }

    /**
     * @return the kilometrajeActualVo
     */
    public SgKilometrajeVo getKilometrajeActualVo() {
	return kilometrajeActualVo;
    }

    /**
     * @param kilometrajeActualVo the kilometrajeActualVo to set
     */
    public void setKilometrajeActualVo(SgKilometrajeVo kilometrajeActualVo) {
	this.kilometrajeActualVo = kilometrajeActualVo;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the sgVehiculoSeleccionado
     */
    public VehiculoVO getSgVehiculoSeleccionado() {
	return sgVehiculoSeleccionado;
    }

    /**
     * @param sgVehiculoSeleccionado the sgVehiculoSeleccionado to set
     */
    public void setSgVehiculoSeleccionado(VehiculoVO sgVehiculoSeleccionado) {
	this.sgVehiculoSeleccionado = sgVehiculoSeleccionado;
    }
}
