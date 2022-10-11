/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.catalogo.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.modelo.SgAerolinea;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgEmpresa;
import sia.modelo.SgLugar;
import sia.modelo.SgMarca;
import sia.modelo.SgModelo;
import sia.modelo.SgMotivo;
import sia.modelo.SgOficina;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgTipoTipoEspecifico;
import sia.modelo.SiCondicion;
import sia.modelo.SiOperacion;
import sia.modelo.SiPais;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.semaforo.vo.SemaforoVo;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.RolTipoSolicitudVo;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.UsuarioRolGerenciaVo;
import sia.modelo.sgl.vo.SgDetalleRutaTerrestreVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.sgl.catalogo.bean.model.CatalogoBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 * @author mluis
 *//*
 */

@Named(value = "catalogoBean")
@RequestScoped
public class CatalogoBean implements Serializable {

    @ManagedProperty(value = "#{catalogoBeanModel}")
    private CatalogoBeanModel catalogoBeanModel;

    /**
     * Creates a new instance of CatalogoBean
     */
    public CatalogoBean() {
    }

    public String goToRutaTerrestre() {
	return "/vistas/sgl/viaje/catalogo/catalogoRuta";
    }

    public String goToDetalleRutaTerrestre() {
	catalogoBeanModel.setIdTipoEspecifico(Constantes.RUTA_TIPO_OFICINA);
	catalogoBeanModel.setLista(null);
	return "/vistas/sgl/viaje/catalogo/detalleRuta";
    }

    public String goToCatalogoEmpresa() {
	catalogoBeanModel.traerEmpresas();
	return "/vistas/sgl/administrar/catalogoEmpresa";
    }

    public String goToCatalogoInvitado() {
	//     catalogoBeanModel.beginConversationCatalogoInvitado();
	catalogoBeanModel.setIdEmpresa(-1);
	catalogoBeanModel.traerInvitados();
	return "/vistas/sgl/administrar/catalogoInvitado";
    }

    public String goToCatalogoSgLugar() {
	//     catalogoBeanModel.beginConversationCatalogoSgLugar();
	catalogoBeanModel.iniciaControladorPopPupFalse("popupCreateSgLugar");
	catalogoBeanModel.iniciaControladorPopPupFalse("popupUpdateSgLugar");
	catalogoBeanModel.iniciaControladorPopPupFalse("popupDeleteSgLugar");
	return "/vistas/sgl/administrar/catalogoSgLugar";
    }

    /**
     * Este método limpia el valor de un Componente HTML
     *
     * @param nombreFormulario
     * @param nombreComponente
     */
    public void clearComponent(String nombreFormulario, String nombreComponente) {
	UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
	try {
	    FacesContext context = FacesContext.getCurrentInstance();
	    UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
	    UIComponent parentComponent = component.getParent();
	    parentComponent.getChildren().clear();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
	}
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Tipos - START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void agregarTipo(ActionEvent event) {
	catalogoBeanModel.setPopUp(true);
	catalogoBeanModel.setSgTipo(new SgTipo());
	catalogoBeanModel.setCrearPopUp(true);
    }

    public DataModel getTraerTipo() {
	try {
	    return catalogoBeanModel.traerTipo();
	} catch (Exception e) {
	    return null;
	}
    }

    public void guardarTipo(ActionEvent event) {
	if (catalogoBeanModel.getSgTipo().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Nombre es requerido");
	} else {
	    try {
		catalogoBeanModel.guardarTipo();
		catalogoBeanModel.setPopUp(false);
		catalogoBeanModel.setSgTipo(null);
		catalogoBeanModel.setCrearPopUp(false);
		FacesUtils.addInfoMessage("El Tipo fue creado satisfactoriamente");
	    } catch (SIAException siae) {
		FacesUtils.addErrorMessage(siae.getMessage());
		UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
		catalogoBeanModel.setSgTipo(null);
		catalogoBeanModel.setPopUp(false);
		catalogoBeanModel.setCrearPopUp(false);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		FacesUtils.addErrorMessage(new SIAException().getMessage());
		catalogoBeanModel.setSgTipo(null);
		catalogoBeanModel.setPopUp(false);
		catalogoBeanModel.setCrearPopUp(false);
	    }
	}
    }

    public void seleccionarTipo(ActionEvent event) {
	catalogoBeanModel.setPopUp(true);
	catalogoBeanModel.setSgTipo((SgTipo) catalogoBeanModel.getListaTipo().getRowData());
	catalogoBeanModel.setModificarPopUp(true);
    }

    public void modificarTipo(ActionEvent event) {
	if (catalogoBeanModel.getSgTipo().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Es necesario agregar el nombre");
	} else {
	    try {
		catalogoBeanModel.modificarTipo();
		catalogoBeanModel.setPopUp(false);
		catalogoBeanModel.setSgTipo(null);
		catalogoBeanModel.setCrearPopUp(false);
		FacesUtils.addInfoMessage("El Tipo fue actualizado satisfactoriamente");
	    } catch (SIAException siae) {
		FacesUtils.addErrorMessage(siae.getMessage());
		UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
		catalogoBeanModel.setSgTipo(null);
		catalogoBeanModel.setPopUp(false);
		catalogoBeanModel.setCrearPopUp(false);
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		FacesUtils.addErrorMessage(new SIAException().getMessage());
		catalogoBeanModel.setSgTipo(null);
		catalogoBeanModel.setPopUp(false);
		catalogoBeanModel.setCrearPopUp(false);
	    }
	}
    }

    public void eliminarTipo(ActionEvent event) {
	catalogoBeanModel.setSgTipo((SgTipo) catalogoBeanModel.getListaTipo().getRowData());
	if (catalogoBeanModel.buscarTipoEnTipoTipoEspecifico()) {
	    FacesUtils.addErrorMessage("No es posible eliminar el tipo debido a que ya está siendo utilizado en otras partes del Sistema");
	} else {
	    try {
		catalogoBeanModel.eliminarTipo();
		FacesUtils.addInfoMessage("El Tipo fue eliminado satisfactoriamente");
	    } catch (SIAException siae) {
		FacesUtils.addErrorMessage(siae.getMessage());
		UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());
		e.printStackTrace();
		FacesUtils.addErrorMessage(new SIAException().getMessage());
	    }
	}
    }

    public void cerrarPop(ActionEvent event) {
	if (catalogoBeanModel.isPopUp()) {
	    catalogoBeanModel.setPopUp(false);
	    catalogoBeanModel.setCrearPopUp(false);
	    catalogoBeanModel.setModificarPopUp(false);
	    catalogoBeanModel.setSgTipo(null);
	    catalogoBeanModel.setSgMotivo(null);
	    catalogoBeanModel.setMostarPanel(false);
	}
    }

    public void cerrarPopModificar(ActionEvent event) {
	catalogoBeanModel.traerInvitados();
	catalogoBeanModel.setModificarPopUp(false);
	catalogoBeanModel.setSgMotivo(null);
    }

    //Tipo especifico
    public List<SelectItem> getTraerTipoGeneral() {
	return catalogoBeanModel.traerTipoGeneral();
    }

    public void agregarTipoEspecifico(ActionEvent event) {
	catalogoBeanModel.setSgTipoEspecifico(new SgTipoEspecifico());
	catalogoBeanModel.setPopUp(true);
	catalogoBeanModel.setCrearPopUp(true);
	catalogoBeanModel.setMostarPanel(false);
	catalogoBeanModel.buscarTipoPorId();
    }

    public void createTipoEspecifico(ActionEvent actionEvent) {
	try {
	    catalogoBeanModel.saveTipoEspecifico();
	    if (catalogoBeanModel.getMensaje() != null && !catalogoBeanModel.getMensaje().equals("")) {
		FacesUtils.addInfoMessage(catalogoBeanModel.getMensaje());
	    }
	    FacesUtils.addInfoMessage("El Tipo de " + catalogoBeanModel.getCadena() + " fué guardado exitosamente");
	    ocultarPopupCrearTipoEspecifico(actionEvent);
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    ocultarPopupCrearTipoEspecifico(actionEvent);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    ocultarPopupCrearTipoEspecifico(actionEvent);
	}
    }

    public void traerTipoEspecificoPorTipo(ValueChangeEvent valueChangeEvent) {
	catalogoBeanModel.setIdTipo((Integer) valueChangeEvent.getNewValue());
	if (catalogoBeanModel.getIdTipo() > 0) {
	    catalogoBeanModel.traerTipoEspecificoPorTipo();
	} else {
	    catalogoBeanModel.setIdTipo(-1);
	    catalogoBeanModel.traerTipoEspecificoPorTipo();
	}
    }

    public DataModel getTraerTipoEspecifico() {
	try {
	    return catalogoBeanModel.getListaTipoTipo();
	} catch (Exception e) {
	    return null;
	}
    }

    public void asignarTipoEspecifico(ActionEvent event) {
	if (catalogoBeanModel.filtarFilasSeleccionadas().size() > 0) {
	    catalogoBeanModel.asignarTipoEspecifico();
	    catalogoBeanModel.setMostarPanel(false);
	    catalogoBeanModel.setPopUp(false);
	    catalogoBeanModel.setSgTipoEspecifico(null);
	    catalogoBeanModel.traerTipoEspecificoPorTipo();
	} else {
	    FacesUtils.addInfoMessage("Es necesario seleccionar al menos una opción");
	}
    }

    public void mostrarPanelAgregarTipoEspecifico(ActionEvent event) {
	catalogoBeanModel.setMostarPanel(true);
	catalogoBeanModel.setSgTipoEspecifico(new SgTipoEspecifico());
    }

    public DataModel getTraerTipoEspecificoSinTipo() {
	try {
	    return catalogoBeanModel.traerTipoEspecificoSinTipo();
	} catch (Exception e) {
	    return null;
	}
    }

