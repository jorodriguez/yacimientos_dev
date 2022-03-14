/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.requisicion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.catalogos.bean.backing.UsuarioBean;
import sia.inventarios.service.InvCadenaAprobacionImpl;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@ManagedBean(name = "cadenaAprobacionAlmacenBean")
@ViewScoped
public class CadenaAprobacionAlmacenBean implements Serializable{

    /**
     * Creates a new instance of CadenaAprobacionAlmacenBean
     */
    public CadenaAprobacionAlmacenBean() {
    }

    @ManagedProperty(value = "#{usuarioBean}")
    private UsuarioBean usuarioBean;

    private List<CadenaAprobacionVo> cadenas;

    @EJB
    InvCadenaAprobacionImpl cadenaAprobacionLocal;
    @EJB
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;

    private List<SelectItem> usuarios;
    private List<UsuarioVO> listaUsuaarios;
    private CadenaAprobacionVo cadenaAprobacionVo;

    @PostConstruct
    public void iniciar() {
        usuarios = new ArrayList<SelectItem>();
        listaUsuaarios = new ArrayList<UsuarioVO>();
        cadenas = new ArrayList<CadenaAprobacionVo>();
        //
        llenar();
        //
        listaUsuaarios = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(usuarioBean.getUsuarioVO().getIdCampo());
    }

    private void llenar() {
        cadenas = cadenaAprobacionLocal.traerPorCampo(usuarioBean.getUsuarioVO().getIdCampo());
    }

    public void crearCadena() {
        cadenaAprobacionVo = new CadenaAprobacionVo();
        PrimeFaces.current().executeScript("$(dialogoCadenaAprobacion).modal('show');");
    }

    public void seleccionarSolicita(ValueChangeEvent event) {
        try {
            usuarios.clear();
            if (event.getComponent() instanceof SelectInputText) {
                SelectInputText autoComplete = (SelectInputText) event.getComponent();
                String cadenaDigitada = (String) event.getNewValue();
                for (UsuarioVO userVo : listaUsuaarios) {
                    if (userVo.getNombre().toLowerCase().contains(cadenaDigitada.toLowerCase())) {
                        usuarios.add(new SelectItem(userVo));
                    }
                }
                if (autoComplete.getSelectedItem() != null) {
                    UsuarioVO uSolVo = (UsuarioVO) autoComplete.getSelectedItem().getValue();
                    cadenaAprobacionVo.setIdSolicita(uSolVo.getId());
                    cadenaAprobacionVo.setSolicita(uSolVo.getNombre());
                    //
                    autoComplete.resetValue();
                    autoComplete.clearInitialState();
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    public void seleccionarAutoriza(ValueChangeEvent event) {
        try {
            usuarios.clear();
            if (event.getComponent() instanceof SelectInputText) {
                SelectInputText autoComplete = (SelectInputText) event.getComponent();
                String cadenaDigitada = (String) event.getNewValue();
                for (UsuarioVO userVo : listaUsuaarios) {
                    if (userVo.getNombre().toLowerCase().contains(cadenaDigitada.toLowerCase())) {
                        usuarios.add(new SelectItem(userVo));
                    }
                }
                if (autoComplete.getSelectedItem() != null) {
                    UsuarioVO uAuto = (UsuarioVO) autoComplete.getSelectedItem().getValue();
                    cadenaAprobacionVo.setIdAprueba(uAuto.getId());
                    cadenaAprobacionVo.setAprueba(uAuto.getNombre());
                    //
                    autoComplete.resetValue();
                    autoComplete.clearInitialState();
                }
            }
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
    }

    public void guardar() {
        if (cadenaAprobacionVo.getIdSolicita() != null && !cadenaAprobacionVo.getIdSolicita().isEmpty()) {
            if (cadenaAprobacionVo.getIdAprueba() != null && !cadenaAprobacionVo.getIdAprueba().isEmpty()) {
                //
                cadenaAprobacionLocal.guardar(getCadenaAprobacionVo(), usuarioBean.getUsuarioVO().getIdCampo(), usuarioBean.getUsuarioVO().getId());
                //
                llenar();
                //
                PrimeFaces.current().executeScript("$(dialogoCadenaAprobacion).modal('hide');");
            } else {
                FacesUtils.addErrorMessage("Es necesario agregar autorizador.");
            }
        } else {
            FacesUtils.addErrorMessage("Es necesario agregar al solicitante.");
        }
    }

    public void eliminarCadena() {
        int idCad = Integer.parseInt(FacesUtils.getRequestParameter("idCad"));
        //
        cadenaAprobacionLocal.eliminar(idCad, usuarioBean.getUsuarioVO().getId());
        //
        llenar();
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    /**
     * @return the cadenas
     */
    public List<CadenaAprobacionVo> getCadenas() {
        return cadenas;
    }

    /**
     * @param cadenas the cadenas to set
     */
    public void setCadenas(List<CadenaAprobacionVo> cadenas) {
        this.cadenas = cadenas;
    }

    /**
     * @return the usuarios
     */
    public List<SelectItem> getUsuarios() {
        return usuarios;
    }

    /**
     * @param usuarios the usuarios to set
     */
    public void setUsuarios(List<SelectItem> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * @return the cadenaAprobacionVo
     */
    public CadenaAprobacionVo getCadenaAprobacionVo() {
        return cadenaAprobacionVo;
    }

    /**
     * @param cadenaAprobacionVo the cadenaAprobacionVo to set
     */
    public void setCadenaAprobacionVo(CadenaAprobacionVo cadenaAprobacionVo) {
        this.cadenaAprobacionVo = cadenaAprobacionVo;
    }

}
