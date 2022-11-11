/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.catalogos.bean.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.usuario.vo.EmpleadoMaterialVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.RhEmpleadoMaterialImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.Env;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "solicitarMaterialBean")
@ViewScoped
public class SolicitarMaterialBean implements Serializable {

    /**
     * Creates a new instance of SolicitarMaterialBean
     */
    public SolicitarMaterialBean() {
    }

    private static final int OFICINA_MONTERREY = 1;
    private static final int SUBDIRECCION_ADMINISTRATIVA = 48;

    private static final int STAFF_HOUSE = 8;
    private static final int CONFIGURACION_CORREO = 15;

    @Inject
    Sesion sesion;
    @Inject
    UsuarioImpl usuarioImpl;
    @Inject
    SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    RhEmpleadoMaterialImpl rhEmpleadoMaterialImpl;
    @Inject
    SgOficinaImpl sgOficinaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Getter
    @Setter
    private UsuarioVO usuarioVo;
    @Getter
    @Setter
    private int seleccionNuevoIngreso;
    @Getter
    @Setter
    private List<EmpleadoMaterialVO> listaMaterial;
    @Getter
    @Setter
    private List<SelectItem> listaOficina;
    @Getter
    @Setter
    private Map<Integer, Boolean> filaSeleccionada;

    @Getter
    @Setter
    private List<EmpleadoMaterialVO> listaFilasSeleccionadas;

    @Getter
    @Setter
    private int idSgOficina;
    @Getter
    @Setter
    private LocalDate fechaSalida;
    @Getter
    @Setter
    private boolean solicitaEstancia;
    @Getter
    @Setter
    private List<UsuarioVO> usuarios;
    @Getter
    @Setter
    private List<String> usuariosFiltrados;

    @PostConstruct
    public void iniciar() {
        usuarios = new ArrayList<>();
        usuariosFiltrados = new ArrayList<>();
        usuarioVo = new UsuarioVO();
        seleccionNuevoIngreso = 1;
        listaOficina = new ArrayList<>();
        listaMaterial = new ArrayList<>();
        listaFilasSeleccionadas = new ArrayList<>();
        filaSeleccionada = new HashMap<>();
        String parametro = Env.getContext(sesion.getCtx(), "USER_NAME");
        if (!parametro.isEmpty()) {
            usuarioVo = usuarioImpl.findByName(parametro);
            traerListaOficinasItems();
        }
        traerUsuarios();
    }

    public void traerUsuarios() {
        usuarios = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(sesion.getUsuarioVo().getIdCampo());
    }

    public List<String> completarUsuario(String cadena) {
        usuariosFiltrados.clear();
        usuarios.stream().filter(us -> us.getNombre().toUpperCase().contains(cadena.toUpperCase())).forEach(u -> {
            usuariosFiltrados.add(u.getNombre());
        });
        return usuariosFiltrados;
    }

    public void buscarUsuarioSolicitudNuevoIngreso() {
        usuarioVo = usuarioImpl.findByName(usuarioVo.getNombre());
        traerListaMateriales();
    }

    public String solicitarMaterialUSuarios() {
        if (usuarioVo != null) {
            if (!verificaLista().isEmpty()) {
                if (solMaterial()) {
                    FacesUtils.addInfoMessage("frmUser:error", "Se enviaron las solicitudes");
                    if (!isSolicitaEstancia()) {
                        setSolicitaEstancia(false);
                        getFilaSeleccionada().clear();
                        traerListaMateriales();
                        return "solicitudMaterialEmpleado";
                    } else {
                        setFechaSalida(sumarDias());
                    }
                } else {
                    return "";
                }
            } else {
                FacesUtils.addErrorMessage("frmUser:error", "Seleccione al menos un material");
            }
        } else {
            FacesUtils.addErrorMessage("frmUser:error", "Por favor seleccione al usuario que se le asignarán los materiales");
        }
        return "";
    }

    public void traerListaMateriales() {

        List<EmpleadoMaterialVO> lstEmpMaterial;
        List<EmpleadoMaterialVO> tem = new ArrayList<>();

        if (usuarioVo.getIdOficina() == OFICINA_MONTERREY) {
            lstEmpMaterial = rhEmpleadoMaterialImpl.getListEmpleadoMaterial();
        } else {
            lstEmpMaterial = rhEmpleadoMaterialImpl.getListEmpleadoMaterial();

            for (EmpleadoMaterialVO empleadoMaterialVO : lstEmpMaterial) {
                if (empleadoMaterialVO.getIdGerencia() != SUBDIRECCION_ADMINISTRATIVA) {
                    tem.add(empleadoMaterialVO);
                }
            }

            lstEmpMaterial.clear();
            lstEmpMaterial.addAll(tem);
        }

        setListaMaterial((lstEmpMaterial));
    }

    public void traerListaOficinasItems() {
        try {
            List<OficinaVO> lo = sgOficinaImpl.getIdOffices();
            for (OficinaVO vo : lo) {
                SelectItem item = new SelectItem(vo.getId(), vo.getNombre());
                listaOficina.add(item);
            }

        } catch (Exception e) {
            UtilLog4j.log.error("Excepcion al trae la lista de oficinas ", e);
        }
    }

    public List<EmpleadoMaterialVO> verificaLista() {

        List<EmpleadoMaterialVO> lstEmpMaterial = new ArrayList<>();

        for (EmpleadoMaterialVO sgV : listaMaterial) {
            if (filaSeleccionada.get(sgV.getId())) {

                if (sgV.getId() == STAFF_HOUSE) {
                    setIdSgOficina(usuarioVo.getIdOficina());
                    sumarDias();
                    setSolicitaEstancia(true);
                }

                if (sgV.getId() == CONFIGURACION_CORREO) {//requiere correo
                    usuarioVo.setRequiereConfiguracionCorreo(true);
                }

                lstEmpMaterial.add(sgV);
                filaSeleccionada.remove(sgV.getId());
            }
        }

        setListaFilasSeleccionadas(lstEmpMaterial);

        return getListaFilasSeleccionadas();
    }

    public LocalDate sumarDias() {
        setFechaSalida(convertToLocalDateViaInstant(siManejoFechaLocal.fechaSumarDias(getUsuarioVo().getFechaIngreso(), 60)));
        return getFechaSalida();
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public boolean solMaterial() {
        return usuarioImpl.enviarSolicitudMaterial(
                sesion.getUsuarioVo().getId(),
                getUsuarioVo(),
                listaFilasSeleccionadas,
                getSeleccionNuevoIngreso()
        );
    }

    public String cancelarUsuario() {
       setUsuarioVo(new UsuarioVO());
       this.listaMaterial = Collections.emptyList();    
       return "/vistas/recursos/principalRecursosHumanos.xhtml?faces-redirect=true;";
    }

    //Genera solicitud de estancia
    public String solicitarEstancia() {
        sgSolicitudEstanciaImpl.guardarSolicitudEstancia(
                sesion.getUsuarioVo().getId(),
                getUsuarioVo().getId(),
                getUsuarioVo().getIdGerencia(),
                getIdSgOficina(),
                getUsuarioVo().getFechaIngreso(),
                convertToDateViaInstant(getFechaSalida())
        );

        setUsuarioVo(new UsuarioVO());
        setFechaSalida(null);
        FacesUtils.addErrorMessage("frmUser", FacesUtils.getKeyResourceBundle("sia.solicitud.enviada"));
        return "";
    }

    public Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public void cancelarSolicitarEstancia() {
        setUsuarioVo(new UsuarioVO());
        setFechaSalida(null);

    }

}
