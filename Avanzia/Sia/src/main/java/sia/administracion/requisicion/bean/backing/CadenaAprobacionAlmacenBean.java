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
import javax.inject.Inject;




import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
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
@Named(value = "cadenaAprobacionAlmacenBean")
@ViewScoped
public class CadenaAprobacionAlmacenBean implements Serializable {

    /**
     * Creates a new instance of CadenaAprobacionAlmacenBean
     */
    public CadenaAprobacionAlmacenBean() {
    }

    @Inject
    private UsuarioBean usuarioBean;

    private List<CadenaAprobacionVo> cadenas;

    @Inject
    InvCadenaAprobacionImpl cadenaAprobacionLocal;
    @Inject
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

    public List<String> seleccionarSolicita(String cadena) {
        try {
            List<String> usrs = new ArrayList<>();
            usuarios.clear();
            for (UsuarioVO userVo : listaUsuaarios) {
                if (userVo.getNombre().toLowerCase().contains(cadena.toLowerCase())) {
                    usrs.add(userVo.getNombre());
                }
            }
            return usrs;
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
        return null;
    }

    public void onItemSelectSolicita(SelectEvent<String> event) {
        cadenaAprobacionVo.setSolicita(event.getObject());
    }

    public List<String> seleccionarAutoriza(String cadena) {
        try {
            List<String> usrs = new ArrayList<>();
            usuarios.clear();
            for (UsuarioVO userVo : listaUsuaarios) {
                if (userVo.getNombre().toLowerCase().contains(cadena.toLowerCase())) {
                    usrs.add(userVo.getNombre());
                }
            }
            return usrs;
        } catch (Exception ex) {
            UtilLog4j.log.error(ex);
        }
        return null;
    }

    public void onItemSelectAprueba(SelectEvent<String> event) {
        cadenaAprobacionVo.setAprueba(event.getObject());
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
