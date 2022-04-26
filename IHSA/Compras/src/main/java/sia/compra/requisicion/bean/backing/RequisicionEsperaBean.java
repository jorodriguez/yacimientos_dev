/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.orden.bean.backing.NotaOrdenBean;
import sia.compra.orden.bean.backing.OrdenBean;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.inventarios.service.InventarioImpl;
import sia.modelo.Orden;
import sia.modelo.ReRequisicionEts;
import sia.modelo.Rechazo;
import sia.modelo.Requisicion;
import sia.modelo.RequisicionDetalle;
import sia.modelo.Usuario;
import sia.modelo.comunicacion.vo.NoticiaVO;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.orden.impl.OrdenDetalleImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionDetalleImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.requisicion.impl.RequisicionSiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */

@Named (value = RequisicionEsperaBean.BEAN_NAME)
@ViewScoped
public class RequisicionEsperaBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "requisicionEsperaBean";
    //------------------------------------------------------
    @Inject
    private OrdenImpl ordenServicioRemoto;
    @Inject
    private RequisicionImpl requisicionServicioRemoto;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private NotificacionRequisicionImpl notificacionRequisicionImpl;
    @Inject
    private OrdenDetalleImpl ordenDetalleImpl;
    @Inject
    private RequisicionDetalleImpl requisicionDetalleImpl;
    @Inject
    private EstatusImpl estatusServicioRemoto;
    @Inject
    private ReRequisicionEtsImpl reRequisicionEtsImpl;
    @Inject
    InventarioImpl inventarioImpl;
    @Inject
    private RequisicionSiMovimientoImpl requisicionSiMovimientoImpl;

    /**
     * Este metodo sirve para rechazar una requisición
     */
    //-- Managed Beans ----
    
    @Inject
    private UsuarioBean usuarioBean;
    @Inject
    private PopupBean popupBean ;
    private Requisicion requisicionActual;
    private RequisicionDetalle itemActual;
    //- - - - - - - - - - - - -
    private DataModel listaRequisiciones = null; //almacena la lista de requisiciones
    private DataModel listaItems = null; //almacena la lista de Servicios adicionales
    private boolean mostrarOpcion = false;
    //--------------------------
    private boolean actualizar = false;
    //-------------
    private Orden orden;
    private String motivoDevolucion = "";
    private String motivoFinalizacion = "";
    private Map<Integer, Boolean> filasSeleccionadas = new HashMap<Integer, Boolean>();
    private String consecutivoRequisicion;    
    private List<OrdenVO> listaOrden = null;
    private List<ReRequisicionEts> listaEts = null;
    private List<OrdenVO> historicoVentas = null;    

    //
    private String nombreMultiProyectos;
    private List<InventarioVO> inventario;

    /**
     * Creates a new instance of RecepcionRequisicion
     */
    public RequisicionEsperaBean() {
    }

    @PostConstruct
    public void iniciar() {
        if (usuarioBean.getUsuarioConectado() != null) {
            
            setListaRequisiciones(
                    new ListDataModel(
                            requisicionServicioRemoto.traerRequisicionesSinContrato(
                                    this.usuarioBean.getUsuarioConectado().getId(),
                                    Constantes.REQUISICION_ASIGNADA,
                                    usuarioBean.getUsuarioConectado().getApCampo().getId()
                            )
                    )
            );
        }
    }

    private boolean puedoDevolverRequisicion() {
        boolean devolver = true;
        try {
            List<OrdenVO> l = ordenServicioRemoto.getOrdenesPorRequisicion(requisicionActual.getId(), null);
            if (!l.isEmpty()) {
                for (OrdenVO objects : l) {
                    if (this.ordenServicioRemoto.find(objects.getId()).getAutorizacionesOrden().getEstatus().getId() > 100) {
                        devolver = false;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
        return devolver;
    }

    private boolean itemsDisgregados() {
        boolean disgregado = false;
        for (RequisicionDetalleVO Lista : requisicionServicioRemoto.getItemsPorRequisicion(requisicionActual.getId(), false, false)) {
            if (Lista.isDisgregado()) {
                disgregado = true;
            }
        }
        return disgregado;
    }

    public void devolverRequisicion() {
        //Verificar si tiene ordenes de compra        
        if (this.getRequisicionActual() != null
                && this.getRequisicionActual().getId() > 0) {
            boolean puedoDevolverReq = requisicionDetalleImpl.tieneInvArticulo(this.getRequisicionActual().getId(), true);
            if (puedoDevolverReq && puedoDevolverRequisicion()) {
                // Verificar si no tiene Items disgregados
                if (itemsDisgregados()) {
                    FacesUtilsBean.addErrorMessage("No se puede devolver la requisición, ya que esta cuenta con órdenes de compra en proceso.");
                } else {
                    PrimeFaces.current().executeScript( ";abrirDialogoModal(dialogoDevRecepReq);");
                }
            } else {
                if (!puedoDevolverReq) {
                    FacesUtilsBean.addErrorMessage("No se puede devolver la requisición, ya que esta no utiliza el catálogo de articulos.");
                } else {
                    FacesUtilsBean.addErrorMessage("No se puede devolver la requisición, ya que esta cuenta con órdenes de compra en proceso.");
                }
            }
        }
    }

    public void terminarDevolverRequisicion() {
        try {
            // se toma la requisición y motivo de rechazo del panel emergente rechazar requisición
            Rechazo rechazoActual = new Rechazo();
            //Asigno requisicion fecha hora y usuario de rechazo
            rechazoActual.setRequisicion(this.requisicionActual);
            rechazoActual.setFecha(new Date());
            rechazoActual.setHora(new Date());
            rechazoActual.setCumplido(Constantes.BOOLEAN_FALSE);
            rechazoActual.setMotivo(motivoDevolucion);
            rechazoActual.setRechazo(this.usuarioBean.getUsuarioConectado());

            if (notificacionRequisicionImpl.envioRechazoRequisicion(
                    destinatarios(this.requisicionActual),
                    "",
                    "",
                    new StringBuilder().append("REQUISICIÓN: ").append(this.requisicionActual.getConsecutivo()).append(" DEVUELTA").toString(),
                    this.requisicionActual,
                    rechazoActual)) {
                //-- Marcar la requisicion como rechazada y regresarla al solicitante
                this.requisicionActual.setRechazada(Constantes.BOOLEAN_TRUE);
                this.requisicionActual.setEstatus(estatusServicioRemoto.find(1)); // 0 = Pendiente
                //-- Quitar las fechas y hora de operaciones realizadas
                this.requisicionActual.setHoraAsigno(null);
                this.requisicionActual.setFechaAsigno(null);
                this.requisicionActual.setVistoBueno(null);
                this.requisicionActual.setAsigna(null);
                
                this.requisicionActual.setFechaReviso(null);
                this.requisicionActual.setHoraReviso(null);
                this.requisicionActual.setFechaAprobo(null);
                this.requisicionActual.setHoraAprobo(null);
                this.requisicionActual.setFechaAutorizo(null);
                this.requisicionActual.setHoraAutorizo(null);
                this.requisicionActual.setFechaVistoBueno(null);
                this.requisicionActual.setHoraVistoBueno(null);
                //actualiza la requisición
                requisicionServicioRemoto.edit(this.requisicionActual);
                //actualizar rechazo
                requisicionServicioRemoto.crearRechazo(rechazoActual);
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage("La requisición se devolvió correctamente...");
                //Esto es para Quitar las lineas seleccionadas
                this.cambiarRequisicion(0);
                actualizar = true;
                //Esto es para cerrar el panel emergente de cancelar requisicion
                PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoDevRecepReq);");
                //popupDevolverRequisicionBean.toggleModal();
                //Regresa a la tabla
                this.iniciar();
                PrimeFaces.current().executeScript( ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
            }//------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage("No se pudo devolver la requisición, por favor notifique el problema a: sia@ihsa.mx");
            }
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }

    public void finalizarRequisicion() {
        RequisicionVO requisicionVO = ((RequisicionVO) this.getListaRequisiciones().getRowData());
        this.setRequisicionActual(requisicionServicioRemoto.find(requisicionVO.getId()));
        //  popupFinalizarRequisicionBean.toggleModal();
    }

    /**
     * Este metodo sirve para que el analista finalice una requisición
     *
     * @param 
     */
    public void terminarFinalizarRequisicion() {
        try {
            //Asigno fecha en que se finaliza la requisiciòn
            this.requisicionActual.setFechaFinalizo(new Date());
            this.requisicionActual.setHoraFinalizo(new Date());
            // ----- Quien finaliza
            this.requisicionActual.setFinalizo(this.usuarioBean.getUsuarioConectado());
            this.requisicionActual.setMotivoFinalizo(motivoFinalizacion);
            //------------------------------------
            if (notificacionRequisicionImpl.envioFinalizarRequisicion(
                    destinatarios(requisicionActual), "", "",
                    new StringBuilder().append("REQUISICIÓN: ").append(requisicionActual.getConsecutivo()).append(" FINALIZADA").toString(),
                    requisicionActual,
                    "finalizar")) {
                //Si mando el correo se actualiza la requisición
                this.requisicionActual.setEstatus(estatusServicioRemoto.find(Constantes.REQUISICION_FINALIZADA)); // 60 = Finalizada

                for (RequisicionDetalleVO rd : requisicionDetalleImpl.getItemsAnalistaNativa(requisicionActual.getId(), false)) {
                    requisicionDetalleImpl.cambiarDisgregadoItemRequisicion(rd, usuarioBean.getUsuarioConectado().getId(), Constantes.BOOLEAN_TRUE);
                }
                this.requisicionServicioRemoto.edit(requisicionActual);
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage("La requisición fue finalizada correctamente...");
                //Esto es para Quitar las lineas seleccionadas
                this.cambiarRequisicion(0);
                //Esto es para cerrar el panel emergente de finalizar requisicion
                PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoFinRecepReq);");
                //popupFinalizarRequisicionBean.toggleModal();
                //Regresa a la tabla
                PrimeFaces.current().executeScript( ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
            } //------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addErrorMessage("No se pudo finalizar la requisición, por favor notifique el problema a: sia@ihsa.mx");
            }
            actualizar = true;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }
    
    /**
     * Este metodo sirve para que el analista ponga en espera una requisición
     *
     * @param 
     */
    public void esperaRequisicion() {
        try {
            //Asigno fecha en que se finaliza la requisiciòn
            this.requisicionActual.setFechaFinalizo(new Date());
            this.requisicionActual.setHoraFinalizo(new Date());
            // ----- Quien finaliza
            this.requisicionActual.setFinalizo(this.usuarioBean.getUsuarioConectado());
            this.requisicionActual.setMotivoFinalizo(motivoFinalizacion);
            //------------------------------------
            if (notificacionRequisicionImpl.envioEsperaRequisicion(
                    destinatarios(requisicionActual), "", "",
                    new StringBuilder().append("REQUISICIÓN: ").append(requisicionActual.getConsecutivo()).append(" EN ESPERA").toString(),
                    requisicionActual,
                    "espera")) {
                //Si mando el correo se actualiza la requisición
                this.requisicionActual.setEstatus(estatusServicioRemoto.find(Constantes.REQUISICION_EN_ESPERA)); // 45 = EN ESPERA
                this.requisicionActual.setFinalizo(null);
                this.requisicionActual.setMotivoFinalizo("");

                
                this.requisicionServicioRemoto.edit(requisicionActual);
                requisicionSiMovimientoImpl.saveRequestMove(this.usuarioBean.getUsuarioConectado().getId(), motivoFinalizacion, requisicionActual.getId(), Constantes.ID_SI_OPERACION_ESPERA);
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage("La requisición fue puesta en espera correctamente...");
                //Esto es para Quitar las lineas seleccionadas
                this.cambiarRequisicion(0);
                //Esto es para cerrar el panel emergente de finalizar requisicion
                PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoEsperaReq);");
                //popupFinalizarRequisicionBean.toggleModal();
                //Regresa a la tabla
                PrimeFaces.current().executeScript( ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
            } //------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addErrorMessage("No se pudo poner en espera la requisición, por favor notifique el problema a: sia@ihsa.mx");
            }
            actualizar = true;
            motivoFinalizacion = "";
            this.iniciar();
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }

    private String destinatarios(Requisicion requisicion) {
        StringBuilder destinatariosSB = new StringBuilder();
        if (requisicion.getEstatus().getId() == 10) {
            destinatariosSB.append(requisicion.getSolicita().getEmail());
        }
        if (requisicion.getEstatus().getId() == 15) {
            destinatariosSB.append(requisicion.getSolicita().getEmail());
            destinatariosSB.append(",").append(requisicion.getRevisa().getEmail());
        }
        if (requisicion.getEstatus().getId() == 20) {
            destinatariosSB.append(requisicion.getSolicita().getEmail());
            destinatariosSB.append(",").append(requisicion.getRevisa().getEmail());
            destinatariosSB.append(",").append(requisicion.getAprueba().getEmail());
        }
        if (requisicion.getEstatus().getId() == 40) {
            destinatariosSB.append(requisicion.getSolicita().getEmail());
            destinatariosSB.append(",").append(requisicion.getRevisa().getEmail());
            destinatariosSB.append(",").append(requisicion.getAprueba().getEmail());
            destinatariosSB.append(",").append(requisicion.getAsigna().getEmail());
        }
                
        return destinatariosSB.toString();
    }

    public DataModel getItemsNativo() {
        try {
            List<RequisicionDetalleVO> lo = null;
            if (requisicionActual != null && requisicionActual.getId() != null && requisicionActual.getId() > 0) {
                if (requisicionActual.isMultiproyecto()) {
                    lo = this.requisicionServicioRemoto.getItemsAnalistaNativaMulti(this.requisicionActual.getId(), false);
                } else {
                    lo = this.requisicionServicioRemoto.getItemsAnalistaNativa(this.requisicionActual.getId(), false);
                }
                this.listaItems = new ListDataModel(lo);
            }
        } catch (RuntimeException ex) {
            this.listaItems = null;
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
        return this.listaItems;
    }
    
    public void historyItem() {
        try {
            if (getRequisicionActual() != null) {
                int idInv = Integer.parseInt(FacesUtilsBean.getRequestParameter("idInv"));
                int idCampo = Integer.parseInt(FacesUtilsBean.getRequestParameter("idCampo"));         
                if(idInv > 0 && idCampo > 0){
                    this.setHistoricoVentas(this.ordenDetalleImpl.historicoDetalleOrden(idInv, idCampo));                
                } else {
                    this.setHistoricoVentas(new ArrayList<OrdenVO>());
                }
            } 
            PrimeFaces.current().executeScript( "$(dialogoHistorialCompraReq).modal('show');");
        } catch (NumberFormatException e) {
            UtilLog4j.log.fatal(this, e);
        }
    } 

    public void seleccionarRequisicion() {
        RequisicionVO requisicionVO = ((RequisicionVO) this.getListaRequisiciones().getRowData());
        setRequisicionActual(requisicionServicioRemoto.find(requisicionVO.getId()));
        setRequisicionActual(requisicionActual);
        if (this.getRequisicionActual().isMultiproyecto()) {
            listaItems = new ListDataModel(this.requisicionServicioRemoto.getItemsAnalistaNativaMulti(this.requisicionActual.getId(), false));
        } else {
            listaItems = new ListDataModel(this.requisicionServicioRemoto.getItemsAnalistaNativa(this.requisicionActual.getId(), false));
        }
        OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
        ordenBean.setOrdenActual(null);
        ordenesPorRequisicion();
        //
        etsPorRequisicion();
        mostrarOpcion = listaItems != null && listaItems.getRowCount() > 1;
    }

    public void etsPorRequisicion() {
        try {
            setListaEts(reRequisicionEtsImpl.traerAdjuntosPorRequisicion(getRequisicionActual().getId()));
        } catch (RuntimeException ex) {
            UtilLog4j.log.error(ex);
            setListaEts(null);
        }
    }

    public void seleccionarRequisicionConvenio() {
        int reqId = Integer.parseInt(FacesUtilsBean.getRequestParameter("idReq"));
        setRequisicionActual(requisicionServicioRemoto.find(reqId));
        if (this.getRequisicionActual().isMultiproyecto()) {
            listaItems = new ListDataModel(this.requisicionServicioRemoto.getItemsAnalistaNativaMulti(this.requisicionActual.getId(), false));
        } else {
            listaItems = new ListDataModel(this.requisicionServicioRemoto.getItemsAnalistaNativa(this.requisicionActual.getId(), false));
        }
        //
        ordenesPorRequisicion();
        //
        mostrarOpcion = listaItems != null && listaItems.getRowCount() > 1;
    }

    public void ordenesPorRequisicion() {
        try {
            listaOrden =  ordenServicioRemoto.getOrdenesPorRequisicion(requisicionActual.getId(), null);

        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public void seleccionarOrden() {
        int idOrden = Integer.parseInt(FacesUtilsBean.getRequestParameter("idOrdenReq"));
        OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
        ordenBean.setOrdenActual(ordenServicioRemoto.find(idOrden));
        itemsPorOrden(idOrden);
        notasPorOrden(idOrden);
        CargaEtsBean cargaEtsBean = (CargaEtsBean) FacesUtilsBean.getManagedBean("cargaEtsBean");
        //argaEtsBean.etsPorOrdenCategoria();
        cargaEtsBean.traerTablaComparativa();
        cargaEtsBean.etsPorOrdenRequisicion();
        //
        cargaEtsBean.ordenEtsPorCategoria();
        cargaEtsBean.traerEspecificacionTecnica();
    }

    public void itemsPorOrden(int idOrden) {
        try {
            OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
            ordenBean.setListaItems(ordenServicioRemoto.itemsPorOrdenCompraMulti(idOrden));
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
    }

    public List<NoticiaVO> notasPorOrden(int idOrden) {
        try {
            NotaOrdenBean notaOrdenBean = (NotaOrdenBean) FacesUtilsBean.getManagedBean("notaOrdenBean");
            return notaOrdenBean.traerNoticiaPorOrden(idOrden);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "notasPorOrden  . . ." + e.getMessage(), e);
        }
        return null;
    }

    public void generarOCS() {
        for (Object object : getListaRequisiciones()) {
            RequisicionVO req = (RequisicionVO) object;
            if (req.isSelected()) {
                List<RequisicionDetalleVO> l = requisicionServicioRemoto.getItemsAnalistaNativa(req.getId(), false);
                if (l != null && l.size() > 0) {
                    OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
                    try {
                        requisicionActual = requisicionServicioRemoto.find(req.getId());
                        orden = crearOrden(requisicionActual.isContrato());
                        ordenDetalleImpl.guardarListaItems(l, orden.getId(), usuarioBean.getUsuarioConectado().getId(), Constantes.OCS_SIN_CONTRATO);
                        //--- - quitar la requisición de la lista
                        requisicionServicioRemoto.finalizaRequisicion(usuarioBean.getUsuarioConectado().getId(), req.getId());
                        //Falta disgregar items
                        for (RequisicionDetalleVO requisicionDetalleVO : l) {
                            requisicionDetalleImpl.cambiarDisgregadoItemRequisicion(requisicionDetalleVO, usuarioBean.getUsuarioConectado().getId(), Constantes.BOOLEAN_TRUE);
                        }

                        //cambiarRequisicion(0);
                        setRequisicionActual(null);
                        setConsecutivoRequisicion("");
                        FacesUtilsBean.addInfoMessage("Se crearon las OC/S");
                        //Limpiar listas
                        ordenBean.setOrdenActual(null);
                    } catch (Exception e) {
                        ordenBean.setOrdenActual(null);
                        setRequisicionActual(null);
                    }
                } else {
                    setRequisicionActual(null);
                    FacesUtilsBean.addErrorMessage("No se generó la OC/S -" + req.getConsecutivo() + "-. Debido a que no tiene items autorizados.");
                }
            }
        }
        listaRequisiciones = new ListDataModel(requisicionServicioRemoto.traerRequisicionesSinContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId()));

        actualizar = true;
        String jsMetodo = ";limpiarTodos();";
        //
        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        contarBean.llenarOcsSinSolicitar();
        contarBean.llenarRecReq();
        //
        PrimeFaces.current().executeScript( jsMetodo);
    }

    public void generarOCSConvenio() {        
        listaRequisiciones = new ListDataModel(requisicionServicioRemoto.traerRequisicionesSinContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId()));
        
        actualizar = true;
        String jsMetodo = ";limpiarTodos();";
        //
        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        contarBean.llenarOcsSinSolicitar();
        contarBean.llenarRecReq();
        //
        PrimeFaces.current().executeScript( jsMetodo);

    }

    public void generarOrdenCompra() {
        //- - - - - crear la orden - - - -
        List<RequisicionDetalleVO> l = requisicionServicioRemoto.getItemsAnalistaNativa(this.requisicionActual.getId(), false);
        if (l != null && l.size() > 0) {
            try {
                orden = crearOrden(requisicionActual.isContrato());
                ordenDetalleImpl.guardarListaItems(l, orden.getId(), usuarioBean.getUsuarioConectado().getId(), Constantes.OCS_SIN_CONTRATO);
                //--- - quitar la requisición de la lista
                requisicionServicioRemoto.finalizaRequisicion(usuarioBean.getUsuarioConectado().getId(), requisicionActual.getId());
                //Falta disgregar items
                
                listaRequisiciones = new ListDataModel(requisicionServicioRemoto.traerRequisicionesSinContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId()));
                
                //
                cambiarRequisicion(0);
                actualizar = true;
                setRequisicionActual(null);
                setConsecutivoRequisicion("");
                FacesUtilsBean.addInfoMessage("La orden de compra se generó correctamente...");
                //Limpiar listas
                OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
                ordenBean.setOrdenActual(null);
                PrimeFaces.current().executeScript( ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e.getMessage(), e);
            }

        } else {
            setRequisicionActual(null);
            PrimeFaces.current().executeScript( ";alertaGeneral('No es posible generar la OC/S sin items.');");
        }

        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        contarBean.llenarOcsSinSolicitar();
        contarBean.llenarRecReq();
        //

    }

    public void buscarRequsicionPorCodigo() {
        if (getConsecutivoRequisicion() != null && !getConsecutivoRequisicion().isEmpty()) {
            //          System.out.println("Vino aqui  --- -- - dentro");
            requisicionActual = requisicionServicioRemoto.buscarPorConsecutivo(getConsecutivoRequisicion());
        }
    }

    public void limpiarSeleccionAnterior() {
        setConsecutivoRequisicion("");
    }

    public void generarOrdenCompraDeSeleccion() {
        boolean ordenGenerada = false;
        try {
            List<RequisicionDetalleVO> listaAuto = new ArrayList<>();
            DataModel<RequisicionDetalleVO> lista = this.listaItems;
            for (RequisicionDetalleVO requisicionDetalleVO : lista) {
                if (requisicionDetalleVO.isSelected()) {
                    listaAuto.add(requisicionDetalleVO);
                    requisicionDetalleImpl.cambiarDisgregadoItemRequisicion(requisicionDetalleVO, usuarioBean.getUsuarioConectado().getId(), Constantes.BOOLEAN_TRUE);
                }
            }
            if (!listaAuto.isEmpty()) {
                if (ordenGenerada == false) {
                    orden = crearOrden(requisicionActual.isContrato());//Constantes.FALSE);
                    ordenGenerada = true;
                }
                boolean v = ordenDetalleImpl.guardarListaItems(listaAuto, orden.getId(), usuarioBean.getUsuarioConectado().getId(), Constantes.OCS_SIN_CONTRATO);
                if (ordenGenerada && v) {
                    if (listaAuto.size() == listaItems.getRowCount()) {
                        requisicionServicioRemoto.finalizaRequisicion(usuarioBean.getUsuarioConectado().getId(), requisicionActual.getId());
                        mostrarOpcion = false;
                        cambiarRequisicion(0);
                        PrimeFaces.current().executeScript( ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
                    }
                    FacesUtilsBean.addInfoMessage("La orden de compra se generó correctamente...");
                    listaItems = new ListDataModel(requisicionServicioRemoto.getItemsAnalistaNativa(requisicionActual.getId(), false));
                }
            } else {
                FacesUtilsBean.addInfoMessage("Seleccione al menos un Ítem .  .  .");
            }
            actualizar = true;

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage(), e);
        }

    }

    public Map<Integer, Boolean> getFilasSeleccionadas() {
        return filasSeleccionadas;
    }

    public void seleccionarItem() {
        this.itemActual = (RequisicionDetalle) this.listaItems.getRowData();
        this.popupBean.setItem(this.itemActual);
        this.popupBean.toggleModalModificar();
    }

    /**
     * Este metodo sirve para actualzar el Ítem desde el panel emergente
     *
     * @param 
     */
    public void actualizarItem() {
        try {
            // se toma la linea de requisición
            this.itemActual = popupBean.getItem();
            this.itemActual.setDisgregado(Constantes.BOOLEAN_FALSE);// Por si lo seleccionan y actualizan
            this.requisicionServicioRemoto.actualizarItem(this.itemActual);
            FacesUtilsBean.addInfoMessage("El Ítem se actualizó correctamente...");
            //Esto es para cerrar el panel emergente de modificar Ítem
            popupBean.toggleModalModificar();
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }

    public long getTotalRequisicionesSinDisgregar() {
        try {
            return requisicionServicioRemoto.getTotalRequisicionesSinDisgregar(this.usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getApCampo().getId());
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
        return 0;
    }

    /**
     * @return the requisicionActual
     */
    public Requisicion getRequisicionActual() {
        return requisicionActual;
    }

    /**
     * @return the mostrarOpcion
     */
    public boolean isMostrarOpcion() {
        return mostrarOpcion;
    }

    private Orden crearOrden(boolean conConvenio) {
        //- - - -
        orden = new Orden();
        //Agraga el campo a la OC/S
        orden.setApCampo(requisicionActual.getApCampo());

        if (usuarioBean.getUsuarioConectado().getApCampo() != null
                && usuarioBean.getUsuarioConectado().getApCampo().getCompania() != null
                && usuarioBean.getUsuarioConectado().getApCampo().getCompania().getMoneda() != null) {
            orden.setMoneda(usuarioBean.getUsuarioConectado().getApCampo().getCompania().getMoneda());
        }

        orden.setRequisicion(requisicionActual);
        orden.setGerencia(requisicionActual.getGerencia());
        orden.setResponsableGerencia(gerenciaImpl.getResponsableByApCampoAndGerencia(usuarioBean.getUsuarioConectado().getApCampo().getId(), requisicionActual.getGerencia().getId(), false));
        orden.setProyectoOt(requisicionActual.getProyectoOt());
        orden.setCompania(requisicionActual.getCompania());
        orden.setContactoCompania(requisicionActual.getCompania().getRecepcionFactura());
        orden.setAnalista(usuarioBean.getUsuarioConectado());
        orden.setFecha(new Date());
        orden.setDestino(requisicionActual.getLugarEntrega());
        orden.setReferencia(requisicionActual.getReferencia());
        orden.setContrato(conConvenio ? "True" : Constantes.OCS_SIN_CONTRATO);
        orden.setCuentaContableProyectoOt(requisicionActual.getProyectoOt().getCuentaContable());
        orden.setGenero(usuarioBean.getUsuarioConectado());
        orden.setFechaGenero(new Date());
        orden.setHoraGenero(new Date());
        orden.setEliminado(Constantes.NO_ELIMINADO);
        orden.setOcUnidadCosto(requisicionActual.getOcUnidadCosto());
        orden.setTipo(requisicionActual.getTipo().name());
        orden.setLeida(Constantes.BOOLEAN_FALSE);
        orden.setFechaEntrega(new Date());
        orden.setMultiproyecto(requisicionActual.isMultiproyecto());
        //- - - - gerente de compras - - -
        //gerencia de compras
        UsuarioResponsableGerenciaVo uvo = gerenciaImpl.traerResponsablePorApCampoYGerencia(orden.getApCampo().getId(), Constantes.GERENCIA_ID_COMPRAS);
        orden.setGerenteCompras(new Usuario(uvo.getIdUsuario()));
        //
        orden.setConConvenio(conConvenio);
        //
        if (requisicionActual.getOcUsoCFDI() != null) {
            orden.setOcUsoCFDI(requisicionActual.getOcUsoCFDI());
        }
        //- - - - - crear la orden - - - -
        return this.ordenServicioRemoto.createReturnOrden(orden);
        //

    }

    private void cambiarRequisicion(Object idRequisicion) {
        this.setRequisicionActual(this.requisicionServicioRemoto.find(idRequisicion));
        this.mostrarOpcion = false;
    }

    public void mostrarInventario() {
        int idArt = Integer.parseInt(FacesUtilsBean.getRequestParameter("idArt"));
        InventarioVO inventarioVO = new InventarioVO();
        inventarioVO.setArticuloId(idArt);
        setInventario(new ArrayList<InventarioVO>());
        //.out.println("campo: " + usuarioBean.getUsuarioConectado().getApCampo().getId());
        setInventario(inventarioImpl.traerInventario(inventarioVO, usuarioBean.getUsuarioConectado().getApCampo().getId()));
        //
        PrimeFaces.current().executeScript("$(dialogoMostrarInventario).modal('show');");
    }

    /**
     * @param requisicionActual the requisicionActual to set
     */
    public void setRequisicionActual(Requisicion requisicionActual) {
        this.requisicionActual = requisicionActual;
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
     * @return the motivoFinalizacion
     */
    public String getMotivoFinalizacion() {
        return motivoFinalizacion;
    }

    /**
     * @param motivoFinalizacion the motivoFinalizacion to set
     */
    public void setMotivoFinalizacion(String motivoFinalizacion) {
        this.motivoFinalizacion = motivoFinalizacion;
    }

    /**
     * @return the actualizar
     */
    public boolean isActualizar() {
        return actualizar;
    }

    /**
     * @param actualizar the actualizar to set
     */
    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }

    /**
     * @return the consecutivoRequisicion
     */
    public String getConsecutivoRequisicion() {
        return consecutivoRequisicion;
    }

    /**
     * @param consecutivoRequisicion the consecutivoRequisicion to set
     */
    public void setConsecutivoRequisicion(String consecutivoRequisicion) {
        this.consecutivoRequisicion = consecutivoRequisicion;
    }

    /**
     * @return the listaRequisiciones
     */
    public DataModel getListaRequisiciones() {
        return listaRequisiciones;
    }

    /**
     * @param listaRequisiciones the listaRequisiciones to set
     */
    public void setListaRequisiciones(DataModel listaRequisiciones) {
        this.listaRequisiciones = listaRequisiciones;
    }

    /*
     */
    public String getNombreMultiProyectos() {
        nombreMultiProyectos = "";
        if (this.listaItems != null && this.listaItems.getRowCount() > 0) {
            nombreMultiProyectos = ((List<RequisicionDetalleVO>) this.listaItems.getWrappedData()).get(0).getMultiProyectos();
        }
        return nombreMultiProyectos;
    }

    /**
     * @param nombreMultiProyectos the nombreMultiProyectos to set
     */
    public void setNombreMultiProyectos(String nombreMultiProyectos) {
        this.nombreMultiProyectos = nombreMultiProyectos;
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
     * @return the listaEts
     */
    public List<ReRequisicionEts> getListaEts() {
        return listaEts;
    }

    /**
     * @param listaEts the listaEts to set
     */
    public void setListaEts(List<ReRequisicionEts> listaEts) {
        this.listaEts = listaEts;
    }

    /**
     * @return the inventario
     */
    public List<InventarioVO> getInventario() {
        return inventario;
    }

    /**
     * @param inventario the inventario to set
     */
    public void setInventario(List<InventarioVO> inventario) {
        this.inventario = inventario;
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
