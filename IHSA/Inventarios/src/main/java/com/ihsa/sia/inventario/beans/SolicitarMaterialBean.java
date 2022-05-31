package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.SessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.inventarios.service.AlmacenRemote;
import sia.inventarios.service.ArticuloRemote;
import sia.inventarios.service.InvCadenaAprobacionImpl;
import sia.inventarios.service.InvDetalleSolicitudMaterialImpl;
import sia.inventarios.service.InvSolicitudMaterialImpl;
import sia.inventarios.service.InventarioImpl;
import sia.modelo.ApCampo;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.orden.vo.MovimientoVO;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.InvSolicitudMovimientoImpl;
import sia.modelo.vo.inventarios.InventarioVO;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "solicitarMaterialBean")
@ViewScoped
public class SolicitarMaterialBean implements Serializable {

    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    InvSolicitudMaterialImpl invSolicitudMaterialImpl;
    @Inject
    InvDetalleSolicitudMaterialImpl detalleSolicitudMaterialImpl;
    @Inject
    GerenciaImpl gerenciaImpl;
    @Inject
    ArticuloRemote articuloImpl;
    @Inject
    InventarioImpl inventarioImpl;
    @Inject
    AlmacenRemote almacenImpl;
    @Inject
    InvCadenaAprobacionImpl cadenaAprobacionImpl;
    @Inject
    InvSolicitudMovimientoImpl solicitudMovimientoImpl;
    @Inject
    ApCampoImpl apCampoImpl;

    private List<SelectItem> articulos;
    private List<InventarioVO> inventarios;
    private SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo;
    private List<SolicitudMaterialAlmacenVo> solicitudes;
    private List<DetalleSolicitudMaterialAlmacenVo> materiales;
    private List<SelectItem> gerencias;
    private List<SelectItem> almacenes;
    private InventarioVO articulo;
    private int idGerencia, idAlmacen;
    private String observacion, idAutoriza;
    private List<SelectItem> autorizadores;
    private Date fechaMinima;
    private List<MovimientoVO> devoluciones;
    private ApCampo apCampo;
    @Getter
    @Setter
    private ArticuloVO articuloVo;

    final protected SessionBean sesion = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");

    public SolicitarMaterialBean() {
    }

    @PostConstruct
    protected void iniciar() {
        solicitudMaterialAlmacenVo = new SolicitudMaterialAlmacenVo();
        articulos = new ArrayList<>();
        autorizadores = new ArrayList<>();
        inventarios = new ArrayList<>();
        devoluciones = new ArrayList<>();
        solicitudes = new ArrayList<>();
        materiales = new ArrayList<>();
        gerencias = new ArrayList<>();
        almacenes = new ArrayList<>();
        articulo = new InventarioVO();
        articuloVo = new ArticuloVO();
        apCampo = apCampoImpl.find(sesion.getUser().getIdCampo());
        //
        idAlmacen = apCampo.getInvAlmacen().getId();
        idGerencia = sesion.getUser().getIdGerencia();
        //
        llenar();
        //
        List<GerenciaVo> ger = gerenciaImpl.getAllGerenciaByApCampo(sesion.getUser().getIdCampo(), "nombre", true, Boolean.TRUE, false);
        for (GerenciaVo gerenciaVo : ger) {
            gerencias.add(new SelectItem(gerenciaVo.getId(), gerenciaVo.getNombre()));
        }
        //
        List<AlmacenVO> alm = almacenImpl.almacenesPorCampo(sesion.getUser().getIdCampo());
        for (AlmacenVO avo : alm) {
            almacenes.add(new SelectItem(avo.getId(), avo.getNombre()));
        }//
        List<CadenaAprobacionVo> cads = cadenaAprobacionImpl.traerPorSolicita(sesion.getUser().getId(), sesion.getUser().getIdCampo());
        for (CadenaAprobacionVo avo : cads) {
            autorizadores.add(new SelectItem(avo.getIdAprueba(), avo.getAprueba()));
        }
        //
        inventarios = inventarioImpl.inventarioPorCampoYAlmacen(apCampo.getId(), idAlmacen);
        //
        fechaMinima = new Date();
    }

    private void llenar() {
        solicitudes = invSolicitudMaterialImpl.traerSolicitudesGenero(sesion.getUser().getIdCampo(), sesion.getUser().getId());
    }

