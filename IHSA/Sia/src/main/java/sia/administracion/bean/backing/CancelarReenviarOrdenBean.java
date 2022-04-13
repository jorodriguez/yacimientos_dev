/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.SelectEvent;
import sia.modelo.Orden;
import sia.modelo.Usuario;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.requisicion.impl.RequisicionDetalleImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named
@ViewScoped
public class CancelarReenviarOrdenBean implements Serializable {

    @Inject
    private Sesion sesion;
    @Inject
    private RequisicionDetalleImpl requisicionDetalleImpl;

    @Inject
    private OrdenImpl ordenServicioRemoto;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenServicioRemoto;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    //
    //
    @Setter
    @Getter
    private Orden orden;
    @Setter
    @Getter
    private String consecutivo;
    @Setter
    @Getter
    private boolean mostrar = false;
    @Setter
    @Getter
    private boolean modal = false;
    @Setter
    @Getter
    private boolean cancelReq = false;
    @Setter
    @Getter
    private boolean mostrarDev = false;
    @Setter
    @Getter
    private String motivoDev;
    @Setter
    @Getter
    private String motivo;
    @Setter
    @Getter
    private String usuarioSolicita;
    @Setter
    @Getter
    private String opcionUsuario = "compra";
    @Setter
    @Getter
    private List<Usuario> listaUsuarios;
    //
    @Setter
    @Getter
    private int idCampo;
    @Setter
    @Getter
    private int idGerenciaCompra;

    /**
     * Creates a new instance of CancelarReenviarOrdenBean
     */
    public CancelarReenviarOrdenBean() {
    }

    @PostConstruct
    public void llenaCampo() {
        listaUsuarios = new ArrayList<>();
        listaUsuarios = usuarioImpl.getActivos();
        setIdCampo(sesion.getUsuario().getApCampo().getId());
        setOrden(null);
        setOrden(null);
        setUsuarioSolicita("");
        setConsecutivo("");
    }

    public void buscarOrden() {
        setOrden(buscarOrden(getConsecutivo().trim()));
        if (getOrden() != null) {
            setMostrar(false);
        } else {
            setMostrar(true);
        }

    }

    public Orden buscarOrden(String consecutivo) {
        try {
            return this.ordenServicioRemoto.buscarPorConsecutivoBloque(consecutivo, getIdCampo(), this.sesion.getUsuario().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepción al buscar la OC/S - - -" + e.getMessage());
        }
        return null;

    }

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeEvent) {
        Integer var = (Integer) valueChangeEvent.getNewValue();
        if (var != null) {
            setIdCampo(var);
            UtilLog4j.log.info(this, "campo: " + getIdCampo());
            setOrden(null);
        }

    }

    public void cancelarOrdenCompraDR() {
        setModal(true);
        setCancelReq(false);
    }

    public void cancelarOrdenCompraSDR() {
        setModal(true);
        setCancelReq(true);
    }

    public void devolverOrdenCompra() {
        setMostrarDev(true);
    }

    public List<String> usuarioListener(String texto) {
        String queryLowerCase = texto.toLowerCase();
        List<String> usuarioFiltrados = new ArrayList<>();
        List<Usuario> usuarios = listaUsuarios;
        for (Usuario users : usuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(queryLowerCase)).collect(Collectors.toList());

    }

    public void onItemSelectSolicita(SelectEvent<String> event) {
        setUsuarioSolicita(event.getObject());
    }

    public void completarCancelacionOrden() {
        try {
            if (getMotivo().length() > 10) {
                boolean puedoCancelarReq = this.puedoCancelarRequisicion();
                boolean puedoRegresarReq = this.requisicionDetalleImpl.tieneInvArticulo(this.getOrden().getRequisicion().getId(),
                        true);
                if ((puedoCancelarReq || !this.isCancelReq())
                        && (puedoRegresarReq || this.isCancelReq())
                        && ordenServicioRemoto.cancelarOrden(getOrden(),  getUsuarioSolicita(), sesion.getUsuario().getId(), (Object) getMotivo(), true, isCancelReq())) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Se canceló correctamente la orden de C/S ").append(getOrden().getConsecutivo());

                    if (isCancelReq()) {
                        msg.append(" junto con su requisición.");
                    } else {
                        msg.append(" y la requisición fue devuelta. ");
                    }
                    FacesUtils.addInfoMessage(msg.toString());
                    setOrden(null);
                    setUsuarioSolicita("");
                    setConsecutivo("");
                } else if (!puedoCancelarReq) {
                    FacesUtils.addErrorMessage("No se puede cancelar la requisición, ya que esta cuenta con órdenes de compra en proceso.");
                } else if (!this.isCancelReq() && !puedoRegresarReq) {
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

    public boolean puedoCancelarRequisicion() {
        boolean cancelar = true;
        try {
            List<Object[]> l = this.ordenServicioRemoto.getOrdenesPorRequisicion(getOrden().getRequisicion().getId(), "and e.id <> 100");
            if (!l.isEmpty() && l.size() > 1) {
                for (Object[] objects : l) {
                    if (((Integer) objects[0]) != getOrden().getId() && this.ordenServicioRemoto.find((Integer) objects[0]).getAutorizacionesOrden().getEstatus().getId() > 100) {
                        cancelar = false;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return cancelar;
    }

    private void completarDevolucionOrden() {
        try {
            if (getMotivoDev().length() > 10) {
                if (ordenServicioRemoto.devolverOrden(getOrden(), getUsuarioSolicita(), sesion.getUsuario().getId(),  getMotivoDev())) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("Se devolvió correctamente la orden de C/S ").append(getOrden().getConsecutivo());

                    FacesUtils.addInfoMessage(msg.toString());
                    setOrden(null);
                    setUsuarioSolicita("");
                    setConsecutivo("");
                    //toggleMostrarDev();
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
            
            v = ordenServicioRemoto.reenviarOrdenCompras(orden, this.sesion.getUsuario());//(getOrden());
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
            v = ordenServicioRemoto.reenviarCodigos(orden);
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
//            v = generarExcel(getOrden());
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
        if (getOrden() != null) {
            if (ordenServicioRemoto.cambiarAnalistaOCS(sesion.getUsuario().getId(), getOrden().getId(), getUsuarioSolicita())) {
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

}
