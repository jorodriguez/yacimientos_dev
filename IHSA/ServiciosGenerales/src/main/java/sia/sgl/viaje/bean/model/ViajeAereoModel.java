/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgAerolinea;
import sia.modelo.sgl.viaje.vo.ItinerarioCompletoVo;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.SiCiudadVO;
import sia.servicios.sgl.viaje.impl.SgAerolineaImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgItinerarioImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "viajeAereoModel")
@ViewScoped
public class ViajeAereoModel implements Serializable {

    /**
     * Creates a new instance of ViajeAereoModel
     */
    public ViajeAereoModel() {
    }
    @Inject
    private Sesion sesion;
    //
    @Inject
    SgViajeImpl sgViajeImpl;
    @Inject
    SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    SgItinerarioImpl sgItinerarioImpl;
    @Inject
    SiCiudadImpl siCiudadImpl;
    @Inject
    SgDetalleItinerarioImpl sgDetalleItinerarioImpl;
    @Inject
    SgAerolineaImpl sgAerolineaImpl;
    @Inject
    SgViajeroImpl sgViajeroImpl;

    //
    private List lista;
    private List listaProceso;
    private ViajeVO viajeVO;
    private String motivo;
    private ItinerarioCompletoVo itinerarioVuelta;
    private List<SelectItem> listaCiudadOrigen;
    private List<SelectItem> listaCiudadDestino;
    private List<SelectItem> listaAerolinea;
    private String numeroVuelo;
    private int idAerolinea;
    private int idCiudadOrigen;
    private int idCiudadDestino;
    private Date fechaSalida;
    private int horaSalida;
    private Date fechaLlegada;
    private int horaLlegada;
    private int minutoSalida;
    private int minutoLlegada;

