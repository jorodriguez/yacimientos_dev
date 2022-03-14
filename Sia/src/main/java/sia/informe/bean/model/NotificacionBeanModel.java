/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.informe.bean.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.SiAdjunto;
import sia.modelo.SiNotificacion;
import sia.modelo.SiNotificacionAdjunto;
import sia.modelo.SiTipoNotificacion;
import sia.notificaciones.sistema.impl.CorreoImpl;
import sia.servicios.comunicacion.impl.SiNotificacionAdjuntoImpl;
import sia.servicios.comunicacion.impl.SiNotificacionImpl;
import sia.servicios.comunicacion.impl.SiTipoNotificacionImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.ConversationsManager;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "notificacionBeanModel")
@ConversationScoped
public class NotificacionBeanModel implements Serializable {

    @EJB
    CorreoImpl correoServicea;
    @EJB
    SiNotificacionImpl siNotificacionService;
    @EJB
    SiTipoNotificacionImpl siTipoNotificacionService;
    @EJB
    private Conversation conversationNotificacion;
    @EJB
    private ConversationsManager conversationsManager;
    @EJB
    private SiAdjuntoImpl siAdjuntoImpl;
    @EJB
    private SiNotificacionAdjuntoImpl siNotificacionAdjuntoService;
    @EJB
    private SiParametroImpl siParametroImpl;
    private Sesion sesion;
    private SiNotificacion siNotificacion;
    private SiTipoNotificacion siTipoNotificacion;
    private SiNotificacionAdjunto siNotificacionAdjunto;
    private DataModel listaNotificacionModel;
    private DataModel listaAdjuntosModel;
    private List<SelectItem> listaTipoClasificacion;
    private int idTipoNotifiacion;
    private boolean mrSubirArchivo = false;
    private String correos; //<--solo para pruebas
    private String seleccion = "NOENVIADOS";
    private String operacion = "";
    private String directorioPath;
    private String disablePanel = "False";
    //popups
    private boolean mrCrearPopup = false;
    private boolean mrModificarPopup = false;
    private boolean mrDetallePopup = false;
    private boolean mrEnviarPopup = false;

    public NotificacionBeanModel() {
    }

    public void beginConversationCatalogoNotificacion() {
        UtilLog4j.log.info(this, "NotificacionBeanModel.beginConversationCatalogoNotificacion()");
        this.conversationsManager.finalizeAllConversations();
        this.conversationsManager.beginConversation(conversationNotificacion, "Notificacion");
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        traerNotificacionesNoEnviadas();
        seleccion = "NOENVIADOS";
    }

