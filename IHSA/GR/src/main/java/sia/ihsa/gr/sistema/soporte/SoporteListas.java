/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.gr.sistema.soporte;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.gr.vo.MapaVO;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.gr.impl.GrMapaImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.util.UtilLog4j;


/**
 *
 * @author ihsa
 */
@Named(value = "soporteListas")
@ViewScoped
public class SoporteListas implements Serializable{
        
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private SgInvitadoImpl sgInvitadoImpl;
    @Inject
    private GrMapaImpl grMapaImpl;

    private List<CampoUsuarioPuestoVo> listaUsuario = null;    
    private List<InvitadoVO> listaInvitado = null; 
    private List<MapaVO> zonas = null;

    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada, int idApCampo) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (CampoUsuarioPuestoVo p : this.getUsuarioActivo(idApCampo)) {
            if (p.getUsuario() != null) {
                String cadenaPersona = p.getUsuario().toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (cadenaPersona.contains(cadenaDigitada)) {
                    SelectItem item = new SelectItem(p, p.getUsuario());
                    list.add(item);
                }
            }
        }
        return list;
    }
    
    public List<SelectItem> regresaInvitadosActivo(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (InvitadoVO p : this.getInvitadosActivo()) {
            if (p.getNombre()!= null) {
                String cadenaPersona = p.getNombre().toLowerCase();
                cadenaDigitada = cadenaDigitada.toLowerCase();
                if (cadenaPersona.contains(cadenaDigitada)) {
                    SelectItem item = new SelectItem(p, p.getNombre());
                    list.add(item);
                }
            }
        }
        return list;
    }
    
    public List<SelectItem> regresaZonas(String cadenaDigitada) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        for (MapaVO p : this.getZonasbyCodigo(null)) {
            if (p.getNombre()!= null && p.getCodigo()!= null) {
                String cadenaZona = new StringBuilder().append(p.getNombre()).append(" - ").append(p.getCodigo()).toString().toUpperCase();
                cadenaDigitada = cadenaDigitada.toUpperCase();
                if (cadenaZona.contains(cadenaDigitada)) {
                    SelectItem item = new SelectItem(p.getCodigo(), cadenaZona);
                    list.add(item);
                }
            }
        }
        return list;
    }

    public List<CampoUsuarioPuestoVo> getUsuarioActivo(int idApCampo) {
        UsuarioVO vo = new UsuarioVO();
        setListaUsuario(apCampoUsuarioRhPuestoImpl.traerUsurioPorCampo(idApCampo,vo));
        UtilLog4j.log.info(this, "Usuario: " + getListaUsuario().size() + " completa");
        return getListaUsuario();
    }
    
    public List<InvitadoVO> getInvitadosActivo() {        
        setListaInvitado(sgInvitadoImpl.buscarInvitado());
        UtilLog4j.log.info(this, "Invitados: " + getListaInvitado().size() + " completa");
        return getListaInvitado();
    }
    
    public List<MapaVO> getZonasbyCodigo(String codigo) {        
        setZonas(grMapaImpl.getMapas(codigo));
        UtilLog4j.log.info(this, "zonasRuta: " + getZonas().size() + " completa");
        return getZonas();
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

    /**
     * @return the listaInvitado
     */
    public List<InvitadoVO> getListaInvitado() {
        return listaInvitado;
    }

    /**
     * @param listaInvitado the listaInvitado to set
     */
    public void setListaInvitado(List<InvitadoVO> listaInvitado) {
        this.listaInvitado = listaInvitado;
    }

    /**
     * @return the zonas
     */
    public List<MapaVO> getZonas() {
        return zonas;
    }

    /**
     * @param zonas the zonas to set
     */
    public void setZonas(List<MapaVO> zonas) {
        this.zonas = zonas;
    }
}

