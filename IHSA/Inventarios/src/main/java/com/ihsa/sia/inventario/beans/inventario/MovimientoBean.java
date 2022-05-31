package com.ihsa.sia.inventario.beans.inventario;

import com.ihsa.sia.commons.AbstractBean;
import com.ihsa.sia.commons.Messages;
import com.ihsa.sia.commons.SessionBean;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import static sia.constantes.Constantes.INV_MOVIMIENTO_TIPO_ENTRADA;
import static sia.constantes.Constantes.INV_TRANSACCION_STATUS_PREPARACION;
import sia.excepciones.SIAException;
import sia.inventarios.service.AlmacenRemote;
import sia.inventarios.service.InvOrdenFormatoImpl;
import sia.inventarios.service.TransaccionRemote;
import sia.inventarios.service.Utilitarios;
import sia.modelo.SiAdjunto;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.modelo.vo.inventarios.ArticuloCompraVO;
import sia.modelo.vo.inventarios.OrdenFormatoVo;
import sia.modelo.vo.inventarios.TransaccionArticuloVO;
import sia.modelo.vo.inventarios.TransaccionVO;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.util.UtilLog4j;

/**
 * @author Aplimovil SA de CV
 */
@Named(value = "movimiento")
@ViewScoped
public class MovimientoBean extends AbstractBean implements Serializable {

    private TransaccionVO elemento;
    private List<AlmacenVO> almacenes;
    private List<SelectItem> tipos;
    private List<SelectItem> monedas;
    private List<TransaccionArticuloVO> elementos;
    private DataModel<TransaccionArticuloVO> model;
    private int indiceFilaEdicion;
    private Boolean folioValido;
    private Integer transaccionId;
    private String motivoRechazo;
    private DataModel<OrdenFormatoVo> formatos;
    @Inject
    protected AlmacenRemote almacenService;
    @Inject
    protected TransaccionRemote transaccionService;
    @Inject
    MonedaImpl monedaImpl;
    @Inject
    SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    InvOrdenFormatoImpl invOrdenFormatoImpl;
    @Inject
    SiParametroImpl siParametroImpl;
    @Inject
    ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    OrdenImpl ordenImpl;
    //
    @Inject
    TipoMovimientoBean tipoMovimiento;

    @Inject
    SessionBean principal;// = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");

    @Getter
    @Setter
    private UploadedFile fileInfo;

    @PostConstruct
    public void inicializar() {
        formatos = new ArrayDataModel<>();
        almacenes = almacenService.buscarPorFiltros(new AlmacenVO(), getCampoId());
        tipos = tipoMovimiento.buildSelectItems();
        inicializarVOs();
        cargarEditar();
        //monedas
        monedas = new ArrayList<>();
        List<MonedaVO> lmon = monedaImpl.traerMonedaActiva(getCampoId());
        for (MonedaVO monedaVO : lmon) {
            monedas.add(new SelectItem(monedaVO.getId(), monedaVO.getSiglas()));
        }
    }

    public void cargarEditar() {
        String paramTransaccionId = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("transaccionId");
        if (paramTransaccionId == null) {
            return;
        }
        try {
            transaccionId = Integer.valueOf(paramTransaccionId);
            setElemento(transaccionService.buscar(transaccionId));
            elementos = transaccionService.traerPorTrasaccionId(transaccionId, getCampoId());//obtenerListaArticulos(transaccionId, getCampoId());
            model = new ListDataModel(elementos);
            //
            if (elemento.getTipoMovimiento() == INV_MOVIMIENTO_TIPO_ENTRADA) {
                formatos = new ListDataModel<>(invOrdenFormatoImpl.traerPorMovimiento(elemento.getFolioOrdenCompra(), INV_MOVIMIENTO_TIPO_ENTRADA));
            }
        } catch (SIAException ex) {
            ManejarExcepcion(ex);
        }
    }

    public void eliminarFila(int indice) {
        elementos.remove(indice);
        if (elementos.isEmpty()) {
            elementos.add(new TransaccionArticuloVO());
        }
        model = new ListDataModel(elementos);
    }

    public void agregarFila() {
        elementos.add(new TransaccionArticuloVO());
        model = new ListDataModel(elementos);
    }

    public void validarFolio(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (INV_MOVIMIENTO_TIPO_ENTRADA.equals(getElemento().getTipoMovimiento())) {
            String folio = (String) value;
            if (folio == null || folio.isEmpty()) {
                folioValido = null;
                return;
            }
            try {
                folioValido = transaccionService.validarFolioOrdenDeCompra(folio);
            } catch (SIAException ex) {
                ManejarExcepcion(ex);
            }
        } else {
            folioValido = null;
        }
    }

