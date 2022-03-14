/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import sia.administracion.bean.model.CancelarReenviarOrdenModel;
import sia.modelo.Orden;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.servicios.requisicion.impl.RequisicionDetalleImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@ManagedBean(name = "cancelarReenviarOrdenBean")
@ViewScoped
public class CancelarReenviarOrdenBean implements Serializable {

    @ManagedProperty(value = "#{cancelarReenviarOrdenModel}")
    private CancelarReenviarOrdenModel cancelarReenviarOrdenModel;
    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    @EJB
    private RequisicionDetalleImpl requisicionDetalleImpl;

    /**
     * Creates a new instance of CancelarReenviarOrdenBean
     */
    public CancelarReenviarOrdenBean() {
    }

    @PostConstruct
    public void llenaCampo() {
        cancelarReenviarOrdenModel.iniciarCampo();
    }

    public void init() {
        if (cancelarReenviarOrdenModel != null) {
            setOrden(null);
            setUsuarioSolicita("");
            setConsecutivo("");
        }
    }

    public void buscarOrden() {
        cancelarReenviarOrdenModel.setOrden(cancelarReenviarOrdenModel.buscarOrden(getConsecutivo().trim()));
        if (cancelarReenviarOrdenModel.getOrden() != null) {
            cancelarReenviarOrdenModel.setMostrar(false);
        } else {
            cancelarReenviarOrdenModel.setMostrar(true);
        }

    }

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeEvent) {
        Integer var = (Integer) valueChangeEvent.getNewValue();
        if (var != null) {
            cancelarReenviarOrdenModel.setIdCampo(var);
            UtilLog4j.log.info(this, "campo: " + cancelarReenviarOrdenModel.getIdCampo());
            cancelarReenviarOrdenModel.setOrden(null);
        }

    }

    public List<SelectItem> getListaCampoPorUsuarioTareas() {
        return cancelarReenviarOrdenModel.listaCampoTareas();
    }
    
    public List<SelectItem> getListaCampoPorUsuarioCancelar() {
        return cancelarReenviarOrdenModel.listaCampoCancelar();
    }

    public void cancelarOrdenCompraDR() {
        cancelarReenviarOrdenModel.setModal(true);
        cancelarReenviarOrdenModel.setCancelReq(false);
    }

    public void cancelarOrdenCompraSDR() {
        cancelarReenviarOrdenModel.setModal(true);
        cancelarReenviarOrdenModel.setCancelReq(true);
    }

    public void devolverOrdenCompra() {
        cancelarReenviarOrdenModel.setMostrarDev(true);
    }

    public void usuarioListener(ValueChangeEvent textChangeEvent) {
        if (textChangeEvent.getComponent() instanceof SelectInputText) {
            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
            String cadenaDigitada = (String) textChangeEvent.getNewValue();

            cancelarReenviarOrdenModel.setListaSelect(cancelarReenviarOrdenModel.regresaUsuarioActivo(cadenaDigitada));//, -1, "nombre", true, null, false));

            if (autoComplete.getSelectedItem() != null) {
                CampoUsuarioPuestoVo usuaroiSel = (CampoUsuarioPuestoVo) autoComplete.getSelectedItem().getValue();
                cancelarReenviarOrdenModel.setUsuarioSolicita(usuaroiSel.getUsuario());
//                this.u = usuaroiSel.getNombre();
            }
        }
    }

    public void completarCancelacionOrden() {
        try {
            if (getMotivo().length() > 10) {
                boolean puedoCancelarReq = this.cancelarReenviarOrdenModel.puedoCancelarRequisicion();
                boolean puedoRegresarReq = this.requisicionDetalleImpl.tieneInvArticulo(this.cancelarReenviarOrdenModel.getOrden().getRequisicion().getId(), 
                        true);
                if ((puedoCancelarReq || !this.cancelarReenviarOrdenModel.isCancelReq()) 
                        && (puedoRegresarReq || this.cancelarReenviarOrdenModel.isCancelReq()) 
                        && this.cancelarReenviarOrdenModel.completarCancelacionOrden()) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Se canceló correctamente la orden de C/S ").append(cancelarReenviarOrdenModel.getOrden().getConsecutivo());

                    if (cancelarReenviarOrdenModel.isCancelReq()) {
                        msg.append(" junto con su requisición.");
                    } else {
                        msg.append(" y la requisición fue devuelta. ");
                    }
                    FacesUtils.addInfoMessage(msg.toString());
                    cancelarReenviarOrdenModel.setOrden(null);
                    cancelarReenviarOrdenModel.setUsuarioSolicita("");
                    setConsecutivo("");
                    this.toggleModal(event);
                } else if(!puedoCancelarReq){
                    FacesUtils.addErrorMessage("No se puede cancelar la requisición, ya que esta cuenta con órdenes de compra en proceso.");
                } else if(!this.cancelarReenviarOrdenModel.isCancelReq() && !puedoRegresarReq){
                    FacesUtils.addErrorMessage("No se puede devolver la requisición, ya que esta no utiliza el catálogo de articulos.");
                } else {
                    FacesUtils.addInfoMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
                }
            } else {
                FacesUtils.addErrorMessage("Por favor escriba un motivo de más de 10 caracteres");
            }
        } catch (Exception e) {
            FacesUtils.addInfoMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
            Logger.getLogger(CancelarReenviarOrdenBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void completarDevolucionOrden() {
        try {
            if (getMotivoDev().length() > 10) {
                if (this.cancelarReenviarOrdenModel.completarDevoluccionOrden()) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Se devolvió correctamente la orden de C/S ").append(cancelarReenviarOrdenModel.getOrden().getConsecutivo());


                    FacesUtils.addInfoMessage(msg.toString());
                    cancelarReenviarOrdenModel.setOrden(null);
                    cancelarReenviarOrdenModel.setUsuarioSolicita("");
                    setConsecutivo("");
                    this.toggleMostrarDev(event);
                } else {
                    FacesUtils.addInfoMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
                }
            } else {
                FacesUtils.addErrorMessage("Por favor escriba un motivo de más de 10 caracteres");
            }
        } catch (Exception e) {
            FacesUtils.addInfoMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
            Logger.getLogger(CancelarReenviarOrdenBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void reeviarOrden() {
        try {
            boolean v;
            v = cancelarReenviarOrdenModel.reenviarOrden(getOrden());
            if (v) {
                setOrden(null);
                FacesUtils.addInfoMessage("Se envío la OC/S, favor de verificar.");
                setConsecutivo("");
            } else {
                FacesUtils.addInfoMessage("No se realizó el envio de la  OC/S, por favor verifique los datos del proveedor.");
            }
        } catch (Exception ex) {
            Logger.getLogger(sia.administracion.bean.backing.CancelarReenviarOrdenBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addInfoMessage("No se realizó el envio de la  OC/S, por favor verifique los datos del proveedor.");
        }
    }

    public void reeviarCodigos() {
        try {
            boolean v;
            v = cancelarReenviarOrdenModel.reenviarCodigos(getOrden());
            if (v) {
                setOrden(null);
                FacesUtils.addInfoMessage("Se enviaron los còdigos NAVISION de la OC/S, favor de verificar.");
                setConsecutivo("");
            } else {
                FacesUtils.addInfoMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
            }
        } catch (Exception ex) {
            Logger.getLogger(sia.administracion.bean.backing.CancelarReenviarOrdenBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addInfoMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
        }
    }

    public void generarExcel() {
        try {
            boolean v = false;
//            v = cancelarReenviarOrdenModel.generarExcel(getOrden());
            if (v) {
                setOrden(null);
                FacesUtils.addInfoMessage("Se enviaron los còdigos NAVISION de la OC/S, favor de verificar.");
                setConsecutivo("");
            } else {
                FacesUtils.addInfoMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
            }
        } catch (Exception ex) {
            Logger.getLogger(sia.administracion.bean.backing.CancelarReenviarOrdenBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
        }
    }

    /**
     * *
     *
     * @return
     */
    public void cambiarAnalistaOCS() {
        if (cancelarReenviarOrdenModel.getOrden() != null) {
            if (cancelarReenviarOrdenModel.cambiarAnalistaOCS()) {
                FacesUtils.addInfoMessage("Se cambió la OC/S al analista seleccionado, favor de verificar.");
                setOrden(null);
                setConsecutivo("");
                setUsuarioSolicita("");
            } else {
                FacesUtils.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
            }
        } else {
            FacesUtils.addInfoMessage("Es encesario buscar la OC/S.");
        }

    }

    public List<SelectItem> getListaAnalista() {
        return cancelarReenviarOrdenModel.listaAnalista();
    }

    //Propiededades

    public String getMotivo() {
        return cancelarReenviarOrdenModel.getMotivo();
    }

    public void setMotivo(String motivo) {
        cancelarReenviarOrdenModel.setMotivo(motivo);
    }

    public void toggleModal() {
        cancelarReenviarOrdenModel.setModal(!cancelarReenviarOrdenModel.isModal());
        cancelarReenviarOrdenModel.setUsuarioSolicita(null);
    }

    public boolean isModal() {
        return cancelarReenviarOrdenModel.isModal();
    }

    public void setModal(boolean modal) {
        cancelarReenviarOrdenModel.setModal(modal);
    }

    public void setCancelarReenviarOrdenModel(CancelarReenviarOrdenModel cancelarReenviarOrdenModel) {
        this.cancelarReenviarOrdenModel = cancelarReenviarOrdenModel;
    }

    public String getConsecutivo() {
        return cancelarReenviarOrdenModel.getConsecutivo();
    }

    public void setConsecutivo(String consecutivo) {
        cancelarReenviarOrdenModel.setConsecutivo(consecutivo);
    }

    public Orden getOrden() {
        return cancelarReenviarOrdenModel.getOrden();
    }

    public void setOrden(Orden orden) {
        cancelarReenviarOrdenModel.setOrden(orden);
    }

    public boolean isMostrar() {
        return cancelarReenviarOrdenModel.isMostrar();
    }

    public void setMostrar(boolean mostrar) {
        cancelarReenviarOrdenModel.setMostrar(mostrar);
    }

    /**
     * @return the usuarioSolicita
     */
    public String getUsuarioSolicita() {
        return cancelarReenviarOrdenModel.getUsuarioSolicita();
    }

    /**
     * @param usuarioSolicita the usuarioSolicita to set
     */
    public void setUsuarioSolicita(String usuarioSolicita) {
        cancelarReenviarOrdenModel.setUsuarioSolicita(usuarioSolicita);
    }

    /**
     * @return the listaSelect
     */
    public List<SelectItem> getListaSelect() {
        return cancelarReenviarOrdenModel.getListaSelect();
    }

    /**
     * @param listaSelect the listaSelect to set
     */
    public void setListaSelect(List<SelectItem> listaSelect) {
        cancelarReenviarOrdenModel.setListaSelect(listaSelect);
    }

    /**
     * @return the opcionUsuario
     */
    public String getOpcionUsuario() {
        return cancelarReenviarOrdenModel.getOpcionUsuario();
    }

    /**
     * @param opcionUsuario the opcionUsuario to set
     */
    public void setOpcionUsuario(String opcionUsuario) {
        cancelarReenviarOrdenModel.setUsuarioSolicita(opcionUsuario);
    }

    /**
     * @return the mostrarDev
     */
    public boolean isMostrarDev() {
        return cancelarReenviarOrdenModel.isMostrarDev();
    }

    /**
     * @param mostrarDev the mostrarDev to set
     */
    public void setMostrarDev(boolean mostrarDev) {
        this.cancelarReenviarOrdenModel.setMostrarDev(mostrarDev);
    }

    /**
     * @return the motivoDev
     */
    public String getMotivoDev() {
        return cancelarReenviarOrdenModel.getMotivoDev();
    }

    /**
     * @param motivoDev the motivoDev to set
     */
    public void setMotivoDev(String motivoDev) {
        this.cancelarReenviarOrdenModel.setMotivoDev(motivoDev);
    }

    public void toggleMostrarDev() {
        cancelarReenviarOrdenModel.setMostrarDev(!cancelarReenviarOrdenModel.isMostrarDev());
        cancelarReenviarOrdenModel.setUsuarioSolicita(null);
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return cancelarReenviarOrdenModel.getIdCampo();
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        cancelarReenviarOrdenModel.setIdCampo(idCampo);
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
     * @return the idGerenciaCompra
     */
    public int getIdGerenciaCompra() {
        return cancelarReenviarOrdenModel.getIdGerenciaCompra();
    }

    /**
     * @param idGerenciaCompra the idGerenciaCompra to set
     */
    public void setIdGerenciaCompra(int idGerenciaCompra) {
        cancelarReenviarOrdenModel.setIdGerenciaCompra(idGerenciaCompra);
    }
}
