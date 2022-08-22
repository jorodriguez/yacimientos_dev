/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.excepciones.SIAException;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.bean.model.ViajeAereoModel;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "viajeAereoBean")
@RequestScoped
public class ViajeAereoBean implements Serializable {

    /**
     * Creates a new instance of ViajeAereoBean
     */
    public ViajeAereoBean() {
    }
    //
    @ManagedProperty(value = "#{viajeAereoModel}")
    private ViajeAereoModel viajeAereoModel;

    public void salidaViaje(ActionEvent event) {
	try {
	    int id = Integer.parseInt(FacesUtils.getRequestParameter("idViaje"));
	    viajeAereoModel.llenarViaje(id);
	    if (viajeAereoModel.validaFechaSalidaViaje()) {
		viajeAereoModel.salidaViaje();
		//Limpiar variables
		viajeAereoModel.setViajeVO(null);
	    } else {
		FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.viaje.mensaje.fecha.salida"));
	    }
	} catch (SIAException e) {
	    FacesUtils.addErrorMessage(e.getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    public void finalizarViaje(ActionEvent event) {
	int id = Integer.parseInt(FacesUtils.getRequestParameter("idViaje"));
	viajeAereoModel.llenarViaje(id);
	viajeAereoModel.finalizarViaje();
    }

    public void verDetalleViajeAereo(ActionEvent event) {
	int id = Integer.parseInt(FacesUtils.getRequestParameter("idViaje"));
	viajeAereoModel.llenarViaje(id);
	viajeAereoModel.traerItinerario();
	PrimeFaces.current().executeScript(";$(dialogoDetalleViaje).modal('show');");
    }

    public void cancelarViaje(ActionEvent event) {
	//      int viaje = Integer.parseInt(FacesUtils.getRequestParameter("viaje"));
//	viajeAereoModel.setViajeVO((ViajeVO) viajeAereoModel.getLista().getRowData());
	//      viajeAereoModel.setViajeVO((ViajeVO) viajeAereoModel.getLista().getRowData());
	PrimeFaces.current().executeScript(";$(dialogoCancelarViaje).modal('show');");
    }

    public void completarCancelar(ActionEvent event) {
	if (!viajeAereoModel.getMotivo().trim().isEmpty()) {
	    try {
		viajeAereoModel.cancelarViaje();
		viajeAereoModel.setViajeVO(null);
		viajeAereoModel.setMotivo("");
		PrimeFaces.current().executeScript(";$(dialogoCancelarViaje).modal('hide');");
	    } catch (Exception ex) {
		FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.viaje.mensaje.cancelar"));
	    }
	} else {
	    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sia.sgl.agregar.motivo"));
	}
    }

    public void cerrarDetalleViaje(ActionEvent event) {
	PrimeFaces.current().executeScript(";$(dialogoDetalleViaje).modal('hide');");
	viajeAereoModel.setViajeVO(null);

    }

    public void completarCrearViajeAereoRegreso(ActionEvent event) {
	try {
	    if (viajeAereoModel.getItinerarioVuelta().getEscalas().size() > 0) {
		viajeAereoModel.guardarViajeAereoRegreso();
		viajeAereoModel.setViajeVO(null);
		viajeAereoModel.setItinerarioVuelta(null);
		PrimeFaces.current().executeScript(";$(dialogoViajeRegreso).modal('hide');");
	    } else {
		FacesUtils.addErrorMessage("Es necesario agregar las escalas al itinerario de vuelta.");
	    }

	} catch (Exception e) {
	    FacesUtils.addErrorMessage("Ocurrio un error al guardar el viaje aéreo, favor de contactar con el equipo del SIA al correo soportesia@ihsa.mx");
	    UtilLog4j.log.fatal(this, "Ocurrió un error al guardar el viaje de regreso" + e.getMessage());
	}
    }

    public void cerrarViajeRegreso(ActionEvent event) {
	PrimeFaces.current().executeScript(";$(dialogoViajeRegreso).modal('hide');");
	viajeAereoModel.setViajeVO(null);
	viajeAereoModel.setItinerarioVuelta(null);

    }

    public List getTraerViajesAereosEnProceso() {
	try {
	    return viajeAereoModel.getListaProceso();
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(e.getMessage());
	}
	return null;
    }

    public void crearViajeAereoRegreso(ActionEvent event) {
	int id = Integer.parseInt(FacesUtils.getRequestParameter("idViaje"));
	viajeAereoModel.llenarViaje(id);
	//Trae la solicitud y busca el itinerario de regreso
	viajeAereoModel.llenaDatosItinerarioRegreso();
	PrimeFaces.current().executeScript(";$(dialogoViajeRegreso).modal('show');");
	//Vaciar datos al viaje de regreso
    }

    public void saveSgDetalleItinerario(ActionEvent actionEvent) {
	//Validaciones

	try {
	    this.viajeAereoModel.saveSgDetalleItinerario();
	    FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.sgItinerario.escala") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));

	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    public List<SelectItem> getSelectItemHoras() {
	List<SelectItem> listSelectItem = new ArrayList<SelectItem>();

	for (Integer i = 1; i < 10; i++) { //01am - 09am
	    listSelectItem.add(new SelectItem(i, ("0" + i.toString() + " am")));
	}
	for (Integer i = 10; i < 12; i++) { //10am - 11am
	    listSelectItem.add(new SelectItem(i, i.toString() + " am"));
	}
	listSelectItem.add(new SelectItem(12, "12" + " pm")); //12 pm

	for (Integer i = 13; i < 22; i++) { //01pm - 09pm
	    listSelectItem.add(new SelectItem(i, ("0" + (i - 12) + " pm")));
	}
	for (Integer i = 22; i < 24; i++) { //10pm - 11pm
	    listSelectItem.add(new SelectItem(i, (i - 12) + " pm"));
	}
	listSelectItem.add(new SelectItem(0, "12" + " am")); //12am
	return listSelectItem;
    }

    public List<SelectItem> getSelectItemMinutos() {
	List<SelectItem> listSelectItem = new ArrayList<SelectItem>();

	for (Integer i = 0; i < 60; i++) {
	    listSelectItem.add(new SelectItem(i, ((i < 10) ? ("0".toString().concat(i.toString())) : i.toString())));
	}
	return listSelectItem;
    }

    /**
     * @return the lista
     */
    public List getLista() {
	return viajeAereoModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List lista) {
	viajeAereoModel.setLista(lista);
    }

    /**
     * @return the viajeVO
     */
    public ViajeVO getViajeVO() {
	return viajeAereoModel.getViajeVO();
    }

    /**
     * @param viajeVO the viajeVO to set
     */
    public void setViajeVO(ViajeVO viajeVO) {
	viajeAereoModel.setViajeVO(viajeVO);
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
	return viajeAereoModel.getMotivo();
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
	viajeAereoModel.setMotivo(motivo);
    }

    /**
     * @param viajeAereoModel the viajeAereoModel to set
     */
    public void setViajeAereoModel(ViajeAereoModel viajeAereoModel) {
	this.viajeAereoModel = viajeAereoModel;
    }

    /**
     * @return the listaProceso
     */
    public List getListaProceso() {
	return viajeAereoModel.getListaProceso();
    }

    /**
     * @param listaProceso the listaProceso to set
     */
    public void setListaProceso(List listaProceso) {
	viajeAereoModel.setListaProceso(listaProceso);
    }

    /**
     * @return the itinerarioVuelta
     */
    public ItinerarioCompletoVo getItinerarioVuelta() {
	return viajeAereoModel.getItinerarioVuelta();
    }

    /**
     * @param itinerarioVuelta the itinerarioVuelta to set
     */
    public void setItinerarioVuelta(ItinerarioCompletoVo itinerarioVuelta) {
	viajeAereoModel.setItinerarioVuelta(itinerarioVuelta);
    }

    /**
     * @return the listaCiudadOrigen
     */
    public List<SelectItem> getListaCiudadOrigen() {
	return viajeAereoModel.getListaCiudadOrigen();
    }

    /**
     * @param listaCiudadOrigen the listaCiudadOrigen to set
     */
    public void setListaCiudadOrigen(List<SelectItem> listaCiudadOrigen) {
	viajeAereoModel.setListaCiudadOrigen(listaCiudadOrigen);
    }

    /**
     * @return the listaCiudadDestino
     */
    public List<SelectItem> getListaCiudadDestino() {
	return viajeAereoModel.getListaCiudadDestino();
    }

    /**
     * @param listaCiudadDestino the listaCiudadDestino to set
     */
    public void setListaCiudadDestino(List<SelectItem> listaCiudadDestino) {
	viajeAereoModel.setListaCiudadDestino(listaCiudadDestino);
    }

    /**
     * @return the numeroVuelo
     */
    public String getNumeroVuelo() {
	return viajeAereoModel.getNumeroVuelo();
    }

    /**
     * @param numeroVuelo the numeroVuelo to set
     */
    public void setNumeroVuelo(String numeroVuelo) {
	viajeAereoModel.setNumeroVuelo(numeroVuelo);
    }

    /**
     * @return the idAerolinea
     */
    public int getIdAerolinea() {
	return viajeAereoModel.getIdAerolinea();
    }

    /**
     * @param idAerolinea the idAerolinea to set
     */
    public void setIdAerolinea(int idAerolinea) {
	viajeAereoModel.setIdAerolinea(idAerolinea);
    }

    /**
     * @return the fechaSalida
     */
    public Date getFechaSalida() {
	return viajeAereoModel.getFechaSalida();
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(Date fechaSalida) {
	viajeAereoModel.setFechaSalida(fechaSalida);
    }

    /**
     * @return the horaSalida
     */
    public int getHoraSalida() {
	return viajeAereoModel.getHoraSalida();
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(int horaSalida) {
	viajeAereoModel.getHoraSalida();
    }

    /**
     * @return the fechaLlegada
     */
    public Date getFechaLlegada() {
	return viajeAereoModel.getFechaLlegada();
    }

    /**
     * @param fechaLlegada the fechaLlegada to set
     */
    public void setFechaLlegada(Date fechaLlegada) {
	viajeAereoModel.setFechaLlegada(fechaLlegada);
    }

    /**
     * @return the horaLlegada
     */
    public int getHoraLlegada() {
	return viajeAereoModel.getHoraLlegada();
    }

    /**
     * @param horaLlegada the horaLlegada to set
     */
    public void setHoraLlegada(int horaLlegada) {
	viajeAereoModel.setHoraLlegada(horaLlegada);
    }

    /**
     * @return the minutoSalida
     */
    public int getMinutoSalida() {
	return viajeAereoModel.getMinutoSalida();
    }

    /**
     * @param minutoSalida the minutoSalida to set
     */
    public void setMinutoSalida(int minutoSalida) {
	viajeAereoModel.setMinutoSalida(minutoSalida);
    }

    /**
     * @return the minutoLlegada
     */
    public int getMinutoLlegada() {
	return viajeAereoModel.getMinutoLlegada();
    }

    /**
     * @param minutoLlegada the minutoLlegada to set
     */
    public void setMinutoLlegada(int minutoLlegada) {
	viajeAereoModel.setMinutoLlegada(minutoLlegada);
    }

    /**
     * @return the idCiudadOrigen
     */
    public int getIdCiudadOrigen() {
	return viajeAereoModel.getIdCiudadOrigen();
    }

    /**
     * @param idCiudadOrigen the idCiudadOrigen to set
     */
    public void setIdCiudadOrigen(int idCiudadOrigen) {
	viajeAereoModel.setIdCiudadOrigen(idCiudadOrigen);
    }

    /**
     * @return the idCiudadDestino
     */
    public int getIdCiudadDestino() {
	return viajeAereoModel.getIdCiudadDestino();
    }

    /**
     * @param idCiudadDestino the idCiudadDestino to set
     */
    public void setIdCiudadDestino(int idCiudadDestino) {
	viajeAereoModel.setIdCiudadDestino(idCiudadDestino);
    }

    /**
     * @return the listaAerolinea
     */
    public List<SelectItem> getListaAerolinea() {
	return viajeAereoModel.getListaAerolinea();
    }

    /**
     * @param listaAerolinea the listaAerolinea to set
     */
    public void setListaAerolinea(List<SelectItem> listaAerolinea) {
	viajeAereoModel.setListaAerolinea(listaAerolinea);
    }

}
