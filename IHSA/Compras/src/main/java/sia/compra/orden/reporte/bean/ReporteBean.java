/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.reporte.bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.shaded.json.JSONException;
import org.primefaces.shaded.json.JSONObject;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.metabase.impl.SiaMetabaseImpl;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OcFlujoImpl;
import sia.servicios.orden.impl.OrdenSiMovimientoImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.util.OrdenEstadoEnum;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "reporteBean")
@ViewScoped
public class ReporteBean implements Serializable {

    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    private RequisicionImpl requisicionImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private OcFlujoImpl ocFlujoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaImpl;
    @Inject
    private OrdenSiMovimientoImpl ordenSiMovimientoImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private SiaMetabaseImpl siaMetabaseImpl;
    //
    @Getter
    @Setter
    private LocalDate inicio;
    @Getter
    @Setter
    private LocalDate fin;
    private boolean autorizada = true;
    private List<?> lista = null;
    private List<?> listaR = null;
    private List<SelectItem> listaCombo = null;
    private int idRol;
    private String tipoRequisicion = "PS";
    private String estadoOrden = Constantes.ENVIADA_PROVEEDOR;
    private int idStatus;
    private int idGerencia;
    private int idProveedor;
    private String idUsuario;
    private String nombreUsuario;
    private String panelSeleccion = "TODO";
    private String conContrato = "Si";
    private String jsonProveedores = "";
    private int diasAnticipados = 0;
    private int idMoneda = 1;
    private int idGerenciaCompra;
    private int indiceTab = 1;
    private int opcioSeleccionada = 1;
    //
    private String tituloTabla;
    @Getter
    @Setter
    private List<SelectItem> listaGerencias = null;
    @Getter
    @Setter
    private List<SelectItem> listaEstatus;
    @Getter
    @Setter
    private List<SelectItem> listaMoneda;
    @Getter
    @Setter
    private List<SelectItem> listaRevisan;
    @Inject
    private UsuarioBean usuarioBean;

    @PostConstruct
    public void init() {
        if (usuarioBean.getUsuarioConectado() != null) {
            idMoneda = usuarioBean.getUsuarioConectado().getApCampo().getCompania().getMoneda().getId();
        }
        fin = LocalDate.now();
        inicio = fin.minusDays(30);
        listaGerencias = new ArrayList<>();
        listaEstatus = new ArrayList<>();
        listaMoneda = new ArrayList<>();
        listaRevisan = new ArrayList<>();

    }

    private void llenarGerencias() {
        List<GerenciaVo> geres = gerenciaImpl.getAllGerenciaByApCompaniaAndApCampo(usuarioBean.getCompania().getRfc(), usuarioBean.getUsuarioConectado().getApCampo().getId(), "nombre", true, true, false);
        //gerenciaImpl.traerGerenciaAbreviatura(usuarioBean.getUsuarioConectado().getApCampo().getId());
        geres.stream().forEach(ger -> {
            listaGerencias.add(new SelectItem(ger.getId(), ger.getNombre()));
        });
        panelSeleccion = "TODO";
    }

    public void onTabChange(TabChangeEvent event) {
        switch (event.getTab().getTitle()) {
            case "OC/S por Status":
                idStatus = OrdenEstadoEnum.POR_VOBO.getId();
                llenarEstatus();
                llenarRevisa();
                break;
            case "OC/S de gerencias":
                llenarGerencias();
                break;
            case "Proveedores":
                llenarMoneda();
                break;
            case "Ordenes por proveedor":
                panelSeleccion = "TODOS";
                break;
            case "Estadística":
                indiceTab = 1;
                break;
            default:
                break;
        }
        limpiarLista();
    }

    public void limpiarLista() {
        setLista(null);
        setListaR(null);
    }

    public void limpiarListaEntregada() {
        inicio = LocalDate.now();
        setLista(null);
        setListaR(null);
        setOpcioSeleccionada(1);
    }

    public void actualizarGerenciaCompra() {
        // setIdGerenciaCopra((Integer) event.getNewValue());
    }

