/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.hotel.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.excepciones.ExistingItemException;
import sia.excepciones.SIAException;
import sia.modelo.SgHotel;
import sia.modelo.SgHotelHabitacion;
import sia.modelo.SgHotelTipoEspecifico;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.sgl.hotel.vo.SgHotelTipoEspecificoVo;
import sia.sgl.hotel.bean.model.HotelBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 * @modify mluis
 */
@Named(value = "hotelBean")
@RequestScoped
public class HotelBean implements Serializable {

    //ManagedBeans
    @ManagedProperty(value = "#{hotelBeanModel}")
    private HotelBeanModel hotelBeanModel;

    public HotelBean() {
    }

    public void buscarObjetoProveedor(ActionEvent event) {
	try {
	    UtilLog4j.log.info(this, "buscarObjetoProveedor");
	    if (hotelBeanModel.traerProveedorPorNombre() == null) {
		UtilLog4j.log.fatal(this, "El proveedor es null");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion en buscarObjetoProveedor " + e.getMessage());
	}
    }

    /**
     * Consultas
     *
     * @param event
     */
    public void traerHoteles(ActionEvent event) {
	UtilLog4j.log.fatal(this, "HotelBean.traerHoteles");
	hotelBeanModel.traerHotelesModel();
    }

    /**
     * Datamodel's
     *
     *
     * @return
     */
    public DataModel getAllHotelModel() {
	hotelBeanModel.traerHotelesModel();
	return hotelBeanModel.getHotelModel();
    }

    public DataModel getAllHabitacionToHotelModel() {
	return hotelBeanModel.getHabitacionToHotelModel();
    }

    public List<SelectItem> getTraerTiposHabitacionItems() {
	UtilLog4j.log.fatal(this, "HotelBean.getTraerTiposHabitacionItems");
	this.hotelBeanModel.traerTipoEspecificoPorTipoHabitacionItems();
	return this.hotelBeanModel.getListaTiposHabitacionItems();
    }

    public List<SelectItem> getAllServiciosHotelSelectItem() {
	List<SgTipoTipoEspecifico> serviciosList = this.hotelBeanModel.getAllServiciosSgHotel();
	List<SelectItem> list = new ArrayList<SelectItem>();

	for (SgTipoTipoEspecifico tte : serviciosList) {
	    SelectItem item = new SelectItem(tte.getSgTipoEspecifico().getId(), tte.getSgTipoEspecifico().getNombre());
	    list.add(item);
	}
	return list;
    }

    public String crearHotel() {
	String ret = "";
	try {
	    UtilLog4j.log.info(this, "HotelBean.crearHotel");
	    if (!hotelBeanModel.getNombreProveedor().equals("")) {
		if (hotelBeanModel.traerProveedorPorNombre() != null) {
		    //buscar repetido
		    if (!hotelBeanModel.buscarHotelRepetido()) {
			hotelBeanModel.createHotel();
			FacesUtils.addInfoMessage("Hotel ha sido creado satisfactoriamente");
//                        hotelBeanModel.setMrPopupAgregarHotel(false);
			ret = "catalogoHotel";
		    } else {
			FacesUtils.addErrorMessage("El hotel ya ha sido registrado");
		    }
		} else {
		    UtilLog4j.log.info(this, "no existe el proveedor");
//                    FacesUtils.addInfoMessage("No existe el proveedor");
		    FacesUtils.addErrorMessage("No existe ningún hotel con el nombre ' " + hotelBeanModel.getNombreProveedor() + " '");
		}
	    } else {
		FacesUtils.addErrorMessage("Por favor indique el nombre del hotel");
	    }
	    return ret;
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.info(this, "Error al crear el hotel : " + e.getMessage());
	    hotelBeanModel.setMensajeError("por favor indique el nombre del hotel");
	    return "catalogoHotel";
	}
    }

    public String editHotel() {
	try {
	    hotelBeanModel.editHotel();
	    //hotelBeanModel.setMrPopupModificarHotel(false);
	    return "catalogoHotel";
	} catch (Exception e) {
	    UtilLog4j.log.info(this, e.getMessage());
	    FacesUtils.addInfoMessage("Por favor indique el nombre del hotel");
	    return "catalogoHotel";
	}

    }

    public void deleteHotel(ActionEvent event) {
	try {
	    UtilLog4j.log.info(this, "HotelBean.deleteHotel");
	    //esta linea es para que funcione el Confirm de javaScript
	    hotelBeanModel.setSgHotel((SgHotel) hotelBeanModel.getHotelModel().getRowData());
	    if (!hotelBeanModel.buscarHotelEnRelacion()) {
		hotelBeanModel.deleteHotel();
		hotelBeanModel.setMrPopupEliminarHotel(false);
	    } else {
		hotelBeanModel.setMrPopupAviso(true);
		// FacesUtils.addInfoMessage("No se puede eliminar el hotel,el registro esta siendo usado por otro proceso");
		// hotelBeanModel.setMensajeError("No se puede eliminar el hotel,el registro esta siendo usado por otro proceso");
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, " Excepcion DeleteHotel " + e.getMessage());
	    //FacesUtils.addInfoMessage(e.getMessage());
	}
    }

    public void createHabitacionToHotel(ActionEvent event) {
	int errors = 0;

	if (this.hotelBeanModel.getIdTipoEspecificoHabitacion() < 0) {
	    errors++;
	    FacesUtils.addErrorMessage("formPopupHotelHabitaciones:selectTipoHabitacion", FacesUtils.getKeyResourceBundle("sgl.sgHotel.tipo.habitacion")
		    + " "
		    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
	}

	if (getBigDecimal() == null || getBigDecimal().compareTo(BigDecimal.ZERO) < 1) {
	    errors++;
	    FacesUtils.addErrorMessage("formPopupHotelHabitaciones:precio", FacesUtils.getKeyResourceBundle("sgl.mantenimiento.grid.importe")
		    + " "
		    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
	}

	try {
	    if (errors == 0) {
		if (!hotelBeanModel.buscarHabitacionRepetida()) {
		    hotelBeanModel.createHotelHabitacion();
		    FacesUtils.addInfoMessage("formPopupHotelHabitaciones:msgsPopupHotelHabitaciones", (FacesUtils.getKeyResourceBundle("sgl.sgHotel.tipo.habitacion") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.agregacionSatisfactoria")));
		    this.hotelBeanModel.setIdTipoEspecificoHabitacion(-1);
		    setBigDecimal(BigDecimal.ZERO);
		    setDataModel(new ListDataModel(this.hotelBeanModel.getAllSgHabitacionHotelBySgHotel()));
		} else {
		    FacesUtils.addInfoMessage("formPopupHotelHabitaciones:msgsPopupHotelHabitaciones", FacesUtils.getKeyResourceBundle("sgl.sgHotel.sgHabitacionHotel.tipo.mensaje.error.tipoHabitacionRepetida"));
		}
	    }
	} catch (Exception e) {
	    FacesUtils.addErrorMessage("formPopupHotelHabitaciones:msgsPopupHotelHabitaciones", new SIAException().getMessage());
	    UtilLog4j.log.info(this, e.getMessage());
	    e.printStackTrace();
	}
    }

    public void deleteHotelHabitacion(ActionEvent actionEvent) {
	try {
	    hotelBeanModel.setSgHabitacion((SgHotelHabitacion) hotelBeanModel.getDataModel().getRowData());

	    if (hotelBeanModel.getSgHabitacion() != null) {
		if (!hotelBeanModel.buscarHabitacionEnRelacion()) {
		    hotelBeanModel.deleteHotelHabitacion();
		    FacesUtils.addInfoMessage("formPopupHotelHabitaciones:msgsPopupHotelHabitaciones", (FacesUtils.getKeyResourceBundle("sgl.sgHotel.tipo.habitacion") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria")));
		    this.hotelBeanModel.setSgHabitacion(null);
		    setDataModel(new ListDataModel(this.hotelBeanModel.getAllSgHabitacionHotelBySgHotel()));
		} else {
		    FacesUtils.addInfoMessage("formPopupHotelHabitaciones:msgsPopupHotelHabitaciones", FacesUtils.getKeyResourceBundle("sistema.mensaje.error.eliminar.registroUsado"));
		}
	    }
	} catch (Exception e) {
	    FacesUtils.addErrorMessage("formPopupHotelHabitaciones:msgsPopupHotelHabitaciones", new SIAException().getMessage());
	    UtilLog4j.log.info(this, e.getMessage());
	    e.printStackTrace();
	}
    }

    public void editPriceHotelHabitacion(ActionEvent actionEvent) {
	try {
	    hotelBeanModel.setSgHabitacion((SgHotelHabitacion) hotelBeanModel.getDataModel().getRowData());

	    if (hotelBeanModel.getSgHabitacion() != null) {
		//abrir popup para modificar el precio de habitacion
                /*
		 * hotelBeanModel.deleteHotelHabitacion();
		 * FacesUtils.addInfoMessage("formPopupHotelHabitaciones:msgsPopupHotelHabitaciones",
		 * (FacesUtils.getKeyResourceBundle("sgl.sgHotel.tipo.habitacion")
		 * + " " +
		 * FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria")));
		 * this.hotelBeanModel.setSgHabitacion(null); setDataModel(new
		 * ListDataModel(this.hotelBeanModel.getAllSgHabitacionHotelBySgHotel()));
		 */
	    }
	} catch (Exception e) {
	    FacesUtils.addErrorMessage("formPopupHotelHabitaciones:msgsPopupHotelHabitaciones", new SIAException().getMessage());
	    UtilLog4j.log.info(this, e.getMessage());
	    e.printStackTrace();
	}
    }

    public void saveSgHotelTipoEspecificoToHotel(ActionEvent event) {
	int errors = 0;

	if (this.hotelBeanModel.getIdServicio() < 0) {
	    errors++;
	    FacesUtils.addErrorMessage("formPopupHotelServicios:selectOneMenuServicios", FacesUtils.getKeyResourceBundle("sgl.sgHotel.servicio")
		    + " "
		    + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
	}

	try {
	    if (errors == 0) {
		this.hotelBeanModel.saveSgHotelTipoEspecifico();
		FacesUtils.addInfoMessage("formPopupHotelServicios:msgsPopupHotelServicios", (FacesUtils.getKeyResourceBundle("sgl.sgHotel.servicio") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.agregacionSatisfactoria")));
		this.hotelBeanModel.setIdServicio(-1);
	    }
	} catch (ExistingItemException eie) {
	    FacesUtils.addErrorMessage("formPopupHotelServicios:msgsPopupHotelServicios", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SgHotelTipoEspecifico) eie.getElemento()).getSgTipoEspecifico().getNombre());
	    UtilLog4j.log.info(this, eie.getMensajeParaProgramador());
	} catch (Exception e) {
	    FacesUtils.addErrorMessage("formPopupHotelServicios:msgsPopupHotelServicios", new SIAException().getMessage());
	    UtilLog4j.log.info(this, e.getMessage());
	    e.printStackTrace();
	}
    }

    public void deleteSghotelTipoEspecifico(ActionEvent actionEvent) {
	try {
	    setSgHotelTipoEspecificoVo((SgHotelTipoEspecificoVo) getDataModel().getRowData());
	    hotelBeanModel.deleteSgHotelTipoEspecifico();
	    FacesUtils.addInfoMessage("formPopupHotelServicios:msgsPopupHotelServicios", (FacesUtils.getKeyResourceBundle("sgl.sgHotel.servicio") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria")));
	    setSgHotelTipoEspecificoVo(null);

	} catch (Exception e) {
	    FacesUtils.addErrorMessage("formPopupHotelServicios:msgsPopupHotelServicios", new SIAException().getMessage());
	    UtilLog4j.log.info(this, e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     * Complete
     */
    //Auto completar proveedor
    public List<SelectItem> getListaProveedor() {
	return hotelBeanModel.getListaProveedor();
    }

    public void proveedorListener(String textChangeEvent) {
	UtilLog4j.log.info(this, "proveedorListener");
	    hotelBeanModel.setListaProveedor(regresaProveedorActivo(textChangeEvent));
//	    if (autoComplete.getSelectedItem() != null) {
//		String proveedorSel = (String) autoComplete.getSelectedItem().getValue();
//		hotelBeanModel.setNombreProveedor(proveedorSel);
//		UtilLog4j.log.info(this, "Proveedor seleccionado " + hotelBeanModel.getNombreProveedor());
//	    }
    }

    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	try {
	    for (Iterator it = hotelBeanModel.getListaProveedorBuscar().iterator(); it.hasNext();) {
		String string = (String) it.next();
		if (string != null) {
		    if (string.toLowerCase().startsWith(cadenaDigitada.toLowerCase())) {
			SelectItem item = new SelectItem(string);
			list.add(item);
		    }
		}
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Excepcion al regresar proveedor activo" + e.getMessage());
	}
	return list;
    }

    public void setListaProveedor(List<SelectItem> listaProveedor) {
	hotelBeanModel.setListaProveedor(listaProveedor);
    }

    public SgHotel getSgHotel() {
	return hotelBeanModel.getSgHotel();
    }

    public SgHotel getSgHotelSeleccionado() {
	return hotelBeanModel.getSgHotelSeleccionado();
    }

    public SgHotelHabitacion getSgHabitacion() {
	return hotelBeanModel.getSgHabitacion();
    }

    public String getNombreProveedor() {
	return hotelBeanModel.getNombreProveedor();
    }

    public void setNombreProveedor(String nombreProveedor) {
	hotelBeanModel.setNombreProveedor(nombreProveedor);
    }

    public String getOperacion() {
	return hotelBeanModel.getOperacion();
    }

    public String getNumeroEstrellas() {
	return hotelBeanModel.getNumeroEstrellas();
    }

    public void setNumeroEstrellas(String numeroEstrellas) {
	hotelBeanModel.setNumeroEstrellas(numeroEstrellas);
    }

    public String getMensajeError() {
	return hotelBeanModel.getMensajeError();
    }

    /*
     * Metododos para Popup's *
     */
    public boolean getMrPopupAviso() {
	return hotelBeanModel.isMrPopupAviso();
    }

    public boolean getMrPopupAgregarHabitacionToHotel() {
	return hotelBeanModel.isMrPopupAgregarHabitacionToHotel();
    }

    public boolean getMrPopupEliminarHabitacionToHotel() {
	return hotelBeanModel.isMrPopupEliminarHabitacionToHotel();
    }

    public boolean getMrPopupAgregarHotel() {
	return hotelBeanModel.isMrPopupAgregarHotel();
    }

    public void setMrPopupAgregarHotel(boolean mrPopupAgregarHotel) {
	hotelBeanModel.setMrPopupAgregarHotel(mrPopupAgregarHotel);
    }

    public String mostrarPopupAgregarHotel() {
	hotelBeanModel.setSgHotel(new SgHotel());
	hotelBeanModel.setNombreProveedor("");
	return "createHotel";
    }

    public String ocultarPopupAgregarHotel() {
	UtilLog4j.log.info(this, "ocultarPopupAgregarHotel");
	hotelBeanModel.setNombreProveedor("");
	UtilLog4j.log.info(this, "terminar ocultar agregar hotel");
	hotelBeanModel.setSgHotel(null);
	UtilLog4j.log.info(this, "terminar ocultar agregar hotel");
	hotelBeanModel.traerHotelesModel();
	return "catalogoHotel";
    }

    public String mostrarPopupModificarHotel() {
	UtilLog4j.log.info(this, "HotelBean.mostrarPopupAgregarHotelModificar");
	hotelBeanModel.setSgHotel((SgHotel) hotelBeanModel.getHotelModel().getRowData());
	hotelBeanModel.setMensajeError("");
	hotelBeanModel.setNombreProveedor(hotelBeanModel.getSgHotel().getProveedor().getNombre());
	return "editHotel";
    }

    public String ocultarPopupModificarHotel() {
	UtilLog4j.log.info(this, "HotelBean.mostrarPopupAgregarHotelModificar");
	hotelBeanModel.getHabitacionToHotelModel();
	return "catalogoHotel";
    }

    public boolean getMrPopupModificarHotel() {
	return hotelBeanModel.isMrPopupModificarHotel();
    }

    public boolean getMrPopupAgregarHabitacion() {
	return hotelBeanModel.isMrPopupAgregarHotelHabitacion();
    }

    public boolean getMrPopupEliminarHotel() {
	return hotelBeanModel.isMrPopupEliminarHotel();
    }

    public void mostrarPopupEliminarHotel(ActionEvent event) {
	hotelBeanModel.setSgHotel((SgHotel) hotelBeanModel.getHotelModel().getRowData());
	hotelBeanModel.setMrPopupEliminarHotel(true);
    }

    public void ocultarPopupEliminarHotel(ActionEvent event) {
	hotelBeanModel.setMrPopupEliminarHotel(false);
    }

    public boolean getMrPopupDetalleHotel() {
	return hotelBeanModel.isMrPopupVerDetalleHotel();
    }

    public void mostrarPopupDetalleHotel(ActionEvent event) {
	UtilLog4j.log.info(this, "HotelBean.mostrarPopupDetalleHotel");
	this.hotelBeanModel.setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);

	hotelBeanModel.setSgHotel((SgHotel) hotelBeanModel.getHotelModel().getRowData());
	hotelBeanModel.traerHotelHabitacionModel();
	this.hotelBeanModel.setTipoHabitacionModel(new ListDataModel(this.hotelBeanModel.getAllSgHotelTipoEspecificoBySgHotel()));
	hotelBeanModel.setMrPopupVerDetalleHotel(true);
    }

    public void ocultarPopupDetalleHotel(ActionEvent event) {
	hotelBeanModel.setMrPopupVerDetalleHotel(false);
    }

    //--------
    public String goToAgregarHabitacionToHotel() {
	this.hotelBeanModel.setMrPopupVerDetalleHotel(false);
	setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);

	hotelBeanModel.setSgHotel((SgHotel) hotelBeanModel.getHotelModel().getRowData());
	hotelBeanModel.setIdTipoEspecificoHabitacion(-1);
	hotelBeanModel.setSgHabitacion(new SgHotelHabitacion());
	setDataModel(new ListDataModel(this.hotelBeanModel.getAllSgHabitacionHotelBySgHotel()));
	//hotelBeanModel.controlarPop("popupSgHotelHabitaciones", Boolean.TRUE);
	return "administrarHabitaciones";
    }

    public String regregarCatalogoHotelAgregarHabitacionToHotel() {
	setDataModel(null);
	this.hotelBeanModel.setSgHotel(null);
	this.hotelBeanModel.setMrPopupVerDetalleHotel(false);
	//hotelBeanModel.controlarPop("popupSgHotelHabitaciones", Boolean.FALSE);
	setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);
	return "catalogoHotel";
    }

    public void mostrarPopupModificarPrecio(ActionEvent event) {
	hotelBeanModel.setSgHabitacion((SgHotelHabitacion) hotelBeanModel.getDataModel().getRowData());
	if (hotelBeanModel.getSgHabitacion() != null) {
	    setBigDecimal(hotelBeanModel.getSgHabitacion().getPrecio());
	    hotelBeanModel.controlarPop("popupSgHotelHabitaciones", Boolean.TRUE);
	}
    }

    public void cerrarPopupModificarPrecio(ActionEvent event) {
	hotelBeanModel.controlarPop("popupSgHotelHabitaciones", Boolean.FALSE);
	setBigDecimal(new BigDecimal(0));
    }

    public void modificarPrecioHotel(ActionEvent event) {
	if (getBigDecimal() != null) {
	    if (getBigDecimal().intValue() > 0) {
		if (hotelBeanModel.modificarPrecioHabitacion()) {
		    hotelBeanModel.controlarPop("popupSgHotelHabitaciones", Boolean.FALSE);
		    this.hotelBeanModel.setDataModel(new ListDataModel(this.hotelBeanModel.getAllSgHabitacionHotelBySgHotel()));
		    setBigDecimal(new BigDecimal(0));
		}
	    } else {
		FacesUtils.addErrorMessage("formPopupModificarPrecio:msgsPopupHotelHabitaciones", "Por favor escriba un valor mayor a 0 ");
	    }
	} else {
	    FacesUtils.addErrorMessage("formPopupModificarPrecio:msgsPopupHotelHabitaciones", "Por favor escriba el precio para la habitación");
	}
    }

    //-----------
    public void mostrarPopupAgregarHabitacionToHotel(ActionEvent event) {
	this.hotelBeanModel.setMrPopupVerDetalleHotel(false);
	setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);

	hotelBeanModel.setSgHotel((SgHotel) hotelBeanModel.getHotelModel().getRowData());
	hotelBeanModel.setIdTipoEspecificoHabitacion(-1);
	hotelBeanModel.setSgHabitacion(new SgHotelHabitacion());
	if (getDataModel() == null) {
	    setDataModel(new ListDataModel(this.hotelBeanModel.getAllSgHabitacionHotelBySgHotel()));
	}
	hotelBeanModel.controlarPop("popupSgHotelHabitaciones", Boolean.TRUE);
    }

    public void ocultarPopupAgregarHabitacionToHotel(ActionEvent event) {
	setDataModel(null);
	this.hotelBeanModel.setSgHotel(null);
	this.hotelBeanModel.setMrPopupVerDetalleHotel(false);
	hotelBeanModel.controlarPop("popupSgHotelHabitaciones", Boolean.FALSE);
	setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);
    }

    //----Servicios
    public String goToAddServicesToHotel() {
	this.hotelBeanModel.setMrPopupVerDetalleHotel(false);
	setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);

	hotelBeanModel.setSgHotel((SgHotel) hotelBeanModel.getHotelModel().getRowData());
	setDataModel(new ListDataModel(this.hotelBeanModel.getAllSgHotelTipoEspecificoBySgHotel()));
	//hotelBeanModel.controlarPop("popupSgHotelServices", Boolean.TRUE);
	return "administrarServicios";
    }

    public String regresarCatalogoHotelAddServicesToHotel(ActionEvent event) {
	this.hotelBeanModel.setMrPopupVerDetalleHotel(false);
	setDataModel(null);
	this.hotelBeanModel.setSgHotel(null);
	setIdServicio(-1);
	//hotelBeanModel.controlarPop("popupSgHotelServices", Boolean.FALSE);
	setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);
	return "catalogoHotel";
    }

    //-------------
    public void openPopupAddServicesToHotel(ActionEvent event) {
	this.hotelBeanModel.setMrPopupVerDetalleHotel(false);
	setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);

	hotelBeanModel.setSgHotel((SgHotel) hotelBeanModel.getHotelModel().getRowData());
	setDataModel(new ListDataModel(this.hotelBeanModel.getAllSgHotelTipoEspecificoBySgHotel()));
	hotelBeanModel.controlarPop("popupSgHotelServices", Boolean.TRUE);
    }

    public void closePopupAddServicesToHotel(ActionEvent event) {
	this.hotelBeanModel.setMrPopupVerDetalleHotel(false);
	setDataModel(null);
	this.hotelBeanModel.setSgHotel(null);
	setIdServicio(-1);
	hotelBeanModel.controlarPop("popupSgHotelServices", Boolean.FALSE);
	setTipoHabitacionModel(null);
	this.hotelBeanModel.setHabitacionToHotelModel(null);
    }

    public void mostrarPopupEliminarHotelToHabitacion(ActionEvent event) {
	UtilLog4j.log.info(this, "HotelBean.mostrarPopupEliminarHotelToHabitacion");
	hotelBeanModel.setSgHabitacion((SgHotelHabitacion) hotelBeanModel.getHabitacionToHotelModel().getRowData());
	hotelBeanModel.setMrPopupEliminarHabitacionToHotel(true);
    }

    public void ocultarPopupEliminarHotelToHabitacion(ActionEvent event) {
	UtilLog4j.log.info(this, "HotelBean.ocultarPopupEliminarHotelToHabitacion");
	hotelBeanModel.setMrPopupEliminarHabitacionToHotel(false);
    }

    public boolean getMrPopupAgregarTipoHabitacionHotel() {
	return hotelBeanModel.isMrPopupAgregarTipoHabitacionHotel();
    }

    public void ocultarPopupAgregarTipoHabitacion(ActionEvent event) {
	hotelBeanModel.setMrPopupAgregarTipoHabitacionHotel(false);
    }

    public void ocultarPopupEliminarTipoHabitacion(ActionEvent event) {
	hotelBeanModel.setMrPopupEliminarTipoHabitacionHotel(false);
    }

    public void ocultarPopupModificarTipoHabitacion(ActionEvent event) {
	hotelBeanModel.setMrPopupModificarTipoHabitacionHotel(false);
    }

    public void mostrarPopupAviso(ActionEvent event) {
	UtilLog4j.log.info(this, "HotelBean.mostrarPopupAviso");
	hotelBeanModel.setMrPopupAviso(true);
    }

    public void ocultarPopupAviso(ActionEvent event) {
	UtilLog4j.log.info(this, "HotelBean.ocultarPopupAviso");
	hotelBeanModel.setMrPopupAviso(false);
    }

    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
	return this.hotelBeanModel.getDataModel();
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
	this.hotelBeanModel.setDataModel(dataModel);
    }

    /**
     * @return the bigDecimal
     */
    public BigDecimal getBigDecimal() {
	return this.hotelBeanModel.getBigDecimal();
    }

    /**
     * @param bigDecimal the bigDecimal to set
     */
    public void setBigDecimal(BigDecimal bigDecimal) {
	this.hotelBeanModel.setBigDecimal(bigDecimal);
    }

    /**
     * @return the idServicio
     */
    public int getIdServicio() {
	return this.hotelBeanModel.getIdServicio();
    }

    /**
     * @param idServicio the idServicio to set
     */
    public void setIdServicio(int idServicio) {
	this.hotelBeanModel.getIdServicio();
    }

    /**
     * @return the sgHotelTipoEspecificoVo
     */
    public SgHotelTipoEspecificoVo getSgHotelTipoEspecificoVo() {
	return this.hotelBeanModel.getSgHotelTipoEspecificoVo();
    }

    /**
     * @param sgHotelTipoEspecificoVo the sgHotelTipoEspecificoVo to set
     */
    public void setSgHotelTipoEspecificoVo(SgHotelTipoEspecificoVo sgHotelTipoEspecificoVo) {
	this.hotelBeanModel.setSgHotelTipoEspecificoVo(sgHotelTipoEspecificoVo);
    }

    /**
     * @return the tipoHabitacionModel
     */
    public DataModel getTipoHabitacionModel() {
	return this.hotelBeanModel.getTipoHabitacionModel();
    }

    /**
     * @param tipoHabitacionModel the tipoHabitacionModel to set
     */
    public void setTipoHabitacionModel(DataModel tipoHabitacionModel) {
	this.hotelBeanModel.setTipoHabitacionModel(tipoHabitacionModel);
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
	return this.hotelBeanModel.isFlag();
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
	this.hotelBeanModel.setFlag(flag);
    }

    /**
     * @param hotelBeanModel the hotelBeanModel to set
     */
    public void setHotelBeanModel(HotelBeanModel hotelBeanModel) {
	this.hotelBeanModel = hotelBeanModel;
    }

}