    public void eliminarTipoEspecifico(ActionEvent event) {
	catalogoBeanModel.setSgTipoEspecifico((SgTipoEspecifico) catalogoBeanModel.getListaTipoEspecifico().getRowData());
	try {
	    catalogoBeanModel.eliminarTipoEspecifico();
	    catalogoBeanModel.traerTipoEspecificoSinTipo();
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());

	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	}
    }

    public void guardarTipoEspecifico(ActionEvent event) {
	if (catalogoBeanModel.getSgTipoEspecifico().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Es necesario agragar el nombre");
	} else {
	    catalogoBeanModel.guardarTipoEspecifico();
	    catalogoBeanModel.setSgTipoEspecifico(null);
	    catalogoBeanModel.setPopUp(false);
	    catalogoBeanModel.traerTipoEspecificoPorTipo();
	    catalogoBeanModel.setMostarPanel(false);
	}
    }

    public void seleccionarTipoTipoEspecifico(ActionEvent event) {
	catalogoBeanModel.setSgTipoTipoEspecifico((SgTipoTipoEspecifico) catalogoBeanModel.getListaTipoTipo().getRowData());
	catalogoBeanModel.setPago(catalogoBeanModel.getSgTipoTipoEspecifico().getSgTipoEspecifico().isPago());
	catalogoBeanModel.setPopUp(true);
	catalogoBeanModel.setModificarPopUp(true);
    }

    public void modificarTipoEspecifico(ActionEvent event) {
	if (catalogoBeanModel.getSgTipoTipoEspecifico().getSgTipoEspecifico().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Es necesario agragar el nombre");
	} else {
	    catalogoBeanModel.modificarTipoEspecifico();
	    catalogoBeanModel.setModificarPopUp(false);
	    catalogoBeanModel.setPopUp(false);
	    catalogoBeanModel.setSgTipoEspecifico(null);
	}
    }

    public void eliminarTipoTipoEspecifico(ActionEvent event) {
	catalogoBeanModel.setSgTipoTipoEspecifico((SgTipoTipoEspecifico) catalogoBeanModel.getListaTipoTipo().getRowData());
	catalogoBeanModel.eliminarTipoTipoEspecifico();
	catalogoBeanModel.traerTipoEspecificoPorTipo();
    }

    public SgTipoEspecifico getTipoEspecificoById() {
	return catalogoBeanModel.getTipoEspecificoById();
    }

    public SgMarca getMarcaById() {
	return catalogoBeanModel.getMarcaById();
    }

    public void mostrarPopupCrearTipoEspecifico(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "mostrarPopupCrearTipoEspecifico");
	catalogoBeanModel.setSgTipoEspecifico(null);
	catalogoBeanModel.setSgTipoEspecifico(new SgTipoEspecifico());
	catalogoBeanModel.inicializaPopUpTrue("popupCrearTipoEspecifico");
	//sesion.getControladorPopups().put("popupCrearTipoEspecifico", Boolean.TRUE);
    }

    public void ocultarPopupCrearTipoEspecifico(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "ocultarPopupCrearTipoEspecifico");
	catalogoBeanModel.setSgTipoEspecifico(null);
	clearComponent("popupCrearTipoEspecificoCatalogoModelo", "nombreTipoEspecifico");
	clearComponent("popupCrearTipoEspecificoCatalogoModelo", "descripcionTipoEspecifico");
	catalogoBeanModel.iniciaControladorPopPupFalse("popupCrearTipoEspecifico");
	//sesion.getControladorPopups().put("popupCrearTipoEspecifico", Boolean.FALSE);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Tipos - END <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Características - START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void cargarCaracteristicasInTableByTipo(ValueChangeEvent valueChangeEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.cargarCaracteristicasInTableByTipo()");
	UtilLog4j.log.info(this, "id tipoSeleccionado: " + valueChangeEvent.getNewValue().toString());
	catalogoBeanModel.setIdTipo(Integer.valueOf(valueChangeEvent.getNewValue().toString()));
	try {
//            catalogoBeanModel.getCaracteristicasByTipo();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    if (e.getMessage().equals("")) {
		FacesUtils.addInfoMessage("Hubo un error al consultar las Características. Porfavor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
	    } else {
		FacesUtils.addInfoMessage(e.getMessage());
	    }
	}
	getCaracteristicasDataModel();
    }

    public DataModel getCaracteristicasDataModel() {
	try {
	    catalogoBeanModel.getAllCaracteristicas();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());

	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    return catalogoBeanModel.getDataModelGeneric();
	}
	return catalogoBeanModel.getDataModelGeneric();
    }

    public void createCaracteristica(ActionEvent actionEvent) {
	try {
	    catalogoBeanModel.createCaracteristica();
	    catalogoBeanModel.reloadAllCaracteristicas();
	    FacesUtils.addInfoMessage("La Característica " + catalogoBeanModel.getNombreCaracteristica() + " fue creada satisfactoriamente");
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());

	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	} finally {
	    ocultarPopupCrearCaracteristica(actionEvent);
	}
    }

    public void updateCaracteristica(ActionEvent actionEvent) {
	try {
	    catalogoBeanModel.updateCaracteristica();
	    catalogoBeanModel.reloadAllCaracteristicas();
	    ocultarPopupActualizarCaracteristica(actionEvent);
	    FacesUtils.addInfoMessage("La Característica fue actualizada satisfactoriamente");
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    ocultarPopupActualizarCaracteristica(actionEvent);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());

	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    ocultarPopupActualizarCaracteristica(actionEvent);
	} finally {
	    try {
		catalogoBeanModel.reloadAllCaracteristicas();
	    } catch (Exception e) {
	    }
	}
    }

    public void deleteCaracteristica(ActionEvent actionEvent) {
	try {
	    catalogoBeanModel.deleteCaracteristica();
	    catalogoBeanModel.reloadAllCaracteristicas();
	    ocultarPopupEliminarCaracteristica(actionEvent);
	    FacesUtils.addInfoMessage("La Característica fue eliminada satisfactoriamente");
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    ocultarPopupEliminarCaracteristica(actionEvent);
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());

	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    ocultarPopupEliminarCaracteristica(actionEvent);
	}
    }

    public void createTipo(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.saveTipo()");
	try {
	    catalogoBeanModel.createTipo();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    if (e.getMessage().equals("")) {
		FacesUtils.addInfoMessage("Hubo un error al guardar el Tipo. Porfavor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
	    } else {
		FacesUtils.addInfoMessage(e.getMessage());
	    }
	}
	ocultarPopupCrearTipo(actionEvent);
    }

    //Popups
    public void mostrarPopupCrearCaracteristica(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.mostrarPopupCrearCaracteristica()");
	catalogoBeanModel.setMrPopupCrearCaracteristica(!catalogoBeanModel.isMrPopupCrearCaracteristica());
	//Dándole memoria a característica
	catalogoBeanModel.setCaracteristica(new SgCaracteristica());
    }

    public void ocultarPopupCrearCaracteristica(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.ocultarPopupCrearCaracteristica()");
	//Quitándole memoria a Característica
	catalogoBeanModel.setCaracteristica(null);
	catalogoBeanModel.setNombreCaracteristica("");
	catalogoBeanModel.setPago(false);
	clearComponent("formPopupCrearCaracteristica", "inpTxtnombreCaracteristica");
	catalogoBeanModel.setMrPopupCrearCaracteristica(!catalogoBeanModel.isMrPopupCrearCaracteristica());
    }

    public void mostrarPopupActualizarCaracteristica(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.mostrarPopupActualizarCaracteristica()");
	//Dándole memoria a Característica
	catalogoBeanModel.setCaracteristica((SgCaracteristica) catalogoBeanModel.getDataModelGeneric().getRowData());
	catalogoBeanModel.setNombreCaracteristica(catalogoBeanModel.getCaracteristica().getNombre());
	catalogoBeanModel.setMrPopupModificarCaracteristica(!catalogoBeanModel.isMrPopupModificarCaracteristica());
    }

    public void ocultarPopupActualizarCaracteristica(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.ocultarPopupActualizarCaracteristica()");
	//Quitándole memoria a Característica
	catalogoBeanModel.setCaracteristica(null);
	catalogoBeanModel.setNombreCaracteristica("");
	clearComponent("formPopupActualizarCaracteristica", "inpTxtnombreCaracteristica");
	catalogoBeanModel.setMrPopupModificarCaracteristica(!catalogoBeanModel.isMrPopupModificarCaracteristica());
    }

    public void mostrarPopupEliminarCaracteristica(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.mostrarPopupEliminarCaracteristica()");
	//Dándole memoria a Característica
	catalogoBeanModel.setCaracteristica((SgCaracteristica) catalogoBeanModel.getDataModelGeneric().getRowData());
	catalogoBeanModel.setMrPopupEliminarCaracteristica(!catalogoBeanModel.isMrPopupEliminarCaracteristica());
    }

    public void ocultarPopupEliminarCaracteristica(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.ocultarPopupEliminarCaracteristica()");
	//Quitándole memoria a Característica
	catalogoBeanModel.setCaracteristica(null);
	catalogoBeanModel.setMrPopupEliminarCaracteristica(!catalogoBeanModel.isMrPopupEliminarCaracteristica());
    }

    public void mostrarPopupCrearTipo(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.mostrarPopupCrearTipo()");
	catalogoBeanModel.setMrPopupCrearTipo(!catalogoBeanModel.isMrPopupCrearTipo());
    }

    public void ocultarPopupCrearTipo(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "CatalogoBean.ocultarPopupCrearTipo()");
	catalogoBeanModel.setNombreTipo("");
	catalogoBeanModel.setDescripcionTipo("");
	catalogoBeanModel.setMrPopupCrearTipo(!catalogoBeanModel.isMrPopupCrearTipo());
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Características - END <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Modelo - START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void selectModelo(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "selectModelo() - " + ((SgModelo) catalogoBeanModel.getDataModelGeneric().getRowData()).getNombre());
	catalogoBeanModel.setModelo((SgModelo) catalogoBeanModel.getDataModelGeneric().getRowData());
    }

    public void givenMemoryModelo(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "givenMemoryModelo()");
	catalogoBeanModel.setModelo(new SgModelo());
	catalogoBeanModel.setMarca(getMarcaById());
    }

    public void removeMemoryModelo(ActionEvent actionEvent) {
	UtilLog4j.log.info(this, "removeMemoryModelo()");
	catalogoBeanModel.setModelo(null);
    }

    public DataModel getModeloDataModel() {
	if (catalogoBeanModel.getIdTipoEspecifico() > 0 && catalogoBeanModel.getIdMarca() > 0) {
	    try {
		catalogoBeanModel.reloadModeloList();
	    } catch (SIAException siae) {
		FacesUtils.addErrorMessage(siae.getMessage());
		UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    } catch (Exception e) {
		UtilLog4j.log.fatal(this, e.getMessage());

		FacesUtils.addErrorMessage(new SIAException().getMessage());
	    }
	    return catalogoBeanModel.getDataModelGeneric();
	}
	return null;
    }

    public List<SelectItem> getTipoEspecificoByTipoListSelectItem() {
	List<SgTipoEspecifico> tipoEspecificoList = null;
	try {
	    tipoEspecificoList = catalogoBeanModel.getTipoEspecificoByTipo();
	    List<SelectItem> tipoEspecificoListItem = new ArrayList<SelectItem>();
	    for (SgTipoEspecifico te : tipoEspecificoList) {
		SelectItem item = new SelectItem(te.getId(), te.getNombre());
		tipoEspecificoListItem.add(item);
	    }
	    return tipoEspecificoListItem;
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    return null;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());

	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    return null;
	}
    }

    public void loadModelosIntoTable(ValueChangeEvent event) {
	catalogoBeanModel.setIdMarca((Integer) event.getNewValue());
	catalogoBeanModel.setMarca(catalogoBeanModel.getMarcaById());
	if (((Integer) event.getNewValue()).intValue() < 1) {
	    catalogoBeanModel.setDataModelGeneric(null);
	} else {
	    if (catalogoBeanModel.getIdTipoEspecifico() > 0 && ((Integer) event.getNewValue()).intValue() > 0) {
		try {
		    catalogoBeanModel.reloadModeloList();
		} catch (SIAException siae) {
		    FacesUtils.addErrorMessage(siae.getMessage());
		    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
		} catch (Exception e) {
		    UtilLog4j.log.fatal(this, e.getMessage());

		    FacesUtils.addErrorMessage(new SIAException().getMessage());
		}
	    }
	}
    }

    public void saveModelo(ActionEvent actionEvent) {
	try {
	    catalogoBeanModel.saveModelo();
	    catalogoBeanModel.reloadModeloList();
	    FacesUtils.addInfoMessage("El Modelo fué guardado exitosamente");
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	}
    }

    public String updateModelo() {
	try {
	    catalogoBeanModel.updateModelo();
	    catalogoBeanModel.reloadModeloList();
	    FacesUtils.addInfoMessage("El Modelo fué actualizado exitosamente");
	    catalogoBeanModel.setModelo(null);
	    return "list";
	} catch (SIAException siae) {
	    catalogoBeanModel.setDataModelGeneric(null);
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    return "list";
	} catch (Exception e) {
	    catalogoBeanModel.setDataModelGeneric(null);
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());

	    return "list";
	}
    }

    public String deleteModelo() {
	try {
	    catalogoBeanModel.deleteModelo();
	    catalogoBeanModel.reloadModeloList();
	    FacesUtils.addInfoMessage("El Modelo fué eliminado exitosamente");
	    catalogoBeanModel.setModelo(null);
	    catalogoBeanModel.iniciaControladorPopPupFalse("popupEliminarModelo");
	    //sesion.getControladorPopups().put("popupEliminarModelo", Boolean.FALSE);
	    return "list";
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    catalogoBeanModel.iniciaControladorPopPupFalse("popupEliminarModelo");
	    //sesion.getControladorPopups().put("popupEliminarModelo", Boolean.FALSE);
	    return "list";
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	    catalogoBeanModel.iniciaControladorPopPupFalse("popupEliminarModelo");
	    //sesion.getControladorPopups().put("popupEliminarModelo", Boolean.FALSE);
	    return "list";
	}
    }

    public String mostrarPopupEliminarModelo() {
	UtilLog4j.log.fatal(this, "mostrarPopupEliminarModelo");
	catalogoBeanModel.inicializaPopUpTrue("popupEliminarModelo");
	//sesion.getControladorPopups().put("popupEliminarModelo", Boolean.TRUE);
	return "";
    }

    public String ocultarPopupEliminarModelo() {
	UtilLog4j.log.fatal(this, "ocultarPopupEliminarModelo");
	catalogoBeanModel.iniciaControladorPopPupFalse("popupEliminarModelo");
	//sesion.getControladorPopups().put("popupEliminarModelo", Boolean.FALSE);
	return "";
    }

    public String goToCreateModelo() {
	UtilLog4j.log.fatal(this, "idTipoEspecifico: " + catalogoBeanModel.getIdTipoEspecifico());
	UtilLog4j.log.fatal(this, "idMarca: " + catalogoBeanModel.getIdMarca());

	if (catalogoBeanModel.getIdTipoEspecifico() != -1) {
	    if (catalogoBeanModel.getIdMarca() != -1) {
		return "create";

	    } else {
		FacesUtils.addErrorMessage("Es necesario seleccionar una Marca");
		return "";
	    }
	} else {
	    FacesUtils.addErrorMessage("Es necesario seleccionar un Tipo de Vehículo");
	    if (catalogoBeanModel.getIdMarca() == -1) {
		FacesUtils.addErrorMessage("Es necesario seleccionar una Marca");
	    }
	    return "";
	}
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Modelo - END <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Marca - START <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void selectMarca(ActionEvent actionEvent) {
	UtilLog4j.log.fatal(this, "selectMarca() - " + ((SgMarca) catalogoBeanModel.getListaMarca().getRowData()).getNombre());
	catalogoBeanModel.setMarca((SgMarca) catalogoBeanModel.getListaMarca().getRowData());
    }

    public void givenMemoryMarcaVehiculo(ActionEvent actionEvent) {
	UtilLog4j.log.fatal(this, "givenMemoryMarcaVehiculo()");
	catalogoBeanModel.setMarca(new SgMarca());
    }

    public void removeMemoryMarcaVehiculo(ActionEvent actionEvent) {
	UtilLog4j.log.fatal(this, "removeMemoryMarcaVehiculo()");
	catalogoBeanModel.setMarca(null);
    }

    public void loadMarcasIntoComboMarcas(ValueChangeEvent valueChangeEvent) {
	catalogoBeanModel.setIdTipoEspecifico((Integer) valueChangeEvent.getNewValue());
	catalogoBeanModel.setSgTipoEspecifico(catalogoBeanModel.getTipoEspecificoById());
	if (((Integer) valueChangeEvent.getNewValue()) < 1) {
	    catalogoBeanModel.setDataModelGeneric(null);
	} else {
	    if (catalogoBeanModel.getIdMarca() > 0 && ((Integer) valueChangeEvent.getNewValue()) > 0) {
		try {
		    catalogoBeanModel.reloadModeloList();
		} catch (SIAException siae) {
		    FacesUtils.addErrorMessage(siae.getMessage());
		    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
		} catch (Exception e) {
		    UtilLog4j.log.fatal(this, e.getMessage());

		    FacesUtils.addErrorMessage(new SIAException().getMessage());
		}
	    }
	}
    }

    public List<SelectItem> getMarcasByTipoListSelectItem() {
	List<SgMarca> marcasList = null;

	try {
	    marcasList = catalogoBeanModel.getMarcasByTipo();
	    List<SelectItem> marcasListItem = new ArrayList<SelectItem>();
	    for (SgMarca marca : marcasList) {
		SelectItem item = new SelectItem(marca.getId(), marca.getNombre());
		marcasListItem.add(item);
	    }
	    return marcasListItem;
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    return null;
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());

	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    return null;
	}
    }

    public DataModel getMarcaDataModel() {
	try {
	    catalogoBeanModel.marcaList();
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e.getMessage());
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	}
        return catalogoBeanModel.getListaMarca();
    }

