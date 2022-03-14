/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.informe.bean.model;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import sia.modelo.SiCodificacion;
import sia.modelo.SiTipoNotificacion;
import sia.servicios.comunicacion.impl.SiCodificacionImpl;
import sia.servicios.comunicacion.impl.SiTipoNotificacionImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.ConversationsManager;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "codificacionBeanModel")
@ConversationScoped
public class CodificacionBeanModel implements Serializable {

    @EJB
    SiCodificacionImpl siCodificacionService;
    @EJB
    SiTipoNotificacionImpl siTipoCodificacionService;
    @Inject
    private Conversation conversationCodificacion;
    @Inject
    private ConversationsManager conversationsManager;
    private Sesion sesion;
    private SiCodificacion siCodificacion;
    private SiTipoNotificacion siTipoNotificacion;
    private DataModel listaCodificacionesModel;
    //popups
    private boolean mrCrearPopup = false;
    private boolean mrModificarPopup = false;

    /**
     * Creates a new instance of CodificacionBeanModel
     */
    public CodificacionBeanModel() {
    }

    public void beginConversationCatalogoCodificacion() {
        UtilLog4j.log.info(this, "codificacionBeanModel.beginConversationCatalogoCodificacion()");
        this.conversationsManager.finalizeAllConversations();
        this.conversationsManager.beginConversation(conversationCodificacion, "Codificacion");
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        traerCodificaciones();
    }

    public void traerCodificaciones() {
        UtilLog4j.log.info(this, "traerCodificaciones");
        try {
            ListDataModel<SiTipoNotificacion> codificacionModel = new ListDataModel(siTipoCodificacionService.findAllTiposNotificacion(sesion.getUsuario()));
            this.setListaCodificacionesModel(codificacionModel);
            UtilLog4j.log.info(this, "Datamodel asiganado " + getListaCodificacionesModel().getRowCount());
        } catch (Exception e) {
            UtilLog4j.log.info(this, e.getMessage());
        }
    }

    public void createCodificacion() {
        try {
            if (siCodificacionService.createCodificacion(getSiCodificacion(), sesion.getUsuario())) {
                siTipoCodificacionService.createTipoNotificacion(getSiTipoNotificacion(), getSiCodificacion(), sesion.getUsuario());
                traerCodificaciones();
                setMrCrearPopup(false);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion  en crear cod" + e.getMessage());
        }
    }

    public void editCodificacion() {
        try {
            if (siCodificacionService.updateCodificacion(getSiCodificacion(), sesion.getUsuario())) {
                siTipoCodificacionService.updateTipoNotificacion(getSiTipoNotificacion(), sesion.getUsuario());
                traerCodificaciones();
                setMrModificarPopup(false);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion  en modifcar cod" + e.getMessage());
        }
    }

    public void editTipoCodificacion() {
        try {
            siTipoCodificacionService.updateTipoNotificacion(getSiTipoNotificacion(), sesion.getUsuario());
            traerCodificaciones();
            setMrModificarPopup(false);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion  en modifcar Tipo de codificacion cod" + e.getMessage());
        }
    }

    public void deleteCodificacion() {
        try {
            if (siCodificacionService.deleteCodificacion(getSiCodificacion(), sesion.getUsuario())) {
                siTipoCodificacionService.deleteTipoNotificacion(getSiTipoNotificacion(), sesion.getUsuario());
                traerCodificaciones();
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion  en eliminar cod" + e.getMessage());
        }
    }

    /**
     * @return the siCodificacion
     */
    public SiCodificacion getSiCodificacion() {
        return siCodificacion;
    }

    /**
     * @param siCodificacion the siCodificacion to set
     */
    public void setSiCodificacion(SiCodificacion siCodificacion) {
        this.siCodificacion = siCodificacion;
    }

    /**
     * @return the listaCodificacionesModel
     */
    public DataModel getListaCodificacionesModel() {
        return listaCodificacionesModel;
    }

    /**
     * @param listaCodificacionesModel the listaCodificacionesModel to set
     */
    public void setListaCodificacionesModel(DataModel listaCodificacionesModel) {
        this.listaCodificacionesModel = listaCodificacionesModel;
    }

    /**
     * @return the mrCrearPopup
     */
    public boolean isMrCrearPopup() {
        return mrCrearPopup;
    }

    /**
     * @param mrCrearPopup the mrCrearPopup to set
     */
    public void setMrCrearPopup(boolean mrCrearPopup) {
        this.mrCrearPopup = mrCrearPopup;
    }

    /**
     * @return the mrModificarPopup
     */
    public boolean isMrModificarPopup() {
        return mrModificarPopup;
    }

    /**
     * @param mrModificarPopup the mrModificarPopup to set
     */
    public void setMrModificarPopup(boolean mrModificarPopup) {
        this.mrModificarPopup = mrModificarPopup;
    }

    /**
     * @return the siTipoNotificacion
     */
    public SiTipoNotificacion getSiTipoNotificacion() {
        return siTipoNotificacion;
    }

    /**
     * @param siTipoNotificacion the siTipoNotificacion to set
     */
    public void setSiTipoNotificacion(SiTipoNotificacion siTipoNotificacion) {
        this.siTipoNotificacion = siTipoNotificacion;
    }
}
