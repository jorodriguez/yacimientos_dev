/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.administracion.orden.bean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import sia.constantes.Constantes;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Orden;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.sgl.vo.EstatusAprobacionVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.StatusVO;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.SoporteProveedor;
import sia.util.SolicitudMaterialEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "transfereriTrabajoBean")
@ViewScoped
public class TransfereriTrabajoBean implements Serializable {

    /**
     * Creates a new instance of TransfereriTrabajoBean
     */
    public TransfereriTrabajoBean() {
    }

    @Inject
    private OrdenImpl ordenServicioRemoto;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenServicioRemoto;
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
    @Inject
    private SoporteProveedor soporteProveedor;
    @Inject
    private Sesion sesion;
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
    private String idSolicita;
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
    private String idAprobara;
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
    private List<SelectItem> estatus;

    /**
     * Creates a new instance of DevolverOrdenCompraModel
     */
    @PostConstruct
    public void iniciar() {
        tipoTrabajo = "ocs";
        estatus = new ArrayList<>();
        setIdCampo(sesion.getUsuarioVo().getIdCampo());
        listaEstatus();
    }

    public Orden buscarOrden(String consecutivo) {
        try {
            return this.ordenServicioRemoto.buscarPorConsecutivoEmpresa(consecutivo, sesion.getUsuarioVo().getRfcEmpresa());
        } catch (Exception e) {
            return null;
        }
    }

