/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.busqueda.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import sia.constantes.Constantes;
import sia.modelo.sgl.viaje.vo.EstatusAprobacionSolicitudVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.servicios.sgl.impl.SgEstatusAprobacionImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;

/**
 *
 * @author jevazquez
 */
@Named(value = "buscarSVOrVIBeanModel")
@ViewScoped
public class BuscarSVOrVIBeanModel implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    SgSolicitudViajeImpl solicitudViajeImpl;
    @Inject
    SgViajeImpl viajeImpl;
    @Inject
    SgEstatusAprobacionImpl estatusAprobacionImpl;

    private String codigoSV;
    private String fechaSalida;
    private String fechaRegreso;
    private boolean tipoSoV;
    private SolicitudViajeVO sv;
    private ViajeVO viajeBuscar;
    private List<ViajeVO> vi;
    private List<EstatusAprobacionSolicitudVO> lea;

    private String codigoVI;
    private String estatusActual;

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
        setTipoSoV(Constantes.TRUE);//tipo SV
        setCodigoSV("");
        setCodigoVI("");
        setEstatusActual("");

    }

    /**
     * @return the tipoSoV
     */
    public boolean isTipoSoV() {
        return tipoSoV;
    }

    /**
     * @param tipo the tipoSoV to set
     */
    public void setTipoSoV(boolean tipo) {
        this.tipoSoV = tipo;
    }

    /**
     * @return the codigoSV
     */
    public String getCodigoSV() {
        return codigoSV;
    }

    /**
     * @param codigo the codigoSV to set
     */
    public void setCodigoSV(String codigo) {
        this.codigoSV = codigo;
    }

    /**
     * @return the sv
     */
    public SolicitudViajeVO getSv() {
        return sv;
    }

    /**
     * @param sv the sv to set
     */
    public void setSv(SolicitudViajeVO sv) {
        this.sv = sv;
    }

    /**
     * @return the vi
     */
    public List<ViajeVO> getVi() {
        return vi;
    }

    /**
     * @param vi the vi to set
     */
    public void setVi(List<ViajeVO> vi) {
        this.vi = vi;
    }

    /**
     * @return the fechaSalida
     */
    public String getFechaSalida() {
        return fechaSalida;
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    /**
     * @return the fechaRegreso
     */
    public String getFechaRegreso() {
        return fechaRegreso;
    }

    /**
     * @param fechaRegreso the fechaRegreso to set
     */
    public void setFechaRegreso(String fechaRegreso) {
        this.fechaRegreso = fechaRegreso;
    }

    public void buscarSV() {
        String cod = FacesUtils.getRequestParameter("textSerchSV").toUpperCase();

        List<SolicitudViajeVO> lsv = new ArrayList<>();
        if (cod != null && !cod.isEmpty()) {
            lsv = solicitudViajeImpl.buscarPorCodigo(cod, Constantes.FALSE);
            if (lsv != null && !lsv.isEmpty()) {
                setSv(lsv.get(0));
                if (getSv() != null) {
                    setCodigoSV(getSv().getCodigo());
                    setFechaSalida(Constantes.FMT_ddMMyyy.format(
                            getSv().getFechaSalida()) + " a las " + Constantes.FMT_hmm_a.format(getSv().getHoraSalida()));
                    if (getSv().getFechaRegreso() != null) {
                        setFechaRegreso(Constantes.FMT_ddMMyyy.format(
                                getSv().getFechaRegreso()) + " a las " + Constantes.FMT_hmm_a.format(getSv().getHoraRegreso()));
                    }

                    setVi(viajeImpl.traerViajesPorSolicitud(getSv().getIdSolicitud()));
                    setLea(estatusAprobacionImpl.EstatusBySolicitud(getSv().getIdSolicitud()));
                } else {
                    limpiarSV();
                    FacesUtils.addErrorMessage("no se encontro la Solicitud, verifique que se encuentre bien escrito el código");
                    
                }
            } else {
                limpiarSV();
                FacesUtils.addErrorMessage("no se encontro la Solicitud, verifique que se encuentre bien escrito el código");
            }
        } else {
            limpiarSV();
            FacesUtils.addErrorMessage("debe de escribir un código para poder realizar la búsqueda");
        }
        setTipoSoV(Constantes.TRUE);

    }

    public void buscarVI() {
        String cod = FacesUtils.getRequestParameter("textSerchVI").toUpperCase();

        if (cod != null && !cod.isEmpty()) {
            setViajeBuscar(viajeImpl.buscarPorCodigo(cod));
            if (getViajeBuscar() != null) {
                setCodigoVI(getViajeBuscar().getCodigo());
                setEstatusActual(getViajeBuscar().getEstatus());
            } else {
                limpiarVi();
                FacesUtils.addErrorMessage("no se encontro el viaje, verifique que se encuentre bien escrito el código");
            }

        } else {
            limpiarVi();
            FacesUtils.addErrorMessage("no se encontro el viaje, verifique que se encuentre bien escrito el código");
        }
        setTipoSoV(Constantes.FALSE);
    }

    /**
     * @return the lea
     */
    public List<EstatusAprobacionSolicitudVO> getLea() {
        return lea;
    }

    /**
     * @param lea the lea to set
     */
    public void setLea(List<EstatusAprobacionSolicitudVO> lea) {
        this.lea = lea;
    }

    /**
     * @return the codigoVI
     */
    public String getCodigoVI() {
        return codigoVI;
    }

    /**
     * @param codigoVI the codigoVI to set
     */
    public void setCodigoVI(String codigoVI) {
        this.codigoVI = codigoVI;
    }

    /**
     * @return the estatusActual
     */
    public String getEstatusActual() {
        return estatusActual;
    }

    /**
     * @param estatusActual the estatusActual to set
     */
    public void setEstatusActual(String estatusActual) {
        this.estatusActual = estatusActual;
    }

    /**
     * @return the viajeBuscar
     */
    public ViajeVO getViajeBuscar() {
        return viajeBuscar;
    }

    /**
     * @param viajeBuscar the viajeBuscar to set
     */
    public void setViajeBuscar(ViajeVO viajeBuscar) {
        this.viajeBuscar = viajeBuscar;
    }
    
    public void limpiarSV(){
        setLea(null);
        setCodigoSV("");
        setFechaSalida("");
        setFechaRegreso("");
        setSv(null);
        setVi(null);
    }
    
    public void limpiarVi(){
        setViajeBuscar(null);
        setCodigoVI("");
        setEstatusActual("");
        
    }

}
