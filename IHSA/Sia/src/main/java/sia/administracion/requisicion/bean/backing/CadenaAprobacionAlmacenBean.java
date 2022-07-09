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
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.inventarios.service.InvCadenaAprobacionImpl;
import sia.modelo.Usuario;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.sistema.bean.backing.Sesion;
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
    private Sesion sesion;

    private List<CadenaAprobacionVo> cadenas;

    @Inject
    InvCadenaAprobacionImpl cadenaAprobacionLocal;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    UsuarioImpl usuarioImpl;

    private List<SelectItem> usuarios;
    private List<UsuarioVO> listaUsuaarios;
    private CadenaAprobacionVo cadenaAprobacionVo;
@Getter
@Setter
    private String solicita;
    private String aprueba;
    @PostConstruct
    public void iniciar() {
        usuarios = new ArrayList<>();
        listaUsuaarios = new ArrayList<>();
        cadenas = new ArrayList<>();
        //
        llenar();
        //
        listaUsuaarios = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(sesion.getUsuarioVo().getIdCampo());
    }

    private void llenar() {
        cadenas = cadenaAprobacionLocal.traerPorCampo(sesion.getUsuarioVo().getIdCampo());
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

    public void onItemSelectSolicita() {
        Usuario usSol = usuarioImpl.buscarPorNombre(cadenaAprobacionVo.getSolicita());
        if (usSol != null) {
            cadenaAprobacionVo.setIdSolicita(usSol.getId());
            cadenaAprobacionVo.setSolicita(usSol.getNombre());
        }
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

    public void onItemSelectAprueba() {

        Usuario usSol = usuarioImpl.buscarPorNombre(cadenaAprobacionVo.getAprueba());
        if (usSol != null) {
            cadenaAprobacionVo.setIdAprueba(usSol.getId());
            cadenaAprobacionVo.setAprueba(usSol.getNombre());
        }
    }

    public void guardar() {
        if (cadenaAprobacionVo.getIdSolicita() != null && !cadenaAprobacionVo.getIdSolicita().isEmpty()) {
            if (cadenaAprobacionVo.getIdAprueba() != null && !cadenaAprobacionVo.getIdAprueba().isEmpty()) {
                //
                cadenaAprobacionLocal.guardar(getCadenaAprobacionVo(), sesion.getUsuarioVo().getIdCampo(), sesion.getUsuarioVo().getId());
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

    public void eliminarCadena(int idCad) {
        //
        cadenaAprobacionLocal.eliminar(idCad, sesion.getUsuarioVo().getId());
        //
        llenar();
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
