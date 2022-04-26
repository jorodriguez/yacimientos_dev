/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;


import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.MenuBarBean;
import sia.compra.requisicion.bean.backing.ProveedorBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Configurador;
import sia.constantes.Constantes;
import sia.modelo.Convenio;
import sia.modelo.OcFormaPago;
import sia.modelo.OcTipoCompra;
import sia.modelo.Orden;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ConvenioArticuloVo;
import sia.modelo.orden.vo.ContactoOrdenVo;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.vo.GeneralVo;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.ImpuestoImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.orden.impl.OcFlujoImpl;
import sia.servicios.orden.impl.OcFormaPagoImpl;
import sia.servicios.orden.impl.OcOrdenCoNoticiaImpl;
import sia.servicios.orden.impl.OcTerminoPagoImpl;
import sia.servicios.orden.impl.OcTipoCompraImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.orden.impl.OrdenSiMovimientoImpl;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.PvProveedorCompaniaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named (value = "solicitarOrdenBean")
@ViewScoped
public class SolicitarOrdenBean implements Serializable {

    /**
     * Creates a new instance of SolicitarOrdenBean
     */
    public SolicitarOrdenBean() {
    }
    @Inject
    private UsuarioBean sesion;
    @Inject
    ProveedorBean proveedorBean; // = (ProveedorBean) FacesUtilsBean.getManagedBean("proveedorBean");

    //
    @Inject
    private ImpuestoImpl impuestoImpl;
    @Inject
    private OcTerminoPagoImpl ocTerminoPagoImpl;
    @Inject
    private OcFormaPagoImpl ocFormaPagoImpl;
    @Inject
    private OcTipoCompraImpl ocTipoCompraImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private ContactoProveedorImpl contactoProveedorServicioImpl;
    @Inject
    private PvProveedorCompaniaImpl proveedorCompaniaImpl;
    @Inject
    private OcOrdenCoNoticiaImpl ocOrdenCoNoticiaImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private OcFlujoImpl ocFlujoImpl;
    @Inject
    private OrdenSiMovimientoImpl ordenSiMovimientoImpl;
    //
    private Orden ordenActual;
    private String proveedorSeleccionado; //almacena el proveedor o convenio seleccionado

    private List<SelectItem> lstImpuestos;
    //
    private List<SelectItem> listaTerminoPago;
    private List<SelectItem> listaTipoPago;
    private List<SelectItem> listaFormaPago;
    private List<SelectItem> listaProveedorEnConvenio;
    private List<SelectItem> usuarioAprueba;
    private List<SelectItem> usuarioRevisa;
    private List<ContactoProveedorVO> listaContactos; //almacena la lista de contactos de cada proveedor
    private List contactos = new ArrayList();//almacena la lista de contactos de la orden de compra
    private int idProveedorr;
    private List<OrdenDetalleVO> listaItems; //almacena la lista de Items de la Orden de compra
    private String aprueba, revisa;
    private String monedaSeleccionada;
    private int idTerminoPago;
    private String tipoOrden = Constantes.VACIO;
    private String condicionSeleccionada = Constantes.VACIO;
    private final static String ACTION_ORDEN_COMPRA = "ordenCompra";
    private final static String ERR_OCS_NO_SOLIC = "No se pudo solicitar la orden de compra y/o servicio, por favor notifique el problema a: soportesia@ihsa.mx";
    private boolean mostrarTipoOrden = true;
    private int iva = 0;
    private String consecutivo;
    //Lista de contratos
    private List<SelectItem> listConvenio = null;
    private Map<String, List<ConvenioArticuloVo>> listaMapa;
    private ContratoVO contratoVOO;
    private List<MovimientoVO> rechazosCarta = null;
    private String archivoRepse;

