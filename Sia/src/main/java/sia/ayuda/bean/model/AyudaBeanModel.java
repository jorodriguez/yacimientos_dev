/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ayuda.bean.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ConversationScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.*;
import sia.servicios.sistema.impl.SiAyudaAdjuntoImpl;
import sia.servicios.sistema.impl.SiAyudaImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author sluis
 */
@Named
@ConversationScoped
public class AyudaBeanModel implements Serializable {

    @EJB
    private SiAyudaImpl ayudaService;
    @EJB
    private SiModuloImpl moduloService;
    @EJB
    private SiOpcionImpl opcionService;
    @EJB
    private SiParametroImpl parametroService;
    @EJB
    private SiAyudaAdjuntoImpl adjuntoAyudaService;
    private List<SiModulo> modulosActivosList = null;
    private List<SiOpcionVo> opcionesFiltradasByModuloList = new ArrayList<SiOpcionVo>();
    private List<SiAyuda> ayudasFiltradasByModuloAndOpcionList = null;
    private List<SiAdjunto> archivosAyudaList = null;
    private DataModel<SiAyuda> ayudasFiltradasByModuloAndOpcionDataModel = null;
    private DataModel<SiAdjunto> archivosAyudaDataModel = null;
    private DataModel dataModel;
    private SiAyuda ayudaSeleccionada;
    private SiAyudaAdjunto ayudaAdjunto;
    private Sesion sesion;
    private String nombreModuloSeleccionado;
    private int idModuloSeleccionado;
    private String nombreOpcionSeleccionada;
    private String nombreAyuda;
    private String nombreModulo;
    private String rutaModulo;
    private String nombreOpcion;
    private String paginaOpcion;    
    private int idOpcionSeleccionada;
    private boolean modalRenderedPopupCrearAyuda = false;
    private boolean modalRenderedPopupCrearOpcion = false;
    private boolean modalRenderedPopupCrearModulo = false;
    private boolean modalRenderedPopupActualizarAyuda = false;
    private boolean modalRenderedPopupEliminarAyuda = false;
    
    @PostConstruct
    public void init() {
        this.sesion = (Sesion) FacesUtils.getManagedBean("sesion");
        UtilLog4j.log.info(this, "idUsuario: " + sesion.getUsuario().getId());
    }
    
    public void beginConversationCatalogoAyudas() {
        UtilLog4j.log.info(this, "AyudaBeanModel.beginConversationCatalogoAyudas()");
        //Reiniciando variables por si acaso
        this.modulosActivosList = null;
        this.opcionesFiltradasByModuloList = new ArrayList<SiOpcionVo>();
        this.ayudasFiltradasByModuloAndOpcionList = null;
        this.archivosAyudaList = null;
        this.ayudasFiltradasByModuloAndOpcionDataModel = null;
        this.dataModel = null;
        this.archivosAyudaDataModel = null;
        this.ayudaSeleccionada = null;
        this.nombreModuloSeleccionado = null;
        this.nombreOpcionSeleccionada = null;        
        this.nombreAyuda = null;
        this.nombreModulo = null;
        this.rutaModulo = null;
        this.nombreOpcion = null;
        this.paginaOpcion = null;       
        this.idOpcionSeleccionada = -1;
    }
    
    public List<SiModulo> getAllModulosActivosFromService() {
        List<SiModulo> modulosListTmp = moduloService.getAllModulosByEstado(Constantes.NO_ELIMINADO);
        this.setModulosActivosList(modulosListTmp);
        return this.getModulosActivosList();
    }
    
