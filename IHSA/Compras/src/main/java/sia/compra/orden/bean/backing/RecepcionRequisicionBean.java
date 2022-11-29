/*
 * RecepcionRequisicion.java
 * Creado el 7/10/2009, 05:23:20 PM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: new_nick_name@hotmail.com
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.compra.requisicion.bean.backing.CargaEtsBean;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
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
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.convenio.impl.CvConvenioAdjuntoImpl;
import sia.servicios.orden.impl.OrdenDetalleImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.orden.impl.OrdenSiMovimientoImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionDetalleImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.requisicion.impl.RequisicionSiMovimientoImpl;
import sia.util.UtilLog4j;

/**
 *
 */
@Named(value = "recepcionRequisicionBean")
@ViewScoped
public class RecepcionRequisicionBean implements Serializable {

    //------------------------------------------------------
    @Inject
    private OrdenImpl ordenServicioRemoto;
    @Inject
    OrdenSiMovimientoImpl ordenSiMovimientoImpl;
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
    @Inject
    private CvConvenioAdjuntoImpl cvConvenioAdjuntoImpl;

    /**
     * Este metodo sirve para rechazar una requisición
     */
    //-- Managed Beans ----
    @Inject
    UsuarioBean usuarioBean;
    //
    private Requisicion requisicionActual;
    private RequisicionDetalle itemActual;
    //- - - - - - - - - - - - -
    private List<RequisicionVO> listaRequisiciones = null; //almacena la lista de requisiciones
    @Getter
    @Setter
    private List<RequisicionDetalleVO> listaItems = null; //almacena la lista de Servicios adicionales
    private boolean mostrarOpcion = false;
    //--------------------------
    private boolean actualizar = false;
    //-------------
    private Orden orden;
    private String motivoDevolucion = "";
    private String motivoFinalizacion = "";
    private Map<Integer, Boolean> filasSeleccionadas = new HashMap<>();
    private String consecutivoRequisicion;
    private List<RequisicionVO> listaRequisicionContrato = null; //almacena la lista de requisiciones
    private List<OrdenVO> listaOrden = null;
    @Getter
    @Setter
    private List<OrdenDetalleVO> listaItemsOrden;
    private List<ReRequisicionEts> listaEts = null;
    private List<OrdenVO> historicoVentas = null;
    @Getter
    @Setter
    private List<MovimientoVO> ordenesRechazadas;
    //
    @Getter
    @Setter
    private List<ContratoVO> listaArchivoConvenio;
    //
    private String nombreMultiProyectos;
    private List<InventarioVO> inventario;

    @Getter
    @Setter
    private Orden ordenActual;
    @Getter
    @Setter
    private List<RequisicionVO> requisicionesSeleccionadas = null;
    @Getter
    @Setter
    private List<RequisicionVO> requisicionesConContratoSeleccionadas = null;
    @Getter
    @Setter
    private List<RequisicionDetalleVO> itemsSeleccionados = null;

    /**
     * Creates a new instance of RecepcionRequisicion
     */
    public RecepcionRequisicionBean() {
    }

