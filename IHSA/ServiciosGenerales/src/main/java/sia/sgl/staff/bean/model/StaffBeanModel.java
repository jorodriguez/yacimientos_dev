/*
 * To change this template, choose Tools | Templates
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
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Convenio;
import sia.modelo.Proveedor;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgCaracteristicaCocina;
import sia.modelo.SgCaracteristicaGym;
import sia.modelo.SgCaracteristicaHabitacion;
import sia.modelo.SgCaracteristicaStaff;
import sia.modelo.SgCocina;
import sia.modelo.SgDireccion;
import sia.modelo.SgGym;
import sia.modelo.SgHistorialConvenioStaff;
import sia.modelo.SgStaff;
import sia.modelo.SgStaffHabitacion;
import sia.modelo.SiAdjunto;
import sia.modelo.SiPais;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgCaracteristicaImpl;
import sia.servicios.sgl.impl.SgHistorialConvenioStaffImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiPaisImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 */
@Named(value = "staffBeanModel")

public class StaffBeanModel implements Serializable {

    //Sistema
    @Inject
    private Sesion sesion;
    //EJB
    @Inject
    private SgStaffImpl staffService;
    @Inject
    private SgCaracteristicaImpl caracteristicaService;
    @Inject
    private SgHistorialConvenioStaffImpl historialConvenioService;
    @Inject
    private ConvenioImpl convenioService;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private SiPaisImpl siPaisImpl;
    //Variables
    private SgDireccion direccion;
    private SgStaff staff;
    private SgStaffHabitacion habitacion;
    private SgGym gimnasio;
    private SgCocina cocina;
    private SgCaracteristica caracteristica;
    private SgCaracteristicaStaff caracteristicaStaff;
    private SgCaracteristicaHabitacion caracteristicaHabitacion;
    private SgCaracteristicaGym caracteristicaGimnasio;
    private SgCaracteristicaCocina caracteristicaCocina;
    private SgHistorialConvenioStaff ultimoConvenioStaff; //ultimo convenio
    private SgHistorialConvenioStaff sgHistorialConvenioSelecionado;
    private Convenio convenioSeleccionado;
    private Proveedor proveedorSeleccionado;
    private Object object;
    private Integer cantidadCaracteristica = 1;
    private String caracteristicaAgregadaMensaje = "";
    private String nombreProveedorSeleccionado = "";
    private String url = Constantes.URL;
    private String prefijo = "";
    private List<SelectItem> listaProveedor;
    private List<SelectItem> caracteristicas;
    private List<SelectItem> matchesList;
    private List<String> listaProveedorBuscar;
    private DataModel dataModel;
    private DataModel habitacionesDataModel;
    private DataModel gimnasiosDataModel;
    private DataModel cocinasDataModel;
    private DataModel convenioStaff;
    private DataModel historialConvenioModel;
    private DataModel todoHistorialConvenioModel;
    private DataModel AdjuntoModel;
    private DataModel caracteristicasStaffDataModel;
    private DataModel caracteristicasHabitacionDataModel; //Para consulta
    private DataModel caracteristicasGimnasioDataModel; //Para consulta
    private DataModel caracteristicasCocinaDataModel; //Para consulta
    //Variables Popups
    private int idAdjunto;
    private String uuid;
    private int idPais;
    private boolean mrPopupCrearStaff = false;
    private boolean mrPopupModificarGeneralesStaff = false;
    private boolean mrPopupEliminarStaff = false;
    private boolean mrPopupAgregarHabitacion = false;
    private boolean mrPopupModificarHabitacion = false;
    private boolean mrPopupEliminarHabitacion = false;
    private boolean mrPopupAgregarGimnasio = false;
    private boolean mrPopupModificarGimnasio = false;
    private boolean mrPopupEliminarGimnasio = false;
    private boolean mrPopupAgregarCocina = false;
    private boolean mrPopupModificarCocina = false;
    private boolean mrPopupEliminarCocina = false;
    private boolean mrPopupAgregarCaracteristicaStaff = false;
    private boolean mrPopupCaracteristicasHabitacionStaff = false;
    private boolean mrPopupCaracteristicasGimnasioStaff = false;
    private boolean mrPopupCaracteristicasCocinaStaff = false;
    private boolean mrPopupDetalleHabitacionStaff = false;
    private boolean mrPopupDetalleGimnasioStaff = false;
    private boolean mrPopupDetalleCocinaStaff = false;
    private boolean mrPopupAgregarContrato = false;
    private boolean mrPopupEliminarContrato = false;
    private boolean mrPopupVerHistorialConvenios = false;
    private boolean mrPopupAbrirArchivo = false;
    private boolean mrPopupAbrirArchivoTodoHistorial = false;

    /**
     * Inicia la Conversacion para mostrar el Catálogo de Staff
     */
    public void beginConversationStaffCatalog() {

	this.dataModel = null;
	this.caracteristicasStaffDataModel = null;
	this.caracteristicasHabitacionDataModel = null;
	this.caracteristicasGimnasioDataModel = null;
	this.caracteristicasCocinaDataModel = null;
	this.habitacionesDataModel = null;
	this.gimnasiosDataModel = null;
	this.cocinasDataModel = null;
    }

    @PostConstruct
    public void iniciar() {
	beginConversationStaffCatalog();
    }

    /**
     * Devuelve todos los Staff que pertenecen a la Oficina actual, es decir, la
     * Oficina a la pertenece el Usuario que está en Sesión
     */
    public void getAllStaffByOficina() {
	this.dataModel = new ListDataModel(staffService.getAllStaffByStatusAndOficina(Constantes.NO_ELIMINADO, this.sesion.getOficinaActual().getId()));

    }

    public void controlaPopUp(String llave, boolean estado) {
	sesion.getControladorPopups().put(llave, estado);
    }

