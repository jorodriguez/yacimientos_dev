/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.estancia.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.sgl.estancia.vo.DetalleEstanciaVO;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.vo.MotivoVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgMotivoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;

/**
 *
 * @author mluis
 */
@Named(value = "solicitarEstanciaBean")
@ViewScoped
public class SolicitarEstanciaBean implements Serializable {

    public SolicitarEstanciaBean() {
    }
    @Inject
    private Sesion sesion;
    @Inject
    GerenciaImpl gerenciaImpl;
    @Inject
    SgOficinaImpl sgOficinaImpl;
    @Inject
    SgMotivoImpl sgMotivoImpl;
    @Inject
    SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private SgInvitadoImpl sgInvitadoImpl;
    private int idGerencia;
    private Map<String, List<SelectItem>> mapaCombos;
    private Date fechaIngreso;
    private Date fechaSalida;
    private int idOficina;
    private int idMotivo;
    private String user;
    private int idInvitado;
    private String idEmpleado;
    private String invitado;
    private List<DetalleEstanciaVO> listaDetalleEstancia;
    private DetalleEstanciaVO detalleSolicitudVO;
    private String usuarioJson;
    private String invitadoJson;
    private String observacion;

    @PostConstruct
    public void iniciar() {
        listaDetalleEstancia = new ArrayList<DetalleEstanciaVO>();
        if (sesion.getUsuario().getGerencia() != null) {
            idGerencia = sesion.getUsuario().getGerencia().getId();
        }
         DetalleEstanciaVO detalleSolVO = new DetalleEstanciaVO();
        detalleSolVO.setUsuario(sesion.getUsuario().getNombre());
        detalleSolVO.setInvitado("");
        detalleSolVO.setIdTipoEspecifico(Constantes.EMPLEADO);
        detalleSolVO.setTipoDetalle("Empleado");
        detalleSolVO.setIdUsuario(sesion.getUsuario().getId());
        detalleSolVO.setIdInvitado(Constantes.CERO);
        //
        listaDetalleEstancia.add(detalleSolVO);
        fechaIngreso = new Date();
//        System.out.println("! una vez ");
        mapaCombos = new HashMap<String, List<SelectItem>>();
        getMapaCombos().put("gerencias", listaGerencia());
        getMapaCombos().put("oficinas", trearListaOficina());
        getMapaCombos().put("motivos", listaMotivo());
        //
        //
        usuariosJson();
        PrimeFaces.current().executeScript(";jsonUsuario('frmSolEstancia', 'autocompleteUsuario', " + usuarioJson + " , 'hidenUsuario');");
        invitadoJson();
        PrimeFaces.current().executeScript(";jsonInvitado('frmSolEstancia', 'autocompleteInvitado', " + invitadoJson + " , 'hidenInvitado');");
//      
    }

    private void usuariosJson() {
        usuarioJson = apCampoUsuarioRhPuestoImpl.traerUsuarioJsonPorCampo(Constantes.AP_CAMPO_NEJO);
    }

    private void invitadoJson() {
        invitadoJson = sgInvitadoImpl.traerInvitadoJsonPorCampo();
    }
    
    //
    private List<SelectItem> listaGerencia() {
        List<SelectItem> ls = new ArrayList<SelectItem>();
        try {
            List<GerenciaVo> lo = gerenciaImpl.traerGerenciaActivaPorCampo(Constantes.AP_CAMPO_DEFAULT);
            for (GerenciaVo g : lo) {
                SelectItem item = new SelectItem(g.getId(), g.getNombre());
                ls.add(item);
            }
            return ls;
        } catch (Exception e) {
            return null;
        }
    }

    private List<SelectItem> trearListaOficina() {
        List<SelectItem> ls = new ArrayList<SelectItem>();
        try {
            List<OficinaVO> lo = sgOficinaImpl.findByVistoBuenoList(Constantes.TRUE, Constantes.FALSE);
            for (OficinaVO oficinaVo : lo) {
                SelectItem item = new SelectItem(oficinaVo.getId(), oficinaVo.getNombre());
                ls.add(item);
            }
            return ls;
        } catch (Exception e) {
            return null;
        }
    }

    private List<SelectItem> listaMotivo() {
        List<SelectItem> ls = new ArrayList<SelectItem>();
        try {
            List<MotivoVo> lo = sgMotivoImpl.traerTodosMotivo();
            for (MotivoVo sgM : lo) {
                SelectItem item = new SelectItem(sgM.getId(), sgM.getNombre());
                ls.add(item);
            }
            return ls;
        } catch (Exception e) {
            return null;
        }
    }

    public void agregarUsuarioLista() {
        DetalleEstanciaVO detalleSolVO = new DetalleEstanciaVO();
        detalleSolVO.setUsuario("".equals(getInvitado()) ? getUser() : "");
        detalleSolVO.setInvitado("".equals(getUser()) ? getInvitado() : "");
        detalleSolVO.setIdTipoEspecifico("".equals(getInvitado()) ? Constantes.EMPLEADO : Constantes.INVITADO);
        detalleSolVO.setTipoDetalle("".equals(getInvitado()) ? "Empleado" : "Invitado");
        detalleSolVO.setIdUsuario(getIdEmpleado());
        detalleSolVO.setIdInvitado(idInvitado);
        //
        listaDetalleEstancia.add(detalleSolVO);
        setInvitado("");
        setUser("");
        setIdEmpleado("");
        setIdInvitado(0);
        //setListaUsuario(new ListDataModel(getLu()));
    }