    public void getAllAyudasByModuloAndOpcion() {
        UtilLog4j.log.info(this, "idOpcionSeleccionada: " + this.idOpcionSeleccionada);

        if (this.ayudasFiltradasByModuloAndOpcionDataModel == null) {
            if (this.nombreModuloSeleccionado != null && !this.nombreModuloSeleccionado.equals("")
                    && !this.nombreModuloSeleccionado.equals("-1") && this.idOpcionSeleccionada > 0) {
                this.ayudasFiltradasByModuloAndOpcionDataModel = (DataModel) new ListDataModel(ayudaService.getAyudasByModuloAndOpcion(this.nombreModulo, this.idOpcionSeleccionada, Constantes.NO_ELIMINADO));
            } else {
                UtilLog4j.log.info(this, "No se pudo obtener ninguna ayuda porque falta la Opción");
                this.ayudasFiltradasByModuloAndOpcionDataModel = null;
            }
        }
    }
    
    public void reloadAllAyudasByModuloAndOpcion() {
        UtilLog4j.log.info(this, "AyudaBeanModel.reloadAllAyudasByModuloAndOpcion()");
        
        UtilLog4j.log.info(this, "IdOpcion seleccionada: " +  this.idOpcionSeleccionada);
        UtilLog4j.log.info(this, "Nombre módulo seleccionado: " + this.nombreModuloSeleccionado);
        
            if (this.nombreModuloSeleccionado != null && !this.nombreModuloSeleccionado.equals("")
                    && !this.nombreModuloSeleccionado.equals("-1") && this.idOpcionSeleccionada != -1) {
                this.ayudasFiltradasByModuloAndOpcionDataModel = (DataModel) new ListDataModel(ayudaService.getAyudasByModuloAndOpcion(this.nombreModuloSeleccionado, this.idOpcionSeleccionada, Constantes.NO_ELIMINADO));
            } else {
                UtilLog4j.log.info(this, "No se pudo obtener ninguna ayuda porque falta la Opción");
                this.ayudasFiltradasByModuloAndOpcionDataModel = null;
            }
    }
    
    public void actualizarOpcionesFiltradasByModulo() {

        if(this.getIdModuloSeleccionado() > 0) {
            List<SiOpcionVo> opcionesListTmp = opcionService.getAllOpcionesByModulo(this.getIdModuloSeleccionado(), Constantes.NO_ELIMINADO);
            
            if(opcionesListTmp != null) {
                this.setOpcionesFiltradasByModuloList(opcionesListTmp);
            }
            else {
                this.setOpcionesFiltradasByModuloList(null);
            }
        }
        else {
            this.setOpcionesFiltradasByModuloList(null); //Si no se seleccionó ningún módulo, se debe vaciar la lista
        }
    }
    
    public void actualizarAyudasFiltradasByModuloAndOpcion() {
        if (this.getNombreModuloSeleccionado() != null && !this.getNombreModuloSeleccionado().equals("-1")) {
            if (this.getIdOpcionSeleccionada() > 0) {
                List<SiAyuda> ayudasTmp = ayudaService.getAyudasByModuloAndOpcion(this.getNombreModuloSeleccionado(), this.getIdOpcionSeleccionada(), Constantes.NO_ELIMINADO);
                this.setAyudasFiltradasByModuloAndOpcionList(ayudasTmp);
                ListDataModel<SiAyuda> ayudasListDataModel = new ListDataModel(ayudasTmp);
                this.setAyudasFiltradasByModuloAndOpcionDataModel((DataModel) ayudasListDataModel);
            } else {
                this.setAyudasFiltradasByModuloAndOpcionList(null);
                this.setAyudasFiltradasByModuloAndOpcionDataModel(null);
            }
        } else {
            this.setAyudasFiltradasByModuloAndOpcionList(null);
            this.setAyudasFiltradasByModuloAndOpcionDataModel(null);
        }
    }
    
