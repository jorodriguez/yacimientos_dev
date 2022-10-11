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
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.ApCampo;
import sia.modelo.ApCampoGerencia;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.usuario.vo.UsuarioResponsableGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.campo.nuevo.impl.ApCompaniaGerenciaImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author rluna MLUIS
 */
@Named(value = "catalogoApGerenciaBean")
@ViewScoped
public class CatalogoApGerenciaBean implements Serializable {

    //Sistema
    @Inject
    private Sesion sesion;
    /*
     * @Inject private Sesion sesion; @Inject private Conversation conversation;
     * @Inject private ConversationsManager conversationsManager;
     *
     */
    //Servicios
    @Inject
    private ApCampoGerenciaImpl apCampoGerenciaImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private ApCompaniaGerenciaImpl apCompaniaGerenciaImpl;
    @Inject
    private ApCampoImpl apCampoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    SiUsuarioRolImpl siUsuarioRolImpl;
    //Entidad
    @Getter
    @Setter
    private ApCampoGerencia apCAmpoGerencia;
    //Clases
    @Getter
    @Setter
    private ApCampoGerenciaVo apCampoGerenciaVo;
    //Colecciones
    @Getter
    @Setter
    private List<ApCampoGerenciaVo> dataModel;
    @Getter
    @Setter
    private List<SelectItem> listaUsuario;
    @Getter
    @Setter
    private List<SelectItem> gerencias;
    @Getter
    @Setter
    private List<SelectItem> campos;
    //
    @Getter
    @Setter
    private List<SelectItem> listaUsuariosAlta;
    //Primitivos
    @Getter
    @Setter
    private String nombreGerencia;
    @Getter
    @Setter
    private String responsable;
    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    private int idGerencia;
    @Getter
    @Setter
    private int idPuesto;
    @Getter
    @Setter
    private boolean visibleGerencia;
    @Getter
    @Setter
    private String rfcEmpresa;
    @Getter
    @Setter
    private boolean verNewGerencia = false;
    @Getter
    @Setter
    private List<UsuarioVO> usuarios;
    @Getter
    @Setter
    private List<String> usuariosFiltrados;

    public CatalogoApGerenciaBean() {
    }

    @PostConstruct
    public void iniciarCampo() {
        setIdCampo(sesion.getUsuario().getApCampo().getId());
        allApGerencia();
        apCampoGerenciaVo = new ApCampoGerenciaVo();
        usuarios = new ArrayList<>();
        usuariosFiltrados = new ArrayList<>();
        gerencias = new ArrayList<>();
        campos = new ArrayList<>();
        traerUsuarios();
        //
        listaCampo();
    }

    public void traerUsuarios() {
        usuarios = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(getIdCampo());
    }

    public String goToCatalogoApGerencia() {
        allApGerencia();
        return "/vistas/administracion/gerencia/catalogoApGerencia.xhtml?faces-redirect=true";
    }

    public void openPopupCreateGerencia() {
        PrimeFaces.current().executeScript("$(dialogoResponsableGerencia).modal('show');");
    }

    public String goToAltaGerencia() {
        setRfcEmpresa(traerCampoPorId().getCompania().getRfc());
        this.setIdGerencia(-1);
        return "/vistas/administracion/gerencia/altaGerencia.xhtml?faces-redirect=true";
    }

    public void idCampoSel() {
        allApGerencia();
    }

    public void allApGerencia() {
        dataModel = apCampoGerenciaImpl.findAllCampoGerenciaPorCampo(getIdCampo());
    }

    public void cambiarVisible(int idCampoGer) {
        apCampoGerenciaImpl.completarcambiarVisible(sesion.getUsuarioVo().getId(), idCampoGer);
        dataModel = apCampoGerenciaImpl.findAllCampoGerenciaPorCampo(getIdCampo());
    }

    public void cambiarResponsable(ApCampoGerenciaVo campoGerenciaVo) {
        apCampoGerenciaVo = new ApCampoGerenciaVo();
        apCampoGerenciaVo = campoGerenciaVo;
        PrimeFaces.current().executeScript("$(dialogoResponsableGerencia).modal('show');");
    }

    public List<String> completarUsuario(String cadena) {
        usuariosFiltrados.clear();
        usuarios.stream().filter(us -> us.getNombre().toUpperCase().contains(cadena.toUpperCase())).forEach(u -> {
            usuariosFiltrados.add(u.getNombre());
        });
        return usuariosFiltrados;
    }

    public void completarCambiarResponsable() {
        if (buscarUsuarioPorIdUsuario() != null) {
            completarCambiarResponsable();
            setApCampoGerenciaVo(null);
            setResponsable("");
            PrimeFaces.current().executeScript("$(dialogoResponsableGerencia).modal('hide');");
        } else {
            PrimeFaces.current().executeScript(";alertaGeneral('No existe el empleado . . .');");
        }

    }

