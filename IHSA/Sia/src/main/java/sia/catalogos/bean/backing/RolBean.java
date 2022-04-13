    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;




import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.catalogos.bean.model.RolBeanModel;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author mluis
 */
@Named(value = "rolBean")
@ViewScoped
public class RolBean implements Serializable {

    /**
     * Creates a new instance of RolBean
     */
    public RolBean() {
    }
    @Inject
    private RolBeanModel rolBeanModel;

    public void limpiarRol() {
	rolBeanModel.setIdCampo(rolBeanModel.regresaIdCampo());
	rolBeanModel.setRfcCompania(rolBeanModel.regresaRfcCompania());
	setIdModulo(-1);
	setIdRol(-1);
	setNombreUsuario("");
	setLista(new ArrayList<UsuarioRolVo>());
    }

    public List<SelectItem> getListaModulo() {
	return rolBeanModel.listaModulo();
    }

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeListener) {
	rolBeanModel.setIdCampo((Integer) valueChangeListener.getNewValue());
    }

    public void cambiarSeleccionCompania(ValueChangeEvent valueChangeListener) {
	rolBeanModel.setRfcCompania((String) valueChangeListener.getNewValue());
    }

    public List<SelectItem> getListaCampoPorUsuario() {
	return rolBeanModel.listaCampoPorUsuario();
    }

    public List<SelectItem> getListaCompaniaPorUsuario() {
	return rolBeanModel.listaCompaniaPorUsuario();
    }

    public List<SelectItem> getListaRol() {
	return rolBeanModel.listaRol();
    }

    public void usuarioListener(String cadena) {
	rolBeanModel.setListaUsuario(rolBeanModel.traerUsuario(cadena));
	//listaUsuariosAlta = soporteProveedor.regresaUsuarioActivoVO(event.getNewValue().toString());
    }

    public void guardarUsuarioRol() {
	if (rolBeanModel.getIdModulo() > 0) {
	    if (rolBeanModel.getIdRol() > 0) {
		if (!rolBeanModel.getNombreUsuario().isEmpty()) {
		    if (rolBeanModel.buscarUsuarioRol()) {
			rolBeanModel.guardarUsuarioRol();
		    } else {
			FacesUtils.addErrorMessage("Ya existe el usuario con el rol seleccionado");
		    }
		} else {
		    FacesUtils.addErrorMessage("Agregue un usuario");
		}
	    } else {
		FacesUtils.addErrorMessage("Seleccione un rol");
	    }
	} else {
	    FacesUtils.addErrorMessage("Seleccione un módulo");
	}
    }

    public void buscarRol() {
	rolBeanModel.buscarRol();
    }

    public void eliminarUsuarioRol() {
	int idUrol = Integer.parseInt(FacesUtils.getRequestParameter("idUsuarioRol"));
	rolBeanModel.eliminarUsuarioRol(idUrol);
    }

    //
    /**
     * @param rolBeanModel the rolBeanModel to set
     */
    public void setRolBeanModel(RolBeanModel rolBeanModel) {
	this.rolBeanModel = rolBeanModel;
    }

    public List<UsuarioRolVo> getListaUsuarioRol() {
	return rolBeanModel.listaUsuarioRol();
    }

    /**
     * @return the idModulo
     */
    public int getIdModulo() {
	return rolBeanModel.getIdModulo();
    }

    /**
     * @param idModulo the idModulo to set
     */
    public void setIdModulo(int idModulo) {
	rolBeanModel.setIdModulo(idModulo);
    }

    /**
     * @return the idRol
     */
    public int getIdRol() {
	return rolBeanModel.getIdRol();
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
	rolBeanModel.setIdRol(idRol);
    }

    /**
     * @return the nombreUsuario
     */
    public String getNombreUsuario() {
	return rolBeanModel.getNombreUsuario();
    }

    /**
     * @param nombreUsuario the nombreUsuario to set
     */
    public void setNombreUsuario(String nombreUsuario) {
	rolBeanModel.setNombreUsuario(nombreUsuario);
    }

    /**
     * @return the idUsuario
     */
    public String getIdUsuario() {
	return rolBeanModel.getIdUsuario();
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(String idUsuario) {
	rolBeanModel.setIdUsuario(idUsuario);
    }

    /**
     * @return the listaUsuario
     */
    public List<SelectItem> getListaUsuario() {
	return rolBeanModel.getListaUsuario();
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<SelectItem> listaUsuario) {
	rolBeanModel.setListaUsuario(listaUsuario);
    }

    /**
     * @return the lista
     */
    public List getLista() {
	return rolBeanModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List lista) {
	rolBeanModel.setLista(lista);
    }

    /**
     * @return the principal
     */
    public boolean isPrincipal() {
	return rolBeanModel.isPrincipal();
    }

    /**
     * @param principal the principal to set
     */
    public void setPrincipal(boolean principal) {
	rolBeanModel.setPrincipal(principal);
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
	return rolBeanModel.getIdCampo();
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
	rolBeanModel.setIdCampo(idCampo);
    }

    /**
     * @return the rfcCompania
     */
    public String getRfcCompania() {
	return rolBeanModel.getRfcCompania();
    }

    /**
     * @param rfcCompania the rfcCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
	rolBeanModel.setRfcCompania(rfcCompania);
    }

    /**
     * @return the viewAll
     */
    public boolean isViewAll() {
	return rolBeanModel.isViewAll();
    }

    /**
     * @param principal the principal to set
     */
    public void setViewAll(boolean viewAll) {
	rolBeanModel.setViewAll(viewAll);
    }
}
