/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;


import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.modelo.sgl.vo.OrdenDetalleVO;
import sia.modelo.sgl.vo.OrdenVO;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenDetalleImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.OrdenEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named (value = "recibirOrdenBean")
@ViewScoped
public class RecibirOrdenBean implements Serializable {

    /**
     * Creates a new instance of RecibirOrdenBean
     */
    public RecibirOrdenBean() {
    }
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;
    @Inject
    private OrdenDetalleImpl ordenDetalleImpl;
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    
    @Inject
    private UsuarioBean sesion;

    private List<OrdenVO> listaOrden;
    private List<OrdenVO> listaOrdenParcial;
    private List<OrdenVO> listaOrdenParcialEliminar;
    private List<OrdenDetalleVO> listaOrdenDetalle;
    private List<OrdenDetalleVO> listaOrdenDetalleEliminar;
    private OrdenVO ordenVO;
    private int idPartidaCancelar;
    private int idOrdenPartidaCancelar;
    private String motivoCancelar;
    private boolean puedeEliminar;
    private String msgError;

    @PostConstruct
    public void iniciar() {
        ordenesPorRecibir();
        setOrdenVO(new OrdenVO());
        puedeEliminar = siUsuarioRolImpl.buscarRolPorUsuarioModulo(
                sesion.getUsuarioConectado().getId(), 
                Constantes.MODULO_COMPRA, 
                Constantes.CODIGO_ROL_ELIMINAR_PARTIDAS,
                sesion.getUsuarioConectado().getApCampo().getId());
    }

