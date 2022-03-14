/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.backing;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.PrimeFaces;
import sia.catalogos.bean.model.UsuarioListModel;
import sia.constantes.Constantes;
import sia.excepciones.EmailNotFoundException;
import sia.excepciones.SIAException;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.gerencia.vo.RhTipoGerenciaVo;
import sia.modelo.puesto.vo.RhPuestoVo;
import sia.modelo.usuario.vo.UsuarioGerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.ApCampoGerenciaVo;
import sia.servicios.sistema.vo.MenuSiOpcionVo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@ManagedBean(name = "usuarioBean")
@ViewScoped
public class UsuarioBean implements Serializable {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    /**
     * Creates a new instance of UsuarioBean
     */
    public UsuarioBean() {
    }

    @ManagedProperty(value = "#{usuarioListModel}")
    private UsuarioListModel usuarioListModel;

    @PostConstruct
    public void iniciar() {
        usuarioListModel.inicio();
        traerUsuarioJson();
        traerPuestoJson();
    }

    public String goToLiberarEmpleadoBaja() {
        //Limpiar variables
        usuarioListModel.setListaUsuarioFree(null);
        //Llenar datos
        usuarioListModel.setListaUsuarioFree(new ListDataModel(allUsuarioForSetFree()));
        return "/vistas/recursos/liberarEmpleado";
    }

    public int totalAllUsuarioForSetFree() {
        List<UsuarioGerenciaVo> list = allUsuarioForSetFree();
        return ((list != null && !list.isEmpty()) ? list.size() : 0);
    }

    public List<UsuarioGerenciaVo> allUsuarioForSetFree() {
        return usuarioListModel.allUsuarioForFree();
    }

