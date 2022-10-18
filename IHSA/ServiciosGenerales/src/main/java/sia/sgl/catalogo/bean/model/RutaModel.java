/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.catalogo.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.SgOficina;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.TipoEspecificoVo;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.sgl.vo.SiCiudadVO;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaCiudadImpl;
import sia.servicios.sgl.viaje.impl.SgDetalleRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sistema.impl.SiCiudadImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "rutaModel")
@ViewScoped
public class RutaModel implements Serializable{

    /**
     * Creates a new instance of RutaModel
     */
    public RutaModel() {
    }
    @Inject
    private Sesion sesion;

    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @Inject
    private SgTipoEspecificoImpl tipoEspecificoService;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgDetalleRutaTerrestreImpl sgDetalleRutaTerrestreImpl;
    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private SgDetalleRutaCiudadImpl sgDetalleRutaCiudadImpl;
    @Inject
    private SiCiudadImpl siCiudadImpl;

    private RutaTerrestreVo rutaTerrestreVo;
    private int idTipoEspecifico;
    private DataModel lista;
    private String cadena = "";
    private int idRuta;
    private int idCiudad;
    private String opcionDestino;
    private boolean popUp = false;
    private boolean crearPopUp = false;
    private boolean modificarPopUp = false;
    private boolean cambiarPopUp = false;
    private boolean detallePop = false;
    private boolean mostarPanel = false;
    private DataModel listaTipo = new ArrayDataModel();
    private Map<Integer, Boolean> filasSeleccionadas = new HashMap<Integer, Boolean>();
    private List listaFilasSeleccionadas;
    private SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo;

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo ruta - INICIO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    /**
     * MLUIS 28/11/2013
     *
     * @return
     */
    public List<SelectItem> listaTipoEspecificoRuta() {
	List<TipoEspecificoVo> l = tipoEspecificoService.traerTipoEspecificoPorRango(21, 22);
	List<SelectItem> lis = new ArrayList<SelectItem>();

	for (TipoEspecificoVo tipoEspecificoVo : l) {
	    SelectItem item = new SelectItem(tipoEspecificoVo.getId(), tipoEspecificoVo.getNombre());
	    lis.add(item);
	    if (getIdTipoEspecifico() == -1) {
		setIdTipoEspecifico(tipoEspecificoVo.getId());
	    }
	}
	return lis;
    }

    public DataModel traerRuta() {
	try {
	    setLista(new ListDataModel(sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(sesion.getOficinaActual().getId(), getIdTipoEspecifico())));
	    return getLista();
	} catch (Exception e) {
	    return null;
	}
    }

    public boolean buscarRutaUsado() {
	boolean v = false;
	v = sgViajeImpl.buscarRutaUsada(getRutaTerrestreVo().getId());
	return v;
    }

    public void eliminarRuta() {
	sgRutaTerrestreImpl.modificarRutaTerrestre(sesion.getUsuario(), getRutaTerrestreVo(), Constantes.ELIMINADO);
    }

    public RutaTerrestreVo buscarRutaPorNombre() {
	if (getCadena().isEmpty()) {
	    return sgRutaTerrestreImpl.buscarPorNombre(getRutaTerrestreVo().getNombre());
	} else {
	    if (getCadena().equals(getRutaTerrestreVo().getNombre())) {
		return null;
	    } else {
		return sgRutaTerrestreImpl.buscarPorNombre(getRutaTerrestreVo().getNombre());
	    }
	}
    }

    public void completarRuta() {
	sgRutaTerrestreImpl.guardarRutaTerrestre(sesion.getUsuario(), getRutaTerrestreVo(), sesion.getOficinaActual().getId(), getIdTipoEspecifico());
    }

    public void modificarRuta() {
	sgRutaTerrestreImpl.modificarRutaTerrestre(sesion.getUsuario(), getRutaTerrestreVo(), Constantes.NO_ELIMINADO);
    }
//*************  FIN ruta************************/

    /// *******************************INICIO Detalle RUTA ***********************************//
    public DataModel traerDetalleRuta() {
	//Oficina
	switch (getIdTipoEspecifico()) {
	case Constantes.RUTA_TIPO_OFICINA:
	    setLista(new ListDataModel(sgDetalleRutaTerrestreImpl.getAllSgDetalleRutaTerrestreBySgRutaTerrestre(getIdRuta(), "nombre", true, false)));
	    break;
	case Constantes.RUTA_TIPO_CIUDAD:
	    //Ciudad
	    setLista(new ListDataModel(sgDetalleRutaCiudadImpl.traerDetalleRutaPorRuta(getIdRuta(), Constantes.NO_ELIMINADO)));
	    break;
	}
	return getLista();
    }

