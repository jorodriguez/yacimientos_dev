    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.hotel.bean.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.modelo.Proveedor;
import sia.modelo.SgHotel;
import sia.modelo.SgHotelHabitacion;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.sgl.hotel.vo.SgHotelTipoEspecificoVo;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgHotelHabitacionImpl;
import sia.servicios.sgl.impl.SgHotelImpl;
import sia.servicios.sgl.impl.SgHotelTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 * @see mluis
 */
@Named(value = "hotelBeanModel")
@ViewScoped
public class HotelBeanModel implements Serializable {

    //Sistema
    @Inject
    private Sesion sesion;
    //ManagedBeans
    //Servicios
    @Inject
    private SgHotelImpl sgHotelImpl;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgHotelHabitacionImpl sgHotelHabitacionImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SgHotelTipoEspecificoImpl sgHotelTipoEspecificoImpl;
    //Entidades
    private Proveedor proveedorSeleccionado;
    private SgHotel sgHotel;
    private SgHotel sgHotelSeleccionado;
    private SgHotelHabitacion sgHabitacion;
    //Clases
    private String nombreProveedor;
    private String operacion = "";
    private String mensajeError;
    private String numeroEstrellas;
    private BigDecimal bigDecimal = new BigDecimal("0.00");
    private SgHotelTipoEspecificoVo sgHotelTipoEspecificoVo;
    //Colecciones
    private DataModel dataModel;
    private DataModel hotelModel;
    private DataModel tipoHabitacionModel;
    private DataModel habitacionToHotelModel;
    private List<SelectItem> listaProveedor;
    private List<SelectItem> listaTiposHabitacionItems;
    private List<SgHotelHabitacion> listHotelHabitacion;
    private List<String> listaProveedorBuscar;
    //Primitivos
    private int idTipoEspecificoHabitacion;
    private int idServicio;
    //Booleanos
    private boolean flag;
    private boolean mrPopupAgregarHotel = false;
    private boolean mrPopupModificarHotel = false;
    private boolean mrPopupEliminarHotel = false;
    private boolean mrPopupVerDetalleHotel = false;
    private boolean mrPopupAgregarHabitacionToHotel = false;
    private boolean mrPopupEliminarHabitacionToHotel = false;
    private boolean mrPopupAgregarTipoHabitacionHotel = false;
    private boolean mrPopupModificarTipoHabitacionHotel = false;
    private boolean mrPopupEliminarTipoHabitacionHotel = false;
    private boolean mrPopupAviso = false;

    public HotelBeanModel() {
    }

    @PostConstruct
    public void beginConversationCatalogoHotel() {
	UtilLog4j.log.info(this, "beginConversacionHotel");
	sesion.getControladorPopups().put("popupSgHotelHabitaciones", Boolean.FALSE);
	sesion.getControladorPopups().put("popupSgHotelServices", Boolean.FALSE);
	setListaProveedorBuscar(traerProveedor());
	traerHotelesModel();
	setHabitacionToHotelModel(null);
	setDataModel(null);
	setMrPopupVerDetalleHotel(false);
	setSgHotel(null);
	setSgHabitacion(null);
	setFlag(false);
	setIdServicio(-1);
	setTipoHabitacionModel(null);
    }

    public void controlarPop(String pop, boolean estado) {
	sesion.getControladorPopups().put(pop, estado);
    }

