/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.SiModuloVo;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author mluis
 */
@Named(value = "rolBean")
@ViewScoped
public class RolBean implements Serializable {

    /**
     * Creates a new instance of RolBean
     */
    public RolBean() {
    }

    @Inject
    private Sesion sesion;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;

    @Inject
    private SiModuloImpl siModuloImpl;
    @Inject
    private SiRolImpl siRolImpl;
    //
    @Getter
    @Setter
    private int idModulo;
    @Getter
    @Setter
    private int idRol;

    @Getter
    @Setter
    private String nombreUsuario;
    @Getter
    @Setter
    private String idUsuario;
    @Getter
    @Setter
    private List<UsuarioVO> listaUsuario;
    @Getter
    @Setter
    private List<String> usuariosFiltrados;
    @Getter
    @Setter
    private List<UsuarioRolVo> lista;
    @Getter
    @Setter
    private boolean principal = false;
    @Getter
    @Setter
    private boolean viewAll = false;
    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    private String rfcCompania;
    @Getter
    @Setter
    private List<SelectItem> modulos;
    @Getter
    @Setter
    private List<SelectItem> roles;
    @Getter
    @Setter
    private List<SelectItem> campos;

    @PostConstruct
    public void iniciarIdCampo() {
        modulos = new ArrayList<>();
        roles = new ArrayList<>();
        campos = new ArrayList<>();
        usuariosFiltrados = new ArrayList<>();
        listaUsuario = new ArrayList<>();
        setLista(new ArrayList<>());
        setIdCampo(sesion.getUsuario().getApCampo().getId());
        setRfcCompania(regresaRfcCompania());
        //
        listaUsuario = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(idCampo);
        llenarModulo();
        listaCampoPorUsuario();
    }

    public void limpiarRol() {
        setIdCampo(sesion.getUsuario().getApCampo().getId());
        setRfcCompania(regresaRfcCompania());
        setIdModulo(-1);
        setIdRol(-1);
        setNombreUsuario("");
        setLista(new ArrayList<>());
    }

    public String regresaRfcCompania() {
        return sesion.getUsuario().getApCampo().getCompania().getRfc();
    }

    public void llenarModulo() {
        modulos.clear();
        for (SiModuloVo siModuloVo : siModuloImpl.getAllSiModuloList("nombre", true, false)) {
            SelectItem item = new SelectItem(siModuloVo.getId(), siModuloVo.getNombre());
            modulos.add(item);
        }
    }

    public void listaCampoPorUsuario() {
        for (CampoUsuarioPuestoVo cuVo : apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioVo().getId())) {
            SelectItem item = new SelectItem(cuVo.getIdCampo(), cuVo.getCampo());
            campos.add(item);
        }
    }

    public void listaRol() {
        roles.clear();
        for (RolVO rolVO : siRolImpl.traerRol(idModulo)) {
            SelectItem item = new SelectItem(rolVO.getId(), rolVO.getNombre());
            roles.add(item);
        }
    }

    public List<String> usuarioListener(String cadena) {
        usuariosFiltrados.clear();
        listaUsuario.stream().filter(us -> us.getNombre().toUpperCase().startsWith(cadena.toUpperCase())).forEach(u -> {
            usuariosFiltrados.add(u.getNombre());
        });
        return usuariosFiltrados;
    }

    public void buscarRol() {
        lista = siUsuarioRolImpl.traerUsuarioPorRolModulo(getIdRol(), getIdModulo(), getIdCampo());
    }

    public void cambiarModulo() {
        listaRol();
    }

    public void cambiarSeleccionCampo() {
        llenarModulo();
        roles.clear();
    }

    public void guardarUsuarioRol() {
        if (getIdModulo() > 0) {
            if (getIdRol() > 0) {
                if (!getNombreUsuario().isEmpty()) {
                    if (buscarUsuarioRol()) {
                        boolean v = siUsuarioRolImpl.guardarUsuarioRol(getIdRol(), getNombreUsuario(), isPrincipal(), sesion.getUsuario().getId(), getIdCampo());
                        if (v) {
                            setIdModulo(-1);
                            setIdRol(-1);
                            setNombreUsuario("");
                            buscarRol();
                            FacesUtils.addInfoMessage("Se agregó correctamente el nuevo rol.");
                        }
                    } else {
                        FacesUtils.addErrorMessage("Ya existe el usuario con el rol seleccionado");
                    }
                } else {
                    FacesUtils.addErrorMessage("Agregue un usuario");
                }
            } else {
                FacesUtils.addErrorMessage("Seleccione un rol");
            }
        } else {
            FacesUtils.addErrorMessage("Seleccione un módulo");
        }
    }

    public boolean buscarUsuarioRol() {
        UsuarioRolVo uvo = siUsuarioRolImpl.findNombreUsuarioRolVO(getIdRol(), getNombreUsuario(), getIdCampo());
        return uvo == null;
    }

    public void eliminarUsuarioRol(int idUrol, int idrol) {
        siUsuarioRolImpl.eliminarUsuarioRol(idUrol, sesion.getUsuario().getId());
        lista = siUsuarioRolImpl.traerUsuarioPorRolModulo(idrol, getIdModulo(), getIdCampo());
    }

}