    public void listaEstatus() {
        List<StatusVO> ls = estatusImpl.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_ORDEN);
        for (StatusVO stVo : ls) {
            String valor = null;
            if (stVo.getIdStatus() > Constantes.ESTATUS_CANCELADA && stVo.getIdStatus() < Constantes.ESTATUS_AUTORIZADA) {
                switch (stVo.getIdStatus()) {
                    case Constantes.ESTATUS_PENDIENTE_R:
                        valor = "Sin Solicitar";
                        break;
                    case Constantes.ESTATUS_SOLICITADA_R:
                        valor = "Visto Bueno";
                        break;
                    case Constantes.ESTATUS_VISTO_BUENO_R:
                        valor = "Revisar";
                        break;
                    case Constantes.ESTATUS_REVISADA:
                        valor = "Aprobar";
                        break;
                    case Constantes.ESTATUS_APROBADA:
                        valor = "Autorizar";
                        break;
                    case Constantes.ESTATUS_POR_APROBAR_SOCIO:
                        valor = "Revision por Socio";
                        break;
                    case Constantes.ESTATUS_AUTORIZADA:
                        valor = "Por enviar a proveedor";
                        break;
                    default:
                        break;
                }
                estatus.add(new SelectItem(stVo.getIdStatus(), valor));
            }
        }
    }

    public AutorizacionesOrden buscarOrdenAutorizacion(int id) {
        try {
            return this.autorizacionesOrdenServicioRemoto.buscarPorOrden(id);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verificaUsuarioSolicita() {
        Usuario u = usuarioImpl.buscarPorNombre(getUsuarioSolicita());
        if (u != null) {
            return true;
        } else {
            return false;
        }
    }

    public void completarDevolucionOrden() {
        try {
            ordenServicioRemoto.devolverOrden(getOrden(), getUsuarioSolicita(), sesion.getUsuarioVo().getId(), getMotivo());

        } catch (Exception ex) {
            Logger.getLogger(TransfereriTrabajoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean reenviarOrden(Orden orden) throws Exception {
        Usuario u = usuarioImpl.buscarPorNombre(getUsuarioSolicita());
        return ordenServicioRemoto.autorizarOrdenCompras(orden, u);
    }

    public List<String> regresaUsuarioActivo(String cadenaDigitada) {
        List<UsuarioVO> usVos = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(idCampo);
        List<String> usNom = new ArrayList<>();
        usVos.stream().filter(u -> u.getNombre().toUpperCase().startsWith(cadenaDigitada.toUpperCase())).forEach(us -> {
            usNom.add(us.getNombre());
        });
        return usNom;
    }

    public void buscarUsuarioConsulta() {
        UsuarioVO usuaroiSel = buscarPorNombre(usuarioSolicita);
        if (usuaroiSel != null) {
            idSolicita = usuaroiSel.getId();
        } else {
            UtilLog4j.log.info(this, "No se encontro el usuario");
        }
    }

    public void buscarUsuarioAprueba() {
        UsuarioVO usuaroiSel = buscarPorNombre(usuarioAprobara);
        if (usuaroiSel != null) {
            idAprobara = usuaroiSel.getId();
        } else {
            UtilLog4j.log.info(this, "No se encontro el usuario");
        }
    }

    public UsuarioVO buscarPorNombre(String userName) {
        return usuarioImpl.findByName(userName);
    }

    public void cambiarTipoTrabajo() {
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

    public List<SelectItem> listaEstatusRequisicion() {
        List<SelectItem> le = new ArrayList<>();
        List<StatusVO> ls = estatusImpl.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_REQ);
        for (StatusVO statVo : ls) {
            String valor = null;
            if (statVo.getIdStatus() > Constantes.ESTATUS_CANCELADA && statVo.getIdStatus() < Constantes.ESTATUS_AUTORIZADA) {
                switch (statVo.getIdStatus()) {
                    case Constantes.REQUISICION_PENDIENTE:
                        valor = "Sin Solicitar";
                        break;
                    case Constantes.REQUISICION_SOLICITADA:
                        valor = "Revisar";
                        break;
                    case Constantes.REQUISICION_APROBADA:
                        valor = "Aprobar";
                        break;
                    default:
                        break;
                }
                le.add(new SelectItem(statVo.getIdStatus(), valor));
            }
        }
        return le;
    }

    public void buscarOrdenCompraServicio() {
        setLista(new ListDataModel(ordenServicioRemoto.traerOrdenComporaUsuarioEstatus(idSolicita, getIdStatus(), getIdCampo())));
    }

    public void buscarSolicitudesMaterial() {
        setLista(new ListDataModel(estadoAprobacionSolicitudLocal.traerSolicitudesUsuarioStatus(getIdCampo(), sesion.getUsuarioVo().getId(), getIdStatus())));
    }

    public boolean pasarSolicitudMaterial() {
        boolean exito;
        List<SolicitudMaterialAlmacenVo> lo = new ArrayList<>();
        for (Object object : getLista()) {
            SolicitudMaterialAlmacenVo o = (SolicitudMaterialAlmacenVo) object;
            if (o.isSelected()) {
                lo.add(o);
            }
        }
        exito = estadoAprobacionSolicitudLocal.pasarSolicitud(lo, idSolicita, idSolicita, sesion.getUsuarioVo(), getIdStatus(), getIdCampo());
        buscarSolicitudesMaterial();

        return exito;
    }

    public void buscarRequisiones() {
        List<RequisicionVO> lr = new ArrayList<>();
        switch (getIdStatus()) {
            case Constantes.REQUISICION_PENDIENTE: {
                List<Object[]> l = requisicionImpl.getRequisicionesSinSolicitar(idSolicita, getIdCampo());
                for (Object[] objects : l) {
                    lr.add(castReqSinSol(objects));
                }
                break;
            }
            case Constantes.REQUISICION_SOLICITADA: {
                List<Object[]> l = requisicionImpl.getRequisicionesSinRevisar(idSolicita, getIdCampo());
                for (Object[] objects : l) {
                    lr.add(castReq(objects));
                }
                break;
            }
            case Constantes.REQUISICION_REVISADA: {
                List<Object[]> l = requisicionImpl.getRequisicionesSinAprobar(idSolicita, getIdCampo());
                for (Object[] objects : l) {
                    lr.add(castReq(objects));
                }
                break;
            }
            default: {
                lr = requisicionImpl.getRequisicionesSinDisgregar(idSolicita, getIdCampo());
                break;
            }
        }
        setLista(new ListDataModel(lr));
    }

    public void buscarSolicitud() {
        List<EstatusAprobacionVO> le = new ArrayList<>();
        List<EstatusAprobacionVO> tmpList = sgEstatusAprobacionImpl.traerEstatusAprobacionPorUsuario(idSolicita, Constantes.ESTATUS_APROBAR, Constantes.SOLICITUDES_TERRESTRES);
        for (EstatusAprobacionVO estatusAprobacionVO : tmpList) {
            if (siManejoFechaLocal.compare(estatusAprobacionVO.getFechaSalida(), new Date()) >= 0) {
                le.add(estatusAprobacionVO);
            }
        }
        setLista(new ListDataModel(le));

    }

    private RequisicionVO castReqSinSol(Object[] objects) {
        RequisicionVO o = new RequisicionVO();
        o.setId((Integer) objects[0]);
        o.setRechazada((Boolean) objects[1]);
        o.setCompania((String) objects[2]);
//        o.setMontoPesos(((Double) objects[3]));
//        o.setMontoDolares((Double) objects[4]);
//        o.setMontoTotalDolares((Double) objects[5]);
        o.setIdUnidadCosto(objects[3] != null ? (Integer) objects[3] : 0);
        o.setTipo(objects[4] != null ? (String) objects[4] : "");
        o.setReferencia(objects[5] != null ? (String) objects[5] : "");
        if (o.getConsecutivo() == null || o.getConsecutivo().isEmpty()) {
            o.setConsecutivo("" + o.getId());
        }
        return o;
    }

    private RequisicionVO castReq(Object[] objects) {
        RequisicionVO o = new RequisicionVO();
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
        o.setSelected(true);
        return o;
    }

    public void pasarOrdenesCompra() {
        List<OrdenVO> lo = new ArrayList<>();
        for (Object object : getLista()) {
            OrdenVO o = (OrdenVO) object;
            if (o.isSelected()) {
                lo.add(o);
            }
        }
        ordenServicioRemoto.pasarOrdenesCompra(lo, getIdStatus(), idSolicita, idAprobara, sesion.getUsuarioVo().getId(),
                sesion.getUsuarioVo().getRfcEmpresa(), sesion.getUsuarioVo().getMail());
        setLista(new ListDataModel(ordenServicioRemoto.traerOrdenComporaUsuarioEstatus(idSolicita, getIdStatus(), getIdCampo())));
        //
        usuarioAprobara = "";
        usuarioSolicita = "";
        idSolicita = "";
        idAprobara = "";
    }

    public void pasarSolicitudes() {
        List<EstatusAprobacionVO> lo = new ArrayList<>();
        for (Object object : getLista()) {
            EstatusAprobacionVO o = (EstatusAprobacionVO) object;
            if (o.isSelected()) {
                lo.add(o);
            }
        }
        sgEstatusAprobacionImpl.pasarSolicitudes(lo, idSolicita, idSolicita, sesion.getUsuarioVo().getId(),
                sesion.getUsuarioVo().getRfcEmpresa(), sesion.getUsuarioVo().getMail(), getIdStatus());
        buscarSolicitud();
        usuarioAprobara = "";
        usuarioSolicita = "";
        idSolicita = "";
        idAprobara = "";

    }

    public void pasarRequisiciones() {
        List<RequisicionVO> lo = new ArrayList<>();
        for (Object object : getLista()) {
            RequisicionVO o = (RequisicionVO) object;
            if (o.isSelected()) {
                lo.add(o);
            }
        }
        requisicionImpl.pasarRequisiciones(lo, getIdStatus(), idSolicita, idAprobara, sesion.getUsuarioVo().getId(),
                sesion.getUsuarioVo().getRfcEmpresa(), sesion.getUsuarioVo().getMail());
        buscarRequisiones();
        //
        usuarioAprobara = "";
        usuarioSolicita = "";
        idSolicita = "";
        idAprobara = "";

    }

    public List<SelectItem> listaCampo() {
        List<SelectItem> l = new ArrayList<>();
        try {
            //lc = apCampoImpl.getAllField();
            listaCampo = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioVo().getId());
            for (CampoUsuarioPuestoVo ca : listaCampo) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }
}