    public void traerNotificacionesEnviadas() {
        UtilLog4j.log.info(this, "traerNotificaciones");
        ListDataModel<SiNotificacion> notificacionModel = null;
        try {
            UtilLog4j.log.info(this, "Enviados Model ok");
            notificacionModel = new ListDataModel(siNotificacionService.findAllNotificacion(sesion.getUsuario(), "True"));
            this.setListaNotificacionModel(notificacionModel);
            UtilLog4j.log.info(this, "Datamodel asiganado " + getListaNotificacionModel().getRowCount());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    public void traerNotificacionesNoEnviadas() {
        UtilLog4j.log.info(this, "traerNotificacionesNoEnviadas");
        ListDataModel<SiNotificacion> notificacionModel = null;
        try {
            UtilLog4j.log.info(this, "EnviadosNO Model ok");
            notificacionModel = new ListDataModel(siNotificacionService.findAllNotificacion(sesion.getUsuario(), "False"));
            this.setListaNotificacionModel(notificacionModel);
            UtilLog4j.log.info(this, "Datamodel asiganado " + getListaNotificacionModel().getRowCount());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    public void traerAdjuntos() {
        UtilLog4j.log.info(this, "traerAdjuntos");
        try {
            ListDataModel<SiNotificacionAdjunto> notificacionAdjuntoModel = new ListDataModel(siNotificacionAdjuntoService.findAllNotificacionAdjuntoToNotificacion(getSiNotificacion()));
            this.setListaAdjuntosModel(notificacionAdjuntoModel);
            UtilLog4j.log.info(this, "Datamodel asiganado " + getListaNotificacionModel().getRowCount());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
        }
    }

    public List<SelectItem> traerClasificacionItems() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<SiTipoNotificacion> lt;
        try {
            lt = siTipoNotificacionService.findAllTiposNotificacion(sesion.getUsuario());
            for (SiTipoNotificacion tipon : lt) {
                SelectItem item = new SelectItem(tipon.getId(), tipon.getSiCodificacion().getLetras() + " | " + tipon.getNombre());
                l.add(item);
            }
            setListaTipoClasificacion(l);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error en la consulta de clasififcaciones");
        }
        return getListaTipoClasificacion();
    }

    public boolean createNotificacion() {
        UtilLog4j.log.info(this, "NOtificacionBEanModel");
        boolean ret = false;
        try {
            UtilLog4j.log.info(this, "idTiponotificacion " + getIdTipoNotifiacion());
            setSiTipoNotificacion(siTipoNotificacionService.find(getIdTipoNotifiacion()));
            UtilLog4j.log.info(this, "NOtifiacion encontrada " + getSiTipoNotificacion().getNombre());
            if (siNotificacionService.createNotificacion(getSiNotificacion(), getSiTipoNotificacion(), sesion.getUsuario())) {
                setSeleccion("NOENVIADOS");
                traerNotificacionesNoEnviadas();
                ret = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion  en crear notificaciones" + e.getMessage());
            return false;
        }
        return ret;
    }

    public boolean editNotificacion() {
        boolean ret = false;
        try {
            UtilLog4j.log.info(this, "idTipo notificacion " + getIdTipoNotifiacion());
            setSiTipoNotificacion(siTipoNotificacionService.find(getIdTipoNotifiacion()));
            if (siNotificacionService.updateNotificacion(getSiNotificacion(), getSiTipoNotificacion(), sesion.getUsuario())) {
                setSeleccion("NOENVIADOS");
                traerNotificacionesNoEnviadas();
                ret = true;
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion  en modifcar cod" + e.getMessage());
            return false;
        }
        return ret;
    }

    public void deleteNotificacion() {
        List<SiNotificacionAdjunto> la = null;
        try {
            //Eliminar todos los archivos
            siNotificacionService.eliminarNotificacion(siNotificacion, sesion.getUsuario());            
            la = siNotificacionAdjuntoService.findAllNotificacionAdjuntoToNotificacion(getSiNotificacion());
            if (la != null) {
                String path = this.siParametroImpl.find(1).getUploadDirectory();
                for (SiNotificacionAdjunto si : la) {
                    //eliminar
                    File file = new File(path + si.getSiAdjunto().getUrl());
                    if (file.delete()) {
                        UtilLog4j.log.info(this, "Entro a eliminar");
                        siAdjuntoImpl.eliminarArchivo(si.getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
                        UtilLog4j.log.info(this, "Elimino el archivo de siAdjunto");
                        siNotificacionAdjuntoService.deleteArchivoNotificacionAdjunto(si, sesion.getUsuario());
                    }
                    UtilLog4j.log.info(this, "entrando a eliminar el archivo fisico");
                    //Elimina la carpeta
                String dir = "SGyL/Vehiculo/ComprobanteMantenimiento" + "/" + getSiNotificacion().getId() + "/";
                UtilLog4j.log.info(this, "Ruta carpeta: " + dir);
                File sessionfileUploadDirectory = new File(path + dir);
                    if (sessionfileUploadDirectory.isDirectory()) {
                        try {
                            sessionfileUploadDirectory.delete();
                        } catch (SecurityException e) {
                            UtilLog4j.log.fatal(this, e.getMessage());
                        }
                    }
                }
            }
            setSeleccion("NOENVIADOS");
            traerNotificacionesNoEnviadas();
            setMrDetallePopup(false);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion  en eliminar notificacion " + e.getMessage());
        }
    }

    /**
     * ****************subir archivo *******************
     */
    public boolean guardarArchivo(String fileName, String contentType, long size) {
        boolean v = false;
        UtilLog4j.log.info(this, "Absolute path " + getDirectorioPath());
        SiAdjunto siAdjunto = siAdjuntoImpl.guardarArchivoDevolverArchivo(sesion.getUsuario().getId(), 1, "SGyL/Vehiculo/ComprobanteMantenimiento/" + getSiNotificacion().getId() + "/" + fileName, fileName, contentType, size, 9, "SGyL");
        UtilLog4j.log.info(this, "Aqui después de guardar el archivo");
        if (siAdjunto != null) {
            v = true;
            UtilLog4j.log.info(this, "Siadjunto fue != de null");
            siNotificacionAdjuntoService.addArchivoNotificacionAdjunto(getSiNotificacion(), siAdjunto, sesion.getUsuario());
        } else {
            siAdjuntoImpl.eliminarArchivo(siAdjunto, sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
        }
        return v;
    }

    public void dirNotifcacionesAdjuntos() {
        if (getSiNotificacion() != null) {
            this.setDirectorioPath(siParametroImpl.find(1).getUploadDirectory() + "SGyL/Vehiculo/ComprobanteMantenimiento" + "/" + getSiNotificacion().getId() + "/");
            UtilLog4j.log.info(this, "directorio " + getDirectorioPath());
        }
    }

    public void quitarArchivo() {
        //Se eliminan fisicamente los archivos
        UtilLog4j.log.info(this, "quitarArchivo");
        String path = this.siParametroImpl.find(1).getUploadDirectory();
        try {
            File file = new File(path + getSiNotificacionAdjunto().getSiAdjunto().getUrl());
            UtilLog4j.log.info(this, "path :" + path);
            UtilLog4j.log.info(this, "path absoluto :" + getSiNotificacionAdjunto().getSiAdjunto().getUrl());
            if (file.delete()) {
                UtilLog4j.log.info(this, "Entro a eliminar");
                siAdjuntoImpl.eliminarArchivo(getSiNotificacionAdjunto().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
                UtilLog4j.log.info(this, "Elimino el archivo de siAdjunto");
                siNotificacionAdjuntoService.deleteArchivoNotificacionAdjunto(getSiNotificacionAdjunto(), sesion.getUsuario());
                UtilLog4j.log.info(this, "Elimino el adjunto de mantenimiento");
            }
            UtilLog4j.log.info(this, "entrando a eliminar el archivo fisico");
            //Elimina la carpeta
//         for (PcTipoDocumento pcTipoDocumento : this.pcClasificacionServicioRemoto.traerArchivoActivo()) {
            String dir = "SGyL/Vehiculo/ComprobanteMantenimiento" + "/" + getSiNotificacion().getId() + "/";
            UtilLog4j.log.fatal(this, "Ruta carpeta: " + dir);
            File sessionfileUploadDirectory = new File(path + dir);
            if (sessionfileUploadDirectory.isDirectory()) {
                try {
                    sessionfileUploadDirectory.delete();
                } catch (SecurityException e) {
                    UtilLog4j.log.fatal(this, e.getMessage());
                }
            }
//        }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en quitar archivo :" + e.getMessage());
        }
    }

    /**
     * *************************************************
     */
    /**
     * ***** ENVIO DE CORREO ****************
     */
//    public boolean enviarInforme() {
//
//        if (correoService.enviarNotificacionInforme(getSiNotificacion(), "jorodriguez@ihsa.mx,mluis@ihsa.mx")) {
//            siNotificacionService.notificacionEnviada(getSiNotificacion(), sesion.getUsuario());
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * *************************************
     */
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

    /**
     * @return the siNotificacion
     */
    public SiNotificacion getSiNotificacion() {
        return siNotificacion;
    }

    /**
     * @param siNotificacion the siNotificacion to set
     */
    public void setSiNotificacion(SiNotificacion siNotificacion) {
        this.siNotificacion = siNotificacion;
    }

    /**
     * @return the listaNotificacionModel
     */
    public DataModel getListaNotificacionModel() {
        return listaNotificacionModel;
    }

    /**
     * @param listaNotificacionModel the listaNotificacionModel to set
     */
    public void setListaNotificacionModel(DataModel listaNotificacionModel) {
        this.listaNotificacionModel = listaNotificacionModel;
    }

    /**
     * @return the listaTipoClasificacion
     */
    public List<SelectItem> getListaTipoClasificacion() {
        return listaTipoClasificacion;
    }

    /**
     * @param listaTipoClasificacion the listaTipoClasificacion to set
     */
    public void setListaTipoClasificacion(List<SelectItem> listaTipoClasificacion) {
        this.listaTipoClasificacion = listaTipoClasificacion;
    }

    /**
     * @return the idTipoNotifiacion
     */
    public int getIdTipoNotifiacion() {
        return idTipoNotifiacion;
    }

    /**
     * @param idTipoNotifiacion the idTipoNotifiacion to set
     */
    public void setIdTipoNotifiacion(int idTipoNotifiacion) {
        this.idTipoNotifiacion = idTipoNotifiacion;
    }

    /**
     * @return the directorioPath
     */
    public String getDirectorioPath() {
        return directorioPath;
    }

    /**
     * @param directorioPath the directorioPath to set
     */
    public void setDirectorioPath(String directorioPath) {
        this.directorioPath = directorioPath;
    }

    /**
     * @return the siNotificacionAdjunto
     */
    public SiNotificacionAdjunto getSiNotificacionAdjunto() {
        return siNotificacionAdjunto;
    }

    /**
     * @param siNotificacionAdjunto the siNotificacionAdjunto to set
     */
    public void setSiNotificacionAdjunto(SiNotificacionAdjunto siNotificacionAdjunto) {
        this.siNotificacionAdjunto = siNotificacionAdjunto;
    }

    /**
     * @return the mrSubirArchivo
     */
    public boolean isMrSubirArchivo() {
        return mrSubirArchivo;
    }

    /**
     * @param mrSubirArchivo the mrSubirArchivo to set
     */
    public void setMrSubirArchivo(boolean mrSubirArchivo) {
        this.mrSubirArchivo = mrSubirArchivo;
    }

    /**
     * @return the listaAdjuntosModel
     */
    public DataModel getListaAdjuntosModel() {
        return listaAdjuntosModel;
    }

    /**
     * @param listaAdjuntosModel the listaAdjuntosModel to set
     */
    public void setListaAdjuntosModel(DataModel listaAdjuntosModel) {
        this.listaAdjuntosModel = listaAdjuntosModel;
    }

    /**
     * @return the mrDetallePopup
     */
    public boolean isMrDetallePopup() {
        return mrDetallePopup;
    }

    /**
     * @param mrDetallePopup the mrDetallePopup to set
     */
    public void setMrDetallePopup(boolean mrDetallePopup) {
        this.mrDetallePopup = mrDetallePopup;
    }

    /**
     * @return the seleccion
     */
    public String getSeleccion() {
        return seleccion;
    }

    /**
     * @param seleccion the seleccion to set
     */
    public void setSeleccion(String seleccion) {
        this.seleccion = seleccion;
    }

    /**
     * @return the operacion
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * @param operacion the operacion to set
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * @return the disablePanel
     */
    public String getDisablePanel() {
        return disablePanel;
    }

    /**
     * @param disablePanel the disablePanel to set
     */
    public void setDisablePanel(String disablePanel) {
        this.disablePanel = disablePanel;
    }

    /**
     * @return the correos
     */
    public String getCorreos() {
        return correos;
    }

    /**
     * @param correos the correos to set
     */
    public void setCorreos(String correos) {
        this.correos = correos;
    }

    /**
     * @return the mrEnviarPopup
     */
    public boolean isMrEnviarPopup() {
        return mrEnviarPopup;
    }

    /**
     * @param mrEnviarPopup the mrEnviarPopup to set
     */
    public void setMrEnviarPopup(boolean mrEnviarPopup) {
        this.mrEnviarPopup = mrEnviarPopup;
    }
}