    public String guardar() {
        List<TransaccionArticuloVO> elementosTemp = new ArrayList<>();
        if (folioValido != null && !folioValido) {
            addErrorMessage(obtenerCadenaDeRecurso("sia.inventarios.movimiento.folioOrdenCompraInvalido"));
            return null;//si no es valido el folio
        }
        try {
            boolean neg = false;

            for (TransaccionArticuloVO artTVoTemp : elementos) {
                if (artTVoTemp.isSelected()) {
                    elementosTemp.add(artTVoTemp);
                }

            }
            if (elemento.getTipoMovimiento() == INV_MOVIMIENTO_TIPO_ENTRADA) {
                for (TransaccionArticuloVO artTVo : elementosTemp) {
                    if (artTVo.getPrecioUnitario() <= Constantes.CERO
                            || artTVo.getNumeroUnidades() <= Constantes.CERO
                            || (artTVo.getNumeroUnidadesOriginal() > 0
                            && artTVo.getNumeroUnidadesOriginal() < artTVo.getNumeroUnidades())) {
                        neg = true;
                    }
                    artTVo.setTotalPendiente(artTVo.getNumeroUnidades());
                }
            }
            if (!neg && !elementosTemp.isEmpty()) {
                if (transaccionId == null) { //nuevo elemento
                    transaccionService.crear(elemento, elementosTemp, getUserName(), getCampoId());
                    agregarMensajeEnSesion(Messages.getString("sia.inventarios.movimientos.crearMensaje"));
                } else {
                    transaccionService.actualizar(elemento, elementosTemp, getUserName(), getCampoId());
                    agregarMensajeEnSesion(Messages.getString("sia.inventarios.movimientos.editarMensaje"));
                }
                elementos = new ArrayList<>();
                elementos.addAll(elementosTemp);
                return "movimientos?faces-redirect=true";
            } else {
                if (elementosTemp.isEmpty()) {
                    addErrorMessage("Se requiere agregar un artículo para el movimiento en el almacén.");
                } else {
                    addErrorMessage("No se permiten cantidades o precios unitarios menores a CERO ni cantidades mayores a las de la orden de compra.");
                }
            }
        } catch (SIAException ex) {
            ManejarExcepcion(ex);
        }
        return null;
    }

    public String procesar() {
        if (transaccionId == null) {
            return null;
        }
        try {
            transaccionService.procesar(transaccionId, getUserName(), getCampoId());
            agregarMensajeEnSesion(Messages.getString("sia.inventarios.movimientos.procesarMensaje"));
            return "movimientos?faces-redirect=true";
        } catch (SIAException ex) {
            ManejarExcepcion(ex);
        }
        return null;
    }

    public String confirmar() {
        try {
            transaccionService.confirmar(transaccionId, getUserName(), getCampoId());
            agregarMensajeEnSesion(Messages.getString("sia.inventarios.movimientos.confirmarMensaje"));
            return "movimientos?faces-redirect=true";
        } catch (SIAException ex) {
            ManejarExcepcion(ex);
        }
        return null;
    }

    public String rechazar() {
        try {
            transaccionService.rechazar(transaccionId, motivoRechazo, getUserName());
            motivoRechazo = "";
            agregarMensajeEnSesion(Messages.getString("sia.inventarios.movimientos.rechazarMensaje"));
            return "movimientos?faces-redirect=true";
        } catch (SIAException ex) {
            ManejarExcepcion(ex);
        }
        return null;
    }

    private void inicializarVOs() {
        elemento = new TransaccionVO();
        elemento.setFecha(new Date());
        elemento.setNumeroArticulos(0);
        elementos = new ArrayList<TransaccionArticuloVO>();
        elementos.add(new TransaccionArticuloVO());
        model = new ListDataModel(elementos);
    }

    public List<AlmacenVO> getAlmacenes() {
        return almacenes;
    }

    public List<SelectItem> getTipos() {
        return tipos;
    }

    public TransaccionVO getElemento() {
        return elemento;
    }

    public void setElemento(TransaccionVO elemento) {
        this.elemento = elemento;
    }

    public DataModel<TransaccionArticuloVO> getElementos() {
        return model;
    }

    public void setIndiceFilaEdicion(int indiceFilaEdicion) {
        this.indiceFilaEdicion = indiceFilaEdicion;
    }

    public int getIndiceFilaEdicion() {
        return indiceFilaEdicion;
    }

    public Boolean getFolioValido() {
        return folioValido;
    }

    public TipoMovimientoBean getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimientoBean tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getTitle() {
        if (transaccionId != null && FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("lista") != null) {
            return Messages.getString("sia.inventarios.movimiento.tituloListar");
        } else if (transaccionId != null) {
            return Messages.getString("sia.inventarios.movimiento.tituloEditar");
        }

        return Messages.getString("sia.inventarios.movimiento.titulo");
    }

    public String formatearFechaGenero(Date fechaGenero) {
        if (fechaGenero == null) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(fechaGenero);
    }

