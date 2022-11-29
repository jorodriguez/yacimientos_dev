/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.sistema.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campoVO.CampoOrden;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OcOrdenCoNoticiaImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.OcRequisicionCoNoticiaImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.util.FacturaEstadoEnum;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "contarBean")
@ViewScoped
public class ContarBean implements Serializable {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    public ContarBean() {
    }
    //
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private RequisicionImpl requisicionImpl;
    @Inject
    private OcOrdenCoNoticiaImpl ocOrdenCoNoticiaImpl;
    @Inject
    private OcRequisicionCoNoticiaImpl ocRequisicionCoNoticiaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    SiFacturaImpl siFacturaImpl;
    @Inject
    CvEvaluacionImpl cvEvaluacionImpl;
    ///
    @Inject
    private UsuarioBean usuarioBean;
///
    private List<CampoOrden> campoOrden;
    private List<CampoUsuarioPuestoVo> listaModulo;
    private Map<String, Long> mapaTotal;
    //

    //
    //REQUISISCIONES
    @PostConstruct
    public void iniciar() {
        if (usuarioBean.getUsuarioConectado() != null) {
            mapaTotal = new HashMap<>();
            taerPendiente();
            //
            llenarTotales();
        }
    }
//

    private void llenarTotales() {
        try {
            mapaTotal.put("reqsinSolicitar", requisicionImpl.getTotalRequisicionesSinSolicitar(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("reqsinRevisar", requisicionImpl.getTotalRequisicionesSinRevisar(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("reqsinAprobar", requisicionImpl.getTotalRequisicionesSinAprobar(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("reqsinVistoBueno", requisicionImpl.getTotalRequisicionesSinVistoBueno(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    TipoRequisicion.PS.name(),
                    Constantes.ROL_VISTO_BUENO_COSTO));
            mapaTotal.put("reqsinVoBoConta", requisicionImpl.getTotalRequisicionesSinVistoBueno(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    TipoRequisicion.AF.toString(), Constantes.ROL_VISTO_BUENO_CONTABILIDAD));
            mapaTotal.put("reqsinAsignar", requisicionImpl.getTotalRequisicionesSinAsignar(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    Constantes.REQUISICION_APROBADA,
                    Constantes.ROL_ASIGNA_REQUISICION));
            mapaTotal.put("reqSinDisgregar", requisicionImpl.getTotalRequisicionesSinDisgregar(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            //
            mapaTotal.put("ocsSinSolicitar", ordenImpl.totalOrdenesSinSolicitar(this.usuarioBean.getUsuarioConectado().getId(),
                    this.usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAprobar", ordenImpl.totalOrdenesSinAprobar(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAutorizarMPG", ordenImpl.totalOrdenesSinAutorizarMPG(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAutorizarIHSA", ordenImpl.getTotalOrdenesSinAutorizarIHSA(
                    usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAutorizaCompras", ordenImpl.getTotalOrdenesSinAutorizarCompras(
                    usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAutoComprasLicita", ordenImpl.getTotalOrdenesSinAutorizarComprasLicitacion(
                    usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAautoComprasTarea", ordenImpl.getTotalTareasSinCompleta(usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAutorizaComprasAF", ordenImpl.getTotalTareasSinCompletaAF(
                    usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAutorizaComprasPS", ordenImpl.getTotalTareasSinCompletaPS(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAutorizaFinanzas", ordenImpl.totalOrdenesSinAutorizarFinanzas(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinAutorizaSocio", ordenImpl.totalOrdenesSinAprobarSocio(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));
            mapaTotal.put("ocsSinRecibir", ordenImpl.totalOrdenesEstatusUsuario(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId(),
                    Constantes.ESTATUS_ENVIADA_PROVEEDOR));

            mapaTotal.put("notasInvitado", ocOrdenCoNoticiaImpl.totalNoticiaPorUsuario(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));

            mapaTotal.put("notaReqInv", ocRequisicionCoNoticiaImpl.totalNoticiaPorUsuario(
                    usuarioBean.getUsuarioConectado().getId(),
                    usuarioBean.getUsuarioConectado().getApCampo().getId()));

            if (null != usuarioBean.getMapaRoles().get("Revisa Contenido Nacional")) {
                mapaTotal.put("totalFacSinProc", siFacturaImpl.totalFacturaPorStatusCampo(FacturaEstadoEnum.ENVIADA_CLIENTE.getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO));
            }

            if (null != usuarioBean.getMapaRoles().get("Enviar Facturas CNH")) {
                mapaTotal.put("totalFacSinEnviar", siFacturaImpl.totalFacturaPorStatusCampo(FacturaEstadoEnum.PROCESO_DE_PAGO.getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO));
            }

            if (null != usuarioBean.getMapaRoles().get("Evaluador")) {
                mapaTotal.put("totalEvaluaciones", cvEvaluacionImpl.getTotalEvaluaciones(usuarioBean.getUsuarioConectado().getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId(), false));
            }

            if (null != usuarioBean.getMapaRoles().get("Historial Evaluaciones")) {
                mapaTotal.put("totalHistEvaluaciones", cvEvaluacionImpl.getTotalEvaluaciones(null,
                        usuarioBean.getUsuarioConectado().getApCampo().getId(), true));
            }

            if (null != usuarioBean.getMapaRoles().get("En Espera")) {
                mapaTotal.put("reqenEspera", requisicionImpl.getTotalRequisicionesEnEspera(
                        usuarioBean.getUsuarioConectado().getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId(),
                        Constantes.REQUISICION_EN_ESPERA,
                        Constantes.ROL_REQUISICION_ESPERA));
            }

            if (null != usuarioBean.getMapaRoles().get("Espera Adm")) {
                mapaTotal.put("reqenEsperaAdm", requisicionImpl.getTotalRequisicionesSinAsignar(
                        usuarioBean.getUsuarioConectado().getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId(),
                        Constantes.REQUISICION_EN_ESPERA,
                        Constantes.ROL_REQUISICION_ESPERA_ADM));
            }

            if (null != usuarioBean.getMapaRoles().get("Revisores REPSE")) {
                mapaTotal.put("totalPorRevREPSE", autorizacionesOrdenImpl.totalOrdenesStatusCampo(OrdenEstadoEnum.POR_REVISAR_REPSE.getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId()));
            }

        } catch (RuntimeException ex) {
            LOGGER.fatal(this, "Ocurrio un error al contar : : : : : " + ex.getMessage(), ex);
        }
    }

    public void llenarReqEnEspera() {
        mapaTotal.put("reqenEspera", requisicionImpl.getTotalRequisicionesEnEspera(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                Constantes.REQUISICION_EN_ESPERA,
                Constantes.ROL_REQUISICION_ESPERA));
    }

    public void llenarReqEnEsperaAdm() {
        mapaTotal.put("reqenEsperaAdm", requisicionImpl.getTotalRequisicionesSinAsignar(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                Constantes.REQUISICION_EN_ESPERA,
                Constantes.ROL_REQUISICION_ESPERA_ADM));
    }

    public void llenarReqSinSolicitar() {
        mapaTotal.put("reqsinSolicitar", requisicionImpl.getTotalRequisicionesSinSolicitar(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarReqSinRevisar() {
        mapaTotal.put("reqsinRevisar", requisicionImpl.getTotalRequisicionesSinRevisar(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarReqSinAprobar() {
        mapaTotal.put("reqsinAprobar", requisicionImpl.getTotalRequisicionesSinAprobar(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarReqSinVistoBueno() {
        mapaTotal.put("reqsinVistoBueno", requisicionImpl.getTotalRequisicionesSinVistoBueno(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                TipoRequisicion.PS.name(),
                Constantes.ROL_VISTO_BUENO_COSTO));
    }

    public void llenarReqSinVoBoConta() {
        mapaTotal.put("reqsinVoBoConta", requisicionImpl.getTotalRequisicionesSinVistoBueno(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                TipoRequisicion.AF.toString(), Constantes.ROL_VISTO_BUENO_CONTABILIDAD));
    }

    public void llenarReqSinAsignar() {
        System.out.println("@@@ llenarReqSinAsignar------");
        long nuevoContador = requisicionImpl.getTotalRequisicionesSinAsignar(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                Constantes.REQUISICION_APROBADA,
                Constantes.ROL_ASIGNA_REQUISICION);
        long contadorAnt =  mapaTotal.get("reqsinAsignar");
        System.out.println("@@Contador ante "+contadorAnt+" contador nuevo "+nuevoContador);
        mapaTotal.put("reqsinAsignar",nuevoContador );
    }

    public void llenarRecReq() {
        mapaTotal.put("reqSinDisgregar", requisicionImpl.getTotalRequisicionesSinDisgregar(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }
    // ordenes de compra

    public void llenarOcsSinSolicitar() {
        mapaTotal.put("ocsSinSolicitar", ordenImpl.totalOrdenesSinSolicitar(this.usuarioBean.getUsuarioConectado().getId(),
                this.usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAprobar() {
        mapaTotal.put("ocsSinAprobar", ordenImpl.totalOrdenesSinAprobar(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAutorizarMPG() {
        mapaTotal.put("ocsSinAutorizarMPG", ordenImpl.totalOrdenesSinAutorizarMPG(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAutorizarIHSA() {
        mapaTotal.put("ocsSinAutorizarIHSA", ordenImpl.getTotalOrdenesSinAutorizarIHSA(
                usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAutorizaCompras() {
        mapaTotal.put("ocsSinAutorizaCompras", ordenImpl.getTotalOrdenesSinAutorizarCompras(
                usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAutoComprasLicita() {
        mapaTotal.put("ocsSinAutoComprasLicita", ordenImpl.getTotalOrdenesSinAutorizarComprasLicitacion(
                usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAutoComprasAF() {
        mapaTotal.put("ocsSinAutorizaComprasAF", ordenImpl.getTotalTareasSinCompletaAF(
                usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAutoComprasPS() {
        mapaTotal.put("ocsSinAutorizaComprasPS", ordenImpl.getTotalTareasSinCompletaPS(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAutoFinanzas() {
        mapaTotal.put("ocsSinAutorizaFinanzas", ordenImpl.totalOrdenesSinAutorizarFinanzas(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinAutoSocio() {
        mapaTotal.put("ocsSinAutorizaSocio", ordenImpl.totalOrdenesSinAprobarSocio(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOcsSinRecibir() {
        mapaTotal.put("ocsSinRecibir", ordenImpl.totalOrdenesEstatusUsuario(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                Constantes.ESTATUS_ENVIADA_PROVEEDOR));
    }

    public void llenarOcsEnivarProveedor() {
        mapaTotal.put("ocsSinAautoComprasTarea", ordenImpl.getTotalTareasSinCompleta(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarEvaluador() {
        mapaTotal.put("totalEvaluaciones", cvEvaluacionImpl.getTotalEvaluaciones(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(), false));
    }

    public void llenarHistoEval() {
        mapaTotal.put("totalHistEvaluaciones", cvEvaluacionImpl.getTotalEvaluaciones(null,
                usuarioBean.getUsuarioConectado().getApCampo().getId(), true));
    }

    public void llenarNotasRequisicion() {
        mapaTotal.put("notaReqInv", ocRequisicionCoNoticiaImpl.totalNoticiaPorUsuario(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarNotasOrden() {
        mapaTotal.put("notasInvitado", ocOrdenCoNoticiaImpl.totalNoticiaPorUsuario(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void llenarOCSDevueltas() {
        mapaTotal.put("ocsSinAprobar", ordenImpl.totalOrdenesSinAprobar(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaTotal.put("ocsSinAutorizarMPG", ordenImpl.totalOrdenesSinAutorizarMPG(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaTotal.put("ocsSinAutorizarIHSA", ordenImpl.getTotalOrdenesSinAutorizarIHSA(
                usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaTotal.put("ocsSinAutorizaCompras", ordenImpl.getTotalOrdenesSinAutorizarCompras(
                usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaTotal.put("ocsSinAutoComprasLicita", ordenImpl.getTotalOrdenesSinAutorizarComprasLicitacion(
                usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaTotal.put("ocsSinAautoComprasTarea", ordenImpl.getTotalTareasSinCompleta(usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaTotal.put("ocsSinAutorizaFinanzas", ordenImpl.totalOrdenesSinAutorizarFinanzas(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
        mapaTotal.put("ocsSinAutorizaSocio", ordenImpl.totalOrdenesSinAprobarSocio(
                usuarioBean.getUsuarioConectado().getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId()));
    }

    public void taerPendiente() {
        setListaModulo(new ArrayList<>());
        if (usuarioBean.getUsuarioConectado() != null) {
            List<CampoUsuarioPuestoVo> cup = apCampoUsuarioRhPuestoImpl.traerCampoPorUsurioMenosActual(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId());
            List<CampoUsuarioPuestoVo> lc = new ArrayList<>();

            //prueba
            Map<Integer, List<SiOpcionVo>> contadores
                    = autorizacionesOrdenImpl.totalRevPagina(usuarioBean.getUsuarioConectado().getId());
            //prueba
            Map<Integer, List<SiOpcionVo>> contadoresReq
                    = requisicionImpl.totalPendienteCampos(usuarioBean.getUsuarioConectado().getId());

            for (CampoUsuarioPuestoVo campoUsuarioPuestoVo : cup) {
                campoUsuarioPuestoVo.setListaOpcion(new HashMap<>());
                //prueba                
                if (contadores != null && contadores.containsKey(campoUsuarioPuestoVo.getIdCampo())) {
                    campoUsuarioPuestoVo.getListaOpcion().put(Constantes.M_ORDEN, contadores.get(campoUsuarioPuestoVo.getIdCampo()));
                }
                //prueba
                if (contadoresReq != null && contadoresReq.containsKey(campoUsuarioPuestoVo.getIdCampo())) {
                    campoUsuarioPuestoVo.getListaOpcion().put(Constantes.M_REQUISICION, contadoresReq.get(campoUsuarioPuestoVo.getIdCampo()));
                }
                if (!campoUsuarioPuestoVo.getListaOpcion().isEmpty()) {
                    lc.add(campoUsuarioPuestoVo);
                }
            }
            getListaModulo().addAll(lc);
        }
    }

    public void llenarFacturaSinProcesar(String tipoFactura, int estatusID) {
        mapaTotal.put(tipoFactura, siFacturaImpl.totalFacturaPorStatusCampo(estatusID,
                usuarioBean.getUsuarioConectado().getApCampo().getId(), Constantes.CERO));
    }

    /**
     * @return the campoOrden
     */
    public List<CampoOrden> getCampoOrden() {
        return campoOrden;
    }

    /**
     * @param campoOrden the campoOrden to set
     */
    public void setCampoOrden(List<CampoOrden> campoOrden) {
        this.campoOrden = campoOrden;
    }

    /**
     * @return the listaModulo
     */
    public List<CampoUsuarioPuestoVo> getListaModulo() {
        return listaModulo;
    }

    /**
     * @param listaModulo the listaModulo to set
     */
    public void setListaModulo(List<CampoUsuarioPuestoVo> listaModulo) {
        this.listaModulo = listaModulo;
    }

    /**
     * @return the mapaTotal
     */
    public Map<String, Long> getMapaTotal() {
        return mapaTotal;
    }

    /**
     * @param mapaTotal the mapaTotal to set
     */
    public void setMapaTotal(Map<String, Long> mapaTotal) {
        this.mapaTotal = mapaTotal;
    }

}
