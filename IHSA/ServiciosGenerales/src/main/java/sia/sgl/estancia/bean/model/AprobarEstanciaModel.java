/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.estancia.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;

import sia.constantes.Constantes;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.sgl.sistema.bean.backing.Sesion;

/**
 *
 * @author ihsa
 */
@Named(value = "aprobarEstanciaModel")
@ViewScoped
public class AprobarEstanciaModel implements Serializable {

    @Inject
    private Sesion sesion;
    //
    private List<SgSolicitudEstanciaVo> listaSolicitud = new ArrayList<>();

    public AprobarEstanciaModel() {
    }

    @Inject
    SgSolicitudEstanciaImpl SgSolicitudEstanciaImpl;
    //
    private SgSolicitudEstanciaVo solicitudEstanciaVo;

    @PostConstruct
    public void inicio() {
	solicitudEstanciaVo = new SgSolicitudEstanciaVo();
	listaSolicitud = SgSolicitudEstanciaImpl.solicituesPorAprobar(sesion.getUsuario().getId());
    }

    public void aprobarEstancia() {
	SgSolicitudEstanciaImpl.aprobarEstancia(sesion.getUsuario().getId(), solicitudEstanciaVo.getId());
	listaSolicitud = SgSolicitudEstanciaImpl.solicituesPorAprobar(sesion.getUsuario().getId());

    }

    public void cancelarEstancia() {
	String motivo = "NO es necesaria";
	SgSolicitudEstanciaImpl.cancelarSolicitudEstancia(sesion.getUsuario(), solicitudEstanciaVo, motivo, Constantes.TRUE,Constantes.TRUE);
	listaSolicitud = SgSolicitudEstanciaImpl.solicituesPorAprobar(sesion.getUsuario().getId());

    }

    public void solicitudEstanciaPorId() {
	solicitudEstanciaVo = SgSolicitudEstanciaImpl.buscarEstanciaPorId(solicitudEstanciaVo.getId());
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }

    /**
     * @return the listaSolicitud
     */
    public List<SgSolicitudEstanciaVo> getListaSolicitud() {
	return listaSolicitud;
    }

    /**
     * @param listaSolicitud the listaSolicitud to set
     */
    public void setListaSolicitud(List<SgSolicitudEstanciaVo> listaSolicitud) {
	this.listaSolicitud = listaSolicitud;
    }

    /**
     * @return the solicitudEstanciaVo
     */
    public SgSolicitudEstanciaVo getSolicitudEstanciaVo() {
	return solicitudEstanciaVo;
    }

    /**
     * @param solicitudEstanciaVo the solicitudEstanciaVo to set
     */
    public void setSolicitudEstanciaVo(SgSolicitudEstanciaVo solicitudEstanciaVo) {
	this.solicitudEstanciaVo = solicitudEstanciaVo;
    }
}
