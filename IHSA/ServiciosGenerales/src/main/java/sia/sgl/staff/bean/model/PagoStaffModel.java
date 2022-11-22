/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.staff.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
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
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioStaff;
import sia.modelo.SgStaff;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.SiAdjunto;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgPagoServicioImpl;
import sia.servicios.sgl.impl.SgPagoServicioStaffImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "pagoStaffModel")

public class PagoStaffModel implements Serializable {

    /**
     * Creates a new instance of PagoStaffModel
     */
    public PagoStaffModel() {
    }

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private Sesion sesion;
    @Inject
    private SgStaffImpl sgStaffImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SgTipoImpl sgTipoImpl;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoImpl;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgPagoServicioImpl sgPagoServicioImpl;
    @Inject
    private SgPagoServicioStaffImpl sgPagoServicioStaffImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private SiParametroImpl siParametroImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;

    private int idStaff;
    private int idTipoEspecifico;
    private int idMoneda;
    private String opcionPagar;
    private SgTipo sgTipo;
    private DataModel lista;
    private List<String> listaProveedorBuscar;
    private List<SelectItem> listaPagos;

    private List<SelectItem> listaTipoEspecifico;
    private SgTipoEspecifico sgTipoEspecifico;
    private DataModel listaComedor;
    private SgPagoServicio sgPagoServicio;
    private SgStaff sgStaff;
    private String pro;
    private SgPagoServicioStaff sgPagoServicioStaff;
    private boolean modificarPopUp = false;
    private boolean subirArchivo = false;
    private boolean crearPopUp = false;
    private List<SelectItem> listaProveedor; //Usado: a)SiEstado en alta de Oficina

    @PostConstruct
    public void iniciar() {
	setIdStaff(-1);
	setIdTipoEspecifico(-1);
	traerCasaStaff();
	setOpcionPagar("Staff");
	buscarTipoGeneral();
	setLista(null);
	setListaProveedorBuscar(traerProveedor());
    }

