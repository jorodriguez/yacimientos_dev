/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.catalogos.bean.model.UsuarioListModel;
import sia.constantes.Constantes;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Orden;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.requisicion.vo.RequisicionReporteVO;
import sia.modelo.sgl.vo.EstatusAprobacionVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
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
import sia.sistema.bean.support.SoporteProveedor;

/**
 *
 * @author mluis
 */
@ManagedBean
@SessionScoped
public class DevolverOrdenCompraModel implements Serializable {

    @EJB
    private OrdenImpl ordenServicioRemoto;
    @EJB
    private AutorizacionesOrdenImpl autorizacionesOrdenServicioRemoto;
    @EJB
    private UsuarioImpl usuarioImpl;
    @EJB
    private EstatusImpl estatusImpl;
    @EJB
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @EJB
    private RequisicionImpl requisicionImpl;
    @EJB
    private SgEstatusAprobacionImpl sgEstatusAprobacionImpl;
    @EJB
    private SiManejoFechaImpl siManejoFechaLocal;
    @EJB
    private GerenciaImpl gerenciaImpl;
    @EJB
    private NotificacionRequisicionImpl notificacionRequisicionImpl;
    @EJB
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @EJB
    InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudLocal;
    //
    @ManagedProperty(value = "#{soporteProveedor}")
    private SoporteProveedor soporteProveedor;
    @ManagedProperty(value = "#{usuarioListModel}")
    private UsuarioListModel usuarioListModel;
    private Orden orden;
    private String consecutivo;
    private boolean mostrar = false;
    private boolean modal = false;
    private String motivo;
    private String usuarioSolicita;
    private String opcionUsuario = "compra";
    private List<SelectItem> listaSelect;
    private int idStatus;
    private DataModel lista;
    private String usuarioAprobara;
    private String tipoTrabajo = "ocs";
    private List<CampoUsuarioPuestoVo> listaCampo;
    private int idCampo;

    /**
     * Creates a new instance of DevolverOrdenCompraModel
     */
    public DevolverOrdenCompraModel() {
    }

    public void iniciar() {
        if (usuarioListModel.getUsuarioVO() != null) {
            setIdCampo(usuarioListModel.getUsuarioVO().getIdCampo());
        }
    }

    public Orden buscarOrden(String consecutivo) {
        try {
            return this.ordenServicioRemoto.buscarPorConsecutivoEmpresa(consecutivo, usuarioListModel.getUsuarioVO().getRfcEmpresa());
        } catch (Exception e) {
            return null;
        }

    }

