package sia.catalogos.bean.backing;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;
import sia.catalogos.bean.model.PuestosBeanModel;
import sia.excepciones.ExistingItemException;
import sia.excepciones.ItemUsedBySystemException;
import sia.excepciones.SIAException;
import sia.modelo.puesto.vo.RhPuestoVo;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author rluna
 */
@Named(value = "puestosBean")
@RequestScoped
public class PuestosBean implements Serializable {

    @Inject
    private PuestosBeanModel puestosBeanModel;

    public PuestosBean() {
    }

    /**
     * Metodo para ir a catalogoPuesto
     *
     * @return
     */
    public String goToRhPuesto() {
        puestosBeanModel.reloadAllRhPuesto();
        setCadenaBuscar(null);
        setOpcionSeleccionada("todo");
        clearComponent("formPrincipal", "buscarPuesto");
        puestosBeanModel.controlaPopUpFalso("popupCreateRhPuesto");
        puestosBeanModel.controlaPopUpFalso("popupUpdateRhPuesto");
        return "/vistas/administracion/usuario/catalogoPuesto";

    }

    /**
     * Este metodo limpia el valor del componente html
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
            // e.printStackTrace();
            UtilLog4j.log.fatal(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }
    //---------------Aqui empieza el catalogo Puestos-----------------------------

    /**
     * Trae el datamodel con los campos de puesto ordenados por nombre
     *
     * @return
     */
    public DataModel getAllRhPuestoDataModel() {
        try {
            this.puestosBeanModel.getAllRhPuesto();
            return this.puestosBeanModel.getDataModel();
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
            return null;
        }
    }

    public void traerPorFiltro(ValueChangeEvent changeEvent) {
        puestosBeanModel.setOpcionSeleccionada(changeEvent.getNewValue().toString());
        if (puestosBeanModel.getOpcionSeleccionada().equals("todo")) {
            puestosBeanModel.reloadAllRhPuesto();
        }
        if (puestosBeanModel.getOpcionSeleccionada().equals("filtro")) {
            puestosBeanModel.buscarRhPuesto();
        }
    }

    public void saveRhPuesto() {

        if (!getNombrePuesto().trim().isEmpty()) {
            try {
                this.puestosBeanModel.saveRhPuesto();
                FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("rhPuesto") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
                closePopupCreateRhPuesto();
            } catch (ExistingItemException eie) {
                FacesUtils.addErrorMessage("popupCreateRhPuesto:msgsPopupCreateRhPuesto", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + getNombrePuesto());
                UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
            } catch (Exception e) {
                FacesUtils.addErrorMessage("popupCreateRhPuesto:msgsPopupCreateRhPuesto", new SIAException().getMessage());
                UtilLog4j.log.fatal(this, e.getMessage());
            }
        } else {
            FacesUtils.addErrorMessage("popupCreateRhPuesto:msgsPopupCreateRhPuesto", "Nombre es requerido");
        }

    }

    public void updateRhPuesto() {
        UtilLog4j.log.info(this, "PuestosBean.update()");
        try {
            this.puestosBeanModel.updateRhPuesto();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("rhPuesto") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.modificacionSatisfactoria"));
            closePopupUpdateRhPuesto();
        } catch (ExistingItemException eie) {
            FacesUtils.addErrorMessage("popupUpdateRhPuesto:msgsPopupUpdateRhPuesto", FacesUtils.getKeyResourceBundle(eie.getLiteral()) + ": " + getNombrePuesto());
            UtilLog4j.log.fatal(this, eie.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage("formPopupUpdateRhPuesto:msgsPopupUpdateRhPuesto", new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    public void deleteRhPuesto() {
        try {
            this.puestosBeanModel.setRhPuestoVo((RhPuestoVo) this.puestosBeanModel.getDataModel().getRowData());
            this.puestosBeanModel.deleteRhPuesto();
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("rhPuesto") + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.eliminacionSatisfactoria"));
        } catch (ItemUsedBySystemException iue) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(iue.getLiteral()));
            UtilLog4j.log.fatal(this, iue.getMensajeParaProgramador());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    public void buscarPuesto() {
        puestosBeanModel.buscarRhPuesto();
        setOpcionSeleccionada("filtro");
    }

    public void openPopupCreateRhPuesto() {
        puestosBeanModel.controlaPopUpTrue("popupCreateRhPuesto");
        //this.sesion.getControladorPopups().put("popupCreateRhPuesto", Boolean.TRUE);
    }

    public void closePopupCreateRhPuesto() {
        this.setDescripcionPuesto("");
        this.setNombrePuesto("");
        clearComponent("popupCreateRhPuesto", "inpTxtNombre");
        clearComponent("popupCreateRhPuesto", "inpTxtDescripcion");
        puestosBeanModel.controlaPopUpFalso("popupCreateRhPuesto");
        //this.sesion.getControladorPopups().put("popupCreateRhPuesto", Boolean.FALSE);

    }

    public void openPopupUpdateRhPuesto() {
        this.puestosBeanModel.setRhPuestoVo((RhPuestoVo) this.puestosBeanModel.getDataModel().getRowData());
        this.puestosBeanModel.setNombrePuesto(this.puestosBeanModel.getRhPuestoVo().getNombre());
        this.puestosBeanModel.setDescripcionPuesto(this.puestosBeanModel.getRhPuestoVo().getDescripcion());
        this.puestosBeanModel.setIdPuesto(this.puestosBeanModel.getRhPuestoVo().getId());
        puestosBeanModel.controlaPopUpTrue("popupUpdateRhPuesto");
        //this.sesion.getControladorPopups().put("popupUpdateRhPuesto", Boolean.TRUE);
    }

    public void closePopupUpdateRhPuesto() {
        this.setDescripcionPuesto("");
        this.setNombrePuesto("");
        clearComponent("popupUpdateRhPuesto", "inpTxtNombre");
        clearComponent("popupUpdateRhPuesto", "inpTxtDescripcion");
        puestosBeanModel.controlaPopUpFalso("popupUpdateRhPuesto");
//        this.sesion.getControladorPopups().put("popupUpdateRhPuesto", Boolean.FALSE);
    }

    public String getNombrePuesto() {
        return puestosBeanModel.getNombrePuesto();
    }

    /**
     * @param nombreModulo the nombreModulo to set
     */
    public void setNombrePuesto(String nombrePuesto) {
        puestosBeanModel.setNombrePuesto(nombrePuesto);
    }

    /**
     * @return the rutaModulo
     */
    public String getDescripcionPuesto() {
        return puestosBeanModel.getDescripcionPuesto();
    }

    /**
     * @param rutaModulo the rutaModulo to set
     */
    public void setDescripcionPuesto(String descripcionPuesto) {
        puestosBeanModel.setDescripcionPuesto(descripcionPuesto);
    }

    public String getCadenaBuscar() {
        return puestosBeanModel.getCadenaBuscar();
    }

    /**
     * @param rutaModulo the rutaModulo to set
     */
    public void setCadenaBuscar(String cadenaBuscar) {
        puestosBeanModel.setCadenaBuscar(cadenaBuscar);
    }

    /**
     * @return the opcionSeleccionada
     */
    public String getOpcionSeleccionada() {
        return puestosBeanModel.getOpcionSeleccionada();
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(String opcionSeleccionada) {
        puestosBeanModel.setOpcionSeleccionada(opcionSeleccionada);
    }

    /**
     * @param puestosBeanModel the puestosBeanModel to set
     */
    public void setPuestosBeanModel(PuestosBeanModel puestosBeanModel) {
        this.puestosBeanModel = puestosBeanModel;
    }
}
