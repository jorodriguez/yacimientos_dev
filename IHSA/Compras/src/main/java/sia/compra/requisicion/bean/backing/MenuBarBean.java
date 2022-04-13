/*
 * MenuBarBean.java
 * Creado el 16/06/2009, 10:21:39 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 16/06/2009
 */
import java.io.Serializable;
import javax.faces.bean.CustomScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;

import sia.compra.orden.bean.backing.NotaOrdenBean;
import sia.compra.orden.bean.backing.OrdenBean;
import sia.compra.orden.bean.backing.RecepcionRequisicionBean;
import sia.constantes.Constantes;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcUsuarioOpcionImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.UtilLog4j;

/**
 * <p>
 * The MenuBarBean class determines which menu item fired the ActionEvent and
 * stores the modified id information in a String. MenuBarBean also controls the
 * orientation of the Menu Bar.</p>
 */
@Named(value = "menuBarBean")
@CustomScoped(value = "#{window}")
public class MenuBarBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "menuBarBean";
    //------------------------------------------------------
    @Inject
    private OcUsuarioOpcionImpl ocUsuarioOpcionImpl;
    @Inject
    private SiOpcionImpl siOpcionImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    private String paginaActual = "Principal";
    // accion que ejecuta el sistema
    private String accionMenu;

    @Inject
    private UsuarioBean usuarioBean;
    private SiOpcionVo opcionVo;
    private String paginaDestino;
    private String paginaOrigen;

    /**
     * @return the menuRequisicion
     */
    /**
     * @return the accionMenu
     */
    public String getAccionMenu() {
        return accionMenu;
    }

    /**
     * @param accionMenu the accionMenu to set
     */
    public void setAccionMenu(String accionMenu) {
        this.accionMenu = accionMenu;
    }

    /**
     * @return the opcionVo
     */
    public SiOpcionVo getOpcionVo() {
        return opcionVo;
    }

    /**
     * @param opcionVo the opcionVo to set
     */
    public void setOpcionVo(SiOpcionVo opcionVo) {
        this.opcionVo = opcionVo;
    }

    /**
     * @return the paginaDestino
     */
    public String getPaginaDestino() {
        return paginaDestino;
    }

    /**
     * @return the paginaOrigen
     */
    public String getPaginaOrigen() {
        return paginaOrigen;
    }

    /**
     * @param paginaOrigen the paginaOrigen to set
     */
    public void setPaginaOrigen(String paginaOrigen) {
        this.paginaOrigen = paginaOrigen;
    }

    private enum Comando {

        crearRequisicion, catalogoServicios, serviciosAdicionales, solicitarRequisicion, revisarRequisicion, aprobarRequisicion, aceptarRequisicion, autorizarRequisicion,
        vistoBuenoRequisicion, asignarRequisicion, notaRequisicion, historialRequisicion, detalleHistorial, recepcionRequisicion, tablaComparativa,
        ordenCompra, solicitarOrden, aprobarOrden, autorizaMpg, autorizaIhsa, autorizaCompras, autorizaTarea, autorizaTareaExcel, notaOrden, ayuda, listaConvenios, detalleOrden, cargarETS, cerrarSesion, novalue, historialOrden,
        autorizaFinanza, autorizaSocio, paginaPrincipal;

        public static Comando getOpcion(String Str) {
            try {
                return valueOf(Str);
            } catch (Exception ex) {
                return novalue;
            }
        }
    }

    /*
     *
     * @PostConstruct public void menuOCS() { System.out.println("Menu : : : :
     * :"); try { List<SiOpcionVo> lo =
     * siOpcionImpl.getSipcionesSinRol(Constantes.MODULO_REQUISICION, "id",
     * true, false);
     *
     * for (SiOpcionVo opcVo : lo) { SiOpcionVo opcionVo = new SiOpcionVo();
     * opcionVo.setId(opcVo.getId()); opcionVo.setNombre(opcVo.getNombre());
     * opcionVo.setPagina(opcVo.getNombre()); menuRequisicion.add(opcionVo); }
     * List<SiOpcionVo> locs =
     * siOpcionImpl.getSipcionesSinRol(Constantes.MODULO_COMPRA, "id", true,
     * false);
     *
     * for (SiOpcionVo opcVo : locs) { SiOpcionVo opcionVo = new SiOpcionVo();
     * opcionVo.setId(opcVo.getId()); opcionVo.setNombre(opcVo.getNombre());
     * opcionVo.setPagina(opcVo.getNombre()); menuOrden.add(opcionVo); }
     *
     * } catch (Exception e) { } }
     */
    public void marcarComoPrincipal() {
        try {
            //   
            SiOpcionVo siOpcionVo = siOpcionImpl.buscarOpcion(getAccionMenu());
            //    
            if (siOpcionVo != null) {
                ocUsuarioOpcionImpl.guardar(usuarioBean.getUsuarioConectado().getId(), siOpcionVo.getId());
            } else {
                siOpcionVo = siOpcionImpl.buscarOpcion("/vistas/SiaWeb/Requisiciones/CrearRequisicion.xhtml");
                ocUsuarioOpcionImpl.guardar(usuarioBean.getUsuarioConectado().getId(), siOpcionVo.getId());
                //
            }
            PrimeFaces.current().executeScript(";cerrarDialogoModal(popAgregarPagina);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al guardar la opcion principal. " + e.getMessage());
            PrimeFaces.current().executeScript(";cerrarDialogoModal(popAgregarPagina);");
        }
    }

    /**
     * Optiene el parametro del elemento que disparó el evento del menu de la
     * aplicación Este metodo sirve para cambiar paginas principales de la
     * aplicación. Paginas Como: Requisiciones(Crear, Revisar, Aprobar,
     * Autorizar, etc), Compras (), Pagina princpal, cerrar sesion, etc.
     *
     * @param e the event that fired the listener
     */
    public void escuchaClick() {
        RequisicionBean requisicionBean = (RequisicionBean) FacesUtilsBean.getManagedBean("requisicionBean");
        NotaOrdenBean notaOrdenBean = (NotaOrdenBean) FacesUtilsBean.getManagedBean("notaOrdenBean");
        NotaRequisicionBean notaRequisicionBean = (NotaRequisicionBean) FacesUtilsBean.getManagedBean("notaRequisicionBean");
        //-----Esto es para Quitar los datos de la requisicion seleccionada
        requisicionBean.setRequisicionActual(null);

        OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
        //-----Esto es para Quitar los datos de la requisicion seleccionada
        ordenBean.setOrdenActual(null);
        ordenBean.setConsecutivo(null);

        //-----Esto es para poner actualizar true y vaya por la lista
        RecepcionRequisicionBean recepcionRequisicionBean = (RecepcionRequisicionBean) FacesUtilsBean.getManagedBean("recepcionRequisicionBean");
        recepcionRequisicionBean.setActualizar(false);
        recepcionRequisicionBean.setRequisicionActual(null);
        recepcionRequisicionBean.setConsecutivoRequisicion("");
        recepcionRequisicionBean.setConsecutivoRequisicion("");
        //notas
        notaOrdenBean.setFiltrar(true);
        notaRequisicionBean.setFiltrar(true);
        //------------------------------------------------------------
        String accion = FacesUtilsBean.getRequestParameter("myParam");

        System.out.println("Ruta actual: " + accion);

        if (accion.contains("istorialRequisicion")) {
            requisicionBean.setTipoFiltro(Constantes.FILTRO);
            requisicionBean.llenarProyectoOT();
//            requisicionBean.setIdStatus(Constantes.REQUISICION_SOLICITADA);
//            requisicionBean.setIdProyectoOT(-1);
            setAccionMenu(accion);
        } else if (accion.contains("istorialOrden")) {
            ordenBean.setTipoFiltro(Constantes.FILTRO);
            setAccionMenu(accion);
        } else {
            ordenBean.limpiarListaOrdenesSolicitadas();
            requisicionBean.limpiarListaRequisionesSolicitadas();
            setAccionMenu(accion);
        }
        //
//        cambiaTituloPagina(this.accionMenu);
    }

    public String cambiarPagina() {
        RequisicionBean requisicionBean = (RequisicionBean) FacesUtilsBean.getManagedBean("requisicionBean");
        NotaOrdenBean notaOrdenBean = (NotaOrdenBean) FacesUtilsBean.getManagedBean("notaOrdenBean");
        NotaRequisicionBean notaRequisicionBean = (NotaRequisicionBean) FacesUtilsBean.getManagedBean("notaRequisicionBean");
        //-----Esto es para Quitar los datos de la requisicion seleccionada
        requisicionBean.setRequisicionActual(null);

        OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
        //-----Esto es para Quitar los datos de la requisicion seleccionada
        ordenBean.setOrdenActual(null);
        ordenBean.setConsecutivo(null);

        //-----Esto es para poner actualizar true y vaya por la lista
//        RecepcionRequisicionBean recepcionRequisicionBean = (RecepcionRequisicionBean) FacesUtilsBean.getManagedBean("recepcionRequisicionBean");
//        recepcionRequisicionBean.setActualizar(true);
//        recepcionRequisicionBean.setRequisicionActual(null);
//        recepcionRequisicionBean.setConsecutivoRequisicion("");
//        recepcionRequisicionBean.setConsecutivoRequisicion("");
        //notas
        notaOrdenBean.setFiltrar(true);
        notaRequisicionBean.setFiltrar(true);
        //------------------------------------------------------------
        setAccionMenu(FacesUtilsBean.getRequestParameter("myParam"));
        //System.out.println("Ruta actual: " + getAccionMenu());

        // ---------------- opcion selecionada
        opcionVo = siOpcionImpl.buscarOpcion(getAccionMenu());
        requisicionBean.setTipoFiltro(Constantes.FILTRO);
        ordenBean.setTipoFiltro(Constantes.FILTRO);
        notaOrdenBean.traerNoticiaPorUsuario();
        //
        //usuarioBean.llenarRoles();
        ////-----Esto es para poner actualizar true y vaya por la lista
        //ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
        //contarBean.taerPendiente();
        return getAccionMenu();
    }

    public String cambiarPaginaPendiente(String pagina, int campoId) {
        //------------------------------------------------------------
        setAccionMenu(pagina);
        //
        usuarioImpl.cambiarCampoUsuario(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getId(), campoId);
        usuarioBean.setUsuarioConectado(usuarioImpl.find(usuarioBean.getUsuarioConectado().getId()));
        usuarioBean.setCompania(usuarioBean.getUsuarioConectado().getApCampo().getCompania());
        //

        return getAccionMenu() + ".xhtml?faces-redirect=true";
    }
    // Este metodo lo utilizo cuando uso botones y paso un solo parametro

    public void escuchaClickBoton() {
        //------------------------------------------------------------
        this.setAccionMenu(FacesUtilsBean.getParametroCadena());
        cambiaTituloPagina(this.getAccionMenu());
    }

    public String regresaSolicitarRequisicion() {
        RequisicionBean requisicionBean = (RequisicionBean) FacesUtilsBean.getManagedBean("requisicionBean");
        requisicionBean.setRequisicionActual(null);
        paginaActual = "Solicitar Requisiciones";
        return "crearRequisicion";
    }

    /**
     * Este metodo sirve para cambiar subpaginas de la aplicación. Paginas Como:
     * Solicitar requisicion, agregar ETS, modificar Ítem, Detalle Historial
     *
     * @param accion
     */
    public void procesarAccion(String accion) {
        cambiaTituloPagina(this.getAccionMenu());
    }

    public String cargarEts() {
        try {
            CargaEtsBean cargaEtsBean = (CargaEtsBean) FacesUtilsBean.getManagedBean("cargaEtsBean");
            cargaEtsBean.setOpcionSeleccionada("EtsRequisicion");
            setPaginaOrigen(FacesUtilsBean.getRequestParameter("paginaOrigen"));
            paginaDestino = FacesUtilsBean.getRequestParameter("paginaDestino");
            return getPaginaDestino();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            return "";
        }
    }

    public String cargarTablaComparativa() {
        try {
            CargaEtsBean cargaEtsBean = (CargaEtsBean) FacesUtilsBean.getManagedBean("cargaEtsBean");
            cargaEtsBean.setOpcionSeleccionada("agregarTablaComparativa");
            setPaginaOrigen(FacesUtilsBean.getRequestParameter("paginaOrigen"));
            paginaDestino = FacesUtilsBean.getRequestParameter("paginaDestino");
            cargaEtsBean.setIdCategoriaSelccionada(Constantes.OCS_CATEGORIA_TABLA);
            return getPaginaDestino();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            return "";
        }
    }

    public String cargarEts(String origen, String destino) {
        try {
            CargaEtsBean cargaEtsBean = (CargaEtsBean) FacesUtilsBean.getManagedBean("cargaEtsBean");
            cargaEtsBean.setOpcionSeleccionada("EtsRequisicion");
            paginaOrigen = origen;
            paginaDestino = destino;
            return getPaginaDestino();
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            return "";
        }
    }

    public String irCargarEtsOc() {
        setPaginaOrigen(FacesUtilsBean.getRequestParameter("paginaOrigen"));
        paginaDestino = FacesUtilsBean.getRequestParameter("paginaDestino");
        return "CargarEtsOc";
    }

    public String regresarPagina() {
        return getPaginaOrigen();
    }

    public void procesarAccion(String accion, String jsCode) {
        this.setAccionMenu(accion);
        cambiaTituloPagina(this.getAccionMenu());
        PrimeFaces.current().executeScript(jsCode);
    }

    /**
     * Determinar el titulo de la pagina donde se encuentra el usuario
     *
     * @param str cadena que entra de la opcion del menu seleccionado
     */
    private void cambiaTituloPagina(String str) {
        RequisicionBean requisicionBean = (RequisicionBean) FacesUtilsBean.getManagedBean("requisicionBean");
        switch (Comando.getOpcion(str)) {
            case crearRequisicion:
                this.paginaActual = "Solicitar Requisiciones";
                break;
            case catalogoServicios:
                this.paginaActual = "Catálogo de Servicios..";
                break;
            case serviciosAdicionales:
                this.paginaActual = "Servicios Adicionales..";
                break;
            case solicitarRequisicion:
                this.paginaActual = "Solicitando Requisición";
                break;
            case revisarRequisicion:
                this.paginaActual = "Revisar Requisiciones";
                break;
            case aprobarRequisicion:
                this.paginaActual = "Aprobar Requisiciones";
                break;
            case aceptarRequisicion:
                this.paginaActual = "Aceptar Requisiciones";
                break;
            case autorizarRequisicion:
                this.paginaActual = "Autorizar Requisiciones";
                break;
            case vistoBuenoRequisicion:
                this.paginaActual = "Visto Bueno de Requisiciones";
                break;
            case asignarRequisicion:
                this.paginaActual = "Asignar Requisiciones";
                break;
            case notaRequisicion:
                this.paginaActual = "Notas de las Requisiciones";
                break;
            case historialRequisicion:
                this.paginaActual = "Historial de Requisiciones";
                break;
            case detalleHistorial:
                this.paginaActual = "Detalle de la Requisición";
                break;
            case recepcionRequisicion:
                this.paginaActual = "Recepción Requisición";
                break;
            case tablaComparativa:
                this.paginaActual = "Tabla Comparativa";
                break;
            case ordenCompra:
                this.paginaActual = "Solicitar Orden";

                break;
            case notaOrden:
                this.paginaActual = "Notas Orden de Compra";

                break;
            case aprobarOrden:
                this.paginaActual = "Vo. Bo. Orden";

                break;
            case autorizaMpg:
                this.paginaActual = "Revisar Orden";

                break;
            case autorizaIhsa:
                this.paginaActual = "Aprobar Orden";
                break;
            case autorizaCompras:
                this.paginaActual = "Autorizar Orden";
                break;
            case autorizaTarea:
                this.paginaActual = "Enviar Orden";
                break;
            case autorizaTareaExcel:
                this.paginaActual = "Generar Excel";
                break;
            case autorizaFinanza:
                this.paginaActual = "Aprobar Orden ";
                break;
            case autorizaSocio:
                this.paginaActual = "Aprobar Orden ";
                break;
            case solicitarOrden:
                this.paginaActual = "Solicitar Orden de Compra";
                break;
            case detalleOrden:
                this.paginaActual = "Detalle Orden C/S";
                break;
            case listaConvenios:
                this.paginaActual = "Contratos";
                break;
            case ayuda:
                this.paginaActual = "Ayuda";
                break;
            case cargarETS:
                this.paginaActual = "Carga ETS";
                break;
            case historialOrden:
                this.paginaActual = "Historial Orden C/S";
                break;
            case cerrarSesion:
                requisicionBean.inicializar();
                break;
            case paginaPrincipal:
                this.paginaActual = "Página Principal";
                break;
            default:
                UtilLog4j.log.info(this, "error :" + str);
                break;
        }
    }

    /**
     * @return the paginaActual
     */
    public String getPaginaActual() {
        return paginaActual;
    }
}
