/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.busqueda.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;

import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgViajero;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;

/**
 *
 * @author jevazquez
 */
@Named(value = "misSolicitudesBeanModel")
@ViewScoped
public class MisSolicitudesBeanModel implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    SgSolicitudViajeImpl solicitudViajeImpl;
    @Inject
    SgViajeImpl viajeImpl;
    @Inject
    SgViajeroImpl viajeroImpl;
    @Inject
    SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    EstatusImpl estatusImpl;
    @Inject
    SgEstatusAprobacionImpl aprobacionImpl;

    private List<SolicitudViajeVO> listaSolicitudes;
    private ViajeroVO viajero;
    private ViajeVO viaje;
    private SolicitudViajeVO solicitudViaje;
    private Date fechaInicio;
    private Date fechaFin;
    private String motivo;
    private SolicitudViajeVO svActual;

    /**
     * @return the viajero
     */
    public ViajeroVO getViajero() {
        return viajero;
    }

    /**
     * @param viajero the viajero to set
     */
    public void setViajero(ViajeroVO viajero) {
        this.viajero = viajero;
    }

    /**
     * @return the viaje
     */
    public ViajeVO getViaje() {
        return viaje;
    }

    /**
     * @param viaje the viaje to set
     */
    public void setViaje(ViajeVO viaje) {
        this.viaje = viaje;
    }

    /**
     * @return the solicitudViaje
     */
    public SolicitudViajeVO getSolicitudViaje() {
        return solicitudViaje;
    }

    /**
     * @param solicitudViaje the solicitudViaje to set
     */
    public void setSolicitudViaje(SolicitudViajeVO solicitudViaje) {
        this.solicitudViaje = solicitudViaje;
    }

    /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return sesion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the example
     */
    @PostConstruct
    public void inicializar() {
        listarMisSolicitudes(null, null, "act");

    }

    public void listarMisSolicitudes(Date d1, Date d2, String filtro) {
        List<SolicitudViajeVO> lsv = solicitudViajeImpl.traerListMisSolicitudes(sesion.getUsuario().getId(), d1, d2, filtro);
        setListaSolicitudes(lsv);
    }

    /**
     * @return the listaSolicitudes
     */
    public List<SolicitudViajeVO> getListaSolicitudes() {
        return listaSolicitudes;
    }

    /**
     * @param listaSolicitudes the listaSolicitudes to set
     */
    public void setListaSolicitudes(List<SolicitudViajeVO> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public void buscarByFiltros() {
        String var = FacesUtils.getRequestParameter("selecFiltro");
        listarMisSolicitudes(getFechaInicio(), getFechaFin(), var);
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void cancelarSV() throws Exception {
        setMotivo(FacesUtils.getRequestParameter("msjCancelacion"));
        boolean cancelada = false;

        if (getSvActual() != null) {
            if (getSvActual().getFechaSalida()
                    .after(siManejoFechaLocal.fechaRestarDias(new Date(), 1)) && getSvActual().getIdEstatus() > Constantes.ESTATUS_PENDIENTE) {
                if (getSvActual().getIdEstatus() < Constantes.ESTATUS_PARA_HACER_VIAJE) {
                    List<ViajeroVO> lviajeros = viajeroImpl.getAllViajerosList(getSvActual().getIdSolicitud());

                    if (lviajeros.size() > 1) {

                        for (ViajeroVO vo : lviajeros) {
                            if (vo.getIdUsuario().equals(sesion.getUsuario().getId())) {
                                SgViajero vro = viajeroImpl.find(vo.getId());
                                viajeroImpl.cancelTraveller(sesion.getUsuario(), vro, getMotivo(), Constantes.CERO);
                                cancelada = true;
                            }
                        }

                        
                        FacesUtils.addInfoMessage("Se a bajado al viajero de la solicitud de viaje, debido ha que hay más viajeros en esta solicitud");
                    } else {
                        cancelada = aprobacionImpl.cancelarSolicitud(
                                aprobacionImpl.buscarEstatusAprobacionPorIdSolicitudIdEstatus(getSvActual().getIdSolicitud(), getSvActual().getIdEstatus()).getId(),
                                getMotivo(), sesion.getUsuario().getId(), Constantes.FALSE, Constantes.TRUE, Constantes.TRUE);
                        FacesUtils.addInfoMessage("Se a Cancelado la solicitud de viaje");

                    }

                } else {
                    List<ViajeVO> viajes = viajeImpl.traerViajesPorSolicitud(getSvActual().getIdSolicitud());
                    boolean bajar = true;
                    //  List<SgViaje> lv = new ArrayList<>();
                    if (viajes != null && !viajes.isEmpty()) {
                        for (ViajeVO vo : viajes) {
                            if (vo.getIdEstatus() == 510) {
                                bajar = false;
                                break;
                            }
                            // lv.add(viajeImpl.find(vo.getId()));
                        }
                        if (bajar) {
                            // lo baja de los viajes
                            /*for (SgViaje v : lv) {
                                v.setEliminado(bajar);
                                v.setModifico(sesion.getUsuario());
                                v.setFechaModifico(new Date());
                                v.setHoraModifico(new Date());
                                v.setSgViaje(null);
                                viajeImpl.edit(v);

                            }*/
                            List<ViajeroVO> lviajeros = viajeroImpl.getAllViajerosList(getSvActual().getIdSolicitud());

                            // se valida lo de la sv
                            if (lviajeros.size() > 1) {

                                for (ViajeroVO vo : lviajeros) {
                            if (vo.getIdUsuario().equals(sesion.getUsuario().getId())) {
                                SgViajero vro = viajeroImpl.find(vo.getId());
                                viajeroImpl.cancelTraveller(sesion.getUsuario(), vro, getMotivo(), Constantes.CERO);
                                cancelada = true;
                            } else {
                                cancelada = false;
                            }
                        }
                                if (cancelada){
                                    cancelada = aprobacionImpl.cancelarSolicitud(
                                        aprobacionImpl.buscarEstatusAprobacionPorIdSolicitudIdEstatus(getSvActual().getIdSolicitud(), getSvActual().getIdEstatus()).getId(),
                                        getMotivo(), sesion.getUsuario().getId(), Constantes.FALSE, Constantes.TRUE, Constantes.TRUE);
                                FacesUtils.addInfoMessage("Se a bajado al viajero del viaje, debido ha que el viajero cancelo su solicitud");
                                } else {
                                    FacesUtils.addInfoMessage("Se a bajado al viajero de la solicitud de viaje, debido ha que la solicitud contaba con más viajeros");
                                    cancelada = true;
                                }
                                
                            } else {
                                cancelada = aprobacionImpl.cancelarSolicitud(
                                        aprobacionImpl.buscarEstatusAprobacionPorIdSolicitudIdEstatus(getSvActual().getIdSolicitud(), getSvActual().getIdEstatus()).getId(),
                                        getMotivo(), sesion.getUsuario().getId(), Constantes.FALSE, Constantes.TRUE, Constantes.TRUE);
                                FacesUtils.addInfoMessage("Se a Cancelado la solicitud de viaje");

                            }

                        } else {
                            //poner msj de que no se puede bajar viaje en proceso
                            FacesUtils.addInfoMessage("No se puede Cancelar la Solicitud debido a que el viaje ya está en proceso");
                        }
                    } else {
                        //el estatus de la sv es 450 pero no tiene viajes
                        List<ViajeroVO> lviajeros = viajeroImpl.getAllViajerosList(getSvActual().getIdSolicitud());

                        // se valida lo de la sv
                        if (lviajeros.size() > 1) {
                            for (ViajeroVO vo : lviajeros) {
                            if (vo.getIdUsuario().equals(sesion.getUsuario().getId())) {
                                SgViajero vro = viajeroImpl.find(vo.getId());
                                viajeroImpl.cancelTraveller(sesion.getUsuario(), vro, getMotivo(), Constantes.CERO);
                                cancelada = true;
                            } 
                        }
                            FacesUtils.addInfoMessage("Se a bajado al viajero de la solicitud de viaje, debido ha que hay más viajeros en esta solicitud");
                        } else {
                            cancelada = aprobacionImpl.cancelarSolicitud(
                                    aprobacionImpl.buscarEstatusAprobacionPorIdSolicitudIdEstatus(getSvActual().getIdSolicitud(), getSvActual().getIdEstatus()).getId(),
                                    getMotivo(), sesion.getUsuario().getId(), Constantes.FALSE, Constantes.TRUE, Constantes.TRUE);

                        }
                    }
                }
            }
        }
        if (cancelada) {
            PrimeFaces.current().executeScript(";$('#modalCancelar').modal('hide');");
            listarMisSolicitudes(null, null, "act");
            setMotivo("");
            setSvActual(null);
        }

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

    public void popCancelarSolicitud() {
        String var = FacesUtils.getRequestParameter("idsv");
        SgSolicitudViaje solicitud;
        //List<ViajeroVO> lviajeros = viajeroImpl.getAllViajerosList(0);
        if (var != null && !var.isEmpty()) {
            int ids = Integer.parseInt(var);

            solicitud = solicitudViajeImpl.find(ids);
            for (SolicitudViajeVO sv : this.getListaSolicitudes()) {
                if (sv.getIdSolicitud() == ids) {
                    setSvActual(sv);
                    PrimeFaces.current().executeScript(";$('#modalCancelar').modal('show');");
                    break;
                }
            }
        }
    }

    /**
     * @return the svActual
     */
    public SolicitudViajeVO getSvActual() {
        return svActual;
    }

    /**
     * @param svActual the svActual to set
     */
    public void setSvActual(SolicitudViajeVO svActual) {
        this.svActual = svActual;
    }

}
