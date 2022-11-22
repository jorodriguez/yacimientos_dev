/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.backing;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.EmailNotFoundException;
import sia.excepciones.SIAException;
import sia.modelo.SgEmpresa;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.gerencia.vo.RhTipoGerenciaVo;
import sia.modelo.puesto.vo.RhPuestoVo;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.usuario.vo.EmpleadoMaterialVO;
import sia.modelo.usuario.vo.UsuarioGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.modelo.vo.ApCampoVo;
import sia.servicios.campo.nuevo.impl.ApCampoGerenciaImpl;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.campo.nuevo.impl.RhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoNoticiaImpl;
import sia.servicios.requisicion.impl.CadenasMandoImpl;
import sia.servicios.rh.impl.RhCampoGerenciaImpl;
import sia.servicios.sgl.impl.SgEmpresaImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgSolicitudEstanciaImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiModuloImpl;
import sia.servicios.sistema.impl.SiOpcionImpl;
import sia.servicios.sistema.impl.SiRelRolOpcionImpl;
import sia.servicios.sistema.impl.SiRolImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.servicios.sistema.vo.MenuSiOpcionVo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.servicios.usuario.impl.RhTipoGerenciaImpl;
import sia.servicios.usuario.impl.RhUsuarioGerenciaImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.sistema.bean.support.SoporteListas;
import sia.sistema.bean.support.SoporteProveedor;
import sia.util.Env;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class UsuarioBean implements Serializable {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of UsuarioBean
     */
    public UsuarioBean() {
    }

    @Inject
    Sesion sesion;
    private static final int OFICINA_MONTERREY = 1;
    private static final int SUBDIRECCION_ADMINISTRATIVA = 48;
    private static final int STAFF_HOUSE = 8;
    private static final int CONFIGURACION_CORREO = 15;

    private static final int FINANZAS = 54;
    private static final int SERVICIOS_INFORMATICOS = 61;
    private static final int SERVICIOS_GENERALES = 33;
    private static final int HSE = 47;

    //@ManagedProperty(value = "#{soporteProveedor}")
    @Inject
    private SoporteProveedor soporteProveedor;
    //@ManagedProperty(value = "#{soporteListas}")
  //  @Inject
//    private SoporteListas soporteListas;
    //
    @Inject
    SiOpcionImpl siOpcionImpl;
    @Inject
    UsuarioImpl servicioUsuario;
    @Inject
    CadenasMandoImpl cadenasMandoImpl;
    @Inject
    GerenciaImpl gerenciaImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private RhPuestoImpl rhPuestoImpl;
    @Inject
    ApCampoImpl apCampoImpl;
    @Inject
    ApCampoGerenciaImpl apCampoGerenciaImpl;
    @Inject
    SgOficinaImpl sgOficinaImpl;
    @Inject
    SgEmpresaImpl sgEmpresaImpl;
    //@Inject
    //RhEmpleadoMaterialImpl rhEmpleadoMaterialImpl;
    @Inject
    SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
    @Inject
    RhUsuarioGerenciaImpl rhUsuarioGerenciaImpl;
    //@Inject
    //SiUsuarioTipoImpl siUsuarioTipoImpl;
    @Inject
    RhTipoGerenciaImpl rhTipoGerenciaImpl;
    @Inject
    RhCampoGerenciaImpl rhCampoGerenciaImpl;
    @Inject
    CoNoticiaImpl coNoticiaImpl;
    @Inject
    SiRolImpl siRolImpl;
    @Inject
    SiRelRolOpcionImpl siRolOpcion;
    @Inject
    SiUsuarioRolImpl siUsuarioRol;
    @Inject
    SiModuloImpl siModuloImpl;
    @Getter
    @Setter
    private List<MenuSiOpcionVo> listaMenu;
    @Getter
    @Setter
    private DataModel listaUsuarioFree;
    @Getter
    @Setter
    private boolean apruebaOCPop = false; //tambien se usa para abrir el popup para agregar gerencias a la lista de Gerencias que liberan al empleado en la opcion Inicio de baja
    @Getter
    @Setter
    private boolean flag;
    @Getter
    @Setter
    private ApCampoGerenciaVo apCampoGerenciaVo;
    @Getter
    @Setter
    private UsuarioVO usuarioVOAlta; //para trabajar con usuario
    @Getter
    @Setter
    private UsuarioVO usuarioVO; //para la sesion
    @Getter
    @Setter
    private UsuarioGerenciaVo usuarioGerenciaVo;
    @Getter
    @Setter
    private DataModel lista;
    @Getter
    @Setter
    private CampoUsuarioPuestoVo campoUsuarioPuestoVo;
    @Getter
    @Setter
    private RhPuestoVo rhPuestoVo;
    //
    @Getter
    @Setter
    private DataModel listaUsuario;
    //
    @Getter
    @Setter
    private DataModel listaGerencias;
    @Getter
    @Setter
    private DataModel listaGerenciasSeleccionadas;
    @Getter
    @Setter
    private DataModel listaMaterial;
    @Getter
    @Setter
    private DataModel listaOpciones;
    @Getter
    @Setter
    private int idGerencia;
    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    private int idSgOficina; //<< Ocupado en la opcion de pedir material, cuando se pide staff house se pide que seleccione la oficina de estancia
    @Getter
    @Setter
    private String nombre;
    @Getter
    @Setter
    private String primerApellido;
    @Getter
    @Setter
    private String segundoApellido;
    @Getter
    @Setter
    private Map<Integer, Boolean> filaSeleccionada = new HashMap<>();
    @Getter
    @Setter
    private Map<String, Boolean> mapaRoles = new HashMap<>();
    @Getter
    @Setter
    private List listaFilasSeleccionadas;
    @Getter
    @Setter
    private List<GerenciaVo> listaGeneral;
    @Getter
    @Setter
    private boolean solicitaEstancia;
    @Getter
    @Setter
    private Date fechaSalida;
    @Getter
    @Setter
    private String respuesta; // Tambien ocupada para escribir el motivo de Baja
    @Getter
    @Setter
    private List<SelectItem> listaPuestos;
    @Getter
    @Setter
    private int idPuesto; //tambien usado para agregar gerencias a campos desde RH- representa el campo (idApCampo)
    @Getter
    @Setter
    private List<SelectItem> listaGerenciasItems;
    @Getter
    @Setter
    private List<SelectItem> listaCamposItems;
    //
    @Getter
    @Setter
    private int preguntaEntero = 1; // se ocupa para la consulta de compradores y solicitan requision
    @Getter
    @Setter
    private String cambiarPass = "no";
    @Getter
    @Setter
    private int agregarNuevoPass;
    @Getter
    @Setter
    private String u;
    @Getter
    @Setter
    private int seleccionNuevoIngreso = 1;

    @Getter
    @Setter
    private int parametroTipoUsuario = 1;
    @Getter
    @Setter
    private String c;
    @Getter
    @Setter
    private String confirmarPassword;
    @Getter
    @Setter
    private String nuevaClave;
    @Getter
    @Setter
    private String claveActual;
    @Getter
    @Setter
    private int accion;
    @Getter
    @Setter
    private List<UsuarioVO> usuarios;
    @Getter
    @Setter
    private List<String> usuariosFiltrados;
    @Getter
    @Setter
    private List<SelectItem> gerencias;
    @Getter
    @Setter
    private List<SelectItem> listaCampo;

    @PostConstruct
    public void iniciar() {
        gerencias = new ArrayList<>();
        usuarios = new ArrayList<>();
        usuariosFiltrados = new ArrayList<>();
        listaMenu = new ArrayList<>();
        listaCampo = new ArrayList<>();
        usuarioVOAlta = new UsuarioVO();
        rhPuestoVo = new RhPuestoVo();
        llenarMenu();
        List<UsuarioRolVo> rolUsuario = siUsuarioRol.traerRolPorUsuarioModulo(sesion.getUsuarioVo().getId(), Constantes.MODULO_RH_ADMIN, sesion.getUsuarioVo().getIdCampo());
        for (UsuarioRolVo usuarioRolVo : rolUsuario) {
            mapaRoles.put(usuarioRolVo.getNombreRol(), Boolean.TRUE);
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

    public void llenarMenu() {
        listaMenu.addAll(taerListaMenu(Constantes.MODULO_ADMINSIA, sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo()));

    }

    public List<MenuSiOpcionVo> taerListaMenu(Integer modulo, String usrID, int campo) {
        listaMenu = new ArrayList<>();

        try {
            listaMenu.addAll(makeItems(siOpcionImpl.getListaMenu(modulo, usrID, campo)));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return listaMenu;
    }

    private List<MenuSiOpcionVo> makeItems(List<MenuSiOpcionVo> listaItems) {
        List<MenuSiOpcionVo> itemsReturn = new ArrayList<>();

        for (MenuSiOpcionVo oldVO : listaItems) {
            MenuSiOpcionVo menuSiOpcionVo = new MenuSiOpcionVo();
            menuSiOpcionVo.setPadre(oldVO.getPadre());

            for (SiOpcionVo hijo : oldVO.getHijos()) {
                menuSiOpcionVo.getHijos().add(hijo);
            }

            itemsReturn.add(menuSiOpcionVo);
        }

        return itemsReturn;
    }

    public List<SiOpcionVo> getListaSubMenus() {
        Collection<SiOpcionVo> s = new HashSet<>();
        try {
            for (UsuarioRolVo rol : traerRolesPorUsuario(sesion.getUsuarioVo().getId(), 0, sesion.getUsuarioVo().getIdCampo())) {
                s.addAll(taerOpcionesByRol(Constantes.MODULO_ADMINSIA, rol.getIdRol()));
            }
        } catch (Exception e) {
            return null;
        }
        return new ArrayList<>(s);
    }

    public List<SiOpcionVo> taerOpcionesByRol(Integer modulo, Integer rol) {
        return siOpcionImpl.getSiOpcionBySiModulo(modulo, rol);

    }

    public List<UsuarioRolVo> traerRolesPorUsuario(String idUsuario, int modulo, int idCamp) {
        List<UsuarioRolVo> ur = null;
        try {
            ur = siUsuarioRol.traerRolPorUsuarioModulo(idUsuario, modulo, idCamp);

        } catch (Exception e) {
            LOGGER.warn(this, e);
        }
        return ur;
    }

    public DataModel getTraerUsuariosSinCorreo() {
        setLista(traerUsuariosSinCorreo());
        return getLista();
    }

    public String goToLiberarEmpleadoBaja() {
        //Limpiar variables
        setListaUsuarioFree(null);
        //Llenar datos
        setListaUsuarioFree(new ListDataModel(allUsuarioForSetFree()));
        return "/vistas/recursos/liberarEmpleado";
    }

    public void setListaUsuarioFree(DataModel listaUsuarioFree) {
        this.listaUsuarioFree = listaUsuarioFree;
    }

    public int totalAllUsuarioForSetFree() {
        List<UsuarioGerenciaVo> list = allUsuarioForSetFree();
        return ((list != null && !list.isEmpty()) ? list.size() : 0);
    }

    public DataModel traerUsuariosSinCorreo() {
        setListaUsuario(new ListDataModel(servicioUsuario.traerListaUsuariosSinCorreos()));
        return getListaUsuario();
    }

    public List<UsuarioGerenciaVo> allUsuarioForSetFree() {
        return allUsuarioForFree();
    }

    public List<UsuarioGerenciaVo> allUsuarioForFree() {
        return rhUsuarioGerenciaImpl.findAllForFreeByUsuario(sesion.getUsuarioVo().getId());
    }

    public void setFreeEmployee() {
        UsuarioGerenciaVo vo = (UsuarioGerenciaVo) getListaUsuarioFree().getRowData();

        try {
            this.setFreeEmployee(vo.getId());
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.Usuario.liberarSatisfactorio"));
            setListaUsuarioFree(new ListDataModel(allUsuarioForSetFree()));
        } catch (EmailNotFoundException enfe) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(enfe.getLiteral()) + ": " + enfe.getAllUsuariosWithoutEmail());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void setFreeEmployee(int idUsuarioGerencia) throws EmailNotFoundException {
        rhUsuarioGerenciaImpl.setFreeUsuarioAndAdvicing(idUsuarioGerencia, sesion.getUsuarioVo().getId());
    }

    public void comentarNotificia() {
        UsuarioGerenciaVo vo = (UsuarioGerenciaVo) getLista().getRowData();

        try {
            this.setFreeEmployee(vo.getId());
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.Usuario.liberarSatisfactorio"));
            setListaUsuario(new ListDataModel(allUsuarioForSetFree()));
        } catch (EmailNotFoundException enfe) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(enfe.getLiteral()) + ": " + enfe.getAllUsuariosWithoutEmail());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void buscarId() {
        String[] cad = sesion.getUsuarioVo().getMail().split("@");
        Usuario us = buscarPorId(cad[0].toUpperCase());
        if (us != null) {
            FacesUtils.addInfoMessage("Ya existe un usuario con el ID " + cad[0]);
        }
    }
///Campo

    public String goToModificarCorreoTi() {
        traerUsuariosSinCorreo();
        //setUsuarioVOAlta(null);
        return "altaCorreoTi.xhtml?faces-redirect=true";
    }

    public String goToIniciarBajaEmpleado() {
//        traerListaRhTipoGerencia();
        setListaGerencias(null);
        setRespuesta("");
//        getFilaSeleccionada().clear();
        setUsuarioVOAlta(null);
        setU("");
        clearComponent("formInicioBaja", "userSelect");
        setFlag(false);

        llenarListaApCamposItems();
        setIdCampo(1);
        llenarComboGerenciasPorCampo();
        agregarGerenciasDefaultParaInicioBaja();
        return "/vistas/recursos/inicioBajaEmpleado.xhtml?faces-redirect=true";
    }

    public void agregarGerenciasDefaultParaInicioBaja() {
        List<GerenciaVo> lista = gerenciaImpl.getAllGerenciaByApCampo(sesion.getIdCampo(), "nombre", true, null, false);
        listaGeneral = new ArrayList<GerenciaVo>();

        for (GerenciaVo vo : lista) {
            if (vo.getId() == SERVICIOS_GENERALES || vo.getId() == SERVICIOS_INFORMATICOS
                    || vo.getId() == FINANZAS || vo.getId() == HSE) {
                //pasar a la lista de sleccion
                listaGeneral.add(vo);
            }
        }

        listaGerenciasSeleccionadas = new ListDataModel(listaGeneral);
    }

    public void llenarComboGerenciasPorCampo() {
        //TODO : revisar si es necesario el doble try / catch
        try {
            List<GerenciaVo> lista = gerenciaImpl.getAllGerenciaByApCampo(idCampo, "nombre", true, null, false);
            LOGGER.info(this, "getApCamposItems");
            List<SelectItem> l = new ArrayList<SelectItem>();
            try {
                for (GerenciaVo vo : lista) {
                    SelectItem item = new SelectItem(vo.getId(), vo.getNombre() + " -  " + vo.getNombreResponsable());
                    l.add(item);
                }
                this.listaGerenciasItems = l;
            } catch (Exception e) {
                LOGGER.error(this, "Excepcion al traer la lista de gerencias " + e.getMessage(), e);
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void llenarListaApCamposItems() {
        LOGGER.info(this, "getApCamposItems");
        List<SelectItem> l = new ArrayList<>();
        try {
            for (CampoVo apCampoVo : this.apCampoImpl.getAllField()) {
                SelectItem item = new SelectItem(apCampoVo.getId(), apCampoVo.getNombre());
                l.add(item);
            }
            LOGGER.info(this, " size" + l.size());
            this.listaCamposItems = l;
        } catch (Exception e) {
            LOGGER.error(this, "Excepcion al traer la lista de campos " + e.getMessage(), e);
        }
    }

    public String gotoFinalizaBaja() {
        setUsuarioVOAlta(null);
        return "finalizaBaja.xhtml?faces-redirect=true";
    }

    public String cambiarCampo() {
        CampoUsuarioPuestoVo campoUsuarioPuesto = (CampoUsuarioPuestoVo) getLista().getRowData();
        UtilLog4j.log.info(this, "Campo: " + campoUsuarioPuesto.getCampo() + "id: " + campoUsuarioPuesto.getIdCampo());
        cambiarUsuarioPuesto(sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getId(), campoUsuarioPuesto.getIdCampo());
        //traerCampoUsuario();
        llenarDatosUsuario();
        return "";
    }

///Fin de campo
    public void cambiarUsuarioPuesto(String idUsuario, String idUserModifico, int campo) {
        servicioUsuario.cambiarCampoUsuario(idUsuario, idUserModifico, campo);
        //
    }

    public String guardarUsuarioNuevoIngreso(String param) {
        boolean v;
        if (!this.validateTextHastNotPunctuation(getNombre())) {
            if (!this.validateTextHastNotPunctuation(getPrimerApellido())) {
                if (!this.validateTextHastNotPunctuation(getSegundoApellido())) {
                    if (getUsuarioVOAlta().getIdCampo() > 0) {
                        if (getUsuarioVOAlta().getIdOficina() > 0) {
                            if (getIdGerencia() > 0) {
                                if (verificaPuesto()) {
                                    //if (!getUsuarioVOAlta().getIdJefe().isEmpty()) {
                                    if (getUsuarioVOAlta().getFechaIngreso() != null) {
                                        UtilLog4j.log.info(this, "nombre puesto: " + getRhPuestoVo().getNombre());
                                        if (getUsuarioVOAlta().getIdNomina() > 0) {
                                            if (validaMail(getUsuarioVOAlta().getMail())) {
                                                try {
                                                    v = this.guardarUsuarioNuevoIngreso();//sesion.getUsuarioVo().getId(), getUsuarioVOAlta(), getIdGerencia());
                                                } catch (Exception e) {
                                                    FacesUtils.addErrorMessage("frmUser:error", "Ocurrió un error al guardar el Usuario");
                                                    v = false;
                                                    LOGGER.warn(this, "", e);
                                                }
                                                if (v) {
                                                    this.limpiar();
                                                    setListaPuestos(null);
                                                    traerListaMateriales();
                                                    if (param != null && !param.isEmpty() && "AD".equals(param)) {
                                                        FacesUtils.addInfoMessage("El usuario se guardo exitosamente. ");
                                                        cancelarNuevoIngreso();
                                                        return "/vistas/recursos/altaNuevoIngresoAdmin.xhtml?faces-redirect=true";
                                                    } else {
                                                        Env.setContext(sesion.getCtx(), "USER_NAME", usuarioVOAlta.getNombre());
                                                        return "/vistas/recursos/solicitudMaterialEmpleado.xhtml?faces-redirect=true";
                                                    }
                                                } else {
                                                    FacesUtils.addInfoMessage("frmUser:error", "Ocurrio un error . . .  + + + ");
                                                }
                                            } else {
                                                FacesUtils.addInfoMessage("Mail no válido");
                                            }
                                        } else {
                                            FacesUtils.addInfoMessage("frmUser:error", "Seleccione la nomina");
                                        }
                                    } else {
                                        FacesUtils.addInfoMessage("frmUser:error", "Seleccione la fecha de ingreso");
                                    }
//                                    } else {
//                                        FacesUtils.addInfoMessage("frmUser:error", "Seleccione el jefe");
//                                    }
                                } else {
                                    FacesUtils.addInfoMessage("frmUser:error", "Seleccione el puesto");
                                }
                            } else {
                                FacesUtils.addInfoMessage("frmUser:error", "Seleccione alguna gerencia");
                            }
                        } else {
                            FacesUtils.addInfoMessage("frmUser:error", "Seleccione la oficina");
                        }
                    } else {
                        FacesUtils.addErrorMessage("frmUser:error", "Campo es requerido");
                    }
                } else {
                    FacesUtils.addErrorMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.segundoApellido.valida.false"));
                }
            } else {
                FacesUtils.addErrorMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.primerApellido.valida.false"));
            }
        } else {
            FacesUtils.addErrorMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.nombre.valida.false"));
        }

        return "";
    }

    public void traerListaMateriales() {
        LOGGER.info(this, "lsita de materiales");
        List<EmpleadoMaterialVO> lstEmpMaterial;
        List<EmpleadoMaterialVO> tem = new ArrayList<>();
        /*
        if (getUsuarioVOAlta().getIdOficina() == OFICINA_MONTERREY) {
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

        LOGGER.info(this, "lista recuperada : " + lstEmpMaterial.size());

        setListaMaterial(new ListDataModel(lstEmpMaterial));
         */
        LOGGER.info(this, "lista datamodel : " + getLista());
    }

    public boolean guardarUsuarioNuevoIngreso() throws NoSuchAlgorithmException {

        // si contamos con el nombre de usuario de directorio, se debiera utilizar
        // este como id del usuario        
        if (Strings.isNullOrEmpty(getUsuarioVOAlta().getUsuarioDirectorio())) {
            construirId();
        } else {
            getUsuarioVOAlta().setId(getUsuarioVOAlta().getUsuarioDirectorio());
        }

        getUsuarioVOAlta().setDestinatarios(getUsuarioVOAlta().getMail());
        getUsuarioVOAlta().setIdPuesto(getRhPuestoVo().getId());
        getUsuarioVOAlta().setClave(encriptar("1234"));
        //  LOGGER.info(this, "JEFE: " + getUsuarioVOAlta().getIdJefe());

        getUsuarioVOAlta().setIdJefe(gerenciaImpl.getResponsableByApCampoAndGerencia(getUsuarioVOAlta().getIdCampo(), getIdGerencia(), false).getId());
        getUsuarioVOAlta().setIdGerencia(getIdGerencia());
        getUsuarioVOAlta().setGerencia(gerenciaImpl.find(getIdGerencia()).getNombre());
        getUsuarioVOAlta().setOficina(sgOficinaImpl.find(getUsuarioVOAlta().getIdOficina()).getNombre());
        getUsuarioVOAlta().setPuesto(rhPuestoImpl.find(getUsuarioVOAlta().getIdPuesto()).getNombre());

        return this.servicioUsuario.guardarUsuarioNuevoIngreso(sesion.getUsuarioVo().getId(), getUsuarioVOAlta(), getIdGerencia());
    }

    public String encriptar(String text) throws NoSuchAlgorithmException {
        //LOGGER.info(this, "Text: ");
        return servicioUsuario.encriptar(text);
    }

    private void construirId() {

        String cad = removeSpecialCharactersReplacingWithASCII(getNombre()).trim();
        StringBuilder id = new StringBuilder();
        Usuario us;
        int numLetrasNombre = 1;

        for (int i = 0; i < cad.length(); i++) {
            for (int j = 0; j < numLetrasNombre; j++) {
                id.append(cad.charAt(i + j));
//                id += cad.charAt(i + j);
            }
            id.append(removeSpecialCharactersReplacingWithASCII(getPrimerApellido().trim()));

            LOGGER.info(this, "ID: " + id);
            us = servicioUsuario.findRH(id.toString().toUpperCase());
            if (us == null) {
                usuarioVOAlta.setId(id.toString().toUpperCase());
                usuarioVOAlta.setNombre(
                        getNombre().trim()
                        + " " + getPrimerApellido().trim()
                        + " " + getSegundoApellido().trim()
                );
                LOGGER.info(this, "ID: " + id);
                break;
            } else {
                id = new StringBuilder();
            }

            if (i == cad.length() - 1) {
                numLetrasNombre++;
                i = -1;
            }
        }
    }

    public String removeSpecialCharactersReplacingWithASCII(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public boolean validateTextHastNotPunctuation(String text) {
        Pattern p = Pattern.compile("\\p{Punct}");
        Matcher m = p.matcher(text);

        return m.find();
    }

    public boolean verificaPuesto() {
        setRhPuestoVo(rhPuestoImpl.findByName(getRhPuestoVo().getNombre(), false));
        return getRhPuestoVo() != null;
    }

    public String guardarUsuarioNuevoIngresoPrueba() throws NoSuchAlgorithmException {
        boolean v = true;
        //String pagina = "";
        //    v = this.guardarUsuarioNuevoIngreso();//sesion.getUsuarioVo().getId(), getUsuarioVOAlta(), getIdGerencia());
        if (v) {
            return "solicitudMaterialEmpleado";
        } else {
            FacesUtils.addInfoMessage("frmUser:error", "Ocurrio un error . . .  + + + ");
        }

        return "";
    }

    public void modificarCorreoPorTi() {
        try {
            if (!getUsuarioVOAlta().getMail().equals("")) {
                if (validaMail(getUsuarioVOAlta().getMail())) {
                    //saber si cambio el correo
                    if (verificarModificacionMail()) {
                        if (guardarDireccionCorreoReal(sesion.getUsuarioVo().getId())) {
                            cerrarPopup();
//                   FacesUtils.addInfoMessage("Se ha modificado satisfactoriamente la direccion de correo del usuario " + getUsuarioVOAlta().getNombre());
                        } else {
                            FacesUtils.addInfoMessage("No se ha podido modificar la dirección de correo. Por favor contacte al equipo de soporte SIA para verficar esta situación (soportesia@ihsa.mx)");
                        }
                    } else {
                        FacesUtils.addInfoMessage("No se ha modificado la direccion de correo");
                    }
                } else {
                    FacesUtils.addInfoMessage("El formato del correo no es valido");
                }
            } else {
                FacesUtils.addInfoMessage("Por favor escriba una direccion de correo");
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al modificar la cuenta de correo " + e.getMessage());
        }
    }

    public boolean verificarModificacionMail() {
        return servicioUsuario.find(usuarioVOAlta.getId())
                .getEmail().equals(usuarioVOAlta.getMail());
    }

    public boolean guardarDireccionCorreoReal(String idUsuario) {
        return servicioUsuario.guardarDireccionMailNuevoIngreso(
                usuarioVOAlta.getId(),
                usuarioVOAlta.getMail(),
                idUsuario
        );
    }

    public String solicitarMaterialUSuarios() {
        if (getUsuarioVOAlta() != null) {
            if (verificaLista().size() > 0) {
                if (solicitarMaterial()) {
                    FacesUtils.addInfoMessage("frmUser:error", "Se enviaron las solicitudes");
                    if (!isSolicitaEstancia()) {
                        setSolicitaEstancia(false);
                        this.limpiar();
                        this.setU("");
                        clearComponent("frmUser", "userSelect");
                        this.getFilaSeleccionada().clear();
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

    public List<EmpleadoMaterialVO> verificaLista() {
        setSolicitaEstancia(false);

        DataModel<EmpleadoMaterialVO> lt = getListaMaterial();
        List<EmpleadoMaterialVO> lstEmpMaterial = new ArrayList<>();
        setListaFilasSeleccionadas(new ArrayList<>());

        LOGGER.info(this, "Filas seleccionadas: " + filaSeleccionada.size());

        for (EmpleadoMaterialVO sgV : lt) {
            if (filaSeleccionada.get(sgV.getId())) {

                if (sgV.getId() == STAFF_HOUSE) {
                    setIdSgOficina(usuarioVOAlta.getIdOficina());
                    sumarDias();
                    setSolicitaEstancia(true);
                }

                if (sgV.getId() == CONFIGURACION_CORREO) {//requiere correo
                    usuarioVOAlta.setRequiereConfiguracionCorreo(true);
                }

                lstEmpMaterial.add(sgV);
                filaSeleccionada.remove(sgV.getId());
            }
        }

        setListaFilasSeleccionadas(lstEmpMaterial);

        return getListaFilasSeleccionadas();
    }

    public Date sumarDias() {
        setFechaSalida(siManejoFechaLocal.fechaSumarDias(getUsuarioVOAlta().getFechaIngreso(), 60));
        return getFechaSalida();
    }

    public boolean validarFechaInicioMenorAHoy(Date valor) {
        Calendar fIni = Calendar.getInstance();
        Calendar fHoy = Calendar.getInstance();
        fIni.setTime(valor);
        fHoy.setTime(new Date());
        return siManejoFechaLocal.compare(fIni, fHoy, false) == -1;
    }

    public boolean solicitarMaterial() {
        return servicioUsuario.enviarSolicitudMaterial(
                sesion.getUsuarioVo().getId(),
                getUsuarioVOAlta(),
                listaFilasSeleccionadas,
                getSeleccionNuevoIngreso()
        );
    }

    //
    public String guardarUsuario() throws NoSuchAlgorithmException {
        boolean v;
        String[] cad = getUsuarioVOAlta().getMail().split("@");
        String posibleId = this.removeSpecialCharactersReplacingWithASCII(cad[0]);

        int errors = 0;

        if (!this.validaMail(getUsuarioVOAlta().getMail()) || this.validateTextHastNotPunctuation(posibleId)) {
            FacesUtils.addInfoMessage("frmUser:error", "Mail no válido");
            FacesUtils.addInfoMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.email.msg.noValido.caracteresEspeciale"));
            errors++;
        }
        if (getUsuarioVOAlta().getIdCampo() <= 0) {
            FacesUtils.addInfoMessage("frmUser:error", "Campo es requerido");
            errors++;
        }
        if (this.validateTextHastNotPunctuation(getUsuarioVOAlta().getNombre())) {
            FacesUtils.addErrorMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.nombre.valida.false"));
            errors++;
        }
        if (getIdGerencia() <= 0) {
            FacesUtils.addInfoMessage("frmUser:error", "Gerencia es requerido");
            errors++;
        }
        if (!this.validaMail(getUsuarioVOAlta().getDestinatarios()) || this.validateTextHastNotPunctuation(getUsuarioVOAlta().getDestinatarios())) {
            FacesUtils.addInfoMessage("frmUser:error", "Destinatarios no válido");
            FacesUtils.addInfoMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.email.msg.noValido.caracteresEspeciale"));
            errors++;
        }

        if (errors == 0) {
            getUsuarioVOAlta().setId(posibleId);
            if (this.traerIdUsuario(getUsuarioVOAlta().getId().toUpperCase()) == null) {
                getUsuarioVOAlta().setIdPuesto(getRhPuestoVo().getId());
                getUsuarioVOAlta().setClave(encriptar(getUsuarioVOAlta().getClave()));
                v = servicioUsuario.guardarNuevoUsuario(sesion.getUsuarioVo().getId(), getUsuarioVOAlta(), getIdGerencia());
                if (v) {
//                    this.soporteProveedor.setUsuario(null);
//                    soporteProveedor.setPuestoVo(null);
                    setRhPuestoVo(null);
                    setUsuarioVOAlta(null);
                    this.limpiar();
                    return "consultaUsuario";
                } else {
                    FacesUtils.addInfoMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sistema.msg.error.guardar"));
                }
            } else {
                FacesUtils.addInfoMessage("frmUser:error", "El id " + getUsuarioVOAlta().getId() + " ya existe, por favor elija otro");
            }
        }
        return "";
    }

    public String traerIdUsuario(String id) {
        String retVal = null;

        try {
            retVal = servicioUsuario.find(id).getId();
        } catch (Exception e) {
            LOGGER.error(e);
        }

        return retVal;
    }

    public String cancelarNuevoIngreso() {
        setU("");
        setUsuarioVOAlta(new UsuarioVO());
        setRhPuestoVo(new RhPuestoVo());
        setIdGerencia(-1);
        setNombre("");
        setPrimerApellido("");
        setSegundoApellido("");
        this.getFilaSeleccionada().clear();
        return "/vistas/administracion/principalAdministrar.xhtml?faces-redirect=true";
    }

    public void guardarUsuarioCampo(int campo, int puesto) {
        int gerencia = servicioUsuario.find(getU()).getGerencia().getId();
        apCampoUsuarioRhPuestoImpl.save(sesion.getUsuarioVo().getId(), campo, getU(), puesto, gerencia);
    }

    public void cancelNuevoIngreso() {
        setU("");
        setUsuarioVOAlta(new UsuarioVO());
        setRhPuestoVo(new RhPuestoVo());
        setIdGerencia(-1);
        setNombre("");
        setPrimerApellido("");
        setSegundoApellido("");
        this.getFilaSeleccionada().clear();
    }

    public List<UsuarioGerenciaVo> getTraerUsuarioProcesoBaja() {
        return traerUsuarioProcesoBaja();
    }

    public void finalizarBaja(String idUs) {
        setRespuesta(idUs);
        if (servicioUsuario.finalizarBaja(sesion.getUsuarioVo().getId(), getRespuesta())) {
            FacesUtils.addInfoMessage("Se termino con el proceso de baja de usuario");
        } else {
            FacesUtils.addInfoMessage("Ocurrio un error, favor de cominicar al equipo de desarrollo del SIA (sia@ihsa.mx). ");
        }
    }

    public List<UsuarioGerenciaVo> traerUsuarioProcesoBaja() {
        List<UsuarioGerenciaVo> retVal;

        if (getUsuarioVOAlta() == null) {
            retVal = rhUsuarioGerenciaImpl.traerUsuarioNoLiberadoGerencia("", null);
        } else {
            retVal = rhUsuarioGerenciaImpl.traerUsuarioNoLiberadoGerencia(getUsuarioVOAlta().getId(), null);
        }

        return retVal;
    }

    public void cancelarUsuario() {
        setU("");
        setUsuarioVOAlta(null);
        setRhPuestoVo(null);
        setIdGerencia(-1);
//        this.mostrarNuevoUusario = false;
//        this.mostrarTitulo = true;
    }

    public String buscarUsuarioSolicitudNuevoIngreso() {
//        Usuario us = this.buscarPorNombre(getU());
        if (getU().equals("")) {
            FacesUtils.addInfoMessage("Por favor escribar el nombre del usuario que desea buscar  ");
            return "";
        } else {
            llenarUsuarioVOAlta(buscarPorNombre(getU()));
            if (getUsuarioVOAlta() != null) {
                setIdGerencia(getUsuarioVOAlta().getIdGerencia());
                setIdCampo(getUsuarioVOAlta().getIdCampo());
                traerListaMateriales();
                return "";
            } else {
                FacesUtils.addInfoMessage("No, se encontro el usuario: " + getU());
                return "";
            }
        }
    }

    public void llenarUsuarioVOAlta(UsuarioVO u) {
        if (u == null) {
            setUsuarioVOAlta(null);
        } else {
            //Traer puesto del usuario
            setUsuarioVOAlta(new UsuarioVO());
            getUsuarioVOAlta().setId(u.getId());
            getUsuarioVOAlta().setNombre(u.getNombre());
            getUsuarioVOAlta().setClave(u.getClave());
            CampoUsuarioPuestoVo campoPuesto = traerPuesto(u.getId(), u.getIdCampo());

            if (campoPuesto != null) {
                setIdPuesto(campoPuesto.getIdPuesto());
                getUsuarioVOAlta().setIdPuesto(campoPuesto.getIdPuesto());
                getUsuarioVOAlta().setPuesto(campoPuesto.getPuesto());
                setIdPuesto(campoPuesto.getIdPuesto());
            }
            getUsuarioVOAlta().setMail(u.getMail());

            getUsuarioVOAlta().setUsuarioDirectorio(u.getUsuarioDirectorio());

            getUsuarioVOAlta().setDestinatarios(u.getDestinatarios());
            getUsuarioVOAlta().setRfc(u.getRfc());
            getUsuarioVOAlta().setTelefono(u.getTelefono());
            getUsuarioVOAlta().setExtension(u.getExtension());
            getUsuarioVOAlta().setCelular(u.getCelular());
            getUsuarioVOAlta().setSexo(u.getSexo());
            //
            getUsuarioVOAlta().setIdCampo(u.getIdCampo());
            getUsuarioVOAlta().setCampo(u.getCampo());
            getUsuarioVOAlta().setActivo(u.isActivo());
            getUsuarioVOAlta().setPregunta(u.getPregunta());
            getUsuarioVOAlta().setRespuesta(u.getRespuesta());
            //Otros 5
            getUsuarioVOAlta().setFechaIngreso(u.getFechaIngreso());
            getUsuarioVOAlta().setFechaNacimiento(u.getFechaNacimiento());
            //if (u.getIdOficina() > 0) {
                getUsuarioVOAlta().setOficina(u.getOficina());
                getUsuarioVOAlta().setIdOficina(u.getIdOficina());
            //}

            if (u.getGerencia() == null) {
                setIdGerencia(-1);
            } else {
                getUsuarioVOAlta().setIdGerencia(u.getIdGerencia());
                getUsuarioVOAlta().setGerencia(u.getGerencia());
            }

            if (u.getIdNomina() != 0) {
                getUsuarioVOAlta().setIdNomina(u.getIdNomina());
            }

            if (sesion.getUsuarioVo() != null) {
                getUsuarioVOAlta().setUsuarioInSessionGerente(usuarioResponsableForAnyGerencia());
            }

            UsuarioRolVo uvo = traerRolPrincipal(u.getId(), u.getIdCampo());

            if (uvo != null) {
                getUsuarioVOAlta().setRolPrincipal(uvo.getNombreRol());
                getUsuarioVOAlta().setRolId(uvo.getIdRol());
            }
        }
    }

    public UsuarioRolVo traerRolPrincipal(String idUsuario, int idCamp) {
        return siUsuarioRol.traerRolPrincipal(idUsuario, Constantes.MODULO_SGYL, idCamp);
    }

    public boolean usuarioResponsableForAnyGerencia() {
        return gerenciaImpl.isUsuarioResponsableForAnyGerencia(-1, sesion.getUsuarioVo().getId(), false);
    }

    public void cargarListaMateriales() {
        traerListaMateriales();
    }

//    public DataModel getTraerMaterial() {
//        if (getUsuarioVOAlta() != null) {
//            UtilLog4j.log.info(this, "Usuario vo alta: " + getUsuarioVOAlta().getNombre());
//            //traerListaMateriales();
//            return getListaMaterial();
//        }
//        return getLista();
//    }
    public String buscarUsuario() {
//        Usuario us = this.buscarPorNombre(getU());
        if (getUsuarioVOAlta() != null) {
            setIdGerencia(getUsuarioVOAlta().getIdGerencia());
            setIdCampo(getUsuarioVOAlta().getIdCampo());
            return "modificarUsuario";
        } else {
            FacesUtils.addInfoMessage("No, se encontro el usuario: " + getU());
            return "";
        }
    }

    public String solicitudMaterial() {

        setLista(null);
        //setUsuarioVOAlta(new UsuarioVO());
        setUsuarioVOAlta(null);
        setNombre(null);
        setPrimerApellido(null);
        setIdGerencia(-1);
        setSolicitaEstancia(false);
        setU("");
        setRhPuestoVo(new RhPuestoVo());
        return "/vistas/recursos/solicitudMaterialEmpleado.xhtml?faces-redirect=true";
    }

    public String agregarUsuario() {
//        this.usuario = null;
        setUsuarioVOAlta(new UsuarioVO());
        getUsuarioVOAlta().setGafete("si");
        setNombre(null);
        setPrimerApellido(null);
        setIdGerencia(-1);
        setRhPuestoVo(new RhPuestoVo());
//        setListaUsuarios(listaPuestos());
//        cancelarUsuario(event);
        return "/vistas/recursos/altaNuevoIngresoRH.xhtml?faces-redirect=true";
    }

    public void cambiarTipoUsuario(ValueChangeEvent event) {
        setPreguntaEntero((Integer) event.getNewValue());
    }

    public void eiminarUsuario() {
        this.eiminarUsuario();
        this.setUsuarioVOAlta(null);
        setU("");
//        this.mostrarTitulo = true;
    }

    public void activarUsuario() {
        activarUsuario();
        //this.usuario = this.buscarPorId(this.usuario.getId());
//        soporteProveedor.setUsuario(null);
        setUsuarioVOAlta(null);
        setU("");
    }

    public void cambiarCampoUsuario() {
        lleanrGerencias();
    }

    public void modificarUsuario() {

        if (getIdGerencia() > 0) {
            if (this.validaMail(getUsuarioVOAlta().getMail())
                    && this.validaMail(getUsuarioVOAlta().getDestinatarios())) {

                if (servicioUsuario.modificarUsuario(getUsuarioVOAlta(), getIdGerencia(), getIdCampo())) {
                    setIdGerencia(-1);
                    setIdCampo(sesion.getUsuarioVo().getIdCampo());
                    setU("");
                    FacesUtils.addInfoMessage("Se modificaro los datos del usuario");
                } else {
                    FacesUtils.addInfoMessage("Ocurrio un error . . .  +  +  +");
                }

            } else {
                FacesUtils.addInfoMessage("Mail no válido");
            }

        } else {
            FacesUtils.addInfoMessage("Es necesario seleccionar una gerencia");
        }

    }

    public void cancelarModificacionUuario() {
        //this.usuario = null;
        setUsuarioVOAlta(null);
        setU("");
    }

    public void cambiarCampoUsuario(ValueChangeEvent valueChangeEvent) {
        setIdCampo((Integer) valueChangeEvent.getNewValue());
    }

    public void lleanrGerencias() {
        List<GerenciaVo> lstGerencia;
        try {
            gerencias.clear();
            lstGerencia = gerenciaImpl.getAllGerenciaByApCampo(getIdCampo(), "nombre", true, null, false);
            for (GerenciaVo ger : lstGerencia) {
                SelectItem item = new SelectItem(ger.getId(), ger.getNombre());
                gerencias.add(item);
            }

        } catch (Exception e) {
            LOGGER.error(this, e);
        }
    }

    public void traerCadenaMando() {
        try {
            if (this.sesion.getUsuarioVo() != null) {

                //cadenasMandoImpl.traerCadenaAprobacion(sesion.getUsuarioVo().getNombre(), this.sesion.getUsuarioVo().getIdCampo());
            }

        } catch (Exception e) {
        }
    }
    //Autocompletar jefe inmediato

    public List<String> puestoListener(String texto) {
        System.out.println("@puestoListener");
        //List<String> puestos = new ArrayList<>();
        setListaPuestos(regresaPuesto(texto));
               
        /*getListaPuestos().stream().forEach(listaPuesto -> {
            puestos.add(listaPuesto.getLabel());
        });*/
        
        //usuarioFiltrados.stream().filter(t -> t.toLowerCase().startsWith(texto.toLowerCase())).collect(Collectors.toList());
        return getListaPuestos()
                .stream()
                .map(SelectItem::getLabel)
                .filter(crit -> crit.toLowerCase().startsWith(texto.toLowerCase()))
                .collect(Collectors.toList()); 

    }

    public List<SelectItem> regresaPuesto(String cadena) {
        return soporteProveedor.regresaPuesto(cadena);
    }

//Lista de oficinas
    public List<SelectItem> getListaOficina() {
        if (getUsuarioVOAlta() != null) {
            return listaOficina();
        }
        return null;
    }

    public List<SelectItem> listaOficina() {
        List<OficinaVO> lo;
        List<SelectItem> li = null;
        try {
            lo = sgOficinaImpl.getIdOffices();
            li = new ArrayList<>();
            for (OficinaVO i : lo) {
                SelectItem item = new SelectItem(i.getId(), i.getNombre());
                li.add(item);
            }

        } catch (Exception e) {
            LOGGER.error(this, "Excepcion en traer oficina " + e.getMessage(), e);
        }

        return li;
    }

    public List<SelectItem> getListaEmpresa() {
        if (getUsuarioVOAlta() != null) {
            return listaEmpresa();
        }
        return null;
    }

    public List<SelectItem> listaEmpresa() {
        List<SgEmpresa> lo;
        List<SelectItem> li = null;
        try {
            lo = sgEmpresaImpl.getAllCompanyByNomina();
            li = new ArrayList<>();
            for (SgEmpresa i : lo) {
                SelectItem item = new SelectItem(i.getId(), i.getNombre());
                li.add(item);
            }

        } catch (Exception e) {
            LOGGER.error(this, "Excepcion en traer empresa " + e.getMessage(), e);
        }

        return li;
    }

    public List<SelectItem> traerListaOficinasItems() {
        List<SelectItem> ls = null;
        try {
            List<OficinaVO> lo = sgOficinaImpl.getIdOffices();

            ls = new ArrayList<>();

            for (OficinaVO vo : lo) {
                SelectItem item = new SelectItem(vo.getId(), vo.getNombre());
                ls.add(item);
            }

        } catch (Exception e) {
            LOGGER.error(this, "Excepcion al trae la lista de oficinas ", e);
        }

        return ls;
    }

    public void buscarUsuarioConsulta() {
        UsuarioVO usuaroiSel = buscarPorNombre(getU());
        if (usuaroiSel != null) {
            llenarUsuarioVOAlta(usuaroiSel);
            setIdCampo(getUsuarioVOAlta().getIdCampo());
            lleanrGerencias();
            setIdGerencia(getUsuarioVOAlta().getIdGerencia());

            UtilLog4j.log.info(this, "Usr:{0}", new Object[]{getUsuarioVOAlta().getNombre()});
        } else {
            UtilLog4j.log.info(this, "No se encontro el usuario");
        }
    }

    public void limpiarVar() {
//        this.mostrarTitulo = true;
//        this.mostrarNuevoUusario = false;
        this.limpiar();
        setParametroTipoUsuario(1);
    }

    public void limpiar() {
//        this.mostrarNuevoUusario = false;
        setU("");
    }

//    // INICIO DE LOS MÉTODOS PARA CAMBIAR PASS
//    public String traerUsuario() {
//	try {
//	    this.usuario = this.buscarPorId(getU());
//	    if (this.usuario == null) {
//		FacesUtils.addInfoMessage("No se encontro el usuario, favor de verificar . . .");
//	    } else {
//		return "/vistas/usuario/olvidoPassword";
//	    }
//	} catch (Exception e) {
//	    FacesUtils.addInfoMessage("No se encontro el usuario, favor de verificar . . .");
//	}
//	return "";
//    }
    public String regresarLogin() {
        return "/principal";
    }

    public String cancelarVerificacion() {
        setPreguntaEntero(1);
        setUsuarioVOAlta(null);
        setRespuesta("");
        return "/principal";
    }

    public void verificarRespuesta() {
        if (getPreguntaEntero() == 3) {
            if (getUsuarioVOAlta().getRespuesta().equals(getRespuesta())) {
                this.setAgregarNuevoPass(1);
            } else {
                this.setAgregarNuevoPass(0);
                setPreguntaEntero(1);
                FacesUtils.addErrorMessage("Su respuesta no es la correcta. . . ");
            }
        } else {
            setPreguntaEntero(1);
            setAgregarNuevoPass(0);
            FacesUtils.addErrorMessage("Su respuesta no es la correcta. . . ");
        }
    }

    public String cancelarCambioContrasenia() {
        setPreguntaEntero(1);
        setCambiarPass("no");
        setAgregarNuevoPass(0);
        return "/principal";
    }

    public String cambioContrasenia() throws NoSuchAlgorithmException {
        boolean v;
        if (getC().equals(getConfirmarPassword())) {
            v = this.cambioContrasenia(getC(), getConfirmarPassword());
            if (v) {
                setRespuesta("");
                setUsuarioVOAlta(null);

                return "/principal";
            } else {
                FacesUtils.addInfoMessage("Ocurrio un error, favor de comunicarse con el equipo de desarrollo del SIA");
                return "/vistas/usuario/olvidoPassword";
            }
        } else {
            FacesUtils.addInfoMessage("Las contraseñas no coinciden, favor de verificar");
            return "/vistas/usuario/olvidoPassword";
        }
    }

    public boolean cambioContrasenia(String c, String confirmarPasswor) throws NoSuchAlgorithmException {
        return servicioUsuario.cambioContrasenia(getUsuarioVOAlta().getId(), encriptar(c), confirmarPasswor);
    }
//Fin de olvido de pass

    /// modificaicon de datos del usuario
    public String cancelarModificaUsuario() {
//	this.c = "";
//	this.nuevaClave = "";
        setCambiarPass("no");
        return "/principal";
    }

    public String modificaUsuario() throws NoSuchAlgorithmException {
        boolean v;
        String inicio = "";

        int errors = 0;

        LOGGER.info(this, "nombre: " + sesion.getUsuarioVo().getNombre());

        if (!validaMail(sesion.getUsuarioVo().getMail())) {
            FacesUtils.addErrorMessage("Favor de verificar el mail");
            errors++;
        }
        if (!validaMail(sesion.getUsuarioVo().getDestinatarios())) {
            FacesUtils.addErrorMessage("Favor de verificar el mail de destinatarios");
            errors++;
        }
//////        if (this.validateTextHastNotPunctuation(sesion.getUsuarioVo().getNombre())) {
//////            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sia.Usuario.nombre.valida.false"));
//////            errors++;
//////        }

        if (errors == 0) {
            if ("no".equals(getCambiarPass())) {
                try {
                    modificaUsuarioDatosSinClave(
                            sesion.getUsuarioVo().getId(),
                            sesion.getUsuarioVo().getNombre(),
                            sesion.getUsuarioVo().getMail(),
                            sesion.getUsuarioVo().getDestinatarios(),
                            sesion.getUsuarioVo().getRfc(),
                            sesion.getUsuarioVo().getTelefono(),
                            sesion.getUsuarioVo().getExtension(),
                            sesion.getUsuarioVo().getCelular()
                    );
//                    llenarUsuarioVO(
//                            buscarPorId(sesion.getUsuarioVo().getId())
//                    );

                    inicio = "/principal";
                } catch (Exception e) {
                    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.msg.error.guardar"));
//                    llenarUsuarioVO(
//                            buscarPorId(sesion.getUsuarioVo().getId())
//                    );

                    LOGGER.error(e);

                    return "/principal";
                }
            } else if ("si".equals(getCambiarPass())) {
                //FIXME : no está funcionando el cambio de contraseña, esto incluso antes de mis cambios (mrojas)
                // getClaveActual() no trae valor
                boolean valid
                        = encriptar(getClaveActual())
                                .equals(sesion.getUsuarioVo().getClave());

                if (valid) {
                    if (!getC().isEmpty()) {
                        if (getC().equals(getNuevaClave())) {
                            try {
                                v = modificaUsuarioDatosConClave(
                                        sesion.getUsuarioVo().getId(),
                                        sesion.getUsuarioVo().getNombre(),
                                        sesion.getUsuarioVo().getMail(),
                                        sesion.getUsuarioVo().getDestinatarios(),
                                        sesion.getUsuarioVo().getRfc(),
                                        sesion.getUsuarioVo().getTelefono(),
                                        sesion.getUsuarioVo().getExtension(),
                                        sesion.getUsuarioVo().getCelular(),
                                        getNuevaClave()
                                );

                                if (v) {
//                                    llenarUsuarioVO(
//                                            buscarPorId(sesion.getUsuarioVo().getId())
//                                    );
                                    inicio = "/principal";
                                }
                            } catch (Exception e) {
                                FacesUtils.addErrorMessage(
                                        FacesUtils.getKeyResourceBundle("sistema.msg.error.modificar")
                                );

                                LOGGER.error(e);
                            }

                        } else {
                            FacesUtils.addErrorMessage("No coinciden las claves, favor de verificar");
                        }
                    } else {
                        FacesUtils.addErrorMessage("Agregue su nueva clave");
                    }
                } else {
                    FacesUtils.addErrorMessage("La clave actual no es correcta");
                }
            }
        }
        return inicio;
    }

    public void modificaUsuarioDatosSinClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular) {
        servicioUsuario.modificaUsuarioDatosSinClave(idUser, nombre, correo, destinatarios, rfc, telefono, ext, celular);
    }

    public boolean modificaUsuarioDatosConClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular, String nuevaClave) throws NoSuchAlgorithmException {
        return servicioUsuario.modificaUsuarioDatosConClave(idUser, nombre, correo, destinatarios, rfc, telefono, ext, celular, encriptar(nuevaClave));
    }

    public String traerPuestoUsusaio(String userId, int campoId) {
        return apCampoUsuarioRhPuestoImpl.getPuestoPorUsurioCampo(userId, campoId);
    }

    public CampoUsuarioPuestoVo traerPuesto(String userId, int campoId) {
        return apCampoUsuarioRhPuestoImpl.traerPuestoPorUsuarioCampo(userId, campoId);
    }

    public Usuario buscarPorId(String idUsuario) {
        return servicioUsuario.find(idUsuario);
    }

    public UsuarioVO buscarPorNombre(String userName) {
        return servicioUsuario.findByName(userName);
    }

    //Validaciones
    public boolean validaMail(String correo) {
        String[] mails = correo.split(",");
        boolean v = true;
        for (String string : mails) {
            v = mail(string.trim());
            if (!v) {
                break;
            }
        }

        return v;
    }

    public boolean mail(String correo) {

        boolean retVal = false;

        try {
            InternetAddress ia = new InternetAddress(correo);
            ia.validate();

            retVal = true;
        } catch (AddressException ex) {
            LOGGER.warn(this, "*** email : " + correo, ex);
        }

        return retVal;
    }
//Reinicio clave

    public String reinicioClave() {
        boolean v;
        v = servicioUsuario.reinicioClave(sesion.getUsuarioVo().getId(), usuarioVOAlta.getId());
        if (v) {
            FacesUtils.addInfoMessage("Se envio la clave al usuario");
            setUsuarioVOAlta(null);
            setU("");
            return "consultaUsuario";
        } else {
            FacesUtils.addInfoMessage("Ocurrio un error favor de notificar al equipo de desarrollo del SIA");
            return "";
        }
    }

    public void apruebanOrdenCompra() {
        setIdCampo(sesion.getUsuarioVo().getIdCampo());
        //return "agregarUsuarioCampo";
    }

    public void agregarUsuaroACampo() {
        setRhPuestoVo(new RhPuestoVo());
        setIdCampo(sesion.getUsuarioVo().getIdCampo());
        //return "agregarUsuarioCampo";
    }

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeEvent) {
        setIdCampo((Integer) valueChangeEvent.getNewValue());
        UtilLog4j.log.info(this, "campo: " + getIdCampo());
        setU("");
        setUsuarioVOAlta(null);
    }
    ///Lsita de campos
    ///Lsita de campos

    public List<SelectItem> getListaCampoPorUsuario() {
        return listaCampoUsuario();
    }

    public List<SelectItem> listaCampoUsuario() {
        List<SelectItem> l = null;
        List<CampoUsuarioPuestoVo> lc;
        try {
            lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioVo().getId());
            l = new ArrayList<>();
            for (CampoUsuarioPuestoVo ca : lc) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                l.add(item);
            }
        } catch (Exception e) {
            LOGGER.error(this, e);
        }

        return l;
    }

    public List<SelectItem> getListaCampo() {
        return listaCampo();
    }

    public List<SelectItem> listaCampo() {
        List<SelectItem> l = null;
        List<ApCampoVo> lc;
        try {
            lc = apCampoImpl.traerApCampo();
            l = new ArrayList<>();
            for (ApCampoVo ca : lc) {
                SelectItem item = new SelectItem(ca.getId(), ca.getNombre());
                l.add(item);
            }
        } catch (Exception e) {
            LOGGER.error(this, e);
        }

        return l;
    }

    public void seleccionarCampoUsuario() {
        setCampoUsuarioPuestoVo((CampoUsuarioPuestoVo) getLista().getRowData());
        getCampoUsuarioPuestoVo().setIdCampo(getCampoUsuarioPuestoVo().getIdCampo());
        setRhPuestoVo(new RhPuestoVo());
        getRhPuestoVo().setId(getCampoUsuarioPuestoVo().getIdPuesto());
        getRhPuestoVo().setNombre(getCampoUsuarioPuestoVo().getPuesto());
//	setUsuarioCampoPop(true);
        setAccion(1);
        //Buscar en campo-gerencia-responsable
        if (buscarCampoGerencia()) {
            setC("si");
            setIdGerencia(buscarGerenciaResponsable());
        } else {
            setIdGerencia(-1);
            setC("no");
        }
    }

    public boolean buscarCampoGerencia() {

        return !apCampoGerenciaImpl.buscarCampoGerencia(
                getCampoUsuarioPuestoVo().getIdUsuario(),
                getCampoUsuarioPuestoVo().getIdCampo()
        ).isEmpty();
    }

    public int buscarGerenciaResponsable() {
        return 0;
    }

//listener usuario capmpo
    public RhPuestoVo buscarPuestoPorNombre() {
        return rhPuestoImpl.findByName(getRhPuestoVo().getNombre(), false);
    }

    public void limpiarListaUser() {
        setListaUsuario(null);
    }
    //Aprobar oC/S

    public void agregarAprobadorOrdenCompra() {
        setUsuarioVOAlta(new UsuarioVO());
        setApruebaOCPop(true);
    }

    public DataModel getTraerAprobadorOrdenCompra() {
        try {
            if (sesion.getUsuarioVo().getIdCampo() > 0) {
                //setListaUser(traerUsuarioAprobanOrdenCompra(sesion.getUsuarioVo().getIdCampo()));
                setListaUsuario(traerUsuarioAprobanOrdenCompra(getIdCampo()));
                return getListaUsuario();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public DataModel traerUsuarioAprobanOrdenCompra(int campo) {
        return new ListDataModel(servicioUsuario.getApruebanOrden(campo));
    }

    public void eliminarUsuarioApruebaOC() {
        setUsuarioVOAlta((UsuarioVO) getListaUsuario().getRowData());
        eliminarUsuarioApruebaOC(sesion.getUsuarioVo().getId(), getUsuarioVOAlta().getId(), getIdCampo());
        setUsuarioVOAlta(null);
    }

    public void guardarUsuarioApruebaOC() {
        if (!getU().trim().isEmpty()) {
            aprobarOrdenCompra(getU(), getIdCampo());
            setApruebaOCPop(false);
            setUsuarioVOAlta(null);
            setU("");
            PrimeFaces.current().executeScript("$(dialogoAprobarOcs).modal('hide');");
        } else {
            FacesUtils.addErrorMessage("frmPopApruebaOC", FacesUtils.getKeyResourceBundle("sia.usuario.necesario"));
        }
    }

    public void eliminarUsuarioApruebaOC(String sesion, String idUser, int campo) {
        servicioUsuario.quitarUsuarioApruebaOrdenCompra(sesion, idUser, campo, Constantes.OCFLUJO_ACTION_APROBAR);
    }

    public void aprobarOrdenCompra(String u, int campo) {
        servicioUsuario.aprobarOrdenCompra(sesion.getUsuarioVo().getId(), u, campo, Constantes.OCFLUJO_ACTION_APROBAR);
    }

    public void cancelarUsuarioApruebaOC() {
        setApruebaOCPop(false);
        setUsuarioVOAlta(null);
        setU("");
    }

    //Genera solicitud de estancia
    public String solicitarEstancia() {

        solicitarEstancia();
        setUsuarioVOAlta(null);
        setFechaSalida(null);
        setSolicitaEstancia(false);
        clearComponent("frmUser", "userSelect");
        FacesUtils.addErrorMessage("frmUser", FacesUtils.getKeyResourceBundle("sia.solicitud.enviada"));
        return "";
    }

    public void cancelarSolicitarEstancia() {
        setUsuarioVOAlta(null);
        setFechaSalida(null);
        setSolicitaEstancia(false);

    }

    public void validaFechaFin(FacesContext context, UIComponent validate, Object value) {
        Date f = (Date) value;
        if (f != null) {
            if (f != null) {
                if (siManejoFechaLocal.compare(f, usuarioVOAlta.getFechaIngreso()) == -1) {
                    UtilLog4j.log.info(this, "Error fecha salida posterior");
                    ((UIInput) validate).setValid(false);
                    FacesUtils.addErrorMessage("frmSolicitudEstancia:mensajes", "La fecha de ingreso no puede ser mayor a la fecha de salida..");
                }
            }
        }
    }

    public void validaFechaInicio(FacesContext context, UIComponent validate, Object value) {
        Date f = (Date) value;
        if (f != null) {
            if (validarFechaInicioMenorAHoy(f)) {
                ((UIInput) validate).setValid(false);
                FacesUtils.addErrorMessage("frmSolicitudEstancia:mensajes", "La fecha de ingreso no puede ser menor a hoy..");
                //FacesUtils.addErrorMessage("popupCrearEditarSolicitudEstancia:msgCrearSolicitudEstancia", FacesUtils.getKeyResourceBundle("sistema.mensaje.error.fechaAnteriorHoy"));
            }
        }
    }

    public void clearComponent(String nombreFormulario, String nombreComponente) {
        UtilLog4j.log.info(this, "Limpiando el componente: " + nombreFormulario + ":" + nombreComponente);
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIComponent component = context.getViewRoot().findComponent(nombreFormulario + ":" + nombreComponente);
            UIComponent parentComponent = component.getParent();
            parentComponent.getChildren().clear();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Hubo algún error al limpiar el componente: " + nombreFormulario + ":" + nombreComponente);
        }
    }

    public void abrirPopup(String idUser) {
        UsuarioVO u = buscarPorNombre(idUser);
        if (u != null) {
            UtilLog4j.log.info(this, "8888888888888888888888usuario " + u.getNombre());
            llenarUsuarioVOAlta(u);
        } else {
            FacesUtils.addErrorMessage("El usuario no existe, por favor contacta al equipo del sia para atender esta sictuación al correo soportesia@ihsa.mx ");
        }
        setFlag(true);
    }

    public void cerrarPopup() {
        setFlag(false);
        setUsuarioVOAlta(null);
        traerUsuariosSinCorreo();
    }

    //-*************** iniciar baja de empleado
    //traer la lista de gerencias de la tabla usuario_gerencia, seleccionar para enviar notificacion que deben liberar al usuario
    public void traerListaGerenciasParaLiberar() {
        traerListaRhTipoGerencia();
    }

    public void traerListaRhTipoGerencia() {

        this.listaGerencias = new ListDataModel(gerenciaImpl.getAllGerenciaByApCampo(idCampo, "nombre", true, null, false));
        /**
         * 33 - Servicios Generales 61 - TI 54 - Finanzas - Obango 47 - HSE
         */
        List<GerenciaVo> list = getDataModelAsList(listaGerencias);
        for (GerenciaVo vo : list) {
            if (vo.getId() == SERVICIOS_GENERALES || vo.getId() == SERVICIOS_INFORMATICOS
                    || vo.getId() == FINANZAS || vo.getId() == HSE) {
                //pasar a la lista de sleccion
                getListaFilasSeleccionadas().add(vo);
            }
        }

    }

    public <T> List<T> getDataModelAsList(DataModel dm) {
        return (List<T>) dm.getWrappedData();
    }

    private void limpiarIniciarBajaUsuario() {
        setIdCampo(1);
        setListaGerenciasSeleccionadas(null);
    }

    public String buscarUsuarioBaja() {
//        Usuario us = this.buscarPorNombre(getU());
        if (getU().equals("")) {
//            FacesUtils.addInfoMessage("Por favor escribar el nombre del usuario que desea buscar  ");
            return "";
        } else {
            llenarUsuarioVOAlta(buscarPorNombre(getU()));
            if (getUsuarioVOAlta() != null) {   
                if (!rhUsuarioGerenciaImpl.verficiarProcesoBaja(usuarioVOAlta.getId())) {
                    setIdGerencia(getUsuarioVOAlta().getIdGerencia());
                    setIdCampo(getUsuarioVOAlta().getIdCampo());
                    limpiarIniciarBajaUsuario();

                    llenarListaApCamposItems();
                    setIdCampo(1);
                    llenarComboGerenciasPorCampo();

                    llenarComboGerenciasPorCampo();
                    agregarGerenciasDefaultParaInicioBaja();
                    setIdGerencia(-1);
                } else {
                    FacesUtils.addErrorMessage("El usuario " + getUsuarioVOAlta().getNombre() + " ya se encuentra en proceso de baja");
                    setUsuarioVOAlta(null);
                    setU("");
                    //clearComponent("formInicioBaja", "userSelect");
                }
                return "";
            } else {
                FacesUtils.addInfoMessage("No, se encontro el usuario: " + getU());
                return "";
            }
        }
    }

    public void confirmarBaja() {
        if (getUsuarioVOAlta() != null) {
            //if (existenFilasSeleccionada()) {
            if (getListaGerenciasSeleccionadas().getRowCount() > 0) {
                if (!getRespuesta().equals("")) {
                    //iniciar baja
                    setFlag(true);
                } else {
                    FacesUtils.addErrorMessage("Por favor escriba el motivo de baja");
                }
            } else {
                FacesUtils.addErrorMessage("Por favor agrege al menos una gerencia de la lista de gerencias ");
            }
        } else {
            FacesUtils.addErrorMessage("Por favor seleccione el empleado a dar de baja");
        }
    }

    public String cancelarInicioBaja() {
        setListaGerencias(null);
        setRespuesta("");
        getFilaSeleccionada().clear();
        setUsuarioVOAlta(null);
        clearComponent("formInicioBaja", "userSelect");
        return "principalRecursosHumanos";
    }

    private boolean existenFilasSeleccionada() {
        List<RhTipoGerenciaVo> l = getDataModelAsList(getListaGerencias());
        for (RhTipoGerenciaVo vo : l) {
            if (getFilaSeleccionada().get(vo.getId()).booleanValue()) {
                return true;
            }
        }
        return false;
    }

    public void iniciarBaja() {
        if (getUsuarioVOAlta() != null) {
            //if (existenFilasSeleccionada()) {
            if (!getRespuesta().equals("")) {
                //iniciar baja
                if (baja()) {
                    setFlag(false);
                    FacesUtils.addInfoMessage("Se ha iniciado la baja del usuario ");
                    setListaGerenciasSeleccionadas(null);
                    setRespuesta("");
                    //getFilaSeleccionada().clear();
                    setUsuarioVOAlta(null);
                    setIdGerencia(-1);
                    setIdCampo(-1);
                    setU("");
                } else {
                    FacesUtils.addErrorMessage("No se pudo iniciar la baja, por favor contacta al equipo del SIA para verificar esta situación al correo soportesia@ihsa.mx");
                }
            } else {
                FacesUtils.addErrorMessage("Por favor seleccione escriba el motivo de baja");
            }
            /*
	     * } else { FacesUtils.addErrorMessage("Por favor seleccione al
	     * menos una gerencia de la lista de gerencias "); }
             */
        } else {
            FacesUtils.addErrorMessage("Por favor seleccione el empleado a dar de baja");
        }
    }

    public boolean baja() {
//	List<Integer> listaEnviar = new ArrayList<Integer>();
        boolean retVal = false;

        try {
            List<GerenciaVo> list = getDataModelAsList(getListaGerenciasSeleccionadas());
            retVal = rhUsuarioGerenciaImpl.iniciarBajaEmpleado(this.usuarioVOAlta.getId(),
                    this.respuesta, list, usuarioVO.getId());
        } catch (Exception e) {
            LOGGER.error(this, "Excepcion al inicar baja " + e.getMessage(), e);
        }

        return retVal;
    }

    public List<String> puestoTextChanged(String cad) {
        List<String> puestos = new ArrayList<>();
        List<RhPuestoVo> pusVo = rhPuestoImpl.getRhPuestoLike(cad);
        pusVo.stream().forEach(p -> {
            puestos.add(p.getNombre());
        });
        return puestos;
    }

    public void seleccionarPuesto() {
        rhPuestoVo = rhPuestoImpl.findByName(rhPuestoVo.getNombre(), Boolean.FALSE);

    }

    public void cambiarValorCampoNuevoIngreso() {
        try {
            //llenar nueva lista de gerencias con el campo seleccioando
            gerencias = new ArrayList<>();
            List<ApCampoGerenciaVo> campGer = apCampoGerenciaImpl.findAllCampoGerenciaPorCampo(usuarioVOAlta.getIdCampo());
            campGer.stream().forEach(cg -> {
                gerencias.add(new SelectItem(cg.getIdGerencia(), cg.getNombreGerencia()));
            });
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al cambiar de valor al campo " + e.getMessage());
        }
    }

    public void cambiarValorCampo() {
        try {
            llenarComboGerenciasPorCampo();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al cambiar de valor al campo " + e.getMessage());
        }
    }

    public void cerrarConfirmacionBajaEmpleado() {
        setFlag(false);
    }

    //COMENTAR
    public void abrirPopupComentarBaja() {
        UsuarioGerenciaVo vo = (UsuarioGerenciaVo) getListaUsuarioFree().getRowData();
        setNombre("");
        setUsuarioGerenciaVo(vo);
        setFlag(true);
    }

    public void cerrarPopupComentarBaja() {
        setFlag(false);
    }

    public void comentarBaja() {
        if (getNombre() == null || getNombre().trim().isEmpty()) {
            FacesUtils.addErrorMessage("Por favor escriba el comentario");
//            FacesUtils.addErrorMessage("formPopupRecomendarViaje:inptxtRecomendacion",
            //             FacesUtils.getKeyResourceBundle("sgl.seguridad.recomendacion") + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        } else {
            crearComentario();
            setFlag(false);
            //ir a la nocia...
        }
    }

    public void crearComentario() {
        LOGGER.info(this, "usuarioGerenciaVo.getIdNoticia()" + usuarioGerenciaVo.getIdNoticia());
        coNoticiaImpl.nuevoComentario(
                usuarioGerenciaVo.getIdNoticia(),
                sesion.getUsuarioVo().getId(),
                getNombre(),
                true,
                false,
                sesion.getUsuarioVo().getIdCampo(),
                Constantes.MODULO_ADMIN_SIA
        );

//	return true;
    }

    public void agregarGerenciaAListaGerenciasSelccionadas() {
        try {
            if (getIdGerencia() != -1) {
                if (!gerenciaRepetidaEnGerenciasSeleccionadas()) {
                    if (agregarGerenciaAListaSeleccionadas()) {
                        setIdGerencia(-1);
                        FacesUtils.addInfoMessage("Se agrego la Gerencia correctamente..");
                    }
                } else {
                    FacesUtils.addErrorMessage("La gerencia seleccionada ya esta en la lista");
                }
            } else {
                FacesUtils.addErrorMessage("Por favor seleccione una gerencia de la lista");
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exceocion al agregargerenci " + e.getMessage());
        }
    }

    public boolean gerenciaRepetidaEnGerenciasSeleccionadas() {
        boolean retVal = false;

        List<GerenciaVo> listaActual = getDataModelAsList(listaGerenciasSeleccionadas);
        //saber si no existe
        for (GerenciaVo vo : listaActual) {
            if (vo.getId() == idGerencia) {
                retVal = true;
            }
        }
        return retVal;
    }

    public void quitarGerenciaDeListaGerenciasSelccionadas(Object obj) {
        try {
            GerenciaVo vo = (GerenciaVo) obj;
            if (vo != null) {
                if (quitarGerenciaAListaSeleccionadas(vo)) {
                    FacesUtils.addErrorMessage("Se ha quitado la gerencia ");
                } else {
                    FacesUtils.addErrorMessage("Existio un error al intentar quitar la gerencia seleccionada ");
                }
            }

        } catch (Exception e) {
            UtilLog4j.log.info(this, "Exceocion al quitar " + e.getMessage());
        }
    }

    public void agregarGerenciaACampo() {
        UtilLog4j.log.info(this, "----------------------agregarGerenciaACampo");
        try {
            //int idCampoAgregar = Integer.parseInt(FacesUtils.getRequestParameter("idCampoAgregar"));
            UtilLog4j.log.info(this, "idCampoAgregar " + getIdPuesto());
            if (getIdGerencia() != -1) {
                if (!vefiricarExistenciaGerenciaEnApCampo()) {
                    if (agrGerenciaACampo()) {
                        cerrarPopupAgregarGerencia();
                        //refrescar
                        llenarComboGerenciasPorCampo();
                        FacesUtils.addInfoMessage("Se ha agregado la gerencia a la lista..");
                    } else {
                        FacesUtils.addInfoMessage("Existió un error al agregar la gerencia al campo, por favor contacte al equipo del SIA para verificar esta situación al correo soportesia@ihsa.mx");
                    }
                } else {
                    FacesUtils.addInfoMessage("No se puede agregar la gerencia al campo, por que ya existe, para mas información contecte al equipo del SIA al correo soportesia@ihsa.mx");
                }
            } else {
                FacesUtils.addInfoMessage("Por favor seleccione una gerencia de la lista");
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Existio un error al dar de alta la gerencia " + e.getMessage());
        }
    }

    public boolean agrGerenciaACampo() {
        boolean retVal = false;

        LOGGER.info(this, "idCampo a agregar " + idPuesto);
        LOGGER.info(this, "idCampo gerencia " + idGerencia);
        try {
            //buscar gerencia por el id seleccionado
            ApCampoGerenciaVo vo = apCampoGerenciaImpl.findByCampoGerencia(1, idGerencia, false);
            if (vo != null) {
                //apCampoGerenciaImpl.guardarCampoGerenciaResponsable(usuarioVO.getId(), vo.getIdResponsable(), idPuesto, idGerencia);
                rhCampoGerenciaImpl.agregarRelacionCampoGerencia(idPuesto, idGerencia, vo.getIdResponsable(), usuarioVO.getId());
            }
            retVal = true;
        } catch (Exception e) {
            LOGGER.error(this, "Excepcion al agregar nueva gerencia al campo por RH" + e.getMessage(), e);
        }

        return retVal;
    }

    public boolean vefiricarExistenciaGerenciaEnApCampo() {
        //idPuesto : representa el idApCampo selccionado al que se le agregara la gerencia..

        return apCampoGerenciaImpl.findByCampoGerencia(idPuesto, idGerencia, false) != null;
    }

    public boolean agregarGerenciaAListaSeleccionadas() {
        boolean esta = false;
        List<GerenciaVo> listaGerencias = gerenciaImpl.getAllGerenciaByApCampo(idCampo, "nombre", true, null, false);
        List<GerenciaVo> listaSeleccionActual = getDataModelAsList(listaGerenciasSeleccionadas);
        //buscar la gerencia en la lista
        for (GerenciaVo vo : listaGerencias) {
            if (vo.getId() == idGerencia) {
                //agregar
                listaSeleccionActual.add(vo);
                LOGGER.info(this, "Se agrego la gerecia " + vo.getNombre() + " de responsable " + vo.getNombreResponsable());
                esta = true;
            }
        }
        return esta;
    }

    public boolean quitarGerenciaAListaSeleccionadas(GerenciaVo gerenciaVoQuitar) {
        boolean esta = false;

        try {

            List<GerenciaVo> listaSeleccionActual = getDataModelAsList(listaGerenciasSeleccionadas);
            listaSeleccionActual.remove(gerenciaVoQuitar);
            LOGGER.info(this, "Se quito correctamente la gerencia de a lista");
            esta = true;
        } catch (Exception e) {
            LOGGER.error(this, "Exc" + e.getMessage(), e);
        }

        return esta;
    }

    public boolean buscarGerenciaRepetidaEnGerenciasSeleccionadas() {
        boolean retVal = false;

        List<GerenciaVo> listaActual = getDataModelAsList(listaGerenciasSeleccionadas);
        //saber si no existe
        for (GerenciaVo vo : listaActual) {
            if (vo.getId() == idGerencia) {
                retVal = true;
            }
        }
        return retVal;
    }

    public void abrirPopupAgregarGerencia() {
        setIdGerencia(-1);
        setApruebaOCPop(true);
    }
    //cerrar el popup para agregar la gerencia a la lista de gerencias que liberan a los usuarios

    public void cerrarPopupAgregarGerencia() {
        setApruebaOCPop(false);
    }

    public void llenarDatosUsuario() {
        setUsuarioVOAlta(servicioUsuario.findByName(getU()));
        llenarUsuarioVOAlta(usuarioVOAlta);
        setIdPuesto(getUsuarioVOAlta().getIdPuesto());
        cambiarValorCampoNuevoIngreso();
        setU("");
    }

    public void modificarDatos() {
        if (modificarUsuarioRH()) {
            setUsuarioVOAlta(null);
            setIdGerencia(-1);
            setIdCampo(-1);
            setIdPuesto(-1);
            setU("");
            FacesUtils.addInfoMessage("Se modificaron los datos el usuario");
        } else {
            FacesUtils.addErrorMessage("Ocurrio una excepción");
        }
    }

    public boolean modificarUsuarioRH() {
        boolean v = false;
            
        System.out.println("@@fech nac"+usuarioVOAlta.getFechaNacimiento());
        System.out.println("f@@ech ing"+usuarioVOAlta.getFechaIngreso());

        try {
            servicioUsuario.modificarDatosUsuario(sesion.getUsuarioVo().getId(), getUsuarioVOAlta(), getIdPuesto());
            v = true;
        } catch (Exception e) {
            System.out.println("Ex "+e.getMessage());
            LOGGER.fatal(this, "Modificando usuario {0}", new Object[]{sesion.getUsuarioVo().getId()}, e);
        }

        return v;
    }

    public String cancelarModificacion() {
        setUsuarioVOAlta(null);
        setIdGerencia(-1);
        setIdCampo(-1);
        setIdPuesto(-1);
        return "/vistas/recursos/principalRecursosHumanos.xhtml";
        
    }

}