    /**
     */
    public void ordenesPorRecibir() {
        try {
            if (sesion.getMapaRoles().containsKey("Recepción de oc/s")) {
                listaOrden = autorizacionesOrdenImpl.traerOrdenSinTerminar(OrdenEstadoEnum.POR_RECIBIR.getId(),
                        sesion.getUsuarioConectado().getApCampo().getId(), Constantes.VACIO, false);
                //
                listaOrdenParcial = autorizacionesOrdenImpl.traerOrdenSinTerminar(OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(),
                        sesion.getUsuarioConectado().getApCampo().getId(), Constantes.VACIO, false);                
                
                setListaOrdenParcialEliminar(autorizacionesOrdenImpl.traerOrdenSinTerminar(OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(),
                        sesion.getUsuarioConectado().getApCampo().getId(), Constantes.VACIO, true));                
                
            } else {
                listaOrden = autorizacionesOrdenImpl.traerOrdenSinTerminar(OrdenEstadoEnum.POR_RECIBIR.getId(),
                        sesion.getUsuarioConectado().getApCampo().getId(), sesion.getUsuarioConectado().getId(), false);
                //
                listaOrdenParcial = autorizacionesOrdenImpl.traerOrdenSinTerminar(OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(),
                        sesion.getUsuarioConectado().getApCampo().getId(), sesion.getUsuarioConectado().getId(), false);                
                
                setListaOrdenParcialEliminar(autorizacionesOrdenImpl.traerOrdenSinTerminar(OrdenEstadoEnum.RECIBIDA_PARCIAL.getId(),
                        sesion.getUsuarioConectado().getApCampo().getId(), sesion.getUsuarioConectado().getId(), true));                
            }
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void seleccionarOrdenRecibida(int id) {
        //
        ordenVO = ordenImpl.buscarOrdenPorId(id, sesion.getUsuarioConectado().getApCampo().getId(), false);
        getOrdenVO().setFecha(new Date());
        getOrdenVO().setDetalleOrden(new ArrayList<OrdenDetalleVO>());
        listaOrdenDetalle = ordenDetalleImpl.itemsPorOrden(id);
        //
        PrimeFaces.current().executeScript( ";abrirDialogoModal(dialogoRecibirOCS);");
    }
    
    public void seleccionarOrdenRecibidaEliminar(int id) {        
        this.setIdOrdenPartidaCancelar(id);
        ordenVO = ordenImpl.buscarOrdenPorId(this.getIdOrdenPartidaCancelar(), sesion.getUsuarioConectado().getApCampo().getId(), false);
        getOrdenVO().setFecha(new Date());
        getOrdenVO().setDetalleOrden(new ArrayList<OrdenDetalleVO>());
        listaOrdenDetalleEliminar = ordenDetalleImpl.itemsPorOrdenEliminar(this.getIdOrdenPartidaCancelar());
        msgError = null;
        PrimeFaces.current().executeScript( ";abrirDialogoModal(dialogoRecibirOCSEliminar);");
    }

    public void recibirOrdenCompras() {
        try {
            ordenVO.setDetalleOrden(new ArrayList<OrdenDetalleVO>());
            ordenVO.setDetalleOrden(listaOrdenDetalle);
            autorizacionesOrdenImpl.marcarOrdenRecibida(sesion.getUsuarioConectado().getId(), ordenVO);
            ordenesPorRecibir();
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarOcsSinRecibir();
            ordenVO = new OrdenVO();
            FacesUtilsBean.addInfoMessage("Orden de compra recibida.");
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            FacesUtilsBean.addErrorMessage("Ocurrio un error al recibir la orden de compra");
        }
        ordenesPorRecibir();
        PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoRecibirOCS);");
    }
    
    public void eliminarPartida() {
        try {
            if(this.getIdOrdenPartidaCancelar() > 0 && this.getIdPartidaCancelar() > 0){
                if(this.getMotivoCancelar() != null && this.getMotivoCancelar().length() > 50){
                    ordenDetalleImpl.eliminarItem(this.getIdPartidaCancelar(), this.getIdOrdenPartidaCancelar(), 
                            sesion.getUsuarioConectado().getId(), getMotivoCancelar());                    
                    listaOrdenDetalleEliminar = ordenDetalleImpl.itemsPorOrdenEliminar(this.getIdOrdenPartidaCancelar());
                    ordenVO = ordenImpl.buscarOrdenPorId(this.getIdOrdenPartidaCancelar(), sesion.getUsuarioConectado().getApCampo().getId(), false);                    
                    msgError = "Se eliminio correctamente la partida de la orden.";
                    PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoMotivoEliminar);abrirDialogoModal(dialogoRecibirOCSEliminar);");
                } else {
                    msgError = "Al eliminar el item seleccionado ya no podra ocuparlo para generar ordenes de compra, por favor confirme su motivo (minimo 50 caracteres) ";
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            FacesUtilsBean.addErrorMessage("Ocurrio un error al recibir la orden de compra");
        }
    }
    
    public void motivoEliminarPartida(int idPar) {
        try {
            this.setMotivoCancelar("");
            this.setIdPartidaCancelar(idPar);            
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            FacesUtilsBean.addErrorMessage("Ocurrio un error al eliminar la partida de la orden de compra");
        }        
        PrimeFaces.current().executeScript( "abrirDialogoModal(dialogoMotivoEliminar);");
    }

    public void cerrarRecibirOrdenCompras() {
        ordenVO = new OrdenVO();
        PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoRecibirOCS);");
    }
    
    public void cerrarEliminarPartidaOrdenCompras() {   
        msgError = null;
        this.setIdPartidaCancelar(0);
        this.setIdOrdenPartidaCancelar(0);
        this.setMotivoCancelar("");
        PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoRecibirOCSEliminar);");
    }
    
