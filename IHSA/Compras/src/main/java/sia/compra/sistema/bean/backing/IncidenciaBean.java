/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.sistema.bean.backing;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Prioridad;
import sia.modelo.SiAdjunto;
import sia.modelo.SiCategoriaIncidencia;
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
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.util.TicketEstadoEnum;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class IncidenciaBean implements Serializable {

    /**
     * Creates a new instance of IncidenciaBean
     */
    public IncidenciaBean() {

    }

    @Inject
    private UsuarioBean sesion;

    @Inject
    SiIncidenciaImpl incidenciaImpl;
    @Inject
    SiIncidenciaAdjuntoImpl incidenciaAdjuntoImpl;
    @Inject
    PrioridadImpl prioridadImpl;
    @Inject
    SiCategoriaIncidenciaImpl categoriaIncidenciaImpl;
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    SiAdjuntoImpl adjuntoImpl;
    @Inject
    FolioImpl folioImpl;
    @Inject
    RequisicionImpl requisicionImpl;
    @Inject
    OrdenImpl ordenImpl;
    @Inject
    SgSolicitudViajeImpl solicitudViajeImpl;
    @Inject
    SgViajeImpl sgViajeImpl;
    @Inject
    SiFacturaImpl facturaImpl;
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
    @Getter
    @Setter
    private UploadedFile fileInfo;

    //    
    @PostConstruct
    public void iniciar() {
        setIncidencias(new ArrayList<IncidenciaVo>());
        setIncidenciasAdjunto(new ArrayList<AdjuntoVO>());
        prioridades = new ArrayList<>();
        tiposEvidencias = new ArrayList<>();
        //
        llenarIncidencias();
        List<Prioridad> priors = prioridadImpl.findAll();
        for (Prioridad prior : priors) {
            prioridades.add(new SelectItem(prior.getId(), prior.getNombre()));
        }
        categoriaIncidenciaVo = new CategoriaIncidenciaVo();
        List<SiCategoriaIncidencia> cats = categoriaIncidenciaImpl.findAll();
        for (SiCategoriaIncidencia cat : cats) {
            tiposEvidencias.add(new SelectItem(cat.getId(), cat.getNombre()));
        }
        if (tiposEvidencias != null) {
            idCatIncidencia = Integer.parseInt(String.valueOf(tiposEvidencias.get(0).getValue()));
            setCategoriaIncidenciaVo(categoriaIncidenciaImpl.buscarPorId(idCatIncidencia));
        }
    }

    private void llenarIncidencias() {
        setIncidencias(incidenciaImpl.traerPorUsuario(sesion.getUsuarioConectado().getId(), TicketEstadoEnum.NUEVO.getId()));
        getIncidencias().addAll(incidenciaImpl.traerPorUsuario(sesion.getUsuarioConectado().getId(), TicketEstadoEnum.ASIGNADO.getId()));
    }

    public void mostrarTickets() {
        llenarIncidencias();
        //
        PrimeFaces.current().executeScript("$(dialogoTickts).modal('show');");
    }

    public void traerCategoria() {
        setCategoriaIncidenciaVo(categoriaIncidenciaImpl.buscarPorId(idCatIncidencia));
        //
        if (categoriaIncidenciaVo.getTabla() != null && !categoriaIncidenciaVo.getTabla().isEmpty()) {
            PrimeFaces.current().executeScript("mostrarDiv('divCodigo');");
            tieneCodigo = "si";
            idTieneCod = 1;
            PrimeFaces.current().executeScript("mostrarDiv('inpCod');");
            PrimeFaces.current().executeScript("ocultarDiv('noInpCod');");
        } else {
            idTieneCod = 0;
            tieneCodigo = "no";
            PrimeFaces.current().executeScript("ocultarDiv('divCodigo');");
        }
    }

    public void mostrarTextoCodigo() {
        if (idTieneCod == 0) {
            PrimeFaces.current().executeScript("mostrarDiv('noInpCod');");
            PrimeFaces.current().executeScript("ocultarDiv('inpCod');");
        } else {
            PrimeFaces.current().executeScript("mostrarDiv('inpCod');");
            PrimeFaces.current().executeScript("ocultarDiv('noInpCod');");
        }
    }

    public void crearIncidecnia() {
        setIncidenciaVo(new IncidenciaVo());
        incidenciasAdjunto = new ArrayList<>();
        PrimeFaces.current().executeScript("$(dialogoNuevoTickts).modal('show');");
        if (categoriaIncidenciaVo.getTabla() != null && !categoriaIncidenciaVo.getTabla().isEmpty()) {
            PrimeFaces.current().executeScript("mostrarDiv('divCodigo');");
            tieneCodigo = "si";
            idTieneCod = 1;
        } else {
            tieneCodigo = "no";
            idTieneCod = 0;
            PrimeFaces.current().executeScript("ocultarDiv('divCodigo');");
        }
    }

    public void eliminarAAdjunto(int index) {
        //
        adjuntoImpl.eliminarArchivo(incidenciasAdjunto.get(index).getId(), sesion.getUsuarioConectado().getId());
        incidenciasAdjunto.remove(index);
    }

    public void subirAdjunto(FileUploadEvent event) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {

            AlmacenDocumentos almacenDocumentos
                    = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            fileInfo = event.getFile();
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setRuta(uploadDirectoryTicket());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                SiAdjunto adj = adjuntoImpl.save(documentoAnexo.getNombreBase(),
                        new StringBuilder()
                                .append(documentoAnexo.getRuta())
                                .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                        fileInfo.getContentType(), fileInfo.getSize(), sesion.getUsuarioConectado().getId());
                //
                incidenciaAdjuntoImpl.agregarArchivoIncidencia(incidenciaVo.getIdIncidencia(), sesion.getUsuarioConectado().getId(), adj.getId());
                //
                incidenciasAdjunto = incidenciaAdjuntoImpl.traerArchivoPorIncidencia(incidenciaVo.getIdIncidencia());

            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();

        } catch (IOException | SIAException e) {
            UtilLog4j.log.error(e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    public void eliminarArchivo(int idIncAdj) {
        //
        incidenciaAdjuntoImpl.eliminarRelacion(idIncAdj, sesion.getUsuarioConectado().getId());
        incidenciasAdjunto = incidenciaAdjuntoImpl.traerArchivoPorIncidencia(incidenciaVo.getIdIncidencia());
    }

    public String uploadDirectoryTicket() {
        return new StringBuilder().append("Ticket/").append(sesion.getUsuarioConectado().getId()).toString();
    }

    public void guardarEnviarIncidencia() {
        if (idTieneCod == 0 || categoriaIncidenciaVo.getTabla() == null) {
            incidenciaVo.setCodigoCategoria("NA");
            ejecutarRegistro();
        } else {
            boolean encontrado;
            if (!incidenciaVo.getCodigoCategoria().trim().isEmpty()) {
                encontrado = incidenciaImpl.validaCodigo(categoriaIncidenciaVo.getTabla(), categoriaIncidenciaVo.getCampoTabla(), incidenciaVo.getCodigoCategoria());
                if (encontrado) {
                    ejecutarRegistro();
                } else {
                    FacesUtilsBean.addErrorMessage("msgCod", "Es necesario agregar un código válido para la solicitud.");
                }

                //    FacesUtilsBean.addErrorMessage("Es necesario agregar un código bien formado (prefijo, guión y dígitos).");
            } else {
                FacesUtilsBean.addErrorMessage("msgCod", "Es necesario agregar un código para el ticket.");
            }
        }
    }

    private void ejecutarRegistro() {
        getIncidenciaVo().setIdPrioridad(idPrioridad);
        getIncidenciaVo().setIdCategoriaIncidencia(idCatIncidencia);
        getIncidenciaVo().setIdCampo(sesion.getUsuarioConectado().getApCampo().getId());
        getIncidenciaVo().setIdGerencia(sesion.getUsuarioConectado().getGerencia() != null ? sesion.getUsuarioConectado().getGerencia().getId() : Constantes.GERENCIA_ID_SGL);

        incidenciaImpl.guardar(getIncidenciaVo(), getIncidenciasAdjunto(), sesion.getUsuarioConectado());
        //
        llenarIncidencias();
        PrimeFaces.current().executeScript("$(dialogoNuevoTickts).modal('hide');");
    }

    public void inicioCerrarTicket(int idT) {
        incidenciaVo = incidenciaImpl.buscarPorId(idT);
        //
        PrimeFaces.current().executeScript("$(dialogoCierreTickt).modal('show');");
    }

    public void cerrarTicket() {
        incidenciaImpl.cerrarIncidencia(incidenciaVo, getComplemento(), sesion.getUsuarioConectado());
        complemento = "";
        //
        llenarIncidencias();
        PrimeFaces.current().executeScript("$(dialogoCierreTickt).modal('hide');");
    }

    public void inicioReenviarTicket(int idT) {
        incidenciaVo = incidenciaImpl.buscarPorId(idT);
        //
        PrimeFaces.current().executeScript("$(dialogoComplementoTickt).modal('show');");
    }

    public void agregarAdjunto(int idT) {
        incidenciaVo = incidenciaImpl.buscarPorId(idT);
        incidenciasAdjunto = incidenciaAdjuntoImpl.traerArchivoPorIncidencia(idT);
        //
        PrimeFaces.current().executeScript("$(dialogoAdjuntaarTickt).modal('show');");
    }

    public void reenviarTicket() {
        incidenciaImpl.reenviarIncidencia(incidenciaVo, getComplemento(), sesion.getUsuarioConectado());
        complemento = "";
        llenarIncidencias();
        PrimeFaces.current().executeScript("$(dialogoComplementoTickt).modal('hide');");
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
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
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

    public void refrescarTickets() {
        this.llenarIncidencias();
    }
}