    public void crearSolicitudMaterial() {
        solicitudMaterialAlmacenVo = new SolicitudMaterialAlmacenVo();
        solicitudMaterialAlmacenVo.setFechaRequiere(new Date());
        solicitudMaterialAlmacenVo.setMateriales(new ArrayList<>());

        setIdGerencia(sesion.getUser().getIdGerencia());
        articulo = new InventarioVO();
        //
        materiales = new ArrayList<>();

        gerencias = new ArrayList<>();
        almacenes = new ArrayList<>();
        inventarios = new ArrayList<>();
        idAlmacen = apCampo.getInvAlmacen().getId();
        List<GerenciaVo> ger = gerenciaImpl.getAllGerenciaByApCampo(sesion.getUser().getIdCampo(), "nombre", true, Boolean.TRUE, false);
        for (GerenciaVo gerenciaVo : ger) {
            gerencias.add(new SelectItem(gerenciaVo.getId(), gerenciaVo.getNombre()));
        }
        //
        List<AlmacenVO> alm = almacenImpl.almacenesPorCampo(sesion.getUser().getIdCampo());
        for (AlmacenVO avo : alm) {
            almacenes.add(new SelectItem(avo.getId(), avo.getNombre()));
        }//
        List<CadenaAprobacionVo> cads = cadenaAprobacionImpl.traerPorSolicita(sesion.getUser().getId(), sesion.getUser().getIdCampo());
        for (CadenaAprobacionVo avo : cads) {
            autorizadores.add(new SelectItem(avo.getIdAprueba(), avo.getAprueba()));
        }
        //
        inventarios = inventarioImpl.inventarioPorCampoYAlmacen(apCampo.getId(), idAlmacen);

    }

    public void seleccionarAlmacen() {
        inventarios = inventarioImpl.inventarioPorCampoYAlmacen(apCampo.getId(), getIdAlmacen());
    }

    public List<InventarioVO> completarArticulo(String cadenaDigitada) {
        articulos.clear();
        return inventarios.stream().filter(inv -> inv.getArticuloNombre().toLowerCase().contains(cadenaDigitada.toLowerCase())).collect(Collectors.toList());

    }

