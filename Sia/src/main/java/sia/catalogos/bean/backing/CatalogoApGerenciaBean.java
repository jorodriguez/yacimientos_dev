package sia.catalogos.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.catalogos.bean.model.CatalogoApGerenciaBeanModel;
import sia.constantes.Constantes;
import sia.excepciones.ExistingItemException;
import sia.excepciones.SIAException;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author rluna MLUIS
 */
@ManagedBean(name = "catalogoApGerenciaBean")
@ViewScoped
public class CatalogoApGerenciaBean implements Serializable {

    @ManagedProperty(value = "#{catalogoApGerenciaBeanModel}")
    private CatalogoApGerenciaBeanModel catalogoApGerenciaBeanModel;

    public CatalogoApGerenciaBean() {
    }

    /**
     * Metodo para ir a catalogoPuesto
     *
     * @param event
     */
    public void goToCatalogoApGerencia() {
	catalogoApGerenciaBeanModel.idCampoUsuario();
	this.catalogoApGerenciaBeanModel.reloadAllApGerencia();
    }

    public String goToAltaGerencia() {
	catalogoApGerenciaBeanModel.setRfcEmpresa(catalogoApGerenciaBeanModel.traerCampoPorId().getCompania().getRfc());
	this.setIdGerencia(-1);
	return "/vistas/administracion/gerencia/altaGerencia";
    }

    /**
     * Trae el datamodel con los campos de puesto ordenados por nombre
     *
     * @return
     */
    public DataModel getAllApGerenciaDataModel() {
	try {
	    this.catalogoApGerenciaBeanModel.getAllApGerencia();
	    return this.catalogoApGerenciaBeanModel.getDataModel();
	} catch (Exception e) {
	    FacesUtils.addErrorMessage(new SIAException().getMessage());
	    UtilLog4j.log.fatal(this, e.getMessage());
	    return null;
	}
    }

    public void cambiarVisible() {
	catalogoApGerenciaBeanModel.setApCampoGerenciaVo((ApCampoGerenciaVo) catalogoApGerenciaBeanModel.getDataModel().getRowData());
	catalogoApGerenciaBeanModel.completarcambiarVisible();
    }

    public void cambiarResponsable() {
	catalogoApGerenciaBeanModel.setApCampoGerenciaVo((ApCampoGerenciaVo) catalogoApGerenciaBeanModel.getDataModel().getRowData());
	traerUsuarioJson();
	//catalogoApGerenciaBeanModel.controlaPopUpTrue("popCambioResponsable");
    }

    public void traerUsuarioJson() {
	String datos = catalogoApGerenciaBeanModel.traerUsuarioJson();
	PrimeFaces.current().executeScript(";llenarJsonUsuario(" + datos + ");");
	UtilLog4j.log.info(this, "datos :: : .:: " + datos);
    }

    public void completarCambiarResponsable() {
	if (catalogoApGerenciaBeanModel.buscarUsuarioPorIdUsuario() != null) {
	    catalogoApGerenciaBeanModel.completarCambiarResponsable();
	    catalogoApGerenciaBeanModel.setApCampoGerenciaVo(null);
	    catalogoApGerenciaBeanModel.setResponsable("");
	    // catalogoApGerenciaBeanModel.controlaPopUpFalso("popCambioResponsable");
	    PrimeFaces.current().executeScript(";dialogoOK('dialogoResponsableGerencia');");
	    //limpia las cajas de texto
	    PrimeFaces.current().executeScript(";limpiarComponenteCaja();");
	} else {
	    PrimeFaces.current().executeScript(";alertaGeneral('No existe el empleado . . .');");
	    //FacesUtils.addErrorMessage("No existe el empleado");
	}

    }

    public void cerrarCambioResponsable() {
	// catalogoApGerenciaBeanModel.controlaPopUpFalso("popCambioResponsable");
	catalogoApGerenciaBeanModel.setApCampoGerenciaVo(null);
	catalogoApGerenciaBeanModel.setResponsable("");
    }

