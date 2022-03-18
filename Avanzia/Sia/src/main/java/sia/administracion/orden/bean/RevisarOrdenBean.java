/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.orden.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;




import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.extern.log4j.Log4j;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.usuario.vo.UsuarioTipoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcFlujoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author mluis
 */
@Named(value = "revisarOrdenBean")
@ViewScoped
@Log4j
public class RevisarOrdenBean implements Serializable {

    /**
     * Creates a new instance of RevisarOrdenBean
     */
    public RevisarOrdenBean() {
    }

    @Inject
    private Sesion sesion;

    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private OcFlujoImpl ocFlujoImpl;
    //
    private UsuarioVO usuarioVO;
    private List<UsuarioTipoVo> listaRevisa;
    private List<SelectItem> listaCampo;

    @PostConstruct
    public void iniciar() {
        usuarioVO = new UsuarioVO();
        usuarioVO.setIdCampo(sesion.getUsuario().getApCampo().getId());
        listaRevisa = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, usuarioVO.getIdCampo(), Constantes.NO_ELIMINADO);
        listaCampoUsuario();
    }

    public void listaCampoUsuario() {
        List<CampoUsuarioPuestoVo> lc;
        listaCampo = new ArrayList<>();
        try {
            lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuario().getId());
            for (CampoUsuarioPuestoVo ca : lc) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                listaCampo.add(item);
            }
        } catch (Exception e) {
            log.warn("", e);
        }
    }

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null) {
            listaRevisa = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, Integer.parseInt(valueChangeEvent.getNewValue().toString()), Constantes.NO_ELIMINADO);
        }
    }

    public void agregarAprobadorOrdenCompra() {
        traerUsuarioJson();
        PrimeFaces.current().executeScript(";$(dialogoAgregarRev).modal('show');");
    }

    public void traerUsuarioJson() {
        PrimeFaces.current().executeScript(";llenarJsonUsuario(" + apCampoUsuarioRhPuestoImpl.traerUsuarioJsonPorCampo(usuarioVO.getIdCampo()) + ");");
    }

    public void eliminarUsuarioApruebaOC() {
        int idRev = Integer.parseInt(FacesUtils.getRequestParameter("idRev"));
        ocFlujoImpl.eliminar(sesion.getUsuario().getId(), idRev);
        listaRevisa = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, usuarioVO.getIdCampo(), Constantes.NO_ELIMINADO);
    }

    public void guardarUsuarioApruebaOC() {
        if (!getUsuarioVO().getNombre().trim().isEmpty()) {
            usuarioImpl.aprobarOrdenCompra(sesion.getUsuario().getId(), usuarioVO.getNombre(), usuarioVO.getIdCampo(), Constantes.OCFLUJO_ACTION_REVISAR);
            PrimeFaces.current().executeScript(";$(dialogoAgregarRev).modal('hide');");
            listaRevisa = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, usuarioVO.getIdCampo(), Constantes.NO_ELIMINADO);
            usuarioVO.setNombre("");
        } else {
            FacesUtils.addErrorMessage("frmRevOcs", FacesUtils.getKeyResourceBundle("sia.usuario.necesario"));
        }
    }

    public void cancelarUsuarioApruebaOC() {
        usuarioVO.setNombre("");
        PrimeFaces.current().executeScript(";$(dialogoAgregarRev).modal('hide');");
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the usuarioVO
     */
    public UsuarioVO getUsuarioVO() {
        return usuarioVO;
    }

    /**
     * @param usuarioVO the usuarioVO to set
     */
    public void setUsuarioVO(UsuarioVO usuarioVO) {
        this.usuarioVO = usuarioVO;
    }

    /**
     * @return the listaRevisa
     */
    public List<UsuarioTipoVo> getListaRevisa() {
        return listaRevisa;
    }

    /**
     * @param listaRevisa the listaRevisa to set
     */
    public void setListaRevisa(List<UsuarioTipoVo> listaRevisa) {
        this.listaRevisa = listaRevisa;
    }

    /**
     * @return the listaCampo
     */
    public List<SelectItem> getListaCampo() {
        return listaCampo;
    }

    /**
     * @param listaCampo the listaCampo to set
     */
    public void setListaCampo(List<SelectItem> listaCampo) {
        this.listaCampo = listaCampo;
    }

}
