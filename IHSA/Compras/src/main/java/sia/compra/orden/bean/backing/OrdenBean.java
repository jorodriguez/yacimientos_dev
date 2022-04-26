/*
 * OrdenBean.java
 * Creado el 14/10/2009, 04:29:00 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.orden.bean.backing;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.compra.requisicion.bean.backing.CargaEtsBean;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.MenuBarBean;
import sia.compra.requisicion.bean.backing.ProveedorBean;
import sia.compra.requisicion.bean.backing.ProyectoOtBean;
import sia.compra.requisicion.bean.backing.RequisicionBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.excepciones.SIAException;
import sia.inventarios.service.InvOrdenFormatoImpl;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.CoNoticia;
import sia.modelo.Compania;
import sia.modelo.Estatus;
import sia.modelo.OcProductoCompania;
import sia.modelo.Orden;
import sia.modelo.OrdenDetalle;
import sia.modelo.Proveedor;
import sia.modelo.Rechazo;
import sia.modelo.RequisicionDetalle;
import sia.modelo.Usuario;
import sia.modelo.campoVO.CampoOrden;
import sia.modelo.comunicacion.ComparteCon;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.orden.vo.ContactoOrdenVo;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.orden.vo.OcActivoFijoVO;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.requisicion.vo.RequisicionEtsVo;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.vo.FacturaAdjuntoVo;
import sia.modelo.sistema.vo.FacturaContenidoNacionalVo;
import sia.modelo.sistema.vo.FacturaDetalleVo;
import sia.modelo.sistema.vo.FacturaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.OrdenFormatoVo;
import sia.notificaciones.orden.impl.NotificacionOrdenImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioAdjuntoImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OcActivoFijoImpl;
import sia.servicios.orden.impl.OcOrdenCoNoticiaImpl;
import sia.servicios.orden.impl.OcProductoCompaniaImpl;
import sia.servicios.orden.impl.OcProductoImpl;
import sia.servicios.orden.impl.OrdenDetalleImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.orden.impl.OrdenSiMovimientoImpl;
import sia.servicios.proveedor.impl.PvProveedorSinCartaIntencionImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaContenidoNacionalImpl;
import sia.servicios.sistema.impl.SiFacturaDetalleImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail new_nick_name@hotmail.c/ho om @date 14/10/2009
 */
@Named(value = "ordenBean")
@ViewScoped
public class OrdenBean implements Serializable {

    private static final UtilLog4j<OrdenBean> LOGGER = UtilLog4j.log;

    //------------------------------------------------------
    public static final String BEAN_NAME = "ordenBean";
    //------------------------------------------------------
    @Inject
    private RequisicionImpl requisicionServicioImpl;
    @Inject
    private OrdenImpl ordenServicioRemoto;
    @Inject
    private ReRequisicionEtsImpl servicioReRequisicion;
    @Inject
    private OrdenDetalleImpl ordenDetalleImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private OcActivoFijoImpl ocActivoFijoImpl;
    @Inject
    private OcProductoCompaniaImpl ocProductoCompaniaImpl;
    @Inject
    private OcProductoImpl ocProductoImpl;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    private EstatusImpl estatusImpl;
    @Inject
    private OrdenSiMovimientoImpl ordenSiMovimientoImpl;
    @Inject
    private OcOrdenCoNoticiaImpl ocOrdenCoNoticiaImpl;
    @Inject
    private NotificacionOrdenImpl notificacionOrdenImpl;
    @Inject
    private CoNoticiaImpl coNoticiaImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    //
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private CvConvenioAdjuntoImpl cvConvenioAdjuntoImpl;
    @Inject
    private InvOrdenFormatoImpl invOrdenFormatoImpl;
    /////////////////
    @Inject
    SiFacturaImpl siFacturaImpl;
    @Inject
    SiFacturaAdjuntoImpl siFacturaAdjuntoImpl;
    @Inject
    SiFacturaContenidoNacionalImpl facturaContenidoNacionalImpl;
    @Inject
    SiFacturaDetalleImpl siFacturaDetalleImpl;
    @Inject
    PvProveedorSinCartaIntencionImpl PvProveedorSinCartaIntencionImpl;
    ////////////////    
    //-- Managed Beans ----
    @Inject
    private UsuarioBean usuarioBean;
    @Inject
    private MenuBarBean menuBarBean;
    @Inject
    private ProyectoOtBean proyectoOtBean;
    @Inject
    private ProveedorBean proveedorBean;

    @Inject
    private PopupCompletarActivoFijoBean popupCompletarActivoFijoBean;
    @Inject
    private NotaOrdenBean notaOrdenBean;
    // - - - - - - - - - -

    //  private Map<String, List<OrdenVO>> mapaOrdenes;
    private List<OrdenDetalleVO> listaItems; //almacena la lista de Items de la Orden de compra
//    private DataModel listaRechazos; //almacena la lista de Rechazos de la Orden de compra
    @Getter
    @Setter
    private List<RequisicionEtsVo> listaEts; //almacena la lista de Espeficicacion tecnica de suministro
    @Getter
    @Setter
    private List<RequisicionDetalle> itemsRequisicion;
    //----------------------
    private Orden ordenActual;
    private List<SelectItem> ocProductos;
    private List<String> activoFijjoCodigos;
    private OrdenDetalle itemActual;
    //- - - - - - - -
    private String letra = "A"; //alamacena la letra para filtrar proveedores y convenios
    private List<ProveedorVo> listaProveedores; //almacena la lista de proveedores
    private List<ContactoProveedorVO> listaContactos; //almacena la lista de contactos de cada proveedor    
//    private List listaMonedas; //almacena la lista de Monedas
    private Proveedor proveedor; //Sirve para poder mostrar la dirección en pantalla del proveedor seleccionado
    private String contactoSeleccionado;

    private List<ContactoOrdenVo> contactos = new ArrayList();//almacena la lista de contactos de la orden de compra
    private String contactoParaEliminar;
    private String motivoCancelacion = Constantes.VACIO;
    private String motivoDevolucion = Constantes.VACIO;
    private String operacionItem, aprueba, revisa;
    private boolean mostrar;
    private boolean actualizarExcel;
    private String paginaAtras;
    private int panelSeleccionado;
    private int idProveedorr;
    private int idTarea;
    //
    private String fechaInicio;
    private String fechaFin = Constantes.FMT_ddMMyyy.format(new Date());
    //
    private int idProyectoOT;
    private String tipoFiltro;
    private String referencia;// usuado tambien para buscar OC/S en pag ordenCompra
    private OrdenVO ordenVO;
    //
    private String consecutivo;
    //
    private boolean irInicio = false;
    //
    private List<ContratoVO> listaArchivoConvenio;

    private final static String ERR_MSG_OCS = "Ocurrio un error en el proceso de OC/S : : : ";
    private final static String ERR_UNEXPECTED = "Error inesperado: por favor notifique el problema a: soportesia@ihsa.mx  ";

    private final static String ERR_OPERACION = "Ocurrió un error en la operación, por favor contacte al equipo de soporte de SIA : soportesia@ihsa.mx";

    private final static String JS_METHOD_REGRESAR_DIV_AUTORIZA = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
    private final static String JS_METHOD_REGRESAR_DIV_AUTO = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
    private final static String JS_METHOD_LIMPIAR_TODOS = ";limpiarTodos();";

    private final static String MSG_COMPRAS = "Compras";
    private final static String MSG_APROBAR = "Aprobar";

    private final static String ACTION_ORDEN_COMPRA = "ordenCompra";

//
    private List<MovimientoVO> listaRechazos;
    private List<OrdenFormatoVo> formatosEntrada;

    private List<SelectItem> listaMonedas;
    private String nombreTareaMulti;
    private String nombreProyectoMulti;

    private List<FacturaVo> listaFactura;
    private FacturaVo facturaVo;
    private List<FacturaVo> listaNotaCredito;
    private List<FacturaContenidoNacionalVo> contenidoNacional;
    private List<FacturaAdjuntoVo> listaArchivosFactura;
    private List<FacturaAdjuntoVo> listaArchivosNotaCredito;
    private boolean proveedorCI;
    @Getter
    @Setter
    private List<OrdenVO> ordenesDevCan;
    @Getter
    @Setter
    private List<OrdenVO> ocs;
    @Getter
    @Setter
    private List<OrdenVO> ordenesTareaAF;
    @Getter
    @Setter
    private List<OrdenVO> ordenesTareaPS;

    public OrdenBean() {
    }

    /**
     */
    @PostConstruct
    public void iniciarLimpiar() {
        //  mapaOrdenes = new HashMap<>();
        setOrdenActual(null);
        notaOrdenBean.setFiltrar(false);
        llenarCompras();
        ordenesTareaAF = new ArrayList<>();
        ordenesTareaPS = new ArrayList<>();
    }

    private void llenarCompras() {
        ocs = new ArrayList<>();
        //VoBo
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest(); // returns something like "/myapplication/home.faces" 
        String fullURI = servletRequest.getRequestURI();
        if (fullURI.endsWith("AprobarOrdenCompra.xhtml")) { //110
            ocs = ordenServicioRemoto.getOrdenesApruebaGerenciaSolicitante(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId());
        } else if (fullURI.endsWith("AutorizaCompras.xhtml")) { //140
            ocs = ordenServicioRemoto.getOrdenesAutorizaCompras(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId());
        } else if (fullURI.endsWith("AutorizaLicitacion.xhtml")) { //151
            ocs = ordenServicioRemoto.getOrdenesAutorizaLicitacion(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId());
        } else if (fullURI.endsWith("AutorizaSocio.xhtml")) { //135
            ocs = ordenServicioRemoto.getOrdenesAutorizaSocio(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId());
        } else if (fullURI.endsWith("AutorizacionIhsa.xhtml")) { //130
            ocs = ordenServicioRemoto.getOrdenesAutorizaIHSA(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId());
        } else if (fullURI.endsWith("AutorizacionMpg.xhtml")) { //120
            ocs = ordenServicioRemoto.getOrdenesAutorizaMPG(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId());
        } else if (fullURI.endsWith("autorizaFinanza.xhtml")) {  //autorizaFinanza
            ocs = ordenServicioRemoto.getOrdenesAutorizaFinanzas(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId());
        } else {
            ocs = new ArrayList<>();
        }

        ordenesAutorizaTareaAF();
        ordenesAutorizaTareaOP();
        /*
        mapaOrdenes.put("vobo", ordenServicioRemoto.getOrdenesApruebaGerenciaSolicitante(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaOrdenes.put("revisa", ordenServicioRemoto.getOrdenesAutorizaMPG(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaOrdenes.put("aprueba", ordenServicioRemoto.getOrdenesAutorizaIHSA(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaOrdenes.put("autoriza", ordenServicioRemoto.getOrdenesAutorizaCompras(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaOrdenes.put("socio", ordenServicioRemoto.getOrdenesAutorizaSocio(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaOrdenes.put("finanzas", ordenServicioRemoto.getOrdenesAutorizaFinanzas(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaOrdenes.put("licitacion", ordenServicioRemoto.getOrdenesAutorizaLicitacion(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
         */
    }

    public void etsPorRequisicion() {
        try {
            listaEts = servicioReRequisicion.traerAdjuntosPorRequisicion(getOrdenActual().getRequisicion().getConsecutivo());
        } catch (Exception ex) {
            listaEts = null;
            LOGGER.fatal(this, ex);
        }
    }

    public void cargarEts() {
        try {
            setPaginaAtras(FacesUtilsBean.getRequestParameter("paginaAtras"));
            notaOrdenBean.setFiltrar(false);
        } catch (Exception e) {
            LOGGER.fatal(this, e);
        }
    }

    public void seleccionarOrden(int idOrden) {
        setOrdenActual(ordenServicioRemoto.find(idOrden));
        getOrdenActual().setLeida(Constantes.BOOLEAN_TRUE);
        ordenServicioRemoto.editarOrden(ordenActual);
        notaOrdenBean.setFiltrar(true);

        itemsPorOrden();
        notasPorOrden();
        CargaEtsBean cargaEtsBean = (CargaEtsBean) FacesUtilsBean.getManagedBean("cargaEtsBean");
        //
        cargaEtsBean.traerTablaComparativa();
        cargaEtsBean.etsPorOrdenRequisicion();
        //
        cargaEtsBean.ordenEtsPorCategoria();
        cargaEtsBean.traerEspecificacionTecnica();
        setProveedorCI(PvProveedorSinCartaIntencionImpl.existeProveedorCI(getOrdenActual().getApCampo().getId(), getOrdenActual().getProveedor().getId()));
        //
        rechazos();
        //
        itemsRequisicion = new ArrayList<>();
        itemsPorRequisicion();

    }

    public void llenarListaConvenio(String idOr) {
        listaArchivoConvenio = cvConvenioAdjuntoImpl.traerPorConvenioPorNumero(idOr);
    }

    public void limpiarListaConvenio() {
        listaArchivoConvenio = null;
    }

    public List<NoticiaVO> notasPorOrden() {
        try {
            if (ordenActual != null) {
                return notaOrdenBean.traerNoticiaPorOrden(getOrdenActual().getId());
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "notasPorOrden  . . ." + e.getMessage(), e);
        }
        return null;
    }

