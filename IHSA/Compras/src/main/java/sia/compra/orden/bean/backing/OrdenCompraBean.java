/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.MonedaBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.excepciones.SIAException;
import sia.inventarios.service.InvArticuloCampoImpl;
import sia.modelo.Convenio;
import sia.modelo.CvConvenioArticulo;
import sia.modelo.InvArticulo;
import sia.modelo.Moneda;
import sia.modelo.OcActividadPetrolera;
import sia.modelo.OcCategoriaEts;
import sia.modelo.OcOrdenEts;
import sia.modelo.OcSubTarea;
import sia.modelo.OcTarea;
import sia.modelo.OcUnidadCosto;
import sia.modelo.Orden;
import sia.modelo.OrdenDetalle;
import sia.modelo.Proveedor;
import sia.modelo.ProyectoOt;
import sia.modelo.ReRequisicionEts;
import sia.modelo.SiAdjunto;
import sia.modelo.SiUnidad;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.orden.vo.OrdenEtsVo;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;
import sia.constantes.Configurador;
import sia.inventarios.service.ArticuloRemote;
import sia.modelo.requisicion.vo.OcSubtareaVO;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioArticuloImpl;
import sia.servicios.orden.impl.OcCategoriaEtsImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.orden.impl.OrdenDetalleImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.orden.impl.OrdenSiMovimientoImpl;
import sia.servicios.requisicion.impl.OcActividadPetroleraImpl;
import sia.servicios.requisicion.impl.OcGerenciaProyectoImpl;
import sia.servicios.requisicion.impl.OcPresupuestoDetalleImpl;
import sia.servicios.requisicion.impl.OcSubTareaImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiRelCategoriaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.Env;

/**
 *
 * @author mluis
 */
@Named(value = "ordenCompraBean")
@ViewScoped
public class OrdenCompraBean implements Serializable {

    /**
     * Creates a new instance of OrdenCompraBean
     */
    public OrdenCompraBean() {
    }
    private final static UtilLog4j LOGGER = UtilLog4j.log;
    /**
     * @return Lista de ordenes de compra nuevas
     */

    //Sistema
    @Inject
    private UsuarioBean sesion;
    @Inject
    private MonedaBean monedaBean;
    @Inject
    OrdenBean ordenBean;

    //
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private ReRequisicionEtsImpl reRequisicionEtsImpl;
    @Inject
    private ArticuloRemote articuloImpl;
    @Inject
    private InvArticuloCampoImpl invArticuloCampoImpl;
    @Inject
    private SiRelCategoriaImpl siRelCategoriaImpl;
    @Inject
    private OcActividadPetroleraImpl ocActividadPetroleraImpl;
    @Inject
    private OcSubTareaImpl ocSubTareaImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private OcGerenciaProyectoImpl ocGerenciaProyectoImpl;
    @Inject
    private OrdenSiMovimientoImpl ordenSiMovimientoImpl;
    @Inject
    private OrdenDetalleImpl ordenDetalleImpl;
    @Inject
    private OcTareaImpl ocTareaImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @Inject
    private ReRequisicionEtsImpl servicioReRequisicion; //servicio que realiza operaciones con la relacione entee Requisicion y SiAdjunto
    @Inject
    private OcCategoriaEtsImpl servicioOcCategoriaEts;
    @Inject
    private OcOrdenEtsImpl servicioOcOrdenEts;
    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Inject
    private CvConvenioArticuloImpl cvConvenioArticuloImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private OcPresupuestoDetalleImpl ocPresupuestoDetalleImpl;
    //
    private Orden ordenActual;
    private OrdenDetalle itemActual;
    private List<OrdenVO> listaOrden;
    private List<OrdenVO> listaConContrato;
    private List<MovimientoVO> listaRechazos;
    private List<ContactoProveedorVO> listaContactos; //almacena la lista de contactos de cada proveedor
    private List<OrdenDetalleVO> listaItems; //almacena la lista de Items de la Orden de compra
    private Map<String, List<SelectItem>> listaMapa;
    private final static String JS_METHOD_LIMPIAR_TODOS = ";limpiarTodos();";
    private final static String ERR_OPERACION = "Ocurrió un error en la operación, por favor contacte al equipo de soporte de SIA : soportesia@ihsa.mx";
    protected static final String UPDATE_OPERATION = "Actualizar";
    protected static final String CREATE_OPERATION = "Crear";
    private String operacionItem;
    private String articuloTx;
    private int articuloID;
    private int idTarea;
    //private CategoriaVo categoriaVoInicial;
    private CategoriaVo categoriaVo;
    private List<SelectItem> listaMonedas;
    private String categoriasTxt;
    private List<CategoriaVo> categoriasSeleccionadas = new ArrayList<>();
    private List<ArticuloVO> articulosFrecuentes = new ArrayList<>();
    private List<ArticuloVO> articulosResultadoBqda = new ArrayList<>();
    private String referencia;// usuado tambien para buscar OC/S en pag ordenCompra
    private OrdenVO ordenVO;
    private InvArticulo articulo;
    // ETS
    private final String simbolos = "&acute; ! ¡ &ldquo; &rdquo; # $ % &amp;  / \\ = ¿ ? ' &lsquo; &rsquo;  &gt; &lt; { } [ ]";
    private String opcionSeleccionada;
    private DataModel<ReRequisicionEts> listaEts; //almacena la lista Especificacion tecnica de suministro    
    private DataModel<OrdenEtsVo> listaOcOrdenEts;
    private List listaTablaComparativa;
    private Map<Integer, Boolean> filasSeleccionadas = new HashMap<>();
    private boolean seleccionarTodo;
    private int idCategoriaSelccionada = -1;
    @Getter
    @Setter
    private int idCatSel;
    private int idOrdenSeleccionada = -1;
    private OcOrdenEts etsOcOrden;
    private List<SelectItem> listaCategoriaEts;
    private List<ConvenioArticuloVo> listaConvenioArticuloVo;
    private String nombreTareaMulti;
    private String nombreProyectoMulti;
    private int idMoneda;
    private List<OrdenVO> historicoVentas = new ArrayList<>();
    @Getter
    @Setter
    private UploadedFile fileInfo;
    @Getter
    @Setter
    private ArticuloVO articuloVo;
    @Getter
    @Setter
    private CategoriaVo categoriaTempVo;
    @Getter
    @Setter
    private List<CategoriaVo> categorias;
    @Getter
    @Setter
    private ConvenioArticuloVo convenioArticuloVo;
    @Getter
    DocumentoAnexo documentoAnexo;

    //
    @PostConstruct
    public void iniciar() {
        llenarListaOrden();
        listaCategoriaEts = new ArrayList<>();
        traerOcCategoriasItems();
        listaMonedas = new ArrayList<>();
        ordenVO = new OrdenVO();
        ordenVO.setDetalleOrden(new ArrayList<>());
        categorias = new ArrayList<>();
    }

    private void llenarListaOrden() {
        if (sesion.getUsuarioConectado() != null) {
            setListaOrden(ordenImpl.ordenesPendientes(sesion.getUsuarioConectado().getId(), sesion.getUsuarioConectado().getApCampo().getId(), Constantes.BOOLEAN_FALSE));
            setListaConContrato(ordenImpl.ordenesPendientes(sesion.getUsuarioConectado().getId(), sesion.getUsuarioConectado().getApCampo().getId(), Constantes.BOOLEAN_TRUE));
        }

    }

    public void iniciarLimpiar() {
        setOrdenActual(null);
        NotaOrdenBean notaOrdenBean = (NotaOrdenBean) FacesUtilsBean.getManagedBean("notaOrdenBean");
        notaOrdenBean.setFiltrar(false);
        //
        llenarListaOrden();
    }

    public void regresarEts() {
        PrimeFaces.current().executeScript("regresar('divDatos', 'Etsregresar', 'divCargaEts', 'divOperacion');");
    }