    public List<SelectItem> traerCasaStaff() {
	List<SgStaff> lc;
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    lc = sgStaffImpl.getAllStaffByStatusAndOficina(Constantes.BOOLEAN_FALSE, sesion.getOficinaActual().getId());
	    for (SgStaff st : lc) {
		SelectItem item = new SelectItem(st.getId(), st.getNombre());
		l.add(item);
	    }
	    setListaPagos(l);
	    return getListaPagos();
	} catch (Exception e) {
	    return null;
	}
    }

    public List<String> traerProveedor() {
	return proveedorImpl.traerNombreProveedorQueryNativo(sesion.getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());
    }

    public void buscarTipoGeneral() {
	List<SgTipo> lt = sgTipoImpl.traerTipo(sesion.getUsuario(), Constantes.BOOLEAN_FALSE);
	for (SgTipo sgT : lt) {
	    if (sgT.getNombre().toUpperCase().equals(getOpcionPagar().toUpperCase())) {
		setSgTipo(sgT);
		break;
	    }
	}
    }

    public List<SelectItem> traerTipoEspecificoPorTipoOficina() {

	if (sesion.getOficinaActual().getId() > 0) {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<SgTipoTipoEspecifico> lc;
	    try {
		lc = sgTipoTipoEspecificoImpl.traerPorTipoPago(getSgTipo(), Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);
		for (SgTipoTipoEspecifico tipoEsp : lc) {
		    SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		    l.add(item);
		}
		setListaTipoEspecifico(l);
	    } catch (Exception e) {
		UtilLog4j.log.info(this, "Aqui en la excepción");
	    }
	} else {
	    setListaTipoEspecifico(null);
	}
	return getListaTipoEspecifico();
    }

    public List<SelectItem> traerTipoEspecificoPorTipoStaff() {
	if (getIdStaff() > 0) {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    List<SgTipoTipoEspecifico> lc;
	    try {
		lc = sgTipoTipoEspecificoImpl.traerPorTipoPago(getSgTipo(), Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_TRUE);
		for (SgTipoTipoEspecifico tipoEsp : lc) {
		    SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		    l.add(item);
		}
		setListaTipoEspecifico(l);
	    } catch (Exception e) {
		UtilLog4j.log.info(this, "Aqui en la excepción");
	    }
	} else {
	    setListaTipoEspecifico(null);
	}
	return getListaTipoEspecifico();
    }

    public void traerPagoPorStaff() {
	setSgTipoEspecifico(sgTipoEspecificoImpl.find(getIdTipoEspecifico()));
	setLista(new ListDataModel(sgPagoServicioStaffImpl.traerPagoPorStaff(getSgStaff(), getSgTipo(), getSgTipoEspecifico(), Constantes.BOOLEAN_FALSE)));
	setSgPagoServicio(null);
    }

    public List<SelectItem> traerMoneda() {
	List<MonedaVO> lc;
	try {
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    lc = monedaImpl.traerMonedaActiva(Constantes.AP_CAMPO_DEFAULT);
	    UtilLog4j.log.info(this, "LMon: " + lc.size());
	    for (MonedaVO mon : lc) {
		SelectItem item = new SelectItem(mon.getId(), mon.getSiglas());
		l.add(item);
	    }
	    return l;
	} catch (Exception e) {
	    return null;
	}
    }

    public void buscarObjetoStaff() {
	if (getIdStaff() > 0) {
	    setSgStaff(sgStaffImpl.find(getIdStaff()));
	}
    }

    public Proveedor buscarProveedorPorNombre() {
	try {
	    return proveedorImpl.getPorNombre(getPro(), sesion.getRfcEmpresa());
	} catch (Exception e) {
	    return null;
	}
    }

    public boolean guardarPagoServicioStaff() {
	try {
	    setSgStaff(sgStaffImpl.find(getIdStaff()));
	    sgPagoServicioImpl.guardarPagoServicio(Constantes.TIPO_PAGO_STAFF, getSgTipoEspecifico(), getSgPagoServicio(),
		    getSgStaff(), sesion.getUsuario(), Constantes.BOOLEAN_FALSE, getIdMoneda(), getOpcionPagar(),
		    getPro(), sesion.getRfcEmpresa());
	    traerPagoPorStaff();
	    return true;
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "e + + " + e.getMessage() + "  + + +" + e.getCause());
	    return false;
	}
    }

    public void modificarPagoServicio() {
	sgPagoServicioImpl.modificarPagoServicio(getSgPagoServicio(), sesion.getUsuario(), getIdMoneda());
    }

    public void eliminarPagoServicioStaff() {
	sgPagoServicioImpl.eliminarPagoServicio(
		getSgStaff(),
		getSgPagoServicioStaff(),
		sesion.getUsuario(),
		Constantes.BOOLEAN_TRUE
	);
	if (getSgPagoServicio().getSiAdjunto() != null) {
	    eliminarComprobante();
	}
    }

    public void eliminarComprobante() {

	SiAdjunto adjunto = getSgPagoServicio().getSiAdjunto();
	if (adjunto != null) {
	    try {
		proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(adjunto.getUrl());

		getSgPagoServicio().setSiAdjunto(null);
		sgPagoServicioImpl.modificarPagoServicio(
			getSgPagoServicio(),
			sesion.getUsuario(),
			getSgPagoServicio().getMoneda().getId()
		);
		siAdjuntoImpl.eliminarArchivo(
			adjunto,
			sesion.getUsuario().getId(),
			Constantes.BOOLEAN_TRUE
		);
	    } catch (SIAException ex) {
		LOGGER.error("Eliminando adjunto " + adjunto.getUrl(), ex);
	    }
	}

	//Se eliminan fisicamente los archivos
//	String path = this.siParametroImpl.find(1).getUploadDirectory();
//	try {
//	    File file = new File(path + getSgPagoServicio().getSiAdjunto().getUrl());
//	    if (file.delete()) {
//		getSgPagoServicio().setSiAdjunto(null);
//		sgPagoServicioImpl.modificarPagoServicio(getSgPagoServicio(), sesion.getUsuario(), getSgPagoServicio().getMoneda().getId());
//		siAdjuntoImpl.eliminarArchivo(getSgPagoServicio().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//	    }
//	    //Elimina la carpeta
//	    //         for (PcTipoDocumento pcTipoDocumento : this.pcClasificacionServicioRemoto.traerArchivoActivo()) {
//	    String dir = "SGyL/Pago/" + getSgTipo().getNombre() + "/" + getSgPagoServicio().getId();
//	    UtilLog4j.log.info(this, "Ruta carpeta: " + dir);
//	    File sessionfileUploadDirectory = new File(path + dir);
//	    if (sessionfileUploadDirectory.isDirectory()) {
//		try {
//		    sessionfileUploadDirectory.delete();
//		} catch (SecurityException e) {
//		    UtilLog4j.log.info(this, e.getMessage());
//		}
//	    }
//	    //        }
//	} catch (Exception e) {
//	    UtilLog4j.log.info(this, e.getMessage());
//	}
    }

    public boolean guardarArchivo(String fileName, String contentType, long size) {
	boolean v = false;
	SiAdjunto siAdjunto = siAdjuntoImpl.guardarArchivoDevolverArchivo(sesion.getUsuario().getId(), 1, "SGyL/Pago/" + getSgTipo().getNombre() + "/" + getSgPagoServicio().getId() + "/" + fileName, fileName, contentType, size, 9, "SGyL");
	UtilLog4j.log.info(this, "Aqui después de guardar el archivo");
	if (siAdjunto != null) {
	    v = sgPagoServicioImpl.agregarArchivoPagoServicio(getSgPagoServicio(), sesion.getUsuario(), siAdjunto);
	    UtilLog4j.log.info(this, "Ahora déspues de agreegar a pago servicio");
	    if (v) {
		v = true;
	    } else {
		siAdjuntoImpl.remove(siAdjunto);
	    }
	}
	return v;
    }

    public void traerPagoPorOficina() {
	setSgTipoEspecifico(sgTipoEspecificoImpl.find(getIdTipoEspecifico()));
	setSgPagoServicio(null);
    }

    public String getDirectorio() {
	return "SGyL/Pago/" + getSgTipo().getNombre() + "/" + getSgPagoServicio().getId() + "/";
    }

    /**
     * @return the idStaff
     */
    public int getIdStaff() {
	return idStaff;
    }

    /**
     * @param idStaff the idStaff to set
     */
    public void setIdStaff(int idStaff) {
	this.idStaff = idStaff;
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
	return idTipoEspecifico;
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
	this.idTipoEspecifico = idTipoEspecifico;
    }

    /**
     * @return the opcionPagar
     */
    public String getOpcionPagar() {
	return opcionPagar;
    }

    /**
     * @param opcionPagar the opcionPagar to set
     */
    public void setOpcionPagar(String opcionPagar) {
	this.opcionPagar = opcionPagar;
    }

    /**
     * @return the sgTipo
     */
    public SgTipo getSgTipo() {
	return sgTipo;
    }

    /**
     * @param sgTipo the sgTipo to set
     */
    public void setSgTipo(SgTipo sgTipo) {
	this.sgTipo = sgTipo;
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
	return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
	this.lista = lista;
    }

    /**
     * @return the listaProveedorBuscar
     */
    public List<String> getListaProveedorBuscar() {
	return listaProveedorBuscar;
    }

    /**
     * @param listaProveedorBuscar the listaProveedorBuscar to set
     */
    public void setListaProveedorBuscar(List<String> listaProveedorBuscar) {
	this.listaProveedorBuscar = listaProveedorBuscar;
    }

    /**
     * @return the listaPagos
     */
    public List<SelectItem> getListaPagos() {
	return listaPagos;
    }

    /**
     * @param listaPagos the listaPagos to set
     */
    public void setListaPagos(List<SelectItem> listaPagos) {
	this.listaPagos = listaPagos;
    }

    /**
     * @return the listaTipoEspecifico
     */
    public List<SelectItem> getListaTipoEspecifico() {
	return listaTipoEspecifico;
    }

    /**
     * @param listaTipoEspecifico the listaTipoEspecifico to set
     */
    public void setListaTipoEspecifico(List<SelectItem> listaTipoEspecifico) {
	this.listaTipoEspecifico = listaTipoEspecifico;
    }

    /**
     * @return the sgTipoEspecifico
     */
    public SgTipoEspecifico getSgTipoEspecifico() {
	return sgTipoEspecifico;
    }

    /**
     * @param sgTipoEspecifico the sgTipoEspecifico to set
     */
    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
	this.sgTipoEspecifico = sgTipoEspecifico;
    }

    /**
     * @return the listaComedor
     */
    public DataModel getListaComedor() {
	return listaComedor;
    }

    /**
     * @param listaComedor the listaComedor to set
     */
    public void setListaComedor(DataModel listaComedor) {
	this.listaComedor = listaComedor;
    }

    /**
     * @return the sgPagoServicio
     */
    public SgPagoServicio getSgPagoServicio() {
	return sgPagoServicio;
    }

    /**
     * @param sgPagoServicio the sgPagoServicio to set
     */
    public void setSgPagoServicio(SgPagoServicio sgPagoServicio) {
	this.sgPagoServicio = sgPagoServicio;
    }

    /**
     * @return the sgStaff
     */
    public SgStaff getSgStaff() {
	return sgStaff;
    }

    /**
     * @param sgStaff the sgStaff to set
     */
    public void setSgStaff(SgStaff sgStaff) {
	this.sgStaff = sgStaff;
    }

    /**
     * @return the pro
     */
    public String getPro() {
	return pro;
    }

    /**
     * @param pro the pro to set
     */
    public void setPro(String pro) {
	this.pro = pro;
    }

    /**
     * @return the sgPagoServicioStaff
     */
    public SgPagoServicioStaff getSgPagoServicioStaff() {
	return sgPagoServicioStaff;
    }

    /**
     * @param sgPagoServicioStaff the sgPagoServicioStaff to set
     */
    public void setSgPagoServicioStaff(SgPagoServicioStaff sgPagoServicioStaff) {
	this.sgPagoServicioStaff = sgPagoServicioStaff;
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

    private boolean eliminarPop = false;
    private boolean popUp = false;

    /**
     * @return the modificarPopUp
     */
    public boolean isModificarPopUp() {
	return modificarPopUp;
    }

    /**
     * @param modificarPopUp the modificarPopUp to set
     */
    public void setModificarPopUp(boolean modificarPopUp) {
	this.modificarPopUp = modificarPopUp;
    }

    /**
     * @return the eliminarPop
     */
    public boolean isEliminarPop() {
	return eliminarPop;
    }

    /**
     * @param eliminarPop the eliminarPop to set
     */
    public void setEliminarPop(boolean eliminarPop) {
	this.eliminarPop = eliminarPop;
    }

    /**
     * @return the subirArchivo
     */
    public boolean isSubirArchivo() {
	return subirArchivo;
    }

    /**
     * @param subirArchivo the subirArchivo to set
     */
    public void setSubirArchivo(boolean subirArchivo) {
	this.subirArchivo = subirArchivo;
    }

    /**
     * @return the popUp
     */
    public boolean isPopUp() {
	return popUp;
    }

    /**
     * @param popUp the popUp to set
     */
    public void setPopUp(boolean popUp) {
	this.popUp = popUp;
    }

    /**
     * @return the crearPopUp
     */
    public boolean isCrearPopUp() {
	return crearPopUp;
    }

    /**
     * @param crearPopUp the crearPopUp to set
     */
    public void setCrearPopUp(boolean crearPopUp) {
	this.crearPopUp = crearPopUp;
    }

    /**
     * @return the listaProveedor
     */
    public List<SelectItem> getListaProveedor() {
	return listaProveedor;
    }

    /**
     * @param listaProveedor the listaProveedor to set
     */
    public void setListaProveedor(List<SelectItem> listaProveedor) {
	this.listaProveedor = listaProveedor;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }
}
