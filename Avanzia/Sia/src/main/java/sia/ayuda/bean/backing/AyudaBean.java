/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ayuda.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;


import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.ayuda.bean.model.AyudaBeanModel;
import sia.excepciones.SIAException;
import sia.modelo.SiAyuda;
import sia.modelo.SiAyudaAdjunto;
import sia.modelo.SiModulo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@Named(value = "ayudaBean")
@RequestScoped
public class AyudaBean implements Serializable {

    @Inject
    private AyudaBeanModel ayudaBeanModel;

    public void goToCatalogoAyudas() {
        getAyudaBeanModel().beginConversationCatalogoAyudas();
//        return "/vistas/administracion/ayuda/catalogoAyudas";
    }

    private UploadedFile fileUpload;

    public String goToAdministrarArchivosAyudas() {
        getAyudaBeanModel().setAyudaSeleccionada(getAyudaBeanModel().getAyudasFiltradasByModuloAndOpcionDataModel().getRowData());
        return "/vistas/administracion/ayuda/administrarArchivosAyuda";
    }

    public void cargarOpcionesInCombo(ValueChangeEvent event) {
        if (event != null && (Integer) event.getNewValue() > 0) {
            getAyudaBeanModel().setNombreOpcionSeleccionada(null);
            getAyudaBeanModel().setIdOpcionSeleccionada(0);
            UtilLog4j.log.info(this, "Módulo Seleccionado: " + event.getNewValue());
            getAyudaBeanModel().setIdModuloSeleccionado((Integer) event.getNewValue());
            getAyudaBeanModel().setNombreModuloSeleccionado(event.getNewValue().toString());
            getAyudaBeanModel().actualizarOpcionesFiltradasByModulo();
            getAyudaBeanModel().actualizarAyudasFiltradasByModuloAndOpcion();
        }
    }

    public void cargarAyudasInTable(ValueChangeEvent event) {
        if (event != null && (Integer) event.getNewValue() > 0) {
            UtilLog4j.log.info(this, "Opción Seleccionada (id): " + event.getNewValue());
            ayudaBeanModel.setIdOpcionSeleccionada((Integer) event.getNewValue());
            ayudaBeanModel.actualizarAyudasFiltradasByModuloAndOpcion();
        }
    }

    public DataModel<SiAyuda> getAyudasFiltradasByModuloAndOpcionDataModel() {
        getAyudaBeanModel().getAllAyudasByModuloAndOpcion();
        return getAyudaBeanModel().getAyudasFiltradasByModuloAndOpcionDataModel();
    }

    public List<SelectItem> getModulosListItem() {
        UtilLog4j.log.info(this, "Llenando Combo de Módulos");
        List<SiModulo> modulosTmp = ayudaBeanModel.getModulosActivosList();
        List<SelectItem> modulosListItem = new ArrayList<SelectItem>();

        for (SiModulo op : modulosTmp) {
            SelectItem item = new SelectItem(op.getId(), op.getNombre());
            modulosListItem.add(item);
        }
        return modulosListItem;
    }

    public List<SelectItem> getOpcionesListItem() {
        UtilLog4j.log.info(this, "Llenando Combo de Opciones");
        List<SiOpcionVo> opcionesTmp = ayudaBeanModel.getOpcionesFiltradasByModuloList();
        if (opcionesTmp == null) {
            return null;
        }
        List<SelectItem> opcionesListItem = new ArrayList<SelectItem>();

        for (SiOpcionVo op : opcionesTmp) {
            SelectItem item = new SelectItem(op.getId(), op.getNombre());
            opcionesListItem.add(item);
        }
        return opcionesListItem;
    }

    public void mostrarPopupCrearAyuda() {
        UtilLog4j.log.info(this, "AyudaBean.mostrarPopupCrearAyuda");

        if (ayudaBeanModel.getIdModuloSeleccionado() > -1) {
            if (ayudaBeanModel.getIdOpcionSeleccionada() > -1) {
                ayudaBeanModel.setModalRenderedPopupCrearAyuda(!ayudaBeanModel.isModalRenderedPopupCrearAyuda());
            } else {
                FacesUtils.addInfoMessage("No has seleccionado ninguna opción!");
            }
        } else {
            FacesUtils.addInfoMessage("No has seleccionado ningún módulo!");
        }
    }

