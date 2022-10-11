/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ihsa.sia.inventario.beans;

import com.ihsa.sia.commons.SessionBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;

import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.inventarios.service.InvSolicitudMaterialImpl;
import sia.modelo.vo.inventarios.DetalleSolicitudMaterialAlmacenVo;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.util.SolicitudMaterialEstadoEnum;

/**
 *
 * @author mluis
 */
@Named(value = "entregaMaterialBean")
@ViewScoped
public class EntregaMaterialBean implements Serializable {

    /**
     * Creates a new instance of EntregaMaterialBean
     */
    public EntregaMaterialBean() {
    }

    @Inject
    InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudImpl;
    @Inject
    InvSolicitudMaterialImpl solicitudMaterialImpl;

    final protected SessionBean sesion = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");

    private List<SolicitudMaterialAlmacenVo> solicitudes;
    private SolicitudMaterialAlmacenVo solicitudeVo;
    private String observacion;
    private String motivo;

    @PostConstruct
    public void inciar() {
        setSolicitudes(new ArrayList<>());
        llenar();
        setSolicitudeVo(new SolicitudMaterialAlmacenVo());
    }

    private void llenar() {
        setSolicitudes(estadoAprobacionSolicitudImpl.traerSolicitudesRolStatus(sesion.getUser().getIdCampo(), sesion.getUser().getId(), SolicitudMaterialEstadoEnum.POR_ENTREGAR_MATERIAL.getId(), Constantes.ROL_ENTREGA_MAT));
    }

    public void finalizarSolicitud(int idSolicitud) {
        solicitudeVo = solicitudMaterialImpl.solicitudesPorId(idSolicitud);
        //    
    }

    public void completarFinalizaSolicitud() {
        estadoAprobacionSolicitudImpl.finalizarSolicitudMaterial(solicitudeVo, sesion.getUser(), motivo);
        llenar();
        //    
        PrimeFaces.current().executeScript("PF('crearDialogoFinalizaSolicitud').hide();");
    }

    public void cerrarFinalizaSolicitud() {
        solicitudeVo = new SolicitudMaterialAlmacenVo();
        //    
        PrimeFaces.current().executeScript("PF('crearDialogoFinalizaSolicitud').hide();");
    }

    public void verSolicitud(int idSolicitud) {
        solicitudeVo = solicitudMaterialImpl.solicitudesPorId(idSolicitud);
        //    
        PrimeFaces.current().executeScript(";mostrarDialogo(crearDialogoEntregaMaterial);");
    }

    public void guardarEntrega() {
        boolean continuar = true;

        if (!solicitudeVo.getUsuarioRecibeMaterial().isEmpty() && solicitudeVo.getUsuarioRecibeMaterial().contains(" ")) {
            for (DetalleSolicitudMaterialAlmacenVo materiale : solicitudeVo.getMateriales()) {
                if (materiale.getCantidad() == Constantes.CERO || materiale.getCantidadRecibida() != materiale.getCantidad()) {
                    continuar = false;
                    break;
                }
            }
            if (continuar) {
                entregarMaterial();
            } else {
                PrimeFaces current = PrimeFaces.current();
                current.executeScript("PF('confirmarEntrega').show();");
                current.ajax().update("frmConfEntrega");
            }
        } else {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Es necesario agregar el nombre del usuario.", null);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        }

    }

    private void entregarMaterial() {
        if (!observacion.isEmpty()) {
            solicitudeVo.setObservacion(solicitudeVo.getObservacion() + " * " + sesion.getUser().getNombre() + ": " + observacion);
        }
        solicitudeVo.setIdCampo(sesion.getUser().getIdCampo());
        estadoAprobacionSolicitudImpl.entregarMaterial(solicitudeVo, sesion.getUser());
        //
        llenar();
        //
        PrimeFaces.current().executeScript("PF('crearDialogoEntregaMaterial').hide();");
    }

    public void cerrarEntrega() {
        PrimeFaces.current().executeScript("PF('crearDialogoEntregaMaterial').hide();");
    }

    public void confirmarEntrega() {
        PrimeFaces.current().executeScript("PF('confirmarEntrega').hide();");
        entregarMaterial();

    }

    public void cerrarConfirmacionEntrega() {
        PrimeFaces.current().executeScript("PF('confirmarEntrega').hide();");
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