    public void cerrarMotivoEliminarPartidaOrdenCompras() {    
        this.setIdPartidaCancelar(0);
        msgError = null;
        PrimeFaces.current().executeScript( ";cerrarDialogoModal(dialogoMotivoEliminar);");
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the listaOrden
     */
    public List<OrdenVO> getListaOrden() {
        return listaOrden;
    }

    /**
     * @param listaOrden the listaOrden to set
     */
    public void setListaOrden(List<OrdenVO> listaOrden) {
        this.listaOrden = listaOrden;
    }

    /**
     * @return the ordenVO
     */
    public OrdenVO getOrdenVO() {
        return ordenVO;
    }

    /**
     * @param ordenVO the ordenVO to set
     */
    public void setOrdenVO(OrdenVO ordenVO) {
        this.ordenVO = ordenVO;
    }

    /**
     * @return the listaOrdenDetalle
     */
    public List<OrdenDetalleVO> getListaOrdenDetalle() {
        return listaOrdenDetalle;
    }

    /**
     * @param listaOrdenDetalle the listaOrdenDetalle to set
     */
    public void setListaOrdenDetalle(List<OrdenDetalleVO> listaOrdenDetalle) {
        this.listaOrdenDetalle = listaOrdenDetalle;
    }

    /**
     * @return the listaOrdenParcial
     */
    public List<OrdenVO> getListaOrdenParcial() {
        return listaOrdenParcial;
    }

    /**
     * @param listaOrdenParcial the listaOrdenParcial to set
     */
    public void setListaOrdenParcial(List<OrdenVO> listaOrdenParcial) {
        this.listaOrdenParcial = listaOrdenParcial;
    }

    /**
     * @return the listaOrdenDetalleEliminar
     */
    public List<OrdenDetalleVO> getListaOrdenDetalleEliminar() {
        return listaOrdenDetalleEliminar;
    }

    /**
     * @param listaOrdenDetalleEliminar the listaOrdenDetalleEliminar to set
     */
    public void setListaOrdenDetalleEliminar(List<OrdenDetalleVO> listaOrdenDetalleEliminar) {
        this.listaOrdenDetalleEliminar = listaOrdenDetalleEliminar;
    }

    /**
     * @return the idPartidaCancelar
     */
    public int getIdPartidaCancelar() {
        return idPartidaCancelar;
    }

    /**
     * @param idPartidaCancelar the idPartidaCancelar to set
     */
    public void setIdPartidaCancelar(int idPartidaCancelar) {
        this.idPartidaCancelar = idPartidaCancelar;
    }

    /**
     * @return the idOrdenPartidaCancelar
     */
    public int getIdOrdenPartidaCancelar() {
        return idOrdenPartidaCancelar;
    }

    /**
     * @param idOrdenPartidaCancelar the idOrdenPartidaCancelar to set
     */
    public void setIdOrdenPartidaCancelar(int idOrdenPartidaCancelar) {
        this.idOrdenPartidaCancelar = idOrdenPartidaCancelar;
    }

    /**
     * @return the motivoCancelar
     */
    public String getMotivoCancelar() {
        return motivoCancelar;
    }

    /**
     * @param motivoCancelar the motivoCancelar to set
     */
    public void setMotivoCancelar(String motivoCancelar) {
        this.motivoCancelar = motivoCancelar;
    }

    /**
     * @return the puedeEliminar
     */
    public boolean isPuedeEliminar() {
        return puedeEliminar;
    }

    /**
     * @param puedeEliminar the puedeEliminar to set
     */
    public void setPuedeEliminar(boolean puedeEliminar) {
        this.puedeEliminar = puedeEliminar;
    }

    /**
     * @return the msgError
     */
    public String getMsgError() {
        return msgError;
    }

    /**
     * @param msgError the msgError to set
     */
    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }

    /**
     * @return the listaOrdenParcialEliminar
     */
    public List<OrdenVO> getListaOrdenParcialEliminar() {
        return listaOrdenParcialEliminar;
    }

    /**
     * @param listaOrdenParcialEliminar the listaOrdenParcialEliminar to set
     */
    public void setListaOrdenParcialEliminar(List<OrdenVO> listaOrdenParcialEliminar) {
        this.listaOrdenParcialEliminar = listaOrdenParcialEliminar;
    }

}
