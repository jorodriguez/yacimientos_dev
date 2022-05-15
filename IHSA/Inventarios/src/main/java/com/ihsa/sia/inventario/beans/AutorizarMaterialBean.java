/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.SessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;

import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.inventarios.service.InvSolicitudMaterialImpl;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.util.SolicitudMaterialEstadoEnum;

/**
 *
 * @author mluis
 */
@Named(value = "autorizarMaterialBean")
@ViewScoped
public class AutorizarMaterialBean implements Serializable{

    /**
     * Creates a new instance of AutorizarMaterialBean
     */
    public AutorizarMaterialBean() {
    }

    @Inject
    InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudImpl;
    @Inject
    InvSolicitudMaterialImpl solicitudMaterialImpl;

    final protected SessionBean sesion = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");

    private List<SolicitudMaterialAlmacenVo> solicitudes;
    private SolicitudMaterialAlmacenVo solicitudeVo;
    private String motivo;

    @PostConstruct
    public void inciar() {
        setSolicitudes(new ArrayList<>());
        llenar();
        solicitudeVo = new SolicitudMaterialAlmacenVo();
    }

    private void llenar() {
        solicitudes = estadoAprobacionSolicitudImpl.traerSolicitudesUsuarioStatus(sesion.getUser().getIdCampo(), sesion.getUser().getId(), SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId());
    }

    public void autorizarMaterial(int idSolicitud) {
        estadoAprobacionSolicitudImpl.autrizarSolicitud(idSolicitud, sesion.getUser().getId(), sesion.getUser().getIdCampo());
        //
        llenar();
    }

    public void rechazarSolicitud(int idSolicitud) {
        solicitudeVo = solicitudMaterialImpl.solicitudesPorId(idSolicitud);
        //
        PrimeFaces.current().executeScript(";mostrarDialogo(dialogoSolicitudRechazo);");
    }

    public void completarRechazarSolicitud() {
        if (!motivo.isEmpty()) {
            estadoAprobacionSolicitudImpl.rechazarSolicitud(solicitudeVo.getId(), sesion.getUser().getId(), motivo, sesion.getUser().getIdCampo());
            //
            llenar();
            PrimeFaces.current().executeScript( ";cerrarDialogo(dialogoSolicitudRechazo);");
        } else {

            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Agregar el motivo", null);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        }
    }

    public void cerrarRechazoSolicitud() {
        PrimeFaces.current().executeScript( ";cerrarDialogo(dialogoSolicitudRechazo);");
    }

    public void cancelarSolicitud(int idSolicitud) {
        solicitudeVo = solicitudMaterialImpl.solicitudesPorId(idSolicitud);
        //
        PrimeFaces.current().executeScript( ";mostrarDialogo(dialogoSolicitudCancela);");
    }

    public void completarCancelarSolicitud() {
        if (!motivo.isEmpty()) {
            estadoAprobacionSolicitudImpl.cancelarSolicitud(solicitudeVo.getId(), sesion.getUser().getId(), motivo, sesion.getUser().getIdCampo());
            //
            llenar();
            PrimeFaces.current().executeScript( ";cerrarDialogo(dialogoSolicitudCancela);");
        } else {

            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Agregar el motivo", null);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        }
    }

    public void cerrarCancelaSolicitud() {
        PrimeFaces.current().executeScript( ";cerrarDialogo(dialogoSolicitudCancela);");
    }

    public void verSolicitud(int idSolicitud) {
        solicitudeVo = solicitudMaterialImpl.solicitudesPorId(idSolicitud);
        //
        PrimeFaces.current().executeScript( ";mostrarDialogo(crearDialogoDatosSolicitud);");
    }

    public void cerrarVerSolicitud() {
        PrimeFaces.current().executeScript( ";cerrarDialogo(crearDialogoDatosSolicitud);");
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

}