    public void seleccionarOrdenAF(int idOrden) {
        this.setOrdenActual(ordenServicioRemoto.find(idOrden));
        this.notaOrdenBean.setNotaActual(null);
        this.mostrar = true;
        List<OrdenDetalleVO> lo = this.ordenServicioRemoto.itemsPorOrdenCompra(getOrdenActual().getId());
        this.setListaItems(lo);
    }

    public void seleccionarOrdenOP(int idOrden) {
        this.setOrdenActual(ordenServicioRemoto.find(idOrden));
        this.mostrar = true;
        List<OrdenDetalleVO> lo = null;
        if (this.getOrdenActual().isMultiproyecto()) {
            lo = this.ordenServicioRemoto.itemsPorOrdenCompraMulti(getOrdenActual().getId());
        } else {
            lo = this.ordenServicioRemoto.itemsPorOrdenCompra(getOrdenActual().getId());
        }
        this.setListaItems(lo);
        CargaEtsBean cargaEtsBean = (CargaEtsBean) FacesUtilsBean.getManagedBean("cargaEtsBean");
        cargaEtsBean.traerTablaComparativa();
    }

    public void limpiarTab() {
        quitarSeleccionOrden(true);
    }

    public void seleccionarContrato(ValueChangeEvent event) {
        //System.out.println(event.getNewValue());
        if (!event.getNewValue().equals(Constantes.OCS_SIN_CONTRATO)) {//
            ContratoVO contratoVO = convenioImpl.traerConveniosPorCodigo((String) event.getNewValue());

            if (contratoVO != null) {
                LOGGER.fatal(this, "con: " + contratoVO.getFechaVencimiento());

                if (siManejoFechaImpl.compare(contratoVO.getFechaVencimiento(), new Date()) < 0) {
                    // el contrato eta vencido
                    PrimeFaces.current().executeScript(";alertaGeneral('" + "El contrato seleccionado esta vencido." + "');");
                }
            }
        }
    }