    public AutorizacionesOrden buscarOrdenAutorizacion(int id) {
        try {
            return this.autorizacionesOrdenServicioRemoto.buscarPorOrden(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada) {//, -1, "nombre", true, null, false){
        return soporteProveedor.regresaUsuario(cadenaDigitada);
    }

    public boolean verificaUsuarioSolicita() {
        Usuario u = usuarioImpl.buscarPorNombre(getUsuarioSolicita());
        if (u != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean completarDevolucionOrden() throws Exception {
        boolean v = false;
        v = this.ordenServicioRemoto.devolverOrden(getOrden(), getUsuarioSolicita(), usuarioListModel.getUsuarioVO().getId(), getMotivo());
//        v = this.ordenServicioRemoto.devolucionOrden(, getOrden(), getMotivo(), getUsuarioSolicita());
        return v;
    }

    public boolean reenviarOrden(Orden orden) throws Exception {
        Usuario u = usuarioImpl.buscarPorNombre(getUsuarioSolicita());
        return ordenServicioRemoto.autorizarOrdenCompras(orden, u);
    }

    public List<SelectItem> listaEstatus() {
        List<SelectItem> le = new ArrayList<SelectItem>();
        List<StatusVO> ls = estatusImpl.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_ORDEN);
        for (StatusVO estatus : ls) {
            String valor = null;
            if (estatus.getIdStatus() > Constantes.ESTATUS_CANCELADA && estatus.getIdStatus() < Constantes.ESTATUS_AUTORIZADA) {
                switch (estatus.getIdStatus()) {
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
                le.add(new SelectItem(estatus.getIdStatus(), valor));
            }
        }
        return le;
    }

    public List<SelectItem> listaEstatusRequisicion() {
        List<SelectItem> le = new ArrayList<SelectItem>();
        List<StatusVO> ls = estatusImpl.traerPorTipo(Constantes.ESTATUS_COMPROBANTE_REQ);
        for (StatusVO estatus : ls) {
            String valor = null;
            if (estatus.getIdStatus() > Constantes.ESTATUS_CANCELADA && estatus.getIdStatus() < Constantes.ESTATUS_AUTORIZADA) {
                switch (estatus.getIdStatus()) {
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
                le.add(new SelectItem(estatus.getIdStatus(), valor));
            }
        }
        return le;
    }

    public String traerUsuarioJson() {
        return apCampoUsuarioRhPuestoImpl.traerUsuarioActivoPorBloque(getIdCampo(), Constantes.CERO);
    }

    public void buscarOrdenCompraServicio() {
        setLista(new ListDataModel(ordenServicioRemoto.traerOrdenComporaUsuarioEstatus(getUsuarioSolicita(), getIdStatus(), getIdCampo())));
    }

    public void buscarSolicitudesMaterial() {
        setLista(new ListDataModel(estadoAprobacionSolicitudLocal.traerSolicitudesUsuarioStatus(getIdCampo(), getUsuarioSolicita(), getIdStatus())));
    }

    public boolean pasarSolicitudMaterial() {
        boolean exito;
        List<SolicitudMaterialAlmacenVo> lo = new ArrayList<SolicitudMaterialAlmacenVo>();
        for (Object object : getLista()) {
            SolicitudMaterialAlmacenVo o = (SolicitudMaterialAlmacenVo) object;
            if (o.isSelected()) {
                lo.add(o);
            }
        }
        exito = estadoAprobacionSolicitudLocal.pasarSolicitud(lo, getUsuarioSolicita(), getUsuarioAprobara(), usuarioListModel.getUsuarioVO(), getIdStatus(), getIdCampo());
        buscarSolicitudesMaterial();

        return exito;
    }

    public void buscarRequisiones() {
        List<RequisicionVO> lr = new ArrayList<RequisicionVO>();
        switch (getIdStatus()) {
            case Constantes.REQUISICION_PENDIENTE: {
                List<Object[]> l = requisicionImpl.getRequisicionesSinSolicitar(getUsuarioSolicita(), getIdCampo());
                for (Object[] objects : l) {
                    lr.add(castReqSinSol(objects));
                }
                break;
            }
            case Constantes.REQUISICION_SOLICITADA: {
                List<Object[]> l = requisicionImpl.getRequisicionesSinRevisar(getUsuarioSolicita(), getIdCampo());
                for (Object[] objects : l) {
                    lr.add(castReq(objects));
                }
                break;
            }
            case Constantes.REQUISICION_REVISADA: {
                List<Object[]> l = requisicionImpl.getRequisicionesSinAprobar(getUsuarioSolicita(), getIdCampo());
                for (Object[] objects : l) {
                    lr.add(castReq(objects));
                }
                break;
            }
            default: {
                lr = requisicionImpl.getRequisicionesSinDisgregar(getUsuarioSolicita(), getIdCampo());
                break;
            }
        }
        setLista(new ListDataModel(lr));
    }

    public void buscarSolicitud() {
        List<EstatusAprobacionVO> le = new ArrayList<>();
        List<EstatusAprobacionVO> tmpList = sgEstatusAprobacionImpl.traerEstatusAprobacionPorUsuario(getUsuarioSolicita(), Constantes.ESTATUS_APROBAR, Constantes.SOLICITUDES_TERRESTRES);
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
        if(o.getConsecutivo() == null || o.getConsecutivo().isEmpty()){
            o.setConsecutivo(""+o.getId());
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

    public boolean pasarOrdenesCompra() {
        boolean exito;
        List<OrdenVO> lo = new ArrayList<OrdenVO>();
        for (Object object : getLista()) {
            OrdenVO o = (OrdenVO) object;
            if (o.isSelected()) {
                lo.add(o);
            }
        }
        exito = ordenServicioRemoto.pasarOrdenesCompra(lo, getIdStatus(), getUsuarioSolicita(), getUsuarioAprobara(), usuarioListModel.getUsuarioVO().getId(),
                usuarioListModel.getUsuarioVO().getRfcEmpresa(), usuarioListModel.getUsuarioVO().getMail());
        setLista(new ListDataModel(ordenServicioRemoto.traerOrdenComporaUsuarioEstatus(getUsuarioSolicita(), getIdStatus(), getIdCampo())));
        return exito;
    }

    public boolean pasarSolicitudes() {
        boolean exito;
        List<EstatusAprobacionVO> lo = new ArrayList<EstatusAprobacionVO>();
        for (Object object : getLista()) {
            EstatusAprobacionVO o = (EstatusAprobacionVO) object;
            if (o.isSelected()) {
                lo.add(o);
            }
        }
        exito = sgEstatusAprobacionImpl.pasarSolicitudes(lo, getUsuarioSolicita(), getUsuarioAprobara(), usuarioListModel.getUsuarioVO().getId(),
                usuarioListModel.getUsuarioVO().getRfcEmpresa(), usuarioListModel.getUsuarioVO().getMail(), getIdStatus());
        buscarSolicitud();

        return exito;
    }

    public boolean pasarRequisiciones() {
        boolean exito;
        List<RequisicionVO> lo = new ArrayList<RequisicionVO>();
        for (Object object : getLista()) {
            RequisicionVO o = (RequisicionVO) object;
            if (o.isSelected()) {
                lo.add(o);
            }
        }
        exito = requisicionImpl.pasarRequisiciones(lo, getIdStatus(), getUsuarioSolicita(), getUsuarioAprobara(), usuarioListModel.getUsuarioVO().getId(),
                usuarioListModel.getUsuarioVO().getRfcEmpresa(), usuarioListModel.getUsuarioVO().getMail());
        buscarRequisiones();

        return exito;
    }

    public List<SelectItem> listaCampo() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            //lc = apCampoImpl.getAllField();
            listaCampo = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(usuarioListModel.getUsuarioVO().getId());
            for (CampoUsuarioPuestoVo ca : listaCampo) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean enviarReporteCompradores() {
        boolean v = false;
        Calendar c = Calendar.getInstance();
        int mes = c.get(c.MONTH);
        int anio = c.get(c.YEAR);
        int exito = 0;
        for (CampoUsuarioPuestoVo apCampoVo : listaCampo) {
//            List<GerenciaVo> lger = ocURolGerenciaCampoLocal.traerGerenciaCompradores(usuarioListModel.getUsuarioVO().getId(), apCampoVo.getIdCampo(), Constantes.ROL_ADMIN_SIA);
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

    /**
     * @return the orden
     */
    public Orden getOrden() {
        return orden;
    }

    /**
     * @param orden the orden to set
     */
    public void setOrden(Orden orden) {
        this.orden = orden;
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
     * @return the modal
     */
    public boolean isModal() {
        return modal;
    }

    /**
     * @param modal the modal to set
     */
    public void setModal(boolean modal) {
        this.modal = modal;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the usuarioSolicita
     */
    public String getUsuarioSolicita() {
        return usuarioSolicita;
    }

    /**
     * @param usuarioSolicita the usuarioSolicita to set
     */
    public void setUsuarioSolicita(String usuarioSolicita) {
        this.usuarioSolicita = usuarioSolicita;
    }

    /**
     * @param soporteProveedor the soporteProveedor to set
     */
    public void setSoporteProveedor(SoporteProveedor soporteProveedor) {
        this.soporteProveedor = soporteProveedor;
    }

    /**
     * @return the listaSelect
     */
    public List<SelectItem> getListaSelect() {
        return listaSelect;
    }

    /**
     * @param listaSelect the listaSelect to set
     */
    public void setListaSelect(List<SelectItem> listaSelect) {
        this.listaSelect = listaSelect;
    }

    /**
     * @return the opcionUsuario
     */
    public String getOpcionUsuario() {
        return opcionUsuario;
    }

    /**
     * @param opcionUsuario the opcionUsuario to set
     */
    public void setOpcionUsuario(String opcionUsuario) {
        this.opcionUsuario = opcionUsuario;
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
     * @return the lista
     */
    public DataModel getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        this.lista = lista;
    }

    /**
     * @return the usuarioAprobara
     */
    public String getUsuarioAprobara() {
        return usuarioAprobara;
    }

    /**
     * @param usuarioAprobara the usuarioAprobara to set
     */
    public void setUsuarioAprobara(String usuarioAprobara) {
        this.usuarioAprobara = usuarioAprobara;
    }

    /**
     * @return the idTipoTrabajo
     */
    public String getTipoTrabajo() {
        return tipoTrabajo;
    }

    /**
     * @param idTipoTrabajo the idTipoTrabajo to set
     */
    public void setTipoTrabajo(String tipoTrabajo) {
        this.tipoTrabajo = tipoTrabajo;
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        this.idCampo = idCampo;
    }

    /**
     * @param usuarioListModel the usuarioListModel to set
     */
    public void setUsuarioListModel(UsuarioListModel usuarioListModel) {
        this.usuarioListModel = usuarioListModel;
    }

}