    @PostConstruct
    public void iniciar() {
        if (usuarioBean.getUsuarioConectado() != null) {
            actualizar = true;
            listaRequisicionContrato = requisicionServicioRemoto.traerRequisicionesArticuloContrato(
                    usuarioBean.getUsuarioConectado().getId(),
                    Constantes.REQUISICION_ASIGNADA,
                    usuarioBean.getUsuarioConectado().getApCampo().getId()
            );
            //
            setListaRequisiciones(
                    requisicionServicioRemoto.traerRequisicionesSinContrato(
                            this.usuarioBean.getUsuarioConectado().getId(),
                            Constantes.REQUISICION_ASIGNADA,
                            usuarioBean.getUsuarioConectado().getApCampo().getId()));
        }
        listaItems = new ArrayList<>();
        listaItemsOrden = new ArrayList<>();
        ordenesRechazadas = new ArrayList<>();
        listaArchivoConvenio = new ArrayList<>();
        requisicionesSeleccionadas = new ArrayList<>();
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
                    PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDevRecepReq);");
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
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoDevRecepReq);");
                //popupDevolverRequisicionBean.toggleModal();
                //Regresa a la tabla
                this.iniciar();
                PrimeFaces.current().executeScript(";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
            }//------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addInfoMessage("No se pudo devolver la requisición, por favor notifique el problema a: sia@ihsa.mx");
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }

    public void finalizarRequisicion(int idReq) {
        this.setRequisicionActual(requisicionServicioRemoto.find(idReq));
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
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoFinRecepReq);");
                //popupFinalizarRequisicionBean.toggleModal();
                //Regresa a la tabla
                PrimeFaces.current().executeScript(";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
            } //------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addErrorMessage("No se pudo finalizar la requisición, por favor notifique el problema a: sia@ihsa.mx");
            }
            actualizar = true;
        } catch (Exception ex) {
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
                PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoEsperaReq);");
                //popupFinalizarRequisicionBean.toggleModal();
                //Regresa a la tabla
                PrimeFaces.current().executeScript(";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
                ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
                contarBean.llenarReqEnEspera();
                contarBean.llenarReqEnEsperaAdm();
                contarBean.llenarRecReq();
            } //------- Si el correo no se pudo enviar  -----
            else {
                //---- Mostrar mensaje  ----
                FacesUtilsBean.addErrorMessage("No se pudo poner en espera la requisición, por favor notifique el problema a: sia@ihsa.mx");
            }
            actualizar = true;
            motivoFinalizacion = "";
            this.iniciar();
        } catch (Exception ex) {
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

    public List<RequisicionDetalleVO> itemsNativo() {
        try {
            List<RequisicionDetalleVO> lo = null;
            if (requisicionActual != null && requisicionActual.getId() != null && requisicionActual.getId() > 0) {
                if (requisicionActual.isMultiproyecto()) {
                    lo = this.requisicionServicioRemoto.getItemsAnalistaNativaMulti(this.requisicionActual.getId(), false);
                } else {
                    lo = this.requisicionServicioRemoto.getItemsAnalistaNativa(this.requisicionActual.getId(), false);
                }
                this.listaItems = lo;
            }
        } catch (Exception ex) {
            this.listaItems = null;
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
        return this.listaItems;
    }

    public void historyItem(int idInv, int idCampo) {
        try {
            if (getRequisicionActual() != null) {
                if (idInv > 0 && idCampo > 0) {
                    this.setHistoricoVentas(this.ordenDetalleImpl.historicoDetalleOrden(idInv, idCampo));
                } else {
                    this.setHistoricoVentas(new ArrayList<>());
                }
            }
            PrimeFaces.current().executeScript("$(dialogoHistorialCompraReq).modal('show');");
        } catch (NumberFormatException e) {
            UtilLog4j.log.fatal(this, e);
        }
    }

    public void seleccionarRequisicion(int idR) {
        try {

            setRequisicionActual(requisicionServicioRemoto.find(idR));
            if (this.getRequisicionActual().isMultiproyecto()) {
                listaItems = requisicionServicioRemoto.getItemsAnalistaNativaMulti(this.requisicionActual.getId(), false);
            } else {
                listaItems = requisicionServicioRemoto.getItemsAnalistaNativa(this.requisicionActual.getId(), false);
            }
            ordenesPorRequisicion();
            //
            etsPorRequisicion();
            mostrarOpcion = listaItems != null && listaItems.size() > 1;
        } catch (Exception e) {
            System.out.println("Error: {}" + e);
        }
    }

    public void etsPorRequisicion() {
        try {
            setListaEts(reRequisicionEtsImpl.traerAdjuntosPorRequisicion(getRequisicionActual().getId()));
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
            setListaEts(null);
        }
    }

    public void seleccionarRequisicionConvenio(int reqId) {
        setRequisicionActual(requisicionServicioRemoto.find(reqId));
        if (this.getRequisicionActual().isMultiproyecto()) {
            listaItems = requisicionServicioRemoto.getItemsAnalistaNativaMulti(this.requisicionActual.getId(), false);
        } else {
            listaItems = requisicionServicioRemoto.getItemsAnalistaNativa(this.requisicionActual.getId(), false);
        }
        //
        ordenesPorRequisicion();
        //
        mostrarOpcion = listaItems != null && listaItems.size() > 1;
    }

    public void ordenesPorRequisicion() {
        try {
            listaOrden = new ArrayList<>();
            listaOrden = ordenServicioRemoto.getOrdenesPorRequisicion(requisicionActual.getId(), null);
        } catch (Exception ex) {
            UtilLog4j.log.fatal("Error al recuperar las órdenes por requisicion. {}", ex);
        }
    }

    public void seleccionarOrden(int idOrden) {
        setOrdenActual(ordenServicioRemoto.find(idOrden));
        itemsPorOrden(idOrden);
        notasPorOrden(idOrden);
        ordenesRechazadas = ordenSiMovimientoImpl.getMovimientsobyOrden(idOrden);
        CargaEtsBean cargaEtsBean = (CargaEtsBean) FacesUtilsBean.getManagedBean("cargaEtsBean");
        //argaEtsBean.etsPorOrdenCategoria();
        cargaEtsBean.traerTablaComparativa(idOrden);
        cargaEtsBean.etsPorOrdenRequisicion();
        //
        cargaEtsBean.ordenEtsPorCategoria();
        cargaEtsBean.traerEspecificacionTecnica();
    }

    public void itemsPorOrden(int idOrden) {
        try {
            listaItemsOrden = ordenServicioRemoto.itemsPorOrdenCompraMulti(idOrden);
        } catch (Exception ex) {
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
        if (!requisicionesSeleccionadas.isEmpty()) {
            requisicionesSeleccionadas.stream().forEach(req -> {
                List<RequisicionDetalleVO> l = requisicionServicioRemoto.getItemsAnalistaNativa(req.getId(), false);
                if (l != null && !l.isEmpty()) {
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
                        setOrdenActual(null);
                    } catch (Exception e) {
                        setOrdenActual(null);
                        setRequisicionActual(null);
                    }
                } else {
                    setRequisicionActual(null);
                    FacesUtilsBean.addErrorMessage("No se generó la OC/S -" + req.getConsecutivo() + "-. Debido a que no tiene items autorizados.");
                }

            });
            listaRequisiciones = (requisicionServicioRemoto.traerRequisicionesSinContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId()));
            listaRequisicionContrato = requisicionServicioRemoto.traerRequisicionesArticuloContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId());
            actualizar = true;
            String jsMetodo = ";limpiarTodos();";
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinSolicitar();
            contarBean.llenarRecReq();
            //
            PrimeFaces.current().executeScript(jsMetodo);
        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos una requisicion");
        }
    }

    private void llenarRequisicionesRecibidas() {
        listaRequisiciones = (requisicionServicioRemoto.traerRequisicionesSinContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId()));
        listaRequisicionContrato = requisicionServicioRemoto.traerRequisicionesArticuloContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId());
    }

    public void generarOCSConvenio() {

        if (!requisicionesConContratoSeleccionadas.isEmpty()) {
            for (RequisicionVO req : requisicionesConContratoSeleccionadas) {
                List<RequisicionDetalleVO> l = requisicionServicioRemoto.getItemsAnalistaNativa(req.getId(), false);
                if (l != null && l.size() > 0) {
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
                        setOrdenActual(null);
                    } catch (Exception e) {
                        setOrdenActual(null);
                        setRequisicionActual(null);
                    }
                } else {
                    setRequisicionActual(null);
                    FacesUtilsBean.addErrorMessage("No se generó la OC/S -" + req.getConsecutivo() + "-. Debido a que no tiene items autorizados.");
                }
            }
            listaRequisiciones = (requisicionServicioRemoto.traerRequisicionesSinContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId()));
            listaRequisicionContrato = requisicionServicioRemoto.traerRequisicionesArticuloContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId());
            actualizar = true;
            String jsMetodo = ";limpiarTodos();";
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinSolicitar();
            contarBean.llenarRecReq();
            //
            PrimeFaces.current().executeScript(jsMetodo);

        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos una requisicion");
        }
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
                if (requisicionActual.isContrato()) {
                    listaRequisicionContrato = requisicionServicioRemoto.traerRequisicionesArticuloContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId());
                } else {
                    listaRequisiciones = (requisicionServicioRemoto.traerRequisicionesSinContrato(this.usuarioBean.getUsuarioConectado().getId(), Constantes.REQUISICION_ASIGNADA, usuarioBean.getUsuarioConectado().getApCampo().getId()));
                }
                //
                cambiarRequisicion(0);
                actualizar = true;
                setRequisicionActual(null);
                setConsecutivoRequisicion("");
                FacesUtilsBean.addInfoMessage("La orden de compra se generó correctamente...");
                //Limpiar listas
                setOrdenActual(null);

                PrimeFaces.current().executeScript(";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e.getMessage(), e);
            }

        } else {
            setRequisicionActual(null);
            PrimeFaces.current().executeScript(";alertaGeneral('No es posible generar la OC/S sin items.');");
        }
        llenarRequisicionesRecibidas();

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
        if (!itemsSeleccionados.isEmpty()) {
            try {
                for (RequisicionDetalleVO requisicionDetalleVO : itemsSeleccionados) {
                    requisicionDetalleImpl.cambiarDisgregadoItemRequisicion(requisicionDetalleVO, usuarioBean.getUsuarioConectado().getId(), Constantes.BOOLEAN_TRUE);

                }
                if (ordenGenerada == false) {
                    orden = crearOrden(requisicionActual.isContrato());//Constantes.FALSE);
                    ordenGenerada = true;
                }
                boolean v = ordenDetalleImpl.guardarListaItems(itemsSeleccionados, orden.getId(), usuarioBean.getUsuarioConectado().getId(), Constantes.OCS_SIN_CONTRATO);
                if (ordenGenerada && v) {
                    if (itemsSeleccionados.size() == listaItems.size()) {
                        requisicionServicioRemoto.finalizaRequisicion(usuarioBean.getUsuarioConectado().getId(), requisicionActual.getId());
                        mostrarOpcion = false;
                        cambiarRequisicion(0);
                        PrimeFaces.current().executeScript(";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
                    } else {
                        listaItems = (requisicionServicioRemoto.getItemsAnalistaNativa(requisicionActual.getId(), false));
                    }
                    FacesUtilsBean.addInfoMessage("La orden de compra se generó correctamente...");
                }
                actualizar = true;
                //
                llenarRequisicionesRecibidas();
                //
                ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
                contarBean.llenarOcsSinSolicitar();
                contarBean.llenarRecReq();
            } catch (Exception e) {
                UtilLog4j.log.fatal(this, e.getMessage(), e);
            }

        } else {
            FacesUtilsBean.addErrorMessage("Seleccione al menos un Ítem .  .  .");
        }

    }

    public Map<Integer, Boolean> getFilasSeleccionadas() {
        return filasSeleccionadas;
    }

    public void seleccionarItem(int idItem) {
        this.itemActual = requisicionDetalleImpl.find(idItem);
    }

    /**
     * Este metodo sirve para actualzar el Ítem desde el panel emergente
     *
     * @param
     */
    public void actualizarItem() {
        try {
            // se toma la linea de requisición
            this.itemActual.setDisgregado(Constantes.BOOLEAN_FALSE);// Por si lo seleccionan y actualizan
            this.requisicionServicioRemoto.actualizarItem(this.itemActual);
            FacesUtilsBean.addInfoMessage("El Ítem se actualizó correctamente...");
            //Esto es para cerrar el panel emergente de modificar Ítem
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
        }
    }

    public long getTotalRequisicionesSinDisgregar() {
        try {
            return requisicionServicioRemoto.getTotalRequisicionesSinDisgregar(this.usuarioBean.getUsuarioConectado().getId(), this.usuarioBean.getUsuarioConectado().getApCampo().getId());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage(), ex);
            return 0;
        }
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
        orden.setRepse(Boolean.FALSE);
        orden.setOcMetodoPago(requisicionActual.getOcMetodoPago());
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

    public void mostrarInventario(int idArt) {
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
     *
     * @return the listaRequisicionContrato
     */
    public List<RequisicionVO> getListaRequisicionContrato() {
        return listaRequisicionContrato;
    }

    /**
     * @param listaRequisicionContrato the listaRequisicionContrato to set
     */
    public void setListaRequisicionContrato(List<RequisicionVO> listaRequisicionContrato) {
        this.listaRequisicionContrato = listaRequisicionContrato;
    }

    /**
     * @return the listaRequisiciones
     */
    public List<RequisicionVO> getListaRequisiciones() {
        return listaRequisiciones;
    }

    /**
     * @param listaRequisiciones the listaRequisiciones to set
     */
    public void setListaRequisiciones(List<RequisicionVO> listaRequisiciones) {
        this.listaRequisiciones = listaRequisiciones;
    }

    /*
     */
    public String getNombreMultiProyectos() {
        nombreMultiProyectos = "";
        if (this.listaItems != null && !this.listaItems.isEmpty()) {
            nombreMultiProyectos = listaItems.get(0).getMultiProyectos();
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

    public void llenarListaConvenio(String idOr) {
        listaArchivoConvenio = cvConvenioAdjuntoImpl.traerPorConvenioPorNumero(idOr);
    }
}
