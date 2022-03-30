/*
 * RequisicionesBean.java
 *
 * Creado el 11/06/2009, 06:08:42 PM.
 * Managed Bean desarrollado por Héctor Acosta Sierra para la empresa MPG-IHSA.
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones, o mejoras
 * enviar un mail a hacosta@ihsa.mx o a hacosta.0505@gmail.com.
 *
 */
package sia.compra.requisicion.bean.backing;

import com.google.common.base.Strings;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import sia.compra.busqueda.SoporteArticulos;
import sia.compra.orden.bean.backing.OrdenBean;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.inventarios.service.ArticuloImpl;
import sia.inventarios.service.InvArticuloCampoImpl;
import sia.inventarios.service.InventarioImpl;
import sia.modelo.Gerencia;
import sia.modelo.InvArticulo;
import sia.modelo.OcActividadPetrolera;
import sia.modelo.OcCodigoSubtarea;
import sia.modelo.OcCodigoTarea;
import sia.modelo.OcMetodoPago;
import sia.modelo.OcPresupuesto;
import sia.modelo.OcSubTarea;
import sia.modelo.OcTarea;
import sia.modelo.OcUnidadCosto;
import sia.modelo.ProyectoOt;
import sia.modelo.Rechazo;
import sia.modelo.Requisicion;
import sia.modelo.RequisicionDetalle;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.GereciaTareaVo;
import sia.modelo.requisicion.vo.OcSubtareaVO;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.requisicion.vo.RequisicionEsperaVO;
import sia.modelo.requisicion.vo.RequisicionMovimientoVO;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.sistema.vo.CategoriaVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.notificaciones.usuario.movil.impl.NotificacionMovilUsuarioImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcUnidadCostoImpl;
import sia.servicios.requisicion.impl.OcActividadPetroleraImpl;
import sia.servicios.requisicion.impl.OcGerenciaProyectoImpl;
import sia.servicios.requisicion.impl.OcGerenciaTareaImpl;
import sia.servicios.requisicion.impl.OcPresupuestoDetalleImpl;
import sia.servicios.requisicion.impl.OcPresupuestoImpl;
import sia.servicios.requisicion.impl.OcRequisicionCoNoticiaImpl;
import sia.servicios.requisicion.impl.OcSubTareaImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.servicios.requisicion.impl.RequisicionDetalleImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.requisicion.impl.RequisicionSiMovimientoImpl;
import sia.servicios.sistema.impl.FolioImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.impl.SiRelCategoriaImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.servicios.sistema.impl.SiUnidadImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 * @author Héctor Acosta Sierra
 * @version 1.0
 */
@Named(value = "requisicionBean")
@ViewScoped
public class RequisicionBean implements Serializable {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    //------------------------------------------------------
    public static final String BEAN_NAME = "requisicionBean";
    //------------------------------------------------------
    @Inject
    private SiParametroImpl parametrosSistema;
    @Inject
    private SiAdjuntoImpl servicioSiAdjuntoImpl;
    @Inject
    private RequisicionImpl requisicionServicioRemoto;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private NotificacionRequisicionImpl notificacionRequisicionImpl;
    @Inject
    private ProyectoOtImpl proyectoOtImpl;
    @Inject
    private RequisicionDetalleImpl requisicionDetalleImpl;
    @Inject
    private SiUnidadImpl ocUnidadImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private OcUnidadCostoImpl ocUnidadCostoImpl;
    @Inject
    private OcGerenciaTareaImpl ocGerenciaTareaImpl;
    @Inject
    private OcRequisicionCoNoticiaImpl ocRequisicionCoNoticiaImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private ArticuloImpl articuloImpl;
    @Inject
    private InvArticuloCampoImpl invArticuloCampoImpl;
    @Inject
    private SiRelCategoriaImpl siRelCategoriaImpl;
    @Inject
    private SiRolImpl siRolImpl;
    @Inject
    private OcTareaImpl ocTareaImpl;
    @Inject
    private OcActividadPetroleraImpl ocActividadPetroleraImpl;
    @Inject
    private OcGerenciaProyectoImpl ocGerenciaProyectoImpl;
    @Inject
    private OcSubTareaImpl ocSubTareaImpl;
    @Inject
    private FolioImpl folioImpl;
    @Inject
    private OcPresupuestoDetalleImpl ocPresupuestoDetalleImpl;
    @Inject
    private OcPresupuestoImpl ocPresupuestoImpl;
    @Inject
    private RequisicionSiMovimientoImpl requisicionSiMovimientoImpl;
    @Inject
    InventarioImpl inventarioImpl;
    @Inject
    private NotificacionMovilUsuarioImpl notificacionMovilImpl;

    //
    @Inject
    private SoporteArticulos soporteArticulos;
    @Inject
    private MonedaBean monedaBean;

    @Inject
    private UsuarioBean usuarioBean;
    @Inject
    private NotaRequisicionBean notaRequisicionBean;
    @Inject
    private EstatusBean estatusBean;
    @Inject
    private FolioBean folioBean;
    @Inject
    private PopupBean popupBean;
    @Inject
    private MenuBarBean menuBarBean;
    @Inject
    private TipoObraBean tipoObraBean;
    @Inject
    private GerenciaBean gerenciaBean;
    @Inject
    private OrdenBean ordenBean;

    @Getter
    @Setter
    private List<SelectItem> listaG;
    @Getter
    @Setter
    private int idGerencia;
    @Getter
    @Setter
    private int idProyectoOT;
    @Getter
    @Setter
    private int idUnidadCosto;
    @Getter
    @Setter
    private int idNombreTarea;
    @Getter
    @Setter
    private String tipoRequisicion;
    @Getter
    @Setter
    private String idRevisa;
    @Getter
    @Setter
    private String idAprueba;
    @Getter
    @Setter
    private ArticuloVO articuloVo;
    //----------------------
    @Getter
    @Setter
    private Requisicion requisicionActual;
    @Getter
    @Setter
    private RequisicionDetalle itemActual;
//    private Rechazo rechazoActual;
    //----------------------
    @Getter
    @Setter
    private DataModel listaRequisiciones = null; //almacena la lista de Ordenes de compra
    @Getter
    @Setter
    private DataModel listaItems = null; //almacena la lista de Items de la Orden de compra
    @Getter
    @Setter
    private DataModel requisicionesSolicitadas;
    @Getter
    @Setter
    private DataModel requisicionesRevisadas;
    @Getter
    @Setter
    private DataModel requisicionesAprobadas;
    @Getter
    @Setter
    private DataModel requisicionesAutorizadas;
    @Getter
    @Setter
    private DataModel requisicionesVistoBueno;
    @Getter
    @Setter
    private DataModel requisicionesAsignadas;
    //----------------------
    // Este Atributo almacena la actual operación de la modificación que se está llevando a cabo con
    // La Requisicion. Cuenta con 2 posibilidades "Actualizar" y "Crear"
    @Getter
    @Setter
    private String operacionRequisicion;
    @Getter
    @Setter
    private String operacionItem;
    @Getter
    @Setter
    int fila, numerofilas;
    @Getter
    @Setter
    private int totalRequisiciones;
    @Getter
    @Setter
    protected static final String UPDATE_OPERATION = "Actualizar";
    @Getter
    @Setter
    protected static final String CREATE_OPERATION = "Crear";
    @Getter
    @Setter
    private String copiasOcultass = "";

    @Getter
    @Setter
    private Calendar fechaActual = Calendar.getInstance();
    @Getter
    @Setter
    private Date fechaRequerida = (Date) fechaActual.getTime().clone();
    @Getter
    @Setter
    private boolean verAutoriza = false;
    @Getter
    @Setter
    private boolean verVistoBueno = false;
    @Getter
    @Setter
    private boolean crearItem = false;
    @Getter
    @Setter
    private boolean irInicio = false;
    @Getter
    @Setter
    private boolean error = false;
    //------------------------------
    @Getter
    @Setter
    private int panelSeleccionado;
    //------------------------------
    @Getter
    @Setter
    private String mensajeError;
    @Getter
    @Setter
    private int paginaSeleccionada = 1;
    @Getter
    @Setter
    private DataTable dataTable;
    @Getter
    @Setter
    private boolean selected;
    private Map<Integer, Boolean> filaSeleccionada = new HashMap<Integer, Boolean>();
    //
    @Getter
    @Setter
    private String fechaInicio;
    @Getter
    @Setter
    private String fechaFin = Constantes.FMT_ddMMyyy.format(new Date());
    //
    @Getter
    @Setter
    private String unidadMedida;
    @Getter
    @Setter
    private String tipoFiltro = "filtro";
    @Getter
    @Setter
    private String referencia;
    ///
    @Getter
    @Setter
    private String codigo;
    //
    @Getter
    @Setter
    private RequisicionVO requisicionVO;
    @Getter
    @Setter
    private RequisicionVO requiVO;
    //v
    @Getter
    @Setter
    private List<SelectItem> listaTO;
    @Getter
    @Setter
    private List<SelectItem> proyectosOt = new ArrayList<>();
//
    @Getter
    @Setter
    private int idGerenciaCompra;
    @Getter
    @Setter
    private String idAnalista;
    @Getter
    @Setter
    private List<SelectItem> lstArticulos = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> lstGEArticulos = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> lstCategorias = new ArrayList<>();
    @Getter
    @Setter
    private String articuloTx;
    @Getter
    @Setter
    private String articuloGETx;
    @Getter
    @Setter
    private String categoriaGETx;
//    private String articuloValorTx;
//    private String unidadTx;
    @Getter
    @Setter
    private int articuloID;
    @Getter
    @Setter
    private List<CategoriaVo> categorias;

    @Getter
    @Setter
    private List<CategoriaVo> categoriasSeleccionadas = new ArrayList<>();
    @Getter
    @Setter
    private List<ArticuloVO> articulosFrecuentes = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> articulosResultadoBqda = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> articulosResultadoBqdaCat = new ArrayList<>();
    
    private CategoriaVo categoriaVo;
    private CategoriaVo categoriaVoInicial;
    @Getter
    @Setter
    private String newArticuloText;
    @Getter
    @Setter
    private String newArticuloTextUso;
    @Getter
    @Setter
    private List<SelectItem> listaUnidad;
    @Getter
    @Setter
    private List<ProyectoOtVo> listaProyectosOT = new ArrayList<>();
    @Getter
    @Setter
    private List<ProyectoOtVo> listaProyectosOTMulti = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> listaUnidadCosto = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> lstActividad = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> lstPresupuesto = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> lstCentroCosto = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> lstMesPresupuesto = new ArrayList<>();
    @Getter
    @Setter
    private List<SelectItem> lstAnioPresupuesto = new ArrayList<>();
    @Getter
    @Setter
    private int anioPresupuesto;
    @Getter
    @Setter
    private int mesPresupuesto;
    @Getter
    @Setter
    private int idActPetrolera;
    @Getter
    @Setter
    private int idPresupuesto;
    @Getter
    @Setter
    private int idProyectoOt;
    @Getter
    @Setter
    private int idTipoTarea;
    @Getter
    @Setter
    private int idTarea;
    @Getter
    @Setter
    private int idCentroCosto;
    @Getter
    @Setter
    private String devuelve = "";
    @Getter
    @Setter
    private boolean multiProyecto;
    @Getter
    @Setter
    private boolean mostrarMultiProyecto;
    @Getter
    @Setter
    private String codigoCentroCosto;
    @Getter
    @Setter
    private ProyectoOtVo proyOTMultiPrimero = null;
    @Getter
    @Setter
    private List<ProyectoOtVo> lstProyOTMultiResto = new ArrayList<>();
    @Getter
    @Setter
    private String multiProyectosEtiqueta;
    @Getter
    @Setter
    private List<Rechazo> listaRechazo;
    @Getter
    @Setter
    private int indexTab = 0;
    @Getter
    @Setter
    private int totalReqConInventario = 0;
    @Getter
    @Setter
    private List<InventarioVO> inventario;
    @Getter
    @Setter
    private RequisicionEsperaVO esperaVO;
    @Getter
    @Setter
    private String msgEspera;
    @Getter
    @Setter
    private boolean contieneGerencia = false;
    @Getter
    @Setter
    private boolean contineOt = false;
    @Getter
    @Setter
    private int proyectoOtID = 0;
    @Getter
    @Setter
    private CategoriaVo categoriaTempVo;
    @Getter
    @Setter
    private SelectItem artSeleccionado;

    /**
     * Creates a new instance of ManagedBeanRequisiciones
     */
    public RequisicionBean() {
    }

    @PostConstruct
    public void inicializar() {
        esperaVO = new RequisicionEsperaVO();
        esperaVO.setMsgs(new ArrayList<>());
        requisicionActual = new Requisicion();
        requisicionVO = new RequisicionVO();
        requisicionVO.setListaDetalleRequision(new ArrayList<>());
        categorias = new ArrayList<>();
        listaRechazo = new ArrayList<>();
    }

    public List<SelectItem> getListaAnalista() {
        return usuarioBean.getListaAnalista();
    }

    public List<SelectItem> getListaEstatus() {
        return estatusBean.listaStatus();
    }