    public void eliminarOrden() {
        try {
            for (Object object : getListaOrden()) {
                OrdenVO o = (OrdenVO) object;
                if (o.isSelected()) {
                    disgregarEtsRequisicion(o.getIdRequisicion());
                    ordenImpl.remove(o.getId(), sesion.getUsuarioConectado().getId());
                    FacesUtilsBean.addInfoMessage("Se eliminaron las OC/S y regresaron a recepción de requisiciones.");
                    setOrdenActual(null);
                    setListaItems(null);
                }
            }
            llenarListaOrden();
            String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinSolicitar();
            contarBean.llenarRecReq();
            //
            PrimeFaces.current().executeScript(jsMetodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public void eliminarOrdenConContrato() {
        ordenImpl.remove(ordenActual.getId(), sesion.getUsuarioConectado().getId());
        PrimeFaces.current().executeScript("regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
        llenarListaOrden();
        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        contarBean.llenarOcsSinSolicitar();
        contarBean.llenarRecReq();
    }

    private boolean disgregarEtsRequisicion(int idRequisicion) {
        try {
            return reRequisicionEtsImpl.disgregarAdjuntosPorRequisicion(idRequisicion, sesion.getUsuarioConectado());
        } catch (Exception e) {
            FacesUtilsBean.addErrorMessage(ERR_OPERACION);
            UtilLog4j.log.info(this, "Excepcion al momento de disgregar las ETS de la requisicion " + e.getMessage());
            return false;
        }
    }

    public void completarActualizacionOrden() {
        try {
            ordenImpl.editarOrden(getOrdenActual());
            //Esto es para cerrar el panel emergente de modificar Orden
            FacesUtilsBean.addInfoMessage("Se actualizó correctamente la orden de compra");
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoModOCS);");
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public void itemsPorOrden() {
        try {
            if (this.getOrdenActual().isMultiproyecto()) {
                setListaItems(ordenImpl.itemsPorOrdenCompraMulti(getOrdenActual().getId()));
            } else {
                setListaItems(ordenImpl.itemsPorOrdenCompra(getOrdenActual().getId()));
            }
        } catch (Exception ex) {
            setListaItems(null);
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public void itemsPorOrdenSingle() {
        try {
            setListaItems(ordenImpl.itemsPorOrdenCompra(getOrdenActual().getId()));
        } catch (Exception ex) {
            setListaItems(null);
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public String soliciarOCS() {
        //OrdenVO o = ordenImpl.buscarOrdenPorId(ordenActual.getId(), ordenActual.getApCampo().getId(), false);
        //        
        return solOCS(ordenActual.getId());
    }

    public String solicitarOrden(int idOrd) {
        return solOCS(idOrd);
    }

    private String solOCS(int idOrden) {
        try {
            if (ordenActual == null) {
                setOrdenActual(ordenImpl.find(idOrden));
            }
            //
            itemsPorOrdenSingle();
            boolean hasInvArticulos = ordenDetalleImpl.tieneInvArticulo(idOrden, false);
            String msgValidarPresupuesto = ordenDetalleImpl.validarPresupuesto(idOrden);
            if (getListaItems() == null || getListaItems().isEmpty() || !hasInvArticulos) {
                if (!hasInvArticulos) {
                    itemsPorOrden();
                    PrimeFaces.current().executeScript(
                            ";alertaGeneral('No es posible solicitar una OC/S con items que no están en el catálogo de producto junto con items que sí lo están.');");
                } else {
                    itemsPorOrden();
                    PrimeFaces.current().executeScript(";alertaGeneral('No es posible solicitar una OC/S sin Items');");
                }
            } else if (ordenActual.getTotal() == null || ordenActual.getTotal() == Constantes.CERO) {
                itemsPorOrden();
                PrimeFaces.current().executeScript(
                        ";alertaGeneral('No se puede solicitar la orden de compra sin un total, por favor modifique una partida para recalcular el total de la orden.');");
            } else if (ordenActual.getMoneda() == null) {
                itemsPorOrden();
                PrimeFaces.current().executeScript(
                        ";alertaGeneral('No se puede solicitar la orden de compra sin la moneda, por favor seleccione el tipo de moneda para la orden.');");
            } else if (ordenActual.getMoneda().getId() < Constantes.UNO) {
                itemsPorOrden();
                PrimeFaces.current().executeScript(
                        ";alertaGeneral('No se puede solicitar la orden de compra sin la moneda, por favor seleccione el tipo de moneda para la orden.');");
            } else if (Configurador.validarConvenio() && ordenActual.isConConvenio() && !ordenImpl.validarConvenio(ordenActual.getId(), ordenActual.getContrato())) {
                itemsPorOrden();
                ordenImpl.notificarValidarContrato(
                        ordenActual,
                        "El convenio " + ordenActual.getContrato() + " junto con sus convenios modificatorios no cuenta con saldo suficiente para realizar la compra.");
                PrimeFaces.current().executeScript(
                        ";alertaGeneral('El convenio " + ordenActual.getContrato() + " junto con sus convenios modificatorios no cuenta con saldo suficiente para realizar la compra.');");
            } else if (Configurador.validarPresupuesto() && msgValidarPresupuesto != null && !msgValidarPresupuesto.isEmpty()) {
                itemsPorOrden();
                ordenImpl.notificarValidarPresupuesto(
                        ordenActual,
                        msgValidarPresupuesto);
                PrimeFaces.current().executeScript(
                        ";alertaGeneral('No se cuenta con presupuesto para la compra. Por favor validar con la Gerencia de Costos: " + msgValidarPresupuesto + " ');");
            } else {
                int errorAux = recorreItems(getListaItems());
                switch (ordenActual.getTipo()) {
                    case "PS":
                        if (errorAux == 0) {
                            if (recorreItemsPS(getListaItems())) {
                                return cambiarPagina(idOrden);
                            } else {
                                itemsPorOrden();
                                PrimeFaces.current().executeScript(
                                        ";alertaGeneral('Para continuar con el proceso por favor verifique:  La tarea de los items de la OC/S ');");
                            }
                        } else {
                            itemsPorOrden();
                            switch (errorAux) {
                                case 1:
                                    PrimeFaces.current().executeScript(
                                            ";alertaGeneral('Para solicitar una Orden de C/S todos los Items deben llevar cantidad mayor a cero.');");
                                    break;
                                case 2:
                                    PrimeFaces.current().executeScript(
                                            ";alertaGeneral('Para continuar con el proceso por favor verifique:  La unidad de medida de los items de la OC/S ');");
                                    break;
                                case 3:
                                    PrimeFaces.current().executeScript(
                                            ";alertaGeneral('Para continuar con el proceso por favor verifique:  El precio de los items de la OC/S ');");
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case "AF":
                        if (errorAux == 0) {
                            return cambiarPagina(idOrden);
                        } else {
                            itemsPorOrden();
                            switch (errorAux) {
                                case 1:
                                    PrimeFaces.current().executeScript(
                                            ";alertaGeneral('Para solicitar una Orden de C/S todos los Items deben llevar cantidad mayor a cero.');"
                                    );
                                    break;
                                case 2:
                                    PrimeFaces.current().executeScript(
                                            ";alertaGeneral('Para continuar con el proceso por favor verifique:  La unidad de medida de los items de la OC/S ');"
                                    );
                                    break;
                                case 3:
                                    PrimeFaces.current().executeScript(
                                            ";alertaGeneral('Para continuar con el proceso por favor verifique:  El precio de los items de la OC/S ');"
                                    );
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    default:
                        return Constantes.VACIO;
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return Constantes.VACIO;
    }

    private String cambiarPagina(int idOrden) {
        Env.setContext(sesion.getCtx(), "ORDEN_ID", idOrden);
        return "/vistas/SiaWeb/Orden/SolicitarOrden.xhtml?faces-redirect=true";
    }

    private int recorreItems(List<OrdenDetalleVO> lrd) {
        int todoBien = 0;
        int errorCantidad = 1;
        int errorUnidad = 2;
        int errorPrecio = 3;
        int i = 0;
        for (OrdenDetalleVO ordenDetalleVO : lrd) {
            if (ordenDetalleVO.getArtIdUnidad() == 0) {
                return errorUnidad;
            }
            if (ordenDetalleVO.getCantidad() == 0) {
                return errorCantidad;
            }
            if (!(ordenDetalleVO.getPrecioUnitario() < -0.01) && !(ordenDetalleVO.getPrecioUnitario() > 0.01)) {
                return errorPrecio;
            }
            i++;
        }
        return todoBien;
    }

    private boolean recorreItemsPS(List<OrdenDetalleVO> lrd) {
        boolean conUnidad = true;
        for (OrdenDetalleVO ordenDetalleVO : lrd) {
            if (ordenDetalleVO.getOcTarea() == 0) {
                conUnidad = false;
                break;
            }
        }
        return conUnidad;
    }

    public void seleccionarOrden(int idOrd) {
        PrimeFaces.current().executeScript("activarTab('tabsRecepReq', 0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');");
        llenarDatosOrden(idOrd);
    }

    private void llenarDatosOrden(int oVo) {
        setOrdenActual(ordenImpl.find(oVo));
        getOrdenActual().setLeida(Constantes.BOOLEAN_TRUE);
        //        
        itemsPorOrden();
        // 
        ordenBean.setOrdenActual(ordenActual);
        //
        traerEspecificacionTecnica();
        //
        rechazos();
        //
        notasPorOrden();
        // ets
        traerTablaComparativa();
        ordenEtsPorCategoria();
        etsPorOrdenRequisicion();
        //
        setListaMonedas(monedaBean.getListaMonedas(getOrdenActual().getApCampo().getId()));
    }

    public void agregarContratoOrden() {
        // quita las partidas que no tienen requisicion
        boolean originalConConvenio = ordenActual.isConConvenio();
        List<OrdenDetalleVO> listaPartSinReq = new ArrayList<>();
        TreeSet<Integer> convenioIDs = new TreeSet<>();
        int idConvenioPorAgregar = 0;
        for (OrdenDetalleVO od : listaItems) {
            if ((od.getIdAgrupador() == 0 && od.getIdRequisicionDetalle() > Constantes.CERO)
                    || (od.getIdAgrupador() > 0 && od.getIdRequisicionDetalle() == Constantes.CERO)) {
                if (od.getIdConvenio() > 0) {
                    CvConvenioArticulo convenioArt = cvConvenioArticuloImpl.find(od.getIdConvenio());
                    if (convenioArt != null && convenioArt.getConvenio() != null) {
                        idConvenioPorAgregar = convenioArt.getConvenio().getConvenio() == null ? convenioArt.getConvenio().getId() : convenioArt.getConvenio().getConvenio().getId();
                        convenioIDs.add(idConvenioPorAgregar);
                    }
                } else if (od.getIdConvenio() < 0) {
                    convenioIDs.add(od.getIdConvenio());
                } else {
                    convenioIDs.add(od.getIdConvenio());
                }
            } else {
                listaPartSinReq.add(od);
            }
        }
        Map<Integer, List<OrdenDetalleVO>> mapaOcsDet = new HashMap<>();
        for (int codConvenioID : convenioIDs) {
            List<OrdenDetalleVO> lTemp = new ArrayList<>();
            for (OrdenDetalleVO voDet : listaItems) {
                if (voDet != null && voDet.getIdConvenio() > 0) {
                    CvConvenioArticulo convenioArt = cvConvenioArticuloImpl.find(voDet.getIdConvenio());
                    //Convenio cc = convenioImpl.find(voDet.getIdConvenio());
                    if (convenioArt != null
                            && (codConvenioID == convenioArt.getConvenio().getId() || (convenioArt.getConvenio().getConvenio() != null && codConvenioID == convenioArt.getConvenio().getConvenio().getId()))
                            && ((voDet.getIdAgrupador() == 0 && voDet.getIdRequisicionDetalle() > Constantes.CERO)
                            || (voDet.getIdAgrupador() > 0 && voDet.getIdRequisicionDetalle() == Constantes.CERO))) {
                        lTemp.add(voDet);
                    }
                } else if (codConvenioID == voDet.getIdConvenio()) {
                    lTemp.add(voDet);
                }
            }
            mapaOcsDet.put(codConvenioID, lTemp);
        }
        int contador = 0;
        for (Map.Entry<Integer, List<OrdenDetalleVO>> entry : mapaOcsDet.entrySet()) {
            Integer codCov = entry.getKey();
            List<OrdenDetalleVO> value = entry.getValue();
            //
            if (contador == 0) {
                if (!listaPartSinReq.isEmpty()) {
                    for (OrdenDetalleVO ordenDetalleVO : listaPartSinReq) {
                        ordenImpl.eliminarItem(ordenDetalleImpl.findLazy(ordenDetalleVO.getId()));
                    }
                }
                if (codCov > 0) {
                    ordenConvenioProceso(codCov, value, ordenActual, ordenActual);
                } else {
                    if (codCov < 0) {
                        ordenSinConvenioProceso(value, ordenActual, ordenActual);
                    }
                }
                ordenActual = ordenImpl.find(ordenActual.getId());
            } else {
                Orden o = crearOrden((codCov > 0) || originalConConvenio);
                if (o.isConConvenio()) {
                    ordenConvenioProceso(codCov, value, ordenActual, o);
                } else {
                    //if(codCov < 0){
                    ordenSinConvenioProceso(value, ordenActual, o);
//                    } else {
//                        ordenConvenioProceso(codCov, value, ordenActual, o);
//                    }
                }
            }
            //  
            contador++;
        }
        if (contador > 1) {
            ordenImpl.actualizaMontoOrden(ordenActual, sesion.getUsuarioConectado().getId());
        }
        if (ordenActual.isMultiproyecto()) {
            listaItems = ordenDetalleImpl.itemsPorOrdenMulti(ordenActual.getId());
        } else {
            listaItems = ordenDetalleImpl.itemsPorOrden(ordenActual.getId());
        }

        PrimeFaces.current().executeScript("$(dialogoCambiarContrato).modal('hide');");
        PrimeFaces.current().executeScript("activarTab('tabsRecepReq', 0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');");
        //
    }

    public void cerrarContratoOrden() {
        PrimeFaces.current().executeScript(";$(dialogoCambiarContrato).modal('hide');");
    }

    private void ordenConvenioProceso(int idConvMarco, List<OrdenDetalleVO> detalle, Orden fromOrden, Orden toOrden) {
        Convenio convenioMarco = null;
        if (idConvMarco > 0) {
            convenioMarco = convenioImpl.find(idConvMarco);
        }
        for (OrdenDetalleVO odVo : detalle) {
            if (idConvMarco > 0) {
                ConvenioArticuloVo caVo = cvConvenioArticuloImpl.convenioPorArticuloConvenio(odVo.getIdConvenio(), odVo.getArtID());            //       
                odVo.setPrecioUnitario(caVo.getPrecioUnitario());
                odVo.setImporte(odVo.getCantidad() * caVo.getPrecioUnitario());
                toOrden.setContrato(convenioMarco.getCodigo());
                toOrden.setMoneda(new Moneda(caVo.getIdMoneda()));
                odVo.setConvenio(caVo.getCodigo());
                toOrden.setProveedor(new Proveedor(caVo.getIdProveedor()));
            }

            odVo.setDescuento(0.0);
            odVo.setOrden(toOrden.getId());
            ordenDetalleImpl.actualizarItem(odVo, sesion.getUsuarioConectado().getId(), fromOrden.getId(), toOrden.getId());

        }
        // actualizar orden 
        toOrden.setDetalleProcesado(Constantes.BOOLEAN_TRUE);
        ordenImpl.actualizaMontoOrden(toOrden, sesion.getUsuarioConectado().getId());
        //
    }

    private void ordenSinConvenioProceso(List<OrdenDetalleVO> detalle, Orden fromOrden, Orden toOrden) {
        if (fromOrden != null && toOrden != null) {
            if (fromOrden.getId() != toOrden.getId()) {
                for (OrdenDetalleVO odVo : detalle) {
                    odVo.setOrden(toOrden.getId());
                    odVo.setIdConvenio(0);
                    odVo.setConvenio("");
                    ordenDetalleImpl.actualizarItem(odVo, sesion.getUsuarioConectado().getId(), fromOrden.getId(), toOrden.getId());
                }
                // actualizar orden 
                toOrden.setDetalleProcesado(Constantes.BOOLEAN_TRUE);
            } else {
                toOrden.setConConvenio(false);
            }
        }
        ordenImpl.actualizaMontoOrden(toOrden, sesion.getUsuarioConectado().getId());
    }

    public void mostrarContratoItem() {
        for (OrdenDetalleVO listaItem : listaItems) {
            listaItem.setConvenios(new ArrayList<>());
            if ((listaItem.getIdAgrupador() == 0 && listaItem.getIdRequisicionDetalle() > Constantes.CERO)
                    || (listaItem.getIdAgrupador() > 0 && listaItem.getIdRequisicionDetalle() == Constantes.CERO)) {
                listaItem.setConvenios(cvConvenioArticuloImpl.convenioPorArticulo(listaItem.getArtID(), ordenActual.getApCampo().getId()));
                if (!ordenActual.getContrato().equals(Constantes.OCS_SIN_CONTRATO)) {
                    Convenio c = convenioImpl.buscarContratoPorNumero(ordenActual.getContrato());
                    if (c != null) {
                        listaItem.setConvenio(c.getCodigo());
                    }
                }
            }
        }
        PrimeFaces.current().executeScript("$(dialogoCambiarContrato).modal('show');");
    }

    public void seleccionaMoneda(AjaxBehaviorEvent event) {
        if (idMoneda > 0) {
            ordenActual.setMoneda(new Moneda(idMoneda));
            //agregar moneda orden
            ordenImpl.editarOrden(ordenActual);
            // agregar moneda a los items
//            ordenDetalleImpl.agragarMonedaItems(listaItems, ordenActual.getMoneda().getId(), sesion.getUsuarioConectado().getId());
            //ordenActual.setTotal(0.0);
            setOrdenActual(ordenImpl.find(this.getOrdenActual().getId()));
        } else {
            ordenActual.setMoneda(new Moneda());
        }
        PrimeFaces.current().executeScript("ocultarDiv('selMoneda');");
    }

    private Orden crearOrden(boolean conConvenio) {
        //- - - -
        Orden orden = new Orden();
        //Agraga el campo a la OC/S
        orden.setApCampo(ordenActual.getRequisicion().getApCampo());
        orden.setRequisicion(ordenActual.getRequisicion());
        orden.setGerencia(ordenActual.getGerencia());
        orden.setResponsableGerencia(ordenActual.getResponsableGerencia());
        orden.setProyectoOt(ordenActual.getProyectoOt());
        orden.setCompania(ordenActual.getCompania());
        orden.setContactoCompania(ordenActual.getCompania().getRecepcionFactura());
        orden.setAnalista(ordenActual.getAnalista());
        orden.setFecha(new Date());
        orden.setDestino(ordenActual.getDestino());
        orden.setReferencia(ordenActual.getReferencia());
        orden.setContrato(Constantes.OCS_SIN_CONTRATO);
        orden.setCuentaContableProyectoOt(ordenActual.getProyectoOt().getCuentaContable());
        orden.setGenero(sesion.getUsuarioConectado());
        orden.setFechaGenero(new Date());
        orden.setHoraGenero(new Date());
        orden.setEliminado(Constantes.NO_ELIMINADO);
        orden.setOcUnidadCosto(ordenActual.getOcUnidadCosto());
        orden.setTipo(ordenActual.getTipo());
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        orden.setFechaEntrega(new Date());
        orden.setMultiproyecto(ordenActual.isMultiproyecto());
        orden.setTotal(0.0);
        //- - - - gerente de compras - - -
        //gerencia de compras        
        orden.setGerenteCompras(ordenActual.getGerenteCompras());
        orden.setMoneda(ordenActual.getMoneda());
        //
        orden.setConConvenio(conConvenio);
        //- - - - - crear la orden - - - -
        return ordenImpl.createReturnOrden(orden);
    }

    public void rechazos() {
        try {
            setListaRechazos(ordenSiMovimientoImpl.getMovimientsobyOrden(getOrdenActual().getId()));
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public List<NoticiaVO> notasPorOrden() {
        try {
            if (ordenActual != null) {
                NotaOrdenBean notaOrdenBean = (NotaOrdenBean) FacesUtilsBean.getManagedBean("notaOrdenBean");
                return notaOrdenBean.traerNoticiaPorOrden(getOrdenActual().getId());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "notasPorOrden  . . ." + e.getMessage(), e);
        }
        return null;
    }

    public void actualizarItem(int id) {
        try {
            if (getOrdenActual() != null && getOrdenActual().isMultiproyecto()) {
                OrdenDetalleVO odvo = listaItems.get(id);
                odvo.setId(this.ordenDetalleImpl.itemsPorOrdenMultiID(this.getOrdenActual().getId(), odvo.getIdAgrupador()));
                if (odvo.getId() > 0) {
                    setItemActual(ordenDetalleImpl.find(odvo.getId()));
                    BigDecimal cant = new BigDecimal(odvo.getCantidad());
                    BigDecimal c = cant.setScale(3, RoundingMode.FLOOR);
                    getItemActual().setCantidad(c.doubleValue());
                    this.setNombreTareaMulti(odvo.getNombreTarea());
                    actualizarItemService();
                }
            } else {
                OrdenDetalleVO odvo = listaItems.get(id);
                setItemActual(ordenDetalleImpl.find(odvo.getId()));
                actualizarItemService();
            }
        } catch (NumberFormatException e) {
            UtilLog4j.log.fatal(this, e);
        }
    }

    public void historyItem(int idInv, int idCampo) {
        try {
            if (getOrdenActual() != null) {
                if (idInv > 0 && idCampo > 0) {
                    this.setHistoricoVentas(this.ordenDetalleImpl.historicoDetalleOrden(idInv, idCampo));
                } else {
                    this.setHistoricoVentas(new ArrayList<>());
                }
            }
            PrimeFaces.current().executeScript("$(dialogoHistorialCompra).modal('show');");
        } catch (NumberFormatException e) {
            UtilLog4j.log.fatal(this, e);
        }
    }

    public void cerrarHistoryItem() {
        this.setHistoricoVentas(new ArrayList<>());
        PrimeFaces.current().executeScript(";$(dialogoHistorialCompra).modal('hide');");
    }

    private void actualizarItemService() {
        try {
            //getItemActual().setMoneda(ordenActual.getMoneda());
            //
            operacionItem = UPDATE_OPERATION;
            listaMapa = new HashMap<>();
            if (ordenActual.getApCampo().getTipo().equals("C")) {
//                if (TipoRequisicion.PS.equals(getItemActual().getOrden().getTipo())) {
                setIdTarea(getItemActual().getOcTarea().getOcCodigoTarea().getId());
                llenarActvidad();
                llenarProyecto();
                llenarTipoTarea();
                llenarTarea();
                llenarSubTarea();
//                } else {
//                    if(itemActual.getOcActividadPetrolera() == null){
//                        itemActual.setOcActividadPetrolera(new OcActividadPetrolera());
//                    }
//                    llenarActvidad();
//                    setIdTarea(0);
//                    llenarProyectoActivoFijo();
//                    itemActual.setOcSubTarea(null);
//                    itemActual.setOcTarea(null);
//                    itemActual.setOcUnidadCosto(null);
//                }
            } else { // Campos no contractuales
                if (TipoRequisicion.PS.toString().equals(getOrdenActual().getTipo())) {
                    itemActual.setOcActividadPetrolera(null);
                    itemActual.setOcSubTarea(null);
                    setIdTarea(itemActual.getOcTarea().getId());
                    if (itemActual.getProyectoOt() == null) {
                        itemActual.setProyectoOt(new ProyectoOt(ordenActual.getProyectoOt().getId()));
                        itemActual.setOcUnidadCosto(new OcUnidadCosto(ordenActual.getOcUnidadCosto().getId()));
                    }
                    llenarProyecto();
                    llenarTipoTarea();
                    llenarTarea();
                } else {
                    //llenarActvidad();
                    llenarProyectoActivoFijo();
                }

            }
            if (getItemActual().getInvArticulo() != null
                    && getItemActual().getInvArticulo().getSiCategoria() != null
                    && getItemActual().getInvArticulo().getSiCategoria().getId() > 0) {
                iniciarCatSel();
                categoriaVo = new CategoriaVo();
                categoriaVo.setId(getItemActual().getInvArticulo().getSiCategoria().getId());
                this.llenarCategoria(null);
            }

            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoModItemOCS);");
        } catch (NumberFormatException ex) {
            System.out.println("EEEERRRROOOORRR : " + ex.getMessage());
            UtilLog4j.log.fatal(this, ex);
        }
        this.setListaMonedas(monedaBean.getListaMonedas(ordenActual != null && ordenActual.getApCampo() != null ? ordenActual.getApCampo().getId() : 0));
    }

    private void llenarProyectoActivoFijo() {
        listaMapa.put("proyecto", new ArrayList<>());
        listaMapa.put("proyecto", ocGerenciaProyectoImpl.traerProyectoOtItems(ordenActual.getGerencia().getId(), ordenActual.getApCampo().getId()));
    }

    public void crearItem() {
        try {
            setItemActual(new OrdenDetalle());
            getItemActual().setOrden(getOrdenActual());
            getItemActual().setInvArticulo(new InvArticulo());
            getItemActual().getInvArticulo().setId(0);
            getItemActual().setOcActividadPetrolera(new OcActividadPetrolera());
            getItemActual().setOcSubTarea(new OcSubTarea());
            getItemActual().setProyectoOt(new ProyectoOt());
            getItemActual().setOcUnidadCosto(new OcUnidadCosto());
            getItemActual().setObservaciones("");
            getItemActual().setDescuento(0.0);
            if (TipoRequisicion.PS.toString().equals(getOrdenActual().getTipo())) {
                getItemActual().setOcUnidadCosto(getOrdenActual().getOcUnidadCosto());
            }
            getItemActual().setProyectoOt(getOrdenActual().getProyectoOt());
            //itemActual.getProyectoOt().setId(ordenActual.getProyectoOt().getId());
            setArticuloTx("");
            setIdTarea(0);
            setOperacionItem(CREATE_OPERATION);
            // @articulos
            categorias = invArticuloCampoImpl.traerCategoriaArticulo();

            setArticulosFrecuentes(articuloImpl.articulosFrecuentesOrden(sesion.getUsuarioConectado().getId(),
                    getOrdenActual().getApCampo().getId()));
            if (getArticulosFrecuentes() != null && getArticulosFrecuentes().size() > 10) {
                setArticulosFrecuentes(getArticulosFrecuentes().subList(0, 10));
            }
            iniciarCatSel();
            setArticulosResultadoBqda(articuloImpl.obtenerArticulos("", sesion.getUsuarioConectado().getApCampo().getId(), 0, null));
            setCategoriasTxt();
            setListaMonedas(monedaBean.getListaMonedas(getOrdenActual() != null && getOrdenActual().getApCampo() != null ? getOrdenActual().getApCampo().getId() : 0));

            //
            setListaMapa(new HashMap());
            if (getOrdenActual().getApCampo().getTipo().equals("N")) {
                getItemActual().getOcActividadPetrolera().setId(Constantes.CERO);
                getItemActual().getOcSubTarea().setId(Constantes.CERO);
                llenarTarea();
            } else {
                llenarActvidad();
            }
            PrimeFaces.current().executeScript("abrirDialogoModal(dialogoModItemOCS);");
        } catch (Exception ex) {
            System.out.println("Exc:" + ex);
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public void agregarItemConvenio() {
        setItemActual(new OrdenDetalle());
        listaConvenioArticuloVo = cvConvenioArticuloImpl.traerCodigoConvenioArticulo(ordenActual.getContrato(), ordenActual.getApCampo().getId());
        //
        PrimeFaces.current().executeScript(";$(dialogoItemConvenio).modal('show');");
    }

    public void seleccionarArticuloConvenio(SelectEvent<ConvenioArticuloVo> event) {
        setItemActual(new OrdenDetalle());
        ConvenioArticuloVo caVo = (ConvenioArticuloVo) event.getObject();
        //
        itemActual.setPrecioUnitario(caVo.getPrecioUnitario());
        itemActual.setDescuento(0.0);
        itemActual.setInvArticulo(new InvArticulo(caVo.getIdArticulo()));
        itemActual.getInvArticulo().setNombre(caVo.getNombre());
        itemActual.getInvArticulo().setCodigoInt(caVo.getCodigoInt());
        itemActual.getInvArticulo().setUnidad(new SiUnidad(caVo.getUnidadId()));
        itemActual.getInvArticulo().getUnidad().setNombre(caVo.getUnidadNombre());
//        itemActual.setMoneda(new Moneda(caVo.getIdMoneda()));
//        itemActual.getMoneda().setNombre(caVo.getMoneda());
        itemActual.setOrden(ordenActual);
        itemActual.setProyectoOt(ordenActual.getProyectoOt());
        itemActual.setOcUnidadCosto(ordenActual.getOcUnidadCosto());
        itemActual.setOcActividadPetrolera(new OcActividadPetrolera());
        itemActual.setSiUnidad(new SiUnidad(caVo.getUnidadId()));
        itemActual.setOcSubTarea(new OcSubTarea());
        itemActual.setRecibido(Constantes.BOOLEAN_FALSE);
        itemActual.setConvenio(caVo.getIdConvenio());
        itemActual.setConvenioCodigo(caVo.getConvenio());
        setListaMapa(new HashMap<>());
        if (getOrdenActual().getApCampo().getTipo().equals("N")) {
            itemActual.getOcActividadPetrolera().setId(Constantes.CERO);
            itemActual.getOcSubTarea().setId(Constantes.CERO);
            llenarTarea();
        } else {
            llenarActvidad();
        }
        itemActual.setGenero(sesion.getUsuarioConectado());
        itemActual.setFechaGenero(new Date());
        itemActual.setHoraGenero(new Date());
        itemActual.setEliminado(Constantes.BOOLEAN_FALSE);
    }

    public void completarAgregarItemConvenio() {
        //
        itemActual.setOcActividadPetrolera(itemActual.getOcActividadPetrolera().getId() > 0 ? new OcActividadPetrolera(itemActual.getOcActividadPetrolera().getId()) : null);
        itemActual.setOcSubTarea(itemActual.getOcSubTarea().getId() > 0 ? new OcSubTarea(itemActual.getOcSubTarea().getId()) : null);
        //
        itemActual.setImporte(itemActual.getPrecioUnitario() * itemActual.getCantidad());
        itemActual.setOcTarea(new OcTarea(idTarea));
        itemActual.setDescuento(0.0);
        ordenDetalleImpl.crear(itemActual);
        //
        ordenImpl.actualizaMontoOrden(ordenActual, sesion.getUsuarioConectado().getId());
        listaItems = ordenDetalleImpl.itemsPorOrden(ordenActual.getId());
        itemActual = null;
        PrimeFaces.current().executeScript(";$(dialogoItemConvenio).modal('hide');");
    }

    public void cerrarAgregarItemConvenio() {
        //
        itemActual = null;
        PrimeFaces.current().executeScript(";$(dialogoItemConvenio).modal('hide');");
    }

    private void llenarActvidad() {
        getListaMapa().put("actividad", ocActividadPetroleraImpl.getActividadesItems());
    }

    private void llenarProyecto() {
        List<ProyectoOtVo> lt = ocTareaImpl.traerProyectoOtPorGerencia(getOrdenActual().getGerencia().getId(), getItemActual().getOrden().getApCampo().getId());
        List<SelectItem> ls = new ArrayList<>();
        for (ProyectoOtVo ocTareaVo : lt) {
            ls.add(new SelectItem(ocTareaVo.getId(), ocTareaVo.getNombre()));
            getListaMapa().put("proyecto", ls);
        }
    }

    public void seleccionarActividad(AjaxBehaviorEvent event) {
        if (event != null) {
            if (getOrdenActual().getTipo().equals(TipoRequisicion.PS.toString())) {
                llenarProyecto();
                getItemActual().getProyectoOt().setId(Constantes.CERO);
                getItemActual().getOcUnidadCosto().setId(Constantes.CERO);
                setIdTarea(Constantes.CERO);
                getItemActual().getOcSubTarea().setId(Constantes.CERO);
            } else {
                llenarProyectoActivoFijo();
            }
        }
    }

    public void seleccionarProyecto(AjaxBehaviorEvent event) {
        if (event != null) {
            if (getOrdenActual().getTipo().toString().equals(TipoRequisicion.PS.toString())) {
                if (getItemActual().getProyectoOt().getId() > Constantes.CERO) {
                    llenarTipoTarea();
                    getItemActual().getOcUnidadCosto().setId(Constantes.CERO);
                }
                getItemActual().getOcUnidadCosto().setId(Constantes.CERO);
                setIdTarea(Constantes.CERO);
            }
        }
    }

    public void seleccionarTipoTarea(AjaxBehaviorEvent event) {
        if (event != null) {
            llenarTarea();
        }
    }

    public void seleccionarTarea(AjaxBehaviorEvent event) {
        if (event != null) {
            if (getOrdenActual().getApCampo().getTipo().equals("C")) {
                llenarSubTarea();
            } else {
                getItemActual().setOcUnidadCosto(getOrdenActual().getOcUnidadCosto());
            }
        }
    }

    private void llenarTipoTarea() {
        if (getItemActual().getOcActividadPetrolera() != null && getItemActual().getOcActividadPetrolera().getId() > 0) {
            List<OcTareaVo> lt = ocTareaImpl.traerUnidadCostoPorGerenciaProyectoOT(getOrdenActual().getGerencia().getId(), getItemActual().getProyectoOt().getId(), getItemActual().getOcActividadPetrolera().getId());
            List<SelectItem> ls = new ArrayList<>();
            for (OcTareaVo ocTareaVo : lt) {
                ls.add(new SelectItem(ocTareaVo.getIdUnidadCosto(), ocTareaVo.getUnidadCosto()));
            }
            getListaMapa().put("tipoTarea", ls);
        }
    }

    private void llenarTarea() {
        listaTarea();
    }

    public void listaTarea() {
        try {
            List<SelectItem> li = null;
            if (getOrdenActual() != null) {
                int idGer = 0;
                li = new ArrayList<>();
                if (ordenActual.getGerencia().getAbrev().contains(";")) {
                    String[] cad = ordenActual.getGerencia().getAbrev().split(";");
                    UtilLog4j.log.debug(this, "Gerencia: " + ordenActual.getGerencia().getNombre() + "Cadena :   " + cad[0]);
                    GerenciaVo ga = gerenciaImpl.traerGerenciaVOAbreviatura(cad[0]);
                    idGer = ga.getId();
                } else {
                    idGer = ordenActual.getGerencia().getId();
                }
                listaMapa.put("tarea", ocTareaImpl.traerNombrePorProyectoOtGerenciaUnidadCostoItems(idGer, itemActual.getProyectoOt().getId(), itemActual.getOcUnidadCosto().getId(), null, ordenActual.getApCampo().getId(), ordenActual.getApCampo().getTipo()));
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Ocurrio un error al traer las unidades # # # # # " + e.getMessage());
        }
    }

    private void llenarSubTarea() {
//        getListaMapa().put("subTarea", ocSubTareaImpl.traerLstCentoCostosItems(getIdTarea(), ""));

        if (getItemActual().getProyectoOt().getId() > 0
                && getItemActual().getOcActividadPetrolera().getId() > 0
                && getItemActual().getOcUnidadCosto().getId() > 0
                && getItemActual().getOcCodigoTarea().getId() > 0) {
            getListaMapa().put("subTarea", ocPresupuestoDetalleImpl.getSubTareasItems(
                    getItemActual().getOcPresupuesto().getId(),
                    getItemActual().getOcActividadPetrolera().getId(),
                    getOrdenActual().getApCampo().getId(),
                    getItemActual().getProyectoOt().getId(),
                    getItemActual().getOcUnidadCosto().getId(),
                    getItemActual().getOcCodigoTarea().getId(),
                    getItemActual().getAnioPresupuesto(),
                    getItemActual().getMesPresupuesto(),
                    false));
        } else if (getOrdenActual().isMultiproyecto()) {
            getListaMapa().put("subTarea", ocPresupuestoDetalleImpl.getSubTareasItems(
                    getItemActual().getOcPresupuesto().getId(),
                    getItemActual().getOcActividadPetrolera().getId(),
                    getOrdenActual().getApCampo().getId(),
                    0,
                    getItemActual().getOcUnidadCosto().getId(),
                    getItemActual().getOcCodigoTarea().getId(),
                    getItemActual().getAnioPresupuesto(),
                    getItemActual().getMesPresupuesto(),
                    false));
        } else {
            getListaMapa().put("subTarea", new ArrayList<>());
        }

    }

    public void eliminarItem(int id) {
        try {
            OrdenDetalleVO odvo = getListaItems().get(id);
            setItemActual(ordenDetalleImpl.find(odvo.getId()));
            ordenImpl.eliminarItem(getItemActual());
            setOrdenActual(ordenImpl.find(getOrdenActual().getId()));
            FacesUtilsBean.addInfoMessage("Se eliminó correctamente el Ítem");
            itemsPorOrden();
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public void completarActualizacionItem() {
        try {
//            if (ordenActual.getMoneda() != null && ordenActual.getMoneda().getId() > Constantes.CERO) {
//                itemActual.setMoneda(ordenActual.getMoneda());
//            } else {
//                itemActual.setMoneda(null);
//            }
            //
            if (this.getOrdenActual().isMultiproyecto()) {
                completarActualizacionMultiItems();
            } else {
                completarActualizacionSingleItem();
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }

    public void completarActualizacionMultiItems() {
        try {

            if (this.operacionItem.equals(UPDATE_OPERATION)) {
                ordenImpl.actualizarMultiItems(getOrdenActual().getId(),
                        this.getItemActual(),
                        sesion.getUsuarioConectado().getId(),
                        ordenActual.getMoneda().getNombre(),
                        ordenActual.getApCampo().getCompania().getRfc());
                // setOrdenActual(ordenImpl.find(getOrdenActual().getId()));
                FacesUtilsBean.addInfoMessage("Se actualizó correctamente el Ítem");
            }
            //Actualizar
            itemsPorOrden();
            setOrdenActual(ordenImpl.find(getOrdenActual().getId()));
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoModItemOCS);");

        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }

    public void completarActualizacionSingleItem() {
        try {
            boolean continuar = true;
//
            if (ordenActual.getApCampo().getTipo().equals("C")) {
                if (ordenActual.getTipo().equals(TipoRequisicion.PS.name())) {
                    if (itemActual.getOcActividadPetrolera().getId() == Constantes.CERO
                            || itemActual.getProyectoOt().getId() == Constantes.CERO
                            || itemActual.getOcUnidadCosto().getId() == Constantes.CERO
                            || itemActual.getOcSubTarea().getId() == Constantes.CERO) {
                        continuar = false;
                        FacesUtilsBean.addErrorMessage("Seleccione valores para todos los campos del Ítem . . . ");
                    } else {
                        itemActual.setOcActividadPetrolera(new OcActividadPetrolera(itemActual.getOcActividadPetrolera().getId()));
                        itemActual.setProyectoOt(new ProyectoOt(itemActual.getProyectoOt().getId()));
                        itemActual.setOcUnidadCosto(new OcUnidadCosto(itemActual.getOcUnidadCosto().getId()));
                        itemActual.setOcSubTarea(new OcSubTarea(itemActual.getOcSubTarea().getId()));
                    }
                } else {
                    if (itemActual.getOcActividadPetrolera().getId() == Constantes.CERO
                            || itemActual.getProyectoOt().getId() == Constantes.CERO) {
                        FacesUtilsBean.addErrorMessage("Seleccione valores para todos los campos del Ítem . . . ");
                        continuar = false;
                    } else {
                        itemActual.setOcActividadPetrolera(new OcActividadPetrolera(itemActual.getOcActividadPetrolera().getId()));
                        itemActual.setProyectoOt(new ProyectoOt(itemActual.getProyectoOt().getId()));
                    }
                }

                OcSubtareaVO ocstarea = ocTareaImpl.traerIDTarea(
                        itemActual.getProyectoOt().getId(),
                        itemActual.getOcUnidadCosto().getId(),
                        this.getIdTarea(),
                        itemActual.getOcActividadPetrolera().getId(),
                        itemActual.getOcCodigoSubtarea().getId());
                if (ocstarea != null) {
                    if (ocstarea.getIdTarea() > 0) {
                        itemActual.setOcTarea(new OcTarea(ocstarea.getIdTarea()));
                        setIdTarea(ocstarea.getIdTarea());
                    }

                    if (ocstarea.getId() > 0) {
                        itemActual.setOcSubTarea(new OcSubTarea(ocstarea.getId()));
                    }
                }

            }
            if (continuar) {
                if (this.operacionItem.equals(UPDATE_OPERATION)) {
                    ordenImpl.actualizarItem(itemActual, sesion.getUsuarioConectado().getId(), getIdTarea());
                    setOrdenActual(ordenImpl.find(getOrdenActual().getId()));
                    FacesUtilsBean.addInfoMessage("Se actualizó correctamente el Ítem");
                } else {
                    if (getItemActual() != null) {
                        if (getItemActual().getRequisicionDetalle() == null) {
                            UtilLog4j.log.info(this, "LA REQUISICION DETALLE ES NULA");
                        }
                        if (getItemActual().getOrden() == null) {
                            UtilLog4j.log.info(this, "LA ORDEN DEL ITEMACTUAL ES NULA");
                        } else {
                            UtilLog4j.log.info(this, "LA ORDEN DEL ITEMACTUAL NO ES NULA");
                            UtilLog4j.log.info(this, "IdItemActual " + getItemActual().getOrden().getId());
                        }
                        UtilLog4j.log.info(this, "LA ORDEN DEL ENTITY--" + getOrdenActual().getId());
                        //UtilLog4j.log.info(this, "MONEDA--"+getItemActual().getMoneda().getId());
                    }
                    //actualiza monto orden
                    //
                    ordenImpl.crearItem(itemActual, sesion.getUsuarioConectado().getId(), getIdTarea());

                    //
                    FacesUtilsBean.addInfoMessage("Se creó correctamente el Ítem");
                } // Termina crear item . . .
                //Actualizar
                itemsPorOrden();
                setOrdenActual(ordenImpl.find(getOrdenActual().getId()));
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoModItemOCS);");
                //
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }

    public void pasarItemsOrden() {
        try {
            for (OrdenDetalleVO ordenDetalleVO : getOrdenVO().getDetalleOrden()) {
                if (ordenDetalleVO.isSelected()) {
                    ordenDetalleImpl.guardarItem(ordenDetalleVO, getOrdenActual().getId(), sesion.getUsuarioConectado().getId());
                }
            }
            // actualizar los montos
            ordenImpl.actualizaMontoOrden(getOrdenActual(), sesion.getUsuarioConectado().getId());
            ///Cerrar el dialogo  y limpiar todo;
            limpiar();
            itemsPorOrden();
            //cierra
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDetalleOCS);");
            //PrimeFaces.current().executeScript( ";dialogoOK('dialogOK', 'dialogoDetalleOCS');");
        } catch (Exception e) {
            limpiar();
            UtilLog4j.log.info(this, e.getMessage() + "# # # # # # # # # " + e);
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDetalleOCS);");
        }

    }

    public void agregarCantidad(int index) {
        OrdenDetalleVO odvo = listaItems.get(index);

        odvo.setImporte(odvo.getCantidad() * odvo.getPrecioUnitario());
        //
        ordenDetalleImpl.actualizarItem(odvo, sesion.getUsuarioConectado().getId(), ordenActual.getId(), ordenActual.getId());
        //
        ordenImpl.actualizaMontoOrden(ordenActual, sesion.getUsuarioConectado().getId());

        if (ordenActual.isMultiproyecto()) {
            listaItems = ordenDetalleImpl.itemsPorOrdenMulti(ordenActual.getId());
        } else {
            listaItems = ordenDetalleImpl.itemsPorOrden(ordenActual.getId());
        }
        //
        listaItems.get(index).setCantidad(odvo.getCantidad());

        String divMostrar = "dvCantidad" + odvo.getId();
        String divOcultar = "dvCantidadMod" + odvo.getId();
        PrimeFaces.current().executeScript(";ocultarDiv('" + divOcultar + "'); mostrarDiv('" + divMostrar + "');");

    }

    public void agregarDescuentoItem(int indice) {
        OrdenDetalleVO odvo = listaItems.get(indice);
        if (odvo.getIdAgrupador() == Constantes.CERO) {
            //
            double total = ordenActual.getTotal() - (odvo.getCantidad() * odvo.getPrecioUnitario());
            //
            ordenDetalleImpl.actualizarItem(odvo, sesion.getUsuarioConectado().getId(), ordenActual.getId(), ordenActual.getId());
            //
            ordenActual.setTotal(total + (odvo.getCantidad() * odvo.getPrecioUnitario()) - odvo.getDescuento());
            //
            ordenImpl.actualizaMontoOrden(ordenActual, sesion.getUsuarioConectado().getId());
            //
            listaItems = ordenDetalleImpl.itemsPorOrden(ordenActual.getId());
        } else {
            ordenDetalleImpl.actualizarItem(odvo, sesion.getUsuarioConectado().getId(), ordenActual.getId(), ordenActual.getId());
            //
            ordenImpl.actualizaMontoOrden(ordenActual, sesion.getUsuarioConectado().getId());
            //
            listaItems = ordenDetalleImpl.itemsPorOrdenMulti(ordenActual.getId());
        }

        String divMostrar = "divDescOp" + odvo.getId();
        String divOcultar = "divDesc" + odvo.getId();
        PrimeFaces.current().executeScript(";ocultarDiv('" + divMostrar + "'); mostrarDiv('" + divOcultar + "');");
    }

    public void limpiar() {
        setReferencia(Constantes.VACIO);
        setOrdenVO(null);
    }

    public void seleccionarArtFrecuente(SelectEvent<ArticuloVO> event) {
        try {
            ArticuloVO artVO = (ArticuloVO) event.getObject();
            cambiarArticulo(artVO);
            //PrimeFaces.current().ajax().update("PF('pnlArt').unselect(0));");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void seleccionarResultadoBA(SelectEvent<ArticuloVO> event) {
        try {
            ArticuloVO artItem = (ArticuloVO) event.getObject();
            cambiarArticulo(artItem);
            // PrimeFaces.current().ajax().update("PF('pnlArt').unselect(1));");
        } catch (Exception e) {
            System.out.println("Exc: sele art." + e);
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void cambiarArticulo(ArticuloVO artVo) {
        try {
            if (artVo != null) {
                setArticuloID(artVo.getId());
                getItemActual().setInvArticulo(articuloImpl.find(artVo.getId()));
                setArticuloTx("");
                categoriasSeleccionadas = new ArrayList<>();
                iniciarCatSel();
            }
        } catch (Exception e) {
            System.out.println("Exc: cambiarArticulo art." + e);
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    /**
     * @return the categoriaVo
     */
    public CategoriaVo getCategoriaVo() {
        return categoriaVo;
    }

    /**
     * @param categoriaVo the categoriaVo to set
     */
    public void setCategoriaVo(CategoriaVo categoriaVo) {
        this.categoriaVo = categoriaVo;
    }

    public void seleccionarCategoriaCabecera(int id) {
        traerSubcategoria(id);
        setCategoriasTxt();
    }

    public void traerArticulosItemsListener(String event) {
        String cadena = event;
        if ((cadena != null && !cadena.isEmpty() && cadena.length() > 2)
                || (cadena == null || cadena.isEmpty())) {
            setArticulosResultadoBqda(traerArticulosItems(cadena));
        }
        PrimeFaces.current().executeScript(";marcarBusquedaOrden();");
    }

    private String filtrosCadena(String cadena) {
        String[] output = cadena.split("\\%");
        StringBuilder cadenaNombre = new StringBuilder("and ((");
        StringBuilder cadenaCodigo = new StringBuilder(") or (");
        String and = "";
        for (String s : output) {
            cadenaNombre.append(and).append("upper(a.NOMBRE) like upper('%").append(s).append("%') ");
            cadenaCodigo.append(and).append("upper(a.CODIGO_INT) like upper('%").append(s).append("%') ");
            and = " and ";
        }
        return cadenaNombre.toString() + cadenaCodigo.toString() + "))";
    }

    private List<ArticuloVO> traerArticulosItems(String cadena) {
        List<ArticuloVO> list;
        try {
            cadena = filtrosCadena(cadena.replace(" ", "%"));
            list = articuloImpl.obtenerArticulos(cadena, sesion.getUsuarioConectado().getApCampo().getId(),
                    Constantes.CERO,
                    getCodigos(getCategoriasSeleccionadas().size() > 1
                            ? getCategoriasSeleccionadas().subList(1, getCategoriasSeleccionadas().size())
                            : new ArrayList<>()));
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

    private void traerSubcategoria(int indice) {
        try {
            CategoriaVo c = categoriasSeleccionadas.get(indice);
            if (indice == 0) {
                categorias = invArticuloCampoImpl.traerCategoriaArticulo();
                iniciarCatSel();
            } else {
                setCategoriaVo(siRelCategoriaImpl.traerCategoriaPorCategoria(c.getId(), getSoloCodigos(getCategoriasSeleccionadas().subList(0, indice)), sesion.getUsuarioConectado().getApCampo().getId()));
                if (c.getId() != getCategoriaVo().getId()) {
                    categoriasSeleccionadas.add(getCategoriaVo());// limpiar lista seleccionadas
                }
                if ((indice + 1) < categoriasSeleccionadas.size()) {
                    for (int i = (categoriasSeleccionadas.size() - 1); (indice + 1) < categoriasSeleccionadas.size(); i--) {
                        categoriasSeleccionadas.remove(i);
                    }
                }
            }
            setItemActual(new OrdenDetalle());
            getItemActual().setOrden(getOrdenActual());
            //
            getItemActual().setInvArticulo(new InvArticulo());
            getItemActual().setObservaciones("");
            setArticuloTx("");

        } catch (Exception e) {
            System.out.println("Exc: traer subcategorías: " + e);
        }
    }

    private void iniciarCatSel() {
        setCategoriasSeleccionadas(new ArrayList<>());
        CategoriaVo c = new CategoriaVo();
        c.setNombre("Pricipales");
        c.setId(Constantes.CERO);
        categoriasSeleccionadas.add(c);
    }

    /**
     * @param event
     */
    public void buscarOrdenPorConsecutivoBloque() {
        setOrdenVO(ordenImpl.buscarOrdenPorUsuarioInvArticulo(getReferencia().toUpperCase(), sesion.getUsuarioConectado().getApCampo().getId(), sesion.getUsuarioConectado().getId(), true));
        if (getOrdenVO() == null) {
            PrimeFaces.current().executeScript(";alertaGeneral('No se encontró la OC/S o la OC/S no utiliza el catálogo de artículos del SIA.');");
        }
    }

    /**
     * @return the categoriasSeleccionadas
     */
    public List<CategoriaVo> getCategoriasSeleccionadas() {
        return categoriasSeleccionadas;
    }

    /**
     * @param categoriasSeleccionadas the categoriasSeleccionadas to set
     */
    public void setCategoriasSeleccionadas(List<CategoriaVo> categoriasSeleccionadas) {
        this.categoriasSeleccionadas = categoriasSeleccionadas;
    }

    /**
     * @return the articulosFrecuentes
     */
    public List<ArticuloVO> getArticulosFrecuentes() {
        return articulosFrecuentes;
    }

    /**
     * @param articulosFrecuentes the articulosFrecuentes to set
     */
    public void setArticulosFrecuentes(List<ArticuloVO> articulosFrecuentes) {
        this.articulosFrecuentes = articulosFrecuentes;
    }

    /**
     * @return the articulosResultadoBqda
     */
    public List<ArticuloVO> getArticulosResultadoBqda() {
        return articulosResultadoBqda;
    }

    /**
     * @param articulosResultadoBqda the articulosResultadoBqda to set
     */
    public void setArticulosResultadoBqda(List<ArticuloVO> articulosResultadoBqda) {
        this.articulosResultadoBqda = articulosResultadoBqda;
    }

    /**
     * @return the listaMonedas
     */
    public List<SelectItem> getListaMonedas() {
        return listaMonedas;
    }

    /**
     * @param listaMonedas the listaMonedas to set
     */
    public void setListaMonedas(List<SelectItem> listaMonedas) {
        this.listaMonedas = listaMonedas;
    }

    /**
     * @return the ordenActual
     */
    public Orden getOrdenActual() {
        return ordenActual;
    }

    /**
     * @param ordenActual the ordenActual to set
     */
    public void setOrdenActual(Orden ordenActual) {
        this.ordenActual = ordenActual;
    }

    /**
     * @return the listaItems
     */
    public List<OrdenDetalleVO> getListaItems() {
        return listaItems;
    }

    /**
     * @param listaItems the listaItems to set
     */
    public void setListaItems(List<OrdenDetalleVO> listaItems) {
        this.listaItems = listaItems;
    }

    /**
     * @return the listaMapa
     */
    public Map<String, List<SelectItem>> getListaMapa() {
        return listaMapa;
    }

    /**
     * @param listaMapa the listaMapa to set
     */
    public void setListaMapa(Map<String, List<SelectItem>> listaMapa) {
        this.listaMapa = listaMapa;
    }

    /**
     * @return the listaContactos
     */
    public List<ContactoProveedorVO> getListaContactos() {
        return listaContactos;
    }

    /**
     * @param listaContactos the listaContactos to set
     */
    public void setListaContactos(List<ContactoProveedorVO> listaContactos) {
        this.listaContactos = listaContactos;
    }

    /**
     * @return the articulo
     */
    public InvArticulo getArticulo() {
        return articulo;
    }

    /**
     * @param articulo the articulo to set
     */
    public void setArticulo(InvArticulo articulo) {
        this.articulo = articulo;
    }

    /**
     * @return the articuloTx
     */
    public String getArticuloTx() {
        return articuloTx;
    }

    /**
     * @param articuloTx the articuloTx to set
     */
    public void setArticuloTx(String articuloTx) {
        this.articuloTx = articuloTx;
    }

    //FIXME
    public void cambiarArticuloRevisar() {
        try {
            if (getArticuloTx() != null && !getArticuloTx().isEmpty()) {
                int aux = 2;
                String codigo = getArticuloTx().substring(
                        (getArticuloTx().lastIndexOf("=>") + aux));
                List<ArticuloVO> articulos = articuloImpl.obtenerArticulos(codigo, sesion.getUsuarioConectado().getApCampo().getId(), 0, "");
                if (articulos != null && articulos.size() > 0) {
                    setArticuloID(articulos.get(0).getId());
                    getItemActual().setInvArticulo(articuloImpl.find(articulos.get(0).getId()));
                    setArticuloTx("");
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    private String getCodigos(List<CategoriaVo> categorias) {
        String codigosTxt = "";
        try {
            for (CategoriaVo cat : categorias) {
                codigosTxt = codigosTxt + " and upper(a.CODIGO) like upper('%" + cat.getCodigo() + "%') ";
            }
        } catch (Exception e) {
            System.out.println("Exc: " + e);
        }
        return codigosTxt;
    }

    public void seleccionarCategoria(SelectEvent<CategoriaVo> event) {
        CategoriaVo con = (CategoriaVo) event.getObject();
        setCategoriaVo(con);
        categoriaVo = siRelCategoriaImpl.traerCategoriaPorCategoria(con.getId(), getSoloCodigos(categoriasSeleccionadas), sesion.getUsuarioConectado().getApCampo().getId());
        categoriasSeleccionadas.add(categoriaVo);
        //.out.println("Categoría rec: " + categoriaVo.getNombre() + " cats: " + categoriaVo.getListaCategoria());
        categorias = categoriaVo.getListaCategoria();
        //llenarCategoria(getSoloCodigos(getCategoriasSeleccionadas()));
        if (getCategoriasSeleccionadas() != null && getCategoriasSeleccionadas().size() > 1) {
            setArticulosResultadoBqda(articuloImpl.obtenerArticulos("", sesion.getUsuarioConectado().getApCampo().getId(),
                    0,
                    getCodigos(getCategoriasSeleccionadas().size() > 1
                            ? getCategoriasSeleccionadas().subList(1, getCategoriasSeleccionadas().size())
                            : new ArrayList<>())));
        }
        setCategoriasTxt();
    }

    private void llenarCategoria(String catSeleccionadas) {
        categoriaVo = siRelCategoriaImpl.traerCategoriaPorCategoria(categoriaVo.getId(), catSeleccionadas, sesion.getUsuarioConectado().getApCampo().getId());
        categoriasSeleccionadas.add(categoriaVo);
    }

    private String getSoloCodigos(List<CategoriaVo> categorias) {
        String codigosTxt = "";
        try {
            if (categorias != null) {
                for (CategoriaVo cat : categorias) {
                    if (codigosTxt.isEmpty() && cat.getId() > 0) {
                        codigosTxt = cat.getId() + "";
                    } else if (cat.getId() > 0) {
                        codigosTxt = codigosTxt + "," + cat.getId();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exc: solo código: " + e);
        }
        return codigosTxt;
    }

    ////////////////////////////// ETS //////////
    public void traerOcCategoriasItems() {
        try {
            List<OcCategoriaEts> lista = servicioOcCategoriaEts.traerOcCategoriaEts();

            if (!lista.isEmpty()) {
                listaCategoriaEts = new ArrayList<>();

                for (OcCategoriaEts ce : lista) {
                    SelectItem item = new SelectItem(ce.getId(), ce.getNombre());
                    listaCategoriaEts.add(item);
                }
            }

        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
        }
    }

    public void guardarSeleccionEts() {

        try {
            if (getListaEts().getRowCount() > 0) {
                for (ReRequisicionEts reRequisicion : getListaEts()) {
                    if (getFilasSeleccionadas().get(reRequisicion.getId())
                            && servicioOcOrdenEts.crearOcOrdenEts(getOrdenActual().getId(), getIdCategoriaSelccionada(),
                                    reRequisicion.getSiAdjunto(), sesion.getUsuarioConectado())) {
                        servicioReRequisicion.ponerDisgregada(reRequisicion, sesion.getUsuarioConectado(), Constantes.BOOLEAN_TRUE);

                        getFilasSeleccionadas().remove(reRequisicion.getId());
                    }
                }
            } else {
                FacesUtilsBean.addInfoMessage("Seleccione un ETS de la requisición");
            }

            //setIdCategoriaSelccionada(-1);
            ordenEtsPorCategoria();
            etsPorOrdenRequisicion();
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion en guardar EtsSeleccionadas " + e.getMessage(), e);
        }
    }

    public void ordenEtsPorCategoria() {
        try {
            setListaOcOrdenEts(
                    new ListDataModel(
                            servicioOcOrdenEts.traerEtsPorOrdenCategoria(
                                    getOrdenActual().getId()
                            )
                    )
            );
        } catch (Exception ex) {
            setListaOcOrdenEts(null);
            LOGGER.fatal(this, null, ex);
        }
    }

    public void etsPorOrdenRequisicion() {
        try {
            listaEts = new ListDataModel(servicioReRequisicion.traerAdjuntosPorRequisicionVisible(getOrdenActual().getRequisicion().getId(), Constantes.BOOLEAN_TRUE));
        } catch (Exception ex) {
            listaEts = null;
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void traerEspecificacionTecnica() {
        try {
            setListaOcOrdenEts(new ListDataModel(servicioOcOrdenEts.traerEtsPorOrdenCategoria(getOrdenActual().getId(), Constantes.CERO)));
        } catch (Exception ex) {
            setListaOcOrdenEts(null);
            LOGGER.fatal(this, null, ex);
        }
    }

    public void guardarTodoEts() {
        try {
            if (getListaEts().getRowCount() > 0) {
                for (ReRequisicionEts reRequisicion : getListaEts()) {
                    if (servicioOcOrdenEts.crearOcOrdenEts(getOrdenActual().getId(),
                            getIdCategoriaSelccionada(), reRequisicion.getSiAdjunto(), sesion.getUsuarioConectado())) {
                        servicioReRequisicion.ponerDisgregada(reRequisicion, sesion.getUsuarioConectado(), Constantes.BOOLEAN_TRUE);
                        getFilasSeleccionadas().remove(reRequisicion.getId());
                    }
                }
                traerEspecificacionTecnica();
                ordenEtsPorCategoria();
                etsPorOrdenRequisicion();
                setListaEts(null);
            } else {
                FacesUtilsBean.addInfoMessage("Seleccione al menos un ETS de la requisición");
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion en guardar todo ", e);
        }
    }


    /*
     * ********************** CARGA DE ETS DESDE REQUISICION******************
     */
    /**
     * @param fileEvent
     */
    public void uploadFile(FileUploadEvent fileEvent) {
        ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
        try {
            AlmacenDocumentos almacenDocumentos
                    = proveedorAlmacenDocumentos.getAlmacenDocumentos();
            fileInfo = fileEvent.getFile();
            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                documentoAnexo = new DocumentoAnexo(fileInfo.getContent());
                documentoAnexo.setTipoMime(fileInfo.getContentType());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                documentoAnexo.setRuta(getUploadDirectoryOrden());

                almacenDocumentos.guardarDocumento(documentoAnexo);
            } else {
                FacesUtilsBean.addErrorMessage(new StringBuilder()
                        .append("No se permiten los siguientes caracteres especiales en el nombre del Archivo: ")
                        .append(validadorNombreArchivo.getCaracteresNoValidos())
                        .toString());
            }

            fileInfo.delete();
        } catch (IOException e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        } catch (SIAException e) {
            LOGGER.fatal(this, "+ + + ERROR + + +", e);
            FacesUtilsBean.addInfoMessage("Ocurrió un problema al cargar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    public void guardarEtsLocal() {
        try {
            SiAdjunto adj = servicioSiAdjuntoImpl.save(documentoAnexo.getNombreBase(),
                    new StringBuilder()
                            .append(documentoAnexo.getRuta())
                            .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                    documentoAnexo.getTipoMime(), documentoAnexo.getTamanio(), sesion.getUsuarioConectado().getId());
            if (adj != null) {
                servicioOcOrdenEts.crearOcOrdenEts(getOrdenActual().getId(), idCatSel, adj, sesion.getUsuarioConectado());
            }
            documentoAnexo = null;
            //
            ordenEtsPorCategoria();
            FacesUtilsBean.addInfoMessage("El archivo fue agregado correctamente.");
        } catch (SIAException ex) {
            Logger.getLogger(OrdenCompraBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cerrarEtsLocal() {
        documentoAnexo = null;
        PrimeFaces.current().executeScript("$(dialogoCargarEts).modal('hide');");
    }

    public String getUploadDirectoryOrden() {
        return new StringBuilder().append("ETS/Orden/").append(getOrdenActual().getId()).toString();
    }

    public void etsPorOrdenCategoria() {
        if (getIdCategoriaSelccionada() == -1) {
            setListaOcOrdenEts(null);
        } else {
            try {
                setListaOcOrdenEts(new ListDataModel(servicioOcOrdenEts.traerEtsPorOrdenCategoria(getOrdenActual().getId(), getIdCategoriaSelccionada())));
            } catch (Exception ex) {
                setListaOcOrdenEts(null);
                LOGGER.fatal(this, " excepcion en getOrdenEts", ex);
            }
        }
    }

    public void traerTablaComparativa() {
        if (getOrdenVO() == null) {
            setListaTablaComparativa(null);
        } else {
            try {
                setListaTablaComparativa(servicioOcOrdenEts.traerEtsPorOrdenCategoria(getOrdenActual().getId(), Constantes.OCS_CATEGORIA_TABLA));
            } catch (Exception ex) {
                setListaTablaComparativa(null);
                LOGGER.fatal(this, " excepcion en getOrdenEts", ex);
            }
        }
    }

    public void eliminarTablaComparativaOrden(int idRelacionn) {
        try {

            LOGGER.info("rel : : : " + idRelacionn);

            // Buscar en ReRequicision
            setEtsOcOrden(servicioOcOrdenEts.find(idRelacionn));
            servicioOcOrdenEts.eliminarOcOrdenEts(getEtsOcOrden().getId(), sesion.getUsuarioConectado().getId());
        } catch (NumberFormatException e) {
            LOGGER.fatal(this, "Excepcion en eliminar ETS " + e.getMessage());
        }
    }

    public void eliminarEtsOrden(int idRelacionn) {
        try {
            LOGGER.info(String.format("rel : : : {0}%s", idRelacionn));
            // Buscar en ReRequicision
            setEtsOcOrden(servicioOcOrdenEts.find(idRelacionn));
            ReRequisicionEts reRequisicion
                    = servicioReRequisicion.buscarPorRequisicionAdjunto(
                            getEtsOcOrden().getOrden().getRequisicion(),
                            getEtsOcOrden().getSiAdjunto()
                    );

            if (servicioOcOrdenEts.eliminarOcOrdenEts(getEtsOcOrden().getId(), sesion.getUsuarioConectado().getId())) {
                servicioReRequisicion.ponerDisgregada(reRequisicion, sesion.getUsuarioConectado(), Constantes.BOOLEAN_FALSE);
                FacesUtilsBean.addInfoMessage("El archivo ETS regreso a la requisición.");
            }
            //etsPorOrdenCategoria();
            ordenEtsPorCategoria();
            etsPorOrdenRequisicion();
        } catch (NumberFormatException e) {
            LOGGER.fatal(this, "Excepcion en eliminar ETS ", e);
            FacesUtilsBean.addErrorMessage("Ocurrió un problema al eliminar el archivo, por favor contacte al equipo de soporte SIA (soportesia@ihsa.mx)");
        }
    }

    public boolean eliminarAdjunto(SiAdjunto siAdjunto, Usuario usuario) {
        boolean retVal = false;
        try {

            proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(siAdjunto.getUrl());
            servicioSiAdjuntoImpl.delete(siAdjunto, sesion.getUsuarioConectado().getId());

            retVal = true;
        } catch (SIAException ex) {
            LOGGER.fatal(this, "Excepcion en eliminar adjunto", ex);
        }

        return retVal;
    }

    /**
     * @return the categoriasTxt
     */
    public String getCategoriasTxt() {
        return categoriasTxt;
    }

    /**
     * @param categoriasTxt the categoriasTxt to set
     */
    public void setCategoriasTxt(String categoriasTxt) {
        this.categoriasTxt = categoriasTxt;
    }

    public void setCategoriasTxt() {
        if (categoriasSeleccionadas != null && categoriasSeleccionadas.size() > 1) {
            String cats = " la(s) categoría(s) ";
            for (int i = 1; i < categoriasSeleccionadas.size(); i++) {
                cats += "->" + categoriasSeleccionadas.get(i).getNombre();
            }
            setCategoriasTxt(cats);
        } else {
            setCategoriasTxt("todas las categorías.");
        }
    }

    /**
     * @return the articuloID
     */
    public int getArticuloID() {
        return articuloID;
    }

    /**
     * @param articuloID the articuloID to set
     */
    public void setArticuloID(int articuloID) {
        this.articuloID = articuloID;
    }

    /**
     * @return the listaConContrato
     */
    public List<OrdenVO> getListaConContrato() {
        return listaConContrato;
    }

    /**
     * @param listaConContrato the listaConContrato to set
     */
    public void setListaConContrato(List<OrdenVO> listaConContrato) {
        this.listaConContrato = listaConContrato;
    }

    /**
     * @return the listaRechazos
     */
    public List<MovimientoVO> getListaRechazos() {
        return listaRechazos;
    }

    /**
     * @param listaRechazos the listaRechazos to set
     */
    public void setListaRechazos(List<MovimientoVO> listaRechazos) {
        this.listaRechazos = listaRechazos;
    }

    /**
     * @return the itemActual
     */
    public OrdenDetalle getItemActual() {
        return itemActual;
    }

    /**
     * @param itemActual the itemActual to set
     */
    public void setItemActual(OrdenDetalle itemActual) {
        this.itemActual = itemActual;
    }

    /**
     * @return the idTarea
     */
    public int getIdTarea() {
        return idTarea;
    }

    /**
     * @param idTarea the idTarea to set
     */
    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    /**
     * @param monedaBean the monedaBean to set
     */
    public void setMonedaBean(MonedaBean monedaBean) {
        this.monedaBean = monedaBean;
    }

    /**
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * @return the ordenVO
     */
    public OrdenVO getOrdenVO() {
        return ordenVO;
    }

    /**
     * @param ordenVO the ordenVO to set
     */
    public void setOrdenVO(OrdenVO ordenVO) {
        this.ordenVO = ordenVO;
    }

    /**
     * @return the listaOrden
     */
    public List<OrdenVO> getListaOrden() {
        return listaOrden;
    }

    /**
     * @param listaOrden the listaOrden to set
     */
    public void setListaOrden(List<OrdenVO> listaOrden) {
        this.listaOrden = listaOrden;
    }

    /**
     * @return the operacionItem
     */
    public String getOperacionItem() {
        return operacionItem;
    }

    /**
     * @param operacionItem the operacionItem to set
     */
    public void setOperacionItem(String operacionItem) {
        this.operacionItem = operacionItem;
    }

    /**
     * @return the simbolos
     */
    public String getSimbolos() {
        return simbolos;
    }

    /**
     * @return the opcionSeleccionada
     */
    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(String opcionSeleccionada) {
        this.opcionSeleccionada = opcionSeleccionada;
    }

    /**
     * @return the listaEts
     */
    public DataModel<ReRequisicionEts> getListaEts() {
        return listaEts;
    }

    /**
     * @param listaEts the listaEts to set
     */
    public void setListaEts(DataModel<ReRequisicionEts> listaEts) {
        this.listaEts = listaEts;
    }

    /**
     * @return the listaOcOrdenEts
     */
    public DataModel<OrdenEtsVo> getListaOcOrdenEts() {
        return listaOcOrdenEts;
    }

    /**
     * @param listaOcOrdenEts the listaOcOrdenEts to set
     */
    public void setListaOcOrdenEts(DataModel<OrdenEtsVo> listaOcOrdenEts) {
        this.listaOcOrdenEts = listaOcOrdenEts;
    }

    /**
     * @return the listaTablaComparativa
     */
    public List getListaTablaComparativa() {
        return listaTablaComparativa;
    }

    /**
     * @param listaTablaComparativa the listaTablaComparativa to set
     */
    public void setListaTablaComparativa(List listaTablaComparativa) {
        this.listaTablaComparativa = listaTablaComparativa;
    }

    /**
     * @return the filasSeleccionadas
     */
    public Map<Integer, Boolean> getFilasSeleccionadas() {
        return filasSeleccionadas;
    }

    /**
     * @param filasSeleccionadas the filasSeleccionadas to set
     */
    public void setFilasSeleccionadas(Map<Integer, Boolean> filasSeleccionadas) {
        this.filasSeleccionadas = filasSeleccionadas;
    }

    /**
     * @return the seleccionarTodo
     */
    public boolean isSeleccionarTodo() {
        return seleccionarTodo;
    }

    /**
     * @param seleccionarTodo the seleccionarTodo to set
     */
    public void setSeleccionarTodo(boolean seleccionarTodo) {
        this.seleccionarTodo = seleccionarTodo;
    }

    /**
     * @return the idCategoriaSelccionada
     */
    public int getIdCategoriaSelccionada() {
        return idCategoriaSelccionada;
    }

    /**
     * @param idCategoriaSelccionada the idCategoriaSelccionada to set
     */
    public void setIdCategoriaSelccionada(int idCategoriaSelccionada) {
        this.idCategoriaSelccionada = idCategoriaSelccionada;
    }

    /**
     * @return the idOrdenSeleccionada
     */
    public int getIdOrdenSeleccionada() {
        return idOrdenSeleccionada;
    }

    /**
     * @param idOrdenSeleccionada the idOrdenSeleccionada to set
     */
    public void setIdOrdenSeleccionada(int idOrdenSeleccionada) {
        this.idOrdenSeleccionada = idOrdenSeleccionada;
    }

    /**
     * @return the etsOcOrden
     */
    public OcOrdenEts getEtsOcOrden() {
        return etsOcOrden;
    }

    /**
     * @param etsOcOrden the etsOcOrden to set
     */
    public void setEtsOcOrden(OcOrdenEts etsOcOrden) {
        this.etsOcOrden = etsOcOrden;
    }

    /**
     * @return the listaCategoriaEts
     */
    public List<SelectItem> getListaCategoriaEts() {
        return listaCategoriaEts;
    }

    /**
     * @param listaCategoriaEts the listaCategoriaEts to set
     */
    public void setListaCategoriaEts(List<SelectItem> listaCategoriaEts) {
        this.listaCategoriaEts = listaCategoriaEts;
    }

    /**
     * @return the listaConvenioArticuloVo
     */
    public List<ConvenioArticuloVo> getListaConvenioArticuloVo() {
        return listaConvenioArticuloVo;
    }

    /**
     * @param listaConvenioArticuloVos the listaConvenioArticuloVo to set
     */
    public void setListaConvenioArticuloVo(List<ConvenioArticuloVo> listaConvenioArticuloVos) {
        this.listaConvenioArticuloVo = listaConvenioArticuloVos;
    }

    /**
     * @return the nombreTareaMulti
     */
    public String getNombreTareaMulti() {
        return nombreTareaMulti;
    }

    /**
     * @param nombreTareaMulti the nombreTareaMulti to set
     */
    public void setNombreTareaMulti(String nombreTareaMulti) {
        this.nombreTareaMulti = nombreTareaMulti;
    }

    /**
     * @return the nombreProyectoMulti
     */
    public String getNombreProyectoMulti() {
        nombreProyectoMulti = "";
        if (this.listaItems != null && this.listaItems.size() > 0) {
            nombreProyectoMulti = this.listaItems.get(0).getMultiProyectos();
        }
        return nombreProyectoMulti;
    }

    /**
     * @param nombreProyectoMulti the nombreProyectoMulti to set
     */
    public void setNombreProyectoMulti(String nombreProyectoMulti) {
        this.nombreProyectoMulti = nombreProyectoMulti;
    }

    /**
     * @return the idMoneda
     */
    public int getIdMoneda() {
        return idMoneda;
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
    }

    /**
     * @return the historicoVentas
     */
    public List<OrdenVO> getHistoricoVentas() {
        return historicoVentas;
    }

    /**
     * @param historicoVentas the historicoVentas to set
     */
    public void setHistoricoVentas(List<OrdenVO> historicoVentas) {
        this.historicoVentas = historicoVentas;
    }
}
