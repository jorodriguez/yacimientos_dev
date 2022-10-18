/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.catalogo.bean;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.sgl.catalogo.bean.model.RutaModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "rutaBean")
@RequestScoped
public class RutaBean {

    /**
     * Creates a new instance of RutaBean
     */
    public RutaBean() {
    }

    @ManagedProperty(value = "#{rutaModel}")
    private RutaModel rutaModel;

    /////***********************************************INICIO Ruta  /////
    public void agregarRuta(ActionEvent event) {
	rutaModel.setRutaTerrestreVo(new RutaTerrestreVo());
	rutaModel.setCrearPopUp(true);
    }

    public List<SelectItem> getListaTipoEspecificoRuta() {
	try {
	    return rutaModel.listaTipoEspecificoRuta();
	} catch (Exception e) {
	    return null;
	}
    }

    public void cambiarTipoRuta(ValueChangeEvent event) {
	rutaModel.setIdTipoEspecifico((Integer) event.getNewValue());
	rutaModel.traerRuta();

    }

    public DataModel getTraerRuta() {
	try {
	    return rutaModel.traerRuta();
	} catch (Exception e) {
	    return null;
	}
    }

    public void seleccionarRuta(ActionEvent e) {
	rutaModel.setRutaTerrestreVo((RutaTerrestreVo) rutaModel.getLista().getRowData());
	rutaModel.setModificarPopUp(true);
    }

    public void eliminarRuta(ActionEvent event) {
	rutaModel.setRutaTerrestreVo((RutaTerrestreVo) rutaModel.getLista().getRowData());
	if (rutaModel.buscarRutaUsado()) {
	    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.viaje.ruta.usado"));//"No es posible eliminar el país, se ha utilizado");
	} else {
	    rutaModel.eliminarRuta();
	    rutaModel.setRutaTerrestreVo(null);
	}
    }

    public void verDetalleRuta(ActionEvent event) {
	try {
	    rutaModel.setRutaTerrestreVo((RutaTerrestreVo) rutaModel.getLista().getRowData());
	    rutaModel.setIdRuta(rutaModel.getRutaTerrestreVo().getId());
	    rutaModel.traerDetalleRutaPop();
	    rutaModel.setDetallePop(true);
	} catch (SIAException ex) {
	    UtilLog4j.log.fatal(this, "ex: " + ex.getMessage());
	}
    }

    public DataModel getTraerDetalleRutaPop() {
	try {
	    return rutaModel.traerDetalleRutaPop();
	} catch (SIAException ex) {
	    UtilLog4j.log.fatal(this, "ex: " + ex.getMessage());
	}
	return null;
    }

    public void cerrarDetalleRuta(ActionEvent event) {
	rutaModel.setRutaTerrestreVo(null);
	rutaModel.setIdRuta(-1);
	rutaModel.setDetallePop(false);
    }

    public void guardarRuta(ActionEvent event) {
	if (rutaModel.getRutaTerrestreVo().getNombre().isEmpty()) {
	    FacesUtils.addErrorMessage("frmCrearRuta:rutaN", FacesUtils.getKeyResourceBundle("sgl.viaje.ruta.requerido"));
	} else if (rutaModel.buscarRutaPorNombre() != null) {
	    FacesUtils.addErrorMessage("frmCrearRuta:rutaN", FacesUtils.getKeyResourceBundle("sgl.viaje.ruta.existe"));
	} else {
	    rutaModel.completarRuta();
	    rutaModel.setCadena("");
	    rutaModel.setRutaTerrestreVo(null);
	    rutaModel.setPopUp(false);
	}
    }

    public void cerrarPopRuta(ActionEvent event) {
	rutaModel.setRutaTerrestreVo(null);
	rutaModel.setIdRuta(-1);
	rutaModel.setPopUp(false);
    }

    /**
     * Valida que una ruta pueda modificarse
     *
     * @return 'true' si la ruta es modificable
     */
    public boolean validateUpdateSgRutaTerrestre() {
	UtilLog4j.log.fatal(this, "CatalogoBean.validateUpdateSgRutaTerrestre()");
	RutaTerrestreVo rutaTerrestreEncontrada = this.rutaModel.buscarRutaPorNombre();

	if (rutaTerrestreEncontrada == null) {
	    return true;
	} else {
	    return rutaTerrestreEncontrada.getId() == rutaModel.getRutaTerrestreVo().getId();
	}
    }

    public void completarModificacionRuta(ActionEvent event) {
	if (rutaModel.getRutaTerrestreVo().getNombre().isEmpty()) {
	    FacesUtils.addErrorMessage("frmCrearRuta:rutaNM", FacesUtils.getKeyResourceBundle("sgl.viaje.ruta.requerido"));
	} else if (!validateUpdateSgRutaTerrestre()) {
	    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.viaje.ruta.existe"));
	} else {
	    rutaModel.modificarRuta();
	    rutaModel.setRutaTerrestreVo(null);
	    rutaModel.setModificarPopUp(false);
	}
    }