    private void agregarMensajeEnSesion(String mensaje) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("mensaje", mensaje);
    }

    /**
     * @return the motivoRechazo
     */
    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public boolean puedeEditarLista() {
        return getElemento().getId() == null || getElemento().getStatus().equals(INV_TRANSACCION_STATUS_PREPARACION);
    }

    /**
     * @param motivoRechazo the motivoRechazo to set
     */
    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public void cambioFolioOrdenDeCompra(ValueChangeEvent event) {
        //public void cambioFolioOrdenDeCompra(AjaxBehaviorEvent event) {

        if (folioValido != null && folioValido) {
            elementos.clear();
            String folioCompra = (String) event.getNewValue();
            //
            if (!Utilitarios.esNuloOVacio(folioCompra)) {
                //OrdenVO ovo = ordenImpl.buscarOrdenPorConsecutivo(folioCompra, false);
                //if (ovo.getCompania().equals(principal.getUser().getRfcEmpresa())) {
                List<ArticuloCompraVO> articuloCompraVOs = transaccionService.listarArticulosPorFolioOrdenDeCompra(folioCompra);
                //elementos.clear();
                formatos = new ListDataModel<>();
                for (ArticuloCompraVO articuloCompraVo : articuloCompraVOs) {
                    TransaccionArticuloVO transaccionArticuloVO = new TransaccionArticuloVO();
                    transaccionArticuloVO.setArticuloId(articuloCompraVo.getId());
                    transaccionArticuloVO.setArticuloNombre(articuloCompraVo.getNombre());
                    transaccionArticuloVO.setDetalleCompraId(articuloCompraVo.getIdDetalleCompra());
                    transaccionArticuloVO.setNumeroUnidades(articuloCompraVo.getCantidad());
                    transaccionArticuloVO.setNumeroUnidadesOriginal(articuloCompraVo.getCantidad());
                    transaccionArticuloVO.setPrecioUnitario(articuloCompraVo.getPrecio());
                    transaccionArticuloVO.setMoneda(articuloCompraVo.getMoneda());
                    transaccionArticuloVO.setIdMoneda(articuloCompraVo.getIdMoneda());
                    transaccionArticuloVO.setCantidad(articuloCompraVo.getCantidad());
                    //
                    elementos.add(transaccionArticuloVO);

                }
                formatos = new ListDataModel<>(invOrdenFormatoImpl.traerPorMovimiento(folioCompra, INV_MOVIMIENTO_TIPO_ENTRADA));
                //
                model = new ListDataModel<>(elementos);
            }
            //
        }
    }

    public void subirFormato(FileUploadEvent fileEntryEvent) {
        try {
            fileInfo = fileEntryEvent.getFile();
            AlmacenDocumentos almacenDocumentos = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
            documentoAnexo.setNombreBase(fileInfo.getFileName());
            documentoAnexo.setTipoMime(fileInfo.getContentType());
            documentoAnexo.setRuta(rutaFormato());

            almacenDocumentos.guardarDocumento(documentoAnexo);

            SiAdjunto adj = siAdjuntoImpl.save(documentoAnexo.getNombreBase(),
                    new StringBuilder()
                            .append(documentoAnexo.getRuta())
                            .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                    documentoAnexo.getTipoMime(), fileInfo.getSize(), getUserName());
            if (adj != null) {
                invOrdenFormatoImpl.guardar(getUserName(), elemento.getFolioOrdenCompra(), adj.getId(), INV_MOVIMIENTO_TIPO_ENTRADA);
                //
                formatos = new ListDataModel<>(invOrdenFormatoImpl.traerPorMovimiento(elemento.getFolioOrdenCompra(), INV_MOVIMIENTO_TIPO_ENTRADA));
            }
        } catch (SIAException e) {
            UtilLog4j.log.error(e);
        }
    }

    public void quitarFormato() {
        OrdenFormatoVo ofv = formatos.getRowData();
        invOrdenFormatoImpl.eliminar(getUserName(), ofv.getId());
        //
        formatos = new ListDataModel<>(invOrdenFormatoImpl.traerPorMovimiento(elemento.getFolioOrdenCompra(), INV_MOVIMIENTO_TIPO_ENTRADA));
    }

    public String rutaFormato() {
        if (!elemento.getFolioOrdenCompra().isEmpty()) {
            return new StringBuilder().append("ETS/Orden/Inventario/").append(elemento.getFolioOrdenCompra().replace("-", "")).toString();
        }
        return "";
    }

    /**
     * @return the monedas
     */
    public List<SelectItem> getMonedas() {
        return monedas;
    }

    /**
     * @param monedas the monedas to set
     */
    public void setMonedas(List<SelectItem> monedas) {
        this.monedas = monedas;
    }

    /**
     * @return the formatos
     */
    public DataModel<OrdenFormatoVo> getFormatos() {
        return formatos;
    }

    /**
     * @param formatos the formatos to set
     */
    public void setFormatos(DataModel<OrdenFormatoVo> formatos) {
        this.formatos = formatos;
    }
}
