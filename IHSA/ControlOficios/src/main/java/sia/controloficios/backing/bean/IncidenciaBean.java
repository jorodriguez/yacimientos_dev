/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.controloficios.backing.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FilesUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

/*import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;
import org.icefaces.util.JavaScriptRunner;*/
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.controloficios.sistema.bean.backing.Sesion;
import sia.controloficios.sistema.soporte.FacesUtils;
import sia.controloficios.sistema.soporte.PrimeUtils;
import sia.excepciones.InsufficientPermissionsException;
import sia.excepciones.SIAException;
import sia.modelo.Prioridad;
import sia.modelo.SiAdjunto;
import sia.modelo.SiCategoriaIncidencia;
import sia.modelo.oficio.vo.OficioConsultaVo;
import sia.modelo.sgl.vo.AdjuntoVO;
import sia.modelo.sistema.vo.CategoriaIncidenciaVo;
import sia.modelo.sistema.vo.IncidenciaVo;
import sia.servicios.catalogos.impl.PrioridadImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiCategoriaIncidenciaImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaAdjuntoImpl;
import sia.servicios.sgl.incidencia.vehiculo.impl.SiIncidenciaImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.util.TicketEstadoEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
//@ManagedBean
@Named(value = "incidenciaBean")
@ViewScoped
//public class IncidenciaBean extends OficioOpcionesBloquesUIBean {
public class IncidenciaBean implements Serializable{

    

    @Inject
    SiIncidenciaImpl incidenciaLocal;
    @Inject
    SiIncidenciaAdjuntoImpl incidenciaAdjuntoLocal;
    @Inject
    PrioridadImpl prioridadRemote;
    @Inject
    SiCategoriaIncidenciaImpl categoriaIncidenciaLocal;
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    SiAdjuntoImpl adjuntoRemote;
    @Inject
    FolioImpl folioRemote;
    @Inject
    RequisicionImpl requisicionRemote;
    @Inject
    OrdenImpl ordenRemote;
    @Inject
    SgSolicitudViajeImpl solicitudViajeRemote;
    @Inject
    SgViajeImpl sgViajeRemote;
    @Inject
    SiFacturaAdjuntoImpl facturaRemote;
    
    @Inject
    private Sesion sesion;
    
    @Getter
    @Setter
    private UploadedFile fileInfo;
 
    
    //
    private List<IncidenciaVo> incidencias;
    private List<AdjuntoVO> incidenciasAdjunto;
    private IncidenciaVo incidenciaVo;
    private List<SelectItem> prioridades;
    private List<SelectItem> tiposEvidencias;
    private int idPrioridad, idCatIncidencia;
    private String complemento;
    private final String simbolos = "&acute; ! ¡ &ldquo; &rdquo; # $ % &amp;  / \\ = ¿ ? ' &lsquo; &rsquo;  &gt; &lt; { } [ ]";
    private String tieneCodigo;
    private CategoriaIncidenciaVo categoriaIncidenciaVo;
    private int idTieneCod;

    
     private int oficioEntradaId;
    
    private int oficioSalidaId;
    
    
    /**
     * 
     * @throws SIAException 
     */
    @PostConstruct
    protected void postConstruct() {
        System.out.println("@Postconstruct en tickets");
        this.iniciarIncidencias();
        
        System.out.println("@sesion "+sesion.getUsuario().getNombre());
        //setear valor nuevo para mostrar publicos
        // valores iniciales para pantalla de consulta
        /*
        // validar si no hay una consulta iniciada en la sesión
        if (getSesion().getOficioConsultaVo() == null) {
            
            iniciarSesionIncidecias();
        }
        
        setVo(getSesion().getOficioConsultaVo());
        getVo().setMaxOficios(Constantes.OFICIOS_MAXIMO_RETORNO_CONSULTA_INICIAL);
        // establecer filtros de bloques para consulta 
        configurarCombosCompaniaBloqueGerencia();
        
        // mostrar resultados con filtros actuales al ingresar
        this.buscarOficios(null);

        // desactivar modo edición en esta sesión
        this.getSesion().setModoEdicion(false);
        
        // valores para popup de simbologia
        this.oficioEntradaId = Constantes.OFICIOS_TIPO_OFICIO_ENTRADA_ID;
        this.oficioSalidaId = Constantes.OFICIOS_TIPO_OFICIO_SALIDA_ID;
        */
        
    } // fin @PostConstruct
    
    
    