    public void listaCampo() {
        List<CampoUsuarioPuestoVo> lc;
        lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioVo().getId());
        for (CampoUsuarioPuestoVo ca : lc) {
            SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
            campos.add(item);
        }
    }

    public UsuarioVO buscarUsuarioPorIdUsuario() {
        return usuarioImpl.findByName(getResponsable());
    }

    public void cerrarCambioResponsable() {
        // controlaPopUpFalso("popCambioResponsable");
        setApCampoGerenciaVo(null);
        setResponsable("");
        PrimeFaces.current().executeScript("$(dialogoResponsableGerencia).modal('hide');");
    }

    public void idCampoGer() {
        setRfcEmpresa(traerCampoPorId().getCompania().getRfc());
        llenarListaGerencia();

    }

    public ApCampo traerCampoPorId() {
        return apCampoImpl.find(getIdCampo());
    }

    public void guardar() {
        if (getIdCampo() > 0) {
            if (getIdGerencia() > 0) {
                if (!getResponsable().isEmpty()) {
                    try {
                        apCampoGerenciaImpl.guardarCampoGerenciaResponsable(sesion.getUsuario().getId(), getResponsable(), idCampo, idGerencia);
                        FacesUtils.addInfoMessage("Gerencia" + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
                        setIdCampo(-1);
                        setIdGerencia(-1);
                        setResponsable("");
                    } catch (Exception e) {
                        FacesUtils.addErrorMessage(new SIAException().getMessage());
                        UtilLog4j.log.fatal(this, e.getMessage());
                    }
                } else {
                    FacesUtils.addErrorMessage("Todos los campos son requeridos");
                }
            } else {
                FacesUtils.addErrorMessage("Todos los campos son requeridos");
            }
        } else {
            FacesUtils.addErrorMessage("Todos los campos son requeridos");
        }
    }

    public void guardarGerencia() {
        if (!getNombreGerencia().trim().isEmpty()) {
            try {
                this.gerenciaImpl.guardarGerencia(this.sesion.getUsuario().getId(), nombreGerencia);
                FacesUtils.addInfoMessage("Gerencia" + " " + FacesUtils.getKeyResourceBundle("sistema.mensaje.info.creacionSatisfactoria"));
                closePopupCreateGerencia();
            } catch (Exception e) {
                FacesUtils.addErrorMessage("popupCreateRhPuesto:msgsPopupCreateRhPuesto", new SIAException().getMessage());
                UtilLog4j.log.fatal(this, e.getMessage());
            }
        } else {
            FacesUtils.addErrorMessage("popupCreateRhPuesto:msgsPopupCreateRhPuesto", "Nombre es requerido");
        }
    }

    public void agregarGerenciaCompania() {
        if (buscarGerencia()) {
            agregarGerCompania();
        } else {
            PrimeFaces.current().executeScript(";alertaGeneral('La gerencia seleccionada ya tiene un responsable');");
            //FacesUtils.addErrorMessage("La gerencia seleccionada ya tiene un responsable.");
        }

    }

    public void agregarGerCompania() {
        apCompaniaGerenciaImpl.guardarRelacionGerencia(sesion.getUsuarioVo().getId(), getRfcEmpresa(), getIdGerencia());
        llenarListaGerencia();
    }

    public void llenarListaGerencia() {
        List<GerenciaVo> lc;
        UtilLog4j.log.info(this, "Campo sel: " + getIdCampo());
        lc = gerenciaImpl.traerGerenciaPorCompaniaCampo(getRfcEmpresa(), getIdCampo(), Constantes.NO_ELIMINADO);
        for (GerenciaVo ca : lc) {
            SelectItem item = new SelectItem(ca.getId(), ca.getNombre());
            gerencias.add(item);
        }
    }

    public boolean buscarGerencia() {
        boolean v = true;
        UsuarioResponsableGerenciaVo g = apCampoGerenciaImpl.buscarResponsablePorGerencia(getIdGerencia(), getIdCampo());
        if (g != null) {
            v = false;
        }
        return v;
    }

    public void closePopupCreateGerencia() {
        setIdGerencia(-1);

    }

    public void eliminarGerencia(int idApCampo, int idGerencia) {

        apCampoGerenciaImpl.deleteApCampoGerencia(idApCampo, sesion.getUsuario().getId());
        if (!apCampoGerenciaImpl.findRelacionGerenciaCampo(idGerencia)) {
            gerenciaImpl.deleteGerencia(idGerencia, sesion.getUsuario().getId());
        }

        dataModel = new ArrayList<>();
        allApGerencia();
    }

    public boolean validaRolDesarrollo() {
        return rolDesarrollo();
    }

    public boolean rolDesarrollo() {
        boolean siTieneRol = false;
        try {

            UsuarioRolVo urvo = siUsuarioRolImpl.findUsuarioRolVO(47, sesion.getUsuario().getId(), getIdCampo());
            if (urvo != null) {
                siTieneRol = Constantes.TRUE;
            } else {
                siTieneRol = Constantes.FALSE;
            }
        } catch (Exception e) {
            UtilLog4j.log.error(this, e);

        }
        return siTieneRol;
    }

    public void crearNuevaGerencia() {
        setVerNewGerencia(Constantes.TRUE);
    }

    public void noCrearNuevaGerencia() {
        setVerNewGerencia(Constantes.FALSE);
    }
}