    public void cerrarPopModificarRuta(ActionEvent event) {
	rutaModel.setRutaTerrestreVo(null);
	rutaModel.setModificarPopUp(false);
    }

    ///////*********************************************FIN Ruta **********************************
    ///////*********************************************INICIO DETALLE RUTA**********************************
    public void buscarDetalleRuta(ValueChangeEvent valueChangeEvent) {
	rutaModel.setIdRuta((Integer) valueChangeEvent.getNewValue());
	if (rutaModel.getIdRuta() > 0) {
	    try {
		rutaModel.traerDetalleRuta();
	    } catch (Exception ex) {
		UtilLog4j.log.fatal(this, "Excepcion: " + ex.getMessage());
		Logger.getLogger(CatalogoBean.class.getName()).log(Level.SEVERE, null, ex);
	    }
	} else {
	    rutaModel.setLista(null);
	}
    }

    public List<SelectItem> getListaRuta() {
	return rutaModel.listaRuta();
    }

    public DataModel getTraerDetalleRuta() {
	return rutaModel.getLista();
    }

    public void agregarDetalleRuta(ActionEvent event) {
	rutaModel.setRutaTerrestreVo((RutaTerrestreVo) rutaModel.getLista().getRowData());
	rutaModel.setPopUp(true);
	rutaModel.setSgDetalleRutaTerrestreVo(new SgDetalleRutaTerrestreVo());
    }

