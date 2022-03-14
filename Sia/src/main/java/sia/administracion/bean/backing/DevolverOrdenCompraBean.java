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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.administracion.bean.model.DevolverOrdenCompraModel;
import sia.constantes.Constantes;
import sia.modelo.Orden;
import sia.modelo.Usuario;
import sia.sistema.bean.support.FacesUtils;
import sia.util.SolicitudMaterialEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@ManagedBean
@ViewScoped
public class DevolverOrdenCompraBean implements Serializable {

    /**
     * Creates a new instance of DevolverOrdenCompraBean
     */
    public DevolverOrdenCompraBean() {
    }

    @PostConstruct
    public void iniciar() {
        devolverOrdenCompraModel.iniciar();
        traerUsuarioJson();
    }

    @ManagedProperty(value = "#{devolverOrdenCompraModel}")
    private DevolverOrdenCompraModel devolverOrdenCompraModel;

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeEvent) {
        Integer var = (Integer) valueChangeEvent.getNewValue();
        if (var != null) {
            devolverOrdenCompraModel.setIdCampo(var);
            UtilLog4j.log.info(this, "campo: " + devolverOrdenCompraModel.getIdCampo());
            traerUsuarioJson();
        }

    }

    public void buscarOrden() {
        devolverOrdenCompraModel.setOrden(this.devolverOrdenCompraModel.buscarOrden(getConsecutivo().trim()));
        if (devolverOrdenCompraModel.getOrden() != null) {
            devolverOrdenCompraModel.setMostrar(false);
        } else {
            devolverOrdenCompraModel.setMostrar(true);
        }
    }

    public void devolverOrdenCompra() {
        devolverOrdenCompraModel.setModal(true);
    }

    public void usuarioListener(ValueChangeEvent textChangeEvent) {
        if (textChangeEvent.getComponent() instanceof SelectInputText) {
            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
            String cadenaDigitada = (String) textChangeEvent.getNewValue();

            devolverOrdenCompraModel.setListaSelect(devolverOrdenCompraModel.regresaUsuarioActivo(cadenaDigitada));//, -1, "nombre", true, null, false));

            if (autoComplete.getSelectedItem() != null) {
                Usuario usuaroiSel = (Usuario) autoComplete.getSelectedItem().getValue();
                devolverOrdenCompraModel.setUsuarioSolicita(usuaroiSel.getNombre());
//                this.u = usuaroiSel.getNombre();
            }
        }
    }

    public void completarDevolucionOrden() {
        boolean v = false;
        try {
            if (devolverOrdenCompraModel.verificaUsuarioSolicita()) {
                if (getMotivo().length() > 10) {
                    v = this.devolverOrdenCompraModel.completarDevolucionOrden(); // ultimas comillas usuario solicita la devolucion
                    if (v) {
                        FacesUtils.addInfoMessage("Se devolvió la OC/S " + devolverOrdenCompraModel.getOrden().getConsecutivo());
                        devolverOrdenCompraModel.setOrden(null);
                        devolverOrdenCompraModel.setUsuarioSolicita("");
                        this.toggleModal();
                    } else {
                        FacesUtils.addInfoMessage("Ocurrio un error . . . ");
                    }
                } else {
                    FacesUtils.addErrorMessage("Por favor escriba un motivo de más de 10 caracteres");
                }
            } else {
                FacesUtils.addErrorMessage("No se encontro el usuario en el SIA");
            }
        } catch (Exception ex) {
            Logger.getLogger(DevolverOrdenCompraBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addInfoMessage("Ocurrio un error. Por favor notifique el problema a: sia@ihsa.mx");
        }
    }
    ///

    public void buscarOrdenReenviar() {
        if (devolverOrdenCompraModel.getConsecutivo().trim().isEmpty()) {
////////            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle(""));
            FacesUtils.addInfoMessage("Ingrese un consecutivo");
        } else {
            devolverOrdenCompraModel.setOrden(devolverOrdenCompraModel.buscarOrden(devolverOrdenCompraModel.getConsecutivo()));
            if (devolverOrdenCompraModel.getOrden() == null) {
                devolverOrdenCompraModel.setOrden(null);
                FacesUtils.addInfoMessage("OC/S no encontrada.");
            } else {
            }
        }
    }

    public void reeviarOrden() {
        try {
            boolean v;
            v = devolverOrdenCompraModel.reenviarOrden(getOrden());
            if (v) {
                setOrden(null);
                FacesUtils.addInfoMessage("Se envío la OC/S, favor de verificar.");
            } else {
                FacesUtils.addInfoMessage("No se realizó el envio de la  OC/S, por favor verifique los datos del proveedor.");
            }
        } catch (Exception ex) {
            Logger.getLogger(DevolverOrdenCompraBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesUtils.addInfoMessage("No se realizó el envio de la  OC/S, por favor verifique los datos del proveedor.");
        }
    }

    /////////////////////////////////////
    public void cambiarTipoTrabajo(ValueChangeEvent event) {
        devolverOrdenCompraModel.setTipoTrabajo((String) event.getNewValue());
        if (devolverOrdenCompraModel.getTipoTrabajo().equals("ocs")) {
            devolverOrdenCompraModel.setIdStatus(Constantes.ESTATUS_PENDIENTE_R);
        } else if (devolverOrdenCompraModel.getTipoTrabajo().equals("req")) {
            devolverOrdenCompraModel.setIdStatus(Constantes.REQUISICION_REVISADA);
        } else if (devolverOrdenCompraModel.getTipoTrabajo().equals("inv")) {
            devolverOrdenCompraModel.setIdStatus(SolicitudMaterialEstadoEnum.POR_AUTORIZAR.getId());
        } else {
            devolverOrdenCompraModel.setIdStatus(Constantes.ESTATUS_APROBAR);
        }
        devolverOrdenCompraModel.setLista(null);
        devolverOrdenCompraModel.setUsuarioAprobara("");
        devolverOrdenCompraModel.setUsuarioSolicita("");
    }

    private void traerUsuarioJson() {
        devolverOrdenCompraModel.setUsuarioAprobara("");
        devolverOrdenCompraModel.setUsuarioSolicita("");
        devolverOrdenCompraModel.setLista(null);
        String datos = devolverOrdenCompraModel.traerUsuarioJson();
        PrimeFaces.current().executeScript(";llenarJsonUsuario(" + datos + ");");
    }

    public List<SelectItem> getListaCampo() {
        return devolverOrdenCompraModel.listaCampo();
    }

    public void enviarReporteCompradores() {
        if (devolverOrdenCompraModel.enviarReporteCompradores()) {
            FacesUtils.addInfoMessage("Se enviaron los reportes a la gerencias de Compras");
        } else {
            FacesUtils.addInfoMessage("No se encontraron datos para el reporte de compra");
        }
    }

    //
    public List<SelectItem> getListaEstatus() {
        return devolverOrdenCompraModel.listaEstatus();
    }

    public List<SelectItem> getListaEstatusRequisicion() {
        return devolverOrdenCompraModel.listaEstatusRequisicion();
    }

    public void buscarOrdenCompraServicio() {
        devolverOrdenCompraModel.buscarOrdenCompraServicio();
    }

    public void buscarRequisiones() {
        devolverOrdenCompraModel.buscarRequisiones();
    }

    public void buscarSolicitudesMaterial() {
        devolverOrdenCompraModel.buscarSolicitudesMaterial();
    }

    public void pasarSolicitudMaterial() {
        devolverOrdenCompraModel.pasarSolicitudMaterial();
    }

    public void pasarOrdenesCompra() {
        devolverOrdenCompraModel.pasarOrdenesCompra();
    }

    public void pasarRequisiciones() {
        devolverOrdenCompraModel.pasarRequisiciones();
    }

    //Sol viaje
    public void buscarSolicitud() {
        devolverOrdenCompraModel.buscarSolicitud();
    }

    public void pasarSolicitudes() {
        devolverOrdenCompraModel.pasarSolicitudes();
    }

    //Propiededades
    public String getMotivo() {
        return devolverOrdenCompraModel.getMotivo();
    }

    public void setMotivo(String motivo) {
        devolverOrdenCompraModel.setMotivo(motivo);
    }

    public void toggleModal() {
        devolverOrdenCompraModel.setModal(!devolverOrdenCompraModel.isModal());
        devolverOrdenCompraModel.setUsuarioSolicita(null);
    }

    public boolean isModal() {
        return devolverOrdenCompraModel.isModal();
    }

    public void setModal(boolean modal) {
        devolverOrdenCompraModel.setModal(modal);
    }

    public void setDevolverOrdenCompraModel(DevolverOrdenCompraModel devolverOrdenCompraModel) {
        this.devolverOrdenCompraModel = devolverOrdenCompraModel;
    }

    public String getConsecutivo() {
        return devolverOrdenCompraModel.getConsecutivo();
    }

    public void setConsecutivo(String consecutivo) {
        devolverOrdenCompraModel.setConsecutivo(consecutivo);
    }

    public Orden getOrden() {
        return devolverOrdenCompraModel.getOrden();
    }

    public void setOrden(Orden orden) {
        devolverOrdenCompraModel.setOrden(orden);
    }

    public boolean isMostrar() {
        return devolverOrdenCompraModel.isMostrar();
    }

    public void setMostrar(boolean mostrar) {
        devolverOrdenCompraModel.setMostrar(mostrar);
    }

    /**
     * @return the usuarioSolicita
     */
    public String getUsuarioSolicita() {
        return devolverOrdenCompraModel.getUsuarioSolicita();
    }

    /**
     * @param usuarioSolicita the usuarioSolicita to set
     */
    public void setUsuarioSolicita(String usuarioSolicita) {
        devolverOrdenCompraModel.setUsuarioSolicita(usuarioSolicita);
    }

    /**
     * @return the listaSelect
     */
    public List<SelectItem> getListaSelect() {
        return devolverOrdenCompraModel.getListaSelect();
    }

    /**
     * @param listaSelect the listaSelect to set
     */
    public void setListaSelect(List<SelectItem> listaSelect) {
        devolverOrdenCompraModel.getListaSelect();
    }

    /**
     * @return the opcionUsuario
     */
    public String getOpcionUsuario() {
        return devolverOrdenCompraModel.getOpcionUsuario();
    }

    /**
     * @param opcionUsuario the opcionUsuario to set
     */
    public void setOpcionUsuario(String opcionUsuario) {
        devolverOrdenCompraModel.setUsuarioSolicita(opcionUsuario);
    }

    /**
     * @return the idStatus
     */
    public int getIdStatus() {
        return devolverOrdenCompraModel.getIdStatus();
    }

    /**
     * @param idStatus the idStatus to set
     */
    public void setIdStatus(int idStatus) {
        devolverOrdenCompraModel.setIdStatus(idStatus);
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return devolverOrdenCompraModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        devolverOrdenCompraModel.setLista(lista);
    }

    /**
     * @return the usuarioAprobara
     */
    public String getUsuarioAprobara() {
        return devolverOrdenCompraModel.getUsuarioAprobara();
    }

    /**
     * @param usuarioAprobara the usuarioAprobara to set
     */
    public void setUsuarioAprobara(String usuarioAprobara) {
        devolverOrdenCompraModel.setUsuarioAprobara(usuarioAprobara);
    }

    /**
     * @return the idTipoTrabajo
     */
    public String getTipoTrabajo() {
        return devolverOrdenCompraModel.getTipoTrabajo();
    }

    /**
     * @param idTipoTrabajo the idTipoTrabajo to set
     */
    public void setTipoTrabajo(String tipoTrabajo) {
        devolverOrdenCompraModel.setTipoTrabajo(tipoTrabajo);
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return devolverOrdenCompraModel.getIdCampo();
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        devolverOrdenCompraModel.setIdCampo(idCampo);
    }
}
