/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.informe.bean.backing;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.naming.NamingException;
import org.apache.commons.io.monitor.FileEntry;
import sia.informe.bean.model.NotificacionBeanModel;
import sia.modelo.SiNotificacion;
import sia.modelo.SiNotificacionAdjunto;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "notificacionBean")
@RequestScoped
public class NotificacionBean implements Serializable {

    @Inject
    //@ManagedProperty(value = "#{notificacionBeanModel}")    
    private NotificacionBeanModel notificacionBeanModel;
    @Inject
    //@ManagedProperty(value = "#{sesion}")    
    private Sesion sesion;

    public NotificacionBean() {
    }

    public void goToCatalogoNotificacion() {
        notificacionBeanModel.beginConversationCatalogoNotificacion();
//        return "/vistas/comunicacion/informe/catalogoNotificacion";
    }

    public String goToCrearNotificacion() {
        notificacionBeanModel.setSiNotificacion(new SiNotificacion());
        notificacionBeanModel.setOperacion("INSERTAR");
        notificacionBeanModel.setIdTipoNotifiacion(-1);
        notificacionBeanModel.setDisablePanel("False");
        notificacionBeanModel.traerClasificacionItems();
        return "crearNotificacion";
    }

    public String goToModificarNotificacion() {
        notificacionBeanModel.setSiNotificacion((SiNotificacion) notificacionBeanModel.getListaNotificacionModel().getRowData());
        UtilLog4j.log.info(this, "Modo Modificacion");
        notificacionBeanModel.setOperacion("MODIFICAR");
        notificacionBeanModel.setDisablePanel("True");
        notificacionBeanModel.traerClasificacionItems();
        UtilLog4j.log.info(this, "traer clasificaciones ok");
        notificacionBeanModel.setIdTipoNotifiacion(notificacionBeanModel.getSiNotificacion().getSiTipoNotificacion().getId());
        return "crearNotificacion";
    }

    public void traerListaNotificacion() {
        try {
            this.notificacionBeanModel.traerNotificacionesEnviadas();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en traer " + e.getMessage());
        }
    }

    public void traerListaAdjuntos() {
        try {
            this.notificacionBeanModel.traerAdjuntos();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en traer adjuintos " + e.getMessage());
        }
    }

//    public void enviarInforme() {
//        notificacionBeanModel.setSiNotificacion((SiNotificacion) notificacionBeanModel.getListaNotificacionModel().getRowData());
//        if (notificacionBeanModel.enviarInforme()) {            
//            notificacionBeanModel.traerNotificacionesNoEnviadas();
//            notificacionBeanModel.setMrDetallePopup(false);
//            FacesUtils.addInfoMessage("Notificación enviada exitosamente..");
//        }
//    }

    public void inicioCreateNotificacion() {
        UtilLog4j.log.info(this, "Create nottificacion");
        try {
            this.notificacionBeanModel.createNotificacion();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en crear" + e.getMessage());
        }
    }

