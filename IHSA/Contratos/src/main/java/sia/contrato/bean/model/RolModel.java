/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioUsuarioImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;

/**
 *
 * @author ihsa
 */
@Named(value = "rolBean")
@ViewScoped
public class RolModel implements Serializable {

    static final long serialVersionUID = 1;

    /**
     * Creates a new instance of RolModel
     */
    public RolModel() {
    }
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SiRolImpl siRolImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private CvConvenioUsuarioImpl cvConvenioUsuarioImpl;
    @Inject
    private ConvenioImpl convenioImpl;

    @Inject
    Sesion sesion;
    //
    @Getter
    @Setter
    private UsuarioVO usuarioVo;
    private List<UsuarioRolVo> listaUsuarioRol;
    private UsuarioRolVo usuarioRolVo;
    private int idRol;
    private int idGerencia;
    private List<GerenciaVo> gerencias;
    private List<ContratoVO> contratos;
    private List<ContratoVO> listaContratos;
    private List<ContratoVO> listaContratosAgregar;
    @Getter
    @Setter
    private ContratoVO contratoVo;
    private boolean editar;
    private boolean agregarContrato;
    private String usuarioJson;
    private String mensaje;
    @Getter
    @Setter
    private int activeTab;
    @Getter
    @Setter
    TabView tabView;
    //
    private final Map<String, List<SelectItem>> mapaSelect = new HashMap<>();

    @PostConstruct
    public void iniciar() {
        tabView = new TabView();
        activeTab = 0;
        contratoVo = new ContratoVO();
        listaContratos = new ArrayList<>();
        listaContratosAgregar = new ArrayList<>();
        usuarioRolVo = new UsuarioRolVo();
        setIdRol(sesion.getIdRol());
        llenarCombos();
        llenarListaUsusarioRol();
        setEditar(false);
        setAgregarContrato(false);
        tabView.setIndex(activeTab);
        //
        usuarioVo = new UsuarioVO();
    }

    /**
     * @return the listaRol
     */
    public List<SelectItem> getListaGerencia() {
        List<SelectItem> item = new ArrayList<>();
        for (Object l1 : getMapaSelect().get("gerencias")) {
            GerenciaVo gerenciaVo = (GerenciaVo) l1;
            item.add(new SelectItem(gerenciaVo.getId(), gerenciaVo.getNombre()));
        }
        return item;
    }

    public void cambiarGerencia() {
        llenarListaUsusarioRol();
    }

    public void cambiarRol() {
        llenarListaUsusarioRol();
    }

    public void iniciarAgregarGerencia() {
        setEditar(true);
        getUsuarioRolVo().setIdUsuarioRol(Integer.parseInt(FacesUtils.getRequestParam("idUsuarioRol")));
        getUsuarioRolVo().setUsuario(FacesUtils.getRequestParam("nombreUsuarioRol"));
        getUsuarioRolVo().setNombreRol(FacesUtils.getRequestParam("nombreRol"));
        //
        PrimeFaces.current().executeScript(";llenarEtiqueta('frmAgregarRolUsuario', 'cjNombreUsuario','" + getUsuarioRolVo().getUsuario() + "');");
        //
        PrimeFaces.current().executeScript(";$(dialogoAgregarRolUsuario).modal('show');");
    }

    public void limpiarPestana() {
        setIdGerencia(Constantes.MENOS_UNO);
        getListaContratos().clear();
        getListaContratosAgregar().clear();
        setAgregarContrato(false);
    }

    public void seleccionarUsuario(SelectEvent<UsuarioRolVo> event) {
        setUsuarioRolVo(event.getObject());
        permisosPorUsurio();
        //
        setIdGerencia(Constantes.MENOS_UNO);
        getListaContratos().clear();
        getListaContratosAgregar().clear();
        activeTab = 1;
        tabView.setActiveIndex(activeTab);
    }

    public void eliminarUsuario(int idUserRol) {
        getUsuarioRolVo().setIdUsuarioRol(idUserRol);
        siUsuarioRolImpl.eliminarUsuarioRol(usuarioRolVo.getIdUsuarioRol(), sesion.getUsuarioSesion().getId());
        llenarListaUsusarioRol();
    }

    public void guardarRolesUsuario() {
        siUsuarioRolImpl.guardar(getIdRol(), usuarioVo.getId(), false, sesion.getUsuarioSesion().getIdCampo(), sesion.getUsuarioSesion().getId());

        llenarListaUsusarioRol();
        //
        usuarioRolVo = new UsuarioRolVo();
        PrimeFaces.current().executeScript(";$(dialogoAgregarRolUsuario).modal('hide');;");
    }

    public void buscarPermisosPorUsurio() {
        permisosPorUsurio();
        //
        //
        setIdGerencia(Constantes.MENOS_UNO);
        getListaContratos().clear();
        getListaContratosAgregar().clear();
    }

    public void agregarRelacionUsuarioContrato() {
        setAgregarContrato(true);
    }