    public void iniciarIncidencias() {
        System.out.println("@@iniciarIncidencias");
        
        setIncidencias(new ArrayList<IncidenciaVo>());
        setIncidenciasAdjunto(new ArrayList<AdjuntoVO>());
        prioridades = new ArrayList<>();
        tiposEvidencias = new ArrayList<>();
        //
        llenarIncidencias();
        List<Prioridad> priors = prioridadRemote.findAll();
        priors.forEach(prior -> {
            prioridades.add(new SelectItem(prior.getId(), prior.getNombre()));
        });
        categoriaIncidenciaVo = new CategoriaIncidenciaVo();
        List<SiCategoriaIncidencia> cats = categoriaIncidenciaLocal.findAll();
        cats.forEach(cat -> {
            tiposEvidencias.add(new SelectItem(cat.getId(), cat.getNombre()));
        });
        if (tiposEvidencias != null) {
            idCatIncidencia = Integer.parseInt(String.valueOf(tiposEvidencias.get(0).getValue()));
            setCategoriaIncidenciaVo(categoriaIncidenciaLocal.buscarPorId(idCatIncidencia));
        }
    }

    private void llenarIncidencias() {
        System.out.println("@llenarIncidencias");
        setIncidencias(incidenciaLocal.traerPorUsuario(getSesion().getUsuario().getId(), TicketEstadoEnum.NUEVO.getId()));
        getIncidencias().addAll(incidenciaLocal.traerPorUsuario(getSesion().getUsuario().getId(), TicketEstadoEnum.ASIGNADO.getId()));
    }

    public void mostrarTickets(ActionEvent event) {
        System.out.println("@mostrarTickets");
        
        llenarIncidencias();
        
        PrimeUtils.executeScript("$(dialogoTickts).modal('show')");
        //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "$(dialogoTickts).modal('show');");
    }