    public void seleccionarArticulo() {
        try {
            //articulo = (InventarioVO) autoComplete.getSelectedItem().getValue();
             InventarioVO invVo = inventarios.stream().filter(inv -> (Objects.equals(inv.getId(), articulo.getId()))).findAny().get();
            DetalleSolicitudMaterialAlmacenVo ddVo = new DetalleSolicitudMaterialAlmacenVo();            
            ddVo.setArticulo(invVo.getArticuloNombre());
            ddVo.setUnidad(invVo.getArticuloUnidad());
            ddVo.setIdUnidad(invVo.getUnidadId());
            ddVo.setCodigoArt(invVo.getCodigo());
            ddVo.setDisponibles(invVo.getTotalUnidades());
            //
            materiales.add(ddVo);
            //
            limpiarAlmacen(idAlmacen);
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    private void limpiarAlmacen(int idAlm) {
        if (idAlm > 0) {
            SelectItem aux = null;
            for (SelectItem itm : almacenes) {
                if (idAlm == (Integer) itm.getValue()) {
                    aux = new SelectItem(itm.getValue(), itm.getLabel());
                    break;
                }
            }
            if (aux != null) {
                setAlmacenes(new ArrayList<>());
                getAlmacenes().add(aux);
            }
        } else {
            setAlmacenes(new ArrayList<>());
            List<AlmacenVO> alm = almacenImpl.almacenesPorCampo(sesion.getUser().getIdCampo());
            for (AlmacenVO avo : alm) {
                almacenes.add(new SelectItem(avo.getId(), avo.getNombre()));
            }
        }
    }

    public void eliminarMaterialSolicitud(String codigo) {
        DetalleSolicitudMaterialAlmacenVo ddVo = new DetalleSolicitudMaterialAlmacenVo();
        for (DetalleSolicitudMaterialAlmacenVo materiale : materiales) {
            if (codigo.equals(materiale.getCodigoArt())) {
                ddVo = materiale;
                break;
            }
        }
        if (ddVo.getId() > 0) {
            detalleSolicitudMaterialImpl.eliminar(ddVo.getId(), sesion.getUser().getId());
        }
        materiales.remove(ddVo);
        if (materiales != null && materiales.isEmpty()) {
            limpiarAlmacen(0);
        }
    }

    public void guardar() {
        if (!solicitudMaterialAlmacenVo.getTelefono().isEmpty()) {
            if (solicitudMaterialAlmacenVo.getFechaRequiere() != null) {
                if (!solicitudMaterialAlmacenVo.getUsuarioRecoge().isEmpty() && solicitudMaterialAlmacenVo.getUsuarioRecoge().contains(" ")) {
                    if (!idAutoriza.isEmpty()) {
                        boolean isCantidad = true;
                        for (DetalleSolicitudMaterialAlmacenVo materiale : materiales) {
                            if (materiale.getCantidad() <= 0 || materiale.getCantidad() > materiale.getDisponibles()) {
                                isCantidad = false;
                                break;
                            }
                        }
                        if (isCantidad) {
                            solicitudMaterialAlmacenVo.setIdGerencia(idGerencia);
                            solicitudMaterialAlmacenVo.setIdAlmacen(idAlmacen);
                            solicitudMaterialAlmacenVo.setObservacion(observacion);
                            solicitudMaterialAlmacenVo.setMateriales(materiales);
                            invSolicitudMaterialImpl.guardar(solicitudMaterialAlmacenVo, sesion.getUser().getId(), sesion.getUser().getIdCampo());
                            PrimeFaces.current().executeScript("PF('crearDialogoMaterial').hide()");
                            llenar();
                        } else {
                            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Materiales deben tener cantidad y debe ser menor o igual a lo disponible en almacén.", null);
                            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                        }
                    } else {
                        mensajePantalla("Para solicitar es necesario contar con cadena de aprobación.");
                    }
                } else {
                    mensajePantalla("Es necesario agregar nombre y apellidos de quien recogerá el material.");
                }
            } else {
                mensajePantalla("Es necesario agregar fecha.");
            }
        } else {
            mensajePantalla("Es necesario agregar teléfono.");
        }
    }

    private void mensajePantalla(String mensaje) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    public void guardarSolicitarMaterial() {
        if (materiales != null && !materiales.isEmpty()) {
            if (!solicitudMaterialAlmacenVo.getTelefono().isEmpty()) {
                if (solicitudMaterialAlmacenVo.getFechaRequiere() != null) {
                    if (!solicitudMaterialAlmacenVo.getUsuarioRecoge().isEmpty() && solicitudMaterialAlmacenVo.getUsuarioRecoge().contains(" ")) {
                        if (!idAutoriza.isEmpty()) {
                            boolean isCantidad = true;
                            for (DetalleSolicitudMaterialAlmacenVo materiale : materiales) {
                                if (materiale.getCantidad() <= 0 || materiale.getCantidad() > materiale.getDisponibles()) {
                                    isCantidad = false;
                                    break;
                                }
                            }
                            if (isCantidad) {
                                solicitudMaterialAlmacenVo.setIdGerencia(idGerencia);
                                solicitudMaterialAlmacenVo.setIdAlmacen(idAlmacen);
                                solicitudMaterialAlmacenVo.setObservacion(observacion);
                                solicitudMaterialAlmacenVo.setMateriales(materiales);
                                //
                                invSolicitudMaterialImpl.guardarSolicitar(solicitudMaterialAlmacenVo, sesion.getUser().getId(), sesion.getUser().getIdCampo(), idAutoriza);
                                llenar();
                                PrimeFaces.current().executeScript("PF('crearDialogoMaterial').hide()");
                            } else {
                                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Materiales deben tener cantidad y debe ser menor o igual a lo disponible en almacén.", null);
                                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                            }
                        } else {
                            mensajePantalla("Para solicitar es necesario contar con cadena de aprobación.");
                        }
                    } else {
                        mensajePantalla("Es necesario agregar nombre y apellidos de quien recogerá el material.");
                    }
                } else {
                    mensajePantalla("Es necesario agregar fecha.");
                }
            } else {
                mensajePantalla("Es necesario agregar teléfono.");
            }
        } else {
            mensajePantalla("Es necesario agregar por lo menos un artículo.");
        }
    }

    public void cerrarSolicitudMaterial() {
        materiales.clear();
        devoluciones.clear();
        solicitudMaterialAlmacenVo.setMateriales(new ArrayList<>());
        PrimeFaces.current().executeScript("PF('crearDialogoMaterial').hide()");
    }

    public void modificar(int idSolicitud) {
        materiales = new ArrayList<>();
        solicitudMaterialAlmacenVo = invSolicitudMaterialImpl.solicitudesPorId(idSolicitud);
        idGerencia = solicitudMaterialAlmacenVo.getIdGerencia();
        idAlmacen = solicitudMaterialAlmacenVo.getIdAlmacen();
        observacion = solicitudMaterialAlmacenVo.getObservacion();
        materiales = solicitudMaterialAlmacenVo.getMateriales();
        //
        devoluciones = solicitudMovimientoImpl.traerPorSolicitud(idSolicitud);
        articulo = new InventarioVO();

        limpiarAlmacen(materiales.isEmpty() ? 0 : idAlmacen);

        //
    }

    public void solicitarMaterial(int idSolicitud) {
        if (autorizadores.size() == 1) {
            invSolicitudMaterialImpl.solicitarMateriales(idSolicitud, autorizadores.get(0).getValue().toString(), sesion.getUser().getId());
            llenar();
        } else {
            // mostrar un pop donde elija quien le autorizará
            solicitudMaterialAlmacenVo = invSolicitudMaterialImpl.solicitudesPorId(idSolicitud);
            PrimeFaces.current().executeScript("PF('crearDialogoMaterial').show();");
        }
    }

    public void solicitarMaterialAutorizar() {
        invSolicitudMaterialImpl.solicitarMateriales(solicitudMaterialAlmacenVo.getId(), idAutoriza, sesion.getUser().getId());
        PrimeFaces.current().executeScript("PF('crearDialogoMaterial').hide()");
        llenar();
    }

    public void eliminiarSolicitarMaterial(int idSolicitud) {
        solicitudMaterialAlmacenVo = invSolicitudMaterialImpl.solicitudesPorId(idSolicitud);
        invSolicitudMaterialImpl.eliminar(solicitudMaterialAlmacenVo, sesion.getUser().getId());
        llenar();
    }

    /**
     * @return the solicitudMaterialAlmacenVo
     */
    public SolicitudMaterialAlmacenVo getSolicitudMaterialAlmacenVo() {
        return solicitudMaterialAlmacenVo;
    }

    /**
     * @param solicitudMaterialAlmacenVo the solicitudMaterialAlmacenVo to set
     */
    public void setSolicitudMaterialAlmacenVo(SolicitudMaterialAlmacenVo solicitudMaterialAlmacenVo) {
        this.solicitudMaterialAlmacenVo = solicitudMaterialAlmacenVo;
    }

    /**
     * @return the solicitudes
     */
    public List<SolicitudMaterialAlmacenVo> getSolicitudes() {
        return solicitudes;
    }

    /**
     * @param solicitudes the solicitudes to set
     */
    public void setSolicitudes(List<SolicitudMaterialAlmacenVo> solicitudes) {
        this.solicitudes = solicitudes;
    }

    /**
     * @return the materiales
     */
    public List<DetalleSolicitudMaterialAlmacenVo> getMateriales() {
        return materiales;
    }

    /**
     * @param materiales the materiales to set
     */
    public void setMateriales(List<DetalleSolicitudMaterialAlmacenVo> materiales) {
        this.materiales = materiales;
    }

    /**
     * @return the gerencias
     */
    public List<SelectItem> getGerencias() {
        return gerencias;
    }

    /**
     * @param gerencias the gerencias to set
     */
    public void setGerencias(List<SelectItem> gerencias) {
        this.gerencias = gerencias;
    }

    /**
     * @return the articulo
     */
    public InventarioVO getArticulo() {
        return articulo;
    }

    /**
     * @param articulo the articulo to set
     */
    public void setArticulo(InventarioVO articulo) {
        this.articulo = articulo;
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
     * @return the articulos
     */
    public List<SelectItem> getArticulos() {
        return articulos;
    }

    /**
     * @param articulos the articulos to set
     */
    public void setArticulos(List<SelectItem> articulos) {
        this.articulos = articulos;
    }

    /**
     * @return the almacenes
     */
    public List<SelectItem> getAlmacenes() {
        return almacenes;
    }

    /**
     * @param almacenes the almacenes to set
     */
    public void setAlmacenes(List<SelectItem> almacenes) {
        this.almacenes = almacenes;
    }

    /**
     * @return the idAlmacen
     */
    public int getIdAlmacen() {
        return idAlmacen;
    }

    /**
     * @param idAlmacen the idAlmacen to set
     */
    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
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

    /**
     * @return the idAutoriza
     */
    public String getIdAutoriza() {
        return idAutoriza;
    }

    /**
     * @param idAutoriza the idAutoriza to set
     */
    public void setIdAutoriza(String idAutoriza) {
        this.idAutoriza = idAutoriza;
    }

    /**
     * @return the autorizadores
     */
    public List<SelectItem> getAutorizadores() {
        return autorizadores;
    }

    /**
     * @param autorizadores the autorizadores to set
     */
    public void setAutorizadores(List<SelectItem> autorizadores) {
        this.autorizadores = autorizadores;
    }

    /**
     * @return the fechaMinima
     */
    public Date getFechaMinima() {
        return fechaMinima;
    }

    /**
     * @param fechaMinima the fechaMinima to set
     */
    public void setFechaMinima(Date fechaMinima) {
        this.fechaMinima = fechaMinima;
    }

    /**
     * @return the devoluciones
     */
    public List<MovimientoVO> getDevoluciones() {
        return devoluciones;
    }

    /**
     * @param devoluciones the devoluciones to set
     */
    public void setDevoluciones(List<MovimientoVO> devoluciones) {
        this.devoluciones = devoluciones;
    }

}