    public void eliminarRelacionConvUsuario(int id) {
        cvConvenioUsuarioImpl.eliminar(contratos.get(id).getIdRelacion(), sesion.getUsuarioSesion().getId());
        getContratos().remove(id);
    }

    public void mostrarContratosPorGerencia() {
        llenarListaContratos();
    }

    public void asignarTodosContratoGerencia() {
        if (getIdGerencia() > 0) {
            List<ContratoVO> lcg = convenioImpl.traerConveniosPorGerencia(getIdGerencia(), sesion.getUsuarioSesion().getIdCampo());
            cvConvenioUsuarioImpl.guardar(usuarioRolVo.getIdUsuario(), lcg, sesion.getUsuarioSesion().getId());
            permisosPorUsurio();
        } else {
            FacesUtils.addErrorMessage("Es necesario seleccionar al menos una gerencia");
        }
    }

    public void quitarTodosContratoGerencia() {
        for (ContratoVO lcg1 : contratos) {
            if (lcg1.isSelected()) {
                cvConvenioUsuarioImpl.eliminar(lcg1.getIdRelacion(), sesion.getUsuarioSesion().getId());
            }
        }
        permisosPorUsurio();
    }

    public void seleccionarContrato(SelectEvent event) {
        ContratoVO cvo = (ContratoVO) event.getObject();
        getListaContratosAgregar().add(cvo);
        //
        getListaContratos().remove(cvo);
    }

    public void cancelarAgregarContrato() {
        getListaContratosAgregar().clear();
        setAgregarContrato(false);
    }

    public void agregarContratoUsuario() {
        if (!getListaContratosAgregar().isEmpty()) {

            cvConvenioUsuarioImpl.guardar(usuarioRolVo.getIdUsuario(), listaContratosAgregar, sesion.getUsuarioSesion().getId());
            //
            contratos = cvConvenioUsuarioImpl.traerContratoPorUsuario(usuarioRolVo.getIdUsuario());
            mensaje = ", tiene acceso a los contratos.";
            setAgregarContrato(false);
            getListaContratosAgregar().clear();
        }
    }

    public void quitarContratotemporarUsuario(int id) {
        getListaContratosAgregar().remove(id);
    }

    public void llenarCombos() {
        List<SelectItem> item = new ArrayList<>();
        for (RolVO rolVO : siRolImpl.traerRol(Constantes.MODULO_CONTRATO)) {
            item.add(new SelectItem(rolVO.getId(), rolVO.getNombre()));
        }
        getMapaSelect().put("roles", item);
        //
        item = new ArrayList<>();
        for (GerenciaVo rolVO : gerenciaImpl.traerGerenciaActivaPorCampo(sesion.getUsuarioSesion().getIdCampo())) {
            item.add(new SelectItem(rolVO.getId(), rolVO.getNombre()));
        }
        getMapaSelect().put("gerencias", item);
    }

    public void llenarListaUsusarioRol() {
        listaUsuarioRol = siUsuarioRolImpl.traerUsuarioPorRolModulo(getIdRol(), Constantes.MODULO_CONTRATO, sesion.getUsuarioSesion().getIdCampo());
    }

//    public void eliminarUsuario() {
//	siUsuarioRolImpl.eliminarUsuarioRol(usuarioRolVo.getIdUsuarioRol(), sesion.getUsuarioSesion().getId());
//    }
//
//    public void guardarRolesUsuario() {
//	siUsuarioRolImpl.guardar(getIdRol(), usuarioRolVo.getIdUsuario(), false, sesion.getUsuarioSesion().getIdCampo(), sesion.getUsuarioSesion().getId());
//
//	llenarListaUsusarioRol();
//    }
    private void permisosPorUsurio() {
        boolean isRolAdminContrato = siUsuarioRolImpl.buscarRolPorUsuarioModulo(usuarioRolVo.getIdUsuario(), Constantes.MODULO_CONTRATO, Constantes.COD_CONVENIO, sesion.getUsuarioSesion().getIdCampo());
        boolean isRolComprador = siUsuarioRolImpl.buscarRolPorUsuarioModulo(usuarioRolVo.getIdUsuario(), Constantes.MODULO_COMPRA, Constantes.COD_ROL_COMPRADOR, sesion.getUsuarioSesion().getIdCampo());
        boolean isRolConsConv = siUsuarioRolImpl.buscarRolPorUsuarioModulo(usuarioRolVo.getIdUsuario(), Constantes.MODULO_CONTRATO, Constantes.COD_ROL_CONS_ADMIN_CONV, sesion.getUsuarioSesion().getIdCampo());
        mensaje = usuarioRolVo.getUsuario();
        if (isRolConsConv || isRolComprador || isRolAdminContrato) {
            mensaje += ", tiene acceso a todos los contratos.";
            gerencias = new ArrayList<>();
            contratos = new ArrayList<>();
        } else {
            boolean isGerencte = usuarioImpl.isGerente(sesion.getUsuarioSesion().getIdCampo(), usuarioRolVo.getIdUsuario());
            if (isGerencte) {
                gerencias = gerenciaImpl.getAllGerenciaByApCampoAndResponsable(sesion.getUsuarioSesion().getIdCampo(), usuarioRolVo.getIdUsuario(), "id", true, true, false);
                if (!mensaje.isEmpty()) {
                    mensaje = " y ";
                }
            } else {
                gerencias = new ArrayList<>();
            }
            contratos = cvConvenioUsuarioImpl.traerContratoPorUsuario(usuarioRolVo.getIdUsuario());
            if (contratos != null && !contratos.isEmpty()) {
                mensaje += ", tiene acceso a los contratos.";
            } else {
                if (!mensaje.isEmpty()) {
                    mensaje += ",";
                }
                mensaje += " NO tiene acceso a ningún contrato.";

            }
        }
    }

