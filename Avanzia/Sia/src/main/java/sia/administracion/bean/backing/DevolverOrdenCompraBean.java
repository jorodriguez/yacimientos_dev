/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;




import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import sia.catalogos.bean.model.UsuarioListModel;
import sia.constantes.Constantes;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.modelo.Orden;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.requisicion.vo.RequisicionReporteVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.SolicitudMaterialEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class DevolverOrdenCompraBean implements Serializable {

    /**
     * Creates a new instance of DevolverOrdenCompraBean
     */
    public DevolverOrdenCompraBean() {
    }
    @Inject
    private Sesion sesion;

    @Inject
    private OrdenImpl ordenServicioRemoto;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private EstatusImpl estatusImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private RequisicionImpl requisicionImpl;
    @Inject
    private SgEstatusAprobacionImpl sgEstatusAprobacionImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private NotificacionRequisicionImpl notificacionRequisicionImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudLocal;
    //
    @Getter
    @Setter
    private Orden orden;
    @Getter
    @Setter
    private String consecutivo;
    @Getter
    @Setter
    private boolean mostrar = false;
    @Getter
    @Setter
    private boolean modal = false;
    @Getter
    @Setter
    private String motivo;
    @Getter
    @Setter
    private String usuarioSolicita;
    @Getter
    @Setter
    private String opcionUsuario = "compra";
    @Getter
    @Setter
    private List<SelectItem> listaSelect;
    @Getter
    @Setter
    private int idStatus;
    @Getter
    @Setter
    private DataModel lista;
    @Getter
    @Setter
    private String usuarioAprobara;
    @Getter
    @Setter
    private String tipoTrabajo = "ocs";
    @Getter
    @Setter
    private List<CampoUsuarioPuestoVo> listaCampo;
    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    List<Usuario> listaUsuarios;

    @PostConstruct
    public void iniciar() {
        listaUsuarios = new ArrayList<>();
        setIdCampo(sesion.getUsuarioVo().getIdCampo());
        listaUsuarios = usuarioImpl.getActivos();
    }

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeEvent) {
        Integer var = (Integer) valueChangeEvent.getNewValue();
        if (var != null) {
            setIdCampo(var);
            UtilLog4j.log.info(this, "campo: " + getIdCampo());
        }

    }

    public void buscarOrden() {
        setOrden(buscarOrden(getConsecutivo().trim()));
        if (getOrden() != null) {
            setMostrar(false);
        } else {
            setMostrar(true);
        }
    }

    public Orden buscarOrden(String consecutivo) {
        try {
            return this.ordenServicioRemoto.buscarPorConsecutivoEmpresa(consecutivo, sesion.getUsuarioVo().getRfcEmpresa());
        } catch (Exception e) {
            return null;
        }

    }

    public void devolverOrdenCompra() {
        setModal(true);
    }

    public List<String> usuarioListener(String texto) {
        String queryLowerCase = texto.toLowerCase();
        List<String> usuarioFiltrados = new ArrayList<>();
        List<Usuario> usuarios = listaUsuarios;
        for (Usuario users : usuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());
    }

    public void completarDevolucionOrden() {
        boolean v = false;
        try {
            if (verificaUsuarioSolicita()) {
                if (getMotivo().length() > 10) {
                    v = this.ordenServicioRemoto.devolverOrden(getOrden(), getUsuarioSolicita(), sesion.getUsuarioVo().getId(), getMotivo());
                    if (v) {
                        FacesUtils.addInfoMessage("Se devolvió la OC/S " + getOrden().getConsecutivo());
                        setOrden(null);
                        setUsuarioSolicita("");
                        //.toggleModal();
                    } else {
                        FacesUtils.addInfoMessage("Ocurrio un error . . . ");
                    }
                } else {
                    FacesUtils.addErrorMessage("Por favor escriba un motivo de más de 10 caracteres");
                }
            } else {
                FacesUtils.addErrorMessage("No se encontro el usuario en el SIA");
            }
        } catch (Exception ex) {
            Logger.getLogger(DevolverOrdenCompraBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addInfoMessage("Ocurrio un error. Por favor notifique el problema a: sia@ihsa.mx");
        }
    }

    private boolean verificaUsuarioSolicita() {
        Usuario u = usuarioImpl.buscarPorNombre(getUsuarioSolicita());
        if (u != null) {
            return true;
        } else {
            return false;
        }
    }

    public void buscarOrdenReenviar() {
        if (getConsecutivo().trim().isEmpty()) {
////////            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle(""));
            FacesUtils.addInfoMessage("Ingrese un consecutivo");
        } else {
            setOrden(buscarOrden(getConsecutivo()));
            if (getOrden() == null) {
                setOrden(null);
                FacesUtils.addInfoMessage("OC/S no encontrada.");
            } else {
            }
        }
    }

    public void reeviarOrden() {
        try {
            boolean v;
            v = ordenServicioRemoto.autorizarOrdenCompras(orden, usuarioImpl.buscarPorNombre(getUsuarioSolicita()));
            if (v) {
                setOrden(null);
                FacesUtils.addInfoMessage("Se envío la OC/S, favor de verificar.");
            } else {
                FacesUtils.addInfoMessage("No se realizó el envio de la  OC/S, por favor verifique los datos del proveedor.");
            }
        } catch (Exception ex) {
            Logger.getLogger(DevolverOrdenCompraBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addInfoMessage("No se realizó el envio de la  OC/S, por favor verifique los datos del proveedor.");
        }
    }

    /////////////////////////////////////
    public void cambiarTipoTrabajo(ValueChangeEvent event) {
        setTipoTrabajo((String) event.getNewValue());
        if (getTipoTrabajo().equals("ocs")) {
            setIdStatus(Constantes.ESTATUS_PENDIENTE_R);
        } else if (getTipoTrabajo().equals("req")) {
            setIdStatus(Constantes.REQUISICION_REVISADA);
        } else if (getTipoTrabajo().equals("inv")) {
            setIdStatus(SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId());
        } else {
            setIdStatus(Constantes.ESTATUS_APROBAR);
        }
        setLista(null);
        setUsuarioAprobara("");
        setUsuarioSolicita("");
    }

    public void enviarReporteCompradores() {
        if (enviarReporte()) {
            FacesUtils.addInfoMessage("Se enviaron los reportes a la gerencias de Compras");
        } else {
            FacesUtils.addInfoMessage("No se encontraron datos para el reporte de compra");
        }
    }

    private boolean enviarReporte() {
        boolean v = false;
        Calendar c = Calendar.getInstance();
        int mes = c.get(c.MONTH);
        int anio = c.get(c.YEAR);
        int exito = 0;
        for (CampoUsuarioPuestoVo apCampoVo : listaCampo) {
//            List<GerenciaVo> lger = ocURolGerenciaCampoLocal.traerGerenciaCompradores(sesion.getUsuarioVo().getId(), apCampoVo.getIdCampo(), Constantes.ROL_ADMIN_SIA);
//            for (GerenciaVo gerenciaVo : lger) {
            String sb = "Reporte de analistas de compras ( " + apCampoVo.getCampo() + " )";
            List<UsuarioRolVo> listaComprador = siUsuarioRolImpl.traerUsuarioPorRolModulo(Constantes.ROL_COMPRADOR, Constantes.MODULO_COMPRA, apCampoVo.getIdCampo());
            List<RequisicionReporteVO> listaNumeroComprador = new ArrayList<RequisicionReporteVO>();
            List<RequisicionReporteVO> lReporte = new ArrayList<RequisicionReporteVO>();
            for (UsuarioRolVo usuarioVO : listaComprador) {
                //Reporte detalle de req
                RequisicionReporteVO r = new RequisicionReporteVO();
                List<RequisicionVO> lr = requisicionImpl.listaRequisicionAsignadas(usuarioVO.getIdUsuario(), Constantes.ESTATUS_ASIGNADA, Constantes.DIAS_REPORTE_COMPRADORES, apCampoVo.getIdCampo());
                //Reporte totales
                if (lr.size() > 0) {
                    r.setComprador(usuarioVO.getUsuario());
                    r.setLRequisicion(lr);
                    lReporte.add(r);
                    //
                    int total = 0;
                    List<RequisicionVO> lrTemp = new ArrayList<RequisicionVO>();
                    RequisicionReporteVO rrvo = new RequisicionReporteVO();
                    rrvo.setComprador(usuarioVO.getUsuario());
                    for (int numMes = 0; numMes <= mes; numMes++) {
                        RequisicionVO req = new RequisicionVO();
                        long totalMes = requisicionImpl.totalRequisionesPorMes(usuarioVO.getIdUsuario(), apCampoVo.getIdCampo(), (numMes + 1), Constantes.DIAS_REPORTE_COMPRADORES, Constantes.ESTATUS_ASIGNADA, anio);

                        String m = Constantes.MESES[numMes];
                        if (totalMes > 0) {
                            req.setCadena(m);
                            req.setTotalItems(totalMes);
                            lrTemp.add(req);
                            total += totalMes;
                        }
                    }
                    rrvo.setLRequisicion(lrTemp);
                    //Anios anteriores al actual
                    String mens = "Anterior a " + anio;
                    rrvo.setCadena(mens);
                    rrvo.setTotalAnioAnteriores(requisicionImpl.totalRequisionesPendienteDesdeAniosAnterior(usuarioVO.getIdUsuario(), apCampoVo.getIdCampo(),
                            anio, Constantes.DIAS_REPORTE_COMPRADORES, Constantes.ESTATUS_ASIGNADA));
                    total += rrvo.getTotalAnioAnteriores();
                    rrvo.setTotalRequisiciones(total);
                    listaNumeroComprador.add(rrvo);
                }
                // } // fin del comprador
                //Se agrega a la lista del total de requisiones por mes y anio		
            }

            if (lReporte.size() > 0) {
                UsuarioResponsableGerenciaVo urgv = gerenciaImpl.traerResponsablePorApCampoYGerencia(apCampoVo.getIdCampo(), Constantes.GERENCIA_ID_COMPRAS);
                notificacionRequisicionImpl.envioReporteDiarioCompradores(urgv.getEmailUsuario(), "", "",
                        lReporte, sb, Constantes.DIAS_REPORTE_COMPRADORES, listaNumeroComprador);
                exito++;
            }

        }//Notifica requisiones por bloque
        if (exito > 0) {
            v = true;
        }
        return v;
    }

    //
}