    /**
     * @return the responsable
     */
    public String getResponsable() {
	return catalogoApGerenciaBeanModel.getResponsable();
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(String responsable) {
	catalogoApGerenciaBeanModel.setResponsable(responsable);
    }

    public int getIdGerencia() {
	return catalogoApGerenciaBeanModel.getIdGerencia();
    }

    public void setIdGerencia(int idGerencia) {
	catalogoApGerenciaBeanModel.setIdGerencia(idGerencia);
    }

    public int getIdCampo() {
	return catalogoApGerenciaBeanModel.getIdCampo();
    }

    public void setIdCampo(int idCampo) {
	catalogoApGerenciaBeanModel.setIdCampo(idCampo);
    }

    public String getNombreGerencia() {
	return catalogoApGerenciaBeanModel.getNombreGerencia();
    }

    public void setNombreGerencia(String nombreGerencia) {
	catalogoApGerenciaBeanModel.setNombreGerencia(nombreGerencia);
    }

    public void idCampoSel(ValueChangeEvent valueChangeEvent) {
	catalogoApGerenciaBeanModel.setIdCampo((Integer) valueChangeEvent.getNewValue());
	catalogoApGerenciaBeanModel.reloadAllApGerencia();
    }

    public void idCampoGer(ValueChangeEvent valueChangeEvent) {
	catalogoApGerenciaBeanModel.setIdCampo((Integer) valueChangeEvent.getNewValue());
	catalogoApGerenciaBeanModel.setRfcEmpresa(catalogoApGerenciaBeanModel.traerCampoPorId().getCompania().getRfc());
	getListaGerencia();
	catalogoApGerenciaBeanModel.limpiarListaUsuario();
        // catalogoApGerenciaBeanModel.listaGerencia();
	// catalogoApGerenciaBeanModel.listaGerencia();
	//catalogoApGerenciaBeanModel.reloadGerencia();

    }

    public void guardar() throws ExistingItemException {
	if (getIdCampo() > 0) {
	    if (getIdGerencia() > 0) {
		if (!getResponsable().isEmpty()) {
		    try {
			catalogoApGerenciaBeanModel.saveCampoGerenciaResposable();
			FacesUtils.addInfoMessage("Gerencia" + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
			setIdCampo(-1);
			setIdGerencia(-1);
			setResponsable("");
		    } catch (ExistingItemException eie) {
			FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.mensaje.error.crear.relacion.gerencia"));//"La gerencia seleccionada ya tiene asignado un responsable.");
			UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
		    } catch (Exception e) {
			FacesUtils.addErrorMessage(new SIAException().getMessage());
			UtilLog4j.log.fatal(this, e.getMessage());
		    }
		} else {
		    FacesUtils.addErrorMessage("Todos los campos son requeridos");
		}
	    } else {
		FacesUtils.addErrorMessage("Todos los campos son requeridos");
	    }
	} else {
	    FacesUtils.addErrorMessage("Todos los campos son requeridos");
	}
    }

    public void guardarGerencia() {
	if (!getNombreGerencia().trim().isEmpty()) {
	    try {
		catalogoApGerenciaBeanModel.saveGerencia();
		FacesUtils.addInfoMessage("Gerencia" + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
		closePopupCreateGerencia(actionEvent);
	    } catch (ExistingItemException e) {
		FacesUtils.addErrorMessage("popupCreateRhPuesto:msgsPopupCreateRhPuesto", FacesUtils.getKeyResourceBundle(e.getLiteral()) + ": " + getNombreGerencia());
		UtilLog4j.log.fatal(this, e.getMensajeParaProgramador());
	    } catch (Exception e) {
		FacesUtils.addErrorMessage("popupCreateRhPuesto:msgsPopupCreateRhPuesto", new SIAException().getMessage());
		UtilLog4j.log.fatal(this, e.getMessage());
	    }
	} else {
	    FacesUtils.addErrorMessage("popupCreateRhPuesto:msgsPopupCreateRhPuesto", "Nombre es requerido");
	}
    }

    public List<SelectItem> getListaGerencia() {
	return catalogoApGerenciaBeanModel.listaGerencia();
    }

    public List<SelectItem> getListaTodasGerencia() {
	return catalogoApGerenciaBeanModel.listaTodasGerencia();
    }

    public List<SelectItem> getListaCampo() {
	return catalogoApGerenciaBeanModel.listaCampo();
    }

    public void usuarioListenerAsignaCampo(TextChangeEvent event) {
	catalogoApGerenciaBeanModel.setListaUsuariosAlta(catalogoApGerenciaBeanModel.traerUsuario(event.getNewValue().toString()));
    }

    /**
     * @return the listaUsuariosAlta
     */
    public List<SelectItem> getListaUsuariosAlta() {
	return catalogoApGerenciaBeanModel.getListaUsuariosAlta();
    }

    /**
     * @param listaUsuariosAlta the listaUsuariosAlta to set
     */
    public void setListaUsuariosAlta(List<SelectItem> listaUsuariosAlta) {
	catalogoApGerenciaBeanModel.setListaUsuariosAlta(listaUsuariosAlta);
    }

    public void openPopupCreateGerencia() {
	catalogoApGerenciaBeanModel.controlaPopUpTrue("popupCreateGerencia");
	//this.sesion.getControladorPopups().put("popupCreateGerencia", Boolean.TRUE);
    }

    public void agregarGerenciaCompania() {
	if (catalogoApGerenciaBeanModel.buscarGerencia()) {
	    catalogoApGerenciaBeanModel.agregarGerenciaCompania();
	    catalogoApGerenciaBeanModel.controlaPopUpFalso("popupCreateGerencia");
	} else {
	    PrimeFaces.current().executeScript(";alertaGeneral('La gerencia seleccionada ya tiene un responsable');");
	    //FacesUtils.addErrorMessage("La gerencia seleccionada ya tiene un responsable.");
	}

    }

    public void closePopupCreateGerencia() {
	catalogoApGerenciaBeanModel.controlaPopUpFalso("popupCreateGerencia");
	catalogoApGerenciaBeanModel.setIdGerencia(-1);
//        this.sesion.getControladorPopups().put("popupCreateGerencia", Boolean.FALSE);

    }

    public void openPopupCreateCampoPuesto() {
	catalogoApGerenciaBeanModel.controlaPopUpTrue("popupCampoRhPuesto");
//        this.sesion.getControladorPopups().put("popupCampoRhPuesto", Boolean.TRUE);
    }

    public void closePopupCreateCampoPuesto() {
	catalogoApGerenciaBeanModel.controlaPopUpFalso("popupCampoRhPuesto");
//        this.sesion.getControladorPopups().put("popupCampoRhPuesto", Boolean.FALSE);

    }

    /**
     * @param catalogoApGerenciaBeanModel the catalogoApGerenciaBeanModel to set
     */
    public void setCatalogoApGerenciaBeanModel(CatalogoApGerenciaBeanModel catalogoApGerenciaBeanModel) {
	this.catalogoApGerenciaBeanModel = catalogoApGerenciaBeanModel;
    }

    /**
     * @return the rfcEmpresa
     */
    public String getRfcEmpresa() {
	return catalogoApGerenciaBeanModel.getRfcEmpresa();
    }

    /**
     * @param rfcEmpresa the rfcEmpresa to set
     */
    public void setRfcEmpresa(String rfcEmpresa) {
	catalogoApGerenciaBeanModel.setRfcEmpresa(rfcEmpresa);
    }

    /**
     * @return the apCampoGerenciaVo
     */
    public ApCampoGerenciaVo getApCampoGerenciaVo() {
	return catalogoApGerenciaBeanModel.getApCampoGerenciaVo();
    }

    /**
     * @param apCampoGerenciaVo the apCampoGerenciaVo to set
     */
    public void setApCampoGerenciaVo(ApCampoGerenciaVo apCampoGerenciaVo) {
	catalogoApGerenciaBeanModel.setApCampoGerenciaVo(apCampoGerenciaVo);
    }
    
    public void eliminarGerencia() {
	catalogoApGerenciaBeanModel.setApCampoGerenciaVo((ApCampoGerenciaVo) catalogoApGerenciaBeanModel.getDataModel().getRowData());
        catalogoApGerenciaBeanModel.deleteGerencia();
    }
    
    public boolean validaRolDesarrollo(){
     return catalogoApGerenciaBeanModel.rolDesarrollo();
    }
    
    public void crearNuevaGerencia(){
        setVerNewGerencia(Constantes.TRUE);
    }
    
    public void noCrearNuevaGerencia(){
        setVerNewGerencia(Constantes.FALSE);
    }
    
    /**
     * @return the verNewGerencia
     */
    public boolean isVerNewGerencia() {
        return catalogoApGerenciaBeanModel.isVerNewGerencia();
    }

    /**
     * @param verNewGerencia the verNewGerencia to set
     */
    public void setVerNewGerencia(boolean verNewGerencia) {
        catalogoApGerenciaBeanModel.setVerNewGerencia(verNewGerencia);
    }
}
