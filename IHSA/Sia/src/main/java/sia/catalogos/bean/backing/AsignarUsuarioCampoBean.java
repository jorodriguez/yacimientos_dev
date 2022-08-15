/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.catalogos.bean.backing;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.puesto.vo.RhPuestoVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.campo.nuevo.impl.RhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "asignarUsuarioCampoBean")
@ViewScoped
public class AsignarUsuarioCampoBean implements Serializable {

    /**
     * Creates a new instance of AsignarUsuarioCampoBean
     */
    public AsignarUsuarioCampoBean() {
    }
    @Inject
    Sesion sesion;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    GerenciaImpl gerenciaImpl;
    @Inject
    ApCampoImpl apCampoImpl;
    @Inject
    UsuarioImpl usuarioImpl;
    @Inject
    RhPuestoImpl rhPuestoImpl;
    @Getter
    @Setter
    private CampoUsuarioPuestoVo campoUsuarioPuestoVo;
    @Getter
    @Setter
    private RhPuestoVo rhPuestoVo;

    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    private List<CampoUsuarioPuestoVo> lista;
    @Getter
    @Setter
    private List<UsuarioVO> usuarios;
    @Getter
    @Setter
    private List<String> usuariosFiltrados;
    @Getter
    @Setter
    private List<RhPuestoVo> puestos;
    @Getter
    @Setter
    private List<String> puestosFiltrados;
    @Getter
    @Setter
    private int accion;
    @Getter
    @Setter
    private int idGerencia;
    @Getter
    @Setter
    private int idGerenciaRes;
    @Getter
    @Setter
    private String responsableGerencia;
    @Getter
    @Setter
    private String u;
    private String userId;
    @Getter
    @Setter
    private String c;
    @Getter
    @Setter
    private List<SelectItem> lstGerencia;
    @Getter
    @Setter
    private List<SelectItem> listaCamposItems;

    @PostConstruct
    public void iniciar() {
        idCampo = sesion.getIdCampo();
        accion = 1;
        usuarios = new ArrayList<>();
        usuariosFiltrados = new ArrayList<>();
        puestos = new ArrayList<>();
        puestosFiltrados = new ArrayList<>();
        listaCamposItems = new ArrayList<>();
        lstGerencia = new ArrayList<>();
        setCampoUsuarioPuestoVo(new CampoUsuarioPuestoVo());
        getCampoUsuarioPuestoVo().setIdCampo(-1);
        setRhPuestoVo(new RhPuestoVo());
        getRhPuestoVo().setNombre("");
        llenarListaApCamposItems();
        //
        usuarios = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(idCampo);
        puestos = rhPuestoImpl.findAllRhPuesto("nombre", true, true);
        traerCampoUsuario();
    }

    public void agregarCampoUsuario() {
        setCampoUsuarioPuestoVo(new CampoUsuarioPuestoVo());
        getCampoUsuarioPuestoVo().setIdCampo(-1);
        setRhPuestoVo(new RhPuestoVo());
        getRhPuestoVo().setNombre("");
        setC("no");
        setAccion(0);
    }

    public void cambiarSeleccionCampo() {
        traerCampoUsuario();
    }

    public void traerCampoUsuario() {
        setLista(apCampoUsuarioRhPuestoImpl.traerUsurioPorCampo(getIdCampo(), sesion.getUsuarioVo()));
    }

    public void eliminarRelacion(int idCampoUser) {

        apCampoUsuarioRhPuestoImpl.delete(sesion.getUsuarioVo().getId(), idCampoUser);
        setCampoUsuarioPuestoVo(null);
        traerCampoUsuario();
    }

    public void cambiarFiltroHistorial() {
        //setTipoFiltro((String) event.getNewValue());
        if (responsableGerencia.equals("si")) {
            PrimeFaces.current().ajax().update("PF('pnlFiltro').show();");
        }

    }

    public void llenarGerencia() {
        lstGerencia.clear();
        List<GerenciaVo> gereVos;
        gereVos = gerenciaImpl.getAllGerenciaByApCampo(getIdCampo(), "nombre", true, null, false);
        for (GerenciaVo ger : gereVos) {
            SelectItem item = new SelectItem(ger.getId(), ger.getNombre());
            lstGerencia.add(item);
        }
    }