    public void cambiarTipoRequisicion() {
        setListaUnidadCosto(new ArrayList<>());
        setListaProyectosOT(new ArrayList<>());
        setIdUnidadCosto(0);

        if (getTipoRequisicion().equals(TipoRequisicion.AF.name())) {
            setListaG(gerenciaBean.traerGereciaActivoFijo(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia()));
            if (gerenciaBean.isContieneGerencia()) {
                setProyectosOt(gerenciaBean.traerProyectoActivoFijo(getIdGerencia(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
                gerenciaBean.setContieneGerencia(false);
            }

        } else {
            setListaG(new ArrayList<>());
            List<SelectItem> listaGerencia = gerenciaBean.listaGerenciasConAbreviatura(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdGerencia());
            if (listaGerencia != null && listaGerencia.size() > 0) {
                setListaG(listaGerencia);
                if (gerenciaBean.isContieneGerencia()) {
                    setProyectosOt(listaProyectoPorGerencia(getIdGerencia(), usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdProyectoOT()));
                    gerenciaBean.setContieneGerencia(false);
                    if (isContineOt()) {
                        setListaUnidadCosto(listaUnidadCosto(getIdGerencia(), getIdProyectoOT(), 0));
                        setContineOt(false);
                    } else {
                        setListaUnidadCosto(new ArrayList<>());
                        setContineOt(false);
                    }
                }
            }
        }
    }

    public List<SelectItem> listaUnidadCosto(int idGerencia, int idProyectoOt, int idActividad) {
        List<SelectItem> resultList = new ArrayList<>();
        try {
            int idGer;
            GerenciaVo g = gerenciaImpl.buscarPorId(idGerencia);
            if (g.getAbrev().contains(";")) {
                String[] cad = g.getAbrev().split(";");
                UtilLog4j.log.debug(this, "Gerencia: " + g.getNombre() + "Cadena :   " + cad[0]);
                GerenciaVo ga = gerenciaImpl.traerGerenciaVOAbreviatura(cad[0]);
                idGer = ga.getId();
            } else {
                idGer = g.getId();
            }
            List<OcTareaVo> tempList = ocTareaImpl.traerUnidadCostoPorGerenciaProyectoOT(idGer, idProyectoOt, idActividad);
            for (OcTareaVo lista : tempList) {
                SelectItem item = new SelectItem(lista.getIdUnidadCosto(), lista.getUnidadCosto());
                resultList.add(item);
            }
            return resultList;
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" + ex.getMessage());
        }
        return resultList;
    }

    public List<SelectItem> getListaTipoRequisicion() {
        try {
            List<SelectItem> ls = new ArrayList<>();
            ls.add(new SelectItem(TipoRequisicion.PS, "Productos/Servicios"));
            ls.add(new SelectItem(TipoRequisicion.AF, "Activo Fijo"));
            return ls;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            return null;
        }
    }

    public List<SelectItem> listaProyectoPorGerencia(int idGerencia, int idCampo, int proyectoOTID) {
        proyectoOtID = proyectoOTID;
        return listaProyectoPorGerencia(idGerencia, idCampo);
    }

    public List<SelectItem> listaProyectoPorGerencia(int idGerencia, int idCampo) {
        UtilLog4j.log.info(this, "ApCampo " + idCampo);
        List<SelectItem> resultList = new ArrayList<>();
        try {
            int idGer;
            GerenciaVo g = gerenciaImpl.buscarPorId(idGerencia);
            if (g.getAbrev().contains(";")) {
                String[] cad = g.getAbrev().split(";");
                UtilLog4j.log.debug(this, "Gerencia: " + g.getNombre() + "Cadena :   " + cad[0]);
                GerenciaVo ga = gerenciaImpl.traerGerenciaVOAbreviatura(cad[0]);
                idGer = ga.getId();
            } else {
                idGer = g.getId();
            }
            List<ProyectoOtVo> tempList = ocTareaImpl.traerProyectoOtPorGerencia(idGer, idCampo);
            for (ProyectoOtVo lista : tempList) {
                if (!isContineOt() && lista.getId() == proyectoOtID) {
                    setContineOt(true);
                }
                SelectItem item = new SelectItem(lista.getId(), lista.getNombre());
                resultList.add(item);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" + ex.getMessage());
            resultList = new ArrayList<>();
        }
        return resultList;
    }

    public void limpiarListaRequisionesSolicitadas() {
        requisicionesSolicitadas = null;
    }

    public void filtrarRequisicion() {
        try {
            switch (getTipoFiltro()) {
                case "filtro":
                    requisicionesSolicitadas = new ListDataModel(requisicionServicioRemoto.buscarRequisicionPorFiltro(usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId(),
                            Constantes.REQUISICION_SOLICITADA, siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaInicio()), siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaFin())));
                    break;
                case "palabra":
                    requisicionesSolicitadas = new ListDataModel(requisicionServicioRemoto.buscarRequisicionFiltroPorPalabra(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), getReferencia()));
                    break;
                default:
                    requisicionesSolicitadas = new ListDataModel(requisicionServicioRemoto.traerRequisicionPorGerencia(Constantes.REQUISICION_SOLICITADA, usuarioBean.getUsuarioConectado().getApCampo().getId(), usuarioBean.getUsuarioConectado().getGerencia().getId(),
                            siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaInicio()), siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaFin())));
                    break;
            }
            if (requisicionesSolicitadas == null) {
                FacesUtilsBean.addErrorMessage("No ha requisiciones con los parametros solicitados");
            }

        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    public void cambiarFiltroHistorial() {
        //setTipoFiltro((String) event.getNewValue());

        PrimeFaces.current().ajax().update("PF('pnlFiltro').show();");

        requisicionesSolicitadas = null;
    }

    public void limpiar() {
        requisicionVO = null;
        setCodigo("");
    }

    // ITEMS
    public void validarCantidad(FacesContext context, UIComponent validate, Object value) {
        Double Cantidad = (Double) value;

        if (Cantidad != null) {
            if (Cantidad <= 0) {
                ((UIInput) validate).setValid(false);
                FacesMessage msg = new FacesMessage("Introduzca una cantidad valida por favor...");
                context.addMessage(validate.getClientId(context), msg);
            }
        }
    }

    public void validarPrecio(FacesContext context, UIComponent validate, Object value) {
        Double precio = (Double) value;

        if (precio == null) {
//            if (Precio <= 0) {
            ((UIInput) validate).setValid(false);
            FacesMessage msg = new FacesMessage("Introduzca un precio valido por favor...");
            context.addMessage(validate.getClientId(context), msg);
        }
//        }
    }

    //--- Lista de Monedas
    public List getListaMonedas() {
        return monedaBean.getListaMonedas(requisicionActual.getApCampo().getId());
    }

//    public void cambiarValorMoneda() {
//        itemActual.setMoneda(monedaBean.buscarPorNombre(event.getNewValue().toString(), requisicionActual.getApCampo().getCompania().getRfc()));
//    }
//    public void cambiarValorMoneda() {
//        itemActual.setMoneda(monedaBean.buscarPorNombre(event.getNewValue()));
//    }
    /**
     * Este metodo sirve para cambiar la requisicion seleccionada desde
     * cualquier parte del sistema y quitar los datos seleccionados...
     *
     * @param idRequisicion
     */
    public void cambiarRequisicion(int idRequisicion) {
        setRequisicionActual(requisicionServicioRemoto.find(idRequisicion));
        requisicionActual = null;
        crearItem = false;
    }

    //Se paso la implementacion del metodo a notaRequisicionServicioImpl
    private void finalizarNotas() {
        if (requisicionActual != null) {
            ocRequisicionCoNoticiaImpl.finalizarNotas(usuarioBean.getUsuarioConectado().getId(), requisicionActual.getId());
        }
    }

    public DataModel getHistorialNotas() {
        if (requisicionActual != null) {
            return new ListDataModel(ocRequisicionCoNoticiaImpl.traerTodasNoticiaPorRequisicion(requisicionActual.getId(), true));
        }
        return null;

    }

    /**
     * Este metodo sirve para seleccionar una requisicion de cualquier parte del
     * sistema
     *
     * @param
     */
    public void cargarETS() {
        //FIXME : al parecer este método no tiene referencias, revisar si es posible eliminarlo
        try {
            RequisicionVO r = (RequisicionVO) listaRequisiciones.getRowData();
            setRequisicionActual(requisicionServicioRemoto.find(r.getId()));

            menuBarBean.procesarAccion("cargarETS");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public String solicitarRequisicion() {
        try {
            setRequisicionActual(requisicionServicioRemoto.find(getRequisicionActual().getId()));
            List<RequisicionDetalleVO> lrd = requisicionServicioRemoto.getItemsPorRequisicion(requisicionActual.getId(), true, false);
            //Verifica formato
            boolean hasInvArticulo = requisicionDetalleImpl.tieneInvArticulo(getRequisicionActual().getId(), true);
            if (lrd != null && !lrd.isEmpty() && hasInvArticulo) { //Valida que tiene items
                if (requisicionDetalleImpl.validarPartidas(getRequisicionActual().getId())) {
                    requisicionActual.setProveedor("Licitación por Depto. de Compras");
                    PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoSolicitandoReq);");
                } else {
                    menuBarBean.procesarAccion("");
                    if ("C".equals(requisicionActual.getApCampo().getTipo())) {
                        PrimeFaces.current().executeScript(";alertaGeneral('Es necesario agregar Proyecto OT, Tipo tarea, Tarea, Actividad Petrolera, Datos del Presupuesto y Subtarea a los items de la requisición.');");
                    } else if (requisicionActual.getTipo().equals(TipoRequisicion.PS)) {
                        PrimeFaces.current().executeScript(";alertaGeneral('Es necesario agregar Proyecto OT, Tipo tarea y Tarea a los items de la requisición.');");
                    } else if (requisicionActual.getTipo().equals(TipoRequisicion.AF)) {
                        PrimeFaces.current().executeScript(";alertaGeneral('Es necesario agregar Proyecto OT a la requisición.');");
                    } else {
                        PrimeFaces.current().executeScript(";alertaGeneral('Es necesario modificar la requisición para cambiar el tipo.');");
                    }
                }
            } else {
                menuBarBean.procesarAccion("");
                if (lrd != null && lrd.size() > 0 && !hasInvArticulo) {
                    PrimeFaces.current().executeScript(";alertaGeneral('Sólo se puede solicitar requisiciones que utilizan el catálogo de productos  . . . ');");
                } else {
                    PrimeFaces.current().executeScript(";alertaGeneral('Es necesario agregar al menos un item a la requisición');");
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
        return "";
    }

//    private String cambiarPagina(RequisicionVO r) {
//        setRequisicionActual(requisicionServicioRemoto.find(r.getId()));
//        requisicionActual.setProveedor("Licitación por Depto. de Compras");
//        return "solicitarRequisicion";
//    }
    private boolean recorreItems(List<RequisicionDetalleVO> lrd) {
        boolean conUnidad = true;
        for (RequisicionDetalleVO requisicionDetalleVO : lrd) {
            if (requisicionDetalleVO.getIdTarea() == 0
                    || requisicionDetalleVO.getIdProyectoOt() == 0
                    || requisicionDetalleVO.getIdTipoTarea() == 0) {
                conUnidad = false;
                break;
            }
        }
        return conUnidad;
    }
//
//    private boolean recorreItemsUnidad(List<RequisicionDetalleVO> lrd) {
//        boolean conUnidad = true;
//        for (RequisicionDetalleVO requisicionDetalleVO : lrd) {
//            if (requisicionDetalleVO.getIdUnidad() == 0) {
//                conUnidad = false;
//                break;
//            }
//        }
//        return conUnidad;
//    }

    public void modificarRequisicion() {
        try {
            //setRequisicionActual(requisicionServicioRemoto.find(r.getId()));
            operacionRequisicion = UPDATE_OPERATION;
            //
            int idGerAdm = 0;

            setIdGerencia(requisicionActual.getGerencia().getId());

            if (requisicionActual.getTipo() == TipoRequisicion.PS) {
//                if (requisicionActual.getGerencia().getAbrev() != null) {
//                    idGer = requisicionActual.getGerencia().getId();
//                }
                setListaG(gerenciaBean.listaGerenciasConAbreviatura(requisicionActual.getApCampo().getId()));
                //setIdGerencia(idGer);

                if (requisicionActual.getProyectoOt() != null && requisicionActual.getProyectoOt().getId() > 0) {
                    setIdProyectoOT(requisicionActual.getProyectoOt().getId());
                }
                if (requisicionActual.getOcUnidadCosto() != null) {
                    setIdUnidadCosto(requisicionActual.getOcUnidadCosto().getId());
                }
                setTipoRequisicion(requisicionActual.getTipo().toString());
                //OT's
                if (requisicionActual.getGerencia().getAbrev().contains(";")) {
                    String[] cad = requisicionActual.getGerencia().getAbrev().split(";");
                    LOGGER.debug(this, "Gerencia: " + requisicionActual.getGerencia().getNombre() + "Cadena :   " + cad[0]);
                    GerenciaVo ga = gerenciaImpl.traerGerenciaVOAbreviatura(cad[0]);
                    idGerAdm = ga.getId();
                } else {
                    //idGerAdm = getIdGerencia();
                    idGerAdm = requisicionActual.getGerencia().getId();
                }
                //setIdGerencia(requisicionActual.getGerencia().getId());
                setProyectosOt(listaProyectoPorGerencia(idGerAdm, requisicionActual.getApCampo().getId()));
                setListaUnidadCosto(listaUnidadCosto(idGerAdm, getIdProyectoOT(), 0));
                //
            } else if (requisicionActual.getTipo() == TipoRequisicion.AF) {
                setListaG(gerenciaBean.traerGereciaActivoFijo(requisicionActual.getApCampo().getId()));
                //setIdGerencia(requisicionActual.getGerencia().getId());		
                if (requisicionActual.getProyectoOt() != null && requisicionActual.getProyectoOt().getId() > 0) {
                    setIdProyectoOT(requisicionActual.getProyectoOt().getId());
                }
                //OT's
                setProyectosOt(gerenciaBean.traerProyectoActivoFijo(getIdGerencia(), requisicionActual.getApCampo().getId()));
                //
                setTipoRequisicion(requisicionActual.getTipo().toString());
            } else {
                setListaG(gerenciaBean.listaGerenciasConAbreviatura(requisicionActual.getApCampo().getId()));
                setIdGerencia(0);
                setIdProyectoOT(0);
                //setListaProyectosOT(proyectoOtBean.listaProyectoPorGerencia(0, requisicionActual.getApCampo().getId()));
                requisicionActual.setTipoObra(null);
                setTipoRequisicion(TipoRequisicion.PS.name());
            }
            if (getRequisicionActual() != null
                    && getRequisicionActual().getRevisa() != null) {
                setIdRevisa(getRequisicionActual().getRevisa().getId());
            }
            if (getRequisicionActual() != null
                    && getRequisicionActual().getAprueba() != null) {
                setIdAprueba(getRequisicionActual().getAprueba().getId());
            }
            //
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoCrearNewReq);");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void inicioCancelarRequisicion() {
        try {
            popupBean.setRequisicion(requisicionActual);
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void devolverRequisicion() {
        try {
            error = false;
            popupBean.setRequisicion(requisicionActual);
            if (popupBean.getRechazo() != null && popupBean.getRechazo().getMotivo() != null) {
                popupBean.getRechazo().setMotivo("");
            }
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDevReq);");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void abrirMsgEspera() {
        try {
            error = false;
            setMsgEspera("");
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoMsgEsperaReq);");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void abrirAdjuntoEspera() {
        try {
            error = false;
            setMsgEspera("");
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoAdjuntoEsperaReq);");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void guardarMsgEspera() {
        error = false;
        try {
            if (getMsgEspera().trim().isEmpty()) {
                mensajeError = "Es necesario agregar un mensaje.";
                error = true;
            } else {
                requisicionSiMovimientoImpl.saveRequestMove(usuarioBean.getUsuarioConectado().getId(), getMsgEspera(), requisicionActual.getId(), Constantes.ID_SI_OPERACION_ESPERAMSG);
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoMsgEsperaReq);");
                enEsperaDet();
            }

        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void cambiarDptoRequisicion() {
        try {
            error = false;
            popupBean.setCambiarDpto(true);
            popupBean.setRequisicion(requisicionActual);
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoCategoriaRequi);");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void seleccionarRequisicion(int id) {
        try {
            requisicionActual = requisicionServicioRemoto.find(id);
            crearItem = true;
            System.out.println("Selección: " + requisicionActual.getCompania().getNombre());
            notaRequisicionBean.setFiltrar(true);
            getItemsActualizar();
            rechazosRequisicion();
            if (Constantes.REQUISICION_EN_ESPERA == requisicionActual.getEstatus().getId()) {
                enEsperaDet();
            }
            //
            String jsMetodo = ";activarTab('tabsRequi',0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');";
            PrimeFaces.current().executeScript(jsMetodo);
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void inicioAsignarRequisicion() {
        try {
            //           
            setRequisicionActual(requisicionServicioRemoto.find(requiVO.getId()));
            popupBean.setRequisicion(requisicionActual);
            popupBean.toggleModalCancelar();
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void mostrarInventario() {
        int idArt = Integer.parseInt(FacesUtilsBean.getRequestParameter("idArt"));
        InventarioVO inventarioVO = new InventarioVO();
        inventarioVO.setArticuloId(idArt);
        inventario = new ArrayList<>();
        //.out.println("campo: " + usuarioBean.getUsuarioConectado().getApCampo().getId());
        setInventario(inventarioImpl.traerInventario(inventarioVO, usuarioBean.getUsuarioConectado().getApCampo().getId()));
        //
        PrimeFaces.current().executeScript("$(dialogoMostrarInventario).modal('show');");
    }

    public DataModel getTraerNoticia() {
        try {
            if (usuarioBean.getUsuarioConectado() != null) {
                if (getRequisicionActual() != null) {
                    if (notaRequisicionBean.isFiltrar()) {
                        return notaRequisicionBean.traerNoticiaPorRequisicion(getRequisicionActual().getId());
                    }
                } else {
                    if (notaRequisicionBean.isFiltrar()) {
                        return notaRequisicionBean.traerNoticiaPorUsuario(usuarioBean.getUsuarioConectado().getId());
                    }
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.fatal(this, "getTraerNoticia  . . ." + e.getMessage(), e);
            return null;
        }
    }

    public void comentarNoticia() {
        //
        int noti = getTraerNoticia().getRowIndex();
        //
        try {
            List<NoticiaVO> ln = (List<NoticiaVO>) getTraerNoticia().getWrappedData();
            ln.get(noti).setComentar(true);
            //noticias = null; // refrescar la lista
        } catch (Exception e) {
            LOGGER.fatal(this, "Exception en comentar noticia " + e.getMessage(), e);
        }
    }

    public void nuevoComentario() {
        if (!notaRequisicionBean.getComentario().getValue().toString().trim().equals("")) {
            String idN = FacesUtilsBean.getRequestParameter("idNoticia");
            notaRequisicionBean.setIdNoticiaActiva(Integer.parseInt(idN));
            int campo = Integer.parseInt(FacesUtilsBean.getRequestParameter("idCampo"));
            notaRequisicionBean.agregarComentario(notaRequisicionBean.getIdNoticiaActiva(), campo);
        } else {
            FacesUtilsBean.addInfoMessage("Agregue un comentario a la noticia .  .  .  . ");
        }
    }

    public void verDetalle(int idReq) {
        setRequisicionActual(requisicionServicioRemoto.find(idReq));
        getItemsActualizar();
        //
        rechazosRequisicion();
        ordenBean.ordenesDeRequisicion(idReq);
        menuBarBean.procesarAccion("/vistas/SiaWeb/Requisiciones/DetalleHistorial.xhtml?faces-redirect=true");
    }

    public void verDetalleRevisadas() {
        setRequisicionActual(requisicionServicioRemoto.find(requiVO.getId()));
        getItemsActualizar();
        menuBarBean.procesarAccion("detalleHistorial");
    }

    public void verDetalleAprobadas() {
        setRequisicionActual(requisicionServicioRemoto.find(requiVO.getId()));
        getItemsActualizar();
        menuBarBean.procesarAccion("detalleHistorial");
    }

    public void verDetalleAutorizadas() {
        setRequisicionActual((Requisicion) requisicionesAutorizadas.getRowData());
        getItemsActualizar();
        menuBarBean.procesarAccion("detalleHistorial");
    }

    public void verDetalleVistoBueno() {
        setRequisicionActual((Requisicion) requisicionesVistoBueno.getRowData());
        getItemsActualizar();
        menuBarBean.procesarAccion("detalleHistorial");
    }

    public void verDetalleAsignadas() {
        setRequisicionActual(requisicionServicioRemoto.find(requiVO.getId()));
        getItemsActualizar();
        menuBarBean.procesarAccion("detalleHistorial");
    }

    public void verHistorial() {
        menuBarBean.procesarAccion("historialRequisicion");
    }

    //
    public void llenarProyectoOT() {
        String proyectosOT = proyectoOtImpl.traerProyectoOTJson(usuarioBean.getCompania().getRfc());
        //   LOGGER.info(this, "pots: " + proyectosOT);
        PrimeFaces.current().executeScript(";setJson(" + proyectosOT + ");");
    }
    //

    public void inicioGenerarRequisicion() {
        PrimeFaces.current().executeScript(";dialogoCrearRequisicion('dialogoCrearReq');");
    }

    public void buscarRequisicionPorConsecutivoBloque() {
        requisicionVO = requisicionServicioRemoto.buscarRequisicionPorUsuario(getCodigo().toUpperCase(), usuarioBean.getUsuarioConectado().getApCampo().getId(), usuarioBean.getUsuarioConectado().getId(), true, true);
        if (requisicionVO == null) {
            FacesUtilsBean.addErrorMessage("No se encontró la requisición. La requisición debe utiliza el catálogo de artículos del SIA y no debe ser tipo multiproyecto.");
            requisicionVO = null;
        }
    }

    public void generarRequisicion() {
        if (requisicionVO != null) {
            if (requisicionServicioRemoto.crearRequisicionDesdeOtra(usuarioBean.getUsuarioConectado().getId(), requisicionVO)) {
                requisicionVO = null;
                setCodigo("");

                PrimeFaces.current().executeScript(";cerrarNRDO();");
            } else {
                PrimeFaces.current().executeScript(";errorNRDO();");
            }
        }
    }

    public void generarRequisicionDetalle() {
        requisicionVO = requisicionServicioRemoto.buscarPorConsecutivoBloque(requisicionActual.getConsecutivo(), requisicionActual.getApCampo().getId(), true, true);
        generarRequisicion();
    }

    public void seleccionarMalRequisicion(int idRequisicion) {
        try {
            //se toma el parametro
            String accion = FacesUtilsBean.getRequestParameter("myParam");
            //Se toma el Id de la requisicion Id

            setRequisicionActual(requisicionServicioRemoto.find(idRequisicion));
            crearItem = true;
            //--- Verifica si se envio un parametro para procesar la accion correspondiente
            if (accion != null) {
                if ("popupCancelarRequisicion".equals(accion)) {
                    popupBean.setRequisicion(requisicionActual);
                    popupBean.toggleModalCancelar();
                } else {
                    if ("popupCrearRequisicion".equals(accion)) {
                        operacionRequisicion = UPDATE_OPERATION;
                    } else {
                        if ("popupDevolverRequisicion".equals(accion)) {
                            popupBean.setRequisicion(requisicionActual);
                            popupBean.toggleModalDevolver();
                        } else {
                            if ("solicitarRequisicion".equals(accion)) {
                                requisicionActual.setProveedor("Licitación por Depto. de Compras");

                                menuBarBean.procesarAccion(accion);
                            } else {
                                menuBarBean.procesarAccion(accion);
                            }
                        }
                    }

                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void mostrarCatalogo() {
        try {
            menuBarBean.procesarAccion("catalogoServicios");
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void crearNuevaRequisicion() {
        try {
            setRequisicionActual(new Requisicion());
            setTipoRequisicion(TipoRequisicion.PS.name());
            //Se agrega el campo a la requisicion
            requisicionActual.setApCampo(usuarioBean.getUsuarioConectado().getApCampo());
            requisicionActual.setSolicita(usuarioBean.getUsuarioConectado());
            requisicionActual.setCompania(usuarioBean.getCompania()); // se toma la compania de la sesion del usuarios
            requisicionActual.setGerencia(new Gerencia());
            requisicionActual.setProyectoOt(new ProyectoOt());
            //----------Carbiar el status de la requisicion 1 = Pendiente-------
            requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_PENDIENTE));
            // Marca la opreción
            operacionRequisicion = CREATE_OPERATION;
            //llena las gerencias
            setListaProyectosOT(new ArrayList<>());
            setListaUnidadCosto(new ArrayList<>());
            setListaG(gerenciaBean.listaGerenciasConAbreviatura(usuarioBean.getUsuarioConectado().getApCampo().getId()));

            setIdGerencia(0);
            setIdProyectoOT(0);
            setIdUnidadCosto(0);
            setIdNombreTarea(0);
            setIdRevisa("revisa");
            setIdAprueba("aprueba");
//

        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void cambiarValorGerencia() {
        if (idGerencia > 0) {
            if (tipoRequisicion.equals(TipoRequisicion.PS.name())) {
                proyectosOt = listaProyectoPorGerencia(getIdGerencia(), requisicionActual.getApCampo().getId(), getIdProyectoOT());
                if (isContineOt()) {
                    this.listaUnidadCosto = listaUnidadCosto(getIdGerencia(), getIdProyectoOT(), 0);
                    setContineOt(false);
                } else {
                    this.listaUnidadCosto = new ArrayList<>();
                    setContineOt(false);
                }
            } else {
                proyectosOt = gerenciaBean.traerProyectoActivoFijo(getIdGerencia(), usuarioBean.getUsuarioConectado().getApCampo().getId());
            }
        } else {
            this.listaProyectosOT = new ArrayList<>();
            this.listaUnidadCosto = new ArrayList<>();
        }
        idProyectoOT = 0;
        idUnidadCosto = 0;
        idNombreTarea = 0;
    }

    public void cambiarGerencia() {
        if (idGerencia != 0) {
            proyectosOt = listaProyectoPorGerencia(getIdGerencia(), usuarioBean.getUsuarioConectado().getApCampo().getId());
        } else {
            proyectosOt = null;
        }
        idProyectoOT = 0;
        idUnidadCosto = 0;
        idNombreTarea = 0;
    }

    public void cambiarValorProyectoOt() {
        if (idProyectoOT != 0) {
            listaUnidadCosto = listaUnidadCosto(getIdGerencia(), getIdProyectoOT(), 0);
        } else {
//	    requisicion.getProyectoOt().setId(null);
            idNombreTarea = 0;
            idUnidadCosto = 0;
            listaUnidadCosto = null;
        }
    }
    //--- Lista de Usuarios que revisan requisicion

    public List<SelectItem> getListaRevisan() {
        if (requisicionActual != null && this.requisicionActual.getSolicita() != null) {
            return usuarioBean.listaRevisa(usuarioBean.getUsuarioConectado().getId());
        }
        return null;
        //return usuarioBean.listaRevisa(usuarioBean.getUsuarioConectado().getId());
    }

    public List<SelectItem> getListaAprueban() {
        if (requisicionActual != null && this.requisicionActual.getSolicita() != null && getIdRevisa() != null) {
            return usuarioBean.getListaAprueban(usuarioBean.getUsuarioConectado().getId(), getIdRevisa());
        }
        return null;
    }

    public void cambiarValorRevisa() {
        //this.requisicion.setRevisa(this.usuarioBean.buscarPorNombre(event.getNewValue()));
        getListaAprueban();
    }

    public void completarActualizacionRequisicion() {
        try {
            if (operacionRequisicion.equals(UPDATE_OPERATION)) {
                List<RequisicionDetalleVO> rd = requisicionServicioRemoto.getItemsAnalistaNativa(requisicionActual.getId(), false);
                //Verifica cambios
                //Se agrega el campo a la requisicion
                if (getTipoRequisicion().equals(TipoRequisicion.PS.name())) {
                    if (requisicionActual.getApCampo() != null && "N".equals(requisicionActual.getApCampo().getTipo())) {
                        requisicionActual.setTipo(TipoRequisicion.PS);
                        if (rd != null && rd.size() > 0) {
                            if (requisicionActual.getGerencia().getId() != getIdGerencia()
                                    || requisicionActual.getProyectoOt().getId() != getIdProyectoOT()
                                    || (requisicionActual.getOcUnidadCosto() != null && requisicionActual.getOcUnidadCosto().getId() != getIdUnidadCosto())
                                    || (requisicionActual.getOcUnidadCosto() == null && getIdUnidadCosto() > 0)) {
                                limpiarItems(rd, getIdProyectoOT(), getIdUnidadCosto());
                            }
                        }
                        requisicionActual.setOcUnidadCosto(ocUnidadCostoImpl.find(getIdUnidadCosto()));
                        requisicionActual.setProyectoOt(proyectoOtImpl.find(getIdProyectoOT()));
                    } else if (requisicionActual.getApCampo() != null && "C".equals(requisicionActual.getApCampo().getTipo())) {
                        requisicionActual.setTipo(TipoRequisicion.PS);
                    }
                } else {
                    if (requisicionActual.getApCampo() != null && "N".equals(requisicionActual.getApCampo().getTipo())) {
                        requisicionActual.setTipo(TipoRequisicion.AF);
                        requisicionActual.setOcUnidadCosto(null);
                        if (requisicionActual.getProyectoOt() != null && requisicionActual.getProyectoOt().getId() != getIdProyectoOT()) {
                            requisicionActual.setProyectoOt(proyectoOtImpl.find(getIdProyectoOT()));
                            limpiarItems(rd, getIdProyectoOT(), 0);
                        }
                    } else if (requisicionActual.getApCampo() != null && "C".equals(requisicionActual.getApCampo().getTipo())) {
                        requisicionActual.setTipo(TipoRequisicion.AF);
                        requisicionActual.setOcUnidadCosto(null);
                    }
                }
                //Gerencia

                requisicionActual.setGerencia(gerenciaImpl.find(getIdGerencia()));

                //cadenas mando
                requisicionActual.setRevisa(usuarioImpl.find(getIdRevisa()));
                requisicionActual.setAprueba(usuarioImpl.find(getIdAprueba()));
                requisicionActual.setTipoObra(null);
                //
                requisicionServicioRemoto.edit(requisicionActual);

                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("RequiBean.exito.actualizar"));
            } else {

                //Gerencia
                requisicionActual.setGerencia(gerenciaImpl.find(getIdGerencia()));
                //
                requisicionActual.setProyectoOt(proyectoOtImpl.find(getIdProyectoOT()));
                //
                if (getTipoRequisicion().equals(TipoRequisicion.PS.name())) {
                    requisicionActual.setOcUnidadCosto(ocUnidadCostoImpl.find(getIdUnidadCosto()));
                    requisicionActual.setTipo(TipoRequisicion.PS);
                } else {
                    requisicionActual.setTipo(TipoRequisicion.AF);
                }
                //cadenas mando
                requisicionActual.setRevisa(usuarioImpl.find(getIdRevisa()));
                requisicionActual.setAprueba(usuarioImpl.find(getIdAprueba()));
                //
                requisicionActual.setRechazada(Constantes.BOOLEAN_FALSE);
                requisicionActual.setFechaElaboracion(new Date());
                //Usuario genero
                requisicionActual.setGenero(usuarioBean.getUsuarioConectado());
                requisicionActual.setFechaGenero(new Date());
                requisicionActual.setHoraGenero(new Date());
                requisicionActual.setEliminado(Constantes.NO_ELIMINADO);
                requisicionActual.setMultiproyecto(Constantes.NO_ELIMINADO);
                requisicionActual.setOcMetodoPago(new OcMetodoPago(1));
                requisicionServicioRemoto.create(requisicionActual);
                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("RequiBean.exito.crear"));
                setTipoRequisicion("");
                // Para ver el link de crear Ítem
                crearItem = true;
                //Esto es para cerrar el panel emergente

            }
            //
            setIdGerencia(0);
            setIdGerenciaCompra(0);
            setIdProyectoOT(0);
            setIdUnidadCosto(0);
            setIdNombreTarea(0);
            requisicionActual = null;
//

            //toggleModal();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCrearNewReq);");
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
            FacesUtilsBean.addInfoMessage(
                    "No se pudo " + operacionRequisicion.toLowerCase()
                    + " la requisición.  Error inesperado: por favor notifique el problema a: soportesia@ihsa.mx  "
                    + ex.toString()
            );
        }
    }

    private void limpiarItems(List<RequisicionDetalleVO> lrd, int proyOTID, int ocUnidadCID) {
        for (RequisicionDetalleVO requisicionDetalleVO : lrd) {
            requisicionDetalleImpl.limpiarTareaItems(requisicionDetalleVO.getIdRequisicionDetalle(), proyOTID, ocUnidadCID, usuarioBean.getUsuarioConectado().getId());
        }
        getItemsActualizar();
    }

    public void cancelarModificaRequisicion() {
        requisicionActual = null;
        crearItem = false;
        setListaG(null);
        setListaProyectosOT(null);
        setListaUnidadCosto(null);
        setIdGerencia(0);
        setIdProyectoOT(0);
        setIdUnidadCosto(0);
        setIdNombreTarea(0);
        PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCrearNewReq);");
        //toggleModal(event);
    }

    public void eliminarRequisicion() {
        try {
//            RequisicionVO r = (RequisicionVO) listaRequisiciones.getRowData();
//            setRequisicionActual(requisicionServicioRemoto.find(requiVO.getId()));

            if (requisicionActual.getConsecutivo() == null) {
                //Si no tiene Consecutivo asignado Eliminarla

                eliminarDirectorioEts();
                //si tiene consecutivo marcarla como cancelada
                requisicionActual.setEstatus(estatusBean.getPorId(50)); // 50 = Cancelada
                requisicionActual.setFechaCancelo(new Date());
                requisicionActual.setHoraCancelo(new Date());
                requisicionActual.setCancelo(usuarioBean.getUsuarioConectado());
                //Modifico
                requisicionActual.setModifico(usuarioBean.getUsuarioConectado());
                requisicionActual.setFechaModifico(new Date());
                requisicionActual.setHoraModifico(new Date());
                requisicionActual.setEliminado(Constantes.ELIMINADO);
                requisicionActual.setMotivoCancelo("El usuario eliminó la requisición . . .");
                requisicionServicioRemoto.edit(requisicionActual);
            } else {
                //si tiene consecutivo marcarla como cancelada
                requisicionActual.setEstatus(estatusBean.getPorId(50)); // 50 = Cancelada
                requisicionActual.setFechaCancelo(new Date());
                requisicionActual.setHoraCancelo(new Date());
                requisicionActual.setCancelo(usuarioBean.getUsuarioConectado());
                requisicionActual.setMotivoCancelo("Se cancela por que el usuario decidió eliminar la requisición y por algún motivo esta ya tenía un consecutivo asignado");
                requisicionServicioRemoto.edit(requisicionActual);
                // Finalizar notas si tiene
                finalizarNotas();
            }

            //---- Mostrar mensaje  ---
            FacesUtilsBean.addInfoMessage("La requisición fue eliminada correctamente...");
            //Esto es para Quitar las lineas seleccionadas
            cambiarRequisicion(0);
            PrimeFaces.current().executeScript(";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
        } catch (Exception ex) {
            FacesUtilsBean.addInfoMessage("No se pudo eliminar la requisición.  Error: por favor notifique el problema a: soportesia@ihsa.mx" + ex.toString());
        }
    }

    public void eliminarDirectorioEts() {
        //uploadDirectory
        String ud = parametrosSistema.find(1).getUploadDirectory();
        //related path
        String rp = "ETS/Requisicion/" + requisicionActual.getId().toString();
        // antes eliminar todos los archivos q contiene el directorio
        for (SiAdjunto adjunto : servicioSiAdjuntoImpl.traerArchivos(1, requisicionActual.getId(), "ETS-REQ")) {
            try {
                File file = new File(ud + adjunto.getUrl());
                file.delete();
            } catch (Exception e) {
                LOGGER.fatal(this, e.getMessage(), e);
            }
        }
        // sin archivos ya se puede eliminar el directorio
        File sessionfileUploadDirectory = new File(ud + rp);
        if (sessionfileUploadDirectory.isDirectory()) {
            try {
                sessionfileUploadDirectory.delete();
            } catch (SecurityException e) {
                LOGGER.fatal(this, e.getMessage(), e);
            }
        }
    }

    /**
     * Este metodo sirve para cancelar una requisición
     *
     * @param
     */
    @Deprecated
    public void cancelarRequisicion() {
        try {
            // se toma la requisición del panel emergente Cancelar requisición
            setRequisicionActual(popupBean.getRequisicion());
            //Asigno fecha en que se cancela la requisiciòn
            requisicionActual.setFechaCancelo(new Date());
            requisicionActual.setHoraCancelo(new Date());
            requisicionActual.setCancelo(usuarioBean.getUsuarioConectado());
            // Esto Sirve para probar la aplicaciòn
            if (notificacionRequisicionImpl.envioCancelacionRequisicion(
                    getDestinatarios(requisicionActual),
                    "",
                    "",
                    new StringBuilder().append("REQUISICIÓN: ").append(requisicionActual.getConsecutivo()).append(" CANCELADA").toString(),
                    requisicionActual,
                    "cancelo")) {
                //Si mando el correo se actualiza la requisición
                requisicionActual.setEstatus(estatusBean.getPorId(50)); // 50 = Cancelada
                requisicionServicioRemoto.edit(requisicionActual);
                // Finalizar notas si tiene
                finalizarNotas();

                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage("Requisición(es) cancelada(s) correctamente...");
                //Esto es para Quitar las lineas seleccionadas
                cambiarRequisicion(0);
                //Esto es para cerrar el panel emergente de cancelar requisicion
                PrimeFaces.current().executeScript(";cerrarCancelar();");
            }//------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage("No se pudo cancelar la requisición, por favor notifique el problema a: soportesia@ihsa.mx");

            }
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    private String getDestinatarios(Requisicion requisicion) {
        StringBuilder destinatariosSB = new StringBuilder();
        //Verifica si la req es de costos o contabilidad

        String correoFinanza
                = traerCorreoAsigna(
                        requisicion.getTipo().equals(TipoRequisicion.AF)
                        ? Constantes.ROL_VISTO_BUENO_CONTABILIDAD
                        : Constantes.ROL_VISTO_BUENO_COSTO,
                        Constantes.MODULO_REQUISICION,
                        requisicion.getApCampo().getId()
                );

        switch (requisicion.getEstatus().getId()) {
            case Constantes.REQUISICION_SOLICITADA:
                destinatariosSB.append(requisicion.getSolicita().getDestinatarios());
                break;
            case Constantes.REQUISICION_REVISADA:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(requisicion.getRevisa().getEmail());
                destinatariosSB.append(',').append(correoFinanza);
                break;
            case Constantes.REQUISICION_APROBADA:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(requisicion.getRevisa().getEmail());
                destinatariosSB.append(',').append(requisicion.getAprueba().getEmail());
                destinatariosSB.append(',').append(correoFinanza);
                break;
            case Constantes.REQUISICION_VISTO_BUENO_C:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                break;
            case Constantes.REQUISICION_VISTO_BUENO:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(requisicion.getRevisa().getEmail());
                destinatariosSB.append(',').append(requisicion.getAprueba().getEmail());
                destinatariosSB.append(',').append(correoFinanza);
                destinatariosSB.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                break;
            case Constantes.REQUISICION_ASIGNADA:
                destinatariosSB.append(requisicion.getSolicita().getEmail());
                destinatariosSB.append(',').append(requisicion.getRevisa().getEmail());
                destinatariosSB.append(',').append(requisicion.getAprueba().getEmail());
                destinatariosSB.append(',').append(correoFinanza);
                destinatariosSB.append(',').append(usuarioBean.getUsuarioConectado().getEmail());
                break;
            default:
                destinatariosSB.append(usuarioBean.getUsuarioConectado().getEmail());
                break;
        }
        return destinatariosSB.toString();
    }

    public void rechazarRequisicion() {
        error = false;
        try {
            if (popupBean.getRechazo().getMotivo().trim().isEmpty()) {
                mensajeError = "Es necesario agregar un motivo de rechazo de la requisición";
                error = true;
            } else {

                // se toma la requisición y motivo de rechazo del panel emergente rechazar requisición
                //setRequisicionActual(popupBean.getRequisicion());
                Rechazo rechazo = new Rechazo();

                //Asigno requisicion fecha hora y usuario de rechazo
                rechazo.setMotivo(popupBean.getRechazo().getMotivo());
                rechazo.setRequisicion(requisicionActual);
                rechazo.setFecha(new Date());
                rechazo.setHora(new Date());
                rechazo.setCumplido(Constantes.BOOLEAN_FALSE);
                rechazo.setRechazo(usuarioBean.getUsuarioConectado());

                UsuarioVO uvo
                        = usuarioImpl.traerResponsableGerencia(
                                requisicionActual.getApCampo().getId(),
                                Constantes.GERENCIA_ID_COMPRAS,
                                requisicionActual.getCompania().getRfc()
                        );

                if (notificacionRequisicionImpl.envioRechazoRequisicion(
                        getDestinatarios(requisicionActual),
                        "", uvo.getMail(),
                        new StringBuilder().append("REQUISICIÓN: ").append(requisicionActual.getConsecutivo()).append(" DEVUELTA").toString(),
                        requisicionActual,
                        rechazo)) {
                    //-- Marcar la requisicion como rechazada y regresarla al solicitante
                    requisicionActual.setRechazada(Constantes.BOOLEAN_TRUE);
                    requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_PENDIENTE)); // 1 = Pendiente
                    //-- Quitar las fechas y hora de operaciones realizadas
                    requisicionActual.setFechaReviso(null);
                    requisicionActual.setHoraReviso(null);
                    requisicionActual.setFechaAprobo(null);
                    requisicionActual.setHoraAprobo(null);
                    requisicionActual.setFechaAutorizo(null);
                    requisicionActual.setHoraAutorizo(null);
                    requisicionActual.setFechaVistoBueno(null);
                    requisicionActual.setHoraVistoBueno(null);
                    //actualiza la requisición
                    requisicionServicioRemoto.edit(requisicionActual);
                    //actualizar rechazo
                    requisicionServicioRemoto.crearRechazo(rechazo);
                    //finaliza notas
                    finalizarNotas();
                    //---- Mostrar mensaje  ----

                    FacesUtilsBean.addInfoMessage("Requisición(es) devuelta(s) correctamente...");
                    //Esto es para Quitar las lineas seleccionadas
                    cambiarRequisicion(0);
                    requisicionActual = null;
                    //Esto es para cerrar el panel emergente de cancelar requisicion
                    PrimeFaces.current().executeScript(";cerrarDevolver();");
                } else {
                    //---- Mostrar mensaje  ----
                    FacesUtilsBean.addInfoMessage("No se pudo devolver la requisición, por favor notifique el problema a: soportesia@ihsa.mx");
                }
            }
            //
            contarPendiente();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    public void completarSolicitudRequisicion() {
        try {
            //Asigno fecha en que se solicita la requisiciòn
//            if (requisicionActual.getFechaSolicito() == null) {
            requisicionActual.setFechaSolicito(new Date());
//            }
//            if (requisicionActual.getHoraSolicito() == null) {
            requisicionActual.setHoraSolicito(new Date());
//            }
            requisicionActual.setFechaRequerida(fechaRequerida);
            requisicionActual.setRechazada(Constantes.BOOLEAN_FALSE);

            //--- Checar si ya tiene consecutivo asignado antes de darle esto
            if (requisicionActual.getConsecutivo() == null) {
                //Verifica por compania
                requisicionActual.setConsecutivo(folioBean.getFolio("REQUISICION_CONSECUTIVO", requisicionActual.getApCampo().getId()));
            }
            if (Constantes.RFC_MPG.equals(requisicionActual.getCompania().getRfc())
                    && !TipoRequisicion.AF.equals(requisicionActual.getTipo())) {
                requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_SOLICITADA)); // 10 = Solicitada para MPG
            } else {
                requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_VISTO_BUENO_C)); // 25 = Visto Bueno or costo
            }
            //requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_SOLICITADA)); // 10 = Solicitada
            requisicionActual.setPrioridad(requisicionServicioRemoto.calcularPrioridad(requisicionActual.getFechaRequerida(),
                    requisicionActual.getFechaSolicito()));
            // Esto Sirve para probar la aplicaciòn
            //Si mando la notificación
            requisicionActual.setNueva(Constantes.BOOLEAN_TRUE);
            if (requisicionActual.getSolicita() != null
                    && requisicionActual.getSolicita().getEmail() != null && !requisicionActual.getSolicita().getEmail().isEmpty()
                    && requisicionActual.getRevisa() != null
                    && requisicionActual.getRevisa().getEmail() != null && !requisicionActual.getRevisa().getEmail().isEmpty()
                    && requisicionActual.getAprueba() != null
                    && requisicionActual.getAprueba().getEmail() != null && !requisicionActual.getAprueba().getEmail().isEmpty()
                    && notificacionRequisicionImpl.envioSolicitudRequisicion(requisicionActual.getSolicita().getEmail(),
                            "", "", new StringBuilder().append("SOLICITÓ LA REQUISICIÓN: ").append(requisicionActual.getConsecutivo()).toString(),
                            requisicionActual)) {
                //---- Enviar correos
                if (requisicionActual.getTipo().equals(TipoRequisicion.AF)) {
                    String correo = traerCorreoAsigna(Constantes.ROL_VISTO_BUENO_CONTABILIDAD, Constantes.MODULO_REQUISICION, requisicionActual.getApCampo().getId());
                    //requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_VISTO_BUENO_C)); // 25 = Visto Bueno
                    if (notificacionRequisicionImpl.envioNotificacionRequisicion(
                            correo, "", "", "REVISAR LA REQUISICIÓN: " + requisicionActual.getConsecutivo(),
                            requisicionActual, "solicito", "", "")) {
                        requisicionServicioRemoto.edit(requisicionActual);
                        //---- Mostrar mensaje  ----

                        FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.enviado"));
                        //Esto es para Quitar las lineas seleccionadas
                        // válida si la requisicion trea articulos en convenio
                        requisicionServicioRemoto.verificaArticuloItemTieneContrato(requisicionActual, usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId());
                        cambiarRequisicion(0);
                        String jsMetodo = ";regresarSolitar();";
                        PrimeFaces.current().executeScript(jsMetodo);

                    } //------- Si el correo no se pudo enviar  -----
                    else {
                        //---- Mostrar mensaje  ----
                        FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correo.noenviado"));
                    }
                } //tipo af
                else {  // Rquisicion operativa PS
                    String correoAsigna = "";
                    if (Constantes.RFC_MPG.equals(requisicionActual.getCompania().getRfc())) {
                        correoAsigna = requisicionActual.getRevisa().getEmail();
                    } else {
                        correoAsigna = traerCorreoAsigna(Constantes.ROL_VISTO_BUENO_COSTO, Constantes.MODULO_REQUISICION, requisicionActual.getApCampo().getId());
                    }
                    // Esto Sirve para probar la aplicaciòn
                    LOGGER.info(this, "Correo de revisa visto bueno costo: " + correoAsigna);
                    if (notificacionRequisicionImpl.envioNotificacionRequisicion(correoAsigna, "", "", "REVISAR LA REQUISICIÓN: " + requisicionActual.getConsecutivo(),
                            requisicionActual, "solicito", "", "")) {
                        requisicionServicioRemoto.edit(requisicionActual);
                        //---- Mostrar mensaje  ----

                        FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.enviado"));
                        // válida si la requisicion trea articulos en convenio
                        requisicionServicioRemoto.verificaArticuloItemTieneContrato(requisicionActual, usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId());
                        //Esto es para Quitar las lineas seleccionadas
                        cambiarRequisicion(0);
                        String jsMetodo = ";regresarSolitar();";
                        PrimeFaces.current().executeScript(jsMetodo);

                    } //------- Si el correo no se pudo enviar  -----
                    else {
                        //---- Mostrar mensaje  ----
                        FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correo.noenviado"));
                    }
                }
            } else {
                //---- Mostrar mensaje  ----
                if (requisicionActual.getSolicita() == null
                        || Strings.isNullOrEmpty(requisicionActual.getSolicita().getEmail())
                        || requisicionActual.getRevisa() == null
                        || Strings.isNullOrEmpty(requisicionActual.getRevisa().getEmail())
                        || requisicionActual.getAprueba() == null
                        || Strings.isNullOrEmpty(requisicionActual.getAprueba().getEmail())) {
                    FacesUtilsBean.addErrorMessage("Se requiere validar la cadena de mando de la requisicion.");
                } else {
                    FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correo.noenviado"));
                }
            }
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarReqSinSolicitar();
            //
            menuBarBean.procesarAccion("crearRequisicion");
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    /**
     * Este metodo sirve para marcar como revisada una requisición
     *
     * @param
     */
    public void revisarRequisicion() {
        try {
            if (requisicionActual == null) {
                boolean entro = false;
                for (Object object : listaRequisiciones) {
                    RequisicionVO o = (RequisicionVO) object;
                    if (o.isSelected()) {
                        setRequisicionActual(requisicionServicioRemoto.find(o.getId()));
                        //revisarRequisicion(requisicionActual);
                        requisicionServicioRemoto.revisarRequisicion(requisicionActual, usuarioBean.getUsuarioConectado());
                        if (!entro) {
                            entro = true;
                        }
                    }
                }
                if (entro) {

                    FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.REVenviado"));
                    cambiarRequisicion(0);
                }
                String jsMetodo = ";limpiarTodos();";
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                //revisarRequisicion(requisicionActual);
                requisicionServicioRemoto.revisarRequisicion(requisicionActual, usuarioBean.getUsuarioConectado());

                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.REVenviado"));
                cambiarRequisicion(0);
                String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
                PrimeFaces.current().executeScript(jsMetodo);
            }
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarReqSinRevisar();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    /*@Deprecated
    private void revisarRequisicion(Requisicion requiActual) {
        //Asigno fecha en que se cancela la requisiciòn
        requiActual.setFechaReviso(new Date());
        requiActual.setHoraReviso(new Date());
        requiActual.setRevisa(usuarioBean.getUsuarioConectado());
        requiActual.setEstatus(estatusBean.getPorId(15)); // 15 = Revisada        
        //Valida si hay items autorizados
        if (requisicionServicioRemoto.getItemsPorRequisicion(requiActual.getId(), true, false) != null) {
            
            final String asunto  = new StringBuilder().append("APROBAR LA REQUISICIÓN: ").append(requiActual.getConsecutivo()).toString();
            
            if (notificacionRequisicionImpl.envioNotificacionRequisicion(
                    requiActual.getAprueba().getEmail(),
                    "",
                    "",
                    asunto,
                    requiActual, "solicito", "revisar")) {
                requisicionServicioRemoto.edit(requiActual);                
                // Finalizar notas si tiene
                finalizarNotas();
                //Enviar mensaje al movil
                notificacionMovilImpl
                            .enviarNotificacionRequisicion(
                                        requiActual,
                                        requiActual.getAprueba(),
                                        asunto
                            );
            } //------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correo.REVnoenviado"));
            }
        } else {
            FacesUtilsBean.addInfoMessage("No se puede revisar una requisición sin items autorizados. ");
        }
    }*/
    /**
     * Este metodo sirve para aprobar una requisición
     *
     * @param
     */
    public void aprobarRequisicion() {
        try {
            if (requisicionActual == null) {
                boolean entro = false;
                for (Object object : listaRequisiciones) {
                    RequisicionVO o = (RequisicionVO) object;
                    if (o.isSelected()) {
                        setRequisicionActual(requisicionServicioRemoto.find(o.getId()));
                        //aprobarRequision(requisicionActual);
                        requisicionServicioRemoto.aprobarRequisicion(requisicionActual, usuarioBean.getUsuarioConectado());
                        if (!entro) {
                            entro = true;
                        }
                    }
                }
                if (entro) {

                    FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.APRenviado"));
                    cambiarRequisicion(0);
                }
                String jsMetodo = ";limpiarTodos();";
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                //aprobarRequision(requisicionActual);
                requisicionServicioRemoto.aprobarRequisicion(requisicionActual, usuarioBean.getUsuarioConectado());

                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.APRenviado"));
                cambiarRequisicion(0);
                String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
                PrimeFaces.current().executeScript(jsMetodo);
            }
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarReqSinAprobar();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        requisicionActual = null;
    }

    public void activarRequisicion() {
        try {
            StringBuilder requiOK = new StringBuilder();
            StringBuilder requiError = new StringBuilder();

            if (activarRequisicionProceso(requisicionActual)) {

                FacesUtilsBean.addInfoMessage(
                        new StringBuilder().append("La  requisicion ")
                                .append(requisicionActual.getConsecutivo())
                                .append(" se reactivo correctamente...").toString());
                cambiarRequisicion(0);
                String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                FacesUtilsBean.addErrorMessage(
                        new StringBuilder().append("No se pudo activar la requisicion: ")
                                .append(requisicionActual.getConsecutivo()).append(". Por favor notifique el problema a: soportesia@ihsa.mx").toString());
            }

            cambiarRequisicion(0);
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarRecReq();
            contarBean.llenarReqEnEspera();
            contarBean.llenarReqEnEsperaAdm();
        } catch (Exception ex) {
            LOGGER.fatal(this, "", ex);
            FacesUtilsBean.addErrorMessage("Por favor pongase en contacto con el equipo de desarrollo al correo soportesia@ihsa.mx");
        }
    }

    /* @Deprecated
    private void aprobarRequision(Requisicion requiActual) {
        //Asigno fecha en que se cancela la requisiciòn
        requiActual.setFechaAprobo(new Date());
        requiActual.setHoraAprobo(new Date());
        requiActual.setAprueba(usuarioBean.getUsuarioConectado());
        if (requisicionServicioRemoto.getItemsPorRequisicion(requiActual.getId(), true, false) == null) {
            FacesUtilsBean.addInfoMessage("No se puede aprobar una requisición sin items autorizados. ");
        } else {
            if (requisicionActual.isNueva()) { // La requisicion es nueva y pasa  a compras
                aprobarNuevaRequision();
            } else {
                enviarRequisicionFinanza();
            }
        }
    }

    @Deprecated
    private void aprobarNuevaRequision() {
        String correoAsigna = "";
        if (requisicionActual.getTipo().equals(TipoRequisicion.AF)) {
            correoAsigna = traerCorreoAsigna(Constantes.ROL_ASIGNA_REQUISICION, Constantes.MODULO_REQUISICION, requisicionActual.getApCampo().getId());
            requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_APROBADA)); // 25 = Visto Bueno
            if (!correoAsigna.isEmpty() && notificacionRequisicionImpl.envioNotificacionRequisicion(
                    correoAsigna, "", "", "ASIGNAR LA REQUISICIÓN: " + requisicionActual.getConsecutivo(),
                    requisicionActual, "solicito", "revisar", "aprobar")) {
                requisicionServicioRemoto.edit(requisicionActual);
            } //------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.APRnoenviado"));
            }
        } else {  // Rquisicion operativa PS
            requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_APROBADA)); // 35 = asignar
            correoAsigna = traerCorreoAsigna(Constantes.ROL_ASIGNA_REQUISICION, Constantes.MODULO_REQUISICION, requisicionActual.getApCampo().getId());
            // Esto Sirve para probar la aplicaciòn
            //LOGGER.info(this, "Correo de revisa visto bueno costo: " + correoAsigna);
            if (!correoAsigna.isEmpty() && notificacionRequisicionImpl.envioNotificacionRequisicion(correoAsigna, "", "", "ASIGNAR LA REQUISICIÓN: " + requisicionActual.getConsecutivo(),
                    requisicionActual, "solicito", "revisar", "aprobar")) {
                requisicionServicioRemoto.edit(requisicionActual);
            } //------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.APRnoenviado"));
            }
        }
        // Finalizar notas si tiene
        finalizarNotas();
        // Envia la notificacion al movil AQUI        
                    
    }

    @Deprecated
    private void enviarRequisicionFinanza() {
        requisicionActual.setEstatus(estatusBean.getPorId(Constantes.REQUISICION_VISTO_BUENO_C)); // 10 = solicitada
        // Esto Sirve para probar la aplicaciòn
        String correoRevisa = traerCorreoAsigna(Constantes.ROL_VISTO_BUENO_COSTO, Constantes.MODULO_REQUISICION, requisicionActual.getApCampo().getId());
        if (notificacionRequisicionImpl.envioNotificacionRequisicion(
                correoRevisa, "", "", "REVISAR LA REQUISICIÓN: " + requisicionActual.getConsecutivo(),
                requisicionActual, "solicito", "revisar", "aprobar")) {
            requisicionServicioRemoto.edit(requisicionActual);
            // Finalizar notas si tiene
            finalizarNotas();
        } //------- Si el correo no se pudo enviar  -----
        else {
            //---- Mostrar mensaje  ----
            FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.APRnoenviado"));
        }
    }*/
    private String traerCorreoAsigna(int rol, int modulo, int campo) {
        StringBuilder sb = new StringBuilder();
        try {
            List<UsuarioVO> lu = usuarioImpl.traerListaRolPrincipalUsuarioRolModulo(rol, modulo, campo);
            for (UsuarioVO usuarioVO : lu) {
                if (sb.length() == 0) {
                    sb.append(usuarioVO.getMail());
                } else {
                    sb.append(',').append(usuarioVO.getMail());
                }
                LOGGER.info(this, "Nombre asignar: " + usuarioVO.getNombre());
            }
        } catch (Exception e) {
            LOGGER.info(this, "Ocurrio un error al traer los correos de asiganción de orden " + e.getMessage(), e);
            sb = new StringBuilder(Constantes.VACIO);
        }
        return sb.toString();
    }

    public void asignarRequisicion() {
        try {
            StringBuilder requiOK = new StringBuilder();
            StringBuilder requiError = new StringBuilder();

            // .out.println("asdasdadsa sele");
            if (requisicionActual == null) {
                boolean entro = false;
                for (Object object : listaRequisiciones) {
                    RequisicionVO o = (RequisicionVO) object;
                    if (o.isSelected()) {
                        setRequisicionActual(requisicionServicioRemoto.find(o.getId()));
                        if (asignarRequisicionProceso(requisicionActual)) {
                            if (requiOK.toString().isEmpty()) {
                                requiOK.append(requisicionActual.getConsecutivo());
                            } else {
                                requiOK.append(", ").append(requisicionActual.getConsecutivo());
                            }
                        } else {
                            if (requiError.toString().isEmpty()) {
                                requiError.append(requisicionActual.getConsecutivo());
                            } else {
                                requiError.append(", ").append(requisicionActual.getConsecutivo());
                            }
                        }
                        if (!entro) {
                            entro = true;
                        }
                    }
                }
                if (entro) {

                    FacesUtilsBean.addInfoMessage(
                            new StringBuilder().append("La(s) requisicion(es) ")
                                    .append(requiOK.toString())
                                    .append(" se asignaron correctamente...").toString());
                    cambiarRequisicion(0);
                }
                if (requiError.toString().isEmpty()) {
                    FacesUtilsBean.addErrorMessage(
                            new StringBuilder().append("No se pudo asignar la(s) requisicion(es): ")
                                    .append(requiError.toString()).append(". Por favor notifique el problema a: soportesia@ihsa.mx").toString());
                }
                String jsMetodo = ";limpiarTodos();";
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                if (asignarRequisicionProceso(requisicionActual)) {

                    FacesUtilsBean.addInfoMessage(
                            new StringBuilder().append("La(s) requisicion(es) ")
                                    .append(requisicionActual.getConsecutivo())
                                    .append(" se asignaron correctamente...").toString());
                    cambiarRequisicion(0);
                    String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
                    PrimeFaces.current().executeScript(jsMetodo);
                } else {
                    FacesUtilsBean.addErrorMessage(
                            new StringBuilder().append("No se pudo asignar la(s) requisicion(es): ")
                                    .append(requisicionActual.getConsecutivo()).append(". Por favor notifique el problema a: soportesia@ihsa.mx").toString());
                }
            }
            cambiarRequisicion(0);
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarReqSinAsignar();
        } catch (Exception ex) {
            LOGGER.fatal(this, "", ex);
            System.out.println(ex);
            FacesUtilsBean.addErrorMessage("Por favor pongase en contacto con el equipo de desarrollo al correo soportesia@ihsa.mx");
        }
    }

    public boolean asignarRequisicionProceso(Requisicion requiActual) throws Exception {
        boolean exito = false;
        try {
            Requisicion requisicionActualOld = requisicionServicioRemoto.find(requiActual.getId());
            //----- Quien compra
            Usuario compra = usuarioBean.buscarPorId(getIdAnalista());
            requiActual.setCompra(compra);
            if (notificacionRequisicionImpl.envioNotificacionRequisicionPDF(requiActual.getCompra().getEmail(), "", "",
                    new StringBuilder().append("REQUISICIÓN: ").append(requiActual.getConsecutivo()).append(" POR FAVOR COLOCAR LA ORDEN DE COMPRA.").toString(),
                    requiActual, usuarioBean.getUsuarioConectado(),
                    "solicito", "revisar", "aprobar")) {
                if (notificacionRequisicionImpl.envioAutorizadaRequisicion(requiActual.getSolicita().getDestinatarios(), "", "",
                        new StringBuilder().append("REQUISICIÓN: ").append(requiActual.getConsecutivo()).append(" AUTORIZADA.").toString(),
                        requiActual, "solicito", "revisar", "aprobar")) {
                    //---- Mostrar mensaje  ----
                    //Asigno fecha en que se asigna la requisiciòn
                    requiActual = requisicionServicioRemoto.find(requiActual.getId());
                    requiActual.setFechaAsigno(new Date());
                    requiActual.setHoraAsigno(new Date());
                    // ----- Quien asigna
                    requiActual.setAsigna(usuarioBean.getUsuarioConectado());
                    requiActual.setEstatus(estatusBean.getPorId(Constantes.ESTATUS_ASIGNADA)); // 40 = Asignada
                    requiActual.setCompra(compra);
                    requisicionServicioRemoto.edit(requiActual);
                    //cambiarRequisicion(0);
                    exito = true;
                    //Esto es para cerrar el panel emergente de asignar requisicion
                    //   popupBean.toggleModalCancelar();
                } else {//------- Si el correo no se pudo enviar  -----
                    requisicionServicioRemoto.edit(requisicionActualOld);
                    //---- Mostrar mensaje  ----
                    //FacesUtilsBean.addErrorMessage("No se pudo asignar la requisición, " + requiActual.getConsecutivo() + ", por favor notifique el problema a: sia@ihsa.mx");
                }
            } else {
                requisicionServicioRemoto.edit(requisicionActualOld);
                //---- Mostrar mensaje  ----
            }

            if (exito && !requiActual.isContrato()) {
                requisicionServicioRemoto.verificaArticuloItemTieneContrato(requiActual, usuarioBean.getUsuarioConectado().getId(), requiActual.getApCampo().getId());
            }

            filaSeleccionada.clear();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        }
        return exito;
    }

    public boolean activarRequisicionProceso(Requisicion requiActual) throws Exception {
        boolean exito = false;
        try {
            Requisicion requisicionActualOld = requisicionServicioRemoto.find(requiActual.getId());
            RequisicionMovimientoVO moo = new RequisicionMovimientoVO();
            moo.setFecha(new Date());
            moo.setHora(new Date());
            moo.setUsuario(usuarioBean.getUsuarioConectado().getNombre());
            if (notificacionRequisicionImpl.envioActivarRequisicion(requiActual.getSolicita().getEmail(), "", "",
                    new StringBuilder().append("REQUISICIÓN: ").append(requiActual.getConsecutivo()).append(" Reactivada.").toString(),
                    requiActual, moo, "solicito", "revisar", "aprobar", "activar")) {
                requiActual = requisicionServicioRemoto.find(requiActual.getId());
                requiActual.setEstatus(estatusBean.getPorId(Constantes.ESTATUS_ASIGNADA)); // 40 = Asignada                                
                requisicionServicioRemoto.edit(requiActual);

                exito = true;
            } else {
                requisicionServicioRemoto.edit(requisicionActualOld);
            }

            if (exito) {
                requisicionSiMovimientoImpl.saveRequestMove(usuarioBean.getUsuarioConectado().getId(), "REACTIVAR REQUISICION", requisicionActual.getId(), Constantes.ID_SI_OPERACION_ACTIVAR_REQ);
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        }
        return exito;
    }

    public void crearNota(int idReq) {
        // se toma el Id de la requisicion Id
//        RequisicionVO r = (RequisicionVO) listaRequisiciones.getRowData();
        setRequisicionActual(requisicionServicioRemoto.find(idReq));
    }

    public void completarCreacionNota() {
        notaRequisicionBean.completarCreacionNota(usuarioBean.getUsuarioConectado().getNombre(), requisicionActual);
        notaRequisicionBean.setTextoNoticia("");
    }

    /**
     * @return the requisicionActual
     */
    public Requisicion getRequisicionActual() {
        return requisicionActual;
    }

    /**
     * @return Lista de Requisiciones Sin Solicitar
     */
    public DataModel getRequisicionesSinSolicitar() {
        try {
            List<RequisicionVO> lo = new ArrayList<>();
            RequisicionVO o;
            List<Object[]> l
                    = requisicionServicioRemoto.getRequisicionesSinSolicitar(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );
            for (Object[] objects : l) {
                o = new RequisicionVO();
                o.setId((Integer) objects[0]);
                o.setRechazada((Boolean) objects[1]);
                o.setCompania((String) objects[2]);
                o.setIdUnidadCosto(objects[3] != null ? (Integer) objects[3] : 0);
                o.setTipo(objects[4] != null ? (String) objects[4] : "");
                lo.add(o);
            }
            listaRequisiciones = new ListDataModel(lo);

            if (irInicio && requisicionActual != null && requisicionActual.getId() > 0) {
                irInicio = false;
                crearItem = true;
                notaRequisicionBean.setFiltrar(true);
                String jsMetodo = ";activarTab('tabOCSProc',0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');";
                PrimeFaces.current().executeScript(jsMetodo);
            }

        } catch (Exception ex) {
            listaRequisiciones = null;
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return listaRequisiciones;
    }

    /**
     * @return Lista de Requisiciones Sin Revisar
     */
    public DataModel getRequisicionesSinRevisar() {
        try {
            List<RequisicionVO> lo = new ArrayList<>();
            RequisicionVO o;
            List<Object[]> l = requisicionServicioRemoto.getRequisicionesSinRevisar(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId());
            for (Object[] objects : l) {
                o = new RequisicionVO();
                o.setId((Integer) objects[0]);
                o.setConsecutivo(String.valueOf(objects[1]));
                o.setReferencia(String.valueOf(objects[2]));
                o.setFechaSolicitada((Date) objects[3]);
                o.setFechaRequerida((Date) objects[4]);
                o.setPrioridad((String) objects[5]);
                o.setCompania((String) objects[6]);
                o.setMontoPesos(((Double) objects[7]));
                o.setMontoDolares((Double) objects[8]);
                o.setMontoTotalDolares((Double) objects[9]);
                lo.add(o);
            }
            listaRequisiciones = new ListDataModel(lo);

        } catch (Exception ex) {
            listaRequisiciones = null;
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return listaRequisiciones;
    }

    public void requisicionesSinRevisar() {
        try {
            List<RequisicionVO> lo = new ArrayList<>();
            RequisicionVO o;
            List<Object[]> l = requisicionServicioRemoto.getRequisicionesSinRevisar(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId());
            for (Object[] objects : l) {
                o = new RequisicionVO();
                o.setId((Integer) objects[0]);
                o.setConsecutivo(String.valueOf(objects[1]));
                o.setReferencia(String.valueOf(objects[2]));
                o.setFechaSolicitada((Date) objects[3]);
                o.setFechaRequerida((Date) objects[4]);
                o.setPrioridad((String) objects[5]);
                o.setCompania((String) objects[6]);
                o.setMontoPesos(((Double) objects[7]));
                o.setMontoDolares((Double) objects[8]);
                o.setMontoTotalDolares((Double) objects[9]);
                lo.add(o);
            }
            listaRequisiciones = new ListDataModel(lo);

        } catch (Exception ex) {
            listaRequisiciones = null;
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    /**
     * @return Lista de Requisiciones Sin Aprobar
     */
    public DataModel getRequisicionesSinAprobar() {
        try {
            List<RequisicionVO> lo = new ArrayList<>();
            RequisicionVO o;
            List<Object[]> l = requisicionServicioRemoto.getRequisicionesSinAprobar(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId());
            for (Object[] objects : l) {
                o = new RequisicionVO();
                o.setId((Integer) objects[0]);
                o.setConsecutivo(String.valueOf(objects[1]));
                o.setReferencia(String.valueOf(objects[2]));
                o.setFechaSolicitada((Date) objects[3]);
                o.setFechaRequerida((Date) objects[4]);
                o.setPrioridad((String) objects[5]);
                o.setCompania((String) objects[6]);
                o.setMontoPesos(((Double) objects[7]));
                o.setMontoDolares((Double) objects[8]);
                o.setMontoTotalDolares((Double) objects[9]);
                lo.add(o);
            }
            listaRequisiciones = new ListDataModel(lo);

        } catch (Exception ex) {
            listaRequisiciones = null;
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return listaRequisiciones;
    }

    public void requisicionesSinAprobar() {
        try {

            List<RequisicionVO> lo = new ArrayList<>();
            RequisicionVO o;
            List<Object[]> l = requisicionServicioRemoto.getRequisicionesSinAprobar(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId());
            for (Object[] objects : l) {
                o = new RequisicionVO();
                o.setId((Integer) objects[0]);
                o.setConsecutivo(String.valueOf(objects[1]));
                o.setReferencia(String.valueOf(objects[2]));
                o.setFechaSolicitada((Date) objects[3]);
                o.setFechaRequerida((Date) objects[4]);
                o.setPrioridad((String) objects[5]);
                o.setCompania((String) objects[6]);
                o.setMontoPesos(((Double) objects[7]));
                o.setMontoDolares((Double) objects[8]);
                o.setMontoTotalDolares((Double) objects[9]);
                lo.add(o);
            }
            listaRequisiciones = new ListDataModel(lo);

        } catch (Exception ex) {
            listaRequisiciones = null;
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }

    }

    /**
     * @return Lista de Requisiciones Sin Autorizar
     */
    public DataModel getRequisicionesSinAutorizar() {
        try {
            listaRequisiciones = new ListDataModel(requisicionServicioRemoto.getRequisicionesSinAutorizar(usuarioBean.getUsuarioConectado().getId()));
        } catch (Exception ex) {
            listaRequisiciones = null;
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return listaRequisiciones;
    }

    public void seleccionarTabAsignarRequisicion() {
        List<RequisicionVO> lo = requisicionServicioRemoto.getRequisicionesSinAsignar(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                Constantes.ROL_ASIGNA_REQUISICION, Constantes.REQUISICION_APROBADA);
        List<RequisicionVO> lt = new ArrayList<>();
        if (getIndexTab() == 0) {
            for (RequisicionVO rqVo : lo) {
                if (rqVo.getTotal() == 0) {
                    lt.add(rqVo);
                }
            }
        } else {
            for (RequisicionVO rqVo : lo) {
                if (rqVo.getTotal() > 0) {
                    lt.add(rqVo);
                }
            }
        }

        listaRequisiciones = new ListDataModel(lt);
    }

    /**
     * @return Lista de Requisiciones Sin Asignar
     */
    public DataModel getRequisicionesSinAsignar() {
        try {
            // .out.println("aqui dando vueltas " + indexTab);
            totalReqConInventario = 0;
            List<RequisicionVO> lt = new ArrayList<>();
            List<RequisicionVO> lo = requisicionServicioRemoto.getRequisicionesSinAsignar(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    Constantes.ROL_ASIGNA_REQUISICION, Constantes.REQUISICION_APROBADA);
            for (RequisicionVO rqVo : lo) {
                if (rqVo.getTotal() == 0) {
                    lt.add(rqVo);
                } else {
                    totalReqConInventario++;
                }
            }
            listaRequisiciones = new ListDataModel(lt);

        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return listaRequisiciones;
    }

    /**
     * @return Lista de Requisiciones En Espera
     */
    public DataModel getRequisicionesEnEspera() {
        try {
            // .out.println("aqui dando vueltas " + indexTab);
            totalReqConInventario = 0;
            boolean admEsp = isAdminEnEspera();
            List<RequisicionVO> lo = requisicionServicioRemoto.getRequisicionesEnEspera(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    admEsp ? Constantes.ROL_REQUISICION_ESPERA_ADM : Constantes.ROL_REQUISICION_ESPERA, Constantes.REQUISICION_EN_ESPERA, 0, admEsp);
            listaRequisiciones = new ListDataModel(lo);

        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return listaRequisiciones;
    }

    /**
     * @return Lista de Requisiciones Sin Disgregar por un analista
     */
    public DataModel getRequisicionesSinDisgregar() {
        try {
            listaRequisiciones
                    = new ListDataModel(
                            requisicionServicioRemoto.getRequisicionesSinDisgregar(
                                    usuarioBean.getUsuarioConectado().getId(),
                                    usuarioBean.getUsuarioConectado().getApCampo().getId()
                            )
                    );
        } catch (Exception ex) {
            listaRequisiciones = null;
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return listaRequisiciones;
    }

    //----  Zona de Historial de las Requisiciones  -----
    /**
     * @return Lista de Requisiciones Solicitadas
     */
    public DataModel listaRequisicionesSolicitadas() {
        DataModel retVal = null;
        try {
            if (getTipoFiltro().equals(Constantes.FILTRO)) {
                if (getFechaInicio() == null || getFechaInicio().isEmpty()) {
                    setFechaInicio(inicio());
                }
                List<RequisicionVO> lo
                        = requisicionServicioRemoto.buscarRequisicionPorFiltro(
                                usuarioBean.getUsuarioConectado().getId(),
                                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                                Constantes.REQUISICION_SOLICITADA,
                                siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaInicio()),
                                siManejoFechaImpl.cambiarddmmyyyyAyyyymmaa(getFechaFin())
                        );
                if (lo != null) {
                    setRequisicionesSolicitadas(new ListDataModel(lo));
                    retVal = requisicionesSolicitadas;
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return retVal;
    }

    private String inicio() {
        Calendar fechaInicial = Calendar.getInstance();
        fechaInicial.set(Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat("dd/MM/yyyy").format(fechaInicial.getTime());
    }

    /**
     * @return Lista de Requisiciones Revisadas
     */
    public DataModel listaRequisicionesRevisadas() {
        try {
            List<RequisicionVO> lo = new ArrayList<>();
            RequisicionVO r;
            List<Object[]> l = requisicionServicioRemoto.listaRequisicionesRevisadas(usuarioBean.getUsuarioConectado());
            for (Object[] objects : l) {
                r = new RequisicionVO();
                r.setId((Integer) objects[0]);
                r.setConsecutivo(String.valueOf(objects[1]));
                r.setFechaSolicitada((Date) objects[2]);
                r.setFechaRequerida((Date) objects[3]);
                r.setPrioridad((String) objects[4]);
                r.setCompania((String) objects[5]);
                r.setEstatus(((String) objects[6]));
                r.setReferencia(((String) objects[7]));
                lo.add(r);
            }
            setRequisicionesRevisadas(new ListDataModel(lo));

        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
            setRequisicionesRevisadas(null);

        }
        return requisicionesRevisadas;
    }

    /**
     * @return Lista de Requisiciones Aprobadas
     */
    public DataModel listaRequisicionesAprobadas() {
        try {
            List<RequisicionVO> lo = new ArrayList<>();
            RequisicionVO r;
            List<Object[]> l = requisicionServicioRemoto.listaRequisicionesAprobadas(usuarioBean.getUsuarioConectado());
            for (Object[] objects : l) {
                r = new RequisicionVO();
                r.setId((Integer) objects[0]);
                r.setConsecutivo(String.valueOf(objects[1]));
                r.setFechaSolicitada((Date) objects[2]);
                r.setFechaRequerida((Date) objects[3]);
                r.setPrioridad((String) objects[4]);
                r.setCompania((String) objects[5]);
                r.setEstatus(((String) objects[6]));
                r.setReferencia(((String) objects[7]));
                lo.add(r);
            }
            setRequisicionesAprobadas(new ListDataModel(lo));

        } catch (Exception ex) {
            setRequisicionesAprobadas(null);
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
        return requisicionesAprobadas;
    }

    /**
     * @return Lista de Requisiciones Asignadas
     */
    public DataModel listaRequisicionesAsignadas() {
        try {
            List<RequisicionVO> lo = new ArrayList<>();
            RequisicionVO r;
            List<Object[]> l = requisicionServicioRemoto.listaRequisicionesAsignadas(usuarioBean.getUsuarioConectado());
            for (Object[] objects : l) {
                r = new RequisicionVO();
                r.setId((Integer) objects[0]);
                r.setConsecutivo(String.valueOf(objects[1]));
                r.setFechaSolicitada((Date) objects[2]);
                r.setFechaRequerida((Date) objects[3]);
                r.setPrioridad((String) objects[4]);
                r.setCompania((String) objects[5]);
                r.setEstatus(((String) objects[6]));
                r.setReferencia(((String) objects[7]));
                lo.add(r);
            }
            setRequisicionesAsignadas(new ListDataModel(lo));

        } catch (Exception ex) {
            setRequisicionesAsignadas(null);
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
        return requisicionesAsignadas;
    }

    //-----------------  Zona de Items ---------------------
    //Items por Consulata nativa
    public DataModel getItems() {
        return listaItems;
    }

    public void getItemsActualizar() {
        try {
            if (requisicionActual != null && requisicionActual.getId() != null && requisicionActual.getId() > 0) {
                List<RequisicionDetalleVO> lo = new ArrayList<>();
                if (requisicionActual.isMultiproyecto()) {
                    lo = requisicionServicioRemoto.getItemsPorRequisicionMulti(requisicionActual.getId(), true, false);
                } else {
                    lo = requisicionServicioRemoto.getItemsPorRequisicion(requisicionActual.getId(), true, false);
                }
                listaItems = new ListDataModel(lo);
            }
        } catch (Exception ex) {
            listaItems = null;
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public DataModel getItemsProcesoAprobar() {
        return listaItems;
    }

    public void itemsProcesoAprobar() {
        try {
            if (requisicionActual != null && requisicionActual.getId() > 0) {
                List<RequisicionDetalleVO> lo = requisicionServicioRemoto.getItemsPorRequisicion(requisicionActual.getId(), false, false);
                listaItems = new ListDataModel(lo);
            }
        } catch (Exception ex) {
            listaItems = null;
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void itemsProcesoAprobarMulti() {
        try {
            if (requisicionActual != null && requisicionActual.getId() > 0) {
                List<RequisicionDetalleVO> lo = requisicionServicioRemoto.getItemsPorRequisicionMulti(requisicionActual.getId(), false, false);
                listaItems = new ListDataModel(lo);
            }
        } catch (Exception ex) {
            listaItems = null;
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    /**
     * Este metodo sirve para seleccionar un item de requisicion de cualquier
     * parte del sistema
     *
     * @param
     */
    public void seleccionarItem(int idPartida) {
        try {
            if (idPartida > 0) {
                setItemActual(requisicionServicioRemoto.buscarItemPorId(idPartida));
                if (TipoRequisicion.AF.equals(getRequisicionActual().getTipo())
                        || (TipoRequisicion.PS.equals(getRequisicionActual().getTipo())
                        && getRequisicionActual().getOcUnidadCosto() != null)
                        || TipoRequisicion.AI.equals(getRequisicionActual().getTipo())) {

                    listaTO = tipoObraBean.listaTarea(
                            getRequisicionActual().getProyectoOt().getId(),
                            requisicionActual.getGerencia().getId(),
                            requisicionActual.getOcUnidadCosto() == null ? 0 : requisicionActual.getOcUnidadCosto().getId()
                    );
                    operacionItem = UPDATE_OPERATION;
                    popupBean.setItem(getItemActual());
                    popupBean.toggleModalModificar();
                    PrimeFaces.current().executeScript(";abrirDialogoModal(AutorizaCantidadItemsRequi);");
                } else {
                    PrimeFaces.current().executeScript(";alertaGeneral('Es necesario agregar un tipo de tarea a la requisión');");
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void modificarItem(int idPartida) {
        try {
            if (idPartida > 0) {
                setMostrarMultiProyecto(false);
                //RequisicionDetalleVO requisicionDetalleVO = requisicionDetalleImpl.getItemPorIdConsultaNativa(idPartida);
                switch (getRequisicionActual().getTipo()) {
                    case PS:
                        if (getRequisicionActual().getOcUnidadCosto() != null) {
                            setItemActual(requisicionServicioRemoto.buscarItemPorId(idPartida));
                            if (getItemActual() != null && getItemActual().isAutorizado()) {
                                operacionItem = UPDATE_OPERATION;
                                setMultiProyecto(false);
                                if (getItemActual().getOcTarea() != null
                                        && TipoRequisicion.PS.equals(getItemActual().getRequisicion().getTipo())) {
                                    setIdNombreTarea(getItemActual().getOcTarea().getId());
                                    setIdTarea(getItemActual().getOcTarea().getId());
                                    if ("C".equals(getRequisicionActual().getApCampo().getTipo())
                                            && getItemActual().getOcTarea().getOcActividadPetrolera() != null) {
                                        setIdTarea(getItemActual().getOcTarea().getOcCodigoTarea().getId());
                                        if (getItemActual() != null) {
                                            if (getItemActual().getOcActividadpetrolera() != null) {
                                                setIdActPetrolera(getItemActual().getOcActividadpetrolera().getId());
                                            }
                                            if (getItemActual().getOcPresupuesto() != null) {
                                                setIdPresupuesto(getItemActual().getOcPresupuesto().getId());
                                            }
                                            if (getItemActual().getAnioPresupuesto() != null) {
                                                setAnioPresupuesto(getItemActual().getAnioPresupuesto());
                                            }
                                            if (getItemActual().getMesPresupuesto() != null) {
                                                setMesPresupuesto(getItemActual().getMesPresupuesto());
                                            }
                                        }
                                    } else if ("N".equals(getRequisicionActual().getApCampo().getTipo())) {
                                        setListaTO(tipoObraBean.listaTarea(
                                                getRequisicionActual().getProyectoOt().getId(),
                                                requisicionActual.getGerencia().getId(),
                                                requisicionActual.getOcUnidadCosto() != null
                                                ? requisicionActual.getOcUnidadCosto().getId() : 0));
                                    }
                                }

                                if ("C".equals(getRequisicionActual().getApCampo().getTipo())) {
                                    if (getItemActual().getProyectoOt() != null) {
                                        setIdProyectoOT(getItemActual().getProyectoOt().getId());
                                    }
                                    if (getItemActual().getOcUnidadCosto() != null) {
                                        setIdTipoTarea(getItemActual().getOcUnidadCosto().getId());
                                    }
                                    if (getItemActual().getOcSubTarea() != null) {
                                        setIdCentroCosto(getItemActual().getOcSubTarea().getOcCodigoSubtarea().getId());
                                    }
                                    setLstActividad(ocActividadPetroleraImpl.getActividadesItems());
                                    setLstPresupuesto(ocPresupuestoImpl.getPresupuestoItems(getRequisicionActual().getApCampo().getId(), false));
                                    setLstAnioPresupuesto(ocPresupuestoDetalleImpl.getAniosItems(getIdPresupuesto(), false));
                                    setLstMesPresupuesto(ocPresupuestoDetalleImpl.getMesesItems(getIdPresupuesto(), getAnioPresupuesto(), false));
                                    setLstActividad(ocPresupuestoDetalleImpl.getActividadesItems(getIdPresupuesto(), getAnioPresupuesto(), getMesPresupuesto(), false));
                                    setListaProyectosOT(ocTareaImpl.traerProyectoOtPorGerenciaItems(getRequisicionActual().getGerencia().getId(), getRequisicionActual().getApCampo().getId(), 0, 0, 0, 0, 0));
                                    if (idProyectoOt > 0 && idActPetrolera > 0) {
                                        setListaUnidadCosto(ocTareaImpl.traerUnidadCostoPorGerenciaProyectoOTItems(getRequisicionActual().getGerencia().getId(), idProyectoOt, idActPetrolera, null, getRequisicionActual().getApCampo().getId()));
                                    }
                                    if (idProyectoOt > 0 && idTipoTarea > 0) {
                                        setListaTO(ocTareaImpl.traerNombrePorProyectoOtGerenciaUnidadCostoItems(getRequisicionActual().getGerencia().getId(), idProyectoOt, idTipoTarea, null, getRequisicionActual().getApCampo().getId(), getRequisicionActual().getApCampo().getTipo()));
                                    }
//                                    if (idProyectoOt > 0 && idActPetrolera > 0 && idTipoTarea > 0 && idTarea > 0) {
//                                        setLstCentroCosto(ocSubTareaImpl.traerLstCentoCostosItems(idTarea, null));                                    
//                                    }
                                    actualizaTarea();
                                } else {
                                    setListaTO(tipoObraBean.listaTarea(
                                            getRequisicionActual().getProyectoOt().getId(),
                                            requisicionActual.getGerencia().getId(),
                                            requisicionActual.getOcUnidadCosto() != null
                                            ? requisicionActual.getOcUnidadCosto().getId() : 0));
                                }

                                if (getItemActual().getInvArticulo() != null
                                        && getItemActual().getInvArticulo().getSiCategoria() != null
                                        && getItemActual().getInvArticulo().getSiCategoria().getId() > 0) {
                                    iniciarCatSel();
                                    setCategoriaVo(new CategoriaVo());
                                    getCategoriaVo().setId(getItemActual().getInvArticulo().getSiCategoria().getId());
                                    llenarCategoria(null);
                                }
                                PrimeFaces.current().executeScript(
                                        ";abrirDialogoModal(dialogoItemsRequi);"
                                );
                            } else {
                                FacesUtilsBean.addInfoMessage("No se puede modificar un Ítem que no esta autorizado...");
                            }
                        } else {
                            PrimeFaces.current().executeScript(
                                    ";alertaGeneral('Es necesario modificar la requisición para agregar el tipo de tarea.');"
                            );
                        }
                        break;

                    case AI:
                        PrimeFaces.current().executeScript(
                                ";alertaGeneral('Es necesario modificar la requisición para cambiar el tipo.');"
                        );
                        break;

                    default:
                        setItemActual(requisicionServicioRemoto.buscarItemPorId(idPartida));
                        if (getItemActual().isAutorizado()) {
                            operacionItem = UPDATE_OPERATION;
                            setMultiProyecto(false);
                            if (getItemActual().getOcTarea() != null) {
                                setIdNombreTarea(getItemActual().getOcTarea().getId());
                                setIdTarea(getItemActual().getOcTarea().getId());
                                if ("C".equals(getRequisicionActual().getApCampo().getTipo()) && getItemActual().getOcTarea().getOcActividadPetrolera() != null) {
                                    setIdActPetrolera(getItemActual().getOcTarea().getOcActividadPetrolera().getId());
                                    setIdTarea(getItemActual().getOcTarea().getOcCodigoTarea().getId());
                                }
                            }

                            if ("C".equals(getRequisicionActual().getApCampo().getTipo())) {

                                setIdActPetrolera(getItemActual().getOcActividadpetrolera().getId());
                                setIdPresupuesto(getItemActual().getOcPresupuesto().getId());
                                setAnioPresupuesto(getItemActual().getAnioPresupuesto());
                                setMesPresupuesto(getItemActual().getMesPresupuesto());

                                if (getItemActual().getProyectoOt() != null) {
                                    setIdProyectoOT(getItemActual().getProyectoOt().getId());
                                }

                                if (getItemActual().getOcUnidadCosto() != null) {
                                    setIdTipoTarea(getItemActual().getOcUnidadCosto().getId());
                                }
                                if (getItemActual().getOcSubTarea() != null) {
                                    setIdCentroCosto(getItemActual().getOcSubTarea().getOcCodigoSubtarea().getId());
                                }
                                setLstPresupuesto(ocPresupuestoImpl.getPresupuestoItems(getRequisicionActual().getApCampo().getId(), false));
                                setLstAnioPresupuesto(ocPresupuestoDetalleImpl.getAniosItems(getIdPresupuesto(), false));
                                setLstMesPresupuesto(ocPresupuestoDetalleImpl.getMesesItems(getIdPresupuesto(), getAnioPresupuesto(), false));
                                setLstActividad(ocPresupuestoDetalleImpl.getActividadesItems(getIdPresupuesto(), getAnioPresupuesto(), getMesPresupuesto(), false));
                                setListaProyectosOT(ocTareaImpl.traerProyectoOtPorGerenciaItems(getRequisicionActual().getGerencia().getId(), getRequisicionActual().getApCampo().getId(), 0, 0, 0, 0, 0));

                                if (idProyectoOt > 0 && idActPetrolera > 0) {
                                    setListaUnidadCosto(ocTareaImpl.traerUnidadCostoPorGerenciaProyectoOTItems(getRequisicionActual().getGerencia().getId(), idProyectoOt, idActPetrolera, null, getRequisicionActual().getApCampo().getId()));
                                }
                                if (idProyectoOt > 0 && idTipoTarea > 0) {
                                    setListaTO(ocTareaImpl.traerNombrePorProyectoOtGerenciaUnidadCostoItems(getRequisicionActual().getGerencia().getId(), idProyectoOt, idTipoTarea, null, getRequisicionActual().getApCampo().getId(), getRequisicionActual().getApCampo().getTipo()));
                                }
                                actualizaTarea();
                            }
                            PrimeFaces.current().executeScript(
                                    ";abrirDialogoModal(dialogoItemsRequi);"
                            );
                        } else {
                            FacesUtilsBean.addInfoMessage("No se puede modificar un Ítem que no esta autorizado...");
                        }
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public List<GereciaTareaVo> getListaAyuda() {
        try {
            if ((requisicionActual != null && TipoRequisicion.PS.equals(requisicionActual.getTipo()))
                    && itemActual != null) {
                int idG = 0;
                if (requisicionActual.getGerencia().getAbrev().contains(";")) {
                    String cad[] = requisicionActual.getGerencia().getAbrev().split(";");
                    GerenciaVo gvo = gerenciaImpl.traerGerenciaVOAbreviatura(cad[0]);
                    idG = gvo.getId();
                } else {
                    idG = requisicionActual.getGerencia().getId();
                }
                return ocGerenciaTareaImpl.traerTareaTrabajo(idG, requisicionActual.getApCampo().getId());
            }
        } catch (Exception e) {
            LOGGER.fatal(this, "Excepion al recuperar la ayuda de las requisiciones  : : : " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Este metodo sirve para actualzar el Ítem desde el panel emergente
     *
     * @param
     */
    public void completarActualizacionItem() {
        try {
            // se toma la linea de requisición
            setItemActual(popupBean.getItem());
            if (operacionItem.equals(UPDATE_OPERATION)) {
                requisicionServicioRemoto.actualizarItem(getItemActual());
                FacesUtilsBean.addInfoMessage("El Ítem se actualizó correctamente...");
            } else {
                getItemActual().setRequisicion(requisicionActual);
                //---Esto es para que no mande error y actualice montos
                requisicionServicioRemoto.crearItem(getItemActual(), getIdNombreTarea(), usuarioBean.getUsuarioConectado().getId());
                FacesUtilsBean.addInfoMessage("El Ítem se creó correctamente...");
            }
            //actualizar lista

            itemsProcesoAprobar();
            //Esto es para cerrar el panel emergente de modificar Ítem
            //popupBean.toggleModalModificar();
            PrimeFaces.current().executeScript(";cerrarDialogoModal(AutorizaCantidadItemsRequi);");
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public List<SelectItem> getListaUnidad() {
        return listaUnidad;
    }

    /**
     * Este metodo sirve para actualzar el Ítem desde el panel emergente esto
     * esta asi por lo de los montos
     *
     * @param
     */
    public void completarActualizacionItemCrearRequisicion() {
        try {
            // se toma la linea de requisición            
            if (requisicionActual != null && operacionItem.equals(UPDATE_OPERATION)) {
                if (requisicionActual.getApCampo() != null && "N".equals(requisicionActual.getApCampo().getTipo())) {
                    if (getItemActual().getProyectoOt() == null || itemActual.getProyectoOt().getId() <= 0) {
                        getItemActual().setProyectoOt(requisicionActual.getProyectoOt());
                    }
                    requisicionServicioRemoto.actualizarItemCrearRequisicion(getItemActual(), getIdTarea());
                    FacesUtilsBean.addInfoMessage("El Ítem se actualizó correctamente...");
                } else if (requisicionActual.getApCampo() != null && "C".equals(requisicionActual.getApCampo().getTipo())) {
                    guardarReqDetalleContractual(requisicionActual.getTipo().name());
                    FacesUtilsBean.addInfoMessage("El Ítem se actualizó correctamente...");
                }
            } else if (requisicionActual != null && operacionItem.equals(CREATE_OPERATION)) {
                if (requisicionActual.getApCampo() != null && "N".equals(requisicionActual.getApCampo().getTipo())) {
                    getItemActual().setRequisicion(requisicionActual);
                    //---Esto es para que no mande error y actualice montos
                    getItemActual().setProyectoOt(getRequisicionActual().getProyectoOt());
                    if (TipoRequisicion.PS.equals(requisicionActual.getTipo())) {
                        if (getItemActual() != null) {
                            getItemActual().setOcUnidadCosto(getRequisicionActual().getOcUnidadCosto());
                        }
                    }
                    requisicionServicioRemoto.crearItem(getItemActual(), getIdTarea(), usuarioBean.getUsuarioConectado().getId());
                    FacesUtilsBean.addInfoMessage("El Ítem se creó correctamente...");
                } else if (requisicionActual.getApCampo() != null && "C".equals(requisicionActual.getApCampo().getTipo())) {
                    if (multiProyecto) {
                        guardarReqDetalleContractualMulti(requisicionActual.getTipo().name());
                        if (proyOTMultiPrimero != null && proyOTMultiPrimero.getId() > 0) {
                            copiarNewItems(getItemActual());
                        }
                    } else {
                        guardarReqDetalleContractual(requisicionActual.getTipo().name());
                    }
                }
            }
            //Esto es para cerrar el panel emergente de modificar Ítem

            resetActividadPetrolera();
            setMultiProyecto(false);
            setMostrarMultiProyecto(false);
            setRequisicionActual(requisicionServicioRemoto.find(getRequisicionActual().getId()));
            if (getRequisicionActual().isMultiproyecto()) {
                itemsProcesoAprobarMulti();
            } else {
                itemsProcesoAprobar();
            }
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoItemsRequi);");

        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    private void copiarNewItems(RequisicionDetalle itemDet) {
        try {
            for (ProyectoOtVo vo : lstProyOTMultiResto) {
                itemDet.setProyectoOt(new ProyectoOt(vo.getId()));
                OcSubtareaVO subTareaVO = ocTareaImpl.traerIDTarea(
                        vo.getId(),
                        itemDet.getOcUnidadCosto().getId(),
                        itemDet.getOcCodigoTarea().getId(),
                        itemDet.getOcActividadpetrolera().getId(),
                        itemDet.getOcCodigoSubtarea().getId());
                if (subTareaVO != null) {
                    itemDet.setOcTarea(new OcTarea(subTareaVO.getIdTarea()));
                    itemDet.setOcSubTarea(new OcSubTarea(subTareaVO.getId()));
                }
                itemDet.setId(0);
                requisicionServicioRemoto.crearItem(itemDet, 0, usuarioBean.getUsuarioConectado().getId());
            }
            lstProyOTMultiResto = new ArrayList<>();
            proyOTMultiPrimero = null;
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    private double getCantidadMultiProyecto(double cantidadOriginal) {
        int cantidadProyectos = 0;
        try {
            if (isMostrarMultiProyecto() && multiProyecto) {
                lstProyOTMultiResto = new ArrayList<>();
                for (ProyectoOtVo vo : listaProyectosOTMulti) {
                    if (vo.isSelected()) {
                        lstProyOTMultiResto.add(vo);
                        cantidadProyectos++;
                    }
                }
                if (lstProyOTMultiResto.size() > 0) {
                    proyOTMultiPrimero = lstProyOTMultiResto.get(0);
                    lstProyOTMultiResto.remove(0);
                }
            } else {
                cantidadProyectos = 1;
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
            cantidadProyectos = 1;
        }
        BigDecimal canO = BigDecimal.valueOf(cantidadOriginal);
        BigDecimal canP = BigDecimal.valueOf(cantidadProyectos);
        BigDecimal resultado = canO.divide(canP, 9, BigDecimal.ROUND_HALF_UP);
        return resultado.doubleValue();
    }

    private void guardarReqDetalleContractual(String tipo) {
        if ( //                TipoRequisicion.PS.name().equals(tipo) && 
                requisicionActual != null && itemActual != null
                && getIdPresupuesto() > 0 && getMesPresupuesto() > 0 && getAnioPresupuesto() > 0
                && getIdActPetrolera() > 0 && getIdProyectoOT() > 0 && getIdTipoTarea() > 0 && getIdTarea() > 0) {
//            OcTarea tarea = ocTareaImpl.find(idTarea);
//            ProyectoOt proy = proyectoOtImpl.find(idProyectoOt);
//            OcUnidadCosto tipotarea = ocUnidadCostoImpl.find(idTipoTarea);
            getItemActual().setOcPresupuesto(new OcPresupuesto(getIdPresupuesto()));
            getItemActual().setMesPresupuesto(getMesPresupuesto());
            getItemActual().setAnioPresupuesto(getAnioPresupuesto());
            getItemActual().setOcActividadpetrolera(new OcActividadPetrolera(getIdActPetrolera()));
            getItemActual().setProyectoOt(new ProyectoOt(getIdProyectoOT()));
            getItemActual().setOcUnidadCosto(new OcUnidadCosto(getIdTipoTarea()));
            getItemActual().setOcCodigoTarea(new OcCodigoTarea(getIdTarea()));
            OcSubtareaVO ocstarea = ocTareaImpl.traerIDTarea(
                    getIdProyectoOT(),
                    getIdTipoTarea(),
                    getIdTarea(),
                    getIdActPetrolera(),
                    getIdCentroCosto());
            if (ocstarea != null) {
                if (ocstarea.getIdTarea() > 0) {
                    getItemActual().setOcTarea(new OcTarea(ocstarea.getIdTarea()));
                }

                if (ocstarea.getId() > 0) {
                    getItemActual().setOcSubTarea(new OcSubTarea(ocstarea.getId()));
                }
            }

            getItemActual().setCantidadSolicitada(getCantidadMultiProyecto(getItemActual().getCantidadSolicitada()));
            if (getIdCentroCosto() > 0) {
                getItemActual().setOcCodigoSubtarea(new OcCodigoSubtarea(getIdCentroCosto()));
            }
            boolean guardar = false;
            if (requisicionActual.getProyectoOt() == null) {
                requisicionActual.setProyectoOt(new ProyectoOt(getIdProyectoOT()));
                guardar = true;
            }
            if (requisicionActual.getOcUnidadCosto() == null) {
                requisicionActual.setOcUnidadCosto(new OcUnidadCosto(getIdTipoTarea()));
                guardar = true;
            }
            if (guardar) {
                requisicionServicioRemoto.edit(requisicionActual);
                setRequisicionActual(requisicionServicioRemoto.find(getRequisicionActual().getId()));
            }
            getItemActual().setRequisicion(requisicionActual);
            if (getItemActual() != null && getItemActual().getId() != null && getItemActual().getId() > 0) {
                requisicionServicioRemoto.actualizarItemCrearRequisicion(getItemActual(), 0);
            } else {
                requisicionServicioRemoto.crearItem(getItemActual(), 0, usuarioBean.getUsuarioConectado().getId());
            }
        }
//        else if (TipoRequisicion.AF.name().equals(tipo) && requisicionActual != null && itemActual != null
//                && idActPetrolera > 0 && idProyectoOt > 0) {
//            getItemActual().setOcPresupuesto(new OcPresupuesto(getIdPresupuesto()));
//            getItemActual().setMesPresupuesto(getMesPresupuesto());
//            getItemActual().setAnioPresupuesto(getAnioPresupuesto());
//            getItemActual().setOcActividadpetrolera(new OcActividadPetrolera(getIdActPetrolera()));
//            getItemActual().setProyectoOt(new ProyectoOt(getIdProyectoOT()));
//
//            boolean guardar = false;
//            if (requisicionActual.getProyectoOt() == null) {
//                requisicionActual.setProyectoOt(new ProyectoOt(getIdProyectoOT()));
//                guardar = true;
//            }
//
//            if (guardar) {
//                requisicionServicioRemoto.edit(requisicionActual);
//                setRequisicionActual(requisicionServicioRemoto.find(getRequisicionActual().getId()));
//            }
//            getItemActual().setRequisicion(requisicionActual);
//            if (getItemActual() != null && getItemActual().getId() != null && getItemActual().getId() > 0) {
//                requisicionServicioRemoto.actualizarItemCrearRequisicion(getItemActual(), 0);
//            } else {
//                getItemActual().setGenero(new Usuario(usuarioBean.getUsuarioConectado().getId()));
//                getItemActual().setFechaGenero(new Date());
//                getItemActual().setHoraGenero(new Date());
//                requisicionServicioRemoto.crearItem(getItemActual(), 0, usuarioBean.getUsuarioConectado().getId());
//            }
//        }
    }

    private void guardarReqDetalleContractualMulti(String tipo) {
        if (TipoRequisicion.PS.name().equals(tipo) && requisicionActual != null && itemActual != null
                && getIdPresupuesto() > 0 && getMesPresupuesto() > 0 && getAnioPresupuesto() > 0
                && getIdActPetrolera() > 0 && getIdTipoTarea() > 0 && getIdTarea() > 0) {
            getItemActual().setCantidadSolicitada(getCantidadMultiProyecto(getItemActual().getCantidadSolicitada()));
            getItemActual().setOcPresupuesto(new OcPresupuesto(getIdPresupuesto()));
            getItemActual().setMesPresupuesto(getMesPresupuesto());
            getItemActual().setAnioPresupuesto(getAnioPresupuesto());
            getItemActual().setOcActividadpetrolera(new OcActividadPetrolera(getIdActPetrolera()));
            getItemActual().setOcUnidadCosto(new OcUnidadCosto(getIdTipoTarea()));
            getItemActual().setOcCodigoTarea(new OcCodigoTarea(getIdTarea()));
            if (proyOTMultiPrimero != null && proyOTMultiPrimero.getId() > 0) {
                OcSubtareaVO ocstarea = ocTareaImpl.traerIDTarea(
                        proyOTMultiPrimero.getId(),
                        getIdTipoTarea(),
                        getIdTarea(),
                        getIdActPetrolera(),
                        getIdCentroCosto());

//                List<SelectItem> itemS = ocSubTareaImpl.traerLstCentoCostosItemsCampoActividad(idTarea, getRequisicionActual().getApCampo().getId(), idActPetrolera, getLabelSubTarea(), proyOTMultiPrimero.getId());
//                OcSubTarea centCosto = null;
//                if (itemS != null && itemS.size() > 0) {
                if (ocstarea != null) {
//                    setIdCentroCosto((Integer) itemS.get(0).getValue());
//                    setIdCentroCosto(ocstarea.getId());
                    if (ocstarea.getId() > 0) {
//                        centCosto = ocSubTareaImpl.find(idCentroCosto);
                        getItemActual().setOcSubTarea(new OcSubTarea(ocstarea.getId()));

                        setIdTarea(ocstarea.getIdTarea());
                        getItemActual().setOcTarea(new OcTarea(getIdTarea()));

                        setIdProyectoOT(ocstarea.getIdProyectoOT());
                        getItemActual().setProyectoOt(new ProyectoOt(ocstarea.getIdProyectoOT()));

                        getItemActual().setOcCodigoSubtarea(new OcCodigoSubtarea(getIdCentroCosto()));

//                        setIdTipoTarea(centCosto.getOcTarea().getOcUnidadCosto().getId());
//                        getItemActual().setOcUnidadCosto(centCosto.getOcTarea().getOcUnidadCosto());
                        if (requisicionActual.getProyectoOt() == null) {
                            requisicionActual.setProyectoOt(getItemActual().getProyectoOt());

                        }
                        if (requisicionActual.getOcUnidadCosto() == null) {
                            requisicionActual.setOcUnidadCosto(getItemActual().getOcUnidadCosto());

                        }
                        getRequisicionActual().setMultiproyecto(Constantes.BOOLEAN_TRUE);
                        requisicionServicioRemoto.edit(requisicionActual);
                        setRequisicionActual(requisicionServicioRemoto.find(getRequisicionActual().getId()));

                        getItemActual().setRequisicion(requisicionActual);

                        getItemActual().setMultiproyectoId(folioImpl.traerFolio("AGRUPADOR_MULTIPROYECTO"));

                        if (getItemActual() != null && getItemActual().getId() != null && getItemActual().getId() > 0) {
                            requisicionServicioRemoto.actualizarItemCrearRequisicion(getItemActual(), 0);
                        } else {
                            requisicionServicioRemoto.crearItem(getItemActual(), 0, usuarioBean.getUsuarioConectado().getId());
                        }
                    }
                    listaProyectosOTMulti = new ArrayList<>();
                }
            }
        } else if (TipoRequisicion.AF.name().equals(tipo) && requisicionActual != null && itemActual != null
                && idActPetrolera > 0) {
            ProyectoOt proy = proyectoOtImpl.find(idProyectoOt);
            getItemActual().setProyectoOt(proy);

            boolean guardar = false;
            if (requisicionActual.getProyectoOt() == null) {
                requisicionActual.setProyectoOt(proy);
                guardar = true;
            }

            if (guardar) {
                requisicionServicioRemoto.edit(requisicionActual);
                setRequisicionActual(requisicionServicioRemoto.find(getRequisicionActual().getId()));
            }
            getItemActual().setRequisicion(requisicionActual);
            if (getItemActual() != null && getItemActual().getId() != null && getItemActual().getId() > 0) {
                requisicionServicioRemoto.actualizarItemCrearRequisicion(getItemActual(), 0);
            } else {
                requisicionServicioRemoto.crearItem(getItemActual(), 0, usuarioBean.getUsuarioConectado().getId());
            }
        }
    }

    public void enviarSolicitudAltaArticulo() {
        try {
            String para = "";
            List<RolVO> roles = siRolImpl.traerRol(Constantes.MODULO_ADMIN_SIA, 0, "REGISTRAR ARTICULOS");
            if (roles != null && roles.size() > 0) {
                List<Integer> rolesIDs = new ArrayList<>();
                rolesIDs.add(roles.get(0).getId());
                para = siUsuarioRolImpl.traerCorreosByRolList(rolesIDs, requisicionActual.getApCampo().getId());
            }
            notificacionRequisicionImpl.envioSoliciudAltaArticulo(
                    para, "", "", " Solicitud de registro de un nuevo artículo ",
                    requisicionActual, getNewArticuloText(), getNewArticuloTextUso(), getCategoriasSeleccionadas(), getUnidadMedida());
            setNewArticuloText("");
            setNewArticuloTextUso("");
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoItemsRequiNewArt);");
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void cancelarItem(int idPartida) {
        try {
            if (idPartida > 0) {
                setItemActual(requisicionServicioRemoto.buscarItemPorId(idPartida));
                getItemActual().setAutorizado(Constantes.BOOLEAN_FALSE);
                getItemActual().setModifico(usuarioBean.getUsuarioConectado());
                getItemActual().setFechaModifico(new Date());
                getItemActual().setHoraModifico(new Date());
                requisicionServicioRemoto.actualizarItem(getItemActual());

                itemsProcesoAprobar();
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void autorizarItem(int idPartida) {
        try {
            if (idPartida > 0) {
                setItemActual(requisicionServicioRemoto.buscarItemPorId(idPartida));
                getItemActual().setAutorizado(Constantes.BOOLEAN_TRUE);
                getItemActual().setModifico(usuarioBean.getUsuarioConectado());
                getItemActual().setFechaModifico(new Date());
                getItemActual().setHoraModifico(new Date());
                requisicionServicioRemoto.actualizarItem(getItemActual());
                // para actualizar la lista
                itemsProcesoAprobar();
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void eliminarItem(int idPartida, int idAgrupador) {
        try {
            // se toma el Id del Ítem
            if (getRequisicionActual().isMultiproyecto()) {
                if (idPartida < 1 && idAgrupador > 0) {
                    requisicionServicioRemoto.eliminarItems(getRequisicionActual(), idAgrupador);
                    if (listaItems == null || listaItems.getRowCount() == 1) {
                        requisicionActual.setMultiproyecto(Constantes.BOOLEAN_FALSE);
                        requisicionServicioRemoto.edit(requisicionActual);
                    }
                    itemsProcesoAprobarMulti();

                }
            } else {
                if (idPartida > 0) {
                    setItemActual(requisicionServicioRemoto.buscarItemPorId(idPartida));
                    requisicionServicioRemoto.eliminarItem(getItemActual());
                    getItemsActualizar();

                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void crearNuevoItem() {
        try {
            //----------------------------------------            
            setMostrarMultiProyecto(getRequisicionActual().isMultiproyecto()
                    || ((listaItems == null || listaItems.getRowCount() < 1)
                    && TipoRequisicion.PS.equals(getRequisicionActual().getTipo())
                    && "C".equals(getRequisicionActual().getApCampo().getTipo())));

            switch (getRequisicionActual().getTipo()) {
                case PS:
//		if (getRequisicionActual().getOcUnidadCosto() != null) {
                    setItemActual(new RequisicionDetalle());
                    itemActual.setInvArticulo(new InvArticulo());
                    operacionItem = CREATE_OPERATION;
                    setMultiProyecto(false);
                    if ("N".equals(getRequisicionActual().getApCampo().getTipo())) {
                        setListaTO(tipoObraBean.listaTarea(
                                getRequisicionActual().getProyectoOt().getId(),
                                requisicionActual.getGerencia().getId(),
                                requisicionActual.getOcUnidadCosto() == null ? 0 : requisicionActual.getOcUnidadCosto().getId()));
                    } else if ("C".equals(getRequisicionActual().getApCampo().getTipo())) {
//                        setLstActividad(ocActividadPetroleraImpl.getActividadesItems());
//                        resetActividadPetrolera();
                        resetPresupuesto();
                        setLstPresupuesto(ocPresupuestoImpl.getPresupuestoItems(getRequisicionActual().getApCampo().getId(), false));
                    }

                    setIdNombreTarea(0);
                    setCategoriaVoInicial(new CategoriaVo());
                    getCategoriaVoInicial().setListaCategoria(invArticuloCampoImpl.traerCategoriaArticulo());
                    categorias = invArticuloCampoImpl.traerCategoriaArticulo();
                    setCategoriaVo(getCategoriaVoInicial());
                    setArticulosFrecuentes(
                            articuloImpl.articulosFrecuentes(
                                    usuarioBean.getUsuarioConectado().getId(),
                                    getRequisicionActual().getApCampo().getId()
                            )
                    );
                    if (getArticulosFrecuentes() != null && getArticulosFrecuentes().size() > 10) {
                        setArticulosFrecuentes(getArticulosFrecuentes().subList(0, 10));
                    }
                    iniciarCatSel();
                    setLstArticulos(new ArrayList<>());
                    setArticuloTx("");
                    setArticulosResultadoBqda(
                            soporteArticulos.obtenerArticulos(
                                    getArticuloTx(),
                                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                                    0,
                                    null
                            )
                    );
                    PrimeFaces.current().executeScript("$(dialogoItemsRequi).modal('show');");
                    break;
                case AI:
                    PrimeFaces.current().executeScript(";alertaGeneral('Es necesario modificar la requisición para cambiar el tipo.');");
                    break;
                default:
                    setItemActual(new RequisicionDetalle());
                    getItemActual().setInvArticulo(new InvArticulo());
                    operacionItem = CREATE_OPERATION;
                    setMultiProyecto(false);
                    setCategoriaVoInicial(new CategoriaVo());
                    getCategoriaVoInicial().setListaCategoria(invArticuloCampoImpl.traerCategoriaArticulo());
                    setCategoriaVo(getCategoriaVoInicial());
                    setArticulosFrecuentes(
                            articuloImpl.articulosFrecuentes(
                                    usuarioBean.getUsuarioConectado().getId(),
                                    getRequisicionActual().getApCampo().getId()
                            )
                    );
                    iniciarCatSel();
                    setArticuloTx(Constantes.VACIO);
                    setLstArticulos(new ArrayList<>());
                    setArticulosResultadoBqda(
                            soporteArticulos.obtenerArticulos(
                                    getArticuloTx(),
                                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                                    0,
                                    null
                            )
                    );
                    lstActividad = ocActividadPetroleraImpl.getActividadesItems();
                    resetPresupuesto();
                    setLstPresupuesto(ocPresupuestoImpl.getPresupuestoItems(getRequisicionActual().getApCampo().getId(), false));
                    PrimeFaces.current().executeScript("$(dialogoItemsRequi).modal('show');");
                    break;
            }
            setListaUnidad(ocUnidadImpl.traerUnidadItems());
        } catch (Exception ex) {
            LOGGER.fatal(this, null, ex);
        }
    }

    public void enEsperaDet() {
        try {
            setEsperaVO(requisicionSiMovimientoImpl.requisicionMovDet(getRequisicionActual().getId()));
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void rechazosRequisicion() {
        try {
            listaRechazo = requisicionServicioRemoto.getRechazosPorRequisicion(requisicionActual.getId());
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public boolean isUsuarioConPermiso() {
        return siUsuarioRolImpl.buscarRolPorUsuarioModulo(
                usuarioBean.getUsuarioConectado().getId(),
                Constantes.MODULO_REQUISICION,
                Constantes.CODIGO_ROL_CONS_OCS,
                usuarioBean.getUsuarioConectado().getApCampo().getId()
        );
    }

    /**
     * @return the operacionRequisicion
     */
    public String getOperacionRequisicion() {
        return operacionRequisicion;
    }

    /**
     * @return the fechaRequerida
     */
    public Date getFechaRequerida() {
        return (Date) fechaRequerida.clone();
    }

    /**
     * @param fechaRequerida the fechaRequerida to set
     */
    public void setFechaRequerida(Date fechaRequerida) {
        if (fechaActual.getTime().after(fechaRequerida)) {
            fechaRequerida = fechaActual.getTime();
        } else {
            fechaRequerida = (Date) fechaRequerida.clone();
        }
    }

    /**
     * @return the operacionItem
     */
    public String getOperacionItem() {
        return operacionItem;
    }

    /**
     * @return the crearItem
     */
    public boolean isCrearItem() {
        return crearItem;
    }

    /**
     * @return the requisicionesSolicitadas
     */
    public DataModel getRequisicionesSolicitadas() {
//////        if (requisicionesSolicitadas == null && usuarioBean.getUsuarioConectado() != null) {
//////            listaRequisicionesSolicitadas();
//////        }
        return requisicionesSolicitadas;
    }

    /**
     * @return the requisicionesRevisadas
     */
    public DataModel getRequisicionesRevisadas() {
        if (requisicionesRevisadas == null && usuarioBean.getUsuarioConectado() != null) {
            listaRequisicionesRevisadas();
        }
        return requisicionesRevisadas;
    }

    /**
     * @return the requisicionesAprobadas
     */
    public DataModel getRequisicionesAprobadas() {
        if (requisicionesAprobadas == null && usuarioBean.getUsuarioConectado() != null) {
            listaRequisicionesAprobadas();
        }
        return requisicionesAprobadas;
    }

    /**
     * @return the requisicionesAsignadas
     */
    public DataModel getRequisicionesAsignadas() {
        if (requisicionesAsignadas == null && usuarioBean.getUsuarioConectado() != null) {
            listaRequisicionesAsignadas();
        }
        return requisicionesAsignadas;
    }

    /**
     * @return the verVistoBueno
     */
    public boolean isVerVistoBueno() {
        return verVistoBueno;
    }

    /**
     * @return the verAutoriza
     */
    public boolean isVerAutoriza() {
        return verAutoriza;
    }

    /**
     * @return the totalRequisiciones
     */
    public long getTotalRequisiciones() {
        return totalRequisiciones;
    }

    public long getTotalRequisicionesSinSolicitar() {
        long retVal = 0;
        try {
            retVal
                    = requisicionServicioRemoto.getTotalRequisicionesSinSolicitar(
                            usuarioBean.getUsuarioConectado().getId(),
                            usuarioBean.getUsuarioConectado().getApCampo().getId()
                    );
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
        return retVal;

    }

    public long getTotalRequisicionesSinRevisar() {
        long retVal = 0;
        try {
            retVal = requisicionServicioRemoto.getTotalRequisicionesSinRevisar(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()
            );
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
        return retVal;

    }

    public long getTotalRequisicionesSinAprobar() {
        long retVal = 0;
        try {
            retVal = requisicionServicioRemoto.getTotalRequisicionesSinAprobar(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()
            );
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
        return retVal;

    }

    public long getTotalRequisicionesSinVistoBueno() {
        long retVal = 0;
        try {
            retVal = requisicionServicioRemoto.getTotalRequisicionesSinVistoBueno(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    TipoRequisicion.PS.name(),
                    Constantes.ROL_VISTO_BUENO_COSTO
            );
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
        return retVal;
    }

    public long getTotalRequisicionesSinVistoBuenoContabilidad() {
        long retVal = 0;
        try {
            retVal = requisicionServicioRemoto.getTotalRequisicionesSinVistoBueno(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    TipoRequisicion.AF.name(),
                    Constantes.ROL_VISTO_BUENO_CONTABILIDAD
            );
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
        return retVal;
    }

    public long getTotalRequisicionesSinAsignar() {
        long retVal = 0;
        try {
            retVal = requisicionServicioRemoto.getTotalRequisicionesSinAsignar(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    Constantes.REQUISICION_VISTO_BUENO,
                    Constantes.ROL_ASIGNA_REQUISICION
            );
        } catch (Exception ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
        return retVal;

    }

    public void limpiarRequisicion() {
        cambiarRequisicion(0);
    }

    public String cargarEts() {
        String retVal = null;
        try {
            irInicio = true;
            retVal
                    = menuBarBean.cargarEts(
                            FacesUtilsBean.getRequestParameter("paginaOrigen"),
                            FacesUtilsBean.getRequestParameter("paginaDestino")
                    );
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
            retVal = Constantes.VACIO;
        }
        return retVal;
    }

    public void devolverVariasRequisicion() {
        try {
            if (requisicionActual == null) {
                boolean entro = false;
                for (Object object : listaRequisiciones) {
                    RequisicionVO o = (RequisicionVO) object;
                    if (o.isSelected()) {
                        setRequisicionActual(requisicionServicioRemoto.find(o.getId()));
                        rechazarRequisicion();//cancelarRequisicion
                        //                        popupBean.setRechazo(new Rechazo());
                        //                        popupBean.getRechazo().setMotivo(rechazoActual.getMotivo());
                        if (!entro) {
                            entro = true;
                        }
                    }
                }
                String jsMetodo = ";limpiarTodos();";

                popupBean.getRechazo().setMotivo(Constantes.BLANCO);

                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                //revisarRequisicion(requisicionActual);                                
                rechazarRequisicion();

                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.REVenviado"));
                cambiarRequisicion(0);
                String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
                PrimeFaces.current().executeScript(jsMetodo);

            }
            //
            contarPendiente();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }

    }

    public void cancelarVariasRequisicion() {
        try {
            if (requisicionActual == null) {
                boolean entro = false;
                String motivo = popupBean.getRequisicion().getMotivoCancelo();
                for (Object object : listaRequisiciones) {
                    RequisicionVO o = (RequisicionVO) object;
                    if (o.isSelected()) {
                        popupBean.setRequisicion(requisicionServicioRemoto.find(o.getId()));
                        popupBean.getRequisicion().setMotivoCancelo(motivo);
                        cancelarRequisicion();
                        if (!entro) {
                            entro = true;
                        }
                    }
                }
                if (entro) {

                    //FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("se rechazaron las requisiciones correctamente"));
                    PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCancelarVariasReq);");
                    cambiarRequisicion(0);
                }
                String jsMetodo = ";limpiarTodos();";
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                //revisarRequisicion(requisicionActual);
                cancelarRequisicion();

                FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.REVenviado"));
                cambiarRequisicion(0);
                String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
                PrimeFaces.current().executeScript(jsMetodo);

            }
            //
            contarPendiente();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    private void contarPendiente() {

        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        contarBean.llenarReqSinRevisar();
        contarBean.llenarReqSinAprobar();
        contarBean.llenarReqSinAsignar();
        contarBean.llenarReqSinVistoBueno();
        contarBean.llenarReqSinVoBoConta();
    }

    private boolean isAdminEnEspera() {
        boolean ret = false;
        try {
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            if (contarBean.getMapaTotal().get("reqenEsperaAdm") != null) {
                ret = contarBean.getMapaTotal().get("reqenEsperaAdm") > 0;
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
        return ret;
    }

    public void traerArticulosListener(String event) {
        setLstArticulos(traerArticulos(event));
    }

    public void traerArticulosItemsListener(String event) {
        String cadena = event;
        if ((cadena != null && !cadena.isEmpty() && cadena.length() > 2)
                || (cadena == null || cadena.isEmpty())) {
            setArticulosResultadoBqda(traerArticulosItems(cadena));
        }
        PrimeFaces.current().executeScript(";marcarBusqueda();");
    }

    public void traerCategoriasListener(String event) {
        setLstCategorias(traerCategorias(event));
    }

    public void traerGEArticulosListener(String event) {
        setLstGEArticulos(traerGEArticulos(event));
    }

    private String filtrosCadena(String cadena) {
        String[] output = cadena.split("\\%");
        StringBuilder cadenaNombre = new StringBuilder("and ((");
//        StringBuilder cadenaCodigo = new StringBuilder(") or (");
        String and = "";
        for (String s : output) {
            cadenaNombre.append(and).append("upper(a.NOMBRE||a.CODIGO_INT) like upper('%").append(s).append("%') ");
//            cadenaCodigo.append(and).append("upper(a.CODIGO_INT) like upper('%").append(s).append("%') ");                        
            and = " and ";
        }
//        return cadenaNombre.toString()+cadenaCodigo.toString()+"))";
        return cadenaNombre.toString() + "))";
    }

    private List<SelectItem> traerArticulosItems(String cadena) {
        List<SelectItem> list;
        try {
            cadena = filtrosCadena(cadena.replace(" ", "%"));
            list = soporteArticulos.obtenerArticulosItems(cadena, usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    Constantes.CERO,
                    getCodigos(getCategoriasSeleccionadas().size() > 1
                            ? getCategoriasSeleccionadas().subList(1, getCategoriasSeleccionadas().size())
                            : new ArrayList<CategoriaVo>()));
        } catch (Exception e) {
            list = new ArrayList<>();
        }
        return list;
    }

    private List<SelectItem> traerArticulos(String cadena) {
        List<SelectItem> list;
        try {
            if (cadena != null && !cadena.isEmpty() && cadena.length() > 2) {
                list = soporteArticulos.obtenerArticulos(cadena, usuarioBean.getUsuarioConectado().getApCampo().getId(),
                        Constantes.CERO,
                        getCodigos(getCategoriasSeleccionadas().size() > 2
                                ? getCategoriasSeleccionadas().subList(2, getCategoriasSeleccionadas().size())
                                : new ArrayList<CategoriaVo>()));
            } else {
                list = new ArrayList<>();
            }
        } catch (Exception e) {
            LOGGER.error(this, "", e);
            list = new ArrayList<>();
        }
        return list;
    }

    private List<SelectItem> traerCategorias(String cadena) {
        List<SelectItem> list;
        try {
            if (cadena != null && !cadena.isEmpty() && cadena.length() > 2) {
                list = soporteArticulos.obtenerCategorias(cadena, usuarioBean.getUsuarioConectado().getApCampo().getId());
            } else {
                list = new ArrayList<>();
            }
        } catch (Exception e) {
            LOGGER.error(this, "", e);
            list = new ArrayList<>();
        }
        return list;
    }

    private List<SelectItem> traerGEArticulos(String cadena) {
        List<SelectItem> list;
        try {
            if (cadena != null && !cadena.isEmpty() && cadena.length() > 2) {
                list = soporteArticulos.obtenerArticulos(cadena, usuarioBean.getUsuarioConectado().getApCampo().getId(),
                        Constantes.CERO,
                        null);
            } else {
                list = new ArrayList<>();
            }
        } catch (Exception e) {
            LOGGER.error(this, "", e);
            list = new ArrayList<>();
        }
        return list;
    }

    private String getCodigos(List<CategoriaVo> categorias) {
        String codigosTxt = "";
        for (CategoriaVo cat : categorias) {
            codigosTxt = codigosTxt + " and upper(a.CODIGO) like upper('%" + cat.getCodigo() + "%') ";
        }
        return codigosTxt;
    }

    private String getSoloCodigos(List<CategoriaVo> categorias) {
        String codigosTxt = "";
        for (CategoriaVo cat : categorias) {
            if (codigosTxt.isEmpty() && cat.getId() > 0) {
                codigosTxt = cat.getId() + "";
            } else if (cat.getId() > 0) {
                codigosTxt = codigosTxt + ',' + cat.getId();
            }
        }
        return codigosTxt;
    }

    public void cambiarCategoria() {
        try {
            if (getCategoriaGETx() != null && !getCategoriaGETx().isEmpty()) {
                setArticulosResultadoBqdaCat(soporteArticulos.obtenerArticulos("", usuarioBean.getUsuarioConectado().getApCampo().getId(),
                        0,
                        getCategoriaGETx()));
                setCategoriaGETx("");
                setLstCategorias(new ArrayList<>());
                PrimeFaces.current().executeScript(";minimizarPanel('artFrecImg', 'collapsePanelArtFre');minimizarPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');");
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx)");
        }
    }

    public void cambiarArticulo() {
        try {
            if (getArticuloTx() != null && !getArticuloTx().isEmpty()) {
                int aux = 2;
                String codigoInt = getArticuloTx().substring(
                        (getArticuloTx().lastIndexOf("=>") + aux));
                List<ArticuloVO> articulos = soporteArticulos.getArticulosActivo(codigoInt, usuarioBean.getUsuarioConectado().getApCampo().getId(), 0, "");
                if (articulos != null && articulos.size() > 0) {
                    setArticuloID(articulos.get(0).getId());
                    getItemActual().setInvArticulo(articuloImpl.find(articulos.get(0).getId()));
                    setArticuloTx("");
                    setLstArticulos(new ArrayList<>());
                    setCategoriaVo(getCategoriaVoInicial());
                    categoriasSeleccionadas = new ArrayList<>();
                    iniciarCatSel();
//                    PrimeFaces.current().executeScript(
//                            ";minimizarPanel('artFrecImg', 'collapsePanelArtFre');minimizarPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');"
//                    );
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx)");
        }
    }

    public void cambiarArticuloGE() {
        try {
            if (getArticuloGETx() != null && !getArticuloGETx().isEmpty()) {
                int aux = 2;
                String codigoInt = getArticuloGETx().substring((getArticuloGETx().lastIndexOf("=>") + aux));

                List<ArticuloVO> articulos
                        = soporteArticulos.getArticulosActivo(
                                codigoInt,
                                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                                0,
                                Constantes.VACIO
                        );
                if (articulos != null && articulos.size() > 0) {
                    setArticuloID(articulos.get(0).getId());
                    getItemActual().setInvArticulo(articuloImpl.find(articulos.get(0).getId()));
                    setArticuloGETx("");
                    setLstGEArticulos(new ArrayList<>());
                    setArticuloTx("");
                    setLstArticulos(new ArrayList<>());
                    setCategoriaVo(getCategoriaVoInicial());
                    categoriasSeleccionadas = new ArrayList<>();
                    iniciarCatSel();
//                    PrimeFaces.current().executeScript(
//                            ";minimizarPanel('artFrecImg', 'collapsePanelArtFre');minimizarPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');"
//                    );
                }
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx)");
        }
    }

    public void seleccionarCategoriaCabecera(int id) {
        traerSubcategoria(id);
    }

    public void seleccionarArtFrecuente(SelectEvent<ArticuloVO> event) {
        try {
            ArticuloVO artVo = event.getObject();
            if (artVo != null && artVo.getId() > 0) {
                setArticuloTx(new StringBuilder().append(artVo.getNombre())
                        .append("=>").append(artVo.getCodigoInt())
                        .toString().toLowerCase());
                cambiarArticulo();
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx)");
        }
    }

    public void seleccionarResultadoBA(SelectEvent<SelectItem> event) {
        try {
            SelectItem artItem = (SelectItem) event.getObject();
            if (artItem != null && artItem.getValue() != null && ((ArticuloVO) artItem.getValue()).getId() > 0) {
                setArticuloTx(new StringBuilder().append(((ArticuloVO) artItem.getValue()).getNombre())
                        .append("=>").append(((ArticuloVO) artItem.getValue()).getNumParte())
                        .toString().toLowerCase());
                cambiarArticulo();

//                resetActividadPetrolera();
            }
        } catch (Exception e) {
            LOGGER.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx)");
        }
    }

    private void traerSubcategoria(int indice) {
        CategoriaVo c = categoriasSeleccionadas.get(indice);
        if (indice == 0) {
            categorias = categoriaVo.getListaCategoria();
            setCategoriaVo(getCategoriaVoInicial());
            categoriasSeleccionadas = new ArrayList<>();
            iniciarCatSel();
        } else {
            setCategoriaVo(siRelCategoriaImpl.traerCategoriaPorCategoria(c.getId(), getSoloCodigos(getCategoriasSeleccionadas().subList(0, indice)), usuarioBean.getUsuarioConectado().getApCampo().getId()));
            if (c.getId() != getCategoriaVo().getId()) {
                categoriasSeleccionadas.add(getCategoriaVo());// limpiar lista seleccionadas
            }
            if ((indice + 1) < categoriasSeleccionadas.size()) {
                for (int i = (categoriasSeleccionadas.size() - 1); (indice + 1) < categoriasSeleccionadas.size(); i--) {
                    categoriasSeleccionadas.remove(i);
                }
            }
        }
        if (requisicionActual != null && operacionItem.equals(CREATE_OPERATION)) {
            setItemActual(new RequisicionDetalle());
        }
        getItemActual().setInvArticulo(new InvArticulo());
        setArticuloTx("");
        setArticulosResultadoBqda(
                soporteArticulos.obtenerArticulosItems(
                        getArticuloTx(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId(),
                        0,
                        getCodigos(
                                getCategoriasSeleccionadas().size() > 1
                                ? getCategoriasSeleccionadas().subList(1, getCategoriasSeleccionadas().size())
                                : new ArrayList<>()
                        )
                )
        );
        setLstArticulos(new ArrayList<>());
//        PrimeFaces.current().executeScript(
//                ";minimizarPanel('artFrecImg', 'collapsePanelArtFre');expandirPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');"
//        );
    }

    private void iniciarCatSel() {
        setCategoriasSeleccionadas(new ArrayList<>());
        CategoriaVo c = new CategoriaVo();
        c.setNombre("PRINCIPALES");
        c.setId(Constantes.CERO);
        categoriasSeleccionadas.add(c);
    }

    public void seleccionarCategoria(SelectEvent<CategoriaVo> event) {
        try {
            CategoriaVo con = (CategoriaVo) event.getObject();
            //setCategoriaVo(con);
            //
            categoriaVo  = siRelCategoriaImpl.traerCategoriaPorCategoria(con.getId(), getSoloCodigos(categoriasSeleccionadas), usuarioBean.getUsuarioConectado().getApCampo().getId());
            categorias = categoriaVo.getListaCategoria();
            //
            //llenarCategoria(getSoloCodigos(getCategoriasSeleccionadas()));
            categoriasSeleccionadas.add(categoriaVo);
            if (getCategoriasSeleccionadas() != null && getCategoriasSeleccionadas().size() > 1) {
                setArticuloTx("");
                setArticulosResultadoBqda(
                        soporteArticulos.obtenerArticulosItems(
                                getArticuloTx(),
                                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                                0,
                                getCodigos(
                                        getCategoriasSeleccionadas().size() > 1
                                        ? getCategoriasSeleccionadas().subList(1, getCategoriasSeleccionadas().size())
                                        : new ArrayList<>()
                                )
                        )
                );
            }
        } catch (Exception e) {
            System.out.println("Excp: " + e);
        }
//        PrimeFaces.current().executeScript(
//                ";minimizarPanel('artFrecImg', 'collapsePanelArtFre');expandirPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');"
//        );

    }

    private void llenarCategoria(String catSeleccionadas) {
        setCategoriaVo(siRelCategoriaImpl.traerCategoriaPorCategoria(getCategoriaVo().getId(), catSeleccionadas, usuarioBean.getUsuarioConectado().getApCampo().getId()));
        categoriasSeleccionadas.add(getCategoriaVo());
    }

    public void actualizaActividadPetrolera() {
//        idActPetrolera= (Integer) event.getNewValue();
        setListaProyectosOT(new ArrayList<>());
        if (getRequisicionActual() != null && getRequisicionActual().getGerencia() != null
                && getRequisicionActual().getGerencia().getId() > 0) {
            if (getRequisicionActual().getTipo().name().equals(TipoRequisicion.PS.name())) {
                if (listaItems == null || listaItems.getRowCount() < 1 || !isMostrarMultiProyecto()) {
//                    setListaProyectosOT(ocTareaImpl.traerProyectoOtPorGerenciaItems(getRequisicionActual().getGerencia().getId(), getRequisicionActual().getApCampo().getId(), 0, 0, 0, null));
                    setListaProyectosOT(ocPresupuestoDetalleImpl.getProyectoOtVOs(getIdPresupuesto(), getIdActPetrolera(),
                            getRequisicionActual().getApCampo().getId(),
                            getAnioPresupuesto(),
                            getMesPresupuesto(),
                            false));
                }
                if (isMostrarMultiProyecto()) {
                    ProyectoOtVo vo = new ProyectoOtVo();
                    vo.setId(-2);
                    vo.setNombre("Multiproyecto");
                    getListaProyectosOT().add(0, vo);
                }
            } else {
                setListaProyectosOT(ocGerenciaProyectoImpl.traerProyectoOt(getRequisicionActual().getGerencia().getId(), getRequisicionActual().getApCampo().getId()));
            }
        } else {
            setListaProyectosOT(new ArrayList<>());
        }
        setIdProyectoOT(-1);
        setIdTipoTarea(0);
        setIdTarea(0);
        setIdCentroCosto(-1);
        setListaUnidadCosto(new ArrayList<>());
        setListaTO(new ArrayList<>());
        setLstCentroCosto(new ArrayList<>());
    }

    public void actualizaPresupuesto() {
//        idActPetrolera= (Integer) event.getNewValue();
//        setLstMesPresupuesto(ocPresupuestoDetalleImpl.getMesesItems(getIdPresupuesto(), false));        
        setLstAnioPresupuesto(ocPresupuestoDetalleImpl.getAniosItems(getIdPresupuesto(), false));
        setLstMesPresupuesto(new ArrayList<>());
        setAnioPresupuesto(0);
        setMesPresupuesto(0);
        setIdActPetrolera(0);
        setIdProyectoOT(-1);
        setIdTipoTarea(0);
        setIdTarea(0);
        setIdCentroCosto(-1);
        setListaUnidadCosto(new ArrayList<>());
        setListaTO(new ArrayList<>());
        setLstCentroCosto(new ArrayList<>());
        setLstActividad(new ArrayList<>());
    }

    public void actualizaAnio() {
        setLstMesPresupuesto(ocPresupuestoDetalleImpl.getMesesItems(getIdPresupuesto(), getAnioPresupuesto(), false));
        setMesPresupuesto(0);
        setIdActPetrolera(0);
        setIdProyectoOT(-1);
        setIdTipoTarea(0);
        setIdTarea(0);
        setIdCentroCosto(-1);
        setListaUnidadCosto(new ArrayList<>());
        setListaTO(new ArrayList<>());
        setLstCentroCosto(new ArrayList<>());
    }

    public void actualizaMes() {
        setLstActividad(ocPresupuestoDetalleImpl.getActividadesItems(getIdPresupuesto(), getAnioPresupuesto(), getMesPresupuesto(), false));
        setIdActPetrolera(0);
        setIdProyectoOT(-1);
        setIdTipoTarea(0);
        setIdTarea(0);
        setIdCentroCosto(-1);
        setListaUnidadCosto(new ArrayList<>());
        setListaTO(new ArrayList<>());
        setLstCentroCosto(new ArrayList<>());
    }

    private void resetActividadPetrolera() {
        setIdProyectoOT(0);
        setIdTipoTarea(0);
        setIdTarea(0);
        setIdCentroCosto(-1);
        setIdActPetrolera(0);
        setListaProyectosOT(new ArrayList<>());
        setListaUnidadCosto(new ArrayList<>());
        setListaTO(new ArrayList<>());
        setLstCentroCosto(new ArrayList<>());
    }

    private void resetPresupuesto() {
        setIdProyectoOT(0);
        setIdTipoTarea(0);
        setIdTarea(0);
        setIdCentroCosto(-1);
        setIdActPetrolera(0);
        setIdPresupuesto(0);
        setMesPresupuesto(0);
        setAnioPresupuesto(0);
        setListaProyectosOT(new ArrayList<>());
        setListaUnidadCosto(new ArrayList<>());
        setListaTO(new ArrayList<>());
        setLstCentroCosto(new ArrayList<>());
        setLstPresupuesto(new ArrayList<>());
        setLstMesPresupuesto(new ArrayList<>());
    }

    public void actualizaProyectoOT() {
//        idProyectoOt = (Integer) event.getNewValue();
        if (idProyectoOt > 0 && idActPetrolera > 0 && idPresupuesto > 0) {
            setMultiProyecto(false);
            setListaUnidadCosto(ocPresupuestoDetalleImpl.getUnidadCostosItems(
                    getIdPresupuesto(),
                    getIdActPetrolera(),
                    getRequisicionActual().getApCampo().getId(),
                    getIdProyectoOT(),
                    getAnioPresupuesto(),
                    getMesPresupuesto(),
                    false));
        } else if (idProyectoOt == -2) {
            setMultiProyecto(true);
            setListaUnidadCosto(ocPresupuestoDetalleImpl.getUnidadCostosItems(
                    getIdPresupuesto(),
                    getIdActPetrolera(),
                    getRequisicionActual().getApCampo().getId(),
                    0,
                    getAnioPresupuesto(),
                    getMesPresupuesto(),
                    false));
        } else {
            setListaUnidadCosto(new ArrayList<>());
        }
        setIdTipoTarea(0);
        setIdTarea(0);
        setIdCentroCosto(-1);
        setListaTO(new ArrayList<>());
        setLstCentroCosto(new ArrayList<>());
    }

    public void actualizaTipoTarea() {
//        idTipoTarea = (Integer) event.getNewValue();
        if (idProyectoOt > 0 && idActPetrolera > 0 && idTipoTarea > 0) {
            setListaTO(ocPresupuestoDetalleImpl.getTareasItems(
                    getIdPresupuesto(),
                    getIdActPetrolera(),
                    getRequisicionActual().getApCampo().getId(),
                    getIdProyectoOT(),
                    getIdTipoTarea(),
                    getAnioPresupuesto(),
                    getMesPresupuesto(),
                    false));
        } else if (isMultiProyecto()) {
            setListaTO(ocPresupuestoDetalleImpl.getTareasItems(
                    getIdPresupuesto(),
                    getIdActPetrolera(),
                    getRequisicionActual().getApCampo().getId(),
                    0,
                    getIdTipoTarea(),
                    getAnioPresupuesto(),
                    getMesPresupuesto(),
                    false));
        } else {
            setListaTO(new ArrayList<>());
        }
        setIdTarea(0);
        setIdCentroCosto(-1);
        setLstCentroCosto(new ArrayList<>());
    }

    public void actualizaTarea() {
//	idTarea= (Integer) event.getNewValue();
        if (idProyectoOt > 0 && idActPetrolera > 0 && idTipoTarea > 0 && idTarea > 0) {
            setLstCentroCosto(ocPresupuestoDetalleImpl.getSubTareasItems(
                    getIdPresupuesto(),
                    getIdActPetrolera(),
                    getRequisicionActual().getApCampo().getId(),
                    getIdProyectoOT(),
                    getIdTipoTarea(),
                    getIdTarea(),
                    getAnioPresupuesto(),
                    getMesPresupuesto(),
                    false));
        } else if (isMultiProyecto()) {
            setLstCentroCosto(ocPresupuestoDetalleImpl.getSubTareasItems(
                    getIdPresupuesto(),
                    getIdActPetrolera(),
                    getRequisicionActual().getApCampo().getId(),
                    0,
                    getIdTipoTarea(),
                    getIdTarea(),
                    getAnioPresupuesto(),
                    getMesPresupuesto(),
                    false));
        } else {
            setLstCentroCosto(new ArrayList<>());
        }
    }

    public void actualizaSubTarea() {
        if (isMultiProyecto() && getIdCentroCosto() > -1) {
            setListaProyectosOTMulti(ocTareaImpl.traerProyectoOtPorGerenciaItems(
                    getRequisicionActual().getGerencia().getId(),
                    getRequisicionActual().getApCampo().getId(),
                    getIdActPetrolera(),
                    getIdTipoTarea(),
                    getIdTarea(),
                    getIdPresupuesto(),
                    getIdCentroCosto()));
        }
    }

    /**
     * @return the multiProyectosEtiqueta
     */
    public String getMultiProyectosEtiqueta() {
        multiProyectosEtiqueta = "";
        if (listaItems != null && listaItems.getRowCount() > 0) {
            multiProyectosEtiqueta = ((List<RequisicionDetalleVO>) listaItems.getWrappedData()).get(0).getMultiProyectos();
        }
        return multiProyectosEtiqueta;
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

    /**
     * @return the categoriaVoInicial
     */
    public CategoriaVo getCategoriaVoInicial() {
        return categoriaVoInicial;
    }

    /**
     * @param categoriaVoInicial the categoriaVoInicial to set
     */
    public void setCategoriaVoInicial(CategoriaVo categoriaVoInicial) {
        this.categoriaVoInicial = categoriaVoInicial;
    }

}