//    public void saveMarcaFromCatalogoMarca(ActionEvent actionEvent) {
//        saveMarca(actionEvent);
//       UtilLog4j.log.fatal(this, "PAso por el metodo");
//        try {
//           UtilLog4j.log.fatal(this, "SaveMarcaFromCatalogoMarca");
//            catalogoBeanModel.saveMarca();
//            catalogoBeanModel.reloadMarcaList();
//           UtilLog4j.log.fatal(this, "termino todo bien");
//        } catch (Exception e) {
//           UtilLog4j.log.fatal(this, "Excepcion");
//            FacesUtils.addErrorMessage(new SIAException().getMessage());
//           UtilLog4j.log.fatal(this, e.getMessage());
//            e.printStackTrace();
//        }
//    }
    public void saveMarcaFromCatalogoModelo(ActionEvent actionEvent) {
	saveMarca(actionEvent);
	ocultarPopupCrearMarca(actionEvent);
    }

    public void saveMarca(ActionEvent actionEvent) {
	try {
	    catalogoBeanModel.saveMarca();
	    FacesUtils.addInfoMessage("La Marca fué guardada exitosamente");
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());

	}
    }

    public String updateMarca() throws Exception {
	try {
	    catalogoBeanModel.updateMarca();
	    catalogoBeanModel.setMarca(null);
	    FacesUtils.addInfoMessage("La Marca fué actualizada exitosamente");
	    return "list";
	} catch (SIAException siae) {
	    catalogoBeanModel.reloadMarcaList();
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    return "list";
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    catalogoBeanModel.reloadMarcaList();
	    UtilLog4j.log.fatal(this, e.getMessage());

	    return "list";
	}
    }

    public String deleteMarca() {
	try {
	    catalogoBeanModel.deleteMarca();
	    catalogoBeanModel.reloadMarcaList();
	    FacesUtils.addInfoMessage("La Marca fué eliminada exitosamente");
	    catalogoBeanModel.setModelo(null);
	    catalogoBeanModel.iniciaControladorPopPupFalse("popupEliminarMarca");
	    //sesion.getControladorPopups().put("popupEliminarMarca", Boolean.FALSE);
	    return "list";
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	    catalogoBeanModel.iniciaControladorPopPupFalse("popupEliminarMarca");
	    //sesion.getControladorPopups().put("popupEliminarMarca", Boolean.FALSE);
	    return "list";
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	    catalogoBeanModel.iniciaControladorPopPupFalse("popupEliminarMarca");
	    //sesion.getControladorPopups().put("popupEliminarMarca", Boolean.FALSE);
	    return "list";
	}
    }

    public String mostrarPopupEliminarMarca() {
	UtilLog4j.log.fatal(this, "mostrarPopupEliminarModelo");
	catalogoBeanModel.inicializaPopUpTrue("popupEliminarMarca");
	//sesion.getControladorPopups().put("popupEliminarMarca", Boolean.TRUE);
	return "";
    }

    public String ocultarPopupEliminarMarca() {
	UtilLog4j.log.fatal(this, "ocultarPopupEliminarModelo");
	catalogoBeanModel.iniciaControladorPopPupFalse("popupEliminarMarca");
	//sesion.getControladorPopups().put("popupEliminarMarca", Boolean.FALSE);
	return "";
    }

    public void mostrarPopupCrearMarca(ActionEvent actionEvent) {
	UtilLog4j.log.fatal(this, "mostrarPopupCrearMarca");
	catalogoBeanModel.setMarca(new SgMarca());
	catalogoBeanModel.inicializaPopUpTrue("popupCrearMarca");
//        sesion.getControladorPopups().put("popupCrearMarca", Boolean.TRUE);
    }

    public void ocultarPopupCrearMarca(ActionEvent actionEvent) {
	UtilLog4j.log.fatal(this, "ocultarPopupCrearMarca");
	catalogoBeanModel.setMarca(null);
	clearComponent("popupCrearMarcaCatalogoModelo", "nombreMarca");
	catalogoBeanModel.inicializaPopUpTrue("popupCrearMarca");
	//sesion.getControladorPopups().put("popupCrearMarca", Boolean.FALSE);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Marca - FIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Motivo - FIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void agregarMotivo(ActionEvent event) {
	catalogoBeanModel.setSgMotivo(new SgMotivo());
	catalogoBeanModel.setPopUp(true);
    }

    public DataModel getTraerMotivo() {
	try {
	    return catalogoBeanModel.traerMotivo();
	} catch (Exception e) {
	    return null;
	}
    }

    public void seleccionarMotivo(ActionEvent event) {
	catalogoBeanModel.setSgMotivo((SgMotivo) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.setCadena(catalogoBeanModel.getSgMotivo().getNombre());
	catalogoBeanModel.setModificarPopUp(true);
    }

    public void eliminarMotivo(ActionEvent event) {
	catalogoBeanModel.setSgMotivo((SgMotivo) catalogoBeanModel.getLista().getRowData());

	if (catalogoBeanModel.buscarMotivoUsado()) {
	    FacesUtils.addInfoMessage("No es posible eliminar el motivo, se ha utilizado");
	} else {
	    catalogoBeanModel.eliminarMotivo();
	    catalogoBeanModel.setSgMotivo(null);
	}
    }

    public void guardarMotivo(ActionEvent event) {
	if (catalogoBeanModel.getSgMotivo().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Motivo es requerido");
	} else if (catalogoBeanModel.buscarMotivoPorNombre() != null) {
	    FacesUtils.addInfoMessage("El motivo ya existe, por favor intente con otro nombre");
	} else {
	    catalogoBeanModel.completarMotivo();
	    catalogoBeanModel.setSgMotivo(null);
	    catalogoBeanModel.setPopUp(false);
	}
    }

    public void completarModificacionMotivo(ActionEvent event) {
	if (catalogoBeanModel.getSgMotivo().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Motivo es requerido");
	} else if (catalogoBeanModel.buscarMotivoPorNombre() != null) {
	    FacesUtils.addInfoMessage("El motivo ya existe, por favor intente con otro nombre");
	} else {
	    catalogoBeanModel.modificarMotivo();
	    catalogoBeanModel.setCadena("");
	    catalogoBeanModel.setSgMotivo(null);
	    catalogoBeanModel.setModificarPopUp(false);
	}
    }
//OPERACION

    public void agregarOperacion(ActionEvent event) {
	catalogoBeanModel.setPopUp(true);
	catalogoBeanModel.setSiOperacion(new SiOperacion());
    }

    public DataModel getTraerOperacion() {
	try {
	    return catalogoBeanModel.traerOperacion();
	} catch (Exception e) {
	    return null;
	}
    }

    public void guardarOperacion(ActionEvent event) {
	if (catalogoBeanModel.getSiOperacion().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Es necesario agregar el nombre de la operación");
	} else if (catalogoBeanModel.buscarOperacionPorNombre() != null) {
	    FacesUtils.addInfoMessage("Ya existe la operación, intente con otro nombre");
	} else {
	    catalogoBeanModel.guardarOperacion();
	    catalogoBeanModel.setPopUp(false);
	    catalogoBeanModel.setSiOperacion(null);
	}
    }

    public void cerrarPopAgregarOperacion(ActionEvent event) {
	catalogoBeanModel.setPopUp(false);
	catalogoBeanModel.setSiOperacion(null);
    }

    public void seleccionarOperacion(ActionEvent event) {
	catalogoBeanModel.setSiOperacion((SiOperacion) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.setModificarPopUp(true);
    }

    public void eliminarOperacion(ActionEvent event) {
	catalogoBeanModel.setSiOperacion((SiOperacion) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.eliminarOperacion();
    }

    public void completarModificacionOperacion(ActionEvent event) {
	if (catalogoBeanModel.getSiOperacion().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Es necesario agregar el nombre de la operación");
	} else if (catalogoBeanModel.buscarOperacionPorNombre() != null) {
	    FacesUtils.addInfoMessage("Ya existe la operación, intente con otro nombre");
	} else {
	    catalogoBeanModel.completarModificarOperacion();
	    catalogoBeanModel.setModificarPopUp(false);
	    catalogoBeanModel.setSiOperacion(null);

	}
    }

    public void cerrarPopModificarOperacion(ActionEvent event) {
	catalogoBeanModel.setModificarPopUp(false);
	catalogoBeanModel.setSiOperacion(null);
    }

    /**
     * ********************************
     */
    //Condicion
    /**
     * *********************************
     */
    public void agregarCondicion(ActionEvent event) {
	catalogoBeanModel.setPopUp(true);
	catalogoBeanModel.setSiCondicion(new SiCondicion());
    }

    public DataModel getTraerCondicion() {
	try {
	    return catalogoBeanModel.traerCondicion();
	} catch (Exception e) {
	    return null;
	}
    }

    public void guardarCondicion(ActionEvent event) {
	if (catalogoBeanModel.getSiCondicion().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Es necesario agregar el nombre ");
	} else if (catalogoBeanModel.buscarCondicionPorNombre() != null) {
	    FacesUtils.addInfoMessage("Ya existe la condición, intente con otro nombre");
	} else {
	    catalogoBeanModel.guardarCondicion();
	    catalogoBeanModel.setPopUp(false);
	    catalogoBeanModel.setSiCondicion(null);
	}
    }

    public void cerrarPopCondicion(ActionEvent event) {
	catalogoBeanModel.setPopUp(false);
	catalogoBeanModel.setSiCondicion(null);
    }

    public void seleccionarCondicion(ActionEvent event) {
	catalogoBeanModel.setSiCondicion((SiCondicion) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.setModificarPopUp(true);
    }

    public void eliminarCondicion(ActionEvent event) {
	catalogoBeanModel.setSiCondicion((SiCondicion) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.eliminarCondicion();
    }

    public void completarModificacionCondicion(ActionEvent event) {
	if (catalogoBeanModel.getSiCondicion().getNombre().isEmpty()) {
	    FacesUtils.addInfoMessage("Es necesario agregar el nombre ");
	} else if (catalogoBeanModel.buscarCondicionPorNombre() != null) {
	    FacesUtils.addInfoMessage("Ya existe la operación, intente con otro nombre");
	} else {
	    catalogoBeanModel.completarModificarCondicion();
	    catalogoBeanModel.setModificarPopUp(false);
	    catalogoBeanModel.setSiCondicion(null);

	}
    }

    public void cerrarPopModificarCondicion(ActionEvent event) {
	catalogoBeanModel.setModificarPopUp(false);
	catalogoBeanModel.setSiCondicion(null);
    }

    public DataModel getAllAerolineas() {
	try {
	    this.catalogoBeanModel.getAllAerolineas();
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage(siae.getMessage());
	    UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	} finally {
	    return this.catalogoBeanModel.getDataModelGeneric();
	}
    }

    public void createAerolinea(ActionEvent actionEvent) {
	try {
	    this.catalogoBeanModel.saveAerolinea();
	    FacesUtils.addInfoMessage("Aerolínea " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
	    closePopupCreateAerolinea(actionEvent);
	} catch (ExistingItemException eie) {
	    FacesUtils.addErrorMessage("formPopupCreateAerolinea:msgsPopupCreateAerolinea", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SgAerolinea) eie.getElemento()).getNombre());
	    UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	}
    }

    public void updateAerolinea(ActionEvent actionEvent) {
	try {
	    this.catalogoBeanModel.updateAerolinea();
	    FacesUtils.addInfoMessage("Aerolínea " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
	    closePopupUpdateAerolinea(actionEvent);
	} catch (ExistingItemException eie) {
	    FacesUtils.addErrorMessage("formPopupUpdateAerolinea:msgsPopupUpdateAerolinea", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + ((SgAerolinea) eie.getElemento()).getNombre());
	    UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
	    this.catalogoBeanModel.reloadAllAerolineas();
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	}
    }

    public void deleteAerolinea(ActionEvent actionEvent) {
	try {
	    this.catalogoBeanModel.deleteAerolinea();
	    FacesUtils.addInfoMessage("Aerolínea " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
	    closePopupDeleteAerolinea(actionEvent);
	} catch (ItemUsedBySystemException iue) {
	    FacesUtils.addErrorMessage("formPopupDeleteAerolinea:msgsPopupDeleteAerolinea", FacesUtils.getKeyResourceBundle(iue.getLiteral()) + ": " + ((SgAerolinea) iue.getElemento()).getNombre());
	    UtilLog4j.log.fatal(this, iue.getMensajeParaProgramador());
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    e.printStackTrace();
	}
    }

    public void openPopupCreateAerolinea(ActionEvent actionEvent) {
	this.catalogoBeanModel.setAerolinea(new SgAerolinea());
	catalogoBeanModel.inicializaPopUpTrue("popupCreateAerolinea");
	//sesion.getControladorPopups().put("popupCreateAerolinea", Boolean.TRUE);
    }

    public void closePopupCreateAerolinea(ActionEvent actionEvent) {
	this.catalogoBeanModel.setAerolinea(null);
	clearComponent("formPopupCreateAerolinea", "inpTxtnombreAerolinea");
	catalogoBeanModel.iniciaControladorPopPupFalse("popupCreateAerolinea");
	//sesion.getControladorPopups().put("popupCreateAerolinea", Boolean.FALSE);
    }

    public void openPopupUpdateAerolinea(ActionEvent actionEvent) {
	this.catalogoBeanModel.setAerolinea((SgAerolinea) this.catalogoBeanModel.getDataModelGeneric().getRowData());
	catalogoBeanModel.inicializaPopUpTrue("popupUpdateAerolinea");
	//sesion.getControladorPopups().put("popupUpdateAerolinea", Boolean.TRUE);
    }

    public void closePopupUpdateAerolinea(ActionEvent actionEvent) {
	this.catalogoBeanModel.setAerolinea(null);
	clearComponent("formPopupUpdateAerolinea", "inpTxtnombreAerolinea");
	catalogoBeanModel.iniciaControladorPopPupFalse("popupUpdateAerolinea");
//        sesion.getControladorPopups().put("popupUpdateAerolinea", Boolean.FALSE);
    }

    public void openPopupDeleteAerolinea(ActionEvent actionEvent) {
	this.catalogoBeanModel.setAerolinea((SgAerolinea) this.catalogoBeanModel.getDataModelGeneric().getRowData());
	catalogoBeanModel.inicializaPopUpTrue("popupDeleteAerolinea");
	//sesion.getControladorPopups().put("popupDeleteAerolinea", Boolean.TRUE);
    }

    public void closePopupDeleteAerolinea(ActionEvent actionEvent) {
	this.catalogoBeanModel.setAerolinea(null);
	catalogoBeanModel.iniciaControladorPopPupFalse("popupDeleteAerolinea");
	//sesion.getControladorPopups().put("popupDeleteAerolinea", Boolean.FALSE);
    }

    /////***********************************************INICIO PAIS /////
//    public void agregarPais(ActionEvent event) {
//        catalogoBeanModel.setSiPais(new SiPais());
//        catalogoBeanModel.setPopUp(true);
//    }
//
//    public DataModel getTraerPais() {
//        try {
//            return catalogoBeanModel.traerPais();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    public void seleccionarPais(ActionEvent event) {
//        catalogoBeanModel.setSiPais((SiPais) catalogoBeanModel.getLista().getRowData());
//        catalogoBeanModel.setCadena(catalogoBeanModel.getSiPais().getNombre());
//        catalogoBeanModel.setModificarPopUp(true);
//    }
//
//    public void eliminarPais(ActionEvent event) {
//        catalogoBeanModel.setSiPais((SiPais) catalogoBeanModel.getLista().getRowData());
//       UtilLog4j.log.fatal(this, "Pais " + catalogoBeanModel.getSiPais().getNombre());
//        if (catalogoBeanModel.buscarPaisUsado()) {
//            FacesUtils.addInfoMessage("cataPais:eliminar", FacesUtils.getKeyResourceBundle("sistema.pais.usado"));
//        } else {
//            catalogoBeanModel.eliminarPais();
//            catalogoBeanModel.setSiPais(null);
//        }
//    }
//
//    public void guardarPais(ActionEvent event) {
//        if (catalogoBeanModel.getSiPais().getNombre().isEmpty()) {
//            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.pais.requerido"));
//        } else if (catalogoBeanModel.buscarPaisPorNombre() != null) {
//            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.pais.existe"));
//        } else {
//            catalogoBeanModel.completarPais();
//            catalogoBeanModel.setSiPais(null);
//            catalogoBeanModel.setPopUp(false);
//        }
//    }
//
//    public void completarModificacionPais(ActionEvent event) {
//        if (catalogoBeanModel.getSiPais().getNombre().isEmpty()) {
//            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.pais.requerido"));
//        } else if (catalogoBeanModel.buscarPaisPorNombre() != null) {
//            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.pais.existe"));
//        } else {
//            catalogoBeanModel.modificarPais();
//            catalogoBeanModel.setCadena("");
//            catalogoBeanModel.setSiPais(null);
//            catalogoBeanModel.setModificarPopUp(false);
//        }
//    }
    ///////*********************************************FIN PAIS **********************************
    /**
     * ******************************* START CATALOGO EMPRESA *
     * ***************************************
     */
    public void traerEmpresaSelectItem() {
	catalogoBeanModel.traerEmpresaItems();
    }

    public List<SelectItem> getEmpresaSelectItem() {
	return catalogoBeanModel.getListItem();
    }

    public void abrirPopupAgregarEmpresa(ActionEvent event) {
	catalogoBeanModel.setSgEmpresa(new SgEmpresa());
	catalogoBeanModel.setPopUp(true);
    }

    public void abrirPanelAgregarEmpresa(ActionEvent event) {
	catalogoBeanModel.setSgEmpresa(new SgEmpresa());
	catalogoBeanModel.setMostarPanel(true); //sirve para mostarr un panel
    }

    public void cerrarPanelAgregarEmpresa(ActionEvent event) {
	catalogoBeanModel.setSgEmpresa(null);
	catalogoBeanModel.setMostarPanel(false); //sirve para mostarr un panel
    }

    public DataModel getTraerEmpresas() {
	try {
	    return catalogoBeanModel.traerEmpresas();
	} catch (Exception e) {
	    return null;
	}
    }

    public void seleccionarEmpresa(ActionEvent event) {
	catalogoBeanModel.setSgEmpresa((SgEmpresa) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.setModificarPopUp(true);
    }

    public void eliminarEmpresa(ActionEvent event) {
	catalogoBeanModel.setSgEmpresa((SgEmpresa) catalogoBeanModel.getLista().getRowData());
	if (catalogoBeanModel.buscarEmpresaOcupada()) {
	    //sgl.empresa.mensaje.no.eliminar
//            FacesUtils.addInfoMessage("No es posible eliminar el registro, esta siendo utilizado en otro proceso");
	    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.eliminar.registroUsado"));
	} else {
	    catalogoBeanModel.eliminarEmpresa();
	    catalogoBeanModel.setSgEmpresa(null);
	}
    }

    public void guardarEmpresa(ActionEvent event) {
	if (catalogoBeanModel.getSgEmpresa().getNombre().isEmpty()) {
	    FacesUtils.addErrorMessage("formGuardarInvitado:msgsPopupCreateSgInvitado", FacesUtils.getKeyResourceBundle("sgl.generales.nombre") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
	} else if (catalogoBeanModel.buscarEmpresaPorNombre() != null) {
	    FacesUtils.addErrorMessage("formGuardarInvitado:msgsPopupCreateSgInvitado", FacesUtils.getKeyResourceBundle("sistema.mensaje.error.elementoExistente") + " " + this.catalogoBeanModel.getSgEmpresa().getNombre());
	} else {
	    catalogoBeanModel.guardarEmpresa();
	    catalogoBeanModel.traerEmpresaItems();
	    catalogoBeanModel.setSgEmpresa(null);
	    catalogoBeanModel.setPopUp(false);
	    catalogoBeanModel.setMostarPanel(false); //la cierra siempre desde un lugar afuera del pop
	}
    }

    public void completarModificacionEmpresa(ActionEvent event) {
	if (catalogoBeanModel.getSgEmpresa().getNombre().isEmpty()) {
	    //FacesUtils.addInfoMessage("Por favor especifique el nombre del empresa");
	    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.empresa.mensaje.falta"));
	} else if (catalogoBeanModel.buscarEmpresaPorNombre() != null) {
	    //
	    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.empresa.mensaje.ya.existe"));
	    //FacesUtils.addInfoMessage("Ya existe un nombre con esa especificación, por favor intente con otro nombre");
	} else {
	    catalogoBeanModel.modificarEmpresa();
	    catalogoBeanModel.setSgEmpresa(null);
	    catalogoBeanModel.setModificarPopUp(false);
	}
    }

    public void cerrarPopEmpresaModificar(ActionEvent event) {
	catalogoBeanModel.setSgEmpresa(null);
	catalogoBeanModel.setModificarPopUp(false);
    }

    public SgEmpresa getSgEmpresa() {
	return catalogoBeanModel.getSgEmpresa();
    }

    public void setSgEmpresa(SgEmpresa sgEmpresa) {
	catalogoBeanModel.setSgEmpresa(sgEmpresa);
    }

    // ****************** START CATALOGO INVITADO ******************
    public void traerInvitadoSelectItem() {
	catalogoBeanModel.traerEmpresaItems();
    }

    public List<SelectItem> getInvitadoSelectItem() {
	return catalogoBeanModel.getListItem();
    }

    public DataModel getTraerInvitados() {
	try {
	    return catalogoBeanModel.traerInvitados();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "Excepcion all traer invitados " + e.getMessage());
	}
	return null;
    }

    public void seleccionarInvitado(ActionEvent event) {
	catalogoBeanModel.setInvitadoVo((InvitadoVO) catalogoBeanModel.getLista().getRowData());
	//con estas variables controlo si se realizan las modificaciones
	catalogoBeanModel.setCadena(catalogoBeanModel.getInvitadoVo().getNombre());
	catalogoBeanModel.setIdTipo(catalogoBeanModel.getInvitadoVo().getIdEmpresa());
	//--------------------
	catalogoBeanModel.traerEmpresaItems();
	catalogoBeanModel.setIdEmpresa(catalogoBeanModel.getInvitadoVo().getIdEmpresa());
	catalogoBeanModel.setMostarPanel(false);
	catalogoBeanModel.setModificarPopUp(true);
    }

    public void eliminarInvitado(ActionEvent event) {
	catalogoBeanModel.setInvitadoVo((InvitadoVO) catalogoBeanModel.getLista().getRowData());
	if (catalogoBeanModel.buscarInvitadoOcupado()) {
	    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.eliminar.registroUsado"));
	} else {
	    catalogoBeanModel.eliminarInvitado();
	    catalogoBeanModel.traerInvitados();
	    catalogoBeanModel.setInvitadoVo(null);
	}
    }

    public void guardarInvitado(ActionEvent event) {
	if (!catalogoBeanModel.getInvitadoVo().getNombre().isEmpty()) {
	    if (catalogoBeanModel.getIdEmpresa() != -1) {
		if (validaMail(catalogoBeanModel.getInvitadoVo().getEmail())) {
		    if (!catalogoBeanModel.buscarInvitado()) {
			catalogoBeanModel.guardarInvitado();
			catalogoBeanModel.traerInvitados();
			catalogoBeanModel.setInvitadoVo(null);
			catalogoBeanModel.setCrearPopUp(false);
		    } else {
			FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.elementoExistente") + " " + this.catalogoBeanModel.getInvitadoVo().getNombre());
		    }
		} else {
		    FacesUtils.addErrorMessage("formGuardarInvitado:msgsPopupCreateSgInvitado", FacesUtils.getKeyResourceBundle("sgl.invitado.mensaje.emailNoValido"));
		}
	    } else {
		FacesUtils.addErrorMessage("formGuardarInvitado:msgsPopupCreateSgInvitado", FacesUtils.getKeyResourceBundle("sgl.empresa.empresa") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
	    }
	} else {
	    FacesUtils.addErrorMessage("formGuardarInvitado:msgsPopupCreateSgInvitado", FacesUtils.getKeyResourceBundle("sgl.generales.nombre") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
	}
    }

    public void completarModificacionInvitado(ActionEvent event) {
	if (!catalogoBeanModel.getInvitadoVo().getNombre().isEmpty()) {
	    if (catalogoBeanModel.getIdEmpresa() != -1) {
		if (validaMail(catalogoBeanModel.getInvitadoVo().getEmail())) {
		    if (catalogoBeanModel.getInvitadoVo().getIdEmpresa() == catalogoBeanModel.getIdTipo() && catalogoBeanModel.getInvitadoVo().getNombre().equals(catalogoBeanModel.getCadena())) {
			UtilLog4j.log.info(this, "no sucedio nada en empresa ni en el nombre");
			catalogoBeanModel.modificarInvitado();
			catalogoBeanModel.setInvitadoVo(null);
			catalogoBeanModel.setModificarPopUp(false);
			UtilLog4j.log.info(this, "Se realizo bien la actualizacion");
		    } else {
			UtilLog4j.log.info(this, "Se realizaron cambios en empresa o en el nombre del invitado");
			if (!catalogoBeanModel.buscarInvitado()) {
			    catalogoBeanModel.modificarInvitado();
			    catalogoBeanModel.setInvitadoVo(null);
			    catalogoBeanModel.setModificarPopUp(false);
			    UtilLog4j.log.info(this, "No se encontro y se modifico");
			} else {
			    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.invitado.mensaje.ya.existe"));
			}
		    }
		} else {
		    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.invitado.mensaje.emailNoValido"));
		}
	    } else {
		FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.invitado.mensaje.seleccione.empresa"));
	    }
	} else {
	    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.invitado.mensaje.falta.nombre"));
	}
    }

    public void abrirPopupAgregarInvitado(ActionEvent event) {
	catalogoBeanModel.setInvitadoVo(new InvitadoVO());
	catalogoBeanModel.setIdEmpresa(-1);
	catalogoBeanModel.traerEmpresaItems();
	catalogoBeanModel.setCrearPopUp(true);
	catalogoBeanModel.setMostarPanel(false);
	UtilLog4j.log.info(this, "###puso en true el pop##");
    }

    public void cerrarPoppupAgregarInvitado(ActionEvent event) {
	catalogoBeanModel.setInvitadoVo(null);
	catalogoBeanModel.traerInvitados();
	catalogoBeanModel.setCrearPopUp(false);
    }

    public void cerrarPoppupModificarInvitado(ActionEvent event) {
//        catalogoBeanModel.setSgInvitado(null);
	catalogoBeanModel.traerInvitados();
	catalogoBeanModel.setModificarPopUp(false);
    }

    public InvitadoVO getInvitadoVo() {
	return catalogoBeanModel.getInvitadoVo();
    }

    public void setInvitadoVo(InvitadoVO sgInvitado) {
	catalogoBeanModel.setInvitadoVo(sgInvitado);
    }

    //Validaciones
    public boolean validaMail(String correo) {
	boolean v = true;
	if (!correo.equals("")) {
	    v = false;
	    String[] mails = correo.split(",");
	    for (String string : mails) {
		if (this.mail(string.trim())) {
		    v = true;
		} else {
		    v = false;
		    break;
		}
	    }
	}
	return v;
    }
    //metodo para validar correo electronio

    public boolean mail(String correo) {
	boolean v = false;
	Pattern pat = null;
	Matcher mat = null;
	//pat = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
	pat = Pattern.compile("^[\\w-\\.]+\\@[\\w\\.-]+\\.[a-z]{2,4}$");
	mat = pat.matcher(correo);
	if (mat.find()) {
	    v = true;
	}
	return v;
    }

    // ********************* FIN DE CATALOGO DE INVITADO ****************
  

    public void saveSgLugar(ActionEvent actionEvent) {
	try {
	    this.catalogoBeanModel.saveSgLugar();
	    FacesUtils.addInfoMessage(
		    FacesUtils.getKeyResourceBundle("sistema.articulo.el.mayuscula") + " "
		    + FacesUtils.getKeyResourceBundle("sgl.sgLugar") + " "
		    + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
	    closePopupCreateSgLugar(actionEvent);
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage("formPopupCreateSgLugar:msgsPopupCreateSgLugar", FacesUtils.getKeyResourceBundle(siae.getLiteral()));
//            siae.printStackTrace();
	} catch (Exception e) {
	    FacesUtils.addErrorMessage("formPopupCreateSgLugar:msgsPopupCreateSgLugar", FacesUtils.getKeyResourceBundle("sistema.mensaje.error.guardar"));
	    e.getMessage();
	    e.printStackTrace();
	}
    }

    public void updateSgLugar(ActionEvent actionEvent) {
	try {
	    this.catalogoBeanModel.updateSgLugar();
	    FacesUtils.addInfoMessage(
		    FacesUtils.getKeyResourceBundle("sistema.articulo.el.mayuscula") + " "
		    + FacesUtils.getKeyResourceBundle("sgl.sgLugar") + " "
		    + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
	    closePopupUpdateSgLugar(actionEvent);
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage("formPopupUpdateSgLugar:msgsPopupUpdateSgLugar", FacesUtils.getKeyResourceBundle(siae.getLiteral()));
//            siae.printStackTrace();
	} catch (Exception e) {
	    FacesUtils.addErrorMessage("formPopupUpdateSgLugar:msgsPopupUpdateSgLugar", FacesUtils.getKeyResourceBundle("sistema.mensaje.error.actualizar"));
	    e.getMessage();
	    e.printStackTrace();
	}
    }

    public void deleteSgLugar(ActionEvent actionEvent) {
	try {
	    this.catalogoBeanModel.deleteSgLugar();
	    FacesUtils.addInfoMessage(
		    FacesUtils.getKeyResourceBundle("sistema.articulo.el.mayuscula") + " "
		    + FacesUtils.getKeyResourceBundle("sgl.sgLugar") + " "
		    + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
	    closePopupDeleteSgLugar(actionEvent);
	} catch (SIAException siae) {
	    FacesUtils.addErrorMessage("formPopupDeleteSgLugar:msgsPopupDeleteSgLugar", FacesUtils.getKeyResourceBundle(siae.getLiteral()));
//            siae.printStackTrace();
	} catch (Exception e) {
	    FacesUtils.addErrorMessage("formPopupDeleteSgLugar:msgsPopupDeleteSgLugar", FacesUtils.getKeyResourceBundle("sistema.mensaje.error.eliminar"));
	    e.getMessage();
	    e.printStackTrace();
	}
    }

    public void openPopupCreateSgLugar(ActionEvent actionEvent) {
	this.catalogoBeanModel.setSgLugar(new SgLugar());
	catalogoBeanModel.inicializaPopUpTrue("popupCreateSgLugar");
	//sesion.getControladorPopups().put("popupCreateSgLugar", Boolean.TRUE);
    }

    public void closePopupCreateSgLugar(ActionEvent actionEvent) {
	this.catalogoBeanModel.setSgLugar(null);
	catalogoBeanModel.iniciaControladorPopPupFalse("popupCreateSgLugar");
	//sesion.getControladorPopups().put("popupCreateSgLugar", Boolean.FALSE);
	clearComponent("formPopupCreateSgLugar", "nombre");
    }

    public void openPopupUpdateSgLugar(ActionEvent actionEvent) {
	this.catalogoBeanModel.setSgLugar((SgLugar) this.catalogoBeanModel.getDataModelGeneric().getRowData());
	this.catalogoBeanModel.setCadena(this.catalogoBeanModel.getSgLugar().getNombre());
	catalogoBeanModel.inicializaPopUpTrue("popupUpdateSgLugar");
	//sesion.getControladorPopups().put("popupUpdateSgLugar", Boolean.TRUE);
    }

    public void closePopupUpdateSgLugar(ActionEvent actionEvent) {
	this.catalogoBeanModel.setSgLugar(null);
	this.catalogoBeanModel.setCadena(null);
	catalogoBeanModel.iniciaControladorPopPupFalse("popupUpdateSgLugar");
//        sesion.getControladorPopups().put("popupUpdateSgLugar", Boolean.FALSE);
	clearComponent("formPopupUpdateSgLugar", "nombre");
    }

    public void openPopupDeleteSgLugar(ActionEvent actionEvent) {
	this.catalogoBeanModel.setSgLugar((SgLugar) this.catalogoBeanModel.getDataModelGeneric().getRowData());
	catalogoBeanModel.inicializaPopUpTrue("popupDeleteSgLugar");
	//sesion.getControladorPopups().put("popupDeleteSgLugar", Boolean.TRUE);
    }

    public void closePopupDeleteSgLugar(ActionEvent actionEvent) {
	this.catalogoBeanModel.setSgLugar(null);
	catalogoBeanModel.iniciaControladorPopPupFalse("popupDeleteSgLugar");
//        sesion.getControladorPopups().put("popupDeleteSgLugar", Boolean.FALSE);
    }
//**   **  ** ************************ USUARIO COPIADO * * * * *  * * * * * * * * * * ////////

    public void irAgregarUsuarioCopiado(ActionEvent event) {
	//       catalogoBeanModel.beginConversationUsuarioCopia();
	catalogoBeanModel.setOficinaVO(new OficinaVO());
    }

    public void usuarioListener(String textChangeEvent) {
	    catalogoBeanModel.setListItem(catalogoBeanModel.regresaUsuarioActivo(textChangeEvent));

//	    if (autoComplete.getSelectedItem() != null) {
//		Usuario usuaroiSel = (Usuario) autoComplete.getSelectedItem().getValue();
////                this.u = usuaroiSel.getNombre();
//		catalogoBeanModel.setCadena(usuaroiSel.getNombre());
//		UtilLog4j.log.info(this, "Usr:" + catalogoBeanModel.getCadena());
//		catalogoBeanModel.setListItem(null);
//	    }
    }

    public List<SelectItem> getListaOficina() {
	if (catalogoBeanModel.getIdTipo() > 0) {
	    return catalogoBeanModel.listaOficina();
	}
	return null;
    }

    public void agregarUsuario(ActionEvent event) {
	catalogoBeanModel.setUsuarioTipoVo(new UsuarioTipoVo());
	catalogoBeanModel.setSgTipo(catalogoBeanModel.buscarTipoPorId());
	catalogoBeanModel.setNombreTipo(catalogoBeanModel.buscarOficinaVO());
	catalogoBeanModel.setCadena("");
	catalogoBeanModel.setPopUp(true);
    }

    public void cambiarTipoGeneral(ValueChangeEvent valueChangeEvent) {
	catalogoBeanModel.setIdTipo((Integer) valueChangeEvent.getNewValue());
	System.out.println("Ti " + catalogoBeanModel.getIdTipo());
	if (catalogoBeanModel.getIdTipo() == -1) {
	    catalogoBeanModel.getOficinaVO().setId(-1);
	    catalogoBeanModel.setListaUsuarioCopiado(null);
	} else {
	    catalogoBeanModel.getOficinaVO().setId(-1);
	    catalogoBeanModel.setSgTipo(catalogoBeanModel.buscarTipoPorId());
	}
    }

    public void traerUsuarioEnRelacion(ValueChangeEvent valueChangeEvent) {
	catalogoBeanModel.getOficinaVO().setId((Integer) valueChangeEvent.getNewValue());
	if (catalogoBeanModel.getOficinaVO().getId() > 0) {
	    catalogoBeanModel.traerUsuarioCopiado();
	} else {
	    catalogoBeanModel.getOficinaVO().setId(-1);
	    catalogoBeanModel.setListaUsuarioCopiado(null);
	}

    }

    public DataModel getTraerUsuarioCopiado() {
	try {
	    if (catalogoBeanModel.getOficinaVO().getId() > 0) {
		return catalogoBeanModel.getListaUsuarioCopiado();
	    }
	} catch (Exception e) {
	    return null;
	}
	return null;
    }

    public void guardarUsurioCopiado(ActionEvent event) {
	if (!catalogoBeanModel.getCadena().trim().isEmpty()) {
	    if (catalogoBeanModel.buscarUsuario()) {
		if (catalogoBeanModel.getAccion() == 0) {
		    catalogoBeanModel.guardarUsurioCopiado();
		    catalogoBeanModel.traerUsuarioCopiado();
		    catalogoBeanModel.setNombreTipo(null);
		    catalogoBeanModel.setUsuarioTipoVo(null);
		    catalogoBeanModel.setPopUp(false);
		    catalogoBeanModel.setSgTipo(null);
		    catalogoBeanModel.setCadena("");
		} else if (catalogoBeanModel.getAccion() == 1) {
		    //Modificar
		}
	    } else {
		FacesUtils.addErrorMessage("No se encontro el usuario");
	    }
	} else {
	    FacesUtils.addErrorMessage("Es necesario seleccionar un usuario");
	}

    }

    public void cancelarUsurioCopiado(ActionEvent event) {
	catalogoBeanModel.setUsuarioTipoVo(null);
	catalogoBeanModel.setSgTipo(null);
	catalogoBeanModel.setNombreTipo(null);
	catalogoBeanModel.setCadena("");
	catalogoBeanModel.setPopUp(false);
    }

    public void quitarUsuario(ActionEvent event) {
	catalogoBeanModel.setUsuarioTipoVo((UsuarioTipoVo) catalogoBeanModel.getListaUsuarioCopiado().getRowData());
	catalogoBeanModel.quitarUsuario();
	catalogoBeanModel.traerUsuarioCopiado();
	catalogoBeanModel.setUsuarioTipoVo(null);
    }
    // * * * * * * * * * * * * CATÁLOGO SG_LUGAR - END * * * * * * * * * * * *

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROL TIPO SOLI - INICIO<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void cambiarTabRolSolicitud(ValueChangeEvent event) {
	catalogoBeanModel.setLista(null);
	catalogoBeanModel.setUser("");
	catalogoBeanModel.setIdRol(-1);
	catalogoBeanModel.setIdGerencia(-1);
    }

    public List<SelectItem> getListaRol() {
	try {
	    return catalogoBeanModel.listaRol();
	} catch (Exception e) {
	    return null;
	}
    }

    public List<SelectItem> getListaGerencia() {
	try {
	    return catalogoBeanModel.listaGerecia();
	} catch (Exception e) {
	    return null;
	}
    }

    public List<SelectItem> getListaTipoSolicitud() {
	try {
	    return catalogoBeanModel.listaTipoSolicitud();
	} catch (Exception e) {
	    return null;
	}
    }

    public void agregarRolTipoSolicitud(ActionEvent event) {
	if (catalogoBeanModel.getIdTipo() > 0) {
	    if (catalogoBeanModel.validaTipoRelacion()) {
		catalogoBeanModel.agregarRolTipoSolicitud();
		catalogoBeanModel.setIdTipo(-1);
	    } else {
		FacesUtils.addInfoMessage("La solicitud ya esta en la relación");
	    }
	} else {
	    FacesUtils.addInfoMessage("Seleccione un rol");
	}
    }

    public void cambiarRol(ValueChangeEvent event) {
	catalogoBeanModel.setIdRol((Integer) event.getNewValue());
    }

    public DataModel getTraerTipoSolicitud() {
	try {
	    if (catalogoBeanModel.getIdRol() > 1) {
		return catalogoBeanModel.traerTipoSolicitud();
	    }
	} catch (Exception e) {
	    return null;
	}
	return null;
    }

    public void quitarRelacion(ActionEvent event) {
	catalogoBeanModel.setRolTipoSolicitudVo((RolTipoSolicitudVo) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.quitarRelacion();
	catalogoBeanModel.setRolTipoSolicitudVo(null);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROL TIPO SOLI - FIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROL U-G - INICIO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void usuarioListenerRol(String event) {
	catalogoBeanModel.setListItem(catalogoBeanModel.traerUsuarioActivo(event));
    }

    public void buscarUsuario(ActionEvent event) {
	if (!catalogoBeanModel.getCadena().trim().isEmpty()) {
	    catalogoBeanModel.setLista(catalogoBeanModel.traerRolGerencia());
	}
    }

    public void agregarUsuarioRolGerencia(ActionEvent event) {
	if (catalogoBeanModel.getIdRol() > 0) {
	    if (catalogoBeanModel.getIdGerencia() > 0) {
		if (catalogoBeanModel.buscarUsuario()) {
		    catalogoBeanModel.agregarUsuarioRolGerencia();
		    catalogoBeanModel.setCadena("");
		    catalogoBeanModel.setIdRol(-1);
		    catalogoBeanModel.setIdGerencia(-2);
		} else {
		    FacesUtils.addInfoMessage("No es un usuario del SIA");
		}
	    } else {
		FacesUtils.addInfoMessage("Seleccione una gerencia");
	    }
	} else {
	    FacesUtils.addInfoMessage("Seleccione un rol");
	}
    }

    public void quitarRolGerencia(ActionEvent event) {
	catalogoBeanModel.setUsuarioRolGerenciaVo((UsuarioRolGerenciaVo) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.quitarRelacionUsuarioRolGerencia();
	catalogoBeanModel.traerRolGerencia();
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ROL U-G - FIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //Semaforo
    public SemaforoVo getTraerEstadoSemaforo() {
	try {
	    return catalogoBeanModel.traerEstadoSemaforo();
	} catch (Exception e) {
	    e.getStackTrace();
	    UtilLog4j.log.fatal(this, "exc: " + e.getMessage() + "  - - - " + e.getCause());
	    return null;
	}
    }

    public void mostrarJustificacionRuta(ActionEvent event) {
	String var = FacesUtils.getRequestParameter("idEstadoSemaforo");
	catalogoBeanModel.setIdSemaforo(Integer.parseInt(var));
	catalogoBeanModel.setSemaforoVo(catalogoBeanModel.traerEstadoSemaforoActual());
    }

    public void cerrarMostrarJustifiacion(ActionEvent event) {
	catalogoBeanModel.setIdSemaforo(-1);
	catalogoBeanModel.setSemaforoVo(null);
	PrimeFaces.current().executeScript(";justificar.hide();");
    }

    public void seleccinarRuta(ActionEvent event) {
	String var = FacesUtils.getRequestParameter("idEstadoSemaforo");
	catalogoBeanModel.setIdSemaforo(Integer.parseInt(var));
	UtilLog4j.log.info(this, "Id estado semaforo: " + catalogoBeanModel.getIdSemaforo());
	catalogoBeanModel.setSemaforoVo(catalogoBeanModel.traerEstadoSemaforoActual());
    }

    public void cancelarModificarHorario(ActionEvent event) {
	catalogoBeanModel.setIdSemaforo(-1);
	catalogoBeanModel.setSemaforoVo(null);
	PrimeFaces.current().executeScript(";dialogoCambiarHorario.hide();");
    }

    public void actualizarRangoHorario(ActionEvent event) {
	catalogoBeanModel.actualizarRangoHorario();
	catalogoBeanModel.setIdSemaforo(-1);
	catalogoBeanModel.setSemaforoVo(null);
	PrimeFaces.current().executeScript(";dialogoCambiarHorario.hide();");
    }

    public DataModel getListaSemaforo() {
	try {
	    return catalogoBeanModel.traerSemaforo();
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, "E: " + e.getMessage());
	    return null;
	}
    }

    public void modificarSemaforo(ActionEvent event) {
	catalogoBeanModel.setSemaforoVo((SemaforoVo) catalogoBeanModel.getLista().getRowData());
	catalogoBeanModel.setPopUp(true);
    }

    public void cancelarCambioCiudad(ActionEvent event) {
	catalogoBeanModel.setSemaforoVo(null);
	catalogoBeanModel.setPopUp(false);
    }

    public void cambiarRango(ActionEvent event) {
	catalogoBeanModel.completarModificarHorario();
	catalogoBeanModel.setSemaforoVo(null);
	catalogoBeanModel.setPopUp(false);
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Propiedades (getters y setters) <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public Map<Integer, Boolean> getFilasSeleccionadas() {
	return catalogoBeanModel.getFilasSeleccionadas();
    }

    public void setFilasSeleccionadas(Map<Integer, Boolean> filasSeleccionadas) {
	catalogoBeanModel.getFilasSeleccionadas();
    }

    public SgTipoTipoEspecifico getSgTipoTipoEspecifico() {
	return catalogoBeanModel.getSgTipoTipoEspecifico();
    }

    public boolean isPago() {
	return catalogoBeanModel.isPago();
    }

    public void setPago(boolean pago) {
	catalogoBeanModel.setPago(pago);
    }

    public boolean isMostrarPanel() {
	return catalogoBeanModel.isMostarPanel();
    }

    public int getIdTipo() {
	return catalogoBeanModel.getIdTipo();
    }

    public void setIdTipo(int idTipo) {
	catalogoBeanModel.setIdTipo(idTipo);
    }

    public boolean isPopUp() {
	return catalogoBeanModel.isPopUp();
    }

    public boolean isCrearPopUp() {
	return catalogoBeanModel.isCrearPopUp();
    }

    public boolean isModificarPopUp() {
	return catalogoBeanModel.isModificarPopUp();
    }

    public SgTipo getSgTipo() {
	return catalogoBeanModel.getSgTipo();
    }

    public SgTipoEspecifico getSgTipoEspecifico() {
	return catalogoBeanModel.getSgTipoEspecifico();
    }

    /**
     * @return the mrPopupCrearCaracteristica
     */
    public boolean isMrPopupCrearCaracteristica() {
	return catalogoBeanModel.isMrPopupCrearCaracteristica();
    }

    /**
     * @return the mrPopupModificarCaracteristica
     */
    public boolean isMrPopupModificarCaracteristica() {
	return catalogoBeanModel.isMrPopupModificarCaracteristica();
    }

    /**
     * @return the mrPopupEliminarCaracteristica
     */
    public boolean isMrPopupEliminarCaracteristica() {
	return catalogoBeanModel.isMrPopupEliminarCaracteristica();
    }

    /**
     * @return the mrPopupCrearTipo
     */
    public boolean isMrPopupCrearTipo() {
	return catalogoBeanModel.isMrPopupCrearTipo();
    }

    /**
     * @return the nombreTipo
     */
    public String getNombreTipo() {
	return catalogoBeanModel.getNombreTipo();
    }

    /**
     * @param nombreTipo the nombreTipo to set
     */
    public void setNombreTipo(String nombreTipo) {
	catalogoBeanModel.setNombreTipo(nombreTipo);
    }

    /**
     * @return the caracteristica
     */
    public SgCaracteristica getCaracteristica() {
	return catalogoBeanModel.getCaracteristica();
    }

    /**
     * @param caracteristica the caracteristica to set
     */
    public void setCaracteristica(SgCaracteristica caracteristica) {
	catalogoBeanModel.setCaracteristica(caracteristica);
    }

    /**
     * @return the descripcionTipo
     */
    public String getDescripcionTipo() {
	return catalogoBeanModel.getDescripcionTipo();
    }

    /**
     * @param descripcionTipo the descripcionTipo to set
     */
    public void setDescripcionTipo(String descripcionTipo) {
	catalogoBeanModel.setDescripcionTipo(descripcionTipo);
    }

    /**
     * @return the nombreCaracteristica
     */
    public String getNombreCaracteristica() {
	return catalogoBeanModel.getNombreCaracteristica();
    }

    /**
     * @param nombreCaracteristica the nombreCaracteristica to set
     */
    public void setNombreCaracteristica(String nombreCaracteristica) {
	catalogoBeanModel.setNombreCaracteristica(nombreCaracteristica);
    }

    /**
     * @return the modelo
     */
    public SgModelo getModelo() {
	return catalogoBeanModel.getModelo();
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(SgModelo modelo) {
	catalogoBeanModel.setModelo(modelo);
    }

    /**
     * @return the marca
     */
    public SgMarca getMarca() {
	return catalogoBeanModel.getMarca();
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(SgMarca marca) {
	catalogoBeanModel.setMarca(marca);
    }

    /**
     * @return the sgMotivo
     */
    public SgMotivo getSgMotivo() {
	return catalogoBeanModel.getSgMotivo();
    }

    /**
     * @param sgMotivo the sgMotivo to set
     */
    public void setSgMotivo(SgMotivo sgMotivo) {
	catalogoBeanModel.setSgMotivo(sgMotivo);
    }

    /**
     * @return the cadena
     */
    public String getCadena() {
	return catalogoBeanModel.getCadena();
    }

    /**
     * @param cadena the cadena to set
     */
    public void setCadena(String cadena) {
	catalogoBeanModel.setCadena(cadena);
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
	return catalogoBeanModel.getIdTipoEspecifico();
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
	catalogoBeanModel.setIdTipoEspecifico(idTipoEspecifico);
    }

    /**
     * @return the idMarca
     */
    public int getIdMarca() {
	return catalogoBeanModel.getIdMarca();
    }

    /**
     * @param idMarca the idMarca to set
     */
    public void setIdMarca(int idMarca) {
	catalogoBeanModel.setIdMarca(idMarca);
    }

    /**
     * @return the siOperacion
     */
    public SiOperacion getSiOperacion() {
	return catalogoBeanModel.getSiOperacion();
    }

    /**
     * @param siOperacion the siOperacion to set
     */
    public void setSiOperacion(SiOperacion siOperacion) {
	catalogoBeanModel.setSiOperacion(siOperacion);
    }

    /**
     * @return the siCondicion
     */
    public SiCondicion getSiCondicion() {
	return catalogoBeanModel.getSiCondicion();
    }

    /**
     * @param siCondicion the siCondicion to set
     */
    public void setSiCondicion(SiCondicion siCondicion) {
	catalogoBeanModel.setSiCondicion(siCondicion);
    }

    /**
     * @return the aerolinea
     */
    public SgAerolinea getAerolinea() {
	return this.catalogoBeanModel.getAerolinea();
    }

    /**
     * @param aerolinea the aerolinea to set
     */
    public void setAerolinea(SgAerolinea aerolinea) {
	this.catalogoBeanModel.setAerolinea(aerolinea);
    }

    /**
     * @return the siPais
     */
    public SiPais getSiPais() {
	return catalogoBeanModel.getSiPais();
    }

    /**
     * @param siPais the siPais to set
     */
    public void setSiPais(SiPais siPais) {
	catalogoBeanModel.setSiPais(siPais);
    }

    /**
     * @return the idRuta
     */
    public int getIdRuta() {
	return catalogoBeanModel.getIdRuta();
    }

    /**
     * @param idRuta the idRuta to set
     */
    public void setIdRuta(int idRuta) {
	catalogoBeanModel.setIdRuta(idRuta);
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
	return catalogoBeanModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
	catalogoBeanModel.setLista(lista);
    }

    /**
     * @return the opcionDestino
     */
    public String getOpcionDestino() {
	return catalogoBeanModel.getOpcionDestino();
    }

    /**
     * @param opcionDestino the opcionDestino to set
     */
    public void setOpcionDestino(String opcionDestino) {
	catalogoBeanModel.setOpcionDestino(opcionDestino);
    }

    /**
     * @return the sgOficina
     */
    public SgOficina getSgOficina() {
	return catalogoBeanModel.getSgOficina();
    }

    /**
     * @param sgOficina the sgOficina to set
     */
    public void setSgOficina(SgOficina sgOficina) {
	catalogoBeanModel.setSgOficina(sgOficina);
    }

    /**
     * Estos metodos se llaman get y set empresa pero usan la variable idMarca
     * por cuestiones de no declarar una variable mas en el modelBean
     *
     */
    public int getIdEmpresa() {
	return catalogoBeanModel.getIdEmpresa();
    }

    /**
     */
    public void setIdEmpresa(int idEmpresa) {
	catalogoBeanModel.setIdEmpresa(idEmpresa);
    }

    /**
     * @return the listItem
     */
    public List<SelectItem> getListItem() {
	return catalogoBeanModel.getListItem();
    }

    /**
     * @param listItem the listItem to set
     */
    public void setListItem(List<SelectItem> empresaListItem) {
	this.catalogoBeanModel.setListItem(empresaListItem);
    }

    /**
     * @return the sgLugar
     */
    public SgLugar getSgLugar() {
	return this.catalogoBeanModel.getSgLugar();
    }

    /**
     * @param sgLugar the sgLugar to set
     */
    public void setSgLugar(SgLugar sgLugar) {
	this.catalogoBeanModel.setSgLugar(sgLugar);
    }

    /**
     * @return the detallePop
     */
    public boolean isDetallePop() {
	return catalogoBeanModel.isDetallePop();
    }

    /**
     * @param detallePop the detallePop to set
     */
    public void setDetallePop(boolean detallePop) {
	catalogoBeanModel.setDetallePop(detallePop);
    }

    /**
     * @return the usuarioCopiadoVo
     */
    public UsuarioTipoVo getUsuarioTipoVo() {
	return catalogoBeanModel.getUsuarioTipoVo();
    }

    /**
     * @param usuarioTipoVo
     */
    public void setUsuarioTipoVo(UsuarioTipoVo usuarioTipoVo) {
	catalogoBeanModel.setUsuarioTipoVo(usuarioTipoVo);
    }

    /**
     * @return the accion
     */
    public int getAccion() {
	return catalogoBeanModel.getAccion();
    }

    /**
     * @param accion the accion to set
     */
    public void setAccion(int accion) {
	catalogoBeanModel.setAccion(accion);
    }

    /**
     * @return the oficinaVO
     */
    public OficinaVO getOficinaVO() {
	return catalogoBeanModel.getOficinaVO();
    }

    /**
     * @param oficinaVO the oficinaVO to set
     */
    public void setOficinaVO(OficinaVO oficinaVO) {
	catalogoBeanModel.setOficinaVO(oficinaVO);
    }

    /**
     * @return the rutaTerrestreVo
     */
    public RutaTerrestreVo getRutaTerrestreVo() {
	return catalogoBeanModel.getRutaTerrestreVo();
    }

    /**
     * @param rutaTerrestreVo the rutaTerrestreVo to set
     */
    public void setRutaTerrestreVo(RutaTerrestreVo rutaTerrestreVo) {
	catalogoBeanModel.setRutaTerrestreVo(rutaTerrestreVo);
    }

    /**
     * @param catalogoBeanModel the catalogoBeanModel to set
     */
    public void setCatalogoBeanModel(CatalogoBeanModel catalogoBeanModel) {
	this.catalogoBeanModel = catalogoBeanModel;
    }

    /**
     * @return the sgDetalleRutaTerrestreVo
     */
    public SgDetalleRutaTerrestreVo getSgDetalleRutaTerrestreVo() {
	return catalogoBeanModel.getSgDetalleRutaTerrestreVo();
    }

    /**
     * @param sgDetalleRutaTerrestreVo the sgDetalleRutaTerrestreVo to set
     */
    public void setSgDetalleRutaTerrestreVo(SgDetalleRutaTerrestreVo sgDetalleRutaTerrestreVo) {
	catalogoBeanModel.setSgDetalleRutaTerrestreVo(sgDetalleRutaTerrestreVo);
    }

    /**
     * @return the idCiudad
     */
    public int getIdCiudad() {
	return catalogoBeanModel.getIdCiudad();
    }

    /**
     * @param idCiudad the idCiudad to set
     */
    public void setIdCiudad(int idCiudad) {
	catalogoBeanModel.setIdCiudad(idCiudad);
    }

    /**
     * @return the idRol
     */
    public int getIdRol() {
	return catalogoBeanModel.getIdRol();
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
	catalogoBeanModel.setIdRol(idRol);
    }

    /**
     * @return the rolTipoSolicitudVo
     */
    public RolTipoSolicitudVo getRolTipoSolicitudVo() {
	return catalogoBeanModel.getRolTipoSolicitudVo();
    }

    /**
     * @param rolTipoSolicitudVo the rolTipoSolicitudVo to set
     */
    public void setRolTipoSolicitudVo(RolTipoSolicitudVo rolTipoSolicitudVo) {
	catalogoBeanModel.setRolTipoSolicitudVo(rolTipoSolicitudVo);
    }

    /**
     * @return the usuarioRolGerenciaVo
     */
    public UsuarioRolGerenciaVo getUsuarioRolGerenciaVo() {
	return catalogoBeanModel.getUsuarioRolGerenciaVo();
    }

    /**
     * @param usuarioRolGerenciaVo the usuarioRolGerenciaVo to set
     */
    public void setUsuarioRolGerenciaVo(UsuarioRolGerenciaVo usuarioRolGerenciaVo) {
	catalogoBeanModel.setUsuarioRolGerenciaVo(usuarioRolGerenciaVo);
    }

    /**
     * @return the user
     */
    public String getUser() {
	return catalogoBeanModel.getUser();
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
	catalogoBeanModel.setUser(user);
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
	return catalogoBeanModel.getIdGerencia();
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
	catalogoBeanModel.setIdGerencia(idGerencia);
    }

    /**
     * @return the idLugar
     */
    public int getIdLugar() {
	return catalogoBeanModel.getIdLugar();
    }

    /**
     * @param idLugar the idLugar to set
     */
    public void setIdLugar(int idLugar) {
	catalogoBeanModel.setIdLugar(idLugar);
    }

    /**
     * @return the semaforoVo
     */
    public SemaforoVo getSemaforoVo() {
	return catalogoBeanModel.getSemaforoVo();
    }

    /**
     * @param semaforoVo the semaforoVo to set
     */
    public void setSemaforoVo(SemaforoVo semaforoVo) {
	catalogoBeanModel.setSemaforoVo(semaforoVo);
    }

    /**
     * @return the idSemaforo
     */
    public int getIdSemaforo() {
	return catalogoBeanModel.getIdSemaforo();
    }

    /**
     * @param idSemaforo the idSemaforo to set
     */
    public void setIdSemaforo(int idSemaforo) {
	catalogoBeanModel.setIdSemaforo(idSemaforo);
    }

    /**
     * @return the semaforoVoActual
     */
    public SemaforoVo getSemaforoVoActual() {
	return catalogoBeanModel.getSemaforoVoActual();
    }

    /**
     * @param semaforoVoActual the semaforoVoActual to set
     */
    public void setSemaforoVoActual(SemaforoVo semaforoVoActual) {
	catalogoBeanModel.setSemaforoVoActual(semaforoVoActual);
    }

    /**
     * @return the listaUsuarioCopiado
     */
    public DataModel getListaUsuarioCopiado() {
	return catalogoBeanModel.getListaUsuarioCopiado();
    }

    /**
     * @param listaUsuarioCopiado the listaUsuarioCopiado to set
     */
    public void setListaUsuarioCopiado(DataModel listaUsuarioCopiado) {
	catalogoBeanModel.setListaUsuarioCopiado(listaUsuarioCopiado);
    }
    
    /**
     * @return the listaMarca
     */
    public DataModel getListaMarca() {
        return catalogoBeanModel.getListaMarca();
    }

    /**
     * @param listaMarca the listaMarca to set
     */
    public void setListaMarca(DataModel listaMarca) {
        catalogoBeanModel.setListaMarca(listaMarca);
    }

}
