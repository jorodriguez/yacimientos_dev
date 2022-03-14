/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.informe.bean.backing;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import sia.informe.bean.model.CodificacionBeanModel;
import sia.modelo.SiCodificacion;
import sia.modelo.SiTipoNotificacion;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "codificacionBean")
@RequestScoped
public class CodificacionBean implements Serializable {

    @Inject
    CodificacionBeanModel codificacionBeanModel;
    @Inject
    Sesion sesion;

    /**
     * Creates a new instance of CodificacionBean
     */
    public CodificacionBean() {
    }

    public void goToCatalogoTipoNotificacion() {
        codificacionBeanModel.beginConversationCatalogoCodificacion();
//        return "/vistas/comunicacion/informe/catalogoTipoNotificacion";
    }

    public void traerListaCodificacion() {
        try {
            this.codificacionBeanModel.traerCodificaciones();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en traer " + e.getMessage());
        }

    }

    public void createCodificacion() {
        try {
            this.codificacionBeanModel.createCodificacion();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en crear" + e.getMessage());
        }
    }

    public void updateTodoCodificacion() {
        try {
            this.codificacionBeanModel.editCodificacion();
            this.codificacionBeanModel.editTipoCodificacion();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en modificar" + e.getMessage());
        }
    }

    public void updateTipoNotificacion() {
        try {
            this.codificacionBeanModel.editTipoCodificacion();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en modificar" + e.getMessage());
        }
    }

    public void deleteCodificacion() {
        try {
            this.codificacionBeanModel.setSiTipoNotificacion((SiTipoNotificacion) codificacionBeanModel.getListaCodificacionesModel().getRowData());
            this.codificacionBeanModel.setSiCodificacion(this.codificacionBeanModel.getSiTipoNotificacion().getSiCodificacion());
            this.codificacionBeanModel.deleteCodificacion();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en eliminar" + e.getMessage());
        }
    }

    public SiCodificacion getSiCodificacion() {
        return this.codificacionBeanModel.getSiCodificacion();
    }

    public void setSiCodificacion(SiCodificacion siCodificacion) {
        this.codificacionBeanModel.setSiCodificacion(siCodificacion);
    }

    public SiTipoNotificacion getSiTipoNotificacion() {
        return this.codificacionBeanModel.getSiTipoNotificacion();
    }

    public void setSiTipoNotificacion(SiTipoNotificacion siTipoNotificacion) {
        this.codificacionBeanModel.setSiTipoNotificacion(siTipoNotificacion);
    }

    public DataModel getCodificacionModel() {
        return this.codificacionBeanModel.getListaCodificacionesModel();
    }

    /**
     * ******************** POPUPS *********************************
     */
    public boolean getMrCrearPupup() {
        return this.codificacionBeanModel.isMrCrearPopup();
    }

    public boolean getMrModificarPupup() {
        return this.codificacionBeanModel.isMrModificarPopup();
    }

    public void abrirCrearPupup() {
        this.codificacionBeanModel.setSiCodificacion(new SiCodificacion());
        this.codificacionBeanModel.setSiTipoNotificacion(new SiTipoNotificacion());
        this.codificacionBeanModel.setMrCrearPopup(true);
    }

    public void cerrarCrearPupup() {
        this.codificacionBeanModel.setMrCrearPopup(false);
    }

    public void abrirModificarPupup() {
        this.codificacionBeanModel.setSiTipoNotificacion((SiTipoNotificacion) codificacionBeanModel.getListaCodificacionesModel().getRowData());
        this.codificacionBeanModel.setSiCodificacion(this.codificacionBeanModel.getSiTipoNotificacion().getSiCodificacion());
        this.codificacionBeanModel.setMrModificarPopup(true);
    }

    public void cerrarModificarPupup() {
        this.codificacionBeanModel.setMrModificarPopup(false);
    }
    /**
     * ******************** FIN POPUS *******************************
     */
}
