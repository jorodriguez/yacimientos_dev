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
import lector.dominio.modelo.usuario.vo.UsuarioVO;
import lector.dominio.vo.UsuarioRolVo;
import lector.modelo.SiUsuarioRol;
import lector.modelo.Usuario;
import lector.modelo.usuario.vo.UsuarioGerenciaDto;
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
    private UsuarioGerenciaDto usuarioGerenciaVo;
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
        listaMenu.addAll(
                taerListaMenuUsuarioSesion()
        );

    }

    public List<MenuSiOpcionVo> taerListaMenuUsuarioSesion() {
        listaMenu = new ArrayList<>();

        try {
            listaMenu.addAll(makeItems(siOpcionImpl.getListaMenu(sesion.getUsuarioSesion())));
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
            for (UsuarioRolVo rol : traerRolesPorUsuario()) {
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

    public List<UsuarioRolVo> traerRolesPorUsuario() {
        List<UsuarioRolVo> ur = null;
        try {
            //ur = SiUsuarioRol.traerRolPorUsuarioModulo(idUsuario, modulo, idCamp);

        } catch (Exception e) {
            LOGGER.warn(this, e);
        }
        return ur;
    }


    public void buscarId() {
        String[] cad = sesion.getUsuarioSesion().getEmail().split("@");
        Usuario us = buscarPorId(cad[0].toUpperCase());
        if (us != null) {
            FacesUtils.addInfoMessage("Ya existe un usuario con el ID " + cad[0]);
        }
    }


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

  /*  public boolean verificarModificacionMail() {
        return servicioUsuario.find(usuarioVOAlta.getId())
                .getEmail().equals(usuarioVOAlta.getMail());
    }
*/

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


            getUsuarioVOAlta().setDestinatarios(u.getDestinatarios());
            getUsuarioVOAlta().setRfc(u.getRfc());
            getUsuarioVOAlta().setTelefono(u.getTelefono());
            getUsuarioVOAlta().setExtension(u.getExtension());
            getUsuarioVOAlta().setCelular(u.getCelular());
            getUsuarioVOAlta().setSexo(u.getSexo());
            //
            getUsuarioVOAlta().setCampo(u.getCampo());
            getUsuarioVOAlta().setActivo(u.isActivo());
            getUsuarioVOAlta().setPregunta(u.getPregunta());
            getUsuarioVOAlta().setRespuesta(u.getRespuesta());
            //Otros 5
            getUsuarioVOAlta().setFechaNacimiento(u.getFechaNacimiento());


        }
    }



    public String agregarUsuario() {
        setUsuarioVOAlta(new UsuarioVO());
        return "/vistas/recursos/altaNuevoIngresoRH.xhtml?faces-redirect=true";
    }


    public void eiminarUsuario() {
        this.eiminarUsuario();
        this.setUsuarioVOAlta(null);
        setU("");
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
        v = servicioUsuario.reinicioClave(sesion.getUsuarioSesion().getId(), usuarioVOAlta.getId());
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