    private int recorreItems(List<OrdenDetalleVO> lrd) {
        int todoBien = 0;
        int errorMoneda = 1;
        int errorUnidad = 2;
        int errorPrecio = 3;
        String mo = "";
        int i = 0;
        for (OrdenDetalleVO ordenDetalleVO : lrd) {
//            if (i == 0) {
//                mo = ordenDetalleVO.getMoneda();
//            }
//            if (!mo.equals(ordenDetalleVO.getMoneda())) {
//                return errorMoneda;
//            }
            if (ordenDetalleVO.getArtIdUnidad() == 0) {
                return errorUnidad;
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

//    public void guardarItemOCProducto(ValueChangeEvent event) {
    public void guardarItemOCProducto(OrdenDetalleVO odvo) {
        if (odvo.getId() > 0) {
            setItemActual(ordenDetalleImpl.find(odvo.getId()));
            getItemActual().setOcProductoCompania(ocProductoCompaniaImpl.find(odvo.getOcProductoID()));
            ordenDetalleImpl.editar(getItemActual());
        } else {
            ordenServicioRemoto.actualizarMultiItemsProducto(this.getOrdenActual().getId(), odvo.getIdAgrupador(),
                    odvo.getOcProductoID());
        }
    }

    public void modificarItemOCProducto(int id) {
        OrdenDetalleVO ordenDetalleVO = (OrdenDetalleVO) getListaItems().get(id);
        setItemActual(ordenDetalleImpl.find(ordenDetalleVO.getId()));
        List<OrdenDetalleVO> listaDetalleOCS = (List<OrdenDetalleVO>) this.getListaItems();
        listaDetalleOCS.get(id).setEditar(true);
        this.setListaItems(listaDetalleOCS);
    }

    public void crearNota() {
        notaOrdenBean.setFiltrar(true);
    }

    public void completarCrearNota() {
        try {
            StringBuilder correoNota = new StringBuilder();
            List<ContactoOrdenVo> listaContactosOrden = ordenServicioRemoto.getContactosVo(ordenActual.getId());
            UsuarioVO uvo = usuarioImpl.traerResponsableGerencia(ordenActual.getApCampo().getId(), Constantes.GERENCIA_ID_COMPRAS,
                    ordenActual.getCompania().getRfc());

            List<UsuarioRolVo> lur = siUsuarioRolImpl.traerRolPorCodigo(Constantes.CODIGO_ROL_NOTI_NOT, ordenActual.getApCampo().getId(), Constantes.MODULO_COMPRA);

            if (lur != null && !lur.isEmpty()) {
                correoNota = new StringBuilder();
                for (UsuarioRolVo lur1 : lur) {
                    if (correoNota.length() == 0) {
                        correoNota.append(lur1.getCorreo());
                    } else {
                        correoNota.append(",").append(lur1.getCorreo());
                    }
                }
            }

            StringBuilder asunto = new StringBuilder(100);
            asunto.append("Nota de la orden: ").append(ordenActual.getConsecutivo()).append(' ');
            if (notificacionOrdenImpl.enviarNotificacionNotaOrden(
                    castUsuarioInvitados(ordenActual),
                    correoNota.length() > 0 ? correoNota.toString() : "",
                    uvo.getMail(),
                    ordenActual,
                    asunto.toString(), usuarioBean.getUsuarioConectado().getNombre(), notaOrdenBean.getTextoNoticia(),
                    listaContactosOrden)) {
                //
                //Noticias nueavas
                CoNoticia coNoticia = coNoticiaImpl.nuevaNoticia(usuarioBean.getUsuarioConectado().getId(), asunto.toString(), "",
                        notaOrdenBean.getTextoNoticia(), 0, 0, castUsuarioComparteCon(ordenActual));
                //Guarda la nota
                ocOrdenCoNoticiaImpl.guardarNoticia(usuarioBean.getUsuarioConectado().getId(), coNoticia, ordenActual);

                FacesUtilsBean.addInfoMessage("Se creó correctamente la nota");
            }
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoNotaOrden);");
            //this.popupCrearNotaBean.toggleModal(actionEvent);
            //Regresa a la tabla
            PrimeFaces.current().executeScript(JS_METHOD_REGRESAR_DIV_AUTO);
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }

    }

    private List<ComparteCon> castUsuarioComparteCon(Orden orden) {
        List<ComparteCon> retVal = null;
        try {
            List<ComparteCon> listaCompartidos = new ArrayList<>();
            //        hacer una lista de invitados enviar la notificacion y si se envio correctamente guardarlos a todos
            switch (orden.getAutorizacionesOrden().getEstatus().getId()) {
                case Constantes.ORDENES_SIN_APROBAR:
                    //solicita
                    listaCompartidos.add(castComparteCon(orden.getAnalista()));
                    //invitados.append(this.orden.getAutorizacionesOrden().getSolicito().getEmail());
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    //invitados.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_MPG:
                    //solicita
                    listaCompartidos.add(castComparteCon(orden.getAnalista()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_IHSA:
                    listaCompartidos.add(castComparteCon(orden.getAnalista()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaMpg()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    break;
                case Constantes.ESTATUS_POR_APROBAR_SOCIO:
                    listaCompartidos.add(castComparteCon(orden.getAnalista()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaMpg()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaIhsa()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_COMPRAS:
                    listaCompartidos.add(castComparteCon(orden.getAnalista()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaMpg()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaIhsa()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    if (orden.getCompania().isSocio()) {
                        listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaFinanzas()));
                    }
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_LICITACION:
                    listaCompartidos.add(castComparteCon(orden.getAnalista()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaGerencia()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaMpg()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaIhsa()));
                    listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaCompras()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
                    if (orden.getCompania().isSocio()) {
                        listaCompartidos.add(castComparteCon(orden.getAutorizacionesOrden().getAutorizaFinanzas()));
                    }
                    break;
                default:
                    listaCompartidos.add(castComparteCon(orden.getAnalista()));
                    listaCompartidos.add(castComparteCon(usuarioBean.getUsuarioConectado()));
            }

            retVal = listaCompartidos;
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion al castear los usuario  " + e.getMessage(), e);
        }

        return retVal;
    }

    private ComparteCon castComparteCon(Usuario usuario) {
        ComparteCon compateCon = new ComparteCon();
        compateCon.setId(usuario.getId());
        compateCon.setNombre(usuario.getNombre());
        compateCon.setCorreoUsuario(usuario.getEmail());
        compateCon.setTipo("Usuario");
        return compateCon;
    }

    private String castUsuarioInvitados(Orden orden) {
        StringBuilder invitados = null;

        try {
            //        hacer una lista de invitados enviar la notificacion y si se envio correctamente guardarlos a todos
            invitados = new StringBuilder();
            switch (orden.getAutorizacionesOrden().getEstatus().getId()) {
                case Constantes.ESTATUS_SOLICITADA:
                    invitados.append(orden.getAutorizacionesOrden().getSolicito().getEmail());
                    break;
                case Constantes.ORDENES_SIN_APROBAR:
                    //solicita
                    invitados.append(orden.getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_MPG:
                    //solicita
                    invitados.append(orden.getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
                    invitados.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_IHSA:
                    invitados.append(orden.getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaMpg().getEmail());
                    invitados.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ESTATUS_POR_APROBAR_SOCIO:
                    invitados.append(orden.getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaMpg().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaIhsa().getEmail());
                    invitados.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
                case Constantes.ORDENES_SIN_AUTORIZAR_COMPRAS:
                    invitados.append(orden.getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaGerencia().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaMpg().getEmail());
                    invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaIhsa().getEmail());
                    invitados.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                    if (orden.getCompania().isSocio()) {
                        invitados.append(',').append(orden.getAutorizacionesOrden().getAutorizaFinanzas().getEmail());
                    }
                    break;
                default:
                    invitados.append(orden.getAutorizacionesOrden().getSolicito().getEmail());
                    invitados.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                    break;
            }

        } catch (Exception e) {
            LOGGER.fatal(this, "Excepcion al castear los usuario  " + e.getMessage(), e);
            invitados = null;
        }

        return (invitados == null ? null : invitados.toString());
    }

    public DataModel getNotasPorOrden() {
        DataModel listaNotas = null;
        if (this.getOrdenActual() != null) {
            listaNotas = this.notaOrdenBean.getNotasPorOrden(this.getOrdenActual().getId());
        }
        return listaNotas;
    }

    public DataModel getNotasParaDetalleOrden() {
        DataModel listaNotas = null;
        if (this.getOrdenActual() != null) {
            listaNotas = this.notaOrdenBean.getNotasParaDetalleOrden(this.getOrdenActual().getId());
        }
        return listaNotas;
    }
// Item 

    public void eliminarItem(int id) {
        try {
            OrdenDetalleVO odvo = listaItems.get(id);
            this.setItemActual(ordenDetalleImpl.find(odvo.getId()));
            this.ordenServicioRemoto.eliminarItem(getItemActual());
            setOrdenActual(ordenServicioRemoto.find(getOrdenActual().getId()));

            FacesUtilsBean.addInfoMessage("Se eliminó correctamente el Ítem");
            itemsPorOrden();
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    public void cambiarValorProyectoOt(ValueChangeEvent event) {
        this.getOrdenActual().setProyectoOt(this.proyectoOtBean.buscarPorNombre(event.getNewValue(), getOrdenActual().getCompania().getNombre()));
    }

    /**
     * @return Lista de ordenes de compra Sin autorizar por el socio ccampo
     * finanzas
     */
    public String traerNombreSocio() {
        String nombreSocio = "";
        List<UsuarioRolVo> lu = siUsuarioRolImpl.traerUsuarioPorRolModulo(Constantes.ROL_SOCIO, Constantes.MODULO_COMPRA, usuarioBean.getUsuarioConectado().getApCampo().getId());
        if (lu != null && !lu.isEmpty()) {
            for (UsuarioRolVo voUsr : lu) {
                if (!nombreSocio.isEmpty()) {
                    nombreSocio += ", ";
                }
                nombreSocio += lu.get(0).getUsuario();
            }
        }
        return nombreSocio;
    }

    public void ordenesAutorizaTareaAF() {
        try {
            OrdenVO o;
            List<Object[]> l = this.ordenServicioRemoto.getOrdenesAutorizadasCompras(this.usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    TipoRequisicion.AF.name());
            for (Object[] objects : l) {
                o = new OrdenVO();
                o.setId((Integer) objects[0]);
                o.setConsecutivo(String.valueOf(objects[1]));
                o.setReferencia(String.valueOf(objects[2]));
                o.setRequisicion((String) objects[3]);
                o.setFecha((Date) objects[4]);
                o.setProveedor((String) objects[6]);
                o.setEstatus((String) objects[5]);
                o.getContratoVO().setNumero(String.valueOf(objects[7]));
                o.setTotal((Double) objects[8]);
                o.setMoneda((String) objects[9]);
                o.setSuperaMonto((Boolean) objects[10]);
                o.setSelected(false);
                ordenesTareaAF.add(o);
            }

            //mapaOrdenes.put("autorizaTareaAF", lo);
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    public void ordenesAutorizaTareaOP() {
        try {
            OrdenVO o;
            List<Object[]> l = this.ordenServicioRemoto.getOrdenesAutorizadasCompras(this.usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    TipoRequisicion.PS.name());
            for (Object[] objects : l) {
                o = new OrdenVO();
                o.setId((Integer) objects[0]);
                o.setConsecutivo(String.valueOf(objects[1]));
                o.setReferencia(String.valueOf(objects[2]));
                o.setRequisicion((String) objects[3]);
                o.setFecha((Date) objects[4]);
                o.setEstatus((String) objects[5]);
                o.setProveedor((String) objects[6]);
                o.getContratoVO().setNumero(String.valueOf(objects[7]));
                o.setTotal((Double) objects[8]);
                o.setMoneda((String) objects[9]);
                o.setSuperaMonto((Boolean) objects[10]);
                o.setSelected(false);
                ordenesTareaPS.add(o);
            }
            //  mapaOrdenes.put("autorizaTareaOP", lo);
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    public void ordenesIntegrarSIA() {
        try {
            if (actualizarExcel) {
                List<OrdenVO> l = ordenServicioRemoto.traerOrdenStatusUsuarioRol(Constantes.ESTATUS_ENVIADA_PROVEEDOR, usuarioBean.getUsuarioConectado().getApCampo().getId(), usuarioBean.getUsuarioConectado().getId(), Constantes.ROL_INTEGRA_SIA_NAV);
                //   mapaOrdenes.put("integrarSIA", l);
            }
            actualizarExcel = false;
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    /**
     * @return Lista de ordenes de compra que tiene q autorizar compras
     */
    public void ordenesAutorizaTarea(String tipo) {
        try {
            List<OrdenVO> lo = new ArrayList<>();
            OrdenVO o;
            List<Object[]> l = this.ordenServicioRemoto.getOrdenesAutorizadasCompras(this.usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getApCampo().getId(), tipo);
            for (Object[] objects : l) {
                o = new OrdenVO();
                o.setId((Integer) objects[0]);
                o.setConsecutivo(String.valueOf(objects[1]));
                o.setReferencia(String.valueOf(objects[2]));
                o.setRequisicion((String) objects[3]);
                o.setFecha((Date) objects[4]);
                o.setProveedor((String) objects[6]);
                o.setEstatus((String) objects[5]);
                o.getContratoVO().setNumero(String.valueOf(objects[7]));
                o.setTotal((Double) objects[8]);
                o.setMoneda((String) objects[9]);
                o.setSuperaMonto((Boolean) objects[10]);
                o.setSelected(false);
                lo.add(o);
            }
            //   mapaOrdenes.put("autorizaTarea", lo);
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    /**
     */
    public void itemsPorOrden() {
        try {
            if (this.getOrdenActual().isMultiproyecto()) {
                setListaItems(ordenServicioRemoto.itemsPorOrdenCompraMulti(getOrdenActual().getId()));
            } else {
                setListaItems(ordenServicioRemoto.itemsPorOrdenCompra(getOrdenActual().getId()));
            }
        } catch (Exception ex) {
            setListaItems(null);
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    public void itemsPorOrdenSingle() {
        try {
            setListaItems(ordenServicioRemoto.itemsPorOrdenCompra(getOrdenActual().getId()));
        } catch (Exception ex) {
            setListaItems(null);
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    public List<OrdenDetalleVO> getItemsPorOrdenTarea() {
        return getListaItems();
    }

    public void rechazos() {
        try {
            setListaRechazos(ordenSiMovimientoImpl.getMovimientsobyOrden(getOrdenActual().getId()));
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    public void formatosEntradaOrden() {
        try {
            formatosEntrada = new ArrayList<OrdenFormatoVo>();
            if (ordenActual != null) {
                formatosEntrada = invOrdenFormatoImpl.traerPorCompra(getOrdenActual().getConsecutivo());
            } else {
                formatosEntrada = null;
            }
        } catch (Exception ex) {
            setListaItems(null);
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    public void actualizarOrden(int idOrden) {
        try {
            setOrdenActual(ordenServicioRemoto.find(idOrden));
            mostrar = true;
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }


    /*
     * Modificación 06/Feb/2013 las iteraciones para disgreagar la relacion de
     * adjuntos a una requisicion ahora se realizan en el servicio
     */
    private boolean disgregarEtsRequisicion(int idRequisicion) {
        try {
            return servicioReRequisicion.disgregarAdjuntosPorRequisicion(idRequisicion, usuarioBean.getUsuarioConectado());
        } catch (Exception e) {
            FacesUtilsBean.addErrorMessage(ERR_OPERACION);
            LOGGER.info(this, "Excepcion al momento de disgregar las ETS de la requisicion " + e.getMessage());
            return false;
        }
    }

    public DataModel getHistorialNotas() {
        if (ordenActual != null) {
            return new ListDataModel(ocOrdenCoNoticiaImpl.traerTodasNoticiasPorOrden(ordenActual.getId(), true));
        }
        return null;
    }

    public void aprobarOrdenCompras() {
        List<OrdenVO> lo = new ArrayList<>();
        StringBuilder aprobadasSB = new StringBuilder();
        StringBuilder noAprobadasSB = new StringBuilder();
        try {
            if (getOrdenActual() == null) {
                try {
                    Preconditions.checkArgument(ocs.stream().anyMatch(OrdenVO::isSelected), "Seleccione al menos una orden de compra");
                    ocs.stream().filter(OrdenVO::isSelected).forEach(ordVo -> {
                        boolean v = ordenServicioRemoto.aprobarOrdenGerenciaSolicitante(ordVo.getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail());
                        if (v) {
                            if (aprobadasSB.toString().isEmpty()) {
                                aprobadasSB.append(ordVo.getConsecutivo());
                            } else {
                                aprobadasSB.append(", ").append(ordVo.getConsecutivo());
                            }
                        } else {
                            if (noAprobadasSB.toString().isEmpty()) {
                                noAprobadasSB.append(ordVo.getConsecutivo());
                            } else {
                                noAprobadasSB.append(", ").append(ordVo.getConsecutivo());
                            }
                            //noAprobadas += "  " + ordVo.getConsecutivo();
                            ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, "Visto Bueno", ordVo.getConsecutivo(), ordVo.getId());
                            LOGGER.fatal(this, "Ocurrio un error en el proceso de Visto Bueno a  la OC/S : ");
                        }
                    });

                    //mostrar los mensajes de error
                    mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                    mostrar = false;

                    String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
                    PrimeFaces.current().executeScript(jsMetodo);
                } catch (Exception e) {
                    FacesUtilsBean.addErrorMessage(e.getMessage());
                }
            } else {
                boolean v = ordenServicioRemoto.aprobarOrdenGerenciaSolicitante(getOrdenActual().getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail());
                if (v) {
                    if (aprobadasSB.toString().isEmpty()) {
                        aprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        aprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //aprobadas += "  " + getOrdenActual().getConsecutivo();
                    //FacesUtilsBean.addInfoMessage("Se enviaron las OC/S, seleccionadas . . . ");
                } else {
                    if (noAprobadasSB.toString().isEmpty()) {
                        noAprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        noAprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //noAprobadas += "  " + getOrdenActual().getConsecutivo();
                    ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, "Visto Bueno", getOrdenActual().getConsecutivo(), getOrdenActual().getId().intValue());
                    LOGGER.fatal(this, "Ocurrio un error en el proceso de Visto Bueno a  la OC/S : ");
                }
                //mostrar los mensajes de error
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;

                setOrdenActual(null);
                String jsMetodo = JS_METHOD_REGRESAR_DIV_AUTORIZA;
                PrimeFaces.current().executeScript(jsMetodo);
            }
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinAprobar();
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtilsBean.addInfoMessage(ERR_UNEXPECTED);
        }
    }

    public void devolverOrdenCompras() {
        List<OrdenVO> lo = new ArrayList<>();
        StringBuilder devolverSB = new StringBuilder();
        StringBuilder noDevolverSB = new StringBuilder();
        try {
            Orden ord1 = null;
            if (getOrdenActual() == null) {
                for (Object object : ordenesDevCan) {
                    OrdenVO ord = (OrdenVO) object;
                    if (ord.isSelected()) {
                        lo.add(ord);
                    }
                }
                for (OrdenVO ordVo : lo) {
                    boolean v = false;
                    ord1 = ordenServicioRemoto.buscarPorConsecutivo(ordVo.getConsecutivo());
                    if (this.motivoDevolucion != null && !this.motivoDevolucion.equals("")) {
                        v = ordenServicioRemoto.devolverOrden(ord1, usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getId(), this.motivoDevolucion);
                    }

                    if (v) {
                        if (devolverSB.toString().isEmpty()) {
                            devolverSB.append(ordVo.getConsecutivo());
                        } else {
                            devolverSB.append(", ").append(ordVo.getConsecutivo());
                        }
                        //aprobadas += "  " + ordVo.getConsecutivo();
                        //FacesUtilsBean.addInfoMessage("Se enviaron las OC/S, seleccionadas . . . ");
                    } else {
                        if (noDevolverSB.toString().isEmpty()) {
                            noDevolverSB.append(ordVo.getConsecutivo());
                        } else {
                            noDevolverSB.append(", ").append(ordVo.getConsecutivo());
                        }
                        //noAprobadas += "  " + ordVo.getConsecutivo();
                        ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, "Visto Bueno", ordVo.getConsecutivo(), ordVo.getId());
                        LOGGER.fatal(this, "Ocurrio un error en el proceso de devolución en Visto Bueno a  la OC/S : ");
                    }
                }
                //mostrar los mensajes de error
                mostrarMensajeDevolver(devolverSB.toString(), noDevolverSB.toString());
                mostrar = false;

                String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
                PrimeFaces.current().executeScript(jsMetodo);
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDevolverVariasOCS);");
            } else {
                boolean v = false;
                if (this.motivoDevolucion != null && !this.motivoDevolucion.equals("")) {
                    v = ordenServicioRemoto.devolverOrden(ord1, usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getId(), this.motivoDevolucion);
                }
                if (v) {
                    if (devolverSB.toString().isEmpty()) {
                        devolverSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        devolverSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //aprobadas += "  " + getOrdenActual().getConsecutivo();
                    //FacesUtilsBean.addInfoMessage("Se enviaron las OC/S, seleccionadas . . . ");
                } else {
                    if (noDevolverSB.toString().isEmpty()) {
                        noDevolverSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        noDevolverSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //noAprobadas += "  " + getOrdenActual().getConsecutivo();
                    ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, "Visto Bueno", getOrdenActual().getConsecutivo(), getOrdenActual().getId().intValue());
                    LOGGER.fatal(this, "Ocurrio un error en el proceso de Visto Bueno a  la OC/S : ");
                }
                //mostrar los mensajes de error
                mostrarMensajeDevolver(devolverSB.toString(), noDevolverSB.toString());
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDevolverVariasOCS);");
                //$(dialogoDevolverVariasOCS).modal('hide');
                mostrar = false;

                setOrdenActual(null);
                String jsMetodo = JS_METHOD_REGRESAR_DIV_AUTORIZA;
                PrimeFaces.current().executeScript(jsMetodo);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
            FacesUtilsBean.addInfoMessage(ERR_UNEXPECTED);
        }
    }

    public void inicioCancelarOrdenComprasVoBo() {
        try {
            ordenesDevCan = new ArrayList<>();
//            mapaOrdenes.get("VoBo").stream().filter(OrdenVO::isSelected).forEach(o -> {
//                ordenesDevCan.add(o);
//            });
            Preconditions.checkArgument(!ordenesDevCan.isEmpty(), "Es necesario seleccionar al menos una OC/S");
        } catch (IllegalArgumentException e) {
            FacesUtilsBean.addErrorMessage(e.getMessage());
        }
    }

    public void inicioDevolverOrdenCompras() {
        if (ocs.stream().anyMatch(OrdenVO::isSelected)) {
            ordenesDevCan = new ArrayList<>();
            ocs.stream().filter(OrdenVO::isSelected).forEach(ordenesDevCan::add);
            //
            PrimeFaces.current().executeScript("$(dialogoDevolverVariasOCS).modal('show');");
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario seleccionar al menos una OC/S");
        }
    }

    public void inicioCancelarOrdenCompras() {
        if (ocs.stream().anyMatch(OrdenVO::isSelected)) {
            ordenesDevCan = new ArrayList<>();
            ocs.stream().filter(OrdenVO::isSelected).forEach(ordenesDevCan::add);
            //
            PrimeFaces.current().executeScript("$(dialogoCancelarVariasOCS).modal('show');");
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario seleccionar al menos una OC/S");
        }
    }

    public void cancelarOrdenCompras() {
        List<OrdenVO> lo = new ArrayList<>();
        StringBuilder devolverSB = new StringBuilder();
        StringBuilder noDevolverSB = new StringBuilder();
        try {
            Orden ord1 = null;
            if (getOrdenActual() == null) {
                for (OrdenVO ord : ordenesDevCan) {
                    if (ord.isSelected()) {
                        lo.add(ord);
                    }
                }
                for (OrdenVO ordVo : lo) {
                    boolean v = false;
                    ord1 = ordenServicioRemoto.buscarPorConsecutivo(ordVo.getConsecutivo());
                    if (!Strings.isNullOrEmpty(this.motivoDevolucion)) {
                        v
                                = ordenServicioRemoto.cancelarOrden(
                                        ord1,
                                        usuarioBean.getUsuarioConectado().getNombre(),
                                        usuarioBean.getUsuarioConectado().getId(),
                                        this.motivoDevolucion,
                                        Constantes.FALSE, Constantes.FALSE
                                );

                    }

                    if (v) {
                        if (devolverSB.toString().isEmpty()) {
                            devolverSB.append(ordVo.getConsecutivo());
                        } else {
                            devolverSB.append(", ").append(ordVo.getConsecutivo());
                        }
                        //aprobadas += "  " + ordVo.getConsecutivo();
                        //FacesUtilsBean.addInfoMessage("Se enviaron las OC/S, seleccionadas . . . ");
                    } else {
                        if (noDevolverSB.toString().isEmpty()) {
                            noDevolverSB.append(ordVo.getConsecutivo());
                        } else {
                            noDevolverSB.append(", ").append(ordVo.getConsecutivo());
                        }
                        //noAprobadas += "  " + ordVo.getConsecutivo();
                        ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, "Visto Bueno", ordVo.getConsecutivo(), ordVo.getId());
                        LOGGER.fatal(this, "Ocurrio un error en el proceso de devolución en Visto Bueno a  la OC/S : ");
                    }
                }
                //mostrar los mensajes de error
                mostrarMensajeCancelar(devolverSB.toString(), noDevolverSB.toString());
                mostrar = false;

                String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
                ordenesDevCan = new ArrayList<>();
                PrimeFaces.current().executeScript(jsMetodo);
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCancelarVariasOCS);");
            } else {
                boolean v = false;
                if (!Strings.isNullOrEmpty(motivoDevolucion)) {
                    //FIXME : ord1 es nulo en este punto
                    v
                            = ordenServicioRemoto.cancelarOrden(
                                    ord1,
                                    ord1.getAnalista().getNombre(),
                                    usuarioBean.getUsuarioConectado().getId(),
                                    this.motivoDevolucion,
                                    Constantes.FALSE,
                                    Constantes.FALSE
                            );
                }
                if (v) {
                    if (devolverSB.toString().isEmpty()) {
                        devolverSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        devolverSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }

                } else {
                    if (noDevolverSB.toString().isEmpty()) {
                        noDevolverSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        noDevolverSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //noAprobadas += "  " + getOrdenActual().getConsecutivo();
                    ordenServicioRemoto.enviarExcepcionSia(
                            usuarioBean.getUsuarioConectado().getId(),
                            correoExcepcion(), MSG_COMPRAS,
                            "Visto Bueno",
                            getOrdenActual().getConsecutivo(),
                            getOrdenActual().getId().intValue()
                    );

                    LOGGER.fatal(this, "Ocurrio un error en el proceso de Visto Bueno a  la OC/S : ");
                }
                //mostrar los mensajes de error
                mostrarMensajeCancelar(devolverSB.toString(), noDevolverSB.toString());
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCancelarVariasOCS);");
                //$(dialogoDevolverVariasOCS).modal('hide');
                mostrar = false;

                setOrdenActual(null);
                String jsMetodo = JS_METHOD_REGRESAR_DIV_AUTORIZA;
                PrimeFaces.current().executeScript(jsMetodo);
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
            FacesUtilsBean.addInfoMessage(ERR_UNEXPECTED);
        }
    }

    public void aprobarOrdenComprasRevisa() {
        StringBuilder aprobadasSB = new StringBuilder();
        StringBuilder noAprobadasSB = new StringBuilder();

        List<OrdenVO> lo = new ArrayList<>();
        try {
            if (getOrdenActual() == null) {
//                for (Object object : mapaOrdenes.get("revisaOrdenes")) {
//                    OrdenVO ord = (OrdenVO) object;
//                    if (ord.isSelected()) {
//                        lo.add(ord);
//                    }
//                }
                for (OrdenVO ordVo : lo) {
                    boolean v
                            = ordenServicioRemoto.autorizarOrdenMPG(
                                    ordVo.getId(),
                                    usuarioBean.getUsuarioConectado().getId(),
                                    usuarioBean.getUsuarioConectado().getEmail()
                            );

                    if (v) {
                        if (aprobadasSB.toString().isEmpty()) {
                            aprobadasSB.append(ordVo.getConsecutivo());
                        } else {
                            aprobadasSB.append(", ").append(ordVo.getConsecutivo());
                        }
                        //aprobadas += ordVo.getConsecutivo();
                    } else {
                        if (noAprobadasSB.toString().isEmpty()) {
                            noAprobadasSB.append(ordVo.getConsecutivo());
                        } else {
                            noAprobadasSB.append(", ").append(ordVo.getConsecutivo());
                        }
                        //noAprobadas += ordVo.getConsecutivo();
                        ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, "Revisa", ordVo.getConsecutivo(), ordVo.getId());
                        LOGGER.fatal(this, ERR_MSG_OCS);
                    }
                }
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;

                String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                boolean v = ordenServicioRemoto.autorizarOrdenMPG(getOrdenActual().getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail());
                if (v) {
                    if (aprobadasSB.toString().isEmpty()) {
                        aprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        aprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //aprobadas += getOrdenActual().getConsecutivo();
                } else {
                    if (noAprobadasSB.toString().isEmpty()) {
                        noAprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        noAprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //noAprobadas += getOrdenActual().getConsecutivo();
                    ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, "Revisa", getOrdenActual().getConsecutivo(), getOrdenActual().getId().intValue());
                    LOGGER.fatal(this, ERR_MSG_OCS);
                }
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;

                setOrdenActual(null);
                String jsMetodo = JS_METHOD_REGRESAR_DIV_AUTORIZA;
                PrimeFaces.current().executeScript(jsMetodo);
            }
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinAutorizarMPG();
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtilsBean.addInfoMessage(ERR_UNEXPECTED);
        }
    }

    public void aprobarOrdenComprasAprueba() {
        try {
            StringBuilder aprobadasSB = new StringBuilder();
            StringBuilder noAprobadasSB = new StringBuilder();
            List<OrdenVO> lo = new ArrayList<>();
            if (getOrdenActual() == null) {
//                for (Object object : mapaOrdenes.get("aprobarOrdenes")) {
//                    OrdenVO ord = (OrdenVO) object;
//                    if (ord.isSelected()) {
//                        lo.add(ord);
//                    }
//                }
                boolean v;
                for (OrdenVO ordVO : lo) {
                    v = ordenServicioRemoto.autorizarOrdenIHSA(ordVO.getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail());
                    if (v) {
                        if (aprobadasSB.toString().isEmpty()) {
                            aprobadasSB.append(ordVO.getConsecutivo());
                        } else {
                            aprobadasSB.append(", ").append(ordVO.getConsecutivo());
                        }
                        //aprobadas += " " + ordVO.getConsecutivo();
                    } else {
                        if (noAprobadasSB.toString().isEmpty()) {
                            noAprobadasSB.append(ordVO.getConsecutivo());
                        } else {
                            noAprobadasSB.append(", ").append(ordVO.getConsecutivo());
                        }
                        //noAprobadas += "  " + ordVO.getConsecutivo();
                        ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, MSG_APROBAR, ordVO.getConsecutivo(), ordVO.getId());
                        LOGGER.fatal(this, ERR_MSG_OCS);
                    }
                }
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;

                String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                boolean v = ordenServicioRemoto.autorizarOrdenIHSA(getOrdenActual().getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail());
                if (v) {
                    if (aprobadasSB.toString().isEmpty()) {
                        aprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        aprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //aprobadas += " " + getOrdenActual().getConsecutivo();
                } else {
                    if (noAprobadasSB.toString().isEmpty()) {
                        noAprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        noAprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //noAprobadas += "  " + getOrdenActual().getConsecutivo();
                    ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, MSG_APROBAR, getOrdenActual().getConsecutivo(), getOrdenActual().getId().intValue());
                    LOGGER.fatal(this, ERR_MSG_OCS);
                }
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;

                setOrdenActual(null);
                String jsMetodo = JS_METHOD_REGRESAR_DIV_AUTORIZA;
                PrimeFaces.current().executeScript(jsMetodo);
            }
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinAutorizarIHSA();
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtilsBean.addInfoMessage(ERR_UNEXPECTED);
        }
    }

    public void aprobarOrdenComprasFinanzas() {
        StringBuilder aprobadasSB = new StringBuilder();
        StringBuilder noAprobadasSB = new StringBuilder();
        List<OrdenVO> lo = new ArrayList<>();
//        for (OrdenVO ord : mapaOrdenes.get("ordenesFinanzas")) {
//            if (ord.isSelected()) {
//                lo.add(ord);
//            }
//        }
        boolean v;
        for (OrdenVO ordVO : lo) {
            v = ordenServicioRemoto.autorizarOrdenIHSA(ordVO.getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail());
            if (v) {
                if (aprobadasSB.toString().isEmpty()) {
                    aprobadasSB.append(ordVO.getConsecutivo());
                } else {
                    aprobadasSB.append(", ").append(ordVO.getConsecutivo());
                }
                //aprobadas += "  " + ordVO.getConsecutivo();
            } else {
                if (noAprobadasSB.toString().isEmpty()) {
                    noAprobadasSB.append(ordVO.getConsecutivo());
                } else {
                    noAprobadasSB.append(", ").append(ordVO.getConsecutivo());
                }
                //noAprobadas += "  " + ordVO.getConsecutivo();
                ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, MSG_APROBAR, ordVO.getConsecutivo(), ordVO.getId());
                LOGGER.fatal(this, ERR_MSG_OCS);

            }
        }
        //
        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        contarBean.llenarOcsSinAutoFinanzas();
        mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
        mostrar = false;

    }

    public void aprobarOrdenComprasSocio() {
        StringBuilder aprobadasSB = new StringBuilder();
        StringBuilder noAprobadasSB = new StringBuilder();
        List<OrdenVO> lo = new ArrayList<>();
        try {
            if (getOrdenActual() == null) {
//                for (OrdenVO ord : mapaOrdenes.get("aprobarSocio")) {
//                    if (ord.isSelected()) {
//                        lo.add(ord);
//                    }
//                }
                boolean v;
                for (OrdenVO ordVo : lo) {
                    v = ordenServicioRemoto.autorizarOrdenSocio(ordVo.getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail());
                    if (v) {
                        if (aprobadasSB.toString().isEmpty()) {
                            aprobadasSB.append(ordVo.getConsecutivo());
                        } else {
                            aprobadasSB.append(", ").append(ordVo.getConsecutivo());
                        }
                        //aprobadas += "   " + ordVo.getConsecutivo();
                    } else {
                        if (noAprobadasSB.toString().isEmpty()) {
                            noAprobadasSB.append(ordVo.getConsecutivo());
                        } else {
                            noAprobadasSB.append(", ").append(ordVo.getConsecutivo());
                        }
                        //noAprobadas += "   " + ordVo.getConsecutivo();
                        ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, MSG_APROBAR, ordVo.getConsecutivo(), ordVo.getId());
                        LOGGER.fatal(this, ERR_MSG_OCS);
                    }
                }
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;

                String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                boolean v = ordenServicioRemoto.autorizarOrdenSocio(getOrdenActual().getId().intValue(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail());
                if (v) {
                    if (aprobadasSB.toString().isEmpty()) {
                        aprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        aprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //aprobadas += "   " + getOrdenActual().getConsecutivo();
                } else {
                    if (noAprobadasSB.toString().isEmpty()) {
                        noAprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        noAprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //noAprobadas += "   " + getOrdenActual().getConsecutivo();
                    ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), correoExcepcion(), MSG_COMPRAS, MSG_APROBAR, getOrdenActual().getConsecutivo(), getOrdenActual().getId().intValue());
                    LOGGER.fatal(this, ERR_MSG_OCS);
                }
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;

                setOrdenActual(null);
                String jsMetodo = JS_METHOD_REGRESAR_DIV_AUTORIZA;
                PrimeFaces.current().executeScript(jsMetodo);
            }
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinAutoSocio();
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtilsBean.addInfoMessage(ERR_UNEXPECTED);
        }
    }

    public void autorizarOrdenCompras() {
        StringBuilder aprobadasSB = new StringBuilder();
        StringBuilder noAprobadasSB = new StringBuilder();
        List<OrdenVO> lo = new ArrayList<>();
        try {
            if (getOrdenActual() == null) {
                ocs.stream().filter(OrdenVO::isSelected).forEach(oc -> {
                    lo.add(oc);
                });
                boolean v;
                //OC/S nuevas
                if (!lo.isEmpty()) {
                    boolean val;
                    for (OrdenVO ord : lo) {
                        val = ordenServicioRemoto.autorizarTareaCompra(ord.getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail(), Constantes.FALSE);
                        if (val) {
                            if (aprobadasSB.toString().isEmpty()) {
                                aprobadasSB.append(ord.getConsecutivo());
                            } else {
                                aprobadasSB.append(", ").append(ord.getConsecutivo());
                            }
                            //
                        } else {
                            if (noAprobadasSB.toString().isEmpty()) {
                                noAprobadasSB.append(ord.getConsecutivo());
                            } else {
                                noAprobadasSB.append(", ").append(ord.getConsecutivo());
                            }
                            //
                            ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail(), MSG_COMPRAS, MSG_APROBAR, ord.getConsecutivo(), ord.getId());
                            LOGGER.fatal(this, ERR_MSG_OCS);
                        }
                    }
                }
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;
                //Linea para recargar la lista de OC/S
                String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                boolean val = ordenServicioRemoto.autorizarTareaCompra(getOrdenActual().getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail(), Constantes.FALSE);
                if (val) {
                    if (aprobadasSB.toString().isEmpty()) {
                        aprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        aprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //
                } else {
                    if (noAprobadasSB.toString().isEmpty()) {
                        noAprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        noAprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //
                    ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail(), MSG_COMPRAS, MSG_APROBAR, getOrdenActual().getConsecutivo(), getOrdenActual().getId().intValue());
                    LOGGER.fatal(this, ERR_MSG_OCS);
                }

                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;
                //Linea para recargar la lista de OC/S
                setOrdenActual(null);
                String jsMetodo = JS_METHOD_REGRESAR_DIV_AUTO;
                PrimeFaces.current().executeScript(jsMetodo);
            }
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinAutorizaCompras();
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtilsBean.addInfoMessage(ERR_UNEXPECTED);
        }
    }

    public void autorizarLicitacion() {
        StringBuilder aprobadasSB = new StringBuilder();
        StringBuilder noAprobadasSB = new StringBuilder();
        List<OrdenVO> lo = new ArrayList<>();

        try {
            if (getOrdenActual() == null) {
//                for (Object object : mapaOrdenes.get("autorizaLicitacion")) {
//                    OrdenVO ord = (OrdenVO) object;
//                    if (ord.isSelected()) {
//                        lo.add(ord);
//                    }
//                }
                //OC/S nuevas
                if (!lo.isEmpty()) {
                    boolean val;
                    for (OrdenVO ord : lo) {
                        val = ordenServicioRemoto.enviarCompraLicitacion(ord.getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail(), Constantes.FALSE);
                        if (val) {
                            if (aprobadasSB.toString().isEmpty()) {
                                aprobadasSB.append(ord.getConsecutivo());
                            } else {
                                aprobadasSB.append(", ").append(ord.getConsecutivo());
                            }
                            //
                        } else {
                            if (noAprobadasSB.toString().isEmpty()) {
                                noAprobadasSB.append(ord.getConsecutivo());
                            } else {
                                noAprobadasSB.append(", ").append(ord.getConsecutivo());
                            }
                            //
                            ordenServicioRemoto.enviarExcepcionSia(
                                    usuarioBean.getUsuarioConectado().getId(),
                                    usuarioBean.getUsuarioConectado().getEmail(),
                                    MSG_COMPRAS,
                                    MSG_APROBAR,
                                    ord.getConsecutivo(),
                                    ord.getId()
                            );

                            LOGGER.fatal(this, ERR_MSG_OCS);
                        }
                    }
                }
                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;
                //Linea para recargar la lista de OC/S
                String jsMetodo = JS_METHOD_LIMPIAR_TODOS;
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                boolean val = ordenServicioRemoto.enviarCompraLicitacion(getOrdenActual().getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail(), Constantes.FALSE);
                if (val) {
                    if (aprobadasSB.toString().isEmpty()) {
                        aprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        aprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //
                } else {
                    if (noAprobadasSB.toString().isEmpty()) {
                        noAprobadasSB.append(getOrdenActual().getConsecutivo());
                    } else {
                        noAprobadasSB.append(", ").append(getOrdenActual().getConsecutivo());
                    }
                    //
                    ordenServicioRemoto.enviarExcepcionSia(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail(), MSG_COMPRAS, MSG_APROBAR, getOrdenActual().getConsecutivo(), getOrdenActual().getId().intValue());
                    LOGGER.fatal(this, ERR_MSG_OCS);
                }

                mostrarMensaje(aprobadasSB.toString(), noAprobadasSB.toString());
                mostrar = false;
                //Linea para recargar la lista de OC/S
                setOrdenActual(null);
                String jsMetodo = JS_METHOD_REGRESAR_DIV_AUTO;
                PrimeFaces.current().executeScript(jsMetodo);
            }
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinAutoComprasLicita();
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage());
            FacesUtilsBean.addInfoMessage(ERR_UNEXPECTED);
        }
    }

    private void mostrarMensaje(String ocsAprobadas, String ocsNoAprobadas) {
        if (!Strings.isNullOrEmpty(ocsAprobadas)) {
            FacesUtilsBean.addInfoMessage("Se Aprobaron la(s) OC/S: " + ocsAprobadas);
        }

        if (Strings.isNullOrEmpty(ocsNoAprobadas)) {
            PrimeFaces.current().executeScript(
                    ";ocultarDiv('divMensajeError');"
            );
        } else {
            PrimeFaces.current().executeScript(
                    ";mostrarMensajeError('divMensajeError','mensajeError','"
                    + "NO Aprobaron la(s) OC/S: "
                    + ocsNoAprobadas + "');"
            );
        }
    }

    private void mostrarMensajeDevolver(String ocsAprobadas, String ocsNoAprobadas) {
        if (!Strings.isNullOrEmpty(ocsAprobadas)) {
            FacesUtilsBean.addInfoMessage("OC/S devuelta(s): " + ocsAprobadas);
        }

        if (Strings.isNullOrEmpty(ocsNoAprobadas)) {
            PrimeFaces.current().executeScript(
                    ";ocultarDiv('divMensajeError');"
            );
        } else {
            PrimeFaces.current().executeScript(
                    ";mostrarMensajeError('divMensajeError','mensajeError','"
                    + "NO Devuelta(s) la(s) OC/S: "
                    + ocsNoAprobadas + "');"
            );
        }
    }

    private void mostrarMensajeCancelar(String ocsAprobadas, String ocsNoAprobadas) {
        if (!Strings.isNullOrEmpty(ocsAprobadas)) {
            FacesUtilsBean.addInfoMessage("OC/S Cancelada(s): " + ocsAprobadas);
        }

        if (Strings.isNullOrEmpty(ocsNoAprobadas)) {
            PrimeFaces.current().executeScript(
                    ";ocultarDiv('divMensajeError');"
            );
        } else {
            PrimeFaces.current().executeScript(
                    ";mostrarMensajeError('divMensajeError','mensajeError','"
                    + "NO Devuelta(s) la(s) OC/S: "
                    + ocsNoAprobadas + "');"
            );
        }
    }

    private String correoExcepcion() {
        List<UsuarioRolVo> ur = siUsuarioRolImpl.traerRolPorCodigo(Constantes.ROL_DESARROLLO_SISTEMA, Constantes.AP_CAMPO_DEFAULT, Constantes.MODULO_COMPRA);
        StringBuilder correo = new StringBuilder();

        if (ur == null || ur.isEmpty()) {
            correo.append("siaihsa@ihsa.mx");
        } else {
            for (UsuarioRolVo usuarioRolVo : ur) {

                if (correo.length() > 0) {
                    correo.append(',');
                }

                correo.append(usuarioRolVo.getCorreo());
            }
        }
        return correo.toString();
    }

    public void cancelarOrden(int idOrden) {
        this.setOrdenActual(ordenServicioRemoto.find(idOrden));
        notaOrdenBean.setFiltrar(false);

    }

    public void completarOrden(int idOrden) {
        this.setOrdenActual(ordenServicioRemoto.find(idOrden));
        //  this.popupCompletarOrdenBean.toggleModal(actionEvent);
    }

    public void completarOrdenAF(int idOrden) {
        this.setOrdenActual(ordenServicioRemoto.find(idOrden));
        if (ocActivoFijoImpl.afCompletos(this.getOrdenActual().getId()) <= 0) {

            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoEnviarProveedor);");
            //this.popupCompletarOrdenBean.toggleModal(actionEvent);
        } else {
            FacesUtilsBean.addErrorMessage("Se require que se capturen todos los códigos de los activos fijos de NAVISION.");
        }
    }

    public void limpiarOrdenExcel(int idOrden) {
        try {
            this.setOrdenActual(ordenServicioRemoto.find(idOrden));
            AutorizacionesOrden autorizacionesOrden = this.autorizacionesOrdenImpl.buscarPorOrden(getOrdenActual().getId());
            autorizacionesOrden.setEstatus(this.estatusImpl.find(Constantes.ESTATUS_ENVIADA_PROVEEDOR)); // 160
            this.autorizacionesOrdenImpl.editar(autorizacionesOrden);
            //Linea para actualizar la lista de OC/S
        } catch (Exception e) {
            LOGGER.info(this, e.getMessage());
            FacesUtilsBean.addErrorMessage(ERR_OPERACION);
        }
    }

    public void terminarOrdenExcel() {
        try {
            AutorizacionesOrden autorizacionesOrden = autorizacionesOrdenImpl.buscarPorOrden(getOrdenActual().getId());
            autorizacionesOrden.setEstatus(new Estatus(Constantes.ESTATUS_ENVIADA_PROVEEDOR)); // 160
            autorizacionesOrdenImpl.editar(autorizacionesOrden);
            setOrdenActual(null);
//	    setIdUnidad(0);
            //Linea para actualizar la lista de OC/S
        } catch (Exception e) {
            setOrdenActual(null);
            LOGGER.info(this, e.getMessage());
            FacesUtilsBean.addErrorMessage(ERR_OPERACION);
        }
    }

    public void completarOrdenOP(int idOrden) {
        this.setOrdenActual(ordenServicioRemoto.find(idOrden));
        if ((getOrdenActual().getNavCode() == null || this.getOrdenActual().getNavCode().isEmpty())
                && this.ordenServicioRemoto.productosLineasGuadados(getOrdenActual().getId())) {
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoEnviarProveedor);");
            //this.popupCompletarOrdenBean.toggleModal(actionEvent);
        } else {
            FacesUtilsBean.addErrorMessage("Se require que se capturen todos los códigos de los Productos para NAVISION.");
        }
    }

    public void detActivosFijo(int id) {
        OrdenDetalleVO ordenDetVO = getListaItems().get(id);
        List<OcActivoFijoVO> actFijoLst = new ArrayList<>();
        actFijoLst.addAll(ocActivoFijoImpl.getDetActivoFijo(ordenActual.getId(), ordenDetVO.getId()));
        actFijoLst.addAll(crearLista(((int) ordenDetVO.getCantidad() - actFijoLst.size()), ordenActual.getId(), ordenDetVO.getId()));
        ordenDetVO.setNavCodes(actFijoLst);
        popupCompletarActivoFijoBean.setApplyAll(false);
        popupCompletarActivoFijoBean.setLinea(ordenDetVO);
        PrimeFaces.current().executeScript("$(dialogoPedidoActivo).modal('show');");

    }

    public void detAFPS() {
        if (getOrdenActual() != null && TipoRequisicion.PS.equals(getOrdenActual().getTipo())) {
            this.popupCompletarActivoFijoBean.setOcProductos(getOcProductos());
            this.popupCompletarActivoFijoBean.setPopUpocProductoID(0);
        } else {
            this.popupCompletarActivoFijoBean.setAfValue(Constantes.VACIO);
        }
        this.popupCompletarActivoFijoBean.setApplyAll(true);
    }

    public void completarCancelarOrden() throws SIAException, Exception {
        try {
            //disgregar las ETS de la requicision
            this.ordenServicioRemoto.cancelarOrden(this.getOrdenActual(), this.usuarioBean.getUsuarioConectado().getNombre(), this.usuarioBean.getUsuarioConectado().getId(), this.motivoCancelacion, false, false);
            disgregarEtsRequisicion(this.getOrdenActual().getRequisicion().getId());
            //Finaliza las noticias
            finalizarNoticiaOrden(usuarioBean.getUsuarioConectado().getId(), getOrdenActual().getId());
            //Linea para actualizar la lista de OC/S
            FacesUtilsBean.addInfoMessage("Se canceló correctamente la orden de compra y/o servicio");
            quitarSeleccionOrden(false);
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCancelOCS);");
            //this.popupCancelarOrdenBean.toggleModal(actionEvent);
            //Regresa a la tabla
            PrimeFaces.current().executeScript(JS_METHOD_REGRESAR_DIV_AUTO);
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOCSDevueltas();
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            FacesUtilsBean.addErrorMessage(ERR_OPERACION);
        }
    }

    private void finalizarNoticiaOrden(String sesion, int idOrden) {
        ocOrdenCoNoticiaImpl.finalizarNoticia(sesion, idOrden);
    }

    private boolean validarPrefijoNavCode(String navCode, Compania compania) {
        boolean navCodeCorrecto = false;
        String regexp = compania.getNavPrefijo() + "\\d+";

        if (!Strings.isNullOrEmpty(navCode) && compania != null) {
            navCodeCorrecto
                    = (compania.getNavPrefijo() == null
                    || compania.getNavPrefijo().isEmpty()
                    || (navCode.startsWith(compania.getNavPrefijo())
                    && Pattern.matches(regexp, navCode)));
        }

        return navCodeCorrecto;
    }

    public void completarTarea() {
        Orden oldOrden = null;
        try {
            oldOrden = ordenServicioRemoto.find(this.getOrdenActual().getId());

            if (validarPrefijoNavCode(getOrdenActual().getNavCode(), getOrdenActual().getCompania())
                    && getOrdenActual().getNavCode() != null
                    && !getOrdenActual().getNavCode().isEmpty()
                    && !ordenServicioRemoto.existeNavCode(getOrdenActual().getNavCode())) {
                contactos = ordenServicioRemoto.getContactosVo(getOrdenActual().getId());

                if (getContactos() == null || getContactos().isEmpty()) {
                    LOGGER.info(this, " # # # # # # No se autorizó la OC/S: " + getOrdenActual().getConsecutivo() + ", ========NO TIENE CONTACOS==========");

                    FacesUtilsBean.addErrorMessage(
                            "No se pudo enviar la orden de compra y/o servicio: "
                            + getOrdenActual().getConsecutivo()
                            + ". El proveedor no tiene contactos registrados. Favor de actualizar los contactos del proveedor."
                    );
                } else {
                    ordenServicioRemoto.editarOrden(getOrdenActual());
                    //OrdenVO ordVo = ordenServicioRemoto.buscarOrdenPorId(getOrdenActual().getId(), ordenActual.getApCampo().getId(), false);
                    if (ordenServicioRemoto.autorizarOrdenCompras(this.getOrdenActual().getId(), usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getEmail())) {
                        LOGGER.info(this, "Se autorizó y envió la OC/S: " + getOrdenActual().getConsecutivo());
                        //Linea para actualizar la lista de OC/S
                        FacesUtilsBean.addInfoMessage("Se guardo correctamente la orden de compra y/o servicio");
                        this.quitarSeleccionOrden(false);
                    } else {
                        FacesUtilsBean.addErrorMessage("No se pudo autorizar la orden de compra y/o servicio, favor de contactar al equipo de soporte. ");
                    }
                }
            } else {
                if (this.getOrdenActual().getNavCode() == null || this.getOrdenActual().getNavCode().isEmpty()) {
                    FacesUtilsBean.addErrorMessage("Se require que se capture el código del pedido de NAVISION");
                } else if (!validarPrefijoNavCode(this.getOrdenActual().getNavCode(), this.getOrdenActual().getCompania())) {
                    FacesUtilsBean.addErrorMessage(new StringBuilder().append("Se require que se capture un código del pedido de NAVISION correcto: Prefijo - ").append(this.getOrdenActual().getCompania().getNavPrefijo()).append(". ").toString());
                } else if (TipoRequisicion.AF.equals(this.getOrdenActual().getTipo()) && ocActivoFijoImpl.afCompletos(this.getOrdenActual().getId()) != 0) {
                    FacesUtilsBean.addErrorMessage("Se require que se capturen todos los códigos de los activos fijos de NAVISION.");
                } else if (this.getOrdenActual().getNavCode() != null && !this.getOrdenActual().getNavCode().isEmpty()
                        && ordenServicioRemoto.existeNavCode(this.getOrdenActual().getNavCode())) {
                    FacesUtilsBean.addErrorMessage("El código del pedido de NAVISION ya existe en el SIA, es necesario proporcionar otro.");
                }
            }
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsEnivarProveedor();
            //

        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            if (oldOrden != null && oldOrden.getId() > 0) {
                ordenServicioRemoto.editarOrden(oldOrden);
            }
            FacesUtilsBean.addErrorMessage(ex.getMessage());
        }
        PrimeFaces.current().executeScript(";cerrarEnvioPDF();");
    }

    public void completarActivosFijo() throws SIAException, Exception {
        try {
            if (ocActivoFijoImpl.unicActivosFijo(ordenActual, popupCompletarActivoFijoBean.getLinea().getNavCodes())) {
                Usuario usrConectado = this.usuarioBean.getUsuarioConectado();
                OrdenDetalle linea = ordenDetalleImpl.find(popupCompletarActivoFijoBean.getLinea().getId());
                ocActivoFijoImpl.completarActivosFijo(ordenActual, linea, usrConectado, popupCompletarActivoFijoBean.getLinea().getNavCodes());
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoPedidoActivo);");
                //  popupCompletarActivoFijoBean.toggleModal(actionEvent);
            } else {
                FacesUtilsBean.addErrorMessage("Los códigos de los activos fijos deben de ser unicos en el SIA. Ya existe uno de los códigos capturados, es necesario proporcionar otro.");
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            FacesUtilsBean.addErrorMessage(ERR_OPERACION);
        }
    }

    public void completarAFPS() throws SIAException, Exception {
        try {
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            if (TipoRequisicion.AF.equals(ordenActual.getTipo())) {
                if (popupCompletarActivoFijoBean.getAfValue() != null && !popupCompletarActivoFijoBean.getAfValue().isEmpty()) {
                    List<OrdenDetalle> lstDet = ordenDetalleImpl.getItemsPorOrden(ordenActual.getId());
                    List<OcActivoFijoVO> lstAF = ocActivoFijoImpl.getDetActivoFijo(ordenActual.getId(), 0);

                    List<OcActivoFijoVO> navCodes = new ArrayList<>();
                    OcActivoFijoVO vo = new OcActivoFijoVO();
                    vo.setOrdenId(ordenActual.getId());
                    vo.setCodigo(popupCompletarActivoFijoBean.getAfValue());
                    navCodes.add(vo);

                    Usuario usrConectado = this.usuarioBean.getUsuarioConectado();

                    if (ocActivoFijoImpl.unicActivosFijo(ordenActual, navCodes)) {
                        if (lstDet != null && !lstDet.isEmpty()) {
                            if (lstAF.isEmpty()) {
                                ocActivoFijoImpl.allNewAF(ordenActual, usrConectado, lstDet, vo);
                            } else {
                                ocActivoFijoImpl.newUpdateAF(ordenActual, usrConectado, lstDet, lstAF, vo);
                            }
                        }
                    } else {
                        FacesUtilsBean.addErrorMessage("Los códigos de los activos fijos deben de ser unicos en el SIA. Ya existe uno de los códigos capturados, es necesario proporcionar otro.");
                    }
                }
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoPedidoActivo);");
                //popupCompletarActivoFijoBean.toggleModal(actionEvent);
            } else if (TipoRequisicion.PS.equals(ordenActual.getTipo())) {
                if (popupCompletarActivoFijoBean.getPopUpocProductoID() > 0) {
                    OcProductoCompania ocProducto = ocProductoCompaniaImpl.find(popupCompletarActivoFijoBean.getPopUpocProductoID());
                    if (ocProducto != null && ocProducto.getId() > 0) {
                        List<OrdenDetalle> lstDet = ordenDetalleImpl.getItemsPorOrden(ordenActual.getId());
                        for (OrdenDetalle detObj : lstDet) {
                            detObj.setOcProductoCompania(ocProducto);
                            ordenDetalleImpl.editar(detObj);
                        }
                        List<OrdenDetalleVO> lo = null;
                        if (ordenActual.isMultiproyecto()) {
                            lo = this.ordenServicioRemoto.itemsPorOrdenCompraMulti(getOrdenActual().getId());
                        } else {
                            lo = this.ordenServicioRemoto.itemsPorOrdenCompra(getOrdenActual().getId());
                        }
                        this.setListaItems((lo));
                    }
                }
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoPedidoActivo);");
            }
            //            
            contarBean.llenarOcsEnivarProveedor();
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            FacesUtilsBean.addErrorMessage(ERR_OPERACION);
        }
    }

    public void devolverOrden(int idOrden) {
        this.setOrdenActual(ordenServicioRemoto.find(idOrden));
        notaOrdenBean.setFiltrar(false);

    }

    public void completarDevolverOrden() throws SIAException, Exception {
        try {
            this.ordenServicioRemoto.devolverOrden(this.getOrdenActual(), this.usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getId(), this.motivoDevolucion);
            //Finaliza las noticias
            finalizarNoticiaOrden(usuarioBean.getUsuarioConectado().getId(), getOrdenActual().getId());
            //Linea para actualizar la lista de OC/S
            quitarSeleccionOrden(false);
            FacesUtilsBean.addInfoMessage("Se devolvió correctamente la orden de compra y/o servicio");
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDevolverOCS);");
            //
            //Regresa a la tabla
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOCSDevueltas();
            PrimeFaces.current().executeScript(JS_METHOD_REGRESAR_DIV_AUTO);

        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
            FacesUtilsBean.addErrorMessage(ERR_OPERACION);
        }
    }

    public void quitarSeleccionOrden(boolean limpiar) {
        // true quita la orden seleccionada y limpia la pantalla
        // false selecciona la ultima orden d la lista si es q hay si no limpia todo...
        setOrdenActual(ordenServicioRemoto.find(0));

        List<OrdenDetalleVO> lo = new ArrayList<OrdenDetalleVO>();

        setListaItems((lo));

        setConsecutivo(Constantes.VACIO);
        mostrar = false;
    }

//    public void irSolicitarOrden() {
//        menuBarBean.procesarAccion(ACTION_ORDEN_COMPRA, ";activarTab('tabOCSProc',0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');");
//    }
    public void irEts() {
        this.menuBarBean.procesarAccion("Ets");
    }
    //Valida Mails

    public boolean validaMail(String correo) {
        String[] mails = correo.split(",");
        boolean v = false;
        for (String string : mails) {
            if (this.mail(string.trim())) {
                v = true;
            } else {
                v = false;
                break;
            }
        }
        return v;
    }
    //metodo para validar correo electronio

    public boolean mail(String correo) {
        boolean v = false;
        Pattern pat = null;
        Matcher mat = null;
        pat = Pattern.compile("^[\\w-\\.]+\\@[\\w\\.-]+\\.[a-z]{2,4}$");
        mat = pat.matcher(correo);
        if (mat.find()) {
            v = true;
        }
        return v;
    }

    /**
     * ***** Historial de Ordenes de compra
     */
    //
    public void llenarProyectoOT() {
        String proyectosOT = proyectoOtBean.traerProyectoOTJson(usuarioBean.getCompania().getRfc());
        //LOGGER.info(this, "pots: " + proyectosOT);
        PrimeFaces.current().executeScript(";setJson(" + proyectosOT + ");");
    }

    public void llenarProveedores() {
        String p = proveedorBean.traerProveedorJson(usuarioBean.getCompania().getRfc());
        //  LOGGER.info(this, "provee: " + p);
        PrimeFaces.current().executeScript(";setJsonProveedor(" + p + ");");
    }

    public void limpiarListaOrdenesSolicitadas() {
        // mapaOrdenes.put("ordenesSolicitadas", null);
    }

    public void listaOrdenesSolicitadas() {
        try {
            List<OrdenVO> lo = ordenServicioRemoto.getHistorialOrdenes(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(), siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaInicio()),
                    siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaFin()), Constantes.ORDENES_SIN_APROBAR);
            //  mapaOrdenes.put("ordenesSolicitadas", lo);
        } catch (Exception ex) {
            Logger.getLogger(OrdenBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cambiarFiltroHistorial(ValueChangeEvent event) {
        setTipoFiltro((String) event.getNewValue());
        setReferencia(Constantes.VACIO);
        limpiarListaOrdenesSolicitadas();
    }

    public void filtrarOrden() {
        try {
            if (getFechaInicio() == null || getFechaInicio().isEmpty()) {
                setFechaInicio(siManejoFechaImpl.convertirFechaStringddMMyyyy(new Date()));
            }
//            if (getTipoFiltro().equals(Constantes.FILTRO)) {
//                mapaOrdenes.put("ordenesSolicitadas", ordenServicioRemoto.getHistorialOrdenes(usuarioBean.getUsuarioConectado().getId(),
//                        usuarioBean.getUsuarioConectado().getApCampo().getId(),
//                        siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaInicio()),
//                        siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaFin()),
//                        Constantes.ORDENES_SIN_APROBAR));
//            } else if (getTipoFiltro().equals("palabra")) {
//                mapaOrdenes.put("ordenesSolicitadas", ordenServicioRemoto.traerHistorialOrdenePorCadenaItems(getReferencia(),
//                        usuarioBean.getUsuarioConectado().getId(),
//                        usuarioBean.getUsuarioConectado().getApCampo().getId()));
//            }
//            if (mapaOrdenes.get("ordenesSolicitadas") == null) {
//                FacesUtilsBean.addErrorMessage("No hay OC/S con los parámetros solicitados.");
//            }
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ocurriio un error al traer los datos del historial. " + ex.getMessage());
        }
    }

    public String historialAction() {
        limpiarListaOrdenesSolicitadas();
        this.panelSeleccionado = 0;
        return "/vistas/SiaWeb/Orden/HistorialOrden.xhtml?faces-redirect=true";
    }

    public String verDetalleOrden(int idOrd) {
        ordenActual = this.ordenServicioRemoto.find(idOrd);
        CargaEtsBean cargaEtsBean = (CargaEtsBean) FacesUtilsBean.getManagedBean("cargaEtsBean");
        cargaEtsBean.traerTablaComparativa();
        cargaEtsBean.ordenEtsPorCategoria();
        if (notaOrdenBean.getNotaActual() != null) {
            notaOrdenBean.cambiarNotaOrden(0);
        }
        itemsPorOrden();
        //
        formatosEntradaOrden();
        menuBarBean.procesarAccion("detalleOrden.xhtml?faces-redirect=true");
        if (usuarioBean.getMapaRoles().containsKey("Consulta OCS Factura")) {
            cargarFacturas(idOrd);
        }
        return "detalleOrden";
    }

    /**
     * @return Items de la requisición
     */
    public void itemsPorRequisicion() {
        try {
            if (this.getOrdenActual() != null) {
                itemsRequisicion = requisicionServicioImpl.getItems(this.getOrdenActual().getRequisicion().getId(), 0);

            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    public void itemsRequisicionConsultaNativas() {
        List<RequisicionDetalleVO> lo = null;
        try {
            if (this.getOrdenActual() != null) {
                RequisicionDetalleVO o;
                lo = this.requisicionServicioImpl.getItemsPorRequisicion(this.getOrdenActual().getRequisicion().getId(), true, false);
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    // Zona  de rechazos o devoluciones
    public Rechazo[] getRechazosRequisicion() {
        try {
            if (this.getOrdenActual() != null) {
                List<Rechazo> tempList = this.requisicionServicioImpl.getRechazosPorRequisicion(this.getOrdenActual().getRequisicion().getId());
                return tempList.toArray(new Rechazo[tempList.size()]);
            }

        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
        return new Rechazo[0];
    }

    public void buscarOrdenPorConsecutivo() {
        LOGGER.info(this, "Vino aqui  --- -- - codigo");
        if (getConsecutivo() != null && !getConsecutivo().isEmpty()) {
            LOGGER.info(this, "Vino aqui  --- -- - id dentro : : : : " + getConsecutivo());
            ordenActual = ordenServicioRemoto.buscarPorConsecutivo(getConsecutivo());
        }
    }

    public void buscarOrdenPorId() {
        LOGGER.info(this, "Vino aqui  --- -- -  + + + + + + id");
        if (getConsecutivo() != null && !getConsecutivo().isEmpty()) {
            LOGGER.info(this, "Vino aqui  --- -- - id dentro :: : " + getConsecutivo());
            ordenActual = ordenServicioRemoto.find(Integer.parseInt(getConsecutivo()));
        }
    }

    public void limpiarSeleccionAnterior() {
        LOGGER.info(this, "Limpiando el consecutivo + " + getConsecutivo());
        setConsecutivo(Constantes.VACIO);
        LOGGER.info(this, "Limpiando el consecutivo  despues + " + getConsecutivo());

    }

    public void limpiar() {
        setReferencia(Constantes.VACIO);
        setOrdenVO(null);
    }

    public void completarActualizacionOrden() {
        try {
            ordenServicioRemoto.editarOrden(getOrdenActual());
            //Esto es para cerrar el panel emergente de modificar Orden
            FacesUtilsBean.addInfoMessage("Se actualizó correctamente la orden de compra");
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoModOCS);");
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    /**
     * *************************************************************************************************************************
     * *
     * /**
     * ***************************** OC_CATEGORIA_ETS
     * ***************************
     */
    /**
     * **************************************************************************
     */
    /**
     * @return the letra
     */
    public String getLetra() {
        return letra;
    }

    /**
     * @param letra the letra to set
     */
    public void setLetra(String letra) {
        if (!Strings.isNullOrEmpty(letra)) {
            this.letra = letra;
        }
    }

    /**
     * @return the lista
     */
    public List getListaProveedores() {
        return listaProveedores;
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

    public String getContactoSeleccionado() {
        return contactoSeleccionado;
    }

    /**
     * @param contactoSeleccionado the contactoSeleccionado to set
     */
    public void setContactoSeleccionado(String contactoSeleccionado) {
        this.contactoSeleccionado = contactoSeleccionado;
    }

    /**
     * @return the contactos
     */
    public List getContactos() {
        return contactos;
    }

    /**
     * @return the contactoParaEliminar
     */
    public String getContactoParaEliminar() {
        return contactoParaEliminar;
    }

    /**
     * @param contactoParaEliminar the contactoParaEliminar to set
     */
    public void setContactoParaEliminar(String contactoParaEliminar) {
        this.contactoParaEliminar = contactoParaEliminar;
    }

    public long getTotalOrdenesSinSolicitar() {
        long retVal = 0;

        try {
            retVal
                    = ordenServicioRemoto.totalOrdenesSinSolicitar(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }

        return retVal;
    }

    public long getTotalOrdenesSinAprobar() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.totalOrdenesSinAprobar(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }

        return retVal;
    }

    public long getTotalOrdenesSinAutorizarMPG() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.totalOrdenesSinAutorizarMPG(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }
        return retVal;

    }

    public long getTotalOrdenesSinAutorizarIHSA() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.getTotalOrdenesSinAutorizarIHSA(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }
        return retVal;

    }

    public long getTotalOrdenesSinAutorizarCompras() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.getTotalOrdenesSinAutorizarCompras(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }
        return retVal;

    }

    public long getTotalTareasSinAutorizarCompras() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.getTotalTareasSinCompleta(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }
        return retVal;

    }

    public long getTotalTareasSinAutorizarComprasAF() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.getTotalTareasSinCompletaAF(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }

        return retVal;

    }

    public long getTotalTareasSinAutorizarComprasPS() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.getTotalTareasSinCompletaPS(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }
        return retVal;

    }

    public long getTotalOrdenesSinAutorizarFinanzas() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.totalOrdenesSinAutorizarFinanzas(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }
        return retVal;

    }

    public long getTotalOrdenesSinAprobarSocio() {
        long retVal = 0;

        try {

            retVal
                    = ordenServicioRemoto.totalOrdenesSinAprobarSocio(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );

        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }
        return retVal;
    }

    /// INICIA EL TRABAJO PENDIENTE
    public List<CampoOrden> getBuscarTrabajoPendiente() {
        List<CampoOrden> retVal = null;

        try {
            retVal = ordenServicioRemoto.buscarTrabajoPendienteCampo(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId());
        } catch (Exception ex) {
            LOGGER.fatal(this, ex);
        }
        return retVal;
    }

    public void cambiarCampoMenu(int idCampo) {

        usuarioImpl.cambiarCampoUsuario(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getId(), idCampo);
        usuarioBean.setUsuarioConectado(usuarioImpl.find(usuarioBean.getUsuarioConectado().getId()));
        usuarioBean.setCompania(usuarioBean.getUsuarioConectado().getApCampo().getCompania());
        RequisicionBean requisicionBean = (RequisicionBean) FacesUtilsBean.getManagedBean("requisicionBean");
        OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
        ordenBean.setOrdenActual(null);
        //Listas de historial
        requisicionBean.setRequisicionesSolicitadas(null);
        requisicionBean.setRequisicionesRevisadas(null);
        requisicionBean.setRequisicionesAprobadas(null);
        requisicionBean.setRequisicionesAprobadas(null);
        requisicionBean.setRequisicionesAutorizadas(null);
        requisicionBean.setRequisicionesVistoBueno(null);
        requisicionBean.setRequisicionesAsignadas(null);
        requisicionBean.setRequisicionActual(null);

        notaOrdenBean.setNotaActual(null);
        notaOrdenBean.setListaNotas(null);
        usuarioBean.setCambioCampo(false);

    }

    /**
     * ***********************************************************
     * @return
     */
    public List getTraerCancelacion() {
        if (getOrdenActual() != null) {
            return ordenSiMovimientoImpl.getMovimientsobyOrden(getOrdenActual().getId());
        }
        return null;
    }

    /**
     * @return the motivoCancelacion
     */
    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    /**
     * @param motivoCancelacion the motivoCancelacion to set
     */
    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    /**
     * @return the motivoDevolucion
     */
    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }

    /**
     * @param motivoDevolucion the motivoDevolucion to set
     */
    public void setMotivoDevolucion(String motivoDevolucion) {
        this.motivoDevolucion = motivoDevolucion;
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
     * @return the mostrar
     */
    public boolean isMostrar() {
        return mostrar;
    }

    /**
     * @param mostrar the mostrar to set
     */
    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
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
//        this.IdRequisicion = this.ordenActual.getRequisicion().getId();
    }

    private List<OcActivoFijoVO> crearLista(int size, int orden, int ordenDet) {
        List<OcActivoFijoVO> lista = new ArrayList<OcActivoFijoVO>();
        OcActivoFijoVO item = null;
        for (int i = 0; i < size; i++) {
            item = new OcActivoFijoVO();
            item.setCodigo(Constantes.VACIO);
            item.setOrdenId(orden);
            item.setOrdenDetId(ordenDet);
            lista.add(item);
        }
        return lista;
    }

    /**
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

//    public boolean isTienePermisoContrato() {
//        UsuarioRolVo usrRol = null;
//        if (usuarioBean.getUsuarioConectado() != null) {
//            try {
//                usrRol
//                        = siUsuarioRolImpl.findUsuarioRolVO(
//                                Constantes.ROL_REVISA_CONTRATO,
//                                usuarioBean.getUsuarioConectado().getId(),
//                                usuarioBean.getUsuarioConectado().getApCampo().getId()
//                        );
//            } catch (Exception ex) {
//                Logger.getLogger(Orden.class.getSimpleName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return usrRol != null;
//    }
    /**
     * @return the aprueba
     */
    public String getAprueba() {
        return aprueba;
    }

    /**
     * @param aprueba the aprueba to set
     */
    public void setAprueba(String aprueba) {
        this.aprueba = aprueba;
    }

    /**
     * @return the paginaAtras
     */
    public String getPaginaAtras() {
        return paginaAtras;
    }

    /**
     * @param paginaAtras the paginaAtras to set
     */
    public void setPaginaAtras(String paginaAtras) {
        this.paginaAtras = paginaAtras;
    }

    public void paginaHistoria() {
        this.menuBarBean.procesarAccion(paginaAtras, ";activarTab('tabOCSProc',0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');");
    }

    /**
     * @return the panelSeleccionado
     */
    public long getPanelSeleccionado() {
        return panelSeleccionado;
    }

    /**
     * @param panelSeleccionado the panelSeleccionado to set
     */
    public void setPanelSeleccionado(int panelSeleccionado) {
        LOGGER.info(this, "Panel seleccionado " + panelSeleccionado);
        this.panelSeleccionado = panelSeleccionado;
    }

    /**
     * @return the revisa
     */
    public String getRevisa() {
        return revisa;
    }

    /**
     * @param revisa the revisa to set
     */
    public void setRevisa(String revisa) {
        this.revisa = revisa;
    }

    /**
     * @return the idProveedorr
     */
    public long getIdProveedorr() {
        return idProveedorr;
    }

    /**
     * @param idProveedorr the idProveedorr to set
     */
    public void setIdProveedorr(int idProveedorr) {
        this.idProveedorr = idProveedorr;
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
     * @return the fechaInicio
     */
    public String getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the fechaFin
     */
    public String getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * @return the idProyectoOT
     */
    public long getIdProyectoOT() {
        return idProyectoOT;
    }

    /**
     * @param idProyectoOT the idProyectoOT to set
     */
    public void setIdProyectoOT(int idProyectoOT) {
        this.idProyectoOT = idProyectoOT;
    }

    /**
     * @return the tipoFiltro
     */
    public String getTipoFiltro() {
        return tipoFiltro;
    }

    /**
     * @param tipoFiltro the tipoFiltro to set
     */
    public void setTipoFiltro(String tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
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
     * @return the activoFijjoCodigos
     */
    public List<String> getActivoFijjoCodigos() {
        return activoFijjoCodigos;
    }

    /**
     * @param activoFijjoCodigos the activoFijjoCodigos to set
     */
    public void setActivoFijjoCodigos(List<String> activoFijjoCodigos) {
        this.activoFijjoCodigos = activoFijjoCodigos;
    }

    /**
     * @return the consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getGerencia() {
        String gerencia = Constantes.VACIO;
        if (this.ordenActual != null && this.ordenActual.getGerencia() != null
                && this.ordenActual.getGerencia().getAbrev().contains(";")) {
            GerenciaVo gerenciaVo = gerenciaImpl.traerGerenciaVOAbreviatura(this.ordenActual.getGerencia().getAbrev().substring(0, 3));
            if (gerenciaVo != null && !gerenciaVo.getNombre().isEmpty()) {
                gerencia = gerenciaVo.getNombre();
            }
        } else if (this.ordenActual != null && this.ordenActual.getGerencia() != null) {
            gerencia = this.ordenActual.getGerencia().getNombre();
        }
        return gerencia;
    }

    /**
     * @return the ocProductos
     */
    public List<SelectItem> getOcProductos() {
        if (ocProductos == null || ocProductos.isEmpty()) {
            ocProductos = ocProductoImpl.traerProducto(ordenActual.getCompania().getRfc());
        }
        return ocProductos;
    }

    /**
     * @param ocProductos the ocProductos to set
     */
    public void setOcProductos(List<SelectItem> ocProductos) {
        this.ocProductos = ocProductos;
    }

    /**
     * @return the idTarea
     */
    public long getIdTarea() {
        return idTarea;
    }

    /**
     * @param idTarea the idTarea to set
     */
    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

//    public String cargarEtsOCs() {
//        String retVal = Constantes.VACIO;
//        try {
//            setIrInicio(true);
//            retVal = menuBarBean.cargarEts(FacesUtilsBean.getRequestParameter("paginaOrigen"), FacesUtilsBean.getRequestParameter("paginaDestino"));
//            
//        } catch (Exception e) {
//            LOGGER.fatal(this, e);
//        }
//        return retVal;
//    }
    /**
     * @return the irInicio
     */
    public boolean isIrInicio() {
        return irInicio;
    }

    /**
     * @param irInicio the irInicio to set
     */
    public void setIrInicio(boolean irInicio) {
        this.irInicio = irInicio;
    }

    /**
     * @return the listaArchivoConvenio
     */
    public List<ContratoVO> getListaArchivoConvenio() {
        return listaArchivoConvenio;
    }

//    /**
//     * @return the categoriasSeleccionadas
//     */
//    public List<CategoriaVo> getCategoriasSeleccionadas() {
//        return categoriasSeleccionadas;
//    }
//
//    /**
//     * @param categoriasSeleccionadas the categoriasSeleccionadas to set
//     */
//    public void setCategoriasSeleccionadas(List<CategoriaVo> categoriasSeleccionadas) {
//        this.categoriasSeleccionadas = categoriasSeleccionadas;
//    }
//    /**
//     * @return the categoriaVo
//     */
//    public CategoriaVo getCategoriaVo() {
//        return categoriaVo;
//    }
//
//    /**
//     * @param categoriaVo the categoriaVo to set
//     */
//    public void setCategoriaVo(CategoriaVo categoriaVo) {
//        this.categoriaVo = categoriaVo;
//    }
//
//    public void seleccionarCategoriaCabecera() {
//        int id = Integer.parseInt(FacesUtilsBean.getRequestParameter("indiceCatSel"));
//        traerSubcategoria(id);
//        setCategoriasTxt();
//    }
//
//    public void traerArticulosItemsListener(TextChangeEvent event) {
//        String cadena = event.getNewValue().toString();
//        if ((cadena != null && !cadena.isEmpty() && cadena.length() > 2)
//                || (cadena == null || cadena.isEmpty())) {
//            setArticulosResultadoBqda(traerArticulosItems(cadena));
//        }
//        PrimeFaces.current().executeScript( ";marcarBusquedaOrden();");
//    }
//
//    private String filtrosCadena(String cadena) {
//        String[] output = cadena.split("\\%");
//        StringBuilder cadenaNombre = new StringBuilder("and ((");
//        StringBuilder cadenaCodigo = new StringBuilder(") or (");
//        String and = "";
//        for (String s : output) {
//            cadenaNombre.append(and).append("upper(a.NOMBRE) like upper('%").append(s).append("%') ");
//            cadenaCodigo.append(and).append("upper(a.CODIGO_INT) like upper('%").append(s).append("%') ");
//            and = " and ";
//        }
//        return cadenaNombre.toString() + cadenaCodigo.toString() + "))";
//    }
//
//    private List<SelectItem> traerArticulosItems(String cadena) {
//        List<SelectItem> list;
//        try {
//            cadena = filtrosCadena(cadena.replace(" ", "%"));
//            list = soporteArticulos.obtenerArticulosItems(cadena, usuarioBean.getUsuarioConectado().getApCampo().getId(),
//                    Constantes.CERO,
//                    getCodigos(this.getCategoriasSeleccionadas().size() > 1
//                            ? this.getCategoriasSeleccionadas().subList(1, this.getCategoriasSeleccionadas().size())
//                            : new ArrayList<CategoriaVo>()));
//        } catch (Exception e) {
//            list = new ArrayList<>();
//        }
//        return list;
//    }
//
//    private void traerSubcategoria(int indice) {
//        CategoriaVo c = categoriasSeleccionadas.get(indice);
//        if (indice == 0) {
//            setCategoriaVo(getCategoriaVoInicial());
//            categoriasSeleccionadas = new ArrayList<CategoriaVo>();
//            iniciarCatSel();
//        } else {
//            setCategoriaVo(siRelCategoriaImpl.traerCategoriaPorCategoria(c.getId(), getSoloCodigos(this.getCategoriasSeleccionadas().subList(0, indice)), usuarioBean.getUsuarioConectado().getApCampo().getId()));
//            if (c.getId() != getCategoriaVo().getId()) {
//                categoriasSeleccionadas.add(getCategoriaVo());// limpiar lista seleccionadas
//            }
//            if ((indice + 1) < categoriasSeleccionadas.size()) {
//                for (int i = (categoriasSeleccionadas.size() - 1); (indice + 1) < categoriasSeleccionadas.size(); i--) {
//                    categoriasSeleccionadas.remove(i);
//                }
//            }
//        }
//        this.setItemActual(new OrdenDetalle());
//        this.itemActual.setOrden(this.getOrdenActual());
//        this.itemActual.setMoneda(new Moneda());
//        this.itemActual.setInvArticulo(new InvArticulo());
//        this.itemActual.setObservaciones("");
//        this.setArticuloTx("");
//        PrimeFaces.current().executeScript( ";minimizarPanel('artFrecImg', 'collapsePanelArtFre');expandirPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');");
//    }
//
//    private void iniciarCatSel() {
//        this.setCategoriasSeleccionadas(new ArrayList<CategoriaVo>());
//        CategoriaVo c = new CategoriaVo();
//        c.setNombre("Pricipales");
//        c.setId(Constantes.CERO);
//        categoriasSeleccionadas.add(c);
//    }
//
//    public List<CategoriaVo> getListaCatPrin() {
//        List<CategoriaVo> retVal = null;
//        try {
//            if (getCategoriaVo() != null) {
//                retVal = getCategoriaVo().getListaCategoria();
//            }
//        } catch (Exception ex) {
//            LOGGER.fatal(this, ex);
//        }
//        return retVal;
//    }
//    public void seleccionarCategoria(SelectEvent event) {
//	CategoriaVo con = (CategoriaVo) event.getObject();
//	setCategoriaVo(con);
//	llenarCategoria(getCodigos(this.getCategoriasSeleccionadas().size() > 2
//				    ? this.getCategoriasSeleccionadas().subList(2, this.getCategoriasSeleccionadas().size())
//				    : new ArrayList<CategoriaVo>()));
//	if (getCategoriaVo().getListaCategoria() == null || getCategoriaVo().getListaCategoria().size() < 1) {
//	    setArticulosResultadoBqda(soporteArticulos.obtenerArticulos("", usuarioBean.getUsuarioConectado().getApCampo().getId(),
//		    this.getCategoriasSeleccionadas().size() > 1 ? this.getCategoriasSeleccionadas().get(1).getId() : 0,
//		    getCodigos(this.getCategoriasSeleccionadas().size() > 2
//				    ? this.getCategoriasSeleccionadas().subList(2, this.getCategoriasSeleccionadas().size())
//				    : new ArrayList<CategoriaVo>())));
//	}
//	PrimeFaces.current().executeScript( ";minimizarPanel('artFrecImg', 'collapsePanelArtFre');expandirPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');");
//    }
//    /**
//     * @return the articulosFrecuentes
//     */
//    public List<ArticuloVO> getArticulosFrecuentes() {
//        return articulosFrecuentes;
//    }
//
//    /**
//     * @param articulosFrecuentes the articulosFrecuentes to set
//     */
//    public void setArticulosFrecuentes(List<ArticuloVO> articulosFrecuentes) {
//        this.articulosFrecuentes = articulosFrecuentes;
//    }
//
//    /**
//     * @return the articulosResultadoBqda
//     */
//    public List<SelectItem> getArticulosResultadoBqda() {
//        return articulosResultadoBqda;
//    }
//
//    /**
//     * @param articulosResultadoBqda the articulosResultadoBqda to set
//     */
//    public void setArticulosResultadoBqda(List<SelectItem> articulosResultadoBqda) {
//        this.articulosResultadoBqda = articulosResultadoBqda;
//    }
//    public void seleccionarArtFrecuente(SelectEvent event) {
//        try {
//            ArticuloVO artVO = (ArticuloVO) event.getObject();
//            if (artVO != null && artVO.getId() > 0) {
//                setArticuloTx(new StringBuilder().append(artVO.getNombre())
//                        .append("=>").append(artVO.getCodigoInt())
//                        .toString().toLowerCase());
//                cambiarArticulo();
//            }
//        } catch (Exception e) {
//            LOGGER.fatal(this, e);
//            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
//        }
//    }
//
//    public void seleccionarResultadoBA(SelectEvent event) {
//        try {
//            SelectItem artItem = (SelectItem) event.getObject();
//            if (artItem != null && artItem.getValue() != null && ((ArticuloVO) artItem.getValue()).getId() > 0) {
//                setArticuloTx(new StringBuilder().append(((ArticuloVO) artItem.getValue()).getNombre())
//                        .append("=>").append(((ArticuloVO) artItem.getValue()).getNumParte())
//                        .toString().toLowerCase());
//
//                cambiarArticulo();
//            }
//        } catch (Exception e) {
//            LOGGER.fatal(this, e);
//            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
//        }
//    }
//
//    public void cambiarArticulo() {
//        try {
//            if (getArticuloTx() != null && !getArticuloTx().isEmpty()) {
//                int aux = 2;
//                String codigo = getArticuloTx().substring(
//                        (getArticuloTx().lastIndexOf("=>") + aux));
//                List<ArticuloVO> articulos = soporteArticulos.getArticulosActivo(codigo, usuarioBean.getUsuarioConectado().getApCampo().getId(), 0, "");
//                if (articulos != null && articulos.size() > 0) {
//                    this.setArticuloID(articulos.get(0).getId());
//                    this.getItemActual().setInvArticulo(articuloImpl.find(articulos.get(0).getId()));
//                    setArticuloTx("");
//                    setCategoriaVo(getCategoriaVoInicial());
//                    categoriasSeleccionadas = new ArrayList<CategoriaVo>();
//                    iniciarCatSel();
//                    PrimeFaces.current().executeScript( ";minimizarPanel('artFrecImg', 'collapsePanelArtFre');minimizarPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');");
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.fatal(this, e);
//            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
//        }
//    }
//
//    /**
//     * @return the monedaSeleccionadaDet
//     */
//    public String getMonedaSeleccionadaDet() {
//        return monedaSeleccionadaDet;
//    }
//
//    /**
//     * @param monedaSeleccionadaDet the monedaSeleccionadaDet to set
//     */
//    public void setMonedaSeleccionadaDet(String monedaSeleccionadaDet) {
//        this.monedaSeleccionadaDet = monedaSeleccionadaDet;
//    }
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
     * @return the formatosEntrada
     */
    public List<OrdenFormatoVo> getFormatosEntrada() {
        return formatosEntrada;
    }

    /**
     * @param formatosEntrada the formatosEntrada to set
     */
    public void setFormatosEntrada(List<OrdenFormatoVo> formatosEntrada) {
        this.formatosEntrada = formatosEntrada;
    }

    public void ordenesDeRequisicion(int idRequuisicion) {
        try {

            List<OrdenVO> lo = this.ordenServicioRemoto.traerOrdenPorRequisicion(idRequuisicion);
            //   mapaOrdenes.put("ordenesRequisicion", lo);

        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage());
        }
    }

    /**
     * @return the listaFactura
     */
    public List<FacturaVo> getListaFactura() {
        return listaFactura;
    }

    /**
     * @param listaFactura the listaFactura to set
     */
    public void setListaFactura(List<FacturaVo> listaFactura) {
        this.listaFactura = listaFactura;
    }

    /**
     * @return the facturaVo
     */
    public FacturaVo getFacturaVo() {
        return facturaVo;
    }

    /**
     * @param facturaVo the facturaVo to set
     */
    public void setFacturaVo(FacturaVo facturaVo) {
        this.facturaVo = facturaVo;
    }

    /**
     * @return the listaNotaCredito
     */
    public List<FacturaVo> getListaNotaCredito() {
        return listaNotaCredito;
    }

    /**
     * @param listaNotaCredito the listaNotaCredito to set
     */
    public void setListaNotaCredito(List<FacturaVo> listaNotaCredito) {
        this.listaNotaCredito = listaNotaCredito;
    }

    /**
     * @return the contenidoNacional
     */
    public List<FacturaContenidoNacionalVo> getContenidoNacional() {
        return contenidoNacional;
    }

    /**
     * @param contenidoNacional the contenidoNacional to set
     */
    public void setContenidoNacional(List<FacturaContenidoNacionalVo> contenidoNacional) {
        this.contenidoNacional = contenidoNacional;
    }

    /**
     * @return the listaArchivosFactura
     */
    public List<FacturaAdjuntoVo> getListaArchivosFactura() {
        return listaArchivosFactura;
    }

    /**
     * @param listaArchivosFactura the listaArchivosFactura to set
     */
    public void setListaArchivosFactura(List<FacturaAdjuntoVo> listaArchivosFactura) {
        this.listaArchivosFactura = listaArchivosFactura;
    }

    /**
     * @return the listaArchivosNotaCredito
     */
    public List<FacturaAdjuntoVo> getListaArchivosNotaCredito() {
        return listaArchivosNotaCredito;
    }

    /**
     * @param listaArchivosNotaCredito the listaArchivosNotaCredito to set
     */
    public void setListaArchivosNotaCredito(List<FacturaAdjuntoVo> listaArchivosNotaCredito) {
        this.listaArchivosNotaCredito = listaArchivosNotaCredito;
    }

    public void cargarFacturas(int ordenID) {
        setListaFactura(siFacturaImpl.facturasPorOrden(ordenID));
    }

    public void seleccionarFactura(int id) {
        facturaVo = siFacturaImpl.buscarFactura(id);
        facturaVo.setDetalleFactura(new ArrayList<FacturaDetalleVo>());
        facturaVo.setDetalleFactura(siFacturaDetalleImpl.detalleFactura(id));
        //
        listaArchivosFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), true);
        contenidoNacional = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(facturaVo.getId());
        listaNotaCredito = siFacturaImpl.traerNotaCredito(facturaVo.getId());
        //
        listaArchivosNotaCredito = new ArrayList<>();
        PrimeFaces.current().executeScript(";abrirDialogoModal(dogoDatosFacturaOrden);");
    }

    public void seleccionarNCFactura(int id) {
        setListaArchivosNotaCredito(siFacturaAdjuntoImpl.traerSoporteFactura(id, Constantes.BOOLEAN_FALSE));
        PrimeFaces.current().executeScript(";abrirDialogoModal(dogoArchNCFactOrden);");
    }

    /**
     * @return the proveedorCI
     */
    public boolean isProveedorCI() {
        return proveedorCI;
    }

    /**
     * @param proveedorCI the proveedorCI to set
     */
    public void setProveedorCI(boolean proveedorCI) {
        this.proveedorCI = proveedorCI;
    }

}