    public void traerDatosComprador() {
        List<OrdenVO> lo = autorizacionesOrdenImpl.traerOrdenComprador(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), Constantes.BOOLEAN_FALSE, usuarioBean.getUsuarioConectado().getApCampo().getId());
        JSONObject j = new JSONObject();
        String json;
        List<String> u = new ArrayList<>();
        List<Long> total = new ArrayList<>();
        List<Double> totalDolar = new ArrayList<>();
        if (lo != null) {
            for (OrdenVO ordenVO : lo) {
                u.add(ordenVO.getAnalista());
                total.add(ordenVO.getTotalOrdenes());
                totalDolar.add(ordenVO.getTotalUsd());
            }
            lista = lo;
        }
        //
        j.put("Comprador", u);
        j.put("total", total);
        j.put("totalDolar", totalDolar);
        json = j.toString();
        //      
        PrimeFaces.current().executeScript(";llenarDatosCompradores(" + json + ",'" + getInicio() + "','" + getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "'," + isAutorizada() + ");");
    }

    public void buscarRequicion() {
        List<RequisicionVO> lo = requisicionImpl.requisicionesPorEstatus(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), getTipoRequisicion(), Constantes.REQUISICION_VISTO_BUENO_C);
        setListaR(lo);

    }

    public void traerRequisicionesFinanzas() {
        List<RequisicionVO> lo = requisicionImpl.requisicionesPorEstatus(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), getTipoRequisicion(), Constantes.REQUISICION_VISTO_BUENO_C);
        setListaR(lo);
        //
    }

    public void traerRequisicionesSinOrden() {
        List<RequisicionVO> lo = requisicionImpl.totalReqOcsSinProcesar(usuarioBean.getUsuarioConectado().getApCampo().getId(), getDiasAnticipados());
        JSONObject j = new JSONObject();
        String json;
        List<String> u = new ArrayList<>();
        List<Long> total = new ArrayList<>();
        List<Long> totalOCS = new ArrayList<>();
        if (lo != null) {
            for (RequisicionVO rVO : lo) {
                u.add(rVO.getComprador());
                total.add(rVO.getTotal());
                totalOCS.add(rVO.getTotalItems());
            }
        }
        //
        j.put("Comprador", u);
        j.put("total", total);
        j.put("totalOcs", totalOCS);
        json = j.toString();
        //
        PrimeFaces.current().executeScript(";llenarRequiscionesCompradores(" + json + "," + getDiasAnticipados() + ");");
    }

    public void traerRequisionesPorComprador() {
        UsuarioVO uvo = usuarioImpl.findByName(getNombreUsuario());
        String userGrafica = "";
        if (uvo == null) {
            userGrafica = usuarioImpl.buscarPorNombre(getNombreUsuario()).getId();
        } else {
            userGrafica = uvo.getId();
        }
        List<RequisicionVO> lo = requisicionImpl.traerRequisicionSinSolicitarPorUsuaario(usuarioBean.getUsuarioConectado().getApCampo().getId(), getDiasAnticipados(), userGrafica);

        listaR = lo;
    }

    public void traerOrdeneSinSolicitarPorComprador() {
        UsuarioVO uvo = usuarioImpl.findByName(getNombreUsuario());
        String userGrafica = "";
        if (uvo == null) {
            userGrafica = usuarioImpl.buscarPorNombre(getNombreUsuario()).getId();
        } else {
            userGrafica = uvo.getId();
        }
        List<RequisicionVO> lo = autorizacionesOrdenImpl.traerOrdenPorEstatus(Constantes.ORDENES_SIN_SOLICITAR, usuarioBean.getUsuarioConectado().getApCampo().getId(), userGrafica);
        listaR = lo;
    }

    public void traerOCSGerencia() {
        if (getPanelSeleccion().equals("TODO")) {
            List<OrdenVO> lo = autorizacionesOrdenImpl.traerOrdenGerencia(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), !isAutorizada(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.ORDENES_SIN_APROBAR);
            JSONObject j = new JSONObject();
            String json;
            List<String> u = new ArrayList<>();
            List<Long> total = new ArrayList<>();
            List<Double> totalDolar = new ArrayList<>();
            if (lo != null) {
                for (OrdenVO ordenVO : lo) {
                    u.add(ordenVO.getGerencia());
                    total.add(ordenVO.getTotalOrdenes());
                    totalDolar.add(ordenVO.getTotalUsd());
                }
                //
                j.put("Gerencia", u);
                j.put("total", total);
                j.put("totalDolar", totalDolar);
                json = j.toString();
                PrimeFaces.current().executeScript(";llenarDatosOCSGerencia(" + json + ",'" + getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "','" + getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "'," + isAutorizada() + ");");
                lista = lo;
            }

        } else { // por gerencia
            lista = autorizacionesOrdenImpl.traerOrdenPorGerencia(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), !isAutorizada(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.ORDENES_SIN_APROBAR, getIdGerencia());
        }
    }

    public void llenarEstatus() {
        listaEstatus = new ArrayList<>();
        listaEstatus.add(new SelectItem(OrdenEstadoEnum.POR_VOBO.getId(), "Visto bueno"));
        listaEstatus.add(new SelectItem(OrdenEstadoEnum.POR_REVISAR.getId(), "Revisar"));
        listaEstatus.add(new SelectItem(OrdenEstadoEnum.POR_APROBAR_SOCIO.getId(), "Aprobar externo"));
        listaEstatus.add(new SelectItem(OrdenEstadoEnum.POR_APROBAR.getId(), "Aprobar"));
        listaEstatus.add(new SelectItem(OrdenEstadoEnum.POR_AUTORIZAR.getId(), "Autorizar"));
        listaEstatus.add(new SelectItem(OrdenEstadoEnum.POR_AUTORIZAR_1MMD.getId(), "Autorizar OC/S por Monto"));
        listaEstatus.add(new SelectItem(OrdenEstadoEnum.POR_ACEPTAR_CARTA_INTENCION.getId(), "Carta de intención Enviadas"));
        listaEstatus.add(new SelectItem(OrdenEstadoEnum.POR_REVISAR_REPSE.getId(), "En revisión por Jurídico"));
    }

    public void traerOCSPorGerencia() {
        List<OrdenVO> lo = autorizacionesOrdenImpl.traerOrdenPorGerencia(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), isAutorizada(), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.ORDENES_SIN_APROBAR, getIdGerencia());
        lista = lo;
    }

    public void traerOCSProveedor() {
        List<OrdenVO> lo = null;
        if (getPanelSeleccion().equals("TODOS")) {
            lo = autorizacionesOrdenImpl.traerOrdenPorProveedor(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.ESTATUS_AUTORIZADA);
        } else {
            if (getEstadoOrden().equals(Constantes.ENVIADA_PROVEEDOR)) {
                lo = autorizacionesOrdenImpl.traerOrdenPorProveedor(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.ESTATUS_AUTORIZADA, getIdProveedor(), getEstadoOrden());
            } else {
                lo = autorizacionesOrdenImpl.traerOrdenPorProveedor(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.ORDENES_SIN_APROBAR, getIdProveedor(), getEstadoOrden());
            }
        }
        lista = lo;
    }

    public void traerOCSProveedorContrato() {
        List<OrdenVO> lo = autorizacionesOrdenImpl.traerOrdenPorProveedorContrato(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), usuarioBean.getUsuarioConectado().getApCampo().getId(), getConContrato(), Constantes.ORDENES_SIN_SOLICITAR, Constantes.OCS_PROCESO, getIdMoneda());

        lista = lo;
    }

    public void llenarMoneda() {
        listaMoneda = new ArrayList<>();
        for (MonedaVO mo : monedaImpl.traerMonedaActiva(usuarioBean.getUsuarioConectado().getApCampo().getId())) {
            SelectItem i = new SelectItem(mo.getId(), mo.getNombre());
            listaMoneda.add(i);
        }
    }

    public void traerOCSMovimiento() {
        List<OrdenVO> lo = ordenSiMovimientoImpl.ordenesRechadas(getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getIdStatus(), usuarioBean.getUsuarioConectado().getApCampo().getId());
        lista = lo;
    }

    public void traerOCSSolDev() {
        try {
            List<OrdenVO> lo = autorizacionesOrdenImpl.traerSolDevCan(Constantes.ID_SI_OPERACION_DEVOLVER, Constantes.ID_SI_OPERACION_CANCELAR, usuarioBean.getUsuarioConectado().getApCampo().getId(), getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            JSONObject j = new JSONObject();
            String json;
            List<String> u = new ArrayList<>();
            List<Long> totalSol = new ArrayList<>();
            List<Long> totalDev = new ArrayList<>();
            List<Long> totalCan = new ArrayList<>();
            if (lo != null) {
                for (OrdenVO ordenVO : lo) {
                    u.add(ordenVO.getAnalista());
                    totalSol.add(ordenVO.getTotalOrdenes());
                    totalDev.add(ordenVO.getTotalDevueltas());
                    totalCan.add(ordenVO.getTotalCanceladas());
                }
            }
            //
            j.put("analista", u);
            j.put("totalSol", totalSol);
            j.put("totalDev", totalDev);
            j.put("totalCan", totalCan);
            json = j.toString();
//            System.out.println("Cad : sol dev can : : " + json.toString());
            PrimeFaces.current().executeScript(";graficaOCSSolDevCan(" + json + ",'" + getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "','" + getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "');");
            indiceTab = 0;
        } catch (JSONException ex) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepción en las OC/S dev y canceladas : : : : : : : " + ex.getMessage());
        }
    }

    public void traerOCSComprador() {
        UsuarioVO uvo = usuarioImpl.findByName(getNombreUsuario());
        switch (indiceTab) {
            case 1:
                lista = autorizacionesOrdenImpl.traerOrdenSolicitadaPorUsuario(usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.ORDENES_SIN_SOLICITAR, uvo.getId(), getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                break;
            case 2:
                lista = ordenSiMovimientoImpl.ordenesPorUsuario(Constantes.ID_SI_OPERACION_DEVOLVER, usuarioBean.getUsuarioConectado().getApCampo().getId(), uvo.getId(), getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                break;
            case 3:
                lista = ordenSiMovimientoImpl.ordenesPorUsuario(Constantes.ID_SI_OPERACION_CANCELAR, usuarioBean.getUsuarioConectado().getApCampo().getId(), uvo.getId(), getInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString());
                break;
            default:
                break;
        }
    }

    public void llenarRevisa() {
        listaRevisan = new ArrayList<>();
        switch (getIdStatus()) {
            case Constantes.ORDENES_SIN_APROBAR:
                UsuarioResponsableGerenciaVo object = gerenciaImpl.traerResponsablePorApCampoYGerencia(usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.GERENCIA_ID_COMPRAS);
                listaRevisan.add(new SelectItem(object.getIdUsuario(), object.getNombreUsuario()));
                break;
            case Constantes.ORDENES_SIN_AUTORIZAR_MPG:
                List<ApCampoGerenciaVo> lg = apCampoGerenciaImpl.listaGerentes(usuarioBean.getUsuarioConectado().getApCampo().getId());
                for (ApCampoGerenciaVo cg : lg) {
                    listaRevisan.add(new SelectItem(cg.getIdResponsable(), cg.getNombreResponsable()));
                }
                break;
            case Constantes.ORDENES_SIN_AUTORIZAR_IHSA:
                List<UsuarioTipoVo> lt = ocFlujoImpl.getUsuariosPorAccion("AP", usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.NO_ELIMINADO);
                for (UsuarioTipoVo ut : lt) {
                    listaRevisan.add(new SelectItem(ut.getIdUser(), ut.getUsuario()));
                }
                break;
            case Constantes.ESTATUS_POR_APROBAR_SOCIO:
                listaRevisan.add(new SelectItem("Externo", "Externo"));
                break;
            case Constantes.ORDENES_SIN_AUTORIZAR_COMPRAS:
                UsuarioResponsableGerenciaVo ug = gerenciaImpl.traerResponsablePorApCampoYGerencia(usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.ID_GERENCIA_IHSA);
                listaRevisan.add(new SelectItem(ug.getIdUsuario(), ug.getNombreUsuario()));
                break;
            case Constantes.ORDENES_SIN_AUTORIZAR_LICITACION:
                List<UsuarioRolVo> lRol = siUsuarioRolImpl.traerRolPorCodigo(Constantes.CODIGO_ROL_LICITACION, usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.MODULO_COMPRA);
                if (lRol != null) {
                    for (UsuarioRolVo ut : lRol) {
                        listaRevisan.add(new SelectItem(ut.getIdUsuario(), ut.getUsuario()));
                    }
                }
                break;
            default:
                 listaRevisan.add(new SelectItem("", "No hay usuario"));
                break;
        }
    }

    public void traerOCSEstado() {
        List<OrdenVO> lo = autorizacionesOrdenImpl.traerOrdenPorStatusUsuario(usuarioBean.getUsuarioConectado().getApCampo().getId(), getIdStatus(), getIdUsuario(), usuarioBean.getUsuarioConectado().getId());
        lista = lo;
    }

    public void llenarProveedor() {
        jsonProveedores = this.proveedorImpl.getProveedorJson(usuarioBean.getCompania().getRfc(), ProveedorEnum.ACTIVO.getId());
        PrimeFaces.current().executeScript(";llenarProveedor(" + "'frmReporteOCSPorProveedor'," + jsonProveedores + ");");
    }

    public void traerRequisicionesProceso() {
        listaR = requisicionImpl.traerPorRangoEstado(usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.REQUISICION_SOLICITADA, Constantes.REQUISICION_VISTO_BUENO);
    }
// Reporte de ocs entregadsa

    public void buscarOcsEntregadas() {
        try {
            lista = autorizacionesOrdenImpl.ordenesPorFechaEntrega(usuarioBean.getUsuarioConectado().getApCampo().getId(), inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), getOpcioSeleccionada());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepción en las OC/S dev y canceladas : : : : : : : " + ex.getMessage());
        }
    }

    public String getMetaBaseReport() {
        String ret = "";
        try {
            String METABASE_SECRET_KEY = "1d4abeba80e9226dd6154885af5f213e8b13eb66e903e31376ee882cfe1a147f";
            ret = siaMetabaseImpl.getTokenUrl(35, METABASE_SECRET_KEY);
        } catch (Exception ex) {
            Logger.getLogger(ReporteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public String getMetaBaseReportMontoPorAlmacen() {
        String ret = "";
        try {
            String METABASE_SECRET_KEY = "1d4abeba80e9226dd6154885af5f213e8b13eb66e903e31376ee882cfe1a147f";
            ret = siaMetabaseImpl.getTokenUrl(59, METABASE_SECRET_KEY);
        } catch (Exception ex) {
            Logger.getLogger(ReporteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public String getMetaBaseReportMontoMovGerencia() {
        String ret = "";
        try {
            String METABASE_SECRET_KEY = "1d4abeba80e9226dd6154885af5f213e8b13eb66e903e31376ee882cfe1a147f";
            ret = siaMetabaseImpl.getTokenUrlDash(8, METABASE_SECRET_KEY);
        } catch (Exception ex) {
            Logger.getLogger(ReporteBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

//
//    public String getMetabaseEmbeddedUrl(String metabaseSecretKey, Map<String, Object> payload, String metabaseUrl) {
//
//        // Need to encode the secret key 
//        String metaBaseEncodedSecretKey = Base64.getEncoder().encodeToString(metabaseSecretKey.getBytes());
//        String jwtToken = Jwts.builder()
//                .setHeaderParam("typ", "JWT")
//                .setClaims(payload)
//                .signWith(SignatureAlgorithm.HS256, metaBaseEncodedSecretKey)
//                .setIssuedAt(new Date())
//                .compact();
//        return metabaseUrl + "/embed/dashboard/" + jwtToken+ "#bordered=true&titled=true";
//    }
    /**
     * @return the inicio
     */
    /**
     * @return the autorizada
     */
    public boolean isAutorizada() {
        return autorizada;
    }

    /**
     * @param autorizada the autorizada to set
     */
    public void setAutorizada(boolean autorizada) {
        this.autorizada = autorizada;
    }

    /**
     * @return the lista
     */
    public List<?> getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(List<?> lista) {
        this.lista = lista;
    }

    /**
     * @return the idRol
     */
    public int getIdRol() {
        return idRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /**
     * @return the tipoRequisicion
     */
    public String getTipoRequisicion() {
        return tipoRequisicion;
    }

    /**
     * @param tipoRequisicion the tipoRequisicion to set
     */
    public void setTipoRequisicion(String tipoRequisicion) {
        this.tipoRequisicion = tipoRequisicion;
    }

    /**
     * @return the listaR
     */
    public List<?> getListaR() {
        return listaR;
    }

    /**
     * @param listaR the listaR to set
     */
    public void setListaR(List<?> listaR) {
        this.listaR = listaR;
    }

    /**
     * @return the idStatus
     */
    public int getIdStatus() {
        return idStatus;
    }

    /**
     * @param idStatus the idStatus to set
     */
    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * @return the estadoOrden
     */
    public String getEstadoOrden() {
        return estadoOrden;
    }

    /**
     * @param estadoOrden the estadoOrden to set
     */
    public void setEstadoOrden(String estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    /**
     * @return the idUsuario
     */
    public String getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * @return the nombreUsuario
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * @param nombreUsuario the nombreUsuario to set
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * @return the panelSeleccion
     */
    public String getPanelSeleccion() {
        return panelSeleccion;
    }

    /**
     * @param panelSeleccion the panelSeleccion to set
     */
    public void setPanelSeleccion(String panelSeleccion) {
        this.panelSeleccion = panelSeleccion;
    }

    /**
     * @return the diasAnticipados
     */
    public int getDiasAnticipados() {
        return diasAnticipados;
    }

    /**
     * @param diasAnticipados the diasAnticipados to set
     */
    public void setDiasAnticipados(int diasAnticipados) {
        this.diasAnticipados = diasAnticipados;
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
     * @return the idGerenciaCopra
     */
    public int getIdGerenciaCopra() {
        return idGerenciaCompra;
    }

    /**
     * @param idGerenciaCopra the idGerenciaCopra to set
     */
    public void setIdGerenciaCopra(int idGerenciaCopra) {
        this.idGerenciaCompra = idGerenciaCopra;
    }

    /**
     * @return the listaCombo
     */
    public List<SelectItem> getListaCombo() {
        return listaCombo;
    }

    /**
     * @param listaCombo the listaCombo to set
     */
    public void setListaCombo(List<SelectItem> listaCombo) {
        this.listaCombo = listaCombo;
    }

    /*
     * @return the indiceTab
     */
    public int getIndiceTab() {
        return indiceTab;
    }

    /**
     * @param indiceTab the indiceTab to set
     */
    public void setIndiceTab(int indiceTab) {
        this.indiceTab = indiceTab;
    }

    /**
     * @return the conContrato
     */
    public String getConContrato() {
        return conContrato;
    }

    /**
     * @param conContrato the conContrato to set
     */
    public void setConContrato(String conContrato) {
        this.conContrato = conContrato;
    }

    /**
     * @return the tituloTabla
     */
    public String getTituloTabla() {
        return tituloTabla;
    }

    /**
     * @param tituloTabla the tituloTabla to set
     */
    public void setTituloTabla(String tituloTabla) {
        this.tituloTabla = tituloTabla;
    }

    /**
     * @return the opcioSeleccionada
     */
    public int getOpcioSeleccionada() {
        return opcioSeleccionada;
    }

    /**
     * @param opcioSeleccionada the opcioSeleccionada to set
     */
    public void setOpcioSeleccionada(int opcioSeleccionada) {
        this.opcioSeleccionada = opcioSeleccionada;
    }

}