    public void getAllHabitacionesByStaff() {
	if (this.staff != null && this.habitacionesDataModel == null) {
	    this.habitacionesDataModel = new ListDataModel(staffService.getAllHabitacionesByStaff(this.staff, Constantes.NO_ELIMINADO));
	}
    }

    public void getAllGimnasiosByStaff() {
	if (this.staff != null && this.gimnasiosDataModel == null) {
	    this.gimnasiosDataModel = new ListDataModel(staffService.getAllGimnasiosByStaff(this.staff, Constantes.NO_ELIMINADO));
	}
    }

    public void getAllCocinasByStaff() {
	if (this.staff != null && this.cocinasDataModel == null) {
	    this.cocinasDataModel = new ListDataModel(staffService.getAllCocinasByStaff(this.staff, Constantes.NO_ELIMINADO));
	}
    }

    /**
     * Devuelve siempre todas las Características que no sean secundarias y no
     * estén eliminadas y las asigna a las lista de Características usada para
     * el autocomplete
     */
    public void getAllCaracteristicas() {
	try {
	    this.setCaracteristicas(new ArrayList<SelectItem>());
	    this.setMatchesList(new ArrayList<SelectItem>());
	    List<CaracteristicaVo> cars = this.caracteristicaService.getAllSgCaracteristicaStaffAndOficina();
	    if (cars != null && !cars.isEmpty()) {
		for (CaracteristicaVo c : cars) {
		    SelectItem si = new SelectItem(c.getNombre());
		    this.getCaracteristicas().add(si);
		}
	    }
	} catch (Exception e) {
	}
    }

    public void getAllCaracteristicasStaff() throws SIAException, Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.getCaracteristicasStaff()");
	if (this.staff != null && this.caracteristicasStaffDataModel == null) {
	    this.caracteristicasStaffDataModel = new ListDataModel(staffService.getAllCaracteristicasStaffList(this.staff.getId()));
	}
    }

    public void getAllCaracteristicasHabitacion() throws SIAException, Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.getAllCaracteristicasHabitacion()");
	if (this.habitacion != null && this.caracteristicasHabitacionDataModel == null) {
	    this.caracteristicasHabitacionDataModel = new ListDataModel(staffService.getAllCaracteristicasHabitacionList(this.habitacion.getId()));
	}
    }

    public void getAllCaracteristicasGimnasio() throws SIAException, Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.getAllCaracteristicasGimnasio()");
	if (this.gimnasio != null && this.caracteristicasGimnasioDataModel == null) {
	    this.caracteristicasGimnasioDataModel = new ListDataModel(staffService.getAllCaracteristicasGimnasioList(this.gimnasio.getId()));
	}
    }

    public void getAllCaracteristicasCocina() throws SIAException, Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.getAllCaracteristicasCocina()");
	if (this.cocina != null && this.caracteristicasCocinaDataModel == null) {
	    this.caracteristicasCocinaDataModel = new ListDataModel(staffService.getAllCaracteristicasCocinaList(this.cocina.getId()));
	}
    }

    //CONVENIO
    public DataModel getConveniosVigentePorProveedor() {
	if (!nombreProveedorSeleccionado.equals("")) {
	    convenioStaff = new ListDataModel(convenioService.getConveniosPorProveedor(nombreProveedorSeleccionado));
	    UtilLog4j.log.info(this, "Datamodel de proveedor asignado");
	    return getConvenioStaff();
	} else {
	    return null;
	}
    }

    public SgHistorialConvenioStaff buscarContratoVigente() {
	setUltimoConvenioStaff(historialConvenioService.traerContratoVigente(getStaff()));
	if (getUltimoConvenioStaff() != null) {
	    return getUltimoConvenioStaff();
	} else {
	    setUltimoConvenioStaff(null);
	}
	return getUltimoConvenioStaff();
    }

    public DataModel traerContratoStaff() {
	try {
	    if (getUltimoConvenioStaff() != null) {
		setConvenioStaff(new ListDataModel(siAdjuntoImpl.traerArchivos(6, getUltimoConvenioStaff().getConvenio().getId(), "Convenio")));
		return getConvenioStaff();
	    }
	} catch (Exception e) {
	    return null;
	}
	return null;
    }

