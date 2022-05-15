/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.SessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.inventarios.service.InvDetalleSolicitudMaterialImpl;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.inventarios.service.InvSolicitudMaterialImpl;
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.EstadoAprobacionSolicitudVo;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.util.SolicitudMaterialEstadoEnum;

/**
 *
 * @author mluis
 */
@Named(value = "reporteFormatosSalidaBean")
@ViewScoped
public class ReporteFormatosSalidaBean implements Serializable {

    /**
     * Creates a new instance of reporteFormatosSalidaBean
     */
    public ReporteFormatosSalidaBean() {
    }
    @Inject
    InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudImpl;
    @Inject
    InvSolicitudMaterialImpl solicitudMaterialImpl;
    @Inject
    InvDetalleSolicitudMaterialImpl detalleSolicitudMaterialImpl;
    @Inject
    EstatusImpl estatusImpl;

    final protected SessionBean sesion = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");

    private List<SolicitudMaterialAlmacenVo> solicitudes;
    private List<DetalleSolicitudMaterialAlmacenVo> detalleSolicitudMaterialAlmacenVos;
    private SolicitudMaterialAlmacenVo solicitudeVo;
    private List<EstadoAprobacionSolicitudVo> datosSolicitud;
    private Date inicio;
    private Date fin;
    private Date maximaFecha;
    private List<SelectItem> estados;
    private int idStatus;

    @PostConstruct
    public void inciar() {
        setSolicitudes(new ArrayList<SolicitudMaterialAlmacenVo>());
        llenar();
        setSolicitudeVo(new SolicitudMaterialAlmacenVo());
        datosSolicitud = new ArrayList<>();
        detalleSolicitudMaterialAlmacenVos = new ArrayList<>();
        estados = new ArrayList<SelectItem>();
        maximaFecha = new Date();
        //
        llenarEstados();
    }

    private void llenarEstados() {
        estados.add(new SelectItem(SolicitudMaterialEstadoEnum.SOLICITUD_CREADA.getId(), "Solicitud creada"));
        estados.add(new SelectItem(SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId(), "Por Autorizar"));
        estados.add(new SelectItem(SolicitudMaterialEstadoEnum.POR_ENTREGAR_MATERIAL.getId(), "Por Entregar material"));
        estados.add(new SelectItem(SolicitudMaterialEstadoEnum.SOLICITUD_TERMINADA.getId(), "Solicitud terminada"));
    }

    private void llenar() {
        setSolicitudes(solicitudMaterialImpl.traerSolicitudesPorCampo(sesion.getUser().getIdCampo(), inicio, fin, idStatus));
    }

    public void seleccionarSolicitud(int idSol) {
        solicitudeVo = solicitudMaterialImpl.solicitudesPorId(idSol);
        datosSolicitud = estadoAprobacionSolicitudImpl.traerProcesoAprobacionPorSolicitud(idSol);
        detalleSolicitudMaterialAlmacenVos = solicitudeVo.getMateriales();
        //
        PrimeFaces.current().executeScript( ";mostrarDialogoFormatosSalidaMaterial();");
    }

    public void cerrarFormatosSalidaMaterial() {
        solicitudeVo = new SolicitudMaterialAlmacenVo();
        datosSolicitud = new ArrayList<>();
        PrimeFaces.current().executeScript( ";cerrarDialogoFormatosSalidaMaterial();");
    }

    public void buscarFormatos() {
        llenar();
    }

    public void reestablecerFormatos() {
        inicio = null;
        fin = null;
        idStatus = Constantes.CERO;
        llenar();
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
     * @return the solicitudeVo
     */
    public SolicitudMaterialAlmacenVo getSolicitudeVo() {
        return solicitudeVo;
    }

    /**
     * @param solicitudeVo the solicitudeVo to set
     */
    public void setSolicitudeVo(SolicitudMaterialAlmacenVo solicitudeVo) {
        this.solicitudeVo = solicitudeVo;
    }

    /**
     * @return the datosSolicitud
     */
    public List<EstadoAprobacionSolicitudVo> getDatosSolicitud() {
        return datosSolicitud;
    }

    /**
     * @param datosSolicitud the datosSolicitud to set
     */
    public void setDatosSolicitud(List<EstadoAprobacionSolicitudVo> datosSolicitud) {
        this.datosSolicitud = datosSolicitud;
    }

    /**
     * @return the detalleSolicitudMaterialAlmacenVos
     */
    public List<DetalleSolicitudMaterialAlmacenVo> getDetalleSolicitudMaterialAlmacenVos() {
        return detalleSolicitudMaterialAlmacenVos;
    }

    /**
     * @param detalleSolicitudMaterialAlmacenVos the
     * detalleSolicitudMaterialAlmacenVos to set
     */
    public void setDetalleSolicitudMaterialAlmacenVos(List<DetalleSolicitudMaterialAlmacenVo> detalleSolicitudMaterialAlmacenVos) {
        this.detalleSolicitudMaterialAlmacenVos = detalleSolicitudMaterialAlmacenVos;
    }

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        this.fin = fin;
    }

    /**
     * @return the maximaFecha
     */
    public Date getMaximaFecha() {
        return maximaFecha;
    }

    /**
     * @return the estados
     */
    public List<SelectItem> getEstados() {
        return estados;
    }

    /**
     * @param estados the estados to set
     */
    public void setEstados(List<SelectItem> estados) {
        this.estados = estados;
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
}