    public void mostrarPopupCrearModulo() {
        getAyudaBeanModel().setModalRenderedPopupCrearModulo(!ayudaBeanModel.isModalRenderedPopupCrearModulo());
    }

    public void mostrarPopupCrearOpcion() {
        if (ayudaBeanModel.getIdModuloSeleccionado() > -1) {
            getAyudaBeanModel().setModalRenderedPopupCrearOpcion(!ayudaBeanModel.isModalRenderedPopupCrearOpcion());
        } else {
            FacesUtils.addInfoMessage("No has seleccionado ningún módulo!");
        }
    }

    public void mostrarPopupActualizar() {
        SiAyuda ayuda = getAyudaBeanModel().getAyudasFiltradasByModuloAndOpcionDataModel().getRowData();
        getAyudaBeanModel().setNombreAyuda(ayuda.getNombre());
        getAyudaBeanModel().setNombreModuloSeleccionado(ayuda.getModulo().getNombre());
        getAyudaBeanModel().setIdModuloSeleccionado(ayuda.getModulo().getId());
        getAyudaBeanModel().setNombreOpcionSeleccionada(ayuda.getOpcion().getNombre());
        getAyudaBeanModel().setIdOpcionSeleccionada(ayuda.getOpcion().getId());
        getAyudaBeanModel().setAyudaSeleccionada(ayuda);
        getAyudaBeanModel().setModalRenderedPopupActualizarAyuda(!ayudaBeanModel.isModalRenderedPopupActualizarAyuda());
    }

    public void mostrarPopupEliminar() {
        getAyudaBeanModel().setAyudaSeleccionada((SiAyuda) getAyudasFiltradasByModuloAndOpcionDataModel().getRowData());
        getAyudaBeanModel().setModalRenderedPopupEliminarAyuda(!ayudaBeanModel.isModalRenderedPopupEliminarAyuda());
    }