    public void traerCategoria(AjaxBehaviorEvent event) {
        
        System.out.println("@traerCategoria");
        
        setCategoriaIncidenciaVo(categoriaIncidenciaLocal.buscarPorId(idCatIncidencia));
        //
        if (categoriaIncidenciaVo.getTabla() != null && !categoriaIncidenciaVo.getTabla().isEmpty()) {
            PrimeUtils.executeScript("mostrarDiv('divCodigo')");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "mostrarDiv('divCodigo');");
            tieneCodigo = "si";
            idTieneCod = 1;
            PrimeUtils.executeScript("mostrarDiv('inpCod')");
            PrimeUtils.executeScript("ocultarDiv('noInpCod')");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "mostrarDiv('inpCod');");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "ocultarDiv('noInpCod');");
        } else {
            idTieneCod = 0;
            tieneCodigo = "no";
            PrimeUtils.executeScript("ocultarDiv('divCodigo')");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "ocultarDiv('divCodigo');");
        }
    }

    public void mostrarTextoCodigo(AjaxBehaviorEvent event) {
        if (idTieneCod == 0) {
            PrimeUtils.executeScript("mostrarDiv('noInpCod')");
            PrimeUtils.executeScript("ocultarDiv('inpCod')");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "mostrarDiv('noInpCod');");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "ocultarDiv('inpCod');");
        } else {
            PrimeUtils.executeScript("mostrarDiv('inpCod')");
            PrimeUtils.executeScript("ocultarDiv('noInpCod')");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "mostrarDiv('inpCod');");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "ocultarDiv('noInpCod');");
        }
    }

    public void crearIncidecnia(ActionEvent event) {
        setIncidenciaVo(new IncidenciaVo());
        incidenciasAdjunto = new ArrayList<>();
        PrimeUtils.executeScript("$(dialogoNuevoTickts).modal('show')");
        //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "$(dialogoNuevoTickts).modal('show');");
        if (categoriaIncidenciaVo.getTabla() != null && !categoriaIncidenciaVo.getTabla().isEmpty()) {
            PrimeUtils.executeScript("mostrarDiv('divCodigo')");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "mostrarDiv('divCodigo');");
            tieneCodigo = "si";
            idTieneCod = 1;
        } else {
            tieneCodigo = "no";
            idTieneCod = 0;
            PrimeUtils.executeScript("ocultarDiv('divCodigo')");
            //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "ocultarDiv('divCodigo');");
        }
    }

    public void eliminarAAdjunto(ActionEvent event) {
        //
        int index = Integer.parseInt(FacesUtils.getRequestParameter("indexAdj"));
        //
        adjuntoRemote.eliminarArchivo(incidenciasAdjunto.get(index).getId(), getSesion().getUsuario().getId());
        incidenciasAdjunto.remove(index);
    }

    public void subirAdjunto(FilesUploadEvent fileEntryEvent) {
        
            ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
            try {

                UploadedFiles upFiles = fileEntryEvent.getFiles();
                                            
                AlmacenDocumentos almacenDocumentos
                        = proveedorAlmacenDocumentos.getAlmacenDocumentos();

                for (UploadedFile fileInfo : upFiles.getFiles()) {
                    boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

                    if (addArchivo) {
                        DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                        documentoAnexo.setRuta(uploadDirectoryTicket());
                        documentoAnexo.setNombreBase(fileInfo.getFileName());
                        almacenDocumentos.guardarDocumento(documentoAnexo);

                        SiAdjunto adj = adjuntoRemote.save(documentoAnexo.getNombreBase(),
                                new StringBuilder()
                                        .append(documentoAnexo.getRuta())
                                        .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                                fileInfo.getContentType(), fileInfo.getSize(), getSesion().getUsuario().getId());
                        //
                        incidenciaAdjuntoLocal.agregarArchivoIncidencia(incidenciaVo.getIdIncidencia(), getSesion().getUsuario().getId(), adj.getId());
                        //
                        incidenciasAdjunto = incidenciaAdjuntoLocal.traerArchivoPorIncidencia(incidenciaVo.getIdIncidencia());

                    } else {
                        FacesUtils.addErrorMessage(new StringBuilder()
                                .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                                .append(validadorNombreArchivo.getCaracteresNoValidos())
                                .toString());
                    }

                    //fileInfo.getFile().delete();
                }

            } catch (SIAException e) {
                UtilLog4j.log.error(e);
                FacesUtils.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
            } catch (Exception e) {
                UtilLog4j.log.error(e);
                FacesUtils.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
            }
       
    }

    public void eliminarArchivo(int idIncAdj) {
        //
        incidenciaAdjuntoLocal.eliminarRelacion(idIncAdj, getSesion().getUsuario().getId());
        incidenciasAdjunto = incidenciaAdjuntoLocal.traerArchivoPorIncidencia(incidenciaVo.getIdIncidencia());
    }

    public String uploadDirectoryTicket() {
        return new StringBuilder().append("Ticket/").append(getSesion().getUsuario().getId()).toString();
    }

    public void guardarEnviarIncidencia(ActionEvent actionEvent) {
        if (idTieneCod == 0 || categoriaIncidenciaVo.getTabla() == null) {
            incidenciaVo.setCodigoCategoria("NA");
            ejecutarRegistro();
        } else {
            boolean encontrado;
            if (!incidenciaVo.getCodigoCategoria().trim().isEmpty()) {
                encontrado = incidenciaLocal.validaCodigo(categoriaIncidenciaVo.getTabla(), categoriaIncidenciaVo.getCampoTabla(), incidenciaVo.getCodigoCategoria());
                if (encontrado) {
                    ejecutarRegistro();
                } else {
                    FacesUtils.addErrorMessage("msgCod", "Es necesario agregar un código válido para la solicitud.");
                }

                //    FacesUtils.addErrorMessage("Es necesario agregar un código bien formado (prefijo, guión y dígitos).");
            } else {
                FacesUtils.addErrorMessage("msgCod", "Es necesario agregar un código para el ticket.");
            }
        }
    }

    private void ejecutarRegistro() {
        getIncidenciaVo().setIdPrioridad(idPrioridad);
        getIncidenciaVo().setIdCategoriaIncidencia(idCatIncidencia);
        getIncidenciaVo().setIdCampo(getSesion().getUsuario().getApCampo().getId());
        getIncidenciaVo().setIdGerencia(getSesion().getUsuario().getGerencia() != null ? getSesion().getUsuario().getGerencia().getId() : Constantes.GERENCIA_ID_SGL);

        incidenciaLocal.guardar(getIncidenciaVo(), getIncidenciasAdjunto(), getSesion().getUsuario());
        //
        llenarIncidencias();
        PrimeUtils.executeScript("$(dialogoNuevoTickts).modal('hide')");
        //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "$(dialogoNuevoTickts).modal('hide');");
    }

    public void inicioCerrarTicket(ActionEvent event) {
        int idT = Integer.parseInt(FacesUtils.getRequestParameter("idTicket"));
        incidenciaVo = incidenciaLocal.buscarPorId(idT);
        //
        PrimeUtils.executeScript("$(dialogoCierreTickt).modal('show')");
        //sJavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "$(dialogoCierreTickt).modal('show');");
    }

    public void cerrarTicket(ActionEvent event) {
        incidenciaLocal.cerrarIncidencia(incidenciaVo, getComplemento(), getSesion().getUsuario());
        complemento = "";
        //
        llenarIncidencias();
        PrimeUtils.executeScript("$(dialogoCierreTickt).modal('hide')");
        //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "$(dialogoCierreTickt).modal('hide');");
    }

    public void inicioReenviarTicket(ActionEvent event) {
        int idT = Integer.parseInt(FacesUtils.getRequestParameter("idTicket"));
        incidenciaVo = incidenciaLocal.buscarPorId(idT);
        //
        PrimeUtils.executeScript("$(dialogoComplementoTickt).modal('show')");
        //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "$(dialogoComplementoTickt).modal('show');");
    }

    public void agregarAdjunto(ActionEvent event) {
        int idT = Integer.parseInt(FacesUtils.getRequestParameter("idTicket"));
        incidenciaVo = incidenciaLocal.buscarPorId(idT);
        incidenciasAdjunto = incidenciaAdjuntoLocal.traerArchivoPorIncidencia(idT);
        //
        PrimeUtils.executeScript("$(dialogoAdjuntaarTickt).modal('show')");
        //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "$(dialogoAdjuntaarTickt).modal('show');");
    }

    public void reenviarTicket(ActionEvent event) {
        incidenciaLocal.reenviarIncidencia(incidenciaVo, getComplemento(), getSesion().getUsuario());
        complemento = "";
        llenarIncidencias();
        PrimeUtils.executeScript("$(dialogoComplementoTickt).modal('hide')");
        //JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), "$(dialogoComplementoTickt).modal('hide');");
    }

    /**
     * @return the prioridades
     */
    public List<SelectItem> getPrioridades() {
        return prioridades;
    }

    /**
     * @param prioridades the prioridades to set
     */
    public void setPrioridades(List<SelectItem> prioridades) {
        this.prioridades = prioridades;
    }

    /**
     * @return the idPrioridad
     */
    public int getIdPrioridad() {
        return idPrioridad;
    }

    /**
     * @param idPrioridad the idPrioridad to set
     */
    public void setIdPrioridad(int idPrioridad) {
        this.idPrioridad = idPrioridad;
    }

    /**
     * @return the tiposEvidencias
     */
    public List<SelectItem> getTiposEvidencias() {
        return tiposEvidencias;
    }

    /**
     * @param tiposEvidencias the tiposEvidencias to set
     */
    public void setTiposEvidencias(List<SelectItem> tiposEvidencias) {
        this.tiposEvidencias = tiposEvidencias;
    }

    /**
     * @return the idCatIncidencia
     */
    public int getIdCatIncidencia() {
        return idCatIncidencia;
    }

    /**
     * @param idCatIncidencia the idCatIncidencia to set
     */
    public void setIdCatIncidencia(int idCatIncidencia) {
        this.idCatIncidencia = idCatIncidencia;
    }

    /**
     * @return the incidencias
     */
    public List<IncidenciaVo> getIncidencias() {
        return incidencias;
    }

    /**
     * @param incidencias the incidencias to set
     */
    public void setIncidencias(List<IncidenciaVo> incidencias) {
        this.incidencias = incidencias;
    }

    /**
     * @return the incidenciasAdjunto
     */
    public List<AdjuntoVO> getIncidenciasAdjunto() {
        return incidenciasAdjunto;
    }

    /**
     * @param incidenciasAdjunto the incidenciasAdjunto to set
     */
    public void setIncidenciasAdjunto(List<AdjuntoVO> incidenciasAdjunto) {
        this.incidenciasAdjunto = incidenciasAdjunto;
    }

    /**
     * @return the incidenciaVo
     */
    public IncidenciaVo getIncidenciaVo() {
        return incidenciaVo;
    }

    /**
     * @param incidenciaVo the incidenciaVo to set
     */
    public void setIncidenciaVo(IncidenciaVo incidenciaVo) {
        this.incidenciaVo = incidenciaVo;
    }

    /**
     * @return the complemento
     */
    public String getComplemento() {
        return complemento;
    }

    /**
     * @param complemento the complemento to set
     */
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    /**
     * @return the simbolos
     */
    public String getSimbolos() {
        return simbolos;
    }

    /**
     * @return the tieneCódigo
     */
    public String getTieneCodigo() {
        return tieneCodigo;
    }

    /**
     * @param tieneCodigo the tieneCodigo to set
     */
    public void setTieneCódigo(String tieneCodigo) {
        this.tieneCodigo = tieneCodigo;
    }

    /**
     * @return the categoriaIncidenciaVo
     */
    public CategoriaIncidenciaVo getCategoriaIncidenciaVo() {
        return categoriaIncidenciaVo;
    }

    /**
     * @param categoriaIncidenciaVo the categoriaIncidenciaVo to set
     */
    public void setCategoriaIncidenciaVo(CategoriaIncidenciaVo categoriaIncidenciaVo) {
        this.categoriaIncidenciaVo = categoriaIncidenciaVo;
    }

    /**
     * @return the idTieneCod
     */
    public int getIdTieneCod() {
        return idTieneCod;
    }

    /**
     * @param idTieneCod the idTieneCod to set
     */
    public void setIdTieneCod(int idTieneCod) {
        this.idTieneCod = idTieneCod;
    }

    public void refrescarTickets(ActionEvent event) {
        this.llenarIncidencias();
    }
    
    private void iniciarSesionIncidecias() {

        // inicializar bean default para las consultas
        
        
        // Removido 2/dic/14 - Generar VO vacío para búsqueda abierta
        /*OficioConsultaVo vo = OficioConsultaVo.instanciaMesActual();*/
        
        
        OficioConsultaVo vo = new OficioConsultaVo();
        
        // el rol de edición de oficios (emisores y receptores) deberán ver 
        // los combos sin preseleccionar para facilitar búsqueda y registro
        /*if (!getPermisos().isRolEdicionOficios()) {
            // inicializar a bloque activo del usuario
            configurarVo(vo, getSesion().getBloqueActivo());
        }*/

        getSesion().setOficioConsultaVo(vo);
        iniciarIncidencias();
    }

    /*@Override
    protected boolean permisosRequeridos() throws InsufficientPermissionsException {
        return getPermisos().isConsultarOficio();
    }*/

    public Sesion getSesion() {
        return sesion;
    }
    
}