    public String createNotificacion() {
        UtilLog4j.log.info(this, "Create nottificacion");
        String ret = "";
        try {
            if (this.notificacionBeanModel.createNotificacion()) {
                UtilLog4j.log.info(this, "Se creo correctamente");
                notificacionBeanModel.setSiNotificacion(null);
                ret = "catalogoNotificacion";
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en crear" + e.getMessage());
            ret = "crearNotifiacion";
        }
        return ret;
    }

    public String editNotificacion() {
        UtilLog4j.log.info(this, "editnottificacion");
        String ret = "";
        try {
            if (this.notificacionBeanModel.editNotificacion()) {
                UtilLog4j.log.info(this, "Se modifico correctamente");
                ret = "catalogoNotificacion";
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en crear" + e.getMessage());
            ret = "crearNotifiacion";
        }
        return ret;
    }

    /**
     * ********************************************************************
     */
    public void subirComprobanteMantenimiento(FileEntryEvent fileEntryEvent) throws NamingException {
        boolean v = false;
        FileEntry fileEntry = (FileEntry) fileEntryEvent.getComponent();
        FileEntryResults results = fileEntry.getResults();
        for (FileEntryResults.FileInfo fileInfo : results.getFiles()) {
            if (fileInfo.getStatus().isSuccess() || fileInfo.isSaved()) {
                UtilLog4j.log.info(this, "Antes de guardar pero el archivo se guardo");
                v = notificacionBeanModel.guardarArchivo(
                        fileInfo.getFileName(),
                        fileInfo.getContentType(),
                        fileInfo.getSize());
                notificacionBeanModel.traerAdjuntos();
                notificacionBeanModel.setMrCrearPopup(false);
            } else {
                UtilLog4j.log.info(this, "No paso ");
            }
            if (v == false) {
                FacesUtils.addInfoMessage("Ocurrio una excepción, favor de comunicar a sia@ihsa.mx");
            }
        }
    }

    public void eliminarArchivo() {
        UtilLog4j.log.info(this, "eliminarArchivo");
        notificacionBeanModel.setSiNotificacionAdjunto((SiNotificacionAdjunto) notificacionBeanModel.getListaAdjuntosModel().getRowData());
        notificacionBeanModel.quitarArchivo();
        //traer los adjuntos del aviso seleccionado
        notificacionBeanModel.traerAdjuntos();
    }

    public void uploadFile() {
        UtilLog4j.log.info(this, "upload");
    }

    public void traerDirectorio() {
        notificacionBeanModel.dirNotifcacionesAdjuntos();
    }

    public String getDirArchivo() {
        return notificacionBeanModel.getDirectorioPath();
    }

    public void updateTodoNotificacion() {
        try {
            this.notificacionBeanModel.editNotificacion();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excep en modificar" + e.getMessage());
        }
    }

    public void deleteNotificacion() {
        try {
            this.notificacionBeanModel.setSiNotificacion((SiNotificacion) notificacionBeanModel.getListaNotificacionModel().getRowData());
            this.notificacionBeanModel.deleteNotificacion();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excep en eliminar" + e.getMessage());
        }
    }

    public String getSeleccion() {
        return notificacionBeanModel.getSeleccion();
    }

    public void setSeleccion(String seleccion) {
        notificacionBeanModel.setSeleccion(seleccion);
    }

    public String getOperacion() {
        return notificacionBeanModel.getOperacion();
    }

    public void setOperacion(String Op) {
        notificacionBeanModel.setOperacion(Op);
    }

    public String getDisablePanel() {
        return notificacionBeanModel.getDisablePanel();
    }

    public void setDisablePanel(String Op) {
        notificacionBeanModel.setDisablePanel(Op);
    }

    public void seleccionarOperacion(ValueChangeEvent event) {
        UtilLog4j.log.info(this, "Seleccionar operacion " + event.getNewValue().toString());
        try {
            notificacionBeanModel.setSeleccion(event.getNewValue().toString());
//            UtilLog4j.log.info(this, "Seleccion" + notificacionBeanModel.getSeleccionRadio());
            if (notificacionBeanModel.getSeleccion().equals("ENVIADOS")) {
                UtilLog4j.log.info(this, "Selecciono Enviados");
                notificacionBeanModel.traerNotificacionesEnviadas();
                notificacionBeanModel.setMrDetallePopup(false);
            } else {
                if (notificacionBeanModel.getSeleccion().equals("NOENVIADOS")) {
                    notificacionBeanModel.traerNotificacionesNoEnviadas();
                    notificacionBeanModel.setMrDetallePopup(false);
                    UtilLog4j.log.info(this, "selecciono no enviados");
                }
            }
//            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al seleecionar operacion " + e.getMessage());
        }
    }

    public SiNotificacion getSiNotificacion() {
        return this.notificacionBeanModel.getSiNotificacion();
    }

    public void setSiNotificacion(SiNotificacion siNotificacion) {
        this.notificacionBeanModel.setSiNotificacion(siNotificacion);
    }

    public DataModel getNotificacionModel() {
        return this.notificacionBeanModel.getListaNotificacionModel();
    }

    public DataModel getAdjuntosModel() {
        return this.notificacionBeanModel.getListaAdjuntosModel();
    }

    public List<SelectItem> getListaClasificacion() {
        return notificacionBeanModel.getListaTipoClasificacion();
    }

    public int getIdTipoClasificacion() {
        return notificacionBeanModel.getIdTipoNotifiacion();
    }

    public void setIdTipoClasificacion(int idTipoNotificacion) {
        notificacionBeanModel.setIdTipoNotifiacion(idTipoNotificacion);
    }

    /**
     * *************Eventos ********************************
     */
    public void cargarIdTipoClasificacion(ValueChangeEvent valueChangeEvent) {
        notificacionBeanModel.setIdTipoNotifiacion((Integer) valueChangeEvent.getNewValue());
        if (notificacionBeanModel.getIdTipoNotifiacion() > 0) {
            // se carga el id..            
        }
    }

    /**
     * ******************** POPUPS *********************************
     */
    public boolean getMrCrearPupup() {
        return this.notificacionBeanModel.isMrCrearPopup();
    }

    public boolean getMrModificarPupup() {
        return this.notificacionBeanModel.isMrModificarPopup();
    }


    public void abrirCrearPupup() {
        UtilLog4j.log.info(this, "Abrir pop ");
        this.notificacionBeanModel.setSiNotificacion((SiNotificacion) this.notificacionBeanModel.getListaNotificacionModel().getRowData());
        traerDirectorio();
        this.notificacionBeanModel.setMrCrearPopup(true);
    }

    public void cerrarCrearPupup() {
        this.notificacionBeanModel.setMrCrearPopup(false);
    }

    public void abrirModificarPupup() {
        this.notificacionBeanModel.setSiNotificacion((SiNotificacion) this.notificacionBeanModel.getListaNotificacionModel().getRowData());
        this.notificacionBeanModel.setMrModificarPopup(true);
    }

    public void cerrarModificarPupup() {
        this.notificacionBeanModel.setMrModificarPopup(false);
    }

    public boolean getMrDetallePupup() {
        return this.notificacionBeanModel.isMrDetallePopup();
    }

    public void abrirDetalllePupup() {
        this.notificacionBeanModel.setSiNotificacion((SiNotificacion) this.notificacionBeanModel.getListaNotificacionModel().getRowData());
        this.notificacionBeanModel.traerAdjuntos();
        this.notificacionBeanModel.setMrDetallePopup(true);
    }

    public void cerrarDetallePupup() {
        this.notificacionBeanModel.setMrDetallePopup(false);
    }
    
    public boolean getMrEnviarPupup() {
        return this.notificacionBeanModel.isMrEnviarPopup();
    }
    
     public void abrirEnviarPupup() {
        this.notificacionBeanModel.setSiNotificacion((SiNotificacion) this.notificacionBeanModel.getListaNotificacionModel().getRowData());
        this.notificacionBeanModel.setMrEnviarPopup(true);
    }

    public void cerrarEnviarPupup() {
        this.notificacionBeanModel.setMrEnviarPopup(false);
    }

    /**
     * ******************** FIN POPUS *******************************
     */
}