    @PostConstruct
    public void iniciar() {
        int idOrden = 0;
        archivoRepse = "";
        if (FacesUtilsBean.getRequestParameter("idOrden") != null && !FacesUtilsBean.getRequestParameter("idOrden").isEmpty()) {
            idOrden = Integer.parseInt(FacesUtilsBean.getRequestParameter("idOrden"));
        } else {
            idOrden = Integer.parseInt(FacesUtilsBean.getRequestParameter("idOrdenSel"));
        }
        //
        listaItems = ordenImpl.itemsPorOrdenCompra(idOrden);
        //
        cambiarPagina(idOrden);

        setListaTerminoPago(new ArrayList<>());
        try {
            List<GeneralVo> tempList = ocTerminoPagoImpl.listaTerminoPago(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc());
            for (GeneralVo gvo : tempList) {
                SelectItem item = new SelectItem(gvo.getValor(), gvo.getNombre());
                getListaTerminoPago().add(item);
            }
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        //
        setListaTipoPago(new ArrayList<>());
        for (OcTipoCompra ocTipoCompra : ocTipoCompraImpl.traerTipoCompra()) {
            getListaTipoPago().add(new SelectItem(ocTipoCompra.getId(), ocTipoCompra.getNombre()));
        }
        setListaFormaPago(new ArrayList<>());
        for (OcFormaPago ocFormaPago : ocFormaPagoImpl.traerFormaPago()) {
            getListaFormaPago().add(new SelectItem(ocFormaPago.getId(), ocFormaPago.getNombre()));
        }
        //
        listaUsuariosAprueban();
        //
        listaUsuariosRevisan();
        //
        ordenActual.setOcTipoCompra(new OcTipoCompra());
        ordenActual.setOcFormaPago(new OcFormaPago());

    }

    private void listaUsuariosAprueban() {
        usuarioAprueba = new ArrayList<>();
        try {
            List<UsuarioTipoVo> tempList = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_APROBAR, sesion.getUsuarioConectado().getApCampo().getId(), Constantes.BOOLEAN_FALSE);
            for (UsuarioTipoVo nombre : tempList) {
                SelectItem item = new SelectItem(nombre.getIdUser(), nombre.getUsuario());
                usuarioAprueba.add(item);
            }
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" + ex.getMessage());
        }
    }

    private void listaUsuariosRevisan() {
        usuarioRevisa = new ArrayList<>();

        if (getOrdenActual() != null) {
            UsuarioResponsableGerenciaVo urgv = gerenciaImpl.traerResponsablePorApCampoYGerencia(getOrdenActual().getApCampo().getId(),
                    getOrdenActual().getRequisicion().getGerencia().getId());

            listaUsuariosRevisan(urgv, ordenActual.getApCampo().getId());
        }
    }