//    public void getHistorialConvenioStaffVigente() {
//        UtilLog4j.log.info(this, "StaffBeanModel.getHistorialConvenioStaffVigente");
//        List<SgHistorialConvenioStaff> h = this.historialConvenioService.getHistorialConvenioStaffVigente(Constantes.BOOLEAN_TRUE, getStaff(), Constantes.BOOLEAN_FALSE);
//        if (!h.isEmpty()) {
//            setUltimoConvenioStaff(h.get(0));
//        }
//        ListDataModel<SgHistorialConvenioStaff> historialDm = new ListDataModel(h);
//        this.historialConvenioModel = (DataModel) historialDm;
//    }
    public void getTodoHistorialConvenioStaff() {
	List<SgHistorialConvenioStaff> h = this.historialConvenioService.getAllHistorialConvenioStaff(getStaff(), Constantes.BOOLEAN_FALSE);
	ListDataModel<SgHistorialConvenioStaff> historialDm = new ListDataModel(h);
	this.todoHistorialConvenioModel = (DataModel) historialDm;

    }

    public DataModel traerAdjuntoContrato() {
	try {
	    return new ListDataModel(siAdjuntoImpl.traerArchivos(6, getConvenioSeleccionado().getId(), "Convenio"));
	} catch (Exception e) {
	    return null;
	}
    }

    public SgHistorialConvenioStaff addContratoProveedor() throws Exception {

	return historialConvenioService.guardarConvenio(sesion.getUsuario(), getConvenioSeleccionado(), getStaff());
//        SgHistorialConvenioStaff hc = new SgHistorialConvenioStaff();
//        hc.setEliminado(Constantes.BOOLEAN_FALSE);
//        hc.setVigente(Constantes.BOOLEAN_TRUE);
//        hc.setFechaGenero(new Date());
//        hc.setHoraGenero(new Date());
//        hc.setGenero(sesion.getUsuario());
//        historialConvenioService.create(hc);
	//poner en vigente el ultimo
//        ultimoConvenioStaff.setVigente(Constantes.BOOLEAN_FALSE);
//        historialConvenioService.edit(ultimoConvenioStaff);
//        UtilLog4j.log.info(this, "StaffBeanModel.addContratoProveedor()");
    }

    public void quitarContratoVigente() {
	if (historialConvenioService.traerContratoVigente(getStaff()) != null) {
	    historialConvenioService.quitarContratoVigente(sesion.getUsuario(), getUltimoConvenioStaff(), getStaff());
	}
    }

    public List<SgHistorialConvenioStaff> buscarRelacionConvenio() {
	UtilLog4j.log.info(this, "1 model");
	return historialConvenioService.buscarRelacionConvenio(getStaff(), getConvenioSeleccionado());
    }

    public void createStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.crearStaff");
	staffService.createStaff(this.staff, this.sesion.getOficinaActual().getId(), this.direccion, sesion.getUsuario().getId(), Constantes.NO_ELIMINADO, getIdPais());
	//Recargar lista de Staff's
	this.dataModel = new ListDataModel(staffService.getAllStaffByStatusAndOficina(Constantes.NO_ELIMINADO, this.sesion.getOficinaActual().getId()));
    }

    public List<SelectItem> listaPais() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	try {
	    List<SiPais> lt = siPaisImpl.findAll("nombre", true, false);
	    for (SiPais siPais : lt) {
		SelectItem item = new SelectItem(siPais.getId(), siPais.getNombre());
		l.add(item);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Fui a ver que tenia la excepción de traer paises");
	}
	return l;
    }

    public void updateStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.updateStaff()");
	staffService.updateStaff(this.staff, this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO, getIdPais());
	reloadStaff();
    }

    public void updateHabitacionStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.updateHabitacionStaff()");
	staffService.updateHabitacionStaff(this.habitacion, this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
    }

    public void updateGimnasioStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.updateGimnasioStaff()");
	staffService.updateGimnasioStaff(this.gimnasio, this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
    }

    public void updateCocinaStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.updateCocinaStaff()");
	staffService.updateCocinaStaff(this.cocina, this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
    }

    public void reloadStaff() {
//        UtilLog4j.log.info(this, "StaffBeanModel.reloadStaff()");
	this.staff = staffService.find(this.staff.getId());
    }

    public void addHabitacionToStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.addHabitacionToStaff()");
	staffService.addHabitacionToStaff(this.staff, this.habitacion, this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
	//Recargar la lista de Habitaciones
	this.habitacionesDataModel = new ListDataModel(staffService.getAllHabitacionesByStaff(this.staff, Constantes.NO_ELIMINADO));
    }

    public void addGimnasioToStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.addGimnasioToStaff()");
	staffService.addGimnasioToStaff(this.staff, this.gimnasio, this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
	//Recargar la lista de Gimnasios
	this.gimnasiosDataModel = new ListDataModel(staffService.getAllGimnasiosByStaff(this.staff, Constantes.NO_ELIMINADO));
    }

    public void addCocinaToStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.addCocinaToStaff()");
	staffService.addCocinaToStaff(this.staff, this.cocina, this.sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
	//Recargar la lista de Cocinas
	this.cocinasDataModel = new ListDataModel(staffService.getAllCocinasByStaff(this.staff, Constantes.NO_ELIMINADO));
    }

    public void addCaracteristica() throws SIAException, Exception {
	UtilLog4j.log.info(this, "StaffBeanModel.addCaracteristica()");
	if (this.object instanceof SgCaracteristicaStaff) {
	    SgCaracteristica car = staffService.addCaracteristica(this.object, this.staff, this.prefijo, this.cantidadCaracteristica, sesion.getUsuario().getId());
	    //Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
	    if (car != null) {
		this.caracteristicas.add(new SelectItem(car.getNombre()));
	    }
	    //Recargar DataModel que muestra las relaciones
	    this.caracteristicasStaffDataModel = new ListDataModel(staffService.getAllCaracteristicasStaffList(this.staff.getId()));
	}
	if (this.object instanceof SgCaracteristicaHabitacion) {
	    SgCaracteristica car = staffService.addCaracteristica(this.object, this.habitacion, this.prefijo, this.cantidadCaracteristica, sesion.getUsuario().getId());
	    //Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
	    if (car != null) {
		this.caracteristicas.add(new SelectItem(car.getNombre()));
	    }
	    //Recargar DataModel que muestra las relaciones
	    this.caracteristicasHabitacionDataModel = new ListDataModel(staffService.getAllCaracteristicasHabitacionList(this.habitacion.getId()));
	}
	if (this.object instanceof SgCaracteristicaGym) {
	    SgCaracteristica car = staffService.addCaracteristica(this.object, this.gimnasio, this.prefijo, this.cantidadCaracteristica, sesion.getUsuario().getId());
	    //Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
	    if (car != null) {
		this.caracteristicas.add(new SelectItem(car.getNombre()));
	    }
	    //Recargar DataModel que muestra las relaciones
	    this.caracteristicasGimnasioDataModel = new ListDataModel(staffService.getAllCaracteristicasGimnasioList(this.gimnasio.getId()));
	}
	if (this.object instanceof SgCaracteristicaCocina) {
	    SgCaracteristica car = staffService.addCaracteristica(this.object, this.cocina, this.prefijo, this.cantidadCaracteristica, sesion.getUsuario().getId());
	    //Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
	    if (car != null) {
		this.caracteristicas.add(new SelectItem(car.getNombre()));
	    }
	    //Recargar DataModel que muestra las relaciones
	    this.caracteristicasCocinaDataModel = new ListDataModel(staffService.getAllCaracteristicasCocinaList(this.cocina.getId()));
	}
    }

    /**
     * Quita una relación entre una Característica y un área (SgStaff,
     * SgStaffHabitacion, SgGym, SgCocina)
     *
     * @param o
     * @throws SIAException
     * @throws Exception
     */
    public void removeCaracteristica(Object o) throws SIAException, Exception {
	UtilLog4j.log.info(this, "StaffBeanModel.removeCaracteristica()");
	if (o instanceof SgCaracteristicaStaff) {
	    staffService.removeCaracteristica(this.caracteristicaStaff, sesion.getUsuario().getId());
	    this.caracteristicasStaffDataModel = new ListDataModel(staffService.getAllCaracteristicasStaffList(this.staff.getId()));
	}
	if (o instanceof SgCaracteristicaHabitacion) {
	    staffService.removeCaracteristica(this.caracteristicaHabitacion, sesion.getUsuario().getId());
	    this.caracteristicasHabitacionDataModel = new ListDataModel(staffService.getAllCaracteristicasHabitacionList(this.habitacion.getId()));
	}
	if (o instanceof SgCaracteristicaGym) {
	    staffService.removeCaracteristica(this.caracteristicaGimnasio, sesion.getUsuario().getId());
	    this.caracteristicasGimnasioDataModel = new ListDataModel(staffService.getAllCaracteristicasGimnasioList(this.gimnasio.getId()));
	}
	if (o instanceof SgCaracteristicaCocina) {
	    staffService.removeCaracteristica(this.caracteristicaCocina, sesion.getUsuario().getId());
	    this.caracteristicasCocinaDataModel = new ListDataModel(staffService.getAllCaracteristicasCocinaList(this.cocina.getId()));
	}
    }

    public void deleteHabitacionStaff() throws SIAException, Exception {
	staffService.deleteHabitacionStaff(this.habitacion, this.sesion.getUsuario().getId(), getIdPais());
	//Recargar la lista de Habitaciones
	this.habitacionesDataModel = new ListDataModel(staffService.getAllHabitacionesByStaff(this.staff, Constantes.NO_ELIMINADO));
    }

    public void deleteGimnasioStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.deleteGimnasioStaff()");
	staffService.updateGimnasioStaff(this.gimnasio, this.sesion.getUsuario().getId(), Constantes.ELIMINADO);
	//Recargar la lista de Gimnasios
	this.gimnasiosDataModel = new ListDataModel(staffService.getAllGimnasiosByStaff(this.staff, Constantes.NO_ELIMINADO));
    }

    public void deleteCocinaStaff() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.deleteCocinaStaff()");
	staffService.updateCocinaStaff(this.cocina, this.sesion.getUsuario().getId(), Constantes.ELIMINADO);
	//Recargar la lista de Cocinas
	this.cocinasDataModel = new ListDataModel(staffService.getAllCocinasByStaff(this.staff, Constantes.NO_ELIMINADO));
    }

    public void deleteStaff() throws SIAException, Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.deleteStaff()");
	staffService.deleteStaff(this.staff, this.sesion.getUsuario().getId(), Constantes.ELIMINADO);
	//Recargar lista de Staff's
	this.dataModel = new ListDataModel(staffService.getAllStaffByStatusAndOficina(Constantes.NO_ELIMINADO, this.sesion.getOficinaActual().getId()));
    }

    public void deleteRelacionConvenio() throws Exception {
//        UtilLog4j.log.info(this, "StaffBeanModel.deleteRelacionConvenio");
	historialConvenioService.eliminarRelacion(getUltimoConvenioStaff(), sesion.getUsuario());
	setUltimoConvenioStaff(null);
    }

    public void buscarAdjuntoConvenio() {
	// List<SiAdjunto> list = siAdjuntoImpl.traerArchivos(6, getSgHistorialConvenioOficina().getConvenio().getId(), "Convenio");
	List<SiAdjunto> list = siAdjuntoImpl.traerArchivos(6, getConvenioSeleccionado().getId(), "Convenio");
	if (!list.isEmpty()) {
	    SiAdjunto siAdjunto;
	    siAdjunto = list.get(0);
	    setIdAdjunto(siAdjunto.getId());
	}
	//      getUrl().concat(String.valueOf(getIdAdjunto()));
    }

    public DataModel traerArchivoConvenioStaffHistorial() {
	try {
	    setAdjuntoModel(new ListDataModel(siAdjuntoImpl.traerArchivos(6, getConvenioSeleccionado().getId(), "Convenio")));
	    return getAdjuntoModel();
	} catch (Exception e) {
	    return null;
	}
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
     * @return the staff
     */
    public SgStaff getStaff() {
	return staff;
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(SgStaff staff) {
	this.staff = staff;
    }

    /**
     * @return the direccion
     */
    public SgDireccion getDireccion() {
	return direccion;
    }

    /**
     * @param direccion the direccion to set
     */
    public void setDireccion(SgDireccion direccion) {
	this.direccion = direccion;
    }

    /**
     * @return the habitacion
     */
    public SgStaffHabitacion getHabitacion() {
	return habitacion;
    }

    /**
     * @param habitacion the habitacion to set
     */
    public void setHabitacion(SgStaffHabitacion habitacion) {
	this.habitacion = habitacion;
    }

    /**
     * @return the gimnasio
     */
    public SgGym getGimnasio() {
	return gimnasio;
    }

    /**
     * @param gimnasio the gimnasio to set
     */
    public void setGimnasio(SgGym gimnasio) {
	this.gimnasio = gimnasio;
    }

    /**
     * @return the caracteristica
     */
    public SgCaracteristica getCaracteristica() {
	return caracteristica;
    }

    /**
     * @param caracteristica the caracteristica to set
     */
    public void setCaracteristica(SgCaracteristica caracteristica) {
	this.caracteristica = caracteristica;
    }

    /**
     * @return the cocina
     */
    public SgCocina getCocina() {
	return cocina;
    }

    /**
     * @param cocina the cocina to set
     */
    public void setCocina(SgCocina cocina) {
	this.cocina = cocina;
    }

    /**
     * @return the caracteristicaAgregadaMensaje
     */
    public String getCaracteristicaAgregadaMensaje() {
	return caracteristicaAgregadaMensaje;
    }

    /**
     * @param caracteristicaAgregadaMensaje the caracteristicaAgregadaMensaje to
     * set
     */
    public void setCaracteristicaAgregadaMensaje(String caracteristicaAgregadaMensaje) {
	this.caracteristicaAgregadaMensaje = caracteristicaAgregadaMensaje;
    }

    /**
     * @return the habitacionesDataModel
     */
    public DataModel getHabitacionesDataModel() {
	return habitacionesDataModel;
    }

    /**
     * @param habitacionesDataModel the habitacionesDataModel to set
     */
    public void setHabitacionesDataModel(DataModel habitacionesDataModel) {
	this.habitacionesDataModel = habitacionesDataModel;
    }

    /**
     * @return the gimnasiosDataModel
     */
    public DataModel getGimnasiosDataModel() {
	return gimnasiosDataModel;
    }

    /**
     * @param gimnasiosDataModel the gimnasiosDataModel to set
     */
    public void setGimnasiosDataModel(DataModel gimnasiosDataModel) {
	this.gimnasiosDataModel = gimnasiosDataModel;
    }

    /**
     * @return the caracteristicasStaffDataModel
     */
    public DataModel getCaracteristicasStaffDataModel() {
	return caracteristicasStaffDataModel;
    }

    /**
     * @param caracteristicasStaffDataModel the caracteristicasStaffDataModel to
     * set
     */
    public void setCaracteristicasStaffDataModel(DataModel caracteristicasStaffDataModel) {
	this.caracteristicasStaffDataModel = caracteristicasStaffDataModel;
    }

    /**
     * @return the cocinasDataModel
     */
    public DataModel getCocinasDataModel() {
	return cocinasDataModel;
    }

    /**
     * @param cocinasDataModel the cocinasDataModel to set
     */
    public void setCocinasDataModel(DataModel cocinasDataModel) {
	this.cocinasDataModel = cocinasDataModel;
    }

    /**
     * @return the caracteristicasHabitacionDataModel
     */
    public DataModel getCaracteristicasHabitacionDataModel() {
	return caracteristicasHabitacionDataModel;
    }

    /**
     * @param caracteristicasHabitacionDataModel the
     * caracteristicasHabitacionDataModel to set
     */
    public void setCaracteristicasHabitacionDataModel(DataModel caracteristicasHabitacionDataModel) {
	this.caracteristicasHabitacionDataModel = caracteristicasHabitacionDataModel;
    }

    /**
     * @return the caracteristicasGimnasioDataModel
     */
    public DataModel getCaracteristicasGimnasioDataModel() {
	return caracteristicasGimnasioDataModel;
    }

    /**
     * @param caracteristicasGimnasioDataModel the
     * caracteristicasGimnasioDataModel to set
     */
    public void setCaracteristicasGimnasioDataModel(DataModel caracteristicasGimnasioDataModel) {
	this.caracteristicasGimnasioDataModel = caracteristicasGimnasioDataModel;
    }

    /**
     * @return the caracteristicasCocinaDataModel
     */
    public DataModel getCaracteristicasCocinaDataModel() {
	return caracteristicasCocinaDataModel;
    }

    /**
     * @param caracteristicasCocinaDataModel the caracteristicasCocinaDataModel
     * to set
     */
    public void setCaracteristicasCocinaDataModel(DataModel caracteristicasCocinaDataModel) {
	this.caracteristicasCocinaDataModel = caracteristicasCocinaDataModel;
    }

    /**
     * @return the mrPopupCrearStaff
     */
    public boolean isMrPopupCrearStaff() {
	return mrPopupCrearStaff;
    }

    /**
     * @param mrPopupCrearStaff the mrPopupCrearStaff to set
     */
    public void setMrPopupCrearStaff(boolean mrPopupCrearStaff) {
	this.mrPopupCrearStaff = mrPopupCrearStaff;
    }

    /**
     * @return the mrPopupEliminarStaff
     */
    public boolean isMrPopupEliminarStaff() {
	return mrPopupEliminarStaff;
    }

    /**
     * @param mrPopupEliminarStaff the mrPopupEliminarStaff to set
     */
    public void setMrPopupEliminarStaff(boolean mrPopupEliminarStaff) {
	this.mrPopupEliminarStaff = mrPopupEliminarStaff;
    }

    /**
     * @return the mrPopoupModificarGeneralesStaff
     */
    public boolean isMrPopupModificarGeneralesStaff() {
	return mrPopupModificarGeneralesStaff;
    }

    /**
     * @param mrPopoupModificarGeneralesStaff the
     * mrPopoupModificarGeneralesStaff to set
     */
    public void setMrPopupModificarGeneralesStaff(boolean mrPopupModificarGeneralesStaff) {
	this.mrPopupModificarGeneralesStaff = mrPopupModificarGeneralesStaff;
    }

    /**
     * @return the mrPopupAgregarHabitacion
     */
    public boolean isMrPopupAgregarHabitacion() {
	return mrPopupAgregarHabitacion;
    }

    /**
     * @param mrPopupAgregarHabitacion the mrPopupAgregarHabitacion to set
     */
    public void setMrPopupAgregarHabitacion(boolean mrPopupAgregarHabitacion) {
	this.mrPopupAgregarHabitacion = mrPopupAgregarHabitacion;
    }

    /**
     * @return the mrPopupModificarHabitacion
     */
    public boolean isMrPopupModificarHabitacion() {
	return mrPopupModificarHabitacion;
    }

    /**
     * @param mrPopupModificarHabitacion the mrPopupModificarHabitacion to set
     */
    public void setMrPopupModificarHabitacion(boolean mrPopupModificarHabitacion) {
	this.mrPopupModificarHabitacion = mrPopupModificarHabitacion;
    }

    /**
     * @return the mrPopupEliminarHabitacion
     */
    public boolean isMrPopupEliminarHabitacion() {
	return mrPopupEliminarHabitacion;
    }

    /**
     * @param mrPopupEliminarHabitacion the mrPopupEliminarHabitacion to set
     */
    public void setMrPopupEliminarHabitacion(boolean mrPopupEliminarHabitacion) {
	this.mrPopupEliminarHabitacion = mrPopupEliminarHabitacion;
    }

    /**
     * @return the mrPopUpAgregarGimnasio
     */
    public boolean isMrPopupAgregarGimnasio() {
	return mrPopupAgregarGimnasio;
    }

    /**
     * @param mrPopUpAgregarGimnasio the mrPopUpAgregarGimnasio to set
     */
    public void setMrPopupAgregarGimnasio(boolean mrPopupAgregarGimnasio) {
	this.mrPopupAgregarGimnasio = mrPopupAgregarGimnasio;
    }

    /**
     * @return the mrPopUpModificarGimnasio
     */
    public boolean isMrPopupModificarGimnasio() {
	return mrPopupModificarGimnasio;
    }

    /**
     * @param mrPopUpModificarGimnasio the mrPopUpModificarGimnasio to set
     */
    public void setMrPopupModificarGimnasio(boolean mrPopupModificarGimnasio) {
	this.mrPopupModificarGimnasio = mrPopupModificarGimnasio;
    }

    /**
     * @return the mrPopupEliminarGimnaio
     */
    public boolean isMrPopupEliminarGimnasio() {
	return mrPopupEliminarGimnasio;
    }

    /**
     * @param mrPopupEliminarGimnaio the mrPopupEliminarGimnaio to set
     */
    public void setMrPopupEliminarGimnasio(boolean mrPopupEliminarGimnasio) {
	this.mrPopupEliminarGimnasio = mrPopupEliminarGimnasio;
    }

    /**
     * @return the mrPopupAgregarCaracteristicaStaff
     */
    public boolean isMrPopupAgregarCaracteristicaStaff() {
	return mrPopupAgregarCaracteristicaStaff;
    }

    /**
     * @param mrPopupAgregarCaracteristicaStaff the
     * mrPopupAgregarCaracteristicaStaff to set
     */
    public void setMrPopupAgregarCaracteristicaStaff(boolean mrPopupAgregarCaracteristicaStaff) {
	this.mrPopupAgregarCaracteristicaStaff = mrPopupAgregarCaracteristicaStaff;
    }

    /**
     * @return the mrPopupAgregarCocina
     */
    public boolean isMrPopupAgregarCocina() {
	return mrPopupAgregarCocina;
    }

    /**
     * @param mrPopupAgregarCocina the mrPopupAgregarCocina to set
     */
    public void setMrPopupAgregarCocina(boolean mrPopupAgregarCocina) {
	this.mrPopupAgregarCocina = mrPopupAgregarCocina;
    }

    /**
     * @return the mrPopupModificarCocina
     */
    public boolean isMrPopupModificarCocina() {
	return mrPopupModificarCocina;
    }

    /**
     * @param mrPopupModificarCocina the mrPopupModificarCocina to set
     */
    public void setMrPopupModificarCocina(boolean mrPopupModificarCocina) {
	this.mrPopupModificarCocina = mrPopupModificarCocina;
    }

    /**
     * @return the mrPopupEliminarCocina
     */
    public boolean isMrPopupEliminarCocina() {
	return mrPopupEliminarCocina;
    }

    /**
     * @param mrPopupEliminarCocina the mrPopupEliminarCocina to set
     */
    public void setMrPopupEliminarCocina(boolean mrPopupEliminarCocina) {
	this.mrPopupEliminarCocina = mrPopupEliminarCocina;
    }

    /**
     * @return the mrPopupDetalleHabitacionStaff
     */
    public boolean isMrPopupDetalleHabitacionStaff() {
	return mrPopupDetalleHabitacionStaff;
    }

    /**
     * @param mrPopupDetalleHabitacionStaff the mrPopupDetalleHabitacionStaff to
     * set
     */
    public void setMrPopupDetalleHabitacionStaff(boolean mrPopupDetalleHabitacionStaff) {
	this.mrPopupDetalleHabitacionStaff = mrPopupDetalleHabitacionStaff;
    }

    /**
     * @return the mrPopupDetalleGimnasioStaff
     */
    public boolean isMrPopupDetalleGimnasioStaff() {
	return mrPopupDetalleGimnasioStaff;
    }

    /**
     * @param mrPopupDetalleGimnasioStaff the mrPopupDetalleGimnasioStaff to set
     */
    public void setMrPopupDetalleGimnasioStaff(boolean mrPopupDetalleGimnasioStaff) {
	this.mrPopupDetalleGimnasioStaff = mrPopupDetalleGimnasioStaff;
    }

    /**
     * @return the mrPopupDetalleCocinaStaff
     */
    public boolean isMrPopupDetalleCocinaStaff() {
	return mrPopupDetalleCocinaStaff;
    }

    /**
     * @param mrPopupDetalleCocinaStaff the mrPopupDetalleCocinaStaff to set
     */
    public void setMrPopupDetalleCocinaStaff(boolean mrPopupDetalleCocinaStaff) {
	this.mrPopupDetalleCocinaStaff = mrPopupDetalleCocinaStaff;
    }

    /**
     * @return the mrPopupCaracteristicasHabitacionStaff
     */
    public boolean isMrPopupCaracteristicasHabitacionStaff() {
	return mrPopupCaracteristicasHabitacionStaff;
    }

    /**
     * @param mrPopupCaracteristicasHabitacionStaff the
     * mrPopupCaracteristicasHabitacionStaff to set
     */
    public void setMrPopupCaracteristicasHabitacionStaff(boolean mrPopupCaracteristicasHabitacionStaff) {
	this.mrPopupCaracteristicasHabitacionStaff = mrPopupCaracteristicasHabitacionStaff;
    }

    /**
     * @return the mrPopupCaracteristicasGimnasioStaff
     */
    public boolean isMrPopupCaracteristicasGimnasioStaff() {
	return mrPopupCaracteristicasGimnasioStaff;
    }

    /**
     * @param mrPopupCaracteristicasGimnasioStaff the
     * mrPopupCaracteristicasGimnasioStaff to set
     */
    public void setMrPopupCaracteristicasGimnasioStaff(boolean mrPopupCaracteristicasGimnasioStaff) {
	this.mrPopupCaracteristicasGimnasioStaff = mrPopupCaracteristicasGimnasioStaff;
    }

    /**
     * @return the mrPopupCaracteristicasCocinaStaff
     */
    public boolean isMrPopupCaracteristicasCocinaStaff() {
	return mrPopupCaracteristicasCocinaStaff;
    }

    /**
     * @param mrPopupCaracteristicasCocinaStaff the
     * mrPopupCaracteristicasCocinaStaff to set
     */
    public void setMrPopupCaracteristicasCocinaStaff(boolean mrPopupCaracteristicasCocinaStaff) {
	this.mrPopupCaracteristicasCocinaStaff = mrPopupCaracteristicasCocinaStaff;
    }

    /**
     * @return the mrPopupAgregarContrato
     */
    public boolean isMrPopupAgregarContrato() {
	return mrPopupAgregarContrato;
    }

    /**
     * @param mrPopupAgregarContrato the mrPopupAgregarContrato to set
     */
    public void setMrPopupAgregarContrato(boolean mrPopupAgregarContrato) {
	this.mrPopupAgregarContrato = mrPopupAgregarContrato;
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
     * @return the nombreProveedorSeleccionado
     */
    public String getNombreProveedorSeleccionado() {
	return nombreProveedorSeleccionado;
    }

    /**
     * @param nombreProveedorSeleccionado the nombreProveedorSeleccionado to set
     */
    public void setNombreProveedorSeleccionado(String nombreProveedorSeleccionado) {
	this.nombreProveedorSeleccionado = nombreProveedorSeleccionado;
    }

    /**
     * @return the ultimoConvenioStaff
     */
    public SgHistorialConvenioStaff getUltimoConvenioStaff() {
	return ultimoConvenioStaff;
    }

    /**
     * @param ultimoConvenioStaff the ultimoConvenioStaff to set
     */
    public void setUltimoConvenioStaff(SgHistorialConvenioStaff ultimoConvenioStaff) {
	this.ultimoConvenioStaff = ultimoConvenioStaff;
    }

    /**
     * @return the convenioSeleccionado
     */
    public Convenio getConvenioSeleccionado() {
	return convenioSeleccionado;
    }

    /**
     * @param convenioSeleccionado the convenioSeleccionado to set
     */
    public void setConvenioSeleccionado(Convenio convenioSeleccionado) {
	this.convenioSeleccionado = convenioSeleccionado;
    }

    /**
     * @return the historialConvenioModel
     */
    public DataModel getHistorialConvenioModel() {
	return historialConvenioModel;
    }

    /**
     * @param historialConvenioModel the historialConvenioModel to set
     */
    public void setHistorialConvenioModel(DataModel historialConvenioModel) {
	this.historialConvenioModel = historialConvenioModel;
    }

    /**
     * @return the mrPopupEliminarContrato
     */
    public boolean isMrPopupEliminarContrato() {
	return mrPopupEliminarContrato;
    }

    /**
     * @param mrPopupEliminarContrato the mrPopupEliminarContrato to set
     */
    public void setMrPopupEliminarContrato(boolean mrPopupEliminarContrato) {
	this.mrPopupEliminarContrato = mrPopupEliminarContrato;
    }

    /**
     * @return the todoHistorialConvenioModel
     */
    public DataModel getTodoHistorialConvenioModel() {
	return todoHistorialConvenioModel;
    }

    /**
     * @param todoHistorialConvenioModel the todoHistorialConvenioModel to set
     */
    public void setTodoHistorialConvenioModel(DataModel todoHistorialConvenioModel) {
	this.todoHistorialConvenioModel = todoHistorialConvenioModel;
    }

    /**
     * @return the mrPopupVerHistorialConvenios
     */
    public boolean isMrPopupVerHistorialConvenios() {
	return mrPopupVerHistorialConvenios;
    }

    /**
     * @param mrPopupVerHistorialConvenios the mrPopupVerHistorialConvenios to
     * set
     */
    public void setMrPopupVerHistorialConvenios(boolean mrPopupVerHistorialConvenios) {
	this.mrPopupVerHistorialConvenios = mrPopupVerHistorialConvenios;
    }

    /**
     * @return the sgHistorialConvenioSelecionado
     */
    public SgHistorialConvenioStaff getSgHistorialConvenioSelecionado() {
	return sgHistorialConvenioSelecionado;
    }

    /**
     * @param sgHistorialConvenioSelecionado the sgHistorialConvenioSelecionado
     * to set
     */
    public void setSgHistorialConvenioSelecionado(SgHistorialConvenioStaff sgHistorialConvenioSelecionado) {
	this.sgHistorialConvenioSelecionado = sgHistorialConvenioSelecionado;
    }

    /**
     * @return the url
     */
    public String getUrl() {
	return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
	this.url = url;
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
     * @return the mrPopupAbrirArchivo
     */
    public boolean isMrPopupAbrirArchivo() {
	return mrPopupAbrirArchivo;
    }

    /**
     * @param mrPopupAbrirArchivo the mrPopupAbrirArchivo to set
     */
    public void setMrPopupAbrirArchivo(boolean mrPopupAbrirArchivo) {
	this.mrPopupAbrirArchivo = mrPopupAbrirArchivo;
    }

    /**
     * @return the AdjuntoModel
     */
    public DataModel getAdjuntoModel() {
	return AdjuntoModel;
    }

    /**
     * @param AdjuntoModel the AdjuntoModel to set
     */
    public void setAdjuntoModel(DataModel AdjuntoModel) {
	this.AdjuntoModel = AdjuntoModel;
    }

    /**
     * @return the mrPopupAbrirArchivoTodoHistorial
     */
    public boolean isMrPopupAbrirArchivoTodoHistorial() {
	return mrPopupAbrirArchivoTodoHistorial;
    }

    /**
     * @param mrPopupAbrirArchivoTodoHistorial the
     * mrPopupAbrirArchivoTodoHistorial to set
     */
    public void setMrPopupAbrirArchivoTodoHistorial(boolean mrPopupAbrirArchivoTodoHistorial) {
	this.mrPopupAbrirArchivoTodoHistorial = mrPopupAbrirArchivoTodoHistorial;
    }

    /**
     * @return the cantidadCaracteristica
     */
    public Integer getCantidadCaracteristica() {
	return cantidadCaracteristica;
    }

    /**
     * @param cantidadCaracteristica the cantidadCaracteristica to set
     */
    public void setCantidadCaracteristica(Integer cantidadCaracteristica) {
	this.cantidadCaracteristica = cantidadCaracteristica;
    }

    /**
     * @return the prefijo
     */
    public String getPrefijo() {
	return prefijo;
    }

    /**
     * @param prefijo the prefijo to set
     */
    public void setPrefijo(String prefijo) {
	this.prefijo = prefijo;
    }

    /**
     * @return the caracteristicaStaff
     */
    public SgCaracteristicaStaff getCaracteristicaStaff() {
	return caracteristicaStaff;
    }

    /**
     * @param caracteristicaStaff the caracteristicaStaff to set
     */
    public void setCaracteristicaStaff(SgCaracteristicaStaff caracteristicaStaff) {
	this.caracteristicaStaff = caracteristicaStaff;
    }

    /**
     * @return the caracteristicaHabitacion
     */
    public SgCaracteristicaHabitacion getCaracteristicaHabitacion() {
	return caracteristicaHabitacion;
    }

    /**
     * @param caracteristicaHabitacion the caracteristicaHabitacion to set
     */
    public void setCaracteristicaHabitacion(SgCaracteristicaHabitacion caracteristicaHabitacion) {
	this.caracteristicaHabitacion = caracteristicaHabitacion;
    }

    /**
     * @return the convenioStaff
     */
    public DataModel getConvenioStaff() {
	return convenioStaff;
    }

    /**
     * @param convenioStaff the convenioStaff to set
     */
    public void setConvenioStaff(DataModel convenioStaff) {
	this.convenioStaff = convenioStaff;
    }

    /**
     * @return the caracteristicaGimnasio
     */
    public SgCaracteristicaGym getCaracteristicaGimnasio() {
	return caracteristicaGimnasio;
    }

    /**
     * @param caracteristicaGimnasio the caracteristicaGimnasio to set
     */
    public void setCaracteristicaGimnasio(SgCaracteristicaGym caracteristicaGimnasio) {
	this.caracteristicaGimnasio = caracteristicaGimnasio;
    }

    /**
     * @return the caracteristicaCocina
     */
    public SgCaracteristicaCocina getCaracteristicaCocina() {
	return caracteristicaCocina;
    }

    /**
     * @param caracteristicaCocina the caracteristicaCocina to set
     */
    public void setCaracteristicaCocina(SgCaracteristicaCocina caracteristicaCocina) {
	this.caracteristicaCocina = caracteristicaCocina;
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
     * @return the object
     */
    public Object getObject() {
	return object;
    }

    /**
     * @param object the object to set
     */
    public void setObject(Object object) {
	this.object = object;
    }

    /**
     * @return the caracteristicas
     */
    public List<SelectItem> getCaracteristicas() {
	return caracteristicas;
    }

    /**
     * @param caracteristicas the caracteristicas to set
     */
    public void setCaracteristicas(List<SelectItem> caracteristicas) {
	this.caracteristicas = caracteristicas;
    }

    /**
     * @return the matchesList
     */
    public List<SelectItem> getMatchesList() {
	return matchesList;
    }

    /**
     * @param matchesList the matchesList to set
     */
    public void setMatchesList(List<SelectItem> matchesList) {
	this.matchesList = matchesList;
    }

    /**
     * @return the idPais
     */
    public int getIdPais() {
	return idPais;
    }

    /**
     * @param idPais the idPais to set
     */
    public void setIdPais(int idPais) {
	this.idPais = idPais;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
	return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
	this.uuid = uuid;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

}