    public DataModel getTraerOficina() {
	try {
	    return rutaModel.traerOficina();
	} catch (SIAException ex) {
	    UtilLog4j.log.fatal(this, "Excepcion: " + ex.getMessage());
	    Logger.getLogger(CatalogoBean.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

    public List<SelectItem> getListaCiudad() {
	return rutaModel.listaCiudad();
    }

    public void guardarDetalleRuta(ActionEvent event) {
	if (rutaModel.getIdTipoEspecifico() == Constantes.RUTA_TIPO_OFICINA) {
	    if (rutaModel.filtarFilasSeleccionadasDetalleRuta().size() > 0) {
		try {
		    if (rutaModel.verificaDestino() == 1) {
			if (rutaModel.buscarDetalleRuta()) {
			    rutaModel.guardarDetalleRuta();
			    rutaModel.traerDetalleRuta();
			    rutaModel.setMostarPanel(false);
			    rutaModel.setPopUp(false);
			    rutaModel.setSgDetalleRutaTerrestreVo(null);
			    rutaModel.setListaFilasSeleccionadas(null);
			    rutaModel.setListaTipo(null);
			} else {
			    FacesUtils.addInfoMessage("Existe una ruta igual");
			}
		    } else {
			FacesUtils.addInfoMessage("sgl.viaje.ruta.eligir.destino");//Es necesario elegir un solo destino");
		    }
		} catch (SIAException ex) {
		    Logger.getLogger(CatalogoBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sgl.viaje.ruta.seleccion"));
	    }
	} else if (rutaModel.getIdTipoEspecifico() == Constantes.RUTA_TIPO_CIUDAD) {
	    if (!rutaModel.buscarRutaCiudad()) {
		try {
		    rutaModel.guardarDetalleRutaCiudad();
		    rutaModel.traerDetalleRuta();
		    rutaModel.setMostarPanel(false);
		    rutaModel.setPopUp(false);
		    rutaModel.setSgDetalleRutaTerrestreVo(null);
		    rutaModel.setListaFilasSeleccionadas(null);
		    rutaModel.setListaTipo(null);
		} catch (Exception ex) {
		    Logger.getLogger(CatalogoBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		FacesUtils.addInfoMessage("Existe una ruta igual");
	    }
	}
    }

    public void cancelarDetalleRuta(ActionEvent event) {
	rutaModel.setPopUp(false);
	rutaModel.setSgDetalleRutaTerrestreVo(null);
	rutaModel.setListaTipo(null);
	rutaModel.setListaFilasSeleccionadas(null);
    }

    public void eliminarDetalleRuta(ActionEvent event) {
	try {
	    rutaModel.eliminarDetalleRuta();
	    rutaModel.traerDetalleRuta();
	} catch (SIAException ex) {
	    Logger.getLogger(CatalogoBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void mostrarPanelDestino(ValueChangeEvent event) {
	rutaModel.setMostarPanel(true);
    }

    public void seleccionarOpcionDestino(ValueChangeEvent valueChangeEvent) {
	rutaModel.setOpcionDestino((String) valueChangeEvent.getNewValue());
    }
    ///////*********************************************FIN DETALLE RUTA**********************************

    /**
     * @param rutaModel the rutaModel to set
     */
    public void setRutaModel(RutaModel rutaModel) {
	this.rutaModel = rutaModel;
    }

    /**
     * @return the rutaTerrestreVo
     */
    public RutaTerrestreVo getRutaTerrestreVo() {
	return rutaModel.getRutaTerrestreVo();
    }

    /**
     * @param rutaTerrestreVo the rutaTerrestreVo to set
     */
    public void setRutaTerrestreVo(RutaTerrestreVo rutaTerrestreVo) {
	rutaModel.setRutaTerrestreVo(rutaTerrestreVo);
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
	return rutaModel.getIdTipoEspecifico();
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
	rutaModel.setIdTipoEspecifico(idTipoEspecifico);
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
	return rutaModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
	rutaModel.setLista(lista);
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
	return rutaModel.getCadena();
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
	rutaModel.setCadena(cadena);
    }

    /**
     * @return the idRuta
     */
    public int getIdRuta() {
	return rutaModel.getIdRuta();
    }

    /**
     * @param idRuta the idRuta to set
     */
    public void setIdRuta(int idRuta) {
	rutaModel.setIdRuta(idRuta);
    }

    /**
     * @return the idCiudad
     */
    public int getIdCiudad() {
	return rutaModel.getIdCiudad();
    }

    /**
     * @param idCiudad the idCiudad to set
     */
    public void setIdCiudad(int idCiudad) {
	rutaModel.setIdCiudad(idCiudad);
    }

    /**
     * @return the listaFilasSeleccionadas
     */
    public List getListaFilasSeleccionadas() {
	return rutaModel.getListaFilasSeleccionadas();
    }

    /**
     * @param listaFilasSeleccionadas the listaFilasSeleccionadas to set
     */
    public void setListaFilasSeleccionadas(List listaFilasSeleccionadas) {
	rutaModel.setListaFilasSeleccionadas(listaFilasSeleccionadas);
    }

    /**
     * @return the listaTipo
     */
    public DataModel getListaTipo() {
	return rutaModel.getListaTipo();
    }

    /**
     * @param listaTipo the listaTipo to set
     */
    public void setListaTipo(DataModel listaTipo) {
	rutaModel.setListaTipo(listaTipo);
    }

    /**
     * @return the popUp
     */
    public boolean isPopUp() {
	return rutaModel.isPopUp();
    }

    /**
     * @param popUp the popUp to set
     */
    public void setPopUp(boolean popUp) {
	rutaModel.setPopUp(popUp);
    }

    /**
     * @return the crearPopUp
     */
    public boolean isCrearPopUp() {
	return rutaModel.isCrearPopUp();
    }

    /**
     * @param crearPopUp the crearPopUp to set
     */
    public void setCrearPopUp(boolean crearPopUp) {
	rutaModel.setCrearPopUp(crearPopUp);
    }

    /**
     * @return the modificarPopUp
     */
    public boolean isModificarPopUp() {
	return rutaModel.isModificarPopUp();
    }

    /**
     * @param modificarPopUp the modificarPopUp to set
     */
    public void setModificarPopUp(boolean modificarPopUp) {
	rutaModel.setModificarPopUp(modificarPopUp);
    }

    /**
     * @return the cambiarPopUp
     */
    public boolean isCambiarPopUp() {
	return rutaModel.isCambiarPopUp();
    }

    /**
     * @param cambiarPopUp the cambiarPopUp to set
     */
    public void setCambiarPopUp(boolean cambiarPopUp) {
	rutaModel.setCambiarPopUp(cambiarPopUp);
    }

    /**
     * @return the detallePop
     */
    public boolean isDetallePop() {
	return rutaModel.isDetallePop();
    }

    /**
     * @param detallePop the detallePop to set
     */
    public void setDetallePop(boolean detallePop) {
	rutaModel.setDetallePop(detallePop);
    }

    /**
     * @return the sgDetalleRutaTerrestreVo
     */
    public SgDetalleRutaTerrestreVo getSgDetalleRutaTerrestreVo() {
	return rutaModel.getSgDetalleRutaTerrestreVo();
    }

    /**
     * @param sgDetalleRutaTerrestreVo the sgDetalleRutaTerrestreVo to set
     */
    public void setSgDetalleRutaTerrestreVo(SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo) {
	rutaModel.setSgDetalleRutaTerrestreVo(sgDetalleRutaTerrestreVo);
    }

    /**
     * @return the mostarPanel
     */
    public boolean isMostarPanel() {
	return rutaModel.isModificarPopUp();
    }

    /**
     * @param mostarPanel the mostarPanel to set
     */
    public void setMostarPanel(boolean mostarPanel) {
	rutaModel.setMostarPanel(mostarPanel);
    }

    /**
     * @return the opcionDestino
     */
    public String getOpcionDestino() {
	return rutaModel.getOpcionDestino();
    }

    /**
     * @param opcionDestino the opcionDestino to set
     */
    public void setOpcionDestino(String opcionDestino) {
	rutaModel.setOpcionDestino(opcionDestino);
    }

    /**
     * @return the filasSeleccionadas
     */
    public Map<Integer, Boolean> getFilasSeleccionadas() {
	return rutaModel.getFilasSeleccionadas();
    }

    /**
     * @param filasSeleccionadas the filasSeleccionadas to set
     */
    public void setFilasSeleccionadas(Map<Integer, Boolean> filasSeleccionadas) {
	rutaModel.setFilasSeleccionadas(filasSeleccionadas);
    }

}