    @PostConstruct
    public void iniciar() {
	try {
	    listaCiudadOrigen = new ArrayList<SelectItem>();
	    listaCiudadDestino = new ArrayList<SelectItem>();
	    listaAerolinea = new ArrayList<SelectItem>();
	    lista = sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR, this.sesion.getUsuario().getId());
	    setListaProceso(sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_PROCESO, this.sesion.getUsuario().getId()));
	    //
	    List<SiCiudadVO> lco = siCiudadImpl.findAllNative("nombre", true, false);
	    for (SiCiudadVO lco1 : lco) {
		getListaCiudadOrigen().add(new SelectItem(lco1.getId(), lco1.getNombre()));
	    }
	    List<SiCiudadVO> lcd = siCiudadImpl.findAllNative("nombre", true, false);
	    for (SiCiudadVO lco1 : lcd) {
		getListaCiudadDestino().add(new SelectItem(lco1.getId(), lco1.getNombre()));
	    }
	    List<SgAerolinea> la = sgAerolineaImpl.findAll("nombre", true, false);
	    for (SgAerolinea la1 : la) {
		getListaAerolinea().add(new SelectItem(la1.getId(), la1.getNombre()));
	    }
	} catch (SIAException ex) {
	    Logger.getLogger(ViajeAereoModel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void llenarViaje(int id) {
	setViajeVO(sgViajeImpl.buscarPorId(id, true, false, false));
    }

    public void traerItinerario() {
	getViajeVO().setItinerarioCompletoVo(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(getViajeVO().getIdItinerario(), true, "nombre"));
    }

    public boolean validaFechaSalidaViaje() {
	return siManejoFechaImpl.validaFechaSalidaViaje(viajeVO.getFechaProgramada(), viajeVO.getHoraProgramada());
    }

    public void salidaViaje() throws SIAException {
	sgViajeImpl.salidaViajeAereo(sesion.getUsuario().getId(), getViajeVO(), Constantes.ESTATUS_VIAJE_PROCESO);
	lista = sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR, this.sesion.getUsuario().getId());
	setListaProceso(sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_PROCESO, this.sesion.getUsuario().getId()));
    }

    public void cancelarViaje() throws SIAException {
	UtilLog4j.log.info(this, "ViajeBeanModel.cancelarViaje()");
	sgViajeImpl.cancelarViajeAereo(sesion.getUsuario().getId(), viajeVO.getId(), motivo, sesion.getUsuario().getNombre());
	//
	lista = sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR, this.sesion.getUsuario().getId());
    }

    public void llenaDatosItinerarioRegreso() {
	try {

	    setItinerarioVuelta(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdSolicitud(viajeVO.getListaViajeros().get(0).getIdSolicitudViaje(), false, true, "id"));
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, ex.getMessage());
	}
    }

    public void guardarViajeAereoRegreso() {
	try {
	    List<ViajeroVO> vj = viajeVO.getListaViajeros();
	    UtilLog4j.log.info(this, "Viajeros: " + vj.size());
	    sgViajeImpl.guardarViajeAereoRegreso(sesion.getOficinaActual().getId(), sesion.getUsuario().getId(), getViajeVO(),
		    vj.get(0).getIdSolicitudViaje(), vj, getItinerarioVuelta());

	    //  if (idV > 0) {
	    //	for (ViajeroVO vj1 : vj) {
	    //	    sgViajeroImpl.guardarViajero(vj1.getIdInvitado(), vj1.getIdUsuario(), 0, vj1.getIdSolicitudViaje(), idV, "",
	    //		    sesion.getUsuario().getId(), Constantes.BOOLEAN_FALSE, Constantes.BOOLEAN_FALSE);
	    //	}
	    //Finaliza viaje
	    //	sgViajeImpl.finalizarViaje(viajeVO.getId(), sesion.getUsuario().getId());
	    //
	    setLista(sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_POR_SALIR, this.sesion.getUsuario().getId()));
	    setListaProceso(sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_PROCESO, this.sesion.getUsuario().getId()));
	    //   }

	} catch (Exception ex) {
	    UtilLog4j.log.info(this, "Exc: + + +  " + ex.getMessage());
	}
    }

    public List<SelectItem> getAllSiCiudadSelectItemByRange(String startFilter, String endFilter) {
	List<SiCiudadVO> list = this.siCiudadImpl.findAllByRangeNative(startFilter, endFilter, "nombre", true, false);
	List<SelectItem> items = new ArrayList<SelectItem>();

	for (SiCiudadVO vo : list) {
	    SelectItem si = new SelectItem(vo.getId(), (vo.getNombre() + "|" + vo.getNombreSiEstado() + "|" + vo.getNombreSiPais()));
	    items.add(si);
	}
	return items;
    }

    public void finalizarViaje() {
	try {
	    sgViajeImpl.finalizarViaje(viajeVO.getId(), sesion.getUsuario().getId());
	    setListaProceso(sgViajeImpl.getAirTravelByOffice(sesion.getOficinaActual().getId(), Constantes.ESTATUS_VIAJE_PROCESO, this.sesion.getUsuario().getId()));
	} catch (SIAException ex) {
	    Logger.getLogger(ViajeAereoModel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void saveSgDetalleItinerario() {
	//

	Calendar cHoraSalida = Calendar.getInstance();
	cHoraSalida.set(Calendar.HOUR_OF_DAY, this.horaSalida);
	cHoraSalida.set(Calendar.MINUTE, this.minutoSalida);

	Calendar cHoraLlegada = Calendar.getInstance();
	cHoraLlegada.set(Calendar.HOUR_OF_DAY, this.horaLlegada);
	cHoraLlegada.set(Calendar.MINUTE, this.minutoLlegada);
	//Crear Times para hora y minuto de salida y regreso
	this.sgDetalleItinerarioImpl.save(getItinerarioVuelta().getId(), getIdAerolinea(), getIdCiudadOrigen(), getIdCiudadDestino(),
		this.sesion.getUsuario().getId(), getNumeroVuelo(), getFechaSalida(), cHoraSalida, getFechaLlegada(), cHoraLlegada);
	//setLista(new ListDataModel(this.sgDetalleItinerarioImpl.findBySgItinerario(getSgItinerarioVuelta(), "id", true, false)));
	viajeVO.setItinerarioCompletoVo(sgItinerarioImpl.buscarItinerarioCompletoVoPorIdItinerario(viajeVO.getIdItinerario(), true, "id"));
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
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
     * @return the motivo
     */
    public String getMotivo() {
	return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
	this.motivo = motivo;
    }

    /**
     * @return the listaProceso
     */
    public List getListaProceso() {
	return listaProceso;
    }

    /**
     * @param listaProceso the listaProceso to set
     */
    public void setListaProceso(List listaProceso) {
	this.listaProceso = listaProceso;
    }

    /**
     * @return the itinerarioVuelta
     */
    public ItinerarioCompletoVo getItinerarioVuelta() {
	return itinerarioVuelta;
    }

    /**
     * @param itinerarioVuelta the itinerarioVuelta to set
     */
    public void setItinerarioVuelta(ItinerarioCompletoVo itinerarioVuelta) {
	this.itinerarioVuelta = itinerarioVuelta;
    }

    /**
     * @return the listaCiudadOrigen
     */
    public List<SelectItem> getListaCiudadOrigen() {
	return listaCiudadOrigen;
    }

    /**
     * @param listaCiudadOrigen the listaCiudadOrigen to set
     */
    public void setListaCiudadOrigen(List<SelectItem> listaCiudadOrigen) {
	this.listaCiudadOrigen = listaCiudadOrigen;
    }

    /**
     * @return the listaCiudadDestino
     */
    public List<SelectItem> getListaCiudadDestino() {
	return listaCiudadDestino;
    }

    /**
     * @param listaCiudadDestino the listaCiudadDestino to set
     */
    public void setListaCiudadDestino(List<SelectItem> listaCiudadDestino) {
	this.listaCiudadDestino = listaCiudadDestino;
    }

    /**
     * @return the numeroVuelo
     */
    public String getNumeroVuelo() {
	return numeroVuelo;
    }

    /**
     * @param numeroVuelo the numeroVuelo to set
     */
    public void setNumeroVuelo(String numeroVuelo) {
	this.numeroVuelo = numeroVuelo;
    }

    /**
     * @return the idAerolinea
     */
    public int getIdAerolinea() {
	return idAerolinea;
    }

    /**
     * @param idAerolinea the idAerolinea to set
     */
    public void setIdAerolinea(int idAerolinea) {
	this.idAerolinea = idAerolinea;
    }

    /**
     * @return the fechaSalida
     */
    public Date getFechaSalida() {
	return fechaSalida;
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(Date fechaSalida) {
	this.fechaSalida = fechaSalida;
    }

    /**
     * @return the horaSalida
     */
    public int getHoraSalida() {
	return horaSalida;
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(int horaSalida) {
	this.horaSalida = horaSalida;
    }

    /**
     * @return the fechaLlegada
     */
    public Date getFechaLlegada() {
	return fechaLlegada;
    }

    /**
     * @param fechaLlegada the fechaLlegada to set
     */
    public void setFechaLlegada(Date fechaLlegada) {
	this.fechaLlegada = fechaLlegada;
    }

    /**
     * @return the horaLlegada
     */
    public int getHoraLlegada() {
	return horaLlegada;
    }

    /**
     * @param horaLlegada the horaLlegada to set
     */
    public void setHoraLlegada(int horaLlegada) {
	this.horaLlegada = horaLlegada;
    }

    /**
     * @return the minutoSalida
     */
    public int getMinutoSalida() {
	return minutoSalida;
    }

    /**
     * @param minutoSalida the minutoSalida to set
     */
    public void setMinutoSalida(int minutoSalida) {
	this.minutoSalida = minutoSalida;
    }

    /**
     * @return the minutoLlegada
     */
    public int getMinutoLlegada() {
	return minutoLlegada;
    }

    /**
     * @param minutoLlegada the minutoLlegada to set
     */
    public void setMinutoLlegada(int minutoLlegada) {
	this.minutoLlegada = minutoLlegada;
    }

    /**
     * @return the idCiudadOrigen
     */
    public int getIdCiudadOrigen() {
	return idCiudadOrigen;
    }

    /**
     * @param idCiudadOrigen the idCiudadOrigen to set
     */
    public void setIdCiudadOrigen(int idCiudadOrigen) {
	this.idCiudadOrigen = idCiudadOrigen;
    }

    /**
     * @return the idCiudadDestino
     */
    public int getIdCiudadDestino() {
	return idCiudadDestino;
    }

    /**
     * @param idCiudadDestino the idCiudadDestino to set
     */
    public void setIdCiudadDestino(int idCiudadDestino) {
	this.idCiudadDestino = idCiudadDestino;
    }

    /**
     * @return the listaAerolinea
     */
    public List<SelectItem> getListaAerolinea() {
	return listaAerolinea;
    }

    /**
     * @param listaAerolinea the listaAerolinea to set
     */
    public void setListaAerolinea(List<SelectItem> listaAerolinea) {
	this.listaAerolinea = listaAerolinea;
    }

}