    public void llenarListaApCamposItems() {
        for (CampoVo apCampoVo : apCampoImpl.getAllField()) {
            SelectItem item = new SelectItem(apCampoVo.getId(), apCampoVo.getNombre());
            listaCamposItems.add(item);
        }
    }

    public List<String> usuarioListener(String texto) {
        usuariosFiltrados.clear();
        usuarios.stream().filter(u -> u.getNombre().toUpperCase().startsWith(texto.toUpperCase())).forEach(us -> {
            usuariosFiltrados.add(us.getNombre());
        });
        return usuariosFiltrados;
    }

    public void buscarusuarioPorNombre() {
        UsuarioVO us = usuarioImpl.findByName(getU());
        userId = us.getId();
    }

    public List<String> puestoListener(String texto) {
        puestosFiltrados.clear();
        puestos.stream().filter(p -> p.getNombre().toUpperCase().startsWith(texto.toUpperCase())).forEach(us -> {
            puestosFiltrados.add(us.getNombre());
        });
        return puestosFiltrados;
    }

    public void cambiaValorPuesto() {
        setIdGerencia(-1);
        getRhPuestoVo().setNombre("");
        setResponsableGerencia("no");
        llenarGerencia();
    }

    public void guardarUsuarioCampo() {
        if (getAccion() == 0) {
            if (verificaUsuarioCampoGuardar()) {
                if (getRhPuestoVo() != null) {
                    guardarUsuarioCampo(getIdCampo(), getRhPuestoVo().getId());
                    UtilLog4j.log.info(this, "Guardó");
                    limpiarVariables();
                    PrimeFaces.current().executeScript(";$(dialogoBloqueUsuario).modal('hide');");
                } else {
                    PrimeFaces.current().executeScript(";alertaGeneral('Seleccione el puesto.');");
                }
            } else {
                PrimeFaces.current().executeScript(";alertaGeneral('" + FacesUtils.getKeyResourceBundle("sia.campo.usuario.existe") + "');");
            }
//
        } else if (getAccion() == 1) { //Modificar
            buscarPuestoPorNombre();
//                    if (getRhPuestoVo(   ) != null) {

            apCampoUsuarioRhPuestoImpl.edit(sesion.getUsuarioVo().getId(), getIdCampo(), getCampoUsuarioPuestoVo().getIdUsuario(), getRhPuestoVo().getId(), getCampoUsuarioPuestoVo().getIdCampoUsuarioPuesto());
            UtilLog4j.log.info(this, "Modificó");
            limpiarVariables();
        }
    }

    public RhPuestoVo buscarPuestoPorId() {
        return rhPuestoImpl.findById(getRhPuestoVo().getId(), false);
    }

    public boolean verificaUsuarioCampoGuardar() {
        List<CampoUsuarioPuestoVo> lcup
                = apCampoUsuarioRhPuestoImpl.getCampoPorUsurio(userId, getIdCampo());

        boolean retVal = true;

        for (CampoUsuarioPuestoVo cupv : lcup) {
            if (getCampoUsuarioPuestoVo().getIdCampo() == cupv.getIdCampo()) {
                retVal = false;
                break;
            }
        }
        return retVal;
    }

    public void guardarUsuarioCampo(int campo, int puesto) {
        apCampoUsuarioRhPuestoImpl.save(sesion.getUsuarioVo().getId(), campo, userId, puesto, idGerencia);
    }

    public void buscarPuestoPorNombre() {
        rhPuestoVo = rhPuestoImpl.findByName(getRhPuestoVo().getNombre(), false);
    }

    private void limpiarVariables() {
        //Limpia variables
        setCampoUsuarioPuestoVo(null);
        setRhPuestoVo(null);
        setC("");
        setU("");
        setIdGerencia(-1);
        userId = "";
    }

    public void cancelarUsuarioCampo() {
        setCampoUsuarioPuestoVo(null);
        setRhPuestoVo(null);
        setU("");
        limpiarVariables();
        PrimeFaces.current().executeScript(";$(dialogoBloqueUsuario).modal('hide');");
    }

}