    public void crearAyuda() throws Exception {
        UtilLog4j.log.info(this, "AyudaBean.crearAyuda()");
        if (ayudaBeanModel.getNombreAyuda() != null && !ayudaBeanModel.getNombreAyuda().equals("")) {
            try {
                getAyudaBeanModel().crearAyuda();
                cancelarCrearAyuda();
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e.getMessage());
                e.printStackTrace();
                FacesUtils.addInfoMessage(new SIAException().getMessage());
            }
        } else {
            FacesUtils.addInfoMessage("El nombre de la ayuda no puede ser vacío");
        }
    }

    public void crearModulo() {
        UtilLog4j.log.info(this, "AyudaBean.crearModulo()");
        if (ayudaBeanModel.getNombreModulo() != null && !ayudaBeanModel.getNombreModulo().equals("")) {
            if (ayudaBeanModel.getRutaModulo() != null && !ayudaBeanModel.getRutaModulo().equals("")) {
                try {
                    getAyudaBeanModel().crearModulo();
                    cancelarCrearModulo();
                } catch (Exception e) {
                    UtilLog4j.log.info(this, e.getMessage());
                    e.printStackTrace();
                    FacesUtils.addInfoMessage(new SIAException().getMessage());
                }
            } else {
                FacesUtils.addInfoMessage("La ruta del módulo no puede ser vacía");
            }
        } else {
            FacesUtils.addInfoMessage("El nombre del módulo no puede ser vacío");
        }
    }

    public void crearOpcion() throws Exception {
        if (getAyudaBeanModel().getNombreOpcion() != null && !ayudaBeanModel.getNombreOpcion().equals("")) {
            if (getAyudaBeanModel().getPaginaOpcion() != null && !ayudaBeanModel.getPaginaOpcion().equals("")) {
                try {
                    getAyudaBeanModel().crearOpcion();
                    cancelarCrearOpcion();
                } catch (Exception e) {
                    UtilLog4j.log.fatal(this, e.getMessage());
                    e.printStackTrace();
                    FacesUtils.addInfoMessage(new SIAException().getMessage());
                }
            } else {
                FacesUtils.addInfoMessage("La página no puede ser vacía");
            }
        } else {
            FacesUtils.addInfoMessage("El nombre de la opción no puede ser vacío");
        }
    }

    public void actualizarAyuda() {
        try {
            if (getAyudaBeanModel().getNombreAyuda() != null && !ayudaBeanModel.getNombreAyuda().equals("") && !ayudaBeanModel.getNombreAyuda().equals("-1")) {
                getAyudaBeanModel().actualizarAyuda();
                cancelarActualizarAyuda();
            } else {
                FacesUtils.addInfoMessage("El nombre de la ayuda no puede ser vacío");
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addInfoMessage(new SIAException().getMessage());
        }
    }

    public void eliminarAyuda() {
        try {
            getAyudaBeanModel().eliminarAyuda();
            cancelarEliminarAyuda();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
            cancelarEliminarAyuda();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
            cancelarEliminarAyuda();
        }
    }

    public void eliminarArchivo() {
        UtilLog4j.log.info(this, "AyudaBean.eliminarArchivo()");
        ayudaBeanModel.setAyudaAdjunto((SiAyudaAdjunto) ayudaBeanModel.getDataModel().getRowData());
        try {
            getAyudaBeanModel().eliminarArchivo();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void cancelarCrearAyuda() {
        getAyudaBeanModel().setNombreAyuda(null);
        getAyudaBeanModel().setNombreOpcionSeleccionada(null);
        getAyudaBeanModel().setIdOpcionSeleccionada(0);
        getAyudaBeanModel().setModalRenderedPopupCrearAyuda(!ayudaBeanModel.isModalRenderedPopupCrearAyuda());
    }

    public void cancelarCrearModulo() {
        getAyudaBeanModel().setNombreModulo(null);
        getAyudaBeanModel().setRutaModulo(null);
        getAyudaBeanModel().setModalRenderedPopupCrearModulo(!ayudaBeanModel.isModalRenderedPopupCrearModulo());
    }

    public void cancelarCrearOpcion() {
        getAyudaBeanModel().setNombreOpcion(null);
        getAyudaBeanModel().setPaginaOpcion(null);
        getAyudaBeanModel().setModalRenderedPopupCrearOpcion(!ayudaBeanModel.isModalRenderedPopupCrearOpcion());
    }

    public void cancelarActualizarAyuda() {
        getAyudaBeanModel().setNombreAyuda(null);
        getAyudaBeanModel().setModalRenderedPopupActualizarAyuda(!ayudaBeanModel.isModalRenderedPopupActualizarAyuda());
    }

    public void cancelarEliminarAyuda() {
        getAyudaBeanModel().setAyudaSeleccionada(null);
        getAyudaBeanModel().setModalRenderedPopupEliminarAyuda(!ayudaBeanModel.isModalRenderedPopupEliminarAyuda());
    }

    /**
     * Devuelve la ruta a la que voy a subir el archivo pdf
     */
    public String getDirectorioPdf() {
        return getAyudaBeanModel().getDirectorioPDF();
    }

    /**
     * Devuelve la ruta a la que voy a subir los archivos para el video
     */
    public String getDirectorioVideo() {
        return getAyudaBeanModel().getDirectorioVideo();
    }

    public void subirArchivoAyudaPDF(FileUploadEvent event) {
        UtilLog4j.log.info(this, "Subiendo archivo pdf...!");
        UploadedFile file = event.getFile();
        boolean savedIsSucessfull = false;
        try {
            UtilLog4j.log.info(this, "Archivo: " + file.getFileName());
            if (file.getContentType().equals("application/pdf")) { //PDF
                try {
                    getAyudaBeanModel().guardarArchivoAyudaPDF(file.getFileName(), file.getContentType(), file.getSize());
                } catch (SIAException siae) {
                    FacesUtils.addErrorMessage(siae.getMessage());
                    UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
                } catch (Exception e) {
                    UtilLog4j.log.info(this, e.getMessage());
                    FacesUtils.addErrorMessage(new SIAException().getMessage());
                }
            } else {
                FacesUtils.addInfoMessage("El archivo que intentas subir no es un .PDF");
                //eliminar del directorio fisico el archivo que ya se subio. No se sabe porque pero en este punto el componente ya subió fisicamente el archivo
                //y por eso hay que eliminarlo
                getAyudaBeanModel().eliminarArchivoFisicamente("Ayuda/" + getAyudaBeanModel().getAyudaSeleccionada().getId() + "/pdf/" + file.getFileName());
            }
        } catch (Exception e) {
            e.getMessage();
            SIAException siae = new SIAException("AyudaBean", "subirArchivoAyudaPDF", "Ocurrió un error al tratar de subir el archivo. Porfavor contacta al Equipo del SIA al correo soportesia@ihsa.mx");
            FacesUtils.addErrorMessage(siae.getMessage());
            e.getMessage();
        }
    }

    public void subirArchivoAyudaWMV(FileUploadEvent event) {
        UploadedFile fileInfo = event.getFile();
        UtilLog4j.log.info(this, "Subiendo archivo wmv...!");
        boolean v = false;
        try {
            UtilLog4j.log.info(this, "Archivo: " + fileInfo.getFileName());

            if (fileInfo.getContentType().equals("video/x-ms-wmv")) { //Archivos para el Video
                try {
                    getAyudaBeanModel().guardarArchivoAyudaVideo(fileInfo.getFileName(), fileInfo.getContentType(), fileInfo.getSize());
                } catch (SIAException siae) {
                    FacesUtils.addErrorMessage(siae.getMessage());
                    UtilLog4j.log.info(this, siae.getMensajeParaProgramador());
                } catch (Exception e) {
                    UtilLog4j.log.info(this, e.getMessage());
                    FacesUtils.addErrorMessage(new SIAException().getMessage());
                }
            } else {
                FacesUtils.addInfoMessage("El archivo que intentas subir no es un .WMV (Video)");
                //eliminar del directorio fisico el archivo que ya se subio. No se sabe porque pero en este punto el componente ya subio fisicamente el archivo
                //y por eso hay que eliminarlo
                getAyudaBeanModel().eliminarArchivoFisicamente("Ayuda/" + getAyudaBeanModel().getAyudaSeleccionada().getId() + "/video/" + fileInfo.getFileName());
            }
        } catch (Exception e) {
            e.getMessage();
            SIAException siae = new SIAException("AyudaBean", "subirArchivoAyudaWMV", "Ocurrió un error al tratar de subir el archivo. Porfavor contacta al Equipo del SIA para solucionarlo al correo soportesia@ihsa.mx");
            FacesUtils.addErrorMessage(siae.getMessage());
            e.getMessage();
        }
    }

    public DataModel getArchivosAyudaDataModel() {
        try {
            getAyudaBeanModel().getAllArchivosAyuda();
            return getAyudaBeanModel().getDataModel();
        } catch (SIAException siae) {
            FacesUtils.addErrorMessage(siae.getMessage());
            UtilLog4j.log.fatal(this, siae.getMensajeParaProgramador());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
        return getAyudaBeanModel().getDataModel();
    }

    public void uploadFile() {
        UtilLog4j.log.info(this, "");
    }

    /**
     * @return the nombreModuloSeleccionado
     */
    public String getNombreModuloSeleccionado() {
        return getAyudaBeanModel().getNombreModuloSeleccionado();
    }

    /**
     * @param nombreModuloSeleccionado the nombreModuloSeleccionado to set
     */
    public void setNombreModuloSeleccionado(String nombreModuloSeleccionado) {
        UtilLog4j.log.info(this, "setNombreModuloSeleccionado: " + nombreModuloSeleccionado);
        ayudaBeanModel.setNombreModuloSeleccionado(nombreModuloSeleccionado);
    }

    /**
     * @return the nombreOpcionSeleccionada
     */
    public String getNombreOpcionSeleccionada() {
        return getAyudaBeanModel().getNombreOpcionSeleccionada();
    }

    /**
     * @param nombreOpcionSeleccionada the nombreOpcionSeleccionada to set
     */
    public void setNombreOpcionSeleccionada(String nombreOpcionSeleccionada) {
        getAyudaBeanModel().setNombreOpcionSeleccionada(nombreOpcionSeleccionada);
    }

    /**
     * @return the nombreAyuda
     */
    public String getNombreAyuda() {
        return getAyudaBeanModel().getNombreAyuda();
    }

    /**
     * @param nombreAyuda the nombreAyuda to set
     */
    public void setNombreAyuda(String nombreAyuda) {
        getAyudaBeanModel().setNombreAyuda(nombreAyuda);
    }

    /**
     * @return the idOpcionSeleccionada
     */
    public int getIdOpcionSeleccionada() {
        return getAyudaBeanModel().getIdOpcionSeleccionada();
    }

    /**
     * @param idOpcionSeleccionada the idOpcionSeleccionada to set
     */
    public void setIdOpcionSeleccionada(int idOpcionSeleccionada) {
        UtilLog4j.log.info(this, "setIdOpcionSeleccionada: " + idOpcionSeleccionada);
        ayudaBeanModel.setIdOpcionSeleccionada(idOpcionSeleccionada);
    }

    /**
     * @return the ayudaSeleccionada
     */
    public SiAyuda getAyudaSeleccionada() {
        return getAyudaBeanModel().getAyudaSeleccionada();
    }

    /**
     * @param ayudaSeleccionada the ayudaSeleccionada to set
     */
    public void setAyudaSeleccionada(SiAyuda ayudaSeleccionada) {
        getAyudaBeanModel().setAyudaSeleccionada(ayudaSeleccionada);
    }

    /**
     * @return the modalRenderedPopupCrearAyuda
     */
    public boolean isModalRenderedPopupCrearAyuda() {
        return getAyudaBeanModel().isModalRenderedPopupCrearAyuda();
    }

    /**
     * @param modalRenderedPopupCrearAyuda the modalRenderedPopupCrearAyuda to
     * set
     */
    public void setModalRenderedPopupCrearAyuda(boolean modalRenderedPopupCrearAyuda) {
        getAyudaBeanModel().setModalRenderedPopupCrearAyuda(modalRenderedPopupCrearAyuda);
    }

    /**
     * @return the modalRenderedPopupActualizarAyuda
     */
    public boolean isModalRenderedPopupActualizarAyuda() {
        return getAyudaBeanModel().isModalRenderedPopupActualizarAyuda();
    }

    /**
     * @param modalRenderedPopupActualizarAyuda the
     * modalRenderedPopupActualizarAyuda to set
     */
    public void setModalRenderedPopupActualizarAyuda(boolean modalRenderedPopupActualizarAyuda) {
        getAyudaBeanModel().setModalRenderedPopupActualizarAyuda(modalRenderedPopupActualizarAyuda);
    }

    /**
     * @return the modalRenderedPopupEliminarAyuda
     */
    public boolean isModalRenderedPopupEliminarAyuda() {
        return getAyudaBeanModel().isModalRenderedPopupEliminarAyuda();
    }

    /**
     * @param modalRenderedPopupEliminarAyuda the
     * modalRenderedPopupEliminarAyuda to set
     */
    public void setModalRenderedPopupEliminarAyuda(boolean modalRenderedPopupEliminarAyuda) {
        getAyudaBeanModel().setModalRenderedPopupEliminarAyuda(modalRenderedPopupEliminarAyuda);
    }

    /**
     * @return the modalRenderedPopupCrearOpcion
     */
    public boolean isModalRenderedPopupCrearOpcion() {
        return getAyudaBeanModel().isModalRenderedPopupCrearOpcion();
    }

    /**
     * @param modalRenderedPopupCrearOpcion the modalRenderedPopupCrearOpcion to
     * set
     */
    public void setModalRenderedPopupCrearOpcion(boolean modalRenderedPopupCrearOpcion) {
        getAyudaBeanModel().setModalRenderedPopupCrearOpcion(modalRenderedPopupCrearOpcion);
    }

    /**
     * @return the modalRenderedPopupCrearModulo
     */
    public boolean isModalRenderedPopupCrearModulo() {
        return getAyudaBeanModel().isModalRenderedPopupCrearModulo();
    }

    /**
     * @param modalRenderedPopupCrearModulo the modalRenderedPopupCrearModulo to
     * set
     */
    public void setModalRenderedPopupCrearModulo(boolean modalRenderedPopupCrearModulo) {
        getAyudaBeanModel().setModalRenderedPopupCrearModulo(modalRenderedPopupCrearModulo);
    }

    /**
     * @return the nombreModulo
     */
    public String getNombreModulo() {
        return getAyudaBeanModel().getNombreModulo();
    }

    /**
     * @param nombreModulo the nombreModulo to set
     */
    public void setNombreModulo(String nombreModulo) {
        getAyudaBeanModel().setNombreModulo(nombreModulo);
    }

    /**
     * @return the rutaModulo
     */
    public String getRutaModulo() {
        return getAyudaBeanModel().getRutaModulo();
    }

    /**
     * @param rutaModulo the rutaModulo to set
     */
    public void setRutaModulo(String rutaModulo) {
        getAyudaBeanModel().setRutaModulo(rutaModulo);
    }

    /**
     * @return the nombreOpcion
     */
    public String getNombreOpcion() {
        return getAyudaBeanModel().getNombreOpcion();
    }

    /**
     * @param nombreOpcion the nombreOpcion to set
     */
    public void setNombreOpcion(String nombreOpcion) {
        getAyudaBeanModel().setNombreOpcion(nombreOpcion);
    }

    /**
     * @return the pagainaOpcion
     */
    public String getPaginaOpcion() {
        return getAyudaBeanModel().getPaginaOpcion();
    }

    /**
     * @param pagainaOpcion the pagainaOpcion to set
     */
    public void setPaginaOpcion(String paginaOpcion) {
        getAyudaBeanModel().setPaginaOpcion(paginaOpcion);
    }

    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
        return getAyudaBeanModel().getDataModel();
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
        getAyudaBeanModel().setDataModel(dataModel);
    }

    /**
     * @return the ayudaAdjunto
     */
    public SiAyudaAdjunto getAyudaAdjunto() {
        return getAyudaBeanModel().getAyudaAdjunto();
    }

    /**
     * @param ayudaAdjunto the ayudaAdjunto to set
     */
    public void setAyudaAdjunto(SiAyudaAdjunto ayudaAdjunto) {
        getAyudaBeanModel().setAyudaAdjunto(ayudaAdjunto);
    }

    /**
     * @return the ayudaBeanModel
     */
    public AyudaBeanModel getAyudaBeanModel() {
        return ayudaBeanModel;
    }

    /**
     * @param ayudaBeanModel the ayudaBeanModel to set
     */
    public void setAyudaBeanModel(AyudaBeanModel ayudaBeanModel) {
        this.ayudaBeanModel = ayudaBeanModel;
    }

    /**
     * @return the idModuloSeleccionado
     */
    public int getIdModuloSeleccionado() {
        return getAyudaBeanModel().getIdModuloSeleccionado();
    }

    /**
     * @param idModuloSeleccionado the idModuloSeleccionado to set
     */
    public void setIdModuloSeleccionado(int idModuloSeleccionado) {
        getAyudaBeanModel().setIdModuloSeleccionado(idModuloSeleccionado);
    }
}