    public DataModel traerDetalleRutaPop() throws SIAException {
	if (getRutaTerrestreVo() != null) {
	    if (getIdTipoEspecifico() == Constantes.RUTA_TIPO_OFICINA) {
		setLista(new ListDataModel(sgDetalleRutaTerrestreImpl.getAllSgDetalleRutaTerrestreBySgRutaTerrestre(getRutaTerrestreVo().getId(), "nombre", true, false)));

	    } else if (getIdTipoEspecifico() == Constantes.RUTA_TIPO_CIUDAD) {
		//Ciudad
		setLista(new ListDataModel(sgDetalleRutaCiudadImpl.traerDetalleRutaPorRuta(getRutaTerrestreVo().getId(), Constantes.NO_ELIMINADO)));
	    }
	    return getLista();

	    //return new ListDataModel(sgDetalleRutaTerrestreImpl.getDetailByRuote(getRutaTerrestreVo().getId(), Constantes.NO_ELIMINADO));
	}
	return null;
    }

    public List<SelectItem> listaRuta() {
	List<SelectItem> l = new ArrayList<SelectItem>();
	List<RutaTerrestreVo> lc;
	try {
	    lc = sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(sesion.getOficinaActual().getId(), getIdTipoEspecifico());
	    for (RutaTerrestreVo rt : lc) {
		SelectItem item = new SelectItem(rt.getId(), rt.getNombre());
		l.add(item);
	    }
	} catch (Exception e) {
	    UtilLog4j.log.info(this, "Aquí en la excepción");
	}
	return l;
    }

    public DataModel traerOficina() throws SIAException {
	List<OficinaVO> lo = new ArrayList<OficinaVO>();
	OficinaVO oficinaVO;
	for (SgOficina sgOfi : sgOficinaImpl.getOfficeWhitoutCurrent(sesion.getOficinaActual().getId())) {
	    oficinaVO = new OficinaVO();
	    oficinaVO.setId(sgOfi.getId());
	    oficinaVO.setCiudad(sgOfi.getSgDireccion().getSiCiudad().getNombre());
	    oficinaVO.setNombre(sgOfi.getNombre());
	    lo.add(oficinaVO);

	}
	setListaTipo(new ListDataModel(lo));
	return getListaTipo();
    }

    public List<OficinaVO> filtarFilasSeleccionadasDetalleRuta() {
//        DataModel<SgOficina> lt = getListaTipo();

	DataModel<OficinaVO> lt = getListaTipo();
	List<OficinaVO> l = new ArrayList<OficinaVO>();
	setListaFilasSeleccionadas(new ArrayList<OficinaVO>());
	UtilLog4j.log.info(this, "Filas seleccionadas :" + getFilasSeleccionadas().size());
	for (OficinaVO sgC : lt) {
	    if (getFilasSeleccionadas().get(sgC.getId()).booleanValue()) {
		l.add(sgC);
		getFilasSeleccionadas().remove(sgC.getId());
	    }
	}
	setListaFilasSeleccionadas(l);
	return getListaFilasSeleccionadas();
    }

    public List<SelectItem> listaCiudad() {
	List<SiCiudadVO> lc = siCiudadImpl.findAllNative("nombre", true, false);
	List<SelectItem> li = new ArrayList<SelectItem>();
	for (SiCiudadVO siCiudadVO : lc) {
	    SelectItem item = new SelectItem(siCiudadVO.getId(), siCiudadVO.getNombre());
	    li.add(item);
	}
	return li;
    }

    public int verificaDestino() {
	int t = 0;
	List<OficinaVO> lt = getListaFilasSeleccionadas();
	for (OficinaVO sgOfi : lt) {
	    if (sgOfi.isDestino()) {
		t++;
	    }
	}
	return t;
    }

    public boolean buscarDetalleRuta() {
	return sgDetalleRutaTerrestreImpl.findDetailRoute(getListaFilasSeleccionadas(), getIdRuta());
    }

    public boolean buscarRutaCiudad() {
	SgDetalleRutaTerrestreVo cvo = sgDetalleRutaCiudadImpl.buscarRutaCiudadPorOficinaCiudad(sesion.getOficinaActual().getId(), getIdCiudad());
	if (cvo == null) {
	    return false;
	} else {
	    return true;
	}
    }

    public void guardarDetalleRuta() throws SIAException {
	sgDetalleRutaTerrestreImpl.saveDetailRoute(sesion.getUsuario(), getListaFilasSeleccionadas(), getIdRuta());
    }