    public void crearAyuda() throws Exception{
        UtilLog4j.log.info(this, "Crear Ayuda(params): nombreAyuda: " + this.getNombreAyuda() + ", nombreModulo: " + this.getNombreModuloSeleccionado()
                + ", idOpcion: " + this.getIdOpcionSeleccionada());
        
        ayudaService.crearAyuda(this.getNombreAyuda(), this.getIdModuloSeleccionado(), this.getIdOpcionSeleccionada(), sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
        reloadAllAyudasByModuloAndOpcion();
    }
    
    public void crearModulo() throws Exception{
        moduloService.crearModulo(this.nombreModulo, this.rutaModulo, sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
        getAllModulosActivosFromService();
        this.nombreModulo = null;
        this.rutaModulo = null;
    } 
    
    public void crearOpcion() throws Exception{
        opcionService.crearOpcion(this.getNombreOpcion(), this.getPaginaOpcion(), sesion.getUsuario().getId(), Constantes.NO_ELIMINADO, this.getNombreModuloSeleccionado(),0,null);
        actualizarOpcionesFiltradasByModulo();
    }    
    
    public void actualizarAyuda() throws Exception{
        ayudaService.actualizarAyuda(this.getAyudaSeleccionada(), this.getNombreAyuda(), this.getIdModuloSeleccionado(), this.getIdOpcionSeleccionada(), sesion.getUsuario().getId(), Constantes.NO_ELIMINADO);
    }
    
    public void eliminarAyuda() throws SIAException, Exception {
        ayudaService.deleteAyuda(this.getAyudaSeleccionada(), sesion.getUsuario().getId());
        reloadAllAyudasByModuloAndOpcion();
    }
    
    public void eliminarArchivo() throws SIAException, Exception {

        if (eliminarArchivoFisicamente(this.ayudaAdjunto.getSiAdjunto().getUrl())) {
            ayudaService.deleteAyudaAdjunto(this.ayudaAdjunto, sesion.getUsuario().getId());
            reloadArchivosAyuda();
            this.ayudaAdjunto = null;
        }
    }
    
    /**
     * Elimina físicamente un archivo
     * @param url
     * @return
     */
    public boolean eliminarArchivoFisicamente(String url) throws SIAException {
        try {
            File file = new File(parametroService.find(1).getUploadDirectory() + url);
            return file.delete();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            throw new SIAException(AyudaBeanModel.class.getName(), "eliminarArchivoFisicamente()",
                    "No se pudo eliminar el archivo físicamente. Porfavor contacta al Equipo del SIA para arreglar esto al correo soportesia@ihsa.mx");
        }
    }
    
    /**
     * Devuelve la ruta a la que voy a subir el archivo
     */
    public String getDirectorioPDF() {
        String directorio = "";
        if (directorio.isEmpty()) {
            directorio = parametroService.find(1).getUploadDirectory();
        }
        return directorio + "Ayuda/" + this.getAyudaSeleccionada().getId() + "/pdf/";
    }
    
    /**
     * Devuelve la ruta a la que voy a subir el archivo
     */
    public String getDirectorioVideo() {
        String directorio = "";
        if (directorio.isEmpty()) {
            directorio = parametroService.find(1).getUploadDirectory();
        }
        return directorio + "Ayuda/" + this.getAyudaSeleccionada().getId() + "/video/";
    }    
    
    public void guardarArchivoAyudaPDF(String nombreArchivo, String contentType, Long tamanioArchivo) throws SIAException, Exception {
        String ruta = "Ayuda/" + this.getAyudaSeleccionada().getId() + "/pdf/" + nombreArchivo;
        ayudaService.saveAyudaAdjunto(this.ayudaSeleccionada, nombreArchivo, contentType, tamanioArchivo, ruta, sesion.getUsuario().getId());
        reloadArchivosAyuda();
    }
    
    public void guardarArchivoAyudaVideo(String nombreArchivo, String contentType, Long tamanioArchivo) throws SIAException, Exception {
        String ruta = "Ayuda/" + this.getAyudaSeleccionada().getId() + "/video/" + nombreArchivo;
        ayudaService.saveAyudaAdjunto(this.ayudaSeleccionada, nombreArchivo, contentType, tamanioArchivo, ruta, sesion.getUsuario().getId());
        reloadArchivosAyuda();
    }

    public void getAllArchivosAyuda() throws SIAException, Exception {
        UtilLog4j.log.info(this, "AyudaBeanModel.getAllArchivosAyuda()");
        if(this.dataModel == null) {
            UtilLog4j.log.info(this, "Trayendo los archivos de la ayuda: " + this.getAyudaSeleccionada().getNombre());
            this.dataModel = (DataModel)new ListDataModel(adjuntoAyudaService.getAllAdjuntosByAyuda(this.ayudaSeleccionada));
        }
    }
    
    public void reloadArchivosAyuda() throws SIAException, Exception {
        UtilLog4j.log.info(this, "AyudaBeanModel.reloadArchivosAyuda()");
        this.dataModel = (DataModel)new ListDataModel(adjuntoAyudaService.getAllAdjuntosByAyuda(this.ayudaSeleccionada));
        UtilLog4j.log.info(this, "dataModel " + this.dataModel.getRowCount());
    }
    
    /**
     * @return the modulosActivos
     */
    public List<SiModulo> getModulosActivosList() {
        UtilLog4j.log.info(this, "AyudaBeanModel.getModulosActivosList()");
        List<SiModulo> modulosListTmp = moduloService.getAllModulosByEstado(Constantes.NO_ELIMINADO);
        this.setModulosActivosList(modulosListTmp);
        return this.modulosActivosList;
    }    
    
    /**
     * @return the nombreModuloSeleccionado
     */
    public String getNombreModuloSeleccionado() {
        return this.nombreModuloSeleccionado;
    }

    /**
     * @param nombreModuloSeleccionado the nombreModuloSeleccionado to set
     */
    public void setNombreModuloSeleccionado(String nombreModuloSeleccionado) {
        this.nombreModuloSeleccionado = nombreModuloSeleccionado;
    }

    /**
     * @return the nombreOpcionSeleccionada
     */
    public String getNombreOpcionSeleccionada() {
        return this.nombreOpcionSeleccionada;
    }

    /**
     * @param nombreOpcionSeleccionada the nombreOpcionSeleccionada to set
     */
    public void setNombreOpcionSeleccionada(String nombreOpcionSeleccionada) {
        this.nombreOpcionSeleccionada = nombreOpcionSeleccionada;
    }

    /**
     * @return the opcionesFiltradasByModuloList
     */
    public List<SiOpcionVo> getOpcionesFiltradasByModuloList() {
        return opcionesFiltradasByModuloList;
    }

    /**
     * @param opcionesFiltradasByModuloList the opcionesFiltradasByModuloList to set
     */
    public void setOpcionesFiltradasByModuloList(List<SiOpcionVo> opcionesFiltradasByModuloList) {
        this.opcionesFiltradasByModuloList = opcionesFiltradasByModuloList;
    }

    /**
     * @param modulosActivosList the modulosActivosList to set
     */
    public void setModulosActivosList(List<SiModulo> modulosActivosList) {
        this.modulosActivosList = modulosActivosList;
    }

    /**
     * @return the ayudasFiltradasByModuloAndOpcionDataModel
     */
    public DataModel<SiAyuda> getAyudasFiltradasByModuloAndOpcionDataModel() {
        return ayudasFiltradasByModuloAndOpcionDataModel;
    }

    /**
     * @param ayudasFiltradasByModuloAndOpcionDataModel the ayudasFiltradasByModuloAndOpcionDataModel to set
     */
    public void setAyudasFiltradasByModuloAndOpcionDataModel(DataModel<SiAyuda> ayudasFiltradasByModuloAndOpcionDataModel) {
        this.ayudasFiltradasByModuloAndOpcionDataModel = ayudasFiltradasByModuloAndOpcionDataModel;
    }

    /**
     * @return the ayudasFiltradasByModuloAndOpcionList
     */
    public List<SiAyuda> getAyudasFiltradasByModuloAndOpcionList() {
        return ayudasFiltradasByModuloAndOpcionList;
    }

    /**
     * @param ayudasFiltradasByModuloAndOpcionList the ayudasFiltradasByModuloAndOpcionList to set
     */
    public void setAyudasFiltradasByModuloAndOpcionList(List<SiAyuda> ayudasFiltradasByModuloAndOpcionList) {
        this.ayudasFiltradasByModuloAndOpcionList = ayudasFiltradasByModuloAndOpcionList;
    }

    /**
     * @return the nombreAyuda
     */
    public String getNombreAyuda() {
        return nombreAyuda;
    }

    /**
     * @param nombreAyuda the nombreAyuda to set
     */
    public void setNombreAyuda(String nombreAyuda) {
        this.nombreAyuda = nombreAyuda;
    }

    /**
     * @return the idOpcionSeleccionada
     */
    public int getIdOpcionSeleccionada() {
        return idOpcionSeleccionada;
    }

    /**
     * @param idOpcionSeleccionada the idOpcionSeleccionada to set
     */
    public void setIdOpcionSeleccionada(int idOpcionSeleccionada) {
        this.idOpcionSeleccionada = idOpcionSeleccionada;        
        //aqui voy a asignar al atributo 'nombreOpcionSeleccionada' el nombre que corresponde al´'idOpcionSeleccionada' buscando en la lista
        for(SiOpcionVo op : this.opcionesFiltradasByModuloList) {
            if(op.getId() == idOpcionSeleccionada) {
                this.setNombreOpcionSeleccionada(op.getNombre());
                UtilLog4j.log.info(this, "Asignando el nombreOpcionSeleccionada: " + op.getNombre());
            }
        }
    }

    /**
     * @return the ayudaSeleccionada
     */
    public SiAyuda getAyudaSeleccionada() {
        return ayudaSeleccionada;
    }

    /**
     * @param ayudaSeleccionada the ayudaSeleccionada to set
     */
    public void setAyudaSeleccionada(SiAyuda ayudaSeleccionada) {
        if(ayudaSeleccionada != null) {
            UtilLog4j.log.info(this, "Ayuda Seleccionada: " + ayudaSeleccionada.getNombre());
        }
        this.ayudaSeleccionada = ayudaSeleccionada;
    }

    /**
     * @return the archivosAyudaList
     */
    public List<SiAdjunto> getArchivosAyudaList() {
        return archivosAyudaList;
    }

    /**
     * @param archivosAyudaList the archivosAyudaList to set
     */
    public void setArchivosAyudaList(List<SiAdjunto> archivosAyudaList) {
        this.archivosAyudaList = archivosAyudaList;
    }

    /**
     * @param archivosAyudaDataModel the archivosAyudaDataModel to set
     */
    public void setArchivosAyudaDataModel(DataModel<SiAdjunto> archivosAyudaDataModel) {
        this.archivosAyudaDataModel = archivosAyudaDataModel;
    }

    /**
     * @return the archivosAyudaDataModel
     */
    public DataModel<SiAdjunto> getArchivosAyudaDataModel() {
        return archivosAyudaDataModel;
    }

    /**
     * @return the modalRenderedPopupCrearAyuda
     */
    public boolean isModalRenderedPopupCrearAyuda() {
        return modalRenderedPopupCrearAyuda;
    }

    /**
     * @param modalRenderedPopupCrearAyuda the modalRenderedPopupCrearAyuda to set
     */
    public void setModalRenderedPopupCrearAyuda(boolean modalRenderedPopupCrearAyuda) {
        this.modalRenderedPopupCrearAyuda = modalRenderedPopupCrearAyuda;
    }

    /**
     * @return the modalRenderedPopupActualizarAyuda
     */
    public boolean isModalRenderedPopupActualizarAyuda() {
        return modalRenderedPopupActualizarAyuda;
    }

    /**
     * @param modalRenderedPopupActualizarAyuda the modalRenderedPopupActualizarAyuda to set
     */
    public void setModalRenderedPopupActualizarAyuda(boolean modalRenderedPopupActualizarAyuda) {
        this.modalRenderedPopupActualizarAyuda = modalRenderedPopupActualizarAyuda;
    }

    /**
     * @return the modalRenderedPopupEliminarAyuda
     */
    public boolean isModalRenderedPopupEliminarAyuda() {
        return modalRenderedPopupEliminarAyuda;
    }

    /**
     * @param modalRenderedPopupEliminarAyuda the modalRenderedPopupEliminarAyuda to set
     */
    public void setModalRenderedPopupEliminarAyuda(boolean modalRenderedPopupEliminarAyuda) {
        this.modalRenderedPopupEliminarAyuda = modalRenderedPopupEliminarAyuda;
    }

    /**
     * @return the modalRenderedPopupCrearOpcion
     */
    public boolean isModalRenderedPopupCrearOpcion() {
        return modalRenderedPopupCrearOpcion;
    }

    /**
     * @param modalRenderedPopupCrearOpcion the modalRenderedPopupCrearOpcion to set
     */
    public void setModalRenderedPopupCrearOpcion(boolean modalRenderedPopupCrearOpcion) {
        this.modalRenderedPopupCrearOpcion = modalRenderedPopupCrearOpcion;
    }

    /**
     * @return the modalRenderedPopupCrearModulo
     */
    public boolean isModalRenderedPopupCrearModulo() {
        return modalRenderedPopupCrearModulo;
    }

    /**
     * @param modalRenderedPopupCrearModulo the modalRenderedPopupCrearModulo to set
     */
    public void setModalRenderedPopupCrearModulo(boolean modalRenderedPopupCrearModulo) {
        this.modalRenderedPopupCrearModulo = modalRenderedPopupCrearModulo;
    }

    /**
     * @return the nombreModulo
     */
    public String getNombreModulo() {
        return nombreModulo;
    }

    /**
     * @param nombreModulo the nombreModulo to set
     */
    public void setNombreModulo(String nombreModulo) {
        this.nombreModulo = nombreModulo;
    }

    /**
     * @return the rutaModulo
     */
    public String getRutaModulo() {
        return rutaModulo;
    }

    /**
     * @param rutaModulo the rutaModulo to set
     */
    public void setRutaModulo(String rutaModulo) {
        this.rutaModulo = rutaModulo;
    }

    /**
     * @return the nombreOpcion
     */
    public String getNombreOpcion() {
        return nombreOpcion;
    }

    /**
     * @param nombreOpcion the nombreOpcion to set
     */
    public void setNombreOpcion(String nombreOpcion) {
        this.nombreOpcion = nombreOpcion;
    }

    /**
     * @return the pagainaOpcion
     */
    public String getPaginaOpcion() {
        return paginaOpcion;
    }

    /**
     * @param pagainaOpcion the pagainaOpcion to set
     */
    public void setPaginaOpcion(String paginaOpcion) {
        this.paginaOpcion = paginaOpcion;
    }

    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
        return dataModel;
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * @return the ayudaAdjunto
     */
    public SiAyudaAdjunto getAyudaAdjunto() {
        return ayudaAdjunto;
    }

    /**
     * @param ayudaAdjunto the ayudaAdjunto to set
     */
    public void setAyudaAdjunto(SiAyudaAdjunto ayudaAdjunto) {
        this.ayudaAdjunto = ayudaAdjunto;
    }

    /**
     * @return the idModuloSeleccionado
     */
    public int getIdModuloSeleccionado() {
        return idModuloSeleccionado;
    }

    /**
     * @param idModuloSeleccionado the idModuloSeleccionado to set
     */
    public void setIdModuloSeleccionado(int idModuloSeleccionado) {
        this.idModuloSeleccionado = idModuloSeleccionado;
    }
    
}