    /**
     * Consultas
     */
    public void traerHotelesModel() {
	UtilLog4j.log.info(this, "hotelBeanModel.traerHotelesModel()");
	try {
	    ListDataModel<SgHotel> hotelDM = new ListDataModel(sgHotelImpl.getAllHotel(sesion.getOficinaActual().getId()));
	    this.hotelModel = (DataModel) hotelDM;
	    UtilLog4j.log.info(this, "Datamodel de hotel asignado");
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public List<SgHotelHabitacion> getAllSgHabitacionHotelBySgHotel() {
	return this.sgHotelHabitacionImpl.findAllHabitacionesToHotel(getSgHotel());
    }

    public void traerHotelHabitacionModel() {
	UtilLog4j.log.info(this, "hotelBeanModel.traerHotelHabitacionModel()");
	try {
	    setListHotelHabitacion(sgHotelHabitacionImpl.findAllHabitacionesToHotel(getSgHotel()));
	    ListDataModel<SgHotelHabitacion> hotelHabitacionDM = new ListDataModel(getListHotelHabitacion());
	    UtilLog4j.log.info(this, "agregado al model");
	    this.habitacionToHotelModel = (DataModel) hotelHabitacionDM;
	    UtilLog4j.log.info(this, "Datamodel de tipo de HotelHabitacion asignado");
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Ocurrio un error");
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public Proveedor traerProveedorPorNombre() {
	try {
	    UtilLog4j.log.info(this, "Traer proveedor por nombre");
	    if (!getNombreProveedor().equals("")) {
		setProveedorSeleccionado(proveedorImpl.getPorNombre(this.getNombreProveedor(), sesion.getRfcEmpresa()));
	    }
	    UtilLog4j.log.info(this, "antes de retornar..");
	    return getProveedorSeleccionado();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion en traer proveedor por nombre " + e.getMessage());
	    return null;
	}
    }

    public List<SelectItem> traerTipoEspecificoPorTipoHabitacionItems() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<SgTipoTipoEspecifico> lc;
	try {
	    //Tipo Especifico
	    lc = sgTipoTipoEspecificoImpl.traerPorIdTipo(10, Constantes.BOOLEAN_FALSE);
	    for (SgTipoTipoEspecifico tipoEsp : lc) {
		SelectItem item = new SelectItem(tipoEsp.getSgTipoEspecifico().getId(), tipoEsp.getSgTipoEspecifico().getNombre());
		l.add(item);
	    }
	    setListaTiposHabitacionItems(l);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de tipos especificos" + e.getMessage());
	}
	return getListaTiposHabitacionItems();
    }

    public List<SgTipoTipoEspecifico> getAllServiciosSgHotel() {
	return this.sgTipoTipoEspecificoImpl.getSgTipoTipoEspecificoBySgTipo(8, true, false, false);
    }

    public List<SgHotelTipoEspecificoVo> getAllSgHotelTipoEspecificoBySgHotel() {
	return this.sgHotelTipoEspecificoImpl.getAllSgHotelTipoEspecificoBySgHotelAndProvided(getSgHotel().getId(), true, "nombre", true, false);
    }

    public void createHotel() {
	if (getProveedorSeleccionado() != null) {
	    sgHotelImpl.createHotel(getProveedorSeleccionado(), getSgHotel(), sesion.getOficinaActual().getId(), sesion.getUsuario());
	    traerHotelesModel();
	    setListaProveedor(null);
	    setProveedorSeleccionado(null);
	    setSgHotel(null);
	    traerHotelesModel();
	}
    }

    public String editHotel() {
	UtilLog4j.log.info(this, "hotelBeanModel.editHotel()");
	try {
//            if (getProveedorSeleccionado()!=null) {
	    sgHotelImpl.editHotel(getSgHotel(), sesion.getOficinaActual().getId(), sesion.getUsuario());
	    traerHotelesModel();
	    setListaProveedor(null);
	    setProveedorSeleccionado(null);
	    setNombreProveedor("");
	    UtilLog4j.log.info(this, "Hotel modificado");
	    //          }
	    return "catalogoHotel";
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcione al editar un hotel " + e.getMessage());
	    return "catalogoHotel";
	}
    }

    public void deleteHotel() {
	UtilLog4j.log.info(this, "hotelBeanModel.deleteHotel()");
	try {
	    sgHotelImpl.deleteHotel(getSgHotel(), sesion.getUsuario());
	    traerHotelesModel();
	    UtilLog4j.log.info(this, "Hotel modificado");
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	}
    }

    public void createHotelHabitacion() {
	UtilLog4j.log.info(this, "hotelBeanModel.createHotelHabitacion()");
	try {
	    if (getIdTipoEspecificoHabitacion() != -1) {
		SgHotelHabitacion sgHotelHabitacion = new SgHotelHabitacion();
		sgHotelHabitacion.setPrecio(getBigDecimal());
		sgHotelHabitacionImpl.createHotelHabitacion(getSgHotel(), getIdTipoEspecificoHabitacion(), sgHotelHabitacion, sesion.getUsuario());
		UtilLog4j.log.info(this, "Habitacion creada sin problemas");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    public boolean modificarPrecioHabitacion() {
	try {
	    UtilLog4j.log.info(this, "habitacion " + sgHabitacion.getId());
	    UtilLog4j.log.info(this, "habitacion " + bigDecimal);
	    return sgHotelHabitacionImpl.updatePrecioHabitacion(sgHabitacion.getId(), bigDecimal, sesion.getUsuario().getId());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return false;
	}
    }

    public void deleteHotelHabitacion() {
	UtilLog4j.log.info(this, "hotelBeanModel.deleteHotelHabitacion()");
	try {
	    if (getSgHabitacion() != null) {
		sgHotelHabitacionImpl.deleteHotelHabitacion(getSgHabitacion(), sesion.getUsuario());
		traerHotelHabitacionModel();
		UtilLog4j.log.info(this, "habitacion eliminada ..");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    public void saveSgHotelTipoEspecifico() throws ExistingItemException {
	this.sgHotelTipoEspecificoImpl.saveWithUpdateForHuespedes(getSgHotel().getId().intValue(), getIdServicio(), this.sesion.getUsuario().getId());
	setDataModel(new ListDataModel(getAllSgHotelTipoEspecificoBySgHotel()));
    }

    public void deleteSgHotelTipoEspecifico() {
	this.sgHotelTipoEspecificoImpl.deleteWithUpdateForHuespedes(getSgHotelTipoEspecificoVo().getId(), this.sesion.getUsuario().getId());
	setDataModel(new ListDataModel(getAllSgHotelTipoEspecificoBySgHotel()));
    }

    public boolean buscarHotelEnRelacion() {
	return sgHotelHabitacionImpl.buscarHotelHabitacionEnRelacion(getSgHotel());
    }

    public boolean buscarHabitacionEnRelacion() {
	return sgHotelHabitacionImpl.buscarHabitacionEnRelacion(getSgHabitacion());
    }

    public boolean buscarHotelRepetido() {
	return sgHotelImpl.buscarHotelRepetido(getProveedorSeleccionado(), sesion.getOficinaActual().getId());
    }

    public boolean buscarHabitacionRepetida() {
	return sgHotelHabitacionImpl.buscarHabitacionRepetida(getSgHotel().getId(), getIdTipoEspecificoHabitacion());
    }

    /*
     * @return the mrPopupAgregarHotel
     */
    public boolean isMrPopupAgregarHotel() {
	return mrPopupAgregarHotel;
    }

    /**
     * @param mrPopupAgregarHotel the mrPopupAgregarHotel to set
     */
    public void setMrPopupAgregarHotel(boolean mrPopupAgregarHotel) {
	this.mrPopupAgregarHotel = mrPopupAgregarHotel;
    }

    /**
     * @return the mrPopupEliminarrHotel
     */
    public boolean isMrPopupEliminarHotel() {
	return mrPopupEliminarHotel;
    }

    /**
     * @param mrPopupEliminarrHotel the mrPopupEliminarrHotel to set
     */
    public void setMrPopupEliminarHotel(boolean mrPopupEliminarrHotel) {
	this.mrPopupEliminarHotel = mrPopupEliminarrHotel;
    }

    /**
     * @return the hotelModel
     */
    public DataModel getHotelModel() {
	return hotelModel;
    }

    /**
     * @param hotelModel the hotelModel to set
     */
    public void setHotelModel(DataModel hotelModel) {
	this.hotelModel = hotelModel;
    }

    /**
     * @return the mrPopupVerDetalleHotel
     */
    public boolean isMrPopupVerDetalleHotel() {
	return mrPopupVerDetalleHotel;
    }

    /**
     * @param mrPopupVerDetalleHotel the mrPopupVerDetalleHotel to set
     */
    public void setMrPopupVerDetalleHotel(boolean mrPopupVerDetalleHotel) {
	this.mrPopupVerDetalleHotel = mrPopupVerDetalleHotel;
    }

    /**
     * @return the sgHotel
     */
    public SgHotel getSgHotel() {
	return sgHotel;
    }

    /**
     * @param sgHotel the sgHotel to set
     */
    public void setSgHotel(SgHotel sgHotel) {
	this.sgHotel = sgHotel;
    }

    /**
     * @return the sgHotelSeleccionado
     */
    public SgHotel getSgHotelSeleccionado() {
	return sgHotelSeleccionado;
    }

    /**
     * @param sgHotelSeleccionado the sgHotelSeleccionado to set
     */
    public void setSgHotelSeleccionado(SgHotel sgHotelSeleccionado) {
	this.sgHotelSeleccionado = sgHotelSeleccionado;
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

    public List<String> traerProveedor() {
	return proveedorImpl.traerNombreProveedorQueryNativo(sesion.getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());
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
     * @return the operacion
     */
    public String getOperacion() {
	return operacion;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
	this.operacion = operacion;
    }

    /**
     * @return the mrPopupModificarHotel
     */
    public boolean isMrPopupModificarHotel() {
	return mrPopupModificarHotel;
    }

    /**
     * @param mrPopupModificarHotel the mrPopupModificarHotel to set
     */
    public void setMrPopupModificarHotel(boolean mrPopupModificarHotel) {
	this.mrPopupModificarHotel = mrPopupModificarHotel;
    }

    /**
     * @return the mrPopupAgregarTipoHabitacionHotel
     */
    public boolean isMrPopupAgregarTipoHabitacionHotel() {
	return mrPopupAgregarTipoHabitacionHotel;
    }

    /**
     * @param mrPopupAgregarTipoHabitacionHotel the
     * mrPopupAgregarTipoHabitacionHotel to set
     */
    public void setMrPopupAgregarTipoHabitacionHotel(boolean mrPopupAgregarTipoHabitacionHotel) {
	this.mrPopupAgregarTipoHabitacionHotel = mrPopupAgregarTipoHabitacionHotel;
    }

    /**
     * @return the tipoHabitacionModel
     */
    public DataModel getTipoHabitacionModel() {
	return tipoHabitacionModel;
    }

    /**
     * @param tipoHabitacionModel the tipoHabitacionModel to set
     */
    public void setTipoHabitacionModel(DataModel tipoHabitacionModel) {
	this.tipoHabitacionModel = tipoHabitacionModel;
    }

    /**
     * @return the mrPopupEliminarTipoHabitacionHotel
     */
    public boolean isMrPopupEliminarTipoHabitacionHotel() {
	return mrPopupEliminarTipoHabitacionHotel;
    }

    /**
     * @param mrPopupEliminarTipoHabitacionHotel the
     * mrPopupEliminarTipoHabitacionHotel to set
     */
    public void setMrPopupEliminarTipoHabitacionHotel(boolean mrPopupEliminarTipoHabitacionHotel) {
	this.mrPopupEliminarTipoHabitacionHotel = mrPopupEliminarTipoHabitacionHotel;
    }

    /**
     * @return the mrPopupModificarTipoHabitacionHotel
     */
    public boolean isMrPopupModificarTipoHabitacionHotel() {
	return mrPopupModificarTipoHabitacionHotel;
    }

    /**
     * @param mrPopupModificarTipoHabitacionHotel the
     * mrPopupModificarTipoHabitacionHotel to set
     */
    public void setMrPopupModificarTipoHabitacionHotel(boolean mrPopupModificarTipoHabitacionHotel) {
	this.mrPopupModificarTipoHabitacionHotel = mrPopupModificarTipoHabitacionHotel;
    }

    /**
     * @return the mrPopupAgregarHabitacionToHotel
     */
    public boolean isMrPopupAgregarHotelHabitacion() {
	return isMrPopupAgregarHabitacionToHotel();
    }

    public List<SgHotelHabitacion> getListHotelHabitacion() {
	return this.listHotelHabitacion;
    }

    /**
     * @param listHotelHabitacion the listHotelHabitacion to set
     */
    public void setListHotelHabitacion(List<SgHotelHabitacion> listHotelHabitacion) {
	this.listHotelHabitacion = listHotelHabitacion;
    }

    /**
     * @param listaTiposHabitacionItems the listaTiposHabitacionItems to set
     */
    public void setListaTiposHabitacion(List<SelectItem> listaTiposHabitacion) {
	this.setListaTiposHabitacionItems(listaTiposHabitacion);
    }

    /**
     * @return the listaTiposHabitacionItems
     */
    public List<SelectItem> getListaTiposHabitacionItems() {
	return listaTiposHabitacionItems;
    }

    /**
     * @param listaTiposHabitacionItems the listaTiposHabitacionItems to set
     */
    public void setListaTiposHabitacionItems(List<SelectItem> listaTiposHabitacionItems) {
	this.listaTiposHabitacionItems = listaTiposHabitacionItems;
    }

    /**
     * @return the idTipoEspecificoHabitacion
     */
    public int getIdTipoEspecificoHabitacion() {
	return idTipoEspecificoHabitacion;
    }

    /**
     * @param idTipoEspecificoHabitacion the idTipoEspecificoHabitacion to set
     */
    public void setIdTipoEspecificoHabitacion(int idTipoEspecificoHabitacion) {
	this.idTipoEspecificoHabitacion = idTipoEspecificoHabitacion;
    }

    /**
     * @return the sgHabitacion
     */
    public SgHotelHabitacion getSgHabitacion() {
	return sgHabitacion;
    }

    /**
     * @param sgHabitacion the sgHabitacion to set
     */
    public void setSgHabitacion(SgHotelHabitacion sgHabitacion) {
	this.sgHabitacion = sgHabitacion;
    }

    /**
     * @return the mrPopupAgregarHabitacionToHotel
     */
    public boolean isMrPopupAgregarHabitacionToHotel() {
	return mrPopupAgregarHabitacionToHotel;
    }

    /**
     * @param mrPopupAgregarHabitacionToHotel the
     * mrPopupAgregarHabitacionToHotel to set
     */
    public void setMrPopupAgregarHabitacionToHotel(boolean mrPopupAgregarHabitacionToHotel) {
	this.mrPopupAgregarHabitacionToHotel = mrPopupAgregarHabitacionToHotel;
    }

    /**
     * @return the mrPopupEliminarHabitacionToHotel
     */
    public boolean isMrPopupEliminarHabitacionToHotel() {
	return mrPopupEliminarHabitacionToHotel;
    }

    /**
     * @param mrPopupEliminarHabitacionToHotel the
     * mrPopupEliminarHabitacionToHotel to set
     */
    public void setMrPopupEliminarHabitacionToHotel(boolean mrPopupEliminarHabitacionToHotel) {
	this.mrPopupEliminarHabitacionToHotel = mrPopupEliminarHabitacionToHotel;
    }

    /**
     * @return the habitacionToHotelModel
     */
    public DataModel getHabitacionToHotelModel() {
	return habitacionToHotelModel;
    }

    /**
     * @param habitacionToHotelModel the habitacionToHotelModel to set
     */
    public void setHabitacionToHotelModel(DataModel habitacionToHotelModel) {
	this.habitacionToHotelModel = habitacionToHotelModel;
    }

    /**
     * @return the numeroEstrellas
     */
    public String getNumeroEstrellas() {
	return numeroEstrellas;
    }

    /**
     * @param numeroEstrellas the numeroEstrellas to set
     */
    public void setNumeroEstrellas(String numeroEstrellas) {
	this.numeroEstrellas = numeroEstrellas;
    }

    /**
     * @return the proveedorSeleccionado
     */
    public Proveedor getProveedorSeleccionado() {
	UtilLog4j.log.info(this, "getProveedorSeleccionado()");
	return proveedorSeleccionado;
    }

    /**
     * @param proveedorSeleccionado the proveedorSeleccionado to set
     */
    public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
	this.proveedorSeleccionado = proveedorSeleccionado;
    }

    /**
     * @return the mensajeError
     */
    public String getMensajeError() {
	return mensajeError;
    }

    /**
     * @param mensajeError the mensajeError to set
     */
    public void setMensajeError(String mensajeError) {
	this.mensajeError = mensajeError;
    }

    /**
     * @return the mrPopupAviso
     */
    public boolean isMrPopupAviso() {
	return mrPopupAviso;
    }

    /**
     * @param mrPopupAviso the mrPopupAviso to set
     */
    public void setMrPopupAviso(boolean mrPopupAviso) {
	this.mrPopupAviso = mrPopupAviso;
    }

    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
	return dataModel;
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
	this.dataModel = dataModel;
    }

    /**
     * @return the bigDecimal
     */
    public BigDecimal getBigDecimal() {
	return bigDecimal;
    }

    /**
     * @param bigDecimal the bigDecimal to set
     */
    public void setBigDecimal(BigDecimal bigDecimal) {
	this.bigDecimal = bigDecimal;
    }

    /**
     * @return the idServicio
     */
    public int getIdServicio() {
	return idServicio;
    }

    /**
     * @param idServicio the idServicio to set
     */
    public void setIdServicio(int idServicio) {
	this.idServicio = idServicio;
    }

    /**
     * @return the sgHotelTipoEspecificoVo
     */
    public SgHotelTipoEspecificoVo getSgHotelTipoEspecificoVo() {
	return sgHotelTipoEspecificoVo;
    }

    /**
     * @param sgHotelTipoEspecificoVo the sgHotelTipoEspecificoVo to set
     */
    public void setSgHotelTipoEspecificoVo(SgHotelTipoEspecificoVo sgHotelTipoEspecificoVo) {
	this.sgHotelTipoEspecificoVo = sgHotelTipoEspecificoVo;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
	return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
	this.flag = flag;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }
}