    private void listaUsuariosRevisan(UsuarioResponsableGerenciaVo usuario, int apCampo) {
        usuarioRevisa = new ArrayList<>();
        try {
            if (usuario != null) {
                usuarioRevisa.add(new SelectItem(usuario.getIdUsuario(), usuario.getNombreUsuario()));
            }
            List<UsuarioTipoVo> tempList = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, apCampo, Constantes.BOOLEAN_FALSE);
            for (UsuarioTipoVo listaU : tempList) {
                if (usuario != null) {
                    if (!usuario.getIdUsuario().equals(listaU.getIdUser())) {
                        usuarioRevisa.add(new SelectItem(listaU.getIdUser(), listaU.getUsuario()));
                    }
                } else {
                    usuarioRevisa.add(new SelectItem(listaU.getIdUser(), listaU.getUsuario()));
                }
            }
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" + ex.getMessage());
        }
    }

    private void cambiarPagina(int idOrden) {
        if (idOrden > 0) {
            setOrdenActual(ordenImpl.find(idOrden));
            if (this.getOrdenActual() != null && this.getOrdenActual().getContrato() != null
                    && !this.getOrdenActual().getContrato().isEmpty() && !"OCS_SIN_CONTRATO".equals(ordenActual.getContrato())) {
                this.contratoVOO = convenioImpl.traerConveniosPorCodigo(ordenActual.getContrato());
            }
        }
        getOrdenActual().setFechaEntrega(new Date());
        //
        getContactos().clear();
        setListaContactos(null);
        if (this.getOrdenActual().getProveedor() != null) {
            if (proveedorCompaniaImpl.buscarRelacionProveedorCompania(getOrdenActual().getProveedor().getId(), getOrdenActual().getCompania().getRfc())) {
                //
                setIdProveedorr((int) getOrdenActual().getProveedor().getId());
                setListaContactos(contactoProveedorServicioImpl.traerContactoPorProveedor(getIdProveedorr(), Constantes.CONTACTO_REP_COMPRAS));
                proveedorBean.llenarJsonProveedor(getOrdenActual().getProveedor().getId(), ordenActual.getCompania().getRfc());
                List<ContactoOrdenVo> lco = ordenImpl.getContactosVo(getOrdenActual().getId());
                // Verifica los contactos agregados
                for (ContactoOrdenVo contactosOrden : lco) {
                    for (ContactoProveedorVO contactoProveedorVO : getListaContactos()) {
                        if (contactosOrden.getIdContactoProveedor() == contactoProveedorVO.getIdContactoProveedor()) {
                            contactoProveedorVO.setSelected(true);
                        }
                    }
                }
                setProveedorSeleccionado(getOrdenActual().getProveedor().getNombre());
            } else {
                getOrdenActual().setProveedor(null);
                setProveedorSeleccionado("");
            }
            PrimeFaces.current().executeScript( ";mostrarDiv('divContactoProveedor');");
        }
        if (getOrdenActual().isConConvenio()) {
            setListaProveedorEnConvenio(new ArrayList<>());
            Convenio conv = convenioImpl.buscarContratoPorNumero(ordenActual.getContrato());
            idProveedorr = conv.getProveedor().getId();
            //
            proveedorBean.llenarJsonProveedor(getIdProveedorr(), ordenActual.getCompania().getRfc());
            //
            listConvenio = new ArrayList<>();
            listConvenio.add(new SelectItem(conv.getCodigo(), conv.getCodigo() + ", " + conv.getNombre()));
            //
        } else {
            proveedorBean.llenarJson(ordenActual.getCompania().getRfc());
        }
        contactoConnvProveedor();
        //
        if (getOrdenActual() != null && getOrdenActual().getAutorizacionesOrden() != null) {
            if (getOrdenActual().getAutorizacionesOrden().getAutorizaMpg() != null) {
                setRevisa(getOrdenActual().getAutorizacionesOrden().getAutorizaMpg().getId());
            }
            if (getOrdenActual().getAutorizacionesOrden().getAutorizaIhsa() != null) {
                setAprueba(getOrdenActual().getAutorizacionesOrden().getAutorizaIhsa().getId());
            }
        }
        if (getOrdenActual() != null && getOrdenActual().getOcTerminoPago() != null) {
            setIdTerminoPago(getOrdenActual().getOcTerminoPago().getId());
        }
        if (getOrdenActual() != null && getOrdenActual().getMoneda() != null) {
            setMonedaSeleccionada(getOrdenActual().getMoneda().getNombre());
        }
        setLstImpuestos(impuestoImpl.traerImpuestoItems(getOrdenActual().getApCampo().getCompania().getRfc(), 0));
        //return "solicitarOrden";
    }
   

    private void contactoConnvProveedor() {
        setListaContactos(proveedorBean.traerContactoPorProveedor(getIdProveedorr(), Constantes.CONTACTO_REP_COMPRAS));
        //
        if (!ordenActual.isConConvenio()) {
            traerConvenios(null);
        } else {
            traerConvenios(ordenActual.getContrato());
        }

        if (getListaContactos() == null || getListaContactos().isEmpty()) {
            PrimeFaces.current().executeScript( ";mostrarDiv('divNoContactoProveedor');");
            //
            PrimeFaces.current().executeScript( ";ocultarDiv('divContactoProveedor');");
        } else {
            PrimeFaces.current().executeScript( ";mostrarDiv('divContactoProveedor');");
            //
            PrimeFaces.current().executeScript( ";ocultarDiv('divNoContactoProveedor');");
        }

        //Buscar si el proveedor ha rechazado aglguna carta de intención
        if (ordenActual.getApCampo().isCartaIntencion()) {
            setRechazosCarta(new ArrayList<MovimientoVO>());
            setRechazosCarta(ordenSiMovimientoImpl.traerCartasRechazadasProveedor(getIdProveedorr()));
            if (getRechazosCarta() != null && !rechazosCarta.isEmpty()) {
                PrimeFaces.current().executeScript(
                        "$(dialogoCartasRechazadas).modal('show');");
            }
            //;alertaGeneral('El proveedor seleccionado previamente ha rechazado compras asignadas.');
        }

//        if (listConvenio == null || listConvenio.isEmpty()) {
//            PrimeFaces.current().executeScript( ";ocultarDiv('tablaConvenio');");
//            PrimeFaces.current().executeScript( ";mostrarDiv('ocsSinContrato');");
//        } else {
//            PrimeFaces.current().executeScript( ";mostrarDiv('tablaConvenio');");
//            PrimeFaces.current().executeScript( ";ocultarDiv('ocsSinContrato');");
//        }
    }

    private void traerConvenios(String codigoConvenio) {
        if (getIdProveedorr() > 0) {
            listConvenio = new ArrayList<>();
            if (codigoConvenio == null || codigoConvenio.isEmpty()) {
                listConvenio.add(new SelectItem(Constantes.OCS_SIN_CONTRATO, "Orden sin contrato"));
            }
            List<ContratoVO> lc = convenioImpl.getListConvenioVigente(getIdProveedorr(), ordenActual.getApCampo().getId(), codigoConvenio);
            if (listConvenio != null && getIdProveedorr() > 0) {
                for (ContratoVO contratooVo : lc) {
                    listConvenio.add(new SelectItem(contratooVo.getNumero(), contratooVo.getNumero() + ", " + contratooVo.getNombre()));
                }
            }
        }
    }

    public List getTiposOrdenes() {
        List resultList = new ArrayList();
        try {
            SelectItem item = new SelectItem("Orden de Compra");
            resultList.add(item);

            SelectItem item2 = new SelectItem("Orden de Servicio");
            resultList.add(item2);

            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
        return resultList;
    }

    public void cancelarSolicitudOrden() {
        MenuBarBean menuBarBean = (MenuBarBean) FacesUtilsBean.getManagedBean("menuBarBean");
        menuBarBean.procesarAccion(ACTION_ORDEN_COMPRA);
    }

//    public void seleccionarProveedor(AjaxBehaviorEvent event) {
//        if (event != null) {
//            proveedorBean.llenarJsonProveedor(idProveedorr);
//            contactoConnvProveedor();
//        }
//    }
    public List<SelectItem> getListConvenio() {
        return listConvenio;
    }

    /**
     * @param listConvenio the listConvenio to set
     */
    public void setListConvenio(List<SelectItem> listConvenio) {
        this.listConvenio = listConvenio;
    }

    public String completarSolicitudOrden() {
        String pagina = Constantes.VACIO;
        if (this.monedaSeleccionada == null || this.monedaSeleccionada.isEmpty()) {
            FacesUtilsBean.addErrorMessage("Para solicitar la orden tiene que especificar un tipo de moneda..");
        } else if (!"OCS_SIN_CONTRATO".equals(ordenActual.getContrato()) && ordenActual.getContrato() != null && !ordenActual.getContrato().isEmpty() && ordenActual.getMoneda().getId() != this.contratoVOO.getIdMoneda()) {
            FacesUtilsBean.addErrorMessage("El contrato seleccionado tiene una moneda distinta a la moneda de la orden de compra.");
        } else if (Configurador.validarConvenio() && !"OCS_SIN_CONTRATO".equals(ordenActual.getContrato()) && ordenActual.getContrato() != null && !ordenActual.getContrato().isEmpty() && !ordenImpl.validarConvenio(ordenActual.getId(), ordenActual.getContrato())) {
            ordenImpl.notificarValidarContrato(
                    ordenActual,
                    "El convenio " + ordenActual.getContrato() + " no cuenta con saldo suficiente para realizar la compra.");
            FacesUtilsBean.addErrorMessage("El convenio " + ordenActual.getContrato() + " no cuenta con saldo suficiente para realizar la compra.");
        } else if (archivoRepse == null) {
            FacesUtilsBean.addErrorMessage("Para solicitar la oc/s debe especificar si la compra requiere mano de obra en sitio ..");
        } else {
            if (getOrdenActual().getConsecutivo() == null || getOrdenActual().getConsecutivo().isEmpty()) {
                // si no tiene consecutivo verificar si selecciono el tipo de orden
                if (this.getTipoOrden() == null || this.getTipoOrden().isEmpty()) {
                    FacesUtilsBean.addErrorMessage("Para solicitar la orden por favor especifique que tipo de Orden es..");
                } else {
                    // Solicitar la orden
                    filtrarContactos();
                    try {
                        ordenActual.setRepse(archivoRepse.equals("Si") ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                        boolean solicitada = ordenImpl.solicitarOrden(getIdProveedorr(), getRevisa(), aprueba,
                                getCondicionSeleccionada(), monedaSeleccionada,
                                getOrdenActual().getFechaEntrega(),
                                getOrdenActual(), getIva(), getTipoOrden(), sesion.getUsuarioConectado().getApCampo().getId(),
                                getListaContactos(), sesion.getUsuarioConectado().getId(),
                                getIdTerminoPago(), ordenActual.getContrato());

                        if (solicitada) {
                            FacesUtilsBean.addInfoMessage("La orden fue solicitada correctamente..");
                            this.contactos.clear();
                            listaContactos.clear();
                            setIdProveedorr(-1);
                            this.setCondicionSeleccionada(Constantes.VACIO);
                            this.monedaSeleccionada = Constantes.VACIO;
                            this.aprueba = Constantes.VACIO;
                            this.setTipoOrden(Constantes.VACIO);
                            setOrdenActual(null);
                            setConsecutivo(null);
                            quitarSeleccionOrden(true);
                            pagina = ACTION_ORDEN_COMPRA;
                        } else {
                            FacesUtilsBean.addErrorMessage(ERR_OCS_NO_SOLIC);
                        }
                    } catch (Exception e) {
                        if ("NOPIRINEOS".equals(e.getMessage())) {
                            FacesUtilsBean.addErrorMessage("La empresa MPG solo puede solicitar requisiciones del bloque PIRINEOS.");
                        } else {
                            FacesUtilsBean.addErrorMessage(ERR_OCS_NO_SOLIC);
                        }
                        UtilLog4j.log.fatal(this, e.getMessage());
                    }
                }
            } else {
                // si tiene consecutivo asignado solicitar la orden
                try {
                    filtrarContactos();
                    //, getIdFormaPago()
                    ordenActual.setRepse(archivoRepse.equals("Si") ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                    boolean solicitada
                            = ordenImpl.solicitarOrden(getIdProveedorr(), getRevisa(),
                                    aprueba, getCondicionSeleccionada(),
                                    monedaSeleccionada, getOrdenActual().getFechaEntrega(),
                                    getOrdenActual(), getIva(), getTipoOrden(),
                                    sesion.getUsuarioConectado().getApCampo().getId(),
                                    getListaContactos(), sesion.getUsuarioConectado().getId(),
                                    getIdTerminoPago(), ordenActual.getContrato()
                            );

                    if (solicitada) {
                        FacesUtilsBean.addInfoMessage("La orden fue solicitada correctamente..");
                        contactos.clear();
                        listaContactos.clear();
                        setIdProveedorr(-1);
                        setCondicionSeleccionada(Constantes.VACIO);
                        monedaSeleccionada = Constantes.VACIO;
                        aprueba = Constantes.VACIO;
                        setTipoOrden(Constantes.VACIO);
                        setRevisa(Constantes.VACIO);
                        setOrdenActual(null);
                        setConsecutivo(null);
                        this.quitarSeleccionOrden(true);
                    } else {
                        FacesUtilsBean.addInfoMessage(ERR_OCS_NO_SOLIC);
                    }
                    pagina = ACTION_ORDEN_COMPRA;
                } catch (Exception e) {
                    if ("NOPIRINEOS".equals(e.getMessage())) {
                        FacesUtilsBean.addErrorMessage("La empresa MPG solo puede solicitar requisiciones del bloque PIRINEOS.");
                    } else {
                        FacesUtilsBean.addErrorMessage(ERR_OCS_NO_SOLIC);
                    }
                }
            }
        }
        //}
        return pagina;
    }

    public void quitarSeleccionOrden(boolean limpiar) {
        // true quita la orden seleccionada y limpia la pantalla
        // false selecciona la ultima orden d la lista si es q hay si no limpia todo...
        setOrdenActual(ordenImpl.find(0));

        List<OrdenDetalleVO> lo = new ArrayList<>();

        setListaItems((lo));
        setConsecutivo(Constantes.VACIO);
    }

    public void filtrarContactos() {
        List<ContactoProveedorVO> l = new ArrayList<>();
        for (ContactoProveedorVO sgV : getListaContactos()) {
            if (sgV.isSelected()) {
                l.add(sgV);
            }
        }
        UtilLog4j.log.info(this, "Filas seleccionadas: " + l.size());
        setListaContactos(l);
    }

    private void finalizarNoticiaOrden(String sesion, int idOrden) {
        ocOrdenCoNoticiaImpl.finalizarNoticia(sesion, idOrden);
    }

    public void seleccionarContrato(AjaxBehaviorEvent event) {
        this.contratoVOO = convenioImpl.traerConveniosPorCodigo(ordenActual.getContrato());

        if (this.contratoVOO != null) {
            boolean isMsg = false;
            String msg = "";
            if (ordenActual.getMoneda().getId() != this.contratoVOO.getIdMoneda()) {
                msg += "El contrato seleccionado tiene una moneda distinta a la moneda de la orden de compra. ";
                isMsg = true;
            }
            if (siManejoFechaImpl.compare(this.contratoVOO.getFechaVencimiento(), new Date()) < 0) {
                // el contrato eta vencido
                msg += "El contrato seleccionado esta vencido. ";
                isMsg = true;
            }
            if (isMsg) {
                isMsg = false;
                PrimeFaces.current().executeScript( ";alertaGeneral('" + msg + "');");
            }
        }
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
     * @return the lstImpuestos
     */
    public List<SelectItem> getLstImpuestos() {
        return lstImpuestos;
    }

    /**
     * @param lstImpuestos the lstImpuestos to set
     */
    public void setLstImpuestos(List<SelectItem> lstImpuestos) {
        this.lstImpuestos = lstImpuestos;
    }

    /**
     * @return the listaTerminoPago
     */
    public List<SelectItem> getListaTerminoPago() {
        return listaTerminoPago;
    }

    /**
     * @param listaTerminoPago the listaTerminoPago to set
     */
    public void setListaTerminoPago(List<SelectItem> listaTerminoPago) {
        this.listaTerminoPago = listaTerminoPago;
    }

    /**
     * @return the listaTipoPago
     */
    public List<SelectItem> getListaTipoPago() {
        return listaTipoPago;
    }

    /**
     * @param listaTipoPago the listaTipoPago to set
     */
    public void setListaTipoPago(List<SelectItem> listaTipoPago) {
        this.listaTipoPago = listaTipoPago;
    }

    /**
     * @return the listaFormaPago
     */
    public List<SelectItem> getListaFormaPago() {
        return listaFormaPago;
    }

    /**
     * @param listaFormaPago the listaFormaPago to set
     */
    public void setListaFormaPago(List<SelectItem> listaFormaPago) {
        this.listaFormaPago = listaFormaPago;
    }

    /**
     * @return the listaProveedorEnConvenio
     */
    public List<SelectItem> getListaProveedorEnConvenio() {
        return listaProveedorEnConvenio;
    }

    /**
     * @param listaProveedorEnConvenio the listaProveedorEnConvenio to set
     */
    public void setListaProveedorEnConvenio(List<SelectItem> listaProveedorEnConvenio) {
        this.listaProveedorEnConvenio = listaProveedorEnConvenio;
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
     * @return the contactos
     */
    public List getContactos() {
        return contactos;
    }

    /**
     * @param contactos the contactos to set
     */
    public void setContactos(List contactos) {
        this.contactos = contactos;
    }

    /**
     * @return the idProveedorr
     */
    public int getIdProveedorr() {
        return idProveedorr;
    }

    /**
     * @param idProveedorr the idProveedorr to set
     */
    public void setIdProveedorr(int idProveedorr) {
        this.idProveedorr = idProveedorr;
    }

    /**
     * @return the proveedorSeleccionado
     */
    public String getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    /**
     * @param proveedorSeleccionado the proveedorSeleccionado to set
     */
    public void setProveedorSeleccionado(String proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
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
     * @return the monedaSeleccionada
     */
    public String getMonedaSeleccionada() {
        return monedaSeleccionada;
    }

    /**
     * @param monedaSeleccionada the monedaSeleccionada to set
     */
    public void setMonedaSeleccionada(String monedaSeleccionada) {
        this.monedaSeleccionada = monedaSeleccionada;
    }

    /**
     * @return the idTerminoPago
     */
    public int getIdTerminoPago() {
        return idTerminoPago;
    }

    /**
     * @param idTerminoPago the idTerminoPago to set
     */
    public void setIdTerminoPago(int idTerminoPago) {
        this.idTerminoPago = idTerminoPago;
    }

    /**
     * @return the tipoOrden
     */
    public String getTipoOrden() {
        return tipoOrden;
    }

    /**
     * @param tipoOrden the tipoOrden to set
     */
    public void setTipoOrden(String tipoOrden) {
        this.tipoOrden = tipoOrden;
    }

    /**
     * @return the condicionSeleccionada
     */
    public String getCondicionSeleccionada() {
        return condicionSeleccionada;
    }

    /**
     * @param condicionSeleccionada the condicionSeleccionada to set
     */
    public void setCondicionSeleccionada(String condicionSeleccionada) {
        this.condicionSeleccionada = condicionSeleccionada;
    }

    /**
     * @return the mostrarTipoOrden
     */
    public boolean isMostrarTipoOrden() {
        mostrarTipoOrden
                = getOrdenActual() == null ? false : getOrdenActual().getConsecutivo() == null;

        return mostrarTipoOrden;
    }

    /**
     * @param mostrarTipoOrden the mostrarTipoOrden to set
     */
    public void setMostrarTipoOrden(boolean mostrarTipoOrden) {
        this.mostrarTipoOrden = mostrarTipoOrden;
    }

    /**
     * @return the iva
     */
    public int getIva() {
        return iva;
    }

    /**
     * @param iva the iva to set
     */
    public void setIva(int iva) {
        this.iva = iva;
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

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the usuarioAprueba
     */
    public List<SelectItem> getUsuarioAprueba() {
        return usuarioAprueba;
    }

    /**
     * @param usuarioAprueba the usuarioAprueba to set
     */
    public void setUsuarioAprueba(List<SelectItem> usuarioAprueba) {
        this.usuarioAprueba = usuarioAprueba;
    }

    /**
     * @return the usuarioRevisa
     */
    public List<SelectItem> getUsuarioRevisa() {
        return usuarioRevisa;
    }

    /**
     * @param usuarioRevisa the usuarioRevisa to set
     */
    public void setUsuarioRevisa(List<SelectItem> usuarioRevisa) {
        this.usuarioRevisa = usuarioRevisa;
    }

    /**
     * @return the listaMapa
     */
    public Map<String, List<ConvenioArticuloVo>> getListaMapa() {
        return listaMapa;
    }

    /**
     * @param listaMapa the listaMapa to set
     */
    public void setListaMapa(Map<String, List<ConvenioArticuloVo>> listaMapa) {
        this.listaMapa = listaMapa;
    }

    /**
     * @param proveedorBean the proveedorBean to set
     */
    public void setProveedorBean(ProveedorBean proveedorBean) {
        this.proveedorBean = proveedorBean;
    }

    /**
     * @return the rechazosCarta
     */
    public List<MovimientoVO> getRechazosCarta() {
        return rechazosCarta;
    }

    /**
     * @param rechazosCarta the rechazosCarta to set
     */
    public void setRechazosCarta(List<MovimientoVO> rechazosCarta) {
        this.rechazosCarta = rechazosCarta;
    }

    /**
     * @return the archivoRepse
     */
    public String getArchivoRepse() {
        return archivoRepse;
    }

    /**
     * @param archivoRepse the archivoRepse to set
     */
    public void setArchivoRepse(String archivoRepse) {
        this.archivoRepse = archivoRepse;
    }

}
