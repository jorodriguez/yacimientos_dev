/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.catalogos.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.dominio.vo.RolVO;
import lector.dominio.vo.UsuarioRolVo;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.servicios.sistema.impl.SiRolImpl;
import lector.servicios.sistema.impl.SiUsuarioRolImpl;
import lombok.Getter;
import lombok.Setter;
import lector.sistema.bean.backing.Sesion;
import lector.sistema.bean.support.FacesUtils;

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
        //listaUsuario = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(idCampo);
    }

    public void limpiarRol() {                
        setIdModulo(-1);
        setIdRol(-1);
        setNombreUsuario("");
        setLista(new ArrayList<>());
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
        roles.clear();
    }

    public void guardarUsuarioRol() {
        if (getIdModulo() > 0) {
            if (getIdRol() > 0) {
                if (!getNombreUsuario().isEmpty()) {
                    if (buscarUsuarioRol()) {
                        boolean v = siUsuarioRolImpl.guardarUsuarioRol(getIdRol(), getNombreUsuario(), isPrincipal(), sesion.getUsuarioSesion().getId(), getIdCampo());
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
        lista = siUsuarioRolImpl.traerUsuarioPorRolModulo(idrol, getIdModulo(), getIdCampo());
    }

}
