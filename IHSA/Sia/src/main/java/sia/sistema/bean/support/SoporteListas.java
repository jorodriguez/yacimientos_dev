/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sistema.bean.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.CompaniaVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.servicios.sistema.vo.SiModuloVo;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
//@ViewScoped
//Named(value = "soporteListas")
//@CustomScoped(value = "#{window}")
@Named
@SessionScoped
public class SoporteListas implements Serializable{

    @Inject
    private SiModuloImpl siModuloImpl;
    @Inject
    private SiRolImpl siRolImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    //  
    private List<CampoUsuarioPuestoVo> listaUsuario = null;
    private List<UsuarioVO> listaUsuarioActivo = null;
//

    public List<SelectItem> listaModulo() {
        try {
            List<SelectItem> ls = new ArrayList<SelectItem>();
            for (SiModuloVo siModuloVo : siModuloImpl.getAllSiModuloList("nombre", true, false)) {
                SelectItem item = new SelectItem(siModuloVo.getId(), siModuloVo.getNombre());
                ls.add(item);
            }
            return ls;
        } catch (Exception e) {
            return null;
        }
    }
    
    
    public List<SelectItem> listaBloquePorUsuario(String idSesion) {
        try {
            List<SelectItem> ls = new ArrayList<SelectItem>();
            for (CampoUsuarioPuestoVo cuVo : apCampoUsuarioRhPuestoImpl.getAllPorUsurio(idSesion)) {
                SelectItem item = new SelectItem(cuVo.getIdCampo(), cuVo.getCampo());
                ls.add(item);
            }
            return ls;
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<SelectItem> listaCompaniaPorUsuario(String idSesion) {
        try {
            List<SelectItem> ls = new ArrayList<SelectItem>();
            for (CompaniaVo cuVo : usuarioImpl.traerCompaniaPorUsuario(idSesion)) {
                SelectItem item = new SelectItem(cuVo.getRfcCompania(), cuVo.getNombre());
                ls.add(item);
            }
            return ls;
        } catch (Exception e) {
            return null;
        }
    }
    
    

    public List<SelectItem> listaGerencias() {
        try {
            List<SelectItem> ls = new ArrayList<SelectItem>();
            for (GerenciaVo g : gerenciaImpl.traerTodasGerencia()) {
                SelectItem item = new SelectItem(g.getId(), g.getNombre());
                ls.add(item);
            }
            return ls;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error ; " + e.getMessage());
            return null;
        }
    }

    public List<SelectItem> listaRoles(int idModulo) {
        try {
            List<SelectItem> ls = new ArrayList<SelectItem>();
            for (RolVO rolVO : siRolImpl.traerRol(idModulo)){
                SelectItem item = new SelectItem(rolVO.getId(), rolVO.getNombre());
                ls.add(item);
            }
            return ls;
        } catch (Exception e) {
            return null;
        }
    }

    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada, int idApCampo) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        cadenaDigitada = cadenaDigitada.toUpperCase();
        for (CampoUsuarioPuestoVo p : this.getUsuarioActivo(cadenaDigitada, idApCampo)) {
            if (p.getUsuario() != null) {                                
                    SelectItem item = new SelectItem(p, p.getUsuario());
                    list.add(item);                
            }
        }
        return list;
    }

    public List<SelectItem> regresaTodosUsuarioActivo(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (UsuarioVO p : traerUsuarioActivo()) {
            if (p.getNombre() != null) {
                String cadenaPersona = p.getNombre().toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (cadenaPersona.contains(cadenaDigitada)) {
                    SelectItem item = new SelectItem(p.getId(), p.getNombre());
                    list.add(item);
                }
            }
        }
        return list;
    }

    public List<CampoUsuarioPuestoVo> getUsuarioActivo(String cadena, int idApCampo) {        
        setListaUsuario(apCampoUsuarioRhPuestoImpl.traerUsurioEnCampoPorCadena(cadena, idApCampo));
        UtilLog4j.log.info(this, "Usuario: " + getListaUsuario().size() + " completa");
        return getListaUsuario();
    }

    public List<UsuarioVO> traerUsuarioActivo() {
        if (listaUsuarioActivo == null) {
            listaUsuarioActivo = new ArrayList<UsuarioVO>();
            listaUsuarioActivo = usuarioImpl.findAll(-1, -1, Boolean.TRUE, "nombre", true, false);
            UtilLog4j.log.info(this, "Usuario: " + listaUsuarioActivo + " completa");
        }

        return listaUsuarioActivo;
    }

    /**
     * @return the listaUsuario
     */
    public List<CampoUsuarioPuestoVo> getListaUsuario() {
        return listaUsuario;
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<CampoUsuarioPuestoVo> listaUsuario) {
        this.listaUsuario = listaUsuario;
    }
}