    public void setFreeEmployee() {
        UsuarioGerenciaVo vo = (UsuarioGerenciaVo) usuarioListModel.getListaUsuarioFree().getRowData();

        try {
            this.usuarioListModel.setFreeEmployee(vo.getId());
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.Usuario.liberarSatisfactorio"));
            usuarioListModel.setListaUsuarioFree(new ListDataModel(allUsuarioForSetFree()));
        } catch (EmailNotFoundException enfe) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(enfe.getLiteral()) + ": " + enfe.getAllUsuariosWithoutEmail());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void comentarNotificia() {
        UsuarioGerenciaVo vo = (UsuarioGerenciaVo) getLista().getRowData();

        try {
            this.usuarioListModel.setFreeEmployee(vo.getId());
            FacesUtils.addInfoMessage(FacesUtils.getKeyResourceBundle("sia.Usuario.liberarSatisfactorio"));
            usuarioListModel.setListaUsuario(new ListDataModel(allUsuarioForSetFree()));
        } catch (EmailNotFoundException enfe) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle(enfe.getLiteral()) + ": " + enfe.getAllUsuariosWithoutEmail());
        } catch (Exception e) {
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public void buscarId() {
        String[] cad = usuarioListModel.getUsuarioVO().getMail().split("@");
        Usuario us = usuarioListModel.buscarPorId(cad[0].toUpperCase());
        if (us != null) {
            FacesUtils.addInfoMessage("Ya existe un usuario con el ID " + cad[0]);
        }
    }
///Campo

    public String goToModificarCorreoTi() {
        usuarioListModel.traerUsuariosSinCorreo();
        //setUsuarioVOAlta(null);
        return "altaCorreoTi";
    }

    public String goToIniciarBajaEmpleado() {
//        usuarioListModel.traerListaRhTipoGerencia();
        usuarioListModel.setListaGerencias(null);
        usuarioListModel.setRespuesta("");
//        usuarioListModel.getFilaSeleccionada().clear();
        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setU("");
        clearComponent("formInicioBaja", "userSelect");
        usuarioListModel.setFlag(false);

        usuarioListModel.llenarListaApCamposItems();
        usuarioListModel.setIdCampo(1);
        usuarioListModel.llenarComboGerenciasPorCampo();
        usuarioListModel.agregarGerenciasDefaultParaInicioBaja();
        traerUsuarioJson();
        return "/vistas/recursos/inicioBajaEmpleado";
    }

    public String gotoFinalizaBaja() {
        setUsuarioVOAlta(null);
        return "finalizaBaja";
    }

    public String cambiarCampo() {
        CampoUsuarioPuestoVo campoUsuarioPuesto = (CampoUsuarioPuestoVo) usuarioListModel.getLista().getRowData();
        UtilLog4j.log.info(this, "Campo: " + campoUsuarioPuesto.getCampo() + "id: " + campoUsuarioPuesto.getIdCampo());
        usuarioListModel.cambiarUsuarioPuesto(getUsuarioVO().getId(), getUsuarioVO().getId(), campoUsuarioPuesto.getIdCampo());
        //usuarioListModel.traerCampoUsuario();
        usuarioListModel.llenarUsuarioVO(usuarioListModel.buscarPorId(usuarioListModel.getUsuarioVO().getId()));
        return "";
    }
///Fin de campo

    public String guardarUsuarioNuevoIngreso(String param) throws NoSuchAlgorithmException {
        boolean v;
        if (!this.usuarioListModel.validateTextHastNotPunctuation(getNombre())) {
            if (!this.usuarioListModel.validateTextHastNotPunctuation(getPrimerApellido())) {
                if (!this.usuarioListModel.validateTextHastNotPunctuation(getSegundoApellido())) {
                    if (usuarioListModel.getUsuarioVOAlta().getIdCampo() > 0) {
                        if (usuarioListModel.getUsuarioVOAlta().getIdOficina() > 0) {
                            if (getIdGerencia() > 0) {
                                if (usuarioListModel.verificaPuesto()) {

                                    //if (!usuarioListModel.getUsuarioVOAlta().getIdJefe().isEmpty()) {
                                    if (usuarioListModel.getUsuarioVOAlta().getFechaIngreso() != null) {
                                        UtilLog4j.log.info(this, "nombre puesto: " + getRhPuestoVo().getNombre());
                                        if (usuarioListModel.getUsuarioVOAlta().getIdNomina() > 0) {
                                            if (usuarioListModel.validaMail(usuarioListModel.getUsuarioVOAlta().getMail())) {
                                                try {
                                                    v = this.usuarioListModel.guardarUsuarioNuevoIngreso();//usuarioListModel.getUsuarioVO().getId(), usuarioListModel.getUsuarioVOAlta(), getIdGerencia());
                                                } catch (Exception e) {
                                                    FacesUtils.addErrorMessage("frmUser:error", "Ocurrió un error al guardar el Usuario");
                                                    v = false;
                                                    LOGGER.warn(this, "", e);
                                                }
                                                if (v) {
                                                    this.limpiar();
                                                    usuarioListModel.setListaPuestos(null);
                                                    //    setListaUsuariosAlta(null);
                                                    usuarioListModel.traerListaMateriales();
                                                    traerUsuarioJson();
                                                    if (param != null && !param.isEmpty() && "AD".equals(param)) {
                                                        FacesUtils.addInfoMessage("El usuario se guardo exitosamente. ");
                                                        cancelarNuevoIngreso();
                                                        return "/vistas/recursos/altaNuevoIngresoAdmin";
                                                    } else {
                                                        return "solicitudMaterialEmpleado";
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

    public String guardarUsuarioNuevoIngresoPrueba() throws NoSuchAlgorithmException {
        boolean v = true;
        //String pagina = "";
        //    v = this.usuarioListModel.guardarUsuarioNuevoIngreso();//usuarioListModel.getUsuarioVO().getId(), usuarioListModel.getUsuarioVOAlta(), getIdGerencia());
        if (v) {
            return "solicitudMaterialEmpleado";
        } else {
            FacesUtils.addInfoMessage("frmUser:error", "Ocurrio un error . . .  + + + ");
        }

        return "";
    }

    public void modificarCorreoPorTi() {
        try {
            if (!usuarioListModel.getUsuarioVOAlta().getMail().equals("")) {
                if (usuarioListModel.validaMail(usuarioListModel.getUsuarioVOAlta().getMail())) {
                    //saber si cambio el correo
                    if (usuarioListModel.verificarModificacionMail()) {
                        if (usuarioListModel.guardarDireccionCorreoReal(getUsuarioVO().getId())) {
                            cerrarPopup(event);
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

    public String solicitarMaterialUSuarios() {
        if (getUsuarioVOAlta() != null) {
            if (usuarioListModel.verificaLista().size() > 0) {
                if (usuarioListModel.solicitarMaterial()) {
                    FacesUtils.addInfoMessage("frmUser:error", "Se enviaron las solicitudes");
                    if (!usuarioListModel.isSolicitaEstancia()) {
                        usuarioListModel.setSolicitaEstancia(false);
                        this.limpiar();
                        this.setU("");
                        clearComponent("frmUser", "userSelect");
                        this.getFilaSeleccionada().clear();
                        usuarioListModel.traerListaMateriales();
                        return "solicitudMaterialEmpleado";
                    } else {
                        usuarioListModel.setFechaSalida(usuarioListModel.sumarDias());
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

    //
    public String guardarUsuario() throws NoSuchAlgorithmException {
        boolean v;
        String[] cad = usuarioListModel.getUsuarioVOAlta().getMail().split("@");
        String posibleId = this.usuarioListModel.removeSpecialCharactersReplacingWithASCII(cad[0]);

        int errors = 0;

        if (!this.usuarioListModel.validaMail(getUsuarioVOAlta().getMail()) || this.usuarioListModel.validateTextHastNotPunctuation(posibleId)) {
            FacesUtils.addInfoMessage("frmUser:error", "Mail no válido");
            FacesUtils.addInfoMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.email.msg.noValido.caracteresEspeciale"));
            errors++;
        }
        if (getUsuarioVOAlta().getIdCampo() <= 0) {
            FacesUtils.addInfoMessage("frmUser:error", "Campo es requerido");
            errors++;
        }
        if (this.usuarioListModel.validateTextHastNotPunctuation(getUsuarioVOAlta().getNombre())) {
            FacesUtils.addErrorMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.nombre.valida.false"));
            errors++;
        }
        if (getIdGerencia() <= 0) {
            FacesUtils.addInfoMessage("frmUser:error", "Gerencia es requerido");
            errors++;
        }
        if (!this.usuarioListModel.validaMail(getUsuarioVOAlta().getDestinatarios()) || this.usuarioListModel.validateTextHastNotPunctuation(getUsuarioVOAlta().getDestinatarios())) {
            FacesUtils.addInfoMessage("frmUser:error", "Destinatarios no válido");
            FacesUtils.addInfoMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.email.msg.noValido.caracteresEspeciale"));
            errors++;
        }

        if (errors == 0) {
            usuarioListModel.getUsuarioVOAlta().setId(posibleId);
            if (this.usuarioListModel.traerIdUsuario(usuarioListModel.getUsuarioVOAlta().getId().toUpperCase()) == null) {
                usuarioListModel.getUsuarioVOAlta().setIdPuesto(usuarioListModel.getRhPuestoVo().getId());
                usuarioListModel.getUsuarioVOAlta().setClave(usuarioListModel.encriptar(usuarioListModel.getUsuarioVOAlta().getClave()));
                v = this.usuarioListModel.guardarUsuario(usuarioListModel.getUsuarioVO().getId(), usuarioListModel.getUsuarioVOAlta(), getIdGerencia());
                if (v) {
//                    this.soporteProveedor.setUsuario(null);
//                    soporteProveedor.setPuestoVo(null);
                    usuarioListModel.setRhPuestoVo(null);
                    usuarioListModel.setUsuarioVOAlta(null);
                    this.limpiar();
                    return "consultaUsuario";
                } else {
                    FacesUtils.addInfoMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sistema.msg.error.guardar"));
                }
            } else {
                FacesUtils.addInfoMessage("frmUser:error", "El id " + usuarioListModel.getUsuarioVOAlta().getId() + " ya existe, por favor elija otro");
            }
        }
        return "";
    }

    public void cancelarNuevoIngreso() {
        cancelarNuevoIngreso();
    }

    public void cancelarNuevoIngreso() {
        usuarioListModel.setU("");
        usuarioListModel.setUsuarioVOAlta(new UsuarioVO());
        setRhPuestoVo(new RhPuestoVo());
        usuarioListModel.setIdGerencia(-1);
        usuarioListModel.setNombre("");
        usuarioListModel.setPrimerApellido("");
        usuarioListModel.setSegundoApellido("");
        this.getFilaSeleccionada().clear();
    }

    public List<UsuarioGerenciaVo> getTraerUsuarioProcesoBaja() {
        return usuarioListModel.traerUsuarioProcesoBaja();
    }

    public void finalizarBaja() {
        usuarioListModel.setU(FacesUtils.getRequestParameter("usSelec"));
        usuarioListModel.setRespuesta(usuarioListModel.getU());
        if (usuarioListModel.finalizarBaja()) {
            FacesUtils.addInfoMessage("Se termino con el proceso de baja de usuario");
        } else {
            FacesUtils.addInfoMessage("Ocurrio un error, favor de cominicar al equipo de desarrollo del SIA (sia@ihsa.mx). ");
        }
    }

    public void cancelarUsuario() {
        usuarioListModel.setU("");
        usuarioListModel.setUsuarioVOAlta(null);
        setRhPuestoVo(null);
        usuarioListModel.setIdGerencia(-1);
//        this.mostrarNuevoUusario = false;
//        this.mostrarTitulo = true;
    }

    public String buscarUsuarioSolicitudNuevoIngreso() {
//        Usuario us = this.usuarioListModel.buscarPorNombre(getU());
        if (getU().equals("")) {
            FacesUtils.addInfoMessage("Por favor escribar el nombre del usuario que desea buscar  ");
            return "";
        } else {
            usuarioListModel.llenarUsuarioVOAlta(usuarioListModel.buscarPorId(usuarioListModel.getU()));
            if (usuarioListModel.getUsuarioVOAlta() != null) {
                usuarioListModel.setIdGerencia(usuarioListModel.getUsuarioVOAlta().getIdGerencia());
                usuarioListModel.setIdCampo(usuarioListModel.getUsuarioVOAlta().getIdCampo());
                usuarioListModel.traerListaMateriales();
                return "";
            } else {
                FacesUtils.addInfoMessage("No, se encontro el usuario: " + usuarioListModel.getU());
                return "";
            }
        }
    }

    public void cargarListaMateriales() {
        usuarioListModel.traerListaMateriales();
    }

//    public DataModel getTraerMaterial() {
//        if (usuarioListModel.getUsuarioVOAlta() != null) {
//            UtilLog4j.log.info(this, "Usuario vo alta: " + usuarioListModel.getUsuarioVOAlta().getNombre());
//            //usuarioListModel.traerListaMateriales();
//            return usuarioListModel.getListaMaterial();
//        }
//        return getLista();
//    }
    public String buscarUsuario() {
//        Usuario us = this.usuarioListModel.buscarPorNombre(getU());
        if (usuarioListModel.getUsuarioVOAlta() != null) {
            usuarioListModel.setIdGerencia(usuarioListModel.getUsuarioVOAlta().getIdGerencia());
            usuarioListModel.setIdCampo(usuarioListModel.getUsuarioVOAlta().getIdCampo());
            return "modificarUsuario";
        } else {
            FacesUtils.addInfoMessage("No, se encontro el usuario: " + usuarioListModel.getU());
            return "";
        }
    }

    public void solicitudMaterial() {

        usuarioListModel.setLista(null);
        //usuarioListModel.setUsuarioVOAlta(new UsuarioVO());
        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setNombre(null);
        usuarioListModel.setPrimerApellido(null);
        usuarioListModel.setIdGerencia(-1);
        usuarioListModel.setSolicitaEstancia(false);
        usuarioListModel.setU("");
        usuarioListModel.setRhPuestoVo(new RhPuestoVo());
        usuarioListModel.traerUsuarioJson();
    }

    public void agregarUsuario() {
//        this.usuario = null;
        usuarioListModel.setUsuarioVOAlta(new UsuarioVO());
        usuarioListModel.getUsuarioVOAlta().setGafete("si");
        usuarioListModel.setNombre(null);
        usuarioListModel.setPrimerApellido(null);
        usuarioListModel.setIdGerencia(-1);
        usuarioListModel.setRhPuestoVo(new RhPuestoVo());
//        setListaUsuarios(usuarioListModel.listaPuestos());
//        cancelarUsuario(event);
    }

    public void cambiarTipoUsuario(ValueChangeEvent event) {
        usuarioListModel.setPreguntaEntero((Integer) event.getNewValue());
    }

    public DataModel getListaUsuarios() {
        try {
            return new ListDataModel(this.usuarioListModel.taerUsuarios());
        } catch (Exception e) {
            return null;
        }
    }

    public List<MenuSiOpcionVo> getListaMenus() {
        List<MenuSiOpcionVo> listaMenu = new ArrayList<MenuSiOpcionVo>();
        try {
            listaMenu.addAll(usuarioListModel.taerListaMenu(Constantes.MODULO_ADMINSIA, getUsuarioVO().getId(), getUsuarioVO().getIdCampo()));
        } catch (Exception e) {
            return new ArrayList<MenuSiOpcionVo>();
        }
        return listaMenu;
    }

    public List<SiOpcionVo> getListaSubMenus() {
        Collection<SiOpcionVo> s = new HashSet<SiOpcionVo>();
        try {
            for (UsuarioRolVo rol : usuarioListModel.traerRolesPorUsuario(getUsuarioVO().getId(), 0, getUsuarioVO().getIdCampo())) {
                s.addAll(usuarioListModel.taerOpcionesByRol(Constantes.MODULO_ADMINSIA, rol.getIdRol()));
            }
        } catch (Exception e) {
            return null;
        }
        return new ArrayList<SiOpcionVo>(s);
    }

    public DataModel getTraerUsuariosSinCorreo() {
        usuarioListModel.setLista(usuarioListModel.traerUsuariosSinCorreo());
        return usuarioListModel.getLista();
    }

    public void eiminarUsuario() {
        this.usuarioListModel.eiminarUsuario();
        this.usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setU("");
//        this.mostrarTitulo = true;
    }

    public void activarUsuario() {
        usuarioListModel.activarUsuario();
        //this.usuario = this.usuarioListModel.buscarPorId(this.usuario.getId());
//        soporteProveedor.setUsuario(null);
        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setU("");
    }

    public String modificarUsuario() {

        String retVal = "";

        if (getIdGerencia() > 0) {

            if (this.usuarioListModel.validaMail(usuarioListModel.getUsuarioVOAlta().getMail())
                    && this.usuarioListModel.validaMail(usuarioListModel.getUsuarioVOAlta().getDestinatarios())) {

                if (usuarioListModel.modificarUsuario()) {
                    usuarioListModel.setIdGerencia(-1);
                    usuarioListModel.setIdCampo(getUsuarioVO().getIdCampo());
                    //   this.usuario = usuarioListModel.buscarPorId(usuarioListModel.getUsuarioVOAlta().getId());
                    setU("");
                    retVal = "consultaUsuario";
                } else {
                    FacesUtils.addInfoMessage("Ocurrio un error . . .  +  +  +");
                }

            } else {
                FacesUtils.addInfoMessage("Mail no válido");
            }

        } else {
            FacesUtils.addInfoMessage("Es necesario seleccionar una gerencia");
        }

        return retVal;
    }

    public void cancelarModificacionUuario() {
        //this.usuario = null;
        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setU("");
    }

    public void cambiarCampoUsuario(ValueChangeEvent valueChangeEvent) {
        usuarioListModel.setIdCampo((Integer) valueChangeEvent.getNewValue());
    }

    public List<SelectItem> getListaGerencia() {
        try {
            return usuarioListModel.listaGerencia();
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel getTraerCadenaMando() {
        try {
            if (this.usuarioListModel.getUsuarioVO() != null) {
                return new ListDataModel(this.usuarioListModel.traerCadenaMando(usuarioListModel.getUsuarioVO().getNombre(), this.usuarioListModel.getUsuarioVO().getIdCampo()));
            }

        } catch (Exception e) {
            return null;
        }
        return null;
    }
    //Autocompletar jefe inmediato

    public void puestoListener(ValueChangeEvent textChangeEvent) {
        if (textChangeEvent.getComponent() instanceof SelectInputText) {
            SelectInputText autoComplete = (SelectInputText) textChangeEvent.getComponent();
            String cadenaDigitada = (String) textChangeEvent.getNewValue();
            usuarioListModel.setListaPuestos(usuarioListModel.regresaPuesto(cadenaDigitada));

            if (autoComplete.getSelectedItem() != null) {
                RhPuestoVo puestoSel = (RhPuestoVo) autoComplete.getSelectedItem().getValue();
                setRhPuestoVo(puestoSel);
                UtilLog4j.log.info(this, "puesto:" + puestoSel.getNombre());
                usuarioListModel.setListaPuestos(null);
            }
        }
    }

    public void puestoTextChanged(TextChangeEvent event) {
        usuarioListModel.setListaPuestos(usuarioListModel.regresaPuesto(event.getNewValue().toString()));
    }

//Lista de oficinas
    public List<SelectItem> getListaOficina() {
        if (getUsuarioVOAlta() != null) {
            return usuarioListModel.listaOficina();
        }
        return null;
    }

    public List<SelectItem> getListaEmpresa() {
        if (getUsuarioVOAlta() != null) {
            return usuarioListModel.listaEmpresa();
        }
        return null;
    }

    public String buscarUsuarioConsulta() {
        Usuario usuaroiSel = usuarioListModel.buscarPorId(getU());
        
        String p = "";
        
        if (usuaroiSel != null) {
            usuarioListModel.llenarUsuarioVOAlta(usuaroiSel);
            setIdCampo(usuarioListModel.getUsuarioVOAlta().getIdCampo());
            setIdGerencia(usuarioListModel.getUsuarioVOAlta().getIdGerencia());

            UtilLog4j.log.info(this, "Usr:{0}", new Object[]{usuarioListModel.getUsuarioVOAlta().getNombre()});

            p = "modificarUsuario";
        
        } else {
            UtilLog4j.log.info(this, "No se encontro el usuario");
        }
        
        return p;

    }

    public void limpiarVar() {
//        this.mostrarTitulo = true;
//        this.mostrarNuevoUusario = false;
        this.limpiar();
        usuarioListModel.setParametroTipoUsuario(1);
    }

    public void limpiar() {
//        this.mostrarNuevoUusario = false;
        usuarioListModel.setU("");
    }

//    // INICIO DE LOS MÉTODOS PARA CAMBIAR PASS
//    public String traerUsuario() {
//	try {
//	    this.usuario = this.usuarioListModel.buscarPorId(getU());
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
        usuarioListModel.setPreguntaEntero(1);
        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setRespuesta("");
        return "/principal";
    }

    public void verificarRespuesta() {
        if (usuarioListModel.getPreguntaEntero() == 3) {
            if (usuarioListModel.getUsuarioVOAlta().getRespuesta().equals(usuarioListModel.getRespuesta())) {
                this.setAgregarNuevoPass(1);
            } else {
                this.setAgregarNuevoPass(0);
                usuarioListModel.setPreguntaEntero(1);
                FacesUtils.addErrorMessage("Su respuesta no es la correcta. . . ");
            }
        } else {
            usuarioListModel.setPreguntaEntero(1);
            setAgregarNuevoPass(0);
            FacesUtils.addErrorMessage("Su respuesta no es la correcta. . . ");
        }
    }

    public String cancelarCambioContrasenia() {
        usuarioListModel.setPreguntaEntero(1);
        usuarioListModel.setCambiarPass("no");
        setAgregarNuevoPass(0);
        return "/principal";
    }

    public String cambioContrasenia() throws NoSuchAlgorithmException {
        boolean v;
        if (getC().equals(getConfirmarPassword())) {
            v = this.usuarioListModel.cambioContrasenia(getC(), getConfirmarPassword());
            if (v) {
                usuarioListModel.setRespuesta("");
                usuarioListModel.setUsuarioVOAlta(null);
                usuarioListModel.setUsuarioVO(null);

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
//Fin de olvido de pass

    /// modificaicon de datos del usuario
    public String cancelarModificaUsuario() {
//	this.c = "";
//	this.nuevaClave = "";
        usuarioListModel.setCambiarPass("no");
        return "/principal";
    }

    public String modificaUsuario() throws NoSuchAlgorithmException {
        boolean v;
        String inicio = "";

        int errors = 0;

        LOGGER.info(this, "nombre: " + getUsuarioVO().getNombre());

        if (!usuarioListModel.validaMail(usuarioListModel.getUsuarioVO().getMail())) {
            FacesUtils.addErrorMessage("Favor de verificar el mail");
            errors++;
        }
        if (!usuarioListModel.validaMail(usuarioListModel.getUsuarioVO().getDestinatarios())) {
            FacesUtils.addErrorMessage("Favor de verificar el mail de destinatarios");
            errors++;
        }
//////        if (this.usuarioListModel.validateTextHastNotPunctuation(getUsuarioVO().getNombre())) {
//////            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sia.Usuario.nombre.valida.false"));
//////            errors++;
//////        }

        if (errors == 0) {
            if ("no".equals(usuarioListModel.getCambiarPass())) {
                try {
                    usuarioListModel.modificaUsuarioDatosSinClave(
                            getUsuarioVO().getId(),
                            getUsuarioVO().getNombre(),
                            getUsuarioVO().getMail(),
                            getUsuarioVO().getDestinatarios(),
                            getUsuarioVO().getRfc(),
                            getUsuarioVO().getTelefono(),
                            getUsuarioVO().getExtension(),
                            getUsuarioVO().getCelular()
                    );

                    usuarioListModel.llenarUsuarioVO(
                            usuarioListModel.buscarPorId(usuarioListModel.getUsuarioVO().getId())
                    );

                    inicio = "/principal";
                } catch (Exception e) {
                    FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sistema.msg.error.guardar"));
                    usuarioListModel.llenarUsuarioVO(
                            usuarioListModel.buscarPorId(usuarioListModel.getUsuarioVO().getId())
                    );

                    LOGGER.error(e);

                    return "/principal";
                }
            } else if ("si".equals(usuarioListModel.getCambiarPass())) {
                //FIXME : no está funcionando el cambio de contraseña, esto incluso antes de mis cambios (mrojas)
                // usuarioListModel.getClaveActual() no trae valor
                boolean valid
                        = usuarioListModel.encriptar(usuarioListModel.getClaveActual())
                                .equals(getUsuarioVO().getClave());

                if (valid) {
                    if (!getC().isEmpty()) {
                        if (getC().equals(getNuevaClave())) {
                            try {
                                v = usuarioListModel.modificaUsuarioDatosConClave(
                                        getUsuarioVO().getId(),
                                        getUsuarioVO().getNombre(),
                                        getUsuarioVO().getMail(),
                                        getUsuarioVO().getDestinatarios(),
                                        getUsuarioVO().getRfc(),
                                        getUsuarioVO().getTelefono(),
                                        getUsuarioVO().getExtension(),
                                        getUsuarioVO().getCelular(),
                                        getNuevaClave()
                                );

                                if (v) {
                                    usuarioListModel.llenarUsuarioVO(
                                            usuarioListModel.buscarPorId(usuarioListModel.getUsuarioVO().getId())
                                    );
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
        } else {
            usuarioListModel.llenarUsuarioVO(usuarioListModel.buscarPorId(usuarioListModel.getUsuarioVO().getId()));
        }
        return inicio;
    }
//Reinicio clave

    public String reinicioClave() {
        boolean v;
        v = usuarioListModel.reinicioClave();
        if (v) {
            FacesUtils.addInfoMessage("Se envio la clave al usuario");
            usuarioListModel.setUsuarioVOAlta(null);
            usuarioListModel.setU("");
            return "consultaUsuario";
        } else {
            FacesUtils.addInfoMessage("Ocurrio un error favor de notificar al equipo de desarrollo del SIA");
            return "";
        }
    }

    public void apruebanOrdenCompra() {
        usuarioListModel.setIdCampo(usuarioListModel.getUsuarioVO().getIdCampo());
        //return "agregarUsuarioCampo";
    }

    public void agregarUsuaroACampo() {
        usuarioListModel.setRhPuestoVo(new RhPuestoVo());
        usuarioListModel.setIdCampo(usuarioListModel.getUsuarioVO().getIdCampo());
        //return "agregarUsuarioCampo";
    }

    public DataModel getTraerCampoUsuario() {
        usuarioListModel.setLista(usuarioListModel.traerCampoUsuario());
        return getLista();
    }

    public void cambiarSeleccionCampo(ValueChangeEvent valueChangeEvent) {
        usuarioListModel.setIdCampo((Integer) valueChangeEvent.getNewValue());
        UtilLog4j.log.info(this, "campo: " + usuarioListModel.getIdCampo());
        setU("");
        setUsuarioVOAlta(null);
        traerUsuarioJson();
    }
    ///Lsita de campos
    ///Lsita de campos

    public List<SelectItem> getListaCampoPorUsuario() {
        return usuarioListModel.listaCampoUsuario();
    }

    public List<SelectItem> getListaCampo() {
        return usuarioListModel.listaCampo();
    }

    public void agregarCampoUsuario() {
        usuarioListModel.setCampoUsuarioPuestoVo(new CampoUsuarioPuestoVo());
        usuarioListModel.getCampoUsuarioPuestoVo().setIdCampo(-1);
        usuarioListModel.setRhPuestoVo(new RhPuestoVo());
        usuarioListModel.getRhPuestoVo().setNombre("");
        traerUsuarioJson();
        traerPuestoJson();
        //usuarioListModel.setUsuarioCampoPop(true);
        usuarioListModel.setC("no");
        usuarioListModel.setAccion(0);
    }

    public void llenarUsuario() {
        traerUsuarioJson();
        traerPuestoJson();
    }

    public void traerUsuarioJson() {
        PrimeFaces.current().executeScript(";llenarJsonUsuario(" + usuarioListModel.getUsuarioJson() + ");");
    }

    public void traerPuestoJson() {
        PrimeFaces.current().executeScript(";llenarJsonPuesto(" + usuarioListModel.getPuestoJson() + ");");
    }

    public void seleccionarCampoUsuario() {
        usuarioListModel.setCampoUsuarioPuestoVo((CampoUsuarioPuestoVo) getLista().getRowData());
        usuarioListModel.getCampoUsuarioPuestoVo().setIdCampo(usuarioListModel.getCampoUsuarioPuestoVo().getIdCampo());
        usuarioListModel.setRhPuestoVo(new RhPuestoVo());
        usuarioListModel.getRhPuestoVo().setId(usuarioListModel.getCampoUsuarioPuestoVo().getIdPuesto());
        usuarioListModel.getRhPuestoVo().setNombre(usuarioListModel.getCampoUsuarioPuestoVo().getPuesto());
//	usuarioListModel.setUsuarioCampoPop(true);
        setAccion(1);
        //Buscar en campo-gerencia-responsable
        if (usuarioListModel.buscarCampoGerencia()) {
            setC("si");
            usuarioListModel.setIdGerencia(usuarioListModel.buscarGerenciaResponsable());
        } else {
            usuarioListModel.setIdGerencia(-1);
            setC("no");
        }
    }

    public void eliminarRelacion() {
        usuarioListModel.setCampoUsuarioPuestoVo((CampoUsuarioPuestoVo) getLista().getRowData());
        usuarioListModel.eliminarRelacion();
        usuarioListModel.setCampoUsuarioPuestoVo(null);
    }

    public void cambiaValorPuesto(ValueChangeEvent valueChangeEvent) {
        usuarioListModel.setIdCampo((Integer) valueChangeEvent.getNewValue());
        usuarioListModel.setIdGerencia(-1);
        usuarioListModel.getRhPuestoVo().setNombre("");
        usuarioListModel.setC("no");
        usuarioListModel.setListaGerenciasItems(usuarioListModel.listaGerencia());
    }
//listener usuario capmpo

    public void guardarUsuarioCampo() {
        if (usuarioListModel.getAccion() == 0) {
            if (usuarioListModel.verificaUsuarioCampoGuardar()) {
                usuarioListModel.setRhPuestoVo(usuarioListModel.buscarPuestoPorId());
                if (usuarioListModel.getRhPuestoVo() != null) {
                    usuarioListModel.guardarUsuarioCampo(usuarioListModel.getIdCampo(), usuarioListModel.getRhPuestoVo().getId());
                    UtilLog4j.log.info(this, "Guardó");
                    limpiarVariables();
                    PrimeFaces.current().executeScript(";dialogoOK('dialogoBloqueUsuario');");
                    //Limpiar las cajas
                    PrimeFaces.current().executeScript(";limpiarComponenteCaja();");
                } else {
                    PrimeFaces.current().executeScript(";alertaGeneral('Seleccione el puesto.');");
                }
            } else {
                PrimeFaces.current().executeScript(";alertaGeneral('" + FacesUtils.getKeyResourceBundle("sia.campo.usuario.existe") + "');");
            }
//
        } else if (usuarioListModel.getAccion() == 1) { //Modificar
            usuarioListModel.setRhPuestoVo(usuarioListModel.buscarPuestoPorNombre());
//                    if (usuarioListModel.getRhPuestoVo(   ) != null) {

            usuarioListModel.modificarUsuarioCampo();
            UtilLog4j.log.info(this, "Modificó");
            limpiarVariables();
        }
    }

    private void limpiarVariables() {
        //Limpia variables
        usuarioListModel.setCampoUsuarioPuestoVo(null);
        setRhPuestoVo(null);
        setUsuarioVOAlta(null);
        setC("");
        usuarioListModel.setU("");
        usuarioListModel.setIdGerencia(-1);
    }

    public void cancelarUsuarioCampo() {
        usuarioListModel.setCampoUsuarioPuestoVo(null);
        setRhPuestoVo(null);
        setU("");
        limpiarVariables();
    }

    public void limpiarListaUser() {
        usuarioListModel.setListaUsuario(null);
    }
    //Aprobar oC/S

    public void agregarAprobadorOrdenCompra() {
        usuarioListModel.setUsuarioVOAlta(new UsuarioVO());
        usuarioListModel.setApruebaOCPop(true);
        traerUsuarioJson();
    }

    public DataModel getTraerAprobadorOrdenCompra() {
        try {
            if (usuarioListModel.getUsuarioVO().getIdCampo() > 0) {
                //setListaUser(usuarioListModel.traerUsuarioAprobanOrdenCompra(getUsuarioVO().getIdCampo()));
                usuarioListModel.setListaUsuario(usuarioListModel.traerUsuarioAprobanOrdenCompra(getIdCampo()));
                return usuarioListModel.getListaUsuario();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void eliminarUsuarioApruebaOC() {
        usuarioListModel.setUsuarioVOAlta((UsuarioVO) getListaUsuario().getRowData());
        usuarioListModel.eliminarUsuarioApruebaOC(getUsuarioVO().getId(), usuarioListModel.getUsuarioVOAlta().getId(), getIdCampo());
        usuarioListModel.setUsuarioVOAlta(null);
    }

    public void guardarUsuarioApruebaOC() {
        if (!getU().trim().isEmpty()) {
            usuarioListModel.aprobarOrdenCompra(getU(), getIdCampo());
            usuarioListModel.setApruebaOCPop(false);
            usuarioListModel.setUsuarioVOAlta(null);
            setU("");
        } else {
            FacesUtils.addErrorMessage("frmPopApruebaOC", FacesUtils.getKeyResourceBundle("sia.usuario.necesario"));
        }
    }

    public void cancelarUsuarioApruebaOC() {
        usuarioListModel.setApruebaOCPop(false);
        usuarioListModel.setUsuarioVOAlta(null);
        setU("");
    }

    //Genera solicitud de estancia
    public String solicitarEstancia() {

        usuarioListModel.solicitarEstancia();
        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setFechaSalida(null);
        usuarioListModel.setSolicitaEstancia(false);
        clearComponent("frmUser", "userSelect");
        FacesUtils.addErrorMessage("frmUser", FacesUtils.getKeyResourceBundle("sia.solicitud.enviada"));
        return "";
    }

    public void cancelarSolicitarEstancia() {
        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setFechaSalida(null);
        usuarioListModel.setSolicitaEstancia(false);

    }

    public void validaFechaFin(FacesContext context, UIComponent validate, Object value) {
        Date f = (Date) value;
        if (f != null) {
            if (f != null) {
                if (usuarioListModel.validarFechaInicioVsFechaFin(f)) {
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
            if (usuarioListModel.validarFechaInicioMenorAHoy(f)) {
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

    public void abrirPopup() {
        UsuarioVO us = (UsuarioVO) usuarioListModel.getListaUsuario().getRowData();
        Usuario u = usuarioListModel.buscarPorId(us.getId());
        if (u != null) {
            UtilLog4j.log.info(this, "8888888888888888888888usuario " + u.getNombre());
            usuarioListModel.llenarUsuarioVOAlta(u);
        } else {
            FacesUtils.addErrorMessage("El usuario no existe, por favor contacta al equipo del sia para atender esta sictuación al correo soportesia@ihsa.mx ");
        }
        usuarioListModel.setFlag(true);
    }

    public void cerrarPopup() {
        usuarioListModel.setFlag(false);
        setUsuarioVOAlta(null);
        usuarioListModel.traerUsuariosSinCorreo();
    }

    //-*************** iniciar baja de empleado
    //traer la lista de gerencias de la tabla usuario_gerencia, seleccionar para enviar notificacion que deben liberar al usuario
    public void traerListaGerenciasParaLiberar() {
        usuarioListModel.traerListaRhTipoGerencia();
    }

    private void limpiarIniciarBajaUsuario() {
        setIdCampo(1);
        usuarioListModel.setListaGerenciasSeleccionadas(null);
    }

    public String buscarUsuarioBaja() {
//        Usuario us = this.usuarioListModel.buscarPorNombre(getU());
        if (getU().equals("")) {
//            FacesUtils.addInfoMessage("Por favor escribar el nombre del usuario que desea buscar  ");
            return "";
        } else {
            usuarioListModel.llenarUsuarioVOAlta(usuarioListModel.buscarPorId(usuarioListModel.getU()));
            if (usuarioListModel.getUsuarioVOAlta() != null) {
                if (!usuarioListModel.verificarProcesoBaja()) {
                    usuarioListModel.setIdGerencia(usuarioListModel.getUsuarioVOAlta().getIdGerencia());
                    usuarioListModel.setIdCampo(usuarioListModel.getUsuarioVOAlta().getIdCampo());
                    limpiarIniciarBajaUsuario();

                    usuarioListModel.llenarListaApCamposItems();
                    usuarioListModel.setIdCampo(1);
                    usuarioListModel.llenarComboGerenciasPorCampo();

                    //usuarioListModel.llenarComboGerenciasPorCampo();
                    usuarioListModel.agregarGerenciasDefaultParaInicioBaja();
                    usuarioListModel.setIdGerencia(-1);
                } else {
                    FacesUtils.addErrorMessage("El usuario " + usuarioListModel.getUsuarioVOAlta().getNombre() + " ya se encuentra en proceso de baja");
                    usuarioListModel.setUsuarioVOAlta(null);
                    setU("");
                    //clearComponent("formInicioBaja", "userSelect");
                }
                return "";
            } else {
                FacesUtils.addInfoMessage("No, se encontro el usuario: " + usuarioListModel.getU());
                return "";
            }
        }
    }

    public void confirmarBaja() {
        if (usuarioListModel.getUsuarioVOAlta() != null) {
            //if (existenFilasSeleccionada()) {
            if (usuarioListModel.getListaGerenciasSeleccionadas().getRowCount() > 0) {
                if (!usuarioListModel.getRespuesta().equals("")) {
                    //iniciar baja
                    usuarioListModel.setFlag(true);
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
        usuarioListModel.setListaGerencias(null);
        usuarioListModel.setRespuesta("");
        usuarioListModel.getFilaSeleccionada().clear();
        usuarioListModel.setUsuarioVOAlta(null);
        clearComponent("formInicioBaja", "userSelect");
        return "principalRecursosHumanos";
    }

    private boolean existenFilasSeleccionada() {
        List<RhTipoGerenciaVo> l = usuarioListModel.getDataModelAsList(usuarioListModel.getListaGerencias());
        for (RhTipoGerenciaVo vo : l) {
            if (usuarioListModel.getFilaSeleccionada().get(vo.getId()).booleanValue()) {
                return true;
            }
        }
        return false;
    }

    public void iniciarBaja() {
        if (usuarioListModel.getUsuarioVOAlta() != null) {
            //if (existenFilasSeleccionada()) {
            if (!usuarioListModel.getRespuesta().equals("")) {
                //iniciar baja
                if (usuarioListModel.iniciarBaja()) {
                    usuarioListModel.setFlag(false);
                    FacesUtils.addInfoMessage("Se ha iniciado la baja del usuario ");
                    usuarioListModel.setListaGerenciasSeleccionadas(null);
                    usuarioListModel.setRespuesta("");
                    //usuarioListModel.getFilaSeleccionada().clear();
                    usuarioListModel.setUsuarioVOAlta(null);
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

    public void cambiarValorCampoNuevoIngreso(ValueChangeEvent valueChangeEvent) {
        try {
            if (valueChangeEvent.getNewValue() != null) {
                usuarioListModel.getUsuarioVOAlta().setIdCampo((Integer) valueChangeEvent.getNewValue());
                UtilLog4j.log.info(this, "campo seleccionado : " + usuarioListModel.getIdCampo());
                //llenar nueva lista de gerencias con el campo seleccioando
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al cambiar de valor al campo " + e.getMessage());
        }
    }

    public void cambiarValorCampo(ValueChangeEvent valueChangeEvent) {
        try {
            if (valueChangeEvent.getNewValue() != null) {
                usuarioListModel.setIdCampo((Integer) valueChangeEvent.getNewValue());
                UtilLog4j.log.info(this, "campo seleccionado : " + usuarioListModel.getIdCampo());
                //llenar nueva lista de gerencias con el campo seleccioando
                usuarioListModel.llenarComboGerenciasPorCampo();
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al cambiar de valor al campo " + e.getMessage());
        }
    }

    public void cerrarConfirmacionBajaEmpleado() {
        usuarioListModel.setFlag(false);
    }

    //COMENTAR
    public void abrirPopupComentarBaja() {
        UsuarioGerenciaVo vo = (UsuarioGerenciaVo) usuarioListModel.getListaUsuarioFree().getRowData();
        usuarioListModel.setNombre("");
        usuarioListModel.setUsuarioGerenciaVo(vo);
        usuarioListModel.setFlag(true);
    }

    public void cerrarPopupComentarBaja() {
        usuarioListModel.setFlag(false);
    }

    public void comentarBaja() {
        if (usuarioListModel.getNombre() == null || usuarioListModel.getNombre().trim().isEmpty()) {
            FacesUtils.addErrorMessage("Por favor escriba el comentario");
//            FacesUtils.addErrorMessage("formPopupRecomendarViaje:inptxtRecomendacion",
            //             FacesUtils.getKeyResourceBundle("sgl.seguridad.recomendacion") + FacesUtils.getKeyResourceBundle("sistema.mensaje.error.esRequerido"));
        } else {
            usuarioListModel.crearComentario();
            usuarioListModel.setFlag(false);
            //ir a la nocia...
        }
    }

    public void agregarGerenciaAListaGerenciasSelccionadas() {
        try {
            if (usuarioListModel.getIdGerencia() != -1) {
                if (!usuarioListModel.buscarGerenciaRepetidaEnGerenciasSeleccionadas()) {
                    if (usuarioListModel.agregarGerenciaAListaSeleccionadas()) {
                        usuarioListModel.setIdGerencia(-1);
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

    public void quitarGerenciaDeListaGerenciasSelccionadas() {
        try {
            GerenciaVo vo = (GerenciaVo) usuarioListModel.getListaGerenciasSeleccionadas().getRowData();
            if (vo != null) {
                if (usuarioListModel.quitarGerenciaAListaSeleccionadas(vo)) {
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
            UtilLog4j.log.info(this, "idCampoAgregar " + usuarioListModel.getIdPuesto());
            if (usuarioListModel.getIdGerencia() != -1) {
                if (!usuarioListModel.vefiricarExistenciaGerenciaEnApCampo()) {
                    if (usuarioListModel.agregarGerenciaACampo()) {
                        cerrarPopupAgregarGerencia(event);
                        //refrescar
                        usuarioListModel.llenarComboGerenciasPorCampo();
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

    public void abrirPopupAgregarGerencia() {
        setIdGerencia(-1);
        setApruebaOCPop(true);
    }
    //cerrar el popup para agregar la gerencia a la lista de gerencias que liberan a los usuarios

    public void cerrarPopupAgregarGerencia() {
        setApruebaOCPop(false);
    }

    //
    public int getAgregarNuevoPass() {
        return usuarioListModel.getAgregarNuevoPass();
    }

    public void setAgregarNuevoPass(int agregarNuevoPass) {
        usuarioListModel.setAgregarNuevoPass(agregarNuevoPass);
    }

    public String getCambiarPass() {
        return usuarioListModel.getCambiarPass();
    }

    public void setCambiarPass(String cambiarPass) {
        usuarioListModel.setCambiarPass(cambiarPass);
    }

    public void setUsuarioListModel(UsuarioListModel usuarioListModel) {
        this.usuarioListModel = usuarioListModel;
    }

    public void llenarDatosUsuario() {
        usuarioListModel.setUsuarioVOAlta(usuarioListModel.findById(getU()));
        //usuarioListModel.llenarUsuarioVOAlta(u);
        usuarioListModel.setIdPuesto(usuarioListModel.getUsuarioVOAlta().getIdPuesto());
        usuarioListModel.setU("");
    }

    public void modificarDatos() {
        if (usuarioListModel.modificarUsuarioRH()) {
            usuarioListModel.setUsuarioVOAlta(null);
            usuarioListModel.setIdGerencia(-1);
            usuarioListModel.setIdCampo(-1);
            usuarioListModel.setIdPuesto(-1);
            usuarioListModel.setU("");
            FacesUtils.addErrorMessage("Se modificaron los datos el usuario");
        } else {
            FacesUtils.addErrorMessage("Ocurrio una excepción");
        }
    }

    public void cancelarModificacion() {
        usuarioListModel.setUsuarioVOAlta(null);
        usuarioListModel.setIdGerencia(-1);
        usuarioListModel.setIdCampo(-1);
        usuarioListModel.setIdPuesto(-1);
    }

    /**
     * @return the idGerencia
     */
    public int getIdGerencia() {
        return usuarioListModel.getIdGerencia();
    }

    /**
     * @param idGerencia the idGerencia to set
     */
    public void setIdGerencia(int idGerencia) {
        usuarioListModel.setIdGerencia(idGerencia);
    }

    /**
     * @return the usuarioVO
     */
    public UsuarioVO getUsuarioVO() {
        return usuarioListModel.getUsuarioVO();
    }

    /**
     * @param usuarioVO the usuarioVO to set
     */
    public void setUsuarioVO(UsuarioVO usuarioVO) {
        usuarioListModel.setUsuarioVO(usuarioVO);
    }

    /**
     * @return the rhPuestoVo
     */
    public RhPuestoVo getRhPuestoVo() {
        return usuarioListModel.getRhPuestoVo();
    }

    /**
     * @param rhPuestoVo the rhPuestoVo to set
     */
    public void setRhPuestoVo(RhPuestoVo rhPuestoVo) {
        usuarioListModel.setRhPuestoVo(rhPuestoVo);
    }

    /**
     * @return the usuarioVoAlta
     */
    public UsuarioVO getUsuarioVOAlta() {
        return usuarioListModel.getUsuarioVOAlta();
    }

    /**
     * @param usuarioVoAlta the usuarioVoAlta to set
     */
    public void setUsuarioVOAlta(UsuarioVO usuarioVOAlta) {
        usuarioListModel.setUsuarioVOAlta(usuarioVOAlta);
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return usuarioListModel.getIdCampo();
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        usuarioListModel.setIdCampo(idCampo);
    }

    /**
     * @return the apruebaOCPop
     */
    public boolean isApruebaOCPop() {
        return usuarioListModel.isApruebaOCPop();
    }

    /**
     * @param apruebaOCPop the apruebaOCPop to set
     */
    public void setApruebaOCPop(boolean apruebaOCPop) {
        usuarioListModel.setApruebaOCPop(apruebaOCPop);
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return usuarioListModel.getLista();
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        usuarioListModel.setLista(lista);
    }

    /**
     * @return the lista
     */
    public DataModel getListaOpciones() {
        return usuarioListModel.getListaOpciones();
    }

    /**
     * @param lista the lista to set
     */
    public void setListaOpciones(DataModel lista) {
        usuarioListModel.setListaOpciones(lista);
    }

    /**
     * @return the apCampoGerenciaVo
     */
    public ApCampoGerenciaVo getApCampoGerenciaVo() {
        return usuarioListModel.getApCampoGerenciaVo();
    }

    /**
     * @param apCampoGerenciaVo the apCampoGerenciaVo to set
     */
    public void setApCampoGerenciaVo(ApCampoGerenciaVo apCampoGerenciaVo) {
        usuarioListModel.setApCampoGerenciaVo(apCampoGerenciaVo);
    }

    /**
     * @return the campoUsuarioPuestoVo
     */
    public CampoUsuarioPuestoVo getCampoUsuarioPuestoVo() {
        return usuarioListModel.getCampoUsuarioPuestoVo();
    }

    /**
     * @param campoUsuarioPuestoVo the campoUsuarioPuestoVo to set
     */
    public void setCampoUsuarioPuestoVo(CampoUsuarioPuestoVo campoUsuarioPuestoVo) {
        usuarioListModel.setCampoUsuarioPuestoVo(campoUsuarioPuestoVo);
    }

    /**
     * @return the primerApellido
     */
    public String getPrimerApellido() {
        return usuarioListModel.getPrimerApellido();
    }

    /**
     * @param primerApellido the primerApellido to set
     */
    public void setPrimerApellido(String primerApellido) {
        usuarioListModel.setPrimerApellido(primerApellido);
    }

    /**
     * @return the segundoApellido
     */
    public String getSegundoApellido() {
        return usuarioListModel.getSegundoApellido();
    }

    /**
     * @param segundoApellido the segundoApellido to set
     */
    public void setSegundoApellido(String segundoApellido) {
        usuarioListModel.setSegundoApellido(segundoApellido);
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return usuarioListModel.getNombre();
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        usuarioListModel.setNombre(nombre);
    }

    /**
     * @return the filaSeleccionada
     */
    public Map<Integer, Boolean> getFilaSeleccionada() {
        return usuarioListModel.getFilaSeleccionada();
    }

    /**
     * @param filaSeleccionada the filaSeleccionada to set
     */
    public void setFilaSeleccionada(Map<Integer, Boolean> filaSeleccionada) {
        usuarioListModel.setFilaSeleccionada(filaSeleccionada);
    }

    /**
     * @return the solicitaEstancia
     */
    public boolean isSolicitaEstancia() {
        return usuarioListModel.isSolicitaEstancia();
    }

    /**
     * @param solicitaEstancia the solicitaEstancia to set
     */
    public void setSolicitaEstancia(boolean solicitaEstancia) {
        usuarioListModel.setSolicitaEstancia(solicitaEstancia);
    }

    /**
     * @return the fechaSalida
     */
    public Date getFechaSalida() {
        return usuarioListModel.getFechaSalida();
    }

    /**
     * @param fechaSalida the fechaSalida to set
     */
    public void setFechaSalida(Date fechaSalida) {
        usuarioListModel.setFechaSalida(fechaSalida);
    }

    /**
     * @return the respuesta
     */
    public String getRespuesta() {
        return usuarioListModel.getRespuesta();
    }

    /**
     * @param respuesta the respuesta to set
     */
    public void setRespuesta(String respuesta) {
        usuarioListModel.setRespuesta(respuesta);
    }

    public List<SelectItem> getListaPuestos() {
        return usuarioListModel.getListaPuestos();
    }

    public void setListaPuestos(List<SelectItem> listaPuestos) {
        usuarioListModel.setListaPuestos(listaPuestos);
    }

    /**
     * @return the idSgOficina
     */
    public int getIdSgOficina() {
        return usuarioListModel.getIdSgOficina();
    }

    /**
     * @param idSgOficina the idSgOficina to set
     */
    public void setIdSgOficina(int idSgOficina) {
        this.usuarioListModel.setIdSgOficina(idSgOficina);
    }

    /**
     */
    public UsuarioGerenciaVo getUsuarioGerenciaVo() {
        return usuarioListModel.getUsuarioGerenciaVo();
    }

    /**
     * @param usuarioGerenciaVo the usuarioGerenciaVo to set
     */
    public void setUsuarioGerenciaVo(UsuarioGerenciaVo usuarioGerenciaVo) {
        usuarioListModel.setUsuarioGerenciaVo(usuarioGerenciaVo);
    }

    public DataModel getListaUsuario() {
        return usuarioListModel.getListaUsuario();
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(DataModel listaUsuario) {
        this.usuarioListModel.setListaUsuario(listaUsuario);
    }

    public DataModel getListaGerencias() {
        return usuarioListModel.getListaGerencias();
    }

    /**
     * @param listaGerencias the listaGerencias to set
     */
    public void setListaGerencias(DataModel listaGerencias) {
        usuarioListModel.setListaGerencias(listaGerencias);
    }

    /**
     * @return the listaMaterial
     */
    public DataModel getListaMaterial() {
        return usuarioListModel.getListaMaterial();
    }

    /**
     * @param listaMaterial the listaMaterial to set
     */
    public void setListaMaterial(DataModel listaMaterial) {
        usuarioListModel.setListaMaterial(listaMaterial);
    }

    /**
     * @return the idPuesto
     */
    public int getIdPuesto() {
        return usuarioListModel.getIdPuesto();
    }

    /**
     * @param idPuesto the idPuesto to set
     */
    public void setIdPuesto(int idPuesto) {
        usuarioListModel.setIdPuesto(idPuesto);
    }

    /**
     * @return the listaGerenciasItems
     */
    public List<SelectItem> getListaGerenciasItems() {
        return usuarioListModel.getListaGerenciasItems();
    }

    /**
     * @param listaGerenciasItems the listaGerenciasItems to set
     */
    public void setListaGerenciasItems(List<SelectItem> listaGerenciasItems) {
        usuarioListModel.setListaGerenciasItems(listaGerenciasItems);
    }

    /**
     * @return the listaCamposItems
     */
    public List<SelectItem> getListaCamposItems() {
        return usuarioListModel.getListaCamposItems();
    }

    public List<SelectItem> getListaCamposItemsSinCampo1() {
        List<SelectItem> l = usuarioListModel.getListaCamposItems();

        for (SelectItem s : l) {
            l.remove(0);
        }
        return usuarioListModel.getListaCamposItems();
    }

    /**
     * @param listaCamposItems the listaCamposItems to set
     */
    public void setListaCamposItems(List<SelectItem> listaCamposItems) {
        usuarioListModel.setListaCamposItems(listaCamposItems);
    }

    /**
     * @return the listaGerenciasSeleccionadas
     */
    public DataModel getListaGerenciasSeleccionadas() {
        return usuarioListModel.getListaGerenciasSeleccionadas();
    }

    /**
     * @param listaGerenciasSeleccionadas the listaGerenciasSeleccionadas to set
     */
    public void setListaGerenciasSeleccionadas(DataModel listaGerenciasSeleccionadas) {
        usuarioListModel.setListaGerenciasSeleccionadas(listaGerenciasSeleccionadas);
    }

//    /**
//     * @return the rolesUsuario
//     */
//    public boolean isRolesUsuario() {
//        return rolesUsuario;
//    }
//
//    /**
//     * @param rolesUsuario the rolesUsuario to set
//     */
//    public void setRolesUsuario(boolean rolesUsuario) {
//        this.rolesUsuario = rolesUsuario;
//    }
    public int getPreguntaEntero() {
        return usuarioListModel.getPreguntaEntero();
    }

    public void setPreguntaEntero(int preguntaEntero) {
        usuarioListModel.setPreguntaEntero(preguntaEntero);
    }

    /**
     * @return the u
     */
    public String getU() {
        return usuarioListModel.getU();
    }

    /**
     * @param u the u to set
     */
    public void setU(String u) {
        usuarioListModel.setU(u);
    }

    /**
     * @return the seleccionNuevoIngreso
     */
    public int getSeleccionNuevoIngreso() {
        return usuarioListModel.getSeleccionNuevoIngreso();
    }

    /**
     * @param seleccionNuevoIngreso the seleccionNuevoIngreso to set
     */
    public void setSeleccionNuevoIngreso(int seleccionNuevoIngreso) {
        usuarioListModel.setSeleccionNuevoIngreso(seleccionNuevoIngreso);
    }

    /**
     * @return the parametroTipoUsuario
     */
    public int getParametroTipoUsuario() {
        return usuarioListModel.getParametroTipoUsuario();
    }

    /**
     * @param parametroTipoUsuario the parametroTipoUsuario to set
     */
    public void setParametroTipoUsuario(int parametroTipoUsuario) {
        usuarioListModel.setParametroTipoUsuario(parametroTipoUsuario);
    }

    /**
     * @return the c
     */
    public String getC() {
        return usuarioListModel.getC();
    }

    /**
     * @param c the c to set
     */
    public void setC(String c) {
        usuarioListModel.setC(c);
    }

    /**
     * @return the confirmarPassword
     */
    public String getConfirmarPassword() {
        return usuarioListModel.getConfirmarPassword();
    }

    /**
     * @param confirmarPassword the confirmarPassword to set
     */
    public void setConfirmarPassword(String confirmarPassword) {
        usuarioListModel.setConfirmarPassword(confirmarPassword);
    }

    /**
     * @return the nuevaClave
     */
    public String getNuevaClave() {
        return usuarioListModel.getNuevaClave();
    }

    /**
     * @param nuevaClave the nuevaClave to set
     */
    public void setNuevaClave(String nuevaClave) {
        usuarioListModel.setNuevaClave(nuevaClave);
    }

    /**
     * @return the claveActual
     */
    public String getClaveActual() {
        return usuarioListModel.getClaveActual();
    }

    /**
     * @param claveActual the claveActual to set
     */
    public void setClaveActual(String claveActual) {
        usuarioListModel.setClaveActual(claveActual);
    }

    /**
     * @return the accion
     */
    public int getAccion() {
        return usuarioListModel.getAccion();
    }

    /**
     * @param accion the accion to set
     */
    public void setAccion(int accion) {
        usuarioListModel.setAccion(accion);
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return usuarioListModel.isFlag();
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        usuarioListModel.setFlag(flag);
    }

    public DataModel getListaUsuarioFree() {
        return usuarioListModel.getListaUsuarioFree();
    }
    
    /**
     * @return the mapaRoles
     */
    public Map<String, Boolean> getMapaRoles() {
        return usuarioListModel.getMapaRoles();
    }

    /**
     * @param mapaRoles the mapaRoles to set
     */
    public void setMapaRoles(Map<String, Boolean> mapaRoles) {
        usuarioListModel.setMapaRoles(mapaRoles);
    }
}