    public void llenarListaContratos() {
        listaContratos = new ArrayList<>();
        listaContratos = convenioImpl.traerConveniosPorGerencia(idGerencia, sesion.getUsuarioSesion().getIdCampo());
    }

//    public void asignarTodosContratoGerencia() {
//        List<ContratoVO> lcg = convenioImpl.traerConveniosPorGerencia(getIdGerencia(), sesion.getUsuarioSesion().getIdCampo());
//        cvConvenioUsuarioImpl.guardar(usuarioRolVo.getIdUsuario(), lcg, sesion.getUsuarioSesion().getId());
//    }
//    public void quitarTodosContratoGerencia() {
//
//        for (ContratoVO lcg1 : contratos) {
//            if (lcg1.isSelected()) {
//                cvConvenioUsuarioImpl.eliminar(lcg1.getIdRelacion(), sesion.getUsuarioSesion().getId());
//            }
//        }
//
//    }
//    public void agregarContratoUsuario() {
//        cvConvenioUsuarioImpl.guardar(usuarioRolVo.getIdUsuario(), listaContratosAgregar, sesion.getUsuarioSesion().getId());
//        //
//        contratos = cvConvenioUsuarioImpl.traerContratoPorUsuario(usuarioRolVo.getIdUsuario());
//        mensaje = ", tiene acceso a los contratos.";
//    }
//    public void eliminarRelacionConvUsuario(int idConUser) {
//        cvConvenioUsuarioImpl.eliminar(contratos.get(idConUser).getIdRelacion(), sesion.getUsuarioSesion().getId());
//    }
    public List<UsuarioVO> completarUsuario(String query) {
        List<CampoUsuarioPuestoVo> users = apCampoUsuarioRhPuestoImpl.traerUsurioEnCampoPorCadena(query, sesion.getUsuarioSesion().getIdCampo());
        //
        List<UsuarioVO> lt = new ArrayList<>();
        users.stream().forEach(u -> {
            lt.add(new UsuarioVO(u.getIdUsuario(), u.getUsuario(), ""));
        });
        return lt;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the usuarioRolVo
     */
    public UsuarioRolVo getUsuarioRolVo() {
        return usuarioRolVo;
    }

    /**
     * @param usuarioRolVo the usuarioRolVo to set
     */
    public void setUsuarioRolVo(UsuarioRolVo usuarioRolVo) {
        this.usuarioRolVo = usuarioRolVo;
    }

    /**
     * @return the listaUsuarioRol
     */
    public List<UsuarioRolVo> getListaUsuarioRol() {
        return listaUsuarioRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /**
     *
     * @return
     */
    public int getIdRol() {
        return idRol;
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return idGerencia;
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        this.idGerencia = idGerencia;
    }

    /**
     * @return the mapaSelect
     */
    public Map<String, List<SelectItem>> getMapaSelect() {
        return mapaSelect;
    }

    /**
     * @return the gerencias
     */
    public List<GerenciaVo> getGerencias() {
        return gerencias;
    }

    /**
     * @return the editar
     */
    public boolean isEditar() {
        return editar;
    }

    /**
     * @param editar the editar to set
     */
    public void setEditar(boolean editar) {
        this.editar = editar;
    }

    /**
     * @return the contratos
     */
    public List<ContratoVO> getContratos() {
        return contratos;
    }

    /**
     * @return the usuarioJson
     */
    public String getUsuarioJson() {
        return usuarioJson;
    }

    /**
     * @return the listaContratos
     */
    public List<ContratoVO> getListaContratos() {
        return listaContratos;
    }

    /**
     * @return the listaContratosAgregar
     */
    public List<ContratoVO> getListaContratosAgregar() {
        return listaContratosAgregar;
    }

    /**
     * @return the agregarContrato
     */
    public boolean isAgregarContrato() {
        return agregarContrato;
    }

    /**
     * @param agregarContrato the agregarContrato to set
     */
    public void setAgregarContrato(boolean agregarContrato) {
        this.agregarContrato = agregarContrato;
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
