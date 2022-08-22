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
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;

import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.inventarios.service.InvEstadoAprobacionSolicitudImpl;
import sia.inventarios.service.InvSolicitudMaterialImpl;
import sia.modelo.vo.inventarios.SolicitudMaterialAlmacenVo;
import sia.util.SolicitudMaterialEstadoEnum;

/**
 *
 * @author mluis
 */
@Named(value = "vistoBuenoSalida")
@ViewScoped
public class VistoBuenoSalida implements Serializable{

    /**
     * Creates a new instance of VistoBuenoSalida
     */
    public VistoBuenoSalida() {
    }
    @Inject
    InvEstadoAprobacionSolicitudImpl estadoAprobacionSolicitudImpl;
    @Inject
    InvSolicitudMaterialImpl solicitudMaterialImpl;

    final protected SessionBean sesion = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");

    private List<SolicitudMaterialAlmacenVo> solicitudes;
    private SolicitudMaterialAlmacenVo solicitudeVo;

    @PostConstruct
    public void inciar() {
        setSolicitudes(new ArrayList<SolicitudMaterialAlmacenVo>());
        llenar();
        setSolicitudeVo(new SolicitudMaterialAlmacenVo());
    }

    private void llenar() {
        setSolicitudes(estadoAprobacionSolicitudImpl.traerSolicitudesRolStatus(sesion.getUser().getIdCampo(), sesion.getUser().getId(), SolicitudMaterialEstadoEnum.MATERIAL_ENTREGADO.getId(), Constantes.ROL_SUP_ENTREGA_MAT));
    }

    public void verSolicitud(int idSolicitud) {
        solicitudeVo = solicitudMaterialImpl.solicitudesPorId(idSolicitud);
        //
        PrimeFaces.current().executeScript( ";mostrarDialogo(crearDialogoVistoBuenoEntrega);");
    }

    public void guardarVistoBueno() {
        estadoAprobacionSolicitudImpl.vistoBuenoEntrega(solicitudeVo, sesion.getUser().getId());
        //
        llenar();
        //
        PrimeFaces.current().executeScript( ";cerrarDialogo(crearDialogoVistoBuenoEntrega);");
    }

    public void cerrarEntrega() {
        PrimeFaces.current().executeScript( ";cerrarDialogo(crearDialogoVistoBuenoEntrega);");
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
}