    public void guardarDetalleRutaCiudad() {
	sgDetalleRutaCiudadImpl.guardarDetalleRuta(sesion.getUsuario(), getIdCiudad(), getRutaTerrestreVo().getId());
    }

    public <T> List<T> dataModelAList(DataModel m) {
	return (List<T>) m.getWrappedData();
    }

    public void eliminarDetalleRuta() throws SIAException {
	List<SgDetalleRutaTerrestreVo> l = dataModelAList(getLista());
	if (getIdTipoEspecifico() == Constantes.RUTA_TIPO_OFICINA) {
	    sgDetalleRutaTerrestreImpl.deleteDetailRoute(sesion.getUsuario(), l);
	} else {
	    sgDetalleRutaCiudadImpl.eliminarRuta(sesion.getUsuario(), l);
	}
    }
    // ********************************FIN Detalle RUTA  ************************************//

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the rutaTerrestreVo
     */
    public RutaTerrestreVo getRutaTerrestreVo() {
	return rutaTerrestreVo;
    }

    /**
     * @param rutaTerrestreVo the rutaTerrestreVo to set
     */
    public void setRutaTerrestreVo(RutaTerrestreVo rutaTerrestreVo) {
	this.rutaTerrestreVo = rutaTerrestreVo;
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
     * @return the cadena
     */
    public String getCadena() {
	return cadena;
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
	this.cadena = cadena;
    }

    /**
     * @return the idRuta
     */
    public int getIdRuta() {
	return idRuta;
    }

    /**
     * @param idRuta the idRuta to set
     */
    public void setIdRuta(int idRuta) {
	this.idRuta = idRuta;
    }

    /**
     * @return the idCiudad
     */
    public int getIdCiudad() {
	return idCiudad;
    }

    /**
     * @param idCiudad the idCiudad to set
     */
    public void setIdCiudad(int idCiudad) {
	this.idCiudad = idCiudad;
    }

    /**
     * @return the listaFilasSeleccionadas
     */
    public List getListaFilasSeleccionadas() {
	return listaFilasSeleccionadas;
    }

    /**
     * @param listaFilasSeleccionadas the listaFilasSeleccionadas to set
     */
    public void setListaFilasSeleccionadas(List listaFilasSeleccionadas) {
	this.listaFilasSeleccionadas = listaFilasSeleccionadas;
    }

    /**
     * @return the listaTipo
     */
    public DataModel getListaTipo() {
	return listaTipo;
    }

    /**
     * @param listaTipo the listaTipo to set
     */
    public void setListaTipo(DataModel listaTipo) {
	this.listaTipo = listaTipo;
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
     * @return the cambiarPopUp
     */
    public boolean isCambiarPopUp() {
	return cambiarPopUp;
    }

    /**
     * @param cambiarPopUp the cambiarPopUp to set
     */
    public void setCambiarPopUp(boolean cambiarPopUp) {
	this.cambiarPopUp = cambiarPopUp;
    }

    /**
     * @return the detallePop
     */
    public boolean isDetallePop() {
	return detallePop;
    }

    /**
     * @param detallePop the detallePop to set
     */
    public void setDetallePop(boolean detallePop) {
	this.detallePop = detallePop;
    }

    /**
     * @return the sgDetalleRutaTerrestreVo
     */
    public SgDetalleRutaTerrestreVo getSgDetalleRutaTerrestreVo() {
	return sgDetalleRutaTerrestreVo;
    }

    /**
     * @param sgDetalleRutaTerrestreVo the sgDetalleRutaTerrestreVo to set
     */
    public void setSgDetalleRutaTerrestreVo(SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo) {
	this.sgDetalleRutaTerrestreVo = sgDetalleRutaTerrestreVo;
    }

    /**
     * @return the mostarPanel
     */
    public boolean isMostarPanel() {
	return mostarPanel;
    }

    /**
     * @param mostarPanel the mostarPanel to set
     */
    public void setMostarPanel(boolean mostarPanel) {
	this.mostarPanel = mostarPanel;
    }

    /**
     * @return the opcionDestino
     */
    public String getOpcionDestino() {
	return opcionDestino;
    }

    /**
     * @param opcionDestino the opcionDestino to set
     */
    public void setOpcionDestino(String opcionDestino) {
	this.opcionDestino = opcionDestino;
    }

    /**
     * @return the filasSeleccionadas
     */
    public Map<Integer, Boolean> getFilasSeleccionadas() {
	return filasSeleccionadas;
    }

    /**
     * @param filasSeleccionadas the filasSeleccionadas to set
     */
    public void setFilasSeleccionadas(Map<Integer, Boolean> filasSeleccionadas) {
	this.filasSeleccionadas = filasSeleccionadas;
    }

}
