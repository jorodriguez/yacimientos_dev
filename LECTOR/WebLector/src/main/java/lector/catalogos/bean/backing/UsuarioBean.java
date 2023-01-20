/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.catalogos.bean.backing;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import lector.constantes.Constantes;
import lector.dominio.vo.UsuarioRolVo;
import lector.modelo.SiUsuarioRol;
import lector.modelo.Usuario;
import lector.modelo.usuario.vo.UsuarioGerenciaVo;
import lector.modelo.usuario.vo.UsuarioVO;
import lector.servicios.catalogos.impl.UsuarioImpl;
import lector.servicios.sistema.impl.SiOpcionImpl;
import lector.servicios.sistema.vo.MenuSiOpcionVo;
import lector.servicios.sistema.vo.SiOpcionVo;
import lombok.Getter;
import lombok.Setter;
import lector.sistema.bean.backing.Sesion;
import lector.sistema.bean.support.FacesUtils;
import lector.util.UtilLog4j;

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
       
    //
    @Inject
    SiOpcionImpl siOpcionImpl;
    @Inject
    UsuarioImpl servicioUsuario;    
     
       
    @Getter
    @Setter
    private List<MenuSiOpcionVo> listaMenu;
        
    
    
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
    
    //
    @Getter
    @Setter
    private DataModel listaUsuario;
    @Getter
    @Setter
    private DataModel listaOpciones;
    @Getter
    @Setter
    private Map<String, Boolean> mapaRoles = new HashMap<>();
   
      
    @Getter
    @Setter
    private String u;
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
        llenarMenu();               
    }

    

    public void llenarMenu() {
        listaMenu.addAll(taerListaMenu(1, sesion.getUsuarioVo().getId(), sesion.getUsuarioVo().getIdCampo()));

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
                s.addAll(taerOpcionesByRol(1, rol.getIdRol()));
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
            //ur = SiUsuarioRol.traerRolPorUsuarioModulo(idUsuario, modulo, idCamp);

        } catch (Exception e) {
            LOGGER.warn(this, e);
        }
        return ur;
    }


    public void buscarId() {
        String[] cad = sesion.getUsuarioVo().getMail().split("@");
        Usuario us = buscarPorId(cad[0].toUpperCase());
        if (us != null) {
            FacesUtils.addInfoMessage("Ya existe un usuario con el ID " + cad[0]);
        }
    }

/*
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
    }*/

    public String encriptar(String text) throws NoSuchAlgorithmException {
        //LOGGER.info(this, "Text: ");
        return servicioUsuario.encriptar(text);
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

    public boolean verificarModificacionMail() {
        return servicioUsuario.find(usuarioVOAlta.getId())
                .getEmail().equals(usuarioVOAlta.getMail());
    }


    /*
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
        if (!this.validaMail(getUsuarioVOAlta().getDestinatarios()) || this.validateTextHastNotPunctuation(getUsuarioVOAlta().getDestinatarios())) {
            FacesUtils.addInfoMessage("frmUser:error", "Destinatarios no válido");
            FacesUtils.addInfoMessage("frmUser:error", FacesUtils.getKeyResourceBundle("sia.Usuario.email.msg.noValido.caracteresEspeciale"));
            errors++;
        }

        if (errors == 0) {
            getUsuarioVOAlta().setId(posibleId);
            if (this.traerIdUsuario(getUsuarioVOAlta().getId().toUpperCase()) == null) {
                getUsuarioVOAlta().setClave(encriptar(getUsuarioVOAlta().getClave()));
                v = servicioUsuario.guardarNuevoUsuario(sesion.getUsuarioVo().getId(), getUsuarioVOAlta(), getIdGerencia());
                if (v) {
//                    this.soporteProveedor.setUsuario(null);
//                    soporteProveedor.setPuestoVo(null);
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
    }*/

    public String cancelarNuevoIngreso() {
        setU("");
        setUsuarioVOAlta(new UsuarioVO());
        return "/vistas/administracion/principalAdministrar.xhtml?faces-redirect=true";
    }


    public void cancelNuevoIngreso() {
        setU("");
        setUsuarioVOAlta(new UsuarioVO());
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

            getUsuarioVOAlta().setMail(u.getMail());


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
                getUsuarioVOAlta().setOficina(u.getOficina());
                getUsuarioVOAlta().setIdOficina(u.getIdOficina());

            if (u.getIdNomina() != 0) {
                getUsuarioVOAlta().setIdNomina(u.getIdNomina());
            }

        }
    }



    public String agregarUsuario() {
        setUsuarioVOAlta(new UsuarioVO());
        getUsuarioVOAlta().setGafete("si");
        return "/vistas/recursos/altaNuevoIngresoRH.xhtml?faces-redirect=true";
    }


    public void eiminarUsuario() {
        this.eiminarUsuario();
        this.setUsuarioVOAlta(null);
        setU("");
//        this.mostrarTitulo = true;
    }



    public void buscarUsuarioConsulta() {
        UsuarioVO usuaroiSel = buscarPorNombre(getU());
        if (usuaroiSel != null) {
            llenarUsuarioVOAlta(usuaroiSel);

            UtilLog4j.log.info(this, "Usr:{0}", new Object[]{getUsuarioVOAlta().getNombre()});
        } else {
            UtilLog4j.log.info(this, "No se encontro el usuario");
        }
    }

    public void limpiarVar() {
        this.limpiar();
        setParametroTipoUsuario(1);
    }

    public void limpiar() {
        setU("");
    }

    public String regresarLogin() {
        return "/principal";
    }



    public boolean cambioContrasenia(String c, String confirmarPasswor) throws NoSuchAlgorithmException {
        return servicioUsuario.cambioContrasenia(getUsuarioVOAlta().getId(), encriptar(c), confirmarPasswor);
    }

    public String cancelarModificaUsuario() {
        return "/principal";
    }

    public void modificaUsuarioDatosSinClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular) {
        servicioUsuario.modificaUsuarioDatosSinClave(idUser, nombre, correo, destinatarios, rfc, telefono, ext, celular);
    }

    public boolean modificaUsuarioDatosConClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular, String nuevaClave) throws NoSuchAlgorithmException {
        return servicioUsuario.modificaUsuarioDatosConClave(idUser, nombre, correo, destinatarios, rfc, telefono, ext, celular, encriptar(nuevaClave));
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



    public void limpiarListaUser() {
        setListaUsuario(null);
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


}
