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

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RevisarOrdenBean implements Serializable {

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
    @Getter
    @Setter
    private List<UsuarioVO> usuarios;
    @Getter
    @Setter
    private List<String> usuariosFiltrados;

    @PostConstruct
    public void iniciar() {
        usuarios = new ArrayList<>();
        usuariosFiltrados = new ArrayList<>();
        usuarioVO = new UsuarioVO();
        usuarioVO.setIdCampo(sesion.getUsuario().getApCampo().getId());
        listaRevisa = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, usuarioVO.getIdCampo(), Constantes.NO_ELIMINADO);
        listaCampoUsuario();
        traerUsuarios();
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

    public void cambiarSeleccionCampo() {
        listaRevisa = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, usuarioVO.getIdCampo(), Constantes.NO_ELIMINADO);
    }

    public void agregarAprobadorOrdenCompra() {
        PrimeFaces.current().executeScript(";$(dialogoAgregarRev).modal('show');");
    }

    public List<String> completarUsuario(String cadena) {
        usuariosFiltrados.clear();
        usuarios.stream().filter(us -> us.getNombre().toUpperCase().contains(cadena.toUpperCase())).forEach(u -> {
            usuariosFiltrados.add(u.getNombre());
        });
        return usuariosFiltrados;
    }

    public void traerUsuarios() {
        usuarios = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(usuarioVO.getIdCampo());
    }

    public void eliminarUsuarioApruebaOC(int idRev) {
        ocFlujoImpl.eliminar(sesion.getUsuario().getId(), idRev);
        listaRevisa = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, usuarioVO.getIdCampo(), Constantes.NO_ELIMINADO);
    }

    public void guardarUsuarioApruebaOC() {
        if (!getUsuarioVO().getNombre().trim().isEmpty()) {
            UsuarioVO usVo = usuarioImpl.findByName(usuarioVO.getNombre());
            if (usVo != null) {
                usuarioImpl.aprobarOrdenCompra(sesion.getUsuario().getId(), usVo.getId(), usuarioVO.getIdCampo(), Constantes.OCFLUJO_ACTION_REVISAR);
                listaRevisa = ocFlujoImpl.getUsuariosPorAccion(Constantes.OCFLUJO_ACTION_REVISAR, usuarioVO.getIdCampo(), Constantes.NO_ELIMINADO);
                usuarioVO.setNombre("");
                PrimeFaces.current().executeScript(";$(dialogoAgregarRev).modal('hide');");
            }
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