    public void agregarInvitado(ActionEvent event) {
        setUser("");
        idInvitado = sgInvitadoImpl.guardarInvitado(sesion.getUsuario().getId(), getInvitado(), "", 2);
        PrimeFaces.current().executeScript(";$(dialogoCrearInvitado).modal('hide');");
        agregarUsuarioLista();
    }

    public void cerrarAgregarInvitado(ActionEvent event) {
        setInvitado("");
        PrimeFaces.current().executeScript(";$(dialogoCrearInvitado).modal('hide');");

    }

    public void quitarUsuarioLista() {
        listaDetalleEstancia.remove(detalleSolicitudVO);
    }

    public String completarEnvioSolicitud() {
        if (getIdOficina() > 0) {
            if (getFechaIngreso() != null) {
                if (getFechaSalida() != null) {
                    if (getIdMotivo() > 0) {
                        if (listaDetalleEstancia != null && !listaDetalleEstancia.isEmpty()) {
                            sgSolicitudEstanciaImpl.guardarEnviarSolicitud(sesion.getUsuario().getId(), idOficina, idMotivo, idGerencia, fechaIngreso, fechaSalida, listaDetalleEstancia, observacion);
                            FacesUtils.addInfoMessage("Se envio la solicitud");
                            return "/principal";
                        } else {
                            FacesUtils.addErrorMessage("Agregue huespedes a la solicitud");
                        }
                    } else {
                        FacesUtils.addErrorMessage("Es necesario seleccionar el motivo");
                    }
                } else {
                    FacesUtils.addErrorMessage("Seleccione la fecha fin");
                }
            } else {
                FacesUtils.addErrorMessage("Selecciene una fecha de inicio");
            }
        } else {
            FacesUtils.addErrorMessage("Selecciene la oficina");
        }
        return "";
    }

    public String cerrarEnvioSolicitud() {
        return "/principal";
    }

    /**
     * @return the idMotivo
     */
    public int getIdMotivo() {
        return idMotivo;
    }

    /**
     * @param idMotivo the idMotivo to set
     */
    public void setIdMotivo(int idMotivo) {
        this.idMotivo = idMotivo;
    }

    /**
     * @return the idOficina
     */
    public int getIdOficina() {
        return idOficina;
    }

    /**
     * @param idOficina the idOficina to set
     */
    public void setIdOficina(int idOficina) {
        this.idOficina = idOficina;
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
     * @return the fechaIngresoHuesped
     */
    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    /**
     * @param fechaIngreso
     */
    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    /**
     * @return the fechaSalidaHuesped
     */
    public Date getFechaSalida() {
        return fechaSalida;
    }

    /**
     * @param fechaSalida
     */
    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the invitado
     */
    public String getInvitado() {
        return invitado;
    }

    /**
     * @param invitado the invitado to set
     */
    public void setInvitado(String invitado) {
        this.invitado = invitado;
    }

    /**
     * @return the listaDetalleEstancia
     */
    public List<DetalleEstanciaVO> getListaDetalleEstancia() {
        return listaDetalleEstancia;
    }

    /**
     * @param listaDetalleEstancia the listaDetalleEstancia to set
     */
    public void setListaDetalleEstancia(List<DetalleEstanciaVO> listaDetalleEstancia) {
        this.listaDetalleEstancia = listaDetalleEstancia;
    }

    /**
     * @return the detalleSolicitudVO
     */
    public DetalleEstanciaVO getDetalleSolicitudVO() {
        return detalleSolicitudVO;
    }

    /**
     * @param detalleSolicitudVO the detalleSolicitudVO to set
     */
    public void setDetalleSolicitudVO(DetalleEstanciaVO detalleSolicitudVO) {
        this.detalleSolicitudVO = detalleSolicitudVO;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the mapaCombos
     */
    public Map<String, List<SelectItem>> getMapaCombos() {
        return mapaCombos;
    }

    /**
     * @return the usuarioJson
     */
    public String getUsuarioJson() {
        return usuarioJson;
    }

    /**
     * @return the invitadoJson
     */
    public String getInvitadoJson() {
        return invitadoJson;
    }

    /**
     * @return the idInvitado
     */
    public int getIdInvitado() {
        return idInvitado;
    }

    /**
     * @param idInvitado the idInvitado to set
     */
    public void setIdInvitado(int idInvitado) {
        this.idInvitado = idInvitado;
    }

    /**
     * @return the idEmpleado
     */
    public String getIdEmpleado() {
        return idEmpleado;
    }

    /**
     * @param idEmpleado the idEmpleado to set
     */
    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    /**
     * @return the observacion
     */
    public String getObservacion() {
        return observacion;
    }

    /**
     * @param observacion the observacion to set
     */
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
