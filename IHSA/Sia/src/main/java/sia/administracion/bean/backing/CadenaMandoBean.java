/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.excepciones.SIAException;
import sia.modelo.Usuario;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.requisicion.impl.CadenasMandoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class CadenaMandoBean implements Serializable {

    @Setter
    @Inject
    Sesion sesion;
    @Inject
    CadenasMandoImpl cadenasMandoServicioRemoto;
    @Inject
    ApCampoImpl apCampoImpl;
    @Inject
    UsuarioImpl usuarioImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Getter
    @Setter
    List<CadenaAprobacionVo> listaCadena;

    @Getter
    @Setter
    private List<UsuarioVO> listaUsuarios;
    @Getter
    @Setter
    private String usuario;
    @Getter
    @Setter
    private String solicita;
    @Getter
    @Setter
    private String revisa;
    @Getter
    @Setter
    private String aprueba;
    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    private String campo;
    @Getter
    @Setter
    private int idOrdena = 1;
    @Getter
    @Setter
    private CadenaAprobacionVo cadenaAprobacionVo;
    @Getter
    @Setter
    private List<SelectItem> campos;

    /**
     * Creates a new instance of CadenaMando
     */
    public CadenaMandoBean() {
    }

    @PostConstruct
    public void iniciar() {
        campos = new ArrayList<>();
        listaUsuarios = new ArrayList<>();
        setIdCampo(sesion.getUsuarioVo().getIdCampo());
        listaCampo();
        listaUsuarios = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(idCampo);
        setListaCadena(cadenasMandoServicioRemoto.traerCadenaAprobacion(null, getIdCampo(), 1, false, false, false));
    }

    private void listaCampo() {
        List<CampoUsuarioPuestoVo> lc;
        lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioVo().getId());
        for (CampoUsuarioPuestoVo ca : lc) {
            campos.add(new SelectItem(ca.getIdCampo(), ca.getCampo()));
        }
    }

    public void irCadenaMando() {
        setIdCampo(1);
        setIdOrdena(1);
    }

    public void cambiarValorCampo() {
        setUsuario(null);
        setIdOrdena(1);
        llenarListaCadena();
        listaUsuarios.clear();
        listaUsuarios = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(idCampo);
    }

    /*
    public void cambiarValorOrdena(ValueChangeEvent valueChangeEvent) {
        setIdOrdena((Integer) valueChangeEvent.getNewValue());
        UtilLog4j.log.info(this, "Operacion: " + getIdOrdena());
        if (getListaCadena().getRowCount() == 0) {
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.cadena.aprobacion.no.existe"));
        }
    }

    public void buscarCadena() {
        //    getTraerCadena();
        if (getListaCadena().getRowCount() < 1) {
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.cadena.aprobacion.no.existe"));
        }
//        this.agregar = "False";
        setCadenaAprobacionVo(null);
    }
     */
    public void agregarCadenaMando() {
        setUsuario(null);
        setRevisa(null);
        setAprueba(null);
        setCadenaAprobacionVo(null);
        //
        ApCampoVo cvo = apCampoImpl.buscarPorId(idCampo);
        campo = cvo.getNombre();
    }

    public void registroCadenaMando() {
        boolean v;
        try {
            v = cadenasMandoServicioRemoto.registroCadenaMando(getIdCampo(), getUsuario(), getRevisa(), getAprueba(), sesion.getUsuarioVo().getId());
            if (v) {
                setUsuario(null);
                setRevisa(null);
                setAprueba(null);
                llenarListaCadena();
                PrimeFaces.current().executeScript("PF('dialogoAgregarCad').hide();");
            } else {
                FacesUtils.addInfoMessage(new SIAException().getMessage());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion: " + e.getMessage());
        }
    }

    public void completarModificacion() {
        cadenasMandoServicioRemoto.completarModificacion(getCadenaAprobacionVo().getId(), getRevisa(), getAprueba(), sesion.getUsuarioVo().getId());
        //
        PrimeFaces.current().executeScript("PF('dialogoModificarCad').hide();");
    }

    public void cancelarRegistroCadenaMando() {
        setRevisa(null);
        setAprueba(null);
        PrimeFaces.current().executeScript("PF('dialogoAgregarCad').hide();");
    }

    public void modificarCadena(int idCad) {
        setCadenaAprobacionVo(cadenasMandoServicioRemoto.traerPorId(idCad));
        UtilLog4j.log.info(this, "Ca: " + getCadenaAprobacionVo().getSolicita() + getRevisa());
        setRevisa("");
        setAprueba("");

    }

    public void cancelarModificacion() {
        setCadenaAprobacionVo(null);
        setRevisa(null);
        setAprueba(null);
        PrimeFaces.current().executeScript("PF('dialogoModificarCad').hide();");
    }

    public void eliminarCadena(int idCad) {
        setCadenaAprobacionVo(new CadenaAprobacionVo());
        getCadenaAprobacionVo().setId(idCad);
        cadenasMandoServicioRemoto.eliminar(getCadenaAprobacionVo().getId(), sesion.getUsuarioVo().getId());
        llenarListaCadena();
        setCadenaAprobacionVo(null);
    }

    public List<String> usuarioListener(String texto) {
        List<String> usuarioFiltrados = new ArrayList<>();
        for (UsuarioVO users : listaUsuarios) {
            usuarioFiltrados.add(users.getNombre());
        }

        return usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(texto.toLowerCase())).collect(Collectors.toList());
    }

    public void onItemSelect(SelectEvent<String> event) {
        setUsuario(event.getObject());
    }

    public void onItemSelectSolicita(SelectEvent<String> event) {
        setSolicita(event.getObject());
    }

    public void onItemSelectRevisa(SelectEvent<String> event) {
        setRevisa(event.getObject());
    }

    //Fin
    public void llenarListaCadena() {
        setListaCadena(this.cadenasMandoServicioRemoto.traerCadenaAprobacion(null, getIdCampo(), getIdOrdena(), false, false, false));
    }
}
