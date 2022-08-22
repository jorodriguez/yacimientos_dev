/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.model;

import com.google.common.base.Strings;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import sia.constantes.Constantes;
import sia.excepciones.EmailNotFoundException;
import sia.modelo.RhPuesto;
import sia.modelo.SgEmpresa;
import sia.modelo.SiRelRolOpcion;
import sia.modelo.SiRol;
import sia.modelo.SiUsuarioRol;
import sia.modelo.Usuario;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.campo.vo.CampoVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.gerencia.vo.RhTipoGerenciaVo;
import sia.modelo.puesto.vo.RhPuestoVo;
import sia.modelo.rol.vo.RolVO;
import sia.modelo.rol.vo.SiRelRolOpcionVO;
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
import sia.servicios.sistema.vo.SiModuloVo;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.servicios.usuario.impl.RhTipoGerenciaImpl;
import sia.servicios.usuario.impl.RhUsuarioGerenciaImpl;
import sia.sistema.bean.support.SoporteListas;
import sia.sistema.bean.support.SoporteProveedor;
import sia.util.UtilLog4j;

/**
 *
 * @author hacosta
 */
/*
 * @Named(value = "usuarioListModel")
 *
 */
//
//@CustomScoped(value = "#{window}")
public class UsuarioListModel implements Serializable {
//
//    private static final UtilLog4j LOGGER = UtilLog4j.log;
//
//    private static final int OFICINA_MONTERREY = 1;
//    private static final int SUBDIRECCION_ADMINISTRATIVA = 48;
//    private static final int STAFF_HOUSE = 8;
//    private static final int CONFIGURACION_CORREO = 15;
//
//    private static final int FINANZAS = 54;
//    private static final int SERVICIOS_INFORMATICOS = 61;
//    private static final int SERVICIOS_GENERALES = 33;
//    private static final int HSE = 47;
//
//    //@ManagedProperty(value = "#{soporteProveedor}")
//    private SoporteProveedor soporteProveedor;
//    //@ManagedProperty(value = "#{soporteListas}")
//    private SoporteListas soporteListas;
//    //
//    @Inject
//    SiOpcionImpl siOpcionImpl;
//    @Inject
//    UsuarioImpl servicioUsuario;
//    @Inject
//    CadenasMandoImpl cadenasMandoImpl;
//    @Inject
//    GerenciaImpl gerenciaImpl;
//    @Inject
//    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
//    @Inject
//    private RhPuestoImpl rhPuestoImpl;
//    @Inject
//    ApCampoImpl apCampoImpl;
//    @Inject
//    ApCampoGerenciaImpl apCampoGerenciaImpl;
//    @Inject
//    SgOficinaImpl sgOficinaImpl;
//    @Inject
//    SgEmpresaImpl sgEmpresaImpl;
//    //@Inject
//    //RhEmpleadoMaterialImpl rhEmpleadoMaterialImpl;
//    @Inject
//    SiManejoFechaImpl siManejoFechaLocal;
//    @Inject
//    SgSolicitudEstanciaImpl sgSolicitudEstanciaImpl;
//    @Inject
//    RhUsuarioGerenciaImpl rhUsuarioGerenciaImpl;
//    //@Inject
//    //SiUsuarioTipoImpl siUsuarioTipoImpl;
//    @Inject
//    RhTipoGerenciaImpl rhTipoGerenciaImpl;
//    @Inject
//    RhCampoGerenciaImpl rhCampoGerenciaImpl;
//    @Inject
//    CoNoticiaImpl coNoticiaImpl;
//    @Inject
//    SiRolImpl siRolImpl;
//    @Inject
//    SiRelRolOpcionImpl siRolOpcion;
//    @Inject
//    SiUsuarioRolImpl siUsuarioRol;
//    @Inject
//    SiModuloImpl siModuloImpl;
//    //Primitivos
//    private boolean apruebaOCPop = false; //tambien se usa para abrir el popup para agregar gerencias a la lista de Gerencias que liberan al empleado en la opcion Inicio de baja
//    private boolean flag;
//    private ApCampoGerenciaVo apCampoGerenciaVo;
//    private UsuarioVO usuarioVOAlta; //para trabajar con usuario
//    private UsuarioVO usuarioVO; //para la sesion
//    private UsuarioGerenciaVo usuarioGerenciaVo;
//    private DataModel lista;
//    private CampoUsuarioPuestoVo campoUsuarioPuestoVo;
//    private RhPuestoVo rhPuestoVo;
//    //
//    private DataModel listaUsuario;
//    //
//    private DataModel listaUsuarioFree;
//    private DataModel listaGerencias;
//    private DataModel listaGerenciasSeleccionadas;
//    private DataModel listaMaterial;
//    private DataModel listaOpciones;
//    private int idGerencia;
//    private int idCampo;
//    private int idSgOficina; //<< Ocupado en la opcion de pedir material, cuando se pide staff house se pide que seleccione la oficina de estancia
//    private String nombre;
//    private String primerApellido;
//    private String segundoApellido;
//    private Map<Integer, Boolean> filaSeleccionada = new HashMap<>();
//    private List listaFilasSeleccionadas;
//    private List<GerenciaVo> listaGeneral;
//    private boolean solicitaEstancia;
//    private Date fechaSalida;
//    private String respuesta; // Tambien ocupada para escribir el motivo de Baja
//    private List<SelectItem> listaPuestos;
//    private int idPuesto; //tambien usado para agregar gerencias a campos desde RH- representa el campo (idApCampo)
//    private List<SelectItem> listaGerenciasItems;
//    private List<SelectItem> listaCamposItems;
//    //
//    private int preguntaEntero = 1; // se ocupa para la consulta de compradores y solicitan requision
//    private String cambiarPass = "no";
//    private int agregarNuevoPass;
//    private String u;
//    private List<MenuSiOpcionVo> listaMenu;
//    private int seleccionNuevoIngreso = 1;
//
//    private int parametroTipoUsuario = 1;
//    private String c;
//    private String confirmarPassword;
//    private String nuevaClave;
//    private String claveActual;
//    private int accion;
//    //
//    private String usuarioJson;
//    private String puestoJson;
//    //
//    private Map<String, Boolean> mapaRoles = new HashMap<>();
//
//    /**
//     * Creates a new instance of UsuarioListModel
//     */
//    @PostConstruct
//    public void inicio() {
//        if (getUsuarioVO() != null
//                && getIdCampo() <= Constantes.CERO) {
//            setIdCampo(getUsuarioVO().getIdCampo());
//        }
//        traerUsuarioJson();
//        traerPuestoJson();
//        //         llenar roles de rh
//        llenarRolRh();
//
//    }
//
//    private void llenarRolRh() {
//        if (usuarioVO != null) {
//
//            List<UsuarioRolVo> rolUsuario = siUsuarioRol.traerRolPorUsuarioModulo(usuarioVO.getId(), Constantes.MODULO_RH_ADMIN, usuarioVO.getIdCampo());
//            for (UsuarioRolVo usuarioRolVo : rolUsuario) {
//                mapaRoles.put(usuarioRolVo.getNombreRol(), Boolean.TRUE);
//            }
//        }
//
//    }
//
//    public List<SelectItem> regresaUsuarioActivo(String cadena) {
//        return soporteListas.regresaTodosUsuarioActivo(cadena);
//    }
//
//    public List<SelectItem> regresaUsuarioActivoVO(String cadena) {
//        return soporteProveedor.regresaUsuarioActivoVO(cadena);
//    }
//
//    public List<SelectItem> regresaUsuariosPorBloque(String cadena) {
//        return soporteProveedor.regresaUsuariosPorBloque(cadena, getIdCampo());
//    }
//
//    public List<SelectItem> regresaPuesto(String cadena) {
//        return soporteProveedor.regresaPuesto(cadena);
//    }
//
//    /*
//     * Este metodo hay que moverlo a la parte de servicios ya que es una
//     * funcionalidad generica.
//     */
//    public void llenarUsuarioVO(Usuario u) {
//        //Traer puesto del usuario
//        setUsuarioVO(new UsuarioVO());
//        getUsuarioVO().setId(u.getId());
//        getUsuarioVO().setNombre(u.getNombre());
//        getUsuarioVO().setClave(u.getClave());
//        getUsuarioVO().setPuesto(traerPuestoUsusaio(u.getId(), u.getApCampo().getId()));
//        getUsuarioVO().setMail(u.getEmail());
//        getUsuarioVO().setDestinatarios(u.getDestinatarios());
//        getUsuarioVO().setRfc(u.getRfc());
//        getUsuarioVO().setTelefono(u.getTelefono());
//        getUsuarioVO().setExtension(u.getExtension());
//        getUsuarioVO().setCelular(u.getCelular());
//        getUsuarioVO().setSexo(u.getSexo());
//        //
//        getUsuarioVO().setIdCampo(u.getApCampo().getId());
//        getUsuarioVO().setCampo(u.getApCampo().getNombre());
//        //Se agrega a la sesion de SIA el RFC de la empresa a la que pertenece el empleado
//        getUsuarioVO().setRfcEmpresa(u.getApCampo().getCompania().getRfc());
//        //
//        getUsuarioVO().setIdGerencia(u.getGerencia().getId());
//        getUsuarioVO().setActivo(u.isActivo());
//        getUsuarioVO().setPregunta(u.getPreguntaSecreta());
//        getUsuarioVO().setRespuesta(u.getRespuestaPreguntaSecreta());
//        //Otros 5
//        getUsuarioVO().setFechaIngreso(u.getFechaIngreso());
//
//        if (u.getSgOficina() != null) {
//            getUsuarioVO().setOficina(u.getSgOficina().getNombre());
//            getUsuarioVO().setIdOficina(u.getSgOficina().getId());
//        }
//
//        if (u.getGerencia() == null) {
//            setIdGerencia(-1);
//        } else {
//            getUsuarioVO().setIdGerencia(u.getGerencia().getId());
//            getUsuarioVO().setGerencia(u.getGerencia().getNombre());
//        }
//
//        if (u.getSgEmpresa() != null) {
//            getUsuarioVO().setIdNomina(u.getSgEmpresa().getId());
//        }
//        getUsuarioVO().setAdministraTI(false);
//        //buscar si es un usuario que administra TI
//      
//        /*List<UsuarioTipoVo> usuarioList = getUsuariosAdministranTI();
//        if (usuarioList != null && !usuarioList.isEmpty()) {
//            for (UsuarioTipoVo vo : usuarioList) {
//                if (vo.getIdUser().equals(getUsuarioVO().getId())) {
//                    LOGGER.info(this, "ES USUARIO QUE ADMINISTRA TI");
//                    getUsuarioVO().setAdministraTI(true);
//                    break;
//                }
//            }
//        }
//        */
//        getUsuarioVO().setUsuarioInSessionGerente(usuarioResponsableForAnyGerencia());
//        this.getUsuarioVO().setLiberaUsuarios(this.isUsuarioInSessionLiberador(this.getUsuarioVO().getId()));
//
//        UsuarioRolVo uvo = traerRolPrincipal(u.getId(), u.getApCampo().getId());
//
//        if (uvo != null) {
//            getUsuarioVO().setRolPrincipal(uvo.getNombreRol());
//            getUsuarioVO().setRolId(uvo.getIdRol());
//        }
//
//        listaMenu = null;
//    }
//
//    
//
//    /**
//     * Función que elimina acentos y caracteres especiales de una cadena de
//     * texto.
//     *
//     * @param input
//     * @return cadena de texto limpia de acentos y caracteres especiales.
//     */
//    public String removeSpecialCharactersReplacingWithASCII(String input) {
//        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
//        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
//        return pattern.matcher(nfdNormalizedString).replaceAll("");
//    }
//
//    public Usuario buscarPorId(String idUsuario) {
//        return this.servicioUsuario.find(idUsuario);
//    }
//
//    public UsuarioVO findById(Object idUsuario) {
//      p  return this.servicioUsuario.findById(idUsuario);
//    }
//
//    public Usuario buscarPorNombre(Object nombreUsuario) {
//        return this.servicioUsuario.buscarPorNombre(nombreUsuario);
//    }
//
//    public List<Usuario> getUsuariosActivos() {
//        return this.servicioUsuario.getActivos();
//    }
//
//    /*
//    public List<UsuarioTipoVo> getUsuariosAdministranTI() {
//        return siUsuarioTipoImpl.getListUser(19, 1);
//    }
//     */
//
//    public boolean enviarClave(Usuario usuario) {
//        return this.servicioUsuario.enviarClave(usuario);
//    }
//
//    public boolean usuarioResponsableForAnyGerencia() {
//        return gerenciaImpl.isUsuarioResponsableForAnyGerencia(-1, getUsuarioVO().getId(), false);
//    }
//
//    public boolean isUsuarioInSessionLiberador(String idUsuario) {
//        return this.rhTipoGerenciaImpl.isLiberador(idUsuario);
//    }
//
//    public boolean verificaPuesto() {
//        setRhPuestoVo(rhPuestoImpl.findById(getRhPuestoVo().getId(), false));
//        return getRhPuestoVo() != null;
//    }
//
//    public boolean guardarUsuarioNuevoIngreso() throws NoSuchAlgorithmException {
//
//        // si contamos con el nombre de usuario de directorio, se debiera utilizar
//        // este como id del usuario        
//        if (Strings.isNullOrEmpty(getUsuarioVOAlta().getUsuarioDirectorio())) {
//            construirId();
//        } else {
//            getUsuarioVOAlta().setId(getUsuarioVOAlta().getUsuarioDirectorio());
//        }
//
//        getUsuarioVOAlta().setDestinatarios(getUsuarioVOAlta().getMail());
//        getUsuarioVOAlta().setIdPuesto(getRhPuestoVo().getId());
//        getUsuarioVOAlta().setClave(encriptar("1234"));
//        //  LOGGER.info(this, "JEFE: " + getUsuarioVOAlta().getIdJefe());
//
//        getUsuarioVOAlta().setIdJefe(gerenciaImpl.getResponsableByApCampoAndGerencia(getUsuarioVOAlta().getIdCampo(), getIdGerencia(), false).getId());
//        getUsuarioVOAlta().setIdGerencia(getIdGerencia());
//        getUsuarioVOAlta().setGerencia(gerenciaImpl.find(getIdGerencia()).getNombre());
//        getUsuarioVOAlta().setOficina(sgOficinaImpl.find(getUsuarioVOAlta().getIdOficina()).getNombre());
//        getUsuarioVOAlta().setPuesto(rhPuestoImpl.find(getUsuarioVOAlta().getIdPuesto()).getNombre());
//
//        return this.servicioUsuario.guardarUsuarioNuevoIngreso(getUsuarioVO().getId(), getUsuarioVOAlta(), getIdGerencia());
//    }
//
//    public void traerListaMateriales() {
//        LOGGER.info(this, "lsita de materiales");
//        List<EmpleadoMaterialVO> lstEmpMaterial;
//        List<EmpleadoMaterialVO> tem = new ArrayList<>();
///*
//        if (getUsuarioVOAlta().getIdOficina() == OFICINA_MONTERREY) {
//            lstEmpMaterial = rhEmpleadoMaterialImpl.getListEmpleadoMaterial();
//        } else {
//            lstEmpMaterial = rhEmpleadoMaterialImpl.getListEmpleadoMaterial();
//
//            for (EmpleadoMaterialVO empleadoMaterialVO : lstEmpMaterial) {
//                if (empleadoMaterialVO.getIdGerencia() != SUBDIRECCION_ADMINISTRATIVA) {
//                    tem.add(empleadoMaterialVO);
//                }
//            }
//            lstEmpMaterial.clear();
//            lstEmpMaterial.addAll(tem);
//        }
//
//        LOGGER.info(this, "lista recuperada : " + lstEmpMaterial.size());
//
//        setListaMaterial(new ListDataModel(lstEmpMaterial));
//*/
//        LOGGER.info(this, "lista datamodel : " + getLista());
//    }
//
//    public List<EmpleadoMaterialVO> verificaLista() {
//        setSolicitaEstancia(false);
//
//        DataModel<EmpleadoMaterialVO> lt = getListaMaterial();
//        List<EmpleadoMaterialVO> lstEmpMaterial = new ArrayList<>();
//        setListaFilasSeleccionadas(new ArrayList<>());
//
//        LOGGER.info(this, "Filas seleccionadas: " + filaSeleccionada.size());
//
//        for (EmpleadoMaterialVO sgV : lt) {
//            if (filaSeleccionada.get(sgV.getId())) {
//
//                if (sgV.getId() == STAFF_HOUSE) {
//                    setIdSgOficina(usuarioVOAlta.getIdOficina());
//                    sumarDias();
//                    setSolicitaEstancia(true);
//                }
//
//                if (sgV.getId() == CONFIGURACION_CORREO) {//requiere correo
//                    usuarioVOAlta.setRequiereConfiguracionCorreo(true);
//                }
//
//                lstEmpMaterial.add(sgV);
//                filaSeleccionada.remove(sgV.getId());
//            }
//        }
//
//        setListaFilasSeleccionadas(lstEmpMaterial);
//
//        return getListaFilasSeleccionadas();
//    }
//
//    public boolean solicitarMaterial() {
//        return servicioUsuario.enviarSolicitudMaterial(
//                getUsuarioVO().getId(),
//                getUsuarioVOAlta(),
//                listaFilasSeleccionadas,
//                getSeleccionNuevoIngreso()
//        );
//    }
//
//    public boolean guardarUsuario(String sesion, UsuarioVO usuarioVO, int idGerencia) {
//        return servicioUsuario.guardarNuevoUsuario(sesion, usuarioVO, idGerencia);
//    }
//
//    public boolean guardarDireccionCorreoReal(String idUsuario) {
//        return servicioUsuario.guardarDireccionMailNuevoIngreso(
//                usuarioVOAlta.getId(),
//                usuarioVOAlta.getMail(),
//                idUsuario
//        );
//    }
//
//    public boolean verificarModificacionMail() {
//        return servicioUsuario.find(usuarioVOAlta.getId())
//                .getEmail().equals(usuarioVOAlta.getMail());
//    }
//
//    private void construirId() {
//
//        String cad = removeSpecialCharactersReplacingWithASCII(getNombre()).trim();
//        StringBuilder id = new StringBuilder();
//        Usuario us;
//        int numLetrasNombre = 1;
//
//        for (int i = 0; i < cad.length(); i++) {
//            for (int j = 0; j < numLetrasNombre; j++) {
//                id.append(cad.charAt(i + j));
////                id += cad.charAt(i + j);
//            }
//            id.append(removeSpecialCharactersReplacingWithASCII(getPrimerApellido().trim()));
//
//            LOGGER.info(this, "ID: " + id);
//            us = servicioUsuario.findRH(id.toString().toUpperCase());
//            if (us == null) {
//                usuarioVOAlta.setId(id.toString().toUpperCase());
//                usuarioVOAlta.setNombre(
//                        getNombre().trim()
//                        + " " + getPrimerApellido().trim()
//                        + " " + getSegundoApellido().trim()
//                );
//                LOGGER.info(this, "ID: " + id);
//                break;
//            } else {
//                id = new StringBuilder();
//            }
//
//            if (i == cad.length() - 1) {
//                numLetrasNombre++;
//                i = -1;
//            }
//        }
//    }
//
//    public String traerIdUsuario(String id) {
//        String retVal = null;
//
//        try {
//            retVal = servicioUsuario.find(id).getId();
//        } catch (Exception e) {
//            LOGGER.error(e);
//        }
//
//        return retVal;
//    }
//
//    public List<UsuarioGerenciaVo> traerUsuarioProcesoBaja() {
//        List<UsuarioGerenciaVo> retVal;
//
//        if (getUsuarioVOAlta() == null) {
//            retVal = rhUsuarioGerenciaImpl.traerUsuarioNoLiberadoGerencia("", null);
//        } else {
//            retVal = rhUsuarioGerenciaImpl.traerUsuarioNoLiberadoGerencia(getUsuarioVOAlta().getId(), null);
//        }
//
//        return retVal;
//    }
//
//    public boolean finalizarBaja() {
//        //idGErencia se ocupar para usuario-gerencia
//        LOGGER.info(this, "USuario seleccionado: " + "idU: " + getRespuesta());
//        return servicioUsuario.finalizarBaja(getUsuarioVO().getId(), getRespuesta());
//    }
//
////    public boolean modificarUsuario(String id, String nombre, String puesto, String clave, String solicita, String revisa, String aprueba, String autoriza, String vistoBueno, String asigna, String compra, String email, String destinatarios, String telefono, String extension, String sexo, String celular) {
////        return this.servicioUsuario.modificarUsuario(id, nombre, puesto, clave, solicita, revisa, aprueba, autoriza, vistoBueno, asigna, compra, email, destinatarios, telefono, extension, sexo, celular);
////    }
//   
//    public boolean modificarUsuarioRH() {
//        boolean v = false;
//
//        try {
//            servicioUsuario.modificarDatosUsuario(getUsuarioVO().getId(), getUsuarioVOAlta(), getIdPuesto());
//            v = true;
//        } catch (Exception e) {
//            LOGGER.fatal(this, "Modificando usuario {0}", new Object[]{getUsuarioVO().getId()}, e);
//        }
//
//        return v;
//    }
//
//    public List<UsuarioVO> taerUsuarios() {
//        List<UsuarioVO> retVal = null;
//
//        if (getPreguntaEntero() == Constantes.UNO) {
//            retVal = servicioUsuario.traerListaRolPrincipalUsuarioRolModulo(Constantes.ROL_COMPRADOR, Constantes.MODULO_COMPRA, getIdCampo());
//        } else {
//            retVal = servicioUsuario.traerUsuariosSolicitaRequision(getIdCampo());
//        }
//
//        return retVal;
//    }
//
//    /**
//     * Creo: NLopez
//     */
//    public List<RolVO> taerRoles(String usuario) {
//        return servicioUsuario.taerRoles(usuario);
//    }
//
//    /**
//     * Creo: NLopez
//     */
//    public SiRol getRolbyId(Integer id) {
//        return siRolImpl.find(id);
//    }
//
//    /**
//     * Creo: NLopez
//     * @param modulo
//     * @param rol
//     * @return 
//     */
//    public List<SiOpcionVo> taerOpcionesByRol(Integer modulo, Integer rol) {
//        return siOpcionImpl.getSiOpcionBySiModulo(modulo, rol);
//
//    }
//
//    public List<MenuSiOpcionVo> taerListaMenu(Integer modulo, String usrID, int campo) {
//        listaMenu = new ArrayList<>();
//
//        try {
//            listaMenu.addAll(makeItems(siOpcionImpl.getListaMenu(modulo, usrID, campo)));
//        } catch (Exception e) {
//            LOGGER.error(e);
//        }
//        return listaMenu;
//    }
//
//    private List<MenuSiOpcionVo> makeItems(List<MenuSiOpcionVo> listaItems) {
//        List<MenuSiOpcionVo> itemsReturn = new ArrayList<>();
//
//        for (MenuSiOpcionVo oldVO : listaItems) {
//            MenuSiOpcionVo menuSiOpcionVo = new MenuSiOpcionVo();
//            menuSiOpcionVo.setPadre(oldVO.getPadre());
//
//            for (SiOpcionVo hijo : oldVO.getHijos()) {
//                menuSiOpcionVo.getHijos().add(hijo);
//            }
//
//            itemsReturn.add(menuSiOpcionVo);
//        }
//
//        return itemsReturn;
//    }
//
//    /*
//    public MenuItem makeItem(SiOpcionVo vo) {
//        MenuItem item = new MenuItem() ;
//        item.setId(new StringBuilder().append("DM").append(vo.getId()).toString());
//        item.setImmediate(true);
//        item.setValue(vo.getNombre());
//        item.setTitle(vo.getNombre());
//
//        if (vo.getPagina() != null && !vo.getPagina().isEmpty()) {
//            MethodBindingString methodBinding = new MethodBindingString(vo.getPagina());
//            item.setAction(methodBinding);
//        }
//
//        if (vo.getPaginaListener() != null && !vo.getPaginaListener().isEmpty()) {
//            FacesContext facesContext = FacesContext.getCurrentInstance();
//
//            if (facesContext == null) {
//                throw new IllegalStateException("Could not get FacesContext");
//            } else {
//                ELContext elContext = facesContext.getELContext();
//                ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
//                MethodExpression methodExpression = expressionFactory.createMethodExpression(elContext, vo.getPaginaListener(), void.class, new Class[]{ActionEvent.class});
//                MethodExpressionActionListener actionListener = new MethodExpressionActionListener(methodExpression);
//                item.addActionListener(actionListener);
//            }
//
//        }
//
//        return item;
//    }
//     */
//    /**
//     * Creo: NLopez
//     */
//    public void eliminarOpcionRol(SiOpcionVo opcion, Integer rol) {
//        SiRelRolOpcionVO relRolOpcion = siRolOpcion.findRolOpcion(opcion.getId(), rol);
//        SiRelRolOpcion rolOpcion = siRolOpcion.find(relRolOpcion.id);
//        siRolOpcion.remove(rolOpcion);
//
//    }
//
//    /**
//     * Creo: NLopez
//     *
//     * @return
//     */
//    public List<SiOpcionVo> taerOpcionesSinRol() {
//        return siOpcionImpl.getSipcionesSinRol(Constantes.MODULO_SGYL,
//                null,
//                Boolean.valueOf(Constantes.BOOLEAN_FALSE),
//                Boolean.valueOf(Constantes.BOOLEAN_FALSE));
//
//    }
//
//    /**
//     * Creo: NLopez
//     */
//    public void agregarOpcionRol(SiOpcionVo opcion, Integer rol) {
//        siRolOpcion.guardar(opcion, rol);
//
//    }
//
//    /**
//     * Creo: NLopez
//     *
//     * @param rolVo
//     * @param usuario
//     * @param idCamp
//     */
//    public void eliminarRolUsuario(RolVO rolVo, String usuario, int idCamp) {
//
//        try {
//            UsuarioRolVo ur = siUsuarioRol.findUsuarioRolVO(rolVo.getId(), usuario, idCamp);
//            SiUsuarioRol usuarioRol = siUsuarioRol.find(ur.getIdUsuarioRol());
//            siUsuarioRol.remove(usuarioRol);
//        } catch (Exception ex) {
//            Logger.getLogger(UsuarioListModel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
//
//    /**
//     * Creo: NLopez
//     *
//     * @param rolVo
//     * @param usuario
//     * @param idCamp
//     */
//    public void agregarRolUsuario(RolVO rolVo, String usuario, int idCamp) {
//        siUsuarioRol.guardar(rolVo, usuario, false, idCamp);
//
//    }
//
//    public List<UsuarioRolVo> traerRolesPorUsuario(String idUsuario, int modulo, int idCamp) {
//        List<UsuarioRolVo> ur = null;
//        try {
//            ur = siUsuarioRol.traerRolPorUsuarioModulo(idUsuario, modulo, idCamp);
//
//        } catch (Exception e) {
//            LOGGER.warn(this, e);
//        }
//        return ur;
//    }
//
//    public UsuarioRolVo traerRolPrincipal(String idUsuario, int idCamp) {
//        return siUsuarioRol.traerRolPrincipal(idUsuario, Constantes.MODULO_SGYL, idCamp);
//    }
//
//    public void eiminarUsuario() {
//        this.servicioUsuario.eiminarUsuario(getUsuarioVO().getId(), getUsuarioVOAlta().getId());
//    }
//
//    public void activarUsuario() {
//        this.servicioUsuario.activarUsuario(getUsuarioVO().getId(), getUsuarioVOAlta());
//    }
//
//    //TAMBIEN USADO EN LA OPCION DE INICIO DE BAJA DE EMPLEADO
//    /**
//     * MLUIS
//     *
//     * @return
//     */
//    
//
//    public List<SelectItem> listaGerenciaPorCampo() {
//        List<SelectItem> lstItem = null;
//        List<GerenciaVo> lstGerencia;
//        try {
//            lstGerencia = gerenciaImpl.getAllGerenciaByApCampo(getUsuarioVOAlta().getIdCampo(), "nombre", true, null, false);
//
//            lstItem = new ArrayList<>();
//
//            for (GerenciaVo ger : lstGerencia) {
//                SelectItem item = new SelectItem(ger.getId(), ger.getNombre());
//                lstItem.add(item);
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(this, e);
//        }
//
//        return lstItem;
//    }
//
//    public List<CadenaAprobacionVo> traerCadenaMando(String nombre, int idApCampo) {
//        return this.;
//    }
//
//    public boolean cambioContrasenia(String c, String confirmarPasswor) throws NoSuchAlgorithmException {
//        return servicioUsuario.cambioContrasenia(getUsuarioVOAlta().getId(), encriptar(c), confirmarPasswor);
//    }
//
//    public void modificaUsuarioDatosSinClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular) {
//        servicioUsuario.modificaUsuarioDatosSinClave(idUser, nombre, correo, destinatarios, rfc, telefono, ext, celular);
//    }
//
//    public boolean modificaUsuarioDatosConClave(String idUser, String nombre, String correo, String destinatarios, String rfc, String telefono, String ext, String celular, String nuevaClave) throws NoSuchAlgorithmException {
//        return servicioUsuario.modificaUsuarioDatosConClave(idUser, nombre, correo, destinatarios, rfc, telefono, ext, celular, encriptar(nuevaClave));
//    }
//
//    public String traerPuestoUsusaio(String userId, int campoId) {
//        return apCampoUsuarioRhPuestoImpl.getPuestoPorUsurioCampo(userId, campoId);
//    }
//
//    public CampoUsuarioPuestoVo traerPuesto(String userId, int campoId) {
//        return apCampoUsuarioRhPuestoImpl.traerPuestoPorUsuarioCampo(userId, campoId);
//    }
//
//    public List<SiModuloVo> traerModulo(UsuarioVO usuario, int moduloID) {
//        return siModuloImpl.getModulosUsuario(usuario.getId(), moduloID);
//    }
//
//    public List<SelectItem> traerListaOficinasItems() {
//        List<SelectItem> ls = null;
//        try {
//            List<OficinaVO> lo = sgOficinaImpl.getIdOffices();
//
//            ls = new ArrayList<>();
//
//            for (OficinaVO vo : lo) {
//                SelectItem item = new SelectItem(vo.getId(), vo.getNombre());
//                ls.add(item);
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(this, "Excepcion al trae la lista de oficinas ", e);
//        }
//
//        return ls;
//    }
//
//    public List<SelectItem> listaPuestos() {
//        List<SelectItem> l = null;
//        List<RhPuestoVo> lc;
//
//        try {
//            lc = rhPuestoImpl.findAllRhPuesto("nombre", true, false);
//            l = new ArrayList<>();
//
//            for (RhPuestoVo ger : lc) {
//                SelectItem item = new SelectItem(ger.getId(), ger.getNombre());
//                l.add(item);
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(this, e);
//        }
//
//        return l;
//    }
//
//    public DataModel listaBloquePorUsuario() {
//
//        try {
//            //lc = apCampoImpl.getAllField();
//            if (getUsuarioVO() != null) {
//                setLista(
//                        new ListDataModel(
//                                apCampoUsuarioRhPuestoImpl.getAllPorUsurio(getUsuarioVO().getId())
//                        )
//                );
//            }
//        } catch (RuntimeException e) {
//            LOGGER.fatal(this, "Ocurrio un error : : : : : " + e.getMessage(), e);
//        }
//        return getLista();
//    }
//
//    public List<SelectItem> listaCampo() {
//        List<SelectItem> l = null;
//        List<ApCampoVo> lc;
//        try {
//            lc = apCampoImpl.traerApCampo();
//            l = new ArrayList<>();
//            for (ApCampoVo ca : lc) {
//                SelectItem item = new SelectItem(ca.getId(), ca.getNombre());
//                l.add(item);
//            }
//        } catch (Exception e) {
//            LOGGER.error(this, e);
//        }
//
//        return l;
//    }
//
//    public List<SelectItem> listaCampoUsuario() {
//        List<SelectItem> l = null;
//        List<CampoUsuarioPuestoVo> lc;
//        try {
//            lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(getUsuarioVO().getId());
//            l = new ArrayList<>();
//            for (CampoUsuarioPuestoVo ca : lc) {
//                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
//                l.add(item);
//            }
//        } catch (Exception e) {
//            LOGGER.error(this, e);
//        }
//
//        return l;
//    }
//
//    //DAtos profesionales
//    public List<SelectItem> listaOficina() {
//        List<OficinaVO> lo;
//        List<SelectItem> li = null;
//        try {
//            lo = sgOficinaImpl.getIdOffices();
//            li = new ArrayList<>();
//            for (OficinaVO i : lo) {
//                SelectItem item = new SelectItem(i.getId(), i.getNombre());
//                li.add(item);
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(this, "Excepcion en traer oficina " + e.getMessage(), e);
//        }
//
//        return li;
//    }
//    //DAtos profesionales
//
//    public List<SelectItem> listaEmpresa() {
//        List<SgEmpresa> lo;
//        List<SelectItem> li = null;
//        try {
//            lo = sgEmpresaImpl.getAllCompanyByNomina();
//            li = new ArrayList<>();
//            for (SgEmpresa i : lo) {
//                SelectItem item = new SelectItem(i.getId(), i.getNombre());
//                li.add(item);
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(this, "Excepcion en traer empresa " + e.getMessage(), e);
//        }
//
//        return li;
//    }
//
//    //usuarios sin correos
//    public DataModel traerUsuariosSinCorreo() {
//        setListaUsuario(new ListDataModel(servicioUsuario.traerListaUsuariosSinCorreos()));
//        return getListaUsuario();
//    }
//
////CAmpo
//    /**
//     *
//     * @return
//     */
//    public DataModel traerCampoUsuario() {
//        setLista(new ListDataModel(apCampoUsuarioRhPuestoImpl.traerUsurioPorCampo(getIdCampo(), usuarioVO)));
//        return getLista();
//    }
//
//    public void cambiarUsuarioPuesto(String idUsuario, String idUserModifico, int campo) {
//        servicioUsuario.cambiarCampoUsuario(idUsuario, idUserModifico, campo);
//        //
//    }
//    //Fin de campo
//
//    public RhPuestoVo buscarPuestoPorNombre() {
//        return rhPuestoImpl.findByName(getRhPuestoVo().getNombre(), false);
//    }
//
//    public RhPuestoVo buscarPuestoPorId() {
//        return rhPuestoImpl.findById(getRhPuestoVo().getId(), false);
//    }
//
//    public void guardarUsuarioCampo(int campo, int puesto) {
//        int gerencia = servicioUsuario.find(getU()).getGerencia().getId();
//        apCampoUsuarioRhPuestoImpl.save(getUsuarioVO().getId(), campo, getU(), puesto, gerencia);
//    }
//
//    public void modificarUsuarioCampo() {
//        apCampoUsuarioRhPuestoImpl.edit(getUsuarioVO().getId(), getIdCampo(), getCampoUsuarioPuestoVo().getIdUsuario(), getRhPuestoVo().getId(), getCampoUsuarioPuestoVo().getIdCampoUsuarioPuesto());
//    }
//
//    public boolean verificaUsuarioCampoGuardar() {
//        List<CampoUsuarioPuestoVo> lcup
//                = apCampoUsuarioRhPuestoImpl.getCampoPorUsurio(getU(), getCampoUsuarioPuestoVo().getIdCampo());
//
//        boolean retVal = true;
//
//        for (CampoUsuarioPuestoVo cupv : lcup) {
//            if (getCampoUsuarioPuestoVo().getIdCampo() == cupv.getIdCampo()) {
//                retVal = false;
//                break;
//            }
//        }
//        return retVal;
//    }
//
//    public boolean verificaUsuarioCampoModificar() {
//        RhPuesto p = rhPuestoImpl.find(getRhPuestoVo().getId());
//
//        return p != null;
//
////	if (p != null) {
////	    return true;
////	} else {
////	    return false;
////	}
//    }
//
//    public boolean buscarCampoGerencia() {
//
//        return !apCampoGerenciaImpl.buscarCampoGerencia(
//                getCampoUsuarioPuestoVo().getIdUsuario(),
//                getCampoUsuarioPuestoVo().getIdCampo()
//        ).isEmpty();
//    }
//
//    public void traerUsuarioJson() {
//        usuarioJson = servicioUsuario.traerUsuarioActivoJson();
//    }
//
//    public void traerPuestoJson() {
//        puestoJson = rhPuestoImpl.traerPuestoActivoJson();
//    }
//
//    public int buscarGerenciaResponsable() {
//        return 0;
//    }
//
//    public void eliminarRelacion() {
//        apCampoUsuarioRhPuestoImpl.delete(getUsuarioVO().getId(), getCampoUsuarioPuestoVo().getIdCampoUsuarioPuesto());
//    }
//
//    public boolean verificaRelacionCampoGerenciaResponsable(int campo, int gerencia) {
//        return apCampoGerenciaImpl.verificaRelacionCampoGerenciaResponsable(getUsuarioVOAlta().getId(), campo, gerencia);
//    }
//
//    //Validaciones
//    public boolean validaMail(String correo) {
//        String[] mails = correo.split(",");
//        boolean v = true;
//        for (String string : mails) {
//            v = mail(string.trim());
//            if (!v) {
//                break;
//            }
//        }
//
//        return v;
//    }
//    //aprobadores de orden de compra
//
////    public DataModel traerUsuarioAprobanOrdenCompra(int campo) {
////        return new ListDataModel(servicioUsuario.getApruebanOrden(Constantes.BOOLEAN_TRUE, campo));
////    }
//    public DataModel traerUsuarioAprobanOrdenCompra(int campo) {
//        return new ListDataModel(servicioUsuario.getApruebanOrden(campo));
//    }
//
//    public void eliminarUsuarioApruebaOC(String sesion, String idUser, int campo) {
//        servicioUsuario.quitarUsuarioApruebaOrdenCompra(sesion, idUser, campo, Constantes.OCFLUJO_ACTION_APROBAR);
//    }
//
//    public void aprobarOrdenCompra(String u, int campo) {
//        servicioUsuario.aprobarOrdenCompra(getUsuarioVO().getId(), u, campo, Constantes.OCFLUJO_ACTION_APROBAR);
//    }
//    //metodo para validar correo electronio
//
//    public boolean mail(String correo) {
//
//        boolean retVal = false;
//
//        try {
//            InternetAddress ia = new InternetAddress(correo);
//            ia.validate();
//
//            retVal = true;
//        } catch (AddressException ex) {
//            LOGGER.warn(this, "*** email : " + correo, ex);
//        }
//
//        return retVal;
//    }
//
//    /**
//     * Valida si una cadena de texto contiene algún signo de puntuación
//     *
//     * @param text
//     * @return true si 'text' contiene algún signo de puntuación:
//     * !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
//     */
//    public boolean validateTextHastNotPunctuation(String text) {
//        Pattern p = Pattern.compile("\\p{Punct}");
//        Matcher m = p.matcher(text);
//
//        return m.find();
//    }
//
//    public String encriptar(String text) throws NoSuchAlgorithmException {
//        //LOGGER.info(this, "Text: ");
//        return servicioUsuario.encriptar(text);
//    }
//
//    public void cambioCampo(int campoNuevo) {
//        servicioUsuario.cambiarCampoUsuario(getUsuarioVO().getId(), getUsuarioVO().getId(), campoNuevo);
//
//    }
//
//    public boolean reinicioClave() {
//        return servicioUsuario.reinicioClave(getUsuarioVO().getId(), getUsuarioVOAlta().getId());
//    }
//
//    //Genera solicitud de estancia
//    public void solicitarEstancia() {
//        //boolean v = false;
//        sgSolicitudEstanciaImpl.guardarSolicitudEstancia(
//                getUsuarioVO().getId(),
//                getUsuarioVOAlta().getId(),
//                getUsuarioVOAlta().getIdGerencia(),
//                getIdSgOficina(),
//                getUsuarioVOAlta().getFechaIngreso(),
//                getFechaSalida()
//        );
//
////	return v;
//    }
//
//    public Date sumarDias() {
//        setFechaSalida(siManejoFechaLocal.fechaSumarDias(getUsuarioVOAlta().getFechaIngreso(), 60));
//        return getFechaSalida();
//    }
//
//    public boolean validarFechaInicioMenorAHoy(Date valor) {
//        Calendar fIni = Calendar.getInstance();
//        Calendar fHoy = Calendar.getInstance();
//        fIni.setTime(valor);
//        fHoy.setTime(new Date());
//        return siManejoFechaLocal.compare(fIni, fHoy, false) == -1;
//    }
//
//    /**
//     * regresa true si la fecha de inicio de estancia es menor a la fecha de fin
//     * de estancia.
//     *
//     * @return
//     */
//    public boolean validarFechaInicioVsFechaFin(Date value) {
//        return siManejoFechaLocal.compare(value, usuarioVOAlta.getFechaIngreso()) == -1;
//    }
//
//    //************** Iniciar baja de empleado
//    public void traerListaRhTipoGerencia() {
//
//        this.listaGerencias = new ListDataModel(gerenciaImpl.getAllGerenciaByApCampo(idCampo, "nombre", true, null, false));
//        /**
//         * 33 - Servicios Generales 61 - TI 54 - Finanzas - Obango 47 - HSE
//         */
//        List<GerenciaVo> list = getDataModelAsList(listaGerencias);
//        for (GerenciaVo vo : list) {
//            if (vo.getId() == SERVICIOS_GENERALES || vo.getId() == SERVICIOS_INFORMATICOS
//                    || vo.getId() == FINANZAS || vo.getId() == HSE) {
//                //pasar a la lista de sleccion
//                getListaFilasSeleccionadas().add(vo);
//            }
//        }
//
//    }
//
//    public void agregarGerenciasDefaultParaInicioBaja() {
//        List<GerenciaVo> lista = gerenciaImpl.getAllGerenciaByApCampo(idCampo, "nombre", true, null, false);
//        listaGeneral = new ArrayList<GerenciaVo>();
//
//        for (GerenciaVo vo : lista) {
//            if (vo.getId() == SERVICIOS_GENERALES || vo.getId() == SERVICIOS_INFORMATICOS
//                    || vo.getId() == FINANZAS || vo.getId() == HSE) {
//                //pasar a la lista de sleccion
//                listaGeneral.add(vo);
//            }
//        }
//
//        listaGerenciasSeleccionadas = new ListDataModel(listaGeneral);
//    }
//
//    public void llenarComboGerenciasPorCampo() {
//        //TODO : revisar si es necesario el doble try / catch
//        try {
//            List<GerenciaVo> lista = gerenciaImpl.getAllGerenciaByApCampo(idCampo, "nombre", true, null, false);
//            LOGGER.info(this, "getApCamposItems");
//            List<SelectItem> l = new ArrayList<SelectItem>();
//            try {
//                for (GerenciaVo vo : lista) {
//                    SelectItem item = new SelectItem(vo.getId(), vo.getNombre() + " -  " + vo.getNombreResponsable());
//                    l.add(item);
//                }
//                this.listaGerenciasItems = l;
//            } catch (Exception e) {
//                LOGGER.error(this, "Excepcion al traer la lista de gerencias " + e.getMessage(), e);
//            }
//
//        } catch (Exception e) {
//            LOGGER.error(e);
//        }
//    }
//
//    //trae todos los campos
//    public void llenarListaApCamposItems() {
//        LOGGER.info(this, "getApCamposItems");
//        List<SelectItem> l = new ArrayList<>();
//        try {
//            for (CampoVo apCampoVo : this.apCampoImpl.getAllField()) {
//                SelectItem item = new SelectItem(apCampoVo.getId(), apCampoVo.getNombre());
//                l.add(item);
//            }
//            LOGGER.info(this, " size" + l.size());
//            this.listaCamposItems = l;
//        } catch (Exception e) {
//            LOGGER.error(this, "Excepcion al traer la lista de campos " + e.getMessage(), e);
//        }
//    }
//
//    public <T> List<T> getDataModelAsList(DataModel dm) {
//        return (List<T>) dm.getWrappedData();
//    }
//
//    public boolean iniciarBaja() {
////	List<Integer> listaEnviar = new ArrayList<Integer>();
//        boolean retVal = false;
//
//        try {
//            List<GerenciaVo> list = getDataModelAsList(getListaGerenciasSeleccionadas());
//            retVal = rhUsuarioGerenciaImpl.iniciarBajaEmpleado(this.usuarioVOAlta.getId(),
//                    this.respuesta, list, usuarioVO.getId());
//        } catch (Exception e) {
//            LOGGER.error(this, "Excepcion al inicar baja " + e.getMessage(), e);
//        }
//
//        return retVal;
//    }
//
//    public boolean verificarProcesoBaja() {
//        return rhUsuarioGerenciaImpl.verficiarProcesoBaja(usuarioVOAlta.getId());
//    }
//
//    //TODO : revisar si es posible obtener el dato desde la base de datos para no iterar en listas
//    public boolean agregarGerenciaAListaSeleccionadas() {
//        boolean esta = false;
//        List<GerenciaVo> listaGerencias = gerenciaImpl.getAllGerenciaByApCampo(idCampo, "nombre", true, null, false);
//        List<GerenciaVo> listaSeleccionActual = getDataModelAsList(listaGerenciasSeleccionadas);
//        //buscar la gerencia en la lista
//        for (GerenciaVo vo : listaGerencias) {
//            if (vo.getId() == idGerencia) {
//                //agregar
//                listaSeleccionActual.add(vo);
//                LOGGER.info(this, "Se agrego la gerecia " + vo.getNombre() + " de responsable " + vo.getNombreResponsable());
//                esta = true;
//            }
//        }
//        return esta;
//    }
//
//    public boolean quitarGerenciaAListaSeleccionadas(GerenciaVo gerenciaVoQuitar) {
//        boolean esta = false;
//
//        try {
//
//            List<GerenciaVo> listaSeleccionActual = getDataModelAsList(listaGerenciasSeleccionadas);
//            listaSeleccionActual.remove(gerenciaVoQuitar);
//            LOGGER.info(this, "Se quito correctamente la gerencia de a lista");
//            esta = true;
//        } catch (Exception e) {
//            LOGGER.error(this, "Exc" + e.getMessage(), e);
//        }
//
//        return esta;
//    }
//
//    public boolean buscarGerenciaRepetidaEnGerenciasSeleccionadas() {
//        boolean retVal = false;
//
//        List<GerenciaVo> listaActual = getDataModelAsList(listaGerenciasSeleccionadas);
//        //saber si no existe
//        for (GerenciaVo vo : listaActual) {
//            if (vo.getId() == idGerencia) {
//                retVal = true;
//            }
//        }
//        return retVal;
//    }
//
//    /*
//     * Metodo que agregar una gerencia del campo nejo al campo seleccionado por
//     * el usuario
//     */
//    public boolean agregarGerenciaACampo() {
//        boolean retVal = false;
//
//        LOGGER.info(this, "idCampo a agregar " + idPuesto);
//        LOGGER.info(this, "idCampo gerencia " + idGerencia);
//        try {
//            //buscar gerencia por el id seleccionado
//            ApCampoGerenciaVo vo = apCampoGerenciaImpl.findByCampoGerencia(1, idGerencia, false);
//            if (vo != null) {
//                //apCampoGerenciaImpl.guardarCampoGerenciaResponsable(usuarioVO.getId(), vo.getIdResponsable(), idPuesto, idGerencia);
//                rhCampoGerenciaImpl.agregarRelacionCampoGerencia(idPuesto, idGerencia, vo.getIdResponsable(), usuarioVO.getId());
//            }
//            retVal = true;
//        } catch (Exception e) {
//            LOGGER.error(this, "Excepcion al agregar nueva gerencia al campo por RH" + e.getMessage(), e);
//        }
//
//        return retVal;
//    }
//
//    public boolean vefiricarExistenciaGerenciaEnApCampo() {
//        //idPuesto : representa el idApCampo selccionado al que se le agregara la gerencia..
//
//        return apCampoGerenciaImpl.findByCampoGerencia(idPuesto, idGerencia, false) != null;
//    }
//
//    public boolean agregarGerenciaRealizaLiberacionEmpleado() {
//        boolean retVal = false;
//
//        try {
//            if (rhTipoGerenciaImpl.agregarGerencia(idGerencia, usuarioVO.getId())) {
//                //lista de gerencias
//                traerListaRhTipoGerencia();
//            }
//            retVal = true;
//        } catch (Exception e) {
//            LOGGER.error(this, "Excepcion al agregar nueva gerencia a la lista de liberacion de empleado " + e.getMessage(), e);
//        }
//
//        return retVal;
//    }
//
//    public boolean buscarGerenciaLiberaExistente() {
//        return rhTipoGerenciaImpl.buscarGerenciaExistente(getIdGerencia());
//    }
//
//    public List<GerenciaVo> getAllGerenciaByResponsable(String idUsuarioResponsable) {
//        return gerenciaImpl.getAllGerenciaByResponsable(getUsuarioVO().getId(), "id", true, null, false);
//    }
//
//    public List<UsuarioGerenciaVo> allUsuarioForFree() {
//        return rhUsuarioGerenciaImpl.findAllForFreeByUsuario(getUsuarioVO().getId());
//    }
//
//    public List<RhTipoGerenciaVo> getAllTipoGerenciaByGerencia(int idGerencia) {
//        return rhTipoGerenciaImpl.findAllRhTipoGerenciaByRhCampoGerencia(idGerencia, "nombre", true, false);
//    }
//
//    public void setFreeEmployee(int idUsuarioGerencia) throws EmailNotFoundException {
//        rhUsuarioGerenciaImpl.setFreeUsuarioAndAdvicing(idUsuarioGerencia, getUsuarioVO().getId());
//    }
//
//    //NOTICIA
//    public void crearComentario() {
//        LOGGER.info(this, "usuarioGerenciaVo.getIdNoticia()" + usuarioGerenciaVo.getIdNoticia());
//        coNoticiaImpl.nuevoComentario(
//                usuarioGerenciaVo.getIdNoticia(),
//                getUsuarioVO().getId(),
//                getNombre(),
//                true,
//                false,
//                getUsuarioVO().getIdCampo(),
//                Constantes.MODULO_ADMIN_SIA
//        );
//
////	return true;
//    }
//
//    public boolean isAnalistaCosto(String sesion, int idCamp) {
//        UsuarioRolVo usrRol = null;
//        try {
//            usrRol = siUsuarioRol.findUsuarioRolVO(Constantes.ROL_VISTO_BUENO_COSTO, sesion, idCamp);
//        } catch (Exception ex) {
//            LOGGER.error(ex);
//        }
//        return usrRol != null && usrRol.getIdUsuarioRol() > 0;
//    }
//
//    /**
//     * @return the idCampo
//     */
//    public int getIdCampo() {
//        return idCampo;
//    }
//
//    /**
//     * @param idCampo the idCampo to set
//     */
//    public void setIdCampo(int idCampo) {
//        this.idCampo = idCampo;
//    }
//
//    /**
//     * @return the apruebaOCPop
//     */
//    public boolean isApruebaOCPop() {
//        return apruebaOCPop;
//    }
//
//    /**
//     * @param apruebaOCPop the apruebaOCPop to set
//     */
//    public void setApruebaOCPop(boolean apruebaOCPop) {
//        this.apruebaOCPop = apruebaOCPop;
//    }
//
//    /**
//     * @return the usuarioVoAlta
//     */
//    public UsuarioVO getUsuarioVOAlta() {
//        return usuarioVOAlta;
//    }
//
//    /**
//     * @param usuarioVoAlta the usuarioVoAlta to set
//     */
//    public void setUsuarioVOAlta(UsuarioVO usuarioVOAlta) {
//        this.usuarioVOAlta = usuarioVOAlta;
//    }
//
//    /**
//     * @return the lista
//     */
//    public DataModel getLista() {
//        return lista;
//    }
//
//    /**
//     * @param lista the lista to set
//     */
//    public void setLista(DataModel lista) {
//        this.lista = lista;
//    }
//
//    /**
//     * @return the usuarioVO
//     */
//    public UsuarioVO getUsuarioVO() {
//        return usuarioVO;
//    }
//
//    /**
//     * @param usuarioVO the usuarioVO to set
//     */
//    public void setUsuarioVO(UsuarioVO usuarioVO) {
//        this.usuarioVO = usuarioVO;
//    }
//
//    /**
//     * @return the apCampoGerenciaVo
//     */
//    public ApCampoGerenciaVo getApCampoGerenciaVo() {
//        return apCampoGerenciaVo;
//    }
//
//    /**
//     * @param apCampoGerenciaVo the apCampoGerenciaVo to set
//     */
//    public void setApCampoGerenciaVo(ApCampoGerenciaVo apCampoGerenciaVo) {
//        this.apCampoGerenciaVo = apCampoGerenciaVo;
//    }
//
//    /**
//     * @return the idGerencia
//     */
//    public int getIdGerencia() {
//        return idGerencia;
//    }
//
//    /**
//     * @param idGerencia the idGerencia to set
//     */
//    public void setIdGerencia(int idGerencia) {
//        this.idGerencia = idGerencia;
//    }
//
//    /**
//     * @return the campoUsuarioPuestoVo
//     */
//    public CampoUsuarioPuestoVo getCampoUsuarioPuestoVo() {
//        return campoUsuarioPuestoVo;
//    }
//
//    /**
//     * @param campoUsuarioPuestoVo the campoUsuarioPuestoVo to set
//     */
//    public void setCampoUsuarioPuestoVo(CampoUsuarioPuestoVo campoUsuarioPuestoVo) {
//        this.campoUsuarioPuestoVo = campoUsuarioPuestoVo;
//    }
//
//    /**
//     * @return the rhPuestoVo
//     */
//    public RhPuestoVo getRhPuestoVo() {
//        return rhPuestoVo;
//    }
//
//    /**
//     * @param rhPuestoVo the rhPuestoVo to set
//     */
//    public void setRhPuestoVo(RhPuestoVo rhPuestoVo) {
//        this.rhPuestoVo = rhPuestoVo;
//    }
//
//    /**
//     * @return the primerApellido
//     */
//    public String getPrimerApellido() {
//        return primerApellido;
//    }
//
//    /**
//     * @param primerApellido the primerApellido to set
//     */
//    public void setPrimerApellido(String primerApellido) {
//        this.primerApellido = primerApellido;
//    }
//
//    /**
//     * @return the segundoApellido
//     */
//    public String getSegundoApellido() {
//        return segundoApellido;
//    }
//
//    /**
//     * @param segundoApellido the segundoApellido to set
//     */
//    public void setSegundoApellido(String segundoApellido) {
//        this.segundoApellido = segundoApellido;
//    }
//
//    /**
//     * @return the nombre
//     */
//    public String getNombre() {
//        return nombre;
//    }
//
//    /**
//     * @param nombre the nombre to set
//     */
//    public void setNombre(String nombre) {
//        this.nombre = nombre;
//    }
//
//    /**
//     * @return the filaSeleccionada
//     */
//    public Map<Integer, Boolean> getFilaSeleccionada() {
//        return filaSeleccionada;
//    }
//
//    /**
//     * @param filaSeleccionada the filaSeleccionada to set
//     */
//    public void setFilaSeleccionada(Map<Integer, Boolean> filaSeleccionada) {
//        this.filaSeleccionada = filaSeleccionada;
//    }
//
//    /**
//     * @return the listaFilasSeleccionadas
//     */
//    /**
//     * @return the solicitaEstancia
//     */
//    public boolean isSolicitaEstancia() {
//        return solicitaEstancia;
//    }
//
//    /**
//     * @param solicitaEstancia the solicitaEstancia to set
//     */
//    public void setSolicitaEstancia(boolean solicitaEstancia) {
//        this.solicitaEstancia = solicitaEstancia;
//    }
//
//    /**
//     * @return the fechaSalida
//     */
//    public Date getFechaSalida() {
//        return fechaSalida;
//    }
//
//    /**
//     * @param fechaSalida the fechaSalida to set
//     */
//    public void setFechaSalida(Date fechaSalida) {
//        this.fechaSalida = fechaSalida;
//    }
//
//    /*
//     * @return the respuesta
//     */
//    public String getRespuesta() {
//        return respuesta;
//    }
//
//    /**
//     * @param respuesta the respuesta to set
//     */
//    public void setRespuesta(String respuesta) {
//        this.respuesta = respuesta;
//    }
//
//    public List<SelectItem> getListaPuestos() {
//        return listaPuestos;
//    }
//
//    public void setListaPuestos(List<SelectItem> listaPuestos) {
//        this.listaPuestos = listaPuestos;
//    }
//
//    /**
//     * @return the idSgOficina
//     */
//    public int getIdSgOficina() {
//        return idSgOficina;
//    }
//
//    /**
//     * @param idSgOficina the idSgOficina to set
//     */
//    public void setIdSgOficina(int idSgOficina) {
//        this.idSgOficina = idSgOficina;
//    }
//
//    /**
//     * @return the listaUsuario
//     */
//    public DataModel getListaUsuario() {
//        return listaUsuario;
//    }
//
//    /**
//     * @param listaUsuario the listaUsuario to set
//     */
//    public void setListaUsuario(DataModel listaUsuario) {
//        this.listaUsuario = listaUsuario;
//    }
//
//    /**
//     * @return the listaGerencias
//     */
//    public DataModel getListaGerencias() {
//        return listaGerencias;
//    }
//
//    /**
//     * @param listaGerencias the listaGerencias to set
//     */
//    public void setListaGerencias(DataModel listaGerencias) {
//        this.listaGerencias = listaGerencias;
//    }
//
//    /*
//     * @return the usuarioGerenciaVo
//     */
//    public UsuarioGerenciaVo getUsuarioGerenciaVo() {
//        return usuarioGerenciaVo;
//    }
//
//    /**
//     * @param usuarioGerenciaVo the usuarioGerenciaVo to set
//     */
//    public void setUsuarioGerenciaVo(UsuarioGerenciaVo usuarioGerenciaVo) {
//        this.usuarioGerenciaVo = usuarioGerenciaVo;
//    }
//
//    /**
//     * @return the listaMaterial
//     */
//    public DataModel getListaMaterial() {
//        return listaMaterial;
//    }
//
//    /**
//     * @param listaMaterial the listaMaterial to set
//     */
//    public void setListaMaterial(DataModel listaMaterial) {
//        this.listaMaterial = listaMaterial;
//    }
//
//    /**
//     * @return the idPuesto
//     */
//    public int getIdPuesto() {
//        return idPuesto;
//    }
//
//    /**
//     * @param idPuesto the idPuesto to set
//     */
//    public void setIdPuesto(int idPuesto) {
//        this.idPuesto = idPuesto;
//    }
//
//    /**
//     * @return the flag
//     */
//    public boolean isFlag() {
//        return flag;
//    }
//
//    /**
//     * @param flag the flag to set
//     */
//    public void setFlag(boolean flag) {
//        this.flag = flag;
//    }
//
//    /**
//     * @return the listaGerenciasSeleccionadas
//     */
//    public DataModel getListaGerenciasSeleccionadas() {
//        return listaGerenciasSeleccionadas;
//    }
//
//    /**
//     * @param listaGerenciasSeleccionadas the listaGerenciasSeleccionadas to set
//     */
//    public void setListaGerenciasSeleccionadas(DataModel listaGerenciasSeleccionadas) {
//        this.listaGerenciasSeleccionadas = listaGerenciasSeleccionadas;
//    }
//
//    /**
//     * @return the listaGerenciasItems
//     */
//    public List<SelectItem> getListaGerenciasItems() {
//        return listaGerenciasItems;
//    }
//
//    /**
//     * @param listaGerenciasItems the listaGerenciasItems to set
//     */
//    public void setListaGerenciasItems(List<SelectItem> listaGerenciasItems) {
//        this.listaGerenciasItems = listaGerenciasItems;
//    }
//
//    /**
//     * @return the listaCamposItems
//     */
//    public List<SelectItem> getListaCamposItems() {
//        return listaCamposItems;
//    }
//
//    /**
//     * @param listaCamposItems the listaCamposItems to set
//     */
//    public void setListaCamposItems(List<SelectItem> listaCamposItems) {
//        this.listaCamposItems = listaCamposItems;
//    }
//
//    /**
//     * @return the listaFilasSeleccionadas
//     */
//    public List getListaFilasSeleccionadas() {
//        return listaFilasSeleccionadas;
//    }
//
//    /**
//     * @param listaFilasSeleccionadas the listaFilasSeleccionadas to set
//     */
//    public void setListaFilasSeleccionadas(List listaFilasSeleccionadas) {
//        this.listaFilasSeleccionadas = listaFilasSeleccionadas;
//    }
//
//    /**
//     * @return the listaGeneral
//     */
//    public List<GerenciaVo> getListaGeneral() {
//        return listaGeneral;
//    }
//
//    /**
//     * @param listaGeneral the listaGeneral to set
//     */
//    public void setListaGeneral(List<GerenciaVo> listaGeneral) {
//        this.listaGeneral = listaGeneral;
//    }
//
//    /**
//     * @return the listaOpciones
//     */
//    public DataModel getListaOpciones() {
//        return listaOpciones;
//    }
//
//    /**
//     * @param listaOpciones the listaOpciones to set
//     */
//    public void setListaOpciones(DataModel listaOpciones) {
//        this.listaOpciones = listaOpciones;
//    }
//
//    public void setSoporteListas(SoporteListas soporteListas) {
//        this.soporteListas = soporteListas;
//    }
//
//    public void setSoporteProveedor(SoporteProveedor soporteProveedor) {
//        this.soporteProveedor = soporteProveedor;
//    }
//
//    public int getAgregarNuevoPass() {
//        return agregarNuevoPass;
//    }
//
//    public void setAgregarNuevoPass(int agregarNuevoPass) {
//        this.agregarNuevoPass = agregarNuevoPass;
//    }
//
//    public String getCambiarPass() {
//        return cambiarPass;
//    }
//
//    public void setCambiarPass(String cambiarPass) {
//        this.cambiarPass = cambiarPass;
//    }
//
//    public int getPreguntaEntero() {
//        return preguntaEntero;
//    }
//
//    public void setPreguntaEntero(int preguntaEntero) {
//        this.preguntaEntero = preguntaEntero;
//    }
//
//    /**
//     * @return the u
//     */
//    public String getU() {
//        return u;
//    }
//
//    /**
//     * @param u the u to set
//     */
//    public void setU(String u) {
//        this.u = u;
//    }
//
//    /**
//     * @return the seleccionNuevoIngreso
//     */
//    public int getSeleccionNuevoIngreso() {
//        return seleccionNuevoIngreso;
//    }
//
//    /**
//     * @param seleccionNuevoIngreso the seleccionNuevoIngreso to set
//     */
//    public void setSeleccionNuevoIngreso(int seleccionNuevoIngreso) {
//        this.seleccionNuevoIngreso = seleccionNuevoIngreso;
//    }
//
//    /**
//     * @return the parametroTipoUsuario
//     */
//    public int getParametroTipoUsuario() {
//        return parametroTipoUsuario;
//    }
//
//    /**
//     * @param parametroTipoUsuario the parametroTipoUsuario to set
//     */
//    public void setParametroTipoUsuario(int parametroTipoUsuario) {
//        this.parametroTipoUsuario = parametroTipoUsuario;
//    }
//
//    /**
//     * @return the c
//     */
//    public String getC() {
//        return c;
//    }
//
//    /**
//     * @param c the c to set
//     */
//    public void setC(String c) {
//        this.c = c;
//    }
//
//    /**
//     * @return the confirmarPassword
//     */
//    public String getConfirmarPassword() {
//        return confirmarPassword;
//    }
//
//    /**
//     * @param confirmarPassword the confirmarPassword to set
//     */
//    public void setConfirmarPassword(String confirmarPassword) {
//        this.confirmarPassword = confirmarPassword;
//    }
//
//    /**
//     * @return the nuevaClave
//     */
//    public String getNuevaClave() {
//        return nuevaClave;
//    }
//
//    /**
//     * @param nuevaClave the nuevaClave to set
//     */
//    public void setNuevaClave(String nuevaClave) {
//        this.nuevaClave = nuevaClave;
//    }
//
//    /**
//     * @return the claveActual
//     */
//    public String getClaveActual() {
//        return claveActual;
//    }
//
//    /**
//     * @param claveActual the claveActual to set
//     */
//    public void setClaveActual(String claveActual) {
//        this.claveActual = claveActual;
//    }
//
//    /**
//     * @return the accion
//     */
//    public int getAccion() {
//        return accion;
//    }
//
//    /**
//     * @param accion the accion to set
//     */
//    public void setAccion(int accion) {
//        this.accion = accion;
//    }
//
//    /**
//     * @return the usuarioJson
//     */
//    public String getUsuarioJson() {
//        return usuarioJson;
//    }
//
//    /**
//     * @param usuarioJson the usuarioJson to set
//     */
//    public void setUsuarioJson(String usuarioJson) {
//        this.usuarioJson = usuarioJson;
//    }
//
//    /**
//     * @return the puestoJson
//     */
//    public String getPuestoJson() {
//        return puestoJson;
//    }
//
//    /**
//     * @param puestoJson the puestoJson to set
//     */
//    public void setPuestoJson(String puestoJson) {
//        this.puestoJson = puestoJson;
//    }
//
//    /**
//     * @return the listaUsuarioFree
//     */
//    public DataModel getListaUsuarioFree() {
//        return listaUsuarioFree;
//    }
//
//    /**
//     * @param listaUsuarioFree the listaUsuarioFree to set
//     */
//    public void setListaUsuarioFree(DataModel listaUsuarioFree) {
//        this.listaUsuarioFree = listaUsuarioFree;
//    }
//
//    /**
//     * @return the mapaRoles
//     */
//    public Map<String, Boolean> getMapaRoles() {
//        return mapaRoles;
//    }
//
//    /**
//     * @param mapaRoles the mapaRoles to set
//     */
//    public void setMapaRoles(Map<String, Boolean> mapaRoles) {
//        this.mapaRoles = mapaRoles;
//    }
//
//    public Usuario findUsuarioForId(String usuarioId) {
//        return servicioUsuario.getUsuarioForId(usuarioId);
//    }
}
